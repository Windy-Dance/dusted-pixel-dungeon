# Scroll 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/Scroll.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.scrolls |
| 类类型 | abstract class |
| 继承关系 | extends Item |
| 代码行数 | 373 |

## 2. 类职责说明
Scroll 是所有卷轴物品的抽象基类，定义了卷轴的通用行为：阅读、符文识别、鉴定状态管理等。它实现了卷轴的核心机制，包括符文与类型的随机对应关系、阅读动画、以及将卷轴转化为符石的配方系统。所有具体卷轴都继承此类并实现 doRead() 方法。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Item {
        +stackable: boolean
        +defaultAction: String
        +identify(boolean): Item
        +isIdentified(): boolean
        +value(): int
    }
    
    class Scroll {
        <<abstract>>
        +AC_READ: String
        +TIME_TO_READ: float
        -runes: LinkedHashMap~String,Integer~
        #handler: ItemStatusHandler~Scroll~
        #rune: String
        +talentFactor: float
        +talentChance: float
        -anonymous: boolean
        +initLabels(): void
        +clearLabels(): void
        +save(Bundle): void
        +restore(Bundle): void
        +reset(): void
        +actions(Hero): ArrayList~String~
        +execute(Hero, String): void
        +doRead()*: void
        +readAnimation(): void
        +isKnown(): boolean
        +setKnown(): void
        +identify(boolean): Item
        +value(): int
        +energyVal(): int
    }
    
    class PlaceHolder {
        +isSimilar(Item): boolean
        +doRead(): void
        +info(): String
    }
    
    class ScrollToStone {
        +stones: HashMap~Class,Class~
        +testIngredients(ArrayList~Item~): boolean
        +cost(ArrayList~Item~): int
        +brew(ArrayList~Item~): Item
        +sampleOutput(ArrayList~Item~): Item
    }
    
    Item <|-- Scroll
    Scroll +-- PlaceHolder
    Scroll +-- ScrollToStone
    
    note for Scroll "所有卷轴的抽象基类\n通过符文识别类型\n可转化为符石\n实现 doRead() 方法"
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_READ | String | "READ" | 阅读卷轴的动作标识 |
| TIME_TO_READ | float | 1f | 阅读卷轴所需的时间（回合） |
| runes | LinkedHashMap<String, Integer> | 12种符文映射 | 符文名称到精灵图索引的映射 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| handler | ItemStatusHandler<Scroll> | protected static | 物品状态处理器，管理卷轴符文和鉴定状态 |
| rune | String | protected | 卷轴符文名称 |
| talentFactor | float | public | 天赋触发强度系数，默认1 |
| talentChance | float | public | 天赋触发概率，默认1 |
| anonymous | boolean | protected | 是否为匿名卷轴（不记录鉴定状态） |
| stackable | boolean | (初始化块) | 设为true，卷轴可堆叠 |
| defaultAction | String | (初始化块) | 默认动作为AC_READ |

## 7. 方法详解

### initLabels()
**签名**: `public static void initLabels()`
**功能**: 初始化卷轴符文处理器，建立卷轴类型与符文的随机对应关系
**实现逻辑**:
```java
// 第105-107行
handler = new ItemStatusHandler<>(
    (Class<? extends Scroll>[])Generator.Category.SCROLL.classes, 
    runes
);
```
- 创建 ItemStatusHandler 实例
- 传入卷轴类型数组和符文映射
- 每局游戏卷轴符文随机分配

### execute(Hero hero, String action)
**签名**: `@Override public void execute(Hero hero, String action)`
**功能**: 执行指定动作
**参数**:
- hero: Hero - 执行动作的英雄
- action: String - 动作名称
**实现逻辑**:
```java
// 第173-192行
super.execute(hero, action);

if (action.equals(AC_READ)) {
    // 检查是否被魔法免疫
    if (hero.buff(MagicImmune.class) != null) {
        GLog.w(Messages.get(this, "no_magic"));
    } 
    // 检查是否失明
    else if (hero.buff(Blindness.class) != null) {
        GLog.w(Messages.get(this, "blinded"));
    } 
    // 检查不稳定的法术书诅咒
    else if (hero.buff(UnstableSpellbook.bookRecharge.class) != null
            && hero.buff(UnstableSpellbook.bookRecharge.class).isCursed()
            && !(this instanceof ScrollOfRemoveCurse || this instanceof ScrollOfAntiMagic)) {
        GLog.n(Messages.get(this, "cursed"));
    } 
    else {
        doRead(); // 执行阅读
    }
}
```
- 检查各种阻止阅读的状态
- 通过检查后调用 doRead()

### doRead()
**签名**: `public abstract void doRead()`
**功能**: 执行阅读卷轴的核心逻辑
**说明**: 抽象方法，由子类实现具体效果

### readAnimation()
**签名**: `public void readAnimation()`
**功能**: 执行阅读动画和通用后处理
**实现逻辑**:
```java
// 第196-209行
Invisibility.dispel();                // 解除隐形
curUser.spend(TIME_TO_READ);          // 消耗时间
curUser.busy();                       // 标记忙碌
((HeroSprite)curUser.sprite).read();  // 播放阅读动画

if (!anonymous) {
    Catalog.countUse(getClass());     // 记录使用次数
}
if (Random.Float() < talentChance) {
    // 触发天赋效果
    Talent.onScrollUsed(curUser, curUser.pos, talentFactor, getClass());
}
```

### ScrollToStone.brew(ArrayList<Item> ingredients)
**签名**: `@Override public Item brew(ArrayList<Item> ingredients)`
**功能**: 将卷轴转化为符石
**参数**:
- ingredients: ArrayList<Item> - 材料（1个卷轴）
**返回值**: Item - 2个对应的符石
**实现逻辑**:
```java
// 第343-358行
if (!testIngredients(ingredients)) return null;

Scroll s = (Scroll) ingredients.get(0);

// 消耗卷轴
s.quantity(s.quantity() - 1);

// 如果在炼金场景，显示鉴定提示
if (ShatteredPixelDungeon.scene() instanceof AlchemyScene) {
    if (!s.isIdentified()) {
        ((AlchemyScene) ShatteredPixelDungeon.scene()).showIdentify(s);
    }
} else {
    s.identify();
}

// 返回2个对应的符石
return Reflection.newInstance(stones.get(s.getClass())).quantity(2);
```

## 11. 使用示例

### 使用卷轴
```java
// 创建并使用卷轴
ScrollOfUpgrade scroll = new ScrollOfUpgrade();
scroll.execute(hero, Scroll.AC_READ);
// 效果：打开物品选择界面
```

### 卷轴转符石
```java
// 卷轴到符石的映射
Scroll.ScrollToStone recipe = new Scroll.ScrollToStone();
ArrayList<Item> ingredients = new ArrayList<>();
ingredients.add(new ScrollOfUpgrade());

Item result = recipe.brew(ingredients);
// 结果：2个附魔符石（StoneOfEnchantment）
```

## 注意事项

1. **符文随机性**: 每局游戏卷轴符文与类型对应关系随机

2. **阅读限制**:
   - 魔法免疫状态无法阅读
   - 失明状态无法阅读
   - 某些诅咒状态限制阅读

3. **匿名卷轴**: 用于UI显示或纯效果生成，不影响鉴定记录

4. **天赋触发**: talentFactor 和 talentChance 控制天赋效果

## 最佳实践

1. 创建新卷轴时继承 Scroll 并实现 doRead() 方法

2. 需要选择物品的卷轴继承 InventoryScroll

3. 在 ScrollToStone.stones 中添加卷轴到符石的映射