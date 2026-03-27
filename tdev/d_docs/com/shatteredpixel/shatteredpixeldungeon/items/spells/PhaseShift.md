# 转移结晶 (PhaseShift)

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\items\spells\PhaseShift.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.spells |
| **文件类型** | class |
| **继承关系** | extends TargetedSpell |
| **代码行数** | 95 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
转移结晶是一个目标传送法术，能够将目标单位传送到本层的随机位置。被传送的角色会被麻痹相当长的一段时间，而足够强大的敌人（Boss和精英怪）可以抵抗麻痹效果。这个法术可以对目标单位或施法者自身使用。

### 系统定位
作为TargetedSpell的子类，转移结晶在游戏的传送和控制系统中扮演重要角色。它与角色状态系统（麻痹Buff）、AI系统（怪物状态转换）和传送系统深度集成。

### 不负责什么
- 不处理物品管理逻辑
- 不提供直接的伤害输出
- 不涉及经济系统或合成系统

## 3. 结构总览

### 主要成员概览
- `image`: 物品图标（ItemSpriteSheet.PHASE_SHIFT）
- `usesTargeting`: 目标选择标记（true）
- `talentChance`: 天赋触发概率（1/6）
- `Recipe`: 内部类，定义合成配方
- 继承自TargetedSpell的targeting和effect机制

### 主要逻辑块概览
- `affectTarget()`: 处理目标传送和麻痹逻辑
- 合成配方：使用传送卷轴

### 生命周期/调用时机
- 玩家选择施放 → 显示目标选择界面
- 选择目标位置 → 射击弹道 → 执行传送和麻痹
- 消耗转移结晶并触发天赋

## 4. 继承与协作关系

### 父类提供的能力
从TargetedSpell继承：
- `onCast()`: 打开目标选择界面
- `fx()`: 播放魔法飞弹效果
- `onSpellused()`: 处理消耗和天赋触发
- `collisionProperties`: 弹道碰撞属性

从Spell继承：
- `AC_CAST`动作常量
- `talentFactor`, `talentChance`天赋相关字段
- 基础物品属性和方法

### 覆写的方法
- `affectTarget(Ballistica bolt, Hero hero)`: 实现核心传送逻辑
- `value()`, `energyVal()`: 自定义价值计算

### 实现的接口契约
通过继承Item间接实现Serializable接口

### 依赖的关键类
- `ScrollOfTeleportation`: 复用传送逻辑
- `Paralysis`: 麻痹状态
- `Mob`: 怪物AI状态管理
- `Actor`: 角色查找
- `Buff`: 状态应用系统
- `Dungeon.level`: 楼层随机位置生成

### 使用者
- 游戏玩家通过背包界面使用
- 合成系统通过Recipe创建
- TargetedSpell系统调用affectTarget()

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| OUT_QUANTITY | int | 6 | 合成产出数量 |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| image | int | ItemSpriteSheet.PHASE_SHIFT | 物品图标索引 |
| usesTargeting | boolean | true | 启用目标选择功能 |
| talentChance | float | 1/6 | 天赋触发概率 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器配合实例初始化块：

```java
{
    image = ItemSpriteSheet.PHASE_SHIFT;
    usesTargeting = true;
    talentChance = 1/(float)Recipe.OUT_QUANTITY; // 1/6
}
```

### 初始化块
实例初始化块设置图标、启用目标选择和天赋触发概率。

### 初始化注意事项
- `usesTargeting=true`确保显示目标选择界面
- 继承了TargetedSpell的stackable=true和defaultAction=AC_CAST
- collisionProperties使用默认的Ballistica.PROJECTILE

## 7. 方法详解

### affectTarget(Ballistica bolt, Hero hero)

**可见性**：protected

**是否覆写**：是，覆写自 TargetedSpell

**方法职责**：处理目标传送和麻痹效果的核心逻辑

**参数**：
- `bolt` (Ballistica)：弹道信息，包含碰撞位置
- `hero` (Hero)：施法的英雄角色

**返回值**：void

**前置条件**：弹道已计算完成

**副作用**：
- 传送目标角色到随机位置
- 应用麻痹状态（非Boss/精英怪）
- 修改怪物AI状态
- 消耗法术物品

**核心实现逻辑**：
- 使用Actor.findChar()查找碰撞位置的角色
- 如果找到角色，调用ScrollOfTeleportation.teleportChar()传送
- 如果是普通怪物，重置AI状态为WANDERING并设置新目标
- 如果不是Boss/精英怪，应用Paralysis麻痹状态
- 如果未找到目标，显示"没有可传送的东西"消息
- 调用onSpellused()处理消耗和天赋

**边界情况**：
- 目标为空时的错误处理
- Boss和精英怪的麻痹免疫
- 传送失败的处理（teleportChar返回false）

### value() 和 energyVal()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：计算物品的金币和能量价值

**参数**：无

**返回值**：int，基于数量计算的价值（60*quantity/6金币，12*quantity/6能量）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
线性价值计算，单价60金币和12能量，按产出数量分摊

**边界情况**：数量为0时返回0

## 8. 对外暴露能力

### 显式 API
- `affectTarget()`: 公开的核心效果接口
- `value()`, `energyVal()`: 价值查询接口
- `Recipe`: 公开的合成配方

### 内部辅助方法
- 无额外的内部方法，主要逻辑在覆写的方法中

### 扩展入口
- 合成配方可通过修改Recipe进行调整
- 麻痹持续时间和条件可在affectTarget()中修改

## 9. 运行机制与调用链

### 创建时机
- 通过炼金合成（传送卷轴 × 1）获得

### 调用者
- Hero.execute() → Spell.execute() → TargetedSpell.onCast()
- TargetedSpell.targeter.onSelect() → TargetedSpell.affectTarget()

### 被调用者
- ScrollOfTeleportation.teleportChar(): 执行实际传送
- Buff.affect(): 应用麻痹状态
- Mob.beckon()/state修改: AI状态更新
- Dungeon.level.randomDestination(): 生成随机目标位置

### 系统流程位置
1. **瞄准阶段**：TargetedSpell打开目标选择界面 → 玩家选择目标位置
2. **射击阶段**：播放魔法飞弹效果 → 计算弹道碰撞位置
3. **效果阶段**：affectTarget() → 查找目标 → 传送 → 应用麻痹 → 消耗物品

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.spells.phaseshift.name | 转移结晶 | 物品名称 |
| items.spells.phaseshift.no_target | 那里没什么可传送的东西。 | 无目标错误消息 |
| items.spells.phaseshift.desc | 这个充满混沌能量的结晶会将目标单位传送到本层随机位置... | 物品描述 |

### 依赖的资源
- ItemSpriteSheet.PHASE_SHIFT: 物品图标
- MagicMissile.MAGIC_MISSILE: 魔法飞弹效果
- Assets.Sounds.ZAP: 施法音效

### 中文翻译来源
来自 core/src/main/assets/messages/items/items_zh.properties 文件

## 11. 使用示例

### 基本用法
```java
// 创建转移结晶实例
PhaseShift phaseShift = new PhaseShift();

// 获取物品价值
int goldValue = phaseShift.value();
int energyValue = phaseShift.energyVal();

// 在TargetedSpell系统中自动调用
// phaseShift.affectTarget(bolt, hero); // 由父类调用
```

### 合成示例
```java
// 通过合成创建转移结晶
ArrayList<Item> ingredients = new ArrayList<>();
ingredients.add(new ScrollOfTeleportation()); // 传送卷轴

PhaseShift.Recipe recipe = new PhaseShift.Recipe();
if (recipe.testIngredients(ingredients)) {
    Item result = recipe.brew(ingredients);
    // result 包含6个转移结晶
}
```

### 效果检查示例
```java
// 检查目标是否会被麻痹
Char target = Actor.findChar(targetPos);
if (target != null && !Char.hasProp(target, Char.Property.BOSS) && 
    !Char.hasProp(target, Char.Property.MINIBOSS)) {
    // 目标会被麻痹
}
```

## 12. 开发注意事项

### 状态依赖
- 依赖角色的Property.BOSS和Property.MINIBOSS属性判断麻痹免疫
- 依赖怪物的AI状态（HUNTING/WANDERING）进行重置
- 与传送系统的teleportChar()方法紧密耦合

### 生命周期耦合
- 与TargetedSpell的弹道系统深度集成
- 依赖Buff系统应用Paralysis状态
- 与Dungeon.level的随机位置生成系统耦合

### 常见陷阱
- Boss和精英怪的麻痹免疫逻辑
- 传送失败时的错误处理
- 怪物AI状态重置的时机和逻辑
- 弹道碰撞位置与实际目标位置的区别

## 13. 修改建议与扩展点

### 适合扩展的位置
- `affectTarget()`方法：可以添加更多目标类型的支持
- 麻痹持续时间：可以通过配置参数调整
- 合成配方：可以调整材料成本或产出数量

### 不建议修改的位置
- Boss/精英怪的麻痹免疫逻辑
- 与ScrollOfTeleportation的集成点
- 怪物AI状态重置逻辑

### 重构建议
- 考虑将麻痹应用逻辑提取到独立方法
- 添加更多目标类型的支持（如友方单位的特殊处理）
- 考虑添加传送距离限制或成功率机制

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点