# Item.java - 物品基类

## 概述
`Item` 类是所有游戏物品的基类，定义了物品的基本属性、行为和交互机制。所有具体物品（如武器、护甲、药水、卷轴等）都继承自此类。

## 核心属性

### 基本属性
- **image**: 物品在UI中显示的图像ID
- **icon**: 用于随机化图像的标识符
- **stackable**: 是否可堆叠（默认为false）
- **quantity**: 物品数量（对于可堆叠物品）
- **dropsDownHeap**: 拾取时是否产生掉落动画

### 等级和诅咒系统
- **level**: 物品等级（正数表示强化，负数表示降级）
- **levelKnown**: 玩家是否已知物品的真实等级
- **cursed**: 物品是否被诅咒
- **cursedKnown**: 玩家是否已知物品的诅咒状态

### 特殊标志
- **unique**: 是否为唯一物品（死亡后保留）
- **keptThoughLostInvent**: 是否在失去背包时保留
- **bones**: 是否可以包含在英雄遗骸中

## 核心方法

### 拾取和丢弃
- **doPickUp(Hero hero)**: 处理物品拾取逻辑
- **doDrop(Hero hero)**: 处理物品丢弃逻辑
- **collect(Bag container)**: 将物品收集到容器中

### 装备和使用
- **execute(Hero hero, String action)**: 执行物品动作（丢弃、投掷、装备等）
- **actions(Hero hero)**: 返回物品可用的动作列表

### 等级管理
- **upgrade()**: 强化物品等级
- **degrade()**: 降低物品等级
- **identify()**: 识别物品（揭示等级和诅咒状态）

### 投掷系统
- **doThrow(Hero hero)**: 处理物品投掷
- **cast(Hero user, int dst)**: 投掷物品到目标位置
- **onThrow(int cell)**: 物品被投掷后的处理逻辑

### 数量管理
- **split(int amount)**: 分割物品堆栈
- **merge(Item other)**: 合并相似物品
- **detach(Bag container)**: 从容器中分离物品

## 继承关系
- 直接子类包括：
  - `EquipableItem`: 可装备物品基类
  - `KindOfWeapon`: 武器基类  
  - `KindofMisc`: 杂项物品基类
- 具体实现类分布在各个子包中（武器、护甲、戒指、神器、药水、卷轴、魔杖等）

## 使用示例
```java
// 创建物品实例
Item item = new Gold(50);

// 拾取物品
if (item.doPickUp(hero)) {
    // 拾取成功
}

// 投掷物品
item.doThrow(hero);

// 升级物品
item.upgrade();
```

## 注意事项
1. 物品的状态管理通过Bundle系统实现，支持游戏保存和加载
2. 所有物品交互都应通过execute方法进行，而不是直接调用内部方法
3. 物品的视觉效果通过ItemSprite和Emitter系统处理
4. 快捷栏集成通过QuickSlotButton系统自动处理