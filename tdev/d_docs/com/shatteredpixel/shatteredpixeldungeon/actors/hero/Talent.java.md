# Talent - 天赋系统

## 概述
`Talent` 枚举类定义了 Shattered Pixel Dungeon 中英雄的天赋系统。天赋是英雄在升级过程中可以分配点数来增强特定能力的被动技能。每个职业都有独特的天赋树，分为4个等级层级（Tier），随着英雄等级提升逐步解锁。

天赋系统支持：
- 职业专属天赋
- 子职业专属天赋  
- 职业盔甲能力天赋
- 全局通用天赋
- 天赋变异（Metamorphosis）
- 天赋效果触发和管理

## 枚举常量

### 战士系天赋 (Warrior)
**Tier 1:**
- `HEARTY_MEAL` - 丰盛餐食：低血量时进食额外回复生命
- `VETERANS_INTUITION` - 老兵直觉：加快盔甲识别速度
- `PROVOKED_ANGER` - 激怒：攻击前获得额外伤害
- `IRON_WILL` - 铁意志：激活破碎封印护盾

**Tier 2:**
- `IRON_STOMACH` - 铁胃：免疫食物相关负面效果
- `LIQUID_WILLPOWER` - 液态意志：使用药水时获得护盾
- `RUNIC_TRANSFERENCE` - 符文转移：盔甲符文效果转移到武器
- `LETHAL_MOMENTUM` - 致命动量：攻击后立即行动
- `IMPROVISED_PROJECTILES` - 即兴投掷：无投掷武器时可投掷近战武器

**Tier 3 (战士):**
- `HOLD_FAST` - 坚守：休息时获得位置相关护甲
- `STRONGMAN` - 力士：基于力量的额外力量加成

**Tier 3 (狂战士):**
- `ENDLESS_RAGE` - 无尽狂怒：狂暴状态持续更久
- `DEATHLESS_FURY` - 不死之怒：狂暴状态提供无敌
- `ENRAGED_CATALYST` - 狂怒催化剂：狂暴增强其他效果

**Tier 3 (角斗士):**
- `CLEAVE` - 劈砍：攻击时对附近敌人造成溅射伤害
- `LETHAL_DEFENSE` - 致命防御：格挡反击造成额外伤害
- `ENHANCED_COMBO` - 强化连击：连击系统增强

**Tier 4 (英雄跃击):**
- `BODY_SLAM` - 身体冲撞：跃击落地时造成范围伤害
- `IMPACT_WAVE` - 冲击波：跃击产生冲击波
- `DOUBLE_JUMP` - 双重跳跃：可以进行两次跃击

**Tier 4 (冲击波):**
- `EXPANDING_WAVE` - 扩展波：冲击波范围更大
- `STRIKING_WAVE` - 打击波：冲击波造成直接伤害
- `SHOCK_FORCE` - 震荡之力：冲击波附带击退效果

**Tier 4 (耐力):**
- `SUSTAINED_RETRIBUTION` - 持续报复：耐力提供持续伤害减免
- `SHRUG_IT_OFF` - 无视伤害：耐力状态下快速恢复
- `EVEN_THE_ODDS` - 势均力敌：劣势时获得额外加成

### 法师系天赋 (Mage)
**Tier 1:**
- `EMPOWERING_MEAL` - 强化餐食：进食后增强法杖伤害
- `SCHOLARS_INTUITION` - 学者直觉：加快法杖识别速度
- `LINGERING_MAGIC` - 持续魔法：攻击附带额外魔法伤害
- `BACKUP_BARRIER` - 备用屏障：自动护盾

**Tier 2:**
- `ENERGIZING_MEAL` - 充能餐食：进食后获得法杖充能
- `INSCRIBED_POWER` - 铭刻力量：使用卷轴后增强法杖
- `WAND_PRESERVATION` - 法杖保存：减少法杖充能消耗
- `ARCANE_VISION` - 奥术视觉：扩大视野范围
- `SHIELD_BATTERY` - 护盾电池：护盾为法杖充能

**Tier 3 (法师):**
- `DESPERATE_POWER` - 绝望之力：低血量时增强装备
- `ALLY_WARP` - 盟友传送：可以传送盟友

**Tier 3 (战斗法师):**
- `EMPOWERED_STRIKE` - 强化打击：法杖攻击造成额外伤害
- `MYSTICAL_CHARGE` - 神秘充能：法杖充能效果增强
- `EXCESS_CHARGE` - 过量充能：法杖可以超充能

**Tier 3 (术士):**
- `SOUL_EATER` - 灵魂吞噬：击杀敌人恢复生命
- `SOUL_SIPHON` - 灵魂虹吸：持续吸取敌人生命
- `NECROMANCERS_MINIONS` - 死灵仆从：召唤亡灵助手

**Tier 4 (元素爆破):**
- `BLAST_RADIUS` - 爆炸半径：元素爆破范围更大
- `ELEMENTAL_POWER` - 元素力量：元素爆破伤害增强
- `REACTIVE_BARRIER` - 反应屏障：受到伤害时生成护盾

**Tier 4 (野性魔法):**
- `WILD_POWER` - 野性力量：随机增强魔法效果
- `FIRE_EVERYTHING` - 万物燃烧：所有攻击附带火焰
- `CONSERVED_MAGIC` - 魔法保存：减少魔法消耗

**Tier 4 (传送信标):**
- `TELEFRAG` - 传送粉碎：传送到敌人位置造成伤害
- `REMOTE_BEACON` - 远程信标：远程放置传送信标
- `LONGRANGE_WARP` - 远距传送：传送距离增加

### 盗贼系天赋 (Rogue)
**Tier 1:**
- `CACHED_RATIONS` - 储备口粮：携带额外食物
- `THIEFS_INTUITION` - 盗贼直觉：加快戒指识别速度
- `SUCKER_PUNCH` - 偷袭：对未察觉的敌人造成额外伤害
- `PROTECTIVE_SHADOWS` - 保护之影：隐身时定期获得护盾

**Tier 2:**
- `MYSTICAL_MEAL` - 神秘餐食：进食后获得遗物充能
- `INSCRIBED_STEALTH` - 铭刻隐匿：使用卷轴后获得隐身
- `WIDE_SEARCH` - 广域搜索：扩大搜索范围
- `SILENT_STEPS` - 无声脚步：移动时不触发陷阱
- `ROGUES_FORESIGHT` - 盗贼先知：提前发现危险

**Tier 3 (盗贼):**
- `ENHANCED_RINGS` - 强化戒指：使用遗物后增强戒指效果
- `LIGHT_CLOAK` - 轻盈披风：披风效果增强

**Tier 3 (刺客):**
- `ENHANCED_LETHALITY` - 强化致命：偷袭伤害大幅增加
- `ASSASSINS_REACH` - 刺客触及：攻击范围增加
- `BOUNTY_HUNTER` - 赏金猎人：击杀特定敌人获得奖励

**Tier 3 (自由奔跑者):**
- `EVASIVE_ARMOR` - 闪避盔甲：移动时获得闪避加成
- `PROJECTILE_MOMENTUM` - 投掷动量：移动中投掷武器更准确
- `SPEEDY_STEALTH` - 快速隐匿：隐身后移动速度增加

**Tier 4 (烟雾弹):**
- `HASTY_RETREAT` - 仓促撤退：使用烟雾弹后快速移动
- `BODY_REPLACEMENT` - 身体替换：烟雾弹创建替身
- `SHADOW_STEP` - 影步：烟雾弹后可以瞬移

**Tier 4 (死亡标记):**
- `FEAR_THE_REAPER` - 死神恐惧：死亡标记造成恐惧
- `DEATHLY_DURABILITY` - 死亡耐久：死亡标记提供护盾
- `DOUBLE_MARK` - 双重标记：可以标记多个目标

**Tier 4 (影分身):**
- `SHADOW_BLADE` - 影刃：分身使用武器攻击
- `CLONED_ARMOR` - 克隆盔甲：分身穿戴相同盔甲
- `PERFECT_COPY` - 完美复制：分身完全复制英雄能力

### 猎人系天赋 (Huntress)
**Tier 1:**
- `NATURES_BOUNTY` - 自然恩赐：携带额外自然物品
- `SURVIVALISTS_INTUITION` - 生存者直觉：加快投掷武器识别
- `FOLLOWUP_STRIKE` - 后续打击：投掷攻击后近战攻击增强
- `NATURES_AID` - 自然援助：自然效果增强

**Tier 2:**
- `INVIGORATING_MEAL` - 提神餐食：进食后获得加速
- `LIQUID_NATURE` - 液态自然：使用药水时召唤草丛
- `REJUVENATING_STEPS` - 回春步伐：在草地上移动时恢复
- `HEIGHTENED_SENSES` - 敏锐感官：视野和感知增强
- `DURABLE_PROJECTILES` - 耐用投掷：投掷武器耐久度增加

**Tier 3 (猎人):**
- `POINT_BLANK` - 近距离射击：近距离投掷伤害增加
- `SEER_SHOT` - 先知射击：投掷武器揭示隐藏内容

**Tier 3 (狙击手):**
- `FARSIGHT` - 远视：远程攻击精度增加
- `SHARED_ENCHANTMENT` - 共享附魔：投掷武器继承主武器附魔
- `SHARED_UPGRADES` - 共享升级：投掷武器继承主武器升级

**Tier 3 (守护者):**
- `DURABLE_TIPS` - 耐用箭头：投掷武器额外耐久
- `BARKSKIN` - 树皮皮肤：在草地上获得护甲
- `SHIELDING_DEW` - 护盾露水：露水提供护盾

**Tier 4 (灵能刀刃):**
- `FAN_OF_BLADES` - 刀刃扇：灵能刀刃范围攻击
- `PROJECTING_BLADES` - 投射刀刃：灵能刀刃穿透敌人
- `SPIRIT_BLADES` - 灵能刀刃：灵能刀刃造成额外伤害

**Tier 4 (自然之力):**
- `GROWING_POWER` - 成长之力：自然力量随时间增强
- `NATURES_WRATH` - 自然之怒：自然攻击范围更大
- `WILD_MOMENTUM` - 野性动量：移动中自然攻击增强

**Tier 4 (灵鹰):**
- `EAGLE_EYE` - 鹰眼：远程攻击精度大幅提升
- `GO_FOR_THE_EYES` - 攻击眼睛：远程攻击造成致盲
- `SWIFT_SPIRIT` - 敏捷之灵：灵鹰移动速度增加

### 决斗者系天赋 (Duelist)
**Tier 1:**
- `STRENGTHENING_MEAL` - 强化餐食：进食后增强物理伤害
- `ADVENTURERS_INTUITION` - 冒险者直觉：加快武器识别速度
- `PATIENT_STRIKE` - 耐心一击：站立不动后下一次攻击增强
- `AGGRESSIVE_BARRIER` - 进攻屏障：低血量时攻击获得护盾

**Tier 2:**
- `FOCUSED_MEAL` - 专注餐食：进食后不同效果（决斗者vs其他职业）
- `LIQUID_AGILITY` - 液态敏捷：使用药水后获得闪避和准确度
- `WEAPON_RECHARGING` - 武器充能：攻击时为法杖/遗物充能
- `LETHAL_HASTE` - 致命急速：击杀敌人后获得加速
- `SWIFT_EQUIP` - 快速装备：切换武器速度加快

**Tier 3 (决斗者):**
- `PRECISE_ASSAULT` - 精准突击：决斗者使用技能后获得超高准确度
- `DEADLY_FOLLOWUP` - 致命后续：投掷攻击后近战攻击伤害增强

**Tier 3 (冠军):**
- `VARIED_CHARGE` - 多样充能：副武器独立充能
- `TWIN_UPGRADES` - 双重升级：副武器共享主武器升级
- `COMBINED_LETHALITY` - 联合致命：副武器攻击增强

**Tier 3 (武僧):**
- `UNENCUMBERED_SPIRIT` - 无拘之灵：获得基础装备
- `MONASTIC_VIGOR` - 修道活力：武僧能力增强
- `COMBINED_ENERGY` - 联合能量：武僧能力和武器能力协同

**Tier 4 (挑战):**
- `CLOSE_THE_GAP` - 缩短距离：挑战技能拉近敌人
- `INVIGORATING_VICTORY` - 胜利提神：击败挑战目标后恢复
- `ELIMINATION_MATCH` - 淘汰赛：挑战目标被削弱

**Tier 4 (元素打击):**
- `ELEMENTAL_REACH` - 元素触及：元素打击范围增加
- `STRIKING_FORCE` - 打击之力：元素打击伤害增强
- `DIRECTED_POWER` - 定向力量：元素打击精准度增加

**Tier 4 (佯攻):**
- `FEIGNED_RETREAT` - 佯装撤退：佯攻后获得移动优势
- `EXPOSE_WEAKNESS` - 暴露弱点：佯攻使敌人易受攻击
- `COUNTER_ABILITY` - 反制能力：成功格挡后获得反击能力

### 牧师系天赋 (Cleric)
**Tier 1:**
- `SATIATED_SPELLS` - 饱食法术：进食后不同效果（牧师vs其他职业）
- `HOLY_INTUITION` - 神圣直觉：加快神圣物品识别
- `SEARING_LIGHT` - 灼热之光：神圣攻击造成额外伤害
- `SHIELD_OF_LIGHT` - 光之护盾：自动护盾

**Tier 2:**
- `ENLIGHTENING_MEAL` - 启迪餐食：进食后为神圣物品充能
- `RECALL_INSCRIPTION` - 铭刻回忆：使用消耗品后有几率返还
- `SUNRAY` - 日光：神圣攻击范围增加
- `DIVINE_SENSE` - 神圣感知：扩大感知范围
- `BLESS` - 祝福：获得祝福效果

**Tier 3 (牧师):**
- `CLEANSE` - 净化：使用遗物后有几率移除负面效果
- `LIGHT_READING` - 轻阅读：神圣典籍效果增强

**Tier 3 (祭司):**
- `HOLY_LANCE` - 神圣长枪：神圣攻击穿透敌人
- `HALLOWED_GROUND` - 神圣之地：创造神圣区域
- `MNEMONIC_PRAYER` - 祈祷记忆：祈祷效果持续更久

**Tier 3 (圣骑士):**
- `LAY_ON_HANDS` - 按手治疗：神圣治疗效果增强
- `AURA_OF_PROTECTION` - 保护光环：神圣护盾范围更大
- `WALL_OF_LIGHT` - 光之墙：神圣屏障阻挡敌人

**Tier 4 (升天形态):**
- `DIVINE_INTERVENTION` - 神圣干预：升天形态提供无敌
- `JUDGEMENT` - 审判：升天形态攻击范围更大
- `FLASH` - 闪光：升天形态移动速度增加

**Tier 4 (三位一体):**
- `BODY_FORM` - 身体形态：三位一体的身体形态
- `MIND_FORM` - 心智形态：三位一体的心智形态
- `SPIRIT_FORM` - 灵魂形态：三位一体的灵魂形态

**Tier 4 (众志成城):**
- `BEAMING_RAY` - 光束射线：众志成城的光束攻击
- `LIFE_LINK` - 生命链接：众志成城的生命链接
- `STASIS` - 静止：众志成城的时间静止

### 通用天赋
**Tier 4:**
- `HEROIC_ENERGY` - 英雄能量：通用第四层天赋槽
- `RATSISTANCE` - 鼠抗性：鼠化后的抗性增强
- `RATLOMACY` - 鼠交涉：鼠化后的社交能力
- `RATFORCEMENTS` - 鼠援军：鼠化后的召唤能力

## Buff 类

天赋系统定义了许多专用的 Buff 类来管理各种效果：

### 药水相关
- `ImprovisedProjectileCooldown` - 即兴投掷冷却
- `LethalMomentumTracker` - 致命动量追踪器
- `StrikingWaveTracker` - 打击波追踪器
- `WandPreservationCounter` - 法杖保存计数器
- `EmpoweredStrikeTracker` - 强化打击追踪器

### 隐身和护盾相关
- `ProtectiveShadowsTracker` - 保护之影追踪器
- `BountyHunterTracker` - 赏金猎人追踪器
- `RejuvenatingStepsCooldown` - 回春步伐冷却
- `RejuvenatingStepsFurrow` - 回春步伐耕地
- `SeerShotCooldown` - 先知射击冷却

### 攻击和战斗相关
- `SpiritBladesTracker` - 灵能刀刃追踪器
- `PatientStrikeTracker` - 耐心一击追踪器
- `AggressiveBarrierCooldown` - 进攻屏障冷却
- `LiquidAgilEVATracker` - 液态敏捷闪避追踪器
- `LiquidAgilACCTracker` - 液态敏捷准确度追踪器
- `LethalHasteCooldown` - 致命急速冷却
- `SwiftEquipCooldown` - 快速装备冷却
- `DeadlyFollowupTracker` - 致命后续追踪器
- `PreciseAssaultTracker` - 精准突击追踪器

### 能量和充能相关
- `VariedChargeTracker` - 多样充能追踪器
- `CombinedLethalityAbilityTracker` - 联合致命能力追踪器
- `CombinedEnergyAbilityTracker` - 联合能量能力追踪器
- `CounterAbilityTacker` - 反制能力追踪器

### 神圣相关
- `SatiatedSpellsTracker` - 饱食法术追踪器
- `SearingLightCooldown` - 灼热之光冷却

### 食物相关
- `CachedRationsDropped` - 储备口粮掉落
- `NatureBerriesDropped` - 自然浆果掉落

### 战士特殊
- `WarriorFoodImmunity` - 战士食物免疫

### 攻击追踪
- `ProvokedAngerTracker` - 激怒追踪器
- `LingeringMagicTracker` - 持续魔法追踪器
- `SuckerPunchTracker` - 偷袭追踪器
- `FollowupStrikeTracker` - 后续打击追踪器

## 核心方法

### 初始化方法
- **initClassTalents(Hero hero)**: 初始化职业天赋
- **initSubclassTalents(Hero hero)**: 初始化子职业天赋  
- **initArmorTalents(Hero hero)**: 初始化盔甲能力天赋

### 存储方法
- **storeTalentsInBundle(Bundle bundle, Hero hero)**: 将天赋存储到 Bundle
- **restoreTalentsFromBundle(Bundle bundle, Hero hero)**: 从 Bundle 恢复天赋

### 效果触发方法
- **onTalentUpgraded(Hero hero, Talent talent)**: 天赋升级时触发
- **onFoodEaten(Hero hero, float foodVal, Item foodSource)**: 进食时触发天赋效果
- **onPotionUsed(Hero hero, int cell, float factor)**: 使用药水时触发天赋效果
- **onScrollUsed(Hero hero, int pos, float factor, Class<?extends Item> cls)**: 使用卷轴时触发天赋效果
- **onRunestoneUsed(Hero hero, int pos, Class<?extends Item> cls)**: 使用符石时触发天赋效果
- **onArtifactUsed(Hero hero)**: 使用遗物时触发天赋效果
- **onItemEquipped(Hero hero, Item item)**: 装备物品时触发天赋效果
- **onItemCollected(Hero hero, Item item)**: 收集物品时触发天赋效果
- **onAttackProc(Hero hero, Char enemy, int dmg)**: 攻击处理时触发天赋效果

### 辅助方法
- **itemIDSpeedFactor(Hero hero, Item item)**: 计算物品识别速度因子
- **icon()**: 获取天赋图标
- **maxPoints()**: 获取天赋最大点数
- **title()**: 获取天赋标题
- **desc()**: 获取天赋描述

## 系统常量
- **tierLevelThresholds**: 各天赋层级解锁的等级阈值 [0, 2, 7, 13, 21, 31]
- **MAX_TALENT_TIERS**: 最大天赋层级数 (4)

## 使用示例

```java
// 检查天赋
if (hero.hasTalent(Talent.HEARTY_MEAL)) {
    // 处理丰盛餐食效果
}

// 获取天赋点数
int points = hero.pointsInTalent(Talent.VETERANS_INTUITION);

// 升级天赋
hero.upgradeTalent(Talent.PROVOKED_ANGER);

// 检查可用天赋点
int available = hero.talentPointsAvailable(1); // Tier 1
```

## 注意事项

1. **层级依赖**：高层级天赋需要先解锁低层级天赋
2. **职业限制**：某些天赋只能由特定职业或子职业使用
3. **效果叠加**：部分天赋效果可以叠加，但有上限
4. **性能优化**：Buff 追踪器类专门设计用于高效的状态管理
5. **兼容性**：系统支持天赋变异和动态天赋替换
6. **本地化**：所有天赋文本都通过 Messages 系统进行本地化