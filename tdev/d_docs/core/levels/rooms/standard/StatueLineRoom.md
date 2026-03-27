# StatueLineRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\StatueLineRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 109 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
StatueLineRoom 类负责生成在房间边缘放置雕像（STATUE）线条的房间布局。它分析所有门的位置，选择最少门或无门的墙壁来放置装饰性雕像线条，确保装饰不会干扰房间的可通行性。

### 系统定位
作为标准房间（StandardRoom）的一种具体实现，StatueLineRoom 在关卡生成过程中提供具有雕像装饰主题的房间类型，增加关卡环境的多样性和视觉装饰性。

### 不负责什么
- 不负责雕像的具体生成逻辑或交互行为
- 不处理特殊的敌人生成规则
- 不管理物品放置的特殊规则
- 不负责房间合并的特殊逻辑

## 3. 结构总览

### 主要成员概览
- 静态常量 N/E/S/W：表示四个方向的整数常量
- 受保护方法 decoTerrain()：定义装饰地形类型
- 私有方法 fillAlongSide()：沿指定边绘制装饰线条

### 主要逻辑块概览
- 尺寸约束逻辑：确保最小尺寸为5x5
- 墙壁选择算法：基于门位置偏好选择最佳装饰墙壁
- 装饰线条绘制：沿选定墙壁绘制连续的雕像线条
- 门通道优化：确保门周围有足够的可行走空间

### 生命周期/调用时机
- 房间实例化时自动设置基础属性
- 关卡绘制阶段调用 paint() 方法生成实际地形和装饰

## 4. 继承与协作关系

### 父类提供的能力
从 StandardRoom 继承：
- SizeCategory 枚举和相关字段
- minWidth()/minHeight() 基础实现（最小5x5）
- setSizeCat() 相关方法
- sizeFactor(), mobSpawnWeight(), connectionWeight() 等辅助方法

从 Room 继承：
- 空间和连接逻辑（Rect 功能扩展）
- Door 内部类

### 覆写的方法
- minWidth()：返回 Math.max(5, super.minWidth())
- minHeight()：返回 Math.max(5, super.minHeight())
- paint()：实现雕像线条房间的具体绘制逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 间接实现）
- Bundlable 接口（通过 Room 间接实现）

### 依赖的关键类
- Level：关卡数据结构
- Terrain：地形类型定义（WALL, EMPTY, STATUE）
- Painter：关卡绘制工具（fill(), drawLine(), drawInside()）
- Random：随机数生成（chances()）
- Point：几何计算

### 使用者
- StandardRoom.createRoom() 静态工厂方法
- LevelGenerator：关卡生成器在构建房间网络时
- RegionDecoLineRoom：作为父类被继承

## 5. 字段/常量详解

### 静态常量
- **N** (Integer)：值为 0，表示北（上）方向
- **E** (Integer)：值为 1，表示东（右）方向  
- **S** (Integer)：值为 2，表示南（下）方向
- **W** (Integer)：值为 3，表示西（左）方向

### 实例字段
无额外字段（全部继承自父类）

## 6. 构造与初始化机制

### 构造器
使用默认构造器，无自定义构造逻辑。

### 初始化块
无显式初始化块。

### 初始化注意事项
- 继承父类的最小尺寸约束（5x5）
- 尺寸分类使用父类的默认概率（[1, 0, 0]，即总是 NORMAL）

## 7. 方法详解

### minWidth()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：确保 StatueLineRoom 的最小宽度至少为5格

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

**方法职责**：确保 StatueLineRoom 的最小高度至少为5格

**参数**：无

**返回值**：int，返回父类 minHeight() 和 5 的较大值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(5, super.minHeight());
```

**边界情况**：当父类 minHeight() 返回值小于5时，强制返回5

### paint(Level level)
**可见性**：public

**是否覆写**：是，覆写自 Room 抽象方法

**方法职责**：在关卡中实际绘制 StatueLineRoom 的地形和雕像装饰线条

**参数**：
- `level` (Level)：要绘制到的关卡实例

**返回值**：void

**前置条件**：房间必须已经设置了有效的位置和尺寸

**副作用**：修改 level 对象的 map 数组

**核心实现逻辑**：
1. 使用 Painter.fill() 填充整个房间为墙壁 (Terrain.WALL)
2. 填充内部1格区域为空地 (Terrain.EMPTY)
3. 初始化四个方向的偏好分数数组 [1, 1, 1, 1]
4. 遍历所有连接的门，根据门的位置减少对应方向的偏好分数：
   - 直接在边缘的门：减2分
   - 距离边缘1格的门：减1分
5. 使用 Random.chances() 选择偏好分数最高的方向
6. 如果没有有效方向（所有分数≤0），逐步增加所有方向分数直到找到有效选择
7. 调用 fillAlongSide() 沿选定方向绘制装饰线条
8. 设置所有连接门为常规门类型，并使用 drawInside() 确保门周围1格内为空地

**边界情况**：
- 最小房间尺寸5x5确保有足够的装饰空间
- 门位置偏好算法确保装饰不会干扰主要通行路径
- 随机选择机制在平局时提供变化性

### decoTerrain()
**可见性**：protected

**是否覆写**：否（可被子类覆写）

**方法职责**：定义装饰线条使用的地形类型

**参数**：无

**返回值**：int，Terrain.STATUE 的值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Terrain.STATUE;
```

**边界情况**：无

### fillAlongSide(Integer side, Level level)
**可见性**：private

**是否覆写**：否

**方法职责**：沿指定方向的墙壁绘制装饰线条

**参数**：
- `side` (Integer)：方向常量（N/E/S/W）
- `level` (Level)：关卡实例

**返回值**：void

**前置条件**：方向参数必须是有效的常量值

**副作用**：修改 level 对象的 map 数组

**核心实现逻辑**：
- **北方向 (0)**：在 top+1 行，从 left+1 到 right-1 绘制水平线
- **东方向 (1)**：在 right-1 列，从 top+1 到 bottom-1 绘制垂直线  
- **南方向 (2)**：在 bottom-1 行，从 left+1 到 right-1 绘制水平线
- **西方向 (3)**：在 left+1 列，从 top+1 到 bottom-1 绘制垂直线

使用 Painter.drawLine() 绘制连续的装饰线条，地形类型由 decoTerrain() 方法提供。

**边界情况**：
- 装饰线条距离房间边缘1格，确保不破坏房间结构
- 覆盖整条墙壁的长度，提供完整的装饰效果

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是 Room/StandardRoom 协议的一部分
- 没有额外的公共 API

### 内部辅助方法
- decoTerrain() 是受保护的扩展点，可用于自定义装饰地形
- fillAlongSide() 是私有方法，不应被外部直接调用

### 扩展入口
- 可以通过继承覆写 decoTerrain() 自定义装饰地形类型
- 可以创建新的子类来实现不同的装饰逻辑（如 RegionDecoLineRoom）

## 9. 运行机制与调用链

### 创建时机
- 在关卡生成过程中的房间创建阶段
- 通过 StandardRoom.createRoom() 静态工厂方法随机选择创建

### 调用者
- StandardRoom.createRoom()：通过反射创建实例
- LevelGenerator：在构建房间网络时调用 paint()

### 被调用者
- Painter.fill()/drawLine()/drawInside()：用于地形绘制
- Random.chances()：用于方向选择
- super.paint()：获取父类基础功能

### 系统流程位置
1. 房间实例创建 → 2. 尺寸设置 → 3. 房间网络构建 → 4. paint() 调用 → 5. 墙壁选择 → 6. 装饰线条绘制

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
项目内未找到官方对应译名

### 依赖的资源
- Terrain 常量：WALL, EMPTY, STATUE
- STATUE 地形可能显示雕像装饰的视觉效果

### 中文翻译来源
在 levels_zh.properties 中未找到 StatueLineRoom 相关翻译

## 11. 使用示例

### 基本用法
```java
// StatueLineRoom 通常由关卡生成器自动创建，不建议手动实例化
StatueLineRoom room = new StatueLineRoom();
// 设置房间位置和尺寸
room.set(new Rect(10, 10, 20, 20));
// 在关卡中绘制
room.paint(level);
// 房间会自动在最合适的位置（最少门的墙壁）绘制雕像装饰线条
```

### 扩展示例
如需自定义装饰地形类型，可以继承 StatueLineRoom：

```java
public class CustomStatueLineRoom extends StatueLineRoom {
    @Override
    protected int decoTerrain() {
        return Terrain.HIGH_GRASS; // 使用高草作为装饰线条
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖于房间的 left/right/top/bottom 坐标已正确设置
- 依赖于 connected 映射已正确建立（门的位置和连接）
- 依赖于 Terrain.STATUE 的存在和正确行为

### 生命周期耦合
- 必须在房间位置和尺寸确定后才能调用 paint()
- paint() 调用后装饰线条位置即固定
- 门的位置必须在 paint() 调用前正确设置

### 常见陷阱
- 修改装饰地形类型时需确保新地形与其他系统兼容
- 门位置偏好算法在极端情况下（所有墙壁都有多个门）可能表现异常
- 最小尺寸5x5是装饰线条的基本要求，更小的房间无法容纳完整装饰

## 13. 修改建议与扩展点

### 适合扩展的位置
- decoTerrain() 方法：自定义装饰地形类型
- 墙壁选择算法：调整门位置的权重计算
- 装饰线条位置：调整距离边缘的偏移量
- 可以创建更多子类实现不同的装饰主题

### 不建议修改的位置
- 基础的房间结构生成逻辑（WALL → EMPTY）
- 门通道优化逻辑（确保可通行性）
- 装饰线条的连续性（这是线状装饰的核心特征）

### 重构建议
当前实现较为清晰，符合开闭原则。如果需要更多自定义功能，建议通过继承而不是修改现有逻辑。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点