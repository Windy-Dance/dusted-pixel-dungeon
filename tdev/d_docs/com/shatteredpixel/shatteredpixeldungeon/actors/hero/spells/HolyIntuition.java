# HolyIntuition 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/HolyIntuition.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends InventoryClericSpell |
| **代码行数** | 86 |
| **中文名称** | 神圣直觉 |

---

## 法术概述

`HolyIntuition`（神圣直觉）是牧师职业的1级法术。该法术的主要功能是：

1. **核心效果**：对未识别的可装备物品或法杖进行鉴定，揭示其是否被诅咒
2. **战术价值**：帮助玩家安全地识别高风险物品，避免装备诅咒物品
3. **使用场景**：获得未知品质的武器、护甲或法杖时，需要确认安全性

**法术类型**：
- **目标类型**：Inventory-Based（需要从背包中选择物品）
- **充能消耗**：4 - 天赋等级 点充能（1-3点）
- **天赋需求**：HOLY_INTUITION 天赋

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
    
    class InventoryClericSpell {
        +void onCast(HolyTome, Hero)
        #String inventoryPrompt()
        #Class~? extends Bag~ preferredBag()
        #boolean usableOnItem(Item)
        #abstract void onItemSelected(HolyTome, Hero, Item)
    }
    
    class HolyIntuition {
        +INSTANCE: HolyIntuition
        +int icon()
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        #boolean usableOnItem(Item)
        #void onItemSelected(HolyTome, Hero, Item)
    }
    
    ClericSpell <|-- InventoryClericSpell
    InventoryClericSpell <|-- HolyIntuition
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | HolyIntuition.INSTANCE | 单例实例 |

---

## 核心属性

### 充能消耗

| 天赋等级 | 充能消耗 | 说明 |
|----------|----------|------|
| 1点 | 3点 | 基础消耗减1 |
| 2点 | 2点 | 基础消耗减2 |
| 3点 | 1点 | 基础消耗减3（最低消耗） |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | false | 使用物品选择而非位置选择 |
| `usableOnItem()` | 可装备物品或法杖 | 限定可选择的物品类型 |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires HOLY_INTUITION | 施放此法术所需的天赋 |

### 物品限制

| 条件 | 说明 |
|------|------|
| **物品类型** | EquipableItem 或 Wand |
| **识别状态** | 必须未识别（!isIdentified()） |
| **诅咒状态** | 诅咒状态未知（!cursedKnown） |

---

## 方法详解

### onItemSelected(HolyTome tome, Hero hero, Item item)

```java
@Override
protected void onItemSelected(HolyTome tome, Hero hero, Item item) {
    // 对选中的物品进行诅咒鉴定
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：处理玩家选择的物品，揭示其诅咒状态。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄
- `item` (Item)：选择的物品（可能为 null）

**实现逻辑**：
1. **空值检查**：如果物品为 null（玩家取消），直接返回
2. **诅咒揭示**：设置 `item.cursedKnown = true`
3. **结果反馈**：
   - 如果物品被诅咒：显示警告消息 "被诅咒了！"
   - 如果物品未被诅咒：显示信息消息 "未被诅咒"
4. **视觉特效**：播放阅读音效和鉴定特效（Identification粒子效果）
5. **完成施法**：调用 `onSpellCast(tome, hero)` 处理后续逻辑

---

### usableOnItem(Item item)

```java
@Override
protected boolean usableOnItem(Item item) {
    return (item instanceof EquipableItem || item instanceof Wand) 
           && !item.isIdentified() 
           && !item.cursedKnown;
}
```

**方法作用**：确定哪些物品可以在背包选择器中被选中。

**返回值**：
- `true`：物品符合条件（可装备/法杖 + 未识别 + 诅咒状态未知）
- `false`：物品不符合条件（已过滤掉）

---

### chargeUse(Hero hero)

```java
@Override
public float chargeUse(Hero hero) {
    return 4 - hero.pointsInTalent(Talent.HOLY_INTUITION);
}
```

**方法作用**：返回基于天赋等级的动态充能消耗。

**返回值**：
- 基础消耗4点减去天赋等级（最少1点消耗）

---

## 特殊机制

### 诅咒鉴定系统

- **部分鉴定**：只揭示诅咒状态，不完全识别物品属性
- **安全机制**：避免玩家意外装备诅咒物品
- **信息价值**：在完全识别前提供关键安全信息

### 物品类型限制

- **可装备物品**：包括武器、护甲等所有继承自EquipableItem的物品
- **法杖**：所有Wand类型的物品
- **排除物品**：药水、卷轴、食物等消耗品无法使用此法术

### 消耗优化

- **天赋投资**：每点天赋减少1点充能消耗
- **成本效益**：高级天赋下消耗低至1点，性价比极高
- **资源管理**：鼓励玩家投资天赋以降低使用成本

---

## 使用示例

### 基本施法

```java
// 施放神圣直觉（会打开物品选择器）
if (hero.hasTalent(Talent.HOLY_INTUITION)) {
    HolyIntuition.INSTANCE.onCast(holyTome, hero);
    // 玩家选择物品后自动调用 onItemSelected
}
```

### 物品鉴定流程

```java
// 完整的鉴定流程
HolyTome tome = new HolyTome();
if (tome.canCast(hero, HolyIntuition.INSTANCE)) {
    // 1. 打开背包选择器（只显示符合条件的物品）
    HolyIntuition.INSTANCE.onCast(tome, hero);
    
    // 2. 玩家选择未识别的武器/护甲/法杖
    // 3. 自动揭示诅咒状态并显示相应消息
}
```

### 消耗计算

```java
// 计算当前消耗
int currentCost = (int) HolyIntuition.INSTANCE.chargeUse(hero);
// 根据天赋等级，消耗为 4 - talentLevel
```

---

## 注意事项

### 平衡性考虑

1. **渐进消耗**：天赋等级显著影响使用成本，鼓励投资
2. **部分鉴定**：只揭示诅咒状态，保留完全识别的价值
3. **物品限制**：专注于高风险的可装备物品，避免滥用

### 特殊机制

1. **安全优先**：专门针对可能造成严重后果的诅咒装备
2. **信息不对称**：保持游戏的探索性和风险决策
3. **视觉反馈**：清晰的消息提示帮助玩家做出正确决策

### 技术限制

1. **物品状态**：依赖物品的cursedKnown和isIdentified属性正确设置
2. **类型检查**：使用instanceof进行运行时类型检查
3. **背包过滤**：物品选择器自动过滤不可用物品

---

## 最佳实践

### 鉴定策略

- **高价值物品**：优先对稀有或强力的未识别装备使用
- **战斗准备**：在重要战斗前确保装备安全性  
- **法杖管理**：对未知法杖进行诅咒检查，避免意外触发

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.HOLY_INTUITION) && 
    hero.pointsInTalent(Talent.HOLY_INTUITION) >= 2) {
    // 低消耗（2点或更少）使得频繁使用变得可行
    // 可以对多个物品进行安全鉴定
}
```

### 资源管理

- **充能规划**：在充能充足时批量鉴定多个物品
- **时机选择**：在安全环境下（无敌人威胁）进行鉴定
- **成本效益**：权衡鉴定成本与潜在风险，优先高风险物品

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `InventoryClericSpell` | 父类 | 物品选择法术基类 |
| `EquipableItem` | 依赖 | 可装备物品基类 |
| `Wand` | 依赖 | 法杖类物品 |
| `Identification` | 依赖 | 鉴定视觉特效 |
| `GLog` | 依赖 | 游戏日志系统（消息显示） |
| `Item.cursedKnown` | 依赖 | 诅咒状态已知标志 |
| `Item.isIdentified()` | 依赖 | 物品识别状态 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.holyintuition.name` | "神圣直觉" | 法术名称 |
| `spells.holyintuition.cursed` | "被诅咒了！" | 诅咒物品警告消息 |
| `spells.holyintuition.uncursed` | "未被诅咒" | 安全物品确认消息 |
| `spells.holyintuition.prompt` | "选择要鉴定的物品" | 物品选择提示 |
| `spells.holyintuition.charge_cost` | "%d 充能" | 充能消耗提示 |