# 瓦片包 (Tiles Package)

## 概述

`tiles` 包实现了 Shattered Pixel Dungeon 的 **分层瓦片渲染系统**，负责地牢的视觉呈现。该系统使用多个重叠的瓦片图层来创建丰富的视觉效果，包括基础地形、墙壁、植物、陷阱、战争迷雾等。

## 文件结构

- `DungeonTilemap.java` - 所有地牢瓦片图的抽象基类
- `DungeonTileSheet.java` - 中央瓦片常量定义和视觉选择逻辑
- `DungeonTerrainTilemap.java` - 主要地形瓦片渲染器  
- `DungeonWallsTilemap.java` - 墙壁瓦片渲染器（包含衔接和悬垂效果）
- `WallBlockingTilemap.java` - 墙壁阻挡视觉（用于战争迷雾）
- `FogOfWar.java` - 战争迷雾渲染管理器
- `TerrainFeaturesTilemap.java` - 地形特征渲染器（植物、陷阱、草地变化）
- `RaisedTerrainTilemap.java` - 抬升地形悬垂效果
- `GridTileMap.java` - 可选的网格叠加层
- `CustomTilemap.java` - 自定义瓦片装饰的抽象基类

---

## 分层渲染架构

瓦片渲染系统采用 **分层方法**，按以下顺序渲染：

1. **DungeonTerrainTilemap** - 基础地形层（地板、墙壁、门等）
2. **DungeonWallsTilemap** - 墙壁悬垂和衔接效果  
3. **TerrainFeaturesTilemap** - 覆盖特征（植物、陷阱、草地变化）
4. **RaisedTerrainTilemap** - 抬升地形悬垂（高草悬垂）
5. **WallBlockingTilemap** - 墙壁阻挡（隐藏墙后区域）
6. **FogOfWar** - 可见性覆盖层
7. **GridTileMap** - 可选网格叠加层（玩家辅助）

---

## DungeonTilemap.java

### 类描述
`DungeonTilemap` 是所有地牢瓦片图的抽象基类，提供坐标转换工具和通用功能。

### 主要方法

| 方法 | 描述 |
|------|------|
| `map(int[] data, int cols)` | 将地牢数据映射到瓦片图，保留对原始地图的引用 |
| `updateMap()` / `updateMapCell(int cell)` | 同步方法更新瓦片视觉效果 |
| `getTileVisual(int pos, int tile, boolean flat)` | 抽象方法，子类定义瓦片外观 |
| `screenToTile(int x, int y, boolean wallAssist)` | 将屏幕坐标转换为地牢单元格索引 |
| `tileToWorld(int pos)` / `tileCenterToWorld(int pos)` / `raisedTileCenterToWorld(int pos)` | 坐标转换工具 |
| `discover(int pos, int oldValue)` | 创建发现隐藏瓦片时的淡出动画 |

### 设计模式

- **模板方法模式**: `getTileVisual` 由子类实现具体逻辑
- **坐标工具集**: 提供完整的坐标系转换支持
- **动画支持**: 内置发现动画机制

---

## DungeonTileSheet.java

### 类描述
`DungeonTileSheet` 是中央瓦片注册表，包含所有瓦片常量和视觉选择逻辑。这是核心瓦片定义文件。

### 关键常量组

#### 地板瓦片
- `FLOOR`, `GRASS`, `EMBERS`, `ENTRANCE`, `EXIT`, `WELL`, `CHASM` 等

#### 水瓦片  
- `WATER` 及其衔接变体

#### 平坦瓦片
- `FLAT_WALL`, `FLAT_DOOR`, `FLAT_BOOKSHELF` 等

#### 抬升瓦片
- `RAISED_WALL`, `RAISED_DOOR`, `RAISED_HIGH_GRASS` 等

#### 悬垂瓦片
- `WALL_OVERHANG`, `DOOR_OVERHANG`, `STATUE_OVERHANG` 等

### 主要方法

| 方法 | 描述 |
|------|------|
| `wallStitcheable(int tile)` | 检查瓦片是否可以与墙壁邻居衔接 |
| `waterStitcheable(int tile)` | 检查瓦片是否可以与水衔接 |
| `stitchWaterTile(...)` / `stitchChasmTile(...)` | 生成衔接视觉效果 |
| `doorTile(int tile)` | 检查瓦片是否为门类型 |
| `getVisualWithAlts(int visual, int pos)` | 应用替代视觉变体（50%常见，5%稀有） |
| `setupVariance(int size, long seed)` | 设置随机变体数组用于瓦片外观 |

### 视觉变体系统

- **常见变体 (50%)**: 基础外观的轻微变化
- **稀有变体 (5%)**: 更显著的视觉差异
- **种子控制**: 使用地牢种子确保确定性生成

---

## DungeonTerrainTilemap.java

### 类描述
`DungeonTerrainTilemap` 是主要的地形渲染器，处理基础地牢地板/墙壁瓦片。

### 主要功能

`getTileVisual` 方法处理以下视觉效果：
- 直接地形（通过 `directVisuals` 映射）
- 水瓦片（带衔接效果）
- 裂缝瓦片（带衔接效果）
- 抬升墙壁、门、雕像、草地、炼金锅、路障
- 平坦地形变体

### 静态工具方法

- `tile(int pos, int tile)` - 创建地形瓦片图像的静态方法

---

## DungeonWallsTilemap.java

### 类描述
`DungeonWallsTilemap` 渲染墙壁悬垂、门框和抬升墙壁特征。

### 关键字段

- `skipCells` - HashSet 存储需要跳过渲染的位置

### 主要功能

`getTileVisual` 方法处理：
- 通过 `DungeonTileSheet.stitchInternalWallTile()` 的内部墙壁衔接
- 门的侧面视觉效果（开启/锁定/水晶门）
- 墙壁悬垂效果（覆盖门、出口、雕像、草地、炼金锅等）

### 交互支持

- `overlapsPoint()` / `overlapsScreenPoint()` - 始终返回 true 以支持交互

---

## WallBlockingTilemap.java

### 类描述
`WallBlockingTilemap` 管理墙壁阻挡瓦片，用于战争迷雾效果中遮挡墙壁后的可见性。

### 关键常量

- `CLEARED`, `BLOCK_NONE`, `BLOCK_RIGHT`, `BLOCK_LEFT`, `BLOCK_ALL`, `BLOCK_BELOW`

### 主要方法

| 方法 | 描述 |
|------|------|
| `updateMap()` | 更新所有单元格，强制边缘/不可发现单元格为清除状态 |
| `updateMapCell(int cell)` | 复杂逻辑确定阻挡状态，基于：墙壁可见性、邻居关系、迷雾隐藏状态、特殊关卡处理 |
| `fogHidden(int cell)` | 检查单元格是否被战争迷雾隐藏 |
| `updateArea(int cell, int radius)` / `updateArea(x, y, w, h)` | 更新特定区域 |

### 特殊关卡支持

- `HallsBossLevel`: 特殊的 Boss 关卡处理
- `MiningLevel`: 矿洞关卡的特殊逻辑

---

## FogOfWar.java

### 类描述
`FogOfWar` 渲染战争迷雾覆盖层，具有 4 种可见性状态和亮度级别。

### 关键组件

- `FOG_COLORS[][]` - 颜色矩阵：可见/已访问/已映射/不可见 × 亮度级别
- `VISITED`, `MAPPED`, `VISIBLE`, `INVISIBLE` - 可见性状态常量

### 主要方法

| 方法 | 描述 |
|------|------|
| `updateFog()` / `updateFog(Rect)` / `updateFog(cell, radius)` / `updateFogArea()` | 队列迷雾更新区域 |
| `updateTexture(...)` | 核心渲染逻辑，基于可见性数组、墙壁瓦片检测、半单元格填充、亮度设置填充 Pixmap |
| `fillCell()` / `fillLeft()` / `fillRight()` | 底层 Pixmap 填充操作 |
| `draw()` | 如果存在待处理更新则触发纹理更新 |

### 可见性状态

1. **VISIBLE**: 当前可见区域
2. **VISITED**: 曾经访问过的区域  
3. **MAPPED**: 已映射但未访问的区域
4. **INVISIBLE**: 完全不可见区域

---

## TerrainFeaturesTilemap.java

### 类描述
`TerrainFeaturesTilemap` 渲染覆盖特征，如植物、陷阱和草地变化。

### 关键字段

- `plants`, `traps` - Plant 和 Trap 对象的 SparseArrays

### 主要功能

`getTileVisual` 返回以下视觉效果：
- 陷阱图标（基于颜色+形状）
- 植物图像
- 草地类型变化（`HIGH_GRASS`, `FURROWED_GRASS`, `GRASS`）带有基于阶段的着色
- Ember 瓦片

### 静态工具方法

- `getTrapVisual(Trap trap)` / `getPlantVisual(Plant plant)` - 获取特征图像的静态方法
- `growPlant(int pos)` - 带有缩放补间效果的植物生长动画

### 动画支持

- 植物生长时播放缩放动画
- 陷阱激活时可能有特殊视觉效果

---

## RaisedTerrainTilemap.java

### 类描述
`RaisedTerrainTilemap` 渲染抬升地形悬垂（看起来抬升的草地）。

### 主要功能

`getTileVisual` 返回悬垂视觉效果：
- `HIGH_GRASS` → `HIGH_GRASS_UNDERHANG`
- `FURROWED_GRASS` → `FURROWED_UNDERHANG`

### 视觉效果

- 创建深度感和层次感
- 使高草看起来从地面升起

---

## GridTileMap.java

### 类描述
`GridTileMap` 提供可选的视觉网格叠加层，用于玩家辅助。

### 主要功能

- `updateMap()` - 读取 `SPDSettings.visualGrid()` 设置
- `getTileVisual` - 在以下瓦片上显示网格：
  - 地板瓦片、草地瓦片（棋盘图案）
  - 门瓦片（带有墙壁邻接的特殊处理）

### 用户设置

- 网格显示可通过游戏设置启用/禁用
- 主要用于玩家导航辅助

---

## CustomTilemap.java

### 类描述
`CustomTilemap` 是自定义关卡装饰的抽象基类，支持保存/恢复功能。

### 关键字段

- `tileX`, `tileY` - 瓦片中的位置
- `tileW`, `tileH` - 瓦片中的尺寸  
- `vis` - 内部 Tilemap 视觉

### 主要方法

| 方法 | 描述 |
|------|------|
| `pos(int x, int y)` / `setRect(...)` | 定位辅助方法 |
| `mapSimpleImage(...)` | 生成纹理坐标数据的工具 |
| `create()` | 创建带有光照支持的 Tilemap 视觉 |
| `name()` / `desc()` | 可重写的描述方法 |
| `restoreFromBundle()` / `storeInBundle()` | 游戏存档持久化支持 |

### 使用场景

- 关卡设计师创建的自定义装饰
- 特殊事件的视觉效果
- 动态生成的环境元素

---

## 性能优化策略

### 内存效率

- **延迟加载**: 瓦片图仅在需要时创建
- **共享纹理**: 多个瓦片图层共享相同的纹理图集
- **稀疏存储**: 使用稀疏数组存储动态元素

### 渲染优化

- **增量更新**: 仅更新发生变化的区域
- **批处理**: 合并多个小更新为单次大更新
- **视锥剔除**: 仅渲染可见区域

### 缓存机制

- **瓦片缓存**: 缓存常用瓦片组合
- **纹理缓存**: 复用纹理对象
- **计算缓存**: 缓存昂贵的计算结果（如节日检测）

## 扩展性设计

### 添加新瓦片类型

1. 在 `DungeonTileSheet` 中添加新的常量
2. 在相应的瓦片图类中添加渲染逻辑
3. 更新衔接和变体逻辑

### 添加新图层

1. 继承 `DungeonTilemap` 或 `CustomTilemap`
2. 实现 `getTileVisual` 方法
3. 在渲染顺序中插入适当位置

### 自定义装饰

- 使用 `CustomTilemap` 创建关卡特定的装饰
- 支持动态生成和持久化
- 可与其他系统集成（如事件触发）

这个瓦片系统为 Shattered Pixel Dungeon 提供了丰富而高效的视觉呈现能力，同时保持了良好的扩展性和性能。