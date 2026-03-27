# EntranceRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\entrance\EntranceRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 188 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
EntranceRoom 类负责创建和绘制地牢关卡的入口房间。入口房间包含通向上一层的楼梯/入口，并负责在游戏初期（第1-2层）放置新手引导页面。

### 系统定位
作为标准房间的一种特殊类型，入口房间是每个地牢关卡的起点，在 LevelGenerator 的房间生成过程中被专门创建。它是玩家进入新关卡时首先到达的位置。

### 不负责什么
- 不负责处理玩家与入口的交互逻辑（由 LevelTransition 和其他系统处理）
- 不负责敌人或物品的常规生成（除了特定的新手引导页面）
- 不负责房间的连接逻辑（由父类 Room 处理）

## 3. 结构总览

### 主要成员概览
- 静态字段 `rooms`: 包含所有可用入口房间类型的列表
- 静态字段 `chances`: 定义不同深度下各入口房间类型的生成概率
- 静态方法 `placeEarlyGuidePages()`: 在前两层放置新手引导页面
- 实例方法 `paint()`: 绘制入口房间的主要逻辑

### 主要逻辑块概览
- 房间尺寸约束（最小5x5）
- 入口标记设置（isEntrance 返回 true）
- 合并和陷阱放置限制（基于地牢深度）
- 入口位置随机选择和地形设置
- 新手引导页面放置逻辑
- 入口房间工厂方法

### 生命周期/调用时机
- 在 LevelGenerator 创建关卡时通过 `createEntrance()` 静态方法创建
- 在 Level.paint() 过程中调用 paint() 方法进行绘制
- `placeEarlyGuidePages()` 在第1-2层的入口房间绘制完成后调用

## 4. 继承与协作关系

### 父类提供的能力
- `StandardRoom` 提供的基础房间尺寸管理（sizeCat, minWidth/maxWidth 等）
- `Room` 提供的空间逻辑、连接逻辑和绘图抽象方法
- `Rect` 提供的几何计算能力

### 覆写的方法
- `minWidth()`: 设置最小宽度为5
- `minHeight()`: 设置最小高度为5  
- `isEntrance()`: 返回 true 标识为入口房间
- `canMerge()`: 在深度≤2时禁止合并
- `canPlaceTrap()`: 在深度=1时禁止放置陷阱
- `paint()`: 实现入口房间的具体绘制逻辑

### 实现的接口契约
- 继承自 `Room` 的 `paint(Level level)` 抽象方法

### 依赖的关键类
- `Level`: 关卡数据结构
- `Terrain`: 地形类型定义
- `Painter`: 地形绘制工具
- `LevelTransition`: 关卡过渡点管理
- `Dungeon`: 全局游戏状态
- `SPDSettings`: 游戏设置
- `Document` 和 `GuidePage/Guidebook`: 新手引导系统
- `Random` 和 `Reflection`: 随机性和反射工具

### 使用者
- `LevelGenerator`: 通过 `createEntrance()` 创建入口房间实例
- 其他入口房间子类：调用 `placeEarlyGuidePages()` 静态方法

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| rooms | ArrayList<Class<? extends StandardRoom>> | 包含20种入口房间类型的列表 | 所有可用的入口房间类型 |
| chances | float[][] | 27个深度的概率数组 | 不同深度下各入口房间类型的生成概率权重 |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| 无实例字段 | - | - | - |

## 6. 构造与初始化机制

### 构造器
EntranceRoom 使用默认构造器，没有显式的构造方法。继承自父类的构造逻辑。

### 初始化块
静态初始化块：
- 初始化 `rooms` 列表，添加所有20种入口房间类型
- 初始化 `chances` 数组，为不同深度设置房间类型概率

### 初始化注意事项
- `rooms` 列表中 `RegionDecoPatchEntranceRoom.class` 被添加了两次（索引3和16），这可能是冗余但不影响功能
- 概率数组 `chances` 只定义到深度26，更深的深度会使用默认值
- 前5层只使用较简单的入口房间类型（前4种）
- 深度6-10使用中间复杂度的房间类型（索引4-7）
- 深度11-15使用更复杂的房间类型（索引8-11）
- 深度16-20使用图书馆相关房间类型（索引12-15）
- 深度21+使用最复杂的房间类型（索引16-19）

## 7. 方法详解（必须覆盖全部方法）

### minWidth()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：确保入口房间的最小宽度至少为5格

**参数**：无

**返回值**：int，返回 Math.max(super.minWidth(), 5)

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
调用父类的 minWidth() 方法，并与5取最大值，确保入口房间足够大以容纳入口和玩家。

**边界情况**：当父类 minWidth() 返回小于5的值时，强制返回5。

### minHeight()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：确保入口房间的最小高度至少为5格

**参数**：无

**返回值**：int，返回 Math.max(super.minHeight(), 5)

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
调用父类的 minHeight() 方法，并与5取最大值，确保入口房间足够大。

**边界情况**：当父类 minHeight() 返回小于5的值时，强制返回5。

### isEntrance()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：标识此房间为入口房间

**参数**：无

**返回值**：boolean，始终返回 true

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
简单返回 true，用于系统识别入口房间。

**边界情况**：无

### canMerge()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：控制入口房间与其他房间的合并行为

**参数**：
- `l` (Level)：当前关卡
- `other` (Room)：要合并的其他房间
- `p` (Point)：合并点
- `mergeTerrain` (int)：合并地形类型

**返回值**：boolean，深度≤2时返回 false，否则返回父类结果

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
检查当前地牢深度，如果深度小于等于2，则禁止合并；否则调用父类的 canMerge 方法。

**边界情况**：深度1-2的入口房间完全不能与其他房间合并。

### canPlaceTrap()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：控制陷阱在入口房间的放置

**参数**：
- `p` (Point)：要放置陷阱的位置

**返回值**：boolean，深度=1时返回 false，否则返回父类结果

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
检查当前地牢深度，如果深度等于1，则禁止放置陷阱；否则调用父类的 canPlaceTrap 方法。

**边界情况**：第1层的入口房间完全不能放置陷阱，确保新手体验。

### paint()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制入口房间的具体实现

**参数**：
- `level` (Level)：要绘制的关卡

**返回值**：void

**前置条件**：房间已正确初始化，level 参数有效

**副作用**：
- 修改 level.map 数组中的地形数据
- 向 level.transitions 添加关卡过渡点
- 可能向 level 中添加物品（新手引导页面）

**核心实现逻辑**：
1. 使用 Painter.fill() 填充墙壁和内部空地
2. 设置所有连接门为 REGULAR 类型
3. 随机选择一个内部位置作为入口点（避开怪物位置）
4. 设置入口地形为 Terrain.ENTRANCE
5. 根据深度添加相应的 LevelTransition（SURFACE 或 REGULAR_ENTRANCE）
6. 调用 placeEarlyGuidePages() 放置新手引导页面

**边界情况**：
- 入口位置选择会避开已有怪物
- 第1层使用 SURFACE 类型过渡，其他层使用 REGULAR_ENTRANCE

### placeEarlyGuidePages()

**可见性**：public static

**是否覆写**：否

**方法职责**：在游戏前两层的入口房间中放置新手引导页面

**参数**：
- `level` (Level)：当前关卡
- `r` (Room)：入口房间

**返回值**：void

**前置条件**：level 和 r 参数有效

**副作用**：
- 使用 Random.pushGenerator()/popGenerator() 确保随机性隔离
- 可能向 level 中添加 Guidebook 或 GuidePage 物品
- 修改 Document.ADVENTURERS_GUIDE 的页面状态

**核心实现逻辑**：
1. 推入独立的随机数生成器以避免影响关卡生成
2. 第1层：如果未读过 GUIDE_INTRO 页面或启用了介绍模式，放置 Guidebook
3. 第2层：如果未找到 GUIDE_SEARCHING 页面，放置对应的 GuidePage
4. 弹出随机数生成器恢复原始状态

**边界情况**：
- 物品放置位置避开入口点和 REGION_DECO 地形
- 不能放置在房间底部行（bottom - 2 限制）

### createEntrance()

**可见性**：public static

**是否覆写**：否

**方法职责**：工厂方法，根据当前深度创建合适的入口房间实例

**参数**：无

**返回值**：StandardRoom，创建的入口房间实例

**前置条件**：Dungeon.depth 已正确设置

**副作用**：可能创建新的房间对象实例

**核心实现逻辑**：
1. 使用 Random.chances() 根据当前深度从 chances 数组选择房间类型索引
2. 使用 Reflection.newInstance() 创建对应类的实例

**边界情况**：
- 如果 chances[Dungeon.depth] 为 null 或无效，Random.chances() 返回 -1，可能导致异常
- 深度超过26时，chances 数组未定义，可能使用 null

## 8. 对外暴露能力

### 显式 API
- `createEntrance()`: 公共工厂方法
- `placeEarlyGuidePages()`: 公共静态方法供子类调用
- 继承的所有公共方法

### 内部辅助方法
- `paint()`: 虽然 public，但主要由系统内部调用
- 尺寸和合并相关的覆写方法

### 扩展入口
- 子类可以通过继承 EntranceRoom 并覆写 paint() 等方法来自定义入口房间行为
- `placeEarlyGuidePages()` 设计为可被子类复用

## 9. 运行机制与调用链

### 创建时机
- 在 LevelGenerator.generate() 方法中，当需要创建入口房间时调用 createEntrance()

### 调用者
- LevelGenerator: 调用 createEntrance()
- Level.paint(): 调用 paint()
- 入口房间子类: 调用 placeEarlyGuidePages()

### 被调用者
- Painter: fill(), set()
- Level: pointToCell(), findMob(), drop()
- Dungeon: 访问 depth 属性
- Random: IntRange(), chances(), pushGenerator(), popGenerator()
- Reflection: newInstance()
- Document: isPageRead(), isPageFound(), deletePage()

### 系统流程位置
1. LevelGenerator 决定需要入口房间
2. 调用 EntranceRoom.createEntrance() 创建实例
3. Level 将房间加入房间列表并建立连接
4. Level.paint() 调用房间的 paint() 方法
5. paint() 执行地形绘制和入口设置
6. 第1-2层额外调用 placeEarlyGuidePages()

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| levels.level.entrance_desc | 通向上一层的楼梯。 | 入口地形的描述文本 |
| levels.caveslevel.entrance_desc | 通向上一层的梯子。 | 洞穴关卡入口描述 |
| levels.citylevel.entrance_desc | 通向上一层的斜坡。 | 城市关卡入口描述 |

### 依赖的资源
- 无直接的纹理/图标/音效依赖
- 间接依赖 Terrain.ENTRANCE 相关的视觉表现

### 中文翻译来源
来自 D:\Develop\Workspace\DustedPixelDungeon\core\src\main\assets\messages\levels\levels_zh.properties

## 11. 使用示例

### 基本用法
```java
// 在 LevelGenerator 中创建入口房间
StandardRoom entrance = EntranceRoom.createEntrance();
// LevelGenerator 会自动将其添加到关卡并调用 paint()
```

### 扩展示例
```java
// 自定义入口房间子类
public class CustomEntranceRoom extends EntranceRoom {
    @Override
    public void paint(Level level) {
        super.paint(level);
        // 添加自定义逻辑
        // 例如放置特殊物品或修改地形
    }
}
```

## 12. 开发注意事项

### 状态依赖
- 严重依赖 Dungeon.depth 全局状态
- 依赖 SPDSettings.intro() 设置来决定是否显示介绍
- 依赖 Document 系统的新手引导状态

### 生命周期耦合
- paint() 方法必须在 Level 完全初始化后调用
- placeEarlyGuidePages() 使用独立的随机数生成器以避免影响关卡生成

### 常见陷阱
- 修改 rooms 列表时要注意索引与 chances 数组的对应关系
- 深度超过26时 chances 数组未定义，可能导致运行时错误
- paint() 方法中的随机位置选择需要避开怪物和其他特殊地形

## 13. 修改建议与扩展点

### 适合扩展的位置
- 创建新的入口房间子类并添加到 rooms 列表
- 扩展 placeEarlyGuidePages() 逻辑以支持更多引导内容
- 修改 chances 数组以调整不同深度的房间类型分布

### 不建议修改的位置
- paint() 方法的核心逻辑（入口设置和过渡点添加）
- 随机数生成器的推入/弹出逻辑
- 基础的尺寸和合并限制逻辑

### 重构建议
- 考虑将 rooms 和 chances 提取到配置文件中
- 将深度相关的逻辑封装到单独的策略类中
- 修复 rooms 列表中的重复项（RegionDecoPatchEntranceRoom）

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点