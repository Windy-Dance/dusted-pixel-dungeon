# StewedMeat 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/food/StewedMeat.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.food |
| 类类型 | public class |
| 继承关系 | extends Food |
| 代码行数 | 79行 |

## 2. 类职责说明
炖肉是通过在炼金锅中炖煮神秘肉获得的安全食物。食用后不会产生负面效果，是处理神秘肉的最佳方式之一。炖肉可以批量制作，效率高。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Food {
        <<abstract>>
        +float energy
    }
    
    class StewedMeat {
        +int value()
    }
    
    class oneMeat {
        +inputs: MysteryMeat
        +cost: 1
        +output: StewedMeat x1
    }
    
    class twoMeat {
        +inputs: MysteryMeat x2
        +cost: 2
        +output: StewedMeat x2
    }
    
    class threeMeat {
        +inputs: MysteryMeat x3
        +cost: 2
        +output: StewedMeat x3
    }
    
    Food <|-- StewedMeat
    Recipe <|-- oneMeat
    Recipe <|-- twoMeat
    Recipe <|-- threeMeat
    StewedMeat +-- oneMeat
    StewedMeat +-- twoMeat
    StewedMeat +-- threeMeat
```

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标（STEWED） |
| energy | float | - | 能量值（HUNGRY/2，约50点） |

## 7. 方法详解

### value()
**签名**: `int value()`
**功能**: 获取物品价值
**参数**: 无
**返回值**: int - 价值（8 * 数量）

## 内部类 oneMeat / twoMeat / threeMeat

三个不同的炖肉配方类，用于批量制作。

### oneMeat
**输入**: 1个神秘肉
**成本**: 1点炼金能量
**输出**: 1个炖肉

### twoMeat
**输入**: 2个神秘肉
**成本**: 2点炼金能量
**输出**: 2个炖肉

### threeMeat
**输入**: 3个神秘肉
**成本**: 2点炼金能量
**输出**: 3个炖肉

## 炖肉配方表

| 配方 | 输入 | 成本 | 输出 |
|------|------|------|------|
| oneMeat | 1个神秘肉 | 1点能量 | 1个炖肉 |
| twoMeat | 2个神秘肉 | 2点能量 | 2个炖肉 |
| threeMeat | 3个神秘肉 | 2点能量 | 3个炖肉 |

## 11. 使用示例
```java
// 创建炖肉
StewedMeat stewed = new StewedMeat();

// 食用炖肉
stewed.execute(hero, Food.AC_EAT);
// 恢复饥饿值（约50点）
// 无负面效果

// 通过炼金制作
// 神秘肉 -> 炖肉
// 成本: 1点炼金能量（单个）
// 批量制作更省能量

// 批量制作配方
// 3个神秘肉 + 2点能量 = 3个炖肉
// 平均每个炖肉成本约0.67点能量
```

## 注意事项
1. 炖肉是安全的食物，无负面效果
2. 能量值与神秘肉相同（约50点）
3. 批量制作更省炼金能量
4. 价值比神秘肉稍高（8金币 vs 5金币）
5. 最推荐的神秘肉处理方式

## 最佳实践
1. 收集神秘肉后批量炖煮
2. 使用threeMeat配方最省能量
3. 炖肉比烤肉和冷冻肉更安全可靠
4. 适合大量制作储存备用
5. 不消耗其他资源（如水）