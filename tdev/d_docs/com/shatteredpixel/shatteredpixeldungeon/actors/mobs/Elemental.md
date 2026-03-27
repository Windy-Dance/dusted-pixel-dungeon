# Elemental 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/Elemental.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.actors.mobs |
| 类类型 | public abstract class |
| 继承关系 | extends Mob |
| 代码行数 | 615行 |

## 2. 类职责说明
Elemental是元素生物的抽象基类，定义了远程攻击、近战攻击和特殊状态处理的通用机制。它有四种具体实现：FireElemental（火焰元素）、FrostElemental（冰霜元素）、ShockElemental（闪电元素）和ChaosElemental（混沌元素）。Elemental具有飞行能力和独特的伤害机制，根据是否被召唤而有不同的属性。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Mob {
        <<abstract>>
        +int HP
        +int HT
        +int defenseSkill
        +int EXP
        +int maxLvl
        +Alignment alignment
        +AiState state
    }
    
    class Elemental {
        <<abstract>>
        +int HP = 60
        +int HT = 60
        +int defenseSkill = 20
        +int EXP = 10
        +int maxLvl = 20
        +boolean flying = true
        +boolean summonedALly
        +int rangedCooldown
        +ArrayList~Class~Buff~~ harmfulBuffs
        +int damageRoll()
        +int attackSkill(Char)
        +void setSummonedALly()
        +int drRoll()
        +boolean act()
        +boolean canAttack(Char)
        +boolean doAttack(Char)
        +int attackProc(Char, int)
        +void zap()
        +void onZapComplete()
        +boolean add(Buff)
        +{abstract} void meleeProc(Char, int)
        +{abstract} void rangedProc(Char)
    }
    
    class FireElemental {
        +Property FIERY
        +PotionOfLiquidFlame loot
    }
    
    class FrostElemental {
        +Property ICY
        +PotionOfFrost loot
    }
    
    class ShockElemental {
        +Property ELECTRIC
        +ScrollOfRecharging loot
    }
    
    class ChaosElemental {
        +ScrollOfTransmutation loot
    }
    
    Mob <|-- Elemental
    Elemental <|-- FireElemental
    Elemental <|-- FrostElemental
    Elemental <|-- ShockElemental
    Elemental <|-- ChaosElemental
    
    note for Elemental "元素生物基类\n支持远程和近战攻击\n根据召唤状态变化属性"
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| HP/HT | int | 60 | 生命值上限（基础状态） |
| defenseSkill | int | 20 | 防御技能等级（基础状态） |
| EXP | int | 10 | 击败后获得的经验值 |
| maxLvl | int | 20 | 最大生成等级 |
| flying | boolean | true | 飞行能力 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| summonedALly | boolean | protected | 是否作为盟友被召唤 |
| rangedCooldown | int | protected | 远程攻击冷却时间 |
| harmfulBuffs | ArrayList<Class<? extends Buff>> | protected | 有害Buff列表 |

## 7. 方法详解

### damageRoll()
**签名**: `int damageRoll()`
**功能**: 计算伤害范围，根据召唤状态变化
**参数**: 无
**返回值**: int - 伤害值
**实现逻辑**:
- 未被召唤：20-25点伤害（第84行）
- 被召唤：随地下城深度缩放的伤害（第87行）

### attackSkill(Char target)
**签名**: `int attackSkill(Char target)`
**功能**: 计算攻击技能等级，根据召唤状态变化
**参数**:
- target: Char - 目标
**返回值**: int - 攻击技能等级
**实现逻辑**:
- 未被召唤：25点攻击技能（第94行）
- 被召唤：随地下城深度缩放的攻击技能（第97行）

### setSummonedALly()
**签名**: `void setSummonedALly()`
**功能**: 设置为被召唤状态，调整属性
**参数**: 无
**返回值**: void
**实现逻辑**:
1. 标记summonedALly为true（第102行）
2. 根据地下城深度调整防御技能和生命值（第104-106行）

### drRoll()
**签名**: `int drRoll()`
**功能**: 计算伤害减免值
**参数**: 无
**返回值**: int - 伤害减免值
**实现逻辑**:
- 在基础伤害减免基础上增加0-5点（第111行）

### act()
**签名**: `protected boolean act()`
**功能**: 行动逻辑，处理远程冷却
**参数**: 无
**返回值**: boolean - 是否完成行动
**实现逻辑**:
- 狩猎状态下远程冷却时间递减（第119行）
- 调用父类act方法（第122行）

### canAttack(Char enemy)
**签名**: `protected boolean canAttack(Char enemy)`
**功能**: 检查是否能攻击目标
**参数**:
- enemy: Char - 敌人
**返回值**: boolean - 是否能攻击
**实现逻辑**:
- 近战或远程冷却结束且能直线攻击时返回true（第133-137行）

### doAttack(Char enemy)
**签名**: `protected boolean doAttack(Char enemy)`
**功能**: 执行攻击，根据条件选择近战或远程
**参数**:
- enemy: Char - 被攻击的敌人
**返回值**: boolean - 是否完成攻击
**实现逻辑**:
1. 满足近战条件或远程不可用时执行近战（第142-147行）
2. 否则执行远程攻击，显示zap动画（第149-156行）

### attackProc(Char enemy, int damage)
**签名**: `int attackProc(Char enemy, int damage)`
**功能**: 攻击处理，调用近战特效
**参数**:
- enemy: Char - 被攻击的敌人
- damage: int - 造成的伤害值
**返回值**: int - 处理后的伤害值
**实现逻辑**:
1. 调用父类attackProc方法（第162行）
2. 调用meleeProc处理近战特效（第163行）

### zap()
**签名**: `protected void zap()`
**功能**: 执行远程攻击的核心逻辑
**参数**: 无
**返回值**: void
**实现逻辑**:
1. 花费1回合时间（第169行）
2. 解除隐身状态（第171行）
3. 检查命中并调用rangedProc（第173-175行）
4. 重置远程冷却时间（第181行）

### add(Buff buff)
**签名**: `boolean add(Buff buff)`
**功能**: 添加Buff，对有害Buff造成自伤
**参数**:
- buff: Buff - 要添加的Buff
**返回值**: boolean - 是否成功添加
**实现逻辑**:
1. 如果Buff在有害列表中，造成自伤并拒绝添加（第191-193行）
2. 否则调用父类add方法（第195行）

### meleeProc(Char enemy, int damage)
**签名**: `protected abstract void meleeProc(Char enemy, int damage)`
**功能**: 抽象方法，由子类实现近战特效
**参数**:
- enemy: Char - 被攻击的敌人
- damage: int - 造成的伤害值
**返回值**: void

### rangedProc(Char enemy)
**签名**: `protected abstract void rangedProc(Char enemy)`
**功能**: 抽象方法，由子类实现远程特效
**参数**:
- enemy: Char - 被攻击的敌人
**返回值**: void

## 具体实现类

### FireElemental
- **属性**: FIERY（火焰属性）
- **掉落**: 火焰药水（PotionOfLiquidFlame，1/8概率）
- **有害Buff**: 冰冻、寒冷
- **近战特效**: 50%概率施加燃烧效果
- **远程特效**: 施加4回合燃烧效果

### FrostElemental  
- **属性**: ICY（冰霜属性）
- **掉落**: 冰霜药水（PotionOfFrost，1/8概率）
- **有害Buff**: 燃烧
- **近战特效**: 33%概率或水上战斗时冻结地面
- **远程特效**: 冻结地面

### ShockElemental
- **属性**: ELECTRIC（电击属性）
- **掉落**: 充能卷轴（ScrollOfRecharging，1/4概率）
- **有害Buff**: 无特殊有害Buff
- **近战特效**: 释放闪电链，对周围敌人造成伤害
- **远程特效**: 50%失明效果

### ChaosElemental
- **属性**: 无特殊属性
- **掉落**: 变异卷轴（ScrollOfTransmutation，100%概率）
- **有害Buff**: 无特殊有害Buff
- **近战特效**: 随机诅咒法杖效果
- **远程特效**: 诅咒法杖的远程效果

## 特殊子类

### NewbornFireElemental
- **用途**: 法杖制作任务的迷你Boss
- **特点**: 更低的属性，独特的蓄力火球攻击
- **特殊行为**: 死亡时掉落灰烬（Embers）并触发音乐切换

### AllyNewBornElemental
- **用途**: 玩家召唤的盟友版本
- **特点**: 无远程攻击能力，非迷你Boss

## 战斗行为
- **飞行能力**: 可以跨越地形障碍
- **远程攻击**: 冷却3-5回合，可直线攻击
- **双模式**: 近战和远程攻击各有不同特效
- **属性抗性**: 对特定有害Buff会产生自伤反应
- **召唤变化**: 被召唤时属性会根据地下城深度调整

## 掉落物品
- **FireElemental**: 火焰药水（12.5%）
- **FrostElemental**: 冰霜药水（12.5%）
- **ShockElemental**: 充能卷轴（25%）
- **ChaosElemental**: 变异卷轴（100%）
- **NewbornFireElemental**: 灰烬（任务物品）

## 特殊属性
- **Flying**: 飞行能力
- **元素属性**: FIERY/ICY/ELECTRIC（根据类型）
- **MINIBOSS**: NewbornFireElemental具有迷你Boss标记

## 11. 使用示例
```java
// 创建随机元素生物
Class<? extends Elemental> elementalType = Elemental.random();
Elemental elemental = (Elemental) Reflection.newInstance(elementalType);

// 近战特效实现（以FireElemental为例）
@Override
protected void meleeProc(Char enemy, int damage) {
    if (Random.Int(2) == 0 && !Dungeon.level.water[enemy.pos]) {
        Buff.affect(enemy, Burning.class).reignite(enemy);
        if (enemy.sprite.visible) Splash.at(enemy.sprite.center(), sprite.blood(), 5);
    }
}

// 远程攻击流程
@Override
protected boolean doAttack(Char enemy) {
    if (canUseRanged()) {
        if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
            sprite.zap(enemy.pos); // 显示动画
            return false;
        } else {
            zap(); // 直接执行
            return true;
        }
    } else {
        return super.doAttack(enemy); // 近战
    }
}
```

## 注意事项
1. Elemental是抽象类，不能直接实例化
2. 有害Buff会导致自伤而非被施加效果
3. NewbornFireElemental有特殊的蓄力攻击机制
4. ChaosElemental的远程攻击会跳过命中检查
5. 被召唤的Elemental属性会随地下城深度动态调整

## 最佳实践
1. 玩家应根据元素类型准备相应的抗性装备
2. 利用有害Buff机制来对抗特定元素生物
3. 远程攻击时注意保持安全距离
4. 在设计关卡时，可根据区域主题选择合适的元素类型
5. 考虑与其他元素相关机制配合，形成完整的元素生态系统