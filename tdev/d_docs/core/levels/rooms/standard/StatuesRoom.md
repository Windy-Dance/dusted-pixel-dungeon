# StatuesRoom 文档

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
StatuesRoom 类负责生成包含多个雕像平台的房间布局。它将房间内部划分为规则的网格，每个网格单元包含一个由特殊雕像（STATUE_SP）装饰的平台，并在足够大的平台上放置火焰基座（REGION_DECO_ALT）作为中心装饰。

### 系统定位
作为标准房间（StandardRoom）的一种具体实现，StatuesRoom 在关卡生成过程中提供具有多平台雕像装饰的复杂房间类型，增加关卡的视觉层次感和探索价值。

### 不负责什么
- 不负责雕像的具体生成逻辑或交互行为
- 不处理特殊的敌人生成规则
- 不管理物品放置的特殊规则
- 不负责平台布局算法的底层数学计算

## 3. 结构总览

### 主要成员概览
- 无额外字段（继承自父类的字段包括 sizeCat 等）

### 主要逻辑块概览
- 尺寸约束逻辑：确保最小尺寸为7x7
- 尺寸分类概率：定义 NORMAL/LARGE/GIANT 的出现概率为 9:3:1
- 网格计算：基于房间尺寸计算行数、列数和平台间距
- 平台生成：创建多个 EMPTY_SP 平台，四角放置 STATUE_SP 雕像
- 中心装饰：在足够大的平台（≥5x5）中心放置 REGION_DECO_ALT 火焰基座

### 生命周期/调用时机
- 房间实例化时自动设置尺寸分类
- 关卡绘制阶段调用 paint() 方法生成实际地形和装饰

## 4. 继承与协作关系

### 父类提供的能力
从 StandardRoom 继承：
- SizeCategory 枚举和相关字段
- minWidth()/minHeight() 基础实现
- setSizeCat() 相关方法
- sizeFactor(), mobSpawnWeight(), connectionWeight() 等辅助方法
- canMerge() 实现

From Room 继承：
- 空间和连接逻辑（Rect 功能扩展）
- paint() 抽象方法
- Door 内部类

### 覆写的方法
- minWidth()：返回 Math.max(7, super.minWidth())
- minHeight()：返回 Math.max(7, super.minHeight())
- sizeCatProbs()：返回 new float[]{9, 3, 1}
- paint()：实现雕像平台房间的具体绘制逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 间接实现）
- Bundlable 接口（通过 Room 间接实现）

### 依赖的关键类
- Level：关卡数据结构
- Terrain：地形类型定义（WALL, EMPTY, EMPTY_SP, STATUE_SP, REGION_DECO_ALT）
- Painter：关卡绘制工具（fill(), set()）
- Random：随机数生成（Int()）

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

**方法职责**：确保 StatuesRoom 的最小宽度至少为7格

**参数**：无

**返回值**：int，返回父类 minWidth() 和 7 的较大值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(7, super.minWidth());
```

**边界情况**：当父类 minWidth() 返回值小于7时，强制返回7

### minHeight()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：确保 StatuesRoom 的最小高度至少为7格

**参数**：无

**返回值**：int，返回父类 minHeight() 和 7 的较大值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(7, super.minHeight());
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

**方法职责**：在关卡中实际绘制 StatuesRoom 的多平台雕像地形

**参数**：
- `level` (Level)：要绘制到的关卡实例

**返回值**：void

**前置条件**：房间必须已经设置了有效的位置和尺寸

**副作用**：修改 level 对象的 map 数组

**核心实现逻辑**：
1. 使用 Painter.fill() 填充整个房间为墙壁 (Terrain.WALL)
2. 填充内部1格区域为空地 (Terrain.EMPTY)
3. 设置所有连接门为常规门类型
4. 计算网格布局参数：
   - 行数 rows = (width() + 1) / 6
   - 列数 cols = (height() + 1) / 6
   - 平台宽度 w = (width() - 4 - (rows-1)) / rows
   - 平台高度 h = (height() - 4 - (cols-1)) / cols
   - 水平间距 Wspacing：根据奇偶性决定为1或2
   - 垂直间距 Hspacing：根据奇偶性决定为1或2
5. 遍历每个网格单元：
   - 计算平台位置（距离房间边缘2格开始）
   - 使用 Painter.fill() 填充平台区域为 EMPTY_SP
   - 在平台四角设置 STATUE_SP 雕像
   - 如果平台尺寸≥5x5，在中心随机位置放置 REGION_DECO_ALT 火焰基座

**边界情况**：
- 最小房间尺寸7x7确保至少能容纳一个平台
- 平台尺寸计算确保不会重叠且保持适当间距
- 中心装饰的随机偏移确保在偶数尺寸平台上的一致性

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是 Room/StandardRoom 协议的一部分
- 没有额外的公共 API

### 内部辅助方法
- 无内部辅助方法，所有逻辑在 paint() 中实现

### 扩展入口
- 可以通过继承调整网格计算逻辑
- 可以覆写其他 StandardRoom 方法来自定义行为

## 9. 运行机制与调用链

### 创建时机
- 在关卡生成过程中的房间创建阶段
- 通过 StandardRoom.createRoom() 静态工厂方法随机选择创建

### 调用者
- StandardRoom.createRoom()：通过反射创建实例
- LevelGenerator：在构建房间网络时调用 paint()

### 被调用者
- Painter.fill()/set()：用于地形绘制
- Random.Int()：用于中心装饰的随机偏移
- super 方法：获取父各基础功能

### 系统流程位置
1. 房间实例创建 → 2. 尺寸设置 → 3. 房间网络构建 → 4. paint() 调用 → 5. 网格计算 → 6. 平台生成 → 7. 装饰放置

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
项目内未找到官方对应译名

### 依赖的资源
- Terrain 常量：WALL, EMPTY, EMPTY_SP, STATUE_SP, REGION_DECO_ALT
- STATUE_SP 可能是特殊雕像地形，具有特定视觉效果
- REGION_DECO_ALT 可能显示火焰或其他装饰效果

### 中文翻译来源
在 levels_zh.properties 中未找到 StatuesRoom 相关翻译

## 11. 使用示例

### 基本用法
```java
// StatuesRoom 通常由关卡生成器自动创建，不建议手动实例化
StatuesRoom room = new StatuesRoom();
// 设置房间位置和尺寸（至少7x7）
room.set(new Rect(10, 10, 20, 20));
// 在关卡中绘制
room.paint(level);
// 房间会自动生成多个雕像平台
```

### 扩展示例
如需自定义平台布局，可以继承 StatuesRoom：

```java
public class CustomStatuesRoom extends StatuesRoom {
    @Override
    public void paint(Level level) {
        // 自定义网格计算
        int rows = 2;
        int cols = 2;
        // ...自定义平台生成逻辑
        super.paint(level); // 或完全重写
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖于房间的 left/right/top/bottom 坐标已正确设置
- 依赖于 Terrain 常量的存在和正确行为
- 网格计算依赖于房间尺寸的准确值

### 生命周期耦合
- 必须在房间位置和尺寸确定后才能调用 paint()
- paint() 调用后所有地形即固定
- 平台布局一旦生成就不可修改

### 常见陷阱
- 修改网格计算逻辑时需确保平台不会重叠或超出房间边界
- 平台间距的奇偶性处理确保了在不同尺寸房间中的一致布局
- 中心装饰的随机偏移仅在偶数尺寸平台上生效，避免不对称
- 最小尺寸7x7是单平台的基本要求，更小的房间无法正常工作

## 13. 修改建议与扩展点

### 适合扩展的位置
- 网格计算逻辑：调整行/列数或平台尺寸
- 装饰类型：使用不同的地形类型替代 STATUE_SP 或 REGION_DECO_ALT
- 平台形状：创建非矩形平台
- 中心装饰条件：调整触发中心装饰的平台尺寸阈值

### 不建议修改的位置
- 基础房间结构（WALL → EMPTY）
- 门类型设置（确保正确的连接行为）
- 平台四角雕像的基本布局（这是雕像房间的核心特征）

### 重构建议
当前实现较为清晰，但如果需要更多自定义选项，可以考虑：
1. 将网格参数提取为可配置常量
2. 将装饰类型作为参数传递
3. 添加更多的平台变体

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点