# Blindness (失明) 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Blindness.java` |
| **包名** | `com.shatteredpixel.shatteredpixeldungeon.actors.buffs` |
| **文件类型** | class |
| **继承关系** | `extends FlavourBuff` |
| **代码行数** | 43 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`Blindness` 负责实现角色的“失明”状态逻辑。它通过控制角色的视野属性，使目标的视距缩减到极小（通常为 1 格），从而剥夺其远程攻击能力和对周围环境的感知。

### 系统定位
属于 Buff 系统中的感知干扰分支。它是应对潜行、致盲投射物或黑暗陷阱的核心负面状态。

### 不负责什么
- 不负责具体的视野半径计算（由 `Char.viewDistance()` 结合此 Buff 存在来实现）。
- 不负责阻止角色读取地图（虽然语义上失明不能看地图，但逻辑由 `Window` 或其他交互类判定）。

## 3. 结构总览

### 主要成员概览
- **常量 DURATION**: 默认持续时间（10 回合）。
- **detach() 方法**: 覆写移除逻辑，包含地图视野的强制刷新。
- **icon() 方法**: 定义 UI 图标索引。

### 主要逻辑块概览
- **风味化继承**: 继承自 `FlavourBuff`，主要管理持续时间。
- **视野联动**: 该 Buff 的核心逻辑并非在自身 `act()` 中，而是在 `detach()` 时触发 `Dungeon.observe()`，确保角色重获光明时立即更新迷雾。

### 生命周期/调用时机
1. **产生**：被致盲草（Blindweed）、闪光弹、特定的致盲攻击击中。
2. **活跃期**：角色视野降至最低。
3. **结束**：持续时间结束，调用 `detach()` 并刷新视野。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `FlavourBuff`：
- 提供 `left` 时间计数。
- 提供自动生成的 UI 描述文本。

### 协作对象
- **Char**: 目标角色。其 `viewDistance()` 会根据是否存在此 Buff 返回 1。
- **Dungeon**: 提供 `observe()` 静态方法，用于在状态解除时刷新迷雾。
- **BuffIndicator.BLINDNESS**: 提供眼睛被遮住形状的图标。

```mermaid
graph LR
    Buff --> FlavourBuff
    FlavourBuff --> Blindness
    Blindness -->|Triggers on Detach| Dungeon[Dungeon.observe()]
    Blindness -->|Checked by| CharVision[Char.viewDistance() Logic]
```

## 5. 字段/常量详解

### 静态常量
- **DURATION**: 10.0f 回合。

## 6. 构造与初始化机制
通过实例初始化块设置 `type = NEGATIVE` 和 `announced = true`。

## 7. 方法详解

### detach() [视野刷新逻辑]

**可见性**：public (Override)

**核心实现分析**：
```java
@Override
public void detach() {
    super.detach();
    Dungeon.observe();
}
```
**技术要点**：
这是 `Blindness` 类中最关键的逻辑。在 Shattered PD 的渲染流程中，迷雾（Fog of War）是持久化的。如果不显式调用 `Dungeon.observe()`，当 Buff 消失时，屏幕可能依然保持全黑，直到角色下一次移动。调用此方法确保了“复明”瞬间视野的实时更新。

---

### icon()

**方法职责**：返回图标。
返回 `BuffIndicator.BLINDNESS`。

---

### iconFadePercent()

**方法职责**：图标淡化。
基于剩余时长动态淡化图标，提示玩家致盲效果即将消失。

## 8. 对外暴露能力
主要通过 `Buff.affect(target, Blindness.class)` 进行应用。

## 9. 运行机制与调用链
`Blindweed.activate()` -> `Buff.affect(Blindness.class)` -> `Char.viewDistance()` 返回 1 -> 界面变黑 -> 时间耗尽 -> `detach()` -> `Dungeon.observe()` -> 视野恢复。

## 10. 资源、配置与国际化关联

### 本地化词条
- `actors.buffs.Blindness.name`: 失明
- `actors.buffs.Blindness.desc`: “你的双眼看不见了！剩余时长：%s。”

## 11. 使用示例

### 在代码中施加失明
```java
Buff.affect(hero, Blindness.class, 5f); // 致盲 5 回合
```

## 12. 开发注意事项

### 视距硬编码
在 `Char.java` 或其具体实现中，致盲导致的视距通常被强制设为 **1**。这意味着角色只能看见相邻的 8 格。

### 移动影响
对于英雄来说，失明状态下点击远处的地板通常无法直接自动寻路，因为目标点在视野之外。

## 13. 修改建议与扩展点

### 增加感知加成
可以修改逻辑，使拥有“心眼”或“嗅觉强化”属性的角色在失明状态下仍能保留一定的感知范围。

## 14. 事实核查清单

- [x] 是否分析了 detach 时的視野重算：是 (Dungeon.observe)。
- [x] 是否解析了默认持续时间：是 (10 回合)。
- [x] 是否说明了它对视野的具体限制：是 (通常降至 1 格)。
- [x] 是否明确了它作为 FlavourBuff 的特征：是。
- [x] 图像索引属性是否核对：是 (BuffIndicator.BLINDNESS)。
