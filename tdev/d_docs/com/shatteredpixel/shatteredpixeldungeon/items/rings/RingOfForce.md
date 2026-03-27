# RingOfForce 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/rings/RingOfForce.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.rings |
| 类类型 | class |
| 继承关系 | extends Ring |
| 代码行数 | 360 |

## 2. 类职责说明
RingOfForce 是力量戒指类，装备后使徒手攻击获得武器级别的伤害。伤害取决于力量属性和戒指等级，可以作为一个有效的近战武器替代品。决斗者职业可以激活"格斗姿态"获得额外伤害加成。

## 7. 方法详解

### damageRoll(Hero hero)
**签名**: `public static int damageRoll(Hero hero)`
**功能**: 计算徒手攻击伤害
**参数**:
- hero: Hero - 攻击的英雄
**返回值**: int - 伤害值范围
**实现逻辑**:
```java
// 第82-107行
boolean usingForce = hero.buff(Force.class) != null;
// 灵魂形态检查
if (hero.buff(SpiritForm.SpiritFormBuff.class) != null 
    && hero.buff(SpiritForm.SpiritFormBuff.class).ring() instanceof RingOfForce) {
    usingForce = true;
}
// 武僧技能时忽略
if (hero.buff(MonkEnergy.MonkAbility.UnarmedAbilityTracker.class) != null) {
    usingForce = false;
}
if (usingForce) {
    int level = getBuffedBonus(hero, Force.class);
    float tier = tier(hero.STR());
    int dmg = Hero.heroDamageIntRange(min(level, tier), max(level, tier));
    // 格斗姿态加成
    if (hero.buff(BrawlersStance.class) != null && hero.buff(BrawlersStance.class).active) {
        dmg += Math.round(3+tier+(level*((4+2*tier)/8f)));
    }
    return dmg;
} else {
    return Hero.heroDamageIntRange(1, Math.max(hero.STR()-8, 1));
}
```

### tier(int str)
**签名**: `private static float tier(int str)`
**功能**: 根据力量计算武器等效阶数
**参数**:
- str: int - 力量值
**返回值**: float - 等效武器阶数
**实现逻辑**:
```java
// 第73-80行
float tier = Math.max(1, (str - 8)/2f);
// 18点力量后的收益减半
if (tier > 5) {
    tier = 5 + (tier - 5) / 2f;
}
return tier;
```

## 11. 使用示例
```java
// 徒手攻击伤害计算
int damage = RingOfForce.damageRoll(hero);
// 10力量(+1戒指)：1阶武器伤害
// 14力量(+2戒指)：3阶武器伤害
// 18力量(+3戒指)：5阶武器伤害
```

## 注意事项
1. 伤害取决于力量和戒指等级
2. 决斗者有特殊技能"格斗姿态"
3. 可以获得武器的附魔和强化效果

## 最佳实践
1. 配合高力量角色使用
2. 决斗者职业最佳
3. 替代低级武器