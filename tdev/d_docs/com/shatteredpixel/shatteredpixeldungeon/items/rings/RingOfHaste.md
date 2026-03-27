# RingOfHaste 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/rings/RingOfHaste.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.rings |
| 类类型 | class |
| 继承关系 | extends Ring |
| 代码行数 | 66 |

## 2. 类职责说明
RingOfHaste 是急速戒指类，装备后提升角色的移动速度。每级提升约17.5%的移动速度，让角色能够更快地探索和躲避敌人。适合需要快速移动的流派。

## 7. 方法详解

### speedMultiplier(Char target)
**签名**: `public static float speedMultiplier(Char target)`
**功能**: 获取角色的移动速度倍率
**参数**:
- target: Char - 目标角色
**返回值**: float - 移动速度倍率
**实现逻辑**:
```java
// 第60-62行
return (float)Math.pow(1.175, getBuffedBonus(target, Haste.class));
```
- 基础乘数：1.175
- 每级提升：约17.5%移动速度

## 11. 使用示例
```java
// 获取移动速度倍率
float speed = RingOfHaste.speedMultiplier(hero);
// +1级戒指：1.175倍移动速度
// +2级戒指：1.38倍移动速度
// +3级戒指：1.62倍移动速度

// 计算每回合移动格子
float tilesPerTurn = baseTilesPerTurn * speed;
```

## 注意事项
1. 每级提升约17.5%移动速度
2. 诅咒时加成变为负数
3. 双戒指效果叠加

## 最佳实践
1. 快速探索地图
2. 躲避危险敌人
3. 配合远程武器使用