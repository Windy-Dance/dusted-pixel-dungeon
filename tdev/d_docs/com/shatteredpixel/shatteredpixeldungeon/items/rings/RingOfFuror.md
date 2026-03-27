# RingOfFuror 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/rings/RingOfFuror.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.rings |
| 类类型 | class |
| 继承关系 | extends Ring |
| 代码行数 | 66 |

## 2. 类职责说明
RingOfFuror 是狂怒戒指类，装备后提升角色的攻击速度。每级提升约9%的攻击速度，让角色能够更频繁地进行攻击。适合需要快速输出的流派。

## 7. 方法详解

### attackSpeedMultiplier(Char target)
**签名**: `public static float attackSpeedMultiplier(Char target)`
**功能**: 获取角色的攻击速度倍率
**参数**:
- target: Char - 目标角色
**返回值**: float - 攻击速度倍率
**实现逻辑**:
```java
// 第60-62行
return (float)Math.pow(1.09051, getBuffedBonus(target, Furor.class));
```
- 基础乘数：1.09051
- 每级提升：约9%攻击速度

## 11. 使用示例
```java
// 获取攻击速度倍率
float speed = RingOfFuror.attackSpeedMultiplier(hero);
// +1级戒指：1.09倍攻击速度
// +2级戒指：1.19倍攻击速度
// +3级戒指：1.30倍攻击速度

// 计算实际攻击间隔
float attackInterval = baseAttackInterval / speed;
```

## 注意事项
1. 每级提升约9%攻击速度
2. 诅咒时加成变为负数
3. 双戒指效果叠加

## 最佳实践
1. 配合高伤害武器使用
2. 近战流派首选
3. 快速击杀敌人