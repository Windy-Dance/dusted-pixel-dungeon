# ChasmBridgeRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/rooms/standard/ChasmBridgeRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends StandardBridgeRoom |
| **代码行数** | 36 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
ChasmBridgeRoom 类实现了一种特殊的房间布局，其中包含一个被深渊（CHASM）包围的桥梁结构。该房间在标准桥式房间的基础上，将空间区域填充为深渊地形，并确保桥梁结构保持可通行。

### 系统定位
作为标准房间类型的变体，ChasmBridgeRoom 在地牢生成过程中被随机选择创建，用于增加关卡的多样性和视觉变化，同时提供危险的深渊环境。它属于 StandardBridgeRoom 的具体实现类。

### 不负责什么
- 不负责物品生成逻辑
- 不负责怪物生成逻辑  
- 不负责房间连接逻辑
- 不直接处理玩家与深渊的交互逻辑

## 3. 结构总览

### 主要成员概览
- 继承自 StandardBridgeRoom 的所有字段和方法
- protected int maxBridgeWidth( int roomDimension )
- protected int spaceTile()

### 主要逻辑块概览
- 桥梁宽度计算逻辑
- 空间地形指定逻辑（深渊）
- 深渊地形的危险性处理（通过 Terrain flags）

### 生命周期/调用时机
- 在地牢生成阶段通过 StandardRoom.createRoom() 静态方法创建
- paint() 方法在 Level 生成过程中被调用以绘制房间地形

## 4. 继承与协作关系

### 父类提供的能力
从 StandardBridgeRoom 继承：
- paint(Level level) 方法实现
- bridgeRect 和 spaceRect 字段
- min/max width/height 重写
- canMerge(), canPlaceItem(), canPlaceCharacter() 方法

从 StandardRoom 继承：
- SizeCategory 枚举和相关方法
- sizeCat 字段和 setSizeCat() 方法
- sizeFactor(), mobSpawnWeight(), connectionWeight() 方法
- minWidth()/maxWidth(), minHeight()/maxHeight() 方法

从 Room 继承：
- 空间和连接逻辑
- 抽象 paint() 方法
- 各种放置点检查方法

### 覆写的方法
- maxBridgeWidth(int roomDimension) - 重写以提供具体的桥梁宽度限制
- spaceTile() - 重写以返回 Terrain.CHASM

### 实现的接口契约
- 实现 StandardBridgeRoom 抽象类的所有抽象方法要求

### 依赖的关键类
- com.shatteredpixel.shatteredpixeldungeon.levels.Terrain

### 使用者
- StandardRoom.createRoom() 静态工厂方法
- Level 生成系统

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无（所有字段均继承自父类）

## 6. 构造与初始化机制

### 构造器
使用默认构造器，无自定义构造逻辑。

### 初始化块
无自定义初始化块。

### 初始化注意事项
- 依赖父类 StandardBridgeRoom 的初始化逻辑
- sizeCat 字段在 StandardRoom 构造时通过 setSizeCat() 初始化

## 7. 方法详解

### maxBridgeWidth(int roomDimension)

**可见性**：protected

**是否覆写**：是，覆写自 StandardBridgeRoom

**方法职责**：根据房间维度计算最大桥梁宽度

**参数**：
- `roomDimension` (int)：房间的宽度或高度尺寸

**返回值**：int，最大桥梁宽度（房间尺寸>=7时返回2，否则返回1）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return roomDimension >= 7 ? 2 : 1;
```

**边界情况**：对于小于7的房间尺寸，桥梁宽度限制为1；7及以上则为2

### spaceTile()

**可见性**：protected

**是否覆写**：是，覆写自 StandardBridgeRoom

**方法职责**：指定空间区域的地形类型

**参数**：无

**返回值**：int，Terrain.CHASM（值为0）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
直接返回 Terrain.CHASM 常量

**边界情况**：无

## 8. 对外暴露能力

### 显式 API
- 继承的所有公共方法

### 内部辅助方法
- maxBridgeWidth() - 受保护，仅供内部使用
- spaceTile() - 受保护，仅供内部使用

### 扩展入口
- 可通过继承扩展其他桥式房间变体

## 9. 运行机制与调用链

### 创建时机
- 在 StandardRoom.createRoom() 中通过反射创建实例

### 调用者
- StandardRoom.createRoom() 静态方法
- Level 生成系统的房间绘制阶段

### 被调用者
- StandardBridgeRoom.paint() - 绘制房间布局
- Terrain.CHASM - 获取深渊地形常量

### 系统流程位置
- 地牢生成流程中的房间类型选择和绘制阶段

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| levels.level.chasm_name | 深渊 | 深渊地形的显示名称 |
| levels.level.chasm_desc | 你看不到底。 | 深渊地形的描述 |

### 依赖的资源
- Terrain.CHASM 地形类型（值为0）
- 深渊相关的视觉和音效资源

### 中文翻译来源
来自 levels_zh.properties 文件：
- levels.level.chasm_name = 深渊

## 11. 使用示例

### 基本用法
```java
// 通过工厂方法创建实例（实际使用中由系统自动调用）
StandardRoom room = StandardRoom.createRoom(); // 可能返回 ChasmBridgeRoom 实例

// 房间绘制（由Level系统自动调用）
room.paint(level);
```

### 扩展示例
不适用，此类为具体实现类，通常不需要进一步扩展。

## 12. 开发注意事项

### 状态依赖
- 依赖父类 StandardBridgeRoom 的 spaceRect 和 bridgeRect 字段状态
- 依赖 StandardRoom 的 sizeCat 字段

### 生命周期耦合
- 必须在正确的时间点调用 paint() 方法
- paint() 方法只能调用一次，后续调用可能导致异常

### 常见陷阱
- 不要直接实例化，应通过 StandardRoom.createRoom() 工厂方法
- 不要修改 spaceTile() 返回值，这会影响房间的核心功能
- 注意 CHASM 地形具有 AVOID | PIT 标志，会阻止角色移动并造成坠落伤害

## 13. 修改建议与扩展点

### 适合扩展的位置
- 如需创建类似但不同的桥式房间变体，可继承 StandardBridgeRoom 创建新类
- 可重写 maxBridgeWidth() 方法调整桥梁尺寸规则

### 不建议修改的位置
- spaceTile() 方法不应修改，这定义了此类的本质
- 桥梁宽度限制逻辑不应随意修改，这会影响游戏平衡

### 重构建议
- 当前实现简洁有效，无需重构
- 如果需要更多桥式房间变体，可考虑提取共同逻辑到中间抽象类

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点