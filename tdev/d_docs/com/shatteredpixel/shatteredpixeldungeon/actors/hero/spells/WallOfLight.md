# WallOfLight 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/WallOfLight.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 317 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
WallOfLight（神圣屏障）创建一面阻挡敌人的光墙。

### 系统定位
作为第3层级的圣骑士专属防御法术：
- 需要天赋 WALL_OF_LIGHT 解锁
- 圣骑士专属强力法术
- 再次施放免费驱散现有屏障

## 3. 方法详解

### icon()
**返回值**：HeroIcon.WALL_OF_LIGHT

### desc()
**动态计算**：宽度 = 1 + 2 × 天赋等级

### targetingFlags()
**返回值**：-1（禁用自动目标选择）

### chargeUse()
**动态计算**：
- 已有屏障存在：0（驱散）
- 否则：3

### canCast()
**前置条件**：需要 WALL_OF_LIGHT 天赋

### onCast()
**核心逻辑**：
```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    // 已有屏障存在，免费驱散
    if (Dungeon.level.blobs.get(LightWall.class) != null
            && Dungeon.level.blobs.get(LightWall.class).volume > 0) {
        Dungeon.level.blobs.get(LightWall.class).fullyClear();
        GLog.i(Messages.get(this, "early_end"));
        return;
    }
    super.onCast(tome, hero);
}
```

### onTargetSelected()
**核心逻辑**：
1. 计算屏障方向（基于目标与英雄的相对位置）
2. 根据天赋确定屏障宽度
3. 放置光墙Blob
4. 将困住的敌人击退并麻痹

### placeWall()
**核心逻辑**：
```java
private void placeWall(int pos, int knockbackDIR) {
    if (!Dungeon.level.solid[pos]) {
        GameScene.add(Blob.seed(pos, 20, LightWall.class));
        
        // 将敌人击退并麻痹
        Char ch = Actor.findChar(pos);
        if (ch != null && ch.alignment == Char.Alignment.ENEMY) {
            WandOfBlastWave.throwChar(ch, new Ballistica(pos, pos+knockbackDIR, Ballistica.PROJECTILE), 1, false, false, WallOfLight.INSTANCE);
            Buff.affect(ch, Paralysis.class, ch.cooldown());
        }
    }
}
```

## 4. 内部类详解

### LightWall

**类型**：public static class extends Blob

**职责**：光墙地形效果，阻挡移动。

**特点**：
- 持续20回合
- 将格子标记为实心（solid）
- 不阻挡视野

**主要方法**：
- `evolve()`：每回合衰减
- `onBuildFlagMaps()`：将光墙位置标记为实心
- `tileDesc()`：返回地形描述

## 5. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.walloflight.name | 神圣屏障 |
| actors.hero.spells.walloflight.short_desc | 创造一面阻挡敌人的屏障。 |
| actors.hero.spells.walloflight.early_end | 你驱散了神圣屏障。 |
| actors.hero.spells.walloflight.desc | 圣骑士将圣光凝聚为墙壁，在自身面前创造一面1格厚... |

### 中文翻译来源
actors_zh.properties 文件

## 6. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译