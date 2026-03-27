# 创建新关卡教程

## 目标
本教程将指导你创建一个自定义关卡类型。

## 前置知识
- 熟悉 Java 基础语法
- 了解 Level 和 RegularLevel 类

---

## 第一部分：关卡类结构

```java
package com.dustedpixel.dustedpixeldungeon.levels;

import com.dustedpixel.dustedpixeldungeon.Assets;
import com.dustedpixel.dustedpixeldungeon.Bones;
import com.dustedpixel.dustedpixeldungeon.Dungeon;
import com.dustedpixel.dustedpixeldungeon.actors.Actor;
import com.dustedpixel.dustedpixeldungeon.actors.mobs.Mob;
import com.dustedpixel.dustedpixeldungeon.items.Heap;
import com.dustedpixel.dustedpixeldungeon.items.Item;
import com.dustedpixel.dustedpixeldungeon.levels.rooms.Room;
import com.dustedpixel.dustedpixeldungeon.levels.rooms.special.SpecialRoom;
import com.dustedpixel.dustedpixeldungeon.messages.Messages;

public class CrystalCavernLevel extends RegularLevel {

    {
        color1 = 0x4488FF;
        color2 = 0x88CCFF;
    }

    @Override
    protected ArrayList<Room> initRooms() {
        ArrayList<Room> rooms = super.initRooms();

        // 添加特殊房间
        rooms.add(new CrystalShrineRoom());

        return rooms;
    }

    @Override
    protected Painter painter() {
        return new CrystalCavernPainter();
    }

    @Override
    protected void createMobs() {
        // 自定义怪物生成
        int mobsToCreate = 5 + Dungeon.depth / 2;

        for (int i = 0; i < mobsToCreate; i++) {
            Mob mob = Bestiary.mob(Dungeon.depth);
            mob.pos = randomRespawnCell(mob);
            if (mob.pos != -1) {
                mobs.add(mob);
            }
        }
    }

    @Override
    protected void createItems() {
        super.createItems();

        // 添加特殊物品
        if (Random.Float() < 0.2f) {
            Item item = new CrystalShard();
            int pos = randomDropCell();
            drop(item, pos).type = Heap.Type.CRYSTAL;
        }
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case Terrain.CRYSTAL_WALL:
                return Messages.get(this, "crystal_wall_name");
            case Terrain.CRYSTAL_FLOOR:
                return Messages.get(this, "crystal_floor_name");
            default:
                return super.tileName(tile);
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.CRYSTAL_WALL:
                return Messages.get(this, "crystal_wall_desc");
            case Terrain.CRYSTAL_FLOOR:
                return Messages.get(this, "crystal_floor_desc");
            default:
                return super.tileDesc(tile);
        }
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();

        // 添加水晶闪烁效果
        for (int i = 0; i < length(); i++) {
            if (map[i] == Terrain.CRYSTAL_WALL) {
                visuals.add(new CrystalVisual(i));
            }
        }

        return visuals;
    }
}
```

---

## 第二部分：Painter 类

```java
public class CrystalCavernPainter extends Painter {

    @Override
    public void paint(Level level, Room room) {
        // 绘制房间
        fill(level, room, Terrain.WALL);
        fill(level, room, 1, Terrain.CRYSTAL_FLOOR);
        
        // 添加水晶装饰
        for (Point p : room.getPoints()) {
            if (Random.Int(20) == 0) {
                level.map[level.pointToCell(p)] = Terrain.CRYSTAL_CLUSTER;
            }
        }
        
        // 绘制门
        for (Room.Door door : room.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
    }
}
```

---

## 第三部分：注册关卡

```java
// 在 Dungeon 类中
public static Level newLevel() {
    switch (depth) {
        case 1-5: return new SewerLevel();
        case 6-10: return new PrisonLevel();
        // ...
        case 21-25: return new CrystalCavernLevel();
        default: return new HallsLevel();
    }
}
```

---

## 本地化

```properties
# messages.properties
levels.crystalcavernlevel.crystal_wall_name=crystal wall
levels.crystalcavernlevel.crystal_wall_desc=A shimmering wall of crystalline formations.
levels.crystalcavernlevel.crystal_floor_name=crystal floor
levels.crystalcavernlevel.crystal_floor_desc=The floor glitters with embedded crystals.

# messages_zh.properties
levels.crystalcavernlevel.crystal_wall_name=水晶墙
levels.crystalcavernlevel.crystal_wall_desc=闪烁着水晶形成的墙壁。
levels.crystalcavernlevel.crystal_floor_name=水晶地面
levels.crystalcavernlevel.crystal_floor_desc=地面上镶嵌着闪闪发光的水晶。
```

---

## 相关资源

- [Level API 参考](../../reference/levels/level-api.md)
- [Room API 参考](../../reference/levels/room-api.md)