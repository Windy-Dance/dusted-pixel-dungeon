# 根骨秘药 (ElixirOfMight)

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/potions/elixirs/ElixirOfMight.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs |
| **文件类型** | class |
| **继承关系** | extends Elixir |
| **代码行数** | 150 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
实现根骨秘药的具体效果：永久增加英雄1点力量，并提供临时的生命值上限提升效果。

### 系统定位
作为Elixir抽象类的具体实现，提供力量增强和生命值提升的双重增益效果。

### 不负责什么
- 不负责炼金合成逻辑（由内部Recipe类处理）
- 不负责视觉效果的具体渲染（通过sprite系统处理）

## 3. 结构总览

### 主要成员概览
- `apply(Hero hero)` - 实现秘药的核心效果
- `desc()` - 返回动态描述文本
- `HTBoost` - 内部Buff类，管理生命值提升效果
- `Recipe` - 内部Recipe类，定义合成配方

### 主要逻辑块概览
- 秘药应用逻辑：增加力量、显示状态、应用生命提升buff
- 生命提升计算：基于英雄当前最大生命值计算提升量
- Recipe合成配方：使用力量药剂合成

### 生命周期/调用时机
- 当玩家饮用根骨秘药时触发apply方法
- HTBoost buff在英雄升级时自动减少持续时间
- 当buff持续时间耗尽时自动移除

## 4. 继承与协作关系

### 父类提供的能力
- 从Elixir继承了基础秘药行为
- 从Potion继承了完整的药剂功能

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `apply(Hero hero)` | 实现根骨秘药的具体效果 |
| `desc()` | 返回包含生命提升数值的动态描述 |

### 实现的接口契约
- 实现了Elixir的apply抽象方法

### 依赖的关键类
- `Hero` - 英雄角色，秘药效果的目标
- `Buff` - Buff系统，用于管理HTBoost效果
- `PotionOfStrength` - 力量药剂，用于合成根骨秘药
- `Badges` - 成就系统，验证相关成就

### 使用者
- 玩家角色在游戏中使用根骨秘药
- 炼金系统使用Recipe类进行合成

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `talentFactor` | float | 2f | 天赋加成因子 |

### 实例字段
无实例字段。

### 初始化块
```java
{
    image = ItemSpriteSheet.ELIXIR_MIGHT;
    unique = true;
    talentFactor = 2f;
}
```

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器。

### 初始化块
- 设置图像资源为 ELIXIR_MIGHT
- 标记为唯一物品(unique = true)
- 设置天赋因子为2f

### 初始化注意事项
由于标记为unique=true，游戏中的根骨秘药只能存在一个实例。

## 7. 方法详解

### apply(Hero hero)

**可见性**：public

**是否覆写**：是，覆写自 Elixir

**方法职责**：应用根骨秘药的效果到英雄身上

**参数**：
- `hero` (Hero)：目标英雄角色

**返回值**：void

**前置条件**：无

**副作用**：
- 增加英雄1点力量
- 显示力量增加的状态提示
- 应用HTBoost buff效果
- 更新英雄的最大生命值
- 触发相关成就验证

**核心实现逻辑**：
```java
identify();
hero.STR++;
hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, "1", FloatingText.STRENGTH);
Buff.affect(hero, HTBoost.class).reset();
HTBoost boost = Buff.affect(hero, HTBoost.class);
boost.reset();
hero.updateHT(true);
GLog.p(Messages.get(this, "msg", hero.STR()));
Badges.validateStrengthAttained();
Badges.validateDuelistUnlock();
```

**边界情况**：无

### desc()

**可见性**：public

**是否覆写**：否，这是Elixir子类的特有方法

**方法职责**：返回包含当前生命提升数值的描述文本

**参数**：无

**返回值**：String，本地化的描述文本

**前置条件**：需要Dungeon.hero来获取参考生命值

**副作用**：无

**核心实现逻辑**：
```java
return Messages.get(this, "desc", HTBoost.boost(Dungeon.hero != null ? Dungeon.hero.HT : 20));
```

**边界情况**：当Dungeon.hero为null时，使用默认HT值20

## 8. 对外暴露能力

### 显式 API
- `apply(Hero hero)` - 秘药效果应用
- `desc()` - 获取描述文本

### 内部辅助方法
- `HTBoost` 内部类提供生命提升管理

### 扩展入口
- 可以通过修改HTBoost类来调整生命提升的计算逻辑

## 9. 运行机制与调用链

### 创建时机
- 通过炼金釜使用1个力量药剂合成
- 游戏世界中可能生成

### 调用者
- Player类在玩家选择"饮用"操作时
- Inventory系统在处理物品使用时

### 被调用者
- Hero.STR++ - 增加力量
- Buff.affect() - 应用buff效果
- hero.updateHT() - 更新最大生命值
- Badges.validate*() - 验证成就

### 系统流程位置
根骨秘药在角色成长系统中扮演重要角色，提供永久性的力量增长和临时的生命值提升。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.potions.elixirs.elixirofmight.name | 根骨秘药 | 物品名称 |
| items.potions.elixirs.elixirofmight.msg | 新生的力量在你体内喷薄而出，你现在拥有%d点力量！ | 使用消息 |
| items.potions.elixirs.elixirofmight.desc | 这种强力的液体会洗刷你的根骨，永久性增加1点力量值并在一段时间内增加%d点生命上限。增强的生命上限值取决于你当前的最大生命值，不过增强值会随着等级提升而逐渐消失。 | 物品描述 |
| items.potions.elixirs.elixirofmight$htboost.name | 生命强化 | Buff名称 |
| items.potions.elixirs.elixirofmight$htboost.desc | 你觉得自己不同寻常的强壮与健康。\n\n你的最大生命在一段时间内会有所提升。生命提升会随着你的升级缓慢而稳定地消失。\n\n当前生命提升量：%d\n剩余生效等级：%d | Buff描述 |

### 依赖的资源
- ItemSpriteSheet.ELIXIR_MIGHT - 秘药图标
- FloatingText.STRENGTH - 力量状态图标
- CharSprite.POSITIVE - 正面状态颜色

### 中文翻译来源
来自 core/src/main/assets/messages/items/items_zh.properties 文件。

## 11. 使用示例

### 基本用法
```java
// 创建根骨秘药实例
ElixirOfMight elixir = new ElixirOfMight();
// 英雄使用秘药
elixir.apply(hero);
```

### 合成示例
```java
// 通过Recipe合成根骨秘药
ElixirOfMight.Recipe recipe = new ElixirOfMight.Recipe();
ArrayList<Item> ingredients = new ArrayList<>();
ingredients.add(new PotionOfStrength());
Item result = recipe.brew(ingredients);
```

## 12. 开发注意事项

### 状态依赖
- 效果依赖于英雄的当前最大生命值(HT)
- HTBoost buff的持续时间与英雄等级相关

### 生命周期耦合
- 秘药使用后会被消耗
- HTBoost buff会在英雄升级时自动减少持续时间

### 常见陷阱
- 忘记调用hero.updateHT(true)会导致生命值显示不正确
- HTBoost buff被应用了两次（代码中有重复调用）

## 13. 修改建议与扩展点

### 适合扩展的位置
- HTBoost.boost() 方法中的生命提升计算公式
- Recipe类中的合成成本调整

### 不建议修改的位置
- 力量增加的固定值（这是游戏平衡的核心）
- HTBoost的持续时间机制（与升级系统紧密耦合）

### 重构建议
可以考虑合并重复的Buff.affect调用，当前代码中对HTBoost应用了两次。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点