# DivineIntervention 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/DivineIntervention.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends ClericSpell |
| **代码行数** | 125 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
DivineIntervention（神圣干预）为牧师及所有盟友提供大量护盾并延长超凡升天持续时间。

### 系统定位
作为第4层级的高级防御法术：
- 需要天赋 DIVINE_INTERVENTION 解锁
- 仅在超凡升天状态下可用
- 每次超凡升天只能使用一次
- 充能消耗极高（5点）

## 3. 方法详解

### icon()
**返回值**：HeroIcon.DIVINE_INTERVENTION

### chargeUse()
**返回值**：固定返回 5

### canCast()
**前置条件**：
- 需要 DIVINE_INTERVENTION 天赋
- 需要处于超凡升天状态（AscendBuff 存在）
- 本次超凡升天尚未施放过神圣干预

### onCast()
**核心逻辑**：
```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    Sample.INSTANCE.play(Assets.Sounds.CHARGEUP, 1, 1.2f);
    hero.sprite.operate(hero.pos);
    
    // 为所有盟友施加护盾
    for (Char ch : Actor.chars()) {
        if (ch.alignment == Char.Alignment.ALLY && ch != hero) {
            Buff.affect(ch, DivineShield.class).setShield(100 + 50*hero.pointsInTalent(Talent.DIVINE_INTERVENTION));
            new Flare(6, 32).color(0xFFFF00, true).show(ch.sprite, 2f);
        }
    }
    
    hero.spendAndNext(1f);
    onSpellCast(tome, hero);
    
    // 为自己施加护盾（在onSpellCast之后避免叠加）
    hero.buff(AscendedForm.AscendBuff.class).setShield(100 + 50*hero.pointsInTalent(Talent.DIVINE_INTERVENTION));
    new Flare(6, 32).color(0xFFFF00, true).show(hero.sprite, 2f);
    
    // 标记已施放，延长超凡升天
    hero.buff(AscendedForm.AscendBuff.class).divineInverventionCast = true;
    hero.buff(AscendedForm.AscendBuff.class).extend(2 + hero.pointsInTalent(Talent.DIVINE_INTERVENTION));
}
```

### desc()
**动态计算**：根据天赋等级计算护盾量和延长时间

## 4. 内部类详解

### DivineShield

**类型**：public static class extends ShieldBuff

**职责**：神圣护盾效果，与超凡升天绑定。

**特点**：
- `shieldUsePriority = 1`：护盾使用优先级
- 超凡升天结束时自动消失
- 显示护盾视觉效果

**主要方法**：
- `act()`：检查超凡升天状态，结束时移除
- `shielding()`：超凡升天结束时返回0
- `fx(boolean)`：添加/移除护盾视觉效果

## 5. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.divineintervention.name | 神圣干预 |
| actors.hero.spells.divineintervention.short_desc | 大幅强化牧师与盟友的护盾。 |
| actors.hero.spells.divineintervention.desc | 牧师借助圣典向自身与盟友体内注入无懈可击的神力... |

### 中文翻译来源
actors_zh.properties 文件

## 6. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译