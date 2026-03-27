# EnergyParticle 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/particles/EnergyParticle.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.effects.particles |
| **文件类型** | class |
| **继承关系** | extends PixelParticle |
| **代码行数** | 69 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现能量效果的视觉粒子表现，用于渲染能量汇聚、法力恢复等场景中的黄色扩散粒子动画。

### 系统定位
粒子效果系统中的具体粒子实现类，位于 `effects.particles` 包中，是游戏视觉反馈系统的一部分。

### 不负责什么
- 不负责能量或法力的逻辑计算
- 不负责粒子发射器的创建和管理
- 不负责能量恢复的数值处理

## 3. 结构总览

### 主要成员概览
- `FACTORY`：静态工厂常量，用于创建和回收粒子实例

### 主要逻辑块概览
- 构造器：初始化粒子颜色、存活时间和随机速度
- `reset()`：重置粒子状态，设置反向起始位置
- `update()`：更新粒子透明度和大小

### 生命周期/调用时机
粒子存活时间为 1 秒，由 `Emitter` 通过 `FACTORY.emit()` 方法创建并发射。

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
- 能量相关的视觉效果场景
- 通过 `Emitter` 类使用 `EnergyParticle.FACTORY` 发射粒子

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `FACTORY` | Emitter.Factory | 匿名实现 | 粒子工厂，`lightMode()` 返回 `true` |

### 实例字段
无显式实例字段，所有字段继承自父类。

## 6. 构造与初始化机制

### 构造器
```java
public EnergyParticle() {
    super();
    
    lifespan = 1f;
    color( 0xFFFFAA );  // 淡黄色
    
    speed.polar( Random.Float( PointF.PI2 ), Random.Float( 24, 32 ) );  // 随机方向
}
```

### 初始化注意事项
- 粒子颜色固定为 `0xFFFFAA`（淡黄色）
- 存活时间固定为 1 秒
- 速度在构造时随机确定，范围 `[24, 32]`，方向为全随机
- 每个粒子实例的速度方向在创建时固定

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
((EnergyParticle)emitter.recycle( EnergyParticle.class )).reset( x, y );
```

### FACTORY.lightMode()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.lightMode()`

**方法职责**：指示粒子使用光照模式渲染。

**返回值**：boolean，固定返回 `true`

### EnergyParticle()

**可见性**：public

**是否覆写**：否

**方法职责**：构造器，初始化粒子的颜色、存活时间和随机速度。

### reset()

**可见性**：public

**是否覆写**：否

**方法职责**：重置粒子状态，设置反向起始位置以实现汇聚效果。

**参数**：
- `x` (float)：目标 X 坐标
- `y` (float)：目标 Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
public void reset( float x, float y ) {
    revive();
    
    left = lifespan;
    
    this.x = x - speed.x * lifespan;  // 反向起始位置
    this.y = y - speed.y * lifespan;
}
```

**边界情况**：
- 参数 `x`, `y` 为目标位置（汇聚中心）
- 实际起始位置根据速度反向计算，使粒子向目标位置移动

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
    am = p < 0.5f ? p * p * 4 : (1 - p) * 2;  // 非线性透明度
    size( Random.Float( 5 * left / lifespan ) );  // 随机大小变化
}
```

**边界情况**：
- 透明度：前半段快速增加（平方增长），后半段线性减少
- 大小：随机变化，范围 `[0, 5]`，与剩余时间成正比

## 8. 对外暴露能力

### 显式 API
- `FACTORY`：静态工厂，用于发射粒子
- `reset(float x, float y)`：重置粒子状态

### 内部辅助方法
无。

### 扩展入口
- 可通过覆写 `update()` 方法自定义透明度和大小变化逻辑
- 可通过修改构造器中的颜色值改变粒子颜色

## 9. 运行机制与调用链

### 创建时机
当需要显示能量效果时，由 `Emitter` 调用 `FACTORY.emit()` 创建。

### 调用者
- `Emitter` 类通过 `pour()` 或 `burst()` 方法调用 `FACTORY.emit()`

### 被调用者
- `Emitter.recycle()`：回收或创建粒子
- `Random.Float()`：生成随机数
- `PointF.polar()`：极坐标转换

### 系统流程位置
```
游戏事件（如能量恢复）
    → 创建 Emitter
    → Emitter.pour(EnergyParticle.FACTORY, ...)
    → FACTORY.emit() → reset()
    → 粒子从远处向目标位置移动
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
// 创建能量汇聚效果
Emitter emitter = new Emitter();
emitter.pos(x, y);  // 目标位置
emitter.pour(EnergyParticle.FACTORY, 0.05f);
scene.add(emitter);
```

### 爆发效果
```java
// 瞬间发射多个能量粒子
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.burst(EnergyParticle.FACTORY, 20);
scene.add(emitter);
```

## 12. 开发注意事项

### 状态依赖
- 不继承 `PixelParticle.Shrinking`，需手动管理大小变化
- 速度在构造时随机确定，每个粒子实例有固定方向

### 生命周期耦合
- 粒子生命周期由 `lifespan` 控制
- 起始位置由 `reset()` 根据速度反向计算

### 常见陷阱
- `reset()` 的参数是目标位置，不是起始位置
- 粒子向目标位置移动，而非从目标位置扩散

## 13. 修改建议与扩展点

### 适合扩展的位置
- 构造器中的颜色值可修改
- `update()` 中的透明度和大小变化逻辑可自定义
- 构造器中的速度范围可调整

### 不建议修改的位置
- `reset()` 的反向位置计算逻辑，以保证汇聚效果

### 重构建议
如需支持不同颜色的能量效果，可考虑添加颜色参数或创建子类。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是，仅 `FACTORY` 一个静态字段
- [x] 是否已覆盖全部方法：是，包括构造器、`reset()`、`update()` 和工厂方法
- [x] 是否已检查继承链与覆写关系：是，继承 `PixelParticle`，覆写 `update()`
- [x] 是否已核对官方中文翻译：不适用，无文本内容
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：无关联
- [x] 是否明确说明了注意事项与扩展点：是