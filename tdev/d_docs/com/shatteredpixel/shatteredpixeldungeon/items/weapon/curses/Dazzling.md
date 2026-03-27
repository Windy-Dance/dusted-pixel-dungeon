# Dazzling 诅咒文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/curses/Dazzling.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses |
| 类类型 | public class |
| 继承关系 | extends Weapon.Enchantment |
| 代码行数 | 70 行 |

## 2. 类职责说明
Dazzling（耀眼）诅咒使武器在攻击时有机会致盲视野内所有角色。攻击者被致盲的时间比其他角色长。

## 7. 方法详解

### proc
**触发概率**: 10%
**效果**: 
- 致盲所有能看到攻击位置的角色
- 攻击者: 完整致盲持续时间
- 其他角色: 一半致盲持续时间
- 屏幕闪白效果

## 最佳实践
- 致盲会使视野变暗
- 攻击者受影响最大
- 可能影响战斗节奏