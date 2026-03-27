# Flail 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/Flail.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee |
| 类类型 | public class |
| 继承关系 | extends MeleeWeapon |
| 代码行数 | 195 行 |

## 2. 类职责说明
Flail（连枷）是一种 Tier 4 的近战武器，具有高伤害但低准确度（ACC=0.8f）。它无法进行偷袭攻击。作为决斗家武器，其特殊能力「旋转」可以蓄力，最多旋转3次，然后下一次攻击造成巨额伤害。连枷是高风险高回报的武器，需要正确的时机释放蓄力。

## 4. 继承与协作关系
```mermaid
classDiagram
    class MeleeWeapon {
        <<abstract>>
        +int tier
        +float ACC
        +int min(int lvl)
        +int max(int lvl)
        +duelistAbility(Hero hero, Integer target)
    }
    class Flail {
        +int tier = 4
        +float ACC = 0.8f
        -int spinBoost
        +max(int lvl)
        +damageRoll(Char owner)
        +accuracyFactor(Char owner, Char target)
        +baseChargeUse(Hero hero, Char target)
        +duelistAbility(Hero hero, Integer target)
        +abilityInfo()
        +upgradeAbilityStat(int level)
    }
    class SpinAbilityTracker {
        +FlavourBuff
        +int spins
        +icon()
        +tintIcon(Image icon)
        +desc()
    }
    MeleeWeapon <|-- Flail
    Flail +-- SpinAbilityTracker
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无静态常量 | - | - | - |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | 初始化块 | 物品图标，使用 ItemSpriteSheet.FLAIL |
| hitSound | String | 初始化块 | 击中音效，使用 Assets.Sounds.HIT_CRUSH |
| hitSoundPitch | float | 初始化块 | 音效音高，设为 0.8f（低沉） |
| tier | int | 初始化块 | 武器等级，设为 4 |
| ACC | float | 初始化块 | 准确度修正，设为 0.8f（20%惩罚） |
| spinBoost | int | private static | 旋转伤害加成 |

## 7. 方法详解

### max
**签名**: `public int max(int lvl)`
**功能**: 计算指定等级下的最大伤害
**参数**: `lvl` - 武器等级
**返回值**: 最大伤害值
**实现逻辑**:
```java
return Math.round(7*(tier+1)) +        // 35基础伤害，高于标准的25
       lvl*Math.round(1.6f*(tier+1));  // 每级+8伤害，高于标准的+5
```
连枷具有较高的伤害来补偿准确度的不足。

### damageRoll
**签名**: `public int damageRoll(Char owner)`
**功能**: 计算实际伤害
**参数**: `owner` - 攻击者
**返回值**: 伤害值
**实现逻辑**:
```java
int dmg = super.damageRoll(owner) + spinBoost;
if (spinBoost > 0) Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
spinBoost = 0;  // 重置旋转加成
return dmg;
```

### accuracyFactor
**签名**: `public float accuracyFactor(Char owner, Char target)`
**功能**: 计算准确度因子
**参数**: 
- `owner` - 攻击者
- `target` - 目标
**返回值**: 准确度倍率
**实现逻辑**:
```java
SpinAbilityTracker spin = owner.buff(SpinAbilityTracker.class);
if (spin != null && spinBoost == 0) {
    // 设置延迟任务在攻击后重置
    Actor.add(new Actor() { ... });
    
    spin.detach();
    // 计算旋转伤害加成：每次旋转 (8 + 2*等级)
    // 3次旋转约 +120%基础伤害，+135%成长伤害
    spinBoost = spin.spins * augment.damageFactor(8 + 2*buffedLvl());
    return Float.POSITIVE_INFINITY;  // 无限准确度
} else if (spinBoost != 0) {
    return Float.POSITIVE_INFINITY;
} else {
    return super.accuracyFactor(owner, target);
}
```

### baseChargeUse
**签名**: `protected int baseChargeUse(Hero hero, Char target)`
**功能**: 计算使用能力所需的充能点数
**返回值**: 充能点数（正在蓄力时为0，否则为1）

### duelistAbility
**签名**: `protected void duelistAbility(Hero hero, Integer target)`
**功能**: 执行决斗家的「旋转」能力
**参数**: 
- `hero` - 执行能力的英雄
- `target` - 目标位置（不需要）
**返回值**: 无
**实现逻辑**:
```java
SpinAbilityTracker spin = hero.buff(SpinAbilityTracker.class);
if (spin != null && spin.spins >= 3){
    GLog.w(Messages.get(this, "spin_warn"));
    return;  // 最多旋转3次
}

beforeAbilityUsed(hero, null);
if (spin == null){
    spin = Buff.affect(hero, SpinAbilityTracker.class, 3f);
}

spin.spins++;
Buff.prolong(hero, SpinAbilityTracker.class, 3f);
Sample.INSTANCE.play(Assets.Sounds.CHAINS, 1, 1, 0.9f + 0.1f*spin.spins);
hero.sprite.operate(hero.pos);
hero.spendAndNext(Actor.TICK);
BuffIndicator.refreshHero();

afterAbilityUsed(hero);
```

### abilityInfo
**签名**: `public String abilityInfo()`
**功能**: 返回能力描述信息
**参数**: 无
**返回值**: 能力描述字符串

### upgradeAbilityStat
**签名**: `public String upgradeAbilityStat(int level)`
**功能**: 返回指定等级下的能力统计
**参数**: `level` - 武器等级
**返回值**: 每次旋转的伤害加成字符串

## 内部类

### SpinAbilityTracker
**类型**: public static class extends FlavourBuff
**功能**: 旋转状态追踪器
**字段**:
| 字段 | 类型 | 说明 |
|------|------|------|
| spins | int | 旋转次数（最大3） |

**方法**:
| 方法 | 说明 |
|------|------|
| `icon()` | 返回buff图标 BuffIndicator.DUEL_SPIN |
| `tintIcon(Image icon)` | 根据旋转次数着色（1绿、2黄、3红） |
| `iconFadePercent()` | 计算图标淡出百分比 |
| `desc()` | 返回描述信息 |
| `storeInBundle/restoreFromBundle` | 序列化支持 |

## 11. 使用示例
```java
// 创建一把连枷
Flail flail = new Flail();
// Tier 4武器，高伤害但准确度低
// 决斗家可以使用「旋转」蓄力

hero.belongings.weapon = flail;
// 使用能力蓄力（最多3次）
// 蓄力后下一次攻击造成巨额伤害
```

## 注意事项
- 准确度惩罚（ACC=0.8f）使攻击更容易被闪避
- 无法进行偷袭攻击
- 蓄力最多旋转3次
- 蓄力后下一次攻击必定命中（无限准确度）
- 蓄力状态持续3回合

## 最佳实践
- 在安全时蓄力，然后攻击
- 3次旋转的伤害加成最高
- 配合控制技能确保攻击命中
- 注意蓄力状态会在3回合后消失