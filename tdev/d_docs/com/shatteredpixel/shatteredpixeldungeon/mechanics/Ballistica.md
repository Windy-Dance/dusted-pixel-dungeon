# Ballistica - 弹道计算类

## 概述
`Ballistica` 类实现了直线弹道（ballistic trajectory）的计算，用于确定从起点到终点的直线路径，同时处理各种碰撞检测。该类是 Shattered Pixel Dungeon 中所有直线效果（如投射物、魔法光束、近战攻击范围等）的基础。

## 核心功能
- 计算两点之间的精确直线路径
- 支持多种碰撞检测条件（目标点、角色、地形阻挡）
- 提供路径子段提取功能
- 处理游戏世界边界和特殊地形

## 主要属性
- `path`: ArrayList<Integer>，存储完整的路径格子（包括碰撞后的格子）
- `sourcePos`: Integer，起始位置
- `collisionPos`: Integer，碰撞发生的位置
- `collisionProperties`: Integer，碰撞属性参数
- `dist`: Integer，到碰撞点的距离（路径索引）

## 碰撞参数常量

### 基础参数
- `STOP_TARGET = 1`: 在目标格子处停止
- `STOP_CHARS = 2`: 遇到第一个角色时停止  
- `STOP_SOLID = 4`: 遇到固体地形时停止
- `IGNORE_SOFT_SOLID = 8`: 忽略软固体地形（如门、蛛网）

### 预定义组合
- `PROJECTILE = STOP_TARGET | STOP_CHARS | STOP_SOLID`: 投射物行为
- `MAGIC_BOLT = STOP_CHARS | STOP_SOLID`: 魔法光束行为  
- `WONT_STOP = 0`: 不停止，直达目标

## 构造方法
```java
public Ballistica(int from, int to, int params)
```

**参数说明：**
- `from`: 起始格子索引
- `to`: 目标格子索引  
- `params`: 碰撞参数（使用位运算组合上述常量）

**初始化流程：**
1. 设置源位置和碰撞属性
2. 调用 `build()` 方法计算完整路径
3. 确定碰撞位置和距离
4. 处理边界情况（空路径、单格路径等）

## 路径计算算法 (build 方法)

### 坐标转换
- 将格子索引转换为二维坐标：`x = pos % width`, `y = pos / width`
- 使用 Bresenham 直线算法的变体进行路径计算

### 碰撞检测顺序
1. **固体地形检测**: 检查 `passable` 和 `avoid` 数组，确保不会在错误的地形上停止
2. **固体阻挡检测**: 检查 `solid` 数组，支持忽略软固体的选项
3. **角色检测**: 使用 `Actor.findChar()` 查找路径上的角色
4. **目标点检测**: 如果启用 `STOP_TARGET`，在到达目标点时停止

### 算法特点
- **增量式计算**: 使用误差项（err）控制步进，确保直线精度
- **双方向步进**: 根据 dx/dy 的大小关系选择主要步进方向
- **边界安全**: 持续检查 `Dungeon.level.insideMap()` 确保不越界

## 辅助方法

### collide 方法（私有）
```java
private void collide(int cell)
```
记录第一次碰撞发生的位置，确保只记录首个碰撞点。

### subPath 方法
```java
public List<Integer> subPath(int start, int end)
```
**功能**: 返回路径的指定子段（包含起止点）

**参数说明:**
- `start`: 子路径起始索引
- `end`: 子路径结束索引

**安全特性:**
- 自动限制结束索引不超过路径长度
- 异常处理，出错时返回空列表而非抛出异常
- 包含错误报告机制（`ShatteredPixelDungeon.reportException`）

## 使用模式

### 常见用例
```java
// 投射物路径计算
Ballistica projectile = new Ballistica(from, to, Ballistica.PROJECTILE);
List<Integer> hitPath = projectile.subPath(1, projectile.dist);

// 魔法光束计算  
Ballistica bolt = new Ballistica(from, to, Ballistica.MAGIC_BOLT);
Set<Integer> affectedCells = new HashSet<>(bolt.path);
```

### 路径解释
- `path.get(0)` 总是等于 `sourcePos`
- `subPath(1, dist)` 返回从源点后一格到碰撞点的路径（最常用）
- 完整的 `path` 包含碰撞后的延续路径（如果有）

## 设计考虑

### 性能优化
- 使用 ArrayList 而非 LinkedList，便于随机访问
- 预分配合理的初始容量（虽然代码中未显式指定）
- 最小化对象创建，重用现有数据结构

### 错误处理
- 全面的边界检查防止数组越界
- 异常捕获确保游戏稳定性
- 合理的默认值处理（如空路径时的行为）

### 扩展性
- 碰撞参数系统支持灵活的自定义行为
- 可以轻松添加新的碰撞条件
- 路径数据结构支持各种后处理操作

## 注意事项
- 路径计算基于游戏地图的当前状态
- 角色检测依赖于 Actor 系统的实时状态
- 地形属性（passable/solid/avoid）由 Dungeon.level 提供
- 所有坐标都是格子索引（一维），需要配合 Dungeon.level 进行坐标转换