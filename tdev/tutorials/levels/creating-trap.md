# 创建新陷阱教程

## 目标
本教程将指导你创建一个自定义陷阱。

## 前置知识
- 熟悉 Java 基础语法
- 了解 Trap 类

---

## 第一部分：陷阱类结构

```java
package com.dustedpixel.dustedpixeldungeon.levels.traps;

import com.dustedpixel.dustedpixeldungeon.Dungeon;
import com.dustedpixel.dustedpixeldungeon.actors.Actor;
import com.dustedpixel.dustedpixeldungeon.actors.Char;
import com.dustedpixel.dustedpixeldungeon.actors.buffs.Buff;
import com.dustedpixel.dustedpixeldungeon.effects.CellEmitter;
import com.dustedpixel.dustedpixeldungeon.effects.particles.ShadowParticle;
import com.dustedpixel.dustedpixeldungeon.messages.Messages;
import com.dustedpixel.dustedpixeldungeon.sprites.TrapSprite;

public class CrystalTrap extends Trap {

    {
        color = TrapSprite.CRYSTAL;
        shape = TrapSprite.STARS;

        // 可以被发现
        canBeHidden = true;
        canBeSearched = true;
    }

    @Override
    public void activate() {
        Char ch = Actor.findChar(pos);

        if (ch != null) {
            // 造成伤害
            int damage = Math.round(ch.HT * 0.15f);
            ch.damage(damage, this);

            // 施加冰冻
            Buff.affect(ch, Frost.class, Frost.duration(ch) * 0.5f);

            // 视觉效果
            CellEmitter.get(pos).burst(ShadowParticle.FROST, 10);
        }
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }

    @Override
    public void disarm() {
        // 陷阱被解除
        super.disarm();

        // 可以掉落物品
        if (Random.Float() < 0.1f) {
            Dungeon.level.drop(new CrystalShard(), pos);
        }
    }
}
```

---

## 第二部分：陷阱属性

| 属性 | 类型 | 说明 |
|------|------|------|
| `color` | int | 陷阱颜色 |
| `shape` | int | 陷阱形状 |
| `canBeHidden` | boolean | 是否可隐藏 |
| `canBeSearched` | boolean | 是否可被搜索发现 |
| `active` | boolean | 是否激活 |

---

## 第三部分：注册陷阱

```java
// 在 Generator.java 中注册
Category.TRAP.classes = new Class<?>[]{
    ToxicTrap.class,
    // ...
    CrystalTrap.class
};
```

---

## 本地化

```properties
# messages.properties
levels.traps.crystaltrap.name=crystal trap
levels.traps.crystaltrap.desc=A trap that releases freezing crystals when triggered.

# messages_zh.properties
levels.traps.crystaltrap.name=水晶陷阱
levels.traps.crystaltrap.desc=一个触发时会释放冰冻水晶的陷阱。
```

---

## 测试验证

```
spawn CrystalTrap
```

---

## 相关资源

- [Level API 参考](../../reference/levels/level-api.md)