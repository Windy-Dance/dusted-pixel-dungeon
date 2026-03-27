# GrimTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/GrimTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 142 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`GrimTrap`（即死陷阱）负责向最近的目标发射致命冲击，能够瞬间击杀大部分敌人。

### 系统定位
陷阱系统中的高致死型陷阱。无法隐藏，避免放置在走廊。

### 不负责什么
- 不负责目标的最终死亡判定（由目标的 `damage()` 方法处理）
- 不保证击杀所有目标（英雄有伤害上限）

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = GREY`，`shape = LARGE_DOT`，`canBeHidden = false`，`avoidsHallways = true`
- **activate() 方法**：发射致命冲击

### 主要逻辑块概览
1. **目标搜索**：寻找最近的有效目标
2. **伤害计算**：基于目标 HP 和 HT 计算
3. **视觉效果**：播放暗影魔法弹效果
4. **伤害应用**：对目标造成大量伤害

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
| `activate()` | 实现即死冲击效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Ballistica` | 弹道计算 |
| `MagicMissile.SHADOW` | 暗影魔法弹效果 |
| `ShadowParticle` | 暗影粒子效果 |
| `Badges` | 成就系统 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | GREY (7) | 灰色陷阱 |
| `shape` | LARGE_DOT (6) | 大点图案 |
| `canBeHidden` | false | 无法隐藏 |
| `avoidsHallways` | true | 避免放置在走廊 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = GREY;
    shape = LARGE_DOT;
    canBeHidden = false;
    avoidsHallways = true;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：向最近的目标发射致命冲击。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 对目标造成大量伤害
- 可能导致目标死亡
- 播放视觉效果和音效

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
                
                // 伤害计算：HP/2 + HT/2
                int damage = Math.round(finalTarget.HT / 2f + finalTarget.HP / 2f);

                // 英雄伤害上限：90% HT
                if (finalTarget == Dungeon.hero) {
                    damage = (int) Math.min(damage, finalTarget.HT * 0.9f);
                }

                final int finalDmg = damage;
                if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[target.pos]) {
                    // 视觉效果
                    ((MagicMissile) finalTarget.sprite.parent.recycle(MagicMissile.class)).reset(
                            MagicMissile.SHADOW,
                            DungeonTilemap.tileCenterToWorld(pos),
                            finalTarget.sprite.center(),
                            new Callback() {
                                @Override
                                public void call() {
                                    finalTarget.damage(finalDmg, GrimTrap.this);
                                    if (finalTarget == Dungeon.hero) {
                                        Sample.INSTANCE.play(Assets.Sounds.CURSED);
                                        if (!finalTarget.isAlive()) {
                                            Badges.validateDeathFromGrimOrDisintTrap();
                                            Dungeon.fail(GrimTrap.this);
                                            GLog.n(Messages.get(GrimTrap.class, "ondeath"));
                                            if (reclaimed) Badges.validateDeathFromFriendlyMagic();
                                        }
                                    } else {
                                        Sample.INSTANCE.play(Assets.Sounds.BURNING);
                                    }
                                    finalTarget.sprite.emitter().burst(ShadowParticle.UP, 10);
                                    next();
                                }
                            });
                    return false;
                } else {
                    finalTarget.damage(finalDmg, GrimTrap.this);
                    return true;
                }
            } else {
                CellEmitter.get(pos).burst(ShadowParticle.UP, 10);
                Sample.INSTANCE.play(Assets.Sounds.BURNING);
                return true;
            }
        }
    });
}
```

**边界情况**：
- 英雄伤害上限为 90% HT，不会一击必杀
- 隐形目标被视为在最大距离
- 无目标时播放空效果

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发即死冲击（覆写自 Trap） |

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
- `MagicMissile.reset()`：创建暗影魔法弹
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
计算伤害（HP/2 + HT/2）
    ↓
发射暗影魔法弹
    ↓
造成伤害
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.grimtrap.name` | 即死陷阱 | 陷阱名称 |
| `levels.traps.grimtrap.ondeath` | 你被即死陷阱的冲击彻底击杀... | 死亡消息 |
| `levels.traps.grimtrap.desc` | 非常强大的破坏魔法储存在这个陷阱里，足以瞬间杀死除了状态最佳的英雄外的所有生物。触发它将向最近的生物发送一个致命的远程冲击魔法。\n\n幸好的是，触发机关并没有被隐藏起来。 | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.CURSED` | 诅咒音效（击中英雄） |
| 音效 | `Assets.Sounds.BURNING` | 燃烧音效（击中其他） |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置即死陷阱（无法隐藏）
GrimTrap trap = new GrimTrap();
trap.set(position);
// 注意：canBeHidden = false，陷阱总是可见的

// 触发后向最近目标发射暗影冲击
// 伤害 = 目标HP/2 + 目标HT/2
// 英雄伤害上限 = 90% HT
```

## 12. 开发注意事项

### 状态依赖
- `canBeHidden = false`：陷阱总是可见
- `avoidsHallways = true`：不会生成在走廊

### 伤害计算
- 基础伤害：`(HP + HT) / 2`
- 英雄保护：伤害不超过 90% HT
- 怪物：可能被一击必杀

### 视觉效果
- 使用 Actor 延迟执行
- 魔法弹需要等待动画完成

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整伤害计算公式
- 可修改英雄保护比例

### 不建议修改的位置
- `canBeHidden = false` 是核心设计特性

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已详细说明