# Builder API 参考

## 类声明
public abstract class Builder

## 类职责
Builder是关卡布局算法的抽象基类，负责将房间列表放置并连接成完整的关卡布局。

## 抽象方法
| 方法签名 | 返回值 | 说明 |
|----------|--------|------|
| build(ArrayList<Room> rooms) | ArrayList<Room> | 接收房间列表并返回连接成完整地图的房间列表，失败时返回null |

## 工具方法
| 方法 | 说明 |
|------|------|
| findNeighbours(ArrayList<Room> rooms) | 为房间列表中的所有房间建立邻居关系 |
| findFreeSpace(Point start, ArrayList<Room> collision, int maxSize) | 从指定起点返回最大可用空间的矩形区域 |
| angleBetweenRooms(Room from, Room to) | 返回两个房间中心点之间的角度（以度为单位，0度表示正上方） |
| placeRoom(ArrayList<Room> collision, Room prev, Room next, float angle) | 尝试放置房间，使其与前一个房间的中心点连线尽可能接近指定角度 |

## 相关子类
[LoopBuilder, FigureEightBuilder, GridBuilder, LineBuilder]