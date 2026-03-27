# Thorns 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/glyphs/Thorns.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs |
| **文件类型** | class |
| **继承关系** | extends Armor.Glyph |
| **代码行数** | 59行 |
| **所属模块** | core |
| **稀有度** | rare（罕见） |

## 2. 文件职责说明

### 核心职责
实现"荆棘"刻印效果，当穿戴者受到攻击时有概率使攻击者获得流血状态。这是一种反击型的防御刻印。

### 系统定位
作为罕见级别的反击型刻印，通过对攻击者施加持续伤害来威慑敌人，适合近战肉搏的战斗风格。

### 不负责什么
- 不负责直接减少伤害
- 不负责流血状态的具体效果实现（由 Bleeding Buff 负责）

## 3. 结构总览

### 主要成员概览
- **RED**：静态常量，暗红色发光效果
- **proc()**：核心方法，处理刻印触发逻辑
- **glowing()**：返回视觉效果

### 主要逻辑块概览
- 触发概率计算
- 流血状态施加

### 生命周期/调用时机
在 Armor.proc() 方法中被调用，当护甲穿戴者受到攻击时触发。

## 4. 继承与协作关系

### 父类提供的能力
- `proc(Armor, Char, Char, int)`：抽象方法，需实现
- `glowing()`：抽象方法，需实现
- `procChanceMultiplier(Char)`：触发概率乘数计算

### 覆写的方法
| 方法 | 说明 |
|------|------|
| proc() | 实现流血效果的触发逻辑 |
| glowing() | 返回暗红色发光效果 |

### 实现的接口契约
继承自 Armor.Glyph 的抽象接口。

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| Armor | 护甲实例 |
| Char | 角色基类 |
| Buff | 状态效果基类 |
| Bleeding | 流血状态效果 |
| ItemSprite.Glowing | 发光效果 |
| Random | 随机数生成 |

### 使用者
- `Armor`：通过 Armor.proc() 调用
- 随机生成系统：通过 Glyph.randomRare() 生成

## 5. 字段/常量详解

### 静态常量

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| RED | ItemSprite.Glowing | new ItemSprite.Glowing(0x660022) | 暗红色发光效果，象征血液/荆棘 |

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

**方法职责**：处理刻印触发效果，对攻击者施加流血状态。

**参数**：
- `armor` (Armor)：触发刻印的护甲实例
- `attacker` (Char)：攻击者
- `defender` (Char)：防御者（护甲穿戴者）
- `damage` (int)：原始伤害值

**返回值**：int，返回原始伤害值（此刻印不修改伤害）

**前置条件**：护甲已装备且存在刻印。

**副作用**：可能对攻击者施加 Bleeding 状态效果。

**核心实现逻辑**：
```java
@Override
public int proc(Armor armor, Char attacker, Char defender, int damage) {

    int level = Math.max(0, armor.buffedLvl());

    // lvl 0 - 16.7%
    // lvl 1 - 23.1%
    // lvl 2 - 28.5%
    float procChance = (level+2f)/(level+12f) * procChanceMultiplier(defender);
    if ( attacker.alignment != defender.alignment && Random.Float() < procChance ) {

        float powerMulti = Math.max(1f, procChance);

        Buff.affect( attacker, Bleeding.class).set( Math.round((4 + level)*powerMulti) );

    }

    return damage;
}
```

**边界情况**：
- 只有当攻击者与防御者阵营不同时才会触发
- 触发概率随等级提升而增加
- 流血伤害量随等级提升

**触发概率计算**：
- 等级0：(0+2)/(0+12) ≈ 16.7%
- 等级1：(1+2)/(1+12) ≈ 23.1%
- 等级5：(5+2)/(5+12) ≈ 41.2%

**流血伤害计算**：
- 等级0：(4 + 0) = 4 流血伤害/回合
- 等级5：(4 + 5) = 9 流血伤害/回合
- 实际流血伤害 = round((4 + level) * powerMulti)

---

### glowing()

**可见性**：public

**是否覆写**：是，覆写自 Armor.Glyph

**方法职责**：返回刻印的视觉发光效果。

**参数**：无

**返回值**：ItemSprite.Glowing，暗红色发光效果对象

**核心实现逻辑**：
```java
@Override
public ItemSprite.Glowing glowing() {
    return RED;
}
```

## 8. 对外暴露能力

### 显式 API
- `proc(Armor, Char, Char, int)`：刻印效果触发
- `glowing()`：获取视觉效果

### 内部辅助方法
无。

### 扩展入口
可覆写 proc() 方法修改触发概率或流血伤害量。

## 9. 运行机制与调用链

### 创建时机
- 护甲随机生成时，有约3.33%概率获得此刻印（rare 类别）
- 通过 Glyph.randomRare() 方法生成

### 调用者
- `Armor.proc()`：在战斗中调用刻印的 proc 方法

### 被调用者
- `Bleeding`：流血状态效果
- `Buff.affect()`：状态施加方法

### 系统流程位置
```
战斗攻击 → Armor.proc() → Thorns.proc() → 阵营检查 + 概率判定
                                              ↓
                                      施加流血状态
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.armor.glyphs.thorns.name | 荆棘%s | 刻印名称 |
| items.armor.glyphs.thorns.desc | 这个强力的刻印会伤害那些攻击穿戴者的敌人，使它们缓慢流血。 | 刻印描述 |

### 依赖的资源
视觉效果：
- 发光效果：ItemSprite.Glowing（暗红色）

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法
```java
// 检查护甲是否有荆棘刻印
Armor armor = hero.belongings.armor();
if (armor != null && armor.hasGlyph(Thorns.class, hero)) {
    // 护甲刻有荆棘刻印，攻击者会被施加流血
}

// 直接创建荆棘刻印并附着
armor.inscribe(new Thorns());
```

### 流血伤害计算示例
```java
// 等级0时：流血伤害 = 4
// 等级5时：流血伤害 = 9
// 如果 procChance > 1，powerMulti 会增加流血伤害
```

## 12. 开发注意事项

### 状态依赖
刻印本身无状态，流血效果由 Bleeding Buff 管理。

### 生命周期耦合
刻印的生命周期与护甲绑定。流血效果的生命周期由 Buff 管理。

### 常见陷阱
1. **阵营检查**：只有不同阵营的攻击才会触发，友军误伤不会触发
2. **流血叠加**：多次触发会刷新流血伤害，而非叠加
3. **不减少伤害**：此刻印不修改 damage 参数

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改触发概率公式
- 修改流血伤害公式

### 不建议修改的位置
- 阵营检查是必要的限制

### 重构建议
无，当前实现简洁清晰。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：已覆盖 RED 常量
- [x] 是否已覆盖全部方法：已覆盖 proc() 和 glowing()
- [x] 是否已检查继承链与覆写关系：已说明继承 Armor.Glyph
- [x] 是否已核对官方中文翻译：已使用 items_zh.properties 中的"荆棘"
- [x] 是否存在任何推测性表述：无，全部基于源码
- [x] 示例代码是否真实可用：示例代码基于实际 API
- [x] 是否遗漏资源/配置/本地化关联：已列出相关消息键
- [x] 是否明确说明了注意事项与扩展点：已详细说明