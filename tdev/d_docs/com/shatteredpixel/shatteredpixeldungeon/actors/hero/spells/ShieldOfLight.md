# ShieldOfLight 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/ShieldOfLight.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 146 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
ShieldOfLight（神圣护盾）为英雄提供仅对特定敌人有效的临时护甲加成。

### 系统定位
作为第1层级的防御法术：
- 需要天赋 SHIELD_OF_LIGHT 解锁
- 施法不消耗回合
- 只能对一个目标生效

## 3. 方法详解

### icon()
**返回值**：HeroIcon.SHIELD_OF_LIGHT

### targetingFlags()
**返回值**：Ballistica.STOP_TARGET

### canCast()
**前置条件**：需要 SHIELD_OF_LIGHT 天赋

### onTargetSelected()
**核心逻辑**：
```java
@Override
protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
    if (target == null) return;
    
    Char ch = Actor.findChar(target);
    // 必须是视野内的敌人
    if (ch == null || ch.alignment == Char.Alignment.ALLY || !Dungeon.level.heroFOV[target]) {
        GLog.w(Messages.get(this, "no_target"));
        return;
    }
    
    // 施加护盾追踪器（5回合，施法不耗时所以显示4回合）
    Buff.prolong(hero, ShieldOfLightTracker.class, 4f).object = ch.id();
    
    // 祭司对敌人施加光耀
    if (hero.subClass == HeroSubClass.PRIEST) {
        Buff.affect(ch, GuidingLight.Illuminated.class);
    }
    
    // 生命联结下也为盟友施加
    Char ally = PowerOfMany.getPoweredAlly();
    if (ally != null && ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null) {
        Buff.prolong(ally, ShieldOfLightTracker.class, 3f).object = ch.id();
    }
    
    onSpellCast(tome, hero);
}
```

### desc()
**动态计算**：护甲加成 = 1 + 天赋等级（最小值），最大值翻倍

## 4. 内部类详解

### ShieldOfLightTracker

**类型**：public static class extends FlavourBuff

**职责**：神圣护盾追踪器，记录护盾生效的目标。

**常量**：
- `DURATION = 5`：基础持续时间

**字段**：
- `object`：int，护盾目标的敌人ID

## 5. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.shieldoflight.name | 神圣护盾 |
| actors.hero.spells.shieldoflight.short_desc | 获得仅对单个目标生效的临时护甲。 |
| actors.hero.spells.shieldoflight.desc | 牧师在自身与敌人间创造了一面薄弱的圣盾... |

### 中文翻译来源
actors_zh.properties 文件

## 6. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译