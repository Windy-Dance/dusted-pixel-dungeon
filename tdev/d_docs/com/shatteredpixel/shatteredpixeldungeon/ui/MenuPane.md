# MenuPane 类

## 概述
`MenuPane` 是 Shattered Pixel Dungeon 中显示游戏菜单的基础 UI 组件。它整合了深度指示器、挑战计数器、日志按钮、菜单按钮和危险指示器等多个功能模块。

## 功能特性
- **深度显示**：显示当前地牢深度和楼层感觉（如水、草、陷阱等）
- **挑战计数**：显示激活的挑战数量
- **版本信息**：显示游戏版本号
- **功能按钮**：提供日志和主菜单的快速访问
- **物品拾取**：显示物品拾取动画
- **文档高亮**：支持特定文档页面的闪烁提示

## 核心组件

### 视觉元素
- **bg**: 背景图像（`Assets.Interfaces.MENU`）
- **version**: 版本文本（"v" + Game.version）
- **versionOverflowBG**: 版本溢出背景（用于长版本号）

### 深度指示器
- **depthIcon**: 深度图标（根据 `Dungeon.level.feeling` 自动选择）
- **depthText**: 深度数字文本
- **depthButton**: 深度按钮（点击显示楼层信息或打开日志）

### 挑战指示器
- **challengeIcon**: 挑战图标（`Icons.CHAL_COUNT`）
- **challengeText**: 挑战数量文本
- **challengeButton**: 挑战按钮（点击打开挑战窗口）

### 功能按钮
- **btnJournal**: 日志按钮（JournalButton 内部类）
- **btnMenu**: 主菜单按钮（MenuButton 内部类）

### 其他组件
- **danger**: 危险指示器（DangerIndicator）
- **pickedUp**: 物品拾取动画（Toolbar.PickedUpItem）

## 静态常量
- **WIDTH = 31**: 菜单面板的标准宽度

## 内部类

### JournalButton 类
专用于日志功能的按钮：

#### 核心功能
- **动态图标**：根据是否有未读文档显示不同图标
- **闪烁提示**：新文档页面会闪烁黄色高亮
- **键盘绑定**：绑定到 JOURNAL 快捷键
- **多状态显示**：
  - 日志图标（无未读文档）
  - 键盘快捷键图标（有未读文档）

#### 点击处理
- 显示对应的文档页面或日志窗口
- 处理闪烁文档的自动跳转
- 更新按键显示状态

### MenuButton 类  
专用于主菜单的按钮：

#### 核心功能  
- **图标显示**：固定的菜单图标
- **键盘绑定**：绑定到 BACK 快捷键
- **视觉反馈**：点击时亮度增加并播放音效

#### 点击处理
- 打开主游戏菜单窗口（WndGame）

## 核心方法

### 构造和布局
- **createChildren()**: 初始化所有子组件
- **layout()**: 
  - 根据平台类型调整版本号显示位置
  - 动态布局各个按钮和指示器
  - 处理右键菜单按钮的位置

### 功能方法
- **pickup(Item item, int cell)**: 触发物品拾取动画
- **flashForPage(Document doc, String page)**: 为特定文档页面设置闪烁提示
- **updateKeys()**: 更新日志按钮的按键显示

## 视觉设计

### 颜色方案
- **背景**: 灰色菜单背景 (`Assets.Interfaces.MENU`)
- **文本颜色**: 浅灰色 (`0xCACFC2`)
- **标题颜色**: 黄色 (`Window.TITLE_COLOR`)

### 尺寸细节
- **深度图标**: 7x7 像素区域
- **挑战图标**: 7x7 像素区域  
- **按钮尺寸**: 13x11 像素（内部）+4 像素边距
- **版本字体**: 0.5x 缩放的像素字体

### 响应式布局
- **桌面模式**: 版本号右对齐，保留 1 像素右边距
- **移动模式**: 版本号右对齐，保留 8 像素右边距（隐藏"内测"标记）

## 使用示例
```java
// 创建菜单面板
MenuPane menuPane = new MenuPane();

// 触发物品拾取动画
menuPane.pickup(healthPotion, heroPosition);

// 为炼金指南设置闪烁提示
menuPane.flashForPage(Document.ALCHEMY_GUIDE, "potion_brewing");

// 更新日志按钮的按键显示
menuPane.updateKeys();
```

## 注意事项
- 挑战计数器仅在有激活挑战时显示
- 深度按钮根据楼层感觉显示不同的信息窗口
- 日志按钮的闪烁效果使用正弦波控制透明度变化
- 版本号显示会自动处理溢出情况（长版本号）
- 危险指示器会自动扩展到整个屏幕宽度
- 所有交互都包含适当的音频和视觉反馈