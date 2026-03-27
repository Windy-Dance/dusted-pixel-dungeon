# Sword 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/Sword.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee |
| 类类型 | class |
| 继承关系 | extends MeleeWeapon |
| 代码行数 | 152 |

## 2. 类职责说明
Sword 是长剑类，3阶近战武器。定义了"劈砍"技能的通用实现，其他剑类武器也使用此技能。劈砍造成额外伤害，击杀敌人后可连续使用。

## 7. 方法详解

### cleaveAbility(Hero hero, Integer target, float dmgMulti, int dmgBoost, MeleeWeapon wep)
**签名**: `public static void cleaveAbility(Hero hero, Integer target, float dmgMulti, int dmgBoost, MeleeWeapon wep)`
**功能**: 静态方法，劈砍技能实现
**参数**:
- hero: Hero - 英雄
- target: Integer - 目标位置
- dmgMulti: float - 伤害倍率
- dmgBoost: int - 额外伤害
- wep: MeleeWeapon - 武器
**效果**:
1. 攻击敌人造成额外伤害
2. 击杀后获得CleaveTracker（可连续使用）
3. 未击杀则消耗正常回合

### CleaveTracker 内部类
**签名**: `public static class CleaveTracker extends FlavourBuff`
**功能**: 追踪劈砍连击状态
**持续**: 4回合内下次劈砍免费

## 11. 使用示例
```java
// 3阶武器，标准伤害
Sword sword = new Sword();
// 技能：劈砍，+(5+等级)额外伤害
// 击杀后可连续使用
```

## 最佳实践
1. 对低血量敌人使用确保击杀触发连击
2. 配合高伤害附魔
3. 群战时逐个击杀