# Dungeon 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/Dungeon.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon |
| **类类型** | class（静态工具类） |
| **继承关系** | 无继承 |
| **代码行数** | 1093 |

---

## 类职责

Dungeon 是游戏的**全局状态管理器**。它是一个静态工具类，管理：

1. **游戏状态**：当前关卡、深度、英雄
2. **存档系统**：游戏保存/加载
3. **关卡生成**：根据深度创建对应关卡
4. **视野系统**：英雄可见区域计算
5. **寻路系统**：角色移动路径计算
6. **限制掉落**：管理稀有物品的掉落数量

**设计模式**：
- **单例模式（变体）**：所有字段都是静态的
- **外观模式**：为复杂的游戏状态提供统一访问点

---

## 4. 继承与协作关系

```mermaid
classDiagram
    class Dungeon {
        +static Hero hero
        +static Level level
        +static int depth
        +static int branch
        +static int gold
        +static int energy
        +static int challenges
        +static long seed
        +static QuickSlot quickslot
        +static LimitedDrops limitedDrops
        +static init() void
        +static newLevel() Level
        +static switchLevel(Level, int) void
        +static saveAll() void
        +static loadGame(int) void
        +static observe() void
        +static findPath(Char, int, boolean[], boolean[], boolean) Path
        +static fail(Object) void
        +static win(Object) void
    }
    
    class Hero {
        +int pos
        +int HP
        +Belongings belongings
    }
    
    class Level {
        +int[] map
        +boolean[] visited
        +boolean[] heroFOV
        +List~Mob~ mobs
        +Map~Class,Blob~ blobs
    }
    
    class LimitedDrops {
        <<enumeration>>
        STRENGTH_POTIONS
        UPGRADE_SCROLLS
        ARCANE_STYLI
        ...
        +int count
        +dropped() boolean
        +drop() void
    }
    
    Dungeon +-- Hero
    Dungeon +-- Level
    Dungeon +-- LimitedDrops
```

---

## 枚举定义

### LimitedDrops（限制掉落）

```java
public static enum LimitedDrops {
    // 限制性世界掉落
    STRENGTH_POTIONS,    // 力量药水
    UPGRADE_SCROLLS,     // 升级卷轴
    ARCANE_STYLI,        // 奥术铁笔
    ENCH_STONE,          // 附魔石
    INT_STONE,           // 直觉石
    TRINKET_CATA,        // 饰品催化剂
    LAB_ROOM,            // 炼金实验室
    
    // 生命药水来源（敌人）
    SWARM_HP,            // 虫群
    NECRO_HP,            // 死灵法师
    BAT_HP,              // 蝙蝠
    WARLOCK_HP,          // 术士
    
    // 其他限制掉落
    COOKING_HP,          // 烹饪
    BLANDFRUIT_SEED,     // 淡果种子
    SLIME_WEP,           // 史莱姆武器
    SKELE_WEP,           // 骷髅武器
    THEIF_MISC,          // 小偷杂物
    GUARD_ARM,           // 守卫护甲
    SHAMAN_WAND,         // 萨满法杖
    DM200_EQUIP,         // DM200装备
    GOLEM_EQUIP,         // 魔像装备
    
    // 容器
    VELVET_POUCH,        // 丝绒袋
    SCROLL_HOLDER,       // 卷轴盒
    POTION_BANDOLIER,    // 药水袋
    MAGICAL_HOLSTER,     // 魔法枪套
    
    // 文档
    LORE_SEWERS,         // 下水道文档
    LORE_PRISON,         // 监狱文档
    LORE_CAVES,          // 洞穴文档
    LORE_CITY,           // 城市文档
    LORE_HALLS;          // 大厅文档

    public int count = 0;

    public boolean dropped(){
        return count != 0;
    }
    public void drop(){
        count = 1;
    }
}
```

**用途**：跟踪每种限制物品的掉落数量，确保游戏平衡

---

## 静态字段

### 核心游戏状态

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `hero` | Hero | 英雄实例 |
| `level` | Level | 当前关卡 |
| `depth` | int | 当前深度（1-26） |
| `branch` | int | 当前分支（0=主线，1=支线） |

### 资源

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `gold` | int | 金币数量 |
| `energy` | int | 能量晶体数量 |

### 游戏配置

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `challenges` | int | 挑战模式位掩码 |
| `mobsToChampion` | float | 冠军怪物计数器 |
| `seed` | long | 游戏种子 |
| `customSeedText` | String | 自定义种子文本 |
| `daily` | boolean | 是否是每日挑战 |
| `dailyReplay` | boolean | 是否是每日挑战重玩 |

### 版本信息

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `initialVersion` | int | 游戏开始时的版本号 |
| `version` | int | 当前版本号 |
| `lastPlayed` | long | 最后游戏时间 |

### 其他状态

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `quickslot` | QuickSlot | 快捷栏 |
| `chapters` | HashSet&lt;Integer&gt; | 已查看的章节 |
| `droppedItems` | SparseArray&lt;ArrayList&lt;Item&gt;&gt; | 掉落到深渊的物品 |
| `generatedLevels` | ArrayList&lt;Integer&gt; | 已生成的关卡列表 |

---

## 7. 方法详解

### init()

```java
public static void init() {
    initialVersion = version = Game.versionCode;
    challenges = SPDSettings.challenges();
    mobsToChampion = 1;

    Actor.clear();
    Actor.resetNextID();

    // 初始化随机数生成器（种子偏移避免模式）
    Random.pushGenerator( seed+1 );

        Scroll.initLabels();     // 初始化卷轴标签
        Potion.initColors();     // 初始化药水颜色
        Ring.initGems();         // 初始化戒指宝石

        SpecialRoom.initForRun();  // 初始化特殊房间
        SecretRoom.initForRun();   // 初始化秘密房间

        Generator.fullReset();     // 重置物品生成器

    Random.resetGenerators();
    
    Statistics.reset();  // 重置统计
    Notes.reset();       // 重置笔记

    quickslot.reset();
    QuickSlotButton.reset();
    
    depth = 1;
    branch = 0;
    generatedLevels.clear();

    gold = 0;
    energy = 0;

    droppedItems = new SparseArray<>();

    LimitedDrops.reset();
    
    chapters = new HashSet<>();
    
    // 重置任务
    Ghost.Quest.reset();
    Wandmaker.Quest.reset();
    Blacksmith.Quest.reset();
    Imp.Quest.reset();

    hero = new Hero();
    hero.live();
    
    Badges.reset();
    
    GamesInProgress.selectedClass.initHero( hero );
}
```

**方法作用**：初始化新游戏。

**执行流程**：
1. 设置版本和挑战模式
2. 清空 Actor 系统
3. 使用种子初始化随机数
4. 初始化卷轴、药水、戒指的随机属性
5. 重置所有游戏状态
6. 创建新英雄
7. 初始化英雄职业

---

### newLevel()

```java
public static Level newLevel() {
    Dungeon.level = null;
    Actor.clear();
    
    Level level;
    if (branch == 0) {
        // 主线关卡
        switch (depth) {
            case 1: case 2: case 3: case 4:
                level = new SewerLevel();       // 下水道
                break;
            case 5:
                level = new SewerBossLevel();   // 下水道Boss
                break;
            case 6: case 7: case 8: case 9:
                level = new PrisonLevel();      // 监狱
                break;
            case 10:
                level = new PrisonBossLevel();  // 监狱Boss
                break;
            case 11: case 12: case 13: case 14:
                level = new CavesLevel();       // 洞穴
                break;
            case 15:
                level = new CavesBossLevel();   // 洞穴Boss
                break;
            case 16: case 17: case 18: case 19:
                level = new CityLevel();        // 城市
                break;
            case 20:
                level = new CityBossLevel();    // 城市Boss
                break;
            case 21: case 22: case 23: case 24:
                level = new HallsLevel();       // 大厅
                break;
            case 25:
                level = new HallsBossLevel();   // 大厅Boss
                break;
            case 26:
                level = new LastLevel();        // 最终关卡
                break;
            default:
                level = new DeadEndLevel();     // 死胡同
        }
    } else if (branch == 1) {
        // 支线关卡
        switch (depth) {
            case 11: case 12: case 13: case 14:
                level = new MiningLevel();      // 矿场
                break;
            case 16: case 17: case 18: case 19:
                level = new VaultLevel();       // 金库
                break;
            default:
                level = new DeadEndLevel();
        }
    } else {
        level = new DeadEndLevel();
    }

    // 记录已生成的关卡
    if (!(level instanceof DeadEndLevel || level instanceof VaultLevel)){
        if (!generatedLevels.contains(depth + 1000*branch)) {
            generatedLevels.add(depth + 1000 * branch);
        }

        // 更新最深楼层
        if (depth > Statistics.deepestFloor && branch == 0) {
            Statistics.deepestFloor = depth;
        }
    }

    level.create();  // 生成关卡
    
    return level;
}
```

**方法作用**：创建新关卡。

**返回值**：新创建的 Level 实例

**关卡对应关系**：
| 深度 | 关卡类型 |
|------|---------|
| 1-4 | 下水道 |
| 5 | 下水道Boss（Goo） |
| 6-9 | 监狱 |
| 10 | 监狱Boss（Tengu） |
| 11-14 | 洞穴 |
| 15 | 洞穴Boss（DM-300） |
| 16-19 | 城市 |
| 20 | 城市Boss（矮人国王） |
| 21-24 | 大厅 |
| 25 | 大厅Boss（Yog-Dzewa） |
| 26 | 最终关卡（护符） |

---

### switchLevel(Level level, int pos)

```java
public static void switchLevel( final Level level, int pos ) {
    // 处理特殊位置标记
    if (pos == -2){
        LevelTransition t = level.getTransition(LevelTransition.Type.REGULAR_EXIT);
        if (t != null) pos = t.cell();
    }

    // 验证并修正英雄位置
    if (pos < 0 || pos >= level.length() || level.invalidHeroPos(pos)){
        pos = level.getTransition(null).cell();
    }
    
    PathFinder.setMapSize(level.width(), level.height());
    
    Dungeon.level = level;
    hero.pos = pos;

    // 飞升挑战处理
    if (hero.buff(AscensionChallenge.class) != null){
        hero.buff(AscensionChallenge.class).onLevelSwitch();
    }

    // 恢复盟友
    Mob.restoreAllies( level, pos );

    // 初始化Actor系统
    Actor.init();

    // 添加怪物刷新器
    level.addRespawner();
    
    // 处理英雄位置的怪物
    for(Mob m : level.mobs){
        if (m.pos == hero.pos && !Char.hasProp(m, Char.Property.IMMOVABLE)){
            // 将怪物推开
            for(int i : PathFinder.NEIGHBOURS8){
                if (Actor.findChar(m.pos+i) == null && level.passable[m.pos + i]){
                    m.pos += i;
                    break;
                }
            }
        }
    }
    
    // 更新视野距离
    Light light = hero.buff( Light.class );
    hero.viewDistance = light == null ? level.viewDistance : Math.max( Light.DISTANCE, level.viewDistance );
    
    hero.curAction = hero.lastAction = null;

    observe();
    try {
        saveAll();
    } catch (IOException e) {
        ShatteredPixelDungeon.reportException(e);
    }
}
```

**方法作用**：切换到指定关卡。

**参数**：
- `level` (Level)：目标关卡
- `pos` (int)：英雄位置（-1=入口，-2=出口）

---

### observe() / observe(int dist)

```java
public static void observe(){
    // 计算视野距离
    int dist = Math.max(Dungeon.hero.viewDistance, 8);
    dist *= 1f + 0.25f*Dungeon.hero.pointsInTalent(Talent.FARSIGHT);

    if (Dungeon.hero.buff(MagicalSight.class) != null){
        dist = Math.max( dist, MagicalSight.DISTANCE );
    }

    observe( dist+1 );
}

public static void observe( int dist ) {
    if (level == null) {
        return;
    }
    
    level.updateFieldOfView(hero, level.heroFOV);

    int x = hero.pos % level.width();
    int y = hero.pos / level.width();

    // 计算视野边界
    int l = Math.max( 0, x - dist );
    int r = Math.min( x + dist, level.width() - 1 );
    int t = Math.max( 0, y - dist );
    int b = Math.min( y + dist, level.height() - 1 );
    
    int width = r - l + 1;
    int height = b - t + 1;
    
    int pos = l + t * level.width();
    
    // 更新已访问区域
    for (int i = t; i <= b; i++) {
        BArray.or( level.visited, level.heroFOV, pos, width, level.visited );
        pos+=level.width();
    }

    // 始终访问相邻格子
    for (int i : PathFinder.NEIGHBOURS9){
        level.visited[hero.pos+i] = true;
    }
    
    GameScene.updateFog(l, t, width, height);

    // 心眼效果：看到所有怪物
    if (hero.buff(MindVision.class) != null || hero.buff(DivineSense.DivineSenseTracker.class) != null){
        for (Mob m : level.mobs.toArray(new Mob[0])){
            // 更新怪物周围的已访问区域
            BArray.or( level.visited, level.heroFOV, m.pos - 1 - level.width(), 3, level.visited );
            BArray.or( level.visited, level.heroFOV, m.pos - 1, 3, level.visited );
            BArray.or( level.visited, level.heroFOV, m.pos - 1 + level.width(), 3, level.visited );
            GameScene.updateFog(m.pos, 2);
        }
    }

    // 洞察效果：看到所有物品堆
    if (hero.buff(Awareness.class) != null){
        for (Heap h : level.heaps.valueList()){
            // 更新物品周围的已访问区域
            GameScene.updateFog(h.pos, 2);
        }
    }

    // 预知护符效果
    for (TalismanOfForesight.CharAwareness c : hero.buffs(TalismanOfForesight.CharAwareness.class)){
        Char ch = (Char) Actor.findById(c.charID);
        if (ch == null || !ch.isAlive()) continue;
        GameScene.updateFog(ch.pos, 2);
    }

    // 召唤物视野
    for (Char ch : Actor.chars()){
        if (ch instanceof WandOfWarding.Ward
                || ch instanceof WandOfRegrowth.Lotus
                || ch instanceof SpiritHawk.HawkAlly
                || ch.buff(PowerOfMany.PowerBuff.class) != null){
            // 计算召唤物视野
            GameScene.updateFog(ch.pos, dist);
        }
    }

    GameScene.afterObserve();
}
```

**方法作用**：更新英雄的可见区域。

**执行流程**：
1. 计算视野距离（考虑天赋和Buff）
2. 更新英雄视野（heroFOV）
3. 将视野合并到已访问区域（visited）
4. 处理特殊视野效果（心眼、洞察等）
5. 更新游戏场景的迷雾

---

### findPath() / findStep() / flee()

```java
public static PathFinder.Path findPath(Char ch, int to, boolean[] pass, boolean[] vis, boolean chars) {
    return PathFinder.find( ch.pos, to, findPassable(ch, pass, vis, chars) );
}

public static int findStep(Char ch, int to, boolean[] pass, boolean[] visible, boolean chars ) {
    if (Dungeon.level.adjacent( ch.pos, to )) {
        return Actor.findChar( to ) == null && pass[to] ? to : -1;
    }
    return PathFinder.getStep( ch.pos, to, findPassable(ch, pass, visible, chars) );
}

public static int flee( Char ch, int from, boolean[] pass, boolean[] visible, boolean chars ) {
    boolean[] passable = findPassable(ch, pass, visible, false, true);
    passable[ch.pos] = true;

    // 恐惧状态限制逃跑逻辑
    boolean canApproachFromPos = ch.buff(Terror.class) == null && ch.buff(Dread.class) == null;
    int step = PathFinder.getStepBack( ch.pos, from, canApproachFromPos ? 8 : 4, passable, canApproachFromPos );

    // 如果逃跑路径上有角色，重新计算
    while (step != -1 && Actor.findChar(step) != null && chars){
        passable[step] = false;
        step = PathFinder.getStepBack( ch.pos, from, canApproachFromPos ? 8 : 4, passable, canApproachFromPos );
    }
    return step;
}
```

**方法作用**：寻路相关方法。

| 方法 | 用途 |
|------|------|
| `findPath` | 找到完整路径 |
| `findStep` | 找到下一步 |
| `flee` | 找到逃跑方向 |

---

### saveAll() / saveGame() / saveLevel()

```java
public static void saveAll() throws IOException {
    if (hero != null && (hero.isAlive() || WndResurrect.instance != null)) {
        Actor.fixTime();
        updateLevelExplored();
        saveGame( GamesInProgress.curSlot );
        saveLevel( GamesInProgress.curSlot );
        GamesInProgress.set( GamesInProgress.curSlot );
    }
}

public static void saveGame( int save ) {
    try {
        Bundle bundle = new Bundle();

        // 保存所有游戏状态
        bundle.put( VERSION, version = Game.versionCode );
        bundle.put( SEED, seed );
        bundle.put( HERO, hero );
        bundle.put( DEPTH, depth );
        bundle.put( BRANCH, branch );
        bundle.put( GOLD, gold );
        bundle.put( ENERGY, energy );
        // ... 保存更多状态
        
        FileUtils.bundleToFile( GamesInProgress.gameFile(save), bundle);
    } catch (IOException e) {
        GamesInProgress.setUnknown( save );
        ShatteredPixelDungeon.reportException(e);
    }
}

public static void saveLevel( int save ) throws IOException {
    Bundle bundle = new Bundle();
    bundle.put( LEVEL, level );
    FileUtils.bundleToFile(GamesInProgress.depthFile( save, depth, branch ), bundle);
}
```

**方法作用**：保存游戏状态。

**存储结构**：
- `game.dat`：全局游戏状态
- `depth_X_Y.dat`：关卡数据（X=深度，Y=分支）

---

### loadGame() / loadLevel()

```java
public static void loadGame( int save ) throws IOException {
    loadGame( save, true );
}

public static Level loadLevel( int save ) throws IOException {
    Dungeon.level = null;
    Actor.clear();

    Bundle bundle = FileUtils.bundleFromFile( GamesInProgress.depthFile( save, depth, branch ));

    Level level = (Level)bundle.get( LEVEL );

    if (level == null){
        throw new IOException();
    } else {
        return level;
    }
}
```

**方法作用**：加载游戏状态。

---

### fail() / win()

```java
public static void fail( Object cause ) {
    if (WndResurrect.instance == null) {
        updateLevelExplored();
        Statistics.gameWon = false;
        Rankings.INSTANCE.submit( false, cause );
    }
}

public static void win( Object cause ) {
    updateLevelExplored();
    Statistics.gameWon = true;

    hero.belongings.identify();  // 鉴定所有物品

    Rankings.INSTANCE.submit( true, cause );
}
```

**方法作用**：处理游戏失败/胜利。

---

### 辅助方法

#### shopOnLevel()

```java
public static boolean shopOnLevel() {
    return depth == 6 || depth == 11 || depth == 16;
}
```

**返回值**：当前关卡是否有商店

#### bossLevel()

```java
public static boolean bossLevel() {
    return bossLevel( depth );
}

public static boolean bossLevel( int depth ) {
    return depth == 5 || depth == 10 || depth == 15 || depth == 20 || depth == 25;
}
```

**返回值**：是否是Boss关卡

#### isChallenged()

```java
public static boolean isChallenged( int mask ) {
    return (challenges & mask) != 0;
}
```

**参数**：挑战模式位掩码

**返回值**：是否启用了该挑战

#### posNeeded() / souNeeded() / asNeeded()

```java
public static boolean posNeeded() {
    // 力量药水：每5层2瓶
    int posLeftThisSet = 2 - (LimitedDrops.STRENGTH_POTIONS.count - (depth / 5) * 2);
    if (posLeftThisSet <= 0) return false;
    // ... 概率计算
}

public static boolean souNeeded() {
    // 升级卷轴：每5层3张
    int souLeftThisSet = 3 - (LimitedDrops.UPGRADE_SCROLLS.count - (depth / 5) * 3);
    if (souLeftThisSet <= 0) return false;
    return Random.Int(5 - floorThisSet) < souLeftThisSet;
}

public static boolean asNeeded() {
    // 奥术铁笔：每5层1支
    int asLeftThisSet = 1 - (LimitedDrops.ARCANE_STYLI.count - (depth / 5));
    if (asLeftThisSet <= 0) return false;
    return Random.Int(5 - floorThisSet) < asLeftThisSet;
}
```

**方法作用**：判断当前关卡是否应该生成对应物品

---

## 11. 使用示例

### 开始新游戏

```java
Dungeon.initSeed();  // 初始化种子
Dungeon.init();      // 初始化游戏状态
Level level = Dungeon.newLevel();  // 创建第一关
Dungeon.switchLevel(level, -1);    // 切换到第一关（入口）
```

### 切换关卡

```java
Dungeon.depth++;  // 增加深度
Level newLevel = Dungeon.newLevel();  // 创建新关卡
Dungeon.switchLevel(newLevel, -1);    // 切换（入口）
```

### 检查游戏状态

```java
// 检查是否是Boss关
if (Dungeon.bossLevel()) {
    // Boss战逻辑
}

// 检查挑战模式
if (Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE)) {
    // 蜂群意识挑战
}

// 检查商店
if (Dungeon.shopOnLevel()) {
    // 生成商店
}
```

---

## 注意事项

### 静态状态管理

1. **所有字段都是静态的**：Dungeon 是全局状态持有者
2. **线程安全**：Actor 系统在独立线程运行，注意同步
3. **存档时机**：每次切换关卡都会自动保存

### 深度与分支

1. **depth**：1-26，对应不同关卡类型
2. **branch**：0=主线，1=支线（矿场、金库）
3. **generatedLevels**：使用 `depth + 1000*branch` 作为唯一标识

### 常见的坑

1. **忘记调用 observe()**：视野不会更新
2. **depth 超出范围**：会生成 DeadEndLevel
3. **存档失败**：需要处理 IOException

### 最佳实践

1. 使用 `switchLevel()` 而非直接修改 `level` 字段
2. 调用 `saveAll()` 确保状态持久化
3. 使用 `LimitedDrops` 枚举管理限制物品