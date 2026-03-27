# Polarized 诅咒文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/curses/Polarized.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses |
| 类类型 | public class |
| 继承关系 | extends Weapon.Enchantment |
| 代码行数 | 54 行 |

## 2. 类职责说明
Polarized（两极）诅咒使武器攻击要么造成150%伤害，要么造成0伤害。这是一种高风险高回报的诅咒，平均伤害为75%。

## 7. 方法详解

### proc
**触发概率**: 50%
**效果**: 
- 50%概率: 伤害 × 1.5
- 50%概率: 伤害 = 0

## 期望值
- 平均伤害 = 0.5 × 1.5 + 0.5 × 0 = 0.75
- 相当于25%的伤害惩罚

## 最佳实践
- 风险较高的诅咒
- 可能错失关键击杀
- 平均伤害降低25%