# Shortsword 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/Shortsword.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee |
| 类类型 | class |
| 继承关系 | extends MeleeWeapon |
| 代码行数 | 75 |

## 2. 类职责说明
Shortsword 是短剑类，2阶近战武器。使用剑类的"劈砍"技能，对敌人造成额外伤害，击杀后可连续使用。

## 7. 方法详解

### duelistAbility(Hero hero, Integer target)
**签名**: `@Override protected void duelistAbility(Hero hero, Integer target)`
**功能**: 决斗者技能"劈砍"
**效果**: 造成+(4+等级)额外伤害，击杀后可连续使用不消耗充能

## 11. 使用示例
```java
// 2阶武器，标准伤害
Shortsword sword = new Shortsword();
// 技能：劈砍，额外伤害
```

## 注意事项
1. 击杀后触发CleaveTracker，下次技能免费
2. 与Sword、Longsword、Greatsword共用劈砍机制