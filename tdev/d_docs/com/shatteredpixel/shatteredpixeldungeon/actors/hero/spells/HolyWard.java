# HolyWard 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/HolyWard.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends ClericSpell |
| **代码行数** | 112 |
| **中文名称** | 神圣护甲 |

---

## 法术概述

`HolyWard`（神圣护甲）是牧师职业的1级基础法术。该法术的主要功能是：

1. **核心效果**：为英雄施加神圣护甲Buff，增强其护甲的防御能力
2. **战术价值**：提供稳定的防御增益，特别是在持久战斗中发挥重要作用
3. **使用场景**：Boss战、精英怪遭遇战、或任何需要提升生存能力的情况

**法术类型**：
- **目标类型**：Self-Targeted（自我目标）
- **充能消耗**：2点充能
- **天赋需求**：无（基础法术，所有牧师都可使用）

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
    
    class HolyWard {
        +INSTANCE: HolyWard
        +int icon()
        +void onCast(HolyTome, Hero)
        +String desc()
    }
    
    class HolyArmBuff {
        +static float DURATION = 50f
        +type = buffType.POSITIVE
        +int icon()
        +float iconFadePercent()
        +String desc()
        +void detach()
        +void extend(float)
    }
    
    ClericSpell <|-- HolyWard
    HolyWard +-- HolyArmBuff
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | HolyWard.INSTANCE | 单例实例 |
| DURATION | 50f | 神圣护甲Buff持续时间（回合数） |

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
| `canCast()` | 无特殊要求 | 基础法术，所有牧师都可使用 |

### 职业差异化

| 职业分支 | 效果差异 | 说明 |
|----------|----------|------|
| 普通牧师 | 50回合持续时间 | 标准效果 |
| 圣骑士 | 可延长至100回合 | 通过其他法术延长持续时间 |

---

## 方法详解

### onCast(HolyTome tome, Hero hero)

```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    // 应用神圣护甲Buff并显示视觉特效
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：执行神圣护甲的主要逻辑，施加Buff并显示视觉效果。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄

**实现逻辑**：
1. **Buff应用**：为英雄施加HolyArmBuff，持续50回合
2. **界面更新**：调用`Item.updateQuickslot()`刷新快捷栏显示
3. **视觉特效**：播放阅读音效和护甲附魔特效（如果装备了护甲）
4. **完成施法**：调用`onSpellCast(tome, hero)`处理后续逻辑

---

### desc()

```java
@Override
public String desc() {
    String desc = Messages.get(this, "desc");
    if (Dungeon.hero.subClass == HeroSubClass.PALADIN){
        desc += "\n\n" + Messages.get(this, "desc_paladin");
    }
    return desc + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本，包含职业特定信息。

**返回值**：
- 基础描述 + 圣骑士职业特殊说明 + 充能消耗信息

---

### extend(float extension) (HolyArmBuff)

```java
public void extend(float extension) {
    if (cooldown()+extension <= 2*DURATION){
        spend(extension);
    } else {
        postpone(2*DURATION);
    }
}
```

**方法作用**：延长神圣护甲Buff的持续时间。

**参数**：
- `extension` (float)：要延长的回合数

**实现逻辑**：
- 最多可延长至100回合（2×DURATION）
- 如果延长后不超过上限，直接增加持续时间
- 如果超过上限，设置为最大值100回合

---

## 内部类 HolyArmBuff

### 类定义

```java
public static class HolyArmBuff extends FlavourBuff {
    public static final float DURATION = 50f;
    {
        type = buffType.POSITIVE;
    }
    // ... 其他方法
}
```

**类作用**：实现神圣护甲的实际效果，包括持续时间管理和视觉显示。

**关键属性**：
- `DURATION`: 50f - Buff基础持续时间
- `type`: buffType.POSITIVE - 正面Buff类型

**关键方法**：
- `icon()`: 返回HOLY_ARMOR图标
- `iconFadePercent()`: 返回图标淡出百分比，用于UI显示
- `desc()`: 返回包含剩余时间的职业特定描述
- `detach()`: Buff结束时刷新快捷栏
- `extend()`: 允许其他系统延长Buff持续时间

### 职业差异化

**普通牧师**：
- 标准50回合持续时间
- 基础神圣护甲效果描述

**圣骑士**：
- 可通过其他法术延长至100回合
- 特殊描述文本强调圣骑士的强化效果
- 与其他圣骑士天赋形成协同效应

---

## 使用示例

### 基本施法

```java
// 施放神圣护甲
HolyWard.INSTANCE.onCast(holyTome, hero);
```

### 持续时间延长

```java
// 圣骑士可以通过其他法术延长神圣护甲
HolyWard.HolyArmBuff holyArm = hero.buff(HolyWard.HolyArmBuff.class);
if (holyArm != null) {
    holyArm.extend(10f); // 延长10回合
}
```

### Buff状态检查

```java
// 检查神圣护甲是否仍然活跃
HolyWard.HolyArmBuff holyArm = hero.buff(HolyWard.HolyArmBuff.class);
if (holyArm != null) {
    float remainingTime = holyArm.cooldown(); // 获取剩余持续时间
}
```

---

## 注意事项

### 平衡性考虑

1. **中等消耗**：2点充能消耗适中，适合定期使用
2. **持久效果**：50回合持续时间足够覆盖大部分战斗
3. **圣骑士强化**：圣骑士可以获得双倍持续时间，体现职业特色

### 特殊机制

1. **延长限制**：最多只能延长到100回合，防止无限叠加
2. **视觉反馈**：装备护甲时显示附魔特效，增强游戏体验
3. **界面同步**：Buff状态变化时自动刷新快捷栏显示

### 技术限制

1. **护甲依赖**：视觉特效需要装备护甲才能显示
2. **序列化支持**：Buff状态正确保存和加载
3. **性能考虑**：长时间Buff对性能影响极小

---

## 最佳实践

### 战斗策略

- **战斗开始**：在进入Boss战或精英战斗前预先施放
- **持久防御**：配合高防御护甲，最大化生存能力
- **资源管理**：合理分配充能，确保关键时刻有神圣护甲可用

### 职业优化

- **圣骑士流派**：
  ```java
  // 圣骑士可以多次延长神圣护甲
  if (hero.subClass == HeroSubClass.PALADIN) {
      // 通过其他法术不断延长，维持永久神圣护甲
  }
  ```
- **祭司流派**：配合治疗法术，形成完整的生存体系
- **通用策略**：与其他防御Buff叠加使用，形成复合防御

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.subClass == HeroSubClass.PALADIN && 
    hero.hasTalent(Talent.HOLY_WARD_EXTENSION)) {
    // 圣骑士配合延长天赋，实现超长持续时间
}
```

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `ClericSpell` | 父类 | 所有牧师法术的基类 |
| `FlavourBuff` | Buff父类 | 提供持续效果的基础Buff类 |
| `Item.updateQuickslot()` | 依赖 | 快捷栏更新系统 |
| `Enchanting.show()` | 依赖 | 护甲附魔视觉特效 |
| `HeroSubClass.PALADIN` | 依赖 | 圣骑士职业，提供延长效果 |
| `HolyWeapon` | 协同 | 神圣武器，形成攻防一体的圣骑士体系 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.holyward.name` | "神圣护甲" | 法术名称 |
| `spells.holyward.desc` | "为你的护甲注入神圣力量，增强其防御能力，持续%d回合。" | 法术描述 |
| `spells.holyward.desc_paladin` | "作为圣骑士，你可以通过其他法术延长神圣护甲的效果，最长可达100回合。" | 圣骑士职业特殊说明 |
| `spells.holyward.charge_cost` | "%d 充能" | 充能消耗提示 |
| `buffs.holyarmbuff.desc` | "你的护甲被神圣力量强化，剩余%d回合。" | Buff描述 |
| `buffs.holyarmbuff.desc_paladin` | "圣骑士的神圣护甲效果将持续%d回合。" | 圣骑士Buff描述 |