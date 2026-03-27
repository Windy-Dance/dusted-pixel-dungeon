# Amulet.java - 护身符

## 概述
`Amulet` 类代表游戏的胜利物品——护身符，是玩家通关游戏的关键物品。拾取护身符会触发游戏结局场景，并根据当前挑战状态显示不同的描述文本。

## 核心特性

### 游戏结束机制
- **AC_END**: 结束游戏的动作常量
- **execute(Hero hero, String action)**: 处理手动结束游戏的请求
- **doPickUp(Hero hero, int pos)**: 拾取时自动触发胜利场景（仅首次获得时）

### 胜利处理流程
1. 标记 `Statistics.amuletObtained = true`
2. 立即切换到 `AmuletScene`（胜利场景）
3. 验证胜利成就和挑战成就
4. 保存游戏进度和全局统计数据

### 描述文本变化
- **普通模式**: 显示护身符的起源故事描述
- **Ascension Challenge（飞升挑战）**: 显示与飞升相关的特殊描述
- 通过 `Dungeon.hero.buff(AscensionChallenge.class)` 判断当前是否处于飞升挑战模式

## 特殊属性

### 物品标志
- **unique = true**: 作为唯一物品，死亡后保留
- **isIdentified()**: 始终返回true（无需识别）
- **isUpgradable()**: 始终返回false（不可升级）

### 视觉表现
- 使用 `ItemSpriteSheet.AMULET` 图像
- 在物品列表中显示特殊的图标

## 使用场景
- **游戏胜利**: 玩家在第25层（或相应层数）找到并拾取护身符
- **飞升挑战**: 在Ascension Challenge模式下，护身符的行为和描述会发生变化

## 注意事项
1. 护身符的拾取过程使用Actor系统延迟处理，确保拾取动画完成后再切换场景
2. 胜利场景会自动保存所有游戏数据，包括成就和统计数据
3. 护身符描述会根据游戏模式动态变化，提供不同的叙事体验
4. 在Ascension Challenge模式下，护身符的动作列表会被清空（无法手动结束游戏）