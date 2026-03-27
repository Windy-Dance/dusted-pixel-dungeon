# ChangeButton 类

## 概述

`ChangeButton` 类是 Shattered Pixel Dungeon 游戏中的更新日志按钮组件。尽管类名包含"Button"，但它实际上继承自 `Component` 类而非真正的按钮类，通过自定义点击事件处理来模拟按钮功能。

该组件专门用于在更新日志中显示可点击的项目，如物品、敌人或功能图标，并在点击时显示详细的变更信息。

## 继承关系

- `com.watabou.noosa.ui.Component`
  - `com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeButton`

## 字段

- **`icon: Image`**：显示的图标图像（可以是物品图标、自定义图像等）
- **`title: String`**：按钮标题（首字母大写格式）
- **`messages: String[]`**：详细变更信息的消息数组

## 构造函数

### 基础构造函数

- **`ChangeButton(Image icon, String title, String... messages)`**  
  创建变更按钮：
  - `icon`：要显示的图标图像
  - `title`：按钮标题文本（自动转换为首字母大写格式）
  - `messages`：可变参数，包含一个或多个详细变更信息
  
  **初始化流程**：
  1. 将图标添加到组件树中
  2. 标题文本通过 `Messages.titleCase()` 进行格式化
  3. 调用布局方法进行初始定位

### 物品专用构造函数

- **`ChangeButton(Item item, String message)`**  
  专门为物品创建变更按钮的便捷构造函数：
  - 自动创建物品精灵作为图标（`new ItemSprite(item)`）
  - 使用物品名称作为标题（`item.name()`）
  - 接受单个变更消息字符串

## 方法

### 核心方法

- **`onClick(): void`**  
  处理点击事件：
  - 调用 `ChangesScene.showChangeInfo()` 显示详细的变更信息
  - 传递图标、标题和消息数组作为参数
  - 这是该组件模拟按钮功能的核心方法

### 布局方法

- **`layout(): void`**  
  图标居中对齐：
  - 在组件区域内水平居中：`icon.x = x + (width - icon.width()) / 2f`
  - 在组件区域内垂直居中：`icon.y = y + (height - icon.height()) / 2f`
  - 使用 `PixelScene.align()` 确保像素对齐

## 技术特点

1. **伪按钮设计**：虽然名为按钮，但实际是普通组件，通过自定义点击处理实现按钮功能
2. **多用途支持**：既支持通用图像，也提供物品专用的便捷构造函数
3. **文本本地化**：标题文本自动进行首字母大写处理，支持多语言环境
4. **灵活消息**：支持单条或多条变更信息，适应不同复杂度的变更描述
5. **集成场景**：与 `ChangesScene` 紧密集成，实现统一的变更信息展示

## 使用场景

1. **物品变更**：显示新物品或物品平衡性调整的详细信息
2. **敌人变更**：展示敌人属性或行为的修改内容
3. **功能变更**：介绍新功能或现有功能的改进
4. **系统变更**：说明游戏机制或UI系统的重大改动
5. **修复列表**：列出重要bug修复的详细信息

## 相关类

- **`Component`**：UI组件基类
- **`ItemSprite`**：物品精灵类，用于创建物品图标
- **`ChangesScene`**：变更日志场景类，负责显示详细信息
- **`Messages`**：消息本地化工具类
- **`PixelScene`**：场景工具类，提供像素对齐功能
- **`Item`**：物品基类

## 注意事项

- 尽管类名包含"Button"，但该类并不继承自任何按钮基类
- 实际的点击检测由父容器 `ChangeInfo` 的 `onClick()` 方法处理
- 图标尺寸会被自动适配到组件容器中，保持居中显示
- 标题文本会自动进行首字母大写处理，确保显示一致性
- 该组件依赖 `ChangesScene` 来显示详细信息，需要确保场景可用