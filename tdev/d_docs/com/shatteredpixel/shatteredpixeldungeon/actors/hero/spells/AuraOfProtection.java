# AuraOfProtection 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/AuraOfProtection.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends ClericSpell |
| **代码行数** | 113 |
| **中文名称** | 保护光环 |

---

## 法术概述

`AuraOfProtection`（保护光环）是牧师职业的3级法术。该法术的主要功能是：

1. **核心效果**：为英雄施加一个持续20回合的保护光环Buff，提供伤害减免和符文增强效果
2. **战术价值**：在面对强大敌人时提供生存能力，同时增强装备符文的效果
3. **使用场景**：Boss战、精英怪遭遇战或需要持久战斗的情况下

**法术类型**：
- **目标类型**：Self-Targeted（自我目标）
- **充能消耗**：2点充能
- **天赋需求**：AURA_OF_PROTECTION 天赋

---

## 类关系图

```mermaid
classDiagram
    class ClericSpell {
        <<abstract>>
        +void onCast(HolyTome, Hero)
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +String name()
        +String shortDesc()
        +String desc()
        +boolean usesTargeting()
        +int targetingFlags()
    }
    
    class AuraOfProtection {
        +INSTANCE: AuraOfProtection
        +int icon()
        +String desc()
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +void onCast(HolyTome, Hero)
    }
    
    class AuraBuff {
        +static float DURATION = 20f
        +Emitter particles
        +int icon()
        +void fx(boolean)
        +float iconFadePercent()
    }
    
    ClericSpell <|-- AuraOfProtection
    AuraOfProtection +-- AuraBuff
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | AuraOfProtection.INSTANCE | 单例实例 |
| DURATION | 20f | 光环持续时间（回合数） |

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
| `canCast()` | requires AURA_OF_PROTECTION | 施放此法术所需的天赋 |

### 效果数值

| 效果 | 基础值 | 天赋加成 | 最大值（3点天赋） |
|------|--------|----------|------------------|
| 伤害减免 | 10% | +10%每点天赋 | 40% |
| 符文威力 | 25% | +25%每点天赋 | 100% |

---

## 方法详解

### onCast(HolyTome tome, Hero hero)

```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    Buff.affect(hero, AuraBuff.class, AuraBuff.DURATION);
    Sample.INSTANCE.play(Assets.Sounds.READ);
    hero.spend(1f);
    hero.busy();
    hero.sprite.operate(hero.pos);
    onSpellCast(tome, hero);
}
```

**方法作用**：执行保护光环法术的主要逻辑。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄

**实现逻辑**：
1. 为英雄添加 `AuraBuff`，持续20回合
2. 播放施法音效（阅读音效）
3. 英雄花费1回合时间并进入忙碌状态
4. 触发英雄精灵的施法动画
5. 调用 `onSpellCast(tome, hero)` 完成施法后处理

---

### desc()

```java
@Override
public String desc() {
    int dmgReduction = 10 + 10*Dungeon.hero.pointsInTalent(Talent.AURA_OF_PROTECTION);
    int glyphPow = 25 + 25*Dungeon.hero.pointsInTalent(Talent.AURA_OF_PROTECTION);
    return Messages.get(this, "desc", dmgReduction, glyphPow) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本，包含动态计算的效果数值。

**返回值**：
- 包含伤害减免百分比、符文威力提升百分比和充能消耗的完整描述字符串

---

### chargeUse(Hero hero)

```java
@Override
public float chargeUse(Hero hero) {
    return 2f;
}
```

**方法作用**：返回施放保护光环所需的充能数量。

**参数**：
- `hero` (Hero)：施法的英雄

**返回值**：
- `2f`：固定消耗2点充能（不受其他天赋影响）

---

### canCast(Hero hero)

```java
@Override
public boolean canCast(Hero hero) {
    return super.canCast(hero) && hero.hasTalent(Talent.AURA_OF_PROTECTION);
}
```

**方法作用**：检查英雄是否可以施放保护光环法术。

**参数**：
- `hero` (Hero)：要检查的英雄

**返回值**：
- `true`：可以施放（拥有AURA_OF_PROTECTION天赋且未被魔法免疫）
- `false`：无法施放

---

## 内部类 AuraBuff

### 类定义

```java
public static class AuraBuff extends FlavourBuff {
    public static float DURATION = 20f;
    private Emitter particles;
    // ... 其他方法
}
```

**类作用**：实现保护光环的实际效果，包括伤害减免、符文增强和视觉特效。

**关键属性**：
- `DURATION`: 20f - Buff持续时间
- `particles`: Emitter - 粒子发射器，用于显示光效

**关键方法**：
- `fx(boolean on)`: 控制粒子特效的开关
- `iconFadePercent()`: 返回Buff图标淡出百分比，用于UI显示

### 效果机制

**伤害减免**: 
- 提供固定百分比的伤害减免
- 数值 = 10% + 10% × 天赋等级
- 与其他伤害减免效果叠加

**符文威力增强**:
- 提升装备上符文的效果强度
- 数值 = 25% + 25% × 天赋等级
- 影响所有符文类型（如防护符文、锋利符文等）

**视觉特效**:
- 在英雄周围显示发光粒子效果
- 特效大小为80×80像素，覆盖整个角色
- 根据Buff剩余时间逐渐淡出

---

## 使用示例

### 基本施法

```java
// 施放保护光环
if (hero.hasTalent(Talent.AURA_OF_PROTECTION)) {
    AuraOfProtection.INSTANCE.onCast(holyTome, hero);
}
```

### 在神圣典籍中使用

```java
// 通过神圣典籍施放
HolyTome tome = new HolyTome();
if (tome.canCast(hero, AuraOfProtection.INSTANCE)) {
    AuraOfProtection.INSTANCE.onCast(tome, hero);
}
```

### 检查Buff状态

```java
// 检查是否已有保护光环
AuraOfProtection.AuraBuff aura = hero.buff(AuraOfProtection.AuraBuff.class);
if (aura != null) {
    float remainingTime = aura.cooldown(); // 获取剩余持续时间
}
```

---

## 注意事项

### 平衡性考虑

1. **充能管理**：2点充能消耗较高，需要合理规划使用时机
2. **时机选择**：最佳使用时机是在预计受到大量伤害前
3. **配合天赋**：投资更多天赋点数可以显著提升效果

### 特殊机制

1. **伤害减免计算**：保护光环的伤害减免在其他减伤效果之后应用
2. **符文增强范围**：影响所有已附魔的装备，包括武器和护甲
3. **视觉特效性能**：粒子特效可能会对低端设备造成轻微性能影响

### 技术限制

1. **不可叠加**：重复施放会刷新持续时间，不会叠加效果
2. **持续时间固定**：20回合的持续时间不受其他因素影响
3. **Buff类型**：属于正面Buff（buffType.POSITIVE），不会被净化移除

---

## 最佳实践

### 战斗策略

- **进攻性使用**：在主动攻击强力敌人前施放，提高生存能力
- **防御性使用**：在被围攻或面对高伤害敌人时紧急施放
- **支援性使用**：虽然只能施放于自身，但可以配合其他治疗/支援法术形成完整防御体系

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.AURA_OF_PROTECTION) && 
    hero.pointsInTalent(Talent.AURA_OF_PROTECTION) >= 2 &&
    hero.hasTalent(Talent.HOLY_WEAPON)) {
    // 在高伤害减免下使用圣光武器进行输出
}
```

### 装备协同

- **符文装备**：优先给高价值装备附魔符文，最大化符文威力提升效果
- **防护装备**：配合高防御装备，形成双重保护
- **药水使用**：在光环持续期间使用治疗药水，提高整体生存能力

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `ClericSpell` | 父类 | 所有牧师法术的基类 |
| `FlavourBuff` | Buff父类 | 提供持续效果的基础Buff类 |
| `HolyTome` | 使用者 | 神圣典籍，法术载体 |
| `Hero` | 施法者/目标 | 英雄角色 |
| `Talent.AURA_OF_PROTECTION` | 依赖 | 必需的天赋 |
| `Barrier` | 协同效果 | 饱食法术天赋产生的屏障效果 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.auraofprotection.name` | "保护光环" | 法术名称 |
| `spells.auraofprotection.desc` | "围绕你形成一个保护光环，减少%d%%的伤害，并使你的符文效果提升%d%%。" | 法术描述 |
| `spells.auraofprotection.charge_cost` | "%d 充能" | 充能消耗提示 |