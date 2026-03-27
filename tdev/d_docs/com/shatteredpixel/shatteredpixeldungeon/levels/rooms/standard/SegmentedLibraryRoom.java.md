# SegmentedLibraryRoom.java 文档

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
SegmentedLibraryRoom 类负责生成分段式图书馆的标准房间布局，使用书架（BOOKSHELF）地形作为墙壁，通过递归分割算法将房间划分为多个可通行的小区域。

### 系统定位
该类属于关卡生成系统中的标准房间类型之一，继承自 StandardRoom，专门用于创建具有图书馆主题和分段结构的房间变体。

### 不负责什么
- 不负责书架地形的具体视觉表现（由渲染系统处理）
- 不负责怪物或物品在图书馆中的放置策略（由上层逻辑处理）
- 不负责门的具体连接逻辑（由上层系统处理）

## 3. 结构总览

### 主要成员概览
- 无实例字段
- 无静态常量
- private createWalls() 方法：递归分割房间并创建书架墙壁

### 主要逻辑块概览
- sizeCatProbs() 方法：定义房间尺寸类别的概率分布 [0, 3, 1]（禁用小型房间）
- paint() 方法：核心绘制逻辑，设置基础地形并启动递归分割
- createWalls() 方法：递归算法将区域分割成多个小段，每段用书架墙隔开并留出通道

### 生命周期/调用时机
该类实例在关卡生成过程中被 RoomFactory 创建，paint() 方法在关卡绘制阶段被调用。

## 4. 继承与协作关系

### 父类提供的能力
- width()/height() 方法：获取房间尺寸
- left/right/top/bottom 字段：房间边界坐标
- connected 字段：连接的门信息
- sizeCat 字段：房间尺寸类别

### 覆写的方法
- sizeCatProbs()：覆写自 Room
- paint()：覆写自 Room

### 实现的接口契约
无

### 依赖的关键类
- Level：关卡数据结构
- Terrain：地形类型定义（WALL, BOOKSHELF, EMPTY_SP）
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

**边界情况**：只有中型和大型房间会被生成，确保有足够的空间进行分段。

### paint()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制图书馆房间的实际布局，包括书架墙壁和递归分段结构

**参数**：
- level (Level)：当前关卡对象

**返回值**：void

**前置条件**：房间边界已正确设置，connected 字典包含连接的门

**副作用**：修改 level 对象的地形数据

**核心实现逻辑**：
1. 首先填充整个房间区域为墙壁地形 (Terrain.WALL)
2. 在距离边界1格的内部区域填充为书架地形 (Terrain.BOOKSHELF)
3. 在距离边界2格的内部区域填充为空地特殊地形 (Terrain.EMPTY_SP)
4. 将所有连接的门设置为常规门类型 (Door.Type.REGULAR)
5. 在门内侧2格范围内绘制 EMPTY_SP 地形，确保门通道连通（有助于 createWalls 逻辑）
6. 调用 createWalls() 方法对内部区域（距离边界2格外）进行递归分割

**边界情况**：
- 由于小型房间被禁用，房间至少为中等尺寸，确保有足够的分割空间
- 门通道的预处理确保分割算法不会阻塞入口

### createWalls()

**可见性**：private

**是否覆写**：否

**方法职责**：递归分割指定区域，创建书架墙壁并将区域划分为多个可通行的小段

**参数**：
- level (Level)：当前关卡对象
- area (Rect)：要处理的矩形区域

**返回值**：void

**前置条件**：area 是有效的矩形区域

**副作用**：修改 level 对象的地形数据

**核心实现逻辑**：
1. 检查区域是否足够大进行分割：
   - 最大维度≥4 且最小维度≥3，否则直接返回
2. 最多尝试10次分割：
   - 如果宽度≥高度（或相等且随机选择）：垂直分割
     - 随机选择分割X坐标（距离边界至少2格）
     - 检查分割线两端是否都是书架地形（确保连续性）
     - 如果满足条件：
       - 在分割位置绘制垂直书架墙
       - 在墙上随机位置留出一个 EMPTY_SP 通道
       - 递归处理左右两个子区域
   - 否则：水平分割
     - 随机选择分割Y坐标（距离边界至少2格）
     - 检查分割线两端是否都是书架地形
     - 如果满足条件：
       - 在分割位置绘制水平书架墙
       - 在墙上随机位置留出一个 EMPTY_SP 通道
       - 递归处理上下两个子区域
3. 如果10次尝试都失败，则放弃分割该区域

**边界情况**：
- 分割坐标确保子区域至少有1格大小
- 通道位置确保相邻区域可以互相通行
- 递归深度受区域大小限制，避免无限递归
- 注释提到存在代码重复（FIXME），与 SegmentedRoom 类似

## 8. 对外暴露能力

### 显式 API
- sizeCatProbs()
- paint()

### 内部辅助方法
- createWalls()：仅供内部使用

### 扩展入口
该类没有提供 protected 方法供子类扩展，如需自定义分段图书馆房间行为，应直接继承 StandardRoom 并实现自己的逻辑。

## 9. 运行机制与调用链

### 创建时机
在关卡生成过程中，当RoomFactory需要创建分段图书馆房间类型时实例化。

### 调用者
- RoomFactory.createRoom()：创建房间实例
- LevelGenerator.paint()：调用 paint() 方法绘制房间

### 被调用者
- Painter.fill()/Painter.drawLine()/Painter.set()/Painter.drawInside()：填充和绘制地形
- Random.Int()/Random.IntRange()：生成随机数
- createWalls()：递归分割（自调用）

### 系统流程位置
位于关卡生成流程的房间绘制阶段，在房间布局确定但具体地形未填充时执行。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| levels.level.bookshelf_name | 书架 | 书架地形名称 |
| levels.caveslevel.bookshelf_desc | 到底会有谁需要在洞窟里摆上这么个书架？ | 书架描述（洞穴层级） |
| levels.citylevel.bookshelf_desc | 不同学科的书排满了书架。 | 书架描述（城市层级） |
| levels.hallslevel.bookshelf_desc | 用远古语言写就的书籍堆积在书架里。 | 书架描述（大厅层级） |
| levels.prisonlevel.bookshelf_desc | 这个书架可能是监狱图书馆的残留物。烧掉怎么样？ | 书架描述（监狱层级） |
| levels.sewerlevel.bookshelf_desc | 这个书架塞满了没用的成功学书籍。烧掉怎么样？ | 书架描述（下水道层级） |

### 依赖的资源
- Terrain.BOOKSHELF：书架地形
- Terrain.EMPTY_SP：特殊空地地形（用于通道和内部区域）
- Terrain.WALL：普通墙壁地形

### 中文翻译来源
core/src/main/assets/messages/levels/levels_zh.properties，第236、182、193、208、279、287行。使用"分段图书馆房间"作为房间类型的中文名称，结合了"library"的含义和房间的分段特性。

## 11. 使用示例

### 基本用法
```java
// 在关卡生成器中创建分段图书馆房间
SegmentedLibraryRoom libraryRoom = new SegmentedLibraryRoom();
libraryRoom.set(left, top, right, bottom); // 设置房间边界
libraryRoom.paint(level); // 绘制房间到关卡
```

### 扩展示例
由于该类没有提供扩展点，如需自定义行为，建议参考其实现创建新的房间类型：

```java
public class CustomLibraryRoom extends StandardRoom {
    @Override
    public void paint(Level level) {
        // 自定义图书馆生成逻辑
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.BOOKSHELF);
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
- 修改 sizeCatProbs() 返回值时需注意小型房间被禁用是有意为之，确保足够的分割空间
- 直接修改 createWalls() 算法时需注意避免创建无法通行的封闭区域
- 门通道的预处理逻辑不应随意移除，否则可能导致分割算法阻塞入口
- 注释中提到的代码重复（FIXME）表明可能与 SegmentedRoom 共享逻辑，修改时需考虑一致性

## 13. 修改建议与扩展点

### 适合扩展的位置
- 如需不同的分割策略，可修改 createWalls() 方法的分割逻辑
- 如需不同的最小区域尺寸，可调整 createWalls() 中的尺寸检查条件
- 可考虑添加配置选项来控制分割密度或通道数量

### 不建议修改的位置
- 小型房间禁用逻辑不应移除，因为分段需要足够的空间
- 书架作为墙壁的核心设计不应改变，这是图书馆房间的主题特征
- 通道创建逻辑不应移除，否则会导致区域无法通行

### 重构建议
注释中明确提到存在代码重复（"some copypasta from segmented room"），建议将共同的分割逻辑提取到共享的工具类或父类中。这将提高代码的可维护性和减少重复。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（使用 levels_zh.properties 中的"书架"翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点