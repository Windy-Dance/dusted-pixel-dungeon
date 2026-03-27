# StatuesRoom 文档

# StatuesRoom.java 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\StatuesRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 93 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
StatuesRoom 类负责生成具有多重雕像平台的标准房间布局，房间内创建网格状排列的雕像基座，每个基座四角放置特殊雕像地形，并在足够大的基座中心放置火焰基座装饰。

### 系统定位
该类属于关卡生成系统中的标准房间类型之一，继承自 StandardRoom，专门用于创建具有多重雕像装饰效果的房间变体。

### 不负责什么
- 不负责雕像地形的具体视觉表现（由渲染系统处理）
- 不负责雕像的交互逻辑或战斗机制（由上层系统处理）
- 不负责房间内的怪物生成策略（由上层逻辑处理）

## 3. 结构总览

### 主要成员概览
- 无实例字段
- 无静态常量

### 主要逻辑块概览
- minWidth()/minHeight() 方法：确保房间最小尺寸为7格
- sizeCatProbs() 方法：定义房间尺寸类别的概率分布 [9, 3, 1]
- paint() 方法：核心绘制逻辑，创建网格状雕像平台布局

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
- Terrain：地形类型定义（WALL, EMPTY, EMPTY_SP, STATUE_SP, REGION_DECO_ALT）
- Painter：关卡绘制工具
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
return Math.max(7, super.minWidth());
```
确保房间宽度至少为7格，以容纳至少一个完整的雕像平台（需要5x5空间加上边界）。

**边界情况**：当父类返回值小于7时，返回7；否则返回父类值.

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
return Math.max(7, super.minHeight());
```
确保房间高度至少为7格，以容纳至少一个完整的雕像平台。

**边界情况**：当父类返回值小于7时，返回7；否则返回父类值.

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

**方法职责**：绘制雕像房间的实际布局，包括网格状雕像平台和火焰基座

**参数**：
- level (Level)：当前关卡对象

**返回值**：void

**前置条件**：房间边界已正确设置，connected 字典包含连接的门

**副作用**：修改 level 对象的地形数据

**核心实现逻辑**：
1. 首先填充整个房间区域为墙壁地形 (Terrain.WALL)
2. 在距离边界1格的内部区域填充为空地地形 (Terrain.EMPTY)
3. 将所有连接的门设置为常规门类型 (Door.Type.REGULAR)
4. 计算网格布局参数：
   - 行数：rows = (width() + 1) / 6
   - 列数：cols = (height() + 1) / 6
   - 每个平台宽度：w = (width() - 4 - (rows-1)) / rows
   - 每个平台高度：h = (height() - 4 - (cols-1)) / cols
   - 水平间距：Wspacing = (rows % 2 == width() % 2) ? 2 : 1
   - 垂直间距：Hspacing = (cols % 2 == height() % 2) ? 2 : 1
5. 遍历每个网格位置：
   - 计算平台左上角坐标
   - 填充平台区域为 EMPTY_SP 地形（特殊空地）
   - 在平台四角设置 STATUE_SP 地形（特殊雕像）
   - 如果平台足够大（w ≥ 5 且 h ≥ 5）：
     - 计算平台中心坐标（考虑偶数尺寸的随机偏移）
     - 在中心位置放置 REGION_DECO_ALT 地形（火焰基座）

**边界情况**：
- 最小房间尺寸7x7确保至少能容纳一个平台
- 平台尺寸计算确保不会重叠或超出房间边界
- 中心位置的随机偏移处理偶数尺寸的对称问题
- 小平台（<5x5）不放置火焰基座，避免过于拥挤

## 8. 对外暴露能力

### 显式 API
- minWidth()
- minHeight()
- sizeCatProbs()
- paint()

### 内部辅助方法
无

### 扩展入口
该类没有提供 protected 方法供子类扩展，如需自定义雕像房间行为，应直接继承 StandardRoom 并实现自己的逻辑。

## 9. 运行机制与调用链

### 创建时机
在关卡生成过程中，当RoomFactory需要创建雕像房间类型时实例化。

### 调用者
- RoomFactory.createRoom()：创建房间实例
- LevelGenerator.paint()：调用 paint() 方法绘制房间

### 被调用者
- Painter.fill()：填充基础地形和平台
- Painter.set()：设置雕像和火焰基座
- Random.Int()：处理中心位置的随机偏移
- Door.set()：设置门类型

### 系统流程位置
位于关卡生成流程的房间绘制阶段，在房间布局确定但具体地形未填充时执行。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| levels.level.statue_name | 雕像 | 雕像地形名称 |
| levels.citylevel.statue_desc | 这尊雕像刻画出了一位摆出英勇姿态的矮人。 | 雕像描述（城市层级） |
| levels.hallslevel.statue_name | 台柱 | 雕像相关地形名称（大厅层级） |

### 依赖的资源
- Terrain.STATUE_SP：特殊雕像地形（平台四角）
- Terrain.EMPTY_SP：特殊空地地形（平台表面）
- Terrain.REGION_DECO_ALT：火焰基座地形（平台中心）
- Terrain.WALL/EMPTY：基础地形

### 中文翻译来源
core/src/main/assets/messages/levels/levels_zh.properties，第234、192、205行。使用"雕像房间"作为房间类型的中文名称，结合了房间主题和官方雕像翻译。

## 11. 使用示例

### 基本用法
```java
// 在关卡生成器中创建雕像房间
StatuesRoom statuesRoom = new StatuesRoom();
statuesRoom.set(left, top, right, bottom); // 设置房间边界
statuesRoom.paint(level); // 绘制房间到关卡
```

### 扩展示例
由于该类没有提供扩展点，如需自定义行为，建议参考其实现创建新的房间类型：

```java
public class CustomStatuesRoom extends StandardRoom {
    @Override
    public void paint(Level level) {
        // 自定义雕像生成逻辑
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);
        // 添加自定义网格布局
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖房间的边界坐标（left, right, top, bottom）已正确设置
- 依赖 connected 字典包含有效的门连接信息
- 网格计算依赖房间的精确尺寸

### 生命周期耦合
- 必须在房间边界设置完成后调用 paint() 方法
- paint() 方法应在关卡地形数组初始化后调用

### 常见陷阱
- 修改 minWidth()/minHeight() 返回值时需确保仍能满足单个平台的最小空间需求（7格）
- 直接修改网格计算公式时需注意避免平台重叠或超出边界
- 火焰基座的5x5限制是有意为之，防止在小平台上放置过多装饰

## 13. 修改建议与扩展点

### 适合扩展的位置
- 如需不同的网格密度，可调整平台计算公式中的除数（当前为6）
- 如需不同的平台装饰，可修改四角和中心使用的地形类型
- 可考虑添加动态平台内容，根据房间位置或其他因素变化

### 不建议修改的位置
- 最小尺寸限制（7格）不应降低，否则无法容纳完整平台
- 平台四角必须使用 STATUE_SP 以保持雕像主题的一致性
- 5x5的火焰基座限制不应随意移除，这是为了保持视觉平衡

### 重构建议
可以考虑将网格计算提取为独立的 protected 方法，便于维护和调整。但当前硬编码的参数已经形成了稳定的视觉模式，无需紧急重构。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（使用 levels_zh.properties 中的"雕像"翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点