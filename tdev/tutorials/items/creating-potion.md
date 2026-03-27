# 创建新药水教程

## 目标
本教程将指导你创建一个自定义药水物品。

## 前置知识
- 熟悉 Java 基础语法
- 了解 Potion 基类

---

## 第一部分：药水类结构

### Potion 基类关键字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `color` | `String` | 颜色标识 |
| `splashDmg` | `int` | 投掷伤害 |
| `colorSprite` | `int` | 颜色精灵索引 |

---

## 第二部分：创建药水类

```java
package com.dustedpixel.dustedpixeldungeon.items.potions;

import com.dustedpixel.dustedpixeldungeon.actors.buffs.Buff;
import com.dustedpixel.dustedpixeldungeon.actors.hero.Hero;
import com.dustedpixel.dustedpixeldungeon.messages.Messages;
import com.dustedpixel.dustedpixeldungeon.sprites.ItemSpriteSheet;
import com.dustedpixel.dustedpixeldungeon.utils.GLog;

public class PotionOfSwiftness extends Potion {

    {
        image = ItemSpriteSheet.POTION_SWIFTNESS;
    }

    @Override
    public void apply(Hero hero) {
        identify();

        // 施加速度 Buff
        Buff.affect(hero, Swiftness.class, Swiftness.DURATION + level() * 5);

        GLog.p(Messages.get(this, "effect"));
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", Swiftness.DURATION + level() * 5);
    }

    @Override
    public int value() {
        return 30 * quantity();
    }
}
```

---

## 第三部分：Buff 效果实现

```java
public class Swiftness extends Buff {

    public static final float DURATION = 50f;

    {
        type = buffType.POSITIVE;
        announced = true;
    }

    @Override
    public int icon() {
        return BuffIndicator.SWIFTNESS;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }

    @Override
    public float speedMultiplier() {
        return 2f;  // 双倍速度
    }
}
```

---

## 第四部分：注册流程

### 1. 注册颜色

```java
// Potion.java 中的颜色映射
private static final LinkedHashMap<String, Integer> COLORS = new LinkedHashMap<>();
static {
    COLORS.put("crimson", ItemSpriteSheet.POTION_CRIMSON);
    COLORS.put("azure", ItemSpriteSheet.POTION_AZURE);
    // 添加新颜色
    COLORS.put("jade", ItemSpriteSheet.POTION_JADE);
}
```

### 2. 注册生成

```java
// Generator.java
Category.POTION.classes = new Class<?>[]{
    PotionOfHealing.class,
    // ...
    PotionOfSwiftness.class
};
```

### 3. 本地化

```properties
# items.properties
items.potions.potionofswiftness.name=potion of swiftness
items.potions.potionofswiftness.desc=This potion grants increased movement speed for %d turns.
items.potions.potionofswiftness.effect=You feel incredibly swift!

# items_zh.properties
items.potions.potionofswiftness.name=迅捷药水
items.potions.potionofswiftness.desc=这瓶药水提供持续 %d 回合的移动速度加成。
items.potions.potionofswiftness.effect=你感到无比轻盈迅捷！
```

---

## 测试验证

```
give PotionOfSwiftness
identify
```

---

## 相关资源

- [Potion API 参考](../../reference/items/potion-api.md)
- [Buff API 参考](../../reference/actors/buff-api.md)