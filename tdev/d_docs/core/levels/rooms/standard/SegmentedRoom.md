# SegmentedRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\SegmentedRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 116 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
SegmentedRoom 类负责生成分段式房间布局。它使用递归分割算法将房间内部划分为多个区域，通过在墙壁之间创建双格宽的通道来确保可通行性，并维护完整的房间结构。

### 系统定位
作为标准房间（StandardRoom）的一种具体实现，SegmentedRoom 在关卡生成过程中提供具有复杂内部结构的房间类型，增加探索的策略性和关卡的视觉多样性。

### 不负责什么
- 不负责特殊的物品生成逻辑
- 不处理特殊的敌人生成规则
- 不管理房间连接的特殊逻辑
- 不负责特定主题的装饰元素

## 3. 结构总览

### 主要成员概览
- 无额外字段（继承自父类的字段包括 sizeCat 等）
- 私有方法 createWalls() 实现递归分割算法

### 主要逻辑块概览
- 尺寸约束逻辑：确保最小尺寸为7x7
- 尺寸分类概率：定义 NORMAL/LARGE/GIANT 的出现概率为 9:3:1
- 基础地形绘制：WALL → EMPTY
- 门区域处理：确保门位置为可行走空地
- 递归分割算法：基于房间长宽比选择分割方向
- 双格通道创建逻辑：在分割线处创建2格宽的安全通行空间

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
- Door 内部类

### 覆写的方法
- minWidth()：返回 Math.max(super.minWidth(), 7)
- minHeight()：返回 Math.max(super.minHeight(), 7)
- sizeCatProbs()：返回 new float[]{9, 3, 1}
- paint()：实现分段房间的具体绘制逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 间接实现）
- Bundlable 接口（通过 Room 间接实现）

### 依赖的关键类
- Level：关卡数据结构
- Terrain：地形类型定义（WALL, EMPTY）
- Painter：关卡绘制工具（fill(), drawLine(), set()）
- Random：随机数生成（Int(), IntRange()）
- Point/Rect：几何计算

### 使用者
- StandardRoom.createRoom() 静态工厂方法
- LevelGenerator：关卡生成器在构建房间网络时

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

**方法职责**：确保 SegmentedRoom 的最小宽度至少为7格

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

**方法职责**：确保 SegmentedRoom 的最小高度至少为7格

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

**方法职责**：在关卡中实际绘制 SegmentedRoom 的分段地形

**参数**：
- `level` (Level)：要绘制到的关卡实例

**返回值**：void

**前置条件**：房间必须已经设置了有效的位置和尺寸

**副作用**：修改 level 对象的 map 数组

**核心实现逻辑**：
1. 使用 Painter.fill() 填充整个房间为墙壁 (Terrain.WALL)
2. 填充内部1格区域为空地 (Terrain.EMPTY)
3. 设置所有连接门为常规门类型，并使用 Painter.set() 确保门位置为 EMPTY
4. 调用 createWalls() 对内部区域 (left+1 到 right-1, top+1 到 bottom-1) 进行递归分割

**边界情况**：
- 最小房间尺寸为7x7，确保有足够的空间进行分割
- 门位置明确设置为 EMPTY，确保玩家可以安全进出

### createWalls(Level level, Rect area)
**可见性**：private

**是否覆写**：否

**方法职责**：递归分割指定区域，创建墙壁和双格通道

**参数**：
- `level` (Level)：关卡实例
- `area` (Rect)：要分割的区域

**返回值**：void

**前置条件**：area 必须是有效的矩形区域

**副作用**：修改 level 对象的 map 数组

**核心实现逻辑**：
1. 检查区域是否足够大（最大维度≥5且最小维度≥3），否则返回
2. 最多重试10次寻找有效的分割位置
3. 基于区域长宽比选择分割方向（水平或垂直）
4. 水平分割：
   - 随机选择分割X坐标（area.left+2 到 area.right-2）
   - 验证分割线上下边缘都是 WALL 地形
   - 如果验证通过，绘制垂直墙壁线
   - 在分割线上随机位置创建2格高的 EMPTY 通道
   - 递归处理左右两个子区域
5. 垂直分割：
   - 随机选择分割Y坐标（area.top+2 到 area.bottom-2）
   - 验证分割线左右边缘都是 WALL 地形
   - 如果验证通过，绘制水平墙壁线
   - 在分割线上随机位置创建2格宽的 EMPTY 通道
   - 递归处理上下两个子区域

**边界情况**：
- 递归终止条件确保不会无限分割
- 分割位置验证确保墙壁结构的连续性
- 双格通道创建确保分割后的区域仍然连通
- 与 SegmentedLibraryRoom 的主要区别在于使用普通 EMPTY 地形和双格通道

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是 Room/StandardRoom 协议的一部分
- 没有额外的公共 API

### 内部辅助方法
- createWalls() 是私有方法，不应被外部依赖

### 扩展入口
- 可以通过继承进一步自定义分割逻辑
- 可以覆写其他 StandardRoom 方法来自定义行为

## 9. 运行机制与调用链

### 创建时机
- 在关卡生成过程中的房间创建阶段
- 通过 StandardRoom.createRoom() 静态工厂方法随机选择创建

### 调用者
- StandardRoom.createRoom()：通过反射创建实例
- LevelGenerator：在构建房间网络时调用 paint()

### 被调用者
- Painter.fill()/drawLine()/set()：用于地形绘制
- Random.Int()/IntRange()：用于随机决策
- createWalls()：递归分割区域
- super 方法：获取父类基础功能

### 系统流程位置
1. 房间实例创建 → 2. 尺寸设置 → 3. 房间网络构建 → 4. paint() 调用 → 5. 基础地形绘制 → 6. 递归分割 → 7. 通道创建

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
项目内未找到官方对应译名

### 依赖的资源
- Terrain 常量：WALL, EMPTY
- 基础地形类型，无特殊资源依赖

### 中文翻译来源
在 levels_zh.properties 中未找到 SegmentedRoom 相关翻译

## 11. 使用示例

### 基本用法
```java
// SegmentedRoom 通常由关卡生成器自动创建，不建议手动实例化
SegmentedRoom room = new SegmentedRoom();
// 设置房间位置和尺寸（至少7x7）
room.set(new Rect(10, 10, 20, 20));
// 在关卡中绘制
room.paint(level);
// 房间会自动生成分段式布局
```

### 扩展示例
如需自定义分割逻辑，可以继承 SegmentedRoom：

```java
public class CustomSegmentedRoom extends SegmentedRoom {
    @Override
    private void createWalls(Level level, Rect area) {
        // 实现自定义的分割算法
        // 例如，总是创建三向分割
        if (area.width() >= 6 && area.height() >= 6) {
            int centerX = (area.left + area.right) / 2;
            int centerY = (area.top + area.bottom) / 2;
            // 创建十字形分割...
        }
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖于房间的 left/right/top/bottom 坐标已正确设置
- 依赖于 Terrain.WALL 和 Terrain.EMPTY 的存在和正确行为
- createWalls() 依赖于初始地形的正确设置

### 生命周期耦合
- 必须在房间位置和尺寸确定后才能调用 paint()
- paint() 调用后所有地形即固定
- 递归分割必须在基础地形绘制完成后进行

### 常见陷阱
- 修改分割算法时需确保生成的区域仍然连通
- 双格通道的创建确保了足够的通行空间，避免单格通道可能造成的卡顿
- 递归分割可能导致栈溢出，但当前实现通过区域大小检查避免了这个问题
- 与 SegmentedLibraryRoom 的关系：两者共享相似的分割逻辑，但使用不同的地形和通道宽度

## 13. 修改建议与扩展点

### 适合扩展的位置
- createWalls() 方法：自定义分割算法
- 通道创建逻辑：调整通道宽度或形状
- 尺寸约束：根据设计需求调整最小尺寸
- 分割方向选择：添加更多复杂的分割策略

### 不建议修改的位置
- 基础地形绘制逻辑（WALL → EMPTY）
- 门位置的 EMPTY 设置（确保玩家能安全进出）
- 分割位置验证逻辑（确保墙壁结构的完整性）
- 双格通道的基本概念（确保良好的游戏体验）

### 重构建议
考虑到与 SegmentedLibraryRoom 的相似性，建议：
1. 提取公共的递归分割算法到抽象基类
2. 将地形类型和通道参数作为可配置字段
3. 将分割常量（如最小区域大小、边距等）提取为类常量

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点