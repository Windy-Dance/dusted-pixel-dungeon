# StripedRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\StripedRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 74 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
StripedRoom 类实现了一种条纹状装饰的房间布局，根据房间尺寸分类（正常或大型）采用不同的条纹模式：正常尺寸房间创建平行条纹，大型房间创建同心圆环状条纹。

### 系统定位
作为标准房间类型的变体，StripedRoom 在地牢生成过程中被随机选择创建，用于提供视觉上独特的装饰性房间。它属于纯装饰性房间类型，不包含物品或特殊功能。

### 不负责什么
- 不负责物品生成逻辑
- 不处理玩家交互逻辑
- 不影响游戏平衡或难度

## 3. 结构总览

### 主要成员概览
- 继承自 StandardRoom 的所有字段和方法
- sizeCatProbs() 方法覆盖
- merge(Level l, Room other, Rect merge, int mergeTerrain) 方法重写
- paint(Level level) 方法实现

### 主要逻辑块概览
- 尺寸分类概率设置逻辑（正常:大型 = 2:1）
- 房间合并特殊处理逻辑（同类型房间连接时使用特殊地形）
- 正常尺寸房间平行条纹生成逻辑
- 大型房间同心圆环条纹生成逻辑

### 生命周期/调用时机
- 在地牢生成阶段通过 StandardRoom.createRoom() 静态方法创建
- paint() 方法在 Level 生成过程中被调用以绘制房间地形
- merge() 方法在房间连接时被调用

## 4. 继承与协作关系

### 父类提供的能力
从 StandardRoom 继承：
- SizeCategory 枚举和相关方法
- sizeCat 字段和 setSizeCat() 方法
- sizeFactor(), mobSpawnWeight(), connectionWeight() 方法
- default maxWidth()/maxHeight() 实现

从 Room 继承：
- 空间和连接逻辑
- 抽象 paint() 方法
- merge() 方法默认实现
- 各种放置点检查方法

### 覆写的方法
- sizeCatProbs() - 重写以设置尺寸分类概率（正常2，大型1，巨型0）
- merge(Level l, Room other, Rect merge, int mergeTerrain) - 重写以处理同类型房间连接
- paint(Level level) - 重写以实现具体的条纹房间绘制逻辑

### 实现的接口契约
- 实现 Room 抽象类的所有抽象方法要求

### 依赖的关键类
- com.shatteredpixel.shatteredpixeldungeon.levels.Level
- com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
- com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
- com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
- com.watabou.utils.Random
- com.watabou.utils.Rect

### 使用者
- StandardRoom.createRoom() 静态工厂方法
- Level 生成系统

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无（所有字段均继承自父类）

## 6. 构造与初始化机制

### 构造器
使用默认构造器，无自定义构造逻辑。

### 初始化块
无自定义初始化块。

### 初始化注意事项
- 依赖父类 StandardRoom 的初始化逻辑
- sizeCat 字段在 StandardRoom 构造时通过 setSizeCat() 初始化
- sizeCatProbs() 方法影响尺寸分类的选择概率

## 7. 方法详解

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：设置房间尺寸分类的概率分布

**参数**：无

**返回值**：float[]，{2, 1, 0} - 正常尺寸概率为2，大型为1，巨型为0

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return new float[]{2, 1, 0};
```

**边界情况**：完全禁用巨型房间（概率为0）

### merge(Level l, Room other, Rect merge, int mergeTerrain)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：处理房间合并时的特殊地形设置

**参数**：
- `l` (Level)：当前关卡实例
- `other` (Room)：要合并的另一个房间
- `merge` (Rect)：合并区域
- `mergeTerrain` (int)：要设置的地形类型

**返回值**：void

**前置条件**：两个房间必须相邻且可以合并

**副作用**：
- 修改关卡地形，在合并区域设置特殊地形

**核心实现逻辑**：
- 如果另一个房间也是 StripedRoom 且合并地形为 Terrain.EMPTY，则使用 Terrain.EMPTY_SP 作为合并地形
- 否则使用父类默认的合并逻辑

**边界情况**：只有当两个 StripedRoom 房间相邻连接时才会触发特殊合并逻辑

### paint(Level level)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制条纹房间的地形布局

**参数**：
- `level` (Level)：当前关卡实例

**返回值**：void

**前置条件**：房间必须已经正确初始化并连接到其他房间

**副作用**：
- 修改关卡地形（设置墙壁、特殊空地、高草条纹）

**核心实现逻辑**：
1. 填充房间墙壁
2. 设置所有门为常规类型
3. 根据尺寸分类应用不同条纹模式：
   - **正常尺寸**：填充内部为 Terrain.EMPTY_SP，然后根据房间宽高比创建平行条纹（水平或垂直）
   - **大型尺寸**：创建多层同心圆环，奇数层为 Terrain.EMPTY_SP，偶数层为 Terrain.HIGH_GRASS

**边界情况**：
- 正常尺寸房间中，当 width == height 时，随机选择水平或垂直条纹方向
- 大型房间的层数计算为 (Math.min(width(), height())-1)/2

## 8. 对外暴露能力

### 显式 API
- 继承的所有公共方法

### 内部辅助方法
- 无额外的内部方法

### 扩展入口
- 可通过继承创建其他类似的装饰性房间变体

## 9. 运行机制与调用链

### 创建时机
- 在 StandardRoom.createRoom() 中通过反射创建实例

### 调用者
- StandardRoom.createRoom() 静态方法
- Level 生成系统的房间绘制阶段
- Room.connect() 方法（触发 merge 调用）

### 被调用者
- Painter.fill() - 填充各种地形
- super.merge() - 调用父类合并逻辑
- Random.Int() - 随机选择条纹方向

### 系统流程位置
- 地牢生成流程中的房间类型选择、绘制和连接阶段

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| 无直接引用 | 无 | 无 |

### 依赖的资源
- Terrain.WALL 地形类型（值为0）
- Terrain.EMPTY_SP 地形类型（值为14）
- Terrain.HIGH_GRASS 地形类型（值为15）

### 中文翻译来源
项目内未找到官方对应译名，保留英文名称 "StripedRoom"

## 11. 使用示例

### 基本用法
```java
// 通过工厂方法创建实例（实际使用中由系统自动调用）
StandardRoom room = StandardRoom.createRoom(); // 可能返回 StripedRoom 实例

// 房间绘制（由Level系统自动调用）
room.paint(level);

// 房间合并（由Level系统在连接房间时自动调用）
room.merge(level, otherRoom, mergeRect, Terrain.EMPTY);
```

### 扩展示例
不适用，此类为具体实现类，通常不需要进一步扩展。

## 12. 开发注意事项

### 状态依赖
- 依赖 sizeCat 字段的状态来决定条纹模式
- 依赖 connected 字段来设置门类型

### 生命周期耦合
- paint() 和 merge() 方法必须在正确的生成阶段调用
- paint() 方法只能调用一次

### 常见陷阱
- 不要直接实例化，应通过 StandardRoom.createRoom() 工厂方法
- 尺寸分类概率分布不应随意修改，这会影响房间类型的生成频率
- 同类型房间合并的特殊处理可能影响相邻房间的地形连续性

## 13. 修改建议与扩展点

### 适合扩展的位置
- 如需不同的条纹模式，可继承 StandardRoom 创建新类
- 可修改条纹的颜色或地形类型以适应不同的地牢主题
- 可添加对巨型房间的支持

### 不建议修改的位置
- 尺寸分类概率分布（正常2，大型1，巨型0）不应随意修改
- 同类型房间合并的特殊逻辑不应移除，这是此类的核心特性

### 重构建议
- 当前实现简洁有效，无需重构
- 条纹生成逻辑可以考虑提取为独立的辅助方法以提高可读性

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（确认无官方翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点