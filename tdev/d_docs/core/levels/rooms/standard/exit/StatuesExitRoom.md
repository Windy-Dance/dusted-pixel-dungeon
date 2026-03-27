# StatuesExitRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\exit\StatuesExitRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit |
| **文件类型** | class |
| **继承关系** | extends StatuesRoom → StandardRoom → Room |
| **代码行数** | 70 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
StatuesExitRoom 类实现了一个具有雕像群特征的出口房间。它在继承 StatuesRoom 雕像群生成逻辑的基础上，添加了楼层出口的绘制和放置逻辑，并在出口周围创建特殊空地以确保玩家可以安全接近出口。

### 系统定位
作为出口房间的一种具体实现，StatuesExitRoom 在关卡深度16-20（根据 ExitRoom 的配置，对应 rooms 列表中的第19个位置）时被随机选择创建。它结合了雕像群房间的网格化雕像布局特性和出口房间的功能要求。

### 不负责什么
- 不负责雕像群地形的具体生成算法（由父类 StatuesRoom 提供）
- 不处理玩家与出口的交互逻辑
- 不管理其他房间类型的生成

## 3. 结构总览

### 主要成员概览
- 无额外字段定义（继承自父类）

### 主要逻辑块概览
- 雕像群大小类别概率配置（sizeCatProbs）
- 出口标识（isExit）
- 出口绘制逻辑（paint）
- 角色放置限制（canPlaceCharacter）

### 生命周期/调用时机
- 在关卡生成过程中被 ExitRoom.createExit() 创建
- paint() 方法在关卡绘制阶段被调用
- canPlaceCharacter() 在角色放置时被调用

## 4. 继承与协作关系

### 父类提供的能力
继承链：StatuesExitRoom → StatuesRoom → StandardRoom → Room

从 StatuesRoom 继承：
- 雕像群绘制逻辑（paint() 中的网格化雕像布局）
- 尺寸限制（minWidth/minHeight 返回至少7）
- 大小类别概率配置（sizeCatProbs 返回 [9, 3, 1]）
- 特殊装饰逻辑（中心火焰基座 REGION_DECO_ALT）

### 覆写的方法
- `sizeCatProbs()`：返回雕像群大小类别概率 [3, 1, 0]
- `isExit()`：返回 true，标识为出口房间
- `paint(Level level)`：添加出口绘制逻辑
- `canPlaceCharacter(Point p, Level l)`：防止角色放置在出口位置

### 实现的接口契约
遵循 Room 抽象类的所有契约。

### 依赖的关键类
- `Level`：关卡数据结构
- `Terrain`：地形类型定义（特别关注 STATUE_SP、REGION_DECO_ALT、EMPTY_SP、EXIT）
- `LevelTransition`：关卡过渡点管理
- `Painter`：关卡绘制工具
- `PathFinder`：路径查找工具（使用 NEIGHBOURS8）
- `Point`：坐标点
- `Random`：随机数生成（用于中心装饰位置）

### 使用者
- ExitRoom.createExit() 工厂方法
- 关卡生成器（LevelGenerator）

## 5. 字段/常量详解

### 静态常量
无显式静态常量定义。

### 实例字段
无实例字段定义

## 6. 构造与初始化机制

### 构造器
使用默认构造器，无自定义构造方法。

### 初始化块
无静态或实例初始化块.

### 初始化注意事项
- 依赖父类的雕像群布局生成
- 出口固定放置在房间中心位置

## 7. 方法详解

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 StatuesRoom

**方法职责**：返回雕像群房间大小类别的概率分布

**参数**：无

**返回值**：float[]，包含三个元素的概率数组 [3, 1, 0]

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return new float[]{3, 1, 0};
```
对应 normal/large/giant 三种大小类别，normal 最大概率（3），large 次之（1），giant 不会出现（0）

**边界情况**：无

### isExit()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：标识此房间为出口房间

**参数**：无

**返回值**：boolean，始终返回 true

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return true;
```

**边界情况**：无

### paint(Level level)

**可见性**：public

**是否覆写**：是，覆写自 StatuesRoom

**方法职责**：绘制雕像群出口房间，包括雕像布局和楼层出口

**参数**：
- `level` (Level)：要绘制的关卡对象

**返回值**：void

**前置条件**：level 参数不为 null

**副作用**：
- 修改 level 的地图数据
- 向 level.transitions 添加新的 LevelTransition 对象
- 在出口周围创建特殊通行区域

**核心实现逻辑**：
1. 调用 super.paint(level) 绘制基础雕像群布局：
   - 外层墙壁（Terrain.WALL）
   - 内层空地（Terrain.EMPTY）
   - 网格化雕像群（每个子区域四角放置 STATUE_SP）
   - 中心火焰基座（REGION_DECO_ALT，如果子区域足够大）
2. 获取房间中心位置作为出口位置
3. 如果房间尺寸较小（宽≤10且高≤10），填充内部3层区域为特殊空地（Terrain.EMPTY_SP）
4. 遍历出口周围的8个邻居：
   - 如果邻居不是特殊雕像（STATUE_SP），则设置为特殊空地（Terrain.EMPTY_SP）
5. 在中心位置设置 Terrain.EXIT 地形
6. 创建并添加 LevelTransition 对象

**边界情况**：
- 小房间的内部填充确保出口有足够的通行空间
- 保护特殊雕像（STATUE_SP）不被覆盖，保持房间的装饰完整性
- 出口固定在中心位置，利用雕像群房间的对称性

### canPlaceCharacter(Point p, Level l)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：检查是否可以在指定位置放置角色

**参数**：
- `p` (Point)：要检查的位置
- `l` (Level)：关卡对象

**返回值**：boolean，true表示可以放置

**前置条件**：p 和 l 参数不为 null

**副作用**：无

**核心实现逻辑**：
```java
return super.canPlaceCharacter(p, l) && l.pointToCell(p) != l.exit();
```
首先调用父类检查（确保位置在可通行区域），然后确保位置不是关卡的出口位置

**边界情况**：
- 当 l.exit() 返回 -1（无出口）时，条件始终为 true

## 8. 对外暴露能力

### 显式 API
- 所有覆写的方法提供标准房间接口实现
- 通过继承获得完整的 StatuesRoom 功能

### 内部辅助方法
- 无 private 或 package-private 方法

### 扩展入口
- 子类可以调整大小类别概率
- 子类可以修改出口周围地形处理逻辑
- 子类可以自定义出口位置（当前固定为中心）

## 9. 运行机制与调用链

### 创建时机
- 关卡深度16-20时，通过 ExitRoom.createExit() 被创建
- 在关卡布局阶段被添加到关卡中

### 调用者
- ExitRoom.createExit() 静态工厂方法
- LevelGenerator 关卡生成器

### 被调用者
- super.paint()：父类雕像群绘制逻辑
- level.pointToCell()：坐标转换
- level.center()：获取房间中心
- PathFinder.NEIGHBOURS8：获取8方向邻居坐标
- Painter.set()：设置地形
- Painter.fill()：填充内部区域
- LevelTransition 构造器：创建出口过渡点
- Random.Int()：中心装饰位置的随机偏移

### 系统流程位置
在关卡生成的房间绘制阶段，属于雕像群类型出口房间的具体实现，利用了房间的中心对称特性来放置出口。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| levels.level.exit_name | 楼层出口 | 出口房间的通用名称 |
| levels.level.exit_desc | 通向下一层的楼梯。 | 出口的描述文本 |

### 依赖的资源
- 无直接依赖的纹理/图标/音效资源
- 依赖 Terrain.EXIT、Terrain.EMPTY_SP、Terrain.STATUE_SP、Terrain.REGION_DECO_ALT 常量
- 使用 PathFinder 邻居坐标常量

### 中文翻译来源
来自 D:\Develop\Workspace\DustedPixelDungeon\core\src\main\assets\messages\levels\levels_zh.properties 文件

## 11. 使用示例

### 基本用法
```java
// 通过工厂方法创建（实际使用方式）
StandardRoom statuesExit = ExitRoom.createExit(); // 可能返回 StatuesExitRoom 实例
statuesExit.paint(level);

// 直接创建（测试用途）
StatuesExitRoom statuesExit = new StatuesExitRoom();
statuesExit.setRect(10, 10, 17, 17); // 设置房间位置和尺寸
statuesExit.paint(level);
```

### 扩展示例
```java
// 自定义雕像群出口房间
public class CustomStatuesExitRoom extends StatuesExitRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{2, 2, 1}; // 允许 giant 类型
    }
    
    @Override
    public void paint(Level level) {
        super.paint(level);
        // 添加自定义装饰，如出口周围的特效
        addExitEffects(level);
    }
}
```

## 12. 开发注意事项

### 状态依赖
- 依赖 level.exit() 返回的出口位置
- 依赖 level.transitions 列表状态
- 依赖全局的关卡深度配置

### 生命周期耦合
- paint() 方法必须在房间被正确放置后调用
- 出口位置固定为中心，无法手动指定

### 常见陷阱
- sizeCatProbs() 配置不允许 giant 类型，可能限制房间多样性
- 小房间的内部填充逻辑可能影响雕像群的视觉效果
- 出口周围地形处理保护 STATUE_SP，但可能在密集雕像布局中造成通行问题

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可以调整 sizeCatProbs() 允许其他大小类别
- 可以修改出口周围地形的处理逻辑（例如只清理特定方向的邻居）
- 可以为出口添加特殊的视觉效果

### 不建议修改的位置
- StatuesRoom 中的基础雕像群生成逻辑
- 出口的核心放置逻辑（影响游戏性）
- STATUE_SP 保护逻辑（保持装饰完整性）

### 重构建议
- 考虑将出口位置选择逻辑提取到单独的方法中
- 可以添加配置选项控制小房间的内部填充策略
- 考虑优化出口周围地形处理以提高通行可靠性

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点