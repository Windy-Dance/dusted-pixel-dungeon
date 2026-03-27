# SummoningTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/SummoningTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 107 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`SummoningTrap`（召唤陷阱）负责触发后召唤本区域的怪物到陷阱周围。

### 系统定位
陷阱系统中的召唤型陷阱。召唤的怪物类型由当前楼层的怪物生成规则决定。

### 不负责什么
- 不直接造成伤害
- 不负责召唤怪物的具体类型选择（由 Level.createMob() 决定）

## 3. 结构总览

### 主要成员概览
- **静态常量**：`DELAY = 2f`（怪物生成延迟）
- **初始化块**：设置 `color = TEAL`，`shape = WAVES`
- **activate() 方法**：召唤怪物

### 主要逻辑块概览
1. **数量确定**：随机决定召唤数量（1-3 只）
2. **位置选择**：选择周围的可行走格子
3. **怪物生成**：生成当前楼层的怪物
4. **陷阱处理**：触发怪物位置的陷阱

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
| `activate()` | 实现召唤效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Dungeon.level.createMob()` | 创建当前楼层怪物 |
| `Char.Property.LARGE` | 大型角色属性判断 |
| `ScrollOfTeleportation.appear()` | 显示怪物 |

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
| `shape` | WAVES (1) | 波浪图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = TEAL;
    shape = WAVES;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：在陷阱周围召唤 1-3 只当前楼层的怪物。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 生成 1-3 只怪物
- 触发怪物位置的陷阱
- 更新地图占用

**核心实现逻辑**：
```java
@Override
public void activate() {
    // 确定召唤数量：1 + 随机增加
    int nMobs = 1;
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
        if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
            candidates.add(p);
        }
    }

    ArrayList<Integer> respawnPoints = new ArrayList<>();
    while (nMobs > 0 && candidates.size() > 0) {
        int index = Random.index(candidates);
        respawnPoints.add(candidates.remove(index));
        nMobs--;
    }

    // 生成怪物
    ArrayList<Mob> mobs = new ArrayList<>();
    for (Integer point : respawnPoints) {
        Mob mob = Dungeon.level.createMob();
        // 大型怪物需要开阔空间
        while (Char.hasProp(mob, Char.Property.LARGE) && !Dungeon.level.openSpace[point]) {
            mob = Dungeon.level.createMob();
        }
        if (mob != null) {
            if (mob.state != mob.PASSIVE) {
                mob.state = mob.WANDERING;
            }
            mob.pos = point;
            GameScene.add(mob, DELAY);
            mobs.add(mob);
        }
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
- 大型怪物需要开阔空间
- 如果空间不足，召唤数量会减少

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发召唤效果（覆写自 Trap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `Dungeon.level.createMob()`：创建怪物
- `GameScene.add()`：添加怪物
- `ScrollOfTeleportation.appear()`：显示怪物

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
计算召唤数量（1-3 只）
    ↓
选择周围可行走位置
    ↓
生成当前楼层的怪物
    ↓
触发怪物位置的陷阱
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.summoningtrap.name` | 召唤陷阱 | 陷阱名称 |
| `levels.traps.summoningtrap.desc` | 触发这个陷阱将召唤本区域的一些怪物到这里。 | 陷阱描述 |

### 依赖的资源
无特殊音效资源。

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置召唤陷阱
SummoningTrap trap = new SummoningTrap();
trap.set(position);
trap.hide();

// 触发后召唤 1-3 只当前楼层的怪物
// 怪物类型由 Dungeon.level.createMob() 决定
```

## 12. 开发注意事项

### 召唤概率
- 50% 概率召唤 2 只
- 25% 概率召唤 3 只
- 25% 概率召唤 1 只

### 怪物特性
- 类型由当前楼层决定
- 大型怪物需要开阔空间
- 初始状态为游荡

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整召唤数量计算逻辑
- 可修改怪物类型选择规则

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：已覆盖静态常量和继承字段
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关信息
- [x] 是否明确说明了注意事项与扩展点：已详细说明