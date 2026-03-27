# CavesFissureRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\CavesFissureRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 324 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
CavesFissureRoom 实现了一个洞穴裂缝主题的房间，内部包含多条放射状的深渊（CHASM）裂缝，从房间中心向外延伸。房间会根据尺寸类别生成2-4条裂缝，并在裂缝上生成桥梁（EMPTY_SP地形）以确保可通行性。房间使用复杂的几何计算来确定裂缝的角度和位置。

### 系统定位
作为 StandardRoom 的具体实现，CavesFissureRoom 提供了一种具有复杂几何结构的特殊房间类型，通过算法生成逼真的裂缝效果。

### 不负责什么
- 不处理房间与其他房间的连接逻辑（由父类处理）
- 不管理裂缝的具体视觉效果（由地形系统处理）
- 不负责深渊相关的游戏机制（如坠落伤害等）

## 3. 结构总览

### 主要成员概览
- 无实例字段
- 静态常量：A（弧度到角度的转换因子）

### 主要逻辑块概览
- 尺寸限制覆写（minWidth/minHeight）
- 尺寸概率配置（sizeCatProbs）
- 合并条件覆写（canMerge）
- 放置点过滤（canPlaceItem/canPlaceCharacter）
- 房间绘制逻辑（paint）
- 辅助方法（buildBridge, angleBetweenPoints, xyToRoomCoords）

### 生命周期/调用时机
- 在地牢生成过程中通过 StandardRoom.createRoom() 创建
- paint() 方法在房间布局完成后被调用以填充地形和生成裂缝
- 绘制过程可能需要多次尝试以确保路径连通性

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
- `minWidth()` - 设置最小宽度为7
- `minHeight()` - 设置最小高度为7  
- `sizeCatProbs()` - 设置尺寸概率为 `{9, 3, 1}`
- `canMerge()` - 特殊的合并条件（允许与 CHASM 地形合并）
- `canPlaceItem()` - 禁止在 EMPTY_SP（桥梁）上放置物品
- `canPlaceCharacter()` - 禁止在 EMPTY_SP（桥梁）上放置角色
- `paint()` - 实现复杂的裂缝生成逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 继承）
- Bundlable 接口（通过 Room 继承）

### 依赖的关键类
- `Level` - 地牢关卡对象
- `Terrain` - 地形类型（WALL, EMPTY, EMPTY_SP, CHASM）
- `Painter` - 地形填充工具
- `PathFinder` - 路径查找，用于确保连通性
- `Random` - 随机数生成
- `PointF` - 浮点坐标操作
- `Point` - 整数坐标操作

### 使用者
- StandardRoom.createRoom() 工厂方法会创建此房间的实例
- 关卡生成器调用 paint() 方法来渲染房间
- 物品和角色放置系统调用放置点过滤方法

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| A | double | 180 / Math.PI | 弧度到角度的转换因子（约57.2958） |

### 实例字段
无

## 6. 构造与初始化机制

### 构造器
使用默认的无参构造器，继承自 StandardRoom。

### 初始化块
继承了 StandardRoom 的 `{ setSizeCat(); }` 初始化块。

### 初始化注意事项
- 尺寸类别在 StandardRoom 构造时就已经确定
- 由于 sizeCatProbs() 返回 `{9, 3, 1}`，房间有约 69% 概率为 NORMAL 尺寸，23% 概率为 LARGE 尺寸，8% 概率为 GIANT 尺寸
- 最小尺寸限制为7x7，确保裂缝效果可见

## 7. 方法详解

### minWidth()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：返回房间的最小宽度，确保至少为7格

**参数**：无

**返回值**：int，Math.max(super.minWidth(), 7)

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
调用父类的 minWidth() 并与7取最大值，确保房间足够大以容纳裂缝结构。

**边界情况**：如果父类返回的尺寸大于7，则使用父类的值

### minHeight()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：返回房间的最小高度，确保至少为7格

**参数**：无

**返回值**：int，Math.max(super.minHeight(), 7)

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
调用父类的 minHeight() 并与7取最大值，确保房间足够大以容纳裂缝结构。

**边界情况**：如果父类返回的尺寸大于7，则使用父类的值

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：返回各尺寸类别的选择概率

**参数**：无

**返回值**：float[]，`{9, 3, 1}`

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定的概率数组，NORMAL 尺寸概率为9，LARGE 尺寸概率为3，GIANT 尺寸概率为1。

**边界情况**：所有三种尺寸类别都可能被选中，但 NORMAL 占主导

### canMerge(Level l, Room other, Point p, int mergeTerrain)

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：检查是否可以与另一个房间合并

**参数**：
- `l` (Level)：当前关卡
- `other` (Room)：要合并的另一个房间
- `p` (Point)：合并点
- `mergeTerrain` (int)：合并后的地形类型

**返回值**：boolean，如果合并地形是 CHASM 则返回 true，否则检查合并点内侧是否不是 CHASM

**前置条件**：pointInside(p, 1) 返回的点必须在关卡范围内

**副作用**：无

**核心实现逻辑**：
- 如果 mergeTerrain 是 Terrain.CHASM，直接返回 true（允许与深渊合并）
- 否则，检查合并点内侧一个格子的地形是否不是 CHASM

**边界情况**：如果 pointInside 返回的点超出关卡范围，l.pointToCell() 可能返回无效索引

### canPlaceItem(Point p, Level l)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：检查指定点是否可以放置物品

**参数**：
- `p` (Point)：要检查的点
- `l` (Level)：当前关卡

**返回值**：boolean，只有当父类允许且该点不是 EMPTY_SP 时返回 true

**前置条件**：点 p 必须在房间范围内

**副作用**：无

**核心实现逻辑**：
首先调用父类的 canPlaceItem() 确保点在房间内部，然后检查该点对应的地形是否不是 EMPTY_SP（桥梁）。

**边界情况**：如果点超出关卡范围，l.pointToCell(p) 可能返回无效索引

### canPlaceCharacter(Point p, Level l)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：检查指定点是否可以放置角色

**参数**：
- `p` (Point)：要检查的点
- `l` (Level)：当前关卡

**返回值**：boolean，只有当父类允许且该点不是 EMPTY_SP 时返回 true

**前置条件**：点 p 必须在房间范围内

**副作用**：无

**核心实现逻辑**：
首先调用父类的 canPlaceCharacter() 确保点在房间内部，然后检查该点对应的地形是否不是 EMPTY_SP（桥梁）。

**边界情况**：如果点超出关卡范围，l.pointToCell(p) 可能返回无效索引

### paint(Level level)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制房间的具体内容，包括裂缝和桥梁的生成

**参数**：
- `level` (Level)：要绘制的关卡

**返回值**：void

**前置条件**：房间的位置和尺寸必须已确定

**副作用**：
- 修改 level.map 数组中的地形
- 可能多次重试以确保路径连通性

**核心实现逻辑**：
1. 设置循环标志 pathable = true
2. 配置 PathFinder 的地图尺寸
3. 进入 do-while 循环，直到生成可通行的布局：
   - 填充基础结构：外层 WALL，内层 EMPTY
   - 计算每个门相对于房间中心的角度
   - 生成2-4条裂缝线的角度（基于 sizeFactor()）
     - 裂缝不能太靠近门（NORMAL 尺寸30度，其他15度）
     - 裂缝之间不能太近（2条线时120度，更多时60度）
   - 如果无法生成至少2条裂缝，退化为空房间
   - 对每条裂缝线：
     - 使用 Bresenham 算法绘制 CHASM 裂缝
     - 根据方向（水平/垂直）和尺寸类别调整裂缝宽度
   - 在大型房间中心添加额外的 CHASM 区域
   - 调用 buildBridge() 为裂缝生成 EMPTY_SP 桥梁
   - 确保所有非 CHASM 区域都是可通行的（使用 PathFinder 验证）
4. 恢复 PathFinder 的原始地图尺寸

**边界情况**：
- 如果无法生成有效的裂缝布局，会退化为空房间
- 路径连通性验证确保玩家不会被困住
- 裂缝角度计算考虑了圆形空间的360度特性

### buildBridge(Level level, float fissureAngle, PointF center, int centerMargin)

**可见性**：private

**是否覆写**：否

**方法职责**：为指定的裂缝生成桥梁

**参数**：
- `level` (Level)：关卡对象
- `fissureAngle` (float)：裂缝角度（度）
- `center` (PointF)：房间中心点
- `centerMargin` (int)：中心区域边距

**返回值**：void

**前置条件**：裂缝必须已经绘制完成

**副作用**：
- 修改 level.map 数组，将部分 CHASM 替换为 EMPTY_SP

**核心实现逻辑**：
1. 根据裂缝角度确定桥梁方向（水平或垂直）
2. 随机选择桥梁位置（避开中心区域和边缘）
3. 沿着裂缝方向找到 CHASM 区域
4. 将 CHASM 替换为 EMPTY_SP 形成桥梁

**边界情况**：
- 桥梁位置随机选择，确保多样性
- 边缘保护（edgemargin = 2）防止桥梁过于靠近墙壁

### angleBetweenPoints(PointF from, PointF to)

**可见性**：protected static

**是否覆写**：否

**方法职责**：计算两点之间的角度

**参数**：
- `from` (PointF)：起始点
- `to` (PointF)：目标点

**返回值**：float，角度（度），范围 -180 到 180

**前置条件**：两点不能在同一垂直线上（x 坐标相同）

**副作用**：无

**核心实现逻辑**：
1. 计算斜率 m = (to.y - from.y) / (to.x - from.x)
2. 使用 atan(m) 计算角度并转换为度
3. 如果 from.x > to.x，减去 180 度以处理第二、三象限

**边界情况**：
- 当 x 坐标相同时会出现除零错误（代码中未处理）
- 角度范围为 -180 到 180 度

### xyToRoomCoords(int x, int y)

**可见性**：protected

**是否覆写**：否

**方法职责**：将绝对坐标转换为房间内部坐标

**参数**：
- `x` (int)：绝对x坐标
- `y` (int)：绝对y坐标

**返回值**：int，房间内部的一维坐标索引

**前置条件**：坐标必须在房间内部

**副作用**：无

**核心实现逻辑**：
计算 `(x-left-1) + ((y-top-1) * (width()-2))`，将二维坐标映射到一维数组索引

**边界情况**：坐标超出房间范围会导致负索引或越界

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是继承自父类的覆写，没有新增的公共API
- angleBetweenPoints() 方法是 protected static，可供其他类使用

### 内部辅助方法
- buildBridge() 和 xyToRoomCoords() 是内部辅助方法
- 所有方法都是为了内部房间生成逻辑服务

### 扩展入口
- 此类是具体的最终实现，一般不需要扩展
- angleBetweenPoints() 可被其他几何计算使用

## 9. 运行机制与调用链

### 创建时机
- 在地牢生成过程中，当 StandardRoom.createRoom() 随机选择到 CavesFissureRoom 时创建

### 调用者
- StandardRoom.createRoom() 创建实例
- LevelBuilder 调用 paint() 方法
- 物品和角色放置系统调用 canPlaceItem()/canPlaceCharacter()

### 被调用者
- 调用父类 StandardRoom 和 Room 的方法
- 调用 Painter.fill()/set()/drawInside() 填充地形
- 调用 PathFinder 进行路径验证
- 调用 Random 生成随机数
- 调用数学函数进行几何计算

### 系统流程位置
- 处于地牢生成的房间绘制阶段
- 在房间连接完成后、内容填充前执行
- 可能需要多次迭代以确保可通行性

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无直接引用的消息键。

### 依赖的资源
- 深渊（CHASM）地形的视觉资源
- 桥梁（EMPTY_SP）地形的视觉资源
- 各种地形的碰撞和交互资源

### 中文翻译来源
项目内未找到官方对应译名。"CavesFissureRoom" 直译为"洞穴裂缝房间"，但由于 levels_zh.properties 文件中没有对应的官方翻译，保留英文名称。

## 11. 使用示例

### 基本用法
```java
// 通过工厂方法创建 CavesFissureRoom 实例
StandardRoom room = StandardRoom.createRoom(); // 可能返回 CavesFissureRoom

// 房间的绘制由关卡生成器自动调用
room.paint(level);
```

### 注意事项
由于 CavesFissureRoom 是通过反射动态创建的，通常不需要直接实例化。

## 12. 开发注意事项

### 状态依赖
- 依赖于父类 StandardRoom 的 sizeCat 初始化状态
- paint() 方法依赖于房间的连接信息（connected 集合）
- 路径验证依赖于 PathFinder 的正确配置

### 生命周期耦合
- 必须在房间连接完成后调用 paint()
- PathFinder 的地图尺寸必须在 paint() 前后正确设置和恢复

### 常见陷阱
- angleBetweenPoints() 方法在垂直线情况下会出现除零错误
- 最小尺寸限制为7x7，如果强制更小的尺寸会导致裂缝效果异常
- 裂缝生成算法复杂，修改时需要注意几何计算的准确性
- 路径验证循环可能导致性能问题（虽然有退化机制）

## 13. 修改建议与扩展点

### 适合扩展的位置
- 此类是具体的最终实现，一般不需要扩展

### 不建议修改的位置
- 裂缝角度约束经过平衡设计，修改会影响房间布局
- 桥梁生成逻辑影响游戏可玩性，不应随意调整
- 最小尺寸限制确保基本的视觉效果，不应降低

### 重构建议
- angleBetweenPoints() 方法应该处理垂直线的特殊情况
- 路径验证逻辑与 PatchRoom 有重复，可以提取为通用工具方法
- 几何计算常量可以提取为命名常量以提高可读性

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（确认无官方翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点