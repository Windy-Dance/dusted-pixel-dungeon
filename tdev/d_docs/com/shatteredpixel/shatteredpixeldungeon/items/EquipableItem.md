# EquipableItem.java - 可装备物品基类

## 概述
`EquipableItem` 类继承自 `Item`，为所有可装备物品（如武器、护甲、戒指等）提供通用功能。它处理装备/卸装备逻辑、诅咒效果和相关的UI交互。

## 核心特性

### 动作系统
- **AC_EQUIP**: 装备动作常量
- **AC_UNEQUIP**: 卸装备动作常量
- 自动根据当前装备状态在动作列表中显示相应的选项

### 装备管理
- **doEquip(Hero hero)**: 抽象方法，具体子类必须实现实际的装备逻辑
- **doUnequip(Hero hero, boolean collect, boolean single)**: 处理卸装备逻辑
  - 检查诅咒状态（未免疫魔法时无法卸下被诅咒的物品）
  - 管理快捷栏位置的重新分配
  - 支持是否将物品收集回背包的选项

### 特殊处理
- **equipCursed(Hero hero)**: 静态方法，处理被诅咒物品的装备效果（显示粒子效果和播放音效）
- **timeToEquip(Hero hero)**: 返回装备所需的时间（默认1秒）
- **activate(Char ch)**: 物品装备后激活的回调方法（默认为空实现）

## 继承要求
所有继承 `EquipableItem` 的子类必须实现：
- **doEquip(Hero hero)**: 具体的装备逻辑实现

## 使用场景
- **武器**: `KindOfWeapon` 及其子类
- **护甲**: Armor及其子类（位于armor包中）
- **戒指**: Ring及其子类（位于rings包中）
- **神器**: Artifact及其子类（位于artifacts包中）

## 注意事项
1. 被诅咒的物品在没有魔法免疫的情况下无法卸下
2. 装备/卸装备操作会自动处理快捷栏的重新分配
3. 骨架标志（bones = true）已默认设置，允许物品出现在英雄遗骸中
4. 拾取未识别的可装备物品时会触发冒险者指南的提示