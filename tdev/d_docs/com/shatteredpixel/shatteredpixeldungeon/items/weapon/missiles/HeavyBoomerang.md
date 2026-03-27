# HeavyBoomerang 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/missiles/HeavyBoomerang.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles |
| 类类型 | public class |
| 继承关系 | extends MissileWeapon |
| 代码行数 | 195 行 |

## 2. 类职责说明
HeavyBoomerang（重型回旋镖）是一种 Tier 4 的特殊投掷武器，投掷后会飞回投掷者。如果在飞回时击中敌人会造成额外伤害，即使没有击中也会返回。这使它成为一种可以重复使用的高效武器。

## 4. 继承与协作关系
```mermaid
classDiagram
    class MissileWeapon {
        <<abstract>>
        +int tier
        +boolean sticky
        +rangedHit(Char enemy, int cell)
        +rangedMiss(int cell)
    }
    class HeavyBoomerang {
        +int tier = 4
        +float baseUses = 5
        +boolean sticky = false
        +boolean circlingBack
        +max(int lvl)
        +adjacentAccFactor(Char owner, Char target)
        +pickupDelay()
        +rangedHit(Char enemy, int cell)
        +rangedMiss(int cell)
    }
    class CircleBack {
        +Buff
        +HeavyBoomerang boomerang
        +int thrownPos
        +int returnPos
        +int returnDepth
        +int returnBranch
        +int left
        +setup()
        +act()
    }
    MissileWeapon <|-- HeavyBoomerang
    HeavyBoomerang +-- CircleBack
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无静态常量 | - | - | - |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | 初始化块 | 物品图标 ItemSpriteSheet.BOOMERANG |
| hitSound | String | 初始化块 | 击中音效 Assets.Sounds.HIT_CRUSH |
| hitSoundPitch | float | 初始化块 | 音效音高 1f |
| tier | int | 初始化块 | 武器等级 4 |
| sticky | boolean | 初始化块 | false - 不粘在敌人身上 |
| baseUses | float | 初始化块 | 基础使用次数 5 |
| circlingBack | boolean | 包级 | 是否正在飞回 |

## 7. 方法详解

### max
**签名**: `public int max(int lvl)`
**功能**: 计算最大伤害
**参数**: `lvl` - 武器等级
**返回值**: 最大伤害值
**实现逻辑**: `return 4 * tier + (tier-1) * lvl;` // 基础16点

### adjacentAccFactor
**签名**: `protected float adjacentAccFactor(Char owner, Char target)`
**功能**: 计算相邻准确度因子
**返回值**: 飞回时为1.5，否则使用父类值

### pickupDelay
**签名**: `public float pickupDelay()`
**功能**: 返回捡起延迟
**返回值**: 飞回时为0（立即捡起），否则使用父类值

### rangedHit
**签名**: `protected void rangedHit(Char enemy, int cell)`
**功能**: 处理命中后的逻辑，启动返回机制
**参数**: 
- `enemy` - 被击中的敌人
- `cell` - 位置
**实现逻辑**:
```java
decrementDurability();
if (durability > 0){
    // 启动返回机制
    Buff.append(Dungeon.hero, CircleBack.class).setup(this, cell, Dungeon.hero.pos, Dungeon.depth, Dungeon.branch);
}
```

### rangedMiss
**签名**: `protected void rangedMiss(int cell)`
**功能**: 处理未命中的逻辑，同样会返回
**参数**: `cell` - 位置
**实现逻辑**:
```java
parent = null;
Buff.append(Dungeon.hero, CircleBack.class).setup(this, cell, Dungeon.hero.pos, Dungeon.depth, Dungeon.branch);
```

## 内部类

### CircleBack
**类型**: public static class extends Buff
**功能**: 回旋镖返回追踪器
**字段**:
| 字段 | 类型 | 说明 |
|------|------|------|
| boomerang | HeavyBoomerang | 回旋镖引用 |
| thrownPos | int | 投掷位置 |
| returnPos | int | 返回位置 |
| returnDepth | int | 返回层数 |
| returnBranch | int | 返回分支 |
| left | int | 剩余回合数 |

**方法**:
| 方法 | 说明 |
|------|------|
| `setup()` | 初始化返回参数 |
| `act()` | 处理返回逻辑，5回合后飞回 |
| `returnPos()` | 获取返回位置 |
| `activeDepth()` | 获取活动层数 |
| `cancel()` | 取消返回 |

**返回机制**:
1. 投掷后5回合开始返回
2. 飞回途中可能击中敌人
3. 击中敌人会消耗耐久度
4. 返回到投掷者位置

## 11. 使用示例
```java
// 创建重型回旋镖
HeavyBoomerang boomerang = new HeavyBoomerang();
// Tier 4投掷武器，会飞回

hero.belongings.collect(boomerang);
// 投掷后5回合会返回
// 飞回时可能击中敌人
```

## 注意事项
- `sticky = false` 不粘在敌人身上
- 投掷后必定返回
- 飞回途中击中敌人会消耗耐久度
- 可以多次重复使用

## 最佳实践
- 利用返回机制多次使用
- 站在敌人附近投掷，飞回时可能再次击中
- 配合移动位置让飞回路径经过敌人