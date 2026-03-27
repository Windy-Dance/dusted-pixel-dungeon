# PlatformRoom.java 文档

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
PlatformRoom 类负责生成包含多个悬浮平台的标准房间布局，平台之间通过小桥连接，平台周围是深渊（CHASM）地形。

### 系统定位
该类属于关卡生成系统中的标准房间类型之一，继承自 StandardRoom，专门用于创建具有平台和深渊地形的房间变体。

### 不负责什么
- 不负责处理玩家掉入深渊的逻辑（由上层系统处理）
- 不负责怪物在平台上的生成策略（由上层逻辑处理）
- 不负责平台的具体游戏机制（仅提供基础的地形生成）

## 3. 结构总览

### 主要成员概览
- 无实例字段
- 无静态常量
- private splitPlatforms() 方法：递归分割平台区域

### 主要逻辑块概览
- minWidth() 方法：确保房间最小宽度为6格
- minHeight() 方法：确保房间最小高度为6格  
- sizeCatProbs() 方法：定义房间尺寸类别的概率分布 [6, 3, 1]
- merge() 方法：处理与其他房间合并时的地形选择
- paint() 方法：核心绘制逻辑，创建深渊和平台地形
- splitPlatforms() 方法：递归算法将大平台分割成多个小平台并添加连接桥

### 生命周期/调用时机
该类实例在关卡生成过程中被 RoomFactory 创建，paint() 方法在关卡绘制阶段被调用。

## 4. 继承与协作关系

### 父类提供的能力
- width() / height() 方法：获取房间尺寸
- left / right / top / bottom 字段：房间边界坐标
- connected 字段：连接的门信息
- sizeCat 字段：房间尺寸类别

### 覆写的方法
- minWidth()：覆写自 Room
- minHeight()：覆写自 Room  
- sizeCatProbs()：覆写自 Room
- merge()：覆写自 Room
- paint()：覆写自 Room

### 实现的接口契约
无

### 依赖的关键类
- Level：关卡数据结构
- Terrain：地形类型定义（WALL, CHASM, EMPTY_SP）
- Painter：关卡绘制工具
- Room：房间基类
- Rect：矩形区域工具类
- Random：随机数生成器
- ArrayList：平台列表存储
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

**返回值**：int，最小宽度值（至少为6）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(super.minWidth(), 6);
```
确保房间宽度至少为6格，以容纳基本的平台布局。

**边界情况**：当父类返回值小于6时，返回6；否则返回父类值。

### minHeight()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：返回房间的最小高度要求

**参数**：无

**返回值**：int，最小高度值（至少为6）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(super.minHeight(), 6);
```
确保房间高度至少为6格，以容纳基本的平台布局。

**边界情况**：当父类返回值小于6时，返回6；否则返回父类值。

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：定义不同尺寸类别房间的生成概率

**参数**：无

**返回值**：float[]，包含三个概率值 [6, 3, 1]

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定数组 new float[]{6, 3, 1}，表示小、中、大三种尺寸类别的相对概率。

**边界情况**：无

### merge()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：处理与其他房间合并时的地形选择逻辑

**参数**：
- l (Level)：当前关卡对象
- other (Room)：要合并的另一个房间
- merge (Rect)：合并区域
- mergeTerrain (int)：合并地形类型

**返回值**：void

**前置条件**：两个房间相邻且需要合并

**副作用**：修改关卡地形数据

**核心实现逻辑**：
- 如果合并地形不是 CHASM（深渊），当前房间与另一个房间有连接，并且另一个房间是 PlatformRoom 或 ChasmRoom，则：
  - 使用 CHASM 地形进行合并
  - 将连接的门位置设置为 EMPTY_SP（特殊空地）地形
- 否则使用父类的默认合并逻辑

**边界情况**：确保平台房间与深渊房间合并时保持深渊地形的一致性。

### paint()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制房间的实际布局，包括墙壁、深渊和平台

**参数**：
- level (Level)：当前关卡对象

**返回值**：void

**前置条件**：房间边界已正确设置，connected 字典包含连接的门

**副作用**：修改 level 对象的地形数据

**核心实现逻辑**：
1. 首先填充整个房间区域为墙壁地形 (Terrain.WALL)
2. 在距离边界1格的内部区域填充为深渊地形 (Terrain.CHASM)
3. 创建一个矩形区域作为初始平台（距离边界2格的内部区域）
4. 调用 splitPlatforms() 方法递归分割平台并生成连接桥
5. 遍历所有生成的平台，在对应位置填充为 EMPTY_SP（特殊空地）地形
6. 将所有连接的门设置为常规门类型 (Door.Type.REGULAR)
7. 使用 Painter.drawInside() 在门内侧2格范围内绘制 EMPTY_SP 地形，确保门通道连通

**边界情况**：
- 最小房间尺寸为6x6，确保至少能容纳基本平台布局
- 平台分割算法确保不会产生过小的无效平台
- 门通道始终连通到主平台区域

### splitPlatforms()

**可见性**：private

**是否覆写**：否

**方法职责**：递归分割平台区域，根据面积决定是否继续分割并添加连接桥

**参数**：
- curPlatform (Rect)：当前要处理的平台区域
- allPlatforms (ArrayList<Rect>)：存储所有最终平台的列表

**返回值**：void

**前置条件**：curPlatform 是有效的矩形区域

**副作用**：修改 allPlatforms 列表

**核心实现逻辑**：
1. 计算当前平台面积 = (width+1) * (height+1)
2. 根据面积计算分割概率：面积在25-36之间时，分割概率从0%线性增长到100%
3. 如果决定分割：
   - 如果宽度≥高度：垂直分割
     - 随机选择分割X坐标（距离边界至少2格）
     - 递归处理左右两个子平台
     - 在随机Y位置添加水平桥（3格宽）
   - 否则：水平分割
     - 随机选择分割Y坐标（距离边界至少2格）
     - 递归处理上下两个子平台
     - 在随机X位置添加垂直桥（3格高）
4. 如果不分割：将当前平台添加到结果列表

**边界情况**：
- 分割坐标确保子平台至少有1格大小
- 桥的位置使用 NormalIntRange 确保在合理范围内
- 避免无限递归（面积越小分割概率越低）

## 8. 对外暴露能力

### 显式 API
- minWidth()
- minHeight() 
- sizeCatProbs()
- merge()
- paint()

### 内部辅助方法
- splitPlatforms()：仅供内部使用

### 扩展入口
该类未提供protected方法供子类扩展，如需自定义平台房间行为，应直接继承StandardRoom并实现自己的逻辑。

## 9. 运行机制与调用链

### 创建时机
在关卡生成过程中，当RoomFactory需要创建平台房间类型时实例化。

### 调用者
- RoomFactory.createRoom()：创建房间实例
- LevelGenerator.paint()：调用paint()方法绘制房间

### 被调用者
- Painter.fill() / Painter.drawInside()：填充和绘制地形
- Random.Float() / Random.Int() / Random.IntRange() / Random.NormalIntRange()：生成随机数
- Door.set()：设置门类型

### 系统流程位置
位于关卡生成流程的房间绘制阶段，在房间布局确定但具体地形未填充时执行。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| (无官方翻译) | 平台房间 | 房间类型的中文名称（项目内未找到官方对应译名） |

### 依赖的资源
- Terrain.WALL：墙壁地形
- Terrain.CHASM：深渊地形  
- Terrain.EMPTY_SP：特殊空地地形（平台表面）

### 中文翻译来源
在 levels_zh.properties 文件中未找到 PlatformRoom 的官方翻译，因此使用描述性翻译"平台房间"。

## 11. 使用示例

### 基本用法
```java
// 在关卡生成器中创建平台房间
PlatformRoom platformRoom = new PlatformRoom();
platformRoom.set(left, top, right, bottom); // 设置房间边界
platformRoom.paint(level); // 绘制房间到关卡
```

### 扩展示例
由于该类没有提供扩展点，如需自定义行为，建议参考其实现创建新的房间类型：

```java
public class CustomPlatformRoom extends StandardRoom {
    @Override
    public void paint(Level level) {
        // 自定义平台生成逻辑
        super.paint(level);
        // 添加额外逻辑
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖房间的边界坐标（left, right, top, bottom）已正确设置
- 依赖 connected 字典包含有效的门连接信息
- splitPlatforms() 方法依赖 Rect 对象的正确初始化

### 生命周期耦合
- 必须在房间边界设置完成后调用 paint() 方法
- paint() 方法应在关卡地形数组初始化后调用

### 常见陷阱
- 修改 minWidth()/minHeight() 返回值时需确保仍能满足平台布局的最小空间需求
- 直接修改 splitPlatforms() 算法时需注意避免产生无效的小平台或断开的桥
- 门通道的 EMPTY_SP 绘制逻辑不应随意修改，以免导致房间无法进入

## 13. 修改建议与扩展点

### 适合扩展的位置
- 如需不同的平台分割策略，可修改 splitPlatforms() 方法的分割逻辑
- 如需不同的平台最小尺寸，可调整分割坐标的约束条件
- 可考虑添加配置选项来控制平台密度或桥的数量

### 不建议修改的位置
- 现有的深渊/平台层级结构不应随意修改，以免破坏游戏机制
- 最小尺寸限制（6格）不应降低，否则可能导致布局错误
- 门通道的连通性逻辑不应移除，否则会导致房间无法访问

### 重构建议
splitPlatforms() 方法的递归逻辑已经相当清晰，但如果需要更复杂的平台生成策略，可以考虑将其重构为独立的平台生成器类。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（未找到官方翻译，使用描述性名称）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点