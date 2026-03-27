# GuardianTrap (守卫陷阱) 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/GuardianTrap.java` |
| **包名** | `com.shatteredpixel.shatteredpixeldungeon.levels.traps` |
| **文件类型** | class / inner class |
| **继承关系** | `extends Trap` |
| **代码行数** | 102 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`GuardianTrap` 负责实现“守卫陷阱”的逻辑。它是警报陷阱的极位强化变体，不仅会吸引全层的怪物，还会在地牢深处额外召唤出特殊的、具有物理体积的“蓝色守卫（Guardian）”雕像来围攻玩家。

### 系统定位
属于陷阱系统中的战斗/防御分支。它结合了“信息引诱”和“实体召唤”双重威胁，通常出现在游戏的中后期关卡。

### 不负责什么
- 不负责守卫雕像的攻击结算（由 `Statue` 父类负责）。
- 不负责计算守卫掉落（守卫被显式设为 0 经验且不产生正常掉落）。

## 3. 结构总览

### 主要成员概览
- **activate() 方法**: 包含全层召唤逻辑和基于深度的守卫生成逻辑。
- **Guardian 内部类**: 继承自 `Statue`，定义了守卫的特殊属性（蓝色外观、无经验、基础武器）。
- **GuardianSprite 内部类**: 负责守卫的视觉呈现（应用蓝色滤镜）。

### 主要逻辑块概览
- **全层召集**: 遍历并对所有怪物执行 `beckon(pos)`。
- **动态守卫生成**: 根据 `(scalingDepth() - 5) / 5` 的公式计算召唤守卫的数量。
- **守卫武器系统**: 为每个守卫随机分发一件基础的、非诅咒、非附魔的近战武器。
- **英雄追踪**: 新生成的守卫会立即被“指引（beckon）”向英雄当前所在的位置。

### 生命周期/调用时机
1. **触发**：角色踩踏。
2. **激活 (`activate`)**:
   - 播放全层警报。
   - 所有存活怪物向陷阱聚拢。
   - 在关卡随机位置产生 0-4 个蓝色守卫并向英雄合围。

## 4. 继承与协作关系

### 父类提供的能力
继承自 `Trap`：
- 提供基础属性和 `trigger` 流程。
- 定义外观为 `RED`（红色）和 `STARS`（星形）。

### 协作对象
- **Statue**: 守卫实体的父类，提供基础的石像鬼逻辑。
- **Mob**: 接收 `beckon` 指令。
- **Generator**: 为生成的守卫随机创建武器。
- **GameScene**: 负责将新守卫加入渲染树。
- **Speck.SCREAM**: 提供视觉预警。

```mermaid
graph TD
    Trap --> GuardianTrap
    GuardianTrap -->|Iterates| Mobs[Dungeon.level.mobs]
    GuardianTrap -->|Calculates count| Math[Depth Formula]
    Math -->|Spawns| Guardian[Guardian NPC]
    Guardian --|> Statue
    Guardian -->|Follows| Hero[Dungeon.hero]
```

## 5. 字段/常量详解

### 初始属性
- **color**: RED（红色）。
- **shape**: STARS（星形，代表高危/复杂效果）。

## 6. 构造与初始化机制
通过实例初始化块配置外观。守卫实体的生成是在 `activate` 时动态完成的。

## 7. 方法详解

### activate() [警报与召唤逻辑]

**核心实现算法分析**：
1. **全员广播**：同警报陷阱，强制所有怪物移动向陷阱位置。
2. **计算守卫数量**：
   ```java
   int count = (scalingDepth() - 5) / 5;
   ```
   **分析**：
   - **第 1-9 层**: `count <= 0`，不产生守卫。
   - **第 10-14 层**: 产生 **1** 个守卫。
   - **第 15-19 层**: 产生 **2** 个守卫。
   - **第 20-24 层**: 产生 **3** 个守卫。
   - **第 25 层+**: 产生 **4** 个守卫。
3. **初始化守卫行为**：
   守卫在随机位置产生后，调用 `guardian.beckon(Dungeon.hero.pos)`。这意味着守卫是唯一一种能直接追踪英雄位置的召唤物。

---

### Guardian.createWeapon() [武器定制逻辑]

**代码逻辑**：
```java
weapon = (MeleeWeapon) Generator.randomUsingDefaults(Generator.Category.WEAPON);
weapon.cursed = false;
weapon.enchant(null);
weapon.level(0);
```
**分析**：不同于天然雕像可能携带强力附魔武器，守卫的武器被强制限制在 **0 级、无附魔、无诅咒**。这确保了召唤出的守卫不会因为携带“极品武器”而导致战斗难度瞬间失控。

## 8. 对外暴露能力
主要通过 `activate()` 接口。

## 9. 运行机制与调用链
`Trap.trigger()` -> `GuardianTrap.activate()` -> `Dungeon.level.mobs.beckon()` -> `new Guardian()` -> `Guardian.beckon(hero)`。

## 10. 资源、配置与国际化关联
- **视觉特征**: 守卫拥有 `tint(0, 0, 1, 0.2f)` 蓝色半透明效果，以便与普通雕像区分。

## 11. 使用示例

### 死亡走廊
在第 20 层之后的狭窄走廊触发守卫陷阱。不仅全层怪物会包抄过来，3 个守卫石像的刷新也可能直接堵死逃生路径。

## 12. 开发注意事项

### 资源清理
守卫石像被显式设为 `EXP = 0`。这是为了防止玩家通过“刷守卫陷阱”来无限制获取经验值。

### 状态冲突
守卫石像的 `beckon` 方法经过特殊处理，覆盖了父类的限制，使其在游荡状态下依然能响应位置指引。

## 13. 修改建议与扩展点

### 改进守卫种类
可以根据地牢环境的不同（如下水道、矿区、机械层），召唤具有不同外观和属性的守卫（如水元素守卫、机械护卫等）。

## 14. 事实核查清单

- [x] 是否分析了守卫生成的数量公式：是 (`(depth-5)/5`)。
- [x] 是否解析了守卫武器的属性限制：是 (0级白板)。
- [x] 是否说明了全层警报的逻辑：是。
- [x] 是否涵盖了守卫对英雄的追踪性：是 (beckon hero)。
- [x] 图像索引属性是否核对：是 (RED, STARS)。
