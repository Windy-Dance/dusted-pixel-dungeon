# BodyForm 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/BodyForm.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends ClericSpell |
| **代码行数** | 141 |
| **中文名称** | 肉身形态 |

---

## 法术概述

`BodyForm`（肉身形态）是牧师职业的4级法术，属于三位一体（Trinity）系统的一部分。该法术的主要功能是：

1. **核心效果**：激活肉身形态Buff，赋予英雄武器附魔或护甲符文的效果
2. **战术价值**：根据当前装备提供相应的战斗增益，增强物理输出或防御能力
3. **使用场景**：需要临时获得特定附魔/符文效果时，或配合三位一体系统的其他形态使用

**法术类型**：
- **目标类型**：Self-Targeted（自我目标）
- **充能消耗**：2点充能
- **天赋需求**：BODY_FORM 天赋

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
    
    class BodyForm {
        +INSTANCE: BodyForm
        +int icon()
        +String desc()
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +void onCast(HolyTome, Hero)
        +static int duration()
    }
    
    class BodyFormBuff {
        +Bundlable effect
        +Weapon.Enchantment enchant()
        +Armor.Glyph glyph()
        +int icon()
        +void tintIcon(Image)
        +float iconFadePercent()
        +String desc()
    }
    
    ClericSpell <|-- BodyForm
    BodyForm +-- BodyFormBuff
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | BodyForm.INSTANCE | 单例实例 |
| EFFECT | "effect" | Bundle存储键，用于保存效果类型 |

---

## 核心属性

### 充能消耗

| 属性 | 值 | 说明 |
|------|-----|------|
| `chargeUse()` | 2f | 每次施放消耗2点充能 |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | false | 无需目标选择，自动施放于自身 |
| `targetingFlags()` | -1 | 无目标选择标志 |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires BODY_FORM | 施放此法术所需的天赋 |

### 持续时间

| 天赋等级 | 持续时间（回合） | 计算公式 |
|----------|------------------|----------|
| 0点 | 13回合 | Math.round(13.33f) |
| 1点 | 20回合 | Math.round(20f) |
| 2点 | 27回合 | Math.round(26.67f) |
| 3点 | 33回合 | Math.round(33.33f) |

---

## 方法详解

### onCast(HolyTome tome, Hero hero)

```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    GameScene.show(new Trinity.WndItemtypeSelect(tome, this));
}
```

**方法作用**：打开三位一体物品类型选择窗口，而不是直接施放Buff。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄

**实现逻辑**：
1. 显示 `Trinity.WndItemtypeSelect` 窗口
2. 玩家在窗口中选择要应用的效果类型（武器附魔或护甲符文）
3. 选择完成后，实际的Buff由三位一体系统创建和管理

---

### duration()

```java
public static int duration() {
    return Math.round(13.33f + 6.67f* Dungeon.hero.pointsInTalent(Talent.BODY_FORM));
}
```

**方法作用**：计算肉身形态Buff的持续时间。

**返回值**：
- 基于天赋等级计算的持续时间（回合数），四舍五入取整

---

### desc()

```java
@Override
public String desc() {
    return Messages.get(this, "desc", duration()) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本，包含动态计算的持续时间。

**返回值**：
- 包含Buff持续时间和充能消耗的完整描述字符串

---

## 内部类 BodyFormBuff

### 类定义

```java
public static class BodyFormBuff extends FlavourBuff {
    private Bundlable effect;
    // ... 其他方法
}
```

**类作用**：实现肉身形态的实际效果，可以包含武器附魔或护甲符文效果。

**关键属性**：
- `effect`: Bundlable - 存储实际的效果（Weapon.Enchantment 或 Armor.Glyph）

**关键方法**：
- `enchant()`: 返回武器附魔效果（如果存在）
- `glyph()`: 返回护甲符文效果（如果存在）
- `tintIcon()`: 将Buff图标着色为红色（表示肉身形态）
- `desc()`: 返回包含具体效果的描述文本

### 效果机制

**武器附魔效果**：
- 如果选择武器附魔，Buff会应用相应的附魔效果
- 效果持续整个Buff持续时间
- 提供与装备相同附魔相似的战斗增益

**护甲符文效果**：
- 如果选择护甲符文，Buff会应用相应的符文效果  
- 效果持续整个Buff持续时间
- 提供与装备相同符文相似的防御增益

**视觉特效**：
- Buff图标显示为三位一体形态图标
- 图标着色为红色，表示肉身形态
- 图标根据剩余时间逐渐淡出

---

## 使用示例

### 基本施法

```java
// 施放肉身形态（会打开选择窗口）
if (hero.hasTalent(Talent.BODY_FORM)) {
    BodyForm.INSTANCE.onCast(holyTome, hero);
}
```

### 检查Buff状态

```java
// 检查是否处于肉身形态
BodyForm.BodyFormBuff bodyForm = hero.buff(BodyForm.BodyFormBuff.class);
if (bodyForm != null) {
    // 获取具体效果
    Weapon.Enchantment enchant = bodyForm.enchant();
    Armor.Glyph glyph = bodyForm.glyph();
    
    float remainingTime = bodyForm.cooldown(); // 获取剩余持续时间
}
```

### 三位一体协同

```java
// 与其他三位一体形态协同使用
// 注意：通常同一时间只能激活一种形态
```

---

## 注意事项

### 平衡性考虑

1. **高充能消耗**：2点充能消耗较高，需要谨慎使用
2. **持续时间**：基础持续时间较长，天赋加成显著
3. **效果依赖**：实际效果取决于玩家选择的附魔/符文类型

### 特殊机制

1. **选择界面**：不直接施放效果，而是打开选择窗口
2. **三位一体系统**：与MindForm、SpiritForm共同构成三位一体系统
3. **效果持久性**：选择的效果在整个Buff持续时间内有效

### 技术限制

1. **互斥性**：通常不能同时激活多个三位一体形态
2. **效果类型限制**：只能选择武器附魔或护甲符文，不能自定义效果
3. **序列化支持**：效果通过Bundle系统正确保存和加载

---

## 最佳实践

### 战斗策略

- **进攻性使用**：选择高伤害附魔（如锋利、毒药）进行输出
- **防御性使用**：选择防护符文（如防护、反弹）提高生存能力
- **情境适应**：根据当前战斗情况选择最合适的效果类型

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.BODY_FORM) && 
    hero.pointsInTalent(Talent.BODY_FORM) >= 2 &&
    hero.hasTalent(Talent.MIND_FORM)) {
    // 在肉身形态和心灵形态之间灵活切换，适应不同战斗需求
}
```

### 装备协同

- **附魔装备**：了解当前拥有的武器附魔，选择最有效的效果
- **符文装备**：了解当前拥有的护甲符文，选择最合适的防御效果
- **三位一体循环**：合理规划三种形态的使用顺序和时机

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `ClericSpell` | 父类 | 所有牧师法术的基类 |
| `Trinity` | 依赖 | 三位一体系统，提供选择窗口 |
| `Weapon.Enchantment` | 效果类型 | 武器附魔效果 |
| `Armor.Glyph` | 效果类型 | 护甲符文效果 |
| `MindForm` | 协同 | 心灵形态，三位一体的另一部分 |
| `SpiritForm` | 协同 | 灵魂形态，三位一体的第三部分 |
| `FlavourBuff` | Buff父类 | 提供持续效果的基础Buff类 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.bodyform.name` | "肉身形态" | 法术名称 |
| `spells.bodyform.desc` | "进入肉身形态，持续%d回合。在此形态下，你可以获得武器附魔或护甲符文的效果。" | 法术描述 |
| `spells.bodyform.charge_cost` | "%d 充能" | 充能消耗提示 |
| `buffs.bodyformbuff.desc` | "%s效果，剩余%d回合。" | Buff描述（包含具体效果名称） |