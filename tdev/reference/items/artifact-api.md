# Artifact API 参考

## 类声明
`public class Artifact extends KindofMisc`

## 类职责
Artifact是所有神器的基类，提供充能系统、被动/主动效果、等级成长等独特机制。神器不可升级但可通过使用成长。

## 关键字段

| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|-------|------|---------|-------|------|
| `passiveBuff` | `Buff` | protected | null | 被动效果的Buff实例，装备时自动激活 |
| `activeBuff` | `Buff` | protected | null | 主动效果的Buff实例（某些神器使用） |
| `exp` | `int` | protected | 0 | 经验值，用于追踪神器等级成长进度 |
| `levelCap` | `int` | protected | 0 | 神器的最大等级上限 |
| `charge` | `int` | protected | 0 | 当前充能值（整数部分） |
| `partialCharge` | `float` | protected | 0.0f | 充能的小数部分，通常在达到1.0时转化为完整充能 |
| `chargeCap` | `int` | protected | 0 | 充能上限，不同神器有不同的上限值 |
| `cooldown` | `int` | protected | 0 | 冷却时间计数器，用于追踪效果或技能的冷却状态 |

## 可重写方法

| 方法签名 | 返回值 | 默认行为 | 说明 |
|---------|--------|----------|------|
| `passiveBuff()` | `ArtifactBuff` | 返回null | 创建并返回被动效果的Buff实例，装备时自动调用 |
| `activeBuff()` | `ArtifactBuff` | 返回null | 创建并返回主动效果的Buff实例 |
| `charge(Hero, float)` | void | 无操作 | 处理充能逻辑，子类可重写此方法实现自定义充能机制 |
| `activate(Char)` | void | 激活被动Buff | 在角色身上激活神器效果 |

## 装备/卸下方法

### `doEquip(Hero hero)`
- **功能**: 将神器装备到英雄身上
- **限制**: 同一类型的神器不能同时装备两个（检查belongings.artifact和belongings.misc）
- **行为**: 调用父类doEquip后自动识别神器，返回装备结果

### `doUnequip(Hero hero, boolean collect, boolean single)`
- **功能**: 卸下神器
- **行为**: 调用父类doUnequip后，如果存在被动Buff则自动分离(detach)

### `activate(Char ch)`
- **功能**: 在指定角色上激活神器效果
- **行为**: 先移除现有的被动Buff，然后创建新的被动Buff并附加到角色上

## 充能系统

充能系统是Artifact的核心机制之一，通过三个关键字段协同工作：

1. **`charge` (int)**: 存储完整的充能单位数量
2. **`partialCharge` (float)**: 存储不完整的充能进度（小数部分）
3. **`chargeCap` (int)**: 充能的上限值

**充能机制**:
- 当获得充能时，通常先增加`partialCharge`
- 当`partialCharge >= 1.0`时，将其转换为完整的充能单位（`charge++`，`partialCharge -= 1.0`）
- 如果`chargeCap > 0`，则`charge`不能超过`chargeCap`
- 状态显示逻辑：
  - 如果`chargeCap == 100`，显示为百分比格式（如"75%"）
  - 如果`chargeCap > 0`，显示为分数格式（如"3/5"）
  - 如果没有上限但有充能，只显示充能数值
  - 无充能时返回null（不显示状态）

**充能方法**:
- 子类通过重写`charge(Hero target, float amount)`方法实现自定义充能逻辑
- `ArtifactBuff`内部类提供了便捷的`charge(Hero target, float amount)`方法，直接调用外部Artifact的充能方法

## 等级系统

Artifact使用独特的等级系统，不同于普通物品的升级机制：

### 关键字段
- **`exp`**: 经验值，用于追踪成长进度
- **`levelCap`**: 最大等级上限
- **`level()`**: 继承自Item父类的实际等级值

### 等级计算
- **可见等级**: `visiblyUpgraded()`方法返回0-10的等级显示值
  ```java
  return levelKnown ? Math.round((level()*10)/(float)levelCap) : 0;
  ```
- 这意味着实际等级被映射到0-10的显示范围内，便于UI展示

### 等级转移
- **`transferUpgrade(int transferLvl)`**: 将另一个神器的等级转移过来
  ```java
  upgrade(Math.round((transferLvl*levelCap)/10f));
  ```
- **`resetForTrinity(int visibleLevel)`**: 为三神器模式重置状态，设置指定的可见等级并充满充能

### 特殊行为
- **不可升级**: `isUpgradable()`始终返回false，神器不能通过常规方式升级
- **经验值管理**: 某些神器使用`exp`字段追踪使用进度，达到条件时自动升级

## ArtifactBuff内部类

`ArtifactBuff`是Artifact的内部Buff类，专为神器效果设计：

### 核心特性
- **自动附着处理**: 在`attachTo()`中处理游戏加载时的特殊情况
- **便捷访问方法**:
  - `itemLevel()`: 返回神器的实际等级
  - `isCursed()`: 检查神器是否被诅咒（且目标没有魔法免疫）
  - `charge(Hero target, float amount)`: 方便地调用外部Artifact的充能方法

### 使用方式
子类神器通过重写`passiveBuff()`或`activeBuff()`方法返回自定义的`ArtifactBuff`子类实例：

```java
@Override
protected ArtifactBuff passiveBuff() {
    return new MyCustomBuff();
}

private class MyCustomBuff extends ArtifactBuff {
    // 自定义Buff逻辑
}
```

## 静态方法

### `artifactProc(Char target, int artifLevel, int chargesUsed)`
- **功能**: 处理神器攻击后的特殊效果
- **触发条件**: 当使用神器进行攻击时调用
- **包含的效果**:
  1. **引导之光**: 祭司副职业在目标身上有照明效果时造成额外伤害
  2. **灼热之光**: 具有相应天赋时为目标添加照明效果
  3. **日耀**: 具有相应天赋时有几率使目标失明

## 使用示例

### 示例1: 创建简单神器
```java
public class SimpleArtifact extends Artifact {
    
    @Override
    protected ArtifactBuff passiveBuff() {
        return new SimplePassiveBuff();
    }
    
    private class SimplePassiveBuff extends ArtifactBuff {
        @Override
        public boolean act() {
            // 每回合执行的简单被动效果
            spend(TICK);
            return true;
        }
    }
}
```

### 示例2: 创建带充能的神器
```java
public class ChargedArtifact extends Artifact {
    
    {
        chargeCap = 5; // 设置充能上限为5
        levelCap = 3;  // 设置最大等级为3
    }
    
    @Override
    public void charge(Hero target, float amount) {
        // 自定义充能逻辑
        partialCharge += amount;
        while (partialCharge >= 1 && charge < chargeCap) {
            partialCharge--;
            charge++;
        }
    }
    
    @Override
    protected ArtifactBuff passiveBuff() {
        return new ChargingBuff();
    }
    
    private class ChargingBuff extends ArtifactBuff {
        @Override
        public boolean act() {
            // 每回合自动充能
            charge(target, 0.2f);
            spend(TICK);
            return true;
        }
    }
}
```

## 相关子类

以下是所有Artifact的具体子类实现：

1. **AlchemistsToolkit.java** - 炼金术士工具包
2. **CapeOfThorns.java** - 荆棘披风
3. **ChaliceOfBlood.java** - 血杯
4. **CloakOfShadows.java** - 暗影斗篷
5. **DriedRose.java** - 干枯玫瑰
6. **EtherealChains.java** - 以太锁链
7. **HolyTome.java** - 神圣典籍
8. **HornOfPlenty.java** - 丰饶号角
9. **LloydsBeacon.java** - 劳埃德信标
10. **MasterThievesArmband.java** - 大盗护腕
11. **SandalsOfNature.java** - 自然凉鞋
12. **SkeletonKey.java** - 骷髅钥匙
13. **TalismanOfForesight.java** - 先知护符
14. **TimekeepersHourglass.java** - 时光沙漏
15. **UnstableSpellbook.java** - 不稳定法术书

## 常见错误

1. **忘记调用父类方法**: 在重写`doEquip`/`doUnequip`时必须调用`super`方法
2. **充能溢出**: 未检查`chargeCap`限制导致充能超过上限
3. **Buff内存泄漏**: 卸下神器时未正确分离Buff，导致效果持续存在
4. **等级计算错误**: 混淆实际等级(`level()`)和显示等级(`visiblyUpgraded()`)
5. **状态显示问题**: 未正确实现`status()`方法，导致UI显示异常
6. **序列化遗漏**: 添加新字段时忘记在`storeInBundle`/`restoreFromBundle`中处理
7. **并发问题**: 在多线程环境中修改充能状态时未考虑同步