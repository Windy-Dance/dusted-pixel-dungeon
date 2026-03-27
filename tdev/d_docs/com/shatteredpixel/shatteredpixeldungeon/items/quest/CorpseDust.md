# 尸尘文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\items\quest\CorpseDust.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.quest |
| **文件类型** | class |
| **继承关系** | extends Item |
| **代码行数** | 216 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
尸尘是一个被诅咒的任务物品，当玩家拾取它时会触发幽灵生成机制。它会持续在玩家周围召唤DustWraith（尘埃幽灵）敌人，并影响任务评分。

### 系统定位
该类属于任务物品系统的一部分，专门处理与尸尘相关的游戏逻辑，包括幽灵生成、任务评分惩罚以及音乐管理。

### 不负责什么
- 不负责生成尸尘（由任务系统生成）
- 不处理其他类型的幽灵或怪物
- 不管理用户界面显示逻辑

## 3. 结构总览

### 主要成员概览
- 内部类 `DustGhostSpawner`：幽灵生成器buff
- 内部类 `DustWraith`：尘埃幽灵怪物类

### 主要逻辑块概览
- 物品属性初始化
- 拾取事件处理（触发幽灵生成）
- 分离事件处理（停止幽灵生成）
- 幽灵生成逻辑（基于能量累积）
- 幽灵攻击逻辑（影响任务评分）

### 生命周期/调用时机
- 游戏加载时：从Bundle恢复状态
- 玩家拾取时：触发DustGhostSpawner buff
- 物品分离时：移除buff并清除所有已生成的幽灵
- Buff更新时：累积能量并可能生成新幽灵

## 4. 继承与协作关系

### 父类提供的能力
- `Item`类提供的基础物品功能
- 持久化支持
- 标准物品操作方法

### 覆写的方法
- `actions(Hero hero)`: 返回空列表，禁止丢弃
- `isUpgradable()`: 返回false，不可升级
- `isIdentified()`: 返回true，始终已鉴定
- `doPickUp(Hero hero, int pos)`: 拾取时触发幽灵生成
- `onDetach()`: 分离时清理幽灵和Buff

### 实现的接口契约
无直接实现的接口

### 依赖的关键类
- `Assets`: 音频资源
- `Dungeon`: 游戏状态管理
- `Statistics`: 任务评分统计
- `Actor`: 角色管理
- `Char`: 角色基类
- `Buff`: 状态效果基类
- `Hero`: 英雄角色
- `Mob`: 怪物基类
- `Wraith`: 幽灵基类
- `Item`: 物品基类
- `Messages`: 国际化消息
- `ItemSpriteSheet`: 物品精灵图
- `GLog`: 游戏日志
- `Game`: 游戏主类
- `Music`: 音乐管理
- `Sample`: 音频播放
- `Bundle`: 数据持久化
- `Callback`: 回调接口
- `Random`: 随机数生成

### 使用者
- 任务系统：在玩家完成特定任务后给予尸尘
- 游戏系统：管理尸尘的持有和分离逻辑

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无静态常量 | - | - | - |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| cursed | boolean | true | 标记为被诅咒的物品 |
| cursedKnown | boolean | true | 标记为已知被诅咒 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过实例初始化块进行初始化。

### 初始化块
```java
{
    image = ItemSpriteSheet.DUST;
    cursed = true;
    cursedKnown = true;
    unique = true;
}
```
- 设置物品图像为尘土图标
- 标记为被诅咒且已知被诅咒
- 标记为唯一物品

### 初始化注意事项
- 尸尘不能被丢弃（actions方法返回空列表）
- 拾取时会自动触发幽灵生成机制

## 7. 方法详解

### actions(Hero hero)
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：获取物品可用的操作列表

**参数**：
- `hero` (Hero)：当前英雄

**返回值**：ArrayList<String>，空列表

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return new ArrayList<>(); //yup, no dropping this one
```

**边界情况**：无

### isUpgradable()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：确定物品是否可升级

**参数**：无

**返回值**：boolean，始终返回false

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return false;
```

**边界情况**：无

### isIdentified()
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：确定物品是否已鉴定

**参数**：无

**返回值**：boolean，始终返回true

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return true;
```

**边界情况**：无

### doPickUp(Hero hero, int pos)
**可见性**：public

**是否覆写**：是，覆写自 Item

**方法职责**：处理物品被拾取的逻辑

**参数**：
- `hero` (Hero)：执行拾取操作的英雄
- `pos` (int)：物品所在位置

**返回值**：boolean，表示拾取是否成功

**前置条件**：物品必须在指定位置且可被拾取

**副作用**：
- 显示"一股寒意穿透了你的脊背"消息
- 应用DustGhostSpawner buff到英雄身上

**核心实现逻辑**：
1. 调用父类的doPickUp方法
2. 如果拾取成功，显示消息并应用DustGhostSpawner buff

**边界情况**：拾取失败时不应用buff

### onDetach()
**可见性**：protected

**是否覆写**：是，覆写自 Item

**方法职责**：处理物品从背包中分离的逻辑

**参数**：无

**返回值**：void

**前置条件**：物品正在从背包中移除

**副作用**：
- 移除DustGhostSpawner buff
- 清除所有已生成的DustWraith
- 淡出当前音乐并恢复关卡音乐

**核心实现逻辑**：
1. 查找并移除DustGhostSpawner buff
2. 如果找到buff，调用其dispel()方法
3. dispel()方法会清除所有DustWraith并恢复音乐

**边界情况**：如果没有DustGhostSpawner buff则不执行任何操作

## 8. 对外暴露能力

### 显式 API
- 所有public方法都是显式API的一部分

### 内部辅助方法
- `DustGhostSpawner`和`DustWraith`内部类不应被外部直接使用

### 扩展入口
- 无特定的扩展入口，因为这是具体的任务物品类

## 9. 运行机制与调用链

### 创建时机
- 由任务系统在玩家完成特定任务后生成

### 调用者
- 游戏系统（拾取操作）
- 背包系统（分离操作）
- Buff系统（更新和清理）

### 被调用者
- `Buff.affect()`: 应用幽灵生成器buff
- `GLog.n()`: 显示消息
- `Dungeon.hero.belongings.getItem()`: 检查物品是否存在
- `Wraith.spawnAt()`: 生成幽灵
- `Sample.INSTANCE.play()`: 播放音效
- `Music.INSTANCE.fadeOut()`: 淡出音乐
- `Dungeon.level.playLevelMusic()`: 恢复关卡音乐

### 系统流程位置
- 位于物品系统、Buff系统和任务系统的交界处
- 参与死亡相关任务的核心流程

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.quest.corpsedust.name | 尸尘 | 物品名称 |
| items.quest.corpsedust.chill | 一股寒意穿透了你的脊背。 | 拾取消息 |
| items.quest.corpsedust.desc | 在外观上这团尸尘和普通灰尘差不多。而你却能够感受到其中潜伏着一股充满恶意的魔力。<br><br>尽快脱手为好。 | 物品描述 |
| items.quest.corpsedust.discover_hint | 你可在某个任务中找到该物品。 | 发现提示 |

### 依赖的资源
- 图像资源：ItemSpriteSheet.DUST
- 音频资源：Assets.Sounds.CURSED

### 中文翻译来源
来自 D:\Develop\Workspace\DustedPixelDungeon\core\src\main\assets\messages\items\items_zh.properties

## 11. 使用示例

### 基本用法
```java
// 创建尸尘（通常由任务系统完成）
CorpseDust dust = new CorpseDust();

// 玩家拾取尸尘
if (dust.doPickUp(hero, position)) {
    // 自动触发幽灵生成机制
    // 显示"一股寒意穿透了你的脊背"消息
}

// 当尸尘被移除时（例如任务完成）
// 自动清理所有生成的幽灵并恢复音乐
```

### 扩展示例
不适用，因为这是具体的任务物品类，不设计用于扩展。

## 12. 开发注意事项

### 状态依赖
- 幽灵生成依赖于DustGhostSpawner buff的存在
- Buff会检查尸尘是否仍然在背包中

### 生命周期耦合
- 与任务系统紧密耦合
- 与音乐系统有交互（淡出和恢复）

### 常见陷阱
- 忘记处理Buff的清理可能导致幽灵继续生成
- 在非任务场景中使用可能产生意外行为

## 13. 修改建议与扩展点

### 适合扩展的位置
- 幽灵生成逻辑可以提取为可配置的参数
- 任务评分惩罚机制可以更加灵活

### 不建议修改的位置
- 核心的不可丢弃逻辑（actions方法）
- Buff的生命周期管理逻辑

### 重构建议
- 可以考虑将幽灵生成参数（如能量需求计算）提取为配置常量

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点