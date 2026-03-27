# Blandfruit 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/food/Blandfruit.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.food |
| **文件类型** | class |
| **继承关系** | extends Food |
| **代码行数** | 311 行 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Blandfruit 类实现"无味果"食物，是一种可烹饪的特殊食物。原始状态不可食用，需与种子一起烹饪后获得对应药剂效果的熟制果实。支持投掷使用，部分类型可造成范围效果。

### 系统定位
Blandfruit 是 Food 的复杂子类，涉及炼金系统、药剂系统、投掷机制等多个游戏系统的交互。它是连接食物和药剂的桥梁物品。

### 不负责什么
- 不负责种子到药剂的映射（由 `Potion.SeedToPotion` 处理）
- 不负责药剂的实际效果（由各类 Potion 子类实现）

## 3. 结构总览

### 主要成员概览
- **实例字段**：`potionAttrib`（药剂属性）、`potionGlow`（发光效果）
- **覆写方法**：`isSimilar()`、`defaultAction()`、`execute()`、`name()`、`desc()`、`value()`、`onThrow()`、`reset()`、`storeInBundle()`、`restoreFromBundle()`、`glowing()`
- **公开方法**：`cook(Seed)`、`imbuePotion(Potion)`
- **内部类**：`CookFruit`（炼金配方）、`Chunks`（掉落物）

### 主要逻辑块概览
- **烹饪机制**：通过种子转化为具有药剂效果的果实
- **投掷机制**：特定类型的熟果投掷时触发药剂效果
- **序列化**：保存和恢复药剂属性

### 生命周期/调用时机
- 地牢中找到原始无味果
- 炼金锅中与种子烹饪
- 食用或投掷使用

## 4. 继承与协作关系

### 父类提供的能力
继承自 Food 类的能力：
- 完整的食用流程
- 饥饿恢复机制
- 物品堆叠系统

### 覆写的方法
| 方法名 | 覆写内容 |
|--------|----------|
| `isSimilar(Item)` | 比较药剂属性是否相同 |
| `defaultAction()` | 根据药剂属性返回默认动作 |
| `execute(Hero, String)` | 处理原始果实不可食用、熟果食用/投掷逻辑 |
| `name()` | 根据药剂类型返回特定名称 |
| `desc()` | 根据状态返回不同描述 |
| `value()` | 返回 20 * quantity |
| `onThrow(int)` | 特定类型投掷时触发药剂效果 |
| `reset()` | 重置发光效果 |
| `storeInBundle(Bundle)` | 保存药剂属性 |
| `restoreFromBundle(Bundle)` | 恢复药剂属性 |
| `glowing()` | 返回药剂发光效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Potion` | 药剂基类，提供效果 |
| `Seed` | 种子类，用于烹饪 |
| `Recipe` | 炼金配方基类 |
| `Reflection` | 反射工具，动态创建药剂实例 |
| `Terrain` | 地形类型判断 |
| `WndUseItem` | 使用物品窗口 |

### 药剂类型对应名称
| 药剂类型 | 果实名称 |
|----------|----------|
| PotionOfHealing | 阳光果 |
| PotionOfStrength | 腐朽果 |
| PotionOfParalyticGas | 地缚果 |
| PotionOfInvisibility | 目盲果 |
| PotionOfLiquidFlame | 火焰果 |
| PotionOfFrost | 冰霜果 |
| PotionOfMindVision | 渐隐果 |
| PotionOfToxicGas | 忧伤果 |
| PotionOfLevitation | 暴风果 |
| PotionOfPurity | 法皇果 |
| PotionOfExperience | 星陨果 |
| PotionOfHaste | 速行果 |

## 5. 字段/常量详解

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `potionAttrib` | Potion | null | 附着的药剂属性 |
| `potionGlow` | ItemSprite.Glowing | null | 物品发光效果 |

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `POTIONATTRIB` | String | "potionattrib" | Bundle 存储键 |

### 初始化块设置
| 字段名 | 值 | 说明 |
|--------|-----|------|
| `stackable` | true | 可堆叠 |
| `image` | ItemSpriteSheet.BLANDFRUIT | 无味果图标 |
| `energy` | Hunger.STARVING (450f) | 熟果的饱腹值 |
| `bones` | true | 可出现在遗骸中 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认无参构造器。

### 初始化块
```java
{
    stackable = true;
    image = ItemSpriteSheet.BLANDFRUIT;
    energy = Hunger.STARVING; // 仅熟果有效
    bones = true;
}
```

### 初始化注意事项
- `energy` 设为 `Hunger.STARVING`，但原始果实不可直接食用
- `potionAttrib` 为 null 时表示未烹饪的原始果实

## 7. 方法详解

### isSimilar(Item item)

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：判断两个无味果是否可堆叠，需比较药剂属性。

**参数**：
- `item` (Item)：要比较的物品

**返回值**：boolean，是否可堆叠

**核心实现逻辑**：
```java
@Override
public boolean isSimilar( Item item ) {
    if ( super.isSimilar(item) ){
        Blandfruit other = (Blandfruit) item;
        if (potionAttrib == null && other.potionAttrib == null) {
            return true;
        } else if (potionAttrib != null && other.potionAttrib != null
                && potionAttrib.isSimilar(other.potionAttrib)){
            return true;
        }
    }
    return false;
}
```

---

### defaultAction()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：返回默认动作，根据药剂属性决定。

**返回值**：String，默认动作标识

**核心实现逻辑**：
```java
@Override
public String defaultAction() {
    if (potionAttrib == null){
        return null; // 原始果实无默认动作
    } else if (potionAttrib.defaultAction().equals(Potion.AC_DRINK)) {
        return AC_EAT; // 药剂默认饮用则改为食用
    } else {
        return potionAttrib.defaultAction(); // 其他动作（如投掷）
    }
}
```

---

### execute(Hero hero, String action)

**可见性**：public

**是否覆写**：是，覆写自 Food

**方法职责**：执行动作，处理原始果实不可食用和熟果效果。

**参数**：
- `hero` (Hero)：执行者
- `action` (String)：动作标识

**返回值**：void

**核心实现逻辑**：
```java
@Override
public void execute( Hero hero, String action ) {
    if (action.equals( Potion.AC_CHOOSE )){
        GameScene.show(new WndUseItem(null, this) );
        return;
    }

    if (action.equals( AC_EAT ) && potionAttrib == null) {
        GLog.w( Messages.get(this, "raw")); // 原始不可食用
        return;
    }

    super.execute(hero, action);

    if (action.equals( AC_EAT ) && potionAttrib != null){
        potionAttrib.apply(hero); // 应用药剂效果
    }
}
```

---

### name()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：根据药剂类型返回特定名称。

**返回值**：String，物品名称

**核心实现逻辑**：
```java
@Override
public String name() {
    if (potionAttrib instanceof PotionOfHealing)        return Messages.get(this, "sunfruit");
    if (potionAttrib instanceof PotionOfStrength)       return Messages.get(this, "rotfruit");
    // ... 其他类型
    return super.name();
}
```

---

### desc()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：返回物品描述，根据状态区分。

**返回值**：String，物品描述

**核心实现逻辑**：
```java
@Override
public String desc() {
    if (potionAttrib== null) {
        return super.desc(); // 原始描述
    } else {
        String desc = Messages.get(this, "desc_cooked") + "\n\n";
        if (potionAttrib instanceof PotionOfFrost
            || potionAttrib instanceof PotionOfLiquidFlame
            || potionAttrib instanceof PotionOfToxicGas
            || potionAttrib instanceof PotionOfParalyticGas) {
            desc += Messages.get(this, "desc_throw"); // 建议投掷
        } else {
            desc += Messages.get(this, "desc_eat"); // 建议食用
        }
        return desc;
    }
}
```

---

### cook(Seed seed)

**可见性**：public

**是否覆写**：否

**方法职责**：使用种子烹饪无味果。

**参数**：
- `seed` (Seed)：用于烹饪的种子

**返回值**：Item，烹饪后的无味果

**核心实现逻辑**：
```java
public Item cook(Seed seed){
    return imbuePotion(Reflection.newInstance(Potion.SeedToPotion.types.get(seed.getClass())));
}
```

---

### imbuePotion(Potion potion)

**可见性**：public

**是否覆写**：否

**方法职责**：将药剂属性注入无味果。

**参数**：
- `potion` (Potion)：要注入的药剂

**返回值**：Item，注入后的无味果

**核心实现逻辑**：
```java
public Item imbuePotion(Potion potion){
    potionAttrib = potion;
    potionAttrib.anonymize();
    potionAttrib.image = ItemSpriteSheet.BLANDFRUIT;

    // 根据药剂类型设置发光颜色
    if (potionAttrib instanceof PotionOfHealing)        potionGlow = new ItemSprite.Glowing( 0x2EE62E );
    if (potionAttrib instanceof PotionOfStrength)       potionGlow = new ItemSprite.Glowing( 0xCC0022 );
    // ... 其他类型

    return this;
}
```

---

### onThrow(int cell)

**可见性**：protected

**是否覆写**：是，覆写自 Item

**方法职责**：处理投掷行为，特定类型触发药剂效果。

**参数**：
- `cell` (int)：投掷目标格

**返回值**：void

**核心实现逻辑**：
```java
@Override
protected void onThrow(int cell) {
    if (Dungeon.level.map[cell] == Terrain.WELL || Dungeon.level.pit[cell]) {
        super.onThrow( cell );
    } else if (potionAttrib instanceof PotionOfLiquidFlame ||
            potionAttrib instanceof PotionOfToxicGas ||
            potionAttrib instanceof PotionOfParalyticGas ||
            potionAttrib instanceof PotionOfFrost ||
            potionAttrib instanceof PotionOfLevitation ||
            potionAttrib instanceof PotionOfPurity) {
        Catalog.countUse(getClass());
        potionAttrib.shatter( cell ); // 触发药剂效果
        Dungeon.level.drop(new Chunks(), cell).sprite.drop(); // 掉落果块
    } else {
        super.onThrow( cell );
    }
}
```

---

### value()

**可见性**：public

**是否覆写**：是，覆写自 Food

**方法职责**：计算物品价值。

**返回值**：int，20 * quantity

---

### glowing()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：返回物品发光效果。

**返回值**：ItemSprite.Glowing，药剂发光效果

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `cook(Seed)` | 使用种子烹饪 |
| `imbuePotion(Potion)` | 注入药剂属性 |

### 内部辅助方法
继承自 Food 的所有方法。

### 扩展入口
- `CookFruit` 配方可被炼金系统使用
- `Chunks` 内部类表示投掷后掉落的果块

## 9. 运行机制与调用链

### 创建时机
- 地牢中随机生成原始无味果
- 炼金锅中烹饪生成熟制无味果

### 烹饪流程
```
无味果 + 种子
    ↓
炼金锅 (CookFruit Recipe)
    ↓
cook(Seed) → imbuePotion(Potion)
    ↓
具有药剂效果的熟制无味果
```

### 投掷流程
```
投掷熟果
    ↓
onThrow(cell)
    ↓
判断药剂类型
    ↓ 攻击型
potionAttrib.shatter(cell) + 掉落 Chunks
    ↓ 非攻击型
普通掉落
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.food.blandfruit.name` | 无味果 | 原始名称 |
| `items.food.blandfruit.cooked` | 熟无味果 | 熟制通用名 |
| `items.food.blandfruit.sunfruit` | 阳光果 | 治疗药剂果实 |
| `items.food.blandfruit.rotfruit` | 腐朽果 | 力量药剂果实 |
| `items.food.blandfruit.earthfruit` | 地缚果 | 麻痹药剂果实 |
| `items.food.blandfruit.blindfruit` | 目盲果 | 隐形药剂果实 |
| `items.food.blandfruit.firefruit` | 火焰果 | 液火药剂果实 |
| `items.food.blandfruit.icefruit` | 冰霜果 | 冰霜药剂果实 |
| `items.food.blandfruit.fadefruit` | 渐隐果 | 灵视药剂果实 |
| `items.food.blandfruit.sorrowfruit` | 忧伤果 | 毒气药剂果实 |
| `items.food.blandfruit.stormfruit` | 暴风果 | 浮空药剂果实 |
| `items.food.blandfruit.dreamfruit` | 法皇果 | 净化药剂果实 |
| `items.food.blandfruit.starfruit` | 星陨果 | 经验药剂果实 |
| `items.food.blandfruit.swiftfruit` | 速行果 | 极速药剂果实 |
| `items.food.blandfruit.raw` | 你没法忍受生吃这玩意儿。 | 原始食用警告 |
| `items.food.blandfruit.desc` | 干燥且脆弱，或许加点其他材料再煮能够增强它的效果。 | 原始描述 |
| `items.food.blandfruit.desc_cooked` | 这个果实已经因为吸收锅中的汤而鼓胀，并且吸收了其中种子的属性。它具有这粒种子对应的药剂效果。 | 熟制描述 |
| `items.food.blandfruit.desc_eat` | 看起来已经可以吃了！ | 食用建议 |
| `items.food.blandfruit.desc_throw` | 它似乎性质很不稳定，最好作为武器丢出去。 | 投掷建议 |

### Chunks 内部类翻译
| 键名 | 中文翻译 |
|------|---------|
| `items.food.blandfruit$chunks.name` | 无味果块 |
| `items.food.blandfruit$chunks.desc` | 无味果触地爆炸，碎成了一地的普通果块。尽管上面沾上了尘土，这些大块的熟制无味果应该可以安全食用。 |

### 中文翻译来源
所有中文翻译来自 `core/src/main/assets/messages/items/items_zh.properties` 文件。

## 11. 使用示例

### 基本用法

```java
// 创建原始无味果
Blandfruit raw = new Blandfruit();

// 尝试食用原始果实
raw.execute(hero, Food.AC_EAT); // 会显示"你没法忍受生吃这玩意儿"

// 烹饪无味果（通常在炼金锅中进行）
Blandfruit cooked = new Blandfruit();
cooked.cook(someSeed); // 注入种子对应的药剂效果

// 食用熟果
cooked.execute(hero, Food.AC_EAT); // 会触发药剂效果
```

### 炼金配方使用

```java
// CookFruit 配方会自动处理
Recipe recipe = new Blandfruit.CookFruit();
ArrayList<Item> ingredients = new ArrayList<>();
ingredients.add(new Blandfruit());
ingredients.add(new Sungrass.Seed()); // 例如：太阳草种子

if (recipe.testIngredients(ingredients)) {
    Item result = recipe.brew(ingredients); // 返回阳光果
}
```

## 12. 开发注意事项

### 状态依赖
- `potionAttrib` 为 null 表示未烹饪状态
- 熟果的堆叠需要药剂属性完全相同

### 生命周期耦合
- 烹饪会改变物品的完整属性（名称、描述、效果、发光）
- 投掷攻击型熟果会消耗物品并产生 Chunks

### 常见陷阱
1. **忘记检查原始状态**：直接食用原始无味果会被拒绝
2. **堆叠逻辑**：不同药剂类型的熟果不能堆叠
3. **投掷效果**：部分熟果投掷时效果与食用不同

## 13. 修改建议与扩展点

### 适合扩展的位置
1. **`imbuePotion()` 方法**：添加新的药剂类型映射
2. **`onThrow()` 方法**：扩展可投掷的药剂类型
3. **`CookFruit` 配方**：修改烹饪成本或条件

### 不建议修改的位置
1. **药剂类型与名称的映射**：保持与现有翻译一致
2. **发光颜色定义**：已与药剂类型绑定

### 扩展示例

```java
// 添加自定义药剂类型
if (potionAttrib instanceof PotionOfCustom) {
    potionGlow = new ItemSprite.Glowing( 0x123456 );
}
```

## 14. 事实核查清单

- [x] 是否已覆盖全部字段 - 已覆盖 potionAttrib、potionGlow
- [x] 是否已覆盖全部方法 - 已覆盖所有公开和覆写方法
- [x] 是否已检查继承链与覆写关系 - 已说明继承自 Food
- [x] 是否已核对官方中文翻译 - 已从 items_zh.properties 获取
- [x] 是否存在任何推测性表述 - 无，所有信息均来自源码
- [x] 示例代码是否真实可用 - 示例基于实际 API
- [x] 是否遗漏资源/配置/本地化关联 - 已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点 - 已在章节 12 和 13 详细说明

---

**文档版本**：1.0  
**最后更新**：2026-03-27  
**基于源码版本**：Shattered Pixel Dungeon (GPL-3.0)