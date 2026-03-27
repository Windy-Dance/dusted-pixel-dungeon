# StatusPane 类

## 概述
`StatusPane` 是 Shattered Pixel Dungeon 中显示玩家状态信息的核心 UI 组件。它整合了英雄头像、生命值、经验值、等级、Buff 指示器、罗盘和忙碌指示器等多个功能模块。

## 功能特性
- **双尺寸支持**：支持大尺寸（160x39）和小尺寸（可变宽度 x 38）两种布局
- **动态内容**：实时更新生命值、护盾值、经验值和等级信息
- **视觉反馈**：低生命值时头像闪烁红色，可用天赋点时闪烁黄色
- **布局优化**：支持自定义裁剪区域以避免与其他 UI 元素重叠
- **完整集成**：包含所有主要的状态显示功能

## 核心组件

### 基础元素
- **bg**: 背景九宫格（大尺寸使用不同纹理）
- **avatar**: 英雄头像（HeroSprite.avatar）
- **heroInfo**: 英雄信息按钮（点击打开 WndHero 窗口）
- **compass**: 罗盘指示器（指向出口或入口）

### 生命值显示
- **shieldHP**: 护盾条（蓝色背景）
- **hp**: 生命值条（绿色背景）  
- **hpText**: 生命值文本（显示格式：当前/最大 或 当前+护盾/最大）

### 经验值显示
- **exp**: 经验值进度条
- **expText**: 经验值文本（显示格式：当前/最大）

### 等级显示
- **level**: 等级文本（"lv. X" 格式）

### 功能模块
- **buffs**: Buff 指示器（BuffIndicator 实例）
- **busy**: 忙碌指示器（BusyIndicator 实例）
- **counter**: 计数圆弧（CircleArc），显示回合计数

## 静态配置变量

### 外观定制
- **heroPaneExtraWidth**: 额外的英雄头像空间宽度
- **hpBarMaxWidth**: 生命条最大宽度（默认 50）
- **buffBarRowMaxWidths**: Buff 指示器行最大宽度数组
- **buffBarRowAdjusts**: Buff 指示器行位置调整数组

### 视觉效果
- **talentBlink**: 天赋点提示闪烁计时器
- **FLASH_RATE**: 闪烁频率（每秒 1.5 次）

## 核心方法

### 构造函数
- `StatusPane(boolean large)` - 创建指定尺寸的状态面板
  - `large = true`: 大尺寸布局（用于桌面版）
  - `large = false`: 小尺寸布局（用于移动版）

### 更新逻辑
- **update()**: 
  - 更新生命值和护盾显示
  - 处理低生命值警告闪烁
  - 更新经验值和等级显示
  - 刷新英雄头像（当等级变化时）
  - 显示升级星星粒子效果

### 视觉控制
- **alpha(float value)**: 设置整体透明度（0-1 范围）
- **showStarParticles()**: 显示升级时的星星粒子效果
- **updateAvatar()**: 强制刷新英雄头像

## 特殊效果

### 低生命值警告
- 当生命值低于 33.4% 时触发
- 使用三色渐变闪烁：`{0x660000, 0xCC0000, 0x660000}`
- 通过 `ColorMath.interpolate()` 实现平滑颜色过渡

### 天赋点提示
- 当有可用天赋点时触发黄色闪烁
- 使用正弦波控制透明度：`(float)Math.abs(Math.cos(talentBlink*FLASH_RATE))/2f`

### 升级动画
- 显示星星粒子爆发效果
- 播放升级音效 (`Assets.Sounds.LEVELUP`)

## 布局细节

### 大尺寸布局
- 宽度固定为 160 像素
- HP 条宽 128 像素
- EXP 条宽 128 像素  
- Buff 区域宽 142 像素

### 小尺寸布局
- 宽度动态计算：`hpBarMaxWidth + 32`
- 支持头像区域扩展和 HP 条裁剪
- Buff 区域宽 55 像素

## 使用示例
```java
// 创建大尺寸状态面板（桌面版）
StatusPane desktopPane = new StatusPane(true);

// 创建小尺寸状态面板（移动端）
StatusPane mobilePane = new StatusPane(false);

// 调整 HP 条最大宽度（避免与其他 UI 重叠）
StatusPane.hpBarMaxWidth = 40;
StatusPane mobilePane2 = new StatusPane(false);

// 控制透明度
desktopPane.alpha(0.8f);
```

## 注意事项
- 所有文本都使用 `PixelScene.pixelFont` 字体
- 等级显示在英雄死亡时会变为半透明灰色
- 罗盘方向基于 `Statistics.amuletObtained` 状态自动切换
- 忙碌指示器的位置根据尺寸模式自动调整
- 圆弧计数器跟随游戏回合时间自动旋转