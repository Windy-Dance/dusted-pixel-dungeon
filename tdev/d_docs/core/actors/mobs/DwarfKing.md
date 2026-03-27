# DwarfKing - 矮人国王

> **文件路径**: `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/DwarfKing.java`  
> **BOSS等级**: 第四关Boss (城市关卡)  
> **继承关系**: `Mob` → `DwarfKing`

---

## 一、基本信息

| 属性 | 数值 | 说明 |
|------|------|------|
| **HP/HT** | 300 (普通) / 450 (强敌挑战) | 生命值上限 |
| **EXP** | 40 | 击杀经验值 |
| **防御技能** | 22 | 基础防御值 |
| **近战攻击力** | 15-25 | `damageRoll()` 返回值 |
| **命中率** | 26 | `attackSkill()` 返回值 |
| **伤害减免** | 0-10 | `drRoll()` 返回值 |
| **属性标签** | `Property.BOSS`, `Property.UNDEAD` | Boss类型、亡灵属性 |

---

## 二、类职责

`DwarfKing` 类实现了游戏中第四关（城市关卡）的Boss战逻辑，负责：

1. **三阶段战斗系统** - 第一阶段主动战斗，第二阶段召唤+护盾，第三阶段狂暴
2. **召唤机制** - 召唤DK系列随从（食尸鬼、武僧、术士、魔像）
3. **生命链接技能** - 将伤害转移到随从身上
4. **传送交换技能** - 与远处的随从交换位置
5. **护盾机制** - 第二阶段拥有等于最大生命值的护盾
6. **掉落物管理** - 死亡后掉落矮人王冠（用于升级护甲）
7. **Boss分数系统** - 特殊的Boss挑战徽章判定

---

## 三、类关系图

```
┌─────────────────────────────────────────────────────────────────┐
│                         DwarfKing                                │
│  (第四关Boss - 矮人国王)                                         │
├─────────────────────────────────────────────────────────────────┤
│ 继承:                                                            │
│   Mob → DwarfKing                                                │
├─────────────────────────────────────────────────────────────────┤
│ 关联类:                                                          │
│   CityBossLevel       - 关卡提供召唤位置和王座                   │
│   KingsCrown          - 死亡掉落物，用于升级护甲                 │
│   BossHealthBar       - Boss血条UI                              │
│   LockedFloor         - 锁层机制                                │
│   LifeLink            - 生命链接Buff                            │
│   Barrier             - 护盾机制                                │
├─────────────────────────────────────────────────────────────────┤
│ 内部类 (随从系统):                                               │
│   DKGhoul             - 矮人国王食尸鬼                          │
│   DKMonk              - 矮人国王武僧                            │
│   DKWarlock           - 矮人国王术士                            │
│   DKGolem             - 矮人国王魔像                            │
├─────────────────────────────────────────────────────────────────┤
│ 内部类 (机制类):                                                 │
│   Summoning           - 召唤过程Buff，延迟生成随从               │
│   KingDamager         - 伤害传递Buff，随从死亡时伤害国王         │
│   DKBarrior           - 国王护盾Buff，每回合增长                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 四、静态常量表

### 技能类型常量

| 常量名 | 值 | 说明 |
|--------|-----|------|
| `NONE` | 0 | 无技能（初始状态） |
| `LINK` | 1 | 生命链接技能 |
| `TELE` | 2 | 传送交换技能 |

### Bundle存储键

| 常量名 | 值 | 说明 |
|--------|-----|------|
| `PHASE` | "phase" | 当前战斗阶段 |
| `SUMMONS_MADE` | "summons_made" | 已召唤随从数量 |
| `SUMMON_CD` | "summon_cd" | 召唤冷却时间 |
| `ABILITY_CD` | "ability_cd" | 技能冷却时间 |
| `LAST_ABILITY` | "last_ability" | 上次使用的技能 |

---

## 五、实例字段表

### 阶段与状态字段

| 字段名 | 类型 | 初始值 | 说明 |
|--------|------|--------|------|
| `phase` | int | 1 | 当前战斗阶段（1/2/3） |
| `summonsMade` | int | 0 | 已召唤随从计数器 |
| `summonCooldown` | float | 0 | 召唤冷却计数器 |
| `abilityCooldown` | float | 0 | 技能冷却计数器 |
| `lastAbility` | int | 0 | 上次使用的技能类型 |

### 冷却时间常量

| 字段名 | 类型 | 普通模式 | 强Boss模式 | 说明 |
|--------|------|----------|------------|------|
| `MIN_COOLDOWN` | int | 10 | 8 | 最小技能冷却回合 |
| `MAX_COOLDOWN` | int | 14 | 10 | 最大技能冷却回合 |

### 基础属性（实例初始化块）

| 属性 | 值 | 说明 |
|------|-----|------|
| `HP` / `HT` | 300 (普通) / 450 (强Boss) | 生命值 |
| `EXP` | 40 | 击杀经验 |
| `defenseSkill` | 22 | 防御技能值 |
| `spriteClass` | KingSprite.class | 精灵类 |
| `properties` | BOSS, UNDEAD | 实体属性 |

---

## 六、方法详解

### 6.1 核心属性方法

#### `damageRoll()`
```java
@Override
public int damageRoll() {
    return Random.NormalIntRange(15, 25);
}
```
**职责**: 返回矮人国王的基础近战伤害范围（15-25点）。

---

#### `attackSkill(Char target)`
```java
@Override
public int attackSkill(Char target) {
    return 26;
}
```
**职责**: 返回攻击命中率（固定26）。

---

#### `drRoll()`
```java
@Override
public int drRoll() {
    return super.drRoll() + Random.NormalIntRange(0, 10);
}
```
**职责**: 返回伤害减免值（0-10点），叠加父类的DR。

---

### 6.2 AI行为主逻辑 - `act()`

#### 第一阶段 (phase == 1)

```java
if (phase == 1) {
    // 召唤逻辑
    if (summonCooldown <= 0 && summonSubject(Dungeon.isChallenged(...) ? 2 : 3)) {
        summonsMade++;
        summonCooldown += Random.NormalIntRange(MIN_COOLDOWN, MAX_COOLDOWN);
    } else if (summonCooldown > 0) {
        summonCooldown--;
    }

    // 技能逻辑
    if (abilityCooldown <= 0) {
        // 技能选择算法
        if (lastAbility == NONE) {
            lastAbility = Random.Int(2) == 0 ? LINK : TELE;  // 50/50
        } else if (lastAbility == LINK) {
            lastAbility = Random.Int(8) == 0 ? LINK : TELE;  // 12.5%重复
        } else {
            lastAbility = Random.Int(8) != 0 ? LINK : TELE;  // 87.5%使用LINK
        }
        
        // 执行技能
        if (lastAbility == LINK && lifeLinkSubject()) { ... }
        else if (teleportSubject()) { ... }
    }
}
```

**第一阶段特点**：
- 正常移动和攻击
- 定期召唤随从
- 使用生命链接/传送交换技能
- HP降到阈值（普通50/强Boss100）时进入第二阶段

---

#### 第二阶段 (phase == 2)

```java
else if (phase == 2) {
    if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
        // 强Boss模式：波次召唤
        // Wave 1: 6只食尸鬼（summonsMade < 6）
        // Wave 2: 6只食尸鬼 + 1武僧/术士（shielding ≤ 300, summonsMade < 12）
        // Wave 3: 混合召唤（shielding ≤ 150, summonsMade < 18）
    } else {
        // 普通模式：波次召唤
        // Wave 1: 4只食尸鬼（summonsMade < 4）
        // Wave 2: 4只食尸鬼 + 1武僧/术士（shielding ≤ 200, summonsMade < 8）
        // Wave 3: 混合召唤（shielding ≤ 100, summonsMade < 12）
    }
}
```

**第二阶段特点**：
- 矮人国王固定在王座上（IMMOVABLE属性）
- 拥有等于HT的护盾（DKBarrior）
- 波次召唤随从
- 随从死亡时伤害国王护盾
- 护盾归零时进入第三阶段

---

#### 第三阶段 (phase == 3)

```java
else if (phase == 3 && buffs(Summoning.class).size() < 4) {
    if (summonSubject(Dungeon.isChallenged(...) ? 2 : 3)) {
        summonsMade++;
    }
}

return super.act();  // 继续正常移动攻击
```

**第三阶段特点**：
- 恢复移动能力
- 继续召唤随从（最多4只同时召唤中）
- 所有伤害转为延迟伤害（类似粘滞符文）
- 播放最终Boss音乐

---

### 6.3 召唤机制 - `summonSubject()`

#### 普通模式召唤规则

```java
// 每4次召唤中，第4次是武僧或术士
if (summonsMade % 4 == 3) {
    return summonSubject(delay, Random.Int(2) == 0 ? DKMonk.class : DKWarlock.class);
} else {
    return summonSubject(delay, DKGhoul.class);  // 其他是食尸鬼
}
```

| 召唤次数 | 随从类型 |
|----------|----------|
| 0, 1, 2 | 食尸鬼 |
| 3 | 武僧/术士（随机） |
| 4, 5, 6 | 食尸鬼 |
| 7 | 武僧/术士（随机） |
| ... | 循环 |

#### 强Boss模式召唤规则

```java
// 每3次召唤中，第3次是武僧/术士或魔像
if (summonsMade % 3 == 2) {
    if (summonsMade % 9 == 8) {
        return summonSubject(delay, DKGolem.class);  // 每9次出魔像
    } else {
        return summonSubject(delay, Random.Int(2) == 0 ? DKMonk.class : DKWarlock.class);
    }
} else {
    return summonSubject(delay, DKGhoul.class);
}
```

| 召唤次数 | 随从类型 |
|----------|----------|
| 0, 1 | 食尸鬼 |
| 2 | 武僧/术士（随机） |
| 3, 4 | 食尸鬼 |
| 5 | 武僧/术士（随机） |
| 6, 7 | 食尸鬼 |
| 8 | 魔像 |
| ... | 循环 |

---

### 6.4 Summoning内部类 - 延迟召唤

```java
public static class Summoning extends Buff {
    private int delay;                      // 延迟回合数
    private int pos;                        // 召唤位置
    private Class<? extends Mob> summon;    // 召唤类型
    private Emitter particles;              // 粒子效果
    
    @Override
    public boolean act() {
        delay--;
        if (delay <= 0) {
            // 生成随从
            Mob m = Reflection.newInstance(summon);
            m.pos = pos;
            m.maxLvl = -2;  // 不提供经验
            GameScene.add(m);
            m.state = m.HUNTING;
            
            // 第二阶段随从附加KingDamager
            if (((DwarfKing)target).phase == 2) {
                Buff.affect(m, KingDamager.class);
            }
            detach();
        }
        return true;
    }
}
```

**粒子效果对照表**：

| 随从类型 | 粒子效果 | 音效 |
|----------|----------|------|
| DKGolem | SparkParticle（电火花） | CHARGEUP |
| DKWarlock | ShadowParticle（暗影） | CURSED |
| DKMonk | ElmoParticle（火焰） | BURNING |
| DKGhoul | Speck.BONE（骨头） | BONES |

---

### 6.5 生命链接技能 - `lifeLinkSubject()`

```java
private boolean lifeLinkSubject() {
    Mob furthest = null;
    
    // 找到距离最远且未被链接的随从
    for (Mob m : getSubjects()) {
        boolean alreadyLinked = false;
        for (LifeLink l : m.buffs(LifeLink.class)) {
            if (l.object == id()) alreadyLinked = true;
        }
        if (!alreadyLinked) {
            if (furthest == null || Dungeon.level.distance(pos, furthest.pos) < Dungeon.level.distance(pos, m.pos)) {
                furthest = m;
            }
        }
    }
    
    if (furthest != null) {
        // 双向链接
        Buff.append(furthest, LifeLink.class, 100f).object = id();
        Buff.append(this, LifeLink.class, 100f).object = furthest.id();
        
        // 视觉效果
        sprite.parent.add(new Beam.HealthRay(sprite.destinationCenter(), furthest.sprite.destinationCenter()));
        Sample.INSTANCE.play(Assets.Sounds.RAY);
        yell(Messages.get(this, "lifelink_" + Random.IntRange(1, 2)));
        return true;
    }
    return false;
}
```

**机制说明**：
- 选择距离国王最远的未被链接随从
- 建立双向生命链接
- 链接持续100回合
- 显示红色光线效果

---

### 6.6 传送交换技能 - `teleportSubject()`

```java
private boolean teleportSubject() {
    if (enemy == null) return false;
    
    Mob furthest = null;
    // 找到距离最远的随从
    
    // 计算国王新位置（敌人反方向）
    Ballistica trajectory = new Ballistica(enemy.pos, pos, Ballistica.STOP_TARGET);
    int targetCell = trajectory.path.get(trajectory.dist + 1);
    
    // 如果反方向不可用，选择最远邻格
    if (Actor.findChar(targetCell) != null || Dungeon.level.solid[targetCell]) {
        // 选择离敌人最远的邻格
    }
    
    // 国王移动到新位置
    Actor.add(new Pushing(this, pos, bestPos));
    pos = bestPos;
    
    // 随从传送到敌人旁边
    ScrollOfTeleportation.appear(furthest, bestPos);
    
    return true;
}
```

**机制说明**：
- 选择距离最远的随从
- 国王被推到敌人反方向
- 随从传送到敌人旁边
- 创造突袭机会

---

### 6.7 伤害处理与阶段转换 - `damage()`

```java
@Override
public void damage(int dmg, Object src) {
    // Boss挑战徽章判定
    // 徒手攻击才能保持资格
    if (src == Dungeon.hero && (!RingOfForce.fightingUnarmed(Dungeon.hero) || ...)) {
        Statistics.qualifiedForBossChallengeBadge = false;
    }
    
    // 第二阶段无敌（除了KingDamager）
    if (isInvulnerable(src.getClass())) {
        super.damage(dmg, src);
        return;
    }
    
    // 第三阶段伤害延迟（粘滞符文效果）
    if (phase == 3 && !(src instanceof Viscosity.DeferedDamage)) {
        if (dmg >= 0) {
            Viscosity.DeferedDamage deferred = Buff.affect(this, Viscosity.DeferedDamage.class);
            deferred.extend(dmg);
        }
        return;
    }
    
    // 第一阶段结束时进入第二阶段
    if (phase == 1) {
        int dmgTaken = preHP - HP;
        abilityCooldown -= dmgTaken / 8f;  // 受伤加速技能冷却
        summonCooldown -= dmgTaken / 8f;
        
        if (HP <= threshold) {  // 普通50 / 强Boss100
            HP = threshold;
            ScrollOfTeleportation.appear(this, CityBossLevel.throne);
            properties.add(Property.IMMOVABLE);
            phase = 2;
            summonsMade = 0;
            Buff.affect(this, DKBarrior.class).setShield(HT);  // 护盾=最大生命
            // 清除所有随从和召唤中的
        }
    }
    
    // 第二阶段结束（护盾归零）
    else if (phase == 2 && shielding() == 0) {
        properties.remove(Property.IMMOVABLE);
        phase = 3;
        // 切换到最终音乐
        Music.INSTANCE.play(Assets.Music.CITY_BOSS_FINALE, true);
        BossHealthBar.bleed(true);
    }
}
```

---

### 6.8 DKBarrior内部类 - 护盾机制

```java
public static class DKBarrior extends Barrier {
    @Override
    public boolean act() {
        incShield();  // 每回合护盾增长
        return super.act();
    }
    
    @Override
    public int icon() {
        return BuffIndicator.NONE;  // 不显示图标
    }
}
```

**护盾特点**：
- 初始值等于最大生命值（HT）
- 每回合自动增长
- 不显示Buff图标
- 护盾归零触发第三阶段

---

### 6.9 KingDamager内部类 - 伤害传递

```java
public static class KingDamager extends Buff {
    {
        revivePersists = true;  // 复活后保留
    }
    
    @Override
    public boolean act() {
        if (target.alignment != Alignment.ENEMY) {
            detach();  // 非敌方时移除
        }
        spend(TICK);
        return true;
    }
    
    @Override
    public void detach() {
        super.detach();
        // 随从死亡时伤害国王
        for (Mob m : Dungeon.level.mobs) {
            if (m instanceof DwarfKing) {
                int damage = m.HT / (Dungeon.isChallenged(...) ? 18 : 12);
                m.damage(damage, this);
            }
        }
    }
}
```

**机制说明**：
- 第二阶段随从附加此Buff
- 随从死亡时对国王造成HT/12（普通）或HT/18（强Boss）伤害
- 这是最有效的破盾方式

---

### 6.10 死亡处理 - `die()`

```java
@Override
public void die(Object cause) {
    GameScene.bossSlain();
    super.die(cause);
    
    // 掉落矮人王冠
    if (pos == CityBossLevel.throne) {
        Dungeon.level.drop(new KingsCrown(), pos + Dungeon.level.width()).sprite.drop(pos);
    } else {
        Dungeon.level.drop(new KingsCrown(), pos).sprite.drop();
    }
    
    // 徽章验证
    Badges.validateBossSlain();
    if (Statistics.qualifiedForBossChallengeBadge) {
        Badges.validateBossChallengeCompleted();
    }
    Statistics.bossScores[3] += 4000;  // Boss分数
    
    // 解锁楼层
    Dungeon.level.unseal();
    
    // 清除所有随从
    for (Mob m : getSubjects()) {
        m.die(null);
    }
    
    // 升级罗伊德信标
    LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
    if (beacon != null) {
        beacon.upgrade();
    }
    
    // 清除降级效果
    if (Dungeon.hero.buff(Degrade.class) != null) {
        Dungeon.hero.buff(Degrade.class).detach();
    }
    
    yell(Messages.get(this, "defeated"));
}
```

---

## 七、随从类详解

### 7.1 DKGhoul - 矮人国王食尸鬼

```java
public static class DKGhoul extends Ghoul {
    {
        properties.add(Property.BOSS_MINION);
        state = HUNTING;  // 直接进入追击状态
    }
    
    @Override
    protected boolean act() {
        partnerID = -2;  // 禁用食尸鬼的伙伴机制
        return super.act();
    }
}
```

**特点**：基础随从，数量最多，无特殊行为修改。

---

### 7.2 DKMonk - 矮人国王武僧

```java
public static class DKMonk extends Monk {
    {
        properties.add(Property.BOSS_MINION);
        state = HUNTING;
    }
}
```

**特点**：继承自武僧，保留武僧的闪避和连击能力。

---

### 7.3 DKWarlock - 矮人国王术士

```java
public static class DKWarlock extends Warlock {
    {
        properties.add(Property.BOSS_MINION);
        state = HUNTING;
    }
    
    @Override
    protected void zap() {
        if (enemy == Dungeon.hero) {
            Statistics.bossScores[3] -= 400;  // 被击中扣分
        }
        super.zap();
    }
}
```

**特点**：继承自术士，保留远程暗影攻击能力，击中英雄扣Boss分。

---

### 7.4 DKGolem - 矮人国王魔像

```java
public static class DKGolem extends Golem {
    {
        properties.add(Property.BOSS_MINION);
        state = HUNTING;
    }
}
```

**特点**：仅在强Boss模式下出现，每9次召唤出现一次。

---

## 八、使用示例

### 8.1 创建矮人国王实例

```java
// 通常由CityBossLevel创建
DwarfKing king = new DwarfKing();
king.pos = level.throne;
GameScene.add(king);
```

### 8.2 检查Boss状态

```java
// 获取当前阶段
int currentPhase = king.phase;  // 1, 2, 或 3

// 检查护盾值
int shield = king.shielding();

// 检查是否在王座上
boolean onThrone = (king.pos == CityBossLevel.throne);
```

### 8.3 自定义Boss属性

```java
public class CustomDwarfKing extends DwarfKing {
    {
        HP = HT = 500;  // 自定义血量
        defenseSkill = 30;  // 自定义防御
    }
    
    @Override
    public int damageRoll() {
        return Random.NormalIntRange(20, 40);  // 自定义伤害
    }
}
```

---

## 九、注意事项

### 9.1 Boss挑战徽章判定

```java
// 保持徽章资格的条件：
// 1. 使用徒手攻击（不持武器）
// 2. 不受益于力量戒指
// 3. 不使用法杖攻击（闪电法杖除外）
// 4. 不使用牧师法术
```

### 9.2 第二阶段无敌机制

```java
@Override
public boolean isInvulnerable(Class effect) {
    if (effect == KingDamager.class) {
        return false;  // 随从死亡伤害可以穿透
    }
    return phase == 2 || super.isInvulnerable(effect);
}
```

**重要**：第二阶段只能通过击杀随从来破盾，直接攻击无效。

### 9.3 第三阶段伤害延迟

```java
// 第三阶段所有伤害转为延迟伤害
// 类似粘滞符文效果，伤害分摊到多回合
Viscosity.DeferedDamage deferred = Buff.affect(this, Viscosity.DeferedDamage.class);
deferred.extend(dmg);
```

### 9.4 强敌挑战模式差异

| 属性 | 普通模式 | 强Boss模式 |
|------|----------|------------|
| HP | 300 | 450 |
| 召唤间隔 | 3回合 | 2回合 |
| 技能冷却 | 10-14回合 | 8-10回合 |
| 第一阶段阈值 | 50 HP | 100 HP |
| 第二阶段波次 | 3波 | 3波（更多随从） |
| 随从类型 | 食尸鬼+武僧/术士 | 食尸鬼+武僧/术士+魔像 |

---

## 十、最佳实践

### 10.1 战斗策略

**第一阶段**：
- 优先击杀被生命链接的随从
- 注意国王的传送交换技能
- 受伤会加速国王的技能冷却

**第二阶段**：
- 专注击杀随从以破盾
- 每12/18只随从死亡破一层护盾
- 注意躲避术士的远程攻击

**第三阶段**：
- 伤害会被延迟，可以持续输出
- 随从仍有威胁，需要控制数量
- 最后阶段会播放特殊音乐

### 10.2 修改Boss数值

```java
// 在实例初始化块中修改
{
    HP = HT = 400;  // 自定义血量
    defenseSkill = 25;  // 自定义防御
    MIN_COOLDOWN = 8;  // 更短的技能冷却
    MAX_COOLDOWN = 12;
}
```

### 10.3 添加自定义随从

```java
public static class DKCustomMinion extends CustomMob {
    {
        properties.add(Property.BOSS_MINION);
        state = HUNTING;
    }
}

// 在summonSubject()中添加召唤逻辑
if (summonsMade % 5 == 4) {
    return summonSubject(delay, DKCustomMinion.class);
}
```

### 10.4 序列化注意事项

```java
@Override
public void storeInBundle(Bundle bundle) {
    super.storeInBundle(bundle);
    bundle.put(PHASE, phase);
    bundle.put(SUMMONS_MADE, summonsMade);
    bundle.put(SUMMON_CD, summonCooldown);
    bundle.put(ABILITY_CD, abilityCooldown);
    bundle.put(LAST_ABILITY, lastAbility);
}

@Override
public void restoreFromBundle(Bundle bundle) {
    super.restoreFromBundle(bundle);
    phase = bundle.getInt(PHASE);
    summonsMade = bundle.getInt(SUMMONS_MADE);
    summonCooldown = bundle.getFloat(SUMMON_CD);
    abilityCooldown = bundle.getFloat(ABILITY_CD);
    lastAbility = bundle.getInt(LAST_ABILITY);
    
    if (phase == 2) properties.add(Property.IMMOVABLE);
    BossHealthBar.assignBoss(this);
    if (phase == 3) BossHealthBar.bleed(true);
}
```

---

## 十一、调试信息

### 战斗流程图

```
┌─────────────────────────────────────────────────────────────────┐
│                      矮人国王战斗流程                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  [阶段1] 主动战斗                                               │
│     ├─ 正常移动攻击                                             │
│     ├─ 召唤随从（食尸鬼为主）                                    │
│     ├─ 使用生命链接/传送交换技能                                 │
│     └─ HP ≤ 50/100 时传送至王座                                 │
│               ↓                                                 │
│  [阶段2] 王座防御                                               │
│     ├─ 固定在王座，无法移动                                      │
│     ├─ 护盾 = 最大生命值                                        │
│     ├─ 波次召唤随从                                             │
│     │    Wave 1: 4/6 食尸鬼                                    │
│     │    Wave 2: 混合召唤                                       │
│     │    Wave 3: 最终波次                                       │
│     └─ 护盾归零时进入下一阶段                                    │
│               ↓                                                 │
│  [阶段3] 狂暴阶段                                               │
│     ├─ 恢复移动能力                                             │
│     ├─ 所有伤害延迟生效                                         │
│     ├─ 继续召唤随从                                             │
│     ├─ 播放最终Boss音乐                                         │
│     └─ 血条变红                                                 │
│               ↓                                                 │
│           [击杀]                                                │
│     ├─ 掉落矮人王冠                                             │
│     ├─ 解锁楼层                                                 │
│     └─ 清除所有随从                                             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 召唤时序

```
普通模式:
  第0-2次: 食尸鬼
  第3次:   武僧/术士（随机）
  第4-6次: 食尸鬼
  第7次:   武僧/术士（随机）
  ...

强Boss模式:
  第0-1次: 食尸鬼
  第2次:   武僧/术士（随机）
  第3-4次: 食尸鬼
  第5次:   武僧/术士（随机）
  第6-7次: 食尸鬼
  第8次:   魔像
  ...
```

---

*文档版本: 1.0*  
*最后更新: 2026-03-26*  
*基于 Shattered Pixel Dungeon 源码分析*