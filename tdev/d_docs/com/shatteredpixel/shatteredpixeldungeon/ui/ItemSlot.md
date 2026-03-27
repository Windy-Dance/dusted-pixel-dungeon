# ItemSlot 类

## 概述
`ItemSlot` 是 Shattered Pixel Dungeon 中用于显示物品的基础 UI 组件。它整合了物品图标、状态文本、额外信息和等级指示器，支持各种特殊物品类型的自定义显示。

## 功能特性
- **物品显示**：自动渲染物品精灵（ItemSprite）
- **状态指示**：显示物品数量、充能等状态信息  
- **等级显示**：显示物品升级/降级状态和强化效果
- **强度要求**：显示武器/护甲的强度要求和警告
- **图标支持**：为特定物品显示额外的小图标
- **交互反馈**：支持启用/禁用状态和透明度控制

## 颜色常量

### 状态颜色
- **DEGRADED = 0xFF4444**: 降级红色
- **UPGRADED = 0x44FF44**: 升级绿色  
- **FADED = 0x999999**: 褪色灰色
- **WARNING = 0xFF8800**: 警告橙色
- **ENHANCED = 0x3399FF**: 强化蓝色
- **MASTERED = 0xFFFF44**: 精通黄色
- **CURSE_INFUSED = 0x8800FF**: 诅咒注入紫色

### 交互状态
- **ENABLED = 1.0f**: 启用状态透明度
- **DISABLED = 0.3f**: 禁用状态透明度

## 特殊虚拟物品

### 预定义常量
- **CHEST**: 普通宝箱图标
- **LOCKED_CHEST**: 上锁宝箱图标  
- **CRYSTAL_CHEST**: 水晶宝箱图标
- **TOMB**: 墓穴图标
- **SKELETON**: 骨骼图标
- **REMAINS**: 遗骸图标

这些虚拟物品用于表示特殊堆叠类型，避免创建实际物品实例。

## 核心方法

### 构造函数
- `ItemSlot()`: 创建空的物品槽
- `ItemSlot(Item item)`: 创建并设置指定物品的槽位

### 物品管理
- **item(Item item)**: 设置当前显示的物品
- **clear()**: 清空物品槽，显示问号图标
- **updateText()**: 更新所有文本标签（状态、额外信息、等级）

### 状态控制
- **enable(boolean value)**: 启用/禁用物品槽
- **alpha(float value)**: 设置整体透明度
- **showExtraInfo(boolean show)**: 控制额外信息文本的显示
- **textVisible(boolean visible)**: 控制所有文本的可见性

### 布局管理
- **setMargins(int left, int top, int right, int bottom)**: 设置物品精灵的边距

### 交互处理
- **hoverText()**: 返回悬停提示文本（物品名称）

## 视觉组件

### 主要元素
- **sprite**: 物品精灵（ItemSprite）
- **status**: 状态文本（数量、充能等）
- **extra**: 额外信息文本（强度要求、图标文本等）
- **itemIcon**: 额外小图标（物品特有图标）
- **level**: 等级文本（升级/降级数值）

### 文本处理

#### 状态文本
- 显示物品的 `status()` 方法返回值
- 投掷武器在最后使用时显示橙色警告

#### 额外信息
- 武器/护甲显示强度要求
- 红色表示强度不足，黄色表示精通加成
- 未知强度显示 "?" 并使用警告颜色

#### 等级文本  
- 显示可视升级数值 (`visiblyUpgraded()`)
- 不同强化效果使用不同颜色：
  - 普通升级：绿色
  - 降级：红色  
  - 诅咒注入：紫色
  - 强化效果：蓝色
  - 负面强化：橙色

## 使用示例
```java
// 创建基本物品槽
ItemSlot itemSlot = new ItemSlot(new HealthPotion());

// 创建空槽位并后续设置物品
ItemSlot emptySlot = new ItemSlot();
emptySlot.item(new Sword());

// 隐藏额外信息（如强度要求）
itemSlot.showExtraInfo(false);

// 设置边距（用于紧凑布局）
itemSlot.setMargins(2, 2, 2, 2);

// 禁用物品槽（例如背包丢失时）
itemSlot.enable(false);
```

## 注意事项
- 物品槽继承自 `Button`，但默认不处理点击事件
- 所有文本都使用 `PixelScene.pixelFont` 字体
- 强度要求仅对武器和护甲类物品显示
- 图标仅对已识别的物品或已知戒指显示
- 透明度设置会影响所有子组件（精灵、文本、图标）
- 边距设置会影响物品精灵的定位，但不影响整体槽位尺寸
- 物品状态更新会自动触发文本重新计算