# Wand API 参考

## 类声明
public abstract class Wand extends Item

## 类职责
Wand是所有法杖的抽象基类，提供充能系统、Zap攻击、法师权杖融合等核心功能。所有具体的法杖类都必须继承此类并实现抽象方法。

## 关键字段
| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|-------|------|---------|-------|------|
| maxCharges | int | public | initialCharges() | 法杖的最大充能数量，随等级提升而增加（最高10） |
| curCharges | int | public | maxCharges | 当前充能数量 |
| partialCharge | float | public | 0f | 部分充能值，用于累积充能进度 |
| charger | Charger | protected | null | 充能器对象，负责处理充能逻辑 |
| curChargeKnown | boolean | public | false | 是否知道当前充能数量（未鉴定的法杖显示为"?"） |
| curseInfusionBonus | boolean | public | false | 诅咒注入增益标记 |
| resinBonus | int | public | 0 | 树脂增益等级 |
| collisionProperties | int | protected | Ballistica.MAGIC_BOLT | 弹道碰撞属性 |
| usesLeftToID | float | private | 10 | 距离可鉴定剩余使用次数 |
| availableUsesToID | float | private | 5f | 可用的鉴定使用次数 |

## 标准行动常量
- `AC_ZAP = "ZAP"` - 法杖的主要使用动作

## 可重写方法
| 方法签名 | 返回值 | 默认行为 | 说明 |
|---------|--------|----------|------|
| `onZap(Ballistica attack)` | void | 抽象方法 | 处理法杖释放时的核心逻辑，必须在子类中实现 |
| `onHit(MagesStaff staff, Char attacker, Char defender, int damage)` | void | 抽象方法 | 处理法杖作为权杖附魔时的命中效果，必须在子类中实现 |
| `initialCharges()` | int | return 2 | 返回法杖的基础充能数量 |
| `chargesPerCast()` | int | return 1 | 返回每次施放消耗的充能数量 |
| `statsDesc()` | String | return Messages.get(this, "stats_desc") | 返回法杖的状态描述信息 |
| `upgradeStat1(int level)` | String | return null | 升级时显示的第一个统计信息 |
| `upgradeStat2(int level)` | String | return null | 升级时显示的第二个统计信息 |
| `upgradeStat3(int level)` | String | return null | 升级时显示的第三个统计信息 |
| `collisionProperties(int target)` | int | cursed ? Ballistica.MAGIC_BOLT : collisionProperties | 获取弹道碰撞属性，诅咒法杖总是使用MAGIC_BOLT |
| `staffFx(MagesStaff.StaffParticle particle)` | void | 设置粒子效果为白色 | 权杖特效，可在子类中重写以自定义视觉效果 |
| `fx(Ballistica bolt, Callback callback)` | void | 使用MagicMissile特效和ZAP音效 | 法杖释放时的视觉和音效 |

## 充能系统

### 充能机制
法杖的充能系统基于以下核心机制：

1. **基础充能**: 每个法杖初始有2点充能（通过`initialCharges()`方法）
2. **等级增长**: 充能上限随法杖等级提升而增加，公式为：`maxCharges = min(initialCharges() + level(), 10)`
3. **充能恢复**: 通过`Charger`内部类处理自动充能，每回合根据当前缺失的充能数量计算充能速度
4. **部分充能**: 使用`partialCharge`浮点数跟踪充能进度，当达到1.0时转换为完整充能

### 充能速度计算
充能速度基于以下公式：
```
turnsToCharge = BASE_CHARGE_DELAY + (SCALING_CHARGE_ADDITION * Math.pow(scalingFactor, missingCharges))
```
其中：
- `BASE_CHARGE_DELAY = 10f`
- `SCALING_CHARGE_ADDITION = 40f`
- `scalingFactor = 0.875f`（正常情况）

### 充能加速因素
- **能量戒指**: 通过`RingOfEnergy.wandChargeMultiplier(target)`提供充能倍率
- **充能状态**: `Recharging` buff提供额外的充能加成（每层0.25f）
- **魔法护符**: 在特定容器（如MagicalHolster）中可获得充能加速

### 特殊充能机制
- **过充**: `gainCharge(float amt, boolean overcharge)`支持过充模式，允许临时超过最大充能
- **立即充能**: `gainCharge(float amt)`直接增加充能，绕过正常充能时间
- **权杖充能**: 当法杖插入法师权杖时，仍会正常充能

## 目标选择

### 弹道计算
法杖使用`Ballistica`类进行弹道计算：

1. **正常情况**: 使用`collisionProperties`字段指定的碰撞属性
2. **诅咒情况**: 无论`collisionProperties`如何设置，诅咒法杖总是使用`Ballistica.MAGIC_BOLT`
3. **目标选择**: 玩家选择目标格子后，系统计算实际碰撞位置

### 弹道属性常量
- `Ballistica.MAGIC_BOLT`: 魔法飞弹，直线飞行直到碰到障碍物或敌人
- 其他可能的弹道类型取决于具体法杖实现

### 目标验证
- **自我目标**: 通常不允许对自己使用（除非特定天赋如`SHIELD_BATTERY`）
- **有效目标**: 必须是有效的地图位置，且不是玩家当前位置
- **可见性**: 目标必须在玩家视野范围内

## 使用示例

### 示例1: 创建简单法杖
```java
public class WandOfSimpleDamage extends Wand {
    
    @Override
    protected void onZap(Ballistica attack) {
        // 获取目标角色
        Char ch = Actor.findChar(attack.collisionPos);
        
        if (ch != null) {
            // 计算伤害（基于法杖等级）
            int damage = buffedLvl() + 4;
            
            // 应用伤害
            ch.damage(damage, this);
            
            // 处理击退或其他效果
            if (ch.isAlive()) {
                ch.sprite.flash();
            }
        }
        
        // 处理区域效果（如爆炸）
        CellEmitter.center(attack.collisionPos).burst(Speck.factory(Speck.BURN), 12);
    }
    
    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        // 权杖命中效果，例如附加燃烧
        Buff.affect(defender, Burning.class).reignite(defender);
    }
    
    @Override
    public String statsDesc() {
        return Messages.get(this, "desc", buffedLvl() + 4);
    }
}
```

### 示例2: 创建有特殊充能消耗的法杖
```java
public class WandOfPowerfulSpell extends Wand {
    
    @Override
    protected int chargesPerCast() {
        // 高级法术消耗更多充能
        return 2;
    }
    
    @Override
    public int initialCharges() {
        // 基础充能更多以平衡高消耗
        return 4;
    }
    
    @Override
    protected void onZap(Ballistica attack) {
        // 强力法术效果
        for (int cell : attack.subPath(1, attack.dist)) {
            CellEmitter.get(cell).burst(Speck.factory(Speck.BLAST), 4);
        }
        
        Char ch = Actor.findChar(attack.collisionPos);
        if (ch != null) {
            int damage = buffedLvl() * 2 + 6;
            ch.damage(damage, this);
            Buff.prolong(ch, Paralysis.class, 2f + buffedLvl());
        }
    }
}
```

## 相关子类
以下是Shattered Pixel Dungeon中的所有Wand子类：

- **DamageWand**: 伤害型法杖的抽象基类
- **CursedWand**: 诅咒法杖的特殊处理类
- **WandOfBlastWave**: 冲击波法杖
- **WandOfCorrosion**: 腐蚀法杖  
- **WandOfCorruption**: 腐化法杖
- **WandOfDisintegration**: 解离法杖
- **WandOfFireblast**: 火焰爆发法杖
- **WandOfFrost**: 冰霜法杖
- **WandOfLightning**: 闪电法杖
- **WandOfLivingEarth**: 活体大地法杖
- **WandOfMagicMissile**: 魔法飞弹法杖
- **WandOfPrismaticLight**: 棱镜光法杖
- **WandOfRegrowth**: 再生法杖
- **WandOfTransfusion**: 输血法杖
- **WandOfWarding**: 守护法杖

## 常见错误

### 1. 忘记调用父类方法
在重写`upgrade()`、`degrade()`等方法时，必须调用`super.upgrade()`以确保充能系统正常工作。

**错误示例**:
```java
@Override
public Item upgrade() {
    // 忘记调用super.upgrade()
    maxCharges++; // 手动修改可能导致同步问题
    return this;
}
```

**正确示例**:
```java
@Override
public Item upgrade() {
    super.upgrade(); // 确保父类逻辑执行
    // 自定义升级逻辑
    return this;
}
```

### 2. 不正确的充能管理
直接修改`curCharges`而不调用`updateQuickslot()`会导致UI不同步。

**错误示例**:
```java
curCharges = 0; // UI不会更新
```

**正确示例**:
```java
curCharges = 0;
updateQuickslot(); // 更新快速栏显示
```

### 3. 忽略诅咒处理
忘记处理诅咒法杖的特殊情况，导致弹道计算错误。

**错误示例**:
```java
// 在onZap中直接使用自己的collisionProperties
Ballistica custom = new Ballistica(user.pos, target, myCustomProperty);
```

**正确示例**:
```java
// 使用Wand提供的collisionProperties方法
Ballistica proper = new Ballistica(user.pos, target, collisionProperties(target));
```

### 4. 错误的异步处理
在`onZap`中忘记处理动画回调，导致游戏逻辑不同步。

**正确做法**:
```java
@Override
protected void onZap(Ballistica attack) {
    // 所有实际效果必须在fx回调中执行
    // 因为此时动画已经完成
}
```

### 5. 忘记调用wandUsed()
每次使用法杖后必须调用`wandUsed()`来处理充能消耗、鉴定进度和天赋效果。

**错误示例**:
```java
@Override
protected void onZap(Ballistica attack) {
    // 执行法术效果但忘记wandUsed()
}
```

**正确示例**:
```java
// wandUsed()已经在基础框架中自动调用
// 开发者只需在onZap中处理法术逻辑即可
```

### 6. 不正确的等级处理
直接使用`level()`而不是`buffedLvl()`获取实际生效等级。

**推荐做法**:
```java
// 使用buffedLvl()获取考虑了所有增益后的实际等级
int effectiveLevel = buffedLvl();
```