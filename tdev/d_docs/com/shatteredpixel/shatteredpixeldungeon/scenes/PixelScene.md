# PixelScene 类文档

## 概述
`PixelScene` 是 Shattered Pixel Dungeon 中所有场景的基类，继承自 `com.watabou.noosa.Scene`。它负责管理游戏中的像素级渲染、相机设置、字体处理、输入事件以及UI元素的基本功能。

## 类结构
```java
public class PixelScene extends Scene
```

## 主要功能

### 1. 显示和缩放管理
- **最小显示尺寸常量**：
  - `MIN_WIDTH_P` / `MIN_HEIGHT_P`: 移动设备竖屏模式的最小虚拟显示尺寸（135x225）
  - `MIN_WIDTH_L` / `MIN_HEIGHT_L`: 移动设备横屏模式的最小虚拟显示尺寸（240x160）
  - `MIN_WIDTH_FULL` / `MIN_HEIGHT_FULL`: 桌面完整UI模式的最小虚拟显示尺寸（360x200）

- **缩放控制**：
  - `defaultZoom`: 默认缩放级别
  - `maxDefaultZoom`: 最大默认缩放级别
  - `minZoom` / `maxZoom`: 缩放范围限制
  - `uiCamera`: UI专用相机实例

### 2. 字体和文本渲染
- `pixelFont`: 3x5位图像素字体，仅支持拉丁字符
- `renderTextBlock()`: 创建渲染文本块的工厂方法，根据当前缩放级别自动调整字体大小

### 3. 像素对齐系统
提供精确的像素对齐功能，确保UI元素在不同缩放级别下保持清晰：
- `align(float pos)`: 对齐单个坐标值
- `align(Visual v)`: 对齐视觉元素的位置
- `align(Component c)`: 对齐UI组件的位置

### 4. 窗口状态管理
- `saveWindows()`: 保存当前场景中的窗口状态
- `restoreWindows()`: 恢复之前保存的窗口状态
- 使用反射机制创建具有公共无参构造函数的窗口实例

### 5. 特效和动画
- `fadeIn()`: 场景淡入效果
- `showBadge()`: 显示徽章横幅
- `shake()`: 屏幕震动效果（受用户设置影响）

### 6. 安全区域处理
- `getCommonInsets()`: 获取设备安全区域边距（考虑刘海屏、圆角等），并根据当前缩放级别进行调整

### 7. 控制器支持
内置对游戏控制器的支持：
- Alt+Enter 切换全屏模式（桌面平台）
- 右摇杆控制虚拟鼠标指针
- 可配置的指针灵敏度

## 内部类

### Fader (淡入淡出器)
私有静态内部类，用于实现场景切换时的淡入淡出效果。

**特性**：
- 支持普通模式和光模式（light mode）的淡入淡出
- 单例模式，确保同一时间只有一个淡入淡出效果
- 自动管理生命周期，在动画完成后自动销毁

### PixelCamera (像素相机)
私有静态内部类，扩展了基础相机功能以支持像素完美的渲染。

**特性**：
- 自动对齐滚动和震动坐标到像素边界
- 确保在不同分辨率下保持一致的视觉效果
- 全屏模式支持

## 生命周期方法

### create()
场景创建时调用，执行以下初始化工作：
1. 清理纹理缓存（从游戏场景切换到菜单场景时）
2. 根据设备方向和界面设置计算合适的缩放级别
3. 初始化UI相机
4. 设置像素字体和渲染文本生成器
5. 配置亚洲语言的更大纹理页面（中文、日文、韩文）

### update()
每帧更新时调用，主要处理：
- 全屏切换快捷键监听（桌面平台）
- 控制器右摇杆到虚拟鼠标的转换
- 相机边缘滚动逻辑

### draw()
渲染场景内容，特别处理控制器虚拟鼠标的绘制。

### destroy()
场景销毁时清理资源：
- 移除所有指针事件监听器
- 移除全屏监听器
- 销毁光标图像

## 静态方法和属性

### 场景判断
- `landscape()`: 判断当前是否为横屏模式

### 特效控制
- `noFade`: 静态标志，用于跳过场景切换时的淡入效果

## 使用示例
所有具体的场景类（如 `GameScene`, `TitleScene` 等）都应该继承 `PixelScene` 并重写相应的方法来实现特定功能。

```java
public class MyCustomScene extends PixelScene {
    @Override
    public void create() {
        super.create();
        // 自定义初始化代码
    }
    
    @Override
    public void update() {
        super.update();
        // 自定义更新逻辑
    }
}
```

## 注意事项
1. **内存管理**：从游戏场景切换到菜单场景时会自动清理纹理缓存，有助于减少内存占用
2. **国际化支持**：自动检测亚洲语言并分配更大的字体纹理页面
3. **设备兼容性**：自动处理不同设备的安全区域和输入方式
4. **性能优化**：像素对齐系统确保在高DPI设备上保持最佳渲染性能

## 版本信息
- **原始作者**: Oleg Dolya (Pixel Dungeon)
- **Shattered版本**: Evan Debenham (2014-2026)
- **许可证**: GNU General Public License v3.0