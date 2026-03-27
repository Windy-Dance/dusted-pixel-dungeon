# ShaftParticle 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/particles/ShaftParticle.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.effects.particles |
| **文件类型** | class |
| **继承关系** | extends PixelParticle |
| **代码行数** | 70 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现光柱效果的视觉粒子表现，用于渲染光柱、神圣效果等场景中的向上发光粒子动画。

### 系统定位
粒子效果系统中的具体粒子实现类，位于 `effects.particles` 包中，是游戏视觉反馈系统的一部分。

### 不负责什么
- 不负责光柱机制的逻辑实现
- 不负责粒子发射器的创建和管理
- 不负责伤害或效果计算

## 3. 结构总览

### 主要成员概览
- `FACTORY`：静态工厂常量，用于创建和回收粒子实例
- `offs`：私有实例字段，偏移量

### 主要逻辑块概览
- 构造器：初始化粒子存活时间和向上速度
- `reset()`：重置粒子状态
- `update()`：更新粒子透明度和缩放

### 生命周期/调用时机
粒子存活时间为 1.2 秒，由 `Emitter` 通过 `FACTORY.emit()` 方法创建并发射。

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
- `update()`：自定义透明度和缩放变化逻辑

### 实现的接口契约
无显式接口实现。

### 依赖的关键类
- `com.watabou.noosa.particles.Emitter`：粒子发射器
- `com.watabou.noosa.particles.Emitter.Factory`：工厂接口
- `com.watabou.noosa.particles.PixelParticle`：粒子基类
- `com.watabou.utils.Random`：随机数工具

### 使用者
- 光柱相关的视觉效果场景
- 通过 `Emitter` 类使用 `ShaftParticle.FACTORY` 发射粒子

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `FACTORY` | Emitter.Factory | 匿名实现 | 粒子工厂，`lightMode()` 返回 `true` |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `offs` | float | 未初始化 | 偏移量，用于随机化粒子起始时间 |

## 6. 构造与初始化机制

### 构造器
```java
public ShaftParticle() {
    super();
    
    lifespan = 1.2f;
    speed.set( 0, -6 );  // 向上速度
}
```

### 初始化注意事项
- 存活时间固定为 1.2 秒
- 速度为 `(0, -6)`，向上运动
- 不设置初始颜色

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
((ShaftParticle)emitter.recycle( ShaftParticle.class )).reset( x, y );
```

### FACTORY.lightMode()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.lightMode()`

**方法职责**：指示粒子使用光照模式渲染。

**返回值**：boolean，固定返回 `true`

### ShaftParticle()

**可见性**：public

**是否覆写**：否

**方法职责**：构造器，初始化粒子的存活时间和向上速度。

### reset()

**可见性**：public

**是否覆写**：否

**方法职责**：重置粒子状态，设置位置和随机偏移。

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
    
    offs = -Random.Float( lifespan );  // 随机偏移
    left = lifespan - offs;
}
```

**边界情况**：
- `offs` 为随机负值，范围 `[-lifespan, 0]`
- `left` 为 `lifespan - offs`，范围 `[lifespan, 2*lifespan]`
- 实现了粒子起始时间的随机化

### update()

**可见性**：public

**是否覆写**：是，覆写自 `PixelParticle.update()`

**方法职责**：更新粒子状态，实现透明度和缩放的动态变化。

**核心实现逻辑**：
```java
@Override
public void update() {
    super.update();
    
    float p = left / lifespan;
    am = p < 0.5f ? p : 1 - p;  // 先增后减
    scale.x = (1 - p) * 4;  // X缩放渐变
    scale.y = 16 + (1 - p) * 16;  // Y缩放渐变
}
```

**边界情况**：
- 透明度：前半段增加，后半段减少，呈菱形变化
- X 缩放：从 4 渐变到 0
- Y 缩放：从 32 渐变到 16
- 粒子呈现垂直拉伸的效果

## 8. 对外暴露能力

### 显式 API
- `FACTORY`：静态工厂，用于发射粒子
- `reset(float x, float y)`：重置粒子状态

### 内部辅助方法
无。

### 扩展入口
- 可通过覆写 `update()` 方法自定义缩放变化逻辑
- 可通过修改构造器中的速度值改变粒子速度

## 9. 运行机制与调用链

### 创建时机
当需要显示光柱效果时，由 `Emitter` 调用 `FACTORY.emit()` 创建。

### 调用者
- `Emitter` 类通过 `pour()` 或 `burst()` 方法调用 `FACTORY.emit()`

### 被调用者
- `Emitter.recycle()`：回收或创建粒子
- `Random.Float()`：生成随机数

### 系统流程位置
```
游戏事件（如光柱效果）
    → 创建 Emitter
    → Emitter.pour(ShaftParticle.FACTORY, ...)
    → FACTORY.emit() → reset()
    → 每帧调用 update() 更新透明度和缩放
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
// 创建光柱效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.pour(ShaftParticle.FACTORY, 0.05f);
scene.add(emitter);
```

### 爆发效果
```java
// 瞬间发射多个光柱粒子
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.burst(ShaftParticle.FACTORY, 10);
scene.add(emitter);
```

## 12. 开发注意事项

### 状态依赖
- 不继承 `PixelParticle.Shrinking`，使用 `scale` 控制大小
- `offs` 字段影响粒子的起始时间

### 生命周期耦合
- 粒子生命周期由 `lifespan` 控制
- 缩放变化与生命周期同步

### 常见陷阱
- 注意 `offs` 和 `left` 的关系，实际存活时间可能超过 `lifespan`
- 缩放在 X 和 Y 方向上不同，粒子呈竖直拉伸状

## 13. 修改建议与扩展点

### 适合扩展的位置
- `update()` 中的缩放变化逻辑可自定义
- 构造器中的速度值可调整

### 不建议修改的位置
- `offs` 的计算逻辑，以保证粒子时间随机化的正确性

### 重构建议
如需支持不同大小的光柱效果，可考虑添加缩放参数。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是，`FACTORY` 静态字段和 `offs` 实例字段
- [x] 是否已覆盖全部方法：是，包括构造器、`reset()`、`update()` 和工厂方法
- [x] 是否已检查继承链与覆写关系：是，继承 `PixelParticle`，覆写 `update()`
- [x] 是否已核对官方中文翻译：不适用，无文本内容
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：无关联
- [x] 是否明确说明了注意事项与扩展点：是