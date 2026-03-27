# DangerIndicator 类

## 概述
`DangerIndicator` 是 Shattered Pixel Dungeon 中的危险指示器UI组件，继承自 `Tag` 基类。它显示一个红色骷髅头图标和可见敌人数量，用于提醒玩家当前场景中存在的威胁程度，并提供快速切换目标的功能。

## 功能特性
- **敌人计数**：实时显示玩家视野内可见敌人的数量
- **视觉警告**：使用醒目的红色（0xC03838）背景和骷髅头图标表示危险
- **闪烁效果**：当敌人数量发生变化时触发闪烁动画以吸引注意
- **目标循环**：点击指示器可在可见敌人之间循环切换目标
- **自动隐藏**：当没有可见敌人时自动隐藏指示器
- **相机跟随**：选择目标后会短暂地让相机跟随该敌人移动

## 核心方法

### 构造函数
- `DangerIndicator()` - 创建危险指示器，初始状态为隐藏

### 重写方法
- `createChildren()` - 创建数字文本和骷髅头图标组件
- `layout()` - 布局数字和图标的位置
- `update()` - 监听可见敌人数量变化并更新显示
- `onClick()` - 处理点击事件，循环切换目标敌人
- `keyAction()` - 返回关联的键盘操作（SPDAction.CYCLE）
- `hoverText()` - 返回悬停提示文本（"危险"）

## 内部组件
- `number` - 显示敌人数量的 BitmapText 组件
- `icon` - 骷髅头图标（Icons.SKULL）
- `enemyIndex` - 当前选中的敌人索引（用于循环）
- `lastNumber` - 上次记录的敌人数量（用于检测变化）
- `COLOR` - 危险指示器颜色常量（0xC03838）
- `HEIGHT` - 指示器高度常量（16像素）

## 使用示例
```java
// 创建危险指示器
DangerIndicator dangerIndicator = new DangerIndicator();

// 添加到界面
add(dangerIndicator);

// 玩家可以通过点击或快捷键在敌人间切换目标
```

## 注意事项
- 只在英雄存活且有可见敌人时才显示
- 敌人数量通过 Dungeon.hero.visibleEnemies() 获取
- 目标切换使用 Dungeon.hero.visibleEnemy(index) 方法
- 切换目标时会调用 QuickSlotButton.target() 设置快捷槽目标
- 如果目标在攻击范围内，还会调用 AttackIndicator.target() 设置攻击指示器
- 相机跟随仅在玩家没有执行其他动作且目标精灵存在时触发