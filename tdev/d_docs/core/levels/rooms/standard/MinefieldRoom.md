# MinefieldRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\MinefieldRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 98 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
MinefieldRoom 实现了一个雷区主题的房间，内部包含随机分布的地雷（爆炸陷阱）。房间根据尺寸类别调整地雷数量，并在地雷周围随机放置余烬（EMBERS）作为视觉提示。地雷可以是可见的或隐藏的，基于 TrapMechanism 的揭示概率机制。

### 系统定位
作为 StandardRoom 的具体实现，MinefieldRoom 提供了一种具有危险陷阱内容的特殊房间类型，增加地牢的挑战性和策略性。

### 不负责什么
- 不处理房间与其他房间的连接逻辑（由父类处理）
- 不管理陷阱的具体爆炸机制（由 ExplosiveTrap 类处理）
- 不负责余烬的具体视觉效果（由地形系统处理）

## 3. 结构总览

### 主要成员概览
- 无实例字段
- 无静态字段或常量

### 主要逻辑块概览
- 尺寸概率配置（sizeCatProbs）
- 合并条件覆写（canMerge）
- 房间绘制逻辑（paint）
- 地雷数量计算
- 陷阱放置和揭示概率处理
- 余烬装饰生成

### 生命周期/调用时机
- 在地牢生成过程中通过 StandardRoom.createRoom() 创建
- paint() 方法在房间布局完成后被调用以填充地形和生成陷阱

## 4. 继承与协作关系

### 父类提供的能力
From StandardRoom 继承：
- 尺寸类别系统（SizeCategory）
- 权重计算方法
- 基础的房间尺寸和连接逻辑
From Room 继承：
- 空间几何操作
- 连接管理
- 随机点生成
- 抽象的 paint() 方法

### 覆写的方法
- `sizeCatProbs()` - 设置尺寸概率为 `{4, 1, 0}`
- `canMerge()` - 只允许与 EMPTY 地形合并
- `paint()` - 实现雷区陷阱和余烬的生成逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 继承）
- Bundlable 接口（通过 Room 继承）

### 依赖的关键类
- `Level` - 地牢关卡对象
- `Terrain` - 地形类型（WALL, EMPTY, EMBERS, TRAP, SECRET_TRAP）
- `Painter` - 填充和设置工具
- `ExplosiveTrap` - 爆炸陷阱类型
- `TrapMechanism` - 陷阱揭示概率机制
- `PathFinder` - 邻居位置查找
- `Point` - 坐标操作
- `Random` - 随机数生成

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
- 由于 sizeCatProbs() 返回 `{4, 1, 0}`，房间有约 80% 概率为 NORMAL 尺寸，20% 概率为 LARGE 尺寸，永远不会生成 GIANT 尺寸

## 7. 方法详解

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：返回各尺寸类别的选择概率

**参数**：无

**返回值**：float[]，`{4, 1, 0}`

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定的概率数组，NORMAL 尺寸概率为4，LARGE 尺寸概率为1，GIANT 尺寸概率为0。

**边界情况**：永远不会生成 GIANT 尺寸的 MinefieldRoom

### canMerge(Level l, Room other, Point p, int mergeTerrain)

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：检查是否可以与另一个房间合并

**参数**：
- `l` (Level)：当前关卡
- `other` (Room)：要合并的另一个房间
- `p` (Point)：合并点
- `mergeTerrain` (int)：合并后的地形类型

**返回值**：boolean，只有当合并点内侧一个格子是 EMPTY 地形时返回 true

**前置条件**：pointInside(p, 1) 返回的点必须在关卡范围内

**副作用**：无

**核心实现逻辑**：
计算合并点内侧一个格子的位置，检查该位置的地形是否为 Terrain.EMPTY。

**边界情况**：如果 pointInside 返回的点超出关卡范围，l.pointToCell() 可能返回无效索引

### paint(Level level)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制房间的具体内容，包括地雷陷阱和余烬装饰

**参数**：
- `level` (Level)：要绘制的关卡

**返回值**：void

**前置条件**：房间的位置和尺寸必须已确定

**副作用**：
- 修改 level.map 数组中的地形
- 向 level 中添加爆炸陷阱实例

**核心实现逻辑**：
1. 填充外层为 WALL（墙壁）
2. 填充内层（距离边缘1格）为 EMPTY（空地）
3. 将所有连接的门设置为 REGULAR 类型
4. 计算地雷数量：
   - 基础数量：`Math.round(Math.sqrt(square()))`
   - NORMAL 尺寸：减3个地雷
   - LARGE 尺寸：加3个地雷  
   - GIANT 尺寸：加9个地雷（虽然不会生成）
5. 为每个地雷执行以下操作：
   - 随机选择一个内部位置（确保不重复）
   - 在地雷周围8个邻居位置中随机放置余烬（EMBERS）
   - 根据 TrapMechanism.revealHiddenTrapChance() 决定地雷是否可见：
     - 如果累积概率 >= 1：放置可见陷阱（TRAP + reveal()）
     - 否则：放置隐藏陷阱（SECRET_TRAP + hide()）

**边界情况**：
- 地雷位置确保不重叠
- 余烬只放置在 EMPTY 地形上
- 陷阱揭示概率机制确保游戏平衡

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是继承自父类的覆写，没有新增的公共API

### 内部辅助方法
- 所有 logic 都在 paint() 方法内部实现

### 扩展入口
- 此类是具体的最终实现，一般不需要扩展

## 9. 运行机制与调用链

### 创建时机
- 在地牢生成过程中，当 StandardRoom.createRoom() 随机选择到 MinefieldRoom 时创建

### 调用者
- StandardRoom.createRoom() 创建实例
- LevelBuilder 调用 paint() 方法

### 被调用者
- 调用父类 StandardRoom 和 Room 的方法
- 调用 Painter.fill()/set() 填充和设置地形
- 调用 TrapMechanism.revealHiddenTrapChance() 获取揭示概率
- 调用 ExplosiveTrap 构造函数创建陷阱实例
- 调用 PathFinder.NEIGHBOURS8 获取邻居位置
- 调用 Random.Int() 生成随机数

### 系统流程位置
- 处于地牢生成的房间绘制阶段
- 在房间连接完成后、内容填充前执行

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无直接引用的消息键。

### 依赖的资源
- 余烬（EMBERS）地形的视觉资源
- 爆炸陷阱的视觉和音效资源
- 各种陷阱状态（可见、隐藏）的UI资源

### 中文翻译来源
项目内未找到官方对应译名。"MinefieldRoom" 直译为"雷区房间"，但由于 levels_zh.properties 文件中没有对应的官方翻译，保留英文名称。

## 11. 使用示例

### 基本用法
```java
// 通过工厂方法创建 MinefieldRoom 实例
StandardRoom room = StandardRoom.createRoom(); // 可能返回 MinefieldRoom

// 房间的绘制由关卡生成器自动调用
room.paint(level);
```

### 注意事项
由于 MinefieldRoom 是通过反射动态创建的，通常不需要直接实例化。

## 12. 开发注意事项

### 状态依赖
- 依赖于父类 StandardRoom 的 sizeCat 初始化状态
- paint() 方法依赖于 TrapMechanism 的揭示概率机制

### 生命周期耦合
- 必须在房间连接完成后调用 paint()
- connected 集合用于正确设置门的类型

### 常见陷阱
- 尺寸概率配置确保房间不会过大，避免地雷过多
- 地雷位置确保不重叠，但可能过于密集
- 余烬装饰只在 EMPTY 地形上生成，避免覆盖其他地形
- 陷阱揭示概率影响游戏难度，不应随意调整

## 13. 修改建议与扩展点

### 适合扩展的位置
- 此类是具体的最终实现，一般不需要扩展

### 不建议修改的位置
- 尺寸概率配置经过平衡设计，修改会影响房间分布
- 地雷数量计算公式影响游戏难度，不应随意调整
- 陷阱揭示概率机制确保游戏可玩性，不应移除

### 重构建议
- 可以考虑将常量值（如3, 9, 8）提取为命名常量以提高可读性
- 余烬生成逻辑可以提取到单独的方法中以提高可维护性
- 地雷数量计算可以配置化以支持不同的难度级别

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（确认无官方翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点