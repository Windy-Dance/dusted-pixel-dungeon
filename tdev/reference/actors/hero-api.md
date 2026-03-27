# Hero API 参考

## 类声明
public class Hero extends Char

## 类职责
Hero是玩家控制的角色类，提供职业系统、天赋系统、物品栏管理、经验成长等核心功能。作为游戏中玩家角色的具体实现，Hero类管理着角色的属性、战斗能力、装备、技能以及与游戏世界的交互。

## 关键字段
| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| heroClass | HeroClass | public | HeroClass.ROGUE | 英雄的基础职业 |
| subClass | HeroSubClass | public | HeroSubClass.NONE | 英雄的子职业 |
| armorAbility | ArmorAbility | public | null | 职业护甲能力 |
| talents | ArrayList<LinkedHashMap<Talent, Integer>> | public | new ArrayList<>() | 天赋系统数据 |
| metamorphedTalents | LinkedHashMap<Talent, Talent> | public | new LinkedHashMap<>() | 变异天赋映射 |
| attackSkill | int | private | 10 | 基础攻击技能值 |
| defenseSkill | int | private | 5 | 基础防御技能值 |
| ready | boolean | public | false | 是否准备就绪 |
| damageInterrupt | boolean | public | true | 受伤时是否中断行动 |
| curAction | HeroAction | public | null | 当前执行的动作 |
| lastAction | HeroAction | public | null | 上一个动作 |
| attackTarget | Char | private | - | 当前攻击目标 |
| resting | boolean | public | false | 是否在休息 |
| belongings | Belongings | public | - | 物品栏管理器 |
| STR | int | public | - | 力量属性值 |
| awareness | float | public | - | 感知范围系数 |
| lvl | int | public | 1 | 当前等级 |
| exp | int | public | 0 | 当前经验值 |
| HTBoost | int | private | 0 | 最大生命值加成 |

## HeroClass枚举
所有可用的基础职业，每个职业都有独特的起始装备和游戏风格：

- **WARRIOR (战士)**: 近战专家，高生命值和防御力，使用短剑作为起始武器
- **MAGE (法师)**: 魔法使用者，依赖法杖和魔法物品，起始装备法师法杖
- **ROGUE (盗贼)**: 敏捷型角色，擅长潜行和远程攻击，起始装备匕首和烟雾弹披风
- **HUNTRESS (猎人)**: 自然亲和者，使用徒手攻击和灵弓，具有优秀的感知能力
- **DUELIST (决斗家)**: 武器大师，精通多种武器类型，起始装备刺剑
- **CLERIC (牧师)**: 神圣魔法使用者，能够治疗和净化，起始装备教棍和圣典

## HeroSubClass枚举
各职业的子职业，提供更专业化的能力：

### 战士子职业
- **BERSERKER (狂战士)**: 专注于狂暴攻击和生存能力
- **GLADIATOR (角斗士)**: 专注于连击和精准攻击

### 法师子职业
- **BATTLEMAGE (战斗法师)**: 结合近战和魔法攻击
- **WARLOCK (术士)**: 专注于灵魂汲取和召唤

### 盗贼子职业
- **ASSASSIN (刺客)**: 专注于致命一击和暗杀
- **FREERUNNER (自由跑者)**: 专注于机动性和闪避

### 猎人子职业
- **SNIPER (狙击手)**: 专注于远程精确射击
- **WARDEN (守护者)**: 专注于自然防御和支援

### 决斗家子职业
- **CHAMPION (冠军)**: 专注于双武器战斗
- **MONK (武僧)**: 专注于徒手战斗和能量控制

### 牧师子职业
- **PRIEST (祭司)**: 专注于神圣攻击和诅咒
- **PALADIN (圣骑士)**: 专注于防御和团队保护

## 核心方法

### 经验与升级系统
- **earnExp(int exp, Class source)**: 获得经验值并处理升级逻辑
- **maxExp()**: 获取当前等级所需的最大经验值
- **updateHT(boolean boostHP)**: 更新最大生命值（HT）并可选择性地提升当前生命值

### 战斗系统
- **attackSkill(Char target)**: 计算对指定目标的攻击技能值，考虑准确度修正
- **defenseSkill(Char enemy)**: 计算对指定敌人的防御技能值，考虑闪避修正
- **damageRoll()**: 计算造成的伤害值
- **attackDelay()**: 获取攻击延迟时间
- **canAttack(Char enemy)**: 检查是否能攻击指定敌人
- **shoot(Char enemy, MissileWeapon wep)**: 执行远程武器攻击

### 属性计算
- **STR()**: 获取实际力量值（包含各种加成）
- **speed()**: 获取移动速度（包含各种修正）

### 行动与交互
- **act()**: 主行动循环，处理各种游戏动作
- **handle(int cell)**: 处理对指定格子的交互
- **rest(boolean fullRest)**: 执行休息动作
- **search(boolean intentional)**: 执行搜索动作，发现隐藏物品和陷阱

### 状态管理
- **live()**: 角色复活后的初始化
- **die(Object cause)**: 处理死亡逻辑
- **resurrect()**: 执行复活逻辑
- **interrupt()**: 中断当前动作
- **ready()**: 设置为准备状态

## 天赋系统

### 天赋查询方法
- **hasTalent(Talent talent)**: 检查是否拥有指定天赋
- **pointsInTalent(Talent talent)**: 获取指定天赋的点数
- **upgradeTalent(Talent talent)**: 升级指定天赋
- **talentPointsSpent(int tier)**: 获取指定层级已花费的天赋点数
- **talentPointsAvailable(int tier)**: 获取指定层级可用的天赋点数

### 天赋层级系统
天赋分为4个层级，分别在2级、7级、13级和21级解锁：
- **Tier 1**: 基础天赋，适用于所有职业
- **Tier 2**: 进阶天赋，提供更专业的加成
- **Tier 3**: 子职业专属天赋，在获得子职业后解锁
- **Tier 4**: 护甲能力专属天赋，在装备职业护甲后解锁

### 天赋存储
天赋数据通过`storeInBundle()`和`restoreFromBundle()`方法进行持久化保存。

## 行动系统

HeroAction类及其子类定义了英雄可以执行的各种动作：

### 移动相关
- **Move**: 移动到指定位置
- **LvlTransition**: 通过楼梯或传送门切换关卡

### 交互相关
- **Interact**: 与NPC或其他角色交互
- **Attack**: 攻击指定目标
- **Alchemy**: 使用炼金台

### 物品操作
- **PickUp**: 拾取指定位置的物品
- **OpenChest**: 打开宝箱或棺材
- **Buy**: 购买商店物品

### 环境操作
- **Unlock**: 解锁门或箱子
- **Mine**: 采矿或破坏墙壁

## Belongings物品栏系统

Belongings类管理英雄的所有装备和物品：

### 装备槽位
- **weapon**: 主武器
- **armor**: 护甲
- **artifact**: 遗物
- **misc**: 杂项装备（如戒指）
- **ring**: 戒指
- **secondWep**: 第二武器（冠军子职业专用）
- **thrownWeapon**: 投掷武器（临时装备）
- **abilityWeapon**: 能力武器（决斗家专用）

### 核心方法
- **attackingWeapon()**: 获取当前用于攻击的武器
- **getItem(Class<T> itemClass)**: 获取指定类型的物品
- **getAllItems(Class<T> itemClass)**: 获取所有指定类型的物品
- **contains(Item item)**: 检查是否包含指定物品
- **identify()**: 识别所有物品
- **observe()**: 观察并识别装备物品

### 背包系统
内置Backpack类管理物品存储，支持嵌套背包系统。

## 使用示例

### 示例1: 检查英雄状态
```java
// 检查英雄是否存活
if (Dungeon.hero.isAlive()) {
    // 获取当前等级
    int level = Dungeon.hero.lvl;
    
    // 检查是否拥有特定天赋
    if (Dungeon.hero.hasTalent(Talent.HEARTY_MEAL)) {
        // 获取天赋点数
        int points = Dungeon.hero.pointsInTalent(Talent.HEARTY_MEAL);
    }
    
    // 获取当前职业名称
    String className = Dungeon.hero.className();
}
```

### 示例2: 给予经验值
```java
// 给予10点经验值
Dungeon.hero.earnExp(10, Mob.class);

// 检查是否达到新等级
if (Dungeon.hero.exp >= Dungeon.hero.maxExp()) {
    // 英雄将自动升级
    GLog.p("英雄升级了！");
}
```

### 示例3: 装备管理
```java
// 获取当前武器
Weapon currentWeapon = Dungeon.hero.belongings.weapon();

// 检查是否有特定物品
if (Dungeon.hero.belongings.getItem(RingOfForce.class) != null) {
    // 使用戒指效果
}

// 遍历所有物品
for (Item item : Dungeon.hero.belongings) {
    // 处理每个物品
}
```

### 示例4: 天赋系统操作
```java
// 升级天赋（通常在玩家选择后调用）
if (Dungeon.hero.talentPointsAvailable(1) > 0) {
    Dungeon.hero.upgradeTalent(Talent.HEARTY_MEAL);
}

// 检查特定层级的天赋点数
int tier1Points = Dungeon.hero.talentPointsSpent(1);
```

## 相关文件
- **HeroClass.java**: 定义基础职业枚举和初始化逻辑
- **HeroSubClass.java**: 定义子职业枚举
- **Talent.java**: 定义天赋系统和所有天赋效果
- **Belongings.java**: 定义物品栏管理系统
- **HeroAction.java**: 定义英雄动作系统