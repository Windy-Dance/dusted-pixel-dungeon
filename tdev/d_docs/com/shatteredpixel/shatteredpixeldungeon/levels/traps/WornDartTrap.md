# WornDartTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/WornDartTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 125 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`WornDartTrap`（老化飞镖陷阱）负责触发后向最近的目标发射普通飞镖，造成物理伤害。

### 系统定位
陷阱系统中的基础远程攻击型陷阱。无法隐藏，避免放置在走廊，是 `PoisonDartTrap` 的基础版本（无中毒效果）。

### 不负责什么
- 不造成中毒效果（不同于 PoisonDartTrap）
- 不保证击中目标（可能被躲避）

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = GREY`，`shape = CROSSHAIR`，`canBeHidden = false`，`avoidsHallways = true`
- **activate() 方法**：发射飞镖

### 主要逻辑块概览
1. **目标搜索**：寻找最近的有效目标
2. **飞镖发射**：发射飞镖并显示飞行动画
3. **伤害计算**：造成 4-8 点物理伤害

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
| `activate()` | 实现飞镖发射效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Dart` | 普通飞镖物品 |
| `Ballistica` | 弹道计算 |
| `MissileSprite` | 飞镖动画 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | GREY (7) | 灰色陷阱 |
| `shape` | CROSSHAIR (5) | 十字准星图案 |
| `canBeHidden` | false | 无法隐藏 |
| `avoidsHallways` | true | 避免放置在走廊 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = GREY;
    shape = CROSSHAIR;
    canBeHidden = false;
    avoidsHallways = true;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：向最近的目标发射飞镖，造成物理伤害。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 对目标造成物理伤害
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

            // 目标搜索
            float range = Math.max(6, Dungeon.level.viewDistance) + 0.5f;
            if (target == null) {
                float closestDist = Float.MAX_VALUE;
                for (Char ch : Actor.chars()) {
                    if (!ch.isAlive()) continue;
                    float curDist = Dungeon.level.trueDistance(pos, ch.pos);
                    if (ch.invisible > 0) curDist = Math.max(curDist, range);
                    Ballistica bolt = new Ballistica(pos, ch.pos, Ballistica.PROJECTILE);
                    if (bolt.collisionPos == ch.pos
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
                            .reset(pos, finalTarget.sprite, new Dart(), new Callback() {
                                @Override
                                public void call() {
                                    int dmg = Random.NormalIntRange(4, 8) - finalTarget.drRoll();
                                    finalTarget.damage(dmg, WornDartTrap.this);
                                    if (finalTarget == Dungeon.hero && !finalTarget.isAlive()) {
                                        Dungeon.fail(WornDartTrap.this);
                                        GLog.n(Messages.get(WornDartTrap.class, "ondeath"));
                                        if (reclaimed) Badges.validateDeathFromFriendlyMagic();
                                    }
                                    Sample.INSTANCE.play(Assets.Sounds.HIT, 1, 1, Random.Float(0.8f, 1.25f));
                                    finalTarget.sprite.bloodBurstA(finalTarget.sprite.center(), dmg);
                                    finalTarget.sprite.flash();
                                    next();
                                }
                            });
                    return false;
                } else {
                    finalTarget.damage(Random.NormalIntRange(4, 8) - finalTarget.drRoll(), WornDartTrap.this);
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

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发飞镖发射（覆写自 Trap） |

### 内部辅助方法
无

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
- `target.damage()`：造成伤害

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
发射飞镖动画
    ↓
造成 4-8 点物理伤害
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.worndarttrap.name` | 老化飞镖陷阱 | 陷阱名称 |
| `levels.traps.worndarttrap.ondeath` | 你被老化飞镖陷阱击杀... | 死亡消息 |
| `levels.traps.worndarttrap.desc` | 附近一定藏着一个小型飞镖发射器，激活这个陷阱会导致它向最近的目标射出一个飞镖。\n\n年久失修导致它并不怎么危险，触发机关甚至没有被隐藏起来... | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.HIT` | 命中音效 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置老化飞镖陷阱（无法隐藏）
WornDartTrap trap = new WornDartTrap();
trap.set(position);
// 注意：canBeHidden = false，陷阱总是可见的

// 触发后向最近目标发射飞镖
// 伤害：4-8 - 护甲减免
// 无中毒效果
```

## 12. 开发注意事项

### 状态依赖
- `canBeHidden = false`：陷阱总是可见
- `avoidsHallways = true`：不会生成在走廊

### 与 PoisonDartTrap 的区别
| 特性 | WornDartTrap | PoisonDartTrap |
|------|--------------|----------------|
| 伤害 | 4-8 | 4-8 |
| 中毒 | 无 | 有（基于深度） |
| 颜色 | GREY | GREEN |
| 描述 | 年久失修，不危险 | 标准毒镖陷阱 |

### 伤害计算
- 基础伤害：4-8
- 减免：护甲值
- 最终伤害：max(0, 基础伤害 - 护甲)

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整伤害范围
- 可添加额外效果

### 设计意图
作为基础陷阱，伤害较低且总是可见，适合作为玩家的入门陷阱体验。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已详细说明