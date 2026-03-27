# RecallInscription 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/RecallInscription.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends ClericSpell |
| **代码行数** | 176 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
RecallInscription（卷藏咒言）复制最近使用的卷轴或符石效果。

### 系统定位
作为第2层级的复制法术：
- 需要天赋 RECALL_INSCRIPTION 解锁
- 充能消耗根据复制物品类型变化
- 不能复制升级卷轴

## 3. 方法详解

### icon()
**返回值**：HeroIcon.RECALL_GLYPH

### onCast()
**核心逻辑**：
1. 检查是否有 UsedItemTracker
2. 反射创建物品实例
3. 执行物品效果（卷轴读取/符石投掷）

### chargeUse()
**动态计算**：
| 物品类型 | 充能消耗 |
|---------|---------|
| 秘卷（嬗变/附魔） | 8 |
| 其他秘卷 | 4 |
| 嬗变卷轴 | 6 |
| 其他卷轴 | 3 |
| 增幅/附魔符石 | 4 |
| 其他符石 | 2 |

### canCast()
**前置条件**：
- 需要 RECALL_INSCRIPTION 天赋
- 需要有 UsedItemTracker（最近使用过兼容物品）

### desc()
**动态计算**：显示记忆持续时间（天赋1时10回合，天赋2时300回合）

## 4. 内部类详解

### UsedItemTracker

**类型**：public static class extends FlavourBuff

**职责**：追踪最近使用的卷轴/符石，供复制使用。

**字段**：
- `item`：Class\<? extends Item\>，物品类型

**主要方法**：
- `icon()`：返回 GLYPH_RECALL 图标
- `desc()`：显示已使用物品名称和剩余时间

## 5. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.recallinscription.name | 卷藏咒言 |
| actors.hero.spells.recallinscription.short_desc | 重复最近使用的符石或卷轴效果。 |
| actors.hero.spells.recallinscription.desc | 牧师使用神圣魔法复制最近使用的符文... |

### 中文翻译来源
actors_zh.properties 文件

## 6. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译