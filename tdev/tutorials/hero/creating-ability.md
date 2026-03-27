# 创建新护甲能力教程

## 目标
本教程将指导你创建一个自定义护甲能力。

## 前置知识
- 熟悉 Java 基础语法
- 了解 ArmorAbility 类

---

## 第一部分：护甲能力结构

```java
package com.dustedpixel.dustedpixeldungeon.actors.hero.abilities;

import com.dustedpixel.dustedpixeldungeon.actors.Actor;
import com.dustedpixel.dustedpixeldungeon.actors.buffs.Buff;
import com.dustedpixel.dustedpixeldungeon.actors.hero.Hero;
import com.dustedpixel.dustedpixeldungeon.actors.hero.Talent;
import com.dustedpixel.dustedpixeldungeon.items.armor.Armor;
import com.dustedpixel.dustedpixeldungeon.items.wands.WandOfBlastWave;
import com.dustedpixel.dustedpixeldungeon.messages.Messages;
import com.dustedpixel.dustedpixeldungeon.scenes.GameScene;
import com.dustedpixel.dustedpixeldungeon.ui.HeroIcon;

public class CrystalBurst extends ArmorAbility {

    {
        baseChargeUse = 35f;  // 基础充能消耗
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public int targetedPos(Hero user, int dst) {
        return dst;
    }

    @Override
    protected void activate(Hero hero, Integer target) {
        if (target == null) return;

        // 消耗充能
        chargeUse = baseChargeUse;
        if (hero.hasTalent(Talent.LIGHT_CRYSTAL)) {
            chargeUse -= 5 * hero.pointsInTalent(Talent.LIGHT_CRYSTAL);
        }

        // 创建爆炸效果
        CellEmitter.get(target).burst(CrystalParticle.FACTORY, 20);

        // 对范围内敌人造成伤害
        for (int cell : PathFinder.NEIGHBOURS9) {
            int pos = target + cell;
            Char ch = Actor.findChar(pos);
            if (ch != null && ch.alignment != hero.alignment) {
                int damage = Math.round(hero.HT * 0.3f);
                ch.damage(damage, this);

                // 冰冻效果
                Buff.affect(ch, Frost.class, Frost.duration(ch) * 0.5f);
            }
        }

        // 标记使用
        hero.spendAndNext(Actor.TICK);
    }

    @Override
    public int icon() {
        return HeroIcon.CRYSTAL_BURST;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{
                Talent.LIGHT_CRYSTAL,
                Talent.SHATTERING_CRYSTAL,
                Talent.CRYSTAL_BARRIER
        };
    }
}
```

---

## 第二部分：注册护甲能力

```java
// 在 HeroClass 枚举中或选择界面中注册
// 通常在英雄选择子职业时设置
```

---

## 第三部分：本地化

```properties
# messages.properties
abilities.warrior.crystalburst.name=Crystal Burst
abilities.warrior.crystalburst.desc=Release a burst of crystalline energy, dealing 30%% of your max HP to nearby enemies and freezing them.
abilities.warrior.crystalburst.prompt=Choose a location to burst

# messages_zh.properties
abilities.warrior.crystalburst.name=水晶爆发
abilities.warrior.crystalburst.desc=释放一股水晶能量爆发，对周围敌人造成最大生命值30%%的伤害并冰冻它们。
abilities.warrior.crystalburst.prompt=选择爆发位置
```

---

## 相关资源

- [英雄系统详解](../../systems/hero-system-detailed.md)
- [天赋系统详解](../../systems/talent-system-detailed.md)