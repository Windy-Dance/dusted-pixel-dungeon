# Chill (冰冷) 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Chill.java` |
| **包名** | `com.shatteredpixel.shatteredpixeldungeon.actors.buffs` |
| **文件类型** | class |
| **继承关系** | `extends FlavourBuff` |
| **代码行数** | 58 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`Chill` 负责实现角色的“冰冷”状态逻辑。它通过控制角色的速度修正因子，使角色的移动和攻击变慢，且减速效果与状态的剩余时长直接挂钩。

### 系统定位
属于 Buff 系统中的机动性干扰分支。它是寒冷属性攻击（如冰帽、霜冻陷阱、霜冻法杖）产生的核心状态，具有随时间衰减的减速特性。

### 不负责什么
- 不负责由于冰冷导致的瞬间“冻结（Frost/Paralysis）”判定。
- 不负责具体的伤害计算（该状态本身不造成直接伤害）。

## 3. 结构总览

### 主要成员概览
- **常量 DURATION**: 默认持续时间（10 回合）。
- **speedFactor() 方法**: 动态计算速度惩罚系数。
- **attachTo() 方法**: 包含与燃烧状态的互斥处理。
- **fx() 方法**: 控制角色的冰冷视觉粒子。

### 主要逻辑块概览
- **相互排斥**: 获得冰冷状态时，会自动移除角色身上的 `Burning`（燃烧）状态。
- **动态衰减减速**: 减速强度随剩余时间（cooldown）线性衰减，每多 1 回合剩余时间额外减速 10%。
- **视觉反馈**: 在精灵周围显示蓝色的冰晶粒子效果。

### 生命周期/调用时机
1. **产生**：被寒冷攻击击中。
2. **活跃期**：角色速度受到动态限制。
3. **结束**：持续时间结束或被火源抵消。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `FlavourBuff`：
- 提供 `left` 变量管理时长。
- 提供基础描述文本支持。

### 协作对象
- **Burning**: 互斥状态。冷与火不可并存。
- **Char**: 目标角色。在 `Char.speed()` 计算中调用 `speedFactor()`。
- **CharSprite.State.CHILLED**: 提供蓝色冷气特效。
- **BuffIndicator.FROST**: 提供冰花形状的图标。

```mermaid
graph TD
    Buff --> FlavourBuff
    FlavourBuff --> Chill
    Chill -->|Removes| Burning
    Chill -->|Calculates| Factor[speedFactor: 1 - left*0.1]
    Factor -->|Limits| CharSpeed[Char.speed()]
    Chill -->|UI| BuffIndicator[FROST Icon]
```

## 5. 字段/常量详解

### 静态常量
- **DURATION**: 10.0f 回合。

## 6. 构造与初始化机制
通过实例初始化块设置 `type = NEGATIVE` 和 `announced = true`。附加时通过 `Buff.detach(target, Burning.class)` 强制灭火。

## 7. 方法详解

### speedFactor() [核心减速算法]

**可见性**：public

**算法职责**：计算当前剩余时间对速度的影响。

**核心逻辑分析**：
```java
return Math.max(0.5f, 1 - cooldown() * 0.1f);
```
**推导结果**：
- 剩余 **10+** 回合：速度系数为 **0.5**（减速 50%）。
- 剩余 **5** 回合：速度系数为 `1 - 0.5 = 0.5`。
- 剩余 **2** 回合：速度系数为 `1 - 0.2 = 0.8`（减速 20%）。
- 剩余 **1** 回合：速度系数为 `1 - 0.1 = 0.9`（减速 10%）。
**结论**：冰冷状态的减速效果会随着时间流逝而“逐渐消融”，直到彻底恢复。

---

### attachTo(Char target)

**方法职责**：处理状态冲突。
在挂载前调用 `Buff.detach( target, Burning.class )`。这确保了角色无法同时处于燃烧和冰冷状态，符合物理常识。

---

### fx(boolean on)

**方法职责**：视觉表现。
调用 `target.sprite.add(CharSprite.State.CHILLED)`。该状态会在角色身上产生蓝色的拖尾或粒子。

## 8. 对外暴露能力
- `speedFactor()`: 被 `Char` 类的速度系统查询。

## 9. 运行机制与调用链
`Icecap.activate()` -> `Buff.affect(Chill.class)` -> `Chill.attachTo()` (移除 Burning) -> `Char.speed()` -> `Chill.speedFactor()` -> 计算最终动作耗时。

## 10. 资源、配置与国际化关联

### 本地化词条
- `actors.buffs.Chill.name`: 冰冷
- `actors.buffs.Chill.desc`: “你感到身体被冻僵了。剩余时长：%s。减速强度：%s%%。”

## 11. 使用示例

### 在代码中施加冰冷效果
```java
Buff.affect(target, Chill.class, Chill.DURATION);
```

## 12. 开发注意事项

### 动态减速特性
开发者应注意 `Chill` 与 `Slow` 的区别。`Slow` 通常是恒定的 50% 减速，而 `Chill` 的减速是动态的。在计算长动作（如休息）时，`Chill` 的影响会随回合数推进而减弱。

### 叠加逻辑
虽然 `cooldown()` 理论上可以超过 10，但 `speedFactor` 内部使用了 `Math.max(0.5f, ...)` 进行保底，因此即使时长叠加到 100 回合，最大减速也只能是 50%。

## 13. 修改建议与扩展点

### 引入结冰几率
可以修改 `act()` 方法，如果 `cooldown()` 超过一定阈值（如 15），则有概率将 `Chill` 转化为真正的 `Frost`（冻结）。

## 14. 事实核查清单

- [x] 是否分析了动态减速公式：是 (`1 - left*0.1`, 封顶 0.5)。
- [x] 是否说明了与 Burning 的互斥关系：是。
- [x] 是否解析了减速随时间衰减的特性：是。
- [x] 图像索引属性是否核对：是 (BuffIndicator.FROST)。
- [x] 示例代码是否正确：是。
