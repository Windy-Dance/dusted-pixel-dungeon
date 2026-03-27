# SacrificialParticle 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/particles/SacrificialParticle.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.effects.particles |
| **文件类型** | class |
| **继承关系** | extends PixelParticle.Shrinking |
| **代码行数** | 68 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现祭献效果的视觉粒子表现，用于渲染祭献仪式、牺牲等场景中的蓝色上升发光粒子动画。

### 系统定位
粒子效果系统中的具体粒子实现类，位于 `effects.particles` 包中，是游戏视觉反馈系统的一部分。

### 不负责什么
- 不负责祭献机制的逻辑实现
- 不负责粒子发射器的创建和管理
- 不负责祭献奖励的计算

## 3. 结构总览

### 主要成员概览
- `FACTORY`：静态工厂常量，用于创建和回收粒子实例

### 主要逻辑块概览
- 构造器：初始化粒子颜色、存活时间和向上加速度
- `reset()`：重置粒子状态
- `update()`：更新粒子透明度

### 生命周期/调用时机
粒子存活时间为 0.6 秒，由 `Emitter` 通过 `FACTORY.emit()` 方法创建并发射。

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

### 使用者
- 祭献相关的视觉效果场景
- 通过 `Emitter` 类使用 `SacrificialParticle.FACTORY` 发射粒子

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
public SacrificialParticle() {
    super();

    color( 0x4488EE );  // 蓝色
    lifespan = 0.6f;

    acc.set( 0, -100 );  // 向上加速度（较强）
}
```

### 初始化注意事项
- 粒子颜色固定为 `0x4488EE`（蓝色）
- 存活时间固定为 0.6 秒
- 加速度为 `(0, -100)`，较强的向上漂浮效果

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
((SacrificialParticle)emitter.recycle( SacrificialParticle.class )).reset( x, y );
```

### FACTORY.lightMode()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.lightMode()`

**方法职责**：指示粒子使用光照模式渲染。

**返回值**：boolean，固定返回 `true`

**说明**：光照模式使粒子产生发光效果。

### SacrificialParticle()

**可见性**：public

**是否覆写**：否

**方法职责**：构造器，初始化粒子的颜色、存活时间和向上加速度。

### reset()

**可见性**：public

**是否覆写**：否

**方法职责**：重置粒子状态，设置位置、大小和存活时间。

**参数**：
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
public void reset( float x, float y ) {
    revive();

    this.x = x;
    this.y = y - 4;  // Y坐标向上偏移4像素

    left = lifespan;

    size = 4;
    speed.set( 0 );  // 无初速度
}
```

**边界情况**：
- Y 坐标向上偏移 4 像素
- 粒子大小为 4
- 无初速度，仅受向上加速度影响

### update()

**可见性**：public

**是否覆写**：是，覆写自 `PixelParticle.Shrinking.update()`

**方法职责**：更新粒子状态，实现淡入效果。

**核心实现逻辑**：
```java
@Override
public void update() {
    super.update();
    float p = left / lifespan;
    am = p > 0.75f ? (1 - p) * 4 : 1;  // 初期淡入
}
```

**边界情况**：
- 当 `p > 0.75f` 时（存活初期约前 25%），透明度从 0 渐变到 1
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
当需要显示祭献效果时，由 `Emitter` 调用 `FACTORY.emit()` 创建。

### 调用者
- `Emitter` 类通过 `pour()` 或 `burst()` 方法调用 `FACTORY.emit()`

### 被调用者
- `Emitter.recycle()`：回收或创建粒子

### 系统流程位置
```
游戏事件（如祭献仪式）
    → 创建 Emitter
    → Emitter.pour(SacrificialParticle.FACTORY, ...)
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
// 创建祭献效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.pour(SacrificialParticle.FACTORY, 0.05f);
scene.add(emitter);
```

### 爆发效果
```java
// 瞬间发射多个祭献粒子
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.burst(SacrificialParticle.FACTORY, 15);
scene.add(emitter);
```

## 12. 开发注意事项

### 状态依赖
- 依赖父类 `PixelParticle.Shrinking` 的 `update()` 方法实现自动缩小
- 透明度计算依赖 `left` 和 `lifespan` 字段

### 生命周期耦合
- 粒子生命周期由 `lifespan` 控制，结束后自动调用 `kill()`

### 常见陷阱
- Y 坐标会向上偏移 4 像素，实际位置与参数不同
- 加速度为负值表示向上运动，值为 -100 较强

## 13. 修改建议与扩展点

### 适合扩展的位置
- 构造器中的颜色值可修改
- `reset()` 中的 Y 偏移量可调整
- `update()` 中的透明度变化逻辑可自定义

### 不建议修改的位置
- `FACTORY` 的基本结构应保持不变

### 重构建议
如需支持不同颜色的祭献效果，可考虑添加颜色参数或创建子类。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是，仅 `FACTORY` 一个静态字段
- [x] 是否已覆盖全部方法：是，包括构造器、`reset()`、`update()` 和工厂方法
- [x] 是否已检查继承链与覆写关系：是，继承 `PixelParticle.Shrinking`，覆写 `update()`
- [x] 是否已核对官方中文翻译：不适用，无文本内容
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：无关联
- [x] 是否明确说明了注意事项与扩展点：是