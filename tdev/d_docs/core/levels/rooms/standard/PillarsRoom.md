# PillarsRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\PillarsRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 106 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
PillarsRoom 类负责生成带有装饰性立柱的房间布局。它根据房间大小和随机因素决定生成2个或4个立柱，并计算立柱的位置和尺寸以确保对称性和美观性。

### 系统定位
作为标准房间（StandardRoom）的一种具体实现，PillarsRoom 在关卡生成过程中被创建，用于提供多样化的房间类型。它是房间生成系统中装饰性房间的一部分。

### 不负责什么
- 不负责管理房间内的敌人生成
- 不负责处理房间连接逻辑
- 不负责物品放置逻辑
- 不负责房间门的特殊类型设置（除常规门外）

## 3. 结构总览

### 主要成员概览
- 无额外字段（继承自父类的字段包括 sizeCat 等）

### 主要逻辑块概览
- 尺寸约束逻辑：确保最小尺寸为7x7
- 尺寸分类概率：定义 NORMAL/LARGE/GIANT 的出现概率为 9:3:1
- 立柱生成逻辑：根据房间尺寸和随机因素选择2柱或4柱布局
- 立柱位置计算：复杂的几何计算确保立柱对称分布

### 生命周期/调用时机
- 房间实例化时自动设置尺寸分类
- 关卡绘制阶段调用 paint() 方法生成实际地形

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
- minWidth()：返回 Math.max(super.minWidth(), 7)
- minHeight()：返回 Math.max(super.minHeight(), 7)  
- sizeCatProbs()：返回 new float[]{9, 3, 1}
- paint(Level level)：实现立柱房间的具体绘制逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 间接实现）
- Bundlable 接口（通过 Room 间接实现）

### 依赖的关键类
- Level：关卡数据结构
- Terrain：地形类型定义
- Painter：关卡绘制工具
- Random：随机数生成
- Door：门类型定义

### 使用者
- StandardRoom.createRoom() 静态工厂方法
- LevelGenerator：关卡生成器在构建房间布局时

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

**方法职责**：确保 PillarsRoom 的最小宽度至少为7格

**参数**：无

**返回值**：int，返回父类 minWidth() 和 7 的较大值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(super.minWidth(), 7);
```

**边界情况**：当父类 minWidth() 返回值小于7时，强制返回7

### minHeight()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：确保 PillarsRoom 的最小高度至少为7格

**参数**：无

**返回值**：int，返回父类 minHeight() 和 7 的较大值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(super.minHeight(), 7);
```

**边界情况**：当父类 minHeight() 返回值小于7时，强制返回7

### sizeCatProbs()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：定义不同尺寸分类的出现概率

**参数**：无

**返回值**：float[]，包含三个元素 [9, 3, 1] 分别对应 NORMAL/LARGE/GIANT

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return new float[]{9, 3, 1};
```

**边界情况**：概率数组长度必须与 SizeCategory 枚举值数量一致

### paint(Level level)
**可见性**：public

**是否覆写**：是，覆写自 Room 抽象方法

**方法职责**：在关卡中实际绘制 PillarsRoom 的地形，包括墙壁、空地和立柱

**参数**：
- `level` (Level)：要绘制到的关卡实例

**返回值**：void

**前置条件**：房间必须已经设置了有效的位置和尺寸

**副作用**：修改 level 对象的 map 数组，设置对应的 Terrain 值

**核心实现逻辑**：
1. 使用 Painter.fill() 填充整个房间区域为墙壁 (Terrain.WALL)
2. 使用 Painter.fill() 填充内部区域（距离边缘1格）为空地 (Terrain.EMPTY)  
3. 设置所有连接门为常规门类型 (Door.Type.REGULAR)
4. 根据房间最小维度和尺寸分类+随机因素决定生成2柱还是4柱
5. 计算立柱的位置和尺寸，确保对称分布
6. 使用 Painter.fill() 在计算出的位置绘制立柱（设置为 Terrain.WALL）

**边界情况**：
- 当 minDim == 7 或 (NORMAL 尺寸且 Random.Int(2) == 0) 时生成2柱
- 其他情况生成4柱
- 立柱尺寸根据房间大小动态调整（更大的房间有更大的立柱或更多内边距）

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是 Room/StandardRoom 协议的一部分
- 没有额外的公共 API

### 内部辅助方法
- paint() 方法中的局部变量和计算逻辑不应被外部依赖

### 扩展入口
- 可以通过继承进一步自定义立柱生成逻辑
- 可以覆写其他 StandardRoom 方法来自定义行为

## 9. 运行机制与调用链

### 创建时机
- 在关卡生成过程中的房间创建阶段
- 通过 StandardRoom.createRoom() 静态工厂方法随机选择创建

### 调用者
- StandardRoom.createRoom()：通过反射创建实例
- LevelGenerator：在构建房间网络时调用 paint()

### 被调用者
- Painter.fill()：用于实际地形绘制
- Random.Int()/Random.IntRange()/Random.Float()：用于随机决策
- super.minWidth()/super.minHeight()：获取父类基础值

### 系统流程位置
1. 房间实例创建 → 2. 尺寸设置 → 3. 房间网络构建 → 4. paint() 调用 → 5. 地形最终确定

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
项目内未找到官方对应译名

### 依赖的资源
无特定资源依赖（仅使用标准 Terrain 常量）

### 中文翻译来源
在 levels_zh.properties 中未找到 PillarsRoom 相关翻译

## 11. 使用示例

### 基本用法
```java
// PillarsRoom 通常由关卡生成器自动创建，不建议手动实例化
PillarsRoom room = new PillarsRoom();
// 设置房间位置和尺寸
room.set(new Rect(10, 10, 20, 20));
// 在关卡中绘制
room.paint(level);
```

### 扩展示例
由于 PillarsRoom 主要用于装饰目的，通常不需要扩展。如需自定义立柱逻辑，可继承并覆写 paint() 方法。

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖于房间的 left/right/top/bottom 坐标已正确设置
- 依赖于 sizeCat 字段已通过父类初始化逻辑正确设置

### 生命周期耦合
- 必须在房间位置和尺寸确定后才能调用 paint()
- paint() 调用后房间地形即固定，不应再次调用

### 常见陷阱
- 立柱尺寸计算复杂，修改时需注意保持对称性和不超出房间边界
- 随机数使用影响房间外观的一致性，在相同种子下应产生相同结果

## 13. 修改建议与扩展点

### 适合扩展的位置
- paint() 方法中的立柱数量决策逻辑
- 立柱位置计算算法
- 立柱的 Terrain 类型（当前固定为 WALL）

### 不建议修改的位置
- 最小尺寸约束（7x7 是立柱布局的基本要求）
- 尺寸分类概率（影响游戏平衡性）

### 重构建议
当前实现较为清晰，但立柱位置计算逻辑可以提取为单独的私有方法以提高可读性。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点