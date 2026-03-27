# RegularLevel 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **类名** | RegularLevel |
| **包路径** | `com.shatteredpixel.shatteredpixeldungeon.levels` |
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/RegularLevel.java` |
| **修饰符** | `public abstract` |
| **父类** | `Level` |
| **代码行数** | 909 行 |
| **许可证** | GNU General Public License v3 |

---

## 类职责

`RegularLevel` 是所有**常规关卡**的抽象基类，负责：

1. **关卡生成流程控制**：协调房间初始化、布局构建和地形绘制的完整流程
2. **房间管理**：管理关卡中所有房间（入口、出口、标准房、特殊房、秘密房）的创建和配置
3. **怪物生成**：在标准房间中合理分布怪物，确保玩家安全区
4. **物品生成**：生成随机物品、宝箱、宝箱怪、遗骨、指南页等
5. **探索进度计算**：根据未发现内容计算关卡的探索完成度

### 设计模式
- **模板方法模式**：定义 `build()` 骨架，子类实现 `painter()`、`standardRooms()`、`specialRooms()` 等具体行为
- **策略模式**：通过 `Builder` 和 `Painter` 接口实现关卡布局和地形绘制的解耦

---

## 4. 继承与协作关系

```
                    ┌─────────────────┐
                    │     Level       │ (抽象基类)
                    │─────────────────│
                    │ + map[]         │
                    │ + mobs          │
                    │ + heaps         │
                    │ + feeling       │
                    └────────┬────────┘
                             │ extends
                             ▼
                    ┌─────────────────┐
                    │  RegularLevel   │ (抽象类)
                    │─────────────────│
                    │ - rooms         │
                    │ - builder       │
                    │ - roomEntrance  │
                    │ - roomExit      │
                    └────────┬────────┘
                             │ extends
            ┌────────────────┼────────────────┐
            │                │                │
            ▼                ▼                ▼
    ┌───────────┐    ┌───────────┐    ┌───────────┐
    │SewerLevel │    │PrisonLevel│    │CavesLevel │
    │ (下水道)   │    │ (监狱)    │    │ (洞穴)    │
    └───────────┘    └───────────┘    └───────────┘
            │                │                │
            ▼                ▼                ▼
    ┌───────────┐    ┌───────────┐    ┌───────────┐
    │ CityLevel │    │ HallsLevel│    │  ...      │
    │ (城市)    │    │ (大厅)    │    │           │
    └───────────┘    └───────────┘    └───────────┘

关联关系:
┌─────────────────┐     uses      ┌─────────────────┐
│  RegularLevel   │──────────────▶│    Builder      │
└─────────────────┘               └─────────────────┘
                                          ▲
                                          │ implements
                        ┌─────────────────┼─────────────────┐
                        │                 │                 │
                        ▼                 ▼                 ▼
                 ┌───────────┐     ┌───────────┐     ┌───────────┐
                 │LoopBuilder│     │FigureEight│     │   ...     │
                 └───────────┘     └───────────┘     └───────────┘

┌─────────────────┐     uses      ┌─────────────────┐
│  RegularLevel   │──────────────▶│    Painter      │
└─────────────────┘               └─────────────────┘
                                          ▲
                                          │ extends
                        ┌─────────────────┼─────────────────┐
                        │                 │                 │
                        ▼                 ▼                 ▼
                 ┌───────────┐     ┌───────────┐     ┌───────────┐
                 │SewerPainter│    │PrisonPainter│   │   ...     │
                 └───────────┘     └───────────┘     └───────────┘
```

---

## 静态常量表

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `limitedDocs` | `HashMap<Document, Dungeon.LimitedDrops>` | 静态初始化 | 文档与限制掉落的映射关系，用于区域传说页面的生成控制 |

### 静态初始化块 (694-701行)

```java
static {
    limitedDocs.put(Document.SEWERS_GUARD, Dungeon.LimitedDrops.LORE_SEWERS);
    limitedDocs.put(Document.PRISON_WARDEN, Dungeon.LimitedDrops.LORE_PRISON);
    limitedDocs.put(Document.CAVES_EXPLORER, Dungeon.LimitedDrops.LORE_CAVES);
    limitedDocs.put(Document.CITY_WARLOCK, Dungeon.LimitedDrops.LORE_CITY);
    limitedDocs.put(Document.HALLS_KING, Dungeon.LimitedDrops.LORE_HALLS);
}
```

**用途**：建立各区域文档（如"下水道守卫日志"）与其对应的限制掉落类型的映射，确保每个区域只掉落一次传说页面。

---

## 实例字段表

| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| `rooms` | `ArrayList<Room>` | `protected` | 关卡中所有房间的集合，由 Builder 生成后填充 |
| `builder` | `Builder` | `protected` | 房间布局构建器，负责将房间连接成可行走的地图结构 |
| `roomEntrance` | `Room` | `protected` | 入口房间引用，玩家进入关卡时的起始位置 |
| `roomExit` | `Room` | `protected` | 出口房间引用，通往下一层的楼梯所在房间 |

---

## 7. 方法详解

### 1. build() - 关卡构建主流程

**位置**：第 104-122 行

```java
@Override
protected boolean build() {
    builder = builder();
    
    ArrayList<Room> initRooms = initRooms();
    Random.shuffle(initRooms);
    
    do {
        for (Room r : initRooms){
            r.neigbours.clear();
            r.connected.clear();
        }
        rooms = builder.build((ArrayList<Room>)initRooms.clone());
    } while (rooms == null);
    
    return painter().paint(this, rooms);
}
```

**逐行解析**：

| 行号 | 代码 | 说明 |
|------|------|------|
| 107 | `builder = builder();` | 调用抽象方法获取具体的布局构建器 |
| 109 | `ArrayList<Room> initRooms = initRooms();` | 初始化房间列表（入口、出口、标准房、特殊房、秘密房） |
| 110 | `Random.shuffle(initRooms);` | 随机打乱房间顺序，增加生成多样性 |
| 112-118 | `do { ... } while (rooms == null);` | 循环尝试构建，直到 Builder 成功返回房间布局 |
| 113-116 | `for (Room r : initRooms){...}` | 每次重试前清除房间的邻居和连接关系 |
| 117 | `rooms = builder.build(...)` | 调用 Builder 构建房间连接，传入克隆列表避免修改原列表 |
| 120 | `return painter().paint(this, rooms);` | 调用 Painter 绘制具体地形，返回是否成功 |

**关键点**：
- 使用 `do-while` 循环确保一定能生成有效的关卡布局
- Builder 在空间不足或连接失败时返回 `null`，触发重新生成
- 房间克隆确保多次重试不会污染原始房间数据

---

### 2. initRooms() - 房间初始化

**位置**：第 124-166 行

```java
protected ArrayList<Room> initRooms() {
    ArrayList<Room> initRooms = new ArrayList<>();
    initRooms.add(roomEntrance = EntranceRoom.createEntrance());
    initRooms.add(roomExit = ExitRoom.createExit());

    // 标准房间
    int standards = standardRooms(feeling == Feeling.LARGE);
    if (feeling == Feeling.LARGE){
        standards = (int)Math.ceil(standards * 1.5f);
    }
    for (int i = 0; i < standards; i++) {
        StandardRoom s;
        do {
            s = StandardRoom.createRoom();
        } while (!s.setSizeCat(standards-i));
        i += s.sizeFactor()-1;
        initRooms.add(s);
    }
    
    // 商店房间
    if (Dungeon.shopOnLevel())
        initRooms.add(new ShopRoom());

    // 特殊房间
    int specials = specialRooms(feeling == Feeling.LARGE);
    if (feeling == Feeling.LARGE){
        specials++;
    }
    SpecialRoom.initForFloor();
    for (int i = 0; i < specials; i++) {
        SpecialRoom s = SpecialRoom.createRoom();
        if (s instanceof PitRoom) specials++;  // 深坑房间不计数
        initRooms.add(s);
    }
    
    // 秘密房间
    int secrets = SecretRoom.secretsForFloor(Dungeon.depth);
    if (feeling == Feeling.SECRETS) secrets++;
    for (int i = 0; i < secrets; i++) {
        initRooms.add(SecretRoom.createRoom());
    }
    
    return initRooms;
}
```

**逐段解析**：

#### 入口和出口房间 (126-127行)
```java
initRooms.add(roomEntrance = EntranceRoom.createEntrance());
initRooms.add(roomExit = ExitRoom.createExit());
```
- 创建并添加入口和出口房间
- 同时保存引用到实例字段，供后续使用

#### 标准房间 (129-141行)
| 代码 | 说明 |
|------|------|
| `standardRooms(feeling == Feeling.LARGE)` | 子类实现，返回标准房间数量 |
| `standards = (int)Math.ceil(standards * 1.5f)` | 大型关卡增加50%房间数 |
| `s.setSizeCat(standards-i)` | 根据剩余空间设置房间大小类别 |
| `i += s.sizeFactor()-1` | 大房间占用更多"位置"，调整计数器 |

#### 商店房间 (143-144行)
```java
if (Dungeon.shopOnLevel())
    initRooms.add(new ShopRoom());
```
- 根据游戏规则判断当前层是否生成商店

#### 特殊房间 (146-156行)
```java
SpecialRoom.initForFloor();  // 初始化本层特殊房间池
for (int i = 0; i < specials; i++) {
    SpecialRoom s = SpecialRoom.createRoom();
    if (s instanceof PitRoom) specials++;  // 深坑房间额外增加计数
    initRooms.add(s);
}
```
- 深坑房间（PitRoom）不占用特殊房间配额

#### 秘密房间 (158-163行)
```java
int secrets = SecretRoom.secretsForFloor(Dungeon.depth);
if (feeling == Feeling.SECRETS) secrets++;
```
- 根据深度计算秘密房间数量
- 秘密感觉的关卡额外增加一个秘密房间

---

### 3. standardRooms() / specialRooms() - 房间数量配置

**位置**：第 168-174 行

```java
protected int standardRooms(boolean forceMax){
    return 0;
}

protected int specialRooms(boolean forceMax){
    return 0;
}
```

**说明**：
- 模板方法，由子类重写以定义各关卡的房间数量
- `forceMax` 参数在 `feeling == Feeling.LARGE` 时为 `true`，子类应返回最大房间数

**子类示例**（SewerLevel.java）：
```java
@Override
protected int standardRooms(boolean forceMax) {
    if (forceMax) return 6;
    return 4+Random.chances(new float[]{1, 3, 1});  // 4-6个，平均5个
}

@Override
protected int specialRooms(boolean forceMax) {
    if (forceMax) return 2;
    return 1+Random.chances(new float[]{1, 4});  // 1-2个，平均1.8个
}
```

---

### 4. builder() - 布局构建器工厂

**位置**：第 176-189 行

```java
protected Builder builder(){
    if (Random.Int(2) == 0){
        return new LoopBuilder()
                .setLoopShape(2, Random.Float(0f, 0.65f), Random.Float(0f, 0.50f));
    } else {
        return new FigureEightBuilder()
                .setLoopShape(2, Random.Float(0.3f, 0.8f), 0f);
    }
}
```

**解析**：
| Builder类型 | 概率 | 形状参数 | 特点 |
|-------------|------|----------|------|
| `LoopBuilder` | 50% | 2个环，随机曲率 | 生成环形路径，适合绕圈探索 |
| `FigureEightBuilder` | 50% | 8字形，中等曲率 | 生成8字形路径，更有分支感 |

**参数说明**：
- `setLoopShape(loops, curve1, curve2)`：
  - `loops`：环的数量
  - `curve1`：主曲率
  - `curve2`：次曲率

---

### 5. painter() - 地形绘制器工厂

**位置**：第 191 行

```java
protected abstract Painter painter();
```

**说明**：
- 抽象方法，子类必须实现
- 返回对应区域的 Painter 实例

**子类示例**（SewerLevel.java）：
```java
@Override
protected Painter painter() {
    return new SewerPainter()
            .setWater(feeling == Feeling.WATER ? 0.85f : 0.30f, 5)
            .setGrass(feeling == Feeling.GRASS ? 0.80f : 0.20f, 4)
            .setTraps(nTraps(), trapClasses(), trapChances());
}
```

---

### 6. nTraps() / trapClasses() / trapChances() - 陷阱配置

**位置**：第 193-203 行

```java
protected int nTraps() {
    return Random.NormalIntRange(2, 3 + (Dungeon.depth/5));
}

protected Class<?>[] trapClasses(){
    return new Class<?>[]{WornDartTrap.class};
}

protected float[] trapChances() {
    return new float[]{1};
}
```

**说明**：
| 方法 | 默认实现 | 子类可覆盖 |
|------|----------|------------|
| `nTraps()` | 随机2到`3+depth/5`个陷阱 | ✅ |
| `trapClasses()` | 仅磨损飞镖陷阱 | ✅ |
| `trapChances()` | 100%飞镖陷阱 | ✅ |

**陷阱数量公式**：
- 深度 1-5：2-4 个陷阱
- 深度 6-10：2-5 个陷阱
- 深度 11-15：2-6 个陷阱
- 以此类推...

---

### 7. mobLimit() - 怪物数量上限

**位置**：第 205-217 行

```java
@Override
public int mobLimit() {
    if (Dungeon.depth <= 1){
        if (!Statistics.amuletObtained) return 0;  // 第1层默认无怪物
        else                            return 10; // 获得护符后第1层有怪物
    }

    int mobs = 3 + Dungeon.depth % 5 + Random.Int(3);
    if (feeling == Feeling.LARGE){
        mobs = (int)Math.ceil(mobs * 1.33f);  // 大型关卡增加33%怪物
    }
    return mobs;
}
```

**怪物数量计算公式**：
```
基础数量 = 3 + (深度 % 5) + 随机(0-2)
大型关卡 = ceil(基础数量 * 1.33)
```

**示例**：
| 深度 | 基础范围 | 大型关卡范围 |
|------|----------|--------------|
| 1 | 0 或 10 | - |
| 2 | 5-7 | 7-10 |
| 5 | 6-8 | 8-11 |
| 10 | 5-7 | 7-10 |

---

### 8. createMobs() - 怪物生成

**位置**：第 219-316 行

这是一个复杂的方法，负责在标准房间中合理分布怪物。

#### 核心逻辑流程

```java
@Override
protected void createMobs() {
    int mobsToSpawn = Dungeon.depth == 1 ? 8 : mobLimit();
    
    // 1. 收集标准房间及其生成权重
    ArrayList<Room> stdRooms = new ArrayList<>();
    for (Room room : rooms) {
        if (room instanceof StandardRoom) {
            for (int i = 0; i < ((StandardRoom) room).mobSpawnWeight(); i++) {
                stdRooms.add(room);  // 权重越大，添加次数越多
            }
        }
    }
    Random.shuffle(stdRooms);
    
    // 2. 计算入口安全区
    boolean[] entranceFOV = new boolean[length()];
    Point c = cellToPoint(entrance());
    ShadowCaster.castShadow(c.x, c.y, width(), entranceFOV, losBlocking, 8);
    
    // 3. 计算入口可达区域
    boolean[] entranceWalkable = BArray.not(solid, null);
    // ... 入口房间内的门处理 ...
    PathFinder.buildDistanceMap(entrance(), entranceWalkable, 8);
    
    // 4. 生成怪物
    while (mobsToSpawn > 0) {
        // ... 怪物位置验证 ...
    }
}
```

#### 入口安全区计算 (235-252行)

| 步骤 | 代码 | 说明 |
|------|------|------|
| FOV计算 | `ShadowCaster.castShadow(...)` | 计算入口8格视野范围 |
| 可行走图 | `BArray.not(solid, null)` | 创建非固体区域副本 |
| 房间内门处理 | 双重循环遍历入口房间 | 入口房间内的门视为可行走 |
| 距离图 | `PathFinder.buildDistanceMap(...)` | 计算入口8格步行距离 |

#### 怪物位置验证 (265-275行)

```java
do {
    mob.pos = pointToCell(roomToSpawn.random());
    tries--;
} while (tries >= 0 && (
    findMob(mob.pos) != null              // 已有怪物
    || entranceFOV[mob.pos]               // 在入口视野内
    || PathFinder.distance[mob.pos] != Integer.MAX_VALUE  // 在入口8格步行距离内
    || !passable[mob.pos]                 // 不可通行
    || solid[mob.pos]                     // 固体地形
    || !roomToSpawn.canPlaceCharacter(...)  // 房间不允许放置
    || mob.pos == exit()                  // 在出口位置
    || traps.get(mob.pos) != null         // 有陷阱
    || plants.get(mob.pos) != null        // 有植物
    || (!openSpace[mob.pos] && mob.properties().contains(Char.Property.LARGE))  // 大型怪物需要开阔空间
));
```

#### 额外怪物生成 (282-304行)

```java
// 25%几率在同一房间生成第二个怪物
if (Dungeon.depth > 1 && mobsToSpawn > 0 && Random.Int(4) == 0){
    mob = createMob();
    // ... 相同的位置验证 ...
}
```

#### 高草处理 (308-314行)

```java
for (Mob m : mobs){
    if (map[m.pos] == Terrain.HIGH_GRASS || map[m.pos] == Terrain.FURROWED_GRASS) {
        map[m.pos] = Terrain.GRASS;  // 高草变成普通草
        losBlocking[m.pos] = false;   // 不再阻挡视线
    }
}
```

---

### 9. randomRespawnCell() - 重生点选择

**位置**：第 318-346 行

```java
@Override
public int randomRespawnCell(Char ch) {
    int count = 0;
    int cell = -1;

    while (true) {
        if (++count > 30) return -1;  // 最多尝试30次

        Room room = randomRoom(StandardRoom.class);
        if (room == null || room == roomEntrance) continue;

        cell = pointToCell(room.random(1));  // 房间内部随机点（边缘1格）
        if (!heroFOV[cell]                          // 不在玩家视野内
            && Actor.findChar(cell) == null         // 无角色占据
            && passable[cell] && !solid[cell]       // 可通行
            && (!Char.hasProp(ch, Char.Property.LARGE) || openSpace[cell])  // 大型角色需要开阔空间
            && room.canPlaceCharacter(cellToPoint(cell), this)
            && cell != exit()) {                    // 不在出口
            return cell;
        }
    }
}
```

**用途**：用于生成新怪物、传送目标等需要安全位置的场景。

---

### 10. randomDestination() - 随机目的地

**位置**：第 348-374 行

```java
@Override
public int randomDestination(Char ch) {
    int count = 0;
    int cell = -1;
    
    while (true) {
        if (++count > 30) return -1;
        
        Room room = Random.element(rooms);  // 随机任意房间
        if (room == null) continue;

        ArrayList<Point> points = room.charPlaceablePoints(this);
        if (!points.isEmpty()){
            cell = pointToCell(Random.element(points));
            if (passable[cell] && (!Char.hasProp(ch, Char.Property.LARGE) || openSpace[cell])) {
                return cell;
            }
        }
    }
}
```

**用途**：为AI提供随机巡逻目的地，范围比 `randomRespawnCell` 更广（包括所有房间）。

---

### 11. createItems() - 物品生成

**位置**：第 377-692 行

这是一个非常长且复杂的方法，负责生成关卡中的所有物品。按功能分段解析：

#### 11.1 随机物品生成 (377-448行)

```java
// 物品数量：3/4/5个，概率60%/30%/10%
int nItems = 3 + Random.chances(new float[]{6, 3, 1});

if (feeling == Feeling.LARGE) nItems += 2;  // 大型关卡多2个

for (int i = 0; i < nItems; i++) {
    Item toDrop = Generator.random();
    int cell = randomDropCell();
    
    // 确定容器类型
    Heap.Type type = null;
    switch (Random.Int(20)) {
        case 0: type = Heap.Type.SKELETON; break;      // 5% 骨架
        case 1-4: // 20% 箱子（可能有宝箱怪）
            if (Random.Float() < (MimicTooth.mimicChanceMultiplier() - 1f)/4f) {
                mobs.add(Mimic.spawnAt(cell, toDrop));  // 宝箱怪
                continue;
            }
            type = Heap.Type.CHEST;
            break;
        case 5: // 5% 纯宝箱怪
            if (Dungeon.depth > 1) {
                mobs.add(Mimic.spawnAt(cell, toDrop));
                continue;
            }
            type = Heap.Type.CHEST;
            break;
        default: type = Heap.Type.HEAP; break;         // 70% 普通堆
    }
    
    // 特殊物品处理
    if ((toDrop instanceof Artifact && Random.Int(2) == 0) ||
        (toDrop.isUpgradable() && Random.Int(4 - toDrop.level()) == 0)) {
        // 高价值物品放入上锁箱子
        Heap dropped = drop(toDrop, cell);
        dropped.type = Heap.Type.LOCKED_CHEST;
        addItemToSpawn(new GoldenKey(Dungeon.depth));
    }
}
```

**物品容器概率表**：

| 类型 | 概率 | 说明 |
|------|------|------|
| 普通堆 (HEAP) | 70% | 直接可见的物品 |
| 箱子 (CHEST) | 20% | 可能有宝箱怪 |
| 骨架 (SKELETON) | 5% | 诅咒物品可能闹鬼 |
| 宝箱怪 | 5% | 伪装成箱子/物品的怪物 |
| 上锁箱子 | 取决于物品价值 | 需要金钥匙 |

#### 11.2 预设物品生成 (450-467行)

```java
for (Item item : itemsToSpawn) {
    int cell = randomDropCell();
    if (item instanceof TrinketCatalyst){
        // 饰品催化剂放入上锁箱子，钥匙另放
        drop(item, cell).type = Heap.Type.LOCKED_CHEST;
        int keyCell = randomDropCell();
        drop(new GoldenKey(Dungeon.depth), keyCell).type = Heap.Type.HEAP;
    } else {
        drop(item, cell).type = Heap.Type.HEAP;
    }
}
```

#### 11.3 黑暗挑战火把 (472-490行)

```java
Random.pushGenerator(Random.Long());  // 使用独立随机种子
    if (Dungeon.isChallenged(Challenges.DARKNESS)){
        int cell = randomDropCell();
        drop(new Torch(), cell);
        if (feeling == Feeling.LARGE){
            cell = randomDropCell();
            drop(new Torch(), cell);  // 大型关卡多一个火把
        }
    }
Random.popGenerator();
```

**关键设计**：使用 `pushGenerator/popGenerator` 确保物品生成不受玩家状态影响，保证关卡生成的确定性。

#### 11.4 遗骨物品 (492-504行)

```java
ArrayList<Item> bonesItems = Bones.get();  // 从其他玩家死亡记录获取
if (bonesItems != null) {
    int cell = randomDropCell();
    for (Item i : bonesItems) {
        drop(i, cell).setHauntedIfCursed().type = Heap.Type.REMAINS;
    }
}
```

#### 11.5 枯萎玫瑰花瓣 (506-526行)

```java
DriedRose rose = Dungeon.hero.belongings.getItem(DriedRose.class);
if (rose != null && rose.isIdentified() && !rose.cursed && Ghost.Quest.completed()){
    // 目标：每2层掉1片花瓣
    int petalsNeeded = (int)Math.ceil((float)((Dungeon.depth / 2) - rose.droppedPetals) / 3);
    
    for (int i = 1; i <= petalsNeeded; i++) {
        if (rose.droppedPetals < 11) {  // 最多11片
            drop(new DriedRose.Petal(), randomDropCell());
            rose.droppedPetals++;
        }
    }
}
```

#### 11.6 缓存口粮（天赋）(528-557行)

```java
if (Dungeon.hero.hasTalent(Talent.CACHED_RATIONS)){
    Talent.CachedRationsDropped dropped = Buff.affect(Dungeon.hero, Talent.CachedRationsDropped.class);
    int targetFloor = (int)(2 + dropped.count());
    if (dropped.count() > 4) targetFloor++;
    
    if (Dungeon.depth >= targetFloor && dropped.count() < 2 + 2*Dungeon.hero.pointsInTalent(Talent.CACHED_RATIONS)){
        // 在特殊房间（非秘密、非商店）放置口粮
        int cell = randomDropCell(SpecialRoom.class);
        // ... 验证位置 ...
        drop(new SupplyRation(), cell).type = Heap.Type.CHEST;
        dropped.countUp(2);
    }
}
```

**掉落规则**：
- 天赋1级：第2、4层各掉1个
- 天赋2级：第2、4、7层各掉1个

#### 11.7 指南页 (559-584行)

```java
Collection<String> allPages = Document.ADVENTURERS_GUIDE.pageNames();
ArrayList<String> missingPages = new ArrayList<>();
for (String page : allPages){
    if (!Document.ADVENTURERS_GUIDE.isPageFound(page)){
        missingPages.add(page);
    }
}

// 掉落概率：0%/25%/50%/75%/100% 对应深度1/2/3/4/5+
float dropChance = 0.25f * (Dungeon.depth - 1);
if (!missingPages.isEmpty() && Random.Float() < dropChance){
    GuidePage p = new GuidePage();
    p.page(missingPages.get(0));
    drop(p, randomDropCell());
}
```

#### 11.8 区域传说页面 (586-646行)

```java
if (Document.ADVENTURERS_GUIDE.allPagesFound()){
    int region = 1 + (Dungeon.depth - 1) / 5;  // 区域编号
    
    Document regionDoc;
    switch(region){
        case 1: regionDoc = Document.SEWERS_GUARD; break;
        case 2: regionDoc = Document.PRISON_WARDEN; break;
        case 3: regionDoc = Document.CAVES_EXPLORER; break;
        case 4: regionDoc = Document.CITY_WARLOCK; break;
        case 5: regionDoc = Document.HALLS_KING; break;
    }
    
    // 根据已收集进度决定掉落层数
    int targetFloor = 5 * (region - 1) + 1 + Math.round(3 * percentComplete);
    if (Dungeon.depth >= targetFloor){
        drop(RegionLorePage.pageForDoc(regionDoc), randomDropCell());
    }
}
```

#### 11.9 黑曜石宝箱怪 (648-677行)

```java
if (Random.Float() < MimicTooth.ebonyMimicChance()){
    ArrayList<Integer> candidateCells = new ArrayList<>();
    
    if (Random.Int(2) == 0){
        // 50%几率伪装成普通物品堆
        for (Heap h : heaps.valueList()){
            if (h.type == Heap.Type.HEAP && !(room(h.pos) instanceof SpecialRoom)){
                candidateCells.add(h.pos);
            }
        }
    }
    
    if (candidateCells.isEmpty()){
        if (Random.Int(5) == 0 && findMob(exit()) == null){
            candidateCells.add(exit());  // 20%几率在出口
        } else {
            // 伪装成门
            for (int i = 0; i < length(); i++){
                if (map[i] == Terrain.DOOR && findMob(i) == null){
                    candidateCells.add(i);
                }
            }
        }
    }
    
    mobs.add(Mimic.spawnAt(Random.element(candidateCells), EbonyMimic.class, false));
}
```

#### 11.10 破裂望远镜额外战利品 (679-690行)

```java
int items = (int)(Random.Float() + CrackedSpyglass.extraLootChance());
for (int i = 0; i < items; i++){
    int cell = randomDropCell();
    drop(Generator.randomUsingDefaults(), cell).hidden = true;  // 隐藏物品
}
```

---

### 12. rooms() / randomRoom() / room() - 房间查询

**位置**：第 703-730 行

```java
public ArrayList<Room> rooms() {
    return new ArrayList<>(rooms);  // 返回副本，保护原始数据
}

protected Room randomRoom(Class<? extends Room> type) {
    Random.shuffle(rooms);  // 打乱顺序
    return room(type);
}

public Room room(Class<? extends Room> type) {
    for (Room r : rooms) {
        if (type.isInstance(r)) return r;
    }
    return null;
}

public Room room(int pos) {
    for (Room room : rooms) {
        if (room.inside(cellToPoint(pos))) return room;
    }
    return null;
}
```

| 方法 | 参数 | 返回 | 用途 |
|------|------|------|------|
| `rooms()` | 无 | 所有房间副本 | 获取关卡房间信息 |
| `randomRoom(type)` | 房间类型 | 随机该类型房间 | 随机选择特定类型房间 |
| `room(type)` | 房间类型 | 第一个匹配房间 | 查找特定类型房间 |
| `room(pos)` | 格子坐标 | 包含该格子的房间 | 根据位置查找房间 |

---

### 13. randomDropCell() - 物品掉落位置

**位置**：第 732-766 行

```java
protected int randomDropCell(){
    return randomDropCell(StandardRoom.class);
}

protected int randomDropCell(Class<? extends Room> roomType) {
    int tries = 100;
    while (tries-- > 0) {
        Room room = randomRoom(roomType);
        if (room == null) return -1;
        
        if (room != roomEntrance) {
            int pos = pointToCell(room.random());
            if (passable[pos] && !solid[pos]
                && pos != exit()
                && heaps.get(pos) == null          // 无其他物品
                && room.canPlaceItem(cellToPoint(pos), this)
                && findMob(pos) == null) {
                
                Trap t = traps.get(pos);
                // 不能在会销毁物品的陷阱上
                if (t == null || !(t instanceof BurningTrap 
                    || t instanceof BlazingTrap
                    || t instanceof ChillingTrap 
                    || t instanceof FrostTrap
                    || t instanceof ExplosiveTrap 
                    || t instanceof DisintegrationTrap
                    || t instanceof PitfallTrap)) {
                    return pos;
                }
            }
        }
    }
    return -1;
}
```

**禁用陷阱列表**：

| 陷阱 | 原因 |
|------|------|
| BurningTrap | 燃烧会销毁物品 |
| BlazingTrap | 烈焰会销毁物品 |
| ChillingTrap | 冰冻可能导致物品损坏 |
| FrostTrap | 冰霜可能导致物品损坏 |
| ExplosiveTrap | 爆炸会销毁物品 |
| DisintegrationTrap | 分解会销毁物品 |
| PitfallTrap | 深坑陷阱物品会掉落 |

---

### 14. fallCell() - 坠落位置

**位置**：第 768-790 行

```java
@Override
public int fallCell(boolean fallIntoPit) {
    if (fallIntoPit) {
        for (Room room : rooms) {
            if (room instanceof PitRoom) {
                ArrayList<Integer> candidates = new ArrayList<>();
                for (Point p : room.getPoints()){
                    int cell = pointToCell(p);
                    if (passable[cell] && findMob(cell) == null){
                        candidates.add(cell);
                    }
                }
                if (!candidates.isEmpty()){
                    return Random.element(candidates);
                }
            }
        }
    }
    return super.fallCell(fallIntoPit);
}
```

**用途**：
- 当玩家掉入深坑时，确定掉落位置
- 优先选择深坑房间内的安全位置

---

### 15. levelExplorePercent() - 探索进度计算

**位置**：第 792-885 行

这是一个复杂的评分系统，用于计算关卡的探索完成度。

#### 未探索房间判定标准

```java
HashSet<Room> missedRooms = new HashSet<>();

// 1. 未发现/未开启容器/包含钥匙的物品堆
for (Heap h : heaps.valueList()){
    if (h.autoExplored) continue;
    
    if (!h.seen || (h.type != Heap.Type.HEAP 
                 && h.type != Heap.Type.FOR_SALE 
                 && h.type != Heap.Type.CRYSTAL_CHEST)){
        missedRooms.add(room(h.pos));
    } else {
        for (Item i : h.items){
            if (i instanceof Key){
                missedRooms.add(room(h.pos));
                break;
            }
        }
    }
}

// 2. 魔法火焰/祭祀火焰
for (Blob b : blobs.values()){
    if (b.volume > 0) {
        if (b instanceof MagicalFireRoom.EternalFire) {
            missedRooms.add(room(MagicalFireRoom.class));
        } else if (b instanceof SacrificialFire) {
            missedRooms.add(room(SacrificeRoom.class));
        }
    }
}

// 3. 未击败的雕像/宝箱怪
for (Mob m : mobs.toArray(new Mob[0])){
    if (m.alignment != Char.Alignment.ALLY){
        if (m instanceof Statue && ((Statue) m).levelGenStatue){
            missedRooms.add(room(StatueRoom.class));
        } else if (m instanceof Mimic){
            missedRooms.add(room(m.pos));
        }
    }
}

// 4. 路障/锁门/暗门
for (int i = 0; i < length; i++){
    if (map[i] == Terrain.BARRICADE 
     || map[i] == Terrain.LOCKED_DOOR 
     || map[i] == Terrain.SECRET_DOOR){
        // 查找相邻房间
        Room candidate = null;
        for (int j : PathFinder.NEIGHBOURS4){
            if (room(i+j) != null){
                if (candidate == null || !missedRooms.contains(candidate)){
                    candidate = room(i+j);
                }
            }
        }
        if (candidate != null) missedRooms.add(candidate);
    }
}

// 5. 未使用的水晶钥匙
for (Notes.KeyRecord rec : Notes.getRecords(Notes.KeyRecord.class)){
    if (rec.depth() == depth && rec.type() == CrystalKey.class){
        for (Room r : rooms()){
            if (SpecialRoom.CRYSTAL_KEY_SPECIALS.contains(r.getClass())){
                missedRooms.add(r);
            }
        }
    }
}
```

#### 评分计算

```java
switch (missedRooms.size()){
    case 0: return 1f;    // 100%
    case 1: return 0.5f;  // 50%
    case 2: return 0.2f;  // 20%
    default: return 0f;   // 0%
}
```

**设计意图**：
- 0个房间未探索：满分
- 1个房间未探索：50%分数（惩罚较重）
- 2个房间未探索：20%分数
- 3个及以上房间未探索：0分

---

### 16. storeInBundle() / restoreFromBundle() - 序列化

**位置**：第 887-907 行

```java
@Override
public void storeInBundle(Bundle bundle) {
    super.storeInBundle(bundle);
    bundle.put("rooms", rooms);
}

@SuppressWarnings("unchecked")
@Override
public void restoreFromBundle(Bundle bundle) {
    super.restoreFromBundle(bundle);
    
    rooms = new ArrayList<>((Collection<Room>) ((Collection<?>) bundle.getCollection("rooms")));
    for (Room r : rooms) {
        r.onLevelLoad(this);
        if (r.isEntrance()){
            roomEntrance = r;
        } else if (r.isExit()){
            roomExit = r;
        }
    }
}
```

**说明**：
- 保存时存储房间列表
- 恢复时重建房间引用，并重新设置入口/出口房间引用

---

## 11. 使用示例

### 创建新的常规关卡

```java
public class MyDungeonLevel extends RegularLevel {
    
    // 1. 定义颜色主题
    {
        color1 = 0x48763c;  // 主题色1
        color2 = 0x59994a;  // 主题色2
    }
    
    // 2. 实现标准房间数量
    @Override
    protected int standardRooms(boolean forceMax) {
        if (forceMax) return 8;
        return 5 + Random.chances(new float[]{1, 2, 1});  // 5-7个
    }
    
    // 3. 实现特殊房间数量
    @Override
    protected int specialRooms(boolean forceMax) {
        if (forceMax) return 3;
        return 1 + Random.chances(new float[]{2, 1});  // 1-2个
    }
    
    // 4. 实现地形绘制器
    @Override
    protected Painter painter() {
        return new MyDungeonPainter()
                .setWater(0.3f, 4)
                .setGrass(0.2f, 3)
                .setTraps(nTraps(), trapClasses(), trapChances());
    }
    
    // 5. 可选：自定义陷阱配置
    @Override
    protected Class<?>[] trapClasses() {
        return new Class<?>[]{
            WornDartTrap.class, 
            ShockingTrap.class, 
            ToxicTrap.class
        };
    }
    
    @Override
    protected float[] trapChances() {
        return new float[]{4, 2, 1};
    }
    
    // 6. 实现资源路径
    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_MY_DUNGEON;
    }
    
    @Override
    public String waterTex() {
        return Assets.Environment.WATER_MY_DUNGEON;
    }
}
```

### 自定义布局构建器

```java
@Override
protected Builder builder() {
    // 总是使用环形布局
    return new LoopBuilder()
            .setLoopShape(3, 0.5f, 0.3f);  // 3个环
}
```

---

## 注意事项

### 1. 随机种子管理

```java
// 正确做法：使用独立随机种子
Random.pushGenerator(Random.Long());
    // ... 生成逻辑 ...
Random.popGenerator();
```

**原因**：确保关卡生成不受玩家状态（如持有的物品、天赋等）影响，保证同一关卡的生成结果可重复。

### 2. 房间引用维护

```java
// 在 initRooms() 中保存引用
initRooms.add(roomEntrance = EntranceRoom.createEntrance());

// 在 restoreFromBundle() 中恢复引用
if (r.isEntrance()) roomEntrance = r;
```

**原因**：入口/出口房间在多处使用，需要保持有效引用。

### 3. 怪物生成安全区

怪物不能生成在：
- 入口房间的8格视野内
- 入口房间的8格步行距离内
- 陷阱、植物上
- 出口位置

### 4. 物品掉落陷阱限制

避免在以下陷阱上放置物品：
- 燃烧类陷阱（BurningTrap, BlazingTrap）
- 冰冻类陷阱（ChillingTrap, FrostTrap）
- 爆炸类陷阱（ExplosiveTrap）
- 分解陷阱（DisintegrationTrap）
- 深坑陷阱（PitfallTrap）

---

## 最佳实践

### 1. 子类实现清单

创建新的 RegularLevel 子类时，必须实现：

| 方法 | 必要性 | 说明 |
|------|--------|------|
| `standardRooms()` | 必须 | 定义标准房间数量 |
| `specialRooms()` | 必须 | 定义特殊房间数量 |
| `painter()` | 必须 | 返回对应的 Painter |
| `tilesTex()` | 必须 | 返回地形纹理路径 |
| `waterTex()` | 必须 | 返回水面纹理路径 |
| `trapClasses()` | 可选 | 自定义陷阱类型 |
| `trapChances()` | 可选 | 自定义陷阱概率 |
| `builder()` | 可选 | 自定义布局风格 |

### 2. 大型关卡适配

```java
// 标准房间数量
int standards = standardRooms(feeling == Feeling.LARGE);
if (feeling == Feeling.LARGE){
    standards = (int)Math.ceil(standards * 1.5f);
}

// 怪物数量
if (feeling == Feeling.LARGE){
    mobs = (int)Math.ceil(mobs * 1.33f);
}

// 物品数量
if (feeling == Feeling.LARGE){
    nItems += 2;
}
```

### 3. 探索度计算扩展

如需添加新的"未完成"判定：

```java
// 在 levelExplorePercent() 中添加
for (MyCustomEntity e : myEntities){
    if (!e.isCompleted()){
        missedRooms.add(room(e.position()));
    }
}
```

### 4. 性能优化建议

- `rooms()` 返回副本而非原始列表，避免外部修改
- 使用 `Random.pushGenerator()` 保护关键生成逻辑
- 怪物/物品生成使用有限重试次数（30-100次）

---

## 相关类参考

| 类名 | 关系 | 说明 |
|------|------|------|
| `Level` | 父类 | 关卡基类，定义地图、怪物、物品等核心结构 |
| `Builder` | 组合 | 房间布局构建器接口 |
| `LoopBuilder` | 实现 | 环形布局构建器 |
| `FigureEightBuilder` | 实现 | 8字形布局构建器 |
| `Painter` | 组合 | 地形绘制器抽象类 |
| `Room` | 组合 | 房间基类 |
| `StandardRoom` | 组合 | 标准房间 |
| `SpecialRoom` | 组合 | 特殊房间 |
| `SecretRoom` | 组合 | 秘密房间 |
| `EntranceRoom` | 组合 | 入口房间 |
| `ExitRoom` | 组合 | 出口房间 |
| `SewerLevel` | 子类 | 下水道关卡 |
| `PrisonLevel` | 子类 | 监狱关卡 |
| `CavesLevel` | 子类 | 洞穴关卡 |
| `CityLevel` | 子类 | 城市关卡 |
| `HallsLevel` | 子类 | 大厅关卡 |

---

*文档生成时间：2026-03-26*
*源文件版本：Shattered Pixel Dungeon (GPL v3)*