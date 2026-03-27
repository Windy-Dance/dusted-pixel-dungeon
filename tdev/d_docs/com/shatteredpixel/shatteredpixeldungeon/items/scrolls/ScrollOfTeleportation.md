# ScrollOfTeleportation 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/ScrollOfTeleportation.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.scrolls |
| 类类型 | class |
| 继承关系 | extends Scroll |
| 代码行数 | 333 |

## 2. 类职责说明
ScrollOfTeleportation 是传送卷轴类，使用后随机传送英雄到同一楼层的另一个位置。传送优先选择未探索的区域，帮助快速探索地图。该类还提供多种静态方法用于传送其他角色或传送到指定位置。

## 7. 方法详解

### doRead()
**签名**: `@Override public void doRead()`
**功能**: 随机传送英雄
**实现逻辑**:
```java
// 第59-69行
detach(curUser.belongings.backpack);
Sample.INSTANCE.play(Assets.Sounds.READ);

if (teleportPreferringUnseen(curUser)) {
    readAnimation();
}
identify();
```

### teleportToLocation(Char ch, int pos)
**签名**: `public static boolean teleportToLocation(Char ch, int pos)`
**功能**: 传送角色到指定位置
**参数**:
- ch: Char - 要传送的角色
- pos: int - 目标位置
**返回值**: boolean - 是否成功

### teleportChar(Char ch)
**签名**: `public static boolean teleportChar(Char ch)`
**功能**: 随机传送角色
**参数**:
- ch: Char - 要传送的角色
**返回值**: boolean - 是否成功

### teleportPreferringUnseen(Hero hero)
**签名**: `public static boolean teleportPreferringUnseen(Hero hero)`
**功能**: 优先传送到未探索区域
**参数**:
- hero: Hero - 要传送的英雄
**返回值**: boolean - 是否成功
**实现逻辑**:
```java
// 第140-211行
// 收集未访问的特殊房间位置
for (Room r : level.rooms()) {
    if (r instanceof SpecialRoom) {
        // 检查是否被锁
        ...
    }
    
    int cell;
    for (Point p : r.charPlaceablePoints(level)) {
        cell = level.pointToCell(p);
        if (level.passable[cell] && !level.visited[cell] 
            && !level.secret[cell] && Actor.findChar(cell) == null) {
            candidates.add(cell);
        }
    }
}

if (candidates.isEmpty()) {
    return teleportChar(hero);
} else {
    int pos = Random.element(candidates);
    // 传送到未探索区域
    appear(hero, pos);
    ...
}
```

### appear(Char ch, int pos)
**签名**: `public static void appear(Char ch, int pos)`
**功能**: 传送动画和位置更新
**参数**:
- ch: Char - 传送的角色
- pos: int - 目标位置

## 11. 使用示例
```java
// 使用传送卷轴
ScrollOfTeleportation scroll = new ScrollOfTeleportation();
scroll.execute(hero, Scroll.AC_READ);
// 效果：传送到随机位置（优先未探索）

// 传送到指定位置
ScrollOfTeleportation.teleportToLocation(hero, targetPos);

// 随机传送敌人
ScrollOfTeleportation.teleportChar(enemy);
```

## 注意事项
1. 优先传送到未探索区域
2. 不可移动的角色无法传送
3. 解除根须状态
4. 已鉴定价值30金币

## 最佳实践
1. 快速探索新楼层
2. 逃离危险区域
3. 发现隐藏房间