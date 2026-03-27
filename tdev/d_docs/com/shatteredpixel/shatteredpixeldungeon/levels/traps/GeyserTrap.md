# GeyserTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/GeyserTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 151 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`GeyserTrap`（激流陷阱）负责触发后喷涌大量水，对火属性敌人造成伤害，击退周围角色，熄灭火焰并覆盖地形。

### 系统定位
陷阱系统中的多功能型陷阱。具有伤害、控制、地形改变等多种效果。

### 不负责什么
- 不负责击退的具体实现（由 WandOfBlastWave 处理）
- 不负责水的持续效果

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = TEAL`，`shape = DIAMOND`
- **实例字段**：`centerKnockBackDirection`（中心击退方向），`source`（伤害来源）
- **activate() 方法**：执行激流效果

### 主要逻辑块概览
1. **视觉效果**：播放水花溅射效果
2. **地形改变**：将周围格子变为水域
3. **伤害处理**：对火属性敌人造成伤害
4. **击退处理**：击退周围角色

### 生命周期/调用时机
```
创建 → set(pos) → trigger() → activate() → disarm()
```

## 4. 继承与协作关系

### 父类提供的能力
继承自 `Trap`：
- 所有实例字段
- `trigger()`, `disarm()` 等方法
- `scalingDepth()` 方法

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `activate()` | 实现激流效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `WandOfBlastWave` | 击退功能 |
| `Ballistica` | 弹道计算 |
| `Fire` | 火焰 Blob |
| `Burning` | 燃烧 Buff |
| `Char.Property.FIERY` | 火属性判断 |
| `Splash` | 水花视觉效果 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `centerKnockBackDirection` | int | -1 | 中心位置的击退方向，-1 表示随机 |
| `source` | Object | this | 伤害来源对象 |

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | TEAL (4) | 青色陷阱 |
| `shape` | DIAMOND (4) | 菱形图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = TEAL;
    shape = DIAMOND;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：释放激流，改变地形、伤害敌人、击退角色。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 将周围格子变为水域
- 熄灭火焰
- 对火属性敌人造成伤害
- 击退周围角色
- 移除燃烧 Buff

**核心实现逻辑**：
```java
@Override
public void activate() {
    // 视觉效果
    Splash.at(DungeonTilemap.tileCenterToWorld(pos), -PointF.PI/2, PointF.PI/2, 0x5bc1e3, 100, 0.01f);
    Sample.INSTANCE.play(Assets.Sounds.GAS, 1f, 0.75f);

    // 改变地形为水域
    Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
    PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), 2);
    for (int i = 0; i < PathFinder.distance.length; i++) {
        if (PathFinder.distance[i] == 2 && Random.Int(3) > 0) {
            Dungeon.level.setCellToWater(true, i);
            if (fire != null) fire.clear(i);
        } else if (PathFinder.distance[i] < 2) {
            Dungeon.level.setCellToWater(true, i);
            if (fire != null) fire.clear(i);
        }
    }

    // 处理周围 8 格的角色
    for (int i : PathFinder.NEIGHBOURS8) {
        Char ch = Actor.findChar(pos + i);
        if (ch != null) {
            // 标记怪物
            if (source == this && ch instanceof Mob) {
                Buff.prolong(ch, Trap.HazardAssistTracker.class, HazardAssistTracker.DURATION);
            }

            // 对火属性敌人造成伤害
            if (Char.hasProp(ch, Char.Property.FIERY)) {
                int dmg = Random.NormalIntRange(5 + scalingDepth(), 10 + scalingDepth() * 2);
                dmg *= 0.67f;
                if (!ch.isImmune(GeyserTrap.class)) {
                    ch.damage(dmg, this);
                }
            }

            if (ch.isAlive()) {
                // 移除燃烧状态
                if (ch.buff(Burning.class) != null) {
                    ch.buff(Burning.class).detach();
                }
                // 击退
                Ballistica trajectory = new Ballistica(pos, ch.pos, Ballistica.STOP_TARGET);
                trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
                WandOfBlastWave.throwChar(ch, trajectory, 2, true, true, source);
            }
        }
    }

    // 处理中心位置的角色
    Char ch = Actor.findChar(pos);
    if (ch != null) {
        // 确定击退方向
        int targetpos = -1;
        if (centerKnockBackDirection != -1) {
            targetpos = centerKnockBackDirection;
        } else if (ch == Dungeon.hero) {
            // 英雄：选择安全方向
            ArrayList<Integer> candidates = new ArrayList<>();
            for (int i : PathFinder.NEIGHBOURS8) {
                if (!Dungeon.level.avoid[pos + i] && !Dungeon.level.avoid[pos + i + i]) {
                    candidates.add(pos + i);
                }
            }
            if (!candidates.isEmpty()) {
                targetpos = Random.element(candidates);
            }
        } else {
            // 其他：随机方向
            targetpos = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
        }

        // 对火属性敌人造成伤害
        if (Char.hasProp(ch, Char.Property.FIERY)) {
            int dmg = Random.NormalIntRange(5 + scalingDepth(), 10 + scalingDepth() * 2);
            if (!ch.isImmune(GeyserTrap.class)) {
                ch.damage(dmg, this);
            }
        }

        // 击退
        if (ch.isAlive() && targetpos != -1) {
            if (ch.buff(Burning.class) != null) {
                ch.buff(Burning.class).detach();
            }
            Ballistica trajectory = new Ballistica(pos, targetpos, Ballistica.MAGIC_BOLT);
            WandOfBlastWave.throwChar(ch, trajectory, 2, true, true, source);
        }
    }
}
```

**边界情况**：
- 外围格子（距离 2）有 33% 概率不变为水域
- 中心格子必然变为水域
- 火属性敌人受额外伤害

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发激流效果（覆写自 Trap） |

### 公共字段
| 字段 | 用途 |
|------|------|
| `centerKnockBackDirection` | 可设置中心位置的击退方向 |
| `source` | 可设置伤害来源 |

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `Splash.at()`：水花效果
- `Dungeon.level.setCellToWater()`：改变地形
- `WandOfBlastWave.throwChar()`：击退角色

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
播放视觉效果
    ↓
将周围格子变为水域
    ↓
对火属性敌人造成伤害
    ↓
击退周围角色
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.geysertrap.name` | 激流陷阱 | 陷阱名称 |
| `levels.traps.geysertrap.desc` | 被触发时，大量的水会从中喷涌而出，对火属性敌人造成伤害，击退周围所有角色，熄灭火焰并覆盖周遭的地形。 | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.GAS` | 气体/水流音效 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置激流陷阱
GeyserTrap trap = new GeyserTrap();
trap.set(position);
trap.hide();

// 触发后：
// - 周围 2 格变为水域
// - 火属性敌人受到伤害
// - 所有角色被击退 2 格
// - 熄灭火焰
```

### 自定义击退方向
```java
GeyserTrap trap = new GeyserTrap();
trap.set(position);
trap.centerKnockBackDirection = specificDirection;
// 中心位置的角色将被击退到特定方向
```

## 12. 开发注意事项

### 状态依赖
- 使用 `scalingDepth()` 计算伤害
- 火属性敌人受 67% 伤害

### 击退规则
- 周围角色：沿陷阱→角色的射线方向击退
- 中心角色：根据设置或随机方向击退
- 英雄：选择安全方向

### 地形改变
- 距离 1 格：100% 变为水域
- 距离 2 格：67% 变为水域

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整击退距离
- 可修改地形改变概率

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：已覆盖 `centerKnockBackDirection`, `source` 和继承字段
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已详细说明