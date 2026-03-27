# Runestone 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/stones/Runestone.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.stones |
| **文件类型** | abstract class |
| **继承关系** | extends Item |
| **代码行数** | 109 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Runestone 是所有符石（Runestone）物品的抽象基类，定义了符石的基本行为模式：可堆叠、默认动作为投掷、激活时产生效果。

### 系统定位
位于物品系统继承链中 Item → Runestone，是所有具体符石类（如 StoneOfAggression、StoneOfBlink 等）的父类。符石是游戏中通过投掷或使用来触发魔法效果的一次性物品。

### 不负责什么
- 不负责具体的激活效果实现（由子类的 `activate()` 方法完成）
- 不负责物品选择界面逻辑（由 InventoryStone 子类处理）

## 3. 结构总览

### 主要成员概览
- `stackable = true` - 可堆叠物品
- `defaultAction = AC_THROW` - 默认动作为投掷
- `anonymous` - 是否为匿名符石
- `PlaceHolder` - 占位符内部类

### 主要逻辑块概览
- `onThrow()` - 投掷时的处理逻辑
- `activate()` - 抽象方法，由子类实现激活效果
- `PlaceHolder` 内部类 - 用于物品显示的占位符

### 生命周期/调用时机
1. 符石被创建/拾取
2. 被投掷到指定位置
3. `onThrow()` 判断是否激活
4. 调用 `activate()` 触发效果

## 4. 继承与协作关系

### 父类提供的能力
从 Item 继承：
- 基础物品属性（数量、图像等）
- `isUpgradable()` - 默认返回 true
- `isIdentified()` - 默认返回 false
- `value()` - 基础价值计算
- `onThrow()` - 基础投掷逻辑

### 覆写的方法
| 方法 | 覆写逻辑 |
|------|---------|
| `onThrow(int cell)` | 处理符石特有的投掷逻辑，包括魔法免疫检查、深坑检查、使用计数等 |
| `isUpgradable()` | 返回 false，符石不可升级 |
| `isIdentified()` | 返回 true，符石默认已鉴定 |
| `value()` | 返回 15 * quantity |
| `energyVal()` | 返回 3 * quantity |

### 实现的接口契约
无显式接口实现。

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Dungeon` | 获取关卡信息、英雄引用 |
| `Actor` | 查找位置上的角色 |
| `Invisibility` | 消除隐形状态 |
| `MagicImmune` | 检查魔法免疫状态 |
| `Catalog` | 记录物品使用 |
| `Talent` | 触发天赋效果 |
| `ItemSpriteSheet` | 精灵图定义 |

### 使用者
所有具体符石类：
- `StoneOfAggression`
- `StoneOfAugmentation`
- `StoneOfBlast`
- `StoneOfBlink`
- `StoneOfClairvoyance`
- `StoneOfDeepSleep`
- `StoneOfDetectMagic`
- `StoneOfEnchantment`
- `StoneOfFear`
- `StoneOfFlock`
- `StoneOfIntuition`
- `StoneOfShock`
- `InventoryStone`（抽象子类）

## 5. 字段/常量详解

### 静态常量
无静态常量定义。

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `stackable` | boolean | true | 继承自 Item，在实例初始化块中设置，表示符石可堆叠 |
| `defaultAction` | String | AC_THROW | 在实例初始化块中设置，默认动作为投掷 |
| `anonymous` | boolean | false | 标记是否为匿名符石，匿名符石不计入使用统计、不掉落 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过实例初始化块设置属性：

```java
{
    stackable = true;
    defaultAction = AC_THROW;
}
```

### 初始化块
- `stackable = true` - 设置符石可堆叠
- `defaultAction = AC_THROW` - 默认动作为投掷

### 初始化注意事项
子类需要在自己的实例初始化块中设置 `image` 属性以指定正确的精灵图。

## 7. 方法详解

### anonymize()

**可见性**：public

**是否覆写**：否

**方法职责**：将符石标记为匿名状态，同时修改其图像为通用符石占位图。

**参数**：无

**返回值**：void

**前置条件**：无

**副作用**：
- 修改 `image` 为 `ItemSpriteSheet.STONE_HOLDER`
- 设置 `anonymous = true`

**核心实现逻辑**：
```java
public void anonymize(){
    image = ItemSpriteSheet.STONE_HOLDER;
    anonymous = true;
}
```

**边界情况**：匿名符石用于仅需要符石效果而不需要物品实体存在的场景，如某些特殊机制生成的符石。

---

### onThrow(int cell)

**可见性**：protected

**是否覆写**：是，覆写自 Item

**方法职责**：处理符石被投掷到指定位置时的逻辑，判断是否激活符石效果。

**参数**：
- `cell` (int)：投掷目标格子的坐标

**返回值**：void

**前置条件**：符石已被投掷

**副作用**：
- 可能触发 `Catalog.countUse()` 记录使用
- 可能触发 `Talent.onRunestoneUsed()` 天赋效果
- 消除隐形状态
- 可能触发 `Dungeon.level.pressCell()`

**核心实现逻辑**：
```java
@Override
protected void onThrow(int cell) {
    // 判断条件：
    // 1. 是 InventoryStone 类型（走普通物品投掷逻辑）
    // 2. 英雄有 MagicImmune buff（魔法免疫）
    // 3. 目标是深坑且没有角色在该位置
    if (this instanceof InventoryStone ||
            Dungeon.hero.buff(MagicImmune.class) != null ||
            (Dungeon.level.pit[cell] && Actor.findChar(cell) == null)){
        if (!anonymous) super.onThrow( cell );
    } else {
        // 正常激活符石
        if (!anonymous) {
            Catalog.countUse(getClass());
            Talent.onRunestoneUsed(curUser, cell, getClass());
        }
        activate(cell);
        if (Actor.findChar(cell) == null) Dungeon.level.pressCell( cell );
        Invisibility.dispel();
    }
}
```

**边界情况**：
- 匿名符石不执行使用统计和天赋触发
- 魔法免疫的英雄投掷符石不触发效果
- 符石落入深坑不触发效果（除非有角色在深坑位置）

---

### activate(int cell)

**可见性**：protected

**是否覆写**：否，这是一个抽象方法

**方法职责**：定义符石激活时产生的效果，由子类实现具体逻辑。

**参数**：
- `cell` (int)：激活位置的格子坐标

**返回值**：void

**前置条件**：符石已到达激活位置

**副作用**：由子类实现决定

**核心实现逻辑**：
```java
protected abstract void activate(int cell);
```

**边界情况**：此方法必须由所有具体符石子类实现。

---

### isUpgradable()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：判断符石是否可以被升级。

**参数**：无

**返回值**：boolean，始终返回 false

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
@Override
public boolean isUpgradable() {
    return false;
}
```

**边界情况**：符石作为消耗品不支持升级。

---

### isIdentified()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：判断符石是否已被鉴定。

**参数**：无

**返回值**：boolean，始终返回 true

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
@Override
public boolean isIdentified() {
    return true;
}
```

**边界情况**：符石默认是已鉴定状态，玩家可以直接知道符石的种类。

---

### value()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：计算符石的出售价值（金币）。

**参数**：无

**返回值**：int，返回 15 * quantity

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
@Override
public int value() {
    return 15 * quantity;
}
```

**边界情况**：单个符石价值15金币。

---

### energyVal()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：计算符石的炼金能量价值。

**参数**：无

**返回值**：int，返回 3 * quantity

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
@Override
public int energyVal() {
    return 3 * quantity;
}
```

**边界情况**：单个符石价值3点炼金能量。

---

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `anonymize()` | 将符石标记为匿名状态 |
| `isUpgradable()` | 返回 false，符石不可升级 |
| `isIdentified()` | 返回 true，符石默认已鉴定 |
| `value()` | 获取出售价值 |
| `energyVal()` | 获取炼金能量价值 |

### 内部辅助方法
| 方法 | 用途 |
|------|------|
| `onThrow(int cell)` | 处理投掷逻辑 |
| `activate(int cell)` | 抽象方法，子类实现激活效果 |

### 扩展入口
- `activate(int cell)` - 所有具体符石类必须实现此方法定义自己的效果
- `onThrow(int cell)` - 可覆写以改变投掷行为（如 StoneOfBlink）

## 9. 运行机制与调用链

### 创建时机
- 在关卡生成时随机生成
- 通过炼金合成获得
- 商店购买
- 特殊机制触发时创建匿名符石

### 调用者
- `Hero` - 英雄投掷或使用符石
- `Item.throwPos()` - 计算投掷位置
- 各种生成符石的机制

### 被调用者
- `Dungeon.level` - 关卡数据
- `Actor.findChar()` - 查找位置上的角色
- `Catalog.countUse()` - 记录使用统计
- `Talent.onRunestoneUsed()` - 触发天赋
- `Invisibility.dispel()` - 消除隐形

### 系统流程位置
```
投掷动作 → Item.execute(AC_THROW) → Item.doThrow() → Item.onThrow()
    → Runestone.onThrow() → activate() → 子类实现的具体效果
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.stones.runestone$placeholder.name | 符石 | PlaceHolder 内部类的名称 |
| items.stones.runestone.discover_hint | 你可在地牢中概率找到该物品，或通过炼金合成该物品。 | 发现提示 |

### 依赖的资源
- `ItemSpriteSheet.STONE_HOLDER` - 符石占位符精灵图（用于匿名符石和 PlaceHolder）

### 中文翻译来源
来自 `items_zh.properties` 文件。

## 11. 使用示例

### 基本用法
```java
// 创建并使用符石（通常由子类实现）
StoneOfBlink stone = new StoneOfBlink();
stone.quantity = 5;

// 投掷符石
stone.doThrow(hero, targetCell);
```

### 匿名符石用法
```java
// 创建匿名符石（用于特殊机制，不计入使用统计）
StoneOfBlast blastStone = new StoneOfBlast();
blastStone.anonymize();
blastStone.activate(targetCell);
```

### PlaceHolder 用法
```java
// 创建符石占位符（用于界面显示）
Runestone.PlaceHolder placeholder = new Runestone.PlaceHolder();
// 可用于物品列表中代表所有符石类型
```

## 12. 开发注意事项

### 状态依赖
- `anonymous` 字段影响使用统计、天赋触发和物品掉落行为
- 投掷时检查 `MagicImmune` buff 决定是否激活效果

### 生命周期耦合
- 符石是消耗品，使用后通常会被销毁
- `InventoryStone` 子类有特殊的激活逻辑，不走普通投掷流程

### 常见陷阱
- 覆写 `activate()` 时忘记处理边界情况（如空位置、深坑等）
- 覆写 `onThrow()` 时忘记调用父类方法或遗漏使用统计

## 13. 修改建议与扩展点

### 适合扩展的位置
- `activate()` 方法 - 创建新符石类型时实现具体效果
- `onThrow()` 方法 - 需要特殊投掷行为时覆写（参考 StoneOfBlink）
- `value()` 和 `energyVal()` 方法 - 为特殊符石定制价值

### 不建议修改的位置
- `isUpgradable()` - 符石设计为不可升级
- `isIdentified()` - 符石设计为默认已鉴定
- 匿名符石机制 - 这是系统级功能，用于特殊场景

### 重构建议
- 可考虑将 `onThrow()` 中的条件判断逻辑提取为独立方法，提高可读性

## 14. 事实核查清单

- [x] 是否已覆盖全部字段（stackable, defaultAction, anonymous）
- [x] 是否已覆盖全部方法（anonymize, onThrow, activate, isUpgradable, isIdentified, value, energyVal）
- [x] 是否已检查继承链与覆写关系（extends Item，覆写4个方法）
- [x] 是否已核对官方中文翻译（符石）
- [x] 是否存在任何推测性表述（无）
- [x] 示例代码是否真实可用（是）
- [x] 是否遗漏资源/配置/本地化关联（已列出）
- [x] 是否明确说明了注意事项与扩展点（已说明）

---

## 附：类关系图

```mermaid
classDiagram
    class Item {
        +stackable: boolean
        +defaultAction: String
        +onThrow(int cell)
        +isUpgradable() boolean
        +isIdentified() boolean
        +value() int
        +energyVal() int
    }
    
    class Runestone {
        <<abstract>>
        #anonymous: boolean
        +anonymize()
        #onThrow(int cell)
        #activate(int cell)*
        +isUpgradable() boolean
        +isIdentified() boolean
        +value() int
        +energyVal() int
    }
    
    class PlaceHolder {
        +activate(int cell)
        +isSimilar(Item item) boolean
        +info() String
    }
    
    class InventoryStone {
        <<abstract>>
        +AC_USE: String
        #preferredBag: Class
        +actions(Hero hero) ArrayList
        +execute(Hero hero, String action)
        #activate(int cell)
        #useAnimation()
        #usableOnItem(Item item) boolean
        #onItemSelected(Item item)*
    }
    
    class ConcreteStones {
        <<interface>>
        +activate(int cell)
    }
    
    Item <|-- Runestone
    Runestone <|-- InventoryStone
    Runestone <|-- ConcreteStones
    Runestone +-- PlaceHolder
```