# TalentsPane 类

## 概述
`TalentsPane` 是 Shattered Pixel Dungeon 中用于显示和管理天赋系统的滚动面板组件。它组织并展示英雄的所有天赋，按等级分组显示，并提供天赋点数分配功能。

## 功能特性
- **分层显示**：按天赋等级（1-4级）分组显示天赋
- **滚动支持**：继承 `ScrollPane` 支持长内容滚动
- **模式切换**：支持信息查看模式和天赋升级模式
- **视觉反馈**：显示当前可用的天赋点数和已分配点数
- **随机分配**：提供随机分配剩余天赋点的功能

## 内部类

### TalentTierPane 类
表示单个天赋等级组的内部面板类：

#### 核心组件
- `title`: 等级标题文本（如"天赋等级 1"）
- `buttons`: 包含该等级所有天赋按钮的列表
- `stars`: 显示天赋点数的星形图标
- `random`: 随机分配按钮（仅在升级模式下显示）

#### 布局逻辑
- 标题居中显示，右侧显示星形点数指示器
- 天赋按钮水平均匀分布
- 星形按网格排列（每行最多 3 个）
- 随机按钮位于右上角

### 构造函数参数
- `talents`: 该等级的天赋映射（天赋 -> 已分配点数）
- `tier`: 天赋等级（1-4）
- `mode`: 显示模式（INFO 或 UPGRADE）

## 核心方法

### 构造函数
- `TalentsPane(TalentButton.Mode mode)` - 使用当前英雄的天赋数据
- `TalentsPane(TalentButton.Mode mode, ArrayList<LinkedHashMap<Talent, Integer>> talents)` - 使用指定的天赋数据

### 点数管理
- `setupStars()` - 重新设置星形图标状态：
  - 白色星形：已使用点数
  - 灰色星形：可用点数  
  - 黑色星形：未来可用点数
- 根据当前等级计算总点数、已用点数和可用点数

### 可用等级计算
根据游戏进度和英雄状态确定可显示的天赋等级：
- **信息模式**：基于徽章解锁状态
- **升级模式**：基于英雄等级和子职业状态
  - 等级 20+ 才能解锁第 2 级天赋
  - 拥有子职业才能解锁第 3 级天赋  
  - 拥有护甲能力才能解锁第 4 级天赋

## 视觉元素

### 颜色编码
- **标题颜色**: `Window.TITLE_COLOR (0xFFFF44)`
- **分隔线**: 黑色 (`0xFF000000`)
- **背景遮罩**: 深灰色 (`0xFF222222`)

### 特殊效果
- 随机按钮在特定条件下闪烁（满足随机胜利条件时）
- 升级时显示星星粒子效果

## 使用示例
```java
// 创建天赋信息面板
TalentsPane infoPane = new TalentsPane(TalentButton.Mode.INFO);

// 创建天赋升级面板
TalentsPane upgradePane = new TalentsPane(TalentButton.Mode.UPGRADE);

// 添加到场景
GameScene.addToFront(upgradePane);
```

## 注意事项
- 最多显示 4 个天赋等级（`Talent.MAX_TALENT_TIERS`）
- 每个等级必须有天赋才会创建对应的 `TalentTierPane`
- 随机分配功能通过对话框确认，防止误操作
- 界面会自动刷新以反映天赋点数的变化
- 使用 `Ratmogrify.useRatroicEnergy` 特殊标志处理老鼠形态能量