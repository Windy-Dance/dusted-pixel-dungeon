# ShieldOfLight 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/ShieldOfLight.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 146 |
| **中文名称** | 光之护盾 |

---

## 法术概述

`ShieldOfLight`（光之护盾）是牧师职业的2级法术。该法术的主要功能是：

1. **核心效果**：对指定敌人施加光之护盾，将受到的伤害反射回该敌人
2. **战术价值**：提供强大的反制能力，特别针对高伤害的单体敌人
3. **使用场景**：面对高伤害Boss、精英怪或需要反制强力攻击的情况

**法术类型**：
- **目标类型**：Targeted（需要选择敌人目标）
- **充能消耗**：1点充能
- **天赋需求**：SHIELD_OF_LIGHT 天赋

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
    
    class ShieldOfLight {
        +INSTANCE: ShieldOfLight
        +int icon()
        +int targetingFlags()
        +boolean canCast(Hero)
        +String desc()
        #void onTargetSelected(HolyTome, Hero, Integer)
    }
    
    class ShieldOfLightTracker {
        +int object = 0
        +static float DURATION = 5
        +type = buffType.POSITIVE
        +int icon()
        +float iconFadePercent()
    }
    
    ClericSpell <|-- TargetedClericSpell
    TargetedClericSpell <|-- ShieldOfLight
    ShieldOfLight +-- ShieldOfLightTracker
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | ShieldOfLight.INSTANCE | 单例实例 |
| OBJECT | "object" | Bundle存储键，用于保存目标ID |
| DURATION | 5f | Tracker Buff持续时间 |

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
| `targetingFlags()` | Ballistica.STOP_TARGET | 在目标处停止的弹道类型 |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires SHIELD_OF_LIGHT | 施放此法术所需的天赋 |

### 反射伤害

| 天赋等级 | 最小伤害 | 最大伤害 | 说明 |
|----------|----------|----------|------|
| 1点 | 2 | 4 | 基础反射伤害 |
| 2点 | 3 | 6 | 增强反射伤害 |
| 3点 | 4 | 8 | 最大反射伤害 |

**计算公式**: min = 1 + talent_level, max = 2 × min

---

## 方法详解

### onTargetSelected(HolyTome tome, Hero hero, Integer target)

```java
@Override
protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
    // 对目标敌人施加光之护盾
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：处理玩家选择的目标，施加光之护盾效果。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄
- `target` (Integer)：选择的目标单元格坐标

**实现逻辑**：
1. **目标验证**：检查目标是否存在、是否为敌人、且在视野范围内
2. **快速瞄准**：设置QuickSlotButton的目标
3. **Buff应用**：
   - 为英雄施加ShieldOfLightTracker Buff，持续4回合
   - 存储目标敌人的ID用于后续伤害反射
4. **职业效果**：如果是祭司职业，对目标施加照亮效果
5. **生命链接协同**：如果存在生命链接盟友，也为其施加Tracker Buff（持续3回合）
6. **视觉特效**：播放阅读音效和光粒子特效

---

### desc()

```java
@Override
public String desc() {
    int min = 1 + Dungeon.hero.pointsInTalent(Talent.SHIELD_OF_LIGHT);
    int max = 2*min;
    return Messages.get(this, "desc", min, max) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本，包含动态计算的反射伤害范围。

**返回值**：
- 包含最小/最大反射伤害值和充能消耗的完整描述字符串

---

## 内部类 ShieldOfLightTracker

### 类定义

```java
public static class ShieldOfLightTracker extends FlavourBuff {
    public int object = 0;
    private static final float DURATION = 5;
    {
        type = buffType.POSITIVE;
    }
    // ... 其他方法
}
```

**类作用**：跟踪被标记的敌人，实现伤害反射机制。

**关键属性**：
- `object`: int - 存储目标敌人的ID
- `DURATION`: 5f - Buff基础持续时间
- `type`: buffType.POSITIVE - 正面Buff类型

**关键方法**：
- `icon()`: 返回LIGHT_SHIELD图标
- `iconFadePercent()`: 返回图标淡出百分比，用于UI显示
- `storeInBundle()/restoreFromBundle()`: 序列化支持

### 反射机制

- **目标绑定**：通过敌人ID精确绑定到特定目标
- **伤害计算**：受到的伤害按比例反射回目标
- **持续时间**：英雄4回合，盟友3回合，确保平衡性

---

## 特殊机制

### 职业差异化

- **祭司职业**：
  - 对目标施加照亮效果，提升团队命中率
  - 形成"反射+照亮"的完美组合
- **圣骑士职业**：
  - 无特殊效果，但可配合其他圣骑士天赋
  - 专注于纯粹的伤害反射

### 生命链接协同

- **双保险机制**：英雄和生命链接盟友都获得护盾
- **持续时间差异**：英雄4回合，盟友3回合，体现主次关系
- **独立触发**：每个单位独立反射伤害，形成双重反制

### 目标限制

- **敌人专属**：只能对敌对阵营施放
- **视野要求**：目标必须在英雄视野范围内
- **单目标绑定**：一次只能绑定一个目标，确保平衡

---

## 使用示例

### 基本施法

```java
// 施放光之护盾
if (hero.hasTalent(Talent.SHIELD_OF_LIGHT)) {
    // 玩家选择敌人目标后自动调用
    ShieldOfLight.INSTANCE.onTargetSelected(tome, hero, targetPos);
}
```

### 目标选择流程

```java
// 完整的施法流程
HolyTome tome = new HolyTome();
if (tome.canCast(hero, ShieldOfLight.INSTANCE)) {
    // 1. 打开目标选择器
    ShieldOfLight.INSTANCE.onCast(tome, hero);
    
    // 2. 玩家选择敌人目标
    // 3. 自动调用 onTargetSelected 施加护盾
}
```

### Buff状态检查

```java
// 检查光之护盾是否仍然活跃
ShieldOfLight.ShieldOfLightTracker tracker = 
    hero.buff(ShieldOfLight.ShieldOfLightTracker.class);
if (tracker != null) {
    int targetId = tracker.object; // 获取绑定的目标ID
    float remainingTime = tracker.cooldown(); // 获取剩余持续时间
}
```

---

## 注意事项

### 平衡性考虑

1. **低消耗高效**：1点充能消耗提供显著的反制能力
2. **天赋投资**：每点天赋提升反射伤害，值得投资
3. **目标专注**：单目标设计防止滥用，保持游戏平衡

### 特殊机制

1. **ID绑定**：使用角色ID而非位置，确保目标正确性
2. **序列化支持**：跨楼层和存档都能正确保存目标绑定
3. **伤害计算**：精确的伤害反射比例，避免数值失衡

### 技术限制

1. **目标检测**：依赖Actor系统正确识别敌人ID
2. **Buff管理**：需要精确控制两个不同持续时间的Buff
3. **性能考虑**：伤害反射计算需要高效实现

---

## 最佳实践

### 战斗策略

- **Boss战**：对高伤害Boss施加护盾，将伤害反射回去
- **精英优先**：优先对能造成致命伤害的精英怪使用
- **连招配合**：与照亮效果配合，提高团队整体输出效率

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.SHIELD_OF_LIGHT) && 
    hero.pointsInTalent(Talent.SHIELD_OF_LIGHT) >= 2) {
    // 高反射伤害配合生命链接，形成完美的反制体系
}
```

### 职业优化

- **祭司流派**：
  ```java
  // 祭司的照亮效果让反射更有价值
  // 团队可以集中攻击被照亮的目标
  ```
- **圣骑士流派**：配合高生存能力，在前线安全施放护盾
- **通用策略**：合理选择目标，优先高伤害单体敌人

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `TargetedClericSpell` | 父类 | 目标选择法术基类 |
| `Ballistica.STOP_TARGET` | 依赖 | 弹道计算系统 |
| `PowerOfMany` | 依赖 | 力量合一天赋，提供盟友检测 |
| `LifeLinkSpell` | 协同 | 生命链接法术，提供协同效果 |
| `GuidingLight.Illuminated` | 协同 | 祭司职业的照亮Debuff |
| `FlavourBuff` | 依赖 | Buff基类，提供持续效果 |
| `QuickSlotButton` | 协同 | 快速瞄准系统 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.shieldoflight.name` | "光之护盾" | 法术名称 |
| `spells.shieldoflight.desc` | "对目标敌人施加光之护盾，将受到的伤害以%d-%d点的形式反射回去。" | 法术描述 |
| `spells.shieldoflight.prompt` | "选择要施加护盾的敌人" | 目标选择提示 |
| `spells.shieldoflight.no_target` | "没有有效目标" | 目标无效错误提示 |
| `spells.shieldoflight.charge_cost` | "%d 充能" | 充能消耗提示 |