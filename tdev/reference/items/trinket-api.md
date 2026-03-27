# Trinket API 参考

## 类声明
```java
public abstract class Trinket extends Item
```

## 类职责
Trinket 类是 Shattered Pixel Dungeon 中所有饰品的基础抽象类。饰品是游戏中的特殊物品类型，具有以下特点：
- 永久升级系统（通过能量水晶）
- 等级上限为 +3
- 自动识别（levelKnown = true）
- 唯一性（unique = true，死亡后保留）
- 提供被动效果或特殊能力
- 通过 Recipe 系统进行升级

饰品在游戏中提供强大的被动增益或主动能力，是后期游戏的重要组成部分。

## 关键字段
Trinket 类没有额外的实例字段，继承自 Item 并设置了以下默认属性：

| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|-------|------|---------|-------|------|
| levelKnown | boolean | (初始化块) | true | 等级始终已知 |
| cursedKnown | boolean | (初始化块) | true | 诅咒状态始终已知（实际上饰品不可诅咒）|
| unique | boolean | (初始化块) | true | 死亡后保留 |

## 构造方法
Trinket 是抽象类，没有公共构造方法，具体实现由子类提供。

## 抽象方法
| 方法签名 | 返回值 | 说明 |
|---------|-------|------|
| upgradeEnergyCost() | int | 返回升级所需的能量水晶数量 |
| statsDesc() | String | 返回饰品的统计信息描述 |

## 公开方法
| 方法签名 | 返回值 | 说明 |
|---------|-------|------|
| isUpgradable() | boolean | 返回 false（通过 Recipe 系统升级而非标准方式）|
| info() | String | 返回基础信息加上 statsDesc() |
| energyVal() | int | 返回能量水晶价值（默认5）|
| restoreFromBundle(Bundle bundle) | void | 从存档恢复时确保 levelKnown 和 cursedKnown 为 true |

## 静态方法
| 方法签名 | 返回值 | 说明 |
|---------|-------|------|
| trinketLevel(Class<? extends Trinket> trinketType) | int | 获取指定类型饰品的等级（-1 表示未装备）|

## 内部类：PlaceHolder
```java
public static class PlaceHolder extends Trinket
```
用于 UI 显示的占位符饰品：

| 特点 | 说明 |
|-----|------|
| 图像 | TRINKET_HOLDER |
| 升级成本 | 0 |
| 相似性检查 | 与任何 Trinket 相似 |
| 信息显示 | 返回空字符串 |

## 内部类：UpgradeTrinket
```java
public static class UpgradeTrinket extends Recipe
```
饰品升级配方，处理饰品的升级逻辑：

| 方法 | 功能 |
|-----|------|
| testIngredients() | 验证输入是否为可升级的饰品（等级 < 3）|
| cost() | 返回饰品的升级能量成本 |
| brew() | 执行升级操作并记录使用统计 |
| sampleOutput() | 返回升级后的预览结果 |

## 生命周期
1. **创建**: 通过反射实例化具体的 Trinket 子类
2. **初始化**: 设置 unique=true, levelKnown=true, cursedKnown=true
3. **发现**: 添加到英雄背包，自动完全识别
4. **装备**: 饰品效果立即激活（通常在 Hero.belongings 中处理）
5. **升级**: 通过 Recipe 系统消耗能量水晶提升等级（最高+3）
6. **死亡保留**: 英雄死亡时饰品保留在遗骸中
7. **重复获取**: 后续获取相同类型饰品会替换现有饰品

## 与其他系统的交互
- **Recipe 系统**: 通过 UpgradeTrinket 实现升级功能
- **能量水晶系统**: 消耗能量水晶进行升级
- **背包系统**: Hero.belongings.getItem() 获取当前装备的饰品
- **统计系统**: Catalog.countUse() 记录饰品使用次数
- **消息系统**: 获取本地化的描述和统计信息
- **天赋系统**: 某些饰品与天赋提供协同效果

## 使用示例
### 示例1: 创建自定义饰品

```java
package com.dustedpixel.dustedpixeldungeon.items.trinkets;

import com.dustedpixel.dustedpixeldungeon.Dungeon;
import com.dustedpixel.dustedpixeldungeon.messages.Messages;

public class RingOfAbundance extends Trinket {

    {
        image = 123; // 饰品图像索引

        // 继承的属性自动设置:
        // unique = true;
        // levelKnown = true;
        // cursedKnown = true;
    }

    @Override
    protected int upgradeEnergyCost() {
        // 升级成本随等级递增
        return (level() + 1) * 5;
    }

    @Override
    public String statsDesc() {
        if (level() <= 0) {
            return Messages.get(this, "desc_0");
        } else if (level() == 1) {
            return Messages.get(this, "desc_1");
        } else if (level() == 2) {
            return Messages.get(this, "desc_2");
        } else {
            return Messages.get(this, "desc_3");
        }
    }

    @Override
    public int energyVal() {
        // 高等级饰品有更高能量价值
        return 5 + level();
    }
}
```

### 示例2: 检查饰品等级和效果
```java
// 获取当前装备的饰品等级
int abundanceLevel = Trinket.trinketLevel(RingOfAbundance.class);

if (abundanceLevel >= 0) {
    // 饰品已装备，应用效果
    float goldMultiplier = 1.0f + (abundanceLevel * 0.1f);
    int finalGold = Math.round(baseGold * goldMultiplier);
} else {
    // 饰品未装备
    System.out.println("未装备财富戒指");
}

// 在其他系统中检查饰品效果
public void onEnemyKilled(Mob enemy) {
    int abundanceLvl = Trinket.trinketLevel(RingOfAbundance.class);
    if (abundanceLvl > 0) {
        // 额外掉落金币
        int extraGold = Random.Int(1, abundanceLvl + 1);
        Dungeon.level.drop(new Gold(extraGold), enemy.pos);
    }
}
```

### 示例3: 饰品升级处理
```java
// 创建升级配方实例
UpgradeTrinket upgradeRecipe = new UpgradeTrinket();

// 准备升级材料
ArrayList<Item> ingredients = new ArrayList<>();
ingredients.add(myTrinket); // 要升级的饰品

// 验证是否可以升级
if (upgradeRecipe.testIngredients(ingredients)) {
    int energyCost = upgradeRecipe.cost(ingredients);
    
    // 检查是否有足够的能量水晶
    if (Dungeon.energy >= energyCost) {
        // 执行升级
        Item upgradedTrinket = upgradeRecipe.brew(ingredients);
        // 升级后的饰品现在在背包中
        Dungeon.energy -= energyCost;
    }
}
```

## 相关子类
Trinket 的具体实现包括各种饰品类型，例如：

- **AlchemistsToolkit**: 炼金术士工具包，增强药水相关能力
- **ChaliceOfBlood**: 鲜血圣杯，提供生命值相关效果  
- **CloakOfShadows**: 暗影斗篷，提供隐身和闪避能力
- **DriedRose**: 干枯玫瑰，召唤幽灵助手
- **EldritchEye**: 邪异之眼，增强远程攻击
- **HornOfPlenty**: 丰饶号角，增加资源获取
- **MasterThievesArmband**: 大盗臂章，增加金币获取
- **SandalsOfNature**: 自然凉鞋，提供生命恢复
- **TalismanOfForesight**: 先知护符，提供危险预警
- **TimekeepersHourglass**: 时光沙漏，操控时间流速

## 常见错误
1. **忘记实现抽象方法**: 必须实现 upgradeEnergyCost() 和 statsDesc() 方法

2. **错误的升级逻辑**: 尝试使用标准的 upgrade() 方法而不是 Recipe 系统

3. **忽略等级上限**: 升级时没有检查等级是否已经达到 +3 上限

4. **不当的能量成本计算**: upgradeEnergyCost() 应该基于当前等级计算，而不是固定值

5. **序列化问题**: 添加新字段时忘记处理 Bundle 序列化

6. **效果激活时机**: 饰品效果应该在被获取时立即激活，而不是等到特定事件

7. **唯一性处理**: 忘记处理重复获取相同饰品时的替换逻辑

8. **消息键缺失**: statsDesc() 中引用的消息键在 messages.properties 中不存在

9. **能量价值设置**: 忘记重写 energyVal() 导致高等级饰品回收价值不变

10. **静态方法误用**: 错误地在非静态上下文中调用 trinketLevel() 静态方法