# Entanglement 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/glyphs/Entanglement.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs |
| **文件类型** | class |
| **继承关系** | extends Armor.Glyph |
| **代码行数** | 65行 |
| **所属模块** | core |
| **稀有度** | uncommon（稀有） |

## 2. 文件职责说明

### 核心职责
实现"缠绕"刻印效果，当穿戴者受到攻击时有概率获得"地根护甲"（Earthroot.Armor），提供额外的伤害吸收能力。

### 系统定位
作为稀有级别的防御型刻印，通过生成吸收伤害的护盾来提高生存能力。护盾在移动后会消失，鼓励玩家采取防守策略。

### 不负责什么
- 不负责直接减少伤害（由地根护甲负责）
- 不负责护盾的具体效果实现（由 Earthroot.Armor Buff 负责）

## 3. 结构总览

### 主要成员概览
- **BROWN**：静态常量，棕色发光效果
- **proc()**：核心方法，处理刻印触发逻辑
- **glowing()**：返回视觉效果

### 主要逻辑块概览
- 触发概率计算
- 地根护甲生成
- 视觉特效播放
- 屏幕震动效果

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
| proc() | 实现地根护甲生成的触发逻辑 |
| glowing() | 返回棕色发光效果 |

### 实现的接口契约
继承自 Armor.Glyph 的抽象接口。

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| Armor | 护甲实例 |
| Char | 角色基类 |
| Buff | 状态效果基类 |
| Earthroot.Armor | 地根护甲状态效果（伤害吸收护盾） |
| CellEmitter | 单元格粒子发射器 |
| EarthParticle | 大地粒子效果 |
| PixelScene | 像素场景（屏幕震动） |
| Dungeon | 地牢实例 |
| ItemSprite.Glowing | 发光效果 |
| Random | 随机数生成 |

### 使用者
- `Armor`：通过 Armor.proc() 调用
- 随机生成系统：通过 Glyph.randomUncommon() 生成

## 5. 字段/常量详解

### 静态常量

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| BROWN | ItemSprite.Glowing | new ItemSprite.Glowing(0x663300) | 棕色发光效果，象征大地/树根 |

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

**方法职责**：处理刻印触发效果，对防御者施加地根护甲状态。

**参数**：
- `armor` (Armor)：触发刻印的护甲实例
- `attacker` (Char)：攻击者
- `defender` (Char)：防御者（护甲穿戴者）
- `damage` (int)：原始伤害值

**返回值**：int，返回原始伤害值（此刻印不修改伤害）

**前置条件**：护甲已装备且存在刻印。

**副作用**：
- 对防御者施加 Earthroot.Armor 状态
- 播放大地粒子效果
- 如果防御者是英雄，产生屏幕震动效果

**核心实现逻辑**：
```java
@Override
public int proc(Armor armor, Char attacker, final Char defender, final int damage ) {

    final int level = Math.max( 0, armor.buffedLvl() );
    float procChance = 1/4f * procChanceMultiplier(defender);

    if (Random.Float() < procChance) {

        float powerMulti = Math.max(1f, procChance);

        Buff.affect( defender, Earthroot.Armor.class ).level( Math.round((5 + 2 * level)*powerMulti) );
        CellEmitter.bottom( defender.pos ).start( EarthParticle.FACTORY, 0.05f, 8 );
        if (defender == Dungeon.hero) PixelScene.shake( 1, 0.4f );
        
    }

    return damage;
}
```

**边界情况**：
- 基础触发概率固定为 25%（1/4）
- 护甲等级只影响护盾强度，不影响触发概率
- 当 procChance > 1 时，powerMulti 会增加护盾强度

---

### glowing()

**可见性**：public

**是否覆写**：是，覆写自 Armor.Glyph

**方法职责**：返回刻印的视觉发光效果。

**参数**：无

**返回值**：ItemSprite.Glowing，棕色发光效果对象

**核心实现逻辑**：
```java
@Override
public Glowing glowing() {
    return BROWN;
}
```

## 8. 对外暴露能力

### 显式 API
- `proc(Armor, Char, Char, int)`：刻印效果触发
- `glowing()`：获取视觉效果

### 内部辅助方法
无。

### 扩展入口
可覆写 proc() 方法修改触发概率或效果强度。

## 9. 运行机制与调用链

### 创建时机
- 护甲随机生成时，有约6.67%概率获得此刻印（uncommon 类别）
- 通过 Glyph.randomUncommon() 方法生成

### 调用者
- `Armor.proc()`：在战斗中调用刻印的 proc 方法

### 被调用者
- `Earthroot.Armor`：地根护甲状态效果
- `CellEmitter`：粒子效果
- `PixelScene.shake()`：屏幕震动

### 系统流程位置
```
战斗攻击 → Armor.proc() → Entanglement.proc() → 概率判定 → 施加地根护甲
                                                               ↓
                                                      播放粒子效果 + 屏幕震动
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.armor.glyphs.entanglement.name | 缠绕%s | 刻印名称 |
| items.armor.glyphs.entanglement.desc | 这个刻印会在使用者周围生出能吸收伤害的地根护甲。这种地根护甲会在使用者移动后散落失效。 | 刻印描述 |

### 依赖的资源
视觉效果：
- 发光效果：ItemSprite.Glowing（棕色）
- 粒子效果：EarthParticle（大地粒子）

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法
```java
// 检查护甲是否有缠绕刻印
Armor armor = hero.belongings.armor();
if (armor != null && armor.hasGlyph(Entanglement.class, hero)) {
    // 护甲刻有缠绕刻印
}

// 直接创建缠绕刻印并附着
armor.inscribe(new Entanglement());
```

### 护盾强度计算
```java
// 等级0时：(5 + 0) = 5 吸收值
// 等级1时：(5 + 2) = 7 吸收值
// 等级5时：(5 + 10) = 15 吸收值
// 实际吸收值 = round((5 + 2 * level) * powerMulti)
```

## 12. 开发注意事项

### 状态依赖
刻印本身无状态，护盾由 Earthroot.Armor Buff 管理。

### 生命周期耦合
刻印的生命周期与护甲绑定。护盾的生命周期由 Buff 管理（移动后消失）。

### 常见陷阱
1. **固定触发概率**：触发概率固定为 25%，不受等级影响
2. **护盾消失条件**：地根护甲在移动后会消失，这是 Earthroot.Armor 的特性
3. **护盾叠加**：每次触发会刷新护盾值，而非叠加

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改触发概率公式
- 修改护盾强度公式

### 不建议修改的位置
- 护盾的消失逻辑由 Earthroot.Armor 管理，不应在此修改

### 重构建议
无，当前实现简洁清晰。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：已覆盖 BROWN 常量
- [x] 是否已覆盖全部方法：已覆盖 proc() 和 glowing()
- [x] 是否已检查继承链与覆写关系：已说明继承 Armor.Glyph
- [x] 是否已核对官方中文翻译：已使用 items_zh.properties 中的"缠绕"
- [x] 是否存在任何推测性表述：无，全部基于源码
- [x] 示例代码是否真实可用：示例代码基于实际 API
- [x] 是否遗漏资源/配置/本地化关联：已列出相关消息键
- [x] 是否明确说明了注意事项与扩展点：已详细说明