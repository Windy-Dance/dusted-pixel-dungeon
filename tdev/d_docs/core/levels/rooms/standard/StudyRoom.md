# StudyRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\StudyRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 91 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
StudyRoom 类负责生成书房主题的房间布局。它创建多层书架结构（BOOKSHELF），内部为特殊空地（EMPTY_SP），并在房间中心放置基座（PEDESTAL）和随机奖励物品（奖品物品或随机药剂/卷轴）。

### 系统定位
作为标准房间（StandardRoom）的一种具体实现，StudyRoom 在关卡生成过程中提供具有书房主题的特殊房间，增加关卡的探索价值和视觉主题性。

### 不负责什么
- 不负责书架物品的具体生成逻辑
- 不处理特殊的敌人生成规则
- 不管理房间连接的特殊逻辑
- 不负责 LARGE 尺寸的完整支持（代码中有 TODO 注释）

## 3. 结构总览

### 主要成员概览
- 无额外字段（继承自父类的字段包括 sizeCat 等）

### 主要逻辑块概览
- 尺寸约束逻辑：确保最小尺寸为7x7
- 尺寸分类概率：定义 NORMAL/LARGE 的出现概率为 2:1（GIANT 为0）
- 多层地形绘制：WALL → BOOKSHELF → EMPTY_SP
- 门通道处理：确保门周围有足够的安全通道
- LARGE 尺寸特殊处理：在四个角落添加书架支柱
- 中心奖励放置：50%概率放置奖品物品，否则放置随机药剂或卷轴

### 生命周期/调用时机
- 房间实例化时自动设置尺寸分类（仅 NORMAL 或 LARGE）
- 关卡绘制阶段调用 paint() 方法生成实际地形和奖励

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
- minWidth()：返回 Math.max(super.minWidth(), 7)
- minHeight()：返回 Math.max(super.minHeight(), 7)
- sizeCatProbs()：返回 new float[]{2, 1, 0}
- paint()：实现书房房间的具体绘制逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 间接实现）
- Bundlable 接口（通过 Room 间接实现）

### 依赖的关键类
- Level：关卡数据结构和 findPrizeItem() 方法
- Terrain：地形类型定义（WALL, BOOKSHELF, EMPTY_SP, PEDESTAL）
- Painter：关卡绘制工具（fill(), drawInside(), set()）
- Generator：随机物品生成器
- Item：物品基类
- Random：随机数生成
- Point：几何计算

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
- LARGE 尺寸支持不完整（有 TODO 注释）

## 7. 方法详解

### minWidth()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：确保 StudyRoom 的最小宽度至少为7格

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

**方法职责**：确保 StudyRoom 的最小高度至少为7格

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

**方法职责**：定义不同尺寸分类的出现概率，禁用 GIANT 尺寸

**参数**：无

**返回值**：float[]，包含三个元素 [2, 1, 0] 分别对应 NORMAL/LARGE/GIANT

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return new float[]{2, 1, 0};
```

**边界情况**：GIANT 尺寸概率为0，确保不会生成超大书房房间

### paint(Level level)
**可见性**：public

**是否覆写**：是，覆写自 Room 抽象方法

**方法职责**：在关卡中实际绘制 StudyRoom 的书房地形和中心奖励

**参数**：
- `level` (Level)：要绘制到的关卡实例

**返回值**：void

**前置条件**：房间必须已经设置了有效的位置和尺寸

**副作用**：修改 level 对象的 map 数组并放置奖励物品

**核心实现逻辑**：
1. 使用 Painter.fill() 填充整个房间为墙壁 (Terrain.WALL)
2. 填充内部1格区域为书架 (Terrain.BOOKSHELF)
3. 填充内部2格区域为特殊空地 (Terrain.EMPTY_SP)
4. 设置所有连接门为常规门类型，并使用 drawInside() 确保门周围2格内为 EMPTY_SP
5. 如果是 LARGE 尺寸：
   - 计算支柱尺寸：pillarW = (width()-7)/2, pillarH = (height()-7)/2
   - 在四个角落添加书架支柱（水平和垂直书架线）
6. 获取房间中心点，设置为基座 (Terrain.PEDESTAL)
7. 50%概率尝试放置奖品物品（level.findPrizeItem()）
8. 如果没有奖品物品，则随机选择药剂或卷轴类别
9. 使用 Generator.random() 生成具体物品并放置在中心

**边界情况**：
- 最小房间尺寸7x7确保有足够的空间放置完整结构
- LARGE 尺寸的支柱计算确保不会重叠或超出边界
- 奖励物品放置逻辑确保总有有效物品生成

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是 Room/StandardRoom 协议的一部分
- 没有额外的公共 API

### 内部辅助方法
- 无内部辅助方法，所有逻辑在 paint() 中实现

### 扩展入口
- 可以通过继承自定义奖励放置逻辑
- 可以覆写其他 StandardRoom 方法来自定义行为

## 9. 运行机制与调用链

### 创建时机
- 在关卡生成过程中的房间创建阶段
- 通过 StandardRoom.createRoom() 静态工厂方法随机选择创建（仅 NORMAL/LARGE）

### 调用者
- StandardRoom.createRoom()：通过反射创建实例
- LevelGenerator：在构建房间网络时调用 paint()

### 被调用者
- Painter.fill()/drawInside()/set()：用于地形绘制
- Level.findPrizeItem()/drop()：用于放置奖励物品
- Generator.random()：生成随机物品
- Random.Int()/oneOf()：用于随机决策
- super 方法：获取父类基础功能

### 系统流程位置
1. 房间实例创建 → 2. 尺寸设置 → 3. 房间网络构建 → 4. paint() 调用 → 5. 书房结构生成 → 6. 奖励放置

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
项目内未找到官方对应译名

### 依赖的资源
- Terrain 常量：WALL, BOOKSHELF, EMPTY_SP, PEDESTAL
- Generator 配置：POTION 和 SCROLL 类别的物品生成
- LEVEL.findPrizeItem()：奖品物品生成系统
- BOOKSHELF 应该是图书馆特有的地形类型

### 中文翻译来源
在 levels_zh.properties 中未找到 StudyRoom 相关翻译

## 11. 使用示例

### 基本用法
```java
// StudyRoom 通常由关卡生成器自动创建，不建议手动实例化
StudyRoom room = new StudyRoom();
// 设置房间位置和尺寸（至少7x7）
room.set(new Rect(10, 10, 20, 20));
// 在关卡中绘制
room.paint(level);
// 房间会自动生成书房布局和中心奖励
```

### 扩展示例
如需自定义奖励逻辑，可以继承 StudyRoom：

```java
public class CustomStudyRoom extends StudyRoom {
    @Override
    public void paint(Level level) {
        // 自定义书房结构
        super.paint(level);
        // 或完全重写奖励放置逻辑
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖于房间的 left/right/top/bottom 坐标已正确设置
- 依赖于 Level.findPrizeItem() 和 Generator 的正确实现
- 依赖于 Terrain 常量的存在和正确行为

### 生命周期耦合
- 必须在房间位置和尺寸确定后才能调用 paint()
- paint() 调用后所有地形和奖励即固定
- 奖励物品放置必须在关卡完全初始化后进行

### 常见陷阱
- LARGE 尺寸支持不完整（有 TODO 注释），可能需要进一步开发
- 修改奖励逻辑时需确保物品系统的兼容性
- 门周围的安全通道确保了玩家可以正常进出

## 13. 修改建议与扩展点

### 适合扩展的位置
- 奖励放置逻辑：自定义物品类型和概率
- LARGE 尺寸支持：完善 GIANT 尺寸和更复杂的 LARGE 布局
- 书房装饰：添加更多书架变体或装饰元素
- 尺寸分类：调整 NORMAL/LARGE 的比例

### 不建议修改的位置
- 基础多层地形绘制逻辑（WALL → BOOKSHELF → EMPTY_SP）
- 门周围的 EMPTY_SP 绘制（确保玩家能安全进出）
- 中心基座的基本概念（这是书房房间的核心特征）

### 重构建议
根据 TODO 注释，需要完善 GIANT 尺寸支持：
1. 添加 GIANT 尺寸的支柱布局
2. 考虑更复杂的多层书架结构
3. 可能需要调整奖励放置逻辑以适应更大房间

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点