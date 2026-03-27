# Longsword 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/Longsword.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee |
| 类类型 | class |
| 继承关系 | extends MeleeWeapon |
| 代码行数 | 75 |

## 2. 类职责说明
Longsword 是长剑类，4阶近战武器。使用剑类的"劈砍"技能，造成+(6+等级)额外伤害。

## 7. 方法详解

### duelistAbility(Hero hero, Integer target)
**签名**: `@Override protected void duelistAbility(Hero hero, Integer target)`
**功能**: 决斗者技能"劈砍"
**效果**: 造成+(6+等级)额外伤害，击杀后可连续使用

## 11. 使用示例
```java
// 4阶武器，高伤害
Longsword sword = new Longsword();
// 技能：劈砍，+(6+等级)额外伤害
```

## 注意事项
1. 使用Sword.cleaveAbility实现
2. 击杀后触发CleaveTracker连续使用