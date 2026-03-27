# WndChanges 类

## 概述

`WndChanges` 类是 Shattered Pixel Dungeon 游戏中的简单更新日志窗口组件。它继承自 `WndTitledMessage` 类，用于显示单一版本或类别的游戏更新内容，并提供便捷的关闭交互功能。

该组件的主要特性包括：
- **简洁设计**：专注于单个更新内容的清晰展示
- **标题图标**：支持自定义图标和标题文本
- **背景点击关闭**：点击窗口外部区域即可关闭
- **继承复用**：基于现有的标题消息窗口实现，减少代码重复
- **轻量实现**：仅20多行代码，保持高度简洁性

## 继承关系

- `com.shatteredpixel.shatteredpixeldungeon.windows.Window`
  - `com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage`
    - `com.shatteredpixel.shatteredpixeldungeon.ui.changelist.WndChanges`

## 构造函数

- **`WndChanges(Image icon, String title, String message)`**  
  创建更新日志窗口：
  - `icon`：窗口标题左侧显示的图标
  - `title`：窗口标题文本
  - `message`：更新日志的详细内容文本
  
  **初始化流程**：
  1. 调用父类构造函数设置基本的标题和消息内容
  2. 创建覆盖整个UI区域的背景点击区域
  3. 设置背景区域的相机为UI相机
  4. 将背景区域添加到窗口中

## 技术特点

1. **极简实现**：核心功能完全依赖父类，仅添加背景点击关闭功能
2. **用户体验**：背景点击关闭提供直观的操作方式
3. **代码复用**：充分利用现有窗口组件的功能，避免重复开发
4. **内存效率**：轻量级实现，占用资源极少
5. **一致性**：保持与游戏其他窗口组件的视觉和交互一致性

## 使用场景

1. **单版本更新**：显示特定版本的完整更新日志
2. **重要公告**：展示重要的游戏变更或通知
3. **功能说明**：详细介绍单一新功能的使用方法
4. **补丁说明**：快速展示热修复的具体内容
5. **简化展示**：当不需要分页功能时的轻量级替代方案

## 相关类

- **`WndTitledMessage`**：带标题的消息窗口基类，提供核心的消息显示功能
- **`Window`**：窗口基类，提供基础的窗口管理功能
- **`PointerArea`**：指针事件处理区域，用于实现背景点击功能
- **`PixelScene`**：场景工具类，提供UI相机引用

## 注意事项

- 该类专为单一更新内容设计，不支持多标签页
- 背景点击区域覆盖整个UI相机区域（通常是整个屏幕）
- 所有文本格式化和布局计算由父类 `WndTitledMessage` 处理
- 图标和标题的样式遵循父类的默认设计
- 关闭操作调用 `onBackPressed()` 方法，遵循Android的返回键行为规范