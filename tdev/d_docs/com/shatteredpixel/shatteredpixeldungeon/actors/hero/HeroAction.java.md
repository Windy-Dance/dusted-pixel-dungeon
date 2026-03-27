# HeroAction - 英雄动作类

## 概述
`HeroAction` 类及其内部静态子类定义了英雄在游戏中可以执行的所有基本动作类型。这些动作类用于封装玩家输入并将其转换为具体的游戏操作，是游戏输入处理和行动系统的核心组件。

每个具体的动作类都继承自 `HeroAction` 基类，并包含执行该动作所需的所有信息（如目标位置、交互对象等）。

## 基类字段

### 核心字段
- **dst**: `int` - 动作的目标位置（地图坐标）

## 动作子类

### Move (移动)
```java
public static class Move extends HeroAction {
    public Move(int dst) {
        this.dst = dst;
    }
}
```
**用途**: 英雄向指定位置移动
**参数**: `dst` - 目标地图坐标

### PickUp (拾取)
```java
public static class PickUp extends HeroAction {
    public PickUp(int dst) {
        this.dst = dst;
    }
}
```
**用途**: 在指定位置拾取物品
**参数**: `dst` - 物品所在位置

### OpenChest (开箱)
```java
public static class OpenChest extends HeroAction {
    public OpenChest(int dst) {
        this.dst = dst;
    }
}
```
**用途**: 打开指定位置的宝箱或特殊容器
**参数**: `dst` - 宝箱位置

### Buy (购买)
```java
public static class Buy extends HeroAction {
    public Buy(int dst) {
        this.dst = dst;
    }
}
```
**用途**: 购买指定位置待售的商品
**参数**: `dst` - 商品位置

### Interact (交互)
```java
public static class Interact extends HeroAction {
    public Char ch;
    public Interact(Char ch) {
        this.ch = ch;
    }
}
```
**用途**: 与指定的角色（NPC或敌人）进行交互
**参数**: `ch` - 交互目标角色对象

### Unlock (解锁)
```java
public static class Unlock extends HeroAction {
    public Unlock(int door) {
        this.dst = door;
    }
}
```
**用途**: 解锁指定位置的门或上锁的容器
**参数**: `dst` - 门或锁的位置

### LvlTransition (楼层切换)
```java
public static class LvlTransition extends HeroAction {
    public LvlTransition(int stairs) {
        this.dst = stairs;
    }
}
```
**用途**: 通过楼梯或其他过渡点切换楼层
**参数**: `dst` - 楼梯或过渡点位置

### Mine (采矿)
```java
public static class Mine extends HeroAction {
    public Mine(int wall) {
        this.dst = wall;
    }
}
```
**用途**: 挖掘墙壁、晶体或其他可挖掘的地形
**参数**: `dst` - 要挖掘的位置

### Alchemy (炼金)
```java
public static class Alchemy extends HeroAction {
    public Alchemy(int pot) {
        this.dst = pot;
    }
}
```
**用途**: 在炼金台进行炼金操作
**参数**: `dst` - 炼金台位置

### Attack (攻击)
```java
public static class Attack extends HeroAction {
    public Char target;
    public Attack(Char target) {
        this.target = target;
    }
}
```
**用途**: 攻击指定的敌人
**参数**: `target` - 攻击目标角色对象

## 设计模式

### 命令模式 (Command Pattern)
`HeroAction` 系统实现了经典的命令模式：
- **封装请求**: 每个动作类封装了执行特定操作所需的所有信息
- **解耦**: 输入处理系统与具体执行逻辑解耦
- **可扩展**: 添加新动作类型只需要创建新的子类

### 多态性
所有动作类型都继承自同一个基类，使得动作处理系统可以通过统一接口处理所有类型的动作：

```java
// 在 Hero.act() 方法中
if (curAction instanceof HeroAction.Move) {
    actMove((HeroAction.Move)curAction);
} else if (curAction instanceof HeroAction.Attack) {
    actAttack((HeroAction.Attack)curAction);
}
// ... 其他动作类型
```

## 使用流程

1. **输入检测**: `Hero.handle(int cell)` 方法根据玩家点击的位置确定要执行的动作类型
2. **动作创建**: 创建相应的 `HeroAction` 子类实例
3. **动作执行**: 在 `Hero.act()` 方法中根据动作类型调用具体的执行方法
4. **状态管理**: `curAction` 字段跟踪当前正在执行的动作

## 扩展性

### 添加新动作类型
要添加新的动作类型，只需：
1. 创建新的静态内部类继承 `HeroAction`
2. 在 `Hero.act()` 方法中添加对应的处理逻辑
3. 在 `Hero.handle()` 方法中添加触发条件

### 示例：添加新动作
```java
public static class CastSpell extends HeroAction {
    public Spell spell;
    public CastSpell(Spell spell, int dst) {
        this.spell = spell;
        this.dst = dst;
    }
}

// 在 Hero.act() 中
} else if (curAction instanceof HeroAction.CastSpell) {
    actCastSpell((HeroAction.CastSpell)curAction);
}
```

## 注意事项

1. **内存效率**: 所有动作类都是轻量级的，只包含必要信息
2. **类型安全**: 使用具体的子类而不是通用参数，提供编译时类型检查
3. **序列化**: 动作对象通常不需要持久化存储，因为它们代表即时操作
4. **线程安全**: 动作系统设计为单线程使用，在游戏主循环中执行
5. **错误处理**: 无效的动作会在执行时被忽略，不会导致游戏崩溃

## 典型使用场景

```java
// 移动到位置 (5, 10)
int pos = Dungeon.level.pointToCell(new Point(5, 10));
HeroAction moveAction = new HeroAction.Move(pos);
Dungeon.hero.curAction = moveAction;

// 攻击敌人
Mob enemy = visibleEnemies.get(0);
HeroAction attackAction = new HeroAction.Attack(enemy);
Dungeon.hero.curAction = attackAction;

// 开启宝箱
Heap chest = Dungeon.level.heaps.get(chestPos);
HeroAction openAction = new HeroAction.OpenChest(chestPos);
Dungeon.hero.curAction = openAction;
```

这个动作系统为 Shattered Pixel Dungeon 提供了灵活、可扩展且高效的游戏输入处理机制。