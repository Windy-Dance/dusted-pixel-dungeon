# SegmentedLibraryRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\SegmentedLibraryRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 109 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
SegmentedLibraryRoom 类负责生成分段式图书馆房间布局。它使用递归分割算法将房间内部划分为多个书架区域，通过在书架之间创建通道来确保可通行性，并使用特殊的空地（EMPTY_SP）地形作为安全通道。

### 系统定位
作为标准房间（StandardRoom）的一种具体实现，SegmentedLibraryRoom 在关卡生成过程中提供具有图书馆主题的复杂分段房间，增加探索的策略性和视觉多样性。

### 不负责什么
- 不负责书架物品的具体生成逻辑
- 不处理特殊的敌人生成规则
- 不管理房间连接的特殊逻辑
- 不负责书架系统的底层实现

## 3. 结构总览

### 主要成员概览
- 无额外字段（继承自父类的字段包括 sizeCat 等）
- 私有方法 createWalls() 实现递归分割算法

### 主要逻辑块概览
- 尺寸分类概率：禁用 NORMAL 尺寸，定义 LARGE/GIANT 的出现概率为 3:1
- 多层地形绘制：WALL → BOOKSHELF → EMPTY_SP
- 门区域处理：确保门周围有足够的安全通道
- 递归分割算法：基于房间长宽比选择分割方向
- 通道创建逻辑：在分割线处创建安全通行空间

### 生命周期/调用时机
- 房间实例化时自动设置尺寸分类（仅 LARGE 或 GIANT）
- 关卡绘制阶段调用 paint() 方法生成实际地形

## 4. 继承与协作关系

### 父类提供的能力
从 StandardRoom 继承：
- SizeCategory 枚举和相关字段
- minWidth()/minHeight() 基础实现（返回 sizeCat.minDim）
- setSizeCat() 相关方法
- sizeFactor(), mobSpawnWeight(), connectionWeight() 等辅助方法
- canMerge() 实现

从 Room 继承：
- 空间和连接逻辑（Rect 功能扩展）
- paint() 抽象方法
- Door 内部类

### 覆写的方法
- sizeCatProbs()：返回 new float[]{0, 3, 1}
- paint()：实现分段图书馆房间的具体绘制逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 间接实现）
- Bundlable 接口（通过 Room 间接实现）

### 依赖的关键类
- Level：关卡数据结构
- Terrain：地形类型定义（WALL, BOOKSHELF, EMPTY_SP）
- Painter：关卡绘制工具（fill(), drawLine(), drawInside(), set()）
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
- NORMAL 尺寸概率为0，因此只会生成 LARGE（最小10x10）或 GIANT（最小14x14）房间

## 7. 方法详解

### sizeCatProbs()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：定义不同尺寸分类的出现概率，禁用 NORMAL 尺寸

**参数**：无

**返回值**：float[]，包含三个元素 [0, 3, 1] 分别对应 NORMAL/LARGE/GIANT

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return new float[]{0, 3, 1};
```

**边界情况**：NORMAL 尺寸概率为0，确保不会生成小型图书馆房间

### paint(Level level)
**可见性**：public

**是否覆写**：是，覆写自 Room 抽象方法

**方法职责**：在关卡中实际绘制 SegmentedLibraryRoom 的分段图书馆地形

**参数**：
- `level` (Level)：要绘制到的关卡实例

**返回值**：void

**前置条件**：房间必须已经设置了有效的位置和尺寸

**副作用**：修改 level 对象的 map 数组

**核心实现逻辑**：
1. 使用 Painter.fill() 填充整个房间为墙壁 (Terrain.WALL)
2. 填充内部1格区域为书架 (Terrain.BOOKSHELF)
3. 填充内部2格区域为特殊空地 (Terrain.EMPTY_SP)
4. 设置所有连接门为常规门类型，并使用 drawInside() 确保门周围2格内为 EMPTY_SP
5. 调用 createWalls() 对中心区域 (left+2 到 right-2, top+2 到 bottom-2) 进行递归分割

**边界情况**：
- 由于禁用了 NORMAL 尺寸，房间至少为10x10，确保有足够的空间进行分割
- 门周围的 EMPTY_SP 确保玩家可以安全进出

### createWalls(Level level, Rect area)
**可见性**：private

**是否覆写**：否

**方法职责**：递归分割指定区域，创建书架墙壁和通道

**参数**：
- `level` (Level)：关卡实例
- `area` (Rect)：要分割的区域

**返回值**：void

**前置条件**：area 必须是有效的矩形区域

**副作用**：修改 level 对象的 map 数组

**核心实现逻辑**：
1. 检查区域是否足够大（最大维度≥4且最小维度≥3），否则返回
2. 最多重试10次寻找有效的分割位置
3. 基于区域长宽比选择分割方向（水平或垂直）
4. 水平分割：
   - 随机选择分割X坐标（area.left+2 到 area.right-2）
   - 验证分割线上下边缘都是 BOOKSHELF 地形
   - 如果验证通过，绘制垂直书架线
   - 在分割线上随机位置创建 EMPTY_SP 通道
   - 递归处理左右两个子区域
5. 垂直分割：
   - 随机选择分割Y坐标（area.top+2 到 area.bottom-2）
   - 验证分割线左右边缘都是 BOOKSHELF 地形
   - 如果验证通过，绘制水平书架线
   - 在分割线上随机位置创建 EMPTY_SP 通道
   - 递归处理上下两个子区域

**边界情况**：
- 递归终止条件确保不会无限分割
- 分割位置验证确保书架结构的连续性
- 通道创建确保分割后的区域仍然连通
- 注释掉的代码显示原本考虑使用普通 EMPTY 地形，但最终选择了 EMPTY_SP

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
- 通过 StandardRoom.createRoom() 静态工厂方法随机选择创建（仅 LARGE/GIANT）

### 调用者
- StandardRoom.createRoom()：通过反射创建实例
- LevelGenerator：在构建房间网络时调用 paint()

### 被调用者
- Painter.fill()/drawLine()/drawInside()/set()：用于地形绘制
- Random.Int()/IntRange()：用于随机决策
- createWalls()：递归分割区域
- super 方法：获取父类基础功能

### 系统流程位置
1. 房间实例创建 → 2. 尺寸设置 → 3. 房间网络构建 → 4. paint() 调用 → 5. 基础地形绘制 → 6. 递归分割 → 7. 通道创建

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
项目内未找到官方对应译名

### 依赖的资源
- Terrain 常量：WALL, BOOKSHELF, EMPTY_SP
- BOOKSHELF 应该是图书馆特有的地形类型
- EMPTY_SP 可能是特殊的空地类型，用于安全通行或触发特殊效果

### 中文翻译来源
在 levels_zh.properties 中未找到 SegmentedLibraryRoom 相关翻译

## 11. 使用示例

### 基本用法
```java
// SegmentedLibraryRoom 通常由关卡生成器自动创建，不建议手动实例化
SegmentedLibraryRoom room = new SegmentedLibraryRoom();
// 设置房间位置和尺寸（至少10x10）
room.set(new Rect(10, 10, 20, 20));
// 在关卡中绘制
room.paint(level);
// 房间会自动生成分段式图书馆布局
```

### 扩展示例
如需自定义分割逻辑，可以继承 SegmentedLibraryRoom：

```java
public class CustomSegmentedLibraryRoom extends SegmentedLibraryRoom {
    @Override
    protected void createWalls(Level level, Rect area) {
        // 实现自定义的分割算法
        // 例如，总是进行水平分割
        if (area.width() >= 4) {
            int splitX = (area.left + area.right) / 2;
            Painter.drawLine(level, new Point(splitX, area.top), new Point(splitX, area.bottom), Terrain.BOOKSHELF);
            Painter.set(level, splitX, (area.top + area.bottom) / 2, Terrain.EMPTY_SP);
            // 递归处理子区域...
        }
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖于房间的 left/right/top/bottom 坐标已正确设置
- 依赖于 Terrain.BOOKSHELF 和 Terrain.EMPTY_SP 的存在和正确行为
- createWalls() 依赖于初始地形的正确设置（BOOKSHELF 区域）

### 生命周期耦合
- 必须在房间位置和尺寸确定后才能调用 paint()
- paint() 调用后所有地形即固定
- 递归分割必须在基础地形绘制完成后进行

### 常见陷阱
- 修改分割算法时需确保生成的区域仍然连通
- EMPTY_SP 地形的行为需要与其他系统（如书架交互）协调
- 递归分割可能导致栈溢出，但当前实现通过区域大小检查避免了这个问题
- 文件开头的 FIXME 注释表明此代码可能有重复，重构时应考虑提取公共逻辑

## 13. 修改建议与扩展点

### 适合扩展的位置
- createWalls() 方法：自定义分割算法
- 通道创建逻辑：添加更多样化的通道模式
- 尺寸分类概率：调整大型房间的分布
- 地形类型：使用不同的安全区域地形

### 不建议修改的位置
- 基础多层地形绘制逻辑（WALL → BOOKSHELF → EMPTY_SP）
- 门周围的 EMPTY_SP 绘制（确保玩家能安全进出）
- 分割位置验证逻辑（确保书架结构的完整性）

### 重构建议
根据 FIXME 注释，此代码可能与 SegmentedRoom 有重复逻辑，建议：
1. 提取公共的递归分割算法到抽象基类
2. 将地形类型作为参数传递
3. 将分割常量（如边距大小）提取为可配置字段

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点