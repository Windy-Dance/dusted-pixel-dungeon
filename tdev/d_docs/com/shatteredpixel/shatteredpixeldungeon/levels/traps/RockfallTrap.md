# RockfallTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/RockfallTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 122 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`RockfallTrap`（落石陷阱）负责触发后在房间范围内落下岩石，造成伤害和麻痹效果。

### 系统定位
陷阱系统中的范围伤害型陷阱。无法隐藏，避免放置在走廊。覆盖整个房间或 5x5 区域。

### 不负责什么
- 不负责岩石的后续行为
- 不造成地形改变（不同于 GnollRockfallTrap）

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = GREY`，`shape = DIAMOND`，`canBeHidden = false`，`avoidsHallways = true`
- **activate() 方法**：执行落石效果

### 主要逻辑块概览
1. **范围确定**：确定落石范围（房间或 5x5）
2. **伤害处理**：对范围内角色造成伤害
3. **麻痹效果**：施加麻痹效果
4. **视觉效果**：播放岩石下落效果

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
| `activate()` | 实现落石效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `RegularLevel` | 规则关卡类 |
| `Room` | 房间类 |
| `Paralysis` | 麻痹 Buff |
| `Speck.ROCK` | 岩石粒子 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | GREY (7) | 灰色陷阱 |
| `shape` | DIAMOND (4) | 菱形图案 |
| `canBeHidden` | false | 无法隐藏 |
| `avoidsHallways` | true | 避免放置在走廊 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = GREY;
    shape = DIAMOND;
    canBeHidden = false;
    avoidsHallways = true;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：在房间或 5x5 范围内落下岩石，造成伤害和麻痹。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 对范围内角色造成伤害
- 施加麻痹效果
- 播放视觉效果和音效
- 屏幕震动

**核心实现逻辑**：
```java
@Override
public void activate() {
    ArrayList<Integer> rockCells = new ArrayList<>();

    // 确定落石范围
    boolean onGround = Dungeon.level.traps.get(pos) == this;
    Room r = null;
    if (Dungeon.level instanceof RegularLevel) {
        r = ((RegularLevel) Dungeon.level).room(pos);
    }

    if (onGround && r != null) {
        // 房间范围
        int cell;
        for (Point p : r.getPoints()) {
            cell = Dungeon.level.pointToCell(p);
            if (!Dungeon.level.solid[cell]) {
                rockCells.add(cell);
            }
        }
    } else {
        // 5x5 范围
        PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                rockCells.add(i);
            }
        }
    }

    boolean seen = false;
    for (int cell : rockCells) {
        if (Dungeon.level.heroFOV[cell]) {
            CellEmitter.get(cell - Dungeon.level.width()).start(Speck.factory(Speck.ROCK), 0.07f, 10);
            seen = true;
        }

        Char ch = Actor.findChar(cell);

        if (ch != null && ch.isAlive()) {
            if (ch instanceof Mob) {
                Buff.prolong(ch, Trap.HazardAssistTracker.class, HazardAssistTracker.DURATION);
            }
            // 伤害：5+深度 到 10+深度*2
            int damage = Random.NormalIntRange(5 + scalingDepth(), 10 + scalingDepth() * 2);
            damage -= ch.drRoll();
            ch.damage(Math.max(damage, 0), this);

            if (ch.isActive()) {
                Buff.prolong(ch, Paralysis.class, Paralysis.DURATION);
            } else if (!ch.isAlive() && ch == Dungeon.hero) {
                Dungeon.fail(this);
                GLog.n(Messages.get(this, "ondeath"));
                if (reclaimed) Badges.validateDeathFromFriendlyMagic();
            }
        }
    }

    if (seen) {
        PixelScene.shake(3, 0.7f);
        Sample.INSTANCE.play(Assets.Sounds.ROCKS);
    }
}
```

**边界情况**：
- 如果陷阱在房间内，落石覆盖整个房间
- 如果不在房间内，落石覆盖 5x5 区域
- 伤害会被护甲减免

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发落石效果（覆写自 Trap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `PathFinder.buildDistanceMap()`：计算范围
- `Buff.prolong()`：施加麻痹
- `CellEmitter.get()`：播放粒子效果
- `PixelScene.shake()`：屏幕震动

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
确定落石范围（房间或 5x5）
    ↓
对范围内角色造成伤害和麻痹
    ↓
播放视觉效果
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.rockfalltrap.name` | 落石陷阱 | 陷阱名称 |
| `levels.traps.rockfalltrap.ondeath` | 你被落石砸扁了... | 死亡消息 |
| `levels.traps.rockfalltrap.desc` | 这个陷阱和头顶上一片松散的岩石相连，触发它会导致石块崩塌砸向整个房间！如果这种陷阱不是在某个房间内，石块会砸向陷阱周围的一定区域。\n\n幸运的是，触发机关并没有被隐藏起来。 | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.ROCKS` | 岩石音效 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置落石陷阱（无法隐藏）
RockfallTrap trap = new RockfallTrap();
trap.set(position);
// 注意：canBeHidden = false，陷阱总是可见的

// 触发后在房间或 5x5 范围内落下岩石
// 伤害 = 5+深度 到 10+深度*2 - 护甲
// 麻痹效果
```

## 12. 开发注意事项

### 状态依赖
- `canBeHidden = false`：陷阱总是可见
- `avoidsHallways = true`：不会生成在走廊

### 范围规则
- 在房间内：覆盖整个房间
- 不在房间内：覆盖 5x5 区域

### 伤害计算
- 最小伤害：5 + depth
- 最大伤害：10 + depth * 2
- 深度 1 时：6-12
- 深度 26 时：31-62

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整伤害范围
- 可修改麻痹持续时间

### 与 GnollRockfallTrap 的区别
| 特性 | RockfallTrap | GnollRockfallTrap |
|------|--------------|-------------------|
| 伤害 | 较高，计算护甲 | 较低，忽略护甲 |
| 范围 | 房间或 5x5 | 5x5，排除木栅栏周边 |
| 麻痹 | 固定时长 | 守卫 10，其他 3 |
| 岩石生成 | 无 | 矿层中可能生成 |

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已详细说明