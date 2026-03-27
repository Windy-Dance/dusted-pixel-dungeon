# PotionOfMagicalSight 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/potions/exotic/PotionOfMagicalSight.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic |
| **文件类型** | class |
| **继承关系** | extends ExoticPotion |
| **代码行数** | 46 行 |
| **所属模块** | core |
| **官方中文名** | 魔能透视合剂 |

## 2. 文件职责说明

### 核心职责
魔能透视合剂是一种饮用型秘卷/合剂，饮用后赋予使用者魔法视野，能够看穿12格范围内的墙壁。

### 系统定位
作为灵视药剂的升级版本，对应普通药剂为灵视药剂（PotionOfMindVision）。提供更强大的视野能力。

### 不负责什么
- 不提供敌人位置感知（由灵视药剂提供）

## 3. 结构总览

### 主要成员概览
- `icon`: 图标标识

### 主要逻辑块概览
- `apply()`: 饮用效果，添加魔法视野Buff

## 4. 继承与协作关系

### 父类提供的能力
从 ExoticPotion 继承：
- 鉴定状态共享机制
- 价值计算（基于灵视药剂 +20金币）
- 颜色和图像设置

### 覆写的方法
| 方法 | 覆写目的 |
|------|----------|
| `apply(Hero)` | 实现饮用效果：添加魔法视野Buff |

### 依赖的关键类
- `MagicalSight`: 魔法视野Buff类
- `SpellSprite`: 法术精灵效果
- `Dungeon`: 地牢实例

## 5. 字段/常量详解

### 实例字段

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `icon` | int | ItemSpriteSheet.Icons.POTION_MAGISIGHT | 物品图标标识 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过初始化块设置图标。

### 初始化块
```java
{
    icon = ItemSpriteSheet.Icons.POTION_MAGISIGHT;
}
```

## 7. 方法详解

### apply(Hero)

**可见性**：public

**是否覆写**：是，覆写自 ExoticPotion

**方法职责**：实现饮用效果，为英雄添加魔法视野Buff。

**参数**：
- `hero` (Hero)：饮用者（英雄）

**返回值**：void

**前置条件**：无

**副作用**：
- 鉴定药剂
- 添加魔法视野Buff
- 显示视觉效果
- 立即更新视野

**核心实现逻辑**：
```java
@Override
public void apply(Hero hero) {
    identify();
    Buff.prolong(hero, MagicalSight.class, MagicalSight.DURATION);
    SpellSprite.show(hero, SpellSprite.VISION);
    Dungeon.observe();
}
```

**边界情况**：
- 魔法视野持续时间为 MagicalSight.DURATION
- 视野范围为12格

## 8. 对外暴露能力

### 显式 API
- `apply(Hero)`: 饮用效果

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
- 通过炼金转换（灵视药剂 + 4能量）
- 通过 Generator 随机生成

### 调用者
- 英雄饮用时调用 `apply()`

### 系统流程位置
```
饮用 → apply() → identify() → Buff.prolong() → SpellSprite.show() → Dungeon.observe()
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.potions.exotic.potionofmagicalsight.name | 魔能透视合剂 | 物品名称 |
| items.potions.exotic.potionofmagicalsight.desc | 饮用这瓶合剂后，你的五感将被提高到一种无法想象的地步，使你能看穿12格以内的墙壁，洞察藏在墙后的事物！ | 物品描述 |

### 依赖的资源
- ItemSpriteSheet.Icons.POTION_MAGISIGHT: 物品图标
- SpellSprite.VISION: 视觉效果

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法

```java
// 饮用魔能透视合剂
PotionOfMagicalSight potion = new PotionOfMagicalSight();
potion.apply(hero); // 为英雄添加魔法视野

// 魔法视野效果
// 持续时间：MagicalSight.DURATION
// 视野范围：12格（可穿透墙壁）
```

## 12. 开发注意事项

### 状态依赖
- 依赖 MagicalSight.DURATION 定义持续时间
- 视野范围在 MagicalSight 类中定义

### 生命周期耦合
- 饮用后立即更新视野（Dungeon.observe()）

### 常见陷阱
1. **视野更新**：添加Buff后必须调用 Dungeon.observe() 才能立即生效

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可修改持续时间
- 可在 MagicalSight 类中修改视野范围

### 不建议修改的位置
- 视野更新的调用

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：否
- [x] 是否明确说明了注意事项与扩展点：是