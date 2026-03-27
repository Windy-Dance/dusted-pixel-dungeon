# Weapon API 参考

## 类声明
public abstract class Weapon extends KindOfWeapon

## 类职责
Weapon是所有武器的抽象基类，提供攻击修正(ACC)、攻击延迟(DLY)、攻击范围(RCH)、附魔系统、力量需求等核心功能。

## 关键字段
| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|-------|------|---------|-------|------|
| ACC | float | public | 1.0f | 攻击准确度修正系数 |
| DLY | float | public | 1.0f | 攻击延迟修正系数 |
| RCH | int | public | 1 | 攻击范围修正值（仅对近战生效） |
| augment | Augment | public | Augment.NONE | 武器强化类型 |
| enchantment | Enchantment | public | null | 当前附魔效果 |
| enchantHardened | boolean | public | false | 附魔是否硬化 |
| curseInfusionBonus | boolean | public | false | 诅咒注入增益 |
| masteryPotionBonus | boolean | public | false | 精通药水增益 |
| usesLeftToID | float | protected | usesToID() | 剩余使用次数直到识别 |
| availableUsesToID | float | protected | usesToID()/2f | 可用于识别的可用使用次数 |

## Augment枚举
- **SPEED** (0.7f, 2/3f): 速度强化，伤害×0.7，延迟×0.67（约+50%攻击速度）
- **DAMAGE** (1.5f, 5/3f): 伤害强化，伤害×1.5，延迟×1.67（约-40%攻击速度）  
- **NONE** (1.0f, 1f): 无强化，保持原始数值

每个Augment提供damageFactor()和delayFactor()方法来计算实际战斗属性。

## 核心方法 - 战斗属性
| 方法签名 | 返回值 | 说明 |
|----------|--------|------|
| accuracyFactor(Char owner, Char target) | float | 计算准确度修正，考虑力量不足惩罚和Wayward附魔效果 |
| delayFactor(Char owner) | float | 计算攻击延迟，结合基础延迟、强化类型、力量不足惩罚 |
| reachFactor(Char owner) | int | 计算攻击范围，考虑Projecting附魔等效果 |
| STRReq() | int | 获取当前等级的力量需求 |
| proc(Char attacker, Char defender, int damage) | int | 处理攻击事件，触发附魔效果并处理识别进度 |

## 附魔系统
| 方法签名 | 返回值 | 说明 |
|----------|--------|------|
| enchant(Enchantment ench) | Weapon | 设置指定附魔 |
| hasEnchant(Class<? extends Enchantment> type, Char owner) | boolean | 检查是否具有指定附魔（考虑MagicImmune等条件） |
| hasGoodEnchant() | boolean | 检查是否有良性附魔 |
| hasCurseEnchant() | boolean | 检查是否有诅咒附魔 |
| glowing() | ItemSprite.Glowing | 返回武器发光效果 |

## Enchantment内部类
- **常见附魔(common)**: Blazing, Chilling, Kinetic, Shocking（各12.5%概率）
- **稀有附魔(uncommon)**: Blocking, Blooming, Elastic, Lucky, Projecting, Unstable（各6.67%概率）
- **罕见附魔(rare)**: Corrupting, Grim, Vampiric（各3.33%概率）
- **诅咒附魔(curses)**: Annoying, Displacing, Dazzling, Explosive, Sacrificial, Wayward, Polarized, Friendly

### 核心方法
- **proc()**: 抽象方法，处理附魔触发逻辑
- **random()**: 随机选择任何类型附魔
- **randomCurse()**: 随机选择诅咒附魔

## 识别系统
武器通过使用获得识别进度：
- **基础使用次数**: usesToID()默认返回20次
- **进度计算**: 每次攻击消耗Talent.itemIDSpeedFactor()计算的使用次数
- **触发条件**: usesLeftToID <= 0时自动识别或设置为可识别状态
- **经验获取**: onHeroGainExp()在英雄获得经验时推进识别进度

## 使用示例
### 示例1: 创建自定义武器
```java
public class CustomSword extends MeleeWeapon {
    {
        ACC = 1.2f;  // +20%准确度
        DLY = 0.9f;  // +11%攻击速度
        RCH = 1;     // 标准范围
    }
    
    @Override
    public int STRReq(int lvl) {
        return STRReq(3, lvl); // tier 3武器
    }
}
```

### 示例2: 添加附魔
```java
// 创建武器并添加随机附魔
Weapon weapon = new CustomSword();
weapon.random(); // 自动30%概率诅咒，10%概率良性附魔

// 手动设置特定附魔
weapon.enchant(new Blazing());
```

## 相关子类
- **MeleeWeapon**: 近战武器基类
- **MissileWeapon**: 远程武器基类  
- **SpiritBow**: 灵魂之弓（特殊远程武器）

## 常见错误
1. **忘记实现STRReq抽象方法**: 所有Weapon子类必须实现`STRReq(int lvl)`方法
2. **附魔触发条件忽略**: 使用`hasEnchant()`而非直接比较`enchantment`字段
3. **识别系统误解**: 武器识别需要累计使用次数，不是单次攻击就能识别
4. **强化类型混淆**: Augment.SPEED实际上是降低伤害提升速度
5. **力量计算错误**: 力量需求公式涉及平方根计算，不能简单线性估算