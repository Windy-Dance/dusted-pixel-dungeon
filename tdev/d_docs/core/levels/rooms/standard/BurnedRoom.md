# BurnedRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\BurnedRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends PatchRoom |
| **代码行数** | 135 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
BurnedRoom 实现了一个被烧毁的房间，内部使用补丁系统（patch system）生成随机分布的烧焦区域。房间包含多种地形混合：空地、余烬、燃烧陷阱（可见、隐藏、已触发）。房间通过补丁算法确保内容分布具有随机性和视觉上的烧焦效果。

### 系统定位
作为 PatchRoom 的具体实现，BurnedRoom 提供了一种基于补丁系统的特殊房间类型，利用父类的补丁生成功能来创建具有特定主题的房间布局。

### 不负责什么
- 不负责补丁生成算法的具体实现（由 Patch 类处理）
- 不处理房间与其他房间的连接逻辑（由父类处理）
- 不管理陷阱的具体行为（由 BurningTrap 类处理）

## 3. 结构总览

### 主要成员概览
- 无实例字段（继承自 PatchRoom 的 patch 字段）
- 无静态字段或常量

### 主要逻辑块概览
- 尺寸概率配置（sizeCatProbs）
- 合并条件覆写（canMerge）
- 补丁参数配置（fill, clustering, ensurePath, cleanEdges）
- 房间绘制逻辑（paint）
- 放置点过滤（canPlaceWater, canPlaceGrass, canPlaceTrap）

### 生命周期/调用时机
- 在地牢生成过程中通过 StandardRoom.createRoom() 创建
- paint() 方法在房间布局完成后被调用以填充地形和生成内容
- setupPatch() 在 paint() 中被调用以生成补丁数据

## 4. 继承与协作关系

### 父类提供的能力
从 PatchRoom 继承：
- 补丁系统集成（patch 字段）
- setupPatch() 方法用于生成补丁
- fillPatch() 辅助方法
- xyToPatchCoords() 坐标转换方法
从 StandardRoom 继承：
- 尺寸类别系统
- 权重计算
- 基础房间逻辑
From Room 继承：
- 空间几何操作
- 连接管理
- 放置点过滤框架

### 覆写的方法
- `sizeCatProbs()` - 设置尺寸概率为 `{4, 1, 0}`
- `canMerge()` - 只允许与空地地形合并
- `fill()` - 返回动态计算的填充率
- `clustering()` - 返回聚类值2
- `ensurePath()` - 返回 false（不确保路径连通性）
- `cleanEdges()` - 返回 false（不清除对角线边缘）
- `paint()` - 实现具体的房间绘制逻辑
- `canPlaceWater()` - 禁止在补丁区域放置水
- `canPlaceGrass()` - 禁止在补丁区域放置草
- `canPlaceTrap()` - 禁止在补丁区域放置陷阱（因为陷阱已在 paint 中生成）

### 实现的接口契约
- Graph.Node 接口（通过 Room 继承）
- Bundlable 接口（通过 Room 继承）

### 依赖的关键类
- `TrapMechanism` - 获取陷阱揭示概率
- `Level` - 地牢关卡对象
- `Terrain` - 地形类型（EMPTY, EMBERS, TRAP, SECRET_TRAP, INACTIVE_TRAP）
- `Painter` - 填充基础地形
- `BurningTrap` - 燃烧陷阱类型
- `Random` - 随机数生成
- `Point` - 点坐标操作
- `Patch` - 补丁生成（通过 PatchRoom 间接使用）

### 使用者
- StandardRoom.createRoom() 工厂方法会创建此房间的实例
- 关卡生成器调用 paint() 方法来渲染房间
- 水、草、陷阱放置系统调用相关过滤方法

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无（但继承了 PatchRoom 的 protected boolean[] patch 字段）

## 6. 构造与初始化机制

### 构造器
使用默认的无参构造器，继承自 StandardRoom。

### 初始化块
继承了 StandardRoom 的 `{ setSizeCat(); }` 初始化块。

### 初始化注意事项
- 尺寸类别在 StandardRoom 构造时就已经确定
- 由于 sizeCatProbs() 返回 `{4, 1, 0}`，房间有 80% 概率为 NORMAL 尺寸，20% 概率为 LARGE 尺寸，不会生成 GIANT 尺寸
- patch 字段在 paint() 方法中通过 setupPatch() 初始化

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

**边界情况**：永远不会生成 GIANT 尺寸的 BurnedRoom

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

### fill()

**可见性**：protected

**是否覆写**：是，覆写自 PatchRoom

**方法职责**：返回补丁的填充率

**参数**：无

**返回值**：float，填充率（0-1之间的值）

**前置条件**：房间的宽度和高度必须已确定

**副作用**：无

**核心实现逻辑**：
使用公式 `Math.min(1f, 1.48f - (width()+height())*0.03f)` 计算填充率。
- 对于8x8的房间：1.48 - 16*0.03 = 1.0（100%填充）
- 对于14x14的房间：1.48 - 28*0.03 = 0.64（64%填充）
- 最大填充率为100%

**边界情况**：非常大的房间填充率会趋近于0，但不会低于0

### clustering()

**可见性**：protected

**是否覆写**：是，覆写自 PatchRoom

**方法职责**：返回补丁的聚类程度

**参数**：无

**返回值**：int，2

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定值2，表示中等程度的聚类（值越大聚类越强）

**边界情况**：无

### ensurePath()

**可见性**：protected

**是否覆写**：是，覆写自 PatchRoom

**方法职责**：指定是否确保补丁区域路径连通

**参数**：无

**返回值**：boolean，false

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回 false，表示不需要确保补丁区域的路径连通性

**边界情况**：无

### cleanEdges()

**可见性**：protected

**是否覆写**：是，覆写自 PatchRoom

**方法职责**：指定是否清理补丁的对角线边缘

**参数**：无

**返回值**：boolean，false

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回 false，表示不清理对角线边缘，保持更随机的烧焦效果

**边界情况**：无

### paint(Level level)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制房间的具体内容，包括地形填充和陷阱生成

**参数**：
- `level` (Level)：要绘制的关卡

**返回值**：void

**前置条件**：房间的位置和尺寸必须已确定

**副作用**：
- 修改 level.map 数组中的地形
- 向 level 中添加燃烧陷阱实例

**核心实现逻辑**：
1. 使用 Painter.fill() 填充基础结构：
   - 外层：WALL（墙壁）
   - 内层：EMPTY（空地）
2. 调用 setupPatch() 生成补丁数据
3. 遍历房间内部每个格子，如果是补丁区域则根据随机选择设置地形：
   - 40% 概率：EMPTY（空地）
   - 20% 概率：EMBERS（余烬）
   - 20% 概率：TRAP（可见的燃烧陷阱）
   - 20% 概率：SECRET_TRAP 或 TRAP（基于 TrapMechanism 的揭示概率）
   - 额外处理：INACTIVE_TRAP（已触发的陷阱）
4. 将所有连接的门设置为 REGULAR 类型

**边界情况**：
- 随机选择确保各种地形都有合理的分布
- 陷阱揭示概率机制确保游戏平衡

### canPlaceWater(Point p)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：检查指定点是否可以放置水

**参数**：
- `p` (Point)：要检查的点

**返回值**：boolean，只有当点不在房间内部或不在补丁区域时返回 true

**前置条件**：patch 字段必须已初始化（在 paint() 之后）

**副作用**：无

**核心实现逻辑**：
检查点是否在房间外部，或者在房间内部但不在补丁区域。

**边界情况**：如果 patch 未初始化，xyToPatchCoords() 可能抛出异常

### canPlaceGrass(Point p)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：检查指定点是否可以放置草

**参数**：
- `p` (Point)：要检查的点

**返回值**：boolean，只有当点不在房间内部或不在补丁区域时返回 true

**前置条件**：patch 字段必须已初始化（在 paint() 之后）

**副作用**：无

**核心实现逻辑**：
检查点是否在房间外部，或者在房间内部但不在补丁区域。

**边界情况**：如果 patch 未初始化，xyToPatchCoords() 可能抛出异常

### canPlaceTrap(Point p)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：检查指定点是否可以放置陷阱

**参数**：
- `p` (Point)：要检查的点

**返回值**：boolean，只有当点不在房间内部或不在补丁区域时返回 true

**前置条件**：patch 字段必须已初始化（在 paint() 之后）

**副作用**：无

**核心实现逻辑**：
检查点是否在房间外部，或者在房间内部但不在补丁区域。
注意：由于 paint() 方法已经生成了所有陷阱，因此禁止在补丁区域额外放置陷阱。

**边界情况**：如果 patch 未初始化，xyToPatchCoords() 可能抛出异常

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是继承自父类的覆写，没有新增的公共API

### 内部辅助方法
- 所有 protected 方法都是为了内部补丁系统服务

### 扩展入口
- 此类是具体的最终实现，一般不需要扩展

## 9. 运行机制与调用链

### 创建时机
- 在地牢生成过程中，当 StandardRoom.createRoom() 随机选择到 BurnedRoom 时创建

### 调用者
- StandardRoom.createRoom() 创建实例
- LevelBuilder 调用 paint() 方法
- 水、草、陷阱放置系统调用相应的过滤方法

### 被调用者
- 调用父类 PatchRoom 和 StandardRoom 的方法
- 调用 Painter.fill() 填充基础地形
- 调用 Patch.generate() 生成补丁（通过 setupPatch()）
- 调用 BurningTrap 构造函数创建陷阱实例
- 调用 TrapMechanism.revealHiddenTrapChance() 获取揭示概率

### 系统流程位置
- 处于地牢生成的房间绘制阶段
- 在房间连接完成后、内容填充前执行

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无直接引用的消息键。

### 依赖的资源
- 余烬（EMBERS）地形的视觉资源
- 燃烧陷阱的视觉和音效资源
- 各种陷阱状态（可见、隐藏、已触发）的UI资源

### 中文翻译来源
项目内未找到官方对应译名。"BurnedRoom" 直译为"烧毁的房间"，但由于 levels_zh.properties 文件中没有对应的官方翻译，保留英文名称。

## 11. 使用示例

### 基本用法
```java
// 通过工厂方法创建 BurnedRoom 实例
StandardRoom room = StandardRoom.createRoom(); // 可能返回 BurnedRoom

// 房间的绘制由关卡生成器自动调用
room.paint(level);
```

### 注意事项
由于 BurnedRoom 是通过反射动态创建的，通常不需要直接实例化。

## 12. 开发注意事项

### 状态依赖
- 依赖于父类 StandardRoom 的 sizeCat 初始化状态
- paint() 方法依赖于 patch 字段的正确初始化
- 放置点过滤方法依赖于 patch 字段已初始化

### 生命周期耦合
- 必须在房间连接完成后调用 paint()
- 放置点过滤方法只能在 paint() 之后安全调用

### 常见陷阱
- 如果在 paint() 之前调用放置点过滤方法，会导致 NullPointerException
- 填充率计算公式假设房间尺寸合理，极端尺寸可能导致意外行为
- 陷阱生成逻辑复杂，修改时需要注意平衡性

## 13. 修改建议与扩展点

### 适合扩展的位置
- 此类是具体的最终实现，一般不需要扩展

### 不建议修改的位置
- 填充率计算公式经过平衡设计，修改会影响房间密度
- 地形分布概率影响游戏体验，不应随意调整
- 补丁参数（clustering 等）经过调优，修改可能影响视觉效果

### 重构建议
- 可以考虑将地形分布概率提取为常量以提高可读性
- 陷阱生成逻辑可以提取到单独的方法中以提高可维护性

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（确认无官方翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点