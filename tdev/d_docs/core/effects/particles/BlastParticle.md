# BlastParticle 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/particles/BlastParticle.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.effects.particles |
| **文件类型** | class |
| **继承关系** | extends PixelParticle.Shrinking |
| **代码行数** | 66 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现爆炸效果的视觉粒子表现，用于渲染爆炸场景中的橙色扩散粒子动画。

### 系统定位
粒子效果系统中的具体粒子实现类，位于 `effects.particles` 包中，是游戏视觉反馈系统的一部分。

### 不负责什么
- 不负责爆炸的逻辑计算或伤害判定
- 不负责粒子发射器的创建和管理
- 不负责爆炸音效的播放

## 3. 结构总览

### 主要成员概览
- `FACTORY`：静态工厂常量，用于创建和回收粒子实例

### 主要逻辑块概览
- 构造器：初始化粒子颜色和加速度
- `reset()`：重置粒子状态，设置位置和运动参数
- `update()`：更新粒子透明度

### 生命周期/调用时机
由 `Emitter` 通过 `FACTORY.emit()` 方法创建并发射，粒子存活时间为 `Random.Float()` 秒（0~1秒）。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `PixelParticle.Shrinking`：
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
- `update()`：更新逻辑（Shrinking 子类会自动缩小粒子）

### 覆写的方法
- `update()`：自定义透明度变化逻辑

### 实现的接口契约
无显式接口实现。

### 依赖的关键类
- `com.watabou.noosa.particles.Emitter`：粒子发射器
- `com.watabou.noosa.particles.Emitter.Factory`：工厂接口
- `com.watabou.noosa.particles.PixelParticle`：粒子基类
- `com.watabou.utils.Random`：随机数工具

### 使用者
- 游戏中需要显示爆炸效果的场景，如炸弹爆炸、特定技能效果等
- 通过 `Emitter` 类使用 `BlastParticle.FACTORY` 发射粒子

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `FACTORY` | Emitter.Factory | 匿名实现 | 粒子工厂，负责创建和发射粒子，`lightMode()` 返回 `true` |

### 实例字段
无显式实例字段，所有字段继承自父类。

## 6. 构造与初始化机制

### 构造器
```java
public BlastParticle() {
    super();
    
    color( 0xEE7722 );  // 橙色
    acc.set( 0, +50 );  // 向下加速度
}
```

### 初始化注意事项
- 粒子颜色固定为 `0xEE7722`（橙色）
- 加速度固定为 `(0, +50)`，模拟重力效果
- 不设置 `lifespan`，由 `reset()` 方法动态设置

## 7. 方法详解

### FACTORY.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建粒子实例，并重置其状态。

**参数**：
- `emitter` (Emitter)：粒子发射器
- `index` (int)：粒子索引
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
((BlastParticle)emitter.recycle( BlastParticle.class )).reset( x, y );
```

### FACTORY.lightMode()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.lightMode()`

**方法职责**：指示粒子是否使用光照模式渲染。

**返回值**：boolean，固定返回 `true`

**说明**：光照模式会使粒子产生发光效果，适合火焰、爆炸等效果。

### BlastParticle()

**可见性**：public

**是否覆写**：否

**方法职责**：构造器，初始化粒子的颜色和加速度。

**核心实现逻辑**：
```java
color( 0xEE7722 );
acc.set( 0, +50 );
```

### reset()

**可见性**：public

**是否覆写**：否

**方法职责**：重置粒子状态，设置位置、存活时间、大小和速度。

**参数**：
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**前置条件**：粒子必须已经被发射器回收或创建。

**副作用**：修改粒子的位置、速度、大小和存活时间。

**核心实现逻辑**：
```java
public void reset( float x, float y ) {
    revive();
    
    this.x = x;
    this.y = y;
    
    left = lifespan = Random.Float();  // 0~1秒随机存活时间
    
    size = 8;
    speed.polar( -Random.Float( 3.1415926f ), Random.Float( 32, 64 ) );
}
```

**边界情况**：
- 存活时间为随机值，范围 `[0, 1)` 秒
- 速度方向为向上半圆（负角度），速度大小为 `[32, 64]`

### update()

**可见性**：public

**是否覆写**：是，覆写自 `PixelParticle.Shrinking.update()`

**方法职责**：更新粒子状态，包括透明度变化。

**核心实现逻辑**：
```java
@Override
public void update() {
    super.update();  // 调用 Shrinking.update() 实现自动缩小
    am = left > 0.8f ? (1 - left) * 5 : 1;  // 淡入效果
}
```

**边界情况**：
- 当 `left > 0.8f` 时（存活初期），透明度从 0 渐变到 1
- 之后透明度保持为 1，直到粒子消失

## 8. 对外暴露能力

### 显式 API
- `FACTORY`：静态工厂，用于发射粒子
- `reset(float x, float y)`：重置粒子状态

### 内部辅助方法
无。

### 扩展入口
- 可通过覆写 `update()` 方法自定义透明度变化逻辑
- 可通过修改构造器中的颜色值改变粒子颜色

## 9. 运行机制与调用链

### 创建时机
当需要显示爆炸效果时，由 `Emitter` 调用 `FACTORY.emit()` 创建。

### 调用者
- `Emitter` 类通过 `pour()` 或 `burst()` 方法调用 `FACTORY.emit()`

### 被调用者
- `Emitter.recycle()`：回收或创建粒子
- `Random.Float()`：生成随机数

### 系统流程位置
```
游戏事件（如炸弹爆炸）
    → 创建 Emitter
    → Emitter.pour(BlastParticle.FACTORY, ...)
    → FACTORY.emit() → reset()
    → 每帧调用 update()
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
// 在场景中创建爆炸效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.burst(BlastParticle.FACTORY, 10);  // 发射10个粒子
scene.add(emitter);
```

### 持续发射示例
```java
// 持续发射爆炸粒子
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.pour(BlastParticle.FACTORY, 0.1f);  // 每0.1秒发射一个
scene.add(emitter);
```

## 12. 开发注意事项

### 状态依赖
- 依赖父类 `PixelParticle.Shrinking` 的 `update()` 方法实现自动缩小
- 透明度计算依赖 `left` 和 `lifespan` 字段

### 生命周期耦合
- 粒子生命周期由 `lifespan` 控制，结束后自动调用 `kill()`
- 不要手动调用 `kill()`，应由 `update()` 自动处理

### 常见陷阱
- 不要忘记调用 `revive()` 来重置粒子状态
- 修改颜色值时需注意与爆炸视觉风格的协调

## 13. 修改建议与扩展点

### 适合扩展的位置
- 构造器中的颜色值可修改
- `reset()` 中的速度范围和大小可调整
- `update()` 中的透明度变化逻辑可自定义

### 不建议修改的位置
- `FACTORY` 的基本结构应保持不变，以符合框架约定

### 重构建议
如需支持不同颜色的爆炸效果，可考虑添加 `color` 参数到 `reset()` 方法中。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是，仅 `FACTORY` 一个静态字段
- [x] 是否已覆盖全部方法：是，包括构造器、`reset()`、`update()` 和工厂方法
- [x] 是否已检查继承链与覆写关系：是，继承 `PixelParticle.Shrinking`，覆写 `update()`
- [x] 是否已核对官方中文翻译：不适用，无文本内容
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：无关联
- [x] 是否明确说明了注意事项与扩展点：是