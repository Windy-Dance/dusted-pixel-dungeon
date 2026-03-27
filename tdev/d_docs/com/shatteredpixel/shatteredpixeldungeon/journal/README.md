# 日志包 (Journal Package)

## 概述

`journal` 包实现了 Shattered Pixel Dungeon 的 **进度跟踪和发现系统**。它维护玩家在整个游戏过程中发现、遭遇和学习内容的持久记录，包括物品图鉴、敌人图鉴、文档阅读状态和地牢探索笔记。

## 文件结构

- `Journal.java` - 全局保存/加载管理器
- `Notes.java` - 地牢探索笔记系统  
- `Document.java` - 游戏内文档系统
- `Catalog.java` - 物品图鉴追踪
- `Bestiary.java` - 敌人/实体百科全书

---

## Journal.java

### 类描述
`Journal` 是所有日志子系统的中央保存/加载协调器。它负责将 Catalog、Bestiary 和 Document 的数据持久化到磁盘，并在游戏启动时恢复这些数据。

### 主要方法

| 方法 | 描述 |
|------|------|
| `loadGlobal()` | 从 `journal.dat` 文件加载日志数据，恢复 Catalog、Bestiary 和 Document 状态 |
| `saveGlobal(boolean force)` | 将所有日志数据持久化到磁盘；除非强制，否则会遵守 `saveNeeded` 标志以减少不必要的写入 |

### 关键字段

| 字段 | 描述 |
|------|------|
| `JOURNAL_FILE` | 持久化存储的文件名（`journal.dat`） |
| `saveNeeded` | 脏标记，用于最小化不必要的磁盘写入 |

### 持久化机制

- 使用 libGDX 的 `Bundle` 系统进行序列化
- 数据存储在用户特定的目录中（通常是应用数据目录）
- 支持向后兼容的格式升级

---

## Notes.java

### 类描述
`Notes` 跟踪地牢运行期间的游戏内发现和兴趣点（每局会话，不跨局持久化）。它记录玩家在探索过程中遇到的特殊位置、获得的钥匙和自定义笔记。

### 主要类结构

#### Record (抽象基类)
- 所有笔记条目的基类
- 包含深度跟踪、图标和序列化支持

#### LandmarkRecord
- 记录特殊位置（深井、商店、NPC、楼层类型等）

#### KeyRecord  
- 跟踪获得的钥匙，支持数量统计

#### CustomRecord
- 用户创建或游戏生成的自定义笔记
- 支持文本、深度链接或物品链接类型

#### Landmark 枚举 (23种类型)
- `CHASM_FLOOR`, `SHOP`, `GHOST`, `RAT_KING` 等特殊位置类型

#### CustomType 枚举
- `TEXT`: 纯文本笔记
- `DEPTH`: 与特定深度关联的笔记  
- `ITEM_TYPE`: 与物品类型关联的笔记
- `SPECIFIC_ITEM`: 与特定物品实例关联的笔记

### 主要静态方法

| 方法 | 描述 |
|------|------|
| `add/remove(Landmark, depth)` | 添加/移除地标记录 |
| `add/remove(Key)` | 跟踪钥匙收集/使用 |
| `add/remove(CustomRecord)` | 管理自定义笔记 |
| `getRecords(Class<T>)` / `getRecords(int depth)` | 按类型或地牢深度查询记录 |
| `keyCount(Key)` | 获取特定钥匙的数量 |
| `contains(Landmark, depth)` | 检查是否已记录某个地标 |
| `storeInBundle/restoreFromBundle` | 用于游戏状态持久化的保存/加载 |

### 使用场景

- 自动记录重要位置（如商店、深井）
- 跟踪钥匙收集情况
- 允许玩家添加自定义提醒
- 在地图界面显示发现的标记

---

## Document.java

### 类描述
`Document` 管理游戏内的书籍/文档系统，具有每页的阅读状态跟踪。玩家可以发现文档页面并在后续阅读它们。

### 文档枚举值 (8种文档)

| 文档 | 描述 | 页数 |
|------|------|------|
| `ADVENTURERS_GUIDE` | 冒险者指南（教程） | 15页 |
| `ALCHEMY_GUIDE` | 炼金术参考指南 | 9页 |
| `INTROS` | 地牢区域介绍 | 可变 |
| `SEWERS_GUARD` 到 `HALLS_KING` | 各区域的背景故事文档 | 各1页 |

### 页面状态机

每个页面有三种状态：
- `NOT_FOUND` (0): 未发现
- `FOUND` (1): 已发现但未阅读  
- `READ` (2): 已阅读

### 主要方法

| 方法 | 描述 |
|------|------|
| `findPage(String/int)` | 标记页面为已发现（解锁页面） |
| `readPage(String/int)` | 标记页面为已阅读 |
| `isPageFound/isPageRead(String/int)` | 查询页面状态 |
| `deletePage/unreadPage(String/int)` | 重置页面状态 |
| `pageSprite()` | 获取文档/页面的图标 |
| `title/pageTitle/pageBody(String/int)` | 获取本地化文本 |
| `store/restore(Bundle)` | 序列化支持 |

### 内容展示

- 文档通常在玩家达到特定进度时自动发现
- 阅读文档提供游戏机制说明和背景故事
- 完成所有文档阅读可以解锁成就

---

## Catalog.java

### 类描述
`Catalog` 跟踪所有物品的发现情况和使用统计数据，跨多局游戏持久化。

### 物品类别枚举 (22个类别)

#### 装备类 (10类)
- `MELEE_WEAPONS`: 近战武器
- `ARMOR`: 护甲  
- `ENCHANTMENTS`: 武器附魔
- `GLYPHS`: 护甲铭文
- `THROWN_WEAPONS`: 投掷武器
- `WANDS`: 法杖
- `RINGS`: 戒指
- `ARTIFACTS`: 遗物
- `TRINKETS`: 小饰品
- `MISC_EQUIPMENT`: 其他装备

#### 消耗品类 (12类)
- `POTIONS`: 药水
- `SEEDS`: 种子
- `SCROLLS`: 卷轴
- `STONES`: 石头
- `FOOD`: 食物
- `EXOTIC_POTIONS`: 异域药水
- `EXOTIC_SCROLLS`: 异域卷轴
- `BOMBS`: 炸弹
- `TIPPED_DARTS`: 毒镖
- `BREWS_ELIXIRS`: 饮剂/灵药
- `SPELLS`: 法术
- `MISC_CONSUMABLES`: 其他消耗品

### 主要方法

| 方法 | 描述 |
|------|------|
| `isSeen(Class<?>)` / `setSeen(Class<?>)` | 检查/标记物品为已发现（当物品被识别时） |
| `useCount(Class<?>)` / `countUse/Uses(Class<?>, int)` | 跟踪使用数量 |
| `items()` | 获取类别中的所有物品类 |
| `totalItems()` / `totalSeen()` | 类别完成度统计 |
| `store/restore(Bundle)` | 持久化支持（包含 v2.5 之前徽章的遗留支持） |

### 徽章集成

- 当物品被发现（识别）时触发 `Badges.validateCatalogBadges()`
- 特定完成度可以解锁徽章成就
- 图鉴完成度在游戏菜单中显示

---

## Bestiary.java

### 类描述
`Bestiary` 是所有实体（怪物、NPC、陷阱、植物）的百科全书，具有遭遇跟踪功能。

### 实体类别枚举 (9类)

| 类别 | 描述 |
|------|------|
| `REGIONAL` | 标准敌人（按地牢区域） |
| `BOSSES` | 主要 Boss 战斗（史莱姆王、天狗、DM300、矮人国王、Yog-Dzewa） |
| `UNIVERSAL` | 随处出现的敌人（幽灵、拟态怪等） |
| `RARE` | 稀有变体（白化、强盗、混沌元素等） |
| `QUEST` | 任务相关敌人 |
| `NEUTRAL` | 非敌对 NPC |
| `ALLY` | 玩家盟友/召唤物 |
| `TRAP` | 所有 37 种陷阱类型 |
| `PLANT` | 所有 14 种植物类型（包括莲花、露珠草、种子荚等） |

### 主要方法

| 方法 | 描述 |
|------|------|
| `isSeen(Class<?>)` / `setSeen(Class<?>)` | 检查/标记实体为已遭遇 |
| `encounterCount(Class<?>)` / `countEncounter/ers(Class<?>, int)` | 跟踪击杀/激活次数 |
| `entities()` | 获取类别中的所有实体类 |
| `totalEntities()` / `totalSeen()` | 完成度统计 |

### 类转换特性

- `classConversions` 映射表用于规范化变体类
- 例如：`CorpseDust.DustWraith` → `Wraith`，确保一致的跟踪

### 战斗反馈

- 首次遭遇新敌人时显示 "新发现！" 提示
- 杀死敌人会增加计数统计
- 最终击杀特定类型的最后一只敌人会显示完成提示

---

## 架构总结

```
Journal.java (保存/加载协调器)
    │
    ├── Catalog.java ───── 物品发现和使用跟踪（全局持久化）
    ├── Bestiary.java ──── 敌人/实体百科全书（全局持久化）
    ├── Document.java ──── 文档/页面阅读状态（全局持久化）
    └── Notes.java ─────── 地牢运行中的地标、钥匙、自定义笔记（会话级别）
```

### 持久化模型

| 数据类型 | 持久化范围 | 存储位置 |
|----------|------------|----------|
| **全局数据** | 跨多局游戏 | `journal.dat` 文件 |
| **会话数据** | 单局游戏 | 游戏存档 Bundle |

### 全局数据 (Catalog, Bestiary, Document)
- 在游戏启动时从 `journal.dat` 加载
- 在发现新内容时更新
- 定期或在退出时保存到磁盘
- 跨所有游戏会话保持

### 会话数据 (Notes)
- 在游戏加载时从存档恢复
- 在游戏过程中动态更新
- 在游戏保存时写入存档
- 仅在当前游戏会话中有效

### 性能优化

- 使用稀疏数组和哈希映射进行高效查找
- 延迟保存策略减少磁盘 I/O
- 内存友好的数据结构设计
- 批量操作支持

这个日志系统为玩家提供了丰富的进度跟踪和发现体验，同时保持了良好的性能和数据完整性。