# TalentIcon 类

## 概述
`TalentIcon` 是 Shattered Pixel Dungeon 中用于显示天赋图标的简单图像组件。它封装了天赋图标纹理的加载和帧选择逻辑，提供统一的图标显示接口。

## 功能特性
- **纹理管理**：自动管理天赋图标纹理集（TextureFilm）
- **帧选择**：根据天赋类型或图标 ID 选择正确的帧
- **尺寸标准化**：所有天赋图标统一为 16x16 像素
- **轻量级设计**：继承自 `com.watabou.noosa.Image`，仅包含必要功能

## 核心方法

### 构造函数
- `TalentIcon(Talent talent)` - 通过天赋对象创建图标
  - 自动调用 `talent.icon()` 获取图标 ID
  
- `TalentIcon(int icon)` - 通过图标 ID 直接创建
  - 直接使用指定的图标 ID 选择帧

### 内部机制
- **静态纹理集**：`film` 变量在首次使用时初始化
- **统一尺寸**：`SIZE = 16` 定义标准图标尺寸
- **纹理源**：使用 `Assets.Interfaces.TALENT_ICONS` 作为纹理资源

## 使用示例
```java
// 通过天赋对象创建图标
Talent myTalent = SomeTalentClass;
TalentIcon icon1 = new TalentIcon(myTalent);

// 通过图标 ID 创建图标  
int iconId = 5;
TalentIcon icon2 = new TalentIcon(iconId);

// 在 UI 组件中使用
addComponent(icon1);
```

## 集成说明
- 主要与 `TalentButton` 配合使用，在天赋按钮中显示对应图标
- 在 `TalentsPane` 中用于展示各个天赋的视觉标识
- 图标 ID 通常在天赋类的 `icon()` 方法中定义
- 纹理集预定义了所有可能的天赋图标帧

## 注意事项
- 纹理集（TextureFilm）是静态的，只会在首次创建图标时初始化
- 所有图标必须符合 16x16 像素的标准尺寸
- 图标资源文件必须位于 `Assets.Interfaces.TALENT_ICONS` 路径
- 不包含交互逻辑，纯粹用于视觉显示