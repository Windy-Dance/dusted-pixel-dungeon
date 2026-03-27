# HolyLance 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/HolyLance.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 191 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
HolyLance（神圣标枪）对目标造成高额远程魔法伤害，对亡灵和恶魔必定造成最大伤害。

### 系统定位
作为第3层级的高伤害法术：
- 需要天赋 HOLY_LANCE 解锁
- 充能消耗较高（4点）
- 有30回合冷却时间
- 祭司专属强力法术

## 3. 方法详解

### icon()
**返回值**：HeroIcon.HOLY_LANCE

### desc()
**动态计算**：
- 最小伤害：15 + 15 × 天赋等级
- 最大伤害：28 + 28 × 天赋等级

### canCast()
**前置条件**：
- 需要 HOLY_LANCE 天赋
- 没有冷却中（LanceCooldown 不存在）

### chargeUse()
**返回值**：固定返回 4

### targetingFlags()
**返回值**：Ballistica.PROJECTILE（投射物弹道）

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
    
    hero.sprite.zap(target);
    hero.busy();
    Sample.INSTANCE.play(Assets.Sounds.ZAP);
    
    Char enemy = Actor.findChar(aim.collisionPos);
    if (enemy != null) {
        // 发射神圣标枪飞弹
        ((MissileSprite) hero.sprite.parent.recycle(MissileSprite.class))
            .reset(hero.sprite, enemy.sprite, new HolyLanceVFX(), new Callback() {
                @Override
                public void call() {
                    int min = 15 + 15*Dungeon.hero.pointsInTalent(Talent.HOLY_LANCE);
                    int max = Math.round(27.5f + 27.5f*Dungeon.hero.pointsInTalent(Talent.HOLY_LANCE));
                    // 亡灵和恶魔必定最大伤害
                    if (Char.hasProp(enemy, Char.Property.UNDEAD) || Char.hasProp(enemy, Char.Property.DEMONIC)) {
                        min = max;
                    }
                    enemy.damage(Hero.heroDamageIntRange(min, max), HolyLance.this);
                    // 施加光耀效果
                    if (enemy.isActive()) {
                        Buff.affect(enemy, GuidingLight.Illuminated.class);
                    }
                    hero.spendAndNext(1f);
                    onSpellCast(tome, hero);
                    // 30回合冷却
                    FlavourBuff.affect(hero, LanceCooldown.class, 30f);
                }
            });
    }
}
```

## 4. 内部类详解

### HolyLanceVFX

**类型**：public static class extends Item

**职责**：神圣标枪的视觉效果，显示发光的投射物。

### LanceCooldown

**类型**：public static class extends FlavourBuff

**职责**：神圣标枪的冷却计时器。

**主要方法**：
- `icon()`：返回 TIME 图标
- `tintIcon(Image)`：将图标染成黄色

## 5. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.holylance.name | 神圣标枪 |
| actors.hero.spells.holylance.short_desc | 造成高额远程魔法伤害。 |
| actors.hero.spells.holylance.desc | 祭司将大量能量聚集为一柄致命的投掷用圣枪... |

### 中文翻译来源
actors_zh.properties 文件

## 6. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译