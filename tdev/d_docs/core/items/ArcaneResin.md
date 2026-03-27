# ArcaneResin 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/ArcaneResin.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items |
| 类类型 | public class |
| 继承关系 | extends Item |
| 代码行数 | 200 行 |

## 2. 类职责说明
ArcaneResin（奥术树脂）用于强化已鉴定的魔杖。每级强化需要的树脂数量递增（+1需1个，+2需2个，+3需3个）。还可以通过炼金将魔杖转化为树脂。法师职业的天赋可以增加产出量。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Item {
        +image int
        +stackable boolean
        +defaultAction String
        +bones boolean
        +isUpgradable() boolean
        +isIdentified() boolean
        +value() int
    }
    
    class ArcaneResin {
        +image ARCANE_RESIN
        +stackable true
        +defaultAction AC_APPLY
        +bones true
        -AC_APPLY String
        -itemSelector ItemSelector
        +isUpgradable() boolean
        +isIdentified() boolean
        +value() int
        +Recipe inner class
    }
    
    Item <|-- ArcaneResin
    ArcaneResin +-- Recipe
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_APPLY | String | "APPLY" | 应用动作标识 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | 初始化块 | 精灵图为 ARCANE_RESIN |
| stackable | boolean | 初始化块 | 可堆叠 true |
| defaultAction | String | 初始化块 | 默认动作 AC_APPLY |
| bones | boolean | 初始化块 | 可从骨头继承 true |
| itemSelector | ItemSelector | private final | 物品选择器 |

## 7. 方法详解

### isUpgradable
**签名**: `public boolean isUpgradable()`
**功能**: 是否可升级
**返回值**: boolean - false（不可升级）

### isIdentified
**签名**: `public boolean isIdentified()`
**功能**: 是否已鉴定
**返回值**: boolean - true（始终已鉴定）

### value
**签名**: `public int value()`
**功能**: 获取出售价格
**返回值**: int - 30 * 数量

### actions
**签名**: `public ArrayList<String> actions(Hero hero)`
**功能**: 获取可用动作列表
**返回值**: ArrayList\<String\> - 包含应用动作

### execute
**签名**: `public void execute(Hero hero, String action)`
**功能**: 执行动作，打开魔杖选择界面
**实现逻辑**:
```java
// 第66-76行：执行应用动作
super.execute(hero, action);
if (action.equals(AC_APPLY)) {
    curUser = hero;
    GameScene.selectItem(itemSelector);          // 打开物品选择器
}
```

### itemSelector (内部)
**功能**: 选择魔杖进行强化
**实现逻辑**:
```java
// 第93-148行：魔杖选择和处理
public void onSelect(Item item) {
    if (item != null && item instanceof Wand) {
        Wand w = (Wand) item;
        
        if (w.level() >= 3) {
            GLog.w(Messages.get(ArcaneResin.class, "level_too_high"));
            return;                               // 魔杖等级过高
        }
        
        int resinToUse = w.level() + 1;           // 需要的树脂数量
        
        if (quantity() < resinToUse) {
            GLog.w(Messages.get(ArcaneResin.class, "not_enough"));
        } else {
            // 消耗树脂
            Catalog.countUses(ArcaneResin.class, resinToUse);
            if (resinToUse < quantity()) {
                quantity(quantity() - resinToUse);
            } else {
                detachAll(Dungeon.hero.belongings.backpack);
            }
            
            // 强化魔杖
            w.resinBonus++;
            w.curCharges++;
            w.updateLevel();
            Item.updateQuickslot();
            
            // 特效
            curUser.sprite.operate(curUser.pos);
            Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
            curUser.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
            curUser.spendAndNext(Actor.TICK);
            GLog.p(Messages.get(ArcaneResin.class, "apply"));
        }
    }
}
```

## 内部类详解

### Recipe
**类型**: public static class extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe
**功能**: 炼金配方，将魔杖转化为树脂
**实现逻辑**:
```java
// 第150-198行：炼金配方
boolean testIngredients(ArrayList<Item> ingredients) {
    return ingredients.size() == 1
        && ingredients.get(0) instanceof Wand
        && ingredients.get(0).cursedKnown
        && !ingredients.get(0).cursed;           // 需要已鉴定且未诅咒的魔杖
}

int cost(ArrayList<Item> ingredients) {
    return 5;                                     // 炼金花费5能量
}

Item brew(ArrayList<Item> ingredients) {
    Item result = sampleOutput(ingredients);
    Wand w = (Wand) ingredients.get(0);
    if (!w.levelKnown) {
        result.quantity(resinQuantity(w));
    }
    w.quantity(0);                                // 消耗魔杖
    return result;
}

private int resinQuantity(Wand w) {
    int level = w.level() - w.resinBonus;
    int quantity = 2 * (level + 1);               // 基础产出
    
    // 法师天赋增加产出
    if (Dungeon.hero.heroClass != HeroClass.MAGE 
        && Dungeon.hero.hasTalent(Talent.WAND_PRESERVATION)) {
        quantity += Dungeon.hero.pointsInTalent(Talent.WAND_PRESERVATION);
    }
    return quantity;
}
```

## 11. 使用示例
```java
// 创建奥术树脂
ArcaneResin resin = new ArcaneResin();
resin.quantity(5);

// 强化+1魔杖需要2个树脂
// 强化+2魔杖需要3个树脂
// 最高只能强化到+3

// 也可以通过炼金将魔杖转化为树脂
```

## 注意事项
1. 只能强化已鉴定的魔杖
2. 魔杖最高强化到+3级
3. 树脂强化不计入升级卷轴限制
4. 法师天赋可以增加树脂产出

## 最佳实践
1. 优先强化常用魔杖
2. 低级魔杖可以转化为树脂
3. 树脂强化不会诅咒物品
4. 考虑树脂数量和魔杖等级的平衡