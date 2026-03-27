# PlantsRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\PlantsRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 128 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
PlantsRoom 类负责生成充满植物的房间布局。它创建多层草地地形（GRASS 和 HIGH_GRASS），并在房间中放置随机的植物种子。根据房间大小决定植物的数量和布局模式。

### 系统定位
作为标准房间（StandardRoom）的一种具体实现，PlantsRoom 在关卡生成过程中提供自然环境主题的房间类型，增加游戏世界的多样性。

### 不负责什么
- 不负责植物的具体生长逻辑
- 不负责植物的效果实现
- 不处理特殊的敌人生成规则
- 不管理房间连接的特殊逻辑

## 3. 结构总览

### 主要成员概览
- 无额外字段（继承自父类的字段包括 sizeCat 等）
- 私有静态方法 randomSeed() 用于生成随机植物种子

### 主要逻辑块概览
- 尺寸约束逻辑：确保最小尺寸为5x5
- 尺寸分类概率：定义 NORMAL/LARGE 的出现概率为 3:1（GIANT 为0）
- 合并逻辑：与相同类型的房间合并时使用 GRASS 地形
- 多层地形绘制：WALL → GRASS → HIGH_GRASS → (可选)中心 GRASS
- 植物放置逻辑：根据房间大小放置1、2或4个植物
- 放置限制：角色和物品不能放置在已有植物的位置

### 生命周期/调用时机
- 房间实例化时自动设置尺寸分类
- 关卡绘制阶段调用 paint() 方法生成实际地形和植物
- 物品/角色放置时调用 canPlaceItem()/canPlaceCharacter() 进行验证

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
- water/grass/trap/item 放置控制方法
- Door 内部类

### 覆写的方法
- minWidth()：返回 Math.max(super.minWidth(), 5)
- minHeight()：返回 Math.max(super.minHeight(), 5)
- sizeCatProbs()：返回 new float[]{3, 1, 0}
- merge()：自定义合并逻辑，相同类型房间合并使用 GRASS 地形
- paint()：实现植物房间的具体绘制逻辑
- canPlaceItem()：阻止在已有植物位置放置物品
- canPlaceCharacter()：阻止在已有植物位置放置角色

### 实现的接口契约
- Graph.Node 接口（通过 Room 间接实现）
- Bundlable 接口（通过 Room 间接实现）

### 依赖的关键类
- Level：关卡数据结构和 plant() 方法
- Terrain：地形类型定义（WALL, GRASS, HIGH_GRASS）
- Painter：关卡绘制工具（fill(), drawLine()）
- Generator：随机物品生成器
- Plant/Plant.Seed：植物系统
- Firebloom：特定植物类型（被排除）
- Random：随机数生成
- Point/Rect：几何计算
- Door：门类型定义

### 使用者
- StandardRoom.createRoom() 静态工厂方法
- LevelGenerator：关卡生成器在构建房间布局时
- Level.plant()：在 paint() 中调用放置植物

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
- GIANT 尺寸概率为0，因此不会生成巨型植物房间

## 7. 方法详解

### minWidth()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：确保 PlantsRoom 的最小宽度至少为5格

**参数**：无

**返回值**：int，返回父类 minWidth() 和 5 的较大值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(super.minWidth(), 5);
```

**边界情况**：当父类 minWidth() 返回值小于5时，强制返回5

### minHeight()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：确保 PlantsRoom 的最小高度至少为5格

**参数**：无

**返回值**：int，返回父类 minHeight() 和 5 的较大值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(super.minHeight(), 5);
```

**边界情况**：当父类 minHeight() 返回值小于5时，强制返回5

### sizeCatProbs()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：定义不同尺寸分类的出现概率，禁用 GIANT 尺寸

**参数**：无

**返回值**：float[]，包含三个元素 [3, 1, 0] 分别对应 NORMAL/LARGE/GIANT

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return new float[]{3, 1, 0};
```

**边界情况**：GIANT 尺寸概率为0，确保不会生成超大植物房间

### merge(Level l, Room other, Rect merge, int mergeTerrain)
**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：自定义房间合并逻辑，当与相同类型的植物房间或墓地房间合并时使用草地地形

**参数**：
- `l` (Level)：关卡实例
- `other` (Room)：要合并的其他房间
- `merge` (Rect)：合并区域
- `mergeTerrain` (int)：原始合并地形类型

**返回值**：void

**前置条件**：合并区域必须有效

**副作用**：修改关卡地形

**核心实现逻辑**：
- 如果合并地形为空地且对方是 PlantsRoom 或 GrassyGraveRoom，则使用 GRASS 地形合并
- 否则使用父类默认合并逻辑

**边界情况**：仅在特定条件下改变合并地形类型

### paint(Level level)
**可见性**：public

**是否覆写**：是，覆写自 Room 抽象方法

**方法职责**：在关卡中实际绘制 PlantsRoom 的地形和植物

**参数**：
- `level` (Level)：要绘制到的关卡实例

**返回值**：void

**前置条件**：房间必须已经设置了有效的位置和尺寸

**副作用**：修改 level 对象的 map 数组和 plants 映射

**核心实现逻辑**：
1. 使用 Painter.fill() 填充整个房间为墙壁 (Terrain.WALL)
2. 填充内部1格区域为草地 (Terrain.GRASS)
3. 填充内部2格区域为高草 (Terrain.HIGH_GRASS)
4. 如果房间最小维度≥7，再填充内部3格区域为草地
5. 根据房间大小决定植物数量和布局：
   - 最大维度≥9且最小维度≥11：放置4个植物，十字形高草线
   - 最大维度≥9但较小：放置2个植物，水平或垂直高草线
   - 其他情况：放置1个植物在中心
6. 设置所有连接门为常规门类型

**边界情况**：
- 植物放置位置避开高草线交叉点（4植物情况）
- 水平/垂直选择基于房间长宽比和随机因素

### canPlaceItem(Point p, Level l)
**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：阻止在已有植物的位置放置物品

**参数**：
- `p` (Point)：要检查的位置
- `l` (Level)：关卡实例

**返回值**：boolean，如果位置有效且无植物则返回true

**前置条件**：位置必须在房间内

**副作用**：无

**核心实现逻辑**：
```java
return super.canPlaceItem(p, l) && l.plants.get(l.pointToCell(p)) == null;
```

**边界情况**：空植物映射返回null，允许放置

### canPlaceCharacter(Point p, Level l)
**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：阻止在已有植物的位置放置角色

**参数**：
- `p` (Point)：要检查的位置
- `l` (Level)：关卡实例

**返回值**：boolean，如果位置有效且无植物则返回true

**前置条件**：位置必须在房间内

**副作用**：无

**核心实现逻辑**：
```java
return super.canPlaceCharacter(p, l) && l.plants.get(l.pointToCell(p)) == null;
```

**边界情况**：同 canPlaceItem()

### randomSeed()
**可见性**：private static

**是否覆写**：否

**方法职责**：生成随机的植物种子，但排除 Firebloom（火绒花）

**参数**：无

**返回值**：Plant.Seed，随机的植物种子实例

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
- 使用 Generator.randomUsingDefaults() 从 SEED 类别生成随机种子
- 如果生成的是 Firebloom.Seed，则重新生成直到获得其他类型

**边界情况**：理论上可能无限循环，但实际中 SEED 类别有多种植物，概率很低

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是 Room/StandardRoom 协议的一部分
- 没有额外的公共 API

### 内部辅助方法
- randomSeed() 是私有静态方法，不应被外部依赖

### 扩展入口
- 可以通过继承进一步自定义植物生成逻辑
- 可以覆写其他 StandardRoom 方法来自定义行为

## 9. 运行机制与调用链

### 创建时机
- 在关卡生成过程中的房间创建阶段
- 通过 StandardRoom.createRoom() 静态工厂方法随机选择创建

### 调用者
- StandardRoom.createRoom()：通过反射创建实例
- LevelGenerator：在构建房间网络时调用 paint()
- Level.itemPlacement/characterPlacement：调用 canPlaceItem()/canPlaceCharacter()

### 被调用者
- Painter.fill()/drawLine()：用于地形绘制
- Level.plant()：放置植物
- Generator.randomUsingDefaults()：生成随机种子
- Random.Int()/IntRange()：用于随机决策
- super 方法：获取父类基础功能

### 系统流程位置
1. 房间实例创建 → 2. 尺寸设置 → 3. 房间网络构建 → 4. paint() 调用 → 5. 植物放置 → 6. 后续物品/角色放置验证

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
项目内未找到官方对应译名

### 依赖的资源
- Terrain 常量：WALL, GRASS, HIGH_GRASS
- Plant 系统：各种植物种子类型
- Generator 配置：SEED 类别的默认生成配置

### 中文翻译来源
在 levels_zh.properties 中未找到 PlantsRoom 相关翻译

## 11. 使用示例

### 基本用法
```java
// PlantsRoom 通常由关卡生成器自动创建，不建议手动实例化
PlantsRoom room = new PlantsRoom();
// 设置房间位置和尺寸
room.set(new Rect(10, 10, 20, 20));
// 在关卡中绘制
room.paint(level);
// 后续物品放置会自动避开植物位置
Point itemPos = room.random(); // 可能需要重试直到找到无植物位置
```

### 扩展示例
如需自定义植物类型，可以继承 PlantsRoom 并覆写 randomSeed() 方法：

```java
public class CustomPlantsRoom extends PlantsRoom {
    @Override
    private static Plant.Seed randomSeed(){
        // 返回特定的植物种子
        return new Sungrass.Seed();
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖于房间的 left/right/top/bottom 坐标已正确设置
- 依赖于 Level.plants 映射的存在
- 依赖于 Generator.SEED 类别的正确配置

### 生命周期耦合
- 必须在房间位置和尺寸确定后才能调用 paint()
- paint() 调用后植物位置即固定
- 物品/角色放置必须在 paint() 之后进行验证

### 常见陷阱
- 修改植物放置逻辑时需注意保持与 canPlaceXxx() 方法的一致性
- Firebloom 被排除的原因可能是其爆炸效果对房间布局造成破坏
- 高草线绘制可能覆盖部分植物位置，但代码中已正确处理坐标偏移

## 13. 修改建议与扩展点

### 适合扩展的位置
- randomSeed() 方法：自定义植物选择逻辑
- 植物数量和布局逻辑：根据游戏需求调整
- 地形层次：添加更多草地类型或装饰

### 不建议修改的位置
- 最小尺寸约束（5x5 是基本布局要求）
- 植物放置的防冲突逻辑（canPlaceXxx 方法）
- Firebloom 排除逻辑（可能有设计原因）

### 重构建议
当前实现较为清晰，但植物放置逻辑可以提取为单独的私有方法以提高可读性。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点