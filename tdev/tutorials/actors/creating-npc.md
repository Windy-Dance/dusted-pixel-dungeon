# 创建新 NPC 教程

## 目标
本教程将指导你创建一个可交互的 NPC 角色。

## 前置知识
- 熟悉 Java 基础语法
- 了解 Mob 和 NPC 基类

---

## 第一部分：NPC 基础结构

```java
package com.dustedpixel.dustedpixeldungeon.actors.mobs.npcs;

import com.dustedpixel.dustedpixeldungeon.actors.Char;
import com.dustedpixel.dustedpixeldungeon.actors.mobs.Mob;
import com.dustedpixel.dustedpixeldungeon.journal.Notes;
import com.dustedpixel.dustedpixeldungeon.messages.Messages;
import com.dustedpixel.dustedpixeldungeon.scenes.GameScene;
import com.dustedpixel.dustedpixeldungeon.sprites.NPCSprite;
import com.dustedpixel.dustedpixeldungeon.windows.WndDialogue;

public class MysteriousMerchant extends NPC {

    {
        spriteClass = MysteriousMerchantSprite.class;

        properties.add(Property.IMMOVABLE);
    }

    @Override
    public int defenseSkill(Char enemy) {
        return INFINITE_EVASION;  // NPC 无法被攻击
    }

    @Override
    public boolean interact(Char c) {
        if (c != Dungeon.hero) return false;

        // 显示对话窗口
        GameScene.show(new WndDialogue(
                this,
                Messages.get(this, "greeting"),
                new String[]{
                        Messages.get(this, "option_buy"),
                        Messages.get(this, "option_talk"),
                        Messages.get(this, "option_leave")
                },
                new WndDialogue.Listener() {
                    @Override
                    public void onSelect(int index) {
                        switch (index) {
                            case 0:
                                openShop();
                                break;
                            case 1:
                                showLore();
                                break;
                            case 2:
                                // 离开
                                break;
                        }
                    }
                }
        ));

        return true;
    }

    private void openShop() {
        // 实现商店界面
        GameScene.show(new WndShop(this));
    }

    private void showLore() {
        GLog.i(Messages.get(this, "lore"));
    }

    @Override
    public String description() {
        return Messages.get(this, "desc");
    }
}
```

---

## 第二部分：商店实现

```java
public class WndShop extends Window {

    private static final int WIDTH = 120;
    private static final int GAP = 2;

    public WndShop(NPC merchant) {
        super();
        
        resize(WIDTH, 0);
        
        // 创建物品列表
        ArrayList<Item> items = generateShopItems();
        
        int y = GAP;
        
        for (Item item : items) {
            ItemButton btn = new ItemButton(item);
            btn.setRect(GAP, y, WIDTH - GAP * 2, ItemButton.HEIGHT);
            add(btn);
            y += ItemButton.HEIGHT + GAP;
        }
        
        resize(WIDTH, y);
    }
}
```

---

## 第三部分：本地化

```properties
# messages.properties
actors.mobs.npcs.mysteriousmerchant.name=Mysterious Merchant
actors.mobs.npcs.mysteriousmerchant.desc=A hooded figure with wares to sell.
actors.mobs.npcs.mysteriousmerchant.greeting=Greetings, traveler...
actors.mobs.npcs.mysteriousmerchant.option_buy=I'd like to see your wares.
actors.mobs.npcs.mysteriousmerchant.option_talk=Tell me about yourself.
actors.mobs.npcs.mysteriousmerchant.option_leave=Goodbye.
actors.mobs.npcs.mysteriousmerchant.lore=I've traveled these depths for many years...

# messages_zh.properties
actors.mobs.npcs.mysteriousmerchant.name=神秘商人
actors.mobs.npcs.mysteriousmerchant.desc=一个兜帽遮面的人物，有着出售的商品。
actors.mobs.npcs.mysteriousmerchant.greeting=问候，旅行者...
actors.mobs.npcs.mysteriousmerchant.option_buy=我想看看你的货物。
actors.mobs.npcs.mysteriousmerchant.option_talk=告诉我关于你的事。
actors.mobs.npcs.mysteriousmerchant.option_leave=再见。
actors.mobs.npcs.mysteriousmerchant.lore=我已经在这些深处游历了很多年...
```

---

## 测试验证

```
spawn MysteriousMerchant
```

---

## 相关资源

- [Mob API 参考](../../reference/actors/mob-api.md)
- [Window API 参考](../../reference/ui/window-api.md)