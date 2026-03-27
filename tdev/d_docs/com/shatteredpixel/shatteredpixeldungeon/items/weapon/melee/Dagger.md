# Dagger 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/Dagger.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee |
| 类类型 | class |
| 继承关系 | extends MeleeWeapon |
| 代码行数 | 144 |

## 2. 类职责说明
Dagger 是匕首类，1阶近战武器。特点是偷袭伤害加成：对被惊吓的敌人造成更高伤害。决斗者技能"潜行"可以瞬移并进入隐形状态。

## 7. 方法详解

### max(int lvl)
**签名**: `@Override public int max(int lvl)`
**功能**: 计算最大伤害（略低于标准）
**实现**: `4*(tier+1) + lvl*(tier+1)` = 8 + lvl

### damageRoll(Char owner)
**签名**: `@Override public int damageRoll(Char owner)`
**功能**: 计算伤害，偷袭时伤害更高
**实现**: 对被惊吓的敌人，伤害范围从 75%最大值到最大值

### duelistAbility(Hero hero, Integer target)
**签名**: `@Override protected void duelistAbility(Hero hero, Integer target)`
**功能**: 决斗者技能"潜行"：瞬移并隐形
**参数**: 目标格子，最大距离5格
**效果**: 获得(2+等级)回合隐形

## 11. 使用示例
```java
// 1阶武器，伤害较低
Dagger dagger = new Dagger();
// 偷袭时伤害提升约50%
// 技能：瞬移+隐形
```

## 最佳实践
1. 配合隐形或惊吓效果
2. 偷袭流派首选
3. 低阶过渡武器