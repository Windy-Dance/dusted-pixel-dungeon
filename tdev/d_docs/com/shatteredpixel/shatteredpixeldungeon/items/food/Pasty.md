# Pasty 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/food/Pasty.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.food |
| **文件类型** | class |
| **继承关系** | extends Food |
| **代码行数** | 243 行 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Pasty 类实现"馅饼"食物，是一种特殊的节日食物。根据当前日期（节日）自动变化为不同形态，提供不同的食用效果。

### 系统定位
Pasty 是 Food 的复杂子类，实现了节日系统的食物表现。它是游戏中唯一会根据现实日期变化的物品。

### 不负责什么
- 不负责节日的判断逻辑（由 `Holiday` 类处理）
- 不负责节日效果的持续时间

## 3. 结构总览

### 主要成员概览
- **实例字段**：`image`、`energy`、`bones`（在初始化块和 reset() 设置）
- **覆写方法**：`reset()`、`eatSFX()`、`satisfy(Hero)`、`name()`、`desc()`、`value()`
- **内部类**：`FishLeftover`（春节剩余鱼肉）

### 主要逻辑块概览
- **节日检测**：`reset()` 根据 `Holiday.getCurrentHoliday()` 设置图标
- **节日效果**：`satisfy()` 根据节日类型触发不同效果
- **节日名称/描述**：`name()` 和 `desc()` 返回节日特定文案

### 支持的节日类型
| 节日 | 名称 | 效果 |
|------|------|------|
| 无节日 | 馅饼 | 无特殊效果 |
| 春节 | 清蒸荷叶鱼 | 恢复部分饥饿 + 余鱼物品 |
| 愚人节 | Yendor护符？ | 神器充能 + 音效 |
| 复活节 | 复活节彩蛋 | 神器充能 |
| 骄傲月 | 虹色药剂 | 魅惑相邻敌人 |
| 破碎地牢生日 | 绿色蛋糕 | 获得10%经验 |
| 万圣节 | 南瓜派 | 治疗5%生命 |
| 像素地牢生日 | 蓝色蛋糕 | 获得10%经验 |
| 冬季节日 | 拐杖糖 | 法杖充能 |
| 元旦 | 气泡药剂 | 获得10%护盾 |

## 4. 继承与协作关系

### 父类提供的能力
继承自 Food 类的完整食用功能。

### 覆写的方法
| 方法名 | 覆写内容 |
|--------|----------|
| `reset()` | 根据节日设置图标 |
| `eatSFX()` | 特定节日播放饮水音效 |
| `satisfy(Hero)` | 触发节日特定效果 |
| `name()` | 返回节日特定名称 |
| `desc()` | 返回节日特定描述 |
| `value()` | 返回 20 * quantity |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Holiday` | 节日判断 |
| `ArtifactRecharge` | 神器充能 |
| `ScrollOfRecharging` | 法杖充能 |
| `Barrier` | 护盾 Buff |
| `Charm` | 魅惑 Buff |
| `PotionOfExperience` | 经验计算 |

## 5. 字段/常量详解

### 实例字段（初始化块设置）
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `image` | int | 根据节日动态设置 | 物品图标 |
| `energy` | float | Hunger.STARVING (450f) | 饱腹值（春节特殊为300） |
| `bones` | boolean | true | 可出现在遗骸中 |

### 图标映射
| 节日 | 图标 |
|------|------|
| NONE | ItemSpriteSheet.PASTY |
| LUNAR_NEW_YEAR | ItemSpriteSheet.STEAMED_FISH |
| APRIL_FOOLS | ItemSpriteSheet.CHOC_AMULET |
| EASTER | ItemSpriteSheet.EASTER_EGG |
| PRIDE | ItemSpriteSheet.RAINBOW_POTION |
| SHATTEREDPD_BIRTHDAY | ItemSpriteSheet.SHATTERED_CAKE |
| HALLOWEEN | ItemSpriteSheet.PUMPKIN_PIE |
| PD_BIRTHDAY | ItemSpriteSheet.VANILLA_CAKE |
| WINTER_HOLIDAYS | ItemSpriteSheet.CANDY_CANE |
| NEW_YEARS | ItemSpriteSheet.SPARKLING_POTION |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认无参构造器。

### 初始化块
```java
{
    reset();
    energy = Hunger.STARVING;
    bones = true;
}
```

### reset() 方法
每次创建时调用，根据当前节日设置图标：
```java
@Override
public void reset() {
    super.reset();
    switch(Holiday.getCurrentHoliday()){
        case NONE: default:
            image = ItemSpriteSheet.PASTY;
            break;
        case LUNAR_NEW_YEAR:
            image = ItemSpriteSheet.STEAMED_FISH;
            break;
        // ... 其他节日
    }
}
```

## 7. 方法详解

### reset()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：根据当前节日重置物品状态。

**核心实现逻辑**：根据 `Holiday.getCurrentHoliday()` 设置对应的图标。

---

### eatSFX()

**可见性**：protected

**是否覆写**：是，覆写自 Food

**方法职责**：播放食用音效，特定节日播放饮水音效。

**核心实现逻辑**：
```java
@Override
protected void eatSFX() {
    switch(Holiday.getCurrentHoliday()){
        case PRIDE:
        case NEW_YEARS:
            Sample.INSTANCE.play( Assets.Sounds.DRINK );
            return;
    }
    super.eatSFX();
}
```

---

### satisfy(Hero hero)

**可见性**：protected

**是否覆写**：是，覆写自 Food

**方法职责**：恢复饥饿值并触发节日特定效果。

**核心实现逻辑**（部分）：
```java
@Override
protected void satisfy(Hero hero) {
    if (Holiday.getCurrentHoliday() == Holiday.LUNAR_NEW_YEAR){
        energy = Hunger.HUNGRY; // 春节只恢复300
    }

    super.satisfy(hero);
    
    switch(Holiday.getCurrentHoliday()){
        case LUNAR_NEW_YEAR:
            // 掉落余鱼物品
            FishLeftover left = new FishLeftover();
            if (!left.collect()){
                Dungeon.level.drop(left, hero.pos).sprite.drop();
            }
            break;
        case APRIL_FOOLS:
            Sample.INSTANCE.play(Assets.Sounds.MIMIC);
            // 继续执行充能效果
        case EASTER:
            ArtifactRecharge.chargeArtifacts(hero, 2f);
            ScrollOfRecharging.charge(hero);
            break;
        // ... 其他节日效果
    }
}
```

---

### name()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：返回节日特定名称。

---

### desc()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：返回节日特定描述。

---

### value()

**可见性**：public

**是否覆写**：是，覆写自 Food

**返回值**：int，20 * quantity

---

### FishLeftover 内部类

**用途**：春节鱼肉食用后的剩余部分。

**特点**：
- `energy` = Hunger.HUNGRY/2 (150)
- `value()` = 10 * quantity

## 8. 对外暴露能力

### 显式 API
继承自 Food 的所有公开方法。

### 内部辅助方法
- `reset()` 处理节日状态重置
- `FishLeftover` 内部类表示春节剩余鱼肉

## 9. 运行机制与调用链

### 创建时机
- 地牢中随机生成
- 创建时自动检测当前节日

### 节日效果触发
```
食用馅饼
    ↓
satisfy(hero)
    ↓
检测节日类型
    ↓
执行对应效果
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| `items.food.pasty.name` | 馅饼 |
| `items.food.pasty.fish_name` | 清蒸荷叶鱼 |
| `items.food.pasty.amulet_name` | Yendor护符？ |
| `items.food.pasty.egg_name` | 复活节彩蛋 |
| `items.food.pasty.rainbow_name` | 虹色药剂 |
| `items.food.pasty.shattered_name` | 绿色蛋糕 |
| `items.food.pasty.pie_name` | 南瓜派 |
| `items.food.pasty.vanilla_name` | 蓝色蛋糕 |
| `items.food.pasty.cane_name` | 拐杖糖 |
| `items.food.pasty.sparkling_name` | 气泡药剂 |
| `items.food.pasty.desc` | 这是份正宗康郡肉馅饼... |
| `items.food.pasty.fish_desc` | 被荷叶包裹...新年快乐！ |
| `items.food.pasty.amulet_desc` | 你终于找到它了...愚人节快乐！ |
| `items.food.pasty.egg_desc` | 一个硕大的巧克力蛋...复活节快乐！ |
| `items.food.pasty.rainbow_desc` | 这瓶多彩的药剂...节日快乐！ |
| `items.food.pasty.shattered_desc` | 这一大块香草蛋糕...祝破碎地牢生日快乐！ |
| `items.food.pasty.pie_desc` | 好大的一块南瓜派...万圣节快乐！ |
| `items.food.pasty.vanilla_desc` | 这一大块香草蛋糕...祝像素地牢生日快乐！ |
| `items.food.pasty.cane_desc` | 甜度爆表的巨型拐杖糖...假日快乐！ |
| `items.food.pasty.sparkling_desc` | 这瓶起泡的药剂...元旦快乐！ |

### FishLeftover 翻译
| 键名 | 中文翻译 |
|------|---------|
| `items.food.pasty$fishleftover.name` | 余鱼 |
| `items.food.pasty$fishleftover.eat_msg` | 吃起来还行。 |
| `items.food.pasty$fishleftover.desc` | 你上一顿吃剩的鱼... |

### 中文翻译来源
所有中文翻译来自 `core/src/main/assets/messages/items/items_zh.properties` 文件。

## 11. 使用示例

### 基本用法

```java
// 创建馅饼（自动检测节日）
Pasty pasty = new Pasty();

// 食用馅饼
pasty.execute(hero, Food.AC_EAT);
// 效果根据当前节日而不同
```

### 检查当前节日

```java
Holiday current = Holiday.getCurrentHoliday();
if (current == Holiday.LUNAR_NEW_YEAR) {
    // 春节特殊效果：获得余鱼
}
```

## 12. 开发注意事项

### 状态依赖
- 依赖 `Holiday` 类判断当前节日
- 节日效果在创建时确定，食用时执行

### 生命周期耦合
- 物品创建时锁定节日状态
- 春节效果会产生额外的余鱼物品

### 常见陷阱
- 节日检测使用系统时间，测试时需注意
- 春节馅饼的饱腹值较低但有余鱼

## 13. 修改建议与扩展点

### 适合扩展的位置
- `reset()` 方法添加新节日
- `satisfy()` 方法添加新节日效果

### 不建议修改的位置
- 节日效果的数值设置
- 已有节日的名称和描述

### 扩展示例

```java
// 添加新节日
case NEW_HOLIDAY:
    image = ItemSpriteSheet.NEW_HOLIDAY_ICON;
    // 在 satisfy() 中添加效果
    break;
```

## 14. 事实核查清单

- [x] 是否已覆盖全部字段 - 已覆盖 image、energy、bones
- [x] 是否已覆盖全部方法 - 已覆盖所有覆写方法及内部类
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