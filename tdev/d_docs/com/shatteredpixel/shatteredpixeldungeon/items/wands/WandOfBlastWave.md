# WandOfBlastWave 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/wands/WandOfBlastWave.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.wands |
| **文件类型** | class |
| **继承关系** | extends DamageWand |
| **代码行数** | 294 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
冲击波法杖是一种击退型伤害法杖，具有以下特点：
- 在目标位置造成爆炸伤害
- 将敌人击退
- 击退伤害可能致死
- 可将敌人击退到深坑中

### 系统定位
位于 DamageWand 层次，是主要的控制型伤害法杖，适合击退敌人到危险位置。

## 3. 结构总览

### 主要成员概览
- **伤害方法**：`min(int lvl)`, `max(int lvl)`
- **击退方法**：`throwChar()` - 静态方法，击退角色
- **内部类**：`BlastWave` - 冲击波视觉效果，`Knockback` - 击退伤害标记，`BWaveOnHitTracker` - 近战效果追踪

## 4. 继承与协作关系

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `min(int lvl)` | 返回 1 + lvl |
| `max(int lvl)` | 返回 3 + 3 * lvl |
| `onZap(Ballistica)` | 施放冲击波 |
| `onHit(MagesStaff, Char, Char, int)` | 近战命中触发爆炸 |
| `fx(Ballistica, Callback)` | 力场飞弹视觉 |
| `upgradeStat2(int)` | 击退距离 |
| `staffFx(MagesStaff.StaffParticle)` | 手杖粒子效果 |

## 5. 字段/常量详解

无实例字段。

## 6. 构造与初始化机制

### 初始化块
```java
{
    image = ItemSpriteSheet.WAND_BLAST_WAVE;
    collisionProperties = Ballistica.PROJECTILE;
}
```

## 7. 方法详解

### min(int lvl)
**可见性**：public

**是否覆写**：是

**方法职责**：返回最小伤害

**返回值**：int，`1 + lvl`

---

### max(int lvl)
**可见性**：public

**是否覆写**：是

**方法职责**：返回最大伤害

**返回值**：int，`3 + 3 * lvl`

---

### onZap(Ballistica bolt)
**可见性**：public

**是否覆写**：是

**方法职责**：施放冲击波

**核心实现逻辑**：
```java
Sample.INSTANCE.play(Assets.Sounds.BLAST);
BlastWave.blast(bolt.collisionPos);

// 按压范围内所有格子
for (int i : PathFinder.NEIGHBOURS9) {
    if (!(Dungeon.level.traps.get(bolt.collisionPos+i) instanceof TenguDartTrap)) {
        Dungeon.level.pressCell(bolt.collisionPos + i);
    }
}

// 击退周围敌人
for (int i : PathFinder.NEIGHBOURS8) {
    Char ch = Actor.findChar(bolt.collisionPos + i);
    if (ch != null) {
        wandProc(ch, chargesPerCast());
        if (ch.alignment != Char.Alignment.ALLY) ch.damage(damageRoll(), this);
        
        // 击退
        Ballistica trajectory = new Ballistica(ch.pos, ch.pos + i, Ballistica.MAGIC_BOLT);
        int strength = Math.round(1.5f + buffedLvl() / 2f);
        throwChar(ch, trajectory, strength, false, true, this);
    }
}

// 击退中心目标
Char ch = Actor.findChar(bolt.collisionPos);
if (ch != null) {
    wandProc(ch, chargesPerCast());
    ch.damage(damageRoll(), this);
    
    if (bolt.path.size() > bolt.dist + 1) {
        Ballistica trajectory = new Ballistica(ch.pos, bolt.path.get(bolt.dist + 1), Ballistica.MAGIC_BOLT);
        int strength = buffedLvl() + 3;
        throwChar(ch, trajectory, strength, false, true, this);
    }
}
```

---

### throwChar(Char ch, Ballistica trajectory, int power, boolean closeDoors, boolean collideDmg, Object cause)
**可见性**：public static

**方法职责**：击退角色

**参数**：
- `ch` (Char)：要击退的角色
- `trajectory` (Ballistica)：击退轨迹
- `power` (int)：击退力度
- `closeDoors` (boolean)：是否关闭经过的门
- `collideDmg` (boolean)：碰撞是否造成伤害
- `cause` (Object)：伤害来源

**核心实现逻辑**：
```java
// Boss击退力度减半
if (ch.properties().contains(Char.Property.BOSS)) {
    power = (power + 1) / 2;
}

int dist = Math.min(trajectory.dist, power);

// 检查不可移动
if (dist <= 0 || ch.rooted || ch.properties().contains(Char.Property.IMMOVABLE)) return;

// 大型角色检查路径
if (Char.hasProp(ch, Char.Property.LARGE)) {
    for (int i = 1; i <= dist; i++) {
        if (!Dungeon.level.openSpace[trajectory.path.get(i)]) {
            dist = i - 1;
            break;
        }
    }
}

// 执行击退动画
Actor.add(new Pushing(ch, ch.pos, newPos, new Callback() {
    public void call() {
        ch.pos = newPos;
        if (collideDmg && ch.isActive()) {
            ch.damage(Random.NormalIntRange(dist, 2 * dist), new Knockback());
            if (ch.isActive()) {
                Paralysis.prolong(ch, Paralysis.class, 1 + dist / 2f);
            }
        }
        Dungeon.level.occupyCell(ch);
    }
}));
```

---

### onHit(MagesStaff staff, Char attacker, Char defender, int damage)
**可见性**：public

**是否覆写**：是

**方法职责**：近战命中麻痹目标时触发爆炸

**触发条件**：目标有麻痹状态且没有追踪器

**核心实现逻辑**：
```java
if (defender.buff(Paralysis.class) != null && defender.buff(BWaveOnHitTracker.class) == null) {
    defender.buff(Paralysis.class).detach();
    int dmg = Hero.heroDamageIntRange(8 + 2 * buffedLvl(), 12 + 3 * buffedLvl());
    defender.damage(Math.round(procChanceMultiplier(attacker) * dmg), this);
    BlastWave.blast(defender.pos);
    Sample.INSTANCE.play(Assets.Sounds.BLAST);
    
    // 短暂免疫防止堆叠
    Buff.prolong(defender, BWaveOnHitTracker.class, 3f);
}
```

---

## BlastWave 内部类

### 基本信息
冲击波是一个视觉效果类，显示圆形扩散的波纹。

### 方法
| 方法 | 职责 |
|------|------|
| `reset(int, float)` | 重置位置和大小 |
| `update()` | 更新动画 |
| `blast(int)` | 静态方法创建冲击波 |

## 8. 对外暴露能力

继承自 DamageWand，提供伤害和击退能力。`throwChar()` 是静态方法，可被其他类使用。

## 9. 运行机制与调用链

### 击退机制
- 周围敌人击退距离：`1.5 + level / 2`
- 中心目标击退距离：`level + 3`
- 碰撞伤害：`dist ~ 2*dist`
- 碰撞后麻痹：`1 + dist / 2` 回合

### 特殊情况
- Boss 击退距离减半
- 大型角色需要开阔路径
- 深坑可致死

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.wands.wandofblastwave.name` | 冲击波法杖 | 法杖名称 |
| `items.wands.wandofblastwave.staff_name` | 冲击波魔杖 | 手杖名称 |
| `items.wands.wandofblastwave.ondeath` | 你用冲击波法杖炸碎了自己... | 死亡消息 |
| `items.wands.wandofblastwave.knockback_ondeath` | 你死于撞击... | 击退死亡消息 |
| `items.wands.wandofblastwave.desc` | 描述文本 | 法杖描述 |
| `items.wands.wandofblastwave.stats_desc` | 统计描述 | 伤害和击退 |
| `items.wands.wandofblastwave.upgrade_stat_name_2` | 击退 | 属性名称 |
| `items.wands.wandofblastwave.bmage_desc` | 战斗法师描述 | 子职业效果 |

## 11. 使用示例

```java
// 创建冲击波法杖
WandOfBlastWave wand = new WandOfBlastWave();
wand.level(2);  // +2级

// 施放法术
Ballistica bolt = new Ballistica(hero.pos, targetPos, Ballistica.PROJECTILE);
wand.onZap(bolt);  // 造成 3-9 伤害，击退敌人

// 静态击退方法
WandOfBlastWave.throwChar(enemy, trajectory, 5, false, true, wand);
```

## 12. 开发注意事项

### 状态依赖
- 击退可能将敌人推到深坑
- 可能误伤盟友（不击退）

### 常见陷阱
- 击退敌人可能触发陷阱
- 可能击退到安全位置

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改击退力度计算
- 修改碰撞伤害公式

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点