# RingOfWealth 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/rings/RingOfWealth.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.rings |
| 类类型 | class |
| 继承关系 | extends Ring |
| 代码行数 | 325 |

## 2. 类职责说明
RingOfWealth 是财富戒指类，装备后增加敌人掉落物品的概率和质量。击杀敌人后有几率获得额外的消耗品和装备。这是收集资源和提升财富的最佳选择。

## 7. 方法详解

### dropChanceMultiplier(Char target)
**签名**: `public static float dropChanceMultiplier(Char target)`
**功能**: 获取掉落倍率
**参数**:
- target: Char - 目标角色
**返回值**: float - 掉落倍率
**实现逻辑**:
```java
// 第107-109行
return (float)Math.pow(1.20, getBuffedBonus(target, Wealth.class));
```
- 基础乘数：1.2
- 每级提升：约20%掉落率

### tryForBonusDrop(Char target, int tries)
**签名**: `public static ArrayList<Item> tryForBonusDrop(Char target, int tries)`
**功能**: 尝试获得额外掉落
**参数**:
- target: Char - 目标角色
- tries: int - 尝试次数
**返回值**: ArrayList<Item> - 掉落的物品列表
**实现逻辑**:
```java
// 第111-165行
int bonus = getBuffedBonus(target, Wealth.class);
if (bonus <= 0) return null;

// 追踪掉落计数器
CounterBuff triesToDrop = target.buff(TriesToDropTracker.class);
CounterBuff dropsToEquip = target.buff(DropsToEquipTracker.class);

ArrayList<Item> drops = new ArrayList<>();
triesToDrop.countDown(tries);

while (triesToDrop.count() <= 0) {
    if (dropsToEquip.count() <= 0) {
        // 生成装备掉落
        Item i = genEquipmentDrop(equipBonus - 1);
        drops.add(i);
        dropsToEquip.countUp(Random.NormalIntRange(5, 10));
    } else {
        // 生成消耗品掉落
        Item i = genConsumableDrop(bonus - 1);
        drops.add(i);
        dropsToEquip.countDown(1);
    }
    triesToDrop.countUp(Random.NormalIntRange(0, 20));
}
return drops;
```

### genConsumableDrop(int level)
**签名**: `public static Item genConsumableDrop(int level)`
**功能**: 生成消耗品掉落
**参数**:
- level: int - 戒指等级
**返回值**: Item - 消耗品

### genEquipmentDrop(int level)
**签名**: `private static Item genEquipmentDrop(int level)`
**功能**: 生成装备掉落
**参数**:
- level: int - 戒指等级
**返回值**: Item - 装备

## 11. 使用示例
```java
// 获取掉落倍率
float dropMult = RingOfWealth.dropChanceMultiplier(hero);
// +1级戒指：1.2倍掉落
// +2级戒指：1.44倍掉落

// 击杀敌人后尝试获得额外掉落
ArrayList<Item> drops = RingOfWealth.tryForBonusDrop(hero, 1);
if (drops != null) {
    for (Item item : drops) {
        Dungeon.level.drop(item, enemy.pos);
    }
}
```

## 注意事项
1. 每级提升约20%掉落率
2. 会掉落消耗品和装备
3. 掉落质量随等级提升

## 最佳实践
1. 资源收集首选
2. 长期游戏必备
3. 配合刷怪使用