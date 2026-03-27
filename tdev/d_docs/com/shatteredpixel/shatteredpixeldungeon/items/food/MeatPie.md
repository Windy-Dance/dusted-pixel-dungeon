# MeatPie 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/food/MeatPie.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.food |
| **文件类型** | class |
| **继承关系** | extends Food |
| **代码行数** | 98 行 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
MeatPie 类实现"全肉大饼"食物，是游戏中饱腹值最高的食物。食用后提供大量饥饿恢复并附加"饱食"增益状态。

### 系统定位
MeatPie 是 Food 的高级子类，只能通过炼金合成获得。它是游戏中最强的食物之一。

### 不负责什么
- 不负责炼金配方的注册（由 Recipe 系统管理）
- 不负责饱食状态的具体效果（由 WellFed Buff 实现）

## 3. 结构总览

### 主要成员概览
- **实例字段**：`image`、`energy`（在初始化块设置）
- **覆写方法**：`satisfy(Hero)`、`value()`
- **内部类**：`Recipe`（炼金配方）

### 主要逻辑块概览
- **超高饱腹值**：energy = Hunger.STARVING * 2 (900f)
- **饱食增益**：食用后附加 WellFed Buff
- **炼金合成**：需要馅饼/幻影鱼肉 + 口粮 + 肉类

### 生命周期/调用时机
- 仅通过炼金合成创建
- 食用时触发饱食增益

## 4. 继承与协作关系

### 父类提供的能力
继承自 Food 类的完整食用功能。

### 覆写的方法
| 方法名 | 覆写内容 |
|--------|----------|
| `satisfy(Hero)` | 添加饱食增益效果 |
| `value()` | 返回 40 * quantity |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `WellFed` | 饱食增益 Buff |
| `Pasty` | 配方原料之一 |
| `PhantomMeat` | 配方原料之一 |
| `Recipe` | 炼金配方基类 |

### 配方原料组合
需要以下三类原料各一：
1. **馅饼类**：`Pasty` 或 `PhantomMeat`
2. **口粮类**：`Food`（仅基础口粮）
3. **肉类**：`MysteryMeat`、`StewedMeat`、`ChargrilledMeat` 或 `FrozenCarpaccio`

## 5. 字段/常量详解

### 实例字段（初始化块设置）
| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.MEAT_PIE | 全肉大饼图标 |
| `energy` | float | Hunger.STARVING*2f (900f) | 饱腹值，是普通口粮的3倍 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认无参构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.MEAT_PIE;
    energy = Hunger.STARVING*2f;
}
```

## 7. 方法详解

### satisfy(Hero hero)

**可见性**：protected

**是否覆写**：是，覆写自 Food

**方法职责**：恢复饥饿值并附加饱食增益。

**参数**：
- `hero` (Hero)：食用者

**返回值**：void

**核心实现逻辑**：
```java
@Override
protected void satisfy(Hero hero) {
    super.satisfy( hero );
    Buff.affect(hero, WellFed.class).reset();
}
```

---

### value()

**可见性**：public

**是否覆写**：是，覆写自 Food

**方法职责**：计算物品价值。

**返回值**：int，40 * quantity

---

### Recipe 内部类

**方法**：

| 方法名 | 说明 |
|--------|------|
| `testIngredients(ArrayList<Item>)` | 检查原料是否符合配方 |
| `cost(ArrayList<Item>)` | 返回炼金成本（6能量） |
| `brew(ArrayList<Item>)` | 执行合成 |
| `sampleOutput(ArrayList<Item>)` | 返回预览产物 |

**配方检查逻辑**：
```java
@Override
public boolean testIngredients(ArrayList<Item> ingredients) {
    boolean pasty = false;
    boolean ration = false;
    boolean meat = false;
    
    for (Item ingredient : ingredients){
        if (ingredient.quantity() > 0) {
            if (ingredient instanceof Pasty || ingredient instanceof PhantomMeat) {
                pasty = true;
            } else if (ingredient.getClass() == Food.class) {
                ration = true;
            } else if (ingredient instanceof MysteryMeat
                    || ingredient instanceof StewedMeat
                    || ingredient instanceof ChargrilledMeat
                    || ingredient instanceof FrozenCarpaccio) {
                meat = true;
            }
        }
    }
    
    return pasty && ration && meat;
}
```

## 8. 对外暴露能力

### 显式 API
继承自 Food 的所有公开方法。

### 内部辅助方法
- `Recipe` 内部类提供炼金合成功能

## 9. 运行机制与调用链

### 创建时机
- 仅通过炼金锅合成

### 合成流程
```
炼金锅 + 原料
    ↓
MeatPie.Recipe.testIngredients() - 验证原料
    ↓
MeatPie.Recipe.brew() - 消耗原料
    ↓
返回 MeatPie 实例
```

### 食用效果
```
食用全肉大饼
    ↓
satisfy(hero)
    ↓
super.satisfy() - 恢复900饥饿值
    ↓
Buff.affect(WellFed.class) - 附加饱食增益
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.food.meatpie.name` | 全肉大饼 | 物品名称 |
| `items.food.meatpie.eat_msg` | 这食物味道真棒！ | 食用成功消息 |
| `items.food.meatpie.desc` | 一份填满了美味肉馅的诱人大饼。吃下它后你会获得远高于其他食物的饱足感。 | 物品描述 |
| `items.food.meatpie.discover_hint` | 你可通过炼金合成该物品。 | 发现提示 |

### 中文翻译来源
所有中文翻译来自 `core/src/main/assets/messages/items/items_zh.properties` 文件。

## 11. 使用示例

### 基本用法

```java
// 创建全肉大饼
MeatPie pie = new MeatPie();

// 英雄食用
pie.execute(hero, Food.AC_EAT);
// 效果：恢复900饥饿值 + 饱食增益
```

### 炼金合成

```java
// 准备原料
ArrayList<Item> ingredients = new ArrayList<>();
ingredients.add(new Pasty());           // 馅饼类
ingredients.add(new Food());            // 口粮
ingredients.add(new MysteryMeat());     // 肉类

// 合成
MeatPie.Recipe recipe = new MeatPie.Recipe();
if (recipe.testIngredients(ingredients)) {
    Item result = recipe.brew(ingredients); // 返回 MeatPie
}
```

## 12. 开发注意事项

### 状态依赖
- 依赖 `WellFed` Buff 类

### 生命周期耦合
- 仅通过炼金系统创建

### 常见陷阱
- 配方原料类型有严格限制
- `Food.class` 精确匹配，不包括子类

## 13. 修改建议与扩展点

### 适合扩展的位置
- `Recipe` 类可扩展支持更多原料组合

### 不建议修改的位置
- 饱腹值和饱食效果影响游戏平衡

## 14. 事实核查清单

- [x] 是否已覆盖全部字段 - 已覆盖 image、energy
- [x] 是否已覆盖全部方法 - 已覆盖 satisfy()、value() 及 Recipe 内部类方法
- [x] 是否已检查继承链与覆写关系 - 已说明继承自 Food
- [x] 是否已核对官方中文翻译 - 已从 items_zh.properties 获取
- [x] 是否存在任何推测性表述 - 无
- [x] 示例代码是否真实可用 - 示例基于实际 API
- [x] 是否遗漏资源/配置/本地化关联 - 已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点 - 已说明

---

**文档版本**：1.0  
**最后更新**：2026-03-27  
**基于源码版本**：Shattered Pixel Dungeon (GPL-3.0)