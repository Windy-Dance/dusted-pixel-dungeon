# Sacrificial 诅咒文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/curses/Sacrificial.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses |
| 类类型 | public class |
| 继承关系 | extends Weapon.Enchantment |
| 代码行数 | 60 行 |

## 2. 类职责说明
Sacrificial（牺牲）诅咒使武器在攻击时有机会让攻击者流血。流血量基于攻击者当前生命值百分比，生命值越高流血越多。

## 7. 方法详解

### proc
**触发概率**: 10%
**效果**: 对攻击者施加流血效果
**流血量计算**: `(HP/HT)² * HT / 8`

## 最佳实践
- 流血量随生命值增加而增加
- 满生命值时流血最多
- 尽快移除此诅咒