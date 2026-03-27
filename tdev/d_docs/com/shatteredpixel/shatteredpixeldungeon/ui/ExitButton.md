# ExitButton 类

## 概述
`ExitButton` 是 Shattered Pixel Dungeon 中的退出按钮UI组件，继承自 `IconButton`。它提供一个退出图标按钮，用于在游戏内返回主菜单或退出应用程序，根据当前场景自动调整行为。

## 功能特性
- **智能场景检测**：在标题场景时直接退出应用程序，在游戏场景时返回标题场景
- **自适应尺寸**：在横屏移动设备上使用更大的尺寸（40x20），其他情况使用标准尺寸（20x20）
- **键盘集成**：关联 BACK 键盘操作，支持键盘导航
- **本地化提示**：提供本地化的悬停文本提示（"返回"）

## 核心方法

### 构造函数
- `ExitButton()` - 创建退出按钮，使用 Icons.EXIT 图标

### 重写方法
- `onClick()` - 处理点击事件，根据当前场景执行相应操作
- `keyAction()` - 返回关联的键盘操作（GameAction.BACK）
- `hoverText()` - 返回悬停提示文本（本地化）

## 内部组件
- 无特殊内部组件，主要继承父类 IconButton 的功能

## 使用示例
```java
// 创建退出按钮
ExitButton exitButton = new ExitButton();

// 添加到界面
add(exitButton);

// 玩家点击后会根据当前场景执行退出或返回操作
```

## 注意事项
- 在标题场景（TitleScene）中点击会调用 Game.instance.finish() 完全退出应用
- 在游戏场景中点击会调用 ShatteredPixelDungeon.switchNoFade(TitleScene.class) 切换到标题场景
- 尺寸适配考虑了设备类型和屏幕方向，确保移动设备上有合适的触摸区域
- 使用 DeviceCompat.isDesktop() 检测是否为桌面平台
- 图标来自 Icons.EXIT 枚举值