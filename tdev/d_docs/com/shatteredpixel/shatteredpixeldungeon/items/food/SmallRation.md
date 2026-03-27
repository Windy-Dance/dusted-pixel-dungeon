# SmallRation 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/food/SmallRation.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.food |
| **文件类型** | class |
| **继承关系** | extends Food |
| **代码行数** | 38 行 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
SmallRation 类实现"小包口粮"食物，是一种饱腹值较低的商店食物。提供普通口粮一半的饥饿恢复。

### 系统定位
SmallRation 是 Food 的简单子类，作为商店中可购买的廉价食物选项。它是游戏中最基础的食物来源之一。

### 不负责什么
- 不负责商店的价格设定
- 不负责特殊的食用效果

## 3. 结构总览

### 主要成员概览
- **实例字段**：`image`、`energy`（在初始化块设置）
- **覆写方法**：`value()`

### 主要逻辑块概览
- **低饱腹值**：energy = Hunger.HUNGRY/2 (150f)，是普通口粮的一半

### 生命周期/调用时机
- 商店购买
- 部分关卡随机生成

## 4. 继承与协作关系

### 父类提供的能力
继承自 Food 类的完整食用功能。

### 覆写的方法
| 方法名 | 覆写内容 |
|--------|----------|
| `value()` | 返回 10 * quantity |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Hunger` | 提供饱腹值常量 |
| `ItemSpriteSheet` | 提供图标资源 |

## 5. 字段/常量详解

### 实例字段（初始化块设置）
| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.OVERPRICED | 小包口粮图标 |
| `energy` | float | Hunger.HUNGRY/2f (150f) | 饱腹值，普通口粮的一半 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认无参构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.OVERPRICED;
    energy = Hunger.HUNGRY/2f;
}
```

## 7. 方法详解

### value()

**可见性**：public

**是否覆写**：是，覆写自 Food

**方法职责**：计算物品价值。

**返回值**：int，10 * quantity

**核心实现逻辑**：
```java
@Override
public int value() {
    return 10 * quantity;
}
```

## 8. 对外暴露能力

### 显式 API
继承自 Food 的所有公开方法。

## 9. 运行机制与调用链

### 创建时机
- 商店购买
- 部分关卡随机生成

### 与普通口粮的比较
| 属性 | 小包口粮 | 普通口粮 |
|------|---------|---------|
| 饱腹值 | 150 | 300 |
| 价值 | 10 | 10 |
| 食用时间 | 3回合 | 3回合 |

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.food.smallration.name` | 小包口粮 | 物品名称 |
| `items.food.smallration.eat_msg` | 吃起来还行。 | 食用成功消息 |
| `items.food.smallration.desc` | 它看起来和普通口粮一样，就是小了点。 | 物品描述 |
| `items.food.smallration.discover_hint` | 你可在商店中购买该物品。 | 发现提示 |

### 中文翻译来源
所有中文翻译来自 `core/src/main/assets/messages/items/items_zh.properties` 文件。

## 11. 使用示例

### 基本用法

```java
// 创建小包口粮
SmallRation ration = new SmallRation();

// 英雄食用
ration.execute(hero, Food.AC_EAT);
// 效果：恢复150饥饿值
```

## 12. 开发注意事项

### 状态依赖
无特殊状态依赖。

### 生命周期耦合
- 主要通过商店获取

### 常见陷阱
- 饱腹值仅为普通口粮的一半，性价比相同

## 13. 修改建议与扩展点

### 适合扩展的位置
- 无特殊扩展需求

### 不建议修改的位置
- 饱腹值和价值影响游戏经济平衡

## 14. 事实核查清单

- [x] 是否已覆盖全部字段 - 已覆盖 image、energy
- [x] 是否已覆盖全部方法 - 已覆盖 value()
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