# RingOfAccuracy 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/rings/RingOfAccuracy.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.rings |
| 类类型 | class |
| 继承关系 | extends Ring |
| 代码行数 | 66 |

## 2. 类职责说明
RingOfAccuracy 是精准戒指类，装备后提升角色的命中准确率。每级提升约30%的命中加成，对于经常出现攻击未命中的情况非常有用。适合配合高伤害但命中率低的武器使用。

## 7. 方法详解

### statsInfo()
**签名**: `public String statsInfo()`
**功能**: 返回戒指属性信息
**实现逻辑**:
```java
// 第36-48行
if (isIdentified()) {
    String info = Messages.get(this, "stats",
        Messages.decimalFormat("#.##", 100f * (Math.pow(1.3f, soloBuffedBonus()) - 1f)));
    // 如果装备了两个同类戒指，显示组合加成
    if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)) {
        info += "\n\n" + Messages.get(this, "combined_stats",
            Messages.decimalFormat("#.##", 100f * (Math.pow(1.3f, combinedBuffedBonus(Dungeon.hero)) - 1f)));
    }
    return info;
} else {
    return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 30f));
}
```

### accuracyMultiplier(Char target)
**签名**: `public static float accuracyMultiplier(Char target)`
**功能**: 获取角色的命中倍率
**参数**:
- target: Char - 目标角色
**返回值**: float - 命中倍率
**实现逻辑**:
```java
// 第60-62行
return (float)Math.pow(1.3f, getBuffedBonus(target, Accuracy.class));
```
- 基础乘数：1.3
- 每级提升：30%命中

## 11. 使用示例
```java
// 获取角色的命中倍率
float accuracy = RingOfAccuracy.accuracyMultiplier(hero);
// +1级戒指：1.3倍命中
// +2级戒指：1.69倍命中
// +3级戒指：2.2倍命中

// 计算最终命中率
float hitChance = baseHitChance * accuracy;
```

## 注意事项
1. 每级提升约30%命中
2. 诅咒时加成变为负数
3. 双戒指效果叠加

## 最佳实践
1. 配合高伤害武器使用
2. 对付高闪避敌人
3. 弥补命中不足的装备