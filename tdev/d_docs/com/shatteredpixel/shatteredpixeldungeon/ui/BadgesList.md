# BadgesList 类

## 概述

`BadgesList` 类是 Shattered Pixel Dungeon 游戏中的成就列表显示组件。它继承自 `ScrollPane` 类，用于在滚动面板中显示玩家已解锁的成就（Badges），并提供点击交互功能以查看成就详细信息。

该组件的主要特性包括：
- **滚动支持**：自动处理大量成就的滚动显示
- **成就过滤**：排除隐藏类型的成就，只显示可见成就
- **交互功能**：点击成就项可弹出详细信息窗口
- **视觉反馈**：点击时播放音效增强用户体验
- **灵活配置**：支持全局或本地成就列表的显示

## 继承关系

- `com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane`
  - `com.shatteredpixel.shatteredpixeldungeon.ui.BadgesList`

## 字段

- **`items: ArrayList<ListItem>`**：存储所有成就列表项的容器

## 构造函数

- **`BadgesList(boolean global)`**  
  创建成就列表组件：
  - `global` 参数决定是否显示全局成就（跨游戏存档）还是当前游戏的成就
  - 自动过滤掉 `HIDDEN` 类型的隐藏成就
  - 为每个可见成就创建对应的 `ListItem` 实例
  - 将所有列表项添加到滚动内容区域

## 方法

### 布局方法

- **`layout(): void`**  
  计算列表项的位置和滚动内容区域的大小：
  - 每个列表项高度固定为 `ListItem.HEIGHT = 18` 像素
  - 垂直堆叠排列所有列表项
  - 根据总项目数设置滚动内容区域的高度

### 交互方法

- **`onClick(float x, float y): void`**  
  处理点击事件：
  - 遍历所有列表项，检查点击位置是否在某个项目内
  - 如果找到匹配的项目，调用其 `onClick()` 方法并停止遍历

## 内部类：ListItem

`ListItem` 是继承自 `Component` 的内部类，代表单个成就列表项。

### 字段

- **`HEIGHT = 18f`** *(静态常量)*：列表项的固定高度
- **`badge: Badges.Badge`**：关联的成就对象
- **`icon: Image`**：成就图标图像
- **`label: RenderedTextBlock`**：成就标题文本

### 构造函数

- **`ListItem(Badges.Badge badge)`**  
  创建列表项实例：
  - 复制成就图标的图像数据（通过 `BadgeBanner.image()`）
  - 设置成就标题文本（通过 `badge.title()`）

### 生命周期方法

- **`createChildren(): void`**  
  创建子组件：图标图像和文本标签
  
- **`layout(): void`**  
  布局计算：
  - 图标垂直居中对齐
  - 文本标签位于图标右侧2像素处，同样垂直居中
  - 使用 `PixelScene.align()` 确保像素对齐

### 交互方法

- **`onClick(float x, float y): boolean`**  
  处理点击事件：
  - 播放点击音效（`Assets.Sounds.CLICK`）
  - 在场景前方添加 `WndBadge` 成就详情窗口
  - 返回 `true` 表示事件已被处理

## 技术特点

1. **内存优化**：使用对象池模式管理列表项，避免重复创建
2. **性能考虑**：预计算布局而非实时计算，提高滚动流畅度
3. **用户体验**：点击音效提供即时反馈，增强交互感
4. **可扩展性**：基于滚动面板实现，天然支持任意数量的成就
5. **数据分离**：UI逻辑与成就数据逻辑分离，便于维护

## 使用场景

1. **成就界面**：在游戏主菜单或专门的成就页面中显示所有已解锁成就
2. **新成就通知**：当玩家解锁新成就时，在通知窗口中显示相关成就
3. **统计面板**：在玩家统计信息中显示成就完成情况
4. **分享功能**：用于生成成就截图或分享内容

## 相关类

- **`Badges`**：成就系统核心类，管理所有成就的定义和状态
- **`BadgeBanner`**：成就横幅效果类，提供成就图标资源
- **`WndBadge`**：成就详情窗口类，显示成就的完整信息
- **`ScrollPane`**：滚动面板基类，提供滚动功能
- **`RenderedTextBlock`**：文本渲染组件，支持多行文本显示
- **`Assets.Sounds`**：音效资源管理

## 注意事项

- 列表只显示非隐藏类型的成就（`badge.type != Badges.BadgeType.HIDDEN`）
- 全局成就模式会显示跨存档的成就，本地模式只显示当前游戏的成就
- 列表项高度固定为18像素，适合移动端触摸操作
- 点击事件处理采用冒泡机制，确保只有一个项目响应点击