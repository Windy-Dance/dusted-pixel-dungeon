# HolyIntuition 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/HolyIntuition.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends InventoryClericSpell |
| **代码行数** | 86 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
HolyIntuition（神圣预知）鉴定物品是否有诅咒。

### 系统定位
作为第1层级的辅助法术：
- 需要天赋 HOLY_INTUITION 解锁
- 不完全鉴定物品，仅判断诅咒状态
- 充能消耗随天赋等级降低

## 3. 方法详解

### icon()
**返回值**：HeroIcon.HOLY_INTUITION

### usableOnItem()
**过滤条件**：
- 必须是可装备物品或法杖
- 必须未完全鉴定
- 必须诅咒状态未知

```java
@Override
protected boolean usableOnItem(Item item) {
    return (item instanceof EquipableItem || item instanceof Wand) 
            && !item.isIdentified() && !item.cursedKnown;
}
```

### chargeUse()
**动态计算**：4 - 天赋等级（天赋1/2/3时消耗3/2/1点）

### canCast()
**前置条件**：需要 HOLY_INTUITION 天赋

### onItemSelected()
**核心逻辑**：
```java
@Override
protected void onItemSelected(HolyTome tome, Hero hero, Item item) {
    if (item == null) return;
    
    item.cursedKnown = true;
    
    if (item.cursed) {
        GLog.w(Messages.get(this, "cursed"));
    } else {
        GLog.i(Messages.get(this, "uncursed"));
    }
    
    hero.spend(1f);
    hero.busy();
    hero.sprite.operate(hero.pos);
    hero.sprite.parent.add(new Identification(hero.sprite.center().offset(0, -16)));
    
    Sample.INSTANCE.play(Assets.Sounds.READ);
    onSpellCast(tome, hero);
}
```

## 4. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.holyintuition.name | 神圣预知 |
| actors.hero.spells.holyintuition.short_desc | 鉴定一个物品有无诅咒。 |
| actors.hero.spells.holyintuition.prompt | 选择一个物品 |
| actors.hero.spells.holyintuition.cursed | 你感觉到这件物品里潜伏着一股充满恶意的魔力。 |
| actors.hero.spells.holyintuition.uncursed | 这个物品没有被诅咒。 |
| actors.hero.spells.holyintuition.desc | 牧师将其感知集中在一个物品上，不必装备物品就能判断其诅咒有无。 |

### 中文翻译来源
actors_zh.properties 文件

## 5. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译