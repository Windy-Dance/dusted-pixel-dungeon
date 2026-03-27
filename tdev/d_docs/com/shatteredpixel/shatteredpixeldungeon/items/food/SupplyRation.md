# SupplyRation 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/food/SupplyRation.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.food |
| **文件类型** | class |
| **继承关系** | extends Food |
| **代码行数** | 76 行 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
SupplyRation 类实现"备用口粮"食物，是盗贼公会的专属食物。可快速食用，恢复少量生命，并为暗影斗篷充能。

### 系统定位
SupplyRation 是 Food 的特殊子类，专用于盗贼英雄的天赋系统。它是唯一能与神器系统交互的食物。

### 不负责什么
- 不负责暗影斗篷的充能逻辑（由 CloakOfShadows 类处理）
- 不负责天赋的解锁条件

## 3. 结构总览

### 主要成员概览
- **实例字段**：`image`、`energy`、`bones`（在初始化块设置）
- **覆写方法**：`eatingTime()`、`satisfy(Hero)`、`value()`

### 主要逻辑块概览
- **快速食用**：食用时间为 0 或 1 回合
- **治疗恢复**：恢复 5 点生命值
- **神器充能**：为暗影斗篷充能 1 点

### 生命周期/调用时机
- 盗贼天赋触发时生成
- 不通过地牢随机生成或商店购买

## 4. 继承与协作关系

### 父类提供的能力
继承自 Food 类的完整食用功能。

### 覆写的方法
| 方法名 | 覆写内容 |
|--------|----------|
| `eatingTime()` | 返回 0（有天赋）或 1（无天赋） |
| `satisfy(Hero)` | 添加治疗效果和神器充能 |
| `value()` | 返回 10 * quantity |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `CloakOfShadows` | 暗影斗篷神器 |
| `ScrollOfRecharging` | 法杖充能效果 |
| `FloatingText` | 浮动文本显示 |

## 5. 字段/常量详解

### 实例字段（初始化块设置）
| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.SUPPLY_RATION | 备用口粮图标 |
| `energy` | float | 2*Hunger.HUNGRY/3f (200f) | 饱腹值 |
| `bones` | boolean | false | 不出现在英雄遗骸中 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认无参构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.SUPPLY_RATION;
    energy = 2*Hunger.HUNGRY/3f; // 200 food value
    bones = false;
}
```

## 7. 方法详解

### eatingTime()

**可见性**：protected

**是否覆写**：是，覆写自 Food

**方法职责**：计算食用时间，实现快速食用。

**返回值**：float，0（有天赋）或 1（无天赋）

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

---

### satisfy(Hero hero)

**可见性**：protected

**是否覆写**：是，覆写自 Food

**方法职责**：恢复饥饿值、治疗生命、为暗影斗篷充能。

**参数**：
- `hero` (Hero)：食用者

**返回值**：void

**核心实现逻辑**：
```java
@Override
protected void satisfy(Hero hero) {
    super.satisfy(hero);

    // 治疗5点生命
    hero.HP = Math.min(hero.HP + 5, hero.HT);
    hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, "5", FloatingText.HEALING );

    // 为暗影斗篷充能
    CloakOfShadows cloak = hero.belongings.getItem(CloakOfShadows.class);
    if (cloak != null) {
        cloak.directCharge(1);
        ScrollOfRecharging.charge(hero);
    }
}
```

---

### value()

**可见性**：public

**是否覆写**：是，覆写自 Food

**返回值**：int，10 * quantity

## 8. 对外暴露能力

### 显式 API
继承自 Food 的所有公开方法。

### 效果总结
| 效果 | 数值 |
|------|------|
| 饱腹值恢复 | 200 |
| 生命恢复 | 5 |
| 暗影斗篷充能 | 1 |
| 食用时间 | 0-1 回合 |

## 9. 运行机制与调用链

### 创建时机
- 盗贼天赋触发时生成

### 效果触发流程
```
食用备用口粮
    ↓
satisfy(hero)
    ↓
super.satisfy() - 恢复200饥饿值
    ↓
hero.HP += 5 - 治疗生命
    ↓
cloak.directCharge(1) - 充能暗影斗篷
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.food.supplyration.name` | 备用口粮 | 物品名称 |
| `items.food.supplyration.desc` | 这是一包由盗贼公会存放，供其成员取用的口粮。这包口粮专门被设计成可快速食用的样式，并且能为成员的隐秘行动提供诸多帮助。尽管不能像正常口粮一样填饱你，它却能被快速食用，恢复少量生命，并且回复盗贼的暗影斗篷一点神器充能。 | 物品描述 |
| `items.food.supplyration.discover_hint` | 你可使用某项特定的英雄天赋找到该物品。 | 发现提示 |

### 中文翻译来源
所有中文翻译来自 `core/src/main/assets/messages/items/items_zh.properties` 文件。

## 11. 使用示例

### 基本用法

```java
// 创建备用口粮
SupplyRation ration = new SupplyRation();

// 英雄食用
ration.execute(hero, Food.AC_EAT);
// 效果：恢复200饥饿值 + 5生命 + 暗影斗篷充能1
```

### 检查暗影斗篷

```java
CloakOfShadows cloak = hero.belongings.getItem(CloakOfShadows.class);
if (cloak != null) {
    // 食用备用口粮会为斗篷充能
}
```

## 12. 开发注意事项

### 状态依赖
- 暗影斗篷充能仅在英雄拥有该神器时生效
- 治疗效果不会超过最大生命值

### 生命周期耦合
- 通过盗贼天赋获取
- 不出现在英雄遗骸中

### 常见陷阱
- 非盗贼英雄食用时没有神器充能效果
- `bones = false` 意味着不会在遗骸中出现

## 13. 修改建议与扩展点

### 适合扩展的位置
- `satisfy()` 方法可修改治疗量或充能值

### 不建议修改的位置
- 与暗影斗篷的绑定是设计决策
- 充能值影响神器平衡

## 14. 事实核查清单

- [x] 是否已覆盖全部字段 - 已覆盖 image、energy、bones
- [x] 是否已覆盖全部方法 - 已覆盖 eatingTime()、satisfy()、value()
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