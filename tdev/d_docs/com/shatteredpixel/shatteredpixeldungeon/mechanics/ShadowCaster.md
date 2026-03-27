# ShadowCaster - 视野计算类

## 概述
`ShadowCaster` 类实现了基于递归阴影投射算法（Recursive Shadowcasting）的视野（Field of View, FOV）计算。该算法用于确定玩家或其他实体在游戏世界中可以看到的区域。此实现基于 Rogue Basin 上的经典算法：http://www.roguebasin.com/index.php?title=FOV_using_recursive_shadowcasting

## 主要功能
- 计算从指定源点出发的圆形视野范围
- 支持最大 20 格的距离限制
- 处理障碍物阻挡，确保视野不会穿透墙壁或其他阻挡物
- 通过八方向扫描（octant scanning）实现完整的 360 度视野计算

## 核心常量
- `MAX_DISTANCE = 20`: 最大视野距离，超过此距离的视野计算将被截断

## 预计算数据
- `rounding[][]`: 二维数组，用于将方形视野转换为圆形视野。对于每个距离和偏移量，存储对应的圆形边界值。

### rounding 数组计算逻辑
```java
rounding[i][j] = (int)Math.min(
    j,
    Math.round( i * Math.cos( Math.asin( j / (i + 0.5) ))));
```
此公式基于三角函数计算，在给定距离 `i` 和偏移量 `j` 的情况下，确定圆形视野的边界。

## 主要方法

### castShadow 方法
```java
public static void castShadow(int x, int y, int w, boolean[] fieldOfView, boolean[] blocking, int distance)
```

**参数说明：**
- `x, y`: 视野源点的坐标
- `w`: 游戏地图的宽度
- `fieldOfView`: 布尔数组，用于存储计算结果（true 表示可见）
- `blocking`: 布尔数组，标识哪些格子是阻挡物（true 表示阻挡）
- `distance`: 视野计算的最大距离

**执行流程：**
1. 初始化 `fieldOfView` 数组为全 false
2. 将源点设置为可见（true）
3. 按顺时针方向扫描八个八分圆（octants）
4. 使用异常处理确保稳定性，出现错误时重置视野数组

### scanOctant 方法（私有）
```java
private static void scanOctant(int distance, boolean[] fov, boolean[] blocking, int row,
                               int x, int y, int w, double lSlope, double rSlope,
                               int mX, int mY, boolean mXY)
```

**功能：** 扫描单个 45 度八分圆的视野范围，通过镜像变换（X轴、Y轴、X=Y对角线）来构建完整的视野。

**关键特性：**
- **斜率计算**: 使用双精度浮点数精确计算视野边界
- **阻挡处理**: 当遇到阻挡物时，递归调用自身以处理被阻挡区域后的可见区域
- **边缘优化**: 在距离为 2 时特殊处理角落填充，避免对角线移动受到过度惩罚
- **中心偏移**: 所有计算都基于单元格中心（偏移 0.5）

## 算法特点
1. **高效性**: 时间复杂度为 O(n²)，其中 n 是视野半径
2. **准确性**: 基于几何学原理，提供精确的视线计算
3. **鲁棒性**: 包含异常处理机制，确保游戏稳定性
4. **灵活性**: 支持自定义阻挡规则和距离限制

## 使用场景
- 玩家视野更新
- 敌人 AI 视野检测  
- 光源效果计算
- 任何需要确定可见区域的游戏机制

## 注意事项
- 该类是 `final` 类，不能被继承
- 所有方法都是静态方法，可以直接调用
- 输入数组必须具有正确的大小（通常为地图宽度 × 地图高度）
- 距离参数会自动限制在 `MAX_DISTANCE` 范围内