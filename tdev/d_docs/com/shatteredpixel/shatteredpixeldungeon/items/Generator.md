# Generator.java - 物品生成器

## 概述
`Generator` 类是Shattered Pixel Dungeon中所有物品的生成核心，负责管理物品的随机生成、概率系统和游戏进度相关的物品分布。它使用复杂的概率卡牌系统确保物品生成的多样性和平衡性。

## 核心机制

### 分类系统 (Category)
物品被分为多个类别，每个类别有独立的概率控制：
- **TRINKET**: 饰品（17种）
- **WEAPON/MISSILE**: 武器系统（近战/远程，分5个等级）
- **ARMOR**: 护甲（11种）
- **WAND**: 魔杖（13种）
- **RING**: 戒指（12种）
- **ARTIFACT**: 神器（13种）
- **POTION/SCROLL**: 药水/卷轴（各12种）
- **SEED/STONE**: 种子/符石（各12种）
- **FOOD**: 食物（3种）
- **GOLD**: 金币

### 卡牌抽牌系统
- **Deck-based Generation**: 使用类似卡牌抽牌的机制
- **概率递减**: 每次生成物品后，该物品的概率会降低
- **重置机制**: 当所有物品概率为0时，重置整个卡组
- **双卡组设计**: 
  - 主卡组：包含戒指和额外护甲
  - 副卡组：包含神器和额外投掷武器
  - 游戏开始时随机选择其中一个

### 游戏进度适配
- **Floor Sets**: 将地牢分为5个等级（每5层一组）
- **Tiered Items**: 武器、护甲、投掷武器按等级分布
  - T1: 1-5层（基础装备）
  - T2: 6-10层（中级装备）
  - T3: 11-15层（高级装备）
  - T4: 16-20层（精英装备）
  - T5: 21-25层（顶级装备）
- **概率分布**: 使用预定义的楼层概率矩阵确保难度曲线

## 特殊物品处理

### 独特物品
- **Artifacts**: 神器在整个游戏过程中不会重复生成
- **Trinkets**: 饰品虽然独特但可以替换（与神器不同）

### 变异系统
- **Exotic Items**: 支持普通药水/卷轴变异为特殊版本
- **变异概率**: 通过ExoticCrystals控制变异几率

### 保证掉落
- **PotionOfStrength**: 每章保证掉落2瓶力量药水
- **ScrollOfUpgrade**: 每章保证掉落3卷升级卷轴
- **StoneOfEnchantment**: 6-19层保证掉落1个附魔符石

## 核心方法

### 随机生成
- **random()**: 生成任意随机物品
- **random(Category cat)**: 生成指定类别的随机物品
- **randomUsingDefaults()**: 使用默认概率（忽略卡组状态）
- **random(Class<? extends Item> cl)**: 生成指定类型的具体物品

### 专用生成器
- **randomArmor()/randomWeapon()/randomMissile()**: 按等级生成对应装备
- **randomArtifact()**: 生成唯一神器（确保不重复）

### 状态管理
- **fullReset()**: 完全重置所有生成器状态
- **generalReset()**: 重置主类别概率
- **reset(Category cat)**: 重置特定类别
- **undoDrop()**: 撤销物品生成（将物品放回卡组）

## 数据持久化

### Bundle系统集成
- **storeInBundle()**: 保存当前生成器状态到游戏存档
- **restoreFromBundle()**: 从存档恢复生成器状态
- **种子管理**: 使用Random.Long()种子确保可重现性
- **向后兼容**: 处理不同游戏版本间的物品类别变化

## 使用示例
```java
// 生成随机物品
Item randomItem = Generator.random();

// 生成当前深度对应的随机武器
MeleeWeapon weapon = Generator.randomWeapon();

// 生成特定等级的护甲
Armor armor = Generator.randomArmor(2); // T3护甲

// 强制生成普通卷轴（避免变异）
Scroll scroll = (Scroll) Generator.randomUsingDefaults(Scroll.class);
```

## 注意事项
1. **神器唯一性**: randomArtifact()在无可用神器时返回null，并自动尝试生成戒指
2. **种子一致性**: 同一游戏会话中的物品生成具有确定性
3. **性能优化**: 使用Reflection.newInstance()进行高效实例化
4. **平衡性**: 所有概率经过精心调整以确保游戏体验
5. **扩展性**: 易于添加新物品类型和调整现有概率