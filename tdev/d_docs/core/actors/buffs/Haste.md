# Haste (急速) 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Haste.java` |
| **包名** | `com.shatteredpixel.shatteredpixeldungeon.actors.buffs` |
| **文件类型** | class |
| **继承关系** | `extends FlavourBuff` |
| **代码行数** | 40 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`Haste` 负责实现角色的“急速”状态逻辑。它通过控制角色的速度修正因子，使角色执行动作（主要是移动）所需的时间减少，从而在相同的时间内能比平时行动更多次。

### 系统定位
属于 Buff 系统中的机动性增强分支。它是对应 `Slow`（减速）的正面状态，广泛应用于急速药水、特定附魔或天赋触发中。

### 不负责什么
- 不负责具体的时间节省计算（由 `Char.speed()` 方法根据此 Buff 的存在来调整返回值）。
- 不负责攻击速度的额外加成（除非 `Char` 类的速度定义包含了攻击耗时）。

## 3. 结构总览

### 主要成员概览
- **常量 DURATION**: 默认持续时间（20 回合）。
- **icon() 方法**: 指定使用的 UI 图标索引。
- **tintIcon() 方法**: 对图标进行特定染色处理。

### 主要逻辑块概览
- **风味化继承**: 继承自 `FlavourBuff`，主要负责时间衰减和 UI 描述，不包含复杂的 `act()` 行为逻辑。
- **视觉特征**: 使用专门的急速图标，并将其染成亮黄色。

### 生命周期/调用时机
1. **产生**：喝下急速药水、触发守林人相关植物增益。
2. **活跃期**：角色移动耗时减半（通常倍率为 2.0f）。
3. **结束**：持续时间结束。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `FlavourBuff`：
- 提供 `left` 时间变量及存档支持。
- 提供状态图标的百分比淡化显示。

### 协作对象
- **Char**: 被加速的主体。
- **BuffIndicator.HASTE**: 提供靴子形状的急速图标。
- **Slow**: 在逻辑上与其对应的负面 Buff。

```mermaid
graph LR
    Buff --> FlavourBuff
    FlavourBuff --> Haste
    Haste -->|Checked by| CharSpeed[Char.speed() Logic]
    Haste -->|Displays| BuffIndicator[HASTE Icon + Yellow Tint]
```

## 5. 字段/常量详解

### 静态常量
- **DURATION**: 20.0f 回合。

## 6. 构造与初始化机制
通过实例初始化块设置 `type = POSITIVE`。注意：该 Buff 默认 `announced` 为 `false`（不显示获得提示），通常由物品逻辑手动处理提示。

## 7. 方法详解

### icon()

**方法职责**：定义状态栏图标。
返回 `BuffIndicator.HASTE`。

---

### tintIcon(Image icon)

**方法职责**：图标染色。
```java
icon.hardlight(1f, 0.8f, 0f);
```
**分析**：应用了亮黄色（R=1.0, G=0.8, B=0.0）。这种明亮的色调提示玩家这是一个积极的机动性增益。

---

### iconFadePercent()

**方法职责**：图标淡化。
基于 `DURATION` (20f) 计算消耗比例，使得图标随时间流逝逐渐变淡。

## 8. 对外暴露能力
主要通过 `Buff.affect(target, Haste.class)` 应用。

## 9. 运行机制与调用链
`PotionOfHaste.apply()` -> `Buff.affect(Haste.class)` -> `Char.speed()` 检查 Buff 存在 -> `speed *= 2.0f` -> 动作耗时减半。

## 10. 资源、配置与国际化关联

### 本地化词条
- `actors.buffs.Haste.name`: 急速
- `actors.buffs.Haste.desc`: “你感到精力充沛，动作敏捷。剩余时长：%s。”

## 11. 使用示例

### 在代码中施加急速
```java
Buff.affect(hero, Haste.class, Haste.DURATION);
```

## 12. 开发注意事项

### 速度实现
急速状态的具体数值（如 2 倍速）通常不在 `Haste.java` 中定义，而是在 `Char.java` 的 `speed()` 或 `moveTime()` 逻辑中通过 `if (buff(Haste.class) != null)` 来实现。这是一种典型的解耦设计。

### 叠加处理
使用 `Buff.affect` 会覆盖现有时间，而 `Buff.prolong` 会累加时间。

## 13. 修改建议与扩展点

### 增强变体
可以创建 `GreaterHaste`（大急速），通过不同的染色或额外的 `iconTextDisplay` 来区分强度。

## 14. 事实核查清单

- [x] 是否分析了默认持续时间：是 (20 回合)。
- [x] 是否解析了图标及其染色逻辑：是 (HASTE 图标, 亮黄色)。
- [x] 是否说明了它对角色速度的实际影响：是 (通常为耗时减半)。
- [x] 是否明确了它作为 FlavourBuff 的特征：是。
- [x] 图像索引属性是否核对：是 (BuffIndicator.HASTE)。
