# TitleBackground 类

## 概述
`TitleBackground` 是 Shattered Pixel Dungeon 标题界面的动态背景组件。它创建了一个多层次的滚动背景，包含拱门、物品簇和各种装饰元素，营造出丰富的视觉效果。

## 功能特性
- **多层视差滚动**：不同层级以不同速度滚动，创造深度感
- **自适应布局**：根据屏幕尺寸和方向自动调整
- **随机生成**：使用随机算法生成多样化的背景元素
- **状态保持**：在场景切换时保持背景状态
- **密度控制**：根据屏幕宽度自动调整元素密度

## 核心机制

### 多层结构
背景由 7 个独立的图层组成，按滚动速度排序：

1. **ArchLayer（拱门层）** - 最慢速滚动
   - 使用 `Assets.Splashes.Title.ARCHS` 纹理
   - 自动拼接形成连续的拱门背景
   
2. **ClustersFarLayer（远景簇群层）** - 远景物品簇
   - 使用 `Assets.Splashes.Title.BACK_CLUSTERS` 纹理
   - 半透明效果 (brightness 0.5f)
   
3. **ClusterLayer（中景簇群层）** - 中景物品簇  
4. **SmallFarLayer（远景小物层）** - 远景小型物品
5. **Mids1Layer（中景混合层1）** - 中景混合物品
6. **Mids2Layer（中景混合层2）** - 中景混合物品（更大）
7. **SmallCloseLayer（近景小物层）** - 近景小型物品

### 滚动速度控制
基础滚动速度由 `SCROLL_SPEED = 15f` 定义，各层按以下比例加速：
- ArchLayer: 1x (基础速度)
- ClustersFarLayer: 1.33x  
- ClusterLayer: 2x
- SmallFarLayer: 2.66x
- Mids1Layer: 3.55x
- Mids2Layer: 4.73x  
- SmallCloseLayer: 6.3x

### 密度算法
```java
density = width / (800f * scale);
density = (density+0.5f)/1.5f; // 向 1 拉近 33%
```

## 静态方法

### 状态管理
- `reset()` - 重置所有静态状态，强制重新生成背景
- 被多个静态列表引用跟踪各层对象状态

### 图像转换
- `convertImage(Image oldImg, float newBaseScale)` - 在场景重置后转换图像
- `convertArchLayer()` - 专门处理拱门层的转换
- `convertFloatingLayer()` - 处理浮动层的转换

## 内部类

### 随机选择器
- `getArchFrame()` - 基于概率选择拱门帧
- `getClusterFrame()` - 基于概率选择簇群帧  
- `getMidFrame()` - 基于概率选择中景帧（避免重复）
- `getSmallFrame()` - 基于概率选择小物帧（避免重复）

## 使用示例
```java
// 创建标题背景
int width = Game.width;
int height = Game.height;
TitleBackground background = new TitleBackground(width, height);

// 重置背景（例如新游戏开始时）
TitleBackground.reset();
```

## 注意事项
- 背景元素使用不同的亮度值来区分远近层次
- 拱门层会在滚动到底部时自动重新生成
- 其他层使用随机位置和角度增加多样性
- 支持横屏和竖屏模式的自动适配
- 在场景切换时会尝试保持现有元素状态
- 性能优化：只更新可视区域内的元素