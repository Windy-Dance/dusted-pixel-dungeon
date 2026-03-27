# PitfallParticle 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/particles/PitfallParticle.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.effects.particles |
| **文件类型** | class |
| **继承关系** | extends PixelParticle.Shrinking |
| **代码行数** | 63 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现深坑效果的视觉粒子表现，用于渲染深坑陷阱、塌陷等场景中的黑色粒子动画。

### 系统定位
粒子效果系统中的具体粒子实现类，位于 `effects.particles` 包中，是游戏视觉反馈系统的一部分。

### 不负责什么
- 不负责深坑陷阱的逻辑实现
- 不负责粒子发射器的创建和管理
- 不负责角色坠落或伤害计算

## 3. 结构总览

### 主要成员概览
- `FACTORY4`：静态工厂常量，大小为4的粒子
- `FACTORY8`：静态工厂常量，大小为8的粒子

### 主要逻辑块概览
- 构造器：初始化粒子颜色和随机角度
- `reset()`：重置粒子状态

### 生命周期/调用时机
粒子存活时间为 1 秒，由 `Emitter` 通过工厂方法创建并发射。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `PixelParticle.Shrinking`：
- `size`：粒子大小（protected）
- `lifespan`：总存活时间（protected）
- `left`：剩余存活时间（protected）
- `x`, `y`：位置坐标（继承自 PseudoPixel）
- `angle`：角度（继承自 Visual）
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
- `com.watabou.utils.Random`：随机数工具

### 使用者
- 深坑相关的视觉效果场景
- 通过 `Emitter` 类使用工厂发射粒子

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `FACTORY4` | Emitter.Factory | 匿名实现 | 粒子工厂，大小为4 |
| `FACTORY8` | Emitter.Factory | 匿名实现 | 粒子工厂，大小为8 |

### 实例字段
无显式实例字段，所有字段继承自父类。

## 6. 构造与初始化机制

### 构造器
```java
public PitfallParticle(){
    super();

    color( 0x000000 );  // 纯黑色
    angle = Random.Float( -30, 30 );  // 随机倾斜角度

}
```

### 初始化注意事项
- 粒子颜色固定为 `0x000000`（纯黑色）
- 初始角度为随机值 `[-30, 30]` 度
- 不设置存活时间，由 `reset()` 方法设置

## 7. 方法详解

### FACTORY4.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建大小为4的粒子实例。

**参数**：
- `emitter` (Emitter)：粒子发射器
- `index` (int)：粒子索引
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
((PitfallParticle)emitter.recycle( PitfallParticle.class )).reset( x,  y, 4 );
```

### FACTORY8.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建大小为8的粒子实例。

**核心实现逻辑**：
```java
((PitfallParticle)emitter.recycle( PitfallParticle.class )).reset( x,  y, 8 );
```

### PitfallParticle()

**可见性**：public

**是否覆写**：否

**方法职责**：构造器，初始化粒子的颜色和角度。

### reset()

**可见性**：public

**是否覆写**：否

**方法职责**：重置粒子状态，设置位置、存活时间和大小。

**参数**：
- `x` (float)：X 坐标
- `y` (float)：Y 坐标
- `size` (int)：粒子大小

**返回值**：void

**核心实现逻辑**：
```java
public void reset( float x, float y, int size ) {
    revive();

    this.x = x;
    this.y = y;

    left = lifespan = 1f;

    this.size = size;
}
```

**边界情况**：
- 存活时间固定为 1 秒
- 大小由参数指定，支持不同大小的粒子

## 8. 对外暴露能力

### 显式 API
- `FACTORY4`：静态工厂，大小为4的粒子
- `FACTORY8`：静态工厂，大小为8的粒子
- `reset(float x, float y, int size)`：重置粒子状态

### 内部辅助方法
无。

### 扩展入口
- 可通过创建新的 Factory 支持其他大小的粒子
- 可通过修改构造器中的颜色值改变粒子颜色

## 9. 运行机制与调用链

### 创建时机
当需要显示深坑效果时，由 `Emitter` 调用工厂方法创建。

### 调用者
- `Emitter` 类通过 `pour()` 或 `burst()` 方法调用工厂方法

### 被调用者
- `Emitter.recycle()`：回收或创建粒子
- `Random.Float()`：生成随机数

### 系统流程位置
```
游戏事件（如深坑触发）
    → 创建 Emitter
    → Emitter.burst(PitfallParticle.FACTORY4, ...)
    → FACTORY4.emit() → reset()
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
// 创建深坑效果（小粒子）
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.burst(PitfallParticle.FACTORY4, 15);
scene.add(emitter);
```

### 混合大小
```java
// 创建混合大小的深坑效果
Emitter emitter = new Emitter();
emitter.pos(x, y);
emitter.burst(PitfallParticle.FACTORY4, 10);
emitter.burst(PitfallParticle.FACTORY8, 5);
scene.add(emitter);
```

## 12. 开发注意事项

### 状态依赖
- 依赖父类 `PixelParticle.Shrinking` 的 `update()` 方法实现自动缩小
- 粒子颜色为纯黑色，不会变化

### 生命周期耦合
- 粒子生命周期由 `lifespan` 控制
- 所有粒子存活时间相同

### 常见陷阱
- 注意粒子有角度属性，视觉上会倾斜
- 粒子无速度和加速度，完全静态

## 13. 修改建议与扩展点

### 适合扩展的位置
- 构造器中的颜色值可修改
- 可创建新的 Factory 支持其他大小

### 不建议修改的位置
- 父类 `update()` 行为，以保证粒子正常缩小

### 重构建议
两个工厂仅大小不同，可考虑使用单一工厂并添加大小参数。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是，`FACTORY4` 和 `FACTORY8` 两个静态字段
- [x] 是否已覆盖全部方法：是，包括构造器、`reset()` 和工厂方法
- [x] 是否已检查继承链与覆写关系：是，继承 `PixelParticle.Shrinking`，无覆写
- [x] 是否已核对官方中文翻译：不适用，无文本内容
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：无关联
- [x] 是否明确说明了注意事项与扩展点：是