# AquaBrew 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\items\potions\brews\AquaBrew.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.potions.brews |
| **文件类型** | class |
| **继承关系** | extends Brew |
| **代码行数** | 79 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
AquaBrew（水爆魔药）实现了一种特殊的魔药，当破碎时会在目标位置触发间歇泉陷阱（GeyserTrap）效果，产生高压水浪，对火属性敌人造成伤害，并能浸湿地形、扑灭火焰、击退角色。

### 系统定位
作为 Brew 抽象类的具体实现，AquaBrew 在游戏的炼金系统中提供了一种基于风暴云合剂的特殊投掷物品，扩展了药剂系统的功能。

### 不负责什么
- 不直接处理伤害计算（由 GeyserTrap 处理）
- 不管理火焰扑灭逻辑（由 GeyserTrap 处理）
- 不处理击退方向计算（由 GeyserTrap 处理）

## 3. 结构总览

### 主要成员概览
- 静态初始化块：设置图像和天赋几率
- shatter(int cell) 方法：触发间歇泉陷阱
- value() 和 energyVal() 方法：重写价值计算
- Recipe 内部类：定义合成配方

### 主要逻辑块概览
- 图像和天赋配置
- 间歇泉陷阱创建和激活
- 击退方向计算（基于投掷轨迹）
- 价值计算（考虑批量数量）

### 生命周期/调用时机
- 对象创建时：设置图像和天赋几率
- 投掷破碎时：shatter() 被调用，创建并激活间歇泉陷阱
- 经济系统查询时：value() 和 energyVal() 被调用

## 4. 继承与协作关系

### 父类提供的能力
从 Brew 继承：
- 禁止饮用行为
- 强制投掷为默认操作
- 自动识别状态
- 基础价值和能量计算

### 覆写的方法
- shatter(int cell)：实现具体的水爆效果
- value()：重写价值计算以考虑批量数量
- energyVal()：重写能量值计算以考虑批量数量

### 实现的接口契约
通过 Brew -> Potion -> Item 间接实现所有物品接口。

### 依赖的关键类
- com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfStormClouds
- com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap
- com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
- com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet

### 使用者
- 炼金系统通过 Recipe 创建 AquaBrew 实例
- 游戏战斗系统在投掷时调用 shatter() 方法
- 商店和炼金系统查询价值和能量值

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| OUT_QUANTITY | int | 8 | 合成输出的数量 |

### 实例字段
无实例字段定义，继承自父类的所有字段。

## 6. 构造与初始化机制

### 构造器
使用默认构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.BREW_AQUA;
    talentChance = 1/(float)Recipe.OUT_QUANTITY;
}
```
- 设置物品图像为 BREW_AQUA
- 设置天赋几率为 1/8（因为每次合成产出 8 个）

### 初始化注意事项
- 初始化块在构造器之前执行
- talentChance 计算依赖于 Recipe.OUT_QUANTITY 常量

## 7. 方法详解

### shatter(int cell)

**可见性**：public

**是否覆写**：是，覆写自 Brew

**方法职责**：在指定格子触发水爆魔药效果，创建并激活间歇泉陷阱

**参数**：
- `cell` (int)：魔药破碎的目标格子

**返回值**：void

**前置条件**：cell 参数应在有效地图范围内

**副作用**：
- 创建 GeyserTrap 实例
- 设置陷阱位置和来源
- 计算击退方向（如果投掷者存在且与目标不同）
- 激活陷阱，触发水浪效果

**核心实现逻辑**：
1. 创建 GeyserTrap 实例
2. 设置陷阱位置为 cell，来源为当前魔药
3. 如果存在投掷者且投掷者位置与目标不同：
   - 创建 Ballistica 弹道计算投掷轨迹
   - 如果弹道路径足够长，设置中心 knockback 方向
4. 调用 geyser.activate() 触发陷阱效果

**边界情况**：
- 当投掷者位置与目标相同时，不计算击退方向
- 当弹道路径不够长时，不设置 knockback 方向

### value()

**可见性**：public

**是否覆写**：是，覆写自 Brew

**方法职责**：计算水爆魔药的金币价值，考虑批量合成的影响

**参数**：无

**返回值**：int，返回基于批量数量调整后的价值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回 (int)(60 * (quantity/(float)Recipe.OUT_QUANTITY))
- 基础价值为 60，但根据批量数量进行调整
- 由于每次合成产出 8 个，单个价值为 60/8 = 7.5，向下取整为 7

**边界情况**：
- quantity 为 0 时返回 0
- 浮点运算后的强制类型转换可能导致精度损失

### energyVal()

**可见性**：public

**是否覆写**：是，覆写自 Brew

**方法职责**：计算水爆魔药的炼金能量值，考虑批量合成的影响

**参数**：无

**返回值**：int，返回基于批量数量调整后的能量值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回 (int)(12 * (quantity/(float)Recipe.OUT_QUANTITY))
- 基础能量值为 12，但根据批量数量进行调整
- 单个能量值为 12/8 = 1.5，向下取整为 1

**边界情况**：
- quantity 为 0 时返回 0
- 浮点运算后的强制类型转换可能导致精度损失

## 8. 对外暴露能力

### 显式 API
- 所有继承的公共方法
- shatter() 方法提供具体的水爆效果
- Recipe 内部类提供合成接口

### 内部辅助方法
- 无内部辅助方法，所有逻辑都在主方法中

### 扩展入口
- Recipe 内部类可以被炼金系统发现和使用
- shatter() 方法可以被游戏系统调用来触发效果

## 9. 运行机制与调用链

### 创建时机
- 通过炼金釜使用风暴云合剂合成（Recipe 类处理）
- 每次合成产出 8 个 AquaBrew

### 调用者
- 炼金系统调用 Recipe.brew() 创建实例
- 投掷系统调用 shatter() 触发效果
- 商店系统调用 value() 获取价格
- 炼金系统调用 energyVal() 获取能量值

### 被调用者
- 调用 GeyserTrap 构造器和 activate() 方法
- 调用 Ballistica 进行弹道计算
- 调用父类 Brew 的方法

### 系统流程位置
- 在炼金合成流程中作为输出物品
- 在战斗流程中作为投掷物品使用
- 在经济系统中作为可交易物品

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.potions.brews.aquabrew.name | 水爆魔药 | 物品名称 |
| items.potions.brews.aquabrew.desc | 当这瓶药剂破裂时，会在原地迸发高压水浪。水浪仅对火属性敌人造成有效伤害，但同时会让水流向附近的地形扩散，可浸湿陷阱使其失效、扑灭火焰，并击退水浪附近的角色。 | 物品描述 |

### 依赖的资源
- 图像资源：ItemSpriteSheet.BREW_AQUA
- 音效资源：由 GeyserTrap 处理
- 合成配方：需要 PotionOfStormClouds 作为输入

### 中文翻译来源
来自 core/src/main/assets/messages/items/items_zh.properties 文件，第 799-800 行

## 11. 使用示例

### 基本用法
```java
// 通过炼金合成创建水爆魔药
ArrayList<Item> ingredients = new ArrayList<>();
ingredients.add(new PotionOfStormClouds());
AquaBrew brewed = (AquaBrew) new AquaBrew.Recipe().brew(ingredients);

// 投掷水爆魔药
brewed.shatter(targetCell);

// 查询价值
int goldValue = brewed.value(); // 单个约为 7
int energyValue = brewed.energyVal(); // 单个约为 1
```

### 合成示例
```java
// 检查合成配方
AquaBrew.Recipe recipe = new AquaBrew.Recipe();
Class<?>[] inputs = recipe.inputs; // [PotionOfStormClouds.class]
int[] quantities = recipe.inQuantity; // [1]
int cost = recipe.cost; // 8
Class<?> output = recipe.output; // AquaBrew.class
int outQty = recipe.outQuantity; // 8
```

## 12. 开发注意事项

### 状态依赖
- 依赖 curUser 字段确定投掷者位置
- 依赖 quantity 字段进行价值计算

### 生命周期耦合
- 与 GeyserTrap 生命周期耦合（魔药作为陷阱的 source）
- 与 Ballistica 弹道系统耦合（用于计算击退方向）

### 常见陷阱
- 忘记考虑批量合成对价值计算的影响
- 直接修改 Recipe.OUT_QUANTITY 而不更新 talentChance 计算
- 误以为可以单独合成一个（实际每次合成 8 个）

## 13. 修改建议与扩展点

### 适合扩展的位置
- Recipe 类：可以修改合成成本或输入材料
- shatter() 方法：可以添加额外的效果或修改击退逻辑

### 不建议修改的位置
- OUT_QUANTITY 常量：影响价值计算和天赋几率
- 基础价值和能量值：破坏游戏经济平衡

### 重构建议
- 如果需要更精确的价值计算，可以避免浮点运算
- 可以考虑将击退方向计算提取为单独的方法以提高可读性

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点