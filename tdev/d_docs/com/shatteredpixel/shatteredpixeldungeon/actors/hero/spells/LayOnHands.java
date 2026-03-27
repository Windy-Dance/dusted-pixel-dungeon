# LayOnHands 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/LayOnHands.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 139 |
| **中文名称** | 按手治疗 |

---

## 法术概述

`LayOnHands`（按手治疗）是牧师职业的2级法术。该法术的主要功能是：

1. **核心效果**：对相邻目标施加治疗和护盾效果，英雄自身只获得护盾，盟友获得治疗+护盾
2. **战术价值**：提供近距离紧急治疗和防护，适合支援濒危盟友或自我防护
3. **使用场景**：盟友生命危急、自身需要护盾保护、或近距离团队支援时

**法术类型**：
- **目标类型**：Targeted（需要选择相邻目标）
- **充能消耗**：1点充能
- **天赋需求**：LAY_ON_HANDS 天赋

---

## 类关系图

```mermaid
classDiagram
    class ClericSpell {
        <<abstract>>
        +void onCast(HolyTome, Hero)
        +float chargeUse(Hero)
        +boolean canCast(Hero)
    }
    
    class TargetedClericSpell {
        +void onCast(HolyTome, Hero)
        +int targetingFlags()
        #abstract void onTargetSelected(HolyTome, Hero, Integer)
    }
    
    class LayOnHands {
        +INSTANCE: LayOnHands
        +int icon()
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +int targetingFlags()
        +String desc()
        #void onTargetSelected(HolyTome, Hero, Integer)
        #void affectChar(Hero, Char)
    }
    
    ClericSpell <|-- TargetedClericSpell
    TargetedClericSpell <|-- LayOnHands
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | LayOnHands.INSTANCE | 单例实例 |

---

## 核心属性

### 充能消耗

| 属性 | 值 | 说明 |
|------|-----|------|
| `chargeUse()` | 1f | 每次施放消耗1点充能 |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | true | 需要目标选择 |
| `targetingFlags()` | -1 | 自定义目标选择行为（仅限相邻目标） |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires LAY_ON_HANDS | 施放此法术所需的天赋 |

### 范围限制

| 效果 | 限制 | 说明 |
|------|------|------|
| **施法距离** | 1格 | 只能对相邻目标施放 |
| **目标类型** | 角色 | 必须存在有效角色目标 |

### 效果数值

| 天赋等级 | 基础效果 | 英雄效果 | 盟友效果 |
|----------|----------|----------|----------|
| 1点 | 15点 | 15点护盾 | 最多15点治疗+溢出护盾 |
| 2点 | 20点 | 20点护盾 | 最多20点治疗+溢出护盾 |
| 3点 | 25点 | 25点护盾 | 最多25点治疗+溢出护盾 |

---

## 方法详解

### onTargetSelected(HolyTome tome, Hero hero, Integer target)

```java
@Override
protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
    // 验证相邻目标并应用治疗/护盾效果
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：处理玩家选择的目标，执行治疗和护盾逻辑。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄
- `target` (Integer)：选择的目标单元格坐标

**实现逻辑**：
1. **距离验证**：检查目标是否在相邻位置（距离≤1）
2. **目标验证**：确认目标位置存在有效角色
3. **特效播放**：播放传送音效（TELEPORT）
4. **效果应用**：调用`affectChar()`应用治疗/护盾效果
5. **动画处理**：
   - 对英雄自身：执行操作动画
   - 对盟友目标：执行ZAP动画
6. **生命链接协同**：如果存在生命链接盟友，复制效果到另一方

---

### affectChar(Hero hero, Char ch)

```java
private void affectChar(Hero hero, Char ch) {
    // 根据目标类型应用不同的治疗/护盾效果
}
```

**方法作用**：为核心目标应用治疗和护盾效果，根据目标是英雄还是盟友采用不同策略。

**参数**：
- `hero` (Hero)：施法英雄（用于天赋等级计算）
- `ch` (Char)：目标角色

**实现逻辑**：
- **英雄自身**：
  - 只获得护盾：10 + 5×天赋等级点
  - 护盾上限：3×基础效果 - 现有护盾值
- **盟友目标**：
  - 优先恢复生命值至最大
  - 溢出的生命值转换为护盾（同样受上限限制）
  - 同时显示治疗和护盾的视觉反馈

---

### targetingFlags()

```java
@Override
public int targetingFlags() {
    return -1; //auto-targeting behaviour is often wrong, so we don't use it
}
```

**方法作用**：禁用自动弹道目标选择，使用自定义相邻目标验证逻辑。

**返回值**：
- `-1`：表示不使用标准弹道系统

---

### desc()

```java
@Override
public String desc() {
    return Messages.get(this, "desc", 10 + 5*Dungeon.hero.pointsInTalent(Talent.LAY_ON_HANDS)) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本，包含动态计算的效果数值。

**返回值**：
- 包含治疗/护盾值和充能消耗的完整描述字符串

---

## 特殊机制

### 目标差异化

- **英雄自身**：仅获得护盾，无治疗效果
- **盟友目标**：优先治疗，溢出转护盾
- **护盾上限**：所有护盾总量不超过3倍基础效果值

### 生命链接协同

- **英雄施放**：同时为生命链接盟友施加相同效果
- **盟友施放**：同时为英雄施加相同效果  
- **智能同步**：确保生命链接双方获得完整的治疗/护盾组合

### 距离限制

- **严格相邻**：只能对距离为1的目标施放
- **安全机制**：无效距离会给出错误提示
- **战术影响**：要求玩家靠近目标，增加风险但提高精准度

---

## 使用示例

### 基本施法

```java
// 施放按手治疗
if (hero.hasTalent(Talent.LAY_ON_HANDS)) {
    // 玩家选择相邻目标后自动调用
    LayOnHands.INSTANCE.onTargetSelected(tome, hero, targetPos);
}
```

### 目标选择流程

```java
// 完整的施法流程
HolyTome tome = new HolyTome();
if (tome.canCast(hero, LayOnHands.INSTANCE)) {
    // 1. 打开目标选择器
    LayOnHands.INSTANCE.onCast(tome, hero);
    
    // 2. 玩家选择相邻目标
    // 3. 自动调用 onTargetSelected 处理治疗/护盾
}
```

### 护盾管理

```java
// 检查护盾状态以优化使用
Barrier barrier = hero.buff(Barrier.class);
if (barrier != null) {
    int currentShield = barrier.shielding();
    int maxPotential = 3 * (10 + 5 * talentLevel);
    int additionalShield = Math.max(0, maxPotential - currentShield);
    // 根据额外护盾量决定是否使用
}
```

---

## 注意事项

### 平衡性考虑

1. **低消耗高效率**：1点充能消耗提供显著治疗/护盾效果
2. **距离限制**：相邻距离要求增加使用风险，防止远程滥用
3. **目标差异**：英雄只获得护盾的设计鼓励支援盟友

### 特殊机制

1. **护盾上限**：防止无限叠加护盾，保持游戏平衡
2. **溢出转换**：盟友治疗溢出自动转护盾，最大化资源利用
3. **生命链接复制**：与生命链接天赋形成强大的团队支援体系

### 技术限制

1. **距离计算**：使用Dungeon.level.distance()精确计算相邻位置
2. **角色检测**：依赖Actor.findChar()正确识别目标角色
3. **视觉反馈**：同时显示治疗和护盾状态，确保玩家清楚效果

---

## 最佳实践

### 战斗策略

- **紧急救援**：在盟友生命危急时立即使用，优先治疗满血再给护盾
- **自我防护**：在预见到大量伤害前为自己施加护盾
- **团队协同**：与生命链接盟友配合，形成双重治疗保障

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.LAY_ON_HANDS) && 
    hero.pointsInTalent(Talent.LAY_ON_HANDS) >= 2 &&
    hero.hasTalent(Talent.LIFE_LINK)) {
    // 高效治疗+生命链接，形成完美的团队生存体系
}
```

### 职业优化

- **祭司流派**：配合其他治疗法术，形成完整的支援体系
- **圣骑士流派**：作为近战支援，在前线为队友提供及时治疗
- **通用策略**：合理规划位置，确保能快速接近需要治疗的目标

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `TargetedClericSpell` | 父类 | 目标选择法术基类 |
| `Barrier` | 效果 | 护盾Buff系统 |
| `PowerOfMany` | 依赖 | 力量合一天赋，提供盟友检测 |
| `LifeLinkSpell` | 协同 | 生命链接法术，提供效果复制 |
| `FloatingText` | 依赖 | 视觉状态反馈系统 |
| `Dungeon.level.distance()` | 依赖 | 距离计算系统 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.layonhands.name` | "按手治疗" | 法术名称 |
| `spells.layonhands.desc` | "对相邻目标施加%d点治疗/护盾效果。对自己仅提供护盾，对盟友优先治疗，溢出部分转为护盾。" | 法术描述 |
| `spells.layonhands.prompt` | "选择要治疗的相邻目标" | 目标选择提示 |
| `spells.layonhands.invalid_target` | "目标太远" | 距离无效错误提示 |
| `spells.layonhands.no_target` | "没有有效目标" | 目标无效错误提示 |
| `spells.layonhands.charge_cost` | "%d 充能" | 充能消耗提示 |