# Displacing 诅咒文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/curses/Displacing.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses |
| 类类型 | public class |
| 继承关系 | extends Weapon.Enchantment |
| 代码行数 | 69 行 |

## 2. 类职责说明
Displacing（传送）诅咒使武器在攻击时有机会将目标传送到随机位置。这可能导致战斗失去控制，敌人可能被传送到危险或有利位置。

## 7. 方法详解

### proc
**触发概率**: 8.3% (1/12)
**效果**: 将目标传送到随机位置
**限制**: 无法传送不可移动的目标

## 特殊效果
- 传送后敌人会进入游荡状态（如果之前在追击）
- 可能将敌人传送到玩家附近或其他地方

## 最佳实践
- 可能打乱战术计划
- 敌人可能被传送到不利位置
- 也可能传送到危险位置