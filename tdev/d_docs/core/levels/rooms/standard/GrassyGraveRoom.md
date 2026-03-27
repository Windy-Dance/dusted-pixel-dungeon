# GrassyGraveRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\GrassyGraveRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 70 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
GrassyGraveRoom 实现了一个草地墓地主题的房间，内部包含草地（GRASS）地形和多个墓碑（TOMB）。房间会在墓地中随机放置物品堆，其中一个包含随机物品，其余包含金币。房间还包含特殊的合并逻辑，与其他草地或植物房间连接时会生成草地而非空地。

### 系统定位
作为 StandardRoom 的具体实现，GrassyGraveRoom 提供了一种具有特定装饰和物品生成规则的特殊房间类型，丰富了地牢的内容多样性。

### 不负责什么
- 不处理房间与其他房间的基本连接逻辑（由父类处理）
- 不管理具体的物品生成逻辑（由 Generator 类处理）
- 不负责墓碑的具体视觉效果（由 Heap.Type.TOMB 处理）

## 3. 结构总览

### 主要成员概览
- 无实例字段
- 无静态字段或常量

### 主要逻辑块概览
- 合并逻辑覆写（merge）
- 房间绘制逻辑（paint）
- 墓碑生成和物品放置

### 生命周期/调用时机
- 在地牢生成过程中通过 StandardRoom.createRoom() 创建
- paint() 方法在房间布局完成后被调用以填充地形和生成内容
- merge() 方法在房间连接时被调用以处理特殊合并情况

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
- `merge()` - 实现特殊的草地合并逻辑
- `paint()` - 实现草地墓地的绘制和物品生成逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 继承）
- Bundlable 接口（通过 Room 继承）

### 依赖的关键类
- `Level` - 地牢关卡对象
- `Terrain` - 地形类型（WALL, GRASS）
- `Painter` - 地形填充工具
- `Generator` - 随机物品生成器
- `Gold` - 金币物品类
- `Heap` - 物品堆类
- `Random` - 随机数生成
- `Rect` - 矩形区域操作

### 使用者
- StandardRoom.createRoom() 工厂方法会创建此房间的实例
- 关卡生成器调用 paint() 方法来渲染房间
- 房间连接算法调用 merge() 方法处理特殊合并

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
- 默认使用 StandardRoom 的 sizeCatProbs()，即 `{1, 0, 0}`，总是 NORMAL 尺寸

## 7. 方法详解

### merge(Level l, Room other, Rect merge, int mergeTerrain)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：处理与其他房间的特殊合并逻辑

**参数**：
- `l` (Level)：当前关卡
- `other` (Room)：要合并的另一个房间
- `merge` (Rect)：合并区域
- `mergeTerrain` (int)：合并后的地形类型

**返回值**：void

**前置条件**：合并区域必须有效

**副作用**：
- 修改 level.map 数组中的地形

**核心实现逻辑**：
- 如果合并地形是 EMPTY 且另一个房间是 GrassyGraveRoom 或 PlantsRoom：
  - 调用 super.merge() 将合并区域设置为 GRASS（草地）
- 否则，使用默认的合并逻辑

**边界情况**：
- 特殊处理确保草地房间之间的连接保持草地地形
- 保持与其他房间类型的正常合并行为

### paint(Level level)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制房间的具体内容，包括草地和墓碑生成

**参数**：
- `level` (Level)：要绘制的关卡

**返回值**：void

**前置条件**：房间的位置和尺寸必须已确定

**副作用**：
- 修改 level.map 数组中的地形
- 向 level 中添加物品堆

**核心实现逻辑**：
1. 填充外层为 WALL（墙壁）
2. 将所有连接的门设置为 REGULAR 类型
3. 填充内层（距离边缘1格）为 GRASS（草地）
4. 计算墓碑数量：`nGraves = Math.max(width()-2, height()-2) / 2`
5. 随机选择一个墓碑位置作为特殊墓碑（index）
6. 随机决定墓碑排列方向（shift = 0 或 1）
7. 根据房间的主要维度（宽或高）生成墓碑：
   - 如果宽度 > 高度：墓碑垂直排列
   - 否则：墓碑水平排列
8. 为每个墓碑创建物品堆：
   - 特殊墓碑（index 位置）：放置随机物品（Generator.random()）
   - 其他墓碑：放置随机数量的金币（new Gold().random()）
9. 所有物品堆的类型设置为 Heap.Type.TOMB（墓碑）

**边界情况**：
- 墓碑数量基于房间的较大维度计算
- 墓碑排列方向根据房间形状自动调整
- 至少有一个墓碑包含有用物品，其余为金币

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是继承自父类的覆写，没有新增的公共API

### 内部辅助方法
- 所有逻辑都在 paint() 和 merge() 方法内部实现

### 扩展入口
- 此类是具体的最终实现，一般不需要扩展

## 9. 运行机制与调用链

### 创建时机
- 在地牢生成过程中，当 StandardRoom.createRoom() 随机选择到 GrassyGraveRoom 时创建

### 调用者
- StandardRoom.createRoom() 创建实例
- LevelBuilder 调用 paint() 方法
- 房间连接算法调用 merge() 方法

### 被调用者
- 调用父类 StandardRoom 和 Room 的方法
- 调用 Painter.fill() 填充地形
- 调用 Generator.random() 生成随机物品
- 调用 Gold.random() 生成随机金币
- 调用 level.drop() 放置物品堆
- 调用 Random.Int() 生成随机数

### 系统流程位置
- 处于地牢生成的房间绘制阶段
- 在房间连接完成后、内容填充前执行
- merge() 方法在房间连接过程中调用

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无直接引用的消息键。

### 依赖的资源
- 草地（GRASS）地形的视觉资源
- 墓碑（TOMB）物品堆的视觉资源
- 各种物品和金币的视觉资源

### 中文翻译来源
项目内未找到官方对应译名。"GrassyGraveRoom" 直译为"草地墓地房间"，但由于 levels_zh.properties 文件中没有对应的官方翻译，保留英文名称。

## 11. 使用示例

### 基本用法
```java
// 通过工厂方法创建 GrassyGraveRoom 实例
StandardRoom room = StandardRoom.createRoom(); // 可能返回 GrassyGraveRoom

// 房间的绘制由关卡生成器自动调用
room.paint(level);
```

### 注意事项
由于 GrassyGraveRoom 是通过反射动态创建的，通常不需要直接实例化。

## 12. 开发注意事项

### 状态依赖
- 依赖于父类 StandardRoom 的 sizeCat 初始化状态
- paint() 方法依赖于房间的正确尺寸计算
- merge() 方法依赖于其他房间的类型检查

### 生命周期耦合
- 必须在房间连接完成后调用 paint()
- connected 集合用于正确设置门的类型
- merge() 方法在房间连接过程中调用

### 常见陷阱
- 墓碑数量计算基于内层尺寸（width()-2, height()-2）
- 墓碑排列方向根据房间形状自动选择，可能影响游戏体验
- 特殊墓碑保证至少有一个有用的物品，这是重要的游戏平衡设计

## 13. 修改建议与扩展点

### 适合扩展的位置
- 此类是具体的最终实现，一般不需要扩展

### 不建议修改的位置
- 墓碑数量计算公式经过平衡设计，修改会影响游戏难度
- 特殊墓碑机制确保玩家获得有用物品，不应移除
- 草地合并逻辑影响房间连贯性，不应随意修改

### 重构建议
- 可以考虑将常量值（如2）提取为命名常量以提高可读性
- 墓碑生成逻辑可以提取到单独的方法中以提高可维护性
- 物品类型选择可以配置化以支持不同的游戏模式

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（确认无官方翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点