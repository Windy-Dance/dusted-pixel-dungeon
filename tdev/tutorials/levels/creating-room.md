# 创建新房间教程

## 目标
本教程将指导你创建一个自定义房间类型。

## 前置知识
- 熟悉 Java 基础语法
- 了解 Room 类

---

## 第一部分：房间类结构

```java
package com.dustedpixel.dustedpixeldungeon.levels.rooms.special;

import com.dustedpixel.dustedpixeldungeon.Dungeon;
import com.dustedpixel.dustedpixeldungeon.items.Generator;
import com.dustedpixel.dustedpixeldungeon.items.Item;
import com.dustedpixel.dustedpixeldungeon.items.keys.CrystalKey;
import com.dustedpixel.dustedpixeldungeon.levels.Level;
import com.dustedpixel.dustedpixeldungeon.levels.Terrain;
import com.dustedpixel.dustedpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class CrystalVaultRoom extends SpecialRoom {

    @Override
    public int minWidth() {
        return 5;
    }

    public int maxWidth() {
        return 7;
    }

    @Override
    public int minHeight() {
        return 5;
    }

    public int maxHeight() {
        return 7;
    }

    @Override
    public int maxConnections(int direction) {
        return 1;  // 只有一个入口
    }

    @Override
    public void paint(Level level) {
        // 填充墙壁
        Painter.fill(level, this, Terrain.WALL);

        // 填充地面
        Painter.fill(level, this, 1, Terrain.EMPTY);

        // 在中心放置宝箱基座
        Point center = center();
        int centerCell = level.pointToCell(center);
        Painter.set(level, centerCell, Terrain.PEDESTAL);

        // 放置物品
        Item prize = prize(level);
        level.drop(prize, centerCell);

        // 添加水晶门
        Point entrance = entrance();
        int doorCell = level.pointToCell(entrance);
        level.map[doorCell] = Terrain.CRYSTAL_DOOR;

        // 放置水晶钥匙
        level.drop(new CrystalKey(Dungeon.depth), level.pointToCell(random()));

        // 添加装饰
        for (Point p : getPoints()) {
            if (Random.Int(10) == 0) {
                Painter.set(level, level.pointToCell(p), Terrain.CRYSTAL_DECO);
            }
        }
    }

    protected Item prize(Level level) {
        // 生成奖励物品
        return Generator.random(Generator.Category.RING);
    }
}
```

---

## 第二部分：房间类型

| 类型 | 基类 | 说明 |
|------|------|------|
| `StandardRoom` | Room | 普通房间 |
| `SpecialRoom` | Room | 特殊房间（有特殊物品/机制） |
| `SecretRoom` | Room | 秘密房间 |
| `BossRoom` | Room | Boss 房间 |
| `ShopRoom` | Room | 商店房间 |
| `ConnectionRoom` | Room | 连接房间 |

---

## 第三部分：注册房间

```java
// 在 SpecialRoom 类中
public static final HashSet<Class<? extends SpecialRoom>> SPECIALS = new HashSet<>();

static {
    SPECIALS.add(CrystalVaultRoom.class);
    // ... 其他房间
}
```

---

## 测试验证

```
depth 6  -- 观察是否有新房间生成
```

---

## 相关资源

- [Room API 参考](../../reference/levels/room-api.md)
- [Level API 参考](../../reference/levels/level-api.md)