# Sickle 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/Sickle.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee |
| 类类型 | public class |
| 继承关系 | extends MeleeWeapon |
| 代码行数 | 127 行 |

## 2. 类职责说明
Sickle（镰刀）是一种 Tier 2 的近战武器，具有高伤害但低准确度（ACC=0.68f）。作为决斗家武器，其特殊能力「收割」会造成流血伤害而非直接伤害。镰刀是高风险高回报的武器，适合有命中加成的角色使用。

## 4. 继承与协作关系
```mermaid
classDiagram
    class MeleeWeapon {
        <<abstract>>
        +int tier
        +float ACC
        +int min(int lvl)
        +int max(int lvl)
        +duelistAbility(Hero hero, Integer target)
    }
    class Sickle {
        +int tier = 2
        +float ACC = 0.68f
        +max(int lvl)
        +targetingPrompt()
        +duelistAbility(Hero hero, Integer target)
        +abilityInfo()
        +upgradeAbilityStat(int level)
        +harvestAbility(Hero hero, Integer target, float bleedMulti, int bleedBoost, MeleeWeapon wep)$
    }
    class HarvestBleedTracker {
        +FlavourBuff
    }
    MeleeWeapon <|-- Sickle
    Sickle +-- HarvestBleedTracker
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无静态常量 | - | - | - |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | 初始化块 | 物品图标，使用 ItemSpriteSheet.SICKLE |
| hitSound | String | 初始化块 | 击中音效，使用 Assets.Sounds.HIT_SLASH |
| hitSoundPitch | float | 初始化块 | 音效音高，设为 1f（正常） |
| tier | int | 初始化块 | 武器等级，设为 2 |
| ACC | float | 初始化块 | 准确度修正，设为 0.68f（32%准确度惩罚） |

## 7. 方法详解

### max
**签名**: `public int max(int lvl)`
**功能**: 计算指定等级下的最大伤害
**参数**: `lvl` - 武器等级
**返回值**: 最大伤害值
**实现逻辑**:
```java
return Math.round(6.67f*(tier+1)) +    // 20基础伤害，高于标准的15
       lvl*(tier+1);                   // 每级+6伤害
```
镰刀的伤害较高，补偿准确度的不足。

### targetingPrompt
**签名**: `public String targetingPrompt()`
**功能**: 返回目标选择提示文本
**参数**: 无
**返回值**: 从消息文件获取的提示字符串

### duelistAbility
**签名**: `protected void duelistAbility(Hero hero, Integer target)`
**功能**: 执行决斗家的「收割」能力
**参数**: 
- `hero` - 执行能力的英雄
- `target` - 目标位置
**返回值**: 无
**实现逻辑**:
```java
// 计算流血量：基础15 + 2.5*武器等级
// 约138%基础伤害，125%成长伤害
int bleedAmt = augment.damageFactor(Math.round(15f + 2.5f*buffedLvl()));
Sickle.harvestAbility(hero, target, 0f, bleedAmt, this);
```
能力造成流血效果，总伤害高于普通攻击。

### abilityInfo
**签名**: `public String abilityInfo()`
**功能**: 返回能力描述信息
**参数**: 无
**返回值**: 能力描述字符串

### upgradeAbilityStat
**签名**: `public String upgradeAbilityStat(int level)`
**功能**: 返回指定等级下的能力伤害统计
**参数**: `level` - 武器等级
**返回值**: 流血伤害数值字符串

### harvestAbility (静态方法)
**签名**: `public static void harvestAbility(Hero hero, Integer target, float bleedMulti, int bleedBoost, MeleeWeapon wep)`
**功能**: 执行收割能力的核心逻辑
**参数**: 
- `hero` - 执行能力的英雄
- `target` - 目标位置
- `bleedMulti` - 流血倍率
- `bleedBoost` - 流血加成
- `wep` - 使用的武器
**返回值**: 无
**实现逻辑**:
```java
if (target == null) {
    return;
}

Char enemy = Actor.findChar(target);
// 验证目标有效性
if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
    GLog.w(Messages.get(wep, "ability_no_target"));
    return;
}

hero.belongings.abilityWeapon = wep;
if (!hero.canAttack(enemy)){
    GLog.w(Messages.get(wep, "ability_target_range"));
    hero.belongings.abilityWeapon = null;
    return;
}
hero.belongings.abilityWeapon = null;

hero.sprite.attack(enemy.pos, new Callback() {
    @Override
    public void call() {
        wep.beforeAbilityUsed(hero, enemy);
        AttackIndicator.target(enemy);

        // 添加流血追踪器
        Buff.affect(enemy, HarvestBleedTracker.class, 0);
        // 执行攻击，使用无限准确度
        if (hero.attack(enemy, bleedMulti, bleedBoost, Char.INFINITE_ACCURACY)){
            Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
        }

        Invisibility.dispel();
        hero.spendAndNext(hero.attackDelay());
        if (!enemy.isAlive()){
            wep.onAbilityKill(hero, enemy);
        }
        wep.afterAbilityUsed(hero);
    }
});
```

## 内部类

### HarvestBleedTracker
**类型**: public static class extends FlavourBuff
**功能**: 流血追踪器，用于标记被收割能力击中的敌人
**说明**: 这是一个标记类buff，持续时间为0，仅用于追踪收割效果。

## 11. 使用示例
```java
// 创建一把镰刀
Sickle sickle = new Sickle();
// Tier 2武器，高伤害但准确度很低
// 决斗家可以使用「收割」能力造成流血

hero.belongings.weapon = sickle;
// 注意：准确度惩罚明显，建议配合命中加成使用
```

## 注意事项
- 准确度惩罚严重（ACC=0.68f，32%miss率）
- 能力造成的流血伤害会在多个回合内生效
- 流血伤害总伤害高于普通攻击
- `harvestAbility` 是静态方法，被 WarScythe 复用

## 最佳实践
- 配合提高命中率的装备或buff（如精准附魔、戒指等）
- 对高生命值敌人使用流血效果更佳
- 升级武器可以显著提升流血伤害
- 利用无限准确度的能力确保命中