# CurrencyIndicator 类

## 概述
`CurrencyIndicator` 是 Shattered Pixel Dungeon 中的货币指示器UI组件，继承自 `com.watabou.noosa.ui.Component`。它用于显示玩家当前拥有的金币（Gold）和能量（Energy）数量，当数量发生变化时会以淡入淡出的动画效果短暂显示更新。

## 功能特性
- **双货币显示**：同时显示金币（黄色）和能量（青蓝色）两种货币数量
- **变化检测**：自动监听 Dungeon.gold 和 Dungeon.energy 的变化
- **淡入淡出动画**：货币数量变化时显示2秒钟的动画效果，前1秒完全不透明，后1秒逐渐淡出
- **智能布局**：根据显示内容自动调整金币和能量文本的垂直位置
- **持久显示模式**：通过 showGold 静态变量可以强制持续显示金币数量

## 核心方法

### 重写方法
- `createChildren()` - 创建并初始化金币和能量的文本显示组件
- `layout()` - 布局文本组件的位置，处理单/双货币显示的不同情况
- `update()` - 监听货币变化、控制显示状态和动画效果

## 内部组件
- `gold` - 显示金币数量的 BitmapText 组件，颜色为 0xFFFF00（黄色）
- `energy` - 显示能量数量的 BitmapText 组件，颜色为 0x44CCFF（青蓝色）
- `lastGold` - 记录上一次的金币数量，用于检测变化
- `lastEnergy` - 记录上一次的能量数量，用于检测变化
- `goldTime` - 金币显示剩余时间计时器
- `energyTime` - 能量显示剩余时间计时器
- `TIME` - 显示持续时间常量，固定为2秒
- `showGold` - 静态布尔值，控制是否强制持续显示金币

## 使用示例
```java
// 创建货币指示器
CurrencyIndicator currencyIndicator = new CurrencyIndicator();

// 添加到界面
add(currencyIndicator);

// 强制持续显示金币（例如在商店界面）
CurrencyIndicator.showGold = true;
```

## 注意事项
- 文本使用 PixelScene.pixelFont 字体
- 金币显示在上方，能量显示在下方（如果两者都可见）
- 如果只有能量可见，能量会显示在金币的位置
- 通过 Dungeon.gold 和 Dungeon.energy 全局变量获取当前货币数量
- 动画使用 alpha 透明度控制，范围从1.0（完全不透明）到0.0（完全透明）
- 当 showGold 为 true 时，金币会持续显示且保持半透明状态（alpha = 1.0）