# GnollRockfallTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/GnollRockfallTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends RockfallTrap |
| **代码行数** | 118 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`GnollRockfallTrap`（豺狼落石陷阱）是 `RockfallTrap` 的特殊变种，造成较小的伤害但忽略护甲，并在矿层中可能生成岩石。

### 系统定位
陷阱系统中的特殊落石型陷阱。专为豺狼矿层设计，具有独特的伤害和地形效果。

### 不负责什么
- 不负责岩石的后续行为（由地形系统处理）
- 不造成大范围伤害（伤害较低）

## 3. 结构总览

### 主要成员概览
- **activate() 方法**：实现落石效果

### 主要逻辑块概览
1. **范围计算**：5x5 区域（距离 2）
2. **支架排除**：排除木栅栏周围的格子
3. **伤害处理**：造成 6-12 点伤害，忽略护甲
4. **麻痹效果**：对豺狼守卫造成更长麻痹
5. **岩石生成**：在矿层中可能生成岩石

### 生命周期/调用时机
```
创建 → set(pos) → trigger() → activate() → disarm()
```

## 4. 继承与协作关系

### 父类提供的能力
继承自 `RockfallTrap`：
- 所有继承自 `Trap` 的字段和方法

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `activate()` | 实现豺狼落石效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `GnollGeomancer` | 豺狼地术师（不受伤害） |
| `GnollGuard` | 豺狼守卫（长麻痹） |
| `MiningLevel` | 矿层类型判断 |
| `Terrain.BARRICADE` | 木栅栏地形 |
| `Terrain.MINE_BOULDER` | 矿层岩石地形 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `RockfallTrap`，无新增字段。

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
无显式初始化块，继承父类设置。

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `RockfallTrap`

**方法职责**：在 5x5 区域内落下岩石，造成伤害和麻痹效果。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 对区域内的角色造成伤害
- 施加麻痹效果
- 可能在矿层生成岩石
- 播放视觉效果

**核心实现逻辑**：
```java
@Override
public void activate() {
    ArrayList<Integer> rockCells = new ArrayList<>();

    // 计算 5x5 区域
    PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), 2);
    for (int i = 0; i < PathFinder.distance.length; i++) {
        if (PathFinder.distance[i] < Integer.MAX_VALUE) {
            // 矿层中排除木栅栏周围的格子
            if (Dungeon.level instanceof MiningLevel) {
                boolean barricade = false;
                for (int j : PathFinder.NEIGHBOURS9) {
                    if (Dungeon.level.map[i + j] == Terrain.BARRICADE) {
                        barricade = true;
                    }
                }
                if (barricade) continue;
            }
            rockCells.add(i);
        }
    }

    boolean seen = false;
    for (int cell : rockCells) {
        if (Dungeon.level.heroFOV[cell]) {
            CellEmitter.get(cell - Dungeon.level.width()).start(Speck.factory(Speck.ROCK), 0.07f, 10);
            seen = true;
        }

        Char ch = Actor.findChar(cell);

        if (ch != null && ch.isAlive() && !(ch instanceof GnollGeomancer)) {
            if (ch instanceof Mob) {
                Buff.prolong(ch, Trap.HazardAssistTracker.class, HazardAssistTracker.DURATION);
            }

            // 伤害：6-12，忽略护甲
            int damage = Random.NormalIntRange(6, 12);
            ch.damage(Math.max(damage, 0), this);

            // 麻痹效果
            Buff.prolong(ch, Paralysis.class, ch instanceof GnollGuard ? 10 : 3);

            if (!ch.isAlive() && ch == Dungeon.hero) {
                Dungeon.fail(this);
                GLog.n(Messages.get(this, "ondeath"));
                if (reclaimed) Badges.validateDeathFromFriendlyMagic();
            }
        } else if (ch == null
                && Dungeon.level instanceof MiningLevel
                && Dungeon.level.traps.get(cell) == null
                && Dungeon.level.plants.get(cell) == null
                && Random.Int(2) == 0) {
            // 在矿层生成岩石
            Level.set(cell, Terrain.MINE_BOULDER);
            GameScene.updateMap(cell);
        }
    }

    if (seen) {
        PixelScene.shake(3, 0.7f);
        Sample.INSTANCE.play(Assets.Sounds.ROCKS);
    }
}
```

**边界情况**：
- 豺狼地术师不受伤害
- 木栅栏周围的格子不受落石影响
- 空格子有 50% 概率生成岩石（仅矿层）

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发落石效果（覆写自 RockfallTrap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
豺狼矿层中生成。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `PathFinder.buildDistanceMap()`：计算范围
- `Buff.prolong()`：施加麻痹
- `Level.set()`：改变地形
- `PixelScene.shake()`：屏幕震动

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
计算 5x5 区域
    ↓
排除木栅栏周围格子
    ↓
对角色造成伤害和麻痹
    ↓
可能生成岩石（矿层）
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.gnollrockfalltrap.name` | 豺狼落石陷阱 | 陷阱名称 |
| `levels.traps.gnollrockfalltrap.desc` | 这个落石陷阱在制作过程中注入了土石术法。触发时，该陷阱会导致在其周围5x5区域内的岩石松动并立即崩塌掉落。该处的岩石与寻常处相比更为松脆，因此其伤害性弱于常规的落石陷阱，但你的护甲却不能很好的抵御其伤害。此外，落石不会发生在支撑矿层结构的支架周边。\n\n_这个陷阱无法辨别敌友，因此对豺狼人和你都同样有效。_ | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.ROCKS` | 岩石音效 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 豺狼落石陷阱通常在矿层中生成
GnollRockfallTrap trap = new GnollRockfallTrap();
trap.set(position);

// 触发后：
// - 5x5 区域落石
// - 伤害 6-12（忽略护甲）
// - 麻痹 3 回合（守卫 10 回合）
// - 可能生成岩石（矿层空格 50%）
```

## 12. 开发注意事项

### 与 RockfallTrap 的区别
| 特性 | GnollRockfallTrap | RockfallTrap |
|------|-------------------|--------------|
| 伤害 | 6-12，忽略护甲 | 较高，计算护甲 |
| 范围 | 5x5 | 房间或圆形区域 |
| 麻痹 | 守卫 10，其他 3 | 无 |
| 岩石生成 | 矿层可能生成 | 无 |

### 特殊规则
- 木栅栏周围不受影响
- 豺狼地术师免疫
- 豺狼守卫受更长麻痹

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整伤害范围
- 可修改麻痹持续时间

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 RockfallTrap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已详细说明