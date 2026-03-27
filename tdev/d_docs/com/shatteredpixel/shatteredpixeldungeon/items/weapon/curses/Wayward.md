# Wayward 诅咒文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/curses/Wayward.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses |
| 类类型 | public class |
| 继承关系 | extends Weapon.Enchantment |
| 代码行数 | 87 行 |

## 2. 类职责说明
Wayward（任性）诅咒使武器在攻击时有机会降低攻击者的准确度，持续10回合。这是一种负面效果，使武器更难命中目标。

## 7. 方法详解

### proc
**触发概率**: 25%
**效果**: 施加准确度降低buff，持续10回合

### curse
**返回值**: true（标识这是诅咒）

## WaywardBuff 内部类
- 类型: FlavourBuff
- 持续时间: 10回合
- 效果: 降低准确度（见 weapon.accuracyFactor）

## 最佳实践
- 尽快移除此诅咒
- 准确度降低使攻击更容易被闪避