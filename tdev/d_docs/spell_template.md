# [SpellClassName] 法术详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/[SpellClassName].java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends [BaseClass] |
| **代码行数** | [line_count] |
| **中文名称** | [chinese_name] |

---

## 法术概述

[SpellClassName]（[chinese_name]）是牧师职业的[等级]级法术。该法术的主要功能是：

1. **核心效果**：[describe main effect]
2. **战术价值**：[describe tactical value]
3. **使用场景**：[describe when to use]

**法术类型**：
- **目标类型**：[Self-Targeted / Targeted / Inventory-Based / Area Effect]
- **充能消耗**：[charge_cost] 点充能
- **天赋需求**：[talent_name] 天赋

---

## 4. 继承与协作关系

```mermaid
classDiagram
    class ClericSpell {
        <<abstract>>
        +void onCast(HolyTome, Hero)
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +String name()
        +String shortDesc()
        +String desc()
        +boolean usesTargeting()
        +int targetingFlags()
    }
    
    class TargetedClericSpell {
        +void onCast(HolyTome, Hero)
        +int targetingFlags()
        #abstract void onTargetSelected(HolyTome, Hero, Integer)
    }
    
    class InventoryClericSpell {
        +void onCast(HolyTome, Hero)
        #abstract void onItemSelected(HolyTome, Hero, Item)
    }
    
    class [SpellClassName] {
        +INSTANCE: [SpellClassName]
        +int icon()
        +String desc()
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +void onCast(HolyTome, Hero)
        [specific_methods]
    }
    
    ClericSpell <|-- TargetedClericSpell
    ClericSpell <|-- InventoryClericSpell
    ClericSpell <|-- [SpellClassName]
    TargetedClericSpell <|-- [SpellClassName]
    InventoryClericSpell <|-- [SpellClassName]
```

---

## 静态常量表

[If applicable]

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | [SpellClassName].INSTANCE | 单例实例 |

---

## 核心属性

### 充能消耗

| 属性 | 值 | 说明 |
|------|-----|------|
| `chargeUse()` | [charge_value]f | 每次施放消耗的充能点数 |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | [true/false] | 是否需要目标选择 |
| `targetingFlags()` | [flag_value] | 目标选择的弹道标志 |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires [talent_name] | 施放此法术所需的天赋 |

---

## 7. 方法详解

### onCast(HolyTome tome, Hero hero)

```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    // [spell implementation]
}
```

**方法作用**：执行法术的主要逻辑。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄

**实现逻辑**：
1. [step 1 description]
2. [step 2 description]
3. 调用 `onSpellCast(tome, hero)` 完成施法后处理

---

### desc()

```java
@Override
public String desc() {
    return Messages.get(this, "desc", [parameters]) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本。

**返回值**：
- 包含法术效果详情和充能消耗的完整描述字符串

---

### chargeUse(Hero hero)

```java
@Override
public float chargeUse(Hero hero) {
    return [charge_value]f;
}
```

**方法作用**：返回施放此法术所需的充能数量。

**参数**：
- `hero` (Hero)：施法的英雄（用于天赋计算）

**返回值**：
- `[charge_value]f`：充能消耗数量

---

### canCast(Hero hero)

```java
@Override
public boolean canCast(Hero hero) {
    return super.canCast(hero) && hero.hasTalent(Talent.[TALENT_NAME]);
}
```

**方法作用**：检查英雄是否可以施放此法术。

**参数**：
- `hero` (Hero)：要检查的英雄

**返回值**：
- `true`：可以施放（拥有必要天赋且未被魔法免疫）
- `false`：无法施放

---

## 内部类

[If applicable]

### [InnerClassName]

```java
public static class [InnerClassName] extends [ParentClass] {
    // [implementation details]
}
```

**类作用**：[describe purpose]

**关键属性**：
- `[field_name]`: [field_type] - [description]

**关键方法**：
- `[method_name]()`: [description]

---

## 11. 使用示例

### 基本施法

```java
// 施放法术
if (hero.hasTalent(Talent.[TALENT_NAME])) {
    [SpellClassName].INSTANCE.onCast(holyTome, hero);
}
```

### 在神圣典籍中使用

```java
// 通过神圣典籍施放
HolyTome tome = new HolyTome();
if (tome.canCast(hero, [SpellClassName].INSTANCE)) {
    tome.targetingSpell = [SpellClassName].INSTANCE;
    [SpellClassName].INSTANCE.onCast(tome, hero);
}
```

---

## 注意事项

### 平衡性考虑

1. **充能管理**：[charge_management_notes]
2. **时机选择**：[timing_considerations]
3. **配合天赋**：[talent_synergies]

### 特殊机制

1. **[mechanism_name]**：[mechanism_description]
2. **[another_mechanism]**：[description]

### 技术限制

1. **目标选择**：[targeting_limitations]
2. **范围限制**：[range_limitations]

---

## 最佳实践

### 战斗策略

- **进攻性使用**：[offensive_usage]
- **防御性使用**：[defensive_usage]
- **支援性使用**：[support_usage]

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.[TALENT_NAME]) && 
    hero.hasTalent(Talent.[SYNERGY_TALENT])) {
    // 最佳效果组合
}
```

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `ClericSpell` | 父类 | 所有牧师法术的基类 |
| `HolyTome` | 使用者 | 神圣典籍，法术载体 |
| `Hero` | 施法者 | 英雄角色 |
| `Talent.[TALENT_NAME]` | 依赖 | 必需的天赋 |
| `[RelatedBuffClass]` | 效果 | 法术产生的Buff效果 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.[spell_key].name` | [english_name] | 法术名称 |
| `spells.[spell_key].desc` | [description] | 法术描述 |
| `spells.[spell_key].prompt` | [prompt_text] | 目标选择提示（如适用） |
| `spells.[spell_key].charge_cost` | "%d 充能" | 充能消耗提示 |