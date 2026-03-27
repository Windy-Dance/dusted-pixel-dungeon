# RingOfMight 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/rings/RingOfMight.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.rings |
| 类类型 | class |
| 继承关系 | extends Ring |
| 代码行数 | 120 |

## 2. 类职责说明
RingOfMight 是力量戒指类，装备后提升角色的力量属性和生命上限。每级提升1点力量和约3.5%生命上限，是强化角色的全能型戒指。适合需要穿戴高级装备和增加生存能力的流派。

## 7. 方法详解

### strengthBonus(Char target)
**签名**: `public static int strengthBonus(Char target)`
**功能**: 获取力量加成
**参数**:
- target: Char - 目标角色
**返回值**: int - 力量加成值

### HTMultiplier(Char target)
**签名**: `public static float HTMultiplier(Char target)`
**功能**: 获取生命上限倍率
**参数**:
- target: Char - 目标角色
**返回值**: float - 生命上限倍率
**实现逻辑**:
```java
// 第113-115行
return (float)Math.pow(1.035, getBuffedBonus(target, Might.class));
```
- 基础乘数：1.035
- 每级提升：约3.5%生命上限

### doEquip/doUnequip
**签名**: `@Override public boolean doEquip(Hero hero)`
**功能**: 装备/卸下时更新生命上限
**实现逻辑**:
```java
// 第40-47行, 第50-57行
hero.updateHT(false);  // 更新生命上限
```

## 11. 使用示例
```java
// 获取力量加成
int strBonus = RingOfMight.strengthBonus(hero);
// +1级戒指：+1力量
// +2级戒指：+2力量

// 获取生命上限倍率
float htMult = RingOfMight.HTMultiplier(hero);
// +1级戒指：1.035倍生命上限
// +2级戒指：1.07倍生命上限
```

## 注意事项
1. 每级提升1点力量
2. 每级提升约3.5%生命上限
3. 装备/卸下时更新生命值

## 最佳实践
1. 穿戴高级装备
2. 增加生存能力
3. 近战流派必备