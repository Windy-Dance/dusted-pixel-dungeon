# Warlock 术士 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/Warlock.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.mobs |
| **类类型** | public class |
| **继承关系** | extends Mob implements Callback |
| **代码行数** | 167 |
| **首次出现** | 原版 Pixel Dungeon |

---

## 类职责

Warlock（术士）是游戏中出现于监狱区域的亡灵类怪物，具有以下核心职责：

1. **远程魔法攻击**：使用黑暗法术进行远程攻击，无需靠近敌人
2. **装备降级诅咒**：攻击有概率施加 Degrade（降级）效果，降低玩家装备等级
3. **亡灵属性**：具有亡灵属性，受圣水等亡灵克制道具影响
4. **战利品掉落**：掉落药水，有概率掉落治疗药水

**核心设计模式**：策略模式（Strategy Pattern）—— 根据敌人距离选择近战或远程攻击策略

**出现区域**：监狱（Prison，第2大层）

---

## 4. 继承与协作关系

```mermaid
classDiagram
    class Mob {
        <<abstract>>
        +int HP, HT
        +int defenseSkill
        +int EXP
        +int maxLvl
        +Class~? extends CharSprite~ spriteClass
        +Item loot
        +float lootChance
        +Set~Property~ properties
        +damageRoll() int
        +attackSkill(Char) int
        +drRoll() int
        +canAttack(Char) boolean
        +doAttack(Char) boolean
        +createLoot() Item
    }
    
    class Warlock {
        -float TIME_TO_ZAP
        +damageRoll() int
        +attackSkill(Char) int
        +drRoll() int
        +canAttack(Char) boolean
        #doAttack(Char) boolean
        #zap() void
        +onZapComplete() void
        +call() void
        +createLoot() Item
    }
    
    class Callback {
        <<interface>>
        +call() void
    }
    
    class DarkBolt {
        <<static inner class>>
    }
    
    class Degrade {
        +float DURATION
        +reduceLevel(int) int
    }
    
    class Ballistica {
        +int collisionPos
        +MAGIC_BOLT$
    }
    
    Mob <|-- Warlock
    Callback <|.. Warlock
    Warlock +-- DarkBolt
    Warlock ..> Degrade : 施加
    Warlock ..> Ballistica : 使用
```

---

## 静态常量表

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `TIME_TO_ZAP` | float | 1f | 施法所需时间（1回合） |

---

## 实例字段表

### 基础属性（通过初始化块设置）

| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `spriteClass` | Class | WarlockSprite.class | 精灵类 |
| `HP` | int | 70 | 当前生命值 |
| `HT` | int | 70 | 最大生命值 |
| `defenseSkill` | int | 18 | 防御技能值 |
| `EXP` | int | 11 | 击杀经验 |
| `maxLvl` | int | 21 | 最大有效等级 |
| `loot` | Object | Generator.Category.POTION | 掉落物类型（药水类） |
| `lootChance` | float | 0.5f | 掉落概率（50%） |
| `properties` | Set | {UNDEAD} | 亡灵属性 |

### 属性分析

| 属性 | 数值 | 对比分析 |
|------|------|----------|
| 生命值 | 70 | 中等偏高，监狱区域最强普通怪物之一 |
| 防御技能 | 18 | 中等，约50%闪避率 |
| 经验值 | 11 | 较高，监狱区域最高 |
| 伤害 | 12-18 | 远程与近战伤害相同 |
| 护甲 | 0-8 | 随机减伤 |

---

## 7. 方法详解

### damageRoll()

```java
@Override
public int damageRoll() {
    return Random.NormalIntRange(12, 18);
}
```

**方法作用**：计算近战攻击伤害。

**返回值**：12-18之间的随机整数

**设计说明**：
- 远程魔法攻击使用相同的伤害范围
- 伤害稳定，方差较小

---

### attackSkill(Char target)

```java
@Override
public int attackSkill(Char target) {
    return 25;
}
```

**方法作用**：返回攻击技能值。

**参数**：
- `target` (Char)：攻击目标（未使用）

**返回值**：固定值 25

**命中计算**：命中概率 ≈ attackSkill / (attackSkill + target.defenseSkill)
- 对玩家（假设防御20）：25/(25+20) ≈ 55.6%

---

### drRoll()

```java
@Override
public int drRoll() {
    return super.drRoll() + Random.NormalIntRange(0, 8);
}
```

**方法作用**：计算伤害减免值。

**返回值**：基础减免 + 0-8随机值

**继承说明**：调用父类方法后再添加随机减免

---

### canAttack(Char enemy)

```java
@Override
protected boolean canAttack(Char enemy) {
    return super.canAttack(enemy)
            || new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
}
```

**方法作用**：判断是否可以攻击目标（扩展攻击范围）。

**参数**：
- `enemy` (Char)：攻击目标

**返回值**：true 如果可以攻击

**攻击条件**：
1. **近战范围**：`super.canAttack(enemy)` —— 相邻格子
2. **魔法范围**：魔法弹道能直击目标位置

**Ballistica.MAGIC_BOLT**：
- 魔法弹道类型
- 可穿透某些障碍物
- 计算直线路径上的第一个碰撞点

---

### doAttack(Char enemy) — 核心攻击逻辑

```java
protected boolean doAttack(Char enemy) {
    // 第1-3行：判断攻击类型
    if (Dungeon.level.adjacent(pos, enemy.pos)
            || new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos != enemy.pos) {
        
        // 近战攻击
        return super.doAttack(enemy);
        
    } else {
        // 远程魔法攻击
        if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
            sprite.zap(enemy.pos);  // 播放施法动画
            return false;            // 等待动画完成
        } else {
            zap();                   // 直接施法
            return true;
        }
    }
}
```

**方法作用**：执行攻击行为，选择近战或远程。

**参数**：
- `enemy` (Char)：攻击目标

**返回值**：
- `true`：攻击已完成
- `false`：等待动画回调

**攻击决策流程**：

```
┌─────────────────────────────────────────────────┐
│              doAttack(enemy)                    │
└─────────────────────────────────────────────────┘
                      │
          ┌───────────┴───────────┐
          ▼                       ▼
    相邻 OR 魔法弹道         魔法弹道可达
    不可达目标                  目标
          │                       │
          ▼                       ▼
    super.doAttack()         远程魔法攻击
    (近战攻击)                    │
                              ┌───┴───┐
                              ▼       ▼
                          可见精灵   不可见
                              │       │
                              ▼       ▼
                        sprite.zap()  zap()
                        + return false  return true
                              │
                              ▼
                      onZapComplete()
                              │
                              ▼
                           zap()
```

---

### zap() — 魔法攻击核心

```java
protected void zap() {
    spend(TIME_TO_ZAP);  // 消耗1回合

    Invisibility.dispel(this);  // 显形
    Char enemy = this.enemy;
    
    // 第1-5行：命中判定
    if (hit(this, enemy, true)) {
        // 第6-10行：施加降级效果（50%概率）
        if (enemy == Dungeon.hero && Random.Int(2) == 0) {
            Buff.prolong(enemy, Degrade.class, Degrade.DURATION);
            Sample.INSTANCE.play(Assets.Sounds.DEGRADE);
        }
        
        // 第11-15行：计算伤害
        int dmg = Random.NormalIntRange(12, 18);
        dmg = Math.round(dmg * AscensionChallenge.statModifier(this));

        // 第16-20行：侵略石减伤逻辑
        if (enemy.buff(StoneOfAggression.Aggression.class) != null
                && enemy.alignment == alignment
                && (Char.hasProp(enemy, Property.BOSS) || Char.hasProp(enemy, Property.MINIBOSS))){
            dmg *= 0.5f;  // 对被侵略标记的Boss/小Boss伤害减半
        }

        enemy.damage(dmg, new DarkBolt());
        
        // 第21-25行：击杀英雄处理
        if (enemy == Dungeon.hero && !enemy.isAlive()) {
            Badges.validateDeathFromEnemyMagic();
            Dungeon.fail(this);
            GLog.n(Messages.get(this, "bolt_kill"));
        }
    } else {
        // 未命中
        enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
    }
}
```

**方法作用**：执行远程魔法攻击。

**执行流程**：

| 步骤 | 操作 | 说明 |
|------|------|------|
| 1 | `spend(TIME_TO_ZAP)` | 消耗1回合时间 |
| 2 | `Invisibility.dispel(this)` | 打破隐身状态 |
| 3 | `hit(this, enemy, true)` | 命中判定 |
| 4 | `Buff.prolong(...)` | 施加降级效果（仅对玩家，50%概率） |
| 5 | `damage(dmg, DarkBolt)` | 造成伤害 |

**伤害计算**：

```
基础伤害 = Random(12-18)
最终伤害 = 基础伤害 × 飞升挑战修正 × 侵略石修正
```

**特殊机制**：

1. **降级效果（Degrade）**：
   - 只对玩家生效
   - 50%概率施加
   - 持续30回合
   - 降低装备等级效果

2. **DarkBolt 伤害类型**：
   - 内部静态类 `DarkBolt{}`
   - 用于区分近战和魔法伤害
   - 可被特定抗性减免

3. **飞升挑战修正**：
   - `AscensionChallenge.statModifier(this)` 
   - 根据飞升挑战状态调整伤害

---

### onZapComplete()

```java
public void onZapComplete() {
    zap();
    next();
}
```

**方法作用**：动画回调，施法动画完成后执行。

**调用时机**：`WarlockSprite.zap()` 动画结束时回调

**执行流程**：
1. 执行 `zap()` 进行实际攻击
2. 调用 `next()` 让怪物继续行动队列

---

### call() — Callback接口实现

```java
@Override
public void call() {
    next();
}
```

**方法作用**：实现 Callback 接口，用于异步回调。

**使用场景**：当动画需要异步等待时，动画结束后调用此方法

---

### createLoot() — 战利品生成

```java
@Override
public Item createLoot() {
    // 第1-5行：治疗药水掉落逻辑
    // 1/6概率，随掉落次数递减（最多8次后归零）
    if (Random.Int(3) == 0 && Random.Int(8) > Dungeon.LimitedDrops.WARLOCK_HP.count) {
        Dungeon.LimitedDrops.WARLOCK_HP.count++;
        return new PotionOfHealing();
    } else {
        // 第6-10行：普通药水掉落（排除治疗药水）
        Item i;
        do {
            i = Generator.randomUsingDefaults(Generator.Category.POTION);
        } while (i instanceof PotionOfHealing);
        return i;
    }
}
```

**方法作用**：生成击杀掉落物品。

**掉落机制**：

| 掉落类型 | 概率 | 说明 |
|----------|------|------|
| 治疗药水 | 1/6 × (8-count)/8 | 有限掉落，最多8次 |
| 其他药水 | 剩余概率 | 随机药水（排除治疗） |

**治疗药水掉落概率衰减**：

```
第1次: 1/6 × 8/8 ≈ 16.7%
第2次: 1/6 × 7/8 ≈ 14.6%
第3次: 1/6 × 6/8 ≈ 12.5%
...
第8次: 1/6 × 1/8 ≈ 2.1%
第9次+: 0%
```

---

## 内部类详解

### DarkBolt — 黑暗法术伤害类型

```java
public static class DarkBolt{}
```

**用途**：
- 标记伤害来源为黑暗魔法
- 允许其他系统区分近战和魔法伤害

**使用示例**：
```java
enemy.damage(dmg, new DarkBolt());
```

**抗性系统**：
- 某些装备/Buff可以针对特定伤害类型提供抗性
- 通过 `resist()` 方法检查 `DarkBolt.class`

---

## 魔法攻击机制详解

### 弹道计算（Ballistica）

Warlock 使用 `Ballistica` 类计算魔法弹道路径：

```java
Ballistica shot = new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT);
```

**Ballistica.MAGIC_BOLT 参数**：
- 魔法弹道类型
- 可以穿过某些地形
- 计算直线弹道直到碰撞

**碰撞检测**：
```java
shot.collisionPos == enemy.pos  // 弹道终点是否为目标位置
```

### 攻击范围判定

```
┌──────────────────────────────────────────────────────┐
│                   攻击范围示意图                       │
├──────────────────────────────────────────────────────┤
│                                                      │
│      ○ ○ ○ ○ ○ ○ ○    ○ = 魔法攻击范围               │
│     ○ ○ ○ ○ ○ ○ ○     ● = 近战攻击范围               │
│     ○ ○ ● W ● ○ ○     W = Warlock                    │
│     ○ ○ ○ ○ ○ ○ ○                                   │
│      ○ ○ ○ ○ ○ ○ ○                                  │
│                                                      │
│    可攻击视线内任何目标（需无障碍物遮挡）              │
└──────────────────────────────────────────────────────┘
```

### 动画同步机制

```
┌─────────────┐     zap(pos)      ┌──────────────────┐
│  Warlock    │ ───────────────▶ │  WarlockSprite   │
│  (逻辑层)   │                   │  (渲染层)        │
└─────────────┘                   └──────────────────┘
       │                                   │
       │ return false                     │ 播放施法动画
       │ (等待回调)                        │
       ▼                                   ▼
   暂停行动                          动画结束
                                           │
                                           │ onZapComplete()
                                           ▼
                                    ┌──────────────────┐
                                    │  zap() + next()  │
                                    │  执行攻击并继续  │
                                    └──────────────────┘
```

---

## 与其他类的交互

### 依赖关系

| 类名 | 用途 |
|------|------|
| `Mob` | 父类，提供基础怪物功能 |
| `Ballistica` | 计算魔法弹道路径 |
| `Degrade` | 施加降级效果 |
| `Invisibility` | 打破隐身状态 |
| `WarlockSprite` | 渲染精灵和动画 |
| `Generator` | 生成随机药水 |
| `PotionOfHealing` | 治疗药水掉落 |
| `AscensionChallenge` | 飞升挑战伤害修正 |
| `StoneOfAggression` | 侵略石机制 |

### 继承关系

| 类 | 关系 |
|----|------|
| `Char` | 祖父类（Mob extends Char） |
| `Mob` | 父类 |
| `Callback` | 实现接口 |

---

## 11. 使用示例

### 生成术士怪物

```java
// 在关卡生成时
Warlock warlock = new Warlock();
warlock.pos = somePosition;
Dungeon.level.mobs.add(warlock);

// 或使用 Mob 的静态方法
Mob warlock = Mob.create(Warlock.class);
```

### 自定义远程攻击怪物

```java
public class CustomMage extends Mob implements Callback {
    
    private static final float TIME_TO_CAST = 1.5f;
    
    {
        spriteClass = CustomMageSprite.class;
        HP = HT = 50;
        defenseSkill = 15;
        EXP = 8;
        maxLvl = 18;
    }
    
    @Override
    protected boolean canAttack(Char enemy) {
        return super.canAttack(enemy)
                || new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
    }
    
    @Override
    protected boolean doAttack(Char enemy) {
        if (Dungeon.level.adjacent(pos, enemy.pos)
                || new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos != enemy.pos) {
            return super.doAttack(enemy);
        } else {
            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                sprite.zap(enemy.pos);
                return false;
            } else {
                cast();
                return true;
            }
        }
    }
    
    protected void cast() {
        spend(TIME_TO_CAST);
        Invisibility.dispel(this);
        // 自定义施法逻辑
        if (hit(this, enemy, true)) {
            int dmg = damageRoll();
            enemy.damage(dmg, new CustomSpell());
        }
    }
    
    public void onCastComplete() {
        cast();
        next();
    }
    
    @Override
    public void call() {
        next();
    }
}
```

---

## 注意事项

### 远程攻击限制

1. **视线要求**：魔法攻击需要视线可达，被墙遮挡的目标无法攻击
2. **相邻优先**：相邻时使用近战攻击，不触发魔法
3. **时间消耗**：施法消耗1回合，与近战攻击相同

### 降级效果（Degrade）

1. **仅对玩家**：降级效果只对英雄生效
2. **50%概率**：每次命中魔法攻击有50%概率触发
3. **等级计算**：
   - 使用 `sqrt(2*(level-1)) + 1` 公式
   - 高等级装备受影响更大

### 战利品限制

1. **治疗药水限制**：全局最多掉落8次
2. **等级衰减**：玩家等级超过 `maxLvl + 2` 后不再掉落
3. **随机药水**：排除治疗药水后随机生成

### 飞升挑战

在飞升挑战中，Warlock的伤害会增加：
```java
dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
```

---

## 最佳实践

### 创建类似远程怪物

1. **实现 Callback 接口**：支持动画回调
2. **重写 canAttack()**：扩展攻击范围判定
3. **重写 doAttack()**：实现攻击类型选择
4. **创建内部伤害类型类**：便于抗性系统处理

### 平衡设计参考

| 属性 | 建议值（中阶怪物） | Warlock值 |
|------|-------------------|-----------|
| 生命值 | 40-80 | 70 |
| 防御技能 | 15-25 | 18 |
| 攻击技能 | 20-30 | 25 |
| 经验值 | 8-15 | 11 |
| 伤害范围 | 10-20 | 12-18 |
| 特殊能力 | 1个 | 远程+降级 |

### 代码风格

1. **命名约定**：`zap()` / `onZapComplete()` 用于远程攻击
2. **时间常量**：使用静态常量 `TIME_TO_ZAP`
3. **伤害类型**：使用内部静态类标记伤害来源