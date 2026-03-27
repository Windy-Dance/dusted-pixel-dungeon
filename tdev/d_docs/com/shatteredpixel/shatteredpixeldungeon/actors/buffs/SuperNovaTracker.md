# SuperNovaTracker 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/SuperNovaTracker.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| **类类型** | public class |
| **继承关系** | extends Buff |
| **代码行数** | 194 行 |
| **内部类** | NovaBombImmune, NovaVFX |

## 2. 文件职责说明

SuperNovaTracker 类实现超新星倒计时和爆炸区域追踪。它会在指定位置持续 10 回合显示警告、扩张危险范围，最终在可见区域内的每个非实心格触发 `Bomb.ConjuredBomb` 爆炸，并可选择让盟友免疫。

**核心职责**：
- 维护爆炸中心位置、楼层上下文和剩余回合 `turnsLeft`
- 用 `ShadowCaster` 计算当前危险范围
- 在倒计时阶段显示浮字、红色标记和光环特效
- 在结束时触发大范围连锁爆炸并破坏地形

## 3. 结构总览

```
SuperNovaTracker (extends Buff)
├── 字段
│   ├── pos: int
│   ├── depth: int
│   ├── branch: int
│   ├── turnsLeft: int
│   ├── harmsAllies: boolean
│   ├── fieldOfView: boolean[]
│   └── halo: NovaVFX
├── 常量
│   └── DIST: int = 8
├── 方法
│   ├── act(): boolean
│   ├── fx(boolean): void
│   ├── storeInBundle()/restoreFromBundle()
└── 内部类
    ├── NovaBombImmune extends FlavourBuff
    └── NovaVFX extends Halo
```

## 4. 继承与协作关系

### 继承关系图

```mermaid
classDiagram
    Buff <|-- SuperNovaTracker
    FlavourBuff <|-- NovaBombImmune
    Halo <|-- NovaVFX
    SuperNovaTracker +-- NovaBombImmune
    SuperNovaTracker +-- NovaVFX
```

### 协作关系

| 协作类 | 协作方式 |
|--------|----------|
| **Buff** | 父类，提供附着与回合调度 |
| **ShadowCaster** | 计算当前超新星影响范围 |
| **Bomb.ConjuredBomb** | 最终爆炸的实际伤害来源 |
| **TargetedCell** | 倒计时期间显示红色目标格 |
| **FloatingText** | 显示剩余回合警告 |
| **GameScene** | 添加特效、闪光与更新地图 |
| **Dungeon.level.destroy()** | 爆炸后破坏地形 |
| **Actor.chars()** | 遍历全部角色给盟友添加免疫 |
| **NovaBombImmune** | 在 `harmsAllies == false` 时给盟友免疫炸弹 |
| **Halo / Game** | `NovaVFX` 的动态光环效果 |

## 5. 字段与常量详解

### 实例字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `pos` | int | 超新星中心格 |
| `depth` | int | 创建时所在楼层深度 |
| `branch` | int | 创建时所在分支 |
| `turnsLeft` | int | 剩余回合，初始为 10 |
| `harmsAllies` | boolean | 是否伤害盟友，默认 `true` |
| `fieldOfView` | boolean[] | 当前超新星覆盖区域缓存 |
| `halo` | NovaVFX | 黄色动态光环特效 |

### 常量

| 常量 | 类型 | 值 | 说明 |
|------|------|----|------|
| `DIST` | int | `8` | 爆炸范围最大半径 |

### Bundle 键

| 常量 | 值 | 用途 |
|------|-----|------|
| `POS` | `pos` | 保存中心格 |
| `DEPTH` | `depth` | 保存楼层 |
| `BRANCH` | `branch` | 保存分支 |
| `LEFT` | `left` | 保存剩余回合 |
| `HARMS_ALLIES` | `harms_allies` | 保存盟友是否受伤 |

## 6. 构造与初始化机制

SuperNovaTracker 没有显式构造函数。实例创建时：
- `depth`、`branch` 默认取当前 `Dungeon` 值
- `turnsLeft` 默认 10
- `harmsAllies` 默认 `true`

## 7. 方法详解

### act()

执行流程：
1. 若当前楼层分支或深度与记录值不一致，只 `spend(TICK)` 并返回。
2. 若 `fieldOfView == null`，初始化与楼层长度相同的布尔数组。
3. 若 `halo == null`，创建并注册 `NovaVFX`。
4. 当 `turnsLeft > 0`：
   - 显示 `turnsLeft + "..."` 警告浮字
   - 动态更新光环半径和透明度
5. 用：

```java
ShadowCaster.castShadow(..., Math.min(DIST, 11-turnsLeft))
```

计算当前危险区域。
6. 若 `turnsLeft <= 0`：
   - 移除 Buff 与光环
   - 若 `!harmsAllies`，给所有盟友附加 `NovaBombImmune`
   - 连续播放三次爆炸音效并震屏
   - 对每个 `fieldOfView[i] && !solid[i]` 的格：
     - `new Bomb.ConjuredBomb().explode(i)`
     - `Dungeon.level.destroy(i)`
     - 若该格是英雄，闪白屏
   - `GameScene.updateMap()`
7. 若 `turnsLeft > 0`：
   - 为所有危险格添加 `TargetedCell(i, 0xFF0000)`
8. 最后 `turnsLeft--` 并 `spend(TICK)`。

### fx(boolean on)

当 Buff 启用且当前仍在原楼层时，如 `halo` 不存在，则重建可视光环。

### 内部类 NovaBombImmune

这是一个极简 `FlavourBuff`，初始化块里只做：

```java
immunities.add(Bomb.ConjuredBomb.class);
```

### 内部类 NovaVFX

继承 `Halo`，在 `update()` 中通过 `cos(20*Game.timeTotal)` 同步调整亮度和缩放，并持续跟随 `pos`。

## 8. 对外暴露能力

| 方法/成员 | 用途 |
|-----------|------|
| `pos` | 指定超新星中心格 |
| `harmsAllies` | 控制盟友是否在爆炸中受伤 |
| `NovaBombImmune` | 供盟友免疫 ConjuredBomb 使用 |

## 9. 运行机制与调用链

```
SuperNovaTracker.act()
├── 校验是否仍在原楼层
├── 维护 halo 和 fieldOfView
├── [turnsLeft > 0] 显示倒计时与 TargetedCell
└── [turnsLeft <= 0] 对范围内每格引爆 ConjuredBomb 并 destroy 地形
```

## 10. 资源、配置与国际化关联

SuperNovaTracker 本类没有在 `actors_zh.properties` 中定义独立名称或描述文本。它主要通过场景特效、倒计时浮字和爆炸表现工作。

## 11. 使用示例

```java
SuperNovaTracker tracker = Buff.affect(hero, SuperNovaTracker.class);
tracker.pos = targetCell;
tracker.harmsAllies = false;
```

## 12. 开发注意事项

- 这个 Buff 会在一个范围内对每个非实心格单独引爆 `ConjuredBomb`，不是单次范围伤害。
- 它会跨回合缓存 `fieldOfView` 和 `halo`，读档和楼层切换时都要注意这些状态的重建条件。
- `harmsAllies == false` 时不是跳过爆炸，而是先给盟友上免疫 Buff。

## 13. 修改建议与扩展点

- 若后续需要不同爆炸半径或倒计时长度，可把 `DIST` 和 `turnsLeft` 初始值参数化。
- 若要减少 `act()` 复杂度，可把“倒计时阶段”和“爆炸阶段”拆成单独私有方法。

## 14. 事实核查清单

- [x] 已覆盖全部字段、方法与内部类
- [x] 已验证继承关系 `extends Buff`
- [x] 已验证楼层切换暂停逻辑
- [x] 已验证 `fieldOfView` 计算与 `TargetedCell` 警告显示
- [x] 已验证 `harmsAllies` 与 `NovaBombImmune` 联动
- [x] 已验证最终逐格 `ConjuredBomb` 爆炸和地形破坏
- [x] 已验证 `Bundle` 存档字段
- [x] 已说明无独立翻译键这一事实
- [x] 无臆测性机制说明
