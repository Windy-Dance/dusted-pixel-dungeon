# BlazingTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/BlazingTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 64 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`BlazingTrap`（爆炎陷阱）负责触发后在较大范围内生成火焰，对范围内所有角色造成火焰伤害。

### 系统定位
陷阱系统中的范围伤害型陷阱。是 `BurningTrap` 的增强版本，覆盖范围更大。

### 不负责什么
- 不直接对角色造成伤害（伤害由火焰 Blob 机制处理）
- 不负责火焰的持续燃烧逻辑

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = ORANGE`，`shape = STARS`
- **activate() 方法**：生成范围火焰

### 主要逻辑块概览
1. **距离计算**：使用 `PathFinder.buildDistanceMap` 计算 2 格范围内的地格
2. **火焰生成**：根据地形生成不同强度的火焰
3. **视觉效果**：播放火焰粒子效果
4. **音效播放**：播放燃烧音效

### 生命周期/调用时机
```
创建 → set(pos) → trigger() → activate() → disarm()
```

## 4. 继承与协作关系

### 父类提供的能力
继承自 `Trap`：
- 所有实例字段
- `trigger()`, `disarm()` 等方法

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `activate()` | 实现爆炎效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `PathFinder` | 计算距离图 |
| `BArray.not()` | 创建非实体格子数组 |
| `Blob.seed()` | 生成火焰 Blob |
| `Fire` | 火焰效果类 |
| `CellEmitter` | 粒子效果 |
| `FlameParticle` | 火焰粒子 |
| `HazardAssistTracker` | 标记怪物受到陷阱伤害 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | ORANGE (1) | 橙色陷阱 |
| `shape` | STARS (3) | 星形图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = ORANGE;
    shape = STARS;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：在陷阱周围 2 格范围内生成火焰。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 在范围内生成火焰 Blob
- 为怪物添加 `HazardAssistTracker` 标记
- 播放视觉效果和音效

**核心实现逻辑**：
```java
@Override
public void activate() {
    // 构建距离图，范围为 2 格
    PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), 2);
    
    for (int i = 0; i < PathFinder.distance.length; i++) {
        if (PathFinder.distance[i] < Integer.MAX_VALUE) {
            // 深渊或水域位置生成短时间火焰
            if (Dungeon.level.pit[i] || Dungeon.level.water[i]) {
                GameScene.add(Blob.seed(i, 1, Fire.class));
            } else {
                // 普通地面生成较长时间火焰
                GameScene.add(Blob.seed(i, 5, Fire.class));
            }
            // 播放火焰粒子
            CellEmitter.get(i).burst(FlameParticle.FACTORY, 5);
            
            // 为怪物添加危险辅助标记
            if (Actor.findChar(i) instanceof Mob) {
                Buff.prolong(Actor.findChar(i), Trap.HazardAssistTracker.class, 
                             HazardAssistTracker.DURATION);
            }
        }
    }
    Sample.INSTANCE.play(Assets.Sounds.BURNING);
}
```

**边界情况**：
- 水域和深渊处火焰持续时间短（1 tick）
- 普通地面火焰持续时间长（5 ticks）
- 实心墙壁不会被点燃

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发爆炎效果（覆写自 Trap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `PathFinder.buildDistanceMap()`：计算距离
- `GameScene.add()`：添加火焰 Blob
- `CellEmitter.get().burst()`：播放粒子
- `Buff.prolong()`：添加 Buff

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
计算 2 格范围内的可点燃地格
    ↓
每个地格生成火焰 Blob
    ↓
火焰对角色造成伤害
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.blazingtrap.name` | 爆炎陷阱 | 陷阱名称 |
| `levels.traps.blazingtrap.desc` | 踩进这个陷阱会点燃某种极具力量的化学混合物，导致很大一块区域起火。 | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.BURNING` | 燃烧音效 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置爆炎陷阱
BlazingTrap trap = new BlazingTrap();
trap.set(position);
trap.hide();

// 触发后 2 格范围内生成火焰
// 水域/深渊：短时间火焰
// 普通地面：较长时间火焰
```

## 12. 开发注意事项

### 状态依赖
- 依赖 `Dungeon.level.solid` 判断实体格子
- 依赖 `Dungeon.level.water` 和 `Dungeon.level.pit` 调整火焰强度

### 生命周期耦合
- 触发后陷阱自动解除

### 常见陷阱
- 在水域附近触发效果减弱
- 与 BurningTrap 相比范围更大

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整距离参数（当前为 2）改变范围
- 可覆写 `activate()` 自定义火焰强度

### 不建议修改的位置
- 火焰 Blob 的生成逻辑依赖 Fire 类

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已在第 12、13 章详细说明