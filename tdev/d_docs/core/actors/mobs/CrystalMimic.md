# CrystalMimic 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/CrystalMimic.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.actors.mobs |
| 类类型 | public class |
| 继承关系 | extends Mimic |
| 代码行数 | 206 行 |

## 2. 类职责说明
CrystalMimic（水晶宝箱怪）是 Mimic（宝箱怪）的变种，隐藏在水晶宝箱中。被触发后会逃跑而不是直接攻击，攻击时传送敌人而不是造成额外伤害。偷窃玩家的物品后逃跑，如果逃脱距离足够远会消失并带走物品。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Mob {
        +spriteClass Class
        +alignment Alignment
        +state State
        +attackProc() int
        +damageRoll() int
        +name() String
        +description() String
    }
    
    class Mimic {
        +items ArrayList~Item~
        +generatePrize() void
        +stopHiding() void
    }
    
    class CrystalMimic {
        +spriteClass MimicSprite.Crystal
        +FLEEING Fleeing
        +name() String
        +description() String
        +damageRoll() int
        +stopHiding() void
        +attackProc() int
        +steal() void
        +generatePrize() void
        +Fleeing inner class
    }
    
    Mob <|-- Mimic
    Mimic <|-- CrystalMimic
    CrystalMimic +-- Fleeing
```

## 静态常量表
无静态常量。

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| spriteClass | Class | 初始化块 | 精灵类为 MimicSprite.Crystal |
| FLEEING | Fleeing | 初始化块 | 自定义逃跑状态 |

## 7. 方法详解

### name
**签名**: `public String name()`
**功能**: 获取名称（隐藏时显示为水晶宝箱）
**返回值**: String - 名称
**实现逻辑**:
```java
// 第59-65行：根据状态返回名称
if (alignment == Alignment.NEUTRAL) {                 // 如果是中立状态（隐藏中）
    return Messages.get(Heap.class, "crystal_chest"); // 显示为水晶宝箱
} else {
    return super.name();                              // 显示真实名称
}
```

### description
**签名**: `public String description()`
**功能**: 获取描述（隐藏时显示宝箱内容提示）
**返回值**: String - 描述文本
**实现逻辑**:
```java
// 第68-93行：根据内容生成描述
if (alignment == Alignment.NEUTRAL) {                 // 如果是隐藏状态
    String desc = null;
    for (Item i : items) {                            // 检查物品类型
        if (i instanceof Artifact) {
            desc = Messages.get(Heap.class, "crystal_chest_desc", Messages.get(Heap.class, "artifact"));
            break;
        } else if (i instanceof Ring) {
            desc = Messages.get(Heap.class, "crystal_chest_desc", Messages.get(Heap.class, "ring"));
            break;
        } else if (i instanceof Wand) {
            desc = Messages.get(Heap.class, "crystal_chest_desc", Messages.get(Heap.class, "wand"));
            break;
        }
    }
    if (desc == null) {
        desc = Messages.get(Heap.class, "locked_chest_desc");
    }
    if (!MimicTooth.stealthyMimics()) {               // 如果饰品未启用隐身
        desc += "\n\n" + Messages.get(this, "hidden_hint"); // 添加提示
    }
    return desc;
} else {
    return super.description();
}
```

### damageRoll
**签名**: `public int damageRoll()`
**功能**: 计算伤害值（不造成额外伤害）
**返回值**: int - 伤害值
**实现逻辑**:
```java
// 第96-106行：计算伤害（隐藏时不改变阵营）
if (alignment == Alignment.NEUTRAL) {
    alignment = Alignment.ENEMY;                      // 临时设为敌对
    int dmg = super.damageRoll();                     // 计算伤害
    alignment = Alignment.NEUTRAL;                    // 恢复中立
    return dmg;
} else {
    return super.damageRoll();
}
```

### stopHiding
**签名**: `public void stopHiding()`
**功能**: 停止隐藏并开始逃跑
**实现逻辑**:
```java
// 第108-124行：停止隐藏，获得加速
state = FLEEING;                                      // 进入逃跑状态
if (sprite != null) sprite.idle();                    // 重置动画

// 根据是否攻击获得不同加速时间
if (alignment == Alignment.NEUTRAL) {
    Buff.affect(this, Haste.class, 2f);               // 攻击触发：2回合加速
} else {
    Buff.affect(this, Haste.class, 1f);               // 其他情况：1回合加速
}

if (Actor.chars().contains(this) && Dungeon.level.heroFOV[pos]) {
    enemy = Dungeon.hero;
    target = Dungeon.hero.pos;
    GLog.w(Messages.get(this, "reveal"));             // 显示警告消息
    CellEmitter.get(pos).burst(Speck.factory(Speck.STAR), 10); // 显示星光特效
    Sample.INSTANCE.play(Assets.Sounds.MIMIC, 1, 1.25f); // 播放音效
}
```

### attackProc
**签名**: `public int attackProc(Char enemy, int damage)`
**功能**: 攻击时偷窃或传送敌人
**参数**:
- enemy: Char - 被攻击的目标
- damage: int - 基础伤害值
**返回值**: int - 最终伤害值
**实现逻辑**:
```java
// 第127-146行：攻击效果
if (alignment == Alignment.NEUTRAL && enemy == Dungeon.hero) {
    steal(Dungeon.hero);                              // 隐藏状态攻击玩家时偷窃
} else {
    // 传送到相邻空格
    ArrayList<Integer> candidates = new ArrayList<>();
    for (int i : PathFinder.NEIGHBOURS8) {
        if (Dungeon.level.passable[pos + i] && Actor.findChar(pos + i) == null) {
            candidates.add(pos + i);
        }
    }
    if (!candidates.isEmpty()) {
        ScrollOfTeleportation.appear(enemy, Random.element(candidates)); // 传送敌人
    }
    if (alignment == Alignment.ENEMY) state = FLEEING; // 敌对状态攻击后逃跑
}
return super.attackProc(enemy, damage);
```

### steal
**签名**: `protected void steal(Hero hero)`
**功能**: 偷窃玩家的物品
**参数**:
- hero: Hero - 被偷窃的玩家
**实现逻辑**:
```java
// 第148-174行：偷窃物品
int tries = 10;
Item item;
do {
    item = hero.belongings.randomUnequipped();         // 随机选择未装备物品
} while (tries-- > 0 && (item == null || item.unique || item.level() > 0)); // 排除唯一物品和升级物品

if (item != null && !item.unique && item.level() < 1) {
    GLog.w(Messages.get(this, "ate", item.name()));   // 显示偷窃消息
    if (!item.stackable) {
        Dungeon.quickslot.convertToPlaceholder(item);  // 转换快捷槽占位符
    }
    item.updateQuickslot();

    if (item instanceof Honeypot) {
        items.add(((Honeypot)item).shatter(this, this.pos)); // 打碎蜂蜜罐
        item.detach(hero.belongings.backpack);
    } else {
        items.add(item.detach(hero.belongings.backpack));    // 添加到物品列表
        if (item instanceof Honeypot.ShatteredPot) {
            ((Honeypot.ShatteredPot)item).pickupPot(this);
        }
    }
}
```

### generatePrize
**签名**: `protected void generatePrize(boolean useDecks)`
**功能**: 生成奖品（确保物品不诅咒）
**参数**:
- useDecks: boolean - 是否使用卡牌系统
**实现逻辑**:
```java
// 第177-183行：确保物品不诅咒
for (Item i : items) {
    i.cursed = false;                                 // 取消诅咒
    i.cursedKnown = true;                             // 标记为已知
}
```

## 内部类详解

### Fleeing
**类型**: private class extends Mob.Fleeing
**功能**: 自定义逃跑状态
**实现逻辑**:
```java
// 第185-204行：逃跑逻辑
@Override
protected void escaped() {                            // 成功逃脱
    if (!Dungeon.level.heroFOV[pos] && Dungeon.level.distance(Dungeon.hero.pos, pos) >= 6) {
        GLog.n(Messages.get(CrystalMimic.class, "escaped")); // 显示逃跑消息
        destroy();                                    // 销毁怪物
        sprite.killAndErase();                        // 移除精灵
    } else {
        state = WANDERING;                            // 否则继续游荡
    }
}

@Override
protected void nowhereToRun() {                       // 无处可逃
    super.nowhereToRun();
    if (state == HUNTING) {
        spend(-TICK);                                 // 立即行动（更快）
    }
}
```

## 11. 使用示例
```java
// 创建水晶宝箱怪
CrystalMimic mimic = new CrystalMimic();
mimic.items = new ArrayList<>();
mimic.items.add(artifact);
mimic.pos = chestPos;
mimic.alignment = Alignment.NEUTRAL;

// 玩家打开宝箱时触发
mimic.stopHiding();  // 开始逃跑
// 攻击玩家时偷窃物品
// 逃脱后带走物品
```

## 注意事项
1. 不像普通宝箱怪，水晶宝箱怪会逃跑而不是主动攻击
2. 偷窃物品后如果逃脱会带走物品
3. 攻击时会传送敌人到相邻格子
4. 获得加速效果使其更难被追上

## 最佳实践
1. 追击水晶宝箱怪，不要让它逃脱
2. 准备好远程武器或瞬移能力
3. 重要物品不要放在背包中（装备在身上不会被偷）
4. 使用模仿之牙饰品可以让宝箱怪不显示提示