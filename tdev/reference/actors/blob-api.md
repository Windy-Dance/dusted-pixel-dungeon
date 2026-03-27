# Blob API 参考

## 类声明
public class Blob extends Actor

## 类职责
Blob是所有区域效果（气体、火焰、水等）的基类，使用元胞自动机在地图上扩散和演化。它负责管理区域效果的生命周期、扩散算法、视觉表现和与游戏世界的交互。

## 关键字段

| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|-------|------|---------|-------|------|
| volume | int | public | 0 | 当前Blob的总体积/强度，用于跟踪是否存在活跃效果 |
| cur[] | int[] | public | null | 当前帧的浓度数组，索引为地图格子ID，值为该格子的浓度 |
| off[] | int[] | protected | null | 下一帧的浓度数组，用于双缓冲避免并发修改问题 |
| emitter | BlobEmitter | public | null | 视觉效果发射器，用于渲染Blob的粒子效果 |
| area | Rect | public | new Rect() | 当前活跃区域的边界矩形，用于优化计算范围 |
| alwaysVisible | boolean | public | false | 是否始终可见（即使在迷雾中） |

## 核心方法 - 播种和清除

| 方法签名 | 返回值 | 说明 |
|----------|--------|------|
| seed(Level level, int cell, int amount) | void | 在指定关卡的指定格子播种指定数量的Blob效果 |
| clear(int cell) | void | 清除指定格子的Blob浓度，减少总体积 |
| fullyClear() | void | 完全清除整个Blob，重置所有状态和数组 |

## 可重写方法

| 方法签名 | 返回值 | 默认行为 | 说明 |
|----------|--------|----------|------|
| evolve() | void | 实现元胞自动机扩散算法 | 子类可重写以自定义扩散逻辑 |
| tileDesc() | String | null | 返回格子描述文本，用于UI显示 |
| landmark() | Notes.Landmark | null | 返回关联的地标，当英雄看到时添加到日记 |
| onBuildFlagMaps(Level l) | void | 空实现 | 在构建地形标志映射时调用，影响行走标志 |
| onUpdateCellFlags(Level l, int cell) | void | 空实现 | 当特定格子地形变化时更新标志 |

## 静态工厂方法

| 方法签名 | 返回值 | 说明 |
|----------|--------|------|
| seed(int cell, int amount, Class<T> type) | T | 在当前关卡播种指定类型的Blob，自动创建实例并管理生命周期 |
| seed(int cell, int amount, Class<T> type, Level level) | T | 在指定关卡播种指定类型的Blob，支持跨关卡操作 |
| volumeAt(int cell, Class<? extends Blob> type) | int | 获取指定格子指定类型Blob的当前浓度 |

## 演化算法

`evolve()`方法实现了基于元胞自动机的扩散算法，工作原理如下：

1. **双缓冲机制**：使用`cur[]`和`off[]`两个数组交替存储当前状态和下一状态，避免在计算过程中修改正在读取的数据。

2. **邻域平均**：对area矩形内每个非固体格子，计算其自身和四个相邻格子（上、下、左、右）的浓度总和，然后除以有效格子数量得到平均值。

3. **浓度衰减**：平均值减1作为新浓度，如果结果小于0则设为0。这确保了Blob会随时间自然消散。

4. **动态区域扩展**：如果新计算的浓度大于0，会相应扩展area边界，确保下一帧包含所有活跃区域。

5. **体积更新**：累加所有格子的新浓度作为新的总体积。

6. **阻塞处理**：固体格子（墙壁等）始终浓度为0，不会传播Blob效果。

算法公式：
```
newValue = (sumOfCurrentCellAndNeighbors / neighborCount) - 1
if newValue < 0: newValue = 0
```

## 视觉效果

Blob通过`BlobEmitter`系统实现视觉效果：

1. **Emitter绑定**：通过`use(BlobEmitter emitter)`方法将视觉效果发射器绑定到Blob实例。

2. **粒子系统**：`BlobEmitter`基于`cur[]`数组中的浓度值动态调整粒子密度和动画。

3. **自动管理**：当Blob体积为0时，视觉效果自动隐藏；有浓度时自动显示。

4. **性能优化**：只在`area`定义的活跃区域内渲染效果，避免全地图计算。

## 使用示例

### 示例1: 创建简单的气体效果
```java
// 在当前位置创建有毒气体
ToxicGas gas = Blob.seed(hero.pos, 20, ToxicGas.class);
// 气体会自动扩散并造成伤害
```

### 示例2: 创建自定义区域效果
```java
// 自定义火焰效果，重写evolve方法改变扩散速度
public class FastFire extends Blob {
    @Override
    protected void evolve() {
        // 调用父类方法但减少衰减
        super.evolve();
        // 增加额外的扩散逻辑
        for (int i = area.top; i <= area.bottom; i++) {
            for (int j = area.left; j <= area.right; j++) {
                int cell = j + i * Dungeon.level.width();
                if (off[cell] > 0 && Random.Float() < 0.3f) {
                    // 随机向邻近格子传播
                    spreadToAdjacent(cell);
                }
            }
        }
    }
}
```

### 示例3: 在特定位置播种气体
```java
// 在房间中心播种电击效果
Room room = Dungeon.level.rooms.get(5); // 获取第5个房间
int center = room.center(); // 获取房间中心格子
Electricity shock = Blob.seed(center, 15, Electricity.class, Dungeon.level);
```

## 相关子类

常见的Blob子类包括：

- **ToxicGas**: 有毒气体，对角色造成中毒伤害
- **Fire**: 火焰效果，造成燃烧伤害并可能点燃物品
- **Electricity**: 电击效果，在水域中快速传导
- **Web**: 蛛网效果，减缓移动速度
- **StormCloud**: 雷暴云，周期性释放电击
- **Frost**: 寒冰效果，冻结敌人并制造冰面
- **ConfusionGas**: 混乱气体，使敌人迷失方向
- **Inferno**: 地狱火，比普通火焰更强大的燃烧效果

这些子类通常重写`evolve()`、`tileDesc()`和`landmark()`方法来提供特定的行为和视觉反馈。

## 常见错误

1. **忘记初始化数组**：直接访问`cur[]`或`off[]`而不调用`seed()`可能导致空指针异常。应始终使用静态工厂方法或先调用`seed()`。

2. **错误的生命周期管理**：手动创建Blob实例而不通过工厂方法可能导致优先级问题，使得Blob获得"额外回合"。

3. **忽略体积检查**：在清除Blob后未检查`volume == 0`就继续处理，可能导致无效计算。

4. **area边界错误**：直接修改`area`字段而不是通过`seed()`方法，可能导致扩散计算范围错误。

5. **并发修改问题**：尝试在`evolve()`过程中直接修改`cur[]`数组，而非使用`off[]`进行下一帧计算。

6. **内存泄漏**：长时间运行的游戏可能积累大量空Blob实例，应定期清理`volume == 0`的实例。