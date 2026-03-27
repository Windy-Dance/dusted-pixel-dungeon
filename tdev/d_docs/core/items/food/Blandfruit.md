# Blandfruit 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/food/Blandfruit.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.food |
| 类类型 | public class |
| 继承关系 | extends Food |
| 代码行数 | 311行 |

## 2. 类职责说明
无味果是一种特殊的食物，可以通过与种子一起烹饪来获得对应药水的效果。烹饪后的无味果具有高能量值（STARVING），并且可以像药水一样使用。某些类型的无味果可以投掷使用。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Food {
        <<abstract>>
        +float energy
        +void execute(Hero, String)
        +void satisfy(Hero)
    }
    
    class Blandfruit {
        -Potion potionAttrib
        -ItemSprite.Glowing potionGlow
        +Item cook(Seed)
        +Item imbuePotion(Potion)
        +String name()
        +String desc()
        +int value()
        +ItemSprite.Glowing glowing()
        #void onThrow(int)
    }
    
    class Chunks {
        +float energy
    }
    
    class CookFruit {
        +boolean testIngredients(ArrayList)
        +int cost(ArrayList)
        +Item brew(ArrayList)
    }
    
    Food <|-- Blandfruit
    Food <|-- Chunks
    Recipe <|-- CookFruit
    Blandfruit +-- Chunks
    Blandfruit +-- CookFruit
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| POTIONATTRIB | String | "potionattrib" | Bundle存储键 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| stackable | boolean | - | 是否可堆叠（true） |
| image | int | - | 物品图标（BLANDFRUIT） |
| energy | float | - | 能量值（STARVING，烹饪后） |
| bones | boolean | - | 是否可出现在遗骨中（true） |
| potionAttrib | Potion | public | 关联的药水属性 |
| potionGlow | ItemSprite.Glowing | public | 发光效果 |

## 7. 方法详解

### isSimilar(Item item)
**签名**: `boolean isSimilar(Item item)`
**功能**: 检查物品是否相似（用于堆叠）
**参数**:
- item: Item - 要比较的物品
**返回值**: boolean - 是否相似
**实现逻辑**:
1. 调用父类isSimilar方法（第73行）
2. 如果都没有药水属性，返回true（第75-76行）
3. 如果都有相同药水属性，返回true（第77-79行）

### defaultAction()
**签名**: `String defaultAction()`
**功能**: 获取默认动作
**参数**: 无
**返回值**: String - 默认动作
**实现逻辑**:
1. 如果没有药水属性，返回null（第87-88行）
2. 如果药水默认动作是饮用，返回进食动作（第89-90行）
3. 否则返回药水的默认动作（第92行）

### execute(Hero hero, String action)
**签名**: `void execute(Hero hero, String action)`
**功能**: 执行指定的物品动作
**参数**:
- hero: Hero - 执行动作的英雄
- action: String - 要执行的动作名称
**返回值**: void
**实现逻辑**:
1. 如果动作是AC_CHOOSE，显示使用窗口（第99-102行）
2. 如果动作是进食且没有药水属性，提示不能生吃（第106-109行）
3. 调用父类execute方法（第113行）
4. 如果动作是进食且有药水属性，应用药水效果（第115-118行）

### name()
**签名**: `String name()`
**功能**: 获取物品名称
**参数**: 无
**返回值**: String - 名称
**实现逻辑**:
- 根据药水类型返回对应的水果名称（第124-136行）

### desc()
**签名**: `String desc()`
**功能**: 获取物品描述
**参数**: 无
**返回值**: String - 描述
**实现逻辑**:
1. 如果没有药水属性，返回基础描述（第141-142行）
2. 否则添加烹饪后的描述和使用提示（第144-153行）

### cook(Seed seed)
**签名**: `Item cook(Seed seed)`
**功能**: 使用种子烹饪无味果
**参数**:
- seed: Seed - 种子
**返回值**: Item - 烹饪后的无味果
**实现逻辑**:
- 通过种子类型获取对应药水，调用imbuePotion（第163行）

### imbuePotion(Potion potion)
**签名**: `Item imbuePotion(Potion potion)`
**功能**: 注入药水属性
**参数**:
- potion: Potion - 药水
**返回值**: Item - 注入后的无味果
**实现逻辑**:
1. 设置药水属性并匿名化（第168-169行）
2. 设置药水图标（第171行）
3. 根据药水类型设置发光颜色（第173-184行）

### onThrow(int cell)
**签名**: `void onThrow(int cell)`
**功能**: 投掷时的处理
**参数**:
- cell: int - 目标单元格
**返回值**: void
**实现逻辑**:
1. 如果目标是井或坑，调用父类方法（第193-194行）
2. 如果是可投掷的药水类型，触发药水效果并掉落碎片（第196-206行）
3. 否则调用父类方法（第207-208行）

### reset()
**签名**: `void reset()`
**功能**: 重置物品状态
**参数**: 无
**返回值**: void
**实现逻辑**:
- 如果有药水属性，重新注入（第215-217行）

### glowing()
**签名**: `ItemSprite.Glowing glowing()`
**功能**: 获取发光效果
**参数**: 无
**返回值**: ItemSprite.Glowing - 发光效果

## 内部类 CookFruit

烹饪无味果的配方类。

### testIngredients(ArrayList<Item> ingredients)
**功能**: 检查原料是否有效
**参数**:
- ingredients: ArrayList<Item> - 原料列表
**返回值**: boolean - 是否有效
**实现逻辑**:
1. 检查原料数量为2（第244行）
2. 检查第一个是无味果，第二个是种子（第246-260行）
3. 检查无味果未烹饪且数量足够（第265-268行）

### brew(ArrayList<Item> ingredients)
**功能**: 烹饪无味果
**参数**:
- ingredients: ArrayList<Item> - 原料列表
**返回值**: Item - 烹饪后的无味果

## 内部类 Chunks

无味果碎片，当投掷某些无味果时掉落的可食用碎片。

## 无味果类型表

| 药水类型 | 名称 | 发光颜色 | 可投掷 |
|---------|------|---------|--------|
| 治疗药水 | Sunfruit | 绿色 | 否 |
| 力量药水 | Rotfruit | 红色 | 否 |
| 麻痹药水 | Earthfruit | 棕色 | 是 |
| 隐身药水 | Blindfruit | 白色 | 否 |
| 液体火焰药水 | Firefruit | 橙色 | 是 |
| 冰霜药水 | Icefruit | 蓝色 | 是 |
| 心眼药水 | Fadefruit | 灰色 | 否 |
| 毒气药水 | Sorrowfruit | 紫色 | 是 |
| 漂浮药水 | Stormfruit | 青色 | 是 |
| 净化药水 | Dreamfruit | 粉色 | 是 |
| 经验药水 | Starfruit | 深灰 | 否 |
| 急速药水 | Swiftfruit | 黄色 | 否 |

## 11. 使用示例
```java
// 创建无味果
Blandfruit fruit = new Blandfruit();

// 生吃无味果 - 会被拒绝
fruit.execute(hero, Food.AC_EAT); // "生吃无味果太恶心了"

// 使用种子烹饪
Blandfruit cooked = (Blandfruit) fruit cook(sungrassSeed);
// 变成Sunfruit，具有治疗药水效果

// 食用烹饪后的无味果
cooked.execute(hero, Food.AC_EAT);
// 满足饥饿并应用治疗药水效果

// 投掷某些类型的无味果
if (cooked.potionAttrib instanceof PotionOfLiquidFlame) {
    // 可以投掷触发火焰效果
}
```

## 注意事项
1. 未烹饪的无味果不能直接食用
2. 烹饪需要无味果和种子各一个
3. 烹饪成本为2点炼金能量
4. 某些类型的无味果可以投掷使用
5. 投掷后会掉落可食用的碎片

## 最佳实践
1. 优先烹饪治疗、力量、经验等有益药水
2. 避免烹饪有害药水（如毒气）
3. 冰霜、火焰、麻痹等类型的无味果可以投掷
4. 烹饪后的能量值很高（STARVING），适合长途探索
5. 可以用于替代药水，节省背包空间