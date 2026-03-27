# RingOfTenacity 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/rings/RingOfTenacity.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.rings |
| 类类型 | class |
| 继承关系 | extends Ring |
| 代码行数 | 68 |

## 2. 类职责说明
RingOfTenacity 是坚韧戒指类，装备后根据角色损失的生命值比例减少受到的伤害。生命值越低，减伤效果越强。这是绝境反击的强力防御装备，适合勇敢的近战流派。

## 7. 方法详解

### damageMultiplier(Char t)
**签名**: `public static float damageMultiplier(Char t)`
**功能**: 计算伤害倍率（基于生命值损失）
**参数**:
- t: Char - 目标角色
**返回值**: float - 伤害倍率（小于1表示减伤）
**实现逻辑**:
```java
// 第60-63行
// (HT - HP)/HT = 当前损失生命百分比
return (float)Math.pow(0.85, getBuffedBonus(t, Tenacity.class)*((float)(t.HT - t.HP)/t.HT));
```
- 基础乘数：0.85
- 效果随生命值损失增加

## 11. 使用示例
```java
// 计算伤害倍率
// 假设+3级戒指，生命值损失50%
float mult = RingOfTenacity.damageMultiplier(hero);
// 计算：0.85^(3*0.5) = 0.85^1.5 ≈ 0.78
// 即减少约22%伤害

// 生命值损失100%时（1点生命）
// 计算：0.85^(3*1.0) = 0.85^3 ≈ 0.61
// 即减少约39%伤害
```

## 注意事项
1. 减伤效果随生命值损失增加
2. 满血时无减伤效果
3. 濒死时效果最强

## 最佳实践
1. 绝境反击的防御装备
2. 勇敢的近战流派
3. 配合治疗物品使用