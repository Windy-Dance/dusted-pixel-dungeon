# Smite 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/Smite.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 132 |
| **中文名称** | 惩击 |

---

## 法术概述

`Smite`（惩击）是牧师职业的圣骑士专属法术。该法术的主要功能是：

1. **核心效果**：对目标敌人进行一次强力神圣攻击，造成基于英雄等级的高额伤害
2. **战术价值**：提供可靠的单体爆发伤害，特别针对亡灵和恶魔单位有额外效果
3. **使用场景**：Boss战、精英怪战斗、或需要快速削减高威胁敌人血量时

**法术类型**：
- **目标类型**：Targeted（需要选择敌人目标）
- **充能消耗**：2点充能
- **天赋需求**：无天赋要求，但仅限圣骑士职业使用

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
    
    class TargetedClericSpell {
        +void onCast(HolyTome, Hero)
        +int targetingFlags()
        #abstract void onTargetSelected(HolyTome, Hero, Integer)
    }
    
    class Smite {
        +INSTANCE: Smite
        +int icon()
        +int targetingFlags()
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +String desc()
        #void onTargetSelected(HolyTome, Hero, Integer)
        +static int bonusDmg(Hero, Char)
    }
    
    class SmiteTracker {
        // 空Buff，用于标记攻击状态
    }
    
    ClericSpell <|-- TargetedClericSpell
    TargetedClericSpell <|-- Smite
    Smite +-- SmiteTracker
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | Smite.INSTANCE | 单例实例 |

---

## 核心属性

### 充能消耗

| 属性 | 值 | 说明 |
|------|-----|------|
| `chargeUse()` | 2f | 每次施放消耗2点充能 |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | true | 需要目标选择 |
| `targetingFlags()` | Ballistica.STOP_TARGET | 在目标处停止的弹道类型（无自动瞄准） |

### 职业限制

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | 仅限圣骑士职业 | 只有HeroSubClass.PALADIN可以施放 |

### 伤害数值

| 英雄等级 | 最小伤害 | 最大伤害 | 亡灵/恶魔伤害 |
|----------|----------|----------|----------------|
| 1级 | 5 | 11 | 11（全额） |
| 10级 | 10 | 20 | 20（全额） |
| 20级 | 15 | 30 | 30（全额） |
| 30级 | 20 | 40 | 40（全额） |

**计算公式**: min = 5 + lvl/2, max = 10 + lvl

---

## 方法详解

### onTargetSelected(HolyTome tome, Hero hero, Integer target)

```java
@Override
protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
    // 执行神圣惩击攻击
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：处理玩家选择的目标，执行神圣惩击的攻击逻辑。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄
- `target` (Integer)：选择的目标单元格坐标

**实现逻辑**：
1. **目标验证**：
   - 检查目标是否存在且不为英雄自己
   - 验证目标在视野范围内且可被攻击
   - 检查是否被目标魅惑（防止自残）
2. **Buff标记**：施加SmiteTracker Buff用于攻击期间的状态跟踪
3. **攻击执行**：
   - 播放攻击动画
   - 设置攻击指示器目标
   - 计算命中率（武器STR满足时获得无限命中）
   - 执行标准攻击逻辑
4. **伤害应用**：
   - 对亡灵/恶魔单位造成全额最大伤害
   - 对其他单位造成随机范围伤害
   - 播放强力打击音效和粒子特效
5. **完成施法**：
   - 移除隐身状态
   - 花费攻击延迟时间
   - 调用onSpellCast处理后续逻辑

---

### bonusDmg(Hero attacker, Char defender)

```java
public static int bonusDmg(Hero attacker, Char defender) {
    // 计算惩击的额外伤害
}
```

**方法作用**：计算惩击对特定敌人的实际伤害值。

**参数**：
- `attacker` (Hero)：攻击的英雄
- `defender` (Char)：防御的敌人

**返回值**：
- 对亡灵/恶魔单位：返回最大伤害值
- 对其他单位：返回随机范围内的伤害值

---

### canCast(Hero hero)

```java
@Override
public boolean canCast(Hero hero) {
    return super.canCast(hero) && hero.subClass == HeroSubClass.PALADIN;
}
```

**方法作用**：检查英雄是否可以施放惩击法术。

**返回值**：
- `true`：英雄是圣骑士职业且未被魔法免疫
- `false`：英雄不是圣骑士职业

---

## 内部类 SmiteTracker

### 类定义

```java
public static class SmiteTracker extends FlavourBuff {};
```

**类作用**：空Buff类，仅用于在攻击过程中标记英雄状态。

**关键特性**：
- 继承自FlavourBuff但无特殊逻辑
- 仅在攻击动画期间存在，攻击完成后立即移除
- 可能用于其他系统检测当前是否在执行惩击

---

## 特殊机制

### 职业专属机制

- **圣骑士限定**：只有圣骑士职业可以使用，体现职业特色
- **无天赋要求**：作为基础职业法术，不需要额外天赋投资
- **协同效应**：与其他圣骑士天赋形成完整的近战输出体系

### 命中机制

- **无限命中**：当装备的武器STR要求满足时，获得无限命中率
- **标准攻击**：使用标准的英雄攻击系统，包括暴击、格挡等机制
- **伤害计算**：基于英雄等级的动态伤害，确保后期依然有效

### 种族克制

- **亡灵/恶魔特攻**：对亡灵（UNDEAD）和恶魔（DEMONIC）单位造成全额最大伤害
- **通用伤害**：对其他单位造成正常的随机范围伤害
- **战术价值**：专门用来对付游戏中常见的亡灵和恶魔敌人

---

## 使用示例

### 基本施法

```java
// 施放惩击（仅限圣骑士职业）
if (hero.subClass == HeroSubClass.PALADIN) {
    // 玩家选择敌人目标后自动调用
    Smite.INSTANCE.onTargetSelected(tome, hero, targetPos);
}
```

### 目标选择流程

```java
// 完整的施法流程
HolyTome tome = new HolyTome();
if (tome.canCast(hero, Smite.INSTANCE)) {
    // 1. 打开目标选择器
    Smite.INSTANCE.onCast(tome, hero);
    
    // 2. 玩家选择敌人目标
    // 3. 自动调用 onTargetSelected 执行攻击
}
```

### 种族克制利用

```java
// 识别亡灵/恶魔单位以最大化伤害
if (Char.hasProp(enemy, Char.Property.UNDEAD) || 
    Char.hasProp(enemy, Char.Property.DEMONIC)) {
    // 惩击将造成全额最大伤害
}
```

---

## 注意事项

### 平衡性考虑

1. **中等消耗**：2点充能消耗适中，适合定期使用
2. **职业限定**：仅限圣骑士使用，保持职业差异化
3. **等级成长**：伤害随英雄等级提升，确保后期依然有效

### 特殊机制

1. **命中保障**：STR满足的武器提供无限命中，确保关键攻击不miss
2. **种族优势**：对特定敌人类型有显著优势，需要识别敌人类型
3. **攻击整合**：使用标准攻击系统，与所有攻击相关天赋和效果兼容

### 技术限制

1. **目标检测**：依赖Actor系统正确识别目标角色
2. **伤害计算**：需要精确的浮点数计算和随机数生成
3. **动画同步**：攻击动画与伤害应用需要精确同步

---

## 最佳实践

### 战斗策略

- **Boss斩杀**：在Boss血量较低时使用，确保击杀
- **精英优先**：优先对高威胁精英怪使用，减少团队压力  
- **种族利用**：专门用来对付亡灵和恶魔单位，发挥最大效果

### 职业优化

- **圣骑士流派**：
  ```java
  // 圣骑士可以在任何近战战斗中使用惩击
  // 形成稳定的输出循环
  ```
- **武器选择**：选择STR要求适合当前等级的武器，确保无限命中
- **连招配合**：与其他圣骑士技能形成"攻击-惩击-攻击"的连招

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.subClass == HeroSubClass.PALADIN && 
    hero.hasTalent(Talent.HOLY_WEAPON)) {
    // 惩击配合神圣武器，形成完美的近战输出体系
}
```

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `TargetedClericSpell` | 父类 | 目标选择法术基类 |
| `Ballistica.STOP_TARGET` | 依赖 | 弹道计算系统 |
| `AttackIndicator` | 依赖 | 攻击目标指示器 |
| `Invisibility` | 依赖 | 隐身状态管理 |
| `Char.Property.UNDEAD/DEMONIC` | 依赖 | 敌人种族属性检测 |
| `Weapon.STRReq()` | 依赖 | 武器力量要求检测 |
| `Hero.attack()` | 依赖 | 标准攻击系统 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.smite.name` | "惩击" | 法术名称 |
| `spells.smite.desc` | "对目标造成%d-%d点神圣伤害。对亡灵和恶魔单位造成全额最大伤害。" | 法术描述 |
| `spells.smite.prompt` | "选择要攻击的敌人" | 目标选择提示 |
| `spells.smite.no_target` | "没有有效目标" | 目标无效错误提示 |
| `spells.smite.invalid_enemy` | "无法攻击该敌人" | 敌人无效错误提示 |
| `spells.smite.charge_cost` | "%d 充能" | 充能消耗提示 |