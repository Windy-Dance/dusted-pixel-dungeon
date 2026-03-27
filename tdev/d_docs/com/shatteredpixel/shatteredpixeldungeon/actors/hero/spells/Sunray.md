# Sunray 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/Sunray.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 139 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Sunray（阳炎射线）发射光束对目标造成伤害并致盲。

### 系统定位
作为第2层级的远程攻击法术：
- 需要天赋 SUNRAY 解锁
- 对亡灵/恶魔必定最大伤害
- 首次致盲，再次命中时麻痹

## 3. 方法详解

### icon()
**返回值**：HeroIcon.SUNRAY

### desc()
**动态计算**（天赋等级）：
| 天赋 | 伤害 | 致盲回合 |
|-----|------|---------|
| 1级 | 4-8 | 4 |
| 2级 | 6-12 | 6 |

### canCast()
**前置条件**：需要 SUNRAY 天赋

### onTargetSelected()
**核心逻辑**：
```java
@Override
protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
    if (target == null) return;
    
    Ballistica aim = new Ballistica(hero.pos, target, targetingFlags());
    
    // 不能瞄准自己
    if (Actor.findChar(aim.collisionPos) == hero) {
        GLog.i(Messages.get(Wand.class, "self_target"));
        return;
    }
    
    hero.busy();
    Sample.INSTANCE.play(Assets.Sounds.RAY);
    hero.sprite.zap(target);
    
    // 显示光束效果
    hero.sprite.parent.add(new Beam.SunRay(hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(aim.collisionPos)));
    
    Char ch = Actor.findChar(aim.collisionPos);
    if (ch != null) {
        ch.sprite.burst(0xFFFFFF44, 5);
        
        // 亡灵/恶魔必定最大伤害
        if (Char.hasProp(ch, Char.Property.UNDEAD) || Char.hasProp(ch, Char.Property.DEMONIC)) {
            ch.damage(hero.pointsInTalent(Talent.SUNRAY) == 2 ? 12 : 8, Sunray.this);
        } else {
            ch.damage(Hero.heroDamageIntRange(...), Sunray.this);
        }
        
        if (ch.isAlive()) {
            // 已致盲且刚被致盲：改为麻痹
            if (ch.buff(Blindness.class) != null && ch.buff(SunRayRecentlyBlindedTracker.class) != null) {
                Buff.prolong(ch, Paralysis.class, 2f + 2f*hero.pointsInTalent(Talent.SUNRAY));
            } 
            // 未被致盲过：致盲
            else if (ch.buff(SunRayUsedTracker.class) == null) {
                Buff.prolong(ch, Blindness.class, 2f + 2f*hero.pointsInTalent(Talent.SUNRAY));
                Buff.prolong(ch, SunRayRecentlyBlindedTracker.class, 2f + 2f*hero.pointsInTalent(Talent.SUNRAY));
                Buff.affect(ch, SunRayUsedTracker.class);
            }
            // 祭司施加光耀
            if (hero.subClass == HeroSubClass.PRIEST) {
                Buff.affect(ch, GuidingLight.Illuminated.class);
            }
        }
    }
    
    hero.spend(1f);
    hero.next();
    onSpellCast(tome, hero);
}
```

## 4. 内部类详解

### SunRayUsedTracker

**类型**：public static class extends Buff

**职责**：标记已被阳炎射线致盲过的敌人（无法再次被致盲）。

### SunRayRecentlyBlindedTracker

**类型**：public static class extends FlavourBuff

**职责**：追踪刚被致盲的敌人（再次命中时改为麻痹）。

## 5. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.sunray.name | 阳炎射线 |
| actors.hero.spells.sunray.short_desc | 造成远程魔法伤害并致盲目标一次。 |
| actors.hero.spells.sunray.desc | 牧师向目标发射致盲光束... |

### 中文翻译来源
actors_zh.properties 文件

## 6. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译