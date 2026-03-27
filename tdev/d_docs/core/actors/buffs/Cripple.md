# Cripple (残废) 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Cripple.java` |
| **包名** | `com.shatteredpixel.shatteredpixeldungeon.actors.buffs` |
| **文件类型** | class |
| **继承关系** | `extends FlavourBuff` |
| **代码行数** | 38 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`Cripple` 负责实现角色的“残废”状态逻辑。它主要通过降低角色的移动速度，模拟角色腿部受创或其他物理损伤导致的机动性下降。

### 系统定位
属于 Buff 系统中的物理干扰/机动性削减分支。与魔法性质的 `Slow`（减速）不同，残废通常由物理陷阱（如夹击陷阱）、重击攻击或高空坠落引发。

### 不负责什么
- 不负责阻止角色的攻击动作（它仅影响移动时间）。
- 不负责由于残废产生的直接伤害（伤害由引发残废的源头，如流血，负责）。

## 3. 结构总览

### 主要成员概览
- **常量 DURATION**: 默认持续时间（10 回合）。
- **icon() 方法**: 指定使用的 UI 图标索引。
- **iconFadePercent() 方法**: 控制图标随时间的淡化。

### 主要逻辑块概览
- **风味化继承**: 继承自 `FlavourBuff`，这意味着它是一个纯净的时间轴控制 Buff，没有复杂的 `act()` 覆写逻辑。
- **属性标识**: 标记为 `NEGATIVE`（负面效果）且 `announced = true`（获得时显示提示）。

### 生命周期/调用时机
1. **产生**：被致残性陷阱、攻击击中。
2. **活跃期**：角色每移动一格所需的时间增加（通常由 `Char.speed()` 或 `Char.moveTime()` 处理）。
3. **结束**：持续时间结束。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `FlavourBuff`：
- 提供 `left` 变量管理剩余时间。
- 提供基于时间的自动描述文本。

### 协作对象
- **Char**: 被致残的主体。在计算移动速度时会检查此 Buff。
- **BuffIndicator.CRIPPLE**: 提供断腿形状的残废图标。
- **GrippingTrap / FlashingTrap**: 产生残废效果的主要陷阱来源。

```mermaid
graph LR
    Buff --> FlavourBuff
    FlavourBuff --> Cripple
    Cripple -->|Checked by| CharSpeed[Char.moveTime() Logic]
    Cripple -->|UI| BuffIndicator[CRIPPLE Icon]
```

## 5. 字段/常量详解

### 静态常量
- **DURATION**: 10.0f 回合。

## 6. 构造与初始化机制
通过实例初始化块设置 `type = NEGATIVE` 和 `announced = true`。

## 7. 方法详解

### icon()

**方法职责**：定义状态栏图标。
返回 `BuffIndicator.CRIPPLE`。

---

### iconFadePercent()

**方法职责**：图标进度反馈。
基于 `DURATION` 计算剩余时间占比，用于 UI 进度环显示。

## 8. 对外暴露能力
主要通过 `Buff.prolong(target, Cripple.class, duration)` 等静态方法被外部系统调用。

## 9. 运行机制与调用链
`GrippingTrap.activate()` -> `Buff.prolong(Cripple.class)` -> `Char.speed()` 判定 -> 移动耗时增加。

## 10. 资源、配置与国际化关联

### 本地化词条
- `actors.buffs.Cripple.name`: 残废
- `actors.buffs.Cripple.desc`: “由于腿部受伤，你无法快速移动。剩余时长：%s。”

## 11. 使用示例

### 在代码中施加双倍时长的残废
```java
Buff.prolong(target, Cripple.class, Cripple.DURATION * 2f);
```

## 12. 开发注意事项

### 速度逻辑解耦
`Cripple` 本身并不包含 `speed *= 0.5f` 这样的代码。实际的速度惩罚在 `Char` 类（或其子类如 `Hero`, `Mob`）的移动逻辑中通过检查该 Buff 的存在来实现。

### 与 Slow 的区别
虽然在目前的 `Char.java` 实现中两者可能导致相同的速度惩罚，但它们代表了不同的语义（物理损伤 vs 魔法迟缓），便于后续进行差异化处理（如针对残废的特殊药膏）。

## 13. 修改建议与扩展点

### 增加伤害联动
可以修改残废逻辑，使角色在残废状态下每次强行位移（如被击退）都会受到少量的额外流血伤害。

## 14. 事实核查清单

- [x] 是否分析了默认持续时间：是 (10 回合)。
- [x] 是否解析了与 Slow 的语义区别：是 (物理受创 vs 魔法迟缓)。
- [x] 是否涵盖了图标淡化逻辑：是。
- [x] 是否说明了它作为 FlavourBuff 的特征：是。
- [x] 图像索引属性是否核对：是 (BuffIndicator.CRIPPLE)。
