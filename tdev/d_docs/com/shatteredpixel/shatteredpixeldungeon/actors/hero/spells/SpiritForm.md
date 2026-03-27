# SpiritForm 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/SpiritForm.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends ClericSpell |
| **代码行数** | 258 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
SpiritForm（魂之位格）为三位一体能力选择戒指或神器效果。

### 系统定位
作为第4层级的位格法术：
- 需要天赋 SPIRIT_FORM 解锁
- 与 Trinity（三位一体）护甲能力协同
- 提供戒指效果或神器效果

## 3. 方法详解

### icon()
**返回值**：HeroIcon.SPIRIT_FORM

### chargeUse()
**返回值**：固定返回 4

### canCast()
**前置条件**：需要 SPIRIT_FORM 天赋

### onCast()
**核心逻辑**：打开物品类型选择窗口

### ringLevel()
**静态方法**：返回天赋等级（戒指强化等级）

### artifactLevel()
**静态方法**：返回 2 + 2 × 天赋等级（神器强化等级）

## 4. 内部类详解

### SpiritFormBuff

**类型**：public static class extends FlavourBuff

**职责**：魂之位格效果Buff，存储戒指或神器效果。

**常量**：
- `DURATION = 20f`：持续时间

**字段**：
- `effect`：Bundlable，存储戒指或神器对象

**主要方法**：
- `icon()`：返回 TRINITY_FORM 图标
- `tintIcon(Image)`：将图标染成绿色
- `ring()`：获取戒指效果
- `artifact()`：获取神器效果

### applyActiveArtifactEffect()
**静态方法**：应用各种神器的主动效果。

## 5. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.spiritform.name | 魂之位格 |
| actors.hero.spells.spiritform.short_desc | 为三位一体选择戒指或神器。 |
| actors.hero.spells.spiritform.desc | 牧师选择本局已鉴定的戒指或神器并使三位一体模拟其效果... |

### 中文翻译来源
actors_zh.properties 文件

## 6. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译