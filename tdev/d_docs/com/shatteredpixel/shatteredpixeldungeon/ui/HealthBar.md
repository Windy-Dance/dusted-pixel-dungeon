# HealthBar 类

## 概述
`HealthBar` 是 Shattered Pixel Dungeon 中的健康条UI组件基类，继承自 `com.watabou.noosa.ui.Component`。它提供了一个三层结构的健康显示：红色背景、白色护盾层和绿色生命层，能够同时显示角色的生命值和护盾值。

## 功能特性
- **三层显示**：背景（红色）、护盾（白色）、生命值（绿色）三重叠显示
- **像素级精度**：使用像素对齐算法确保在任何缩放级别下都显示正确的像素宽度
- **自动比例计算**：根据角色的最大生命值和当前护盾值自动计算显示比例
- **灵活接口**：支持直接设置数值比例或传入角色对象自动计算
- **高效布局**：仅在数值变化时重新计算布局，避免不必要的性能开销

## 核心方法

### 构造函数
- 无显式构造函数，使用父类默认构造函数

### 公共方法
- `level(float value)` - 设置生命值比例（0.0-1.0），护盾为0
- `level(float health, float shield)` - 分别设置生命值和护盾的比例
- `level(Char c)` - 根据角色对象自动计算并设置生命值和护盾比例

### 重写方法
- `createChildren()` - 创建三层 ColorBlock 组件并设置初始高度
- `layout()` - 布局各层组件的位置和大小

## 内部组件
- `Bg` - 背景 ColorBlock，颜色为 0xFFCC0000（半透明红色）
- `Shld` - 护盾 ColorBlock，颜色为 0xFFFFFFFF（白色）
- `Hp` - 生命值 ColorBlock，颜色为 0xFF00EE00（亮绿色）
- `health` - 当前生命值比例（0.0-1.0）
- `shield` - 当前护盾值比例（0.0-1.0）
- `COLOR_BG` - 背景颜色常量
- `COLOR_HP` - 生命值颜色常量  
- `COLOR_SHLD` - 护盾颜色常量
- `HEIGHT` - 健康条高度常量，固定为2像素

## 使用示例
```java
// 创建健康条
HealthBar healthBar = new HealthBar();

// 直接设置比例值
healthBar.level(0.75f, 0.25f); // 75%生命，25%护盾

// 或者使用角色对象
Hero hero = Dungeon.hero;
healthBar.level(hero);

// 添加到界面
add(healthBar);
```

## 注意事项
- 护盾层会覆盖在生命层之上，所以实际显示效果是：背景→生命→护盾
- 像素对齐算法确保即使在非整数缩放倍数下也能正确显示完整像素
- 最大值计算使用 Math.max(health+shield, c.HT)，确保护盾不会被截断
- 高度固定为2像素，适合用作小型指标显示
- 所有颜色都包含完整的Alpha通道（0xFF开头）