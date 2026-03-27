# ScrollingGridPane 类

## 概述
`ScrollingGridPane` 是 Shattered Pixel Dungeon 中基于 `ScrollPane` 实现的网格布局滚动面板。它专门用于显示图标网格，支持标题分组和多行布局，常用于物品展示、成就列表等场景。

## 功能特性
- **网格布局**：以固定大小的网格单元（17x17 像素）排列项目
- **标题分组**：支持带标题的项目分组，标题可居中或左对齐
- **多行支持**：自动换行布局，支持多个分组在同一行显示
- **点击处理**：支持网格项目的点击事件
- **背景着色**：支持为网格项目设置背景颜色
- **双图标支持**：每个网格项目可包含主图标和第二图标

## 内部类

### GridItem 类
表示单个网格项目的组件：

#### 核心组件
- **icon**: 主要图标图像（支持 ItemSprite 特殊处理）
- **secondIcon**: 可选的第二图标（通常用于装饰或状态指示）
- **bg**: 背景色块（默认半透明深灰色）

#### 方法
- **addSecondIcon(Visual icon)**: 添加第二图标
- **hardLightBG(float r, float g, float b)**: 设置背景高亮颜色
- **onClick(float x, float y)**: 处理点击事件，返回 true 表示消耗了点击

### GridHeader 类
表示网格标题的组件：

#### 构造函数
- `GridHeader(String text)`: 创建默认标题（7号字，左对齐）
- `GridHeader(String text, int size, boolean center)`: 自定义字体大小和对齐方式

#### 属性
- **center**: 控制标题是否居中对齐
- **text**: 渲染的文本块

## 核心方法

### 构造函数
- `ScrollingGridNet()` - 创建空的滚动网格面板

### 内容管理
- **addItem(GridItem item)**: 添加网格项目
- **addHeader(String text)**: 添加默认标题（7号字，左对齐）
- **addHeader(String text, int size, boolean center)**: 添加自定义标题
- **clear()**: 清空所有内容和分隔线

### 事件处理
- **onClick(float x, float y)**: 将点击事件传递给对应的网格项目

### 布局管理
- **layout()**: 
  - 智能多行布局算法
  - 支持多个小标题组在同一行显示
  - 自动插入分组分隔线
  - 限制最小分组宽度（3 个项目）

## 布局算法

### 多行分组逻辑
- **freshRow**: 跟踪当前分组是否在首行
- **lastWasSmallheader**: 跟踪上一个元素是否为小标题
- **widthThisGroup**: 跟踪当前分组的宽度

### 分隔线管理
- 在不同分组之间自动添加垂直分隔线
- 分隔线高度根据分组内容动态计算
- 最小分组宽度保证为 3 个项目（51 像素 + 间距）

### 尺寸常量
- **ITEM_SIZE = 17**: 网格项目的标准尺寸
- **MIN_GROUP_SIZE = 54**: 最小分组宽度（3 个项目 + 间距）
- **ITEM_SPACING = 1**: 项目之间的间距

## 使用示例
```java
// 创建滚动网格面板
ScrollingGridPane gridPane = new ScrollingGridPane();

// 添加居中标题
gridPane.addHeader("武器", 9, true);

// 添加网格项目
Image swordIcon = new ItemSprite(ItemSpriteSheet.SWORD);
GridItem weaponItem = new GridItem(swordIcon) {
    @Override
    public boolean onClick(float x, float y) {
        // 处理武器点击
        return true;
    }
};
gridPane.addItem(weaponItem);

// 添加带第二图标的项目
GridItem specialItem = new GridItem(specialIcon);
specialItem.addSecondIcon(Icons.get(Icons.CHECKED));
gridPane.addItem(specialItem);

// 添加左对齐标题
gridPane.addHeader("消耗品");
```

## 注意事项
- 网格项目会自动检测是否为 `ItemSprite` 并使用相应的构造函数
- 第二图标会显示在主图标的右上角位置
- 标题分组的最小宽度为 3 个项目，确保布局合理性
- 分隔线仅在需要时创建，避免不必要的视觉干扰
- 所有坐标计算都考虑了像素对齐（PixelScene.align）
- 点击检测基于精确的网格坐标范围判断
- 继承的滚动功能支持完整的交互体验