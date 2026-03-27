# CustomNoteButton 类

## 概述
`CustomNoteButton` 是 Shattered Pixel Dungeon 中的自定义笔记按钮UI组件，继承自 `IconButton`。它提供了一个加号（+）图标按钮，允许玩家在游戏中创建各种类型的自定义笔记，用于记录重要信息、标记特殊物品或添加个人备注。

## 功能特性
- **多种笔记类型**：支持纯文本笔记、楼层标记、物品关联笔记和物品类型笔记四种创建方式
- **智能限制**：当笔记数量达到上限时会显示提示信息，防止过度创建
- **物品选择集成**：与背包系统集成，可以直接为特定物品创建笔记
- **楼层选择**：提供已探索楼层的选择界面，方便标记特定深度的信息
- **物品类型分类**：按药水、卷轴、戒指等类别组织物品图标，便于选择
- **完整编辑功能**：创建的笔记支持后续编辑标题、内容和删除操作

## 核心方法

### 构造函数
- `CustomNoteButton()` - 创建带有加号图标的按钮，尺寸为11x11像素

### 重写方法
- `onClick()` - 处理按钮点击事件，显示笔记类型选择窗口
- `hoverText()` - 返回悬停提示文本（本地化）

### 内部窗口类
- `WndNoteTypeSelect` - 笔记类型选择窗口
- `WndDepthSelect` - 楼层选择窗口
- `WndItemtypeSelect` - 物品类型选择窗口
- `CustomNoteWindow` - 自定义笔记编辑窗口

## 内部组件
- `itemSelector` - 背包物品选择器，用于选择特定物品创建笔记
- `NOTE_SELECT_INSTANCE` - 静态实例引用，用于管理窗口状态
- `itemVisualcomparator` - 物品视觉排序比较器，按药水→卷轴→戒指的顺序排列

## 使用示例
```java
// 创建自定义笔记按钮
CustomNoteButton noteButton = new CustomNoteButton();

// 添加到界面
add(noteButton);

// 玩家点击后会触发完整的笔记创建流程
```

## 注意事项
- 笔记存储在 Notes.CustomRecord 系统中，具有持久化特性
- 物品关联笔记会自动分配唯一ID并与物品绑定
- 戒指类物品有特殊的重复检测逻辑，避免为同一类型创建多个笔记
- 界面使用本地化文本（Messages.get()），支持多语言
- 窗口层级管理确保正确的显示和隐藏行为
- 笔记数量受 Notes.customRecordLimit() 限制，默认上限为10个