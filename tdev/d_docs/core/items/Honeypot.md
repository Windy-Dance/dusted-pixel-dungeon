# Honeypot 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/Honeypot.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items |
| 类类型 | public class |
| 继承关系 | extends Item |
| 代码行数 | 257 行 |

## 2. 类职责说明
Honeypot（蜂蜜罐）是特殊物品，打碎后会生成一只蜜蜂。蜜蜂会保护蜂蜜罐的位置，攻击持有者或附近敌人。破碎的蜂蜜罐可以被拾取，携带时蜜蜂会跟随。蜜蜂可以用蜜糖治疗药水转化为盟友。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Item {
        +image int
        +defaultAction String
        +usesTargeting boolean
        +stackable boolean
        +isUpgradable() boolean
        +isIdentified() boolean
        +value() int
    }
    
    class Honeypot {
        +image HONEYPOT
        +defaultAction AC_THROW
        +usesTargeting true
        +stackable true
        -AC_SHATTER String
        +actions() ArrayList
        +execute() void
        +onThrow() void
        +shatter() Item
        +isUpgradable() boolean
        +isIdentified() boolean
        +value() int
        +ShatteredPot inner class
    }
    
    Item <|-- Honeypot
    Honeypot +-- ShatteredPot
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_SHATTER | String | "SHATTER" | 打碎动作标识 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | 初始化块 | 精灵图为 HONEYPOT |
| defaultAction | String | 初始化块 | 默认动作 AC_THROW |
| usesTargeting | boolean | 初始化块 | 使用目标选择 true |
| stackable | boolean | 初始化块 | 可堆叠 true |

## 7. 方法详解

### actions
**签名**: `public ArrayList<String> actions(Hero hero)`
**功能**: 获取可用动作列表
**返回值**: ArrayList\<String\> - 包含打碎动作

### execute
**签名**: `public void execute(final Hero hero, String action)`
**功能**: 执行动作
**参数**:
- hero: Hero - 英雄角色
- action: String - 动作名称
**实现逻辑**:
```java
// 第63-85行：执行打碎动作
super.execute(hero, action);

if (action.equals(AC_SHATTER)) {
    hero.sprite.zap(hero.pos);
    
    detach(hero.belongings.backpack);
    Catalog.countUse(getClass());
    
    Item item = shatter(hero, hero.pos);
    if (!item.collect()) {
        Dungeon.level.drop(item, hero.pos);
        if (item instanceof ShatteredPot) {
            ((ShatteredPot) item).dropPot(hero, hero.pos);
        }
    }
    
    hero.next();
}
```

### onThrow
**签名**: `protected void onThrow(int cell)`
**功能**: 投掷处理
**参数**:
- cell: int - 目标位置
**实现逻辑**:
```java
// 第88-95行：投掷处理
if (Dungeon.level.pit[cell]) {
    super.onThrow(cell);                          // 掉入深坑
} else {
    Catalog.countUse(getClass());
    Dungeon.level.drop(shatter(null, cell), cell); // 打碎并掉落
}
```

### shatter
**签名**: `public Item shatter(Char owner, int pos)`
**功能**: 打碎蜂蜜罐，生成蜜蜂
**参数**:
- owner: Char - 所有者
- pos: int - 位置
**返回值**: Item - 破碎的蜂蜜罐或自身（如果无法生成蜜蜂）
**实现逻辑**:
```java
// 第97-136行：打碎逻辑
if (Dungeon.level.heroFOV[pos]) {
    Sample.INSTANCE.play(Assets.Sounds.SHATTER);
    Splash.at(pos, 0xffd500, 5);
}

int newPos = pos;
if (Actor.findChar(pos) != null) {
    // 寻找相邻空位
    ArrayList<Integer> candidates = new ArrayList<>();
    for (int n : PathFinder.NEIGHBOURS4) {
        int c = pos + n;
        if (!Dungeon.level.solid[c] && Actor.findChar(c) == null) {
            candidates.add(c);
        }
    }
    newPos = candidates.size() > 0 ? Random.element(candidates) : -1;
}

if (newPos != -1) {
    // 生成蜜蜂
    Bee bee = new Bee();
    bee.spawn(Dungeon.scalingDepth());
    bee.setPotInfo(pos, owner);
    bee.HP = bee.HT;
    bee.pos = newPos;
    
    GameScene.add(bee);
    if (newPos != pos) Actor.add(new Pushing(bee, pos, newPos));
    
    bee.sprite.alpha(0);
    bee.sprite.parent.add(new AlphaTweener(bee.sprite, 1, 0.15f));
    
    Sample.INSTANCE.play(Assets.Sounds.BEE);
    return new ShatteredPot();
} else {
    return this;                                  // 无法生成蜜蜂，返回自身
}
```

### isUpgradable
**签名**: `public boolean isUpgradable()`
**功能**: 是否可升级
**返回值**: boolean - false

### isIdentified
**签名**: `public boolean isIdentified()`
**功能**: 是否已鉴定
**返回值**: boolean - true

### value
**签名**: `public int value()`
**功能**: 获取出售价格
**返回值**: int - 30 * 数量

## 内部类详解

### ShatteredPot
**类型**: public static class extends Item
**功能**: 破碎的蜂蜜罐，用于追踪蜜蜂
**实现逻辑**:
```java
// 第154-256行：破碎蜂蜜罐实现
// 主要方法：
public void pickupPot(Char holder) {
    for (Bee bee : findBees(holder.pos)) {
        updateBee(bee, -1, holder);               // 蜜蜂跟随持有者
    }
}

public void dropPot(Char holder, int dropPos) {
    for (Bee bee : findBees(holder)) {
        updateBee(bee, dropPos, null);            // 蜜蜂守护位置
    }
}

public void destroyPot(int potPos) {
    for (Bee bee : findBees(potPos)) {
        updateBee(bee, -1, null);                 // 蜜蜂失去目标
    }
}
```

## 11. 使用示例
```java
// 创建蜂蜜罐
Honeypot pot = new Honeypot();

// 投掷或打碎
pot.onThrow(targetPos);  // 投掷
pot.execute(hero, Honeypot.AC_SHATTER);  // 打碎

// 生成蜜蜂
// 蜜蜂保护蜂蜜罐位置或持有者
```

## 注意事项
1. 打碎后生成蜜蜂
2. 蜜蜂会攻击持有者附近的敌人
3. 破碎蜂蜜罐可以追踪蜜蜂
4. 蜜蜂可以用蜜糖治疗药水转化

## 最佳实践
1. 投掷蜂蜜罐到敌人附近
2. 蜜蜂是有效的战斗帮手
3. 拾取破碎蜂蜜罐可以让蜜蜂跟随
4. 可以与蜜糖治疗药水配合使用