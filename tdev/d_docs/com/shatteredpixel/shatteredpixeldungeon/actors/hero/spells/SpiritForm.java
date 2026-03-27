# SpiritForm 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/SpiritForm.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends ClericSpell |
| **代码行数** | 258 |
| **中文名称** | 灵魂形态 |

---

## 法术概述

`SpiritForm`（灵魂形态）是牧师职业的4级法术，属于三位一体（Trinity）系统的一部分。该法术的主要功能是：

1. **核心效果**：激活灵魂形态，允许英雄临时使用高等级戒指或神器的效果
2. **战术价值**：提供灵活的属性增强和特殊能力，在不同战斗场景中切换战略
3. **使用场景**：需要临时属性提升、特殊神器效果、或应对特定战斗情况时

**法术类型**：
- **目标类型**：Self-Targeted（自我目标）
- **充能消耗**：4点充能（最高消耗）
- **天赋需求**：SPIRIT_FORM 天赋

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
    
    class SpiritForm {
        +INSTANCE: SpiritForm
        +int icon()
        +String desc()
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +void onCast(HolyTome, Hero)
        +static int ringLevel()
        +static int artifactLevel()
        +static void applyActiveArtifactEffect(ClassArmor, Artifact)
    }
    
    class SpiritFormBuff {
        +Bundlable effect
        +static float DURATION = 20f
        +type = buffType.POSITIVE
        +int icon()
        +void tintIcon(Image)
        +float iconFadePercent()
        +Ring ring()
        +Artifact artifact()
    }
    
    ClericSpell <|-- SpiritForm
    SpiritForm +-- SpiritFormBuff
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | SpiritForm.INSTANCE | 单例实例 |
| EFFECT | "effect" | Bundle存储键，用于保存效果类型 |
| DURATION | 20f | Buff持续时间 |

---

## 核心属性

### 充能消耗

| 属性 | 值 | 说明 |
|------|-----|------|
| `chargeUse()` | 4f | 每次施放消耗4点充能（游戏最高消耗） |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | false | 无需目标选择，自动激活形态 |
| `targetingFlags()` | -1 | 无目标选择标志 |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires SPIRIT_FORM | 施放此法术所需的天赋 |

### 效果等级

| 天赋等级 | 戒指等级 | 神器等级 | 说明 |
|----------|----------|----------|------|
| 1点 | 1级 | 4级 | 基础灵魂形态 |
| 2点 | 2级 | 6级 | 增强效果 |
| 3点 | 3级 | 8级 | 最强效果 |

**等级计算**: 
- 戒指等级: `ringLevel() = talent_points`
- 神器等级: `artifactLevel() = 2 + 2*talent_points`

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
2. 玩家在窗口中选择要使用的物品类型（戒指或神器）
3. 选择完成后，通过 `SpiritFormBuff` 处理后续效果应用

---

### SpiritFormBuff.setEffect(Bundlable effect)

```java
public void setEffect(Bundlable effect) {
    // 设置并配置选中的效果
}
```

**方法作用**：设置选中的戒指或神器效果，并进行相应配置。

**参数**：
- `effect` (Bundlable)：选中的戒指或神器实例

**实现逻辑**：
- **戒指处理**：设置戒指等级为天赋等级
- **力量之戒特殊处理**：更新英雄最大生命值
- **神器处理**：设置神器升级等级为artifactLevel()

---

### applyActiveArtifactEffect(ClassArmor armor, Artifact effect)

```java
public static void applyActiveArtifactEffect(ClassArmor armor, Artifact effect) {
    // 处理各种神器的主动效果
}
```

**方法作用**：处理不同神器的主动效果激活逻辑。

**支持的神器**：
- **炼金术士工具包**：切换到炼金场景
- **干枯玫瑰**：召唤强化幽灵盟友
- **以太锁链**：打开目标选择器
- **丰饶号角**：触发进食效果
- **大师窃贼臂章**：打开目标选择器
- **自然凉鞋**：随机种子效果并选择目标
- **先知护符**：打开预言目标选择器
- **时光沙漏**：施加时间气泡Buff
- **不稳定法典**：触发阅读效果
- **骷髅钥匙**：打开目标选择器

---

### ring()/artifact()

```java
public Ring ring() { /* 返回配置的戒指 */ }
public Artifact artifact() { /* 返回配置的神器 */ }
```

**方法作用**：返回当前配置的戒指或神器实例，确保正确等级设置。

**返回值**：
- `ring()`: 配置的戒指实例（等级=天赋等级）
- `artifact()`: 配置的神器实例（升级等级=artifactLevel()）

---

## 内部类 SpiritFormBuff

### 类定义

```java
public static class SpiritFormBuff extends FlavourBuff {
    private Bundlable effect;
    {
        type = buffType.POSITIVE;
    }
    // ... 其他方法
}
```

**类作用**：实现灵魂形态的实际效果，管理选中的戒指或神器。

**关键属性**：
- `effect`: Bundlable - 存储选中的戒指或神器
- `DURATION`: 20f - Buff固定持续时间
- `type`: buffType.POSITIVE - 正面Buff类型

**关键方法**：
- `icon()`: 返回TRINITY_FORM图标
- `tintIcon()`: 绿色着色（0,1,0）
- `desc()`: 返回包含具体效果名称的描述文本
- `storeInBundle()/restoreFromBundle()`: 序列化支持

### 特殊机制

**力量之戒协同**：
- 应用时更新英雄最大生命值
- 移除时恢复原始生命值
- 确保属性变化的正确性

**神器升级系统**：
- 自动将神器升级到artifactLevel()
- 保留原有升级，只补充不足的部分
- 支持所有主动效果神器

---

## 特殊机制

### 三位一体系统

- **形态选择**：与BodyForm、MindForm共同构成三位一体系统
- **等级差异化**：戒指等级=天赋等级，神器等级=2+2*天赋等级
- **效果持久性**：20回合的固定持续时间，确保平衡性

### 主动效果处理

- **神器兼容性**：支持所有具有主动效果的神器
- **目标选择集成**：正确集成QuickSlotButton的目标选择系统
- **场景切换**：支持炼金术士工具包的场景切换功能

### 属性管理

- **动态属性**：力量之戒等属性戒指实时影响英雄属性
- **生命值同步**：力量之戒的生命值变化正确同步
- **等级匹配**：确保戒指和神器的等级与天赋投资匹配

---

## 使用示例

### 基本施法

```java
// 施放灵魂形态（会打开选择窗口）
if (hero.hasTalent(Talent.SPIRIT_FORM)) {
    SpiritForm.INSTANCE.onCast(holyTome, hero);
}
```

### 效果选择流程

```java
// 完整的施法流程
HolyTome tome = new HolyTome();
if (tome.canCast(hero, SpiritForm.INSTANCE)) {
    // 1. 打开三位一体物品选择窗口
    SpiritForm.INSTANCE.onCast(tome, hero);
    
    // 2. 玩家选择戒指或神器类型
    // 3. 系统创建相应等级的物品并应用效果
}
```

### 效果等级检查

```java
// 检查当前灵魂形态等级
int ringLvl = SpiritForm.ringLevel();      // 1-3级
int artifactLvl = SpiritForm.artifactLevel(); // 4-8级

// 根据等级选择最适合的物品类型
if (artifactLvl >= 6) {
    // 选择高等级神器获得更强效果
} else {
    // 选择戒指获得稳定属性提升
}
```

---

## 注意事项

### 平衡性考虑

1. **高消耗高回报**：4点充能消耗是游戏中最高，但提供强大的临时效果
2. **天赋投资**：天赋等级显著影响效果强度，值得高投资
3. **时机选择**：最佳使用时机是在面对特殊挑战或需要特定效果时

### 特殊机制

1. **属性同步**：力量之戒的生命值变化需要正确同步
2. **神器升级**：神器升级系统确保不会覆盖已有升级
3. **主动效果**：各种神器的主动效果需要正确集成目标选择系统

### 技术限制

1. **圣骑士依赖**：虽然不直接依赖，但通常与圣骑士ClassArmor协同使用
2. **序列化支持**：需要正确保存和加载Bundlable效果对象
3. **性能考虑**：20回合的持续时间对性能影响极小

---

## 最佳实践

### 战斗策略

- **属性提升**：选择力量之戒等属性戒指提升基础能力
- **特殊能力**：选择时光沙漏等神器获得特殊战斗能力
- **场景适应**：根据当前战斗需求灵活选择戒指或神器

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.SPIRIT_FORM) && 
    hero.pointsInTalent(Talent.SPIRIT_FORM) >= 2) {
    // 高等级灵魂形态配合其他三位一体形态
    // 形成完整的形态切换体系
}
```

### 职业优化

- **圣骑士流派**：
  ```java
  // 灵魂形态配合圣骑士护甲
  // 在关键时刻提供决定性的属性提升或特殊能力
  ```
- **祭司流派**：虽然较少使用，但可了解机制
- **通用策略**：合理规划使用时机，确保在最关键时刻发挥作用

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `ClericSpell` | 父类 | 所有牧师法术的基类 |
| `Trinity.WndItemtypeSelect` | 依赖 | 三位一体物品选择窗口 |
| `ClassArmor` | 协同 | 圣骑士护甲，可能提供充能来源 |
| `Ring` | 效果类型 | 戒指效果实现 |
| `Artifact` | 效果类型 | 神器效果实现 |
| `Corruption` | 依赖 | 干枯玫瑰召唤幽灵的腐化效果 |
| `Swiftthistle.TimeBubble` | 依赖 | 时光沙漏的时间气泡效果 |
| `AlchemyScene` | 依赖 | 炼金术士工具包的场景切换 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.spiritform.name` | "灵魂形态" | 法术名称 |
| `spells.spiritform.desc` | "进入灵魂形态，可以临时使用%d级戒指或%d级神器的效果。" | 法术描述 |
| `spells.spiritform.charge_cost` | "%d 充能" | 充能消耗提示 |
| `buffs.spiritformbuff.desc` | "%s效果，剩余%d回合。" | Buff描述 |