# Component API 参考

## 类声明
```java
public class Component extends Group
```

## 类职责
Component是UI组件的基类，提供布局管理、位置设置、边界检查等UI功能。所有具体的UI组件（如按钮、窗口等）都继承自此类，通过重写`createChildren()`和`layout()`方法来实现特定的UI行为。

## 关键字段
- `x`: 组件左上角的X坐标
- `y`: 组件左上角的Y坐标  
- `width`: 组件的宽度
- `height`: 组件的高度

这些字段都是受保护的（protected），子类可以直接访问和修改。

## 布局方法

### setPos(float x, float y)
设置组件的位置，并自动触发布局更新。
- **参数**: x - X坐标, y - Y坐标
- **返回**: 当前组件实例（支持方法链式调用）

### setSize(float width, float height)
设置组件的尺寸，并自动触发布局更新。
- **参数**: width - 宽度, height - 高度
- **返回**: 当前组件实例（支持方法链式调用）

### setRect(float x, float y, float width, float height)
同时设置组件的位置和尺寸，并自动触发布局更新。
- **参数**: x - X坐标, y - Y坐标, width - 宽度, height - 高度
- **返回**: 当前组件实例（支持方法链式调用）

### fill(Component c)
将当前组件的矩形区域设置为与指定组件完全相同。
- **参数**: c - 源组件
- **效果**: 复制源组件的x、y、width、height值

### inside(float x, float y)
检查指定坐标点是否在组件的边界内。
- **参数**: x - 要检查的X坐标, y - 要检查的Y坐标
- **返回**: 如果点在组件内部返回true，否则返回false
- **边界规则**: 左边界和上边界包含在内，右边界和下边界不包含

## 位置访问方法

### left()
- **返回**: 组件的左边界X坐标（等同于x字段）

### right()
- **返回**: 组件的右边界X坐标（x + width）

### top()
- **返回**: 组件的上边界Y坐标（等同于y字段）

### bottom()
- **返回**: 组件的下边界Y坐标（y + height）

### centerX()
- **返回**: 组件中心点的X坐标（x + width / 2）

### centerY()
- **返回**: 组件中心点的Y坐标（y + height / 2）

### width()
- **返回**: 组件的宽度

### height()
- **返回**: 组件的高度

## 可重写方法

### createChildren()
在组件构造时调用，用于创建子组件。默认实现为空。
- **调用时机**: 组件构造函数中
- **用途**: 初始化子组件，添加到组件树中

### layout()
在组件位置或尺寸发生变化时调用，用于重新布局子组件。默认实现为空。
- **调用时机**: setPos()、setSize()、setRect()方法被调用后
- **用途**: 根据当前的位置和尺寸调整子组件的布局

## 使用示例
```java
// 创建自定义组件
public class MyButton extends Component {
    private Text text;
    
    @Override
    protected void createChildren() {
        text = new Text("Click me");
        add(text);
    }
    
    @Override
    protected void layout() {
        // 将文本居中显示
        text.setPos(centerX() - text.width() / 2, centerY() - text.height() / 2);
    }
}

// 使用组件
MyButton button = new MyButton();
button.setRect(10, 10, 100, 30); // 设置位置和尺寸
```

## 相关子类
- **Button**: 按钮组件
- **Window**: 窗口组件  
- **ScrollPane**: 滚动面板
- **CheckBox**: 复选框
- **Slider**: 滑块控件

这些子类都继承自Component，并实现了各自的`createChildren()`和`layout()`方法来提供特定的UI功能。