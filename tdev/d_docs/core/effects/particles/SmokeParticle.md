# SmokeParticle 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/particles/SmokeParticle.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.effects.particles |
| **文件类型** | class |
| **继承关系** | extends PixelParticle |
| **代码行数** | 84 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现烟雾效果的视觉粒子表现，用于渲染烟雾、爆炸后烟雾等场景中的灰色粒子动画。

### 系统定位
粒子效果系统中的具体粒子实现类，位于 `effects.particles` 包中，是游戏视觉反馈系统的一部分。

### 不负责什么
- 不负责烟雾机制的逻辑实现
- 不负责粒子发射器的创建和管理
- 不负责伤害或效果计算

## 3. 结构总览

### 主要成员概览
- `FACTORY`：静态工厂常量，用于标准烟雾效果
- `SPEW`：静态工厂常量，用于喷射式烟雾效果

### 主要逻辑块概览
- 构造器：初始化粒子颜色和向上加速度
- `reset()`：重置标准烟雾粒子状态
- `resetSpew()`：重置喷射式烟雾粒子状态
- `update()`：更新粒子透明度和大小

### 生命周期/调用时机
- 标准模式：粒子存活 `[0.6, 1.0]` 秒（随机）
- 喷射模式：粒子存活 `[0.6, 1.0]` 秒（随机）

## 4. 继承与协作关系

### 父类提供的能力
继承自 `PixelParticle`：
- `size`：粒子大小（protected）
- `lifespan`：总存活时间（protected）
- `left`：剩余存活时间（protected）
- `x`, `y`：位置坐标（继承自 PseudoPixel）
- `speed`：速度向量（继承自 PseudoPixel）
- `acc`：加速度向量（继承自 PseudoPixel）
- `am`：透明度（继承自 PseudoPixel）
- `revive()`：复活粒子
- `color(int)`：设置颜色
- `size(float)`：设置大小
- `update()`：更新逻辑

### 覆写的方法
- `update()`：自定义透明度和大小变化逻辑

### 实现的接口契约
无显式接口实现。

### 依赖的关键类
- `com.watabou.noosa.particles.Emitter`：粒子发射器
- `com.watabou.noosa.particles.Emitter.Factory`：工厂接口
- `com.watabou.noosa.particles.PixelParticle`：粒子基类
- `com.watabou.utils.PointF`：点/向量工具类
- `com.watabou.utils.Random`：随机数工具

### 使用者
- 烟雾相关的视觉效果场景
- 通过 `Emitter` 类使用工厂发射粒子

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `FACTORY` | Emitter.Factory | 匿名实现 | 标准烟雾粒子工厂，调用 `reset()` |
| `SPEW` | Emitter.Factory | 匿名实现 | 喷射式烟雾粒子工厂，调用 `resetSpew()` |

### 实例字段
无显式实例字段，所有字段继承自父类。

## 6. 构造与初始化机制

### 构造器
```java
public SmokeParticle() {
    super();
    
    color( 0x222222 );  // 深灰色
    
    acc.set( 0, -40 );  // 向上加速度
}
```

### 初始化注意事项
- 粒子颜色固定为 `0x222222`（深灰色）
- 加速度为 `(0, -40)`，模拟烟雾向上飘动

## 7. 方法详解

### FACTORY.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建标准烟雾粒子实例。

**参数**：
- `emitter` (Emitter)：粒子发射器
- `index` (int)：粒子索引
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
((SmokeParticle)emitter.recycle( SmokeParticle.class )).reset( x, y );
```

### SPEW.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建喷射式烟雾粒子实例。

**核心实现逻辑**：
```java
((SmokeParticle)emitter.recycle( SmokeParticle.class )).resetSpew( x, y );
```

### SmokeParticle()

**可见性**：public

**是否覆写**：否

**方法职责**：构造器，初始化粒子的颜色和向上加速度。

### reset()

**可见性**：public

**是否覆写**：否

**方法职责**：重置标准烟雾粒子状态，设置位置、存活时间和随机速度。

**参数**：
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
public void reset( float x, float y ) {
    revive();
    
    this.x = x;
    this.y = y;
    
    left = lifespan = Random.Float( 0.6f, 1f );  // 随机存活时间
    speed.set( Random.Float( -4, +4 ), Random.Float( -8, +8 ) );  // 随机速度
}
```

**边界情况**：
- 存活时间为随机值，范围 `[0.6, 1.0]` 秒
- X 方向速度：`[-4, +4]`
- Y 方向速度：`[-8, +8]`

### resetSpew()

**可见性**：public

**是否覆写**：否

**方法职责**：重置喷射式烟雾粒子状态，设置特定方向的速度和加速度。

**参数**：
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
public void resetSpew( float x, float y ) {
    revive();
    
    this.x = x;
    this.y = y;
    
    acc.set( -40, 40 );  // 斜向加速度
    
    left = lifespan = Random.Float( 0.6f, 1f );
    speed.polar( Random.Float(PointF.PI*1.7f, PointF.PI*1.8f), Random.Float( 30, 60 ) );
}
```

**边界情况**：
- 加速度为 `(-40, 40)`，斜向左上
- 速度方向为 `PI*1.7f` 到 `PI*1.8f`（约 306° 到 324°，即右下方）
- 速度大小为 `[30, 60]`

### update()

**可见性**：public

**是否覆写**：是，覆写自 `PixelParticle.update()`

**方法职责**：更新粒子状态，实现透明度和大小的动态变化。

**核心实现逻辑**：
```java
@Override
public void update() {
    super.update();
    
    float p = left / lifespan;
    am = p > 0.8f ? 2 - 2*p : p * 0.5f;  // 初期快速淡出，后期缓慢增加
    size( 16 - p * 8 );  // 逐渐变小
}
```

**边界情况**：
- 透明度：初期快速从 0.4 增加到 1，之后缓慢减少
- 大小：从 16 渐变到 8

## 8. 对外暴露能力

### 显式 API
- `FACTORY`：静态工厂，用于标准烟雾效果
- `SPEW`：静态工厂，用于喷射式烟雾效果
- `reset(float x, float y)`：重置标准粒子状态
- `resetSpew(float x, float y)`：重置喷射式粒子状态

### 内部辅助方法
无。

### 扩展入口
- 可通过覆写 `update()` 方法自定义透明度和大小变化逻辑
- 可创建新的 Factory 实现不同的发射模式

## 9. 运行机制与调用链

### 创建时机
当需要显示烟雾效果时，由 `Emitter` 调用工厂方法创建。

### 调用者
- `Emitter` 类通过 `pour()` 或 `burst()` 方法调用工厂方法

### 被调用者
- `Emitter.recycle()`：回收或创建粒子
- `Random.Float()`：生成随机数
- `PointF.polar()`：极坐标转换

### 系统流程位置
```
游戏事件（如爆炸后烟雾）
    → 创建 Emitter
    → Emitter.pour(SmokeParticle.FACTORY, ...)
    → FACTORY.emit() → reset()
    → 每帧调用 update() 更新透明度和大小
    → 粒子消失后自动 kill()
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无。

### 依赖的资源
无。

### 中文翻译来源
不适用。

## 11. 使用示例

### 基本用法
```java
// 创建标准烟雾效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.pour(SmokeParticle.FACTORY, 0.05f);
scene.add(emitter);
```

### 喷射效果
```java
// 创建喷射式烟雾效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.burst(SmokeParticle.SPEW, 15);
scene.add(emitter);
```

## 12. 开发注意事项

### 状态依赖
- 不继承 `PixelParticle.Shrinking`，需手动管理大小变化
- 存活时间为随机值

### 生命周期耦合
- 粒子生命周期由 `lifespan` 控制
- 喷射模式会覆盖加速度

### 常见陷阱
- 注意喷射模式的加速度被修改为斜向
- 透明度变化较为复杂，初期快速增加后期缓慢减少

## 13. 修改建议与扩展点

### 适合扩展的位置
- 构造器中的颜色值可修改
- `update()` 中的透明度和大小变化逻辑可自定义

### 不建议修改的位置
- 喷射模式的加速度和速度方向，以保证视觉效果

### 重构建议
如需支持不同颜色的烟雾效果，可考虑添加颜色参数。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是，`FACTORY` 和 `SPEW` 两个静态字段
- [x] 是否已覆盖全部方法：是，包括构造器、`reset()`、`resetSpew()`、`update()` 和工厂方法
- [x] 是否已检查继承链与覆写关系：是，继承 `PixelParticle`，覆写 `update()`
- [x] 是否已核对官方中文翻译：不适用，无文本内容
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：无关联
- [x] 是否明确说明了注意事项与扩展点：是