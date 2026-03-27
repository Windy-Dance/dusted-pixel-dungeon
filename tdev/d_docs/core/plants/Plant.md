# Plant.java 植物基类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/plants/Plant.java` |
| **包名** | `com.shatteredpixel.shatteredpixeldungeon.plants` |
| **类修饰符** | `public abstract class` |
| **父类/接口** | `implements Bundlable` |
| **代码行数** | 250 行 |
| **许可证** | GPL-3.0 |

---

## 类职责

`Plant` 是游戏中所有植物实体的**抽象基类**，负责：

1. **植物生命周期管理** — 定义植物的触发、激活、枯萎流程
2. **位置状态持久化** — 通过 `Bundlable` 接口实现存档/读档
3. **种子机制封装** — 内部类 `Seed` 定义植物种子的物品行为
4. **天赋系统联动** — 处理 `Nature's Aid` 等自然系天赋效果
5. **子类职业协同** — 与 `Warden` (守林人) 子职业的特殊交互

---

## 4. 继承与协作关系

```
┌─────────────────────────────────────────────────────────────────────┐
│                          <<interface>>                              │
│                           Bundlable                                 │
│  ─────────────────────────────────────────────────────────────────── │
│  + restoreFromBundle(Bundle): void                                  │
│  + storeInBundle(Bundle): void                                      │
└─────────────────────────────────────────────────────────────────────┘
                                    △
                                    │ implements
                                    │
┌─────────────────────────────────────────────────────────────────────┐
│                      <<abstract>> Plant                             │
│  ─────────────────────────────────────────────────────────────────── │
│  + image: int                    // 植物图片索引                     │
│  + pos: int                      // 植物在地图上的位置               │
│  # seedClass: Class<? extends Seed> // 对应种子类                    │
│  ─────────────────────────────────────────────────────────────────── │
│  + trigger(): void                                                   │
│  + abstract activate(Char): void                                    │
│  + wither(): void                                                    │
│  + name(): String                                                     │
│  + desc(): String                                                     │
│  + restoreFromBundle(Bundle): void                                   │
│  + storeInBundle(Bundle): void                                       │
├─────────────────────────────────────────────────────────────────────┤
│                     <<static>> Seed extends Item                     │
│  ─────────────────────────────────────────────────────────────────── │
│  + AC_PLANT: String = "PLANT"                                        │
│  - TIME_TO_PLANT: float = 1f                                         │
│  # plantClass: Class<? extends Plant>                                │
│  ─────────────────────────────────────────────────────────────────── │
│  + actions(Hero): ArrayList<String>                                  │
│  # onThrow(int): void                                                │
│  + execute(Hero, String): void                                       │
│  + couch(int, Level): Plant                                         │
│  + isUpgradable(): boolean                                           │
│  + isIdentified(): boolean                                           │
│  + value(): int                                                       │
│  + energyVal(): int                                                   │
│  + desc(): String                                                     │
│  + info(): String                                                     │
├─────────────────────────────────────────────────────────────────────┤
│               <<static>> Seed.PlaceHolder extends Seed              │
│  ─────────────────────────────────────────────────────────────────── │
│  + isSimilar(Item): boolean                                          │
│  + info(): String                                                    │
└─────────────────────────────────────────────────────────────────────┘
                                    △
                                    │ extends
                                    │
┌─────────────────────┬─────────────────────┬─────────────────────┐
│    Firebloom        │     Icecap         │    Earthroot        │
│  ─────────────      │  ─────────────     │  ─────────────      │
│  火焰效果           │    冰冻效果        │    护盾效果         │
├─────────────────────┼─────────────────────┼─────────────────────┤
│    Fadeleaf         │    Sungrass        │    Swiftthistle     │
│  ─────────────      │  ─────────────     │  ─────────────      │
│  传送效果           │    治疗效果        │    加速效果         │
├─────────────────────┼─────────────────────┼─────────────────────┤
│    Sorrowmoss       │    Stormvine        │    Starflower       │
│  ─────────────      │  ─────────────     │  ─────────────      │
│  中毒效果           │    漂浮效果        │    星星能量         │
├─────────────────────┼─────────────────────┼─────────────────────┤
│    Blindweed        │    Mageroyal        │    Rotberry         │
│  ─────────────      │  ─────────────     │  ─────────────      │
│  致盲效果           │    魔法效果        │    腐烂效果         │
└─────────────────────┴─────────────────────┴─────────────────────┘
```

---

## 静态常量表

### Plant 类常量

| 常量名 | 类型 | 值 | 用途 |
|--------|------|-----|------|
| `POS` | `String` | `"pos"` | Bundle 序列化键名，用于保存/恢复植物位置 |

### Seed 内部类常量

| 常量名 | 类型 | 值 | 用途 |
|--------|------|-----|------|
| `AC_PLANT` | `String` | `"PLANT"` | 种植动作标识符 |
| `TIME_TO_PLANT` | `float` | `1f` | 种植所需时间（秒） |

---

## 实例字段表

### Plant 类字段

| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| `image` | `int` | `public` | 植物在精灵图集中的图片索引 |
| `pos` | `int` | `public` | 植物在地牢中的格子位置坐标 |
| `seedClass` | `Class<? extends Plant.Seed>` | `protected` | 该植物对应的种子类，用于莲花种子保存机制 |

### Seed 内部类字段

| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| `stackable` | `boolean` | `public` (继承自Item) | 设为 `true`，种子可堆叠 |
| `defaultAction` | `String` | `protected` (继承自Item) | 设为 `AC_THROW`，默认动作为投掷 |
| `plantClass` | `Class<? extends Plant>` | `protected` | 种子对应的植物类 |

---

## 7. 方法详解

### Plant 类方法

#### `trigger()` — 触发植物效果

```java
public void trigger(){
    Char ch = Actor.findChar(pos);                    // L62: 查找当前位置的角色

    if (ch instanceof Hero){
        ((Hero) ch).interrupt();                     // L65: 打断英雄正在进行的动作
    }

    if (Dungeon.level.heroFOV[pos] && Dungeon.hero.hasTalent(Talent.NATURES_AID)){
        // L68-70: 如果英雄有"自然援助"天赋且在视野内
        // 效果: 3/5 回合树皮护甲，基于天赋点数
        Barkskin.conditionallyAppend(Dungeon.hero, 2, 1 + 2*(Dungeon.hero.pointsInTalent(Talent.NATURES_AID)));
    }

    wither();                                        // L73: 植物枯萎
    activate( ch );                                  // L74: 激活具体效果（子类实现）
    Bestiary.setSeen(getClass());                    // L75: 记录图鉴发现
    Bestiary.countEncounter(getClass());             // L76: 增加遭遇计数
}
```

**执行流程**：
1. 查找当前位置的角色
2. 如果是英雄则打断其当前动作
3. 检查并应用"自然援助"天赋效果
4. 调用 `wither()` 使植物枯萎
5. 调用抽象方法 `activate()` 触发具体效果
6. 更新图鉴记录

---

#### `activate(Char ch)` — 激活植物效果（抽象方法）

```java
public abstract void activate( Char ch );
```

**职责**：由子类实现具体的植物效果逻辑

**参数**：
- `ch` — 触发植物的角色（可能是英雄、怪物或 null）

**子类实现示例** (Firebloom.java):
```java
@Override
public void activate( Char ch ) {
    if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN){
        Buff.affect(ch, FireImbue.class).set(FireImbue.DURATION * 0.3f);
    }
    if (ch instanceof Mob){
        Buff.prolong(ch, Trap.HazardAssistTracker.class, Trap.HazardAssistTracker.DURATION);
    }
    GameScene.add(Blob.seed(pos, 2, Fire.class));
    if (Dungeon.level.heroFOV[pos]) {
        CellEmitter.get(pos).burst(FlameParticle.FACTORY, 5);
    }
}
```

---

#### `wither()` — 植物枯萎处理

```java
public void wither() {
    Dungeon.level.uproot(pos);                       // L82: 从地图移除植物

    if (Dungeon.level.heroFOV[pos]) {
        CellEmitter.get(pos).burst(LeafParticle.GENERAL, 6); // L85: 播放落叶特效
    }

    float seedChance = 0f;
    for (Char c : Actor.chars()){
        if (c instanceof WandOfRegrowth.Lotus){
            WandOfRegrowth.Lotus l = (WandOfRegrowth.Lotus) c;
            if (l.inRange(pos)){
                seedChance = Math.max(seedChance, l.seedPreservation());
            }
        }
    }
    // L89-96: 检查附近是否有再生法杖召唤的莲花，获取种子保存概率

    if (Random.Float() < seedChance){
        if (seedClass != null && seedClass != Rotberry.Seed.class) {
            Dungeon.level.drop(Reflection.newInstance(seedClass), pos).sprite.drop();
        }
    }
    // L98-102: 根据概率掉落种子（腐烂莓种子除外）
}
```

**执行流程**：
1. 调用 `Level.uproot()` 从地图移除植物
2. 如果在视野内则播放落叶粒子特效
3. 检查附近莲花，计算种子保存概率
4. 按概率掉落对应种子（排除 Rotberry）

---

#### `restoreFromBundle(Bundle)` — 从存档恢复状态

```java
private static final String POS = "pos";

@Override
public void restoreFromBundle(Bundle bundle) {
    pos = bundle.getInt(POS);                       // L110: 从Bundle读取位置
}
```

**职责**：实现 `Bundlable` 接口，从存档数据恢复植物位置

---

#### `storeInBundle(Bundle)` — 保存状态到存档

```java
@Override
public void storeInBundle(Bundle bundle) {
    bundle.put(POS, pos);                            // L115: 将位置存入Bundle
}
```

**职责**：实现 `Bundlable` 接口，将植物位置保存到存档数据

---

#### `name()` — 获取植物名称

```java
public String name(){
    return Messages.get(this, "name");              // L119: 从资源文件获取本地化名称
}
```

**职责**：通过消息系统获取植物的本地化显示名称

---

#### `desc()` — 获取植物描述

```java
public String desc() {
    String desc = Messages.get(this, "desc");        // L123: 获取基础描述
    if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.WARDEN){
        desc += "\n\n" + Messages.get(this, "warden_desc"); // L125: 守林人子职业额外描述
    }
    return desc;
}
```

**职责**：获取植物的详细描述文本，守林人子职业会看到额外信息

---

### Seed 内部类方法

#### `actions(Hero)` — 获取可用动作列表

```java
@Override
public ArrayList<String> actions(Hero hero) {
    ArrayList<String> actions = super.actions(hero); // L145: 获取基础动作（丢弃、投掷）
    actions.add(AC_PLANT);                            // L146: 添加种植动作
    return actions;
}
```

**返回**：包含 `AC_DROP`、`AC_THROW`、`AC_PLANT` 的动作列表

---

#### `onThrow(int cell)` — 投掷处理

```java
@Override
protected void onThrow(int cell) {
    // L152-156: 检查是否可以种植
    if (Dungeon.level.map[cell] == Terrain.ALCHEMY     // 炼金釜
            || Dungeon.level.pit[cell]                 // 深坑
            || Dungeon.level.traps.get(cell) != null   // 陷阱
            || Dungeon.isChallenged(Challenges.NO_HERBALISM)) { // 禁药挑战
        super.onThrow(cell);                            // 无法种植，走普通投掷逻辑
    } else {
        Catalog.countUse(getClass());                   // L158: 记录使用统计
        Dungeon.level.plant(this, cell);                // L159: 在地面种植植物
        
        // L160-170: 守林人子职业特殊效果 - 周围生成长草
        if (Dungeon.hero.subClass == HeroSubClass.WARDEN) {
            for (int i : PathFinder.NEIGHBOURS8) {
                int c = Dungeon.level.map[cell + i];
                if (c == Terrain.EMPTY || c == Terrain.EMPTY_DECO
                        || c == Terrain.EMBERS || c == Terrain.GRASS) {
                    Level.set(cell + i, Terrain.FURROWED_GRASS);
                    GameScene.updateMap(cell + i);
                    CellEmitter.get(cell + i).burst(LeafParticle.LEVEL_SPECIFIC, 4);
                }
            }
        }
    }
}
```

**投掷判定**：
| 地形/条件 | 行为 |
|-----------|------|
| 炼金釜 | 普通投掷 |
| 深坑 | 普通投掷 |
| 陷阱 | 普通投掷 |
| 禁药挑战开启 | 普通投掷 |
| 其他地面 | 种植植物 |

---

#### `execute(Hero, String)` — 执行动作

```java
@Override
public void execute(Hero hero, String action) {
    super.execute(hero, action);                       // L177: 先执行父类逻辑

    if (action.equals(AC_PLANT)) {
        hero.busy();                                   // L181: 标记英雄忙碌
        ((Seed)detach(hero.belongings.backpack)).onThrow(hero.pos); // L182: 从背包移除并原地种植
        hero.spend(TIME_TO_PLANT);                      // L183: 消耗1秒时间
        hero.sprite.operate(hero.pos);                 // L185: 播放操作动画
    }
}
```

**种植流程**：
1. 英雄进入忙碌状态
2. 种子从背包移除
3. 在英雄当前位置种植
4. 消耗 1 秒时间
5. 播放操作动画

---

#### `couch(int pos, Level level)` — 创建植物实例

```java
public Plant couch(int pos, Level level) {
    if (level != null && level.heroFOV != null && level.heroFOV[pos]) {
        Sample.INSTANCE.play(Assets.Sounds.PLANT);     // L192: 播放种植音效
    }
    Plant plant = Reflection.newInstance(plantClass); // L194: 反射创建植物实例
    plant.pos = pos;                                   // L195: 设置位置
    return plant;
}
```

**职责**：工厂方法，通过反射创建植物实例并设置位置

**命名说明**：`couch` 原意"卧床/埋设"，此处指将种子埋入地面

---

#### `isUpgradable()` / `isIdentified()` — 物品属性

```java
@Override
public boolean isUpgradable() {
    return false;                                      // L201: 种子不可升级
}

@Override
public boolean isIdentified() {
    return true;                                       // L206: 种子默认已鉴定
}
```

---

#### `value()` / `energyVal()` — 经济价值

```java
@Override
public int value() {
    return 10 * quantity;                              // L211: 商店售价：10金币/个
}

@Override
public int energyVal() {
    return 2 * quantity;                               // L216: 炼金能量：2点/个
}
```

---

#### `desc()` — 种子描述

```java
@Override
public String desc() {
    String desc = Messages.get(plantClass, "desc");   // L221: 获取对应植物的描述
    if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.WARDEN){
        desc += "\n\n" + Messages.get(plantClass, "warden_desc"); // L223: 守林人额外信息
    }
    return desc;
}
```

---

#### `info()` — 种子信息

```java
@Override
public String info() {
    return Messages.get(Seed.class, "info", super.info()); // L230: 组合基础信息模板
}
```

---

### PlaceHolder 内部类方法

```java
public static class PlaceHolder extends Seed {
    {
        image = ItemSpriteSheet.SEED_HOLDER;           // L236: 占位图标
    }
    
    @Override
    public boolean isSimilar(Item item) {
        return item instanceof Plant.Seed;             // L241: 匹配所有种子
    }
    
    @Override
    public String info() {
        return "";                                      // L246: 空信息
    }
}
```

**职责**：用于物品栏界面的种子占位符，表示"任意种子"的通用槽位

---

## 11. 使用示例

### 创建自定义植物

```java
public class MyPlant extends Plant {
    
    // 实例初始化块 - 设置图片和种子类
    {
        image = 10;  // 精灵图集中的图片索引
        seedClass = Seed.class;
    }
    
    // 实现抽象方法 - 定义植物激活效果
    @Override
    public void activate(Char ch) {
        // 效果1：守林人子职业特殊处理
        if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN) {
            Buff.affect(ch, SomeBuff.class);
        }
        
        // 效果2：对怪物造成伤害
        if (ch instanceof Mob) {
            ch.damage(10, this);
        }
        
        // 效果3：视觉特效
        if (Dungeon.level.heroFOV[pos]) {
            CellEmitter.get(pos).burst(ShaftParticle.FACTORY, 5);
        }
    }
    
    // 内嵌种子类
    public static class Seed extends Plant.Seed {
        {
            image = ItemSpriteSheet.SEED_MYPLANT;
            plantClass = MyPlant.class;
        }
    }
}
```

### 在代码中使用植物

```java
// 种植植物
Dungeon.level.plant(seed, targetPos);

// 触发植物（踩上去时）
Plant plant = Dungeon.level.plants.get(pos);
if (plant != null) {
    plant.trigger();
}

// 检查位置是否有植物
if (Dungeon.level.plants.get(pos) != null) {
    // 有植物
}

// 通过种子创建植物实例
Seed seed = new Firebloom.Seed();
Plant plant = seed.couch(pos, Dungeon.level);
```

### 存档序列化

```java
// 保存
Bundle bundle = new Bundle();
plant.storeInBundle(bundle);

// 恢复
Plant plant = Reflection.newInstance(PlantClass);
plant.restoreFromBundle(bundle);
```

---

## 注意事项

### 1. 双向引用维护
每个 `Plant` 子类必须在其初始化块中设置 `seedClass`，对应的 `Seed` 子类也必须设置 `plantClass`：

```java
// Plant 子类
{
    seedClass = MySeed.class;  // ✓ 必须设置
}

// Seed 子类
{
    plantClass = MyPlant.class;  // ✓ 必须设置
}
```

### 2. 守林人（Warden）特殊处理
游戏为守林人子职业提供了多处特殊效果：
- `Plant.desc()` 添加额外描述文本
- `Plant.wither()` 通过莲花机制保存种子（守林人专属）
- `Seed.onThrow()` 在种植位置周围生成长草
- 各植物 `activate()` 方法通常对守林人有特殊增益

### 3. 植物与种子的生命周期
```
种子投掷/种植 → Seed.onThrow() → Level.plant() → Seed.couch() 创建 Plant
                                                              ↓
角色踩上植物 ← Dungeon.level.plants.get(pos) ← Plant 存入地图
       ↓
Plant.trigger() → wither() + activate(ch)
       ↓
可能掉落新种子（莲花机制）
```

### 4. 枯萎后的种子掉落
`wither()` 方法中的种子保存机制需要：
- 附近存在再生法杖召唤的莲花
- 莲花的 `seedPreservation()` 返回 > 0 的概率
- 种子类不能是 `Rotberry.Seed`（腐烂莓种子永不掉落）

### 5. 线程安全
`Plant` 的 `pos` 和 `image` 字段是 public 的，但游戏是单线程的，不需要同步。

### 6. 禁药挑战
当开启 `Challenges.NO_HERBALISM` 挑战时，种子无法种植，会变成普通投掷物。

---

## 最佳实践

### 1. 实现新植物的标准模板

```java
public class NewPlant extends Plant {
    
    {
        image = XY;  // 设置图片索引
        seedClass = Seed.class;
    }
    
    @Override
    public void activate(Char ch) {
        // 总是检查视野再播放特效
        if (Dungeon.level.heroFOV[pos]) {
            CellEmitter.get(pos).burst(Particle.FACTORY, count);
        }
        
        // 对不同角色类型区分效果
        if (ch instanceof Hero) {
            // 英雄效果
        } else if (ch instanceof Mob) {
            // 怪物效果
        }
        
        // 守林人特殊处理
        if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN) {
            // 增强效果
        }
    }
    
    public static class Seed extends Plant.Seed {
        {
            image = ItemSpriteSheet.SEED_NEWPLANT;
            plantClass = NewPlant.class;
        }
    }
}
```

### 2. 使用消息系统
所有显示文本必须通过 `Messages.get()` 获取，支持本地化：
```java
// 正确
return Messages.get(this, "name");  // 自动解析 NewPlant.name

// 错误
return "新植物";  // 硬编码文本
```

### 3. 视觉特效最佳实践
```java
if (Dungeon.level.heroFOV[pos]) {
    // 只在英雄视野内播放特效
    CellEmitter.get(pos).burst(Particle.FACTORY, count);
    Sample.INSTANCE.play(Assets.Sounds.EFFECT);
}
```

### 4. 种子价值平衡
- 基础售价：10 金币/个
- 炼金能量：2 点/个
- 稀有植物可适当提高，但需保持平衡

### 5. 与其他系统联动
创建新植物时应考虑：
- **天赋系统**：`Nature's Aid`（自然援助）天赋会自动给予树皮护甲
- **图鉴系统**：植物触发时会记录到图鉴
- **挑战模式**：禁药挑战下种子无法种植

---

## 相关文件

| 文件 | 关系 |
|------|------|
| `Item.java` | Seed 的父类 |
| `Level.java` | 植物/种子的地图管理 |
| `Terrain.java` | 地形类型判断 |
| `Bestiary.java` | 图鉴记录系统 |
| `Catalog.java` | 使用统计系统 |
| `WandOfRegrowth.java` | 莲花召唤与种子保存 |
| `Barkskin.java` | 树皮护甲 Buff |
| `Talent.java` | 天赋系统定义 |

---

*文档生成时间：2026-03-26*
*基于 Shattered Pixel Dungeon 源码*