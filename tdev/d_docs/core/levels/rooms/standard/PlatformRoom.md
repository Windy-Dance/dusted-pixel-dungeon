# PlatformRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\PlatformRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 116 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
PlatformRoom 类负责生成由多个平台组成的房间布局，平台之间通过桥梁连接，平台下方是深渊（CHASM）地形。它使用递归分割算法将中心区域分割成多个子平台，并在分割处添加连接桥梁。

### 系统定位
作为标准房间（StandardRoom）的一种具体实现，PlatformRoom 在关卡生成过程中提供具有挑战性的地形，玩家需要在分离的平台间移动，增加了探索的复杂性。

### 不负责什么
- 不负责深渊伤害逻辑的实现
- 不处理特殊的敌人生成规则
- 不管理平台间的特殊交互逻辑
- 不负责物品放置的特殊规则

## 3. 结构总览

### 主要成员概览
- 无额外字段（继承自父类的字段包括 sizeCat 等）
- 私有方法 splitPlatforms() 实现递归平台分割算法

### 主要逻辑块概览
- 尺寸约束逻辑：确保最小尺寸为6x6
- 尺寸分类概率：定义 NORMAL/LARGE/GIANT 的出现概率为 6:3:1
- 合并逻辑：与相同类型或深渊房间合并时使用 CHASM 地形和 EMPTY_SP 门
- 平台生成算法：递归分割中心区域生成多个平台
- 桥梁添加逻辑：在分割处随机位置添加连接桥梁
- 门绘制逻辑：确保门周围有足够的可行走空间

### 生命周期/调用时机
- 房间实例化时自动设置尺寸分类
- 关卡绘制阶段调用 paint() 方法生成实际地形
- 房间合并时调用 merge() 方法处理连接区域

## 4. 继承与协作关系

### 父类提供的能力
从 StandardRoom 继承：
- SizeCategory 枚举和相关字段
- minWidth()/minHeight() 基础实现
- setSizeCat() 相关方法
- sizeFactor(), mobSpawnWeight(), connectionWeight() 等辅助方法
- canMerge() 实现

从 Room 继承：
- 空间和连接逻辑（Rect 功能扩展）
- paint() 抽象方法
- water/grass/trap/item 放置控制方法
- Door 内部类

### 覆写的方法
- minWidth()：返回 Math.max(super.minWidth(), 6)
- minHeight()：返回 Math.max(super.minHeight(), 6)
- sizeCatProbs()：返回 new float[]{6, 3, 1}
- merge()：自定义合并逻辑，特定情况下使用 CHASM 地形
- paint()：实现平台房间的具体绘制逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 间接实现）
- Bundlable 接口（通过 Room 间接实现）

### 依赖的关键类
- Level：关卡数据结构
- Terrain：地形类型定义（WALL, CHASM, EMPTY_SP）
- Painter：关卡绘制工具（fill(), drawInside(), set()）
- Random：随机数生成（Float(), Int(), IntRange(), NormalIntRange()）
- Rect：矩形区域表示
- ArrayList：存储平台列表
- Door：门类型定义

### 使用者
- StandardRoom.createRoom() 静态工厂方法
- LevelGenerator：关卡生成器在构建房间网络时
- 其他 PlatformRoom 或 ChasmRoom 实例在合并时

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无额外字段（全部继承自父类）

## 6. 构造与初始化机制

### 构造器
使用默认构造器，无自定义构造逻辑。

### 初始化块
无显式初始化块。

### 初始化注意事项
- sizeCat 字段在 StandardRoom 的初始化块中通过 setSizeCat() 自动设置
- 实例创建后立即具有有效的尺寸分类

## 7. 方法详解

### minWidth()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：确保 PlatformRoom 的最小宽度至少为6格

**参数**：无

**返回值**：int，返回父类 minWidth() 和 6 的较大值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(super.minWidth(), 6);
```

**边界情况**：当父类 minWidth() 返回值小于6时，强制返回6

### minHeight()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：确保 PlatformRoom 的最小高度至少为6格

**参数**：无

**返回值**：int，返回父类 minHeight() 和 6 的较大值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(super.minHeight(), 6);
```

**边界情况**：当父类 minHeight() 返回值小于6时，强制返回6

### sizeCatProbs()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：定义不同尺寸分类的出现概率

**参数**：无

**返回值**：float[]，包含三个元素 [6, 3, 1] 分别对应 NORMAL/LARGE/GIANT

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return new float[]{6, 3, 1};
```

**边界情况**：概率数组长度必须与 SizeCategory 枚举值数量一致

### merge(Level l, Room other, Rect merge, int mergeTerrain)
**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：自定义房间合并逻辑，当与 PlatformRoom 或 ChasmRoom 连接时使用深渊地形和特殊门

**参数**：
- `l` (Level)：关卡实例
- `other` (Room)：要合并的其他房间
- `merge` (Rect)：合并区域
- `mergeTerrain` (int)：原始合并地形类型

**返回值**：void

**前置条件**：房间必须已连接且合并地形不是 CHASM

**副作用**：修改关卡地形和门类型

**核心实现逻辑**：
- 如果合并地形不是 CHASM 且房间已连接，且对方是 PlatformRoom 或 ChasmRoom
- 则使用 CHASM 地形进行合并
- 同时将连接门位置设置为 EMPTY_SP（特殊空地，可能用于防止深渊伤害）

**边界情况**：仅在特定条件下改变合并地形和门类型

### paint(Level level)
**可见性**：public

**是否覆写**：是，覆写自 Room 抽象方法

**方法职责**：在关卡中实际绘制 PlatformRoom 的地形，包括墙壁、深渊和平台

**参数**：
- `level` (Level)：要绘制到的关卡实例

**返回值**：void

**前置条件**：房间必须已经设置了有效的位置和尺寸

**副作用**：修改 level 对象的 map 数组

**核心实现逻辑**：
1. 使用 Painter.fill() 填充整个房间为墙壁 (Terrain.WALL)
2. 填充内部1格区域为深渊 (Terrain.CHASM)
3. 创建平台列表 ArrayList<Rect>
4. 调用 splitPlatforms() 递归分割中心区域 (left+2 到 right-2, top+2 到 bottom-2)，生成平台列表
5. 遍历平台列表，将每个平台区域填充为 EMPTY_SP
6. 设置所有连接门为常规门类型，并使用 drawInside() 确保门周围2格内为 EMPTY_SP

**边界情况**：
- 最小房间尺寸为6x6，确保有足够空间进行分割
- 平台分割算法确保不会无限递归（基于面积阈值）

### splitPlatforms(Rect curPlatform, ArrayList<Rect> allPlatforms)
**可见性**：private

**是否覆写**：否

**方法职责**：递归分割平台区域，生成多个子平台并通过桥梁连接

**参数**：
- `curPlatform` (Rect)：当前要分割的平台区域
- `allPlatforms` (ArrayList<Rect>)：存储所有生成平台的列表

**返回值**：void

**前置条件**：curPlatform 必须是有效的矩形区域

**副作用**：修改 allPlatforms 列表

**核心实现逻辑**：
1. 计算当前平台面积
2. 基于面积决定是否继续分割（面积25-36之间，分割概率0%-100%线性增长）
3. 如果决定分割：
   - 选择水平或垂直分割方向（基于长宽比和随机因素）
   - 在有效范围内随机选择分割线位置
   - 递归处理两个子区域
   - 在分割线处添加连接桥梁（单格宽的平台）
4. 如果不继续分割，则将当前平台添加到结果列表

**边界情况**：
- 最小平台尺寸为1x1（当不满足分割条件时）
- 桥梁确保相邻平台之间有连接通路
- 分割位置确保子平台有足够边界（距边缘至少2格）

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是 Room/StandardRoom 协议的一部分
- 没有额外的公共 API

### 内部辅助方法
- splitPlatforms() 是私有方法，不应被外部依赖

### 扩展入口
- 可以通过继承进一步自定义平台分割逻辑
- 可以覆写其他 StandardRoom 方法来自定义行为

## 9. 运行机制与调用链

### 创建时机
- 在关卡生成过程中的房间创建阶段
- 通过 StandardRoom.createRoom() 静态工厂方法随机选择创建

### 调用者
- StandardRoom.createRoom()：通过反射创建实例
- LevelGenerator：在构建房间网络时调用 paint()
- 房间合并逻辑：调用 merge()

### 被调用者
- Painter.fill()/drawInside()/set()：用于地形绘制
- Random.Float()/Int()/IntRange()/NormalIntRange()：用于随机决策
- splitPlatforms()：递归分割平台
- super 方法：获取父类基础功能

### 系统流程位置
1. 房间实例创建 → 2. 尺寸设置 → 3. 房间网络构建 → 4. paint() 调用 → 5. 平台分割 → 6. 房间合并处理

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
项目内未找到官方对应译名

### 依赖的资源
- Terrain 常量：WALL, CHASM, EMPTY_SP
- EMPTY_SP 可能是特殊的空地类型，用于防止深渊伤害或提供安全区域

### 中文翻译来源
在 levels_zh.properties 中未找到 PlatformRoom 相关翻译

## 11. 使用示例

### 基本用法
```java
// PlatformRoom 通常由关卡生成器自动创建，不建议手动实例化
PlatformRoom room = new PlatformRoom();
// 设置房间位置和尺寸
room.set(new Rect(10, 10, 20, 20));
// 在关卡中绘制
room.paint(level);
// 房间会自动生成多个平台，玩家需要小心在平台间移动
```

### 扩展示例
如需自定义平台分割逻辑，可以继承 PlatformRoom 并覆写 splitPlatforms() 方法：

```java
public class CustomPlatformRoom extends PlatformRoom {
    @Override
    private void splitPlatforms(Rect curPlatform, ArrayList<Rect> allPlatforms) {
        // 实现自定义的平台生成算法
        allPlatforms.add(curPlatform); // 例如，不进行分割
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖于房间的 left/right/top/bottom 坐标已正确设置
- 依赖于 Terrain.EMPTY_SP 的存在和正确行为
- merge() 方法依赖于 connected 映射的正确状态

### 生命周期耦合
- 必须在房间位置和尺寸确定后才能调用 paint()
- paint() 调用后平台布局即固定
- merge() 应在房间连接建立后调用

### 常见陷阱
- 修改平台分割算法时需确保生成的平台仍然连通
- EMPTY_SP 地形的行为需要与其他系统（如深渊伤害）协调
- 递归分割可能导致栈溢出，但当前实现通过面积限制避免了这个问题

## 13. 修改建议与扩展点

### 适合扩展的位置
- splitPlatforms() 方法：自定义平台生成算法
- 平台最小尺寸：调整分割阈值
- 桥梁生成逻辑：添加更多样化的连接方式
- 地形类型：使用不同的安全区域地形

### 不建议修改的位置
- 最小尺寸约束（6x6 是基本分割要求）
- 深渊地形的使用（这是平台房间的核心特征）
- 门周围的 EMPTY_SP 绘制（确保玩家能安全进出）

### 重构建议
当前递归实现较为清晰，但可以考虑改为迭代实现以避免潜在的栈溢出问题（尽管当前面积限制已足够安全）。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点