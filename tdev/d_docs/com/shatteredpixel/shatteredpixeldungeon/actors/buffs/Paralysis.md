# Paralysis 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Paralysis.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| **类类型** | public class |
| **继承关系** | extends FlavourBuff |
| **代码行数** | 121 行 |
| **内部类** | ParalysisResist |
| **官方中文名** | 麻痹 |

## 2. 文件职责说明

Paralysis 类表示“麻痹”Buff。它会增加目标的 `paralysed` 计数，并提供基于累计受伤量的提前解除机制；该提前解除机制由内部类 `ParalysisResist` 记录。

**核心职责**：
- 附着时增加 `paralysed`
- 受伤时累积麻痹挣脱值
- 根据累计伤害与当前生命值的随机比较决定是否提前解除
- 结束时减少 `paralysed`

## 3. 结构总览

```
Paralysis (extends FlavourBuff)
├── 常量
│   └── DURATION: float = 10f
├── 初始化块
│   ├── type = NEGATIVE
│   └── announced = true
├── 方法
│   ├── attachTo(Char): boolean
│   ├── processDamage(int): void
│   ├── detach(): void
│   ├── icon(): int
│   ├── iconFadePercent(): float
│   └── fx(boolean): void
└── 内部类 ParalysisResist extends Buff
    ├── 字段 damage: int
    ├── act(): boolean
    └── storeInBundle()/restoreFromBundle()
```

## 4. 继承与协作关系

### 继承关系图

```mermaid
classDiagram
    Buff <|-- FlavourBuff
    FlavourBuff <|-- Paralysis
    Buff <|-- ParalysisResist
    Paralysis +-- ParalysisResist
```

### 协作关系

| 协作类 | 协作方式 |
|--------|----------|
| **FlavourBuff** | 父类，提供时限型麻痹 Buff 行为 |
| **ParalysisResist** | 记录累计伤害并帮助提前挣脱 |
| **Dungeon.level.heroFOV** | 若可见则显示挣脱提示 |
| **CharSprite.State.PARALYSED** | 控制麻痹视觉状态 |
| **BuffIndicator** | 麻痹图标 |
| **Messages** | “解脱麻痹”提示文本 |
| **Random** | 计算提前挣脱概率 |
| **Bundle** | 存档读写 |

## 5. 字段与常量详解

### 常量

| 常量 | 类型 | 值 | 说明 |
|------|------|----|------|
| `DURATION` | float | `10f` | 默认持续时间 |

### 初始化块

```java
{
    type = buffType.NEGATIVE;
    announced = true;
}
```

### 内部类 `ParalysisResist`

| 字段 | 类型 | 说明 |
|------|------|------|
| `damage` | int | 累计挣脱麻痹所参考的受伤值 |

## 6. 构造与初始化机制

Paralysis 没有显式构造函数。常见施加方式：

```java
Buff.affect(target, Paralysis.class, Paralysis.DURATION);
```

## 7. 方法详解

### attachTo(Char target)

若 `super.attachTo(target)` 成功：
- `target.paralysed++`
- 返回 `true`

### processDamage(int damage)

1. 若 `target == null`，直接返回。
2. 查找或创建 `ParalysisResist`。
3. 把本次伤害累加到 `resist.damage`。
4. 若满足：

```java
Random.NormalIntRange(0, resist.damage) >= Random.NormalIntRange(0, target.HP)
```

则：
- 若该格在英雄视野内，显示 `out`
- `detach()` 提前解除麻痹

### detach()

先 `super.detach()`，再在 `target.paralysed > 0` 时减 1。

### icon()/iconFadePercent()/fx(boolean)

- 图标：`BuffIndicator.PARALYSIS`
- 淡出：`Math.max(0, (DURATION - visualcooldown()) / DURATION)`
- `fx(true)`：添加 `PARALYSED`
- `fx(false)`：仅当 `target.paralysed <= 1` 时移除视觉麻痹状态

### 内部类 `ParalysisResist.act()`

当目标已没有 `Paralysis` 时：
- `damage -= ceil(damage / 10f)`
- 若 `damage <= 0` 则移除自身
- 每回合 `spend(TICK)`

## 8. 对外暴露能力

| 方法 | 用途 |
|------|------|
| `processDamage(int)` | 处理受伤后提前挣脱麻痹 |
| `attachTo(Char)` | 附着时增加麻痹计数 |

## 9. 运行机制与调用链

```
Buff.affect(target, Paralysis.class, DURATION)
└── Paralysis.attachTo(target)
    └── target.paralysed++

目标受伤
└── Paralysis.processDamage(damage)
    ├── 获取/创建 ParalysisResist
    ├── resist.damage += damage
    └── [随机判定成功] detach()
```

## 10. 资源、配置与国际化关联

文件：`core/src/main/assets/messages/actors/actors_zh.properties`

```properties
actors.buffs.paralysis.name=麻痹
actors.buffs.paralysis.heromsg=你被麻痹了！
actors.buffs.paralysis.out=解脱麻痹
actors.buffs.paralysis.desc=通常最坏的事就是什么事都做不出来。
```

## 11. 使用示例

```java
Paralysis p = Buff.affect(enemy, Paralysis.class, Paralysis.DURATION);
p.processDamage(5);
```

## 12. 开发注意事项

- 该 Buff 的提前解除不是固定阈值，而是随机比较累计伤害与当前生命值。
- `ParalysisResist` 不会在麻痹存在时自然衰减，只有麻痹结束后才开始递减。
- `paralysed` 是计数器，视觉状态清理必须和其它麻痹来源兼容。

## 13. 修改建议与扩展点

- 若后续需要更可预测的机制，可把随机挣脱改成确定阈值，但要同步调整平衡。
- 若多类控制都需要受伤挣脱，可把 `ParalysisResist` 提取成共用辅助类。

## 14. 事实核查清单

- [x] 已覆盖全部方法、常量与内部类
- [x] 已验证继承关系 `extends FlavourBuff`
- [x] 已验证 `NEGATIVE` 与 `announced = true`
- [x] 已验证 `paralysed` 计数增减逻辑
- [x] 已验证 `processDamage()` 的随机挣脱判定
- [x] 已验证 `ParalysisResist` 的衰减规则
- [x] 已验证图标与视觉麻痹状态逻辑
- [x] 已核对官方中文名来自翻译文件
- [x] 无臆测性机制说明
