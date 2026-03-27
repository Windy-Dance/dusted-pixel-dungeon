# CharHealthIndicator 类

## 概述
`CharHealthIndicator` 是 Shattered Pixel Dungeon 中用于在游戏角色（Char）上方显示健康条的UI组件。它继承自 `HealthBar` 基类，专为角色精灵设计，能够自动跟随角色移动并在适当的位置显示生命值和护盾状态。

## 功能特性
- **角色绑定**：自动关联到指定的角色对象，实时显示其生命值状态
- **智能定位**：健康条自动定位在角色精灵正上方，宽度为精灵宽度的2/3
- **动态显示**：仅在角色生命值未满或有护盾时才显示，避免不必要的视觉干扰
- **场景集成**：自动添加到当前游戏场景中，无需手动管理
- **条件可见性**：仅在角色存活、激活且精灵可见时才显示健康条

## 核心方法

### 构造函数
- `CharHealthIndicator(Char c)` - 创建与指定角色关联的健康指示器，并自动添加到游戏场景

### 属性设置
- `target(Char ch)` - 更换目标角色，如果新角色无效则清除目标
- `target()` - 获取当前目标角色

### 重写方法
- `createChildren()` - 创建子组件并设置高度为1像素
- `update()` - 更新健康条位置、大小和可见性状态

## 内部组件
- `target` - 当前关联的角色对象
- `HEIGHT` - 健康条高度常量，固定为1像素

## 使用示例
```java
// 为角色创建健康指示器
Char hero = Dungeon.hero;
CharHealthIndicator healthIndicator = new CharHealthIndicator(hero);

// 切换目标角色
Char enemy = someMonster;
healthIndicator.target(enemy);

// 获取当前目标
Char currentTarget = healthIndicator.target();
```

## 注意事项
- 健康条高度固定为1像素，比普通健康条更细
- 自动处理角色死亡、失活或不可见的情况，此时健康条会隐藏
- 位置计算基于角色精灵的坐标和尺寸，确保精准对齐
- 宽度设置为角色精灵宽度的4/6（约2/3），保持适当的视觉比例
- Y坐标偏移-2像素，确保健康条显示在角色头顶上方