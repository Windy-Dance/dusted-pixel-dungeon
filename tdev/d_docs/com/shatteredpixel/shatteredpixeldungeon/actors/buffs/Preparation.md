# Preparation 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Preparation.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| **类类型** | public class |
| **继承关系** | extends Buff implements ActionIndicator.Action |
| **代码行数** | 336 行 |
| **内部类型** | AttackLevel |
| **官方中文名** | 准备阶段 |

## 2. 文件职责说明

Preparation 类实现刺客隐身蓄力系统。它统计角色已隐形的回合数 `turnsInvis`，根据 `AttackLevel` 阶段为下一击提供额外伤害、斩杀阈值和闪现刺杀距离，并通过 `ActionIndicator` 提供主动“蓄意打击”入口。

**核心职责**：
- 在隐身期间累积准备层级
- 根据层级计算下一击伤害与斩杀阈值
- 提供带范围限制的瞬移突袭动作
- 用图标、文本和颜色展示当前准备等级

## 3. 结构总览

```
Preparation (extends Buff implements ActionIndicator.Action)
├── 字段
│   └── turnsInvis: int
├── 初始化块
│   ├── actPriority = BUFF_PRIO - 1
│   └── type = POSITIVE
├── 核心方法
│   ├── act(): boolean
│   ├── attackLevel(): int
│   ├── damageRoll(Char): int
│   ├── canKO(Char): boolean
│   ├── doAction(): void
│   └── 存档/图标/描述相关方法
└── 内部枚举 AttackLevel
    ├── LVL_1
    ├── LVL_2
    ├── LVL_3
    └── LVL_4
```

## 4. 继承与协作关系

### 继承关系图

```mermaid
classDiagram
    Buff <|-- Preparation
    ActionIndicator_Action <|.. Preparation
    Preparation +-- AttackLevel
```

### 协作关系

| 协作类 | 协作方式 |
|--------|----------|
| **Buff** | 父类，提供附着与计时 |
| **ActionIndicator.Action** | 提供“蓄意打击”动作按钮 |
| **Talent.ENHANCED_LETHALITY** | 决定斩杀阈值 |
| **Talent.ASSASSINS_REACH** | 决定闪现攻击距离 |
| **HeroAction.Attack** | 近身时直接切到普通攻击动作 |
| **GameScene / CellSelector** | 提供目标选择界面 |
| **PathFinder** | 计算可闪现路径与最佳落点 |
| **NPC** | 明确排除为可攻击目标 |
| **ActionIndicator / HeroIcon** | 显示可用动作 |
| **Messages / GLog** | 输出提示文本 |

## 5. 字段与常量详解

### 实例字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `turnsInvis` | int | 已连续隐形的回合数 |

### 初始化块

```java
{
    actPriority = BUFF_PRIO - 1;
    type = buffType.POSITIVE;
}
```

### Bundle 键

| 常量 | 值 | 用途 |
|------|-----|------|
| `TURNS` | `turnsInvis` | 保存当前隐形累计回合 |

### AttackLevel 枚举字段

| 字段 | 说明 |
|------|------|
| `turnsReq` | 解锁该等级所需隐形回合 |
| `baseDmgBonus` | 额外伤害倍率 |
| `damageRolls` | 伤害掷骰次数 |

### AttackLevel 固定数据

| 等级 | 所需回合 | 伤害加成 | 伤害掷骰 |
|------|----------|----------|----------|
| `LVL_1` | 1 | 10% | 1 |
| `LVL_2` | 3 | 20% | 1 |
| `LVL_3` | 5 | 35% | 2 |
| `LVL_4` | 9 | 50% | 3 |

## 6. 构造与初始化机制

Preparation 没有显式构造函数。它通常在英雄进入隐身准备阶段后附着，并在 `act()` 中每回合累积 `turnsInvis`。

## 7. 方法详解

### act()

若 `target.invisible > 0`：
- `turnsInvis++`
- 当当前 `AttackLevel` 的 `blinkDistance() > 0` 且目标是英雄时，设置动作按钮
- `spend(TICK)`

若目标不再隐形，则直接 `detach()`。

### attackLevel()

返回当前 `AttackLevel` 的序号 + 1，即 1 到 4 的整数。

### damageRoll(Char attacker)

委托给当前 `AttackLevel.damageRoll(attacker)`：
- 以 `attacker.damageRoll()` 为基础
- 按 `damageRolls` 多次取更高值
- 最后乘以 `1 + baseDmgBonus`

### canKO(Char defender)

仅当目标对攻击者类型不处于无敌状态时才继续。\n
具体斩杀阈值由 `AttackLevel.KOThreshold()` 决定；若目标拥有 `MINIBOSS` 或 `BOSS` 属性，则阈值变为普通目标的五分之一。

### 图标与描述方法

- `icon()` -> `BuffIndicator.PREPARATION`
- `tintIcon()` 根据 `AttackLevel` 切换绿/黄/橙/红
- `desc()` 会依次拼接：
  - 基础描述 `desc`
  - 当前伤害与斩杀说明 `desc_dmg`
  - 若有多次伤害掷骰，拼接 `desc_dmg_likely`
  - 若可闪现，拼接 `desc_blink`
  - 已隐形回合 `desc_invis_time`
  - 若还未满级，拼接下一级所需回合 `desc_invis_next`

### 动作接口实现

- `actionName()` -> `蓄意打击`
- `actionIcon()` -> `HeroIcon.PREPARATION`
- `primaryVisual()` -> 当前准备等级染色图标
- `secondaryVisual()` -> 最多显示到 9 的隐形回合数
- `indicatorColor()` -> `0x444444`
- `doAction()` -> 打开选格器

### 目标选择与闪现攻击

内部 `CellSelector.Listener attack` 负责：
- 过滤不可攻击目标：空格、被魅惑对象、`NPC`、不可见、自己等
- 若英雄可直接攻击，切换为普通 `HeroAction.Attack`
- 否则按当前 `blinkDistance()` 在目标周围寻找最近可落脚点
- 若找到合法格子：
  - 传送英雄到该格
  - 更新占格、视野和迷雾
  - 设置普通攻击动作

## 8. 对外暴露能力

| 方法 | 用途 |
|------|------|
| `attackLevel()` | 获取当前准备等级 |
| `damageRoll(Char)` | 获取强化后的下一击伤害 |
| `canKO(Char)` | 判断是否可斩杀目标 |
| `doAction()` | 主动发动蓄意打击 |

## 9. 运行机制与调用链

```
隐身开始
└── Preparation.act()
    ├── turnsInvis++
    ├── 依据 AttackLevel 解锁更强阶段
    └── [可闪现] 注册 ActionIndicator

点击“蓄意打击”
└── doAction()
    └── GameScene.selectCell(listener)
        └── 选择目标后执行直攻或闪现近身再攻击
```

## 10. 资源、配置与国际化关联

文件：`core/src/main/assets/messages/actors/actors_zh.properties`

```properties
actors.buffs.preparation.name=准备阶段
actors.buffs.preparation.action_name=蓄意打击
actors.buffs.preparation.desc=刺客正耐心地等待着，准备从暗影中给出致命一击。
```

其余 `desc_dmg`、`desc_blink`、`prompt` 等补充键也都在同一翻译文件中定义。

## 11. 使用示例

```java
Preparation prep = hero.buff(Preparation.class);
if (prep != null) {
    int lvl = prep.attackLevel();
    boolean canKill = prep.canKO(enemy);
}
```

## 12. 开发注意事项

- 该 Buff 的核心是“隐身回合数”而不是单纯持续时间。
- `AttackLevel` 把伤害、斩杀和闪现距离三套成长规则统一绑在同一个阶段系统里。
- 闪现攻击并不是直接伤害技能，而是先寻找合法落脚点，再转成普通攻击动作。

## 13. 修改建议与扩展点

- 若未来刺客分支更多，建议把 `AttackLevel` 的 KO 与闪现表格拆到独立配置结构。
- 目标筛选与落点选择逻辑较长，可拆成私有辅助方法提高可维护性。

## 14. 事实核查清单

- [x] 已覆盖全部字段、核心方法、Action 接口和内部枚举
- [x] 已验证继承关系 `extends Buff implements ActionIndicator.Action`
- [x] 已验证 `turnsInvis` 的累积和隐身依赖逻辑
- [x] 已验证 `AttackLevel` 的 4 个阶段与表格规则
- [x] 已验证 `damageRoll()`、`canKO()` 与闪现攻击流程
- [x] 已验证图标、描述与动作按钮逻辑
- [x] 已验证 `Bundle` 存档字段
- [x] 已核对官方中文名与动作文案来自翻译文件
- [x] 无臆测性机制说明
