# StyledButton 类

## 概述
`StyledButton` 是 Shattered Pixel Dungeon 中支持自定义背景、文本和图标的通用样式化按钮组件。它继承自基础 `Button` 类，提供了更丰富的视觉定制选项。

## 功能特性
- **多种 Chrome 样式**：支持任意 `Chrome.Type` 作为背景
- **文本支持**：内置渲染文本块（RenderedTextBlock）支持多行文本
- **图标支持**：可添加任意图像作为图标
- **布局灵活性**：支持左对齐和居中对齐模式
- **尺寸计算**：提供方法计算所需的最小宽高

## 核心构造函数

### 基础构造
```java
public StyledButton(Chrome.Type type, String label)
```
- 使用默认字体大小 9

### 自定义字体大小
```java
public StyledButton(Chrome.Type type, String label, int size)
```
- 允许指定文本渲染大小

## 核心组件

### 视觉元素
- **bg**: 背景九宫格（Chrome），使用指定的 Chrome 类型
- **text**: 渲染文本块（RenderedTextBlock），支持多行和高亮
- **icon**: 可选图标图像（Image）

### 布局控制
- **leftJustify**: 布尔值，控制是否左对齐（默认居中）
- **multiline**: 布尔值，控制是否启用多行文本支持

## 主要方法

### 文本管理
- **text(String value)**: 设置或更新按钮文本
- **text()**: 获取当前文本内容  
- **textColor(int value)**: 设置文本颜色

### 图标管理
- **icon(Image icon)**: 设置或更新按钮图标
- **icon()**: 获取当前图标

### 状态控制
- **enable(boolean value)**: 启用/禁用按钮，同时调整文本和图标的透明度
- **alpha(float value)**: 设置整体透明度

### 尺寸计算
- **reqWidth()**: 计算显示当前内容所需的最小宽度
- **reqHeight()**: 计算显示当前内容所需的最小高度

## 布局逻辑

### 居中布局（默认）
- 文本和图标在按钮内水平居中对齐
- 图标位于左侧，文本位于右侧

### 左对齐布局
- 当 `leftJustify = true` 时启用
- 图标紧贴左侧边距，文本紧随其后
- 适用于需要精确控制位置的场景

### 多行支持
- 当 `multiline = true` 时启用
- 文本宽度限制为 `(width - componentWidth - bg.marginHor() - 2)`
- 自动换行处理

## 交互反馈

### 视觉反馈
- **onPointerDown()**: 背景亮度增加到 1.2x 并播放点击音效
- **onPointerUp()**: 背景颜色重置

### 音效
- 使用 `Assets.Sounds.CLICK` 作为点击音效

## 使用示例
```java
// 创建带图标的居中按钮
Image swordIcon = new Image(Assets.Interfaces.ICONS);
swordIcon.frame(0, 0, 16, 16);
StyledButton attackBtn = new StyledButton(
    Chrome.Type.RED_BUTTON, 
    "攻击", 
    9
);
attackBtn.icon(swordIcon);

// 创建左对齐的多行按钮
StyledButton multiLineBtn = new StyledButton(
    Chrome.Type.GREY_BUTTON,
    "这是一个很长的文本，会自动换行显示",
    7
);
multiLineBtn.leftJustify = true;
multiLineBtn.multiline = true;

// 动态更新内容
attackBtn.text("新的文本");
attackBtn.icon(null); // 移除图标
```

## 注意事项
- 所有子组件都会根据父容器尺寸自动重新布局
- 文本和图标的位置会根据对齐模式动态调整
- 透明度设置会影响所有子组件（背景、文本、图标）
- 多行文本需要显式启用 `multiline` 标志
- 图标和文本的间距固定为 2 像素
- 字体大小会影响按钮的整体尺寸计算