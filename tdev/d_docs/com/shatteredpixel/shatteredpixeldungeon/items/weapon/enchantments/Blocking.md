# Blocking 附魔文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/enchantments/Blocking.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments |
| 类类型 | public class |
| 继承关系 | extends Weapon.Enchantment |
| 代码行数 | 146 行 |

## 2. 类职责说明
Blocking（格挡）附魔使武器在攻击时有机会为攻击者提供护盾。护盾可以吸收伤害，持续5回合。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Weapon.Enchantment {
        <<abstract>>
        +proc(Weapon weapon, Char attacker, Char defender, int damage)
        +glowing()
    }
    class Blocking {
        +Glowing BLUE
        +proc(Weapon weapon, Char attacker, Char defender, int damage)
        +glowing()
    }
    class BlockBuff {
        +ShieldBuff
        +float left
        +setShield(int shield)
        +act()
    }
    Weapon.Enchantment <|-- Blocking
    Blocking +-- BlockBuff
```

## 7. 方法详解

### proc
**签名**: `public int proc(Weapon weapon, Char attacker, Char defender, int damage)`
**功能**: 处理攻击效果，提供护盾
**实现逻辑**:
```java
int level = Math.max(0, weapon.buffedLvl());
// 触发概率: 等级0=10%, 等级1=12%, 等级2=14%
float procChance = (level+4f)/(level+40f) * procChanceMultiplier(attacker);
if (Random.Float() < procChance){
    float powerMulti = Math.max(1f, procChance);
    
    BlockBuff b = Buff.affect(attacker, BlockBuff.class);
    // 护盾值: 2 + 武器等级
    int shield = Math.round(powerMulti * (2 + weapon.buffedLvl()));
    b.setShield(shield);
    
    attacker.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shield), FloatingText.SHIELDING);
    attacker.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 5);
}
return damage;
```

## BlockBuff 内部类
- 继承自 ShieldBuff
- 持续5回合
- 每回合检查是否过期
- 显示护盾图标

## 最佳实践
- 提供临时护盾保护
- 护盾可以吸收伤害
- 适合需要生存能力的情况