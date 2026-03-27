# Tooltip 类

## 概述
`Tooltip` 是 Shattered Pixel Dungeon 中用于显示悬停提示信息的组件。它提供了延迟显示、淡入淡出动画和自动隐藏功能，确保用户体验流畅且不干扰游戏。

## 功能特性
- **延迟显示**：鼠标悬停 0.5 秒后才显示提示
- **淡入效果**：在 0.1 秒内从透明渐变到完全不透明
- **自动隐藏**：当鼠标移开或父组件移动时自动销毁
- **持久性**：如果多个提示连续显示，会在 0.25 秒无活动后才开始淡出
- **位置对齐**：自动调整位置确保提示框在屏幕可视范围内

## 核心方法

### 构造函数
- `Tooltip(Component parent, String msg, int maxWidth)` - 创建工具提示
  - `parent`: 父组件，用于跟踪位置变化
  - `msg`: 显示的消息文本
  - `maxWidth`: 最大宽度限制

### 静态方法
- `resetLastUsedTime()` - 重置上次使用时间，强制下一个提示重新淡入

### 内部方法
- `createChildren()` - 创建背景（Chrome.Type.TOAST_TR_HEAVY）和文本组件
- `update()` - 更新动画状态并检测父组件是否移动
- `layout()` - 布局背景和文本内容

## 内部机制

### 时间控制
- `tooltipAlpha`: 控制透明度的内部变量，范围为 -5 到 1
- `lastUsedTime`: 记录上次提示显示的时间戳
- 使用 `GameMath.gate(0, tooltipAlpha, 1)` 限制透明度在有效范围内

### 销毁条件
当以下任一条件满足时，提示会自动销毁：
- 父组件被移除（`!parent.exists`）
- 父组件不活跃（`!parent.isActive()`）
- 父组件不可见（`!parent.isVisible()`）
- 父组件位置发生变化（任何坐标改变）

## 使用示例
```java
// 在按钮中创建工具提示
@Override
protected void onHoverStart(PointerEvent event) {
    String text = "这是一个工具提示";
    int key = KeyBindings.getFirstKeyForAction(keyAction(), ControllerHandler.controllerActive);
    
    if (key != 0) {
        text += " _(" + KeyBindings.getKeyName(key) + ")_";
    }
    hoverTip = new Tooltip(Button.this, text, 80);
    Button.this.parent.addToFront(hoverTip);
    hoverTip.camera = camera();
    alignTooltip(hoverTip);
}
```

## 注意事项
- 工具提示必须通过 `parent.addToFront()` 添加到父容器的前面
- 需要正确设置相机（`hoverTip.camera = camera()`）以确保正确的渲染
- 文本支持 Markdown 格式，下划线 `_text_` 会以特殊格式显示
- 自动包含键盘绑定提示，如果指定了 `keyAction()`
- 提示框使用 `TOAST_TR_HEAVY` 样式的 Chrome 边框