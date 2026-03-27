# DistortionTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/DistortionTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 163 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`DistortionTrap`（重构陷阱）负责触发后召唤各种怪物到陷阱周围，是高难度的召唤型陷阱。

### 系统定位
陷阱系统中的召唤型陷阱。通过召唤多样化的怪物增加战斗难度。

### 不负责什么
- 不直接对触发者造成伤害
- 不负责召唤怪物的具体行为（由怪物自身 AI 处理）

## 3. 结构总览

### 主要成员概览
- **静态常量**：`DELAY = 2f`（怪物生成延迟）
- **初始化块**：设置 `color = TEAL`，`shape = LARGE_DOT`
- **activate() 方法**：召唤怪物

### 主要逻辑块概览
1. **数量确定**：随机决定召唤数量（3-5 只）
2. **位置选择**：选择周围的可行走格子
3. **怪物选择**：根据召唤序号选择不同类型的怪物
4. **生成处理**：生成怪物并处理陷阱触发

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
| `activate()` | 实现怪物召唤效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `MobSpawner` | 怪物生成器 |
| `Reflection` | 反射创建怪物实例 |
| `Wraith` | 幽灵类 |
| `Piranha` | 食人鱼类 |
| `Mimic` | 宝箱怪类 |
| `Statue` | 石像类 |
| `RatKing` | 鼠王（稀有召唤） |

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `DELAY` | float | 2f | 怪物生成延迟时间 |

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | TEAL (4) | 青色陷阱 |
| `shape` | LARGE_DOT (6) | 大点图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = TEAL;
    shape = LARGE_DOT;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：在陷阱周围召唤多样化的怪物。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 生成 3-5 只怪物
- 触发怪物位置的陷阱
- 更新地图视野

**核心实现逻辑**：
```java
@Override
public void activate() {
    // 确定召唤数量：3 + 随机增加
    int nMobs = 3;
    if (Random.Int(2) == 0) {
        nMobs++;
        if (Random.Int(2) == 0) {
            nMobs++;
        }
    }

    // 选择生成位置
    ArrayList<Integer> candidates = new ArrayList<>();
    for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
        int p = pos + PathFinder.NEIGHBOURS8[i];
        if (Actor.findChar(p) == null && 
            (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
            candidates.add(p);
        }
    }

    ArrayList<Integer> respawnPoints = new ArrayList<>();
    while (nMobs > 0 && candidates.size() > 0) {
        int index = Random.index(candidates);
        respawnPoints.add(candidates.remove(index));
        nMobs--;
    }

    // 根据召唤序号选择怪物类型
    ArrayList<Mob> mobs = new ArrayList<>();
    int summoned = 0;
    for (Integer point : respawnPoints) {
        summoned++;
        Mob mob;
        switch (summoned) {
            case 1:
                // 第 1 只：1% 概率召唤鼠王
                if (Dungeon.depth != 5 && Random.Int(100) == 0) {
                    mob = new RatKing();
                    break;
                }
            case 3:
            case 5:
            default:
                // 默认：随机楼层的普通怪物
                int floor;
                do {
                    floor = Random.Int(25);
                } while (Dungeon.bossLevel(floor));
                mob = Reflection.newInstance(MobSpawner.getMobRotation(floor).get(0));
                break;
            case 2:
                // 第 2 只：特殊怪物
                switch (Random.Int(4)) {
                    case 0:
                        Wraith.spawnAt(point);
                        continue; // 幽灵自己生成
                    case 1:
                        mob = Piranha.random(); // 食人鱼
                        break;
                    case 2:
                        mob = Mimic.spawnAt(point, false); // 宝箱怪
                        ((Mimic)mob).stopHiding();
                        mob.alignment = Char.Alignment.ENEMY;
                        break;
                    case 3:
                        mob = Statue.random(false); // 石像
                        break;
                }
                break;
            case 4:
                // 第 4 只：稀有变种怪物
                mob = Reflection.newInstance(Random.element(MobSpawner.RARE_ALTS.values()));
                break;
        }

        // 大型怪物需要开阔空间
        if (Char.hasProp(mob, Char.Property.LARGE) && !Dungeon.level.openSpace[point]) {
            continue;
        }

        mob.maxLvl = Hero.MAX_LEVEL - 1;
        if (mob.state != mob.PASSIVE) {
            mob.state = mob.WANDERING;
        }
        mob.pos = point;
        GameScene.add(mob, DELAY);
        mobs.add(mob);
    }

    // 处理生成位置的陷阱
    Trap t;
    for (Mob mob : mobs) {
        if ((t = Dungeon.level.traps.get(mob.pos)) != null && t.active) {
            if (t.disarmedByActivation) t.disarm();
            t.reveal();
            Bestiary.setSeen(t.getClass());
            Bestiary.countEncounter(t.getClass());
            t.activate();
        }
        ScrollOfTeleportation.appear(mob, mob.pos);
        Dungeon.level.occupyCell(mob);
    }
}
```

**边界情况**：
- 第 5 层不会召唤鼠王
- 大型怪物需要开阔空间
- 幽灵有独立的生成逻辑

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发怪物召唤（覆写自 Trap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `Reflection.newInstance()`：反射创建怪物
- `GameScene.add()`：添加怪物到场景
- `ScrollOfTeleportation.appear()`：显示怪物

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
计算召唤数量（3-5 只）
    ↓
选择周围可行走位置
    ↓
根据序号选择怪物类型
    ↓
生成怪物并处理陷阱
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.distortiontrap.name` | 重构陷阱 | 陷阱名称 |
| `levels.traps.distortiontrap.desc` | 这种陷阱由来源不明的奇特魔法制作，触发后会召唤各式各样的怪物到这里。 | 陷阱描述 |

### 依赖的资源
无特殊音效资源。

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置重构陷阱
DistortionTrap trap = new DistortionTrap();
trap.set(position);
trap.hide();

// 触发后召唤 3-5 只随机怪物
// 怪物类型多样：普通怪物、幽灵、食人鱼、宝箱怪、石像、稀有变种等
```

## 12. 开发注意事项

### 状态依赖
- 需要周围有可行走的空间
- 大型怪物需要开阔空间

### 召唤规则
| 召唤序号 | 怪物类型 |
|----------|----------|
| 1 | 随机楼层怪物（1% 概率鼠王） |
| 2 | 幽灵/食人鱼/宝箱怪/石像（随机） |
| 3, 5 | 随机楼层怪物 |
| 4 | 稀有变种怪物 |

### 召唤概率
- 50% 概率召唤 4 只
- 25% 概率召唤 5 只
- 25% 概率召唤 3 只

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整召唤数量计算逻辑
- 可修改怪物类型选择规则

### 不建议修改的位置
- `DELAY` 常量影响游戏节奏

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：已覆盖静态常量和继承字段
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关信息
- [x] 是否明确说明了注意事项与扩展点：已详细说明