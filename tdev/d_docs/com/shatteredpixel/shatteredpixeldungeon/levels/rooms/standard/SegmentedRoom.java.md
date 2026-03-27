# SegmentedRoom.java 文档

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
SegmentedRoom 类负责生成分段式标准房间布局，使用普通墙壁（WALL）地形通过递归分割算法将房间划分为多个可通行的小区域。

### 系统定位
该类属于关卡生成系统中的标准房间类型之一，继承自 StandardRoom，专门用于创建具有分段结构的通用房间变体。

### 不负责什么
- 不负责墙壁地形的具体视觉表现（由渲染系统处理）
- 不负责怪物或物品在分段区域中的放置策略（由上层逻辑处理）
- 不负责门的具体连接逻辑（由上层系统处理）

## 3. 结构总览

### 主要成员概览
- 无实例字段
- 无静态常量
- private createWalls() 方法：递归分割房间并创建墙壁

### 主要逻辑块概览
- minWidth()/minHeight() 方法：确保房间最小尺寸为7格
- sizeCatProbs() 方法：定义房间尺寸类别的概率分布 [9, 3, 1]
- paint() 方法：核心绘制逻辑，设置基础地形并启动递归分割
- createWalls() 方法：递归算法将区域分割成多个小段，每段用墙壁隔开并留出双格通道

### 生命周期/调用时机
该类实例在关卡生成过程中被 RoomFactory 创建，paint() 方法在关卡绘制阶段被调用。

## 4. 继承与协作关系

### 父类提供的能力
- width()/height() 方法：获取房间尺寸
- left/right/top/bottom 字段：房间边界坐标
- connected 字段：连接的门信息
- sizeCat 字段：房间尺寸类别

### 覆写的方法
- minWidth()：覆写自 Room
- minHeight()：覆写自 Room
- sizeCatProbs()：覆写自 Room
- paint()：覆写自 Room

### 实现的接口契约
无

### 依赖的关键类
- Level：关卡数据结构
- Terrain：地形类型定义（WALL, EMPTY）
- Painter：关卡绘制工具
- Point/Rect：几何工具类
- Random：随机数生成器
- Door：门对象

### 使用者
- RoomFactory：创建房间实例
- LevelGenerator：在关卡生成过程中使用

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器。

### 初始化块
无

### 初始化注意事项
该类完全依赖父类 StandardRoom 的初始化机制，实例化后通过 paint() 方法进行实际的房间绘制。

## 7. 方法详解

### minWidth()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：返回房间的最小宽度要求

**参数**：无

**返回值**：int，最小宽度值（至少为7）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(super.minWidth(), 7);
```
确保房间宽度至少为7格，以容纳基本的分段布局结构。

**边界情况**：当父类返回值小于7时，返回7；否则返回父类值。

### minHeight()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：返回房间的最小高度要求

**参数**：无

**返回值**：int，最小高度值（至少为7）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(super.minHeight(), 7);
```
确保房间高度至少为7格，以容纳基本的分段布局结构。

**边界情况**：当父类返回值小于7时，返回7；否则返回父类值。

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：定义不同尺寸类别房间的生成概率

**参数**：无

**返回值**：float[]，包含三个概率值 [9, 3, 1]

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定数组 new float[]{9, 3, 1}，表示小、中、大三种尺寸类别的相对概率。

**边界情况**：无

### paint()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制分段房间的实际布局，包括墙壁和递归分段结构

**参数**：
- level (Level)：当前关卡对象

**返回值**：void

**前置条件**：房间边界已正确设置，connected 字典包含连接的门

**副作用**：修改 level 对象的地形数据

**核心实现逻辑**：
1. 首先填充整个房间区域为墙壁地形 (Terrain.WALL)
2. 在距离边界1格的内部区域填充为空地地形 (Terrain.EMPTY)
3. 将所有连接的门设置为常规门类型 (Door.Type.REGULAR)
4. 在门位置设置为空地地形 (Terrain.EMPTY)，确保门通道连通（有助于 createWalls 逻辑）
5. 调用 createWalls() 方法对内部区域（距离边界1格外）进行递归分割

**边界情况**：
- 最小房间尺寸为7x7，确保有足够的空间进行分段
- 门通道的预处理确保分割算法不会阻塞入口

### createWalls()

**可见性**：private

**是否覆写**：否

**方法职责**：递归分割指定区域，创建墙壁并将区域划分为多个可通行的小段

**参数**：
- level (Level)：当前关卡对象
- area (Rect)：要处理的矩形区域

**返回值**：void

**前置条件**：area 是有效的矩形区域

**副作用**：修改 level 对象的地形数据

**核心实现逻辑**：
1. 检查区域是否足够大进行分割：
   - 最大维度≥5 且最小维度≥3，否则直接返回
2. 最多尝试10次分割：
   - 如果宽度≥高度（或相等且随机选择）：垂直分割
     - 随机选择分割X坐标（距离边界至少2格）
     - 检查分割线两端是否都是墙壁地形（确保连续性）
     - 如果满足条件：
       - 在分割位置绘制垂直墙壁
       - 在墙上随机位置留出两个连续的 EMPTY 通道（spaceTop 和 spaceTop+1）
       - 递归处理左右两个子区域
   - 否则：水平分割
     - 随机选择分割Y坐标（距离边界至少2格）
     - 检查分割线两端是否都是墙壁地形
     - 如果满足条件：
       - 在分割位置绘制水平墙壁
       - 在墙上随机位置留出两个连续的 EMPTY 通道（spaceLeft 和 spaceLeft+1）
       - 递归处理上下两个子区域
3. 如果10次尝试都失败，则放弃分割该区域

**边界情况**：
- 分割坐标确保子区域至少有1格大小
- 双格通道确保相邻区域可以互相通行，提供更宽的路径
- 递归深度受区域大小限制，避免无限递归
- 与 SegmentedLibraryRoom 相比，此实现使用双格通道而非单格通道

## 8. 对外暴露能力

### 显式 API
- minWidth()
- minHeight()
- sizeCatProbs()
- paint()

### 内部辅助方法
- createWalls()：仅供内部使用

### 扩展入口
该类没有提供 protected 方法供子类扩展，如需自定义分段房间行为，应直接继承 StandardRoom 并实现自己的逻辑。

## 9. 运行机制与调用链

### 创建时机
在关卡生成过程中，当RoomFactory需要创建分段房间类型时实例化。

### 调用者
- RoomFactory.createRoom()：创建房间实例
- LevelGenerator.paint()：调用 paint() 方法绘制房间

### 被调用者
- Painter.fill()/Painter.drawLine()/Painter.set()：填充和绘制地形
- Random.Int()/Random.IntRange()：生成随机数
- createWalls()：递归分割（自调用）

### 系统流程位置
位于关卡生成流程的房间绘制阶段，在房间布局确定但具体地形未填充时执行。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| (无官方翻译) | 分段房间 | 房间类型的中文名称（项目内未找到官方对应译名） |

### 依赖的资源
- Terrain.WALL：普通墙壁地形
- Terrain.EMPTY：空地地形

### 中文翻译来源
在 levels_zh.properties 文件中未找到 SegmentedRoom 的官方翻译，因此使用描述性翻译"分段房间"。

## 11. 使用示例

### 基本用法
```java
// 在关卡生成器中创建分段房间
SegmentedRoom segmentedRoom = new SegmentedRoom();
segmentedRoom.set(left, top, right, bottom); // 设置房间边界
segmentedRoom.paint(level); // 绘制房间到关卡
```

### 扩展示例
由于该类没有提供扩展点，如需自定义行为，建议参考其实现创建新的房间类型：

```java
public class CustomSegmentedRoom extends StandardRoom {
    @Override
    public void paint(Level level) {
        // 自定义分段生成逻辑
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);
        // 添加自定义分割逻辑
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖房间的边界坐标（left, right, top, bottom）已正确设置
- 依赖 connected 字典包含有效的门连接信息
- createWalls() 方法依赖 level.map 数组已正确初始化

### 生命周期耦合
- 必须在房间边界设置完成后调用 paint() 方法
- paint() 方法应在关卡地形数组初始化后调用

### 常见陷阱
- 修改 minWidth()/minHeight() 返回值时需确保仍能满足分段布局的最小空间需求
- 直接修改 createWalls() 算法时需注意避免创建无法通行的封闭区域
- 门通道的预处理逻辑不应随意移除，否则可能导致分割算法阻塞入口
- 双格通道设计是有意为之，提供更宽的通行路径，不应随意改为单格

## 13. 修改建议与扩展点

### 适合扩展的位置
- 如需不同的分割策略，可修改 createWalls() 方法的分割逻辑
- 如需不同的最小区域尺寸，可调整 createWalls() 中的尺寸检查条件
- 可考虑添加配置选项来控制分割密度或通道宽度

### 不建议修改的位置
- 最小尺寸限制（7格）不应降低，否则可能导致分割失败
- 双格通道的核心设计不应改变，这是与图书馆房间的重要区别
- 通道创建逻辑不应移除，否则会导致区域无法通行

### 重构建议
考虑到 SegmentedLibraryRoom 和 SegmentedRoom 存在大量相似代码（正如 SegmentedLibraryRoom 注释中提到的 "copypasta"），建议将共同的分割逻辑提取到共享的工具类或抽象父类中。这将提高代码的可维护性并减少重复。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（未找到官方翻译，使用描述性名称）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点