# OptionSlider 类

## 概述
`OptionSlider` 是 Shattered Pixel Dungeon 中用于选择数值范围的滑块组件。它提供直观的滑动交互，支持最小值、最大值和当前值的显示，并可自定义标签文本。

## 功能特性
- **数值范围**：支持整数范围选择（内部使用 int，但可表示其他数值类型）
- **视觉反馈**：包含标题、最小/最大值标签和滑动节点
- **刻度标记**：显示数值刻度线，便于精确选择
- **交互控制**：支持鼠标拖拽和点击定位
- **状态管理**：可启用/禁用滑块状态

## 核心构造函数
```java
public OptionSlider(String title, String minTxt, String maxTxt, int minVal, int maxVal)
```
- **title**: 滑块标题文本
- **minTxt**: 最小值显示文本  
- **maxTxt**: 最大值显示文本
- **minVal**: 最小数值（包含）
- **maxVal**: 最大数值（包含）

## 抽象方法

### 必须实现
- **onChange()**: 当滑块值改变时调用
  - 子类必须重写此方法以处理值变化逻辑

## 核心方法

### 值管理
- **getSelectedValue()**: 获取当前选中的数值
- **setSelectedValue(int val)**: 设置当前选中的数值
- **enable(boolean value)**: 启用/禁用滑块（影响透明度和交互）

### 视觉反馈
- **布局计算**: 自动计算滑块轨道长度和刻度间距
- **节点定位**: 精确对齐滑动节点到最近的刻度位置

## 内部组件

### 主要元素
- **title**: 标题文本（RenderedTextBlock）
- **minTxt/maxTxt**: 最小/最大值标签文本
- **sliderNode**: 滑动节点（红色按钮 Chrome 样式）
- **BG**: 背景（半透明红色按钮样式）
- **sliderBG**: 滑块轨道（深灰色 ColorBlock）
- **sliderTicks[]**: 刻度线数组（每个数值一个）

### 交互区域
- **pointerArea**: 指针交互区域（PointerArea）
  - 处理鼠标按下、释放和拖拽事件
  - 实现平滑的滑动体验

## 交互逻辑

### 拖拽操作
- **按下**: 滑动节点高亮（亮度 1.5x）
- **拖拽**: 节点跟随鼠标移动，限制在轨道范围内
- **释放**: 自动对齐到最近的刻度位置并触发 `onChange()`

### 点击定位
- 直接点击轨道任意位置可快速跳转到对应数值
- 释放后自动对齐并触发变更事件

## 视觉设计

### 尺寸计算
- **轨道长度**: `width - 5` 像素
- **刻度间距**: `轨道长度 / (maxVal - minVal)`
- **节点尺寸**: 4x7 像素
- **刻度线**: 1x9 像素

### 颜色方案
- **背景**: 半透明红色 (`alpha(0.5f)`)
- **轨道**: 深灰色 (`0xFF222222`)
- **刻度线**: 深灰色
- **节点**: 红色按钮样式

### 文本处理
- 标题文本如果过宽会自动缩小字体
- 最小/最大值文本固定使用 6 号字体
- 所有文本都居中对齐

## 使用示例
```java
// 创建音量滑块
OptionSlider volumeSlider = new OptionSlider("音量", "静音", "最大", 0, 100) {
    @Override
    protected void onChange() {
        float volume = getSelectedValue() / 100f;
        SPDSettings.volume(volume);
    }
};

// 设置初始值
volumeSlider.setSelectedValue(75);

// 禁用滑块（例如在特定设置下）
volumeSlider.enable(false);
```

## 注意事项
- 构造函数会验证参数有效性（minVal <= maxVal）
- 所有坐标计算都考虑了像素对齐（PixelScene.align）
- 滑块值始终为整数，如需浮点值需要外部转换
- 交互区域覆盖整个组件区域，确保良好的用户体验
- 禁用状态下所有文本和节点透明度降低到 0.3f