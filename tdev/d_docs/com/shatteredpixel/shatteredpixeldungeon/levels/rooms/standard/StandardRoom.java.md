# StandardRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/rooms/standard/StandardRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | abstract class |
| **继承关系** | extends com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room |
| **代码行数** | 194 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
StandardRoom 是所有标准房间类型的抽象基类，为关卡生成系统中的标准房间提供统一的尺寸分类、大小计算和权重管理功能。它定义了房间尺寸的三个类别（NORMAL、LARGE、GIANT），并提供了相应的概率配置和大小设置机制。

### 系统定位
在 Shattered Pixel Dungeon 的关卡生成架构中，StandardRoom 位于房间类型层次结构的中间层。它继承自基础的 Room 类，为所有具体的标准化房间实现提供共享的功能，如尺寸管理、连接权重计算和怪物生成权重等。

### 不负责什么
- 不直接参与关卡的实际绘制（paint 方法由具体子类实现）
- 不处理具体的房间布局逻辑（由各个具体的房间子类实现）
- 不管理房间之间的连接拓扑（由 Room 基类和关卡生成器处理）

## 3. 结构总览

### 主要成员概览
- `SizeCategory` 枚举：定义房间尺寸类别（NORMAL、LARGE、GIANT）
- `sizeCat` 字段：当前房间实例的尺寸类别
- `rooms` 静态列表：所有可创建的标准房间类型列表
- `chances` 静态数组：不同地牢深度下各房间类型的生成概率配置

### 主要逻辑块概览
- 尺寸类别管理逻辑（setSizeCat 相关方法）
- 尺寸限制和计算逻辑（minWidth/maxWidth/minHeight/maxHeight）
- 权重计算逻辑（sizeFactor、mobSpawnWeight、connectionWeight）
- 房间创建工厂方法（createRoom）

### 生命周期/调用时机
- 实例化时自动调用 setSizeCat() 设置尺寸类别
- 关卡生成过程中调用 paint() 方法绘制房间（由子类实现）
- 关卡生成器调用 createRoom() 静态方法创建随机标准房间

## 4. 继承与协作关系

### 父类提供的能力
从 Room 基类继承的能力包括：
- 空间几何操作（Rect 功能：width、height、center、random 等）
- 连接管理（neigbours、connected、connect、addNeigbour 等）
- 绘制占位方法（paint 抽象方法）
- 放置点过滤（canPlaceWater、canPlaceGrass、canPlaceTrap、canPlaceItem、canPlaceCharacter）
- 图论接口实现（Graph.Node）

### 覆写的方法
- `minWidth()`：返回 sizeCat.minDim
- `maxWidth()`：返回 sizeCat.maxDim  
- `minHeight()`：返回 sizeCat.minDim
- `maxHeight()`：返回 sizeCat.maxDim
- `canMerge()`：检查合并点是否为非固体地形

### 实现的接口契约
- 继承 Room 类实现的 Bundlable 接口（用于存档）
- 继承 Room 类实现的 Graph.Node 接口（用于路径查找）

### 依赖的关键类
- `com.shatteredpixel.shatteredpixeldungeon.Dungeon`：获取当前地牢深度
- `com.shatteredpixel.shatteredpixeldungeon.levels.Level`：关卡数据结构
- `com.shatteredpixel.shatteredpixeldungeon.levels.Terrain`：地形常量和标志
- `com.watabou.utils.Random`：随机数生成
- `com.watabou.utils.Reflection`：反射创建房间实例

### 使用者
- 关卡生成器（Level Generator）：调用 createRoom() 创建房间
- 所有具体的 StandardRoom 子类：继承其功能
- 房间连接算法：使用其权重和尺寸信息

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无 | 无 | 无 | 无 |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| sizeCat | SizeCategory | 通过初始化块设置 | 当前房间的尺寸类别，实例化时自动设置 |

### 静态字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| rooms | ArrayList<Class<?extends StandardRoom>> | 包含40个房间类 | 所有可创建的标准房间类型列表，按地牢区域分组 |
| chances | float[][] | 27个深度的概率数组 | 不同地牢深度下各房间类型的生成概率配置 |

## 6. 构造与初始化机制

### 构造器
StandardRoom 使用默认构造器，继承自 Room 类的无参构造器。

### 初始化块
```java
{ setSizeCat(); }
```
实例化时立即调用 setSizeCat() 方法设置尺寸类别。

### 初始化注意事项
- 子类如果需要自定义尺寸类别概率，必须覆写 sizeCatProbs() 方法
- 尺寸类别设置失败时（概率全为0），sizeCat 可能保持 null 状态
- 初始化顺序：父类构造器 → 初始化块 → 子类构造器

## 7. 方法详解

### sizeCatProbs()
**可见性**：public

**是否覆写**：否

**方法职责**：返回当前房间类型在不同尺寸类别下的生成概率数组

**参数**：无

**返回值**：float[]，长度为3的数组，分别对应 NORMAL、LARGE、GIANT 的概率

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
//always normal by default
return new float[]{1, 0, 0};
```
默认只生成 NORMAL 尺寸的房间。

**边界情况**：子类可以覆写此方法返回不同的概率分布

### setSizeCat() (无参版本)
**可见性**：public

**是否覆写**：否

**方法职责**：根据 sizeCatProbs() 设置房间尺寸类别，允许所有尺寸类别

**参数**：无

**返回值**：boolean，设置成功返回 true，失败返回 false

**前置条件**：sizeCatProbs() 返回的数组长度必须等于 SizeCategory.values().length

**副作用**：设置实例的 sizeCat 字段

**核心实现逻辑**：
调用 setSizeCat(0, SizeCategory.values().length-1) 允许所有尺寸类别

**边界情况**：如果所有概率都为0，返回 false 且 sizeCat 不变

### setSizeCat(int maxRoomValue)
**可见性**：public

**是否覆写**：否

**方法职责**：根据最大房间值限制设置尺寸类别

**参数**：
- `maxRoomValue` (int)：最大允许的房间值（SizeCategory.ordinal + 1）

**返回值**：boolean，设置成功返回 true，失败返回 false

**前置条件**：同无参版本

**副作用**：设置实例的 sizeCat 字段

**核心实现逻辑**：
调用 setSizeCat(0, maxRoomValue-1) 限制最大尺寸类别

**边界情况**：maxRoomValue <= 0 时可能返回 false

### setSizeCat(int minOrdinal, int maxOrdinal)
**可见性**：public

**是否覆写**：否

**方法职责**：根据指定的尺寸类别范围设置房间尺寸

**参数**：
- `minOrdinal` (int)：最小允许的 SizeCategory ordinal
- `maxOrdinal` (int)：最大允许的 SizeCategory ordinal

**返回值**：boolean，设置成功返回 true，失败返回 false

**前置条件**：sizeCatProbs() 返回的数组长度必须等于 SizeCategory.values().length

**副作用**：修改传入的概率数组（临时修改），设置实例的 sizeCat 字段

**核心实现逻辑**：
1. 复制 sizeCatProbs() 返回的概率数组
2. 将 minOrdinal 之前和 maxOrdinal 之后的概率设为 0
3. 使用 Random.chances() 选择有效的尺寸类别
4. 设置 sizeCat 字段

**边界情况**：如果有效范围内所有概率都为0，返回 false

### minWidth()
**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：返回当前尺寸类别的最小宽度

**参数**：无

**返回值**：int，sizeCat.minDim

**前置条件**：sizeCat 必须不为 null

**副作用**：无

**核心实现逻辑**：
直接返回 sizeCat.minDim

**边界情况**：如果 sizeCat 为 null，会抛出 NullPointerException

### maxWidth()
**可见性**：public

**是否覆写**：否

**方法职责**：返回当前尺寸类别的最大宽度

**参数**：无

**返回值**：int，sizeCat.maxDim

**前置条件**：sizeCat 必须不为 null

**副作用**：无

**核心实现逻辑**：
直接返回 sizeCat.maxDim

**边界情况**：如果 sizeCat 为 null，会抛出 NullPointerException

### minHeight()
**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：返回当前尺寸类别的最小高度

**参数**：无

**返回值**：int，sizeCat.minDim

**前置条件**：sizeCat 必须不为 null

**副作用**：无

**核心实现逻辑**：
直接返回 sizeCat.minDim

**边界情况**：如果 sizeCat 为 null，会抛出 NullPointerException

### maxHeight()
**可见性**：public

**是否覆写**：否

**方法职责**：返回当前尺寸类别的最大高度

**参数**：无

**返回值**：int，sizeCat.maxDim

**前置条件**：sizeCat 必须不为 null

**副作用**：无

**核心实现逻辑**：
直接返回 sizeCat.maxDim

**边界情况**：如果 sizeCat 为 null，会抛出 NullPointerException

### sizeFactor()
**可见性**：public

**是否覆写**：否

**方法职责**：返回房间的尺寸因子，用于各种计数和权重计算

**参数**：无

**返回值**：int，sizeCat.roomValue

**前置条件**：sizeCat 必须不为 null

**副作用**：无

**核心实现逻辑**：
直接返回 sizeCat.roomValue

**边界情况**：如果 sizeCat 为 null，会抛出 NullPointerException

### mobSpawnWeight()
**可见性**：public

**是否覆写**：否

**方法职责**：返回怪物生成权重，入口房间权重固定为1

**参数**：无

**返回值**：int，非入口房间返回 sizeFactor()，入口房间返回 1

**前置条件**：sizeCat 必须不为 null（非入口房间情况下）

**副作用**：无

**核心实现逻辑**：
```java
if (isEntrance()){
    return 1; //entrance rooms don't have higher mob spawns even if they're larger
}
return sizeFactor();
```

**边界情况**：入口房间不受尺寸影响，始终返回 1

### connectionWeight()
**可见性**：public

**是否覆写**：否

**方法职责**：返回房间连接权重，用于房间连接算法

**参数**：无

**返回值**：int，sizeFactor() 的平方

**前置条件**：sizeCat 必须不为 null

**副作用**：无

**核心实现逻辑**：
返回 sizeFactor() * sizeFactor()

**边界情况**：更大的房间有更高的连接权重

### canMerge()
**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：检查是否可以在指定点合并房间

**参数**：
- `l` (Level)：关卡实例
- `other` (Room)：要合并的其他房间
- `p` (Point)：合并点
- `mergeTerrain` (int)：合并地形类型

**返回值**：boolean，如果合并点不是固体地形则返回 true

**前置条件**：pointInside(p, 1) 必须在关卡范围内

**副作用**：无

**核心实现逻辑**：
```java
int cell = l.pointToCell(pointInside(p, 1));
return (Terrain.flags[l.map[cell]] & Terrain.SOLID) == 0;
```

**边界情况**：如果 pointInside 返回的点超出关卡范围，pointToCell 可能抛出异常

### createRoom()
**可见性**：public static

**是否覆写**：否

**方法职责**：工厂方法，根据当前地牢深度创建随机标准房间

**参数**：无

**返回值**：StandardRoom，新创建的房间实例

**前置条件**：Dungeon.depth 必须在 1-26 范围内（chances 数组索引）

**副作用**：无

**核心实现逻辑**：
```java
return Reflection.newInstance(rooms.get(Random.chances(chances[Dungeon.depth])));
```
使用反射根据概率配置创建房间实例。

**边界情况**：如果 chances[Dungeon.depth] 的所有概率都为0，Random.chances 返回 -1，会导致 IndexOutOfBoundsException

## 8. 对外暴露能力

### 显式 API
- `sizeCatProbs()`：供子类覆写以自定义尺寸概率
- `setSizeCat()` 系列方法：供外部控制房间尺寸
- `sizeFactor()`、`mobSpawnWeight()`、`connectionWeight()`：供关卡生成器使用
- `createRoom()`：供关卡生成器创建房间实例

### 内部辅助方法
- `minWidth()`/`maxWidth()`/`minHeight()`/`maxHeight()`：主要供 Room 基类的 setSize() 方法使用
- `canMerge()`：供房间合并算法使用

### 扩展入口
- 子类可以通过覆写 `sizeCatProbs()` 来改变尺寸类别概率
- 子类必须实现 `paint()` 方法来定义具体的房间绘制逻辑
- 子类可以覆写 `sizeFactor()`、`mobSpawnWeight()`、`connectionWeight()` 来自定义权重计算

## 9. 运行机制与调用链

### 创建时机
- 关卡生成过程中，当需要添加标准房间时调用 createRoom()
- 关卡加载时从存档中恢复房间（通过 Bundlable 接口）

### 调用者
- `Level` 类的生成方法
- 房间连接和布局算法
- 关卡序列化/反序列化系统

### 被调用者
- `Random.chances()`：用于概率选择
- `Reflection.newInstance()`：用于创建房间实例
- `Room` 基类的各种方法：空间计算、连接管理等

### 系统流程位置
在关卡生成流程中，StandardRoom 处于房间类型选择和房间绘制阶段之间：
1. 关卡生成器决定需要添加标准房间
2. 调用 StandardRoom.createRoom() 创建具体房间实例
3. 设置房间位置和尺寸
4. 调用房间的 paint() 方法进行实际绘制

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| 无 | 无 | StandardRoom 本身不直接引用任何 messages 文案 |

### 依赖的资源
- 无直接依赖的纹理、图标或音效资源

### 中文翻译来源
项目内未找到官方对应译名，使用英文原名 "Standard Room"

## 11. 使用示例

### 基本用法
```java
// 在关卡生成器中创建随机标准房间
StandardRoom room = StandardRoom.createRoom();
room.setSize(); // 设置随机尺寸
level.placeRoom(room); // 放置到关卡中
room.paint(level); // 绘制房间（由具体子类实现）
```

### 扩展示例
```java
// 自定义尺寸概率的房间子类
public class CustomRoom extends StandardRoom {
    @Override
    public float[] sizeCatProbs() {
        // 50% LARGE, 50% GIANT, 0% NORMAL
        return new float[]{0, 0.5f, 0.5f};
    }
    
    @Override
    public void paint(Level level) {
        // 自定义绘制逻辑
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);
    }
}
```

## 12. 开发注意事项

### 状态依赖
- `sizeCat` 字段在实例化后应该保持不变
- 尺寸相关的 getter 方法依赖于 `sizeCat` 不为 null

### 生命周期耦合
- 房间的创建和初始化紧密耦合到关卡生成流程
- `paint()` 方法只能在房间被正确放置到关卡后调用

### 常见陷阱
- 忘记覆写 `paint()` 方法会导致 AbstractMethodError
- `sizeCatProbs()` 返回的数组长度必须等于 SizeCategory.values().length（即3）
- `createRoom()` 方法依赖于 `Dungeon.depth` 的有效性，测试时需要注意

## 13. 修改建议与扩展点

### 适合扩展的位置
- `sizeCatProbs()`：自定义尺寸概率分布
- `sizeFactor()` 系列方法：自定义权重计算逻辑
- `paint()`：实现具体的房间绘制逻辑

### 不建议修改的位置
- 静态的 `rooms` 和 `chances` 数组：这些是核心配置，修改会影响整个游戏平衡
- `createRoom()` 的基本逻辑：这是房间创建的核心工厂方法

### 重构建议
- 考虑将 `rooms` 和 `chances` 配置移到外部配置文件中，便于调整而无需重新编译
- `setSizeCat` 方法的多个重载版本可以考虑使用 Builder 模式简化

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（确认无相关翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点