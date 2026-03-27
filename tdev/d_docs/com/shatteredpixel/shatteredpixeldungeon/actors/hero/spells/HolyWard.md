# HolyWard 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/HolyWard.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends ClericSpell |
| **代码行数** | 112 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
HolyWard（神圣护甲）为牧师的护甲附加神圣刻印效果，增加护甲的防御能力。

### 系统定位
作为第1层级的基础法术，神圣护甲是牧师的核心防御增益法术：
- 不需要目标选择，直接施放于自身
- 与圣骑士子职业有特殊联动（更高防御、不覆盖刻印、延展效果）

### 与HolyWeapon的关系
- HolyWard作用于护甲（增加防御）
- HolyWeapon作用于武器（增加伤害）
- 两者设计模式高度相似

## 3. 结构总览

### 主要成员概览
- `INSTANCE`：单例实例
- `HolyArmBuff`：神圣护甲效果Buff

### 主要逻辑块概览
- **效果施加**：给予英雄50回合的神圣护甲Buff
- **圣骑士联动**：施法时延长已有的神圣护甲效果

## 4. 方法详解

### icon()

**返回值**：int，HeroIcon.HOLY_WARD

---

### onCast()

**核心实现逻辑**：
```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    Buff.affect(hero, HolyArmBuff.class, 50f);
    Item.updateQuickslot();
    
    Sample.INSTANCE.play(Assets.Sounds.READ);
    
    hero.sprite.operate(hero.pos);
    if (hero.belongings.armor() != null) 
        Enchanting.show(hero, hero.belongings.armor());
    
    onSpellCast(tome, hero);
}
```

---

### desc()

**方法职责**：返回法术描述，圣骑士有额外说明。

## 5. 内部类详解

### HolyArmBuff

**类型**：public static class extends FlavourBuff

**职责**：神圣护甲效果Buff，增加护甲防御值。

**主要方法**：
- `icon()`：返回神圣护甲图标
- `extend(float)`：延长效果时间

**效果差异**：
- 普通牧师：护甲防御+1，覆盖原有刻印
- 圣骑士：护甲防御+3，不覆盖原有刻印

## 6. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| actors.hero.spells.holyward.name | 神圣护甲 | 法术名称 |
| actors.hero.spells.holyward.short_desc | 临时覆盖刻印以强化护甲防御。 | 简短描述 |
| actors.hero.spells.holyward.desc | 牧师赋予其身穿护甲神圣刻印... | 详细描述 |
| actors.hero.spells.holyward.desc_paladin | 圣骑士施放该法术时效果更强... | 圣骑士额外描述 |
| actors.hero.spells.holyward.glyph_name | 神圣%s | 刻印名称格式 |
| actors.hero.spells.holyward.glyph_desc | 这个刻印略微增加了护甲可防御的伤害量。 | 刻印描述 |

### 中文翻译来源
actors_zh.properties 文件

## 7. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译