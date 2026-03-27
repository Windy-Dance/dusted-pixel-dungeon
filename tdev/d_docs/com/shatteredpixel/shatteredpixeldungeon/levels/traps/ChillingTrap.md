# ChillingTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/ChillingTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 59 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`ChillingTrap`（寒气陷阱）负责触发后在周围小范围内释放冷冻效果，冻结附近空气。

### 系统定位
陷阱系统中的小范围冰冻型陷阱。是 `FrostTrap` 的基础版本，覆盖范围较小（仅 9 格邻域）。

### 不负责什么
- 不直接对角色造成伤害（由冷冻 Blob 机制处理）
- 不负责冰冻的持续效果逻辑

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = WHITE`，`shape = DOTS`
- **activate() 方法**：释放冷冻效果

### 主要逻辑块概览
1. **视野判断**：检查是否在玩家视野内显示视觉效果
2. **邻域遍历**：遍历 `PathFinder.NEIGHBOURS9`（3x3 区域）
3. **冷冻生成**：在非实体格子生成冷冻 Blob
4. **音效播放**：播放破碎音效

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
| `activate()` | 实现冷冻效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `PathFinder.NEIGHBOURS9` | 9 格邻域偏移量 |
| `Blob.seed()` | 生成冷冻 Blob |
| `Freezing` | 冷冻效果类 |
| `Splash` | 溅射视觉效果 |
| `HazardAssistTracker` | 标记怪物受到陷阱伤害 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | WHITE (6) | 白色陷阱 |
| `shape` | DOTS (0) | 点状图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = WHITE;
    shape = DOTS;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：在陷阱周围 3x3 范围内释放冷冻效果。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 在 3x3 范围内生成冷冻 Blob
- 为怪物添加 `HazardAssistTracker` 标记
- 播放视觉效果和音效

**核心实现逻辑**：
```java
@Override
public void activate() {
    // 视觉效果（仅在玩家视野内）
    if (Dungeon.level.heroFOV[pos]) {
        Splash.at(pos, 0xFFB2D6FF, 5);
        Sample.INSTANCE.play(Assets.Sounds.SHATTER);
    }
    
    for (int i : PathFinder.NEIGHBOURS9) {
        if (!Dungeon.level.solid[pos + i]) {
            // 生成冷冻效果（强度 10）
            GameScene.add(Blob.seed(pos + i, 10, Freezing.class));
            
            // 为怪物添加危险辅助标记
            if (Actor.findChar(pos + i) instanceof Mob) {
                Buff.prolong(Actor.findChar(pos + i), Trap.HazardAssistTracker.class, 
                             HazardAssistTracker.DURATION);
            }
        }
    }
}
```

**边界情况**：
- 实心墙壁不会被冷冻
- 冷冻强度固定为 10

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发冷冻效果（覆写自 Trap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `Splash.at()`：播放溅射效果
- `GameScene.add()`：添加冷冻 Blob
- `Buff.prolong()`：添加 Buff

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
遍历 3x3 邻域
    ↓
每个非实体地格生成冷冻 Blob
    ↓
冷冻效果对角色产生冰冻影响
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.chillingtrap.name` | 寒气陷阱 | 陷阱名称 |
| `levels.traps.chillingtrap.desc` | 被触发时，这个陷阱里的化学药剂会迅速冻结附近的空气。 | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.SHATTER` | 破碎音效 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置寒气陷阱
ChillingTrap trap = new ChillingTrap();
trap.set(position);
trap.hide();

// 触发后 3x3 范围内生成冷冻效果（强度 10）
```

## 12. 开发注意事项

### 状态依赖
- 依赖 `Dungeon.level.solid` 判断实体格子
- 依赖 `Dungeon.level.heroFOV` 判断视觉效果

### 与 FrostTrap 的区别
| 特性 | ChillingTrap | FrostTrap |
|------|--------------|-----------|
| 范围 | 3x3 邻域 | 更大范围 |
| 颜色 | WHITE | WHITE |
| 形状 | DOTS | 不同形状 |

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整冷冻强度参数
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