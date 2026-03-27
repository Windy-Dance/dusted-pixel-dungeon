# SparkParticle 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/particles/SparkParticle.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.effects.particles |
| **文件类型** | class |
| **继承关系** | extends PixelParticle |
| **代码行数** | 108 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现电火花效果的视觉粒子表现，用于渲染电击、闪电等场景中的发光粒子动画。

### 系统定位
粒子效果系统中的具体粒子实现类，位于 `effects.particles` 包中，是游戏视觉反馈系统的一部分。

### 不负责什么
- 不负责电击伤害的计算或应用
- 不负责粒子发射器的创建和管理
- 不负责闪电链的逻辑处理

## 3. 结构总览

### 主要成员概览
- `FACTORY`：静态工厂常量，用于标准火花效果
- `STATIC`：静态工厂常量，用于静态火花效果

### 主要逻辑块概览
- 构造器：初始化粒子大小和向下加速度
- `reset()`：重置标准火花粒子状态
- `resetStatic()`：重置静态火花粒子状态
- `resetAttracting()`：重定向粒子朝目标移动
- `setMaxSize()`：设置最大粒子大小
- `update()`：更新粒子大小

### 生命周期/调用时机
- 标准模式：粒子存活 `[0.5, 1.0]` 秒（随机）
- 静态模式：粒子存活 `[0.25, 0.5]` 秒（随机）
- 吸引模式：粒子存活 `[0.2, 0.35]` 秒（随机）

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
- `size(float)`：设置大小
- `update()`：更新逻辑

### 覆写的方法
- `update()`：自定义大小变化逻辑

### 实现的接口契约
无显式接口实现。

### 依赖的关键类
- `com.watabou.noosa.particles.Emitter`：粒子发射器
- `com.watabou.noosa.particles.Emitter.Factory`：工厂接口
- `com.watabou.noosa.particles.PixelParticle`：粒子基类
- `com.watabou.noosa.Visual`：视觉组件基类
- `com.watabou.utils.Random`：随机数工具
- `com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap`：地砖映射

### 使用者
- 电击相关的视觉效果场景
- 通过 `Emitter` 类使用工厂发射粒子

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `FACTORY` | Emitter.Factory | 匿名实现 | 标准火花粒子工厂，`lightMode()` 返回 `true` |
| `STATIC` | Emitter.Factory | 匿名实现 | 静态火花粒子工厂，`lightMode()` 返回 `true` |

### 实例字段
无显式实例字段，所有字段继承自父类。

## 6. 构造与初始化机制

### 构造器
```java
public SparkParticle() {
    super();
    
    size( 2 );
    
    acc.set( 0, +50 );  // 向下加速度
}
```

### 初始化注意事项
- 粒子大小初始为 2
- 加速度为 `(0, +50)`，模拟重力效果

## 7. 方法详解

### FACTORY.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建标准火花粒子实例。

**参数**：
- `emitter` (Emitter)：粒子发射器
- `index` (int)：粒子索引
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
((SparkParticle)emitter.recycle( SparkParticle.class )).reset( x, y );
```

### FACTORY.lightMode()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.lightMode()`

**方法职责**：指示粒子使用光照模式渲染。

**返回值**：boolean，固定返回 `true`

### STATIC.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建静态火花粒子实例。

**核心实现逻辑**：
```java
((SparkParticle)emitter.recycle( SparkParticle.class )).resetStatic( x, y );
```

### STATIC.lightMode()

**可见性**：public（匿名类覆写）

**返回值**：boolean，固定返回 `true`

### SparkParticle()

**可见性**：public

**是否覆写**：否

**方法职责**：构造器，初始化粒子的大小和向下加速度。

### reset()

**可见性**：public

**是否覆写**：否

**方法职责**：重置标准火花粒子状态，设置位置、大小、存活时间和向上半圆方向的速度。

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
    size = 5;
    
    left = lifespan = Random.Float( 0.5f, 1.0f );
    
    speed.polar( -Random.Float( 3.1415926f ), Random.Float( 20, 40 ) );
}
```

**边界情况**：
- 存活时间为随机值，范围 `[0.5, 1.0]` 秒
- 速度方向为向上半圆（负角度）
- 速度大小为 `[20, 40]`

### resetStatic()

**可见性**：public

**是否覆写**：否

**方法职责**：重置静态火花粒子状态，无速度和加速度。

**参数**：
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
public void resetStatic( float x, float y){
    reset(x, y);
    
    left = lifespan = Random.Float( 0.25f, 0.5f );
    
    acc.set( 0, 0 );
    speed.set( 0, 0 );
}
```

**边界情况**：
- 调用 `reset()` 后覆盖加速度和速度
- 存活时间较短，范围 `[0.25, 0.5]` 秒

### resetAttracting()

**可见性**：public

**是否覆写**：否

**方法职责**：重定向粒子朝目标视觉组件移动。

**参数**：
- `x` (float)：X 坐标
- `y` (float)：Y 坐标
- `attracting` (Visual)：目标视觉组件

**返回值**：void

**核心实现逻辑**：
```java
public void resetAttracting( float x, float y, Visual attracting){
    reset(x, y);

    left = lifespan = Random.Float( 0.2f, 0.35f );

    acc.set(0);
    speed.set((attracting.x + attracting.width / 2f) - x,
            (attracting.y + attracting.height / 2f) - y);
    speed.normalize().scale(DungeonTilemap.SIZE * 3f);

    //offset the particles slightly so they don't go too far outside of the cell
    this.x -= speed.x / 8f;
    this.y -= speed.y / 8f;
}
```

**边界情况**：
- 速度指向目标中心
- 速度大小为 `DungeonTilemap.SIZE * 3`（约 48 像素）
- 位置稍微偏移以避免粒子超出格子

### setMaxSize()

**可见性**：public

**是否覆写**：否

**方法职责**：设置粒子的最大大小。

**参数**：
- `value` (float)：最大大小值

**返回值**：void

**核心实现逻辑**：
```java
public void setMaxSize( float value ){
    size = value;
}
```

### update()

**可见性**：public

**是否覆写**：是，覆写自 `PixelParticle.update()`

**方法职责**：更新粒子状态，实现随机大小变化。

**核心实现逻辑**：
```java
@Override
public void update() {
    super.update();
    size( Random.Float( size * left / lifespan ) );
}
```

**边界情况**：
- 大小随机变化，最大值为 `size * left / lifespan`
- 大小随时间减少

## 8. 对外暴露能力

### 显式 API
- `FACTORY`：静态工厂，用于标准火花效果
- `STATIC`：静态工厂，用于静态火花效果
- `reset(float x, float y)`：重置标准粒子
- `resetStatic(float x, float y)`：重置静态粒子
- `resetAttracting(float x, float y, Visual attracting)`：重定向粒子朝目标移动
- `setMaxSize(float value)`：设置最大粒子大小

### 内部辅助方法
无。

### 扩展入口
- 可通过覆写 `update()` 方法自定义大小变化逻辑
- 可创建新的 Factory 实现不同的发射模式

## 9. 运行机制与调用链

### 创建时机
当需要显示电火花效果时，由 `Emitter` 调用工厂方法创建。

### 调用者
- `Emitter` 类通过 `pour()` 或 `burst()` 方法调用工厂方法

### 被调用者
- `Emitter.recycle()`：回收或创建粒子
- `Random.Float()`：生成随机数
- `PointF.polar()`：极坐标转换

### 系统流程位置
```
游戏事件（如电击）
    → 创建 Emitter
    → Emitter.burst(SparkParticle.FACTORY, ...)
    → FACTORY.emit() → reset()
    → 每帧调用 update() 更新大小
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
// 创建标准火花效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.burst(SparkParticle.FACTORY, 15);
scene.add(emitter);
```

### 静态火花
```java
// 创建静态火花效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.pour(SparkParticle.STATIC, 0.05f);
scene.add(emitter);
```

### 吸引效果
```java
// 创建朝目标移动的火花效果
SparkParticle p = (SparkParticle) emitter.recycle(SparkParticle.class);
p.resetAttracting(x, y, targetVisual);
```

## 12. 开发注意事项

### 状态依赖
- 不继承 `PixelParticle.Shrinking`，需手动管理大小变化
- 大小每帧随机变化

### 生命周期耦合
- 不同模式的存活时间不同
- 吸引模式会覆盖速度和加速度

### 常见陷阱
- 注意 `resetStatic()` 和 `resetAttracting()` 会调用 `reset()` 后覆盖部分参数
- 大小每帧随机，视觉上呈现闪烁效果

## 13. 修改建议与扩展点

### 适合扩展的位置
- `update()` 中的大小变化逻辑可自定义
- 可创建新的 Factory 支持不同的发射模式

### 不建议修改的位置
- 吸引模式的速度计算逻辑，以保证正确指向目标

### 重构建议
`resetAttracting()` 方法较为复杂，可考虑提取为独立的方法或类。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是，`FACTORY` 和 `STATIC` 两个静态字段
- [x] 是否已覆盖全部方法：是，包括构造器、三个 reset 方法、`setMaxSize()`、`update()` 和工厂方法
- [x] 是否已检查继承链与覆写关系：是，继承 `PixelParticle`，覆写 `update()`
- [x] 是否已核对官方中文翻译：不适用，无文本内容
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：无关联
- [x] 是否明确说明了注意事项与扩展点：是