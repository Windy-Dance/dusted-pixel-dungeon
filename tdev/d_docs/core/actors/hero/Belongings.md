# Belongings 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Belongings.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.actors.hero |
| 类类型 | class |
| 继承关系 | implements Iterable&lt;Item&gt; |
| 代码行数 | 482 |

---

## 2. 类职责说明

Belongings 是英雄的**物品栏管理系统**，负责管理英雄的所有物品，包括：
1. **背包容器**：Backpack 实例，存储所有非装备物品
2. **装备槽位**：武器、护甲、神器、杂项、戒指、副武器等装备槽
3. **迭代功能**：实现 Iterable&lt;Item&gt; 接口，支持遍历所有物品（背包 + 装备）

---

## 4. 继承与协作关系

```mermaid
classDiagram
    class Belongings {
        -Hero owner
        +Backpack backpack
        +KindOfWeapon weapon
        +Armor armor
        +Artifact artifact
        +KindofMisc misc
        +Ring ring
        +KindOfWeapon thrownWeapon
        +KindOfWeapon abilityWeapon
        +KindOfWeapon secondWep
        -boolean lostInvent
        +Belongings(Hero)
        +KindOfWeapon attackingWeapon()
        +void lostInventory(boolean)
        +boolean lostInventory()
        +KindOfWeapon weapon()
        +Armor armor()
        +Artifact artifact()
        +KindofMisc misc()
        +Ring ring()
        +KindOfWeapon secondWep()
        +void storeInBundle(Bundle)
        +void restoreFromBundle(Bundle)
        +void clear()
        +ArrayList~Bag~ getBags()
        +T getItem(Class~T~)
        +ArrayList~T~ getAllItems(Class~T~)
        +boolean contains(Item)
        +Item getSimilar(Item)
        +ArrayList~Item~ getAllSimilar(Item)
        +void identify()
        +void observe()
        +void uncurseEquipped()
        +Item randomUnequipped()
        +int charge(float)
        +Iterator~Item~ iterator()
    }

    class Backpack {
        +int capacity()
    }

    class ItemIterator {
        -int index
        -Iterator~Item~ backpackIterator
        -Item[] equipped
        -int backpackIndex
        +boolean hasNext()
        +Item next()
        +void remove()
    }

    class Hero {
        +Belongings belongings
    }

    class Item {
        +boolean keptThoughLostInvent
        +keptThroughLostInventory()
    }

    class Bag {
        +ArrayList~Item~ items
        +int capacity()
        +boolean canHold(Item)
    }

    class LostInventory {
        <<Buff>>
    }

    Iterable~Item~ <|.. Belongings
    Bag <|-- Backpack
    Belongings +-- Backpack
    Belongings +-- ItemIterator
    Hero *-- Belongings
    Belongings --> Item : 管理
    Belongings --> Bag : 包含
    LostInventory --> Belongings : 影响
```

---

## 静态常量表

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `WEAPON` | String | "weapon" | Bundle 存储键 - 主武器 |
| `ARMOR` | String | "armor" | Bundle 存储键 - 护甲 |
| `ARTIFACT` | String | "artifact" | Bundle 存储键 - 神器 |
| `MISC` | String | "misc" | Bundle 存储键 - 杂项装备 |
| `RING` | String | "ring" | Bundle 存储键 - 戒指 |
| `SECOND_WEP` | String | "second_wep" | Bundle 存储键 - 副武器 |
| `bundleRestoring` | boolean | false | 标记是否正在从 Bundle 恢复 |

---

## 实例字段表

### 装备槽位

| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| `weapon` | KindOfWeapon | public | 主武器槽 |
| `armor` | Armor | public | 护甲槽 |
| `artifact` | Artifact | public | 神器槽 |
| `misc` | KindofMisc | public | 杂项槽（可放神器或戒指） |
| `ring` | Ring | public | 戒指槽 |
| `secondWep` | KindOfWeapon | public | 副武器槽（冠军子职业专用） |

### 临时武器字段

| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| `thrownWeapon` | KindOfWeapon | public | 投掷武器临时引用 |
| `abilityWeapon` | KindOfWeapon | public | 技能武器引用（决斗师专用） |

### 其他字段

| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| `owner` | Hero | private | 所属英雄 |
| `backpack` | Backpack | public | 背包容器 |
| `lostInvent` | boolean | private | 缓存：是否丢失物品栏 |

---

## 内部类

### Backpack（背包）

Backpack 是 Belongings 的内部类，继承自 Bag，是英雄的主背包容器。

```java
public static class Backpack extends Bag {
    {
        image = ItemSpriteSheet.BACKPACK;
    }

    @Override
    public int capacity() {
        int cap = super.capacity();  // 基础容量 20
        // 每个容器袋增加 1 容量
        for (Item item : items) {
            if (item instanceof Bag) {
                cap++;
            }
        }
        // 副武器占用一个背包槽位
        if (Dungeon.hero != null && Dungeon.hero.belongings.secondWep != null) {
            cap--;
        }
        return cap;
    }
}
```

**容量计算逻辑**：
1. 基础容量：20（继承自 Bag）
2. 容器袋加成：背包内每有一个 Bag 子类物品，容量 +1
3. 副武器惩罚：如果装备了副武器，容量 -1（因为副武器仍占用背包槽位）

---

### ItemIterator（物品迭代器）

ItemIterator 是 Belongings 的私有内部类，实现 Iterator&lt;Item&gt; 接口，用于遍历英雄的所有物品。

```java
private class ItemIterator implements Iterator<Item> {
    private int index = 0;
    private Iterator<Item> backpackIterator = backpack.iterator();
    private Item[] equipped = {weapon, armor, artifact, misc, ring, secondWep};
    private int backpackIndex = equipped.length;  // = 6

    @Override
    public boolean hasNext() {
        // 先检查装备槽是否有剩余物品
        for (int i = index; i < backpackIndex; i++) {
            if (equipped[i] != null) {
                return true;
            }
        }
        // 再检查背包
        return backpackIterator.hasNext();
    }

    @Override
    public Item next() {
        // 优先返回装备槽物品
        while (index < backpackIndex) {
            Item item = equipped[index++];
            if (item != null) {
                return item;
            }
        }
        // 然后返回背包物品
        return backpackIterator.next();
    }

    @Override
    public void remove() {
        // 根据当前索引判断移除装备还是背包物品
        switch (index) {
            case 0: equipped[0] = weapon = null; break;
            case 1: equipped[1] = armor = null; break;
            case 2: equipped[2] = artifact = null; break;
            case 3: equipped[3] = misc = null; break;
            case 4: equipped[4] = ring = null; break;
            case 5: equipped[5] = secondWep = null; break;
            default: backpackIterator.remove();
        }
    }
}
```

**迭代顺序**：
1. weapon（主武器）
2. armor（护甲）
3. artifact（神器）
4. misc（杂项）
5. ring（戒指）
6. secondWep（副武器）
7. backpack 中的所有物品（包括嵌套的 Bag 内容）

---

## 7. 方法详解

### 构造函数

**签名**: `public Belongings(Hero owner)`

**功能**: 创建物品栏实例并初始化背包

**参数**:
- `owner`: Hero - 所属英雄

**实现逻辑**:
```java
public Belongings(Hero owner) {
    this.owner = owner;
    backpack = new Backpack();
    backpack.owner = owner;
}
```

---

### attackingWeapon()

**签名**: `public KindOfWeapon attackingWeapon()`

**功能**: 获取当前用于攻击的武器

**返回值**: KindOfWeapon - 当前攻击武器

**实现逻辑**:
```java
public KindOfWeapon attackingWeapon() {
    if (thrownWeapon != null) return thrownWeapon;   // 优先级1：投掷武器
    if (abilityWeapon != null) return abilityWeapon; // 优先级2：技能武器
    return weapon();                                  // 优先级3：主武器
}
```

**优先级说明**：
1. **thrownWeapon**：投掷武器攻击时临时设置，确保使用投掷武器的属性
2. **abilityWeapon**：决斗师使用武器技能时设置，确保技能使用正确的武器
3. **weapon()**：通过访问器获取主武器（考虑 lostInventory 状态）

---

### lostInventory() 系列方法

**签名**:
- `public void lostInventory(boolean val)`
- `public boolean lostInventory()`

**功能**: 管理"丢失物品栏"状态的缓存

**说明**:
- `lostInventory` 字段是 `hero.buff(LostInventory.class)` 的缓存
- 避免频繁调用 `buff()` 方法检查状态
- 当英雄被 LostInventory Buff 影响时，大部分物品变得不可用
- 特殊物品可通过 `keptThroughLostInventory()` 保持可用

---

### 装备访问器方法（weapon/armor/artifact/misc/ring/secondWep）

**签名**: `public [Type] [fieldName]()`

**功能**: 获取装备槽中的物品，考虑 lostInventory 状态

**返回值**: 对应类型的装备，或 null

**实现模式**（以 weapon() 为例）:
```java
public KindOfWeapon weapon() {
    if (!lostInventory() || (weapon != null && weapon.keptThroughLostInventory())) {
        return weapon;
    } else {
        return null;
    }
}
```

**与直接字段访问的区别**:

| 访问方式 | 是否考虑 lostInventory | 适用场景 |
|----------|------------------------|----------|
| `belongings.weapon` | 否 | 装备/卸下物品、界面显示、死亡英雄处理 |
| `belongings.weapon()` | 是 | 计算属性、战斗逻辑、正常游戏流程 |

**所有访问器方法**:
- `weapon()` - 主武器
- `armor()` - 护甲
- `artifact()` - 神器
- `misc()` - 杂项装备
- `ring()` - 戒指
- `secondWep()` - 副武器

---

### storeInBundle() / restoreFromBundle()

**签名**:
- `public void storeInBundle(Bundle bundle)`
- `public void restoreFromBundle(Bundle bundle)`

**功能**: 序列化/反序列化物品栏数据

**实现逻辑**:
```java
public void storeInBundle(Bundle bundle) {
    backpack.storeInBundle(bundle);
    bundle.put(WEAPON, weapon);
    bundle.put(ARMOR, armor);
    bundle.put(ARTIFACT, artifact);
    bundle.put(MISC, misc);
    bundle.put(RING, ring);
    bundle.put(SECOND_WEP, secondWep);
}

public void restoreFromBundle(Bundle bundle) {
    bundleRestoring = true;
    backpack.clear();
    backpack.restoreFromBundle(bundle);

    weapon = (KindOfWeapon) bundle.get(WEAPON);
    if (weapon() != null) weapon().activate(owner);
    // ... 其他装备同理

    bundleRestoring = false;
}
```

**注意事项**:
- `bundleRestoring` 标志用于控制快速栏位的恢复时机
- 恢复时会调用 `activate()` 激活装备的特殊效果

---

### clear()

**签名**: `public void clear()`

**功能**: 清空物品栏

**实现逻辑**:
```java
public void clear() {
    backpack.clear();
    weapon = secondWep = null;
    armor = null;
    artifact = null;
    misc = null;
    ring = null;
}
```

---

### preview()

**签名**: `public static void preview(GamesInProgress.Info info, Bundle bundle)`

**功能**: 预览存档中的护甲信息（用于存档选择界面）

**参数**:
- `info`: GamesInProgress.Info - 存档信息对象
- `bundle`: Bundle - 存档数据

**实现逻辑**:
```java
public static void preview(GamesInProgress.Info info, Bundle bundle) {
    if (bundle.contains(ARMOR)) {
        Armor armor = ((Armor) bundle.get(ARMOR));
        if (armor instanceof ClassArmor) {
            info.armorTier = 6;  // 职业护甲显示为 tier 6
        } else {
            info.armorTier = armor.tier;
        }
    } else {
        info.armorTier = 0;
    }
}
```

---

### getBags()

**签名**: `public ArrayList<Bag> getBags()`

**功能**: 获取所有容器袋（忽略 lostInventory 状态）

**返回值**: ArrayList&lt;Bag&gt; - 包含主背包和所有容器袋的列表

**实现逻辑**:
```java
public ArrayList<Bag> getBags() {
    ArrayList<Bag> result = new ArrayList<>();
    result.add(backpack);
    for (Item i : this) {
        if (i instanceof Bag) {
            result.add((Bag) i);
        }
    }
    return result;
}
```

---

### getItem() / getAllItems()

**签名**:
- `public <T extends Item> T getItem(Class<T> itemClass)`
- `public <T extends Item> ArrayList<T> getAllItems(Class<T> itemClass)`

**功能**: 按类型查找物品

**参数**:
- `itemClass`: Class&lt;T&gt; - 物品类型的 Class 对象

**返回值**: 指定类型的物品（单个或列表）

**实现逻辑**（以 getItem 为例）:
```java
public <T extends Item> T getItem(Class<T> itemClass) {
    boolean lostInvent = lostInventory();
    for (Item item : this) {
        if (itemClass.isInstance(item)) {
            if (!lostInvent || item.keptThroughLostInventory()) {
                return (T) item;
            }
        }
    }
    return null;
}
```

---

### contains()

**签名**: `public boolean contains(Item contains)`

**功能**: 检查物品是否在物品栏中

**参数**:
- `contains`: Item - 要检查的物品

**返回值**: boolean - 是否包含该物品

---

### getSimilar() / getAllSimilar()

**签名**:
- `public Item getSimilar(Item similar)`
- `public ArrayList<Item> getAllSimilar(Item similar)`

**功能**: 查找相似物品（用于物品堆叠）

**参数**:
- `similar`: Item - 参考物品

**返回值**: 相似的物品（单个或列表）

**说明**: 使用 `item.isSimilar()` 方法判断相似性

---

### identify()

**签名**: `public void identify()`

**功能**: 鉴定所有物品（游戏结束时调用）

**实现逻辑**:
```java
public void identify() {
    for (Item item : this) {
        item.identify(false);  // 不触发 Catalog 记录
    }
}
```

**注意**: 忽略 lostInventory 状态，因为这是游戏结束时调用的

---

### observe()

**签名**: `public void observe()`

**功能**: 观察装备物品，触发鉴定过程

**实现逻辑**:
```java
public void observe() {
    // 处理主武器
    if (weapon() != null) {
        if (ShardOfOblivion.passiveIDDisabled() && weapon() instanceof Weapon) {
            ((Weapon) weapon()).setIDReady();  // 遗忘碎片：标记为"准备鉴定"
        } else {
            weapon().identify();
            Badges.validateItemLevelAquired(weapon());
        }
    }
    // ... 其他装备槽同理

    // 遗忘碎片提示
    if (ShardOfOblivion.passiveIDDisabled()) {
        GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready_worn"));
    }

    // 背包中可装备物品：揭示诅咒状态
    for (Item item : backpack) {
        if (item instanceof EquipableItem || item instanceof Wand) {
            item.cursedKnown = true;
        }
    }
    Item.updateQuickslot();
}
```

**鉴定系统说明**:

| 情况 | 行为 |
|------|------|
| 正常情况 | 直接鉴定装备物品 |
| 装备遗忘碎片 | 武器、护甲、戒指标记为"准备鉴定"状态 |
| 神器 | 始终直接鉴定（不受遗忘碎片影响） |
| 背包物品 | 只揭示 EquipableItem 和 Wand 的诅咒状态 |

---

### uncurseEquipped()

**签名**: `public void uncurseEquipped()`

**功能**: 解除所有已装备物品的诅咒

**实现逻辑**:
```java
public void uncurseEquipped() {
    ScrollOfRemoveCurse.uncurse(owner, armor(), weapon(), artifact(), misc(), ring(), secondWep());
}
```

---

### randomUnequipped()

**签名**: `public Item randomUnequipped()`

**功能**: 随机获取背包中的一个物品

**返回值**: Item - 随机物品，或 null

**注意**: 如果英雄有 LostInventory Buff，返回 null

---

### charge()

**签名**: `public int charge(float charge)`

**功能**: 为所有法杖充能

**参数**:
- `charge`: float - 充能数量

**返回值**: int - 被充能的法杖数量

**实现逻辑**:
```java
public int charge(float charge) {
    int count = 0;
    for (Wand.Charger charger : owner.buffs(Wand.Charger.class)) {
        charger.gainCharge(charge);
        count++;
    }
    return count;
}
```

---

### iterator()

**签名**: `@Override public Iterator<Item> iterator()`

**功能**: 实现 Iterable 接口，返回物品迭代器

**返回值**: Iterator&lt;Item&gt; - 新的 ItemIterator 实例

---

## 11. 使用示例

### 遍历所有物品

```java
for (Item item : hero.belongings) {
    // 处理每个物品
    System.out.println(item.name());
}
```

### 查找特定类型物品

```java
// 查找一个法杖
Wand wand = hero.belongings.getItem(Wand.class);

// 查找所有卷轴
ArrayList<Scroll> scrolls = hero.belongings.getAllItems(Scroll.class);
```

### 装备/卸下物品

```java
// 直接访问字段用于装备操作
hero.belongings.weapon = new Sword();
hero.belongings.weapon.activate(hero);

// 使用访问器获取装备（考虑 lostInventory）
KindOfWeapon currentWeapon = hero.belongings.weapon();
```

### 处理攻击武器

```java
// 获取当前攻击武器（考虑投掷/技能武器）
KindOfWeapon attackWep = hero.belongings.attackingWeapon();
int damage = attackWep.damageRoll(hero);
```

### 检查物品是否在背包中

```java
if (hero.belongings.contains(someItem)) {
    // 物品在物品栏中
}
```

---

## 注意事项

### 访问器 vs 直接字段访问

1. **使用访问器方法**（`weapon()`, `armor()` 等）：
   - 战斗计算
   - 属性计算
   - 正常游戏流程

2. **直接访问字段**：
   - 装备/卸下物品
   - UI 界面显示
   - 死亡英雄处理
   - Bundle 序列化

### lostInventory 系统

1. **触发条件**：英雄受到 LostInventory Buff
2. **效果**：大部分物品变为不可用
3. **例外**：`keptThroughLostInventory()` 返回 true 的物品仍可用
4. **缓存机制**：使用 `lostInvent` 字段缓存状态，避免频繁调用 `buff()`

### 装备槽约束

1. **神器和戒指共享 misc 槽**：
   - 最多同时装备 1 神器 + 1 戒指，或 2 神器，或 2 戒指
   - 由 KindofMisc.doEquip() 处理装备逻辑

2. **副武器槽**：
   - 仅冠军（Champion）子职业可用
   - 仍占用一个背包槽位

### 迭代器注意事项

1. 迭代顺序固定：装备槽 → 背包
2. 迭代器支持 `remove()` 操作
3. 迭代时不考虑 lostInventory 状态（访问原始字段）

---

## 最佳实践

### 1. 根据场景选择正确的访问方式

```java
// 正确：战斗计算使用访问器
int damage = hero.belongings.weapon().damageRoll(hero);

// 正确：装备时直接访问字段
hero.belongings.weapon = newSword;
```

### 2. 使用泛型方法查找物品

```java
// 推荐：类型安全
Potion potion = hero.belongings.getItem(Potion.class);

// 不推荐：手动遍历和类型检查
for (Item item : hero.belongings) {
    if (item instanceof Potion) {
        // ...
    }
}
```

### 3. 处理 lostInventory 状态

```java
// 检查物品栏是否丢失
if (hero.belongings.lostInventory()) {
    // 特殊处理
}

// 使用 keptThroughLostInventory 检查特定物品
if (item.keptThroughLostInventory()) {
    // 该物品在 lostInventory 状态下仍可用
}
```

### 4. 使用 observe() 触发鉴定

```java
// 在英雄拾取或装备新物品时调用
hero.belongings.observe();
```

### 5. 正确使用 attackingWeapon()

```java
// 攻击时使用 attackingWeapon() 而非 weapon()
KindOfWeapon wep = hero.belongings.attackingWeapon();
// 这确保了投掷武器和技能武器的正确处理
```