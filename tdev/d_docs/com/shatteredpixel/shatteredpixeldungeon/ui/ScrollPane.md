# ScrollPane 类

## 概述
`ScrollPane` 是 Shattered Pixel Dungeon 中实现滚动功能的基础组件。它继承自 `com.watabou.noosa.ui.Component`，为包含大量内容的 UI 面板提供垂直滚动支持。

## 功能特性
- **垂直滚动**：支持通过鼠标拖拽、滚轮或键盘进行垂直滚动
- **滚动条指示器**：显示半透明的滚动条（thumb）作为视觉反馈
- **键盘支持**：绑定缩放键（ZOOM_IN/OUT）实现键盘滚动
- **相机管理**：为滚动内容创建独立的相机系统
- **点击处理**：支持在滚动内容上处理点击事件

## 核心组件

### 内部类
- **PointerController**: 继承自 `ScrollArea`，处理所有指针交互
  - 鼠标滚轮事件处理
  - 拖拽滚动支持
  - 点击事件传递

### 主要属性
- **content**: 包含实际内容的 Component 容器
- **thumb**: 滚动条指示器（ColorBlock），颜色为 `0xFF7b8073`
- **controller**: 指针控制器实例
- **keyListener**: 键盘事件监听器

## 核心方法

### 构造函数
- `ScrollPane(Component content)` - 创建滚动面板
  - 自动设置内容尺寸和相机
  - 注册键盘事件监听器

### 滚动控制
- **scrollTo(float x, float y)**: 滚动到指定坐标
  - 自动限制在内容边界内
  - 同步更新滚动条位置

### 布局管理
- **layout()**: 
  - 设置内容容器的位置和相机
  - 控制滚动条的可见性和位置
  - 计算滚动条高度：`height * height / content.height()`

### 事件处理
- **onClick(float x, float y)**: 可被子类重写以处理内容点击
- **update()**: 处理键盘滚动输入

### 资源管理
- **destroy()**: 清理相机和键盘监听器资源

## 物理参数

### 拖拽阈值
- **dragThreshold**: 拖拽灵敏度阈值（默认 `PixelScene.defaultZoom * 8`）
- 小于阈值的移动被视为点击而非拖拽

### 滚动条样式
- **THUMB_COLOR**: `0xFF7b8073` (灰色)
- **THUMB_ALPHA**: `0.5f` (半透明)
- 拖拽时透明度增加到 1.0

### 键盘滚动
- 绑定 `SPDAction.ZOOM_IN` 和 `SPDAction.ZOOM_OUT` 动作
- 滚动速度：每秒 150 像素 (`keyScroll * 150 * Game.elapsed`)

## 使用示例
```java
// 创建内容容器
Component content = new Component();
// 添加内容到容器...

// 创建滚动面板
ScrollPane scrollPane = new ScrollPane(content);

// 处理点击事件
ScrollPane customPane = new ScrollPane(content) {
    @Override
    public void onClick(float x, float y) {
        // 处理点击逻辑
    }
};

// 滚动到特定位置
scrollPane.scrollTo(0, 100);
```

## 注意事项
- 内容组件会自动添加到滚动面板的后面（`addToBack`）
- 相机会自动根据滚动面板的位置和尺寸进行调整
- 滚动条仅在内容高度大于面板高度时显示
- 键盘滚动支持同时按住多个方向键
- 拖拽滚动具有惯性效果，松开后会继续滚动一小段距离
- 所有坐标转换都通过相机系统正确处理