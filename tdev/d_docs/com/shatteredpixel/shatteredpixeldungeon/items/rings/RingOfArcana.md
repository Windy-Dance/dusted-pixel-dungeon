# RingOfArcana 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/rings/RingOfArcana.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.rings |
| 类类型 | class |
| 继承关系 | extends Ring |
| 代码行数 | 67 |

## 2. 类职责说明
RingOfArcana 是奥术戒指类，装备后提升武器附魔和法杖的效果强度。每级提升约17.5%的附魔威力，对于依赖附魔效果的流派非常有价值。适合附魔武器或强化法杖的玩家。

## 7. 方法详解

### enchantPowerMultiplier(Char target)
**签名**: `public static float enchantPowerMultiplier(Char target)`
**功能**: 获取角色的附魔威力倍率
**参数**:
- target: Char - 目标角色
**返回值**: float - 附魔威力倍率
**实现逻辑**:
```java
// 第60-62行
return (float)Math.pow(1.175f, getBuffedBonus(target, Arcana.class));
```
- 基础乘数：1.175
- 每级提升：17.5%附魔威力

## 11. 使用示例
```java
// 获取附魔威力倍率
float power = RingOfArcana.enchantPowerMultiplier(hero);
// +1级戒指：1.175倍威力
// +2级戒指：1.38倍威力
// +3级戒指：1.62倍威力

// 应用到附魔伤害
int enchantDamage = baseEnchantDamage * power;
```

## 注意事项
1. 每级提升约17.5%附魔威力
2. 影响武器附魔和法杖效果
3. 对诅咒附魔也有效

## 最佳实践
1. 配合强力附魔武器
2. 法师流派增强法杖
3. 最大化附魔输出