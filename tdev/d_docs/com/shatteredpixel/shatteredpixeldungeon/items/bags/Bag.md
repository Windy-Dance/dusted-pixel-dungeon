# Bag 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\items\bags\Bag.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.bags |
| **文件类型** | class |
| **继承关系** | extends Item implements Iterable<Item> |
| **代码行数** | 258 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Bag类是所有背包物品的基类，提供容器功能来存储其他物品。它实现了物品容器的核心逻辑，包括物品收集、容量管理、序列化和迭代功能。

### 系统定位
在物品系统中作为专门的容器类别存在，允许玩家将特定类型的物品组织到专门的背包中，提高背包管理效率。

### 不负责什么
- 不定义具体的物品过滤规则（由子类实现）
- 不处理具体的UI显示逻辑（由WndQuickBag处理）
- 不管理物品的具体使用逻辑

## 3. 结构总览

### 主要成员概览
- `public static final String AC_OPEN = "OPEN"` - 打开背包的动作常量
- `public Char owner` - 背包的所有者
- `public ArrayList<Item> items = new ArrayList<>()` - 存储的物品列表
- `public Item quickUseItem` - 快速使用的物品引用
- `{ image = 11; defaultAction = AC_OPEN; unique = true; }` - 初始化块

### 主要逻辑块概览
- `execute()` - 处理背包打开动作
- `collect()/onDetach()` - 物品收集和分离逻辑
- `grabItems()` - 从其他容器抓取物品
- `canHold()` - 容量和物品类型检查
- `storeInBundle()/restoreFromBundle()` - 序列化逻辑
- `ItemIterator` - 内部迭代器类

### 生命周期/调用时机
- 创建时：通过子类构造器初始化
- 收集时：当背包被添加到英雄背包中时
- 打开时：玩家选择打开背包动作时
- 分离时：背包被移除或丢弃时
- 序列化时：游戏保存/加载时

## 4. 继承与协作关系

### 父类提供的能力
继承自Item类的所有基础功能：
- 物品的基本属性（name, image, quantity等）
- 物品操作方法（pickup, drop, collect等）
- Bundle序列化支持
- 渲染和显示相关功能

### 覆写的方法
- `execute(Hero hero, String action)` - 处理打开背包动作
- `collect(Bag container)` - 自定义收集逻辑
- `onDetach()` - 自定义分离逻辑
- `isUpgradable()` - 返回false，背包不可升级
- `isIdentified()` - 返回true，背包始终已鉴定
- `storeInBundle(Bundle bundle)` - 自定义序列化逻辑
- `restoreFromBundle(Bundle bundle)` - 自定义反序列化逻辑
- `targetingPos(Hero user, int dst)` - 代理快速使用物品的目标位置

### 实现的接口契约
- `Iterable<Item>` - 提供迭代器支持，允许遍历背包中的所有物品（包括嵌套背包）

### 依赖的关键类
- `Char` - 角色基类，表示背包所有者
- `Hero` - 英雄角色，用于物品交互
- `ArrayList<Item>` - 物品存储容器
- `GameScene` - 游戏场景，用于显示背包窗口
- `WndQuickBag` - 背包UI窗口
- `Dungeon.quickslot` - 快捷栏系统
- `Badges` - 成就系统
- `LostInventory` - 物品丢失状态
- `Bundle` - 序列化系统

### 使用者
- `PotionBandolier`, `ScrollHolder`, `VelvetPouch`, `MagicalHolster` - 具体的背包实现类
- 物品系统 - 处理背包的收集、存储和使用
- UI系统 - 显示背包内容
- 快捷栏系统 - 处理背包中物品的快捷使用

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_OPEN | String | "OPEN" | 打开背包的动作标识符 |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| owner | Char | null | 背包的所有者（通常是Hero） |
| items | ArrayList<Item> | new ArrayList<>() | 存储在背包中的物品列表 |
| quickUseItem | Item | null | 当前快速使用的物品，用于代理目标位置计算 |
| loading | boolean | false | 临时变量，用于在加载时绕过LostInventory限制 |

### 初始化块
```java
{
    image = 11;
    defaultAction = AC_OPEN;
    unique = true;
}
```
- `image = 11`：设置默认背包精灵图索引
- `defaultAction = AC_OPEN`：设置默认动作为打开背包
- `unique = true`：背包在游戏中是唯一的

## 6. 构造与初始化机制

### 构造器
Bag类没有显式的公共构造器，使用默认构造器。子类通常不重写构造器，而是通过初始化块设置特定属性。

### 初始化块
通过实例初始化块设置默认属性，确保所有背包子类都具有这些基本特征。

### 初始化注意事项
- 背包必须正确关联到所有者（owner字段）
- items列表自动初始化为空
- 子类需要覆写canHold方法来定义可接受的物品类型
- 子类可以覆写capacity()方法来定义容量

## 7. 方法详解

### capacity()

**可见性**：public

**是否覆写**：否

**方法职责**：返回背包的容量

**参数**：无

**返回值**：int，默认返回20

**前置条件**：无

**副作用**：无

**核心实现逻辑**：直接返回20，子类可以覆写此方法

**边界情况**：无

### targetingPos()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：计算物品的目标位置，代理快速使用物品的位置计算

**参数**：
- `user` (Hero)：使用物品的角色
- `dst` (int)：目标位置

**返回值**：int，目标位置

**前置条件**：user参数不为null

**副作用**：无

**核心实现逻辑**：
```java
if (quickUseItem != null){
    return quickUseItem.targetingPos(user, dst);
} else {
    return super.targetingPos(user, dst);
}
```
如果设置了quickUseItem，则代理其targetingPos方法，否则调用父类实现。

**边界情况**：无

### execute()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：执行背包动作，主要是打开背包窗口

**参数**：
- `hero` (Hero)：执行动作的英雄
- `action` (String)：要执行的动作

**返回值**：void

**前置条件**：hero和action参数不为null

**副作用**：
- 如果动作是AC_OPEN且背包不为空，显示WndQuickBag窗口
- 重置quickUseItem

**核心实现逻辑**：
1. 重置quickUseItem为null
2. 调用父类execute方法
3. 如果动作是AC_OPEN且items不为空，显示背包窗口

**边界情况**：如果背包为空，不显示窗口

### collect()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：处理背包被收集到容器中的逻辑

**参数**：
- `container` (Bag)：目标容器

**返回值**：boolean，表示是否成功收集

**前置条件**：container参数不为null

**副作用**：
- 设置owner字段
- 从容器中抓取符合条件的物品
- 更新快捷栏占位符
- 验证成就

**核心实现逻辑**：
1. 调用grabItems(container)抓取符合条件的物品
2. 更新快捷栏占位符
3. 调用父类collect方法
4. 设置owner字段并验证成就

**边界情况**：如果父类collect失败，返回false

### onDetach()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：处理背包从容器中分离的逻辑

**参数**：无

**返回值**：void

**前置条件**：无

**副作用**：
- 清除owner字段
- 从快捷栏中移除背包中的物品
- 更新快捷栏显示

**核心实现逻辑**：
1. 清除owner字段
2. 从Dungeon.quickslot中清除所有背包中的物品
3. 调用updateQuickslot()更新显示

**边界情况**：无

### grabItems()

**可见性**：public

**是否覆写**：否

**方法职责**：从英雄的主背包中抓取符合条件的物品

**参数**：无

**返回值**：void

**前置条件**：owner是Hero类型

**副作用**：修改items列表和主背包内容

**核心实现逻辑**：调用grabItems方法，传入英雄的主背包作为参数

**边界情况**：如果owner不是Hero或不是主背包，不执行任何操作

### grabItems(Bag container)

**可见性**：public

**是否覆写**：否

**方法职责**：从指定容器中抓取符合条件的物品

**参数**：
- `container` (Bag)：源容器

**返回值**：void

**前置条件**：container参数不为null

**副作用**：修改items和container的items列表

**核心实现逻辑**：
1. 遍历容器中的所有物品
2. 对于每个符合条件的物品（canHold返回true）：
   - 记录快捷栏槽位
   - 从容器中分离物品
   - 尝试添加到当前背包
   - 如果添加失败，放回原容器
   - 恢复快捷栏槽位

**边界情况**：如果物品无法添加到背包，会放回原容器

### clear()

**可见性**：public

**是否覆写**：否

**方法职责**：清空背包中的所有物品

**参数**：无

**返回值**：void

**前置条件**：无

**副作用**：清空items列表

**核心实现逻辑**：调用items.clear()

**边界情况**：无

### resurrect()

**可见性**：public

**是否覆写**：否

**方法职责**：复活背包，移除非唯一物品

**参数**：无

**返回值**：void

**前置条件**：无

**副作用**：修改items列表

**核心实现逻辑**：遍历所有物品，移除非unique的物品

**边界情况**：只保留unique为true的物品

### canHold(Item item)

**可见性**：public

**是否覆写**：否

**方法职责**：检查背包是否可以容纳指定物品

**参数**：
- `item` (Item)：要检查的物品

**返回值**：boolean，表示是否可以容纳

**前置条件**：item参数不为null

**副作用**：无

**核心实现逻辑**：
1. 检查LostInventory状态（如果不是loading状态）
2. 检查是否已经包含该物品或物品是背包类型
3. 检查容量是否足够
4. 对于可堆叠物品，检查是否有相似物品可以合并

**边界情况**：loading状态下忽略LostInventory限制

### iterator()

**可见性**：public

**是否覆写**：是，实现Iterable接口

**方法职责**：返回背包的迭代器

**参数**：无

**返回值**：Iterator<Item>，深度优先遍历所有物品（包括嵌套背包）

**前置条件**：无

**副作用**：创建新的ItemIterator实例

**核心实现逻辑**：返回新的ItemIterator实例

**边界情况**：无

### ItemIterator内部类

**可见性**：private

**方法职责**：提供深度优先遍历背包中所有物品的迭代器

**核心特性**：
- 递归遍历嵌套背包
- 支持remove操作
- 正确处理背包嵌套结构

## 8. 对外暴露能力

### 显式 API
- `AC_OPEN` 常量：供外部系统识别打开动作
- `capacity()` 方法：获取背包容量
- `canHold(Item)` 方法：检查物品兼容性
- `grabItems()` 方法：主动抓取物品
- `clear()/resurrect()` 方法：管理背包内容
- 迭代器支持：允许遍历所有物品

### 内部辅助方法
- `loading` 字段：内部使用的临时状态标志
- `quickUseItem` 字段：内部使用的快速使用代理

### 扩展入口
- 子类必须覆写`canHold(Item)`方法定义物品过滤规则
- 子类可以覆写`capacity()`方法定义容量
- 子类可以覆写其他方法自定义行为

## 9. 运行机制与调用链

### 创建时机
- 玩家购买背包时
- 通过商店或特殊事件获得背包时

### 调用者
- 商店系统 - 创建和销售背包
- 物品收集系统 - 处理背包收集
- UI系统 - 处理背包打开和显示
- 快捷栏系统 - 处理背包中物品的快捷使用

### 被调用者
- WndQuickBag - 显示背包UI
- Dungeon.quickslot - 管理快捷栏
- Badges - 验证成就
- Bundle系统 - 序列化背包内容

### 系统流程位置
- 商店购买 → 创建Bag子类实例 → 添加到英雄背包 → 自动抓取符合条件物品 → 玩家打开查看/使用

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.bags.bag.name | 背包 | Bag基类的显示名称 |
| items.bags.bag.discover_hint | 你可在商店中购买该物品。 | Bag基类的发现提示 |

### 依赖的资源
- 精灵图资源：image = 11（默认背包图标）
- UI资源：WndQuickBag窗口
- 音效资源：继承自Item基类

### 中文翻译来源
来自 `core/src/main/assets/messages/items/items_zh.properties` 文件

## 11. 使用示例

### 基本用法
```java
// 创建自定义背包
public class CustomBag extends Bag {
    @Override
    public boolean canHold(Item item) {
        return item instanceof CustomItem;
    }
    
    @Override
    public int capacity() {
        return 15;
    }
}

// 使用背包
CustomBag bag = new CustomBag();
hero.belongings.backpack.collect(bag); // 自动抓取符合条件的物品
```

### 扩展示例
```java
// 带特殊效果的背包
public class SpecialBag extends Bag {
    @Override
    public boolean collect(Bag container) {
        if (super.collect(container)) {
            // 添加特殊效果
            applySpecialEffect();
            return true;
        }
        return false;
    }
    
    private void applySpecialEffect() {
        // 实现特殊效果逻辑
    }
}
```

## 12. 开发注意事项

### 状态依赖
- owner字段必须正确设置以确保功能正常
- items列表的状态影响所有操作
- loading字段是内部使用的临时状态，不应在外部修改

### 生命周期耦合
- 背包与所有者的生命周期紧密耦合
- 与快捷栏系统的耦合确保物品位置正确更新
- 与LostInventory状态的耦合确保在物品丢失状态下行为正确

### 常见陷阱
- 忘记覆写canHold方法会导致背包接受所有物品
- 直接修改items列表而不更新快捷栏会导致UI不一致
- 在迭代过程中修改items列表可能导致ConcurrentModificationException

## 13. 修改建议与扩展点

### 适合扩展的位置
- canHold方法：定义物品过滤规则
- capacity方法：定义背包容量
- collect/onDetach方法：添加特殊效果
- execute方法：添加额外动作

### 不建议修改的位置
- 不要直接修改items列表，应使用提供的API
- 不要修改loading字段，这是内部使用的
- 不要移除unique=true设置，这会影响游戏机制

### 重构建议
- 可以考虑将物品过滤逻辑提取到单独的策略类中
- 迭代器逻辑可以简化，但当前实现正确处理了嵌套结构
- LostInventory检查逻辑可以进一步抽象

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点