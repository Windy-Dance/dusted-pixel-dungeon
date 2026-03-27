# WoolParticle 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/particles/WoolParticle.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.effects.particles |
| **文件类型** | class |
| **继承关系** | extends PixelParticle.Shrinking |
| **代码行数** | 58 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现羊毛效果的视觉粒子表现，用于渲染羊毛、毛絮等场景中的白色上升粒子动画。

### 系统定位
粒子效果系统中的具体粒子实现类，位于 `effects.particles` 包中，是游戏视觉反馈系统的一部分。

### 不负责什么
- 不负责羊毛物品的逻辑实现
- 不负责粒子发射器的创建和管理
- 不负责相关游戏机制

## 3. 结构总览

### 主要成员概览
- `FACTORY`：静态工厂常量，用于创建和回收粒子实例

### 主要逻辑块概览
- 构造器：初始化粒子颜色和向上加速度
- `reset()`：重置粒子状态

### 生命周期/调用时机
粒子存活时间为 `[0.6, 1.0]` 秒（随机），由 `Emitter` 通过 `FACTORY.emit()` 方法创建并发射。

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
无显式覆写，使用父类的 `update()` 实现。

### 实现的接口契约
无显式接口实现。

### 依赖的关键类
- `com.watabou.noosa.particles.Emitter`：粒子发射器
- `com.watabou.noosa.particles.Emitter.Factory`：工厂接口
- `com.watabou.noosa.particles.PixelParticle`：粒子基类
- `com.watabou.utils.ColorMath`：颜色计算工具
- `com.watabou.utils.Random`：随机数工具

### 使用者
- 羊毛相关的视觉效果场景
- 通过 `Emitter` 类使用 `WoolParticle.FACTORY` 发射粒子

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `FACTORY` | Emitter.Factory | 匿名实现 | 粒子工厂，调用 `reset()` |

### 实例字段
无显式实例字段，所有字段继承自父类。

## 6. 构造与初始化机制

### 构造器
```java
public WoolParticle() {
    super();
    
    color( ColorMath.random( 0x999999, 0xEEEEE0 ) );  // 随机浅色
    
    acc.set( 0, -40 );  // 向上加速度
}
```

### 初始化注意事项
- 粒子颜色为随机浅色，范围 `0x999999`（灰色）到 `0xEEEEE0`（米色/象牙白）
- 加速度为 `(0, -40)`，模拟向上漂浮效果
- 不设置存活时间，由 `reset()` 方法设置

## 7. 方法详解

### FACTORY.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建粒子实例。

**参数**：
- `emitter` (Emitter)：粒子发射器
- `index` (int)：粒子索引
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
((WoolParticle)emitter.recycle( WoolParticle.class )).reset( x, y );
```

### WoolParticle()

**可见性**：public

**是否覆写**：否

**方法职责**：构造器，初始化粒子的随机颜色和向上加速度。

### reset()

**可见性**：public

**是否覆写**：否

**方法职责**：重置粒子状态，设置位置、存活时间、大小和随机速度。

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
    size = 5;
    
    speed.set( Random.Float( -10, +10 ), Random.Float( -10, +10 ) );  // 随机方向
}
```

**边界情况**：
- 存活时间为随机值，范围 `[0.6, 1.0]` 秒
- 粒子大小为 5
- 速度为随机值，范围 `[-10, +10]` 在两个方向上

## 8. 对外暴露能力

### 显式 API
- `FACTORY`：静态工厂，用于发射粒子
- `reset(float x, float y)`：重置粒子状态

### 内部辅助方法
无。

### 扩展入口
- 可通过修改构造器中的颜色范围改变粒子颜色
- 可通过修改 `reset()` 中的参数调整粒子行为

## 9. 运行机制与调用链

### 创建时机
当需要显示羊毛效果时，由 `Emitter` 调用 `FACTORY.emit()` 创建。

### 调用者
- `Emitter` 类通过 `pour()` 或 `burst()` 方法调用 `FACTORY.emit()`

### 被调用者
- `Emitter.recycle()`：回收或创建粒子
- `ColorMath.random()`：随机颜色
- `Random.Float()`：生成随机数

### 系统流程位置
```
游戏事件（如羊毛效果）
    → 创建 Emitter
    → Emitter.pour(WoolParticle.FACTORY, ...)
    → FACTORY.emit() → reset()
    → 每帧调用 update()（父类 Shrinking 自动缩小）
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
// 创建羊毛效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.pour(WoolParticle.FACTORY, 0.1f);
scene.add(emitter);
```

### 爆发效果
```java
// 瞬间发射多个羊毛粒子
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.burst(WoolParticle.FACTORY, 20);
scene.add(emitter);
```

## 12. 开发注意事项

### 状态依赖
- 依赖父类 `PixelParticle.Shrinking` 的 `update()` 方法实现自动缩小
- 颜色在构造时随机确定，`reset()` 不会改变颜色

### 生命周期耦合
- 粒子生命周期由 `lifespan` 控制
- 存活时间为随机值

### 常见陷阱
- 注意粒子颜色在回收后不会重新随机
- 加速度为负值表示向上运动

## 13. 修改建议与扩展点

### 适合扩展的位置
- 构造器中的颜色范围可调整
- `reset()` 中的速度范围可调整
- `reset()` 中的大小值可调整

### 不建议修改的位置
- 父类 `update()` 行为，以保证粒子正常缩小

### 重构建议
如需每次 `reset` 都重新随机颜色，应在 `reset()` 中调用 `color()`。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是，仅 `FACTORY` 一个静态字段
- [x] 是否已覆盖全部方法：是，包括构造器、`reset()` 和工厂方法
- [x] 是否已检查继承链与覆写关系：是，继承 `PixelParticle.Shrinking`，无覆写
- [x] 是否已核对官方中文翻译：不适用，无文本内容
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：无关联
- [x] 是否明确说明了注意事项与扩展点：是