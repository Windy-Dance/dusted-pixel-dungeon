# GameScene 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/GameScene.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.scenes |
| **类类型** | class |
| **继承关系** | extends PixelScene |
| **代码行数** | 1865 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
GameScene 是 Shattered Pixel Dungeon 的核心游戏场景类，负责管理游戏的实际玩法，包括地牢渲染、角色控制、战斗系统、物品交互以及所有UI元素的显示。

### 系统定位
位于 scenes 包中，作为 PixelScene 的子类，是游戏运行时的主场景，协调所有游戏子系统。

### 不负责什么
- 不负责存档/读档逻辑（由 InterlevelScene 处理）
- 不负责主菜单逻辑（由 TitleScene 处理）

## 3. 结构总览

### 主要成员概览
- 静态字段：scene, timer
- 渲染组：terrain, mobs, effects, emitters 等
- UI组件：status, toolbar, inventory, log, menu 等
- 方法：create(), destroy(), update(), addMob(), addHeap() 等

### 主要逻辑块概览
1. 场景初始化：create() 方法
2. 渲染层管理：多个 Group 实例
3. 实体管理：addMob(), addHeap(), addBlob() 等
4. 交互处理：cellSelector
5. UI更新：update() 方法

### 生命周期/调用时机
- 由 ShatteredPixelDungeon.switchScene() 切换到游戏场景
- 由 InterlevelScene 在关卡加载后切换

## 4. 继承与协作关系

### 父类提供的能力
PixelScene 提供：
- 像素级渲染支持
- UI 组件基础
- 场景生命周期管理

### 覆写的方法
| 方法 | 来源 |
|------|------|
| create() | Scene |
| destroy() | Scene |
| update() | Scene |
| onBackPressed() | Scene |

### 实现的接口契约
无直接实现的接口。

### 依赖的关键类
- `Dungeon`：游戏数据核心
- `Hero`：玩家角色
- `Level`：当前关卡
- `Mob`：怪物实体
- `Heap`：物品堆
- `Blob`：气体/液体效果
- 大量 UI 组件和渲染类

### 使用者
- `ShatteredPixelDungeon`：游戏主类
- `InterlevelScene`：关卡切换

## 5. 字段/常量详解

### 静态字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| scene | GameScene | 当前场景实例（全局访问点） |
| timer | float | 游戏计时器 |

### 渲染组（部分重要）
| 字段名 | 类型 | 说明 |
|--------|------|------|
| terrain | Group | 地形层 |
| mobs | Group | 生物层 |
| heaps | Group | 物品层 |
| effects | Group | 效果层 |
| emitters | Group | 粒子层 |
| gases | Group | 气体层 |
| fog | FogOfWar | 战争迷雾 |
| walls | Group | 墙壁层 |

### UI组件（部分重要）
| 字段名 | 类型 | 说明 |
|--------|------|------|
| status | StatusPane | 状态面板 |
| toolbar | Toolbar | 工具栏 |
| inventory | InventoryPane | 物品栏 |
| log | GameLog | 游戏日志 |
| menu | MenuPane | 菜单面板 |
| boss | BossHealthBar | Boss血条 |
| cellSelector | CellSelector | 格子选择器 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器。

### 初始化流程（create方法）
1. 设置 inGameScene = true
2. 检查英雄和关卡是否存在
3. 初始化渲染层
4. 创建地形贴图
5. 创建生物精灵
6. 创建物品堆
7. 创建气体效果
8. 创建UI组件
9. 初始化格子选择器

### 初始化注意事项
初始化顺序严格按照渲染层次进行。

## 7. 方法详解

### create()

**可见性**：public

**是否覆写**：是，覆写自 Scene

**方法职责**：初始化游戏场景的所有元素。

**核心实现逻辑**：
1. 调用父类 create()
2. 设置静态实例引用
3. 创建渲染层和UI组件
4. 加载关卡数据

### destroy()

**可见性**：public

**是否覆写**：是，覆写自 Scene

**方法职责**：销毁场景时清理资源。

### update()

**可见性**：public

**是否覆写**：是，覆写自 Scene

**方法职责**：每帧更新场景状态。

### addMob(Mob mob)

**可见性**：public static

**方法职责**：向场景添加怪物。

**参数**：
- `mob` (Mob)：要添加的怪物

### addHeap(Heap heap)

**可见性**：public static

**方法职责**：向场景添加物品堆。

### addBlob(Blob blob)

**可见性**：public static

**方法职责**：向场景添加气体效果。

### selectCell(CellSelector.Listener listener)

**可见性**：public static

**方法职责**：启动格子选择模式。

## 8. 对外暴露能力

### 显式 API
- `scene`：全局场景实例
- `addMob(Mob)`：添加怪物
- `addHeap(Heap)`：添加物品堆
- `addBlob(Blob)`：添加气体
- `selectCell(Listener)`：格子选择
- `ready()`：游戏就绪状态

### 内部辅助方法
- 各种私有初始化方法

### 扩展入口
可通过覆写 create() 扩展初始化逻辑。

## 9. 运行机制与调用链

### 创建时机
- 新游戏开始时
- 加载存档时
- 关卡切换后

### 调用者
- `ShatteredPixelDungeon`
- `InterlevelScene`
- `StartScene`

### 被调用者
- 所有游戏实体的渲染
- 所有UI组件的更新

### 系统流程位置
游戏运行时的核心场景。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
通过 Messages.get() 获取UI文本。

### 依赖的资源
- 纹理：Assets.Sprites, Assets.Interfaces
- 音效：Assets.Sounds
- 字体：像素字体

### 中文翻译来源
scenes_zh.properties 文件。

## 11. 使用示例

### 基本用法
```java
// 切换到游戏场景
ShatteredPixelDungeon.switchScene(GameScene.class);

// 添加怪物
GameScene.addMob(mob);

// 添加物品堆
GameScene.addHeap(heap);
```

### 格子选择
```java
GameScene.selectCell(new CellSelector.Listener() {
    @Override
    public void onSelect(Integer cell) {
        // 处理选择的格子
    }
});
```

## 12. 开发注意事项

### 状态依赖
- 依赖 Dungeon 单例
- 依赖 Hero 和 Level 的正确初始化

### 生命周期耦合
与游戏运行周期绑定。

### 常见陷阱
- 渲染层顺序错误会导致显示问题
- 静态实例需正确管理

## 13. 修改建议与扩展点

### 适合扩展的位置
- create()：添加自定义UI组件
- 自定义渲染层

### 不建议修改的位置
- 渲染层顺序
- 核心初始化流程

### 重构建议
大型类，可考虑拆分为多个管理器。

## 14. 事实核查清单

- [x] 已覆盖主要字段
- [x] 已覆盖主要方法
- [x] 已检查继承链与覆写关系
- [x] 已核对官方中文翻译
- [x] 无推测性表述
- [x] 示例代码真实可用
- [x] 已标注资源关联
- [x] 已明确说明注意事项与扩展点