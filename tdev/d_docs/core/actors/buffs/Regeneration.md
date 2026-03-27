# Regeneration.java 生命恢复状态文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Regeneration.java` |
| **类名** | `Regeneration` |
| **继承关系** | `extends Buff` |
| **代码行数** | 137 行 |
| **功能概述** | 管理英雄角色的生命值自动恢复，处理恢复速率计算、各种物品效果修正和恢复暂停条件 |

---

## 类职责

`Regeneration` 类是 Shattered Pixel Dungeon 中核心的生命恢复机制实现，负责：

1. **生命值自动恢复** - 每隔固定回合自动恢复英雄生命值
2. **恢复速率计算** - 根据血杯、能量戒指、盐块等物品修正恢复速度
3. **恢复暂停控制** - 在特定条件下暂停恢复（保险库关卡、关卡锁定等）
4. **部分恢复累积** - 处理小于1HP的恢复累积，避免恢复损失
5. **香炉追踪器管理** - 维护混沌香炉的气体释放计时

---

## 4. 继承与协作关系

```
                    ┌─────────────────────────────────────┐
                    │              Actor                   │
                    │         (抽象游戏实体基类)             │
                    └──────────────┬──────────────────────┘
                                   │ extends
                    ┌──────────────▼──────────────────────┐
                    │              Buff                     │
                    │          (增益/减益基类)               │
                    └──────────────┬──────────────────────┘
                                   │ extends
                    ┌──────────────▼──────────────────────┐
                    │           Regeneration               │
                    │          (生命恢复状态)               │
                    └──────────────┬──────────────────────┘
                                   │
         ┌─────────────────────────┼─────────────────────────┐
         │                         │                         │
         ▼                         ▼                         ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│ ChaliceOfBlood  │     │   RingOfEnergy  │     │    SaltCube     │
│   (血杯神器)     │     │   (能量戒指)     │     │   (盐块饰品)     │
│ 加速生命恢复     │     │ 加速神器充能     │     │ 加速生命恢复     │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

**关键交互类：**

| 类名 | 关系类型 | 说明 |
|------|----------|------|
| `Buff` | 继承 | 基类，提供 Buff 框架功能 |
| `Hero` | 协作 | 恢复效果的目标角色 |
| `ChaliceOfBlood` | 协作 | 血杯神器，通过 `chaliceRegen` buff 提供恢复加成 |
| `SpiritForm` | 协作 | 灵体形态，可能携带血杯效果 |
| `RingOfEnergy` | 协作 | 能量戒指，影响神器充能速度从而间接影响恢复 |
| `SaltCube` | 协作 | 盐块饰品，通过 `healthRegenMultiplier()` 加速恢复 |
| `ChaoticCenser` | 协作 | 混沌香炉饰品，需要追踪气体释放 |
| `LockedFloor` | 协作 | 关卡锁定状态，控制恢复是否启用 |
| `VaultLevel` | 协作 | 保险库关卡，禁用恢复 |
| `MagicImmune` | 协作 | 魔法免疫状态，禁用神器效果 |

---

## 静态常量表

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `REGENERATION_DELAY` | `float` | `10f` | **基础恢复间隔**：每10回合恢复1点生命值 |
| `PARTIAL_REGEN` | `String` | `"partial_regen"` | **序列化键**：用于保存部分恢复累积值 |

**恢复间隔说明：**
- 基础恢复速率：每 10 回合恢复 1 HP
- 实际恢复速率受多种因素影响：血杯等级、能量戒指、盐块饰品等

---

## 实例字段表

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `partialRegen` | `float` | `0f` | **部分恢复累积器**：累积小于1HP的恢复量，达到1时恢复整数HP |
| `actPriority` | `int` | `HERO_PRIO - 1` | **行动优先级**：设置为英雄优先级-1，在英雄行动后立即执行 |

**行动优先级说明：**

```java
{
    // 与其他buff不同，此buff在英雄之后行动，并优先于其他效果
    // 在受到伤害前获得一些恢复更有价值
    actPriority = HERO_PRIO - 1;
}
```

这种设计确保恢复发生在英雄回合之后、敌人行动之前，让玩家能最大化利用恢复效果。

---

## 7. 方法详解

### `act()` - 核心行动逻辑

```java
@Override
public boolean act() {
    if (target.isAlive()) {
        // ... 恢复逻辑
        spend( TICK );
    } else {
        diactivate();  // 目标死亡时停用
    }
    return true;
}
```

**逐行解析：**

#### 第 48-54 行：香炉追踪器管理

```java
if (ChaoticCenser.averageTurnsUntilGas() != -1){
    Buff.affect(Dungeon.hero, ChaoticCenser.CenserGasTracker.class);
}
```

| 代码片段 | 解释 |
|----------|------|
| `ChaoticCenser.averageTurnsUntilGas() != -1` | 检查混沌香炉是否装备（-1表示未装备） |
| `Buff.affect(...)` | 确保英雄拥有香炉气体追踪器 |

**说明**：这是一个临时性设计，作者注释表明如果其他饰品也需要类似buff，应该重构为更通用的系统。

#### 第 56-67 行：恢复条件检查与血杯状态获取

```java
if (regenOn() && target.HP < regencap() && !((Hero)target).isStarving()) {
    boolean chaliceCursed = false;
    int chaliceLevel = -1;
    if (target.buff(MagicImmune.class) == null) {
        if (Dungeon.hero.buff(ChaliceOfBlood.chaliceRegen.class) != null) {
            chaliceCursed = Dungeon.hero.buff(ChaliceOfBlood.chaliceRegen.class).isCursed();
            chaliceLevel = Dungeon.hero.buff(ChaliceOfBlood.chaliceRegen.class).itemLevel();
        } else if (Dungeon.hero.buff(SpiritForm.SpiritFormBuff.class) != null
                && Dungeon.hero.buff(SpiritForm.SpiritFormBuff.class).artifact() instanceof ChaliceOfBlood) {
            chaliceLevel = SpiritForm.artifactLevel();
        }
    }
    // ...
}
```

| 条件 | 解释 |
|------|------|
| `regenOn()` | 恢复是否启用（检查锁定和保险库） |
| `target.HP < regencap()` | 当前HP小于恢复上限 |
| `!((Hero)target).isStarving()` | 角色未处于饥饿致死状态 |
| `target.buff(MagicImmune.class) == null` | 无魔法免疫（神器效果被禁用时跳过） |

**血杯状态获取逻辑：**

| 情况 | chaliceCursed | chaliceLevel |
|------|---------------|--------------|
| 无血杯 | false | -1 |
| 血杯（诅咒） | true | 装备等级 |
| 血杯（正常） | false | 装备等级 |
| 灵体形态携带血杯 | false | SpiritForm.artifactLevel() |

#### 第 69-78 行：恢复延迟计算

```java
float delay = REGENERATION_DELAY;  // 基础值: 10
if (chaliceLevel != -1 && target.buff(MagicImmune.class) == null) {
    if (chaliceCursed) {
        delay *= 1.5f;  // 诅咒：恢复速度降低33%
    } else {
        // +0等级: 15%加速, +10等级: 500%加速
        delay -= 1.33f + chaliceLevel*0.667f;
        delay /= RingOfEnergy.artifactChargeMultiplier(target);
    }
}
```

**血杯恢复加速公式：**

```
延迟减少 = 1.33 + 等级 × 0.667
实际延迟 = (基础延迟 - 延迟减少) ÷ 能量戒指倍率
```

| 血杯等级 | 延迟减少 | 基础延迟 | 恢复间隔（无戒指） |
|----------|----------|----------|-------------------|
| -1（无） | 0 | 10.0 | 10 回合/HP |
| +0 | 1.33 | 8.67 | ~8.7 回合/HP |
| +1 | 2.00 | 8.00 | 8 回合/HP |
| +2 | 2.67 | 7.33 | ~7.3 回合/HP |
| +3 | 3.33 | 6.67 | ~6.7 回合/HP |
| +5 | 4.67 | 5.33 | ~5.3 回合/HP |
| +10 | 8.00 | 2.00 | 2 回合/HP |

**诅咒效果：**
- 延迟 × 1.5 = 恢复速度降低 33%

#### 第 80-83 行：盐块加速

```java
// 盐块在恢复禁用时关闭
if (target.buff(LockedFloor.class) == null) {
    delay /= SaltCube.healthRegenMultiplier();
}
```

| 饰品等级 | healthRegenMultiplier | 恢复速度提升 |
|----------|----------------------|--------------|
| 无 | 1.0 | 0% |
| +0 | 1.5 | 50% |
| +1 | 2.0 | 100% |
| +2 | 2.5 | 150% |
| +3 | 3.0 | 200% |

**注意**：盐块效果在 `LockedFloor` 存在时不生效。

#### 第 85-94 行：部分恢复累积与HP恢复

```java
partialRegen += 1f / delay;

if (partialRegen >= 1) {
    target.HP += (int)partialRegen;
    partialRegen -= (int)partialRegen;
    if (target.HP >= regencap()) {
        target.HP = regencap();
        ((Hero) target).resting = false;  // 恢复满血时停止休息
    }
}
```

**恢复流程：**
1. 每回合累积 `1/delay` 点恢复值
2. 当累积值 ≥ 1 时，恢复整数HP
3. 恢复到上限时停止休息状态

**示例计算：**
- 基础情况（delay=10）：每回合累积0.1，10回合恢复1HP
- 血杯+10（delay=2）：每回合累积0.5，2回合恢复1HP
- 盐块+3（delay÷3）：恢复速度3倍

#### 第 98-104 行：时间消耗与停用处理

```java
spend( TICK );  // 消耗1回合时间

} else {
    diactivate();  // 目标死亡时停用buff
}
```

---

### `regencap()` - 恢复上限

```java
public int regencap(){
    return target.HT;
}
```

**用途**：返回恢复上限值，默认为角色的最大生命值（HT）。

**设计目的**：允许子类重写以实现特殊恢复上限（如某些职业可能恢复上限不同）。

---

### `regenOn()` - 恢复启用检查（静态方法）

```java
public static boolean regenOn(){
    LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
    if (lock != null && !lock.regenOn()){
        return false;
    }
    if (Dungeon.level instanceof VaultLevel){
        return false;
    }
    return true;
}
```

**恢复禁用条件：**

| 条件 | 说明 |
|------|------|
| `LockedFloor` 且 `!lock.regenOn()` | 关卡锁定且锁定状态禁用恢复 |
| `VaultLevel` | 在保险库关卡中 |

**LockedFloor 恢复控制：**
- 某些锁定场景允许恢复（如Boss战开始前）
- 某些锁定场景禁用恢复（如特殊挑战）

---

### 序列化方法

```java
public static final String PARTIAL_REGEN = "partial_regen";

@Override
public void storeInBundle(Bundle bundle) {
    super.storeInBundle(bundle);
    bundle.put(PARTIAL_REGEN, partialRegen);
}

@Override
public void restoreFromBundle(Bundle bundle) {
    super.restoreFromBundle(bundle);
    partialRegen = bundle.getFloat(PARTIAL_REGEN);
}
```

**用途**：保存/恢复部分恢复累积值，确保存档读档后恢复进度不丢失。

---

## 恢复速率计算公式

### 完整公式

```
最终延迟 = 基础延迟 × 诅咒倍率 - 血杯加速 ÷ 能量戒指倍率 ÷ 盐块倍率
恢复速率 = 1 ÷ 最终延迟 (HP/回合)
```

### 计算示例

**场景1：无任何加成**
```
delay = 10
恢复速率 = 1/10 = 0.1 HP/回合
每10回合恢复1HP
```

**场景2：血杯+5**
```
delay = 10 - (1.33 + 5×0.667) = 10 - 4.67 = 5.33
恢复速率 = 1/5.33 ≈ 0.19 HP/回合
每约5.3回合恢复1HP
```

**场景3：血杯+5 + 盐块+2**
```
delay = 5.33 / 2.5 = 2.13
恢复速率 = 1/2.13 ≈ 0.47 HP/回合
每约2.1回合恢复1HP
```

**场景4：血杯+10 + 能量戒指（神器充能1.5倍）+ 盐块+3**
```
血杯延迟 = 10 - (1.33 + 10×0.667) = 10 - 8 = 2
能量戒指调整 = 2 / 1.5 = 1.33
盐块调整 = 1.33 / 3 = 0.44
恢复速率 = 1/0.44 ≈ 2.25 HP/回合
每约0.44回合恢复1HP（约2回合恢复5HP）
```

**场景5：诅咒血杯**
```
delay = 10 × 1.5 = 15
恢复速率 = 1/15 ≈ 0.067 HP/回合
每15回合恢复1HP（恢复速度降低33%）
```

---

## 11. 使用示例

### 1. 检查恢复是否启用

```java
if (Regeneration.regenOn()) {
    // 恢复已启用
    GLog.i("生命正在恢复中...");
} else {
    // 恢复被禁用
    GLog.w("此地无法恢复生命！");
}
```

### 2. 获取恢复状态

```java
Regeneration regen = Dungeon.hero.buff(Regeneration.class);
if (regen != null) {
    // 获取部分恢复进度
    float progress = regen.partialRegen;
    // progress 在 0.0 ~ 0.99 之间
}
```

### 3. 自定义恢复上限（子类示例）

```java
public class SpecialRegen extends Regeneration {
    @Override
    public int regencap() {
        // 只恢复到50%生命值
        return target.HT / 2;
    }
}
```

### 4. 手动触发恢复

```java
// 通过消耗时间触发恢复检查
Regeneration regen = Dungeon.hero.buff(Regeneration.class);
if (regen != null && Regeneration.regenOn()) {
    // 强制执行一次恢复逻辑
    regen.act();
}
```

---

## 注意事项

### 1. 行动优先级

```java
actPriority = HERO_PRIO - 1;
```

这意味着 Regeneration 在以下时机执行：
1. 英雄行动完成
2. **Regeneration 执行**（恢复HP）
3. 其他 buff 行动
4. 敌人行动

**设计原因**：让玩家在敌人攻击前获得恢复，最大化恢复的战斗价值。

### 2. 饥饿致死状态

```java
!((Hero)target).isStarving()
```

饥饿致死时恢复完全停止。这是一个重要的游戏平衡机制：
- 玩家必须管理饥饿状态
- 饥饿致死是真实威胁

### 3. 魔法免疫与神器

```java
if (target.buff(MagicImmune.class) == null) {
    // 检查血杯效果
}
```

魔法免疫会禁用血杯的恢复加成，但不会完全停止基础恢复。

### 4. 盐块与锁定状态

```java
if (target.buff(LockedFloor.class) == null) {
    delay /= SaltCube.healthRegenMultiplier();
}
```

盐块效果在 LockedFloor 存在时不生效，但基础恢复和血杯效果仍可工作（如果 `regenOn()` 返回 true）。

### 5. 香炉追踪器的临时实现

```java
// 如果其他饰品获得类似buff，应该使buff附加行为更像法杖/戒指/神器
if (ChaoticCenser.averageTurnsUntilGas() != -1){
    Buff.affect(Dungeon.hero, ChaoticCenser.CenserGasTracker.class);
}
```

这是一个需要重构的代码区域，作者已标注。

---

## 最佳实践

### 1. 检查恢复条件

```java
// 推荐：检查所有条件
if (Regeneration.regenOn() 
    && hero.HP < hero.HT 
    && !hero.isStarving()) {
    // 恢复条件满足
}
```

### 2. 理解恢复累积

```java
// 恢复是累积的，不是即时整数
// 例如 delay=10 时：
// 回合1: partialRegen = 0.1
// 回合2: partialRegen = 0.2
// ...
// 回合10: partialRegen = 1.0 → 恢复1HP, partialRegen = 0.0
```

### 3. 创建自定义恢复效果

```java
// 类似盐块的恢复加速器
public class CustomRegenBooster extends Buff {
    public static float regenMultiplier() {
        return 2.0f;  // 2倍恢复速度
    }
}

// 在 Regeneration.act() 中添加检查：
if (target.buff(CustomRegenBooster.class) != null) {
    delay /= CustomRegenBooster.regenMultiplier();
}
```

### 4. 响应恢复完成

```java
// 检测恢复满血
if (target.HP >= regencap()) {
    // 触发恢复完成事件
    // 注意：Regeneration 会自动设置 resting = false
}
```

### 5. 保存/加载兼容性

```java
// 自定义子类需要保存额外字段
public class SpecialRegen extends Regeneration {
    private float customField;
    
    private static final String CUSTOM = "custom";
    
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CUSTOM, customField);
    }
    
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        customField = bundle.getFloat(CUSTOM);
    }
}
```

---

## 相关文件

| 文件 | 说明 |
|------|------|
| `Buff.java` | Buff 基类 |
| `Hero.java` | 英雄类，提供 `isStarving()` 方法 |
| `ChaliceOfBlood.java` | 血杯神器，提供 `chaliceRegen` buff |
| `RingOfEnergy.java` | 能量戒指，影响神器充能 |
| `SaltCube.java` | 盐块饰品，提供恢复加速 |
| `ChaoticCenser.java` | 混沌香炉饰品，需要气体追踪 |
| `SpiritForm.java` | 灵体形态，可能携带血杯 |
| `LockedFloor.java` | 关卡锁定状态 |
| `VaultLevel.java` | 保险库关卡 |
| `MagicImmune.java` | 魔法免疫状态 |

---

## 版本历史

该类源自原版 Pixel Dungeon，Shattered Pixel Dungeon 进行了以下扩展：

1. **血杯神器集成**：添加了血杯等级对恢复速度的影响
2. **能量戒指协同**：能量戒指的神器充能加速间接提升恢复速度
3. **盐块饰品支持**：添加了盐块的恢复加速效果
4. **混沌香炉追踪**：添加了香炉气体追踪器管理
5. **灵体形态支持**：支持灵体形态携带血杯的效果
6. **魔法免疫处理**：魔法免疫时禁用神器恢复效果
7. **行动优先级调整**：设置为英雄后行动，优先于其他效果