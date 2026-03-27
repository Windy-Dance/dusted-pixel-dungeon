# KindOfWeapon.java - 武器基类

## 概述
`KindOfWeapon` 类继承自 `EquipableItem`，为所有武器类型提供通用功能。它处理双武器系统、伤害计算、准确度因子以及与英雄天赋的交互。

## 核心特性

### 双武器支持
- 支持主武器和副武器槽位
- 通过 `HeroSubClass.CHAMPION`（冠军）子职业启用双武器选择界面
- **doEquip(Hero hero)**: 装备为主武器
- **equipSecondary(Hero hero)**: 装备为副武器
- **isEquipped(Hero hero)**: 检查是否装备在任一武器槽位

### 伤害系统
- **min() / max()**: 获取当前等级下的最小/最大伤害值
- **min(int lvl) / max(int lvl)**: 抽象方法，子类必须实现基于等级的伤害计算
- **damageRoll(Char owner)**: 计算实际伤害（英雄使用整数范围，其他角色使用正态分布）

### 战斗属性
- **accuracyFactor(Char owner, Char target)**: 准确度修正因子（默认1.0f）
- **delayFactor(Char owner)**: 攻击速度修正因子（默认1.0f）
- **reachFactor(Char owner)**: 攻击范围因子（默认1格）
- **canReach(Char owner, int target)**: 检查是否能攻击到目标位置
- **defenseFactor(Char owner)**: 防御加成（默认0）

### 特殊效果
- **proc(Char attacker, Char defender, int damage)**: 攻击触发效果处理（默认返回原始伤害）
- **hitSound(float pitch)**: 播放攻击音效

### 天赋集成
- **Talent.SWIFT_EQUIP**: 快速装备天赋支持（减少装备时间到0）
- **Talent.HOLY_INTUITION**: 神圣直觉天赋，在装备时有几率发现诅咒
- **Talent.IMPROVISED_PROJECTILES**: 即兴投掷天赋，投掷非导弹武器时造成致盲效果

## 继承要求
所有武器子类必须实现：
- **min(int lvl)**: 基于等级的最小伤害计算
- **max(int lvl)**: 基于等级的最大伤害计算

## 使用场景
- **近战武器**: Sword, Axe, Hammer等（位于weapon/melee包中）
- **远程武器**: Bow, Crossbow等（位于weapon/ranged包中）
- **投掷武器**: Dart, Boomerang等（位于weapon/missiles包中）

## 注意事项
1. 武器的伤害范围会受到英雄的Buff影响（如Degrade降级效果）
2. Champion子职业可以同时装备主副武器，其他职业只能装备主武器
3. 被诅咒的武器装备时会触发诅咒效果并显示提示消息
4. Swift Equip天赋有冷却时间限制，最多可使用两次（取决于天赋等级）