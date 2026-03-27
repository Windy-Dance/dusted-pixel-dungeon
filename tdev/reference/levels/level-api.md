# Level API 参考

## 类声明
```java
public abstract class Level implements Bundlable
```

## 类职责
Level 是所有关卡的抽象基类，负责关卡生成、地形管理、实体放置、视野计算等核心功能。作为 Shattered Pixel Dungeon 中所有具体关卡类型（如 SewerLevel、PrisonLevel 等）的父类，它提供了统一的关卡管理接口和基础设施。

## 关键字段

### 基础属性
- `width`, `height`, `length`: 关卡的宽度、高度和总格子数
- `version`: 关卡版本号，用于存档兼容性
- `map`: 整型数组，存储每个格子的地形类型
- `visited`: 布尔数组，标记玩家是否访问过每个格子
- `mapped`: 布尔数组，标记地图卷轴是否揭示过每个格子
- `discoverable`: 布尔数组，标记格子是否可被发现（邻近非墙格子）
- `viewDistance`: 视野距离，默认为 8，黑暗挑战下为 2

### 视野相关
- `heroFOV`: 英雄视野范围的布尔数组
- `passable`: 可通行格子的布尔数组
- `losBlocking`: 阻挡视线格子的布尔数组
- `flamable`: 可燃格子的布尔数组
- `secret`: 隐藏格子的布尔数组
- `solid`: 固体格子的布尔数组
- `avoid`: 需要避开的格子的布尔数组
- `water`: 水格子的布尔数组
- `pit`: 陷阱坑格子的布尔数组
- `openSpace`: 开阔空间格子的布尔数组（足够容纳大型生物）

### 关卡结构
- `entrance`: 入口位置（已废弃，使用 transitions 替代）
- `exit`: 出口位置（已废弃，使用 transitions 替代）
- `transitions`: 关卡转换点列表，用于处理楼层间的入口出口
- `locked`: 标记关卡是否被锁定（通常用于 Boss 战）

### 实体管理
- `mobs`: 所有移动生物的集合
- `heaps`: 物品堆的稀疏数组（按位置索引）
- `blobs`: 区域效果（如火焰、毒气）的映射表
- `plants`: 植物的稀疏数组（按位置索引）
- `traps`: 陷阱的稀疏数组（按位置索引）
- `customTiles`: 自定义贴图列表
- `customWalls`: 自定义墙贴图列表

### 其他
- `feeling`: 关卡感觉类型（CHASM、WATER、GRASS、DARK、LARGE、TRAPS、SECRETS）
- `itemsToSpawn`: 待生成物品列表
- `color1`, `color2`: 关卡主色调和辅色调
- `visuals`, `wallVisuals`: 视觉效果组

## 地形常量

地形系统基于 `Terrain` 类中的常量，每个地形类型都有对应的标志位：

### 主要地形类型
- `Terrain.CHASM` (0): 深渊，不可通行，掉落伤害
- `Terrain.EMPTY` (1): 空地，可通行
- `Terrain.GRASS` (2): 草地，可通行且可燃
- `Terrain.WALL` (4): 墙壁，阻挡视线和通行
- `Terrain.DOOR` (5): 关闭的门，可燃但阻挡视线
- `Terrain.OPEN_DOOR` (6): 打开的门，可通行且可燃
- `Terrain.ENTRANCE` (7): 入口楼梯
- `Terrain.EXIT` (8): 出口楼梯
- `Terrain.EMBERS` (9): 余烬，可通行
- `Terrain.LOCKED_DOOR` (10): 锁定的门，完全阻挡
- `Terrain.HIGH_GRASS` (15): 高草，阻挡视线但可通行
- `Terrain.WATER` (29): 水，可通行的液体

### 陷阱相关
- `Terrain.SECRET_TRAP` (17): 隐藏陷阱
- `Terrain.TRAP` (18): 普通陷阱
- `Terrain.INACTIVE_TRAP` (19): 失活陷阱

### 装饰性地形
- `Terrain.WALL_DECO` (12): 装饰性墙壁
- `Terrain.EMPTY_DECO` (20): 装饰性空地
- `Terrain.STATUE` (25): 雕像
- `Terrain.BOOKSHELF` (27): 书架
- `Terrain.ALCHEMY` (28): 炼金台

### 地形标志位
- `PASSABLE` (0x01): 可通行
- `LOS_BLOCKING` (0x02): 阻挡视线
- `FLAMABLE` (0x04): 可燃
- `SECRET` (0x08): 隐藏
- `SOLID` (0x10): 固体
- `AVOID` (0x20): 需要避开
- `LIQUID` (0x40): 液体
- `PIT` (0x80): 陷阱坑

## 抽象方法

| 方法签名 | 返回值 | 说明 |
|---------|--------|------|
| `protected boolean build()` | boolean | 构建关卡布局，返回 true 表示构建成功 |
| `protected void createMobs()` | void | 创建关卡中的生物 |
| `protected void createItems()` | void | 创建关卡中的物品 |

## 核心方法

### 关卡生命周期
- `create()`: 创建关卡，调用 build()、createMobs()、createItems()
- `reset()`: 重置关卡，重新创建生物
- `restoreFromBundle(Bundle)`: 从存档恢复关卡状态
- `storeInBundle(Bundle)`: 将关卡状态保存到存档

### 实体管理
- `drop(Item, int)`: 在指定位置掉落物品，返回 Heap 对象
- `plant(Plant.Seed, int)`: 在指定位置种植植物
- `uproot(int)`: 移除指定位置的植物
- `setTrap(Trap, int)`: 在指定位置设置陷阱
- `disarmTrap(int)`: 解除指定位置的陷阱
- `discover(int)`: 揭示隐藏地形或陷阱

### 生物相关
- `createMob()`: 创建一个随机生物
- `findMob(int)`: 查找指定位置的生物
- `randomRespawnCell(Char)`: 为生物找到随机重生位置
- `randomDestination(Char)`: 为生物找到随机目标位置
- `spawnMob(int)`: 生成一个生物并添加到游戏中
- `mobLimit()`: 获取关卡生物数量限制
- `mobCount()`: 获取当前关卡生物数量

### 物品相关
- `addItemToSpawn(Item)`: 添加待生成物品到列表
- `findPrizeItem()`: 查找奖励物品
- `findPrizeItem(Class<? extends Item>)`: 查找指定类型的奖励物品

### 关卡操作
- `seal()`: 锁定关卡（用于 Boss 战）
- `unseal()`: 解锁关卡
- `setSize(int, int)`: 设置关卡尺寸
- `buildFlagMaps()`: 构建地形标志位映射
- `cleanWalls()`: 计算可发现区域
- `destroy(int)`: 破坏指定位置的地形
- `set(int, int)`: 设置指定位置的地形类型

### 视野与探测
- `updateFieldOfView(Char, boolean[])`: 更新指定角色的视野
- `occupyCell(Char)`: 处理角色占据格子时的效果
- `pressCell(int)`: 触发格子上的效果（陷阱、高草等）
- `tileName(int)`: 获取地形名称
- `tileDesc(int)`: 获取地形描述

### 辅助方法
- `distance(int, int)`: 计算两个位置的曼哈顿距离
- `adjacent(int, int)`: 判断两个位置是否相邻
- `trueDistance(int, int)`: 计算两个位置的真实欧几里得距离
- `invalidHeroPos(int)`: 判断位置是否对英雄无效
- `insideMap(int)`: 判断位置是否在关卡范围内
- `cellToPoint(int)`: 将一维位置转换为二维坐标
- `pointToCell(Point)`: 将二维坐标转换为一维位置

## 视野计算

视野计算通过 `updateFieldOfView()` 方法实现，包含以下特性：

1. **基础视线**: 使用 ShadowCaster 算法计算基础视野
2. **特殊能力**: 支持心灵视觉(MindVision)、魔法视觉(MagicalSight)等特殊视野
3. **环境影响**: 考虑烟雾屏幕、高草等地形对视野的影响
4. **角色特化**: 不同职业和天赋会影响视野范围
5. **盟友共享**: 某些技能允许共享盟友的视野

视野计算流程：
- 首先计算基础视野（考虑盲眼、潜行等状态）
- 应用特殊视野能力（心灵视觉、魔法视觉等）
- 合并所有视野源（包括盟友、物品效果等）
- 更新可见物品的显示状态

## 关卡生成流程

1. **初始化**: 调用 `create()` 方法开始关卡生成
2. **随机种子**: 设置关卡特定的随机种子以确保可重现性
3. **感觉系统**: 根据深度和随机性决定关卡感觉（水、草、黑暗等）
4. **构建布局**: 调用抽象方法 `build()` 创建关卡地形
5. **标志位映射**: 调用 `buildFlagMaps()` 建立地形属性映射
6. **清理墙壁**: 调用 `cleanWalls()` 计算可发现区域
7. **创建实体**: 调用 `createMobs()` 和 `createItems()` 生成生物和物品
8. **完成**: 关卡准备就绪，可供游戏使用

## 实体管理

Level 类维护多个数据结构来管理不同类型的实体：

- **生物(Mobs)**: 使用 HashSet 存储，支持快速查找和迭代
- **物品堆(Heaps)**: 使用 SparseArray 按位置索引，支持快速位置查询
- **区域效果(Blobs)**: 使用 HashMap 按类型存储，便于类型特定操作
- **植物(Plants)**: 使用 SparseArray 按位置索引
- **陷阱(Traps)**: 使用 SparseArray 按位置索引

实体管理还包括重生机制，通过 MobSpawner 在指定间隔内重新生成生物，确保关卡始终保持适当的挑战性。

## 使用示例

```java
// 创建自定义关卡类
public class CustomLevel extends Level {
    @Override
    protected boolean build() {
        setSize(32, 32); // 设置关卡尺寸
        
        // 使用 Painter 工具创建房间和走廊
        Painter.fill(this, 5, 5, 20, 20, Terrain.EMPTY);
        Painter.fill(this, 10, 10, 10, 10, Terrain.WALL);
        
        // 设置入口和出口
        transitions.add(new LevelTransition(LevelTransition.Type.REGULAR_ENTRANCE, 6 + 6 * width()));
        transitions.add(new LevelTransition(LevelTransition.Type.REGULAR_EXIT, 25 + 25 * width()));
        
        return true;
    }
    
    @Override
    protected void createMobs() {
        // 创建特定的生物
        Mob mob = new Gnoll();
        mob.pos = randomRespawnCell(mob);
        mobs.add(mob);
    }
    
    @Override
    protected void createItems() {
        // 添加特定物品
        addItemToSpawn(new PotionOfHealing());
        addItemToSpawn(new ScrollOfUpgrade());
    }
}

// 在游戏中使用
Level customLevel = new CustomLevel();
customLevel.create();
Dungeon.level = customLevel;
```

## 相关子类

Level 类有多个重要的子类，每个代表不同类型的关卡：

- **RegularLevel**: 基础规则关卡，包含房间和走廊的标准布局
- **SewerLevel**: 下水道关卡，第一层的主要关卡类型
- **PrisonLevel**: 监狱关卡，第二层的主要关卡类型  
- **CavesLevel**: 洞穴关卡，第三层的主要关卡类型
- **CityLevel**: 城市关卡，第四层的主要关卡类型
- **HallsLevel**: 大厅关卡，第五层的主要关卡类型
- **SewerBossLevel**, **PrisonBossLevel**, **CavesBossLevel**, **CityBossLevel**, **HallsBossLevel**: 各层的 Boss 关卡
- **LastLevel**: 最终关卡，包含阿米亚的核心
- **LastShopLevel**: 最后的商店关卡
- **MiningLevel**: 采矿关卡（特殊任务关卡）
- **VaultLevel**: 保险库关卡（特殊关卡）
- **DeadEndLevel**: 死胡同关卡（特殊关卡）

这些子类主要通过重写 `build()`、`createMobs()` 和 `createItems()` 方法来实现各自的关卡特色和游戏机制。