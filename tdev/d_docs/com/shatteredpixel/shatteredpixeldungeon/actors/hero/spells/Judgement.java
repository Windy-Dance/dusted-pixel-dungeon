# Judgement 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/Judgement.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends ClericSpell |
| **代码行数** | 104 |
| **中文名称** | 审判 |

---

## 法术概述

`Judgement`（审判）是牧师职业的4级终极法术。该法术的主要功能是：

1. **核心效果**：对所有可见的敌人造成大范围神圣伤害，并根据升天形态中的法术施放次数增强伤害
2. **战术价值**：作为升天形态下的终极清场技能，在面对大量敌人时提供爆发性AoE伤害
3. **使用场景**：精英怪围攻、房间清场、或任何需要快速消灭多个敌人的紧急情况

**法术类型**：
- **目标类型**：Area Effect（区域效果，影响所有可见敌人）
- **充能消耗**：3点充能
- **天赋需求**：JUDGEMENT 天赋 + 升天形态激活

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
    
    class Judgement {
        +INSTANCE: Judgement
        +int icon()
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +void onCast(HolyTome, Hero)
        +String desc()
    }
    
    ClericSpell <|-- Judgement
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | Judgement.INSTANCE | 单例实例 |

---

## 核心属性

### 充能消耗

| 属性 | 值 | 说明 |
|------|-----|------|
| `chargeUse()` | 3f | 每次施放消耗3点充能 |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | false | 无需目标选择，自动影响所有可见敌人 |
| `targetingFlags()` | -1 | 无目标选择标志 |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires JUDGEMENT + AscendedForm | 施放此法术的特殊条件 |

### 伤害数值

| 天赋等级 | 基础伤害 | 伤害范围 | 升天增益 |
|----------|----------|----------|----------|
| 1点 | 10 | 10-20 | +spellCasts/3 |
| 2点 | 15 | 15-30 | +spellCasts/3 |
| 3点 | 20 | 20-40 | +spellCasts/3 |

**升天增益说明**：每3次升天形态中施放的法术，增加基础伤害的100%

---

## 方法详解

### onCast(HolyTome tome, Hero hero)

```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    // 执行审判的AoE伤害逻辑
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：执行审判的主要逻辑，对所有可见敌人造成范围伤害。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄

**实现逻辑**：
1. **动画播放**：执行攻击动画并触发回调
2. **视觉特效**：全屏白色闪光（#80FFFFFF）和爆炸音效
3. **伤害计算**：
   - 基础伤害：5 + 5×天赋等级
   - 升天增益：基础伤害 × (spellCasts / 3)
4. **AoE伤害**：
   - 遍历所有角色，对视野内且敌对阵营的角色造成伤害
   - 伤害范围：基础值到2倍基础值的随机值
5. **祭司效果**：如果是祭司职业，对所有受影响敌人施加照亮效果
6. **升天重置**：将AscendedForm中的spellCasts重置为0

---

### canCast(Hero hero)

```java
@Override
public boolean canCast(Hero hero) {
    return super.canCast(hero)
            && hero.hasTalent(Talent.JUDGEMENT)
            && hero.buff(AscendedForm.AscendBuff.class) != null;
}
```

**方法作用**：检查英雄是否可以施放审判法术。

**返回值**：
- `true`：拥有天赋且处于升天形态
- `false`：缺少天赋或不在升天形态

---

### desc()

```java
@Override
public String desc() {
    int baseDmg = 5 + 5*Dungeon.hero.pointsInTalent(Talent.JUDGEMENT);
    int totalBaseDmg = baseDmg;
    if (Dungeon.hero.buff(AscendedForm.AscendBuff.class) != null) {
        totalBaseDmg += Math.round(baseDmg*Dungeon.hero.buff(AscendedForm.AscendBuff.class).spellCasts/3f);
    }
    return Messages.get(this, "desc", baseDmg, 2*baseDmg, totalBaseDmg, 2*totalBaseDmg) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本，包含动态计算的基础伤害和总伤害范围。

**返回值**：
- 包含基础伤害范围、总伤害范围和充能消耗的完整描述字符串

---

## 特殊机制

### 升天形态协同

- **法术计数**：升天形态中每次施放法术都会增加spellCasts计数器
- **伤害增益**：每3次法术施放，增加100%基础伤害
- **重置机制**：使用审判后重置spellCasts为0，确保平衡性

### 职业差异化

- **祭司职业**：对所有受影响敌人施加照亮效果，提升后续命中率
- **圣骑士职业**：无特殊效果，但可配合其他圣骑士天赋
- **通用效果**：所有职业都享受升天形态的伤害增益

### AoE范围机制

- **视野限制**：只影响英雄视野范围内的敌人
- **阵营过滤**：只对敌对阵营造成伤害，不伤害盟友
- **伤害随机性**：在基础值到2倍基础值之间随机，增加战术不确定性

---

## 使用示例

### 基本施法

```java
// 施放审判（需要升天形态激活）
if (hero.hasTalent(Talent.JUDGEMENT) && 
    hero.buff(AscendedForm.AscendBuff.class) != null) {
    Judgement.INSTANCE.onCast(holyTome, hero);
}
```

### 升天伤害计算

```java
// 计算当前审判伤害
AscendedForm.AscendBuff ascendBuff = hero.buff(AscendedForm.AscendBuff.class);
if (ascendBuff != null) {
    int baseDmg = 5 + 5 * hero.pointsInTalent(Talent.JUDGEMENT);
    int totalDmg = baseDmg + Math.round(baseDmg * ascendBuff.spellCasts / 3f);
    // totalDmg 是实际基础伤害值
}
```

### 团队协同

```java
// 审判会自动影响所有可见敌人
// 无需手动选择目标，系统自动处理
Judgement.INSTANCE.onCast(tome, hero);
```

---

## 注意事项

### 平衡性考虑

1. **中等消耗**：3点充能消耗适中，但需要升天形态限制
2. **时机关键**：最佳使用时机是在升天形态后期，spellCasts计数较高时
3. **范围限制**：只能影响视野内的敌人，需要合理站位

### 特殊机制

1. **计数重置**：使用后重置spellCasts，防止无限叠加
2. **职业协同**：祭司的照亮效果与审判形成完美连招
3. **升天依赖**：完全依赖AscendedForm系统，无法独立使用

### 技术限制

1. **视野检测**：依赖Dungeon.level.heroFOV正确计算可见区域
2. **阵营识别**：正确识别敌对阵营，避免误伤盟友
3. **伤害计算**：精确的浮点数计算和四舍五入处理

---

## 最佳实践

### 战斗策略

- **升天蓄力**：在升天形态中多次施放其他法术，积累spellCasts
- **位置选择**：站在能看见最多敌人的位置施放审判
- **连招配合**：先用其他法术蓄力，再用审判爆发清场

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.JUDGEMENT) && 
    hero.pointsInTalent(Talent.JUDGEMENT) >= 2 &&
    hero.hasTalent(Talent.ASCENDED_FORM)) {
    // 高伤害审判配合强力升天形态，形成终极AoE体系
}
```

### 职业优化

- **祭司流派**：审判+照亮，为团队创造完美的输出环境
- **圣骑士流派**：配合高生存能力，在前线安全施放审判
- **通用策略**：在房间入口或狭窄通道施放，最大化敌人数量

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `ClericSpell` | 父类 | 所有牧师法术的基类 |
| `AscendedForm.AscendBuff` | 依赖 | 升天形态Buff，提供施法条件和伤害增益 |
| `GuidingLight.Illuminated` | 协同 | 祭司职业的照亮Debuff |
| `Actor.chars()` | 依赖 | 角色遍历系统 |
| `Dungeon.level.heroFOV` | 依赖 | 视野检测系统 |
| `Hero.heroDamageIntRange()` | 依赖 | 伤害计算系统 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.judgement.name` | "审判" | 法术名称 |
| `spells.judgement.desc` | "对所有可见的敌人造成%d-%d点神圣伤害。在升天形态下，伤害基于你已施放的法术次数提升至%d-%d点。" | 法术描述 |
| `spells.judgement.charge_cost` | "%d 充能" | 充能消耗提示 |