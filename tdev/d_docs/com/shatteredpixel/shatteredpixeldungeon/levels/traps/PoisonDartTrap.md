# PoisonDartTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/PoisonDartTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 148 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`PoisonDartTrap`（毒镖陷阱）负责触发后向最近的目标发射毒镖，造成伤害并施加中毒效果。

### 系统定位
陷阱系统中的远程攻击型陷阱。无法隐藏，避免放置在走廊。

### 不负责什么
- 不保证击中目标（可能被躲避）
- 不负责中毒的具体效果（由 Poison Buff 处理）

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = GREEN`，`shape = CROSSHAIR`，`canBeHidden = false`，`avoidsHallways = true`
- **poisonAmount() 方法**：计算中毒量
- **canTarget(Char) 方法**：判断是否可以攻击目标
- **activate() 方法**：发射毒镖

### 主要逻辑块概览
1. **目标搜索**：寻找最近的有效目标
2. **飞镖发射**：发射毒镖并显示飞行动画
3. **伤害计算**：造成 4-8 点物理伤害
4. **中毒效果**：施加中毒效果

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
| `activate()` | 实现毒镖发射效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `PoisonDart` | 毒镖物品 |
| `Ballistica` | 弹道计算 |
| `MissileSprite` | 飞镖动画 |
| `Poison` | 中毒 Buff |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | GREEN (3) | 绿色陷阱 |
| `shape` | CROSSHAIR (5) | 十字准星图案 |
| `canBeHidden` | false | 无法隐藏 |
| `avoidsHallways` | true | 避免放置在走廊 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = GREEN;
    shape = CROSSHAIR;
    canBeHidden = false;
    avoidsHallways = true;
}
```

## 7. 方法详解

### poisonAmount()

**可见性**：protected

**是否覆写**：可被子类覆写

**方法职责**：计算中毒效果量。

**参数**：无

**返回值**：int，中毒量

**核心实现逻辑**：
```java
protected int poisonAmount() {
    return 8 + Math.round(2 * scalingDepth() / 3f);
}
```

---

### canTarget(Char ch)

**可见性**：protected

**是否覆写**：可被子类覆写（如 TenguDartTrap）

**方法职责**：判断是否可以攻击指定目标。

**参数**：
- `ch` (Char)：目标角色

**返回值**：boolean，是否可以攻击

**核心实现逻辑**：
```java
protected boolean canTarget(Char ch) {
    return true;
}
```

---

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：向最近的目标发射毒镖。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 对目标造成物理伤害
- 施加中毒效果
- 播放飞镖动画和音效

**核心实现逻辑**：
```java
@Override
public void activate() {
    Actor.add(new Actor() {
        {
            actPriority = VFX_PRIO;
        }

        @Override
        protected boolean act() {
            Actor.remove(this);
            Char target = Actor.findChar(pos);

            if (target != null && !canTarget(target)) {
                target = null;
            }

            // 目标搜索
            float range = Math.max(6, Dungeon.level.viewDistance) + 0.5f;
            if (target == null) {
                float closestDist = Float.MAX_VALUE;
                for (Char ch : Actor.chars()) {
                    if (!ch.isAlive()) continue;
                    float curDist = Dungeon.level.trueDistance(pos, ch.pos);
                    if (ch.invisible > 0) curDist = Math.max(curDist, range);
                    Ballistica bolt = new Ballistica(pos, ch.pos, Ballistica.PROJECTILE);
                    if (canTarget(ch) && bolt.collisionPos == ch.pos
                            && (curDist < closestDist || (curDist == closestDist && target instanceof Hero))) {
                        target = ch;
                        closestDist = curDist;
                    }
                }
                if (closestDist > range) {
                    target = null;
                }
            }

            if (target != null) {
                if (target instanceof Mob) {
                    Buff.prolong(target, Trap.HazardAssistTracker.class, HazardAssistTracker.DURATION);
                }
                final Char finalTarget = target;
                if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[target.pos]) {
                    // 飞镖动画
                    ((MissileSprite) ShatteredPixelDungeon.scene().recycle(MissileSprite.class))
                            .reset(pos, finalTarget.sprite, new PoisonDart(), new Callback() {
                                @Override
                                public void call() {
                                    int dmg = Random.NormalIntRange(4, 8) - finalTarget.drRoll();
                                    finalTarget.damage(dmg, PoisonDartTrap.this);
                                    if (finalTarget == Dungeon.hero) {
                                        if (Dungeon.depth == 10) {
                                            Statistics.qualifiedForBossChallengeBadge = false;
                                            Statistics.bossScores[1] -= 100;
                                        }
                                        if (!finalTarget.isAlive()) {
                                            Dungeon.fail(PoisonDartTrap.this);
                                            GLog.n(Messages.get(PoisonDartTrap.class, "ondeath"));
                                            if (reclaimed) Badges.validateDeathFromFriendlyMagic();
                                        }
                                    }
                                    Buff.affect(finalTarget, Poison.class).set(poisonAmount());
                                    Sample.INSTANCE.play(Assets.Sounds.HIT, 1, 1, Random.Float(0.8f, 1.25f));
                                    finalTarget.sprite.bloodBurstA(finalTarget.sprite.center(), dmg);
                                    finalTarget.sprite.flash();
                                    next();
                                }
                            });
                    return false;
                } else {
                    finalTarget.damage(Random.NormalIntRange(4, 8) - finalTarget.drRoll(), PoisonDartTrap.this);
                    Buff.affect(finalTarget, Poison.class).set(poisonAmount());
                    return true;
                }
            } else {
                return true;
            }
        }
    });
}
```

**边界情况**：
- 隐形目标被视为在最大距离
- 伤害会被护甲减免
- 天狗战斗中有特殊处理

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发毒镖发射（覆写自 Trap） |
| `poisonAmount()` | 计算中毒量（可覆写） |
| `canTarget(Char)` | 判断是否可攻击（可覆写） |

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `Actor.add()`：添加视觉效果 Actor
- `Ballistica`：计算弹道
- `MissileSprite.reset()`：创建飞镖动画
- `Buff.affect()`：施加中毒

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
创建视觉效果 Actor
    ↓
搜索最近目标
    ↓
发射毒镖动画
    ↓
造成伤害和中毒效果
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.poisondarttrap.name` | 毒镖陷阱 | 陷阱名称 |
| `levels.traps.poisondarttrap.ondeath` | 你被毒镖陷阱击杀... | 死亡消息 |
| `levels.traps.poisondarttrap.desc` | 附近一定藏着一个小型飞镖发射器，激活这个陷阱会导致它向最近的目标射出一个毒镖。\n\n幸好的是，触发机关并没有被隐藏起来。 | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.HIT` | 命中音效 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置毒镖陷阱（无法隐藏）
PoisonDartTrap trap = new PoisonDartTrap();
trap.set(position);
// 注意：canBeHidden = false，陷阱总是可见的

// 触发后向最近目标发射毒镖
// 伤害：4-8 - 护甲减免
// 中毒量：8 + 2*深度/3
```

## 12. 开发注意事项

### 状态依赖
- `canBeHidden = false`：陷阱总是可见
- `avoidsHallways = true`：不会生成在走廊

### 中毒量计算
- 基础量：8
- 深度加成：2 * depth / 3
- 深度 1 时：约 9
- 深度 26 时：约 25

### 天狗战斗特殊处理
- 深度 10 触发时影响成就评分

## 13. 修改建议与扩展点

### 适合扩展的位置
- `poisonAmount()`：调整中毒量计算
- `canTarget(Char)`：添加目标筛选逻辑（如 TenguDartTrap）

### 子类扩展示例
```java
public class TenguDartTrap extends PoisonDartTrap {
    @Override
    protected boolean canTarget(Char ch) {
        return ch != null && !(ch instanceof Tengu);
    }
}
```

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`, `poisonAmount()`, `canTarget()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已详细说明