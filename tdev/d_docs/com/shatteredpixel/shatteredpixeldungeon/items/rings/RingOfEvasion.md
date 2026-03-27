# RingOfEvasion 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/rings/RingOfEvasion.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.rings |
| 类类型 | class |
| 继承关系 | extends Ring |
| 代码行数 | 66 |

## 2. 类职责说明
RingOfEvasion 是闪避戒指类，装备后提升角色的闪避能力。每级提升约12.5%的闪避率，让敌人更难命中角色。适合防御型流派或需要躲避高伤害攻击的情况。

## 7. 方法详解

### evasionMultiplier(Char target)
**签名**: `public static float evasionMultiplier(Char target)`
**功能**: 获取角色的闪避倍率
**参数**:
- target: Char - 目标角色
**返回值**: float - 闪避倍率
**实现逻辑**:
```java
// 第60-62行
return (float)Math.pow(1.125, getBuffedBonus(target, Evasion.class));
```
- 基础乘数：1.125
- 每级提升：12.5%闪避

## 11. 使用示例
```java
// 获取闪避倍率
float evasion = RingOfEvasion.evasionMultiplier(hero);
// +1级戒指：1.125倍闪避
// +2级戒指：1.27倍闪避
// +3级戒指：1.42倍闪避

// 计算被命中概率
float hitChance = 1f - (baseEvadeChance * evasion);
```

## 注意事项
1. 每级提升约12.5%闪避
2. 诅咒时加成变为负数
3. 双戒指效果叠加

## 最佳实践
1. 防御型流派
2. 对付高伤害敌人
3. 配合高护甲使用