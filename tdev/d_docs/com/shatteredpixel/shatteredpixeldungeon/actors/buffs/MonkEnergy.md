# MonkEnergy 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/MonkEnergy.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| **类类型** | public class |
| **继承关系** | extends Buff implements ActionIndicator.Action |
| **代码行数** | 678 行 |
| **内部类** | MonkAbility 及其多层子类 |
| **官方中文名** | 内力 |

## 2. 文件职责说明

MonkEnergy 类实现武僧的内力与武功系统。它负责记录当前 `energy`、能力冷却、显示动作入口，并通过内部抽象类 `MonkAbility` 组织 5 个武功：空振、凝神、登云、盘龙、冥思。

**核心职责**：
- 维护内力值与能力冷却
- 在击杀敌人时按目标类型和天赋修正获得内力
- 管理 ActionIndicator 上的“武功”入口
- 处理武功使用后的内力消耗、组合能量联动与 UI 刷新
- 在内部类中定义每种武功的目标选择、效果与辅助 Buff

## 3. 结构总览

```
MonkEnergy (extends Buff implements ActionIndicator.Action)
├── 字段
│   ├── energy: float
│   └── cooldown: int
├── 常量
│   └── MAX_COOLDOWN: float = 5
├── 方法
│   ├── act(): boolean
│   ├── desc(): String
│   ├── gainEnergy(Mob): void
│   ├── energyCap(): int
│   ├── abilityUsed(MonkAbility): void
│   ├── abilitiesEmpowered(Hero): boolean
│   ├── processCombinedEnergy(...): void
│   ├── 图标/动作指示相关方法
│   └── storeInBundle()/restoreFromBundle()
└── 内部类 MonkAbility
    ├── Flurry / Focus / Dash / DragonKick / Meditate
    ├── UnarmedAbilityTracker
    ├── FlurryEmpowerTracker
    ├── FlurryCooldownTracker
    ├── FocusBuff
    └── MeditateResistance
```

## 4. 继承与协作关系

### 继承关系图

```mermaid
classDiagram
    Buff <|-- MonkEnergy
    ActionIndicator_Action <|.. MonkEnergy
    MonkEnergy +-- MonkAbility
```

### 协作关系

| 协作类 | 协作方式 |
|--------|----------|
| **Buff** | 父类，提供附着与回合调度 |
| **ActionIndicator.Action** | 提供“武功”按钮 |
| **Hero / Mob** | 内力获得、武功目标与能力执行主体 |
| **Talent.UNENCUMBERED_SPIRIT / COMBINED_ENERGY / MONASTIC_VIGOR** | 决定获取倍率、组合联动与强化阈值 |
| **WndMonkAbilities** | 点击按钮后展示武功窗口 |
| **Healing / Recharging / ArtifactRecharge** | 冥思能力会附加这些 Buff |
| **Paralysis** | 盘龙会附加麻痹 |
| **Invisibility** | 多个武功会驱散隐形 |
| **WandOfBlastWave / Ballistica / Door** | 登云与盘龙处理位移/击退 |
| **ActionIndicator / BuffIndicator / HeroIcon** | 行动入口与 UI 更新 |

## 5. 字段与常量详解

### 实例字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `energy` | float | 当前内力值 |
| `cooldown` | int | 当前武功冷却；源码注释说明当前未实际用作旧版能力冷却系统 |

### 常量

| 常量 | 类型 | 值 | 说明 |
|------|------|----|------|
| `MAX_COOLDOWN` | float | `5` | 图标淡出显示的冷却基准 |

### 初始化块

```java
{
    type = buffType.POSITIVE;
    revivePersists = true;
}
```

### Bundle 键

| 常量 | 值 | 用途 |
|------|-----|------|
| `ENERGY` | `energy` | 保存内力 |
| `COOLDOWN` | `cooldown` | 保存冷却 |

## 6. 构造与初始化机制

MonkEnergy 没有显式构造函数。通常作为武僧核心 Buff 长期附着在英雄身上，并在击杀或使用武功时不断更新。

## 7. 方法详解

### act()

每回合只处理冷却：
- 若 `cooldown > 0`，则减 1
- 当冷却降到 0 且 `energy >= 1`，设置 ActionIndicator 动作
- 刷新 Buff 图标
- `spend(TICK)`

### desc()

基础描述：

```java
Messages.get(this, "desc", (int)energy, energyCap())
```

若 `cooldown > 0`，再追加 `desc_cooldown`。

### gainEnergy(Mob enemy)

内力获取逻辑：
1. 若 `target == null` 或 `!Regeneration.regenOn()`，直接返回。
2. 基础收益：
   - `BOSS` -> 5
   - `MINIBOSS` -> 3
   - `Ghoul` / `RipperDemon` / `YogDzewa.Larva` / `Wraith` -> 0.5
   - 其他 -> 1
3. 若英雄有 `UNENCUMBERED_SPIRIT`，根据护甲/武器阶级追加收益倍率。
4. 加到 `energy`。
5. 若当前不在 `UnarmedAbilityTracker` 中，则把 `energy` 钳制到 `energyCap()`。
6. 若 `energy >= 1 && cooldown == 0`，设置动作按钮。

### energyCap()

```java
return Math.max(10, 5 + Dungeon.hero.lvl/2);
```

### abilityUsed(MonkAbility abil)

使用武功后：
- `energy -= abil.energyCost()`
- 再次限制到 `energyCap()`
- 检查 `COMBINED_ENERGY` 联动并决定是否附加/处理 `CombinedEnergyAbilityTracker`
- 根据 `cooldown` 与剩余 `energy` 设置/清除动作按钮
- 刷新 Buff 图标

### abilitiesEmpowered(Hero hero)

强化阈值：

```java
energy/energyCap() >= 1.2f - 0.2f*hero.pointsInTalent(Talent.MONASTIC_VIGOR)
```

### processCombinedEnergy(...)

给 `energy + 1`（不超过上限），移除追踪器，并刷新动作与图标。

### 动作入口方法

- `actionName()` -> `武功`
- `actionIcon()` -> `HeroIcon.MONK_ABILITIES`
- `secondaryVisual()` -> 显示当前整数化内力
- `indicatorColor()` -> 强化时亮绿色，否则棕色
- `doAction()` -> 打开 `WndMonkAbilities`

### 内部抽象类 MonkAbility

统一定义：
- `name()` / `desc()`
- `energyCost()`
- `usable()`
- `targetingPrompt()`
- `doAbility(Hero, Integer)`

### 五个武功的事实概览

| 能力 | 能量消耗 | 关键行为 |
|------|----------|----------|
| `Flurry` | 1 | 两次徒手攻击，可受强化与冷却追踪影响 |
| `Focus` | 2 | 附加 `FocusBuff`，强化时立即生效不额外耗回合 |
| `Dash` | 3 | 冲刺到目标格，强化时距离从 4 提升到 8 |
| `DragonKick` | 4 | 高倍率攻击并击退，强化时影响邻近敌人 |
| `Meditate` | 5 | 清理大多数负面 Buff，附加充能效果，强化时治疗并获得抗性 |

## 8. 对外暴露能力

| 方法 | 用途 |
|------|------|
| `gainEnergy(Mob)` | 击杀敌人后获取内力 |
| `energyCap()` | 查询当前内力上限 |
| `abilityUsed(MonkAbility)` | 使用武功后统一结算 |
| `abilitiesEmpowered(Hero)` | 判断武功是否强化 |
| `doAction()` | 打开武功窗口 |

## 9. 运行机制与调用链

```
击杀敌人
└── MonkEnergy.gainEnergy(enemy)
    ├── 计算基础收益
    ├── 应用 UNENCUMBERED_SPIRIT 倍率
    └── 刷新 ActionIndicator / BuffIndicator

点击“武功”按钮
└── MonkEnergy.doAction()
    └── 打开 WndMonkAbilities

使用某门武功
└── MonkAbility.doAbility(...)
    └── MonkEnergy.abilityUsed(ability)
```

## 10. 资源、配置与国际化关联

文件：`core/src/main/assets/messages/actors/actors_zh.properties`

```properties
actors.buffs.monkenergy.name=内力
actors.buffs.monkenergy.action=武功
actors.buffs.monkenergy.desc=每当武僧击败一个敌人，她都会获得可用于施展多门武功的内力。
```

该文件还为 `MonkAbility` 及其部分内部 Buff 提供了大量子键，例如 `flurry`、`focus`、`dash`、`dragonkick`、`meditate` 等。

## 11. 使用示例

```java
MonkEnergy energy = hero.buff(MonkEnergy.class);
if (energy != null) {
    energy.gainEnergy(enemy);
    int cap = energy.energyCap();
    boolean empowered = energy.abilitiesEmpowered(hero);
}
```

## 12. 开发注意事项

- `MonkEnergy` 不是单一数值 Buff，而是整套武功系统的调度中心。
- `gainEnergy()` 会在 `Regeneration.regenOn()` 为假时直接拒绝收益，用于防止某些刷能量行为。
- 许多具体战斗行为写在内部类 `MonkAbility` 里，修改时要同时检查 UI、目标选择、行动消耗与 Buff 追踪器。

## 13. 修改建议与扩展点

- 若未来武功继续增多，建议把 `MonkAbility` 族迁移到独立文件以降低单文件复杂度。
- 若要减少重复 UI 刷新逻辑，可把 ActionIndicator/BuffIndicator 的刷新封装成统一辅助方法。

## 14. 事实核查清单

- [x] 已覆盖核心字段、常量、主方法与动作接口实现
- [x] 已验证继承关系 `extends Buff implements ActionIndicator.Action`
- [x] 已验证 `energy` / `cooldown` / `energyCap()` 逻辑
- [x] 已验证 `gainEnergy()` 的敌人分类与天赋倍率逻辑
- [x] 已验证 `abilityUsed()` 与 `CombinedEnergy` 联动
- [x] 已概括 5 个内置 MonkAbility 的真实职责
- [x] 已核对官方中文名与相关文案来自翻译文件
- [x] 无把内部类行为写成外部系统行为的臆测
