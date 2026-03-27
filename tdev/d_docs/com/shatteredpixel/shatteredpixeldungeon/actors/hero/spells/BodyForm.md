# BodyForm 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/BodyForm.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends ClericSpell |
| **代码行数** | 141 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
BodyForm（体之位格）为三位一体能力选择附魔或刻印效果。

### 系统定位
作为第4层级的位格法术：
- 需要天赋 BODY_FORM 解锁
- 与 Trinity（三位一体）护甲能力协同
- 提供临时附魔/刻印效果

## 3. 方法详解

### icon()
**返回值**：HeroIcon.BODY_FORM

### chargeUse()
**返回值**：固定返回 2

### canCast()
**前置条件**：需要 BODY_FORM 天赋

### onCast()
**核心逻辑**：
```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    GameScene.show(new Trinity.WndItemtypeSelect(tome, this));
}
```
打开物品类型选择窗口，供玩家选择附魔或刻印。

### duration()
**静态方法**：根据天赋等级计算持续时间
- 基础：13回合
- 每级天赋：+7回合

## 4. 内部类详解

### BodyFormBuff

**类型**：public static class extends FlavourBuff

**职责**：体之位格效果Buff，存储附魔或刻印效果。

**字段**：
- `effect`：Bundlable，存储附魔或刻印对象

**主要方法**：
- `icon()`：返回 TRINITY_FORM 图标
- `tintIcon(Image)`：将图标染成红色
- `setEffect(Bundlable)`：设置效果
- `enchant()`：获取附魔效果
- `glyph()`：获取刻印效果
- `desc()`：返回带效果名称的描述

## 5. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.bodyform.name | 体之位格 |
| actors.hero.spells.bodyform.short_desc | 为三位一体选择附魔或刻印。 |
| actors.hero.spells.bodyform.desc | 牧师选择一个本局已鉴定的附魔或刻印并使三位一体模拟其效果... |

### 中文翻译来源
actors_zh.properties 文件

## 6. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译