# DivineSense 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/DivineSense.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends ClericSpell |
| **代码行数** | 109 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
DivineSense（神圣感知）为牧师提供大范围的灵视效果，可看到范围内所有生物。

### 系统定位
作为第2层级的侦查法术：
- 需要天赋 DIVINE_SENSE 解锁
- 施法不消耗回合
- 提供持续50回合的灵视

## 3. 方法详解

### icon()
**返回值**：HeroIcon.DIVINE_SENSE

### chargeUse()
**返回值**：固定返回 2

### canCast()
**前置条件**：需要 DIVINE_SENSE 天赋

### onCast()
**核心逻辑**：
```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    Buff.prolong(hero, DivineSenseTracker.class, DivineSenseTracker.DURATION);
    Dungeon.observe();
    
    Sample.INSTANCE.play(Assets.Sounds.READ);
    
    SpellSprite.show(hero, SpellSprite.VISION);
    hero.sprite.operate(hero.pos);
    hero.next();  // 不消耗回合
    
    // 生命联结下也为盟友施加
    Char ally = PowerOfMany.getPoweredAlly();
    if (ally != null && ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null) {
        Buff.prolong(ally, DivineSenseTracker.class, DivineSenseTracker.DURATION);
        SpellSprite.show(ally, SpellSprite.VISION);
    }
    
    onSpellCast(tome, hero);
}
```

### desc()
**动态计算**：灵视范围 = 4 + 4 × 天赋等级

## 4. 内部类详解

### DivineSenseTracker

**类型**：public static class extends FlavourBuff

**职责**：神圣感知效果Buff，提供灵视能力。

**常量**：
- `DURATION = 50f`：持续时间

**主要方法**：
- `icon()`：返回 HOLY_SIGHT 图标
- `detach()`：移除时重新计算视野

## 5. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.divinesense.name | 神圣感知 |
| actors.hero.spells.divinesense.short_desc | 短时间内获得大范围的灵视感知。 |
| actors.hero.spells.divinesense.desc | 牧师将其感知集中在周遭环境上... |

### 中文翻译来源
actors_zh.properties 文件

## 6. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译