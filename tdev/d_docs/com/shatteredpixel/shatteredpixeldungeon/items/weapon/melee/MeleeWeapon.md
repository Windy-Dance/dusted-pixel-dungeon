# MeleeWeapon 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/MeleeWeapon.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee |
| 类类型 | abstract class |
| 继承关系 | extends Weapon |
| 代码行数 | 583 |

## 2. 类职责说明
MeleeWeapon 是所有近战武器的抽象基类，扩展了 Weapon 类，添加了近战武器特有的功能：阶数(tier)系统、决斗者职业技能（武器技能充能系统）。每个近战武器都有阶数决定其基础伤害和力量需求。

## 7. 方法详解

### min(int lvl) / max(int lvl)
**签名**: `@Override public int min(int lvl) / public int max(int lvl)`
**功能**: 计算最小/最大伤害
**实现逻辑**:
```java
// 第249-258行
public int min(int lvl) {
    return tier + lvl;  // 基础 = 阶数，每级+1
}

public int max(int lvl) {
    return 5*(tier+1) + lvl*(tier+1);  // 基础 = 5×(阶数+1)
}
```

### STRReq(int lvl)
**签名**: `@Override public int STRReq(int lvl)`
**功能**: 计算力量需求
**实现逻辑**:
```java
// 第260-266行
int req = STRReq(tier, lvl);
if (masteryPotionBonus) req -= 2;
return req;
```

### duelistAbility(Hero hero, Integer target)
**签名**: `protected void duelistAbility(Hero hero, Integer target)`
**功能**: 决斗者武器技能（子类重写）
**说明**: 默认不执行任何操作，具体武器需要重写此方法实现独特技能

### beforeAbilityUsed / afterAbilityUsed
**签名**: `protected void beforeAbilityUsed(Hero hero, Char target) / protected void afterAbilityUsed(Hero hero)`
**功能**: 技能使用前后的处理
**实现逻辑**:
- beforeAbilityUsed: 消耗充能，触发天赋效果
- afterAbilityUsed: 处理天赋追踪器，恢复充能

### Charger 内部类
**签名**: `public static class Charger extends Buff`
**功能**: 管理决斗者的武器充能
**实现逻辑**:
```java
// 第412-581行
public int charges = 2;
public float partialCharge;

public int chargeCap() {
    // 冠军副职业有更多充能
    if (Dungeon.hero.subClass == HeroSubClass.CHAMPION) {
        return Math.min(10, 4 + (Dungeon.hero.lvl - 1) / 3);
    } else {
        return Math.min(8, 2 + (Dungeon.hero.lvl - 1) / 3);
    }
}

// 每60-45回合恢复1充能
float chargeToGain = 1/(60f-1.5f*(chargeCap()-charges));
partialCharge += chargeToGain;
```

## 11. 使用示例

### 创建自定义近战武器
```java
public class CustomSword extends MeleeWeapon {
    {
        tier = 3;  // 3阶武器
        ACC = 1.0f;
        DLY = 1.0f;
        RCH = 1;
    }
    
    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        // 实现武器技能
        beforeAbilityUsed(hero, null);
        // ... 技能效果 ...
        afterAbilityUsed(hero);
    }
}
```

### 武器伤害计算
```java
// 3阶+0级武器
int min = tier + lvl = 3 + 0 = 3;
int max = 5*(tier+1) + lvl*(tier+1) = 20 + 0 = 20;

// 3阶+3级武器
int min = 3 + 3 = 6;
int max = 20 + 12 = 32;
```

## 注意事项

1. **阶数系统**: 1-5阶，决定伤害和力量需求
2. **决斗者技能**: 每种近战武器有独特技能
3. **充能系统**: 技能需要充能，自动恢复
4. **双持天赋**: 可以装备两把武器

## 最佳实践

1. 设置 tier 属性定义武器阶数
2. 重写 duelistAbility 实现武器技能
3. 使用 beforeAbilityUsed/afterAbilityUsed 管理充能