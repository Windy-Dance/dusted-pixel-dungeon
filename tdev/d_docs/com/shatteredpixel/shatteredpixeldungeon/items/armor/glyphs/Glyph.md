# Armor.Glyph 抽象类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/Armor.java (内部类) |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.armor |
| **文件类型** | abstract class (内部类) |
| **继承关系** | implements Bundlable |
| **代码行数** | 约126行 (第792-918行) |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
定义护甲刻印（Glyph）的抽象基类，提供刻印效果的统一接口和随机生成机制。刻印是护甲上的魔法附魔效果，在受到攻击时触发特定能力。

### 系统定位
作为护甲系统的核心扩展点，所有具体的刻印实现（有益刻印和诅咒刻印）都继承此类。是护甲与角色战斗系统交互的重要桥梁。

### 不负责什么
- 不负责具体的刻印效果实现（由子类实现）
- 不负责护甲的基础属性计算（由 Armor 类负责）
- 不负责刻印的 UI 显示逻辑

## 3. 结构总览

### 主要成员概览
- **静态常量数组**：common, uncommon, rare, curses - 刻印分类
- **静态常量**：typeChances - 各类刻印的出现概率
- **抽象方法**：proc(), glowing()
- **可覆写方法**：curse(), name(), desc()

### 主要逻辑块概览
- 刻印触发逻辑（proc 方法）
- 刻印命名与描述系统
- 随机刻印生成系统
- 触发概率计算系统

### 生命周期/调用时机
刻印对象在护甲生成时创建，存储于 Armor.glyph 字段中，在战斗中被 Armor.proc() 方法调用。

## 4. 继承与协作关系

### 父类提供的能力
实现 Bundlable 接口，支持序列化/反序列化：
- restoreFromBundle(Bundle)
- storeInBundle(Bundle)

### 覆写的方法
- restoreFromBundle(Bundle)：空实现
- storeInBundle(Bundle)：空实现

### 实现的接口契约
Bundlable 接口要求实现序列化方法，刻印默认为空实现，因为刻印本身通常无状态。

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| Armor | 宿主类，持有刻印实例 |
| Char | 角色（攻击者和防御者） |
| ItemSprite.Glowing | 刻印视觉效果 |
| Bundle | 序列化支持 |
| Reflection | 动态实例化刻印 |
| Random | 随机数生成 |
| Messages | 国际化消息 |
| RingOfArcana | 影响刻印触发概率 |

### 使用者
- Armor 类：管理刻印的附着和触发
- 所有具体刻印子类：继承此类实现具体效果
- 游戏生成系统：通过 random() 方法获取随机刻印

## 5. 字段/常量详解

### 静态常量

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| common | Class<?>[] | {Obfuscation, Swiftness, Viscosity, Potential} | 普通刻印类数组，各占12.5%概率 |
| uncommon | Class<?>[] | {Brimstone, Stone, Entanglement, Repulsion, Camouflage, Flow} | 稀有刻印类数组，各占约6.67%概率 |
| rare | Class<?>[] | {Affection, AntiMagic, Thorns} | 罕见刻印类数组，各占约3.33%概率 |
| typeChances | float[] | {50, 40, 10} | 各类刻印的选择概率权重 |
| curses | Class<?>[] | {AntiEntropy, Corrosion, Displacement, Metabolism, Multiplicity, Stench, Overgrowth, Bulk} | 诅咒刻印类数组 |

### 实例字段
无实例字段（刻印本身无状态存储）

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器。

### 初始化块
无初始化块。

### 初始化注意事项
刻印实例通过 Reflection.newInstance() 动态创建，不通过构造器直接实例化。

## 7. 方法详解

### proc()

**可见性**：public abstract

**是否覆写**：否，这是抽象方法

**方法职责**：在护甲受到攻击时触发刻印效果，这是刻印的核心方法。

**参数**：
- `armor` (Armor)：触发刻印的护甲实例
- `attacker` (Char)：攻击者
- `defender` (Char)：防御者（护甲穿戴者）
- `damage` (int)：原始伤害值

**返回值**：int，处理后的伤害值（可修改伤害）

**前置条件**：护甲已装备且存在刻印。

**副作用**：根据具体刻印实现可能产生各种效果（施加Buff、改变伤害等）。

**核心实现逻辑**：
```java
public abstract int proc( Armor armor, Char attacker, Char defender, int damage );
```

**边界情况**：子类必须实现此方法，返回值决定最终伤害。

---

### procChanceMultiplier()

**可见性**：protected

**是否覆写**：否，但可被子类覆写

**方法职责**：计算刻印触发概率的乘数因子，默认调用通用方法。

**参数**：
- `defender` (Char)：防御者

**返回值**：float，触发概率乘数

**核心实现逻辑**：
```java
protected float procChanceMultiplier( Char defender ){
    return genericProcChanceMultiplier( defender );
}
```

---

### genericProcChanceMultiplier()

**可见性**：public static

**是否覆写**：否，静态方法不可覆写

**方法职责**：计算通用的刻印触发概率乘数，考虑奥术戒指加成和守护光环效果。

**参数**：
- `defender` (Char)：防御者

**返回值**：float，触发概率乘数

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
public static float genericProcChanceMultiplier( Char defender ){
    float multi = RingOfArcana.enchantPowerMultiplier(defender);

    if (Dungeon.hero.alignment == defender.alignment
            && Dungeon.hero.buff(AuraOfProtection.AuraBuff.class) != null
            && (Dungeon.level.distance(defender.pos, Dungeon.hero.pos) <= 2 
                || defender.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null)){
        multi += 0.25f + 0.25f*Dungeon.hero.pointsInTalent(Talent.AURA_OF_PROTECTION);
    }

    return multi;
}
```

**边界情况**：当防御者与英雄不在同一阵营时，仅计算奥术戒指效果。

---

### name() - 无参数版本

**可见性**：public

**是否覆写**：否

**方法职责**：获取刻印的显示名称。

**参数**：无

**返回值**：String，刻印名称

**核心实现逻辑**：
```java
public String name() {
    if (!curse())
        return name( Messages.get(this, "glyph") );
    else
        return name( Messages.get(Item.class, "curse"));
}
```

---

### name() - 带参数版本

**可见性**：public

**是否覆写**：否

**方法职责**：根据护甲名称生成完整的刻印名称。

**参数**：
- `armorName` (String)：护甲名称

**返回值**：String，格式化后的刻印名称

**核心实现逻辑**：
```java
public String name( String armorName ) {
    return Messages.get(this, "name", armorName);
}
```

---

### desc()

**可见性**：public

**是否覆写**：否

**方法职责**：获取刻印的描述文本。

**参数**：无

**返回值**：String，刻印描述

**核心实现逻辑**：
```java
public String desc() {
    return Messages.get(this, "desc");
}
```

---

### curse()

**可见性**：public

**是否覆写**：可覆写

**方法职责**：判断当前刻印是否为诅咒。

**参数**：无

**返回值**：boolean，默认返回 false（非诅咒）

**核心实现逻辑**：
```java
public boolean curse() {
    return false;
}
```

**边界情况**：诅咒刻印子类需覆写此方法返回 true。

---

### restoreFromBundle()

**可见性**：public

**是否覆写**：是，覆写自 Bundlable

**方法职责**：从 Bundle 恢复刻印状态。

**参数**：
- `bundle` (Bundle)：数据存储对象

**返回值**：void

**核心实现逻辑**：
```java
@Override
public void restoreFromBundle( Bundle bundle ) {
    // 空实现，刻印通常无状态
}
```

---

### storeInBundle()

**可见性**：public

**是否覆写**：是，覆写自 Bundlable

**方法职责**：将刻印状态存储到 Bundle。

**参数**：
- `bundle` (Bundle)：数据存储对象

**返回值**：void

**核心实现逻辑**：
```java
@Override
public void storeInBundle( Bundle bundle ) {
    // 空实现，刻印通常无状态
}
```

---

### glowing()

**可见性**：public abstract

**是否覆写**：否，抽象方法

**方法职责**：返回刻印的视觉效果（发光颜色）。

**参数**：无

**返回值**：ItemSprite.Glowing，发光效果对象

**边界情况**：所有具体刻印必须实现此方法。

---

### random()

**可见性**：public static

**是否覆写**：否

**方法职责**：随机生成一个刻印实例，按概率选择普通/稀有/罕见刻印。

**参数**：
- `toIgnore` (Class<? extends Glyph>...)：要排除的刻印类

**返回值**：Glyph，随机生成的刻印实例

**核心实现逻辑**：
```java
@SuppressWarnings("unchecked")
public static Glyph random( Class<? extends Glyph> ... toIgnore ) {
    switch(Random.chances(typeChances)){
        case 0: default:
            return randomCommon( toIgnore );
        case 1:
            return randomUncommon( toIgnore );
        case 2:
            return randomRare( toIgnore );
    }
}
```

---

### randomCommon()

**可见性**：public static

**是否覆写**：否

**方法职责**：从普通刻印池中随机生成一个刻印。

**参数**：
- `toIgnore` (Class<? extends Glyph>...)：要排除的刻印类

**返回值**：Glyph，随机生成的刻印实例

**核心实现逻辑**：
```java
@SuppressWarnings("unchecked")
public static Glyph randomCommon( Class<? extends Glyph> ... toIgnore ){
    ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(common));
    glyphs.removeAll(Arrays.asList(toIgnore));
    if (glyphs.isEmpty()) {
        return random();
    } else {
        return (Glyph) Reflection.newInstance(Random.element(glyphs));
    }
}
```

---

### randomUncommon()

**可见性**：public static

**是否覆写**：否

**方法职责**：从稀有刻印池中随机生成一个刻印。

**参数**：
- `toIgnore` (Class<? extends Glyph>...)：要排除的刻印类

**返回值**：Glyph，随机生成的刻印实例

**核心实现逻辑**：
```java
@SuppressWarnings("unchecked")
public static Glyph randomUncommon( Class<? extends Glyph> ... toIgnore ){
    ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(uncommon));
    glyphs.removeAll(Arrays.asList(toIgnore));
    if (glyphs.isEmpty()) {
        return random();
    } else {
        return (Glyph) Reflection.newInstance(Random.element(glyphs));
    }
}
```

---

### randomRare()

**可见性**：public static

**是否覆写**：否

**方法职责**：从罕见刻印池中随机生成一个刻印。

**参数**：
- `toIgnore` (Class<? extends Glyph>...)：要排除的刻印类

**返回值**：Glyph，随机生成的刻印实例

**核心实现逻辑**：
```java
@SuppressWarnings("unchecked")
public static Glyph randomRare( Class<? extends Glyph> ... toIgnore ){
    ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(rare));
    glyphs.removeAll(Arrays.asList(toIgnore));
    if (glyphs.isEmpty()) {
        return random();
    } else {
        return (Glyph) Reflection.newInstance(Random.element(glyphs));
    }
}
```

---

### randomCurse()

**可见性**：public static

**是否覆写**：否

**方法职责**：从诅咒刻印池中随机生成一个诅咒刻印。

**参数**：
- `toIgnore` (Class<? extends Glyph>...)：要排除的刻印类

**返回值**：Glyph，随机生成的诅咒刻印实例

**核心实现逻辑**：
```java
@SuppressWarnings("unchecked")
public static Glyph randomCurse( Class<? extends Glyph> ... toIgnore ){
    ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(curses));
    glyphs.removeAll(Arrays.asList(toIgnore));
    if (glyphs.isEmpty()) {
        return random();
    } else {
        return (Glyph) Reflection.newInstance(Random.element(glyphs));
    }
}
```

## 8. 对外暴露能力

### 显式 API
- `proc(Armor, Char, Char, int)`：刻印效果触发（抽象，子类实现）
- `glowing()`：获取视觉效果（抽象，子类实现）
- `name()` / `name(String)`：获取刻印名称
- `desc()`：获取刻印描述
- `curse()`：判断是否为诅咒
- `genericProcChanceMultiplier(Char)`：计算触发概率乘数

### 内部辅助方法
- `procChanceMultiplier(Char)`：子类可覆写的概率乘数计算

### 扩展入口
- `proc()`：必须覆写，实现具体刻印效果
- `glowing()`：必须覆写，返回视觉发光效果
- `curse()`：可覆写，诅咒刻印返回 true
- `procChanceMultiplier()`：可覆写，自定义触发概率

## 9. 运行机制与调用链

### 创建时机
- 护甲随机生成时（Armor.random()）
- 玩家使用升级刻印时（Armor.upgrade(true)）
- 炼金附魔时

### 调用者
- `Armor.proc()`：在战斗中调用刻印的 proc 方法
- `Armor.name()`：获取刻印名称
- `Armor.glowing()`：获取刻印视觉效果
- `Armor.inscribe()`：管理刻印附着

### 被调用者
- `Char`：刻印效果的目标
- `Buff`：刻印施加的状态效果
- `Messages`：国际化文本

### 系统流程位置
```
战斗开始 → 攻击计算 → Armor.proc() → Glyph.proc() → 效果生效
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.armor.armor$glyph.glyph | 刻印 | 刻印基础名称 |
| items.armor.armor$glyph.killed | %s杀死了你... | 死亡消息 |
| items.armor.armor$glyph.rankings_desc | 死于刻印 | 排行榜描述 |
| items.armor.armor$glyph.discover_hint | 你可在地牢中概率找到带有该效果的物品，或尝试自行使物品获得该效果。 | 发现提示 |
| items.item.curse | 诅咒 | 诅咒刻印名称后缀 |

### 依赖的资源
无纹理/图标/音效资源，视觉效果由 ItemSprite.Glowing 提供。

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法
```java
// 创建随机刻印并附着到护甲
Armor armor = new PlateArmor();
armor.inscribe(Glyph.random());

// 检查护甲是否有特定刻印
if (armor.hasGlyph(AntiMagic.class, hero)) {
    // 护甲有敌法刻印
}

// 检查是否为诅咒刻印
if (armor.glyph != null && armor.glyph.curse()) {
    // 这是一个诅咒刻印
}
```

### 扩展示例
```java
// 创建自定义刻印
public class MyGlyph extends Armor.Glyph {
    private static ItemSprite.Glowing CYAN = new ItemSprite.Glowing(0x00FFFF);
    
    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        // 实现具体效果
        if (Random.Float() < 0.2f * procChanceMultiplier(defender)) {
            // 20%概率触发效果
            Buff.affect(defender, MyBuff.class);
        }
        return damage;
    }
    
    @Override
    public ItemSprite.Glowing glowing() {
        return CYAN;
    }
}
```

## 12. 开发注意事项

### 状态依赖
刻印本身通常无状态，所有状态应存储在子类的 Buff 或其他结构中。

### 生命周期耦合
刻印的生命周期与护甲绑定，护甲销毁时刻印随之销毁。

### 常见陷阱
1. **proc 方法必须返回值**：即使不修改伤害，也必须返回传入的 damage 参数
2. **触发概率计算**：应使用 procChanceMultiplier() 而非固定概率
3. **诅咒判断**：诅咒刻印必须覆写 curse() 方法返回 true
4. **序列化**：如果刻印有状态，必须覆写 storeInBundle/restoreFromBundle

## 13. 修改建议与扩展点

### 适合扩展的位置
- 创建新的刻印子类
- 覆写 procChanceMultiplier() 自定义触发概率
- 覆写 curse() 创建新的诅咒刻印

### 不建议修改的位置
- common/uncommon/rare/curses 数组：影响游戏平衡
- typeChances 概率数组：影响刻印稀有度分布

### 重构建议
无，当前设计合理，抽象程度适当。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：已覆盖 common, uncommon, rare, curses, typeChances
- [x] 是否已覆盖全部方法：已覆盖所有公开/抽象方法
- [x] 是否已检查继承链与覆写关系：已说明 Bundlable 接口实现
- [x] 是否已核对官方中文翻译：已使用 items_zh.properties 中的官方翻译
- [x] 是否存在任何推测性表述：无，全部基于源码
- [x] 示例代码是否真实可用：示例代码基于实际 API
- [x] 是否遗漏资源/配置/本地化关联：已列出相关消息键
- [x] 是否明确说明了注意事项与扩展点：已详细说明