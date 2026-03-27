# MinefieldRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/rooms/standard/MinefieldRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 98 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
MinefieldRoom 类负责创建一个雷区类型的房间，该房间内部随机分布着爆炸陷阱(ExplosiveTrap)，并伴随有余烬(EMBERS)装饰效果，形成危险的雷场环境。

### 系统定位
作为标准房间的一种具体实现，MinefieldRoom 在地牢关卡生成过程中被用于创建具有高危险性的雷区房间，为玩家提供充满挑战的导航体验。

### 不负责什么
- 不负责关卡的整体布局规划（由 Level 和 Room 相关类处理）
- 不负责地形绘制的具体实现（由 Painter 类处理）
- 不负责具体的怪物生成逻辑

## 3. 结构总览

### 主要成员概览
- 无实例字段
- 无静态常量

### 主要逻辑块概览
- 尺寸类别概率方法（sizeCatProbs）
- 合并检查方法（canMerge）
- 房间绘制方法（paint）

### 生命周期/调用时机
- 在关卡生成过程中，当需要创建雷区房间时被实例化
- paint() 方法在房间被实际绘制到关卡地图时调用

## 4. 继承与协作关系

### 父类提供的能力
- 继承了 StandardRoom 的所有公共和受保护方法
- 继承了 Room 基类的连接门管理、随机点生成等基础功能

### 覆写的方法
- sizeCatProbs()
- canMerge()
- paint()

### 实现的接口契约
- 无直接实现的接口

### 依赖的关键类
- TrapMechanism: 陷阱机制工具类
- Level: 关卡数据结构
- Terrain: 地形类型定义
- Painter: 地形绘制工具
- Room: 房间基类
- ExplosiveTrap: 爆炸陷阱类型
- PathFinder: 路径查找工具
- Point: 坐标表示
- Random: 随机数生成

### 使用者
- LevelGenerator: 在关卡生成过程中选择并实例化房间类型
- Room 类: 作为房间类型系统的一部分

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无

## 6. 构造与初始化机制

### 构造器
使用默认构造器，无自定义构造逻辑。

### 初始化块
无静态或实例初始化块。

### 初始化注意事项
该类完全依赖父类 StandardRoom 的初始化机制，自身不包含额外的初始化逻辑。

## 7. 方法详解

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：定义房间尺寸类别的概率分布

**参数**：无

**返回值**：float[]，包含三个元素的数组，分别对应小、中、大房间的概率权重

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回一个新的浮点数组 {4, 1, 0}，表示小房间的概率权重为4，中等房间为1，大房间为0（即不会生成大尺寸的雷区房间）。

**边界情况**：无

### canMerge()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：确定当前房间是否可以与其他房间合并

**参数**：
- `l` (Level)：当前关卡
- `other` (Room)：要合并的另一个房间
- `p` (Point)：合并位置
- `mergeTerrain` (int)：合并后的地形类型

**返回值**：boolean，如果可以合并则返回true

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
检查指定合并位置内部一点（偏移1个单位）的地形是否为空地(Terrain.EMPTY)，如果是则允许合并。

**边界情况**：当位置超出房间范围时可能抛出异常

### paint()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：将房间实际绘制到关卡地图上

**参数**：
- `level` (Level)：要绘制到的关卡

**返回值**：void

**前置条件**：房间的边界已经确定

**副作用**：
- 修改 level.map 数组中的地形数据
- 向 level.traps 集合中添加爆炸陷阱
- 设置连接门的类型

**核心实现逻辑**：
1. 使用 Painter.fill() 方法填充两层地形：外层墙壁(Terrain.WALL)和内层空地(Terrain.EMPTY)
2. 将所有连接门设置为常规门类型(Door.Type.REGULAR)
3. 根据房间面积计算地雷数量：`mines = round(sqrt(square()))`
4. 根据房间尺寸类别调整地雷数量：
   - 普通尺寸(NORMAL)：减少3个
   - 大尺寸(LARGE)：增加3个
   - 巨型尺寸(GIANT)：增加9个
5. 对于每个地雷：
   - 随机选择一个内部位置（确保该位置没有其他陷阱）
   - 在地雷周围随机位置放置余烬(EMBERS)装饰效果
   - 根据 TrapMechanism.revealHiddenTrapChance() 的概率决定陷阱是否可见：
     - 如果揭示累积值 >= 1，则设置为可见陷阱(Terrain.TRAP)
     - 否则设置为隐藏陷阱(Terrain.SECRET_TRAP)
   - 创建对应的 ExplosiveTrap 实例并添加到关卡中

**边界情况**：
- 当房间尺寸很小时，地雷数量可能很少甚至为负数（但会被 Math.round 处理）
- 余烬只会在空地地形上生成，不会覆盖现有陷阱或其他特殊地形

## 8. 对外暴露能力

### 显式 API
- 所有覆写的公共方法构成了该类的显式API

### 内部辅助方法
- 无内部辅助方法

### 扩展入口
- 可通过继承并覆写更多方法来扩展功能
- 可通过修改地雷生成逻辑来自定义房间内容

## 9. 运行机制与调用链

### 创建时机
- 在 LevelGenerator 选择房间类型时创建

### 调用者
- LevelGenerator: 在关卡生成流程中调用
- Room 类: 在房间验证和填充过程中调用各种方法

### 被调用者
- Painter.fill()/set(): 用于地形操作
- Random.Int()/random(): 用于随机选择
- PathFinder.NEIGHBOURS8: 用于获取相邻位置
- TrapMechanism.revealHiddenTrapChance(): 用于获取陷阱揭示概率
- Level.setTrap()/traps.get(): 用于陷阱管理
- Room.square()/connected.values(): 用于房间属性访问

### 系统流程位置
- 位于关卡生成流程的房间绘制阶段

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| levels.traps.explosivetrap.name | 爆炸陷阱 | 爆炸陷阱的显示名称 |
| levels.traps.explosivetrap.desc | 这个陷阱包含一些粉状炸药和一个触发机制。激活它会导致一定范围的爆炸。 | 爆炸陷阱的描述 |
| levels.level.embers_name | 余烬 | 余烬地形的显示名称 |
| levels.level.embers_desc | 地上散落着余烬。 | 余烬地形的描述 |

### 依赖的资源
- 爆炸陷阱(ExplosiveTrap)的相关资源
- 余烬(EMBERS)地形的纹理资源
- 隐藏陷阱(SECRET_TRAP)和可见陷阱(TRAP)的视觉效果资源

### 中文翻译来源
来自 levels_zh.properties 文件：
- levels.traps.explosivetrap.name = 爆炸陷阱
- levels.level.embers_name = 余烬

## 11. 使用示例

### 基本用法
```java
// 在关卡生成器中创建雷区房间
MinefieldRoom minefieldRoom = new MinefieldRoom();
// 设置房间边界
minefieldRoom.set(new Rect(left, top, right, bottom));
// 绘制到关卡
minefieldRoom.paint(level);
```

### 扩展示例
不适用，该类主要用于关卡生成，一般不需要用户直接扩展。

## 12. 开发注意事项

### 状态依赖
- 依赖于房间边界已正确设置
- 依赖于关卡的 map 数组已初始化
- 依赖于 TrapMechanism 类的全局状态

### 生命周期耦合
- 必须在关卡生成流程的正确阶段调用 paint() 方法
- 陷阱的创建和放置必须在关卡完全初始化后进行

### 常见陷阱
- 如果房间尺寸过小，可能导致地雷数量计算出现负数（但会被适当处理）
- 陷阱揭示概率依赖于 TrapMechanism 的全局状态，需要注意其影响范围
- 余烬生成逻辑可能会在某些情况下无法找到合适的空地位置

## 13. 修改建议与扩展点

### 适合扩展的位置
- 地雷数量的计算公式可以根据游戏平衡需求调整
- 余烬的生成密度和分布模式可以修改以改变视觉效果

### 不建议修改的位置
- sizeCatProbs() 返回的权重不应随意更改，会影响房间生成的平衡性
- 陷阱的核心生成逻辑不应移除，这是该房间类型的设计特色

### 重构建议
当前实现简洁明了，无需重构。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点