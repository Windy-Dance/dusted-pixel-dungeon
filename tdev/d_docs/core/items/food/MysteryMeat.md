# MysteryMeat 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/food/MysteryMeat.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.food |
| 类类型 | public class |
| 继承关系 | extends Food |
| 代码行数 | 92行 |

## 2. 类职责说明
神秘肉是一种有风险的食物，食用后可能产生随机负面效果：燃烧、定身、中毒或减速。可以通过烹饪（炖煮、烤制、冷冻）来消除风险并获得安全的食物。神秘肉通常从螃蟹等怪物身上掉落。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Food {
        <<abstract>>
        +float energy
        +void satisfy(Hero)
    }
    
    class MysteryMeat {
        +void satisfy(Hero)
        +int value()
        +static void effect(Hero)
    }
    
    class PlaceHolder {
        +boolean isSimilar(Item)
        +String info()
    }
    
    Food <|-- MysteryMeat
    MysteryMeat <|-- PlaceHolder
    MysteryMeat +-- PlaceHolder
```

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标（MEAT） |
| energy | float | - | 能量值（HUNGRY/2，约50点） |

## 7. 方法详解

### satisfy(Hero hero)
**签名**: `void satisfy(Hero hero)`
**功能**: 满足饥饿需求并触发随机效果
**参数**:
- hero: Hero - 英雄
**返回值**: void
**实现逻辑**:
1. 调用父类satisfy方法（第46行）
2. 触发随机负面效果（第47行）

### value()
**签名**: `int value()`
**功能**: 获取物品价值
**参数**: 无
**返回值**: int - 价值（5 * 数量）

### effect(Hero hero)
**签名**: `static void effect(Hero hero)`
**功能**: 触发随机负面效果
**参数**:
- hero: Hero - 英雄
**返回值**: void
**实现逻辑**:
1. 随机选择0-4（第55行）
2. 0: 燃烧效果（第56-59行）
3. 1: 定身效果，持续DURATION*2回合（第60-63行）
4. 2: 中毒效果，伤害为最大生命/5（第64-67行）
5. 3: 减速效果，持续DURATION回合（第68-71行）
6. 4: 无效果（20%概率）

## 内部类 PlaceHolder

占位符类，用于合成配方中代表各种肉类。

### isSimilar(Item item)
**签名**: `boolean isSimilar(Item item)`
**功能**: 检查物品是否相似
**参数**:
- item: Item - 要比较的物品
**返回值**: boolean - 是否相似
**实现逻辑**:
- 匹配MysteryMeat、StewedMeat、ChargrilledMeat、FrozenCarpaccio（第83-84行）

### info()
**签名**: `String info()`
**功能**: 获取物品信息
**参数**: 无
**返回值**: String - 空字符串

## 效果概率表

| 效果 | 概率 | 持续时间/伤害 |
|------|------|--------------|
| 燃烧 | 20% | 直到扑灭 |
| 定身 | 20% | Roots.DURATION * 2 |
| 中毒 | 20% | 最大生命/5伤害 |
| 减速 | 20% | Slow.DURATION |
| 无效果 | 20% | - |

## 11. 使用示例
```java
// 创建神秘肉
MysteryMeat meat = new MysteryMeat();

// 直接食用（有风险）
meat.execute(hero, Food.AC_EAT);
// 20%概率燃烧
// 20%概率定身
// 20%概率中毒
// 20%概率减速
// 20%概率无效果

// 烹饪成安全食物
StewedMeat stewed = StewedMeat.cook(meat); // 炖煮
ChargrilledMeat grilled = ChargrilledMeat.cook(meat.quantity()); // 烤制
FrozenCarpaccio frozen = FrozenCarpaccio.cook(meat); // 冷冻

// 手动触发效果
MysteryMeat.effect(hero); // 触发随机效果
```

## 注意事项
1. 食用有80%概率产生负面效果
2. 能量值较低（只有50点）
3. 可以通过烹饪消除风险
4. 燃烧效果在水中会自动扑灭
5. 中毒伤害基于最大生命值

## 最佳实践
1. 不要直接食用，先烹饪处理
2. 炖煮是最安全的处理方式
3. 冷冻后有概率获得正面效果
4. 如果必须直接食用，确保有治疗手段
5. 在安全区域食用可以处理负面效果