# Stasis 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/Stasis.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends ClericSpell |
| **代码行数** | 197 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Stasis（星界投射）将强化盟友存入英雄体内，保护其状态并在需要时释放。

### 系统定位
作为第4层级的盟友保护法术：
- 需要天赋 STASIS 解锁
- 需要有强化盟友才能施放
- 再次施放可提前释放盟友

## 3. 方法详解

### icon()
**返回值**：HeroIcon.STASIS

### desc()
**动态计算**：持续时间 = 30 + 30 × 天赋等级

### canCast()
**前置条件**：
- 需要 STASIS 天赋
- 需要有强化盟友或已有盟友在星界投射状态

### chargeUse()
**动态计算**：
- 已有盟友在星界投射时：0（提前释放）
- 否则：2

### onCast()
**核心逻辑**：
```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    onSpellCast(tome, hero);
    
    // 已有盟友在星界投射，提前释放
    if (hero.buff(StasisBuff.class) != null) {
        hero.sprite.operate(hero.pos);
        hero.buff(StasisBuff.class).act();
        return;
    }
    
    // 将盟友存入星界投射
    Char ally = PowerOfMany.getPoweredAlly();
    
    // 显示光束效果
    hero.sprite.zap(ally.pos);
    MagicMissile.boltFromChar(hero.sprite.parent, MagicMissile.LIGHT_MISSILE, ally.sprite, hero.pos, null);
    
    // 移除盟友并保留正面Buff
    LinkedHashSet<Buff> buffs = ally.buffs();
    Actor.remove(ally);
    ally.sprite.killAndErase();
    ally.sprite = null;
    Dungeon.level.mobs.remove(ally);
    for (Buff b : buffs) {
        if (b.type == Buff.buffType.POSITIVE || b.revivePersists) {
            ally.add(b);
        }
    }
    ally.clearTime();
    
    // 施加星界投射Buff
    Buff.prolong(hero, StasisBuff.class, 30 + 30*hero.pointsInTalent(Talent.STASIS)).stasisAlly = (Mob)ally;
    
    hero.spendAndNext(Actor.TICK);
}
```

### getStasisAlly()
**静态方法**：获取当前星界投射中的盟友。

## 4. 内部类详解

### StasisBuff

**类型**：public static class extends FlavourBuff

**职责**：星界投射效果Buff，存储盟友并在效果结束时释放。

**字段**：
- `stasisAlly`：Mob，存储的盟友

**主要方法**：
- `icon()`：返回 MANY_POWER 图标
- `act()`：效果结束时释放盟友到英雄附近

## 5. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.stasis.name | 星界投射 |
| actors.hero.spells.stasis.short_desc | 将盟友移出地牢并凝滞其状态。 |
| actors.hero.spells.stasis.desc | 牧师将其与盟友的联系集中于自身... |

### 中文翻译来源
actors_zh.properties 文件

## 6. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译