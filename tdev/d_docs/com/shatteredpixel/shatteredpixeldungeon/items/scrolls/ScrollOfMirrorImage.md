# ScrollOfMirrorImage 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/ScrollOfMirrorImage.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.scrolls |
| 类类型 | class |
| 继承关系 | extends Scroll |
| 代码行数 | 99 |

## 2. 类职责说明
ScrollOfMirrorImage 是镜像卷轴类，使用后在英雄周围生成镜像分身。镜像分身拥有与英雄相同的外观和武器，可以攻击敌人但会很快消散。这是一个强大的战斗辅助工具，可以分散敌人注意力并增加输出。

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| NIMAGES | int | 2 | 默认生成的镜像数量 |

## 7. 方法详解

### doRead()
**签名**: `@Override public void doRead()`
**功能**: 生成镜像分身
**实现逻辑**:
```java
// 第48-60行
detach(curUser.belongings.backpack);
if (spawnImages(curUser, NIMAGES) > 0) {
    GLog.i(Messages.get(this, "copies"));
} else {
    GLog.i(Messages.get(this, "no_copies"));
}
identify();
Sample.INSTANCE.play(Assets.Sounds.READ);
readAnimation();
```

### spawnImages(Hero hero, int pos, int nImages)
**签名**: `public static int spawnImages(Hero hero, int pos, int nImages)`
**功能**: 在指定位置生成镜像
**参数**:
- hero: Hero - 复制外观的英雄
- pos: int - 生成位置
- nImages: int - 尝试生成的数量
**返回值**: int - 实际生成的数量
**实现逻辑**:
```java
// 第67-93行
ArrayList<Integer> respawnPoints = new ArrayList<>();

// 收集可用位置
for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
    int p = pos + PathFinder.NEIGHBOURS9[i];
    if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
        respawnPoints.add(p);
    }
}

int spawned = 0;
while (nImages > 0 && respawnPoints.size() > 0) {
    int index = Random.index(respawnPoints);
    
    MirrorImage mob = new MirrorImage();
    mob.duplicate(hero);  // 复制英雄外观
    GameScene.add(mob);
    ScrollOfTeleportation.appear(mob, respawnPoints.get(index));
    
    respawnPoints.remove(index);
    nImages--;
    spawned++;
}

return spawned;
```

## 11. 使用示例
```java
// 使用镜像卷轴
ScrollOfMirrorImage scroll = new ScrollOfMirrorImage();
scroll.execute(hero, Scroll.AC_READ);
// 效果：生成2个镜像分身

// 程序调用生成镜像
int spawned = ScrollOfMirrorImage.spawnImages(hero, hero.pos, 3);
// 在英雄位置尝试生成3个镜像
```

## 注意事项
1. 需要周围有可用空间
2. 镜像会攻击敌人
3. 镜像持续时间有限
4. 已鉴定价值30金币

## 最佳实践
1. 在开阔区域使用效果更好
2. 配合强武器增加输出
3. 分散敌人注意力