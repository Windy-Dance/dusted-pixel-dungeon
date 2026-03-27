# Terrain 接口文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/Terrain.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.levels |
| 类类型 | interface |
| 代码行数 | ~130 |

## 2. 类职责说明
Terrain接口定义了游戏中所有地形类型的常量值，是地图数据的基础标识系统。每种地形都有对应的数值ID，用于地图存储和渲染。

## 地形常量表
| 常量名 | 值 | 说明 |
|--------|-----|------|
| EMPTY | 0 | 空地/通道 |
| GRASS | 1 | 草地 |
| EMPTY_WELL | 2 | 井上方空地 |
| ENTRANCE | 4 | 入口楼梯 |
| EXIT | 5 | 出口楼梯 |
| WALL | 6 | 墙壁 |
| DOOR | 7 | 门 |
| OPEN_DOOR | 8 | 打开的门 |
| WATER | 9 | 水域 |
| TRAP | 10 | 陷阱 |
| HIGH_GRASS | 11 | 高草丛 |
| SECRET_TRAP | 12 | 隐藏陷阱 |
| SECRET_DOOR | 13 | 秘密门 |
| BOOKSHELF | 14 | 书架 |
| BARRICADE | 15 | 路障 |
| EMPTY_SP | 16 | 特殊空地 |
| CHASM | 17 | 深坑 |
| CHASM_FLOOR | 18 | 深坑边缘 |
| SIGN | 19 | 告示牌 |
| STATUE | 20 | 雕像 |
| ALCHEMY | 21 | 炼金台 |
| WELL | 22 | 井 |

## 静态方法

### passable(int terrain)
判断地形是否可通过

### solid(int terrain)
判断地形是否为实心（阻挡视线和移动）

### flamable(int terrain)
判断地形是否可燃

### losBlocking(int terrain)
判断地形是否阻挡视线

## 11. 使用示例
```java
// 检查地形类型
int cell = Dungeon.level.map[pos];
if (cell == Terrain.WATER) {
    // 处理水域效果
}

// 修改地形
Level.set(pos, Terrain.GRASS);
```