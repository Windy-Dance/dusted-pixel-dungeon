# Explosive 诅咒文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/curses/Explosive.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses |
| 类类型 | public class |
| 继承关系 | extends Weapon.Enchantment |
| 代码行数 | 162 行 |

## 2. 类职责说明
Explosive（易爆）诅咒使武器在多次使用后会爆炸。武器有一个隐藏的耐久度，每次攻击减少0-10点，当耐久度降至0时在攻击者附近爆炸。

## 7. 方法详解

### proc
**效果**: 
- 每次攻击减少0-10耐久度
- 耐久度≤50: 显示"温热"警告
- 耐久度≤10: 显示"滚烫"警告
- 耐久度≤0: 爆炸

### 爆炸机制
- 爆炸位置: 防御者附近最近的空格
- 伤害: 炸弹伤害
- 投掷武器会受到额外伤害

## 警告阶段
| 耐久度 | 状态 | 发光颜色 |
|--------|------|---------|
| >50 | 正常 | 黑色 |
| 10-50 | 温热 | 黑色(50%透明) |
| 0-10 | 滚烫 | 黑色(25%透明) |

## 最佳实践
- 注意警告信号
- 爆炸会造成范围伤害
- 可能误伤自己