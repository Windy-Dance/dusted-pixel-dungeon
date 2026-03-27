# ItemButton 类

## 概述
`ItemButton` 是 Shattered Pixel Dungeon 中基于红色按钮样式的物品显示组件。它本质上是 `RedButton` 和 `ItemSlot` 的组合，提供带有物品显示的可交互按钮。

## 功能特性
- **物品集成**：内置 `ItemSlot` 组件显示物品
- **按钮样式**：使用红色按钮 Chrome 样式（Chrome.Type.RED_BUTTON）
- **交互反馈**：点击时提供视觉和音效反馈
- **事件处理**：支持点击和长按事件
- **状态管理**：可通过 `enable()` 方法控制启用状态

## 核心组件

### 主要元素
- **bg**: 红色按钮背景（Chrome.Type.RED_BUTTON）
- **slot**: 物品槽位（ItemSlot），负责物品显示

### 交互反馈
- **点击效果**: 背景亮度增加到 1.2x 并播放点击音效
- **音效**: 使用 `Assets.Sounds.CLICK`

## 核心方法

### 构造和初始化
- **createChildren()**: 
  - 创建红色按钮背景
  - 创建物品槽位并启用交互
  - 设置点击和长按事件处理器

### 物品管理
- **item(Item item)**: 设置当前显示的物品
- **item()**: 获取当前显示的物品
- **clear()**: 清空物品槽位

### 交互处理
- **onClick()**: 空实现，子类需重写处理点击逻辑
- **onLongClick()**: 返回 false，默认不处理长按

### 布局管理
- **layout()**: 
  - 设置背景尺寸和位置
  - 设置物品槽位填充区域
  - 根据按钮大小调整边距（大按钮：2像素，小按钮：1像素）

## 使用示例
```java
// 创建基本物品按钮
ItemButton itemBtn = new ItemButton(new HealthPotion()) {
    @Override
    protected void onClick() {
        // 处理点击逻辑
        useItem();
    }
    
    @Override
    protected boolean onLongClick() {
        // 处理长按逻辑
        assignToQuickSlot();
        return true;
    }
};

// 动态设置物品
itemBtn.item(new Sword());

// 清空物品
itemBtn.clear();

// 获取当前物品
Item current = itemBtn.item();
```

## 注意事项
- 必须重写 `onClick()` 方法以提供实际功能
- `onLongClick()` 默认返回 false，如需长按功能需重写并返回 true
- 物品槽位默认启用所有文本信息显示
- 边距自动根据按钮尺寸调整（>=24px 使用 2px 边距，否则 1px）
- 所有交互反馈都继承自基础按钮实现
- 物品显示完全由内部 `ItemSlot` 组件处理