# HolyWeapon 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/HolyWeapon.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends ClericSpell |
| **代码行数** | 117 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
HolyWeapon（神圣武器）为牧师的武器附加神圣附魔效果，使每次攻击造成额外的魔法伤害。

### 系统定位
作为第1层级的基础法术，神圣武器是牧师的核心增益法术：
- 不需要目标选择，直接施放于自身
- 与圣骑士子职业有特殊联动（更高伤害、延展效果）
- 可与其他法术协同（圣骑士施法时延长效果）

### 不负责什么
- 不负责实际的伤害计算（由武器攻击系统处理）
- 不负责武器原有附魔的管理（仅覆盖视觉效果）

## 3. 结构总览

### 主要成员概览
- `INSTANCE`：单例实例
- `HolyWepBuff`：神圣武器效果Buff

### 主要逻辑块概览
- **效果施加**：给予英雄50回合的神圣武器Buff
- **圣骑士联动**：施法时延长已有的神圣武器效果

## 4. 继承与协作关系

### 父类提供的能力
继承自 ClericSpell：
- `chargeUse()`：默认充能消耗
- `canCast()`：默认施法条件
- `onSpellCast()`：施法后处理

### 覆写的方法
| 方法 | 说明 |
|------|------|
| icon() | 返回神圣武器图标 |
| onCast() | 实现法术效果 |
| chargeUse() | 返回2点充能消耗 |
| desc() | 圣骑士额外描述 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| HolyWepBuff | 神圣武器效果Buff |
| Enchanting | 附魔视觉效果 |

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| INSTANCE | HolyWeapon | 单例 | 法术单例实例 |

### 实例字段
无

## 6. 构造与初始化机制

使用默认无参构造器，通过静态 INSTANCE 单例访问。

## 7. 方法详解

### icon()

**可见性**：public

**是否覆写**：是

**返回值**：int，HeroIcon.HOLY_WEAPON

---

### chargeUse()

**可见性**：public

**是否覆写**：是

**返回值**：float，固定返回2

---

### onCast()

**可见性**：public

**是否覆写**：是

**方法职责**：为英雄施加50回合的神圣武器Buff。

**核心实现逻辑**：
```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    Buff.affect(hero, HolyWepBuff.class, 50f);
    Item.updateQuickslot();
    
    Sample.INSTANCE.play(Assets.Sounds.READ);
    
    hero.sprite.operate(hero.pos);
    if (hero.belongings.weapon() != null) 
        Enchanting.show(hero, hero.belongings.weapon());
    
    onSpellCast(tome, hero);
}
```

---

### desc()

**可见性**：public

**是否覆写**：是

**方法职责**：返回法术描述，圣骑士有额外说明（更高伤害、不覆盖附魔、延展效果）。

## 8. 内部类详解

### HolyWepBuff

**类型**：public static class extends FlavourBuff

**职责**：神圣武器效果Buff，使武器攻击附加魔法伤害。

**常量**：
- `DURATION = 50f`：基础持续时间

**主要方法**：
- `icon()`：返回神圣武器图标
- `iconFadePercent()`：返回持续时间百分比
- `desc()`：根据子职业返回不同描述
- `detach()`：移除时更新快捷栏
- `extend(float)`：延长效果时间，最多延长至2倍基础时长

**字段**：无实例字段

## 9. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| actors.hero.spells.holyweapon.name | 神圣武器 | 法术名称 |
| actors.hero.spells.holyweapon.short_desc | 临时覆盖附魔以强化武器伤害。 | 简短描述 |
| actors.hero.spells.holyweapon.desc | 牧师赋予其手持武器神圣附魔... | 详细描述 |
| actors.hero.spells.holyweapon.desc_paladin | 圣骑士施放该法术时效果更强... | 圣骑士额外描述 |
| actors.hero.spells.holyweapon.ench_name | 神圣%s | 附魔名称格式 |
| actors.hero.spells.holyweapon.ench_desc | 被神圣武器攻击的敌人会受到额外魔法伤害。 | 附魔描述 |

### 中文翻译来源
actors_zh.properties 文件

## 10. 使用示例

```java
// 施放神圣武器
HolyWeapon.INSTANCE.onCast(holyTome, hero);

// 检查是否有神圣武器效果
if (hero.buff(HolyWeapon.HolyWepBuff.class) != null) {
    // 武器攻击将造成额外伤害
}

// 圣骑士施放其他法术时延长效果
if (hero.subClass == HeroSubClass.PALADIN 
    && hero.buff(HolyWeapon.HolyWepBuff.class) != null) {
    hero.buff(HolyWeapon.HolyWepBuff.class).extend(10 * chargeUsed);
}
```

## 11. 事实核查清单

- [x] 是否已覆盖全部字段（1个静态常量）
- [x] 是否已覆盖全部方法（4个方法）
- [x] 是否已核对官方中文翻译
- [x] 示例代码真实可用