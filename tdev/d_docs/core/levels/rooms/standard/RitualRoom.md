# RitualRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\RitualRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends PatchRoom |
| **代码行数** | 114 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
RitualRoom 类负责生成具有仪式祭坛主题的房间布局。它使用补丁系统（Patch system）在房间内部创建装饰性区域，并在房间中心放置由雕像、余烬和基座组成的仪式祭坛结构，同时放置随机奖励物品。

### 系统定位
作为补丁房间（PatchRoom）的一种具体实现，RitualRoom 在关卡生成过程中提供具有特定主题和高价值奖励的特殊房间，增加游戏的探索价值和视觉多样性。

### 不负责什么
- 不负责补丁系统的底层实现（由父类 PatchRoom 处理）
- 不处理特殊的敌人生成规则
- 不管理房间连接的特殊逻辑
- 不负责具体的地形连通性验证（由父类处理）

## 3. 结构总览

### 主要成员概览
- 继承自父类的 patch 字段用于存储补丁数据
- 受保护方法 placeloot() 用于自定义奖励放置逻辑

### 主要逻辑块概览
- 尺寸约束逻辑：确保最小尺寸为9x9
- 补丁配置：填充率约30%-60%，无聚类，确保路径连通，清理边缘
- 中心祭坛生成：7x7空地区域，8个雕像，3x3余烬区域，中心基座
- 奖励放置逻辑：50%概率放置奖品物品，否则放置随机药剂或卷轴

### 生命周期/调用时机
- 房间实例化时自动设置尺寸分类
- 关卡绘制阶段调用 paint() 方法生成实际地形和奖励

## 4. 继承与协作关系

### 父类提供的能力
从 PatchRoom 继承：
- patch 字段用于存储补丁数据
- setupPatch() 方法：生成并验证补丁连通性
- fillPatch() 方法：将补丁应用到关卡地形
- cleanDiagonalEdges() 方法：清理对角线边缘
- xyToPatchCoords() 方法：坐标转换
- 抽象方法框架：fill(), clustering(), ensurePath(), cleanEdges()

从 StandardRoom 继承：
- SizeCategory 枚举和相关字段
- minWidth()/minHeight() 基础实现
- setSizeCat() 相关方法
- sizeFactor(), mobSpawnWeight(), connectionWeight() 等辅助方法

从 Room 继承：
- 空间和连接逻辑（Rect 功能扩展）
- Door 内部类

### 覆写的方法
- minWidth()：返回 Math.max(super.minWidth(), 9)
- minHeight()：返回 Math.max(super.minHeight(), 9)
- sizeCatProbs()：返回 new float[]{6, 3, 1}
- fill()：返回基于房间面积的填充率（0.30 + scale/1024）
- clustering()：返回 0（无聚类）
- ensurePath()：返回 connected.size() > 0（确保连通性）
- cleanEdges()：返回 true（清理边缘）
- paint()：实现仪式房间的具体绘制逻辑
- placeloot()：自定义奖励放置逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 间接实现）
- Bundlable 接口（通过 Room 间接实现）

### 依赖的关键类
- Level：关卡数据结构和 findPrizeItem() 方法
- Terrain：地形类型定义（WALL, EMPTY, REGION_DECO, STATUE, EMBERS, PEDESTAL）
- Painter：关卡绘制工具（fill(), set()）
- Generator：随机物品生成器
- Item：物品基类
- Random：随机数生成
- Point：几何计算
- Patch：补丁生成系统（通过父类）

### 使用者
- StandardRoom.createRoom() 静态工厂方法
- LevelGenerator：关卡生成器在构建房间网络时

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
- **patch** (boolean[])：继承自 PatchRoom，存储补丁数据，表示哪些位置应该填充特定地形

## 6. 构造与初始化机制

### 构造器
使用默认构造器，无自定义构造逻辑。

### 初始化块
无显式初始化块。

### 初始化注意事项
- sizeCat 字段在 StandardRoom 的初始化块中通过 setSizeCat() 自动设置
- patch 字段在 setupPatch() 调用时初始化

## 7. 方法详解

### minWidth()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：确保 RitualRoom 的最小宽度至少为9格

**参数**：无

**返回值**：int，返回父类 minWidth() 和 9 的较大值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(super.minWidth(), 9);
```

**边界情况**：当父类 minWidth() 返回值小于9时，强制返回9

### minHeight()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：确保 RitualRoom 的最小高度至少为9格

**参数**：无

**返回值**：int，返回父类 minHeight() 和 9 的较大值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(super.minHeight(), 9);
```

**边界情况**：当父类 minHeight() 返回值小于9时，强制返回9

### sizeCatProbs()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：定义不同尺寸分类的出现概率

**参数**：无

**返回值**：float[]，包含三个元素 [6, 3, 1] 分别对应 NORMAL/LARGE/GIANT

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return new float[]{6, 3, 1};
```

**边界情况**：概率数组长度必须与 SizeCategory 枚举值数量一致

### fill()
**可见性**：protected

**是否覆写**：是，实现自 PatchRoom 抽象方法

**方法职责**：定义补丁的填充率，基于房间面积动态调整

**参数**：无

**返回值**：float，填充率（30%-60%）

**前置条件**：房间尺寸已确定

**副作用**：无

**核心实现逻辑**：
```java
int scale = Math.min(width()*height(), 18*18);
return 0.30f + scale/1024f;
```

**边界情况**：
- 最小填充率约30%（4x4房间）
- 最大填充率约60%（18x18房间）
- 但实际填充率较低，因为中心7x7区域会被覆盖

### clustering()
**可见性**：protected

**是否覆写**：是，实现自 PatchRoom 抽象方法

**方法职责**：定义补丁的聚类程度

**参数**：无

**返回值**：int，返回 0（无聚类）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return 0;
```

**边界情况**：无聚类意味着补丁分布更均匀

### ensurePath()
**可见性**：protected

**是否覆写**：是，实现自 PatchRoom 抽象方法

**方法职责**：决定是否确保补丁区域的路径连通性

**参数**：无

**返回值**：boolean，如果房间有连接则返回 true

**前置条件**：房间连接已建立

**副作用**：无

**核心实现逻辑**：
```java
return connected.size() > 0;
```

**边界情况**：孤立房间不需要路径连通性

### cleanEdges()
**可见性**：protected

**是否覆写**：是，实现自 PatchRoom 抽象方法

**方法职责**：决定是否清理补丁的对角线边缘

**参数**：无

**返回值**：boolean，始终返回 true

**前置条件**：无

**副作用**：轻微降低填充率但使外观更整洁

**核心实现逻辑**：
```java
return true;
```

**边界情况**：总是清理边缘

### paint(Level level)
**可见性**：public

**是否覆写**：是，覆写自 Room 抽象方法

**方法职责**：在关卡中实际绘制 RitualRoom 的地形、仪式祭坛和奖励

**参数**：
- `level` (Level)：要绘制到的关卡实例

**返回值**：void

**前置条件**：房间必须已经设置了有效的位置和尺寸

**副作用**：修改 level 对象的 map 数组并放置奖励物品

**核心实现逻辑**：
1. 使用 Painter.fill() 填充整个房间为墙壁 (Terrain.WALL)
2. 填充内部1格区域为空地 (Terrain.EMPTY)
3. 获取房间中心点
4. 调用 setupPatch() 生成补丁数据
5. 调用 fillPatch() 将补丁应用为 REGION_DECO 地形
6. 填充中心7x7区域为空地，覆盖部分补丁
7. 在特定位置放置8个雕像形成环绕效果
8. 填充中心3x3区域为余烬 (EMBERS)
9. 在绝对中心放置基座 (PEDESTAL)
10. 调用 placeloot() 在中心放置奖励物品
11. 设置所有连接门为常规门类型

**边界情况**：
- 最小房间尺寸9x9确保有足够的空间放置完整祭坛
- 补丁生成确保与门的连通性（如果房间有连接）

### placeloot(Level level, Point p)
**可见性**：protected

**是否覆写**：否（可被子类覆写）

**方法职责**：在指定位置放置仪式房间的奖励物品

**参数**：
- `level` (Level)：关卡实例
- `p` (Point)：地图中的位置

**返回值**：void

**前置条件**：位置必须有效

**副作用**：在关卡中放置物品

**核心实现逻辑**：
- 50%概率尝试放置奖品物品（level.findPrizeItem()）
- 如果没有奖品物品，则随机选择药剂或卷轴类别
- 使用 Generator.random() 生成具体物品
- 调用 level.drop() 放置物品

**边界情况**：
- findPrizeItem() 可能返回 null，此时退回到随机物品
- Generator.random() 确保总有有效物品生成

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是 Room/StandardRoom 协议的一部分
- 没有额外的公共 API

### 内部辅助方法
- placeloot() 是受保护的扩展点，可用于自定义奖励逻辑
- 其他 protected 方法主要用于满足父类抽象方法的要求

### 扩展入口
- 可以通过继承覆写 placeloot() 自定义奖励类型
- 可以调整补丁参数（fill, clustering 等）来自定义外观
- 可以覆写其他 StandardRoom 方法来自定义行为

## 9. 运行机制与调用链

### 创建时机
- 在关卡生成过程中的房间创建阶段
- 通过 StandardRoom.createRoom() 静态工厂方法随机选择创建

### 调用者
- StandardRoom.createRoom()：通过反射创建实例
- LevelGenerator：在构建房间网络时调用 paint()

### 被调用者
- Patch.generate()：通过父类生成补丁数据
- PathFinder.buildDistanceMap()：验证连通性
- Painter.fill()/set()：用于地形绘制
- Level.findPrizeItem()/drop()：用于放置奖励物品
- Generator.random()：生成随机物品
- super 方法：获取父类基础功能

### 系统流程位置
1. 房间实例创建 → 2. 尺寸设置 → 3. 房间网络构建 → 4. paint() 调用 → 5. 补丁生成 → 6. 祭坛绘制 → 7. 奖励放置

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
项目内未找到官方对应译名

### 依赖的资源
- Terrain 常量：WALL, EMPTY, REGION_DECO, STATUE, EMBERS, PEDESTAL
- Generator 配置：POTION 和 SCROLL 类别的物品生成
- LEVEL.findPrizeItem()：奖品物品生成系统
- Patch 系统：补丁生成算法

### 中文翻译来源
在 levels_zh.properties 中未找到 RitualRoom 相关翻译

## 11. 使用示例

### 基本用法
```java
// RitualRoom 通常由关卡生成器自动创建，不建议手动实例化
RitualRoom room = new RitualRoom();
// 设置房间位置和尺寸
room.set(new Rect(10, 10, 20, 20));
// 在关卡中绘制
room.paint(level);
// 房间会自动生成仪式祭坛和奖励物品
```

### 扩展示例
如需自定义奖励逻辑，可以继承 RitualRoom：

```java
public class CustomRitualRoom extends RitualRoom {
    @Override
    protected void placeloot(Level level, Point p) {
        // 总是放置特定类型的物品
        level.drop(new WandOfFireblast(), level.pointToCell(p));
    }
    
    @Override
    protected float fill() {
        return 0.5f; // 固定50%填充率
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖于房间的 left/right/top/bottom 坐标已正确设置
- 依赖于 Level.findPrizeItem() 和 Generator 的正确实现
- 依赖于 Terrain 常量的存在和正确行为
- 补丁生成依赖于房间连接信息（ensurePath()）

### 生命周期耦合
- 必须在房间位置和尺寸确定后才能调用 paint()
- paint() 调用后所有地形和奖励即固定
- 奖励物品放置必须在关卡完全初始化后进行

### 常见陷阱
- 修改补丁参数可能影响房间的连通性和可玩性
- 中心祭坛结构需要足够的空间（最小9x9），小房间无法容纳
- 奖励物品的选择逻辑需要确保物品系统的兼容性
- 补丁清理边缘会轻微降低实际填充率

## 13. 修改建议与扩展点

### 适合扩展的位置
- placeloot() 方法：自定义奖励类型和概率
- fill() 方法：调整补丁密度
- 中心祭坛结构：添加更多装饰元素或改变布局
- 尺寸约束：根据设计需求调整最小尺寸

### 不建议修改的位置
- 基础补丁生成逻辑（由父类处理）
- 门连接逻辑（确保游戏可玩性）
- 中心祭坛的核心结构（影响视觉识别）

### 重构建议
当前实现较为清晰，但中心祭坛的硬编码坐标可以提取为常量以提高可维护性。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点