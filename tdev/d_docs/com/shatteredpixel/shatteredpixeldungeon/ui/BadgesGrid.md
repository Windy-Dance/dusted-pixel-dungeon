# BadgesGrid 类

## 概述

`BadgesGrid` 类是 Shattered Pixel Dungeon 游戏中的成就网格显示组件。它继承自 `Component` 类，以网格布局的形式展示玩家的所有成就（包括已解锁和未解锁的成就），提供直观的视觉反馈和交互功能。

该组件的主要特性包括：
- **自适应网格布局**：根据容器尺寸和成就数量自动计算最佳行列数
- **成就状态区分**：已解锁成就正常显示，未解锁成就降低亮度
- **动态高亮效果**：已解锁成就会随机闪烁高亮，吸引玩家注意
- **交互功能**：点击任一成就可查看详细信息
- **全局/本地模式**：支持显示全局成就或仅当前游戏的成就

## 继承关系

- `com.watabou.noosa.ui.Component`
  - `com.shatteredpixel.shatteredpixeldungeon.ui.BadgesGrid`

## 字段

- **`badgeButtons: ArrayList<BadgeButton>`**：存储所有成就按钮的容器列表

## 构造函数

- **`BadgesGrid(boolean global)`**  
  创建成就网格组件：
  - `global` 参数控制显示范围：
    - `true`：显示所有成就（包括未解锁的）
    - `false`：仅显示当前游戏已解锁的成就
  - 自动过滤隐藏类型的成就（`HIDDEN`）
  - 对于全局模式，还会添加未解锁但满足前置条件的成就
  - 为每个成就创建对应的 `BadgeButton` 按钮

## 方法

### 布局方法

- **`layout(): void`**  
  实现自适应网格布局算法：
  1. 计算每个成就的理想占用面积：`badgeArea = sqrt(width * height / badgeCount)`
  2. 根据理想面积计算列数：`nCols = round(width / badgeArea)`
  3. 计算所需行数：`nRows = ceil(badgeCount / nCols)`
  4. 计算每个网格单元的尺寸：`badgeWidth = width/nCols`, `badgeHeight = height/nRows`
  5. 将每个按钮居中放置在对应的网格单元中

## 内部类：BadgeButton

`BadgeButton` 是继承自 `Button` 的内部类，代表单个成就按钮。

### 字段

- **`badge: Badges.Badge`**：关联的成就对象
- **`unlocked: boolean`**：标记成就是否已解锁
- **`icon: Image`**：成就图标图像

### 构造函数

- **`BadgeButton(Badges.Badge badge, boolean unlocked)`**  
  创建成就按钮：
  - 复制成就图标（通过 `BadgeBanner.image()`）
  - 如果未解锁，将图标亮度降低至40%（`brightness(0.4f)`）
  - 设置按钮尺寸与图标尺寸一致

### 生命周期方法

- **`layout(): void`**  
  将图标在按钮区域内居中对齐
  
- **`update(): void`**  
  实现动态高亮效果：
  - 仅对已解锁的成就生效
  - 每帧有小概率（`Game.elapsed * 0.1`）触发高亮
  - 调用 `BadgeBanner.highlight()` 显示短暂的高亮动画

### 交互方法

- **`onClick(): void`**  
  处理点击事件：
  - 播放点击音效（`Assets.Sounds.CLICK`）
  - 在场景前方添加 `WndBadge` 成就详情窗口
  - 传递解锁状态给详情窗口

- **`hoverText(): String`**  
  返回悬停提示文本，显示成就标题（`badge.title()`）

## 技术特点

1. **智能布局**：基于数学计算的自适应网格布局，确保最佳视觉效果
2. **状态可视化**：通过亮度差异清晰区分已解锁/未解锁状态
3. **动态效果**：随机高亮增强已解锁成就的视觉吸引力
4. **性能优化**：使用简单的随机概率控制高亮频率，避免性能开销
5. **用户体验**：居中对齐和像素对齐确保界面整洁美观

## 使用场景

1. **成就总览界面**：在玩家档案或专门的成就页面中显示所有成就
2. **进度追踪**：直观展示玩家的成就完成进度
3. **目标激励**：未解锁成就的显示激励玩家继续游戏
4. **收藏展示**：作为玩家游戏历程的视觉化展示

## 相关类

- **`Badges`**：成就系统核心类，提供成就过滤和状态查询
- **`BadgeBanner`**：成就横幅效果类，提供图标资源和高亮效果
- **`WndBadge`**：成就详情窗口类
- **`Button`**：基础按钮类，提供点击和悬停功能
- **`Assets.Sounds`**：音效资源管理
- **`PixelScene`**：场景工具类，提供像素对齐功能

## 注意事项

- 全局模式下只显示满足前置条件的未解锁成就（通过 `filterBadgesWithoutPrerequisites` 过滤）
- 隐藏类型的成就（`HIDDEN`）在任何模式下都不会显示
- 动态高亮效果仅对已解锁成就生效，未解锁成就保持静态显示
- 网格布局会根据容器尺寸自动调整，适合不同分辨率的设备
- 所有成就图标都来自 `BadgeBanner.image()` 方法，确保视觉一致性