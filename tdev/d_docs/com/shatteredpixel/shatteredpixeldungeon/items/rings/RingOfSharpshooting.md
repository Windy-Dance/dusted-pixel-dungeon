# RingOfSharpshooting 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/rings/RingOfSharpshooting.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.rings |
| 类类型 | class |
| 继承关系 | extends Ring |
| 代码行数 | 77 |

## 2. 类职责说明
RingOfSharpshooting 是神射手戒指类，装备后提升远程武器的伤害和耐久度。每级提升1点远程伤害加成和约20%的武器耐久度。适合依赖远程武器的流派。

## 7. 方法详解

### levelDamageBonus(Char target)
**签名**: `public static int levelDamageBonus(Char target)`
**功能**: 获取远程伤害加成
**参数**:
- target: Char - 目标角色
**返回值**: int - 伤害加成值

### durabilityMultiplier(Char target)
**签名**: `public static float durabilityMultiplier(Char target)`
**功能**: 获取武器耐久度倍率
**参数**:
- target: Char - 目标角色
**返回值**: float - 耐久度倍率
**实现逻辑**:
```java
// 第71-73行
return (float)(Math.pow(1.2, getBonus(target, Aim.class)));
```
- 基础乘数：1.2
- 每级提升：约20%耐久度

## 11. 使用示例
```java
// 获取远程伤害加成
int dmgBonus = RingOfSharpshooting.levelDamageBonus(hero);
// +1级戒指：+1伤害
// +2级戒指：+2伤害

// 获取耐久度倍率
float durMult = RingOfSharpshooting.durabilityMultiplier(hero);
// +1级戒指：1.2倍耐久
// +2级戒指：1.44倍耐久
```

## 注意事项
1. 每级提升1点远程伤害
2. 每级提升约20%耐久度
3. 只影响远程武器

## 最佳实践
1. 远程流派首选
2. 延长武器使用寿命
3. 配合弓箭使用