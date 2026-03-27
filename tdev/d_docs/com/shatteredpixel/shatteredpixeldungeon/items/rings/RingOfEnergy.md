# RingOfEnergy 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/rings/RingOfEnergy.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.rings |
| 类类型 | class |
| 继承关系 | extends Ring |
| 代码行数 | 90 |

## 2. 类职责说明
RingOfEnergy 是能量戒指类，装备后提升法杖、神器和护甲的充能速度。每级提升约17.5%的充能效率，对于依赖法杖和神器的流派非常重要。法师和盗贼职业特别受益。

## 7. 方法详解

### wandChargeMultiplier(Char target)
**签名**: `public static float wandChargeMultiplier(Char target)`
**功能**: 获取法杖充能倍率
**参数**:
- target: Char - 目标角色
**返回值**: float - 充能倍率
**实现逻辑**:
```java
// 第64-72行
float bonus = (float)Math.pow(1.175, getBuffedBonus(target, Energy.class));
// 牧师之外的职业有天赋加成
if (target instanceof Hero && ((Hero) target).heroClass != HeroClass.CLERIC
        && ((Hero) target).hasTalent(Talent.LIGHT_READING)) {
    bonus *= 1f + (0.2f * ((Hero) target).pointsInTalent(Talent.LIGHT_READING)/3f);
}
return bonus;
```

### artifactChargeMultiplier(Char target)
**签名**: `public static float artifactChargeMultiplier(Char target)`
**功能**: 获取神器充能倍率
**返回值**: float - 充能倍率

### armorChargeMultiplier(Char target)
**签名**: `public static float armorChargeMultiplier(Char target)`
**功能**: 获取护甲充能倍率
**返回值**: float - 充能倍率

## 11. 使用示例
```java
// 获取法杖充能倍率
float wandBonus = RingOfEnergy.wandChargeMultiplier(hero);
// +1级戒指：1.175倍充能
// +2级戒指：1.38倍充能

// 获取神器充能倍率
float artifactBonus = RingOfEnergy.artifactChargeMultiplier(hero);

// 获取护甲充能倍率
float armorBonus = RingOfEnergy.armorChargeMultiplier(hero);
```

## 注意事项
1. 影响法杖、神器、护甲充能
2. 天赋可以额外提升
3. 法师和盗贼职业特别受益

## 最佳实践
1. 法师流派必备
2. 依赖神器的流派
3. 配合充能卷轴使用