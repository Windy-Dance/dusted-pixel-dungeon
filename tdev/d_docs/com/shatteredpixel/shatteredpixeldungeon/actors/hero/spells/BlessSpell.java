# BlessSpell 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/BlessSpell.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 136 |
| **中文名称** | 祝福术 |

---

## 法术概述

`BlessSpell`（祝福术）是牧师职业的2级法术。该法术的主要功能是：

1. **核心效果**：为目标施加祝福Buff，提升命中率和闪避率，并提供治疗或护盾效果
2. **战术价值**：在战斗前为关键目标提供战斗增益，或在战斗中提供紧急治疗和防护
3. **使用场景**：Boss战前准备、团队支援、紧急治疗或自我强化

**法术类型**：
- **目标类型**：Targeted（需要选择目标角色）
- **充能消耗**：1点充能
- **天赋需求**：BLESS 天赋

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
    
    class BlessSpell {
        +INSTANCE: BlessSpell
        +int icon()
        +int targetingFlags()
        +boolean canCast(Hero)
        +String desc()
        #void onTargetSelected(HolyTome, Hero, Integer)
        #void affectChar(Hero, Char)
    }
    
    ClericSpell <|-- TargetedClericSpell
    TargetedClericSpell <|-- BlessSpell
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | BlessSpell.INSTANCE | 单例实例 |

---

## 核心属性

### 充能消耗

| 属性 | 值 | 说明 |
|------|-----|------|
| `chargeUse()` | 1f | 每次施放消耗1点充能 |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | true | 需要目标选择 |
| `targetingFlags()` | -1 | 自定义目标选择行为（不使用自动弹道） |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires BLESS | 施放此法术所需的天赋 |

### 效果数值

| 效果 | 英雄自身 | 盟友 | 天赋加成（每点） |
|------|----------|------|------------------|
| 祝福持续时间 | 2回合 | 5回合 | +4英/+5盟 |
| 治疗/护盾量 | 护盾5点 | 治疗5点 | +5点 |

---

## 方法详解

### onTargetSelected(HolyTome tome, Hero hero, Integer target)

```java
@Override
protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
    // 处理目标选择逻辑
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：处理玩家选择的目标，执行祝福和治疗/护盾逻辑。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄
- `target` (Integer)：选择的目标单元格坐标

**实现逻辑**：
1. **目标验证**：检查目标是否存在且在视野范围内
2. **特效播放**：播放传送音效和视觉光效
3. **效果应用**：调用 `affectChar()` 应用祝福和治疗/护盾效果
4. **动画处理**：根据目标是英雄还是盟友播放不同动画
5. **生命链接协同**：如果存在生命链接盟友，复制效果到另一方
6. **祭司效果**：如果是祭司职业，对非盟友目标施加照亮效果

---

### affectChar(Hero hero, Char ch)

```java
private void affectChar(Hero hero, Char ch) {
    // 应用祝福和治疗/护盾效果
}
```

**方法作用**：为核心目标应用祝福Buff和治疗/护盾效果。

**参数**：
- `hero` (Hero)：施法英雄（用于天赋等级计算）
- `ch` (Char)：目标角色

**实现逻辑**：
- **英雄自身**：
  - 祝福持续时间：2 + 4×天赋等级 回合
  - 护盾值：5 + 5×天赋等级 点
- **盟友目标**：
  - 祝福持续时间：5 + 5×天赋等级 回合
  - 治疗量：5 + 5×天赋等级 点（优先填满HP，溢出部分转为护盾）

---

### targetingFlags()

```java
@Override
public int targetingFlags() {
    return -1; //auto-targeting behaviour is often wrong, so we don't use it
}
```

**方法作用**：禁用自动弹道目标选择，使用自定义目标选择逻辑。

**返回值**：
- `-1`：表示不使用标准弹道系统

---

### desc()

```java
public String desc() {
    int talentLvl = Dungeon.hero.pointsInTalent(Talent.BLESS);
    return Messages.get(this, "desc", 2+4*talentLvl, 5+5*talentLvl, 5+5*talentLvl, 5+5*talentLvl) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本，包含动态计算的效果数值。

**返回值**：
- 包含祝福持续时间、治疗量、护盾量和充能消耗的完整描述字符串

---

## 特殊机制

### 生命链接协同

当存在生命链接盟友时：
- **对英雄施放**：同时为盟友施加相同效果
- **对盟友施放**：同时为英雄施加相同效果
- 这使得祝福术成为强大的团队支援技能

### 职业差异

- **祭司职业**：对非盟友目标（敌人）施加照亮效果，使其更容易被后续攻击命中
- **圣骑士职业**：无特殊效果，但可与其他圣骑士天赋协同

### 治疗机制

盟友治疗采用智能分配机制：
1. 优先恢复生命值至最大
2. 溢出的生命值自动转换为护盾
3. 同时显示治疗和护盾的视觉反馈

---

## 使用示例

### 基本施法

```java
// 施放祝福术
if (hero.hasTalent(Talent.BLESS)) {
    // 玩家选择目标后自动调用
    BlessSpell.INSTANCE.onTargetSelected(tome, hero, targetPos);
}
```

### 目标选择流程

```java
// 完整的施法流程
HolyTome tome = new HolyTome();
if (tome.canCast(hero, BlessSpell.INSTANCE)) {
    // 1. 打开目标选择器
    BlessSpell.INSTANCE.onCast(tome, hero);
    
    // 2. 玩家选择目标位置
    // 3. 自动调用 onTargetSelected 处理效果
}
```

### 检查Buff状态

```java
// 检查目标是否有祝福Buff
Bless bless = target.buff(Bless.class);
if (bless != null) {
    float remainingTime = bless.cooldown(); // 获取剩余持续时间
}
```

---

## 注意事项

### 平衡性考虑

1. **充能效率**：1点充能消耗较低，可以频繁使用
2. **目标限制**：只能对视野范围内的目标施放
3. **时机选择**：祝福Buff在战斗开始前施放效果最佳

### 特殊机制

1. **治疗智能分配**：盟友治疗会智能分配为生命值和护盾
2. **生命链接复制**：与生命链接天赋形成强大协同效应
3. **祭司照亮**：对敌人施放时提供战术优势

### 技术限制

1. **视野要求**：目标必须在英雄视野范围内
2. **角色限制**：只能对存在的角色施放（不能对空地）
3. **Buff叠加**：祝福Buff不可叠加，重复施放会刷新持续时间

---

## 最佳实践

### 战斗策略

- **进攻性使用**：战斗前为输出角色施加祝福，提高命中率
- **防御性使用**：战斗中为受伤盟友提供紧急治疗
- **自我保护**：为自己施加祝福和护盾，提高生存能力

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.BLESS) && 
    hero.hasTalent(Talent.LIFE_LINK) &&
    hero.pointsInTalent(Talent.BLESS) >= 2) {
    // 生命链接+高级祝福，形成强大的团队支援体系
}
```

### 职业优化

- **祭司流派**：利用照亮效果增强团队输出
- **圣骑士流派**：配合圣光武器延长，形成持久战斗能力
- **通用策略**：在精英怪或Boss战前预先施放祝福

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `TargetedClericSpell` | 父类 | 目标选择法术基类 |
| `Bless` | 效果 | 祝福Buff，提升命中和闪避 |
| `Barrier` | 效果 | 护盾Buff，吸收伤害 |
| `LifeLinkSpell` | 协同 | 生命链接法术，复制祝福效果 |
| `GuidingLight` | 协同 | 引导之光，祭司职业额外效果 |
| `PowerOfMany` | 依赖 | 力量合一天赋，提供盟友 |
| `FloatingText` | 依赖 | 视觉反馈系统 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.blessspell.name` | "祝福术" | 法术名称 |
| `spells.blessspell.desc` | "为目标施加祝福，提升其战斗能力。对自身施放时，获得%d回合的祝福效果和%d点护盾。对盟友施放时，获得%d回合的祝福效果和最多%d点治疗（溢出部分转为护盾）。" | 法术描述 |
| `spells.blessspell.prompt` | "选择要祝福的目标" | 目标选择提示 |
| `spells.blessspell.no_target` | "没有有效目标" | 目标无效错误提示 |
| `spells.blessspell.charge_cost` | "%d 充能" | 充能消耗提示 |