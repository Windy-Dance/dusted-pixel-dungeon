# ConeAOE - 锥形范围效果类

## 概述
`ConeAOE` 类实现了锥形范围效果（Area of Effect）的计算。它通过组合多个弹道（Ballistica）射线来创建一个锥形区域，用于模拟游戏中各种锥形攻击或效果，如龙息、魔法锥形冲击波等。

## 设计原理
该类基于核心射线（core ray）向外扩展，在指定角度范围内投射多条射线，形成一个扇形区域。每条射线都使用 Ballistica 算法进行精确的路径计算，确保能够正确处理游戏世界中的阻挡物。

## 核心属性
- `coreRay`: Ballistica 对象，表示锥形的核心射线（中心线）
- `outerRays`: ArrayList<Ballistica>，存储构成锥形外边缘的射线
- `rays`: ArrayList<Ballistica>，存储构成整个锥形的所有射线
- `cells`: HashSet<Integer>，存储锥形覆盖的所有游戏格子（cell）

## 构造方法

### 基础构造方法
```java
public ConeAOE(Ballistica core, float degrees)
```
**参数说明：**
- `core`: 核心射线，定义锥形的中心方向和基础距离
- `degrees`: 锥形的总角度（以度为单位），例如 90 表示 90 度的锥形

### 完整构造方法
```java
public ConeAOE(Ballistica core, float maxDist, float degrees, int ballisticaParams)
```
**参数说明：**
- `core`: 核心射线
- `maxDist`: 最大距离限制（真实距离，非游戏格子距离）
- `degrees`: 锥形角度
- `ballisticaParams`: Ballistica 参数，控制射线的行为（如是否在角色处停止、是否被地形阻挡等）

## 实现细节

### 坐标系统转换
- 使用 `PointF` 进行精确的浮点坐标计算，而非游戏的整数格子坐标
- 将游戏格子坐标转换为中心点坐标（加 0.5 偏移）
- 所有三角函数计算都基于真实坐标系

### 距离限制处理
```java
if (PointF.distance(fromP, toP) > maxDist){
    toP = PointF.inter(fromP, toP, maxDist/PointF.distance(fromP, toP));
}
```
如果核心射线的距离超过最大限制，则将目标点调整到最大距离位置。

### 半径计算
- 在计算出的基础半径上增加 0.5，确保锥形能够到达目标格子的边缘而非中心

### 射线扫描算法
1. **角度遍历**: 从初始角度 + degrees/2 到初始角度 - degrees/2，以 0.5 度为步长进行顺时针扫描
2. **边界点计算**: 对每个角度，计算锥形外弧上的边界点
3. **内部填充**: 当锥形半径较大时（≥4），还会计算内层弧线上的点，避免射线之间的空隙
4. **格子映射**: 将计算出的真实坐标映射回游戏格子坐标，并进行边界检查

### 射线生成
- 为每个找到的目标格子生成一条 Ballistica 射线
- 将所有射线路径上的格子添加到 `cells` 集合中
- 区分外边缘射线（`outerRays`）和所有射线（`rays`）

## 关键特性
1. **高精度**: 使用浮点坐标和小角度步长（0.5度）确保锥形边缘平滑
2. **完整性**: 通过内外双层扫描避免漏掉格子
3. **灵活性**: 支持自定义距离限制和碰撞参数
4. **效率**: 使用 HashSet 避免重复格子，LinkedHashSet 保持射线顺序

## 使用场景
- 龙类敌人的火焰吐息攻击
- 法师的锥形冰霜冲击
- 特殊武器的范围攻击效果
- 任何需要锥形范围检测的游戏机制

## 注意事项
- 不包含核心射线本身（因为核心射线的碰撞属性可能与锥形不同）
- 所有坐标计算都考虑了游戏世界的边界限制
- 使用 `Dungeon.level.pointToCell()` 和 `Dungeon.level.cellToPoint()` 进行坐标转换
- 结果存储在 `cells` 集合中，可以直接用于游戏逻辑处理