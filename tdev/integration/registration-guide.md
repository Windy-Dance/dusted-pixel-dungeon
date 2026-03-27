# 注册点汇总指南

## 概述
本指南列出所有需要注册新内容的点，确保自定义内容能正确出现在游戏中。

## 物品注册

### 1. Generator.java
**文件**：`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/Generator.java`
**用途**：随机物品生成

#### 添加新武器
```java
// 在对应的Category中添加
WEP_T1.classes = new Class<?>[]{
    WornShortsword.class,
    MagesStaff.class,
    Dagger.class,
    Gloves.class,
    Rapier.class,
    Cudgel.class,
    YourNewWeapon.class // 添加你的新武器
};
WEP_T1.defaultProbs = new float[]{ 2, 0, 2, 2, 2, 2, 2 }; // 相应增加概率值
```

#### 添加新神器
```java
// 在ARTIFACT category中添加
ARTIFACT.classes = new Class<?>[]{
    AlchemistsToolkit.class,
    ChaliceOfBlood.class,
    CloakOfShadows.class,
    // ... 其他神器
    YourNewArtifact.class // 添加你的新神器
};
ARTIFACT.defaultProbs = new float[]{ 1, 1, 0, /* ... */, 1 }; // 相应增加概率值
```

### 2. ItemSpriteSheet.java
**文件**：`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/sprites/ItemSpriteSheet.java`
**用途**：物品图标

#### 添加新图标
```java
// 定义图标索引
private static final int YOUR_NEW_ITEM = xy(1, 33); // 选择合适的坐标位置

// 使用assignItemRect分配精灵图区域
static{
    assignItemRect(YOUR_NEW_ITEM, 16, 16); // 根据实际图标尺寸调整
}
```

## 怪物注册

### 1. 添加到生成池
**用途**：在特定楼层生成怪物

#### 普通怪物
```java
// 在对应区域的Mob类中添加
// 例如在 MobSpawner.java 的 standardMobRotation 方法中
case 1:
    return new ArrayList<>(Arrays.asList(
        Rat.class, Rat.class, Rat.class,
        Snake.class,
        YourNewMob.class // 添加新怪物
    ));
```

#### Boss怪物
```java
// 在BossLevel中添加
// 例如: SewerBossLevel.java
initRooms.add(new GooBossRoom()); // 添加你的Boss房间
```

### 2. MobSprite.java
**文件**：`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/sprites/`
**用途**：怪物精灵图

每个怪物都需要对应的Sprite类：

```java
// 创建 YourMonsterSprite.java
public class YourMonsterSprite extends MobSprite {
    
    public YourMonsterSprite() {
        super();
        
        texture( Assets.Sprites.YOUR_MONSTER );
        
        TextureFilm frames = new TextureFilm( texture, 16, 16 );
        
        idle = new Animation( 1, true );
        idle.frames( frames, 0, 0, 0, 1 );
        
        run = new Animation( 10, true );
        run.frames( frames, 2, 3, 4, 5 );
        
        attack = new Animation( 15, false );
        attack.frames( frames, 6, 7, 8 );
        
        die = new Animation( 10, false );
        die.frames( frames, 9, 10, 11, 12, 13 );
        
        play( idle );
    }
}
```

## 本地化注册

### 1. messages.properties
**文件**：`core/src/main/assets/messages/`
**用途**：文本翻译

```properties
items.artifacts.timeamulet.name=时间护符
items.artifacts.timeamulet.desc=一个神秘的护符...
```

对于中文本地化，需要编辑 `items_zh.properties` 文件：

```properties
# 在 core/src/main/assets/messages/items/items_zh.properties 中添加
items.artifacts.yournewartifact.name=你的新神器
items.artifacts.yournewartifact.desc=这是一个神奇的新神器，具有强大的能力...
```

## 精灵图资源

### 1. items.png
**文件**：`core/src/main/assets/sprites/items.png`
**用途**：物品图标精灵图

### 2. 对应角色精灵图
**用途**：怪物/英雄精灵图

怪物精灵图通常存储在单独的PNG文件中：
- `core/src/main/assets/sprites/your_monster.png`

## 注册检查清单
- [ ] 添加物品到Generator
- [ ] 分配物品图标
- [ ] 添加本地化文本
- [ ] 创建精灵图（如需要）
- [ ] 添加到正确的生成区域