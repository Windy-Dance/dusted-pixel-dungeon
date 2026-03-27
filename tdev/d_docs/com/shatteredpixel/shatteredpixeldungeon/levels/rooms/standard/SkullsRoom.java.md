# SkullsRoom 文档

# SkullsRoom.java 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\SkullsRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 65 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
SkullsRoom 类负责生成具有骷髅主题的标准房间布局，使用椭圆形的雕像（STATUE）环和墙壁环创建独特的同心圆结构，并从门位置延伸长走廊。

### 系统定位
该类属于关卡生成系统中的标准房间类型之一，继承自 StandardRoom，专门用于创建具有骷髅装饰效果的房间变体。

### 不负责什么
- 不负责雕像地形的具体视觉表现（由渲染系统处理）
- 不负责骷髅主题的怪物生成（由上层逻辑处理）
- 不负责房间内的特殊游戏机制（仅提供基础的地形生成）

## 3. 结构总览

### 主要成员概览
- 无实例字段
- 无静态常量

### 主要逻辑块概览
- minWidth()/minHeight() 方法：确保房间最小尺寸为7格
- sizeCatProbs() 方法：定义房间尺寸类别的概率分布 [0, 3, 1]（禁用小型房间）
- paint() 方法：核心绘制逻辑，创建椭圆形的多层同心结构

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
- Terrain：地形类型定义（WALL, EMPTY, STATUE）
- Painter：关卡绘制工具（特别使用 fillEllipse 方法）
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
确保房间宽度至少为7格，以容纳基本的多层椭圆结构。

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
return Math.max(7, super.minHeight());
```
确保房间高度至少为7格，以容纳基本的多层椭圆结构。

**边界情况**：当父类返回值小于7时，返回7；否则返回父类值.

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：定义不同尺寸类别房间的生成概率

**参数**：无

**返回值**：float[]，包含三个概率值 [0, 3, 1]

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定数组 new float[]{0, 3, 1}，表示小、中、大三种尺寸类别的相对概率，小型房间被完全禁用（概率为0）。

**边界情况**：只有中型和大型房间会被生成，确保有足够的空间进行多层结构。

### paint()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制骷髅房间的实际布局，包括多层椭圆结构和门通道

**参数**：
- level (Level)：当前关卡对象

**返回值**：void

**前置条件**：房间边界已正确设置，connected 字典包含连接的门

**副作用**：修改 level 对象的地形数据

**核心实现逻辑**：
1. 首先填充整个房间区域为墙壁地形 (Terrain.WALL)
2. 在距离边界2格的内部区域填充椭圆形空地 (Terrain.EMPTY)
3. 对每个连接的门：
   - 设置为常规门类型 (Door.Type.REGULAR)
   - 如果门在左右两侧（垂直门），向内绘制 width()/2 格的空地走廊
   - 如果门在上下两侧（水平门），向内绘制 height()/2 格的空地走廊
4. 在距离边界4格的内部区域填充椭圆形雕像地形 (Terrain.STATUE)
5. 在距离边界6格的内部区域重新填充为墙壁地形 (Terrain.WALL)，形成最内层的墙壁环

**边界情况**：
- 由于小型房间被禁用，房间至少为中等尺寸，确保多层椭圆结构不会重叠
- 门通道延伸到房间中心附近，确保良好的可达性
- 椭圆填充确保在非正方形房间中也能保持对称的视觉效果

## 8. 对外暴露能力

### 显式 API
- minWidth()
- minHeight()
- sizeCatProbs()
- paint()

### 内部辅助方法
无

### 扩展入口
该类没有提供 protected 方法供子类扩展，如需自定义骷髅房间行为，应直接继承 StandardRoom 并实现自己的逻辑。

## 9. 运行机制与调用链

### 创建时机
在关卡生成过程中，当RoomFactory需要创建骷髅房间类型时实例化。

### 调用者
- RoomFactory.createRoom()：创建房间实例
- LevelGenerator.paint()：调用 paint() 方法绘制房间

### 被调用者
- Painter.fill()：填充基础墙壁
- Painter.fillEllipse()：填充椭圆区域（关键特色方法）
- Painter.drawInside()：绘制门通道
- Door.set()：设置门类型

### 系统流程位置
位于关卡生成流程的房间绘制阶段，在房间布局确定但具体地形未填充时执行。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| levels.level.statue_name | 雕像 | 雕像地形名称 |
| levels.hallslevel.statue_desc | 这个柱也由货真价实的人形生物头骨垒成。酷毙了。 | 雕像描述（提到头骨/骷髅） |

### 依赖的资源
- Terrain.STATUE：雕像地形（用于骷髅装饰）
- Terrain.EMPTY：空地地形
- Terrain.WALL：墙壁地形
- Painter.fillEllipse()：椭圆填充工具方法

### 中文翻译来源
core/src/main/assets/messages/levels/levels_zh.properties，第234、207行。使用"骷髅房间"作为房间类型的中文名称，结合了房间名称中的"Skulls"含义和雕像地形的骷髅主题描述。

## 11. 使用示例

### 基本用法
```java
// 在关卡生成器中创建骷髅房间
SkullsRoom skullsRoom = new SkullsRoom();
skullsRoom.set(left, top, right, bottom); // 设置房间边界
skullsRoom.paint(level); // 绘制房间到关卡
```

### 扩展示例
由于该类没有提供扩展点，如需自定义行为，建议参考其实现创建新的房间类型：

```java
public class CustomSkullsRoom extends StandardRoom {
    @Override
    public void paint(Level level) {
        // 自定义骷髅生成逻辑
        Painter.fill(level, this, Terrain.WALL);
        Painter.fillEllipse(level, this, 2, Terrain.EMPTY);
        // 添加自定义多层结构
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖房间的边界坐标（left, right, top, bottom）已正确设置
- 依赖 connected 字典包含有效的门连接信息
- 椭圆填充效果依赖房间的宽高比例

### 生命周期耦合
- 必须在房间边界设置完成后调用 paint() 方法
- paint() 方法应在关卡地形数组初始化后调用

### 常见陷阱
- 修改 minWidth()/minHeight() 返回值时需确保仍能满足多层椭圆结构的最小空间需求（各层间距至少2格）
- 直接修改椭圆填充的参数时需注意避免层间重叠（2, 4, 6的间距是有意设计的）
- 小型房间禁用逻辑不应移除，因为小房间无法容纳完整的三层结构

## 13. 修改建议与扩展点

### 适合扩展的位置
- 如需不同的层数或间距，可调整 fillEllipse() 的参数
- 如需不同的地形组合，可修改各层使用的地形类型
- 可考虑添加动态层数，根据房间尺寸自动调整

### 不建议修改的位置
- 多层椭圆结构的核心设计（2-4-6间距）不应随意改变，这是房间的标志性特征
- 小型房间禁用逻辑不应移除，否则会导致布局错误
- 门通道延伸到房间中心的设计不应改变，否则会影响可达性

### 重构建议
可以考虑将椭圆参数提取为常量或配置字段，便于维护和调整。但当前硬编码的参数已经形成了稳定的视觉模式，无需紧急重构。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（使用 levels_zh.properties 中的"雕像"翻译和骷髅相关描述）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点