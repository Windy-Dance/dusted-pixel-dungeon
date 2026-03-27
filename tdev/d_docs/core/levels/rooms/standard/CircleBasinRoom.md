# CircleBasinRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\CircleBasinRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends PatchRoom |
| **代码行数** | 118 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
CircleBasinRoom 实现了一个圆形水池主题的房间，内部包含椭圆形的空地区域、中央的深渊（CHASM）区域、十字形的桥梁（EMPTY_SP）结构，以及使用补丁系统生成的水域（WATER）。房间强制使用奇数尺寸以确保对称性，并在墙壁装饰上添加特殊效果。

### 系统定位
作为 PatchRoom 的具体实现，CircleBasinRoom 提供了一种具有复杂几何结构和多层次地形的特殊房间类型，结合了椭圆绘制、直线绘制和补丁系统来创建独特的圆形水池效果。

### 不负责什么
- 不负责椭圆和直线绘制算法的具体实现（由 Painter 类处理）
- 不处理房间与其他房间的连接逻辑（由父类处理）
- 不管理水域相关的游戏机制（如熄灭火焰等）

## 3. 结构总览

### 主要成员概览
- 无实例字段（继承自 PatchRoom 的 patch 字段）
- 无静态字段或常量

### 主要逻辑块概览
- 尺寸限制覆写（minWidth/minHeight）
- 尺寸概率配置（sizeCatProbs）
- 尺寸调整覆写（resize）
- 补丁参数配置（fill, clustering, ensurePath, cleanEdges）
- 房间绘制逻辑（paint）
- 几何绘制（椭圆、直线、中心装饰）

### 生命周期/调用时机
- 在地牢生成过程中通过 StandardRoom.createRoom() 创建
- paint() 方法在房间布局完成后被调用以填充地形和生成内容

## 4. 继承与协作关系

### 父类提供的能力
从 PatchRoom 继承：
- 补丁系统集成（patch 字段）
- setupPatch() 方法用于生成补丁
- fillPatch() 辅助方法
- xyToPatchCoords() 坐标转换方法
From StandardRoom 继承：
- 尺寸类别系统
- 权重计算
- 基础房间逻辑
From Room 继承：
- 空间几何操作
- 连接管理
- 放置点过滤框架

### 覆写的方法
- `minWidth()` - 返回 sizeCat.minDim+1
- `minHeight()` - 返回 sizeCat.minDim+1
- `sizeCatProbs()` - 设置尺寸概率为 `{0, 3, 1}`
- `resize(int w, int h)` - 强制奇数尺寸
- `fill()` - 返回固定填充率 0.5f
- `clustering()` - 返回聚类值 5
- `ensurePath()` - 返回 false（不确保路径连通性）
- `cleanEdges()` - 返回 false（不清除对角线边缘）
- `paint()` - 实现复杂的圆形水池绘制逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 继承）
- Bundlable 接口（通过 Room 继承）

### 依赖的关键类
- `Level` - 地牢关卡对象
- `Terrain` - 地形类型（WALL, EMPTY, CHASM, EMPTY_SP, WATER, WALL_DECO）
- `Painter` - 椭圆、直线、填充等绘制工具
- `Point` - 点坐标操作
- `Rect` - 矩形区域操作

### 使用者
- StandardRoom.createRoom() 工厂方法会创建此房间的实例
- 关卡生成器调用 paint() 方法来渲染房间

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
- 由于 sizeCatProbs() 返回 `{0, 3, 1}`，房间永远不会生成 NORMAL 尺寸，有 75% 概率为 LARGE 尺寸，25% 概率为 GIANT 尺寸
- resize() 方法强制房间尺寸为奇数，确保几何对称性

## 7. 方法详解

### minWidth()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：返回房间的最小宽度

**参数**：无

**返回值**：int，sizeCat.minDim + 1

**前置条件**：sizeCat 必须已正确初始化

**副作用**：无

**核心实现逻辑**：
直接返回 sizeCat.minDim + 1，比父类的最小尺寸大1格。

**边界情况**：如果 sizeCat 为 null，会抛出 NullPointerException

### minHeight()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：返回房间的最小高度

**参数**：无

**返回值**：int，sizeCat.minDim + 1

**前置条件**：sizeCat 必须已正确初始化

**副作用**：无

**核心实现逻辑**：
直接返回 sizeCat.minDim + 1，比父类的最小尺寸大1格。

**边界情况**：如果 sizeCat 为 null，会抛出 NullPointerException

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

**边界情况**：永远不会生成 NORMAL 尺寸的 CircleBasinRoom，确保房间足够大

### resize(int w, int h)

**可见性**：public

**是否覆写**：是，覆写自 Rect

**方法职责**：调整房间尺寸并强制为奇数

**参数**：
- `w` (int)：期望的宽度
- `h` (int)：期望的高度

**返回值**：Rect，this

**前置条件**：无

**副作用**：
- 修改 right 和 bottom 坐标以确保奇数尺寸

**核心实现逻辑**：
1. 调用父类的 resize() 方法
2. 如果宽度为偶数，将 right 减1
3. 如果高度为偶数，将 bottom 减1
4. 返回 this

**边界情况**：确保所有 CircleBasinRoom 都有奇数尺寸，便于对称绘制

### fill()

**可见性**：protected

**是否覆写**：是，覆写自 PatchRoom

**方法职责**：返回补丁的填充率

**参数**：无

**返回值**：float，0.5f（50%填充率）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定的 0.5f 填充值。

**边界情况**：无

### clustering()

**可见性**：protected

**是否覆写**：是，覆写自 PatchRoom

**方法职责**：返回补丁的聚类程度

**参数**：无

**返回值**：int，5

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定值5，表示很高的聚类程度（值越大聚类越强），使水域区域更集中

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
返回 false，表示不需要确保补丁区域的路径连通性。

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
返回 false，表示不清理对角线边缘，保持更自然的水域形状。

**边界情况**：无

### paint(Level level)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制房间的具体内容，包括多层几何结构和水域生成

**参数**：
- `level` (Level)：要绘制的关卡

**返回值**：void

**前置条件**：房间的位置和尺寸必须已确定

**副作用**：
- 修改 level.map 数组中的地形

**核心实现逻辑**：
1. 填充最外层为 WALL（墙壁）
2. 使用 Painter.fillEllipse() 填充内层为 EMPTY（空地）
3. 处理连接的门：
   - 设置为 REGULAR 类型
   - 从门向内绘制通道到房间中心
4. 使用 Painter.fillEllipse() 填充中央为 CHASM（深渊）
5. 绘制十字形桥梁：
   - 垂直桥梁：从顶部到底部
   - 水平桥梁：从左到右
   - 使用 EMPTY_SP（特殊空地）地形
6. 在大型房间（>11x11）中添加中心装饰：
   - 3x3 的 EMPTY_SP 区域
   - 中心点设置为 WALL（墙壁）
7. 调用 setupPatch() 生成补丁数据
8. 遍历房间内部，将 EMPTY 区域中的补丁位置转换为 WATER（水域）
9. 如果水域上方是墙壁，将墙壁转换为 WALL_DECO（墙壁装饰）

**边界情况**：
- 奇数尺寸确保所有几何中心点都是精确的整数坐标
- 大型房间（>11）才有中心装饰
- 水域只在 EMPTY 区域中生成，不会覆盖其他地形

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是继承自父类的覆写，没有新增的公共API

### 内部辅助方法
- 所有 protected 方法都是为了配置父类的补丁系统

### 扩展入口
- 此类是具体的最终实现，一般不需要扩展

## 9. 运行机制与调用链

### 创建时机
- 在地牢生成过程中，当 StandardRoom.createRoom() 随机选择到 CircleBasinRoom 时创建

### 调用者
- StandardRoom.createRoom() 创建实例
- LevelBuilder 调用 paint() 方法

### 被调用者
- 调用父类 PatchRoom、StandardRoom 和 Room 的方法
- 调用 Painter.fillEllipse()/drawLine()/fill()/set()/drawInside() 进行几何绘制
- 调用 Point 和 Rect 进行坐标操作

### 系统流程位置
- 处于地牢生成的房间绘制阶段
- 在房间连接完成后、内容填充前执行

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无直接引用的消息键。

### 依赖的资源
- 水域（WATER）地形的视觉资源
- 深渊（CHASM）地形的视觉资源
- 墙壁装饰（WALL_DECO）地形的视觉资源
- 桥梁（EMPTY_SP）地形的视觉资源

### 中文翻译来源
项目内未找到官方对应译名。"CircleBasinRoom" 直译为"圆形水池房间"，但由于 levels_zh.properties 文件中没有对应的官方翻译，保留英文名称。

## 11. 使用示例

### 基本用法
```java
// 通过工厂方法创建 CircleBasinRoom 实例
StandardRoom room = StandardRoom.createRoom(); // 可能返回 CircleBasinRoom

// 房间的绘制由关卡生成器自动调用
room.paint(level);
```

### 注意事项
由于 CircleBasinRoom 是通过反射动态创建的，通常不需要直接实例化。

## 12. 开发注意事项

### 状态依赖
- 依赖于父类 StandardRoom 的 sizeCat 初始化状态
- paint() 方法依赖于 patch 字段的正确初始化
- 几何绘制依赖于房间的奇数尺寸

### 生命周期耦合
- 必须在房间连接完成后调用 paint()
- resize() 方法在房间尺寸设置时被调用，确保奇数尺寸

### 常见陷阱
- 尺寸限制确保房间至少为 LARGE 尺寸，如果强制更小的尺寸会导致布局异常
- 奇数尺寸强制确保了几何对称性，这是设计的核心要求
- 水域生成只在 EMPTY 区域进行，不会影响其他地形
- 中心装饰只在大型房间中出现，避免小型房间过于拥挤

## 13. 修改建议与扩展点

### 适合扩展的位置
- 此类是具体的最终实现，一般不需要扩展

### 不建议修改的位置
- 尺寸概率配置确保房间足够大，不应修改
- 奇数尺寸强制是几何对称的基础，不能移除
- 补丁参数（高聚类值）确保水域集中，不应随意调整
- 中心装饰的尺寸阈值经过平衡设计，修改会影响视觉效果

### 重构建议
- 可以考虑将常量值（如11）提取为命名常量以提高可读性
- 几何绘制逻辑可以提取到单独的方法中以提高可维护性

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（确认无官方翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点