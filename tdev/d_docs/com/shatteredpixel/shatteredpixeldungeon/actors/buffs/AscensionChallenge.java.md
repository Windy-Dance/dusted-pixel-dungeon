# AscensionChallenge 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/AscensionChallenge.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| **类类型** | public class |
| **继承关系** | extends Buff |
| **代码行数** | 417 行 |
| **内部类** | AscensionBuffBlocker |
| **官方中文名** | 护符诡咒 |

## 2. 文件职责说明

AscensionChallenge 类实现返程阶段的“护符诡咒”机制。它负责维护当前诅咒层数 `stacks`，并在返程过程中持续强化敌人、限制英雄、在楼层切换时追加压力，同时在击杀被强化敌人后降低层数。

**核心职责**：
- 维护护符诡咒的层数与持续伤害累计值
- 提供敌人属性倍率、敌人速度倍率、英雄速度修正等静态规则
- 在楼层切换时刷新返程状态、重置门锁、补怪和处理商店老板撤离
- 在击杀有效敌人后降低层数并触发提示
- 在高层数时对英雄造成持续伤害

## 3. 结构总览

```
AscensionChallenge (extends Buff)
├── 静态字段
│   └── modifiers: HashMap<Class<? extends Mob>, Float>
├── 实例字段
│   ├── stacks: float
│   ├── damageInc: float
│   ├── stacksLowered: boolean
│   └── justAscended: boolean
├── 方法
│   ├── statModifier(Char): float$
│   ├── beckonEnemies(): void$
│   ├── enemySpeedModifier(Mob): float$
│   ├── modifyHeroSpeed(float): float$
│   ├── qualifiedForPacifist(): boolean$
│   ├── processEnemyKill(Char): void$
│   ├── AscensionCorruptResist(Mob): int$
│   ├── onLevelSwitch(): void
│   ├── saySwitch(): void
│   ├── act(): boolean
│   ├── icon(): int
│   ├── tintIcon(Image): void
│   ├── desc(): String
│   ├── storeInBundle(Bundle): void
│   └── restoreFromBundle(Bundle): void
└── 内部类
    └── AscensionBuffBlocker extends Buff
```

## 4. 继承与协作关系

### 继承关系图

```mermaid
classDiagram
    Buff <|-- AscensionChallenge
    Buff <|-- AscensionBuffBlocker
    AscensionChallenge +-- AscensionBuffBlocker
```

### 协作关系

| 协作类 | 协作方式 |
|--------|----------|
| **Buff** | 父类，提供附着、行动调度与存档基础能力 |
| **Mob / Char / Hero** | 返程强化与惩罚的主要作用对象 |
| **Ratmogrify.TransmogRat** | 在多个静态方法里会回溯到原始怪物 |
| **AscensionBuffBlocker** | 标记不受返程强化影响的单位 |
| **Statistics** | 维护 `highestAscent` 等返程进度数据 |
| **Badges** | 英雄被诅咒直接击杀时验证徽章 |
| **Level / Terrain** | 楼层切换时恢复被英雄上锁的门 |
| **Shopkeeper** | 返程较深时触发逃离行为 |
| **DriedRose.GhostHero** | Boss 层首次返程时显示提示 |
| **BuffIndicator** | UI 图标刷新与显示 |
| **Messages / GLog** | 文本描述与提示消息 |

## 5. 字段与常量详解

### 静态字段 `modifiers`

`modifiers` 把若干怪物类型映射到返程属性倍率，例如：

| 怪物类型 | 倍率 |
|----------|------|
| `Rat` | `10f` |
| `Snake` / `Gnoll` | `9f` |
| `Swarm` | `8.5f` |
| `Crab` / `Slime` | `8f` |
| `Skeleton` / `Thief` | `5f` |
| `DM100` | `4.5f` |
| `Guard` / `Necromancer` | `4f` |
| `Bat` | `2.5f` |
| `Brute` / `Shaman` | `2.25f` |
| `Spinner` / `DM200` | `2f` |
| `Ghoul` / `Elemental` | `1.67f` |
| `Warlock` / `Monk` | `1.5f` |
| `Golem` | `1.33f` |
| `RipperDemon` / `Succubus` | `1.2f` |
| `Eye` / `Scorpio` | `1.1f` |

### 实例字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `stacks` | float | 当前护符诡咒层数 |
| `damageInc` | float | 高层数直接伤害的累计值 |
| `stacksLowered` | boolean | 是否曾通过击杀敌人降低过层数 |
| `justAscended` | boolean | 本次楼层切换是否属于首次上行进入该层 |

### Bundle 键

| 常量 | 值 | 用途 |
|------|-----|------|
| `STACKS` | `enemy_stacks` | 保存当前层数 |
| `DAMAGE` | `damage_inc` | 保存累计伤害 |
| `STACKS_LOWERED` | `stacks_lowered` | 保存是否降过层 |

## 6. 构造与初始化机制

初始化块：

```java
{
    revivePersists = true;
}
```

表示该 Buff 会在复活等场景后保留。

常见存在方式：返程阶段英雄身上长期持有该 Buff，随后通过 `onLevelSwitch()` 和 `act()` 持续推进机制。

## 7. 方法详解

### statModifier(Char ch)

返回某个单位在护符诡咒影响下的属性倍率。\n
逻辑要点：
- 若英雄没有 `AscensionChallenge`，返回 `1`
- 若目标是 `TransmogRat`，改用其原始怪物
- 若目标拥有 `AscensionBuffBlocker`，返回 `1`
- 否则按 `modifiers` 中第一个可匹配父类返回倍率

### beckonEnemies()

当英雄存在本 Buff 且 `stacks >= 2f` 时，遍历当前楼层怪物；对距离英雄大于 8 且属于敌对阵营的怪物调用 `beckon(Dungeon.hero.pos)`。

### enemySpeedModifier(Mob m)

当：
- 英雄存在本 Buff
- 怪物阵营为 `ENEMY`
- `stacks >= 4f`
- 怪物状态不是 `HUNTING` 且不是 `FLEEING`

时返回 `2`，否则返回 `1`。

### modifyHeroSpeed(float speed)

当英雄存在本 Buff 且 `stacks >= 6f` 时返回：

```java
Math.min(speed/2f, 1f)
```

否则返回原速度。

### qualifiedForPacifist()

若英雄拥有本 Buff，则返回 `!stacksLowered`；否则返回 `false`。这意味着只要通过击杀降低过层数，就不再满足该判定。

### processEnemyKill(Char enemy)

这是返程“击杀降低层数”逻辑的核心。\n
**执行流程**：
1. 获取英雄身上的本 Buff；没有则返回。
2. 若目标是 `TransmogRat`，回溯成原始怪物。
3. 若目标有 `AscensionBuffBlocker`，直接返回。
4. 只有可在 `modifiers` 表中匹配到的怪物才计入降层。
5. 降层规则：
   - `Ghoul`、`RipperDemon`：`stacks -= 0.5f`
   - 其他有效怪物：`stacks -= 1`
6. 最低不低于 0。
7. 根据是否首次降层、是否跨过 2 层阈值，输出 `weaken` 提示。
8. 若英雄已满级且层数下降，则按 `10 * 清除层数` 给英雄发放“有效经验”。
9. 刷新 `BuffIndicator`。

### AscensionCorruptResist(Mob m)

用于给腐化相关逻辑返回一个抗性值。\n
逻辑：
- 若没有本 Buff，默认返回 `m.EXP`
- 若有 `AscensionBuffBlocker`，返回 `m.EXP`
- `RipperDemon` 返回 `10`
- `Ghoul` 返回 `7`
- 其他属于 `modifiers` 表中的怪物，返回 `Math.max(13, m.EXP)`

### onLevelSwitch()

处理上行切换楼层时的返程逻辑。\n
关键行为：
- 当 `Dungeon.depth < Statistics.highestAscent` 时，更新 `highestAscent` 并设置 `justAscended = true`
- 若是 Boss 层：
  - 满足饥饿值
  - 给英雄添加 `Healing`
- 若不是 Boss 层：
  - `stacks += 2f`
  - 把所有 `HERO_LKD_DR` 恢复成 `DOOR`
  - 清理当前楼层怪物（不能 reset 的直接移除）
  - 调用 `Dungeon.level.spawnMob(12)` 补一只怪
- 若 `Statistics.highestAscent < 20`，让当前楼层所有 `Shopkeeper` 逃跑

### saySwitch()

楼层切换后输出对应文本。\n
规则：
- Boss 层且 `justAscended`：输出 `break`，并让 `DriedRose.GhostHero` 说话
- 非 Boss 层：根据 `stacks` 依次输出 `almost` / `damage` / `slow` / `haste` / `beckon`
- 根据层数与是否曾降层，再输出 `weaken_info_no_kills` 或 `weaken_info`
- 最后把 `justAscended = false`

### act()

每回合：
1. 调用 `beckonEnemies()`
2. 若 `stacks >= 8` 且当前不是 Boss 层：
   - `damageInc += (stacks - 4) / 4f`
   - 当 `damageInc >= 1` 时，对目标造成 `(int)damageInc` 点伤害
   - 若目标是英雄且因此死亡：
     - `Badges.validateDeathFromFriendlyMagic()`
     - 输出 `on_kill`
     - `Dungeon.fail(Amulet.class)`
3. 若层数不够则把 `damageInc = 0`
4. `spend(TICK)`

### icon() / tintIcon()

- 图标：`BuffIndicator.AMULET`
- 染色依据 `stacks` 分段改变：绿色 -> 黄色 -> 橙色 -> 红色

### desc()

先读取基础 `desc`，然后按 `stacks` 拼接：
- `< 2`：`desc_clear`
- `>= 2`：追加 `desc_beckon`
- `>= 4`：追加 `desc_haste`
- `>= 6`：追加 `desc_slow`
- `>= 8`：追加 `desc_damage`

### storeInBundle() / restoreFromBundle()

保存并恢复：
- `stacks`
- `damageInc`
- `stacksLowered`

对旧存档，若没有 `STACKS_LOWERED`，则默认设为 `true`。

### 内部类 AscensionBuffBlocker

```java
public static class AscensionBuffBlocker extends Buff {}
```

这是一个空 Buff 类型，仅用于标记“该单位不受护符诡咒强化影响”。

## 8. 对外暴露能力

| 方法 | 用途 |
|------|------|
| `statModifier(Char)` | 查询单位返程属性倍率 |
| `enemySpeedModifier(Mob)` | 查询敌人速度倍率 |
| `modifyHeroSpeed(float)` | 修正英雄速度 |
| `processEnemyKill(Char)` | 处理击杀降层 |
| `AscensionCorruptResist(Mob)` | 返回腐化抗性参考值 |
| `onLevelSwitch()` | 楼层切换时推进返程逻辑 |
| `saySwitch()` | 楼层切换后输出提示 |

## 9. 运行机制与调用链

```
返程楼层切换
├── AscensionChallenge.onLevelSwitch()
│   ├── [非Boss层] stacks += 2
│   ├── 重置门锁 / 清怪 / 补怪
│   └── 处理 Shopkeeper 逃离
└── AscensionChallenge.saySwitch()
    └── 根据层数输出提示

每回合
└── AscensionChallenge.act()
    ├── beckonEnemies()
    └── [stacks >= 8] 对英雄直接伤害

有效敌人死亡
└── AscensionChallenge.processEnemyKill(enemy)
    ├── 降低 stacks
    ├── 输出 weaken 提示
    └── 刷新 BuffIndicator
```

## 10. 资源、配置与国际化关联

文件：`core/src/main/assets/messages/actors/actors_zh.properties`

```properties
actors.buffs.ascensionchallenge.name=护符诡咒
actors.buffs.ascensionchallenge.desc=不知为何古神仍能借护符维系自己对这个世界的影响，祂正借此设法阻止你向上返程！
actors.buffs.ascensionchallenge.desc_clear=护符中散发出的黑暗能量现今已被最大程度的削弱了。
actors.buffs.ascensionchallenge.desc_beckon=护符正在_呼唤远处的敌人_，向它们通告你的位置。
actors.buffs.ascensionchallenge.desc_haste=护符正在_为远处的敌人加速_，令它们得以更快接近你！
actors.buffs.ascensionchallenge.desc_slow=护符正在_使你减速_，并阻止了一切加速效果！
actors.buffs.ascensionchallenge.desc_damage=护符中涌动的黑暗能量已极为强大，它正在_直接对你造成伤害_！
```

## 11. 使用示例

```java
AscensionChallenge chal = Dungeon.hero.buff(AscensionChallenge.class);
if (chal != null) {
    chal.onLevelSwitch();
    chal.saySwitch();
}

float mod = AscensionChallenge.statModifier(enemy);
float heroSpeed = AscensionChallenge.modifyHeroSpeed(baseSpeed);
```

## 12. 开发注意事项

- `modifiers` 只覆盖指定怪物族谱；不在表里的怪物不会被该静态倍率系统强化。
- 楼层切换时不仅加层数，还会改门、清怪、补怪和驱赶商店老板，这使本类对返程流程有很强的全局耦合。
- `damageInc` 是累计型浮点伤害，不是每回合固定整数伤害。
- `AscensionBuffBlocker` 是多个静态方法都会检查的统一免疫标记。

## 13. 修改建议与扩展点

- 若后续返程强化表继续膨胀，可把 `modifiers` 迁移到独立配置或注册表。
- 若要降低 `onLevelSwitch()` 的耦合度，可把“重置门锁”“清怪补怪”“商店老板逃跑”拆到专门流程方法。

## 14. 事实核查清单

- [x] 已覆盖主要静态字段、实例字段、内部类与全部关键方法
- [x] 已验证继承关系 `extends Buff`
- [x] 已验证 `modifiers` 表的作用与示例倍率
- [x] 已验证 `stacks` 在击杀与切层中的增减逻辑
- [x] 已验证 `stacks >= 2/4/6/8` 的功能分层
- [x] 已验证 `AscensionBuffBlocker` 的阻断作用
- [x] 已验证 `Bundle` 存档字段与旧存档兼容逻辑
- [x] 已核对官方中文名与文案来自翻译文件
- [x] 无臆测性机制说明
