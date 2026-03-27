# Berry 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/food/Berry.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.food |
| **文件类型** | class |
| **继承关系** | extends Food |
| **代码行数** | 72 行 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Berry 类实现"地牢浆果"食物，是一种可快速食用的小型食物，具有种子掉落机制。每食用两颗浆果可获得一颗随机种子。

### 系统定位
Berry 是 Food 的具体子类，专用于女猎手英雄的天赋系统。它是游戏中最快食用的食物之一，并具有独特的种子收集机制。

### 不负责什么
- 不负责种子的具体类型生成（由 Generator 类处理）
- 不负责种子计数器的持久化细节（由 CounterBuff 基类处理）

## 3. 结构总览

### 主要成员概览
- **实例字段**：`image`、`energy`、`bones`（在初始化块设置）
- **覆写方法**：`eatingTime()`、`satisfy(Hero)`、`value()`
- **内部类**：`SeedCounter`（种子计数器 Buff）

### 主要逻辑块概览
- **快速食用**：`eatingTime()` 返回 0 或 1 回合
- **种子掉落**：`satisfy()` 使用计数器机制，每两颗浆果掉落一颗种子

### 生命周期/调用时机
- 通过女猎手天赋获取
- 食用时触发种子掉落机制

## 4. 继承与协作关系

### 父类提供的能力
继承自 Food 类的能力：
- 完整的食用流程（`execute()`、`eatSFX()`）
- 饥饿恢复机制（`satisfy()` 可覆写）
- 物品堆叠和价值系统
- 基础属性设置（`stackable`、`defaultAction`）

### 覆写的方法
| 方法名 | 覆写内容 |
|--------|----------|
| `eatingTime()` | 返回 0（有天赋）或 1（无天赋），实现快速食用 |
| `satisfy(Hero)` | 添加种子掉落机制 |
| `value()` | 返回 5 * quantity |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `CounterBuff` | 计数器 Buff 基类 |
| `Generator` | 随机种子生成 |
| `Dungeon` | 地牢实例和层级访问 |
| `Buff` | Buff 管理类 |

### 使用者
- `Talent` 系统（女猎手天赋）
- 地牢生成系统

## 5. 字段/常量详解

### 实例字段（初始化块设置）
| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.BERRY | 浆果图标 |
| `energy` | float | Hunger.HUNGRY/3f (约100) | 饱腹值，约为普通口粮的 1/3 |
| `bones` | boolean | false | 不出现在英雄遗骸中 |

### 内部类字段
| 字段名 | 所属类 | 说明 |
|--------|--------|------|
| `revivePersists` | SeedCounter | 设为 true，计数器在复活后保留 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认无参构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.BERRY;
    energy = Hunger.HUNGRY/3f; // 100 food value
    bones = false;
}
```

### 初始化注意事项
- `bones = false` 表示浆果不会出现在英雄遗骸中
- `energy` 值约为 100，是普通口粮（300）的 1/3

## 7. 方法详解

### eatingTime()

**可见性**：protected

**是否覆写**：是，覆写自 Food

**方法职责**：计算食用浆果所需时间，实现快速食用机制。

**参数**：无

**返回值**：float，0（有相关天赋）或 1（无天赋）

**核心实现逻辑**：
```java
@Override
protected float eatingTime(){
    if (Dungeon.hero.hasTalent(Talent.IRON_STOMACH)
            || Dungeon.hero.hasTalent(Talent.ENERGIZING_MEAL)
            || Dungeon.hero.hasTalent(Talent.MYSTICAL_MEAL)
            || Dungeon.hero.hasTalent(Talent.INVIGORATING_MEAL)
            || Dungeon.hero.hasTalent(Talent.FOCUSED_MEAL)
            || Dungeon.hero.hasTalent(Talent.ENLIGHTENING_MEAL)){
        return 0;
    } else {
        return 1;
    }
}
```

**边界情况**：
- 有天赋时食用时间为 0（即时食用）
- 无天赋时食用时间为 1 回合（比普通食物的 3 回合快很多）

---

### satisfy(Hero hero)

**可见性**：protected

**是否覆写**：是，覆写自 Food

**方法职责**：恢复饥饿值并处理种子掉落机制。

**参数**：
- `hero` (Hero)：食用浆果的英雄

**返回值**：void

**副作用**：
- 恢复英雄饥饿值（通过 `super.satisfy()`）
- 更新种子计数器
- 满足条件时掉落随机种子

**核心实现逻辑**：
```java
@Override
protected void satisfy(Hero hero) {
    super.satisfy(hero);
    SeedCounter counter = Buff.count(hero, SeedCounter.class, 1);
    if (counter.count() >= 2){
        Dungeon.level.drop(Generator.randomUsingDefaults(Generator.Category.SEED), hero.pos).sprite.drop();
        counter.detach();
    }
}
```

**边界情况**：
- 使用 `Buff.count()` 方法，计数器不存在时自动创建
- 计数器达到 2 时触发种子掉落并重置

---

### value()

**可见性**：public

**是否覆写**：是，覆写自 Food

**方法职责**：计算浆果的金币价值。

**参数**：无

**返回值**：int，5 * quantity

**核心实现逻辑**：
```java
@Override
public int value() {
    return 5 * quantity;
}
```

## 8. 对外暴露能力

### 显式 API
继承自 Food 的所有公开方法。

### 内部辅助方法
| 方法 | 用途 |
|------|------|
| `eatingTime()` | 计算快速食用时间 |
| `satisfy(Hero)` | 处理种子掉落机制 |

### 扩展入口
- `SeedCounter` 内部类可被外部访问

## 9. 运行机制与调用链

### 创建时机
- 女猎手天赋触发时生成
- 不通过地牢随机生成或商店购买

### 调用者
- 女猎手天赋系统

### 被调用者
- `Buff.count()` - 管理计数器
- `Generator.randomUsingDefaults()` - 生成随机种子
- `Dungeon.level.drop()` - 掉落种子到地面

### 种子掉落流程
```
食用浆果
    ↓
satisfy(hero)
    ↓
super.satisfy() - 恢复饥饿值
    ↓
Buff.count() - 增加/创建计数器
    ↓
计数 >= 2?
    ↓ 是
掉落随机种子 + 重置计数器
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.food.berry.name` | 地牢浆果 | 物品名称 |
| `items.food.berry.eat_msg` | 酸甜可口，真棒！ | 食用成功消息 |
| `items.food.berry.desc` | 这颗小浆果是女猎手在地牢的植被里找到的，它可以被快速食用以获得少量饱腹感，还可能有实用的种子包含其中！ | 物品描述 |
| `items.food.berry.discover_hint` | 你可使用某项英雄天赋找到该物品。 | 发现提示 |

### 依赖的资源
| 资源类型 | 资源标识 | 说明 |
|----------|----------|------|
| 图标 | ItemSpriteSheet.BERRY | 浆果图标 |
| 音效 | Assets.Sounds.EAT | 食用音效（继承自 Food） |

### 中文翻译来源
所有中文翻译来自 `core/src/main/assets/messages/items/items_zh.properties` 文件。

## 11. 使用示例

### 基本用法

```java
// 创建浆果
Berry berry = new Berry();
berry.collect(hero.belongings.backpack);

// 食用浆果（快速食用）
berry.execute(hero, Food.AC_EAT);

// 连续食用两颗可获得一颗随机种子
```

### 种子计数器机制

```java
// 检查种子计数器状态
SeedCounter counter = hero.buff(SeedCounter.class);
if (counter != null) {
    int count = counter.count(); // 当前食用次数
}
```

## 12. 开发注意事项

### 状态依赖
- 种子计数器 `SeedCounter` 是持久化的（`revivePersists = true`）
- 计数器状态在英雄复活后保留

### 生命周期耦合
- 种子计数器与英雄绑定
- 掉落的种子直接出现在英雄位置

### 常见陷阱
1. **计数器跨存档**：计数器状态会保存，玩家可能利用此机制
2. **食用时间检查**：天赋检查逻辑与 Food 类相同，需同步维护

## 13. 修改建议与扩展点

### 适合扩展的位置
1. **`satisfy()` 方法**：可修改种子掉落条件或奖励
2. **`SeedCounter` 类**：可扩展以实现不同的计数逻辑

### 不建议修改的位置
1. **`eatingTime()` 的天赋检查**：应与 Food 类保持一致
2. **种子掉落的种子类型**：使用默认随机以保持游戏平衡

### 重构建议
- 天赋检查逻辑可提取到 Food 类的静态方法，避免重复代码

## 14. 事实核查清单

- [x] 是否已覆盖全部字段 - 已覆盖 image、energy、bones
- [x] 是否已覆盖全部方法 - 已覆盖 eatingTime()、satisfy()、value()
- [x] 是否已检查继承链与覆写关系 - 已说明继承自 Food
- [x] 是否已核对官方中文翻译 - 已从 items_zh.properties 获取
- [x] 是否存在任何推测性表述 - 无，所有信息均来自源码
- [x] 示例代码是否真实可用 - 示例基于实际 API
- [x] 是否遗漏资源/配置/本地化关联 - 已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点 - 已在章节 12 和 13 详细说明

---

**文档版本**：1.0  
**最后更新**：2026-03-27  
**基于源码版本**：Shattered Pixel Dungeon (GPL-3.0)