# PotionOfStamina 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/potions/exotic/PotionOfStamina.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic |
| **文件类型** | class |
| **继承关系** | extends ExoticPotion |
| **代码行数** | 44 行 |
| **所属模块** | core |
| **官方中文名** | 精力回复合剂 |

## 2. 文件职责说明

### 核心职责
精力回复合剂是一种饮用型秘卷/合剂，饮用后为使用者提供精力Buff，在较长时间内提高移动速度。

### 系统定位
作为极速药剂的升级版本，对应普通药剂为极速药剂（PotionOfHaste）。提供更持久的移动速度增益。

### 不负责什么
- 不提供攻击速度加成
- 不立即恢复任何资源

## 3. 结构总览

### 主要成员概览
- `icon`: 图标标识

### 主要逻辑块概览
- `apply()`: 饮用效果，添加精力Buff

## 4. 继承与协作关系

### 父类提供的能力
从 ExoticPotion 继承：
- 鉴定状态共享机制
- 价值计算（基于极速药剂 +20金币）
- 颜色和图像设置

### 覆写的方法
| 方法 | 覆写目的 |
|------|----------|
| `apply(Hero)` | 实现饮用效果：添加精力Buff |

### 依赖的关键类
- `Stamina`: 精力Buff类
- `SpellSprite`: 法术精灵效果

## 5. 字段/常量详解

### 实例字段

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `icon` | int | ItemSpriteSheet.Icons.POTION_STAMINA | 物品图标标识 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过初始化块设置图标。

### 初始化块
```java
{
    icon = ItemSpriteSheet.Icons.POTION_STAMINA;
}
```

## 7. 方法详解

### apply(Hero)

**可见性**：public

**是否覆写**：是，覆写自 ExoticPotion

**方法职责**：实现饮用效果，为英雄添加精力Buff。

**参数**：
- `hero` (Hero)：饮用者（英雄）

**返回值**：void

**前置条件**：无

**副作用**：
- 鉴定药剂
- 添加精力Buff
- 显示视觉效果

**核心实现逻辑**：
```java
@Override
public void apply(Hero hero) {
    identify();
    
    Buff.prolong(hero, Stamina.class, Stamina.DURATION);
    SpellSprite.show(hero, SpellSprite.HASTE, 0.5f, 1, 0.5f);
}
```

**边界情况**：
- 精力持续时间为 Stamina.DURATION
- 视觉效果使用 SpellSprite.HASTE

## 8. 对外暴露能力

### 显式 API
- `apply(Hero)`: 饮用效果

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
- 通过炼金转换（极速药剂 + 4能量）
- 通过 Generator 随机生成

### 调用者
- 英雄饮用时调用 `apply()`

### 系统流程位置
```
饮用 → apply() → identify() → Buff.prolong() → SpellSprite.show()
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.potions.exotic.potionofstamina.name | 精力回复合剂 | 物品名称 |
| items.potions.exotic.potionofstamina.desc | 喝下这甜到掉牙的奇怪液体后，体内会爆发一股巨大的能量，让你可以在长时间内飞速奔跑。 | 物品描述 |

### 依赖的资源
- ItemSpriteSheet.Icons.POTION_STAMINA: 物品图标
- SpellSprite.HASTE: 急速视觉效果

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法

```java
// 饮用精力回复合剂
PotionOfStamina potion = new PotionOfStamina();
potion.apply(hero); // 为英雄添加精力Buff

// 精力效果
// 持续时间：Stamina.DURATION
// 效果：提高移动速度
```

### 检查精力状态

```java
// 检查是否有精力Buff
Stamina stamina = hero.buff(Stamina.class);
if (stamina != null) {
    // 英雄处于精力状态
    float remaining = stamina.visualcooldown();
}
```

## 12. 开发注意事项

### 状态依赖
- 持续时间由 Stamina.DURATION 定义
- 移动速度加成在 Stamina 类中定义

### 生命周期耦合
- 精力效果随时间消散

### 常见陷阱
1. **与极速药剂区别**：极速药剂提供短时间爆发，精力合剂提供长时间持续

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可修改持续时间
- 可在 Stamina 类中修改速度加成

### 不建议修改的位置
- Buff 添加的基础逻辑

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：否
- [x] 是否明确说明了注意事项与扩展点：是