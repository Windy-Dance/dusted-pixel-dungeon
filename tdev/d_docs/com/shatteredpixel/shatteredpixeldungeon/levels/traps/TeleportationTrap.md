# TeleportationTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/TeleportationTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 77 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`TeleportationTrap`（传送陷阱）负责触发后将周围的角色和物品传送到本层的随机位置。

### 系统定位
陷阱系统中的传送型陷阱。`WarpingTrap` 继承此类并添加额外效果。

### 不负责什么
- 不造成伤害
- 不负责传送的具体实现（由 ScrollOfTeleportation 处理）

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = TEAL`，`shape = DOTS`
- **activate() 方法**：执行传送效果

### 主要逻辑块概览
1. **邻域遍历**：遍历 3x3 区域
2. **角色传送**：传送范围内的角色
3. **物品传送**：传送范围内的物品堆

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
| `activate()` | 实现传送效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `ScrollOfTeleportation` | 传送功能 |
| `Heap` | 物品堆 |
| `PathFinder.NEIGHBOURS9` | 9 格邻域 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | TEAL (4) | 青色陷阱 |
| `shape` | DOTS (0) | 点状图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = TEAL;
    shape = DOTS;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：传送周围 3x3 区域内的角色和物品到随机位置。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 传送范围内的角色
- 传送范围内的物品堆
- 改变怪物的状态

**核心实现逻辑**：
```java
@Override
public void activate() {
    for (int i : PathFinder.NEIGHBOURS9) {
        // 传送角色
        Char ch = Actor.findChar(pos + i);
        if (ch != null) {
            if (ScrollOfTeleportation.teleportChar(ch)) {
                if (ch instanceof Mob && ((Mob) ch).state == ((Mob) ch).HUNTING) {
                    ((Mob) ch).state = ((Mob) ch).WANDERING;
                    Buff.prolong(ch, Trap.HazardAssistTracker.class, HazardAssistTracker.DURATION);
                }
            }
        }
        
        // 传送物品堆
        Heap heap = Dungeon.level.heaps.get(pos + i);
        if (heap != null && heap.type == Heap.Type.HEAP) {
            int cell = Dungeon.level.randomRespawnCell(null);
            Item item = heap.pickUp();
            if (cell != -1) {
                Dungeon.level.drop(item, cell);
                if (item instanceof Honeypot.ShatteredPot) {
                    ((Honeypot.ShatteredPot)item).movePot(pos, cell);
                }
                Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
                CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 4);
            }
        }
    }
}
```

**边界情况**：
- 物品堆类型必须为 HEAP 才会被传送
- 追猎状态的怪物变为游荡

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发传送效果（覆写自 Trap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `ScrollOfTeleportation.teleportChar()`：传送角色
- `Dungeon.level.randomRespawnCell()`：获取随机重生点
- `Dungeon.level.drop()`：放置物品

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
遍历 3x3 邻域
    ↓
传送范围内的角色和物品
    ↓
追猎状态的怪物变为游荡
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.teleportationtrap.name` | 传送陷阱 | 陷阱名称 |
| `levels.traps.teleportationtrap.desc` | 当这种陷阱被触发时，其周遭的所有东西都会被各自传送到本层的随机地点。 | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.TELEPORT` | 传送音效 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置传送陷阱
TeleportationTrap trap = new TeleportationTrap();
trap.set(position);
trap.hide();

// 触发后传送 3x3 范围内的角色和物品到随机位置
```

## 12. 开发注意事项

### 传送范围
- 角色和物品各自传送到不同位置
- 物品堆类型必须为 HEAP

### 怪物状态
- 追猎状态的怪物变为游荡
- 添加 HazardAssistTracker 标记

## 13. 修改建议与扩展点

### 适合扩展的位置
- `WarpingTrap` 继承此类并添加遗忘地图效果
- 可在子类中添加额外效果

### 与 WarpingTrap 的关系
`WarpingTrap` 继承 `TeleportationTrap`，在传送后清除地图记忆。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已详细说明