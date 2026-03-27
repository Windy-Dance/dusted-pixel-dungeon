# Cleanse 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/Cleanse.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends ClericSpell |
| **代码行数** | 115 |
| **中文名称** | 净化术 |

---

## 法术概述

`Cleanse`（净化术）是牧师职业的3级法术。该法术的主要功能是：

1. **核心效果**：移除自己和所有可见盟友身上的负面状态效果，并提供临时免疫和护盾保护
2. **战术价值**：在面对具有强大负面状态效果的敌人时提供紧急解控和防护
3. **使用场景**：中毒、诅咒、麻痹等负面状态累积时，或预见到即将遭受大量负面效果时

**法术类型**：
- **目标类型**：Area Effect（区域效果，影响多个目标）
- **充能消耗**：2点充能
- **天赋需求**：CLEANSE 天赋

---

## 类关系图

```mermaid
classDiagram
    class ClericSpell {
        <<abstract>>
        +void onCast(HolyTome, Hero)
        +float chargeUse(Hero)
        +boolean canCast(Hero)
    }
    
    class Cleanse {
        +INSTANCE: Cleanse
        +int icon()
        +String desc()
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +void onCast(HolyTome, Hero)
    }
    
    ClericSpell <|-- Cleanse
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | Cleanse.INSTANCE | 单例实例 |

---

## 核心属性

### 充能消耗

| 属性 | 值 | 说明 |
|------|-----|------|
| `chargeUse()` | 2f | 每次施放消耗2点充能 |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | false | 无需目标选择，自动影响区域内的所有盟友 |
| `targetingFlags()` | -1 | 无目标选择标志 |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires CLEANSE | 施放此法术所需的天赋 |

### 效果数值

| 效果 | 1点天赋 | 2点天赋 | 3点天赋 |
|------|---------|---------|---------|
| 护盾值 | 10点 | 20点 | 30点 |
| 免疫持续时间 | 0回合 | 2回合 | 4回合 |
| 影响范围 | 自身+可见盟友 | 自身+可见盟友 | 自身+可见盟友 |

---

## 方法详解

### onCast(HolyTome tome, Hero hero)

```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    // 收集受影响的目标并应用净化效果
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：执行净化术的主要逻辑，影响多个目标。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄

**实现逻辑**：
1. **目标收集**：
   - 添加英雄自身到受影响列表
   - 添加所有视野范围内的盟友Mob
   - 如果存在生命链接盟友且未包含，也添加到列表
2. **负面状态移除**：
   - 遍历每个目标的所有Buff
   - 移除所有负面类型Buff（除了AllyBuff和LostInventory）
3. **免疫效果应用**：
   - 天赋等级>1时，施加PotionOfCleansing.Cleanse免疫效果
   - 持续时间：2×(天赋等级-1)回合
4. **护盾提供**：
   - 为每个目标施加护盾：10×天赋等级点
5. **视觉反馈**：
   - 显示蓝色光效（#FF4CD2）
   - 英雄执行施法动画

---

### desc()

```java
public String desc() {
    int immunity = 2 * (Dungeon.hero.pointsInTalent(Talent.CLEANSE)-1);
    if (immunity > 0) immunity++;
    int shield = 10 * Dungeon.hero.pointsInTalent(Talent.CLEANSE);
    return Messages.get(this, "desc", immunity, shield) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本，包含动态计算的免疫时间和护盾值。

**返回值**：
- 包含免疫持续时间、护盾值和充能消耗的完整描述字符串

---

## 特殊机制

### 负面状态过滤

净化术会移除大部分负面状态，但有例外：
- **保留AllyBuff**：盟友相关的Buff不会被移除
- **保留LostInventory**：丢失物品相关的状态不会被移除
- **移除其他所有负面Buff**：包括中毒、诅咒、麻痹、虚弱等

### 免疫机制

- **基础免疫**：1点天赋时不提供免疫（仅净化现有状态）
- **进阶免疫**：2-3点天赋时提供临时免疫，防止新的负面状态
- **免疫来源**：使用药水系统的Cleanse Buff实现

### 目标范围

- **自动检测**：自动检测视野范围内的所有盟友
- **生命链接支持**：特殊处理生命链接盟友，确保其被包含
- **无距离限制**：只要在视野内就有效，不受距离限制

---

## 使用示例

### 基本施法

```java
// 施放净化术
if (hero.hasTalent(Talent.CLEANSE)) {
    Cleanse.INSTANCE.onCast(holyTome, hero);
}
```

### 团队支援

```java
// 在团队战斗中使用净化术
// 自动影响所有可见盟友，无需手动选择
Cleanse.INSTANCE.onCast(tome, hero);
```

### 状态检查

```java
// 检查是否有需要净化的负面状态
boolean hasNegativeBuffs = false;
for (Buff b : hero.buffs()) {
    if (b.type == Buff.buffType.NEGATIVE) {
        hasNegativeBuffs = true;
        break;
    }
}

if (hasNegativeBuffs) {
    Cleanse.INSTANCE.onCast(tome, hero);
}
```

---

## 注意事项

### 平衡性考虑

1. **高充能消耗**：2点充能消耗较高，需要在关键时刻使用
2. **天赋投资**：1点天赋只提供净化，2-3点才提供免疫，需要合理投资
3. **时机选择**：最佳使用时机是在负面状态累积或预见到强负面效果前

### 特殊机制

1. **例外状态**：某些特殊负面状态不会被移除（如AllyBuff相关）
2. **护盾叠加**：护盾可以与其他护盾效果叠加
3. **免疫优先级**：免疫效果在净化后立即生效，防止后续负面状态

### 技术限制

1. **视野限制**：只能影响视野范围内的盟友
2. **即时效果**：净化是即时效果，无法对未来的负面状态提前预防（除非有免疫）
3. **Buff类型识别**：依赖Buff的type属性正确设置为NEGATIVE

---

## 最佳实践

### 战斗策略

- **紧急解控**：在被控制或中毒时立即使用，恢复行动能力
- **团队保护**：在面对群体负面状态攻击时保护整个团队
- **预防性使用**：在已知敌人有强力负面状态技能前预先使用（配合高天赋）

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.CLEANSE) && 
    hero.pointsInTalent(Talent.CLEANSE) >= 2 &&
    hero.hasTalent(Talent.LIFE_LINK)) {
    // 净化+免疫+生命链接，形成强大的团队生存体系
}
```

### 职业优化

- **祭司流派**：配合其他治疗法术，形成完整的支援体系
- **圣骑士流派**：配合高防御，形成坚不可摧的前线
- **通用策略**：在精英怪或Boss战中作为关键的应急技能

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `ClericSpell` | 父类 | 所有牧师法术的基类 |
| `PotionOfCleansing.Cleanse` | 依赖 | 提供免疫效果的Buff |
| `Barrier` | 效果 | 护盾Buff，吸收伤害 |
| `PowerOfMany` | 协同 | 力量合一天赋，提供额外盟友 |
| `LifeLinkSpell` | 协同 | 生命链接法术，确保盟友被包含 |
| `AllyBuff` | 例外 | 不会被移除的盟友相关Buff |
| `LostInventory` | 例外 | 不会被移除的物品相关状态 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.cleanse.name` | "净化术" | 法术名称 |
| `spells.cleanse.desc` | "净化自身和所有可见盟友的负面状态，并提供%d回合的免疫和%d点护盾。" | 法术描述 |
| `spells.cleanse.charge_cost` | "%d 充能" | 充能消耗提示 |