# StandardBridgeRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\StandardBridgeRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | abstract class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 185 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
StandardBridgeRoom 类是一个抽象基类，负责提供桥式房间的通用实现框架。它在房间内部创建一个分隔空间，并在其中放置一条连接通道（桥梁），用于将房间分割成两个区域同时保持连通性。

### 系统定位
作为标准房间（StandardRoom）的抽象子类，StandardBridgeRoom 为具体的桥式房间实现（如 RegionDecoBridgeRoom、WaterBridgeRoom）提供基础功能和扩展点，是桥式房间类型的共同父类。

### 不负责什么
- 不直接实例化（抽象类）
- 不定义具体的地形类型（通过抽象方法让子类实现）
- 不处理特殊的物品或敌人生成逻辑
- 不负责房间合并的特殊逻辑

## 3. 结构总览

### 主要成员概览
- 受保护字段 spaceRect 和 bridgeRect：存储分隔空间和桥梁区域
- 抽象方法 maxBridgeWidth(), spaceTile(), bridgeTile()：供子类自定义
- 完整的 paint() 实现：处理桥式房间的核心逻辑

### 主要逻辑块概览
- 尺寸约束逻辑：确保最小尺寸为5x5
- 墙壁选择算法：基于门位置和房间尺寸选择最佳分隔方向
- 空间计算：找出最大的无门区域用于放置分隔空间
- 桥梁生成：在分隔空间中随机位置创建连接桥梁
- 物品/角色放置限制：阻止在分隔空间内放置

### 生命周期/调用时机
- 子类实例化时自动设置基础属性
- 关卡绘制阶段调用 paint() 方法生成实际地形

## 4. 继承与协作关系

### 父类提供的能力
从 StandardRoom 继承：
- SizeCategory 枚举和相关字段
- setSizeCat() 相关方法
- sizeFactor(), mobSpawnWeight(), connectionWeight() 等辅助方法

From Room 继承：
- 空间和连接逻辑（Rect 功能扩展）
- Door 内部类

### 覆写的方法
- minWidth()：返回 Math.max(5, super.minWidth())
- minHeight()：返回 Math.max(5, super.minHeight())
- canMerge()：基于 spaceTile() 返回值决定合并行为
- canPlaceItem()：阻止在分隔空间内放置物品
- canPlaceCharacter()：阻止在分隔空间内放置角色
- paint()：实现桥式房间的具体绘制逻辑

### 抽象方法（必须由子类实现）
- maxBridgeWidth(int roomDimension)：定义桥梁的最大宽度
- spaceTile()：定义分隔空间的地形类型
- bridgeTile()：定义桥梁的地形类型（默认为 EMPTY_SP）

### 实现的接口契约
- Graph.Node 接口（通过 Room 间接实现）
- Bundlable 接口（通过 Room 间接实现）

### 依赖的关键类
- Level：关卡数据结构
- Terrain：地形类型定义
- Painter：关卡绘制工具（fill(), drawInside()）
- Random：随机数生成
- Point/Rect：几何计算
- ArrayList/Collections/Comparator：用于墙壁选择算法

### 使用者
- RegionDecoBridgeRoom：具体的桥式房间实现
- WaterBridgeRoom：具体的桥式房间实现
- StandardRoom.createRoom()：通过反射创建子类实例

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
- **spaceRect** (Rect)：受保护字段，存储分隔空间的矩形区域
- **bridgeRect** (Rect)：受保护字段，存储桥梁的矩形区域

## 6. 构造与初始化机制

### 构造器
使用默认构造器，无自定义构造逻辑。

### 初始化块
无显式初始化块。

### 初始化注意事项
- 作为抽象类不能直接实例化
- spaceRect 和 bridgeRect 字段在 paint() 调用时初始化
- 继承父类的默认尺寸分类概率（[1, 0, 0]，即总是 NORMAL）

## 7. 方法详解

### minWidth()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：确保 StandardBridgeRoom 的最小宽度至少为5格

**参数**：无

**返回值**：int，返回父类 minWidth() 和 5 的较大值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(5, super.minWidth());
```

**边界情况**：当父类 minWidth() 返回值小于5时，强制返回5

### minHeight()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：确保 StandardBridgeRoom 的最小高度至少为5格

**参数**：无

**返回值**：int，返回父类 minHeight() 和 5 的较大值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(5, super.minHeight());
```

**边界情况**：当父类 minHeight() 返回值小于5时，强制返回5

### canMerge(Level l, Room other, Point p, int mergeTerrain)
**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：决定是否允许与其他房间合并

**参数**：
- `l` (Level)：关卡实例
- `other` (Room)：其他房间
- `p` (Point)：合并点
- `mergeTerrain` (int)：合并地形类型

**返回值**：boolean，如果合并地形不是分隔空间地形则允许合并

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
int cell = l.pointToCell(pointInside(p, 1));
return l.map[cell] != spaceTile();
```

**边界情况**：只有当合并点内部1格位置不是分隔空间地形时才允许合并

### canPlaceItem(Point p, Level l)
**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：阻止在分隔空间内放置物品

**参数**：
- `p` (Point)：要检查的位置
- `l` (Level)：关卡实例

**返回值**：boolean，如果位置有效且不在分隔空间内则返回 true

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return super.canPlaceItem(p, l) && (spaceRect == null || !spaceRect.inside(p));
```

**边界情况**：spaceRect 为 null 时（paint() 未调用）允许放置

### canPlaceCharacter(Point p, Level l)
**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：阻止在分隔空间内放置角色

**参数**：
- `p` (Point)：要检查的位置
- `l` (Level)：关卡实例

**返回值**：boolean，如果位置有效且不在分隔空间内则返回 true

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return super.canPlaceCharacter(p, l) && (spaceRect == null || !spaceRect.inside(p));
```

**边界情况**：同 canPlaceItem()

### paint(Level level)
**可见性**：public

**是否覆写**：是，覆写自 Room 抽象方法

**方法职责**：在关卡中实际绘制 StandardBridgeRoom 的桥式地形

**参数**：
- `level` (Level)：要绘制到的关卡实例

**返回值**：void

**前置条件**：房间必须已经设置了有效的位置和尺寸

**副作用**：修改 level 对象的 map 数组

**核心实现逻辑**：
1. 使用 Painter.fill() 填充整个房间为墙壁 (Terrain.WALL)
2. 填充内部1格区域为空地 (Terrain.EMPTY)
3. 计算门偏好分数：
   - 优先选择门较少的分隔方向（水平/垂直）
   - 考虑房间长宽比（宽>高倾向于垂直分隔）
4. 如果选择水平分隔（左右分隔）：
   - 收集顶部和底部的门位置
   - 添加虚拟门确保边缘覆盖
   - 找出最大的无门水平间隙
   - 调整间隙宽度不超过 maxBridgeWidth(width())+1
   - 创建分隔空间（spaceRect）和桥梁（bridgeRect）
5. 如果选择垂直分隔（上下分隔）：
   - 收集左侧和右侧的门位置  
   - 添加虚拟门确保边缘覆盖
   - 找出最大的无门垂直间隙
   - 调整间隙宽度不超过 maxBridgeWidth(height())+1
   - 创建分隔空间（spaceRect）和桥梁（bridgeRect）
6. 使用 fill() 填充分隔空间为 spaceTile() 地形
7. 使用 fill() 填充桥梁为 bridgeTile() 地形
8. 设置所有连接门为常规门类型，并使用 drawInside() 确保门周围有足够空间

**边界情况**：
- 最小房间尺寸5x5确保有足够的分隔空间
- 虚拟门的添加确保边缘区域被考虑
- 随机调整间隙确保桥梁位置的变化性

### maxBridgeWidth(int roomDimension)
**可见性**：protected abstract

**是否覆写**：否（抽象方法）

**方法职责**：定义桥梁的最大宽度，基于房间维度

**参数**：
- `roomDimension` (int)：房间的宽度或高度

**返回值**：int，最大桥梁宽度

**前置条件**：无

**副作用**：无

**核心实现逻辑**：由子类实现

**边界情况**：子类应确保返回值合理（通常为1-3）

### spaceTile()
**可见性**：protected abstract

**是否覆写**：否（抽象方法）

**方法职责**：定义分隔空间使用的地形类型

**参数**：无

**返回值**：int，地形类型值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：由子类实现

**边界情况**：子类应返回有效的 Terrain 常量

### bridgeTile()
**可见性**：protected

**是否覆写**：否（可被子类覆写）

**方法职责**：定义桥梁使用的地形类型

**参数**：无

**返回值**：int，默认返回 Terrain.EMPTY_SP

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Terrain.EMPTY_SP;
```

**边界情况**：子类可以覆写以使用不同的桥梁地形

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是 Room/StandardRoom 协议的一部分
- 没有额外的公共 API

### 内部辅助方法
- spaceRect 和 bridgeRect 字段供子类使用
- 抽象方法提供扩展点

### 扩展入口
- 必须实现三个抽象方法来自定义桥式房间
- 可以覆写 bridgeTile() 自定义桥梁地形
- 可以覆写其他 StandardRoom 方法来自定义行为

## 9. 运行机制与调用链

### 创建时机
- 在关卡生成过程中的房间创建阶段
- 通过 StandardRoom.createRoom() 静态工厂方法随机选择创建子类

### 调用者
- StandardRoom.createRoom()：通过反射创建子类实例
- LevelGenerator：在构建房间网络时调用 paint()

### 被调用者
- Painter.fill()/drawInside()：用于地形绘制
- Random.Int()：用于随机决策
- Collections.sort()/Comparator：用于门排序
- ArrayList 操作：管理门列表
- super 方法：获取父类基础功能
- 抽象方法：由具体子类实现

### 系统流程位置
1. 子类实例创建 → 2. 尺寸设置 → 3. 房间网络构建 → 4. paint() 调用 → 5. 墙壁选择 → 6. 空间计算 → 7. 桥梁生成

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
项目内未找到官方对应译名（抽象类不直接显示）

### 依赖的资源
- Terrain 常量：WALL, EMPTY, EMPTY_SP
- EMPTY_SP 可能是特殊的空地类型，用于安全通行

### 中文翻译来源
不适用（抽象基类）

## 11. 使用示例

### 基本用法
```java
// StandardBridgeRoom 是抽象类，不能直接实例化
// 需要创建具体的子类
public class CustomBridgeRoom extends StandardBridgeRoom {
    @Override
    protected int maxBridgeWidth(int roomDimension) {
        return 2;
    }
    
    @Override
    protected int spaceTile() {
        return Terrain.WATER;
    }
    
    @Override
    protected int bridgeTile() {
        return Terrain.EMPTY;
    }
}

// 使用子类
CustomBridgeRoom room = new CustomBridgeRoom();
room.set(new Rect(10, 10, 20, 20));
room.paint(level);
```

### 扩展示例
现有的子类实现：
- RegionDecoBridgeRoom：使用 REGION_DECO_ALT 作为空间，EMPTY_SP 作为桥梁
- WaterBridgeRoom：使用 WATER 作为空间，EMPTY_SP 作为桥梁，动态桥梁宽度

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖于房间的 left/right/top/bottom 坐标已正确设置
- 依赖于 connected 映射已正确建立（门的位置和连接）
- spaceRect 和 bridgeRect 依赖于 paint() 的正确执行

### 生命周期耦合
- 必须在房间位置和尺寸确定后才能调用 paint()
- paint() 调用后所有地形即固定
- 物品/角色放置必须在 paint() 之后进行验证

### 常见陷阱
- 子类必须正确实现所有抽象方法
- 桥梁宽度设置过大会影响游戏平衡
- 分隔空间地形需要与其他系统兼容
- 最小尺寸5x5是基本要求，更小的房间无法正常工作

## 13. 修改建议与扩展点

### 适合扩展的位置
- 抽象方法：完全自定义桥式房间的外观和行为
- 桥梁生成逻辑：添加更多复杂的桥梁模式
- 墙壁选择算法：改进门位置的权重计算

### 不建议修改的位置
- 基础房间结构（WALL → EMPTY）
- 门类型设置和通道优化
- 物品/角色放置限制逻辑

### 重构建议
当前实现较为完善，但如果需要更多桥式变体，可以考虑：
1. 添加更多抽象方法来控制桥梁生成细节
2. 提供默认的 bridgeTile() 实现以减少子类负担
3. 优化墙壁选择算法以支持更多分隔模式

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点