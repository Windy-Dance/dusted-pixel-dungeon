# Tengu - 天狗刺客

> **文件路径**: `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/Tengu.java`  
> **BOSS等级**: 第二关Boss (监狱关卡)  
> **继承关系**: `Mob` → `Tengu`

---

## 一、基本信息

| 属性 | 数值 | 说明 |
|------|------|------|
| **HP/HT** | 200 (普通) / 250 (强敌挑战) | 生命值上限 |
| **EXP** | 20 | 击杀经验值 |
| **防御技能** | 15 | 基础防御值 |
| **视野距离** | 12 | 高于普通怪物 |
| **近战攻击力** | 6-12 | `damageRoll()` 返回值 |
| **近战命中率** | 10 | 相邻目标时 |
| **远程命中率** | 20 | 非相邻目标时 |
| **伤害减免** | 0-5 | `drRoll()` 返回值 |
| **属性标签** | `Property.BOSS` | Boss类型怪物 |

---

## 二、类职责

`Tengu` 类实现了游戏中第二关（监狱关卡）的Boss战逻辑，负责：

1. **双阶段战斗系统** - 第一阶段在小房间战斗，第二阶段在竞技场战斗
2. **位移机制** - 受伤后瞬移跳跃，增加战斗难度
3. **三种技能系统** - 炸弹、火焰、电击技能，第二阶段启用
4. **陷阱放置** - 第一阶段在房间内放置陷阱
5. **掉落物管理** - 死亡后掉落天狗面具（用于选择子职业）
6. **Boss血条控制** - 血量低于50%时显示出血效果

---

## 三、类关系图

```
┌─────────────────────────────────────────────────────────────────┐
│                         Tengu                                   │
│  (第二关Boss - 天狗刺客)                                         │
├─────────────────────────────────────────────────────────────────┤
│ 继承:                                                           │
│   Mob → Tengu                                                   │
├─────────────────────────────────────────────────────────────────┤
│ 关联类:                                                         │
│   PrisonBossLevel     - 关卡状态机控制战斗阶段                   │
│   TengusMask          - 死亡掉落物，用于选择子职业               │
│   BossHealthBar       - Boss血条UI                              │
│   LockedFloor         - 锁层机制                                │
├─────────────────────────────────────────────────────────────────┤
│ 内部类 (技能系统):                                               │
│   Hunting             - 自定义AI行为                            │
│   BombAbility         - 炸弹技能 (Buff + BombItem)              │
│   FireAbility         - 火焰技能 (Buff + FireBlob)              │
│   ShockerAbility      - 电击技能 (Buff + ShockerBlob + ShockerItem) │
└─────────────────────────────────────────────────────────────────┘
```

---

## 四、静态常量表

### 技能类型常量

| 常量名 | 值 | 说明 |
|--------|-----|------|
| `BOMB_ABILITY` | 0 | 炸弹技能标识 |
| `FIRE_ABILITY` | 1 | 火焰技能标识 |
| `SHOCKER_ABILITY` | 2 | 电击技能标识 |

### Bundle存储键

| 常量名 | 值 | 说明 |
|--------|-----|------|
| `LAST_ABILITY` | "last_ability" | 上次使用的技能 |
| `ABILITIES_USED` | "abilities_used" | 已使用技能次数 |
| `ARENA_JUMPS` | "arena_jumps" | 竞技场跳跃次数 |
| `ABILITY_COOLDOWN` | "ability_cooldown" | 技能冷却时间 |

---

## 五、实例字段表

| 字段名 | 类型 | 初始值 | 说明 |
|--------|------|--------|------|
| `loading` | `boolean` | `false` | 加载状态标记，防止加载时添加Buff |
| `lastAbility` | `int` | `-1` | 上次使用的技能ID |
| `abilitiesUsed` | `int` | `0` | 已使用技能总次数 |
| `arenaJumps` | `int` | `0` | 竞技场阶段跳跃次数 |
| `abilityCooldown` | `int` | `2` | 技能冷却计数器 |
| `throwingChar` | `static Char` | - | 投掷物品时的角色引用 |

### 免疫列表

```java
immunities.add(Roots.class);      // 免疫定身
immunities.add(Blindness.class);  // 免疫致盲
immunities.add(Dread.class);      // 免疫恐惧
immunities.add(Terror.class);     // 免疫惊吓
```

---

## 六、方法详解

### 6.1 核心属性方法

#### `damageRoll()`
```java
@Override
public int damageRoll() {
    return Random.NormalIntRange(6, 12);
}
```
**职责**: 返回天狗的基础近战伤害范围（6-12点）。

---

#### `attackSkill(Char target)`
```java
@Override
public int attackSkill(Char target) {
    if (Dungeon.level.adjacent(pos, target.pos)){
        return 10;  // 近战命中率较低
    } else {
        return 20;  // 远程命中率较高
    }
}
```
**职责**: 根据目标距离返回不同的命中率。天狗擅长远程攻击。

---

#### `drRoll()`
```java
@Override
public int drRoll() {
    return super.drRoll() + Random.NormalIntRange(0, 5);
}
```
**职责**: 返回伤害减免值（0-5点），叠加父类的DR。

---

### 6.2 伤害处理与阶段转换

#### `damage(int dmg, Object src)`

**核心逻辑**:
1. **血量分段保护**: 将血量分为8段，每次受伤最多下降一段
2. **锁层时间增加**: 根据伤害值延长锁层时间
3. **阶段转换**: 
   - HP ≤ 50% → 触发第二阶段
   - HP = 0 且在竞技场 → 死亡
4. **跳跃触发**: 血量下降一个分段时触发跳跃

```java
// 血量分段计算
int hpBracket = HT / 8;
int curbracket = HP / hpBracket;

// 防止一次受伤跨多段
if (HP <= (curbracket-1)*hpBracket){
    HP = (curbracket-1)*hpBracket + 1;
}

// 第一阶段结束（血量降至50%）
if (state == PrisonBossLevel.State.FIGHT_START && HP <= HT/2){
    HP = (HT/2);  // 锁定血量
    yell(Messages.get(this, "interesting"));
    ((PrisonBossLevel)Dungeon.level).progress();
    BossHealthBar.bleed(true);  // 血条显示出血
}
```

---

### 6.3 跳跃机制

#### `jump()`

**职责**: 天狗瞬移到新位置。

**第一阶段跳跃逻辑**:
```java
// 在天狗房间内随机选择位置
// 避免距离敌人/英雄太近（≤3.5格）
do {
    newPos = ((PrisonBossLevel)Dungeon.level).randomTenguCellPos();
    tries--;
} while (tries > 0 && (level.trueDistance(newPos, enemy.pos) <= 3.5f
        || level.trueDistance(newPos, Dungeon.hero.pos) <= 3.5f
        || Actor.findChar(newPos) != null));

// 放置陷阱
float fill = 0.9f - 0.5f*((HP-(HT/2f))/(HT/2f));
level.placeTrapsInTenguCell(fill);
```

**第二阶段跳跃逻辑**:
```java
// 在竞技场内选择位置
// 距离敌人/英雄/当前位置都必须在5-7格之间
do {
    newPos = Random.Int(level.length());
} while (tries > 0 &&
        (level.solid[newPos] ||
         level.distance(newPos, enemy.pos) < 5 ||
         level.distance(newPos, enemy.pos) > 7 ||
         ...));

// 累计跳跃次数
if (arenaJumps < 4) arenaJumps++;
```

---

### 6.4 技能系统

#### `canUseAbility()`
```java
public boolean canUseAbility(){
    // 第一阶段不使用技能
    if (HP > HT/2) return false;
    
    // 已达到目标使用次数
    if (abilitiesUsed >= targetAbilityUses()) return false;
    
    // 冷却逻辑
    abilityCooldown--;
    
    // 追赶机制：如果落后太多，加快技能使用
    if (targetAbilityUses() - abilitiesUsed >= 4 && !Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
        abilityCooldown = 0;  // 立即使用
    }
    
    return abilityCooldown == 0;
}
```

#### `targetAbilityUses()`
```java
private int targetAbilityUses(){
    // 基础1次 + 每次跳跃2次
    int targetAbilityUses = 1 + 2*arenaJumps;
    // 第3、4次跳跃额外各加1次
    targetAbilityUses += Math.max(0, arenaJumps-2);
    return targetAbilityUses;
}
```

**跳跃次数与目标技能使用次数对照表**:

| arenaJumps | targetAbilityUses |
|------------|-------------------|
| 0 | 1 |
| 1 | 3 |
| 2 | 5 |
| 3 | 8 |
| 4 | 11 |

#### `useAbility()`

**技能选择逻辑**:
```java
// 前两次技能固定
if (abilitiesUsed == 0) abilityToUse = BOMB_ABILITY;      // 第一次必用炸弹
else if (abilitiesUsed == 1) abilityToUse = SHOCKER_ABILITY;  // 第二次必用电击

// 强敌挑战模式不使用火焰技能
else if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
    abilityToUse = Random.Int(2)*2;  // 只能选0或2
}

// 普通模式随机选择
else {
    abilityToUse = Random.Int(3);  // 0-2随机
}

// 避免连续使用相同技能（10%概率允许重复）
if (abilityToUse != lastAbility || Random.Int(10) == 0){
    // 执行技能...
}
```

---

### 6.5 炸弹技能 (BombAbility)

#### `throwBomb(Char thrower, Char target)`

**目标选择逻辑**:
- 选择目标周围8格中离投掷者最近的空位
- 避开放置炸弹的位置

**BombAbility.BombItem**:
- 无法被玩家拾取
- 投掷后生成BombAbility Buff
- 3回合倒计时后爆炸
- 爆炸范围：以炸弹为中心，距离≤2的非实心格子
- 伤害：`5 + 深度` 到 `10 + 深度*2`

```java
// 爆炸伤害计算
int dmg = Random.NormalIntRange(5 + Dungeon.scalingDepth(), 10 + Dungeon.scalingDepth() * 2);
dmg -= ch.drRoll();  // 减去目标DR
```

---

### 6.6 火焰技能 (FireAbility)

#### `throwFire(Char thrower, Char target)`

**机制**:
- 向目标方向喷射火焰
- 火焰沿指定方向及其左右偏移方向扩散
- 持续燃烧经过的角色

**FireBlob效果**:
- 对非天狗角色施加 `Burning` 状态
- 烧毁地面物品
- 点燃可燃地形
- 英雄被击中会失去Boss挑战徽章资格

---

### 6.7 电击技能 (ShockerAbility)

#### `throwShocker(Char thrower, Char target)`

**目标选择逻辑**:
- 选择目标周围8格中离投掷者≥2格的位置
- 避开已有电击器的位置（距离≥2）

**ShockerAbility机制**:
```java
// 交替电击对角线/正交方向
if (shockingOrdinals){
    // 电击正交方向（上下左右）
    new Lightning(shockerPos - width, shockerPos + width, null);  // 上下
    new Lightning(shockerPos - 1, shockerPos + 1, null);          // 左右
} else {
    // 电击对角线方向
    new Lightning(shockerPos - 1 - width, shockerPos + 1 + width, null);
    new Lightning(shockerPos - 1 + width, shockerPos + 1 - width, null);
}
```

**ShockerBlob效果**:
- 伤害：`2 + 深度` 点
- 每回合交替电击方向
- 英雄被击中可能死亡并记录死亡原因

---

### 6.8 死亡处理

#### `die(Object cause)`

```java
@Override
public void die(Object cause) {
    // 未选择子职业时掉落天狗面具
    if (Dungeon.hero.subClass == HeroSubClass.NONE) {
        Dungeon.level.drop(new TengusMask(), pos).sprite.drop();
    }
    
    GameScene.bossSlain();
    super.die(cause);
    
    Badges.validateBossSlain();
    if (Statistics.qualifiedForBossChallengeBadge){
        Badges.validateBossChallengeCompleted();
    }
    Statistics.bossScores[1] += 2000;  // Boss分数
    
    // 升级罗伊德信标
    LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
    if (beacon != null) {
        beacon.upgrade();
    }
    
    yell(Messages.get(this, "defeated"));
}
```

---

### 6.9 AI行为 (Hunting内部类)

```java
private class Hunting extends Mob.Hunting {
    @Override
    public boolean act(boolean enemyInFOV, boolean justAlerted) {
        enemySeen = enemyInFOV;
        
        if (enemyInFOV && !isCharmedBy(enemy) && canAttack(enemy)) {
            // 优先使用技能
            if (canUseAbility()) {
                return useAbility();
            }
            // 否则普通攻击
            recentlyAttackedBy.clear();
            target = enemy.pos;
            return doAttack(enemy);
        } else {
            return handleUnreachableTarget(enemyInFOV, justAlerted);
        }
    }
    
    @Override
    protected boolean handleUnreachableTarget(boolean enemyInFOV, boolean justAlerted) {
        // 尝试切换目标
        Char oldEnemy = enemy;
        enemy = null;
        enemy = chooseEnemy();
        
        // 仍可使用技能
        if (canUseAbility()) {
            return useAbility();
        }
        
        spend(TICK);
        return true;
    }
}
```

---

## 七、使用示例

### 7.1 创建天狗实例

```java
// 通常由PrisonBossLevel创建
Tengu tengu = new Tengu();
tengu.pos = level.pointToCell(tenguCellCenter);
GameScene.add(tengu);
```

### 7.2 检查天狗状态

```java
// 获取关卡状态
PrisonBossLevel level = (PrisonBossLevel) Dungeon.level;
PrisonBossLevel.State state = level.state();

// 状态枚举
// START        - 初始状态，天狗在笼中
// FIGHT_START  - 第一阶段战斗
// FIGHT_PAUSE  - 阶段转换中
// FIGHT_ARENA  - 第二阶段战斗
// WON          - 战斗胜利
```

### 7.3 手动触发技能

```java
// 天狗炸弹技能
Tengu.throwBomb(tengu, Dungeon.hero);

// 天狗火焰技能
Tengu.throwFire(tengu, Dungeon.hero);

// 天狗电击技能
Tengu.throwShocker(tengu, Dungeon.hero);
```

---

## 八、注意事项

### 8.1 血量分段机制

天狗有特殊的血量保护机制：
- 血量分为8段（每段 = HT/8）
- 单次伤害不能让血量跨越多个分段
- 这防止玩家一击秒杀Boss

```java
// 段位保护代码
if (HP <= (curbracket-1)*hpBracket){
    HP = (curbracket-1)*hpBracket + 1;
}
```

### 8.2 离开关卡时的免疫

当天狗不在关卡中时（如跳跃中），免疫所有伤害和Buff：

```java
@Override
public boolean add(Buff buff) {
    if (Actor.chars().contains(this) || buff instanceof Doom || loading){
        return super.add(buff);
    }
    return false;
}

@Override
public void damage(int dmg, Object src) {
    if (!Dungeon.level.mobs.contains(this)){
        return;  // 免疫伤害
    }
    // ...
}
```

### 8.3 强敌挑战模式差异

开启 `Challenges.STRONGER_BOSSES` 时：
- HP增加到250
- 技能冷却更短
- 不使用火焰技能
- 使用技能后额外追加火焰

### 8.4 Boss分数机制

```java
Statistics.bossScores[1] += 2000;  // 击杀基础分

// 被技能击中会扣分
if (ch == Dungeon.hero){
    Statistics.qualifiedForBossChallengeBadge = false;
    Statistics.bossScores[1] -= 100;
}
```

---

## 九、最佳实践

### 9.1 修改天狗数值

```java
// 在实例初始化块中修改
{
    HP = HT = 300;  // 自定义血量
    defenseSkill = 20;  // 自定义防御
}
```

### 9.2 添加新技能

1. 定义新的技能常量
2. 在 `useAbility()` 中添加选择逻辑
3. 创建新的 Ability 内部类（继承 Buff）
4. 创建对应的 Item 和 Blob（如需要）

### 9.3 自定义跳跃行为

重写 `jump()` 方法以改变瞬移逻辑：

```java
@Override
private void jump() {
    // 自定义跳跃逻辑
    // 例如：跳跃后留下陷阱
}
```

### 9.4 序列化注意事项

确保新增字段在 `storeInBundle()` 和 `restoreFromBundle()` 中正确处理：

```java
@Override
public void storeInBundle(Bundle bundle) {
    super.storeInBundle(bundle);
    bundle.put("new_field", newField);
}

@Override
public void restoreFromBundle(Bundle bundle) {
    super.restoreFromBundle(bundle);
    newField = bundle.getInt("new_field");
}
```

---

## 十、调试信息

### 战斗流程图

```
┌─────────────────────────────────────────────────────────────────┐
│                      天狗战斗流程                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  [START] ──发现英雄──> [FIGHT_START] ──HP≤50%──> [FIGHT_PAUSE] │
│                         │                        │              │
│                    普通攻击+陷阱放置          地图转换           │
│                    跳跃瞬移                      │              │
│                                                 ▼              │
│                                          [FIGHT_ARENA]         │
│                                                 │              │
│                                          技能系统启用           │
│                                          炸弹/火焰/电击         │
│                                          跳跃+技能组合          │
│                                                 │              │
│                                             HP=0               │
│                                                 │              │
│                                                 ▼              │
│                                            [WON]               │
│                                          掉落天狗面具           │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 技能使用时序

```
第1次技能: 炸弹 (固定)
第2次技能: 电击 (固定)
第3次+:   随机 (炸弹/火焰/电击)，避免连续相同

强敌挑战模式:
- 不使用火焰技能
- 每次技能后追加火焰
```

---

*文档版本: 1.0*  
*最后更新: 2026-03-26*  
*基于 Shattered Pixel Dungeon 源码分析*