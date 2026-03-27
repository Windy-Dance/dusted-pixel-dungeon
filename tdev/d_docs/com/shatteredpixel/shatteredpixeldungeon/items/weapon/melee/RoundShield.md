# RoundShield 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/RoundShield.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee |
| 类类型 | public class |
| 继承关系 | extends MeleeWeapon |
| 代码行数 | 143 行 |

## 2. 类职责说明
RoundShield（圆盾）是一种 Tier 3 的防御型近战武器，具有较低的攻击伤害但提供防御加成。作为决斗家武器，其特殊能力「防御姿态」可以进入持续数回合的防御状态，期间可以格挡攻击。圆盾是独特的攻防一体武器。

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
    class RoundShield {
        +int tier = 3
        +max(int lvl)
        +defenseFactor(Char owner)
        +DRMax()
        +DRMax(int lvl)
        +statsInfo()
        +duelistAbility(Hero hero, Integer target)
        +abilityInfo()
        +upgradeAbilityStat(int level)
        +guardAbility(Hero hero, int duration, MeleeWeapon wep)$
    }
    class GuardTracker {
        +FlavourBuff
        +boolean hasBlocked
        +icon()
        +tintIcon(Image icon)
    }
    MeleeWeapon <|-- RoundShield
    RoundShield +-- GuardTracker
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无静态常量 | - | - | - |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | 初始化块 | 物品图标，使用 ItemSpriteSheet.ROUND_SHIELD |
| hitSound | String | 初始化块 | 击中音效，使用 Assets.Sounds.HIT |
| hitSoundPitch | float | 初始化块 | 音效音高，设为 1f（正常） |
| tier | int | 初始化块 | 武器等级，设为 3 |

## 7. 方法详解

### max
**签名**: `public int max(int lvl)`
**功能**: 计算指定等级下的最大伤害
**参数**: `lvl` - 武器等级
**返回值**: 最大伤害值
**实现逻辑**:
```java
return Math.round(3f*(tier+1)) +   // 12基础伤害，远低于标准的20
       lvl*(tier-1);               // 每级+2伤害，低于标准的+4
```
圆盾的攻击伤害很低，这是防御型武器的代价。

### defenseFactor
**签名**: `public int defenseFactor(Char owner)`
**功能**: 返回防御因子（伤害减免值）
**参数**: `owner` - 拥有者
**返回值**: 防御值
**实现逻辑**: `return DRMax();`

### DRMax
**签名**: `public int DRMax()` / `public int DRMax(int lvl)`
**功能**: 计算最大伤害减免值
**参数**: `lvl` - 武器等级（可选）
**返回值**: 伤害减免值
**实现逻辑**:
```java
return 4 + lvl;  // 基础4点 + 每级1点
```

### statsInfo
**签名**: `public String statsInfo()`
**功能**: 返回额外属性信息
**参数**: 无
**返回值**: 防御属性描述字符串
**实现逻辑**: 显示当前武器的防御值。

### duelistAbility
**签名**: `protected void duelistAbility(Hero hero, Integer target)`
**功能**: 执行决斗家的「防御姿态」能力
**参数**: 
- `hero` - 执行能力的英雄
- `target` - 目标位置（不需要）
**返回值**: 无
**实现逻辑**:
```java
RoundShield.guardAbility(hero, 5+buffedLvl(), this);
```
能力持续5+武器等级回合。

### abilityInfo
**签名**: `public String abilityInfo()`
**功能**: 返回能力描述信息
**参数**: 无
**返回值**: 能力描述字符串

### upgradeAbilityStat
**签名**: `public String upgradeAbilityStat(int level)`
**功能**: 返回指定等级下的能力统计
**参数**: `level` - 武器等级
**返回值**: 持续时间字符串

### guardAbility (静态方法)
**签名**: `public static void guardAbility(Hero hero, int duration, MeleeWeapon wep)`
**功能**: 执行防御姿态的核心逻辑
**参数**: 
- `hero` - 执行能力的英雄
- `duration` - 持续回合数
- `wep` - 使用的武器
**返回值**: 无
**实现逻辑**:
```java
wep.beforeAbilityUsed(hero, null);
// 施加防御追踪器buff
Buff.prolong(hero, GuardTracker.class, duration).hasBlocked = false;
hero.sprite.operate(hero.pos);  // 播放操作动画
hero.spendAndNext(Actor.TICK);   // 消耗1回合
wep.afterAbilityUsed(hero);
```

## 内部类

### GuardTracker
**类型**: public static class extends FlavourBuff
**功能**: 防御姿态追踪器
**字段**:
| 字段 | 类型 | 说明 |
|------|------|------|
| hasBlocked | boolean | 是否已经格挡过攻击 |

**方法**:
| 方法 | 说明 |
|------|------|
| `icon()` | 返回buff图标 BuffIndicator.DUEL_GUARD |
| `tintIcon(Image icon)` | 根据是否格挡过调整图标颜色 |
| `iconFadePercent()` | 计算图标淡出百分比 |
| `storeInBundle/restoreFromBundle` | 序列化支持 |

**说明**: 当`hasBlocked`为true时，图标会被染成紫色。

## 11. 使用示例
```java
// 创建一面圆盾
RoundShield shield = new RoundShield();
// Tier 3武器，低伤害但提供防御加成
// 决斗家可以使用「防御姿态」格挡攻击

hero.belongings.weapon = shield;
// 获得被动防御加成（伤害减免）
// 使用能力进入防御姿态格挡攻击
```

## 注意事项
- 攻击伤害很低（12基础 vs 标准20）
- 提供被动防御加成（伤害减免）
- 能力是自buff，不需要选择目标
- 格挡后会改变buff图标颜色

## 最佳实践
- 需要生存能力时使用圆盾
- 在面对高伤害敌人时使用防御姿态
- 升级可以同时提升攻击和防御
- 适合防御型玩法风格