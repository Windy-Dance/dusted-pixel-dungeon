# Room API 参考

## 类声明
public abstract class Room extends Rect implements Graph.Node, Bundlable

## 类职责
Room是所有房间模板的抽象基类，负责房间大小、连接管理、绘制接口等。它继承自Rect（矩形），实现了图节点（Graph.Node）和可序列化（Bundlable）接口，为地牢生成系统提供基础房间功能。

## 关键字段
- **neigbours**: `ArrayList<Room>` - 存储相邻房间的列表
- **connected**: `LinkedHashMap<Room, Door>` - 存储已连接房间及其门类型
- **distance**: `int` - 用于图算法的距离值
- **price**: `int` - 用于图算法的权重值，默认为1

## 尺寸方法
- **minWidth()**: 返回房间最小宽度，默认返回-1（表示无限制）
- **maxWidth()**: 返回房间最大宽度，默认返回-1（表示无限制）
- **minHeight()**: 返回房间最小高度，默认返回-1（表示无限制）
- **maxHeight()**: 返回房间最大高度，默认返回-1（表示无限制）
- **setSize()**: 根据min/max设置随机尺寸
- **forceSize(int w, int h)**: 强制设置指定尺寸
- **setSizeWithLimit(int w, int h)**: 在限制范围内设置尺寸

## 连接方法
- **minConnections(int direction)**: 返回指定方向的最小连接数
- **maxConnections(int direction)**: 返回指定方向的最大连接数（全方向16，单方向4）
- **curConnections(int direction)**: 返回当前连接数
- **remConnections(int direction)**: 返回剩余可连接数
- **canConnect(Point p)**: 检查指定点是否可以连接（必须在单一边缘上，不能在角落）
- **canConnect(int direction)**: 检查指定方向是否还可以连接
- **canConnect(Room r)**: 检查是否可以与另一个房间连接
- **addNeigbour(Room other)**: 添加相邻房间
- **connect(Room room)**: 连接另一个房间
- **clearConnections()**: 清除所有连接和相邻关系

## 抽象方法
- **paint(Level level)**: 抽象绘制方法，子类必须实现具体的房间绘制逻辑

## 辅助方法
- **random()**: 返回房间内部的随机点
- **random(int m)**: 返回距离边缘至少m格的随机点
- **inside(Point p)**: 检查点是否在房间内部（不包括边缘）
- **center()**: 返回房间中心点（考虑奇偶性随机偏移）
- **pointInside(Point from, int n)**: 从指定边缘点向内偏移n格

## 放置点方法
- **canPlaceWater(Point p)**: 检查是否可以在指定点放置水
- **waterPlaceablePoints()**: 获取所有可放置水的点
- **canPlaceGrass(Point p)**: 检查是否可以在指定点放置草
- **grassPlaceablePoints()**: 获取所有可放置草的点
- **canPlaceTrap(Point p)**: 检查是否可以在指定点放置陷阱
- **trapPlaceablePoints()**: 获取所有可放置陷阱的点
- **canPlaceItem(Point p, Level l)**: 检查是否可以在指定点放置物品
- **itemPlaceablePoints(Level l)**: 获取所有可放置物品的点
- **canPlaceCharacter(Point p, Level l)**: 检查是否可以在指定点放置角色
- **charPlaceablePoints(Level l)**: 获取所有可放置角色的点

## 特殊房间类型判断
- **isEntrance()**: 判断是否为入口房间，默认返回false
- **isExit()**: 判断是否为出口房间，默认返回false

## 合并方法
- **canMerge(Level l, Room other, Point p, int mergeTerrain)**: 检查是否可以合并，默认返回false
- **merge(Level l, Room other, Rect merge, int mergeTerrain)**: 执行房间合并操作

## 图节点接口实现
- **distance()**: 获取距离值
- **distance(int value)**: 设置距离值
- **price()**: 获取价格/权重值
- **price(int value)**: 设置价格/权重值
- **edges()**: 获取有效连接的相邻房间（忽略锁定、阻挡或隐藏的门）

## 序列化接口实现
- **storeInBundle(Bundle bundle)**: 保存房间数据到bundle
- **restoreFromBundle(Bundle bundle)**: 从bundle恢复房间数据
- **onLevelLoad(Level level)**: 关卡加载时调用（目前未实现）

## 内部类：Door
Door类继承自Point，实现了Bundlable接口，表示房间之间的门。

### Door.Type 枚举
- **EMPTY**: 空门（默认）
- **TUNNEL**: 隧道
- **WATER**: 水门
- **REGULAR**: 普通门
- **UNLOCKED**: 已解锁门
- **HIDDEN**: 隐藏门
- **BARRICADE**: 路障门
- **LOCKED**: 锁定门
- **CRYSTAL**: 水晶门
- **WALL**: 墙门

### Door 方法
- **set(Type type)**: 设置门类型（只能升级，不能降级）
- **lockTypeChanges(boolean lock)**: 锁定门类型更改

## 房间类型
常见的Room子类包括：
- **StandardRoom**: 标准房间
- **SpecialRoom**: 特殊房间  
- **ConnectionRoom**: 连接房间
- **SecretRoom**: 秘密房间

## 使用示例
```java
// 创建自定义房间类
public class CustomRoom extends Room {
    @Override
    public int minWidth() { return 5; }
    
    @Override
    public int minHeight() { return 5; }
    
    @Override
    public void paint(Level level) {
        // 实现具体的房间绘制逻辑
        Painter.fill(level, this, Terrain.EMPTY);
    }
}

// 设置房间尺寸并连接
CustomRoom room = new CustomRoom();
room.setSize(); // 设置随机尺寸
room.connect(otherRoom); // 连接其他房间
```

## 相关子类
- StandardRoom
- SpecialRoom  
- ConnectionRoom
- SecretRoom
- EntranceRoom
- ExitRoom
- ShopRoom
- BlacksmithRoom
- LaboratoryRoom
- VaultRoom
- TrapsRoom
- CryptRoom
- PoolRoom
- GardenRoom
- StatueRoom
- LibraryRoom