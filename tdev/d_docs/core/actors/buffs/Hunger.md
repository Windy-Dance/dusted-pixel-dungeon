# Hunger.java 饥饿系统文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Hunger.java` |
| **类名** | `Hunger` |
| **继承关系** | `extends Buff implements Hero.Doom` |
| **代码行数** | 220 行 |
| **功能概述** | 管理英雄角色的饥饿状态，处理饥饿累积、饥饿伤害和饥饿致死逻辑 |

---

## 类职责

`Hunger` 类是 Shattered Pixel Dungeon 中核心的生存机制实现，负责：

1. **饥饿状态追踪** - 追踪角色的饥饿值累积
2. **饥饿阶段管理** - 管理从"正常"→"饥饿"→"饥饿致死"的状态转换
3. **饥饿伤害处理** - 在饥饿状态下对角色造成持续伤害
4. **死亡判定** - 实现 `Hero.Doom` 接口，处理饥饿致死逻辑
5. **状态持久化** - 支持游戏存档/读档时的饥饿状态保存

---

## 4. 继承与协作关系

```
                    ┌─────────────────────────────────────┐
                    │              Actor                   │
                    │         (抽象游戏实体基类)             │
                    └──────────────┬──────────────────────┘
                                   │ extends
                    ┌──────────────▼──────────────────────┐
                    │              Buff                    │
                    │          (增益/减益基类)               │
                    └──────────────┬──────────────────────┘
                                   │ extends
                    ┌──────────────▼──────────────────────┐
                    │             Hunger                   │
                    │   implements Hero.Doom (饥饿致死接口) │
                    └──────────────┬──────────────────────┘
                                   │
          ┌────────────────────────┼────────────────────────┐
          │                        │                        │
          ▼                        ▼                        ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│    WellFed      │    │    Shadows      │    │ SaltCube        │
│   (饱腹增益)     │    │  (暗影潜行)      │    │  (盐块饰品)      │
│ 减缓饥饿消耗     │    │ 减缓饥饿消耗     │    │ 减缓饥饿消耗     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

**关键交互类：**

| 类名 | 关系类型 | 说明 |
|------|----------|------|
| `Buff` | 继承 | 基类，提供 Buff 框架功能 |
| `Hero.Doom` | 实现 | 死亡接口，处理饥饿致死回调 |
| `WellFed` | 协作 | 饱腹状态，影响饥饿增长逻辑 |
| `Shadows` | 协作 | 暗影潜行状态，减缓饥饿增长 |
| `SaltCube` | 协作 | 饰品，通过 `hungerGainMultiplier()` 减缓饥饿 |
| `ScrollOfChallenge.ChallengeArena` | 协作 | 挑战竞技场状态，暂停饥饿 |
| `VaultLevel` | 协作 | 保险库关卡，暂停饥饿 |
| `BuffIndicator` | 协作 | UI 指示器，显示饥饿状态图标 |

---

## 静态常量表

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `HUNGRY` | `float` | `300f` | **饥饿阈值**：饥饿值达到此值时进入"饥饿"状态，显示饥饿警告 |
| `STARVING` | `float` | `450f` | **饥饿致死阈值**：饥饿值达到此值时进入"饥饿致死"状态，开始受到伤害 |

**阈值说明：**
- 饥饿值从 0 开始累积
- `0 ≤ level < 300`：正常状态（无饥饿提示）
- `300 ≤ level < 450`：饥饿状态（显示饥饿图标，警告玩家）
- `level ≥ 450`：饥饿致死状态（持续受到伤害）

---

## 实例字段表

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `level` | `float` | 当前饥饿累积值，每回合递增，食物可减少此值 |
| `partialDamage` | `float` | **部分伤害累积器**：饥饿致死时伤害按 `HT/1000` 每回合累积，超过1时造成整数伤害 |

**Bundle 存储键：**

| 键名 | 说明 |
|------|------|
| `LEVEL` | 用于序列化 `level` 字段 |
| `PARTIALDAMAGE` | 用于序列化 `partialDamage` 字段 |

---

## 7. 方法详解

### `act()` - 核心行动逻辑

```java
@Override
public boolean act() {
    // 第66-73行：检查饥饿暂停条件
    if (Dungeon.level.locked
            || target.buff(WellFed.class) != null
            || SPDSettings.intro()
            || target.buff(ScrollOfChallenge.ChallengeArena.class) != null
            || Dungeon.level instanceof VaultLevel){
        spend(TICK);
        return true;
    }
    ...
}
```

**逐行解析：**

| 行号 | 代码片段 | 解释 |
|------|----------|------|
| 66-70 | `Dungeon.level.locked` | 关卡锁定时暂停饥饿（如特殊事件场景） |
| 67 | `target.buff(WellFed.class) != null` | 饱腹状态下暂停饥饿累积 |
| 68 | `SPDSettings.intro()` | 教学关卡中暂停饥饿 |
| 69 | `target.buff(ScrollOfChallenge.ChallengeArena.class) != null` | 挑战竞技场中暂停饥饿 |
| 70 | `Dungeon.level instanceof VaultLevel` | 保险库关卡中暂停饥饿 |
| 71-72 | `spend(TICK); return true;` | 仅消耗时间，不增加饥饿 |

```java
    // 第75-127行：正常饥饿处理逻辑
    if (target.isAlive() && target instanceof Hero) {

        Hero hero = (Hero)target;

        if (isStarving()) {
            // 第79-86行：饥饿致死伤害计算
            partialDamage += target.HT/1000f;

            if (partialDamage > 1){
                target.damage( (int)partialDamage, this);
                partialDamage -= (int)partialDamage;
            }
            
        } else {
            // 第88-116行：正常饥饿累积
            float hungerDelay = 1f;
            if (target.buff(Shadows.class) != null){
                hungerDelay *= 1.5f;  // 暗影状态下减缓50%
            }
            hungerDelay /= SaltCube.hungerGainMultiplier();  // 盐块饰品效果

            float newLevel = level + (1f/hungerDelay);
            if (newLevel >= STARVING) {
                // 第97-103行：进入饥饿致死状态
                GLog.n( Messages.get(this, "onstarving") );
                hero.damage( 1, this );
                hero.interrupt();
                newLevel = STARVING;

            } else if (newLevel >= HUNGRY && level < HUNGRY) {
                // 第105-111行：首次进入饥饿状态
                GLog.w( Messages.get(this, "onhungry") );

                if (!Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_FOOD)){
                    GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_FOOD);
                }

            }
            level = newLevel;

        }
        
        spend( TICK );

    } else {
        diactivate();  // 目标死亡或非英雄时停用
    }

    return true;
```

**饥饿累积公式：**
```
每回合饥饿增量 = 1.0 / hungerDelay
hungerDelay = 基础值(1.0) × 暗影修正(1.5) ÷ 盐块修正
```

**状态转换流程：**
```
正常状态 → (level >= 300) → 饥饿状态 → (level >= 450) → 饥饿致死状态
                ↓                              ↓
          显示饥饿警告                    每回合受到伤害
          引导玩家进食                    HT/1000 点伤害累积
```

---

### `satisfy(float energy)` / `affectHunger(float energy)` - 食物消耗

```java
// 第129-131行：简化接口
public void satisfy( float energy ) {
    affectHunger( energy, false );
}

// 第133-135行：无限制版本
public void affectHunger(float energy ){
    affectHunger( energy, false );
}

// 第137-168行：完整实现
public void affectHunger(float energy, boolean overrideLimits ) {

    // 第139-143行：负值能量（增加饥饿）且存在饱腹状态的特殊处理
    if (energy < 0 && target.buff(WellFed.class) != null){
        target.buff(WellFed.class).left += energy;
        BuffIndicator.refreshHero();
        return;
    }

    float oldLevel = level;

    // 第147-158行：计算新饥饿值
    level -= energy;  // 能量减少饥饿（正数减少，负数增加）
    if (level < 0 && !overrideLimits) {
        level = 0;  // 默认不允饥饿为负
    } else if (level > STARVING) {
        // 超过饥饿致死阈值时，将超出部分转换为即时伤害
        float excess = level - STARVING;
        level = STARVING;
        partialDamage += excess * (target.HT/1000f);
        if (partialDamage > 1f){
            target.damage( (int)partialDamage, this );
            partialDamage -= (int)partialDamage;
        }
    }

    // 第160-165行：状态变化通知
    if (oldLevel < HUNGRY && level >= HUNGRY){
        GLog.w( Messages.get(this, "onhungry") );
    } else if (oldLevel < STARVING && level >= STARVING){
        GLog.n( Messages.get(this, "onstarving") );
        target.damage( 1, this );
    }

    BuffIndicator.refreshHero();
}
```

**参数说明：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `energy` | `float` | 食物能量值，正数减少饥饿，负数增加饥饿 |
| `overrideLimits` | `boolean` | 是否允许饥饿值为负（过度进食） |

**特殊逻辑：**
- 当 `energy < 0`（增加饥饿）且角色有 `WellFed` 状态时，直接减少饱腹时间而非增加饥饿
- 当饥饿值超过 `STARVING` 时，超出部分立即转换为伤害

---

### `isStarving()` - 饥饿致死判定

```java
// 第170-172行
public boolean isStarving() {
    return level >= STARVING;
}
```

**用途**：快速判断角色是否处于饥饿致死状态。

---

### `hunger()` - 获取饥饿值

```java
// 第174-176行
public int hunger() {
    return (int)Math.ceil(level);
}
```

**返回**：向上取整的饥饿值整数表示，用于 UI 显示。

---

### `icon()` - UI 图标

```java
// 第178-187行
@Override
public int icon() {
    if (level < HUNGRY) {
        return BuffIndicator.NONE;      // 正常状态：无图标
    } else if (level < STARVING) {
        return BuffIndicator.HUNGER;    // 饥饿状态：饥饿图标
    } else {
        return BuffIndicator.STARVATION; // 饥饿致死：死亡图标
    }
}
```

**图标常量对应：**
- `BuffIndicator.NONE = 127` - 透明图标
- `BuffIndicator.HUNGER = 5` - 饥饿图标
- `BuffIndicator.STARVATION = 6` - 饥饿致死图标

---

### `name()` - 状态名称

```java
// 第189-196行
@Override
public String name() {
    if (level < STARVING) {
        return Messages.get(this, "hungry");     // "饥饿"
    } else {
        return Messages.get(this, "starving");   // "饥饿致死"
    }
}
```

---

### `desc()` - 状态描述

```java
// 第198-210行
@Override
public String desc() {
    String result;
    if (level < STARVING) {
        result = Messages.get(this, "desc_intro_hungry");
    } else {
        result = Messages.get(this, "desc_intro_starving");
    }

    result += Messages.get(this, "desc");

    return result;
}
```

---

### `onDeath()` - 饥饿致死回调

```java
// 第212-219行
@Override
public void onDeath() {
    Badges.validateDeathFromHunger();  // 验证饥饿死亡徽章
    Dungeon.fail( this );               // 记录死亡原因
    GLog.n( Messages.get(this, "ondeath") );  // 显示死亡消息
}
```

**Hero.Doom 接口**：当 `target.damage()` 导致角色死亡时，会调用此方法。

---

### 序列化方法

```java
// 第49-61行
@Override
public void storeInBundle( Bundle bundle ) {
    super.storeInBundle(bundle);
    bundle.put( LEVEL, level );
    bundle.put( PARTIALDAMAGE, partialDamage );
}

@Override
public void restoreFromBundle( Bundle bundle ) {
    super.restoreFromBundle( bundle );
    level = bundle.getFloat( LEVEL );
    partialDamage = bundle.getFloat(PARTIALDAMAGE);
}
```

**用途**：支持游戏存档时保存饥饿状态，读档时恢复。

---

## 11. 使用示例

### 1. 获取英雄的饥饿状态

```java
// 获取英雄的 Hunger buff
Hunger hunger = Dungeon.hero.buff(Hunger.class);

if (hunger != null) {
    // 检查是否饥饿致死
    if (hunger.isStarving()) {
        GLog.n("你正在挨饿！");
    }
    
    // 获取饥饿值
    int hungerValue = hunger.hunger();
}
```

### 2. 使用食物减少饥饿

```java
// 食物类通常这样调用
public void execute(Hero hero) {
    Hunger hunger = hero.buff(Hunger.class);
    if (hunger != null) {
        hunger.satisfy(100);  // 减少100点饥饿值
    }
}
```

### 3. 增加饥饿（如诅咒效果）

```java
Hunger hunger = hero.buff(Hunger.class);
if (hunger != null) {
    // 增加50点饥饿值（传入负数能量）
    hunger.affectHunger(-50, false);
}
```

### 4. 强制设置饥饿状态

```java
Hunger hunger = hero.buff(Hunger.class);
if (hunger != null) {
    // 直接设置饥饿值（允许负值）
    hunger.affectHunger(-hunger.hunger(), true);  // 清空饥饿
}
```

---

## 注意事项

### 1. 饥饿暂停条件

以下情况下饥饿不会累积：

| 条件 | 说明 |
|------|------|
| `Dungeon.level.locked` | 关卡锁定（Boss战、剧情等） |
| `WellFed` buff | 饱腹状态 |
| `SPDSettings.intro()` | 教学关卡 |
| `ChallengeArena` buff | 挑战卷轴竞技场内 |
| `VaultLevel` | 保险库关卡 |

### 2. 饥饿消耗修正

多个因素可以减缓饥饿累积：

| 修正来源 | 计算方式 | 效果 |
|----------|----------|------|
| 暗影潜行 (Shadows) | `hungerDelay *= 1.5f` | 饥饿累积速度降低 33% |
| 盐块饰品 (SaltCube) | `hungerDelay /= multiplier` | 饥饿累积速度降低 (1-1/multiplier)% |

**盐块修正值表：**

| 饰品等级 | hungerGainMultiplier | 饥饿速度降低 |
|----------|---------------------|-------------|
| 无 | 1.0 | 0% |
| +0 | 0.8 | 20% |
| +1 | 0.667 | 33% |
| +2 | 0.571 | 43% |
| +3 | 0.5 | 50% |

### 3. 饥饿致死伤害机制

```
每回合伤害 = HT / 1000
实际伤害 = floor(累积伤害)
```

**示例**：角色 HT = 100 时，每回合累积 0.1 点伤害，10 回合造成 1 点伤害。

### 4. WellFed 特殊处理

当角色处于饱腹状态时，负数能量（增加饥饿）会直接减少饱腹时间而非增加饥饿值：

```java
if (energy < 0 && target.buff(WellFed.class) != null){
    target.buff(WellFed.class).left += energy;
    return;  // 直接返回，不处理饥饿值
}
```

---

## 最佳实践

### 1. 创建新食物物品

```java
public class MyFood extends Food {
    {
        energy = 100f;  // 设置食物能量值
    }
}
```

### 2. 检测饥饿状态变化

```java
// 在需要监听饥饿状态的地方
Hunger hunger = hero.buff(Hunger.class);
float oldLevel = hunger.hunger();

// 执行某些操作...

if (hunger.hunger() >= Hunger.HUNGRY && oldLevel < Hunger.HUNGRY) {
    // 刚进入饥饿状态
    onEnterHungry();
}
```

### 3. 自定义饥饿修正效果

```java
// 类似 SaltCube 的实现
public static float customHungerMultiplier() {
    // 返回大于1的值会减缓饥饿累积
    return 1.5f;  // 饥饿累积速度降低33%
}

// 在 Hunger.act() 中添加检查
if (target.buff(CustomBuff.class) != null) {
    hungerDelay *= customHungerMultiplier();
}
```

### 4. 安全移除饥饿状态

```java
// 不推荐：直接移除可能导致游戏逻辑异常
Buff.detach(hero, Hunger.class);

// 推荐：通过食物正常减少饥饿
Hunger hunger = hero.buff(Hunger.class);
if (hunger != null) {
    hunger.affectHunger(Float.MAX_VALUE, false);  // 清空饥饿值
}
```

---

## 相关文件

| 文件 | 说明 |
|------|------|
| `Buff.java` | Buff 基类 |
| `Hero.java` | 英雄类，包含 Doom 接口 |
| `WellFed.java` | 饱腹状态 buff |
| `Shadows.java` | 暗影潜行 buff |
| `SaltCube.java` | 盐块饰品 |
| `BuffIndicator.java` | Buff 图标显示 |
| `messages.properties` | 国际化文本资源 |

---

## 版本历史

该类源自原版 Pixel Dungeon，Shattered Pixel Dungeon 进行了以下扩展：
- 添加了 `WellFed` 状态的特殊处理
- 添加了 `Shadows` 状态的饥饿减缓效果
- 添加了 `SaltCube` 饰品的饥饿减缓效果
- 添加了 `ChallengeArena` 状态的饥饿暂停
- 添加了 `VaultLevel` 的饥饿暂停
- 改进了教学关卡中的饥饿处理