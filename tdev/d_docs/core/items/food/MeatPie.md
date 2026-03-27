# MeatPie 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/food/MeatPie.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.food |
| 类类型 | public class |
| 继承关系 | extends Food |
| 代码行数 | 98行 |

## 2. 类职责说明
肉派是一种高级食物，提供双倍的饥饿值恢复（STARVING * 2），并且食用后会获得"饱腹"Buff，增加护甲并持续恢复生命。肉派可以通过炼金合成制作。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Food {
        <<abstract>>
        +float energy
        +void satisfy(Hero)
    }
    
    class MeatPie {
        +void satisfy(Hero)
        +int value()
    }
    
    class Recipe {
        +boolean testIngredients(ArrayList)
        +int cost(ArrayList)
        +Item brew(ArrayList)
        +Item sampleOutput(ArrayList)
    }
    
    Food <|-- MeatPie
    Recipe <|-- MeatPie.Recipe
    MeatPie +-- Recipe
    
    note for MeatPie "能量值: STARVING * 2\n提供饱腹Buff\n合成成本: 6点炼金能量"
```

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标（MEAT_PIE） |
| energy | float | - | 能量值（STARVING * 2，约600点） |

## 7. 方法详解

### satisfy(Hero hero)
**签名**: `void satisfy(Hero hero)`
**功能**: 满足饥饿需求并添加饱腹Buff
**参数**:
- hero: Hero - 英雄
**返回值**: void
**实现逻辑**:
1. 调用父类satisfy方法满足饥饿（第42行）
2. 添加饱腹Buff（第43行）

### value()
**签名**: `int value()`
**功能**: 获取物品价值
**参数**: 无
**返回值**: int - 价值（40 * 数量）

## 内部类 Recipe

肉派的合成配方类。

### testIngredients(ArrayList<Item> ingredients)
**签名**: `boolean testIngredients(ArrayList<Item> ingredients)`
**功能**: 检查原料是否有效
**参数**:
- ingredients: ArrayList<Item> - 原料列表
**返回值**: boolean - 是否有效
**实现逻辑**:
1. 检查三种原料：馅饼/幻影肉、口粮、肉类（第55-71行）
2. 馅饼类型：Pasty或PhantomMeat（第61行）
3. 口粮类型：Food类本身（第63行）
4. 肉类类型：MysteryMeat、StewedMeat、ChargrilledMeat、FrozenCarpaccio（第65-68行）
5. 必须三种原料都存在（第74行）

### cost(ArrayList<Item> ingredients)
**签名**: `int cost(ArrayList<Item> ingredients)`
**功能**: 获取合成成本
**参数**:
- ingredients: ArrayList<Item> - 原料列表
**返回值**: int - 成本（6点炼金能量）

### brew(ArrayList<Item> ingredients)
**签名**: `Item brew(ArrayList<Item> ingredients)`
**功能**: 合成肉派
**参数**:
- ingredients: ArrayList<Item> - 原料列表
**返回值**: Item - 肉派
**实现逻辑**:
1. 检查原料有效性（第84行）
2. 消耗所有原料（第86-88行）
3. 返回肉派（第90行）

### sampleOutput(ArrayList<Item> ingredients)
**签名**: `Item sampleOutput(ArrayList<Item> ingredients)`
**功能**: 获取预览输出
**参数**:
- ingredients: ArrayList<Item> - 原料列表
**返回值**: Item - 肉派

## 合成配方

| 原料类型 | 有效物品 |
|---------|---------|
| 馅饼类 | 馅饼、幻影肉 |
| 口粮类 | 普通口粮 |
| 肉类 | 神秘肉、炖肉、烤肉、冰冻肉片 |

## 11. 使用示例
```java
// 创建肉派
MeatPie pie = new MeatPie();

// 食用肉派
pie.execute(hero, Food.AC_EAT);
// 恢复双倍饥饿值（600点）
// 获得饱腹Buff

// 饱腹Buff效果
WellFed buff = hero.buff(WellFed.class);
// 增加护甲值
// 每10回合恢复生命

// 通过炼金合成
// 馅饼 + 口粮 + 肉类 = 肉派
// 成本: 6点炼金能量
```

## 注意事项
1. 肉派提供的能量值是普通食物的两倍
2. 食用后会获得饱腹Buff
3. 饱腹Buff增加护甲并持续恢复生命
4. 合成需要三种不同类型的食物
5. 合成成本为6点炼金能量

## 最佳实践
1. 在长时间探索前食用，获得持久的Buff效果
2. 饱腹Buff在战斗中提供额外的保护
3. 通过炼金合成比直接找到更有价值
4. 可以替代多种食物的使用
5. 在Boss战前食用获得优势