# Food API 参考

## 类声明
```java
public class Food extends Item
```

## 类职责
Food 类是 Shattered Pixel Dungeon 中所有食物物品的基类。它实现了基本的进食功能，包括消耗物品、恢复饥饿值、播放音效和动画效果，以及处理与天赋系统和挑战模式的交互。所有具体的食物类型（如普通口粮、特殊食物等）都继承自这个类。

## 关键字段
| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|-------|------|---------|-------|------|
| TIME_TO_EAT | float | public static final | 3f | 标准进食所需时间 |
| AC_EAT | String | public static final | "EAT" | 进食动作标识符 |
| energy | float | public | Hunger.HUNGRY | 提供的饥饿值恢复量 |

## 构造方法
Food 类没有显式的构造方法，使用 Java 默认的无参构造方法，并通过实例初始化块设置默认属性。

## 实例初始化块
```java
{
    stackable = true;           // 食物可堆叠
    image = ItemSpriteSheet.RATION; // 使用口粮图像
    defaultAction = AC_EAT;     // 默认动作为进食
    bones = true;               // 可包含在英雄遗骸中
}
```

## 可重写方法
| 方法签名 | 返回值 | 必须重写？ | 默认行为 | 说明 |
|---------|-------|----------|---------|------|
| actions(Hero hero) | ArrayList<String> | 否 | 添加 "EAT" 动作到基础动作列表 | 获取食物可用的动作列表 |
| execute(Hero hero, String action) | void | 否 | 处理 "EAT" 动作，执行进食逻辑 | 执行指定动作 |
| eatSFX() | void | 否 | 播放 Assets.Sounds.EAT 音效 | 进食时的声音效果 |
| eatingTime() | float | 否 | 根据相关天赋减少进食时间（-2秒）或返回标准时间 | 获取实际进食所需时间 |
| satisfy(Hero hero) | void | 否 | 应用饥饿值恢复，考虑挑战模式和诅咒号角影响 | 执行饥饿值满足逻辑 |

## 公开方法
Food 类继承了 Item 的所有公开方法，并未添加新的公开方法。主要通过重写的 execute() 方法实现核心功能。

## 生命周期
1. **创建**: 实例化 Food 对象，默认能量值为 Hunger.HUNGRY
2. **拾取**: 被英雄拾取后加入背包，可与其他食物堆叠
3. **选择**: 在物品菜单中显示 "EAT" 动作选项
4. **使用**: 英雄选择进食动作，开始进食过程
5. **消耗**: 物品从背包中分离，数量减1
6. **效果**: 恢复饥饿值，触发天赋效果，更新统计数据
7. **完成**: 进食动画结束，英雄恢复正常状态

## 与其他系统的交互
- **饥饿系统 (Hunger)**: 直接调用 Buff.affect(hero, Hunger.class).satisfy(foodVal) 恢复饥饿值
- **天赋系统 (Talent)**: 触发 Talent.onFoodEaten() 回调，处理各种进餐天赋效果
- **挑战系统 (Challenges)**: 在 NO_FOOD 挑战下将食物效果减少到1/3
- **遗物系统 (Artifact)**: 检查 HornOfPlenty.hornRecharge 诅咒状态，减少食物效果到2/3
- **统计数据 (Statistics)**: 增加 foodEaten 计数，验证食物成就
- **成就系统 (Badges)**: 调用 Badges.validateFoodEaten() 验证相关成就
- **目录系统 (Catalog)**: 记录食物使用次数
- **音效系统 (Sample)**: 播放进食音效
- **视觉效果 (SpellSprite)**: 显示食物相关的魔法精灵效果
- **消息系统 (Messages)**: 显示进食成功消息

## 使用示例
### 示例1: 创建自定义食物

```java
package com.dustedpixel.dustedpixeldungeon.items.food;

import com.dustedpixel.dustedpixeldungeon.actors.buffs.Hunger;
import com.dustedpixel.dustedpixeldungeon.sprites.ItemSpriteSheet;

public class SpecialFood extends Food {

    {
        // 自定义属性
        image = ItemSpriteSheet.PASTY;      // 使用不同的图像
        energy = Hunger.STARVING;           // 提供更多饥饿值恢复

        // 继承的属性保持不变
        // stackable = true;
        // defaultAction = AC_EAT;
    }

    @Override
    protected float eatingTime() {
        // 特殊食物有更快的进食速度
        return TIME_TO_EAT - 1f;
    }

    @Override
    protected void satisfy(Hero hero) {
        // 自定义满足逻辑，可能添加额外效果
        super.satisfy(hero);
        // 添加额外效果...
    }
}
```

### 示例2: 处理食物使用
```java
// 创建食物实例
Food food = new Food();
food.energy = Hunger.WELL_FED; // 设置更高的能量值

// 英雄使用食物
if (food.actions(Dungeon.hero).contains(Food.AC_EAT)) {
    // 执行进食动作
    food.execute(Dungeon.hero, Food.AC_EAT);
    // 注意：execute 方法会自动处理分离、效果应用等所有逻辑
}

// 手动满足饥饿值（不推荐，绕过正常流程）
// Hero hero = Dungeon.hero;
// Buff.affect(hero, Hunger.class).satisfy(food.energy);
```

## 相关子类
Food 类的主要子类包括：

- **Pasty**: 特殊食物，提供更多的饥饿值恢复
- **MysteryMeat**: 神秘肉块，有随机效果
- **OverpricedRation**: 昂贵口粮，在商店出售
- **StewedMeat**: 炖肉，由史莱姆掉落
- **Blandfruit**: 无味果实，由种子生长而来

这些子类通常通过重写 `energy` 字段、`eatingTime()` 方法或 `satisfy()` 方法来实现不同的食物效果。

## 常见错误
1. **直接修改能量值而不考虑挑战模式**: 忘记在自定义 `satisfy()` 方法中处理 NO_FOOD 挑战的影响

2. **忽略诅咒号角效果**: 在自定义食物效果中没有检查 HornOfPlenty 诅咒状态对食物效果的削弱

3. **忘记调用父类方法**: 重写 `execute()` 或 `satisfy()` 时忘记调用 super 方法，导致统计数据不更新或成就无法解锁

4. **错误的进食时间计算**: 在 `eatingTime()` 中使用错误的天赋检查逻辑，导致时间计算不准确

5. **遗漏音效和视觉效果**: 在自定义食物中忘记调用 `eatSFX()` 和相关视觉效果，影响用户体验

6. **不当的堆叠处理**: 修改 stackable 属性但没有相应调整其他逻辑，导致堆叠行为异常

7. **忽略统计数据更新**: 在自定义实现中忘记增加 Statistics.foodEaten 计数或调用 Badges.validateFoodEaten()

8. **并发问题**: 在多人游戏或异步环境中不当处理食物使用逻辑（虽然 Shattered PD 是单人游戏）