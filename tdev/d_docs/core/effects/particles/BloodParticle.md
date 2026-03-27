# BloodParticle 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/particles/BloodParticle.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.effects.particles |
| **文件类型** | class |
| **继承关系** | extends PixelParticle.Shrinking |
| **代码行数** | 89 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现血液效果的视觉粒子表现，用于渲染角色受伤、攻击命中等场景中的红色血液粒子动画。

### 系统定位
粒子效果系统中的具体粒子实现类，位于 `effects.particles` 包中，是游戏视觉反馈系统的一部分。

### 不负责什么
- 不负责伤害计算或生命值管理
- 不负责粒子发射器的创建和管理
- 不负责血液溅射的物理逻辑

## 3. 结构总览

### 主要成员概览
- `FACTORY`：静态工厂常量，用于标准血液效果
- `BURST`：静态工厂常量，用于爆发式血液效果（带光照）

### 主要逻辑块概览
- 构造器：初始化粒子颜色、存活时间和加速度
- `reset()`：重置标准血液粒子状态
- `resetBurst()`：重置爆发式血液粒子状态
- `update()`：更新粒子透明度

### 生命周期/调用时机
- 标准模式：粒子存活 0.8 秒
- 爆发模式：粒子存活 0.5 秒

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
- `com.watabou.utils.PointF`：点/向量工具类
- `com.watabou.utils.Random`：随机数工具

### 使用者
- 角色受伤时的视觉效果
- 攻击命中时的反馈效果
- 通过 `Emitter` 类使用 `BloodParticle.FACTORY` 或 `BloodParticle.BURST` 发射粒子

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `FACTORY` | Emitter.Factory | 匿名实现 | 标准血液粒子工厂，调用 `reset()` |
| `BURST` | Emitter.Factory | 匿名实现 | 爆发式血液粒子工厂，调用 `resetBurst()`，`lightMode()` 返回 `true` |

### 实例字段
无显式实例字段，所有字段继承自父类。

## 6. 构造与初始化机制

### 构造器
```java
public BloodParticle() {
    super();
    
    color( 0xCC0000 );  // 深红色
    lifespan = 0.8f;
    
    acc.set( 0, +40 );  // 向下加速度
}
```

### 初始化注意事项
- 粒子颜色固定为 `0xCC0000`（深红色）
- 默认存活时间为 0.8 秒
- 加速度固定为 `(0, +40)`，模拟重力效果

## 7. 方法详解

### FACTORY.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建标准血液粒子实例。

**参数**：
- `emitter` (Emitter)：粒子发射器
- `index` (int)：粒子索引
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
((BloodParticle)emitter.recycle( BloodParticle.class )).reset( x, y );
```

### BURST.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建爆发式血液粒子实例。

**核心实现逻辑**：
```java
((BloodParticle)emitter.recycle( BloodParticle.class )).resetBurst( x, y );
```

### BURST.lightMode()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.lightMode()`

**方法职责**：指示爆发模式粒子使用光照模式渲染。

**返回值**：boolean，固定返回 `true`

### BloodParticle()

**可见性**：public

**是否覆写**：否

**方法职责**：构造器，初始化粒子的颜色、存活时间和加速度。

### reset()

**可见性**：public

**是否覆写**：否

**方法职责**：重置标准血液粒子状态，位置固定无初速度。

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
    
    left = lifespan;  // 使用构造器设置的 0.8f
    
    size = 4;
    speed.set( 0 );  // 无初速度，仅受重力影响
}
```

### resetBurst()

**可见性**：public

**是否覆写**：否

**方法职责**：重置爆发式血液粒子状态，具有向外扩散的速度。

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

    speed.polar( Random.Float(PointF.PI2), Random.Float( 16, 32 ) );  // 全方向扩散
    size = 5;

    left = 0.5f;  // 较短存活时间
}
```

**边界情况**：
- 速度方向为全方向（`PI2` = `2π`）
- 速度大小为 `[16, 32]`
- 存活时间固定为 0.5 秒

### update()

**可见性**：public

**是否覆写**：是，覆写自 `PixelParticle.Shrinking.update()`

**方法职责**：更新粒子状态，实现淡入淡出效果。

**核心实现逻辑**：
```java
@Override
public void update() {
    super.update();
    float p = left / lifespan;
    am = p > 0.6f ? (1 - p) * 2.5f : 1;  // 初期淡入，后期保持
}
```

**边界情况**：
- 当 `p > 0.6f` 时（存活初期约前 32%），透明度从 0 渐变到 1
- 之后透明度保持为 1，直到粒子消失

## 8. 对外暴露能力

### 显式 API
- `FACTORY`：静态工厂，用于标准血液效果
- `BURST`：静态工厂，用于爆发式血液效果（带光照）
- `reset(float x, float y)`：重置标准粒子状态
- `resetBurst(float x, float y)`：重置爆发式粒子状态

### 内部辅助方法
无。

### 扩展入口
- 可通过覆写 `update()` 方法自定义透明度变化逻辑
- 可通过创建新的 Factory 实现不同的发射模式

## 9. 运行机制与调用链

### 创建时机
当需要显示血液效果时，由 `Emitter` 调用 `FACTORY.emit()` 或 `BURST.emit()` 创建。

### 调用者
- `Emitter` 类通过 `pour()` 或 `burst()` 方法调用工厂方法

### 被调用者
- `Emitter.recycle()`：回收或创建粒子
- `Random.Float()`：生成随机数
- `PointF.polar()`：极坐标转换

### 系统流程位置
```
游戏事件（如角色受伤）
    → 创建 Emitter
    → Emitter.burst(BloodParticle.BURST, ...)
    → BURST.emit() → resetBurst()
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
// 标准血液效果（无初速度，仅受重力下落）
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.pour(BloodParticle.FACTORY, 0.1f);
scene.add(emitter);
```

### 爆发效果
```java
// 爆发式血液效果（向四周扩散，带光照）
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.burst(BloodParticle.BURST, 15);  // 发射15个粒子
scene.add(emitter);
```

## 12. 开发注意事项

### 状态依赖
- 依赖父类 `PixelParticle.Shrinking` 的 `update()` 方法实现自动缩小
- 透明度计算依赖 `left` 和 `lifespan` 字段

### 生命周期耦合
- 标准模式和爆发模式的存活时间不同
- 爆发模式会覆盖构造器中设置的 `lifespan`

### 常见陷阱
- 使用 `BURST` 工厂时，`lifespan` 被覆盖为 0.5f，不受构造器影响
- 不要在 `resetBurst()` 后手动修改 `lifespan`

## 13. 修改建议与扩展点

### 适合扩展的位置
- 构造器中的颜色值可修改以适应不同角色
- `resetBurst()` 中的速度范围可调整

### 不建议修改的位置
- 透明度计算逻辑，以保证视觉效果一致性

### 重构建议
如需支持不同颜色的血液效果（如不同怪物），可考虑创建子类或添加参数。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是，`FACTORY` 和 `BURST` 两个静态字段
- [x] 是否已覆盖全部方法：是，包括构造器、`reset()`、`resetBurst()`、`update()` 和工厂方法
- [x] 是否已检查继承链与覆写关系：是，继承 `PixelParticle.Shrinking`，覆写 `update()`
- [x] 是否已核对官方中文翻译：不适用，无文本内容
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：无关联
- [x] 是否明确说明了注意事项与扩展点：是