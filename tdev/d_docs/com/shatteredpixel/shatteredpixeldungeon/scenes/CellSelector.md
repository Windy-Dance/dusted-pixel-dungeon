# CellSelector 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/CellSelector.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.scenes |
| **类类型** | class |
| **继承关系** | extends ScrollArea |
| **代码行数** | 525 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
CellSelector 是单元格选择器类，负责处理游戏场景中的点击、拖拽和目标选择操作。

### 系统定位
位于 scenes 包中，作为 ScrollArea 的子类，是玩家与游戏世界交互的核心输入系统。

### 不负责什么
- 不负责游戏逻辑执行
- 不负责视觉效果渲染

## 3. 结构总览

### 主要成员概览
- 字段：listener, enabled, dragThreshold, mouseZoom
- 内部接口：Listener
- 方法：onClick(), onScroll(), select(), resetKeyTimeout()

### 主要逻辑块概览
1. 输入处理：点击、拖拽、滚轮
2. 目标选择：精灵优先、单元格转换
3. 坐标转换：屏幕坐标到游戏坐标

### 生命周期/调用时机
由 GameScene 创建并管理。

## 4. 继承与协作关系

### 父类提供的能力
ScrollArea 提供：
- 滚动区域基础
- 输入事件处理

### 依赖的关键类
- `DungeonTilemap`：地图渲染
- `Camera`：相机系统
- `PointerEvent`：指针事件
- `KeyEvent`：键盘事件
- `ControllerHandler`：控制器输入

### 使用者
- `GameScene`：游戏场景

## 5. 字段/常量详解

### 实例字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| listener | Listener | 选择监听器 |
| enabled | boolean | 是否启用 |
| dragThreshold | float | 拖拽阈值 |
| mouseZoom | float | 鼠标缩放值 |

## 6. 构造与初始化机制

### 构造器
```java
public CellSelector(DungeonTilemap map)
```
创建选择器实例，设置相机和拖拽阈值。

## 7. 方法详解

### onClick(PointerEvent event)

**可见性**：protected

**是否覆写**：是

**方法职责**：处理点击事件。

**核心实现逻辑**：
1. 检查是否为拖拽
2. 将屏幕坐标转换为世界坐标
3. 优先检查精灵重叠
4. 选择对应单元格

### onScroll(ScrollEvent event)

**可见性**：protected

**是否覆写**：是

**方法职责**：处理滚轮缩放。

### select(int cell, PointerEvent.Button button)

**可见性**：public

**方法职责**：选择单元格并通知监听器。

**核心实现逻辑**：
```java
if (enabled && listener != null) {
    listener.onSelect(cell);
    // 播放音效
}
```

### resetKeyTimeout()

**可见性**：public static

**方法职责**：重置键盘选择超时。

## 8. 对外暴露能力

### 显式 API
- `listener`：设置监听器
- `enabled`：启用/禁用
- `select(int, Button)`：手动选择

### 内部辅助方法
- 坐标转换方法

### 扩展入口
Listener 接口可自定义选择行为。

## 9. 运行机制与调用链

### 创建时机
GameScene 初始化时。

### 调用者
- `GameScene`：创建
- 用户输入：触发选择

### 被调用者
- `Listener.onSelect()`：通知选择结果
- `DungeonTilemap`：坐标转换

### 系统流程位置
游戏输入处理层。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
通过 listener.prompt() 获取提示文本。

### 中文翻译来源
scenes_zh.properties 文件。

## 11. 使用示例

### 基本用法
```java
CellSelector selector = new CellSelector(tiles);
selector.listener = new CellSelector.Listener() {
    @Override
    public void onSelect(Integer cell) {
        if (cell != null) {
            hero.handle(cell);
        }
    }
    
    @Override
    public String prompt() {
        return "选择目标";
    }
};
```

## 12. 开发注意事项

### 状态依赖
- 依赖 DungeonTilemap 进行坐标转换
- 依赖 Camera 进行视图转换

### 生命周期耦合
与游戏场景生命周期绑定。

### 常见陷阱
- 精灵优先级高于单元格
- 拖拽会取消点击

## 13. 修改建议与扩展点

### 适合扩展的位置
- Listener 接口实现

### 不建议修改的位置
- 坐标转换逻辑

### 重构建议
无当前重构需求。

## 14. 事实核查清单

- [x] 已覆盖主要字段
- [x] 已覆盖主要方法
- [x] 已检查继承链与覆写关系
- [x] 已核对官方中文翻译
- [x] 无推测性表述
- [x] 示例代码真实可用
- [x] 已标注资源关联
- [x] 已明确说明注意事项与扩展点

## 附录：Listener 接口

```java
public interface Listener {
    void onSelect(Integer cell);
    String prompt();
}
```

选择回调接口，用于处理选择结果和显示提示。