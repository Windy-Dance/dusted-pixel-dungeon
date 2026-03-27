# LeafParticle 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/particles/LeafParticle.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.effects.particles |
| **文件类型** | class |
| **继承关系** | extends PixelParticle.Shrinking |
| **代码行数** | 73 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现树叶效果的视觉粒子表现，用于渲染树叶飘落、植物相关场景中的绿色粒子动画。

### 系统定位
粒子效果系统中的具体粒子实现类，位于 `effects.particles` 包中，是游戏视觉反馈系统的一部分。

### 不负责什么
- 不负责植物机制的逻辑实现
- 不负责粒子发射器的创建和管理
- 不负责地形或植被的处理

## 3. 结构总览

### 主要成员概览
- `color1`：静态字段，颜色参数1
- `color2`：静态字段，颜色参数2
- `GENERAL`：静态工厂常量，通用树叶效果
- `LEVEL_SPECIFIC`：静态工厂常量，关卡特定颜色树叶效果

### 主要逻辑块概览
- 构造器：初始化粒子存活时间和向下加速度
- `reset()`：重置粒子状态

### 生命周期/调用时机
粒子存活时间为 1.2 秒，由 `Emitter` 通过工厂方法创建并发射。

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
- `com.shatteredpixel.shatteredpixeldungeon.Dungeon`：游戏状态

### 使用者
- 树叶相关的视觉效果场景
- 通过 `Emitter` 类使用工厂发射粒子

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `GENERAL` | Emitter.Factory | 匿名实现 | 通用树叶工厂，颜色范围 `0x004400` 到 `0x88CC44` |
| `LEVEL_SPECIFIC` | Emitter.Factory | 匿名实现 | 关卡特定颜色树叶工厂，使用 `Dungeon.level.color1` 和 `color2` |

### 静态字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `color1` | int | 未初始化 | 颜色参数1（未在当前类中使用） |
| `color2` | int | 未初始化 | 颜色参数2（未在当前类中使用） |

### 实例字段
无显式实例字段，所有字段继承自父类。

## 6. 构造与初始化机制

### 构造器
```java
public LeafParticle() {
    super();
    
    lifespan = 1.2f;
    acc.set( 0, 25 );  // 向下加速度
}
```

### 初始化注意事项
- 存活时间固定为 1.2 秒
- 加速度为 `(0, 25)`，模拟重力效果
- 颜色在工厂方法中设置，不在构造器中设置

## 7. 方法详解

### GENERAL.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建通用颜色树叶粒子实例。

**参数**：
- `emitter` (Emitter)：粒子发射器
- `index` (int)：粒子索引
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
LeafParticle p = ((LeafParticle)emitter.recycle( LeafParticle.class ));
p.color( ColorMath.random( 0x004400, 0x88CC44 ) );  // 随机绿色
p.reset( x, y );
```

### LEVEL_SPECIFIC.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建关卡特定颜色树叶粒子实例。

**核心实现逻辑**：
```java
LeafParticle p = ((LeafParticle)emitter.recycle( LeafParticle.class ));
p.color( ColorMath.random( Dungeon.level.color1, Dungeon.level.color2 ) );
p.reset( x, y );
```

**边界情况**：
- 使用 `Dungeon.level.color1` 和 `color2` 作为颜色范围
- 不同关卡可能有不同的树叶颜色

### LeafParticle()

**可见性**：public

**是否覆写**：否

**方法职责**：构造器，初始化粒子的存活时间和加速度。

### reset()

**可见性**：public

**是否覆写**：否

**方法职责**：重置粒子状态，设置位置、速度和大小。

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
    
    speed.set( Random.Float( -8, +8 ), -20 );  // 向上初速度，左右随机
    
    left = lifespan;
    size = Random.Float( 2, 3 );  // 随机大小
}
```

**边界情况**：
- X 方向速度：`[-8, +8]`（随机左右漂移）
- Y 方向速度：`-20`（向上初速度，之后受重力下落）
- 大小：`[2, 3]`（随机）

## 8. 对外暴露能力

### 显式 API
- `GENERAL`：静态工厂，通用绿色树叶效果
- `LEVEL_SPECIFIC`：静态工厂，关卡特定颜色树叶效果
- `color1`, `color2`：静态字段，颜色参数
- `reset(float x, float y)`：重置粒子状态

### 内部辅助方法
无。

### 扩展入口
- 可通过设置 `color1` 和 `color2` 字段自定义颜色
- 可创建新的 Factory 实现不同的颜色策略

## 9. 运行机制与调用链

### 创建时机
当需要显示树叶效果时，由 `Emitter` 调用工厂方法创建。

### 调用者
- `Emitter` 类通过 `pour()` 或 `burst()` 方法调用工厂方法

### 被调用者
- `Emitter.recycle()`：回收或创建粒子
- `ColorMath.random()`：随机颜色
- `Random.Float()`：生成随机数

### 系统流程位置
```
游戏事件（如植物效果）
    → 创建 Emitter
    → Emitter.pour(LeafParticle.GENERAL, ...)
    → GENERAL.emit() → color() → reset()
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
// 创建通用树叶效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.pour(LeafParticle.GENERAL, 0.1f);
scene.add(emitter);
```

### 关卡特定颜色
```java
// 创建关卡特定颜色树叶效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.burst(LeafParticle.LEVEL_SPECIFIC, 20);
scene.add(emitter);
```

## 12. 开发注意事项

### 状态依赖
- 依赖父类 `PixelParticle.Shrinking` 的 `update()` 方法实现自动缩小
- `LEVEL_SPECIFIC` 依赖 `Dungeon.level` 的 `color1` 和 `color2` 字段

### 生命周期耦合
- 粒子生命周期由 `lifespan` 控制
- 颜色在工厂方法中设置，每个粒子可能有不同颜色

### 常见陷阱
- 注意 `color1` 和 `color2` 静态字段未在类内部使用，仅作为外部接口
- `LEVEL_SPECIFIC` 使用关卡颜色，需确保 `Dungeon.level` 已初始化

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可通过设置 `color1` 和 `color2` 字段传递自定义颜色
- 工厂方法中的颜色范围可调整

### 不建议修改的位置
- 父类 `update()` 行为，以保证粒子正常缩小

### 重构建议
`color1` 和 `color2` 静态字段未在类内部使用，可考虑移除或重新设计用途。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是，`GENERAL`、`LEVEL_SPECIFIC`、`color1`、`color2`
- [x] 是否已覆盖全部方法：是，包括构造器、`reset()` 和工厂方法
- [x] 是否已检查继承链与覆写关系：是，继承 `PixelParticle.Shrinking`，无覆写
- [x] 是否已核对官方中文翻译：不适用，无文本内容
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：无关联
- [x] 是否明确说明了注意事项与扩展点：是