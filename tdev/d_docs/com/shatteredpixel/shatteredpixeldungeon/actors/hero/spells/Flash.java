# Flash 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/Flash.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 84 |
| **中文名称** | 闪光传送 |

---

## 法术概述

`Flash`（闪光传送）是牧师职业的4级法术。该法术的主要功能是：

1. **核心效果**：在升天形态下进行短距离传送，帮助英雄快速穿越危险区域或重新定位
2. **战术价值**：提供灵活的机动性，在升天形态期间实现战术位移和逃脱
3. **使用场景**：躲避陷阱、越过障碍、接近敌人、或逃离危险情况

**法术类型**：
- **目标类型**：Targeted（需要选择目标位置）
- **充能消耗**：2 + 使用次数 点充能（随使用次数递增）
- **天赋需求**：FLASH 天赋 + 升天形态激活

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
    
    class Flash {
        +INSTANCE: Flash
        +int icon()
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +int targetingFlags()
        #void onTargetSelected(HolyTome, Hero, Integer)
    }
    
    ClericSpell <|-- TargetedClericSpell
    TargetedClericSpell <|-- Flash
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | Flash.INSTANCE | 单例实例 |

---

## 核心属性

### 充能消耗

| 属性 | 基础值 | 递增机制 | 说明 |
|------|--------|----------|------|
| `chargeUse()` | 2f | +1点每次使用 | 每次使用后消耗增加1点 |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | true | 需要目标选择 |
| `targetingFlags()` | -1 | 自定义目标选择（针对空地，非敌人） |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires FLASH + AscendedForm | 施放此法术的特殊条件 |

### 范围限制

| 效果 | 基础值 | 天赋加成 | 最大值（3点天赋） |
|------|--------|----------|------------------|
| 传送范围 | 2格 | +1格每点天赋 | 5格 |

---

## 方法详解

### onTargetSelected(HolyTome tome, Hero hero, Integer target)

```java
@Override
protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
    // 验证目标并执行传送
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：处理玩家选择的目标位置，执行传送逻辑。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄
- `target` (Integer)：选择的目标单元格坐标

**实现逻辑**：
1. **目标验证**：
   - 检查目标是否为空（用户取消）
   - 验证目标位置是否有效（非固体、已探索/访问过）
   - 检查传送距离是否在范围内（2 + 天赋等级格）
2. **传送执行**：
   - 调用ScrollOfTeleportation.teleportToLocation()执行传送
   - 如果成功传送，花费1回合时间
3. **使用计数**：
   - 增加AscendedForm中的flashCasts计数器
   - 下次使用时充能消耗将增加

---

### chargeUse(Hero hero)

```java
@Override
public float chargeUse(Hero hero) {
    if (hero.buff(AscendedForm.AscendBuff.class) != null){
        return 2 + hero.buff(AscendedForm.AscendBuff.class).flashCasts;
    } else {
        return 2;
    }
}
```

**方法作用**：返回基于使用次数的动态充能消耗。

**返回值**：
- 基础消耗2点 + 在当前升天形态中已使用的次数

### targetingFlags()

```java
@Override
public int targetingFlags() {
    return -1; //targets an empty cell, not an enemy
}
```

**方法作用**：禁用标准弹道系统，因为闪光传送针对空地而非敌人。

**返回值**：
- `-1`：表示自定义目标选择行为

---

## 特殊机制

### 动态消耗机制

- **基础消耗**：2点充能
- **递增消耗**：每次使用后消耗增加1点
- **重置机制**：升天形态结束后，消耗重置为2点
- **策略影响**：鼓励玩家谨慎使用，避免过度消耗

### 目标限制

- **地形限制**：只能传送到非固体（可通行）位置
- **探索要求**：目标必须是已访问或已映射的位置
- **距离限制**：传送距离受天赋等级影响（2-5格）
- **安全机制**：无效目标会给出错误提示

### 升天绑定

- **形态依赖**：只能在升天形态下使用
- **状态追踪**：通过AscendedForm.AscendBuff.flashCasts追踪使用次数
- **协同效应**：与升天形态的其他能力形成完整战术体系

---

## 使用示例

### 基本施法

```java
// 施放闪光传送（需要升天形态激活）
if (hero.hasTalent(Talent.FLASH) && 
    hero.buff(AscendedForm.AscendBuff.class) != null) {
    // 玩家选择目标位置后自动调用
    Flash.INSTANCE.onTargetSelected(tome, hero, targetPos);
}
```

### 目标选择流程

```java
// 完整的施法流程
HolyTome tome = new HolyTome();
if (tome.canCast(hero, Flash.INSTANCE)) {
    // 1. 打开目标选择器
    Flash.INSTANCE.onCast(tome, hero);
    
    // 2. 玩家选择传送目标位置
    // 3. 自动调用 onTargetSelected 处理传送
}
```

### 消耗管理

```java
// 检查当前消耗
AscendedForm.AscendBuff ascendBuff = hero.buff(AscendedForm.AscendBuff.class);
if (ascendBuff != null) {
    int currentCost = 2 + ascendBuff.flashCasts;
    // 根据当前消耗决定是否使用
}
```

---

## 注意事项

### 平衡性考虑

1. **递增消耗**：使用次数越多消耗越大，防止滥用
2. **升天限制**：只能在升天形态下使用，限制使用频率
3. **范围平衡**：基础范围较小，需要天赋投资才能扩大

### 特殊机制

1. **目标验证**：严格的地形和探索状态验证确保游戏平衡
2. **消耗重置**：升天形态结束时消耗重置，鼓励在单次升天中合理分配使用
3. **距离计算**：使用Dungeon.level.distance()计算真实距离，考虑地形障碍

### 技术限制

1. **升天依赖**：完全依赖AscendedForm系统
2. **传送安全**：使用ScrollOfTeleportation的安全传送机制
3. **回合消耗**：每次使用消耗1回合时间，影响战斗节奏

---

## 最佳实践

### 战斗策略

- **逃脱技巧**：在被围攻时传送到安全位置
- **突袭战术**：快速接近远程敌人或Boss
- **地形利用**：越过陷阱、悬崖或其他障碍物

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.FLASH) && 
    hero.pointsInTalent(Talent.FLASH) >= 2 &&
    hero.hasTalent(Talent.ASCENDED_FORM)) {
    // 扩大传送范围，配合强力升天形态形成完美机动性
}
```

### 资源管理

- **早期使用**：在升天形态早期使用较低消耗
- **关键时机**：保留高消耗使用到真正紧急情况
- **位置规划**：选择能最大化战术优势的传送目标

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `TargetedClericSpell` | 父类 | 目标选择法术基类 |
| `AscendedForm.AscendBuff` | 依赖 | 升天形态Buff，提供使用条件和计数器 |
| `ScrollOfTeleportation` | 依赖 | 传送实现，提供安全传送机制 |
| `Dungeon.level` | 依赖 | 地形和距离计算系统 |
| `GLog` | 依赖 | 错误提示系统 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.flash.name` | "闪光传送" | 法术名称 |
| `spells.flash.invalid_target` | "无效目标" | 目标无效错误提示 |
| `spells.flash.charge_cost` | "%d 充能" | 充能消耗提示 |