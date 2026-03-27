# StoneOfIntuition 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/stones/StoneOfIntuition.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.stones |
| **文件类型** | class |
| **继承关系** | extends InventoryStone |
| **代码行数** | 225 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
StoneOfIntuition（感知符石）是一种背包型符石，允许玩家通过猜测来鉴定药剂、卷轴或戒指的类型。猜对则鉴定成功，猜错则无效果。

### 系统定位
位于 InventoryStone → StoneOfIntuition 继承链中，是一种鉴定辅助道具，比鉴定卷轴风险更大但可节省资源。

### 不负责什么
- 不负责直接鉴定物品
- 不负责鉴定武器、护甲、法杖等物品

## 3. 结构总览

### 主要成员概览
- `image` - 精灵图设置
- `curGuess` - 当前猜测类型
- `IntuitionUseTracker` - 使用追踪器
- `WndGuess` - 猜测窗口内部类

### 主要逻辑块概览
- `usableOnItem()` - 判断物品是否可猜测
- `onItemSelected()` - 显示猜测窗口
- `desc()` - 追加使用提示
- `WndGuess` - 猜测界面

### 生命周期/调用时机
1. 玩家在背包中使用符石
2. 选择要猜测的物品
3. 显示猜测选项
4. 玩家做出猜测
5. 判断猜测结果

## 4. 继承与协作关系

### 父类提供的能力
从 InventoryStone 继承：
- `AC_USE` - 使用动作
- `itemSelector` - 物品选择器
- `useAnimation()` - 使用动画

### 覆写的方法
| 方法 | 覆写逻辑 |
|------|----------|
| `usableOnItem(Item item)` | 只允许未鉴定的药剂、卷轴、戒指 |
| `onItemSelected(Item item)` | 显示猜测窗口 |
| `desc()` | 追加使用提示信息 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Item` | 物品基类 |
| `Potion` | 药剂类 |
| `ExoticPotion` | 异域药剂 |
| `Ring` | 戒指类 |
| `Scroll` | 卷轴类 |
| `ExoticScroll` | 异域卷轴 |
| `Buff` | Buff 管理器 |
| `Catalog` | 使用统计 |
| `Talent` | 天赋系统 |
| `Identification` | 鉴定视觉效果 |
| `GameScene` | 游戏场景 |
| `Window` | UI 窗口基类 |
| `GLog` | 游戏日志 |
| `Reflection` | 反射工具 |
| `Messages` | 国际化消息 |

## 5. 字段/常量详解

### 静态字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `curGuess` | Class | null | 当前猜测的物品类型 |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `image` | int | ItemSpriteSheet.STONE_INTUITION | 符石精灵图 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过实例初始化块设置属性：

```java
{
    image = ItemSpriteSheet.STONE_INTUITION;
}
```

## 7. 方法详解

### usableOnItem(Item item)

**可见性**：protected

**是否覆写**：是，覆写自 InventoryStone

**方法职责**：判断物品是否可以进行猜测鉴定。

**参数**：
- `item` (Item)：要检查的物品

**返回值**：boolean，是否可猜测

**核心实现逻辑**：
```java
@Override
protected boolean usableOnItem(Item item) {
    if (item instanceof Ring){
        return !((Ring) item).isKnown();
    } else if (item instanceof Potion){
        return !((Potion) item).isKnown();
    } else if (item instanceof Scroll){
        return !((Scroll) item).isKnown();
    }
    return false;
}
```

**边界情况**：
- 只支持未鉴定的戒指、药剂、卷轴
- 已知类型的物品不可选择

---

### onItemSelected(Item item)

**可见性**：protected

**是否覆写**：是，覆写自 InventoryStone

**方法职责**：显示猜测窗口。

**参数**：
- `item` (Item)：选择的物品

**核心实现逻辑**：
```java
@Override
protected void onItemSelected(Item item) {
    GameScene.show( new WndGuess(item));
}
```

---

### desc()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：返回物品描述，追加使用提示。

**返回值**：String，描述文本

**核心实现逻辑**：
```java
@Override
public String desc() {
    String text = super.desc();
    if (Dungeon.hero != null){
        if (Dungeon.hero.buff(IntuitionUseTracker.class) == null){
            text += "\n\n" + Messages.get(this, "break_info");
        } else {
            text += "\n\n" + Messages.get(this, "break_warn");
        }
    }
    return text;
}
```

**使用提示说明**：
- 首次使用：下次使用不消耗符石
- 已使用过：下次使用会消耗符石

## 8. 内部类详解

### IntuitionUseTracker

**类型**：public static class extends Buff

**职责**：追踪感知符石的使用状态，实现"使用两次才消耗一个"的机制。

**核心实现**：
```java
public static class IntuitionUseTracker extends Buff {
    { revivePersists = true; }
};
```

**特点**：
- `revivePersists = true` 表示 Buff 在英雄复活后保持
- 作为标记使用，不产生实际效果

---

### WndGuess

**类型**：public class extends Window

**职责**：显示猜测界面，让玩家选择猜测的物品类型。

**常量**：
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| WIDTH | int | 120 | 窗口宽度 |
| BTN_SIZE | int | 20 | 按钮尺寸 |

**构造器**：
```java
public WndGuess(final Item item)
```

**核心逻辑**：
1. 显示物品图标和标题
2. 显示提示文本
3. 列出所有可能的物品类型图标按钮
4. 玩家选择后显示确认按钮
5. 确认后判断猜测结果

**猜测结果处理**：
```java
// 猜对了
if (item.getClass() == curGuess){
    if (item instanceof Ring){
        ((Ring) item).setKnown();
    } else {
        item.identify();
    }
    GLog.p(Messages.get(WndGuess.class, "correct"));
    curUser.sprite.parent.add(new Identification(curUser.sprite.center().offset(0, -16)));
} else {
    // 猜错了
    GLog.w(Messages.get(WndGuess.class, "incorrect"));
}

// 消耗逻辑
if (!anonymous) {
    Catalog.countUse(StoneOfIntuition.class);
    if (curUser.buff(IntuitionUseTracker.class) == null) {
        // 首次使用，添加追踪器，不消耗符石
        Buff.affect(curUser, IntuitionUseTracker.class);
    } else {
        // 第二次使用，消耗符石，移除追踪器
        curItem.detach(curUser.belongings.backpack);
        curUser.buff(IntuitionUseTracker.class).detach();
    }
    Talent.onRunestoneUsed(curUser, curUser.pos, StoneOfIntuition.class);
}
```

**特殊处理**：
- 异域药剂/卷轴显示对应的基础类型选项
- 5个以下选项单行显示，超过则双行

## 9. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `usableOnItem(Item)` | 判断物品是否可猜测 |
| `onItemSelected(Item)` | 显示猜测窗口 |
| `desc()` | 获取描述（含使用提示） |

## 10. 运行机制与调用链

```
使用符石 → InventoryStone.execute(AC_USE)
    → GameScene.selectItem() 显示物品选择
    → 玩家选择物品 → onItemSelected()
    → GameScene.show(WndGuess) 显示猜测窗口
    → 玩家选择猜测类型
    → 判断猜测结果
    → 处理消耗逻辑
```

## 11. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.stones.stoneofintuition.name | 感知符石 | 物品名称 |
| items.stones.stoneofintuition.inv_title | 选择一件物品 | 选择界面标题 |
| items.stones.stoneofintuition.desc | 这块符石含有的魔法就像一个弱化版的鉴定卷轴... | 物品描述 |
| items.stones.stoneofintuition.break_info | 你可以在其粉碎前使用两次感知符石... | 首次提示 |
| items.stones.stoneofintuition.break_warn | 你之前已经使用了一次感知符石... | 再次提示 |
| items.stones.stoneofintuition$wndguess.text | 猜猜这件未鉴定道具是什么... | 猜测提示 |
| items.stones.stoneofintuition$wndguess.correct | 猜测正确，此类物品已被鉴定！ | 正确提示 |
| items.stones.stoneofintuition$wndguess.incorrect | 你猜错了。 | 错误提示 |
| items.stones.stoneofintuition$wndguess.break | 你的感知符石化为了尘土... | 消耗提示 |

### 中文翻译来源
来自 `items_zh.properties` 文件。

## 12. 使用示例

### 基本用法
```java
// 使用感知符石
StoneOfIntuition stone = new StoneOfIntuition();

// 玩家选择未鉴定的药剂/卷轴/戒指
// 显示猜测选项界面
// 玩家选择猜测类型
// 正确则鉴定成功，错误则无效果
```

### 特殊机制
```java
// 首次使用：添加 IntuitionUseTracker，不消耗符石
// 第二次使用：消耗符石，移除 IntuitionUseTracker
// 可通过 desc() 查看当前状态
```

## 13. 开发注意事项

### 状态依赖
- IntuitionUseTracker 是持久化 Buff，英雄复活后保持
- 使用两次才消耗一个符石

### 常见陷阱
- 猜错没有任何惩罚（除了浪费一次使用机会）
- 异域物品的选项是基础类型

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述（无）
- [x] 示例代码是否真实可用

---

## 附：类关系图

```mermaid
classDiagram
    class InventoryStone {
        <<abstract>>
        #usableOnItem(Item item) boolean
        #onItemSelected(Item item)*
    }
    
    class StoneOfIntuition {
        -curGuess: Class$
        +usableOnItem(Item item) boolean
        #onItemSelected(Item item)
        +desc() String
    }
    
    class IntuitionUseTracker {
        +revivePersists: boolean
    }
    
    class WndGuess {
        +WIDTH: int
        +BTN_SIZE: int
        +WndGuess(Item item)
    }
    
    class Window {
        <<abstract>>
    }
    
    InventoryStone <|-- StoneOfIntuition
    Buff <|-- IntuitionUseTracker
    Window <|-- WndGuess
    StoneOfIntuition +-- IntuitionUseTracker
    StoneOfIntuition +-- WndGuess
```