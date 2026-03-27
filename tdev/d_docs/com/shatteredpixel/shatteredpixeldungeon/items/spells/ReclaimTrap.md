# 陷阱晶柱 (ReclaimTrap)

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\items\spells\ReclaimTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.spells |
| **文件类型** | class |
| **继承关系** | extends TargetedSpell |
| **代码行数** | 218 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
陷阱晶柱是一个双重功能法术，能够收集未触发的陷阱并将其存储起来，然后在指定位置重新释放该陷阱的效果。晶柱一次只能储存一个陷阱，并且可以对任何位置释放陷阱效果。

### 系统定位
作为TargetedSpell的子类，陷阱晶柱在游戏的陷阱系统中扮演独特角色。它与陷阱激活系统、角色状态管理系统（Buff）和物品存储系统深度集成，提供了陷阱重用和战术部署的能力。

### 不负责什么
- 不直接处理战斗伤害计算
- 不提供永久性的装备强化
- 不涉及经济系统或商店交互

## 3. 结构总览

### 主要成员概览
- `image`: 物品图标（ItemSpriteSheet.RECLAIM_TRAP）
- `talentChance`: 天赋触发概率（1/5）
- `storedTrap`: 存储的陷阱类型（用于v3.0.0前兼容）
- `Recipe`: 内部类，定义合成配方
- `ReclaimedTrap`: 内部类，Buff用于存储陷阱信息
- `COLORS`: 陷阱颜色发光效果数组

### 主要逻辑块概览
- `actions()`: 限制丢弃/投掷动作（旧版兼容）
- `affectTarget()`: 处理陷阱收集和释放的双重逻辑
- `desc()`, `glowing()`: 动态描述和视觉效果
- 合成配方：使用探地卷轴和邪能碎片

### 生命周期/调用时机
- 第一次施放（无存储陷阱）：收集目标位置的陷阱
- 第二次施放（有存储陷阱）：在目标位置释放存储的陷阱
- 物品描述和视觉效果根据存储状态动态变化

## 4. 继承与协作关系

### 父类提供的能力
从TargetedSpell继承：
- `onCast()`: 打开目标选择界面
- `fx()`: 播放魔法飞弹效果
- `onSpellused()`: 处理消耗和天赋触发
- 弹道系统和碰撞检测

从Spell继承：
- `AC_CAST`动作常量
- `talentFactor`, `talentChance`天赋相关字段
- 基础物品属性和方法

### 覆写的方法
- `actions(Hero hero)`: 限制丢弃动作（旧版兼容）
- `affectTarget(Ballistica bolt, Hero hero)`: 实现核心双重逻辑
- `desc()`, `glowing()`: 提供动态描述和视觉效果
- `value()`, `energyVal()`: 自定义价值计算
- `storeInBundle()/restoreFromBundle()`: 自定义序列化

### 实现的接口契约
通过继承Item间接实现Serializable接口

### 依赖的关键类
- `ReclaimedTrap`: 内部Buff类管理陷阱存储
- `Trap`: 陷阱系统基类
- `Reflection`: 动态创建陷阱实例
- `Bestiary`: 图鉴系统记录陷阱遭遇
- `ScrollOfRecharging`: 充能效果
- `Catalog`: 使用统计

### 使用者
- 游戏玩家通过背包界面使用
- 合成系统通过Recipe创建
- 存档系统通过序列化方法保存/加载

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| OUT_QUANTITY | int | 5 | 合成产出数量 |
| STORED_TRAP | String | "stored_trap" | 序列化键名 |
| TRAP | String | "trap" | Buff序列化键名 |
| COLORS | ItemSprite.Glowing[] | 9种颜色 | 陷阱类型对应发光效果 |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| image | int | ItemSpriteSheet.RECLAIM_TRAP | 物品图标索引 |
| talentChance | float | 1/5 | 天赋触发概率 |
| storedTrap | Class<?extends Trap> | null | 存储的陷阱类型（v3.0.0前兼容） |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器配合实例初始化块：

```java
{
    image = ItemSpriteSheet.RECLAIM_TRAP;
    talentChance = 1/(float)Recipe.OUT_QUANTITY; // 1/5
}
```

### 初始化块
实例初始化块设置图标和天赋触发概率。

### 初始化注意事项
- v3.0.0版本前后存储机制不同：之前存储在物品属性中，之后改为Hero Buff
- storedTrap字段仅用于旧版存档兼容
- ReclaimedTrap Buff具有revivePersists=true，死亡后仍保留

## 7. 方法详解

### actions(Hero hero)

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：限制物品的可用动作（防止旧版利用漏洞）

**参数**：
- `hero` (Hero)：当前英雄角色

**返回值**：ArrayList<String>，可用动作列表

**前置条件**：无

**副作用**：从动作列表中移除AC_DROP和AC_THROW

**核心实现逻辑**：
- 如果storedTrap != null（旧版存储），移除丢弃和投掷动作
- 这是为了防止利用旧版存储机制的漏洞

**边界情况**：仅影响v3.0.0前的存档

### affectTarget(Ballistica bolt, Hero hero)

**可见性**：protected

**是否覆写**：是，覆写自 TargetedSpell

**方法职责**：实现陷阱收集和释放的双重逻辑

**参数**：
- `bolt` (Ballistica)：弹道信息
- `hero` (Hero)：施法的英雄角色

**返回值**：void

**前置条件**：弹道已计算完成

**副作用**：
- 收集陷阱时：解除陷阱、充能、创建ReclaimedTrap Buff
- 释放陷阱时：创建新陷阱实例、激活陷阱、消耗物品

**核心实现逻辑**：
**收集阶段（storedTrap == null）**：
- 检查目标位置是否有活跃且可见的陷阱
- 调用t.disarm()解除陷阱（即使通常不可解除的陷阱也会被解除）
- 播放音效、为英雄充能、创建ReclaimedTrap Buff存储陷阱类型
- 记录图鉴信息

**释放阶段（storedTrap != null）**：
- 通过Reflection.newInstance()创建陷阱实例
- 设置位置和reclaimed=true标志
- 调用t.activate()激活陷阱
- 调用onSpellused()消耗物品并触发天赋

**边界情况**：
- 无陷阱可收集时显示错误消息
- 收集阶段不消耗物品（仅释放阶段消耗）
- 旧版和新版存储机制的兼容处理

### desc()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：提供包含当前存储陷阱信息的动态描述

**参数**：无

**返回值**：String，物品描述文本

**前置条件**：Dungeon.hero存在

**副作用**：无

**核心实现逻辑**：
- 获取基础描述
- 如果有storedTrap（旧版），追加陷阱名称
- 如果有ReclaimedTrap Buff（新版），同样追加陷阱名称

**边界情况**：处理Dungeon.hero为null或物品不在背包中的情况

### glowing()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：提供基于存储陷阱类型的彩色发光效果

**参数**：无

**返回值**：ItemSprite.Glowing，对应陷阱颜色的发光效果或null

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
- 如果有storedTrap，返回对应颜色的发光效果
- 如果有ReclaimedTrap Buff，同样返回对应颜色
- 使用Reflection.newInstance().color获取陷阱颜色索引

**边界情况**：无存储陷阱时返回null

### storeInBundle(Bundle bundle) 和 restoreFromBundle(Bundle bundle)

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：处理物品的序列化和反序列化（包括旧版兼容）

**参数**：
- `bundle` (Bundle)：存档数据包

**返回值**：void

**前置条件**：存档操作进行中

**副作用**：读写存档数据

**核心实现逻辑**：
- 调用父类方法处理基础属性
- 只有当storedTrap != null时才保存陷阱类型
- 恢复时检查是否存在STORED_TRAP键

**边界情况**：处理v3.0.0前后存档格式的兼容性

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
- `desc()`, `glowing()`: 动态描述和视觉效果接口
- `value()`, `energyVal()`: 价值查询接口
- `Recipe`: 公开的合成配方
- `ReclaimedTrap`: 公开的Buff类

### 内部辅助方法
- `actions()`: 安全限制方法
- 序列化方法用于存档兼容

### 扩展入口
- `ReclaimedTrap` Buff类可被其他系统使用
- 合成配方可通过修改Recipe进行扩展
- 陷阱颜色映射可在COLORS数组中调整

## 9. 运行机制与调用链

### 创建时机
- 通过炼金合成（探地卷轴 × 1 + 邪能碎片 × 1）获得
- 初始storedTrap = null，ReclaimedTrap Buff不存在

### 调用者
- Hero.execute() → Spell.execute() → TargetedSpell.onCast()
- TargetedSpell.targeter.onSelect() → ReclaimTrap.affectTarget()
- 游戏存档系统 → 序列化方法

### 被调用者
- Reflection.newInstance(): 动态创建陷阱实例
- Trap.disarm()/activate(): 陷阱控制
- ScrollOfRecharging.charge(): 英雄充能
- Bestiary.setSeen()/countEncounter(): 图鉴记录
- Buff.affect(): 创建ReclaimedTrap Buff

### 系统流程位置
1. **收集阶段**：选择陷阱位置 → affectTarget() → 解除陷阱 → 创建ReclaimedTrap Buff → 不消耗物品
2. **释放阶段**：选择目标位置 → affectTarget() → 创建陷阱实例 → 激活陷阱 → 消耗物品
3. **状态显示**：物品描述和发光效果根据存储状态动态更新

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.spells.reclaimtrap.name | 陷阱晶柱 | 物品名称 |
| items.spells.reclaimtrap.no_trap | 这里没有陷阱。 | 无陷阱错误消息 |
| items.spells.reclaimtrap.desc_trap | 施放晶柱将在目标位置产生一次_%s_的效果。 | 动态描述模板 |
| items.spells.reclaimtrap.desc | 这个晶柱蕴含着DM-300残余的机械能量... | 物品描述 |

### 依赖的资源
- ItemSpriteSheet.RECLAIM_TRAP: 物品图标
- Assets.Sounds.LIGHTNING: 收集陷阱音效
- COLORS数组: 9种陷阱颜色对应的发光效果

### 中文翻译来源
来自 core/src/main/assets/messages/items/items_zh.properties 文件

## 11. 使用示例

### 基本用法
```java
// 创建陷阱晶柱实例
ReclaimTrap reclaimTrap = new ReclaimTrap();

// 获取物品价值
int goldValue = reclaimTrap.value();
int energyValue = reclaimTrap.energyVal();

// 在TargetedSpell系统中自动调用
// reclaimTrap.affectTarget(bolt, hero); // 由父类调用
```

### 合成示例
```java
// 通过合成创建陷阱晶柱
ArrayList<Item> ingredients = new ArrayList<>();
ingredients.add(new ScrollOfMagicMapping()); // 探地卷轴
ingredients.add(new MetalShard()); // 邪能碎片

ReclaimTrap.Recipe recipe = new ReclaimTrap.Recipe();
if (recipe.testIngredients(ingredients)) {
    Item result = recipe.brew(ingredients);
    // result 包含5个陷阱晶柱
}
```

### 陷阱存储检查示例
```java
// 检查当前是否存储了陷阱
ReclaimTrap.ReclaimedTrap buff = hero.buff(ReclaimTrap.ReclaimedTrap.class);
if (buff != null) {
    Class<?extends Trap> storedTrap = buff.trap;
    // 获取存储的陷阱类型
}
```

## 12. 开发注意事项

### 状态依赖
- 严重依赖ReclaimedTrap Buff的状态管理
- storedTrap字段仅用于旧版兼容，不应在新代码中使用
- ReclaimedTrap Buff的revivePersists=true确保死亡后状态保留

### 生命周期耦合
- 与陷阱系统的disarm()/activate()方法紧密耦合
- 依赖Reflection进行动态陷阱实例创建
- 与图鉴系统和充能系统集成

### 常见陷阱
- 旧版和新版存储机制的兼容性处理
- 收集阶段不消耗物品但释放阶段消耗的差异
- 陷阱颜色索引的正确映射
- 动态创建陷阱实例的安全性

## 13. 修改建议与扩展点

### 适合扩展的位置
- `affectTarget()`方法：可以添加更多陷阱类型的特殊处理
- COLORS数组：可以调整颜色映射或添加新颜色
- 合成配方：可以调整材料成本或产出数量

### 不建议修改的位置
- 旧版兼容性相关的storedTrap逻辑
- ReclaimedTrap Buff的revivePersists设置
- 与ScrollOfRecharging的充能集成点

### 重构建议
- 考虑将陷阱颜色映射移到Trap基类中
- 添加更详细的陷阱信息存储（如等级、属性等）
- 考虑支持同时存储多个陷阱的进阶版本

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点