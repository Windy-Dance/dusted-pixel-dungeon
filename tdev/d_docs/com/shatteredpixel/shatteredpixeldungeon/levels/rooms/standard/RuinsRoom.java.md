# RuinsRoom.java 文档

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
RuinsRoom 类负责生成废墟主题的标准房间布局，使用补丁系统创建不规则的墙壁和碎石（REGION_DECO）地形，模拟废墟场景。

### 系统定位
该类属于关卡生成系统中的标准房间类型之一，继承自抽象类 PatchRoom，专门用于创建具有废墟效果的房间变体。

### 不负责什么
- 不负责补丁生成的具体算法（由父类 PatchRoom 和 Patch 类处理）
- 不负责废墟地形的具体视觉表现（由渲染系统处理）
- 不负责怪物或物品在废墟中的放置策略（由上层逻辑处理）

## 3. 结构总览

### 主要成员概览
- 继承自 PatchRoom 的 patch 字段：存储生成的补丁数据
- 无额外实例字段或静态常量

### 主要逻辑块概览
- sizeCatProbs() 方法：定义房间尺寸类别的概率分布 [4, 2, 1]
- canMerge() 方法：允许与其他房间合并
- fill() 方法：定义补丁填充率（30%-60%）
- clustering() 方法：设置聚类程度为0（完全分散）
- ensurePath() 方法：确保门连接时路径连通
- cleanEdges() 方法：启用边缘清理
- paint() 方法：绘制废墟房间布局，将补丁转换为墙壁或碎石地形

### 生命周期/调用时机
该类实例在关卡生成过程中被 RoomFactory 创建，paint() 方法在关卡绘制阶段被调用。

## 4. 继承与协作关系

### 父类提供的能力
- patch 字段：存储补丁数据的布尔数组
- setupPatch()：设置补丁数据，处理路径连通性
- cleanDiagonalEdges()：清理对角线边缘使补丁更美观
- xyToPatchCoords()：坐标转换工具方法

### 覆写的方法
- sizeCatProbs()：覆写自 Room
- canMerge()：覆写自 Room
- fill()：实现父类 abstract method
- clustering()：实现父类 abstract method  
- ensurePath()：实现父类 abstract method
- cleanEdges()：实现父类 abstract method
- paint()：覆写自 Room

### 实现的接口契约
通过继承 PatchRoom 实现了补丁房间的所有抽象契约。

### 依赖的关键类
- Level：关卡数据结构
- Terrain：地形类型定义（WALL, EMPTY, REGION_DECO）
- Painter：关卡绘制工具
- Patch：补丁生成算法
- PathFinder：路径查找工具
- Point：几何工具类
- Random：随机数生成器
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

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：定义不同尺寸类别房间的生成概率

**参数**：无

**返回值**：float[]，包含三个概率值 [4, 2, 1]

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定数组 new float[]{4, 2, 1}，表示小、中、大三种尺寸类别的相对概率。

**边界情况**：无

### canMerge()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：确定是否允许与其他房间合并

**参数**：
- l (Level)：当前关卡对象
- other (Room)：要合并的另一个房间
- p (Point)：合并点
- mergeTerrain (int)：合并地形类型

**返回值**：boolean，固定返回true

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return true;
```
始终允许与其他房间合并，这使得废墟房间可以更灵活地集成到关卡布局中。

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
设置聚类程度为0，这会使补丁完全分散，形成不规则的点状分布而非紧凑的块状，适合废墟效果。

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
启用边缘清理功能，移除仅通过对角线连接的补丁单元，使废墟外观更自然。

**边界情况**：边缘清理会略微降低实际填充率。

### paint()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制废墟房间的实际布局，将补丁转换为墙壁或碎石地形

**参数**：
- level (Level)：当前关卡对象

**返回值**：void

**前置条件**：房间边界已正确设置，connected 字典包含连接的门

**副作用**：修改 level 对象的地形数据

**核心实现逻辑**：
1. 首先填充整个房间区域为墙壁地形 (Terrain.WALL)
2. 在距离边界1格的内部区域填充为空地地形 (Terrain.EMPTY)
3. 将所有连接的门设置为常规门类型 (Door.Type.REGULAR)
4. 调用 setupPatch() 方法生成补丁数据
5. 遍历房间内部每个位置：
   - 如果位置在补丁中（patch[xyToPatchCoords(j, i)] 为 true）：
     - 对于内部位置（非边缘）：
       - 计算相邻的补丁单元数量（上下左右）
       - 使用随机判定：Random.Int(2) < adjacent
         - 如果相邻数量≥1，50%概率为墙；≥2，100%概率为墙
         - 孤立的补丁单元（相邻为0）总是转换为碎石 (REGION_DECO)
     - 对于边缘位置：总是保持为墙壁 (Terrain.WALL)
     - 根据判定结果设置地形：wall ? Terrain.WALL : Terrain.REGION_DECO

**边界情况**：
- 边缘的补丁单元总是保持为墙壁，确保房间边界完整性
- 内部孤立的补丁单元总是转换为碎石，形成废墟效果
- 路径连通性确保玩家可以到达所有可通行区域

## 8. 对外暴露能力

### 显式 API
- sizeCatProbs()
- canMerge()
- paint()

### 内部辅助方法
- fill()
- clustering()
- ensurePath()
- cleanEdges()

### 扩展入口
该类没有提供额外的扩展点，但通过实现 PatchRoom 的抽象方法提供了完整的配置能力。如需自定义废墟房间，建议直接继承 PatchRoom 并实现自己的补丁转换逻辑。

## 9. 运行机制与调用链

### 创建时机
在关卡生成过程中，当RoomFactory需要创建废墟房间类型时实例化。

### 调用者
- RoomFactory.createRoom()：创建房间实例
- LevelGenerator.paint()：调用 paint() 方法绘制房间

### 被调用者
- Painter.fill()：填充基础地形
- setupPatch()：生成补丁数据
- Patch.generate()：核心补丁生成算法
- PathFinder.buildDistanceMap()：路径验证（当有门连接时）
- Random.Int()：随机判定墙壁/碎石转换
- xyToPatchCoords()：坐标转换

### 系统流程位置
位于关卡生成流程的房间绘制阶段，在房间布局确定但具体地形未填充时执行。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| (无官方翻译) | 废墟房间 | 房间类型的中文名称（项目内未找到官方对应译名） |

### 依赖的资源
- Terrain.WALL：墙壁地形
- Terrain.REGION_DECO：碎石/废墟装饰地形
- Terrain.EMPTY：空地地形

### 中文翻译来源
在 levels_zh.properties 文件中未找到 RuinsRoom 的官方翻译，因此使用描述性翻译"废墟房间"。

## 11. 使用示例

### 基本用法
```java
// 在关卡生成器中创建废墟房间
RuinsRoom ruinsRoom = new RuinsRoom();
ruinsRoom.set(left, top, right, bottom); // 设置房间边界
ruinsRoom.paint(level); // 绘制房间到关卡
```

### 扩展示例
由于该类主要是配置性的，如需不同的废墟效果，可以创建类似的子类：

```java
public class DenseRuinsRoom extends PatchRoom {
    @Override
    protected float fill() {
        return 0.7f; // 更高的填充率
    }
    
    @Override
    protected int clustering() {
        return 1; // 适度聚类
    }
    
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);
        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
        }
        setupPatch(level);
        // 自定义补丁转换逻辑
        for (int i = top + 1; i < bottom; i++) {
            for (int j = left + 1; j < right; j++) {
                if (patch[xyToPatchCoords(j, i)]) {
                    int cell = i * level.width() + j;
                    // 总是使用水地形作为废墟
                    level.map[cell] = Terrain.WATER;
                }
            }
        }
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖房间的边界坐标已正确设置
- 依赖 connected 字典包含有效的门连接信息
- patch 字段在 setupPatch() 执行后才被初始化

### 生命周期耦合
- 必须在房间边界设置完成后调用 paint() 方法
- paint() 方法应在关卡地形数组初始化后调用
- 访问 patch 字段应在 setupPatch() 方法执行后进行

### 常见陷阱
- 修改 fill() 返回值时需注意不要过高（>60%）以免影响游戏性
- clustering() 值为0是有意为之，确保废墟效果的分散性
- ensurePath() 逻辑不应随意修改，否则可能导致不可达区域
- 边缘总是保持为墙壁的逻辑不应移除，否则会破坏房间边界

## 13. 修改建议与扩展点

### 适合扩展的位置
- 如需不同的填充率曲线，可修改 fill() 方法的计算逻辑
- 如需调整墙壁/碎石转换的概率，可修改 paint() 方法中的随机判定逻辑
- 可考虑添加多层废墟支持，使用不同地形类型表示不同程度的废墟

### 不建议修改的位置
- 聚类程度为0的设计不应改变，这是废墟效果的核心特征
- 路径连通性逻辑不应移除，否则会破坏关卡可玩性
- 边缘墙壁保持逻辑不应移除，否则会导致房间边界不完整

### 重构建议
可以考虑将墙壁/碎石转换逻辑提取为独立的 protected 方法，便于子类自定义。但当前结构已经足够清晰，无需紧急重构。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（未找到官方翻译，使用描述性名称）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点