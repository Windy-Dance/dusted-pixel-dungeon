# CheckBox 类

## 概述
`CheckBox` 是 Shattered Pixel Dungeon 中的复选框UI组件，继承自 `RedButton` 类。它提供了一个带有文本标签和选中/未选中图标的交互式按钮，用户点击时会在两种状态之间切换。

## 功能特性
- **状态切换**：点击时自动在选中和未选中状态之间切换
- **图标显示**：使用 Icons.UNCHECKED 和 Icons.CHECKED 图标表示当前状态
- **智能布局**：自动调整文本和图标的位置，确保在有限空间内正确显示
- **文本适配**：当文本过长时自动减小字体大小以适应可用空间
- **像素对齐**：使用 PixelScene.align() 确保文本和图标在像素级精确对齐

## 核心方法

### 构造函数
- `CheckBox(String label)` - 创建带有指定文本标签的复选框，默认状态为未选中

### 状态控制
- `checked()` - 获取当前选中状态（true表示选中，false表示未选中）
- `checked(boolean value)` - 设置选中状态，如果状态发生变化会更新图标显示

### 重写方法
- `layout()` - 自定义布局逻辑，处理文本和图标的定位及大小调整
- `onClick()` - 处理点击事件，切换选中状态

## 内部组件
- `checked` - 布尔值，存储当前选中状态
- `text` - 文本标签组件
- `icon` - 状态图标组件

## 使用示例
```java
// 创建复选框
CheckBox checkBox = new CheckBox("启用音效");

// 设置初始状态
checkBox.checked(true);

// 获取当前状态
boolean isEnabled = checkBox.checked();

// 添加到界面
add(checkBox);
```

## 注意事项
- 默认图标大小为16x16像素
- 文本高度和图标高度都会影响整体布局
- 当文本过长导致与图标重叠时，会自动减小字体大小（从默认大小开始递减）
- 图标显示在按钮右侧，文本显示在左侧
- 状态切换时会自动更新图标，无需手动调用刷新方法