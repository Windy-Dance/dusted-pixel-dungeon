# FogOfWar.java - 战争迷雾系统

## 概述
`FogOfWar` 实现了游戏的战争迷雾（Fog of War）渲染系统，提供4种可见性状态和动态亮度控制。该系统创建沉浸式探索体验，隐藏未探索区域并逐步揭示已访问的区域。

## 可见性状态体系

### 四级可见性模型
1. **VISIBLE (可见)** - 当前角色视野内的区域
2. **VISITED (已访问)** - 曾经访问过但当前不在视野内的区域  
3. **MAPPED (已映射)** - 通过地图卷轴或其他方式揭示但未实际访问的区域
4. **INVISIBLE (不可见)** - 完全隐藏的未探索区域

### 亮度级别控制
- 支持动态亮度调整（0-6级）
- 不同可见性状态有不同的基础亮度
- 特殊效果可临时调整亮度（如火把、魔法效果）

## 核心数据结构

### 颜色矩阵
```java
private static final int[][] FOG_COLORS = {
    // VISIBLE, VISITED, MAPPED, INVISIBLE
    {0xCCFFFFFF, 0xCCB2A596, 0xCC8C7C6D, 0x00000000}, // Level 0 (darkest)
    {0xCCFFFFFF, 0xCCB2A596, 0xCC8C7C6D, 0xCC332820}, // Level 1
    // ... 共7个亮度级别
};
```

### 状态数组
- `int[] fogArray` - 存储每个单元格的可见性状态
- `Rect queuedUpdate` - 待处理的更新区域队列
- `boolean needsUpdate` - 更新标志

## 渲染机制

### 增量更新系统
```java
// 队列更新区域
public void updateFog()
public void updateFog(Rect rect) 
public void updateFog(int cell, int radius)
public void updateFogArea()

// 执行实际渲染
private void updateTexture()
```

### 半单元格填充算法
为创建平滑的迷雾边缘，使用半单元格精度填充：
- 检测墙壁瓦片的悬垂效果
- 在墙壁边缘创建柔化过渡
- 处理特殊地形（如门、雕像）的遮挡

### 底层填充操作
```java
private void fillCell(Pixmap pix, int x, int y, int color)
private void fillLeft(Pixmap pix, int x, int y, int color)  
private void fillRight(Pixmap pix, int x, int y, int color)
```

## 与其他系统的集成

### 视野系统
- 接收角色视野更新事件
- 动态调整 VISIBLE 区域
- 处理永久光源（如火炬）的影响

### 地图系统  
- 与 DungeonTerrainTilemap 协同工作
- 考虑墙壁悬垂的遮挡效果
- 支持特殊关卡的迷雾规则

### 物品系统
- 地图卷轴设置 MAPPED 状态
- 特殊物品可临时扩展视野
- 永久性视野增强物品的支持

## 性能优化

### 延迟更新
- 批量处理多个小更新
- 避免每帧重新渲染整个迷雾
- 使用脏矩形技术只更新变更区域

### 内存效率
- 复用 Pixmap 对象
- 避免不必要的颜色计算
- 缓存常用的颜色值

### 渲染优化
- 与底层 Tilemap 渲染器集成
- 利用 GPU 纹理缓存
- 最小化 CPU-GPU 数据传输

## 特殊处理逻辑

### 墙壁遮挡处理
- 检测 WallBlockingTilemap 的遮挡信息
- 在墙壁后方创建额外的隐藏区域
- 处理复杂地形的视线阻挡

### 关卡特定规则
- 某些 Boss 关卡有特殊的迷雾行为
- 矿洞关卡的特殊处理逻辑
- 节日模式下的视觉调整

### 边界处理
- 地图边缘的特殊迷雾效果
- 无缝关卡过渡的支持
- 多层地牢的迷雾状态保持

## API 使用

### 基本更新
```java
// 更新整个可见区域
fogOfWar.updateFog();

// 更新特定区域
fogOfWar.updateFog(new Rect(10, 10, 20, 20));

// 以某点为中心的圆形更新
fogOfWar.updateFog(hero.pos, hero.viewDistance);
```

### 状态查询
虽然 FogOfWar 主要负责渲染，但状态信息存储在其他系统中：
- 可见性状态由 `Dungeon.level.visited` 和 `Dungeon.level.mapped` 数组维护
- FogOfWar 读取这些状态进行渲染

### 亮度控制
```java
// 设置全局亮度级别（通常由游戏系统控制）
DungeonTilemap.lightingMode = brightnessLevel;
```

## 设计特点

### 分离关注点
- 渲染逻辑与状态管理分离
- FogOfWar 只负责视觉呈现
- 状态变更由其他系统触发

### 可配置性
- 颜色方案可轻松修改
- 亮度级别数量可调整  
- 迷雾强度可通过颜色透明度控制

### 扩展性
- 支持自定义迷雾效果
- 可添加新的可见性状态
- 易于集成新的光源类型

## 调试支持

### 开发者选项
- 可禁用迷雾以查看完整地图
- 可视化不同可见性状态的边界
- 实时调整亮度级别进行测试

### 性能监控
- 更新频率统计
- 渲染时间测量
- 内存使用情况跟踪

## 常见问题和解决方案

### 性能问题
- **问题**：大型更新区域导致帧率下降
- **解决方案**：使用增量更新和区域限制

### 视觉瑕疵
- **问题**：迷雾边缘出现锯齿或缝隙  
- **解决方案**：半单元格填充和墙壁遮挡检测

### 状态不一致
- **问题**：可见性状态与实际渲染不符
- **解决方案**：确保状态更新后调用相应的 FogOfWar 更新方法