# BrokenSeal 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/BrokenSeal.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items |
| 类类型 | public class |
| 继承关系 | extends Item |
| 代码行数 | 407 行 |

## 2. 类职责说明
BrokenSeal（破碎印记）是战士职业的专属物品，可以附魔到护甲上提供护盾效果。在战斗开始时生成护盾，护盾会在无敌人时逐渐消散。印记可以继承护甲符文，并且只能升级一次。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Item {
        +image int
        +cursedKnown boolean
        +levelKnown boolean
        +unique boolean
        +bones boolean
        +defaultAction String
    }
    
    class BrokenSeal {
        +image SEAL
        +cursedKnown true
        +levelKnown true
        +unique true
        +bones false
        +defaultAction AC_INFO
        -glyph Armor.Glyph
        +AC_AFFIX String
        +AC_INFO String
        +canTransferGlyph() boolean
        +getGlyph() Glyph
        +setGlyph() void
        +maxShield() int
        +glowing() Glowing
        +actions() ArrayList
        +execute() void
        +affixToArmor() void
        +name() String
        +info() String
        +isUpgradable() boolean
        +WarriorShield inner class
    }
    
    Item <|-- BrokenSeal
    BrokenSeal +-- WarriorShield
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_AFFIX | String | "AFFIX" | 附魔动作标识 |
| AC_INFO | String | "INFO_WINDOW" | 信息窗口动作标识 |
| GLYPH | String | "glyph" | Bundle 存储键 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | 初始化块 | 精灵图为 SEAL |
| cursedKnown | boolean | 初始化块 | 已知诅咒状态 true |
| levelKnown | boolean | 初始化块 | 已知等级 true |
| unique | boolean | 初始化块 | 唯一物品 true |
| bones | boolean | 初始化块 | 不可从骨头继承 false |
| defaultAction | String | 初始化块 | 默认动作 AC_INFO |
| glyph | Armor.Glyph | private | 附带的符文 |

## 7. 方法详解

### canTransferGlyph
**签名**: `public boolean canTransferGlyph()`
**功能**: 是否可以转移符文
**返回值**: boolean - 是否可转移
**实现逻辑**:
```java
// 第72-85行：检查天赋等级
if (glyph == null) return false;
if (Dungeon.hero.pointsInTalent(Talent.RUNIC_TRANSFERENCE) == 2) {
    return true;                                  // 满级天赋可转移任何符文
} else if (Dungeon.hero.pointsInTalent(Talent.RUNIC_TRANSFERENCE) == 1
    && (Arrays.asList(Armor.Glyph.common).contains(glyph.getClass())
        || Arrays.asList(Armor.Glyph.uncommon).contains(glyph.getClass()))) {
    return true;                                  // 1级天赋可转移普通和罕见符文
}
return false;
```

### maxShield
**签名**: `public int maxShield(int armTier, int armLvl)`
**功能**: 计算最大护盾值
**参数**:
- armTier: int - 护甲层级
- armLvl: int - 护甲等级
**返回值**: int - 最大护盾值
**实现逻辑**:
```java
// 第95-98行：计算护盾
// 5-15，基于装备层级和铁意志天赋
return 3 + 2 * armTier + Dungeon.hero.pointsInTalent(Talent.IRON_WILL);
```

### affixToArmor
**签名**: `public void affixToArmor(Armor armor, Item outgoing)`
**功能**: 将印记附魔到护甲
**参数**:
- armor: Armor - 目标护甲
- outgoing: Item - 原持有者（印记或护甲）
**实现逻辑**:
```java
// 第126-183行：复杂的附魔逻辑
// 检查护甲是否已知、是否诅咒
// 处理符文转移选择
// 最终附魔护甲
```

### isUpgradable
**签名**: `public boolean isUpgradable()`
**功能**: 是否可升级
**返回值**: boolean - 仅等级为0时可升级

## 内部类详解

### WarriorShield
**类型**: public static class extends ShieldBuff
**功能**: 战士护盾Buff，提供护盾和冷却机制
**实现逻辑**:
```java
// 第246-406行：护盾Buff实现
// 主要字段：
private Armor armor;
private int cooldown = 0;                         // 冷却时间
private float turnsSinceEnemies = 0;              // 无敌人回合数
private int initialShield = 0;                    // 初始护盾值
private static int COOLDOWN_START = 150;          // 冷却起始值

// 主要方法：
public void activate() {
    incShield(maxShield());                       // 生成护盾
    cooldown = Math.max(0, cooldown + COOLDOWN_START);
    turnsSinceEnemies = 0;
    initialShield = maxShield();
}

public boolean act() {
    if (cooldown > 0 && Regeneration.regenOn()) {
        cooldown--;                               // 冷却递减
    }
    
    // 无敌人时护盾消散
    if (shielding() > 0) {
        if (Dungeon.hero.visibleEnemies() == 0 && Dungeon.hero.buff(Combo.class) == null) {
            turnsSinceEnemies += HoldFast.buffDecayFactor(target);
            if (turnsSinceEnemies >= 5) {
                // 返还部分冷却
                if (cooldown > 0) {
                    float percentLeft = shielding() / (float) initialShield;
                    cooldown = Math.max(0, (int)(cooldown - COOLDOWN_START * (percentLeft / 2f)));
                }
                decShield(shielding());           // 护盾消散
            }
        } else {
            turnsSinceEnemies = 0;
        }
    }
    
    if (shielding() <= 0 && maxShield() <= 0 && cooldown == 0) {
        detach();                                 // 移除Buff
    }
    
    spend(TICK);
    return true;
}
```

## 11. 使用示例
```java
// 战士开始游戏时有破碎印记
BrokenSeal seal = new BrokenSeal();

// 附魔到护甲
seal.affixToArmor(armor, seal);

// 战斗开始时自动生成护盾
// 护盾值基于护甲层级和天赋
```

## 注意事项
1. 战士专属物品，其他职业无法使用
2. 只能附魔到一个护甲
3. 护盾有冷却机制
4. 可继承护甲符文（需要天赋）

## 最佳实践
1. 优先附魔高级护甲获得更多护盾
2. 点满铁意志天赋增加护盾值
3. 符文转移天赋可以让印记携带符文
4. 护盾在无敌人时会消散并返还部分冷却