# DungeonTilemap.java - 瓦片图抽象基类

## 概述
`DungeonTilemap` 是所有地牢瓦片图的抽象基类，提供坐标转换工具、通用功能和渲染框架。所有具体的瓦片图层（如地形、墙壁、植物等）都继承此类。

## 核心功能

### 坐标转换系统
提供完整的坐标系转换支持：

```java
// 屏幕坐标 ↔ 瓦片坐标
public int screenToTile(int x, int y, boolean wallAssist)

// 瓦片坐标 ↔ 世界坐标  
public Point tileToWorld(int pos)
public Point tileCenterToWorld(int pos) 
public Point raisedTileCenterToWorld(int pos)
```

**Wall Assist 模式**：在点击检测时，考虑墙壁悬垂效果的特殊处理。

### 数据映射
- **地图绑定**：`map(int[] data, int cols)` 方法绑定原始地图数据
- **引用保持**：保留对原始地图数组的直接引用，避免数据复制
- **列数存储**：缓存地图宽度用于坐标计算

### 渲染框架
- **抽象方法**：`getTileVisual(int pos, int tile, boolean flat)` 由子类实现
- **更新机制**：`updateMap()` 和 `updateMapCell(int cell)` 触发视觉更新
- **增量更新**：支持单单元格或全图更新

### 动画支持
- **发现动画**：`discover(int pos, int oldValue)` 创建淡出效果
- **补间动画**：集成游戏的补间动画系统
- **视觉反馈**：为玩家操作提供即时视觉响应

## 技术实现

### 坐标计算优化
```java
// 高效的坐标转换
public int tile(int x, int y) {
    return x + y * mapWidth;
}

public int tileX(int pos) {
    return pos % mapWidth;
}

public int tileY(int pos) {
    return pos / mapWidth;
}
```

### 内存管理
- **零分配设计**：坐标转换方法避免创建临时对象
- **缓存友好**：频繁访问的字段声明为 final
- **引用透明**：直接使用原始地图数据，无额外内存开销

### 渲染管线集成
- **父类继承**：继承自 `Tilemap` 类，集成底层渲染引擎
- **纹理管理**：自动处理纹理坐标和UV映射
- **批处理支持**：与游戏的批处理渲染器兼容

## 子类扩展接口

### 必须实现的方法
```java
protected abstract int getTileVisual(int pos, int tile, boolean flat);
```
- **pos**：单元格位置索引
- **tile**：地形类型值  
- **flat**：是否为平面渲染模式（影响某些瓦片的选择）

### 可选重写的方法
- **点击处理**：`overlapsPoint()` 系列方法
- **描述信息**：`name()` 和 `desc()` 方法（用于调试）
- **特殊更新**：重写 `updateMapCell()` 处理特定逻辑

## 使用示例

### 创建自定义瓦片图
```java
public class MyCustomTilemap extends DungeonTilemap {
    @Override
    protected int getTileVisual(int pos, int tile, boolean flat) {
        if (tile == Terrain.MY_SPECIAL_TERRAIN) {
            return DungeonTileSheet.MY_SPECIAL_VISUAL;
        }
        return -1; // 返回 -1 表示无瓦片
    }
}
```

### 坐标转换使用
```java
// 将鼠标点击转换为地图位置
int clickedPos = tilemap.screenToTile(mouseX, mouseY, true);

// 获取世界坐标用于粒子效果
Point worldPos = tilemap.tileCenterToWorld(clickedPos);
```

## 性能特性

### 高效渲染
- **视锥剔除**：自动跳过屏幕外的瓦片
- **脏矩形更新**：只重绘变更的区域  
- **纹理批处理**：合并相似的绘制调用

### 内存效率
- **共享数据**：多个瓦片图层共享同一地图数据
- **延迟初始化**：按需创建渲染资源
- **对象池**：重用临时计算对象

## 设计模式

### 模板方法模式
- 父类定义算法骨架
- 子类实现具体步骤（`getTileVisual`）

### 工具类模式  
- 提供完整的坐标工具集
- 独立于具体渲染逻辑

### 观察者模式
- 地图变更通知机制
- 支持多图层同步更新

## 调试和开发支持

### 调试信息
- 自动包含位置和地形信息
- 支持开发者控制台查询

### 可视化辅助
- 发现动画帮助玩家理解游戏状态
- 坐标转换辅助关卡编辑和测试

## 与其他系统的集成

### 输入系统
- 点击检测与交互系统集成
- 支持复杂的选择逻辑

### 特效系统  
- 世界坐标用于粒子和特效定位
- 与动画系统协同工作

### 保存系统
- 虽然不直接处理保存，但为持久化装饰提供基础
- 与 `CustomTilemap` 的保存功能互补