# Kinetic 附魔文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/enchantments/Kinetic.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments |
| 类类型 | public class |
| 继承关系 | extends Weapon.Enchantment |
| 代码行数 | 147 行 |

## 2. 类职责说明
Kinetic（动能）附魔会保存溢出的伤害（击杀敌人时超出其生命值的伤害），并在下一次攻击时释放。这是一种积累型附魔，适合对付弱小敌人后攻击强敌。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Weapon.Enchantment {
        <<abstract>>
        +proc(Weapon weapon, Char attacker, Char defender, int damage)
        +glowing()
    }
    class Kinetic {
        +Glowing YELLOW
        +proc(Weapon weapon, Char attacker, Char defender, int damage)
        +glowing()
    }
    class KineticTracker {
        +Buff
        +int conservedDamage
        +act()
    }
    class ConservedDamage {
        +Buff
        +float preservedDamage
        +setBonus(int bonus)
        +damageBonus()
        +act()
    }
    Weapon.Enchantment <|-- Kinetic
    Kinetic +-- KineticTracker
    Kinetic +-- ConservedDamage
```

## 7. 方法详解

### proc
**签名**: `public int proc(Weapon weapon, Char attacker, Char defender, int damage)`
**功能**: 处理攻击效果，释放保存的伤害
**实现逻辑**:
```java
int conservedDamage = 0;
if (attacker.buff(ConservedDamage.class) != null) {
    conservedDamage = attacker.buff(ConservedDamage.class).damageBonus();
    attacker.buff(ConservedDamage.class).detach();
}

// 使用追踪器以便知道最终伤害
Buff.affect(attacker, KineticTracker.class).conservedDamage = conservedDamage;

return damage + conservedDamage;
```

## 内部类

### KineticTracker
**功能**: 追踪本次攻击的保存伤害

### ConservedDamage
**功能**: 保存溢出伤害的buff
- 每回合损失2.5%（最少0.1）
- 通过 `Char.damage` 计算（溢出伤害 = damage - defender.HP）

## 11. 使用示例
```java
// 1. 攻击弱小敌人，造成超出其生命值的伤害
// 2. 溢出伤害被保存
// 3. 下次攻击强敌时，保存的伤害会释放
```

## 最佳实践
- 先攻击弱小敌人积累伤害
- 然后攻击强敌释放累积伤害
- 保存的伤害会随时间衰减