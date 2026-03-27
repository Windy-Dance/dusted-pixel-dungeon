# 创建新卷轴教程

## 目标
本教程将指导你创建一个自定义卷轴物品。

## 前置知识
- 熟悉 Java 基础语法
- 了解 Scroll 基类

---

## 第一部分：卷轴类结构

### Scroll 基类关键字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `rune` | `String` | 符文标识 |
| `runeSprite` | `int` | 符文精灵索引 |

---

## 第二部分：创建卷轴类

```java
package com.dustedpixel.dustedpixeldungeon.items.scrolls;

import com.dustedpixel.dustedpixeldungeon.actors.buffs.Buff;
import com.dustedpixel.dustedpixeldungeon.actors.hero.Hero;
import com.dustedpixel.dustedpixeldungeon.effects.SpellSprite;
import com.dustedpixel.dustedpixeldungeon.messages.Messages;
import com.dustedpixel.dustedpixeldungeon.sprites.ItemSpriteSheet;
import com.dustedpixel.dustedpixeldungeon.utils.GLog;

public class ScrollOfShielding extends Scroll {

    {
        image = ItemSpriteSheet.SCROLL_SHIELDING;
    }

    @Override
    protected void doRead() {
        identify();

        // 施加护盾效果
        int shieldAmount = 20 + level() * 5;
        Buff.affect(curUser, Barrier.class).setShield(shieldAmount);

        // 视觉效果
        SpellSprite.show(curUser, SpellSprite.SHIELD);

        GLog.p(Messages.get(this, "effect", shieldAmount));
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", 20 + level() * 5);
    }

    @Override
    public int value() {
        return 40 * quantity();
    }
}
```

---

## 第三部分：注册流程

### 1. 注册符文

```java
// Scroll.java 中的符文映射
private static final LinkedHashMap<String, Integer> RUNES = new LinkedHashMap<>();
static {
    RUNES.put("kaunan", ItemSpriteSheet.SCROLL_KAUNAN);
    // 添加新符文
    RUNES.put("algiz", ItemSpriteSheet.SCROLL_ALGIZ);
}
```

### 2. 注册生成

```java
// Generator.java
Category.SCROLL.classes = new Class<?>[]{
    ScrollOfIdentify.class,
    // ...
    ScrollOfShielding.class
};
```

### 3. 本地化

```properties
# items.properties
items.scrolls.scrollofshielding.name=scroll of shielding
items.scrolls.scrollofshielding.desc=This scroll grants a protective barrier that absorbs %d damage.
items.scrolls.scrollofshielding.effect=A protective barrier surrounds you, absorbing %d damage!

# items_zh.properties
items.scrolls.scrollofshielding.name=护盾卷轴
items.scrolls.scrollofshielding.desc=这张卷轴赋予一个能吸收 %d 点伤害的防护屏障。
items.scrolls.scrollofshielding.effect=一道防护屏障环绕着你，可吸收 %d 点伤害！
```

---

## 测试验证

```
give ScrollOfShielding
identify
```

---

## 相关资源

- [Scroll API 参考](../../reference/items/scroll-api.md)
- [Buff API 参考](../../reference/actors/buff-api.md)