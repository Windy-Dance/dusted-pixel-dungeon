# BlessSpell 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/BlessSpell.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 136 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
BlessSpell（神圣祝福）为目标施加赐福效果和护盾/治疗。

### 系统定位
作为第2层级的辅助法术：
- 需要天赋 BLESS 解锁
- 对自己施放：赐福+护盾
- 对他人施放：赐福+治疗（溢出转护盾）

## 3. 方法详解

### icon()
**返回值**：HeroIcon.BLESS

### targetingFlags()
**返回值**：-1（禁用自动目标选择）

### canCast()
**前置条件**：需要 BLESS 天赋

### onTargetSelected()
**核心逻辑**：
1. 检查目标是否在视野内
2. 调用 affectChar() 施加效果
3. 生命联结下复制效果

### affectChar()
**核心逻辑**：
```java
private void affectChar(Hero hero, Char ch) {
    new Flare(6, 32).color(0xFFFF00, true).show(ch.sprite, 2f);
    if (ch == hero) {
        // 自己：赐福 + 护盾
        Buff.prolong(ch, Bless.class, 2f + 4*hero.pointsInTalent(Talent.BLESS));
        Buff.affect(ch, Barrier.class).setShield(5 + 5*hero.pointsInTalent(Talent.BLESS));
    } else {
        // 他人：赐福 + 治疗（溢出转护盾）
        Buff.prolong(ch, Bless.class, 5f + 5*hero.pointsInTalent(Talent.BLESS));
        // 治疗...
    }
    // 祭司对敌人施加光耀
    if (ch.alignment != Char.Alignment.ALLY && hero.subClass == HeroSubClass.PRIEST) {
        Buff.affect(ch, GuidingLight.Illuminated.class);
    }
}
```

### desc()
**动态计算**：根据天赋等级计算各项数值

## 4. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.blessspell.name | 神圣祝福 |
| actors.hero.spells.blessspell.short_desc | 使牧师获得祝福和护盾，使其他单位获得祝福和治疗。 |

### 中文翻译来源
actors_zh.properties 文件

## 5. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译