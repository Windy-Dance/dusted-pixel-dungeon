# RightClickMenu 类

## 概述
`RightClickMenu` 是 Shattered Pixel Dungeon 中实现右键上下文菜单的窗口组件。它为物品或操作提供快速访问菜单，支持图标、标题、分隔线和多个操作选项。

## 功能特性
- **上下文菜单**：显示与特定物品或操作相关的选项列表
- **图标支持**：可显示物品图标或其他图像
- **标题显示**：支持自定义标题文本
- **多选项支持**：可包含任意数量的操作选项
- **自动定位**：智能计算菜单位置以确保完全可见
- **鼠标跟踪**：当鼠标移出菜单区域时自动关闭

## 构造函数

### 物品菜单
```java
public RightClickMenu(Item item)
```
- 自动从物品获取可用操作
- 默认操作移至首位
- 显示物品名称作为标题
- 显示物品精灵作为图标

### 自定义菜单
```java
public RightClickMenu(Image icon, String title, String... options)
```
- 手动指定图标、标题和选项
- 适用于非物品相关的上下文菜单

## 核心组件

### 视觉元素
- **bg**: 背景九宫格（Chrome.Type.TOAST_TR_HEAVY）
- **icon**: 主要图标（通常为物品精灵）
- **titleText**: 标题文本（使用 TITLE_COLOR 高亮）
- **separator**: 顶部的黑色分隔线
- **blocker**: 阻塞层，用于检测鼠标移出
- **topRightButton**: 右上角的物品日志按钮（仅物品菜单）
- **buttons[]**: 红色按钮数组，每个对应一个操作选项

## 核心方法

### 事件处理
- **onSelect(int index)**: 可被重写以处理选项选择事件
  - `index`: 被选择的选项索引

### 布局管理
- **layout()**: 
  - 动态计算菜单宽度和高度
  - 智能定位确保菜单不超出屏幕边界
  - 自动调整各组件位置

### 内部逻辑
- **setup()**: 初始化所有组件和按钮
- **blocker 逻辑**: 当鼠标悬停在菜单外时自动销毁菜单

## 特殊功能

### 物品特定功能
- **物品日志集成**：右上角显示物品日志按钮（如果物品在背包中）
- **默认操作高亮**：默认操作使用 TITLE_COLOR 高亮显示
- **操作名称本地化**：使用 `item.actionName()` 获取本地化操作名称
- **目标功能支持**：如果是默认操作且使用目标瞄准，则启用 InventoryPane 目标模式

### 自动定位
- 检测菜单是否超出屏幕右边界，如超出则向左调整
- 检测菜单是否超出屏幕下边界，如超出则向上调整
- 确保菜单始终完全可见

## 使用示例
```java
// 创建物品右键菜单
Item myItem = new Sword();
RightClickMenu itemMenu = new RightClickMenu(myItem) {
    @Override
    public void onSelect(int index) {
        // 处理选择逻辑
        super.onSelect(index);
    }
};

// 创建自定义右键菜单  
Image customIcon = Icons.get(Icons.INFO);
String[] options = {"选项1", "选项2", "选项3"};
RightClickMenu customMenu = new RightClickMenu(customIcon, "自定义菜单", options);

// 添加到场景
GameScene.addToFront(itemMenu);
```

## 注意事项
- 菜单会自动绑定到当前场景的相机
- 鼠标跟踪使用 `Cursor.getCursorDelta()` 实现精确检测
- 所有按钮都继承自 `RedButton`，具有统一的视觉样式
- 文本大小为 6 号字，确保在小屏幕上可读
- 菜单销毁时会自动清理所有内部资源
- 物品菜单会根据物品状态动态调整可用选项