# Affection 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/glyphs/Affection.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs |
| **文件类型** | class |
| **继承关系** | extends Armor.Glyph |
| **代码行数** | 63行 |
| **所属模块** | core |
| **稀有度** | rare（罕见） |

## 2. 文件职责说明

### 核心职责
实现"魅惑"刻印效果，在防御者受到攻击时有一定概率对攻击者施加魅惑（Charm）状态，使其暂时无法攻击防御者。

### 系统定位
作为罕见的防御型刻印，通过控制敌人来间接提升生存能力。属于控制类刻印。

### 不负责什么
- 不负责直接减少伤害
- 不负责魅惑状态的具体效果实现（由 Charm Buff 负责）

## 3. 结构总览

### 主要成员概览
- **PINK**：静态常量，粉色发光效果
- **proc()**：核心方法，处理刻印触发逻辑
- **glowing()**：返回视觉效果

### 主要逻辑块概览
- 触发概率计算
- 魅惑状态施加
- 视觉特效播放

### 生命周期/调用时机
在 Armor.proc() 方法中被调用，当护甲穿戴者受到攻击时触发。

## 4. 继承与协作关系

### 父类提供的能力
- `proc(Armor, Char, Char, int)`：抽象方法，需实现
- `glowing()`：抽象方法，需实现
- `procChanceMultiplier(Char)`：触发概率乘数计算
- `name()` / `desc()`：名称和描述获取

### 覆写的方法
| 方法 | 说明 |
|------|------|
| proc() | 实现魅惑效果的触发逻辑 |
| glowing() | 返回粉色发光效果 |

### 实现的接口契约
继承自 Armor.Glyph 的抽象接口。

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| Armor | 护甲实例 |
| Char | 角色基类 |
| Buff | 状态效果基类 |
| Charm | 魅惑状态效果 |
| Speck | 粒子效果 |
| ItemSprite.Glowing | 发光效果 |
| Random | 随机数生成 |

### 使用者
- Armor 类：通过 Armor.proc() 调用
- 随机生成系统：通过 Glyph.randomRare() 生成

## 5. 字段/常量详解

### 静态常量

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| PINK | ItemSprite.Glowing | new ItemSprite.Glowing(0xFF4488) | 粉色发光效果，用于护甲的外观特效 |

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

**方法职责**：处理刻印触发效果，对攻击者施加魅惑状态。

**参数**：
- `armor` (Armor)：触发刻印的护甲实例
- `attacker` (Char)：攻击者
- `defender` (Char)：防御者（护甲穿戴者）
- `damage` (int)：原始伤害值

**返回值**：int，返回传入的 damage 参数（不修改伤害）

**前置条件**：护甲已装备且存在此刻印。

**副作用**：
- 可能对攻击者施加 Charm 状态
- 播放心形粒子特效

**核心实现逻辑**：
```java
@Override
public int proc( Armor armor, Char attacker, Char defender, int damage) {

    int level = Math.max(0, armor.buffedLvl());

    // lvl 0 - 15%
    // lvl 1 ~ 19%
    // lvl 2 ~ 23%
    float procChance = (level+3f)/(level+20f) * procChanceMultiplier(defender);
    if (Random.Float() < procChance) {

        float powerMulti = Math.max(1f, procChance);

        Buff.affect( attacker, Charm.class, Math.round(Charm.DURATION*powerMulti) ).object = defender.id();
        attacker.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 5 );

    }

    return damage;
}
```

**触发概率计算**：
- 公式：`(level+3)/(level+20) * procChanceMultiplier`
- +0 级：15% 基础概率
- +1 级：约 19% 概率
- +2 级：约 23% 概率

**魅惑持续时间**：
- 基础：Charm.DURATION（由 Charm 类定义）
- 乘数：max(1, procChance)，概率越高持续时间越长

**边界情况**：
- 当 armor.buffedLvl() 返回负值时，使用 0 作为等级
- procChance 可能超过 100%，此时 powerMulti 会大于 1

---

### glowing()

**可见性**：public

**是否覆写**：是，覆写自 Armor.Glyph

**方法职责**：返回刻印的视觉发光效果。

**参数**：无

**返回值**：ItemSprite.Glowing，粉色发光效果对象

**核心实现逻辑**：
```java
@Override
public Glowing glowing() {
    return PINK;
}
```

## 8. 对外暴露能力

### 显式 API
- `proc(Armor, Char, Char, int)`：刻印触发入口
- `glowing()`：视觉效果获取

### 内部辅助方法
无。

### 扩展入口
无显式扩展点，作为完整实现类使用。

## 9. 运行机制与调用链

### 创建时机
- 护甲随机生成时（约 3.33% 概率从 rare 池中选择）
- 炼金附魔时

### 调用者
```
战斗攻击 → Armor.proc() → Affection.proc() → Charm Buff 施加
```

### 被调用者
- `Charm`：魅惑状态效果
- `Buff.affect()`：状态施加工具
- `Speck`：粒子效果

### 系统流程位置
```
攻击命中 → 伤害计算 → 刻印触发检查 → Affection.proc() → 魅惑判定
    ↓
触发成功 → Charm Buff 施加 → 心形粒子特效
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.armor.glyphs.affection.name | 魅惑%s | 刻印名称（%s 为护甲名） |
| items.armor.glyphs.affection.desc | 这个强力的刻印能够操控攻击者的心智，暂时地魅惑他们。 | 刻印描述 |

### 依赖的资源
- 粒子效果：Speck.HEART（心形粒子）
- 视觉效果：PINK 发光

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法
```java
// 检查护甲是否有魅惑刻印
if (armor.hasGlyph(Affection.class, hero)) {
    // 护甲有魅惑刻印
}

// 直接创建并附着刻印
armor.inscribe(new Affection());
```

### 触发效果示例
```
英雄穿戴带有魅惑刻印的护甲
    ↓
怪物攻击英雄
    ↓
刻印触发（概率性）
    ↓
怪物被施加魅惑状态
    ↓
怪物在魅惑持续期间无法攻击英雄
```

## 12. 开发注意事项

### 状态依赖
魅惑效果依赖 Charm Buff 类实现具体行为。

### 生命周期耦合
刻印效果仅在护甲被穿戴并受到攻击时触发。

### 常见陷阱
1. **魅惑对象设置**：必须设置 Charm.object 为防御者 id，否则魅惑行为异常
2. **粒子效果位置**：粒子在攻击者中心播放，而非防御者
3. **不减少伤害**：此刻印不修改 damage 参数

## 13. 修改建议与扩展点

### 适合扩展的位置
可参考此实现创建类似的控制型刻印（如：混乱、恐惧等）。

### 不建议修改的位置
- 触发概率公式：影响游戏平衡
- Charm.DURATION 引用：应保持与 Charm 类的同步

### 重构建议
无，实现简洁合理。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：已覆盖 PINK 静态常量
- [x] 是否已覆盖全部方法：已覆盖 proc() 和 glowing()
- [x] 是否已检查继承链与覆写关系：已说明继承自 Armor.Glyph
- [x] 是否已核对官方中文翻译：已使用 items_zh.properties 中的"魅惑"
- [x] 是否存在任何推测性表述：无，全部基于源码
- [x] 示例代码是否真实可用：示例代码基于实际 API
- [x] 是否遗漏资源/配置/本地化关联：已列出相关消息键
- [x] 是否明确说明了注意事项与扩展点：已详细说明