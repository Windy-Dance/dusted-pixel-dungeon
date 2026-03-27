# FissureRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\FissureRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 82 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
FissureRoom 实现了一个裂缝主题的房间，内部包含复杂的几何算法来生成不规则的深渊（CHASM）区域。房间根据尺寸大小采用不同的生成策略：极小房间只在中心放置一个深渊格子，较大房间则使用基于距离边缘的算法生成多层裂缝结构。

### 系统定位
作为 StandardRoom 的具体实现，FissureRoom 提供了一种具有复杂数学计算的特殊房间类型，通过几何距离算法创建逼真的裂缝效果。

### 不负责什么
- 不处理房间与其他房间的连接逻辑（由父类处理）
- 不管理深渊相关的游戏机制（如坠落伤害等）
- 不包含随机内容生成（如怪物、物品等）

## 3. 结构总览

### 主要成员概览
- 无实例字段
- 无静态字段或常量

### 主要逻辑块概览
- 尺寸限制覆写（minWidth/minHeight）
- 尺寸概率配置（sizeCatProbs）
- 房间绘制逻辑（paint）
- 几何距离计算算法
- 边缘概率计算

### 生命周期/调用时机
- 在地牢生成过程中通过 StandardRoom.createRoom() 创建
- paint() 方法在房间布局完成后被调用以填充地形和生成裂缝

## 4. 继承与协作关系

### 父类提供的能力
From StandardRoom 继承：
- 尺寸类别系统（SizeCategory）
- 权重计算方法
- 基础的房间尺寸和连接逻辑
From Room 继承：
- 空间几何操作
- 连接管理
- 随机点生成
- 抽象的 paint() 方法

### 覆写的方法
- `sizeCatProbs()` - 设置尺寸概率为 `{6, 3, 1}`
- `minHeight()` - 设置最小高度为5
- `minWidth()` - 设置最小宽度为5
- `paint()` - 实现复杂的裂缝生成逻辑

### 实现的接口契约
- Graph.Node 接口（通过 Room 继承）
- Bundlable 接口（通过 Room 继承）

### 依赖的关键类
- `Level` - 地牢关卡对象
- `Terrain` - 地形类型（WALL, EMPTY, CHASM）
- `Painter` - 地形填充工具
- `Point` - 点坐标操作
- `Random` - 随机数生成

### 使用者
- StandardRoom.createRoom() 工厂方法会创建此房间的实例
- 关卡生成器调用 paint() 方法来渲染房间

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无

## 6. 构造与初始化机制

### 构造器
使用默认的无参构造器，继承自 StandardRoom。

### 初始化块
继承了 StandardRoom 的 `{ setSizeCat(); }` 初始化块。

### 初始化注意事项
- 尺寸类别在 StandardRoom 构造时就已经确定
- 由于 sizeCatProbs() 返回 `{6, 3, 1}`，房间有约 60% 概率为 NORMAL 尺寸，30% 概率为 LARGE 尺寸，10% 概率为 GIANT 尺寸
- 最小尺寸限制为5x5，确保裂缝效果可见

## 7. 方法详解

### sizeCatProbs()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：返回各尺寸类别的选择概率

**参数**：无

**返回值**：float[]，`{6, 3, 1}`

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回固定的概率数组，NORMAL 尺寸概率为6，LARGE 尺寸概率为3，GIANT 尺寸概率为1。

**边界情况**：所有三种尺寸类别都可能被选中，但 NORMAL 占主导

### minHeight()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：返回房间的最小高度，确保至少为5格

**参数**：无

**返回值**：int，Math.max(super.minHeight(), 5)

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
调用父类的 minHeight() 并与5取最大值，确保房间足够大以显示裂缝效果。

**边界情况**：如果父类返回的尺寸大于5，则使用父类的值

### minWidth()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：返回房间的最小宽度，确保至少为5格

**参数**：无

**返回值**：int，Math.max(super.minWidth(), 5)

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
调用父类的 minWidth() 并与5取最大值，确保房间足够大以显示裂缝效果。

**边界情况**：如果父类返回的尺寸大于5，则使用父类的值

### paint(Level level)

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制房间的具体内容，包括裂缝的生成

**参数**：
- `level` (Level)：要绘制的关卡

**返回值**：void

**前置条件**：房间的位置和尺寸必须已确定

**副作用**：
- 修改 level.map 数组中的地形

**核心实现逻辑**：
1. 填充外层为 WALL（墙壁）
2. 将所有连接的门设置为 REGULAR 类型
3. 填充内层（距离边缘1格）为 EMPTY（空地）
4. 根据房间面积决定裂缝生成策略：
   - **极小房间（面积 ≤ 25）**：
     - 只在房间中心放置一个 CHASM（深渊）格子
   - **较大房间（面积 > 25）**：
     - 计算较小维度：`smallestDim = Math.min(width(), height())`
     - 计算地板宽度：`floorW = (int)Math.sqrt(smallestDim)`
     - 计算边缘地板概率：
       - `edgeFloorChance = (float)Math.sqrt(smallestDim) % 1`
       - 调整为 `(edgeFloorChance + (floorW-1)*0.5f) / (float)floorW`
     - 遍历内层区域（距离边缘2格以内）：
       - 计算当前点到上下边缘的最小距离（v）
       - 计算当前点到左右边缘的最小距离（h）
       - 如果 `min(v, h) > floorW`，设置为 CHASM
       - 如果 `min(v, h) == floorW` 且随机数 > edgeFloorChance，设置为 CHASM

**边界情况**：
- 极小房间使用简化逻辑避免复杂计算
- 边缘概率计算确保裂缝边缘有一定的随机性
- 距离计算确保裂缝从中心向外扩散

## 8. 对外暴露能力

### 显式 API
- 所有 public 方法都是继承自父类的覆写，没有新增的公共API

### 内部辅助方法
- 所有逻辑都在 paint() 方法内部实现

### 扩展入口
- 此类是具体的最终实现，一般不需要扩展

## 9. 运行机制与调用链

### 创建时机
- 在地牢生成过程中，当 StandardRoom.createRoom() 随机选择到 FissureRoom 时创建

### 调用者
- StandardRoom.createRoom() 创建实例
- LevelBuilder 调用 paint() 方法

### 被调用者
- 调用父类 StandardRoom 和 Room 的方法
- 调用 Painter.fill()/set() 填充地形
- 调用 Random.Float() 生成随机数
- 调用数学函数进行几何计算

### 系统流程位置
- 处于地牢生成的房间绘制阶段
- 在房间连接完成后、内容填充前执行

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无直接引用的消息键。

### 依赖的资源
- 深渊（CHASM）地形的视觉资源
- 墙壁和空地等地形的视觉资源

### 中文翻译来源
项目内未找到官方对应译名。"FissureRoom" 直译为"裂缝房间"，但由于 levels_zh.properties 文件中没有对应的官方翻译，保留英文名称。

## 11. 使用示例

### 基本用法
```java
// 通过工厂方法创建 FissureRoom 实例
StandardRoom room = StandardRoom.createRoom(); // 可能返回 FissureRoom

// 房间的绘制由关卡生成器自动调用
room.paint(level);
```

### 注意事项
由于 FissureRoom 是通过反射动态创建的，通常不需要直接实例化。

## 12. 开发注意事项

### 状态依赖
- 依赖于父类 StandardRoom 的 sizeCat 初始化状态
- paint() 方法依赖于房间的正确尺寸计算

### 生命周期耦合
- 必须在房间连接完成后调用 paint()
- connected 集合用于正确设置门的类型

### 常见陷阱
- 最小尺寸限制为5x5，如果强制更小的尺寸会导致裂缝效果异常
- 面积阈值（25）决定了生成策略的切换点
- 边缘概率计算使用了复杂的数学公式，修改时需要注意平衡性
- 距离计算假设房间为矩形，非矩形房间可能导致意外结果

## 13. 修改建议与扩展点

### 适合扩展的位置
- 此类是具体的最终实现，一般不需要扩展

### 不建议修改的位置
- 尺寸概率配置经过平衡设计，修改会影响房间分布
- 最小尺寸限制确保基本的视觉效果，不应降低
- 几何算法经过精心设计，随意修改可能破坏裂缝效果

### 重构建议
- 可以考虑将常量值（如25、2）提取为命名常量以提高可读性
- 几何计算逻辑可以提取到单独的方法中以提高可维护性
- 边缘概率计算公式可以添加注释说明其设计意图

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（确认无官方翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点