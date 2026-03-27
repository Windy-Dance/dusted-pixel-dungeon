# EarthParticle 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/particles/EarthParticle.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.effects.particles |
| **文件类型** | class |
| **继承关系** | extends PixelParticle |
| **代码行数** | 99 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现泥土/岩石效果的视觉粒子表现，用于渲染地震、岩石技能等场景中的土色粒子动画。

### 系统定位
粒子效果系统中的具体粒子实现类，位于 `effects.particles` 包中，是游戏视觉反馈系统的一部分。

### 不负责什么
- 不负责地震或岩石技能的逻辑实现
- 不负责粒子发射器的创建和管理
- 不负责地形破坏的物理计算

## 3. 结构总览

### 主要成员概览
- `FACTORY`：静态工厂常量，标准泥土粒子效果
- `SMALL`：静态工厂常量，小型泥土粒子效果
- `FALLING`：静态工厂常量，下落式泥土粒子效果

### 主要逻辑块概览
- 构造器：初始化粒子颜色和角度
- `reset()`：重置标准粒子状态
- `resetSmall()`：重置小型粒子状态
- `resetFalling()`：重置下落式粒子状态
- `update()`：更新粒子大小变化

### 生命周期/调用时机
- 标准模式：粒子存活 0.5 秒
- 小型模式：粒子存活 1.0 秒
- 下落模式：粒子存活 1.0 秒

## 4. 继承与协作关系

### 父类提供的能力
继承自 `PixelParticle`：
- `size`：粒子大小（protected）
- `lifespan`：总存活时间（protected）
- `left`：剩余存活时间（protected）
- `x`, `y`：位置坐标（继承自 PseudoPixel）
- `speed`：速度向量（继承自 PseudoPixel）
- `acc`：加速度向量（继承自 PseudoPixel）
- `angularSpeed`：角速度（继承自 Visual）
- `angle`：角度（继承自 Visual）
- `am`：透明度（继承自 PseudoPixel）
- `revive()`：复活粒子
- `color(int)`：设置颜色
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
- `com.watabou.utils.ColorMath`：颜色计算工具
- `com.watabou.utils.Random`：随机数工具

### 使用者
- 地震、岩石相关技能的视觉效果
- 通过 `Emitter` 类使用工厂发射粒子

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `FACTORY` | Emitter.Factory | 匿名实现 | 标准泥土粒子工厂，调用 `reset()` |
| `SMALL` | Emitter.Factory | 匿名实现 | 小型泥土粒子工厂，调用 `resetSmall()` |
| `FALLING` | Emitter.Factory | 匿名实现 | 下落式泥土粒子工厂，调用 `resetFalling()` |

### 实例字段
无显式实例字段，所有字段继承自父类。

## 6. 构造与初始化机制

### 构造器
```java
public EarthParticle() {
    super();
    
    color( ColorMath.random( 0x444444, 0x777766 ) );  // 土灰色随机
    angle = Random.Float( -30, 30 );  // 随机倾斜角度
}
```

### 初始化注意事项
- 粒子颜色为随机土灰色，范围 `0x444444` 到 `0x777766`
- 初始角度为随机值 `[-30, 30]` 度
- 不设置存活时间，由各 `reset*()` 方法设置

## 7. 方法详解

### FACTORY.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建标准泥土粒子实例。

**参数**：
- `emitter` (Emitter)：粒子发射器
- `index` (int)：粒子索引
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
((EarthParticle)emitter.recycle( EarthParticle.class )).reset( x,  y );
```

### SMALL.emit()

**可见性**：public（匿名类覆写）

**方法职责**：通过发射器回收或创建小型泥土粒子实例。

**核心实现逻辑**：
```java
((EarthParticle)emitter.recycle( EarthParticle.class )).resetSmall( x,  y );
```

### FALLING.emit()

**可见性**：public（匿名类覆写）

**方法职责**：通过发射器回收或创建下落式泥土粒子实例。

**核心实现逻辑**：
```java
((EarthParticle)emitter.recycle( EarthParticle.class )).resetFalling( x,  y );
```

### EarthParticle()

**可见性**：public

**是否覆写**：否

**方法职责**：构造器，初始化粒子的颜色和角度。

### reset()

**可见性**：public

**是否覆写**：否

**方法职责**：重置标准泥土粒子状态，静态无速度效果。

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

    left = lifespan = 0.5f;
    size = 16;

    acc.y = 0;
    speed.y = 0;
    angularSpeed = 0;
}
```

**边界情况**：
- 粒子大小为 16
- 无加速度、无速度、无角速度（静态效果）

### resetSmall()

**可见性**：public

**是否覆写**：否

**方法职责**：重置小型泥土粒子状态。

**参数**：
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
public void resetSmall( float x, float y ) {
    reset(x, y);

    left = lifespan = 1f;
    size = 8;  // 较小尺寸
}
```

**边界情况**：
- 存活时间为 1 秒
- 大小为 8（标准模式的一半）

### resetFalling()

**可见性**：public

**是否覆写**：否

**方法职责**：重置下落式泥土粒子状态，具有初始向上速度和向下加速度。

**参数**：
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
public void resetFalling( float x, float y ) {
    reset(x, y);

    left = lifespan = 1f;
    size = 8;

    acc.y = 30;  // 向下加速度
    speed.y = -5;  // 初始向上速度
    angularSpeed = Random.Float(-90, 90);  // 随机旋转
}
```

**边界情况**：
- 初始速度为向上 5 像素/秒
- 加速度为向下 30 像素/秒²
- 随机角速度 `[-90, 90]` 度/秒

### update()

**可见性**：public

**是否覆写**：是，覆写自 `PixelParticle.update()`

**方法职责**：更新粒子状态，实现大小先增后减的效果。

**核心实现逻辑**：
```java
@Override
public void update() {
    super.update();
    
    float p = left / lifespan;
    size( (p < 0.5f ? p : 1 - p) * size );  // 先增后减
}
```

**边界情况**：
- 前半段：大小从 0 渐变到最大值
- 后半段：大小从最大值渐变到 0
- 实现呼吸式的效果

## 8. 对外暴露能力

### 显式 API
- `FACTORY`：静态工厂，标准泥土粒子
- `SMALL`：静态工厂，小型泥土粒子
- `FALLING`：静态工厂，下落式泥土粒子
- `reset(float x, float y)`：重置标准粒子
- `resetSmall(float x, float y)`：重置小型粒子
- `resetFalling(float x, float y)`：重置下落式粒子

### 内部辅助方法
无。

### 扩展入口
- 可通过覆写 `update()` 方法自定义大小变化逻辑
- 可通过创建新的 Factory 实现不同的发射模式

## 9. 运行机制与调用链

### 创建时机
当需要显示泥土/岩石效果时，由 `Emitter` 调用相应工厂方法创建。

### 调用者
- `Emitter` 类通过 `pour()` 或 `burst()` 方法调用工厂方法

### 被调用者
- `Emitter.recycle()`：回收或创建粒子
- `ColorMath.random()`：随机颜色
- `Random.Float()`：生成随机数

### 系统流程位置
```
游戏事件（如地震技能）
    → 创建 Emitter
    → Emitter.burst(EarthParticle.FALLING, ...)
    → FALLING.emit() → resetFalling()
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
// 标准泥土效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.burst(EarthParticle.FACTORY, 10);
scene.add(emitter);
```

### 下落效果
```java
// 下落式泥土效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.pour(EarthParticle.FALLING, 0.1f);
scene.add(emitter);
```

## 12. 开发注意事项

### 状态依赖
- 不继承 `PixelParticle.Shrinking`，需手动管理大小变化
- 大小变化与生命周期同步，呈现呼吸效果

### 生命周期耦合
- 不同模式的存活时间不同
- `resetSmall()` 和 `resetFalling()` 会覆盖 `reset()` 设置的参数

### 常见陷阱
- 注意粒子有角度属性，视觉上会倾斜
- `resetSmall()` 调用 `reset()` 后会覆盖部分参数

## 13. 修改建议与扩展点

### 适合扩展的位置
- 构造器中的颜色范围可调整
- `update()` 中的大小变化逻辑可自定义
- 各 `reset*()` 方法中的参数可调整

### 不建议修改的位置
- 大小呼吸效果应保持连贯性

### 重构建议
三种模式可考虑使用枚举参数统一，减少方法数量。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是，`FACTORY`、`SMALL`、`FALLING` 三个静态字段
- [x] 是否已覆盖全部方法：是，包括构造器、三个 reset 方法、`update()` 和工厂方法
- [x] 是否已检查继承链与覆写关系：是，继承 `PixelParticle`，覆写 `update()`
- [x] 是否已核对官方中文翻译：不适用，无文本内容
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：无关联
- [x] 是否明确说明了注意事项与扩展点：是