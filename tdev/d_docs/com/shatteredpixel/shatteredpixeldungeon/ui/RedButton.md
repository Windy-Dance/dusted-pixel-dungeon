# RedButton 类

## 概述
`RedButton` 是 Shattered Pixel Dungeon 中最常用的按钮组件。它继承自 `StyledButton`，使用红色按钮 Chrome 样式（Chrome.Type.RED_BUTTON）作为默认外观。

## 功能特性
- **标准红色外观**：使用游戏标准的红色按钮样式
- **文本支持**：内置渲染文本块，支持多行和高亮
- **图标支持**：可添加任意图像作为图标
- **交互反馈**：点击时提供视觉和音效反馈
- **完全继承**：包含 `StyledButton` 的所有功能

## 核心构造函数

### 基础构造
```java
public RedButton(String label)
```
- 使用默认字体大小 9
- 创建标准红色按钮

### 自定义字体大小
```java  
public RedButton(String label, int size)
```
- 允许指定文本渲染大小
- 适用于需要不同字体大小的场景

## 继承功能

### 视觉元素
- **bg**: 红色按钮背景（Chrome.Type.RED_BUTTON）
- **text**: 渲染文本块，支持多行文本
- **icon**: 可选图标图像

### 交互反馈
- **onPointerDown()**: 背景亮度增加到 1.2x 并播放点击音效
- **onPointerUp()**: 背景颜色重置
- **enable(boolean value)**: 启用/禁用按钮状态

### 布局控制
- **leftJustify**: 控制是否左对齐（默认居中）
- **multiline**: 控制是否启用多行文本支持

### 尺寸计算
- **reqWidth()**: 计算显示当前内容所需的最小宽度
- **reqHeight()**: 计算显示当前内容所需的最小高度

## 使用示例
```java
// 创建基本的红色按钮
RedButton confirmBtn = new RedButton("确认");

// 创建带图标的红色按钮
Image acceptIcon = Icons.get(Icons.CHECKED);
RedButton acceptBtn = new RedButton("接受");
acceptBtn.icon(acceptIcon);

// 创建小字体的红色按钮
RedButton smallBtn = new RedButton("取消", 7);

// 创建左对齐的红色按钮
RedButton leftBtn = new RedButton("选项");
leftBtn.leftJustify = true;

// 动态更新按钮文本
confirmBtn.text("新的文本");
```

## 注意事项
- 红色按钮是游戏中最常用的交互元素
- 所有视觉和交互逻辑都继承自 `StyledButton`
- 字体大小会影响按钮的整体尺寸
- 图标和文本会自动居中对齐
- 点击音效使用 `Assets.Sounds.CLICK`
- 按钮状态（启用/禁用）会影响文本和图标的透明度