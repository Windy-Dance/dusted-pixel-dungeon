# ScrollOfMysticalEnergy 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/exotic/ScrollOfMysticalEnergy.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic |
| **文件类型** | class |
| **继承关系** | extends ExoticScroll |
| **代码行数** | 55 行 |
| **所属模块** | core |
| **官方中文名** | 魔能秘卷 |

## 2. 文件职责说明

### 核心职责
魔能秘卷是一种阅读型秘卷，阅读后为使用者提供神器充能效果，在一段时间内持续为所有神器充能。

### 系统定位
作为充能卷轴的升级版本，对应普通卷轴为充能卷轴（ScrollOfRecharging）。

### 不负责什么
- 不为法杖充能（由充能卷轴提供）

## 3. 结构总览

### 主要成员概览
- `icon`: 图标标识

### 主要逻辑块概览
- `doRead()`: 阅读效果，添加神器充能Buff

## 4. 继承与协作关系

### 父类提供的能力
从 ExoticScroll 继承：
- 鉴定状态共享机制
- 价值计算（基于充能卷轴 +30金币）
- 符文和图像设置

### 覆写的方法
| 方法 | 覆写目的 |
|------|----------|
| `doRead()` | 实现阅读效果：添加神器充能Buff |

### 依赖的关键类
- `ArtifactRecharge`: 神器充能Buff类
- `SpellSprite`: 法术精灵效果
- `ScrollOfRecharging`: 充能卷轴

## 5. 字段/常量详解

### 实例字段

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `icon` | int | ItemSpriteSheet.Icons.SCROLL_MYSTENRG | 物品图标标识 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过初始化块设置图标。

### 初始化块
```java
{
    icon = ItemSpriteSheet.Icons.SCROLL_MYSTENRG;
}
```

## 7. 方法详解

### doRead()

**可见性**：public

**是否覆写**：是，覆写自 ExoticScroll

**方法职责**：实现阅读效果，为使用者添加神器充能Buff。

**核心实现逻辑**：
```java
@Override
public void doRead() {
    detach(curUser.belongings.backpack);
    //append buff
    Buff.affect(curUser, ArtifactRecharge.class).set( 30 ).ignoreHornOfPlenty = false;

    Sample.INSTANCE.play( Assets.Sounds.READ );
    Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );
    
    SpellSprite.show( curUser, SpellSprite.CHARGE, 0, 1, 1 );
    identify();
    ScrollOfRecharging.charge(curUser);
    
    readAnimation();
}
```

**边界情况**：
- 充能量为30
- 不忽略丰饶之角

## 8. 对外暴露能力

### 显式 API
- `doRead()`: 阅读效果

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
- 通过炼金转换（充能卷轴 + 6能量）
- 通过 Generator 随机生成

### 系统流程位置
```
阅读 → doRead() → Buff.affect() → 神器充能效果
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.scrolls.exotic.scrollofmysticalenergy.name | 魔能秘卷 | 物品名称 |
| items.scrolls.exotic.scrollofmysticalenergy.desc | 奇异的魔法能量被禁锢在秘卷羊皮纸内，当这股能量被释放时会在短时间内持续为阅读者的所有神器充能。 | 物品描述 |

### 依赖的资源
- ItemSpriteSheet.Icons.SCROLL_MYSTENRG: 物品图标
- Assets.Sounds.READ: 阅读音效
- Assets.Sounds.CHARGEUP: 充能音效
- SpellSprite.CHARGE: 充能视觉效果

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法

```java
// 阅读魔能秘卷
ScrollOfMysticalEnergy scroll = new ScrollOfMysticalEnergy();
scroll.doRead(); // 为使用者添加神器充能效果

// 神器充能效果
// 充能量：30
// 效果：持续为所有神器充能
```

### 检查神器充能状态

```java
ArtifactRecharge recharge = hero.buff(ArtifactRecharge.class);
if (recharge != null) {
    // 英雄处于神器充能状态
}
```

## 12. 开发注意事项

### 状态依赖
- 充能量为30
- 对丰饶之角也生效

### 生命周期耦合
- 神器充能效果持续一定时间后消散

### 常见陷阱
1. **与充能卷轴区别**：充能卷轴为法杖充能，魔能秘卷为神器充能

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可修改充能量

### 不建议修改的位置
- 神器充能的核心逻辑

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：否
- [x] 是否明确说明了注意事项与扩展点：是