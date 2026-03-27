# 镐子文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\items\quest\Pickaxe.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.quest |
| **文件类型** | class |
| **继承关系** | extends MeleeWeapon |
| **代码行数** | 147 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
镐子是一个特殊的近战武器，同时也是采矿任务中的关键工具。它具有独特的决斗家武技，可以对具有硬质表皮的敌人造成额外伤害并施加易伤效果。

### 系统定位
该类属于任务物品和武器系统的交汇点，既是任务必需品又是功能性武器。它在采矿关卡中具有特殊地位，不能被丢弃或投掷。

### 不负责什么
- 不处理采矿关卡的地形生成逻辑
- 不管理任务进度（由外部系统管理）
- 不提供通用的挖掘功能（仅在特定关卡中作为工具）

## 3. 结构总览

### 主要成员概览
- 实例字段：tier = 2（武器等级）
- 覆写方法：STRReq(), actions(), keptThroughLostInventory(), targetingPrompt(), duelistAbility(), abilityInfo(), upgradeAbilityStat()

### 主要逻辑块概览
- 武器属性初始化
- 力量需求调整
- 动作限制（在采矿关卡中禁用丢弃和投掷）
- 决斗家武技实现
- 能力信息显示

### 生命周期/调用时机
- 游戏加载时：从Bundle恢复状态
- 战斗时：使用普通攻击或决斗家武技
- 采矿关卡中：作为必需工具保留

## 4. 继承与协作关系

### 父类提供的能力
- `MeleeWeapon`类提供的所有近战武器功能
- 基础攻击、伤害计算、升级系统等
- 武器相关的标准方法和属性

### 覆写的方法
- `STRReq(int lvl)`: 增加2点力量需求
- `actions(Hero hero)`: 在采矿关卡中移除丢弃和投掷动作
- `keptThroughLostInventory()`: 在采矿关卡中始终保留
- `targetingPrompt()`: 返回目标提示消息
- `duelistAbility(Hero hero, Integer target)`: 实现决斗家武技
- `abilityInfo()`: 返回武技信息
- `upgradeAbilityStat(int level)`: 返回升级后的伤害统计

### 实现的接口契约
无直接实现的接口

### 依赖的关键类
- `Assets`: 音频资源
- `Dungeon`: 游戏状态管理
- `Actor`: 角色管理
- `Char`: 角色基类
- `Buff`: 状态效果基类
- `Hero`: 英雄角色
- `MeleeWeapon`: 近战武器基类
- `MiningLevel`: 采矿关卡
- `Messages`: 国际化消息
- `ItemSpriteSheet`: 物品精灵图
- `AttackIndicator`: 攻击指示器
- `GLog`: 游戏日志
- `Sample`: 音频播放
- `Callback`: 回调接口

### 使用者
- 决斗家英雄：使用武技
- 采矿任务系统：作为必需工具
- 战斗系统：处理攻击逻辑

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无静态常量 | - | - | - |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| tier | int | 2 | 武器等级，影响基础伤害和属性 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过实例初始化块进行初始化。

### 初始化块
```java
{
    image = ItemSpriteSheet.PICKAXE;
    levelKnown = true;
    unique = true;
    bones = false;
    tier = 2;
}
```
- 设置物品图像为镐子图标
- 标记为等级已知（不需要鉴定）
- 标记为唯一物品
- 禁用遗骨生成（不会在玩家死亡时掉落）
- 设置武器等级为2

### 初始化注意事项
- 作为任务必需品，必须在采矿关卡中保持可用
- 具有比同等级武器更高的力量需求

## 7. 方法详解

### STRReq(int lvl)
**可见性**：public

**是否覆写**：是，覆写自 MeleeWeapon

**方法职责**：计算使用武器所需的力量值

**参数**：
- `lvl` (int)：武器等级

**返回值**：int，所需力量值（比同等级武器多2点）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
1. 调用父类的STRReq方法获取基准力量需求
2. 增加2点额外力量需求
3. 返回结果

**边界情况**：无

### actions(Hero hero)
**可见性**：public

**是否覆写**：是，覆写自 MeleeWeapon

**方法职责**：获取物品可用的操作列表

**参数**：
- `hero` (Hero)：当前英雄

**返回值**：ArrayList<String>，操作列表

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
1. 获取父类的动作列表
2. 如果当前关卡是MiningLevel，移除AC_DROP和AC_THROW动作
3. 返回结果列表

**边界情况**：非采矿关卡中包含所有标准动作

### keptThroughLostInventory()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：确定物品是否在玩家失去库存时保留

**参数**：无

**返回值**：boolean，在采矿关卡中始终返回true

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
1. 调用父类的keptThroughLostInventory方法
2. 如果当前关卡是MiningLevel，返回true
3. 返回父类结果或MiningLevel检查结果

**边界情况**：非采矿关卡中使用父类逻辑

### targetingPrompt()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：返回目标选择提示文本

**参数**：无

**返回值**：String，本地化的目标提示消息

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Messages.get(this, "prompt");
```

**边界情况**：无

### duelistAbility(Hero hero, Integer target)
**可见性**：protected

**是否覆写**：是，覆写自 MeleeWeapon

**方法职责**：实现决斗家的特殊武技

**参数**：
- `hero` (Hero)：使用武技的英雄
- `target` (Integer)：目标位置

**返回值**：void

**前置条件**：hero必须是决斗家且有有效的目标

**副作用**：
- 可能对目标造成伤害
- 可能施加易伤效果
- 消耗武技充能
- 消除隐身效果

**核心实现逻辑**：
1. 验证目标有效性（存在、可见、非友方等）
2. 设置能力武器临时引用
3. 验证攻击范围
4. 执行动画和攻击逻辑：
   - 对硬质表皮敌人造成额外伤害（+(8+2*lvl)）
   - 必定命中（INFINITE_ACCURACY）
   - 如果目标存活，施加3回合易伤效果
   - 播放强力攻击音效
5. 消除隐身效果
6. 消耗攻击时间

**边界情况**：
- 无效目标时显示错误消息并返回
- 攻击范围外时显示错误消息并返回

### abilityInfo()
**可见性**：public

**是否覆写**：是，覆写自 MeleeWeapon

**方法职责**：返回武技的详细信息描述

**参数**：无

**返回值**：String，包含伤害范围的本地化描述

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
1. 计算额外伤害值：8 + 2 * buffedLvl()
2. 计算最终伤害范围：min()+dmgBoost 到 max()+dmgBoost
3. 应用伤害增幅修正（augment.damageFactor）
4. 返回格式化的本地化消息

**边界情况**：无

### upgradeAbilityStat(int level)
**可见性**：public

**是否覆写**：否

**方法职责**：返回指定等级下的武技伤害统计

**参数**：
- `level` (int)：武器等级

**返回值**：String，"最小伤害-最大伤害"格式的字符串

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
1. 计算额外伤害值：8 + 2 * level
2. 计算最终伤害范围：min(level)+dmgBoost 到 max(level)+dmgBoost
3. 应用伤害增幅修正
4. 返回格式化的字符串

**边界情况**：无

## 8. 对外暴露能力

### 显式 API
- 所有public方法都是显式API的一部分
- `duelistAbility()`提供决斗家专用武技
- `abilityInfo()`和`upgradeAbilityStat()`提供武技信息

### 内部辅助方法
- 无内部辅助方法

### 扩展入口
- 可以通过修改硬质表皮敌人的判定逻辑来扩展武技效果

## 9. 运行机制与调用链

### 创建时机
- 由采矿任务在玩家进入采矿关卡时提供

### 调用者
- 决斗家英雄：触发武技
- 游戏系统：查询可用动作和物品保留逻辑
- 战斗系统：处理普通攻击

### 被调用者
- `Actor.findChar()`: 查找目标角色
- `hero.canAttack()`: 验证攻击范围
- `hero.sprite.attack()`: 执行攻击动画
- `Char.hasProp()`: 检查敌人属性
- `Buff.affect()`: 应用易伤效果
- `Sample.INSTANCE.play()`: 播放音效
- `Invisibility.dispel()`: 消除隐身

### 系统流程位置
- 位于武器系统、任务系统和角色能力系统的交界处
- 参与采矿任务和决斗家职业的核心流程

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.quest.pickaxe.name | 镐子 | 物品名称 |
| items.quest.pickaxe.ability_name | 穿刺 | 武技名称 |
| items.quest.pickaxe.ability_desc | 决斗家可使用镐子_穿刺_一个敌人。穿刺必定命中，同时施加3回合的易伤效果，且会对有硬质表皮的敌人造成_%1$d~%2$d点伤害_。 | 武技描述 |
| items.quest.pickaxe.upgrade_ability_stat_name | 武技伤害 | 升级统计名称 |
| items.quest.pickaxe.desc | 这是一件笨重但十分耐用的凿岩工具。不需要装备上就能作为挖掘工具使用，但在紧要关头也能作为武器装备。 | 物品描述 |
| items.quest.pickaxe.discover_hint | 你可在某个任务中找到该物品。 | 发现提示 |

### 依赖的资源
- 图像资源：ItemSpriteSheet.PICKAXE
- 音频资源：Assets.Sounds.HIT_STRONG

### 中文翻译来源
来自 D:\Develop\Workspace\DustedPixelDungeon\core\src\main\assets\messages\items\items_zh.properties

## 11. 使用示例

### 基本用法
```java
// 创建镐子（通常由任务系统完成）
Pickaxe pickaxe = new Pickaxe();

// 决斗家使用武技
if (hero.heroClass == HeroClass.DUELIST) {
    pickaxe.duelistAbility(hero, targetPosition);
}

// 在采矿关卡中，镐子不能被丢弃或投掷
// 系统自动处理动作限制
```

### 扩展示例
不适用，因为这是具体的任务物品类，不设计用于扩展。

## 12. 开发注意事项

### 状态依赖
- 依赖于当前关卡类型来决定动作可用性
- 依赖于英雄职业来决定武技可用性

### 生命周期耦合
- 与采矿任务系统紧密耦合
- 与决斗家职业系统有强依赖

### 常见陷阱
- 忘记在非采矿关卡中正确处理动作列表
- 在非决斗家英雄身上错误地启用武技

## 13. 修改建议与扩展点

### 适合扩展的位置
- 硬质表皮敌人的判定逻辑（Char.Property.INORGANIC检查）
- 武技伤害计算公式
- 易伤效果的持续时间

### 不建议修改的位置
- 核心的动作限制逻辑（采矿关卡中的丢弃/投掷禁用）
- 物品保留逻辑（keptThroughLostInventory）

### 重构建议
- 可以考虑将硬质表皮敌人的判定提取为配置常量

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点