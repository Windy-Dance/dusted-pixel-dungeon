# RuinsRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\RuinsRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends PatchRoom |
| **代码行数** | 100 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
RuinsRoom 类负责生成废墟主题的房间布局。它使用补丁系统（Patch system）在房间内部创建随机的墙壁和瓦砾区域，通过分析每个补丁点的相邻墙壁数量来决定将其渲染为完整墙壁还是装饰性瓦砾（REGION_DECO）。

### 系统定位
作为补丁房间（PatchRoom）的一种具体实现，RuinsRoom 在关卡生成过程中提供具有废墟破坏感的房间类型，增加关卡环境的多样性和视觉复杂性。

### 不负责什么
- 不负责补丁系统的底层实现（由父类 PatchRoom 处理）
- 不处理特殊的敌人生成规则
- 不管理物品放置逻辑
- 不负责房间合并的具体地形类型选择

## 3. 结构总览

### 主要成员概览
- 继承自父类的 patch 字段用于存储补丁数据
- 自定义 paint() 方法实现废墟渲染逻辑

### 主要逻辑块概览
- 尺寸分类概率：定义 NORMAL/LARGE/GIANT 的出现概率为 4:2:1
- 补丁配置：填充率约30%-60%，无聚类，确保路径连通，清理边缘
- 废墟渲染逻辑：基于相邻墙壁数量决定地形类型
- 合并逻辑：允许与任何房间合并

### 生命周期/调用时机
- 房间实例化时自动设置尺寸分类
- 关卡绘制阶段调用 paint() 方法生成实际地形

## 4. 继承与协作关系

### 父类提供的能力
从 PatchRoom 继承：
- patch 字段用于存储补丁数据
- setupPatch() 方法：生成并验证补丁连通性
- cleanDiagonalEdges() 方法：清理对角线边缘
- xyToPatchCoords() 方法：坐标转换
- 抽象方法框架：fill(), clustering(), ensurePath(), cleanEdges()

从 StandardRoom 继承：
- SizeCategory 枚举和相关字段
- setSizeCat() 相关方法
- sizeFactor(), mobSpawnWeight(), connectionWeight() 等辅助方法

From Room 继承：
- 空间和连接逻辑（Rect 功能扩展）
- Door 内部类

### 覆写的方法
- sizeCatProbs()：返回 new float[]{4, 2, 1}
- canMerge()：返回 true（总是允许合并）
- fill()：返回基于房间面积的填充率（0.30 + scale/1024）
- clustering()：返回 0（无聚类）
- ensurePath()：返回 connected.size() > 0（确保连通性）
- cleanEdges()：返回 true（清理边缘）
- paint()：实现废墟房间的具体绘制逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 间接实现）
- Bundlable 接口（通过 Room 间接实现）

### 依赖的关键类
- Level：关卡数据结构
- Terrain：地形类型定义（WALL, EMPTY, REGION_DECO）
- Painter：关卡绘制工具（fill()）
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
- **patch** (boolean[])：继承自 PatchRoom，存储补丁数据，表示哪些位置应该填充废墟地形

## 6. 构造与初始化机制

### 构造器
使用默认构造器，无自定义构造逻辑。

### 初始化块
无显式初始化块。

### 初始化注意事项
- sizeCat 字段在 StandardRoom 的初始化块中通过 setSizeCat() 自动设置
- patch 字段在 setupPatch() 调用时初始化
- 最小尺寸使用父类默认值（StandardRoom.minWidth()/minHeight() 返回 sizeCat.minDim）

## 7. 方法详解

### sizeCatProbs()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：定义不同尺寸分类的出现概率

**参数**：无

**返回值**：float[]，包含三个元素 [4, 2, 1] 分别对应 NORMAL/LARGE/GIANT

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return new float[]{4, 2, 1};
```

**边界情况**：概率数组长度必须与 SizeCategory 枚举值数量一致

### canMerge(Level l, Room other, Point p, int mergeTerrain)
**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：决定是否允许与其他房间合并

**参数**：
- `l` (Level)：关卡实例
- `other` (Room)：其他房间
- `p` (Point)：合并点
- `mergeTerrain` (int)：合并地形类型

**返回值**：boolean，始终返回 true

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return true;
```

**边界情况**：总是允许合并，不进行任何条件检查

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

**方法职责**：在关卡中实际绘制 RuinsRoom 的废墟地形

**参数**：
- `level` (Level)：要绘制到的关卡实例

**返回值**：void

**前置条件**：房间必须已经设置了有效的位置和尺寸

**副作用**：修改 level 对象的 map 数组

**核心实现逻辑**：
1. 使用 Painter.fill() 填充整个房间为墙壁 (Terrain.WALL)
2. 填充内部1格区域为空地 (Terrain.EMPTY)
3. 设置所有连接门为常规门类型
4. 调用 setupPatch() 生成补丁数据
5. 遍历内部每个位置：
   - 如果该位置在补丁中（patch[xyToPatchCoords(j, i)] 为 true）：
     - 对于非边缘位置：计算相邻四个方向的补丁点数量
       - 基于相邻数量和随机因素决定是否为墙壁
       - 相邻越多，越可能保持为墙壁
     - 对于边缘位置：总是设为墙壁
     - 根据决定结果设置地形为 WALL 或 REGION_DECO

**边界情况**：
- 边缘位置（距离房间边界1格内）总是设为墙壁以保持结构完整性
- 内部位置根据相邻墙壁数量和随机性决定，创造自然的废墟效果
- 单独的补丁点更可能变成瓦砾（REGION_DECO），而集群的补丁点更可能保持为墙壁

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是 Room/StandardRoom 协议的一部分
- 没有额外的公共 API

### 内部辅助方法
- 所有 protected 方法主要用于满足父类抽象方法的要求
- paint() 中的废墟渲染逻辑不应被外部直接调用

### 扩展入口
- 可以通过继承调整补丁参数（fill, clustering 等）来自定义废墟密度
- 可以覆写 paint() 方法自定义废墟渲染逻辑
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
- Painter.fill()：用于基础地形绘制
- Random.Int()：用于废墟渲染的随机决策
- super 方法：获取父类基础功能

### 系统流程位置
1. 房间实例创建 → 2. 尺寸设置 → 3. 房间网络构建 → 4. paint() 调用 → 5. 补丁生成 → 6. 废墟地形渲染

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
项目内未找到官方对应译名

### 依赖的资源
- Terrain 常量：WALL, EMPTY, REGION_DECO
- REGION_DECO 可能是特定区域的装饰性地形，用于表现瓦砾效果
- Patch 系统：补丁生成算法

### 中文翻译来源
在 levels_zh.properties 中未找到 RuinsRoom 相关翻译

## 11. 使用示例

### 基本用法
```java
// RuinsRoom 通常由关卡生成器自动创建，不建议手动实例化
RuinsRoom room = new RuinsRoom();
// 设置房间位置和尺寸
room.set(new Rect(10, 10, 20, 20));
// 在关卡中绘制
room.paint(level);
// 房间会自动生成废墟风格的地形
```

### 扩展示例
如需自定义废墟渲染逻辑，可以继承 RuinsRoom：

```java
public class CustomRuinsRoom extends RuinsRoom {
    @Override
    protected float fill() {
        return 0.7f; // 更高的填充率，更密集的废墟
    }
    
    @Override
    public void paint(Level level) {
        // 自定义废墟渲染逻辑
        super.paint(level);
        // 添加额外的装饰
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖于房间的 left/right/top/bottom 坐标已正确设置
- 依赖于 Terrain 常量的存在和正确行为
- 补丁生成依赖于房间连接信息（ensurePath()）

### 生命周期耦合
- 必须在房间位置和尺寸确定后才能调用 paint()
- paint() 调用后所有地形即固定
- 废墟渲染逻辑依赖于补丁数据的正确生成

### 常见陷阱
- 修改补丁参数可能影响房间的连通性和可玩性
- 废墟渲染的随机性可能导致相同种子下产生不同结果（但代码中使用了确定性随机）
- 边缘位置的处理确保了房间结构的完整性，不应随意修改

## 13. 修改建议与扩展点

### 适合扩展的位置
- fill() 方法：调整废墟密度
- 废墟渲染逻辑：添加更多地形类型或改变判定规则
- 尺寸分类概率：调整不同大小房间的出现频率

### 不建议修改的位置
- 基础补丁生成逻辑（由父类处理）
- 门连接逻辑（确保游戏可玩性）
- 边缘位置总是设为墙壁的逻辑（保持结构完整性）

### 重构建议
当前实现较为清晰，但废墟渲染的相邻计算逻辑可以提取为单独的私有方法以提高可读性。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点