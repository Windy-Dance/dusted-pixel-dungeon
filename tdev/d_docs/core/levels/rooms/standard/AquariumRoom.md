# AquariumRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\AquariumRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 80 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
AquariumRoom 实现了一个水族馆主题的房间，内部包含多层同心结构：外层为墙壁，向内依次为空地、特殊空地（EMPTY_SP）和中央水域。房间会在水域中生成食人鱼（Piranha）怪物，并确保物品和角色只能放置在非水域位置。

### 系统定位
作为 StandardRoom 的具体实现，AquariumRoom 提供了一种具有特定装饰和生物生成规则的标准房间类型，丰富了地牢的多样性和挑战性。

### 不负责什么
- 不处理房间与其他房间的连接逻辑（由父类处理）
- 不负责食人鱼的具体行为和AI（由 Piranha 类处理）
- 不管理房间的尺寸类别选择（由父类处理）

## 3. 结构总览

### 主要成员概览
- 无实例字段
- 无静态字段或常量

### 主要逻辑块概览
- 尺寸限制覆写（minWidth/minHeight）
- 尺寸概率配置（sizeCatProbs）
- 放置点过滤（canPlaceItem/canPlaceCharacter）
- 房间绘制逻辑（paint）

### 生命周期/调用时机
- 在地牢生成过程中通过 StandardRoom.createRoom() 创建
- paint() 方法在房间布局完成后被调用以填充地形和生成内容
- 放置点过滤方法在关卡生成器尝试放置物品或角色时被调用

## 4. 继承与协作关系

### 父类提供的能力
从 StandardRoom 继承：
- 尺寸类别系统（SizeCategory）
- 权重计算方法
- 基础的房间尺寸和连接逻辑
从 Room 继承：
- 空间几何操作
- 邻居和连接管理
- 随机点生成
- 抽象的 paint() 方法

### 覆写的方法
- `minWidth()` - 设置最小宽度为7
- `minHeight()` - 设置最小高度为7  
- `sizeCatProbs()` - 设置尺寸概率为 `{3, 1, 0}`
- `canPlaceItem()` - 禁止在水域放置物品
- `canPlaceCharacter()` - 禁止在水域放置角色
- `paint()` - 实现具体的房间绘制逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 继承）
- Bundlable 接口（通过 Room 继承）

### 依赖的关键类
- `Piranha` - 食人鱼怪物类，用于在水域中生成
- `Level` - 地牢关卡对象
- `Terrain` - 地形类型，特别是 WATER、WALL、EMPTY、EMPTY_SP
- `Painter` - 用于填充地形
- `Point` - 点坐标操作

### 使用者
- StandardRoom.createRoom() 工厂方法会创建此房间的实例
- 关卡生成器调用 paint() 方法来渲染房间
- 物品和角色放置系统调用放置点过滤方法

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无

## 6. 构造与初始化机制

### 构造器
使用默认的无参构造器，继承自 StandardRoom。

### 初始化块
无显式的初始化块，但继承了 StandardRoom 的 `{ setSizeCat(); }` 初始化块。

### 初始化注意事项
- 尺寸类别在 StandardRoom 构造时就已经确定
- 由于 sizeCatProbs() 返回 `{3, 1, 0}`，房间有 75% 概率为 NORMAL 尺寸，25% 概率为 LARGE 尺寸，不会生成 GIANT 尺寸

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
调用父类的 minWidth() 并与7取最大值，确保房间足够大以容纳水族馆结构。

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
调用父类的 minHeight() 并与7取最大值，确保房间足够大以容纳水族馆结构。

**边界情况**：如果父类返回的尺寸大于7，则使用父类的值

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：返回各尺寸类别的选择概率

**参数**：无

**返回值**：float[]，`{3, 1, 0}`

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定的概率数组，NORMAL 尺寸概率为3，LARGE 尺寸概率为1，GIANT 尺寸概率为0。

**边界情况**：永远不会生成 GIANT 尺寸的 AquariumRoom

### canPlaceItem(Point p, Level l)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：检查指定点是否可以放置物品

**参数**：
- `p` (Point)：要检查的点
- `l` (Level)：当前关卡

**返回值**：boolean，只有当父类允许且该点不是水域时返回 true

**前置条件**：点 p 必须在房间范围内

**副作用**：无

**核心实现逻辑**：
首先调用父类的 canPlaceItem() 确保点在房间内部，然后检查该点对应的地形是否不是 WATER。

**边界情况**：如果点超出关卡范围，l.pointToCell(p) 可能返回无效索引

### canPlaceCharacter(Point p, Level l)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：检查指定点是否可以放置角色

**参数**：
- `p` (Point)：要检查的点
- `l` (Level)：当前关卡

**返回值**：boolean，只有当父类允许且该点不是水域时返回 true

**前置条件**：点 p 必须在房间范围内

**副作用**：无

**核心实现逻辑**：
首先调用父类的 canPlaceCharacter() 确保点在房间内部，然后检查该点对应的地形是否不是 WATER。

**边界情况**：如果点超出关卡范围，l.pointToCell(p) 可能返回无效索引

### paint(Level level)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制房间的具体内容，包括地形填充和生物生成

**参数**：
- `level` (Level)：要绘制的关卡

**返回值**：void

**前置条件**：房间的位置和尺寸必须已确定

**副作用**：
- 修改 level.map 数组中的地形
- 向 level.mobs 列表添加食人鱼实例

**核心实现逻辑**：
1. 使用 Painter.fill() 填充四层同心结构：
   - 外层：WALL（墙壁）
   - 第二层：EMPTY（空地）
   - 第三层：EMPTY_SP（特殊空地）
   - 中央：WATER（水域）
2. 计算要生成的食人鱼数量：`(minDim - 4)/3`，结果为1-3只
3. 为每只食人鱼随机选择水域位置并添加到关卡中
4. 将所有连接的门设置为 REGULAR 类型

**边界情况**：
- 如果房间太小（小于7x7），同心结构可能无法正确形成
- 如果水域中没有足够的空位，食人鱼生成循环可能需要多次尝试

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是继承自父类的覆写，没有新增的公共API

### 内部辅助方法
- 所有方法都是为了内部房间生成逻辑服务

### 扩展入口
- 此类不提供扩展入口，因为它是具体的最终实现

## 9. 运行机制与调用链

### 创建时机
- 在地牢生成过程中，当 StandardRoom.createRoom() 随机选择到 AquariumRoom 时创建

### 调用者
- StandardRoom.createRoom() 创建实例
- LevelBuilder 调用 paint() 方法
- 关卡生成器调用 canPlaceItem()/canPlaceCharacter() 进行放置点验证

### 被调用者
- 调用父类 StandardRoom 和 Room 的方法
- 调用 Painter.fill() 填充地形
- 调用 Piranha.random() 创建食人鱼实例
- 调用 Level 相关方法进行位置和生物管理

### 系统流程位置
- 处于地牢生成的房间绘制阶段
- 在房间连接完成后、内容填充前执行

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无直接引用的消息键。

### 依赖的资源
- 食人鱼怪物的纹理和动画资源（通过 Piranha 类间接依赖）
- 水域、墙壁、空地等地形的视觉资源

### 中文翻译来源
项目内未找到官方对应译名。"AquariumRoom" 直译为"水族馆房间"，但由于 levels_zh.properties 文件中没有对应的官方翻译，保留英文名称。

## 11. 使用示例

### 基本用法
```java
// 通过工厂方法创建 AquariumRoom 实例
StandardRoom room = StandardRoom.createRoom(); // 可能返回 AquariumRoom

// 房间的绘制由关卡生成器自动调用
room.paint(level);
```

### 注意事项
由于 AquariumRoom 是通过反射动态创建的，通常不需要直接实例化。

## 12. 开发注意事项

### 状态依赖
- 依赖于父类 StandardRoom 的 sizeCat 初始化状态
- paint() 方法依赖于房间的位置和尺寸已正确设置

### 生命周期耦合
- 必须在房间连接完成后调用 paint()
- 食人鱼生成依赖于关卡的 mobs 列表可用

### 常见陷阱
- 最小尺寸限制为7x7，如果强制更小的尺寸会导致同心结构异常
- 食人鱼生成循环假设水域中有足够的空位，如果水域太小可能效率低下
- 放置点过滤方法依赖于地形已经正确设置

## 13. 修改建议与扩展点

### 适合扩展的位置
- 此类是具体的最终实现，一般不需要扩展

### 不建议修改的位置
- 同心结构的层数和顺序不应随意修改，会影响游戏平衡
- 食人鱼数量计算公式经过平衡设计，修改会影响难度

### 重构建议
- 可以考虑将同心结构的层数提取为常量以提高可读性
- 食人鱼生成逻辑可以提取到单独的方法中以提高可维护性

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（确认无官方翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点