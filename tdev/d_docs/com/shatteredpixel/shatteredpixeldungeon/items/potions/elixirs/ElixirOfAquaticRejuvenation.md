# 水灵秘药 (ElixirOfAquaticRejuvenation)

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/potions/elixirs/ElixirOfAquaticRejuvenation.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs |
| **文件类型** | class |
| **继承关系** | extends Elixir |
| **代码行数** | 171 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现水灵秘药的具体效果：为英雄施加水灵治疗(AquaHealing)buff，在英雄站在水中时持续恢复生命值，总恢复量为英雄最大生命值的1.5倍。

### 系统定位
作为Elixir抽象类的具体实现，提供基于环境的持续治疗效果，特别适合在水域环境中使用。

### 不负责什么
- 不负责炼金合成逻辑（由内部Recipe类处理）
- 不直接处理无治疗挑战逻辑（通过PotionOfHealing委托处理）

## 3. 结构总览

### 主要成员概览
- `apply(Hero hero)` - 实现秘药的核心效果
- `AquaHealing` - 内部Buff类，管理水灵治疗效果
- `Recipe` - 内部Recipe类，定义合成配方

### 主要逻辑块概览
- 秘药应用逻辑：检查无治疗挑战，施加AquaHealing buff
- AquaHealing逻辑：每回合检查是否在水中并进行治疗
- 治疗量计算：基于英雄最大生命值的1/50，但不超过剩余生命值
- Recipe合成配方：使用治疗药剂和粘咕球合成

### 生命周期/调用时机
- 当玩家饮用水灵秘药时触发apply方法
- AquaHealing buff每回合自动检查并治疗（如果条件满足）
- 合成时触发Catalog.countUse()记录粘咕球使用

## 4. 继承与协作关系

### 父类提供的能力
- 从Elixir继承了基础秘药行为
- 从Potion继承了完整的药剂功能

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `apply(Hero hero)` | 实现水灵秘药的具体效果 |

### 实现的接口契约
- 实现了Elixir的apply抽象方法

### 依赖的关键类
- `Hero` - 英雄角色，秘药效果的目标
- `Buff` - Buff系统，用于管理AquaHealing效果
- `PotionOfHealing` - 治疗药剂，处理无治疗挑战逻辑
- `Dungeon` - 地牢系统，检查水域和挑战状态
- `Challenges` - 挑战系统，检查无治疗挑战
- `GooBlob` - 粘咕球，用于合成水灵秘药
- `Catalog` - 成就系统，记录物品使用

### 使用者
- 玩家角色在游戏中使用水灵秘药
- 游戏系统每回合自动调用AquaHealing.act()
- 炼金系统使用Recipe类进行合成

## 5. 字段/常量详解

### 静态常量
无静态常量。

### 实例字段
无实例字段。

### 初始化块
```java
{
    image = ItemSpriteSheet.ELIXIR_AQUA;
}
```

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器。

### 初始化块
- 设置图像资源为 ELIXIR_AQUA

### 初始化注意事项
无特殊的初始化注意事项。

## 7. 方法详解

### apply(Hero hero)

**可见性**：public

**是否覆写**：是，覆写自 Elixir

**方法职责**：应用水灵秘药的效果到英雄身上

**参数**：
- `hero` (Hero)：目标英雄角色

**返回值**：void

**前置条件**：无

**副作用**：
- 如果处于无治疗挑战，则触发药理恐惧效果
- 否则施加AquaHealing buff，设置总恢复量为英雄最大生命值的1.5倍

**核心实现逻辑**：
```java
if (Dungeon.isChallenged(Challenges.NO_HEALING)){
    PotionOfHealing.pharmacophobiaProc(hero);
} else {
    Buff.affect(hero, AquaHealing.class).set(Math.round(hero.HT * 1.5f));
}
```

**边界情况**：无治疗挑战模式下的特殊处理

### AquaHealing.set(int amount)

**可见性**：public

**是否覆写**：否，这是AquaHealing内部类的特有方法

**方法职责**：设置剩余治疗量

**参数**：
- `amount` (int)：要设置的治疗量

**返回值**：void

**前置条件**：无

**副作用**：更新left字段，但不会减少现有值

**核心实现逻辑**：
```java
if (amount > left) left = amount;
```

**边界情况**：当新值小于当前值时不更新

### AquaHealing.extend(float duration)

**可见性**：public

**是否覆写**：否，这是AquaHealing内部类的特有方法

**方法职责**：延长治疗持续时间

**参数**：
- `duration` (float)：要延长的时间

**返回值**：void

**前置条件**：无

**副作用**：增加left字段值

**核心实现逻辑**：
```java
left += duration;
```

**边界情况**：无

### AquaHealing.act()

**可见性**：public

**是否覆写**：是，覆写自 Buff

**方法职责**：每回合执行的治疗逻辑

**参数**：无

**返回值**：boolean，总是返回true

**前置条件**：英雄必须在水中且生命值未满

**副作用**：
- 计算并应用治疗量
- 更新剩余治疗量
- 在生命值满或离开水面时停止治疗
- 在治疗量耗尽时移除buff

**核心实现逻辑**：
- 检查英雄是否在水中(Dungeon.level.water[target.pos])且未飞行
- 计算治疗量：min(1, HT/50, 剩余治疗量, 生命值缺口)
- 应用治疗并更新剩余量
- 处理生命值满或治疗量耗尽的情况

**边界情况**：
- 英雄不在水中时跳过治疗
- 英雄生命值已满时停止治疗
- 治疗量耗尽时移除buff

## 8. 对外暴露能力

### 显式 API
- `apply(Hero hero)` - 秘药效果应用
- `AquaHealing.set(int amount)` - 设置治疗量
- `AquaHealing.extend(float duration)` - 延长持续时间

### 内部辅助方法
- `AquaHealing` 内部类提供完整的水灵治疗功能
- `Recipe` 内部类提供合成配方和成就计数

### 扩展入口
- 可以通过修改AquaHealing类来调整治疗逻辑
- 可以通过修改Recipe类来调整合成配方

## 9. 运行机制与调用链

### 创建时机
- 通过炼金釜使用1个治疗药剂和1个粘咕球合成
- 游戏世界中可能生成

### 调用者
- Player类在玩家选择"饮用"操作时
- GameScene类每回合自动调用AquaHealing.act()
- Inventory系统在处理物品使用时

### 被调用者
- Dungeon.isChallenged() - 检查挑战状态
- PotionOfHealing.pharmacophobiaProc() - 处理无治疗挑战
- Buff.affect() - 应用AquaHealing buff
- Catalog.countUse() - 记录粘咕球使用（在合成时）

### 系统流程位置
水灵秘药在生存系统中提供环境依赖的持续治疗，特别适合在水域关卡或面对大量敌人时使用，是持久战的重要支援道具。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.potions.elixirs.elixirofaquaticrejuvenation.name | 水灵秘药 | 物品名称 |
| items.potions.elixirs.elixirofaquaticrejuvenation.desc | 这瓶秘药中包含着被粘咕力量强化的治疗液体。它不会为你提供立即的治疗效果，而会当你站在水中时逐渐为你恢复总量更大的生命值。 | 物品描述 |
| items.potions.elixirs.elixirofaquaticrejuvenation$aquahealing.name | 水灵治疗 | Buff名称 |
| items.potions.elixirs.elixirofaquaticrejuvenation$aquahealing.desc | 你暂时获得了如同粘咕一样的恢复能力。\n\n当站在水面上时，你每回合都能够恢复少量生命值。生命值全满或离开水面时会暂停该效果。\n\n剩余的恢复量：%d | Buff描述 |

### 依赖的资源
- ItemSpriteSheet.ELIXIR_AQUA - 秘药图标
- BuffIndicator.HEALING - 治疗buff指示器
- CharSprite.POSITIVE - 正面状态颜色

### 中文翻译来源
来自 core/src/main/assets/messages/items/items_zh.properties 文件。

## 11. 使用示例

### 基本用法
```java
// 创建水灵秘药实例
ElixirOfAquaticRejuvenation elixir = new ElixirOfAquaticRejuvenation();
// 英雄使用秘药
elixir.apply(hero);
```

### 合成示例
```java
// 通过Recipe合成水灵秘药
ElixirOfAquaticRejuvenation.Recipe recipe = new ElixirOfAquaticRejuvenation.Recipe();
ArrayList<Item> ingredients = new ArrayList<>();
ingredients.add(new PotionOfHealing());
ingredients.add(new GooBlob());
Item result = recipe.brew(ingredients);
```

### 治疗触发
```java
// 系统每回合自动调用
AquaHealing aquaHealing = hero.buff(AquaHealing.class);
if (aquaHealing != null) {
    // 在act()方法中自动处理治疗逻辑
}
```

## 12. 开发注意事项

### 状态依赖
- 效果依赖于英雄是否在水中
- 治疗量依赖于英雄的最大生命值(HT)
- 受无治疗挑战模式影响

### 生命周期耦合
- 秘药使用后会被消耗
- AquaHealing buff会在治疗量耗尽或英雄离开水面时自动移除
- 英雄休息状态会在治疗结束时自动取消

### 常见陷阱
- 忘记处理无治疗挑战模式的特殊情况
- 治疗量计算中的浮点数精度问题
- 忘记在合成时调用Catalog.countUse()导致成就无法正确统计

## 13. 修改建议与扩展点

### 适合扩展的位置
- AquaHealing治疗量计算公式（当前为HT/50）
- 总恢复量倍数（当前为1.5倍）
- Recipe类中的合成成本调整（当前为6点能量）

### 不建议修改的位置
- 基础的水域检测逻辑（这是核心机制）
- 合成配方的基础材料（治疗药剂+粘咕球的组合很合理）

### 重构建议
可以考虑将治疗量计算提取为单独的方法，提高可读性和可测试性。当前的GameMath.gate调用可以封装得更清晰。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点