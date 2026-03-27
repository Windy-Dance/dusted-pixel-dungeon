# Flash 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/Flash.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 84 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Flash（天堂阶梯）将牧师传送至附近的已探索位置。

### 系统定位
作为第4层级的移动法术：
- 需要天赋 FLASH 解锁
- 仅在超凡升天状态下可用
- 充能消耗随使用次数递增

## 3. 方法详解

### icon()
**返回值**：HeroIcon.FLASH

### chargeUse()
**动态计算**：
- 基础：2点充能
- 每次使用后：+1点（当前超凡升天内的使用次数）

```java
@Override
public float chargeUse(Hero hero) {
    if (hero.buff(AscendedForm.AscendBuff.class) != null) {
        return 2 + hero.buff(AscendedForm.AscendBuff.class).flashCasts;
    } else {
        return 2;
    }
}
```

### canCast()
**前置条件**：
- 需要 FLASH 天赋
- 需要处于超凡升天状态

### targetingFlags()
**返回值**：-1（目标是空格，非敌人）

### onTargetSelected()
**核心逻辑**：
```java
@Override
protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
    if (target == null) return;
    
    // 验证目标位置
    if (Dungeon.level.solid[target] 
            || (!Dungeon.level.mapped[target] && !Dungeon.level.visited[target])
            || Dungeon.level.distance(hero.pos, target) > 2 + hero.pointsInTalent(Talent.FLASH)) {
        GLog.w(Messages.get(this, "invalid_target"));
        return;
    }
    
    // 执行传送
    if (ScrollOfTeleportation.teleportToLocation(hero, target)) {
        hero.spendAndNext(1f);
        onSpellCast(tome, hero);
        hero.buff(AscendedForm.AscendBuff.class).flashCasts++;
    }
}
```

## 4. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.flash.name | 天堂阶梯 |
| actors.hero.spells.flash.short_desc | 传送至附近一个位置。 |
| actors.hero.spells.flash.prompt | 选择一个位置 |
| actors.hero.spells.flash.desc | 牧师在附近的位置引导出超凡升天的神力并传送到那里... |

### 中文翻译来源
actors_zh.properties 文件

## 5. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译