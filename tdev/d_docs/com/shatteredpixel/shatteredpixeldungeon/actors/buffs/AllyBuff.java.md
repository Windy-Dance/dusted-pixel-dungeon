# AllyBuff 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/AllyBuff.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| **类类型** | public abstract class |
| **继承关系** | extends Buff |
| **代码行数** | 85 行 |
| **直接子类** | 各类“把敌人转成盟友”的 Buff |

## 2. 文件职责说明

AllyBuff 是“敌转友”类 Buff 的抽象父类。它统一处理阵营改写、部分旧 Buff 清理，以及“虽然目标没死但仍按击杀发放掉落和经验”的特殊结算。

**核心职责**：
- 在附着时把目标阵营改成 `ALLY`
- 清理与盟友状态冲突的 `PinCushion`
- 提供 `affectAndLoot()` 静态辅助方法
- 把盟友化与击杀结算逻辑绑定到一起

## 3. 结构总览

```
AllyBuff (extends Buff) [abstract]
├── 初始化块
│   └── revivePersists = true
├── 方法
│   ├── attachTo(Char): boolean
│   └── affectAndLoot(Mob, Hero, Class<? extends AllyBuff>): void
```

## 4. 继承与协作关系

### 继承关系图

```mermaid
classDiagram
    Buff <|-- AllyBuff
    AllyBuff <|-- [具体盟友化Buff]
```

### 协作关系

| 协作类 | 协作方式 |
|--------|----------|
| **Buff** | 父类，提供附着、分离与状态生命周期 |
| **Char** | 被转成盟友的目标 |
| **Mob** | `affectAndLoot()` 的目标类型 |
| **Hero** | 获取经验与结算收益的施法者 |
| **Mimic** | 被当作特殊敌对对象处理 |
| **PinCushion** | 转盟友时被移除 |
| **Bestiary** | 更新图鉴与遭遇统计 |
| **Statistics** | 更新击杀统计与无杀挑战资格 |
| **Badges** | 更新击杀类徽章 |
| **AscensionChallenge** | 处理上行挑战击杀逻辑 |
| **MonkEnergy** | 武僧副职业获取能量 |

## 5. 字段与常量详解

AllyBuff 没有自有字段。\n
### 初始化块

```java
{
    revivePersists = true;
}
```

表示目标复活后此类 Buff 仍会保留。

## 6. 构造与初始化机制

AllyBuff 为抽象类，不能直接实例化。具体子类一般通过：

```java
Buff.affect(enemy, SomeAllyBuff.class);
```

附着。附着时会先走 `attachTo()` 的通用逻辑。

## 7. 方法详解

### attachTo(Char target)

```java
@Override
public boolean attachTo(Char target)
```

**职责**：将目标正式转为盟友。\n
**执行流程**：
1. 调用 `super.attachTo(target)`。
2. 若成功：
   - `target.alignment = Char.Alignment.ALLY`
   - 若目标身上存在 `PinCushion`，立即 `detach()`
3. 返回附着结果。

### affectAndLoot(Mob enemy, Hero hero, Class<? extends AllyBuff> buffCls)

这是 AllyBuff 的关键静态辅助方法。\n
**职责**：把敌人转成盟友，同时把这次转化按“击杀”处理掉落、经验、统计和挑战逻辑。\n
**执行流程**：
1. 记录目标原本是否算敌对：

```java
boolean wasEnemy = enemy.alignment == Char.Alignment.ENEMY || enemy instanceof Mimic;
```

2. `Buff.affect(enemy, buffCls)` 尝试施加盟友化 Buff。\n
3. 若施加成功且原本是敌对目标：
   - `enemy.rollToDropLoot()`
   - `Statistics.enemiesSlain++`
   - `Badges.validateMonstersSlain()`
   - `Statistics.qualifiedForNoKilling = false`
   - `Bestiary.setSeen(enemy.getClass())`
   - `Bestiary.countEncounter(enemy.getClass())`
   - `AscensionChallenge.processEnemyKill(enemy)`
   - 计算经验：`hero.lvl <= enemy.maxLvl ? enemy.EXP : 0`
   - 若经验大于 0，显示经验浮字
   - `hero.earnExp(exp, enemy.getClass())`
   - 若英雄副职业是 `MONK`，则 `Buff.affect(hero, MonkEnergy.class).gainEnergy(enemy)`

## 8. 对外暴露能力

| 方法 | 用途 |
|------|------|
| `attachTo(Char)` | 通用盟友化附着逻辑 |
| `affectAndLoot(...)` | 盟友化并按击杀结算掉落/经验 |

## 9. 运行机制与调用链

```
Buff.affect(enemy, SomeAllyBuff.class)
└── AllyBuff.attachTo(enemy)
    ├── super.attachTo(enemy)
    ├── enemy.alignment = ALLY
    └── 移除 PinCushion

AllyBuff.affectAndLoot(enemy, hero, buffCls)
├── Buff.affect(enemy, buffCls)
└── [成功且原本为敌]
    ├── 掉落物品
    ├── 增加击杀统计
    ├── 处理挑战与图鉴
    ├── 发放经验
    └── [MONK] 获取 MonkEnergy
```

## 10. 资源、配置与国际化关联

AllyBuff 本类没有直接国际化文本、图标或描述资源；具体文案由其子类提供。

## 11. 使用示例

```java
AllyBuff.affectAndLoot(enemy, hero, SomeCharmLikeBuff.class);

if (enemy.alignment == Char.Alignment.ALLY) {
    // 敌人已转成盟友
}
```

## 12. 开发注意事项

- 盟友化后的单位死亡时默认不会掉落物品、也不会给经验，所以 `affectAndLoot()` 专门补了这段结算。
- `Mimic` 即使不满足普通 `ENEMY` 对齐，也会被当成可结算目标处理。
- 设置 `qualifiedForNoKilling = false` 表示这种“转盟友但按击杀结算”的行为会破坏无杀资格。

## 13. 修改建议与扩展点

- 如果后续还有更多“非击杀但算击杀”的机制，可以考虑把 `affectAndLoot()` 抽成更通用的结算工具。
- 若某个子类不想在转盟友时移除 `PinCushion`，需要重新审视这一父类通用逻辑。

## 14. 事实核查清单

- [x] 已覆盖全部自有方法
- [x] 已验证继承关系 `extends Buff`
- [x] 已验证 `revivePersists = true`
- [x] 已验证附着后会改成 `ALLY`
- [x] 已验证 `PinCushion` 清理逻辑
- [x] 已验证掉落、经验、统计与武僧能量结算链
- [x] 无臆测性机制说明
