# Scimitar 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/Scimitar.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee |
| 类类型 | public class |
| 继承关系 | extends MeleeWeapon |
| 代码行数 | 91 行 |

## 2. 类职责说明
Scimitar（弯刀）是一种 Tier 3 的近战武器，具有较快的攻击速度（DLY=0.8f，约1.25倍速）。作为决斗家武器，其特殊能力「剑舞」可以让英雄进入持续数回合的剑舞状态，期间攻击更加致命。弯刀是快速攻击型武器的代表。

## 4. 继承与协作关系
```mermaid
classDiagram
    class MeleeWeapon {
        <<abstract>>
        +int tier
        +float DLY
        +int min(int lvl)
        +int max(int lvl)
        +duelistAbility(Hero hero, Integer target)
    }
    class Scimitar {
        +int tier = 3
        +float DLY = 0.8f
        +max(int lvl)
        +duelistAbility(Hero hero, Integer target)
        +abilityInfo()
        +upgradeAbilityStat(int level)
    }
    class SwordDance {
        +FlavourBuff
        +icon()
        +iconFadePercent()
    }
    MeleeWeapon <|-- Scimitar
    Scimitar +-- SwordDance
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无静态常量 | - | - | - |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | 初始化块 | 物品图标，使用 ItemSpriteSheet.SCIMITAR |
| hitSound | String | 初始化块 | 击中音效，使用 Assets.Sounds.HIT_SLASH |
| hitSoundPitch | float | 初始化块 | 音效音高，设为 1.2f（较高） |
| tier | int | 初始化块 | 武器等级，设为 3 |
| DLY | float | 初始化块 | 攻击延迟，设为 0.8f（1.25倍速） |

## 7. 方法详解

### max
**签名**: `public int max(int lvl)`
**功能**: 计算指定等级下的最大伤害
**参数**: `lvl` - 武器等级
**返回值**: 最大伤害值
**实现逻辑**:
```java
return 4*(tier+1) +    // 16基础伤害，低于标准的20
       lvl*(tier+1);   // 每级+4伤害，标准成长
```
弯刀的伤害较低，补偿较快的攻击速度。

### duelistAbility
**签名**: `protected void duelistAbility(Hero hero, Integer target)`
**功能**: 执行决斗家的「剑舞」能力
**参数**: 
- `hero` - 执行能力的英雄
- `target` - 目标位置（此能力不需要目标）
**返回值**: 无
**实现逻辑**:
```java
beforeAbilityUsed(hero, null);
// 施加剑舞buff，持续3+武器等级回合
// 使用能力本身是瞬时的，所以实际效果少1回合
Buff.prolong(hero, SwordDance.class, 3+buffedLvl());
hero.sprite.operate(hero.pos);  // 播放操作动画
hero.next();                     // 结束当前回合
afterAbilityUsed(hero);
```
剑舞是一个自buff能力，不需要目标。

### abilityInfo
**签名**: `public String abilityInfo()`
**功能**: 返回能力描述信息
**参数**: 无
**返回值**: 能力描述字符串
**实现逻辑**:
```java
if (levelKnown){
    return Messages.get(this, "ability_desc", 4+buffedLvl());
} else {
    return Messages.get(this, "typical_ability_desc", 4);
}
```
显示剑舞的持续时间（实际显示比内部值多1，因为使用能力消耗1回合）。

### upgradeAbilityStat
**签名**: `public String upgradeAbilityStat(int level)`
**功能**: 返回指定等级下的能力统计
**参数**: `level` - 武器等级
**返回值**: 剑舞持续时间字符串
**实现逻辑**: `return Integer.toString(4+level);`

## 内部类

### SwordDance
**类型**: public static class extends FlavourBuff
**功能**: 剑舞状态追踪器
**字段**:
- `announced = true` - 状态变化时公告
- `type = buffType.POSITIVE` - 正面buff

**方法**:
| 方法 | 说明 |
|------|------|
| `icon()` | 返回buff图标 BuffIndicator.DUEL_DANCE |
| `iconFadePercent()` | 计算图标淡出百分比 |

## 11. 使用示例
```java
// 创建一把弯刀
Scimitar scimitar = new Scimitar();
// Tier 3武器，攻击速度较快
// 决斗家可以使用「剑舞」能力获得增益

hero.belongings.weapon = scimitar;
// 使用能力进入剑舞状态
// 剑舞期间攻击更具威力
```

## 注意事项
- 攻击速度较快（DLY=0.8f，约1.25倍速）
- 能力是自buff，不需要选择目标
- 剑舞持续时间随武器等级提升
- 音效音高较高，体现快速攻击的特点

## 最佳实践
- 在准备连续攻击前使用剑舞能力
- 配合高攻击速度可以最大化剑舞效果
- 升级武器可以延长剑舞持续时间