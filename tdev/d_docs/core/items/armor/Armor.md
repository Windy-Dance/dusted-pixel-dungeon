# Armor 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/Armor.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.armor |
| **类类型** | class（非抽象） |
| **继承关系** | extends EquipableItem |
| **代码行数** | 919 |

---

## 类职责

Armor 是游戏中所有**护甲**的基类。它是护甲系统的核心，处理：

1. **防御机制**：计算伤害减免(DR)范围
2. **护甲阶位**：阶位(tier)决定基础防御力和力量需求
3. **铭文系统**：护甲可附加 Glyph（铭文）获得特殊效果
4. **改造系统**：Augment 提供闪避/防御的权衡
5. **战士印系统**：BrokenSeal 提供额外护盾
6. **鉴定机制**：通过使用次数自动鉴定

**设计模式**：
- **模板方法模式**：`proc()` 定义伤害处理框架，Glyph 提供扩展点
- **策略模式**：不同 Glyph 实现不同的防御策略
- **装饰器模式**：Glyph、Augment、Seal 都是对护甲功能的"装饰"

---

## 4. 继承与协作关系

```mermaid
classDiagram
    class EquipableItem {
        <<abstract>>
        +AC_EQUIP: String
        +AC_UNEQUIP: String
        +doEquip(Hero) boolean
        +doUnequip(Hero, boolean, boolean) boolean
        +activate(Char) void
        #timeToEquip(Hero) float
    }
    
    class Armor {
        +tier: int
        +augment: Augment
        +glyph: Glyph
        +glyphHardened: boolean
        +curseInfusionBonus: boolean
        +masteryPotionBonus: boolean
        #seal: BrokenSeal
        +DRMax() int
        +DRMin() int
        +DRMax(int) int
        +DRMin(int) int
        +evasionFactor(Char, float) float
        +speedFactor(Char, float) float
        +proc(Char, Char, int) int
        +inscribe(Glyph) Armor
        +hasGlyph(Class, Char) boolean
        +STRReq() int
        +STRReq(int) int
        +affixSeal(BrokenSeal) void
        +detachSeal() BrokenSeal
    }
    
    class Augment {
        <<enumeration>>
        EVASION
        DEFENSE
        NONE
        +evasionFactor(int) int
        +defenseFactor(int) int
    }
    
    class Glyph {
        <<abstract>>
        +common: Class[]
        +uncommon: Class[]
        +rare: Class[]
        +curses: Class[]
        +proc(Armor, Char, Char, int) int
        +name() String
        +desc() String
        +curse() boolean
        +glowing() ItemSprite.Glowing
        +random() Glyph
        +randomCurse() Glyph
    }
    
    class BrokenSeal {
        +maxShield(int, int) int
        +affixToArmor(Armor) void
        +getGlyph() Glyph
    }
    
    class ClothArmor
    class LeatherArmor
    class MailArmor
    class ScaleArmor
    class PlateArmor
    class ClassArmor
    
    EquipableItem <|-- Armor
    Armor *-- Augment : 包含
    Armor *-- Glyph : 包含
    Armor *-- BrokenSeal : 可选包含
    Glyph <|-- Obfuscation
    Glyph <|-- Swiftness
    Glyph <|-- Viscosity
    Glyph <|-- AntiEntropy
    Glyph <|-- 诅咒铭文...
    Armor <|-- ClothArmor
    Armor <|-- LeatherArmor
    Armor <|-- MailArmor
    Armor <|-- ScaleArmor
    Armor <|-- PlateArmor
    Armor <|-- ClassArmor
```

---

## 静态常量

### 动作常量

| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `AC_DETACH` | String | "DETACH" | 分离战士印的动作 |

### 鉴定常量

| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `USES_TO_ID` | int | 10 | 鉴定所需的使用次数 |

### Bundle 键常量

| 字段名 | 值 | 说明 |
|--------|-----|------|
| `USES_LEFT_TO_ID` | "uses_left_to_id" | 剩余鉴定次数的存储键 |
| `AVAILABLE_USES` | "available_uses" | 可用鉴定次数的存储键 |
| `GLYPH` | "glyph" | 铭文的存储键 |
| `GLYPH_HARDENED` | "glyph_hardened" | 铭文硬化状态的存储键 |
| `CURSE_INFUSION_BONUS` | "curse_infusion_bonus" | 诅咒注入加成的存储键 |
| `MASTERY_POTION_BONUS` | "mastery_potion_bonus" | 精通药水加成的存储键 |
| `SEAL` | "seal" | 战士印的存储键 |
| `AUGMENT` | "augment" | 改造类型的存储键 |

### 调试用静态字段

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `testingNoArmDefSkill` | boolean | 测试用：跳过护甲对闪避的影响 |

---

## 内部枚举：Augment

护甲改造类型，提供闪避与防御的权衡。

```java
public enum Augment {
    EVASION (2f , -1f),  // 闪避型：+闪避 -防御
    DEFENSE (-2f, 1f),   // 防御型：-闪避 +防御
    NONE    (0f ,  0f);  // 无改造
    
    private float evasionFactor;
    private float defenceFactor;
    
    Augment(float eva, float df){
        evasionFactor = eva;
        defenceFactor = df;
    }
    
    public int evasionFactor(int level){
        return Math.round((2 + level) * evasionFactor);
    }
    
    public int defenseFactor(int level){
        return Math.round((2 + level) * defenceFactor);
    }
}
```

**计算公式**：
- 闪避加成 = `(2 + level) * 闪避系数`
- 防御加成 = `(2 + level) * 防御系数`

**示例**：
- +0 闪避型护甲：闪避 +2，防御 -1
- +3 防御型护甲：闪避 -10，防御 +5

---

## 内部抽象类：Glyph

护甲铭文基类，提供特殊效果。

### 铭文稀有度分类

| 稀有度 | 铭文 | 出现概率 |
|--------|------|----------|
| **普通** | Obfuscation, Swiftness, Viscosity, Potential | 各 12.5% |
| **稀有** | Brimstone, Stone, Entanglement, Repulsion, Camouflage, Flow | 各 6.67% |
| **史诗** | Affection, AntiMagic, Thorns | 各 3.33% |

### 诅咒铭文

| 诅咒名 | 效果 |
|--------|------|
| AntiEntropy | 被击时对周围造成冰冻效果 |
| Corrosion | 被击时产生腐蚀酸液 |
| Displacement | 被击时随机传送 |
| Metabolism | 被击时消耗饥饿度回血 |
| Multiplicity | 被击时可能分裂幻象 |
| Stench | 被击时产生毒气 |
| Overgrowth | 被击时产生藤蔓 |
| Bulk | 增大体型，无法穿过狭窄通道 |

---

## 实例字段

### 核心属性

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `tier` | int | 构造传入 | 护甲阶位(1-5) |
| `augment` | Augment | NONE | 改造类型 |
| `glyph` | Glyph | null | 附着的铭文 |
| `glyphHardened` | boolean | false | 铭文是否已硬化 |
| `seal` | BrokenSeal | null | 附着的战士印 |

### 加成标记

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `curseInfusionBonus` | boolean | false | 是否有诅咒注入加成 |
| `masteryPotionBonus` | boolean | false | 是否有精通药水加成（-2力量需求） |

### 鉴定追踪

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `usesLeftToID` | float | 10 | 剩余多少次使用可鉴定 |
| `availableUsesToID` | float | 5 | 当前可累积的鉴定次数 |

---

## 7. 方法详解

### 构造方法

```java
public Armor( int tier ) {
    this.tier = tier;
}
```

**说明**：护甲必须指定阶位。阶位决定：
- 基础防御力
- 力量需求
- 护盾量上限（战士印）

---

### DRMax() / DRMax(int lvl)

```java
public final int DRMax(){
    return DRMax(buffedLvl());
}

public int DRMax(int lvl){
    // 挑战模式：无护甲
    if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
        return 1 + tier + lvl + augment.defenseFactor(lvl);
    }

    int max = tier * (2 + lvl) + augment.defenseFactor(lvl);
    if (lvl > max){
        // 过度升级时，收益减半
        return ((lvl - max)+1)/2;
    } else {
        return max;
    }
}
```

**作用**：计算护甲的最大伤害减免值。

**计算公式**：
- 正常：`tier * (2 + level) + 改造防御加成`
- 过度升级（level > 基础最大值）：`(level - max + 1) / 2`

**示例**：
| 阶位 | +0 | +1 | +5 | +10 |
|------|-----|-----|-----|------|
| 1阶 | 2 | 3 | 7 | 5(溢出) |
| 3阶 | 6 | 9 | 21 | 33 |
| 5阶 | 10 | 15 | 35 | 60 |

---

### DRMin() / DRMin(int lvl)

```java
public final int DRMin(){
    return DRMin(buffedLvl());
}

public int DRMin(int lvl){
    if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
        return 0;
    }

    int max = DRMax(lvl);
    if (lvl >= max){
        return (lvl - max);
    } else {
        return lvl;
    }
}
```

**作用**：计算护甲的最小伤害减免值。

**计算逻辑**：
- 正常情况：最小减免 = level
- 过度升级：最小减免 = level - max

**伤害减免范围**：`[DRMin, DRMax]`，实际减免值随机在此范围内。

---

### evasionFactor(Char owner, float evasion)

```java
public float evasionFactor( Char owner, float evasion ){
    if (testingNoArmDefSkill) return evasion;
    
    // 石之铭文：闪避归零
    if (hasGlyph(Stone.class, owner) && !Stone.testingEvasion()){
        return 0;
    }
    
    if (owner instanceof Hero){
        // 力量不足：降低闪避
        int aEnc = STRReq() - ((Hero) owner).STR();
        if (aEnc > 0) evasion /= Math.pow(1.5, aEnc);
        
        // 势能buff：增加闪避
        Momentum momentum = owner.buff(Momentum.class);
        if (momentum != null){
            evasion += momentum.evasionBonus(((Hero) owner).lvl, Math.max(0, -aEnc));
        }
    }
    
    // 加上改造闪避加成
    return evasion + augment.evasionFactor(buffedLvl());
}
```

**作用**：计算装备此护甲后的闪避值。

**影响因素**：
1. 石之铭文 → 闪避为0
2. 力量不足 → 每差1点力量，闪避 ÷ 1.5
3. 势能Buff → 增加闪避
4. 改造加成 → 闪避型增加，防御型减少

---

### speedFactor(Char owner, float speed)

```java
public float speedFactor( Char owner, float speed ){
    if (owner instanceof Hero) {
        int aEnc = STRReq() - ((Hero) owner).STR();
        if (aEnc > 0) speed /= Math.pow(1.2, aEnc);
    }
    return speed;
}
```

**作用**：计算装备此护甲后的移动速度。

**影响**：力量不足时，每差1点力量，速度 ÷ 1.2

---

### level()

```java
@Override
public int level() {
    int level = super.level();
    // 诅咒注入提供额外等级加成
    if (curseInfusionBonus) level += 1 + level/6;
    return level;
}
```

**作用**：返回考虑诅咒注入加成的等级。

**诅咒注入加成**：`+1 + level/6`（向下取整）

---

### upgrade(boolean inscribe)

```java
public Item upgrade( boolean inscribe ) {
    if (inscribe){
        // 强制附加随机铭文
        if (glyph == null){
            inscribe( Glyph.random() );
        }
    } else if (glyph != null) {
        if (glyphHardened) {
            // 硬化铭文：+6/7/8/9/10 升级时有 10/20/40/80/100% 几率失去硬化
            if (level() >= 6 && Random.Float(10) < Math.pow(2, level()-6)){
                glyphHardened = false;
            }
        } else if (hasCurseGlyph()){
            // 诅咒铭文：33% 几率移除
            if (Random.Int(3) == 0) inscribe(null);
        } else {
            // 普通铭文：+4/5/6/7/8 升级时有 10/20/40/80/100% 几率失去
            int lossChanceStart = 4;
            // 符文转移天赋可推迟失去几率
            if (Dungeon.hero != null && Dungeon.hero.heroClass != HeroClass.WARRIOR 
                    && Dungeon.hero.hasTalent(Talent.RUNIC_TRANSFERENCE)){
                lossChanceStart += 1 + Dungeon.hero.pointsInTalent(Talent.RUNIC_TRANSFERENCE);
            }
            if (level() >= lossChanceStart && Random.Float(10) < Math.pow(2, level()-4)) {
                inscribe(null);
            }
        }
    }
    
    cursed = false;  // 升级移除诅咒状态
    
    // 同步升级战士印
    if (seal != null && seal.level() == 0)
        seal.upgrade();

    return super.upgrade();
}
```

**作用**：升级护甲，处理铭文和诅咒状态。

**升级对铭文的影响**：

| 情况 | 升级等级 | 失去几率 |
|------|----------|----------|
| 普通铭文 | +4 | 10% |
| 普通铭文 | +5 | 20% |
| 普通铭文 | +6 | 40% |
| 普通铭文 | +7 | 80% |
| 普通铭文 | +8+ | 100% |
| 诅咒铭文 | 任意 | 33% |
| 硬化铭文 | +6+ | 同普通铭文 |

---

### proc(Char attacker, Char defender, int damage)

```java
public int proc( Char attacker, Char defender, int damage ) {
    // 魔法免疫时不触发铭文
    if (defender.buff(MagicImmune.class) == null) {
        Glyph trinityGlyph = null;
        // 三位一体天赋：额外铭文效果
        if (Dungeon.hero.buff(BodyForm.BodyFormBuff.class) != null
                && (defender == Dungeon.hero || defender instanceof PrismaticImage 
                    || defender instanceof ShadowClone.ShadowAlly)){
            trinityGlyph = Dungeon.hero.buff(BodyForm.BodyFormBuff.class).glyph();
            if (glyph != null && trinityGlyph != null 
                    && trinityGlyph.getClass() == glyph.getClass()){
                trinityGlyph = null;  // 相同铭文不重复触发
            }
        }

        // 圣佑Buff：圣骑士子职业特殊处理
        if (defender instanceof Hero && isEquipped((Hero) defender)
                && defender.buff(HolyWard.HolyArmBuff.class) != null){
            if (glyph != null &&
                    (((Hero) defender).subClass == HeroSubClass.PALADIN || hasCurseGlyph())){
                damage = glyph.proc( this, attacker, defender, damage );
            }
            if (trinityGlyph != null){
                damage = trinityGlyph.proc( this, attacker, defender, damage );
            }
            // 圣佑额外减伤
            int blocking = ((Hero) defender).subClass == HeroSubClass.PALADIN ? 3 : 1;
            damage -= Math.round(blocking * Glyph.genericProcChanceMultiplier(defender));

        } else {
            // 正常铭文触发
            if (glyph != null) {
                damage = glyph.proc(this, attacker, defender, damage);
            }
            if (trinityGlyph != null){
                damage = trinityGlyph.proc( this, attacker, defender, damage );
            }
            // 守护光环：友方单位获得减伤
            if (defender.alignment == Dungeon.hero.alignment
                    && Dungeon.hero.buff(AuraOfProtection.AuraBuff.class) != null
                    && (Dungeon.level.distance(defender.pos, Dungeon.hero.pos) <= 2 
                        || defender.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null)
                    && Dungeon.hero.buff(HolyWard.HolyArmBuff.class) != null) {
                int blocking = Dungeon.hero.subClass == HeroSubClass.PALADIN ? 3 : 1;
                damage -= Math.round(blocking * Glyph.genericProcChanceMultiplier(defender));
            }
        }
        damage = Math.max(damage, 0);
    }
    
    // 鉴定进度
    if (!levelKnown && defender == Dungeon.hero) {
        float uses = Math.min( availableUsesToID, Talent.itemIDSpeedFactor(Dungeon.hero, this) );
        availableUsesToID -= uses;
        usesLeftToID -= uses;
        if (usesLeftToID <= 0) {
            if (ShardOfOblivion.passiveIDDisabled()){
                // 遗忘碎片：只标记为可鉴定
                if (usesLeftToID > -1){
                    GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready"), name());
                }
                setIDReady();
            } else {
                identify();
                GLog.p(Messages.get(Armor.class, "identify"));
                Badges.validateItemLevelAquired(this);
            }
        }
    }
    
    return damage;
}
```

**作用**：处理被攻击时的效果触发。

**处理流程**：
1. 检查魔法免疫
2. 计算三位一体额外铭文
3. 触发铭文效果
4. 应用圣佑减伤（如果有）
5. 更新鉴定进度

---

### doEquip(Hero hero)

```java
@Override
public boolean doEquip( Hero hero ) {
    // 圣直觉天赋：牧师提前感知诅咒
    if (hero.heroClass != HeroClass.CLERIC && hero.hasTalent(Talent.HOLY_INTUITION)
            && cursed && !cursedKnown
            && Random.Int(20) < 1 + 2*hero.pointsInTalent(Talent.HOLY_INTUITION)){
        cursedKnown = true;
        GLog.p(Messages.get(this, "curse_detected"));
        return false;
    }

    detach(hero.belongings.backpack);

    Armor oldArmor = hero.belongings.armor;
    if (hero.belongings.armor == null || hero.belongings.armor.doUnequip( hero, true, false )) {
        
        hero.belongings.armor = this;
        
        cursedKnown = true;
        if (cursed) {
            equipCursed( hero );
            GLog.n( Messages.get(Armor.class, "equip_cursed") );
        }
        
        ((HeroSprite)hero.sprite).updateArmor();
        activate(hero);
        Talent.onItemEquipped(hero, this);
        hero.spend( timeToEquip( hero ) );

        // 战士职业：询问是否转移战士印
        if (Dungeon.hero.heroClass == HeroClass.WARRIOR && checkSeal() == null){
            BrokenSeal seal = oldArmor != null ? oldArmor.checkSeal() : null;
            if (seal != null && (!cursed || (seal.getGlyph() != null && seal.getGlyph().curse()))){
                // 显示转移确认窗口
                GameScene.show(new WndOptions(...));
            } else {
                hero.next();
            }
        } else {
            hero.next();
        }
        return true;
        
    } else {
        collect( hero.belongings.backpack );
        return false;
    }
}
```

**作用**：装备护甲。

**装备流程**：
1. 圣直觉检测诅咒（牧师天赋）
2. 从背包移除
3. 卸下旧护甲
4. 装备新护甲
5. 触发诅咒效果（如果有）
6. 更新英雄外观
7. 战士：询问转移战士印

---

### affixSeal(BrokenSeal seal)

```java
public void affixSeal(BrokenSeal seal){
    this.seal = seal;
    // 升级过后的印会提升护甲等级
    if (seal.level() > 0){
        int newLevel = trueValue()+1;
        level(newLevel);
        Badges.validateItemLevelAquired(this);
    }
    // 转移印上的铭文
    if (seal.getGlyph() != null){
        inscribe(seal.getGlyph());
    }
    // 如果已装备，激活护盾Buff
    if (isEquipped(Dungeon.hero)){
        Buff.affect(Dungeon.hero, BrokenSeal.WarriorShield.class).setArmor(this);
    }
}
```

**作用**：将战士印附加到护甲上。

**效果**：
- +1级印 → 护甲等级 +1
- 转移印上的铭文
- 激活护盾Buff

---

### detachSeal()

```java
public BrokenSeal detachSeal(){
    if (seal != null){
        // 移除护盾Buff
        if (isEquipped(Dungeon.hero)) {
            BrokenSeal.WarriorShield sealBuff = Dungeon.hero.buff(BrokenSeal.WarriorShield.class);
            if (sealBuff != null) sealBuff.setArmor(null);
        }

        BrokenSeal detaching = seal;
        seal = null;

        // 降低护甲等级
        if (detaching.level() > 0){
            degrade();
        }
        // 处理铭文转移
        if (detaching.canTransferGlyph()){
            inscribe(null);
        } else {
            detaching.setGlyph(null);
        }
        return detaching;
    } else {
        return null;
    }
}
```

**作用**：从护甲上分离战士印。

---

### inscribe(Glyph glyph)

```java
public Armor inscribe( Glyph glyph ) {
    if (glyph == null || !glyph.curse()) curseInfusionBonus = false;
    this.glyph = glyph;
    updateQuickslot();
    // 同步到战士印（为符文转移天赋准备）
    if (seal != null){
        seal.setGlyph(glyph);
    }
    // 已鉴定且在背包中：记录到目录
    if (glyph != null && isIdentified() && Dungeon.hero != null
            && Dungeon.hero.isAlive() && Dungeon.hero.belongings.contains(this)){
        Catalog.setSeen(glyph.getClass());
        Statistics.itemTypesDiscovered.add(glyph.getClass());
    }
    return this;
}
```

**作用**：为护甲附加铭文。

---

### hasGlyph(Class type, Char owner)

```java
public boolean hasGlyph(Class<?extends Glyph> type, Char owner) {
    // 魔法免疫：无铭文效果
    if (owner.buff(MagicImmune.class) != null) {
        return false;
    } 
    // 圣佑Buff覆盖普通铭文（圣骑士除外）
    else if (glyph != null
            && !glyph.curse()
            && owner instanceof Hero
            && isEquipped((Hero) owner)
            && owner.buff(HolyWard.HolyArmBuff.class) != null
            && ((Hero) owner).subClass != HeroSubClass.PALADIN){
        return false;
    } 
    // 三位一体天赋检查
    else if (owner.buff(BodyForm.BodyFormBuff.class) != null
            && owner.buff(BodyForm.BodyFormBuff.class).glyph() != null
            && owner.buff(BodyForm.BodyFormBuff.class).glyph().getClass().equals(type)){
        return true;
    } 
    // 正常检查
    else if (glyph != null) {
        return glyph.getClass() == type;
    } else {
        return false;
    }
}
```

**作用**：检查护甲是否有指定类型的铭文。

---

### STRReq() / STRReq(int lvl)

```java
public int STRReq(){
    return STRReq(level());
}

public int STRReq(int lvl){
    int req = STRReq(tier, lvl);
    // 精通药水减少力量需求
    if (masteryPotionBonus){
        req -= 2;
    }
    return req;
}

protected static int STRReq(int tier, int lvl){
    lvl = Math.max(0, lvl);
    // 力量需求公式：基础值 - 升级减免
    // 升级减免在 +1,+3,+6,+10,+15 等级时增加
    return (8 + Math.round(tier * 2)) - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
}
```

**作用**：计算护甲的力量需求。

**基础力量需求**：
| 阶位 | 力量需求 |
|------|----------|
| 1阶 | 10 |
| 2阶 | 12 |
| 3阶 | 14 |
| 4阶 | 16 |
| 5阶 | 18 |

**升级减免**：
| 升级等级 | 力量减免 |
|----------|----------|
| +1 | -1 |
| +3 | -2 |
| +6 | -3 |
| +10 | -4 |
| +15 | -5 |

---

### random()

```java
@Override
public Item random() {
    // 升级等级随机：+0(75%), +1(20%), +2(5%)
    int n = 0;
    if (Random.Int(4) == 0) {
        n++;
        if (Random.Int(5) == 0) {
            n++;
        }
    }
    level(n);

    Random.pushGenerator(Random.Long());
        // 30% 诅咒，15% 铭文
        float effectRoll = Random.Float();
        if (effectRoll < 0.3f * ParchmentScrap.curseChanceMultiplier()) {
            inscribe(Glyph.randomCurse());
            cursed = true;
        } else if (effectRoll >= 1f - (0.15f * ParchmentScrap.enchantChanceMultiplier())){
            inscribe();
        }
    Random.popGenerator();

    return this;
}
```

**作用**：生成随机护甲属性。

---

### value()

```java
@Override
public int value() {
    if (seal != null) return 0;  // 有战士印的护甲不可出售

    int price = 20 * tier;
    if (hasGoodGlyph()) {
        price *= 1.5;  // 好铭文 +50%
    }
    if (cursedKnown && (cursed || hasCurseGlyph())) {
        price /= 2;  // 诅咒 -50%
    }
    if (levelKnown && level() > 0) {
        price *= (level() + 1);  // 升级乘数
    }
    if (price < 1) {
        price = 1;
    }
    return price;
}
```

**作用**：计算护甲的出售价格。

---

### info()

```java
@Override
public String info() {
    String info = super.info();
    
    // 防御信息
    if (levelKnown) {
        info += "\n\n" + Messages.get(Armor.class, "curr_absorb", tier, DRMin(), DRMax(), STRReq());
        if (Dungeon.hero != null && STRReq() > Dungeon.hero.STR()) {
            info += " " + Messages.get(Armor.class, "too_heavy");
        }
    } else {
        info += "\n\n" + Messages.get(Armor.class, "avg_absorb", tier, DRMin(0), DRMax(0), STRReq(0));
        if (Dungeon.hero != null && STRReq(0) > Dungeon.hero.STR()) {
            info += " " + Messages.get(Armor.class, "probably_too_heavy");
        }
    }

    // 改造信息
    switch (augment) {
        case EVASION:
            info += " " + Messages.get(Armor.class, "evasion");
            break;
        case DEFENSE:
            info += " " + Messages.get(Armor.class, "defense");
            break;
        case NONE:
    }

    // 铭文信息
    if (isEquipped(Dungeon.hero) && !hasCurseGlyph() 
            && Dungeon.hero.buff(HolyWard.HolyArmBuff.class) != null
            && (Dungeon.hero.subClass != HeroSubClass.PALADIN || glyph == null)){
        // 圣佑覆盖
        info += "\n\n" + Messages.capitalize(Messages.get(Armor.class, "inscribed", 
                Messages.get(HolyWard.class, "glyph_name", Messages.get(Glyph.class, "glyph"))));
        info += " " + Messages.get(HolyWard.class, "glyph_desc");
    } else if (glyph != null  && (cursedKnown || !glyph.curse())) {
        info += "\n\n" +  Messages.capitalize(Messages.get(Armor.class, "inscribed", glyph.name()));
        if (glyphHardened) info += " " + Messages.get(Armor.class, "glyph_hardened");
        info += " " + glyph.desc();
    } else if (glyphHardened){
        info += "\n\n" + Messages.get(Armor.class, "hardened_no_glyph");
    }
    
    // 诅咒信息
    if (cursed && isEquipped( Dungeon.hero )) {
        info += "\n\n" + Messages.get(Armor.class, "cursed_worn");
    } else if (cursedKnown && cursed) {
        info += "\n\n" + Messages.get(Armor.class, "cursed");
    } else if (!isIdentified() && cursedKnown){
        if (glyph != null && glyph.curse()) {
            info += "\n\n" + Messages.get(Armor.class, "weak_cursed");
        } else {
            info += "\n\n" + Messages.get(Armor.class, "not_cursed");
        }
    }

    // 战士印信息
    if (seal != null) {
        info += "\n\n" + Messages.get(Armor.class, "seal_attached", seal.maxShield(tier, level()));
    }
    
    return info;
}
```

**作用**：生成护甲的完整描述文本。

---

### glowing()

```java
@Override
public ItemSprite.Glowing glowing() {
    // 圣佑效果：黄色光芒
    if (isEquipped(Dungeon.hero) && !hasCurseGlyph() 
            && Dungeon.hero.buff(HolyWard.HolyArmBuff.class) != null
            && (Dungeon.hero.subClass != HeroSubClass.PALADIN || glyph == null)){
        return HOLY;  // 0xFFFF00 黄色
    } else {
        return glyph != null && (cursedKnown || !glyph.curse()) ? glyph.glowing() : null;
    }
}
```

**作用**：返回护甲的光效颜色。

---

### emitter()

```java
@Override
public Emitter emitter() {
    if (seal == null) return super.emitter();
    // 有战士印时显示红色光芒粒子
    Emitter emitter = new Emitter();
    emitter.pos(ItemSpriteSheet.film.width(image)/2f + 2f, ItemSpriteSheet.film.height(image)/3f);
    emitter.fillTarget = false;
    emitter.pour(Speck.factory( Speck.RED_LIGHT ), 0.6f);
    return emitter;
}
```

**作用**：返回护甲的粒子效果（战士印红色光芒）。

---

## 11. 使用示例

### 创建自定义护甲

```java
public class DragonScaleArmor extends Armor {
    
    {
        image = ItemSpriteSheet.ARMOR_DRAGON;
        tier = 5;
    }
    
    public DragonScaleArmor() {
        super(5);  // 5阶护甲
    }
    
    @Override
    public String desc() {
        String desc = super.desc();
        desc += "\n\n这件龙鳞护甲提供额外的火焰抗性。";
        return desc;
    }
    
    @Override
    public int DRMax(int lvl) {
        // 龙鳞护甲：额外 +2 最大防御
        return super.DRMax(lvl) + 2;
    }
}
```

### 检查护甲属性

```java
// 获取防御范围
int minDR = armor.DRMin();
int maxDR = armor.DRMax();

// 检查力量需求
int strReq = armor.STRReq();
if (hero.STR() >= strReq) {
    // 可以正常装备
} else {
    // 力量不足，会有惩罚
    int penalty = strReq - hero.STR();
}

// 检查铭文
if (armor.hasGlyph(AntiMagic.class, hero)) {
    // 有抗魔铭文
}

// 检查诅咒
if (armor.hasCurseGlyph()) {
    // 有诅咒铭文
}
```

### 装备和卸下护甲

```java
// 装备护甲
if (armor.doEquip(hero)) {
    GLog.i("你装备了护甲。");
}

// 卸下护甲
if (armor.doUnequip(hero, true)) {
    GLog.i("你卸下了护甲。");
}

// 获取当前装备的护甲
Armor equipped = hero.belongings.armor();
```

### 添加和移除铭文

```java
// 随机添加铭文
armor.inscribe();

// 添加特定铭文
armor.inscribe(new AntiMagic());

// 移除铭文
armor.inscribe(null);

// 硬化铭文（防止升级丢失）
armor.glyphHardened = true;
```

### 战士印操作

```java
// 检查是否有战士印
BrokenSeal seal = armor.checkSeal();

// 附加战士印
armor.affixSeal(seal);

// 分离战士印
BrokenSeal detached = armor.detachSeal();
```

---

## 注意事项

### 防御计算顺序

1. **铭文效果** → 可能改变伤害值
2. **伤害减免** → 从伤害中减去随机DR值
3. **剩余伤害** → 应用到角色HP

### 升级注意事项

1. **普通铭文**：+4以上升级有几率丢失铭文
2. **诅咒铭文**：每次升级有33%几率移除
3. **硬化铭文**：+6以上升级有几率失去硬化状态
4. **符文转移天赋**：可推迟普通铭文丢失几率

### 力量惩罚

当角色力量 < 护甲力量需求时：
- **闪避惩罚**：每差1点，闪避 ÷ 1.5
- **速度惩罚**：每差1点，速度 ÷ 1.2
- **力量差距累积**：严重影响角色生存能力

### 鉴定机制

1. **使用次数**：需要约10次被攻击
2. **经验加成**：获得经验可补充可用次数
3. **天赋影响**：某些天赋可加速鉴定
4. **遗忘碎片**：只标记为可鉴定，需手动鉴定

---

## 最佳实践

### 护甲选择

```java
// 根据角色特点选择护甲
if (hero.heroClass == HeroClass.ROGUE) {
    // 盗贼：优先选择低力量需求的护甲
    // 配合闪避型改造
    armor.augment = Augment.EVASION;
}

if (hero.heroClass == HeroClass.WARRIOR) {
    // 战士：高阶护甲配合战士印
    // 配合防御型改造
    armor.augment = Augment.DEFENSE;
}
```

### 铭文策略

```java
// 输出型角色：选择增加机动性的铭文
// Swiftness（急速）、Flow（流动）

// 坦克型角色：选择减伤型铭文
// Stone（石）、Viscosity（粘滞）

// 特殊场景：选择功能性铭文
// AntiMagic（抗魔）- 魔法密集区域
// Camouflage（伪装）- 潜行流
```

### 升级建议

1. **优先升级武器**：护甲收益递减较快
2. **+4前安全**：普通铭文不会丢失
3. **+6前硬化**：超过+6可能失去硬化
4. **诅咒处理**：多次升级或移除诅咒

### 改造选择

| 改造类型 | 适用场景 | 优点 | 缺点 |
|----------|----------|------|------|
| 闪避型 | 高机动角色 | 更高闪避率 | 防御降低 |
| 防御型 | 坦克角色 | 更高伤害减免 | 闪避降低 |
| 无改造 | 平衡发展 | 无副作用 | 无加成 |

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `EquipableItem` | 父类 | 可装备物品基类 |
| `ClothArmor` | 子类 | 1阶布甲 |
| `LeatherArmor` | 子类 | 2阶皮甲 |
| `MailArmor` | 子类 | 3阶锁甲 |
| `ScaleArmor` | 子类 | 4阶鳞甲 |
| `PlateArmor` | 子类 | 5阶板甲 |
| `ClassArmor` | 子类 | 职业护甲（特殊能力） |
| `Glyph` | 内部类 | 护甲铭文基类 |
| `BrokenSeal` | 关联类 | 战士印 |
| `Hero` | 使用者 | 装备护甲的角色 |