# 创建新牧师法术教程

## 目标
本教程将指导你创建一个自定义牧师法术。

## 前置知识
- 熟悉 Java 基础语法
- 了解 HolyTome 和 Spell 系统

---

## 第一部分：法术结构

```java
package com.dustedpixel.dustedpixeldungeon.actors.hero.spells;

import com.dustedpixel.dustedpixeldungeon.actors.Actor;
import com.dustedpixel.dustedpixeldungeon.actors.Char;
import com.dustedpixel.dustedpixeldungeon.actors.buffs.Buff;
import com.dustedpixel.dustedpixeldungeon.actors.hero.Hero;
import com.dustedpixel.dustedpixeldungeon.actors.hero.Talent;
import com.dustedpixel.dustedpixeldungeon.items.artifacts.HolyTome;
import com.dustedpixel.dustedpixeldungeon.messages.Messages;
import com.dustedpixel.dustedpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class DivineShield extends Spell {

    public static final DivineShield INSTANCE = new DivineShield();

    {
        image = 10;  // 法术图标
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {
        // 消耗充能
        int chargeCost = 5 - hero.pointsInTalent(Talent.EFFICIENT_SHIELD);

        if (tome.charge < chargeCost) {
            GLog.w(Messages.get(this, "no_charge"));
            return;
        }

        tome.charge -= chargeCost;

        // 施加护盾
        int shieldAmount = 20 + hero.lvl * 2;
        Buff.affect(hero, ShieldBuff.class).setShield(shieldAmount);

        // 视觉效果
        hero.sprite.emitter().burst(Speck.factory(Speck.SHIELD), 10);

        GLog.p(Messages.get(this, "effect", shieldAmount));
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }

    // 护盾 Buff
    public static class ShieldBuff extends Buff {

        private int shield;

        {
            type = buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return BuffIndicator.SHIELDING;
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", shield);
        }

        public void setShield(int amount) {
            shield = amount;
        }

        public int absorbDamage(int damage) {
            int absorbed = Math.min(shield, damage);
            shield -= absorbed;
            if (shield <= 0) detach();
            return damage - absorbed;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put("shield", shield);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            shield = bundle.getInt("shield");
        }
    }
}
```

---

## 第二部分：注册法术

```java
// 在 HolyTome 类中添加法术
public static final Spell[] SPELLS = {
    Smite.INSTANCE,
    // ...
    DivineShield.INSTANCE
};
```

---

## 第三部分：本地化

```properties
# messages.properties
spells.divineshield.name=Divine Shield
spells.divineshield.desc=Create a protective barrier that absorbs damage equal to your level × 2 + 20.
spells.divineshield.effect=A divine shield surrounds you, absorbing %d damage!
spells.divineshield.no_charge=Not enough charge!

# messages_zh.properties
spells.divineshield.name=神圣护盾
spells.divineshield.desc=创建一个防护屏障，吸收相当于等级×2+20的伤害。
spells.divineshield.effect=一道神圣护盾环绕着你，可吸收 %d 点伤害！
spells.divineshield.no_charge=充能不足！
```

---

## 相关资源

- [天赋系统详解](../../systems/talent-system-detailed.md)
- [Buff API 参考](../../reference/actors/buff-api.md)