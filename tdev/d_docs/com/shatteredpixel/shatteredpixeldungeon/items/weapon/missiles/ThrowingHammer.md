# ThrowingHammer 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/missiles/ThrowingHammer.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles |
| 类类型 | public class |
| 继承关系 | extends MissileWeapon |
| 代码行数 | 49 行 |

## 2. 类职责说明
ThrowingHammer（投掷锤）是一种 Tier 5 的顶级投掷武器，具有高伤害和极高的耐久度（基础使用次数12次）。投掷锤不会粘在敌人身上，并且可以立即捡起（无捡起延迟）。

## 4. 继承与协作关系
```mermaid
classDiagram
    class MissileWeapon {
        <<abstract>>
        +int tier
        +boolean sticky
        +float baseUses
        +int min(int lvl)
        +int max(int lvl)
        +pickupDelay()
    }
    class ThrowingHammer {
        +int tier = 5
        +float baseUses = 12
        +boolean sticky = false
        +pickupDelay()
        +max(int lvl)
    }
    MissileWeapon <|-- ThrowingHammer
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无静态常量 | - | - | - |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | 初始化块 | 物品图标 ItemSpriteSheet.THROWING_HAMMER |
| hitSound | String | 初始化块 | 击中音效 Assets.Sounds.HIT_CRUSH |
| hitSoundPitch | float | 初始化块 | 音效音高 0.8f（低沉） |
| tier | int | 初始化块 | 武器等级 5 |
| baseUses | float | 初始化块 | 基础使用次数 12 |
| sticky | boolean | 初始化块 | false - 不粘在敌人身上 |

## 7. 方法详解

### pickupDelay
**签名**: `public float pickupDelay()`
**功能**: 返回捡起延迟
**返回值**: 0（立即捡起）
**实现逻辑**: `return 0;`

### max
**签名**: `public int max(int lvl)`
**功能**: 计算最大伤害
**参数**: `lvl` - 武器等级
**返回值**: 最大伤害值
**实现逻辑**: `return 4 * tier + tier * lvl;` // 基础20点

## 11. 使用示例
```java
// 创建投掷锤
ThrowingHammer hammer = new ThrowingHammer();
// Tier 5投掷武器，高伤害高耐久

hero.belongings.collect(hammer);
// 可以立即捡起，耐久度极高
```

## 注意事项
- `sticky = false` 不粘在敌人身上
- 可以立即捡起（无延迟）
- 基础使用次数极高（12次）
- 使用粉碎音效

## 最佳实践
- 利用高耐久度长期作战
- 立即捡起特性方便回收
- 高伤害适合对付强敌