# Item API 参考

## 类声明
```java
public class Item implements Bundlable
```

## 类职责
Item 类是 Shattered Pixel Dungeon 中所有物品的基础抽象类。它定义了物品的基本属性、行为和生命周期管理机制，包括拾取、丢弃、投掷、堆叠、升级/降级、识别等核心功能。所有具体物品（如武器、护甲、药水、卷轴等）都继承自这个基类。

## 关键字段
| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|-------|------|---------|-------|------|
| defaultAction | String | protected | null | 物品的默认动作 |
| usesTargeting | boolean | public | false | 是否使用目标选择 |
| image | int | public | 0 | 物品精灵图索引 |
| icon | int | public | -1 | 用于随机化图像物品的标识符 |
| stackable | boolean | public | false | 是否可堆叠 |
| quantity | int | protected | 1 | 物品数量 |
| dropsDownHeap | boolean | public | false | 是否在掉落时形成堆叠 |
| level | int | private | 0 | 物品等级（实际值） |
| levelKnown | boolean | public | false | 等级是否已知 |
| cursed | boolean | public | false | 是否被诅咒 |
| cursedKnown | boolean | public | false | 诅咒状态是否已知 |
| unique | boolean | public | false | 是否为唯一物品（复活后保留） |
| keptThoughLostInvent | boolean | public | false | 即使英雄背包丢失也保留 |
| bones | boolean | public | false | 是否可包含在英雄遗骸中 |
| customNoteID | int | public | -1 | 自定义笔记ID |

## 构造方法
Item 类没有显式的构造方法，使用 Java 默认的无参构造方法。

## 可重写方法
| 方法签名 | 返回值 | 必须重写？ | 默认行为 | 说明 |
|---------|-------|----------|---------|------|
| actions(Hero hero) | ArrayList<String> | 否 | 返回 ["DROP", "THROW"] | 获取物品可用的动作列表 |
| actionName(String action, Hero hero) | String | 否 | 使用消息系统获取动作名称 | 获取动作的显示名称 |
| defaultAction() | String | 否 | 返回 defaultAction 字段 | 获取默认动作 |
| onThrow(int cell) | void | 否 | 在指定位置掉落物品 | 投掷物品后的回调 |
| isSimilar(Item item) | boolean | 否 | 比较类是否相同 | 判断物品是否相似（可堆叠） |
| onDetach() | void | 否 | 空实现 | 物品从背包分离时的回调 |
| level() | int | 否 | 返回 level 字段 | 获取持久化等级 |
| buffedLvl() | int | 否 | 考虑退化效果的等级 | 获取受临时增益/减益影响的等级 |
| isUpgradable() | boolean | 否 | true | 是否可升级 |
| isEquipped(Hero hero) | boolean | 否 | false | 是否被英雄装备 |
| onHeroGainExp(float levelPercent, Hero hero) | void | 否 | 空实现 | 英雄获得经验时的回调 |
| name() | String | 否 | 调用 trueName() | 获取物品名称 |
| glowing() | ItemSprite.Glowing | 否 | null | 获取发光效果 |
| emitter() | Emitter | 否 | null | 获取粒子发射器 |
| desc() | String | 否 | 使用消息系统获取描述 | 获取物品描述 |
| value() | int | 否 | 0 | 获取金币价值 |
| energyVal() | int | 否 | 0 | 获取能量水晶价值 |
| random() | Item | 否 | 返回 this | 随机化物品 |
| status() | String | 否 | 数量大于1时返回数量字符串 | 获取状态显示 |
| targetingPos(Hero user, int dst) | int | 否 | 调用 throwPos() | 获取目标选择位置 |
| throwPos(Hero user, int dst) | int | 否 | 使用弹道计算碰撞位置 | 获取投掷位置 |
| throwSound() | void | 否 | 播放投掷音效 | 投掷时的声音效果 |
| castDelay(Char user, int cell) | float | 否 | TIME_TO_THROW (1.0f) | 投掷延迟时间 |
| pickupDelay() | float | 否 | TIME_TO_PICK_UP (1.0f) | 拾取延迟时间 |

## 公开方法
| 方法签名 | 返回值 | 说明 |
|---------|-------|------|
| doPickUp(Hero hero) | boolean | 执行拾取操作 |
| doPickUp(Hero hero, int pos) | boolean | 在指定位置执行拾取操作 |
| doDrop(Hero hero) | void | 执行丢弃操作 |
| reset() | void | 重置物品属性以确保运行间一致性 |
| keptThroughLostInventory() | boolean | 检查是否通过丢失库存保留 |
| doThrow(Hero hero) | void | 执行投掷操作（进入目标选择） |
| execute(Hero hero, String action) | void | 执行指定动作 |
| execute(Hero hero) | void | 执行默认动作 |
| merge(Item other) | Item | 合并相似物品 |
| collect(Bag container) | boolean | 收集到指定容器 |
| collect() | boolean | 收集到英雄背包 |
| split(int amount) | Item | 分割指定数量 |
| duplicate() | Item | 创建副本 |
| detach(Bag container) | Item | 从容器分离一个单位 |
| detachAll(Bag container) | Item | 从容器分离所有单位 |
| trueLevel() | int | 获取真实等级（忽略所有修饰符） |
| level(int value) | void | 设置物品等级 |
| upgrade() | Item | 升级物品（+1） |
| upgrade(int n) | Item | 升级物品（+n） |
| degrade() | Item | 降级物品（-1） |
| degrade(int n) | Item | 降级物品（-n） |
| visiblyUpgraded() | int | 获取可见升级等级 |
| buffedVisiblyUpgraded() | int | 获取受增益影响的可见升级等级 |
| visiblyCursed() | boolean | 是否可见被诅咒 |
| isIdentified() | boolean | 是否已完全识别 |
| identify() | Item | 识别物品（由英雄识别） |
| identify(boolean byHero) | Item | 识别物品（可指定是否由英雄识别） |
| evoke(Hero hero) | void | 触发动画效果 |
| title() | String | 获取完整标题（包含等级和数量） |
| trueName() | String | 获取真实名称 |
| image() | int | 获取图像索引 |
| info() | String | 获取完整信息（包含自定义笔记） |
| quantity() | int | 获取数量 |
| quantity(int value) | Item | 设置数量 |
| virtual() | Item | 创建虚拟物品（数量为0） |
| setCurrent(Hero hero) | void | 设置当前用户和物品 |
| storeInBundle(Bundle bundle) | void | 序列化到Bundle |
| restoreFromBundle(Bundle bundle) | void | 从Bundle反序列化 |
| cast(Hero user, int dst) | void | 投掷物品到目标位置 |

## 生命周期
1. **创建**: 通过反射实例化 Item 子类
2. **初始化**: 设置基本属性（数量、等级、诅咒状态等）
3. **拾取**: 调用 `doPickUp()` 将物品添加到英雄背包
4. **收集**: 调用 `collect()` 处理堆叠逻辑和库存管理
5. **使用**: 通过 `execute()` 执行各种动作（丢弃、投掷等）
6. **分离**: 调用 `detach()` 或 `detachAll()` 从背包移除
7. **丢弃/投掷**: 物品被放置到地图上形成堆叠
8. **序列化**: 游戏保存时通过 `storeInBundle()` 保存状态
9. **反序列化**: 游戏加载时通过 `restoreFromBundle()`恢复状态
10. **销毁**: 物品被消耗或移除时结束生命周期

## 与其他系统的交互
- **消息系统 (Messages)**: 获取本地化的名称和描述
- **游戏场景 (GameScene)**: 处理拾取、更新显示等视觉反馈
- **音效系统 (Sample)**: 播放拾取、投掷等音效
- **背包系统 (Belongings/Bag)**: 管理物品存储和组织
- **快速栏 (QuickSlotButton)**: 处理快速使用和占位符
- **目录系统 (Catalog/Statistics)**: 记录发现的物品类型
- **成就系统 (Badges)**: 验证物品等级成就
- **天赋系统 (Talent)**: 触发天赋相关效果
- **弹道系统 (Ballistica)**: 计算投掷轨迹
- **动画系统 (Emitter/Speck)**: 显示视觉效果
- **存档系统 (Bundle)**: 处理游戏保存和加载

## 使用示例
### 示例1: 创建自定义物品

```java
package com.dustedpixel.dustedpixeldungeon.items.custom;

import com.dustedpixel.dustedpixeldungeon.items.Item;
import com.dustedpixel.dustedpixeldungeon.actors.hero.Hero;
import com.dustedpixel.dustedpixeldungeon.messages.Messages;

public class CustomPotion extends Item {

    {
        // 设置物品属性
        stackable = true;           // 可堆叠
        image = 15;                 // 图像索引
        usesTargeting = true;       // 使用目标选择

        // 设置初始状态
        levelKnown = true;          // 等级已知
        cursedKnown = true;         // 诅咒状态已知
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        // 添加自定义动作
        actions.add("DRINK");
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals("DRINK")) {
            // 执行喝药水的逻辑
            detach(hero.belongings.backpack);
            // 添加效果...
            hero.sprite.operate(hero.pos);
        }
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }

    @Override
    public int value() {
        return 30; // 药水价值30金币
    }
}
```

### 示例2: 物品收集和处理
```java
// 创建新物品
CustomPotion potion = new CustomPotion();
potion.quantity(3); // 设置数量为3

// 尝试收集到英雄背包
if (potion.collect(Dungeon.hero.belongings.backpack)) {
    // 收集成功
    System.out.println("物品已添加到背包");
    
    // 如果背包中已有相同物品，会自动堆叠
    // 获取当前数量
    int currentQuantity = potion.quantity();
    
    // 分离一个单位使用
    Item singlePotion = potion.detach(Dungeon.hero.belongings.backpack);
    if (singlePotion != null) {
        // 使用单个药水
        ((CustomPotion)singlePotion).execute(Dungeon.hero, "DRINK");
    }
} else {
    // 收集失败（背包已满）
    System.out.println("背包已满，无法收集");
}

// 创建物品副本用于预览
Item preview = potion.duplicate();
preview.quantity(1);
```

## 相关子类
Item 类有许多直接子类，每个都有其特殊功能：

- **Weapon**: 武器类，处理攻击、伤害计算、装备等
- **Armor**: 护甲类，处理防御、耐久度、装备等  
- **Potion**: 药水类，处理喝药效果、瓶子回收等
- **Scroll**: 卷轴类，处理阅读效果、卷轴消耗等
- **Ring**: 戒指类，处理套装效果、等级影响等
- **Wand**: 法杖类，处理充能、施法、MP消耗等
- **Artifact**: 遗物类，处理特殊能力、充能机制等
- **Food**: 食物类，处理饥饿值恢复、进食时间等
- **Key**: 钥匙类，处理门锁开启、不可堆叠等
- **Bag**: 背包类，处理嵌套存储、容量限制等
- **MissileWeapon**: 投掷武器类，处理弹药、远程攻击等

## 常见错误
1. **忘记设置 stackable**: 如果物品应该可堆叠但忘记设置 stackable = true，会导致多个相同物品占用多个槽位

2. **错误的等级处理**: 直接修改 level 字段而不是使用 upgrade()/degrade() 方法，会导致快速栏不更新

3. **忽略识别状态**: 在未设置 levelKnown 和 cursedKnown 的情况下显示等级信息，玩家会看到未识别的状态

4. **不当的 detach() 使用**: 在物品数量为0时调用 detach() 可能导致空指针异常

5. **忘记调用 super.execute()**: 重写 execute() 方法时忘记调用父类方法，会导致基本动作（丢弃、投掷）失效

6. **序列化遗漏**: 添加新字段时忘记在 storeInBundle() 和 restoreFromBundle() 中处理，导致存档加载后数据丢失

7. **并发修改问题**: 在循环中修改 items 列表而没有正确处理迭代器

8. **反射实例化失败**: 某些 Item 子类可能没有默认构造函数，导致 Reflection.newInstance() 返回 null