# Char 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/Char.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors |
| **类类型** | abstract class |
| **继承关系** | extends Actor |
| **代码行数** | 1415 |

---

## 类职责

Char 是游戏中所有"角色"的抽象基类，代表地图上有位置、有生命值、可以移动和战斗的实体。它扩展了 Actor 的回合系统，添加了：

1. **位置与移动**：地图坐标、移动逻辑、飞行能力
2. **生命系统**：HP/HT、伤害计算、死亡处理
3. **战斗系统**：攻击/防御技能、命中率计算、伤害修正
4. **状态效果**：Buff 管理、免疫/抗性系统
5. **视野系统**：视距、可见范围计算

---

## 4. 继承与协作关系

```mermaid
classDiagram
    class Char {
        <<abstract>>
        +int pos
        +CharSprite sprite
        +int HT
        +int HP
        #float baseSpeed
        +int paralysed
        +boolean rooted
        +boolean flying
        +int invisible
        +Alignment alignment
        +int viewDistance
        +boolean[] fieldOfView
        -LinkedHashSet~Buff~ buffs
        +attack(Char, float, float, float) boolean
        +damage(int, Object) void
        +move(int, boolean) void
        +speed() float
        +shielding() int
        +isAlive() boolean
        +add(Buff) boolean
        +remove(Buff) boolean
        +isImmune(Class) boolean
        +resist(Class) float
        +properties() HashSet~Property~
    }
    
    class Actor {
        <<abstract>>
        +float time
        +int id
        #abstract act() boolean
    }
    
    class Mob {
        <<abstract>>
        +AiState state
        +Char enemy
        +int EXP
    }
    
    class Hero {
        +Belongings belongings
        +HeroClass heroClass
        +HeroAction curAction
    }
    
    Actor <|-- Char
    Char <|-- Mob
    Char <|-- Hero
    
    class Alignment {
        <<enumeration>>
        ENEMY
        NEUTRAL
        ALLY
    }
    
    Char +-- Alignment
```

---

## 枚举定义

### Alignment（阵营）

```java
public enum Alignment{
    ENEMY,    // 敌对阵营（对玩家敌对）
    NEUTRAL,  // 中立阵营（不参与战斗）
    ALLY      // 友方阵营（与玩家同阵营）
}
```

**用途**：决定角色之间的敌友关系，影响战斗AI和技能效果

---

### Property（属性标签）

```java
public enum Property{
    BOSS (resistances, immunities),      // Boss敌人
    MINIBOSS (resistances, immunities),  // 小Boss
    BOSS_MINION,                          // Boss随从
    UNDEAD,                               // 亡灵
    DEMONIC,                              // 恶魔
    INORGANIC (resistances, immunities),  // 无机物
    FIERY (resistances, immunities),      // 火焰
    ICY (resistances, immunities),        // 冰霜
    ACIDIC (resistances, immunities),     // 酸性
    ELECTRIC (resistances, immunities),   // 电系
    LARGE,                                // 大型生物（占2x2格子）
    IMMOVABLE (resistances, immunities),  // 不可移动
    STATIC (resistances, immunities);     // 静态AI
    
    private HashSet<Class> resistances;  // 该属性的固有抗性
    private HashSet<Class> immunities;   // 该属性的固有免疫
}
```

**关键属性说明**：

| 属性 | 特殊效果 |
|------|----------|
| BOSS | 免疫恐怖效果，对Grim武器有抗性 |
| UNDEAD | 对牧师治疗有特殊反应 |
| INORGANIC | 免疫流血、毒气、中毒 |
| FIERY | 免疫燃烧，对火系法杖有抗性 |
| ICY | 免疫冰冻、寒冷，对冰系法杖有抗性 |
| ELECTRIC | 对电系法杖有抗性 |
| LARGE | 占据2x2格子，不能进入狭窄区域 |
| IMMOVABLE | 不能被推拉或交换位置 |
| STATIC | 免疫所有AI干扰效果（恐惧、魅惑、狂暴等） |

---

## 实例字段

### 位置与视觉

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `pos` | int | 0 | 当前在地图上的位置（格子索引） |
| `sprite` | CharSprite | null | 角色的视觉精灵 |
| `viewDistance` | int | 8 | 视野距离 |
| `fieldOfView` | boolean[] | null | 可见范围布尔数组 |

### 生命值

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `HT` | int | 0 | 最大生命值（Health Total） |
| `HP` | int | 0 | 当前生命值 |

### 移动与状态

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `baseSpeed` | float | 1.0f | 基础移动速度 |
| `paralysed` | int | 0 | 麻痹计数（>0时无法行动） |
| `rooted` | boolean | false | 是否被定身 |
| `flying` | boolean | false | 是否在飞行（无视地形障碍） |
| `invisible` | int | 0 | 隐形计数（>0时隐形） |

### Buff管理

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `buffs` | LinkedHashSet&lt;Buff&gt; | new | 附加的状态效果集合 |
| `cachedShield` | int | 0 | 缓存的护盾值 |
| `needsShieldUpdate` | boolean | true | 是否需要更新护盾缓存 |

### 抗性与免疫

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `resistances` | HashSet&lt;Class&gt; | new | 固有抗性列表 |
| `immunities` | HashSet&lt;Class&gt; | new | 固有免疫列表 |
| `properties` | HashSet&lt;Property&gt; | new | 属性标签集合 |

### 其他

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `alignment` | Alignment | null | 阵营 |
| `deathMarked` | boolean | false | 是否被死亡标记（死亡动画中） |

---

## 7. 方法详解

### act()

```java
@Override
protected boolean act() {
    // 第1-3行：初始化/更新视野数组
    if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
        fieldOfView = new boolean[Dungeon.level.length()];
    }
    Dungeon.level.updateFieldOfView( this, fieldOfView );

    // 第4-7行：不可移动角色会踢开身上的物品
    if (properties().contains(Property.IMMOVABLE)){
        throwItems();
    }
    return false;  // 返回false表示需要更多处理（子类会继续）
}
```

**方法作用**：角色每回合的基础行动逻辑。

**重写来源**：Actor.act()

**返回值**：false（基础实现不做完整行动，由子类完成）

**执行流程**：
1. 检查视野数组大小是否正确
2. 更新视野（调用Level的视野计算）
3. 如果是不可移动角色，踢开脚下的物品堆

---

### throwItems()

```java
protected void throwItems(){
    Heap heap = Dungeon.level.heaps.get( pos );  // 第1行：获取脚下物品堆
    // 第2-4行：检查物品堆类型，排除特殊物品
    if (heap != null && heap.type == Heap.Type.HEAP
            && !(heap.peek() instanceof Tengu.BombAbility.BombItem)
            && !(heap.peek() instanceof Tengu.ShockerAbility.ShockerItem)) {
        ArrayList<Integer> candidates = new ArrayList<>();
        // 第5-9行：找周围8格的可行位置
        for (int n : PathFinder.NEIGHBOURS8){
            if (Dungeon.level.passable[pos+n]){
                candidates.add(pos+n);
            }
        }
        // 第10-14行：随机选一个位置扔过去
        if (!candidates.isEmpty()){
            Dungeon.level.drop( heap.pickUp(), Random.element(candidates) ).sprite.drop( pos );
        }
    }
}
```

**方法作用**：将脚下的物品堆扔到周围随机位置。

**使用场景**：不可移动角色（如陷阱、雕像）防止物品堆积在脚下

---

### canInteract(Char c)

```java
public boolean canInteract(Char c){
    // 第1-3行：相邻可以直接交互
    if (Dungeon.level.adjacent( pos, c.pos )){
        return true;
    // 第4-8行：友方单位可以使用天赋远程交互
    } else if (c instanceof Hero
            && alignment == Alignment.ALLY
            && !hasProp(this, Property.IMMOVABLE)
            && Dungeon.level.distance(pos, c.pos) <= 2*Dungeon.hero.pointsInTalent(Talent.ALLY_WARP)){
        return true;
    } else {
        return false;
    }
}
```

**方法作用**：判断另一个角色是否可以与当前角色交互。

**参数**：
- `c` (Char)：要交互的角色

**返回值**：是否可以交互

**交互条件**：
1. 相邻格子
2. 或：是友方 + 不是不可移动 + 在天赋范围内

---

### interact(Char c)

```java
public boolean interact(Char c){
    // 第1-14行：各种不能交换的情况检查
    // 不能交换到危险地形（除非飞行）
    if (!Dungeon.level.passable[pos] && !c.flying){
        return true;
    }
    // 大型生物需要开阔空间
    if (properties().contains(Property.LARGE) && !Dungeon.level.openSpace[c.pos]
        || c.properties().contains(Property.LARGE) && !Dungeon.level.openSpace[pos]){
        return true;
    }
    // 不可移动角色不能交换
    if (hasProp(this, Property.IMMOVABLE) || hasProp(c, Property.IMMOVABLE)){
        return true;
    }

    // 第15-28行：处理天赋传送交换
    if (c == Dungeon.hero && Dungeon.hero.hasTalent(Talent.ALLY_WARP)){
        // 构建距离地图检查可达性
        PathFinder.buildDistanceMap(c.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
        if (PathFinder.distance[pos] == Integer.MAX_VALUE){
            return true;  // 不可达
        }
        // 执行传送交换
        pos = newPos;
        c.pos = oldPos;
        ScrollOfTeleportation.appear(this, newPos);
        ScrollOfTeleportation.appear(c, oldPos);
        return true;
    }

    // 第29-40行：检查移动限制
    if (paralysed > 0 || c.paralysed > 0 || rooted || c.rooted
            || buff(Vertigo.class) != null || c.buff(Vertigo.class) != null){
        return true;
    }

    // 第41-50行：执行普通位置交换
    c.pos = oldPos;
    moveSprite( oldPos, newPos );
    move( newPos );
    c.pos = newPos;
    c.sprite.move( newPos, oldPos );
    c.move( oldPos );
    c.spend( 1 / c.speed() );  // 交换消耗时间

    return true;
}
```

**方法作用**：与另一个角色交互（默认行为是交换位置）。

**参数**：
- `c` (Char)：要交互的角色

**返回值**：true表示交互成功

**交换失败条件**：
- 一方站在危险地形且不能飞行
- 大型生物没有足够空间
- 一方不可移动
- 一方麻痹或定身
- 一方有眩晕效果

---

### attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti)

```java
public boolean attack( Char enemy, float dmgMulti, float dmgBonus, float accMulti ) {

    if (enemy == null) return false;  // 第1行：空目标检查
    
    boolean visibleFight = Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[enemy.pos];

    // 第2-15行：无敌状态检查
    if (enemy.isInvulnerable(getClass())) {
        if (visibleFight) {
            enemy.sprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "invulnerable") );
            Sample.INSTANCE.play(Assets.Sounds.HIT_PARRY, 1f, Random.Float(0.96f, 1.05f));
        }
        return false;  // 无效攻击

    // 第16-200+行：命中判定流程
    } else if (hit( this, enemy, accMulti, false )) {
        
        // 计算防御减免
        int dr = Math.round(enemy.drRoll() * AscensionChallenge.statModifier(enemy));
        
        // 狙击手天赋忽略防御
        if (this instanceof Hero){
            Hero h = (Hero)this;
            if (h.belongings.attackingWeapon() instanceof MissileWeapon
                    && h.subClass == HeroSubClass.SNIPER
                    && !Dungeon.level.adjacent(h.pos, enemy.pos)){
                dr = 0;
            }
        }

        // 计算基础伤害
        float dmg;
        Preparation prep = buff(Preparation.class);
        if (prep != null){
            dmg = prep.damageRoll(this);  // 刺客准备攻击
        } else {
            dmg = damageRoll();  // 普通伤害
        }

        dmg = dmg * dmgMulti + dmgBonus;  // 应用伤害倍率和加成

        // 应用各种伤害修正...
        // (省略大量Buff和天赋的修正代码)

        // 执行防御处理
        int effectiveDamage = enemy.defenseProc( this, Math.round(dmg) );
        effectiveDamage = Math.max(effectiveDamage - dr, 0);  // 应用防御

        // 应用易伤效果
        if (enemy.buff(Vulnerable.class) != null) {
            effectiveDamage *= 1.33f;
        }

        effectiveDamage = attackProc(enemy, effectiveDamage);  // 攻击处理

        // 造成伤害
        enemy.damage( effectiveDamage, this );

        // 刺客处决检查
        if (enemy.isAlive() && enemy.alignment != alignment && prep != null && prep.canKO(enemy)){
            enemy.HP = 0;
            if (!enemy.isAlive()) {
                enemy.die(this);
            }
        }

        return true;
        
    } else {
        // 未命中
        if (enemy.sprite != null){
            enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
        }
        Sample.INSTANCE.play(Assets.Sounds.MISS);
        return false;
    }
}
```

**方法作用**：执行对敌人的攻击，包含完整的战斗计算流程。

**参数**：
- `enemy` (Char)：攻击目标
- `dmgMulti` (float)：伤害倍率（默认1.0）
- `dmgBonus` (float)：固定伤害加成（默认0）
- `accMulti` (float)：命中倍率（默认1.0）

**返回值**：
- `true`：攻击命中（不一定造成伤害）
- `false`：攻击未命中或目标无敌

**执行流程**：
1. 检查目标是否存在
2. 检查目标是否无敌
3. 进行命中判定
4. 计算防御减免
5. 计算基础伤害
6. 应用各种伤害修正（Buff、天赋等）
7. 调用目标的防御处理
8. 造成伤害
9. 检查特殊效果（处决等）

---

### hit(Char attacker, Char defender, float accMulti, boolean magic)

```java
public static boolean hit( Char attacker, Char defender, float accMulti, boolean magic ) {
    float acuStat = attacker.attackSkill( defender );  // 攻击方命中
    float defStat = defender.defenseSkill( attacker ); // 防御方闪避

    // 第1-4行：被打断时中断防御
    if (defender instanceof Hero && ((Hero) defender).damageInterrupt){
        ((Hero) defender).interrupt();
    }

    // 第5-8行：隐形攻击者必定命中（偷袭）
    if (attacker.invisible > 0 && attacker.canSurpriseAttack()){
        acuStat = INFINITE_ACCURACY;
    }

    // 第9-12行：武僧专注状态必定闪避
    if (defender.buff(MonkEnergy.MonkAbility.Focus.FocusBuff.class) != null){
        defStat = INFINITE_EVASION;
    }

    // 第13-22行：无限命中/闪避检查
    // 无限闪避胜过无限命中
    if (defStat >= INFINITE_EVASION){
        hitMissIcon = FloatingText.getMissReasonIcon(attacker, acuStat, defender, INFINITE_EVASION);
        return false;
    } else if (acuStat >= INFINITE_ACCURACY){
        hitMissIcon = FloatingText.getHitReasonIcon(attacker, INFINITE_ACCURACY, defender, defStat);
        return true;
    }

    // 第23-45行：掷骰子计算
    float acuRoll = Random.Float( acuStat );  // 攻击方掷骰
    // 应用命中修正（祝福、诅咒、眩晕、飞升等）
    if (attacker.buff(Bless.class) != null) acuRoll *= 1.25f;  // 祝福+25%
    if (attacker.buff(Hex.class) != null) acuRoll *= 0.8f;     // 诅咒-20%
    if (attacker.buff(Daze.class) != null) acuRoll *= 0.5f;    // 眩晕-50%
    acuRoll *= accMulti;  // 应用命中倍率

    float defRoll = Random.Random( defStat );  // 防御方掷骰
    // 应用闪避修正
    // ...

    // 第46-55行：比较结果
    if (acuRoll >= defRoll){
        hitMissIcon = FloatingText.getHitReasonIcon(attacker, acuRoll, defender, defRoll);
        return true;   // 命中
    } else {
        hitMissIcon = FloatingText.getMissReasonIcon(attacker, acuRoll, defender, defRoll);
        return false;  // 未命中
    }
}
```

**方法作用**：判定攻击是否命中。

**参数**：
- `attacker` (Char)：攻击者
- `defender` (Char)：防御者
- `accMulti` (float)：命中倍率
- `magic` (boolean)：是否是魔法攻击（魔法有2倍命中加成）

**返回值**：是否命中

**命中判定机制**：
1. 攻击方在 [0, 攻击技能] 范围内随机取值
2. 防御方在 [0, 防御技能] 范围内随机取值
3. 应用各种修正
4. 攻击值 >= 防御值则命中

**特殊规则**：
- 隐形攻击者偷袭 = 无限命中
- 武僧专注 = 无限闪避
- 无限闪避 > 无限命中

---

### damage(int dmg, Object src)

```java
public void damage( int dmg, Object src ) {
    
    // 第1-3行：无效检查
    if (!isAlive() || dmg < 0) {
        return;
    }

    // 第4-7行：无敌检查
    if(isInvulnerable(src.getClass())){
        sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "invulnerable"));
        return;
    }

    // 第8-25行：生命链接伤害分摊
    if (!(src instanceof LifeLink || src instanceof Hunger) && buff(LifeLink.class) != null){
        HashSet<LifeLink> links = buffs(LifeLink.class);
        // 计算分摊伤害
        dmg = (int)Math.ceil(dmg / (float)(links.size()+1));
        // 对链接目标造成伤害
        for (LifeLink link : links){
            Char ch = (Char)Actor.findById(link.object);
            if (ch != null) {
                ch.damage(dmg, link);
            }
        }
    }

    // 第26-80行：伤害修正计算
    float damage = dmg;
    // 应用各种修正...
    // 光环保护、生命链接法术、恐惧恢复、冰霜解除、
    // 厄运增伤、死亡标记增伤、收割镰刀特殊处理等

    // 第81-90行：抗性与免疫
    Class<?> srcClass = src.getClass();
    if (isImmune( srcClass )) {
        damage = 0;
    } else {
        damage *= resist( srcClass );
    }

    dmg = Math.round(damage);

    // 第91-100行：冠军敌人减伤
    for (ChampionEnemy buff : buffs(ChampionEnemy.class)){
        dmg = (int) Math.ceil(dmg * buff.damageTakenFactor());
    }

    // 第101-110行：魔法抗性
    if (AntiMagic.RESISTS.contains(src.getClass())){
        dmg -= AntiMagic.drRoll(this, glyphLevel(AntiMagic.class));
        if (buff(ArcaneArmor.class) != null) {
            dmg -= Random.NormalIntRange(0, buff(ArcaneArmor.class).level());
        }
        if (dmg < 0) dmg = 0;
    }

    // 第111-120行：护盾吸收
    int shielded = dmg;
    dmg = ShieldBuff.processDamage(this, dmg, src);
    shielded -= dmg;
    HP -= dmg;

    // 第121-130行：死神武器特殊处理
    if (HP > 0 && buff(Grim.GrimTracker.class) != null){
        float finalChance = buff(Grim.GrimTracker.class).maxChance;
        finalChance *= (float)Math.pow( ((HT - HP) / (float)HT), 2);  // 基于已损生命值
        if (Random.Float() < finalChance) {
            int extraDmg = Math.round(HP * resist(Grim.class));
            HP -= extraDmg;  // 额外伤害直接致死
        }
    }

    // 第131-150行：动能守恒处理（溢出伤害）
    if (HP < 0 && src instanceof Char && alignment == Alignment.ENEMY){
        // 将溢出伤害保存到攻击者的动能守恒中
    }

    // 第151-180行：显示伤害数字
    if (sprite != null) {
        int icon = FloatingText.PHYS_DMG;  // 默认物理伤害图标
        // 根据伤害来源选择图标...
        sprite.showStatusWithIcon(CharSprite.NEGATIVE, Integer.toString(dmg + shielded), icon);
    }

    // 第181-190行：死亡检查
    if (HP < 0) HP = 0;
    if (!isAlive()) {
        die( src );
    }
}
```

**方法作用**：对角色造成伤害，包含完整的伤害计算流程。

**参数**：
- `dmg` (int)：基础伤害值
- `src` (Object)：伤害来源（可以是Char、Buff、物品等）

**伤害计算流程**：
1. 检查是否存活、是否为负伤害
2. 检查无敌状态
3. 处理生命链接分摊
4. 应用各种伤害修正
5. 应用抗性/免疫
6. 护盾吸收
7. 扣除HP
8. 特殊效果处理（死神、动能守恒）
9. 显示伤害数字
10. 检查死亡

---

### speed()

```java
public float speed() {
    float speed = baseSpeed;
    // 第1-4行：减益效果
    if ( buff( Cripple.class ) != null ) speed /= 2f;  // 残疾减速50%
    
    // 第5-8行：增益效果
    if ( buff( Stamina.class ) != null) speed *= 1.5f;      // 耐力+50%
    if ( buff( Adrenaline.class ) != null) speed *= 2f;     // 激素涌动+100%
    if ( buff( Haste.class ) != null) speed *= 3f;          // 急速+200%
    if ( buff( Dread.class ) != null) speed *= 2f;          // 恐惧+100%

    // 第9-12行：护甲符文效果
    speed *= Swiftness.speedBoost(this, glyphLevel(Swiftness.class));  // 迅捷符文
    speed *= Flow.speedBoost(this, glyphLevel(Flow.class));            // 流动符文
    speed *= Bulk.speedBoost(this, glyphLevel(Bulk.class));            // 巨型符文

    return speed;
}
```

**方法作用**：计算角色的实际移动速度。

**返回值**：最终速度值（相对于基准1.0）

**速度修正**：
- 残疾：-50%
- 耐力：+50%
- 激素涌动：+100%
- 急速：+200%
- 恐惧：+100%
- 符文效果：根据符文等级计算

---

### shielding()

```java
public int shielding(){
    // 第1-4行：使用缓存
    if (!needsShieldUpdate){
        return cachedShield;
    }
    
    // 第5-9行：重新计算
    cachedShield = 0;
    for (ShieldBuff s : buffs(ShieldBuff.class)){
        cachedShield += s.shielding();  // 累加所有护盾Buff
    }
    needsShieldUpdate = false;
    return cachedShield;
}
```

**方法作用**：获取角色的护盾总量。

**返回值**：护盾值（整数）

**缓存机制**：只在护盾变化时重新计算，避免频繁遍历Buff

---

### move(int step, boolean travelling)

```java
public void move( int step, boolean travelling ) {

    // 第1-15行：眩晕时的随机移动
    if (travelling && Dungeon.level.adjacent( step, pos ) && buff( Vertigo.class ) != null) {
        sprite.interruptMotion();
        int newPos = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
        // 检查目标位置有效性
        if (!(Dungeon.level.passable[newPos] || Dungeon.level.avoid[newPos])
                || (properties().contains(Property.LARGE) && !Dungeon.level.openSpace[newPos])
                || Actor.findChar( newPos ) != null)
            return;  // 无效位置，取消移动
        else {
            sprite.move(pos, newPos);
            step = newPos;  // 随机改变目标
        }
    }

    // 第16-19行：关闭身后的门
    if (Dungeon.level.map[pos] == Terrain.OPEN_DOOR) {
        Door.leave( pos );
    }

    pos = step;  // 更新位置
    
    // 第20-23行：更新可见性
    if (this != Dungeon.hero) {
        sprite.visible = Dungeon.level.heroFOV[pos];
    }
    
    Dungeon.level.occupyCell(this);  // 占据新格子
}
```

**方法作用**：执行角色移动到新位置。

**参数**：
- `step` (int)：目标位置
- `travelling` (boolean)：是否是"行进"（非瞬移）

**移动逻辑**：
1. 眩晕时可能随机偏移方向
2. 自动关闭身后的门
3. 更新位置
4. 更新可见性
5. 占据新格子（触发陷阱等）

---

### add(Buff buff)

```java
public synchronized boolean add( Buff buff ) {

    // 第1-10行：净化状态阻止负面Buff
    if (buff(PotionOfCleansing.Cleanse.class) != null) {
        if (buff.type == Buff.buffType.NEGATIVE
                && !(buff instanceof AllyBuff)
                && !(buff instanceof LostInventory)){
            return false;
        }
    }

    // 第11-14行：观战冻结时不能添加Buff
    if (sprite != null && buff(Challenge.SpectatorFreeze.class) != null){
        return false;
    }

    buffs.add( buff );  // 添加到集合
    if (Actor.chars().contains(this)) Actor.add( buff );  // 注册到Actor系统

    // 第15-25行：显示Buff名称
    if (sprite != null && buff.announced) {
        switch (buff.type) {
            case POSITIVE:
                sprite.showStatus(CharSprite.POSITIVE, Messages.titleCase(buff.name()));
                break;
            case NEGATIVE:
                sprite.showStatus(CharSprite.WARNING, Messages.titleCase(buff.name()));
                break;
            case NEUTRAL:
            default:
                sprite.showStatus(CharSprite.NEUTRAL, Messages.titleCase(buff.name()));
                break;
        }
    }

    return true;
}
```

**方法作用**：为角色添加一个Buff。

**参数**：
- `buff` (Buff)：要添加的Buff

**返回值**：是否成功添加

**添加失败条件**：
- 净化状态 + 负面Buff
- 观战冻结状态

---

### resist(Class effect)

```java
public float resist( Class effect ){
    // 收集所有抗性来源
    HashSet<Class> resists = new HashSet<>(resistances);  // 固有抗性
    for (Property p : properties()){
        resists.addAll(p.resistances());  // 属性抗性
    }
    for (Buff b : buffs()){
        resists.addAll(b.resistances());  // Buff抗性
    }
    
    // 第1-10行：计算最终抗性
    float result = 1f;
    for (Class c : resists){
        if (c.isAssignableFrom(effect)){
            result *= 0.5f;  // 每个抗性减半效果
        }
    }
    return result * RingOfElements.resist(this, effect);  // 加上元素戒指
}
```

**方法作用**：计算对指定效果的抗性系数。

**参数**：
- `effect` (Class)：效果类型

**返回值**：抗性系数（0.5 = 50%伤害，1.0 = 无抗性）

**抗性计算**：
- 固有抗性 + 属性抗性 + Buff抗性
- 每个匹配的抗性减半效果（乘以0.5）
- 最后乘以元素戒指效果

---

### isImmune(Class effect)

```java
public boolean isImmune(Class effect ){
    // 收集所有免疫来源
    HashSet<Class> immunes = new HashSet<>(immunities);  // 固有免疫
    for (Property p : properties()){
        immunes.addAll(p.immunities());  // 属性免疫
    }
    for (Buff b : buffs()){
        immunes.addAll(b.immunities());  // Buff免疫
    }
    // 硫磺符文免疫燃烧
    if (glyphLevel(Brimstone.class) >= 0){
        immunes.add(Burning.class);
    }
    
    // 检查是否匹配
    for (Class c : immunes){
        if (c.isAssignableFrom(effect)){
            return true;
        }
    }
    return false;
}
```

**方法作用**：判断角色是否对指定效果免疫。

**参数**：
- `effect` (Class)：效果类型

**返回值**：是否免疫

---

### isInvulnerable(Class effect)

```java
public boolean isInvulnerable( Class effect ){
    return buff(Challenge.SpectatorFreeze.class) != null  // 观战冻结
        || buff(Invulnerability.class) != null;           // 无敌Buff
}
```

**方法作用**：判断角色是否处于无敌状态。

**返回值**：是否无敌

**无敌来源**：
- 观战冻结
- 无敌Buff（如牧师技能）

---

### die(Object src)

```java
public void die( Object src ) {
    destroy();  // 清理角色
    
    // 第1-10行：播放死亡动画
    if (src != Chasm.class) {  // 深渊死亡不播放动画
        sprite.die();
        // 怪物在深渊格子上会掉落
        if (!flying && Dungeon.level != null && sprite instanceof MobSprite 
            && Dungeon.level.map[pos] == Terrain.CHASM){
            ((MobSprite) sprite).fall();
        }
    }
}
```

**方法作用**：处理角色死亡。

**参数**：
- `src` (Object)：死亡原因

---

### destroy()

```java
public void destroy() {
    HP = 0;
    Actor.remove( this );  // 从Actor系统移除

    // 清除指向该角色的所有Buff
    for (Char ch : Actor.chars().toArray(new Char[0])){
        if (ch.buff(Charm.class) != null && ch.buff(Charm.class).object == id()){
            ch.buff(Charm.class).detach();
        }
        if (ch.buff(Dread.class) != null && ch.buff(Dread.class).object == id()){
            ch.buff(Dread.class).detach();
        }
        if (ch.buff(Terror.class) != null && ch.buff(Terror.class).object == id()){
            ch.buff(Terror.class).detach();
        }
        if (ch.buff(SnipersMark.class) != null && ch.buff(SnipersMark.class).object == id()){
            ch.buff(SnipersMark.class).detach();
        }
        // ... 更多Buff清理
    }
}
```

**方法作用**：完全销毁角色，清理所有引用。

---

## 与其他类的交互

### 被哪些类使用

| 类名 | 如何使用 |
|------|----------|
| `Mob` | 扩展Char实现敌人AI |
| `Hero` | 扩展Char实现玩家角色 |
| `Buff` | 附加到Char上影响其属性 |
| `Level` | 更新视野、处理移动 |
| `Item` | 对Char使用物品 |
| `Weapon` | 计算对Char的伤害 |

### 使用了哪些类

| 类名 | 用于什么目的 |
|------|-------------|
| `Actor` | 基础回合系统 |
| `Buff` | 状态效果管理 |
| `Dungeon` | 访问关卡和英雄 |
| `CharSprite` | 视觉表现 |
| `PathFinder` | 寻路算法 |
| `Bundle` | 序列化 |

---

## 11. 使用示例

### 创建自定义角色

```java
public class CustomMob extends Mob {
    {
        spriteClass = CustomSprite.class;
        HP = HT = 50;
        defenseSkill = 10;
        alignment = Alignment.ENEMY;
        
        properties.add(Property.UNDEAD);  // 添加亡灵属性
        immunities.add(Poison.class);     // 免疫中毒
    }
    
    @Override
    public int damageRoll() {
        return Random.NormalIntRange(5, 15);
    }
    
    @Override
    protected boolean act() {
        boolean result = super.act();
        // 自定义AI逻辑
        return result;
    }
}
```

### 处理伤害

```java
// 造成伤害
enemy.damage(20, this);

// 检查抗性
float resistance = enemy.resist(Fire.class);
int actualDamage = Math.round(20 * resistance);

// 检查免疫
if (!enemy.isImmune(Fire.class)) {
    Buff.affect(enemy, Burning.class);
}
```

---

## 注意事项

### 性能优化

1. **护盾缓存**：使用 `cachedShield` 避免频繁遍历Buff
2. **死亡标记**：`deathMarked` 字段缓存存活状态，避免在渲染时调用 `isAlive()`

### 线程安全

1. `buffs` 集合的操作都是 `synchronized`
2. `add/remove Buff` 方法需要同步

### 常见的坑

1. **忘记更新护盾缓存**：修改护盾后设置 `needsShieldUpdate = true`
2. **直接修改HP**：应该使用 `damage()` 方法触发所有效果
3. **忽略无敌状态**：伤害计算前检查 `isInvulnerable()`

### 最佳实践

1. 继承 Char 时实现 `damageRoll()` 和 `attackSkill()`
2. 使用 `properties()` 而非直接访问 `properties` 字段（支持动态属性）
3. 移除角色时调用 `destroy()` 而非仅从关卡移除