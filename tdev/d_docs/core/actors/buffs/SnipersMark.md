# SnipersMark (狙击手标记) 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/SnipersMark.java` |
| **包名** | `com.shatteredpixel.shatteredpixeldungeon.actors.buffs` |
| **文件类型** | class |
| **继承关系** | `extends FlavourBuff implements ActionIndicator.Action` |
| **代码行数** | 112 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`SnipersMark` 负责实现女猎手子职业“狙击手”的特殊标记逻辑。当狙击手使用飞镖或其他投掷武器击中敌人时，会为自己施加此 Buff，解锁针对该目标的特殊弓箭技能。

### 系统定位
属于 Buff 系统与 UI 交互系统（ActionIndicator）的交集。它不仅是一个状态标记，还作为一个临时的“操作按钮”出现在游戏主界面上。

### 不负责什么
- 不负责飞镖击中时的标记判定（由 `Sniper` 技能逻辑负责）。
- 不负责弓箭的基础伤害计算（由 `SpiritBow` 负责）。

## 3. 结构总览

### 主要成员概览
- **常量 DURATION**: 极短的持续时间（4 回合）。
- **字段 object**: 存储被标记目标的 ID。
- **字段 percentDmgBonus**: 存储本次特殊攻击的额外伤害倍率。
- **接口实现**: 实现了 `ActionIndicator.Action`，定义了特殊按钮的图标、名称和点击后的行为。

### 主要逻辑块概览
- **动态动作命名**: 根据灵弓（Spirit Bow）的不同强化（Augment）类型，动态改变界面上特殊技能的名称（快照、齐射、狙击）。
- **自动寻踪射击**: `doAction()` 实现了自动瞄准目标、扣除回合、发射特殊箭矢并消耗标记的完整流程。

### 生命周期/调用时机
1. **产生**：狙击手使用投掷武器击中角色。
2. **活跃期**：主界面出现特殊攻击按钮。
3. **结束**：点击按钮执行攻击、手动移动、或 4 回合时间结束。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `FlavourBuff`：
- 提供基础的时间管理和 UI 图标展示。

### 实现的接口契约
- **ActionIndicator.Action**: 允许该 Buff 在 UI 顶层显示一个可点击的动作图标。

### 协作对象
- **SpiritBow**: 提供核心远程武力支持，执行 `knockArrow` 和 `cast`。
- **ActionIndicator**: 管理界面动作按钮的注册与清除。
- **QuickSlotButton**: 调用 `autoAim` 辅助瞄准。

```mermaid
graph TD
    Buff --> FlavourBuff
    FlavourBuff --> SnipersMark
    SnipersMark --|> Action[ActionIndicator.Action]
    SnipersMark -->|Controls| SpiritBow[SpiritBow Special Attack]
    SnipersMark -->|Registers| UI[ActionIndicator Button]
```

## 5. 字段/常量详解

### 静态常量
- **DURATION**: 4.0f 回合。

### 实例字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| `object` | int | 锁定目标的全局唯一 ID |
| `percentDmgBonus` | float | 伤害加成百分比 |

## 6. 构造与初始化机制
通过实例初始化块设置 `type = POSITIVE`。该 Buff 通常由狙击手技能代码通过 `Buff.affect()` 创建并调用 `set(id, bonus)` 初始化。

## 7. 方法详解

### actionName() [动态文案逻辑]

**可见性**：public (Override)

**核心逻辑分析**：
根据灵弓的强化类型返回不同的本地化键值：
- **NONE (无)**: “Snapshot (瞬瞄)” - 平衡射击。
- **SPEED (速度)**: “Volley (连射)” - 快速射击。
- **DAMAGE (伤害)**: “Sniper Shot (狙击)” - 强力射击。

---

### doAction() [核心操作实现]

**方法职责**：执行狙击手的特殊射击。

**核心逻辑分析**：
1. **合法性检查**：检查英雄、灵弓、目标角色是否依然存在。
2. **自动瞄准**：
   ```java
   int cell = QuickSlotButton.autoAim(ch, arrow);
   ```
3. **注入属性**：
   ```java
   bow.sniperSpecial = true;
   bow.sniperSpecialBonusDamage = percentDmgBonus;
   ```
   **技术影响**：这会临时修改灵弓的全局状态，使其在接下来的 `cast` 计算中应用特殊倍率。
4. **发射并消耗**：调用 `arrow.cast()` 后立即 `detach()`。

---

### attachTo / detach [UI 联动]

在附加时调用 `ActionIndicator.setAction(this)` 注册 UI 按钮，在移除时调用 `clearAction(this)` 清理。

## 8. 对外暴露能力
主要作为狙击手职业机制的内部驱动运行。

## 9. 运行机制与调用链
`Sniper.proc()` -> `Buff.affect(SnipersMark.class)` -> UI 按钮闪现 -> 玩家点击 -> `doAction()` -> `SpiritBow.cast()` -> 箭矢飞向目标。

## 10. 资源、配置与国际化关联

### 本地化词条
- `SnipersMark.name`: 狙击手标记
- `SnipersMark.action_name_snapshot`: 快照
- `SnipersMark.action_name_volley`: 齐射
- `SnipersMark.action_name_sniper`: 狙击

## 11. 使用示例

### 战术价值
利用投掷武器（如飞镖）击中远处的怪物，然后通过该 Buff 提供的动作按钮，跨越障碍物或在极远距离执行百分之百命中的弓箭惩罚。

## 12. 开发注意事项

### 极短时长
该 Buff 的 `DURATION` 仅为 4。这意味着如果玩家在此期间选择了移动或其他消耗回合的操作，标记会迅速消失。

### 唯一性
同一时间只能存在一个特殊 Action。如果其他 Buff（如特定法术）也尝试显示 Action 按钮，会发生覆盖。

## 13. 修改建议与扩展点

### 增加视野联动
可以修改 `actionIcon()`，使被标记目标的血量或状态在 UI 按钮上直接体现。

## 14. 事实核查清单

- [x] 是否分析了 DURATION 的特殊性：是 (4 回合)。
- [x] 是否解释了与 SpiritBow 强化类型的联动：是。
- [x] 是否解析了 doAction 的核心射击流程：是。
- [x] 是否说明了 ActionIndicator 的注册机制：是。
- [x] 图像索引属性是否核对：是 (BuffIndicator.MARK)。
