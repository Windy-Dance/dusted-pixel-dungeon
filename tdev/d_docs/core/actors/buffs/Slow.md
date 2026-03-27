# Slow (减速) 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Slow.java` |
| **包名** | `com.shatteredpixel.shatteredpixeldungeon.actors.buffs` |
| **文件类型** | class |
| **继承关系** | `extends FlavourBuff` |
| **代码行数** | 43 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`Slow` 负责实现角色的“减速”状态逻辑。它通过控制角色的速度修正因子，使角色执行动作（移动、攻击、施法）所需的时间增加。

### 系统定位
属于 Buff 系统中的机动性干扰分支。它是对应 `Haste`（加速）的负面状态，广泛应用于各种冰属性攻击、粘性环境或衰老魔法中。

### 不负责什么
- 不负责具体的时间消耗计算（由 `Char.speed()` 和 `Char.act()` 结合该 Buff 的存在来完成）。
- 不负责减速效果的叠加算法（逻辑通常由 `Buff.prolong` 处理）。

## 3. 结构总览

### 主要成员概览
- **常量 DURATION**: 默认持续时间（10 回合）。
- **icon() 方法**: 指定使用的 UI 图标索引。
- **tintIcon() 方法**: 对图标进行特定染色处理。

### 主要逻辑块概览
- **风味化继承**: 继承自 `FlavourBuff`，意味着它主要通过 `left`（剩余时间）来维持，不包含复杂的内部状态机。
- **视觉定制**: 采用了与时间相关的图标，并将其染成偏橙红色的警戒色。

### 生命周期/调用时机
1. **产生**：被减速魔法击中、受到霜冻影响。
2. **活跃期**：角色执行任何操作的时间消耗变为原来的 2 倍（具体倍率通常由 `Char.speed()` 方法定义）。
3. **结束**：持续时间结束。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `FlavourBuff`：
- 提供基础的时间计时和存档。
- 提供描述文本的自动格式化。

### 协作对象
- **Char**: 目标角色。在 `Char.speed()` 中会检查是否存在 `Slow.class` 的 Buff。
- **BuffIndicator.TIME**: 提供时钟样式的图标。
- **Haste**: 在逻辑上与其对应的正面 Buff。

```mermaid
graph LR
    Buff --> FlavourBuff
    FlavourBuff --> Slow
    Slow -->|Checked by| CharSpeed[Char.speed() Logic]
    Slow -->|Displays| BuffIndicator[TIME Icon + Tint]
```

## 5. 字段/常量详解

### 静态常量
- **DURATION**: 10.0f 回合。

## 6. 构造与初始化机制
通过实例初始化块设置 `type = NEGATIVE` 和 `announced = true`。

## 7. 方法详解

### icon()

**方法职责**：定义状态栏图标。
返回 `BuffIndicator.TIME`。这表示减速被视为一种对“时间/速度”的操纵。

---

### tintIcon(Image icon)

**方法职责**：图标染色。
```java
icon.hardlight(1f, 0.33f, 0.2f);
```
**分析**：应用了偏红的橙色（R=1.0, G=0.33, B=0.2）。这与加速（通常为绿色调）形成鲜明对比，提示玩家这是一个负面状态。

---

### iconFadePercent()

**方法职责**：图标淡化。
基于剩余时间线性淡化，使玩家能直观预判减速何时结束。

## 8. 对外暴露能力
主要通过 `Buff.affect(target, Slow.class)` 接口进行应用。

## 9. 运行机制与调用链
`Frost.act()` -> `Buff.affect(Slow.class)` -> `Char.speed()` 检查 Buff 存在 -> `speed *= 0.5f` -> 操作耗时翻倍。

## 10. 资源、配置与国际化关联

### 本地化词条
- `actors.buffs.Slow.name`: 减速
- `actors.buffs.Slow.desc`: “你的动作变迟缓了。剩余时长：%s。”

## 11. 使用示例

### 在代码中施加减速
```java
Buff.affect(target, Slow.class, 5f); // 减速 5 回合
```

## 12. 开发注意事项

### 速度影响
在 Shattered PD 的核心逻辑中，减速通常意味着速度减半（耗时变为 200%）。这在对抗快速敌人或逃离陷阱时是极其致命的。

### 互斥逻辑
虽然源码中未显式展示与 `Haste` 的互斥，但通常在逻辑层（如 `Item` 或 `Hero`）会进行判断，或者两者共存但数值相互抵消。

## 13. 修改建议与扩展点

### 引入等级制度
可以修改 `Slow` 使其支持 `level` 字段，从而实现不同强度的减速（如减速 30% 或减速 70%）。

## 14. 事实核查清单

- [x] 是否分析了默认持续时间：是 (10 回合)。
- [x] 是否解析了图标及其染色逻辑：是 (TIME 图标, 橙红色)。
- [x] 是否说明了它对角色速度的实际影响：是 (通常为耗时翻倍)。
- [x] 是否明确了它作为 FlavourBuff 的特征：是。
- [x] 图像索引属性是否核对：是 (BuffIndicator.TIME)。
