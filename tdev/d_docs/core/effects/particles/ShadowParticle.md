# ShadowParticle 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/particles/ShadowParticle.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.effects.particles |
| **文件类型** | class |
| **继承关系** | extends PixelParticle.Shrinking |
| **代码行数** | 97 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现暗影效果的视觉粒子表现，用于渲染暗影魔法、诅咒等场景中的变色粒子动画。

### 系统定位
粒子效果系统中的具体粒子实现类，位于 `effects.particles` 包中，是游戏视觉反馈系统的一部分。

### 不负责什么
- 不负责暗影伤害或诅咒状态的逻辑实现
- 不负责粒子发射器的创建和管理
- 不负责暗影魔法的计算

## 3. 结构总览

### 主要成员概览
- `MISSILE`：静态工厂常量，用于导弹式发射效果
- `CURSE`：静态工厂常量，用于诅咒式发射效果
- `UP`：静态工厂常量，用于向上发射效果

### 主要逻辑块概览
- `reset()`：重置导弹式粒子状态
- `resetCurse()`：重置诅咒式粒子状态
- `resetUp()`：重置向上发射粒子状态
- `update()`：更新粒子大小、透明度和颜色

### 生命周期/调用时机
- 导弹模式：粒子存活 0.5 秒
- 诅咒模式：粒子存活 0.5 秒
- 向上模式：粒子存活 1 秒

## 4. 继承与协作关系

### 父类提供的能力
继承自 `PixelParticle.Shrinking`：
- `size`：粒子大小（protected）
- `lifespan`：总存活时间（protected）
- `left`：剩余存活时间（protected）
- `x`, `y`：位置坐标（继承自 PseudoPixel）
- `speed`：速度向量（继承自 PseudoPixel）
- `am`：透明度（继承自 PseudoPixel）
- `revive()`：复活粒子
- `color(int)`：设置颜色
- `size(float)`：设置大小
- `update()`：更新逻辑（Shrinking 子类会自动缩小粒子）

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
- 暗影相关的视觉效果场景
- 通过 `Emitter` 类使用工厂发射粒子

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `MISSILE` | Emitter.Factory | 匿名实现 | 导弹式粒子工厂，调用 `reset()` |
| `CURSE` | Emitter.Factory | 匿名实现 | 诅咒式粒子工厂，调用 `resetCurse()` |
| `UP` | Emitter.Factory | 匿名实现 | 向上发射粒子工厂，调用 `resetUp()` |

### 实例字段
无显式实例字段，所有字段继承自父类。

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器。

### 初始化注意事项
- 不设置初始颜色，由 `update()` 方法动态计算
- 不设置初始存活时间，由各 `reset*()` 方法设置

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
((ShadowParticle)emitter.recycle( ShadowParticle.class )).reset( x, y );
```

### CURSE.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建诅咒式粒子实例。

**核心实现逻辑**：
```java
((ShadowParticle)emitter.recycle( ShadowParticle.class )).resetCurse( x, y );
```

### UP.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建向上发射粒子实例。

**核心实现逻辑**：
```java
((ShadowParticle)emitter.recycle( ShadowParticle.class )).resetUp( x, y );
```

### reset()

**可见性**：public

**是否覆写**：否

**方法职责**：重置导弹式粒子状态，设置位置、随机速度和存活时间。

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
    
    size = 6;
    left = lifespan = 0.5f;
}
```

**边界情况**：
- 速度为随机值，范围 `[-5, +5]` 在两个方向上
- 粒子大小为 6
- 存活时间 0.5 秒

### resetCurse()

**可见性**：public

**是否覆写**：否

**方法职责**：重置诅咒式粒子状态，设置反向起始位置以实现汇聚效果。

**参数**：
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
public void resetCurse( float x, float y ) {
    revive();
    
    size = 8;
    left = lifespan = 0.5f;
    
    speed.polar( Random.Float( PointF.PI2 ), Random.Float( 16, 32 ) );  // 全方向
    this.x = x - speed.x * lifespan;  // 反向起始位置
    this.y = y - speed.y * lifespan;
}
```

**边界情况**：
- 速度方向为全方向
- 速度大小为 `[16, 32]`
- 起始位置反向计算，粒子向目标位置汇聚

### resetUp()

**可见性**：public

**是否覆写**：否

**方法职责**：重置向上发射粒子状态，设置位置和向上速度。

**参数**：
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
public void resetUp( float x, float y ) {
    revive();
    
    speed.set( Random.Float( -8, +8 ), Random.Float( -32, -48 ) );  // 向上
    this.x = x;
    this.y = y;
    
    size = 6;
    left = lifespan = 1f;
}
```

**边界情况**：
- X 方向速度：`[-8, +8]`（随机左右漂移）
- Y 方向速度：`[-32, -48]`（向上）
- 存活时间 1 秒

### update()

**可见性**：public

**是否覆写**：是，覆写自 `PixelParticle.Shrinking.update()`

**方法职责**：更新粒子状态，实现大小、透明度和颜色的动态变化。

**核心实现逻辑**：
```java
@Override
public void update() {
    super.update();
    
    float p = left / lifespan;
    // alpha: 0 -> 1 -> 0; size: 6 -> 0; color: 0x660044 -> 0x000000
    color( ColorMath.interpolate( 0x000000, 0x440044, p ) );
    am = p < 0.5f ? p * p * 4 : (1 - p) * 2;
}
```

**边界情况**：
- 透明度：先增后减，前半段平方增长，后半段线性减少
- 颜色：从 `0x440044`（深紫色）渐变到 `0x000000`（黑色）
- 大小由父类 `Shrinking` 自动缩小

## 8. 对外暴露能力

### 显式 API
- `MISSILE`：静态工厂，用于导弹式发射效果
- `CURSE`：静态工厂，用于诅咒式发射效果
- `UP`：静态工厂，用于向上发射效果
- `reset(float x, float y)`：重置导弹式粒子
- `resetCurse(float x, float y)`：重置诅咒式粒子
- `resetUp(float x, float y)`：重置向上发射粒子

### 内部辅助方法
无。

### 扩展入口
- 可通过覆写 `update()` 方法自定义颜色渐变逻辑
- 可创建新的 Factory 实现不同的发射模式

## 9. 运行机制与调用链

### 创建时机
当需要显示暗影效果时，由 `Emitter` 调用相应工厂方法创建。

### 调用者
- `Emitter` 类通过 `pour()` 或 `burst()` 方法调用工厂方法

### 被调用者
- `Emitter.recycle()`：回收或创建粒子
- `Random.Float()`：生成随机数
- `ColorMath.interpolate()`：颜色插值

### 系统流程位置
```
游戏事件（如暗影魔法）
    → 创建 Emitter
    → Emitter.burst(ShadowParticle.CURSE, ...)
    → CURSE.emit() → resetCurse()
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
// 导弹式暗影效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.pour(ShadowParticle.MISSILE, 0.05f);
scene.add(emitter);
```

### 诅咒效果
```java
// 诅咒式暗影效果（汇聚）
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.burst(ShadowParticle.CURSE, 20);
scene.add(emitter);
```

### 向上效果
```java
// 向上发射暗影效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.pour(ShadowParticle.UP, 0.1f);
scene.add(emitter);
```

## 12. 开发注意事项

### 状态依赖
- 依赖父类 `PixelParticle.Shrinking` 的 `update()` 方法实现自动缩小
- 颜色渐变依赖 `ColorMath.interpolate()` 方法

### 生命周期耦合
- 不同模式的存活时间不同
- 诅咒模式的起始位置反向计算

### 常见陷阱
- 诅咒模式的参数是目标位置，粒子会向该位置汇聚
- 透明度变化为非线性（先平方增长后线性减少）

## 13. 修改建议与扩展点

### 适合扩展的位置
- `update()` 中的颜色渐变可自定义
- 各 `reset*()` 方法中的参数可调整

### 不建议修改的位置
- 透明度变化逻辑应保持连贯性

### 重构建议
三种模式可考虑使用枚举参数统一，减少方法数量。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是，`MISSILE`、`CURSE`、`UP` 三个静态字段
- [x] 是否已覆盖全部方法：是，包括三个 reset 方法、`update()` 和工厂方法
- [x] 是否已检查继承链与覆写关系：是，继承 `PixelParticle.Shrinking`，覆写 `update()`
- [x] 是否已核对官方中文翻译：不适用，无文本内容
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：无关联
- [x] 是否明确说明了注意事项与扩展点：是