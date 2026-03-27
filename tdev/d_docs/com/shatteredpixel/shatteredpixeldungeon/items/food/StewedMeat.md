# StewedMeat 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/food/StewedMeat.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.food |
| **文件类型** | class |
| **继承关系** | extends Food |
| **代码行数** | 79 行 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
StewedMeat 类实现"炖肉"食物，是一种通过炼金制作的安全可食用肉类。提供中等饱腹值恢复，无负面效果。

### 系统定位
StewedMeat 是 Food 的简单子类，作为神秘肉经过炼金加工后的安全版本。它是游戏中唯一通过炼金批量加工肉类的方式。

### 不负责什么
- 不负责炼金配方的注册（由内部 Recipe 类处理）
- 不负责神秘肉到炖肉的自动化流程

## 3. 结构总览

### 主要成员概览
- **实例字段**：`image`、`energy`（在初始化块设置）
- **覆写方法**：`value()`
- **内部类**：`oneMeat`、`twoMeat`、`threeMeat`（炼金配方）

### 主要逻辑块概览
- **安全食用**：无随机负面效果
- **批量炼金**：支持 1-3 个神秘肉同时加工

### 生命周期/调用时机
- 炼金锅中制作
- 作为安全食品食用

## 4. 继承与协作关系

### 父类提供的能力
继承自 Food 类的完整食用功能。

### 覆写的方法
| 方法名 | 覆写内容 |
|--------|----------|
| `value()` | 返回 8 * quantity |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Hunger` | 提供饱腹值常量 |
| `Recipe` | 炼金配方基类 |
| `MysteryMeat` | 配方原料 |

## 5. 字段/常量详解

### 实例字段（初始化块设置）
| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.STEWED | 炖肉图标 |
| `energy` | float | Hunger.HUNGRY/2f (约150) | 饱腹值 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认无参构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.STEWED;
    energy = Hunger.HUNGRY/2f;
}
```

## 7. 方法详解

### value()

**可见性**：public

**是否覆写**：是，覆写自 Food

**方法职责**：计算物品价值。

**返回值**：int，8 * quantity

---

### Recipe 内部类

提供三种不同批量的炼金配方：

#### oneMeat
- **输入**：1x MysteryMeat
- **成本**：1 能量
- **产出**：1x StewedMeat

#### twoMeat
- **输入**：2x MysteryMeat
- **成本**：2 能量
- **产出**：2x StewedMeat

#### threeMeat
- **输入**：3x MysteryMeat
- **成本**：2 能量
- **产出**：3x StewedMeat

**配方示例**（oneMeat）：
```java
public static class oneMeat extends Recipe.SimpleRecipe{
    {
        inputs =  new Class[]{MysteryMeat.class};
        inQuantity = new int[]{1};
        cost = 1;
        output = StewedMeat.class;
        outQuantity = 1;
    }
}
```

## 8. 对外暴露能力

### 显式 API
继承自 Food 的所有公开方法。

### 炼金配方
三种 Recipe 内部类提供不同批量的加工选项。

## 9. 运行机制与调用链

### 创建时机
- 炼金锅中加工神秘肉

### 炼金流程
```
神秘肉 + 炼金能量
    ↓
StewedMeat.Recipe.brew()
    ↓
返回 StewedMeat 实例
```

### 与其他肉类加工的比较
| 加工方式 | 产物 | 饱腹值 | 特点 |
|----------|------|--------|------|
| 燃烧 | ChargrilledMeat | 150 | 需要燃烧 Buff |
| 冻结 | FrozenCarpaccio | 150 | 有随机增益 |
| 炼金 | StewedMeat | 150 | 可批量加工 |

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.food.stewedmeat.name` | 炖肉 | 物品名称 |
| `items.food.stewedmeat.eat_msg` | 吃起来还行。 | 食用成功消息 |
| `items.food.stewedmeat.desc` | 烹煮的过程中杀死了肉上面可能携带的任何病菌或是寄生虫。现在应该可以安全的食用它了。 | 物品描述 |
| `items.food.stewedmeat.discover_hint` | 你可通过炼金合成该物品。 | 发现提示 |

### 中文翻译来源
所有中文翻译来自 `core/src/main/assets/messages/items/items_zh.properties` 文件。

## 11. 使用示例

### 基本用法

```java
// 创建炖肉
StewedMeat meat = new StewedMeat();

// 英雄食用
meat.execute(hero, Food.AC_EAT);
// 效果：恢复150饥饿值，无负面效果
```

### 炼金合成

```java
// 使用配方
Recipe recipe = new StewedMeat.threeMeat();
// 需要 3x MysteryMeat + 2 能量
// 产出 3x StewedMeat
```

## 12. 开发注意事项

### 状态依赖
无特殊状态依赖。

### 生命周期耦合
- 仅通过炼金系统创建

### 常见陷阱
- 与烤肉、冷冻肉片相比，没有额外效果
- 但可以批量加工，效率更高

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可添加新的配方变体

### 不建议修改的位置
- 饱腹值设置影响游戏平衡

## 14. 事实核查清单

- [x] 是否已覆盖全部字段 - 已覆盖 image、energy
- [x] 是否已覆盖全部方法 - 已覆盖 value() 及 Recipe 内部类
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