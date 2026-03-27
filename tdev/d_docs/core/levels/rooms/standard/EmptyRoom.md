# EmptyRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\EmptyRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 39 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
EmptyRoom 实现了一个最简单的标准房间，内部只有基本的两层结构：外层为墙壁（WALL），内层为空地（EMPTY）。这是所有标准房间中最基础的实现，提供了最小化的房间功能。

### 系统定位
作为 StandardRoom 的具体实现，EmptyRoom 提供了最基础的标准房间类型，在地牢生成系统中用作默认或备用房间类型。它也是其他复杂房间类型的参考实现。

### 不负责什么
- 不处理任何特殊装饰或内容生成
- 不包含任何复杂几何结构
- 不管理特定的游戏机制或特殊行为

## 3. 结构总览

### 主要成员概览
- 无实例字段
- 无静态字段或常量

### 主要逻辑块概览
- 房间绘制逻辑（paint）

### 生命周期/调用时机
- 在地牢生成过程中通过 StandardRoom.createRoom() 创建
- paint() 方法在房间布局完成后被调用以填充基本地形

## 4. 继承与协作关系

### 父类提供的能力
从 StandardRoom 继承：
- 尺寸类别系统（SizeCategory）
- 权重计算方法
- 基础的房间尺寸和连接逻辑
From Room 继承：
- 空间几何操作
- 连接管理
- 放置点过滤框架

### 覆写的方法
- `paint()` - 实现最基本的房间绘制逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 继承）
- Bundlable 接口（通过 Room 继承）

### 依赖的关键类
- `Level` - 地牢关卡对象
- `Terrain` - 地形类型（WALL, EMPTY）
- `Painter` - 填充工具

### 使用者
- StandardRoom.createRoom() 工厂方法会创建此房间的实例
- 关卡生成器调用 paint() 方法来渲染房间
- 其他房间类型（如 CellBlockRoom）可能临时使用 EmptyRoom 作为 Rect 对象

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无

## 6. 构造与初始化机制

### 构造器
使用默认的无参构造器，继承自 StandardRoom。

### 初始化块
继承了 StandardRoom 的 `{ setSizeCat(); }` 初始化块。

### 初始化注意事项
- 尺寸类别在 StandardRoom 构造时就已经确定
- 默认使用 StandardRoom 的 sizeCatProbs()，即 `{1, 0, 0}`，总是 NORMAL 尺寸

## 7. 方法详解

### paint(Level level)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制房间的基本内容，包括墙壁和空地

**参数**：
- `level` (Level)：要绘制的关卡

**返回值**：void

**前置条件**：房间的位置和尺寸必须已确定

**副作用**：
- 修改 level.map 数组中的地形

**核心实现逻辑**：
1. 使用 Painter.fill() 填充外层为 WALL（墙壁）
2. 使用 Painter.fill() 填充内层（距离边缘1格）为 EMPTY（空地）
3. 将所有连接的门设置为 REGULAR 类型

**边界情况**：
- 最小可能的房间尺寸为 4x4（StandardRoom.NORMAL.minDim）
- 对于极小的房间，内外层可能重叠或没有内部空间

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是继承自父类的覆写，没有新增的公共API

### 内部辅助方法
- 所有逻辑都在 paint() 方法内部实现

### 扩展入口
- 此类是具体的最终实现，一般不需要扩展
- 由于其实现简单，常被用作其他目的的 Rect 对象（如 CellBlockRoom 中）

## 9. 运行机制与调用链

### 创建时机
- 在地牢生成过程中，当 StandardRoom.createRoom() 随机选择到 EmptyRoom 时创建
- 也可能被其他代码用作 Rect 对象的创建

### 调用者
- StandardRoom.createRoom() 创建实例
- LevelBuilder 调用 paint() 方法
- 其他房间类型可能创建 EmptyRoom 实例用于几何计算

### 被调用者
- 调用父类 StandardRoom 和 Room 的方法
- 调用 Painter.fill() 填充地形

### 系统流程位置
- 处于地牢生成的房间绘制阶段
- 在房间连接完成后、内容填充前执行

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无直接引用的消息键。

### 依赖的资源
- 墙壁（WALL）和空地（EMPTY）等地形的视觉资源

### 中文翻译来源
项目内未找到官方对应译名。"EmptyRoom" 直译为"空房间"，但由于 levels_zh.properties 文件中没有对应的官方翻译，保留英文名称。

## 11. 使用示例

### 基本用法
```java
// 通过工厂方法创建 EmptyRoom 实例
StandardRoom room = StandardRoom.createRoom(); // 可能返回 EmptyRoom

// 房间的绘制由关卡生成器自动调用
room.paint(level);
```

### 作为 Rect 对象使用
```java
// 在其他房间类型中用作几何计算
Rect internal = new EmptyRoom();
internal.set(left+3, top+3, right-3, bottom-3);
```

## 12. 开发注意事项

### 状态依赖
- 依赖于父类 StandardRoom 的 sizeCat 初始化状态
- paint() 方法不依赖其他状态，逻辑简单可靠

### 生命周期耦合
- 必须在房间连接完成后调用 paint()
- connected 集合用于正确设置门的类型

### 常见陷阱
- 由于其简单性，可能被误用为通用 Rect 对象，但这是一种 hacky 的用法
- 没有特殊的放置点过滤，所有内部区域都可以放置物品和角色

## 13. 修改建议与扩展点

### 适合扩展的位置
- 此类是具体的最终实现，一般不需要扩展

### 不建议修改的位置
- 基本结构是设计的核心，不应随意调整
- 作为最基础的房间类型，保持简单性很重要

### 重构建议
- 如果需要通用的 Rect 对象，应该创建专门的工具类而不是复用 EmptyRoom
- 可以考虑添加调试信息以帮助识别其用途

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（确认无官方翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点