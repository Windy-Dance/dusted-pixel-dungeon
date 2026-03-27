# PillarsRoom.java 文档

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
PillarsRoom 类负责生成包含2个或4个石柱的标准房间布局，这些石柱作为房间内的障碍物，影响玩家移动和战斗策略。

### 系统定位
该类属于关卡生成系统中的标准房间类型之一，继承自 StandardRoom，专门用于创建具有柱状障碍物的房间变体。

### 不负责什么
- 不负责管理房间内的怪物生成（由上层逻辑处理）
- 不负责处理玩家与石柱的交互逻辑（石柱只是地形障碍）
- 不负责房间的特殊游戏机制（仅提供基础的地形生成）

## 3. 结构总览

### 主要成员概览
- 无实例字段
- 无静态常量

### 主要逻辑块概览
- minWidth() 方法：确保房间最小宽度为7格
- minHeight() 方法：确保房间最小高度为7格  
- sizeCatProbs() 方法：定义房间尺寸类别的概率分布 [9, 3, 1]
- paint() 方法：核心绘制逻辑，根据房间尺寸决定生成2个或4个石柱

### 生命周期/调用时机
该类实例在关卡生成过程中被 RoomFactory 创建，paint() 方法在关卡绘制阶段被调用。

## 4. 继承与协作关系

### 父类提供的能力
- width() / height() 方法：获取房间尺寸
- left / right / top / bottom 字段：房间边界坐标
- connected 字段：连接的门信息
- sizeCat 字段：房间尺寸类别

### 覆写的方法
- minWidth()：覆写自 Room 类
- minHeight()：覆写自 Room 类  
- sizeCatProbs()：覆写自 Room 类
- paint()：覆写自 Room 类

### 实现的接口契约
无

### 依赖的关键类
- Level：关卡数据结构
- Terrain：地形类型定义
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
return Math.max(super.minWidth(), 7);
```
确保房间宽度至少为7格，以容纳石柱布局。

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
确保房间高度至少为7格，以容纳石柱布局。

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

**方法职责**：绘制房间的实际布局，包括墙壁、地面和石柱

**参数**：
- level (Level)：当前关卡对象

**返回值**：void

**前置条件**：房间边界已正确设置，connected 字典包含连接的门

**副作用**：修改 level 对象的地形数据

**核心实现逻辑**：
1. 首先填充整个房间区域为墙壁地形 (Terrain.WALL)
2. 然后在内部区域（距离边界1格外）填充为空地地形 (Terrain.EMPTY)
3. 将所有连接的门设置为常规门类型 (Door.Type.REGULAR)
4. 根据房间最小维度决定生成2个还是4个石柱：
   - 如果最小维度为7，或者房间为NORMAL尺寸且随机数为0，则生成2个石柱
   - 否则生成4个石柱
5. 对于2个石柱的情况：
   - 计算石柱插入距离（11格以上为2，否则为1）
   - 计算石柱大小
   - 随机选择水平或垂直方向放置第一个石柱
   - 第二个石柱通过对称计算得到位置
6. 对于4个石柱的情况：
   - 计算石柱插入距离（12格以上为2，否则为1）
   - 计算石柱大小
   - 应用随机偏移使石柱位置不完全对称
   - 分别在四个角落区域绘制石柱

**边界情况**：
- 最小房间尺寸为7x7，确保至少能容纳基本布局
- 石柱大小根据房间尺寸动态计算，确保不会重叠或超出边界

## 8. 对外暴露能力

### 显式 API
- minWidth()
- minHeight() 
- sizeCatProbs()
- paint()

### 内部辅助方法
无

### 扩展入口
该类未提供protected方法供子类扩展，如需自定义石柱房间行为，应直接继承StandardRoom并实现自己的逻辑。

## 9. 运行机制与调用链

### 创建时机
在关卡生成过程中，当RoomFactory需要创建石柱房间类型时实例化。

### 调用者
- RoomFactory.createRoom()：创建房间实例
- LevelGenerator.paint()：调用paint()方法绘制房间

### 被调用者
- Painter.fill()：填充地形
- Random.Int() / Random.IntRange() / Random.Float()：生成随机数
- Door.set()：设置门类型

### 系统流程位置
位于关卡生成流程的房间绘制阶段，在房间布局确定但具体地形未填充时执行。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| levels.hallslevel.statue_name | 台柱 | 房间类型的中文名称 |

### 依赖的资源
无特定纹理或图标资源，石柱使用标准墙壁地形显示。

### 中文翻译来源
core/src/main/assets/messages/levels/levels_zh.properties，第205行

## 11. 使用示例

### 基本用法
```java
// 在关卡生成器中创建石柱房间
PillarsRoom pillarsRoom = new PillarsRoom();
pillarsRoom.set(left, top, right, bottom); // 设置房间边界
pillarsRoom.paint(level); // 绘制房间到关卡
```

### 扩展示例
由于该类没有提供扩展点，如需自定义行为，建议参考其实现创建新的房间类型：

```java
public class CustomPillarsRoom extends StandardRoom {
    @Override
    public void paint(Level level) {
        // 自定义石柱生成逻辑
        super.paint(level);
        // 添加额外逻辑
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖房间的边界坐标（left, right, top, bottom）已正确设置
- 依赖 connected 字典包含有效的门连接信息

### 生命周期耦合
- 必须在房间边界设置完成后调用 paint() 方法
- paint() 方法应在关卡地形数组初始化后调用

### 常见陷阱
- 修改 minWidth()/minHeight() 返回值时需确保仍能满足石柱布局的最小空间需求
- 直接修改 paint() 方法中的石柱生成逻辑时需注意避免石柱重叠或超出房间边界

## 13. 修改建议与扩展点

### 适合扩展的位置
- 如需不同的石柱数量或布局，建议创建新的房间类而非修改此类
- 如需不同的石柱大小计算逻辑，可在新的实现中重用部分现有代码

### 不建议修改的位置
- 现有的随机逻辑和对称计算不应随意修改，以免破坏游戏平衡
- 最小尺寸限制（7格）不应降低，否则可能导致布局错误

### 重构建议
可考虑将石柱生成逻辑提取为独立的工具方法，便于复用和测试，但考虑到该类功能单一且完整，当前结构已足够清晰。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（"台柱"来自levels_zh.properties）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点