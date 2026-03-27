# Judgement 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/Judgement.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends ClericSpell |
| **代码行数** | 104 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Judgement（终末天启）对视野内所有敌人造成伤害，伤害随施法次数增加。

### 系统定位
作为第4层级的AOE法术：
- 需要天赋 JUDGEMENT 解锁
- 仅在超凡升天状态下可用
- 伤害随超凡升天期间的施法次数增长

## 3. 方法详解

### icon()
**返回值**：HeroIcon.JUDGEMENT

### chargeUse()
**返回值**：固定返回 3

### canCast()
**前置条件**：
- 需要 JUDGEMENT 天赋
- 需要处于超凡升天状态

### onCast()
**核心逻辑**：
```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    hero.sprite.attack(hero.pos, new Callback() {
        @Override
        public void call() {
            GameScene.flash(0x80FFFFFF);
            Sample.INSTANCE.play(Assets.Sounds.BLAST);
            
            // 基础伤害 + 施法次数加成
            int damageBase = 5 + 5*hero.pointsInTalent(Talent.JUDGEMENT);
            damageBase += Math.round(damageBase * hero.buff(AscendedForm.AscendBuff.class).spellCasts/3f);
            
            // 对视野内所有敌人造成伤害
            for (Char ch : Actor.chars()) {
                if (ch.alignment != hero.alignment && Dungeon.level.heroFOV[ch.pos]) {
                    ch.damage(Hero.heroDamageIntRange(damageBase, 2*damageBase), Judgement.this);
                    // 祭司施加光耀效果
                    if (hero.subClass == HeroSubClass.PRIEST) {
                        Buff.affect(ch, GuidingLight.Illuminated.class);
                    }
                }
            }
            
            hero.spendAndNext(1f);
            onSpellCast(tome, hero);
            // 重置施法计数
            hero.buff(AscendedForm.AscendBuff.class).spellCasts = 0;
        }
    });
    hero.busy();
}
```

### desc()
**动态计算**：显示基础伤害和当前实际伤害

## 4. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.judgement.name | 终末天启 |
| actors.hero.spells.judgement.short_desc | 对视野内所有敌人造成伤害。 |
| actors.hero.spells.judgement.desc | 牧师撕裂苍穹，以圣光之名审判视野内所有敌人... |

### 中文翻译来源
actors_zh.properties 文件

## 5. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译