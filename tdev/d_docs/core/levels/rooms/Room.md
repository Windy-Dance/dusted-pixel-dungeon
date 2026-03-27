# Room.java 中文文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/rooms/Room.java` |
| **所属包** | `com.shatteredpixel.shatteredpixeldungeon.levels.rooms` |
| **类修饰符** | `public abstract` |
| **父类** | `Rect` (来自 `com.watabou.utils`) |
| **实现接口** | `Graph.Node`, `Bundlable` |
| **代码行数** | 488 行 |

### 导入依赖

```java
import com.dustedpixel.dustedpixeldungeon.levels.Level;
import com.dustedpixel.dustedpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Graph;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;


```

---

## 类职责

`Room` 是地牢关卡中所有房间类型的**抽象基类**，承担以下核心职责：

### 1. 空间定义与管理
- 定义房间的矩形边界（继承自 `Rect`）
- 管理房间的最小/最大尺寸约束
- 提供房间内随机点生成、中心点计算等空间操作

### 2. 房间连接系统
- 维护相邻房间列表 (`neigbours`)
- 管理已连接房间及其门 (`connected`)
- 实现房间间的连接可行性判断
- 提供方向性的连接数量统计

### 3. 图节点接口实现
- 实现 `Graph.Node` 接口，支持图算法路径搜索
- 提供 `distance`（距离）和 `price`（代价）属性用于寻路

### 4. 绘制抽象
- 定义抽象方法 `paint(Level level)`，由子类实现具体绘制逻辑
- 提供水域、草地、陷阱、物品、角色放置点的查询接口

### 5. 数据序列化
- 实现 `Bundlable` 接口，支持游戏存档/读档

---

## 4. 继承与协作关系

```
                    ┌─────────────────┐
                    │     Rect        │
                    │  (watabou.utils)│
                    └────────┬────────┘
                             │ extends
                             ▼
┌────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Graph.Node   │◄───┤       Room      │───►│    Bundlable    │
│ (watabou.utils)│    │   (abstract)    │    │ (watabou.utils) │
└────────────────┘    └────────┬────────┘    └─────────────────┘
                               │ extends
                               ▼
        ┌──────────────────────┼──────────────────────┐
        │                      │                      │
        ▼                      ▼                      ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│ StandardRoom  │    │  SpecialRoom  │    │  SecretRoom   │
│               │    │               │    │               │
└───────────────┘    └───────────────┘    └───────────────┘
        │                      │
        ▼                      ▼
┌───────────────┐    ┌───────────────┐
│ EntranceRoom  │    │  ExitRoom     │
│ ShopRoom      │    │  BossRoom     │
│ TunnelRoom    │    │  PitRoom      │
│ ...           │    │  ...          │
└───────────────┘    └───────────────┘

                    ┌─────────────────┐
                    │      Door       │
                    │ (inner class)   │
                    └─────────────────┘
                          ▲
                          │ contains (LinkedHashMap<Room, Door>)
                          │
                    ┌─────────────────┐
                    │      Room       │
                    └─────────────────┘
```

---

## 静态常量表

| 常量名 | 类型 | 值 | 用途说明 |
|--------|------|-----|----------|
| `ALL` | `int` | 0 | 表示所有方向的通配符，用于连接数统计时表示"所有方向的总和" |
| `LEFT` | `int` | 1 | 表示左侧方向 |
| `TOP` | `int` | 2 | 表示上侧方向 |
| `RIGHT` | `int` | 3 | 表示右侧方向 |
| `BOTTOM` | `int` | 4 | 表示下侧方向 |

---

## 实例字段表

### 公开字段

| 字段名 | 类型 | 初始值 | 用途说明 |
|--------|------|--------|----------|
| `neigbours` | `ArrayList<Room>` | `new ArrayList<>()` | 相邻房间列表，存储物理上相邻（共享边界）的房间 |
| `connected` | `LinkedHashMap<Room, Door>` | `new LinkedHashMap<>()` | 已连接房间映射表，维护房间到门的对应关系，使用 `LinkedHashMap` 保持插入顺序 |
| `distance` | `int` | 0 (默认) | 图搜索中的距离值，用于寻路算法 |
| `price` | `int` | 1 | 图搜索中的代价权重，默认为1 |

### 继承自 Rect 的字段

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `left` | `int` | 房间左边界 X 坐标 |
| `top` | `int` | 房间上边界 Y 坐标 |
| `right` | `int` | 房间右边界 X 坐标 |
| `bottom` | `int` | 房间下边界 Y 坐标 |

---

## 7. 方法详解

### 构造方法

#### `public Room()`
```java
public Room(){
    super();
}
```
- **第45-47行**：默认构造函数，调用父类 `Rect` 的无参构造函数，创建一个空矩形房间。

#### `public Room(Rect other)`
```java
public Room( Rect other ){
    super(other);
}
```
- **第49-51行**：接受一个 `Rect` 参数的构造函数，创建与给定矩形具有相同边界的房间。

---

### 房间设置方法

#### `public Room set(Room other)`
```java
public Room set( Room other ) {
    super.set( other );
    for (Room r : other.neigbours){
        neigbours.add(r);
        r.neigbours.remove(other);
        r.neigbours.add(this);
    }
    for (Room r : other.connected.keySet()){
        Door d = other.connected.get(r);
        r.connected.remove(other);
        r.connected.put(this, d);
        connected.put(r, d);
    }
    return this;
}
```
- **第53-67行**：将当前房间的属性设置为与 `other` 相同，并**接管其所有连接关系**。
- **第54行**：调用父类 `Rect.set()` 复制边界坐标。
- **第55-59行**：遍历 `other` 的邻居列表，将邻居关系转移到当前房间。
  - 从邻居的 `neigbours` 中移除 `other`
  - 添加 `this` 作为新邻居
- **第60-65行**：遍历 `other` 的已连接房间，转移门连接关系。
  - 获取门对象 `d`
  - 从对方房间的 `connected` 中移除 `other`，添加 `this`
  - 在当前房间的 `connected` 中添加映射
- **第66行**：返回 `this` 支持链式调用。

---

### 空间尺寸方法

#### `public int minWidth()` / `public int maxWidth()` / `public int minHeight()` / `public int maxHeight()`
```java
public int minWidth(){
    return -1;
}
public int maxWidth() { return -1; }
public int minHeight() { return -1; }
public int maxHeight() { return -1; }
```
- **第74-80行**：定义房间的尺寸约束。
- **返回值**：默认返回 `-1`，表示无限制。子类应重写这些方法定义具体约束。
- **重要注释（第71-73行）**：重写时必须存储任何随机决定的值。相同房间和相同参数应始终返回相同值，即使初始有随机性。

#### `public boolean setSize()`
```java
public boolean setSize(){
    return setSize(minWidth(), maxWidth(), minHeight(), maxHeight());
}
```
- **第82-84行**：使用最小/最大约束设置房间尺寸的便捷方法。委托给四参数版本。

#### `public boolean forceSize(int w, int h)`
```java
public boolean forceSize( int w, int h ){
    return setSize( w, w, h, h );
}
```
- **第86-88行**：强制设置房间为精确尺寸。将最小和最大值设为相同，确保尺寸固定。

#### `public boolean setSizeWithLimit(int w, int h)`
```java
public boolean setSizeWithLimit( int w, int h ){
    if ( w < minWidth() || h < minHeight()) {
        return false;
    } else {
        setSize();
        
        if (width() > w || height() > h){
            resize(Math.min(width(), w)-1, Math.min(height(), h)-1);
        }
        
        return true;
    }
}
```
- **第90-102行**：在限制范围内设置尺寸。
- **第91-93行**：如果限制小于最小需求，返回 `false`。
- **第94行**：先调用 `setSize()` 按常规方式设置。
- **第96-98行**：如果超出限制，缩小到限制值以内。
  - **注意**：减1是因为房间边界是包含式的（inclusive）。

#### `protected boolean setSize(int minW, int maxW, int minH, int maxH)`
```java
protected boolean setSize(int minW, int maxW, int minH, int maxH) {
    if (minW < minWidth()
            || maxW > maxWidth()
            || minH < minHeight()
            || maxH > maxHeight()
            || minW > maxW
            || minH > maxH){
        return false;
    } else {
        //subtract one because rooms are inclusive to their right and bottom sides
        resize(Random.NormalIntRange(minW, maxW) - 1,
                Random.NormalIntRange(minH, maxH) - 1);
        return true;
    }
}
```
- **第104-118行**：核心尺寸设置方法。
- **第105-110行**：验证参数有效性：
  - 最小值不能小于房间定义的最小值
  - 最大值不能大于房间定义的最大值
  - 最小值不能大于最大值
- **第113-115行**：使用 `Random.NormalIntRange` 在范围内随机选择尺寸，并减1。
  - **重要注释（第113行）**：减1是因为房间的右边界和下边界是包含式的。
  - `Rect` 的 `resize(width, height)` 实际设置的是 `right = left + width`, `bottom = top + height`
  - 由于房间边界包含右侧和底部，实际宽度是 `right - left + 1`

---

### 空间位置方法

#### `public Point pointInside(Point from, int n)`
```java
public Point pointInside(Point from, int n){
    Point step = new Point(from);
    if (from.x == left) {
        step.offset( +n, 0 );
    } else if (from.x == right) {
        step.offset( -n, 0 );
    } else if (from.y == top) {
        step.offset( 0, +n );
    } else if (from.y == bottom) {
        step.offset( 0, -n );
    }
    return step;
}
```
- **第120-132行**：从边界点向房间内部偏移 `n` 格。
- **用途**：给定一个边界上的点，返回向房间内部偏移指定步数的新点。
- **逻辑**：
  - 如果点在左边界 (`from.x == left`)，向右偏移 `+n`
  - 如果点在右边界 (`from.x == right`)，向左偏移 `-n`
  - 如果点在上边界 (`from.y == top`)，向下偏移 `+n`
  - 如果点在下边界 (`from.y == bottom`)，向上偏移 `-n`

#### `public int width()` / `public int height()`
```java
@Override
public int width() {
    return super.width()+1;
}

@Override
public int height() {
    return super.height()+1;
}
```
- **第135-143行**：重写父类方法，返回实际宽高。
- **重要注释（第134行）**：宽高加1是因为房间的右边界和下边界是包含式的。
- 父类 `Rect.width()` 返回 `right - left`，但房间实际宽度是 `right - left + 1`。

#### `public Point random()` / `public Point random(int m)`
```java
public Point random() {
    return random( 1 );
}

public Point random( int m ) {
    return new Point( Random.IntRange( left + m, right - m ),
            Random.IntRange( top + m, bottom - m ));
}
```
- **第145-152行**：在房间内生成随机点。
- **无参版本**：默认边距为1，避开边界墙。
- **有参版本**：`m` 参数控制距离边界的最小距离。
- **返回**：房间内部的一个随机坐标点。

#### `public boolean inside(Point p)`
```java
public boolean inside( Point p ) {
    return p.x > left && p.y > top && p.x < right && p.y < bottom;
}
```
- **第155-157行**：判断点是否在房间内部。
- **重要注释（第154行）**：只有当点在1格边界内部时才被认为是"内部"。
- 使用严格不等式 `>` 和 `<`，排除边界本身。

#### `public Point center()`
```java
public Point center() {
    return new Point(
            (left + right) / 2 + (((right - left) % 2) == 1 ? Random.Int( 2 ) : 0),
            (top + bottom) / 2 + (((bottom - top) % 2) == 1 ? Random.Int( 2 ) : 0) );
}
```
- **第159-163行**：获取房间中心点。
- **处理偶数尺寸**：当宽度或高度为偶数时，中心点有两个可能位置，随机选择一个。
- `(left + right) / 2`：整数除法得到左/上侧的中心候选。
- `(right - left) % 2 == 1`：宽度为奇数时有精确中心；偶数时需要随机选择。

---

### 连接相关方法

#### `public int minConnections(int direction)`
```java
public int minConnections(int direction){
    if (direction == ALL)   return 1;
    else                    return 0;
}
```
- **第174-177行**：返回指定方向的最小连接数。
- 默认实现：总连接数至少为1，各方向最小为0。
- 子类可重写以定义特定连接需求（如入口房间可能需要特定方向的连接）。

#### `public int curConnections(int direction)`
```java
public int curConnections(int direction){
    if (direction == ALL) {
        return connected.size();
        
    } else {
        int total = 0;
        for (Room r : connected.keySet()){
            Rect i = intersect( r );
            if      (direction == LEFT && i.width() == 0 && i.left == left)         total++;
            else if (direction == TOP && i.height() == 0 && i.top == top)           total++;
            else if (direction == RIGHT && i.width() == 0 && i.right == right)      total++;
            else if (direction == BOTTOM && i.height() == 0 && i.bottom == bottom)  total++;
        }
        return total;
    }
}
```
- **第179-194行**：返回指定方向的当前连接数。
- **第180-181行**：`ALL` 方向返回 `connected` 映射表大小。
- **第183-193行**：特定方向的统计：
  - 遍历所有已连接房间，计算交集 `i`
  - 交集宽度为0表示垂直相邻（左右连接）
  - 交集高度为0表示水平相邻（上下连接）
  - 检查交集边界是否与当前房间的对应边界重合

#### `public int remConnections(int direction)`
```java
public int remConnections(int direction){
    if (curConnections(ALL) >= maxConnections(ALL)) return 0;
    else return maxConnections(direction) - curConnections(direction);
}
```
- **第196-199行**：返回指定方向的剩余可用连接数。
- **第197行**：如果总连接数已达上限，返回0。
- **第198行**：返回最大连接数减去当前连接数。

#### `public int maxConnections(int direction)`
```java
public int maxConnections(int direction){
    if (direction == ALL)   return 16;
    else                    return 4;
}
```
- **第201-204行**：返回指定方向的最大连接数。
- 默认值：总连接数上限16，单方向上限4。
- 子类可重写以限制连接数（如隧道房间可能限制为2）。

#### `public boolean canConnect(Point p)`
```java
public boolean canConnect(Point p){
    //point must be along exactly one edge, no corners.
    return (p.x == left || p.x == right) != (p.y == top || p.y == bottom);
}
```
- **第207-210行**：判断指定点是否可以连接。
- **第208行注释**：点必须恰好位于一条边上，不能在角落。
- 使用异或逻辑：`(在左右边界) != (在上下边界)` 确保只在一个轴的边界上。

#### `public boolean canConnect(int direction)`
```java
public boolean canConnect(int direction){
    return remConnections(direction) > 0;
}
```
- **第213-215行**：判断指定方向是否可以连接。
- 仅检查方向限制，不检查具体点。

#### `public boolean canConnect(Room r)`
```java
public boolean canConnect( Room r ){
    if (isExit() && r.isEntrance() || isEntrance() && r.isExit()){
        //entrance and exit rooms cannot directly connect
        return false;
    }

    Rect i = intersect( r );
    
    boolean foundPoint = false;
    for (Point p : i.getPoints()){
        if (canConnect(p) && r.canConnect(p)){
            foundPoint = true;
            break;
        }
    }
    if (!foundPoint) return false;
    
    if (i.width() == 0 && i.left == left)
        return canConnect(LEFT) && r.canConnect(RIGHT);
    else if (i.height() == 0 && i.top == top)
        return canConnect(TOP) && r.canConnect(BOTTOM);
    else if (i.width() == 0 && i.right == right)
        return canConnect(RIGHT) && r.canConnect(LEFT);
    else if (i.height() == 0 && i.bottom == bottom)
        return canConnect(BOTTOM) && r.canConnect(TOP);
    else
        return false;
}
```
- **第218-245行**：综合判断两个房间是否可以连接。
- **第219-222行**：入口房间和出口房间不能直接相连。
- **第224行**：计算两房间的交集矩形。
- **第226-232行**：检查交集中是否存在双方都可连接的点。
- **第235-244行**：根据交集位置判断方向连接可行性：
  - 宽度为0表示垂直相邻（共享垂直边界线）
  - 高度为0表示水平相邻（共享水平边界线）
  - 检查双方的对应方向是否都有剩余连接容量

#### `public boolean canMerge(Level l, Room other, Point p, int mergeTerrain)`
```java
public boolean canMerge(Level l, Room other, Point p, int mergeTerrain){
    return false;
}
```
- **第247-249行**：判断是否可以与另一房间合并。
- 默认返回 `false`。子类可重写以支持特殊合并逻辑。

#### `public void merge(Level l, Room other, Rect merge, int mergeTerrain)`
```java
public void merge(Level l, Room other, Rect merge, int mergeTerrain){
    Painter.fill(l, merge, mergeTerrain);
}
```
- **第252-254行**：合并两个房间。
- **重要注释（第251行）**：可被子类重写以实现特殊合并逻辑。
- 默认使用 `Painter.fill` 在合并区域填充指定地形。

#### `public boolean addNeigbour(Room other)`
```java
public boolean addNeigbour( Room other ) {
    if (neigbours.contains(other))
        return true;
    
    Rect i = intersect( other );
    if ((i.width() == 0 && i.height() >= 2) ||
        (i.height() == 0 && i.width() >= 2)) {
        neigbours.add( other );
        other.neigbours.add( this );
        return true;
    }
    return false;
}
```
- **第256-268行**：将另一房间添加为邻居。
- **第257-258行**：如果已是邻居，直接返回 `true`。
- **第260行**：计算交集。
- **第261-262行**：检查是否物理相邻：
  - 交集宽度为0且高度≥2：垂直相邻，共享边界线
  - 交集高度为0且宽度≥2：水平相邻，共享边界线
  - 高度/宽度≥2 确保边界线长度足够放置门
- **第263-264行**：双向添加邻居关系。
- **注意**：拼写 `neigbours` 是源代码中的拼写错误（应为 `neighbours`）。

#### `public boolean connect(Room room)`
```java
public boolean connect( Room room ) {

    if ((neigbours.contains(room) || addNeigbour(room))
            && !connected.containsKey( room ) && canConnect(room)) {
        connected.put( room, null );
        room.connected.put( this, null );
        return true;
    }
    return false;
}
```
- **第270-279行**：建立两个房间的连接关系。
- **第272-273行**：三个条件必须全部满足：
  1. 已是邻居或可添加为邻居
  2. 尚未连接
  3. `canConnect(room)` 返回 `true`
- **第274-275行**：双向添加连接，门的值初始为 `null`。

#### `public void clearConnections()`
```java
public void clearConnections(){
    for (Room r : neigbours){
        r.neigbours.remove(this);
    }
    neigbours.clear();
    for (Room r : connected.keySet()){
        r.connected.remove(this);
    }
    connected.clear();
}
```
- **第281-290行**：清除所有邻居和连接关系。
- **第282-285行**：从所有邻居的邻居列表中移除自己，然后清空自己的邻居列表。
- **第286-289行**：从所有已连接房间的连接映射中移除自己，然后清空自己的连接映射。

#### `public boolean isEntrance()` / `public boolean isExit()`
```java
public boolean isEntrance(){
    return false;
}

public boolean isExit(){
    return false;
}
```
- **第292-298行**：标识房间是否为入口/出口。
- 默认返回 `false`。入口房间和出口房间子类需重写返回 `true`。

---

### 绘制相关方法

#### `public abstract void paint(Level level)`
```java
public abstract void paint(Level level);
```
- **第302行**：抽象方法，由子类实现具体的房间绘制逻辑。
- 这是房间系统的核心方法，负责在关卡中生成房间的具体内容。

#### `public boolean canPlaceWater(Point p)`
```java
public boolean canPlaceWater(Point p){
    return true;
}
```
- **第305-307行**：判断指定点是否可以放置水域。
- 默认返回 `true`。子类可重写以限制水域放置位置。

#### `public final ArrayList<Point> waterPlaceablePoints()`
```java
public final ArrayList<Point> waterPlaceablePoints(){
    ArrayList<Point> points = new ArrayList<>();
    for (int i = left; i <= right; i++) {
        for (int j = top; j <= bottom; j++) {
            Point p = new Point(i, j);
            if (canPlaceWater(p)) points.add(p);
        }
    }
    return points;
}
```
- **第309-318行**：返回所有可放置水域的点列表。
- 遍历房间内所有点，筛选 `canPlaceWater` 返回 `true` 的点。
- 标记为 `final`，不可被子类重写。

#### `public boolean canPlaceGrass(Point p)`
```java
public boolean canPlaceGrass(Point p){
    return true;
}
```
- **第321-323行**：判断指定点是否可以放置草地。
- 默认返回 `true`。子类可重写以限制草地放置位置。

#### `public final ArrayList<Point> grassPlaceablePoints()`
```java
public final ArrayList<Point> grassPlaceablePoints(){
    ArrayList<Point> points = new ArrayList<>();
    for (int i = left; i <= right; i++) {
        for (int j = top; j <= bottom; j++) {
            Point p = new Point(i, j);
            if (canPlaceGrass(p)) points.add(p);
        }
    }
    return points;
}
```
- **第325-334行**：返回所有可放置草地的点列表。
- 结构与 `waterPlaceablePoints` 相同，筛选 `canPlaceGrass` 返回 `true` 的点。

#### `public boolean canPlaceTrap(Point p)`
```java
public boolean canPlaceTrap(Point p){
    return true;
}
```
- **第337-339行**：判断指定点是否可以放置陷阱。
- 默认返回 `true`。子类可重写以限制陷阱放置位置。

#### `public final ArrayList<Point> trapPlaceablePoints()`
```java
public final ArrayList<Point> trapPlaceablePoints(){
    ArrayList<Point> points = new ArrayList<>();
    for (int i = left; i <= right; i++) {
        for (int j = top; j <= bottom; j++) {
            Point p = new Point(i, j);
            if (canPlaceTrap(p)) points.add(p);
        }
    }
    return points;
}
```
- **第341-350行**：返回所有可放置陷阱的点列表。

#### `public boolean canPlaceItem(Point p, Level l)`
```java
public boolean canPlaceItem(Point p, Level l){
    return inside(p);
}
```
- **第353-355行**：判断指定点是否可以放置物品。
- **第352行注释**：通常通过 `randomDropCell` 放置物品。
- 默认实现：只有房间内部的点才能放置物品。

#### `public final ArrayList<Point> itemPlaceablePoints(Level l)`
```java
public final ArrayList<Point> itemPlaceablePoints(Level l){
    ArrayList<Point> points = new ArrayList<>();
    for (int i = left; i <= right; i++) {
        for (int j = top; j <= bottom; j++) {
            Point p = new Point(i, j);
            if (canPlaceItem(p, l)) points.add(p);
        }
    }
    return points;
}
```
- **第357-366行**：返回所有可放置物品的点列表。

#### `public boolean canPlaceCharacter(Point p, Level l)`
```java
public boolean canPlaceCharacter(Point p, Level l){
    return inside(p);
}
```
- **第369-371行**：判断指定点是否可以放置角色。
- **第368行注释**：通常通过出生、传送或游走放置角色。
- 默认实现：只有房间内部的点才能放置角色。

#### `public final ArrayList<Point> charPlaceablePoints(Level l)`
```java
public final ArrayList<Point> charPlaceablePoints(Level l){
    ArrayList<Point> points = new ArrayList<>();
    for (int i = left; i <= right; i++) {
        for (int j = top; j <= bottom; j++) {
            Point p = new Point(i, j);
            if (canPlaceCharacter(p, l)) points.add(p);
        }
    }
    return points;
}
```
- **第373-382行**：返回所有可放置角色的点列表。

---

### Graph.Node 接口实现

#### `public int distance()` / `public void distance(int value)`
```java
@Override
public int distance() {
    return distance;
}

@Override
public void distance( int value ) {
    distance = value;
}
```
- **第387-395行**：图节点距离属性的 getter/setter。
- 用于图搜索算法（如Dijkstra）计算路径。

#### `public int price()` / `public void price(int value)`
```java
@Override
public int price() {
    return price;
}

@Override
public void price( int value ) {
    price = value;
}
```
- **第397-405行**：图节点代价属性的 getter/setter。
- 用于加权图搜索，影响路径选择的代价。

#### `public Collection<Room> edges()`
```java
@Override
public Collection<Room> edges() {
    ArrayList<Room> edges = new ArrayList<>();
    for( Room r : connected.keySet()){
        Door d = connected.get(r);
        //for the purposes of path building, ignore all doors that are locked, blocked, or hidden
        if (d.type == Door.Type.EMPTY || d.type == Door.Type.TUNNEL
                || d.type == Door.Type.UNLOCKED || d.type == Door.Type.REGULAR){
            edges.add(r);
        }
    }
    return edges;
}
```
- **第407-419行**：返回图搜索中可用的邻接节点。
- **第412-415行注释**：路径构建时忽略锁定、阻塞或隐藏的门。
- 只返回以下门类型的连接房间：
  - `EMPTY`：空门位
  - `TUNNEL`：隧道门
  - `UNLOCKED`：已解锁门
  - `REGULAR`：普通门
- 排除的门类型：`WATER`, `HIDDEN`, `BARRICADE`, `LOCKED`, `CRYSTAL`, `WALL`

---

### Bundlable 接口实现

#### `public void storeInBundle(Bundle bundle)`
```java
@Override
public void storeInBundle( Bundle bundle ) {
    bundle.put( "left", left );
    bundle.put( "top", top );
    bundle.put( "right", right );
    bundle.put( "bottom", bottom );
}
```
- **第421-427行**：将房间边界坐标保存到 Bundle。
- 注意：邻居和连接关系不保存（见第437行注释）。

#### `public void restoreFromBundle(Bundle bundle)`
```java
@Override
public void restoreFromBundle( Bundle bundle ) {
    left = bundle.getInt( "left" );
    top = bundle.getInt( "top" );
    right = bundle.getInt( "right" );
    bottom = bundle.getInt( "bottom" );
}
```
- **第429-435行**：从 Bundle 恢复房间边界坐标。

#### `public void onLevelLoad(Level level)`
```java
//FIXME currently connections and neighbours are not preserved on load
public void onLevelLoad( Level level ){
    //does nothing by default
}
```
- **第437-440行**：关卡加载后的回调方法。
- **FIXME注释**：当前连接和邻居关系在加载时不保留。
- 子类可重写以在关卡加载后重建状态。

---

## 内部类：Door

### 类定义
```java
public static class Door extends Point implements Bundlable {
```
- **第442行**：`Door` 是 `Room` 的静态内部类，继承 `Point`，实现 `Bundlable`。

### 门类型枚举
```java
public enum Type {
    EMPTY, TUNNEL, WATER, REGULAR, UNLOCKED, HIDDEN, BARRICADE, LOCKED, CRYSTAL, WALL
}
```
- **第444-446行**：定义门的类型枚举。

| 类型 | 说明 |
|------|------|
| `EMPTY` | 空门位，尚未决定类型 |
| `TUNNEL` | 隧道入口 |
| `WATER` | 水域门 |
| `REGULAR` | 普通门 |
| `UNLOCKED` | 已解锁门 |
| `HIDDEN` | 隐藏门 |
| `BARRICADE` | 路障 |
| `LOCKED` | 锁定的门 |
| `CRYSTAL` | 水晶门 |
| `WALL` | 墙（实际上是封闭的） |

### 字段

```java
public Type type = Type.EMPTY;
private boolean typeLocked = false;
```
- **第447行**：门类型，默认为 `EMPTY`。
- **第460行**：类型锁定标志，防止类型被修改。

### 构造方法

```java
public Door(){
}

public Door( Point p ){
    super(p);
}

public Door( int x, int y ) {
    super( x, y );
}
```
- **第449-458行**：三种构造方法：无参、Point参数、坐标参数。

### 方法

#### `public void lockTypeChanges(boolean lock)`
```java
public void lockTypeChanges( boolean lock ){
    typeLocked = lock;
}
```
- **第462-464行**：设置类型锁定状态。

#### `public void set(Type type)`
```java
public void set( Type type ) {
    if (!typeLocked && type.compareTo( this.type ) > 0) {
        this.type = type;
    }
}
```
- **第466-470行**：设置门类型。
- **条件**：未锁定且新类型的枚举序号大于当前类型。
- 这确保门类型只能"升级"（向更具体的类型变化）。

#### `public void storeInBundle(Bundle bundle)` / `public void restoreFromBundle(Bundle bundle)`
```java
@Override
public void storeInBundle(Bundle bundle) {
    bundle.put("x", x);
    bundle.put("y", y);
    bundle.put("type", type);
    bundle.put("type_locked", typeLocked);
}

@Override
public void restoreFromBundle(Bundle bundle) {
    x = bundle.getInt("x");
    y = bundle.getInt("y");
    type = bundle.getEnum("type", Type.class);
    typeLocked = bundle.getBoolean("type_locked");
}
```
- **第472-486行**：门的序列化和反序列化。

---

## 11. 使用示例

### 1. 创建自定义房间类型

```java
public class TreasureRoom extends Room {
    
    @Override
    public int minWidth() { return 5; }
    @Override
    public int maxWidth() { return 7; }
    @Override
    public int minHeight() { return 5; }
    @Override
    public int maxHeight() { return 7; }
    
    @Override
    public int maxConnections(int direction) {
        // 宝藏房间只能有一个入口
        return direction == ALL ? 1 : 1;
    }
    
    @Override
    public void paint(Level level) {
        // 填充地板
        Painter.fill(level, this, Terrain.EMPTY);
        
        // 放置宝箱
        Point center = center();
        level.drop(Generator.random(), level.pointToCell(center));
        
        // 绘制门
        for (Room r : connected.keySet()) {
            Door door = connected.get(r);
            door.set(Door.Type.LOCKED);
        }
    }
}
```

### 2. 房间连接检查

```java
// 检查两个房间是否可以连接
Room roomA = new StandardRoom();
Room roomB = new TunnelRoom();

if (roomA.canConnect(roomB)) {
    roomA.connect(roomB);
    System.out.println("房间已连接");
} else {
    System.out.println("房间无法连接");
}
```

### 3. 使用图搜索查找路径

```java
import com.watabou.utils.Graph;

// 查找从入口到出口的最短路径
List<Room> path = Graph.findPath(
    entranceRoom,    // 起点
    exitRoom,        // 终点
    allRooms,        // 所有房间
    Room::distance,  // 距离访问器
    Room::price      // 代价访问器
);

if (path != null) {
    System.out.println("找到路径，长度: " + path.size());
}
```

### 4. 获取可放置物品的位置

```java
// 获取房间内可放置物品的所有位置
ArrayList<Point> itemPoints = room.itemPlaceablePoints(level);
if (!itemPoints.isEmpty()) {
    Point randomPoint = Random.element(itemPoints);
    // 在随机位置放置物品
    level.drop(item, level.pointToCell(randomPoint));
}
```

---

## 注意事项

### 1. 边界包含性
房间的右边界和下边界是**包含式**的：
```java
// Rect 的定义是 left <= x <= right
// 所以实际宽度 = right - left + 1
@Override
public int width() {
    return super.width() + 1;  // 加1修正
}
```

### 2. 邻居 vs 连接
- **邻居 (neigbours)**：物理上相邻的房间，共享边界线。
- **连接 (connected)**：逻辑上相连的房间，可以通过门通行。
- 一个房间可以有很多邻居，但连接数有限制。

### 3. 门类型升级规则
门类型只能单向"升级"：
```java
// set() 方法中使用 compareTo 确保只升级
if (!typeLocked && type.compareTo(this.type) > 0) {
    this.type = type;
}
```
枚举序号决定优先级：`EMPTY(0) < TUNNEL(1) < ... < WALL(9)`。

### 4. 序列化限制
当前实现中，邻居和连接关系**不保存**到 Bundle：
```java
//FIXME currently connections and neighbours are not preserved on load
```
加载游戏后需要重建这些关系。

### 5. 拼写错误
源代码中 `neigbours` 应为 `neighbours`，但为保持兼容性未修正。

---

## 最佳实践

### 1. 重写尺寸约束时保持一致性
```java
// 正确：存储随机值确保一致性
private int storedWidth;
@Override
public int minWidth() { return storedWidth; }
@Override
public int maxWidth() { return storedWidth; }
```

### 2. 限制物品/角色放置位置
```java
@Override
public boolean canPlaceTrap(Point p) {
    // 陷阱不能放在门口
    for (Room r : connected.keySet()) {
        Door d = connected.get(r);
        if (d != null && p.equals(d)) return false;
    }
    return inside(p);
}
```

### 3. 实现特殊的合并逻辑
```java
@Override
public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
    // 特定类型的房间可以合并
    return other instanceof TunnelRoom && mergeTerrain == Terrain.EMPTY;
}

@Override
public void merge(Level l, Room other, Rect merge, int mergeTerrain) {
    // 自定义合并绘制
    Painter.fill(l, merge, mergeTerrain);
    // 添加额外的合并处理...
}
```

### 4. 入口/出口房间标识
```java
public class EntranceRoom extends Room {
    @Override
    public boolean isEntrance() {
        return true;  // 标识为入口房间
    }
    
    @Override
    public boolean canConnect(Room r) {
        // 入口不能与出口直接相连
        return !r.isExit() && super.canConnect(r);
    }
}
```

### 5. 使用 setSizeWithLimit 处理边界情况
```java
// 在有限空间内放置房间
if (!room.setSizeWithLimit(maxWidth, maxHeight)) {
    // 空间不足，跳过此房间或使用更小的尺寸
    continue;
}
```

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `Rect` | 父类 | 提供矩形边界的基础功能 |
| `Level` | 参数 | 关卡类，房间在关卡中绘制 |
| `Painter` | 工具类 | 用于在关卡中绘制地形 |
| `Graph.Node` | 接口 | 图节点接口，用于路径搜索 |
| `Bundlable` | 接口 | 序列化接口，支持存档 |
| `Point` | 组成类 | 坐标点类，用于位置表示 |
| `Bundle` | 工具类 | 序列化数据存储 |

---

*文档生成时间：2026-03-26*