# ImpShopRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/rooms/standard/ImpShopRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends ShopRoom |
| **代码行数** | 161 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
ImpShopRoom 类负责创建一个由小恶魔(Imp)经营的商店房间，该房间在特定条件下（完成相关任务后）生成商店老板和商品，为玩家提供交易服务。

### 系统定位
作为标准商店房间的一种具体实现，ImpShopRoom 在地牢关卡生成过程中被用于创建特殊的商店区域，但实际内容只在玩家完成相关任务后才会生成。

### 不负责什么
- 不负责关卡的整体布局规划（由 Level 和 Room 相关类处理）
- 不负责物品生成的具体逻辑（由父类 ShopRoom 处理）
- 不负责商店界面的显示逻辑（由游戏场景处理）

## 3. 结构总览

### 主要成员概览
- boolean impSpawned: 标记小恶魔是否已生成
- 静态常量 IMP 和 ITEMS: 用于数据持久化的键名

### 主要逻辑块概览
- 尺寸限制方法（minWidth, minHeight, maxWidth, maxHeight）
- 连接限制方法（maxConnections）
- 房间绘制方法（paint）
- 商店老板放置方法（placeShopkeeper）
- 入口门获取方法（entrance）
- 商店生成方法（spawnShop）
- 物品放置方法（placeItems）
- 状态查询方法（shopSpawned）
- 数据持久化方法（storeInBundle, restoreFromBundle）
- 关卡加载回调方法（onLevelLoad）

### 生命周期/调用时机
- 在关卡生成过程中创建房间结构
- paint() 方法在关卡生成时调用，但只决定物品而不实际生成
- onLevelLoad() 方法在关卡加载时调用，检查任务完成状态并生成商店
- spawnShop() 方法在任务完成后调用，实际生成商店内容

## 4. 继承与协作关系

### 父类提供的能力
- 继承了 ShopRoom 的所有功能，包括物品生成、商店老板放置等
- 继承了 StandardRoom 和 Room 基类的基础功能

### 覆写的方法
- minWidth()
- minHeight()
- maxWidth()
- maxHeight()
- maxConnections()
- paint()
- placeShopkeeper()
- entrance()
- placeItems()
- storeInBundle()
- restoreFromBundle()
- onLevelLoad()

### 实现的接口契约
- 无直接实现的接口

### 依赖的关键类
- ShatteredPixelDungeon: 游戏主类
- Mob/Imp/ImpShopkeeper: 怪物和NPC类
- Heap/Item: 物品类
- Level/Terrain: 关卡和地形类
- ShopRoom: 父类商店房间
- GameScene: 游戏场景类
- Bundle: 数据持久化类
- Point/Random: 工具类

### 使用者
- LevelGenerator: 在关卡生成过程中选择并实例化房间类型
- Level: 在关卡加载时调用 onLevelLoad()
- Game logic: 在任务完成时触发商店生成

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| IMP | String | "imp_spawned" | 用于Bundle中存储imp生成状态的键名 |
| ITEMS | String | "items" | 用于Bundle中存储物品列表的键名 |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| impSpawned | boolean | false | 标记小恶魔是否已经生成 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，无自定义构造逻辑。

### 初始化块
无静态或实例初始化块。

### 初始化注意事项
- impSpawned 字段初始为 false
- 房间尺寸被强制固定为 9x9，以确保足够的空间放置48个物品和中心区域

## 7. 方法详解

### minWidth()/minHeight()/maxWidth()/maxHeight()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：强制房间尺寸为固定9x9

**参数**：无

**返回值**：int，固定返回9

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
直接返回常量9，确保房间大小一致。

**边界情况**：无

### maxConnections()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：限制房间的最大连接数

**参数**：
- `direction` (int)：连接方向

**返回值**：int，最大连接数

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
返回常量2，限制每个方向最多2个连接。

**边界情况**：无

### paint()

**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：在关卡生成阶段预生成商店物品

**参数**：
- `level` (Level)：关卡对象

**返回值**：void

**前置条件**：房间边界已设置

**副作用**：
- 初始化 itemsToSpawn 列表（如果为空）

**核心实现逻辑**：
如果 itemsToSpawn 为空，则调用 generateItems() 生成商店物品列表。注意此时不实际放置物品或生成商店老板。

**边界情况**：多次调用不会重复生成物品

### placeShopkeeper()

**可见性**：protected

**是否覆写**：是，覆写自 ShopRoom

**方法职责**：放置商店老板（小恶魔商人）

**参数**：
- `level` (Level)：关卡对象

**返回值**：void

**前置条件**：房间已完全初始化

**副作用**：
- 创建 ImpShopkeeper 实例
- 根据当前场景将其添加到适当的位置

**核心实现逻辑**：
1. 计算中心位置作为默认放置点
2. 如果房间内有基座(Pedestal)地形，则优先使用基座位置
3. 创建 ImpShopkeeper 实例并设置位置
4. 根据当前是否在游戏中场景，决定添加到 GameScene 还是 level.mobs

**边界情况**：当没有基座时使用房间中心点

### entrance()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：获取房间的入口门位置

**参数**：无

**返回值**：Door，入口门对象

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
如果房间没有连接门，则创建一个默认的入口门位置（底部偏右）；否则调用父类方法。

**边界情况**：当 connected 为空时提供默认位置

### spawnShop()

**可见性**：public

**是否覆写**：否

**方法职责**：实际生成商店内容

**参数**：
- `level` (Level)：关卡对象

**返回值**：void

**前置条件**：任务已完成

**副作用**：
- 设置 impSpawned 为 true
- 生成商店老板和物品

**核心实现逻辑**：
1. 标记 impSpawned 为 true
2. 调用 placeShopkeeper() 放置商店老板
3. 调用 placeItems() 放置商店物品

**边界情况**：可以多次调用，但 impSpawned 只会设置一次

### placeItems()

**可见性**：protected

**是否覆写**：是，覆写自 ShopRoom

**方法职责**：放置商店物品，处理已有物品

**参数**：
- `level` (Level)：关卡对象

**返回值**：void

**前置条件**：itemsToSpawn 已初始化

**副作用**：
- 移除房间内现有物品堆
- 放置新的商店物品
- 将原有物品移至房间下方

**核心实现逻辑**：
1. 扫描房间内的所有物品堆，收集并移除它们
2. 调用父类 placeItems() 放置新的商店物品
3. 将收集到的原有物品放置在房间下方的随机位置

**边界情况**：当没有原有物品时跳过移动步骤

### shopSpawned()

**可见性**：public

**是否覆写**：否

**方法职责**：查询商店是否已生成

**参数**：无

**返回值**：boolean，商店是否已生成

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
直接返回 impSpawned 字段的值。

**边界情况**：无

### storeInBundle()/restoreFromBundle()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：序列化和反序列化房间状态

**参数**：
- `bundle` (Bundle)：数据包对象

**返回值**：void

**前置条件**：无

**副作用**：
- 存储或恢复 impSpawned 状态和物品列表

**核心实现逻辑**：
调用父类方法处理基本状态，然后额外处理 impSpawned 状态和 itemsToSpawn 列表。

**边界情况**：当 itemsToSpawn 为空时不存储物品数据

### onLevelLoad()

**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：处理关卡加载时的逻辑

**参数**：
- `level` (Level)：关卡对象

**返回值**：void

**前置条件**：关卡正在加载

**副作用**：
- 可能生成商店内容

**核心实现逻辑**：
调用父类方法，然后检查 Imp.Quest.isCompleted() 状态。如果任务已完成且商店未生成，则调用 spawnShop()。

**边界情况**：当任务未完成或商店已生成时不做任何操作

## 8. 对外暴露能力

### 显式 API
- spawnShop(): 公共方法用于生成商店
- shopSpawned(): 公共方法用于查询商店状态

### 内部辅助方法
- placeShopkeeper(), placeItems(): 受保护方法用于内部逻辑

### 扩展入口
- 可通过继承修改商店生成条件
- 可通过覆写 placeShopkeeper() 自定义商店老板类型

## 9. 运行机制与调用链

### 创建时机
- 在 LevelGenerator 选择房间类型时创建

### 调用者
- LevelGenerator: 调用 paint() 预生成物品
- Level: 调用 onLevelLoad() 检查任务状态
- Game logic: 调用 spawnShop() 生成商店

### 被调用者
- ShopRoom.generateItems(): 生成商店物品
- ImpShopkeeper 构造函数: 创建商店老板
- GameScene.add()/level.mobs.add(): 添加NPC
- Bundle 相关方法: 数据持久化

### 系统流程位置
- 关卡生成阶段：预生成物品
- 关卡加载阶段：检查任务完成状态
- 任务完成后：实际生成商店

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| levels.rooms.quest.ambitiousimproom$questentrance.name | 宝库入口 | 小恶魔相关任务的入口名称 |

### 依赖的资源
- ImpShopkeeper NPC 的精灵图和动画资源
- 商店物品的图标和描述资源
- Pedestal 地形的纹理资源

### 中文翻译来源
来自 levels_zh.properties 文件：
- levels.rooms.quest.ambitiousimproom$questentrance.name = 宝库入口

## 11. 使用示例

### 基本用法
```java
// 在关卡生成器中创建小恶魔商店房间
ImpShopRoom impShopRoom = new ImpShopRoom();
// 设置房间边界（实际上会被强制为9x9）
impShopRoom.set(new Rect(left, top, right, bottom));
// 预生成商店物品
impShopRoom.paint(level);
// 在关卡加载时检查任务状态
impShopRoom.onLevelLoad(level);
```

### 任务完成后生成商店
```java
// 当玩家完成小恶魔任务后
if (Imp.Quest.isCompleted() && !impShopRoom.shopSpawned()) {
    impShopRoom.spawnShop(level);
}
```

## 12. 开发注意事项

### 状态依赖
- 依赖于 Imp.Quest 任务系统的状态
- 依赖于 itemsToSpawn 列表的正确初始化
- 依赖于关卡加载和游戏场景的正确状态

### 生命周期耦合
- paint() 只预生成物品，不实际放置
- 实际生成必须通过 spawnShop() 或 onLevelLoad()
- 必须正确处理数据持久化以确保跨关卡一致性

### 常见陷阱
- 如果不理解两阶段生成机制（预生成+实际生成），可能导致商店无法正确显示
- 在错误的时机调用 spawnShop() 可能导致NPC重复生成
- 数据持久化必须正确处理，否则会导致物品丢失

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可以修改商店生成条件（例如基于其他任务或游戏进度）
- 可以自定义商店老板类型（例如不同的Imp变体）
- 可以调整物品放置逻辑以适应不同的房间布局

### 不建议修改的位置
- 固定的9x9尺寸不应修改，这是为了确保48个物品的放置空间
- 任务完成检查逻辑不应移除，这是商店生成的核心条件

### 重构建议
- 注释中提到商店房间可能不应该继承特殊房间，这值得考虑
- 现有的两阶段生成机制虽然复杂但必要，应保持清晰的文档说明

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点