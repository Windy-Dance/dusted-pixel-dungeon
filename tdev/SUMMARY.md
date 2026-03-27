# 目录

## 架构
- [概览](architecture/overview.md)

## 引擎
- [Noosa 引擎](engine/noosa-engine.md)

## 系统
- [行动者系统](systems/actor-system.md)
- [战斗系统详细文档](systems/combat-system-detailed.md)
- [物品系统](systems/item-system.md)
- [关卡系统](systems/level-system.md)
- [天赋系统详细文档](systems/talent-system-detailed.md)
- [UI系统](systems/ui-system.md)

## 游戏设计
### 怪物
- [概览](game-design/mobs/overview.md)
- [下水道](game-design/mobs/sewers.md)
- [监狱](game-design/mobs/prison.md)
- [矿洞](game-design/mobs/caves.md)
- [矮人都城](game-design/mobs/city.md)
- [恶魔大厅](game-design/mobs/halls.md)
- [Boss](game-design/mobs/bosses.md)

### 物品
- [武器](game-design/items/weapons/)
- [护甲](game-design/items/armor/)
- [药剂](game-design/items/potions/)
- [卷轴](game-design/items/scrolls/)
- [法杖](game-design/items/wands/)
- [戒指](game-design/items/rings/)
- [神器](game-design/items/artifacts/)
- [饰品](game-design/items/trinkets/)
- [食物](game-design/items/food.md)

## API 参考
- [类索引](api/class-index.md)
- [包索引](api/package-index.md)
- [继承关系图](api/inheritance-diagrams.md)

### 核心 API 参考
#### 行动者 (Actors)
- [Actor 基类](reference/actors/actor-api.md)
- [Char 基类](reference/actors/char-api.md)
- [Mob 基类](reference/actors/mob-api.md)
- [Hero 英雄](reference/actors/hero-api.md)
- [Buff 基类](reference/actors/buff-api.md)
- [Blob 基类](reference/actors/blob-api.md)

#### 物品 (Items)
- [Item 基类](reference/items/item-api.md)
- [Artifact 神器](reference/items/artifact-api.md)
- [Weapon 武器](reference/items/weapon-api.md)
- [Armor 护甲](reference/items/armor-api.md)
- [Potion 药水](reference/items/potion-api.md)
- [Scroll 卷轴](reference/items/scroll-api.md)
- [Wand 法杖](reference/items/wand-api.md)
- [Ring 戒指](reference/items/ring-api.md)
- [Trinket 饰品](reference/items/trinket-api.md)
- [Food 食物](reference/items/food-api.md)
- [Key 钥匙](reference/items/key-api.md)

#### 关卡 (Levels)
- [Level 基类](reference/levels/level-api.md)
- [Room 房间](reference/levels/room-api.md)
- [Builder 构建器](reference/levels/builder-api.md)
- [Painter 绘制器](reference/levels/painter-api.md)

#### UI 组件
- [Window 窗口](reference/ui/window-api.md)
- [Button 按钮](reference/ui/button-api.md)

#### 引擎 (Engine)
- [Gizmo 基类](reference/engine/gizmo-api.md)
- [Visual 可视对象](reference/engine/visual-api.md)
- [Group 组](reference/engine/group-api.md)
- [Scene 场景](reference/engine/scene-api.md)
- [Component 组件](reference/engine/component-api.md)

## 教程
### 物品
- [创建新神器](tutorials/items/creating-artifact.md)
- [创建新武器](tutorials/items/creating-weapon.md)

### 怪物
- [创建新怪物](tutorials/actors/creating-mob.md)

### 资源
- [添加精灵图](tutorials/assets/adding-sprites.md)

## 集成指南
- [注册点汇总](integration/registration-guide.md)