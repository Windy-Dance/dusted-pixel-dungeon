# Combo (连击) 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/dustedpixeldungeon/actors/buffs/Combo.java` |
| **包名** | `com.shatteredpixel.dustedpixeldungeon.actors.buffs` |
| **文件类型** | class / inner classes |
| **继承关系** | `extends Buff implements ActionIndicator.Action` |
| **代码行数** | 480 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`Combo` 负责实现战士子职业“角斗士（Gladiator）”的核心战斗机制：连击。它记录连续命中的次数（Count），管理连击计数的存续时间（Combo Time），并提供五种强力的终结技（Combo Moves）。

### 系统定位
属于 Buff 系统、UI 系统与战斗深度集成的核心机制。它是角斗士高机动、多目标和高爆发战斗风格的驱动源。

### 不负责什么
- 不负责计算基础物理攻击命中率。
- 不负责赋予护甲（虽然与 `BrokenSeal` 有联动）。

## 3. 结构总览

### 主要成员概览
- **字段 count**: 当前连击计数的总数。
- **字段 comboTime**: 连击槽的当前剩余时间。
- **枚举 ComboMove**: 定义了五个终结技：`CLOBBER` (击退), `SLAM` (重击), `PARRY` (招架), `CRUSH` (粉碎), `FURY` (怒火)。
- **方法 doAttack()**: 终结技的具体逻辑执行，包含复杂的伤害修正和次生效果。
- **内部类 ParryTracker**: 处理招架成功后的状态同步。

### 主要逻辑块概览
- **计数与计时逻辑 (`hit`)**: 每次命中时增加计数，并根据击杀情况大幅延长计时。
- **终结技选择体系**: 随着计数增加，解锁不同层级的终结技，且支持天赋增强。
- **复合打击逻辑**: `FURY` 实现了多段打击，`CRUSH` 实现了范围冲击波。
- **机动性联动**: 配合天赋支持“跃击”式远程切入。

### 生命周期/调用时机
1. **产生**：转职为角斗士后，首次攻击命中产生。
2. **积累期**：每次命中调用 `hit()`。
3. **活跃期**：点击 UI 按钮弹出 `WndCombo` 选取并执行终结技。
4. **中断**：计时耗尽（`comboTime <= 0`）自动 `detach()`。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `Buff`：
- 提供基础的附加与移除逻辑。
- 设为 `POSITIVE` 类型。

### 实现的接口契约
- **ActionIndicator.Action**: 在主界面提供可点击的连击终结技按钮。

### 协作对象
- **Hero / Talent**: 深度联动“强化连击（ENHANCED_COMBO）”和“致命防御（LETHAL_DEFENSE）”。
- **WandOfBlastWave**: `CLOBBER` 和 `CRUSH` 复用了冲击波的物理效果。
- **HoldFast**: 战士的基础 Buff，影响连击时间的衰减速率。
- **Invisibility**: 任何终结技攻击都会强制破隐。

```mermaid
graph TD
    Combat[Combat System] -->|hit| Combo
    Combo -->|Count| Moves[ComboMove System]
    Combo --|> Action[ActionIndicator.Action]
    Moves -->|Empower| Talents[Talent: Enhanced Combo]
    Combo -->|Logic| Wand[WandOfBlastWave FX]
    Combo -->|Defense| BrokenSeal[Warrior Shield Regen]
```

## 5. 字段/常量详解

### 实例字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| `count` | int | 连击数（连续命中次数）。 |
| `comboTime` | float | 剩余计时。每回合自然流逝。 |
| `initialComboTime`| float | 用于 UI 进度条显示的基准参考。 |

### 核心算法常量
- **计时刷新**: 普通命中刷新至 `max(time, 5f)`。
- **击杀加成**: 击杀目标或腐化满血怪后，刷新至 `15f + 15f * Talent[CLEAVE]`。
- **衰减因子**: `TICK * HoldFast.buffDecayFactor`。这意味着拥有“稳如泰山”Buff 时连击时间掉得更慢。

## 6. 构造与初始化机制
通过职业转职逻辑附加。逻辑重点在于 `act()` 方法中的计时递减和计时结束后的自毁逻辑。

## 7. 方法详解

### hit(Char enemy) [计数驱动]

**核心实现分析**：
1. 递增 `count`。
2. 更新计时器。
3. 如果连击数满足最低终结技要求，立即通过 `ActionIndicator.setAction(this)` 在主界面显示操作按钮。
4. 验证徽章成就。

---

### doAttack(Char enemy) [终结技结算核心]

该方法是整个系统的逻辑核心，处理四种主动终结技：

#### 1. CLOBBER (击退) - 需求 2 连击
- **效果**: 伤害倍率为 0（即不造成额外血量伤害），但会将目标向外推离 2 格。
- **强化**: 如果连击达到 7 次且有天赋，推离距离增加，并附加 `Vertigo`（眩晕）。

#### 2. SLAM (重击) - 需求 4 连击
- **效果**: 将自身已有的护盾量（drRoll）的一部分作为真实伤害附加到攻击中。
- **强化**: 伤害系数随连击数线性增长。

#### 3. CRUSH (粉碎) - 需求 8 连击
- **效果**: 产生冲击波，并对目标周围所有敌人造成 `0.25f * count` 倍率的范围伤害。
- **天赋联动**: 若击杀，可加速 `BrokenSeal` 护盾恢复。

#### 4. FURY (怒火) - 需求 10 连击
- **效果**: **多段连续打击**。其打击次数等于当前连击数 `count`。
- **逻辑**: 每一下造成 0.6x 伤害，循环调用直到次数用尽或敌人死亡。

---

### useMove(ComboMove move) [状态机切换]

**特殊动作：PARRY (招架)**：
与上述攻击不同，招架不执行选择和攻击，而是直接附加一个 `ParryTracker`。角色在下一回合如果受到攻击，会自动触发 `RiposteTracker` 进行一次强力反击。

## 8. 对外暴露能力
- `hit(Char)`: 被战斗系统在每次成功命中后调用。
- `getComboCount()`: 获取当前计数。
- `addTime(float)`: 手动增加连击时间。

## 9. 运行机制与调用链
`Hero.attack()` -> `enemy.damage()` -> `Combo.hit()` -> `count++` -> `ActionIndicator` 显示图标 -> 玩家点击 -> `WndCombo` 选择终结技 -> `doAttack()`。

## 10. 资源、配置与国际化关联

### 视觉特征
- 图标随解锁的最高级终结技进行**动态染色**（从绿到黄再到红）。
- 连击数显示在状态图标上方。

## 11. 使用示例

### 连招：跃击切入
如果连击达到 3 次且点了满级强化天赋，玩家可以点击终结技按钮，选取 2 格外的敌人。英雄会执行一个华丽的跳跃动画落到敌人身边并衔接攻击。

## 12. 开发注意事项

### 计时阻塞
在 `act()` 中，连击时间的减少会受 `HoldFast` 的影响。在设计新的战士天赋时，需注意是否需要通过 `HoldFast` 类注册新的时长修正。

### 弹道安全性
在 `CLOBBER` 的逻辑中，包含对深渊（Pit）的检查，确保普通的击退不会由于非玩家主观意愿导致怪物掉入下一层（除非使用了特定技能）。

## 13. 修改建议与扩展点

### 增加更多动作
可以在 `ComboMove` 枚举中扩展新的常量，并在 `doAttack` 对应分支中实现新的物理/状态逻辑。

## 14. 事实核查清单

- [x] 是否分析了连击计时的衰减与刷新逻辑：是。
- [x] 是否详细列出了五种终结技的具体数值和效果：是。
- [x] 是否解析了与战士护盾（drRoll）的数值联动：是。
- [x] 是否涵盖了天赋对终结技的各种增强：是。
- [x] 是否说明了 Parry 反击的异步实现：是。
- [x] 图像索引属性是否核对：是 (BuffIndicator.COMBO)。
