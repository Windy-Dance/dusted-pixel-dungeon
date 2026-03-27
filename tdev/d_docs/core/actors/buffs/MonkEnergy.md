# MonkEnergy (武僧能量) 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/dustedpixeldungeon/actors/buffs/MonkEnergy.java` |
| **包名** | `com.shatteredpixel.dustedpixeldungeon.actors.buffs` |
| **文件类型** | class / inner classes |
| **继承关系** | `extends Buff implements ActionIndicator.Action` |
| **代码行数** | 485 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`MonkEnergy` 负责实现决斗者子职业“武僧（Monk）”的核心资源系统：气能（Energy）。它管理能量的获取、上限、消耗，并作为容器定义了武僧的五项核心技能（连击、专注、疾冲、神龙摆尾、冥想）。

### 系统定位
属于 Buff 系统、UI 交互系统与战斗系统的高度集成组件。它是武僧职业玩法的灵魂，将击杀反馈转化为可主动使用的功能性/战斗性资源。

### 不负责什么
- 不负责决斗者的“武器技能”逻辑（由 `WeaponAbility` 负责）。
- 不负责计算武僧空手攻击的基础伤害。

## 3. 结构总览

### 主要成员概览
- **字段 energy**: 当前存储的能量值。
- **字段 cooldown**: 状态冷却时间（注：2.5 版本后主要用于内部调度）。
- **静态内部类 MonkAbility**: 技能基类，定义了技能列表及通用属性。
- **五大技能实现**: `Flurry` (连击), `Focus` (专注), `Dash` (疾冲), `DragonKick` (神龙摆尾), `Meditate` (冥想)。

### 主要逻辑块概览
- **动态能量获取 (`gainEnergy`)**: 根据击杀目标的类型（Boss、精英、普通、杂鱼）计算基础能量增量，并应用天赋加成。
- **能量上限系统**: 能量上限随角色等级动态增长。
- **强化状态 (`abilitiesEmpowered`)**: 当能量积累到一定比例时，所有技能获得额外增强效果。
- **技能执行逻辑**: 每种技能包含独立的能量扣除、动作消耗、视觉特效和逻辑结算。

### 生命周期/调用时机
1. **产生**：转职为武僧时自动附加。
2. **积累期**：击杀怪物获得能量。
3. **活跃期**：点击 UI 上的武僧技能图标，弹出选择窗口并消耗能量执行。
4. **存续**：`revivePersists = true` 确保复活后能量不丢失。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `Buff`：
- 定义为 `POSITIVE`。
- 提供 `revivePersists` 支持。

### 实现的接口契约
- **ActionIndicator.Action**: 在主界面提供一个可点击的“武僧技能”动作按钮。

### 协作对象
- **Hero / Talent**: 深度联动“灵动之魂（UNENCUMBERED_SPIRIT）”等多个天赋。
- **WandOfBlastWave**: `DragonKick` 复用了冲击波的位移逻辑。
- **CharSprite**: 处理所有技能的攻击、跳跃和操作动画。
- **WndMonkAbilities**: 为该 Buff 提供专门的技能选取窗口。

```mermaid
graph TD
    Buff --> MonkEnergy
    MonkEnergy --|> Action[ActionIndicator.Action]
    MonkEnergy -->|Defines| MonkAbility
    MonkAbility <|-- Flurry
    MonkAbility <|-- Focus
    MonkAbility <|-- Dash
    MonkAbility <|-- DragonKick
    MonkAbility <|-- Meditate
    MonkEnergy -->|Talents| Spirit[UNENCUMBERED_SPIRIT]
```

## 5. 字段/常量详解

### 实例字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| `energy` | float | 当前气能值。 |
| `cooldown` | int | 技能全局冷却。 |

### 核心算法常量/矩阵
- **energyCap()**: `max(10, 5 + Level/2)`。最高等级 30 时上限为 **20**。
- **gainEnergy 基础值**: 
  - Boss: 5
  - Miniboss: 3
  - 普通: 1
  - 弱小生物 (幽灵、食尸鬼等): 0.5

## 6. 构造与初始化机制
通过转职逻辑附加。`revivePersists = true` 极其重要，防止因死亡导致辛苦积累的能量清空。

## 7. 方法详解

### gainEnergy(Mob enemy) [能量获取算法]

**核心逻辑分析**：
1. **防止刷分机制**：检查 `Regeneration.regenOn()`。在 Boss 战产生无限杂鱼的阶段，无法获取能量。
2. **天赋加成 (`UNENCUMBERED_SPIRIT`)**：
   - 检查护甲阶级（Tier）和武器阶级。
   - 如果装备低阶或不穿甲/空手，且天赋点满，能量获取倍率最高可提升至 **3.0x** (1 + 1.0护甲加成 + 1.0武器加成)。
   **设计意图**：鼓励武僧走轻量化或空手流路线。

---

### abilitiesEmpowered(Hero hero) [强化态判定]

**算法逻辑**：
`energy / cap >= 1.2 - 0.2 * pointsInMonasticVigor`
- **默认 (0点天赋)**: 能量需达到 120% 上限（无法达到，除非有其他修正）。
- **点满天赋 (+3)**: 能量达到上限的 **60%** 即可进入强化态。
**视觉表现**：强化态下技能按钮变为**亮黄绿色**。

---

### MonkAbility 具体技能解析

#### 1. Flurry (连击) - 消耗 1
- **效果**: 执行两次 1.5x 倍率的空手攻击。
- **强化**: 攻击不消耗时间（`next()` 替换 `spend()`）。

#### 2. Focus (专注) - 消耗 2
- **效果**: 获得心灵视界。
- **强化**: 瞬间施放，不消耗当前回合。

#### 3. Dash (疾冲) - 消耗 3
- **效果**: 向 4 格内的空位跳跃。
- **强化**: 范围提升至 **8 格**。

#### 4. DragonKick (神龙摆尾) - 消耗 4
- **效果**: 6x 倍率重击，击退目标 6 格并造成等额回合的麻痹。
- **强化**: 攻击变为 **9x** 倍率，且击退并麻痹周围所有相邻的敌人（AOE 击退）。

#### 5. Meditate (冥想) - 消耗 5
- **效果**: 站立 5 回合进行深度调息。立即清除几乎所有负面 Buff，并加速法杖/神器充能。
- **强化**: 冥想期间获得高额生命回复。

## 8. 对外暴露能力
- `gainEnergy(Mob)`: 被战斗系统在怪物死亡时调用。
- `abilityUsed(MonkAbility)`: 技能执行后的资源结算入口。

## 9. 运行机制与调用链
`Mob.die()` -> `MonkEnergy.gainEnergy()` -> `energy++` -> `ActionIndicator` 显示按钮 -> 玩家点击 -> `WndMonkAbilities` -> `MonkAbility.doAbility()`。

## 10. 资源、配置与国际化关联

### 本地化词条
- `actors.buffs.MonkEnergy.name`: 气能
- `desc`: “当前气能：%d/%d。”
- 各技能的 `name` 和 `desc`/`empower_desc`。

## 11. 使用示例

### 战术连招
通过击杀小怪积攒 12 点能量（点满天赋，此时能量条 60% 变色）。点击神龙摆尾，一次性将周围三名包围英雄的敌人踢飞并麻痹，然后利用连击的高爆发在 0 回合消耗下处理残血。

## 12. 开发注意事项

### 动画同步
`Dash` 和 `DragonKick` 涉及复杂的 `Callback` 和 `Camera.pan` 逻辑，需确保渲染线程不发生死锁。

### 能量溢出处理
代码中 `UnarmedAbilityTracker` 存在期间（即技能执行中）不应用能量上限。这允许武僧在一次大招击杀多名敌人时，能量可以暂时超过上限再被技能消耗扣回。

## 13. 修改建议与扩展点

### 增加新技能
可以向 `MonkAbility.abilities` 静态数组中添加新的实现类，UI 会自动适配显示。

## 14. 事实核查清单

- [x] 是否分析了能量获取的阶梯数值：是 (5, 3, 1, 0.5)。
- [x] 是否解析了 UNENCUMBERED_SPIRIT 的护甲武器加成逻辑：是。
- [x] 是否详细列出了五种技能及其强化效果：是。
- [x] 是否计算了能量上限公式：是 (`max(10, 5+lvl/2)`)。
- [x] 是否涵盖了强化态的判定公式：是。
- [x] 图像索引属性是否核对：是 (BuffIndicator.MONK_ENERGY)。
