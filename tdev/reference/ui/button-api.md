# Button 类 API 参考

## 类声明
```java
public class Button extends Component
```

## 类职责
Button 类是 Shattered Pixel Dungeon UI 系统中的基础按钮组件，提供完整的用户交互功能。它支持多种点击方式和输入设备，并包含以下核心特性：

- 处理鼠标/触摸指针的按下、释放和点击事件
- 支持键盘快捷键绑定和触发
- 实现长按检测功能（默认 0.5 秒）
- 提供悬停提示（Tooltip）系统
- 确保同一时间只有一个按钮处于按下状态

## 关键字段

### 静态字段
- `longClick` (float): 长按检测的时间阈值，默认为 0.5 秒

### 实例字段
- `hotArea` (PointerArea): 按钮的可点击区域
- `hoverTip` (Tooltip): 当前显示的悬停提示
- `pressedButton` (static Button): 当前被按下的按钮实例（确保单例性）
- `pressTime` (float): 按钮按下的持续时间
- `clickReady` (boolean): 标记是否准备好执行点击操作

## 可重写方法

### 事件处理方法
- `onPointerDown()`: 指针按下时调用（可用于视觉反馈）
- `onPointerUp()`: 指针释放时调用（可用于视觉反馈恢复）
- `onClick()`: 左键点击或默认按键触发时调用（主要操作）
- `onRightClick()`: 右键点击时调用
- `onMiddleClick()`: 中键点击时调用
- `onLongClick()`: 长按时调用，返回 true 表示消耗了长按事件

### 属性和行为定制
- `keyAction()`: 返回与按钮关联的主键盘动作（GameAction）
- `secondaryTooltipAction()`: 返回备用的键盘动作（用于悬停提示显示）
- `hoverText()`: 返回悬停时显示的文本内容

## 公开方法

### 生命周期方法
- `createChildren()`: 创建按钮的子组件（包括 hotArea 和键盘监听器）
- `update()`: 更新按钮状态（检测长按、管理悬停提示）
- `destroy()`: 销毁按钮并清理资源（移除键盘监听器、销毁提示）

### 工具方法
- `killTooltip()`: 立即销毁当前的悬停提示
- `givePointerPriority()`: 给按钮的 hotArea 更高的指针优先级
- `layout()`: 布局 hotArea 的位置和尺寸

## 使用示例

### 基本按钮创建
```java
Button basicButton = new Button() {
    @Override
    protected void onClick() {
        // 处理点击事件
        System.out.println("按钮被点击了！");
    }
    
    @Override
    protected String hoverText() {
        return "这是一个基本按钮";
    }
};
basicButton.setSize(100, 30);
parent.add(basicButton);
```

### 键盘快捷键绑定
```java
Button shortcutButton = new Button() {
    @Override
    public GameAction keyAction() {
        return SPDAction.INVENTORY; // 绑定到物品栏快捷键
    }
    
    @Override
    protected void onClick() {
        // 打开物品栏
        GameScene.show(new WndBag());
    }
    
    @Override
    protected String hoverText() {
        return "打开物品栏";
    }
};
```

### 长按功能实现
```java
Button longPressButton = new Button() {
    @Override
    protected void onClick() {
        // 普通点击：使用物品
        useItem();
    }
    
    @Override
    protected boolean onLongClick() {
        // 长按：查看物品详细信息
        showItemDetails();
        return true; // 消耗长按事件
    }
};
```

### 右键菜单
```java
Button contextMenuButton = new Button() {
    @Override
    protected void onRightClick() {
        // 显示右键菜单
        new WndContextMenu().show();
    }
    
    @Override
    protected void onClick() {
        // 左键：主要操作
        performMainAction();
    }
};
```

## 相关子类
Button 类作为基础按钮组件，被多个具体的 UI 按钮类继承：
- `IconButton`: 带图标的按钮
- `RedButton`: 红色主题按钮（通常用于危险操作）
- `GreyButton`: 灰色主题按钮
- `ItemSlot`: 物品槽按钮（继承自 Button）
- `TalentSlot`: 天赋槽按钮

这些子类通常会重写视觉相关的属性，并可能提供特定的点击行为。

## 常见错误

### 1. 忘记设置尺寸
Button 默认尺寸为 0x0，必须显式设置大小才能正常显示和交互：
```java
// 错误：未设置尺寸
Button button = new Button();

// 正确：设置合适的尺寸
button.setSize(80, 25);
```

### 2. 重复添加监听器
Button 在 `createChildren()` 中自动添加了键盘监听器，不要手动重复添加，否则可能导致事件被多次触发。

### 3. 悬停提示内存泄漏
如果在按钮销毁前没有正确清理悬停提示，可能导致内存泄漏。Button 的 `destroy()` 方法会自动调用 `killTooltip()`，但如果有自定义逻辑需要注意。

### 4. 长按事件冲突
长按事件会自动取消普通点击事件（通过设置 `clickReady = false`），确保不会同时触发两种操作。

### 5. 键盘动作返回 null
`keyAction()` 方法默认返回 null，如果需要键盘支持必须重写此方法返回有效的 GameAction 实例。

### 6. UI 线程问题
所有 Button 的操作都应该在主线程中进行，避免在异步回调中直接修改按钮状态。