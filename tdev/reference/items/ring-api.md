# Ring API 参考

## 类声明
```java
public class Ring extends KindofMisc
```

## 类职责
Ring 类是 Shattered Pixel Dungeon 中所有戒指的基础类。它继承自 KindofMisc（杂项装备），提供了戒指特有的功能，包括：
- 基于经验值的自动识别机制
- 戒指效果 Buff 管理系统
- 宝石类型和图像管理
- 等级统计信息显示
- 装备/卸装备时的效果激活和移除
- 与 Spirit Form 天赋的特殊交互

戒指在游戏中提供被动增益效果，可以被英雄同时装备两枚（主手和副手），并且支持升级（+0 到 +2）。

## 关键字段
| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|-------|------|---------|-------|------|
| buff | Buff | protected | null | 当前激活的 Buff 实例 |
| buffClass | Class<? extends RingBuff> | protected | null | Buff 类型引用 |
| gem | String | private | "garnet" | 宝石类型（用于未知戒指显示） |
| levelsToID | float | private | 1 | 识别所需的经验等级数 |
| anonymous | boolean | protected | false | 是否为匿名戒指（用于UI显示） |

## 构造方法
```java
public Ring()
```
调用父类构造器并执行 reset() 方法初始化属性。

## 可重写方法
| 方法签名 | 返回值 | 必须重写？ | 默认行为 | 说明 |
|---------|-------|----------|---------|------|
| buff() | RingBuff | 否 | 返回 null | 创建戒指效果的 Buff 实例 |
| statsInfo() | String | 否 | 返回空字符串 | 显示等级统计信息 |
| upgradeStat1(int level) | String | 否 | 返回 null | 升级统计信息字段1 |
| upgradeStat2(int level) | String | 否 | 返回 null | 升级统计信息字段2 |
| upgradeStat3(int level) | String | 否 | 返回 null | 升级统计信息字段3 |

## 公开方法
| 方法签名 | 返回值 | 说明 |
|---------|-------|------|
| activate(Char ch) | void | 激活戒指效果，附加 Buff 到指定角色 |
| doUnequip(Hero hero, boolean collect, boolean single) | boolean | 卸装备时移除 Buff |
| isKnown() | boolean | 检查戒指类型是否已知 |
| setKnown() | void | 标记戒指类型为已知 |
| identify(boolean byHero) | Item | 识别戒指并标记为已知 |
| readyToIdentify() | boolean | 检查是否准备好识别 |
| setIDReady() | void | 设置为准备识别状态 |
| onHeroGainExp(float levelPercent, Hero hero) | void | 英雄获得经验时处理自动识别 |
| buffedLvl() | int | 获取受 EnhancedRings 天赋影响的等级 |
| getBonus(Char target, Class<?extends RingBuff> type) | int | 获取指定类型戒指的总加成 |
| getBuffedBonus(Char target, Class<?extends RingBuff> type) | int | 获取受增益影响的总加成 |
| soloBonus() | int | 获取单枚戒指的基础加成（描述使用）|
| soloBuffedBonus() | int | 获取单枚戒指的增益后加成（描述使用）|
| combinedBonus(Hero hero) | int | 获取英雄装备的同类型戒指总加成（描述使用）|
| combinedBuffedBonus(Hero hero) | int | 获取英雄装备的同类型戒指增益后总加成（描述使用）|
| anonymize() | void | 将戒指设为匿名状态（UI使用）|

## 静态方法
| 方法签名 | 返回值 | 说明 |
|---------|-------|------|
| initGems() | void | 初始化宝石类型处理器 |
| clearGems() | void | 清除宝石类型处理器 |
| save(Bundle bundle) | void | 保存到存档Bundle |
| restore(Bundle bundle) | void | 从存档Bundle恢复 |
| getKnown() | HashSet<Class<? extends Ring>> | 获取已知戒指类型集合 |
| getUnknown() | HashSet<Class<? extends Ring>> | 获取未知戒指类型集合 |
| allKnown() | boolean | 检查是否所有戒指类型都已知 |

## 内部类：RingBuff
```java
public class RingBuff extends Buff
```
戒指效果的基础 Buff 类，提供等级相关的便利方法：

| 方法签名 | 返回值 | 说明 |
|---------|-------|------|
| attachTo(Char target) | boolean | 附加到目标时的特殊处理 |
| act() | boolean | 每回合执行的标准逻辑 |
| level() | int | 返回戒指的基础等级加成 |
| buffedLvl() | int | 返回受增益影响的等级加成 |

## 生命周期
1. **创建**: 实例化具体的 Ring 子类（如 RingOfMight）
2. **初始化**: 设置宝石类型、图像、随机等级和诅咒状态
3. **拾取**: 添加到英雄背包，但效果未激活
4. **装备**: 调用 activate() 方法，创建并附加 RingBuff 到英雄
5. **生效**: RingBuff 在每回合自动激活，提供持续效果
6. **升级**: 通过法术或商店提升等级，增强效果
7. **卸装备**: 移除 RingBuff，停止效果
8. **识别**: 通过经验值积累自动识别戒指类型
9. **序列化**: 游戏保存时保存状态和识别信息

## 与其他系统的交互
- **经验值系统**: 通过 onHeroGainExp() 实现自动识别
- **Buff 系统**: RingBuff 提供持续的被动效果
- **天赋系统**: EnhancedRings 提升戒指等级，SpiritForm 提供特殊戒指效果
- **消息系统**: 获取本地化的名称和描述（未知戒指使用宝石名称）
- **目录系统**: 记录发现的戒指类型
- **成就系统**: 验证戒指等级成就
- **物品处理器**: ItemStatusHandler 管理戒指类型和识别状态
- **精灵系统**: 不同宝石类型对应不同图像

## 使用示例
### 示例1: 创建自定义戒指

```java
package com.dustedpixel.dustedpixeldungeon.items.rings;

import com.dustedpixel.dustedpixeldungeon.actors.Char;
import com.dustedpixel.dustedpixeldungeon.actors.buffs.Buff;
import com.dustedpixel.dustedpixeldungeon.messages.Messages;

public class RingOfRegeneration extends Ring {

    {
        // 设置基础属性（由父类处理）
        // 图像、宝石类型等由 ItemStatusHandler 自动设置
    }

    @Override
    protected RingBuff buff() {
        return new Regeneration();
    }

    @Override
    protected String statsInfo() {
        if (isIdentified()) {
            return Messages.get(this, "stats", soloBonus(), combinedBonus(Dungeon.hero));
        } else {
            return Messages.get(this, "typical_stats");
        }
    }

    public class Regeneration extends RingBuff {

        @Override
        public boolean act() {
            // 每回合为佩戴者恢复生命值
            if (target.isAlive() && target.HP < target.HT) {
                target.HP = Math.min(target.HT, target.HP + level());
            }
            spend(TICK);
            return true;
        }

        @Override
        public String toString() {
            return Messages.get(RingOfRegeneration.class, "name");
        }
    }
}
```

### 示例2: 获取戒指加成
```java
// 获取英雄的伤害加成（假设 RingOfPower 提供伤害加成）
int powerBonus = Ring.getBonus(Dungeon.hero, RingOfPower.RingOfPowerBuff.class);

// 获取受 EnhancedRings 天赋影响的伤害加成  
int buffedPowerBonus = Ring.getBuffedBonus(Dungeon.hero, RingOfPower.RingOfPowerBuff.class);

// 在战斗逻辑中应用加成
int totalDamage = baseDamage + powerBonus;
```

### 示例3: 检查戒指识别状态
```java
Ring ring = Dungeon.hero.belongings.getItem(RingOfMight.class);

if (ring != null) {
    if (!ring.isIdentified()) {
        // 戒指未完全识别
        if (ring.readyToIdentify()) {
            // 已经准备好识别，等待 ShardOfOblivion 或手动识别
            GLog.p("戒指已准备好识别！");
        }
    } else {
        // 戒指已识别，可以显示详细统计信息
        String stats = ring.statsInfo();
    }
}
```

## 相关子类
Ring 类的主要子类包括各种具体戒指类型：

- **RingOfAccuracy**: 提升命中率
- **RingOfArcana**: 影响法杖充能速度和效果强度
- **RingOfElements**: 提升元素抗性和伤害
- **RingOfEvasion**: 提升闪避率
- **RingOfForce**: 提升物理攻击力
- **RingOfFuror**: 提升攻击速度
- **RingOfHaste**: 提升移动速度和行动频率
- **RingOfMight**: 提升最大生命值和力量
- **RingOfSharpshooting**: 提升远程武器精准度和伤害
- **RingOfTenacity**: 提升防御能力和伤害减免
- **RingOfWealth**: 增加金币掉落率

## 常见错误
1. **忘记重写 buff() 方法**: 如果不重写 buff() 方法返回具体的 RingBuff 实例，戒指将没有任何效果

2. **错误的等级计算**: 在自定义 RingBuff 中直接使用 Ring.this.level() 而不是 level() 方法，会导致忽略诅咒效果的正确处理

3. **忽略诅咒处理**: 诅咒戒指提供负面效果（level()-2），需要在 statsInfo() 和 RingBuff 中正确处理

4. **不当的静态引用**: 在非静态上下文中错误地使用 Ring.getBonus() 静态方法，导致逻辑错误

5. **序列化遗漏**: 添加新的实例字段时忘记在 storeInBundle() 和 restoreFromBundle() 中处理

6. **Buff 重复附加**: 在 activate() 中没有先检查并移除现有的 buff，可能导致多个相同 Buff 同时存在

7. **忽视 SpiritForm 支持**: 忘记在 getBonus() 逻辑中考虑 SpiritForm.SpiritFormBuff 的特殊戒指效果

8. **图像设置错误**: 手动设置 image 字段而不是依赖 ItemStatusHandler，会导致随机化和一致性问题