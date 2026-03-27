# 创建新 Buff 教程

## 目标
本教程将指导你创建一个自定义 Buff 效果。

## 前置知识
- 熟悉 Java 基础语法
- 了解 Buff 基类

---

## 第一部分：Buff 基础结构

```java
package com.dustedpixel.dustedpixeldungeon.actors.buffs;

import com.dustedpixel.dustedpixeldungeon.actors.Char;
import com.dustedpixel.dustedpixeldungeon.messages.Messages;
import com.dustedpixel.dustedpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class FrostShield extends Buff {

    {
        type = buffType.POSITIVE;
        announced = true;
    }

    private int shieldAmount;

    @Override
    public int icon() {
        return BuffIndicator.FROST_SHIELD;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", shieldAmount);
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            // 附着时的效果
            updateShield();
            return true;
        }
        return false;
    }

    @Override
    public boolean act() {
        // 每回合逻辑
        shieldAmount -= 1;

        if (shieldAmount <= 0) {
            detach();
        }

        spend(TICK);
        return true;
    }

    public void setShield(int amount) {
        shieldAmount = amount;
    }

    public int absorbDamage(int damage) {
        if (shieldAmount <= 0) return damage;

        int absorbed = Math.min(shieldAmount, damage);
        shieldAmount -= absorbed;

        if (shieldAmount <= 0) {
            detach();
        }

        return damage - absorbed;
    }

    private static final String SHIELD = "shield";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SHIELD, shieldAmount);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        shieldAmount = bundle.getInt(SHIELD);
    }
}
```

---

## 第二部分：Buff 类型

| 类型 | 说明 | 示例 |
|------|------|------|
| `POSITIVE` | 正面效果 | 祝福、护盾 |
| `NEGATIVE` | 负面效果 | 中毒、减速 |
| `NEUTRAL` | 中性效果 | 标记 |

---

## 第三部分：Buff 图标

在 `BuffIndicator.java` 中注册图标：

```java
public static final int FROST_SHIELD = 100;

private static final int[] ICONS = {
    // ... 现有图标
    FROST_SHIELD
};
```

---

## 第四部分：使用 Buff

```java
// 施加 Buff
Buff.affect(hero, FrostShield.class).setShield(30);

// 检查 Buff
FrostShield shield = hero.buff(FrostShield.class);
if (shield != null) {
    damage = shield.absorbDamage(damage);
}

// 移除 Buff
Buff.detach(hero, FrostShield.class);
```

---

## 测试验证

```
give PotionOfShielding
```

---

## 相关资源

- [Buff API 参考](../../reference/actors/buff-api.md)