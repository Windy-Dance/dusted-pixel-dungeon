# 创建新法杖教程

## 目标
本教程将指导你创建一个自定义法杖物品，包含魔法射击、充能系统和特殊效果。

## 前置知识
- 熟悉 Java 基础语法
- 了解 Wand 基类
- 了解 Ballistica 弹道系统

---

## 第一部分：法杖类结构

### Wand 基类关键字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `maxCharges` | `int` | 最大充能数 |
| `charges` | `float` | 当前充能 |
| `partialCharge` | `float` | 部分充能 |
| `chargeRate` | `float` | 充能速率 |

---

## 第二部分：创建法杖类

```java
package com.dustedpixel.dustedpixeldungeon.items.wands;

import com.dustedpixel.dustedpixeldungeon.Assets;
import com.dustedpixel.dustedpixeldungeon.Dungeon;
import com.dustedpixel.dustedpixeldungeon.actors.Actor;
import com.dustedpixel.dustedpixeldungeon.actors.Char;
import com.dustedpixel.dustedpixeldungeon.actors.buffs.Buff;
import com.dustedpixel.dustedpixeldungeon.effects.MagicMissile;
import com.dustedpixel.dustedpixeldungeon.items.weapon.melee.MagesStaff;
import com.dustedpixel.dustedpixeldungeon.mechanics.Ballistica;
import com.dustedpixel.dustedpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WandOfFrost extends Wand {

    {
        image = ItemSpriteSheet.WAND_FROST;

        // 充能设置
        maxCharges = 3 + level();  // 基础3充能，每级+1
    }

    @Override
    public int maxCharges(int level) {
        return 3 + level;
    }

    @Override
    protected void onZap(Ballistica bolt) {
        Char ch = Actor.findChar(bolt.collisionPos);

        if (ch != null) {
            // 计算伤害
            int damage = damageRoll();

            // 应用冰冻效果
            if (ch.buff(Frost.class) == null) {
                Buff.affect(ch, Frost.class, Frost.duration(ch) * (1f + level() * 0.1f));
            }

            // 造成伤害
            ch.damage(damage, this);

            // 视觉效果
            ch.sprite.burst(new PointF(0.5f, 0.8f, 1f), damage / 2);

            Sample.INSTANCE.play(Assets.Sounds.SHATTER);
        }
    }

    @Override
    protected int chargesPerCast() {
        return 1;  // 每次射击消耗1充能
    }

    @Override
    public void fx(Ballistica bolt, Callback callback) {
        // 发射魔法飞弹效果
        MagicMissile.boltFromChar(
                curUser.sprite.parent,
                MagicMissile.FROST,
                curUser.sprite,
                bolt.collisionPos,
                callback
        );
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
    }

    @Override
    public int damageRoll(int level) {
        return Random.NormalIntRange(5 + level, 10 + level * 2);
    }

    @Override
    public String statsDesc() {
        return Messages.get(this, "stats_desc", damageRoll());
    }

    @Override
    public int value() {
        return 50 * quantity();
    }
}
```

---

## 第三部分：充能系统详解

### 充能计算

```java
// 在 Wand 类中
public void charge(Char target) {
    // 充能恢复
    partialCharge += 1f / chargeRate;
    
    while (partialCharge >= 1f) {
        partialCharge -= 1f;
        charges++;
        
        if (charges > maxCharges()) {
            charges = maxCharges();
        }
    }
    
    updateQuickslot();
}

// 消耗充能
public void useCharge() {
    charges -= chargesPerCast();
    updateQuickslot();
}
```

### 自定义充能恢复

```java
@Override
public float chargeRate() {
    // 基础恢复速率，可被天赋等修改
    return 10f;  // 10回合恢复1充能
}
```

---

## 第四部分：弹道系统

### Ballistica 类

```java
// 创建弹道路径
Ballistica bolt = new Ballistica(
    casterPos,      // 起点位置
    targetPos,      // 目标位置
    Ballistica.MAGIC_BOLT  // 路径类型
);

// 路径类型
Ballistica.STOP_TARGET   // 在目标处停止
Ballistica.STOP_CHARS    // 碰到角色停止
Ballistica.STOP_SOLID    // 碰到实体地形停止
Ballistica.MAGIC_BOLT    // 法术类型（穿透某些地形）
```

---

## 第五部分：注册流程

### 1. 注册精灵图

```java
// ItemSpriteSheet.java
public static final int WAND_FROST = 50;
assignItemRect(WAND_FROST, 16, 16);
```

### 2. 注册生成

```java
// Generator.java
Category.WAND.classes = new Class<?>[]{
    WandOfMagicMissile.class,
    // ...
    WandOfFrost.class
};
```

### 3. 本地化

```properties
# items.properties
items.wands.wandoffrost.name=wand of frost
items.wands.wandoffrost.desc=This wand shoots bolts of icy energy, freezing enemies in their tracks.
items.wands.wandoffrost.stats_desc=This wand deals %d damage and freezes targets.

# items_zh.properties
items.wands.wandoffrost.name=冰霜法杖
items.wands.wandoffrost.desc=这根法杖发射冰霜能量弹，将敌人冻结在原地。
items.wands.wandoffrost.stats_desc=这根法杖造成 %d 点伤害并冻结目标。
```

---

## 测试验证

```bash
# 编译
./gradlew compileJava

# 游戏内测试
give WandOfFrost
upgrade
```

---

## 相关资源

- [Wand API 参考](../../reference/items/wand-api.md)
- [Ballistica 类文档](../../reference/mechanics/ballistica-api.md)
- [注册指南](../../integration/registration-guide.md)