# Potion API 参考

## 类声明
```java
public abstract class Potion extends Item
```

## 类职责
Potion是所有药水的抽象基类，提供饮用/投掷功能、随机颜色系统、识别机制等。作为抽象类，它定义了所有药水共有的行为和属性，具体效果由子类实现。

## 关键字段

### 静态字段
- `AC_DRINK`: 标准饮用动作常量，值为"DRINK"
- `AC_CHOOSE`: 用于可选择饮用或投掷的药水，值为"CHOOSE"
- `TIME_TO_DRINK`: 饮用所需时间，值为1.0f
- `colors`: 药水颜色映射表，包含12种颜色（crimson, amber, golden, jade, turquoise, azure, indigo, magenta, bistre, charcoal, silver, ivory）
- `mustThrowPots`: 必须投掷的药水集合（如毒气药水、液态火焰等）
- `canThrowPots`: 可选择投掷的药水集合（如净化药水、漂浮药水等）
- `handler`: `ItemStatusHandler<Potion>`实例，管理药水的颜色分配和识别状态

### 实例字段
- `color`: 药水的颜色字符串，用于未识别时的显示
- `talentFactor`: 影响药水相关天赋触发强度的因子，默认值为1
- `talentChance`: 药水相关天赋触发的概率（0-1），默认值为1
- `anonymous`: 匿名药水标志，匿名药水始终被视为已识别且不影响识别状态

## 标准行动常量
- `AC_DRINK`: "DRINK" - 标准饮用动作
- `AC_CHOOSE`: "CHOOSE" - 选择动作（用于可饮用或投掷的药水）

## 可重写方法

| 方法签名 | 返回值 | 默认行为 | 说明 |
|---------|--------|----------|------|
| `apply(Hero hero)` | void | 调用`shatter(hero.pos)` | 应用药水效果到英雄身上，通常在饮用时调用 |
| `shatter(int cell)` | void | 调用`splash(cell)`并播放破碎音效 | 在指定位置破碎药水，产生视觉和音效反馈 |
| `drink(Hero hero)` | void | 分离物品、消耗时间、调用apply、播放音效 | 处理饮用逻辑，包括背包操作和天赋触发 |

## 颜色系统
Potion类使用`ItemStatusHandler`实现随机颜色系统：

1. **颜色初始化**：通过`initColors()`方法创建`ItemStatusHandler`实例，将所有药水类与预定义的颜色池关联
2. **颜色分配**：每次创建药水实例时，`reset()`方法会从颜色池中随机分配一个颜色
3. **持久化**：通过`save()`、`restore()`、`saveSelectively()`方法在游戏存档中保存和恢复颜色分配
4. **视觉表现**：颜色决定药水的精灵图（sprite）和破碎时的飞溅效果颜色

颜色池包含12种预定义颜色，每种颜色对应特定的精灵图表索引。这种设计确保相同类型的药水在游戏中总是显示相同的颜色，但不同游戏会话中颜色分配是随机的。

## 识别系统

### 核心方法
- `isKnown()`: 判断药水是否已被识别。匿名药水始终返回true，普通药水通过`ItemStatusHandler`检查
- `setKnown()`: 将药水标记为已识别，更新快捷栏，并在图鉴中记录
- `identify()`: 标准识别方法，内部调用`setKnown()`

### 匿名药水 (anonymousPotions)
- 通过`anonymize()`方法设置
- 始终被视为已识别（`isKnown()`返回true）
- 不影响游戏中的识别状态统计
- 如果未被识别，其精灵图会被替换为占位符（`ItemSpriteSheet.POTION_HOLDER`）
- 主要用于UI显示或仅为其效果而生成的物品

### 识别状态管理
- 使用`ItemStatusHandler`统一管理所有药水的识别状态
- 提供静态方法获取已知/未知药水集合：`getKnown()`, `getUnknown()`
- `allKnown()`方法检查是否所有药水类型都已被发现

## 使用示例

### 示例1: 创建简单药水
```java
// 创建一个基础治疗药水
PotionOfHealing healingPotion = new PotionOfHealing();
// 药水会自动分配随机颜色
healingPotion.reset(); 
// 如果需要强制识别（如在教程中）
healingPotion.setKnown();
```

### 示例2: 创建投掷药水
```java
// 创建一个毒气药水（属于mustThrowPots）
PotionOfToxicGas toxicGas = new PotionOfToxicGas();
// 默认动作为投掷而非饮用
String defaultAction = toxicGas.defaultAction(); // 返回 AC_THROW

// 投掷到指定位置
toxicGas.onThrow(targetCell);
```

### 示例3: 创建自定义药水效果
```java
public class PotionOfRegeneration extends Potion {
    @Override
    public void apply(Hero hero) {
        // 自定义应用逻辑
        Buff.affect(hero, Regeneration.class).level(2);
        // 调用父类破碎效果
        super.shatter(hero.pos);
    }
    
    @Override
    public void shatter(int cell) {
        // 自定义破碎逻辑
        if (Dungeon.level.heroFOV[cell]) {
            GLog.p("魔法能量在空气中弥漫！");
        }
        // 添加自定义效果
        Dungeon.level.addBlob(new RegenerationCloud(cell), hero);
        // 调用父类飞溅效果
        super.splash(cell);
    }
}
```

## 相关子类

### 基础药水类 (12种)
- `PotionOfExperience` - 经验药水
- `PotionOfFrost` - 冰霜药水  
- `PotionOfHaste` - 疾速药水
- `PotionOfHealing` - 治疗药水
- `PotionOfInvisibility` - 隐身药水
- `PotionOfLevitation` - 漂浮药水
- `PotionOfLiquidFlame` - 液态火焰药水
- `PotionOfMindVision` - 心灵感应药水
- `PotionOfParalyticGas` - 麻痹气体药水
- `PotionOfPurity` - 净化药水
- `PotionOfStrength` - 力量药水
- `PotionOfToxicGas` - 毒气药水

### 特殊药水类别

#### Elixir (灵药类)
继承自`Elixir`抽象类，始终被视为已识别，价值更高：
- `ElixirOfAquaticRejuvenation`
- `ElixirOfArcaneArmor` 
- `ElixirOfDragonsBlood`
- `ElixirOfFeatherFall`
- `ElixirOfHoneyedHealing`
- `ElixirOfIcyTouch`
- `ElixirOfMight`
- `ElixirOfToxicEssence`

#### Brew (酿造类)
继承自`Brew`抽象类，只能投掷不能饮用，始终被视为已识别：
- `AquaBrew`
- `BlizzardBrew`
- `CausticBrew`
- `InfernalBrew`
- `ShockingBrew`
- `UnstableBrew`

#### ExoticPotion (异域药水类)
继承自`ExoticPotion`，是基础药水的高级变体：
- `PotionOfCleansing` (对应PotionOfPurity)
- `PotionOfCorrosiveGas` (对应PotionOfToxicGas)
- `PotionOfDivineInspiration` (对应PotionOfExperience)
- `PotionOfDragonsBreath` (对应PotionOfLiquidFlame)
- `PotionOfEarthenArmor` (对应PotionOfParalyticGas)
- `PotionOfMagicalSight` (对应PotionOfMindVision)
- `PotionOfMastery` (对应PotionOfStrength)
- `PotionOfShielding` (对应PotionOfHealing)
- `PotionOfShroudingFog` (对应PotionOfInvisibility)
- `PotionOfSnapFreeze` (对应PotionOfFrost)
- `PotionOfStamina` (对应PotionOfHaste)
- `PotionOfStormClouds` (对应PotionOfLevitation)

## 常见错误

1. **忘记调用super方法**：重写`apply()`或`shatter()`时，如果需要保留默认的视觉效果，记得调用父类的相应方法

2. **颜色系统误解**：不要手动设置`color`字段，应该通过`reset()`方法让`ItemStatusHandler`自动分配

3. **识别状态处理错误**：不要直接修改识别状态，应该使用`setKnown()`和`isKnown()`方法

4. **投掷逻辑错误**：对于`mustThrowPots`中的药水，需要处理玩家尝试饮用时的确认对话框

5. **匿名药水使用不当**：匿名药水主要用于特殊场景，普通游戏物品不应随意设为匿名

6. **天赋触发遗漏**：自定义药水效果时，记得在适当位置调用`Talent.onPotionUsed()`来触发相关天赋

7. **价值计算错误**：重写`value()`或`energyVal()`方法时，注意考虑`quantity`字段的影响