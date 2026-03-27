# LifeLinkSpell 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/LifeLinkSpell.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends ClericSpell |
| **代码行数** | 121 |
| **中文名称** | 生命链接 |

---

## 法术概述

`LifeLinkSpell`（生命链接）是牧师职业的3级法术。该法术的主要功能是：

1. **核心效果**：在英雄和盟友之间建立生命链接，实现生命值共享和同步保护
2. **战术价值**：提供团队生存保障，在英雄或盟友受到伤害时分担伤害
3. **使用场景**：高风险战斗、Boss战、或任何需要确保关键单位存活的情况

**法术类型**：
- **目标类型**：Self-Targeted（自我目标，自动链接到可用盟友）
- **充能消耗**：2点充能
- **天赋需求**：LIFE_LINK 天赋 + PowerOfMany 或 Stasis 盟友

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
    
    class LifeLinkSpell {
        +INSTANCE: LifeLinkSpell
        +int icon()
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +void onCast(HolyTome, Hero)
        +String desc()
    }
    
    class LifeLinkSpellBuff {
        +type = buffType.POSITIVE
        +int icon()
        +float iconFadePercent()
    }
    
    ClericSpell <|-- LifeLinkSpell
    LifeLinkSpell +-- LifeLinkSpellBuff
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | LifeLinkSpell.INSTANCE | 单例实例 |

---

## 核心属性

### 充能消耗

| 属性 | 值 | 说明 |
|------|-----|------|
| `chargeUse()` | 2f | 每次施放消耗2点充能 |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | false | 无需目标选择，自动链接到可用盟友 |
| `targetingFlags()` | -1 | 无目标选择标志 |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires LIFE_LINK + (PowerOfMany 或 Stasis 盟友) | 施放此法术的条件 |

### 效果数值

| 天赋等级 | 链接持续时间 | 伤害分担比例 | 说明 |
|----------|--------------|--------------|------|
| 1点 | 10回合 | 标准分担 | 基础生命链接 |
| 2点 | 13回合 | 标准分担 | 延长持续时间 |
| 3点 | 17回合 | 标准分担 | 最长持续时间 |

**持续时间计算**: Math.round(6.67f + 3.33f * talent_level)

---

## 方法详解

### onCast(HolyTome tome, Hero hero)

```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    // 建立英雄与盟友之间的生命链接
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：执行生命链接的主要逻辑，在英雄和盟友之间建立双向生命链接。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄

**实现逻辑**：
1. **盟友检测**：
   - 优先检查PowerOfMany盟友
   - 如果不存在，检查Stasis盟友
2. **视觉特效**：
   - 对PowerOfMany盟友：显示从英雄到盟友的HealthRay光束
   - 对Stasis盟友：显示环绕英雄的HealthRay光束
3. **Buff应用**：
   - 为英雄施加LifeLink Buff，链接到盟友ID
   - 为盟友施加LifeLink Buff，链接到英雄ID
   - 为盟友施加LifeLinkSpellBuff（持续时间指示器）
4. **特殊处理**：
   - 对Stasis盟友：清除Buff的时间限制（clearTime()）
5. **完成施法**：花费1回合时间并调用onSpellCast

---

### canCast(Hero hero)

```java
@Override
public boolean canCast(Hero hero) {
    return super.canCast(hero)
            && hero.hasTalent(Talent.LIFE_LINK)
            && (PowerOfMany.getPoweredAlly() != null || Stasis.getStasisAlly() != null);
}
```

**方法作用**：检查英雄是否可以施放生命链接法术。

**返回值**：
- `true`：拥有天赋且存在可用盟友（PowerOfMany或Stasis）
- `false`：缺少天赋或没有可用盟友

---

### desc()

```java
@Override
public String desc() {
    return Messages.get(this, "desc", 4 + 2*Dungeon.hero.pointsInTalent(Talent.LIFE_LINK), 30 + 5*Dungeon.hero.pointsInTalent(Talent.LIFE_LINK)) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本，包含动态计算的伤害分担和治疗效果。

**注意**：描述中的参数可能用于其他系统，实际效果主要体现在LifeLink Buff中。

**返回值**：
- 包含效果描述和充能消耗的完整描述字符串

---

## 内部类 LifeLinkSpellBuff

### 类定义

```java
public static class LifeLinkSpellBuff extends FlavourBuff {
    {
        type = buffType.POSITIVE;
    }
    // ... 其他方法
}
```

**类作用**：为盟友提供生命链接状态的视觉指示器。

**关键属性**：
- `type`: buffType.POSITIVE - 正面Buff类型

**关键方法**：
- `icon()`: 返回HOLY_ARMOR图标
- `iconFadePercent()`: 返回图标淡出百分比，基于天赋等级计算持续时间

### 链接机制

**双向保护**：
- 英雄受到伤害时，部分伤害由盟友承担
- 盟友受到伤害时，部分伤害由英雄承担
- 确保两个单位不会同时死亡

**视觉反馈**：
- HealthRay光束特效显示链接建立
- Buff图标显示链接状态
- 持续时间随天赋等级延长

---

## 特殊机制

### 盟友类型差异化

- **PowerOfMany盟友**：
  - 显示从英雄指向盟友的直线光束
  - 标准生命链接行为
- **Stasis盟友**：
  - 显示环绕英雄的光束特效
  - 清除Buff时间限制，提供永久链接（直到Stasis结束）

### 持续时间管理

- **基础持续时间**：基于天赋等级计算（10-17回合）
- **Stasis特殊处理**：对Stasis盟友的链接不受时间限制
- **Buff同步**：英雄和盟友的Buff同步创建和移除

### 伤害分担机制

- **实时同步**：伤害发生时立即计算分担比例
- **生命保护**：确保至少一个单位存活
- **治疗协同**：对任一单位的治疗会部分惠及另一方

---

## 使用示例

### 基本施法

```java
// 施放生命链接（需要存在盟友）
if (hero.hasTalent(Talent.LIFE_LINK) && 
    (PowerOfMany.getPoweredAlly() != null || Stasis.getStasisAlly() != null)) {
    LifeLinkSpell.INSTANCE.onCast(holyTome, hero);
}
```

### 盟友检测

```java
// 检查可用盟友类型
Char poweredAlly = PowerOfMany.getPoweredAlly();
Char stasisAlly = Stasis.getStasisAlly();

if (poweredAlly != null) {
    // 将链接到PowerOfMany盟友
} else if (stasisAlly != null) {
    // 将链接到Stasis盟友
}
```

### Buff状态检查

```java
// 检查生命链接是否活跃
LifeLink lifeLink = hero.buff(LifeLink.class);
if (lifeLink != null) {
    float remainingTime = lifeLink.cooldown(); // 获取剩余持续时间
    int linkedAllyId = lifeLink.object; // 获取链接的盟友ID
}
```

---

## 注意事项

### 平衡性考虑

1. **中等消耗**：2点充能消耗合理，适合关键战斗使用
2. **盟友依赖**：需要先通过其他天赋召唤盟友才能使用
3. **时机选择**：最佳使用时机是在高风险战斗开始前

### 特殊机制

1. **Stasis协同**：与Stasis天赋形成完美的永久保护组合
2. **PowerOfMany协同**：与力量合一天赋提供灵活的团队保护
3. **伤害分担**：双向保护机制确保团队生存能力

### 技术限制

1. **盟友检测**：依赖PowerOfMany和Stasis系统的正确实现
2. **ID管理**：使用角色ID进行链接，确保跨保存/加载的正确性
3. **Buff同步**：需要精确同步两个单位的Buff状态

---

## 最佳实践

### 战斗策略

- **Boss战准备**：在Boss战开始前建立生命链接，提高团队生存率
- **高风险探索**：在危险区域探索时提供额外安全保障
- **精英围攻**：面对多个精英怪时确保关键单位不会被秒杀

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.LIFE_LINK) && 
    hero.pointsInTalent(Talent.LIFE_LINK) >= 2 &&
    (hero.hasTalent(Talent.POWER_OF_MANY) || hero.hasTalent(Talent.STASIS))) {
    // 长持续时间生命链接配合强力盟友，形成终极生存体系
}
```

### 职业优化

- **祭司流派**：配合治疗法术，形成完整的团队支援体系
- **圣骑士流派**：配合高防御，形成坚不可摧的前线组合
- **通用策略**：合理规划盟友召唤和生命链接时机

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `ClericSpell` | 父类 | 所有牧师法术的基类 |
| `LifeLink` | 依赖 | 生命链接Buff，实现伤害分担 |
| `PowerOfMany` | 依赖 | 力量合一天赋，提供盟友 |
| `Stasis` | 依赖 | 静滞天赋，提供盟友 |
| `Beam.HealthRay` | 依赖 | 视觉光束特效 |
| `FlavourBuff` | Buff父类 | 提供持续效果的基础Buff类 |
| `BuffIndicator.HOLY_ARMOR` | 依赖 | Buff图标显示 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.lifelinkspell.name` | "生命链接" | 法术名称 |
| `spells.lifelinkspell.desc` | "在你和盟友之间建立生命链接，持续%d回合。受到的伤害将由双方共同承担，并且治疗效果也会部分惠及对方。" | 法术描述 |
| `spells.lifelinkspell.charge_cost` | "%d 充能" | 充能消耗提示 |