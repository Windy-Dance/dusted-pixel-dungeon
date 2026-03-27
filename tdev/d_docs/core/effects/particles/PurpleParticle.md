# PurpleParticle 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/particles/PurpleParticle.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.effects.particles |
| **文件类型** | class |
| **继承关系** | extends PixelParticle |
| **代码行数** | 87 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现紫色效果的视觉粒子表现，用于渲染魔法、特殊技能等场景中的变色粒子动画。

### 系统定位
粒子效果系统中的具体粒子实现类，位于 `effects.particles` 包中，是游戏视觉反馈系统的一部分。

### 不负责什么
- 不负责相关技能或魔法的逻辑实现
- 不负责粒子发射器的创建和管理
- 不负责伤害或效果计算

## 3. 结构总览

### 主要成员概览
- `MISSILE`：静态工厂常量，用于导弹式发射效果
- `BURST`：静态工厂常量，用于爆发式发射效果

### 主要逻辑块概览
- 构造器：初始化粒子存活时间
- `reset()`：重置导弹式粒子状态
- `resetBurst()`：重置爆发式粒子状态
- `update()`：更新粒子大小、透明度和颜色

### 生命周期/调用时机
粒子存活时间为 0.5 秒，由 `Emitter` 通过工厂方法创建并发射。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `PixelParticle`：
- `size`：粒子大小（protected）
- `lifespan`：总存活时间（protected）
- `left`：剩余存活时间（protected）
- `x`, `y`：位置坐标（继承自 PseudoPixel）
- `speed`：速度向量（继承自 PseudoPixel）
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
- `com.watabou.utils.PointF`：点/向量工具类
- `com.watabou.utils.Random`：随机数工具

### 使用者
- 紫色魔法相关的视觉效果场景
- 通过 `Emitter` 类使用工厂发射粒子

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `MISSILE` | Emitter.Factory | 匿名实现 | 导弹式粒子工厂，调用 `reset()` |
| `BURST` | Emitter.Factory | 匿名实现 | 爆发式粒子工厂，调用 `resetBurst()`，`lightMode()` 返回 `true` |

### 实例字段
无显式实例字段，所有字段继承自父类。

## 6. 构造与初始化机制

### 构造器
```java
public PurpleParticle() {
    super();
    
    lifespan = 0.5f;
}
```

### 初始化注意事项
- 存活时间固定为 0.5 秒
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
((PurpleParticle)emitter.recycle( PurpleParticle.class )).reset( x, y );
```

### BURST.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建爆发式粒子实例。

**核心实现逻辑**：
```java
((PurpleParticle)emitter.recycle( PurpleParticle.class )).resetBurst( x, y );
```

### BURST.lightMode()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.lightMode()`

**方法职责**：指示爆发模式粒子使用光照模式渲染。

**返回值**：boolean，固定返回 `true`

### PurpleParticle()

**可见性**：public

**是否覆写**：否

**方法职责**：构造器，初始化粒子的存活时间。

### reset()

**可见性**：public

**是否覆写**：否

**方法职责**：重置导弹式粒子状态，设置位置和随机速度。

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
    
    speed.set( Random.Float( -5, +5 ), Random.Float( -5, +5 ) );  // 随机方向
    
    left = lifespan;
}
```

**边界情况**：
- 速度为随机值，范围 `[-5, +5]` 在两个方向上

### resetBurst()

**可见性**：public

**是否覆写**：否

**方法职责**：重置爆发式粒子状态，设置位置和全方向扩散速度。

**参数**：
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
public void resetBurst( float x, float y ) {
    revive();
    
    this.x = x;
    this.y = y;
    
    speed.polar( Random.Float( PointF.PI2 ), Random.Float( 16, 32 ) );  // 全方向扩散
    
    left = lifespan;
}
```

**边界情况**：
- 速度方向为全方向（`PI2` = `2π`）
- 速度大小为 `[16, 32]`

### update()

**可见性**：public

**是否覆写**：是，覆写自 `PixelParticle.update()`

**方法职责**：更新粒子状态，实现大小、透明度和颜色的动态变化。

**核心实现逻辑**：
```java
@Override
public void update() {
    super.update();
    // alpha: 1 -> 0; size: 1 -> 5
    size( 5 - (am = left / lifespan) * 4 );
    // color: 0xFF0044 -> 0x220066
    color( ColorMath.interpolate( 0x220066, 0xFF0044, am ) );
}
```

**边界情况**：
- 透明度：从 1 渐变到 0
- 大小：从 1 渐变到 5（逐渐变大）
- 颜色：从 `0xFF0044`（粉红色）渐变到 `0x220066`（深紫色）

## 8. 对外暴露能力

### 显式 API
- `MISSILE`：静态工厂，用于导弹式发射效果
- `BURST`：静态工厂，用于爆发式发射效果
- `reset(float x, float y)`：重置导弹式粒子状态
- `resetBurst(float x, float y)`：重置爆发式粒子状态

### 内部辅助方法
无。

### 扩展入口
- 可通过覆写 `update()` 方法自定义颜色渐变逻辑
- 可通过创建新的 Factory 实现不同的发射模式

## 9. 运行机制与调用链

### 创建时机
当需要显示紫色魔法效果时，由 `Emitter` 调用工厂方法创建。

### 调用者
- `Emitter` 类通过 `pour()` 或 `burst()` 方法调用工厂方法

### 被调用者
- `Emitter.recycle()`：回收或创建粒子
- `Random.Float()`：生成随机数
- `PointF.polar()`：极坐标转换
- `ColorMath.interpolate()`：颜色插值

### 系统流程位置
```
游戏事件（如魔法技能）
    → 创建 Emitter
    → Emitter.burst(PurpleParticle.BURST, ...)
    → BURST.emit() → resetBurst()
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
// 导弹式紫色效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.pour(PurpleParticle.MISSILE, 0.05f);
scene.add(emitter);
```

### 爆发效果
```java
// 爆发式紫色效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.burst(PurpleParticle.BURST, 20);
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
- 颜色渐变方向：从粉红色渐变到深紫色

## 13. 修改建议与扩展点

### 适合扩展的位置
- `update()` 中的颜色渐变可自定义
- `reset()` 和 `resetBurst()` 中的速度范围可调整

### 不建议修改的位置
- 颜色渐变逻辑应保持连贯性以保证视觉效果

### 重构建议
如需支持不同颜色的魔法效果，可考虑添加颜色参数或创建子类。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是，`MISSILE` 和 `BURST` 两个静态字段
- [x] 是否已覆盖全部方法：是，包括构造器、`reset()`、`resetBurst()`、`update()` 和工厂方法
- [x] 是否已检查继承链与覆写关系：是，继承 `PixelParticle`，覆写 `update()`
- [x] 是否已核对官方中文翻译：不适用，无文本内容
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：无关联
- [x] 是否明确说明了注意事项与扩展点：是