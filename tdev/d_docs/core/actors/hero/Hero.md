# Hero 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Hero.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero |
| **类类型** | class |
| **继承关系** | extends Char |
| **代码行数** | 2637 |

---

## 类职责

Hero 是游戏的**主角类**，代表玩家控制的角色。它扩展了 Char 类，添加了：

1. **职业系统**：英雄职业、子职业、护甲能力
2. **天赋系统**：多层级天赋树
3. **背包系统**：物品管理、装备穿戴
4. **行动系统**：玩家输入的处理和执行
5. **成长系统**：等级、经验、属性提升

---

## 4. 继承与协作关系

```mermaid
classDiagram
    class Hero {
        +HeroClass heroClass
        +HeroSubClass subClass
        +ArmorAbility armorAbility
        +ArrayList talents
        +Belongings belongings
        +int STR
        +int lvl
        +int exp
        +boolean ready
        +HeroAction curAction
        +boolean resting
        +live() void
        +updateHT(boolean) void
        +STR() int
        +shoot(Char, MissileWeapon) boolean
        +attackSkill(Char) int
        +defenseSkill(Char) int
        +damageRoll() int
        +speed() float
        +act() boolean
        +rest(boolean) void
        +busy() void
        +interrupt() void
        +resume() void
        +earnExp(int, Class) void
        +die(Object) void
        +hasTalent(Talent) boolean
        +pointsInTalent(Talent) int
        +upgradeTalent(Talent) void
    }
    
    class Char {
        <<abstract>>
        +int pos
        +int HP, HT
        +Alignment alignment
    }
    
    class HeroAction {
        <<abstract>>
        +int dst
    }
    
    class Belongings {
        +Weapon weapon
        +Armor armor
        +Bag backpack
    }
    
    Char <|-- Hero
    Hero +-- HeroAction
    Hero +-- Belongings
    Hero +-- HeroClass
    Hero +-- HeroSubClass
```

---

## 静态常量

| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `MAX_LEVEL` | int | 30 | 最大等级 |
| `STARTING_STR` | int | 10 | 初始力量 |
| `TIME_TO_REST` | float | 1f | 休息消耗时间 |
| `TIME_TO_SEARCH` | float | 2f | 搜索消耗时间 |

---

## 实例字段

### 职业相关

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `heroClass` | HeroClass | ROGUE | 英雄职业 |
| `subClass` | HeroSubClass | NONE | 英雄子职业 |
| `armorAbility` | ArmorAbility | null | 护甲能力 |
| `talents` | ArrayList&lt;LinkedHashMap&lt;Talent, Integer&gt;&gt; | new | 天赋列表 |
| `metamorphedTalents` | LinkedHashMap&lt;Talent, Talent&gt; | new | 变形天赋 |

### 属性

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `attackSkill` | int | 10 | 攻击技能值 |
| `defenseSkill` | int | 5 | 防御技能值 |
| `STR` | int | 10 | 力量值 |
| `awareness` | float | - | 警觉值 |
| `lvl` | int | 1 | 当前等级 |
| `exp` | int | 0 | 当前经验 |
| `HTBoost` | int | 0 | 生命值加成 |

### 状态

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `ready` | boolean | false | 是否准备好行动 |
| `resting` | boolean | false | 是否在休息 |
| `damageInterrupt` | boolean | true | 是否可被伤害打断 |
| `curAction` | HeroAction | null | 当前行动 |
| `lastAction` | HeroAction | null | 上次行动 |
| `attackTarget` | Char | null | 当前攻击目标 |

### 其他

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `belongings` | Belongings | 背包系统 |
| `visibleEnemies` | ArrayList&lt;Mob&gt; | 可见敌人列表 |
| `mindVisionEnemies` | ArrayList&lt;Mob&gt; | 心眼看到的敌人 |

---

## HeroAction 内部类

HeroAction 是英雄行动的抽象基类：

```java
public static abstract class HeroAction {
    public int dst;  // 目标位置
}

// 具体行动类型
public static class Move extends HeroAction { }
public static class Interact extends HeroAction { Char ch; }
public static class Buy extends HeroAction { }
public static class PickUp extends HeroAction { }
public static class OpenChest extends HeroAction { }
public static class Unlock extends HeroAction { }
public static class Mine extends HeroAction { }
public static class LvlTransition extends HeroAction { }
public static class Attack extends HeroAction { Char target; }
public static class Alchemy extends HeroAction { }
```

---

## 7. 方法详解

### live()

```java
public void live() {
    // 移除不持久的Buff
    for (Buff b : buffs()){
        if (!b.revivePersists) b.detach();
    }
    // 添加基础Buff
    Buff.affect( this, Regeneration.class );  // 生命恢复
    Buff.affect( this, Hunger.class );         // 饥饿系统
}
```

**方法作用**：激活英雄，添加基础Buff。

---

### updateHT(boolean boostHP)

```java
public void updateHT( boolean boostHP ){
    int curHT = HT;
    
    // 基础生命 = 20 + 5*(等级-1) + HT加成
    HT = 20 + 5*(lvl-1) + HTBoost;
    
    // 力量戒指加成
    float multiplier = RingOfMight.HTMultiplier(this);
    HT = Math.round(multiplier * HT);
    
    // 力量药剂加成
    if (buff(ElixirOfMight.HTBoost.class) != null){
        HT += buff(ElixirOfMight.HTBoost.class).boost();
    }
    
    // 可选择同步提升HP
    if (boostHP){
        HP += Math.max(HT - curHT, 0);
    }
    HP = Math.min(HP, HT);
}
```

**方法作用**：更新最大生命值。

**参数**：
- `boostHP` (boolean)：是否同步提升当前HP

---

### STR()

```java
public int STR() {
    int strBonus = 0;

    // 力量戒指加成
    strBonus += RingOfMight.strengthBonus( this );
    
    // 激素涌动激增加成
    AdrenalineSurge buff = buff(AdrenalineSurge.class);
    if (buff != null){
        strBonus += buff.boost();
    }

    // 强壮天赋加成（百分比加成）
    if (hasTalent(Talent.STRONGMAN)){
        strBonus += (int)Math.floor(STR * (0.03f + 0.05f*pointsInTalent(Talent.STRONGMAN)));
    }

    return STR + strBonus;
}
```

**方法作用**：获取实际力量值（包含各种加成）。

---

### act()

```java
@Override
public boolean act() {
    // 更新视野
    fieldOfView = Dungeon.level.heroFOV;

    // 处理忍耐状态
    if (buff(Endure.EndureTracker.class) != null){
        buff(Endure.EndureTracker.class).endEnduring();
    }
    
    // 观察环境
    if (!ready) {
        if (!resting || buff(MindVision.class) != null || buff(Awareness.class) != null) {
            Dungeon.observe();
        } else {
            Dungeon.level.updateFieldOfView(this, fieldOfView);
        }
    }
    
    // 更新可见敌人
    checkVisibleMobs();
    BuffIndicator.refreshHero();
    BuffIndicator.refreshBoss();
    
    // 麻痹检查
    if (paralysed > 0) {
        curAction = null;
        spendAndNext( TICK );
        return false;
    }
    
    // 执行当前行动
    boolean actResult;
    if (curAction == null) {
        if (resting) {
            spendConstant( TIME_TO_REST );
            next();
        } else {
            ready();
        }
        actResult = false;
    } else {
        resting = false;
        ready = false;
        
        // 根据行动类型执行
        if (curAction instanceof HeroAction.Move) {
            actResult = actMove( (HeroAction.Move)curAction );
        } else if (curAction instanceof HeroAction.Attack) {
            actResult = actAttack( (HeroAction.Attack)curAction );
        }
        // ... 其他行动类型
    }
    
    // 树皮皮肤天赋
    if(hasTalent(Talent.BARKSKIN) && Dungeon.level.map[pos] == Terrain.FURROWED_GRASS){
        Barkskin.conditionallyAppend(this, (lvl*pointsInTalent(Talent.BARKSKIN))/2, 1 );
    }
    
    return actResult;
}
```

**方法作用**：执行英雄每回合的行动。

**重写来源**：Char.act()

---

### attackSkill(Char target)

```java
@Override
public int attackSkill( Char target ) {
    KindOfWeapon wep = belongings.attackingWeapon();
    
    float accuracy = 1;
    accuracy *= RingOfAccuracy.accuracyMultiplier( this );
    
    // 精确突袭和灵活天赋
    if (!(wep instanceof MissileWeapon)) {
        if ((hasTalent(Talent.PRECISE_ASSAULT) || hasTalent(Talent.LIQUID_AGILITY))
                && belongings.abilityWeapon != wep 
                && buff(MonkEnergy.MonkAbility.UnarmedAbilityTracker.class) == null){

            // 非决斗师职业的精确突袭
            if (heroClass != HeroClass.DUELIST) {
                accuracy *= 1f + 0.1f * pointsInTalent(Talent.PRECISE_ASSAULT);
            }

            // 精确突袭追踪器
            if (buff(Talent.PreciseAssaultTracker.class) != null) {
                switch (pointsInTalent(Talent.PRECISE_ASSAULT)){
                    default: case 1: accuracy *= 2; break;
                    case 2: accuracy *= 5; break;
                    case 3: accuracy *= Float.POSITIVE_INFINITY; break;
                }
            }
        }
    }

    // 剑舞加成
    if (buff(Scimitar.SwordDance.class) != null){
        accuracy *= 1.50f;
    }
    
    // 武器准确度因子
    if (!RingOfForce.fightingUnarmed(this)) {
        return Math.max(1, Math.round(attackSkill * accuracy * wep.accuracyFactor( this, target )));
    } else {
        return Math.max(1, Math.round(attackSkill * accuracy));
    }
}
```

**方法作用**：计算对目标的攻击技能值。

---

### defenseSkill(Char enemy)

```java
@Override
public int defenseSkill( Char enemy ) {
    // 招架追踪器：无限闪避
    if (buff(Combo.ParryTracker.class) != null){
        if (canAttack(enemy) && !isCharmedBy(enemy)){
            Buff.affect(this, Combo.RiposteTracker.class).enemy = enemy;
        }
        return INFINITE_EVASION;
    }

    // 守卫追踪器：无限闪避
    if (buff(RoundShield.GuardTracker.class) != null){
        return INFINITE_EVASION;
    }
    
    float evasion = defenseSkill;
    
    // 闪避戒指加成
    evasion *= RingOfEvasion.evasionMultiplier( this );

    // 灵活天赋
    if (buff(Talent.LiquidAgilEVATracker.class) != null){
        if (pointsInTalent(Talent.LIQUID_AGILITY) == 1){
            evasion *= 3f;
        } else if (pointsInTalent(Talent.LIQUID_AGILITY) == 2){
            return INFINITE_EVASION;
        }
    }

    // 防御姿态加成
    if (buff(Quarterstaff.DefensiveStance.class) != null){
        evasion *= 3;
    }
    
    // 麻痹减半闪避
    if (paralysed > 0) {
        evasion /= 2;
    }

    // 护甲闪避因子
    if (belongings.armor() != null) {
        evasion = belongings.armor().evasionFactor(this, evasion);
    }

    return Math.max(1, Math.round(evasion));
}
```

**方法作用**：计算对敌人的防御技能值。

---

### damageRoll()

```java
@Override
public int damageRoll() {
    KindOfWeapon wep = belongings.attackingWeapon();
    int dmg;

    if (!RingOfForce.fightingUnarmed(this)) {
        dmg = wep.damageRoll( this );
        if (!(wep instanceof MissileWeapon)) dmg += RingOfForce.armedDamageBonus(this);
    } else {
        dmg = RingOfForce.damageRoll(this);
        if (RingOfForce.unarmedGetsWeaponAugment(this)){
            dmg = ((Weapon)belongings.attackingWeapon()).augment.damageFactor(dmg);
        }
    }

    // 物理增强Buff
    PhysicalEmpower emp = buff(PhysicalEmpower.class);
    if (emp != null){
        dmg += emp.dmgBoost;
        emp.left--;
        if (emp.left <= 0) {
            emp.detach();
        }
        Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG, 0.75f, 1.2f);
    }

    // 武器充能天赋
    if (heroClass != HeroClass.DUELIST
            && hasTalent(Talent.WEAPON_RECHARGING)
            && (buff(Recharging.class) != null || buff(ArtifactRecharge.class) != null)){
        dmg = Math.round(dmg * 1.025f + (.025f*pointsInTalent(Talent.WEAPON_RECHARGING)));
    }

    if (dmg < 0) dmg = 0;
    return dmg;
}
```

**方法作用**：计算伤害值。

---

### speed()

```java
@Override
public float speed() {
    float speed = super.speed();

    // 急速戒指加成
    speed *= RingOfHaste.speedMultiplier(this);
    
    // 护甲速度因子
    if (belongings.armor() != null) {
        speed = belongings.armor().speedFactor(this, speed);
    }
    
    // 动量Buff
    Momentum momentum = buff(Momentum.class);
    if (momentum != null){
        ((HeroSprite)sprite).sprint( momentum.freerunning() ? 1.5f : 1f );
        speed *= momentum.speedMultiplier();
    }

    // 自然之力加成
    NaturesPower.naturesPowerTracker natStrength = buff(NaturesPower.naturesPowerTracker.class);
    if (natStrength != null){
        speed *= (2f + 0.25f*pointsInTalent(Talent.GROWING_POWER));
    }

    // 飞升挑战修正
    speed = AscensionChallenge.modifyHeroSpeed(speed);
    
    return speed;
}
```

**方法作用**：计算移动速度。

---

### 天赋相关方法

```java
// 检查是否有天赋
public boolean hasTalent( Talent talent ){
    return pointsInTalent(talent) > 0;
}

// 获取天赋点数
public int pointsInTalent( Talent talent ){
    for (LinkedHashMap<Talent, Integer> tier : talents){
        for (Talent f : tier.keySet()){
            if (f == talent) return tier.get(f);
        }
    }
    return 0;
}

// 升级天赋
public void upgradeTalent( Talent talent ){
    for (LinkedHashMap<Talent, Integer> tier : talents){
        for (Talent f : tier.keySet()){
            if (f == talent) tier.put(talent, tier.get(talent)+1);
        }
    }
    Talent.onTalentUpgraded(this, talent);
}

// 获取已花费的天赋点
public int talentPointsSpent(int tier){
    int total = 0;
    for (int i : talents.get(tier-1).values()){
        total += i;
    }
    return total;
}

// 获取可用天赋点
public int talentPointsAvailable(int tier){
    if (lvl < (Talent.tierLevelThresholds[tier] - 1)
        || (tier == 3 && subClass == HeroSubClass.NONE)
        || (tier == 4 && armorAbility == null)) {
        return 0;
    } else if (lvl >= Talent.tierLevelThresholds[tier+1]){
        return Talent.tierLevelThresholds[tier+1] - Talent.tierLevelThresholds[tier] 
            - talentPointsSpent(tier) + bonusTalentPoints(tier);
    } else {
        return 1 + lvl - Talent.tierLevelThresholds[tier] 
            - talentPointsSpent(tier) + bonusTalentPoints(tier);
    }
}
```

---

### 行动执行方法

```java
// 移动行动
private boolean actMove( HeroAction.Move action ) {
    if (getCloser( action.dst )) {
        canSelfTrample = false;
        return true;
    } else if (pos == action.dst && canSelfTrample()){
        canSelfTrample = false;
        Dungeon.level.pressCell(pos);
        spendAndNext( 1 / speed() );
        return false;
    } else {
        ready();
        return false;
    }
}

// 攻击行动
private boolean actAttack( HeroAction.Attack action ) {
    attackTarget = action.target;

    // 魅惑检查
    if (isCharmedBy(attackTarget)){
        GLog.w( Messages.get(Charm.class, "cant_attack"));
        ready();
        return false;
    }

    // 可以攻击
    if (attackTarget.isAlive() && canAttack(attackTarget) && attackTarget.invisible == 0) {
        // 侵略屏障天赋
        if (heroClass != HeroClass.DUELIST
                && hasTalent(Talent.AGGRESSIVE_BARRIER)
                && buff(Talent.AggressiveBarrierCooldown.class) == null
                && (HP / (float)HT) <= 0.5f){
            int shieldAmt = 1 + 2*pointsInTalent(Talent.AGGRESSIVE_BARRIER);
            Buff.affect(this, Barrier.class).setShield(shieldAmt);
        }
        
        sprite.attack( attackTarget.pos );
        return false;
    } else {
        // 接近目标
        if (fieldOfView[attackTarget.pos] && getCloser( attackTarget.pos )) {
            attackTarget = null;
            return true;
        } else {
            ready();
            attackTarget = null;
            return false;
        }
    }
}

// 拾取行动
private boolean actPickUp( HeroAction.PickUp action ) {
    int dst = action.dst;
    if (pos == dst) {
        Heap heap = Dungeon.level.heaps.get( pos );
        if (heap != null) {
            Item item = heap.peek();
            if (item.doPickUp( this )) {
                heap.pickUp();
                // 拾取成功提示
                GLog.i( Messages.capitalize(Messages.get(this, "you_now_have", item.name())) );
                curAction = null;
            } else {
                // 拾取失败
                GLog.n(Messages.capitalize(Messages.get(this, "you_cant_have", item.name())));
                ready();
            }
        } else {
            ready();
        }
        return false;
    } else if (getCloser( dst )) {
        return true;
    } else {
        ready();
        return false;
    }
}

// 楼层转换行动
private boolean actTransition(HeroAction.LvlTransition action ) {
    int stairs = action.dst;
    LevelTransition transition = Dungeon.level.getTransition(stairs);

    if (rooted) {
        PixelScene.shake(1, 1f);
        ready();
        return false;
    } else if (!Dungeon.level.locked && transition != null && transition.inside(pos)) {
        if (Dungeon.level.activateTransition(this, transition)){
            curAction = null;
        } else {
            ready();
        }
        return false;
    } else if (getCloser( stairs )) {
        return true;
    } else {
        ready();
        return false;
    }
}
```

---

### rest() / busy() / interrupt() / resume()

```java
public void rest( boolean fullRest ) {
    spendAndNextConstant( TIME_TO_REST );
    // 坚守天赋
    if (hasTalent(Talent.HOLD_FAST)){
        Buff.affect(this, HoldFast.class).pos = pos;
    }
    // 耐心打击天赋
    if (hasTalent(Talent.PATIENT_STRIKE)){
        Buff.affect(Dungeon.hero, Talent.PatientStrikeTracker.class).pos = Dungeon.hero.pos;
    }
    resting = fullRest;
}

public void busy() {
    ready = false;
}

public void interrupt() {
    if (isAlive() && curAction != null &&
        ((curAction instanceof HeroAction.Move && curAction.dst != pos) ||
        (curAction instanceof HeroAction.LvlTransition))) {
        lastAction = curAction;
    }
    curAction = null;
    resting = false;
}

public void resume() {
    curAction = lastAction;
    lastAction = null;
    damageInterrupt = false;
    next();
}
```

---

### shoot()

```java
public boolean shoot( Char enemy, MissileWeapon wep ) {
    attackTarget = enemy;
    boolean wasEnemy = enemy.alignment == Alignment.ENEMY
            || (enemy instanceof Mimic && enemy.alignment == Alignment.NEUTRAL);

    // 临时设置投掷武器
    belongings.thrownWeapon = wep;
    boolean hit = attack( enemy );
    Invisibility.dispel();
    belongings.thrownWeapon = null;

    // 角斗士连击
    if (hit && subClass == HeroSubClass.GLADIATOR && wasEnemy){
        Buff.affect( this, Combo.class ).hit( enemy );
    }

    // 决斗师连击
    if (hit && heroClass == HeroClass.DUELIST && wasEnemy){
        Buff.affect( this, Sai.ComboStrikeTracker.class).addHit();
    }

    attackTarget = null;
    return hit;
}
```

**方法作用**：执行远程攻击。

---

### canAttack()

```java
public boolean canAttack(Char enemy){
    if (enemy == null || pos == enemy.pos || !Actor.chars().contains(enemy)) {
        return false;
    }

    // 相邻总是可以攻击
    if (Dungeon.level.adjacent(pos, enemy.pos)) {
        return true;
    }

    KindOfWeapon wep = Dungeon.hero.belongings.attackingWeapon();

    if (wep != null){
        return wep.canReach(this, enemy.pos);
    } else if (buff(AscendedForm.AscendBuff.class) != null) {
        // 飞升形态：范围3
        boolean[] passable = BArray.not(Dungeon.level.solid, null);
        for (Char ch : Actor.chars()) {
            if (ch != this) passable[ch.pos] = false;
        }
        PathFinder.buildDistanceMap(enemy.pos, passable, 3);
        return PathFinder.distance[pos] <= 3;
    } else {
        return false;
    }
}
```

**方法作用**：判断是否可以攻击目标。

---

## 与其他类的交互

### 被哪些类使用

| 类名 | 如何使用 |
|------|----------|
| `Dungeon` | 持有英雄实例 |
| `GameScene` | 渲染和控制英雄 |
| `Item` | 物品使用 |
| `Level` | 关卡交互 |

### 使用了哪些类

| 类名 | 用于什么目的 |
|------|-------------|
| `Char` | 基础角色功能 |
| `Belongings` | 物品管理 |
| `HeroAction` | 行动类型定义 |
| `Talent` | 天赋系统 |
| `HeroClass` | 职业定义 |

---

## 11. 使用示例

### 创建英雄

```java
Hero hero = new Hero();
hero.live();
GamesInProgress.selectedClass.initHero(hero);
```

### 执行行动

```java
// 移动
hero.curAction = new HeroAction.Move(targetPos);

// 攻击
hero.curAction = new HeroAction.Attack(enemy);

// 休息
hero.rest(true);

// 中断当前行动
hero.interrupt();

// 恢复行动
hero.resume();
```

### 检查天赋

```java
if (hero.hasTalent(Talent.STRONGMAN)) {
    int bonus = hero.pointsInTalent(Talent.STRONGMAN);
    // 应用天赋效果
}
```

---

## 注意事项

### 行动系统

1. **ready 标志**：只有 ready=true 时英雄才能接受新指令
2. **curAction**：当前正在执行的行动
3. **lastAction**：用于被中断后恢复

### 状态管理

1. **resting**：休息状态，影响视野更新
2. **damageInterrupt**：是否可被伤害打断
3. **attackTarget**：当前攻击目标，用于某些特效

### 常见的坑

1. **忘记调用 live()**：英雄不会有生命恢复和饥饿
2. **直接修改 STR**：应该使用 STR() 方法获取实际值
3. **忽略天赋**：很多功能依赖天赋系统

### 最佳实践

1. 使用 `spendAndNext()` 管理行动时间
2. 使用 `ready()` 和 `busy()` 管理行动状态
3. 使用天赋系统而非硬编码功能