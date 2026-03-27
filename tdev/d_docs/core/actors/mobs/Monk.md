# Monk 武僧 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/Monk.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.mobs |
| **类类型** | public class |
| **继承关系** | extends Mob |
| **代码行数** | 160 |
| **首次出现** | 原版 Pixel Dungeon |

---

## 类职责

Monk（武僧）是游戏中出现于矮人都城区域的亡灵类怪物，具有以下核心职责：

1. **快速攻击**：攻击速度是普通怪物的2倍（攻击延迟减半）
2. **专注格挡**：进入HUNTING状态时获得Focus（专注）Buff，可完美闪避一次攻击
3. **格挡冷却**：成功格挡后进入冷却期，冷却时间可通过移动加速恢复
4. **任务关联**：与恶魔Imp任务相关，击杀可掉落矮人信物
5. **亡灵属性**：具有亡灵属性，受圣水等亡灵克制道具影响

**核心设计模式**：
- **状态模式（State Pattern）**：通过Focus Buff管理格挡状态
- **策略模式（Strategy Pattern）**：移动可加速专注恢复

**出现区域**：矮人都城（Metropolis，第4大层，第21-25层）

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
        +AiState state
        +damageRoll() int
        +attackSkill(Char) int
        +attackDelay() float
        +drRoll() int
        +defenseSkill(Char) int
        +defenseVerb() String
        +act() boolean
        +spend(float) void
        +move(int, boolean) void
        +rollToDropLoot() void
        +storeInBundle(Bundle) void
        +restoreFromBundle(Bundle) void
    }
    
    class Monk {
        -float focusCooldown
        +damageRoll() int
        +attackSkill(Char) int
        +attackDelay() float
        +drRoll() int
        +defenseSkill(Char) int
        +defenseVerb() String
        #act() boolean
        #spend(float) void
        +move(int, boolean) void
        +rollToDropLoot() void
        +storeInBundle(Bundle) void
        +restoreFromBundle(Bundle) void
    }
    
    class Focus {
        +int icon()
        +tintIcon(Image) void
    }
    
    class Buff {
        <<abstract>>
        +buffType type
        +boolean announced
        +detach() void
    }
    
    class Imp_Quest {
        +process(Mob) void
    }
    
    Mob <|-- Monk
    Buff <|-- Focus
    Monk +-- Focus
    Monk ..> Imp_Quest : 任务关联
    Monk ..> Focus : 施加/移除
```

---

## 静态常量表

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `FOCUS_COOLDOWN` | String | "focus_cooldown" | Bundle存储键名，用于序列化focusCooldown字段 |

---

## 实例字段表

### 基础属性（通过初始化块设置）

| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `spriteClass` | Class | MonkSprite.class | 精灵类 |
| `HP` | int | 70 | 当前生命值 |
| `HT` | int | 70 | 最大生命值 |
| `defenseSkill` | int | 30 | 防御技能值（基础值） |
| `EXP` | int | 11 | 击杀经验 |
| `maxLvl` | int | 21 | 最大有效等级 |
| `loot` | Object | Food.class | 掉落物类型（食物类） |
| `lootChance` | float | 0.083f | 掉落概率（约8.3%） |
| `properties` | Set | {UNDEAD} | 亡灵属性 |

### 实例字段

| 字段名 | 类型 | 初始值 | 说明 |
|--------|------|--------|------|
| `focusCooldown` | float | 0 | 专注冷却时间，用于控制Focus Buff的重新获取 |

### 属性分析

| 属性 | 数值 | 对比分析 |
|------|------|----------|
| 生命值 | 70 | 中等，与Warlock相同 |
| 防御技能 | 30 | 较高，约60%基础闪避率 |
| 经验值 | 11 | 较高，与Warlock相同 |
| 伤害 | 12-25 | 高伤害，方差大 |
| 护甲 | 0-2 | 低护甲减伤 |
| 攻击速度 | 2倍 | 核心特性，攻击延迟减半 |

---

## 7. 方法详解

### damageRoll()

```java
@Override
public int damageRoll() {
    return Random.NormalIntRange(12, 25);
}
```

**方法作用**：计算近战攻击伤害。

**返回值**：12-25之间的随机整数

**设计说明**：
- 伤害范围较宽，最小12最高25
- 配合双倍攻击速度，DPS极高
- 期望伤害约18.5，每回合期望输出约37

---

### attackSkill(Char target)

```java
@Override
public int attackSkill(Char target) {
    return 30;
}
```

**方法作用**：返回攻击技能值。

**参数**：
- `target` (Char)：攻击目标（未使用）

**返回值**：固定值 30

**命中计算**：命中概率 ≈ attackSkill / (attackSkill + target.defenseSkill)
- 对玩家（假设防御20）：30/(30+20) = 60%
- 配合双倍攻击速度，实际威胁极高

---

### attackDelay()

```java
@Override
public float attackDelay() {
    return super.attackDelay() * 0.5f;
}
```

**方法作用**：返回攻击延迟时间。

**返回值**：父类攻击延迟 × 0.5

**设计说明**：
- 父类 `Mob.attackDelay()` 默认返回1f
- 武僧攻击延迟为0.5f，即每回合可攻击2次
- 这是武僧的核心战斗特性

**攻击速度对比**：

| 怪物类型 | 攻击延迟 | 每回合攻击次数 |
|----------|----------|----------------|
| 普通怪物 | 1.0f | 1次 |
| 武僧 | 0.5f | 2次 |
| 激素涌动怪物 | 0.67f | 1.5次 |

---

### drRoll()

```java
@Override
public int drRoll() {
    return super.drRoll() + Random.NormalIntRange(0, 2);
}
```

**方法作用**：计算伤害减免值。

**返回值**：基础减免 + 0-2随机值

**设计说明**：
- 护甲值较低（0-2），物理防御较弱
- 依赖Focus闪避而非护甲减伤

---

### act() — 行动逻辑

```java
@Override
protected boolean act() {
    boolean result = super.act();  // 第1行：执行父类行动逻辑
    
    // 第2-4行：获得Focus的条件判断
    if (buff(Focus.class) == null           // 没有Focus Buff
            && state == HUNTING             // 处于追猎状态
            && focusCooldown <= 0) {        // 冷却已结束
        Buff.affect(this, Focus.class);     // 施加Focus
    }
    return result;
}
```

**方法作用**：怪物每回合的行动逻辑，包含Focus获取判断。

**执行流程**：

```
┌─────────────────────────────────────────────────┐
│                  act()                          │
└─────────────────────────────────────────────────┘
                      │
                      ▼
            super.act() 执行
                      │
                      ▼
         ┌────────────────────────┐
         │  buff(Focus) == null?  │
         └────────────────────────┘
              │           │
             是           否
              │           │
              ▼           └──► 返回result
         ┌────────────┐
         │ state ==   │
         │  HUNTING?  │
         └────────────┘
              │           │
             是           否
              │           │
              ▼           └──► 返回result
         ┌────────────┐
         │ cooldown   │
         │   <= 0?    │
         └────────────┘
              │           │
             是           否
              │           │
              ▼           └──► 返回result
         Buff.affect()
         (Focus.class)
              │
              ▼
           返回result
```

**Focus获取条件**：
1. 当前没有Focus Buff
2. 处于HUNTING状态（正在追击敌人）
3. focusCooldown ≤ 0（冷却已结束）

---

### spend(float time) — 时间消耗

```java
@Override
protected void spend(float time) {
    focusCooldown -= time;    // 第1行：冷却随时间减少
    super.spend(time);        // 第2行：调用父类方法
}
```

**方法作用**：消耗时间并更新冷却。

**参数**：
- `time` (float)：消耗的时间量

**设计说明**：
- 每消耗1单位时间，focusCooldown减少1
- 自然等待会逐渐恢复专注能力

---

### move(int step, boolean travelling) — 移动加速

```java
@Override
public void move(int step, boolean travelling) {
    // moving reduces cooldown by an additional 0.67, giving a total reduction of 1.67f.
    // basically monks will become focused notably faster if you kite them.
    if (travelling) focusCooldown -= 0.67f;  // 移动额外减少冷却
    super.move(step, travelling);
}
```

**方法作用**：移动并加速冷却恢复。

**参数**：
- `step` (int)：目标位置
- `travelling` (boolean)：是否为移动行为

**冷却机制详解**：

| 行为 | 冷却减少 | 说明 |
|------|----------|------|
| 等待1回合 | 1.0 | 基础时间消耗 |
| 移动1格 | 1.67 | 时间消耗 + 移动加成 |
| 格挡成功 | 设置为6-7 | 触发冷却 |

**设计意图**：
- 风筝武僧（kite）反而会使其更快恢复专注
- 鼓励玩家在武僧专注时主动进攻而非逃跑
- 这是一种反直觉的游戏机制设计

---

### defenseSkill(Char enemy) — 防御技能

```java
@Override
public int defenseSkill(Char enemy) {
    // 第1-4行：Focus状态下的无限闪避
    if (buff(Focus.class) != null      // 有Focus Buff
            && paralysed == 0           // 未瘫痪
            && state != SLEEPING) {     // 未睡眠
        return INFINITE_EVASION;        // 返回无限闪避
    }
    return super.defenseSkill(enemy);   // 否则返回基础防御
}
```

**方法作用**：返回防御技能值，Focus状态下可完美闪避。

**参数**：
- `enemy` (Char)：攻击者

**返回值**：
- Focus状态下：`INFINITE_EVASION`（无限闪避）
- 否则：父类防御技能值（30）

**无限闪避机制**：
- `INFINITE_EVASION` 定义在 `Char` 类中
- 值为 `Integer.MAX_VALUE` 或类似极大值
- 使命中概率趋近于0

**例外情况**：
| 状态 | 能否闪避 | 说明 |
|------|----------|------|
| 正常+Focus | ✓ | 完美闪避 |
| 瘫痪 | ✗ | 无法行动 |
| 睡眠 | ✗ | 毫无防备 |

---

### defenseVerb() — 格挡核心逻辑

```java
@Override
public String defenseVerb() {
    Focus f = buff(Focus.class);
    
    // 第1-3行：无Focus时使用默认防御动词
    if (f == null) {
        return super.defenseVerb();
    }
    
    // 第4-9行：有Focus时的格挡处理
    f.detach();  // 移除Focus Buff
    
    // 第6-7行：播放格挡音效
    if (sprite != null && sprite.visible) {
        Sample.INSTANCE.play(Assets.Sounds.HIT_PARRY, 1, Random.Float(0.96f, 1.05f));
    }
    
    // 第8行：设置冷却时间
    focusCooldown = Random.NormalFloat(6, 7);
    
    // 第9行：返回格挡消息
    return Messages.get(this, "parried");
}
```

**方法作用**：处理格挡成功后的效果。

**返回值**：格挡消息字符串

**执行流程**：

```
┌─────────────────────────────────────────────────┐
│              defenseVerb()                      │
└─────────────────────────────────────────────────┘
                      │
                      ▼
            buff(Focus.class)
                      │
           ┌──────────┴──────────┐
           ▼                     ▼
        f == null             f != null
           │                     │
           ▼                     ▼
    super.defenseVerb()     f.detach()
    (普通闪避)                    │
                                ▼
                         播放格挡音效
                                │
                                ▼
                      focusCooldown = 6-7
                                │
                                ▼
                    return "parried"
```

**格挡效果**：
1. **移除Focus**：格挡后立即失去专注状态
2. **播放音效**：`HIT_PARRY` 音效，音高随机微调
3. **触发冷却**：6-7回合冷却期
4. **显示消息**：显示"格挡"文本

---

### rollToDropLoot() — 战利品掉落

```java
@Override
public void rollToDropLoot() {
    Imp.Quest.process(this);  // 第1行：处理Imp任务
    super.rollToDropLoot();   // 第2行：执行标准掉落
}
```

**方法作用**：处理死亡时的战利品掉落。

**Imp任务关联**：
- 如果Imp任务激活且要求击杀Monk
- 击杀时会掉落 `DwarfToken`（矮人信物）
- 任务需要收集4-5个信物

**普通掉落**：
- 8.3%概率掉落食物
- 玩家等级超过23后不再掉落

---

### storeInBundle(Bundle) / restoreFromBundle(Bundle) — 序列化

```java
private static String FOCUS_COOLDOWN = "focus_cooldown";

@Override
public void storeInBundle(Bundle bundle) {
    super.storeInBundle(bundle);
    bundle.put(FOCUS_COOLDOWN, focusCooldown);
}

@Override
public void restoreFromBundle(Bundle bundle) {
    super.restoreFromBundle(bundle);
    focusCooldown = bundle.getInt(FOCUS_COOLDOWN);
}
```

**方法作用**：保存和恢复怪物状态。

**保存内容**：
- 父类保存的所有数据
- `focusCooldown` 专注冷却时间

---

## 内部类详解

### Focus — 专注Buff

```java
public static class Focus extends Buff {
    
    {
        type = buffType.POSITIVE;  // 正面Buff
        announced = true;          // 获得时公告
    }
    
    @Override
    public int icon() {
        return BuffIndicator.MIND_VISION;  // 使用心灵视觉图标
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.25f, 1.5f, 1f);  // 青色调染色
    }
}
```

**类作用**：表示武僧的专注状态，提供完美闪避能力。

**Buff属性**：

| 属性 | 值 | 说明 |
|------|-----|------|
| `type` | POSITIVE | 正面效果，可被某些能力驱散 |
| `announced` | true | 获得时在战斗日志显示 |

**图标显示**：
- 使用 `BuffIndicator.MIND_VISION` 图标（本应为心灵视觉，此处复用）
- 青色高亮：RGB(0.25, 1.5, 1.0)

**行为特点**：
- 本身不提供任何被动效果
- 效果由 `Monk.defenseSkill()` 和 `Monk.defenseVerb()` 实现
- 格挡后立即被 `detach()` 移除

---

## Focus机制详解

### 冷却循环

```
┌──────────────────────────────────────────────────────────────┐
│                     Focus 冷却循环                           │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│   ┌─────────┐    cooldown <= 0     ┌─────────┐             │
│   │ 无Focus │ ──────────────────▶  │ 有Focus │             │
│   └─────────┘                      └─────────┘             │
│        ▲                                │                   │
│        │                                │ 被攻击            │
│        │                                ▼                   │
│        │                         ┌───────────┐             │
│        │                         │  格挡成功  │             │
│        │                         └───────────┘             │
│        │                                │                   │
│        │                                │ cooldown = 6-7    │
│        │                                ▼                   │
│        │                         ┌───────────┐             │
│        └─────────────────────────│ 冷却等待  │             │
│              等待/移动冷却减少    └───────────┘             │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

### 冷却时间计算

| 场景 | 冷却来源 | 冷却值 |
|------|----------|--------|
| 初始 | - | 0（可立即获得Focus） |
| 格挡成功 | `defenseVerb()` | Random(6, 7) |
| 每回合等待 | `spend()` | -1.0 |
| 每次移动 | `move()` | -1.67 |

**最快恢复时间**：
- 冷却6回合
- 全程移动：6 ÷ 1.67 ≈ 3.6回合
- 全程等待：6回合

### 战斗策略图

```
┌──────────────────────────────────────────────────────────────┐
│                   玩家对战武僧策略                            │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│   武僧状态          玩家最佳策略                              │
│   ─────────        ──────────────                            │
│                                                              │
│   有Focus ────────▶ 不要攻击，等待或移动                      │
│       │                   │                                  │
│       │ 格挡              │ Focus消失                        │
│       ▼                   ▼                                  │
│   无Focus ─────────▶ 攻击！                                  │
│   (冷却中)               │                                   │
│       │                  │ 成功造成伤害                      │
│       │                  ▼                                   │
│       └────────────── 武僧受伤                               │
│                                                              │
│   注意：移动会加速武僧恢复Focus！                             │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

## 与其他类的交互

### 依赖关系

| 类名 | 用途 |
|------|------|
| `Mob` | 父类，提供基础怪物功能 |
| `Buff` | Focus的父类 |
| `Focus` | 内部类，专注状态Buff |
| `MonkSprite` | 渲染精灵和动画 |
| `Imp.Quest` | 任务系统，处理矮人信物掉落 |
| `Food` | 战利品类型 |
| `BuffIndicator` | Buff图标显示 |
| `Sample` | 音效播放 |
| `Messages` | 国际化消息 |

### 被使用关系

| 类名 | 使用方式 |
|------|----------|
| `Imp.Quest` | `Imp.Quest.process(Monk)` 检查任务进度 |
| `DwarfToken` | 任务相关掉落物 |

---

## 11. 使用示例

### 基础使用

```java
// 创建武僧
Monk monk = new Monk();
monk.pos = somePosition;
Dungeon.level.mobs.add(monk);
```

### 检查Focus状态

```java
// 检查武僧是否有Focus
Monk monk = (Monk) someMob;
if (monk.buff(Monk.Focus.class) != null) {
    // 武僧可以格挡，不要攻击！
    GLog.w("武僧处于专注状态，攻击将被格挡！");
}
```

### 模拟格挡

```java
// 模拟攻击被格挡
Monk monk = new Monk();
Buff.affect(monk, Monk.Focus.class);  // 给予Focus

// 当玩家攻击时
int defenseValue = monk.defenseSkill(hero);  // 返回INFINITE_EVASION
String verb = monk.defenseVerb();  // 返回"parried"，Focus被移除

// 冷却已启动
System.out.println(monk.focusCooldown);  // 输出: 6.x
```

### 自定义类似怪物

```java
/**
 * 创建一个具有闪避能力的自定义怪物
 */
public class Ninja extends Mob {
    
    protected float dodgeCooldown = 0;
    
    {
        spriteClass = NinjaSprite.class;
        HP = HT = 50;
        defenseSkill = 20;
        EXP = 8;
    }
    
    @Override
    protected boolean act() {
        boolean result = super.act();
        if (buff(Dodge.class) == null && state == HUNTING && dodgeCooldown <= 0) {
            Buff.affect(this, Dodge.class);
        }
        return result;
    }
    
    @Override
    protected void spend(float time) {
        dodgeCooldown -= time;
        super.spend(time);
    }
    
    @Override
    public int defenseSkill(Char enemy) {
        if (buff(Dodge.class) != null && paralysed == 0) {
            return INFINITE_EVASION;
        }
        return super.defenseSkill(enemy);
    }
    
    @Override
    public String defenseVerb() {
        Dodge d = buff(Dodge.class);
        if (d == null) {
            return super.defenseVerb();
        }
        d.detach();
        dodgeCooldown = 5;  // 5回合冷却
        return Messages.get(this, "dodged");
    }
    
    // Dodge Buff内部类
    public static class Dodge extends Buff {
        {
            type = buffType.POSITIVE;
            announced = true;
        }
        
        @Override
        public int icon() {
            return BuffIndicator.INVISIBLE;
        }
    }
}
```

---

## 注意事项

### Focus机制限制

1. **状态限制**：只有HUNTING状态才会获得Focus
   - SLEEPING、WANDERING、PASSIVE状态不会获得
   - 可利用这点进行偷袭

2. **瘫痪无效**：瘫痪时Focus不会生效
   - `paralysed > 0` 时即使有Focus也无法格挡

3. **睡眠无效**：睡眠状态下Focus不生效
   - 可在睡眠时安全击杀

### 冷却机制

1. **移动加速**：风筝战术会加速武僧恢复Focus
   - 移动减少1.67冷却，等待只减少1.0
   - 站桩输出反而是更好的策略

2. **冷却随机性**：格挡后冷却为6-7回合
   - 不确定性增加战斗策略深度

### 任务关联

1. **Imp任务**：矮人都城的恶魔Imp任务
   - 17层必定要求击杀武僧
   - 19层必定要求击杀Golem
   - 18层随机

2. **信物掉落**：任务激活后击杀掉落DwarfToken
   - 需要收集4-5个

### 战斗建议

| 情况 | 建议 |
|------|------|
| 武僧有Focus | 不要攻击，等待其失去Focus |
| 武僧无Focus | 立即攻击 |
| 风筝移动 | 会加速其恢复Focus，不推荐 |
| 站桩输出 | 更安全的策略 |

---

## 最佳实践

### 设计类似机制

1. **状态检查**：在`defenseSkill()`中检查所有限制条件
2. **冷却管理**：使用独立字段而非Buff持续时间
3. **视觉反馈**：提供清晰的Buff图标和音效

### 平衡设计参考

| 属性 | 建议值（高阶怪物） | Monk值 |
|------|-------------------|--------|
| 生命值 | 60-100 | 70 |
| 防御技能 | 25-35 | 30 |
| 攻击技能 | 25-35 | 30 |
| 经验值 | 10-15 | 11 |
| 伤害范围 | 12-25 | 12-25 |
| 特殊能力 | 1-2个 | 双倍攻击+格挡 |
| 攻击速度 | 0.5-1.0 | 0.5 |

### 代码风格

1. **命名约定**：`Focus` 表示专注状态
2. **冷却常量**：可考虑将6-7抽取为常量
3. **消息使用**：使用 `Messages.get()` 支持国际化

### 扩展建议

```java
// 可配置的冷却参数
private static final float BASE_COOLDOWN = 6f;
private static final float COOLDOWN_VARIANCE = 1f;

// 在defenseVerb()中使用
focusCooldown = BASE_COOLDOWN + Random.Float(COOLDOWN_VARIANCE);
```

---

## 调试技巧

### 查看Focus状态

```java
// 调试输出
Monk monk = (Monk) target;
System.out.println("Focus: " + (monk.buff(Monk.Focus.class) != null));
System.out.println("Cooldown: " + monk.focusCooldown);
System.out.println("State: " + monk.state);
```

### 强制移除Focus

```java
// 调试用：强制移除Focus
Focus f = monk.buff(Monk.Focus.class);
if (f != null) {
    f.detach();
}
```

### 重置冷却

```java
// 调试用：重置冷却
monk.focusCooldown = 0;
```

---

## 相关文件

| 文件 | 说明 |
|------|------|
| `MonkSprite.java` | 武僧精灵类，渲染和动画 |
| `Imp.java` | 恶魔NPC，相关任务 |
| `DwarfToken.java` | 矮人信物，任务物品 |
| `Food.java` | 食物类，战利品 |
| `BuffIndicator.java` | Buff图标显示 |
| `messages/monks.properties` | 国际化消息文件 |