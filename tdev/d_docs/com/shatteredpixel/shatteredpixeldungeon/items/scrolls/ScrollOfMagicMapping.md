# ScrollOfMagicMapping 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/ScrollOfMagicMapping.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.scrolls |
| 类类型 | class |
| 继承关系 | extends Scroll |
| 代码行数 | 96 |

## 2. 类职责说明
ScrollOfMagicMapping 是魔法地图卷轴类，使用后揭示整个地牢楼层的地图布局，包括所有房间、走廊和隐藏门。被发现的秘密区域会显示特效，让玩家能够发现隐藏的房间和通道。这是探索和安全导航的重要工具。

## 7. 方法详解

### doRead()
**签名**: `@Override public void doRead()`
**功能**: 揭示整个地图并发现隐藏区域
**实现逻辑**:
```java
// 第43-86行
detach(curUser.belongings.backpack);

// 遍历整个地图
int length = Dungeon.level.length();
int[] map = Dungeon.level.map;
boolean[] mapped = Dungeon.level.mapped;
boolean[] discoverable = Dungeon.level.discoverable;

boolean noticed = false;

for (int i = 0; i < length; i++) {
    int terr = map[i];
    
    if (discoverable[i]) {
        mapped[i] = true;
        // 发现秘密区域
        if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {
            Dungeon.level.discover(i);
            if (Dungeon.level.heroFOV[i]) {
                GameScene.discoverTile(i, terr);
                discover(i);
                noticed = true;
            }
        }
    }
}

GameScene.updateFog();
GLog.i(Messages.get(this, "layout"));
if (noticed) {
    Sample.INSTANCE.play(Assets.Sounds.SECRET);
}

SpellSprite.show(curUser, SpellSprite.MAP);
Sample.INSTANCE.play(Assets.Sounds.READ);
identify();
readAnimation();
```

### discover(int cell)
**签名**: `public static void discover(int cell)`
**功能**: 显示发现特效
**实现逻辑**:
```java
// 第93-95行
CellEmitter.get(cell).start(Speck.factory(Speck.DISCOVER), 0.1f, 4);
```

## 11. 使用示例
```java
ScrollOfMagicMapping scroll = new ScrollOfMagicMapping();
scroll.execute(hero, Scroll.AC_READ);
// 效果：揭示整个地图，发现隐藏门和秘密房间
```

## 注意事项
1. 揭示整个地图布局
2. 发现秘密区域并显示特效
3. 已鉴定价值40金币

## 最佳实践
1. 在复杂楼层使用避免迷路
2. 发现隐藏房间获取额外战利品
3. 规划安全路线