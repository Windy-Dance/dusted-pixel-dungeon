# PoisonParticle 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/particles/PoisonParticle.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.effects.particles |
| **文件类型** | class |
| **继承关系** | extends PixelParticle |
| **代码行数** | 92 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现毒药效果的视觉粒子表现，用于渲染毒气、中毒等场景中的变色粒子动画。

### 系统定位
粒子效果系统中的具体粒子实现类，位于 `effects.particles` 包中，是游戏视觉反馈系统的一部分。

### 不负责什么
- 不负责毒药伤害的计算或应用
- 不负责粒子发射器的创建和管理
- 不负责中毒状态效果的逻辑

## 3. 结构总览

### 主要成员概览
- `MISSILE`：静态工厂常量，用于导弹式发射效果
- `SPLASH`：静态工厂常量，用于溅射式发射效果

### 主要逻辑块概览
- 构造器：初始化粒子存活时间和加速度
- `resetMissile()`：重置导弹式粒子状态
- `resetSplash()`：重置溅射式粒子状态
- `update()`：更新粒子大小、透明度和颜色

### 生命周期/调用时机
粒子存活时间为 0.6 秒，由 `Emitter` 通过工厂方法创建并发射。

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
- `update()`：自定义大小、透明度和颜色变化逻辑

### 实现的接口契约
无显式接口实现。

### 依赖的关键类
- `com.watabou.noosa.particles.Emitter`：粒子发射器
- `com.watabou.noosa.particles.Emitter.Factory`：工厂接口
- `com.watabou.noosa.particles.PixelParticle`：粒子基类
- `com.watabou.utils.ColorMath`：颜色计算工具
- `com.watabou.utils.Random`：随机数工具

### 使用者
- 毒药相关的视觉效果场景
- 通过 `Emitter` 类使用 `PoisonParticle.MISSILE` 或 `PoisonParticle.SPLASH` 发射粒子

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `MISSILE` | Emitter.Factory | 匿名实现 | 导弹式粒子工厂，调用 `resetMissile()`，`lightMode()` 返回 `true` |
| `SPLASH` | Emitter.Factory | 匿名实现 | 溅射式粒子工厂，调用 `resetSplash()`，`lightMode()` 返回 `true` |

### 实例字段
无显式实例字段，所有字段继承自父类。

## 6. 构造与初始化机制

### 构造器
```java
public PoisonParticle() {
    super();
    
    lifespan = 0.6f;
    
    acc.set( 0, +30 );  // 向下加速度
}
```

### 初始化注意事项
- 存活时间固定为 0.6 秒
- 加速度固定为 `(0, +30)`，模拟重力效果
- 不设置初始颜色，由 `update()` 方法动态计算

## 7. 方法详解

### MISSILE.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建导弹式粒子实例。

**参数**：
- `emitter` (Emitter)：粒子发射器
- `index` (int)：粒子索引
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
((PoisonParticle)emitter.recycle( PoisonParticle.class )).resetMissile( x, y );
```

### MISSILE.lightMode()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.lightMode()`

**方法职责**：指示粒子使用光照模式渲染。

**返回值**：boolean，固定返回 `true`

### SPLASH.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建溅射式粒子实例。

**核心实现逻辑**：
```java
((PoisonParticle)emitter.recycle( PoisonParticle.class )).resetSplash( x, y );
```

### SPLASH.lightMode()

**可见性**：public（匿名类覆写）

**返回值**：boolean，固定返回 `true`

### PoisonParticle()

**可见性**：public

**是否覆写**：否

**方法职责**：构造器，初始化粒子的存活时间和加速度。

### resetMissile()

**可见性**：public

**是否覆写**：否

**方法职责**：重置导弹式粒子状态，设置位置和向上半圆方向的速度。

**参数**：
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
public void resetMissile( float x, float y ) {
    revive();
    
    this.x = x;
    this.y = y;
    
    left = lifespan;
    
    speed.polar( -Random.Float( 3.1415926f ), Random.Float( 6 ) );  // 向上半圆，低速
}
```

**边界情况**：
- 速度方向为向上半圆（负角度，`[-π, 0]`）
- 速度大小为 `[0, 6]`

### resetSplash()

**可见性**：public

**是否覆写**：否

**方法职责**：重置溅射式粒子状态，设置位置和全方向扩散速度。

**参数**：
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
public void resetSplash( float x, float y ) {
    revive();
    
    this.x = x;
    this.y = y;
    
    left = lifespan;
    
    speed.polar( Random.Float( 3.1415926f ), Random.Float( 10, 20 ) );  // 全方向，中速
}
```

**边界情况**：
- 速度方向为全方向（`[0, π]`）
- 速度大小为 `[10, 20]`

### update()

**可见性**：public

**是否覆写**：是，覆写自 `PixelParticle.update()`

**方法职责**：更新粒子状态，实现大小、透明度和颜色的动态变化。

**核心实现逻辑**：
```java
@Override
public void update() {
    super.update();
    // alpha: 1 -> 0; size: 1 -> 4
    size( 4 - (am = left / lifespan) * 3 );
    // color: 0x8844FF -> 0x00FF00
    color( ColorMath.interpolate( 0x00FF00, 0x8844FF, am ) );
}
```

**边界情况**：
- 透明度：从 1 渐变到 0
- 大小：从 1 渐变到 4（逐渐变大）
- 颜色：从 `0x8844FF`（紫色）渐变到 `0x00FF00`（绿色）

## 8. 对外暴露能力

### 显式 API
- `MISSILE`：静态工厂，用于导弹式发射效果
- `SPLASH`：静态工厂，用于溅射式发射效果
- `resetMissile(float x, float y)`：重置导弹式粒子状态
- `resetSplash(float x, float y)`：重置溅射式粒子状态

### 内部辅助方法
无。

### 扩展入口
- 可通过覆写 `update()` 方法自定义颜色渐变逻辑
- 可通过创建新的 Factory 实现不同的发射模式

## 9. 运行机制与调用链

### 创建时机
当需要显示毒药效果时，由 `Emitter` 调用 `MISSILE.emit()` 或 `SPLASH.emit()` 创建。

### 调用者
- `Emitter` 类通过 `pour()` 或 `burst()` 方法调用工厂方法

### 被调用者
- `Emitter.recycle()`：回收或创建粒子
- `Random.Float()`：生成随机数
- `ColorMath.interpolate()`：颜色插值计算

### 系统流程位置
```
游戏事件（如毒气陷阱）
    → 创建 Emitter
    → Emitter.burst(PoisonParticle.SPLASH, ...)
    → SPLASH.emit() → resetSplash()
    → 每帧调用 update() 更新大小、透明度和颜色
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
// 导弹式毒药效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.pour(PoisonParticle.MISSILE, 0.05f);
scene.add(emitter);
```

### 溅射效果
```java
// 溅射式毒药效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.burst(PoisonParticle.SPLASH, 20);  // 发射20个粒子
scene.add(emitter);
```

## 12. 开发注意事项

### 状态依赖
- 不继承 `PixelParticle.Shrinking`，需手动管理大小变化
- 颜色渐变依赖 `ColorMath.interpolate()` 方法

### 生命周期耦合
- 粒子生命周期由 `lifespan` 控制
- 大小和颜色变化与生命周期同步

### 常见陷阱
- 注意此粒子继承 `PixelParticle` 而非 `PixelParticle.Shrinking`
- 颜色渐变方向：从紫色渐变到绿色

## 13. 修改建议与扩展点

### 适合扩展的位置
- `update()` 中的颜色渐变可自定义
- `resetMissile()` 和 `resetSplash()` 中的速度范围可调整

### 不建议修改的位置
- 颜色渐变逻辑应保持连贯性以保证视觉效果

### 重构建议
如需支持不同颜色的毒药效果，可考虑添加颜色参数或创建子类。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是，`MISSILE` 和 `SPLASH` 两个静态字段
- [x] 是否已覆盖全部方法：是，包括构造器、`resetMissile()`、`resetSplash()`、`update()` 和工厂方法
- [x] 是否已检查继承链与覆写关系：是，继承 `PixelParticle`，覆写 `update()`
- [x] 是否已核对官方中文翻译：不适用，无文本内容
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：无关联
- [x] 是否明确说明了注意事项与扩展点：是