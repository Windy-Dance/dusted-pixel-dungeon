# 创建新 Boss 教程

## 目标
本教程将指导你创建一个自定义 Boss 怪物，包含多阶段战斗、特殊能力和掉落奖励。

## 前置知识
- 熟悉 Java 基础语法
- 了解 Mob 基类
- 了解 Boss 机制

---

## 第一部分：Boss 基础结构

```java
package com.dustedpixel.dustedpixeldungeon.actors.mobs;

import com.dustedpixel.dustedpixeldungeon.Dungeon;
import com.dustedpixel.dustedpixeldungeon.actors.Actor;
import com.dustedpixel.dustedpixeldungeon.actors.Char;
import com.dustedpixel.dustedpixeldungeon.actors.buffs.Buff;
import com.dustedpixel.dustedpixeldungeon.items.keys.SkeletonKey;
import com.dustedpixel.dustedpixeldungeon.levels.Level;
import com.dustedpixel.dustedpixeldungeon.messages.Messages;
import com.dustedpixel.dustedpixeldungeon.sprites.BossSprite;
import com.dustedpixel.dustedpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class CrystalGuardian extends Mob {

    {
        spriteClass = CrystalGuardianSprite.class;

        HP = HT = 300;
        defenseSkill = 20;

        EXP = 50;
        maxLvl = 30;

        // Boss 特殊属性
        properties.add(Property.BOSS);
        properties.add(Property.IMMOVABLE);
        properties.add(Property.LARGE);
    }

    // 战斗阶段
    private int phase = 1;
    private static final String PHASE = "phase";

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(20 + phase * 5, 30 + phase * 10);
    }

    @Override
    public int attackSkill(Char target) {
        return 30 + phase * 5;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(10 + phase * 5, 20 + phase * 10);
    }

    @Override
    protected boolean act() {
        // Boss AI 逻辑
        if (phase == 1 && HP < HT * 0.5f) {
            enterPhase2();
        }

        return super.act();
    }

    private void enterPhase2() {
        phase = 2;
        GLog.w(Messages.get(this, "phase2"));

        // 召唤小怪
        for (int i = 0; i < 3; i++) {
            CrystalMinion minion = new CrystalMinion();
            minion.pos = Dungeon.level.randomRespawnCell(minion);
            if (minion.pos != -1) {
                GameScene.add(minion);
            }
        }

        sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "summon"));
    }

    @Override
    public void die(Object cause) {
        super.die(cause);

        // 掉落骷髅钥匙
        Dungeon.level.drop(new SkeletonKey(Dungeon.depth), pos).sprite.drop();

        GLog.h(Messages.get(this, "defeated"));
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PHASE, phase);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        phase = bundle.getInt(PHASE);
    }
}
```

---

## 第二部分：Boss 精灵

```java
public class CrystalGuardianSprite extends MobSprite {

    public CrystalGuardianSprite() {
        super();
        
        texture(Assets.Sprites.CRYSTAL_GUARDIAN);
        
        TextureFilm frames = new TextureFilm(texture, 32, 32);
        
        idle = new Animation(8, true);
        idle.frames(frames, 0, 1, 2, 3);
        
        run = new Animation(12, true);
        run.frames(frames, 4, 5, 6, 7);
        
        attack = new Animation(15, false);
        attack.frames(frames, 8, 9, 10, 11);
        
        die = new Animation(12, false);
        die.frames(frames, 12, 13, 14, 15);
        
        play(idle);
    }
    
    public void enterPhase2() {
        // 改变颜色
        tint(0xFF4444, 1f);
    }
}
```

---

## 第三部分：Boss 房间

```java
public class CrystalGuardianRoom extends SpecialRoom {

    @Override
    public int minWidth() { return 13; }
    public int maxWidth() { return 15; }
    public int minHeight() { return 13; }
    public int maxHeight() { return 15; }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);
        
        // 放置 Boss
        CrystalGuardian boss = new CrystalGuardian();
        boss.pos = level.pointToCell(center());
        level.mobs.add(boss);
    }
}
```

---

## 第四部分：注册与本地化

### 本地化

```properties
# messages.properties
actors.mobs.crystalguardian.name=Crystal Guardian
actors.mobs.crystalguardian.desc=A massive crystalline entity guarding the depths.
actors.mobs.crystalguardian.phase2=The Guardian shatters and reforms!
actors.mobs.crystalguardian.summon=Minions rise!
actors.mobs.crystalguardian.defeated=The Guardian crumbles to dust...

# messages_zh.properties
actors.mobs.crystalguardian.name=水晶守护者
actors.mobs.crystalguardian.desc=一个巨大的水晶实体，守护着深处的秘密。
actors.mobs.crystalguardian.phase2=守护者碎裂并重新凝聚！
actors.mobs.crystalguardian.summon=仆从升起！
actors.mobs.crystalguardian.defeated=守护者化为尘埃...
```

---

## 测试验证

```
spawn CrystalGuardian
god  -- 测试阶段转换
```

---

## 相关资源

- [Mob API 参考](../../reference/actors/mob-api.md)
- [创建怪物教程](creating-mob.md)