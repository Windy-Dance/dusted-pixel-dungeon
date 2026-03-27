# ItemJournalButton 类

## 概述
`ItemJournalButton` 是 Shattered Pixel Dungeon 中用于在物品窗口中添加日记笔记功能的按钮组件。它继承自 `IconButton`，提供一键创建或编辑物品自定义笔记的功能。

## 功能特性
- **笔记管理**：自动查找、创建和编辑物品的自定义笔记
- **类型识别**：支持普通物品、可装备物品、魔杖和饰品的不同处理
- **限制检查**：检查笔记数量限制并显示相应提示
- **窗口集成**：与物品使用窗口（WndUseItem）无缝集成
- **图标显示**：使用日志图标（Icons.JOURNAL）

## 核心构造函数
```java
public ItemJournalButton(Item item, Window parentWnd)
```
- **item**: 关联的物品对象
- **parentWnd**: 父级窗口（通常为 WndUseItem），用于关闭和重新打开

## 内部逻辑

### 笔记查找
- **可装备物品/魔杖/饰品**: 使用 `item.customNoteID` 查找笔记
- **普通物品**: 使用 `item.getClass()` 查找笔记  
- **笔记分配**: 找不到时自动分配唯一 ID

### 限制处理
- **数量限制**: 检查 `Notes.customRecordLimit()` 限制
- **提示显示**: 超出限制时显示警告窗口

### 窗口交互
- **新建笔记**: 显示文本输入窗口（WndTextInput）
- **编辑笔记**: 显示自定义笔记窗口（CustomNoteWindow）
- **窗口重开**: 创建笔记后自动重新打开父级物品窗口

## 核心方法

### 主要处理
- **customNote()**: 
  - 查找现有笔记
  - 处理限制情况
  - 创建新笔记或打开编辑窗口

### 辅助方法
- **addNote()**: 静态方法，处理笔记创建的完整流程
  - 显示文本输入窗口
  - 保存笔记内容
  - 重新打开父级窗口

### 交互处理
- **onClick()**: 触发自定义笔记流程

## 使用示例
```java
// 在物品窗口中创建日记按钮
Item item = new HealthPotion();
WndUseItem parentWindow = new WndUseItem(null, item);
ItemJournalButton journalBtn = new ItemJournalButton(item, parentWindow);

// 添加到窗口
parentWindow.add(journalBtn);
```

## 注意事项
- 按钮会自动处理不同物品类型的笔记 ID 分配
- 新建笔记时会自动填充默认标题（基于物品名称）
- 笔记内容保存后会自动刷新父级窗口
- 超出笔记限制时会显示友好的错误提示
- 与 CustomNoteButton 共享相同的文本输入逻辑
- 魔杖和饰品的笔记通过 customNoteID 进行持久化