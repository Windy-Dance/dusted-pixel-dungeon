# LibraryRingRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\LibraryRingRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 79 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
LibraryRingRoom 实现了一个环形图书馆主题的房间，内部包含多层同心结构：外层为墙壁，向内依次为书架（BOOKSHELF）、空地（EMPTY）和中心书架。房间在 GIANT 尺寸时会添加十字形的通道，确保大型图书馆的可通行性。

### 系统定位
作为 StandardRoom 的具体实现，LibraryRingRoom 提供了一种具有固定层次结构的图书馆房间类型，通过简单的嵌套矩形创建环形图书馆效果。

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
- 尺寸调整覆写（resize）
- 房间绘制逻辑（paint）
- 十字通道生成（仅 GIANT 尺寸）

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
- `minWidth()` - 设置最小宽度为9
- `minHeight()` - 设置最小高度为9
- `sizeCatProbs()` - 设置尺寸概率为 `{4, 2, 1}`
- `resize(int w, int h)` - 强制 GIANT 尺寸为偶数
- `paint()` - 实现环形图书馆的绘制逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 继承）
- Bundlable 接口（通过 Room 继承）

### 依赖的关键类
- `Level` - 地牢关卡对象
- `Terrain` - 地形类型（WALL, BOOKSHELF, EMPTY）
- `Painter` - 填充和绘图工具
- `Point` - 坐标操作
- `Rect` - 矩形区域操作

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
- 由于 sizeCatProbs() 返回 `{4, 2, 1}`，房间有约 57% 概率为 NORMAL 尺寸，29% 概率为 LARGE 尺寸，14% 概率为 GIANT 尺寸
- 最小尺寸限制为9x9，确保环形结构可见

## 7. 方法详解

### minWidth()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：返回房间的最小宽度，确保至少为9格

**参数**：无

**返回值**：int，Math.max(super.minWidth(), 9)

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
调用父类的 minWidth() 并与9取最大值，确保房间足够大以显示环形图书馆结构。

**边界情况**：如果父类返回的尺寸大于9，则使用父类的值

### minHeight()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：返回房间的最小高度，确保至少为9格

**参数**：无

**返回值**：int，Math.max(super.minHeight(), 9)

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
调用父类的 minHeight() 并与9取最大值，确保房间足够大以显示环形图书馆结构。

**边界情况**：如果父类返回的尺寸大于9，则使用父类的值

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：返回各尺寸类别的选择概率

**参数**：无

**返回值**：float[]，`{4, 2, 1}`

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定的概率数组，NORMAL 尺寸概率为4，LARGE 尺寸概率为2，GIANT 尺寸概率为1。

**边界情况**：所有三种尺寸类别都可能被选中

### resize(int w, int h)

**可见性**：public

**是否覆写**：是，覆写自 Rect

**方法职责**：调整房间尺寸，强制 GIANT 尺寸为偶数

**参数**：
- `w` (int)：期望的宽度
- `h` (int)：期望的高度

**返回值**：Rect，this

**前置条件**：无

**副作用**：
- 如果是 GIANT 尺寸且宽度/高度为奇数，将 right/bottom 减1

**核心实现逻辑**：
1. 调用父类的 resize() 方法
2. 如果 sizeCat 是 GIANT：
   - 如果宽度为奇数，将 right 减1
   - 如果高度为奇数，将 bottom 减1
3. 返回 this

**边界情况**：确保 GIANT 尺寸房间为偶数尺寸，便于中心十字通道的对称绘制

### paint(Level level)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制房间的具体内容，包括环形图书馆结构和十字通道

**参数**：
- `level` (Level)：要绘制的关卡

**返回值**：void

**前置条件**：房间的位置和尺寸必须已确定

**副作用**：
- 修改 level.map 数组中的地形

**核心实现逻辑**：
1. 填充最外层为 WALL（墙壁）
2. 填充第二层（距离边缘1格）为 BOOKSHELF（书架）
3. 填充第三层（距离边缘2格）为 EMPTY（空地）
4. 填充第四层（距离边缘4格）为 BOOKSHELF（书架），形成环形结构
5. 如果是 GIANT 尺寸：
   - 计算房间中心点（总是向下取整）
   - 绘制水平通道：10x2 的 EMPTY 区域
   - 绘制垂直通道：2x10 的 EMPTY 区域
   - 形成十字形通道确保可通行性
6. 处理连接的门：
   - 设置为 REGULAR 类型
   - 从门向内绘制2格的 EMPTY 通道

**边界情况**：
- GIANT 尺寸的偶数强制确保中心点计算的一致性
- 四层结构需要足够的空间（最小9x9）
- 十字通道只在 GIANT 尺寸时生成，避免小型房间过于拥挤

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是继承自父类的覆写，没有新增的公共API

### 内部辅助方法
- 所有逻辑都在 paint() 和 resize() 方法内部实现

### 扩展入口
- 此类是具体的最终实现，一般不需要扩展

## 9. 运行机制与调用链

### 创建时机
- 在地牢生成过程中，当 StandardRoom.createRoom() 随机选择到 LibraryRingRoom 时创建

### 调用者
- StandardRoom.createRoom() 创建实例
- LevelBuilder 调用 paint() 方法

### 被调用者
- 调用父类 StandardRoom 和 Room 的方法
- 调用 Painter.fill()/drawInside() 进行填充和绘制
- 调用 Point 和 Rect 进行坐标操作

### 系统流程位置
- 处于地牢生成的房间绘制阶段
- 在房间连接完成后、内容填充前执行

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无直接引用的消息键。

### 依赖的资源
- 书架（BOOKSHELF）地形的视觉资源
- 墙壁和空地等地形的视觉资源

### 中文翻译来源
项目内未找到官方对应译名。"LibraryRingRoom" 直译为"环形图书馆房间"，但由于 levels_zh.properties 文件中没有对应的官方翻译，保留英文名称。

## 11. 使用示例

### 基本用法
```java
// 通过工厂方法创建 LibraryRingRoom 实例
StandardRoom room = StandardRoom.createRoom(); // 可能返回 LibraryRingRoom

// 房间的绘制由关卡生成器自动调用
room.paint(level);
```

### 注意事项
由于 LibraryRingRoom 是通过反射动态创建的，通常不需要直接实例化。

## 12. 开发注意事项

### 状态依赖
- 依赖于父类 StandardRoom 的 sizeCat 初始化状态
- paint() 方法依赖于房间的正确尺寸和 GIANT 尺寸的偶数约束

### 生命周期耦合
- 必须在房间连接完成后调用 paint()
- resize() 方法在房间尺寸设置时被调用，确保 GIANT 尺寸为偶数

### 常见陷阱
- 最小尺寸限制为9x9，如果强制更小的尺寸会导致环形结构异常
- GIANT 尺寸的偶数强制是十字通道对称性的基础
- 四层结构需要精确的间距计算（1格书架层，2格空地层，4格内书架层）

## 13. 修改建议与扩展点

### 适合扩展的位置
- 此类是具体的最终实现，一般不需要扩展

### 不建议修改的位置
- 尺寸概率配置经过平衡设计，修改会影响房间分布
- 最小尺寸限制确保基本的视觉效果，不应降低
- 四层结构是环形图书馆的核心设计，不应随意调整

### 重构建议
- 可以考虑将常量值（如1, 2, 4, 9, 10）提取为命名常量以提高可读性
- 十字通道的尺寸可以配置化以支持不同的视觉效果

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（确认无官方翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点