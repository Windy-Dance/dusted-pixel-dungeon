# Armor API 参考

## 类声明
public class Armor extends EquipableItem

## 类职责
Armor是所有护甲的基类，提供伤害减免(DR)、刻印系统、力量需求、闪避修正等核心功能。

## 关键字段
| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|-------|------|---------|-------|------|
| tier | int | public | - | 护甲等级（1-5），决定基础属性 |
| augment | Augment | public | Augment.NONE | 护甲强化类型（闪避/防御/无） |
| glyph | Glyph | public | null | 刻印效果 |
| glyphHardened | boolean | public | false | 刻印是否硬化（更难移除） |
| curseInfusionBonus | boolean | public | false | 是否有诅咒融合加成（+1额外等级） |
| masteryPotionBonus | boolean | public | false | 是否有精通药水加成（-2力量需求） |
| seal | BrokenSeal | protected | null | 战士印记（仅战士职业可用） |
| usesLeftToID | float | private | 10 | 剩余识别使用次数 |
| availableUsesToID | float | private | 5 | 可用识别使用次数 |

## 构造方法
Armor(int tier)

## Augment枚举
Augment枚举定义了护甲的两种强化方向：
- **EVASION**：增加闪避能力，减少防御力（闪避修正+2，防御修正-1）
- **DEFENSE**：增加防御能力，减少闪避（闪避修正-2，防御修正+1）  
- **NONE**：无强化（修正值均为0）

每个强化类型的修正值会根据护甲等级动态计算：
- `evasionFactor(int level)`：返回闪避修正值 = (2 + 等级) × 闪避因子
- `defenseFactor(int level)`：返回防御修正值 = (2 + 等级) × 防御因子

## 伤害减免方法
| 方法签名 | 返回值 | 说明 |
|----------|--------|------|
| DRMax() | int | 获取当前等级的最大伤害减免值 |
| DRMax(int lvl) | int | 获取指定等级的最大伤害减免值 |
| DRMin() | int | 获取当前等级的最小伤害减免值 |
| DRMin(int lvl) | int | 获取指定等级的最小伤害减免值 |

**DR计算公式**：
- 最大DR = tier × (2 + lvl) + augment.defenseFactor(lvl)
- 如果等级 > 最大DR，则额外DR = ((lvl - max) + 1) / 2
- 最小DR = 如果等级 >= 最大DR，则为(lvl - max)，否则为lvl
- 在"No Armor"挑战模式下，最大DR = 1 + tier + lvl + augment.defenseFactor(lvl)，最小DR = 0

## 修正方法
| 方法签名 | 返回值 | 说明 |
|----------|--------|------|
| evasionFactor(Char owner, float evasion) | float | 计算闪避修正后的最终闪避值 |
| speedFactor(Char owner, float speed) | float | 计算速度修正后的最终速度值 |

**修正规则**：
- **闪避修正**：考虑力量不足惩罚（每点不足力量使闪避÷1.5）、动量天赋加成、石肤刻印效果（完全禁用闪避）、强化类型修正
- **速度修正**：仅考虑力量不足惩罚（每点不足力量使速度÷1.2）

## 刻印系统
| 方法签名 | 返回值 | 说明 |
|----------|--------|------|
| inscribe(Glyph glyph) | Armor | 为护甲添加指定刻印 |
| inscribe() | Armor | 随机添加一个非诅咒刻印 |
| hasGlyph(Class<? extends Glyph> type, Char owner) | boolean | 检查是否具有指定类型刻印（考虑魔法免疫等条件） |
| hasGoodGlyph() | boolean | 检查是否具有正面刻印 |
| hasCurseGlyph() | boolean | 检查是否具有诅咒刻印 |
| glowing() | ItemSprite.Glowing | 返回刻印的发光效果 |

## Glyph内部类
### 刻印分类
- **常见刻印(common)**：Obfuscation（隐蔽）、Swiftness（迅捷）、Viscosity（粘滞）、Potential（潜能）
- **稀有刻印(uncommon)**：Brimstone（硫磺）、Stone（石肤）、Entanglement（纠缠）、Repulsion（排斥）、Camouflage（伪装）、Flow（流动）
- **罕见刻印(rare)**：Affection（魅惑）、AntiMagic（反魔法）、Thorns（荆棘）
- **诅咒刻印(curses)**：AntiEntropy（反熵）、Corrosion（腐蚀）、Displacement（位移）、Metabolism（代谢）、Multiplicity（多重）、Stench（恶臭）、Overgrowth（过度生长）、Bulk（笨重）

### 刻印方法
- **proc()方法**：处理刻印触发效果，参数为(护甲, 攻击者, 防御者, 伤害值)，返回修正后的伤害值
- **random()**：随机生成一个刻印（按稀有度概率分布）
- **randomCurse()**：随机生成一个诅咒刻印
- **curse()**：判断是否为诅咒刻印（默认返回false）
- **genericProcChanceMultiplier()**：计算刻印触发概率倍数（受奥术戒指、保护光环等影响）

### 刻印概率分布
- 常见刻印：50%总概率（每个12.5%）
- 稀有刻印：40%总概率（每个6.67%）
- 罕见刻印：10%总概率（每个3.33%）

## 战士印记系统
| 方法签名 | 返回值 | 说明 |
|----------|--------|------|
| affixSeal(BrokenSeal seal) | void | 将战士印记附着到护甲上 |
| detachSeal() | BrokenSeal | 分离战士印记并返回 |
| checkSeal() | BrokenSeal | 检查当前护甲的战士印记 |

**印记机制**：
- 仅战士职业可使用战士印记
- 附着印记会提升护甲等级+1（不触发诅咒/刻印升级逻辑）
- 印记提供额外护盾值 = tier + level()
- 分离印记时会降级护甲，并可能移除刻印

## 力量需求
| 方法签名 | 返回值 | 说明 |
|----------|--------|------|
| STRReq() | int | 获取当前等级的力量需求 |
| STRReq(int lvl) | int | 获取指定等级的力量需求 |
| STRReq(int tier, int lvl) | int | 静态方法，计算指定tier和等级的力量需求 |

**力量需求公式**：
- 基础需求 = 8 + Math.round(tier × 2)
- 等级减免 = (int)(Math.sqrt(8 × lvl + 1) - 1) / 2
- 最终需求 = 基础需求 - 等级减免
- 精通药水加成：额外-2力量需求

**力量不足惩罚**：
- 闪避：每点不足力量使闪避 ÷ 1.5^不足点数
- 速度：每点不足力量使速度 ÷ 1.2^不足点数

## 使用示例

### 示例1: 创建自定义护甲
```java
// 创建一件3级护甲
Armor customArmor = new MailArmor(); // MailArmor继承自Armor
customArmor.level(5); // 设置等级为+5
customArmor.inscribe(new Thorns()); // 添加荆棘刻印
customArmor.augment = Augment.DEFENSE; // 设置为防御强化
```

### 示例2: 添加刻印
```java
// 方法1: 直接指定刻印类型
armor.inscribe(new AntiMagic());

// 方法2: 随机添加刻印（避免重复）
armor.inscribe();

// 方法3: 添加诅咒刻印
armor.inscribe(Glyph.randomCurse());
armor.cursed = true;
```

### 示例3: 检查刻印效果
```java
// 检查是否具有特定刻印
if (armor.hasGlyph(Stone.class, hero)) {
    // 英雄无法闪避
}

// 检查是否为正面刻印
if (armor.hasGoodGlyph()) {
    // 显示正面效果
}
```

## 相关子类
- **基础护甲**：ClothArmor（布甲）、LeatherArmor（皮甲）、MailArmor（链甲）、ScaleArmor（鳞甲）、PlateArmor（板甲）
- **职业护甲**：各职业特有的护甲变种（如法师的魔法护甲、盗贼的轻便护甲等）
- **特殊护甲**：各种任务或特殊掉落的护甲

## 常见错误

### 1. 力量不足导致性能下降
**问题**：护甲力量需求高于英雄当前力量
**症状**：闪避率和移动速度大幅下降
**解决方案**：提升英雄力量属性或使用精通药水

### 2. 刻印丢失问题
**问题**：升级护甲时刻印意外消失
**原因**：未硬化刻印在+4级以上升级时有丢失概率
**解决方案**：使用硬化卷轴保护重要刻印

### 3. 诅咒护甲装备问题
**问题**：装备诅咒护甲后无法卸下
**解决方案**：使用净化卷轴或找到移除诅咒的方法

### 4. 战士印记转移失败
**问题**：战士更换护甲时印记无法转移
**原因**：新护甲被诅咒且印记包含正面刻印
**解决方案**：先净化护甲再转移印记

### 5. DR计算误解
**问题**：认为DR是固定减伤值
**实际**：DR是一个范围[min, max]，每次受到攻击时在此范围内随机取值
**注意**：高等级护甲的DR增长会逐渐放缓

### 6. 刻印触发条件忽略
**问题**：刻印效果不触发
**原因**：目标处于魔法免疫状态，或圣盾技能覆盖了原有刻印
**检查点**：确认目标没有MagicImmune buff，且不是圣骑士子职业的特殊情况