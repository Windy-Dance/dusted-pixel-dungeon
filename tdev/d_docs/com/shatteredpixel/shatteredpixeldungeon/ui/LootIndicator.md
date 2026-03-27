# LootIndicator 类

## 概述
`LootIndicator` 是 Shattered Pixel Dungeon 中用于显示可拾取物品的标签组件。它继承自 `Tag` 类，当英雄站在包含物品的堆叠（Heap）上时自动显示。

## 功能特性
- **自动检测**：实时检测英雄当前位置是否有可拾取物品
- **物品预览**：显示堆叠顶部物品或特殊堆叠图标
- **交互支持**：点击可触发拾取操作
- **键盘绑定**：绑定到 TAG_LOOT 和 WAIT_OR_PICKUP 快捷键
- **状态管理**：根据英雄就绪状态自动启用/禁用

## 核心方法

### 构造函数
- `LootIndicator()` - 创建拾取指示器
  - 使用蓝色主题色 (`0x185898`)
  - 默认大小为 `SIZE x SIZE` (24x24 像素)
  - 初始隐藏状态

### 组件创建
- **createChildren()**: 
  - 添加物品槽位 (`ItemSlot`)
  - 配置槽位不显示额外信息
  - 设置点击和键盘事件处理

### 布局管理
- **layout()**: 
  - 根据翻转状态调整物品槽位置
  - 设置槽位边距（左侧布局：左2右0；右侧布局：左0右2）

### 状态更新
- **update()**: 
  - 检测英雄当前位置的堆叠
  - 根据堆叠类型设置相应的图标
  - 更新可见性和交互状态
  - 处理物品数量变化的闪烁效果

### 交互处理
- **onClick()**: 
  - 触发拾取操作 (`Dungeon.hero.handle(Dungeon.hero.pos)`)
  - 推进英雄回合

## 堆叠类型映射

### 特殊堆叠图标
- **CHEST**: 普通宝箱 (`ItemSlot.CHEST`)
- **LOCKED_CHEST**: 上锁宝箱 (`ItemSlot.LOCKED_CHEST`)  
- **CRYSTAL_CHEST**: 水晶宝箱 (`ItemSlot.CRYSTAL_CHEST`)
- **TOMB**: 墓穴 (`ItemSlot.TOMB`)
- **SKELETON**: 骨骼 (`ItemSlot.SKELETON`)
- **REMAINS**: 遗骸 (`ItemSlot.REMAINS`)

### 普通堆叠
- 显示堆叠顶部的实际物品

## 快捷键绑定
- **keyAction()**: 返回 `SPDAction.TAG_LOOT`
- **secondaryTooltipAction()**: 返回 `SPDAction.WAIT_OR_PICKUP`

## 使用示例
```java
// 创建拾取指示器
LootIndicator lootTag = new LootIndicator();

// 添加到场景（通常由 GameScene 自动管理）
GameScene.addToFront(lootTag);

// 指示器会自动根据 hero.pos 的 heap 状态显示/隐藏
```

## 注意事项
- 指示器仅在以下条件下显示：
  - 英雄就绪 (`Dungeon.hero.ready`)
  - 当前位置有堆叠 (`Dungeon.level.heaps.get(Dungeon.hero.pos) != null`)
- 图标闪爾示意物品数量发生变化
- 只有普通物品堆叠才会显示实际物品，特殊堆叠显示对应图标
- 英雄死亡或未就绪时指示器会被禁用
- 点击事件会直接调用英雄的 handle 方法进行拾取