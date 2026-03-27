# TalentButton 类

## 概述
`TalentButton` 是 Shattered Pixel Dungeon 中用于显示和交互天赋的专用按钮组件。它继承自基础 `Button` 类，添加了天赋特定的视觉效果、信息显示和升级功能。

## 功能特性
- **多种操作模式**：支持信息查看、升级、变形选择和替换等不同模式
- **等级系统**：与天赋等级系统集成，显示可用点数和已分配点数
- **视觉反馈**：提供点击、悬停和升级时的视觉和音效反馈
- **填充指示器**：底部显示彩色条形指示器，反映天赋点数分配状态
- **弹窗集成**：点击时自动打开对应的天赋信息窗口

## 核心枚举

### Mode 枚举
定义按钮的操作模式：
- **INFO**: 仅显示天赋信息，不可升级
- **UPGRADE**: 支持天赋升级操作  
- **METAMORPH_CHOOSE**: 变形法术选择模式
- **METAMORPH_REPLACE**: 变形法术替换模式

## 构造函数
```java
public TalentButton(int tier, Talent talent, int points, Mode mode)
```
- **tier**: 天赋所属等级（1-4）
- **talent**: 具体的天赋对象
- **points**: 当前已分配的点数
- **mode**: 操作模式

## 核心组件

### 视觉元素
- **icon**: `TalentIcon` 实例，显示天赋图标
- **bg**: 背景图像，使用 `Assets.Interfaces.TALENT_BUTTON`
- **fill**: 底部彩色填充条，显示点数分配进度

### 尺寸常量
- **WIDTH = 20**: 按钮宽度
- **HEIGHT = 26**: 按钮高度

## 交互方法

### 点击处理
- **onClick()**: 处理主要点击事件
  - 根据模式创建不同的 `WndInfoTalent` 窗口
  - 升级模式下显示升级确认按钮
  - 变形模式下处理天赋替换逻辑

### 升级功能
- **upgradeTalent()**: 执行天赋升级操作
  - 验证是否有可用点数
  - 调用 `Dungeon.hero.upgradeTalent(talent)`
  - 显示星星粒子动画效果
  - 播放升级音效

### 状态控制
- **enable(boolean value)**: 启用/禁用按钮
  - 调整图标和背景的透明度

### 事件处理
- **onPointerDown()**: 点击按下时增加亮度并播放音效
- **onPointerUp()**: 点击释放时重置颜色
- **hoverText()**: 返回悬停提示文本（天赋标题）

## 特殊功能

### 变形支持
在 `METAMORPH_CHOOSE` 和 `METAMORPH_REPLACE` 模式下：
- 与 `ScrollOfMetamorphosis` 类集成
- 处理天赋替换的数据结构更新
- 更新英雄的 `metamorphedTalents` 映射

### 粒子效果
升级时创建 `Emitter` 并发射 `Speck.STAR` 粒子，增强视觉反馈。

## 使用示例
```java
// 创建可升级的天赋按钮
TalentButton upgradeBtn = new TalentButton(
    1,                    // 等级 1
    someTalent,          // 天赋对象  
    2,                   // 已分配 2 点
    TalentButton.Mode.UPGRADE  // 升级模式
);

// 创建信息查看按钮
TalentButton infoBtn = new TalentButton(
    2, 
    anotherTalent, 
    3, 
    TalentButton.Mode.INFO
);
```

## 注意事项
- 按钮尺寸固定为 20x26 像素
- 填充条宽度根据 `pointsInTalent/talent.maxPoints()` 计算
- 升级操作需要验证 `Dungeon.hero.talentPointsAvailable(tier) > 0`
- 在非游戏场景中也能正常显示信息窗口
- 随机胜利徽章相关逻辑会跟踪天赋升级操作