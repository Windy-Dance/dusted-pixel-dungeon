# SkullsRoom 文档

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
SkullsRoom 类负责生成骷髅主题的房间布局。它使用椭圆填充算法创建同心椭圆结构：外层为空地（EMPTY），中层为雕像（STATUE），内层为墙壁（WALL），形成具有骷髅装饰效果的环形房间。

### 系统定位
作为标准房间（StandardRoom）的一种具体实现，SkullsRoom 在关卡生成过程中提供具有骷髅主题装饰的特殊房间，增加关卡的视觉多样性和主题氛围。

### 不负责什么
- 不负责雕像的具体生成逻辑或交互行为
- 不处理特殊的敌人生成规则
- 不管理物品放置的特殊规则
- 不负责椭圆绘制算法的底层实现

## 3. 结构总览

### 主要成员概览
- 无额外字段（继承自父类的字段包括 sizeCat 等）

### 主要逻辑块概览
- 尺寸约束逻辑：确保最小尺寸为7x7
- 尺寸分类概率：禁用 NORMAL 尺寸，定义 LARGE/GIANT 的出现概率为 3:1
- 椭圆分层结构：EMPTY（内边距2）→ STATUE（内边距4）→ WALL（内边距6）
- 门通道绘制：根据门的方向绘制半房间长度的通道

### 生命周期/调用时机
- 房间实例化时自动设置尺寸分类（仅 LARGE 或 GIANT）
- 关卡绘制阶段调用 paint() 方法生成实际地形

## 4. 继承与协作关系

### 父类提供的能力
从 StandardRoom 继承：
- SizeCategory 枚举和相关字段
- minWidth()/minHeight() 基础实现
- setSizeCat() 相关方法
- sizeFactor(), mobSpawnWeight(), connectionWeight() 等辅助方法
- canMerge() 实现

从 Room 继承：
- 空间和连接逻辑（Rect 功能扩展）
- paint() 抽象方法
- Door 内部类

### 覆写的方法
- minWidth()：返回 Math.max(7, super.minWidth())
- minHeight()：返回 Math.max(7, super.minHeight())
- sizeCatProbs()：返回 new float[]{0, 3, 1}
- paint()：实现骷髅房间的具体绘制逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 间接实现）
- Bundlable 接口（通过 Room 间接实现）

### 依赖的关键类
- Level：关卡数据结构
- Terrain：地形类型定义（WALL, EMPTY, STATUE）
- Painter：关卡绘制工具（fill(), fillEllipse(), drawInside()）

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

### minWidth()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：确保 SkullsRoom 的最小宽度至少为7格

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

**方法职责**：确保 SkullsRoom 的最小高度至少为7格

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

**方法职责**：定义不同尺寸分类的出现概率，禁用 NORMAL 尺寸

**参数**：无

**返回值**：float[]，包含三个元素 [0, 3, 1] 分别对应 NORMAL/LARGE/GIANT

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return new float[]{0, 3, 1};
```

**边界情况**：NORMAL 尺寸概率为0，确保不会生成小型骷髅房间

### paint(Level level)
**可见性**：public

**是否覆写**：是，覆写自 Room 抽象方法

**方法职责**：在关卡中实际绘制 SkullsRoom 的骷髅主题椭圆结构

**参数**：
- `level` (Level)：要绘制到的关卡实例

**返回值**：void

**前置条件**：房间必须已经设置了有效的位置和尺寸

**副作用**：修改 level 对象的 map 数组

**核心实现逻辑**：
1. 使用 Painter.fill() 填充整个房间为墙壁 (Terrain.WALL)
2. 使用 Painter.fillEllipse() 填充内边距2的椭圆区域为空地 (Terrain.EMPTY)
3. 设置所有连接门为常规门类型，并根据门的方向绘制通道：
   - 水平门（左/右）：绘制宽度一半的内部通道
   - 垂直门（上/下）：绘制高度一半的内部通道
4. 使用 Painter.fillEllipse() 填充内边距4的椭圆区域为雕像 (Terrain.STATUE)
5. 使用 Painter.fillEllipse() 填充内边距6的椭圆区域为墙壁 (Terrain.WALL)

**边界情况**：
- 由于禁用了 NORMAL 尺寸，房间至少为10x10，确保有足够的空间容纳三层椭圆结构
- 最内层墙壁（内边距6）确保中心区域被封闭，形成骷髅眼睛的效果
- 门通道绘制确保玩家可以从任何方向进入房间中心区域

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是 Room/StandardRoom 协议的一部分
- 没有额外的公共 API

### 内部辅助方法
- 无内部辅助方法，所有逻辑在 paint() 中实现

### 扩展入口
- 可以通过继承调整椭圆内边距来改变骷髅外观
- 可以覆写其他 StandardRoom 方法来自定义行为

## 9. 运行机制与调用链

### 创建时机
- 在关卡生成过程中的房间创建阶段
- 通过 StandardRoom.createRoom() 静态工厂方法随机选择创建（仅 LARGE/GIANT）

### 调用者
- StandardRoom.createRoom()：通过反射创建实例
- LevelGenerator：在构建房间网络时调用 paint()

### 被调用者
- Painter.fill()/fillEllipse()/drawInside()：用于地形绘制
- super 方法：获取父类基础功能

### 系统流程位置
1. 房间实例创建 → 2. 尺寸设置 → 3. 房间网络构建 → 4. paint() 调用 → 5. 椭圆结构绘制 → 6. 门通道绘制

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
项目内未找到官方对应译名

### 依赖的资源
- Terrain 常量：WALL, EMPTY, STATUE
- STATUE 地形可能显示骷髅装饰的视觉效果
- Painter.fillEllipse()：椭圆绘制工具，可能使用特定算法

### 中文翻译来源
在 levels_zh.properties 中未找到 SkullsRoom 相关翻译

## 11. 使用示例

### 基本用法
```java
// SkullsRoom 通常由关卡生成器自动创建，不建议手动实例化
SkullsRoom room = new SkullsRoom();
// 设置房间位置和尺寸（至少10x10）
room.set(new Rect(10, 10, 20, 20));
// 在关卡中绘制
room.paint(level);
// 房间会自动生成三层椭圆的骷髅主题结构
```

### 扩展示例
如需自定义骷髅外观，可以继承 SkullsRoom：

```java
public class CustomSkullsRoom extends SkullsRoom {
    @Override
    public void paint(Level level) {
        // 自定义椭圆内边距
        Painter.fill(level, this, Terrain.WALL);
        Painter.fillEllipse(level, this, 1, Terrain.EMPTY);  // 更窄的空地区域
        Painter.fillEllipse(level, this, 3, Terrain.STATUE); // 更大的雕像区域
        Painter.fillEllipse(level, this, 5, Terrain.WALL);   // 更小的中心墙壁
        
        // 门通道绘制...
        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
            // ...门通道逻辑
        }
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖于房间的 left/right/top/bottom 坐标已正确设置
- 依赖于 Terrain 常量的存在和正确行为
- 依赖于 Painter.fillEllipse() 的正确实现

### 生命周期耦合
- 必须在房间位置和尺寸确定后才能调用 paint()
- paint() 调用后所有地形即固定
- 门通道绘制必须在基础椭圆结构绘制完成后进行

### 常见陷阱
- 修改椭圆内边距时需确保各层之间有合理的间距（内边距4 > 内边距2 + 充足空间）
- 最内层墙壁（内边距6）必须足够大以形成明显的封闭区域
- 门通道绘制使用房间尺寸的一半，确保能到达房间中心区域
- 由于只支持 LARGE/GIANT 尺寸，小房间测试时需要特别注意

## 13. 修改建议与扩展点

### 适合扩展的位置
- 椭圆内边距：调整各层的大小比例
- 地形类型：使用不同的装饰地形替代 STATUE
- 门通道逻辑：调整通道长度或形状
- 尺寸分类概率：调整大型房间的分布

### 不建议修改的位置
- 基础三层椭圆结构（这是骷髅房间的核心特征）
- 门通道的基本逻辑（确保可通行性）
- 尺寸约束（确保有足够的空间容纳三层结构）

### 重构建议
当前实现非常简洁，但如果需要更多自定义选项，可以考虑：
1. 将椭圆内边距提取为可配置常量
2. 将地形类型作为参数传递
3. 添加更多的装饰元素变体

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点