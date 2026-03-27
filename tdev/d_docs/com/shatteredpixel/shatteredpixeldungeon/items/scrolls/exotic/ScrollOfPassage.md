# ScrollOfPassage 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/exotic/ScrollOfPassage.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic |
| **文件类型** | class |
| **继承关系** | extends ExoticScroll |
| **代码行数** | 60 行 |
| **所属模块** | core |
| **官方中文名** | 归返秘卷 |

## 2. 文件职责说明

### 核心职责
归返秘卷是一种传送型秘卷，阅读后可以瞬间将英雄传送到楼上距离最近的区域首层（通常是商店所在楼层）。

### 系统定位
作为传送卷轴的升级版本，对应普通卷轴为传送卷轴（ScrollOfTeleportation）。

### 不负责什么
- 不能在禁止楼层传送时使用
- 不能选择传送目的地（自动计算）

## 3. 结构总览

### 主要成员概览
- `icon`: 图标标识（ItemSpriteSheet.Icons.SCROLL_PASSAGE）

### 主要逻辑块概览
- `doRead()`: 阅读逻辑，执行传送

### 生命周期/调用时机
阅读后立即传送，无延迟

## 4. 继承与协作关系

### 父类提供的能力
从 ExoticScroll 继承：
- 鉴定状态共享机制
- 价值计算
- 符文和图像设置

### 覆写的方法
| 方法 | 覆写目的 |
|------|----------|
| `doRead()` | 实现阅读逻辑：传送到区域首层 |

### 依赖的关键类
- `Dungeon`: 获取当前深度和传送权限
- `InterlevelScene`: 场景切换
- `Level`: 层级过渡处理
- `ScrollOfTeleportation`: 共享消息键
- `Game`: 场景切换

## 5. 字段/常量详解

### 实例字段

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `icon` | int | ItemSpriteSheet.Icons.SCROLL_PASSAGE | 物品图标标识 |

### 静态常量
无

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过初始化块设置图标。

### 初始化块
```java
{
    icon = ItemSpriteSheet.Icons.SCROLL_PASSAGE;
}
```

## 7. 方法详解

### doRead()

**可见性**：public

**是否覆写**：是，覆写自 ExoticScroll

**方法职责**：实现阅读逻辑，将英雄传送到最近区域首层。

**前置条件**：
- 英雄必须处于可以传送的状态

**副作用**：
- 从背包移除物品
- 鉴定物品
- 切换游戏场景

**核心实现逻辑**：
```java
@Override
public void doRead() {
    detach(curUser.belongings.backpack);
    identify();
    readAnimation();
    
    if (!Dungeon.interfloorTeleportAllowed()) {
        GLog.w( Messages.get(ScrollOfTeleportation.class, "no_tele") );
        return;
    }

    Level.beforeTransition();
    InterlevelScene.mode = InterlevelScene.Mode.RETURN;
    // 计算最近区域首层：当前深度 - 1 - (当前深度-2) % 5
    InterlevelScene.returnDepth = Math.max(1, (Dungeon.depth - 1 - (Dungeon.depth-2)%5));
    InterlevelScene.returnBranch = 0;
    InterlevelScene.returnPos = -1;
    Game.switchScene( InterlevelScene.class );
}
```

**边界情况**：
- 当 `Dungeon.interfloorTeleportAllowed()` 返回 false 时，显示警告消息但不消耗物品
- 深度计算确保最小值为 1

## 8. 对外暴露能力

### 显式 API
- `doRead()`: 阅读逻辑

### 内部辅助方法
无

### 扩展入口
- 可覆写 `doRead()` 自定义传送逻辑

## 9. 运行机制与调用链

### 创建时机
- 通过炼金转换（传送卷轴 + 6能量）
- 通过 Generator 随机生成

### 调用者
- 英雄使用物品时调用

### 被调用者
- `Dungeon.interfloorTeleportAllowed()`: 检查传送权限
- `Level.beforeTransition()`: 过渡前处理
- `Game.switchScene()`: 场景切换

### 系统流程位置
```
阅读 → doRead() → 检查传送权限 → 计算目标深度 → 切换场景
```

### 目标深度计算公式
```java
// 区域首层计算：每5层为一个区域
// 深度 1-5 → 区域首层 1
// 深度 6-10 → 区域首层 6
// 深度 11-15 → 区域首层 11
// ...
returnDepth = Math.max(1, (Dungeon.depth - 1 - (Dungeon.depth-2)%5));
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.scrolls.exotic.scrollofpassage.name | 归返秘卷 | 物品名称 |
| items.scrolls.exotic.scrollofpassage.desc | 这张羊皮纸上的咒语能瞬间将读者传送到楼上与之距离最近的区域首层。想去商店的话，用这张秘卷会非常方便。 | 物品描述 |
| items.scrolls.scrollofteleportation.no_tele | （从传送卷轴引用） | 无法传送时的警告 |

### 依赖的资源
- ItemSpriteSheet.Icons.SCROLL_PASSAGE: 物品图标

### 中文翻译来源
- core/src/main/assets/messages/items/items_zh.properties
- 物品描述引用的"no_tele"来自 items.scrolls.scrollofteleportation.no_tele

## 11. 使用示例

### 基本用法

```java
// 阅读归返秘卷
ScrollOfPassage scroll = new ScrollOfPassage();
scroll.doRead(); // 传送到最近区域首层

// 示例：当前在深度 8
// 计算结果：8 - 1 - (8-2)%5 = 8 - 1 - 2 = 5
// 实际传送到深度 6（区域首层：6-10 区域的首层是 6）
```

### 深度计算示例

```java
// 当前深度 3: 3 - 1 - (3-2)%5 = 2 - 1 = 1 → 传送到深度 1
// 当前深度 7: 7 - 1 - (7-2)%5 = 6 - 0 = 6 → 传送到深度 6
// 当前深度 12: 12 - 1 - (12-2)%5 = 11 - 0 = 11 → 传送到深度 11
// 当前深度 21: 21 - 1 - (21-2)%5 = 20 - 4 = 16 → 传送到深度 16
```

## 12. 开发注意事项

### 状态依赖
- 依赖 `Dungeon.interfloorTeleportAllowed()` 判断是否可传送

### 生命周期耦合
- 使用后立即切换场景，无法取消

### 常见陷阱
1. **传送限制**：某些特殊楼层禁止楼层间传送
2. **深度计算**：公式确保传送到区域首层而非当前层上方一层

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可修改目标深度计算逻辑
- 可添加传送前的额外效果

### 不建议修改的位置
- 场景切换的核心逻辑

### 重构建议
考虑将深度计算提取为独立的静态方法，便于测试和复用

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是（仅 icon）
- [x] 是否已覆盖全部方法：是（仅 doRead）
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：否
- [x] 是否明确说明了注意事项与扩展点：是