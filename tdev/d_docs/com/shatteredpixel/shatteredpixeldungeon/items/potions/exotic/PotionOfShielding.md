# PotionOfShielding 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/potions/exotic/PotionOfShielding.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic |
| **文件类型** | class |
| **继承关系** | extends ExoticPotion |
| **代码行数** | 52 行 |
| **所属模块** | core |
| **官方中文名** | 奥术护盾合剂 |

## 2. 文件职责说明

### 核心职责
奥术护盾合剂是一种饮用型秘卷/合剂，饮用后为使用者提供护盾，抵挡伤害。护盾量约为英雄最大生命值的60%+10。

### 系统定位
作为治疗药剂的升级版本，对应普通药剂为治疗药剂（PotionOfHealing）。提供即时防护而非治疗。

### 不负责什么
- 不治疗生命值
- 不清除毒素

## 3. 结构总览

### 主要成员概览
- `icon`: 图标标识

### 主要逻辑块概览
- `apply()`: 饮用效果，添加护盾Buff

## 4. 继承与协作关系

### 父类提供的能力
从 ExoticPotion 继承：
- 鉴定状态共享机制
- 价值计算（基于治疗药剂 +20金币）
- 颜色和图像设置

### 覆写的方法
| 方法 | 覆写目的 |
|------|----------|
| `apply(Hero)` | 实现饮用效果：添加护盾Buff |

### 依赖的关键类
- `Barrier`: 护盾Buff类
- `Challenges`: 挑战模式
- `PotionOfHealing`: 治疗药剂（用于禁疗挑战处理）

## 5. 字段/常量详解

### 实例字段

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `icon` | int | ItemSpriteSheet.Icons.POTION_SHIELDING | 物品图标标识 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过初始化块设置图标。

### 初始化块
```java
{
    icon = ItemSpriteSheet.Icons.POTION_SHIELDING;
}
```

## 7. 方法详解

### apply(Hero)

**可见性**：public

**是否覆写**：是，覆写自 ExoticPotion

**方法职责**：实现饮用效果，为英雄添加护盾。

**参数**：
- `hero` (Hero)：饮用者（英雄）

**返回值**：void

**前置条件**：无

**副作用**：
- 鉴定药剂
- 添加护盾Buff或触发禁疗挑战效果

**核心实现逻辑**：
```java
@Override
public void apply(Hero hero) {
    identify();

    if (Dungeon.isChallenged(Challenges.NO_HEALING)){
        PotionOfHealing.pharmacophobiaProc(hero);
    } else {
        //~75% of a potion of healing
        Buff.affect(hero, Barrier.class).setShield((int) (0.6f * hero.HT + 10));
        hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString((int) (0.6f * hero.HT + 10)), FloatingText.SHIELDING );
    }
}
```

**边界情况**：
- 禁疗挑战模式下会触发 pharmacophobia 效果
- 护盾量 = (int)(0.6f * hero.HT + 10)

## 8. 对外暴露能力

### 显式 API
- `apply(Hero)`: 饮用效果

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
- 通过炼金转换（治疗药剂 + 4能量）
- 通过 Generator 随机生成

### 调用者
- 英雄饮用时调用 `apply()`

### 系统流程位置
```
饮用 → apply() → identify() → 
检查禁疗挑战 → 
  是：pharmacophobiaProc()
  否：添加护盾Buff
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.potions.exotic.potionofshielding.name | 奥术护盾合剂 | 物品名称 |
| items.potions.exotic.potionofshielding.desc | 与治疗药剂不同的是，饮用这瓶合剂能够使周身被护盾环绕，抵挡所受到的伤害。 | 物品描述 |

### 依赖的资源
- ItemSpriteSheet.Icons.POTION_SHIELDING: 物品图标
- FloatingText.SHIELDING: 护盾浮动文字效果

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法

```java
// 饮用奥术护盾合剂
PotionOfShielding potion = new PotionOfShielding();
potion.apply(hero); // 为英雄添加护盾

// 护盾量计算
// shieldAmount = (int)(0.6f * hero.HT + 10)
// 例如：英雄HT=100，护盾量 = 0.6*100+10 = 70
```

### 护盾Buff操作

```java
// 检查护盾
Barrier barrier = hero.buff(Barrier.class);
if (barrier != null) {
    int shieldAmount = barrier.shielding();
}
```

## 12. 开发注意事项

### 状态依赖
- 护盾量依赖英雄最大生命值
- 禁疗挑战会改变效果

### 生命周期耦合
- 护盾在受到伤害时消耗

### 常见陷阱
1. **禁疗挑战**：会触发 pharmacophobia 效果而非提供护盾
2. **护盾叠加**：护盾可以叠加但会被新的护盾值覆盖

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可修改护盾量计算公式
- 可添加护盾持续时间限制

### 不建议修改的位置
- 禁疗挑战的处理逻辑

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：否
- [x] 是否明确说明了注意事项与扩展点：是