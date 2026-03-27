# Window 类 API 参考

## 类声明
```java
public class Window extends Group implements Signal.Listener<KeyEvent>
```

## 类职责
Window 类是 Shattered Pixel Dungeon UI 系统中的基础窗口组件，用于创建弹出式界面。它提供了以下核心功能：

- 创建带有阴影效果的窗口容器
- 处理点击窗口外部区域时的关闭逻辑
- 管理窗口的尺寸调整和位置偏移
- 处理键盘输入事件（返回键、等待键）
- 自动管理相机和渲染资源

## 关键字段

### 受保护字段
- `width` (int): 窗口内容区域的宽度
- `height` (int): 窗口内容区域的高度  
- `xOffset` (int): 窗口在 X 轴上的偏移量
- `yOffset` (int): 窗口在 Y 轴上的偏移量
- `blocker` (PointerArea): 用于检测窗口外部点击的指针区域
- `shadow` (ShadowBox): 窗口的阴影效果组件
- `chrome` (NinePatch): 窗口的边框装饰（九宫格贴图）

### 公共常量
- `WHITE` (0xFFFFFF): 白色常量
- `TITLE_COLOR` (0xFFFF44): 标题颜色常量
- `SHPX_COLOR` (0x33BB33): Shattered Pixel 颜色常量

## 可重写方法

### 必须考虑重写的生命周期方法
- `onBackPressed()`: 当用户按下返回键或点击窗口外部时调用，默认实现调用 `hide()`
- `offset(int xOffset, int yOffset)`: 当窗口偏移量改变时调用，包含滚动面板的窗口通常需要重写此方法来刷新滚动状态

### 键盘事件处理
- `onSignal(KeyEvent event)`: 处理键盘事件，检测 SPDAction.BACK 和 SPDAction.WAIT 动作

## 公开方法

### 构造方法
- `Window()`: 创建默认大小的窗口（使用 WINDOW 类型的 Chrome）
- `Window(int width, int height)`: 创建指定尺寸的窗口
- `Window(int width, int height, NinePatch chrome)`: 使用自定义 Chrome 创建窗口

### 核心方法
- `resize(int w, int h)`: 重新调整窗口大小
- `getOffset()`: 获取当前偏移量（返回 Point 对象）
- `offset(Point offset)`: 设置窗口偏移量
- `boundOffsetWithMargin(int margin)`: 确保窗口及其偏移量不会超出指定边距范围
- `hide()`: 隐藏并从父容器中移除窗口
- `destroy()`: 销毁窗口并清理资源（自动移除相机和键盘监听器）

## 使用示例

### 基本窗口创建
```java
// 创建一个 200x150 像素的窗口
Window myWindow = new Window(200, 150);
myWindow.add(new Text("Hello World"));
GameScene.scene().addToFront(myWindow);
```

### 自定义关闭行为
```java
Window customWindow = new Window(300, 200) {
    @Override
    public void onBackPressed() {
        // 显示确认对话框而不是直接关闭
        new ConfirmDialog("确定要关闭吗？", () -> hide()).show();
    }
};
```

### 窗口偏移控制
```java
Window scrollableWindow = new Window(250, 180);
// 将窗口向右偏移 50 像素
scrollableWindow.offset(50, 0);
// 确保偏移后的窗口不会超出屏幕边界
scrollableWindow.boundOffsetWithMargin(10);
```

## 相关子类
Window 类作为基础窗口组件，被多个具体的 UI 窗口类继承：
- `WndBag`: 物品背包窗口
- `WndHero`: 英雄信息窗口  
- `WndJournal`: 日志记录窗口
- `WndMessage`: 消息对话框
- `WndChallenges`: 挑战设置窗口

这些子类通常会重写构造方法来添加特定的 UI 组件，并可能重写 `onBackPressed()` 方法来实现特定的关闭逻辑。

## 常见错误

### 1. 忘记添加到场景
创建 Window 实例后必须将其添加到当前场景的 UI 层，否则不会显示：
```java
// 错误：只创建但未添加
Window window = new Window(200, 150);

// 正确：添加到场景
GameScene.scene().addToFront(window);
```

### 2. 内存泄漏问题
Window 实例在不再需要时应该调用 `hide()` 方法正确销毁，避免相机和事件监听器的内存泄漏。

### 3. 偏移量计算错误
修改 `xOffset` 和 `yOffset` 字段时应使用 `offset()` 方法而不是直接赋值，以确保相机位置和阴影正确更新。

### 4. 线程安全问题
Window 的所有操作都应该在主线程（游戏循环）中进行，避免在后台线程中直接操作 UI 组件。