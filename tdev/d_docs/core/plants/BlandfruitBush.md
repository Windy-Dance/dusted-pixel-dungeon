# BlandfruitBush (枯燥果灌木) 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/plants/BlandfruitBush.java` |
| **包名** | `com.shatteredpixel.shatteredpixeldungeon.plants` |
| **文件类型** | class |
| **继承关系** | `extends Plant` |
| **代码行数** | 43 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`BlandfruitBush` 负责实现“枯燥果灌木”植物及其种子的逻辑。它是一种产出型植物，触发时会掉落一个“枯燥果”（Blandfruit），该果实可以与不同种子炼金以获得各种 Buff。

### 系统定位
属于植物系统中的食物/产出分支。它是获取枯燥果的唯一自然来源，通常只在特定的“花园”房间或通过“再生法杖”随机生成。

### 不负责什么
- 不负责枯燥果的炼金逻辑（由 `Blandfruit` 类负责）。
- 不负责守林人的特殊交互（该植物对守林人没有额外的 Buff 增强）。

## 3. 结构总览

### 主要成员概览
- **BlandfruitBush 类**: 植物实体类，仅包含 `activate` 方法。
- **Seed 类**: 种子类，但在当前版本中，该种子被注释为“永不掉落”。

### 主要逻辑块概览
- **激活逻辑 (`activate`)**: 
  - 在当前位置调用 `Dungeon.level.drop()` 生成一个 `Blandfruit` 实例。
  - 调用 `sprite.drop()` 播放掉落动画。

### 生命周期/调用时机
1. **触发**：角色踩踏。
2. **激活**：产生果实，植物消失（由父类 `wither` 处理）。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `Plant`：
- 定义位置和图像索引（12）。

### 协作对象
- **Blandfruit**: 核心产出物，重要的食物/炼金基底。
- **Level**: 负责处理物品的掉落和地图更新。

```mermaid
graph LR
    Plant --> BlandfruitBush
    BlandfruitBush -->|Drops| Blandfruit
    BlandfruitBush *-- Seed
```

## 5. 字段/常量详解

### BlandfruitBush 字段
- **image**: 12。

## 6. 构造与初始化机制

### BlandfruitBush 初始化
通过初始化块设置 `image = 12`。

## 7. 方法详解

### activate(Char ch)

**方法职责**：定义产出逻辑。

**核心逻辑分析**：
```java
@Override
public void activate( Char ch ) {
    Dungeon.level.drop( new Blandfruit(), pos ).sprite.drop();
}
```
**分析**：该逻辑非常纯粹，不检查触发者身份，也不提供任何状态加成。它的唯一目的就是“收割”果实。

## 8. 对外暴露能力
主要通过 `activate()` 接口。

## 9. 运行机制与调用链
`Plant.trigger()` -> `BlandfruitBush.activate()` -> `Level.drop(Blandfruit)`。

## 10. 资源、配置与国际化关联
不适用。

## 11. 使用示例

### 场景生成
在关卡生成器中，如果判定某个房间为花园，会随机放置数个 `BlandfruitBush`：
```java
level.plant( new BlandfruitBush().couch( pos, level ) );
```

## 12. 开发注意事项

### 种子稀有性
源码注释明确指出 `//seed is never dropped`（种子永不掉落）。这意味着玩家无法通过种植来大面积收割枯燥果，必须依赖关卡自然生成。

### 触发优先级
由于枯燥果灌木被踩踏后会掉落实体物品，如果角色背包已满，果实会留在地面。

## 13. 修改建议与扩展点

### 守林人联动
目前的实现对守林人并不公平。可以增加逻辑，使守林人收割时有概率获得两个果实，或者获得一个预先随机炼金过的果实。

## 14. 事实核查清单

- [x] 是否分析了产出物：是（Blandfruit）。
- [x] 是否指出了种子的特殊状态：是（不掉落）。
- [x] 图像索引是否准确：是 (12)。
- [x] 逻辑是否仅限产出：是。
