# Greatsword 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/Greatsword.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee |
| 类类型 | class |
| 继承关系 | extends MeleeWeapon |
| 代码行数 | 76 |

## 2. 类职责说明
Greatsword 是巨剑类，5阶近战武器，剑类武器的最高阶。使用"劈砍"技能，造成+(7+等级)额外伤害，是最强的剑类武器。

## 7. 方法详解

### duelistAbility(Hero hero, Integer target)
**签名**: `@Override protected void duelistAbility(Hero hero, Integer target)`
**功能**: 决斗者技能"劈砍"
**效果**: 造成+(7+等级)额外伤害，击杀后可连续使用

## 11. 使用示例
```java
// 5阶武器，剑类最高阶
Greatsword sword = new Greatsword();
// 技能：劈砍，+(7+等级)额外伤害
// 基础伤害：min=6, max=30 (+0级)
```

## 最佳实践
1. 高阶首选剑类武器
2. 配合劈砍技能最大化伤害
3. 击杀触发连击效果