# AntiMagic 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/glyphs/AntiMagic.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs |
| **文件类型** | class |
| **继承关系** | extends Armor.Glyph |
| **代码行数** | 157行 |
| **所属模块** | core |
| **稀有度** | rare（罕见） |

## 2. 文件职责说明

### 核心职责
实现"敌法"刻印效果，使护甲能够抵御魔法伤害。该刻印提供两种保护机制：完全免疫特定类型的魔法伤害，以及提供额外的魔法伤害减免。

### 系统定位
作为罕见的防御型刻印，专门针对魔法伤害提供防护，是面对法术攻击时的强力选择。与物理防御形成互补。

### 不负责什么
- 不负责物理伤害减免（由护甲基础属性负责）
- 不负责具体的伤害计算逻辑（由 Char.damage() 负责）

## 3. 结构总览

### 主要成员概览
- **RESISTS**：静态常量集合，存储所有可抵抗的伤害类型
- **TEAL**：静态常量，青色发光效果
- **proc()**：核心方法（空实现，效果在别处触发）
- **drRoll()**：静态方法，计算魔法伤害减免
- **glowing()**：返回视觉效果

### 主要逻辑块概览
- 静态初始化块：填充 RESISTS 集合
- 魔法伤害减免计算

### 生命周期/调用时机
刻印在护甲生成时创建，效果在 Char.damage() 中通过检查 RESISTS 集合触发。

## 4. 继承与协作关系

### 父类提供的能力
- `proc(Armor, Char, Char, int)`：抽象方法
- `glowing()`：抽象方法
- `genericProcChanceMultiplier(Char)`：触发概率乘数计算

### 覆写的方法
| 方法 | 说明 |
|------|------|
| proc() | 空实现，效果在 Char.damage() 中触发 |
| glowing() | 返回青色发光效果 |

### 实现的接口契约
继承自 Armor.Glyph 的抽象接口。

### 依赖的关键类

#### 状态效果类
| 类名 | 用途 |
|------|------|
| MagicalSleep | 魔法睡眠状态 |
| Charm | 魅惑状态 |
| Weakness | 虚弱状态 |
| Vulnerable | 易伤状态 |
| Hex | 诅咒状态 |
| Degrade | 降级状态 |

#### 陷阱类
| 类名 | 用途 |
|------|------|
| DisintegrationTrap | 分解陷阱 |
| GrimTrap | 死亡陷阱 |

#### 炸弹类
| 类名 | 用途 |
|------|------|
| ArcaneBomb | 奥术炸弹 |
| HolyBomb.HolyDamage | 神圣炸弹伤害 |

#### 卷轴类
| 类名 | 用途 |
|------|------|
| ScrollOfRetribution | 惩戒卷轴 |
| ScrollOfPsionicBlast | 心灵爆破卷轴 |
| ScrollOfTeleportation | 传送卷轴 |

#### 法杖类
| 类名 | 用途 |
|------|------|
| WandOfBlastWave | 冲击波法杖 |
| WandOfDisintegration | 分解法杖 |
| WandOfFireblast | 烈焰法杖 |
| WandOfFrost | 冰霜法杖 |
| WandOfLightning | 闪电法杖 |
| WandOfLivingEarth | 大地法杖 |
| WandOfMagicMissile | 魔法飞弹法杖 |
| WandOfPrismaticLight | 棱光法杖 |
| WandOfTransfusion | 输血法杖 |
| WandOfWarding.Ward | 守卫法杖 |

#### 怪物攻击类
| 类名 | 用途 |
|------|------|
| DM100.LightningBolt | DM-100闪电攻击 |
| Shaman.EarthenBolt | 萨满土石攻击 |
| CrystalWisp.LightBeam | 水晶精灵光束 |
| Warlock.DarkBolt | 术士暗影攻击 |
| Eye.DeathGaze | 邪眼死亡凝视 |
| YogFist.BrightFist.LightBeam | Yog光拳光束 |
| YogFist.DarkFist.DarkBolt | Yog暗拳暗影攻击 |

### 使用者
- `Char.damage()`：检查 RESISTS 集合判断是否免疫伤害
- `Armor`：管理刻印附着

## 5. 字段/常量详解

### 静态常量

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| TEAL | ItemSprite.Glowing | new ItemSprite.Glowing(0x88EEFF) | 青色发光效果 |
| RESISTS | HashSet<Class> | 包含50+种魔法伤害类型的集合 | 可被敌法刻印抵抗的伤害类型集合 |

### RESISTS 集合内容

集合在静态初始化块中填充，包含以下类别：

#### 负面状态效果（6种）
- MagicalSleep、Charm、Weakness、Vulnerable、Hex、Degrade

#### 陷阱伤害（2种）
- DisintegrationTrap、GrimTrap

#### 炸弹伤害（3种）
- ArcaneBomb、HolyBomb.HolyDamage、HolyDart

#### 卷轴效果（3种）
- ScrollOfRetribution、ScrollOfPsionicBlast、ScrollOfTeleportation

#### 牧师法术（6种）
- GuidingLight、HolyWeapon、Sunray、HolyLance、Smite、Judgement

#### 法杖效果（11种）
- CursedWand、WandOfBlastWave、WandOfDisintegration、WandOfFireblast、WandOfFrost、WandOfLightning、WandOfLivingEarth、WandOfMagicMissile、WandOfPrismaticLight、WandOfTransfusion、WandOfWarding.Ward

#### 其他（8种）
- ChaliceOfBlood、ElementalBlast、ElementalStrike、Blazing、Shocking、Grim、WarpBeacon

#### 怪物攻击（6种）
- DM100.LightningBolt、Shaman.EarthenBolt、CrystalWisp.LightBeam、Warlock.DarkBolt、Eye.DeathGaze、YogFist 相关攻击

### 实例字段
无实例字段。

## 6. 构造与初始化机制

### 构造器
使用默认构造器，无显式构造器定义。

### 初始化块
静态初始化块填充 RESISTS 集合：
```java
static {
    RESISTS.add( MagicalSleep.class );
    RESISTS.add( Charm.class );
    // ... 添加所有可抵抗的类型
}
```

### 初始化注意事项
RESISTS 集合在类加载时初始化，之后不可修改。

## 7. 方法详解

### proc()

**可见性**：public

**是否覆写**：是，覆写自 Armor.Glyph

**方法职责**：空实现。敌法刻印的效果不在 proc() 中触发，而是在 Char.damage() 方法中通过检查 RESISTS 集合来实现。

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
    //no proc effect, triggers in Char.damage
    return damage;
}
```

**边界情况**：此刻印不通过 proc() 方法生效。

---

### drRoll()

**可见性**：public static

**是否覆写**：否，静态方法

**方法职责**：计算魔法伤害减免值（Damage Reduction）。这是敌法刻印提供的额外防护，与完全免疫不同。

**参数**：
- `owner` (Char)：护甲穿戴者
- `level` (int)：护甲等级

**返回值**：int，魔法伤害减免值

**前置条件**：护甲已装备且有敌法刻印。

**副作用**：无

**核心实现逻辑**：
```java
public static int drRoll( Char owner, int level ){
    if (level == -1){
        return 0;
    } else {
        return Random.NormalIntRange(
                Math.round(level * genericProcChanceMultiplier(owner)),
                Math.round((3 + (level * 1.5f)) * genericProcChanceMultiplier(owner)));
    }
}
```

**边界情况**：
- 当 level == -1 时返回 0（表示无效刻印）
- 伤害减免值为随机值，范围随等级增加

**调用位置**：从源码推测，此方法在 Char.damage() 或相关伤害计算中被调用。

---

### glowing()

**可见性**：public

**是否覆写**：是，覆写自 Armor.Glyph

**方法职责**：返回刻印的视觉发光效果。

**参数**：无

**返回值**：ItemSprite.Glowing，青色发光效果对象

**核心实现逻辑**：
```java
@Override
public ItemSprite.Glowing glowing() {
    return TEAL;
}
```

## 8. 对外暴露能力

### 显式 API
- `proc(Armor, Char, Char, int)`：刻印效果触发（空实现）
- `glowing()`：获取视觉效果
- `drRoll(Char, int)`：静态方法，计算魔法伤害减免
- `RESISTS`：静态集合，可被检查的抵抗类型

### 内部辅助方法
无。

### 扩展入口
可通过修改 RESISTS 集合（需在静态初始化块中）添加新的可抵抗类型。

## 9. 运行机制与调用链

### 创建时机
- 护甲随机生成时，有约3.33%概率获得此刻印（rare 类别）
- 通过 Glyph.randomRare() 方法生成

### 调用者
- `Char.damage()`：检查 RESISTS 判断是否免疫魔法伤害
- 伤害计算系统：调用 drRoll() 获取魔法伤害减免

### 系统流程位置
```
魔法攻击 → Char.damage() → 检查 RESISTS 集合 → 完全免疫或减免伤害
                                      ↓
                              drRoll() 计算减免值
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.armor.glyphs.antimagic.name | 敌法%s | 刻印名称 |
| items.armor.glyphs.antimagic.desc | 这个强力的刻印能使护甲在防御物理伤害的同时抵御魔法伤害。这种魔法防御不受护甲等阶影响。 | 刻印描述 |

### 依赖的资源
视觉效果：
- 发光效果：ItemSprite.Glowing（青色）

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法
```java
// 检查护甲是否有敌法刻印
Armor armor = hero.belongings.armor();
if (armor != null && armor.hasGlyph(AntiMagic.class, hero)) {
    // 护甲刻有敌法刻印，可抵御魔法伤害
}

// 计算魔法伤害减免
int level = armor.buffedLvl();
int dr = AntiMagic.drRoll(hero, level);
// dr 为随机减免值
```

### 检查特定伤害类型是否可被抵抗
```java
// 检查闪电伤害是否可被抵抗
if (AntiMagic.RESISTS.contains(WandOfLightning.class)) {
    // 闪电伤害可被敌法刻印抵抗
}
```

## 12. 开发注意事项

### 状态依赖
刻印本身无状态，所有状态由 RESISTS 静态集合管理。

### 生命周期耦合
刻印的生命周期与护甲绑定。RESISTS 集合在类加载时初始化。

### 常见陷阱
1. **proc() 不生效**：敌法刻印的 proc() 是空实现，效果通过 RESISTS 集合在 Char.damage() 中触发
2. **伤害减免计算**：drRoll() 返回随机值，不是固定减免
3. **等级 -1**：当护甲被诅咒或无效时，level 可能为 -1，此时 drRoll() 返回 0

## 13. 修改建议与扩展点

### 适合扩展的位置
- 在静态初始化块中向 RESISTS 添加新的伤害类型
- 覆写 drRoll() 修改伤害减免公式

### 不建议修改的位置
- RESISTS 集合的访问权限（应为 public final 以便外部检查）

### 重构建议
可考虑将 RESISTS 的初始化拆分为多个语义化的方法，提高可读性。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：已覆盖 TEAL、RESISTS
- [x] 是否已覆盖全部方法：已覆盖 proc()、drRoll()、glowing()
- [x] 是否已检查继承链与覆写关系：已说明继承 Armor.Glyph
- [x] 是否已核对官方中文翻译：已使用 items_zh.properties 中的"敌法"
- [x] 是否存在任何推测性表述：drRoll() 调用位置基于源码注释推测
- [x] 示例代码是否真实可用：示例代码基于实际 API
- [x] 是否遗漏资源/配置/本地化关联：已列出相关消息键
- [x] 是否明确说明了注意事项与扩展点：已详细说明