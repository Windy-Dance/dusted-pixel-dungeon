# MissileWeapon 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/missiles/MissileWeapon.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles |
| 类类型 | abstract class |
| 继承关系 | extends Weapon |
| 代码行数 | 831 |

## 2. 类职责说明
MissileWeapon 是所有远程投掷武器的抽象基类，扩展了 Weapon 类，添加了远程武器特有的功能：耐久度系统、堆叠系统、套装追踪系统。远程武器可以堆叠，每次使用消耗耐久度，用完会损坏。

## 实例字段表
| 字段名 | 类型 | 说明 |
|--------|------|------|
| stackable | boolean | true，可堆叠 |
| setID | long | 套装唯一标识 |
| durability | float | 当前耐久度，最大100 |
| baseUses | float | 基础使用次数，默认8 |
| tier | int | 武器阶数 |
| sticky | boolean | 是否粘在敌人身上 |
| holster | boolean | 是否在魔法枪套中 |
| parent | MissileWeapon | 父武器引用（投掷时） |

## 7. 方法详解

### min(int lvl) / max(int lvl)
**签名**: `@Override public int min(int lvl) / public int max(int lvl)`
**功能**: 计算最小/最大伤害
**实现逻辑**:
```java
// 第108-126行
public int min(int lvl) {
    return 2 * tier + lvl;
}

public int max(int lvl) {
    return 5 * tier + tier * lvl;
}
```

### durabilityPerUse(int level)
**签名**: `public float durabilityPerUse(int level)`
**功能**: 计算每次使用消耗的耐久度
**返回值**: float - 每次使用消耗
**实现逻辑**:
```java
// 第445-472行
float usages = baseUses * (float)(Math.pow(1.5f, level));

// 天赋增加耐久
if (Dungeon.hero.hasTalent(Talent.DURABLE_PROJECTILES)) {
    usages *= 1.25f + (0.25f * pointsInTalent);
}

// 魔法枪套增加50%耐久
if (holster) {
    usages *= MagicalHolster.HOLSTER_DURABILITY_FACTOR;
}

// 速度强化增加50%，伤害强化减少33%
usages /= augment.delayFactor(1f);

// 神射手戒指增加耐久
usages *= RingOfSharpshooting.durabilityMultiplier(Dungeon.hero);

return MAX_DURABILITY / usages;
```

### onThrow(int cell)
**签名**: `@Override protected void onThrow(int cell)`
**功能**: 投掷到指定格子的处理
**实现逻辑**:
```java
// 第266-293行
Char enemy = Actor.findChar(cell);
if (enemy == null || enemy == curUser) {
    // 没有敌人，掉落在地
    super.onThrow(cell);
} else {
    // 攻击敌人
    if (!curUser.shoot(enemy, this)) {
        rangedMiss(cell);
    } else {
        rangedHit(enemy, cell);
    }
}
```

### rangedHit(Char enemy, int cell)
**签名**: `protected void rangedHit(Char enemy, int cell)`
**功能**: 命中敌人后的处理
**实现逻辑**:
```java
// 第404-417行
decrementDurability();  // 消耗耐久

if (durability > 0 && !spawnedForEffect) {
    // 粘在敌人身上
    if (sticky && enemy != null && enemy.isActive()) {
        Buff.affect(enemy, PinCushion.class).stick(this);
    } else {
        Dungeon.level.drop(this, cell).sprite.drop();
    }
}
```

### decrementDurability()
**签名**: `protected void decrementDurability()`
**功能**: 减少耐久度
**实现逻辑**:
```java
// 第474-500行
if (parent != null) {
    // 从父武器减少耐久
    if (parent.durability <= parent.durabilityPerUse()) {
        durability = 0;  // 破坏当前武器
        parent.durability = MAX_DURABILITY;
        GLog.n(Messages.get(this, "has_broken"));
    } else {
        parent.durability -= parent.durabilityPerUse();
        if (parent.durability > 0 && parent.durability <= parent.durabilityPerUse()) {
            GLog.w(Messages.get(this, "about_to_break"));
        }
    }
}
```

## 11. 使用示例

### 创建自定义投掷武器
```java
public class CustomDart extends MissileWeapon {
    {
        tier = 2;
        baseUses = 10;
        sticky = true;
    }
    
    @Override
    public int defaultQuantity() {
        return 5;  // 默认5支
    }
}
```

### 投掷武器使用
```java
// 创建投掷武器
MissileWeapon dart = new ThrowingKnife();
dart.quantity = 3;

// 投掷
dart.cast(hero, targetCell);

// 耐久度计算
float usesLeft = dart.durability / dart.durabilityPerUse();
```

## 注意事项

1. **耐久度系统**: 每次使用消耗耐久，用完损坏
2. **堆叠系统**: 相同套装ID的武器可堆叠
3. **粘性**: 命中敌人后可能粘在敌人身上
4. **魔法枪套**: 在枪套中增加50%耐久

## 最佳实践

1. 设置 tier 和 baseUses 定义伤害和耐久
2. 使用 sticky 控制是否粘在敌人身上
3. 重写 defaultQuantity 设置默认数量