# QuickSlotButton 类

## 概述
`QuickSlotButton` 是 Shattered Pixel Dungeon 中用于管理快捷物品槽的专用按钮组件。它集成了物品显示、交互操作、目标瞄准和键盘绑定等功能。

## 功能特性
- **物品槽集成**：包装标准 `ItemSlot` 组件显示物品
- **多键支持**：支持左键使用、右键分配、中键使用的完整交互
- **目标瞄准**：支持自动瞄准功能，显示十字准星
- **键盘绑定**：每个槽位绑定独立的快捷键（QUICKSLOT_1 到 QUICKSLOT_6）
- **状态同步**：自动与全局快捷槽系统同步

## 静态变量

### 全局状态
- **instance[]**: 所有快捷槽按钮实例数组（大小为 QuickSlot.SIZE）
- **targetingSlot**: 当前处于目标瞄准模式的槽位索引
- **lastTarget**: 上次瞄准的目标角色
- **lastVisible**: 最后可见的槽位数量（基于屏幕宽度）

### 重置方法
- **reset()**: 重置所有静态状态和引用

## 核心方法

### 构造函数
- `QuickSlotButton(int slotNum)` - 创建指定槽位的按钮
  - 自动从 `Dungeon.quickslot` 获取物品
  - 设置键盘绑定

### 物品管理
- **item(Item item)**: 设置当前槽位的物品
- **enable(boolean value)**: 启用/禁用按钮
- **refresh()**: 静态方法，刷新所有槽位的物品显示

### 目标瞄准
- **useTargeting()**: 进入目标瞄准模式
  - 显示十字准星连接英雄和目标
  - 设置 `targetingSlot` 状态
  
- **autoAim(Char target, Item item)**: 计算自动瞄准位置
  - 尝试直接瞄准
  - 失败时在目标周围2格范围内寻找最佳射击角度
  - 返回瞄准的地面位置

### 用户交互
- **onClick()**: 处理主要点击逻辑
  - 目标瞄准模式下执行瞄准操作
  - 正常模式下打开物品选择窗口
  
- **onLongClick()**: 长按处理（等同于普通点击）

### 快捷操作
- **set(Item item)**: 静态方法，在第一个空槽位设置物品
- **set(int slotNum, 0):  item)**: 静态方法，在指定槽位设置物品

### 状态管理
- **target(Char target)**: 设置当前瞄准目标
  - 更新 `TargetHealthIndicator`
  - 更新 `InventoryPane` 目标状态
  
- **cancel()**: 取消当前的目标瞄准模式
  - 隐藏所有十字准星
  - 重置瞄准状态

## 内部组件

### 子组件
- **slot**: `ItemSlot` 实例，负责物品显示
- **crossB**: 基础十字准星图标
- **crossM**: 移动十字准星图标（跟随目标）

### 事件处理器
- **itemSelector**: 物品选择器，用于分配新物品到槽位
- **keyAction()**: 返回对应的快捷键绑定

## 视觉反馈

### 高亮效果
- **onPointerDown()**: 物品精灵亮度降低到 0.7f
- **onPointerUp()**: 物品精灵颜色重置

### 目标显示
- 十字准星连接槽位按钮和目标角色
- 目标角色上方显示生命值条
- 瞄准时槽位显示高亮效果

## 使用示例
```java
// 创建快捷槽按钮（通常由 Toolbar 自动创建）
QuickSlotButton slot1 = new QuickSlotButton(0);

// 手动设置物品
QuickSlotButton.set(new HealthPotion());

// 刷新所有槽位显示
QuickSlotButton.refresh();

// 取消目标瞄准
QuickSlotButton.cancel();
```

## 注意事项
- 快捷槽按钮通常由 `Toolbar` 组件自动管理
- 目标瞄准功能与 `InventoryPane` 和 `TargetHealthIndicator` 集成
- 物品分配限制：只有具有默认操作的物品才能分配到快捷槽
- 键盘绑定根据槽位索引自动映射（0->QUICKSLOT_1, 1->QUICKSLOT_2等）
- 背包丢失状态下，保持性物品（keptThroughLostInventory）仍然可用
- 自动瞄准算法计算开销较大，在性能敏感场景需要注意