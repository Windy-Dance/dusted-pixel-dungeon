# Obfuscation 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/glyphs/Obfuscation.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs |
| **文件类型** | class |
| **继承关系** | extends Armor.Glyph |
| **代码行数** | 51行 |
| **所属模块** | core |
| **稀有度** | common（普通） |

## 2. 文件职责说明

### 核心职责
实现"晦暗"刻印效果，提高穿戴者的潜行值，使其更难被敌人发现。这是一种被动型的隐蔽增强刻印。

### 系统定位
作为普通级别的辅助型刻印，通过增加潜行值来降低被敌人发现的概率，适合需要潜行通过敌人区域的场景。

### 不负责什么
- 不负责潜行系统的具体实现（由 Char.stealth() 负责）
- 不负责敌人的发现机制

## 3. 结构总览

### 主要成员概览
- **GREY**：静态常量，灰色发光效果
- **proc()**：核心方法（空实现，效果在别处触发）
- **stealthBoost()**：静态方法，计算潜行加成
- **glowing()**：返回视觉效果

### 主要逻辑块概览
- 潜行值加成计算

### 生命周期/调用时机
刻印在护甲生成时创建，效果在 Char.stealth() 方法中通过检查刻印触发。

## 4. 继承与协作关系

### 父类提供的能力
- `proc(Armor, Char, Char, int)`：抽象方法
- `glowing()`：抽象方法
- `genericProcChanceMultiplier(Char)`：触发概率乘数计算

### 覆写的方法
| 方法 | 说明 |
|------|------|
| proc() | 空实现，效果在 Char.stealth() 中触发 |
| glowing() | 返回灰色发光效果 |

### 实现的接口契约
继承自 Armor.Glyph 的抽象接口。

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| Armor | 护甲实例 |
| Char | 角色基类 |
| ItemSprite.Glowing | 发光效果 |

### 使用者
- `Char.stealth()`：计算潜行值时调用 stealthBoost() 方法
- `Armor`：管理刻印附着

## 5. 字段/常量详解

### 静态常量

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| GREY | ItemSprite.Glowing | new ItemSprite.Glowing(0x888888) | 灰色发光效果，象征隐匿/晦暗 |

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

**方法职责**：空实现。晦暗刻印的效果不在 proc() 中触发，而是在 Char.stealth() 方法中通过调用 stealthBoost() 实现潜行加成。

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
    //no proc effect, triggered in Char.stealth()
    return damage;
}
```

**边界情况**：此刻印不通过 proc() 方法生效。

---

### stealthBoost()

**可见性**：public static

**是否覆写**：否，静态方法

**方法职责**：计算晦暗刻印提供的潜行值加成。

**参数**：
- `owner` (Char)：护甲穿戴者
- `level` (int)：护甲等级

**返回值**：float，潜行值加成（0表示无加成）

**前置条件**：护甲已装备且有晦暗刻印。

**副作用**：无

**核心实现逻辑**：
```java
public static float stealthBoost( Char owner, int level ){
    if (level == -1) {
        return 0;
    } else {
        return (1 + level / 3f) * genericProcChanceMultiplier(owner);
    }
}
```

**边界情况**：
- 当 level == -1 时返回 0（无加成）
- 潜行加成随等级增长

**潜行加成计算**：
- 等级0：1.0 潜行加成
- 等级3：2.0 潜行加成
- 等级6：3.0 潜行加成

---

### glowing()

**可见性**：public

**是否覆写**：是，覆写自 Armor.Glyph

**方法职责**：返回刻印的视觉发光效果。

**参数**：无

**返回值**：ItemSprite.Glowing，灰色发光效果对象

**核心实现逻辑**：
```java
@Override
public ItemSprite.Glowing glowing() {
    return GREY;
}
```

## 8. 对外暴露能力

### 显式 API
- `proc(Armor, Char, Char, int)`：刻印效果触发（空实现）
- `glowing()`：获取视觉效果
- `stealthBoost(Char, int)`：静态方法，计算潜行加成

### 内部辅助方法
无。

### 扩展入口
可覆写 stealthBoost() 方法修改潜行加成公式。

## 9. 运行机制与调用链

### 创建时机
- 护甲随机生成时，有约12.5%概率获得此刻印（common 类别）
- 通过 Glyph.randomCommon() 方法生成

### 调用者
- `Char.stealth()`：计算潜行值时调用 stealthBoost() 方法
- `Armor`：管理刻印附着

### 系统流程位置
```
敌人发现检测 → Char.stealth() → 检查晦暗刻印 → Obfuscation.stalthBoost()
                                                    ↓
                                              返回潜行加成
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.armor.glyphs.obfuscation.name | 晦暗%s | 刻印名称 |
| items.armor.glyphs.obfuscation.desc | 这个刻印会掩盖使用者的气息，让使用者更难以被发现。 | 刻印描述 |

### 依赖的资源
视觉效果：
- 发光效果：ItemSprite.Glowing（灰色）

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法
```java
// 检查护甲是否有晦暗刻印
Armor armor = hero.belongings.armor();
if (armor != null && armor.hasGlyph(Obfuscation.class, hero)) {
    // 护甲刻有晦暗刻印，潜行值提高
}

// 计算潜行加成
int level = armor.buffedLvl();
float stealth = Obfuscation.stealthBoost(hero, level);
// stealth > 0 表示有潜行加成
```

## 12. 开发注意事项

### 状态依赖
刻印本身无状态，潜行加成是被动效果。

### 生命周期耦合
刻印的生命周期与护甲绑定。

### 常见陷阱
1. **proc() 不生效**：晦暗刻印的 proc() 是空实现
2. **被动效果**：效果持续生效，不需要触发条件
3. **等级 -1**：当护甲无效时，返回潜行加成 0

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改 stealthBoost() 中的潜行加成公式

### 不建议修改的位置
- proc() 方法的空实现是刻意的

### 重构建议
无，当前实现简洁清晰。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：已覆盖 GREY 常量
- [x] 是否已覆盖全部方法：已覆盖 proc()、stealthBoost()、glowing()
- [x] 是否已检查继承链与覆写关系：已说明继承 Armor.Glyph
- [x] 是否已核对官方中文翻译：已使用 items_zh.properties 中的"晦暗"
- [x] 是否存在任何推测性表述：Char.stealth() 调用方式基于源码注释推测
- [x] 示例代码是否真实可用：示例代码基于实际 API
- [x] 是否遗漏资源/配置/本地化关联：已列出相关消息键
- [x] 是否明确说明了注意事项与扩展点：已详细说明