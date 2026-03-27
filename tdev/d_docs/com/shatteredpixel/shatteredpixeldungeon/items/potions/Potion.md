# Potion 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/potions/Potion.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.potions |
| 类类型 | class |
| 继承关系 | extends Item |
| 代码行数 | 566 |

## 2. 类职责说明
Potion 是所有药水物品的抽象基类，定义了药水的通用行为：饮用、投掷、颜色识别、鉴定状态管理等。它实现了药水的核心机制，包括通过种子酿造药水的配方系统、药水颜色与类型的随机对应关系、以及不同药水的使用方式（饮用/投掷/选择）。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Item {
        +stackable: boolean
        +defaultAction: String
        +actions(Hero): ArrayList~String~
        +execute(Hero, String): void
        +doThrow(Hero): void
        +onThrow(int): void
        +identify(boolean): Item
        +isIdentified(): boolean
        +isUpgradable(): boolean
        +value(): int
    }
    
    class Potion {
        +AC_DRINK: String
        +AC_CHOOSE: String
        +TIME_TO_DRINK: float
        -colors: LinkedHashMap~String,Integer~
        -mustThrowPots: HashSet~Class~
        -canThrowPots: HashSet~Class~
        -handler: ItemStatusHandler~Potion~
        -color: String
        -talentFactor: float
        -talentChance: float
        -anonymous: boolean
        +initColors(): void
        +clearColors(): void
        +save(Bundle): void
        +saveSelectively(Bundle, ArrayList~Item~): void
        +restore(Bundle): void
        +reset(): void
        +defaultAction(): String
        +actions(Hero): ArrayList~String~
        +execute(Hero, String): void
        +doThrow(Hero): void
        #drink(Hero): void
        #onThrow(int): void
        +apply(Hero): void
        +shatter(int): void
        +cast(Hero, int): void
        +isKnown(): boolean
        +setKnown(): void
        +identify(boolean): Item
        +name(): String
        +info(): String
        +desc(): String
        +isIdentified(): boolean
        +isUpgradable(): boolean
        +getKnown(): HashSet~Class~
        +getUnknown(): HashSet~Class~
        +allKnown(): boolean
        #splashColor(): int
        #splash(int): void
        +value(): int
        +energyVal(): int
    }
    
    class PlaceHolder {
        +isSimilar(Item): boolean
        +info(): String
    }
    
    class SeedToPotion {
        +types: HashMap~Class,Class~
        +testIngredients(ArrayList~Item~): boolean
        +cost(ArrayList~Item~): int
        +brew(ArrayList~Item~): Item
        +sampleOutput(ArrayList~Item~): Item
    }
    
    Item <|-- Potion
    Potion +-- PlaceHolder
    Potion +-- SeedToPotion
    
    note for Potion "所有具体药水类继承此类\n如 PotionOfHealing, PotionOfStrength 等"
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_DRINK | String | "DRINK" | 饮用药水的动作标识 |
| AC_CHOOSE | String | "CHOOSE" | 选择使用方式（饮用或投掷）的动作标识 |
| TIME_TO_DRINK | float | 1f | 饮用药水所需的时间（回合） |
| colors | LinkedHashMap<String, Integer> | 12种颜色映射 | 药水颜色名称到精灵图索引的映射 |
| mustThrowPots | HashSet<Class<? extends Potion>> | 8种药水 | 必须投掷使用的药水类型集合 |
| canThrowPots | HashSet<Class<? extends Potion>> | 4种药水 | 可选择饮用或投掷的药水类型集合 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| handler | ItemStatusHandler<Potion> | protected static | 物品状态处理器，管理药水颜色和鉴定状态 |
| color | String | protected | 药水颜色名称 |
| talentFactor | float | protected | 天赋触发强度系数，默认1 |
| talentChance | float | protected | 天赋触发概率，默认1 |
| anonymous | boolean | protected | 是否为匿名药水（不记录鉴定状态） |
| stackable | boolean | (初始化块) | 设为true，药水可堆叠 |
| defaultAction | String | (初始化块) | 默认动作为AC_DRINK |

## 7. 方法详解

### initColors()
**签名**: `public static void initColors()`
**功能**: 初始化药水颜色处理器，建立药水类型与颜色的随机对应关系
**实现逻辑**:
```java
// 第149-152行
handler = new ItemStatusHandler<>(
    (Class<? extends Potion>[])Generator.Category.POTION.classes, 
    colors
);
```
- 创建 ItemStatusHandler 实例
- 传入药水类型数组和颜色映射
- 每局游戏药水颜色随机分配

### clearColors()
**签名**: `public static void clearColors()`
**功能**: 清除颜色处理器，用于重置游戏状态
**实现逻辑**:
```java
// 第154-156行
handler = null;
```
- 将 handler 设为 null

### save(Bundle bundle)
**签名**: `public static void save(Bundle bundle)`
**功能**: 保存药水颜色和鉴定状态到存档
**实现逻辑**:
```java
// 第158-160行
handler.save(bundle);
```
- 调用 handler 的 save 方法保存状态

### saveSelectively(Bundle bundle, ArrayList<Item> items)
**签名**: `public static void saveSelectively(Bundle bundle, ArrayList<Item> items)`
**功能**: 选择性保存指定物品列表中药水的状态
**实现逻辑**:
```java
// 第162-176行
// 1. 创建类列表
ArrayList<Class<?extends Item>> classes = new ArrayList<>();
// 2. 遍历物品列表
for (Item i : items){
    // 3. 处理异域药水
    if (i instanceof ExoticPotion){
        if (!classes.contains(ExoticPotion.exoToReg.get(i.getClass()))){
            classes.add(ExoticPotion.exoToReg.get(i.getClass()));
        }
    } 
    // 4. 处理普通药水
    else if (i instanceof Potion){
        if (!classes.contains(i.getClass())){
            classes.add(i.getClass());
        }
    }
}
// 5. 保存指定类的状态
handler.saveClassesSelectively(bundle, classes);
```

### restore(Bundle bundle)
**签名**: `public static void restore(Bundle bundle)`
**功能**: 从存档恢复药水颜色和鉴定状态
**实现逻辑**:
```java
// 第178-181行
handler = new ItemStatusHandler<>(
    (Class<? extends Potion>[])Generator.Category.POTION.classes, 
    colors, 
    bundle
);
```
- 从 Bundle 恢复状态

### Potion()
**签名**: `public Potion()`
**功能**: 构造函数，初始化药水
**实现逻辑**:
```java
// 第183-186行
super();
reset(); // 重置颜色和图像
```

### anonymize()
**签名**: `public void anonymize()`
**功能**: 将药水标记为匿名，用于UI显示或纯效果生成
**实现逻辑**:
```java
// 第192-195行
if (!isKnown()) image = ItemSpriteSheet.POTION_HOLDER;
anonymous = true;
```
- 未鉴定的匿名药水使用占位符图标
- 设为匿名后不影响鉴定状态

### reset()
**签名**: `@Override public void reset()`
**功能**: 重置药水的颜色和图像
**实现逻辑**:
```java
// 第198-207行
super.reset();
if (handler != null && handler.contains(this)) {
    // 从handler获取分配的颜色和图像
    image = handler.image(this);
    color = handler.label(this);
} else {
    // 默认使用深红色
    image = ItemSpriteSheet.POTION_CRIMSON;
    color = "crimson";
}
```

### defaultAction()
**签名**: `@Override public String defaultAction()`
**功能**: 返回默认动作，根据药水类型和鉴定状态决定
**实现逻辑**:
```java
// 第210-218行
if (isKnown() && mustThrowPots.contains(this.getClass())) {
    return AC_THROW; // 必须投掷的药水
} else if (isKnown() && canThrowPots.contains(this.getClass())){
    return AC_CHOOSE; // 可选择的药水
} else {
    return AC_DRINK; // 默认饮用
}
```

### actions(Hero hero)
**签名**: `@Override public ArrayList<String> actions(Hero hero)`
**功能**: 返回可执行的动作列表
**实现逻辑**:
```java
// 第221-225行
ArrayList<String> actions = super.actions(hero);
actions.add(AC_DRINK);
return actions;
```
- 在父类动作基础上添加饮用动作

### execute(Hero hero, String action)
**签名**: `@Override public void execute(final Hero hero, String action)`
**功能**: 执行指定动作
**实现逻辑**:
```java
// 第228-259行
super.execute(hero, action);

if (action.equals(AC_CHOOSE)){
    // 显示使用方式选择窗口
    GameScene.show(new WndUseItem(null, this));
    
} else if (action.equals(AC_DRINK)) {
    if (isKnown() && mustThrowPots.contains(getClass())) {
        // 有害药水需确认
        GameScene.show(new WndOptions(...) {
            @Override
            protected void onSelect(int index) {
                if (index == 0) drink(hero);
            }
        });
    } else {
        drink(hero);
    }
}
```

### doThrow(Hero hero)
**签名**: `@Override public void doThrow(final Hero hero)`
**功能**: 处理投掷动作，对有益药水需确认
**实现逻辑**:
```java
// 第262-285行
if (isKnown()
    && !mustThrowPots.contains(this.getClass())
    && !canThrowPots.contains(this.getClass())) {
    // 有益药水投掷需确认
    GameScene.show(new WndOptions(...) {
        @Override
        protected void onSelect(int index) {
            if (index == 0) Potion.super.doThrow(hero);
        }
    });
} else {
    super.doThrow(hero);
}
```

### drink(Hero hero)
**签名**: `protected void drink(Hero hero)`
**功能**: 执行饮用药水的逻辑
**实现逻辑**:
```java
// 第287-305行
detach(hero.belongings.backpack); // 从背包移除
hero.spend(TIME_TO_DRINK);        // 消耗时间
hero.busy();                      // 标记忙碌
apply(hero);                      // 应用效果

Sample.INSTANCE.play(Assets.Sounds.DRINK); // 播放音效
hero.sprite.operate(hero.pos);    // 播放动画

if (!anonymous) {
    Catalog.countUse(getClass()); // 记录使用次数
    if (Random.Float() < talentChance) {
        // 触发天赋效果
        Talent.onPotionUsed(curUser, curUser.pos, talentFactor);
    }
}
```

### onThrow(int cell)
**签名**: `@Override protected void onThrow(int cell)`
**功能**: 处理药水投掷到指定格子的效果
**实现逻辑**:
```java
// 第308-329行
if (Dungeon.level.map[cell] == Terrain.WELL || Dungeon.level.pit[cell]) {
    super.onThrow(cell); // 扔进井或坑洞
} else {
    // AquaBrew和StormClouds不触发格子
    if (!(this instanceof AquaBrew) && !(this instanceof PotionOfStormClouds)){
        Dungeon.level.pressCell(cell);
    }
    shatter(cell); // 碎裂效果

    if (!anonymous) {
        Catalog.countUse(getClass());
        if (Random.Float() < talentChance) {
            Talent.onPotionUsed(curUser, cell, talentFactor);
        }
    }
}
```

### apply(Hero hero)
**签名**: `public void apply(Hero hero)`
**功能**: 应用药水效果到英雄（默认调用shatter）
**实现逻辑**:
```java
// 第331-333行
shatter(hero.pos);
```
- 子类重写此方法实现具体效果

### shatter(int cell)
**签名**: `public void shatter(int cell)`
**功能**: 药水碎裂效果，默认显示溅射效果
**实现逻辑**:
```java
// 第335-341行
splash(cell);
if (Dungeon.level.heroFOV[cell]) {
    GLog.i(Messages.get(Potion.class, "shatter"));
    Sample.INSTANCE.play(Assets.Sounds.SHATTER);
}
```
- 子类重写此方法实现投掷效果

### isKnown()
**签名**: `public boolean isKnown()`
**功能**: 检查药水是否已被鉴定
**实现逻辑**:
```java
// 第348-350行
return anonymous || (handler != null && handler.isKnown(this));
```
- 匿名药水视为已鉴定

### setKnown()
**签名**: `public void setKnown()`
**功能**: 将药水标记为已鉴定
**实现逻辑**:
```java
// 第352-364行
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
```

### identify(boolean byHero)
**签名**: `@Override public Item identify(boolean byHero)`
**功能**: 鉴定药水
**实现逻辑**:
```java
// 第367-374行
super.identify(byHero);
if (!isKnown()) {
    setKnown();
}
return this;
```

### name()
**签名**: `@Override public String name()`
**功能**: 返回药水名称
**实现逻辑**:
```java
// 第377-379行
return isKnown() ? super.name() : Messages.get(this, color);
```
- 未鉴定时显示颜色名称

### desc()
**签名**: `@Override public String desc()`
**功能**: 返回药水描述
**实现逻辑**:
```java
// 第388-390行
return isKnown() ? super.desc() : Messages.get(this, "unknown_desc");
```
- 未鉴定时显示未知描述

### splash(int cell)
**签名**: `protected void splash(int cell)`
**功能**: 显示药水溅射效果并清除火和粘液
**实现逻辑**:
```java
// 第418-437行
// 1. 清除格子上的火焰
Fire fire = (Fire)Dungeon.level.blobs.get(Fire.class);
if (fire != null) {
    fire.clear(cell);
}

// 2. 清除友方角色身上的燃烧和粘液
Char ch = Actor.findChar(cell);
if (ch != null && ch.alignment == Char.Alignment.ALLY) {
    Buff.detach(ch, Burning.class);
    Buff.detach(ch, Ooze.class);
}

// 3. 显示溅射特效
if (Dungeon.level.heroFOV[cell]) {
    if (ch != null) {
        Splash.at(ch.sprite.center(), splashColor(), 5);
    } else {
        Splash.at(cell, splashColor(), 5);
    }
}
```

### SeedToPotion.brew(ArrayList<Item> ingredients)
**签名**: `@Override public Item brew(ArrayList<Item> ingredients)`
**功能**: 使用3个种子酿造药水
**实现逻辑**:
```java
// 第507-548行
// 1. 验证材料
if (!testIngredients(ingredients)) return null;

// 2. 消耗材料
for (Item ingredient : ingredients){
    ingredient.quantity(ingredient.quantity() - 1);
}

// 3. 统计不同种子类型
ArrayList<Class<?extends Plant.Seed>> seeds = new ArrayList<>();
for (Item i : ingredients) {
    if (!seeds.contains(i.getClass())) {
        seeds.add((Class<? extends Plant.Seed>) i.getClass());
    }
}

// 4. 决定产出药水类型
Potion result;
if ((seeds.size() == 2 && Random.Int(4) == 0)
    || (seeds.size() == 3 && Random.Int(2) == 0)) {
    // 混合种子有概率产出随机药水
    result = (Potion) Generator.randomUsingDefaults(Generator.Category.POTION);
} else {
    // 否则产出对应种子类型的药水
    result = Reflection.newInstance(types.get(Random.element(ingredients).getClass()));
}

// 5. 同类型种子直接鉴定
if (seeds.size() == 1){
    result.identify();
}

// 6. 治疗药水掉落限制
while (result instanceof PotionOfHealing
    && Random.Int(10) < Dungeon.LimitedDrops.COOKING_HP.count) {
    result = (Potion) Generator.randomUsingDefaults(Generator.Category.POTION);
}

if (result instanceof PotionOfHealing) {
    Dungeon.LimitedDrops.COOKING_HP.count++;
}

return result;
```

## 11. 使用示例

### 创建并饮用药水
```java
// 创建治疗药水
PotionOfHealing potion = new PotionOfHealing();
// 英雄饮用
potion.execute(hero, Potion.AC_DRINK);
// 效果：恢复生命值并治愈负面状态
```

### 投掷药水
```java
// 创建毒气药水
PotionOfToxicGas potion = new PotionOfToxicGas();
// 投掷到指定位置
potion.cast(hero, targetCell);
// 效果：在目标位置产生毒气云
```

### 使用种子酿造药水
```java
// 准备3个种子
ArrayList<Item> seeds = new ArrayList<>();
seeds.add(new Sungrass.Seed());
seeds.add(new Sungrass.Seed());
seeds.add(new Sungrass.Seed());

// 酿造
Potion.SeedToPotion recipe = new Potion.SeedToPotion();
Potion result = recipe.brew(seeds);
// 结果：必为已鉴定的治疗药水（同类型种子）
```

## 注意事项

1. **颜色随机性**: 每局游戏药水颜色与类型对应关系随机，玩家需要通过使用或鉴定来了解

2. **投掷/饮用选择**: 
   - `mustThrowPots` 中的药水必须投掷
   - `canThrowPots` 中的药水可选择饮用或投掷
   - 其他药水默认饮用

3. **匿名药水**: 用于UI显示或纯效果生成，不影响游戏记录

4. **天赋触发**: `talentFactor` 和 `talentChance` 控制天赋效果的触发

## 最佳实践

1. 创建新药水时继承 Potion 并重写 `apply()` 和 `shatter()` 方法

2. 根据药水性质将其添加到 `mustThrowPots` 或 `canThrowPots` 集合

3. 在 `SeedToPotion.types` 中添加种子到药水的映射关系