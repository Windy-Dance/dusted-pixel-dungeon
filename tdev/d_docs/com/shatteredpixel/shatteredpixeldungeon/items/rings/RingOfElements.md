# RingOfElements 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/rings/RingOfElements.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.rings |
| 类类型 | class |
| 继承关系 | extends Ring |
| 代码行数 | 103 |

## 2. 类职责说明
RingOfElements 是元素戒指类，装备后提升对各种元素伤害和负面状态的抗性。可以抵抗火焰、冰冻、毒素、腐蚀、麻痹等多种元素效果，是全面的防御型戒指。

## 静态常量表
| 常量名 | 类型 | 说明 |
|--------|------|------|
| RESISTS | HashSet<Class> | 可抵抗的效果类型集合 |

### RESISTS 包含的效果
- Burning（燃烧）
- Chill（寒冷）
- Frost（冰冻）
- Ooze（粘液）
- Paralysis（麻痹）
- Poison（毒素）
- Corrosion（腐蚀）
- ToxicGas（毒气）
- Electricity（电流）
- 以及 AntiMagic.RESISTS 中的所有魔法效果

## 7. 方法详解

### resist(Char target, Class effect)
**签名**: `public static float resist(Char target, Class effect)`
**功能**: 计算对特定效果的抗性倍率
**参数**:
- target: Char - 目标角色
- effect: Class - 效果类型
**返回值**: float - 抗性倍率（1表示无抗性，小于1表示有抗性）
**实现逻辑**:
```java
// 第88-98行
if (getBuffedBonus(target, Resistance.class) == 0) return 1f;

for (Class c : RESISTS) {
    if (c.isAssignableFrom(effect)) {
        return (float)Math.pow(0.825, getBuffedBonus(target, Resistance.class));
    }
}
return 1f;
```
- 基础乘数：0.825
- 每级减少约17.5%伤害

## 11. 使用示例
```java
// 获取对毒素的抗性
float resist = RingOfElements.resist(hero, Poison.class);
// +1级戒指：0.825倍伤害
// +2级戒指：0.68倍伤害
// +3级戒指：0.56倍伤害

// 计算实际伤害
int actualDamage = (int)(baseDamage * resist);
```

## 注意事项
1. 抵抗多种元素效果
2. 每级减少约17.5%伤害
3. 对Boss的元素攻击也有效

## 最佳实践
1. 对付元素攻击强的敌人
2. 探索危险环境时佩戴
3. 全面防御的选择