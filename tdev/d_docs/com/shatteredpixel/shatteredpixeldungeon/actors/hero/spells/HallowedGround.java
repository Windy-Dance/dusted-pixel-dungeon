# HallowedGround 法术详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/HallowedGround.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | class（非抽象） |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 270 |
| **中文名称** | 神圣之地 |

---

## 法术概述

`HallowedGround`（神圣之地）是牧师职业的3级法术。该法术的主要功能是：

1. **核心效果**：在目标区域创造一片神圣之地，将地形转换为草地并产生持续的治疗/护盾效果（对盟友）或控制效果（对敌人）
2. **战术价值**：提供持续的区域控制和团队支援，在战斗中创造有利地形
3. **使用场景**：团队战斗、持久战、或需要区域控制的战略位置

**法术类型**：
- **目标类型**：Targeted（需要选择目标位置）
- **充能消耗**：2点充能
- **天赋需求**：HALLOWED_GROUND 天赋

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
    
    class HallowedGround {
        +INSTANCE: HallowedGround
        +int icon()
        +float chargeUse(Hero)
        +int targetingFlags()
        +boolean canCast(Hero)
        +String desc()
        #void onTargetSelected(HolyTome, Hero, Integer)
        #void affectChar(Char)
    }
    
    class HallowedTerrain {
        +protected void evolve()
        #void affectChar(Char)
        +void use(BlobEmitter)
        +String tileDesc()
    }
    
    class HallowedFurrowTracker {
        // 计数器Buff，用于追踪草地产量
    }
    
    ClericSpell <|-- TargetedClericSpell
    TargetedClericSpell <|-- HallowedGround
    HallowedGround +-- HallowedTerrain
    HallowedGround +-- HallowedFurrowTracker
```

---

## 静态常量表

| 常量 | 值 | 说明 |
|------|-----|------|
| INSTANCE | HallowedGround.INSTANCE | 单例实例 |

---

## 核心属性

### 充能消耗

| 属性 | 值 | 说明 |
|------|-----|------|
| `chargeUse()` | 2f | 每次施放消耗2点充能 |

### 目标选择

| 属性 | 值 | 说明 |
|------|-----|------|
| `usesTargeting()` | true | 需要目标选择 |
| `targetingFlags()` | Ballistica.STOP_TARGET | 在目标处停止的弹道类型 |

### 天赋依赖

| 属性 | 值 | 说明 |
|------|-----|------|
| `canCast()` | requires HALLOWED_GROUND | 施放此法术所需的天赋 |

### 范围效果

| 天赋等级 | 基础半径 | 影响格子数（平均） | 说明 |
|----------|----------|-------------------|------|
| 1点 | 1格 | 9格 | 小范围区域 |
| 2点 | 2格 | 17格 | 中等范围区域 |
| 3点 | 3格 | 25格 | 大范围区域 |

---

## 方法详解

### onTargetSelected(HolyTome tome, Hero hero, Integer target)

```java
@Override
protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
    // 创建神圣之地并应用初始效果
    // ...
    onSpellCast(tome, hero);
}
```

**方法作用**：处理玩家选择的目标位置，创建神圣之地并应用初始效果。

**参数**：
- `tome` (HolyTome)：神圣典籍实例
- `hero` (Hero)：施法的英雄
- `target` (Integer)：选择的目标单元格坐标

**实现逻辑**：
1. **目标验证**：检查目标是否有效（非固体、在视野内）
2. **距离计算**：使用PathFinder.buildDistanceMap计算影响范围
3. **地形转换**：
   - 将空地、余烬、装饰地形转换为草地（Terrain.GRASS）
   - 更新地图显示并播放粒子特效
4. **持续效果**：为每个影响格子添加HallowedTerrain Blob（持续20回合）
5. **角色影响**：收集范围内所有角色并调用affectChar应用效果
6. **生命链接协同**：特殊处理生命链接盟友，确保双方都被包含

---

### affectChar(Char ch)

```java
private void affectChar(Char ch) {
    // 根据角色阵营应用不同效果
}
```

**方法作用**：根据角色阵营应用不同的即时效果。

**实现逻辑**：
- **盟友效果**：
  - 满血：获得15点护盾（不超过30点上限）
  - 非满血：恢复15点生命值，溢出部分转为护盾
- **敌人效果**：
  - 施加照亮效果（GuidingLight.Illuminated）
  - 对非飞行敌人施加根须效果（Roots），持续2回合

---

### desc()

```java
public String desc() {
    int area = 1 + 2*Dungeon.hero.pointsInTalent(Talent.HALLOWED_GROUND);
    return Messages.get(this, "desc", area) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
}
```

**方法作用**：返回法术的详细描述文本，包含动态计算的影响半径。

**返回值**：
- 包含影响半径和充能消耗的完整描述字符串

---

## 内部类 HallowedTerrain

### 类定义

```java
public static class HallowedTerrain extends Blob {
    // ... 持续地形效果实现
}
```

**类作用**：实现神圣之地的持续效果，包括地形维护、角色影响和视觉特效。

**关键方法**：
- `evolve()`: 每回合执行的持续逻辑
- `use(BlobEmitter)`: 设置粒子发射器
- `tileDesc()`: 返回地形描述文本

### 持续机制

**地形维护**：
- 每回合有概率将普通草地升级为高草（HIGH_GRASS）或犁过的草地（FURROWED_GRASS）
- 升级概率：10% + 10% × 天赋等级
- 火焰会破坏神圣之地

**持续角色影响**：
- **盟友**：每回合获得1点治疗或护盾
- **敌人**：每回合受到残废效果（Cripple），持续1回合

**资源管理**：
- 使用HallowedFurrowTracker计数器追踪总草地产量
- 最多100回合后开始自动降级为犁过的草地

---

## 特殊机制

### 地形转换系统

- **基础转换**：空地→草地（立即）
- **持续升级**：草地→高草/犁过的草地（概率性）
- **破坏机制**：火焰会完全摧毁神圣之地
- **视觉反馈**：不同草地类型有不同的粒子特效

### 阵营差异化

- **盟友支援**：
  - 优先治疗，溢出转护盾
  - 持续每回合微量恢复
- **敌人控制**：
  - 照亮效果提升命中率
  - 根须/残废效果限制移动
  - 飞行敌人免疫根须但受照亮影响

### 生命链接协同

- **智能包含**：如果英雄被影响而盟友没有，自动包含盟友
- **反向包含**：如果盟友被影响而英雄没有，自动包含英雄
- **效果同步**：确保生命链接双方获得相同的区域效果

---

## 使用示例

### 基本施法

```java
// 施放神圣之地
if (hero.hasTalent(Talent.HALLOWED_GROUND)) {
    // 玩家选择目标位置后自动调用
    HallowedGround.INSTANCE.onTargetSelected(tome, hero, targetPos);
}
```

### 战术部署

```java
// 在团队战斗前预设神圣之地
// 选择能覆盖最多盟友和敌人的位置
HallowedGround.INSTANCE.onTargetSelected(tome, hero, strategicPosition);
```

### 效果监控

```java
// 检查神圣之地是否仍然存在
HallowedGround.HallowedTerrain terrain = 
    (HallowedGround.HallowedTerrain) Dungeon.level.blobs.get(HallowedGround.HallowedTerrain.class);
if (terrain != null && terrain.volume > 0) {
    // 神圣之地仍然活跃
}
```

---

## 注意事项

### 平衡性考虑

1. **中等消耗**：2点充能消耗合理，可以定期使用
2. **范围投资**：天赋等级显著影响范围和效果强度
3. **持久价值**：一次施放提供20回合的持续效果

### 特殊机制

1. **火焰脆弱性**：神圣之地容易被火焰破坏，需注意环境
2. **地形限制**：只能在可通行地形上创建，固体地形无效
3. **视野要求**：目标必须在英雄视野范围内

### 技术限制

1. **性能影响**：大范围神圣之地可能对低端设备造成轻微性能影响
2. **Blob管理**：需要正确处理Blob的生命周期和序列化
3. **粒子特效**：大量粒子特效需要注意内存使用

---

## 最佳实践

### 战斗策略

- **区域控制**：在狭窄通道或房间入口创建神圣之地，限制敌人移动
- **团队支援**：在团队聚集区域施放，最大化治疗/护盾效果
- **持久防御**：在长期战斗区域预设，提供持续支援

### 天赋搭配

```java
// 推荐的天赋组合
if (hero.hasTalent(Talent.HALLOWED_GROUND) && 
    hero.pointsInTalent(Talent.HALLOWED_GROUND) >= 2 &&
    hero.hasTalent(Talent.LIFE_LINK)) {
    // 大范围神圣之地配合生命链接，为整个团队提供完美支援
}
```

### 职业优化

- **祭司流派**：配合其他治疗法术，形成完整的区域支援体系
- **圣骑士流派**：在前线创建神圣之地，增强近战生存能力
- **通用策略**：避免在易燃区域使用，防止被火焰快速摧毁

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `TargetedClericSpell` | 父类 | 目标选择法术基类 |
| `Blob` | 父类 | 持续效果Blob基类 |
| `Terrain` | 依赖 | 地形类型系统 |
| `PathFinder` | 依赖 | 路径查找和距离计算 |
| `Barrier` | 效果 | 护盾Buff系统 |
| `Roots` | 效果 | 根须控制效果 |
| `LifeLinkSpell` | 协同 | 生命链接法术，提供协同效果 |
| `PowerOfMany` | 依赖 | 力量合一天赋，提供盟友检测 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `spells.hallowedground.name` | "神圣之地" | 法术名称 |
| `spells.hallowedground.desc` | "在目标周围%d格范围内创造神圣之地。盟友在此区域内获得治疗和护盾，敌人被照亮并受到移动限制。" | 法术描述 |
| `spells.hallowedground.invalid_target` | "无效目标" | 目标无效错误提示 |
| `spells.hallowedground.charge_cost` | "%d 充能" | 充能消耗提示 |
| `blobs.hallowedterrain.desc` | "这片土地被神圣力量祝福，为盟友提供庇护，对敌人施加限制。" | 地形描述 |