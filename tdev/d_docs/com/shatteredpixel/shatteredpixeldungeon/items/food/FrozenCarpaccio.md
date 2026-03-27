# FrozenCarpaccio 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/food/FrozenCarpaccio.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.food |
| **文件类型** | class |
| **继承关系** | extends Food |
| **代码行数** | 81 行 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
FrozenCarpaccio 类实现"冷冻生肉片"食物，是一种通过冻结效果制作的安全可食用肉类。食用时随机触发有益效果（隐形、树肤、净化或治疗）。

### 系统定位
FrozenCarpaccio 是 Food 的子类，作为神秘肉经过冻结处理后的安全版本。它与烤肉不同，提供额外的随机增益效果。

### 不负责什么
- 不负责冻结效果的触发（由外部机制处理）
- 不负责神秘肉到冷冻肉片的转化流程

## 3. 结构总览

### 主要成员概览
- **实例字段**：`image`、`energy`（在初始化块设置）
- **覆写方法**：`satisfy(Hero)`、`value()`
- **静态方法**：`effect(Hero)`、`cook(MysteryMeat)`

### 主要逻辑块概览
- **随机效果**：食用时 4/5 概率触发以下效果之一：隐形、树肤、净化、治疗
- **工厂方法**：从神秘肉创建实例

### 生命周期/调用时机
- 神秘肉被冻结时自动转化
- 食用时触发随机效果

## 4. 继承与协作关系

### 父类提供的能力
继承自 Food 类的完整食用功能。

### 覆写的方法
| 方法名 | 覆写内容 |
|--------|----------|
| `satisfy(Hero)` | 添加随机效果触发 |
| `value()` | 返回 10 * quantity |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Invisibility` | 隐形 Buff |
| `Barkskin` | 树肤 Buff |
| `PotionOfHealing` | 治疗和净化效果 |
| `Random` | 随机数生成 |

## 5. 字段/常量详解

### 实例字段（初始化块设置）
| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.CARPACCIO | 冷冻肉片图标 |
| `energy` | float | Hunger.HUNGRY/2f (约150) | 饱腹值 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认无参构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.CARPACCIO;
    energy = Hunger.HUNGRY/2f;
}
```

## 7. 方法详解

### satisfy(Hero hero)

**可见性**：protected

**是否覆写**：是，覆写自 Food

**方法职责**：恢复饥饿值并触发随机效果。

**参数**：
- `hero` (Hero)：食用者

**返回值**：void

**核心实现逻辑**：
```java
@Override
protected void satisfy(Hero hero) {
    super.satisfy(hero);
    effect(hero);
}
```

---

### effect(Hero hero) [静态方法]

**可见性**：public static

**是否覆写**：否

**方法职责**：对英雄施加随机有益效果。

**参数**：
- `hero` (Hero)：效果目标

**返回值**：void

**核心实现逻辑**：
```java
public static void effect(Hero hero){
    switch (Random.Int( 5 )) {
        case 0:
            // 隐形效果
            GLog.i( Messages.get(FrozenCarpaccio.class, "invis") );
            Buff.affect( hero, Invisibility.class, Invisibility.DURATION );
            break;
        case 1:
            // 树肤效果
            GLog.i( Messages.get(FrozenCarpaccio.class, "hard") );
            Barkskin.conditionallyAppend( hero, hero.HT / 4, 1 );
            break;
        case 2:
            // 净化效果
            GLog.i( Messages.get(FrozenCarpaccio.class, "refresh") );
            PotionOfHealing.cure(hero);
            break;
        case 3:
            // 治疗效果
            GLog.i( Messages.get(FrozenCarpaccio.class, "better") );
            hero.HP = Math.min( hero.HP + hero.HT / 4, hero.HT );
            hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(hero.HT / 4), FloatingText.HEALING );
            break;
        // case 4: 无效果
    }
}
```

**效果概率分布**：
| 随机值 | 效果 | 概率 |
|--------|------|------|
| 0 | 隐形 | 20% |
| 1 | 树肤（防御+1/4最大生命） | 20% |
| 2 | 净化（清除负面效果） | 20% |
| 3 | 治疗（恢复1/4最大生命） | 20% |
| 4 | 无效果 | 20% |

---

### cook(MysteryMeat ingredient) [静态方法]

**可见性**：public static

**是否覆写**：否

**方法职责**：工厂方法，从神秘肉创建冷冻肉片。

**参数**：
- `ingredient` (MysteryMeat)：原料神秘肉

**返回值**：Food，冷冻肉片实例

**核心实现逻辑**：
```java
public static Food cook( MysteryMeat ingredient ) {
    FrozenCarpaccio result = new FrozenCarpaccio();
    result.quantity = ingredient.quantity();
    return result;
}
```

---

### value()

**可见性**：public

**是否覆写**：是，覆写自 Food

**方法职责**：计算物品价值。

**返回值**：int，10 * quantity

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `effect(Hero)` | 触发随机效果（可独立调用） |
| `cook(MysteryMeat)` | 工厂方法 |

### 继承的 API
继承自 Food 的所有公开方法。

## 9. 运行机制与调用链

### 创建时机
- 神秘肉受冻结效果时

### 效果触发流程
```
食用冷冻肉片
    ↓
satisfy(hero)
    ↓
super.satisfy() - 恢复饥饿
    ↓
effect(hero) - 随机效果
    ↓
Random.Int(5) 决定效果类型
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.food.frozencarpaccio.name` | 冷冻生肉片 | 物品名称 |
| `items.food.frozencarpaccio.invis` | 你看到自己的手隐形了！ | 隐形效果消息 |
| `items.food.frozencarpaccio.hard` | 你感到皮肤变硬了！ | 树肤效果消息 |
| `items.food.frozencarpaccio.refresh` | 神清气爽！ | 净化效果消息 |
| `items.food.frozencarpaccio.better` | 你感觉好多了！ | 治疗效果消息 |
| `items.food.frozencarpaccio.desc` | 这是份速冻生肉，只能切成薄片取食，而且意外的好吃。 | 物品描述 |
| `items.food.frozencarpaccio.discover_hint` | 你可使用另一种食物制作该物品。 | 发现提示 |

### 中文翻译来源
所有中文翻译来自 `core/src/main/assets/messages/items/items_zh.properties` 文件。

## 11. 使用示例

### 基本用法

```java
// 创建冷冻肉片
FrozenCarpaccio meat = new FrozenCarpaccio();

// 英雄食用（会触发随机效果）
meat.execute(hero, Food.AC_EAT);

// 独立触发效果（不食用食物）
FrozenCarpaccio.effect(hero);
```

### 从神秘肉转化

```java
MysteryMeat raw = new MysteryMeat();
raw.quantity(2);
Food frozen = FrozenCarpaccio.cook(raw);
```

## 12. 开发注意事项

### 状态依赖
- 效果依赖英雄的最大生命值（HT）

### 生命周期耦合
- 作为神秘肉的加工产品存在

### 常见陷阱
- `effect()` 是静态方法，可独立于食用行为调用
- 20% 概率无任何效果

## 13. 修改建议与扩展点

### 适合扩展的位置
- `effect()` 方法可扩展更多效果类型

### 不建议修改的位置
- 效果概率分布影响游戏平衡

### 扩展示例

```java
// 添加新效果类型
case 5:
    GLog.i("新效果！");
    Buff.affect(hero, Haste.class, 5f);
    break;
```

## 14. 事实核查清单

- [x] 是否已覆盖全部字段 - 已覆盖 image、energy
- [x] 是否已覆盖全部方法 - 已覆盖 satisfy()、value()、effect()、cook()
- [x] 是否已检查继承链与覆写关系 - 已说明继承自 Food
- [x] 是否已核对官方中文翻译 - 已从 items_zh.properties 获取
- [x] 是否存在任何推测性表述 - 无
- [x] 示例代码是否真实可用 - 示例基于实际 API
- [x] 是否遗漏资源/配置/本地化关联 - 已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点 - 已说明

---

**文档版本**：1.0  
**最后更新**：2026-03-27  
**基于源码版本**：Shattered Pixel Dungeon (GPL-3.0)