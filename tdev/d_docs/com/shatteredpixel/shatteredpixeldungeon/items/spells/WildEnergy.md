# 强能结晶 (WildEnergy)

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\items\spells\WildEnergy.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.spells |
| **文件类型** | class |
| **继承关系** | extends TargetedSpell |
| **代码行数** | 104 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
强能结晶是一个充能类法术，当施放时会为英雄的法杖与佩戴着的神器充能，但同时也会随机触发一种诅咒法杖效果。幸运的是使用者可以指定这种诅咒魔法的施放方向。

### 系统定位
作为TargetedSpell的子类，强能结晶在游戏的充能和状态管理系统中扮演双重角色。它与法杖充能系统、神器充能系统、诅咒法杖系统和视觉效果系统深度集成。

### 不负责什么
- 不直接处理战斗伤害计算
- 不提供永久性的装备强化
- 不涉及经济系统或商店交互

## 3. 结构总览

### 主要成员概览
- `image`: 物品图标（ItemSpriteSheet.WILD_ENERGY）
- `usesTargeting`: 目标选择标记（true）
- `talentChance`: 天赋触发概率（1/5）
- `Recipe`: 内部类，定义合成配方
- 继承自TargetedSpell的targeting和effect机制

### 主要逻辑块概览
- `fx()`: 重写视觉效果，使用诅咒法杖的效果
- `affectTarget()`: 处理充能和诅咒效果的核心逻辑
- 合成配方：使用充能卷轴和邪能碎片

### 生命周期/调用时机
- 玩家选择施放 → 显示目标选择界面
- 选择目标位置 → 射击弹道 → 触发诅咒效果并执行充能
- 消耗强能结晶并触发天赋

## 4. 继承与协作关系

### 父类提供的能力
从TargetedSpell继承：
- `onCast()`: 打开目标选择界面
- 弹道系统和碰撞检测
- `onSpellused()`: 处理消耗和天赋触发

从Spell继承：
- `AC_CAST`动作常量
- `talentFactor`, `talentChance`天赋相关字段
- 基础物品属性和方法

### 覆写的方法
- `fx(Ballistica bolt, Callback callback)`: 重写视觉效果
- `affectTarget(Ballistica bolt, Hero hero)`: 实现核心充能和诅咒逻辑
- `value()`, `energyVal()`: 自定义价值计算

### 实现的接口契约
通过继承Item间接实现Serializable接口

### 依赖的关键类
- `CursedWand`: 诅咒法杖效果复用
- `ScrollOfRecharging`: 充能效果复用
- `Recharging`, `ArtifactRecharge`: 充能Buff系统
- `SpellSprite`: 视觉效果
- `Sample`: 音效播放

### 使用者
- 游戏玩家通过背包界面使用
- 合成系统通过Recipe创建
- TargetedSpell系统调用affectTarget()

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| OUT_QUANTITY | int | 5 | 合成产出数量 |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| image | int | ItemSpriteSheet.WILD_ENERGY | 物品图标索引 |
| usesTargeting | boolean | true | 启用目标选择功能 |
| talentChance | float | 1/5 | 天赋触发概率 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器配合实例初始化块：

```java
{
    image = ItemSpriteSheet.WILD_ENERGY;
    usesTargeting = true;
    talentChance = 1/(float)Recipe.OUT_QUANTITY; // 1/5
}
```

### 初始化块
实例初始化块设置图标、启用目标选择和天赋触发概率。

### 初始化注意事项
- `usesTargeting=true`确保显示目标选择界面
- 继承了TargetedSpell的stackable=true和defaultAction=AC_CAST
- 依赖CursedWand进行诅咒效果处理

## 7. 方法详解

### fx(Ballistica bolt, Callback callback)

**可见性**：protected

**是否覆写**：是，覆写自 TargetedSpell

**方法职责**：重写视觉效果，使用诅咒法杖的诅咒效果

**参数**：
- `bolt` (Ballistica)：弹道信息
- `callback` (Callback)：完成回调

**返回值**：void

**前置条件**：无

**副作用**：播放诅咒法杖的视觉和音效

**核心实现逻辑**：
```java
CursedWand.cursedZap(this, curUser, bolt, callback);
```
复用CursedWand的cursedZap方法来处理诅咒效果的视觉表现。

**边界情况**：无特殊边界情况

### affectTarget(Ballistica bolt, final Hero hero)

**可见性**：protected

**是否覆写**：是，覆写自 TargetedSpell

**方法职责**：处理充能效果的核心逻辑（诅咒效果由fx处理）

**参数**：
- `bolt` (Ballistica)：弹道信息
- `hero` (Hero)：施法的英雄角色

**返回值**：void

**前置条件**：弹道已计算完成

**副作用**：
- 为英雄和装备充能
- 应用Recharging和ArtifactRecharge Buff
- 播放充能音效和视觉效果
- 消耗强能结晶

**核心实现逻辑**：
**音效和视觉**：
- 播放闪电和充能音效
- 显示充能视觉效果（SpellSprite.CHARGE）

**充能效果**：
- 调用ScrollOfRecharging.charge()为英雄充能
- 调用hero.belongings.charge(1f)为所有法杖充能
- 调用ArtifactRecharge.chargeArtifacts(hero, 4f)为所有神器充能8回合

**Buff应用**：
- 应用Recharging Buff持续8回合
- 应用ArtifactRecharge Buff持续8回合，并设置ignoreHornOfPlenty=false

**完成处理**：
- 调用onSpellused()消耗物品并触发天赋

**边界情况**：
- 诅咒效果的实际触发由CursedWand.cursedZap处理
- 充能效果与诅咒效果是分离的逻辑

### value() 和 energyVal()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：计算物品的金币和能量价值

**参数**：无

**返回值**：int，基于数量计算的价值（60*quantity/5金币，12*quantity/5能量）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
线性价值计算，单价60金币和12能量，按产出数量分摊

**边界情况**：数量为0时返回0

## 8. 对外暴露能力

### 显式 API
- `affectTarget()`: 公开的核心效果接口
- `fx()`: 视觉效果接口
- `value()`, `energyVal()`: 价值查询接口
- `Recipe`: 公开的合成配方

### 内部辅助方法
- 无额外的内部方法，主要逻辑在覆写的方法中

### 扩展入口
- 合成配方可通过修改Recipe进行调整
- 充能持续时间可在affectTarget()中修改
- 诅咒效果类型由CursedWand决定，无法直接扩展

## 9. 运行机制与调用链

### 创建时机
- 通过炼金合成（充能卷轴 × 1 + 邪能碎片 × 1）获得

### 调用者
- Hero.execute() → Spell.execute() → TargetedSpell.onCast()
- TargetedSpell.targeter.onSelect() → TargetedSpell.affectTarget()

### 被调用者
- CursedWand.cursedZap(): 诅咒效果处理
- ScrollOfRecharging.charge(): 英雄充能
- ArtifactRecharge.chargeArtifacts(): 神器充能
- Recharging Buff: 法杖充能Buff
- SpellSprite.show(): 充能视觉效果

### 系统流程位置
1. **瞄准阶段**：TargetedSpell打开目标选择界面 → 玩家选择目标位置
2. **效果阶段**：fx() → CursedWand.cursedZap() → 诅咒效果
3. **充能阶段**：affectTarget() → 多重充能效果 → Buff应用 → 消耗物品

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.spells.wildenergy.name | 强能结晶 | 物品名称 |
| items.spells.wildenergy.desc | 这个结晶中含有部分驱动DM-300的诅咒之力... | 物品描述 |

### 依赖的资源
- ItemSpriteSheet.WILD_ENERGY: 物品图标
- Assets.Sounds.LIGHTNING: 闪电音效
- Assets.Sounds.CHARGEUP: 充能音效
- SpellSprite.CHARGE: 充能视觉效果

### 中文翻译来源
来自 core/src/main/assets/messages/items/items_zh.properties 文件

## 11. 使用示例

### 基本用法
```java
// 创建强能结晶实例
WildEnergy wildEnergy = new WildEnergy();

// 获取物品价值
int goldValue = wildEnergy.value();
int energyValue = wildEnergy.energyVal();

// 在TargetedSpell系统中自动调用
// wildEnergy.affectTarget(bolt, hero); // 由父类调用
```

### 合成示例
```java
// 通过合成创建强能结晶
ArrayList<Item> ingredients = new ArrayList<>();
ingredients.add(new ScrollOfRecharging()); // 充能卷轴
ingredients.add(new MetalShard()); // 邪能碎片

WildEnergy.Recipe recipe = new WildEnergy.Recipe();
if (recipe.testIngredients(ingredients)) {
    Item result = recipe.brew(ingredients);
    // result 包含5个强能结晶
}
```

### 效果分析示例
```java
// 强能结晶的实际效果包含两部分：
// 1. 诅咒效果：由CursedWand.cursedZap()处理，随机触发
// 2. 充能效果：由affectTarget()处理，确定性效果
// 玩家可以通过选择目标方向来控制诅咒效果的影响范围
```

## 12. 开发注意事项

### 状态依赖
- 依赖CursedWand的诅咒效果随机性
- 依赖充能系统的多重Buff应用
- 与ScrollOfRecharging的充能逻辑紧密耦合

### 生命周期耦合
- 与TargetedSpell的弹道系统深度集成
- 依赖Buff系统的Recharging和ArtifactRecharge
- 与CursedWand的诅咒效果系统耦合

### 常见陷阱
- 诅咒效果和充能效果的分离处理
- 目标选择对诅咒效果的影响
- 充能持续时间的精确控制
- 多重充能效果的叠加处理

## 13. 修改建议与扩展点

### 适合扩展的位置
- 充能持续时间：可以在affectTarget()中调整
- 合成配方：可以调整材料成本或产出数量
- 音效和视觉效果：可以添加更多反馈

### 不建议修改的位置
- 诅咒效果的随机性（由CursedWand控制）
- 充能效果的基本逻辑
- 与CursedWand的集成点

### 重构建议
- 考虑将充能效果提取到独立方法
- 添加充能强度的配置选项
- 考虑支持更精细的充能控制（如只充能特定类型）

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点