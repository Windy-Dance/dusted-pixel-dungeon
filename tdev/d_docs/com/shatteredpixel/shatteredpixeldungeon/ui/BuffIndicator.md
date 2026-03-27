# BuffIndicator 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ui/BuffIndicator.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.ui |
| **类类型** | class |
| **继承关系** | extends Component |
| **代码行数** | 421 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
BuffIndicator 是状态效果（Buff）指示器组件，负责显示角色当前激活的所有状态效果图标，支持英雄和Boss两种实例，并提供动态的图标管理、布局调整和交互功能。

### 系统定位
位于 ui 包中，作为 Component 的子类，是游戏界面中状态效果显示的核心组件。

### 不负责什么
- 不负责状态效果的逻辑处理
- 不负责状态效果的持续时间计算
- 不负责状态效果的存储和持久化

## 3. 结构总览

### 主要成员概览
- 85个图标类型常量
- 2个尺寸常量
- 2个静态实例字段
- 6个实例字段
- 内部类 BuffButton

### 主要逻辑块概览
1. 常量定义：状态效果图标索引
2. 实例管理：heroInstance 和 bossInstance
3. 布局逻辑：layout() 方法
4. 内部类：BuffButton 按钮组件

### 生命周期/调用时机
- 在 GameScene 创建时初始化 heroInstance
- 在 BossHealthBar 创建时初始化 bossInstance

## 4. 继承与协作关系

### 父类提供的能力
Component 提供：
- UI 组件基础功能
- 布局机制
- 更新循环

### 覆写的方法
| 方法 | 来源 |
|------|------|
| destroy() | Component |
| update() | Component |
| layout() | Component |

### 实现的接口契约
无直接实现的接口。

### 依赖的关键类
- `Char`：角色对象
- `Buff`：状态效果基类
- `BuffButton`：内部按钮类
- `WndInfoBuff`：状态详情窗口
- `Badges`：成就系统

### 使用者
- `GameScene`：游戏场景
- `BossHealthBar`：Boss血条
- `WndInfoMob`：怪物信息窗口

## 5. 字段/常量详解

### 图标类型常量（部分重要）

| 常量名 | 值 | 说明 |
|--------|-----|------|
| NONE | 127 | 透明图标（不显示） |
| MIND_VISION | 0 | 心灵视觉 |
| LEVITATION | 1 | 漂浮 |
| FIRE | 2 | 燃烧 |
| POISON | 3 | 中毒 |
| PARALYSIS | 4 | 麻痹 |
| HUNGER | 5 | 饥饿 |
| FROST | 15 | 冻伤 |
| BLINDNESS | 16 | 失明 |
| COMBO | 17 | 连击 |
| BERSERK | 40 | 狂暴 |
| HASTE | 41 | 急速 |
| UPGRADE | 50 | 升级 |
| MONK_ENERGY | 68 | 武僧能量 |
| TRINITY_FORM | 82 | 三位一体形态 |

### 尺寸常量

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| SIZE_SMALL | int | 7 | 小尺寸图标 |
| SIZE_LARGE | int | 16 | 大尺寸图标 |

### 静态字段

| 字段名 | 类型 | 说明 |
|--------|------|------|
| heroInstance | BuffIndicator | 英雄状态指示器实例 |
| bossInstance | BuffIndicator | Boss状态指示器实例 |

### 实例字段

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| buffButtons | LinkedHashMap<Buff, BuffButton> | new LinkedHashMap<>() | 状态效果按钮映射 |
| needsRefresh | boolean | false | 是否需要刷新 |
| ch | Char | - | 关联的角色 |
| large | boolean | false | 是否大尺寸 |
| rowWidthLimits | float[9] | - | 每行宽度限制 |
| rowHeightAdjusts | float[9] | - | 每行高度调整 |
| maxBuffs | int | 14 | 最大显示数量 |

## 6. 构造与初始化机制

### 构造器
```java
public BuffIndicator(Char ch, boolean large)
```
创建状态指示器实例：
- `ch`：关联的角色对象
- `large`：是否使用大尺寸图标
- 如果角色是英雄，则设置为 heroInstance

### 初始化注意事项
构造时会自动将英雄实例注册到静态字段。

## 7. 方法详解

### destroy()

**可见性**：public

**是否覆写**：是，覆写自 Component

**方法职责**：销毁组件时清理静态引用。

**核心实现逻辑**：
```java
if (this == heroInstance) {
    heroInstance = null;
}
```

### update()

**可见性**：public synchronized

**是否覆写**：是，覆写自 Component

**方法职责**：每帧更新，检查是否需要刷新布局。

**核心实现逻辑**：
```java
if (needsRefresh){
    needsRefresh = false;
    layout();
}
```

### layout()

**可见性**：protected

**是否覆写**：是，覆写自 Component

**方法职责**：核心布局方法，同步状态效果并排列图标。

**核心实现逻辑**：
1. 收集当前角色的所有状态效果
2. 移除不再存在的图标（带淡出动画）
3. 添加新的图标
4. 计算多行布局
5. 处理空间不足时的压缩逻辑
6. 验证"多种状态效果"成就

### refreshHero() / refreshBoss()

**可见性**：public static

**是否覆写**：否

**方法职责**：标记对应实例需要刷新。

**核心实现逻辑**：
```java
if (heroInstance != null) {
    heroInstance.needsRefresh = true;
}
```

### allBuffsVisible()

**可见性**：public

**是否覆写**：否

**方法职责**：返回是否所有状态效果都可见。

**返回值**：boolean

## 8. 对外暴露能力

### 显式 API
- `refreshHero()`：刷新英雄状态指示器
- `refreshBoss()`：刷新Boss状态指示器
- `setBossInstance()`：设置Boss实例
- `allBuffsVisible()`：检查可见性

### 内部辅助方法
- `layout()`：布局计算

### 扩展入口
可覆写 layout() 实现自定义布局。

## 9. 运行机制与调用链

### 创建时机
- GameScene 初始化时创建 heroInstance
- BossHealthBar 创建时设置 bossInstance

### 调用者
- `GameScene`：创建和管理英雄状态栏
- `BossHealthBar`：显示Boss状态
- `WndInfoMob`：显示敌人状态

### 被调用者
- `Buff.icon()`：获取图标索引
- `BuffButton`：显示单个状态图标
- `WndInfoBuff`：显示状态详情

### 系统流程位置
游戏界面渲染层。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
通过 Buff.desc() 获取状态效果的本地化描述。

### 依赖的资源
- 纹理：Assets.Sprites.BUFFS_SMALL / BUFFS_LARGE

### 中文翻译来源
ui_zh.properties 和 actors_zh.properties 文件。

## 11. 使用示例

### 基本用法
```java
// 创建英雄状态指示器
BuffIndicator indicator = new BuffIndicator(Dungeon.hero, false);

// 刷新状态显示
BuffIndicator.refreshHero();

// 检查可见性
if (indicator.allBuffsVisible()) {
    // 所有状态都可见
}
```

### Boss 状态显示
```java
// 设置 Boss 实例
BuffIndicator.setBossInstance(bossIndicator);
BuffIndicator.refreshBoss();
```

## 12. 开发注意事项

### 状态依赖
- 依赖 Char.buffs() 获取状态列表
- 依赖 Buff.icon() 获取图标索引

### 生命周期耦合
与游戏场景生命周期绑定。

### 常见陷阱
- 修改图标常量值会影响存档兼容性
- 新增状态效果需添加对应的图标常量

## 13. 修改建议与扩展点

### 适合扩展的位置
- 新增图标常量
- 自定义布局算法

### 不建议修改的位置
- 现有图标常量的值
- maxBuffs 默认值

### 重构建议
源码注释提到图标常量需要整理，建议分类管理。

## 14. 事实核查清单

- [x] 已覆盖主要字段和常量
- [x] 已覆盖主要方法
- [x] 已检查继承链与覆写关系
- [x] 已核对官方中文翻译
- [x] 无推测性表述
- [x] 示例代码真实可用
- [x] 已标注资源关联
- [x] 已明确说明注意事项与扩展点

## 附录：内部类 BuffButton

BuffButton 是继承自 IconButton 的内部类，用于显示单个状态效果图标并提供交互功能。

### 核心功能
- 显示状态效果图标
- 显示数值文本（如持续时间）
- 显示渐变效果（表示剩余时间）
- 点击显示详细信息窗口

### 主要字段
- `buff`：关联的状态效果
- `large`：是否大尺寸
- `topOffset`：顶部偏移