# Vertigo (眩晕/迷失方向) 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Vertigo.java` |
| **包名** | `com.shatteredpixel.shatteredpixeldungeon.actors.buffs` |
| **文件类型** | class |
| **继承关系** | `extends FlavourBuff` |
| **代码行数** | 38 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`Vertigo` 负责实现角色的“眩晕”或“失去方向感”状态逻辑。它主要通过干扰角色的移动决策，使角色在尝试移动时会随机走向错误的方向。

### 系统定位
属于 Buff 系统中的控制/移动干扰分支。它通常由混乱气体（Confusion Gas）、特定的诅咒或脑部重击引发，是制造混乱场面和导致“地形杀”的关键机制。

### 不负责什么
- 不负责具体的随机位移算法（由 `Char.move()` 或 `Mob.act()` 内部检查此 Buff 存在后，通过随机数生成偏移量来实现）。
- 不负责阻止角色的攻击（仅影响移动方向，不影响攻击目标的选取，除非该目标需要移动才能触及）。

## 3. 结构总览

### 主要成员概览
- **常量 DURATION**: 默认持续时间（10 回合）。
- **icon() 方法**: 指定使用的 UI 图标索引。
- **iconFadePercent() 方法**: 控制图标随时间的淡化。

### 主要逻辑块概览
- **风味化继承**: 继承自 `FlavourBuff`，这意味着它主要作为一个逻辑标记存在，不包含内部的主动 `act()` 行为。
- **属性定义**: 标记为 `NEGATIVE` 且 `announced = true`。

### 生命周期/调用时机
1. **产生**：吸入混乱气体、触发雷暴藤（Stormvine）。
2. **活跃期**：角色尝试向点 A 移动，实际可能走向点 B、C 或 D。
3. **结束**：持续时间结束。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `FlavourBuff`：
- 提供基础的时间衰减和持久化存档。
- 提供自动格式化的 UI 描述支持。

### 协作对象
- **Char / Hero / Mob**: 目标角色。在角色的位移逻辑（`move` 系列方法）中会检查 `Vertigo.class`。
- **BuffIndicator.VERTIGO**: 提供表示方向迷失的图标（通常为交叉的箭头或螺旋线）。
- **Stormvine**: 产生此效果的主要植物来源。

```mermaid
graph LR
    Buff --> FlavourBuff
    FlavourBuff --> Vertigo
    Vertigo -->|Checked by| MoveLogic[Char.move() / Mob AI]
    MoveLogic -->|If Vertigo| RandomDir[Randomize Direction]
    Vertigo -->|UI| BuffIndicator[VERTIGO Icon]
```

## 5. 字段/常量详解

### 静态常量
- **DURATION**: 10.0f 回合。

## 6. 构造与初始化机制
通过实例初始化块设置 `type = NEGATIVE` 和 `announced = true`。

## 7. 方法详解

### icon()

**方法职责**：定义状态栏图标。
返回 `BuffIndicator.VERTIGO`。

---

### iconFadePercent()

**方法职责**：进度反馈。
基于剩余时长计算图标淡化度。

## 8. 对外暴露能力
主要通过 `Buff.affect(target, Vertigo.class, duration)` 等静态方法进行应用。

## 9. 运行机制与调用链
`ConfusionGas.act()` -> `Buff.affect(Vertigo.class)` -> `Char.move(cell)` 检查 Buff -> `cell = randomNeighbour()` -> 产生错误位移。

## 10. 资源、配置与国际化关联

### 本地化词条
- `actors.buffs.Vertigo.name`: 眩晕
- `actors.buffs.Vertigo.desc`: “你感到世界正在旋转，无法走直线！剩余时长：%s。”

## 11. 使用示例

### 在代码中施加眩晕
```java
Buff.affect(target, Vertigo.class, Vertigo.DURATION);
```

## 12. 开发注意事项

### 随机移动实现
`Vertigo` 类本身并不包含随机数生成逻辑。具体的方向干扰逻辑分散在：
- `Mob.act()`: 怪物寻路时若处于眩晕态，会随机选择相邻格子。
- `Hero.handleInput()`: 玩家操作时若处于眩晕态，输入的移动指令会被拦截并重定向。

### 危险地形
眩晕状态下最致命的风险是失足坠入深渊（Chasm）或踩入已知的陷阱。开发者在设计关卡时应考虑眩晕陷阱与深渊的组合布局。

## 13. 修改建议与扩展点

### 增加平衡感影响
可以修改逻辑，使拥有“猫之优雅”或特定运动天赋的角色能够部分抵消眩晕带来的方向偏转概率。

## 14. 事实核查清单

- [x] 是否分析了默认持续时间：是 (10 回合)。
- [x] 是否解析了作为 FlavourBuff 的特征：是。
- [x] 是否明确了随机移动逻辑的实际位置：是（解耦在移动/AI逻辑中）。
- [x] 图像索引属性是否核对：是 (BuffIndicator.VERTIGO)。
- [x] 是否说明了对地形交互的间接影响：是。
