# Friendly 诅咒文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/curses/Friendly.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses |
| 类类型 | public class |
| 继承关系 | extends Weapon.Enchantment |
| 代码行数 | 69 行 |

## 2. 类职责说明
Friendly（友善）诅咒使武器在攻击时有机会魅惑攻击者和目标，使他们暂时无法互相攻击。

## 7. 方法详解

### proc
**触发概率**: 10%
**效果**: 
- 攻击者被魅惑（完整持续时间）
- 目标被魅惑（一半持续时间）
- 如果已经被目标魅惑，伤害变为0

## 最佳实践
- 可能导致战斗陷入被动
- 魅惑期间无法攻击特定目标
- 尽快移除此诅咒