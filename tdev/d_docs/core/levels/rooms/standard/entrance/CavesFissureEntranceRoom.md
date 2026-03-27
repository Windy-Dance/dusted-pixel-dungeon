# CavesFissureEntranceRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\entrance\CavesFissureEntranceRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance |
| **文件类型** | class |
| **继承关系** | extends CavesFissureRoom |
| **代码行数** | 64 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
CavesFissureEntranceRoom 类负责创建和绘制具有裂缝特征的洞穴入口房间。这类房间基于 CavesFissureRoom 的裂缝布局，并在其中设置通向上一层的入口。

### 系统定位
作为 CavesFissureRoom 的特殊变体，CavesFissureEntranceRoom 在较深的地牢关卡（深度6-10）中出现，为入口房间提供带有裂缝的复杂布局样式。

### 不负责什么
- 不负责处理玩家与入口的交互逻辑（由 LevelTransition 系统处理）
- 不负责新手引导页面的放置（仅在深度1-2的简单入口房间中放置）
- 不负责房间的基础裂缝生成逻辑（由父类处理）

## 3. 结构总览

### 主要成员概览
- 无额外字段或常量
- 覆写父类的大小类别概率、入口标记和绘制方法

### 主要逻辑块概览
- 大小类别概率设置（normal:large:giant = 3:1:0）
- 入口标记设置（isEntrance 返回 true）
- 入口位置选择（避开 CHASM 和 EMPTY_SP 地形）
- 入口相邻裂缝地形清理为空地
- 入口地形和过渡点设置

### 生命周期/调用时机
- 在 LevelGenerator 创建关卡时通过 EntranceRoom.createEntrance() 创建
- 在 Level.paint() 过程中调用 paint() 方法进行绘制

## 4. 继承与协作关系

### 父类提供的能力
- `CavesFissureRoom` 提供的裂缝生成逻辑
- `StandardRoom` 提供的基础房间管理
- `Room` 提供的空间和连接逻辑

### 覆写的方法
- `sizeCatProbs()`: 设置大小类别概率为 [3, 1, 0]
- `isEntrance()`: 返回 true 标识为入口房间
- `paint()`: 实现裂缝入口房间的具体绘制逻辑

### 实现的接口契约
- 继承自 `Room` 的 `paint(Level level)` 抽象方法

### 依赖的关键类
- `Level`: 关卡数据结构
- `Terrain`: 地形类型定义（CHASM, EMPTY_SP, ENTRANCE）
- `Painter`: 地形绘制工具
- `LevelTransition`: 关卡过渡点管理
- `PathFinder`: 邻域遍历工具

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
CavesFissureEntranceRoom 使用默认构造器，没有显式的构造方法。继承自父类的构造逻辑。

### 初始化块
无静态或实例初始化块。

### 初始化注意事项
- 依赖父类 CavesFissureRoom 的裂缝生成逻辑
- 入口房间出现在深度6-10的关卡中（根据 EntranceRoom.chances 配置）

## 7. 方法详解（必须覆盖全部方法）

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：定义裂缝入口房间的大小类别概率分布

**参数**：无

**返回值**：float[]，返回 [3, 1, 0] 表示 normal:large:giant = 3:1:0

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定的概率数组，使得裂缝入口房间主要生成 normal 大小，较少生成 large 大小，从不生成 giant 大小。

**边界情况**：giant 大小被完全禁用（概率为0）。

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

**是否覆写**：是，覆写自 CavesFissureRoom

**方法职责**：绘制裂缝入口房间的具体实现

**参数**：
- `level` (Level)：要绘制的关卡

**返回值**：void

**前置条件**：房间已正确初始化，level 参数有效

**副作用**：
- 修改 level.map 数组中的地形数据
- 向 level.transitions 添加关卡过渡点

**核心实现逻辑**：
1. 调用 super.paint(level) 执行父类的裂缝绘制逻辑
2. 随机选择一个内部位置作为入口点，避开以下地形：
   - Terrain.CHASM（裂缝）
   - Terrain.EMPTY_SP（特殊空地）
   - 怪物位置
3. 检查入口的4邻域，如果发现 CHASM 地形，则将其转换为 EMPTY
4. 设置入口地形为 Terrain.ENTRANCE
5. 添加 REGULAR_ENTRANCE 类型的 LevelTransition

**边界情况**：
- 入口位置选择确保不会放置在危险地形上
- 相邻裂缝地形被清理以确保玩家安全移动到入口

## 8. 对外暴露能力

### 显式 API
- 继承的所有公共方法

### 内部辅助方法
- `paint()`: 虽然 public，但主要由系统内部调用

### 扩展入口
- 可以通过继承 CavesFissureEntranceRoom 并覆写 paint() 等方法来自定义行为

## 9. 运行机制与调用链

### 创建时机
- 在 LevelGenerator.generate() 方法中，当深度为6-10且随机选择到 CavesFissureEntranceRoom 类型时创建

### 调用者
- EntranceRoom.createEntrance(): 通过反射调用构造器
- Level.paint(): 调用 paint()

### 被调用者
- CavesFissureRoom.paint(): 父类绘制逻辑
- Painter.set(): 设置地形
- Level.pointToCell(): 坐标转换
- Level.findMob(): 检查怪物位置
- PathFinder.NEIGHBOURS4: 4邻域遍历
- LevelTransition 构造器: 创建过渡点

### 系统流程位置
1. EntranceRoom.createEntrance() 创建 CavesFissureEntranceRoom 实例
2. LevelGenerator 将房间加入关卡并建立连接
3. Level.paint() 调用 paint() 方法
4. paint() 执行裂缝绘制和入口设置

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| levels.level.entrance_desc | 通向上一层的楼梯。 | 入口地形的描述文本 |
| levels.caveslevel.entrance_desc | 通向上一层的梯子。 | 洞穴关卡入口描述 |

### 依赖的资源
- 无直接的纹理/图标/音效依赖
- 间接依赖 Terrain.ENTRANCE、Terrain.CHASM 相关的视觉表现

### 中文翻译来源
来自 D:\Develop\Workspace\DustedPixelDungeon\core\src\main\assets\messages\levels\levels_zh.properties

## 11. 使用示例

### 基本用法
```java
// CavesFissureEntranceRoom 通过 EntranceRoom.createEntrance() 自动创建
// 无需手动实例化
```

### 扩展示例
```java
// 自定义裂缝入口房间
public class CustomCavesFissureEntranceRoom extends CavesFissureEntranceRoom {
    @Override
    public void paint(Level level) {
        super.paint(level);
        // 添加自定义逻辑
    }
}
```

## 12. 开发注意事项

### 状态依赖
- 依赖父类的裂缝生成逻辑
- 依赖 Level.map 的当前状态进行入口位置选择

### 生命周期耦合
- paint() 方法必须在父类裂缝绘制完成后调用
- 入口位置选择依赖于父类已经绘制的地形

### 常见陷阱
- 入口位置选择必须避开 CHASM 和 EMPTY_SP 地形
- 相邻裂缝地形清理确保玩家安全
- 修改裂缝处理逻辑可能影响入口安全性

## 13. 修改建议与扩展点

### 适合扩展的位置
- 调整 sizeCatProbs() 以改变房间大小分布
- 扩展 paint() 方法添加自定义装饰或特殊逻辑
- 修改入口位置选择条件以适应特殊需求

### 不建议修改的位置
- 入口相邻 CHASM 地形清理的核心逻辑
- 基础的入口位置安全检查
- CHASM 和 EMPTY_SP 地形的避让逻辑

### 重构建议
- 考虑将入口安全检查逻辑提取到可重用的方法中
- 可以添加配置选项来控制裂缝入口房间的复杂度

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点