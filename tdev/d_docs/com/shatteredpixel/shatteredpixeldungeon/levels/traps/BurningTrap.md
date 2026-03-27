# BurningTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/BurningTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 58 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`BurningTrap`（烈焰陷阱）负责触发后在周围小范围内生成火焰，对范围内角色造成火焰伤害。

### 系统定位
陷阱系统中的小范围伤害型陷阱。是 `BlazingTrap` 的基础版本，覆盖范围较小（仅 9 格邻域）。

### 不负责什么
- 不直接对角色造成伤害（伤害由火焰 Blob 机制处理）
- 不负责火焰的持续燃烧逻辑

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = ORANGE`，`shape = DOTS`
- **activate() 方法**：生成小范围火焰

### 主要逻辑块概览
1. **邻域遍历**：遍历 `PathFinder.NEIGHBOURS9`（3x3 区域）
2. **火焰生成**：在非实体格子生成火焰
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
| `activate()` | 实现烈焰效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `PathFinder.NEIGHBOURS9` | 9 格邻域偏移量 |
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
| `shape` | DOTS (0) | 点状图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = ORANGE;
    shape = DOTS;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：在陷阱周围 3x3 范围内生成火焰。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 在 3x3 范围内生成火焰 Blob
- 为怪物添加 `HazardAssistTracker` 标记
- 播放视觉效果和音效

**核心实现逻辑**：
```java
@Override
public void activate() {
    for (int i : PathFinder.NEIGHBOURS9) {
        if (!Dungeon.level.solid[pos + i]) {
            // 生成火焰（强度 2）
            GameScene.add(Blob.seed(pos + i, 2, Fire.class));
            // 播放火焰粒子
            CellEmitter.get(pos + i).burst(FlameParticle.FACTORY, 5);
            
            // 为怪物添加危险辅助标记
            if (Actor.findChar(pos + i) instanceof Mob) {
                Buff.prolong(Actor.findChar(pos + i), Trap.HazardAssistTracker.class, 
                             HazardAssistTracker.DURATION);
            }
        }
    }
    Sample.INSTANCE.play(Assets.Sounds.BURNING);
}
```

**边界情况**：
- 实心墙壁不会被点燃
- 火焰强度固定为 2（比 BlazingTrap 弱）

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发烈焰效果（覆写自 Trap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `GameScene.add()`：添加火焰 Blob
- `CellEmitter.get().burst()`：播放粒子
- `Buff.prolong()`：添加 Buff

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
遍历 3x3 邻域
    ↓
每个非实体地格生成火焰 Blob
    ↓
火焰对角色造成伤害
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.burningtrap.name` | 烈焰陷阱 | 陷阱名称 |
| `levels.traps.burningtrap.desc` | 踩进这个陷阱会点燃某种化学混合物，导致附近一块区域起火。 | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.BURNING` | 燃烧音效 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置烈焰陷阱
BurningTrap trap = new BurningTrap();
trap.set(position);
trap.hide();

// 触发后 3x3 范围内生成火焰（强度 2）
```

## 12. 开发注意事项

### 状态依赖
- 依赖 `Dungeon.level.solid` 判断实体格子

### 与 BlazingTrap 的区别
| 特性 | BurningTrap | BlazingTrap |
|------|-------------|-------------|
| 范围 | 3x3 邻域 | 距离 2 内所有格子 |
| 火焰强度 | 2 | 5（普通）/ 1（水域/深渊） |
| 颜色 | ORANGE | ORANGE |
| 形状 | DOTS | STARS |

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整火焰强度参数
- 可覆写 `activate()` 自定义范围

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已详细说明