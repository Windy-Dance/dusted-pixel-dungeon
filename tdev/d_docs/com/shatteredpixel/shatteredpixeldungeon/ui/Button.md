# Button 类

## 概述
`Button` 是 Shattered Pixel Dungeon 中所有按钮组件的基类。它继承自 `com.watabou.noosa.ui.Component`，提供了通用的按钮交互功能，包括点击、长按、悬停提示等。

## 功能特性
- **多指针支持**：支持鼠标左键、右键、中键点击
- **键盘绑定**：可以通过 `keyAction()` 方法绑定键盘快捷键
- **悬停提示**：当鼠标悬停时显示工具提示（Tooltip）
- **长按检测**：默认长按时间为 0.5 秒（`longClick = 0.5f`）
- **互斥点击**：确保同一时间只有一个按钮处于按下状态

## 核心方法

### 事件处理方法
- `onClick()` - 处理左键点击（默认键类型）
- `onRightClick()` - 处理右键点击
- `onMiddleClick()` - 处理中键点击
- `onLongClick()` - 处理长按事件，返回 `true` 表示消耗了长按事件
- `onPointerDown()` - 指针按下时调用
- `onPointerUp()` - 指针抬起时调用

### 配置方法
- `keyAction()` - 返回此按钮绑定的游戏操作（GameAction），用于键盘输入
- `secondaryTooltipAction()` - 当主键操作未绑定时，用于工具提示的备用操作
- `hoverText()` - 返回悬停时显示的文本内容
- `givePointerPriority()` - 给此按钮指针优先级

### 工具提示相关
- `killTooltip()` - 销毁当前显示的工具提示
- `alignTooltip(Tooltip tip)` - 对齐工具提示位置，确保在屏幕可视范围内

## 使用示例
```java
// 创建一个简单的按钮
Button myButton = new Button() {
    @Override
    protected void onClick() {
        // 处理点击逻辑
    }
    
    @Override
    protected String hoverText() {
        return "这是一个按钮";
    }
    
    @Override
    public GameAction keyAction() {
        return SPDAction.MY_ACTION; // 绑定键盘快捷键
    }
};
```

## 注意事项
- 按钮使用静态变量 `pressedButton` 来跟踪当前按下的按钮，确保互斥性
- 工具提示会自动根据是否有键盘绑定来显示相应的按键提示
- 长按事件会触发设备振动（如果启用了振动设置）
- 按钮在销毁时会自动移除键盘监听器和工具提示