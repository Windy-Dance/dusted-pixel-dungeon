# Stone 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/glyphs/Stone.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs |
| **文件类型** | class |
| **继承关系** | extends Armor.Glyph |
| **代码行数** | 112行 |
| **所属模块** | core |
| **稀有度** | uncommon（稀有） |

## 2. 文件职责说明

### 核心职责
实现"磐岩"刻印效果，将穿戴者的闪避能力转化为伤害减免。穿戴者无法闪避，但会根据原本的闪避能力获得伤害减免。这是一种高风险高回报的防御型刻印。

### 系统定位
作为稀有级别的防御型刻印，通过牺牲闪避能力来换取稳定的伤害减免，适合重视稳定防御而非赌闪避的玩家。

### 不负责什么
- 不负责基础的闪避计算（由 Char.defenseSkill() 负责）
- 不负责修改闪避显示（由 Armor.evasionFactor() 负责）

## 3. 结构总览

### 主要成员概览
- **GREY**：静态常量，深灰色发光效果
- **testing**：静态变量，用于测试时标记
- **proc()**：核心方法，处理伤害减免逻辑
- **testingEvasion()**：静态方法，返回测试状态
- **glowing()**：返回视觉效果

### 主要逻辑块概览
- 命中率计算（考虑各种修正）
- 伤害减免转换
- 边界值处理

### 生命周期/调用时机
在 Armor.proc() 方法中被调用，当护甲穿戴者受到攻击时触发。

## 4. 继承与协作关系

### 父类提供的能力
- `proc(Armor, Char, Char, int)`：抽象方法，需实现
- `glowing()`：抽象方法，需实现
- `genericProcChanceMultiplier(Char)`：触发概率乘数计算

### 覆写的方法
| 方法 | 说明 |
|------|------|
| proc() | 实现伤害减免的转换逻辑 |
| glowing() | 返回深灰色发光效果 |

### 实现的接口契约
继承自 Armor.Glyph 的抽象接口。

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| Armor | 护甲实例 |
| Char | 角色基类 |
| Dungeon | 地牢实例 |
| Bless | 祝福状态 |
| Hex | 诅咒状态 |
| Daze | 眩晕状态 |
| ChampionEnemy | 精英敌人状态 |
| AscensionChallenge | 飞升挑战修正 |
| Talent | 天赋系统 |
| FerretTuft | 雪貂毛饰物 |
| GameMath | 游戏数学工具 |
| ItemSprite.Glowing | 发光效果 |

### 使用者
- `Armor`：通过 Armor.proc() 调用
- `Armor.evasionFactor()`：检查是否有磐岩刻印以将闪避设为 0
- 随机生成系统：通过 Glyph.randomUncommon() 生成

## 5. 字段/常量详解

### 静态常量

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| GREY | ItemSprite.Glowing | new ItemSprite.Glowing(0x222222) | 深灰色发光效果，象征岩石 |

### 静态变量

| 变量名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| testing | boolean | false | 用于在测试闪避时标记，防止 proc() 中的闪避计算产生循环调用 |

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

**方法职责**：将闪避能力转化为伤害减免。根据攻击者的命中率计算伤害减免比例，然后减少伤害。

**参数**：
- `armor` (Armor)：触发刻印的护甲实例
- `attacker` (Char)：攻击者
- `defender` (Char)：防御者（护甲穿戴者）
- `damage` (int)：原始伤害值

**返回值**：int，减免后的伤害值

**前置条件**：护甲已装备且存在刻印。

**副作用**：无（仅修改伤害值）

**核心实现逻辑**：
```java
@Override
public int proc(Armor armor, Char attacker, Char defender, int damage) {
    
    testing = true;
    float accuracy = attacker.attackSkill(defender);
    float evasion = defender.defenseSkill(attacker);
    testing = false;

    // 应用各种修正（复制自 hit() 方法）
    if (attacker.buff(Bless.class) != null) accuracy *= 1.25f;
    if (attacker.buff(Hex.class) != null) accuracy *= 0.8f;
    if (attacker.buff(Daze.class) != null) accuracy *= 0.5f;
    // ... 更多修正

    evasion *= genericProcChanceMultiplier(defender);
    
    float hitChance;
    if (evasion >= accuracy){
        hitChance = (accuracy/evasion)/2f;
    } else {
        hitChance = 1f - (evasion/accuracy)/2f;
    }
    
    // 75%的闪避率转化为伤害减免
    hitChance = GameMath.gate(0.25f, (1f + 3f*hitChance)/4f, 1f);
    
    damage = (int)Math.ceil(damage * hitChance);
    
    return damage;
}
```

**边界情况**：
- 使用 GameMath.gate() 确保伤害减免比例在 25%-100% 之间
- 使用 Math.ceil() 确保伤害至少为 1

**伤害减免计算**：
- 如果 evasion >= accuracy：hitChance = (accuracy/evasion)/2（较低命中率）
- 如果 evasion < accuracy：hitChance = 1 - (evasion/accuracy)/2（较高命中率）
- 最终伤害 = damage * gate(0.25, (1 + 3*hitChance)/4, 1)

---

### testingEvasion()

**可见性**：public static

**是否覆写**：否，静态方法

**方法职责**：返回当前是否在测试闪避状态。用于 Armor.evasionFactor() 检查，防止在 proc() 计算闪避时产生循环调用。

**参数**：无

**返回值**：boolean，是否在测试状态

**核心实现逻辑**：
```java
public static boolean testingEvasion(){
    return testing;
}
```

**使用场景**：
```java
// 在 Armor.evasionFactor() 中的检查
if (hasGlyph(Stone.class, owner) && !Stone.testingEvasion()){
    return 0;  // 闪避为 0
}
```

---

### glowing()

**可见性**：public

**是否覆写**：是，覆写自 Armor.Glyph

**方法职责**：返回刻印的视觉发光效果。

**参数**：无

**返回值**：ItemSprite.Glowing，深灰色发光效果对象

**核心实现逻辑**：
```java
@Override
public ItemSprite.Glowing glowing() {
    return GREY;
}
```

## 8. 对外暴露能力

### 显式 API
- `proc(Armor, Char, Char, int)`：刻印效果触发
- `glowing()`：获取视觉效果
- `testingEvasion()`：静态方法，返回测试状态

### 内部辅助方法
无。

### 扩展入口
可覆写 proc() 方法修改伤害减免公式。

## 9. 运行机制与调用链

### 创建时机
- 护甲随机生成时，有约6.67%概率获得此刻印（uncommon 类别）
- 通过 Glyph.randomUncommon() 方法生成

### 调用者
- `Armor.proc()`：在战斗中调用刻印的 proc 方法
- `Armor.evasionFactor()`：检查是否有磐岩刻印

### 被调用者
- `Char.attackSkill()`：获取攻击技能值
- `Char.defenseSkill()`：获取防御技能值
- `GameMath.gate()`：边界值限制

### 系统流程位置
```
攻击 → 闪避检查（磐岩使闪避=0） → 命中 → Armor.proc() → Stone.proc()
                                                              ↓
                                                   计算命中率和伤害减免
                                                              ↓
                                                   返回减免后的伤害
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.armor.glyphs.stone.name | 磐岩%s | 刻印名称 |
| items.armor.glyphs.stone.desc | 这个刻印使用沉重的魔法石包裹了整个铠甲让使用者无法闪避，但会根据原有的闪避能力吸收伤害。 | 刻印描述 |

### 依赖的资源
视觉效果：
- 发光效果：ItemSprite.Glowing（深灰色）

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法
```java
// 检查护甲是否有磐岩刻印
Armor armor = hero.belongings.armor();
if (armor != null && armor.hasGlyph(Stone.class, hero)) {
    // 护甲刻有磐岩刻印，无法闪避但获得伤害减免
}

// 直接创建磐岩刻印并附着
armor.inscribe(new Stone());
```

### 伤害减免计算示例
```java
// 假设攻击者命中率 = 100，防御者闪避率 = 50
// hitChance = 1 - (50/100)/2 = 0.75
// 最终伤害比例 = (1 + 3*0.75)/4 = 0.8125
// 实际伤害 = damage * 0.8125

// 假设攻击者命中率 = 50，防御者闪避率 = 100
// hitChance = (50/100)/2 = 0.25
// 最终伤害比例 = gate(0.25, (1 + 3*0.25)/4, 1) = 0.4375
// 实际伤害 = damage * 0.4375
```

## 12. 开发注意事项

### 状态依赖
刻印本身无状态，伤害减免基于实时计算的命中率和闪避率。

### 生命周期耦合
刻印的生命周期与护甲绑定。

### 常见陷阱
1. **无法闪避**：装备磐岩刻印后闪避为 0，只能通过伤害减免防御
2. **testing 变量**：在计算闪避时必须设置 testing = true，防止循环调用
3. **修正复制**：proc() 中复制了 hit() 方法的修正逻辑，两边需要保持同步
4. **最小伤害**：使用 ceil() 确保伤害至少为 1

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改伤害减免公式
- 添加额外的效果（如：反弹部分伤害）

### 不建议修改的位置
- testing 变量机制是防止循环调用的关键
- 修正逻辑需要与 hit() 方法保持同步

### 重构建议
可将修正逻辑提取为静态方法，供 proc() 和 hit() 共同使用，避免代码重复。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：已覆盖 GREY 常量和 testing 变量
- [x] 是否已覆盖全部方法：已覆盖 proc()、testingEvasion()、glowing()
- [x] 是否已检查继承链与覆写关系：已说明继承 Armor.Glyph
- [x] 是否已核对官方中文翻译：已使用 items_zh.properties 中的"磐岩"
- [x] 是否存在任何推测性表述：无，全部基于源码
- [x] 示例代码是否真实可用：示例代码基于实际 API
- [x] 是否遗漏资源/配置/本地化关联：已列出相关消息键
- [x] 是否明确说明了注意事项与扩展点：已详细说明