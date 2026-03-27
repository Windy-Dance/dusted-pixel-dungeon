# Radiance 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/Radiance.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends ClericSpell |
| **代码行数** | 90 |
| **中文名称** | 辐射 |

---

## 法术概述

`Radiance`（辐射）是牧师职业的祭司专属法术。该法术的主要功能是：

1. **核心效果**：对所有可见敌人施加照亮和麻痹效果，已照亮的敌人受到额外神圣伤害
2. **战术价值**：提供强大的区域控制能力，同时削弱敌人输出并增强团队命中率
3. **使用场景**：面对大量敌人、需要群体控制、或配合团队输出时

**法术类型**：
- **目标类型**：Area Effect（区域效果，影响所有可见敌人）
- **充能消耗**：2点充能
- **天赋需求**：无天赋要求，但仅限祭司职业使用

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
    
    class Radiance {
        +INSTANCE: Radiance
        +int icon()
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +void onCast(HolyTome, Hero)
    }
    
    ClericSpell <|-- Radiance
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | Radiance.INSTANCE | 单例实例 |

---

## 核心属性

### 充能消耗

| 属性 | 值 | 说明 |
|------|-----|------|
| `chargeUse()` | 2f | 每次施放消耗2点充能 |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | false | 无需目标选择，自动影响所有可见敌人 |
| `targetingFlags()` | -1 | 无目标选择标志 |

### 职业限制

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | 仅限祭司职业 | 只有HeroSubClass.PRIEST可以施放 |

### 效果数值

| 效果 | 数值 | 说明 |
|------|------|------|
| **麻痹持续时间** | 3回合 | 所有受影响敌人的麻痹时间 |
| **额外伤害** | 英雄等级+5 | 对已照亮敌人的神圣伤害 |
| **光照延长** | 20回合（黑暗挑战）/100回合（普通） | 在视野距离<6时延长光照 |

---

## 方法详解

### onCast(HolyTome tome, Hero hero)

```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    // 对所有可见敌人施加照亮、麻痹和伤害效果
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：执行辐射的主要逻辑，对所有可见敌人应用多种效果。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄

**实现逻辑**：
1. **视觉特效**：全屏白色闪光（#80FFFFFF）和爆炸音效
2. **光照管理**：
   - 如果当前视野距离<6，延长Light Buff持续时间
   - 黑暗挑战下延长20回合，普通情况下延长100回合
3. **敌人处理**：
   - 遍历所有Mob，找到视野内且非盟友的敌人
   - 对已照亮的敌人造成英雄等级+5点神圣伤害
   - 对未照亮的敌人施加Illuminated和WasIlluminatedTracker两个Buff
   - 对活跃的敌人施加3回合麻痹效果
4. **完成施法**：花费1回合时间并调用onSpellCast

---

### canCast(Hero hero)

```java
@Override
public boolean canCast(Hero hero) {
    return super.canCast(hero) && hero.subClass == HeroSubClass.PRIEST;
}
```

**方法作用**：检查英雄是否可以施放辐射法术。

**返回值**：
- `true`：英雄是祭司职业且未被魔法免疫
- `false`：英雄不是祭司职业

---

## 特殊机制

### 祭司专属机制

- **职业限定**：只有祭司职业可以使用，体现职业特色
- **无天赋要求**：作为基础职业法术，不需要额外天赋投资
- **协同效应**：与其他祭司法术形成完整的支援体系

### 伤害机制

- **双重视觉**：已照亮敌人受到额外伤害，未照亮敌人先被照亮
- **等级成长**：额外伤害随英雄等级提升（lvl+5）
- **神圣属性**：伤害类型为神圣伤害，受相关抗性影响

### 控制机制

- **群体麻痹**：所有受影响敌人都被麻痹3回合
- **智能激活**：只对活跃的敌人施加麻痹，避免浪费效果
- **连招配合**：照亮+麻痹为团队创造完美的输出窗口

---

## 使用示例

### 基本施法

```java
// 施放辐射（仅限祭司职业）
if (hero.subClass == HeroSubClass.PRIEST) {
    Radiance.INSTANCE.onCast(holyTome, hero);
}
```

### 团队协同

```java
// 辐射会自动影响所有可见敌人
// 无需手动选择目标，系统自动处理
Radiance.INSTANCE.onCast(tome, hero);
```

### 效果检查

```java
// 检查敌人是否已被照亮
GuidingLight.Illuminated illuminated = enemy.buff(GuidingLight.Illuminated.class);
if (illuminated != null) {
    // 敌人会被造成额外伤害
} else {
    // 敌人会被施加照亮效果
}
```

---

## 注意事项

### 平衡性考虑

1. **中等消耗**：2点充能消耗合理，适合定期使用
2. **职业限定**：仅限祭司使用，保持职业差异化
3. **范围限制**：只能影响视野内的敌人，需要合理站位

### 特殊机制

1. **光照协同**：在低视野环境下提供光照延长，适应不同游戏模式
2. **伤害递增**：随英雄等级提升的伤害确保后期依然有效
3. **控制优先**：麻痹效果确保敌人无法反击，提高团队生存率

### 技术限制

1. **视野检测**：依赖Dungeon.level.heroFOV正确计算可见区域
2. **阵营识别**：正确识别敌对阵营，避免误伤盟友
3. **Buff管理**：需要精确管理多个Buff的叠加和移除

---

## 最佳实践

### 战斗策略

- **开场控制**：战斗开始时立即使用，创造完美输出环境
- **精英围攻**：面对多个精英怪时提供关键的群体控制
- **Boss战准备**：在Boss战开始前使用，确保第一轮攻击命中率

### 职业优化

- **祭司流派**：
  ```java
  // 祭司可以在任何战斗中使用辐射
  // 形成完整的"照亮-输出-控制"循环
  ```
- **团队协作**：告知队友辐射已使用，协调输出时机
- **位置选择**：站在能看见最多敌人的位置施放，最大化效果

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.subClass == HeroSubClass.PRIEST && 
    hero.hasTalent(Talent.HOLY_WEAPON)) {
    // 辐射的照亮效果配合神圣武器，形成完美的输出循环
}
```

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `ClericSpell` | 父类 | 所有牧师法术的基类 |
| `GuidingLight.Illuminated` | 依赖 | 照亮Debuff效果 |
| `Paralysis` | 依赖 | 麻痹控制效果 |
| `Light` | 依赖 | 光照Buff系统 |
| `Challenges.DARKNESS` | 依赖 | 黑暗挑战模式检测 |
| `Dungeon.level.heroFOV` | 依赖 | 视野检测系统 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.radiance.name` | "辐射" | 法术名称 |
| `spells.radiance.charge_cost` | "%d 充能" | 充能消耗提示 |