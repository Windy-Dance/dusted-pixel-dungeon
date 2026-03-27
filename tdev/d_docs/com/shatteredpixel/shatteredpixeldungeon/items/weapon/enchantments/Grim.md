# Grim 附魔文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/enchantments/Grim.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments |
| 类类型 | public class |
| 继承关系 | extends Weapon.Enchantment |
| 代码行数 | 83 行 |

## 2. 类职责说明
Grim（死神）附魔使武器在攻击生命值较低的敌人时有机会直接击杀目标。触发概率随敌人生命值降低而提高，是强大的收割型附魔。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Weapon.Enchantment {
        <<abstract>>
        +proc(Weapon weapon, Char attacker, Char defender, int damage)
        +glowing()
    }
    class Grim {
        +Glowing BLACK
        +proc(Weapon weapon, Char attacker, Char defender, int damage)
        +glowing()
    }
    class GrimTracker {
        +Buff
        +float maxChance
        +boolean qualifiesForBadge
        +act()
    }
    Weapon.Enchantment <|-- Grim
    Grim +-- GrimTracker
```

## 7. 方法详解

### proc
**签名**: `public int proc(Weapon weapon, Char attacker, Char defender, int damage)`
**功能**: 处理攻击效果，可能直接击杀
**实现逻辑**:
```java
if (defender.isImmune(Grim.class)) {
    return damage;  // 免疫死神附魔
}

int level = Math.max(0, weapon.buffedLvl());
// 最大触发概率: 50% + 5%*等级
float maxChance = 0.5f + .05f*level;
maxChance *= procChanceMultiplier(attacker);

// 使用追踪器延迟处理
Buff.affect(defender, GrimTracker.class).maxChance = maxChance;

// 如果是英雄使用带死神附魔的武器
if (defender.buff(GrimTracker.class) != null
        && attacker instanceof Hero
        && weapon.hasEnchant(Grim.class, attacker)){
    defender.buff(GrimTracker.class).qualifiesForBadge = true;
}

return damage;
```

## 触发机制
- 触发概率 = maxChance * (1 - defender.HP / defender.HT)
- 敌人生命值越低，触发概率越高
- 敌人生命值为1时达到最大概率
- 免疫死神附魔的敌人不会触发

## 最佳实践
- 对付低生命值敌人效果显著
- 可以直接击杀敌人
- 适合收割残血敌人