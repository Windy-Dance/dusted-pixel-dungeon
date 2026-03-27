# CellBlockRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\CellBlockRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 123 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
CellBlockRoom 实现了一个监狱牢房主题的房间，内部被划分为多个小格子（cells），形成监狱区域。每个格子可以是开放的（EMPTY_SP）或封闭的（REGION_DECO）。房间会根据网格布局在格子之间生成门（DOOR地形），并确保至少有一个格子是开放的（特别是入口/出口房间）。

### 系统定位
作为 StandardRoom 的具体实现，CellBlockRoom 提供了一种具有网格化结构的特殊房间类型，模拟监狱牢房的布局。

### 不负责什么
- 不处理房间与其他房间的连接逻辑（由父类处理）
- 不管理具体的牢房内容（如囚犯、物品等）
- 不负责门的具体交互机制（由游戏系统处理）

## 3. 结构总览

### 主要成员概览
- 无实例字段
- 无静态字段或常量

### 主要逻辑块概览
- 尺寸概率配置（sizeCatProbs）
- 房间绘制逻辑（paint）
- 网格计算和布局
- 门位置确定逻辑
- 开放格子随机化

### 生命周期/调用时机
- 在地牢生成过程中通过 StandardRoom.createRoom() 创建
- paint() 方法在房间布局完成后被调用以填充地形和生成格子结构

## 4. 继承与协作关系

### 父类提供的能力
从 StandardRoom 继承：
- 尺寸类别系统（SizeCategory）
- 权重计算方法
- 基础的房间尺寸和连接逻辑
From Room 继承：
- 空间几何操作
- 邻居和连接管理
- 随机点生成
- 抽象的 paint() 方法

### 覆写的方法
- `sizeCatProbs()` - 设置尺寸概率为 `{0, 3, 1}`
- `paint()` - 实现具体的监狱牢房绘制逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 继承）
- Bundlable 接口（通过 Room 继承）

### 依赖的关键类
- `Level` - 地牢关卡对象
- `Terrain` - 地形类型（WALL, EMPTY, EMPTY_SP, REGION_DECO, DOOR）
- `Painter` - 地形填充工具
- `Random` - 随机数生成
- `Rect` - 矩形区域操作
- `EmptyRoom` - 仅用作 Rect 对象创建内部区域

### 使用者
- StandardRoom.createRoom() 工厂方法会创建此房间的实例
- 关卡生成器调用 paint() 方法来渲染房间

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
- 由于 sizeCatProbs() 返回 `{0, 3, 1}`，房间永远不会生成 NORMAL 尺寸，有 75% 概率为 LARGE 尺寸，25% 概率为 GIANT 尺寸
- 这确保了房间足够大以容纳网格化的牢房结构

## 7. 方法详解

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：返回各尺寸类别的选择概率

**参数**：无

**返回值**：float[]，`{0, 3, 1}`

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定的概率数组，NORMAL 尺寸概率为0，LARGE 尺寸概率为3，GIANT 尺寸概率为1。

**边界情况**：永远不会生成 NORMAL 尺寸的 CellBlockRoom，确保房间足够大

### paint(Level level)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制房间的具体内容，包括牢房网格和门的生成

**参数**：
- `level` (Level)：要绘制的关卡

**返回值**：void

**前置条件**：房间的位置和尺寸必须已确定

**副作用**：
- 修改 level.map 数组中的地形

**核心实现逻辑**：
1. 填充基础结构：
   - 外层：WALL（墙壁）
   - 中间层：EMPTY（空地）
   - 内层：WALL（墙壁）
2. 创建内部矩形区域（internal），距离外层墙壁3格
3. 计算网格行列数（rows, cols）：
   - 基于内部区域尺寸除以3
   - 特殊处理11x11的尺寸
4. 计算每个格子的宽度和高度（w, h）
5. 确定门的方向策略（topBottomDoors）：
   - 默认优先垂直方向（上下）
   - 当只有一行或一列时切换策略
   - 单格子时禁用特定策略
6. 遍历每个格子：
   - 跳过3x3网格的中心格子
   - 计算格子位置
   - 随机决定格子类型：
     - 大部分为 EMPTY_SP（开放牢房）
     - 少数为 REGION_DECO（封闭牢房）
     - 入口/出口房间保证至少有一个开放格子
   - 根据策略在格子边缘生成 DOOR（门）
7. 将所有连接的外部门设置为 REGULAR 类型

**边界情况**：
- 3x3网格时跳过中心格子
- 入口/出口房间保证可通行性
- 单格子房间的特殊门生成逻辑
- 11x11尺寸的特殊处理

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是继承自父极的覆写，没有新增的公共API

### 内部辅助方法
- 所有逻辑都在 paint() 方法内部实现

### 扩展入口
- 此类是具体的最终实现，一般不需要扩展

## 9. 运行机制与调用链

### 创建时机
- 在地牢生成过程中，当 StandardRoom.createRoom() 随机选择到 CellBlockRoom 时创建

### 调用者
- StandardRoom.createRoom() 创建实例
- LevelBuilder 调用 paint() 方法

### 被调用者
- 调用父类 StandardRoom 和 Room 的方法
- 调用 Painter.fill()/set() 填充地形
- 调用 Random.Int() 生成随机数
- 调用 isEntrance()/isExit() 检查房间类型

### 系统流程位置
- 处于地牢生成的房间绘制阶段
- 在房间连接完成后、内容填充前执行

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无直接引用的消息键。

### 依赖的资源
- 区域装饰（REGION_DECO）地形的视觉资源
- 特殊空地（EMPTY_SP）地形的视觉资源
- 门（DOOR）地形的视觉和交互资源

### 中文翻译来源
项目内未找到官方对应译名。"CellBlockRoom" 直译为"牢房区房间"，但由于 levels_zh.properties 文件中没有对应的官方翻译，保留英文名称。

## 11. 使用示例

### 基本用法
```java
// 通过工厂方法创建 CellBlockRoom 实例
StandardRoom room = StandardRoom.createRoom(); // 可能返回 CellBlockRoom

// 房间的绘制由关卡生成器自动调用
room.paint(level);
```

### 注意事项
由于 CellBlockRoom 是通过反射动态创建的，通常不需要直接实例化。

## 12. 开发注意事项

### 状态依赖
- 依赖于父类 StandardRoom 的 sizeCat 初始化状态
- paint() 方法依赖于房间的连接信息（用于判断是否为入口/出口）

### 生命周期耦合
- 必须在房间连接完成后调用 paint()
- 连接信息影响开放格子的保证逻辑

### 常见陷阱
- 尺寸限制确保房间至少为 LARGE 尺寸，如果强制更小的尺寸会导致网格计算异常
- 3x3网格的中心格子被跳过，这是有意的设计
- 11x11尺寸的特殊处理可能影响网格布局
- 门生成逻辑复杂，修改时需要注意连通性

## 13. 修改建议与扩展点

### 适合扩展的位置
- 此类是具体的最终实现，一般不需要扩展

### 不建议修改的位置
- 尺寸概率配置确保房间足够大，不应修改
- 网格计算逻辑经过平衡设计，修改会影响布局
- 开放格子保证机制影响游戏可玩性，不应随意调整

### 重构建议
- 可以考虑将网格计算逻辑提取到单独的方法中以提高可读性
- 门生成策略可以使用枚举类型替代布尔值以提高清晰度
- 常量值（如3格边距）可以提取为命名常量

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（确认无官方翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点