# 创建新武器教程

## 目标
完成这个教程后，你将能够创建一个自定义武器，包含伤害计算、力量需求、附魔支持等。

## 前置知识
- 阅读 [Weapon API 参考](../../reference/items/weapon-api.md)
- 阅读 [Item API 参考](../../reference/items/item-api.md)

## 最终成果
创建一个"暗影匕首"武器：
- 高攻击速度
- 低伤害但高暴击
- 特殊的决斗者能力

## 步骤

### 步骤1：创建武器类
**目标**：创建武器的基本框架
**文件**：`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/ShadowDagger.java`

首先创建新的武器类文件。暗影匕首将继承 `MeleeWeapon` 类，并设置基本属性：

```java
/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.dustedpixel.dustedpixeldungeon.items.weapon.melee;

import com.dustedpixel.dustedpixeldungeon.Assets;
import com.dustedpixel.dustedpixeldungeon.actors.Char;
import com.dustedpixel.dustedpixeldungeon.actors.hero.Hero;
import com.dustedpixel.dustedpixeldungeon.messages.Messages;
import com.dustedpixel.dustedpixeldungeon.sprites.ItemSpriteSheet;

public class ShadowDagger extends MeleeWeapon {

    {
        image = ItemSpriteSheet.DAGGER;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1.2f;

        tier = 1;
        DLY = 0.8f;  // 更快的攻击速度
        ACC = 1.2f;  // 稍高的准确度
    }

    @Override
    public int min(int lvl) {
        return 1 +              // 基础最小伤害
                lvl;            // 等级缩放
    }

    @Override
    public int max(int lvl) {
        return 4 +              // 基础最大伤害  
                lvl * 2;        // 等级缩放（较低）
    }

    @Override
    public int STRReq(int lvl) {
        int req = STRReq(tier, lvl);
        if (masteryPotionBonus) {
            req -= 2;
        }
        return req;
    }
}
```

### 步骤2：设置属性
**目标**：定义伤害、速度、力量需求

在上面的代码中，我们已经设置了关键属性：

- **tier = 1**: 这是1级武器，表示它是游戏中较弱的武器类型
- **DLY = 0.8f**: 攻击延迟为0.8倍，意味着比普通武器快25%
- **ACC = 1.2f**: 准确度加成为1.2倍，稍微提高命中率
- **伤害范围**: 最小伤害为 `1 + lvl`，最大伤害为 `4 + lvl * 2`

**Tier系统说明**：
Shattered Pixel Dungeon使用1-5级的武器分级系统：
- **Tier 1**: 最弱的武器（如短剑、匕首）
- **Tier 2**: 轻型武器（如短剑、手斧）  
- **Tier 3**: 中等武器（如长剑、钉头锤）
- **Tier 4**: 重型武器（如阔剑、战斧）
- **Tier 5**: 最强武器（如巨剑、战锤）

力量需求公式：`STRReq = (8 + tier * 2) - (int)(Math.sqrt(8 * lvl + 1) - 1)/2`

### 步骤3：实现伤害计算
**目标**：重写min/max伤害方法

暗影匕首设计为低伤害高暴击的武器。我们的伤害公式如下：

- **最小伤害**: `1 + lvl`
- **最大伤害**: `4 + lvl * 2`

这比同等级的普通匕首（最小1+lvl，最大5+lvl）要低一些，但我们会通过特殊能力来补偿。

**伤害计算流程**：
1. 基础伤害 = `Random.NormalIntRange(min(lvl), max(lvl))`
2. 应用增益效果（如强化、附魔）
3. 添加额外力量伤害：如果角色力量超过需求，每点额外力量提供0-1点额外伤害
4. 应用速度增益（影响攻击频率，不直接影响单次伤害）

### 步骤4：添加决斗者能力
**目标**：实现特殊能力

现在为暗影匕首添加独特的决斗者能力。我们将实现一个"暗影突袭"能力，在背刺时造成额外伤害：

```java
// 在 ShadowDagger.java 中添加以下方法

@Override
protected int baseChargeUse(Hero hero, Char target){
    // 暗影匕首能力消耗1点充能
    return 1;
}

@Override
public String targetingPrompt() {
    return Messages.get(this, "prompt");
}

@Override
protected void duelistAbility(Hero hero, Integer target) {
    // 暗影突袭：对目标造成+(6+lvl)点额外伤害，并有概率使其失明
    int dmgBoost = augment.damageFactor(6 + buffedLvl());
    shadowStrike(hero, target, dmgBoost, this);
}

@Override
public String abilityInfo() {
    int dmgBoost = levelKnown ? 6 + buffedLvl() : 6;
    if (levelKnown){
        return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
    } else {
        return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
    }
}

public String upgradeAbilityStat(int level){
    int dmgBoost = 6 + level;
    return augment.damageFactor(min(level)+dmgBoost) + "-" + augment.damageFactor(max(level)+dmgBoost);
}

// 实现暗影突袭能力
private static void shadowStrike(Hero hero, Integer target, int damage, ShadowDagger weapon) {
    if (target == null || !hero.fieldOfView[target] || !Dungeon.level.heroFOV[target]) {
        GLog.w(Messages.get(MeleeWeapon.class, "ability_bad_target"));
        return;
    }

    Char enemy = Actor.findChar(target);
    if (enemy == null || enemy == hero) {
        GLog.w(Messages.get(MeleeWeapon.class, "ability_bad_target"));
        return;
    }

    if (!Dungeon.level.adjacent(hero.pos, enemy.pos)) {
        GLog.w(Messages.get(MeleeWeapon.class, "ability_bad_distance"));
        return;
    }

    beforeAbilityUsed(hero, enemy);

    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
    
    // 计算总伤害
    int totalDamage = weapon.damageRoll(hero) + damage;
    enemy.damage(totalDamage, weapon);
    
    // 50%概率使敌人失明2回合
    if (Random.Int(2) == 0) {
        Buff.affect(enemy, Blindness.class, 2f);
        enemy.sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "blinded"));
    }

    afterAbilityUsed(hero);
    
    onAbilityKill(hero, enemy);
    
    hero.spendAndNext(1f); // 消耗1回合
}
```

你还需要在messages.properties文件中添加相应的文本：

```
items.weapon.melee.ShadowDagger.prompt=选择一个相邻的敌人进行暗影突袭
items.weapon.melee.ShadowDagger.ability_desc=暗影突袭造成_%1$d-%2$d_点伤害，并有50%%概率使敌人失明2回合
items.weapon.melee.ShadowDagger.typical_ability_desc=暗影突袭通常造成_%1$d-%2$d_点伤害
items.weapon.melee.ShadowDagger.blinded=失明！
```

### 步骤5：注册武器
**目标**：添加到生成池

最后，需要将暗影匕首添加到武器生成系统中。编辑 `Generator.java` 文件，在 `WEP_T1.classes` 数组中添加我们的新武器：

```java
// 在 Generator.java 的 WEP_T1.classes 定义中
WEP_T1.classes = new Class<?>[]{
    WornShortsword.class,
    MagesStaff.class,
    Dagger.class,
    Gloves.class,
    Rapier.class,
    Cudgel.class,
    ShadowDagger.class  // 添加这一行
};

// 同时在概率数组中添加相应的概率
WEP_T1.defaultProbs = new float[]{ 2, 0, 2, 2, 2, 2, 1 }; // 最后一个1是暗影匕首的概率
```

另外，在文件顶部的import部分添加：

```java
import com.dustedpixel.dustedpixeldungeon.items.weapon.melee.ShadowDagger;
```

## 完整代码

以下是完整的 `ShadowDagger.java` 文件：

```java
/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.dustedpixel.dustedpixeldungeon.items.weapon.melee;

import com.dustedpixel.dustedpixeldungeon.Assets;
import com.dustedpixel.dustedpixeldungeon.Dungeon;
import com.dustedpixel.dustedpixeldungeon.actors.Actor;
import com.dustedpixel.dustedpixeldungeon.actors.Char;
import com.dustedpixel.dustedpixeldungeon.actors.buffs.Buff;
import com.dustedpixel.dustedpixeldungeon.actors.buffs.Blindness;
import com.dustedpixel.dustedpixeldungeon.actors.hero.Hero;
import com.dustedpixel.dustedpixeldungeon.messages.Messages;
import com.dustedpixel.dustedpixeldungeon.sprites.CharSprite;
import com.dustedpixel.dustedpixeldungeon.sprites.ItemSpriteSheet;
import com.dustedpixel.dustedpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class ShadowDagger extends MeleeWeapon {

    {
        image = ItemSpriteSheet.DAGGER;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1.2f;

        tier = 1;
        DLY = 0.8f;  // 更快的攻击速度
        ACC = 1.2f;  // 稍高的准确度
    }

    @Override
    public int min(int lvl) {
        return 1 +              // 基础最小伤害
                lvl;            // 等级缩放
    }

    @Override
    public int max(int lvl) {
        return 4 +              // 基础最大伤害  
                lvl * 2;        // 等级缩放（较低）
    }

    @Override
    public int STRReq(int lvl) {
        int req = STRReq(tier, lvl);
        if (masteryPotionBonus) {
            req -= 2;
        }
        return req;
    }

    @Override
    protected int baseChargeUse(Hero hero, Char target) {
        return 1;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        if (target == null || !hero.fieldOfView[target] || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(MeleeWeapon.class, "ability_bad_target"));
            return;
        }

        Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero) {
            GLog.w(Messages.get(MeleeWeapon.class, "ability_bad_target"));
            return;
        }

        if (!Dungeon.level.adjacent(hero.pos, enemy.pos)) {
            GLog.w(Messages.get(MeleeWeapon.class, "ability_bad_distance"));
            return;
        }

        beforeAbilityUsed(hero, enemy);

        Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);

        int dmgBoost = augment.damageFactor(6 + buffedLvl());
        int totalDamage = damageRoll(hero) + dmgBoost;
        enemy.damage(totalDamage, this);

        // 50%概率使敌人失明2回合
        if (Random.Int(2) == 0) {
            Buff.affect(enemy, Blindness.class, 2f);
            enemy.sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "blinded"));
        }

        afterAbilityUsed(hero);

        onAbilityKill(hero, enemy);

        hero.spendAndNext(1f);
        updateQuickslot();
    }

    @Override
    public String abilityInfo() {
        int dmgBoost = levelKnown ? 6 + buffedLvl() : 6;
        if (levelKnown) {
            return Messages.get(this, "ability_desc", augment.damageFactor(min() + dmgBoost), augment.damageFactor(max() + dmgBoost));
        } else {
            return Messages.get(this, "typical_ability_desc", min(0) + dmgBoost, max(0) + dmgBoost);
        }
    }

    public String upgradeAbilityStat(int level) {
        int dmgBoost = 6 + level;
        return augment.damageFactor(min(level) + dmgBoost) + "-" + augment.damageFactor(max(level) + dmgBoost);
    }
}
```

## 测试验证

完成实现后，按以下步骤测试你的新武器：

1. **编译游戏**：确保没有编译错误
2. **启动游戏**：创建新游戏或加载存档
3. **获取武器**：通过控制台命令或修改代码获得暗影匕首
4. **测试属性**：
   - 检查基础伤害是否正确
   - 验证攻击速度是否更快
   - 测试力量需求计算
5. **测试决斗者能力**：
   - 切换到决斗者职业
   - 充能后使用暗影突袭能力
   - 验证额外伤害和失明效果
6. **测试附魔支持**：确保可以正常附魔和诅咒

## 进阶修改

你可以进一步扩展暗影匕首的功能：

1. **特殊附魔支持**：创建专属于匕首的附魔
2. **暴击系统**：添加内置暴击机制
3. **隐身协同**：与盗贼的隐身能力产生特殊效果
4. **连击系统**：连续攻击同一目标时增加伤害
5. **音效和视觉效果**：添加独特的攻击动画和音效

## 常见问题

**Q: 武器不显示在游戏中怎么办？**
A: 确保在 `Generator.java` 中正确注册了武器，并且概率值大于0。

**Q: 决斗者能力无法使用？**
A: 检查是否实现了 `targetingPrompt()` 方法，以及能力消耗是否正确设置。

**Q: 伤害计算不正确？**
A: 确保正确重写了 `min(int lvl)` 和 `max(int lvl)` 方法，并考虑了强化（augment）的影响。

**Q: 如何调整武器平衡性？**
A: 修改 `tier`、`DLY`、`ACC` 值，以及伤害公式中的常数来调整武器强度。

**Q: 武器图标显示错误？**
A: 确保 `image` 属性指向正确的精灵表位置，或者创建新的精灵资源。