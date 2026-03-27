# ShockingBrew 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\items\potions\brews\ShockingBrew.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.potions.brews |
| **文件类型** | class |
| **继承关系** | extends Brew |
| **代码行数** | 69 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
ShockingBrew（雷鸣魔药）实现了一种特殊的魔药，当破碎时会向周围3格范围内的所有格子释放电击能量，创建 Electricity（电击）blob 效果，对范围内的单位造成麻痹效果并根据命中目标数量为使用者的法杖恢复充能。

### 系统定位
作为 Brew 抽象类的具体实现，ShockingBrew 在游戏的炼金系统中提供了一种基于麻痹药剂的特殊投掷物品，专注于范围控制和法杖充能支持。

### 不负责什么
- 不直接处理麻痹效果计算（由 Electricity blob 处理）
- 不管理法杖充能逻辑（由 Electricity blob 处理）
- 不处理视觉效果细节（由 splash 方法处理）

## 3. 结构总览

### 主要成员概览
- 静态初始化块：设置图像
- shatter(int cell) 方法：触发雷鸣效果
- Recipe 内部类：定义合成配方

### 主要逻辑块概览
- 图像配置
- 视觉和音效播放
- 距离地图构建（3格范围）
- 范围内格子的 Electricity blob 创建

### 生命周期/调用时机
- 对象创建时：设置图像
- 投掷破碎时：shatter() 被调用，创建电击效果
- 合成时：通过 Recipe 创建实例

## 4. 继承与协作关系

### 父类提供的能力
从 Brew 继承：
- 禁止饮用行为
- 强制投掷为默认操作
- 自动识别状态
- 基础价值和能量计算（使用父类默认值）

### 覆写的方法
- shatter(int cell)：实现具体的雷鸣效果

### 实现的接口契约
通过 Brew -> Potion -> Item 间接实现所有物品接口。

### 依赖的关键类
- com.shatteredpixel.shatteredpixeldungeon.Assets
- com.shatteredpixel.shatteredpixeldungeon.Dungeon
- com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
- com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity
- com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas
- com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
- com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
- com.watabou.utils.BArray
- com.watabou.noosa.audio.Sample
- com.watabou.utils.PathFinder

### 使用者
- 炼金系统通过 Recipe 创建 ShockingBrew 实例
- 游戏战斗系统在投掷时调用 shatter() 方法
- 游戏场景系统添加 Electricity blob 到场景中

## 5. 字段/常量详解

### 静态常量
无静态常量定义。

### 实例字段
无实例字段定义，继承自父类的所有字段。

## 6. 构造与初始化机制

### 构造器
使用默认构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.BREW_SHOCKING;
}
```
- 设置物品图像为 BREW_SHOCKING

### 初始化注意事项
- 无特殊初始化要求
- 使用父类的默认价值和能量值（value=60, energyVal=12）

## 7. 方法详解

### shatter(int cell)

**可见性**：public

**是否覆写**：是，覆写自 Brew

**方法职责**：在指定格子触发雷鸣魔药效果，在3格范围内创建 Electricity blob

**参数**：
- `cell` (int)：魔药破碎的目标格子

**返回值**：void

**前置条件**：cell 参数应在有效地图范围内

**副作用**：
- 播放破碎和闪电音效（如果在英雄视野内）
- 在3格范围内创建 Electricity blob

**核心实现逻辑**：
1. 调用 splash(cell) 显示破碎视觉效果
2. 如果目标格子在英雄视野内，播放 SHATTER 和 LIGHTNING 音效
3. 使用 PathFinder.buildDistanceMap 构建以 cell 为中心、半径为3的距离地图，可穿越非固体地形
4. 遍历所有格子，对于距离小于 Integer.MAX_VALUE 的格子：
   - 调用 GameScene.add(Blob.seed()) 创建体积为20的 Electricity blob

**边界情况**：
- 当范围内有大量固体地形阻挡时，实际影响范围会减小
- Electricity blob 的具体效果（麻痹、充能）由 blob 自身逻辑处理

## 8. 对外暴露能力

### 显式 API
- 所有继承的公共方法
- shatter() 方法提供具体的雷鸣效果
- Recipe 内部类提供合成接口

### 内部辅助方法
- 无内部辅助方法，所有逻辑都在 shatter() 方法中

### 扩展入口
- Recipe 内部类可以被炼金系统发现和使用
- shatter() 方法可以被游戏系统调用来触发效果

## 9. 运行机制与调用链

### 创建时机
- 通过炼金釜使用麻痹药剂合成（Recipe 类处理）
- 每次合成产出 1 个 ShockingBrew

### 调用者
- 炼金系统调用 Recipe.brew() 创建实例
- 投掷系统调用 shatter() 触发效果
- 游戏场景系统管理 Electricity blob 的生命周期

### 被调用者
- 调用 splash() 方法显示破碎效果
- 调用 Sample.INSTANCE.play() 播放音效
- 调用 PathFinder.buildDistanceMap() 构建距离地图
- 调用 GameScene.add() 添加 blob 到场景
- 调用 Blob.seed() 创建 Electricity blob 实例

### 系统流程位置
- 在炼金合成流程中作为输出物品
- 在战斗流程中作为范围控制投掷物品使用
- 在法杖充能支持系统中发挥作用

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.potions.brews.shockingbrew.name | 雷鸣魔药 | 物品名称 |
| items.potions.brews.shockingbrew.desc | 当瓶子破裂时，这瓶魔药会向周围释放一阵闪电风暴。 | 物品描述 |

### 依赖的资源
- 图像资源：ItemSpriteSheet.BREW_SHOCKING
- 音效资源：Assets.Sounds.SHATTER, Assets.Sounds.LIGHTNING
- 合成配方：需要 PotionOfParalyticGas 作为输入

### 中文翻译来源
来自 core/src/main/assets/messages/items/items_zh.properties 文件，第 811-812 行

## 11. 使用示例

### 基本用法
```java
// 通过炼金合成创建雷鸣魔药
ArrayList<Item> ingredients = new ArrayList<>();
ingredients.add(new PotionOfParalyticGas());
ShockingBrew brewed = (ShockingBrew) new ShockingBrew.Recipe().brew(ingredients);

// 投掷雷鸣魔药
brewed.shatter(targetCell);

// 查询价值（使用父类默认值）
int goldValue = brewed.value(); // 60
int energyValue = brewed.energyVal(); // 12
```

### 合成示例
```java
// 检查合成配方
ShockingBrew.Recipe recipe = new ShockingBrew.Recipe();
Class<?>[] inputs = recipe.inputs; // [PotionOfParalyticGas.class]
int[] quantities = recipe.inQuantity; // [1]
int cost = recipe.cost; // 10
Class<?> output = recipe.output; // ShockingBrew.class
int outQty = recipe.outQuantity; // 1
```

## 12. 开发注意事项

### 状态依赖
- 依赖 Dungeon.level.heroFOV 确定是否播放音效
- 依赖 Dungeon.level.solid 确定地形是否可穿越

### 生命周期耦合
- 与 GameScene 的 blob 管理系统耦合
- 与 Electricity blob 的生命周期耦合
- 与法杖充能系统的耦合

### 常见陷阱
- 忘记检查 heroFOV 导致音效在不可见区域播放
- 误以为 Electricity blob 会对固体地形后的单位生效（实际上受距离地图限制）
- 忽略体积值（20）相对于其他魔药可能较低，但 Electricity 效果本身较强

## 13. 修改建议与扩展点

### 适合扩展的位置
- Recipe 类：可以修改合成成本
- shatter() 方法：可以调整体积值、范围或添加额外效果
- 音效播放：可以添加更多音效变体

### 不建议修改的位置
- 距离范围：3格是经过平衡设计的数值
- 体积值：20 是针对 Electricity blob 特性的平衡值
- 合成成本：10 反映了其作为中等强度魔药的定位

### 重构建议
- 可以将体积值提取为常量以提高可维护性
- 可以考虑将距离范围作为配置参数

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点