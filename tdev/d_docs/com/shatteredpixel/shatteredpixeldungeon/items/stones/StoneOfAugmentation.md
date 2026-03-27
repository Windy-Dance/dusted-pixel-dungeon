# StoneOfAugmentation 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/stones/StoneOfAugmentation.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.stones |
| **文件类型** | class |
| **继承关系** | extends InventoryStone |
| **代码行数** | 169 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
StoneOfAugmentation（强化符石）是一种背包型符石，用于强化装备的一种属性（如伤害或速度），代价是减弱另一种属性。

### 系统定位
位于 InventoryStone → StoneOfAugmentation 继承链中，是一种装备强化道具，可通过商店购买或炼金合成获得。

### 不负责什么
- 不负责直接提升装备基础数值
- 不负责附魔（由 StoneOfEnchantment 处理）

## 3. 结构总览

### 主要成员概览
- `preferredBag` - 优先显示背包
- `WndAugment` - 强化选择窗口内部类

### 主要逻辑块概览
- `usableOnItem()` - 判断物品是否可强化
- `onItemSelected()` - 显示强化选择窗口
- `apply()` - 应用强化效果
- `WndAugment` - UI 窗口

### 生命周期/调用时机
1. 玩家在背包中使用符石
2. 选择要强化的装备
3. 显示强化选项窗口
4. 选择强化类型
5. 应用强化效果

## 4. 继承与协作关系

### 父类提供的能力
从 InventoryStone 继承：
- `AC_USE` - 使用动作
- `itemSelector` - 物品选择器
- `useAnimation()` - 使用动画
- `activate()` - 激活方法

### 覆写的方法
| 方法 | 覆写逻辑 |
|------|----------|
| `usableOnItem(Item item)` | 检查物品是否可被附魔（使用 ScrollOfEnchantment.enchantable） |
| `onItemSelected(Item item)` | 显示强化选择窗口 |
| `value()` | 返回 30 * quantity |
| `energyVal()` | 返回 5 * quantity |

### 实现的接口契约
无显式接口实现。

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Belongings` | 背包类型定义 |
| `Item` | 物品基类 |
| `Weapon` | 武器类 |
| `Armor` | 护甲类 |
| `ScrollOfUpgrade` | 升级卷轴（用于视觉反馈） |
| `ScrollOfEnchantment` | 附魔卷轴（检查可附魔物品） |
| `Catalog` | 使用统计 |
| `Talent` | 天赋系统 |
| `GameScene` | 游戏场景 |
| `Window` | UI 窗口基类 |

### 使用者
- `Hero` - 英雄使用
- 商店 - 出售
- 炼金系统 - 合成

## 5. 字段/常量详解

### 静态常量
无静态常量定义。

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `preferredBag` | Class | Belongings.Backpack.class | 优先显示背包类型 |
| `image` | int | ItemSpriteSheet.STONE_AUGMENTATION | 精灵图 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过实例初始化块设置属性：

```java
{
    preferredBag = Belongings.Backpack.class;
    image = ItemSpriteSheet.STONE_AUGMENTATION;
}
```

## 7. 方法详解

### usableOnItem(Item item)

**可见性**：protected

**是否覆写**：是，覆写自 InventoryStone

**方法职责**：判断物品是否可以被强化。

**参数**：
- `item` (Item)：要检查的物品

**返回值**：boolean，是否可强化

**核心实现逻辑**：
```java
@Override
protected boolean usableOnItem(Item item) {
    return ScrollOfEnchantment.enchantable(item);
}
```

---

### onItemSelected(Item item)

**可见性**：protected

**是否覆写**：是，覆写自 InventoryStone

**方法职责**：显示强化选择窗口。

**参数**：
- `item` (Item)：选择的物品

**核心实现逻辑**：
```java
@Override
protected void onItemSelected(Item item) {
    GameScene.show(new WndAugment( item));
}
```

---

### apply(Weapon weapon, Weapon.Augment augment)

**可见性**：public

**是否覆写**：否

**方法职责**：对武器应用强化效果。

**参数**：
- `weapon` (Weapon)：要强化的武器
- `augment` (Weapon.Augment)：强化类型（SPEED/DAMAGE/NONE）

**返回值**：void

**副作用**：
- 修改武器的 augment 属性
- 播放使用动画
- 触发升级视觉效果
- 记录使用统计
- 消耗符石

**核心实现逻辑**：
```java
public void apply( Weapon weapon, Weapon.Augment augment ) {
    weapon.augment = augment;
    useAnimation();
    ScrollOfUpgrade.upgrade(curUser);
    if (!anonymous) {
        curItem.detach(curUser.belongings.backpack);
        Catalog.countUse(getClass());
        Talent.onRunestoneUsed(curUser, curUser.pos, getClass());
    }
}
```

---

### apply(Armor armor, Armor.Augment augment)

**可见性**：public

**是否覆写**：否

**方法职责**：对护甲应用强化效果。

**参数**：
- `armor` (Armor)：要强化的护甲
- `augment` (Armor.Augment)：强化类型（EVASION/DEFENSE/NONE）

**返回值**：void

**副作用**：同武器版本的 apply

---

### value()

**可见性**：public

**是否覆写**：是

**返回值**：int，返回 30 * quantity

---

### energyVal()

**可见性**：public

**是否覆写**：是

**返回值**：int，返回 5 * quantity

## 8. 内部类详解

### WndAugment

**类型**：public class extends Window

**职责**：显示强化选项选择窗口。

**常量**：
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| WIDTH | int | 120 | 窗口宽度 |
| MARGIN | int | 2 | 边距 |
| BUTTON_WIDTH | int | WIDTH - MARGIN * 2 | 按钮宽度 |
| BUTTON_HEIGHT | int | 20 | 按钮高度 |

**构造器**：
```java
public WndAugment( final Item toAugment )
```

**核心逻辑**：
- 显示物品图标和标题
- 显示"你想强化哪个属性？"提示
- 根据物品类型（武器/护甲）显示对应的强化选项
- 提供"算了"取消按钮

**武器选项**：
- 速度（SPEED）
- 伤害（DAMAGE）
- 移除强化（NONE）

**护甲选项**：
- 闪避（EVASION）
- 防御（DEFENSE）
- 移除强化（NONE）

## 9. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `apply(Weapon, Augment)` | 对武器应用强化 |
| `apply(Armor, Augment)` | 对护甲应用强化 |

### 扩展入口
- `WndAugment` 可通过修改按钮文本自定义

## 10. 运行机制与调用链

```
使用符石 → InventoryStone.execute(AC_USE)
    → GameScene.selectItem() 显示物品选择
    → 玩家选择物品 → onItemSelected()
    → GameScene.show(WndAugment) 显示强化窗口
    → 玩家选择强化类型 → apply()
    → 应用强化效果 → 消耗符石
```

## 11. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.stones.stoneofaugmentation.name | 强化符石 | 物品名称 |
| items.stones.stoneofaugmentation.inv_title | 强化一件物品 | 选择界面标题 |
| items.stones.stoneofaugmentation.desc | 这颗符石内的强力魔法可以用于强化装备... | 物品描述 |
| items.stones.stoneofaugmentation.discover_hint | 你可在商店中中购买该物品... | 发现提示 |
| items.stones.stoneofaugmentation$wndaugment.choice | 你想强化哪个属性？ | 选择提示 |
| items.stones.stoneofaugmentation$wndaugment.speed | 速度 | 速度选项 |
| items.stones.stoneofaugmentation$wndaugment.damage | 伤害 | 伤害选项 |
| items.stones.stoneofaugmentation$wndaugment.evasion | 闪避 | 闪避选项 |
| items.stones.stoneofaugmentation$wndaugment.defense | 防御 | 防御选项 |
| items.stones.stoneofaugmentation$wndaugment.none | 移除强化 | 移除选项 |
| items.stones.stoneofaugmentation$wndaugment.cancel | 算了 | 取消按钮 |

### 中文翻译来源
来自 `items_zh.properties` 文件。

## 12. 使用示例

### 基本用法
```java
// 使用强化符石
StoneOfAugmentation stone = new StoneOfAugmentation();

// 玩家选择物品后显示强化窗口
// 选择后调用 apply() 方法
stone.apply(weapon, Weapon.Augment.SPEED); // 强化速度
stone.apply(armor, Armor.Augment.DEFENSE); // 强化防御
```

## 13. 开发注意事项

### 状态依赖
- 只有可附魔的物品才能被强化
- 强化会修改物品的 augment 属性

### 常见陷阱
- 强化后物品属性会变化，需要注意平衡性
- 移除强化不会恢复原有属性

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述（无）
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联

---

## 附：类关系图

```mermaid
classDiagram
    class InventoryStone {
        <<abstract>>
        #onItemSelected(Item item)*
    }
    
    class StoneOfAugmentation {
        +apply(Weapon weapon, Augment augment)
        +apply(Armor armor, Augment augment)
        +value() int
        +energyVal() int
    }
    
    class WndAugment {
        +WIDTH: int
        +MARGIN: int
        +WndAugment(Item toAugment)
    }
    
    class Window {
        <<abstract>>
    }
    
    InventoryStone <|-- StoneOfAugmentation
    Window <|-- WndAugment
    StoneOfAugmentation +-- WndAugment
```