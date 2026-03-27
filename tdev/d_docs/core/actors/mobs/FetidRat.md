# FetidRat 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/FetidRat.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.actors.mobs |
| 类类型 | public class |
| 继承关系 | extends Rat |
| 代码行数 | 111 行 |

## 2. 类职责说明
FetidRat（腐臭老鼠）是 Rat（老鼠）的小Boss变种，攻击时施加 Ooze（粘液）效果，被攻击时释放臭气云。是幽灵任务的第一个Boss，倾向于游荡到玩家附近。免疫臭气效果。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Mob {
        +spriteClass Class
        +HP int
        +HT int
        +defenseSkill int
        +EXP int
        +state State
        +properties Set~Property~
        +attackSkill() int
        +drRoll() int
        +attackProc() int
        +defenseProc() int
        +die() void
        +immunities Set~Class~
    }
    
    class Rat {
        +baseSpeed float
    }
    
    class FetidRat {
        +spriteClass FetidRatSprite
        +HP 20, HT 20
        +defenseSkill 5
        +EXP 4
        +state WANDERING
        +properties MINIBOSS, DEMONIC
        +WANDERING Wandering
        +attackSkill() int
        +drRoll() int
        +attackProc() int
        +defenseProc() int
        +die() void
        +immunities StenchGas
    }
    
    Mob <|-- Rat
    Rat <|-- FetidRat
    FetidRat +-- Wandering
```

## 静态常量表
无静态常量。

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| spriteClass | Class | 初始化块 | 精灵类为 FetidRatSprite |
| HP | int | 初始化块 | 当前生命值 20 |
| HT | int | 初始化块 | 最大生命值 20 |
| defenseSkill | int | 初始化块 | 防御技能 5 |
| EXP | int | 初始化块 | 经验值 4 |
| state | State | 初始化块 | 初始状态为 WANDERING |
| properties | Set\<Property\> | 初始化块 | MINIBOSS, DEMONIC |
| WANDERING | Wandering | 初始化块 | 自定义游荡状态 |
| immunities | Set\<Class\> | 初始化块 | 免疫 StenchGas |

## 7. 方法详解

### attackSkill
**签名**: `public int attackSkill(Char target)`
**功能**: 获取攻击技能值
**参数**:
- target: Char - 攻击目标
**返回值**: int - 攻击技能值（12）
**实现逻辑**:
```java
// 第55-57行：返回攻击技能
return 12;
```

### drRoll
**签名**: `public int drRoll()`
**功能**: 计算伤害减免值
**返回值**: int - 随机伤害减免值（0-2）
**实现逻辑**:
```java
// 第60-62行：计算伤害减免
return super.drRoll() + Random.NormalIntRange(0, 2);
```

### attackProc
**签名**: `public int attackProc(Char enemy, int damage)`
**功能**: 攻击时施加粘液效果并扣减任务分数
**参数**:
- enemy: Char - 被攻击的目标
- damage: int - 基础伤害值
**返回值**: int - 最终伤害值
**实现逻辑**:
```java
// 第65-76行：攻击效果
damage = super.attackProc(enemy, damage);
if (Random.Int(3) == 0) {                              // 33%概率
    Buff.affect(enemy, Ooze.class).set(Ooze.DURATION); // 施加粘液效果
    // 如果是玩家且不在水中，扣减任务分数
    if (enemy == Dungeon.hero && !Dungeon.level.water[enemy.pos]) {
        Statistics.questScores[0] -= 50;
    }
}
return damage;
```

### defenseProc
**签名**: `public int defenseProc(Char enemy, int damage)`
**功能**: 被攻击时释放臭气云
**参数**:
- enemy: Char - 攻击者
- damage: int - 受到的伤害
**返回值**: int - 最终伤害值
**实现逻辑**:
```java
// 第79-84行：防御时释放臭气
GameScene.add(Blob.seed(pos, 20, StenchGas.class));  // 在位置释放臭气云
return super.defenseProc(enemy, damage);
```

### die
**签名**: `public void die(Object cause)`
**功能**: 死亡时触发幽灵任务进度
**参数**:
- cause: Object - 死亡原因
**实现逻辑**:
```java
// 第87-91行：死亡处理
super.die(cause);
Ghost.Quest.process();  // 更新幽灵任务进度
```

## 内部类详解

### Wandering
**类型**: protected class extends Mob.Wandering
**功能**: 自定义游荡状态，倾向于接近玩家
**实现逻辑**:
```java
// 第93-106行：游荡逻辑
@Override
protected int randomDestination() {
    // 在两个潜在游荡位置中选择离玩家更近的
    int pos1 = super.randomDestination();
    int pos2 = super.randomDestination();
    PathFinder.buildDistanceMap(Dungeon.hero.pos, Dungeon.level.passable);
    if (PathFinder.distance[pos2] < PathFinder.distance[pos1]) {
        return pos2;
    } else {
        return pos1;
    }
}
```

## 11. 使用示例
```java
// 创建腐臭老鼠（幽灵任务Boss）
FetidRat rat = new FetidRat();
rat.pos = position;
Dungeon.level.mobs.add(rat);

// 攻击时可能施加粘液
// 被攻击时释放臭气
// 击杀后推进幽灵任务
```

## 注意事项
1. 是幽灵任务的第一个Boss
2. 攻击命中时扣减任务分数（不在水中时）
3. 被攻击会释放臭气云，注意保持距离
4. 免疫臭气效果
5. 倾向于游荡到玩家附近

## 最佳实践
1. 远程攻击可以避免臭气
2. 站在水中可以避免任务分数扣减
3. 准备好清洁物品应对粘液效果
4. 利用臭气免疫特性不会被自己的臭气影响
5. 是早期获取幽灵奖励的关键怪物