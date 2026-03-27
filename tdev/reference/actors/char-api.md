# Char API 参考

## 类声明
public abstract class Char extends Actor

## 类职责
Char是所有角色（英雄、怪物、NPC）的抽象基类，提供生命值、战斗、移动、状态效果管理等核心功能。

## 关键字段
| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|-------|------|---------|-------|------|
| pos | int | public | 0 | 角色在游戏地图中的位置坐标 |
| sprite | CharSprite | public | null | 角色的精灵图像 |
| HT | int | public | 0 | 最大生命值（Hit Points Total） |
| HP | int | public | 0 | 当前生命值（Hit Points） |
| baseSpeed | float | protected | 1.0f | 基础移动速度 |
| paralysed | int | public | 0 | 麻痹状态计数器，大于0时表示麻痹 |
| rooted | boolean | public | false | 是否被定身，无法移动 |
| flying | boolean | public | false | 是否具有飞行能力 |
| invisible | int | public | 0 | 隐形状态计数器，大于0时表示隐形 |
| alignment | Alignment | public | null | 角色阵营（敌人、中立、盟友） |
| viewDistance | int | public | 8 | 视野距离 |
| fieldOfView | boolean[] | public | null | 视野范围数组，标记可见的格子 |
| buffs | LinkedHashSet<Buff> | private | new LinkedHashSet<>() | 当前角色的所有状态效果 |

## Alignment枚举
- **ENEMY**: 敌人阵营
- **NEUTRAL**: 中立阵营  
- **ALLY**: 盟友阵营

## Property枚举
| 属性 | 说明 | 内置抗性 | 内置免疫 |
|------|------|----------|----------|
| BOSS | Boss角色 | Grim, GrimTrap, ScrollOfRetribution, ScrollOfPsionicBlast | AllyBuff, Dread |
| MINIBOSS | 小Boss角色 | 无 | AllyBuff, Dread |
| BOSS_MINION | Boss随从 | 无 | 无 |
| UNDEAD | 亡灵生物 | 无 | Bleeding, ToxicGas, Poison |
| DEMONIC | 恶魔生物 | 无 | 无 |
| INORGANIC | 无机物 | 无 | Bleeding, ToxicGas, Poison |
| FIERY | 火焰生物 | WandOfFireblast, Elemental.FireElemental | Burning, Blazing |
| ICY | 冰霜生物 | WandOfFrost, Elemental.FrostElemental | Frost, Chill |
| ACIDIC | 酸性生物 | Corrosion | Ooze |
| ELECTRIC | 电系生物 | WandOfLightning, Shocking, Potential, Electricity, ShockingDart, Elemental.ShockElemental | 无 |
| LARGE | 大型生物 | 无 | 无 |
| IMMOVABLE | 不可移动 | 无 | Vertigo |
| STATIC | 静态生物（AI不受影响） | 无 | AllyBuff, Dread, Terror, Amok, Charm, Sleep, Paralysis, Frost, Chill, Slow, Speed |

## 可重写方法 - 战斗属性
| 方法签名 | 返回值 | 默认行为 | 说明 |
|----------|--------|----------|------|
| attackSkill(Char target) | int | 0 | 攻击技能值，用于命中判定 |
| defenseSkill(Char enemy) | int | 0 | 防御技能值，用于闪避判定 |
| damageRoll() | int | 1 | 基础伤害值计算 |
| drRoll() | int | 随机装甲减伤值 | 装甲减伤计算，包含树根护甲等效果 |
| defenseVerb() | String | "def_verb"本地化消息 | 防御动作描述文本 |

## 可重写方法 - 行为
| 方法签名 | 返回值 | 默认行为 | 说明 |
|----------|--------|----------|------|
| act() | boolean | 更新视野并处理不可移动角色的物品抛掷 | 每回合执行的主逻辑 |
| attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) | boolean | 执行完整攻击流程 | 自定义攻击逻辑 |
| interact(Char c) | boolean | 与相邻角色交换位置 | 交互逻辑 |
| canSurpriseAttack() | boolean | true | 是否能进行偷袭攻击 |
| speed() | float | 基于baseSpeed和各种状态效果计算 | 当前移动速度 |
| stealth() | float | 基于隐身相关效果计算 | 隐身能力值 |
| move(int step, boolean travelling) | void | 移动到指定位置，处理地形交互 | 移动逻辑 |
| modifyPassable(boolean[] passable) | boolean[] | 返回原数组 | 修改可通行性判断 |
| onMotionComplete() | void | 无操作 | 移动完成回调 |
| onAttackComplete() | void | 调用next() | 攻击完成回调 |
| onOperateComplete() | void | 调用next() | 操作完成回调 |

## 战斗方法
### 核心攻击方法
- `attack(Char enemy)`: 基础攻击方法，调用完整攻击流程
- `attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti)`: 带有伤害倍数、伤害加成和命中倍数的攻击方法

### 命中判定静态方法
- `hit(Char attacker, Char defender, boolean magic)`: 魔法攻击命中判定
- `hit(Char attacker, Char defender, float accMulti, boolean magic)`: 带命中倍数的通用命中判定
- `INFINITE_ACCURACY = 1_000_000`: 无限命中常量
- `INFINITE_EVASION = 1_000_000`: 无限闪避常量

### 伤害处理方法
- `damage(int dmg, Object src)`: 处理伤害，包括护盾、抗性、免疫等机制
- `defenseProc(Char enemy, int damage)`: 防御预处理，在装甲减伤前执行
- `attackProc(Char enemy, int damage)`: 攻击后处理，在造成伤害后执行

## Buff管理方法
### 查询方法
- `buffs()`: 返回所有状态效果的副本
- `<T extends Buff> buffs(Class<T> c)`: 返回指定类型的所有状态效果
- `<T extends Buff> buff(Class<T> c)`: 返回指定类型的单个状态效果实例
- `isCharmedBy(Char ch)`: 判断是否被指定角色魅惑

### 添加/移除方法
- `add(Buff buff)`: 添加状态效果，返回是否成功添加
- `remove(Buff buff)`: 移除指定状态效果
- `remove(Class<? extends Buff> buffClass)`: 移除指定类型的所有状态效果

### 护盾相关
- `shielding()`: 获取当前总护盾值，使用缓存优化性能
- `needsShieldUpdate`: 护盾更新标志位

## 抵抗/免疫系统
### 抗性系统
- `resist(Class effect)`: 计算对指定效果的抗性百分比（0.5倍递减）
- `resistances`: 角色自身的抗性集合

### 免疫系统  
- `isImmune(Class effect)`: 判断是否对指定效果完全免疫
- `immunities`: 角色自身的免疫集合

### 无敌系统
- `isInvulnerable(Class effect)`: 判断是否处于无敌状态（主要考虑挑战模式冻结和无敌Buff）

### 属性系统
- `properties()`: 获取角色的所有属性，包括Buff动态添加的属性
- `hasProp(Char ch, Property p)`: 静态方法，判断角色是否具有指定属性

## 移动方法
- `move(int step)`: 基础移动方法
- `move(int step, boolean travelling)`: 带旅行标志的移动方法（用于区分瞬移和普通移动）
- `distance(Char other)`: 计算与其他角色的距离
- `getCloser(int target)`: 寻找更接近目标的路径（继承自Actor）

## 生命周期方法
- `isAlive()`: 判断角色是否存活（HP > 0 或被死亡标记）
- `isActive()`: 判断角色是否活跃（等同于isAlive）
- `destroy()`: 销毁角色，清理相关状态效果
- `die(Object src)`: 角色死亡处理，调用destroy并处理死亡动画

## 使用示例

### 示例1: 创建自定义角色
```java
public class CustomMob extends Mob {
    {
        HP = HT = 20;
        baseSpeed = 1.2f;
        alignment = Alignment.ENEMY;
        
        // 添加属性
        properties.add(Property.UNDEAD);
        properties.add(Property.LARGE);
        
        // 添加内置抗性
        resistances.add(Poison.class);
        immunities.add(Bleeding.class);
    }
    
    @Override
    public int attackSkill(Char target) {
        return 15;
    }
    
    @Override
    public int defenseSkill(Char enemy) {
        return 8;
    }
    
    @Override
    public int damageRoll() {
        return Random.NormalIntRange(3, 6);
    }
    
    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 2);
    }
}
```

### 示例2: 处理伤害和死亡
```java
// 自定义伤害处理
@Override
public int defenseProc(Char enemy, int damage) {
    // 在这里处理防御预处理逻辑
    if (buff(SpecialArmor.class) != null) {
        damage = Math.max(damage - 2, 0);
    }
    return super.defenseProc(enemy, damage);
}

// 自定义死亡逻辑
@Override
public void die(Object src) {
    // 死亡前的特殊处理
    if (alignment == Alignment.ENEMY) {
        // 掉落特殊物品
        Dungeon.level.drop(new SpecialItem(), pos).sprite.drop();
    }
    super.die(src);
}

// 添加状态效果
public void addSpecialBuff() {
    Buff.affect(this, SpecialBuff.class).set(10); // 持续10回合
}
```

## 相关子类
- **Mob**: 所有怪物的基类，实现了AI逻辑
- **Hero**: 英雄类，包含装备、技能、天赋等玩家特有功能

## 常见错误

1. **直接修改HP而不调用damage()方法**
   - 错误：`char.HP -= 10;`
   - 正确：`char.damage(10, source);`
   - 原因：damage()方法处理了护盾、抗性、免疫、Buff响应等完整逻辑

2. **在buff()方法中使用isInstance而不是getClass()**
   - 错误：在需要精确类型匹配时使用`buffs(Charm.class)`
   - 正确：使用`buff(Charm.class)`获取精确实例
   - 原因：buff()方法使用getClass()进行精确匹配，而buffs()使用isInstance进行继承匹配

3. **忽略isAlive()的deathMarked机制**
   - 错误：直接检查`HP > 0`
   - 正确：使用`isAlive()`方法
   - 原因：死亡标记(Doom等效果)会让角色在HP=0时仍然保持"存活"状态

4. **忘记处理护盾系统**
   - 错误：在自定义damage()中忽略护盾
   - 正确：调用`ShieldBuff.processDamage(this, dmg, src)`
   - 原因：护盾系统是独立于HP的伤害吸收机制

5. **在act()方法中忘记调用super.act()**
   - 错误：完全重写act()而不更新视野
   - 正确：在自定义逻辑前后适当调用父类方法
   - 原因：父类act()处理了视野更新和不可移动角色的物品抛掷

6. **错误处理Property枚举的动态性**
   - 错误：认为properties()只返回静态定义的属性
   - 正确：理解ChampionEnemy.Giant等Buff会动态添加Property.LARGE
   - 原因：properties()方法会合并静态属性和Buff动态添加的属性