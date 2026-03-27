# Sungrass (太阳草) 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/plants/Sungrass.java` |
| **包名** | `com.shatteredpixel.shatteredpixeldungeon.plants` |
| **文件类型** | class |
| **继承关系** | `extends Plant` |
| **代码行数** | 128 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`Sungrass` 负责实现“太阳草”植物及其种子的逻辑。它提供一种强大的、非即时性的治疗效果，角色在停留期间可以获得持续的生命恢复。

### 系统定位
属于植物系统中的医疗分支。它与 `Buff` 系统（特别是内部类 `Health`）紧密协作，是游戏中除了治疗药水外最可靠的回复手段之一。

### 不负责什么
- 不负责瞬时治疗（其效果是随回合流逝的）。
- 不负责治疗过程中的饱腹感影响（不提供食物值）。

## 3. 结构总览

### 主要成员概览
- **Sungrass 类**: 植物实体类，实现触发和激活。
- **Seed 类**: 种子物品类，支持种植和遗物（bones）机制。
- **Health 内部类**: 继承自 `Buff`，实现了基于位置的持续治疗逻辑。

### 主要逻辑块概览
- **激活逻辑 (`activate`)**: 为普通角色应用 `Health` 增益，或为守林人应用瞬发增强的 `Healing` 增益。
- **治疗公式 (`Health.act`)**: 基于角色最大生命值（HT）动态计算每回合的回血量。
- **打断逻辑**: 如果角色移动，治疗效果立即中断。

### 生命周期/调用时机
1. **生成**：由种子种植或地图生成。
2. **触发**：角色踩踏时触发。
3. **活跃期**：角色在原位休整或等待，`Health` 持续扣除池化生命值（level）并转化为角色 HP。
4. **失效**：角色 HP 满值、治疗池耗尽或角色移动。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `Plant`：
- 提供基础的 `trigger()` 处理流程和图像索引（3）。

### 实现的接口契约
- **Bundlable**: `Health` 类支持完整序列化，确保存档后治疗进度不丢失。

### 协作对象
- **Healing**: 守林人触发时使用的标准回复 Buff。
- **CellEmitter / ShaftParticle**: 提供柔和的圣光柱粒子特效。
- **Hero**: 处理 `resting` 状态的重置。

```mermaid
graph TD
    Plant --> Sungrass
    Sungrass *-- Seed
    Sungrass *-- Health
    Health --|> Buff
    Health -->|Regens| Char[Char.HP]
```

## 5. 字段/常量详解

### Sungrass 字段
- **image**: 3（太阳草的图集索引）。

### Health 字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| `pos` | int | 获得增益时的格子，必须保持在此格子才有治疗 |
| `level` | int | 治疗池总量（初始为角色的 HT），随治疗过程消耗 |
| `partialHeal` | float | 累计的浮点治疗量，满 1.0 时转化为 1 点 HP |

## 6. 构造与初始化机制

### Sungrass 初始化
初始化块中设置 `image = 3`。

### Health 初始化
- `type = POSITIVE`: 标记为增益。
- `announced = true`: 在日志中显示获得。

## 7. 方法详解

### activate(Char ch)

**方法职责**：定义激活效果。

**核心逻辑**：
1. **守林人处理**：调用 `Buff.affect(ch, Healing.class).setHeal(ch.HT, 0, 1)`。这为守林人提供一个不限制位置、且速度极快的治疗效果。
2. **普通处理**：应用 `Sungrass.Health` 并调用 `boost(ch.HT)` 填充治疗池。
3. **特效**：在 FOV 内产生 `ShaftParticle`（向上升起的金色光轴）。

---

### Health.act() [关键治疗算法]

**算法职责**：每回合执行回复。

**核心代码分析**：
1. **位置检查**：`if (target.pos != pos) detach();` 确保角色留在原地。
2. **治疗速率计算**：
   ```java
   partialHeal += (40 + target.HT)/150f;
   ```
   - **等级 1 角色** (约 20 HP): 每回合回复约 0.4 HP，全满约需 50 回合。
   - **等级 30 角色** (约 HT 140+): 回复速度随最大生命值增加而加快，约需 120 回合。
3. **溢出处理**：如果目标生命已满，将 `Hero.resting` 设为 `false`（停止自动休息）。
4. **池化消耗**：`level` 随 `healThisTurn` 扣除，归零时 `detach`。

## 8. 对外暴露能力

### 显式 API
- `Sungrass.activate(Char)`: 激活入口。
- `Health.boost(int)`: 增加治疗池总量。

## 9. 运行机制与调用链
`Plant.trigger()` -> `Sungrass.activate()` -> `Health.act()` (逐回合触发) -> `target.HP += healThisTurn`。

## 10. 资源、配置与国际化关联

### 本地化
- `actors.buffs.Sungrass$Health.name`: 太阳草治疗
- `actors.buffs.Sungrass$Health.desc`: “处于太阳草的愈合力笼罩中。当前治疗池：%d。”

## 11. 使用示例

### 强制给予一个太阳草治疗（不限位置）
由于其设计中 `act` 检查 `pos`，若要模拟不受限的治疗，需手动设置 `pos = target.pos`：
```java
Sungrass.Health h = Buff.affect(hero, Sungrass.Health.class);
h.boost(20);
```

## 12. 开发注意事项

### 自动休息兼容
`Health` 逻辑会修改 `Hero.resting` 状态。这确保了玩家在使用“睡眠（Wait）”功能通过太阳草回复时，生命值满额会自动停止等待，防止浪费回合。

### 治疗池机制
`level` 字段在这里不是“等级”，而是“剩余可治疗的生命值总点数”。

## 13. 修改建议与扩展点

### 调整回复速度
如果觉得回复太慢，可以修改 `150f` 这个除数。减小该值将加快每回合的治疗量。

### 增加解毒效果
可以在 `activate` 中增加逻辑，使太阳草在治疗的同时缓慢移除 `Poison` 或 `Bleeding` Buff。

## 14. 事实核查清单

- [x] 是否分析了治疗速度公式：是 (`(40+HT)/150f`)。
- [x] 是否对比了守林人的优势：是（移动式增强 Healing）。
- [x] 是否提到了对 Hero.resting 的控制：是。
- [x] 是否说明了位置锁定的实现：是。
- [x] 图像索引是否核对：是 (3)。
