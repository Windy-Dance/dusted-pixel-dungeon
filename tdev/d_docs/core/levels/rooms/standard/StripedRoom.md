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
StripedRoom 类负责生成条纹主题的房间布局。它根据房间尺寸分类（NORMAL/LARGE）采用不同的条纹模式：NORMAL 尺寸使用平行条纹（水平或垂直），LARGE 尺寸使用同心环形条纹，交替使用 EMPTY_SP 和 HIGH_GRASS 地形。

### 系统定位
作为标准房间（StandardRoom）的一种具体实现，StripedRoom 在关卡生成过程中提供具有条纹装饰主题的房间类型，增加关卡的视觉多样性和主题氛围。

### 不负责什么
- 不处理 GIANT 尺寸（概率为0）
- 不负责特殊的敌人生成规则
- 不管理物品放置的特殊规则
- 不负责条纹地形的具体交互逻辑

## 3. 结构总览

### 主要成员概览
- 无额外字段（继承自父类的字段包括 sizeCat 等）

### 主要逻辑块概览
- 尺寸分类概率：定义 NORMAL/LARGE 的出现概率为 2:1（GIANT 为0）
- 合并逻辑：与相同类型的 StripedRoom 合并时使用 EMPTY_SP 地形
- 双模式条纹生成：
  - NORMAL 模式：平行条纹（间隔为2格）
  - LARGE 模式：同心环形条纹（交替地形）

### 生命周期/调用时机
- 房间实例化时自动设置尺寸分类（仅 NORMAL 或 LARGE）
- 关卡绘制阶段调用 paint() 方法生成实际地形
- 房间合并时调用 merge() 方法处理连接区域

## 4. 继承与协作关系

### 父类提供的能力
从 StandardRoom 继承：
- SizeCategory 枚举和相关字段
- setSizeCat() 相关方法
- sizeFactor(), mobSpawnWeight(), connectionWeight() 等辅助方法

From Room 继承：
- 空间和连接逻辑（Rect 功能扩展）
- paint() 抽象方法
- Door 内部类

### 覆写的方法
- sizeCatProbs()：返回 new float[]{2, 1, 0}
- merge()：自定义合并逻辑，相同类型房间合并使用 EMPTY_SP 地形
- paint()：实现条纹房间的具体绘制逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 间接实现）
- Bundlable 接口（通过 Room 间接实现）

### 依赖的关键类
- Level：关卡数据结构
- Terrain：地形类型定义（WALL, EMPTY_SP, HIGH_GRASS）
- Painter：关卡绘制工具（fill()）
- Random：随机数生成（Int()）
- Rect：矩形区域表示

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
- GIANT 尺寸概率为0，因此只会生成 NORMAL 或 LARGE 房间

## 7. 方法详解

### sizeCatProbs()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：定义不同尺寸分类的出现概率，禁用 GIANT 尺寸

**参数**：无

**返回值**：float[]，包含三个元素 [2, 1, 0] 分别对应 NORMAL/LARGE/GIANT

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return new float[]{2, 1, 0};
```

**边界情况**：GIANT 尺寸概率为0，确保不会生成超大条纹房间

### merge(Level l, Room other, Rect merge, int mergeTerrain)
**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：自定义房间合并逻辑，当与相同类型的 StripedRoom 连接时使用 EMPTY_SP 地形

**参数**：
- `l` (Level)：关卡实例
- `other` (Room)：要合并的其他房间
- `merge` (Rect)：合并区域
- `mergeTerrain` (int)：原始合并地形类型

**返回值**：void

**前置条件**：房间必须已连接且合并地形为空地

**副作用**：修改关卡地形

**核心实现逻辑**：
- 如果对方也是 StripedRoom 且合并地形为空地，则使用 EMPTY_SP 地形合并
- 否则使用父类默认合并逻辑

**边界情况**：仅在特定条件下改变合并地形类型

### paint(Level level)
**可见性**：public

**是否覆写**：是，覆写自 Room 抽象方法

**方法职责**：在关卡中实际绘制 StripedRoom 的条纹地形

**参数**：
- `level` (Level)：要绘制到的关卡实例

**返回值**：void

**前置条件**：房间必须已经设置了有效的位置和尺寸

**副作用**：修改 level 对象的 map 数组

**核心实现逻辑**：
1. 使用 Painter.fill() 填充整个房间为墙壁 (Terrain.WALL)
2. 设置所有连接门为常规门类型
3. 根据尺寸分类选择条纹模式：
   - **NORMAL 模式**：
     - 填充内部1格区域为 EMPTY_SP
     - 基于房间长宽比和随机因素选择条纹方向：
       - 水平条纹（宽度>高度或随机选择）：每隔2格绘制垂直 HIGH_GRASS 条纹
       - 垂直条纹（高度>宽度）：每隔2格绘制水平 HIGH_GRASS 条纹
   - **LARGE 模式**：
     - 计算层数：layers = (min(width, height) - 1) / 2
     - 从内到外逐层填充，奇数层为 EMPTY_SP，偶数层为 HIGH_GRASS

**边界情况**：
- GIANT 尺寸不会出现（概率为0）
- 条纹间隔固定为2格，确保视觉效果清晰
- LARGE 模式的同心环从内向外交替，形成靶心效果

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是 Room/StandardRoom 协议的一部分
- 没有额外的公共 API

### 内部辅助方法
- 无内部辅助方法，所有逻辑在 paint() 中实现

### 扩展入口
- 可以通过继承调整条纹模式
- 可以覆写其他 StandardRoom 方法来自定义行为

## 9. 运行机制与调用链

### 创建时机
- 在关卡生成过程中的房间创建阶段
- 通过 StandardRoom.createRoom() 静态工厂方法随机选择创建（仅 NORMAL/LARGE）

### 调用者
- StandardRoom.createRoom()：通过反射创建实例
- LevelGenerator：在构建房间网络时调用 paint()
- 房间合并逻辑：调用 merge()

### 被调用者
- Painter.fill()：用于地形绘制
- Random.Int()：用于条纹方向选择
- super 方法：获取父类基础功能

### 系统流程位置
1. 房间实例创建 → 2. 尺寸设置 → 3. 房间网络构建 → 4. paint() 调用 → 5. 条纹生成 → 6. 房间合并处理

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
项目内未找到官方对应译名

### 依赖的资源
- Terrain 常量：WALL, EMPTY_SP, HIGH_GRASS
- EMPTY_SP 可能是特殊的空地类型，用于安全通行
- HIGH_GRASS 可能显示高草的视觉效果

### 中文翻译来源
在 levels_zh.properties 中未找到 StripedRoom 相关翻译

## 11. 使用示例

### 基本用法
```java
// StripedRoom 通常由关卡生成器自动创建，不建议手动实例化
StripedRoom room = new StripedRoom();
// 设置房间位置和尺寸
room.set(new Rect(10, 10, 20, 20));
// 在关卡中绘制
room.paint(level);
// 房间会自动生成条纹主题的地形
```

### 扩展示例
如需自定义条纹模式，可以继承 StripedRoom：

```java
public class CustomStripedRoom extends StripedRoom {
    @Override
    public void paint(Level level) {
        // 自定义条纹逻辑
        Painter.fill(level, this, Terrain.WALL);
        // ...自定义条纹生成
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖于房间的 left/right/top/bottom 坐标已正确设置
- 依赖于 sizeCat 字段的正确值
- 依赖于 Terrain 常量的存在和正确行为

### 生命周期耦合
- 必须在房间位置和尺寸确定后才能调用 paint()
- paint() 调用后条纹布局即固定
- 合并逻辑依赖于正确的房间类型识别

### 常见陷阱
- 修改条纹间隔时需确保视觉效果仍然清晰
- LARGE 模式的层数计算需要考虑房间的最小维度
- 条纹方向选择的随机性确保了房间的多样性

## 13. 修改建议与扩展点

### 适合扩展的位置
- 条纹模式：添加更多条纹样式（对角线、网格等）
- 地形类型：使用不同的装饰地形
- 尺寸分类：调整 NORMAL/LARGE 的比例
- 合并逻辑：自定义不同类型房间的合并行为

### 不建议修改的位置
- 基础房间结构（WALL 外墙）
- 门类型设置（确保正确的连接行为）
- 条纹的基本间隔逻辑（影响视觉识别）

### 重构建议
当前实现较为简洁，但如果需要更多条纹变体，可以考虑：
1. 将条纹模式抽象为策略模式
2. 将地形类型作为可配置参数
3. 添加更多的尺寸分类支持

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点