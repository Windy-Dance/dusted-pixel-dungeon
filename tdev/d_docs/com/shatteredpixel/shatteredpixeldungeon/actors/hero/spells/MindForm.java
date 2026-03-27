# MindForm 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/MindForm.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends ClericSpell |
| **代码行数** | 177 |
| **中文名称** | 心灵形态 |

---

## 法术概述

`MindForm`（心灵形态）是牧师职业的4级法术，属于三位一体（Trinity）系统的一部分。该法术的主要功能是：

1. **核心效果**：激活心灵形态，允许英雄临时使用高等级法杖或投掷武器的效果
2. **战术价值**：提供灵活的远程攻击能力，在不同战斗场景中切换攻击方式
3. **使用场景**：需要远程魔法攻击、对付高防御敌人、或补充物理输出时

**法术类型**：
- **目标类型**：Self-Targeted（自我目标）
- **充能消耗**：3点充能
- **天赋需求**：MIND_FORM 天赋

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
    
    class MindForm {
        +INSTANCE: MindForm
        +int icon()
        +String desc()
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +void onCast(HolyTome, Hero)
        +static int effectLevel()
        +static int itemLevel()
    }
    
    class targetSelector {
        +Bundlable effect
        +Wand wand()
        +MissileWeapon thrown()
        +void onSelect(Integer)
        +String prompt()
    }
    
    ClericSpell <|-- MindForm
    MindForm +-- targetSelector
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | MindForm.INSTANCE | 单例实例 |

---

## 核心属性

### 充能消耗

| 属性 | 值 | 说明 |
|------|-----|------|
| `chargeUse()` | 3f | 每次施放消耗3点充能 |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | false | 无需目标选择，自动激活形态 |
| `targetingFlags()` | -1 | 无目标选择标志 |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires MIND_FORM | 施放此法术所需的天赋 |

### 效果等级

| 天赋等级 | 物品等级 | 法杖效果等级 | 说明 |
|----------|----------|--------------|------|
| 1点 | 3级 | 3级 | 基础心灵形态 |
| 2点 | 4级 | 4级 | 增强效果 |
| 3点 | 5级 | 5级 | 最强效果 |

**等级计算**: `itemLevel() = 2 + talent_points`, `effectLevel() = 2 + talent_points`

---

## 方法详解

### onCast(HolyTome tome, Hero hero)

```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    GameScene.show(new Trinity.WndItemtypeSelect(tome, this));
}
```

**方法作用**：打开三位一体物品类型选择窗口，而不是直接施放效果。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄

**实现逻辑**：
1. 显示 `Trinity.WndItemtypeSelect` 窗口
2. 玩家在窗口中选择要使用的物品类型（法杖或投掷武器）
3. 选择完成后，通过 `targetSelector` 处理后续目标选择和效果应用

---

### targetSelector.onSelect(Integer target)

```java
@Override
public void onSelect(Integer target) {
    // 处理目标选择并应用法杖/投掷武器效果
}
```

**方法作用**：处理玩家选择的目标，执行法杖或投掷武器的效果。

**参数**：
- `target` (Integer)：选择的目标单元格坐标

**实现逻辑**：
- **法杖效果**：
  - 创建指定等级的法杖（满充能、已识别）
  - 使用标准法杖弹道和效果系统
  - 有概率触发WonderousResin的额外诅咒效果
  - 消耗ClassArmor充能而非HolyTome充能
- **投掷武器效果**：
  - 创建指定等级的投掷武器（完全修复、已识别）
  - 使用标准投掷武器施放逻辑
  - 消耗ClassArmor充能

---

### itemLevel() / effectLevel()

```java
public static int itemLevel() {
    return 2 + Dungeon.hero.pointsInTalent(Talent.MIND_FORM);
}

public static int effectLevel() {
    return 2 + Dungeon.hero.pointsInTalent(Talent.MIND_FORM);
}
```

**方法作用**：返回基于天赋等级计算的物品和效果等级。

**返回值**：
- 基础等级2 + 天赋等级（范围3-5级）

---

### desc()

```java
@Override
public String desc() {
    return Messages.get(this, "desc", itemLevel()) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本，包含动态计算的物品等级。

**返回值**：
- 包含物品等级和充能消耗的完整描述字符串

---

## 特殊机制

### 三位一体系统

- **形态选择**：与BodyForm、SpiritForm共同构成三位一体系统
- **物品创建**：临时创建高等级法杖或投掷武器用于单次使用
- **充能管理**：消耗ClassArmor充能而非HolyTome充能，与其他系统协同

### 法杖效果机制

- **等级提升**：创建的法杖等级为2+talent_points（3-5级）
- **充能满载**：法杖创建时充满充能
- **自动识别**：法杖自动识别，无需鉴定
- **额外效果**：有概率触发WonderousResin的积极效果

### 投掷武器机制

- **等级提升**：创建的投掷武器等级为2+talent_points（3-5级）
- **完全修复**：武器创建时完全修复，耐久度满
- **标记特效**：设置spawnedForEffect标志，确保正确行为

### 充能协同

- **圣骑士协同**：消耗圣骑士护甲的充能，而非神圣典籍充能
- **资源管理**：合理规划护甲充能使用，避免资源冲突
- **平衡设计**：高充能消耗确保不会滥用

---

## 使用示例

### 基本施法

```java
// 施放心灵形态（会打开选择窗口）
if (hero.hasTalent(Talent.MIND_FORM)) {
    MindForm.INSTANCE.onCast(holyTome, hero);
}
```

### 目标选择流程

```java
// 完整的施法流程
HolyTome tome = new HolyTome();
if (tome.canCast(hero, MindForm.INSTANCE)) {
    // 1. 打开三位一体物品选择窗口
    MindForm.INSTANCE.onCast(tome, hero);
    
    // 2. 玩家选择法杖或投掷武器类型
    // 3. 系统创建相应物品并打开目标选择器
    // 4. 玩家选择目标，自动应用效果
}
```

### 效果等级检查

```java
// 检查当前心灵形态等级
int currentLevel = MindForm.itemLevel();
// 根据等级选择最适合的物品类型
if (currentLevel >= 4) {
    // 选择高伤害法杖
} else {
    // 选择控制型法杖或投掷武器
}
```

---

## 注意事项

### 平衡性考虑

1. **高消耗**：3点充能消耗较高，需要谨慎使用
2. **等级投资**：天赋等级显著影响效果强度，值得投资
3. **时机选择**：最佳使用时机是面对特殊敌人类型或需要特定效果时

### 特殊机制

1. **充能来源**：消耗ClassArmor充能，需要圣骑士护甲支持
2. **临时物品**：创建的物品仅用于单次使用，不会添加到背包
3. **效果随机性**：法杖效果可能受随机因素影响（如WonderousResin）

### 技术限制

1. **圣骑士依赖**：完全依赖圣骑士ClassArmor系统
2. **物品创建**：需要正确的物品等级和状态设置
3. **充能同步**：需要精确管理ClassArmor充能消耗

---

## 最佳实践

### 战斗策略

- **元素克制**：根据敌人弱点选择合适的法杖类型
- **控制优先**：在团队战斗中优先选择控制型法杖
- **爆发输出**：在Boss战中选择高伤害法杖进行爆发

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.MIND_FORM) && 
    hero.pointsInTalent(Talent.MIND_FORM) >= 2 &&
    hero.subClass == HeroSubClass.PALADIN) {
    // 高等级心灵形态配合圣骑士护甲，形成强大的远程攻击体系
}
```

### 职业优化

- **圣骑士流派**：
  - 配合高充能护甲，确保频繁使用
  - 选择与护甲协同的法杖效果
- **祭司流派**：虽然无法使用（需要圣骑士），但可了解机制
- **通用策略**：根据战斗需求灵活选择法杖或投掷武器

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `ClericSpell` | 父类 | 所有牧师法术的基类 |
| `Trinity.WndItemtypeSelect` | 依赖 | 三位一体物品选择窗口 |
| `ClassArmor` | 依赖 | 圣骑士护甲，提供充能来源 |
| `Wand` | 效果类型 | 法杖效果实现 |
| `MissileWeapon` | 效果类型 | 投掷武器效果实现 |
| `Ballistica` | 依赖 | 弹道计算系统 |
| `WondrousResin` | 协同 | 提供额外积极效果 |
| `CursedWand` | 依赖 | 诅咒法杖效果系统 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.mindform.name` | "心灵形态" | 法术名称 |
| `spells.mindform.desc` | "进入心灵形态，可以临时使用%d级的法杖或投掷武器效果。" | 法术描述 |
| `spells.mindform.charge_cost` | "%d 充能" | 充能消耗提示 |