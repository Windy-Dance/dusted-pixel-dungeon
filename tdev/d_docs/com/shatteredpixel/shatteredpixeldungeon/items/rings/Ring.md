# Ring 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/rings/Ring.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.rings |
| 类类型 | class |
| 继承关系 | extends KindofMisc |
| 代码行数 | 459 |

## 2. 类职责说明
Ring 是所有戒指物品的抽象基类，定义了戒指的通用行为：装备时提供Buff、宝石颜色识别、鉴定状态管理等。戒指通过佩戴提供持续的被动效果，每个具体戒指类型提供不同的Buff。戒指需要装备1个等级后才能自动鉴定。

## 4. 继承与协作关系
```mermaid
classDiagram
    class KindofMisc {
        <<abstract>>
        +equip(Hero): boolean
        +doUnequip(Hero, boolean, boolean): boolean
    }
    
    class Ring {
        #buff: Buff
        #buffClass: Class~RingBuff~
        -gems: LinkedHashMap~String,Integer~
        -handler: ItemStatusHandler~Ring~
        -gem: String
        -levelsToID: float
        -anonymous: boolean
        +activate(Char): void
        +doUnequip(Hero, boolean, boolean): boolean
        +isKnown(): boolean
        +setKnown(): void
        +name(): String
        +desc(): String
        +info(): String
        +statsInfo(): String
        +upgrade(): Item
        +isIdentified(): boolean
        +identify(boolean): Item
        +random(): Item
        +onHeroGainExp(float, Hero): void
        +buffedLvl(): int
        +getBonus(Char, Class): int
        +getBuffedBonus(Char, Class): int
        +soloBonus(): int
        +combinedBonus(Hero): int
        #buff(): RingBuff
    }
    
    class RingBuff {
        +act(): boolean
        +level(): int
        +buffedLvl(): int
    }
    
    KindofMisc <|-- Ring
    Ring +-- RingBuff
    
    note for Ring "所有戒指的基类\n通过装备提供Buff\n需要1级经验鉴定\n宝石颜色随机分配"
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| gems | LinkedHashMap<String, Integer> | 12种宝石映射 | 宝石名称到精灵图索引的映射 |
| LEVELS_TO_ID | String | "levels_to_ID" | 存档键名 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| buff | Buff | protected | 当前激活的Buff |
| buffClass | Class<? extends RingBuff> | protected | Buff类型类 |
| handler | ItemStatusHandler<Ring> | private static | 物品状态处理器 |
| gem | String | private | 宝石颜色名称 |
| levelsToID | float | private | 鉴定所需剩余经验 |
| anonymous | boolean | protected | 是否为匿名戒指 |

## 7. 方法详解

### activate(Char ch)
**签名**: `public void activate(Char ch)`
**功能**: 激活戒指效果，附加Buff到角色
**实现逻辑**:
```java
// 第127-134行
if (buff != null) {
    buff.detach();
    buff = null;
}
buff = buff();  // 创建新的Buff
buff.attachTo(ch);  // 附加到角色
```

### doUnequip(Hero hero, boolean collect, boolean single)
**签名**: `@Override public boolean doUnequip(Hero hero, boolean collect, boolean single)`
**功能**: 卸下戒指，移除Buff
**实现逻辑**:
```java
// 第137-152行
if (super.doUnequip(hero, collect, single)) {
    if (buff != null) {
        buff.detach();
        buff = null;
    }
    return true;
} else {
    return false;
}
```

### onHeroGainExp(float levelPercent, Hero hero)
**签名**: `public void onHeroGainExp(float levelPercent, Hero hero)`
**功能**: 英雄获得经验时处理鉴定进度
**实现逻辑**:
```java
// 第329-346行
if (isIdentified() || !isEquipped(hero)) return;
levelPercent *= Talent.itemIDSpeedFactor(hero, this);
levelsToID -= levelPercent;

if (levelsToID <= 0) {
    if (ShardOfOblivion.passiveIDDisabled()) {
        // 遗物效果：设置为准备鉴定
        if (levelsToID > -1) {
            GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready"), name());
        }
        setIDReady();
    } else {
        // 正常鉴定
        identify();
        GLog.p(Messages.get(Ring.class, "identify"));
        Badges.validateItemLevelAquired(this);
    }
}
```

### getBonus(Char target, Class<? extends RingBuff> type)
**签名**: `public static int getBonus(Char target, Class<? extends RingBuff> type)`
**功能**: 获取角色身上指定类型戒指的总加成
**参数**:
- target: Char - 目标角色
- type: Class<? extends RingBuff> - Buff类型
**返回值**: int - 总加成值
**实现逻辑**:
```java
// 第357-371行
if (target.buff(MagicImmune.class) != null) return 0;
int bonus = 0;
for (RingBuff buff : target.buffs(type)) {
    bonus += buff.level();
}
// 处理灵魂形态的虚拟戒指
SpiritForm.SpiritFormBuff spiritForm = target.buff(SpiritForm.SpiritFormBuff.class);
if (bonus == 0 && spiritForm != null && spiritForm.ring() != null
        && spiritForm.ring().buffClass == type) {
    bonus += spiritForm.ring().soloBonus();
}
return bonus;
```

### soloBonus()
**签名**: `public int soloBonus()`
**功能**: 计算单个戒指的加成值
**返回值**: int - 加成值
**实现逻辑**:
```java
// 第389-395行
if (cursed) {
    return Math.min(0, Ring.this.level() - 2);
} else {
    return Ring.this.level() + 1;
}
```
- 诅咒戒指：等级-2（最小为负数）
- 普通戒指：等级+1

## 11. 使用示例

### 创建自定义戒指
```java
public class RingOfExample extends Ring {
    {
        icon = ItemSpriteSheet.Icons.RING_EXAMPLE;
        buffClass = ExampleBuff.class;
    }
    
    @Override
    protected RingBuff buff() {
        return new ExampleBuff();
    }
    
    public static float exampleBonus(Char target) {
        return getBuffedBonus(target, ExampleBuff.class);
    }
    
    public class ExampleBuff extends RingBuff {
    }
}
```

### 获取戒指加成
```java
// 获取角色的闪避加成
float evasion = RingOfEvasion.evasionMultiplier(hero);

// 获取角色的准确加成
float accuracy = RingOfAccuracy.accuracyMultiplier(hero);
```

## 注意事项

1. **鉴定机制**: 装备后需要1级经验才能自动鉴定

2. **诅咒效果**: 诅咒戒指的加成为负值

3. **宝石颜色**: 每局游戏宝石与戒指类型随机对应

4. **魔法免疫**: 魔法免疫状态下戒指效果无效

5. **双戒指**: 可以装备两个相同类型的戒指，效果叠加

## 最佳实践

1. 创建新戒指时继承 Ring 并实现 buff() 方法

2. 提供 static 方法获取加成值

3. 重写 statsInfo() 显示属性信息