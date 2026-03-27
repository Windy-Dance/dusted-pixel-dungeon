# TargetHealthIndicator 类

## 概述
`TargetHealthIndicator` 是 Shattered Pixel Dungeon 中用于显示目标敌人生命值的健康条组件。它继承自 `HealthBar`，专门用于在战斗中跟踪和显示被锁定敌人的生命状态。

## 功能特性
- **自动目标跟踪**：实时跟踪指定目标的位置和状态
- **智能显示**：仅在目标可见且存活时显示
- **位置同步**：自动与目标精灵保持同步位置
- **全局实例**：通过静态 `instance` 变量提供全局访问

## 核心方法

### 构造函数
- `TargetHealthIndicator()` - 创建目标健康指示器并设置为全局实例

### 状态管理  
- `target(Char ch)` - 设置或清除当前跟踪的目标
  - 如果目标无效（死亡、不活跃）则清除目标引用
- `target()` - 获取当前跟踪的目标

### 更新逻辑
- `update()` - 重写父类更新方法，添加目标跟踪逻辑：
  - 检查目标是否有效（存活、活跃、可见）
  - 同步位置到目标精灵上方
  - 设置健康条数值
  - 控制可见性

### 继承功能
- 继承 `HealthBar` 的所有生命值显示功能
- 自动处理生命值百分比计算和视觉更新

## 静态变量
- `instance`: 全局唯一的 `TargetHealthIndicator` 实例，便于其他组件访问

## 使用示例
```java
// 设置目标（通常在攻击或选择敌人时）
TargetHealthIndicator.instance.target(enemyChar);

// 清除目标
TargetHealthIndicator.instance.target(null);

// 检查当前目标
Char currentTarget = TargetHealthIndicator.instance.target();
```

## 注意事项
- 健康条显示在目标精灵上方 3 像素处 (`y = sprite.y - 3`)
- 宽度与目标精灵宽度相同 (`width = sprite.width()`)
- 仅当目标满足以下条件时才显示：
  - 目标存活 (`target.isAlive()`)
  - 目标活跃 (`target.isActive()`)  
  - 目标精灵存在且可见 (`target.sprite != null && target.sprite.visible`)
- 在场景切换或销毁时会自动清理实例引用
- 与其他 UI 组件（如 `QuickSlotButton`）集成，支持自动瞄准功能