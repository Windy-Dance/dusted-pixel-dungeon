# Lucky 附魔文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/enchantments/Lucky.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments |
| 类类型 | public class |
| 继承关系 | extends Weapon.Enchantment |
| 代码行数 | 96 行 |

## 2. 类职责说明
Lucky（幸运）附魔使武器在击杀敌人时有机会掉落额外的消耗品。这是一种收益型附魔，可以增加资源获取。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Weapon.Enchantment {
        <<abstract>>
        +proc(Weapon weapon, Char attacker, Char defender, int damage)
        +glowing()
    }
    class Lucky {
        +Glowing GREEN
        +proc(Weapon weapon, Char attacker, Char defender, int damage)
        +genLoot()$
        +showFlare(Visual vis)$
        +glowing()
    }
    class LuckProc {
        +Buff
        +int ringLevel
        +act()
        +genLoot()
    }
    Weapon.Enchantment <|-- Lucky
    Lucky +-- LuckProc
```

## 7. 方法详解

### proc
**签名**: `public int proc(Weapon weapon, Char attacker, Char defender, int damage)`
**功能**: 处理攻击效果，标记幸运掉落
**实现逻辑**:
```java
int level = Math.max(0, weapon.buffedLvl());
// 触发概率: 等级0=10%, 等级1=12%, 等级2=14%
float procChance = (level+4f)/(level+40f) * procChanceMultiplier(attacker);
if (Random.Float() < procChance){
    float powerMulti = Math.max(1f, procChance);
    // 标记幸运掉落，在敌人死亡时生成战利品
    Buff.affect(defender, LuckProc.class).ringLevel = -10 + Math.round(5*powerMulti);
}
return damage;
```

### genLoot
**签名**: `public static Item genLoot()`
**功能**: 生成幸运掉落物品
**返回值**: 消耗品物品

## 触发概率表
| 武器等级 | 触发概率 |
|---------|---------|
| +0 | 10% |
| +1 | 12% |
| +2 | 14% |

## 最佳实践
- 击杀敌人时有机会掉落额外物品
- 适合需要大量资源的情况
- 掉落物品质量随触发概率提升