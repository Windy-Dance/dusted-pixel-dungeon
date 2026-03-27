# InventorySlot 类

## 概述
`InventorySlot` 是 Shattered Pixel Dungeon 中用于显示背包物品的专用槽位组件。它继承自 `ItemSlot`，添加了背景色块、装备状态指示和诅咒/未识别状态的视觉反馈。

## 功能特性
- **背景指示**：使用不同颜色的背景指示物品状态
- **装备高亮**：已装备物品显示特殊背景色
- **诅咒警告**：已知诅咒物品显示红色背景
- **未识别提示**：未识别物品显示蓝色或紫色背景  
- **交互反馈**：点击时提供视觉和音效反馈
- **状态同步**：自动与英雄背包状态同步

## 颜色常量

### 背景颜色
- **NORMAL = 0x9953564D**: 普通物品背景（半透明深灰）
- **EQUIPPED = 0x9991938C**: 已装备物品背景（半透明浅灰）

### 状态颜色
- **诅咒**: 红色偏移 (`ra = +0.3f, ga = -0.15f, ba = -0.15f`)
- **未识别**: 
  - 普通未识别: 蓝紫色 (`ra = +0.35f, ba = +0.35f`)
  - 武器/护甲/魔杖已知诅咒: 蓝色 (`ba = +0.3f, ra = -0.1f`)

## 核心方法

### 构造函数
- `InventorySlot(Item item)` - 创建指定物品的库存槽位

### 组件初始化
- **createChildren()**: 
  - 创建背景色块（ColorBlock）
  - 调用父类创建物品槽位

### 布局管理
- **layout()**: 
  - 设置背景尺寸和位置
  - 调用父类布局物品槽位

### 状态更新
- **item(Item item)**: 
  - 更新背景可见性（金币和袋子不显示背景）
  - 检查装备状态并设置相应背景色
  - 处理诅咒和未识别状态的颜色偏移
  - 同步启用状态（背包丢失时禁用）

### 交互反馈
- **onPointerDown()**: 背景亮度增加到 1.5x 并播放点击音效
- **onPointerUp()**: 背景亮度重置到 1.0x

## 装备检测逻辑

### 装备类型检查
- **武器**: `hero.belongings.weapon`
- **护甲**: `hero.belongings.armor`  
- **饰品**: `hero.belongings.artifact`
- **杂项**: `hero.belongings.misc`
- **戒指**: `hero.belongings.ring`
- **副手**: `hero.belongings.secondWep`

### 特殊物品处理
- **金币 (Gold)**: 不显示背景色块
- **袋子 (Bag)**: 不显示背景色块
- **背包丢失**: 禁用不可保留的物品槽位

## 使用示例
```java
// 创建基本库存槽位
InventorySlot slot = new InventorySlot(new HealthPotion());

// 添加到库存面板
inventoryPane.add(slot);

// 库存面板会自动管理所有槽位的状态更新
```

## 注意事项
- 背景色块完全覆盖物品槽位区域
- 诅咒和未识别的颜色效果通过 RGB 偏移实现
- 已装备物品的检测包括所有装备类型
- 背包丢失状态下，只有 `keptThroughLostInventory()` 返回 true 的物品才保持可用
- 所有视觉效果都基于 TextureCache.createSolid() 创建的纯色纹理
- 音效使用 `Assets.Sounds.CLICK` 并带有随机音高变化