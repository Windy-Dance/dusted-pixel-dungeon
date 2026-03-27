# LifeLinkSpell 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/LifeLinkSpell.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends ClericSpell |
| **代码行数** | 121 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
LifeLinkSpell（血色羁绊）建立牧师与强化盟友之间的生命联结，共享伤害。

### 系统定位
作为第4层级的盟友协同法术：
- 需要天赋 LIFE_LINK 解锁
- 需要有强化盟友（万物一心或星界投射）
- 使盟友获得伤害减免和法术复制效果

## 3. 方法详解

### icon()
**返回值**：HeroIcon.LIFE_LINK

### canCast()
**前置条件**：
- 需要 LIFE_LINK 天赋
- 需要有强化盟友（PowerOfMany.getPoweredAlly() 或 Stasis.getStasisAlly()）

### chargeUse()
**返回值**：固定返回 2

### onCast()
**核心逻辑**：
```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    int duration = Math.round(6.67f + 3.33f*Dungeon.hero.pointsInTalent(Talent.LIFE_LINK));
    
    Char ally = PowerOfMany.getPoweredAlly();
    
    if (ally != null) {
        // 显示光束效果
        hero.sprite.zap(ally.pos);
        hero.sprite.parent.add(new Beam.HealthRay(hero.sprite.center(), ally.sprite.center()));
        Sample.INSTANCE.play(Assets.Sounds.RAY);
        
        // 建立生命联结
        Buff.prolong(hero, LifeLink.class, duration).object = ally.id();
    } else {
        ally = Stasis.getStasisAlly();
        // 从星界投射状态恢复
        hero.sprite.operate(hero.pos);
        hero.sprite.parent.add(new Beam.HealthRay(DungeonTilemap.tileCenterToWorld(hero.pos), hero.sprite.center()));
        Sample.INSTANCE.play(Assets.Sounds.RAY);
    }
    
    Buff.prolong(ally, LifeLink.class, duration).object = hero.id();
    Buff.prolong(ally, LifeLinkSpellBuff.class, duration);
    
    hero.spendAndNext(Actor.TICK);
    onSpellCast(tome, hero);
}
```

### desc()
**动态计算**：
- 持续时间：7-13回合（随天赋等级）
- 伤害减免：30-40%（随天赋等级）

## 4. 内部类详解

### LifeLinkSpellBuff

**类型**：public static class extends FlavourBuff

**职责**：血色羁绊增益效果，标记盟友获得伤害减免和法术复制。

**主要方法**：
- `icon()`：返回 HOLY_ARMOR 图标
- `iconFadePercent()`：返回持续时间百分比

**效果**：
- 3阶及以下的增益法术复制给英雄
- 万物一心不会在此效果期间结束

## 5. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.lifelinkspell.name | 血色羁绊 |
| actors.hero.spells.lifelinkspell.short_desc | 与盟友共享所受伤害，并使其获得伤害减免。 |
| actors.hero.spells.lifelinkspell.desc | 牧师强化自身与其强化盟友之间的生命联结... |

### 中文翻译来源
actors_zh.properties 文件

## 6. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译