# ChargrilledMeat 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/food/ChargrilledMeat.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.food |
| **文件类型** | class |
| **继承关系** | extends Food |
| **代码行数** | 44 行 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
ChargrilledMeat 类实现"烤肉"食物，是一种通过燃烧效果制作的安全可食用肉类。提供中等饱腹值恢复。

### 系统定位
ChargrilledMeat 是 Food 的简单子类，作为神秘肉经过火焰处理后的安全版本。它是肉类食物加工链的一环。

### 不负责什么
- 不负责肉类的生成逻辑（由燃烧 Buff 或其他机制触发）
- 不负责神秘肉到烤肉的转化流程（由外部逻辑调用 `cook()` 方法）

## 3. 结构总览

### 主要成员概览
- **实例字段**：`image`、`energy`（在初始化块设置）
- **覆写方法**：`value()`
- **静态方法**：`cook(int)`

### 主要逻辑块概览
- **初始化**：设置图标和饱腹值
- **工厂方法**：`cook()` 静态方法创建实例

### 生命周期/调用时机
- 神秘肉被燃烧时自动转化为烤肉
- 炼金合成时创建

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
| `ItemSpriteSheet` | 提供图标资源 |

### 使用者
- `Burning` Buff（燃烧效果）
- 炼金系统

## 5. 字段/常量详解

### 实例字段（初始化块设置）
| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.STEAK | 烤肉图标 |
| `energy` | float | Hunger.HUNGRY/2f (约150) | 饱腹值，约为普通口粮的一半 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认无参构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.STEAK;
    energy = Hunger.HUNGRY/2f;
}
```

## 7. 方法详解

### value()

**可见性**：public

**是否覆写**：是，覆写自 Food

**方法职责**：计算烤肉的金币价值。

**返回值**：int，8 * quantity

**核心实现逻辑**：
```java
@Override
public int value() {
    return 8 * quantity;
}
```

---

### cook(int quantity) [静态方法]

**可见性**：public static

**是否覆写**：否

**方法职责**：工厂方法，创建指定数量的烤肉。

**参数**：
- `quantity` (int)：要创建的数量

**返回值**：Food，烤肉实例

**核心实现逻辑**：
```java
public static Food cook( int quantity ) {
    ChargrilledMeat result = new ChargrilledMeat();
    result.quantity = quantity;
    return result;
}
```

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `cook(int)` | 工厂方法，创建烤肉实例 |

### 继承的 API
继承自 Food 的所有公开方法。

## 9. 运行机制与调用链

### 创建时机
- 神秘肉受燃烧效果影响时
- 炼金合成时

### 调用链
```
神秘肉 + Burning Buff
    ↓
ChargrilledMeat.cook(quantity)
    ↓
创建烤肉实例
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.food.chargrilledmeat.name` | 烤肉 | 物品名称 |
| `items.food.chargrilledmeat.desc` | 看起来像块好肉排。 | 物品描述 |
| `items.food.chargrilledmeat.discover_hint` | 你可使用另一种食物制作该物品。 | 发现提示 |

### 中文翻译来源
所有中文翻译来自 `core/src/main/assets/messages/items/items_zh.properties` 文件。

## 11. 使用示例

### 基本用法

```java
// 创建单个烤肉
ChargrilledMeat meat = new ChargrilledMeat();

// 使用工厂方法创建多个
Food meats = ChargrilledMeat.cook(3);

// 英雄食用
meats.execute(hero, Food.AC_EAT);
```

### 从神秘肉转化

```java
// 神秘肉被燃烧时（由 Burning Buff 触发）
MysteryMeat raw = new MysteryMeat();
raw.quantity(2);
Food cooked = ChargrilledMeat.cook(raw.quantity());
```

## 12. 开发注意事项

### 状态依赖
无特殊状态依赖。

### 生命周期耦合
- 作为神秘肉的加工产品存在
- 与燃烧效果机制关联

### 常见陷阱
- `cook()` 是静态方法，不消耗原料，需调用者处理原料消耗

## 13. 修改建议与扩展点

### 适合扩展的位置
- 无特殊扩展需求

### 不建议修改的位置
- 饱腹值设置应与游戏平衡保持一致

## 14. 事实核查清单

- [x] 是否已覆盖全部字段 - 已覆盖 image、energy
- [x] 是否已覆盖全部方法 - 已覆盖 value()、cook()
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