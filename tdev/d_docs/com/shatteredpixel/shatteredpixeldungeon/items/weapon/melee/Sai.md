# Sai 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/Sai.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee |
| 类类型 | public class |
| 继承关系 | extends MeleeWeapon |
| 代码行数 | 208 行 |

## 2. 类职责说明
Sai（铁钗/钗）是一种 Tier 3 的近战武器，具有极快的攻击速度（DLY=0.5f，即2倍速）。作为决斗家武器，其特殊能力「连击」会根据之前的攻击次数增加伤害。Sai是游戏中最快的武器之一，适合追求高攻击频率的玩家。

## 4. 继承与协作关系
```mermaid
classDiagram
    class MeleeWeapon {
        <<abstract>>
        +int tier
        +float DLY
        +int min(int lvl)
        +int max(int lvl)
        +duelistAbility(Hero hero, Integer target)
    }
    class Sai {
        +int tier = 3
        +float DLY = 0.5f
        +max(int lvl)
        +targetingPrompt()
        +duelistAbility(Hero hero, Integer target)
        +abilityInfo()
        +upgradeAbilityStat(int level)
        +comboStrikeAbility(...)$
    }
    class ComboStrikeTracker {
        +Buff
        +int DURATION = 5
        +int hits
        +float comboTime
        +addHit()
        +icon()
        +act()
    }
    MeleeWeapon <|-- Sai
    Sai +-- ComboStrikeTracker
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无静态常量 | - | - | - |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | 初始化块 | 物品图标，使用 ItemSpriteSheet.SAI |
| hitSound | String | 初始化块 | 击中音效，使用 Assets.Sounds.HIT_STAB |
| hitSoundPitch | float | 初始化块 | 音效音高，设为 1.3f（高音） |
| tier | int | 初始化块 | 武器等级，设为 3 |
| DLY | float | 初始化块 | 攻击延迟，设为 0.5f（2倍速） |

## 7. 方法详解

### max
**签名**: `public int max(int lvl)`
**功能**: 计算指定等级下的最大伤害
**参数**: `lvl` - 武器等级
**返回值**: 最大伤害值
**实现逻辑**:
```java
return Math.round(2.5f*(tier+1)) +     // 10基础伤害，远低于标准的20
       lvl*Math.round(0.5f*(tier+1));  // 每级+2伤害，低于标准的+4
```
Sai的伤害很低，但攻击速度是普通武器的2倍。

### targetingPrompt
**签名**: `public String targetingPrompt()`
**功能**: 返回目标选择提示文本
**参数**: 无
**返回值**: 从消息文件获取的提示字符串

### duelistAbility
**签名**: `protected void duelistAbility(Hero hero, Integer target)`
**功能**: 执行决斗家的「连击」能力
**参数**: 
- `hero` - 执行能力的英雄
- `target` - 目标位置
**返回值**: 无
**实现逻辑**:
```java
// 计算基础伤害加成：4 + 武器等级
// 约60%基础伤害加成，67%成长加成
int dmgBoost = augment.damageFactor(4 + buffedLvl());
Sai.comboStrikeAbility(hero, target, 0, dmgBoost, this);
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
**返回值**: 伤害加成字符串

### comboStrikeAbility (静态方法)
**签名**: `public static void comboStrikeAbility(Hero hero, Integer target, float multiPerHit, int boostPerHit, MeleeWeapon wep)`
**功能**: 执行连击能力的核心逻辑
**参数**: 
- `hero` - 执行能力的英雄
- `target` - 目标位置
- `multiPerHit` - 每次命中增加的伤害倍率
- `boostPerHit` - 每次命中增加的固定伤害
- `wep` - 使用的武器
**返回值**: 无
**实现逻辑**:
```java
// 验证目标...
// 获取之前的连击数
int recentHits = 0;
ComboStrikeTracker buff = hero.buff(ComboStrikeTracker.class);
if (buff != null){
    recentHits = buff.hits;
    buff.detach();
}

// 攻击时根据连击数增加伤害
boolean hit = hero.attack(enemy, 1f + multiPerHit*recentHits, boostPerHit*recentHits, Char.INFINITE_ACCURACY);
// ...
if (recentHits >= 2 && hit){
    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);  // 连击数>=2时播放强击音效
}
```

## 内部类

### ComboStrikeTracker
**类型**: public static class extends Buff
**功能**: 连击追踪器，记录最近的攻击次数
**字段**:
| 字段 | 类型 | 说明 |
|------|------|------|
| DURATION | int | 持续时间常量，值为5 |
| comboTime | float | 连击计时器 |
| hits | int | 连击次数 |

**方法**:
| 方法 | 说明 |
|------|------|
| `icon()` | 返回buff图标（仅当装备拳套类武器时显示） |
| `act()` | 每tick减少计时，归零时移除buff |
| `addHit()` | 增加连击数并重置计时器 |
| `iconFadePercent()` | 计算图标淡出百分比 |
| `iconTextDisplay()` | 显示剩余时间 |
| `storeInBundle/restoreFromBundle` | 序列化支持 |

## 11. 使用示例
```java
// 创建一对Sai
Sai sai = new Sai();
// Tier 3武器，攻击速度极快（2倍速）
// 连续攻击后使用能力造成更高伤害

hero.belongings.weapon = sai;
// 先进行几次普通攻击积累连击
// 然后使用能力打出高伤害
```

## 注意事项
- 攻击速度是普通武器的2倍（DLY=0.5f）
- 单次伤害很低，依靠攻击频率取胜
- 连击能力需要积累连击数才有效果
- 音效音高最高（1.3f），体现快速攻击

## 最佳实践
- 配合暴击装备或buff效果更佳
- 连续攻击敌人积累连击数
- 连击数达到2+时使用能力最大化伤害
- 适合对付需要快速攻击触发的机制