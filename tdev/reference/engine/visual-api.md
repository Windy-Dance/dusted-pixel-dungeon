# Visual API 参考

## 类声明
```java
public class Visual extends Gizmo
```

## 类职责
Visual是Gizmo的直接子类，为Noosa引擎中的可视对象提供了完整的视觉属性支持。它包含了位置、尺寸、缩放、旋转、颜色变换等所有必要的视觉特性，并实现了基于物理的运动系统（速度、加速度）和高效的矩阵变换缓存机制。Visual是构建所有具体可视组件（如图片、文本、UI元素）的基础类。

## 关键字段
| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| x | float | public | 构造参数 | 对象在父容器中的X坐标 |
| y | float | public | 构造参数 | 对象在父容器中的Y坐标 |
| width | float | public | 构造参数 | 对象的基础宽度 |
| height | float | public | 构造参数 | 对象的基础高度 |
| scale | PointF | public | (1, 1) | 缩放因子，X和Y方向独立控制 |
| origin | PointF | public | (0, 0) | 变换原点，用于旋转和缩放的基准点 |
| matrix | float[] | protected | 16元素数组 | 4x4变换矩阵，用于OpenGL渲染 |
| rm/gm/bm/am | float | public | 1 | 颜色乘数（红/绿/蓝/透明度） |
| ra/ga/ba/aa | float | public | 0 | 颜色加数（红/绿/蓝/透明度） |
| speed | PointF | public | (0, 0) | 当前速度，用于物理运动 |
| acc | PointF | public | (0, 0) | 加速度，用于物理运动 |
| angle | float | public | 0 | 旋转角度（弧度） |
| angularSpeed | float | public | 0 | 角速度，用于旋转动画 |

## 生命周期
Visual继承了Gizmo的所有生命周期状态（exists, alive, active, visible），并在此基础上添加了视觉相关的状态管理：
- **visible**: 在Visual中被重写，增加了相机边界检测和屏幕可见性判断
- **matrix缓存**: 内部维护lastX, lastY, lastW, lastH, lastA等变量，用于避免不必要的矩阵重新计算

## 核心方法

### 基础变换方法
- **point() / point(PointF/Point)**: 获取或设置位置坐标
- **center() / center(PointF/Visual)**: 获取或设置中心点位置，支持相对于其他Visual居中
- **originToCenter()**: 将变换原点设置为对象中心，便于围绕中心旋转缩放
- **width() / height()**: 获取实际显示尺寸（考虑缩放后的尺寸）

### 运动系统
- **updateMotion()**: 更新基于物理的位置和旋转，根据速度、加速度和角速度计算新位置
- **update()**: 调用updateMotion()进行运动更新

### 矩阵管理
- **updateMatrix()**: 重新计算变换矩阵，包含平移、旋转、缩放操作
- **draw()**: 检查属性变化并调用updateMatrix()，实现高效的矩阵缓存

### 颜色和视觉效果
- **alpha()**: 设置/获取透明度
- **invert()**: 反转颜色（负片效果）
- **lightness()**: 调整亮度（0-1范围）
- **brightness()**: 设置亮度乘数
- **tint()**: 添加色调（支持RGB/强度或颜色值参数）
- **color()**: 设置纯色（覆盖原有颜色）
- **hardlight()**: 设置硬光效果颜色
- **resetColor()**: 重置所有颜色变换为默认值

### 碰撞和可见性检测
- **overlapsPoint()**: 检查是否包含指定的世界坐标点
- **overlapsScreenPoint()**: 检查是否包含指定的屏幕坐标点
- **isVisible()**: 重写的可见性检测，考虑相机边界和屏幕坐标

## 使用示例
```java
// 创建Visual对象
Visual visual = new Visual(100, 100, 50, 50);

// 基本变换
visual.x = 200;                    // 移动位置
visual.scale.set(2, 2);            // 缩放2倍
visual.angle = (float)Math.PI/4;   // 旋转45度
visual.originToCenter();           // 围绕中心旋转

// 颜色效果
visual.alpha(0.5f);                // 半透明
visual.tint(1, 0, 0, 0.3f);        // 添加红色色调
visual.brightness(0.8f);           // 降低亮度

// 物理运动
visual.speed.set(100, 0);          // 设置水平速度
visual.acc.set(0, 200);            // 设置向下加速度
visual.angularSpeed = 2f;          // 设置旋转速度

// 中心对齐
Visual other = new Visual(0, 0, 30, 30);
other.center(visual);              // 将other居中到visual上
```

## 相关子类
- **Image**: 显示纹理图片的Visual子类
- **Text**: 显示文本的Visual子类  
- **Button**: 交互式按钮组件
- **Window**: UI窗口基类
- 所有具体的UI组件和游戏对象通常都直接或间接继承自Visual