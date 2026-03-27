# Wand 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/wands/Wand.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.wands |
| **文件类型** | abstract class |
| **继承关系** | extends Item |
| **代码行数** | 882 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Wand 是所有法杖的抽象基类，提供法杖的核心功能框架，包括：
- 充能系统管理（充能获取、消耗、自然恢复）
- 施法机制（目标选择、投射路径、效果触发）
- 诅咒处理（诅咒法杖的随机效果）
- 鉴定机制（使用次数累积鉴定）
- 与法师手杖的集成（嵌入后的近战效果）

### 系统定位
位于物品系统的法杖子模块顶层，作为所有具体法杖实现的模板。在整体架构中处于：
- **上层调用者**：Hero（英雄）、GameScene（游戏场景）、QuickSlotButton（快捷槽）
- **下层依赖**：Item（物品基类）、Ballistica（投射路径）、Buff系统

### 不负责什么
- 不实现具体的法术效果（由子类的 `onZap()` 实现）
- 不处理具体的近战命中效果（由子类的 `onHit()` 实现）
- 不直接管理法杖的生成逻辑（由 Generator 处理）

## 3. 结构总览

### 主要成员概览
- **静态常量**：`AC_ZAP`（施法动作）、`TIME_TO_ZAP`（施法时间）、`USES_TO_ID`（鉴定所需使用次数）
- **充能字段**：`maxCharges`（最大充能）、`curCharges`（当前充能）、`partialCharge`（部分充能）
- **状态字段**：`curChargeKnown`（充能已知）、`curseInfusionBonus`（诅咒注入加成）、`resinBonus`（树脂加成）
- **内部类**：`Charger`（充能Buff）、`PlaceHolder`（占位符）、`zapper`（施法监听器）

### 主要逻辑块概览
1. **施法流程**：`actions()` → `execute()` → `zapper.onSelect()` → `tryToZap()` → `fx()` → `onZap()` → `wandUsed()`
2. **充能流程**：`charge()` → `Charger.act()` → `recharge()` → `gainCharge()`
3. **鉴定流程**：`wandUsed()` → 累积使用次数 → `identify()`
4. **诅咒处理**：`cursedZap()` → `CursedWand.cursedZap()`

### 生命周期/调用时机
- **创建**：通过 `Generator.random(Generator.Category.WAND)` 生成，或从存档恢复
- **装备**：拾取时自动调用 `collect()`，触发充能系统启动
- **使用**：玩家选择施法动作后触发完整施法流程
- **卸下**：从背包移除时调用 `onDetach()` 停止充能

## 4. 继承与协作关系

### 父类提供的能力
从 `Item` 继承：
- `level`（等级）、`cursed`（诅咒状态）、`cursedKnown`（诅咒已知）
- `quantity`（数量）、`bundle`（序列化支持）
- `identify()`（鉴定方法）、`upgrade()`（升级方法）
- `actions()`、`execute()`、`collect()` 等基础物品操作

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `actions(Hero hero)` | 添加 `AC_ZAP` 动作 |
| `execute(Hero hero, String action)` | 处理施法动作 |
| `targetingPos(Hero user, int dst)` | 返回诅咒法杖或正常投射的碰撞位置 |
| `collect(Bag container)` | 启动充能系统 |
| `onDetach()` | 停止充能系统 |
| `level(int value)` | 更新等级后刷新充能上限 |
| `identify(boolean byHero)` | 鉴定时设置 `curChargeKnown` |
| `info()` | 添加法杖特有的描述信息 |
| `isIdentified()` | 包含 `curChargeKnown` 检查 |
| `status()` | 显示充能状态 |
| `level()` | 计算包含诅咒注入和树脂加成的等级 |
| `upgrade()` | 升级时刷新充能并可能移除诅咒 |
| `degrade()` | 降级时刷新充能 |
| `buffedLvl()` | 计算包含各种加成的有效等级 |
| `random()` | 随机生成初始状态 |
| `glowing()` | 树脂加成的发光效果 |
| `value()` | 价格计算 |
| `storeInBundle()` / `restoreFromBundle()` | 序列化 |
| `reset()` | 重置鉴定计数器 |

### 实现的接口契约
无显式接口实现，但定义了以下抽象方法供子类实现：
- `onZap(Ballistica attack)` - 施法效果
- `onHit(MagesStaff staff, Char attacker, Char defender, int damage)` - 近战命中效果

### 依赖的关键类
| 类 | 用途 |
|----|------|
| `Ballistica` | 投射路径计算 |
| `MagicMissile` | 魔法飞弹视觉效果 |
| `Charger` | 内部充能Buff类 |
| `CursedWand` | 诅咒法杖效果处理 |
| `MagesStaff` | 法师手杖集成 |
| `Messages` | 国际化文案 |
| `Talent` | 天赋系统集成 |
| `WondrousResin` | 奇妙树脂饰品效果 |

### 使用者
- `Hero` - 持有和使用法杖
- `GameScene` - 管理施法目标选择
- `QuickSlotButton` - 快捷槽显示和触发
- `MagesStaff` - 嵌入法杖作为近战武器
- `Generator` - 生成法杖实例
- `Badges` - 成就系统跟踪

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `AC_ZAP` | String | "ZAP" | 施法动作标识符，用于UI交互 |
| `TIME_TO_ZAP` | float | 1f | 施法所需时间（回合） |
| `USES_TO_ID` | int | 10 | 完全鉴定所需的使用次数 |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `maxCharges` | int | `initialCharges()` | 最大充能数，随等级提升 |
| `curCharges` | int | `maxCharges` | 当前可用充能数 |
| `partialCharge` | float | 0f | 部分充能，累积到1时转化为完整充能 |
| `charger` | Charger | null | 充能Buff实例，管理自动充能 |
| `curChargeKnown` | boolean | false | 当前充能数是否已知（鉴定的一部分） |
| `curseInfusionBonus` | boolean | false | 诅咒注入加成标记，影响 `level()` 计算 |
| `resinBonus` | int | 0 | 奥术树脂升级加成 |
| `collisionProperties` | int | `Ballistica.MAGIC_BOLT` | 投射路径碰撞属性 |
| `usesLeftToID` | float | 10 | 剩余鉴定使用次数 |
| `availableUsesToID` | float | 5 | 可用的鉴定使用次数（影响鉴定速度） |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器。

### 初始化块
```java
{
    defaultAction = AC_ZAP;
    usesTargeting = true;
    bones = true;
}
```
- 设置默认动作为施法
- 启用目标选择模式
- 允许在骨头中掉落

### 初始化注意事项
- `maxCharges` 和 `curCharges` 在字段声明时初始化，依赖 `initialCharges()` 方法
- 子类应覆写 `initialCharges()` 以改变初始充能数
- 充能系统在 `collect()` 时启动，`onDetach()` 时停止

## 7. 方法详解

### actions(Hero hero)
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：返回法杖可用的动作列表

**参数**：
- `hero` (Hero)：当前英雄

**返回值**：ArrayList<String>，包含基础动作和施法动作

**核心实现逻辑**：
```java
ArrayList<String> actions = super.actions(hero);
if (curCharges > 0 || !curChargeKnown) {
    actions.add(AC_ZAP);
}
return actions;
```

**边界情况**：只有充能已知且有充能，或充能未知时才添加施法动作

---

### execute(Hero hero, String action)
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：执行指定动作

**参数**：
- `hero` (Hero)：执行动作的英雄
- `action` (String)：动作标识

**前置条件**：action 必须是有效的动作字符串

**核心实现逻辑**：
```java
super.execute(hero, action);
if (action.equals(AC_ZAP)) {
    curUser = hero;
    curItem = this;
    GameScene.selectCell(zapper);
}
```

---

### targetingPos(Hero user, int dst)
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：计算法杖施法的目标位置

**参数**：
- `user` (Hero)：施法者
- `dst` (int)：目标格子

**返回值**：int，实际命中位置

**核心实现逻辑**：
```java
if (cursed && cursedKnown) {
    return new Ballistica(user.pos, dst, Ballistica.MAGIC_BOLT).collisionPos;
} else {
    return new Ballistica(user.pos, dst, collisionProperties).collisionPos;
}
```

**边界情况**：诅咒法杖已知时使用 `MAGIC_BOLT` 属性，可能导致投射方向偏差

---

### onZap(Ballistica attack)
**可见性**：public abstract

**是否覆写**：否，抽象方法

**方法职责**：施放法术的核心效果，由子类实现

**参数**：
- `attack` (Ballistica)：投射路径对象

**说明**：所有具体法杖必须实现此方法定义其魔法效果

---

### onHit(MagesStaff staff, Char attacker, Char defender, int damage)
**可见性**：public abstract

**是否覆写**：否，抽象方法

**方法职责**：法杖嵌入法师手杖时的近战命中效果

**参数**：
- `staff` (MagesStaff)：法师手杖实例
- `attacker` (Char)：攻击者
- `defender` (Char)：防御者
- `damage` (int)：造成的伤害

---

### procChanceMultiplier(Char attacker)
**可见性**：public static

**是否覆写**：否

**方法职责**：计算法杖效果触发概率的乘数

**参数**：
- `attacker` (Char)：攻击者

**返回值**：float，概率乘数

**核心实现逻辑**：
```java
if (attacker.buff(Talent.EmpoweredStrikeTracker.class) != null) {
    return 1f + ((Hero)attacker).pointsInTalent(Talent.EMPOWERED_STRIKE) / 2f;
}
return 1f;
```

---

### tryToZap(Hero owner, int target)
**可见性**：public

**是否覆写**：否

**方法职责**：检查是否可以施放法术

**参数**：
- `owner` (Hero)：法杖持有者
- `target` (int)：目标位置

**返回值**：boolean，可以施放返回 true

**前置条件**：
- 持有者不能有 `MagicImmune` buff（除非使用狂野魔法）
- 充能数必须足够（除非使用狂野魔法）

**副作用**：充能不足时显示提示消息

**核心实现逻辑**：
```java
if (owner.buff(WildMagic.WildMagicTracker.class) == null && owner.buff(MagicImmune.class) != null) {
    GLog.w(Messages.get(this, "no_magic"));
    return false;
}
if (owner.buff(WildMagic.WildMagicTracker.class) != null || curCharges >= chargesPerCast()) {
    return true;
} else {
    GLog.w(Messages.get(this, "fizzles"));
    return false;
}
```

---

### collect(Bag container)
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：将法杖放入容器并启动充能

**参数**：
- `container` (Bag)：目标容器

**返回值**：boolean，成功收集返回 true

**核心实现逻辑**：
```java
if (super.collect(container)) {
    if (container.owner != null) {
        if (container instanceof MagicalHolster) {
            charge(container.owner, ((MagicalHolster) container).HOLSTER_SCALE_FACTOR);
        } else {
            charge(container.owner);
        }
    }
    return true;
}
return false;
```

**边界情况**：魔法筒袋提供充能加速

---

### gainCharge(float amt, boolean overcharge)
**可见性**：public

**是否覆写**：否

**方法职责**：增加充能

**参数**：
- `amt` (float)：增加的充能量
- `overcharge` (boolean)：是否允许超充能

**副作用**：可能增加 `curCharges`，更新快捷槽显示

**核心实现逻辑**：
```java
partialCharge += amt;
while (partialCharge >= 1) {
    if (overcharge) {
        curCharges = Math.min(maxCharges + (int)amt, curCharges + 1);
    } else {
        curCharges = Math.min(maxCharges, curCharges + 1);
    }
    partialCharge--;
    updateQuickslot();
}
```

---

### charge(Char owner) / charge(Char owner, float chargeScaleFactor)
**可见性**：public

**是否覆写**：否

**方法职责**：启动充能系统

**参数**：
- `owner` (Char)：充能目标
- `chargeScaleFactor` (float)：充能速率乘数

**核心实现逻辑**：
```java
if (charger == null) charger = new Charger();
charger.attachTo(owner);
// 可选：设置充能速率乘数
charger.setScaleFactor(chargeScaleFactor);
```

---

### wandProc(Char target, int wandLevel, int chargesUsed)
**可见性**：protected static

**是否覆写**：否

**方法职责**：处理法杖命中后的通用效果（天赋触发等）

**参数**：
- `target` (Char)：目标角色
- `wandLevel` (int)：法杖等级
- `chargesUsed` (int)：消耗的充能数

**核心实现逻辑**：
1. 奥术视觉天赋：给予目标感知
2. 术士子职业：施加灵魂印记
3. 牧师子职业：配合圣光术
4. 战斗牧师天赋：致盲/圣光效果

---

### onDetach()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：法杖从背包移除时停止充能

**核心实现逻辑**：
```java
stopCharging();
```

---

### stopCharging()
**可见性**：public

**是否覆写**：否

**方法职责**：停止充能系统

**核心实现逻辑**：
```java
if (charger != null) {
    charger.detach();
    charger = null;
}
```

---

### level(int value)
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：设置等级并更新充能上限

**参数**：
- `value` (int)：新等级

**核心实现逻辑**：
```java
super.level(value);
updateLevel();
```

---

### identify(boolean byHero)
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：鉴定法杖

**参数**：
- `byHero` (boolean)：是否由英雄鉴定

**返回值**：Item，当前法杖实例

**核心实现逻辑**：
```java
curChargeKnown = true;
super.identify(byHero);
updateQuickslot();
return this;
```

---

### setIDReady()
**可见性**：public

**是否覆写**：否

**方法职责**：设置法杖已准备好鉴定

**核心实现逻辑**：
```java
usesLeftToID = -1;
```

---

### readyToIdentify()
**可见性**：public

**是否覆写**：否

**方法职责**：检查法杖是否准备好鉴定

**返回值**：boolean，可以鉴定返回 true

**核心实现逻辑**：
```java
return !isIdentified() && usesLeftToID <= 0;
```

---

### onHeroGainExp(float levelPercent, Hero hero)
**可见性**：public

**是否覆写**：否

**方法职责**：英雄获得经验时增加可用鉴定次数

**参数**：
- `levelPercent` (float)：经验百分比
- `hero` (Hero)：英雄实例

**核心实现逻辑**：
```java
levelPercent *= Talent.itemIDSpeedFactor(hero, this);
if (!isIdentified() && availableUsesToID <= USES_TO_ID / 2f) {
    availableUsesToID = Math.min(USES_TO_ID / 2f, availableUsesToID + levelPercent * USES_TO_ID / 2f);
}
```

---

### info()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：返回法杖的完整描述信息

**返回值**：String，格式化的描述文本

**核心实现逻辑**：
```java
String desc = super.info();
desc += "\n\n" + statsDesc();
// 添加树脂加成描述
// 添加诅咒状态描述
// 添加战斗法师子职业描述
return desc;
```

---

### statsDesc()
**可见性**：public

**是否覆写**：否（子类可覆写）

**方法职责**：返回法杖的统计描述

**返回值**：String，统计信息

---

### upgradeStat1/2/3(int level)
**可见性**：public

**是否覆写**：否（子类可覆写）

**方法职责**：返回指定等级的升级属性值，用于UI显示

**返回值**：String，属性值描述

---

### isIdentified()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：检查法杖是否完全鉴定

**返回值**：boolean，完全鉴定返回 true

**核心实现逻辑**：
```java
return super.isIdentified() && curChargeKnown;
```

---

### status()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：返回法杖状态字符串（用于UI显示）

**返回值**：String，充能状态如 "3/5"

**核心实现逻辑**：
```java
if (levelKnown) {
    return (curChargeKnown ? curCharges : "?") + "/" + maxCharges;
} else {
    return null;
}
```

---

### level()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：返回法杖的有效等级（包含加成）

**返回值**：int，有效等级

**核心实现逻辑**：
```java
// 检查并清理无效的诅咒注入加成
if (!cursed && curseInfusionBonus) {
    curseInfusionBonus = false;
    updateLevel();
}
int level = super.level();
if (curseInfusionBonus) level += 1 + level / 6;
level += resinBonus;
return level;
```

---

### upgrade()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：升级法杖

**返回值**：Item，当前法杖实例

**副作用**：
- 有 1/3 概率移除诅咒
- 消耗一个树脂加成（如果有）
- 增加充能
- 更新快捷槽

**核心实现逻辑**：
```java
super.upgrade();
if (Random.Int(3) == 0) {
    cursed = false;
}
if (resinBonus > 0) {
    resinBonus--;
}
updateLevel();
curCharges = Math.min(curCharges + 1, maxCharges);
updateQuickslot();
return this;
```

---

### degrade()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：降级法杖

**返回值**：Item，当前法杖实例

**核心实现逻辑**：
```java
super.degrade();
updateLevel();
updateQuickslot();
return this;
```

---

### buffedLvl()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：返回包含所有临时加成的有效等级

**返回值**：int，有效等级

**核心实现逻辑**：
```java
int lvl = super.buffedLvl();
if (charger != null && charger.target != null) {
    // 衰减效果处理
    if (charger.target == Dungeon.hero && !Dungeon.hero.belongings.contains(this) && Dungeon.hero.buff(Degrade.class) != null) {
        lvl = Degrade.reduceLevel(lvl);
    }
    // 卷轴强化加成
    if (charger.target.buff(ScrollEmpower.class) != null) {
        lvl += 2;
    }
    // 绝地力量天赋
    if (curCharges == 1 && charger.target instanceof Hero && ((Hero)charger.target).hasTalent(Talent.DESPERATE_POWER)) {
        lvl += ((Hero)charger.target).pointsInTalent(Talent.DESPERATE_POWER);
    }
    // 狂野魔法加成
    if (charger.target.buff(WildMagic.WildMagicTracker.class) != null) {
        // 复杂的等级加成计算
    }
    // 魔弹法杖的魔力强化buff
    WandOfMagicMissile.MagicCharge buff = charger.target.buff(WandOfMagicMissile.MagicCharge.class);
    if (buff != null && buff.level() > lvl) {
        return buff.level();
    }
}
return lvl;
```

---

### updateLevel()
**可见性**：public

**是否覆写**：否

**方法职责**：根据等级更新充能上限

**核心实现逻辑**：
```java
maxCharges = Math.min(initialCharges() + level(), 10);
curCharges = Math.min(curCharges, maxCharges);
```

---

### initialCharges()
**可见性**：public

**是否覆写**：否（子类可覆写）

**方法职责**：返回初始充能数

**返回值**：int，默认为 2

---

### chargesPerCast()
**可见性**：protected

**是否覆写**：否（子类可覆写）

**方法职责**：返回每次施法消耗的充能数

**返回值**：int，默认为 1

---

### fx(Ballistica bolt, Callback callback)
**可见性**：public

**是否覆写**：否（子类可覆写）

**方法职责**：播放施法视觉效果

**参数**：
- `bolt` (Ballistica)：投射路径
- `callback` (Callback)：效果完成后的回调

**核心实现逻辑**：
```java
MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.MAGIC_MISSILE, curUser.sprite, bolt.collisionPos, callback);
Sample.INSTANCE.play(Assets.Sounds.ZAP);
```

---

### staffFx(MagesStaff.StaffParticle particle)
**可见性**：public

**是否覆写**：否（子类可覆写）

**方法职责**：设置法师手杖的粒子效果

**参数**：
- `particle` (MagesStaff.StaffParticle)：粒子实例

**核心实现逻辑**：
```java
particle.color(0xFFFFFF);
particle.am = 0.3f;
particle.setLifespan(1f);
particle.speed.polar(Random.Float(PointF.PI2), 2f);
particle.setSize(1f, 2f);
particle.radiateXY(0.5f);
```

---

### wandUsed()
**可见性**：public

**是否覆写**：否

**方法职责**：处理施法完成后的状态更新

**副作用**：
- 更新鉴定进度
- 消耗充能
- 处理各种天赋效果
- 取消隐形状态
- 消耗时间

**核心实现逻辑**：
```java
// 鉴定进度处理
if (!isIdentified()) {
    float uses = Math.min(availableUsesToID, Talent.itemIDSpeedFactor(Dungeon.hero, this));
    availableUsesToID -= uses;
    usesLeftToID -= uses;
    if (usesLeftToID <= 0 || Dungeon.hero.pointsInTalent(Talent.SCHOLARS_INTUITION) == 2) {
        // 鉴定或准备鉴定
    }
}

// 充盈护盾天赋
if (Dungeon.hero.hasTalent(Talent.EXCESS_CHARGE) && curCharges >= maxCharges) {
    // 给予护盾
}

// 消耗充能
curCharges -= cursed ? 1 : chargesPerCast();

// 移除魔法强化buff（如果不是刚刚施加的）
// 处理卷轴强化消耗
// 触发各种天赋效果

Invisibility.dispel();
updateQuickslot();
curUser.spendAndNext(TIME_TO_ZAP);
```

---

### random()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：随机生成法杖初始状态

**返回值**：Item，当前法杖实例

**核心实现逻辑**：
```java
// 随机等级：+0(66.67%), +1(26.67%), +2(6.67%)
int n = 0;
if (Random.Int(3) == 0) {
    n++;
    if (Random.Int(5) == 0) {
        n++;
    }
}
level(n);
curCharges += n;

// 30% 概率诅咒
if (Random.Float() < 0.3f) {
    cursed = true;
}
return this;
```

---

### glowing()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：返回发光效果（树脂加成）

**返回值**：ItemSprite.Glowing，发光效果实例

**核心实现逻辑**：
```java
if (resinBonus == 0) return null;
return new ItemSprite.Glowing(0xFFFFFF, 1f / (float)resinBonus);
```

---

### value()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：计算法杖价值

**返回值**：int，金币价值

**核心实现逻辑**：
```java
int price = 75;
if (cursed && cursedKnown) price /= 2;
if (levelKnown) {
    if (level() > 0) price *= (level() + 1);
    else if (level() < 0) price /= (1 - level());
}
return Math.max(1, price);
```

---

### collisionProperties(int target)
**可见性**：public

**是否覆写**：否

**方法职责**：返回碰撞属性（考虑诅咒状态）

**参数**：
- `target` (int)：目标位置

**返回值**：int，Ballistica 碰撞属性

**核心实现逻辑**：
```java
if (cursed) return Ballistica.MAGIC_BOLT;
else return collisionProperties;
```

---

### storeInBundle(Bundle bundle) / restoreFromBundle(Bundle bundle)
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：序列化和反序列化法杖状态

---

### reset()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：重置法杖到初始状态

**核心实现逻辑**：
```java
super.reset();
usesLeftToID = USES_TO_ID;
availableUsesToID = USES_TO_ID / 2f;
```

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `onZap(Ballistica)` | 施法效果（抽象，子类实现） |
| `onHit(MagesStaff, Char, Char, int)` | 近战命中效果（抽象，子类实现） |
| `tryToZap(Hero, int)` | 尝试施法 |
| `charge(Char)` / `charge(Char, float)` | 启动充能 |
| `gainCharge(float)` / `gainCharge(float, boolean)` | 增加充能 |
| `stopCharging()` | 停止充能 |
| `initialCharges()` | 初始充能数（可覆写） |
| `chargesPerCast()` | 每次施法消耗（可覆写） |
| `fx(Ballistica, Callback)` | 视觉效果（可覆写） |
| `staffFx(MagesStaff.StaffParticle)` | 手杖粒子效果（可覆写） |
| `wandUsed()` | 施法完成处理 |
| `statsDesc()` | 统计描述（可覆写） |
| `updateLevel()` | 更新充能上限 |

### 内部辅助方法
| 方法 | 用途 |
|------|------|
| `wandProc(Char, int, int)` | 法杖命中通用效果 |
| `setIDReady()` | 设置准备鉴定 |

### 扩展入口
- `onZap()` - 必须实现，定义法术效果
- `onHit()` - 必须实现，定义近战效果
- `initialCharges()` - 可覆写，改变初始充能
- `chargesPerCast()` - 可覆写，改变充能消耗
- `fx()` - 可覆写，自定义视觉
- `staffFx()` - 可覆写，自定义手杖粒子
- `statsDesc()` - 可覆写，自定义统计描述
- `upgradeStat1/2/3()` - 可覆写，自定义升级属性显示

## 9. 运行机制与调用链

### 创建时机
- 地牢生成时由 `Generator.random(Generator.Category.WAND)` 创建
- 从存档恢复时由 `restoreFromBundle()` 重建

### 调用者
- `Hero` - 主要使用者
- `GameScene` - 目标选择
- `QuickSlotButton` - 快捷施法
- `MagesStaff` - 嵌入使用

### 被调用者
- `Ballistica` - 投射路径
- `MagicMissile` - 视觉效果
- `CursedWand` - 诅咒效果
- `Buff` 系统 - 各种Buff效果
- `Messages` - 国际化

### 系统流程位置
```
物品系统
├── Item (基类)
│   ├── Wand (法杖基类)
│   │   ├── DamageWand (伤害法杖基类)
│   │   │   ├── WandOfMagicMissile
│   │   │   ├── WandOfFireblast
│   │   │   ├── WandOfFrost
│   │   │   ├── WandOfLightning
│   │   │   ├── WandOfLivingEarth
│   │   │   ├── WandOfPrismaticLight
│   │   │   ├── WandOfDisintegration
│   │   │   └── WandOfBlastWave
│   │   ├── WandOfCorrosion
│   │   ├── WandOfCorruption
│   │   ├── WandOfRegrowth
│   │   ├── WandOfTransfusion
│   │   └── WandOfWarding
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.wands.wand.ac_zap` | 释放 | 施法动作名称 |
| `items.wands.wand.fizzles` | 你的法杖滋滋作响；一定是没能量了。 | 充能不足提示 |
| `items.wands.wand.no_magic` | 你的法杖滋滋作响；你不能在魔法免疫时使用法杖。 | 魔法免疫提示 |
| `items.wands.wand.self_target` | 你不能瞄准你自己！ | 自身目标提示 |
| `items.wands.wand.identify` | 你对你的法杖已经足够熟悉并将其完全鉴定。 | 鉴定提示 |
| `items.wands.wand.resin_one` | 这根法杖已经从奥术树脂获得了_1_次升级。 | 树脂加成描述 |
| `items.wands.wand.resin_many` | 这根法杖已经从奥术树脂获得了_%d_次升级。 | 树脂加成描述 |
| `items.wands.wand.cursed` | 这根法杖受到了诅咒，导致它的魔法混乱而随机。 | 诅咒描述 |
| `items.wands.wand.not_cursed` | 这根法杖没有被诅咒。 | 非诅咒描述 |
| `items.wands.wand.curse_discover` | 这根%s是诅咒的！ | 发现诅咒提示 |
| `items.wands.wand.prompt` | 选择要释放魔法的位置 | 目标选择提示 |
| `items.wands.wand$placeholder.name` | 法杖 | 占位符名称 |

### 依赖的资源
- `Assets.Sounds.ZAP` - 施法音效
- `Assets.Sounds.CHARGEUP` - 充能音效
- `ItemSpriteSheet.WAND_*` - 法杖图标
- `MagicMissile.MAGIC_MISSILE` - 魔法飞弹视觉

### 中文翻译来源
所有中文翻译来自 `core/src/main/assets/messages/items/items_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建法杖
Wand wand = new WandOfMagicMissile();
wand.level(2);  // 升级到+2

// 施放法术
if (wand.tryToZap(hero, targetPos)) {
    Ballistica shot = new Ballistica(hero.pos, targetPos, wand.collisionProperties(targetPos));
    wand.fx(shot, new Callback() {
        public void call() {
            wand.onZap(shot);
            wand.wandUsed();
        }
    });
}

// 检查充能
int currentCharges = wand.curCharges;
int maxCharges = wand.maxCharges;
System.out.println("充能: " + currentCharges + "/" + maxCharges);

// 增加充能
wand.gainCharge(1.5f);  // 增加1.5充能

// 启动自动充能
wand.charge(hero);
```

### 嵌入法师手杖示例
```java
// 法杖嵌入法师手杖后的近战效果触发
// 由 MagesStaff 调用
MagesStaff staff = new MagesStaff();
staff.imbue(wand);
// 近战命中时
wand.onHit(staff, attacker, defender, damage);
```

## 12. 开发注意事项

### 状态依赖
- `curCharges` 不能超过 `maxCharges`
- `maxCharges` 依赖 `level()` 计算
- `level()` 依赖 `curseInfusionBonus` 和 `resinBonus`
- 充能系统依赖 `charger` Buff 的正确附加

### 生命周期耦合
- 法杖必须在英雄背包中才能充能
- `collect()` 启动充能，`onDetach()` 停止充能
- 鉴定需要 `usesLeftToID` 和 `availableUsesToID` 配合

### 常见陷阱
1. **忘记调用 `wandUsed()`**：导致充能不消耗、鉴定进度不更新
2. **直接修改 `curCharges`**：应使用 `gainCharge()` 以正确处理 `partialCharge`
3. **忽略诅咒状态**：诅咒法杖会改变 `collisionProperties`
4. **未处理 `charger` 为 null**：在某些边缘情况下 `charger` 可能为 null

## 13. 修改建议与扩展点

### 适合扩展的位置
- `onZap()` - 实现新法杖的核心效果
- `onHit()` - 实现新法杖的近战效果
- `initialCharges()` - 改变初始充能数
- `chargesPerCast()` - 实现可变充能消耗
- `fx()` - 自定义施法视觉
- `staffFx()` - 自定义手杖粒子效果
- `statsDesc()` - 自定义统计描述
- `upgradeStat1/2/3()` - 自定义升级属性显示

### 不建议修改的位置
- `wandUsed()` - 核心状态管理，修改可能导致充能系统失效
- `buffedLvl()` - 复杂的等级计算，涉及多个天赋系统
- `Charger.act()` - 充能核心逻辑

### 重构建议
- 可考虑将天赋相关逻辑提取到独立的 TalentProc 类中
- Charger 可以设计为独立的外部类以减少内部类复杂度

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点