# LibraryHallRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\LibraryHallRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 144 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
LibraryHallRoom 实现了一个图书馆大厅主题的房间，内部包含智能布局的书架（BOOKSHELF）系统。房间根据尺寸和门的位置自动决定书架的排列方向（水平或垂直），并在不同尺寸下调整书架数量和装饰位置，创造出多样化的图书馆效果。

### 系统定位
作为 StandardRoom 的具体实现，LibraryHallRoom 提供了一种具有智能布局算法的特殊房间类型，通过几何分析和随机决策创建适应不同房间形状的图书馆布局。

### 不负责什么
- 不处理房间与其他房间的连接逻辑（由父类处理）
- 不管理书架的具体交互机制（由游戏系统处理）
- 不包含书籍或物品的生成逻辑

## 3. 结构总览

### 主要成员概览
- 无实例字段
- 无静态字段或常量

### 主要逻辑块概览
- 尺寸限制覆写（minWidth/minHeight）
- 尺寸概率配置（sizeCatProbs）
- 房间绘制逻辑（paint）
- 布局方向决策算法
- 书架放置逻辑（基于尺寸条件）
- 装饰放置逻辑（基座/区域装饰）

### 生命周期/调用时机
- 在地牢生成过程中通过 StandardRoom.createRoom() 创建
- paint() 方法在房间布局完成后被调用以填充地形和生成内容

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
- `minWidth()` - 设置最小宽度为7
- `minHeight()` - 设置最小高度为7
- `sizeCatProbs()` - 设置尺寸概率为 `{2, 1, 0}`
- `paint()` - 实现图书馆大厅的智能布局逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 继承）
- Bundlable 接口（通过 Room 继承）

### 依赖的关键类
- `Level` - 地牢关卡对象
- `Terrain` - 地形类型（WALL, EMPTY, BOOKSHELF, REGION_DECO）
- `Painter` - 直线绘制和设置工具
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
- 由于 sizeCatProbs() 返回 `{2, 1, 0}`，房间有约 67% 概率为 NORMAL 尺寸，33% 概率为 LARGE 尺寸，永远不会生成 GIANT 尺寸
- 最小尺寸限制为7x7，确保图书馆布局可见

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
调用父类的 minWidth() 并与7取最大值，确保房间足够大以显示图书馆布局。

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
调用父类的 minHeight() 并与7取最大值，确保房间足够大以显示图书馆布局。

**边界情况**：如果父类返回的尺寸大于7，则使用父类的值

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：返回各尺寸类别的选择概率

**参数**：无

**返回值**：float[]，`{2, 1, 0}`

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定的概率数组，NORMAL 尺寸概率为2，LARGE 尺寸概率为1，GIANT 尺寸概率为0。

**边界情况**：永远不会生成 GIANT 尺寸的 LibraryHallRoom，但 TODO 注释提到未来可能支持

### paint(Level level)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制房间的具体内容，包括智能布局的书架和装饰

**参数**：
- `level` (Level)：要绘制的关卡

**返回值**：void

**前置条件**：房间的位置和尺寸必须已确定

**副作用**：
- 修改 level.map 数组中的地形

**核心实现逻辑**：
1. 填充外层为 WALL（墙壁）
2. 填充内层（距离边缘1格）为 EMPTY（空地）
3. **布局方向决策**：
   - 计算 topBottomBooks 和 leftRightBooks 分数
   - 主要基于房间的宽高比（更宽则水平布局，更高则垂直布局）
   - 如果一维为奇数另一维为偶数，优先选择奇数维度
   - 如果仍相等，优先选择门较少的边留空
   - 最终随机决定（如果分数相等）
4. **书架放置**（基于主要维度 majorDim）：
   - 如果 majorDim >= 11 或 < 9：在边缘放置一行书架
   - 如果 majorDim >= 9：在中心区域额外放置两行书架
   - 书架间距根据次要维度 minorDim 调整（>=13 时增加间距）
5. **装饰放置**（REGION_DECO）：
   - 如果 minorDim 为奇数且 < 9：在中心放置单个装饰
   - 否则：在两侧对称放置装饰，位置根据 minorDim 调整
6. **门处理**：
   - 确保所有门都能被访问（向内绘制 EMPTY 通道）
   - 将所有门设置为 REGULAR 类型

**边界情况**：
- 布局方向决策考虑多种因素，确保合理性和多样性
- 书架和装饰的放置适应不同房间尺寸
- 门的可访问性得到保证

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是继承自父类的覆写，没有新增的公共API

### 内部辅助方法
- 所有逻辑都在 paint() 方法内部实现

### 扩展入口
- 此类是具体的最终实现，一般不需要扩展

## 9. 运行机制与调用链

### 创建时机
- 在地牢生成过程中，当 StandardRoom.createRoom() 随机选择到 LibraryHallRoom 时创建

### 调用者
- StandardRoom.createRoom() 创建实例
- LevelBuilder 调用 paint() 方法

### 被调用者
- 调用父类 StandardRoom 和 Room 的方法
- 调用 Painter.fill()/drawLine()/set()/drawInside() 进行绘制
- 调用 Random.Int() 生成随机数
- 调用 Point 进行坐标操作

### 系统流程位置
- 处于地牢生成的房间绘制阶段
- 在房间连接完成后、内容填充前执行

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无直接引用的消息键。

### 依赖的资源
- 书架（BOOKSHELF）地形的视觉资源
- 区域装饰（REGION_DECO）地形的视觉资源
- 墙壁和空地等地形的视觉资源

### 中文翻译来源
项目内未找到官方对应译名。"LibraryHallRoom" 直译为"图书馆大厅房间"，但由于 levels_zh.properties 文件中没有对应的官方翻译，保留英文名称。

## 11. 使用示例

### 基本用法
```java
// 通过工厂方法创建 LibraryHallRoom 实例
StandardRoom room = StandardRoom.createRoom(); // 可能返回 LibraryHallRoom

// 房间的绘制由关卡生成器自动调用
room.paint(level);
```

### 注意事项
由于 LibraryHallRoom 是通过反射动态创建的，通常不需要直接实例化。

## 12. 开发注意事项

### 状态依赖
- 依赖于父类 StandardRoom 的 sizeCat 初始化状态
- paint() 方法依赖于房间的正确尺寸和连接信息

### 生命周期耦合
- 必须在房间连接完成后调用 paint()
- connected 集合用于布局方向决策和门处理

### 常见陷阱
- 最小尺寸限制为7x7，如果强制更小的尺寸会导致布局异常
- 布局方向决策算法复杂，修改时需要注意平衡性
- 书架和装饰的条件逻辑相互依赖，需要整体考虑

## 13. 修改建议与扩展点

### 适合扩展的位置
- 此类是具体的最终实现，一般不需要扩展

### 不建议修改的位置
- 尺寸概率配置确保房间大小分布合理，不应修改
- 最小尺寸限制确保基本的视觉效果，不应降低
- 布局决策算法经过精心设计，随意修改可能破坏多样性

### 重构建议
- TODO 注释提到可以添加 GIANT 变体，这需要添加第三行书架或中心镂空
- 可以考虑将布局决策逻辑提取到单独的方法中以提高可读性
- 常量值（如2, 9, 11, 13）可以提取为命名常量

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（确认无官方翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点