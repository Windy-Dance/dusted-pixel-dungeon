# Hero - 英雄类

## 概述
`Hero` 类是 Shattered Pixel Dungeon 游戏中玩家控制的角色的核心实现。它继承自 `Char`（角色）基类，并包含了英雄的所有核心功能，包括属性管理、战斗系统、行动处理、物品交互等。

作为游戏中最重要的类之一，`Hero` 负责处理玩家的所有输入、状态管理和游戏逻辑交互。

## 字段

### 核心属性字段
- **heroClass**: `HeroClass` - 英雄的基础职业（战士、法师、盗贼、猎人、决斗者、牧师）
- **subClass**: `HeroSubClass` - 英雄的进阶子职业（如狂战士、角斗士等）
- **armorAbility**: `ArmorAbility` - 当前装备的职业盔甲能力
- **talents**: `ArrayList<LinkedHashMap<Talent, Integer>>` - 天赋系统，按等级分层存储已学习的天赋和点数
- **metamorphedTalents**: `LinkedHashMap<Talent, Talent>` - 变异后的天赋映射
- **STR**: `int` - 力量属性值，初始为 10
- **lvl**: `int` - 当前等级，最大等级为 30
- **exp**: `int` - 当前经验值
- **HTBoost**: `int` - 额外的生命值加成

### 战斗相关字段
- **attackSkill**: `int` - 攻击技能等级，默认为 10
- **defenseSkill**: `int` - 防御技能等级，默认为 5
- **ready**: `boolean` - 英雄是否准备好执行下一个动作
- **damageInterrupt**: `boolean` - 是否在受到伤害时中断当前动作
- **curAction**: `HeroAction` - 当前正在执行的动作
- **lastAction**: `HeroAction` - 上一个执行的动作
- **attackTarget**: `Char` - 当前攻击目标的引用
- **resting**: `boolean` - 英雄是否处于休息状态

### 系统字段
- **belongings**: `Belongings` - 英雄的物品背包系统
- **awareness**: `float` - 觉察度，影响搜索范围
- **visibleEnemies**: `ArrayList<Mob>` - 当前可见的敌人列表
- **mindVisionEnemies**: `ArrayList<Mob>` - 心灵感应可见的敌人列表

## 构造函数

### Hero()
初始化英雄对象：
- 设置生命值 HT = HP = 20
- 设置初始力量 STR = 10
- 创建 Belongings 物品管理系统
- 初始化可见敌人列表

## 核心方法

### 属性管理方法

#### updateHT(boolean boostHP)
更新英雄的最大生命值（HT），基于等级、力量和其他加成效果。如果 `boostHP` 为 true，则同时增加当前生命值以匹配新的最大值。

#### STR()
计算并返回英雄的有效力量值，包括基础力量和各种加成（戒指、天赋、buff 等）。

#### className()
返回英雄的完整职业名称（包含子职业）。

#### name()
返回英雄的显示名称，支持伪装效果覆盖。

### 存储和加载方法

#### storeInBundle(Bundle bundle)
将英雄的所有状态序列化到 Bundle 中用于保存。

#### restoreFromBundle(Bundle bundle)
从 Bundle 中反序列化恢复英雄的所有状态。

#### preview(GamesInProgress.Info info, Bundle bundle)
预览存档信息，用于游戏选择界面显示。

### 天赋系统方法

#### hasTalent(Talent talent)
检查英雄是否拥有指定的天赋（至少分配了1点）。

#### pointsInTalent(Talent talent)
返回在指定天赋上分配的点数。

#### upgradeTalent(Talent talent)
升级指定的天赋（增加1点），并触发天赋升级事件。

#### talentPointsSpent(int tier)
返回在指定等级层级上已分配的天赋点数。

#### talentPointsAvailable(int tier)
返回在指定等级层级上可用的天赋点数。

#### bonusTalentPoints(int tier)
返回由于特殊效果（如神启药水）获得的额外天赋点数。

### 战斗系统方法

#### hitSound(float pitch)
播放攻击音效，根据武器类型和力量调整音调。

#### blockSound(float pitch)
播放格挡音效，优先使用武器格挡音效。

#### live()
英雄复活时调用，重置 buff 并应用基础再生和饥饿效果。

#### tier()
返回当前装备盔甲的等级（0-6），用于某些天赋和效果的判定。

#### shoot(Char enemy, MissileWeapon wep)
使用投掷武器攻击敌人，处理相关的天赋效果（如角斗士连击）。

#### attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti)
重写父类攻击方法，处理攻击相关的天赋消耗（精准突袭、液体敏捷等）。

#### attackSkill(Char target)
计算对目标的攻击技能值，考虑准确性加成、天赋效果和武器因素。

#### defenseSkill(Char enemy)
计算对敌人的防御技能值，考虑闪避加成、天赋效果和盔甲因素。

#### defenseVerb()
返回防御动作的描述文本，支持多种防御类型（格挡、招架、专注等）。

#### drRoll()
计算护甲提供的伤害减免，考虑力量不足的惩罚。

#### damageRoll()
计算基础伤害值，考虑武器、戒指、buff 和天赋效果。

#### heroDamageIntRange(int min, int max)
提供受四叶草影响的伤害范围计算（RNG 影响）。

#### speed()
计算移动速度，考虑速度加成、buff 和天赋效果。

#### canSurpriseAttack()
判断是否可以进行偷袭攻击，基于武器类型、力量要求等因素。

#### canAttack(Char enemy)
判断是否可以攻击指定的敌人，考虑距离、武器范围等因素。

#### attackDelay()
计算攻击延迟时间，考虑怒火戒指、剑舞等效果。

### 行动系统方法

#### act()
英雄的主要行动循环，处理所有游戏逻辑：
- 更新视野和观察
- 处理可见敌人
- 执行当前动作（移动、交互、攻击等）
- 应用树皮皮肤等被动效果

#### busy()
标记英雄为忙碌状态，无法接受新输入。

#### ready()
英雄准备就绪，可以接受新输入。

#### interrupt()
中断当前动作，通常在受到伤害或需要响应紧急情况时调用。

#### resume()
恢复之前的动作。

#### canSelfTrample()
判断英雄是否可以在当前位置自我踩踏（高草、耕地等）。

### 具体动作处理方法

#### actMove(HeroAction.Move action)
处理移动动作，包括正常移动和自我踩踏。

#### actInteract(HeroAction.Interact action)
处理与 NPC 或可交互对象的互动。

#### actBuy(HeroAction.Buy action)
处理购买动作。

#### actAlchemy(HeroAction.Alchemy action)
处理炼金动作。

#### actPickUp(HeroAction.PickUp action)
处理拾取物品动作。

#### actOpenChest(HeroAction.OpenChest action)
处理打开宝箱动作。

#### actUnlock(HeroAction.Unlock action)
处理解锁门或宝箱动作。

#### actMine(HeroAction.Mine action)
处理采矿动作（挖掘墙壁、晶体等）。

#### actTransition(HeroAction.LvlTransition action)
处理楼层切换动作。

#### actAttack(HeroAction.Attack action)
处理攻击动作，包括战前准备和天赋触发。

### 辅助方法

#### rest(boolean fullRest)
让英雄休息，消耗时间但不进行其他动作。

#### attackProc(Char enemy, int damage)
攻击后的处理，包括武器附魔、圣光武器等效果。

#### defenseProc(Char enemy, int damage)
防御后的处理，包括盔甲符文、岩石护甲等效果。

#### glyphLevel(Class<? extends Armor.Glyph> cls)
获取指定盔甲符文的等级。

#### damage(int dmg, Object src)
处理受到伤害，包括无敌状态检查、伤害减免、死亡处理等。

#### checkVisibleMobs()
检查并更新可见的敌人列表，处理自动锁定目标。

#### visibleEnemies()
返回可见敌人的数量。

#### visibleEnemy(int index)
返回指定索引的可见敌人。

#### getCloser(int target)
尝试向目标位置移动一步。

#### handle(int cell)
处理玩家点击地图单元格的输入，确定要执行的动作类型。

### 经验和升级方法

#### earnExp(int exp, Class source)
获得经验值，处理经验相关的 buff 充能和升级。

#### maxExp()
返回当前等级所需的最大经验值。

#### isStarving()
检查英雄是否处于饥饿状态。

### Buff 管理方法

#### add(Buff buff)
添加 buff，处理特定类型的 buff 限制（如无敌状态下的负面效果）。

#### remove(Buff buff)
移除 buff，刷新 UI 显示。

#### onRemove()
移除角色时的清理工作。

### 死亡和复活方法

#### die(Object cause)
英雄死亡处理，检查是否有安克（Ankh）可以复活。

#### reallyDie(Object cause)
实际的死亡处理，包括掉落物品、游戏结束等。

#### isAlive()
检查英雄是否存活，特别处理狂战士的狂暴状态。

#### resurrect()
英雄复活时的处理，恢复生命值并重新激活持久物品。

### 其他重要方法

#### move(int step, boolean travelling)
移动处理，播放相应的脚步音效。

#### onAttackComplete()
攻击完成后的处理。

#### onMotionComplete()
移动完成后的处理。

#### onOperateComplete()
操作完成后的处理（开锁、开箱等）。

#### search(boolean intentional)
搜索周围区域，发现隐藏的陷阱和门。

#### next()
推进游戏时间，仅在英雄存活时调用。

## 内部接口

### Doom
死亡回调接口，定义 `onDeath()` 方法。

## 使用示例

```java
// 创建英雄实例
Hero hero = new Hero();

// 设置职业
hero.heroClass = HeroClass.WARRIOR;

// 检查天赋
if (hero.hasTalent(Talent.HEARTY_MEAL)) {
    // 处理饱食餐效果
}

// 执行攻击
hero.shoot(enemy, throwingKnife);

// 获得经验
hero.earnExp(50, Mob.class);
```

## 注意事项

1. **性能优化**：`visibleEnemies` 列表用于避免重复计算视野内的敌人
2. **状态管理**：所有状态变更都应该通过适当的 setter 方法或系统方法进行
3. **扩展性**：通过天赋系统和职业系统提供了高度的可扩展性
4. **内存管理**：注意 buff 的生命周期管理，避免内存泄漏
5. **线程安全**：部分方法（如 `isAlive()`）进行了缓存优化以提高性能