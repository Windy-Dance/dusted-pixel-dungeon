# Tag 类

## 概述
`Tag` 是 Shattered Pixel Dungeon 中用于显示圆形标签按钮的基础组件。它通常用作状态指示器或快捷操作按钮，具有闪烁效果和自定义颜色支持。

## 功能特性
- **圆形设计**：使用 `Chrome.Type.TAG` 九宫格样式创建圆形外观
- **闪烁效果**：支持高亮闪烁动画，用于吸引玩家注意力
- **颜色自定义**：可设置任意 RGB 颜色
- **翻转支持**：支持水平翻转以适应不同布局需求
- **继承基础按钮功能**：包含所有标准按钮交互（点击、悬停等）

## 核心方法

### 构造函数
- `Tag(int color)` - 创建指定颜色的标签
  - 颜色参数为标准 RGB 整数值（如 `0xFF0000` 表示红色）

### 状态控制
- `flash()` - 触发闪烁效果，使标签高亮显示
- `flip(boolean value)` - 设置标签是否水平翻转
- `setColor(int color)` - 动态更改标签颜色

### 事件处理
- `onClick()` - 重写点击事件，默认清除 `GameScene.tagDisappeared` 标志

### 更新逻辑
- `update()` - 处理闪烁动画效果：
  - 使用正弦波控制亮度变化
  - 闪烁完成后自动恢复原始颜色

## 内部属性

### 样式常量
- **SIZE = 24**: 标签的标准尺寸（24x24 像素）
- **lightness**: 当前闪烁亮度值（0-1 范围）

### 颜色管理
- **r, g, b**: 分别存储红、绿、蓝分量（0-1 浮点数）
- **flipped**: 跟踪是否启用了水平翻转

## 继承关系
- 继承自 `Button` 类，包含所有基础按钮功能
- 重写部分方法以适配标签的特殊需求

## 使用示例
```java
// 创建红色标签
Tag redTag = new Tag(0xFF0000);

// 创建并立即触发闪烁
Tag importantTag = new Tag(0x00FF00);
importantTag.flash();

// 更改标签颜色
redTag.setColor(0x0000FF); // 改为蓝色

// 启用翻转（用于右侧对齐布局）
Tag rightAlignedTag = new Tag(0xFFFF00);
rightAlignedTag.flip(true);
```

## 注意事项
- 标签闪烁效果持续约 1 秒（基于 `Game.elapsed` 计算）
- 颜色值会自动转换为 0-1 范围的浮点数进行内部处理
- 翻转功能通过 `bg.flipHorizontal(value)` 实现
- 点击事件默认清除场景的标签消失标志，子类可重写此行为
- 所有视觉更新都在 `update()` 方法中处理，确保动画流畅性