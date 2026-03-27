# SpectralWallParticle 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/particles/SpectralWallParticle.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.effects.particles |
| **文件类型** | class |
| **继承关系** | extends PixelParticle |
| **代码行数** | 143 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现幽灵墙壁效果的视觉粒子表现，用于渲染不同关卡的墙壁砖块纹理粒子动画。根据关卡深度自动调整颜色和形状。

### 系统定位
粒子效果系统中的具体粒子实现类，位于 `effects.particles` 包中，是游戏视觉反馈系统的一部分。

### 不负责什么
- 不负责墙壁碰撞逻辑
- 不负责粒子发射器的创建和管理
- 不负责关卡生成的逻辑

## 3. 结构总览

### 主要成员概览
- `FACTORY`：静态工厂常量，根据关卡深度创建不同类型的粒子
- `type`：私有实例字段，粒子类型（1-5）

### 主要逻辑块概览
- 构造器：初始化粒子存活时间和透明度
- `reset()`：重置粒子状态，根据关卡深度设置颜色和位置
- `update()`：更新粒子缩放

### 生命周期/调用时机
粒子存活时间为 4 秒，由 `Emitter` 通过 `FACTORY.emit()` 方法创建并发射。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `PixelParticle`：
- `size`：粒子大小（protected）
- `lifespan`：总存活时间（protected）
- `left`：剩余存活时间（protected）
- `x`, `y`：位置坐标（继承自 PseudoPixel）
- `scale`：缩放向量（继承自 Visual）
- `am`：透明度（继承自 PseudoPixel）
- `revive()`：复活粒子
- `color(int)`：设置颜色
- `update()`：更新逻辑

### 覆写的方法
- `update()`：自定义缩放变化逻辑

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
- 幽灵墙壁相关的视觉效果场景
- 通过 `Emitter` 类使用 `SpectralWallParticle.FACTORY` 发射粒子

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `FACTORY` | Emitter.Factory | 匿名实现 | 粒子工厂，根据深度决定发射频率，`lightMode()` 返回 `false` |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `type` | int | 0 | 粒子类型（1-5），对应下水道到恶魔大厅 |

## 6. 构造与初始化机制

### 构造器
```java
public SpectralWallParticle() {
    super();

    lifespan = 4f;

    am = 0.6f;
}
```

### 初始化注意事项
- 存活时间固定为 4 秒（较长）
- 透明度固定为 0.6（半透明）

## 7. 方法详解

### FACTORY.emit()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.emit()`

**方法职责**：通过发射器回收或创建粒子实例，根据关卡深度调整发射频率。

**参数**：
- `emitter` (Emitter)：粒子发射器
- `index` (int)：粒子索引
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
// 根据砖块大小调整发射频率
int type = 1 + Dungeon.depth/5;
if (type > 5) type = 5;

switch (type){
    case 1: if (Random.Int(2) != 0) return; break;  // 50%概率
    case 2: if (Random.Int(3) != 0) return; break;  // 67%概率
    case 3: break;  // 100%概率
    case 4: break;  // 100%概率
    case 5: if (Random.Int(4) != 0) return; break;  // 75%概率
}

((SpectralWallParticle)emitter.recycle( SpectralWallParticle.class )).reset( x, y );
```

**边界情况**：
- 类型根据 `Dungeon.depth/5` 计算，范围 1-5
- 不同类型的发射频率不同

### FACTORY.lightMode()

**可见性**：public（匿名类覆写）

**是否覆写**：是，覆写自 `Emitter.Factory.lightMode()`

**方法职责**：指示粒子是否使用光照模式渲染。

**返回值**：boolean，固定返回 `false`

### SpectralWallParticle()

**可见性**：public

**是否覆写**：否

**方法职责**：构造器，初始化粒子的存活时间和透明度。

### reset()

**可见性**：public

**是否覆写**：否

**方法职责**：重置粒子状态，根据关卡深度设置类型、颜色和位置。

**参数**：
- `x` (float)：X 坐标
- `y` (float)：Y 坐标

**返回值**：void

**核心实现逻辑**：
```java
public void reset( float x, float y ) {
    revive();

    type = 1 + Dungeon.depth/5;
    if (type > 5) type = 5;

    this.x = x;
    this.y = y;

    left = lifespan;

    switch (type){
        case 1: // 下水道 - 灰色砖块
            this.x = Math.round(x/7)*7;
            this.y = Math.round(y/4)*4 - 6;
            this.x += Math.round(this.y % 8)/4f - 1;
            color(ColorMath.random(0xD4D4D4, 0xABABAB));
            break;
        case 2: // 监狱 - 土黄色砖块
            this.x = Math.round(x/7)*7;
            this.y = Math.round(y/6)*6 - 6;
            this.x += Math.round(this.y % 8)/4f - 1;
            color(ColorMath.random(0xc4be9c, 0x9c927d));
            break;
        case 3: // 洞穴 - 深灰色砖块
            this.y -= 6;
            float colorScale = (this.x%16)/16 + (this.y%16)/16;
            if (colorScale > 1f) colorScale = 2f - colorScale;
            color(ColorMath.interpolate(0xb7b0a5, 0x6a6662, colorScale));
            break;
        case 4: // 城市 - 浅棕色砖块
            this.x = Math.round(x/4)*4;
            this.y = Math.round(y/4)*4 - 6;
            this.x += Math.round(this.y % 16)/4f - 2;
            color(ColorMath.interpolate(0xd0bca3, 0xa38d81));
            break;
        case 5: // 恶魔大厅 - 深棕色砖块
            this.x = Math.round(x/4)*4;
            this.y = Math.round((y+8)/16)*16 - 14;
            color(ColorMath.interpolate(0xa2947d, 0x594847));
            break;
    }
}
```

**边界情况**：
- 不同类型的砖块大小和对齐方式不同
- 颜色根据关卡类型变化

### update()

**可见性**：public

**是否覆写**：是，覆写自 `PixelParticle.update()`

**方法职责**：更新粒子状态，根据类型设置缩放。

**核心实现逻辑**：
```java
public void update(){
    super.update();

    float sizeFactor = (left / lifespan);

    switch (type){
        case 1: scale.set(sizeFactor*6, sizeFactor*3); break;
        case 2: scale.set(sizeFactor*6, sizeFactor*5); break;
        case 3: scale.set(sizeFactor*4, sizeFactor*4); break;
        case 4: scale.set(sizeFactor*3, sizeFactor*3); break;
        case 5: scale.set(sizeFactor*4, sizeFactor*20); break;
    }
}
```

**边界情况**：
- 缩放随时间减小
- 类型5的Y缩放特别大（20倍）

## 8. 对外暴露能力

### 显式 API
- `FACTORY`：静态工厂，根据关卡深度创建不同类型的粒子
- `reset(float x, float y)`：重置粒子状态

### 内部辅助方法
无。

### 扩展入口
- 可通过修改 `reset()` 方法添加新的关卡类型
- 可通过修改 `update()` 方法自定义缩放效果

## 9. 运行机制与调用链

### 创建时机
当需要显示幽灵墙壁效果时，由 `Emitter` 调用 `FACTORY.emit()` 创建。

### 调用者
- `Emitter` 类通过 `pour()` 方法调用 `FACTORY.emit()`

### 被调用者
- `Emitter.recycle()`：回收或创建粒子
- `Dungeon.depth`：获取关卡深度
- `ColorMath.random()` / `ColorMath.interpolate()`：颜色计算
- `Random.Int()`：生成随机数

### 系统流程位置
```
游戏场景加载
    → 创建 Emitter
    → Emitter.pour(SpectralWallParticle.FACTORY, ...)
    → FACTORY.emit() 根据深度决定是否发射
    → reset() 根据深度设置颜色和位置
    → 每帧调用 update() 更新缩放
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
// 创建幽灵墙壁效果
Emitter emitter = new Emitter();
emitter.pos(x, y, width, height);
emitter.pour(SpectralWallParticle.FACTORY, 0.5f);
scene.add(emitter);
```

## 12. 开发注意事项

### 状态依赖
- 粒子类型依赖 `Dungeon.depth`
- 颜色和形状依赖粒子类型

### 生命周期耦合
- 粒子生命周期较长（4秒）
- 缩放变化与生命周期同步

### 常见陷阱
- 不同类型的发射频率不同，某些类型可能不发射粒子
- 粒子位置会对齐到砖块网格
- 类型5的Y缩放特别大，视觉效果与其他类型差异明显

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可在 switch 语句中添加新的类型
- 可调整各类型的位置对齐和颜色

### 不建议修改的位置
- 砖块网格对齐逻辑，以保证视觉效果一致性

### 重构建议
各类型的颜色和缩放配置可考虑提取为配置表，便于维护。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是，`FACTORY` 静态字段和 `type` 实例字段
- [x] 是否已覆盖全部方法：是，包括构造器、`reset()`、`update()` 和工厂方法
- [x] 是否已检查继承链与覆写关系：是，继承 `PixelParticle`，覆写 `update()`
- [x] 是否已核对官方中文翻译：不适用，无文本内容
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：无关联
- [x] 是否明确说明了注意事项与扩展点：是