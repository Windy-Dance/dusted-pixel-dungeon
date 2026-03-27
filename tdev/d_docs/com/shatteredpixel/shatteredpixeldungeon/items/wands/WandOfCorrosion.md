# WandOfCorrosion 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/wands/WandOfCorrosion.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.wands |
| **文件类型** | class |
| **继承关系** | extends Wand |
| **代码行数** | 137 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
酸蚀法杖是一种持续伤害型法杖，具有以下特点：
- 在目标位置生成腐蚀酸雾
- 酸雾造成递增的持续伤害
- 伤害起始值随法杖等级提升
- 近战命中可施加淤泥效果

### 系统定位
位于 Wand 层次（非 DamageWand），是间接持续伤害型法杖，不直接造成伤害而是通过区域效果。

## 3. 结构总览

### 主要成员概览
- **施法方法**：`onZap()` - 生成酸雾
- **近战效果**：`onHit()` - 施加淤泥
- **视觉效果**：`fx()` - 腐蚀飞弹

## 4. 继承与协作关系

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `onZap(Ballistica)` | 生成腐蚀酸雾 |
| `fx(Ballistica, Callback)` | 腐蚀飞弹视觉 |
| `onHit(MagesStaff, Char, Char, int)` | 近战命中施加淤泥 |
| `staffFx(MagesStaff.StaffParticle)` | 手杖粒子效果 |
| `statsDesc()` | 统计描述 |
| `upgradeStat1(int)` | 酸蚀伤害 |
| `upgradeStat2(int)` | 气体总量倍数 |

## 5. 字段/常量详解

无实例字段。

## 6. 构造与初始化机制

### 初始化块
```java
{
    image = ItemSpriteSheet.WAND_CORROSION;
    collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
}
```

## 7. 方法详解

### onZap(Ballistica bolt)
**可见性**：public

**是否覆写**：是

**方法职责**：在目标位置生成腐蚀酸雾

**核心实现逻辑**：
```java
CorrosiveGas gas = Blob.seed(bolt.collisionPos, 50 + 10 * buffedLvl(), CorrosiveGas.class);
CellEmitter.get(bolt.collisionPos).burst(Speck.factory(Speck.CORROSION), 10);
gas.setStrength(2 + buffedLvl(), getClass());
GameScene.add(gas);
Sample.INSTANCE.play(Assets.Sounds.GAS);

// 对范围内目标触发法杖效果
for (int i : PathFinder.NEIGHBOURS9) {
    Char ch = Actor.findChar(bolt.collisionPos + i);
    if (ch != null) {
        wandProc(ch, chargesPerCast());
    }
}
```

---

### onHit(MagesStaff staff, Char attacker, Char defender, int damage)
**可见性**：public

**是否覆写**：是

**方法职责**：近战命中时可能施加淤泥效果

**触发概率**：
- 0级：33%
- 1级：50%
- 2级：60%

**核心实现逻辑**：
```java
int level = Math.max(0, buffedLvl());
float procChance = (level + 1f) / (level + 3f) * procChanceMultiplier(attacker);
if (Random.Float() < procChance) {
    float powerMulti = Math.max(1f, procChance);
    Buff.affect(defender, Ooze.class).set(Ooze.DURATION * powerMulti);
    CellEmitter.center(defender.pos).burst(CorrosionParticle.SPLASH, 5);
}
```

---

### statsDesc()
**可见性**：public

**是否覆写**：是

**方法职责**：返回统计描述

**返回值**：String，显示酸雾起始伤害值

---

### upgradeStat1(int level)
**可见性**：public

**是否覆写**：是

**方法职责**：返回酸蚀伤害值

**返回值**：String，`2 + level`

---

### upgradeStat2(int level)
**可见性**：public

**是否覆写**：是

**方法职责**：返回气体总量倍数

**返回值**：String，`1 + 0.2 * level` 倍

## 8. 对外暴露能力

继承自 Wand，提供持续腐蚀伤害能力。

## 9. 运行机制与调用链

### 酸雾机制
- 体积：`50 + 10 * level` 格
- 伤害：起始 `2 + level`，每回合递增
- 持续：直到气体消散

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.wands.wandofcorrosion.name` | 酸蚀法杖 | 法杖名称 |
| `items.wands.wandofcorrosion.staff_name` | 酸蚀魔杖 | 手杖名称 |
| `items.wands.wandofcorrosion.desc` | 描述文本 | 法杖描述 |
| `items.wands.wandofcorrosion.stats_desc` | 统计描述 | 酸雾伤害 |
| `items.wands.wandofcorrosion.upgrade_stat_name_1` | 酸蚀伤害 | 属性名称 |
| `items.wands.wandofcorrosion.upgrade_stat_name_2` | 气体总量 | 属性名称 |
| `items.wands.wandofcorrosion.bmage_desc` | 战斗法师描述 | 子职业效果 |

## 11. 使用示例

```java
// 创建酸蚀法杖
WandOfCorrosion wand = new WandOfCorrosion();
wand.level(2);  // +2级

// 施放法术
Ballistica bolt = new Ballistica(hero.pos, targetPos, Ballistica.STOP_TARGET);
wand.onZap(bolt);  // 在目标位置生成酸雾

// 酸雾效果：
// - 体积：70 格
// - 起始伤害：4 点/回合
// - 每回合伤害递增
```

## 12. 开发注意事项

### 状态依赖
- 不继承 DamageWand，不直接造成伤害
- 伤害通过 CorrosiveGas 区域效果实现

### 常见陷阱
- 目标可能离开酸雾区域
- 需要预判敌人位置

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改酸雾体积计算
- 修改伤害起始值和递增公式

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点