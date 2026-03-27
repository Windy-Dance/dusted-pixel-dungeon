# Generator 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\items\Generator.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items |
| 类类型 | class (工具类) |
| 继承关系 | 无显式继承 (继承自 java.lang.Object) |
| 代码行数 | 979 行 |

## 2. 类职责说明

Generator 是 Shattered Pixel Dungeon 的核心物品生成系统，负责游戏中所有物品的随机生成。该类实现了一套独特的**牌组概率系统（Deck-based Probability System）**，确保物品掉落在长期内保持均衡分布，避免短期内的极端随机性。系统通过维护可消耗的概率"卡牌"，在玩家获得特定物品后降低其再次出现的概率，直到所有概率归零后重置，从而实现公平且可控的掉落体验。

## 4. 继承与协作关系

```mermaid
classDiagram
    class Generator {
        -float[][] floorSetTierProbs
        -boolean usingFirstDeck
        -HashMap~Category,Float~ defaultCatProbs
        -HashMap~Category,Float~ categoryProbs
        +fullReset()
        +generalReset()
        +reset(Category)
        +undoDrop(Item)
        +undoDrop(Class)
        +random() Item
        +random(Category) Item
        +randomUsingDefaults() Item
        +randomUsingDefaults(Category) Item
        +random(Class) Item
        +randomArmor() Armor
        +randomArmor(int) Armor
        +randomWeapon() MeleeWeapon
        +randomWeapon(int) MeleeWeapon
        +randomWeapon(boolean) MeleeWeapon
        +randomWeapon(int, boolean) MeleeWeapon
        +randomMissile() MissileWeapon
        +randomMissile(int) MissileWeapon
        +randomMissile(boolean) MissileWeapon
        +randomMissile(int, boolean) MissileWeapon
        +randomArtifact() Artifact
        +removeArtifact(Class) boolean
        +storeInBundle(Bundle)
        +restoreFromBundle(Bundle)
    }
    
    class Category {
        <<enumeration>>
        +Class[] classes
        +float[] probs
        +float[] defaultProbs
        +float[] defaultProbs2
        +boolean using2ndProbs
        +float[] defaultProbsTotal
        +Long seed
        +int dropped
        +float firstProb
        +float secondProb
        +Class superClass
        +order(Item) int
    }
    
    Generator +-- Category : 包含
    
    note for Category "TRINKET, WEAPON, WEP_T1-T5, ARMOR\nMISSILE, MIS_T1-T5, WAND, RING\nARTIFACT, FOOD, POTION, SEED\nSCROLL, STONE, GOLD"
```

## 内部枚举 Category

Category 枚举定义了游戏中所有物品的分类，每个类别都有独立的行为配置和物品列表。

### 枚举值一览

| 枚举值 | 父类类型 | 第一副牌概率 | 第二副牌概率 | 说明 |
|--------|----------|-------------|-------------|------|
| TRINKET | Trinket.class | 0 | 0 | 饰品类物品 |
| WEAPON | MeleeWeapon.class | 2 | 2 | 近战武器（由子层级生成） |
| WEP_T1 | MeleeWeapon.class | 0 | 0 | 1阶近战武器 |
| WEP_T2 | MeleeWeapon.class | 0 | 0 | 2阶近战武器 |
| WEP_T3 | MeleeWeapon.class | 0 | 0 | 3阶近战武器 |
| WEP_T4 | MeleeWeapon.class | 0 | 0 | 4阶近战武器 |
| WEP_T5 | MeleeWeapon.class | 0 | 0 | 5阶近战武器 |
| ARMOR | Armor.class | 2 | 1 | 护甲 |
| MISSILE | MissileWeapon.class | 1 | 2 | 投掷武器（由子层级生成） |
| MIS_T1 | MissileWeapon.class | 0 | 0 | 1阶投掷武器 |
| MIS_T2 | MissileWeapon.class | 0 | 0 | 2阶投掷武器 |
| MIS_T3 | MissileWeapon.class | 0 | 0 | 3阶投掷武器 |
| MIS_T4 | MissileWeapon.class | 0 | 0 | 4阶投掷武器 |
| MIS_T5 | MissileWeapon.class | 0 | 0 | 5阶投掷武器 |
| WAND | Wand.class | 1 | 1 | 法杖 |
| RING | Ring.class | 1 | 0 | 戒指 |
| ARTIFACT | Artifact.class | 0 | 1 | 神器（唯一性物品） |
| FOOD | Food.class | 0 | 0 | 食物 |
| POTION | Potion.class | 8 | 8 | 药水 |
| SEED | Plant.Seed.class | 1 | 1 | 种子 |
| SCROLL | Scroll.class | 8 | 8 | 卷轴 |
| STONE | Runestone.class | 1 | 1 | 符文石 |
| GOLD | Gold.class | 10 | 10 | 金币 |

### 牌组概率系统详解

Category 枚举实现了**双牌组概率系统**，核心字段说明：

```
┌─────────────────────────────────────────────────────────────────┐
│                     牌组概率系统工作流程                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  defaultProbs ─────► 初始概率数组（第一副牌）                      │
│        │                                                        │
│        ▼                                                        │
│  probs ────────────► 当前概率数组（随抽取递减）                    │
│        │                                                        │
│        │ 概率归零时                                               │
│        ▼                                                        │
│  defaultProbs2 ────► 第二副牌（某些类别使用）                      │
│        │                                                        │
│        ▼                                                        │
│  reset() ──────────► 重置 probs 数组                            │
│                                                                 │
│  特殊规则：                                                      │
│  • ARTIFACT: 概率不重置，保证全局唯一性                           │
│  • SEED: 主要来源是草地，使用默认概率                             │
│  • 使用种子保证确定性生成的一致性                                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**关键字段：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| classes | Class<?>[] | 该类别下所有物品类数组 |
| probs | float[] | 当前概率数组，随抽取递减 |
| defaultProbs | float[] | 默认概率数组（第一副牌），null表示不使用牌组系统 |
| defaultProbs2 | float[] | 第二副牌的默认概率，用于交替机制 |
| using2ndProbs | boolean | 是否正在使用第二副牌 |
| defaultProbsTotal | float[] | 两副牌概率之和，用于非牌组生成 |
| seed | Long | 随机种子，保证确定性生成的一致性 |
| dropped | int | 已掉落计数，用于同步随机状态 |

### 子排序系统 (subOrderings)

某些类别需要特定的物品排序规则：

```java
Trinket.class    → [Trinket.class, TrinketCatalyst.class]
MissileWeapon.class → [MissileWeapon.class, Bomb.class]
Potion.class     → [Waterskin, Potion, ExoticPotion, Brew, Elixir, LiquidMetal]
Scroll.class     → [Scroll, ExoticScroll, Spell, ArcaneResin]
```

## 静态字段表

| 字段名 | 类型 | 说明 |
|--------|------|------|
| floorSetTierProbs | float[][] | 层级阶段到阶位的概率映射矩阵 |
| usingFirstDeck | boolean | 是否正在使用第一副主牌组 |
| defaultCatProbs | HashMap<Category,Float> | 类别默认总概率 |
| categoryProbs | HashMap<Category,Float> | 类别当前概率 |
| wepTiers | Category[] | 武器阶位数组 [WEP_T1-T5] |
| misTiers | Category[] | 投掷武器阶位数组 [MIS_T1-T5] |

## 层级阶位概率系统 (floorSetTierProbs)

```
floorSetTierProbs[楼层阶段][阶位] = 概率百分比

              阶位1  阶位2  阶位3  阶位4  阶位5
阶段0(1-5层):   0%    75%    20%     4%     1%
阶段1(6-10层):  0%    25%    50%    20%     5%
阶段2(11-15层): 0%     0%    40%    50%    10%
阶段3(16-20层): 0%     0%    20%    40%    40%
阶段4(21-25层): 0%     0%     0%    20%    80%
```

**设计意图：**
- 深层区域倾向于生成更高阶位的装备
- 保证玩家装备随进度升级
- 低层不会出现高阶武器，高层可能出现低阶武器但概率极低

## 7. 方法详解

### random()
**签名**: `public static Item random()`
**功能**: 从主类别牌组中随机抽取一个物品类别并生成物品
**返回值**: Item - 生成的物品实例

**实现逻辑**:
```java
1. 使用 Random.chances(categoryProbs) 按概率选择类别
2. 如果所有类别概率都为0（牌组耗尽）：
   a. 切换到另一副牌组 (usingFirstDeck = !usingFirstDeck)
   b. 调用 generalReset() 重置类别概率
   c. 重新选择类别
3. 将选中类别的概率减1
4. 特殊处理 SEED 类别（使用默认概率）
5. 调用 random(cat) 生成具体物品
```

### random(Category cat)
**签名**: `public static Item random(Category cat)`
**功能**: 从指定类别中随机生成一个物品
**参数**:
- cat: Category - 物品类别

**返回值**: Item - 生成的物品实例

**实现逻辑**:
```java
switch (cat) {
    case ARMOR:   → 调用 randomArmor()
    case WEAPON:  → 调用 randomWeapon()
    case MISSILE: → 调用 randomMissile()
    case ARTIFACT: 
        → 调用 randomArtifact()
        → 如果返回null（无可用神器），返回随机戒指
    default:
        → 使用种子同步确定性生成（如果适用）
        → Random.chances(cat.probs) 选择具体物品
        → 概率归零时 reset(cat) 重置牌组
        → 减少选中物品的概率
        → 处理异域药剂/卷轴转换
        → 反射创建实例并调用 .random() 初始化
}
```

### randomUsingDefaults()
**签名**: `public static Item randomUsingDefaults()`
**功能**: 使用默认概率（忽略牌组消耗状态）生成物品
**返回值**: Item - 生成的物品实例

### randomUsingDefaults(Category cat)
**签名**: `public static Item randomUsingDefaults(Category cat)`
**功能**: 使用默认概率从指定类别生成物品（神器除外）
**参数**:
- cat: Category - 物品类别

**返回值**: Item - 生成的物品实例

**实现逻辑**:
```java
if (cat == Category.WEAPON) → randomWeapon(true)
else if (cat == Category.MISSILE) → randomMissile(true)
else if (无牌组系统 || ARTIFACT) → random(cat)  // 神器必须使用牌组
else if (有第二副牌) → 使用 defaultProbsTotal
else → 使用 defaultProbs
```

### random(Class<? extends Item> cl)
**签名**: `public static Item random(Class<? extends Item> cl)`
**功能**: 直接创建指定类别的物品实例
**参数**:
- cl: Class<? extends Item> - 物品类

**返回值**: Item - 生成的物品实例

### randomArmor()
**签名**: `public static Armor randomArmor()`
**功能**: 根据当前楼层生成随机护甲
**返回值**: Armor - 生成的护甲实例

### randomArmor(int floorSet)
**签名**: `public static Armor randomArmor(int floorSet)`
**功能**: 根据楼层阶段生成随机护甲
**参数**:
- floorSet: int - 楼层阶段（0-4，对应每5层一个阶段）

**返回值**: Armor - 生成的护甲实例

**实现逻辑**:
```java
1. 使用 GameMath.gate() 将 floorSet 限制在有效范围 [0, 4]
2. 从 floorSetTierProbs[floorSet] 获取阶位概率
3. Random.chances() 选择护甲阶位
4. 反射创建护甲实例并调用 .random() 初始化
```

### randomWeapon()
**签名**: `public static MeleeWeapon randomWeapon()`
**功能**: 根据当前楼层生成随机近战武器
**返回值**: MeleeWeapon - 生成的武器实例

### randomWeapon(int floorSet)
**签名**: `public static MeleeWeapon randomWeapon(int floorSet)`
**功能**: 根据楼层阶段生成随机近战武器
**参数**:
- floorSet: int - 楼层阶段（0-4）

**返回值**: MeleeWeapon - 生成的武器实例

### randomWeapon(boolean useDefaults)
**签名**: `public static MeleeWeapon randomWeapon(boolean useDefaults)`
**功能**: 根据当前楼层生成随机近战武器
**参数**:
- useDefaults: boolean - 是否使用默认概率

**返回值**: MeleeWeapon - 生成的武器实例

### randomWeapon(int floorSet, boolean useDefaults)
**签名**: `public static MeleeWeapon randomWeapon(int floorSet, boolean useDefaults)`
**功能**: 根据楼层阶段和概率模式生成随机近战武器
**参数**:
- floorSet: int - 楼层阶段（0-4）
- useDefaults: boolean - 是否使用默认概率

**返回值**: MeleeWeapon - 生成的武器实例

**实现逻辑**:
```java
1. 限制 floorSet 到有效范围
2. 从 floorSetTierProbs[floorSet] 选择阶位
3. 根据 useDefaults 决定：
   - true: 调用 randomUsingDefaults(wepTier)
   - false: 调用 random(wepTier)
```

### randomMissile()
**签名**: `public static MissileWeapon randomMissile()`
**功能**: 根据当前楼层生成随机投掷武器
**返回值**: MissileWeapon - 生成的投掷武器实例

### randomMissile(int floorSet)
**签名**: `public static MissileWeapon randomMissile(int floorSet)`
**功能**: 根据楼层阶段生成随机投掷武器
**参数**:
- floorSet: int - 楼层阶段（0-4）

**返回值**: MissileWeapon - 生成的投掷武器实例

### randomMissile(boolean useDefaults)
**签名**: `public static MissileWeapon randomMissile(boolean useDefaults)`
**功能**: 根据当前楼层生成随机投掷武器
**参数**:
- useDefaults: boolean - 是否使用默认概率

**返回值**: MissileWeapon - 生成的投掷武器实例

### randomMissile(int floorSet, boolean useDefaults)
**签名**: `public static MissileWeapon randomMissile(int floorSet, boolean useDefaults)`
**功能**: 根据楼层阶段和概率模式生成随机投掷武器
**参数**:
- floorSet: int - 楼层阶段（0-4）
- useDefaults: boolean - 是否使用默认概率

**返回值**: MissileWeapon - 生成的投掷武器实例

### randomArtifact()
**签名**: `public static Artifact randomArtifact()`
**功能**: 随机生成一个神器（保证全局唯一性）
**返回值**: Artifact - 生成的神器实例，如果所有神器已被获取则返回null

**实现逻辑（神器唯一性保证）**:
```java
1. 使用种子同步确定性生成
2. Random.chances(cat.probs) 选择神器
3. 如果所有概率为0（i == -1），返回 null
4. 减少选中神器的概率（且永不重置）
5. 创建并初始化神器实例
```

**重要说明**：
- 神器是全局唯一物品，获取后不会再出现
- Artifact 类别的 probs 不会重置
- 当所有神器都被获取后，返回 null，调用者应返回戒指替代

### removeArtifact(Class<? extends Artifact> artifact)
**签名**: `public static boolean removeArtifact(Class<? extends Artifact> artifact)`
**功能**: 从可用神器池中移除指定神器
**参数**:
- artifact: Class<? extends Artifact> - 要移除的神器类

**返回值**: boolean - 是否成功移除

**实现逻辑**:
```java
遍历 ARTIFACT 类别的 classes 数组：
    如果找到匹配类且概率 > 0：
        将概率设为 0
        返回 true
返回 false
```

### undoDrop(Item item) / undoDrop(Class cls)
**签名**: 
- `public static void undoDrop(Item item)`
- `public static void undoDrop(Class cls)`

**功能**: 撤销物品掉落对概率的影响（相当于将卡牌放回牌组）
**参数**:
- item: Item - 要撤销的物品
- cls: Class - 要撤销的物品类

**实现逻辑**:
```java
遍历所有 Category：
    如果类匹配 superClass 且有牌组系统：
        找到对应的 classes 索引
        probs[i]++  // 概率+1
```

### fullReset()
**签名**: `public static void fullReset()`
**功能**: 完全重置生成器状态（新游戏开始时调用）

**实现逻辑**:
```java
1. 随机选择使用第一副或第二副主牌组
2. 调用 generalReset() 重置类别概率
3. 对每个有牌组系统的类别：
   a. 随机选择初始使用哪副牌
   b. 调用 reset(cat) 重置概率数组
   c. 生成新的随机种子
   d. 重置 dropped 计数器
```

### generalReset()
**签名**: `public static void generalReset()`
**功能**: 重置类别概率到当前牌组的初始值

### reset(Category cat)
**签名**: `public static void reset(Category cat)`
**功能**: 重置指定类别的概率数组
**参数**:
- cat: Category - 要重置的类别

**实现逻辑**:
```java
if (有第二副牌 defaultProbs2) {
    切换使用的牌组 (using2ndProbs = !using2ndProbs)
    根据切换结果克隆对应默认概率数组
} else {
    克隆 defaultProbs 到 probs
}
```

### storeInBundle(Bundle bundle)
**签名**: `public static void storeInBundle(Bundle bundle)`
**功能**: 将生成器状态保存到 Bundle（游戏存档时调用）
**参数**:
- bundle: Bundle - 存储容器

**保存内容**:
| 键名 | 内容 |
|------|------|
| FIRST_DECK | 是否使用第一副牌 |
| GENERAL_PROBS | 类别概率数组 |
| [类别名]_probs | 各类别的概率数组 |
| [类别名]_using_probs2 | 是否使用第二副牌 |
| [类别名]_seed | 随机种子 |
| [类别名]_dropped | 已掉落计数 |

### restoreFromBundle(Bundle bundle)
**签名**: `public static void restoreFromBundle(Bundle bundle)`
**功能**: 从 Bundle 恢复生成器状态（游戏读档时调用）
**参数**:
- bundle: Bundle - 存储容器

**实现逻辑**:
```java
1. 调用 fullReset() 初始化
2. 读取 FIRST_DECK 标志
3. 读取类别概率数组
4. 对每个类别：
   a. 读取 probs 数组
   b. 读取 using2ndProbs 标志
   c. 读取 seed 和 dropped
   d. 处理旧版本存档兼容性（神器类）
```

## 各类别物品详细列表

### 药水类别 (POTION)
| 类名 | 中文名 | 默认概率1 | 默认概率2 |
|------|--------|----------|----------|
| PotionOfStrength | 力量药水 | 0 | 0 |
| PotionOfHealing | 治疗药水 | 3 | 3 |
| PotionOfMindVision | 心眼药水 | 2 | 2 |
| PotionOfFrost | 冰霜药水 | 1 | 2 |
| PotionOfLiquidFlame | 火焰药水 | 2 | 1 |
| PotionOfToxicGas | 毒气药水 | 1 | 2 |
| PotionOfHaste | 加速药水 | 1 | 1 |
| PotionOfInvisibility | 隐身药水 | 1 | 1 |
| PotionOfLevitation | 漂浮药水 | 1 | 1 |
| PotionOfParalyticGas | 麻痹药水 | 1 | 1 |
| PotionOfPurity | 净化药水 | 1 | 1 |
| PotionOfExperience | 经验药水 | 1 | 0 |

### 种子类别 (SEED)
| 类名 | 中文名 | 默认概率 |
|------|--------|---------|
| Rotberry.Seed | 腐莓种子 | 0 |
| Sungrass.Seed | 太阳草种子 | 2 |
| Fadeleaf.Seed | 隐现叶种子 | 2 |
| Icecap.Seed | 冰冠花种子 | 2 |
| Firebloom.Seed | 火焰花种子 | 2 |
| Sorrowmoss.Seed | 悲伤苔种子 | 2 |
| Swiftthistle.Seed | 迅捷蓟种子 | 2 |
| Blindweed.Seed | 盲目草种子 | 2 |
| Stormvine.Seed | 风暴藤种子 | 2 |
| Earthroot.Seed | 地根草种子 | 2 |
| Mageroyal.Seed | 法师花种子 | 2 |
| Starflower.Seed | 星花种子 | 1 |

### 卷轴类别 (SCROLL)
| 类名 | 中文名 | 默认概率1 | 默认概率2 |
|------|--------|----------|----------|
| ScrollOfUpgrade | 升级卷轴 | 0 | 0 |
| ScrollOfIdentify | 鉴定卷轴 | 3 | 3 |
| ScrollOfRemoveCurse | 解咒卷轴 | 2 | 2 |
| ScrollOfMirrorImage | 镜像卷轴 | 1 | 2 |
| ScrollOfRecharging | 充能卷轴 | 2 | 1 |
| ScrollOfTeleportation | 传送卷轴 | 1 | 2 |
| ScrollOfLullaby | 摇篮曲卷轴 | 1 | 1 |
| ScrollOfMagicMapping | 魔法地图卷轴 | 1 | 1 |
| ScrollOfRage | 狂暴卷轴 | 1 | 1 |
| ScrollOfRetribution | 惩戒卷轴 | 1 | 1 |
| ScrollOfTerror | 恐惧卷轴 | 1 | 1 |
| ScrollOfTransmutation | 转化卷轴 | 1 | 0 |

### 符文石类别 (STONE)
| 类名 | 中文名 | 默认概率 |
|------|--------|---------|
| StoneOfEnchantment | 附魔石 | 0 |
| StoneOfIntuition | 直觉石 | 2 |
| StoneOfDetectMagic | 魔法检测石 | 2 |
| StoneOfFlock | 群鸟石 | 2 |
| StoneOfShock | 震击石 | 2 |
| StoneOfBlink | 闪烁石 | 2 |
| StoneOfDeepSleep | 深眠石 | 2 |
| StoneOfClairvoyance | 透视石 | 2 |
| StoneOfAggression | 挑衅石 | 2 |
| StoneOfBlast | 爆裂石 | 2 |
| StoneOfFear | 恐惧石 | 2 |
| StoneOfAugmentation | 强化石 | 0 |

### 法杖类别 (WAND)
| 类名 | 中文名 | 默认概率 |
|------|--------|---------|
| WandOfMagicMissile | 魔弹法杖 | 3 |
| WandOfLightning | 闪电法杖 | 3 |
| WandOfDisintegration | 解离法杖 | 3 |
| WandOfFireblast | 火焰冲击法杖 | 3 |
| WandOfCorrosion | 腐蚀法杖 | 3 |
| WandOfBlastWave | 冲击波法杖 | 3 |
| WandOfLivingEarth | 活土法杖 | 3 |
| WandOfFrost | 冰霜法杖 | 3 |
| WandOfPrismaticLight | 棱光法杖 | 3 |
| WandOfWarding | 守卫法杖 | 3 |
| WandOfTransfusion | 输血法杖 | 3 |
| WandOfCorruption | 腐化法杖 | 3 |
| WandOfRegrowth | 再生法杖 | 3 |

### 戒指类别 (RING)
| 类名 | 中文名 | 默认概率 |
|------|--------|---------|
| RingOfAccuracy | 命中之戒 | 3 |
| RingOfArcana | 奥术之戒 | 3 |
| RingOfElements | 元素之戒 | 3 |
| RingOfEnergy | 能量之戒 | 3 |
| RingOfEvasion | 闪避之戒 | 3 |
| RingOfForce | 力量之戒 | 3 |
| RingOfFuror | 狂暴之戒 | 3 |
| RingOfHaste | 急速之戒 | 3 |
| RingOfMight | 威力之戒 | 3 |
| RingOfSharpshooting | 神射之戒 | 3 |
| RingOfTenacity | 坚韧之戒 | 3 |
| RingOfWealth | 财富之戒 | 3 |

### 神器类别 (ARTIFACT)
| 类名 | 中文名 | 默认概率 | 备注 |
|------|--------|---------|------|
| AlchemistsToolkit | 炼金术士工具包 | 1 | |
| ChaliceOfBlood | 鲜血圣杯 | 1 | |
| CloakOfShadows | 暗影斗篷 | 0 | 盗贼专属 |
| DriedRose | 枯萎玫瑰 | 1 | |
| EtherealChains | 虚灵锁链 | 1 | |
| HolyTome | 神圣典籍 | 0 | 牧师专属 |
| HornOfPlenty | 丰收号角 | 1 | |
| MasterThievesArmband | 盗贼大师臂章 | 1 | |
| SandalsOfNature | 自然之履 | 1 | |
| SkeletonKey | 骷髅钥匙 | 1 | |
| TalismanOfForesight | 预知护符 | 1 | |
| TimekeepersHourglass | 守时沙漏 | 1 | |
| UnstableSpellbook | 不稳定法术书 | 1 | |

### 饰品类别 (TRINKET)
| 类名 | 中文名 | 默认概率 |
|------|--------|---------|
| RatSkull | 鼠骨 | 1 |
| ParchmentScrap | 羊皮纸片 | 1 |
| PetrifiedSeed | 石化种子 | 1 |
| ExoticCrystals | 异域水晶 | 1 |
| MossyClump | 苔藓块 | 1 |
| DimensionalSundial | 维度日晷 | 1 |
| ThirteenLeafClover | 十三叶草 | 1 |
| TrapMechanism | 陷阱机关 | 1 |
| MimicTooth | 宝箱怪牙齿 | 1 |
| WondrousResin | 奇妙树脂 | 1 |
| EyeOfNewt | 蝾螈之眼 | 1 |
| SaltCube | 盐块 | 1 |
| VialOfBlood | 血瓶 | 1 |
| ShardOfOblivion | 遗忘碎片 | 1 |
| ChaoticCenser | 混沌香炉 | 1 |
| FerretTuft | 雪貂毛簇 | 1 |
| CrackedSpyglass | 裂纹望远镜 | 1 |

### 护甲类别 (ARMOR)
| 类名 | 中文名 | 阶位 | 概率 |
|------|--------|------|------|
| ClothArmor | 布甲 | 0 | 1 |
| LeatherArmor | 皮甲 | 1 | 1 |
| MailArmor | 锁子甲 | 2 | 1 |
| ScaleArmor | 鳞甲 | 3 | 1 |
| PlateArmor | 板甲 | 4 | 1 |
| WarriorArmor | 战士护甲 | - | 0 |
| MageArmor | 法师护甲 | - | 0 |
| RogueArmor | 盗贼护甲 | - | 0 |
| HuntressArmor | 猎人护甲 | - | 0 |
| DuelistArmor | 决斗者护甲 | - | 0 |
| ClericArmor | 牧师护甲 | - | 0 |

### 近战武器类别 (WEP_T1 - WEP_T5)

#### 一阶武器 (WEP_T1)
| 类名 | 中文名 | 默认概率 |
|------|--------|---------|
| WornShortsword | 磨损短剑 | 2 |
| MagesStaff | 法师法杖 | 0 |
| Dagger | 匕首 | 2 |
| Gloves | 拳套 | 2 |
| Rapier | 细剑 | 2 |
| Cudgel | 短棍 | 2 |

#### 二阶武器 (WEP_T2)
| 类名 | 中文名 | 默认概率 |
|------|--------|---------|
| Shortsword | 短剑 | 2 |
| HandAxe | 手斧 | 2 |
| Spear | 长矛 | 2 |
| Quarterstaff | 铁杖 | 2 |
| Dirk | 短匕首 | 2 |
| Sickle | 镰刀 | 2 |
| Pickaxe | 十字镐 | 0 |

#### 三阶武器 (WEP_T3)
| 类名 | 中文名 | 默认概率 |
|------|--------|---------|
| Sword | 长剑 | 2 |
| Mace | 锤矛 | 2 |
| Scimitar | 弯刀 | 2 |
| RoundShield | 圆盾 | 2 |
| Sai | 双叉戟 | 2 |
| Whip | 鞭子 | 2 |

#### 四阶武器 (WEP_T4)
| 类名 | 中文名 | 默认概率 |
|------|--------|---------|
| Longsword | 大剑 | 2 |
| BattleAxe | 战斧 | 2 |
| Flail | 连枷 | 2 |
| RunicBlade | 符文刃 | 2 |
| AssassinsBlade | 刺客之刃 | 2 |
| Crossbow | 十字弩 | 2 |
| Katana | 武士刀 | 2 |

#### 五阶武器 (WEP_T5)
| 类名 | 中文名 | 默认概率 |
|------|--------|---------|
| Greatsword | 巨剑 | 2 |
| WarHammer | 战锤 | 2 |
| Glaive | 关刀 | 2 |
| Greataxe | 巨斧 | 2 |
| Greatshield | 塔盾 | 2 |
| Gauntlet | 臂铠 | 2 |
| WarScythe | 战镰 | 2 |

### 投掷武器类别 (MIS_T1 - MIS_T5)

#### 一阶投掷武器 (MIS_T1)
| 类名 | 中文名 | 默认概率 |
|------|--------|---------|
| ThrowingStone | 投石 | 3 |
| ThrowingKnife | 飞刀 | 3 |
| ThrowingSpike | 飞刺 | 3 |
| Dart | 飞镖 | 0 |

#### 二阶投掷武器 (MIS_T2)
| 类名 | 中文名 | 默认概率 |
|------|--------|---------|
| FishingSpear | 渔叉 | 3 |
| ThrowingClub | 投掷棒 | 3 |
| Shuriken | 手里剑 | 3 |

#### 三阶投掷武器 (MIS_T3)
| 类名 | 中文名 | 默认概率 |
|------|--------|---------|
| ThrowingSpear | 投矛 | 3 |
| Kunai | 苦无 | 3 |
| Bolas | 飞球索 | 3 |

#### 四阶投掷武器 (MIS_T4)
| 类名 | 中文名 | 默认概率 |
|------|--------|---------|
| Javelin | 标枪 | 3 |
| Tomahawk | 战斧投矛 | 3 |
| HeavyBoomerang | 重型回旋镖 | 3 |

#### 五阶投掷武器 (MIS_T5)
| 类名 | 中文名 | 默认概率 |
|------|--------|---------|
| Trident | 三叉戟 | 3 |
| ThrowingHammer | 投掷锤 | 3 |
| ForceCube | 力场立方 | 3 |

### 食物类别 (FOOD)
| 类名 | 中文名 | 默认概率 |
|------|--------|---------|
| Food | 口粮 | 4 |
| Pasty | 馅饼 | 1 |
| MysteryMeat | 神秘肉 | 0 |

## 11. 使用示例

### 基本物品生成
```java
// 生成随机物品（使用牌组系统）
Item randomItem = Generator.random();

// 生成特定类别的物品
Potion potion = (Potion) Generator.random(Generator.Category.POTION);
Scroll scroll = (Scroll) Generator.random(Generator.Category.SCROLL);
Wand wand = (Wand) Generator.random(Generator.Category.WAND);

// 使用默认概率生成（忽略牌组消耗状态）
Item item = Generator.randomUsingDefaults(Generator.Category.POTION);
```

### 武器和护甲生成
```java
// 根据当前楼层生成装备
Armor armor = Generator.randomArmor();
MeleeWeapon weapon = Generator.randomWeapon();
MissileWeapon missile = Generator.randomMissile();

// 指定楼层阶段生成（0-4对应每5层）
Armor earlyArmor = Generator.randomArmor(0);  // 1-5层装备
Armor lateArmor = Generator.randomArmor(4);   // 21-25层装备

// 使用默认概率生成武器
MeleeWeapon weapon = Generator.randomWeapon(true);
```

### 神器唯一性处理
```java
// 生成神器（可能返回null）
Artifact artifact = Generator.randomArtifact();
if (artifact == null) {
    // 所有神器已被获取，使用戒指替代
    Ring ring = (Ring) Generator.random(Generator.Category.RING);
}

// 手动移除特定神器（如玩家获得特殊神器）
Generator.removeArtifact(CloakOfShadows.class);
```

### 撤销物品掉落
```java
// 当物品被拾取后又丢弃，可能需要恢复概率
Item dropped = Generator.random(Generator.Category.POTION);
// ... 玩家丢弃物品 ...
Generator.undoDrop(dropped);  // 恢复该物品的出现概率
```

### 游戏存档/读档
```java
// 保存生成器状态
Bundle saveBundle = new Bundle();
Generator.storeInBundle(saveBundle);

// 恢复生成器状态
Generator.restoreFromBundle(saveBundle);
```

## 注意事项

### 神器唯一性
- 神器在整个游戏运行期间保持唯一性
- `ARTIFACT` 类别的 `probs` 数组**永远不会重置**
- 当 `randomArtifact()` 返回 `null` 时，应提供替代物品
- 职业专属神器（暗影斗篷、神圣典籍）初始概率为0

### 牌组系统设计理念
```
┌─────────────────────────────────────────────────────────────────┐
│                    为什么使用牌组系统？                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  传统随机:                                                       │
│  ┌───┐┌───┐┌───┐┌───┐┌───┐┌───┐┌───┐┌───┐┌───┐┌───┐            │
│  │ A ││ B ││ A ││ A ││ C ││ B ││ A ││ D ││ A ││ A │            │
│  └───┘└───┘└───┘└───┘└───┘└───┘└───┘└───┘└───┘└───┘            │
│        ↑ 可能连续出现相同的物品，体验不公平                        │
│                                                                 │
│  牌组系统:                                                       │
│  ┌───┐┌───┐┌───┐┌───┐┌───┐┌───┐┌───┐┌───┐┌───┐┌───┐            │
│  │ A ││ B ││ C ││ D ││ A ││ B ││ C ││ D ││ A ││ B │            │
│  └───┘└───┘└───┘└───┘└───┘└───┘└───┘└───┘└───┘└───┘            │
│        ↑ 保证长期均衡，短期避免极端情况                            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 种子确定性
- `seed` 字段确保在相同游戏种子下，物品生成顺序一致
- 对于确定性生成（如关卡预设物品），系统会保存和恢复种子状态
- 这使得竞技模式和回放系统成为可能

### 双牌组机制
- 药水和卷轴使用双牌组 (`defaultProbs` 和 `defaultProbs2`)
- 两副牌的物品分布略有不同，增加多样性
- 一副牌耗尽后自动切换到另一副

## 最佳实践

### 生成物品时
1. **优先使用类型特定方法**：使用 `randomWeapon()` 而非 `random(Category.WEAPON)`
2. **处理神器null返回**：始终检查 `randomArtifact()` 的返回值
3. **使用正确的楼层阶段**：`floorSet = Dungeon.depth / 5`

### 扩展新物品类型
1. 在对应 Category 的 `classes` 数组中添加新类
2. 在 `defaultProbs` 数组中添加对应概率
3. 考虑是否需要双牌组 (`defaultProbs2`)
4. 如果是神器，确保唯一性处理

### 性能考虑
- 反射创建实例 (`Reflection.newInstance()`) 有轻微开销
- 高频生成场景可考虑对象池
- 牌组重置操作较少，性能影响可忽略

### 存档兼容性
- 新增物品时，考虑旧存档的 `probs` 数组长度
- 参考 `restoreFromBundle()` 中的版本迁移代码
- 添加新物品时更新数组长度检查