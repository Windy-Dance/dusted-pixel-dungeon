# RitualRoom.java 文档

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
RitualRoom 类负责生成具有仪式主题的标准房间布局，房间包含围绕中心祭坛的装饰性补丁、雕像排列、余烬区域和奖品物品。

### 系统定位
该类属于关卡生成系统中的标准房间类型之一，继承自抽象类 PatchRoom，专门用于创建具有仪式场景的房间变体。

### 不负责什么
- 不负责奖品物品的具体选择逻辑（由 Generator 和 level.findPrizeItem() 处理）
- 不负责补丁生成的具体算法（由父类 PatchRoom 和 Patch 类处理）
- 不负责仪式场景的战斗或交互逻辑（由上层系统处理）

## 3. 结构总览

### 主要成员概览
- 继承自 PatchRoom 的 patch 字段：存储生成的补丁数据
- 无额外实例字段或静态常量
- protected placeloot() 方法：在中心位置放置奖品物品

### 主要逻辑块概览
- minWidth()/minHeight() 方法：确保房间最小尺寸为9格
- sizeCatProbs() 方法：定义房间尺寸类别的概率分布 [6, 3, 1]
- fill() 方法：定义补丁填充率（30%-60%）
- clustering() 方法：设置聚类程度为0（完全分散）
- ensurePath() 方法：确保门连接时路径连通
- cleanEdges() 方法：启用边缘清理
- paint() 方法：绘制仪式房间布局，包括补丁、雕像、余烬和祭坛
- placeloot() 方法：在祭坛上放置奖品物品

### 生命周期/调用时机
该类实例在关卡生成过程中被 RoomFactory 创建，paint() 方法在关卡绘制阶段被调用。

## 4. 继承与协作关系

### 父类提供的能力
- patch 字段：存储补丁数据的布尔数组
- setupPatch()：设置补丁数据，处理路径连通性
- fillPatch()：将补丁数据应用到关卡地形
- cleanDiagonalEdges()：清理对角线边缘使补丁更美观
- xyToPatchCoords()：坐标转换工具方法

### 覆写的方法
- minWidth()：覆写自 Room
- minHeight()：覆写自 Room
- sizeCatProbs()：覆写自 Room
- fill()：实现父类抽象 method
- clustering()：实现父类 abstract method  
- ensurePath()：实现父类 abstract method
- cleanEdges()：实现父类 abstract method
- paint()：覆写自 Room

### 实现的接口契约
通过继承 PatchRoom 实现了补丁房间的所有抽象契约。

### 依赖的关键类
- Level：关卡数据结构
- Terrain：地形类型定义（WALL, EMPTY, REGION_DECO, STATUE, EMBERS, PEDESTAL）
- Painter：关卡绘制工具
- Patch：补丁生成算法
- PathFinder：路径查找工具
- Point：几何工具类
- Random：随机数生成器
- Generator：物品生成器
- Item：物品基类
- Door：门对象

### 使用者
- RoomFactory：创建房间实例
- LevelGenerator：在关卡生成过程中使用

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无（继承 patch 字段但不直接声明）

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器。

### 初始化块
无

### 初始化注意事项
该类完全依赖父类 PatchRoom 的初始化机制，patch 字段在 setupPatch() 方法中被初始化。

## 7. 方法详解

### minWidth()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：返回房间的最小宽度要求

**参数**：无

**返回值**：int，最小宽度值（至少为9）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(super.minWidth(), 9);
```
确保房间宽度至少为9格，以容纳仪式布局的基本结构（特别是中央7x7区域）。

**边界情况**：当父类返回值小于9时，返回9；否则返回父类值。

### minHeight()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：返回房间的最小高度要求

**参数**：无

**返回值**：int，最小高度值（至少为9）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(super.minHeight(), 9);
```
确保房间高度至少为9格，以容纳仪式布局的基本结构。

**边界情况**：当父类返回值小于9时，返回9；否则返回父类值。

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：定义不同尺寸类别房间的生成概率

**参数**：无

**返回值**：float[]，包含三个概率值 [6, 3, 1]

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定数组 new float[]{6, 3, 1}，表示小、中、大三种尺寸类别的相对概率。

**边界情况**：无

### fill()

**可见性**：protected

**是否覆写**：是，实现父类 abstract method

**方法职责**：返回补丁的填充率（占房间内部面积的比例）

**参数**：无

**返回值**：float，填充率（30%-60%）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
int scale = Math.min(width()*height(), 18*18);
return 0.30f + scale/1024f;
```
- 对于4x4房间：填充率约为30%
- 对于18x18房间：填充率约为60%
- 更大的房间保持60%的上限
- 注释特别说明：由于中央7x7区域会被覆盖，实际整体填充率要低得多

**边界情况**：房间尺寸越大，填充率越高，但有上限防止过度填充。

### clustering()

**可见性**：protected

**是否覆写**：是，实现父类 abstract method

**方法职责**：返回补丁的聚类程度

**参数**：无

**返回值**：int，聚类值（固定为0）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return 0;
```
设置聚类程度为0，这会使补丁完全分散，形成不规则的点状分布而非紧凑的块状。

**边界情况**：无

### ensurePath()

**可见性**：protected

**是否覆写**：是，实现父类 abstract method

**方法职责**：确定是否需要确保路径连通性

**参数**：无

**返回值**：boolean，当有门连接时返回true

**前置条件**：connected 字典已正确设置

**副作用**：无

**核心实现逻辑**：
```java
return connected.size() > 0;
```
只有当房间有门连接时才需要确保路径连通性，避免生成无法到达的区域。

**边界情况**：无门连接的孤立房间不需要路径验证。

### cleanEdges()

**可见性**：protected

**是否覆写**：是，实现父类 abstract method

**方法职责**：确定是否清理补丁的对角线边缘

**参数**：无

**返回值**：boolean，固定返回true

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return true;
```
启用边缘清理功能，移除仅通过对角线连接的补丁单元，使补丁外观更整洁。

**边界情况**：边缘清理会略微降低实际填充率。

### paint()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制仪式房间的实际布局，包括补丁、雕像、余烬和祭坛

**参数**：
- level (Level)：当前关卡对象

**返回值**：void

**前置条件**：房间边界已正确设置，connected 字典包含连接的门

**副作用**：修改 level 对象的地形数据和物品数据

**核心实现逻辑**：
1. 首先填充整个房间区域为墙壁地形 (Terrain.WALL)
2. 在距离边界1格的内部区域填充为空地地形 (Terrain.EMPTY)
3. 获取房间中心点
4. 调用 setupPatch() 方法生成补丁数据
5. 调用 fillPatch() 方法将补丁应用为 REGION_DECO 地形
6. 在中心7x7区域重新填充为空地 (Terrain.EMPTY)，覆盖部分补丁
7. 在特定位置放置8个雕像 (Terrain.STATUE)，形成仪式图案：
   - 四个角落位置：(cx±2, cy±1) 和 (cx±1, cy±2)
8. 在中心3x3区域填充余烬 (Terrain.EMBERS)
9. 在精确中心位置放置祭坛 (Terrain.PEDESTAL)
10. 调用 placeloot() 在祭坛上放置奖品物品
11. 将所有连接的门设置为常规门类型 (Door.Type.REGULAR)

**边界情况**：
- 最小房间尺寸为9x9，确保中央7x7区域有足够的空间
- 路径连通性确保玩家可以到达仪式区域
- 补丁生成考虑了后续的中央覆盖，实际视觉效果协调

### placeloot()

**可见性**：protected

**是否覆写**：否

**方法职责**：在指定位置放置仪式奖品物品

**参数**：
- level (Level)：当前关卡对象
- p (Point)：地图位置点

**返回值**：void

**前置条件**：p 是有效的地图位置

**副作用**：在指定位置添加物品到关卡

**核心实现逻辑**：
```java
Item prize = Random.Int(2) == 0 ? level.findPrizeItem() : null;
if (prize == null){
    prize = Generator.random(Random.oneOf(Generator.Category.POTION, Generator.Category.SCROLL));
}
level.drop(prize, level.pointToCell(p));
```
- 50%概率尝试获取关卡奖品物品
- 如果没有奖品物品，则随机生成药剂或卷轴
- 将物品放置在指定位置

**边界情况**：findPrizeItem() 可能返回null，此时会生成随机消耗品。

## 8. 对外暴露能力

### 显式 API
- minWidth()
- minHeight()
- sizeCatProbs()
- paint()

### 内部辅助方法
- fill()
- clustering()
- ensurePath()
- cleanEdges()
- placeloot()：可被子类重写以改变奖品放置逻辑

### 扩展入口
该类提供了一个 protected 方法作为扩展点：
- placeloot()：允许子类自定义奖品物品放置逻辑

## 9. 运行机制与调用链

### 创建时机
在关卡生成过程中，当RoomFactory需要创建仪式房间类型时实例化。

### 调用者
- RoomFactory.createRoom()：创建房间实例
- LevelGenerator.paint()：调用 paint() 方法绘制房间

### 被调用者
- Painter.fill()/Painter.set()：填充和设置地形
- setupPatch()/fillPatch()：补丁生成和应用
- level.findPrizeItem()：查找合适的奖品物品
- Generator.random()：生成随机物品
- level.drop()：在指定位置放置物品
- Random.Int()/Random.oneOf()：生成随机数

### 系统流程位置
位于关卡生成流程的房间绘制阶段，在房间布局确定但具体地形未填充时执行。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| levels.rooms.quest.ritualsiteroom$ritualmarker.name | 仪式标记 | 仪式相关文案（任务房间） |

### 依赖的资源
- Terrain.REGION_DECO：区域装饰地形
- Terrain.STATUE：雕像地形
- Terrain.EMBERS：余烬地形  
- Terrain.PEDESTAL：祭坛地形
- 各种消耗品物品（药剂、卷轴）

### 中文翻译来源
core/src/main/assets/messages/levels/levels_zh.properties，第30行。虽然这是针对任务房间的翻译，但"仪式"一词适用于描述此标准房间的主题，因此使用"仪式房间"作为房间类型的中文名称。

## 11. 使用示例

### 基本用法
```java
// 在关卡生成器中创建仪式房间
RitualRoom ritualRoom = new RitualRoom();
ritualRoom.set(left, top, right, bottom); // 设置房间边界
ritualRoom.paint(level); // 绘制房间到关卡
```

### 扩展示例
可以通过重写 placeloot() 方法来自定义奖品放置逻辑：

```java
public class CustomRitualRoom extends RitualRoom {
    @Override
    protected void placeloot(Level level, Point p) {
        // 总是放置特定的稀有物品
        Item specialItem = new WandOfFireblast();
        level.drop(specialItem, level.pointToCell(p));
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖房间的边界坐标已正确设置
- 依赖 connected 字典包含有效的门连接信息
- level.findPrizeItem() 依赖关卡的奖品物品池已初始化
- Generator.random() 依赖物品生成系统已配置

### 生命周期耦合
- 必须在房间边界设置完成后调用 paint() 方法
- paint() 方法应在关卡地形数组和物品系统初始化后调用

### 常见陷阱
- 修改 minWidth()/minHeight() 返回值时需确保仍能满足中央7x7区域的最小空间需求
- 直接修改 fill() 返回值可能影响补丁的视觉效果和游戏平衡
- 中央7x7区域的覆盖逻辑不应随意修改，否则会破坏仪式布局的完整性
- 聚类程度为0的设计是有意为之，确保补丁分散以突出中央仪式区域

## 13. 修改建议与扩展点

### 适合扩展的位置
- 如需不同的奖品放置逻辑，可重写 placeloot() 方法
- 如需调整补丁参数，可修改 fill() 或 clustering() 方法
- 可考虑添加不同的雕像排列模式或祭坛装饰

### 不建议修改的位置
- 最小尺寸限制（9格）不应降低，否则无法容纳完整的仪式布局
- 中央7x7区域的覆盖逻辑不应移除，这是仪式房间的核心特征
- 雕像的8个特定位置不应随意更改，这形成了标准的仪式图案

### 重构建议
可以考虑将雕像位置计算提取为独立的方法，便于维护和扩展。但当前硬编码的位置已经形成了稳定的视觉模式，无需紧急重构。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（使用 levels_zh.properties 中的"仪式"翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点