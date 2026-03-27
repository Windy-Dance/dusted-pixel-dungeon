# HolyLance 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/HolyLance.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 191 |
| **中文名称** | 神圣长矛 |

---

## 法术概述

`HolyLance`（神圣长矛）是牧师职业的3级法术。该法术的主要功能是：

1. **核心效果**：发射一道神圣长矛，对目标敌人造成高额神圣伤害，并对亡灵/恶魔单位造成全额伤害
2. **战术价值**：作为高爆发单体伤害技能，专门针对高威胁精英怪和Boss
3. **使用场景**：对付高生命值敌人、亡灵/恶魔单位、或需要快速削减敌人血量的关键时刻

**法术类型**：
- **目标类型**：Targeted（需要选择敌人目标）
- **充能消耗**：4点充能（高消耗）
- **天赋需求**：HOLY_LANCE 天赋

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
    
    class HolyLance {
        +INSTANCE: HolyLance
        +int icon()
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +int targetingFlags()
        +String desc()
        #void onTargetSelected(HolyTome, Hero, Integer)
    }
    
    class HolyLanceVFX {
        +image = ItemSpriteSheet.THROWING_SPIKE
        +ItemSprite.Glowing glowing()
        +Emitter emitter()
    }
    
    class LanceCooldown {
        +int icon()
        +void tintIcon(Image)
        +float iconFadePercent()
    }
    
    ClericSpell <|-- TargetedClericSpell
    TargetedClericSpell <|-- HolyLance
    HolyLance +-- HolyLanceVFX
    HolyLance +-- LanceCooldown
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | HolyLance.INSTANCE | 单例实例 |

---

## 核心属性

### 充能消耗

| 属性 | 值 | 说明 |
|------|-----|------|
| `chargeUse()` | 4f | 每次施放消耗4点充能（高消耗） |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | true | 需要目标选择 |
| `targetingFlags()` | Ballistica.PROJECTILE | 投射物弹道，遇障碍物停止 |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires HOLY_LANCE + 无冷却 | 施放此法术的条件（需无LanceCooldown） |

### 冷却机制

| 属性 | 值 | 说明 |
|------|-----|------|
| **冷却时间** | 30回合 | 施放后进入冷却状态 |
| **冷却Buff** | LanceCooldown | 显示冷却计时器 |

### 伤害数值

| 天赋等级 | 最小伤害 | 最大伤害 | 亡灵/恶魔伤害 |
|----------|----------|----------|----------------|
| 1点 | 30 | 55 | 55（全额） |
| 2点 | 45 | 82.5→83 | 83（全额） |
| 3点 | 60 | 110 | 110（全额） |

---

## 方法详解

### onTargetSelected(HolyTome tome, Hero hero, Integer target)

```java
@Override
protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
    // 发射神圣长矛并应用伤害效果
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：处理玩家选择的目标，执行神圣长矛的伤害计算和效果应用。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄
- `target` (Integer)：选择的目标单元格坐标

**实现逻辑**：
1. **弹道计算**：使用Ballistica.PROJECTILE计算投射物路径（遇障碍停止）
2. **目标验证**：检查是否为目标自己（防止自伤）
3. **快速瞄准**：设置QuickSlotButton的攻击目标
4. **视觉特效**：
   - 播放ZAP音效
   - 创建HolyLanceVFX导弹特效
   - 英雄执行施法动画
5. **伤害应用**：
   - 对碰撞位置的敌人造成神圣伤害
   - 亡灵/恶魔单位受到全额最大伤害
   - 播放魔法打击和刺击双重音效
6. **Debuff应用**：对活跃敌人施加照亮效果（GuidingLight.Illuminated）
7. **冷却设置**：施加30回合的LanceCooldown Buff

---

### desc()

```java
@Override
public String desc() {
    int min = 15 + 15*Dungeon.hero.pointsInTalent(Talent.HOLY_LANCE);
    int max = Math.round(27.5f + 27.5f*Dungeon.hero.pointsInTalent(Talent.HOLY_LANCE));
    return Messages.get(this, "desc", min, max) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本，包含动态计算的伤害范围。

**返回值**：
- 包含最小/最大伤害值和充能消耗的完整描述字符串

---

### canCast(Hero hero)

```java
@Override
public boolean canCast(Hero hero) {
    return super.canCast(hero)
            && hero.hasTalent(Talent.HOLY_LANCE)
            && hero.buff(LanceCooldown.class) == null;
}
```

**方法作用**：检查英雄是否可以施放神圣长矛法术。

**返回值**：
- `true`：拥有天赋且不在冷却中
- `false`：缺少天赋或处于冷却状态

---

## 内部类

### HolyLanceVFX

**类作用**：实现神圣长矛的视觉特效。

**关键特性**：
- **基础图像**：使用THROWING_SPIKE精灵
- **发光效果**：白色发光（#FFFFFF）带透明度
- **粒子特效**：持续发射SparkParticle火花粒子
- **导弹轨迹**：通过MissileSprite实现飞行轨迹

### LanceCooldown

**类作用**：神圣长矛的冷却计时器。

**关键特性**：
- **图标**：使用TIME图标（沙漏）
- **着色**：黄色调色（0.67, 0.67, 0）
- **淡出效果**：根据剩余冷却时间逐渐淡出
- **持续时间**：30回合固定冷却

---

## 特殊机制

### 伤害机制

- **可变伤害**：基于天赋等级提供不同伤害范围
- **种族克制**：对亡灵（UNDEAD）和恶魔（DEMONIC）单位造成全额最大伤害
- **双重音效**：同时播放魔法打击和物理刺击音效，体现神圣+物理双重属性

### 弹道系统

- **投射物行为**：使用PROJECTILE弹道，遇到墙壁或障碍物会停止
- **精准打击**：只攻击第一个碰撞到的敌人，不会穿透
- **安全机制**：无法对自身施放，防止意外自伤

### 冷却管理

- **强制冷却**：30回合冷却确保不会被滥用
- **视觉反馈**：Buff图标清晰显示冷却状态
- **平衡设计**：高消耗+长冷却+高伤害的平衡三角

---

## 使用示例

### 基本施法

```java
// 施放神圣长矛（需要冷却结束）
if (hero.hasTalent(Talent.HOLY_LANCE) && 
    hero.buff(HolyLance.LanceCooldown.class) == null) {
    // 玩家选择目标后自动调用
    HolyLance.INSTANCE.onTargetSelected(tome, hero, targetPos);
}
```

### 冷却检查

```java
// 检查神圣长矛是否可用
HolyLance.LanceCooldown cooldown = 
    hero.buff(HolyLance.LanceCooldown.class);
if (cooldown == null) {
    // 可以施放神圣长矛
} else {
    float remainingTime = cooldown.cooldown(); // 获取剩余冷却时间
}
```

### 种族克制利用

```java
// 识别亡灵/恶魔单位以最大化伤害
if (Char.hasProp(enemy, Char.Property.UNDEAD) || 
    Char.hasProp(enemy, Char.Property.DEMONIC)) {
    // 神圣长矛将造成全额最大伤害
}
```

---

## 注意事项

### 平衡性考虑

1. **高成本高回报**：4点充能+30回合冷却换取高额爆发伤害
2. **天赋投资**：每点天赋显著提升伤害，值得投资
3. **时机选择**：最佳使用时机是对付高威胁单体目标

### 特殊机制

1. **弹道限制**：投射物会被障碍物阻挡，需要确保视线通畅
2. **冷却强制**：无法通过任何手段跳过冷却时间
3. **种族优势**：对特定敌人类型有显著优势，需要识别敌人类型

### 技术限制

1. **弹道计算**：依赖Ballistica系统正确计算碰撞
2. **Buff管理**：冷却Buff需要正确序列化和加载
3. **视觉同步**：导弹特效与伤害应用需要精确同步

---

## 最佳实践

### 战斗策略

- **Boss斩杀**：在Boss血量较低时使用，确保击杀
- **精英优先**：优先对高威胁精英怪使用，减少团队压力  
- **种族利用**：专门用来对付亡灵和恶魔单位，发挥最大效果

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.HOLY_LANCE) && 
    hero.pointsInTalent(Talent.HOLY_LANCE) >= 2) {
    // 高伤害输出配合其他支援天赋
    // 形成完整的输出循环
}
```

### 资源管理

- **充能储备**：确保在关键时刻有足够的充能施放
- **冷却规划**：将冷却时间纳入战斗节奏规划
- **目标选择**：避免对普通小怪浪费，保留给重要目标

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `TargetedClericSpell` | 父类 | 目标选择法术基类 |
| `Ballistica.PROJECTILE` | 依赖 | 投射物弹道系统 |
| `MissileSprite` | 依赖 | 导弹视觉特效系统 |
| `GuidingLight.Illuminated` | 协同 | 照亮Debuff效果 |
| `QuickSlotButton` | 协同 | 快速瞄准系统 |
| `FlavourBuff` | 依赖 | 冷却Buff基类 |
| `Char.Property.UNDEAD/DEMONIC` | 依赖 | 敌人种族属性检测 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.holylance.name` | "神圣长矛" | 法术名称 |
| `spells.holylance.desc` | "发射一道神圣长矛，对目标造成%d-%d点神圣伤害。对亡灵和恶魔单位造成全额最大伤害。" | 法术描述 |
| `spells.holylance.prompt` | "选择要攻击的敌人" | 目标选择提示 |
| `spells.holylance.charge_cost` | "%d 充能" | 充能消耗提示 |