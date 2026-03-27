# 英雄系统详细文档

## 概述

英雄系统是 Shattered Pixel Dungeon 的核心系统，定义了玩家角色的所有属性、能力和发展路径。本文档详细描述英雄的各个子系统及其实现细节。

**核心源文件：**
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Hero.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/HeroClass.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/HeroSubClass.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Talent.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Belongings.java`

---

## 1. 职业系统

游戏共有 **6 个可选职业**，每个职业都有独特的初始装备、天赋系统和专属护甲能力。

### 1.1 职业枚举定义

```java
public enum HeroClass {
    WARRIOR,    // 战士
    MAGE,       // 法师
    ROGUE,      // 盗贼
    HUNTRESS,   // 猎人
    DUELIST,    // 决斗者
    CLERIC      // 牧师
}
```

### 1.2 各职业详情

#### 战士 (Warrior)
| 属性 | 详情 |
|------|------|
| **子职业** | Berserker (狂战士), Gladiator (角斗士) |
| **初始武器** | WornShortsword (旧短剑) |
| **初始护甲** | ClothArmor + BrokenSeal (布甲+破碎印记) |
| **初始物品** | ThrowingStone (投石), PotionOfHealing (治疗药水), ScrollOfRage (狂暴卷轴) |
| **护甲能力** | HeroicLeap (英雄跳跃), Shockwave (冲击波), Endure (忍耐) |
| **解锁条件** | 默认解锁 |

**职业特点：**
- 唯一拥有破碎印记的职业，提供额外护盾
- 印记可转移给其他护甲
- 适合新手玩家

---

#### 法师 (Mage)
| 属性 | 详情 |
|------|------|
| **子职业** | Battlemage (战斗法师), Warlock (术士) |
| **初始武器** | MagesStaff (法师法杖，内置魔法飞弹) |
| **初始护甲** | ClothArmor (布甲) |
| **初始物品** | ScrollOfUpgrade (升级卷轴), PotionOfLiquidFlame (液态火焰药水) |
| **护甲能力** | ElementalBlast (元素爆破), WildMagic (野性魔法), WarpBeacon (传送信标) |
| **解锁条件** | 需解锁 MASTERY_MAGE 徽章 |

**职业特点：**
- 法杖可镶嵌任意魔杖
- 镶嵌的魔杖可升级
- 拥有独特的魔杖充能机制

---

#### 盗贼 (Rogue)
| 属性 | 详情 |
|------|------|
| **子职业** | Assassin (刺客), Freerunner (自由跑者) |
| **初始武器** | Dagger (匕首) |
| **初始护甲** | ClothArmor (布甲) |
| **初始神器** | CloakOfShadows (暗影斗篷) |
| **初始物品** | ThrowingKnife (飞刀), ScrollOfMagicMapping (魔法地图卷轴), PotionOfInvisibility (隐形药水) |
| **护甲能力** | SmokeBomb (烟雾弹), DeathMark (死亡印记), ShadowClone (暗影分身) |
| **解锁条件** | 需解锁 MASTERY_ROGUE 徽章 |

**职业特点：**
- 拥有暗影斗篷神器
- 更高的搜索范围（2格 vs 1格）
- 更擅长潜行和偷袭

---

#### 猎人 (Huntress)
| 属性 | 详情 |
|------|------|
| **子职业** | Sniper (狙击手), Warden (守护者) |
| **初始武器** | Gloves (手套) |
| **初始护甲** | ClothArmor (布甲) |
| **初始物品** | SpiritBow (精灵弓), PotionOfMindVision (心灵视觉药水), ScrollOfLullaby (催眠卷轴) |
| **护甲能力** | SpectralBlades (幽灵刀刃), NaturesPower (自然之力), SpiritHawk (精灵鹰) |
| **解锁条件** | 需解锁 MASTERY_HUNTRESS 徽章 |

**职业特点：**
- 拥有无限弹药的精灵弓
- 可穿越草地而不损坏
- 更擅长远程战斗

---

#### 决斗者 (Duelist)
| 属性 | 详情 |
|------|------|
| **子职业** | Champion (冠军), Monk (武僧) |
| **初始武器** | Rapier (细剑) |
| **初始护甲** | ClothArmor (布甲) |
| **初始物品** | ThrowingSpike (投刺), PotionOfStrength (力量药水), ScrollOfMirrorImage (镜像卷轴) |
| **护甲能力** | Challenge (决斗挑战), ElementalStrike (元素打击), Feint (假动作) |
| **解锁条件** | 需解锁 MASTERY_DUELIST 徽章 |

**职业特点：**
- 拥有独特的武器技能充能系统
- 可使用武器特殊技能
- 武器充能随时间恢复

---

#### 牧师 (Cleric)
| 属性 | 详情 |
|------|------|
| **子职业** | Priest (祭司), Paladin (圣骑士) |
| **初始武器** | Cudgel (短棍) |
| **初始护甲** | ClothArmor (布甲) |
| **初始神器** | HolyTome (圣典) |
| **初始物品** | PotionOfPurity (纯净药水), ScrollOfRemoveCurse (解咒卷轴) |
| **护甲能力** | AscendedForm (飞升形态), Trinity (三位一体), PowerOfMany (众神之力) |
| **解锁条件** | 需解锁 MASTERY_CLERIC 徽章 |

**职业特点：**
- 拥有圣典神器，可施放神术
- 可使用多种圣术技能
- 支持多种玩法风格

---

## 2. 子职业系统

每个职业在击败第二个 Boss 后可选择一个子职业，获得额外的天赋和能力强化。

### 2.1 子职业枚举

```java
public enum HeroSubClass {
    NONE,           // 无子职业
    
    // 战士
    BERSERKER,      // 狂战士
    GLADIATOR,      // 角斗士
    
    // 法师
    BATTLEMAGE,     // 战斗法师
    WARLOCK,        // 术士
    
    // 盗贼
    ASSASSIN,       // 刺客
    FREERUNNER,     // 自由跑者
    
    // 猎人
    SNIPER,         // 狙击手
    WARDEN,         // 守护者
    
    // 决斗者
    CHAMPION,       // 冠军
    MONK,           // 武僧
    
    // 牧师
    PRIEST,         // 祭司
    PALADIN         // 圣骑士
}
```

### 2.2 子职业详情

#### 战战士子职业

| 子职业 | 描述 | 核心机制 |
|--------|------|----------|
| **Berserker (狂战士)** | 受伤时积累怒气，怒气提供伤害加成 | 怒气系统、濒死狂暴 |
| **Gladiator (角斗士)** | 连续攻击积累连击点，释放强力终结技 | Combo 连击系统 |

#### 法师子职业

| 子职业 | 描述 | 核心机制 |
|--------|------|----------|
| **Battlemage (战斗法师)** | 用法杖近战时触发魔杖特效 | 近战魔杖触发 |
| **Warlock (术士)** | 对敌人造成伤害时回复生命 | 灵魂收割 |

#### 盗贼子职业

| 子职业 | 描述 | 核心机制 |
|--------|------|----------|
| **Assassin (刺客)** | 准备攻击造成巨额伤害 | 准备攻击系统、暴击加成 |
| **Freerunner (自由跑者)** | 移动积累动能，提供速度和闪避 | Momentum 动能系统 |

#### 猎人子职业

| 子职业 | 描述 | 核心机制 |
|--------|------|----------|
| **Sniper (狙击手)** | 远程攻击可标记敌人，造成额外伤害 | SnipersMark 标记系统 |
| **Warden (守护者)** | 与自然互动获得加成，草地和露珠效果增强 | 自然加成、草皮护甲 |

#### 决斗者子职业

| 子职业 | 描述 | 核心机制 |
|--------|------|----------|
| **Champion (冠军)** | 可双持武器，武器升级共享 | 双持武器系统 |
| **Monk (武僧)** | 武僧能量系统，多种武术技能 | MonkEnergy 能量系统 |

#### 牧师子职业

| 子职业 | 描述 | 核心机制 |
|--------|------|----------|
| **Priest (祭司)** | 强化圣术效果，远程圣光攻击 | HolyLance 神圣长矛 |
| **Paladin (圣骑士)** | 近战圣光加成，光环保护 | AuraOfProtection 保护光环 |

---

## 3. 属性系统

英雄继承自 `Char` 类，拥有一系列核心属性。

### 3.1 基础属性

```java
// 来自 Char 基类
public int HT;              // 最大生命值 (Health Total)
public int HP;              // 当前生命值
public int pos;             // 当前位置（地图坐标）
public int viewDistance;    // 视野距离（默认8格）
public boolean[] fieldOfView; // 视野数组

// 状态标志
public int paralysed;       // 麻痹回合数
public boolean rooted;      // 是否定身
public boolean flying;      // 是否飞行
public int invisible;       // 隐形等级
```

### 3.2 英雄专属属性

```java
public class Hero extends Char {
    // 等级与经验
    public static final int MAX_LEVEL = 30;     // 最大等级
    public int lvl = 1;                          // 当前等级
    public int exp = 0;                          // 当前经验
    
    // 力量系统
    public static final int STARTING_STR = 10;  // 初始力量
    public int STR;                              // 基础力量值
    public int HTBoost = 0;                      // 生命值加成
    
    // 攻防属性
    private int attackSkill = 10;                // 攻击技能值
    private int defenseSkill = 5;                // 防御技能值
    
    // 感知
    public float awareness;                      // 感知概率
    
    // 状态
    public boolean ready = false;                // 是否可行动
    public boolean resting = false;              // 是否休息中
    public boolean damageInterrupt = true;       // 受伤是否打断
}
```

### 3.3 属性计算

#### 生命值计算
```java
public void updateHT(boolean boostHP) {
    HT = 20 + 5*(lvl-1) + HTBoost;  // 基础公式：20 + 5*等级 + 加成
    
    // 力量指环加成
    float multiplier = RingOfMight.HTMultiplier(this);
    HT = Math.round(multiplier * HT);
    
    // 强效药剂加成
    if (buff(ElixirOfMight.HTBoost.class) != null) {
        HT += buff(ElixirOfMight.HTBoost.class).boost();
    }
}
```

#### 力量计算
```java
public int STR() {
    int strBonus = 0;
    
    // 力量指环加成
    strBonus += RingOfMight.strengthBonus(this);
    
    // 肾上腺素激增
    AdrenalineSurge buff = buff(AdrenalineSurge.class);
    if (buff != null) {
        strBonus += buff.boost();
    }
    
    // 大力士天赋
    if (hasTalent(Talent.STRONGMAN)) {
        strBonus += (int)Math.floor(STR * (0.03f + 0.05f*pointsInTalent(Talent.STRONGMAN)));
    }
    
    return STR + strBonus;
}
```

#### 经验需求
```java
public static int maxExp(int lvl) {
    return 5 + lvl * 5;  // 每级需要 5+等级*5 点经验
}
```

### 3.4 攻击与防御

#### 攻击技能
```java
public int attackSkill(Char target) {
    KindOfWeapon wep = belongings.attackingWeapon();
    float accuracy = 1;
    
    // 精准指环加成
    accuracy *= RingOfAccuracy.accuracyMultiplier(this);
    
    // 天赋加成
    if (hasTalent(Talent.PRECISE_ASSAULT)) {
        // 非决斗者职业：+10%/20%/30% 精准
        // 决斗者职业：2x/5x/无限 精准
    }
    
    return Math.max(1, Math.round(attackSkill * accuracy * wep.accuracyFactor(this, target)));
}
```

#### 防御技能
```java
public int defenseSkill(Char enemy) {
    // 格挡状态（连击系统）
    if (buff(Combo.ParryTracker.class) != null) {
        return INFINITE_EVASION;
    }
    
    float evasion = defenseSkill;
    
    // 闪避指环加成
    evasion *= RingOfEvasion.evasionMultiplier(this);
    
    // 护甲影响
    if (belongings.armor() != null) {
        evasion = belongings.armor().evasionFactor(this, evasion);
    }
    
    return Math.max(1, Math.round(evasion));
}
```

#### 伤害减免 (DR)
```java
public int drRoll() {
    int dr = super.drRoll();
    
    // 护甲伤害减免
    if (belongings.armor() != null) {
        int armDr = Random.NormalIntRange(belongings.armor().DRMin(), belongings.armor().DRMax());
        // 力量不足时减少 DR
        if (STR() < belongings.armor().STRReq()) {
            armDr -= 2*(belongings.armor().STRReq() - STR());
        }
        if (armDr > 0) dr += armDr;
    }
    
    // 武器格挡
    if (belongings.weapon() != null && !RingOfForce.fightingUnarmed(this)) {
        int wepDr = Random.NormalIntRange(0, belongings.weapon().defenseFactor(this));
        if (wepDr > 0) dr += wepDr;
    }
    
    return dr;
}
```

---

## 4. 背包系统

`Belongings` 类管理英雄的所有物品和装备。

### 4.1 装备槽位

```java
public class Belongings {
    // 主要装备槽
    public KindOfWeapon weapon = null;      // 主武器
    public Armor armor = null;               // 护甲
    public Artifact artifact = null;         // 神器
    public KindofMisc misc = null;           // 杂项装备
    public Ring ring = null;                 // 戒指
    
    // 特殊槽位
    public KindOfWeapon thrownWeapon = null; // 投掷武器（临时）
    public KindOfWeapon abilityWeapon = null;// 技能武器（决斗者）
    public KindOfWeapon secondWep = null;    // 副武器（冠军）
    
    // 背包
    public Backpack backpack;                // 主背包
}
```

### 4.2 背包容量

```java
public static class Backpack extends Bag {
    public int capacity() {
        int cap = super.capacity();  // 基础容量 = 19
        for (Item item : items) {
            if (item instanceof Bag) {
                cap++;  // 容器本身占一格，但提供额外空间
            }
        }
        if (Dungeon.hero != null && Dungeon.hero.belongings.secondWep != null) {
            cap--;  // 副武器占用一格
        }
        return cap;
    }
}
```

### 4.3 可用容器

| 容器 | 容量 | 存储类型 |
|------|------|----------|
| Backpack (背包) | 19 | 所有物品 |
| VelvetPouch (天鹅绒袋) | 12 | 种子、符石、投掷武器 |
| ScrollHolder (卷轴筒) | 12 | 卷轴、魔法地图 |
| PotionBandolier (药水带) | 12 | 药水、炼金材料 |
| MagicalHolster (魔法枪套) | 12 | 魔杖 |

### 4.4 装备访问方法

```java
// 获取当前攻击武器（考虑投掷和技能）
public KindOfWeapon attackingWeapon() {
    if (thrownWeapon != null) return thrownWeapon;
    if (abilityWeapon != null) return abilityWeapon;
    return weapon();
}

// 获取装备（考虑背包丢失状态）
public KindOfWeapon weapon() {
    if (!lostInventory() || (weapon != null && weapon.keptThroughLostInventory())) {
        return weapon;
    }
    return null;
}
```

---

## 5. 天赋系统

天赋系统是英雄成长的核心，提供大量被动加成和主动能力。

### 5.1 天赋层级

```java
public static int[] tierLevelThresholds = new int[]{0, 2, 7, 13, 21, 31};

// Tier 1: 等级 2 解锁
// Tier 2: 等级 7 解锁  
// Tier 3: 等级 13 解锁（需选择子职业）
// Tier 4: 等级 21 解锁（需选择护甲能力）
```

### 5.2 天赋结构

```java
public enum Talent {
    // 战士 Tier 1
    HEARTY_MEAL(0),           // 丰盛一餐：低血量时进食回复HP
    VETERANS_INTUITION(1),    // 老兵直觉：快速识别护甲
    PROVOKED_ANGER(2),        // 激怒：受伤后下次攻击加伤
    IRON_WILL(3),             // 钢铁意志：提供护盾
    
    // 战士 Tier 2
    IRON_STOMACH(4),          // 铁胃：进食时减伤/免伤
    LIQUID_WILLPOWER(5),      // 液态意志：使用药水获得护盾
    RUNIC_TRANSFERENCE(6),    // 符文转移：印记继承强化
    LETHAL_MOMENTUM(7),       // 致命动能：击杀后立即行动
    IMPROVISED_PROJECTILES(8),// 即兴投掷：投掷石块伤害
    
    // ... 更多天赋
}
```

### 5.3 天赋点获取

```java
public int talentPointsAvailable(int tier) {
    // 未达到解锁等级
    if (lvl < (Talent.tierLevelThresholds[tier] - 1)) return 0;
    
    // Tier 3 需要子职业
    if (tier == 3 && subClass == HeroSubClass.NONE) return 0;
    
    // Tier 4 需要护甲能力
    if (tier == 4 && armorAbility == null) return 0;
    
    // 计算可用点数
    if (lvl >= Talent.tierLevelThresholds[tier+1]) {
        return Talent.tierLevelThresholds[tier+1] - Talent.tierLevelThresholds[tier] 
               - talentPointsSpent(tier) + bonusTalentPoints(tier);
    } else {
        return 1 + lvl - Talent.tierLevelThresholds[tier] 
               - talentPointsSpent(tier) + bonusTalentPoints(tier);
    }
}
```

### 5.4 各职业天赋分布

#### 战士天赋

| 层级 | 天赋名称 | 效果 |
|------|----------|------|
| T1 | Hearty Meal | 低于33%血量进食时回复4/6HP |
| T1 | Veteran's Intuition | 1.75x/瞬间识别护甲 |
| T1 | Provoked Anger | 受伤后下次攻击+3/5伤害 |
| T1 | Iron Will | 获得2/4护盾 |
| T2 | Iron Stomach | 进食期间减伤75%/免疫 |
| T2 | Liquid Willpower | 使用药水获得6.5%/10%最大HP护盾 |
| T2 | Runic Transference | 印记可转移强化/附加效果 |
| T2 | Lethal Momentum | 击杀后立即行动 |
| T2 | Improvised Projectiles | 投掷石块造成伤害 |

#### 法师天赋

| 层级 | 天赋名称 | 效果 |
|------|----------|------|
| T1 | Empowering Meal | 进食后魔杖伤害+2/3，持续3次 |
| T1 | Scholar's Intuition | 3x/瞬间识别魔杖 |
| T1 | Lingering Magic | 使用魔杖后下次攻击+1~2伤害 |
| T1 | Backup Barrier | 使用魔杖后获得护盾 |
| T2 | Energizing Meal | 进食后获得5/8回合充能 |
| T2 | Inscribed Power | 使用卷轴后魔杖强化 |
| T2 | Wand Preservation | 分解魔杖时返还魔杖 |
| T2 | Arcane Vision | 使用魔杖后获得心眼 |
| T2 | Shield Battery | 魔杖充能满时获得护盾 |

#### 盗贼天赋

| 层级 | 天赋名称 | 效果 |
|------|----------|------|
| T1 | Cached Rations | 发现藏匿口粮 |
| T1 | Thief's Intuition | 2x/瞬间识别戒指 |
| T1 | Sucker Punch | 惊讶攻击+1~2伤害 |
| T1 | Protective Shadows | 隐身时积累护盾 |
| T2 | Mystical Meal | 进食后神器充能 |
| T2 | Inscribed Stealth | 使用卷轴后隐身 |
| T2 | Wide Search | 搜索范围+1，圆形搜索 |
| T2 | Silent Steps | 接近敌人时不被发现 |
| T2 | Rogue's Foresight | 感知陷阱和密门 |

#### 猎人天赋

| 层级 | 天赋名称 | 效果 |
|------|----------|------|
| T1 | Nature's Bounty | 草地中发现浆果 |
| T1 | Survivalist's Intuition | 3x/瞬间识别投掷武器 |
| T1 | Followup Strike | 投掷后近战攻击+伤害 |
| T1 | Nature's Aid | 使用精灵弓后获得树皮 |
| T2 | Invigorating Meal | 进食后获得加速 |
| T2 | Liquid Nature | 使用药水生草/定身敌人 |
| T2 | Rejuvenating Steps | 踩草地恢复HP |
| T2 | Heightened Senses | 感知周围敌人 |
| T2 | Durable Projectiles | 投掷武器耐久提升 |

#### 决斗者天赋

| 层级 | 天赋名称 | 效果 |
|------|----------|------|
| T1 | Strengthening Meal | 进食后近战伤害+3，持续2/3次 |
| T1 | Adventurer's Intuition | 2.5x/瞬间识别武器 |
| T1 | Patient Strike | 等待后下次攻击+伤害 |
| T1 | Aggressive Barrier | 低血量攻击时获得护盾 |
| T2 | Focused Meal | 进食后获得武器充能 |
| T2 | Liquid Agility | 使用药水后闪避/精准提升 |
| T2 | Weapon Recharging | 充能时武器伤害提升 |
| T2 | Lethal Haste | 击杀后获得加速 |
| T2 | Swift Equip | 快速切换武器 |

#### 牧师天赋

| 层级 | 天赋名称 | 效果 |
|------|----------|------|
| T1 | Satiated Spells | 进食后圣术消耗降低 |
| T1 | Holy Intuition | 快速识别圣典 |
| T1 | Searing Light | 攻击时造成神圣伤害 |
| T1 | Shield of Light | 获得光之护盾 |
| T2 | Enlightening Meal | 进食后圣典充能 |
| T2 | Recall Inscription | 使用卷轴/符石时有概率返还 |
| T2 | Sunray | 阳光攻击 |
| T2 | Divine Sense | 感知敌人 |
| T2 | Bless | 祝福效果 |

---

## 6. 护甲能力系统

护甲能力是每个职业在击败第4个Boss后解锁的终极技能，通过将护甲升级为职业护甲来激活。

### 6.1 能力基类

```java
public abstract class ArmorAbility implements Bundlable {
    protected float baseChargeUse = 35;  // 基础充能消耗
    
    // 使用能力
    public void use(ClassArmor armor, Hero hero) { ... }
    
    // 激活效果（子类实现）
    protected abstract void activate(ClassArmor armor, Hero hero, Integer target);
    
    // 关联天赋
    public abstract Talent[] talents();
    
    // 充能消耗（考虑天赋减免）
    public float chargeUse(Hero hero) {
        float chargeUse = baseChargeUse;
        if (hero.hasTalent(Talent.HEROIC_ENERGY)) {
            // 减少 12%/23%/32%/40%
            switch (hero.pointsInTalent(Talent.HEROIC_ENERGY)) {
                case 1: chargeUse *= 0.88f; break;
                case 2: chargeUse *= 0.77f; break;
                case 3: chargeUse *= 0.68f; break;
                case 4: chargeUse *= 0.60f; break;
            }
        }
        return chargeUse;
    }
}
```

### 6.2 各职业护甲能力

#### 战士护甲能力

| 能力名称 | 充能消耗 | 效果描述 |
|----------|----------|----------|
| **Heroic Leap (英雄跳跃)** | 35 | 跳跃到目标位置，造成范围伤害 |
| **Shockwave (冲击波)** | 35 | 发出冲击波，击退并眩晕敌人 |
| **Endure (忍耐)** | 35 | 短暂无敌，之后反击造成伤害 |

#### 法师护甲能力

| 能力名称 | 充能消耗 | 效果描述 |
|----------|----------|----------|
| **Elemental Blast (元素爆破)** | 35 | 以魔杖元素释放大范围攻击 |
| **Wild Magic (野性魔法)** | 35 | 随机释放所有魔杖效果 |
| **Warp Beacon (传送信标)** | 35 | 设置信标，随时传送回信标位置 |

#### 盗贼护甲能力

| 能力名称 | 充能消耗 | 效果描述 |
|----------|----------|----------|
| **Smoke Bomb (烟雾弹)** | 35 | 瞬移并留下烟雾，致盲敌人 |
| **Death Mark (死亡印记)** | 35 | 标记敌人，标记期间伤害增加 |
| **Shadow Clone (暗影分身)** | 35 | 创建一个协助战斗的分身 |

#### 猎人护甲能力

| 能力名称 | 充能消耗 | 效果描述 |
|----------|----------|----------|
| **Spectral Blades (幽灵刀刃)** | 35 | 发射幽灵刀刃攻击敌人 |
| **Nature's Power (自然之力)** | 35 | 获得自然加成，移速和攻击提升 |
| **Spirit Hawk (精灵鹰)** | 35 | 召唤精灵鹰侦察和攻击 |

#### 决斗者护甲能力

| 能力名称 | 充能消耗 | 效果描述 |
|----------|----------|----------|
| **Challenge (决斗挑战)** | 35 | 与敌人1v1决斗，胜利回复 |
| **Elemental Strike (元素打击)** | 35 | 武器附加元素效果攻击 |
| **Feint (假动作)** | 35 | 假装攻击后反击 |

#### 牧师护甲能力

| 能力名称 | 充能消耗 | 效果描述 |
|----------|----------|----------|
| **Ascended Form (飞升形态)** | 35 | 进入飞升状态，强化各项能力 |
| **Trinity (三位一体)** | 35 | 切换不同形态获得加成 |
| **Power of Many (众神之力)** | 35 | 召唤神力协助 |

### 6.3 护甲能力天赋 (Tier 4)

每个护甲能力都有3个专属天赋，可通过天赋点强化：

```java
// 示例：英雄跳跃的天赋
BODY_SLAM,       // 落地造成范围伤害
IMPACT_WAVE,     // 击退周围敌人
DOUBLE_JUMP      // 允许连续跳跃

// 示例：烟雾弹的天赋
HASTY_RETREAT,   // 使用后获得加速
BODY_REPLACEMENT,// 留下分身
SHADOW_STEP      // 额外隐身时间
```

### 6.4 通用护甲能力天赋

所有职业共享 `HEROIC_ENERGY` 天赋，可降低护甲能力充能消耗：

```java
HEROIC_ENERGY(26, 4)  // 充能消耗降低 12%/23%/32%/40%
```

---

## 7. 英雄行为系统

### 7.1 行动类型

```java
// 英雄行为类型
public class HeroAction {
    public static class Move extends HeroAction { int dst; }
    public static class Interact extends HeroAction { Char ch; }
    public static class Buy extends HeroAction { int dst; }
    public static class PickUp extends HeroAction { int dst; }
    public static class OpenChest extends HeroAction { int dst; }
    public static class Unlock extends HeroAction { int dst; }
    public static class Mine extends HeroAction { int dst; }
    public static class LvlTransition extends HeroAction { int dst; }
    public static class Attack extends HeroAction { Char target; }
    public static class Alchemy extends HeroAction { int dst; }
}
```

### 7.2 核心行为方法

```java
@Override
public boolean act() {
    fieldOfView = Dungeon.level.heroFOV;
    
    // 检查视野内的敌人
    checkVisibleMobs();
    BuffIndicator.refreshHero();
    
    if (paralysed > 0) {
        curAction = null;
        spendAndNext(TICK);
        return false;
    }
    
    if (curAction == null) {
        if (resting) {
            spendConstant(TIME_TO_REST);
            next();
        } else {
            ready();
        }
        return false;
    }
    
    // 执行当前行动
    // ...
}
```

---

## 8. 数据持久化

### 8.1 存储字段

```java
private static final String CLASS = "class";
private static final String SUBCLASS = "subClass";
private static final String ABILITY = "armorAbility";
private static final String ATTACK = "attackSkill";
private static final String DEFENSE = "defenseSkill";
private static final String STRENGTH = "STR";
private static final String LEVEL = "lvl";
private static final String EXPERIENCE = "exp";
private static final String HTBOOST = "htboost";
```

### 8.2 存储方法

```java
@Override
public void storeInBundle(Bundle bundle) {
    super.storeInBundle(bundle);
    
    bundle.put(CLASS, heroClass);
    bundle.put(SUBCLASS, subClass);
    bundle.put(ABILITY, armorAbility);
    Talent.storeTalentsInBundle(bundle, this);
    
    bundle.put(ATTACK, attackSkill);
    bundle.put(DEFENSE, defenseSkill);
    bundle.put(STRENGTH, STR);
    bundle.put(LEVEL, lvl);
    bundle.put(EXPERIENCE, exp);
    bundle.put(HTBOOST, HTBoost);
    
    belongings.storeInBundle(bundle);
}
```

---

## 9. 总结

英雄系统是 Shattered Pixel Dungeon 最复杂的系统之一，包含：

1. **6个独特职业**：每个都有专属装备、天赋和护甲能力
2. **12个子职业**：提供进一步的游戏风格分化
3. **深度天赋系统**：4个层级，100+种天赋
4. **装备系统**：多槽位装备，背包管理
5. **护甲能力**：终极技能系统，带专属天赋

该系统设计精良，提供了丰富的角色构建可能性和重玩价值。