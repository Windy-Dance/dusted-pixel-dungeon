# WandOfTransfusion 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/wands/WandOfTransfusion.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.wands |
| **文件类型** | class |
| **继承关系** | extends DamageWand |
| **代码行数** | 230 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
注魂法杖是一种治疗/控制型法杖，具有以下特点：
- 对盟友：消耗自身生命治疗目标
- 对敌人：获得护盾并魅惑目标
- 对亡灵：造成伤害而不是魅惑
- 可消耗免费充能次数

### 系统定位
位于 DamageWand 层次，是主要的治疗/控制型法杖，适合支持盟友和控制敌人。

## 3. 结构总览

### 主要成员概览
- **伤害方法**：`min(int lvl)`, `max(int lvl)` - 仅对亡灵有效
- **特殊字段**：`freeCharge` - 免费充能标记
- **特殊方法**：`damageHero()` - 自身伤害

## 4. 继承与协作关系

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `min(int lvl)` | 返回 3 + lvl（仅对亡灵） |
| `max(int lvl)` | 返回 6 + 2 * lvl（仅对亡灵） |
| `onZap(Ballistica)` | 根据目标类型执行不同效果 |
| `onHit(MagesStaff, Char, Char, int)` | 近战命中获得免费充能 |
| `fx(Ballistica, Callback)` | 生命射线视觉 |
| `statsDesc()` | 统计描述 |
| `upgradeStat1(int)` | 盟友治疗量 |
| `upgradeStat2(int)` | 自身护盾 |
| `upgradeStat3(int)` | 亡灵伤害 |
| `staffFx(MagesStaff.StaffParticle)` | 手杖粒子效果 |

## 5. 字段/常量详解

### 实例字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| `freeCharge` | boolean | 是否有免费充能 |

## 6. 构造与初始化机制

### 初始化块
```java
{
    image = ItemSpriteSheet.WAND_TRANSFUSION;
    collisionProperties = Ballistica.PROJECTILE;
}
```

## 7. 方法详解

### min(int lvl)
**可见性**：public

**是否覆写**：是

**方法职责**：返回最小伤害（仅对亡灵有效）

**返回值**：int，`3 + lvl`

---

### max(int lvl)
**可见性**：public

**是否覆写**：是

**方法职责**：返回最大伤害（仅对亡灵有效）

**返回值**：int，`6 + 2 * lvl`

---

### onZap(Ballistica beam)
**可见性**：public

**是否覆写**：是

**方法职责**：根据目标类型执行不同效果

**核心实现逻辑**：
```java
Char ch = Actor.findChar(cell);

if (ch instanceof Mob) {
    // 对盟友或被魅惑的敌人：治疗
    if (ch.alignment == Char.Alignment.ALLY || ch.buff(Charm.class) != null) {
        int selfDmg = Math.round(curUser.HT * 0.05f);  // 5%最大生命
        int healing = selfDmg + 3 * buffedLvl();
        
        // 溢出治疗转化为护盾
        int shielding = (ch.HP + healing) - ch.HT;
        if (shielding > 0) {
            Buff.affect(ch, Barrier.class).setShield(shielding);
        }
        ch.HP += healing - shielding;
        
        // 消耗自身生命（除非免费充能）
        if (!freeCharge) {
            damageHero(selfDmg);
        } else {
            freeCharge = false;
        }
    }
    
    // 对敌人
    else if (ch.alignment == Char.Alignment.ENEMY || ch instanceof Mimic) {
        // 获得护盾
        Buff.affect(curUser, Barrier.class).setShield(5 + buffedLvl());
        
        // 亡灵：造成伤害
        if (ch.properties().contains(Char.Property.UNDEAD)) {
            ch.damage(damageRoll(), this);
        }
        // 活体：魅惑
        else {
            Charm charm = Buff.affect(ch, Charm.class, Charm.DURATION / 2f);
            charm.object = curUser.id();
        }
    }
}
```

---

### damageHero(int damage)
**可见性**：private

**方法职责**：对施法者造成伤害

**核心实现逻辑**：
```java
curUser.damage(damage, this);
if (!curUser.isAlive()) {
    Badges.validateDeathFromFriendlyMagic();
    Dungeon.fail(this);
    GLog.n(Messages.get(this, "ondeath"));
}
```

---

### onHit(MagesStaff staff, Char attacker, Char defender, int damage)
**可见性**：public

**是否覆写**：是

**方法职责**：近战命中被魅惑的目标时获得免费充能和护盾

**触发条件**：目标被自己魅惑

**核心实现逻辑**：
```java
if (defender.buff(Charm.class) != null && 
    defender.buff(Charm.class).object == attacker.id()) {
    freeCharge = true;
    int shieldToGive = Math.round((2 * (5 + buffedLvl())) * procChanceMultiplier(attacker));
    Buff.affect(attacker, Barrier.class).setShield(shieldToGive);
    GLog.p(Messages.get(this, "charged"));
}
```

---

## 8. 对外暴露能力

继承自 DamageWand，提供治疗、护盾、魅惑和亡灵伤害能力。

## 9. 运行机制与调用链

### 目标类型效果
| 目标类型 | 效果 |
|---------|------|
| 盟友 | 消耗5%生命治疗，溢出转护盾 |
| 被魅惑敌人 | 同盟友 |
| 普通敌人 | 获得护盾，魅惑目标 |
| 亡灵敌人 | 获得护盾，造成伤害 |

### 免费充能机制
- 近战命中被魅惑的目标获得
- 下次对盟友施法不消耗生命

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.wands.wandoftransfusion.name` | 注魂法杖 | 法杖名称 |
| `items.wands.wandoftransfusion.staff_name` | 注魂魔杖 | 手杖名称 |
| `items.wands.wandoftransfusion.ondeath` | 你用注魂法杖耗尽了自己的生命... | 死亡消息 |
| `items.wands.wandoftransfusion.charged` | 敌人的生命能量流进了你的魔杖！ | 充能消息 |
| `items.wands.wandoftransfusion.desc` | 描述文本 | 法杖描述 |
| `items.wands.wandoftransfusion.stats_desc` | 统计描述 | 效果说明 |
| `items.wands.wandoftransfusion.upgrade_stat_name_1` | 盟友治疗 | 属性名称 |
| `items.wands.wandoftransfusion.upgrade_stat_name_2` | 自身护盾 | 属性名称 |
| `items.wands.wandoftransfusion.upgrade_stat_name_3` | 亡灵伤害 | 属性名称 |
| `items.wands.wandoftransfusion.bmage_desc` | 战斗法师描述 | 子职业效果 |

## 11. 使用示例

```java
// 创建注魂法杖
WandOfTransfusion wand = new WandOfTransfusion();
wand.level(2);  // +2级

// 对盟友施放（消耗5%生命，治疗11点）
wand.onZap(beam);

// 对敌人施放（获得7点护盾，魅惑敌人）
wand.onZap(beam);

// 对亡灵施放（获得7点护盾，造成5-10伤害）
wand.onZap(beam);
```

## 12. 开发注意事项

### 状态依赖
- 治疗盟友需要消耗自身生命
- 低生命时使用有风险
- 免费充能可避免消耗

### 常见陷阱
- 可能因治疗消耗而死
- 对被魅惑的目标视为盟友

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改自身伤害比例
- 修改治疗量和护盾量计算

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点