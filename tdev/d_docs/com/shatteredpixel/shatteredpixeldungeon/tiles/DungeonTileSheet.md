# DungeonTileSheet.java - 瓦片视觉定义中心

## 概述
`DungeonTileSheet` 是瓦片渲染系统的核心，负责定义所有瓦片的视觉常量和自动拼接逻辑。它实现了基于16x16像素网格的瓦片系统，支持自动邻接检测、视觉变体和特殊效果。

## 瓦片分类体系

### 基础地板瓦片
- **标准地板**：`FLOOR`, `FLOOR_DECO`, `FLOOR_ALT_1`, `FLOOR_ALT_2`
- **特殊地形**：`GRASS`, `EMBERS`, `HIGH_GRASS`, `FURROWED_GRASS`
- **功能区域**：`ENTRANCE`, `EXIT`, `WELL`, `EMPTY_WELL`, `PEDESTAL`
- **深渊处理**：`CHASM` 及其拼接变体（地板、墙壁、水域）

### 水域瓦片
- **基础水域**：`WATER` (16个拼接变体)
- **拼接支持**：自动与地板、草地、墙壁等邻接瓦片拼接

### 墙壁相关瓦片
- **标准墙壁**：`WALL`, `WALL_DECO`
- **门类瓦片**：`DOOR`, `OPEN_DOOR`, `LOCKED_DOOR`, `HERO_LKD_DR`, `SECRET_DOOR`
- **抬升墙壁**：`RAISED_WALL`, `RAISED_DOOR` 等立体效果
- **悬垂效果**：`WALL_OVERHANG`, `DOOR_OVERHANG` 等遮挡效果

### 特殊功能瓦片
- **陷阱瓦片**：各种陷阱类型的支持
- **互动对象**：`BOOKSHELF`, `BARRICADE`, `ALCHEMY` (炼金锅)
- **装饰瓦片**：`CUSTOM_DECO`, `REGION_DECO`  as 区域特定装饰

## 核心功能

### 自动拼接系统
```java
// 检查是否支持拼接
public static boolean wallStitcheable(int tile)
public static boolean waterStitcheable(int tile)

// 生成拼接视觉
public static int stitchWaterTile(...)
public static int stitchChasmTile(...)
public static int stitchInternalWallTile(...)
```

### 视觉变体系统
- **常见变体** (50%概率)：基础外观的轻微变化
- **稀有变体** (5%概率)：更显著的视觉差异  
- **种子控制**：使用地牢种子确保确定性生成

```java
// 应用变体
public static int getVisualWithAlts(int visual, int pos)

// 初始化变体数组
public static void setupVariance(int size, long seed)
```

### 门类检测
```java
// 检测门类型
public static boolean doorTile(int tile)
```

## 技术实现细节

### 坐标系统
- 使用 `xy(x, y)` 方法将1-based坐标转换为线性索引
- 瓦片表宽度固定为16 (`WIDTH = 16`)
- 支持空瓦片常量 (`NULL_TILE = -1`)

### 数据结构
- **SparseArray**：用于拼接映射（节省内存）
- **HashSet**：用于快速成员检测
- **静态初始化块**：预填充所有映射关系

### 性能优化
- **缓存友好**：瓦片常量按功能分组存储
- **零分配**：查询方法避免创建临时对象
- **位操作优化**：内部计算使用位运算提高效率

## 使用模式

### 瓦片图层集成
各瓦片图层类通过静态方法访问瓦片定义：
```java
// DungeonTerrainTilemap 中的使用
int visual = DungeonTileSheet.FLOOR;
visual = DungeonTileSheet.getVisualWithAlts(visual, pos);
```

### 动态瓦片选择
根据邻接关系动态选择正确瓦片：
```java
// 墙壁拼接示例
if (DungeonTileSheet.wallStitcheable(neighborTile)) {
    int stitchedVisual = DungeonTileSheet.stitchInternalWallTile(...);
}
```

## 扩展性

### 添加新瓦片类型
1. 在相应分类区域添加新的常量定义
2. 更新拼接映射（如需要）
3. 在瓦片图层中添加渲染逻辑

### 自定义视觉变体
- 修改 `setupVariance()` 方法调整变体概率
- 添加新的变体常量到相应区域
- 更新 `getVisualWithAlts()` 的处理逻辑

## 设计原则

### 单一职责
- 专注瓦片定义，不处理渲染逻辑
- 提供纯净的数据接口给渲染系统

### 向后兼容
- 保持常量顺序稳定
- 避免破坏现有瓦片映射

### 内存效率
- 最小化对象创建
- 使用原始类型和数组
- 避免不必要的包装类

## 与其他系统的集成

### 地形系统
- 与 `Terrain` 类紧密集成
- 瓦片常量对应地形类型

### 节日系统  
- 支持节日特定的视觉变体
- 动态切换节日主题瓦片

### 关卡生成
- 为关卡生成器提供视觉反馈
- 支持区域特定的装饰瓦片

## 调试支持

### 开发者工具
- 调试模式下显示瓦片边界
- 支持快速切换视觉变体进行测试

### 错误处理
- 提供安全的默认值
- 避免空指针异常