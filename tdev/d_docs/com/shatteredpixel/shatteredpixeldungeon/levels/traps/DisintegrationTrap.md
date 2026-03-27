# DisintegrationTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/DisintegrationTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 104 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`DisintegrationTrap`（解离陷阱）负责触发后向最近的目标发射解离射线，造成大量伤害并可能破坏物品。

### 系统定位
陷阱系统中的高伤害远程攻击型陷阱。具有无法隐藏、避免走廊的特殊属性。

### 不负责什么
- 不负责目标的最终选择逻辑（由 `activate()` 内部实现）
- 不负责死亡处理（由目标自身的 `damage()` 方法处理）

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = VIOLET`，`shape = CROSSHAIR`，`canBeHidden = false`，`avoidsHallways = true`
- **activate() 方法**：发射解离射线

### 主要逻辑块概览
1. **目标搜索**：寻找最近的可见目标
2. **物品破坏**：破坏陷阱位置的物品堆
3. **射线发射**：向目标发射解离射线
4. **伤害计算**：造成 30-50 + 深度的伤害

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
| `activate()` | 实现解离射线效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Ballistica` | 弹道计算 |
| `Beam.DeathRay` | 解离射线视觉效果 |
| `Char` | 角色基类 |
| `Heap` | 物品堆 |
| `Badges` | 成就系统 |
| `GLog` | 日志输出 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | VIOLET (5) | 紫色陷阱 |
| `shape` | CROSSHAIR (5) | 十字准星图案 |
| `canBeHidden` | false | 无法隐藏 |
| `avoidsHallways` | true | 避免放置在走廊 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = VIOLET;
    shape = CROSSHAIR;
    canBeHidden = false;
    avoidsHallways = true;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：向最近的目标发射解离射线，造成大量伤害。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 破坏陷阱位置的物品堆
- 对目标造成伤害
- 播放视觉效果和音效
- 可能导致英雄死亡

**核心实现逻辑**：
```java
@Override
public void activate() {
    Char target = Actor.findChar(pos);

    // 目标搜索逻辑
    float range = Math.max(6, Dungeon.level.viewDistance) + 0.5f;
    if (target == null) {
        float closestDist = Float.MAX_VALUE;
        for (Char ch : Actor.chars()) {
            if (!ch.isAlive()) continue;
            float curDist = Dungeon.level.trueDistance(pos, ch.pos);
            // 隐形目标视为最大距离
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

    // 破坏物品堆
    Heap heap = Dungeon.level.heaps.get(pos);
    if (heap != null) heap.explode();

    // 发射解离射线
    if (target != null) {
        if (target instanceof Mob) {
            Buff.prolong(target, Trap.HazardAssistTracker.class, HazardAssistTracker.DURATION);
        }
        // 视觉效果
        if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[target.pos]) {
            Sample.INSTANCE.play(Assets.Sounds.RAY);
            ShatteredPixelDungeon.scene().add(
                new Beam.DeathRay(DungeonTilemap.tileCenterToWorld(pos), target.sprite.center()));
        }
        // 造成伤害：30-50 + 深度
        target.damage(Random.NormalIntRange(30, 50) + scalingDepth(), this);
        
        // 英雄死亡处理
        if (target == Dungeon.hero) {
            Hero hero = (Hero) target;
            if (!hero.isAlive()) {
                Badges.validateDeathFromGrimOrDisintTrap();
                Dungeon.fail(this);
                GLog.n(Messages.get(this, "ondeath"));
                if (reclaimed) Badges.validateDeathFromFriendlyMagic();
            }
        }
    }
}
```

**边界情况**：
- 隐形目标被视为在最大距离
- 超出视野范围的目标不会被选中
- 如果陷阱位置有角色，优先攻击该角色

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发解离射线效果（覆写自 Trap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `Actor.chars()`：获取所有角色
- `Ballistica`：计算弹道
- `Beam.DeathRay`：创建射线效果
- `target.damage()`：造成伤害
- `Badges.validateDeathFromGrimOrDisintTrap()`：验证成就

### 系统流程位置
```
角色踩中陷阱（或陷阱被触发）
    ↓
trigger() 调用 activate()
    ↓
搜索最近的有效目标
    ↓
发射解离射线
    ↓
造成 30-50 + 深度的伤害
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.disintegrationtrap.name` | 解离陷阱 | 陷阱名称 |
| `levels.traps.disintegrationtrap.one` | 陷阱解离了你的%s！ | 物品破坏提示（单个） |
| `levels.traps.disintegrationtrap.some` | 陷阱解离了你一部分的%s！ | 物品破坏提示（多个） |
| `levels.traps.disintegrationtrap.ondeath` | 你被解离陷阱击杀... | 死亡消息 |
| `levels.traps.disintegrationtrap.desc` | 被触发时，这个陷阱将会用解离射线袭击离它最近的目标，造成显著伤害的同时破坏物品。\n\n幸运的是，触发机关并没有被隐藏起来。 | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.RAY` | 射线音效 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置解离陷阱（无法隐藏）
DisintegrationTrap trap = new DisintegrationTrap();
trap.set(position);
// 注意：canBeHidden = false，陷阱总是可见的

// 触发后向最近目标发射解离射线
// 伤害 = 30-50 + 当前深度
```

## 12. 开发注意事项

### 状态依赖
- `canBeHidden = false`：陷阱总是可见
- `avoidsHallways = true`：不会生成在走廊

### 目标选择规则
1. 优先选择陷阱位置上的角色
2. 选择视野范围内最近的目标
3. 隐形目标被视为在最大距离
4. 英雄在同等距离下有优先级

### 伤害计算
- 基础伤害：30-50
- 深度加成：+depth
- 深度 1 时：31-51 伤害
- 深度 26 时：56-76 伤害

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整伤害计算公式
- 可修改目标选择逻辑

### 不建议修改的位置
- `canBeHidden` 和 `avoidsHallways` 是设计特性，不建议修改

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已详细说明