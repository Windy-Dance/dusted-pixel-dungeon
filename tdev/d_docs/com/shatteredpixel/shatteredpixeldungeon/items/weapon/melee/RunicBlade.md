# RunicBlade 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/RunicBlade.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee |
| 类类型 | public class |
| 继承关系 | extends MeleeWeapon |
| 代码行数 | 127 行 |

## 2. 类职责说明
RunicBlade（符文剑）是一种 Tier 4 的特殊近战武器，具有独特的伤害成长曲线：基础伤害较低（相当于Tier 3），但成长伤害较高（相当于Tier 5）。作为决斗家武器，其特殊能力「符文斩击」可以造成巨额伤害加成（300%+50%*等级）。这是一把后期成长性极强的武器。

## 4. 继承与协作关系
```mermaid
classDiagram
    class MeleeWeapon {
        <<abstract>>
        +int tier
        +int min(int lvl)
        +int max(int lvl)
        +duelistAbility(Hero hero, Integer target)
    }
    class RunicBlade {
        +int tier = 4
        +max(int lvl)
        +targetingPrompt()
        +duelistAbility(Hero hero, Integer target)
        +abilityInfo()
        +upgradeAbilityStat(int level)
    }
    class RunicSlashTracker {
        +FlavourBuff
        +float boost
    }
    MeleeWeapon <|-- RunicBlade
    RunicBlade +-- RunicSlashTracker
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无静态常量 | - | - | - |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | 初始化块 | 物品图标，使用 ItemSpriteSheet.RUNIC_BLADE |
| hitSound | String | 初始化块 | 击中音效，使用 Assets.Sounds.HIT_SLASH |
| hitSoundPitch | float | 初始化块 | 音效音高，设为 1f（正常） |
| tier | int | 初始化块 | 武器等级，设为 4 |

## 7. 方法详解

### max
**签名**: `public int max(int lvl)`
**功能**: 计算指定等级下的最大伤害
**参数**: `lvl` - 武器等级
**返回值**: 最大伤害值
**实现逻辑**:
```java
return 5*(tier) +                	// 20基础伤害，低于Tier 4标准的25
       Math.round(lvl*(tier+2));	// 每级+6伤害，高于Tier 4标准的+5
```
符文剑的基础伤害相当于Tier 3，但成长相当于Tier 5。在+5等级时，总伤害与标准Tier 4武器持平。

### targetingPrompt
**签名**: `public String targetingPrompt()`
**功能**: 返回目标选择提示文本
**参数**: 无
**返回值**: 从消息文件获取的提示字符串

### duelistAbility
**签名**: `protected void duelistAbility(Hero hero, Integer target)`
**功能**: 执行决斗家的「符文斩击」能力
**参数**: 
- `hero` - 执行能力的英雄
- `target` - 目标位置
**返回值**: 无
**实现逻辑**:
```java
// 验证目标...
// 施加符文斩击追踪器
RunicSlashTracker tracker = Buff.affect(hero, RunicSlashTracker.class);
tracker.boost = 3f + 0.50f*buffedLvl();  // 300% + 50%*等级的伤害加成

// 执行攻击
if (hero.attack(enemy, 1f, 0, Char.INFINITE_ACCURACY)){
    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
    // ...
}
tracker.detach();  // 攻击后移除buff
```
关键特点：符文斩击追踪器的`boost`值被用于计算伤害倍率。

### abilityInfo
**签名**: `public String abilityInfo()`
**功能**: 返回能力描述信息
**参数**: 无
**返回值**: 能力描述字符串
**实现逻辑**:
```java
if (levelKnown){
    return Messages.get(this, "ability_desc", 300+50*buffedLvl());
} else {
    return Messages.get(this, "typical_ability_desc", 300);
}
```
显示伤害加成百分比（如300%、350%等）。

### upgradeAbilityStat
**签名**: `public String upgradeAbilityStat(int level)`
**功能**: 返回指定等级下的能力统计
**参数**: `level` - 武器等级
**返回值**: 伤害加成百分比字符串
**实现逻辑**: `return "+" + (300+50*level) + "%";`

## 内部类

### RunicSlashTracker
**类型**: public static class extends FlavourBuff
**功能**: 符文斩击追踪器，存储伤害加成倍率
**字段**:
| 字段 | 类型 | 说明 |
|------|------|------|
| boost | float | 伤害加成倍率（默认2f） |

**说明**: 这是一个轻量级的标记buff，用于在攻击时应用伤害加成。

## 11. 使用示例
```java
// 创建一把符文剑
RunicBlade blade = new RunicBlade();
// Tier 4武器，低基础伤害但高成长
// 决斗家可以使用「符文斩击」造成巨额伤害

hero.belongings.weapon = blade;
// 升级武器可以获得更高的伤害成长
// 能力可以造成300%+的额外伤害
```

## 注意事项
- 基础伤害较低（20 vs 标准25）
- 成长伤害较高（+6/级 vs 标准+5）
- 能力造成百分比伤害加成而非固定加成
- 在+5等级时总伤害与标准Tier 4武器持平
- 能力使用无限准确度确保命中

## 最佳实践
- 优先升级武器以获得更高的成长收益
- 对高生命值敌人使用能力效果更佳
- 后期武器成长性极强
- 符文斩击的伤害加成是百分比，配合高基础伤害效果更好