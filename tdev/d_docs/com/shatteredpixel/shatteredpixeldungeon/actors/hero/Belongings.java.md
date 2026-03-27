# Belongings - 英雄物品管理系统

## 概述
`Belongings` 类管理英雄的所有物品，包括装备、背包和特殊物品。它提供了一个统一的接口来访问和操作英雄持有的所有物品，处理装备状态、持久化存储以及与游戏其他系统的交互。

该类实现了 `Iterable<Item>` 接口，可以直接遍历所有物品（包括装备和背包中的物品）。

## 内部类

### Backpack - 背包类
继承自 `Bag` 类，专门用于英雄的主背包：
- **capacity()**: 计算背包容量，考虑额外的袋子和副武器占用的格子

## 字段

### 核心字段
- **owner**: `Hero` - 物品所有者的引用
- **backpack**: `Backpack` - 主背包实例

### 装备字段
- **weapon**: `KindOfWeapon` - 主武器
- **armor**: `Armor` - 盔甲  
- **artifact**: `Artifact` - 遗物
- **misc**: `KindofMisc` - 杂项装备（通常是戒指）
- **ring**: `Ring` - 戒指（为了向后兼容保留）

### 特殊用途字段
- **thrownWeapon**: `KindOfWeapon` - 临时作为当前武器的投掷武器
- **abilityWeapon**: `KindOfWeapon` - 决斗者使用技能时的武器引用
- **secondWep**: `KindOfWeapon` - 冠军子职业的副武器

### 状态字段
- **lostInvent**: `boolean` - 缓存的物品丢失状态（用于死亡后的物品处理）

## 构造函数

### Belongings(Hero owner)
初始化物品管理系统：
- 设置所有者引用
- 创建新的背包实例并设置所有者

## 装备访问器方法

这些方法考虑了 `LostInventory` buff 的影响，在物品丢失状态下返回 null：

- **attackingWeapon()**: 返回实际用于攻击的武器（可能是投掷武器或技能武器）
- **weapon()**: 返回主武器
- **armor()**: 返回盔甲
- **artifact()**: 返回遗物
- **misc()**: 返回杂项装备
- **ring()**: 返回戒指
- **secondWep()**: 返回副武器

## 存储和加载方法

### storeInBundle(Bundle bundle)
将所有物品状态序列化到 Bundle 中。

### restoreFromBundle(Bundle bundle)
从 Bundle 中恢复所有物品状态，并激活装备。

### clear()
清空所有物品和装备。

### preview(GamesInProgress.Info info, Bundle bundle)
预览存档中的盔甲信息。

## 物品查询方法

### getBags()
获取所有袋子列表（包括主背包和其他袋子）。

### getItem(Class<T> itemClass)
获取指定类型的第一个物品实例。

### getAllItems(Class<T> itemClass)
获取指定类型的所有物品实例。

### contains(Item contains)
检查是否包含指定的物品。

### getSimilar(Item similar)
获取与指定物品相似的第一个物品。

### getAllSimilar(Item similar)
获取与指定物品相似的所有物品。

### identify()
识别所有物品（用于游戏结束时的统计）。

## 观察和净化方法

### observe()
观察所有装备，触发识别和成就验证。

### uncurseEquipped()
移除所有已装备物品的诅咒。

## 实用方法

### randomUnequipped()
随机选择一个未装备的物品（来自背包）。

### charge(float charge)
为所有法杖充能器提供充能。

## 迭代器实现

### ItemIterator
自定义迭代器实现，按以下顺序遍历物品：
1. 装备物品（武器、盔甲、遗物、杂项、戒指、副武器）
2. 背包中的物品

支持标准的 `Iterator` 操作，包括 `remove()` 方法。

## 使用示例

```java
// 获取英雄的物品系统
Belongings belongings = hero.belongings;

// 检查是否有特定物品
if (belongings.getItem(PotionOfHealing.class) != null) {
    // 使用治疗药水
}

// 遍历所有物品
for (Item item : belongings) {
    if (item instanceof Weapon) {
        // 处理武器
    }
}

// 获取所有戒指
ArrayList<Ring> rings = belongings.getAllItems(Ring.class);
```

## 注意事项

1. **物品丢失处理**：在英雄死亡或特殊情况下，某些物品可能会暂时不可用，访问器方法会自动处理这种情况
2. **性能考虑**：`getItem()` 和 `getAllItems()` 方法在每次调用时都会遍历所有物品，对于频繁查询建议缓存结果
3. **装备激活**：从 Bundle 恢复时会自动激活装备，确保所有效果正常工作
4. **迭代顺序**：装备物品总是先于背包物品被遍历，这对于某些需要优先处理装备的逻辑很重要
5. **线程安全**：该类不是线程安全的，应在主线程中使用