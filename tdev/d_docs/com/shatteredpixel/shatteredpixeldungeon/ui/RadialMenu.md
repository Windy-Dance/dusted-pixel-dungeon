# RadialMenu 类

## 概述
`RadialMenu` 是 Shattered Pixel Dungeon 中实现径向菜单的窗口组件。它提供圆形布局的选择菜单，支持多个选项、图标和描述文本，主要用于快捷操作选择。

## 功能特性
- **径向布局**：选项以圆形方式均匀分布
- **鼠标/控制器支持**：支持鼠标移动和控制器右摇杆控制
- **视觉反馈**：选中项高亮显示，其他项半透明
- **双击支持**：左键确认，右键或长按执行备用操作
- **自适应大小**：根据界面尺寸自动调整菜单大小

## 核心构造函数
```java
public RadialMenu(String title, String desc, String[] optionTexts, Image[] optionIcons)
```
- **title**: 菜单标题
- **desc**: 菜单描述文本  
- **optionTexts**: 选项文本数组
- **optionIcons**: 选项图标数组（与文本一一对应）

## 核心组件

### 视觉元素
- **selectionArc**: 选择弧形指示器（CircleArc）
- **titleTxt**: 标题文本（使用 TITLE_COLOR 高亮）
- **descTxt**: 描述文本
- **outerBG**: 外层背景图像
- **innerBG**: 内层背景图像

### 交互组件
- **selector**: 透明点击区域，处理确认操作
- **Cursor.captureCursor()**: 捕获鼠标光标以支持精确控制

## 核心方法

### 事件处理
- **onSelect(int idx, boolean alt)**: 处理选项选择
  - `idx`: 选中的选项索引
  - `alt`: 是否为备用操作（右键/长按）

### 控制逻辑
- **update()**: 
  - 处理鼠标移动和控制器输入
  - 计算选中角度并更新选择弧形
  - 同步更新标题文本和图标透明度

### 资源管理
- **destroy()**: 释放鼠标光标捕获

## 物理参数

### 尺寸配置
- **小尺寸模式**: 直径 140 像素（界面尺寸 0）
- **大尺寸模式**: 直径 200 像素（界面尺寸 1+）
- **图标距离**: 小尺寸 57 像素，大尺寸 80 像素

### 输入处理
- **鼠标隐藏**: 鼠标在 20 像素半径内隐藏以提供自然选择感
- **控制器死区**: 右摇杆 40% 死区避免误触发
- **角度计算**: 使用 `PointF.angle()` 计算选择角度

### 视觉效果
- **图标透明度**: 未选中 0.4f，选中 1.0f
- **选择弧形**: 半透明白色 (`0xFFFFFF`)，透明度 0.6f
- **标题闪烁**: 选中时显示对应的选项标题

## 使用示例
```java
// 创建径向菜单
String title = "快速槽位";
String desc = "LEFT: 使用\nRIGHT: 分配";
String[] options = {"药水", "卷轴", "食物"};
Image[] icons = {Icons.get(Icons.POTION), Icons.get(Icons.SCROLL), new ItemSprite(food)};

RadialMenu menu = new RadialMenu(title, desc, options, icons) {
    @Override
    public void onSelect(int idx, boolean alt) {
        if (alt) {
            // 执行分配操作
            assignQuickSlot(idx);
        } else {
            // 执行使用操作  
            useQuickSlot(idx);
        }
    }
};

// 显示菜单
GameScene.show(menu);
```

## 注意事项
- 径向菜单会自动捕获鼠标光标，确保精确控制
- 第一次鼠标输入会被忽略（用于隐藏鼠标）
- 支持同时使用鼠标和控制器输入
- 菜单大小根据 `SPDSettings.interfaceSize()` 自动调整
- 所有图标都会自动对齐到圆周上
- 标题文本会动态更新以反映当前选中项
- 选择弧形的角度计算考虑了选项数量的均匀分布