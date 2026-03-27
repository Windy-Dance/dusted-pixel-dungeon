# GuidingLight 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/GuidingLight.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 181 |
| **中文名称** | 引导之光 |

---

## 法术概述

`GuidingLight`（引导之光）是牧师职业的1级基础法术。该法术的主要功能是：

1. **核心效果**：对目标敌人造成神圣伤害并施加照亮效果，使其更容易被后续攻击命中
2. **战术价值**：作为基础伤害技能和Debuff工具，提高团队整体输出效率
3. **使用场景**：对付高闪避敌人、精英怪削弱、或作为常规输出手段

**法术类型**：
- **目标类型**：Targeted（需要选择敌人目标）
- **充能消耗**：1点充能（祭司职业免费但有冷却）
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
    
    class TargetedClericSpell {
        +void onCast(HolyTome, Hero)
        +int targetingFlags()
        #abstract void onTargetSelected(HolyTome, Hero, Integer)
    }
    
    class GuidingLight {
        +INSTANCE: GuidingLight
        +int icon()
        +float chargeUse(Hero)
        +String desc()
        #void onTargetSelected(HolyTome, Hero, Integer)
    }
    
    class Illuminated {
        +type = buffType.NEGATIVE
        +int icon()
        +void fx(boolean)
        +String desc()
    }
    
    class WasIlluminatedTracker {
        // 空Buff，用于标记
    }
    
    class GuidingLightPriestCooldown {
        +int icon()
        +void tintIcon(Image)
        +float iconFadePercent()
    }
    
    ClericSpell <|-- TargetedClericSpell
    TargetedClericSpell <|-- GuidingLight
    GuidingLight +-- Illuminated
    GuidingLight +-- WasIlluminatedTracker  
    GuidingLight +-- GuidingLightPriestCooldown
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | GuidingLight.INSTANCE | 单例实例 |

---

## 核心属性

### 充能消耗

| 职业分支 | 充能消耗 | 冷却时间 | 说明 |
|----------|----------|----------|------|
| 普通牧师 | 1点 | 无 | 标准消耗 |
| 祭司 | 0点 | 50回合 | 免费但有长冷却 |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | true | 需要目标选择 |
| `targetingFlags()` | Ballistica.MAGIC_BOLT | 魔法弹道，穿过敌人停止 |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | 无特殊要求 | 基础法术，所有牧师都可使用 |

### 伤害数值

| 伤害范围 | 平均伤害 | 说明 |
|----------|----------|------|
| 2-8点 | 5点 | 固定伤害范围，不受天赋影响 |

---

## 方法详解

### onTargetSelected(HolyTome tome, Hero hero, Integer target)

```java
@Override
protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
    // 处理目标选择和法术效果
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：处理玩家选择的目标，执行伤害计算和照亮效果应用。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄
- `target` (Integer)：选择的目标单元格坐标

**实现逻辑**：
1. **弹道计算**：使用Ballistica计算魔法弹道路径
2. **目标验证**：检查是否为目标自己（防止自伤）
3. **快速瞄准**：设置QuickSlotButton的攻击目标
4. **视觉特效**：播放ZAP音效和Light Missile光束特效
5. **伤害应用**：对碰撞位置的敌人造成2-8点神圣伤害
6. **照亮效果**：施加Illuminated和WasIlluminatedTracker两个Buff
7. **祭司特殊处理**：如果是祭司且无冷却，施加50回合冷却Buff

---

### chargeUse(Hero hero)

```java
@Override
public float chargeUse(Hero hero) {
    if (hero.subClass == HeroSubClass.PRIEST
        && hero.buff(GuidingLightPriestCooldown.class) == null){
        return 0;
    } else {
        return 1;
    }
}
```

**方法作用**：根据英雄职业分支返回不同的充能消耗。

**返回值**：
- `0f`：祭司职业且无冷却时免费施放
- `1f`：其他情况标准消耗

---

### desc()

```java
public String desc() {
    String desc = Messages.get(this, "desc");
    if (Dungeon.hero.subClass == HeroSubClass.PRIEST){
        desc += "\n\n" + Messages.get(this, "desc_priest");
    }
    return desc + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本，包含职业特定信息。

**返回值**：
- 基础描述 + 祭司职业特殊说明 + 充能消耗信息

---

## 内部类

### Illuminated Buff

**作用**：负面Buff，使目标敌人被照亮，更容易被命中。

**关键特性**：
- **Buff类型**：NEGATIVE（负面状态）
- **视觉效果**：在目标精灵上添加ILLUMINATED状态
- **职业差异**：
  - 祭司：提供额外效果描述
  - 其他职业：标准照亮效果
  - 非牧师：通用描述

### WasIlluminatedTracker Buff

**作用**：空Buff，仅用于标记目标曾经被照亮过。

**关键特性**：
- 继承自普通Buff类，无特殊逻辑
- 可能用于其他系统检测目标历史状态

### GuidingLightPriestCooldown Buff

**作用**：祭司职业的引导之光冷却计时器。

**关键特性**：
- **持续时间**：50回合
- **图标显示**：使用照亮图标，亮度减半
- **冷却指示**：图标根据剩余时间淡出
- **界面刷新**：Buff移除时刷新ActionIndicator

---

## 特殊机制

### 职业差异化

- **祭司职业**：
  - 免费施放引导之光
  - 50回合冷却限制
  - 冷却期间显示特殊Buff图标
- **圣骑士职业**：
  - 标准1点充能消耗
  - 无特殊效果
  - 可配合其他圣骑士天赋

### 伤害机制

- **固定范围**：2-8点神圣伤害
- **类型判定**：属于魔法伤害，受相关抗性影响
- **视觉反馈**：白色粒子爆破效果（#FFFFFF44）

### 照亮效果

- **命中提升**：被照亮的敌人更容易被攻击命中
- **状态可见**：目标身上显示发光特效
- **持久标记**：WasIlluminatedTracker确保效果可被追踪

---

## 使用示例

### 基本施法

```java
// 施放引导之光
// 玩家选择目标后自动调用
GuidingLight.INSTANCE.onTargetSelected(tome, hero, targetPos);
```

### 目标选择流程

```java
// 完整的施法流程
HolyTome tome = new HolyTome();
if (tome.canCast(hero, GuidingLight.INSTANCE)) {
    // 1. 打开目标选择器
    GuidingLight.INSTANCE.onCast(tome, hero);
    
    // 2. 玩家选择敌人目标
    // 3. 自动调用 onTargetSelected 处理伤害和照亮
}
```

### 祭司冷却检查

```java
// 检查祭司是否可以免费施放
if (hero.subClass == HeroSubClass.PRIEST) {
    GuidingLight.GuidingLightPriestCooldown cooldown = 
        hero.buff(GuidingLight.GuidingLightPriestCooldown.class);
    if (cooldown == null) {
        // 可以免费施放
    } else {
        // 还在冷却中
        float remainingTime = cooldown.cooldown();
    }
}
```

---

## 注意事项

### 平衡性考虑

1. **基础伤害**：2-8点伤害适中，适合早期游戏
2. **充能效率**：1点充能消耗合理，祭司免费但有冷却平衡
3. **Debuff价值**：照亮效果显著提升团队输出，特别是对高闪避敌人

### 特殊机制

1. **自我保护**：无法对自身施放，防止意外自伤
2. **弹道穿透**：魔法弹道会穿过敌人，在第一个敌人处停止
3. **快速瞄准**：自动设置QuickSlotButton目标，方便后续攻击

### 技术限制

1. **视野要求**：目标必须在英雄视野范围内
2. **碰撞检测**：弹道会在第一个障碍物或敌人处停止
3. **冷却管理**：祭司的冷却Buff需要正确序列化和加载

---

## 最佳实践

### 战斗策略

- **精英怪削弱**：先用引导之光照亮精英怪，再进行全力输出
- **高闪避目标**：专门用来对付具有高闪避能力的敌人
- **连招起手**：作为连招的第一步，为后续高伤害技能创造条件

### 职业优化

- **祭司流派**：
  ```java
  // 祭司可以在冷却结束后立即再次使用
  if (hero.subClass == HeroSubClass.PRIEST && 
      hero.buff(GuidingLight.GuidingLightPriestCooldown.class) == null) {
      // 免费连续使用
  }
  ```
- **圣骑士流派**：配合圣光武器延长，形成持久输出循环
- **通用策略**：在战斗开始前预先照亮关键目标

### 团队协同

- **输出配合**：告知队友优先攻击被照亮的目标
- **时机把握**：在团队集火前施放，最大化Debuff效果
- **资源管理**：合理分配充能，确保关键时刻有引导之光可用

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `TargetedClericSpell` | 父类 | 目标选择法术基类 |
| `Ballistica` | 依赖 | 弹道计算系统 |
| `MagicMissile` | 依赖 | 视觉特效系统 |
| `QuickSlotButton` | 协同 | 快速瞄准系统 |
| `ActionIndicator` | 协同 | 动作指示器，显示祭司冷却 |
| `Wand` | 参考 | 自我目标消息来源 |
| `CharSprite.State.ILLUMINATED` | 依赖 | 视觉状态效果 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.guidinglight.name` | "引导之光" | 法术名称 |
| `spells.guidinglight.desc` | "发射一道神圣光束，对目标造成2-8点伤害并施加照亮效果，使其更容易被命中。" | 法术描述 |
| `spells.guidinglight.desc_priest` | "作为祭司，你可以免费施放此法术，但有50回合的冷却时间。" | 祭司职业特殊说明 |
| `spells.guidinglight.prompt` | "选择要攻击的敌人" | 目标选择提示 |
| `spells.guidinglight.charge_cost` | "%d 充能" | 充能消耗提示 |
| `buffs.illuminated.desc_priest` | "祭司的引导之光使你更容易被所有攻击命中。" | 祭司照亮效果描述 |
| `buffs.illuminated.desc_generic` | "你被神圣之光照亮，更容易被攻击命中。" | 通用照亮效果描述 |