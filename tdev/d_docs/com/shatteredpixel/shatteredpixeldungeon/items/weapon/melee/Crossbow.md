# Crossbow 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/Crossbow.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee |
| 类类型 | public class |
| 继承关系 | extends MeleeWeapon |
| 代码行数 | 183 行 |

## 2. 类职责说明
Crossbow（十字弩）是一种 Tier 4 的特殊近战武器，当装备飞镖时会增强飞镖的伤害。作为决斗家武器，其特殊能力「蓄力射击」可以让下一次攻击必定命中并将敌人击退。十字弩是独特的远程/近战混合武器。

## 4. 继承与协作关系
```mermaid
classDiagram
    class MeleeWeapon {
        <<abstract>>
        +int tier
        +int min(int lvl)
        +int max(int lvl)
        +duelistAbility(Hero hero, Integer target)
    }
    class Crossbow {
        +int tier = 4
        +doUnequip(Hero hero, boolean collect, boolean single)
        +accuracyFactor(Char owner, Char target)
        +proc(Char attacker, Char defender, int damage)
        +max(int lvl)
        +dartMin()
        +dartMin(int lvl)
        +dartMax()
        +dartMax(int lvl)
        +statsInfo()
        +duelistAbility(Hero hero, Integer target)
        +abilityInfo()
        +upgradeAbilityStat(int level)
    }
    class ChargedShot {
        +Buff
        +icon()
    }
    MeleeWeapon <|-- Crossbow
    Crossbow +-- ChargedShot
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无静态常量 | - | - | - |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | 初始化块 | 物品图标，使用 ItemSpriteSheet.CROSSBOW |
| hitSound | String | 初始化块 | 击中音效，使用 Assets.Sounds.HIT |
| hitSoundPitch | float | 初始化块 | 音效音高，设为 1f（正常） |
| tier | int | 初始化块 | 武器等级，设为 4 |

## 7. 方法详解

### doUnequip
**签名**: `public boolean doUnequip(Hero hero, boolean collect, boolean single)`
**功能**: 卸下武器时的处理
**参数**: 
- `hero` - 英雄
- `collect` - 是否收集到背包
- `single` - 是否单独卸下
**返回值**: 是否成功卸下
**实现逻辑**:
```java
if (super.doUnequip(hero, collect, single)){
    if (hero.buff(ChargedShot.class) != null &&
            !(hero.belongings.weapon() instanceof Crossbow)
            && !(hero.belongings.secondWep() instanceof Crossbow)){
        // 如果没有其他十字弩装备，清除蓄力射击状态
        hero.buff(ChargedShot.class).detach();
    }
    return true;
}
return false;
```

### accuracyFactor
**签名**: `public float accuracyFactor(Char owner, Char target)`
**功能**: 计算准确度因子
**参数**: 
- `owner` - 攻击者
- `target` - 目标
**返回值**: 准确度倍率
**实现逻辑**:
```java
if (owner.buff(Crossbow.ChargedShot.class) != null){
    // 设置延迟任务处理击杀
    Actor.add(new Actor() { ... });
    return Float.POSITIVE_INFINITY;  // 蓄力射击必定命中
} else {
    return super.accuracyFactor(owner, target);
}
```

### proc
**签名**: `public int proc(Char attacker, Char defender, int damage)`
**功能**: 处理攻击时的特殊效果
**参数**: 
- `attacker` - 攻击者
- `defender` - 防御者
- `damage` - 原始伤害
**返回值**: 处理后的伤害
**实现逻辑**:
```java
int dmg = super.proc(attacker, defender, damage);

// 蓄力射击效果
if (attacker == Dungeon.hero
        && Dungeon.hero.buff(ChargedShot.class) != null
        && Dungeon.hero.belongings.attackingWeapon() == this){
    // 计算击退轨迹
    Ballistica trajectory = new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_TARGET);
    trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
    // 击退敌人4格
    WandOfBlastWave.throwChar(defender, trajectory, 4, true, true, this);
    attacker.buff(Crossbow.ChargedShot.class).detach();
}
return dmg;
```
蓄力射击会将敌人击退4格。

### max
**签名**: `public int max(int lvl)`
**功能**: 计算近战最大伤害
**参数**: `lvl` - 武器等级
**返回值**: 最大伤害值
**实现逻辑**:
```java
return 4*(tier+1) +    // 20基础伤害
       lvl*(tier);     // 每级+4伤害
```

### dartMin / dartMax
**签名**: `public int dartMin()` / `public int dartMin(int lvl)` / `public int dartMax()` / `public int dartMax(int lvl)`
**功能**: 计算飞镖伤害加成
**参数**: `lvl` - 武器等级（可选）
**返回值**: 飞镖伤害值
**实现逻辑**:
```java
// dartMin
return 4 + lvl;    // 基础4 + 每级1

// dartMax
return 12 + 3*lvl; // 基础12 + 每级3
```
十字弩会增强飞镖的伤害。

### statsInfo
**签名**: `public String statsInfo()`
**功能**: 返回额外属性信息
**参数**: 无
**返回值**: 飞镖伤害描述字符串

### duelistAbility
**签名**: `protected void duelistAbility(Hero hero, Integer target)`
**功能**: 执行决斗家的「蓄力射击」能力
**参数**: 
- `hero` - 执行能力的英雄
- `target` - 目标位置（不需要）
**返回值**: 无
**实现逻辑**:
```java
if (hero.buff(ChargedShot.class) != null){
    GLog.w(Messages.get(this, "ability_cant_use"));
    return;  // 已经蓄力则无法再次使用
}

beforeAbilityUsed(hero, null);
Buff.affect(hero, ChargedShot.class);
hero.sprite.operate(hero.pos);
hero.next();
afterAbilityUsed(hero);
```

### abilityInfo
**签名**: `public String abilityInfo()`
**功能**: 返回能力描述信息
**参数**: 无
**返回值**: 能力描述字符串

### upgradeAbilityStat
**签名**: `public String upgradeAbilityStat(int level)`
**功能**: 返回指定等级下的能力统计
**参数**: `level` - 武器等级
**返回值**: 击退格数字符串

## 内部类

### ChargedShot
**类型**: public static class extends Buff
**功能**: 蓄力射击状态追踪器
**字段**:
- `announced = true` - 状态变化时公告
- `type = buffType.POSITIVE` - 正面buff

**方法**:
| 方法 | 说明 |
|------|------|
| `icon()` | 返回buff图标 BuffIndicator.DUEL_XBOW |

## 11. 使用示例
```java
// 创建一把十字弩
Crossbow crossbow = new Crossbow();
// Tier 4武器，增强飞镖伤害
// 决斗家可以使用「蓄力射击」能力

hero.belongings.weapon = crossbow;
// 装备飞镖获得伤害加成
// 使用能力蓄力，下一次攻击必定命中并击退敌人
```

## 注意事项
- 装备时会增强飞镖的伤害
- 蓄力射击必定命中（无限准确度）
- 蓄力射击会将敌人击退4格
- 卸下武器会清除蓄力状态

## 最佳实践
- 配合飞镖使用获得远程伤害加成
- 利用蓄力射击控制敌人位置
- 击退效果可以将敌人推入陷阱
- 是远程/近战混合的优秀选择