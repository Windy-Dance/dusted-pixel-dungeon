# Gizmo Class Documentation

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | SPD-classes/src/main/java/com/watabou/noosa/Gizmo.java |
| **包名** | com.watabou.noosa |
| **文件类型** | class |
| **继承关系** | 无（根类） |
| **代码行数** | 101 |
| **所属模块** | SPD-classes |

## 2. 文件职责说明

### 核心职责
Gizmo 类是 noosa 渲染框架中所有对象的基类，提供基础的存在性、活性、激活状态和可见性管理，以及场景图层次结构的基本支持。

### 系统定位
作为整个 noosa 对象系统的根类，Gizmo 定义了所有可视化和非可视化组件的共同接口和状态管理机制。它是 Visual 类的父类，也是 Group 容器类的基础。

### 不负责什么
- 不处理具体的渲染逻辑（由 Visual 及其子类处理）
- 不管理变换矩阵或几何属性（由 Visual 处理）
- 不处理用户输入或游戏逻辑（由更高层系统处理）

## 3. 结构总览

### 主要成员概览
- **状态标志**: exists, alive, active, visible
- **层次结构**: parent 引用, camera 引用
- **相机缓存**: camera 字段用于优化相机查找

### 主要逻辑块概览
- 构造函数初始化默认状态
- 基础生命周期方法（destroy, update, draw, kill, revive）
- 相机管理和状态查询方法
- 对象管理方法（killAndErase, remove）

### 生命周期/调用时机
- **创建时**: 构造函数设置所有状态标志为 true
- **每帧更新**: update() 方法（通常被子类重写）
- **每帧渲染**: draw() 方法（通常被子类重写）
- **销毁时**: destroy() 方法清理引用，kill() 方法设置状态标志

## 4. 继承与协作关系

### 父类提供的能力
无父类（根类）

### 覆写的方法
无（作为基类，方法供子类覆写）

### 实现的接口契约
无直接接口实现

### 依赖的关键类
- **com.watabou.noosa.Camera**: 用于相机管理和坐标转换
- **com.watabou.noosa.Group**: 作为 parent 的具体类型

### 使用者
- **Visual**: 所有可视化元素的基础
- **Group**: 容器类，管理多个 Gizmo 子对象
- **Scene**: 场景根容器
- **所有具体的 noosa 组件**: 如 Image, BitmapText, Tilemap 等

## 5. 字段/常量详解

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| exists | boolean | true | 对象是否存在（可用于对象池复用）|
| alive | boolean | true | 对象是否存活（生命状态）|
| active | boolean | true | 对象是否激活（影响 update 调用）|
| visible | boolean | true | 对象是否可见（影响 draw 调用）|
| parent | Group | null | 父容器引用|
| camera | Camera | null | 关联的相机引用（缓存优化）|

## 6. 构造与初始化机制

### 构造器
```java
public Gizmo()
```
- 初始化所有状态标志为 true
- parent 和 camera 字段初始化为 null

### 初始化块
无静态或实例初始化块

### 初始化注意事项
- 所有对象创建时默认处于完全激活和可见状态
- 需要手动添加到 Group 中才能参与场景图层次结构
- 相机引用通过 camera() 方法懒加载获取

## 7. 方法详解

### destroy()
**可见性**：public  
**是否覆写**：否  
**方法职责**：清理对象资源，断开父子关系  
**参数**：无  
**返回值**：void  
**前置条件**：无  
**副作用**：设置 parent = null  
**核心实现逻辑**：仅断开父容器引用  
**边界情况**：parent 为 null 时无操作

### update()
**可见性**：public  
**是否覆写**：否  
**方法职责**：空的基础更新方法  
**参数**：无  
**返回值**：void  
**前置条件**：无  
**副作用**：无  
**核心实现逻辑**：空方法（供子类重写）  
**边界情况**：总是成功

### draw()
**可见性**：public  
**是否覆写**：否  
**方法职责**：空的基础渲染方法  
**参数**：无  
**返回值**：void  
**前置条件**：无  
**副作用**：无  
**核心实现逻辑**：空方法（供子类重写）  
**边界情况**：总是成功

### kill()
**可见性**：public  
**是否覆写**：否  
**方法职责**：杀死对象，设置不存在和不存活状态  
**参数**：无  
**返回值**：void  
**前置条件**：无  
**副作用**：修改 exists 和 alive 字段  
**核心实现逻辑**：alive = false, exists = false  
**边界情况**：已死亡对象再次调用无额外效果

### revive()
**可见性**：public  
**是否覆写**：否  
**方法职责**：复活对象，设置存在和存活状态  
**参数**：无  
**返回值**：void  
**前置条件**：无  
**副作用**：修改 exists 和 alive 字段  
**核心实现逻辑**：alive = true, exists = true  
**边界情况**：已存活对象再次调用无额外效果

### camera()
**可见性**：public  
**是否覆写**：否  
**方法职责**：获取关联的相机引用  
**参数**：无  
**返回值**：Camera，关联的相机或 null  
**前置条件**：无  
**副作用**：可能设置 camera 字段（缓存）  
**核心实现逻辑**：
1. 如果 camera 字段非 null，直接返回
2. 否则如果 parent 非 null，递归调用 parent.camera()
3. 否则返回 null
4. 设置 camera 字段缓存结果
**边界情况**：根对象（无 parent）返回 null

### isVisible()
**可见性**：public  
**是否覆写**：否  
**方法职责**：检查对象及其祖先是否都可见  
**参数**：无  
**返回值**：boolean，是否可见  
**前置条件**：无  
**副作用**：无  
**核心实现逻辑**：
1. 如果 parent 为 null，返回 visible
2. 否则返回 visible && parent.isVisible()
**边界情况**：形成递归链直到根对象

### isActive()
**可见性**：public  
**是否覆写**：否  
**方法职责**：检查对象及其祖先是否都激活  
**参数**：无  
**返回值**：boolean，是否激活  
**前置条件**：无  
**副作用**：无  
**核心实现逻辑**：
1. 如果 parent 为 null，返回 active
2. 否则返回 active && parent.isActive()
**边界情况**：形成递归链直到根对象

### killAndErase()
**可见性**：public  
**是否覆写**：否  
**方法职责**：杀死对象并从父容器中移除  
**参数**：无  
**返回值**：void  
**前置条件**：无  
**副作用**：调用 kill() 和 parent.erase()  
**核心实现逻辑**：
1. 调用 kill() 设置死亡状态
2. 如果 parent 非 null，调用 parent.erase(this)
**边界情况**：无 parent 时仅杀死对象

### remove()
**可见性**：public  
**是否覆写**：否  
**方法职责**：从父容器中移除对象（不改变状态）  
**参数**：无  
**返回值**：void  
**前置条件**：无  
**副作用**：调用 parent.remove()  
**核心实现逻辑**：如果 parent 非 null，调用 parent.remove(this)  
**边界情况**：无 parent 时无操作

## 8. 对外暴露能力

### 显式 API
- **状态管理**: exists, alive, active, visible 字段
- **生命周期**: destroy(), kill(), revive(), killAndErase(), remove()
- **状态查询**: isVisible(), isActive()
- **相机访问**: camera()

### 内部辅助方法
- **基础钩子**: update(), draw()（供子类重写）

### 扩展入口
- **update()**: 子类重写实现自定义更新逻辑
- **draw()**: 子类重写实现自定义渲染逻辑
- **destroy()**: 子类重写清理特定资源

## 9. 运行机制与调用链

### 创建时机
- 由所有 noosa 对象的构造函数间接创建
- 通常在游戏对象初始化或场景加载时创建

### 调用者
- **Group**: 在 update() 和 draw() 中检查 exists 和 active/visible 状态
- **Game**: 在主循环中触发整个场景图的 update/draw
- **开发者代码**: 直接调用生命周期和状态管理方法

### 被调用者
- **Group**: 通过 parent 引用调用容器方法
- **Camera**: 通过 camera() 方法获取相机信息

### 系统流程位置
1. **对象创建**: new Gizmo() → 初始化状态标志
2. **场景更新**: Group.update() → 检查 g.exists && g.active → g.update()
3. **场景渲染**: Group.draw() → 检查 g.exists && g.isVisible() → g.draw()
4. **对象销毁**: g.kill() 或 g.destroy() → 清理状态和引用

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无

### 依赖的资源
无直接资源依赖

### 中文翻译来源
无官方中文翻译关联

## 11. 使用示例

### 基本用法
```java
// 创建基础 Gizmo 对象
Gizmo gizmo = new Gizmo();
gizmo.active = false; // 禁用更新
gizmo.visible = false; // 隐藏渲染

// 添加到组中
Group group = new Group();
group.add(gizmo);

// 生命周期管理
gizmo.kill(); // 杀死对象（但仍可复活）
gizmo.revive(); // 复活对象

// 从父容器移除
gizmo.remove(); // 仅移除，不改变状态
gizmo.killAndErase(); // 杀死并移除
```

### 扩展示例
```java
// 自定义 Gizmo 子类
public class CustomGizmo extends Gizmo {
    private int customData;
    
    public CustomGizmo(int data) {
        this.customData = data;
    }
    
    @Override
    public void update() {
        // 自定义更新逻辑
        if (active) {
            customData++;
        }
    }
    
    @Override
    public void draw() {
        // 自定义渲染逻辑（如果是可视化的）
    }
    
    @Override
    public void destroy() {
        // 清理自定义资源
        super.destroy();
    }
}
```

## 12. 开发注意事项

### 状态依赖
- **exists vs alive**: exists 表示对象是否存在于内存中（用于对象池），alive 表示逻辑生命状态
- **active vs visible**: active 影响 update() 调用，visible 影响 draw() 调用
- **层次状态传播**: isActive() 和 isVisible() 会递归检查所有祖先的状态

### 生命周期耦合
- **父子关系**: parent 引用影响相机查找、状态传播和对象管理
- **对象池模式**: exists 标志支持对象复用模式，配合 Group.recycle()
- **相机缓存**: camera 字段提供性能优化，但首次访问有递归开销

### 常见陷阱
- **状态混乱**: 混淆 exists/alive 或 active/visible 的用途
- **内存泄漏**: 忘记调用 remove() 或 destroy() 导致对象无法被垃圾回收
- **递归深度**: 深层次的场景图可能导致 camera() 或 isVisible() 调用栈溢出
- **并发问题**: 在多线程环境中修改状态标志可能导致竞态条件

## 13. 修改建议与扩展点

### 适合扩展的位置
- **具体行为实现**: 重写 update() 和 draw() 方法添加特定功能
- **资源管理**: 重写 destroy() 方法清理子类特有的资源
- **状态逻辑**: 根据业务需求扩展状态标志的含义和使用

### 不建议修改的位置
- **基础状态标志**: exists/alive/active/visible 的基本语义不应改变
- **层次结构逻辑**: parent/camera 的管理逻辑是框架核心，不应修改
- **递归查询**: isVisible()/isActive() 的递归实现是标准做法，修改可能破坏一致性

### 重构建议
- **状态枚举**: 可以考虑使用枚举代替布尔标志，提供更清晰的状态语义
- **事件系统**: 可以添加状态变化事件通知机制
- **性能优化**: 对于深场景图，可以考虑迭代而非递归实现状态查询

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（无相关翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点