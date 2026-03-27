# HeroIcon 类

## 概述
`HeroIcon` 是 Shattered Pixel Dungeon 中的英雄图标UI组件，继承自 `com.watabou.noosa.Image`。它专门用于显示英雄相关的内容图标，包括职业子类、技能能力、牧师法术和行动指示器等，使用16x16像素的精灵表纹理。

## 功能特性
- **多功能图标支持**：统一管理英雄子类、技能、法术和行动指示器的图标显示
- **精灵表优化**：使用 TextureFilm 高效管理16x16像素的图标纹理
- **类型安全构造**：提供针对不同英雄内容类型的专用构造函数
- **透明图标支持**：包含 NONE 常量（127）用于显示透明/空图标
- **分类组织**：图标按功能分组，使用不同的数值范围避免冲突

## 核心方法

### 构造函数
- `HeroIcon(HeroSubClass subCls)` - 显示英雄子类图标
- `HeroIcon(ArmorAbility abil)` - 显示护甲技能图标  
- `HeroIcon(ActionIndicator.Action action)` - 显示行动指示器图标
- `HeroIcon(ClericSpell spell)` - 显示牧师法术图标

## 图标常量分组

### 通用常量
- `NONE (127)` - 透明/空图标

### 职业子类 (0-11)
- `BERSERKER (0)`, `GLADIATOR (1)`, `BATTLEMAGE (2)`, `WARLOCK (3)`
- `ASSASSIN (4)`, `FREERUNNER (5)`, `SNIPER (6)`, `WARDEN (7)`  
- `CHAMPION (8)`, `MONK (9)`, `PRIEST (10)`, `PALADIN (11)`

### 技能能力 (16-34)
- `HEROIC_LEAP (16)`, `SHOCKWAVE (17)`, `ENDURE (18)`, `ELEMENTAL_BLAST (19)`
- `WILD_MAGIC (20)`, `WARP_BEACON (21)`, `SMOKE_BOMB (22)`, `DEATH_MARK (23)`
- `SHADOW_CLONE (24)`, `SPECTRAL_BLADES (25)`, `NATURES_POWER (26)`, `SPIRIT_HAWK (27)`
- `CHALLENGE (28)`, `ELEMENTAL_STRIKE (29)`, `FEINT (30)`, `ASCENDED_FORM (31)`
- `TRINITY (32)`, `POWER_OF_MANY (33)`, `RATMOGRIFY (34)`

### 牧师法术 (40-66)
- `GUIDING_LIGHT (40)` 到 `STASIS (66)` - 共27个牧师法术图标

### 行动指示器 (104-110)
- `BERSERK (104)`, `COMBO (105)`, `PREPARATION (106)`, `MOMENTUM (107)`
- `SNIPERS_MARK (108)`, `WEAPON_SWAP (109)`, `MONK_ABILITIES (110)`

### 特殊偏移
- `SPELL_ACTION_OFFSET (32)` - 用于牧师法术行动指示器的无背景图标

## 内部组件
- `film` - 静态 TextureFilm 实例，管理16x16像素的图标纹理
- `SIZE` - 图标尺寸常量，固定为16像素
- `texture` - 来自 Assets.Interfaces.HERO_ICONS 的纹理资源

## 使用示例
```java
// 显示狂战士子类图标
HeroIcon berserkerIcon = new HeroIcon(HeroSubClass.BERSERKER);

// 显示英勇跳跃技能图标  
HeroIcon leapIcon = new HeroIcon(armorAbility);

// 显示引导之光法术图标
HeroIcon lightIcon = new HeroIcon(clericSpell);

// 添加到界面
add(berserkerIcon);
```

## 注意事项
- 所有图标都来自统一的 HERO_ICONS 纹理资源文件
- TextureFilm 在首次使用时初始化，后续重复使用同一实例
- 图标索引通过各类型的 icon() 方法获取
- 支持透明图标显示（索引127）
- 牧师法术有专门的行动指示器变体（无背景版本）