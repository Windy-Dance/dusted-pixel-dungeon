# Smite 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/Smite.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 132 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Smite（至圣斩击）对敌人进行一次必中、附魔强化的近战攻击。

### 系统定位
作为第3层级的圣骑士专属攻击法术：
- 仅圣骑士子职业可用
- 必定命中（不超力时）
- 附魔效果+300%
- 对亡灵/恶魔必定最大伤害

## 3. 方法详解

### icon()
**返回值**：HeroIcon.SMITE

### targetingFlags()
**返回值**：Ballistica.STOP_TARGET（无自动瞄准）

### chargeUse()
**返回值**：固定返回 2

### canCast()
**前置条件**：子职业必须是 PALADIN

### onTargetSelected()
**核心逻辑**：
```java
@Override
protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
    if (target == null) return;
    
    Char enemy = Actor.findChar(target);
    if (enemy == null || enemy == hero) {
        GLog.w(Messages.get(this, "no_target"));
        return;
    }
    
    // 施加至圣斩击追踪器
    SmiteTracker tracker = Buff.affect(hero, SmiteTracker.class);
    
    // 验证目标
    if (hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target] || !hero.canAttack(enemy)) {
        GLog.w(Messages.get(this, "invalid_enemy"));
        tracker.detach();
        return;
    }
    
    hero.sprite.attack(enemy.pos, new Callback() {
        @Override
        public void call() {
            // 不超力时必定命中
            float accMult = 1;
            if (!(hero.belongings.attackingWeapon() instanceof Weapon)
                    || ((Weapon) hero.belongings.attackingWeapon()).STRReq() <= hero.STR()) {
                accMult = Char.INFINITE_ACCURACY;
            }
            
            if (hero.attack(enemy, 1, 0, accMult)) {
                Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                enemy.sprite.burst(0xFFFFFFFF, 10);
            }
            tracker.detach();
            
            Invisibility.dispel();
            hero.spendAndNext(hero.attackDelay());
            onSpellCast(tome, hero);
        }
    });
}
```

### desc()
**动态计算**：
- 最小伤害：5 + 等级/2
- 最大伤害：10 + 等级

### bonusDmg()
**静态方法**：计算额外魔法伤害，对亡灵/恶魔返回最大值。

## 4. 内部类详解

### SmiteTracker

**类型**：public static class extends FlavourBuff

**职责**：至圣斩击追踪器，标记正在执行至圣斩击。

## 5. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.smite.name | 至圣斩击 |
| actors.hero.spells.smite.short_desc | 一次带有额外伤害与附魔强化的必中攻击。 |
| actors.hero.spells.smite.desc | 圣骑士为一次致命的近战攻击注入正义之力... |

### 中文翻译来源
actors_zh.properties 文件

## 6. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译