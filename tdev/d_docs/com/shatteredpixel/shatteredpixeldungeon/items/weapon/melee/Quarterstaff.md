# Quarterstaff 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/Quarterstaff.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee |
| 类类型 | public class |
| 继承关系 | extends MeleeWeapon |
| 代码行数 | 96 行 |

## 2. 类职责说明
Quarterstaff（长棍）是一种 Tier 2 的近战武器，具有较低的伤害但提供防御加成。作为决斗家武器，其特殊能力「防御姿态」可以让英雄进入闪避状态，期间更容易躲避攻击。长棍是攻防平衡的武器，适合需要生存能力的玩家。

## 4. 继承与协作关系
```mermaid
classDiagram
    class MeleeWeapon {
        <<abstract>>
        +int tier
        +int min(int lvl)
        +int max(int lvl)
        +defenseFactor(Char owner)
    }
    class Quarterstaff {
        +int tier = 2
        +max(int lvl)
        +defenseFactor(Char owner)
        +duelistAbility(Hero hero, Integer target)
        +abilityInfo()
        +upgradeAbilityStat(int level)
    }
    class DefensiveStance {
        +FlavourBuff
        +icon()
        +iconFadePercent()
    }
    MeleeWeapon <|-- Quarterstaff
    Quarterstaff +-- DefensiveStance
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无静态常量 | - | - | - |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | 初始化块 | 物品图标，使用 ItemSpriteSheet.QUARTERSTAFF |
| hitSound | String | 初始化块 | 击中音效，使用 Assets.Sounds.HIT_CRUSH |
| hitSoundPitch | float | 初始化块 | 音效音高，设为 1f（正常） |
| tier | int | 初始化块 | 武器等级，设为 2 |

## 7. 方法详解

### max
**签名**: `public int max(int lvl)`
**功能**: 计算指定等级下的最大伤害
**参数**: `lvl` - 武器等级
**返回值**: 最大伤害值
**实现逻辑**:
```java
return 4*(tier+1) +    // 12基础伤害，低于标准的15
       lvl*(tier+1);   // 每级+3伤害，标准成长
```
长棍的伤害较低，但提供防御加成补偿。

### defenseFactor
**签名**: `public int defenseFactor(Char owner)`
**功能**: 返回防御因子
**参数**: `owner` - 拥有者
**返回值**: 固定返回2
**实现逻辑**: `return 2;` // 提供2点额外防御

### duelistAbility
**签名**: `protected void duelistAbility(Hero hero, Integer target)`
**功能**: 执行决斗家的「防御姿态」能力
**参数**: 
- `hero` - 执行能力的英雄
- `target` - 目标位置（不需要）
**返回值**: 无
**实现逻辑**:
```java
beforeAbilityUsed(hero, null);
// 施加防御姿态buff，持续3+武器等级回合
// 使用能力本身是瞬时的，所以实际效果少1回合
Buff.prolong(hero, DefensiveStance.class, 3 + buffedLvl());
hero.sprite.operate(hero.pos);  // 播放操作动画
hero.next();                     // 结束当前回合
afterAbilityUsed(hero);
```
防御姿态是一个自buff能力，提供闪避加成。

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
显示防御姿态的持续时间。

### upgradeAbilityStat
**签名**: `public String upgradeAbilityStat(int level)`
**功能**: 返回指定等级下的能力统计
**参数**: `level` - 武器等级
**返回值**: 持续时间字符串
**实现逻辑**: `return Integer.toString(4+level);`

## 内部类

### DefensiveStance
**类型**: public static class extends FlavourBuff
**功能**: 防御姿态追踪器
**字段**:
- `announced = true` - 状态变化时公告
- `type = buffType.POSITIVE` - 正面buff

**方法**:
| 方法 | 说明 |
|------|------|
| `icon()` | 返回buff图标 BuffIndicator.DUEL_EVASIVE |
| `iconFadePercent()` | 计算图标淡出百分比 |

**说明**: 防御姿态提供闪避加成，具体实现可能在其他地方处理。

## 11. 使用示例
```java
// 创建一根长棍
Quarterstaff staff = new Quarterstaff();
// Tier 2武器，低伤害但提供防御加成
// 决斗家可以使用「防御姿态」获得闪避

hero.belongings.weapon = staff;
// 获得2点被动防御加成
// 使用能力进入防御姿态闪避攻击
```

## 注意事项
- 提供2点被动防御加成
- 能力是自buff，不需要选择目标
- 防御姿态持续时间随武器等级提升
- 使用粉碎音效（HIT_CRUSH）

## 最佳实践
- 在面对高伤害敌人时使用防御姿态
- 配合其他防御装备效果更佳
- 升级武器可以延长防御姿态持续时间
- 适合需要生存能力的玩法风格