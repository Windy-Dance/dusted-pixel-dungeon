# Gizmo API 参考

## 类声明
```java
public class Gizmo
```

## 类职责
Gizmo是Noosa引擎中最基础的可视化组件类，作为所有可视对象的基类。它提供了基本的存在性、生命周期状态管理以及与父容器（Group）和相机（Camera）的关联功能。所有的可视元素都继承自Gizmo或其子类Visual。

## 关键字段
| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| exists | boolean | public | true | 表示对象是否存在，用于标记是否应该被处理 |
| alive | boolean | public | true | 表示对象是否存活，影响更新逻辑 |
| active | boolean | public | true | 表示对象是否激活，影响交互和更新 |
| visible | boolean | public | true | 表示对象是否可见，影响渲染 |
| parent | Group | public | null | 父容器组，用于层次结构管理 |
| camera | Camera | public | null | 关联的相机，用于坐标转换 |

## 生命周期
- **exists**: 控制对象的整体存在状态，当为false时对象通常会被忽略
- **alive**: 控制对象的存活状态，影响是否执行更新逻辑
- **active**: 控制对象的激活状态，可以与父对象的active状态组合判断
- **visible**: 控制对象的可见性，可以与父对象的visible状态组合判断

这些状态相互独立但又可以组合使用，例如一个对象可能alive但不可见(visible=false)，或者存在(exists=true)但不激活(active=false)。

## 核心方法
- **destroy()**: 销毁对象，断开与父容器的连接（parent = null）
- **update()**: 更新对象状态，默认为空实现，子类可重写
- **draw()**: 绘制对象，默认为空实现，子类可重写  
- **kill()**: 杀死对象，将alive和exists都设为false
- **revive()**: 复活对象，将alive和exists都设为true
- **camera()**: 获取关联的相机，优先使用自身的camera，否则递归查找父容器的camera
- **isVisible()**: 检查对象是否可见，考虑自身和所有父容器的visible状态
- **isActive()**: 检查对象是否激活，考虑自身和所有父容器的active状态
- **killAndErase()**: 杀死对象并从父容器中移除
- **remove()**: 从父容器中移除对象

## 使用示例
```java
// 创建Gizmo对象
Gizmo gizmo = new Gizmo();

// 控制状态
gizmo.visible = false; // 隐藏对象
gizmo.active = false;  // 停用对象

// 生命周期管理
gizmo.kill();          // 杀死对象
gizmo.revive();        // 复活对象

// 移除对象
gizmo.remove();        // 从父容器移除
// 或者
gizmo.killAndErase();  // 杀死并从父容器移除
```

## 相关子类
- **Visual**: Gizmo的主要子类，添加了位置、尺寸、变换、颜色等视觉属性
- 所有具体的可视组件（如Image、Text、Button等）通常都继承自Visual