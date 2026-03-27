# 仪式蜡烛文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\items\quest\CeremonialCandle.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.quest |
| **文件类型** | class |
| **继承关系** | extends Item |
| **代码行数** | 202 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
仪式蜡烛是用于完成法师任务的关键道具。它需要被放置在仪式地点房间的四个特定位置，当所有四个位置都放置了点燃的蜡烛时，会召唤出新生火焰元素。

### 系统定位
该类属于任务物品系统的一部分，专门用于处理与蜡烛相关的游戏逻辑，包括状态管理、火焰效果显示以及元素召唤机制。

### 不负责什么
- 不负责生成蜡烛（由法师任务生成）
- 不负责管理其他任务物品
- 不处理用户界面交互逻辑

## 3. 结构总览

### 主要成员概览
- `ritualPos` (static int): 仪式地点的位置
- `aflame` (boolean): 蜡烛是否处于点燃状态
- `AFLAME` (static String): Bundle存储键名常量

### 主要逻辑块概览
- 物品属性初始化
- 拾取/丢弃/投掷事件处理
- 状态持久化（Bundle存储/恢复）
- 视觉效果管理（火焰粒子）
- 蜡烛检查和元素召唤逻辑

### 生命周期/调用时机
- 游戏加载时：从Bundle恢复状态
- 玩家拾取时：设置aflame为false并触发检查
- 玩家丢弃或投掷时：设置aflame为false并触发检查
- 渲染时：根据aflame状态显示火焰效果

## 4. 继承与协作关系

### 父类提供的能力
- `Item`类提供的基础物品功能（image, defaultAction, unique, stackable等）
- 持久化支持（storeInBundle/restoreFromBundle）
- 标准物品操作方法（doDrop, onThrow, doPickUp等）

### 覆写的方法
- `isUpgradable()`: 返回false，不可升级
- `isIdentified()`: 返回true，始终已鉴定
- `doDrop(Hero hero)`: 丢弃时熄灭火焰并检查蜡烛
- `onThrow(int cell)`: 投掷时熄灭火焰并检查蜡烛
- `doPickUp(Hero hero, int pos)`: 拾取时熄灭火焰
- `emitter()`: 返回火焰粒子效果发射器
- `storeInBundle(Bundle bundle)`: 存储aflame状态
- `restoreFromBundle(Bundle bundle)`: 恢复aflame状态

### 实现的接口契约
无直接实现的接口

### 依赖的关键类
- `Assets`: 音频资源
- `Dungeon`: 游戏状态管理
- `Actor`: 角色管理
- `Char`: 角色基类
- `Hero`: 英雄角色
- `Elemental`: 元素生物
- `CellEmitter`: 粒子效果管理
- `ElmoParticle`: 火焰粒子效果
- `Heap`: 物品堆管理
- `RegularLevel`: 关卡基类
- `PrisonLevel`: 监狱关卡
- `RitualSiteRoom`: 仪式地点房间
- `GameScene`: 游戏场景
- `ItemSpriteSheet`: 物品精灵图
- `Sample`: 音频播放
- `Bundle`: 数据持久化
- `PathFinder`: 路径查找
- `Random`: 随机数生成

### 使用者
- `WandmakerQuest` (法师任务): 生成蜡烛
- 游戏系统: 管理蜡烛的放置和召唤逻辑

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| ritualPos | int | - | 仪式地点的位置坐标 |
| AFLAME | String | "aflame" | Bundle中存储aflame状态的键名 |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| aflame | boolean | false | 蜡烛是否处于点燃状态 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过实例初始化块进行初始化。

### 初始化块
```java
{
    image = ItemSpriteSheet.CANDLE;
    defaultAction = AC_THROW;
    unique = true;
    stackable = true;
}
```
- 设置物品图像为蜡烛图标
- 设置默认动作为投掷
- 标记为唯一物品（不会与同类型物品堆叠）
- 标记为可堆叠（允许多个蜡烛存在）

### 初始化注意事项
- `ritualPos`需要在外部设置（通常由任务系统设置）
- 蜡烛的点燃状态在拾取/丢弃/投掷时会被重置为false

## 7. 方法详解

### isUpgradable()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：确定物品是否可升级

**参数**：无

**返回值**：boolean，始终返回false

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return false;
```

**边界情况**：无

### isIdentified()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：确定物品是否已鉴定

**参数**：无

**返回值**：boolean，始终返回true

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return true;
```

**边界情况**：无

### doDrop(Hero hero)
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：处理物品被丢弃的逻辑

**参数**：
- `hero` (Hero)：执行丢弃操作的英雄

**返回值**：void

**前置条件**：物品必须在英雄的背包中

**副作用**：
- 将aflame设置为false
- 调用checkCandles()检查蜡烛状态

**核心实现逻辑**：
1. 调用父类的doDrop方法
2. 设置aflame为false
3. 调用checkCandles()方法

**边界情况**：无

### onThrow(int cell)
**可见性**：protected

**是否覆写**：是，覆写自 Item

**方法职责**：处理物品被投掷的逻辑

**参数**：
- `cell` (int)：投掷目标位置

**返回值**：void

**前置条件**：物品必须被投掷

**副作用**：
- 将aflame设置为false
- 调用checkCandles()检查蜡烛状态

**核心实现逻辑**：
1. 调用父类的onThrow方法
2. 设置aflame为false
3. 调用checkCandles()方法

**边界情况**：无

### doPickUp(Hero hero, int pos)
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：处理物品被拾取的逻辑

**参数**：
- `hero` (Hero)：执行拾取操作的英雄
- `pos` (int)：物品所在位置

**返回值**：boolean，表示拾取是否成功

**前置条件**：物品必须在指定位置且可被拾取

**副作用**：
- 如果拾取成功，将aflame设置为false

**核心实现逻辑**：
1. 调用父类的doPickUp方法
2. 如果拾取成功，设置aflame为false
3. 返回拾取结果

**边界情况**：拾取失败时不会修改aflame状态

### emitter()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：返回物品的粒子效果发射器

**参数**：无

**返回值**：Emitter，火焰粒子效果发射器或null

**前置条件**：物品正在被渲染

**副作用**：无

**核心实现逻辑**：
1. 如果aflame为true，创建并配置ElmoParticle火焰粒子发射器
2. 否则返回父类的emitter()

**边界情况**：aflame为false时返回null（父类实现）

### storeInBundle(Bundle bundle)
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：将物品状态存储到Bundle中

**参数**：
- `bundle` (Bundle)：存储数据的Bundle对象

**返回值**：void

**前置条件**：游戏需要保存状态

**副作用**：修改bundle对象的内容

**核心实现逻辑**：
1. 调用父类的storeInBundle方法
2. 将aflame状态存储到bundle中

**边界情况**：无

### restoreFromBundle(Bundle bundle)
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：从Bundle中恢复物品状态

**参数**：
- `bundle` (Bundle)：包含存储数据的Bundle对象

**返回值**：void

**前置条件**：游戏正在加载状态

**副作用**：修改物品的aflame状态

**核心实现逻辑**：
1. 调用父类的restoreFromBundle方法
2. 从bundle中读取aflame状态

**边界情况**：bundle中不存在AFLAME键时，aflame保持默认值false

### checkCandles()
**可见性**：private static

**是否覆写**：否

**方法职责**：检查仪式地点周围的四个位置是否都有点燃的蜡烛，如果是则召唤元素

**参数**：无

**返回值**：void

**前置条件**：当前关卡必须是RegularLevel且包含RitualSiteRoom

**副作用**：
- 可能点燃未点燃的蜡烛
- 可能移除所有蜡烛
- 可能召唤新生火焰元素
- 可能播放音效和粒子效果
- 可能更新任务音乐

**核心实现逻辑**：
1. 验证当前关卡类型和房间类型
2. 获取仪式地点周围四个位置的物品堆
3. 检查每个位置是否有蜡烛且已点燃（如未点燃则点燃）
4. 如果四个位置都有点燃的蜡烛：
   - 移除所有蜡烛
   - 在仪式地点召唤新生火焰元素
   - 更新任务音乐（如果是监狱关卡）
   - 播放火焰音效和粒子效果

**边界情况**：
- 关卡不是RegularLevel时直接返回
- 仪式地点房间不存在时直接返回
- 某个位置没有物品堆或物品堆中没有蜡烛时，不进行召唤
- 元素召唤位置被占用时，尝试寻找附近的空位

## 8. 对外暴露能力

### 显式 API
- 所有public方法都是显式API的一部分

### 内部辅助方法
- `checkCandles()`：不应被外部直接调用

### 扩展入口
- 无特定的扩展入口，因为这是一个具体的任务物品类

## 9. 运行机制与调用链

### 创建时机
- 由法师任务(WandmakerQuest)在任务开始时生成

### 调用者
- 游戏系统（拾取、丢弃、投掷操作）
- 渲染系统（获取粒子效果）
- 保存/加载系统（状态持久化）

### 被调用者
- `Elemental.NewbornFireElemental`：召唤元素
- `CellEmitter.get()`：创建粒子效果
- `Sample.INSTANCE.play()`：播放音效
- `GameScene.add()`：添加新角色到场景
- `PrisonLevel.updateWandmakerQuestMusic()`：更新任务音乐

### 系统流程位置
- 位于物品系统和任务系统的交界处
- 参与法师任务的核心流程

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.quest.ceremonialcandle.name | 仪式蜡烛 | 物品名称 |
| items.quest.ceremonialcandle.desc | 一套配套的蜡烛，在使用中融化在了一起。<br><br>单独看来它们毫无价值，但与其它蜡烛按特定排布共用时却能为召唤仪式聚集能量。 | 物品描述 |
| items.quest.ceremonialcandle.discover_hint | 你可在某个任务中找到该物品。 | 发现提示 |

### 依赖的资源
- 图像资源：ItemSpriteSheet.CANDLE
- 音频资源：Assets.Sounds.BURNING
- 粒子效果：ElmoParticle.FACTORY

### 中文翻译来源
来自 D:\Develop\Workspace\DustedPixelDungeon\core\src\main\assets\messages\items\items_zh.properties

## 11. 使用示例

### 基本用法
```java
// 创建仪式蜡烛（通常由任务系统完成）
CeremonialCandle candle = new CeremonialCandle();

// 设置仪式地点位置（由任务系统完成）
CeremonialCandle.ritualPos = somePosition;

// 玩家拾取蜡烛
hero.belongings.backpack.add(candle);

// 玩家将蜡烛放置在仪式地点周围
// 当四个位置都有点燃的蜡烛时，自动召唤元素
```

### 扩展示例
不适用，因为这是具体的任务物品类，不设计用于扩展。

## 12. 开发注意事项

### 状态依赖
- `ritualPos`必须在使用前正确设置
- `aflame`状态在物品移动时会被重置

### 生命周期耦合
- 与法师任务紧密耦合
- 依赖于RitualSiteRoom的存在

### 常见陷阱
- 忘记设置ritualPos会导致checkCandles无法正常工作
- 在非RegularLevel关卡中使用可能导致异常行为

## 13. 修改建议与扩展点

### 适合扩展的位置
- 无明确的扩展点，因为这是专用的任务物品

### 不建议修改的位置
- `checkCandles()`方法的核心逻辑
- 与Elemental召唤相关的代码

### 重构建议
- 可以考虑将蜡烛检查逻辑提取到单独的工具类中，以提高可测试性

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点