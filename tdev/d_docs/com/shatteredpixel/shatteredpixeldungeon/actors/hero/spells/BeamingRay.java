# BeamingRay 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/BeamingRay.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 223 |
| **中文名称** | 光束传送 |

---

## 法术概述

`BeamingRay`（光束传送）是牧师职业的4级法术。该法术的主要功能是：

1. **核心效果**：将盟友（通过力量合一或静滞天赋召唤的单位）传送到指定位置，并为其提供攻击增益
2. **战术价值**：实现盟友的快速部署和战术重定位，同时增强其战斗能力
3. **使用场景**：需要快速支援、重新部署盟友或对特定区域进行突袭时

**法术类型**：
- **目标类型**：Targeted（需要选择目标位置）
- **充能消耗**：1点充能
- **天赋需求**：BEAMING_RAY 天赋 + PowerOfMany 或 Stasis 天赋

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
    
    class BeamingRay {
        +INSTANCE: BeamingRay
        +int icon()
        +String desc()
        +int targetingFlags()
        +boolean canCast(Hero)
        #void onTargetSelected(HolyTome, Hero, Integer)
    }
    
    class BeamingRayBoost {
        +int object = 0
        +static float DURATION = 10f
        +int icon()
        +float iconFadePercent()
    }
    
    ClericSpell <|-- TargetedClericSpell
    TargetedClericSpell <|-- BeamingRay
    BeamingRay +-- BeamingRayBoost
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | BeamingRay.INSTANCE | 单例实例 |
| DURATION | 10f | 攻击增益持续时间（回合数） |
| OBJECT | "object" | Bundle存储键，用于保存目标ID |

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
| `targetingFlags()` | Ballistica.STOP_TARGET | 在目标处停止的弹道类型 |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires BEAMING_RAY + (PowerOfMany 或 Stasis) | 施放此法术所需的天赋组合 |

### 范围限制

| 效果 | 基础值 | 天赋加成 | 最大值（3点天赋） |
|------|--------|----------|------------------|
| 传送范围 | 4格 | +4格每点天赋 | 16格 |
| 移动限制 | 范围减半 | 对不可移动单位生效 | - |

---

## 方法详解

### onTargetSelected(HolyTome tome, Hero hero, Integer target)

```java
@Override
protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
    // 处理目标选择逻辑
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：处理玩家选择的目标位置，执行传送和增益逻辑。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄
- `target` (Integer)：选择的目标单元格坐标

**实现逻辑**：
1. **目标验证**：检查目标位置是否有效（在地图内、可见、可通行）
2. **盟友获取**：确定要传送的盟友（PowerOfMany盟友优先，否则使用Stasis盟友）
3. **距离检查**：验证传送距离是否在范围内（考虑不可移动单位的距离减半）
4. **位置调整**：如果目标位置被占用，寻找最近的有效位置
5. **传送执行**：将盟友传送到目标位置并播放光束特效
6. **增益应用**：为目标附近的敌人应用攻击增益，引导盟友攻击
7. **Buff同步**：如果是生命链接盟友，同步生命链接Buff

---

### desc()

```java
@Override
public String desc() {
    return Messages.get(this, "desc", 4*Dungeon.hero.pointsInTalent(Talent.BEAMING_RAY), 30 + 5*Dungeon.hero.pointsInTalent(Talent.BEAMING_RAY)) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本，包含动态计算的范围和效果数值。

**返回值**：
- 包含传送范围格数、增益效果强度和充能消耗的完整描述字符串

---

### targetingFlags()

```java
@Override
public int targetingFlags() {
    return Ballistica.STOP_TARGET;
}
```

**方法作用**：返回弹道标志，指定在目标处精确停止。

**返回值**：
- `Ballistica.STOP_TARGET`：确保光束在目标位置准确停止

---

### canCast(Hero hero)

```java
@Override
public boolean canCast(Hero hero) {
    return super.canCast(hero)
            && hero.hasTalent(Talent.BEAMING_RAY)
            && (PowerOfMany.getPoweredAlly() != null || Stasis.getStasisAlly() != null);
}
```

**方法作用**：检查英雄是否可以施放光束传送法术。

**参数**：
- `hero` (Hero)：要检查的英雄

**返回值**：
- `true`：可以施放（拥有所有必要天赋且有可用盟友）
- `false`：无法施放（缺少天赋或没有可用盟友）

---

## 内部类 BeamingRayBoost

### 类定义

```java
public static class BeamingRayBoost extends FlavourBuff {
    {
        type = buffType.POSITIVE;
    }
    public int object = 0;
    public static final float DURATION = 10f;
    // ... 其他方法
}
```

**类作用**：为被传送的盟友提供攻击增益效果，使其优先攻击指定目标。

**关键属性**：
- `object`: int - 存储目标敌人的ID
- `DURATION`: 10f - 增益持续时间

**关键方法**：
- `storeInBundle()/restoreFromBundle()`: 序列化支持
- `icon()`: 返回圣光武器图标
- `iconFadePercent()`: 返回图标淡出百分比

### 增益机制

**目标锁定**：
- 如果指定了敌人目标，盟友会优先攻击该目标
- 如果未指定目标，盟友会攻击距离最近的敌人
- 对于可指挥盟友（DirectableAlly），会设置攻击目标
- 对于普通盟友（Mob），会设置仇恨目标

**持续时间**：
- 固定10回合，不受其他因素影响
- 时间结束后自动移除，不影响盟友正常AI行为

---

## 使用示例

### 基本施法

```java
// 施放光束传送（需要先有盟友）
if (hero.hasTalent(Talent.BEAMING_RAY) && 
    (PowerOfMany.getPoweredAlly() != null || Stasis.getStasisAlly() != null)) {
    // 玩家选择目标位置后自动调用
    BeamingRay.INSTANCE.onTargetSelected(tome, hero, targetPos);
}
```

### 目标选择流程

```java
// 完整的施法流程
HolyTome tome = new HolyTome();
if (tome.canCast(hero, BeamingRay.INSTANCE)) {
    // 1. 打开目标选择器
    BeamingRay.INSTANCE.onCast(tome, hero);
    
    // 2. 玩家选择目标位置
    // 3. 自动调用 onTargetSelected 处理传送
}
```

### 盟友管理

```java
// 获取当前可用盟友
Char ally = PowerOfMany.getPoweredAlly();
if (ally == null) {
    ally = Stasis.getStasisAlly();
}

if (ally != null) {
    // 盟友存在，可以使用光束传送
}
```

---

## 注意事项

### 平衡性考虑

1. **盟友依赖**：必须先通过其他天赋召唤盟友才能使用
2. **范围限制**：传送距离受天赋等级和盟友移动性影响
3. **时机选择**：最佳使用时机是在需要快速重新部署盟友时

### 特殊机制

1. **位置智能调整**：如果目标位置被占用，会自动寻找最近的有效位置
2. **敌人智能选择**：传送后自动为盟友选择最优攻击目标
3. **生命链接同步**：如果是生命链接盟友，会同步生命链接状态
4. **职业差异**：祭司职业会使目标敌人被标记（照亮），增加后续伤害

### 技术限制

1. **视野要求**：目标位置必须在英雄视野范围内
2. **地形限制**：目标位置必须是可通行的（或盟友能飞行的避免区域）
3. **盟友唯一性**：同一时间只能有一个活跃的传送盟友

---

## 最佳实践

### 战斗策略

- **进攻性使用**：将盟友直接传送到敌人集群中心，最大化AOE效果
- **防御性使用**：将盟友从危险区域传送到安全位置
- **战术配合**：与圣光武器、审判等法术配合，形成连招

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.BEAMING_RAY) && 
    hero.hasTalent(Talent.POWER_OF_MANY) &&
    hero.hasTalent(Talent.HOLY_WEAPON)) {
    // 力量合一召唤强力盟友，用光束传送部署，配合圣光武器输出
}
```

### 盟友协同

- **祭司流派**：利用照亮效果增加盟友攻击效率
- **圣骑士流派**：延长圣光武器持续时间，增强盟友输出
- **距离控制**：合理利用传送范围，在安全距离部署盟友

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `TargetedClericSpell` | 父类 | 目标选择法术基类 |
| `PowerOfMany` | 依赖 | 力量合一天赋，提供盟友 |
| `Stasis` | 依赖 | 静滞天赋，提供盟友 |
| `DirectableAlly` | 协同 | 可指挥盟友，支持目标设置 |
| `LifeLink` | 协同 | 生命链接Buff，需要同步 |
| `GuidingLight` | 协同 | 引导之光，祭司职业额外效果 |
| `ScrollOfTeleportation` | 依赖 | 传送效果实现 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.beamingray.name` | "光束传送" | 法术名称 |
| `spells.beamingray.desc` | "将你的盟友传送到指定位置，传送距离为%d格。传送后，盟友获得增益，使其攻击范围内的敌人，增益效果提升%d%%。" | 法术描述 |
| `spells.beamingray.prompt` | "选择要传送盟友的位置" | 目标选择提示 |
| `spells.beamingray.no_space` | "没有可用空间" | 位置无效错误提示 |
| `spells.beamingray.out_of_range` | "超出范围" | 距离过远错误提示 |
| `spells.beamingray.charge_cost` | "%d 充能" | 充能消耗提示 |