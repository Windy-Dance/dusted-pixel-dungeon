# Weapon 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/Weapon.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon |
| 类类型 | abstract class |
| 继承关系 | extends KindOfWeapon |
| 代码行数 | 662 |

## 2. 类职责说明
Weapon 是所有武器物品的抽象基类，定义了武器的通用属性和行为：准确率(ACC)、攻击延迟(DLY)、攻击范围(RCH)、附魔系统、强化系统等。武器可以通过附魔获得额外效果，通过升级提升伤害。武器使用20次后会自动鉴定。

## 4. 继承与协作关系
```mermaid
classDiagram
    class KindOfWeapon {
        <<abstract>>
    }
    
    class Weapon {
        <<abstract>>
        +ACC: float
        +DLY: float
        +RCH: int
        +augment: Augment
        +enchantment: Enchantment
        +enchantHardened: boolean
        +curseInfusionBonus: boolean
        +masteryPotionBonus: boolean
        +proc(Char, Char, int): int
        +accuracyFactor(Char, Char): float
        +delayFactor(Char): float
        +reachFactor(Char): int
        +STRReq(): int
        +STRReq(int): int*
        +upgrade(boolean): Item
        +enchant(Enchantment): Weapon
        +hasEnchant(Class, Char): boolean
        +hasGoodEnchant(): boolean
        +hasCurseEnchant(): boolean
    }
    
    class Augment {
        <<enumeration>>
        SPEED
        DAMAGE
        NONE
        +damageFactor(int): int
        +delayFactor(float): float
    }
    
    class Enchantment {
        <<abstract>>
        +common: Class[]
        +uncommon: Class[]
        +rare: Class[]
        +curses: Class[]
        +proc(Weapon, Char, Char, int): int*
        +name(): String
        +curse(): boolean
        +glowing(): Glowing
        +random(): Enchantment
        +randomCurse(): Enchantment
    }
    
    KindOfWeapon <|-- Weapon
    Weapon +-- Augment
    Weapon +-- Enchantment
    
    note for Weapon "所有武器的基类\n支持附魔和强化\n20次使用后鉴定"
```

## 静态常量表
| 常量名 | 类型 | 说明 |
|--------|------|------|
| Augment.SPEED | Augment | 速度强化（伤害×0.7，延迟×0.67） |
| Augment.DAMAGE | Augment | 伤害强化（伤害×1.5，延迟×1.67） |
| Augment.NONE | Augment | 无强化 |

## 实例字段表
| 字段名 | 类型 | 说明 |
|--------|------|------|
| ACC | float | 准确率修正，默认1.0 |
| DLY | float | 攻击延迟修正，默认1.0 |
| RCH | int | 攻击范围，默认1 |
| augment | Augment | 武器强化类型 |
| enchantment | Enchantment | 武器附魔 |
| enchantHardened | boolean | 附魔是否已硬化 |
| curseInfusionBonus | boolean | 诅咒注入加成 |
| masteryPotionBonus | boolean | 精通药剂加成 |
| usesLeftToID | float | 鉴定剩余使用次数 |

## 7. 方法详解

### proc(Char attacker, Char defender, int damage)
**签名**: `@Override public int proc(Char attacker, Char defender, int damage)`
**功能**: 处理攻击时的武器效果（附魔、鉴定进度）
**参数**:
- attacker: Char - 攻击者
- defender: Char - 防御者
- damage: int - 基础伤害
**返回值**: int - 最终伤害
**实现逻辑**:
```java
// 第131-209行
// 1. 处理三位一体附魔（牧师技能）
// 2. 处理神圣武器效果（圣骑士技能）
// 3. 触发武器附魔
if (enchantment != null) {
    damage = enchantment.proc(this, attacker, defender, damage);
}

// 4. 更新鉴定进度
if (!levelKnown && attacker == Dungeon.hero) {
    usesLeftToID -= uses;
    if (usesLeftToID <= 0) {
        identify();
    }
}
return damage;
```

### accuracyFactor(Char owner, Char target)
**签名**: `@Override public float accuracyFactor(Char owner, Char target)`
**功能**: 计算准确率因子
**返回值**: float - 准确率倍率
**实现逻辑**:
```java
// 第291-306行
int encumbrance = 0;
if (owner instanceof Hero) {
    encumbrance = STRReq() - ((Hero)owner).STR();
}
// 力量不足时准确率降低
return encumbrance > 0 ? (float)(ACC / Math.pow(1.5, encumbrance)) : ACC;
```

### delayFactor(Char owner)
**签名**: `@Override public float delayFactor(Char owner)`
**功能**: 计算攻击延迟因子
**返回值**: float - 延迟倍率
**实现逻辑**:
```java
// 第309-311行
return baseDelay(owner) * (1f/speedMultiplier(owner));
```

### STRReq(int lvl)
**签名**: `public abstract int STRReq(int lvl)`
**功能**: 获取指定等级的力量需求（抽象方法）
**参数**:
- lvl: int - 武器等级
**返回值**: int - 力量需求

### upgrade(boolean enchant)
**签名**: `public Item upgrade(boolean enchant)`
**功能**: 升级武器
**参数**:
- enchant: boolean - 是否强制添加附魔
**返回值**: Item - 升级后的武器
**实现逻辑**:
```java
// 第379-405行
if (enchant && enchantment == null) {
    enchant(Enchantment.random());
}
// 高等级升级可能移除附魔
if (enchantment != null && level() >= 4) {
    if (Random.Float(10) < Math.pow(2, level()-4)) {
        enchant(null);
    }
}
cursed = false;
return super.upgrade();
```

### enchant(Enchantment ench)
**签名**: `public Weapon enchant(Enchantment ench)`
**功能**: 为武器添加附魔
**参数**:
- ench: Enchantment - 附魔类型
**返回值**: Weapon - 附魔后的武器

## 11. 使用示例

### 创建自定义武器
```java
public class CustomWeapon extends Weapon {
    public int tier = 3;
    
    @Override
    public int min(int lvl) {
        return tier + lvl;
    }
    
    @Override
    public int max(int lvl) {
        return 5 * (tier + 1) + lvl * (tier + 1);
    }
    
    @Override
    public int STRReq(int lvl) {
        return STRReq(tier, lvl);
    }
}
```

### 武器附魔
```java
// 添加随机附魔
weapon.enchant();

// 添加特定附魔
weapon.enchant(new Blazing());

// 添加诅咒附魔
weapon.enchant(Enchantment.randomCurse());
```

## 注意事项

1. **鉴定机制**: 使用20次后自动鉴定
2. **附魔移除**: 高等级升级可能移除附魔
3. **力量需求**: 力量不足会降低准确率和攻击速度
4. **附魔分类**: 普通(50%)、稀有(40%)、传说(10%)

## 最佳实践

1. 创建武器时实现 STRReq(int lvl) 方法
2. 重写 min(int lvl) 和 max(int lvl) 定义伤害
3. 使用 Augment 系统调整武器属性