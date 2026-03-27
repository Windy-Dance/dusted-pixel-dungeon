# CellBlockEntranceRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\entrance\CellBlockEntranceRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance |
| **文件类型** | class |
| **继承关系** | extends CellBlockRoom |
| **代码行数** | 71 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
CellBlockEntranceRoom 类负责创建和绘制具有牢房区块布局的入口房间。这类房间将内部空间划分为多个小牢房，并在其中一个特殊空地（EMPTY_SP）位置设置通向上一层的入口。

### 系统定位
作为 CellBlockRoom 的特殊变体，CellBlockEntranceRoom 在中等深度的地牢关卡（深度11-15）中出现，为入口房间提供监狱牢房风格的复杂布局。

### 不负责什么
- 不负责处理玩家与入口的交互逻辑（由 LevelTransition 系统处理）
- 不负责新手引导页面的放置（仅在深度1-2的简单入口房间中放置）
- 不负责牢房区块的基础生成逻辑（由父类处理）

## 3. 结构总览

### 主要成员概览
- 无额外字段或常量
- 覆写父类的大小类别概率、入口标记和绘制方法

### 主要逻辑块概览
- 大小类别概率设置（normal:large:giant = 0:1:0，仅生成large大小）
- 入口标记设置（isEntrance 返回 true）
- 入口位置选择（在 EMPTY_SP 地形中寻找合适位置）
- 入口周围门地形检查（避免靠近 DOOR 地形）
- 特殊入口地形设置（Terrain.ENTRANCE_SP）

### 生命周期/调用时机
- 在 LevelGenerator 创建关卡时通过 EntranceRoom.createEntrance() 创建
- 在 Level.paint() 过程中调用 paint() 方法进行绘制

## 4. 继承与协作关系

### 父类提供的能力
- `CellBlockRoom` 提供的牢房区块生成逻辑（网格布局、随机装饰房间）
- `StandardRoom` 提供的基础房间管理
- `Room` 提供的空间和连接逻辑

### 覆写的方法
- `sizeCatProbs()`: 设置大小类别概率为 [0, 1, 0]
- `isEntrance()`: 返回 true 标识为入口房间
- `paint()`: 实现牢房区块入口房间的具体绘制逻辑

### 实现的接口契约
- 继承自 `Room` 的 `paint(Level level)` 抽象方法

### 依赖的关键类
- `Level`: 关卡数据结构
- `Terrain`: 地形类型定义（EMPTY_SP, DOOR, ENTRANCE_SP）
- `Painter`: 地形绘制工具
- `LevelTransition`: 关卡过渡点管理
- `PathFinder`: 邻域遍历工具
- `Point`: 坐标处理

### 使用者
- `EntranceRoom.createEntrance()`: 通过反射创建实例
- `Level.paint()`: 调用 paint() 方法

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无静态常量 | - | - | - |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| 无实例字段 | - | - | - |

## 6. 构造与初始化机制

### 构造器
CellBlockEntranceRoom 使用默认构造器，没有显式的构造方法。继承自父类的构造逻辑。

### 初始化块
无静态或实例初始化块。

### 初始化注意事项
- 强制只生成 large 大小的房间（sizeCatProbs 返回 [0, 1, 0]）
- 入口房间出现在深度11-15的关卡中（根据 EntranceRoom.chances 配置）

## 7. 方法详解（必须覆盖全部方法）

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：定义牢房区块入口房间的大小类别概率分布

**参数**：无

**返回值**：float[]，返回 [0, 1, 0] 表示 normal:large:giant = 0:1:0

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定的概率数组，强制牢房区块入口房间只生成 large 大小，确保有足够的空间容纳牢房网格布局。

**边界情况**：normal 和 giant 大小被完全禁用（概率为0）。

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

### paint()

**可见性**：public

**是否覆写**：是，覆写自 CellBlockRoom

**方法职责**：绘制牢房区块入口房间的具体实现

**参数**：
- `level` (Level)：要绘制的关卡

**返回值**：void

**前置条件**：房间已正确初始化，level 参数有效

**副作用**：
- 修改 level.map 数组中的地形数据
- 向 level.transitions 添加关卡过渡点

**核心实现逻辑**：
1. 调用 super.paint(level) 执行父类的牢房区块绘制逻辑
2. 循环随机选择内部位置（使用 random(3) 确保距离边缘足够远）
3. 检查选中的位置是否为 Terrain.EMPTY_SP（特殊空地，即开放的牢房）
4. 如果是 EMPTY_SP，进一步检查8邻域内是否有 Terrain.DOOR（门）
5. 如果8邻域内没有门，则将该位置设置为 Terrain.ENTRANCE_SP（特殊入口）
6. 添加 REGULAR_ENTRANCE 类型的 LevelTransition
7. 返回结束循环

**边界情况**：
- 无限循环直到找到合适的入口位置（理论上总是能找到）
- 入口必须位于 EMPTY_SP 地形上（开放的牢房）
- 入口不能靠近任何门（DOOR 地形）

## 8. 对外暴露能力

### 显式 API
- 继承的所有公共方法

### 内部辅助方法
- `paint()`: 虽然 public，但主要由系统内部调用

### 扩展入口
- 可以通过继承 CellBlockEntranceRoom 并覆写 paint() 等方法来自定义行为

## 9. 运行机制与调用链

### 创建时机
- 在 LevelGenerator.generate() 方法中，当深度为11-15且随机选择到 CellBlockEntranceRoom 类型时创建

### 调用者
- EntranceRoom.createEntrance(): 通过反射调用构造器
- Level.paint(): 调用 paint()

### 被调用者
- CellBlockRoom.paint(): 父类绘制逻辑
- Painter.set(): 设置地形
- Level.pointToCell(): 坐标转换
- PathFinder.NEIGHBOURS8: 8邻域遍历
- LevelTransition 构造器: 创建过渡点

### 系统流程位置
1. EntranceRoom.createEntrance() 创建 CellBlockEntranceRoom 实例
2. LevelGenerator 将房间加入关卡并建立连接
3. Level.paint() 调用 paint() 方法
4. paint() 执行牢房区块绘制和入口设置

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| levels.level.entrance_desc | 通向上一层的楼梯。 | 入口地形的描述文本 |

### 依赖的资源
- 无直接的纹理/图标/音效依赖
- 间接依赖 Terrain.ENTRANCE_SP、Terrain.EMPTY_SP、Terrain.DOOR 相关的视觉表现

### 中文翻译来源
来自 D:\Develop\Workspace\DustedPixelDungeon\core\src\main\assets\messages\levels\levels_zh.properties

## 11. 使用示例

### 基本用法
```java
// CellBlockEntranceRoom 通过 EntranceRoom.createEntrance() 自动创建
// 无需手动实例化
```

### 扩展示例
```java
// 自定义牢房区块入口房间
public class CustomCellBlockEntranceRoom extends CellBlockEntranceRoom {
    @Override
    public void paint(Level level) {
        super.paint(level);
        // 添加自定义逻辑，如特殊物品放置在入口附近
    }
}
```

## 12. 开发注意事项

### 状态依赖
- 依赖父类的牢房区块生成逻辑
- 依赖 Level.map 的当前状态进行入口位置选择
- 依赖父类保证至少有一个 EMPTY_SP 位置（因为是入口房间）

### 生命周期耦合
- paint() 方法必须在父类牢房区块绘制完成后调用
- 入口位置选择依赖于父类已经创建的牢房布局

### 常见陷阱
- 修改入口位置选择逻辑时要确保避开 DOOR 地形
- Terrain.ENTRANCE_SP 是特殊的入口地形类型，不同于普通的 ENTRANCE
- 无限循环设计假设总是能找到合适的入口位置

## 13. 修改建议与扩展点

### 适合扩展的位置
- 扩展 paint() 方法添加自定义装饰或特殊逻辑
- 修改入口位置选择条件以适应特殊需求
- 调整 DOOR 邻域检查的范围或逻辑

### 不建议修改的位置
- Terrain.ENTRANCE_SP 的使用（这是牢房区块入口的标准）
- 基础的 EMPTY_SP 位置要求
- DOOR 邻域安全检查逻辑

### 重构建议
- 考虑将入口位置选择逻辑提取到可重用的方法中
- 可以添加超时机制避免理论上的无限循环
- 可以优化随机位置选择算法提高效率

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点