# Doom (定命状态效果)

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **类名** | `Doom` |
| **包路径** | `com.shatteredpixel.shatteredpixeldungeon.actors.buffs` |
| **继承关系** | `extends Buff` |
| **修饰符** | `public` |
| **源文件** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Doom.java` |
| **许可证** | GNU General Public License v3.0 |

---

## 类职责

`Doom`（定命）是一个永久性的负面状态效果，它通过大幅增加受到的伤害来宣告目标的"死刑"。作为 `Buff` 的简洁实现，它承担以下核心职责：

1. **伤害增幅标记**：标记目标为"被定命"状态，使其受到的所有伤害增加 67%
2. **视觉反馈**：通过精灵变暗效果展示定命状态
3. **永久性诅咒**：效果不会自然消退，只有死亡才能解除

### 设计理念

定命是一种"终极诅咒"机制——它不会直接造成伤害，而是让目标变得极度脆弱。这与腐化（Corruption）不同：腐化将敌人转化为盟友但会持续损耗其生命，而定命则让敌人保持敌对但变得不堪一击。这种设计形成了战术上的两难选择：快速击杀脆弱的敌人，还是获得一个（虽然会衰弱的）战斗帮手。

---

## 4. 继承与协作关系

```
                    ┌─────────────────┐
                    │     Actor       │
                    │  (抽象基类)      │
                    └────────┬────────┘
                             │ extends
                             ▼
                    ┌─────────────────┐
                    │      Buff       │
                    │  (状态效果基类)   │
                    └────────┬────────┘
                             │ extends
                             ▼
                    ┌─────────────────┐
                    │      Doom       │
                    │    (定命效果)    │
                    └─────────────────┘
```

### 依赖关系

```
Doom
    │
    ├─── CharSprite (精灵视觉效果)
    │       └── State.DARKENED 状态、add/remove 方法
    │
    ├─── BuffIndicator (状态图标指示器)
    │       └── CORRUPT 常量 (值=36)
    │
    └─── Char (目标角色 - 外部依赖)
            └── damage() 方法中检查 Doom 并增加伤害倍率
```

---

## 静态常量表

本类未定义静态常量。相关常量来自依赖类：

| 常量 | 来源类 | 值 | 用途 |
|------|--------|-----|------|
| `CORRUPT` | `BuffIndicator` | `36` | 状态图标索引（与 Corruption 共用） |
| `DARKENED` | `CharSprite.State` | 枚举值 | 精灵变暗状态 |

---

## 实例字段表

| 字段名 | 类型 | 访问修饰符 | 初始值 | 用途 |
|--------|------|------------|--------|------|
| `type` | `buffType` | `buffType.NEGATIVE` (实例初始化块) | 负面状态 | 标识此效果为负面状态（用于UI显示和效果判定） |
| `announced` | `boolean` | `true` (实例初始化块) | 公告状态 | 当效果施加时向玩家显示公告 |

### 5. 字段/常量详解

Doom 类本身未定义新字段，所有字段均继承自父类 `Buff` 或在实例初始化块中设置：

#### 继承字段（来自 Buff）

| 字段 | 类型 | 说明 |
|------|------|------|
| `target` | `Char` | 状态效果的目标角色 |
| `mnemonicExtended` | `boolean` | 是否被记忆祷言延长 |
| `revivePersists` | `boolean` | 复活后是否保留（默认 false） |
| `resistances` | `HashSet<Class>` | 抗性集合 |
| `immunities` | `HashSet<Class>` | 免疫集合 |

---

## 7. 方法详解

### 实例初始化块

```java
{
    type = buffType.NEGATIVE;
    announced = true;
}
```

**逐行解释**：
- **第29-32行**：实例初始化块，在构造函数之前执行
  - `type = buffType.NEGATIVE`：设置状态类型为负面。定命是一个诅咒效果，会大幅增加受到的伤害，对目标而言是不利的
  - `announced = true`：启用公告，当效果施加时会向玩家显示提示信息。这是一个重要的状态变化，需要通知玩家

---

### `fx()` - 视觉效果控制

```java
@Override
public void fx(boolean on) {
    if (on) target.sprite.add( CharSprite.State.DARKENED );
    else if (target.invisible == 0) target.sprite.remove( CharSprite.State.DARKENED );
}
```

**逐行解释**：
- **第34行**：`@Override` 注解，表示重写父类 `Buff` 的方法
- **第35行**：方法签名
  - `boolean on`：`true` 表示效果开始施加，`false` 表示效果被移除
- **第36行**：`if (on) target.sprite.add(CharSprite.State.DARKENED)`
  - 当定命效果施加时，为目标精灵添加变暗效果
  - `DARKENED` 状态使精灵显示为暗色调，视觉上表现"被诅咒"的状态
  - 这与 `Corruption` 腐化效果使用相同的视觉状态
- **第37行**：`else if (target.invisible == 0) target.sprite.remove(CharSprite.State.DARKENED)`
  - 当效果移除时，检查目标是否可见（`invisible == 0` 表示非隐身状态）
  - 仅在可见时移除变暗效果，避免与隐身效果冲突
  - 这防止隐身角色突然"显现"变暗效果，破坏隐身状态

**视觉效果**：被定命的角色精灵会呈现紫黑色暗色调，与正常角色形成明显区分。

**CharSprite 中的 DARKENED 处理**：
```java
// CharSprite.java 第425-428行 - 添加状态
case DARKENED:
    if (darkBlock != null) darkBlock.killAndErase();
    darkBlock = DarkBlock.darken(this);
    break;

// CharSprite.java 第528-532行 - 移除状态
case DARKENED:
    if (darkBlock != null) {
        darkBlock.lighten();
        darkBlock = null;
    }
    break;
```

---

### `icon()` - 状态图标

```java
@Override
public int icon() {
    return BuffIndicator.CORRUPT;
}
```

**逐行解释**：
- **第40行**：`@Override` 注解，重写父类 `Buff` 的方法
- **第41行**：方法签名，返回状态图标索引
- **第42行**：`return BuffIndicator.CORRUPT`
  - 返回腐化状态图标的索引值（36）
  - 注意：定命与腐化共用同一个图标，因为它们都是"黑暗诅咒"类的效果
  - 这种图标复用反映了两个效果在主题上的关联性

**图标位置**：在 `BuffIndicator` 类中定义：
```java
public static final int CORRUPT = 36;
```

---

## 伤害增幅机制（外部实现）

定命效果的核心机制——伤害增幅，实际上是在 `Char.damage()` 方法中实现的：

```java
// Char.java 第886-888行
if (this.buff(Doom.class) != null && !isImmune(Doom.class)){
    damage *= 1.67f;
}
```

**机制详解**：
1. **检查时机**：在计算最终伤害时检查
2. **检查条件**：目标身上有 Doom 效果 且 不免疫 Doom
3. **伤害增幅**：`damage *= 1.67f`，即伤害增加 67%
4. **位置**：此检查位于伤害计算链中，在其他增幅/减免效果之后

**伤害计算示例**：
```
原始伤害 = 10
定命增幅 = 10 × 1.67 = 16.7 ≈ 17 点伤害
```

---

## 11. 使用示例

### 基础用法：对敌人施加定命

```java
import com.dustedpixel.dustedpixeldungeon.actors.buffs.Doom;

import static com.dustedpixel.dustedpixeldungeon.actors.buffs.Buff.affect;

// 对敌人施加定命效果
Mob enemy = // 获取敌人实例

// 施加定命
        affect(enemy, Doom.class);
```

### 完整示例：腐化法杖的定命应用

```java
// WandOfCorruption.java 中的实现（简化版）
public void applyDoom(Mob enemy) {
    // 检查目标是否已被腐化或定命
    if (enemy.buff(Corruption.class) != null || enemy.buff(Doom.class) != null) {
        // 已有相关效果，不能再次施加
        return;
    }
    
    // 施加定命效果
    Buff.affect(enemy, Doom.class);
    
    // 目标现在受到的伤害增加67%
    // 效果永久持续，直到目标死亡
}
```

### 检查定命效果

```java
// 检查目标是否有定命效果
Doom doom = target.buff(Doom.class);
if (doom != null) {
    // 目标被定命，伤害会增加67%
    // 可以在此添加特殊逻辑
}
```

### 免疫处理示例

```java
// 为某类角色添加定命免疫
public class ResistantEnemy extends Mob {
    {
        immunities.add(Doom.class);
    }
}

// 或在法杖/道具中添加免疫
public class ProtectiveWand extends Wand {
    {
        // 使使用者免疫定命
        // 这在 WandOfRegrowth 中实现
    }
}
```

---

## 注意事项

### 1. 永久性效果

定命效果**不会自然消退**：
- 没有持续时间限制
- 不会因回合流逝而消失
- 只有目标死亡才会解除

**游戏内描述**：
> "Doom is permanent, its effects only end in death."
> "定命是永久性的，死后才能解脱。"

### 2. 伤害增幅的特殊性

```java
// 伤害增幅在 Char.damage() 中实现
// 这意味着：
// 1. 所有伤害来源都会被增幅（物理、魔法、环境等）
// 2. 增幅发生在最终伤害计算阶段
// 3. 需要检查免疫状态，防止"免疫了增幅但仍显示效果"
```

### 3. 与 Corruption 的关系

| 特性 | Doom (定命) | Corruption (腐化) |
|------|-------------|-------------------|
| 效果类型 | 伤害增幅 | 持续伤害 |
| 目标阵营 | 保持敌对 | 转为盟友 |
| 视觉效果 | DARKENED | DARKENED |
| 图标 | CORRUPT | CORRUPT |
| 持续性 | 永久 | 永久 |
| 互斥性 | 与 Corruption 互斥 | 与 Doom 互斥 |

### 4. 视觉效果冲突

```java
// 注意：fx方法中检查 invisible 状态
else if (target.invisible == 0) target.sprite.remove(CharSprite.State.DARKENED);
```
- 隐身状态的角色移除定命效果时不会立即更新视觉效果
- 这是为了避免隐身效果被意外"揭示"
- 隐身角色仍然受定命伤害增幅影响

### 5. 免疫机制

某些 Boss 对定命有特殊处理：
```java
// DwarfKing.java - 矮人国王的特殊免疫
@Override
public boolean isImmune(Class effect) {
    // 第2阶段及以后，免疫定命的伤害增幅效果
    // 但定命效果本身仍可被施加
    if (phase > 1 && effect == Doom.class && buff(Doom.class) != null) {
        return true;
    }
    return super.isImmune(effect);
}
```

### 6. 公告显示

```java
announced = true;  // 效果施加时会显示公告
```

这会在UI上显示定命效果被施加的消息，提醒玩家效果状态。

### 7. 与其他效果的交互

- **DeathMark（死亡印记）**：伤害增幅叠加（1.67 × 1.25 = 2.0875 倍伤害）
- **Bleeding（流血）**：流血伤害也会被增幅
- **Poison（中毒）**：毒伤害也会被增幅
- **环境伤害**：深渊坠落、陷阱等伤害也会被增幅

---

## 最佳实践

### 1. 标准施加流程

```java
// 在 WandOfCorruption 中的推荐做法
public void tryApplyDoom(Mob enemy, float corruptingPower, float enemyResist) {
    // 1. 检查是否已有相关效果
    if (enemy.buff(Corruption.class) != null || enemy.buff(Doom.class) != null) {
        // 已有效果，跳过
        return;
    }
    
    // 2. 检查免疫
    if (enemy.isImmune(Doom.class)) {
        // 目标免疫定命
        return;
    }
    
    // 3. 施加定命
    Buff.affect(enemy, Doom.class);
}
```

### 2. 作为 debuff 注册

```java
// 在 WandOfCorruption 中，定命被注册为 MAJOR_DEBUFF
private static final HashMap<Class<? extends Buff>, Float> MAJOR_DEBUFFS = new HashMap<>();
static {
    MAJOR_DEBUFFS.put(Doom.class, 0f);  // 权重为0，表示基础概率
}
```

### 3. 免疫设置

```java
// 如 WandOfRegrowth 使使用者免疫定命
public class WandOfRegrowth extends Wand {
    // 在相关效果类中：
    {
        immunities.add(Doom.class);
    }
}
```

### 4. 自定义定命变体

如需创建不同强度的定命效果：

```java
// 方式1：子类化（需要修改 Char.damage() 支持新类）
public class GreaterDoom extends Doom {
    // 在 Char.damage() 中需要添加新的检查逻辑
}

// 方式2：通过配置实现（推荐）
public class ConfigurableDoom extends Doom {
    public float damageMultiplier = 1.67f;
    
    // 需要修改 Char.damage() 来读取此配置
}
```

### 5. 效果移除

```java
// 移除定命效果（通常只有死亡才会触发）
Buff.detach(target, Doom.class);
```

### 6. 调试建议

```java
// 在 Char.damage() 中添加调试输出
if (this.buff(Doom.class) != null && !isImmune(Doom.class)) {
    float originalDamage = damage;
    damage *= 1.67f;
    // GLog.d("Doom amplifies damage: %.1f -> %.1f", originalDamage, damage);
}
```

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `Buff` | 父类 | 状态效果基类，提供核心框架 |
| `Corruption` | 关联 | 同样使用 DARKENED 效果和 CORRUPT 图标 |
| `CharSprite` | 依赖 | 精灵视觉效果管理 |
| `BuffIndicator` | 依赖 | 状态图标显示 |
| `Char` | 外部依赖 | 伤害增幅逻辑在此实现 |
| `WandOfCorruption` | 应用者 | 腐化法杖可施加定命效果 |
| `WandOfRegrowth` | 免疫提供者 | 生长法杖提供定命免疫 |

---

## 消息定义

定命效果的多语言消息定义在 `actors.properties` 中：

**英文版**：
```properties
actors.buffs.doom.name=doomed
actors.buffs.doom.desc=It's hard to keep going when it seems like the universe wants you dead.\n\nDoomed characters will receive +67% damage from all sources.\n\nDoom is permanent, its effects only end in death.
```

**中文版**：
```properties
actors.buffs.doom.name=定命
actors.buffs.doom.desc=当整个宇宙都看起来想置你于死地时，继续斗争还有什么意义呢？\n\n被定命的角色受到的任何伤害都会提升67%。\n\n定命是永久性的，死后才能解脱。
```

---

## 版本历史

- **初始版本**：继承自 Pixel Dungeon
- **Shattered Pixel Dungeon**：由 Evan Debenham 维护扩展
- **当前版本**：持续更新中（2014-2026）

---

## 参考资料

- [Shattered Pixel Dungeon 官方网站](https://shatteredpixel.com/shatteredpd/)
- [GNU GPL v3.0 许可证](http://www.gnu.org/licenses/)