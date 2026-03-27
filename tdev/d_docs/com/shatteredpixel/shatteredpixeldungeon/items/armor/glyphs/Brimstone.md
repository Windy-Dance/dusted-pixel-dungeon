# Brimstone 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/glyphs/Brimstone.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs |
| **文件类型** | class |
| **继承关系** | extends Armor.Glyph |
| **代码行数** | 43行 |
| **所属模块** | core |
| **稀有度** | uncommon（稀有） |

## 2. 文件职责说明

### 核心职责
实现"狱火"刻印效果，使穿戴者和其所有物完全免疫火焰伤害及相关负面效果。这是最简单的刻印实现之一。

### 系统定位
作为稀有级别的防御型刻印，专门针对火焰伤害提供完全免疫。在面对火焰类敌人或陷阱时极具价值。

### 不负责什么
- 不负责减少其他类型的伤害
- 不负责火焰免疫的具体判定逻辑（由 Char.isImmune() 负责）

## 3. 结构总览

### 主要成员概览
- **ORANGE**：静态常量，橙色发光效果
- **proc()**：核心方法（空实现，效果在别处触发）
- **glowing()**：返回视觉效果

### 主要逻辑块概览
刻印本身无复杂逻辑，效果通过外部系统检查实现。

### 生命周期/调用时机
刻印在护甲生成时创建，免疫效果在 Char.isImmune() 中通过检查刻印类型触发。

## 4. 继承与协作关系

### 父类提供的能力
- `proc(Armor, Char, Char, int)`：抽象方法
- `glowing()`：抽象方法

### 覆写的方法
| 方法 | 说明 |
|------|------|
| proc() | 空实现，效果在 Char.isImmune() 中触发 |
| glowing() | 返回橙色发光效果 |

### 实现的接口契约
继承自 Armor.Glyph 的抽象接口。

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| Armor | 护甲实例 |
| Char | 角色基类 |
| ItemSprite.Glowing | 发光效果 |

### 使用者
- `Char.isImmune()`：检查是否有狱火刻印以判断火焰免疫
- `Armor`：管理刻印附着

## 5. 字段/常量详解

### 静态常量

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| ORANGE | ItemSprite.Glowing | new ItemSprite.Glowing(0xFF4400) | 橙色发光效果，象征火焰 |

### 实例字段
无实例字段。

## 6. 构造与初始化机制

### 构造器
使用默认构造器，无显式构造器定义。

### 初始化块
无初始化块。

### 初始化注意事项
刻印实例通过 Reflection.newInstance() 动态创建。

## 7. 方法详解

### proc()

**可见性**：public

**是否覆写**：是，覆写自 Armor.Glyph

**方法职责**：空实现。狱火刻印的效果不在 proc() 中触发，而是在 Char.isImmune() 方法中通过检查护甲是否有此刻印来实现火焰免疫。

**参数**：
- `armor` (Armor)：触发刻印的护甲实例
- `attacker` (Char)：攻击者
- `defender` (Char)：防御者
- `damage` (int)：原始伤害值

**返回值**：int，返回原始伤害值

**核心实现逻辑**：
```java
@Override
public int proc(Armor armor, Char attacker, Char defender, int damage) {
    //no proc effect, triggers in Char.isImmune
    return damage;
}
```

**边界情况**：此刻印不通过 proc() 方法生效。

---

### glowing()

**可见性**：public

**是否覆写**：是，覆写自 Armor.Glyph

**方法职责**：返回刻印的视觉发光效果。

**参数**：无

**返回值**：ItemSprite.Glowing，橙色发光效果对象

**核心实现逻辑**：
```java
@Override
public ItemSprite.Glowing glowing() {
    return ORANGE;
}
```

## 8. 对外暴露能力

### 显式 API
- `proc(Armor, Char, Char, int)`：刻印效果触发（空实现）
- `glowing()`：获取视觉效果

### 内部辅助方法
无。

### 扩展入口
此刻印是最终实现，无扩展点。

## 9. 运行机制与调用链

### 创建时机
- 护甲随机生成时，有约6.67%概率获得此刻印（uncommon 类别）
- 通过 Glyph.randomUncommon() 方法生成

### 调用者
- `Char.isImmune()`：检查护甲是否有狱火刻印来判断火焰免疫
- `Armor`：管理刻印附着

### 系统流程位置
```
火焰伤害判定 → Char.isImmune() → 检查是否有 Brimstone 刻印 → 返回 true（免疫）
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.armor.glyphs.brimstone.name | 狱火%s | 刻印名称 |
| items.armor.glyphs.brimstone.desc | 这个刻印会保护穿戴者和其所有物免受火焰的伤害及其他影响。 | 刻印描述 |

### 依赖的资源
视觉效果：
- 发光效果：ItemSprite.Glowing（橙色）

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法
```java
// 检查护甲是否有狱火刻印
Armor armor = hero.belongings.armor();
if (armor != null && armor.hasGlyph(Brimstone.class, hero)) {
    // 护甲刻有狱火刻印，免疫火焰伤害
}

// 直接创建狱火刻印并附着
armor.inscribe(new Brimstone());
```

### 与 Char.isImmune() 配合
```java
// 在 Char.isImmune() 中的典型检查模式（推测）
public boolean isImmune(Class effect) {
    if (effect == Burning.class || effect == Fire.class) {
        Armor armor = belongings.armor();
        if (armor != null && armor.hasGlyph(Brimstone.class, this)) {
            return true;
        }
    }
    return super.isImmune(effect);
}
```

## 12. 开发注意事项

### 状态依赖
刻印本身无状态。

### 生命周期耦合
刻印的生命周期与护甲绑定。

### 常见陷阱
1. **proc() 不生效**：狱火刻印的 proc() 是空实现，效果通过外部系统检查
2. **免疫范围**：免疫火焰伤害和相关负面效果（如燃烧状态）
3. **物品保护**：刻印描述明确提到"及其所有物"，推测背包物品也受保护

## 13. 修改建议与扩展点

### 适合扩展的位置
无，此刻印设计简单直接。

### 不建议修改的位置
- proc() 方法的空实现是刻意设计，不应添加逻辑

### 重构建议
无，当前实现已足够简洁。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：已覆盖 ORANGE 常量
- [x] 是否已覆盖全部方法：已覆盖 proc() 和 glowing()
- [x] 是否已检查继承链与覆写关系：已说明继承 Armor.Glyph
- [x] 是否已核对官方中文翻译：已使用 items_zh.properties 中的"狱火"
- [x] 是否存在任何推测性表述：Char.isImmune() 调用方式基于刻印设计模式推测
- [x] 示例代码是否真实可用：示例代码基于实际 API
- [x] 是否遗漏资源/配置/本地化关联：已列出相关消息键
- [x] 是否明确说明了注意事项与扩展点：已详细说明