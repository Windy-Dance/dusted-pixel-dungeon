# RecallInscription 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/RecallInscription.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends ClericSpell |
| **代码行数** | 176 |
| **中文名称** | 回忆铭文 |

---

## 法术概述

`RecallInscription`（回忆铭文）是牧师职业的3级法术。该法术的主要功能是：

1. **核心效果**：召回最近使用过的卷轴或符石，重新获得相同物品的效果
2. **战术价值**：提供关键消耗品的二次使用机会，增强资源利用效率
3. **使用场景**：关键时刻需要重复使用强力卷轴、符石效果时，或弥补误用珍贵物品的错误

**法术类型**：
- **目标类型**：Self-Targeted（自我目标）
- **充能消耗**：动态消耗（2-8点充能，基于物品类型）
- **天赋需求**：RECALL_INSCRIPTION 天赋 + UsedItemTracker Buff

---

## 类关系图

```mermaid
classDiagram
    class ClericSpell {
        <<abstract>>
        +void onCast(HolyTome, Hero)
        +float chargeUse(Hero)
        +boolean canCast(Hero)
    }
    
    class RecallInscription {
        +INSTANCE: RecallInscription
        +int icon()
        +String desc()
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +void onCast(HolyTome, Hero)
    }
    
    class UsedItemTracker {
        +Class<?extends Item> item
        +type = buffType.POSITIVE
        +int icon()
        +float iconFadePercent()
        +String desc()
    }
    
    ClericSpell <|-- RecallInscription
    RecallInscription +-- UsedItemTracker
```

---

## 静态常量表

| 常量 | 値 | 说明 |
|------|-----|------|
| INSTANCE | RecallInscription.INSTANCE | 单例实例 |
| ITEM | "item" | Bundle存储键，用于保存物品类型 |

---

## 核心属性

### 充能消耗

| 物品类型 | 基础消耗 | 天赋2+消耗 | 说明 |
|----------|----------|------------|------|
| 普通符石 | 2点 | 2点 | 基础符石类型 |
| 高级符石 | 4点 | 4点 | 强化/附魔符石 |
| 普通卷轴 | 3点 | 3点 | 基础卷轴类型 |
| 高级卷轴 | 6点 | 6点 | 变身卷轴 |
| 异域卷轴 | 4点 | 4点 | 基础异域卷轴 |
| 高级异域卷轴 | 8点 | 8点 | 变形/附魔异域卷轴 |

### 持续时间

| 天赋等级 | 持续时间 | 说明 |
|----------|----------|------|
| 1点 | 10回合 | 基础持续时间 |
| 2-3点 | 300回合 | 超长持续时间 |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires RECALL_INSCRIPTION + UsedItemTracker | 需要天赋和物品追踪Buff |

---

## 方法详解

### onCast(HolyTome tome, Hero hero)

```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    // 召回并重新使用最近使用的物品
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：执行回忆铭文的主要逻辑，重新创建并激活最近使用的物品。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄

**实现逻辑**：
1. **Buff检查**：验证UsedItemTracker Buff是否存在
2. **物品重建**：使用Reflection.newInstance()重建原始物品
3. **视觉特效**：播放物品附魔特效
4. **物品激活**：
   - 卷轴：调用doRead()方法（禁用天赋触发）
   - 库存符石：直接激活（directActivate）
   - 投掷符石：在渲染线程中延时投掷
5. **Buff清理**：移除UsedItemTracker Buff防止重复使用

---

### chargeUse(Hero hero)

```java
@Override
public float chargeUse(Hero hero) {
    // 根据物品类型返回动态充能消耗
    // ...
    return 0;
}
```

**方法作用**：根据要召回的物品类型返回相应的充能消耗。

**返回值**：
- 基于物品类型的动态消耗（2-8点充能）
- 如果没有可用物品，返回0（但canCast会阻止此情况）

---

### canCast(Hero hero)

```java
@Override
public boolean canCast(Hero hero) {
    return super.canCast(hero)
            && hero.hasTalent(Talent.RECALL_INSCRIPTION)
            && hero.buff(UsedItemTracker.class) != null;
}
```

**方法作用**：检查英雄是否可以施放回忆铭文法术。

**返回值**：
- `true`：拥有天赋且存在UsedItemTracker Buff
- `false`：缺少天赋或没有可召回的物品

---

## 内部类 UsedItemTracker

### 类定义

```java
public static class UsedItemTracker extends FlavourBuff {
    public Class<?extends Item> item;
    {
        type = buffType.POSITIVE;
    }
    // ... 其他方法
}
```

**类作用**：跟踪最近使用的物品类型，为回忆铭文提供召回目标。

**关键属性**：
- `item`: Class<?extends Item> - 存储物品的类类型
- `type`: buffType.POSITIVE - 正面Buff类型

**关键方法**：
- `icon()`: 返回GLYPH_RECALL图标
- `iconFadePercent()`: 基于天赋等级显示不同的持续时间
- `desc()`: 显示要召回的物品名称和剩余时间
- `storeInBundle()/restoreFromBundle()`: 序列化支持

### 持续时间机制

- **基础持续时间**：10回合（1点天赋）
- **延长持续时间**：300回合（2-3点天赋）
- **视觉反馈**：Buff图标根据剩余时间淡出

---

## 特殊机制

### 物品类型处理

- **卷轴处理**：
  - 禁用anonymize()防止识别
  - 设置talentChance=0避免天赋触发
  - 直接调用doRead()激活效果
- **符石处理**：
  - 库存符石：立即激活（如鉴定符石）
  - 投掷符石：延时投掷确保正确时机
- **异域物品**：特殊处理高级异域卷轴的高消耗

### 天赋差异化

- **1点天赋**：短时间窗口（10回合），适合快速连续使用
- **2-3点天赋**：超长时间窗口（300回合），提供极大的使用灵活性

### 安全机制

- **Buff依赖**：必须存在UsedItemTracker才能施放
- **一次性使用**：使用后自动移除Buff，防止无限循环
- **类型安全**：使用Reflection.newInstance确保正确重建物品

---

## 使用示例

### 基本施法

```java
// 施放回忆铭文（需要已使用的物品）
if (hero.hasTalent(Talent.RECALL_INSCRIPTION) && 
    hero.buff(RecallInscription.UsedItemTracker.class) != null) {
    RecallInscription.INSTANCE.onCast(holyTome, hero);
}
```

### Buff状态检查

```java
// 检查是否有可召回的物品
RecallInscription.UsedItemTracker tracker = 
    hero.buff(RecallInscription.UsedItemTracker.class);
if (tracker != null) {
    Class<? extends Item> itemType = tracker.item;
    float remainingTime = tracker.cooldown();
    // 根据物品类型和剩余时间决定是否使用
}
```

### 消耗预计算

```java
// 预先计算回忆特定物品的消耗
float recallCost = RecallInscription.INSTANCE.chargeUse(hero);
if (hero.tome.charge >= recallCost) {
    // 充能足够，可以安全施放
}
```

---

## 注意事项

### 平衡性考虑

1. **动态消耗**：高价值物品消耗更多充能，保持平衡
2. **时间窗口**：天赋投资显著影响使用灵活性
3. **一次性限制**：每个物品只能被召回一次，防止滥用

### 特殊机制

1. **天赋抑制**：召回的卷轴不会触发天赋效果，避免连锁反应
2. **线程安全**：符石投掷使用渲染线程确保正确时序
3. **序列化支持**：物品类型正确保存和加载，跨楼层有效

### 技术限制

1. **反射依赖**：使用Reflection.newInstance重建物品
2. **类型检查**：需要精确的物品类型层次结构
3. **Buff管理**：UsedItemTracker的生命周期需要精确控制

---

## 最佳实践

### 战斗策略

- **关键时刻**：在Boss战或精英战中召回强力卷轴
- **资源优化**：弥补误用珍贵物品的损失
- **连招配合**：与高消耗卷轴形成"使用-召回"循环

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.RECALL_INSCRIPTION) && 
    hero.pointsInTalent(Talent.RECALL_INSCRIPTION) >= 2) {
    // 300回合超长窗口，极大提升战术灵活性
    // 可以安全地使用高价值物品
}
```

### 物品选择

- **高价值优先**：优先召回变身卷轴、附魔卷轴等高消耗物品
- **时机把握**：在安全环境下使用，避免浪费召回机会
- **充能管理**：确保有足够的充能支持高消耗召回

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `ClericSpell` | 父类 | 所有牧师法术的基类 |
| `FlavourBuff` | Buff父类 | 提供持续效果的基础Buff类 |
| `Reflection.newInstance()` | 依赖 | 物品重建机制 |
| `Scroll.doRead()` | 依赖 | 卷轴激活系统 |
| `Runestone.doThrow()` | 依赖 | 符石投掷系统 |
| `ExoticScroll` | 依赖 | 异域物品类型检测 |
| `InventoryStone` | 依赖 | 库存符石类型检测 |

---

## 消息键

| 键名 | 値 | 用途 |
|------|-----|------|
| `spells.recallinscription.name` | "回忆铭文" | 法术名称 |
| `spells.recallinscription.desc` | "召回你最近使用的物品，持续%d回合。可以召回卷轴或符石，消耗相应充能。" | 法术描述 |
| `spells.recallinscription.charge_cost` | "%d 充能" | 充能消耗提示 |
| `buffs.useditemtracker.desc` | "将召回%s，剩余%d回合。" | Buff描述 |