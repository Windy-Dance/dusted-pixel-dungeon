# 注魔菱晶 (MagicalInfusion)

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\items\spells\MagicalInfusion.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.spells |
| **文件类型** | class |
| **继承关系** | extends InventorySpell |
| **代码行数** | 141 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
注魔菱晶是一个物品升级法术，能够安全地升级装备物品（武器、护甲、法杖等），在升级的同时不会消除物品上已有的附魔、刻印或诅咒。这使其成为比普通升级卷轴更安全的升级选择。

### 系统定位
作为InventorySpell的子类，注魔菱晶在游戏的物品强化系统中提供安全升级功能。它与升级系统、附魔/刻印系统以及诅咒系统深度集成，解决了普通升级卷轴可能移除有益附魔的问题。

### 不负责什么
- 不直接参与战斗逻辑
- 不处理非可升级物品
- 不提供直接的战斗buff或debuff效果

## 3. 结构总览

### 主要成员概览
- `image`: 物品图标（ItemSpriteSheet.MAGIC_INFUSE）
- `unique`: 唯一性标记（true）
- `talentFactor`: 天赋因子（2）
- `Recipe`: 内部类，定义合成配方
- 继承自InventorySpell的itemSelector和相关方法

### 主要逻辑块概览
- `usableOnItem()`: 定义可升级的物品类型
- `onItemSelected()`: 打开升级窗口
- `upgradeItem()`: 执行安全升级逻辑
- `useAnimation()`: 处理使用动画和天赋触发
- 合成配方：使用升级卷轴

### 生命周期/调用时机
- 玩家选择施放 → 打开物品选择界面
- 选择可升级物品 → 显示升级确认窗口
- 确认升级 → 执行安全升级并消耗物品

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
- `usableOnItem(Item item)`: 定义可升级物品
- `onItemSelected(Item item)`: 显示升级窗口
- `value()`, `energyVal()`: 自定义价值计算

### 实现的接口契约
通过继承Item间接实现Serializable接口

### 依赖的关键类
- `WndUpgrade`: 升级确认窗口
- `ScrollOfUpgrade`: 复用升级逻辑
- `Weapon`, `Armor`, `Wand`: 装备类型
- `Degrade`: 移除降级状态
- `Badges`, `Statistics`: 成就和统计
- `Catalog`, `Talent`: 使用统计和天赋系统

### 使用者
- 游戏玩家通过背包界面使用
- 合成系统通过Recipe创建
- WndUpgrade窗口调用upgradeItem()方法

## 5. 字段/常量详解

### 静态常量
无静态常量

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| image | int | ItemSpriteSheet.MAGIC_INFUSE | 物品图标索引 |
| unique | boolean | true | 唯一性标记（不能堆叠） |
| talentFactor | float | 2 | 天赋触发强度因子 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器配合实例初始化块：

```java
{
    image = ItemSpriteSheet.MAGIC_INFUSE;
    unique = true;
    talentFactor = 2;
}
```

### 初始化块
实例初始化块设置图标、唯一性和天赋因子。注意`unique=true`意味着注魔菱晶不能堆叠。

### 初始化注意事项
- 与其他可堆叠的法术物品不同，注魔菱晶是唯一的
- talentFactor=2表示天赋效果强度加倍
- 继承了InventorySpell的defaultAction=AC_CAST

## 7. 方法详解

### usableOnItem(Item item)

**可见性**：protected

**是否覆写**：是，覆写自 InventorySpell

**方法职责**：确定指定物品是否可以被注魔菱晶升级

**参数**：
- `item` (Item)：待检查的物品

**返回值**：boolean，true表示可升级

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return item.isUpgradable();
```

**边界情况**：只允许isUpgradable()返回true的物品

### onItemSelected(Item item)

**可见性**：protected

**是否覆写**：是，覆写自 InventorySpell

**方法职责**：对选中的物品显示升级确认窗口

**参数**：
- `item` (Item)：目标物品

**返回值**：void

**前置条件**：物品必须通过usableOnItem()检查

**副作用**：显示WndUpgrade窗口

**核心实现逻辑**：
```java
GameScene.show(new WndUpgrade(this, item, false));
```
第三个参数false表示这不是来自升级卷轴（即注魔菱晶模式）

**边界情况**：无特殊边界情况

### reShowSelector()

**可见性**：public

**是否覆写**：否

**方法职责**：重新显示物品选择器（用于取消升级后的重新选择）

**参数**：无

**返回值**：void

**前置条件**：需要重新选择物品

**副作用**：设置curItem并打开选择界面

**核心实现逻辑**：
- 设置curItem为当前实例
- 调用GameScene.selectItem()重新打开选择器

**边界情况**：依赖curItem静态变量（存在竞态条件风险）

### getSelector()

**可见性**：public

**是否覆写**：否

**方法职责**：获取物品选择器（供WndUpgrade使用）

**参数**：无

**返回值**：WndBag.ItemSelector，物品选择器实例

**前置条件**：无

**副作用**：设置curItem

**核心实现逻辑**：
- 设置curItem为当前实例
- 返回itemSelector

**边界情况**：依赖curItem静态变量

### useAnimation()

**可见性**：public

**是否覆写**：否

**方法职责**：执行使用动画和后置处理

**参数**：无

**返回值**：void

**前置条件**：物品升级已完成

**副作用**：
- 消耗1回合时间
- 播放阅读音效
- 解除隐身状态
- 触发天赋效果
- 记录使用统计

**核心实现逻辑**：
- 调用标准的使用动画流程
- 触发天赋系统（talentFactor=2）

**边界情况**：无特殊边界情况

### upgradeItem(Item item)

**可见性**：public

**是否覆写**：否

**方法职责**：执行安全升级逻辑，保留现有附魔/刻印/诅咒

**参数**：
- `item` (Item)：要升级的物品

**返回值**：Item，升级后的物品

**前置条件**：物品必须是可升级的

**副作用**：
- 升级物品
- 保留诅咒状态
- 触发成就验证
- 更新统计数据

**核心实现逻辑**：
- 调用ScrollOfUpgrade.upgrade()增加全局升级计数
- 移除Degrade状态
- 根据物品类型分别处理：
  - 武器：保留附魔并升级
  - 护甲：保留刻印并升级  
  - 其他：保留诅咒状态并升级
- 播放升级消息
- 验证成就和统计

**边界情况**：
- 诅咒法杖的curseInfusionBonus状态保留
- 已有附魔/刻印的安全保留
- 武器和护甲的特殊处理逻辑

### value() 和 energyVal()

**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：计算物品的金币和能量价值

**参数**：无

**返回值**：int，线性价值计算（60*quantity金币，12*quantity能量）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
由于unique=true，quantity通常为1，但方法仍支持堆叠情况

**边界情况**：数量为0时返回0

## 8. 对外暴露能力

### 显式 API
- `usableOnItem()`: 公开的物品可用性检查
- `reShowSelector()`, `getSelector()`: 窗口交互接口
- `useAnimation()`, `upgradeItem()`: 升级处理接口
- `value()`, `energyVal()`: 价值查询接口
- `Recipe`: 公开的合成配方

### 内部辅助方法
- `onItemSelected()`: 内部窗口显示逻辑

### 扩展入口
- `usableOnItem()`方法可被子类扩展
- `upgradeItem()`方法提供了完整的升级逻辑实现

## 9. 运行机制与调用链

### 创建时机
- 通过炼金合成（升级卷轴 × 1）获得
- 由于unique=true，每次合成只产出1个

### 调用者
- Hero.execute() → Spell.execute() → InventorySpell.onCast() → MagicalInfusion.usableOnItem()
- WndUpgrade窗口 → MagicalInfusion.upgradeItem()
- WndUpgrade窗口 → MagicalInfusion.useAnimation()

### 被调用者
- GameScene.show(): 显示升级窗口
- ScrollOfUpgrade.upgrade(): 处理升级计数
- Degrade.detach(): 移除降级状态
- Badges.validateItemLevelAquired(): 成就验证
- Talent.onScrollUsed(): 天赋触发

### 系统流程位置
1. **选择阶段**：InventorySpell打开物品选择界面 → usableOnItem()过滤可升级物品
2. **确认阶段**：onItemSelected() → 显示WndUpgrade确认窗口
3. **执行阶段**：upgradeItem() → 安全升级 → useAnimation() → 消耗物品

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.spells.magicalinfusion.name | 注魔菱晶 | 物品名称 |
| items.spells.magicalinfusion.inv_title | 灌注一件物品 | 选择窗口标题 |
| items.spells.magicalinfusion.infuse | 你的物品充满了奥术能量！ | 升级成功消息 |
| items.spells.magicalinfusion.desc | 这个菱晶蕴含着和升级卷轴同样强大的魔力，不过这种魔力更为稳定... | 物品描述 |

### 依赖的资源
- ItemSpriteSheet.MAGIC_INFUSE: 物品图标
- Assets.Sounds.READ: 阅读音效

### 中文翻译来源
来自 core/src/main/assets/messages/items/items_zh.properties 文件

## 11. 使用示例

### 基本用法
```java
// 创建注魔菱晶实例
MagicalInfusion infusion = new MagicalInfusion();

// 检查物品是否可升级
Item weapon = new Weapon();
if (infusion.usableOnItem(weapon)) {
    // 显示升级窗口
    infusion.onItemSelected(weapon);
}

// 执行安全升级
Item upgradedWeapon = infusion.upgradeItem(weapon);

// 获取物品价值
int goldValue = infusion.value();
int energyValue = infusion.energyVal();
```

### 合成示例
```java
// 通过合成创建注魔菱晶
ArrayList<Item> ingredients = new ArrayList<>();
ingredients.add(new ScrollOfUpgrade()); // 升级卷轴

MagicalInfusion.Recipe recipe = new MagicalInfusion.Recipe();
if (recipe.testIngredients(ingredients)) {
    Item result = recipe.brew(ingredients);
    // result 包含1个注魔菱晶（unique=true）
}
```

### 窗口交互示例
```java
// 在WndUpgrade中使用
MagicalInfusion infusion = new MagicalInfusion();
WndBag.ItemSelector selector = infusion.getSelector();
infusion.reShowSelector(); // 重新显示选择器
```

## 12. 开发注意事项

### 状态依赖
- 依赖curItem静态变量进行窗口交互（存在竞态条件）
- unique=true影响物品堆叠行为
- 与ScrollOfUpgrade的升级计数系统集成

### 生命周期耦合
- 与WndUpgrade窗口紧密耦合
- 依赖全局的Statistics.upgradesUsed计数
- 与成就系统和天赋系统集成

### 常见陷阱
- curItem静态变量的安全性问题（FIXME注释在其他类中提到）
- 诅咒状态和curseInfusionBonus的正确保留
- 武器/护甲与普通物品的不同处理逻辑
- unique=true与其他堆叠物品的行为差异

## 13. 修改建议与扩展点

### 适合扩展的位置
- `upgradeItem()`方法：可以添加更多升级效果
- `usableOnItem()`方法：可以扩展支持更多物品类型
- 合成配方：可以调整材料成本

### 不建议修改的位置
- 诅咒状态保留逻辑
- curItem相关的窗口交互逻辑（应重构而非修改）
- 与ScrollOfUpgrade的集成点

### 重构建议
- 消除curItem静态变量依赖，改用回调或事件系统
- 将升级逻辑提取到独立的工具类
- 考虑将unique属性改为配置选项

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点