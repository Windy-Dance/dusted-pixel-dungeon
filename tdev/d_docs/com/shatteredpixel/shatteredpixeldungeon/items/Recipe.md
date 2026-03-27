# Recipe.java - 制作配方系统

## 概述
`Recipe` 类是Shattered Pixel Dungeon中制作系统的抽象基类，定义了物品制作的核心接口和逻辑。所有具体的制作配方都继承此类，并通过静态注册机制集成到游戏的制作系统中。

## 核心接口

### 抽象方法
- **testIngredients(ArrayList<Item> ingredients)**: 测试输入材料是否满足制作条件
- **cost(ArrayList<Item> ingredients)**: 返回制作所需能量水晶数量
- **brew(ArrayList<Item> ingredients)**: 执行实际制作过程，消耗材料并返回结果
- **sampleOutput(ArrayList<Item> ingredients)**: 预览制作结果（不消耗材料）

### SimpleRecipe内部类
为常见的一对一制作场景提供简化实现：
- **inputs**: 输入物品类型数组
- **inQuantity**: 对应输入数量数组  
- **output**: 输出物品类型
- **outQuantity**: 输出数量
- **getIngredients()**: 获取示例材料列表
- 自动处理材料验证、消耗和产出逻辑

## 制作分类系统

### 可变材料配方 (variableRecipes)
- 目前为空，预留用于复杂制作逻辑
- 材料数量和类型可能动态变化

### 单材料配方 (oneIngredientRecipes) - 23种
包含各种转化和增强配方：
- **卷轴转化**: ScrollToStone（卷轴转符石）
- **药水变异**: PotionToExotic（普通药水转特殊药水）
- **卷轴变异**: ScrollToExotic（普通卷轴转特殊卷轴）
- **资源制作**: ArcaneResin.Recipe, LiquidMetal.Recipe
- **魔法药剂**: 各种Brew和Elixir配方
- **法术卷轴**: 各种Spell配方
- **食物制作**: StewedMeat.oneMeat()
- **饰品系统**: TrinketCatalyst.Recipe, UpgradeTrinket()

### 双材料配方 (twoIngredientRecipes) - 13种
组合型制作配方：
- **水果料理**: Blandfruit.CookFruit()
- **炸弹增强**: Bomb.EnhanceBomb()
- **复合药剂**: UnstableBrew, CausticBrew等
- **高级法术**: Alchemize, CurseInfusion, WildEnergy等
- **食物合成**: StewedMeat.twoMeat()

### 三材料配方 (threeIngredientRecipes) - 3种
复杂制作配方：
- **种子炼金**: Potion.SeedToPotion()
- **高级食物**: StewedMeat.threeMeat(), MeatPie.Recipe()

## 核心功能

### 配方查找
- **findRecipes(ArrayList<Item> ingredients)**: 
  - 根据输入材料数量选择对应的配方类别
  - 测试所有相关配方的材料条件
  - 返回所有可行的配方列表

### 材料验证
- **usableInRecipe(Item item)**: 检查物品是否可用于制作
  - **可装备物品**: 必须已知诅咒状态、未被诅咒、且为可升级投掷武器
  - **魔杖**: 必须已知诅咒状态、未被诅咒
  - **其他物品**: 不能被诅咒（可未识别）

## 使用流程

### 制作界面交互
1. 玩家选择要制作的材料
2. 调用 `findRecipes()` 查找可行配方
3. 显示可用配方列表和预览结果
4. 玩家确认制作后调用 `brew()` 执行

### 材料消耗逻辑
- **精确消耗**: 只消耗配方所需的材料数量
- **堆叠处理**: 支持从堆叠物品中部分消耗
- **失败保护**: 材料验证失败时不会消耗任何物品

## 扩展机制

### 自定义配方
- 继承Recipe类实现四个抽象方法
- 复杂配方可直接继承Recipe
- 简单一对一配方可继承SimpleRecipe

### 静态注册
- 所有配方在类加载时自动注册到对应数组
- 按材料数量分类提高查找效率
- 支持运行时动态添加新配方

## 注意事项
1. 制作系统与能量水晶经济系统紧密集成
2. 材料的诅咒状态对制作可行性有严格限制
3. SimpleRecipe适用于大多数标准制作场景
4. 复杂配方需要手动实现完整的四个接口方法
5. 制作预览功能帮助玩家避免错误的材料组合
6. 系统设计支持未来扩展更多制作类别和配方类型