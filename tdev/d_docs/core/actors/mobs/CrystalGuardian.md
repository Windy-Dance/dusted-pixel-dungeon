# CrystalGuardian 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/CrystalGuardian.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.actors.mobs |
| 类类型 | public class |
| 继承关系 | extends Mob |
| 代码行数 | 282 行 |

## 2. 类职责说明
CrystalGuardian（水晶守卫）是一种小Boss级别的怪物，具有极高的生命值和伤害减免。当生命值归零时会进入恢复状态而不是死亡，每回合回复5点生命直到满血。只有在击破水晶尖塔后才能真正击杀。移动时可以破坏水晶地形。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Mob {
        +spriteClass Class
        +HP int
        +HT int
        +defenseSkill int
        +EXP int
        +maxLvl int
        +state State
        +properties Set~Property~
        +act() boolean
        +damageRoll() int
        +attackSkill() int
        +defenseSkill() int
        +drRoll() int
        +isAlive() boolean
        +isInvulnerable() boolean
        +move() void
        +storeInBundle() void
        +restoreFromBundle() void
    }
    
    class CrystalGuardian {
        +spriteClass CrystalGuardianSprite
        +HP 100, HT 100
        +defenseSkill 14
        +EXP 10
        +maxLvl -2
        +state SLEEPING
        +properties INORGANIC, MINIBOSS
        -recovering boolean
        +recovering() boolean
        +act() boolean
        +damageRoll() int
        +attackSkill() int
        +defenseSkill() int
        +surprisedBy() boolean
        +drRoll() int
        +reset() boolean
        +attack() boolean
        +defenseProc() int
        +isAlive() boolean
        +isInvulnerable() boolean
        +spawningWeight() float
        +speed() float
        +move() void
        +modifyPassable() boolean[]
        +beckon() void
        +storeInBundle() void
        +restoreFromBundle() void
        +Sleeping inner class
    }
    
    Mob <|-- CrystalGuardian
    CrystalGuardian +-- Sleeping
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| SPRITE | String | "sprite" | Bundle 存储键 - 精灵类 |
| RECOVERING | String | "recovering" | Bundle 存储键 - 恢复状态 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| spriteClass | Class | 初始化块 | 精灵类（随机蓝/绿/红） |
| HP | int | 初始化块 | 当前生命值 100 |
| HT | int | 初始化块 | 最大生命值 100 |
| defenseSkill | int | 初始化块 | 防御技能 14 |
| EXP | int | 初始化块 | 经验值 10 |
| maxLvl | int | 初始化块 | 最大等级 -2（无等级上限） |
| state | State | 初始化块 | 初始状态为 SLEEPING |
| properties | Set\<Property\> | 初始化块 | INORGANIC（无机物）、MINIBOSS（小Boss） |
| recovering | boolean | private | 是否处于恢复状态 |

## 7. 方法详解

### recovering
**签名**: `public boolean recovering()`
**功能**: 获取恢复状态
**返回值**: boolean - 是否正在恢复
**实现逻辑**:
```java
// 第66-68行：返回恢复状态
return recovering;
```

### act
**签名**: `protected boolean act()`
**功能**: 执行行动逻辑（包含恢复逻辑）
**返回值**: boolean - 行动完成
**实现逻辑**:
```java
// 第71-89行：恢复状态下的行动
if (recovering) {                                    // 如果正在恢复
    if (buff(PinCushion.class) != null) {            // 移除针垫状态
        buff(PinCushion.class).detach();
    }
    throwItems();                                    // 丢弃物品
    HP = Math.min(HT, HP + 5);                       // 每回合恢复5点生命
    if (Dungeon.level.heroFOV[pos]) {                // 显示恢复效果
        sprite.showStatusWithIcon(CharSprite.POSITIVE, "5", FloatingText.HEALING);
    }
    if (HP == HT) {                                  // 如果满血
        recovering = false;                          // 结束恢复状态
        if (sprite instanceof CrystalGuardianSprite) {
            ((CrystalGuardianSprite) sprite).endCrumple(); // 恢复站立动画
        }
    }
    spend(TICK);
    return true;
}
return super.act();
```

### damageRoll
**签名**: `public int damageRoll()`
**功能**: 计算伤害值
**返回值**: int - 随机伤害值（10-16）
**实现逻辑**:
```java
// 第92-94行：计算随机伤害
return Random.NormalIntRange(10, 16);  // 高伤害输出
```

### attackSkill
**签名**: `public int attackSkill(Char target)`
**功能**: 获取攻击技能值
**参数**:
- target: Char - 攻击目标
**返回值**: int - 攻击技能值（20）
**实现逻辑**:
```java
// 第97-99行：返回攻击技能
return 20;  // 高攻击技能
```

### defenseSkill
**签名**: `public int defenseSkill(Char enemy)`
**功能**: 获取防御技能值（恢复时为0）
**参数**:
- enemy: Char - 攻击者
**返回值**: int - 防御技能值
**实现逻辑**:
```java
// 第102-105行：恢复时防御为0
if (recovering) return 0;               // 恢复时无法防御
else            return super.defenseSkill(enemy);
```

### surprisedBy
**签名**: `public boolean surprisedBy(Char enemy, boolean attacking)`
**功能**: 判断是否被偷袭
**参数**:
- enemy: Char - 攻击者
- attacking: boolean - 是否正在攻击
**返回值**: boolean - 是否被偷袭
**实现逻辑**:
```java
// 第108-111行：恢复时不会被偷袭
if (recovering) return false;                        // 恢复时不会被偷袭
else            return super.surprisedBy(enemy, attacking);
```

### drRoll
**签名**: `public int drRoll()`
**功能**: 计算伤害减免值
**返回值**: int - 随机伤害减免值（0-10）
**实现逻辑**:
```java
// 第114-116行：计算伤害减免
return super.drRoll() + Random.NormalIntRange(0, 10);  // 高伤害减免
```

### reset
**签名**: `public boolean reset()`
**功能**: 是否重置状态
**返回值**: boolean - true（不随玩家离开而重置）
**实现逻辑**:
```java
// 第119-121行：不会重置
return true;  // 守卫不会重置
```

### attack
**签名**: `public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti)`
**功能**: 攻击时扣减任务分数
**参数**:
- enemy: Char - 攻击目标
- dmgMulti: float - 伤害倍数
- dmgBonus: float - 伤害加成
- accMulti: float - 命中倍数
**返回值**: boolean - 是否成功攻击
**实现逻辑**:
```java
// 第124-138行：攻击时扣减分数
if (enemy == Dungeon.hero) {                         // 如果攻击玩家
    boolean spireNear = false;
    for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])) {
        if (m instanceof CrystalSpire && m.HP != m.HT && Dungeon.level.distance(pos, m.pos) <= 8) {
            spireNear = true;                        // 检查附近是否有受损的水晶尖塔
        }
    }
    if (!spireNear) {                                // 如果没有，扣减100分
        Statistics.questScores[2] -= 100;
    }
}
return super.attack(enemy, dmgMulti, dmgBonus, accMulti);
```

### defenseProc
**签名**: `public int defenseProc(Char enemy, int damage)`
**功能**: 防御处理（恢复状态下伤害不致死）
**参数**:
- enemy: Char - 攻击者
- damage: int - 受到的伤害
**返回值**: int - 最终伤害值
**实现逻辑**:
```java
// 第141-150行：恢复时受到的伤害不会致死
if (recovering) {
    sprite.showStatusWithIcon(CharSprite.NEGATIVE, Integer.toString(damage), FloatingText.PHYS_DMG_NO_BLOCK);
    HP = Math.max(1, HP - damage);                    // 生命值最低为1
    damage = -1;                                      // 返回-1表示被阻挡
}
return super.defenseProc(enemy, damage);
```

### isAlive
**签名**: `public boolean isAlive()`
**功能**: 判断是否存活（触发恢复状态）
**返回值**: boolean - 是否存活
**实现逻辑**:
```java
// 第153-171行：生命值归零时进入恢复状态
if (HP <= 0) {
    HP = 1;                                           // 生命值设为1
    for (Buff b : buffs()) {                          // 移除大部分Buff
        if (!(b instanceof Doom || b instanceof Cripple)) {
            b.detach();
        }
    }
    if (!recovering) {                                // 如果还未在恢复状态
        recovering = true;                            // 进入恢复状态
        Bestiary.setSeen(getClass());                 // 记录遭遇
        Bestiary.countEncounter(getClass());
        if (sprite != null) ((CrystalGuardianSprite) sprite).crumple(); // 播放坍塌动画
    }
}
return super.isAlive();
```

### isInvulnerable
**签名**: `public boolean isInvulnerable(Class effect)`
**功能**: 判断是否对特定效果免疫
**参数**:
- effect: Class - 效果类型
**返回值**: boolean - 是否免疫
**实现逻辑**:
```java
// 第174-181行：恢复时对非玩家攻击免疫
if (recovering) {
    // 恢复时免疫来自非英雄/水晶尖塔的攻击
    return super.isInvulnerable(effect) || (Char.class.isAssignableFrom(effect) && !Hero.class.isAssignableFrom(effect) && !CrystalSpire.class.isAssignableFrom(effect));
}
return super.isInvulnerable(effect);
```

### 构造函数
**签名**: `public CrystalGuardian()`
**功能**: 初始化守卫并随机设置颜色
**实现逻辑**:
```java
// 第183-196行：随机选择颜色
super();
switch (Random.Int(3)) {
    case 0: default:
        spriteClass = CrystalGuardianSprite.Blue.class;   // 蓝色
        break;
    case 1:
        spriteClass = CrystalGuardianSprite.Green.class;  // 绿色
        break;
    case 2:
        spriteClass = CrystalGuardianSprite.Red.class;    // 红色
        break;
}
```

### spawningWeight
**签名**: `public float spawningWeight()`
**功能**: 获取生成权重
**返回值**: float - 生成权重（0，不随机生成）
**实现逻辑**:
```java
// 第199-201行：不随机生成
return 0;  // 只在特定位置生成
```

### speed
**签名**: `public float speed()`
**功能**: 计算移动速度（狭窄空间减速）
**返回值**: float - 移动速度
**实现逻辑**:
```java
// 第204-210行：狭窄空间移动更慢
if (!Dungeon.level.openSpace[pos]) {
    return Math.max(0.25f, super.speed() / 4f);       // 狭窄空间速度降低到1/4
}
return super.speed();
```

### move
**签名**: `public void move(int step, boolean travelling)`
**功能**: 移动并破坏水晶地形
**参数**:
- step: int - 目标位置
- travelling: boolean - 是否在旅行
**实现逻辑**:
```java
// 第213-225行：移动并破坏水晶
super.move(step, travelling);
if (Dungeon.level.map[pos] == Terrain.MINE_CRYSTAL) {    // 如果踩到水晶
    Level.set(pos, Terrain.EMPTY);                        // 破坏水晶
    GameScene.updateMap(pos);
    if (Dungeon.level.heroFOV[pos]) {
        Splash.at(pos, 0xFFFFFF, 5);                      // 显示破碎效果
        Sample.INSTANCE.play(Assets.Sounds.SHATTER);      // 播放破碎音效
    }
    spend(1 / super.speed());                             // 破坏水晶消耗额外时间
}
```

### modifyPassable
**签名**: `public boolean[] modifyPassable(boolean[] passable)`
**功能**: 修改可行走区域（可穿越水晶）
**参数**:
- passable: boolean[] - 原始可行走数组
**返回值**: boolean[] - 修改后的可行走数组
**实现逻辑**:
```java
// 第228-240行：追击时可穿越水晶
if (state == HUNTING && target != -1) {
    PathFinder.buildDistanceMap(target, passable);
    if (PathFinder.distance[pos] > 2 * Dungeon.level.distance(pos, target)) {
        for (int i = 0; i < Dungeon.level.length(); i++) {
            passable[i] = passable[i] || Dungeon.level.map[i] == Terrain.MINE_CRYSTAL;
        }
    }
}
return passable;
```

### beckon
**签名**: `public void beckon(int cell)`
**功能**: 响应召唤（睡眠时不响应）
**参数**:
- cell: int - 召唤位置
**实现逻辑**:
```java
// 第243-249行：睡眠时不响应召唤
if (state == SLEEPING) {
    // 什么都不做
} else {
    super.beckon(cell);
}
```

### storeInBundle / restoreFromBundle
**功能**: 保存/恢复状态（包括精灵类和恢复状态）
**实现逻辑**: 标准的 Bundle 序列化

## 内部类详解

### Sleeping
**类型**: protected class extends Mob.Sleeping
**功能**: 自定义睡眠状态，不会因无法到达的敌人而醒来
**实现逻辑**:
```java
// 第251-264行：睡眠逻辑
@Override
protected void awaken(boolean enemyInFOV) {
    if (enemyInFOV) {
        // 如果无法到达敌人，不醒来
        PathFinder.buildDistanceMap(enemy.pos, Dungeon.level.passable);
        if (PathFinder.distance[pos] == Integer.MAX_VALUE) {
            return;
        }
    }
    super.awaken(enemyInFOV);
}
```

## 11. 使用示例
```java
// 在水晶矿区生成水晶守卫
CrystalGuardian guardian = new CrystalGuardian();
guardian.pos = position;
Dungeon.level.mobs.add(guardian);

// 守卫生命值归零时进入恢复状态
// 需要击破水晶尖塔才能真正击杀
// 追击时可穿越水晶地形
```

## 注意事项
1. MINIBOSS 属性表示这是小Boss级别怪物
2. 恢复状态下无法被击杀，生命值最低为1
3. 攻击玩家时会扣减任务分数（除非附近有受损的尖塔）
4. 狭窄空间移动速度大幅降低

## 最佳实践
1. 先击破水晶尖塔，才能真正击杀守卫
2. 利用狭窄空间减速守卫进行风筝
3. 恢复状态时可以安全回复或逃跑
4. 不要在守卫附近与玩家战斗，会扣分