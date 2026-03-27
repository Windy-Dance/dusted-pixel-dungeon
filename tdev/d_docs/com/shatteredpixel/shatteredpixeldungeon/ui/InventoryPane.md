# InventoryPane 类

## 概述
`InventoryPane` 是 Shattered Pixel Dungeon 中显示玩家背包的完整 UI 面板组件。它整合了装备槽、背包物品、袋子管理、金币/能量显示和物品选择功能。

## 功能特性
- **装备显示**：显示武器、护甲、饰品、杂项和戒指五个装备槽
- **背包管理**：显示当前选中袋子的所有物品（最多20个）
- **多袋子支持**：支持最多5个不同类型的袋子切换
- **资源显示**：显示金币数量和能量值
- **物品选择**：支持物品选择器模式，用于技能或操作的目标选择
- **目标瞄准**：集成右键菜单和瞄准功能
- **快捷交互**：支持左键使用、右键菜单、中键直接使用的完整交互

## 核心组件

### 视觉元素
- **bg**: 背景九宫格（Chrome.Type.TOAST_TR_HEAVY）
- **blocker**: 阻塞层，用于处理点击外部取消选择器

### 装备槽
- **equipped[]**: 5个装备槽位（InventorySlot）
  - `equipped[0]`: 武器
  - `equipped[1]`: 护甲  
  - `equipped[2]`: 饰品
  - `equipped[3]`: 杂项
  - `equipped[4]`: 戒指

### 背包物品
- **bagItems[]**: 20个背包物品槽位（InventorySlot）
- **lastBag**: 静态变量，跟踪当前选中的袋子

### 资源显示
- **gold/goldTxt**: 金币图标和文本
- **energy/energyTxt**: 能量图标和文本
- **promptTxt**: 选择器提示文本

### 袋子按钮
- **bags[]**: 5个袋子按钮（BagButton）
  - 显示对应的袋子图标和名称
  - 点击切换当前显示的袋子

### 目标瞄准
- **crossB/crossM**: 十字准星图标，用于目标瞄准模式

## 核心方法

### 初始化和布局
- **createChildren()**: 初始化所有子组件
- **layout()**: 布局所有组件到固定尺寸（187x82像素）

### 状态管理
- **updateInventory()**: 
  - 更新所有槽位的物品显示
  - 同步选择器状态
  - 处理背包丢失状态
  - 更新启用状态

- **setSelector(WndBag.ItemSelector selector)**: 设置物品选择器
- **getSelector()**: 获取当前选择器
- **isSelecting()**: 检查是否处于选择模式

### 目标功能
- **useTargeting()**: 进入目标瞄准模式
- **cancelTargeting()**: 取消目标瞄准模式
- **clearTargetingSlot()**: 清除当前目标槽位

### 全局访问
- **refresh()**: 静态方法，刷新当前实例
- **alpha(float value)**: 设置整体透明度

## 内部类

### BagButton 类
袋子切换按钮：

#### 功能特性
- **动态图标**: 根据袋子类型显示不同图标
- **状态指示**: 当前选中袋子显示高亮背景
- **键盘绑定**: 绑定到 BAG_1 到 BAG_5 快捷键
- **悬停提示**: 显示袋子名称

#### 图标映射
- **VelvetPouch**: 种子袋图标 (SEED_POUCH)
- **ScrollHolder**: 卷轴袋图标 (SCROLL_HOLDER)  
- **MagicalHolster**: 魔杖袋图标 (WAND_HOLSTER)
- **PotionBandolier**: 药水袋图标 (POTION_BANDOLIER)
- **其他**: 背包图标 (BACKPACK)

### InventoryPaneSlot 类
专门的库存槽位，重写交互逻辑：

#### 交互处理
- **onClick()**: 
  - 选择器模式：选择物品并关闭选择器
  - 正常模式：打开物品使用窗口
  
- **onRightClick()**: 显示右键菜单
- **onMiddleClick()**: 直接执行物品默认操作
- **onLongClick()**: 分配到快捷槽位

## 使用示例
```java
// 创建库存面板
InventoryPane inventory = new InventoryPane();

// 设置物品选择器
WndBag.ItemSelector selector = new WndBag.ItemSelector() {
    @Override
    public void onSelect(Item item) {
        // 处理物品选择
    }
};
inventory.setSelector(selector);

// 刷新显示
InventoryPane.refresh();

// 控制透明度
inventory.alpha(0.8f);
```

## 注意事项
- 面板尺寸固定为 187x82 像素
- 装备槽和背包物品都使用 InventorySlot 组件
- 选择器模式会覆盖资源显示区域显示提示文本
- 目标瞄准模式与 QuickSlotButton 集成
- 背包丢失状态下不可保留的物品会被禁用
- 所有窗口打开都会自动居中到库存面板位置
- 键盘事件会正确处理选择器取消逻辑