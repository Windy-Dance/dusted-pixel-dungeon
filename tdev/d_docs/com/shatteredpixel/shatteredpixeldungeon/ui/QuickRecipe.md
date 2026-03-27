# QuickRecipe 类

## 概述
`QuickRecipe` 是 Shattered Pixel Dungeon 中用于显示炼金配方的 UI 组件。它以简洁的格式展示配方的输入物品、输出物品和制作成本，通常在炼金指南或物品信息窗口中使用。

## 功能特性
- **配方显示**：清晰展示配方的输入、箭头和输出
- **成本指示**：显示制作所需的能量成本（如果有）
- **交互支持**：点击箭头可快速跳转到炼金界面并预填充材料
- **物品匿名化**：自动处理未识别物品的显示，避免剧透
- **批量生成**：支持按页面批量生成多个配方

## 构造函数

### 简单配方
```java
public QuickRecipe(Recipe.SimpleRecipe r)
```
- 自动从简单配方获取输入和输出

### 自定义配方  
```java  
public QuickRecipe(Recipe r, ArrayList<Item> inputs, Item output)
```
- 手动指定输入物品列表和输出物品
- 适用于复杂或动态配方

## 核心组件

### 输入输出管理
- **ingredients**: 原始输入物品列表（保存用于跳转）
- **inputs[]**: 输入物品槽位数组（ItemSlot）
- **output**: 输出物品槽位（ItemSlot）

### 箭头组件
- **arrow**: 自定义箭头按钮类
  - 显示制作成本（如果大于0）
  - 支持文本高亮显示
  - 处理点击事件跳转到炼金界面

## 内部方法

### 匿名化处理
- **anonymize(Item item)**: 
  - 对未识别的药水和卷轴进行匿名化处理
  - 防止在游戏中过早透露物品信息

### 布局管理
- **layout()**: 
  - 水平排列输入、箭头和输出组件
  - 根据输入数量调整间距
  - 确保整体布局紧凑美观

## 箭头内部类

### 构造函数
- `arrow()`: 创建基础箭头
- `arrow(Image icon)`: 创建带图标的箭头
- `arrow(Image icon, int count)`: 创建带成本计数的箭头

### 核心功能
- **onClick()**: 处理箭头点击事件
  - 关闭当前窗口
  - 跳转到炼金界面
  - 预填充指定的材料物品

### 视觉效果
- **图标高亮**: 可用配方显示黄色高亮，不可用显示黑色
- **成本文本**: 使用蓝色高亮显示能量成本
- **禁用状态**: 不可用的配方会降低透明度

## 批量配方生成

### 静态方法
- **getRecipes(int pageIdx)**: 按页面索引获取配方列表

### 页面分类
- **页面 0**: 种子→药水配方
- **页面 1**: 卷轴→符石配方  
- **页面 2**: 食物配方（炖肉、肉派、水果）
- **页面 3**: 药水→异域药水配方
- **页面 4**: 卷轴→异域卷轴配方
- **页面 5**: 炸弹强化配方
- **页面 6**: 液态金属和秘法树脂配方
- **页面 7**: 各种酿造配方（不稳定酿造、腐蚀酿造等）
- **页面 8**: 法术配方（不稳定法术、狂野能量等）

### 特殊处理
- 使用 `Reflection.newInstance()` 动态创建物品实例
- 支持 null 条目作为页面分隔符
- 保持未识别物品的匿名状态

## 使用示例
```java
// 创建简单的食物配方
QuickRecipe stewedMeat = new QuickRecipe(new StewedMeat.oneMeat());

// 创建自定义配方
ArrayList<Item> inputs = new ArrayList<>(Arrays.asList(new Bomb(), new Redstone()));
QuickRecipe enhancedBomb = new QuickRecipe(new Bomb.EnhanceBomb(), inputs, new Bomb.StrongBomb());

// 获取特定页面的所有配方
ArrayList<QuickRecipe> page3Recipes = QuickRecipe.getRecipes(3);
```

## 注意事项
- 配方组件通常嵌入到更大的 UI 容器中（如窗口或滚动面板）
- 点击箭头会自动关闭父窗口并跳转到炼金界面
- 未识别物品的匿名化确保游戏平衡性
- 成本为0的配方不显示成本文本
- 批量生成时注意处理反射创建的异常情况
- 配方页面索引从0开始，最大为8