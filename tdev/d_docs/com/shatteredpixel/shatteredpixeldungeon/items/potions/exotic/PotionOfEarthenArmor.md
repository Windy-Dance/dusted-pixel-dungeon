# PotionOfEarthenArmor 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/potions/exotic/PotionOfEarthenArmor.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic |
| **文件类型** | class |
| **继承关系** | extends ExoticPotion |
| **代码行数** | 41 行 |
| **所属模块** | core |
| **官方中文名** | 大地护甲合剂 |

## 2. 文件职责说明

### 核心职责
大地护甲合剂是一种饮用型秘卷/合剂，饮用后为使用者提供树肤Buff，增加防御力。

### 系统定位
作为麻痹药剂的升级版本，对应普通药剂为麻痹药剂（PotionOfParalyticGas）。不同于麻痹药剂的投掷效果，大地护甲合剂提供自身防御增益。

### 不负责什么
- 不造成麻痹效果
- 不影响敌人

## 3. 结构总览

### 主要成员概览
- `icon`: 图标标识

### 主要逻辑块概览
- `apply()`: 饮用效果，添加树肤Buff

## 4. 继承与协作关系

### 父类提供的能力
从 ExoticPotion 继承：
- 鉴定状态共享机制
- 价值计算（基于麻痹药剂 +20金币）
- 颜色和图像设置

### 覆写的方法
| 方法 | 覆写目的 |
|------|----------|
| `apply(Hero)` | 实现饮用效果：添加树肤Buff |

### 依赖的关键类
- `Barkskin`: 树肤Buff类

## 5. 字段/常量详解

### 实例字段

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `icon` | int | ItemSpriteSheet.Icons.POTION_EARTHARMR | 物品图标标识 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过初始化块设置图标。

### 初始化块
```java
{
    icon = ItemSpriteSheet.Icons.POTION_EARTHARMR;
}
```

## 7. 方法详解

### apply(Hero)

**可见性**：public

**是否覆写**：是，覆写自 ExoticPotion

**方法职责**：实现饮用效果，为英雄添加树肤Buff。

**参数**：
- `hero` (Hero)：饮用者（英雄）

**返回值**：void

**前置条件**：无

**副作用**：
- 鉴定药剂
- 添加树肤Buff

**核心实现逻辑**：
```java
@Override
public void apply( Hero hero ) {
    identify();
    
    Barkskin.conditionallyAppend( hero, 2 + hero.lvl/3, 50 );
}
```

**边界情况**：
- 树肤等级 = 2 + 英雄等级/3
- 树肤持续回合 = 50

## 8. 对外暴露能力

### 显式 API
- `apply(Hero)`: 饮用效果

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
- 通过炼金转换（麻痹药剂 + 4能量）
- 通过 Generator 随机生成

### 调用者
- 英雄饮用时调用 `apply()`

### 系统流程位置
```
饮用 → apply() → identify() → Barkskin.conditionallyAppend()
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.potions.exotic.potionofearthenarmor.name | 大地护甲合剂 | 物品名称 |
| items.potions.exotic.potionofearthenarmor.desc | 与麻痹药剂不同的是，饮用这瓶合剂能够使使用者的皮肤硬化，在一段时间内形成一道天然护甲。 | 物品描述 |

### 依赖的资源
- ItemSpriteSheet.Icons.POTION_EARTHARMR: 物品图标

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法

```java
// 饮用大地护甲合剂
PotionOfEarthenArmor potion = new PotionOfEarthenArmor();
potion.apply(hero); // 为英雄添加树肤Buff

// 树肤效果
// 等级 = 2 + hero.lvl/3
// 持续回合 = 50
// 效果：增加防御力
```

### 树肤等级计算

```java
// 假设英雄等级为10
int barkskinLevel = 2 + 10/3; // = 2 + 3 = 5
// 树肤提供额外的防御等级
```

## 12. 开发注意事项

### 状态依赖
- 树肤等级随英雄等级增加

### 生命周期耦合
- 树肤持续50回合

### 常见陷阱
1. **等级计算**：整数除法，hero.lvl/3 向下取整

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可修改树肤等级公式
- 可修改持续时间

### 不建议修改的位置
- 树肤的基础逻辑

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：否
- [x] 是否明确说明了注意事项与扩展点：是