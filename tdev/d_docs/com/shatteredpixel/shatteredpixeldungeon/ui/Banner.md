# Banner 类

## 概述

`Banner` 类是 Shattered Pixel Dungeon 游戏中的横幅/标语显示组件。它继承自 `Image` 类，提供带有淡入淡出动画效果的图像显示功能，常用于显示游戏中的重要信息、标题或通知。

该组件的核心特性包括：
- **三阶段动画**：淡入（FADE_IN）、静态显示（STATIC）、淡出（FADE_OUT）
- **颜色着色**：支持在淡入阶段应用指定颜色效果
- **灵活时长控制**：可分别设置淡入/淡出时间和静态显示时间
- **自动清理**：动画完成后自动销毁并从场景中移除

## 继承关系

- `com.watabou.noosa.Image`
  - `com.shatteredpixel.shatteredpixeldungeon.ui.Banner`

## 内部枚举：State

定义Banner的三种状态：

- **`FADE_IN`**：淡入阶段，图像逐渐显现并应用颜色着色
- **`STATIC`**：静态显示阶段，图像完全可见且无特殊效果  
- **`FADE_OUT`**：淡出阶段，图像逐渐消失直至完全透明

## 字段

### 状态管理

- **`state: State`**：当前Banner所处的动画状态
- **`time: float`**：当前状态剩余的时间

### 显示配置

- **`color: int`**：淡入阶段应用的颜色值（ARGB格式）
- **`fadeTime: float`**：淡入和淡出阶段的持续时间（秒）
- **`showTime: float`**：静态显示阶段的持续时间（秒）

## 构造函数

### 基于现有图像的构造函数

- **`Banner(Image sample)`**  
  创建Banner实例：
  - 复制传入图像的所有属性
  - 初始透明度设为0（完全透明）

### 基于纹理资源的构造函数

- **`Banner(Object tx)`**  
  直接从纹理资源创建Banner：
  - 使用指定的纹理对象
  - 初始透明度设为0（完全透明）

## 方法

### 显示控制方法

- **`show(int color, float fadeTime, float showTime): void`**  
  启动Banner显示动画：
  - `color`：淡入阶段的颜色着色值
  - `fadeTime`：淡入/淡出持续时间
  - `showTime`：静态显示持续时间

- **`show(int color, float fadeTime): void`**  
  启动永久显示的Banner动画：
  - 静态显示时间为 `Float.MAX_VALUE`（永久显示，直到手动触发淡出）

### 更新方法

- **`update(): void`**  
  每帧更新动画状态：
  
  **淡入阶段（FADE_IN）**：
  - 应用颜色着色：`tint(color, p)`
  - 设置透明度：`alpha(1 - p)`（p为进度比例）
  
  **静态阶段（STATIC）**：
  - 重置颜色效果：`resetColor()`
  - 保持完全不透明
  
  **淡出阶段（FADE_OUT）**：
  - 重置颜色效果：`resetColor()`
  - 设置透明度：`alpha(p)`（逐渐变透明）
  
  **状态转换**：
  - 淡入完成 → 进入静态阶段
  - 静态完成 → 进入淡出阶段  
  - 淡出完成 → 调用 `killAndErase()` 自动销毁

## 使用示例

```java
// 创建一个红色淡入的Banner
Image titleImage = new Image(Assets.Sprites.TITLE);
Banner banner = new Banner(titleImage);
banner.show(0xFFFF0000, 1.0f, 3.0f); // 红色，1秒淡入，3秒显示，1秒淡出

// 创建永久显示的Banner
Banner permanentBanner = new Banner(Assets.Interfaces.NOTICE);
permanentBanner.show(0xFFFFFFFF, 0.5f); // 白色，0.5秒淡入，永久显示
```

## 技术特点

1. **流畅动画**：基于游戏帧率的平滑渐变效果
2. **内存安全**：自动清理机制避免内存泄漏
3. **灵活配置**：支持各种显示时长组合
4. **视觉层次**：淡入阶段的颜色着色增强视觉冲击力
5. **轻量设计**：代码简洁，专注于核心动画功能

## 使用场景

1. **游戏标题**：主菜单或关卡开始时的标题显示
2. **重要通知**：成就解锁、特殊事件等重要信息提示
3. **章节过渡**：不同游戏章节或区域间的过渡标语
4. **胜利/失败**：游戏结束时的结果显示
5. **加载提示**：长时间操作时的等待提示

## 相关类

- **`Image`**：基础图像组件类，提供渲染和变换功能
- **`Game`**：游戏核心类，提供帧时间（`Game.elapsed`）
- **`Assets`**：资源管理类，提供各种纹理和图像资源

## 注意事项

- Banner一旦开始显示就不能重新配置参数
- 如果需要重复使用，必须创建新的Banner实例
- 颜色参数使用标准ARGB格式（如0xFFRRGGBB）
- 永久显示的Banner需要手动触发淡出（通过再次调用show方法）