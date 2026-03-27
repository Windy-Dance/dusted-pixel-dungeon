# Toast 类

## 概述
`Toast` 是 Shattered Pixel Dungeon 中用于显示临时通知消息的组件。它提供了一个带有关闭按钮的简单弹窗，通常用于显示重要但非阻塞的信息。

## 功能特性
- **简洁设计**：使用 `Chrome.Type.TOAST_TR` 样式背景
- **关闭功能**：右上角包含关闭按钮（Icons.CLOSE）
- **自动布局**：文本和关闭按钮自动水平排列
- **可扩展性**：可通过重写 `onClose()` 方法自定义关闭逻辑

## 核心方法

### 构造函数
- `Toast(String text)` - 创建带有指定文本的吐司通知

### 文本管理
- `text(String txt)` - 更新显示的文本内容

### 关闭处理
- `onClose()` - 当用户点击关闭按钮时调用，可被子类重写以实现自定义逻辑

### 布局方法
- `createChildren()` - 创建背景、关闭按钮和文本组件
- `layout()` - 自动布局各个子组件

## 内部组件
- `bg`: 背景九宫格（Chrome.Type.TOAST_TR）
- `close`: 关闭按钮（IconButton 包装 Icons.CLOSE）
- `text`: 渲染的文本块（RenderedTextBlock），字体大小为 8

## 使用示例
```java
// 创建基本的吐司通知
Toast myToast = new Toast("这是一条重要消息！");

// 创建自定义关闭逻辑的吐司
Toast customToast = new Toast("点击关闭会执行特殊操作") {
    @Override
    protected void onClose() {
        // 执行自定义关闭逻辑
        super.onClose();
    }
};

// 更新文本内容
myToast.text("文本已更新！");
```

## 注意事项
- 关闭按钮绑定到 `GameAction.BACK` 键盘操作
- 文本和关闭按钮之间有 2 像素的水平间距
- 组件高度由文本高度和关闭按钮高度中的较大值决定
- 所有子组件都相对于吐司容器进行定位
- 吐司不自动销毁，需要手动调用 `hide()` 或通过其他机制移除