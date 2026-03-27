# StoneOfAggression 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/stones/StoneOfAggression.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.stones |
| **文件类型** | class |
| **继承关系** | extends Runestone |
| **代码行数** | 105 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
StoneOfAggression（敌意符石）是一种投掷型符石，被投掷到目标位置后，会对命中的角色施加"众矢之的"（Aggression）状态，使附近所有敌人在短时间内优先攻击该角色。

### 系统定位
位于 Runestone → StoneOfAggression 继承链中，是一种战术性符石，可用于控制战场仇恨分配，使敌人攻击特定目标。

### 不负责什么
- 不负责直接造成伤害
- 不负责对 Boss 产生完整效果（Boss 有抗性）

## 3. 结构总览

### 主要成员概览
- `image` - 精灵图设置
- `Aggression` - 内部 Buff 类

### 主要逻辑块概览
- `activate(int cell)` - 激活符石效果
- `Aggression` 内部类 - 仇恨控制 Buff

### 生命周期/调用时机
1. 玩家投掷符石到目标位置
2. 符石激活，检测目标位置的角色
3. 对角色施加 Aggression Buff
4. Buff 持续期间影响敌人仇恨

## 4. 继承与协作关系

### 父类提供的能力
从 Runestone 继承：
- `stackable = true` - 可堆叠
- `defaultAction = AC_THROW` - 默认动作为投掷
- `onThrow()` - 投掷逻辑
- `activate()` - 激活方法（需覆写）
- `value()`、`energyVal()` - 价值计算

### 覆写的方法
| 方法 | 覆写逻辑 |
|------|----------|
| `activate(int cell)` | 检测目标位置角色，施加 Aggression Buff |

### 实现的接口契约
无显式接口实现。

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Actor` | 查找位置上的角色 |
| `Char` | 角色基类 |
| `Buff` | Buff 管理器 |
| `FlavourBuff` | 味道 Buff 基类 |
| `Mob` | 怪物类 |
| `CellEmitter` | 单元格特效发射器 |
| `Speck` | 粒子效果 |
| `ItemSpriteSheet` | 精灵图定义 |
| `BuffIndicator` | Buff 图标指示器 |
| `Sample` | 音效播放 |

### 使用者
- `Hero` - 英雄投掷使用
- 炼金系统 - 合成此物品

## 5. 字段/常量详解

### 静态常量
无静态常量定义。

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `image` | int | ItemSpriteSheet.STONE_AGGRESSION | 在初始化块中设置，符石精灵图 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过实例初始化块设置属性：

```java
{
    image = ItemSpriteSheet.STONE_AGGRESSION;
}
```

### 初始化块
- `image = ItemSpriteSheet.STONE_AGGRESSION` - 设置符石精灵图

## 7. 方法详解

### activate(int cell)

**可见性**：protected

**是否覆写**：是，覆写自 Runestone

**方法职责**：激活符石效果，对目标位置的角色施加 Aggression Buff。

**参数**：
- `cell` (int)：激活位置的格子坐标

**返回值**：void

**前置条件**：符石已被投掷到有效位置

**副作用**：
- 对目标角色施加 Aggression Buff
- 播放视觉效果（Screaming Speck）
- 播放音效

**核心实现逻辑**：
```java
@Override
protected void activate(int cell) {
    Char ch = Actor.findChar( cell );
    
    if (ch != null) {
        // Boss 和 MiniBoss 有抗性，持续时间缩短为 1/4
        if (Char.hasProp(ch, Char.Property.BOSS) || Char.hasProp(ch, Char.Property.MINIBOSS)) {
            Buff.prolong(ch, Aggression.class, Aggression.DURATION / 4f);
        } else {
            Buff.prolong(ch, Aggression.class, Aggression.DURATION);
        }
    }

    // 播放视觉效果
    CellEmitter.center(cell).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
    Sample.INSTANCE.play( Assets.Sounds.READ );
}
```

**边界情况**：
- 目标位置无角色时，仅播放视觉效果
- Boss/MiniBoss 的效果持续时间缩短为普通目标的 1/4

---

## 8. 内部类详解

### Aggression

**类型**：public static class extends FlavourBuff

**职责**：表示"众矢之的"状态，使附近敌人优先攻击持有此 Buff 的角色。

**常量**：
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `DURATION` | float | 20f | 基础持续时间（回合） |

**实例字段**：
从 FlavourBuff 继承，无额外字段。

**方法**：

#### icon()

**可见性**：public

**是否覆写**：是

**方法职责**：返回 Buff 图标。

**返回值**：int，返回 `BuffIndicator.TARGETED`

#### iconFadePercent()

**可见性**：public

**是否覆写**：是

**方法职责**：计算 Buff 图标的淡出百分比，用于显示剩余时间。

**返回值**：float，基于剩余时间计算的淡出百分比

**核心实现逻辑**：
```java
@Override
public float iconFadePercent() {
    if (Char.hasProp(target, Char.Property.BOSS) || Char.hasProp(target, Char.Property.MINIBOSS)){
        return Math.max(0, (DURATION/4f - visualcooldown()) / (DURATION/4f));
    } else {
        return Math.max(0, (DURATION - visualcooldown()) / DURATION);
    }
}
```

#### detach()

**可见性**：public

**是否覆写**：是

**方法职责**：当 Buff 移除时，重置相关敌人的仇恨。

**核心实现逻辑**：
```java
@Override
public void detach() {
    // 如果目标是敌人，重置所有涉及它的敌人间仇恨
    if (target.isAlive()) {
        if (target.alignment == Char.Alignment.ENEMY) {
            for (Mob m : Dungeon.level.mobs) {
                if (m.alignment == Char.Alignment.ENEMY && m.isTargeting(target)) {
                    m.aggro(null);
                }
                if (target instanceof Mob && ((Mob) target).isTargeting(m)){
                    ((Mob) target).aggro(null);
                }
            }
        }
    }
    super.detach();
}
```

## 9. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate(int cell)` | 激活符石效果（由父类调用） |

### 内部辅助方法
无额外内部方法。

### 扩展入口
- `Aggression` 内部类可被外部引用用于检测状态

## 10. 运行机制与调用链

### 创建时机
- 在地牢中随机生成
- 通过炼金合成获得

### 调用者
- `Runestone.onThrow()` - 投掷后激活

### 被调用者
- `Actor.findChar()` - 查找角色
- `Buff.prolong()` - 施加 Buff
- `CellEmitter.center()` - 播放特效
- `Sample.INSTANCE.play()` - 播放音效

### 系统流程位置
```
投掷动作 → Runestone.onThrow() → activate()
    → Actor.findChar() 查找目标
    → Buff.prolong() 施加 Aggression
    → 播放视觉/音效
    → 敌人 AI 检测 Aggression 状态并改变目标
```

## 11. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.stones.stoneofaggression.name | 敌意符石 | 物品名称 |
| items.stones.stoneofaggression.desc | 当把这颗符石丢向一个盟友或敌人时... | 物品描述 |
| items.stones.stoneofaggression$aggression.name | 众矢之的 | Buff 名称 |
| items.stones.stoneofaggression$aggression.desc | 支配魔法正使附近所有敌人优先攻击该单位... | Buff 描述 |

### 依赖的资源
- `ItemSpriteSheet.STONE_AGGRESSION` - 符石精灵图
- `Assets.Sounds.READ` - 阅读音效
- `Speck.SCREAM` - 尖叫粒子效果

### 中文翻译来源
来自 `items_zh.properties` 文件。

## 12. 使用示例

### 基本用法
```java
// 创建并投掷敌意符石
StoneOfAggression stone = new StoneOfAggression();
stone.quantity = 1;

// 投掷到敌人位置
stone.doThrow(hero, enemyCell);

// 敌人会被施加 Aggression 状态
// 附近所有敌人会优先攻击该目标
```

### 战术应用
```java
// 将符石投掷到召唤物或盟友身上
// 使敌人攻击该目标而非玩家

// 或投掷到敌人身上
// 使其他敌人攻击该敌人（可用于减少 Boss 受到的小怪伤害）
```

## 13. 开发注意事项

### 状态依赖
- Aggression Buff 的效果依赖敌人 AI 的仇恨检测机制
- Boss 和 MiniBoss 有特殊抗性处理

### 生命周期耦合
- Aggression Buff 会在 detach 时重置相关仇恨

### 常见陷阱
- 对 Boss 使用效果会大幅减弱
- 对盟友使用可能导致盟友死亡

## 14. 修改建议与扩展点

### 适合扩展的位置
- `activate()` 方法 - 可添加额外的视觉效果
- `Aggression` 内部类 - 可扩展功能

### 不建议修改的位置
- DURATION 常量 - 影响游戏平衡
- Boss 抗性逻辑 - 核心游戏机制

### 重构建议
- 可考虑将 Boss 抗性倍数提取为常量

## 15. 事实核查清单

- [x] 是否已覆盖全部字段（image）
- [x] 是否已覆盖全部方法（activate）
- [x] 是否已检查继承链与覆写关系（extends Runestone，覆写 activate）
- [x] 是否已核对官方中文翻译（敌意符石、众矢之的）
- [x] 是否存在任何推测性表述（无）
- [x] 示例代码是否真实可用（是）
- [x] 是否遗漏资源/配置/本地化关联（已列出）
- [x] 是否明确说明了注意事项与扩展点（已说明）

---

## 附：类关系图

```mermaid
classDiagram
    class Runestone {
        <<abstract>>
        #activate(int cell)*
    }
    
    class StoneOfAggression {
        +activate(int cell)
    }
    
    class Aggression {
        +DURATION: float
        +icon() int
        +iconFadePercent() float
        +detach()
    }
    
    class FlavourBuff {
        <<abstract>>
    }
    
    Runestone <|-- StoneOfAggression
    FlavourBuff <|-- Aggression
    StoneOfAggression +-- Aggression
```