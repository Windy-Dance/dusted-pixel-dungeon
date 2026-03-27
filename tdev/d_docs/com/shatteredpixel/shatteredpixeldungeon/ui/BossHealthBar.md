# BossHealthBar 类

## 概述

`BossHealthBar` 类是 Shattered Pixel Dungeon 游戏中的Boss血条显示组件。它专门用于在Boss战期间显示Boss的生命值、护盾值、状态效果和基本信息，并提供交互功能。

该组件具有以下核心特性：
- **动态血条显示**：实时更新Boss的当前生命值和护盾值
- **双尺寸支持**：根据界面设置自动切换大/小尺寸显示模式
- **状态效果集成**：内嵌 `BuffIndicator` 显示Boss的当前状态效果
- **视觉反馈**：支持出血特效和骷髅图标变色
- **信息交互**：点击可查看Boss详细信息窗口
- **自动管理**：根据Boss状态自动显示/隐藏

## 继承关系

- `com.watabou.noosa.ui.Component`
  - `com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar`

## 静态字段

- **`boss: Mob`**：当前关联的Boss怪物实例
- **`instance: BossHealthBar`**：单例实例引用
- **`bleeding: boolean`**：标记Boss是否处于出血状态
- **`asset: String`**：使用的资源文件路径（`Assets.Interfaces.BOSSHP`）

## 实例字段

### UI元素

- **`bar: Image`**：血条背景图像
- **`shieldHP: Image`**：护盾值进度条
- **`hp: Image`**：生命值进度条  
- **`hpText: BitmapText`**：生命值/护盾值文本显示
- **`bossInfo: Button`**：Boss信息按钮
- **`buffs: BuffIndicator`**：Boss状态效果指示器
- **`skull: Image`**：骷髅图标（小尺寸时使用静态图标，大尺寸时使用Boss精灵）
- **`blood: Emitter`**：血液粒子发射器（用于出血特效）

### 配置

- **`large: boolean`**：是否使用大尺寸界面（基于SPDSettings.interfaceSize()）

## 构造函数

- **`BossHealthBar()`**  
  创建Boss血条组件：
  - 根据是否存在Boss设置初始可见性
  - 设置单例引用

## 方法

### 生命周期方法

- **`destroy(): void`**  
  销毁组件时清理单例引用和状态指示器。

- **`createChildren(): void`**  
  创建所有子UI元素，包括：
  - 背景、血条、护盾条
  - 文本显示、信息按钮
  - 状态指示器（如果存在Boss）
  - 骷髅图标和血液粒子系统

- **`layout(): void`**  
  布局计算，根据不同尺寸模式调整各元素位置和大小。

- **`update(): void`**  
  每帧更新逻辑：
  - 检查Boss存活状态，必要时隐藏组件
  - 更新血条和护盾条的缩放比例
  - 处理出血状态的视觉效果
  - 更新生命值/护盾值文本显示

### 静态工具方法

- **`assignBoss(Mob boss): void`**  
  分配新的Boss到血条组件：
  - 更新Boss引用
  - 重置出血状态
  - 在渲染线程中更新UI（包括更换骷髅图标、重新创建状态指示器）

- **`isAssigned(): boolean`**  
  检查是否有有效的Boss被分配且存活。

- **`bleed(boolean value): void`**  
  设置Boss的出血状态。

- **`isBleeding(): boolean`**  
  检查Boss是否处于出血状态。

## 技术特点

1. **线程安全**：通过 `ShatteredPixelDungeon.runOnRenderThread()` 确保UI更新在渲染线程执行
2. **资源优化**：大尺寸模式下直接使用Boss精灵作为骷髅图标，减少资源占用
3. **智能布局**：根据界面尺寸自动调整血条、文本和状态效果的布局
4. **视觉层次**：出血时骷髅图标变红并显示血液粒子效果
5. **数值处理**：智能处理生命值+护盾值超过最大值的情况，进行比例缩放

## 使用场景

1. **Boss战开始**：当玩家进入Boss房间时，系统调用 `assignBoss()` 显示血条
2. **战斗过程**：实时显示Boss的生命状态变化
3. **特殊效果**：当Boss受到出血伤害时，显示相应的视觉反馈
4. **信息查询**：玩家点击血条可查看Boss的详细属性和能力
5. **Boss死亡**：自动隐藏血条组件

## 相关类

- **`Mob`**：怪物基类，提供生命值、护盾值和状态信息
- **`BuffIndicator`**：状态效果指示器，显示Boss的当前Buff
- **`WndInfoMob`**：怪物信息窗口，显示Boss详细信息
- **`BloodParticle`**：血液粒子效果类
- **`SPDSettings`**：游戏设置类，提供界面尺寸配置
- **`Dungeon`**：游戏核心类，提供当前关卡和怪物列表

## 注意事项

- 血条组件是单例模式，同一时间只能显示一个Boss的信息
- 所有UI更新必须在渲染线程中执行，避免多线程问题
- 出血状态需要手动通过 `bleed()` 方法设置，不会自动检测
- 当Boss死亡或离开当前关卡时，组件会自动隐藏并清理资源