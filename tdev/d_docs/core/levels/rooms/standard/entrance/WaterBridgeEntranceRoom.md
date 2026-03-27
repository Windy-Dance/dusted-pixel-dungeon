# WaterBridgeEntranceRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\entrance\WaterBridgeEntranceRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance |
| **文件类型** | class |
| **继承关系** | extends WaterBridgeRoom |
| **代码行数** | 93 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
WaterBridgeEntranceRoom 类负责创建和绘制具有水域桥梁布局的入口房间。这类房间包含水域区域和连接两侧的桥梁，并在安全位置设置通向上一层的入口，同时支持新手引导页面放置。

### 系统定位
作为 WaterBridgeRoom 的特殊变体，WaterBridgeEntranceRoom 在前几层的地牢关卡（深度1-5）中出现，为入口房间提供带有水域桥梁的布局样式。

### 不负责什么
- 不负责处理玩家与入口的交互逻辑（由 LevelTransition 系统处理）
- 不负责水域和桥梁的基础生成逻辑（由父类处理）

## 3. 结构总览

### 主要成员概览
- 无额外字段或常量
- 覆写父类的尺寸约束、入口标记、合并/陷阱放置规则和绘制方法

### 主要逻辑块概览
- 尺寸约束（最小7x7）
- 入口标记设置（isEntrance 返回 true）
- 深度相关限制（深度≤2禁止合并，深度=1禁止陷阱）
- 入口位置选择（避开 spaceRect 区域和怪物位置）
- 入口周围8邻域清理为空地
- 入口类型根据深度区分（SURFACE/REGULAR_ENTRANCE）
- 新手引导页面放置支持

### 生命周期/调用时机
- 在 LevelGenerator 创建关卡时通过 EntranceRoom.createEntrance() 创建
- 在 Level.paint() 过程中调用 paint() 方法进行绘制

## 4. 继承与协作关系

### 父类提供的能力
- `WaterBridgeRoom` 提供的水域桥梁生成逻辑（spaceRect, bridgeRect）
- `StandardBridgeRoom` 提供的桥梁布局系统
- `StandardRoom` 提供的基础房间管理
- `Room` 提供的空间和连接逻辑

### 覆写的方法
- `minWidth()`: 设置最小宽度为7
- `minHeight()`: 设置最小高度为7  
- `isEntrance()`: 返回 true 标识为入口房间
- `canMerge()`: 深度≤2时禁止合并
- `canPlaceTrap()`: 深度=1时禁止陷阱
- `paint()`: 实现水域桥梁入口房间的具体绘制逻辑

### 实现的接口契约
- 继承自 `Room` 的 `paint(Level level)` 抽象方法

### 依赖的关键类
- `Level`: 关卡数据结构
- `Terrain`: 地形类型定义（WATER, EMPTY, ENTRANCE）
- `Painter`: 地形绘制工具
- `LevelTransition`: 关卡过渡点管理
- `Dungeon`: 全局游戏状态（深度信息）
- `PathFinder`: 邻域遍历工具
- `EntranceRoom`: 静态方法 placeEarlyGuidePages()

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
WaterBridgeEntranceRoom 使用默认构造器，没有显式的构造方法。继承自父类的构造逻辑。

### 初始化块
无静态或实例初始化块。

### 初始化注意事项
- 强制最小尺寸为7x7以确保有足够的空间容纳水域桥梁布局
- 入口房间出现在深度1-5的关卡中（根据 EntranceRoom.chances 配置）
- 支持新手引导页面放置（调用 EntranceRoom.placeEarlyGuidePages()）

## 7. 方法详解（必须覆盖全部方法）

### minWidth()

**可见性**：public

**是否覆写**：是，覆写自 WaterBridgeRoom

**方法职责**：确保水域桥梁入口房间的最小宽度至少为7格

**参数**：无

**返回值**：int，返回 Math.max(7, super.minWidth())

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
调用父类的 minWidth() 方法，并与7取最大值，确保房间足够宽以容纳水域桥梁布局。

**边界情况**：当父类 minWidth() 返回小于7的值时，强制返回7。

### minHeight()

**可见性**：public

**是否覆写**：是，覆写自 WaterBridgeRoom

**方法职责**：确保水域桥梁入口房间的最小高度至少为7格

**参数**：无

**返回值**：int，返回 Math.max(7, super.minHeight())

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
调用父类的 minHeight() 方法，并与7取最大值，确保房间足够高。

**边界情况**：当父类 minHeight() 返回小于7的值时，强制返回7。

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

**方法职责**：控制水域桥梁入口房间与其他房间的合并行为

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

**方法职责**：控制陷阱在水域桥梁入口房间的放置

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

**是否覆写**：是，覆写自 WaterBridgeRoom

**方法职责**：绘制水域桥梁入口房间的具体实现

**参数**：
- `level` (Level)：要绘制的关卡

**返回值**：void

**前置条件**：房间已正确初始化，level 参数有效

**副作用**：
- 修改 level.map 数组中的地形数据
- 向 level.transitions 添加关卡过渡点
- 可能向 level 中添加物品（新手引导页面）

**核心实现逻辑**：
1. 调用 super.paint(level) 执行父类的水域桥梁绘制逻辑
2. 随机选择一个内部位置作为入口点（使用 random(2) 确保距离边缘足够远）
3. 检查选中的位置是否不在 spaceRect 区域内（即不在水域区域）且没有怪物
4. 将入口周围8邻域设置为空地（Terrain.EMPTY）
5. 设置入口地形为 Terrain.ENTRANCE
6. 根据深度添加相应的 LevelTransition（SURFACE 或 REGULAR_ENTRANCE）
7. 调用 EntranceRoom.placeEarlyGuidePages() 放置新手引导页面

**边界情况**：
- 入口位置选择确保不会放置在水域（WATER）区域
- 第1层使用 SURFACE 类型过渡，其他层使用 REGULAR_ENTRANCE
- 新手引导页面仅在第1-2层放置

## 8. 对外暴露能力

### 显式 API
- 继承的所有公共方法

### 内部辅助方法
- `paint()`: 虽然 public，但主要由系统内部调用

### 扩展入口
- 可以通过继承 WaterBridgeEntranceRoom 并覆写 paint() 等方法来自定义行为

## 9. 运行机制与调用链

### 创建时机
- 在 LevelGenerator.generate() 方法中，当深度为1-5且随机选择到 WaterBridgeEntranceRoom 类型时创建

### 调用者
- EntranceRoom.createEntrance(): 通过反射调用构造器
- Level.paint(): 调用 paint()

### 被调用者
- WaterBridgeRoom.paint(): 父类绘制逻辑
- Painter.set()/fill(): 设置地形
- Level.pointToCell(): 坐标转换
- Level.findMob(): 检查怪物位置
- PathFinder.NEIGHBOURS8: 8邻域遍历
- LevelTransition 构造器: 创建过渡点
- EntranceRoom.placeEarlyGuidePages(): 放置新手引导页面

### 系统流程位置
1. EntranceRoom.createEntrance() 创建 WaterBridgeEntranceRoom 实例
2. LevelGenerator 将房间加入关卡并建立连接
3. Level.paint() 调用 paint() 方法
4. paint() 执行水域桥梁绘制、入口设置和新手引导页面放置

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| levels.level.entrance_desc | 通向上一层的楼梯。 | 入口地形的描述文本 |
| levels.caveslevel.entrance_desc | 通向上一层的梯子。 | 洞穴关卡入口描述 |

### 依赖的资源
- 无直接的纹理/图标/音效依赖
- 间接依赖 Terrain.ENTRANCE、Terrain.WATER 相关的视觉表现

### 中文翻译来源
来自 D:\Develop\Workspace\DustedPixelDungeon\core\src\main\assets\messages\levels\levels_zh.properties

## 11. 使用示例

### 基本用法
```java
// WaterBridgeEntranceRoom 通过 EntranceRoom.createEntrance() 自动创建
// 无需手动实例化
```

### 扩展示例
```java
// 自定义水域桥梁入口房间
public class CustomWaterBridgeEntranceRoom extends WaterBridgeEntranceRoom {
    @Override
    public void paint(Level level) {
        super.paint(level);
        // 添加自定义逻辑，如调整入口周围的装饰
    }
}
```

## 12. 开发注意事项

### 状态依赖
- 严重依赖 Dungeon.depth 全局状态
- 依赖 SPDSettings 和 Document 系统的新手引导状态
- 依赖父类的 spaceRect 和 bridgeRect 字段来确定安全区域

### 生命周期耦合
- paint() 方法必须在父类水域桥梁绘制完成后调用
- 新手引导页面放置使用独立的随机数生成器以避免影响关卡生成

### 常见陷阱
- 入口位置选择必须避开 spaceRect（水域）区域
- 入口周围清理逻辑确保玩家可以正常移动到入口位置
- 深度相关的逻辑（合并、陷阱、过渡类型、新手引导）必须保持一致

## 13. 修改建议与扩展点

### 适合扩展的位置
- 扩展 paint() 方法添加自定义装饰或特殊逻辑
- 修改入口位置选择条件以适应特殊需求
- 调整水域桥梁的生成参数

### 不建议修改的位置
- spaceRect 区域避让的核心逻辑
- 入口周围8邻域清理为空地的核心逻辑
- 深度相关的安全检查逻辑

### 重构建议
- 考虑将入口位置选择和清理逻辑提取到可重用的方法中
- 可以添加配置选项来控制水域桥梁入口房间的复杂度

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点