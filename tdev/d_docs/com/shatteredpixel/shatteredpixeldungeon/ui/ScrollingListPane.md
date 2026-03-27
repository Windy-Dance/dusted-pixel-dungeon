# ScrollingListPane 类

## 概述
`ScrollingListPane` 是 Shattered Pixel Dungeon 中基于 `ScrollPane` 实现的垂直列表滚动面板。它专门用于显示带有图标和文本的列表项，常用于设置菜单、物品列表等场景。

## 功能特性
- **列表结构**：支持标题和列表项两种类型的行
- **图标支持**：每个列表项可包含图标和可选的图标文本
- **点击处理**：支持列表项点击事件
- **颜色定制**：支持文本和图标的高亮着色
- **继承滚动功能**：完整的 `ScrollPane` 滚动支持

## 内部类

### ListItem 类
表示单个列表项的组件：

#### 核心组件
- **icon**: 主要图标图像
- **iconLabel**: 图标上的文本标签（如数字、字母等）
- **label**: 主要文本标签
- **line**: 顶部分隔线

#### 方法
- **onClick(float x, float y)**: 处理点击事件，返回 true 表示消耗了点击
- **hardlight(int color)**: 为图标标签和主标签设置高亮颜色
- **hardlightIcon(int color)**: 仅为图标设置高亮颜色

### ListTitle 类  
表示列表标题的组件：

#### 核心组件
- **label**: 标题文本（使用 `Window.TITLE_COLOR` 高亮）
- **line**: 顶部分隔线

## 核心方法

### 构造函数
- `ScrollingListPane()` - 创建空的滚动列表面板

### 内容管理
- **addItem(Image icon, String iconText, String text)**: 添加带图标文本的列表项
- **addItem(Image icon, String text)**: 添加普通列表项
- **addItem(ListItem item)**: 直接添加自定义列表项
- **addTitle(String text)**: 添加标题行
- **clear()**: 清空所有内容

### 事件处理
- **onClick(float x, float y)**: 将点击事件传递给对应的列表项

### 布局管理
- **layout()**: 
  - 垂直排列所有项目
  - 每行高度固定为 18 像素
  - 自动计算总内容高度

## 视觉样式

### 尺寸常量
- **ITEM_HEIGHT = 18**: 每行的标准高度

### 颜色配置
- **标题颜色**: `Window.TITLE_COLOR (0xFFFF44)`
- **分隔线颜色**: `0xFF222222` (深灰色)
- **文本字体大小**: 主标签使用 7 号字，图标标签和标题使用 9 号字

### 布局细节
- **图标区域**: 左侧 16 像素宽的正方形区域
- **文本区域**: 剩余宽度用于主文本显示
- **图标文本**: 显示在图标中央，居中对齐

## 使用示例
```java
// 创建滚动列表面板
ScrollingListPane listPane = new ScrollingListPane();

// 添加标题
listPane.addTitle("游戏设置");

// 添加带图标的列表项
Image soundIcon = Icons.get(Icons.AUDIO);
listPane.addItem(soundIcon, "声音", "音量和音效设置");

// 添加普通列表项
Image graphicsIcon = Icons.get(Icons.DISPLAY);
listPane.addItem(graphicsIcon, "图形设置");

// 创建可点击的列表项
ListItem clickableItem = new ListItem(Icons.get(Icons.KEYBOARD), "控制") {
    @Override
    public boolean onClick(float x, float y) {
        // 处理点击逻辑
        return true;
    }
};
listPane.addItem(clickableItem);
```

## 注意事项
- 图标文本会自动居中显示在图标上
- 主文本支持多行显示，但会限制最大宽度
- 分隔线位于每行的顶部，创建视觉分组效果
- 所有文本都使用 `RenderedTextBlock` 进行渲染
- 列表项的点击检测基于精确的坐标范围判断
- 继承的滚动功能支持鼠标滚轮、拖拽和键盘操作