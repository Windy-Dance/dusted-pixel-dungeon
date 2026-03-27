# Scroll.java (卷轴基类) 详细文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/Scroll.java` |
| **类名** | `Scroll` |
| **修饰符** | `public abstract` |
| **继承关系** | `extends Item` |
| **实现接口** | 无 |
| **代码行数** | 373 行 |
| **许可证** | GNU General Public License v3.0 |

---

## 类职责

`Scroll` 是游戏中所有卷轴物品的**抽象基类**，负责：

1. **物品识别系统**：管理卷轴的随机符文标签和已识别状态
2. **阅读行为定义**：提供卷轴使用的核心流程（检查→执行→动画）
3. **状态持久化**：保存/恢复卷轴的识别状态
4. **炼金转换**：支持卷轴转换为符石（Runestone）的配方
5. **匿名卷轴**：提供不参与识别系统的特殊卷轴机制

---

## 4. 继承与协作关系

```
┌─────────────────────────────────────────────────────────────────┐
│                           Item                                   │
│                     (物品基类 - 可堆叠/可识别)                    │
└─────────────────────────────────────────────────────────────────┘
                                ▲
                                │ extends
                                │
┌─────────────────────────────────────────────────────────────────┐
│                     Scroll (抽象类)                              │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ 静态常量: AC_READ, TIME_TO_READ, runes                    │   │
│  │ 静态变量: handler (ItemStatusHandler)                     │   │
│  │ 实例字段: rune, talentFactor, talentChance, anonymous     │   │
│  │ 核心方法: doRead() (抽象), readAnimation(), setKnown()    │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
          ▲                    ▲                    ▲
          │                    │                    │
    ┌─────┴─────┐        ┌─────┴─────┐        ┌─────┴─────┐
    │ 具体卷轴   │        │ 库存卷轴   │        │ 异域卷轴   │
    │ (直接继承) │        │ Inventory │        │  Exotic   │
    └───────────┘        │  Scroll   │        │  Scroll   │
           │             └─────┬─────┘        └───────────┘
           │                   │
    ┌──────┴──────┐      ┌─────┴─────┐
    │ ScrollOf    │      │ ScrollOf  │
    │ Identify    │      │ Upgrade   │
    │ Terror      │      │ RemoveCurse│
    │ Teleport... │      │ Transmute │
    └─────────────┘      └───────────┘
```

### 直接子类

| 子类类型 | 类名 | 说明 |
|---------|------|------|
| 具体卷轴 | `ScrollOfIdentify` | 鉴定卷轴 |
| 具体卷轴 | `ScrollOfLullaby` | 摇篮曲卷轴 |
| 具体卷轴 | `ScrollOfMagicMapping` | 魔法地图卷轴 |
| 具体卷轴 | `ScrollOfMirrorImage` | 镜像卷轴 |
| 具体卷轴 | `ScrollOfRage` | 狂暴卷轴 |
| 具体卷轴 | `ScrollOfRecharging` | 充能卷轴 |
| 具体卷轴 | `ScrollOfRemoveCurse` | 解咒卷轴 |
| 具体卷轴 | `ScrollOfRetribution` | 报复卷轴 |
| 具体卷轴 | `ScrollOfTeleportation` | 传送卷轴 |
| 具体卷轴 | `ScrollOfTerror` | 恐惧卷轴 |
| 具体卷轴 | `ScrollOfTransmutation` | 转化卷轴 |
| 库存卷轴 | `InventoryScroll` | 需选择物品的卷轴抽象类 |
| 库存卷轴 | `ScrollOfUpgrade` | 升级卷轴 |
| 异域卷轴 | `ExoticScroll` | 异域卷轴抽象基类 |

---

## 静态常量表

### 动作常量

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `AC_READ` | `String` | `"READ"` | 阅读动作标识符 |

### 时间常量

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `TIME_TO_READ` | `float` | `1f` | 阅读卷轴所需时间（1回合） |

### 符文标签映射

| 常量名 | 类型 | 说明 |
|--------|------|------|
| `runes` | `LinkedHashMap<String, Integer>` | 符文名称到精灵图索引的映射，共12种符文 |

**符文列表**（北欧如尼文）：

| 符文名 | 精灵图常量 | 含义 |
|--------|-----------|------|
| `KAUNAN` | `SCROLL_KAUNAN` | 火炬/溃疡 |
| `SOWILO` | `SCROLL_SOWILO` | 太阳 |
| `LAGUZ` | `SCROLL_LAGUZ` | 水/湖泊 |
| `YNGVI` | `SCROLL_YNGVI` | 英格维神 |
| `GYFU` | `SCROLL_GYFU` | 礼物 |
| `RAIDO` | `SCROLL_RAIDO` | 旅途 |
| `ISAZ` | `SCROLL_ISAZ` | 冰 |
| `MANNAZ` | `SCROLL_MANNAZ` | 人类 |
| `NAUDIZ` | `SCROLL_NAUDIZ` | 需求/困境 |
| `BERKANAN` | `SCROLL_BERKANAN` | 桦树/生长 |
| `ODAL` | `SCROLL_ODAL` | 遗产/家园 |
| `TIWAZ` | `SCROLL_TIWAZ` | 提尔神/正义 |

---

## 实例字段表

| 字段名 | 类型 | 默认值 | 访问级别 | 说明 |
|--------|------|--------|---------|------|
| `handler` | `ItemStatusHandler<Scroll>` | `null` | `protected static` | 物品状态处理器，管理识别状态 |
| `rune` | `String` | 由handler分配 | `protected` | 当前卷轴的符文标签 |
| `talentFactor` | `float` | `1` | `public` | 天赋触发强度系数 |
| `talentChance` | `float` | `1` | `public` | 天赋触发概率 (0-1) |
| `anonymous` | `boolean` | `false` | `protected` | 是否为匿名卷轴 |

### 继承自 Item 的关键字段

| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `stackable` | `boolean` | `true` | 卷轴可堆叠 |
| `defaultAction` | `String` | `AC_READ` | 默认动作为阅读 |

---

## 7. 方法详解

### 静态初始化方法

#### `initLabels()` (第105-107行)

```java
@SuppressWarnings("unchecked")
public static void initLabels() {
    handler = new ItemStatusHandler<>( (Class<? extends Scroll>[])Generator.Category.SCROLL.classes, runes );
}
```

**职责**：游戏初始化时创建物品状态处理器，为所有卷轴类型随机分配符文标签。

**流程**：
1. 获取 `Generator.Category.SCROLL.classes` 中的所有卷轴类型（12种）
2. 获取 `runes` 映射中的所有可用符文
3. 随机打乱并为每种卷轴类型分配唯一的符文标签

**调用时机**：游戏启动时调用，确保每局游戏卷轴外观随机化。

---

#### `clearLabels()` (第109-111行)

```java
public static void clearLabels(){
    handler = null;
}
```

**职责**：清除状态处理器，用于游戏重置。

---

#### `save(Bundle bundle)` (第113-115行)

```java
public static void save( Bundle bundle ) {
    handler.save( bundle );
}
```

**职责**：将所有卷轴的识别状态和符文标签保存到存档。

---

#### `saveSelectively(Bundle bundle, ArrayList<Item> items)` (第117-131行)

```java
public static void saveSelectively( Bundle bundle, ArrayList<Item> items ) {
    ArrayList<Class<?extends Item>> classes = new ArrayList<>();
    for (Item i : items){
        if (i instanceof ExoticScroll){
            if (!classes.contains(ExoticScroll.exoToReg.get(i.getClass()))){
                classes.add(ExoticScroll.exoToReg.get(i.getClass()));
            }
        } else if (i instanceof Scroll){
            if (!classes.contains(i.getClass())){
                classes.add(i.getClass());
            }
        }
    }
    handler.saveClassesSelectively( bundle, classes );
}
```

**职责**：选择性保存卷轴状态，仅保存指定物品列表中涉及的卷轴类型。

**参数**：
- `bundle`：存档数据容器
- `items`：需要保存状态的物品列表

**特殊处理**：异域卷轴（ExoticScroll）会映射回对应的普通卷轴类型进行保存。

---

#### `restore(Bundle bundle)` (第133-136行)

```java
@SuppressWarnings("unchecked")
public static void restore( Bundle bundle ) {
    handler = new ItemStatusHandler<>( (Class<? extends Scroll>[])Generator.Category.SCROLL.classes, runes, bundle );
}
```

**职责**：从存档恢复卷轴的识别状态和符文标签分配。

**调用时机**：读取存档时调用。

---

### 构造方法

#### `Scroll()` (第138-141行)

```java
public Scroll() {
    super();
    reset();
}
```

**职责**：创建卷轴实例并调用 `reset()` 初始化符文标签和图像。

---

### 匿名卷轴机制

#### `anonymize()` (第147-150行)

```java
public void anonymize(){
    if (!isKnown()) image = ItemSpriteSheet.SCROLL_HOLDER;
    anonymous = true;
}
```

**职责**：将卷轴标记为匿名状态。

**效果**：
1. 如果卷轴未被识别，图像替换为占位符（`SCROLL_HOLDER`）
2. 设置 `anonymous = true`

**使用场景**：
- UI显示中的虚拟卷轴
- 仅用于产生效果的临时卷轴实例

**注意**：匿名卷轴始终视为已识别，不影响全局识别状态。

---

### 重写方法

#### `reset()` (第153-163行)

```java
@Override
public void reset(){
    super.reset();
    if (handler != null && handler.contains(this)) {
        image = handler.image(this);
        rune = handler.label(this);
    } else {
        image = ItemSpriteSheet.SCROLL_KAUNAN;
        rune = "KAUNAN";
    }
}
```

**职责**：重置卷轴状态，从状态处理器获取符文标签和图像。

**流程**：
1. 调用父类 `reset()` 清除临时状态
2. 如果 handler 存在且包含此类型：
   - 从 handler 获取随机分配的图像索引
   - 从 handler 获取随机分配的符文名称
3. 否则使用默认值（KAUNAN符文）

---

#### `actions(Hero hero)` (第165-170行)

```java
@Override
public ArrayList<String> actions( Hero hero ) {
    ArrayList<String> actions = super.actions( hero );
    actions.add( AC_READ );
    return actions;
}
```

**职责**：返回卷轴可用的动作列表。

**返回**：父类动作（丢弃、投掷）+ 阅读动作。

---

#### `execute(Hero hero, String action)` (第172-192行)

```java
@Override
public void execute( Hero hero, String action ) {
    super.execute( hero, action );

    if (action.equals( AC_READ )) {
        
        if (hero.buff(MagicImmune.class) != null){
            GLog.w( Messages.get(this, "no_magic") );
        } else if (hero.buff( Blindness.class ) != null) {
            GLog.w( Messages.get(this, "blinded") );
        } else if (hero.buff(UnstableSpellbook.bookRecharge.class) != null
                && hero.buff(UnstableSpellbook.bookRecharge.class).isCursed()
                && !(this instanceof ScrollOfRemoveCurse || this instanceof ScrollOfAntiMagic)){
            GLog.n( Messages.get(this, "cursed") );
        } else {
            doRead();
        }
    }
}
```

**职责**：执行卷轴动作，处理阅读前的各种检查。

**检查流程**（按顺序）：

| 检查条件 | 失败提示 | 说明 |
|---------|---------|------|
| `MagicImmune` buff存在 | "no_magic" | 魔法免疫状态无法使用卷轴 |
| `Blindness` buff存在 | "blinded" | 失明状态无法阅读卷轴 |
| `UnstableSpellbook` 被诅咒 | "cursed" | 被诅咒的不稳定法术书会阻止大多数卷轴使用 |

**特殊例外**：解咒卷轴（`ScrollOfRemoveCurse`）和反魔法卷轴（`ScrollOfAntiMagic`）可以在被诅咒的不稳定法术书状态下使用。

**通过检查后**：调用抽象方法 `doRead()` 执行具体阅读逻辑。

---

### 核心抽象方法

#### `doRead()` (第194行)

```java
public abstract void doRead();
```

**职责**：执行卷轴阅读的具体效果，**子类必须实现**。

**实现要求**：
- 处理卷轴的具体效果
- 调用 `readAnimation()` 播放阅读动画
- 调用 `detach()` 消耗卷轴

---

### 阅读动画

#### `readAnimation()` (第196-209行)

```java
public void readAnimation() {
    Invisibility.dispel();
    curUser.spend( TIME_TO_READ );
    curUser.busy();
    ((HeroSprite)curUser.sprite).read();

    if (!anonymous) {
        Catalog.countUse(getClass());
    }
    if (Random.Float() < talentChance) {
        Talent.onScrollUsed(curUser, curUser.pos, talentFactor, getClass());
    }
}
```

**职责**：执行阅读卷轴的标准动画和后续处理。

**执行步骤**：

| 步骤 | 代码 | 说明 |
|-----|------|------|
| 1 | `Invisibility.dispel()` | 解除英雄的隐身状态 |
| 2 | `curUser.spend(TIME_TO_READ)` | 消耗1回合时间 |
| 3 | `curUser.busy()` | 将英雄标记为忙碌状态 |
| 4 | `((HeroSprite)curUser.sprite).read()` | 播放阅读动画 |
| 5 | `Catalog.countUse(getClass())` | 记录使用次数（非匿名卷轴） |
| 6 | `Talent.onScrollUsed(...)` | 触发相关天赋效果 |

**天赋触发机制**：
- `talentChance` 控制触发概率（默认100%）
- `talentFactor` 控制触发强度（默认1.0）

---

### 识别状态管理

#### `isKnown()` (第211-213行)

```java
public boolean isKnown() {
    return anonymous || (handler != null && handler.isKnown( this ));
}
```

**职责**：判断当前卷轴是否已被识别。

**返回 `true` 的条件**：
- 卷轴是匿名的（`anonymous == true`），或
- handler 存在且该卷轴类型在已知集合中

---

#### `setKnown()` (第215-227行)

```java
public void setKnown() {
    if (!anonymous) {
        if (!isKnown()) {
            handler.know(this);
            updateQuickslot();
        }
        
        if (Dungeon.hero.isAlive()) {
            Catalog.setSeen(getClass());
            Statistics.itemTypesDiscovered.add(getClass());
        }
    }
}
```

**职责**：将当前卷轴标记为已识别。

**流程**：
1. 检查是否为匿名卷轴（匿名卷轴不处理）
2. 如果未识别，调用 `handler.know(this)` 添加到已知集合
3. 更新快捷栏显示
4. 如果英雄存活：
   - 记录到图鉴（Catalog）
   - 添加到已发现物品类型统计

---

#### `identify(boolean byHero)` (第229-237行)

```java
@Override
public Item identify( boolean byHero ) {
    super.identify(byHero);

    if (!isKnown()) {
        setKnown();
    }
    return this;
}
```

**职责**：识别物品，同时更新卷轴的已知状态。

**参数**：`byHero` - 是否由英雄主动识别。

---

### 显示相关方法

#### `name()` (第239-242行)

```java
@Override
public String name() {
    return isKnown() ? super.name() : Messages.get(this, rune);
}
```

**职责**：返回卷轴的显示名称。

**返回值**：
- 已识别：返回真实名称（如"升级卷轴"）
- 未识别：返回符文名称（如"KAUNAN符文卷轴"）

---

#### `info()` (第244-248行)

```java
@Override
public String info() {
    //skip custom notes if anonymized and un-Ided
    return (anonymous && (handler == null || !handler.isKnown( this ))) ? desc() : super.info();
}
```

**职责**：返回卷轴的详细信息文本。

**处理逻辑**：匿名且未识别的卷轴跳过自定义注释，直接返回描述文本。

---

#### `desc()` (第250-253行)

```java
@Override
public String desc() {
    return isKnown() ? super.desc() : Messages.get(this, "unknown_desc");
}
```

**职责**：返回卷轴的描述文本。

**返回值**：
- 已识别：返回具体描述
- 未识别：返回"未知的卷轴"描述

---

### 物品属性方法

#### `isUpgradable()` (第255-258行)

```java
@Override
public boolean isUpgradable() {
    return false;
}
```

**职责**：卷轴不可升级。

---

#### `isIdentified()` (第260-263行)

```java
@Override
public boolean isIdentified() {
    return isKnown();
}
```

**职责**：卷轴的识别状态等同于已知状态。

---

#### `value()` (第277-280行)

```java
@Override
public int value() {
    return 30 * quantity;
}
```

**职责**：返回卷轴的金币价值（每张30金币）。

---

#### `energyVal()` (第282-285行)

```java
@Override
public int energyVal() {
    return 6 * quantity;
}
```

**职责**：返回卷轴的能量价值（每张6能量）。

---

### 静态查询方法

#### `getKnown()` (第265-267行)

```java
public static HashSet<Class<? extends Scroll>> getKnown() {
    return handler.known();
}
```

**职责**：返回所有已识别的卷轴类型集合。

---

#### `getUnknown()` (第269-271行)

```java
public static HashSet<Class<? extends Scroll>> getUnknown() {
    return handler.unknown();
}
```

**职责**：返回所有未识别的卷轴类型集合。

---

#### `allKnown()` (第273-275行)

```java
public static boolean allKnown() {
    return handler != null && handler.known().size() == Generator.Category.SCROLL.classes.length;
}
```

**职责**：判断是否所有卷轴类型都已识别。

**返回 `true` 条件**：
- handler 存在，且
- 已识别数量 == 卷轴总类型数（12种）

---

## 内部类

### PlaceHolder (占位符卷轴)

```java
public static class PlaceHolder extends Scroll {
    {
        image = ItemSpriteSheet.SCROLL_HOLDER;
    }
    
    @Override
    public boolean isSimilar(Item item) {
        return ExoticScroll.regToExo.containsKey(item.getClass())
                || ExoticScroll.regToExo.containsValue(item.getClass());
    }
    
    @Override
    public void doRead() {}
    
    @Override
    public String info() {
        return "";
    }
}
```

**职责**：在UI中显示卷轴占位符，用于快捷栏或背包界面。

**特性**：
- 使用占位符图像
- `isSimilar()` 方法匹配所有卷轴和异域卷轴
- `doRead()` 空实现，不可实际使用
- `info()` 返回空字符串

---

### ScrollToStone (卷轴转符石配方)

```java
public static class ScrollToStone extends Recipe {
    private static HashMap<Class<?extends Scroll>, Class<?extends Runestone>> stones = new HashMap<>();
    static {
        stones.put(ScrollOfIdentify.class,      StoneOfIntuition.class);
        stones.put(ScrollOfLullaby.class,       StoneOfDeepSleep.class);
        stones.put(ScrollOfMagicMapping.class,  StoneOfClairvoyance.class);
        stones.put(ScrollOfMirrorImage.class,   StoneOfFlock.class);
        stones.put(ScrollOfRetribution.class,   StoneOfBlast.class);
        stones.put(ScrollOfRage.class,          StoneOfAggression.class);
        stones.put(ScrollOfRecharging.class,    StoneOfShock.class);
        stones.put(ScrollOfRemoveCurse.class,   StoneOfDetectMagic.class);
        stones.put(ScrollOfTeleportation.class, StoneOfBlink.class);
        stones.put(ScrollOfTerror.class,        StoneOfFear.class);
        stones.put(ScrollOfTransmutation.class, StoneOfAugmentation.class);
        stones.put(ScrollOfUpgrade.class,       StoneOfEnchantment.class);
    }
    // ... 其他方法
}
```

**职责**：定义卷轴炼金转换为符石的配方。

**转换映射表**：

| 卷轴类型 | 转换为符石 |
|---------|-----------|
| 鉴定卷轴 | 直觉符石 |
| 摇篮曲卷轴 | 深睡符石 |
| 魔法地图卷轴 | 透视符石 |
| 镜像卷轴 | 羊群符石 |
| 报复卷轴 | 爆破符石 |
| 狂暴卷轴 | 挑衅符石 |
| 充能卷轴 | 电击符石 |
| 解咒卷轴 | 探魔符石 |
| 传送卷轴 | 闪现符石 |
| 恐惧卷轴 | 恐惧符石 |
| 转化卷轴 | 强化符石 |
| 升级卷轴 | 附魔符石 |

**配方方法**：

| 方法 | 说明 |
|------|------|
| `testIngredients()` | 验证输入是否为单个可转换的卷轴 |
| `cost()` | 返回炼金消耗（0能量） |
| `brew()` | 执行转换：1卷轴 → 2符石 |
| `sampleOutput()` | 返回预览输出 |

**特殊逻辑**（`brew()` 方法）：
- 如果在炼金界面，未识别的卷轴会触发鉴定提示
- 其他情况下直接鉴定卷轴

---

## 11. 使用示例

### 创建自定义卷轴

```java
public class ScrollOfExample extends Scroll {
    
    {
        // 设置卷轴图标（用于已知状态的显示）
        icon = ItemSpriteSheet.Icons.SCROLL_CUSTOM;
        
        // 调整天赋触发参数
        talentFactor = 1.5f;  // 效果增强50%
        talentChance = 0.8f;  // 80%触发概率
    }
    
    @Override
    public void doRead() {
        // 1. 执行卷轴效果
        Sample.INSTANCE.play(Assets.Sounds.READ);
        GLog.i("你阅读了示例卷轴！");
        
        // 2. 具体效果实现
        curUser.HP = curUser.HT;
        
        // 3. 触发阅读动画和后续处理
        readAnimation();
        
        // 4. 消耗卷轴
        detach(curUser.belongings.backpack);
    }
}
```

### 创建库存选择型卷轴

```java
public class ScrollOfEnchant extends InventoryScroll {
    
    {
        // 指定优先显示的背包类型
        preferredBag = Belongings.Backpack.class;
    }
    
    @Override
    protected boolean usableOnItem(Item item) {
        // 只有武器和盔甲可以附魔
        return item instanceof Weapon || item instanceof Armor;
    }
    
    @Override
    protected void onItemSelected(Item item) {
        // 执行附魔效果
        if (item instanceof Weapon) {
            // 武器附魔逻辑
        }
        // 动画由父类 InventoryScroll 自动处理
    }
}
```

### 使用匿名卷轴

```java
// 创建匿名卷轴（用于UI显示或特殊效果）
Scroll scroll = new ScrollOfIdentify();
scroll.anonymize();

// 匿名卷轴特性：
// 1. 始终视为已识别
// 2. 不影响全局识别状态
// 3. 不计入使用统计
```

### 检查卷轴识别状态

```java
// 检查特定卷轴是否已知
if (ScrollOfUpgrade.class.newInstance().isKnown()) {
    GLog.i("升级卷轴已知！");
}

// 获取所有已知卷轴
HashSet<Class<? extends Scroll>> known = Scroll.getKnown();

// 获取所有未知卷轴
HashSet<Class<? extends Scroll>> unknown = Scroll.getUnknown();

// 检查是否全部识别
if (Scroll.allKnown()) {
    GLog.i("所有卷轴都已识别！");
}
```

---

## 注意事项

### 1. 符文随机化机制

每局游戏开始时，系统会为所有卷轴类型随机分配不同的符文外观。这意味着：
- 同一类型的卷轴在不同存档中有不同的外观
- 玩家需要通过使用或鉴定来学习每种符文对应的卷轴类型

### 2. 匿名卷轴的使用限制

匿名卷轴适用于：
- UI界面中的占位显示
- 临时产生的效果实例
- 不应计入识别统计的场景

**不适用于**：
- 玩家正常获取和使用的卷轴
- 需要持久化保存的物品

### 3. 异域卷轴识别机制

异域卷轴（`ExoticScroll`）与普通卷轴共享识别状态：
- 识别普通卷轴后，对应的异域卷轴自动视为已识别
- 反之亦然

### 4. 天赋系统交互

`talentFactor` 和 `talentChance` 字段用于控制卷轴使用时的天赋触发：
- 大多数卷轴使用默认值（1.0, 1.0）
- 升级卷轴等特殊卷轴可以调高 `talentFactor`

### 5. 存档兼容性

修改卷轴类型或添加新卷轴时，需要注意：
- 保持 `Generator.Category.SCROLL.classes` 数组与 `runes` 映射的数量一致
- 新增卷轴需要在静态初始化块中添加对应的符石映射

---

## 最佳实践

### 1. 实现新卷轴时

```java
public class ScrollOfCustom extends Scroll {
    
    @Override
    public void doRead() {
        // 优先执行核心效果
        applyEffect();
        
        // 然后处理动画和消耗
        readAnimation();
        detach(curUser.belongings.backpack);
    }
    
    private void applyEffect() {
        // 具体效果实现
    }
}
```

### 2. 处理卷轴消耗

始终在效果执行后消耗卷轴：

```java
// 正确做法
doEffect();
readAnimation();
detach(curUser.belongings.backpack);

// 错误做法 - 先消耗可能导致空指针
detach(curUser.belongings.backpack);
doEffect(); // 此时 quantity 已减，可能出问题
```

### 3. 使用 InventoryScroll 进行物品选择

如果卷轴效果需要选择物品：

```java
public class ScrollOfRefine extends InventoryScroll {
    
    @Override
    protected boolean usableOnItem(Item item) {
        return item.isUpgradable();
    }
    
    @Override
    protected void onItemSelected(Item item) {
        // item 已通过 usableOnItem 验证
        item.upgrade();
        // 无需手动调用 readAnimation()，父类已处理
    }
}
```

### 4. 自定义价值

特殊卷轴可以覆盖价值计算：

```java
@Override
public int value() {
    return isKnown() ? 50 * quantity : super.value();
}

@Override
public int energyVal() {
    return isKnown() ? 10 * quantity : super.energyVal();
}
```

### 5. 添加炼金转换

在 `ScrollToStone.stones` 映射中添加新卷轴的转换：

```java
static {
    // ... 现有映射
    stones.put(ScrollOfCustom.class, StoneOfCustom.class);
}
```

---

## 相关类参考

| 类名 | 说明 |
|------|------|
| `Item` | 物品基类 |
| `ItemStatusHandler` | 物品状态处理器，管理识别系统 |
| `InventoryScroll` | 需选择物品的卷轴抽象类 |
| `ExoticScroll` | 异域卷轴抽象基类 |
| `Runestone` | 符石基类 |
| `Recipe` | 炼金配方基类 |
| `Generator` | 物品生成器 |
| `Talent` | 天赋系统 |
| `Catalog` | 图鉴/物品发现记录 |