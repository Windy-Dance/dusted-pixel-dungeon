# RenderedTextBlock 类

## 概述
`RenderedTextBlock` 是 Shattered Pixel Dungeon 中用于渲染和管理文本块的组件。它支持多行文本、高亮标记、对齐方式和动态布局，是游戏中所有复杂文本显示的基础。

## 功能特性
- **多行支持**：自动处理文本换行和多行布局
- **高亮标记**：支持使用下划线 `_text_` 或双星号 `**text**` 标记高亮文本
- **对齐方式**：支持左对齐、居中对齐和右对齐
- **动态布局**：根据内容自动调整尺寸和位置
- **字体缩放**：支持动态字体缩放
- **颜色管理**：支持整体着色和高亮着色

## 核心属性

### 文本管理
- **text**: 当前显示的完整文本
- **tokens**: 文本分词后的数组（包含空格和换行符）
- **words**: 渲染的文本单词列表（RenderedText 对象）
- **multiline**: 是否启用多行模式

### 尺寸控制
- **maxWidth**: 最大宽度限制（默认 Integer.MAX_VALUE）
- **nLines**: 实际行数
- **size**: 字体大小
- **zoom**: 字体缩放比例

### 颜色和高亮
- **color**: 整体文本颜色（-1 表示默认）
- **hightlightColor**: 高亮文本颜色（默认 TITLE_COLOR）
- **highlightingEnabled**: 是否启用高亮功能

### 对齐方式
- **LEFT_ALIGN = 1**: 左对齐（默认）
- **CENTER_ALIGN = 2**: 居中对齐  
- **RIGHT_ALIGN = 3**: 右对齐
- **alignment**: 当前对齐方式

## 核心方法

### 构造函数
- `RenderedTextBlock(int size)`: 创建指定字体大小的文本块
- `RenderedTextBlock(String text, int epsize)`: 创建并设置初始文本

### 文本设置
- **text(String text)**: 设置单行文本
- **text(String text, int maxWidth)**: 设置多行文本并指定最大宽度
- **tokens(String... words)**: 手动设置文本分词（假设有空格分隔）

### 样式控制
- **maxWidth(int maxWidth)**: 动态设置最大宽度
- **zoom(float zoom)**: 设置字体缩放
- **hardlight(int color)**: 设置整体文本颜色
- **resetColor()**: 重置为默认颜色
- **alpha(float value)**: 设置透明度
- **setHightlighting(boolean enabled)**: 启用/禁用高亮
- **invert()**: 反转文本颜色（用于特殊效果）

### 布局控制
- **align(int align)**: 设置文本对齐方式
- **layout()**: 重新布局所有文本单词

## 内部机制

### 文本分词
- 使用 `Game.platform.splitforTextBlock()` 进行平台特定的文本分词
- 支持中文、日文等无空格分词的语言
- 保留空格和换行符作为独立的 token

### 高亮解析
- 下划线 `_` 或双星号 `**` 用于切换高亮状态
- 高亮符号本身不渲染到屏幕上
- 高亮颜色可通过 `setHightlighting()` 自定义

### 多行布局
- 根据 `maxWidth` 自动计算换行位置
- 支持手动换行符 `\n`
- 每行高度基于最高单词的高度计算
- 行间距固定为 2 像素

### 对齐处理
- 居中和右对齐在布局完成后进行位置调整
- 每行独立计算对齐位置
- 支持混合对齐（不同行可以有不同的对齐方式）

## 使用示例
```java
// 创建基本文本块
RenderedTextBlock textBlock = new RenderedTextBlock("Hello World", 9);

// 创建多行文本块
RenderedTextBlock multiLine = new RenderedTextBlock("这是一段很长的文本\n会自动换行显示", 8);
multiLine.maxWidth(200); // 设置最大宽度

// 启用高亮并设置高亮颜色
textBlock.setHightlighting(true, 0xFF0000); // 红色高亮
textBlock.text("普通文本 _高亮文本_ 继续普通");

// 设置居中对齐
textBlock.align(RenderedTextBlock.CENTER_ALIGN);

// 动态调整字体大小
textBlock.zoom(1.5f);
```

## 注意事项
- 文本块继承自 `Component`，需要添加到场景树中才能显示
- 所有样式更改后都需要调用 `layout()` 或通过父容器的布局触发重新布局
- 中文和日文文本会逐字符渲染（无空格分隔）
- 高亮标记必须成对出现，否则可能导致意外的高亮效果
- 最大宽度设置会触发多行模式自动启用
- 字体缩放会影响文本的精确对齐，建议使用 `PixelScene.align()`