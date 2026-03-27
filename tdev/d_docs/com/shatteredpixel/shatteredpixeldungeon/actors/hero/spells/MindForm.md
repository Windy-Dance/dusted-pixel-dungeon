# MindForm 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/MindForm.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends ClericSpell |
| **代码行数** | 177 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
MindForm（智之位格）为三位一体能力选择法杖或投掷武器效果。

### 系统定位
作为第4层级的位格法术：
- 需要天赋 MIND_FORM 解锁
- 与 Trinity（三位一体）护甲能力协同
- 提供单次法杖施法或投武攻击

## 3. 方法详解

### icon()
**返回值**：HeroIcon.MIND_FORM

### chargeUse()
**返回值**：固定返回 3

### canCast()
**前置条件**：需要 MIND_FORM 天赋

### onCast()
**核心逻辑**：打开物品类型选择窗口

### itemLevel()
**静态方法**：返回 2 + 天赋等级（法杖/投武的强化等级）

## 4. 内部类详解

### targetSelector

**类型**：public static class extends CellSelector.Listener

**职责**：目标选择器，处理法杖施法或投武攻击。

**主要方法**：
- `wand()`：获取法杖效果
- `thrown()`：获取投武效果
- `onSelect(Integer)`：执行法杖施法或投武攻击

## 5. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.mindform.name | 智之位格 |
| actors.hero.spells.mindform.short_desc | 为三位一体选择法杖或投武。 |
| actors.hero.spells.mindform.desc | 牧师选择一个本局已鉴定的法杖或投武并使三位一体模拟其效果... |

### 中文翻译来源
actors_zh.properties 文件

## 6. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译