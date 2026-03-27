# PitfallTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/PitfallTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 175 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`PitfallTrap`（塌方陷阱）负责触发后使周围地板崩塌，导致角色和物品掉落到下一层。

### 系统定位
陷阱系统中的环境改变型陷阱。能够改变游戏进程，将目标传送到下一层。

### 不负责什么
- 不在 Boss 层或深度 25 以上生效
- 不负责掉落后的具体处理（由 Chasm 类处理）

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = RED`，`shape = DIAMOND`
- **activate() 方法**：触发塌方效果
- **DelayedPit 内部类**：延迟执行的塌方效果

### 主要逻辑块概览
1. **条件检查**：验证是否可以触发塌方
2. **延迟设置**：创建延迟执行的塌方效果
3. **位置记录**：记录受影响的位置
4. **效果执行**：使角色和物品掉落

### 生命周期/调用时机
```
创建 → set(pos) → trigger() → activate() → DelayedPit.act()
```

## 4. 继承与协作关系

### 父类提供的能力
继承自 `Trap`：
- 所有实例字段
- `trigger()`, `disarm()` 等方法

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `activate()` | 触发塌方效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Chasm` | 深渊掉落处理 |
| `DelayedPit` | 延迟塌方效果 |
| `Heap` | 物品堆 |
| `FlavourBuff` | Buff 基类 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | RED (0) | 红色陷阱 |
| `shape` | DIAMOND (4) | 菱形图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = RED;
    shape = DIAMOND;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：触发塌方效果，使周围地板崩塌。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 创建延迟塌方效果
- 显示警告消息
- 播放视觉效果

**核心实现逻辑**：
```java
@Override
public void activate() {
    // 条件检查：Boss 层、深度 25+、非主线分支不生效
    if (Dungeon.bossLevel() || Dungeon.depth > 25 || Dungeon.branch != 0) {
        GLog.w(Messages.get(this, "no_pit"));
        return;
    }

    // 创建延迟塌方效果
    DelayedPit p = Buff.append(Dungeon.hero, DelayedPit.class, 1);
    p.depth = Dungeon.depth;
    p.branch = Dungeon.branch;

    // 记录受影响位置
    ArrayList<Integer> positions = new ArrayList<>();
    for (int i : PathFinder.NEIGHBOURS9) {
        if (!Dungeon.level.solid[pos + i] || Dungeon.level.passable[pos + i]) {
            CellEmitter.floor(pos + i).burst(PitfallParticle.FACTORY4, 8);
            positions.add(pos + i);
        }
    }
    p.setPositions(positions);

    // 显示警告
    if (pos == Dungeon.hero.pos) {
        GLog.n(Messages.get(this, "triggered_hero"));
    } else if (Dungeon.level.heroFOV[pos]) {
        GLog.n(Messages.get(this, "triggered"));
    }
}
```

---

### DelayedPit 内部类

**类型**：public static class extends FlavourBuff

**职责**：延迟 1 回合后执行塌方效果。

**主要字段**：
| 字段名 | 类型 | 说明 |
|--------|------|------|
| `positions` | int[] | 受影响的位置 |
| `depth` | int | 触发时的深度 |
| `branch` | int | 触发时的分支 |
| `ignoreAllies` | boolean | 是否忽略盟友 |

**关键方法**：

#### act()

```java
@Override
public boolean act() {
    boolean herofell = false;
    if (depth == Dungeon.depth && branch == Dungeon.branch && positions != null) {
        for (int cell : positions) {
            if (!Dungeon.level.insideMap(cell)
                    || (Dungeon.level.solid[cell] && !Dungeon.level.passable[cell])) {
                continue;
            }

            CellEmitter.floor(cell).burst(PitfallParticle.FACTORY8, 12);

            Char ch = Actor.findChar(cell);
            // 飞行角色、不可移动中立角色、盟友（可选）不受影响
            if (ch != null && !ch.flying
                    && !(ch.alignment == Char.Alignment.NEUTRAL && Char.hasProp(ch, Char.Property.IMMOVABLE))
                    && !(ch.alignment == Char.Alignment.ALLY && ignoreAllies)) {
                if (ch == Dungeon.hero) {
                    herofell = true;
                } else {
                    Chasm.mobFall((Mob) ch);
                }
            }

            // 处理物品堆
            Heap heap = Dungeon.level.heaps.get(cell);
            if (heap != null && !ignoreAllies
                    && heap.type != Heap.Type.FOR_SALE
                    && heap.type != Heap.Type.LOCKED_CHEST
                    && heap.type != Heap.Type.CRYSTAL_CHEST) {
                for (Item item : heap.items) {
                    Dungeon.dropToChasm(item);
                }
                heap.sprite.kill();
                GameScene.discard(heap);
                heap.sprite.drop();
                Dungeon.level.heaps.remove(cell);
            }
        }
    }

    // 英雄最后处理
    if (herofell) {
        Chasm.heroFall(Dungeon.hero.pos);
    }

    detach();
    return !herofell;
}
```

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发塌方效果（覆写自 Trap） |
| `DelayedPit` | 延迟塌方效果类 |

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `Buff.append()`：添加延迟效果
- `CellEmitter.floor()`：播放粒子效果
- `Chasm.heroFall()`：英雄掉落
- `Chasm.mobFall()`：怪物掉落

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
检查是否可以触发
    ↓
创建 DelayedPit（延迟 1 回合）
    ↓
记录受影响位置
    ↓
1 回合后执行塌方
    ↓
角色和物品掉落
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.pitfalltrap.name` | 塌方陷阱 | 陷阱名称 |
| `levels.traps.pitfalltrap.triggered_hero` | 你周围的地板在迅速崩塌！ | 英雄踩中警告 |
| `levels.traps.pitfalltrap.triggered` | 陷阱附近的地板在迅速崩塌！ | 触发警告 |
| `levels.traps.pitfalltrap.no_pit` | 这里的地面太结实了，塌方陷阱在这里无效。 | 无效提示 |
| `levels.traps.pitfalltrap.desc` | 这种陷阱与一种大型活板门装置相连，在激活后会使周围的地板迅速崩塌！不过这类陷阱在坚实的地面上会失效。 | 陷阱描述 |

### 依赖的资源
无特殊音效资源。

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置塌方陷阱
PitfallTrap trap = new PitfallTrap();
trap.set(position);
trap.hide();

// 触发后 1 回合内周围地板崩塌
// 角色和物品掉落到下一层
// 注意：Boss 层和深度 25+ 不生效
```

## 12. 开发注意事项

### 状态依赖
- Boss 层不生效
- 深度 25 以上不生效
- 非主线分支不生效

### 不受影响的角色
- 飞行状态的角色
- 不可移动的中立角色
- 盟友（可选）

### 不受影响的物品堆
- 待售物品
- 锁住的箱子
- 水晶箱子

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整延迟时间
- 可修改受影响范围

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()` 及内部类方法
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关信息
- [x] 是否明确说明了注意事项与扩展点：已详细说明