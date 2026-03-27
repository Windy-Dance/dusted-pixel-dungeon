# WndBlacksmith 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndBlacksmith.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 557 |

## 2. 类职责说明
WndBlacksmith 是铁匠NPC服务窗口，提供多种物品强化和改造服务，包括镐子、重铸、硬化、升级、锻造和现金兑换等功能。

## 7. 方法详解
- **构造函数 WndBlacksmith()**: 创建铁匠服务窗口，显示所有可用的服务选项。
- **服务分类**: 提供6种主要服务：Pickaxe（镐子）、Reforge（重铸）、Harden（硬化）、Upgrade（升级）、Smith（锻造）、Cash Out（现金兑换）。
- **物品选择**: 大多数服务需要玩家选择特定的物品作为操作目标，集成 WndBag.ItemSelector 实现物品选择功能。
- **费用计算**: 根据物品类型、等级和服务类型计算所需金币费用。
- **嵌套窗口**: 每种服务都有对应的内部窗口类（如 WndReforge、HardenSelector 等）处理具体的交互逻辑。
- **状态验证**: 在执行服务前验证玩家是否满足条件（如拥有足够金币、物品是否符合条件等）。
- **结果反馈**: 服务完成后显示成功或失败的消息，并更新相关游戏状态。