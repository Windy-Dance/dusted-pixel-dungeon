# Bless (祝福) 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Bless.java` |
| **包名** | `com.shatteredpixel.shatteredpixeldungeon.actors.buffs` |
| **文件类型** | class |
| **继承关系** | `extends FlavourBuff` |
| **代码行数** | 38 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`Bless` 负责实现角色的“祝福”状态逻辑。它通过提升角色的基础战斗属性（命中率和闪避率），增强角色的整体攻防能力。

### 系统定位
属于 Buff 系统中的数值增强分支。它是玩家通过星辰花（Starflower）、特定祭坛、或神圣法术获得的核心正面状态。

### 不负责什么
- 不负责具体的命中/闪避加成数值计算（由 `Char.attackSkill()` 和 `Char.defenseSkill()` 内部检查此 Buff 存在后应用倍率）。
- 不负责回血或护盾加成（由 `Regeneration` 或 `ShieldBuff` 负责）。

## 3. 结构总览

### 主要成员概览
- **常量 DURATION**: 默认持续时间（30 回合）。
- **icon() 方法**: 指定使用的 UI 图标索引。
- **iconFadePercent() 方法**: 控制图标随时间的淡化反馈。

### 主要逻辑块概览
- **风味化继承**: 继承自 `FlavourBuff`，作为一个标记性 Buff，其逻辑主要存在于它被其他战斗方法引用的时刻。
- **属性定义**: 明确标记为 `POSITIVE`（正面效果）且 `announced = true`（获得时在日志中提示）。

### 生命周期/调用时机
1. **产生**：踩踏星辰花、接受祭坛祝福、使用特定卷轴。
2. **活跃期**：角色在攻击和防御判定中获得显著优势。
3. **结束**：持续时间结束。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `FlavourBuff`：
- 提供 `left` 时间管理。
- 提供基于时间的描述文本。

### 协作对象
- **Char / Hero / Mob**: 目标角色。在战斗判定逻辑中会检查 `Bless.class`。
- **BuffIndicator.BLESS**: 提供太阳或圣光样式的祝福图标。
- **Starflower**: 产生此效果的主要植物来源。

```mermaid
graph LR
    Buff --> FlavourBuff
    FlavourBuff --> Bless
    Bless -->|Checked by| AttackLogic[Char.attackSkill()]
    Bless -->|Checked by| DefenseLogic[Char.defenseSkill()]
    AttackLogic -->|Boosts| Accuracy
    DefenseLogic -->|Boosts| Evasion
    Bless -->|UI| BuffIndicator[BLESS Icon]
```

## 5. 字段/常量详解

### 静态常量
- **DURATION**: 30.0f 回合。祝福通常是一个中长期持续的稳定增益。

## 6. 构造与初始化机制
通过实例初始化块设置 `type = POSITIVE` 和 `announced = true`。

## 7. 方法详解

### icon()

**方法职责**：定义 UI 图标。
返回 `BuffIndicator.BLESS`。

---

### iconFadePercent()

**方法职责**：进度反馈。
基于 `DURATION` 计算剩余时间占比，用于进度环显示。

## 8. 对外暴露能力
主要通过 `Buff.prolong(target, Bless.class, duration)` 等静态方法应用。

## 9. 运行机制与调用链
`Starflower.activate()` -> `Buff.prolong(Bless.class)` -> `Char.attackSkill()` 检查 Buff -> 增加随机 ROLL 值的上限 -> 提升命中率。

## 10. 资源、配置与国际化关联

### 本地化词条
- `actors.buffs.Bless.name`: 祝福
- `actors.buffs.Bless.desc`: “你感到一股神圣的力量在引导你的行动。剩余时长：%s。”

## 11. 使用示例

### 在代码中施加祝福
```java
Buff.prolong(hero, Bless.class, Bless.DURATION);
```

## 12. 开发注意事项

### 属性影响实现
`Bless` 类本身不包含“增加 20% 命中”的代码。具体的数值加成在 `Char.java` 或其具体实现的技能方法中。例如：
```java
// 典型的战斗逻辑代码示例
if (buff(Bless.class) != null) {
    skill *= 1.4f; // 实际倍率以 Char 类源码为准
}
```

### 与 Cursed 的潜在冲突
虽然源码中未显式互斥，但在设计上祝福往往作为诅咒的对立面。在特定关卡或 Mod 开发中，可以增加逻辑使两者相互抵消。

## 13. 修改建议与扩展点

### 增加伤害保底
可以修改逻辑，使拥有祝福状态的角色在攻击未命中时，仍有极小概率造成 1 点“擦伤”伤害。

## 14. 事实核查清单

- [x] 是否分析了默认持续时间：是 (30 回合)。
- [x] 是否解析了作为 FlavourBuff 的特征：是。
- [x] 是否明确了它对命中和闪避的逻辑影响位置：是（解耦在战斗类中）。
- [x] 图像索引属性是否核对：是 (BuffIndicator.BLESS)。
- [x] 示例代码是否正确：是。
