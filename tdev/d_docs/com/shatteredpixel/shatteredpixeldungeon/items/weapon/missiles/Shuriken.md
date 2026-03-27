# Shuriken 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/missiles/Shuriken.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles |
| 类类型 | public class |
| 继承关系 | extends MissileWeapon |
| 代码行数 | 83 行 |

## 2. 类职责说明
Shuriken（手里剑）是一种 Tier 2 的投掷武器，具有独特的快速投掷机制。投掷手里剑后，英雄在一定时间内可以立即再次行动。这使得手里剑非常适合连续攻击或紧急情况下的快速反应。

## 4. 继承与协作关系
```mermaid
classDiagram
    class MissileWeapon {
        <<abstract>>
        +int tier
        +int min(int lvl)
        +int max(int lvl)
        +onThrow(int cell)
        +castDelay(Char user, int cell)
    }
    class Shuriken {
        +int tier = 2
        +float baseUses = 5
        +max(int lvl)
        +onThrow(int cell)
        +castDelay(Char user, int cell)
    }
    class ShurikenInstantTracker {
        +FlavourBuff
        +int DURATION = 20
        +icon()
        +tintIcon(Image icon)
    }
    MissileWeapon <|-- Shuriken
    Shuriken +-- ShurikenInstantTracker
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无静态常量 | - | - | - |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | 初始化块 | 物品图标 ItemSpriteSheet.SHURIKEN |
| hitSound | String | 初始化块 | 击中音效 Assets.Sounds.HIT_STAB |
| hitSoundPitch | float | 初始化块 | 音效音高 1.2f |
| tier | int | 初始化块 | 武器等级 2 |
| baseUses | float | 初始化块 | 基础使用次数 5 |

## 7. 方法详解

### max
**签名**: `public int max(int lvl)`
**功能**: 计算最大伤害
**参数**: `lvl` - 武器等级
**返回值**: 最大伤害值
**实现逻辑**:
```java
return 4 * tier + (tier == 1 ? 2*lvl : tier*lvl);
// 基础8点伤害，每级+2
```

### onThrow
**签名**: `protected void onThrow(int cell)`
**功能**: 处理投掷逻辑，施加快速投掷buff
**参数**: `cell` - 目标格子
**实现逻辑**:
```java
super.onThrow(cell);
if (curUser.buff(ShurikenInstantTracker.class) == null) {
    // 施加快速投掷追踪器，持续19回合（实际效果20回合，减1因为投掷本身消耗回合）
    FlavourBuff.affect(curUser, ShurikenInstantTracker.class, ShurikenInstantTracker.DURATION-1);
}
```

### castDelay
**签名**: `public float castDelay(Char user, int cell)`
**功能**: 计算投掷延迟
**参数**: 
- `user` - 投掷者
- `cell` - 目标格子
**返回值**: 延迟时间
**实现逻辑**:
```java
return user.buff(ShurikenInstantTracker.class) != null ? super.castDelay(user, cell) : 0;
// 如果没有快速投掷buff，投掷延迟为0（立即行动）
// 如果有buff，则使用正常延迟
```

## 内部类

### ShurikenInstantTracker
**类型**: public static class extends FlavourBuff
**功能**: 快速投掷追踪器
**字段**:
| 字段 | 类型 | 说明 |
|------|------|------|
| DURATION | int | 持续时间常量，值为20 |

**方法**:
| 方法 | 说明 |
|------|------|
| `icon()` | 返回buff图标 BuffIndicator.THROWN_WEP |
| `tintIcon(Image icon)` | 图标着灰色 |
| `iconFadePercent()` | 计算图标淡出百分比 |

## 11. 使用示例
```java
// 创建手里剑
Shuriken shuriken = new Shuriken();
// Tier 2投掷武器，投掷后可立即行动

hero.belongings.collect(shuriken);
// 连续投掷可以有效利用快速投掷机制
```

## 注意事项
- 第一次投掷后可以立即行动（延迟为0）
- 快速投掷效果持续20回合
- 伤害略低于标准值（8 vs 10）
- 基础使用次数较低（5次）

## 最佳实践
- 利用快速投掷进行连续攻击
- 在紧急情况下快速反应
- 配合其他武器进行混合攻击