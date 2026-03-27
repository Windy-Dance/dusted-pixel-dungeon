# ResumeIndicator 类

## 概述
`ResumeIndicator` 是 Shattered Pixel Dungeon 中用于显示恢复操作提示的标签组件。它继承自 `Tag` 类，专门用于在英雄有上一个操作可恢复时显示箭头提示。

## 功能特性
- **恢复提示**：当英雄有上一个动作可以恢复时显示
- **闪烁效果**：显示时自动触发闪烁动画以吸引注意
- **方向支持**：支持水平翻转以适应不同UI布局
- **英雄信息集成**：点击时打开英雄信息窗口
- **键盘绑定**：绑定到 TAG_RESUME 快捷键

## 核心方法

### 构造函数
- `ResumeIndicator()` - 创建恢复指示器
  - 使用灰色主题色 (`0xA3A695`)
  - 默认大小为 `SIZE x SIZE` (24x24 像素)
  - 初始隐藏状态

### 组件创建
- **createChildren()**: 
  - 添加箭头图标 (`Icons.ARROW`)
  - 图标根据翻转状态自动调整位置

### 布局管理
- **layout()**: 
  - 根据翻转状态调整箭头图标位置
  - 确保图标垂直居中对齐

### 交互处理
- **onClick()**: 
  - 调用 `Dungeon.hero.resume()` 执行恢复操作
  - 仅在英雄就绪时生效

### 文本提示
- **hoverText()**: 返回悬停提示文本（"恢复操作"）

### 状态更新
- **update()**: 
  - 根据 `Dungeon.hero.lastAction` 存在与否控制可见性
  - 首次显示时自动触发闪烁效果
  - 英雄死亡时隐藏

## 快捷键绑定
- **keyAction()**: 返回 `SPDAction.TAG_RESUME`
- 允许通过键盘快捷键触发恢复操作

## 视觉元素
- **icon**: 箭头图标 (`Icons.ARROW`)
- **颜色**: 灰色主题 (`0xA3A695`)
- **尺寸**: 标准标签尺寸 (24x24 像素)

## 使用示例
```java
// 创建恢复指示器
ResumeIndicator resumeTag = new ResumeIndicator();

// 添加到场景
GameScene.addToFront(resumeTag);

// 指示器会自动根据 hero.lastAction 状态显示/隐藏
```

## 注意事项
- 指示器仅在以下条件下显示：
  - 英雄存活 (`Dungeon.hero.isAlive()`)
  - 有上一个动作 (`Dungeon.hero.lastAction != null`)
- 闪烁效果使用 `Tag.flash()` 方法实现
- 箭头方向可通过 `flip(boolean)` 方法控制
- 点击事件和键盘事件都会触发相同的恢复逻辑
- 悬停提示文本通过 `WndKeyBindings` 本地化
- 指示器的可见性完全由 `update()` 方法自动管理