# Mob 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/Mob.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.mobs |
| **类类型** | abstract class |
| **继承关系** | extends Char |
| **代码行数** | 1511 |

---

## 类职责

Mob 是游戏中所有"怪物"的抽象基类，代表有AI行为、可以与玩家战斗的实体。它在 Char 的基础上添加了：

1. **AI状态机**：定义怪物的行为状态（睡眠、游荡、追击、逃跑等）
2. **敌对系统**：追踪敌人、仇恨管理
3. **战利品系统**：掉落物品、经验值
4. **特殊能力**：召唤、变形等复杂行为

**核心设计模式**：状态模式（State Pattern）——每种AI状态是一个独立的内部类

---

## 4. 继承与协作关系

```mermaid
classDiagram
    class Mob {
        <<abstract>>
        +AiState SLEEPING
        +AiState HUNTING
        +AiState WANDERING
        +AiState FLEEING
        +AiState INVESTIGATING
        +AiState PASSIVE
        +AiState state
        +Char enemy
        +int target
        +int defenseSkill
        +int EXP
        +int maxLvl
        +Class~? extends CharSprite~ spriteClass
        #boolean firstAdded
        #boolean intelligentAlly
        +chooseEnemy() Char
        +aggro(Char) void
        +beckon(int) void
        +die(Object) void
        +rollToDropLoot() void
        +createLoot() Item
        +surprisedBy(Char) boolean
        +description() String
    }
    
    class Char {
        <<abstract>>
        +int pos
        +int HP, HT
        +Alignment alignment
    }
    
    class AiState {
        <<interface>>
        +act(boolean, boolean) boolean
    }
    
    class Sleeping {
        +act() boolean
        #detectionChance(Char) float
        #awaken(boolean) void
    }
    
    class Wandering {
        +act() boolean
        #detectionChance(Char) float
        #noticeEnemy() boolean
        #continueWandering() boolean
    }
    
    class Hunting {
        +act() boolean
        #handleRecentAttackers() boolean
        #handleUnreachableTarget() boolean
    }
    
    class Fleeing {
        +act() boolean
        #escaped() void
        #nowhereToRun() void
    }
    
    class Investigating {
        +act() boolean
    }
    
    class Passive {
        +act() boolean
    }
    
    Char <|-- Mob
    Mob +-- AiState
    AiState <|.. Sleeping
    AiState <|.. Wandering
    AiState <|.. Hunting
    AiState <|.. Fleeing
    AiState <|.. Investigating
    AiState <|.. Passive
    Mob +-- Sleeping
    Mob +-- Wandering
    Mob +-- Hunting
    Mob +-- Fleeing
    Mob +-- Investigating
    Mob +-- Passive
```

---

## 接口定义

### AiState（AI状态接口）

```java
public interface AiState {
    boolean act( boolean enemyInFOV, boolean justAlerted );
}
```

**作用**：定义所有AI状态的统一接口

**方法**：
- `act(enemyInFOV, justAlerted)`：执行该状态的行动逻辑

---

## 实例字段

### AI状态

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `SLEEPING` | AiState | new Sleeping() | 睡眠状态 |
| `HUNTING` | AiState | new Hunting() | 追击状态 |
| `INVESTIGATING` | AiState | new Investigating() | 调查状态 |
| `WANDERING` | AiState | new Wandering() | 游荡状态 |
| `FLEEING` | AiState | new Fleeing() | 逃跑状态 |
| `PASSIVE` | AiState | new Passive() | 被动状态 |
| `state` | AiState | SLEEPING | 当前AI状态 |

### 战斗相关

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `enemy` | Char | null | 当前目标 |
| `enemyID` | int | -1 | 目标ID（用于存档恢复） |
| `enemySeen` | boolean | false | 是否看见敌人 |
| `alerted` | boolean | false | 是否刚被警醒 |
| `target` | int | -1 | 目标位置 |
| `recentlyAttackedBy` | ArrayList&lt;Char&gt; | new | 最近攻击者列表 |

### 属性

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spriteClass` | Class&lt;? extends CharSprite&gt; | - | 精灵类 |
| `defenseSkill` | int | 0 | 防御技能值 |
| `EXP` | int | 1 | 击杀经验 |
| `maxLvl` | int | Hero.MAX_LEVEL-1 | 最大有效等级 |
| `loot` | Object | null | 掉落物品类型 |
| `lootChance` | float | 0 | 掉落概率 |

### 内部状态

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `firstAdded` | boolean | true | 是否首次添加到关卡 |
| `intelligentAlly` | boolean | false | 是否是智能盟友 |

### 静态字段

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `TIME_TO_WAKE_UP` | float | 唤醒所需时间（1f） |
| `heldAllies` | ArrayList&lt;Mob&gt; | 暂存的盟友列表 |

---

## AI状态详解

### 1. Sleeping（睡眠状态）

```java
protected class Sleeping implements AiState {
    public static final String TAG = "SLEEPING";

    @Override
    public boolean act( boolean enemyInFOV, boolean justAlerted ) {
        // 第1-10行：负面Buff会唤醒怪物
        for (Buff b : buffs()){
            if (b.type == Buff.buffType.NEGATIVE){
                awaken(enemyInFOV);
                return true;
            }
        }

        // 第11-30行：检测可见的敌人
        if (enemyInFOV || (enemy != null && enemy.invisible > 0)) {
            float highestChance = Float.POSITIVE_INFINITY;
            Char closestHostile = null;

            for (Char ch : Actor.chars()){
                if (fieldOfView[ch.pos] && ch.invisible == 0 
                    && ch.alignment != alignment && ch.alignment != Alignment.NEUTRAL){
                    float bestChance = detectionChance(ch);
                    // 静步天赋：远距离更难被察觉
                    if ((ch instanceof Hero || ch instanceof ShadowClone.ShadowAlly)
                            && Dungeon.hero.hasTalent(Talent.SILENT_STEPS)){
                        if (distance(ch) >= 4 - Dungeon.hero.pointsInTalent(Talent.SILENT_STEPS)) {
                            bestChance = Float.POSITIVE_INFINITY;
                        }
                    }
                    // 飞行角色更难被察觉
                    if (ch.flying && distance(ch) >= 2){
                        bestChance = Float.POSITIVE_INFINITY;
                    }
                    if (bestChance < highestChance){
                        highestChance = bestChance;
                        closestHostile = ch;
                    }
                }
            }

            // 随机检测是否唤醒
            if (closestHostile != null && Random.Float() < detectionChance(closestHostile)) {
                awaken(enemyInFOV);
                return true;
            }
        }

        enemySeen = false;
        spend( TICK );
        return true;
    }

    // 检测概率 = 1 / (距离 + 隐匿值)
    protected float detectionChance( Char enemy ){
        return 1 / (distance( enemy ) + enemy.stealth());
    }

    protected void awaken( boolean enemyInFOV ){
        if (enemyInFOV) {
            enemySeen = true;
            notice();
            state = HUNTING;  // 看见敌人直接追击
            target = enemy.pos;
        } else {
            notice();
            state = WANDERING;  // 没看见敌人则游荡调查
            target = Dungeon.level.randomDestination( Mob.this );
        }

        // 蜂群意识挑战：唤醒附近其他怪物
        if (alignment == Alignment.ENEMY && Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE)) {
            for (Mob mob : Dungeon.level.mobs) {
                if (mob.paralysed <= 0
                        && Dungeon.level.distance(pos, mob.pos) <= 8
                        && mob.state != mob.HUNTING) {
                    mob.beckon(target);
                }
            }
        }
        spend(TIME_TO_WAKE_UP);
    }
}
```

**状态行为**：
- 每回合检测是否有敌人
- 检测概率基于距离和敌人隐匿值
- 负面Buff必定唤醒
- 唤醒后进入追击或游荡状态

---

### 2. Wandering（游荡状态）

```java
protected class Wandering implements AiState {
    public static final String TAG = "WANDERING";

    @Override
    public boolean act( boolean enemyInFOV, boolean justAlerted ) {
        // 第1-7行：检测到敌人则切换到追击
        if (enemyInFOV && (justAlerted || Random.Float() < detectionChance(enemy))) {
            return noticeEnemy();
        } else {
            return continueWandering();
        }
    }

    // 检测概率 = 1 / (距离/2 + 隐匿值)
    // 比睡眠状态更容易检测到敌人
    protected float detectionChance( Char enemy ){
        return 1 / (distance( enemy ) / 2f + enemy.stealth());
    }

    protected boolean noticeEnemy(){
        enemySeen = true;
        notice();           // 显示警觉标记
        alerted = true;
        state = HUNTING;    // 进入追击状态
        target = enemy.pos;
        
        // 蜂群意识挑战
        if (alignment == Alignment.ENEMY && Dungeon.isChallenged( Challenges.SWARM_INTELLIGENCE )) {
            for (Mob mob : Dungeon.level.mobs) {
                if (mob.paralysed <= 0
                        && Dungeon.level.distance(pos, mob.pos) <= 8
                        && mob.state != mob.HUNTING) {
                    mob.beckon( target );
                }
            }
        }
        return true;
    }
    
    protected boolean continueWandering(){
        enemySeen = false;
        
        int oldPos = pos;
        if (target != -1 && getCloser( target )) {
            spend( 1 / speed() );
            return moveSprite( oldPos, pos );
        } else {
            target = randomDestination();  // 随机选择新目标
            spend( TICK );
        }
        return true;
    }

    protected int randomDestination(){
        return Dungeon.level.randomDestination( Mob.this );
    }
}
```

**状态行为**：
- 在关卡中随机游荡
- 检测敌人比睡眠状态更敏感
- 发现敌人后切换到追击

---

### 3. Hunting（追击状态）

```java
protected class Hunting implements AiState {
    public static final String TAG = "HUNTING";

    @Override
    public boolean act( boolean enemyInFOV, boolean justAlerted ) {
        enemySeen = enemyInFOV;
        
        // 第1-10行：在攻击范围内则攻击
        if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {
            recentlyAttackedBy.clear();
            target = enemy.pos;
            return doAttack( enemy );

        } else {
            // 第11-15行：处理最近攻击者（切换目标）
            if (handleRecentAttackers()){
                return act( true, justAlerted );  // 递归重新行动
            }

            // 第16-25行：更新目标位置
            if (enemyInFOV) {
                target = enemy.pos;
            } else if (enemy == null) {
                sprite.showLost();       // 显示丢失目标
                state = WANDERING;
                target = ((Mob.Wandering)WANDERING).randomDestination();
                spend( TICK );
                return true;
            }
            
            // 第26-40行：向目标移动
            int oldPos = pos;
            if (target != -1 && getCloser( target )) {
                spend( 1 / speed() );
                return moveSprite( oldPos, pos );
            } else {
                return handleUnreachableTarget(enemyInFOV, justAlerted);
            }
        }
    }

    protected boolean handleRecentAttackers(){
        boolean swapped = false;
        if (!recentlyAttackedBy.isEmpty()){
            for (Char ch : recentlyAttackedBy){
                // 找到更近或可攻击的目标
                if (ch != null && ch.isActive() && Actor.chars().contains(ch) 
                    && alignment != ch.alignment && fieldOfView[ch.pos] 
                    && ch.invisible == 0 && !isCharmedBy(ch)) {
                    if (canAttack(ch) || enemy == null 
                        || Dungeon.level.distance(pos, ch.pos) < Dungeon.level.distance(pos, enemy.pos)) {
                        enemy = ch;
                        target = ch.pos;
                        swapped = true;
                    }
                }
            }
            recentlyAttackedBy.clear();
        }
        return swapped;
    }

    // 防止无限递归
    protected boolean recursing = false;

    protected boolean handleUnreachableTarget(boolean enemyInFOV, boolean justAlerted){
        if (!recursing) {
            Char oldEnemy = enemy;
            enemy = null;
            enemy = chooseEnemy();  // 尝试选择新目标
            if (enemy != null && enemy != oldEnemy) {
                recursing = true;
                boolean result = act(enemyInFOV, justAlerted);
                recursing = false;
                return result;
            }
        }

        spend( TICK );
        if (!enemyInFOV) {
            sprite.showLost();
            state = WANDERING;
            target = ((Mob.Wandering)WANDERING).randomDestination();
        }
        return true;
    }
}
```

**状态行为**：
- 追击敌人直到进入攻击范围
- 攻击后继续追击或切换目标
- 无法到达目标时尝试寻找新目标
- 目标丢失后回到游荡

---

### 4. Fleeing（逃跑状态）

```java
protected class Fleeing implements AiState {
    public static final String TAG = "FLEEING";

    @Override
    public boolean act( boolean enemyInFOV, boolean justAlerted ) {
        enemySeen = enemyInFOV;
        
        // 第1-15行：逃跑成功检查
        // 敌人不在视野内且距离足够远时有概率逃脱
        if (enemy == null || !enemyInFOV && 1 + Random.Int(Dungeon.level.distance(pos, target)) >= 6){
            escaped();
            if (state != FLEEING){
                spend( TICK );
                return true;
            }
        // 更新逃跑方向
        } else if (enemyInFOV) {
            target = enemy.pos;
        }

        // 第16-30行：远离目标
        int oldPos = pos;
        if (target != -1 && getFurther( target )) {
            spend( 1 / speed() );
            return moveSprite( oldPos, pos );
        } else {
            spend( TICK );
            nowhereToRun();  // 无处可逃
            return true;
        }
    }

    protected void escaped(){
        // 子类可重写，默认不做任何事
    }

    protected void nowhereToRun() {
        // 没有恐惧/绝望Buff时转为追击
        if (buff( Terror.class ) == null && buff( Dread.class ) == null) {
            if (enemySeen) {
                sprite.showStatus(CharSprite.WARNING, Messages.get(Mob.class, "rage"));
                state = HUNTING;  // 绝地反击
            } else {
                state = WANDERING;
            }
        }
    }
}
```

**状态行为**：
- 远离敌人
- 距离足够远时可能逃脱
- 无处可逃时转为追击

---

### 5. Investigating（调查状态）

```java
protected class Investigating extends Wandering {
    public static final String TAG = "INVESTIGATING";

    @Override
    public boolean act(boolean enemyInFOV, boolean justAlerted) {
        if (enemyInFOV){
            target = enemy.pos;  // 看见敌人则更新目标
        } else {
            // 到达目标位置附近但仍未发现敌人
            if (Dungeon.level.distance(pos, target) <= 1){
                sprite.showLost();
                state = WANDERING;
                target = ((Mob.Wandering)WANDERING).randomDestination();
                spend( TICK );
                return true;
            }
        }
        return super.act(enemyInFOV, justAlerted);
    }
}
```

**状态行为**：
- 继承游荡的检测逻辑
- 到达目标位置后回到游荡
- 主要用于调查声音或迹象

---

### 6. Passive（被动状态）

```java
protected class Passive implements AiState {
    public static final String TAG = "PASSIVE";

    @Override
    public boolean act( boolean enemyInFOV, boolean justAlerted ) {
        enemySeen = enemyInFOV;
        spend( TICK );
        return true;  // 什么都不做
    }
}
```

**状态行为**：
- 完全被动，不移动不攻击
- 用于NPC、陷阱等

---

## 7. 方法详解

### act()

```java
@Override
protected boolean act() {
    super.act();  // 调用Char.act()更新视野
    
    boolean justAlerted = alerted;
    alerted = false;
    
    // 第1-10行：显示/隐藏状态图标
    if (justAlerted){
        sprite.showAlert();   // 显示警觉图标
    } else {
        sprite.hideAlert();
        sprite.hideLost();
        sprite.hideInvestigate();
    }
    
    // 第11-15行：麻痹时不能行动
    if (paralysed > 0) {
        enemySeen = false;
        spend( TICK );
        return true;
    }

    // 第16-18行：恐惧/绝望强制进入逃跑状态
    if (buff(Terror.class) != null || buff(Dread.class) != null ){
        state = FLEEING;
    }
    
    // 第19-20行：选择敌人
    enemy = chooseEnemy();
    
    boolean enemyInFOV = enemy != null && enemy.isAlive() 
        && fieldOfView[enemy.pos] && enemy.invisible <= 0;

    // 第21-25行：佯攻干扰
    if (buff(Feint.AfterImage.FeintConfusion.class) != null){
        enemySeen = enemyInFOV;
        spend( TICK );
        return true;  // 无法行动
    }

    // 第26-35行：执行当前状态的行动
    boolean result = state.act( enemyInFOV, justAlerted );

    // 更新拥有"力量共享"Buff的盟友视野
    if (buff(PowerOfMany.PowerBuff.class) != null){
        Dungeon.level.updateFieldOfView( this, fieldOfView );
        GameScene.updateFog(pos, viewDistance+(int)Math.ceil(speed()));
    }

    return result;
}
```

**方法作用**：执行怪物每回合的行动。

**重写来源**：Char.act()

**执行流程**：
1. 更新视野
2. 处理警觉状态
3. 检查麻痹
4. 检查恐惧状态
5. 选择敌人
6. 执行AI状态

---

### chooseEnemy()

```java
protected Char chooseEnemy() {
    // 第1-15行：恐惧/绝望优先攻击恐惧来源
    Dread dread = buff( Dread.class );
    if (dread != null) {
        Char source = (Char)Actor.findById( dread.object );
        if (source != null) {
            return source;
        }
    }

    Terror terror = buff( Terror.class );
    if (terror != null) {
        Char source = (Char)Actor.findById( terror.object );
        if (source != null) {
            return source;
        }
    }
    
    // 第16-30行：攻击带有"侵略"标记的目标
    if ((alignment == Alignment.ENEMY || buff(Amok.class) != null ) 
        && state != PASSIVE && state != SLEEPING) {
        if (enemy != null && enemy.buff(StoneOfAggression.Aggression.class) != null){
            state = HUNTING;
            return enemy;
        }
        for (Char ch : Actor.chars()) {
            if (ch != this && fieldOfView[ch.pos] &&
                    ch.buff(StoneOfAggression.Aggression.class) != null) {
                state = HUNTING;
                return ch;
            }
        }
    }

    // 第31-60行：判断是否需要新敌人
    boolean newEnemy = false;
    if ( enemy == null || !enemy.isAlive() || !Actor.chars().contains(enemy) || state == WANDERING) {
        newEnemy = true;
    } else if (buff( Amok.class ) != null && enemy == Dungeon.hero) {
        newEnemy = true;  // 狂暴时不攻击英雄
    } else if (buff(Charm.class) != null && buff(Charm.class).object == enemy.id()) {
        newEnemy = true;  // 魅惑时不攻击魅惑者
    }

    // 盟友特殊逻辑
    if (!newEnemy && alignment == Alignment.ALLY){
        if (enemy.alignment == Alignment.ALLY){
            newEnemy = true;  // 不攻击友军
        } else if (enemy.isInvulnerable(getClass())){
            newEnemy = true;  // 不攻击无敌目标
        }
    }

    // 第61-150行：搜索潜在敌人
    if ( newEnemy ) {
        HashSet<Char> enemies = new HashSet<>();

        // 狂暴时优先攻击敌方怪物
        if ( buff(Amok.class) != null) {
            for (Mob mob : Dungeon.level.mobs)
                if (mob.alignment == Alignment.ENEMY && mob != this
                        && fieldOfView[mob.pos] && mob.invisible <= 0) {
                    enemies.add(mob);
                }
            // 其次攻击友军
            // 最后攻击英雄
        // 盟友逻辑
        } else if ( alignment == Alignment.ALLY ) {
            for (Mob mob : Dungeon.level.mobs)
                if (mob.alignment == Alignment.ENEMY && fieldOfView[mob.pos]
                        && mob.invisible <= 0 && !mob.isInvulnerable(getClass()))
                    if (mob.state != mob.PASSIVE &&
                            (!intelligentAlly || (mob.state != mob.SLEEPING && mob.state != mob.WANDERING))) {
                        enemies.add(mob);
                    }
        // 敌方逻辑
        } else if (alignment == Alignment.ENEMY) {
            for (Mob mob : Dungeon.level.mobs)
                if (mob.alignment == Alignment.ALLY && fieldOfView[mob.pos] && mob.invisible <= 0)
                    enemies.add(mob);
            if (fieldOfView[Dungeon.hero.pos] && Dungeon.hero.invisible <= 0) {
                enemies.add(Dungeon.hero);
            }
        }

        // 排除魅惑者
        Charm charm = buff( Charm.class );
        if (charm != null){
            Char source = (Char)Actor.findById( charm.object );
            if (source != null && enemies.contains(source) && enemies.size() > 1){
                enemies.remove(source);
            }
        }

        // 选择最近的敌人
        if (!enemies.isEmpty()){
            PathFinder.buildDistanceMap(pos, Dungeon.findPassable(this, Dungeon.level.passable, fieldOfView, true));
            Char closest = null;
            int closestDist = Integer.MAX_VALUE;

            for (Char curr : enemies){
                int currDist = Integer.MAX_VALUE;
                for (int i : PathFinder.NEIGHBOURS8){
                    if (PathFinder.distance[curr.pos+i] < currDist){
                        currDist = PathFinder.distance[curr.pos+i];
                    }
                }
                // 选择逻辑：可攻击 > 更近 > 英雄优先
                if (closest == null){
                    closest = curr;
                    closestDist = currDist;
                } else if (canAttack(closest) && !canAttack(curr)){
                    continue;
                } else if ((canAttack(curr) && !canAttack(closest))
                        || (currDist < closestDist)){
                    closest = curr;
                } else if ( curr == Dungeon.hero &&
                        (currDist == closestDist) || (canAttack(curr) && canAttack(closest))){
                    closest = curr;
                }
            }
            
            // 佯攻影像优先于英雄
            if (closest == Dungeon.hero){
                for (Char ch : enemies){
                    if (ch instanceof Feint.AfterImage){
                        closest = ch;
                        break;
                    }
                }
            }

            return closest;
        }
        return null;
    } else {
        return enemy;
    }
}
```

**方法作用**：选择当前的目标敌人。

**返回值**：目标Char，没有则返回null

**选择优先级**：
1. 恐惧/绝望来源
2. 带有侵略标记的目标
3. 最近的敌人
4. 可攻击的优先
5. 英雄优先（同距离）

---

### getCloser(int target)

```java
protected boolean getCloser( int target ) {
    // 第1-5行：基本检查
    if (rooted || target == pos || !Dungeon.level.insideMap(target)) {
        return false;
    }

    int step = -1;

    // 第6-15行：相邻目标直接移动
    if (Dungeon.level.adjacent( pos, target )) {
        path = null;
        if (cellIsPathable(target)) {
            step = target;
        }
    } else {
        // 第16-100行：路径规划
        boolean newPath = false;
        float longFactor = state == WANDERING ? 2f : 1.33f;
        
        // 检查是否需要新路径
        if (path == null || path.isEmpty()
                || !Dungeon.level.adjacent(pos, path.getFirst())
                || path.size() > longFactor*Dungeon.level.distance(pos, target))
            newPath = true;
        else if (path.getLast() != target) {
            // 调整路径末端
            if (Dungeon.level.adjacent(target, path.getLast())) {
                int last = path.removeLast();
                // ...路径调整逻辑
            } else {
                newPath = true;
            }
        }

        // 检查路径下一格是否可通行
        if (!newPath) {
            int nextCell = path.removeFirst();
            if (!cellIsPathable(nextCell)) {
                newPath = true;
                // 尝试找替代路径
            } else {
                path.addFirst(nextCell);
            }
        }

        // 生成新路径
        if (newPath) {
            PathFinder.Path full = Dungeon.findPath(this, target, Dungeon.level.passable, fieldOfView, true);
            if (state != HUNTING){
                path = full;
            } else {
                // 追击时检查是否有更短路径
                PathFinder.Path ignoreChars = Dungeon.findPath(this, target, Dungeon.level.passable, fieldOfView, false);
                if (ignoreChars != null && (full == null || full.size() > 2*ignoreChars.size())){
                    path = ignoreChars;
                } else {
                    path = full;
                }
            }
        }

        if (path != null) {
            step = path.removeFirst();
        } else {
            return false;
        }
    }
    
    if (step != -1) {
        move( step );
        return true;
    } else {
        return false;
    }
}
```

**方法作用**：向目标位置移动一格。

**参数**：
- `target` (int)：目标位置

**返回值**：是否成功移动

**路径规划**：
- 使用 PathFinder 寻路
- 游荡状态容忍更长路径
- 追击状态寻找最短路径
- 路径缓存避免重复计算

---

### die(Object cause)

```java
@Override
public void die( Object cause ) {
    // 第1-10行：深渊坠落减少经验
    if (cause == Chasm.class){
        if (EXP % 2 == 1) EXP += Random.Int(2);
        EXP /= 2;
    }

    // 第11-30行：敌方死亡处理
    if (alignment == Alignment.ENEMY){
        // 危险辅助击杀统计
        if (buff(Trap.HazardAssistTracker.class) != null){
            Statistics.hazardAssistedKills++;
            Badges.validateHazardAssists();
        }

        rollToDropLoot();  // 掉落物品

        // 致命动能天赋
        if (cause == Dungeon.hero || cause instanceof Weapon || cause instanceof Weapon.Enchantment){
            if (Dungeon.hero.hasTalent(Talent.LETHAL_MOMENTUM)
                    && Random.Float() < 0.34f + 0.33f* Dungeon.hero.pointsInTalent(Talent.LETHAL_MOMENTUM)){
                Buff.affect(Dungeon.hero, Talent.LethalMomentumTracker.class, 0f);
            }
            // 致命急速天赋
            if (Dungeon.hero.heroClass != HeroClass.DUELIST
                    && Dungeon.hero.hasTalent(Talent.LETHAL_HASTE)
                    && Dungeon.hero.buff(Talent.LethalHasteCooldown.class) == null){
                Buff.affect(Dungeon.hero, Talent.LethalHasteCooldown.class, 100f);
                Buff.affect(Dungeon.hero, GreaterHaste.class).set(2 + 2*Dungeon.hero.pointsInTalent(Talent.LETHAL_HASTE));
            }
        }
    }

    // 第31-40行：日志
    if (Dungeon.hero.isAlive() && !Dungeon.level.heroFOV[pos]) {
        GLog.i( Messages.get(this, "died") );
    }

    boolean soulMarked = buff(SoulMark.class) != null;
    super.die( cause );

    // 第41-55行：死灵法师仆从天赋
    if (!(this instanceof Wraith)
            && soulMarked
            && Random.Float() < (0.4f*Dungeon.hero.pointsInTalent(Talent.NECROMANCERS_MINIONS)/3f)){
        Wraith w = Wraith.spawnAt(pos, Wraith.class);
        if (w != null) {
            Buff.affect(w, Corruption.class);  // 变成盟友
            if (Dungeon.level.heroFOV[pos]) {
                CellEmitter.get(pos).burst(ShadowParticle.CURSE, 6);
                Sample.INSTANCE.play(Assets.Sounds.CURSED);
            }
        }
    }
}
```

**方法作用**：处理怪物死亡。

**参数**：
- `cause` (Object)：死亡原因

**死亡处理**：
1. 深渊坠落减少经验
2. 掉落物品
3. 触发天赋效果
4. 记录日志
5. 死灵法师天赋召唤亡灵

---

### rollToDropLoot()

```java
public void rollToDropLoot(){
    // 第1-3行：等级过高不掉落
    if (Dungeon.hero.lvl > maxLvl + 2) return;

    MasterThievesArmband.StolenTracker stolen = buff(MasterThievesArmband.StolenTracker.class);
    if (stolen == null || !stolen.itemWasStolen()) {
        if (Random.Float() < lootChance()) {
            Item loot = createLoot();
            if (loot != null) {
                Dungeon.level.drop(loot, pos).sprite.drop();
            }
        }
    }
    
    // 第4-15行：财富之戒额外掉落
    if (Ring.getBuffedBonus(Dungeon.hero, RingOfWealth.Wealth.class) > 0) {
        int rolls = 1;
        if (properties.contains(Property.BOSS)) rolls = 15;
        else if (properties.contains(Property.MINIBOSS)) rolls = 5;
        ArrayList<Item> bonus = RingOfWealth.tryForBonusDrop(Dungeon.hero, rolls);
        if (bonus != null && !bonus.isEmpty()) {
            for (Item b : bonus) Dungeon.level.drop(b, pos).sprite.drop();
            RingOfWealth.showFlareForBonusDrop(sprite);
        }
    }
    
    // 第16-20行：幸运附魔
    if (buff(Lucky.LuckProc.class) != null){
        Dungeon.level.drop(buff(Lucky.LuckProc.class).genLoot(), pos).sprite.drop();
        Lucky.showFlare(sprite);
    }

    // 第21-25行：灵魂 eater 天赋
    if (buff(SoulMark.class) != null &&
            Random.Int(10) < Dungeon.hero.pointsInTalent(Talent.SOUL_EATER)){
        Talent.onFoodEaten(Dungeon.hero, 0, null);
    }
}
```

**方法作用**：处理战利品掉落。

**掉落机制**：
1. 基础掉落（基于 lootChance）
2. 财富之戒额外掉落
3. 幸运附魔额外掉落
4. 灵魂 eater 天赋触发

---

### destroy()

```java
@Override
public void destroy() {
    super.destroy();
    
    Dungeon.level.mobs.remove( this );  // 从关卡移除

    // 第1-10行：心眼更新视野
    if (Dungeon.hero.buff(MindVision.class) != null){
        Dungeon.observe();
        GameScene.updateFog(pos, 2);
    }

    // 第11-40行：敌方死亡统计
    if (Dungeon.hero.isAlive()) {
        if (alignment == Alignment.ENEMY) {
            Statistics.enemiesSlain++;
            Badges.validateMonstersSlain();
            Statistics.qualifiedForNoKilling = false;
            Bestiary.setSeen(getClass());
            Bestiary.countEncounter(getClass());

            AscensionChallenge.processEnemyKill(this);
            
            // 计算经验
            int exp = Dungeon.hero.lvl <= maxLvl ? EXP : 0;
            // 飞升模式的特殊经验计算
            if (Dungeon.hero.buff(AscensionChallenge.class) != null &&
                    exp == 0 && maxLvl > 0 && EXP > 0 && Dungeon.hero.lvl < Hero.MAX_LEVEL){
                exp = Math.round(10 * spawningWeight());
            }

            if (exp > 0) {
                Dungeon.hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(exp), FloatingText.EXPERIENCE);
            }
            Dungeon.hero.earnExp(exp, getClass());

            // 武僧能量
            if (Dungeon.hero.subClass == HeroSubClass.MONK){
                Buff.affect(Dungeon.hero, MonkEnergy.class).gainEnergy(this);
            }
        }
    }
}
```

**方法作用**：完全销毁怪物，更新统计。

---

### surprisedBy(Char enemy)

```java
public final boolean surprisedBy( Char enemy ){
    return surprisedBy( enemy, true);
}

public boolean surprisedBy( Char enemy, boolean attacking ){
    return enemy == Dungeon.hero
            && (enemy.invisible > 0 || !enemySeen || (fieldOfView != null && !fieldOfView[enemy.pos]))
            && (!attacking || enemy.canSurpriseAttack());
}
```

**方法作用**：判断是否被偷袭。

**参数**：
- `enemy` (Char)：攻击者
- `attacking` (boolean)：是否是攻击时的判断

**返回值**：是否被偷袭

**偷袭条件**：
- 攻击者是英雄
- 英雄隐形 或 怪物未发现英雄 或 英雄不在视野内

---

### aggro(Char ch)

```java
public void aggro( Char ch ) {
    enemy = ch;
    if (state != PASSIVE){
        state = HUNTING;
    }
}
```

**方法作用**：使怪物对指定角色产生仇恨。

---

### beckon(int cell)

```java
public void beckon( int cell ) {
    notice();
    if (state != HUNTING && state != FLEEING) {
        state = WANDERING;
    }
    target = cell;
}
```

**方法作用**：吸引怪物前往指定位置。

---

## 静态方法详解

### holdAllies(Level level, int holdFromPos)

```java
public static void holdAllies( Level level, int holdFromPos ){
    heldAllies.clear();
    for (Mob mob : level.mobs.toArray( new Mob[0] )) {
        // 保存可指挥盟友或强化智能盟友
        if (mob instanceof DirectableAlly
            || (mob.intelligentAlly && PowerOfMany.getPoweredAlly() == mob)) {
            level.mobs.remove( mob );
            heldAllies.add(mob);
        // 保存附近的智能盟友
        } else if (mob.alignment == Alignment.ALLY
                && mob.intelligentAlly
                && Dungeon.level.distance(holdFromPos, mob.pos) <= 5){
            level.mobs.remove( mob );
            heldAllies.add(mob);
        }
    }
}
```

**方法作用**：在切换关卡前保存盟友。

---

### restoreAllies(Level level, int pos, int gravitatePos)

```java
public static void restoreAllies( Level level, int pos, int gravitatePos ){
    if (!heldAllies.isEmpty()){
        // 计算可用位置
        ArrayList<Integer> candidatePositions = new ArrayList<>();
        for (int i : PathFinder.NEIGHBOURS8) {
            if (!Dungeon.level.solid[i+pos] && !Dungeon.level.avoid[i+pos] 
                && level.findMob(i+pos) == null){
                candidatePositions.add(i+pos);
            }
        }

        // 按距离排序或随机打乱
        if (gravitatePos == -1) {
            Collections.shuffle(candidatePositions);
        } else {
            Collections.sort(candidatePositions, ...);
        }

        // 处理"力量共享"冲突
        if (Stasis.getStasisAlly() != null){
            for (Mob mob : level.mobs.toArray( new Mob[0] )) {
                if (mob.buff(PowerOfMany.PowerBuff.class) != null){
                    mob.buff(PowerOfMany.PowerBuff.class).detach();
                }
            }
        }
        
        // 恢复盟友
        for (Mob ally : heldAllies) {
            level.mobs.add(ally);
            ally.state = ally.WANDERING;
            ally.pos = candidatePositions.isEmpty() ? pos : candidatePositions.remove(0);
            if (ally.sprite != null) ally.sprite.place(ally.pos);
            Dungeon.level.updateFieldOfView( ally, ally.fieldOfView );
        }
    }
    heldAllies.clear();
}
```

**方法作用**：在进入新关卡后恢复盟友。

---

## 与其他类的交互

### 被哪些类继承

| 类名 | 说明 |
|------|------|
| `NPC` | 非玩家角色基类 |
| `Elemental` | 元素生物基类 |
| `Shaman` | 萨满基类 |
| `YogFist` | Yog-Dzewa的拳头基类 |
| 所有具体怪物 | 如Rat、Gnoll、Crab等 |

### 使用了哪些类

| 类名 | 用于什么目的 |
|------|-------------|
| `Char` | 基础角色功能 |
| `AiState` | AI状态接口 |
| `PathFinder` | 寻路算法 |
| `Dungeon` | 访问关卡和英雄 |
| `Buff` | 状态效果 |

---

## 11. 使用示例

### 创建自定义怪物

```java
public class CustomMonster extends Mob {
    {
        spriteClass = CustomMonsterSprite.class;
        HP = HT = 100;
        defenseSkill = 15;
        
        EXP = 20;
        maxLvl = 25;
        
        loot = Gold.class;
        lootChance = 0.5f;
        
        properties.add(Property.UNDEAD);
    }
    
    @Override
    public int damageRoll() {
        return Random.NormalIntRange(10, 25);
    }
    
    @Override
    public int attackSkill(Char target) {
        return 20;
    }
    
    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 10);
    }
    
    @Override
    protected boolean act() {
        // 自定义行为
        return super.act();
    }
}
```

### 状态切换示例

```java
// 激怒怪物
mob.aggro(Dungeon.hero);

// 吸引怪物
mob.beckon(targetPos);

// 强制进入逃跑状态
Buff.affect(mob, Terror.class, 10f);
```

---

## 注意事项

### AI状态切换

1. **状态切换时机**：通过Buff自动切换（如恐惧进入FLEEING）
2. **状态持久化**：状态会随存档保存和恢复
3. **递归防护**：Hunting状态使用 `recursing` 标志防止无限递归

### 性能优化

1. **路径缓存**：`path` 字段缓存寻路结果
2. **延迟寻路**：只在必要时重新计算路径

### 常见的坑

1. **忘记设置属性**：新怪物需要设置HP、防御、攻击等
2. **状态循环**：自定义状态时要避免无限循环
3. **路径死锁**：无法到达目标时需要处理

### 最佳实践

1. 继承时实现 `damageRoll()`、`attackSkill()`、`drRoll()`
2. 使用 `properties.add()` 添加属性标签
3. 自定义AI时继承对应的状态类