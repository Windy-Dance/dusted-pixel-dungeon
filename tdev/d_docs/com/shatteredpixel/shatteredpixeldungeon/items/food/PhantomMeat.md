# PhantomMeat 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/food/PhantomMeat.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.food |
| **文件类型** | class |
| **继承关系** | extends Food |
| **代码行数** | 62 行 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
PhantomMeat 类实现"幻影鱼肉"食物，是一种从幻影食人鱼获得的珍贵食物。食用后提供多种强大的防御性增益效果。

### 系统定位
PhantomMeat 是 Food 的高级子类，作为游戏中最高级的食物之一。它提供独特的复合增益效果组合。

### 不负责什么
- 不负责幻影食人鱼的掉落逻辑
- 不负责各 Buff 的具体效果实现

## 3. 结构总览

### 主要成员概览
- **实例字段**：`image`、`energy`（在初始化块设置）
- **覆写方法**：`satisfy(Hero)`、`value()`
- **静态方法**：`effect(Hero)`

### 主要逻辑块概览
- **高饱腹值**：energy = Hunger.STARVING (450f)
- **复合增益**：隐形 + 树肤 + 治疗 + 净化

### 生命周期/调用时机
- 幻影食人鱼掉落
- 作为高级食物食用

## 4. 继承与协作关系

### 父类提供的能力
继承自 Food 类的完整食用功能。

### 覆写的方法
| 方法名 | 覆写内容 |
|--------|----------|
| `satisfy(Hero)` | 添加复合增益效果 |
| `value()` | 返回 30 * quantity |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Invisibility` | 隐形 Buff |
| `Barkskin` | 树肤 Buff |
| `PotionOfHealing` | 治疗和净化效果 |
| `FloatingText` | 浮动文本显示 |

## 5. 字段/常量详解

### 实例字段（初始化块设置）
| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.PHANTOM_MEAT | 幻影鱼肉图标 |
| `energy` | float | Hunger.STARVING (450f) | 饱腹值，完全填饱 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认无参构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.PHANTOM_MEAT;
    energy = Hunger.STARVING;
}
```

## 7. 方法详解

### satisfy(Hero hero)

**可见性**：protected

**是否覆写**：是，覆写自 Food

**方法职责**：恢复饥饿值并触发复合增益效果。

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

**方法职责**：对英雄施加所有增益效果。

**参数**：
- `hero` (Hero)：效果目标

**返回值**：void

**核心实现逻辑**：
```java
public static void effect(Hero hero){
    // 树肤效果（防御增益）
    Barkskin.conditionallyAppend( hero, hero.HT / 4, 1 );
    
    // 隐形效果
    Buff.affect( hero, Invisibility.class, Invisibility.DURATION );
    
    // 治疗效果（恢复1/4最大生命）
    hero.HP = Math.min( hero.HP + hero.HT / 4, hero.HT );
    hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(hero.HT / 4), FloatingText.HEALING );
    
    // 净化效果（清除负面效果）
    PotionOfHealing.cure(hero);
}
```

**效果总结**：
| 效果 | 数值/时长 |
|------|----------|
| 树肤 | +1/4最大生命防御 |
| 隐形 | Invisibility.DURATION |
| 治疗 | 1/4最大生命值 |
| 净化 | 清除所有负面效果 |

---

### value()

**可见性**：public

**是否覆写**：是，覆写自 Food

**方法职责**：计算物品价值。

**返回值**：int，30 * quantity

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `effect(Hero)` | 触发所有增益效果（可独立调用） |

### 继承的 API
继承自 Food 的所有公开方法。

## 9. 运行机制与调用链

### 创建时机
- 幻影食人鱼掉落

### 效果触发流程
```
食用幻影鱼肉
    ↓
satisfy(hero)
    ↓
super.satisfy() - 恢复450饥饿值
    ↓
effect(hero) - 全部增益效果
    ├── 树肤
    ├── 隐形
    ├── 治疗
    └── 净化
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.food.phantommeat.name` | 幻影鱼肉 | 物品名称 |
| `items.food.phantommeat.desc` | 这块从幻影食人鱼身上切下的大块鱼肉呈半透明状，闪烁着奇光。这块充满魔力的肉无须烹饪即可食用，不但能完全填饱你的肚子，而且能赋予多种防御性增益。食用后，它会为你提供隐形、树肤和少量治疗，并净化大部分有害效果。 | 物品描述 |
| `items.food.phantommeat.discover_hint` | 你可从某种敌人的掉落物中获得该物品。 | 发现提示 |

### 中文翻译来源
所有中文翻译来自 `core/src/main/assets/messages/items/items_zh.properties` 文件。

## 11. 使用示例

### 基本用法

```java
// 创建幻影鱼肉
PhantomMeat meat = new PhantomMeat();

// 英雄食用
meat.execute(hero, Food.AC_EAT);
// 效果：完全填饱 + 树肤 + 隐形 + 治疗 + 净化
```

### 独立触发效果

```java
// 不食用食物，仅触发效果
PhantomMeat.effect(hero);
```

## 12. 开发注意事项

### 状态依赖
- 效果依赖英雄的最大生命值（HT）
- 所有效果同时生效，不可分割

### 生命周期耦合
- 仅从幻影食人鱼获得
- 是游戏中最强的防御性食物

### 常见陷阱
- `effect()` 是静态方法，可独立于食用行为调用
- 净化效果会清除所有负面效果，包括某些有用的

## 13. 修改建议与扩展点

### 适合扩展的位置
- `effect()` 方法可调整效果组合

### 不建议修改的位置
- 效果组合非常强大，修改需谨慎
- 价值 (30) 是所有食物中最高的之一

## 14. 事实核查清单

- [x] 是否已覆盖全部字段 - 已覆盖 image、energy
- [x] 是否已覆盖全部方法 - 已覆盖 satisfy()、value()、effect()
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