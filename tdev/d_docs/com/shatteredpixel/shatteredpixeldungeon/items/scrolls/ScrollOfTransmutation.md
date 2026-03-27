# ScrollOfTransmutation 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/ScrollOfTransmutation.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.scrolls |
| 类类型 | class |
| 继承关系 | extends InventoryScroll |
| 代码行数 | 404 |

## 2. 类职责说明
ScrollOfTransmutation 是转化卷轴类，使用后可以选择一个物品将其转化为同类型的另一个物品。武器转化为同级武器，药水/卷轴可以在普通版和异域版之间转化。这是一个强大的资源管理工具，可以将不需要的物品转化为更有用的东西。

## 7. 方法详解

### usableOnItem(Item item)
**签名**: `@Override protected boolean usableOnItem(Item item)`
**功能**: 检查物品是否可以转化
**实现逻辑**:
```java
// 第71-97行
// 近战武器（除采矿关卡的镐子）
if (item instanceof MeleeWeapon) {
    return !(item instanceof Pickaxe && Dungeon.level instanceof MiningLevel);
}
// 远程武器（除未沾毒飞镖）
else if (item instanceof MissileWeapon) {
    return item.getClass() != Dart.class;
}
// 药水（非酿剂和灵药）
else if (item instanceof Potion) {
    return !(item instanceof Elixir || item instanceof Brew);
}
// 卷轴（可以转化自己）
else if (item instanceof Scroll) {
    return item != this || item.quantity() > 1 || identifiedByUse;
}
// 神器（非独特）
else if (item instanceof Artifact) {
    return !item.unique;
}
// 戒指、法杖、饰品、种子、符石
else {
    return item instanceof Ring || item instanceof Wand || item instanceof Trinket
            || item instanceof Plant.Seed || item instanceof Runestone;
}
```

### changeItem(Item item)
**签名**: `public static Item changeItem(Item item)`
**功能**: 转化物品为同类其他物品
**参数**:
- item: Item - 要转化的物品
**返回值**: Item - 转化后的物品
**实现逻辑**:
根据物品类型调用对应的转化方法：
- `changeStaff()` - 法师法杖
- `changeTippedDart()` - 沾毒飞镖
- `changeWeapon()` - 武器
- `changeScroll()` - 卷轴（普通↔异域）
- `changePotion()` - 药水（普通↔异域）
- `changeRing()` - 戒指
- `changeWand()` - 法杖
- `changeSeed()` - 种子
- `changeStone()` - 符石
- `changeArtifact()` - 神器
- `changeTrinket()` - 饰品

### changeWeapon(Weapon w)
**签名**: `private static Weapon changeWeapon(Weapon w)`
**功能**: 转化武器
**实现逻辑**:
```java
// 第231-271行
Weapon n;
Generator.Category c;
if (w instanceof MeleeWeapon) {
    c = Generator.wepTiers[((MeleeWeapon)w).tier - 1];
} else {
    c = Generator.misTiers[((MissileWeapon)w).tier - 1];
}

// 随机生成同阶武器
do {
    n = (Weapon)Generator.randomUsingDefaults(c);
} while (Challenges.isItemBlocked(n) || n.getClass() == w.getClass());

// 继承属性
n.level(0);
n.quantity(w.quantity());
int level = w.trueLevel();
if (level > 0) n.upgrade(level);
else if (level < 0) n.degrade(-level);

n.enchantment = w.enchantment;
n.cursed = w.cursed;
n.augment = w.augment;
// ...

return n;
```

### changeScroll(Scroll s) / changePotion(Potion p)
**签名**: `private static Scroll changeScroll(Scroll s)`
**功能**: 卷轴/药水在普通版和异域版之间转化
**实现逻辑**:
```java
// 第379-393行
if (s instanceof ExoticScroll) {
    return Reflection.newInstance(ExoticScroll.exoToReg.get(s.getClass()));
} else {
    return Reflection.newInstance(ExoticScroll.regToExo.get(s.getClass()));
}
```

## 11. 使用示例
```java
// 转化武器
ScrollOfTransmutation scroll = new ScrollOfTransmutation();
scroll.execute(hero, Scroll.AC_READ);
// 选择武器后转化为同阶其他武器

// 程序调用转化
Weapon newWeapon = ScrollOfTransmutation.changeItem(oldWeapon);
```

## 注意事项
1. 保持物品等级和属性
2. 卷轴/药水在普通↔异域间转化
3. 神器如果转化完了会变成戒指
4. 已鉴定价值50金币，能量价值10

## 最佳实践
1. 转化不需要的武器
2. 获取想要的附魔武器
3. 普通药水/卷轴转为异域版