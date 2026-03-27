# StandardRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\StandardRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | abstract class |
| **继承关系** | extends Room |
| **代码行数** | 194 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
StandardRoom 是所有标准房间类型的基础抽象类，为具体的房间实现提供了统一的尺寸分类系统、连接权重计算和合并逻辑。它定义了房间的基本行为模式，并通过尺寸类别系统（SizeCategory）来控制房间生成时的尺寸范围。

### 系统定位
作为房间层次结构中的中间层，StandardRoom 继承自基础的 Room 类，并为所有具体的标准房间实现提供通用功能。它是地牢生成系统中房间创建和配置的关键组件。

### 不负责什么
- 不负责具体的房间绘制逻辑（由子类实现 paint 方法）
- 不负责特定房间类型的特殊装饰或内容生成
- 不处理入口/出口房间的特殊逻辑（这些有专门的类处理）

## 3. 结构总览

### 主要成员概览
- `SizeCategory` 枚举：定义房间尺寸类别（NORMAL, LARGE, GIANT）
- `sizeCat` 字段：当前房间实例的尺寸类别
- `rooms` 静态列表：所有可用的标准房间类型
- `chances` 静态数组：不同深度下各种房间类型的生成概率

### 主要逻辑块概览
- 尺寸类别设置逻辑（setSizeCat 相关方法）
- 尺寸范围计算（minWidth/maxWidth/minHeight/maxHeight）
- 权重计算（sizeFactor, mobSpawnWeight, connectionWeight）
- 房间合并逻辑（canMerge 方法）
- 房间创建工厂方法（createRoom）

### 生命周期/调用时机
- 实例化时自动调用 setSizeCat() 初始化尺寸类别
- 在地牢生成过程中通过 createRoom() 创建具体房间实例
- 在房间连接和合并过程中调用相关方法

## 4. 继承与协作关系

### 父类提供的能力
从 Room 类继承的所有基本功能：
- 空间几何操作（Rect 功能）
- 邻居和连接管理
- 随机点生成
- 连接逻辑和门类型管理
- 抽象的 paint() 方法
- 各种放置点过滤方法

### 覆写的方法
- `minWidth()` - 返回基于尺寸类别的最小宽度
- `maxWidth()` - 返回基于尺寸类别的最大宽度  
- `minHeight()` - 返回基于尺寸类别的最小高度
- `maxHeight()` - 返回基于尺寸类别的最大高度
- `canMerge()` - 提供标准房间的合并条件检查

### 实现的接口契约
- Graph.Node 接口（通过 Room 继承）
- Bundlable 接口（通过 Room 继承）

### 依赖的关键类
- `Dungeon` - 获取当前地牢深度用于房间选择
- `Level` - 地牢关卡对象，用于地形检查
- `Terrain` - 地形类型和标志，用于合并检查
- `Random` - 随机数生成
- `Reflection` - 动态创建房间实例
- `Point` - 点坐标操作

### 使用者
- 地牢生成器（LevelBuilder）通过 createRoom() 创建房间
- 所有具体的 StandardRoom 子类继承其功能
- 房间连接算法使用其连接权重和合并逻辑

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| sizeCat | SizeCategory | 通过初始化块设置 | 当前房间的尺寸类别，决定房间的尺寸范围和权重因子 |

### 静态字段
| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| rooms | ArrayList<Class<?extends StandardRoom>> | 包含35种房间类型的列表 | 所有可用的标准房间类型，按地牢区域分组 |
| chances | float[][] | 27x35的概率数组 | 不同地牢深度下各种房间类型的生成概率 |

## 6. 构造与初始化机制

### 构造器
StandardRoom 没有显式的构造器，使用默认的无参构造器。由于是抽象类，不能直接实例化。

### 初始化块
```java
{ setSizeCat(); }
```
在每个实例创建时自动调用 setSizeCat() 方法来初始化 sizeCat 字段。

### 初始化注意事项
- 尺寸类别在实例化时就已经确定，后续不会改变
- 如果子类需要自定义尺寸概率，需要覆写 sizeCatProbs() 方法
- 初始化失败（概率数组长度不匹配）会导致 sizeCat 为 null

## 7. 方法详解

### SizeCategory 枚举

**可见性**：public static

**是否覆写**：否

**方法职责**：定义标准房间的三种尺寸类别及其参数

**字段说明**：
- `minDim` (int)：该尺寸类别的最小维度
- `maxDim` (int)：该尺寸类别的最大维度  
- `roomValue` (int)：该尺寸类别的权重值

**枚举值**：
- `NORMAL(4, 10, 1)`：普通尺寸，4-10格，权重1
- `LARGE(10, 14, 2)`：大型尺寸，10-14格，权重2
- `GIANT(14, 18, 3)`：巨型尺寸，14-18格，权重3

### sizeCatProbs()

**可见性**：public

**是否覆写**：否，但设计为被子类覆写

**方法职责**：返回各尺寸类别的选择概率数组

**参数**：无

**返回值**：float[]，长度必须等于 SizeCategory.values().length（即3）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
默认返回 `{1, 0, 0}`，即总是选择 NORMAL 尺寸类别。

**边界情况**：子类可以覆写此方法来启用更大的尺寸类别

### setSizeCat() (无参)

**可见性**：public

**是否覆写**：否

**方法职责**：设置房间尺寸类别，允许所有可能的尺寸

**参数**：无

**返回值**：boolean，成功设置返回 true，失败返回 false

**前置条件**：无

**副作用**：修改 sizeCat 字段

**核心实现逻辑**：
调用 `setSizeCat(0, SizeCategory.values().length-1)`，即允许从 NORMAL 到 GIANT 的所有尺寸。

**边界情况**：如果 sizeCatProbs() 返回的数组长度与枚举数量不匹配，返回 false

### setSizeCat(int maxRoomValue)

**可见性**：public

**是否覆写**：否

**方法职责**：根据最大房间值限制尺寸类别选择范围

**参数**：
- `maxRoomValue` (int)：最大允许的房间值（roomValue）

**返回值**：boolean，成功设置返回 true，失败返回 false

**前置条件**：maxRoomValue 应该对应有效的房间值

**副作用**：修改 sizeCat 字段

**核心实现逻辑**：
将 maxRoomValue 转换为枚举序号（减1），然后调用三参数版本的 setSizeCat。

**边界情况**：maxRoomValue <= 0 会导致无法设置任何尺寸

### setSizeCat(int minOrdinal, int maxOrdinal)

**可见性**：public

**是否覆写**：否

**方法职责**：根据枚举序号范围设置房间尺寸类别

**参数**：
- `minOrdinal` (int)：最小允许的枚举序号
- `maxOrdinal` (int)：最大允许的枚举序号

**返回值**：boolean，成功设置返回 true，失败返回 false

**前置条件**：minOrdinal 和 maxOrdinal 应该在有效范围内

**副作用**：修改 sizeCat 字段和 probs 数组

**核心实现逻辑**：
1. 获取子类提供的概率数组
2. 将范围外的概率设为0
3. 使用 Random.chances() 选择尺寸类别
4. 设置 sizeCat 字段

**边界情况**：
- 概率数组长度与枚举数量不匹配时返回 false
- 所有概率都为0时 Random.chances() 返回 -1，方法返回 false

### minWidth()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：返回房间的最小宽度

**参数**：无

**返回值**：int，基于当前 sizeCat 的 minDim 值

**前置条件**：sizeCat 必须已正确初始化

**副作用**：无

**核心实现逻辑**：
直接返回 `sizeCat.minDim`

**边界情况**：如果 sizeCat 为 null，会抛出 NullPointerException

### maxWidth()

**可见性**：public

**是否覆写**：否

**方法职责**：返回房间的最大宽度

**参数**：无

**返回值**：int，基于当前 sizeCat 的 maxDim 值

**前置条件**：sizeCat 必须已正确初始化

**副作用**：无

**核心实现逻辑**：
直接返回 `sizeCat.maxDim`

**边界情况**：如果 sizeCat 为 null，会抛出 NullPointerException

### minHeight()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：返回房间的最小高度

**参数**：无

**返回值**：int，基于当前 sizeCat 的 minDim 值

**前置条件**：sizeCat 必须已正确初始化

**副作用**：无

**核心实现逻辑**：
直接返回 `sizeCat.minDim`

**边界情况**：如果 sizeCat 为 null，会抛出 NullPointerException

### maxHeight()

**可见性**：public

**是否覆写**：否

**方法职责**：返回房间的最大高度

**参数**：无

**返回值**：int，基于当前 sizeCat 的 maxDim 值

**前置条件**：sizeCat 必须已正确初始化

**副作用**：无

**核心实现逻辑**：
直接返回 `sizeCat.maxDim`

**边界情况**：如果 sizeCat 为 null，会抛出 NullPointerException

### sizeFactor()

**可见性**：public

**是否覆写**：否

**方法职责**：返回房间的尺寸因子，用于各种计数和权重计算

**参数**：无

**返回值**：int，基于当前 sizeCat 的 roomValue

**前置条件**：sizeCat 必须已正确初始化

**副作用**：无

**核心实现逻辑**：
直接返回 `sizeCat.roomValue`

**边界情况**：如果 sizeCat 为 null，会抛出 NullPointerException

### mobSpawnWeight()

**可见性**：public

**是否覆写**：否

**方法职责**：返回房间的怪物生成权重

**参数**：无

**返回值**：int，入口房间返回1，其他房间返回 sizeFactor()

**前置条件**：sizeCat 必须已正确初始化（非入口房间）

**副作用**：无

**核心实现逻辑**：
检查是否为入口房间，如果是则返回1（避免入口房间因尺寸大而生成过多怪物），否则返回 sizeFactor()

**边界情况**：入口房间不受尺寸影响，始终返回1

### connectionWeight()

**可见性**：public

**是否覆写**：否

**方法职责**：返回房间的连接权重

**参数**：无

**返回值**：int，sizeFactor() 的平方值

**前置条件**：sizeCat 必须已正确初始化

**副作用**：无

**核心实现逻辑**：
返回 `sizeFactor() * sizeFactor()`

**边界情况**：如果 sizeCat 为 null，会抛出 NullPointerException

### canMerge(Level l, Room other, Point p, int mergeTerrain)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：检查是否可以与另一个房间合并

**参数**：
- `l` (Level)：当前关卡
- `other` (Room)：要合并的另一个房间
- `p` (Point)：合并点
- `mergeTerrain` (int)：合并后的地形类型

**返回值**：boolean，如果可以合并返回 true

**前置条件**：pointInside(p, 1) 返回的点必须在关卡范围内

**副作用**：无

**核心实现逻辑**：
1. 计算合并点内侧一个格子的位置
2. 转换为关卡单元格索引
3. 检查该位置的地形是否不是固体（SOLID 标志未设置）

**边界情况**：如果 pointInside 返回的点超出关卡范围，l.pointToCell() 可能返回无效索引

### createRoom()

**可见性**：public static

**是否覆写**：否

**方法职责**：工厂方法，根据当前地牢深度创建随机的标准房间实例

**参数**：无

**返回值**：StandardRoom，新创建的房间实例

**前置条件**：Dungeon.depth 必须在 1-26 范围内，chances 数组必须已初始化

**副作用**：无

**核心实现逻辑**：
1. 使用 Dungeon.depth 作为索引获取对应深度的概率数组
2. 使用 Random.chances() 选择房间类型索引
3. 使用 Reflection.newInstance() 创建对应的房间实例

**边界情况**：
- 如果 chances[Dungeon.depth] 为 null，会抛出 NullPointerException
- 如果 Random.chances() 返回 -1（所有概率为0），会抛出 IndexOutOfBoundsException

## 8. 对外暴露能力

### 显式 API
- `sizeCatProbs()` - 允许子类自定义尺寸概率
- `setSizeCat()` 系列方法 - 允许外部代码控制尺寸设置
- `sizeFactor()`, `mobSpawnWeight()`, `connectionWeight()` - 提供权重计算
- `createRoom()` - 公共工厂方法用于创建房间

### 内部辅助方法
- `canMerge()` - 虽然 public，但主要用于内部房间合并逻辑
- 尺寸范围方法（minWidth/maxWidth 等）- 继承自父类，主要用于内部布局

### 扩展入口
- 子类可以通过覆写 `sizeCatProbs()` 来启用不同的尺寸类别
- 子类可以覆写 `paint()` 方法实现具体的房间绘制逻辑
- 子类可以覆写各种放置点过滤方法来自定义房间内容生成

## 9. 运行机制与调用链

### 创建时机
- 在地牢生成过程中，当需要添加标准房间时调用 createRoom()
- 每个具体房间实例在构造时自动初始化尺寸类别

### 调用者
- LevelBuilder 使用 createRoom() 创建房间
- Room 类的连接和合并算法调用相关方法
- 关卡生成器调用权重计算方法进行平衡

### 被调用者
- 调用父类 Room 的方法进行基本操作
- 调用 Dungeon 获取当前深度信息
- 调用 Random 进行随机选择
- 调用 Reflection 进行动态实例化

### 系统流程位置
- 处于地牢生成流程的房间创建和配置阶段
- 在房间连接和布局调整阶段提供合并和权重信息

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无直接引用的消息键。

### 依赖的资源
无直接依赖的纹理、图标或音效资源。

### 中文翻译来源
项目内未找到官方对应译名。"StandardRoom" 在上下文中可理解为"标准房间"，但由于 levels_zh.properties 文件中没有对应的官方翻译，保留英文名称。

## 11. 使用示例

### 基本用法
```java
// 创建一个随机的标准房间实例
StandardRoom room = StandardRoom.createRoom();

// 获取房间的尺寸因子
int factor = room.sizeFactor();

// 检查房间是否可以与另一个房间合并
if (room.canMerge(level, otherRoom, mergePoint, Terrain.EMPTY)) {
    // 执行合并操作
}
```

### 扩展示例
```java
// 自定义房间类，启用更大的尺寸
public class LargeLibraryRoom extends StandardRoom {
    @Override
    public float[] sizeCatProbs() {
        // 50% 几率 NORMAL，50% 几率 LARGE
        return new float[]{0.5f, 0.5f, 0};
    }
    
    @Override
    public void paint(Level level) {
        // 实现具体的绘制逻辑
    }
}
```

## 12. 开发注意事项

### 状态依赖
- sizeCat 字段在实例化后就固定不变，所有相关方法都依赖于此状态
- 如果 sizeCat 初始化失败（为 null），大部分方法会抛出异常

### 生命周期耦合
- 初始化块中的 setSizeCat() 调用必须在父类构造完成后执行
- 静态字段 rooms 和 chances 在类加载时初始化，依赖于所有子类的存在

### 常见陷阱
- 覆写 sizeCatProbs() 时必须返回长度为3的数组，否则 setSizeCat() 会失败
- createRoom() 依赖于 Dungeon.depth 的有效性，测试时需要注意
- 静态字段的初始化顺序很重要，rooms 列表中的类必须已经定义

## 13. 修改建议与扩展点

### 适合扩展的位置
- sizeCatProbs() 方法是主要的扩展点，用于控制房间尺寸
- paint() 方法必须被子类实现以提供具体的房间内容
- 各种放置点过滤方法可以被覆写以自定义房间生成规则

### 不建议修改的位置
- 静态的 rooms 和 chances 数组结构复杂，修改容易出错
- 尺寸类别枚举的数值应该保持现有比例关系
- createRoom() 的实现逻辑不宜修改，除非完全重构房间系统

### 重构建议
- 静态字段的初始化可以考虑使用更清晰的数据结构
- 概率数组的硬编码可以提取到配置文件中
- 可以考虑将尺寸类别系统提取为独立的组件

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（确认无官方翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点