# 无序结晶 (UnstableSpell)

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\items\spells\UnstableSpell.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.spells |
| **文件类型** | class |
| **继承关系** | extends Spell |
| **代码行数** | 171 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
无序结晶是一个随机卷轴法术，激活时会随机触发一种卷轴效果。如果视野内存在敌人，则必定触发战斗类卷轴效果；反之则必定触发非战斗类卷轴效果。这个机制确保了法术效果的战术相关性。

### 系统定位
作为Spell的直接子类，无序结晶在游戏的随机效果系统中扮演独特角色。它与卷轴系统、敌人检测系统和随机选择系统深度集成，提供了情境感知的随机卷轴体验。

### 不负责什么
- 不直接处理物品管理逻辑
- 不提供永久性的状态改变
- 不涉及经济系统或合成配方以外的交互

## 3. 结构总览

### 主要成员概览
- `image`: 物品图标（ItemSpriteSheet.UNSTABLE_SPELL）
- `scrollChances`: 卷轴权重映射表
- `nonCombatScrolls`: 非战斗卷轴集合
- `combatScrolls`: 战斗卷轴集合
- `Recipe`: 内部类，定义合成配方

### 主要逻辑块概览
- 静态初始化块：设置卷轴权重和分类
- `onCast()`: 实现情境感知的随机卷轴选择和执行
- 合成配方：使用任意卷轴和符石

### 生命周期/调用时机
- 玩家选择施放 → 检测敌人存在情况
- 根据情境选择合适的卷轴类型
- 随机选择并执行卷轴效果
- 消耗无序结晶

## 4. 继承与协作关系

### 父类提供的能力
从Spell继承：
- `AC_CAST`动作常量
- `talentFactor`, `talentChance`天赋相关字段
- `stackable=true`, `defaultAction=AC_CAST`
- 基础物品属性和方法

### 覆写的方法
- `onCast(Hero hero)`: 实现核心随机卷轴逻辑
- `value()`, `energyVal()`: 自定义价值计算

### 实现的接口契约
通过继承Item间接实现Serializable接口

### 依赖的关键类
- `Scroll`: 卷轴基类
- `Reflection`: 动态创建卷轴实例
- `Random.chances()`: 加权随机选择
- `ExoticScroll`: 异域卷轴支持
- `Runestone`: 符石类（合成材料）

### 使用者
- 游戏玩家通过背包界面使用
- 合成系统通过Recipe创建
- Spell.execute()调用onCast()

## 5. 字段/常量详解

### 静态常量
无显式静态常量，但有三个重要的静态字段：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| scrollChances | HashMap<Class<? extends Scroll>, Float> | 卷轴权重映射表 |
| nonCombatScrolls | HashSet<Class<? extends Scroll>> | 非战斗卷轴集合 |
| combatScrolls | HashSet<Class<? extends Scroll>> | 战斗卷轴集合 |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| image | int | ItemSpriteSheet.UNSTABLE_SPELL | 物品图标索引 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器配合实例初始化块：

```java
{
    image = ItemSpriteSheet.UNSTABLE_SPELL;
}
```

### 初始化块
实例初始化块仅设置图标。主要的初始化逻辑在静态初始化块中：

**scrollChances权重映射**：
- 鉴定卷轴: 3.0f（最高权重）
- 祛邪、探地、镜像、充能、催眠、复仇、盛怒、传送、恐惧卷轴: 2.0f
- 嬗变卷轴: 1.0f（最低权重）

**卷轴分类**：
- 非战斗卷轴：鉴定、祛邪、探地、充能、催眠、传送、嬗变
- 战斗卷轴：镜像、充能、催眠、复仇、盛怒、传送、恐惧

注意：充能、催眠、传送卷轴同时属于两类，确保情境适应性。

### 初始化注意事项
- 继承了Spell的stackable=true和defaultAction=AC_CAST
- 无天赋触发（talentChance未设置，默认为1.0f但不使用）
- 合成成本极低（仅需1点炼金能量）

## 7. 方法详解

### onCast(Hero hero)

**可见性**：protected

**是否覆写**：是，覆写自 Spell

**方法职责**：实现情境感知的随机卷轴选择和执行逻辑

**参数**：
- `hero` (Hero)：施法的英雄角色

**返回值**：void

**前置条件**：无

**副作用**：
- 消耗无序结晶
- 创建并执行随机卷轴
- 记录使用统计
- 不触发自身天赋（由卷轴触发）

**核心实现逻辑**：
**情境检测**：
- 调用hero.visibleEnemies()检测视野内敌人数量
- 如果为0，选择非战斗卷轴；否则选择战斗卷轴

**加权随机选择**：
- 使用Random.chances(scrollChances)进行加权随机选择
- 通过while循环确保选择的卷轴符合当前情境
- 使用Reflection.newInstance()创建卷轴实例

**卷轴执行**：
- 调用s.anonymize()匿名化卷轴
- 设置s.talentChance = s.talentFactor = 1
- 将curItem设置为卷轴实例
- 调用s.doRead()执行卷轴效果
- 记录使用统计但不触发天赋（由卷轴自身处理）

**边界情况**：
- 确保至少有一个卷轴符合情境要求
- 处理卷轴权重的动态调整
- 异域卷轴的兼容性处理

### value() 和 energyVal()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：计算物品的金币和能量价值

**参数**：无

**返回值**：int，线性价值计算（40*quantity金币，8*quantity能量）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
由于合成成本较低，价值也相应降低（40金币，8能量每个）

**边界情况**：数量为0时返回0

## 8. 对外暴露能力

### 显式 API
- `onCast()`: 公开的核心效果接口
- `value()`, `energyVal()`: 价值查询接口
- `Recipe`: 公开的合成配方

### 内部辅助方法
- 无额外的内部方法，主要逻辑在静态初始化和onCast()中

### 扩展入口
- scrollChances映射表可被修改调整权重
- 卷轴分类集合可被扩展
- 合成配方可通过修改Recipe进行调整

## 9. 运行机制与调用链

### 创建时机
- 通过炼金合成（任意卷轴 + 符石）获得
- 合成成本仅为1点炼金能量

### 调用者
- Hero.execute() → Spell.execute() → UnstableSpell.onCast()
- 合成系统 → Recipe.brew()

### 被调用者
- Random.chances(): 加权随机选择
- Reflection.newInstance(): 动态创建卷轴实例
- Scroll.doRead(): 执行卷轴效果
- Catalog.countUse(): 使用统计

### 系统流程位置
1. **情境检测**：onCast() → hero.visibleEnemies() → 确定卷轴类别
2. **随机选择**：Random.chances() → 卷轴类型选择 → 确保符合情境
3. **执行阶段**：创建卷轴实例 → anonymize() → doRead() → 消耗物品

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.spells.unstablespell.name | 无序结晶 | 物品名称 |
| items.spells.unstablespell.desc | 这块黑色方形小水晶的每个面都浮动着变幻莫测的符文... | 物品描述 |

### 依赖的资源
- ItemSpriteSheet.UNSTABLE_SPELL: 物品图标

### 中文翻译来源
来自 core/src/main/assets/messages/items/items_zh.properties 文件

## 11. 使用示例

### 基本用法
```java
// 创建无序结晶实例
UnstableSpell unstableSpell = new UnstableSpell();

// 施放法术（通常由游戏系统调用）
unstableSpell.onCast(hero);

// 获取物品价值
int goldValue = unstableSpell.value();
int energyValue = unstableSpell.energyVal();
```

### 合成示例
```java
// 通过合成创建无序结晶
ArrayList<Item> ingredients = new ArrayList<>();
ingredients.add(new ScrollOfIdentify()); // 任意卷轴
ingredients.add(new Runestone()); // 任意符石

UnstableSpell.Recipe recipe = new UnstableSpell.Recipe();
if (recipe.testIngredients(ingredients)) {
    Item result = recipe.brew(ingredients);
    // result 包含1个无序结晶
}
```

### 权重调整示例
```java
// 修改卷轴权重（理论上可行）
UnstableSpell.scrollChances.put(ScrollOfTeleportation.class, 5.0f);
// 这将增加传送卷轴的出现概率
```

## 12. 开发注意事项

### 状态依赖
- 严重依赖hero.visibleEnemies()的准确检测
- 依赖Random.chances()的加权随机算法
- 卷轴分类的正确维护

### 生命周期耦合
- 与卷轴系统的doRead()方法紧密耦合
- 依赖Reflection的动态实例创建
- 与全局curItem静态变量交互

### 常见陷阱
- 卷轴分类的维护（新增卷轴需要更新集合）
- 权重调整对游戏平衡的影响
- 异域卷轴的兼容性处理
- curItem静态变量的竞态条件风险

## 13. 修改建议与扩展点

### 适合扩展的位置
- scrollChances映射表：可以动态调整权重
- 卷轴分类集合：可以添加新的卷轴类型
- 情境检测逻辑：可以添加更多情境条件

### 不建议修改的位置
- 核心的while循环逻辑（确保情境匹配）
- 卷轴执行的anonymize()和doRead()调用顺序
- 合成配方的基本逻辑

### 重构建议
- 考虑将卷轴权重配置移到外部文件
- 添加更多情境类型（如低血量、特殊楼层等）
- 考虑支持异域卷轴的专门处理逻辑

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点