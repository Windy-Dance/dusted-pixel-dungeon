# Icons 类

## 概述
`Icons` 是 Shattered Pixel Dungeon 中用于管理和获取各种 UI 图标的枚举类。它提供了统一的图标访问接口，支持不同类别（标题、按钮、游戏内等）的图标资源。

## 图标分类

### 标题界面图标（17x16 像素）
- **ENTER**: 进入游戏
- **GOLD**: 金币  
- **RANKINGS**: 排行榜
- **BADGES**: 徽章
- **NEWS**: 新闻
- **CHANGES**: 更新日志
- **PREFS**: 设置
- **SHPX**: 开发者
- **JOURNAL**: 日志

### 灰色按钮图标（16x16 像素）
- **EXIT**: 退出
- **DISPLAY/DISPLAY_LAND/DISPLAY_PORT**: 显示设置
- **DATA**: 数据管理  
- **AUDIO**: 音频设置
- **LANGS**: 语言选择
- **CONTROLLER**: 控制器设置
- **KEYBOARD**: 键盘设置
- **STATS**: 统计信息
- **CHALLENGE_GREY**: 挑战（灰色）
- **SCROLL_GREY**: 卷轴（灰色）
- **SEED**: 种子
- **LEFTARROW/RIGHTARROW**: 左右箭头
- **CALENDAR**: 日历
- **CHEVRON**: 列表标记
- **SHUFFLE**: 随机分配

### 彩色功能图标（16x16 像素）
- **TARGET**: 目标瞄准
- **INFO**: 信息
- **WARNING**: 警告
- **UNCHECKED/CHECKED**: 复选框
- **CLOSE**: 关闭
- **PLUS**: 加号
- **REPEAT**: 重复
- **ARROW**: 箭头
- **CHALLENGE_COLOR**: 挑战（彩色）
- **SCROLL_COLOR**: 卷轴（彩色）
- **COPY/PASTE**: 复制粘贴

### 大型功能图标（16x16 像似）
- **BACKPACK_LRG**: 大背包
- **TALENT**: 天赋
- **MAGNIFY**: 放大镜
- **SNAKE**: 蛇形
- **BUFFS**: Buff 指示器
- **CATALOG**: 目录
- **ALCHEMY**: 炼金
- **GRASS**: 草地

### 楼层感觉图标（15x16 像素）
- **STAIRS/STAIRS_CHASM/STAIRS_WATER/STAIRS_GRASS/STAIRS_DARK/STAIRS_LARGE/STAIRS_TRAPS/STAIRS_SECRETS**: 各种楼梯图标
- **WELL_HEALTH/WELL_AWARENESS**: 水井类型
- **SACRIFICE_ALTAR**: 祭坛
- **DISTANT_WELL**: 远程水井

### 小型图标（可变尺寸）
- **SKULL**: 头骨
- **BUSY**: 忙碌指示器
- **COMPASS**: 罗盘
- **SLEEP/ALERT/LOST/INVESTIGATE**: 状态指示器
- **DEPTH 系列**: 深度指示器（多种类型）
- **CHAL_COUNT**: 挑战计数
- **COIN_SML/ENERGY_SML**: 小型资源图标
- **BACKPACK/SEED_POUCH/SCROLL_HOLDER/WAND_HOLSTER/POTION_BANDOLIER**: 背包和袋子图标

### 关于页面图标（可变尺寸）
- **LIBGDX/ALEKS/WATA**: 技术和艺术贡献者
- **CELESTI/LUMINE/ARCNOR/PURIGRO/CUBE_CODE**: 社区艺术家

## 核心方法

### 图标获取
- **get()**: 获取当前枚举值对应的图标
- **get(Icons type)**: 静态方法，获取指定类型的图标

### 特殊图标获取
- **get(HeroClass cl)**: 获取英雄职业图标
  - WARRIOR: 印章 (SEAL)
  - MAGE: 法师之杖 (MAGES_STAFF) 
  - ROGUE: 斗篷 (ARTIFACT_CLOAK)
  - HUNTRESS: 灵魂弓 (SPIRIT_BOW)
  - DUELIST: 细剑 (RAPIER)
  - CLERIC: 圣典 (ARTIFACT_TOME)

- **get(Level.Feeling feeling)**: 获取深度小型图标
- **getLarge(Level.Feeling feeling)**: 获取深度大型图标

## 内部机制

### 纹理管理
- 所有图标来自 `Assets.Interfaces.ICONS` 纹理集
- 使用 UV 坐标精确定位每个图标帧
- 自动处理像素对齐 (`PixelScene.align()`)

### 运行类型适配
- **Dungeon.daily**: 日常模式使用偏移 +64
- **Dungeon.customSeedText**: 自定义种子使用 Y 偏移 +8
- **Dungeon.dailyReplay**: 日常回放使用 Y 偏移 +8

### 尺寸处理
- 大型图标（如艺术家头像）会自动缩放 (`scale.set(0.49f)`)
- 不同类别图标保持原始比例
- 所有坐标计算考虑纹理边界

## 使用示例
```java
// 获取基本图标
Image exitIcon = Icons.EXIT.get();

// 获取带有运行类型适配的深度图标  
Image depthIcon = Icons.get(Dungeon.level.feeling);

// 获取英雄职业图标
Image warriorIcon = Icons.get(HeroClass.WARRIOR);

// 在按钮中使用
IconButton exitBtn = new IconButton(Icons.EXIT.get()) {
    @Override
    protected void onClick() {
        // 处理退出逻辑
    }
};
```

## 注意事项
- 所有图标都是独立的 Image 对象，可直接添加到场景中
- 图标尺寸在枚举注释中详细说明
- 运行类型适配仅影响深度相关图标
- 英雄职业图标使用物品精灵而非普通图标
- 大型图标会自动应用缩放以匹配游戏比例
- 纹理坐标使用 uvRectBySize 方法确保精确性