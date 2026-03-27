# 创建新护甲教程

## 目标
本教程将指导你创建一个自定义护甲物品，包含完整的护甲类实现、刻印系统、精灵图注册和本地化配置。

## 前置知识
- 熟悉 Java 基础语法
- 了解 Item 和 Armor 基类
- 熟悉项目的类结构

---

## 第一部分：护甲类结构

### Armor 基类关键字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `tier` | `int` | 护甲阶数（1-5） |
| `DRMin` / `DRMax` | `int` | 伤害减免范围 |
| `STRReq` | `int` | 力量需求 |
| `glyph` | `Glyph` | 刻印效果 |

### 护甲阶数对照表

| 阶数 | 名称 | 基础DR | 力量需求 |
|------|------|--------|---------|
| 1 | 布甲 | 0-2 | 10 |
| 2 | 皮甲 | 1-4 | 11 |
| 3 | 鳞甲 | 2-6 | 12 |
| 4 | 板甲 | 3-8 | 13 |
| 5 | 重甲 | 4-12 | 14 |

---

## 第二部分：创建护甲类

```java
package com.dustedpixel.dustedpixeldungeon.items.armor;

import com.dustedpixel.dustedpixeldungeon.actors.Char;
import com.dustedpixel.dustedpixeldungeon.actors.hero.Hero;
import com.dustedpixel.dustedpixeldungeon.items.Item;
import com.dustedpixel.dustedpixeldungeon.messages.Messages;
import com.dustedpixel.dustedpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class CrystalArmor extends Armor {

    {
        image = ItemSpriteSheet.ARMOR_CRYSTAL;  // 精灵图索引
        tier = 3;  // 3阶护甲

        // 可选：设置独特属性
        bones = false;  // 不在英雄遗骸中生成
    }

    @Override
    public int DRMin(int level) {
        return 3 + level;  // 基础 3 + 升级加成
    }

    @Override
    public int DRMax(int level) {
        return 8 + 2 * level;  // 基础 8 + 升级加成
    }

    @Override
    public int STRReq(int lvl) {
        return 11 - lvl;  // 每级减少1点力量需求
    }

    @Override
    public String desc() {
        String desc = super.desc();
        desc += "\n\n" + Messages.get(this, "effect_desc");
        return desc;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // 护甲效果：有概率反弹部分伤害
        if (defender.buff(CrystalReflect.class) == null && Random.Float() < 0.15f + level() * 0.05f) {
            int reflectDmg = Math.round(damage * 0.3f);
            if (reflectDmg > 0) {
                attacker.damage(reflectDmg, this);
                defender.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "reflect"));
            }
        }
        return super.proc(attacker, defender, damage);
    }

    @Override
    public int value() {
        return 150 * quantity();  // 售价
    }

    // 内部 Buff 类
    public static class CrystalReflect extends Buff {
        // 用于防止无限反弹
    }
}
```

---

## 第三部分：创建自定义刻印

```java
package com.dustedpixel.dustedpixeldungeon.items.armor.glyphs;

import com.dustedpixel.dustedpixeldungeon.actors.Char;
import com.dustedpixel.dustedpixeldungeon.actors.buffs.Buff;
import com.dustedpixel.dustedpixeldungeon.items.armor.Armor;
import com.dustedpixel.dustedpixeldungeon.sprites.ItemSprite;

public class Crystallization extends Armor.Glyph {

    private static ItemSprite.Glowing CRYSTAL = new ItemSprite.Glowing(0x4488FF);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        // 10% 概率获得护盾
        if (Random.Int(10) == 0) {
            int shield = Math.round(damage * 0.5f);
            Buff.affect(defender, Barrier.class).setShield(shield);
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return CRYSTAL;  // 发光效果颜色
    }
}
```

---

## 第四部分：注册流程

### 1. 注册精灵图

在 `ItemSpriteSheet.java` 中：

```java
// 添加常量
public static final int ARMOR_CRYSTAL = 100;  // 选择未使用的索引

// 在静态块中分配区域
assignItemRect(ARMOR_CRYSTAL, 16, 16);
```

### 2. 注册生成

在 `Generator.java` 中：

```java
Category.ARMOR.classes = new Class<?>[]{
    ClothArmor.class,
    LeatherArmor.class,
    // ...
    CrystalArmor.class  // 添加
};
```

### 3. 本地化

在 `items.properties` 中：

```properties
items.armor.crystalarmor.name=crystal armor
items.armor.crystalarmor.desc=A suit of armor made from enchanted crystals. It seems to shimmer with inner light.
items.armor.crystalarmor.effect_desc=When struck, there is a chance to reflect 30% of the damage back to the attacker.
items.armor.crystalarmor.reflect=reflected!
```

在 `items_zh.properties` 中：

```properties
items.armor.crystalarmor.name=水晶护甲
items.armor.crystalarmor.desc=一套由附魔水晶制成的护甲，似乎闪烁着内在的光芒。
items.armor.crystalarmor.effect_desc=被击中时，有概率将30%的伤害反弹给攻击者。
items.armor.crystalarmor.reflect=反弹了！
```

---

## 测试验证

### 编译测试
```bash
./gradlew compileJava
```

### 游戏内测试
```
give CrystalArmor  -- 获得护甲
identify           -- 鉴定
upgrade            -- 升级测试
```

### 测试清单
- [ ] 护甲正确显示
- [ ] 装备/卸下正常
- [ ] 伤害减免正确
- [ ] 特殊效果触发
- [ ] 升级属性正确
- [ ] 刻印效果正常

---

## 相关资源

- [Armor API 参考](../../reference/items/armor-api.md)
- [Item API 参考](../../reference/items/item-api.md)
- [注册指南](../../integration/registration-guide.md)