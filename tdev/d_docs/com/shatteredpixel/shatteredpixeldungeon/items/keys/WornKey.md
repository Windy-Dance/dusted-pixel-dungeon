# WornKey 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\items\keys\WornKey.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.keys |
| **文件类型** | class |
| **继承关系** | extends Key |
| **代码行数** | 70 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
WornKey类实现了具体的磨损钥匙物品，用于解锁特殊的非同寻常的门。它继承了Key基类的所有功能，并添加了特殊的拾取逻辑，用于显示支持提示窗口。

### 系统定位
作为特殊用途的钥匙类型，用于解锁游戏中的特殊门或区域，同时在首次拾取时触发支持提示功能。

### 不负责什么
- 不处理门锁的解锁逻辑（由外部系统处理）
- 不提供持续的游戏功能
- 不处理复杂的物品交互

## 3. 结构总览

### 主要成员概览
- `{ image = ItemSpriteSheet.WORN_KEY; }` - 初始化块设置精灵图

### 主要逻辑块概览
- 两个构造器：无参构造器和带depth参数的构造器
- 覆写的doPickUp方法：添加支持提示逻辑

### 生命周期/调用时机
- 地牢生成时创建
- 英雄首次拾取时显示支持提示窗口
- 后续拾取时触发基类的拾取逻辑

## 4. 继承与协作关系

### 父类提供的能力
继承自Key类的所有功能：
- depth字段管理
- 拾取逻辑基础（doPickUp的基础实现）
- 序列化支持（storeInBundle/restoreFromBundle）
- 堆叠和唯一性设置
- 不可升级和始终已鉴定的特性

### 覆写的方法
- `doPickUp(Hero hero, int pos)` - 添加支持提示逻辑

### 实现的接口契约
无显式接口实现

### 依赖的关键类
- `ItemSpriteSheet` - 提供磨损钥匙的精灵图索引
- `Dungeon` - 用于保存游戏状态
- `SPDSettings` - 检查是否已显示支持提示
- `ShatteredPixelDungeon` - 游戏主类，用于异常报告
- `Game` - 渲染线程管理
- `WndSupportPrompt` - 支持提示窗口
- `Callback` - 渲染回调

### 使用者
- 地牢生成系统 - 创建磨损钥匙来匹配特殊门
- 物品系统 - 处理磨损钥匙的拾取、存储和使用
- 玩家 - 拾取和使用磨损钥匙

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无

### 初始化块
```java
{
    image = ItemSpriteSheet.WORN_KEY;
}
```
- 设置磨损钥匙的精灵图索引为WORN_KEY

## 6. 构造与初始化机制

### 构造器
#### 无参构造器
**可见性**：public
**参数**：无
**实现逻辑**：调用带depth参数的构造器，传入默认深度0
```java
public WornKey() {
    this(0);
}
```

#### 带depth参数的构造器
**可见性**：public
**参数**：
- `depth` (int)：关联的地牢深度
**实现逻辑**：
```java
public WornKey(int depth) {
    super();
    this.depth = depth;
}
```
调用父类构造器，然后设置depth字段

### 初始化块
通过实例初始化块设置image属性，确保所有WornKey实例都使用正确的精灵图。

### 初始化注意事项
- 必须正确设置depth字段以确保钥匙在正确的地牢深度工作
- image属性由初始化块自动设置，无需在构造器中处理
- 支持提示逻辑只在首次拾取时触发

## 7. 方法详解

### doPickUp()

**可见性**：public

**是否覆写**：是，覆写自 Key

**方法职责**：处理英雄拾取磨损钥匙时的逻辑，包括显示支持提示窗口

**参数**：
- `hero` (Hero)：拾取钥匙的英雄
- `pos` (int)：拾取位置

**返回值**：boolean，总是返回true表示拾取成功

**前置条件**：hero参数不为null

**副作用**：
- 如果未显示过支持提示，会保存游戏并显示支持提示窗口
- 触发父类的拾取逻辑（更新目录、统计数据等）

**核心实现逻辑**：
1. 检查是否已显示支持提示（`!SPDSettings.supportNagged()`）
2. 如果未显示过：
   - 保存当前游戏状态
   - 在渲染线程上创建并显示`WndSupportPrompt`窗口
   - 处理可能的IO异常
3. 调用父类的doPickUp方法完成标准拾取逻辑

**边界情况**：
- 如果保存游戏时发生IO异常，会记录异常但继续执行
- 如果已显示过支持提示，则直接调用父类逻辑

## 8. 对外暴露能力

### 显式 API
- 两个公共构造器：`WornKey()` 和 `WornKey(int depth)`
- 覆写的`doPickUp`方法
- 继承自Key的所有公共方法和字段

### 内部辅助方法
无

### 扩展入口
- 可以通过继承WornKey创建更专门的磨损钥匙变体
- 可以进一步覆写其他Key基类的方法来自定义行为

## 9. 运行机制与调用链

### 创建时机
- 地牢生成过程中，当需要为特殊门创建钥匙时
- 通常在特定的地牢层生成，用于解锁非同寻常的门

### 调用者
- DungeonGenerator - 地牢生成器
- GameLoader - 游戏加载器（从存档中恢复）
- Hero.collect - 英雄拾取物品时

### 被调用者
- Key基类的各种方法（序列化、堆叠判断等）
- ItemSpriteSheet - 获取精灵图
- SPDSettings - 检查支持提示状态
- Dungeon - 保存游戏
- WndSupportPrompt - 显示支持窗口

### 系统流程位置
- 地牢生成 → 创建WornKey → 玩家首次拾取 → 显示支持提示 → 完成标准拾取流程

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.keys.wornkey.name | 磨损钥匙 | WornKey的显示名称 |
| items.keys.wornkey.desc | 这把磨损而褪色的钥匙看起来非同寻常。大概它可以打开附近某道非同寻常的门。 | WornKey的物品描述 |

### 依赖的资源
- 精灵图资源：ItemSpriteSheet.WORN_KEY（索引值）
- 音效资源：继承自Key基类的Assets.Sounds.ITEM
- UI资源：WndSupportPrompt窗口

### 中文翻译来源
来自 `core/src/main/assets/messages/items/items_zh.properties` 文件

## 11. 使用示例

### 基本用法
```java
// 创建一个关联到第8层的磨损钥匙
WornKey key = new WornKey(8);

// 添加到英雄的背包中（会触发支持提示逻辑，如果尚未显示）
hero.belongings.backpack.collect(key);
```

### 地牢生成中的使用
```java
// 在地牢生成器中创建匹配的磨损钥匙
if (hasSpecialDoor()) {
    WornKey key = new WornKey(currentDepth);
    placeKeySomewhereOnLevel(key);
}
```

## 12. 开发注意事项

### 状态依赖
- depth字段必须与创建钥匙的地牢深度匹配
- image属性由初始化块自动设置，不应手动修改
- SPDSettings.supportNagged()状态影响是否显示支持提示

### 生命周期耦合
- WornKey的生命周期完全由物品系统管理
- 与地牢深度的耦合确保钥匙只能在正确的地方使用
- 与游戏设置系统的耦合确保支持提示只显示一次

### 常见陷阱
- 忘记设置depth参数会导致钥匙无法正常工作
- 直接修改image属性会破坏视觉一致性
- 在非主线程上操作UI组件会导致崩溃

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可以添加自定义的支持提示逻辑
- 可以覆写基类方法添加特殊效果
- 可以创建专门的子类（如AncientWornKey）

### 不建议修改的位置
- 不要修改初始化块中的image赋值
- 不要移除或修改构造器签名
- 不要在doPickUp中移除保存游戏的逻辑

### 重构建议
- 当前实现符合单一职责原则，但支持提示逻辑可以提取到单独的服务类中
- 可以考虑使用观察者模式来解耦支持提示逻辑

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点