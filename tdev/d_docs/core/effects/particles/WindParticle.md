# WindParticle 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/particles/WindParticle.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.effects.particles |
| **文件类型** | class (含内部类 Wind) |
| **继承关系** | extends PixelParticle; 内部类 Wind extends Emitter |
| **代码行数** | 101 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现风效果的视觉粒子表现，用于渲染风吹过等场景中的粒子动画。包含专用的 `Wind` 内部类用于地砖级别的风效果管理。

### 系统定位
粒子效果系统中的具体粒子实现类，位于 `effects.particles` 包中，是游戏视觉反馈系统的一部分。

### 不负责什么
- 不负责风机制的逻辑实现
- 不负责粒子发射器的创建和管理
- 不负责角色推动效果

## 3. 结构总览

### 主要成员概览
- `FACTORY`：静态工厂常量，用于创建和回收粒子实例
- `angle`：静态字段，全局风向角度
- `speed`：静态字段，全局风速向量
- `Wind`：内部类，用于地砖级别的风发射器

### 主要逻辑块概览
- 构造器：初始化粒子存活时间、大小和缩放
- `reset()`：重置粒子状态
- `update()`：更新粒子透明度
- `Wind` 内部类：管理特定地砖的风效果

### 生命周期/调用时机
粒子存活时间为 `[1, 2]` 秒（随机），由 `Emitter` 通过 `FACTORY.emit()` 方法创建并发射。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `PixelParticle`：
- `size`：粒子大小（protected）
- `lifespan`：总存活时间（protected）
- `left`：剩余存活时间（protected）
- `x`, `y`：位置坐标（继承自 PseudoPixel）
- `speed`：速度向量（继承自 PseudoPixel）
- `scale`：缩放向量（继承自 Visual）
- `am`：透明度（继承自 PseudoPixel）
- `revive()`：复活粒子
- `size(float)`：设置大小
- `update()`：更新逻辑

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
- `com.shatteredpixel.shatteredpixeldungeon.Dungeon`：游戏状态
- `com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap`：地砖映射

### 使用者
- 风相关的视觉效果场景
- 通过 `Wind` 内部类或 `Emitter` 类使用粒子

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `FACTORY` | Emitter.Factory | 匿名实现 | 粒子工厂，调用 `reset()` |

### 静态字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `angle` | float | 随机 | 全局风向角度（弧度） |
| `speed` | PointF | 极坐标计算 | 全局风速向量 |

### 实例字段
无显式实例字段，所有字段继承自父类。

## 6. 构造与初始化机制

### 构造器
```java
public WindParticle() {
    super();
    
    lifespan = Random.Float( 1, 2 );
    scale.set( size = Random.Float( 3 ) );
}
```

### 初始化注意事项
- 存活时间为随机值，范围 `[1, 2]` 秒
- 大小为随机值，范围 `[0, 3]`
- 缩放与大小相同

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
((WindParticle)emitter.recycle( WindParticle.class )).reset( x, y );
```

### WindParticle()

**可见性**：public

**是否覆写**：否

**方法职责**：构造器，初始化粒子的存活时间、大小和缩放。

### reset()

**可见性**：public

**是否覆写**：否

**方法职责**：重置粒子状态，设置反向起始位置并更新全局风向。

**参数**：
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
public void reset( float x, float y ) {
    revive();
    
    left = lifespan;
    
    super.speed.set( WindParticle.speed );
    super.speed.scale( size );
    
    this.x = x - super.speed.x * lifespan / 2;
    this.y = y - super.speed.y * lifespan / 2;
    
    angle += Random.Float( -0.1f, +0.1f );  // 微调全局风向
    speed = new PointF().polar( angle, 5 );
    
    am = 0;
}
```

**边界情况**：
- 起始位置反向计算，粒子从一侧移动到另一侧
- 全局风向每次发射微调 `[-0.1, +0.1]` 弧度
- 初始透明度为 0，由 `update()` 逐渐增加

### update()

**可见性**：public

**是否覆写**：是，覆写自 `PixelParticle.update()`

**方法职责**：更新粒子状态，实现透明度变化。

**核心实现逻辑**：
```java
@Override
public void update() {
    super.update();
    
    float p = left / lifespan;
    am = (p < 0.5f ? p : 1 - p) * size * 0.2f;  // 先增后减
}
```

**边界情况**：
- 透明度：前半段增加，后半段减少，呈菱形变化
- 最大透明度与粒子大小相关

## 8. 对外暴露能力

### 显式 API
- `FACTORY`：静态工厂，用于发射粒子
- `reset(float x, float y)`：重置粒子状态
- `Wind`：内部类，用于地Tile级别的风效果

### 内部辅助方法
无。

### 扩展入口
- 可通过覆写 `update()` 方法自定义透明度变化逻辑
- `Wind` 内部类可作为参考创建其他地Tile级别的效果

## 9. 运行机制与调用链

### 创建时机
当需要显示风效果时，由 `Emitter` 或 `Wind` 调用 `FACTORY.emit()` 创建。

### 调用者
- `Emitter` 类通过 `pour()` 或 `burst()` 方法调用 `FACTORY.emit()`
- `Wind` 内部类通过 `pour()` 方法调用 `FACTORY.emit()`

### 被调用者
- `Emitter.recycle()`：回收或创建粒子
- `Random.Float()`：生成随机数
- `PointF.polar()`：极坐标转换

### 系统流程位置
```
游戏场景加载（风地砖）
    → 创建 Wind 发射器
    → Wind.pour(FACTORY, 2.5f)
    → FACTORY.emit() → reset()
    → 粒子从一侧移动到另一侧
    → 每帧调用 update() 更新透明度
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
// 创建风效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.pour(WindParticle.FACTORY, 0.2f);
scene.add(emitter);
```

### 使用 Wind 内部类
```java
// 创建地Tile级别的风效果
Wind wind = new Wind(tilePos);  // tilePos为地砖位置
scene.add(wind);
```

## 12. 开发注意事项

### 状态依赖
- 不继承 `PixelParticle.Shrinking`，粒子大小不变
- 全局风向 `angle` 和 `speed` 是静态变量，影响所有粒子

### 生命周期耦合
- 粒子生命周期由 `lifespan` 控制
- `Wind` 内部类会在不可见时停止更新

### 常见陷阱
- 注意全局风向会随着每次 `reset()` 微调
- 粒子使用全局风速，方向会动态变化
- `Wind` 内部类会检查 `heroFOV` 来决定是否更新

## 13. 修改建议与扩展点

### 适合扩展的位置
- `update()` 中的透明度变化逻辑可自定义
- `Wind` 内部类可扩展为其他地Tile效果

### 不建议修改的位置
- 全局风向机制，以保证风效果的自然变化

### 重构建议
`Wind` 内部类可作为模板创建其他地Tile级别的粒子效果。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是，`FACTORY`、`angle`、`speed` 静态字段
- [x] 是否已覆盖全部方法：是，包括构造器、`reset()`、`update()` 和工厂方法，以及 `Wind` 内部类
- [x] 是否已检查继承链与覆写关系：是，继承 `PixelParticle`，覆写 `update()`
- [x] 是否已核对官方中文翻译：不适用，无文本内容
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：无关联
- [x] 是否明确说明了注意事项与扩展点：是