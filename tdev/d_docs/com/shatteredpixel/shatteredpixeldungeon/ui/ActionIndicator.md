# ActionIndicator 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ui/ActionIndicator.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.ui |
| **类类型** | class |
| **继承关系** | extends Tag |
| **代码行数** | 205 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
ActionIndicator 是动作指示器组件，用于显示当前可用的特殊动作，并提供动态的视觉反馈和交互功能。

### 系统定位
位于 ui 包中，作为 Tag 的子类，是游戏界面中动作快捷入口的核心组件。

### 不负责什么
- 不负责动作的具体执行逻辑（由 Action 接口实现）
- 不负责动作的触发条件判断

## 3. 结构总览

### 主要成员概览
- 静态字段：action, instance
- 实例字段：primaryVis, secondVis
- 内部接口：Action

### 主要逻辑块概览
1. 动作显示：根据当前动作更新视觉元素
2. 交互处理：点击执行动作
3. 视觉更新：布局和透明度调整

### 生命周期/调用时机
由 GameScene 创建并管理。

## 4. 继承与协作关系

### 父类提供的能力
Tag 提供：
- 圆形按钮基础功能
- 点击交互支持
- 悬停提示

### 覆写的方法
| 方法 | 来源 |
|------|------|
| destroy() | Component |
| layout() | Component |
| update() | Component |
| onClick() | Tag |
| hoverText() | Tag |
| keyAction() | Tag |

### 实现的接口契约
无直接实现的接口。

### 依赖的关键类
- `Action`：动作接口
- `HeroIcon`：英雄图标
- `Visual`：视觉元素
- `Dungeon`：游戏数据

### 使用者
- `GameScene`：创建和管理
- 各种 Action 实现类

## 5. 字段/常量详解

### 静态字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| action | Action | 当前动作对象 |
| instance | ActionIndicator | 单例实例 |

### 实例字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| primaryVis | Visual | 主视觉元素 |
| secondVis | Visual | 次级视觉元素 |

## 6. 构造与初始化机制

### 构造器
```java
public ActionIndicator()
```
创建实例并初始化单例引用，设置默认尺寸并隐藏。

### 初始化注意事项
默认不可见，只有设置了有效动作才显示。

## 7. 方法详解

### layout()

**可见性**：protected synchronized

**是否覆写**：是

**方法职责**：计算视觉元素的位置。

**核心实现逻辑**：
- 主视觉元素居中对齐
- 次级视觉元素放置在右下角

### update()

**可见性**：public

**是否覆写**：是

**方法职责**：每帧更新状态。

**核心实现逻辑**：
- 检查动作状态变化
- 创建/销毁视觉元素
- 根据英雄状态调整透明度

### onClick()

**可见性**：protected

**是否覆写**：是

**方法职责**：处理点击事件。

**核心实现逻辑**：
```java
if (action != null && Dungeon.hero.ready) {
    action.doAction();
}
```

### setAction(Action action)

**可见性**：public static synchronized

**方法职责**：设置当前动作。

### clearAction()

**可见性**：public static synchronized

**方法职责**：清除当前动作。

## 8. 对外暴露能力

### 显式 API
- `setAction(Action)`：设置动作
- `clearAction()`：清除动作
- `refresh()`：刷新显示

### 内部辅助方法
无。

### 扩展入口
Action 接口可被各种动作类型实现。

## 9. 运行机制与调用链

### 创建时机
GameScene 初始化时。

### 调用者
- `GameScene`：创建
- Action 实现类：设置动作

### 被调用者
- `Action.doAction()`：执行动作

### 系统流程位置
游戏界面交互层。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
通过 Messages.get() 获取动作名称。

### 中文翻译来源
ui_zh.properties 文件。

## 11. 使用示例

### 基本用法
```java
// 设置动作
ActionIndicator.setAction(new MyAction());

// 清除动作
ActionIndicator.clearAction();
```

### 实现 Action 接口
```java
public class MyAction implements ActionIndicator.Action {
    @Override
    public String actionName() {
        return "我的动作";
    }
    
    @Override
    public int indicatorColor() {
        return 0xFF00FF00;
    }
    
    @Override
    public void doAction() {
        // 执行动作逻辑
    }
}
```

## 12. 开发注意事项

### 状态依赖
- 依赖 Dungeon.hero.ready 状态
- 需要设置有效动作才显示

### 生命周期耦合
与游戏场景生命周期绑定。

### 常见陷阱
- 忘记清除动作会导致UI残留
- 线程安全问题需要使用 synchronized 方法

## 13. 修改建议与扩展点

### 适合扩展的位置
- Action 接口可扩展新方法
- 可覆写 primaryVisual() 自定义视觉

### 不建议修改的位置
- 静态单例模式

### 重构建议
无当前重构需求。

## 14. 事实核查清单

- [x] 已覆盖全部字段
- [x] 已覆盖主要方法
- [x] 已检查继承链与覆写关系
- [x] 已核对官方中文翻译
- [x] 无推测性表述
- [x] 示例代码真实可用
- [x] 已标注资源关联
- [x] 已明确说明注意事项与扩展点