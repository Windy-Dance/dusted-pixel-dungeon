# Radiance 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/Radiance.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends ClericSpell |
| **代码行数** | 90 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Radiance（破晓辐光）对视野内所有敌人施加光耀效果并击晕。

### 系统定位
作为第3层级的祭司专属AOE法术：
- 仅祭司子职业可用
- 触发光耀的额外伤害
- 视情况提供发光效果

## 3. 方法详解

### icon()
**返回值**：HeroIcon.RADIANCE

### chargeUse()
**返回值**：固定返回 2

### canCast()
**前置条件**：子职业必须是 PRIEST

### onCast()
**核心逻辑**：
```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    GameScene.flash(0x80FFFFFF);
    Sample.INSTANCE.play(Assets.Sounds.BLAST);
    
    // 黑暗环境下提供发光效果
    if (Dungeon.level.viewDistance < 6) {
        Buff.prolong(hero, Light.class, Dungeon.isChallenged(Challenges.DARKNESS) ? 20 : 100);
    }
    
    // 对视野内所有敌人
    for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
        if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
            // 已光耀则触发光耀伤害
            if (mob.buff(GuidingLight.Illuminated.class) != null) {
                mob.damage(hero.lvl + 5, GuidingLight.class);
            } else {
                // 施加光耀
                Buff.affect(mob, GuidingLight.Illuminated.class);
                Buff.affect(mob, GuidingLight.WasIlluminatedTracker.class);
            }
            // 击晕3回合
            if (mob.isActive()) {
                Buff.affect(mob, Paralysis.class, 3f);
            }
        }
    }
    
    hero.spend(1f);
    hero.busy();
    hero.sprite.operate(hero.pos);
    
    onSpellCast(tome, hero);
}
```

## 4. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.radiance.name | 破晓辐光 |
| actors.hero.spells.radiance.short_desc | 为视野内敌人触发并施加光耀而将其暂时击晕。 |
| actors.hero.spells.radiance.desc | 祭司通体迸出圣光，击晕所有视野内敌人3回合... |

### 中文翻译来源
actors_zh.properties 文件

## 5. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译