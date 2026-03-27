# CausticBrew 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\items\potions\brews\CausticBrew.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.potions.brews |
| **文件类型** | class |
| **继承关系** | extends Brew |
| **代码行数** | 87 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
CausticBrew（淤泥魔药）实现了一种特殊的魔药，当破碎时会在3格范围内大量溅出腐蚀性淤泥，对范围内的所有角色施加 Ooze（淤泥）负面状态效果，使其缓慢融化，除非及时在水中冲洗掉。

### 系统定位
作为 Brew 抽象类的具体实现，CausticBrew 在游戏的炼金系统中提供了一种基于毒气药剂和粘咕球的特殊投掷物品，专注于持续伤害和 debuff 效果。

### 不负责什么
- 不直接处理融化伤害计算（由 Ooze buff 处理）
- 不管理水中清除逻辑（由 Ooze buff 处理）
- 不处理视觉效果细节（由 Splash 类处理）

## 3. 结构总览

### 主要成员概览
- 静态初始化块：设置图像
- shatter(int cell) 方法：触发腐蚀淤泥效果
- Recipe 内部类：定义合成配方，包含特殊计数逻辑

### 主要逻辑块概览
- 图像配置
- 视觉效果播放
- 距离地图构建（3格范围）
- 范围内角色查找和 Ooze buff 应用
- 合成配方中的任务物品计数

### 生命周期/调用时机
- 对象创建时：设置图像
- 投掷破碎时：shatter() 被调用，应用 Ooze 效果
- 合成时：通过 Recipe 创建实例并记录 GooBlob 使用

## 4. 继承与协作关系

### 父类提供的能力
从 Brew 继承：
- 禁止饮用行为
- 强制投掷为默认操作
- 自动识别状态
- 基础价值和能量计算（使用父类默认值）

### 覆写的方法
- shatter(int cell)：实现具体的腐蚀淤泥效果

### 实现的接口契约
通过 Brew -> Potion -> Item 间接实现所有物品接口。

### 依赖的关键类
- com.shatteredpixel.shatteredpixeldungeon.Assets
- com.shatteredpixel.shatteredpixeldungeon.Dungeon
- com.shatteredpixel.shatteredpixeldungeon.actors.Actor
- com.shatteredpixel.shatteredpixeldungeon.actors.Char
- com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
- com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze
- com.shatteredpixel.shatteredpixeldungeon.effects.Splash
- com.shatteredpixel.shatteredpixeldungeon.items.Item
- com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas
- com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob
- com.shatteredpixel.shatteredpixeldungeon.journal.Catalog
- com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
- com.watabou.utils.BArray
- com.watabou.noosa.audio.Sample
- com.watabou.utils.PathFinder

### 使用者
- 炼金系统通过 Recipe 创建 CausticBrew 实例
- 游戏战斗系统在投掷时调用 shatter() 方法
- 任务追踪系统通过 Catalog 记录 GooBlob 使用

## 5. 字段/常量详解

### 静态常量
无静态常量定义。

### 实例字段
无实例字段定义，继承自父类的所有字段。

## 6. 构造与初始化机制

### 构造器
使用默认构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.BREW_CAUSTIC;
}
```
- 设置物品图像为 BREW_CAUSTIC

### 初始化注意事项
- 无特殊初始化要求
- 使用父类的默认价值和能量值（value=60, energyVal=12）

## 7. 方法详解

### shatter(int cell)

**可见性**：public

**是否覆写**：是，覆写自 Brew

**方法职责**：在指定格子触发淤泥魔药效果，在3格范围内对所有角色施加 Ooze 负面状态

**参数**：
- `cell` (int)：魔药破碎的目标格子

**返回值**：void

**前置条件**：cell 参数应在有效地图范围内

**副作用**：
- 播放破碎音效（如果在英雄视野内）
- 在3格范围内显示黑色溅射效果
- 对范围内的所有角色施加 Ooze buff

**核心实现逻辑**：
1. 调用 splash(cell) 显示破碎视觉效果
2. 如果目标格子在英雄视野内，播放 SHATTER 音效
3. 使用 PathFinder.buildDistanceMap 构建以 cell 为中心、半径为3的距离地图
4. 遍历所有格子，对于距离小于 Integer.MAX_VALUE 的格子：
   - 调用 Splash.at() 显示黑色（0x000000）溅射效果，强度为5
   - 使用 Actor.findChar() 查找该格子的角色
   - 如果找到角色，使用 Buff.affect() 施加 Ooze buff，持续时间为 Ooze.DURATION

**边界情况**：
- 当范围内没有角色时，只显示视觉效果
- 当角色已经在 Ooze 状态下，会刷新持续时间
- 距离计算使用 Manhattan 距离（PathFinder 默认）

## 8. 对外暴露能力

### 显式 API
- 所有继承的公共方法
- shatter() 方法提供具体的腐蚀淤泥效果
- Recipe 内部类提供合成接口

### 内部辅助方法
- 无内部辅助方法，所有逻辑都在 shatter() 方法中

### 扩展入口
- Recipe 内部类可以被炼金系统发现和使用
- shatter() 方法可以被游戏系统调用来触发效果

## 9. 运行机制与调用链

### 创建时机
- 通过炼金釜使用毒气药剂和粘咕球合成（Recipe 类处理）
- 每次合成产出 1 个 CausticBrew

### 调用者
- 炼金系统调用 Recipe.brew() 创建实例
- 投掷系统调用 shatter() 触发效果
- 任务系统通过 Catalog.countUse() 记录 GooBlob 使用

### 被调用者
- 调用 splash() 方法显示破碎效果
- 调用 Sample.INSTANCE.play() 播放音效
- 调用 PathFinder.buildDistanceMap() 构建距离地图
- 调用 Splash.at() 显示溅射效果
- 调用 Actor.findChar() 查找角色
- 调用 Buff.affect() 应用 Ooze buff

### 系统流程位置
- 在炼金合成流程中作为输出物品
- 在战斗流程中作为 debuff 投掷物品使用
- 在任务追踪流程中记录稀有材料使用

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.potions.brews.causticbrew.name | 淤泥魔药 | 物品名称 |
| items.potions.brews.causticbrew.desc | 这瓶魔药在打碎时会大范围地溅出腐蚀淤泥。被腐蚀淤泥影响的单位将会缓慢融化，除非他们能及时在水中冲洗掉淤泥。 | 物品描述 |

### 依赖的资源
- 图像资源：ItemSpriteSheet.BREW_CAUSTIC
- 音效资源：Assets.Sounds.SHATTER
- 合成配方：需要 PotionOfToxicGas 和 GooBlob 作为输入
- 任务追踪：Catalog 系统记录 GooBlob 使用

### 中文翻译来源
来自 core/src/main/assets/messages/items/items_zh.properties 文件，第 807-808 行

## 11. 使用示例

### 基本用法
```java
// 通过炼金合成创建淤泥魔药
ArrayList<Item> ingredients = new ArrayList<>();
ingredients.add(new PotionOfToxicGas());
ingredients.add(new GooBlob());
CausticBrew brewed = (CausticBrew) new CausticBrew.Recipe().brew(ingredients);

// 投掷淤泥魔药
brewed.shatter(targetCell);

// 查询价值（使用父类默认值）
int goldValue = brewed.value(); // 60
int energyValue = brewed.energyVal(); // 12
```

### 合成示例
```java
// 检查合成配方
CausticBrew.Recipe recipe = new CausticBrew.Recipe();
Class<?>[] inputs = recipe.inputs; // [PotionOfToxicGas.class, GooBlob.class]
int[] quantities = recipe.inQuantity; // [1, 1]
int cost = recipe.cost; // 1
Class<?> output = recipe.output; // CausticBrew.class
int outQty = recipe.outQuantity; // 1

// 合成时会自动记录 GooBlob 使用
CausticBrew brew = (CausticBrew) recipe.brew(ingredients);
// Catalog.countUse(GooBlob.class) 已被调用
```

## 12. 开发注意事项

### 状态依赖
- 依赖 Dungeon.level.heroFOV 确定是否播放音效
- 依赖 Actor 系统查找范围内的角色
- 依赖 Buff 系统管理 Ooze 状态

### 生命周期耦合
- 与 Ooze buff 的生命周期耦合
- 与 Catalog 任务追踪系统的耦合
- 与 PathFinder 距离计算系统的耦合

### 常见陷阱
- 忘记 GooBlob 是任务物品，需要在 Catalog 中记录使用
- 误以为 Ooze 效果会立即造成伤害（实际上是持续伤害）
- 忽略距离地图构建的性能影响（但3格范围很小，影响不大）

## 13. 修改建议与扩展点

### 适合扩展的位置
- Recipe 类：可以修改合成成本或添加更多输入材料
- shatter() 方法：可以调整范围、持续时间或添加额外效果
- Ooze 持续时间：可以通过修改 Ooze.DURATION 常量来平衡

### 不建议修改的位置
- 核心距离范围：3格是经过平衡设计的数值
- 视觉效果颜色：黑色（0x000000）符合腐蚀淤泥的主题

### 重构建议
- 可以将距离范围提取为常量以提高可维护性
- 可以考虑将 Ooze 持续时间作为配置参数

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点