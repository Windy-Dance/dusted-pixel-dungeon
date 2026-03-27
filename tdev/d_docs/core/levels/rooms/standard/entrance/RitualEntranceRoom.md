# RitualEntranceRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\entrance\RitualEntranceRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance |
| **文件类型** | class |
| **继承关系** | extends RitualRoom |
| **代码行数** | 48 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
RitualEntranceRoom 类负责创建和绘制具有仪式布局的入口房间。这类房间包含复杂的仪式装饰（雕像、余烬、基座），并在中心基座位置设置通向上一层的入口，替代了普通仪式房间的战利品物品。

### 系统定位
作为 RitualRoom 的特殊变体，RitualEntranceRoom 在最深的地牢关卡（深度21+）中出现，为入口房间提供带有复杂仪式装饰的布局样式。

### 不负责什么
- 不负责处理玩家与入口的交互逻辑（由 LevelTransition 系统处理）
- 不负责新手引导页面的放置（仅在深度1-2的简单入口房间中放置）
- 不负责仪式装饰的基础生成逻辑（由父类处理）

## 3. 结构总览

### 主要成员概览
- 无额外字段或常量
- 覆写父类的大小类别概率、入口标记和战利品放置方法

### 主要逻辑块概览
- 大小类别概率设置（normal:large:giant = 0:1:0，仅生成large大小）
- 入口标记设置（isEntrance 返回 true）
- 中心入口放置（替代战利品物品）

### 生命周期/调用时机
- 在 LevelGenerator 创建关卡时通过 EntranceRoom.createEntrance() 创建
- 在 Level.paint() 过程中调用 paint() 方法进行绘制

## 4. 继承与协作关系

### 父类提供的能力
- `RitualRoom` 提供的仪式布局系统（patch系统、雕像排列、余烬和基座）
- `PatchRoom` 提供的补丁系统支持
- `StandardRoom` 提供的基础房间管理
- `Room` 提供的空间和连接逻辑

### 覆写的方法
- `sizeCatProbs()`: 设置大小类别概率为 [0, 1, 0]
- `isEntrance()`: 返回 true 标识为入口房间
- `placeloot()`: 放置入口而非战利品物品

### 实现的接口契约
- 继承自 `Room` 的 `paint(Level level)` 抽象方法

### 依赖的关键类
- `Level`: 关卡数据结构
- `Terrain`: 地形类型定义（REGION_DECO, STATUE, EMBERS, PEDESTAL, ENTRANCE）
- `Painter`: 地形绘制工具
- `LevelTransition`: 关卡过渡点管理
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
RitualEntranceRoom 使用默认构造器，没有显式的构造方法。继承自父类的构造逻辑。

### 初始化块
无静态或实例初始化块。

### 初始化注意事项
- 强制只生成 large 大小的房间（sizeCatProbs 返回 [0, 1, 0]）
- 入口房间出现在深度21及以上的关卡中（根据 EntranceRoom.chances 配置）
- 仪式房间需要最小尺寸9x9才能正确生成复杂的仪式装饰

## 7. 方法详解（必须覆盖全部方法）

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：定义仪式入口房间的大小类别概率分布

**参数**：无

**返回值**：float[]，返回 [0, 1, 0] 表示 normal:large:giant = 0:1:0

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定的概率数组，强制仪式入口房间只生成 large 大小，确保有足够的空间容纳复杂的仪式装饰布局。

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

### placeloot()

**可见性**：protected

**是否覆写**：是，覆写自 RitualRoom

**方法职责**：在仪式中心基座位置放置入口而非战利品物品

**参数**：
- `level` (Level)：当前关卡
- `p` (Point)：中心基座位置的坐标

**返回值**：void

**前置条件**：p 参数有效

**副作用**：
- 修改 level.map 数组中的地形数据
- 向 level.transitions 添加关卡过渡点

**核心实现逻辑**：
1. 将指定位置设置为 Terrain.ENTRANCE（普通入口地形）
2. 添加 REGULAR_ENTRANCE 类型的 LevelTransition

**边界情况**：无

## 8. 对外暴露能力

### 显式 API
- 继承的所有公共方法

### 内部辅助方法
- `placeloot()`: 虽然 protected，但主要由父类内部调用

### 扩展入口
- 可以通过继承 RitualEntranceRoom 并覆写 placeloot() 等方法来自定义行为

## 9. 运行机制与调用链

### 创建时机
- 在 LevelGenerator.generate() 方法中，当深度为21+且随机选择到 RitualEntranceRoom 类型时创建

### 调用者
- EntranceRoom.createEntrance(): 通过反射调用构造器
- Level.paint(): 调用 paint()

### 被调用者
- RitualRoom.paint(): 父类绘制逻辑
- Painter.set()/fill(): 设置地形
- Level.pointToCell(): 坐标转换
- LevelTransition 构造器: 创建过渡点

### 系统流程位置
1. EntranceRoom.createEntrance() 创建 RitualEntranceRoom 实例
2. LevelGenerator 将房间加入关卡并建立连接
3. Level.paint() 调用 paint() 方法
4. paint() 执行仪式装饰绘制，调用 placeloot() 放置入口

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| levels.level.entrance_desc | 通向上一层的楼梯。 | 入口地形的描述文本 |

### 依赖的资源
- 无直接的纹理/图标/音效依赖
- 间接依赖 Terrain.ENTRANCE、Terrain.STATUE、Terrain.EMBERS、Terrain.PEDESTAL 相关的视觉表现

### 中文翻译来源
来自 D:\Develop\Workspace\DustedPixelDungeon\core\src\main\assets\messages\levels\levels_zh.properties

## 11. 使用示例

### 基本用法
```java
// RitualEntranceRoom 通过 EntranceRoom.createEntrance() 自动创建
// 无需手动实例化
```

### 扩展示例
```java
// 自定义仪式入口房间
public class CustomRitualEntranceRoom extends RitualEntranceRoom {
    @Override
    protected void placeloot(Level level, Point p) {
        // 添加自定义逻辑，如使用特殊入口地形
        super.placeloot(level, p);
    }
}
```

## 12. 开发注意事项

### 状态依赖
- 依赖父类的仪式装饰生成逻辑
- 依赖父类的中心位置计算逻辑
- 依赖 Level.map 的当前状态

### 生命周期耦合
- paint() 方法必须在父类仪式绘制过程中调用
- 入口放置发生在特定的绘制阶段（在基座放置之后）

### 常见陷阱
- placeloot() 必须使用普通 ENTRANCE 而非特殊入口地形
- 仪式房间的最小尺寸要求必须满足（9x9）
- 修改入口放置逻辑可能影响仪式装饰的完整性

## 13. 修改建议与扩展点

### 适合扩展的位置
- 扩展 placeloot() 方法添加额外的逻辑
- 调整入口的视觉效果
- 修改入口周围的装饰（但保持中心位置不变）

### 不建议修改的位置
- 仪式装饰的基本结构（雕像、余烬、基座）
- 中心位置的计算逻辑
- ENTRANCE 地形的使用

### 重构建议
- 考虑将入口放置逻辑提取到可重用的方法中
- 可以添加配置选项来控制仪式入口房间的复杂度

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点