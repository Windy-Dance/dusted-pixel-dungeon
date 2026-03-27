# DivineSense 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/DivineSense.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends ClericSpell |
| **代码行数** | 109 |
| **中文名称** | 神圣感知 |

---

## 法术概述

`DivineSense`（神圣感知）是牧师职业的2级法术。该法术的主要功能是：

1. **核心效果**：揭示整个地图的视野，移除战争迷雾，并在50回合内持续提供完全视野
2. **战术价值**：提供完美的地图信息，帮助规划安全路线和发现隐藏威胁
3. **使用场景**：探索未知区域、寻找隐藏房间、避开陷阱或危险敌人时

**法术类型**：
- **目标类型**：Self-Targeted（自我目标）
- **充能消耗**：2点充能
- **天赋需求**：DIVINE_SENSE 天赋

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
    
    class DivineSense {
        +INSTANCE: DivineSense
        +int icon()
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +void onCast(HolyTome, Hero)
        +String desc()
    }
    
    class DivineSenseTracker {
        +static float DURATION = 50f
        +type = buffType.POSITIVE
        +int icon()
        +float iconFadePercent()
        +void detach()
    }
    
    ClericSpell <|-- DivineSense
    DivineSense +-- DivineSenseTracker
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | DivineSense.INSTANCE | 单例实例 |
| DURATION | 50f | 神圣感知持续时间（回合数） |

---

## 核心属性

### 充能消耗

| 属性 | 值 | 说明 |
|------|-----|------|
| `chargeUse()` | 2f | 每次施放消耗2点充能 |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | false | 无需目标选择，自动施放于自身 |
| `targetingFlags()` | -1 | 无目标选择标志 |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires DIVINE_SENSE | 施放此法术所需的天赋 |

### 效果数值

| 效果 | 数值 | 说明 |
|------|------|------|
| 视野范围 | 全地图 | 揭示所有已探索和未探索区域 |
| 持续时间 | 50回合 | 固定持续时间（不受天赋影响） |
| 生命链接协同 | 是 | 同时为生命链接盟友施加效果 |

---

## 方法详解

### onCast(HolyTome tome, Hero hero)

```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    // 应用神圣感知效果并更新地图视野
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：执行神圣感知的主要逻辑，揭示地图视野。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄

**实现逻辑**：
1. **Buff应用**：为英雄施加DivineSenseTracker Buff，持续50回合
2. **地图更新**：调用`Dungeon.observe()`揭示当前楼层所有可见区域
3. **视觉特效**：播放阅读音效和VISION法术精灵效果
4. **生命链接协同**：如果存在生命链接盟友，也为其施加相同效果
5. **完成施法**：调用`onSpellCast(tome, hero)`处理后续逻辑

---

### desc()

```java
public String desc() {
    return Messages.get(this, "desc", 4+4*Dungeon.hero.pointsInTalent(Talent.DIVINE_SENSE)) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本。

**注意**：描述中的数值参数`(4+4*talent)`可能是遗留代码或用于其他用途，实际效果是固定的50回合持续时间。

**返回值**：
- 包含效果描述和充能消耗的完整描述字符串

---

### detach() (DivineSenseTracker)

```java
@Override
public void detach() {
    super.detach();
    Dungeon.observe();
    GameScene.updateFog();
}
```

**方法作用**：当Buff结束时，重新计算视野和迷雾。

**实现逻辑**：
- 调用`Dungeon.observe()`重新计算可见区域
- 调用`GameScene.updateFog()`更新战争迷雾显示
- 这确保了当神圣感知结束时，视野正确回归到正常状态

---

## 内部类 DivineSenseTracker

### 类定义

```java
public static class DivineSenseTracker extends FlavourBuff {
    public static final float DURATION = 50f;
    {
        type = buffType.POSITIVE;
    }
    // ... 其他方法
}
```

**类作用**：实现神圣感知的持续效果，维持全地图视野。

**关键属性**：
- `DURATION`: 50f - Buff固定持续时间
- `type`: buffType.POSITIVE - 正面Buff类型

**关键方法**：
- `icon()`: 返回HOLY_SIGHT图标
- `iconFadePercent()`: 返回图标淡出百分比，用于UI显示
- `detach()`: Buff结束时处理视野恢复

### 视野机制

**全地图揭示**：
- 神圣感知激活期间，玩家可以看到整个楼层的地图
- 包括未探索的房间、走廊和隐藏区域
- 敌人位置也会被显示（如果在地图上）

**迷雾移除**：
- 战争迷雾被完全移除，所有区域都保持可见
- 即使英雄移动到新区域，之前揭示的区域仍然可见

**持久性**：
- 50回合的持续时间足够完成大部分楼层探索
- 与生命链接盟友共享，提供团队视野优势

---

## 使用示例

### 基本施法

```java
// 施放神圣感知
if (hero.hasTalent(Talent.DIVINE_SENSE)) {
    DivineSense.INSTANCE.onCast(holyTome, hero);
}
```

### 地图探索

```java
// 在进入新楼层时使用神圣感知
// 揭示整个楼层布局，包括隐藏房间和陷阱
DivineSense.INSTANCE.onCast(tome, hero);
```

### 团队协同

```java
// 神圣感知会自动应用到生命链接盟友
// 无需额外操作，系统自动处理
DivineSense.INSTANCE.onCast(tome, hero);
```

---

## 注意事项

### 平衡性考虑

1. **中等消耗**：2点充能消耗适中，可以定期使用
2. **固定持续时间**：50回合持续时间固定，不受天赋等级影响
3. **探索价值**：在复杂楼层或需要精确规划时价值极高

### 特殊机制

1. **Buff结束处理**：Buff结束时会重新计算视野，确保游戏状态正确
2. **生命链接同步**：与生命链接天赋形成完美协同
3. **地图持久性**：揭示的区域在玩家离开后仍然保持可见

### 技术限制

1. **仅限当前楼层**：神圣感知只影响当前所在楼层
2. **不揭示未生成内容**：无法揭示尚未生成的楼层内容
3. **性能考虑**：全地图揭示可能会对低端设备造成轻微性能影响

---

## 最佳实践

### 探索策略

- **楼层开始**：在进入新楼层时立即使用，获得完整地图信息
- **复杂区域**：在迷宫般复杂的区域使用，避免迷路
- **Boss准备**：在Boss战前使用，了解整个战斗区域布局

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.DIVINE_SENSE) && 
    hero.hasTalent(Talent.LIFE_LINK)) {
    // 神圣感知+生命链接，为整个团队提供完美视野
}
```

### 资源管理

- **时机选择**：在充能充足且即将进入未知区域时使用
- **避免浪费**：不要在已经完全探索的楼层使用
- **紧急情况**：在被困或迷失方向时作为应急手段

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `ClericSpell` | 父类 | 所有牧师法术的基类 |
| `FlavourBuff` | Buff父类 | 提供持续效果的基础Buff类 |
| `Dungeon.observe()` | 依赖 | 地图视野更新系统 |
| `GameScene.updateFog()` | 依赖 | 战争迷雾更新系统 |
| `SpellSprite.VISION` | 依赖 | 视觉特效系统 |
| `LifeLinkSpell` | 协同 | 生命链接法术，提供团队视野 |
| `PowerOfMany` | 依赖 | 力量合一天赋，提供盟友检测 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.divinesense.name` | "神圣感知" | 法术名称 |
| `spells.divinesense.desc` | "获得神圣感知能力，持续%d回合。在此期间，你可以看到整个楼层的地图。" | 法术描述 |
| `spells.divinesense.charge_cost` | "%d 充能" | 充能消耗提示 |