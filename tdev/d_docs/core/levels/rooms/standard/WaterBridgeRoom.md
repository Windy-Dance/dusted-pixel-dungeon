# WaterBridgeRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\WaterBridgeRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardBridgeRoom |
| **代码行数** | 42 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
WaterBridgeRoom 类负责生成水桥主题的房间布局。它继承自 StandardBridgeRoom，通过覆写抽象方法来自定义地形类型（使用 WATER 作为空间地形）和桥梁宽度限制，并禁用在房间内放置额外的水。

### 系统定位
作为标准桥式房间（StandardBridgeRoom）的一种具体实现，WaterBridgeRoom 在关卡生成过程中提供具有水域环境的桥式房间布局，增加关卡的多样性和环境主题性。

### 不负责什么
- 不负责桥式房间的核心逻辑实现（由父类 StandardBridgeRoom 处理）
- 不处理房间尺寸计算和门位置逻辑
- 不管理物品或角色放置的具体规则
- 不负责房间合并的特殊逻辑

## 3. 结构总览

### 主要成员概览
- 无额外字段（继承自父类的 fields 包括 spaceRect, bridgeRect 等）
- 覆写父类的抽象方法和特定方法来自定义行为

### 主要逻辑块概览
- 桥梁宽度限制：根据房间尺寸动态调整（≥8格时为3，否则为2）
- 空间地形设置：使用 WATER 作为空间地形
- 水放置禁用：阻止在房间内放置额外的水
- 继承父类的完整桥式房间逻辑

### 生命周期/调用时机
- 房间实例化时自动设置基础属性
- 关卡绘制阶段调用 paint() 方法（继承自父类）生成实际地形

## 4. 继承与协作关系

### 父类提供的能力
从 StandardBridgeRoom 继承：
- 抽象方法的默认实现框架
- spaceRect 和 bridgeRect 字段用于存储空间和桥梁区域
- paint() 方法的完整实现（包括门处理、空间计算、桥梁放置）
- canMerge() 实现（基于 spaceTile() 返回值）
- canPlaceItem()/canPlaceCharacter() 实现（阻止在空间区域内放置）

从 StandardRoom 继承：
- SizeCategory 枚举和相关 fields
- minWidth()/minHeight() 基础实现（最小5x5）
- setSizeCat() 相关方法
- sizeFactor(), mobSpawnWeight(), connectionWeight() 等辅助 methods

From Room 继承：
- 空间和连接逻辑（Rect 功能扩展）
- Door 内部类

### 覆写的方法
- maxBridgeWidth(int roomDimension)：根据房间尺寸返回2或3
- spaceTile()：返回 Terrain.WATER
- canPlaceWater(Point p)：返回 false

### 实现的接口契约
- Graph.Node 接口（通过 Room 间接实现）
- Bundlable 接口（通过 Room 间接实现）

### 依赖的关键类
- Terrain：地形类型定义（WATER）
- StandardBridgeRoom：桥式房间的基础实现
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
- 继承父类的默认尺寸分类概率（[1, 0, 0]，即总是 NORMAL）
- 实例创建后立即具有有效的尺寸分类
- 最小尺寸约束为5x5（来自 StandardBridgeRoom）

## 7. 方法详解

### maxBridgeWidth(int roomDimension)
**可见性**：protected

**是否覆写**：是，实现自 StandardBridgeRoom 抽象方法

**方法职责**：根据房间尺寸动态调整桥梁的最大宽度

**参数**：
- `roomDimension` (int)：房间的宽度或高度维度

**返回值**：int，如果房间维度≥8则返回3，否则返回2

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return roomDimension >= 8 ? 3 : 2;
```

**边界情况**：
- 小房间（<8格）：桥梁宽度限制为2格
- 大房间（≥8格）：桥梁宽度可达到3格，提供更多通行空间

### spaceTile()
**可见性**：protected

**是否覆写**：是，实现自 StandardBridgeRoom 抽象方法

**方法职责**：定义空间区域使用的地形类型为 WATER

**参数**：无

**返回值**：int，Terrain.WATER 的值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Terrain.WATER;
```

**边界情况**：无

### canPlaceWater(Point p)
**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：禁用在房间内放置额外的水

**参数**：
- `p` (Point)：要检查的位置

**返回值**：boolean，始终返回 false

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return false;
```

**边界情况**：完全禁止水放置，由房间自身控制水地形

## 8. 对外暴露能力

### 显式 API
- 所有 public methods 都是 Room/StandardRoom 协议的一部分
- 没有额外的公共 API

### 内部辅助方法
- 所有 protected methods 都是为了满足父类抽象方法的要求
- 不应被外部直接调用

### 扩展入口
- 可以通过继承进一步自定义地形类型
- 可以调整桥梁宽度计算逻辑
- 可以覆写其他 StandardRoom methods 来自定义行为

## 9. 运行机制与调用链

### 创建时机
- 在关卡生成过程中的房间创建阶段
- 通过 StandardRoom.createRoom() 静态工厂方法随机选择创建

### 调用者
- StandardRoom.createRoom()：通过反射创建实例
- LevelGenerator：在构建房间网络时调用 paint()（继承自父类）

### 被调用者
- Terrain.WATER：获取地形类型值
- super.paint()：执行完整的桥式房间绘制逻辑
- super.canMerge()：基于 spaceTile() 返回值决定合并行为

### 系统流程位置
1. 房间实例创建 → 2. 尺寸设置 → 3. 房间网络构建 → 4. paint() 调用（父类实现）→ 5. 地形最终确定

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
项目内未找到官方对应译名

### 依赖的资源
- Terrain 常量：WATER
- WATER 地形可能具有特殊的视觉效果或游戏机制（如伤害、减速等）

### 中文翻译来源
在 levels_zh.properties 中未找到 WaterBridgeRoom 相关翻译

## 11. 使用示例

### 基本用法
```java
// WaterBridgeRoom 通常由关卡生成器自动创建，不建议手动实例化
WaterBridgeRoom room = new WaterBridgeRoom();
// 设置房间位置和尺寸
room.set(new Rect(10, 10, 20, 20));
// 在关卡中绘制（使用父类实现）
room.paint(level);
// 房间会自动生成水桥布局，空间区域为 WATER，桥梁为 EMPTY_SP
```

### 扩展示例
如需自定义桥梁宽度，可以继承并覆写相应方法：

```java
public class CustomWaterBridgeRoom extends WaterBridgeRoom {
    @Override
    protected int maxBridgeWidth(int roomDimension) {
        return Math.min(roomDimension / 3, 4); // 更宽的桥梁
    }
}
```

## 12. 开发注意事项

### 状态依赖
- 依赖于 Terrain.WATER 的存在和正确行为
- 依赖于父类 StandardBridgeRoom 的完整实现
- 物品/角色放置逻辑依赖于 spaceRect 字段的正确设置
- 水放置禁用确保了房间水地形的一致性

### 生命周期耦合
- 必须在房间位置和尺寸确定后才能调用 paint()
- paint() 调用后空间和桥梁区域即固定
- 物品/角色放置必须在 paint() 之后进行验证

### 常见陷阱
- 修改地形类型时需确保新地形与其他系统兼容
- WATER 地形的具体效果需要查看对应的交互逻辑
- 桥梁宽度的动态调整确保了不同尺寸房间的游戏体验平衡

## 13. 修改建议与扩展点

### 适合扩展的位置
- maxBridgeWidth() 方法：自定义桥梁宽度计算
- 可以添加桥梁地形的自定义（当前使用父类默认的 EMPTY_SP）
- 尺寸约束：根据设计需求调整最小尺寸

### 不建议修改的位置
- 基本的桥式房间逻辑（由父类处理）
- 门处理逻辑（由父类实现）
- 水放置禁用逻辑（确保房间水地形的一致性）

### 重构建议
当前实现非常简洁，主要依赖父类的功能，符合单一职责原则。如果需要更多自定义功能，建议创建新的子类而不是修改现有逻辑。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点