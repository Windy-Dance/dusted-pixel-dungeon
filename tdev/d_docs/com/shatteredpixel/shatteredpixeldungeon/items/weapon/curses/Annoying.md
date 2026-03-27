# Annoying 诅咒文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/curses/Annoying.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses |
| 类类型 | public class |
| 继承关系 | extends Weapon.Enchantment |
| 代码行数 | 73 行 |

## 2. 类职责说明
Annoying（恼人）诅咒使武器在攻击时有机会发出尖叫，吸引所有敌人的注意力。这会使所有敌人向攻击者移动，可能引发危险的战斗。

## 7. 方法详解

### proc
**触发概率**: 5% (1/20)
**效果**: 
- 吸引所有敌人向攻击者移动
- 播放尖叫声效
- 显示随机恼人消息
- 解除隐身状态

## 消息类型
- 常见消息 (1-10): 约90%概率
- 稀有消息 (11-13): 约10%概率

## 最佳实践
- 可能引来大量敌人
- 解除隐身很危险
- 尽快移除此诅咒