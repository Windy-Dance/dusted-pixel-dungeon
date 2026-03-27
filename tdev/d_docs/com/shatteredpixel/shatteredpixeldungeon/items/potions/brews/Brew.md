# Brew 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\items\potions\brews\Brew.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.potions.brews |
| **文件类型** | abstract class |
| **继承关系** | extends Potion |
| **代码行数** | 67 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Brew 类是所有魔药（Brew）物品的抽象基类，为具体的魔药实现提供统一的行为框架。它定义了魔药作为药剂子类的基本特性，包括禁止饮用、强制投掷、自动识别等。

### 系统定位
在物品系统中，Brew 位于药剂（Potion）类之下，作为特殊药剂类型的基类。所有具体的魔药（如水爆魔药、冰暴魔药等）都继承自此类。

### 不负责什么
- 不负责具体的魔药效果实现（由子类实现 shatter 方法）
- 不负责魔药的配方定义（由子类中的 Recipe 内部类实现）
- 不处理具体的视觉或音效表现

## 3. 结构总览

### 主要成员概览
- 继承自 Potion 的所有字段和方法
- 重写的 actions() 方法
- 重写的 defaultAction() 方法
- 重写的 doThrow() 方法
- 抽象的 shatter() 方法
- 重写的 isKnown() 方法
- 重写的 value() 方法
- 重写的 energyVal() 方法

### 主要逻辑块概览
- 行为定制：移除饮用行为，设置默认动作为投掷
- 投掷机制：使用 GameScene.selectCell 进行目标选择
- 识别状态：所有魔药默认为已识别状态
- 价值计算：固定的价值和能量值计算

### 生命周期/调用时机
- 对象创建时：初始化继承的 Potion 字段
- 用户交互时：actions() 和 defaultAction() 被调用来确定可用操作
- 投掷时：doThrow() 被调用，触发 shatter() 效果
- 经济系统查询时：value() 和 energyVal() 被调用

## 4. 继承与协作关系

### 父类提供的能力
从 Potion 类继承：
- 所有药剂相关的字段（image, curUser, talentChance 等）
- 药剂相关的方法（splash(), anonymize() 等）
- 物品基础功能（quantity(), price() 等）

### 覆写的方法
- actions(Hero hero)：移除饮用行为
- defaultAction()：返回 AC_THROW
- doThrow(Hero hero)：使用单元格选择器
- shatter(int cell)：声明为抽象方法
- isKnown()：始终返回 true
- value()：返回 quantity * 60
- energyVal()：返回 quantity * 12

### 实现的接口契约
无直接实现的接口，通过 Potion 间接实现 Item 接口。

### 依赖的关键类
- com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
- com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion
- com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene

### 使用者
- 所有具体的魔药子类（AquaBrew, BlizzardBrew, CausticBrew, InfernalBrew, ShockingBrew, UnstableBrew）
- 游戏场景系统（GameScene）用于投掷交互
- 物品管理系统用于价值和能量计算

## 5. 字段/常量详解

### 静态常量
无静态常量定义。

### 实例字段
继承自 Potion 的所有字段，Brew 类本身未定义新的实例字段。

## 6. 构造与初始化机制

### 构造器
使用默认构造器，无自定义构造器。

### 初始化块
无初始化块。

### 初始化注意事项
- 由于是抽象类，不能直接实例化
- 子类必须实现 shatter(int cell) 抽象方法

## 7. 方法详解

### actions(Hero hero)

**可见性**：public

**是否覆写**：是，覆写自 Potion

**方法职责**：返回英雄对当前魔药可用的操作列表

**参数**：
- `hero` (Hero)：执行操作的英雄

**返回值**：ArrayList<String>，包含可用的操作名称

**前置条件**：hero 参数不为 null

**副作用**：从父类返回的操作列表中移除 AC_DRINK 操作

**核心实现逻辑**：
```java
ArrayList<String> actions = super.actions(hero);
actions.remove(AC_DRINK);
return actions;
```

**边界情况**：如果父类没有返回 AC_DRINK，则不会有移除操作

### defaultAction()

**可见性**：public

**是否覆写**：是，覆写自 Potion

**方法职责**：返回魔药的默认操作

**参数**：无

**返回值**：String，始终返回 AC_THROW

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
直接返回 AC_THROW 常量

**边界情况**：无

### doThrow(Hero hero)

**可见性**：public

**是否覆写**：是，覆写自 Potion

**方法职责**：处理魔药的投掷操作

**参数**：
- `hero` (Hero)：执行投掷的英雄

**返回值**：void

**前置条件**：hero 参数不为 null

**副作用**：调用 GameScene.selectCell 触发目标选择界面

**核心实现逻辑**：
调用 GameScene.selectCell(thrower)，其中 thrower 是从 Potion 继承的内部类

**边界情况**：无

### shatter(int cell)

**可见性**：public

**是否覆写**：是，声明为抽象方法

**方法职责**：定义魔药破碎时的具体效果

**参数**：
- `cell` (int)：魔药破碎的格子位置

**返回值**：void

**前置条件**：cell 参数应在有效范围内

**副作用**：根据具体实现产生各种游戏效果

**核心实现逻辑**：
抽象方法，由子类实现

**边界情况**：由子类具体处理

### isKnown()

**可见性**：public

**是否覆写**：是，覆写自 Potion

**方法职责**：检查魔药是否已被识别

**参数**：无

**返回值**：boolean，始终返回 true

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
直接返回 true，表示所有魔药默认都是已知的

**边界情况**：无

### value()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：计算魔药的金币价值

**参数**：无

**返回值**：int，返回 quantity * 60

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回数量乘以 60 的结果

**边界情况**：数量为 0 时返回 0

### energyVal()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：计算魔药的炼金能量值

**参数**：无

**返回值**：int，返回 quantity * 12

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回数量乘以 12 的结果

**边界情况**：数量为 0 时返回 0

## 8. 对外暴露能力

### 显式 API
- 所有公共方法都是对外暴露的 API
- 抽象的 shatter 方法要求子类实现具体效果

### 内部辅助方法
- 无内部辅助方法，所有方法都有明确的外部用途

### 扩展入口
- shatter(int cell) 方法是主要的扩展点，子类必须实现此方法来定义具体的魔药效果

## 9. 运行机制与调用链

### 创建时机
- 通过炼金系统（Recipe）创建具体的魔药实例
- 无法直接创建 Brew 实例（抽象类）

### 调用者
- 游戏 UI 系统调用 actions() 和 defaultAction()
- 投掷系统调用 doThrow()
- 具体效果系统调用 shatter()
- 商店和炼金系统调用 value() 和 energyVal()
- 物品识别系统调用 isKnown()

### 被调用者
- 调用 Potion 父类的方法（super.actions()）
- 调用 GameScene.selectCell() 用于目标选择

### 系统流程位置
- 在物品交互流程中，作为药剂的特殊子类参与
- 在炼金合成流程中，作为合成结果的基类

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.potions.brews.brew.discover_hint | 你可通过炼金合成该物品。 | 发现提示 |

### 依赖的资源
- 继承 Potion 的图像资源系统
- 具体子类会使用各自的 ItemSpriteSheet 图像

### 中文翻译来源
来自 core/src/main/assets/messages/items/items_zh.properties 文件

## 11. 使用示例

### 基本用法
```java
// 创建具体的魔药实例（不能直接创建 Brew）
BlizzardBrew blizzardBrew = new BlizzardBrew();

// 魔药默认不可饮用，只能投掷
ArrayList<String> actions = blizzardBrew.actions(hero);
// actions 不包含 AC_DRINK

// 魔药默认已识别
boolean known = blizzardBrew.isKnown(); // true

// 获取价值
int value = blizzardBrew.value();
int energy = blizzardBrew.energyVal();
```

### 扩展示例
```java
// 自定义魔药实现
public class CustomBrew extends Brew {
    {
        image = ItemSpriteSheet.CUSTOM_BREW;
    }
    
    @Override
    public void shatter(int cell) {
        // 自定义破碎效果
        splash(cell);
        // ... 自定义逻辑
    }
    
    public static class Recipe extends SimpleRecipe {
        {
            inputs = new Class[]{SomePotion.class};
            inQuantity = new int[]{1};
            cost = 8;
            output = CustomBrew.class;
            outQuantity = 1;
        }
    }
}
```

## 12. 开发注意事项

### 状态依赖
- 依赖 curUser 字段来确定投掷者位置
- 依赖 quantity 字段来计算价值和能量

### 生命周期耦合
- 与 Potion 的生命周期完全耦合
- 与 GameScene 的投掷选择系统耦合

### 常见陷阱
- 忘记在子类中实现 shatter 方法（编译错误）
- 试图直接实例化 Brew（编译错误，因为是抽象类）
- 误以为魔药可以饮用（actions 方法已移除饮用选项）

## 13. 修改建议与扩展点

### 适合扩展的位置
- shatter 方法：添加新的魔药效果
- 子类中的 Recipe 内部类：定义新的合成配方

### 不建议修改的位置
- actions 方法：移除饮用行为是魔药的核心特性
- isKnown 方法：魔药默认已知是设计意图
- value 和 energyVal 方法：固定的价值体系

### 重构建议
- 如果需要更多可配置的行为，可以考虑将某些硬编码值提取为受保护字段
- 如果魔药类型增多，可以考虑进一步的分类抽象

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点