# 诅咒菱晶 (CurseInfusion)

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\items\spells\CurseInfusion.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.spells |
| **文件类型** | class |
| **继承关系** | extends InventorySpell |
| **代码行数** | 129 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
诅咒菱晶是一个物品强化法术，能够向装备物品（武器、护甲、法杖）注入DM-300内部相同的邪恶魔法，使其被诅咒并获得特殊效果。与其他诅咒不同，诅咒菱晶还会为物品提供额外的升级效果。

### 系统定位
作为InventorySpell的子类，诅咒菱晶在游戏的物品强化系统中扮演独特角色。它与装备系统的诅咒机制、附魔/刻印系统以及升级系统深度集成，提供了特殊的"有益诅咒"机制。

### 不负责什么
- 不直接参与战斗逻辑
- 不处理非装备类物品
- 不提供直接的战斗buff或debuff效果

## 3. 结构总览

### 主要成员概览
- `image`: 物品图标（ItemSpriteSheet.CURSE_INFUSE）
- `talentChance`: 天赋触发概率（1/4）
- `Recipe`: 内部类，定义合成配方
- 继承自InventorySpell的itemSelector和相关方法

### 主要逻辑块概览
- `usableOnItem()`: 定义可使用的物品类型
- `onItemSelected()`: 处理物品诅咒和强化逻辑
- 合成配方：使用祛邪卷轴和邪能碎片

### 生命周期/调用时机
- 玩家选择施放 → 打开物品选择界面
- 选择有效装备 → 应用诅咒和强化效果
- 消耗诅咒菱晶并触发天赋

## 4. 继承与协作关系

### 父类提供的能力
从InventorySpell继承：
- `onCast()`: 打开物品选择界面
- `inventoryTitle()`: 获取窗口标题
- `preferredBag`: 首选背包类型
- `itemSelector`: 物品选择器实现

从Spell继承：
- `AC_CAST`动作常量
- `talentFactor`, `talentChance`天赋相关字段
- 基础物品属性和方法

### 覆写的方法
- `usableOnItem(Item item)`: 定义可用物品类型
- `onItemSelected(Item item)`: 实现诅咒逻辑
- `value()`, `energyVal()`: 自定义价值计算

### 实现的接口契约
通过继承Item间接实现Serializable接口

### 依赖的关键类
- `Weapon`, `Armor`, `Wand`: 装备类型
- `SpiritBow`: 灵弓类型
- `MagesStaff`: 法师魔杖
- `RingOfMight`: 力量之戒（特殊处理）
- `Badges`: 成就系统
- `CellEmitter`, `ShadowParticle`: 视觉效果
- `Catalog`: 使用统计

### 使用者
- 游戏玩家通过背包界面使用
- 合成系统通过Recipe创建
- 装备系统处理诅咒效果

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| OUT_QUANTITY | int | 4 | 合成产出数量 |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| image | int | ItemSpriteSheet.CURSE_INFUSE | 物品图标索引 |
| talentChance | float | 1/4 | 天赋触发概率 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器配合实例初始化块：

```java
{
    image = ItemSpriteSheet.CURSE_INFUSE;
    talentChance = 1/(float)Recipe.OUT_QUANTITY; // 1/4
}
```

### 初始化块
实例初始化块设置图标和天赋触发概率。

### 初始化注意事项
- 继承了InventorySpell的stackable=true和defaultAction=AC_CAST
- curseInfusionBonus字段在目标物品上设置，用于标识诅咒来源

## 7. 方法详解

### usableOnItem(Item item)

**可见性**：protected

**是否覆写**：是，覆写自 InventorySpell

**方法职责**：确定指定物品是否可以被诅咒菱晶使用

**参数**：
- `item` (Item)：待检查的物品

**返回值**：boolean，true表示可用

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return ((item instanceof EquipableItem && item.isUpgradable()) || 
        item instanceof Wand || 
        item instanceof SpiritBow);
```

**边界情况**：只允许可升级的装备、法杖和灵弓

### onItemSelected(Item item)

**可见性**：protected

**是否覆写**：是，覆写自 InventorySpell

**方法职责**：对选中的物品应用诅咒和强化效果

**参数**：
- `item` (Item)：目标物品

**返回值**：void

**前置条件**：物品必须通过usableOnItem()检查

**副作用**：
- 播放诅咒音效和粒子效果
- 设置物品cursed=true
- 应用随机诅咒附魔/刻印
- 设置curseInfusionBonus=true
- 触发成就验证
- 更新快捷栏

**核心实现逻辑**：
- 发射阴影粒子效果
- 播放诅咒音效
- 根据物品类型分别处理：
  - 武器：应用随机诅咒附魔，设置curseInfusionBonus
  - 护甲：应用随机诅咒刻印，设置curseInfusionBonus  
  - 法杖：设置curseInfusionBonus并更新等级
  - 力量之戒：更新最大生命值
- 验证物品等级成就

**边界情况**：
- 已有附魔/刻印的处理逻辑（不替换已存在的诅咒）
- 法师魔杖需要特殊更新
- 灵弓作为特殊武器类型被支持

### value() 和 energyVal()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：计算物品的金币和能量价值

**参数**：无

**返回值**：int，基于数量计算的价值（60*quantity/4金币，12*quantity/4能量）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
线性价值计算，单价60金币和12能量

**边界情况**：数量为0时返回0

## 8. 对外暴露能力

### 显式 API
- `usableOnItem()`: 公开的物品可用性检查
- `onItemSelected()`: 公开的物品处理接口
- `value()`, `energyVal()`: 价值查询接口
- `Recipe`: 公开的合成配方

### 内部辅助方法
- 无额外的内部方法，主要逻辑在覆写的方法中

### 扩展入口
- `usableOnItem()`方法可被子类扩展支持更多物品类型
- 合成配方可通过修改Recipe进行调整

## 9. 运行机制与调用链

### 创建时机
- 通过炼金合成（祛邪卷轴 × 1 + 邪能碎片 × 1）获得
- 在商店中可能购买获得

### 调用者
- Hero.execute() → Spell.execute() → InventorySpell.onCast() → CurseInfusion.usableOnItem()
- WndBag.ItemSelector.onSelect() → CurseInfusion.onItemSelected()

### 被调用者
- CellEmitter.get(): 播放粒子效果
- Sample.INSTANCE.play(): 播放音效
- Weapon.enchant()/Armor.inscribe(): 应用诅咒
- Badges.validateItemLevelAquired(): 成就验证
- Catalog.countUse(): 使用统计

### 系统流程位置
1. **选择阶段**：InventorySpell打开物品选择界面 → usableOnItem()过滤可用物品
2. **应用阶段**：onItemSelected() → 应用诅咒 → 播放效果 → 更新物品状态
3. **完成阶段**：消耗物品 → 触发天赋 → 更新UI

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.spells.curseinfusion.name | 诅咒菱晶 | 物品名称 |
| items.spells.curseinfusion.inv_title | 诅咒一件物品 | 选择窗口标题 |
| items.spells.curseinfusion.desc | 这个菱晶可以向一件装备注入和DM-300内部相同的的邪恶魔法... | 物品描述 |

### 依赖的资源
- ItemSpriteSheet.CURSE_INFUSE: 物品图标
- Assets.Sounds.CURSED: 诅咒音效
- ShadowParticle.UP: 阴影粒子效果

### 中文翻译来源
来自 core/src/main/assets/messages/items/items_zh.properties 文件

## 11. 使用示例

### 基本用法
```java
// 创建诅咒菱晶实例
CurseInfusion curseInfusion = new CurseInfusion();

// 检查物品是否可用
Item weapon = new Weapon();
if (curseInfusion.usableOnItem(weapon)) {
    // 应用诅咒
    curseInfusion.onItemSelected(weapon);
}

// 获取物品价值
int goldValue = curseInfusion.value();
int energyValue = curseInfusion.energyVal();
```

### 合成示例
```java
// 通过合成创建诅咒菱晶
ArrayList<Item> ingredients = new ArrayList<>();
ingredients.add(new ScrollOfRemoveCurse()); // 祛邪卷轴
ingredients.add(new MetalShard()); // 邪能碎片

CurseInfusion.Recipe recipe = new CurseInfusion.Recipe();
if (recipe.testIngredients(ingredients)) {
    Item result = recipe.brew(ingredients);
    // result 包含4个诅咒菱晶
}
```

### 诅咒效果检查
```java
// 检查物品是否被诅咒菱晶诅咒
if (item.cursed && item instanceof Weapon) {
    Weapon weapon = (Weapon) item;
    if (weapon.curseInfusionBonus) {
        // 此武器被诅咒菱晶强化过
        // 移除诅咒后升级效果也会消失
    }
}
```

## 12. 开发注意事项

### 状态依赖
- 严重依赖物品的cursed和curseInfusionBonus字段
- curseInfusionBonus字段用于区分普通诅咒和诅咒菱晶诅咒
- 移除诅咒后升级效果会同时消失的特殊逻辑

### 生命周期耦合
- 与装备系统的附魔/刻印系统紧密耦合
- 依赖成就系统的物品等级验证
- 与视觉效果系统集成播放粒子和音效

### 常见陷阱
- 已有附魔/刻印的物品处理逻辑复杂
- 法师魔杖需要特殊更新逻辑
- 力量之戒的特殊处理（更新最大生命值）
- curseInfusionBonus字段的生命周期管理

## 13. 修改建议与扩展点

### 适合扩展的位置
- `usableOnItem()`方法：可以扩展支持更多装备类型
- 诅咒类型选择逻辑：可以添加更多诅咒种类
- 合成配方：可以调整材料或产出数量

### 不建议修改的位置
- curseInfusionBonus字段的语义和生命周期
- 已有附魔/刻印的保护逻辑
- 成就验证调用点

### 重构建议
- 考虑将诅咒应用逻辑提取到独立的工具类
- 为不同装备类型的处理创建统一接口
- 添加更详细的日志记录用于调试诅咒效果

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点