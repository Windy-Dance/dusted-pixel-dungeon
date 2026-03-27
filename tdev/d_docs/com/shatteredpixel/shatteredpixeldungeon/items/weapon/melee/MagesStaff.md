# MagesStaff 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/MagesStaff.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee |
| 类类型 | public class |
| 继承关系 | extends MeleeWeapon |
| 代码行数 | 563 行 |

## 2. 类职责说明
MagesStaff（法师法杖）是法师职业的专属武器，可以镶嵌一根法杖来获得远程施法能力。法杖结合了近战和远程魔法攻击，并且会随等级提升增加法杖的最大充能。法师的战斗法师（Battlemage）子职业在近战时会获得额外效果。

## 4. 继承与协作关系
```mermaid
classDiagram
    class MeleeWeapon {
        <<abstract>>
        +int tier
        +int min(int lvl)
        +int max(int lvl)
    }
    class MagesStaff {
        -Wand wand
        +String AC_IMBUE
        +String AC_ZAP
        +float STAFF_SCALE_FACTOR
        +MagesStaff()
        +MagesStaff(Wand wand)
        +imbueWand(Wand wand, Char owner)
        +gainCharge(float amt)
        +applyWandChargeBuff(Char owner)
        +updateWand(boolean levelled)
        +proc(Char attacker, Char defender, int damage)
    }
    class StaffParticle {
        +float minSize
        +float maxSize
        +float sizeJitter
        +reset(float x, float y)
        +setSize(float min, float max)
        +setLifespan(float life)
    }
    MeleeWeapon <|-- MagesStaff
    MagesStaff +-- StaffParticle
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_IMBUE | String | "IMBUE" | 镶嵌法杖的动作标识 |
| AC_ZAP | String | "ZAP" | 施放法术的动作标识 |
| STAFF_SCALE_FACTOR | float | 0.75f | 法杖充能速度缩放因子 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| wand | Wand | private | 镶嵌的法杖 |
| image | int | 初始化块 | 物品图标 |
| hitSound | String | 初始化块 | 击中音效 |
| hitSoundPitch | float | 初始化块 | 音效音高 1.1f |
| tier | int | 初始化块 | 武器等级 1 |
| defaultAction | String | 初始化块 | 默认动作 AC_ZAP |
| usesTargeting | boolean | 初始化块 | 使用目标选择 true |
| unique | boolean | 初始化块 | 唯一物品 true |
| bones | boolean | 初始化块 | 不出现在遗骸 false |

## 7. 方法详解

### max
**签名**: `public int max(int lvl)`
**功能**: 计算近战最大伤害
**参数**: `lvl` - 武器等级
**返回值**: 最大伤害值
**实现逻辑**:
```java
return Math.round(3f*(tier+1)) +   // 6基础伤害
       lvl*(tier+1);               // 每级+2
```

### MagesStaff (构造函数)
**签名**: `public MagesStaff()` / `public MagesStaff(Wand wand)`
**功能**: 创建法师法杖
**参数**: `wand` - 初始镶嵌的法杖（可选）
**实现逻辑**:
```java
// 无参构造：wand = null
// 带参构造：设置法杖、识别、移除诅咒、更新等级、充满充能
```

### actions
**签名**: `public ArrayList<String> actions(Hero hero)`
**功能**: 返回可用动作列表
**返回值**: 动作列表
**实现逻辑**: 添加 IMBUE 和 ZAP（如果有法杖且有充能）动作。

### execute
**签名**: `public void execute(Hero hero, String action)`
**功能**: 执行指定动作
**参数**: 
- `hero` - 英雄
- `action` - 动作标识
**实现逻辑**:
```java
if (action.equals(AC_IMBUE)) {
    // 打开物品选择界面选择法杖
    GameScene.selectItem(itemSelector);
} else if (action.equals(AC_ZAP)){
    // 如果有法杖，委托给法杖执行
    wand.execute(hero, AC_ZAP);
}
```

### proc
**签名**: `public int proc(Char attacker, Char defender, int damage)`
**功能**: 处理攻击时的特殊效果
**参数**: 
- `attacker` - 攻击者
- `defender` - 防御者
- `damage` - 原始伤害
**返回值**: 处理后的伤害
**实现逻辑**:
```java
// 神秘充能天赋：为神器充能
if (attacker instanceof Hero && hero.hasTalent(Talent.MYSTICAL_CHARGE)){
    ArtifactRecharge.chargeArtifacts(hero, ...);
}

// 强化打击天赋：增加伤害
Talent.EmpoweredStrikeTracker empoweredStrike = attacker.buff(...);
if (empoweredStrike != null){
    damage = Math.round(damage * (1f + ...));
}

// 战斗法师子职业：
if (attacker instanceof Hero && subClass == HeroSubClass.BATTLEMAGE) {
    if (wand.curCharges < wand.maxCharges) wand.partialCharge += 0.5f;
    ScrollOfRecharging.charge((Hero)attacker);
    wand.onHit(this, attacker, defender, damage);
}
```

### imbueWand
**签名**: `public Item imbueWand(Wand wand, Char owner)`
**功能**: 将法杖镶嵌到法师法杖中
**参数**: 
- `wand` - 要镶嵌的法杖
- `owner` - 拥有者
**返回值**: 返回自身
**实现逻辑**:
```java
// 保存旧法杖充能
int oldStaffcharges = this.wand != null ? this.wand.curCharges : 0;

// 法杖保存天赋：返还旧法杖
if (owner == Dungeon.hero && hero.hasTalent(Talent.WAND_PRESERVATION)){
    // 将旧法杖返还到背包或掉落
}

// 设置新法杖
this.wand = null;
wand.resinBonus = 0;
wand.updateLevel();

// 同步等级
int targetLevel = Math.max(this.trueLevel(), wand.trueLevel());
if (wand.trueLevel() >= this.trueLevel() && this.trueLevel() > 0) targetLevel++;

level(targetLevel);
this.wand = wand;
wand.levelKnown = wand.curChargeKnown = true;
updateWand(false);
wand.curCharges = Math.min(wand.maxCharges, wand.curCharges+oldStaffcharges);

// 处理诅咒
if (wand.cursed && (!this.cursed || !this.hasCurseEnchant())){
    equipCursed(Dungeon.hero);
    this.cursed = this.cursedKnown = true;
    enchant(Enchantment.randomCurse());
}
```

### updateWand
**签名**: `public void updateWand(boolean levelled)`
**功能**: 更新法杖状态
**参数**: `levelled` - 是否因升级调用
**实现逻辑**:
```java
if (wand != null) {
    int curCharges = wand.curCharges;
    wand.level(level());
    // 法杖最大充能+1，上限10
    wand.maxCharges = Math.min(wand.maxCharges + 1, 10);
    wand.curCharges = Math.min(curCharges + (levelled ? 1 : 0), wand.maxCharges);
    updateQuickslot();
}
```

## 内部类

### StaffParticle
**类型**: public class extends PixelParticle
**功能**: 法杖粒子效果
**字段**:
| 字段 | 类型 | 说明 |
|------|------|------|
| minSize | float | 最小尺寸 |
| maxSize | float | 最大尺寸 |
| sizeJitter | float | 尺寸抖动 |

**方法**:
| 方法 | 说明 |
|------|------|
| `reset(float x, float y)` | 重置粒子，调用法杖的staffFx |
| `setSize(float min, float max)` | 设置尺寸范围 |
| `setLifespan(float life)` | 设置生命周期 |
| `shuffleXY(float amt)` | 随机偏移位置 |
| `radiateXY(float amt)` | 放射状偏移 |

### ItemSelector (匿名类)
**功能**: 选择法杖的界面回调
**方法**:
- `textPrompt()` - 返回提示文本
- `preferredBag()` - 返回魔法 holster
- `itemSelectable()` - 只能选择法杖
- `onSelect()` - 选择后的处理

## 11. 使用示例
```java
// 创建法师法杖
MagesStaff staff = new MagesStaff();
// 镶嵌一根法杖
staff.imbueWand(new WandOfFireball(), hero);
// 法师装备后可以：
// 1. 近战攻击
// 2. 施放法术（使用法杖充能）
// 3. 升级后法杖充能上限增加
```

## 注意事项
- 法师专属武器，其他职业无法有效使用
- 镶嵌法杖后可以远程施法
- 升级会增加法杖最大充能（上限10）
- 战斗法师子职业近战攻击会回复法杖充能
- 法杖等级与法杖等级同步

## 最佳实践
- 选择适合的法杖进行镶嵌
- 利用战斗法师的近战充能特性
- 使用法杖保存天赋保留旧法杖
- 升级法杖以获得更多充能