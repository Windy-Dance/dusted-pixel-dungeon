# MnemonicPrayer 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/MnemonicPrayer.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 197 |
| **中文名称** | 记忆祈祷 |

---

## 法术概述

`MnemonicPrayer`（记忆祈祷）是牧师职业的3级法术。该法术的主要功能是：

1. **核心效果**：延长目标身上所有正面Buff的持续时间（盟友）或负面Buff的持续时间（敌人）
2. **战术价值**：显著增强团队Buff效果的持久性，或延长敌人Debuff的控制时间
3. **使用场景**：关键Buff即将结束时、需要延长控制效果、或最大化团队增益持续时间

**法术类型**：
- **目标类型**：Targeted（需要选择目标角色）
- **充能消耗**：1点充能
- **天赋需求**：MNEMONIC_PRAYER 天赋

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
    
    class MnemonicPrayer {
        +INSTANCE: MnemonicPrayer
        +int icon()
        +int targetingFlags()
        +boolean canCast(Hero)
        +String desc()
        #void onTargetSelected(HolyTome, Hero, Integer)
        #void affectChar(Char, float)
    }
    
    ClericSpell <|-- TargetedClericSpell
    TargetedClericSpell <|-- MnemonicPrayer
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | MnemonicPrayer.INSTANCE | 单例实例 |

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
| `canCast()` | requires MNEMONIC_PRAYER | 施放此法术所需的天赋 |

### 延长效果

| 天赋等级 | 延长回合数 | 说明 |
|----------|------------|------|
| 1点 | 3回合 | 基础延长效果 |
| 2点 | 4回合 | 增强延长效果 |
| 3点 | 5回合 | 最大延长效果 |

**计算公式**: `extension = 2 + talent_points`

---

## 方法详解

### onTargetSelected(HolyTome tome, Hero hero, Integer target)

```java
@Override
protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
    // 延长目标身上的Buff持续时间
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：处理玩家选择的目标，延长其身上所有符合条件的Buff持续时间。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄
- `target` (Integer)：选择的目标单元格坐标

**实现逻辑**：
1. **目标验证**：检查目标是否存在且在视野范围内
2. **快速瞄准**：设置QuickSlotButton的目标
3. **效果应用**：调用`affectChar()`延长Buff持续时间
4. **生命链接协同**：如果存在生命链接盟友，复制效果到另一方
5. **动画处理**：
   - 对英雄自身：执行操作动画
   - 对其他目标：执行ZAP动画

---

### affectChar(Char ch, float extension)

```java
private void affectChar(Char ch, float extension) {
    // 根据目标阵营延长相应类型的Buff
}
```

**方法作用**：根据目标阵营延长正面或负面Buff的持续时间。

**参数**：
- `ch` (Char)：目标角色
- `extension` (float)：要延长的回合数

**实现逻辑**：
- **盟友效果**：
  - 播放充能音效和上升粒子特效
  - 遍历所有正面Buff，延长符合条件的Buff
  - 应用mnemonicExtended标志防止重复延长
- **敌人效果**：
  - 播放Debuff音效和下降粒子特效
  - 施加照亮效果（GuidingLight.Illuminated）
  - 遍历所有负面Buff，延长符合条件的Buff
  - 应用mnemonicExtended标志防止重复延长

### 特殊排除机制

- **不延长的Buff类型**：
  - Trinity系统Buff（BodyForm、SpiritForm等）
  - AscendedForm.AscendBuff
  - PowerOfMany.PowerBuff
  - BeamingRay.BeamingRayBoost
  - LifeLink相关Buff
- **支持的Buff类型**：
  - 所有FlavourBuff子类
  - 特定Buff类（AdrenalineSurge、ArcaneArmor、Barkskin等）
  - 敌人Debuff（Bleeding、Burning、Poison等）

---

### desc()

```java
public String desc() {
    return Messages.get(this, "desc", 2 + Dungeon.hero.pointsInTalent(Talent.MNEMONIC_PRAYER)) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本，包含动态计算的延长回合数。

**返回值**：
- 包含延长回合数和充能消耗的完整描述字符串

---

## 特殊机制

### Buff类型差异化

- **正面Buff延长**：针对盟友的所有正面Buff（除特殊排除外）
- **负面Buff延长**：针对敌人的所有负面Buff
- **视觉反馈**：不同的粒子特效区分延长方向（上升vs下降）

### 生命链接协同

- **智能复制**：如果对英雄施放，同时延长盟友的Buff
- **反向复制**：如果对盟友施放，同时延长英雄的Buff
- **效果同步**：确保生命链接双方获得相同的延长效果

### 防滥用机制

- **重复标记**：mnemonicExtended标志防止同一Buff被多次延长
- **特殊排除**：关键系统Buff（如三位一体、升天形态）不受影响
- **阵营过滤**：严格区分正面/负面Buff，避免意外效果

---

## 使用示例

### 基本施法

```java
// 施放记忆祈祷
if (hero.hasTalent(Talent.MNEMONIC_PRAYER)) {
    // 玩家选择目标后自动调用
    MnemonicPrayer.INSTANCE.onTargetSelected(tome, hero, targetPos);
}
```

### 关键Buff延长

```java
// 在重要Buff即将结束时使用
Bless bless = hero.buff(Bless.class);
if (bless != null && bless.cooldown() < 5) {
    // 使用记忆祈祷延长祝福效果
}
```

### 敌人控制延长

```java
// 延长敌人身上的中毒、燃烧等Debuff
// 提高团队输出效率和控制能力
MnemonicPrayer.INSTANCE.onTargetSelected(tome, hero, enemyPos);
```

---

## 注意事项

### 平衡性考虑

1. **低消耗高效**：1点充能消耗提供显著的Buff延长效果
2. **天赋投资**：每点天赋增加1回合延长，值得投资
3. **时机关键**：最佳使用时机是在关键Buff即将结束前

### 特殊机制

1. **排除列表**：某些关键系统Buff不受影响，保持游戏平衡
2. **防重复**：同一Buff不会被多次延长，避免无限叠加
3. **阵营识别**：自动识别目标阵营，正确应用延长效果

### 技术限制

1. **Buff类型检测**：需要精确的Buff类型检查和转换
2. **序列化支持**：mnemonicExtended标志需要正确保存和加载
3. **性能考虑**：遍历所有Buff可能对性能有轻微影响

---

## 最佳实践

### 战斗策略

- **Buff维护**：在关键增益Buff（如Bless、Haste）即将结束时使用
- **Debuff延长**：延长敌人身上的控制效果，为团队创造输出窗口
- **紧急支援**：在团队Buff被移除前紧急延长

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.MNEMONIC_PRAYER) && 
    hero.pointsInTalent(Talent.MNEMONIC_PRAYER) >= 2 &&
    hero.hasTalent(Talent.BLESS)) {
    // 长时间祝福配合记忆祈祷，形成超长Buff持续时间
}
```

### 职业优化

- **祭司流派**：配合治疗和增益Buff，最大化团队生存能力
- **圣骑士流派**：延长防御和攻击Buff，形成持久战斗能力
- **通用策略**：优先延长高价值Buff，如屏障、祝福、急速等

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `TargetedClericSpell` | 父类 | 目标选择法术基类 |
| `Ballistica.STOP_TARGET` | 依赖 | 弹道计算系统 |
| `PowerOfMany` | 依赖 | 力量合一天赋，提供盟友检测 |
| `LifeLinkSpell` | 协同 | 生命链接法术，提供效果复制 |
| `GuidingLight.Illuminated` | 依赖 | 照亮Debuff效果 |
| `FlavourBuff` | 依赖 | Buff基类，支持延长机制 |
| `QuickSlotButton` | 协同 | 快速瞄准系统 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.mnemonicprayer.name` | "记忆祈祷" | 法术名称 |
| `spells.mnemonicprayer.desc` | "延长目标身上所有Buff的持续时间%d回合。对盟友延长正面效果，对敌人延长负面效果。" | 法术描述 |
| `spells.mnemonicprayer.prompt` | "选择要延长Buff的目标" | 目标选择提示 |
| `spells.mnemonicprayer.no_target` | "没有有效目标" | 目标无效错误提示 |
| `spells.mnemonicprayer.charge_cost` | "%d 充能" | 充能消耗提示 |