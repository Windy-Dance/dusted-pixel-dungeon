# DivineIntervention 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/DivineIntervention.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends ClericSpell |
| **代码行数** | 125 |
| **中文名称** | 神圣干预 |

---

## 法术概述

`DivineIntervention`（神圣干预）是牧师职业的4级终极法术。该法术的主要功能是：

1. **核心效果**：为所有盟友（包括英雄自己）施加巨额护盾，并延长升天形态的持续时间
2. **战术价值**：作为升天形态下的终极保命技能，在团队面临灭团危机时提供绝对防护
3. **使用场景**：Boss战关键时刻、精英怪围攻、或任何需要绝对生存保障的紧急情况

**法术类型**：
- **目标类型**：Area Effect（区域效果，影响所有盟友）
- **充能消耗**：5点充能（游戏最高消耗）
- **天赋需求**：DIVINE_INTERVENTION 天赋 + 升天形态激活

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
    
    class DivineIntervention {
        +INSTANCE: DivineIntervention
        +int icon()
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +void onCast(HolyTome, Hero)
        +String desc()
    }
    
    class DivineShield {
        +shieldUsePriority = 1
        +boolean act()
        +int shielding()
        +void fx(boolean)
    }
    
    ClericSpell <|-- DivineIntervention
    DivineIntervention +-- DivineShield
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | DivineIntervention.INSTANCE | 单例实例 |

---

## 核心属性

### 充能消耗

| 属性 | 值 | 说明 |
|------|-----|------|
| `chargeUse()` | 5f | 每次施放消耗5点充能（最高消耗） |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | false | 无需目标选择，自动影响所有盟友 |
| `targetingFlags()` | -1 | 无目标选择标志 |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires DIVINE_INTERVENTION + AscendedForm | 施放此法术的特殊条件 |

### 特殊限制

| 条件 | 说明 |
|------|------|
| **升天形态** | 必须处于AscendedForm.AscendBuff状态 |
| **单次使用** | 每次升天形态只能使用一次（divineInverventionCast标记） |
| **高消耗** | 5点充能消耗，通常需要充能管理 |

### 效果数值

| 效果 | 1点天赋 | 2点天赋 | 3点天赋 |
|------|---------|---------|---------|
| 护盾值 | 150点 | 200点 | 250点 |
| 升天延长 | 3回合 | 4回合 | 5回合 |
| 影响范围 | 所有盟友 + 英雄 | 所有盟友 + 英雄 | 所有盟友 + 英雄 |

---

## 方法详解

### onCast(HolyTome tome, Hero hero)

```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    // 为所有盟友施加神圣护盾并延长升天形态
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：执行神圣干预的主要逻辑，为所有盟友提供巨额护盾。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄

**实现逻辑**：
1. **音效播放**：播放充能音效（CHARGEUP）
2. **盟友护盾**：
   - 遍历所有角色，找到所有盟友（不包括英雄自己）
   - 为每个盟友施加DivineShield护盾：100 + 50×天赋等级点
   - 显示金色光效（#FFFF00）
3. **英雄护盾**：
   - 通过AscendedForm buff施加相同数量的护盾
   - 显示相同的金色光效
4. **升天延长**：
   - 标记divineInverventionCast为true（防止重复使用）
   - 延长升天形态持续时间：2 + 天赋等级回合

---

### canCast(Hero hero)

```java
@Override
public boolean canCast(Hero hero) {
    return super.canCast(hero)
            && hero.hasTalent(Talent.DIVINE_INTERVENTION)
            && hero.buff(AscendedForm.AscendBuff.class) != null
            && !hero.buff(AscendedForm.AscendBuff.class).divineInverventionCast;
}
```

**方法作用**：检查英雄是否可以施放神圣干预法术。

**返回值**：
- `true`：满足所有条件（天赋+升天形态+未使用过）
- `false`：缺少任一条件

### desc()

```java
@Override
public String desc() {
    int shield = 100 + 50*Dungeon.hero.pointsInTalent(Talent.DIVINE_INTERVENTION);
    int leftBonus = 2+Dungeon.hero.pointsInTalent(Talent.DIVINE_INTERVENTION);
    return Messages.get(this, "desc", shield, leftBonus) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本，包含动态计算的护盾值和升天延长回合数。

**返回值**：
- 包含护盾值、升天延长回合数和充能消耗的完整描述字符串

---

## 内部类 DivineShield

### 类定义

```java
public static class DivineShield extends ShieldBuff {
    {
        shieldUsePriority = 1;
    }
    // ... 其他方法
}
```

**类作用**：实现神圣护盾效果，具有特殊的持续时间和优先级机制。

**关键属性**：
- `shieldUsePriority`: 1 - 高优先级护盾（先于普通护盾使用）

**关键方法**：
- `act()`: 检查升天形态是否存在，不存在则移除护盾
- `shielding()`: 返回护盾值，升天形态结束后返回0
- `fx()`: 添加/移除SHIELDED视觉状态

### 护盾机制

**动态护盾**：
- 护盾值在升天形态存在时正常生效
- 升天形态结束后，护盾立即失效（shielding()返回0）
- 这确保了护盾与升天形态的绑定关系

**高优先级**：
- `shieldUsePriority = 1` 确保护盾在其他护盾之前被消耗
- 提供最可靠的保护层

---

## 使用示例

### 基本施法

```java
// 施放神圣干预（需要升天形态激活）
if (hero.hasTalent(Talent.DIVINE_INTERVENTION) && 
    hero.buff(AscendedForm.AscendBuff.class) != null &&
    !hero.buff(AscendedForm.AscendBuff.class).divineInverventionCast) {
    DivineIntervention.INSTANCE.onCast(holyTome, hero);
}
```

### 升天形态检测

```java
// 检查是否可以使用神圣干预
AscendedForm.AscendBuff ascendBuff = hero.buff(AscendedForm.AscendBuff.class);
if (ascendBuff != null && !ascendBuff.divineInverventionCast) {
    // 可以施放神圣干预
}
```

### 团队保护

```java
// 神圣干预自动保护所有盟友
// 无需手动选择目标，系统自动处理
DivineIntervention.INSTANCE.onCast(tome, hero);
```

---

## 注意事项

### 平衡性考虑

1. **极高消耗**：5点充能是游戏中最高消耗，需要精心规划
2. **严格限制**：必须在升天形态下使用，且每形态只能使用一次
3. **时机关键**：最佳使用时机是在团队生命危急但升天形态仍在持续时

### 特殊机制

1. **护盾绑定**：护盾与升天形态绑定，形态结束后护盾立即消失
2. **单次限制**：通过divineInverventionCast标记确保不会滥用
3. **团队覆盖**：自动影响所有盟友，包括召唤单位和NPC盟友

### 技术限制

1. **升天依赖**：完全依赖AscendedForm系统，无法独立使用
2. **Buff检查**：护盾会定期检查升天Buff是否存在
3. **优先级管理**：高优先级确保护盾最先被消耗

---

## 最佳实践

### 战斗策略

- **终极保命**：在团队血量极低且面临大量伤害时使用
- **Boss斩杀**：在升天形态最后几回合使用，确保存活到战斗结束
- **精英围攻**：面对多个精英怪围攻时提供绝对防护

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.DIVINE_INTERVENTION) && 
    hero.pointsInTalent(Talent.DIVINE_INTERVENTION) >= 2 &&
    hero.hasTalent(Talent.ASCENDED_FORM)) {
    // 高级神圣干预配合强力升天形态，形成终极防护体系
}
```

### 资源管理

- **充能储备**：确保在升天形态激活前有足够的充能储备
- **时机把握**：不要过早使用，保留到真正危急时刻
- **团队协调**：告知队友神圣干预已使用，调整后续战术

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `ClericSpell` | 父类 | 所有牧师法术的基类 |
| `AscendedForm.AscendBuff` | 依赖 | 升天形态Buff，提供施法条件 |
| `ShieldBuff` | 父类 | 护盾Buff基类 |
| `CharSprite.State.SHIELDED` | 依赖 | 视觉护盾状态 |
| `PowerOfMany` | 协同 | 力量合一天赋，提供额外盟友 |
| `LifeLinkSpell` | 协同 | 生命链接法术，盟友也会获得护盾 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.divineintervention.name` | "神圣干预" | 法术名称 |
| `spells.divineintervention.desc` | "为所有盟友施加%d点护盾，并延长升天形态%d回合。此法术在每次升天形态中只能使用一次。" | 法术描述 |
| `spells.divineintervention.charge_cost` | "%d 充能" | 充能消耗提示 |