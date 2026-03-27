# BuffIcon 类

## 概述

`BuffIcon` 类是 Shattered Pixel Dungeon 游戏中的状态效果图标渲染组件。它继承自 `Image` 类，专门用于显示各种状态效果（Buff）的图标，支持小尺寸（7x7像素）和大尺寸（16x16像素）两种显示模式。

该组件的主要功能是从预定义的纹理贴图中提取对应的状态效果图标，并支持通过 `Buff` 对象进行颜色着色（tinting），以区分正面和负面效果。

## 继承关系

- `com.watabou.noosa.Image`
  - `com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon`

## 静态字段

### 纹理贴图管理

- **`smallFilm: TextureFilm`** *(静态)*  
  小尺寸图标（7x7像素）的纹理贴图，延迟初始化以节省内存。

- **`largeFilm: TextureFilm`** *(静态)*  
  大尺寸图标（16x16像素）的纹理贴图，延迟初始化以节省内存。

### 尺寸常量

- **`SML_SIZE = 7`**：小图标尺寸（像素）
- **`LRG_SIZE = 16`**：大图标尺寸（像素）

## 实例字段

- **`large: boolean`** *(final)*  
  标记是否使用大尺寸图标，构造时确定且不可更改。

## 构造函数

### 基于 Buff 对象的构造函数

- **`BuffIcon(Buff buff, boolean large)`**  
  创建状态效果图标：
  - 根据 `large` 参数选择对应的纹理资源（`BUFFS_LARGE` 或 `BUFFS_SMALL`）
  - 调用 `refresh(buff)` 初始化图标显示
  - 自动应用 Buff 的颜色着色效果

### 基于图标ID的构造函数

- **`BuffIcon(int icon, boolean large)`**  
  直接通过图标ID创建状态效果图标：
  - 同样根据 `large` 参数选择纹理资源
  - 调用 `refresh(icon)` 显示指定的图标
  - 不应用颜色着色（适用于不需要区分正负面效果的场景）

## 方法

### 刷新方法

- **`refresh(Buff buff): void`**  
  使用 `Buff` 对象刷新图标显示：
  1. 调用 `refresh(buff.icon())` 设置基础图标
  2. 调用 `buff.tintIcon(this)` 应用颜色着色效果

- **`refresh(int icon): void`**  
  通过图标ID直接刷新显示：
  - 根据 `large` 字段选择对应的纹理贴图
  - 从纹理贴图中提取指定索引的图标帧
  - 设置为当前显示的图像

## 技术特点

1. **资源复用**：使用静态纹理贴图避免重复加载相同资源
2. **延迟初始化**：纹理贴图在首次使用时才创建，优化启动性能
3. **双尺寸支持**：灵活支持UI不同位置的显示需求
4. **颜色编码**：通过 `tintIcon()` 方法实现正面/负面效果的颜色区分
5. **轻量设计**：代码简洁，专注于核心的图标显示功能

## 使用场景

1. **BuffIndicator 组件**：作为状态指示器中的单个图标元素
2. **信息窗口**：在状态效果详情窗口中显示大尺寸图标
3. **敌人状态面板**：显示敌人的当前状态效果
4. **成就系统**：在成就界面中显示相关的状态效果图标

## 相关类

- **`Buff`**：状态效果基类，提供图标ID和颜色着色方法
- **`BuffIndicator`**：状态指示器组件，管理多个 BuffIcon 实例
- **`TextureFilm`**：纹理贴图管理类，用于从大纹理中提取子图像
- **`Assets.Interfaces`**：资源管理类，包含 BUFFS_LARGE 和 BUFFS_SMALL 纹理

## 注意事项

- 图标ID必须与 `BuffIndicator` 类中定义的常量保持一致
- 颜色着色效果依赖于 `Buff` 对象的 `tintIcon()` 实现
- 静态纹理贴图在整个应用生命周期内保持有效，无需手动清理