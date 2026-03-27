# MysteryMeat 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/food/MysteryMeat.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.food |
| **文件类型** | class |
| **继承关系** | extends Food |
| **代码行数** | 92 行 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
MysteryMeat 类实现"神秘的肉"食物，是一种危险的食物。食用时会随机触发负面效果（燃烧、缠绕、中毒或减速），但可以通过加工转化为安全食品。

### 系统定位
MysteryMeat 是 Food 的特殊子类，作为肉类食物加工链的起点。它代表了未经处理的原材料状态。

### 不负责什么
- 不负责肉类到安全食品的转化（由子类或外部机制处理）
- 不负责敌人的掉落逻辑

## 3. 结构总览

### 主要成员概览
- **实例字段**：`image`、`energy`（在初始化块设置）
- **覆写方法**：`satisfy(Hero)`、`value()`
- **静态方法**：`effect(Hero)`
- **内部类**：`PlaceHolder`（占位符物品）

### 主要逻辑块概览
- **随机负面效果**：食用时 4/5 概率触发以下效果之一：燃烧、缠绕、中毒、减速
- **加工转化**：可通过燃烧→烤肉、冻结→冷冻肉片、炖煮→炖肉

### 生命周期/调用时机
- 敌人掉落时生成
- 加工后转化为安全食品

## 4. 继承与协作关系

### 父类提供的能力
继承自 Food 类的完整食用功能。

### 覆写的方法
| 方法名 | 覆写内容 |
|--------|----------|
| `satisfy(Hero)` | 添加随机负面效果 |
| `value()` | 返回 5 * quantity |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Burning` | 燃烧 Buff |
| `Roots` | 缠绕 Buff |
| `Poison` | 中毒 Buff |
| `Slow` | 减速 Buff |
| `Random` | 随机数生成 |

### 可转化的安全食品
| 加工方式 | 产物 |
|----------|------|
| 燃烧 | ChargrilledMeat（烤肉） |
| 冻结 | FrozenCarpaccio（冷冻生肉片） |
| 炖煮（炼金） | StewedMeat（炖肉） |

## 5. 字段/常量详解

### 实例字段（初始化块设置）
| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.MEAT | 神秘肉图标 |
| `energy` | float | Hunger.HUNGRY/2f (约150) | 饱腹值 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认无参构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.MEAT;
    energy = Hunger.HUNGRY/2f;
}
```

## 7. 方法详解

### satisfy(Hero hero)

**可见性**：protected

**是否覆写**：是，覆写自 Food

**方法职责**：恢复饥饿值并触发随机负面效果。

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

**方法职责**：对英雄施加随机负面效果。

**参数**：
- `hero` (Hero)：效果目标

**返回值**：void

**核心实现逻辑**：
```java
public static void effect(Hero hero){
    switch (Random.Int( 5 )) {
        case 0:
            // 燃烧
            GLog.w( Messages.get(MysteryMeat.class, "hot") );
            Buff.affect( hero, Burning.class ).reignite( hero );
            break;
        case 1:
            // 缠绕
            GLog.w( Messages.get(MysteryMeat.class, "legs") );
            Buff.prolong( hero, Roots.class, Roots.DURATION*2f );
            break;
        case 2:
            // 中毒
            GLog.w( Messages.get(MysteryMeat.class, "not_well") );
            Buff.affect( hero, Poison.class ).set( hero.HT / 5 );
            break;
        case 3:
            // 减速
            GLog.w( Messages.get(MysteryMeat.class, "stuffed") );
            Buff.prolong( hero, Slow.class, Slow.DURATION );
            break;
        // case 4: 无效果
    }
}
```

**效果概率分布**：
| 随机值 | 效果 | 概率 |
|--------|------|------|
| 0 | 燃烧 | 20% |
| 1 | 缠绕（双倍时长） | 20% |
| 2 | 中毒（1/5最大生命值伤害） | 20% |
| 3 | 减速 | 20% |
| 4 | 无效果 | 20% |

---

### value()

**可见性**：public

**是否覆写**：是，覆写自 Food

**方法职责**：计算物品价值。

**返回值**：int，5 * quantity

---

### PlaceHolder 内部类

**用途**：作为炼金界面中的占位符，代表任意类型的肉类。

**特点**：
- 使用特殊的 `FOOD_HOLDER` 图标
- `isSimilar()` 接受所有肉类类型
- `info()` 返回空字符串

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `effect(Hero)` | 触发随机负面效果（可独立调用） |

### 继承的 API
继承自 Food 的所有公开方法。

## 9. 运行机制与调用链

### 创建时机
- 敌人掉落

### 食用效果流程
```
食用神秘的肉
    ↓
satisfy(hero)
    ↓
super.satisfy() - 恢复饥饿
    ↓
effect(hero) - 随机负面效果
    ↓
Random.Int(5) 决定效果类型
```

### 加工转化流程
```
神秘的肉
    ├── Burning Buff → ChargrilledMeat
    ├── 冻结效果 → FrozenCarpaccio
    └── 炼金锅 → StewedMeat
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.food.mysterymeat.name` | 神秘的肉 | 物品名称 |
| `items.food.mysterymeat.eat_msg` | 这玩意儿吃起来很...奇怪。 | 食用成功消息 |
| `items.food.mysterymeat.hot` | 嗷！好烫！ | 燃烧效果消息 |
| `items.food.mysterymeat.legs` | 你的腿没有知觉了！ | 缠绕效果消息 |
| `items.food.mysterymeat.not_well` | 你感觉...不太好。 | 中毒效果消息 |
| `items.food.mysterymeat.stuffed` | 你吃撑了。 | 减速效果消息 |
| `items.food.mysterymeat.desc` | 想吃可以，后果自腹！ | 物品描述 |
| `items.food.mysterymeat.discover_hint` | 你可从某种敌人的掉落物中获得该物品。 | 发现提示 |
| `items.food.mysterymeat$placeholder.name` | 肉 | 占位符名称 |

### 中文翻译来源
所有中文翻译来自 `core/src/main/assets/messages/items/items_zh.properties` 文件。

## 11. 使用示例

### 基本用法

```java
// 创建神秘的肉
MysteryMeat meat = new MysteryMeat();

// 英雄食用（可能触发负面效果）
meat.execute(hero, Food.AC_EAT);
```

### 检查处理方式

```java
// 神秘的肉应该先处理再食用
// 方式1：燃烧
if (hero.buff(Burning.class) != null) {
    // 燃烧中会自动转化为烤肉
}

// 方式2：冻结
// 通过冻结效果转化为冷冻肉片

// 方式3：炼金
StewedMeat stewed = new StewedMeat(); // 通过炼金配方
```

## 12. 开发注意事项

### 状态依赖
- 效果依赖英雄的最大生命值（HT）

### 生命周期耦合
- 是肉类加工链的起点

### 常见陷阱
- 食用时有 80% 概率受到负面效果
- 应优先加工处理

## 13. 修改建议与扩展点

### 适合扩展的位置
- `effect()` 方法可扩展更多效果类型

### 不建议修改的位置
- 效果概率和强度影响游戏平衡

## 14. 事实核查清单

- [x] 是否已覆盖全部字段 - 已覆盖 image、energy
- [x] 是否已覆盖全部方法 - 已覆盖 satisfy()、value()、effect() 及 PlaceHolder 内部类
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