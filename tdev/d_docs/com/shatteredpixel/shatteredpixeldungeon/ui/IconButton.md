# IconButton 类

## 概述
`IconButton` 是 `Button` 的子类，专门用于显示图标的按钮。它在基础按钮功能上添加了图标支持，并提供了视觉反馈（点击时图标变亮）和声音反馈。

## 功能特性
- 继承了 `Button` 的所有交互功能
- 支持设置和显示图标图像
- 点击时图标亮度增加到 1.5x 并播放点击音效
- 可以通过 `enable()` 方法控制按钮的启用/禁用状态
- 图标会根据按钮状态自动调整透明度

## 核心方法

### 构造函数
- `IconButton()` - 创建空的图标按钮
- `IconButton(Image icon)` - 创建带有指定图标的按钮

### 图标管理
- `icon(Image icon)` - 设置按钮图标，会替换现有图标
- `icon()` - 获取当前设置的图标

### 状态控制
- `enable(boolean value)` - 启用或禁用按钮，同时调整图标透明度（启用时为 1.0，禁用时为 0.3）

### 事件处理
- `onPointerDown()` - 按下时增加图标亮度并播放音效
- `onPointerUp()` - 抬起时重置图标颜色

## 使用示例
```java
// 创建一个带有图标的按钮
Image myIcon = Icons.get(Icons.EXIT);
IconButton exitButton = new IconButton(myIcon) {
    @Override
    protected void onClick() {
        // 处理退出逻辑
    }
};

// 动态更改图标
exitButton.icon(Icons.get(Icons.CLOSE));

// 禁用按钮
exitButton.enable(false);
```

## 注意事项
- 图标会在布局时自动居中对齐
- 按钮启用状态会影响图标的透明度，但不会影响点击事件的触发
- 音效使用 `Assets.Sounds.CLICK`，确保资源文件存在
- 移除图标时会自动从组件树中移除旧图标