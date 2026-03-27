# Berserk (狂战士状态)

> **源文件**: `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Berserk.java`
> **最后更新**: 2026-03-26

---

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **完整类名** | `com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Berserk` |
| **继承关系** | `ShieldBuff` → `Buff` → `Actor` |
| **实现接口** | `ActionIndicator.Action` |
| **Buff类型** | `POSITIVE` (正面Buff) |
| **所属模块** | 战士子职业「狂战士」(Berserker) |

---

## 类职责

`Berserk` 是战士英雄子职业「狂战士」的核心机制类，实现了以下职责：

1. **怒气系统管理** - 跟踪和计算怒气值(power)，怒气通过受到伤害积累
2. **狂暴状态激活** - 当怒气达到100%时，玩家可主动激活狂暴状态获得护盾
3. **护盾系统** - 提供基于当前生命值百分比的动态护盾
4. **伤害加成** - 根据怒气值提供最高50%的伤害加成
5. **死亡保护** - 通过「不死之怒」天赋实现濒死时自动触发狂暴
6. **恢复机制** - 狂暴结束后需要恢复才能再次使用

---

## 4. 继承与协作关系

```
                    ┌─────────────┐
                    │   Actor     │
                    │  (抽象类)   │
                    └──────┬──────┘
                           │ extends
                    ┌──────▼──────┐
                    │    Buff     │
                    │  (抽象类)   │
                    └──────┬──────┘
                           │ extends
                    ┌──────▼──────┐
                    │ ShieldBuff  │
                    │  (抽象类)   │
                    └──────┬──────┘
                           │ extends + implements
        ┌──────────────────▼──────────────────┐
        │             Berserk                 │
        └──────────────────┬──────────────────┘
                           │ 使用/关联
        ┌──────────────────┼──────────────────┐
        ▼                  ▼                  ▼
┌───────────────┐  ┌──────────────┐  ┌───────────────┐
│    Hero       │  │   Talent     │  │ WarriorShield │
│  (英雄类)     │  │  (天赋系统)  │  │  (战士印章)   │
└───────────────┘  └──────────────┘  └───────────────┘
```

### 关联的天赋

| 天赋 | 作用 |
|------|------|
| `ENDLESS_RAGE` (无尽之怒) | 允许怒气超过100%，最高达到116.67% |
| `DEATHLESS_FURY` (不死之怒) | 濒死时自动触发狂暴，避免死亡 |
| `ENRAGED_CATALYST` (狂暴催化剂) | 根据怒气值提高武器附魔触发概率 |

---

## 静态常量表

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `LEVEL_RECOVER_START` | `float` | `4.0f` | 等级恢复的起始值（用于死后狂暴恢复） |
| `TURN_RECOVERY_START` | `int` | `100` | 回合恢复的起始值（用于正常狂暴后恢复） |
| `STATE` | `String` | `"state"` | 序列化键：状态 |
| `LEVEL_RECOVERY` | `String` | `"levelrecovery"` | 序列化键：等级恢复进度 |
| `TURN_RECOVERY` | `String` | `"turn_recovery"` | 序列化键：回合恢复进度 |
| `POWER` | `String` | `"power"` | 序列化键：怒气值 |
| `POWER_BUFFER` | `String` | `"power_buffer"` | 序列化键：怒气损失缓冲 |

---

## 实例字段表

| 字段名 | 类型 | 初始值 | 说明 |
|--------|------|--------|------|
| `state` | `State` | `State.NORMAL` | 当前状态（NORMAL/BERSERK/RECOVERING） |
| `levelRecovery` | `float` | `0` | 等级恢复剩余值（死后狂暴需要获得经验恢复） |
| `turnRecovery` | `int` | `0` | 回合恢复剩余值（正常狂暴后需要等待回合） |
| `powerLossBuffer` | `int` | `0` | 怒气损失缓冲计数器（受击后延迟衰减） |
| `power` | `float` | `0` | 当前怒气值（0~1+，1表示100%） |

---

## 状态机详解

`Berserk` 使用三态状态机管理狂战士的行为：

```
                    ┌─────────────────────────────────────┐
                    │                                     │
                    ▼                                     │
            ┌───────────────┐                             │
            │    NORMAL     │◄────────────────────────────┤
            │   (正常状态)  │                             │
            └───────┬───────┘                             │
                    │                                     │
         ┌──────────┴──────────┐                         │
         │                     │                          │
         │ power >= 1.0f       │ doAction() 或           │
         │ + doAction()        │ berserking() 自动触发    │
         ▼                     │                          │
┌─────────────────┐           │                          │
│    BERSERK      │           │                          │
│  (狂暴状态)     │           │                          │
└────────┬────────┘           │                          │
         │                     │                          │
         │ 护盾耗尽            │                          │
         ▼                     │                          │
┌─────────────────┐           │                          │
│   RECOVERING    │───────────┴───────┬──────────────────┘
│   (恢复状态)    │                    │
└─────────────────┘                    │
                                       │
                        ┌──────────────┴──────────────┐
                        │                             │
                        │ levelRecovery=0            │ turnRecovery=0
                        │ + 获得经验                  │ + 等待回合
                        │ (死后狂暴)                  │ (正常狂暴)
                        │                             │
                        └─────────────────────────────┘
```

### 状态说明

| 状态 | 描述 | 图标颜色 | 可用操作 |
|------|------|----------|----------|
| `NORMAL` | 正常状态，可积累怒气 | 橙色(power<100%) / 红色(power≥100%) | 激活狂暴 |
| `BERSERK` | 狂暴中，拥有护盾 | 红色 | 无 |
| `RECOVERING` | 恢复中，无法使用 | 蓝色 | 无 |

---

## 7. 方法详解

### 实例初始化块

```java
{
    type = buffType.POSITIVE;
    detachesAtZero = false;
    shieldUsePriority = -1;
}
```

**逐行解释**：
- **Line 50**: 设置Buff类型为正面效果，显示为绿色图标
- **Line 52**: 当护盾降至0时不自动分离此Buff（狂战士需要保留状态机）
- **Line 53**: 设置护盾使用优先级为-1，表示此护盾在其他护盾之后被消耗

---

### storeInBundle(Bundle bundle)

```java
@Override
public void storeInBundle(Bundle bundle) {
    super.storeInBundle(bundle);
    bundle.put(STATE, state);
    bundle.put(POWER, power);
    bundle.put(POWER_BUFFER, powerLossBuffer);
    bundle.put(LEVEL_RECOVERY, levelRecovery);
    bundle.put(TURN_RECOVERY, turnRecovery);
}
```

**功能**：将Berserk状态序列化保存，用于游戏存档

**逐行解释**：
- **Line 78**: 调用父类ShieldBuff的序列化方法（保存护盾值）
- **Line 79**: 保存当前状态（NORMAL/BERSERK/RECOVERING）
- **Line 80**: 保存当前怒气值
- **Line 81**: 保存怒气损失缓冲计数
- **Line 82**: 保存等级恢复进度
- **Line 83**: 保存回合恢复进度

---

### restoreFromBundle(Bundle bundle)

```java
@Override
public void restoreFromBundle(Bundle bundle) {
    super.restoreFromBundle(bundle);
    state = bundle.getEnum(STATE, State.class);
    power = bundle.getFloat(POWER);
    powerLossBuffer = bundle.getInt(POWER_BUFFER);
    levelRecovery = bundle.getFloat(LEVEL_RECOVERY);
    turnRecovery = bundle.getInt(TURN_RECOVERY);

    if (power >= 1f && state == State.NORMAL){
        ActionIndicator.setAction(this);
    }
}
```

**功能**：从存档恢复Berserk状态

**逐行解释**：
- **Line 88**: 调用父类恢复方法
- **Line 90-94**: 恢复各字段值
- **Line 96-98**: 如果怒气已满且处于正常状态，重新注册动作指示器

---

### act()

```java
@Override
public boolean act() {
    if (state == State.BERSERK){
        if (target.shielding() > 0) {
            // 护盾每回合衰减2.5%
            float dmg = (float)Math.ceil(target.shielding() * 0.025f) * HoldFast.buffDecayFactor(target);
            if (Random.Float() < dmg % 1){
                dmg++;
            }
            ShieldBuff.processDamage(target, (int)dmg, this);
            // 护盾耗尽后进入恢复状态
            if (target.shielding() <= 0){
                state = State.RECOVERING;
                power = 0f;
                BuffIndicator.refreshHero();
                if (!target.isAlive()){
                    target.die(this);
                    if (!target.isAlive()) Dungeon.fail(this);
                }
            }
        } else {
            // 无护盾直接进入恢复
            state = State.RECOVERING;
            power = 0f;
            // 检查是否因护盾消失而死亡
            if (!target.isAlive()){
                target.die(this);
                if (!target.isAlive()) Dungeon.fail(this);
            }
        }
    } else if (state == State.NORMAL) {
        // 怒气自然衰减逻辑
        if (powerLossBuffer > 0){
            powerLossBuffer--;
        } else {
            // 衰减速度与当前生命值比例相关
            power -= GameMath.gate(0.1f, power, 1f) * 0.05f * Math.pow((target.HP / (float) target.HT), 2);
            if (power < 1f){
                ActionIndicator.clearAction(this);
            } else {
                ActionIndicator.refresh();
            }
            if (power <= 0) {
                detach();
            }
        }
    } else if (state == State.RECOVERING && levelRecovery == 0 && Regeneration.regenOn()){
        // 回合恢复逻辑
        turnRecovery--;
        if (turnRecovery <= 0){
            turnRecovery = 0;
            state = State.NORMAL;
        }
    }
    spend(TICK);
    return true;
}
```

**功能**：每回合执行的核心逻辑，处理三种状态的转换和效果

**逐行解释**：

**BERSERK状态处理 (Line 103-131)**:
- **Line 103-104**: 当处于狂暴状态且有护盾时
- **Line 106**: 计算护盾衰减伤害（每回合2.5%，受HoldFast天赋影响减少）
- **Line 107-109**: 处理小数部分的随机伤害
- **Line 111**: 应用护盾伤害
- **Line 113-121**: 护盾耗尽时切换到恢复状态，检查是否死亡

**NORMAL状态处理 (Line 132-147)**:
- **Line 133-134**: 如果有怒气损失缓冲，减少缓冲值
- **Line 136**: 计算怒气衰减：`衰减量 = 基础衰减 × 生命比例²`
  - 生命值越高，衰减越慢
  - 公式确保怒气越高衰减越快（至少0.1倍）
- **Line 138-146**: 更新动作指示器显示，怒气归零时分离Buff

**RECOVERING状态处理 (Line 148-154)**:
- **Line 148**: 仅在等级恢复完成且允许再生时执行回合恢复
- **Line 149-153**: 每回合减少恢复计数，归零后回到正常状态

---

### detach()

```java
@Override
public void detach() {
    super.detach();
    if (state == State.BERSERK) {
        state = State.RECOVERING;
    }
    ActionIndicator.clearAction(this);
}
```

**功能**：处理Buff分离时的特殊情况

**逐行解释**：
- **Line 161**: 调用父类分离方法
- **Line 162-164**: 如果正在狂暴，切换到恢复状态（防止意外脱离导致状态异常）
- **Line 165**: 清除动作指示器

---

### enchantFactor(float chance)

```java
public float enchantFactor(float chance){
    return chance + ((Math.min(1f, power) * 0.15f) * ((Hero) target).pointsInTalent(Talent.ENRAGED_CATALYST));
}
```

**功能**：计算武器附魔触发概率加成（配合「狂暴催化剂」天赋）

**参数**：
- `chance`: 原始附魔触发概率

**返回**：增加后的附魔触发概率

**公式**：
```
新概率 = 原概率 + (怒气值 × 0.15 × 天赋点数)
```

**示例**：
- 怒气100%，天赋3级：增加45%附魔触发率
- 被调用位置：`Weapon.java` 第547行

---

### damageFactor(float dmg)

```java
public float damageFactor(float dmg){
    return dmg * Math.min(1.5f, 1f + (power / 2f));
}
```

**功能**：计算基于怒气的伤害加成

**参数**：
- `dmg`: 原始伤害值

**返回**：加成后的伤害值

**公式**：
```
伤害倍率 = min(1.5, 1 + 怒气/2)
```

**怒气与伤害关系表**：

| 怒气值 | 伤害倍率 | 增幅 |
|--------|----------|------|
| 0% | 1.0x | +0% |
| 25% | 1.125x | +12.5% |
| 50% | 1.25x | +25% |
| 75% | 1.375x | +37.5% |
| 100% | 1.5x | +50% |
| 116.67% | 1.5x (上限) | +50% |

---

### berserking()

```java
public boolean berserking(){
    if (target.HP == 0
            && state == State.NORMAL
            && power >= 1f
            && ((Hero)target).hasTalent(Talent.DEATHLESS_FURY)){
        startBerserking();
        ActionIndicator.clearAction(this);
    }
    return state == State.BERSERK && target.shielding() > 0;
}
```

**功能**：检查是否正在狂暴，并处理「不死之怒」天赋的自动触发

**逐行解释**：
- **Line 177-180**: 如果英雄HP为0（濒死）、处于正常状态、怒气已满、有「不死之怒」天赋
- **Line 181-182**: 自动触发狂暴，清除动作指示器
- **Line 185**: 返回是否正在狂暴且有护盾

**调用位置**：`Hero.isAlive()` 用于判断英雄是否存活

---

### startBerserking()

```java
private void startBerserking(){
    state = State.BERSERK;
    SpellSprite.show(target, SpellSprite.BERSERK);
    Sample.INSTANCE.play( Assets.Sounds.CHALLENGE );
    GameScene.flash(0xFF0000);

    if (target.HP > 0) {
        turnRecovery = TURN_RECOVERY_START;
        levelRecovery = 0;
    } else {
        levelRecovery = LEVEL_RECOVER_START - ((Hero)target).pointsInTalent(Talent.DEATHLESS_FURY);
        turnRecovery = 0;
    }

    int shieldAmount = currentShieldBoost();
    setShield(shieldAmount);
    target.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(shieldAmount), FloatingText.SHIELDING );

    BuffIndicator.refreshHero();
}
```

**功能**：启动狂暴状态的内部方法

**逐行解释**：
- **Line 189**: 设置状态为BERSERK
- **Line 190**: 显示法术特效
- **Line 191**: 播放挑战音效
- **Line 192**: 屏幕红色闪烁效果
- **Line 194-199**: 根据是否濒死设置恢复方式：
  - 正常激活：100回合恢复
  - 濒死激活：需要获得经验恢复（天赋每级减少1）
- **Line 202-204**: 计算并设置护盾值，显示护盾获得特效
- **Line 206**: 刷新Buff图标显示

---

### currentShieldBoost()

```java
public int currentShieldBoost(){
    // 基础倍率：生命值越低，倍率越高
    float shieldMultiplier = 1f + 2*(float)Math.pow((1f-(target.HP/(float)target.HT)), 3);

    // 无尽之怒效果：超过100%的怒气增强护盾和减少冷却
    if (power > 1f){
        shieldMultiplier *= power;
        levelRecovery *= 2f - power;
        turnRecovery *= 2f - power;
    }

    int baseShield = 8;
    if (target instanceof Hero && ((Hero) target).belongings.armor() != null){
        baseShield += 2*((Hero) target).belongings.armor().buffedLvl();
    }
    return Math.round(baseShield * shieldMultiplier);
}
```

**功能**：计算当前可获得的护盾量

**护盾计算公式**：
```
基础护盾 = 8 + 2 × 护甲强化等级
护盾倍率 = 1 + 2 × (1 - HP/HT)³

如果怒气 > 100%:
    护盾倍率 ×= 怒气值
    恢复时间 ×= (2 - 怒气值)

最终护盾 = 基础护盾 × 护盾倍率
```

**生命值与护盾倍率关系**：

| 生命比例 | 护盾倍率 | 说明 |
|----------|----------|------|
| 100% | 1.0x | 满血 |
| 75% | 1.03x | |
| 50% | 1.25x | 半血 |
| 37% | 1.5x | 基准点 |
| 25% | 2.125x | |
| 10% | 2.65x | |
| 0% | 3.0x | 濒死 |

---

### maxShieldBoost()

```java
public int maxShieldBoost(){
    int baseShield = 8;
    if (target instanceof Hero && ((Hero) target).belongings.armor() != null){
        baseShield += 2*((Hero) target).belongings.armor().buffedLvl();
    }
    return baseShield*3;
}
```

**功能**：返回理论最大护盾量（不考虑天赋和怒气超限）

**返回**：基础护盾 × 3

---

### damage(int damage)

```java
public void damage(int damage){
    if (state != State.NORMAL) return;
    float maxPower = 1f + 0.1667f*((Hero)target).pointsInTalent(Talent.ENDLESS_RAGE);
    power = Math.min(maxPower, power + (damage/(float)target.HT)/4f );
    BuffIndicator.refreshHero();
    powerLossBuffer = 3;
    if (power >= 1f){
        ActionIndicator.setAction(this);
    }
}
```

**功能**：处理受到伤害时怒气的积累

**参数**：
- `damage`: 受到的伤害值

**怒气计算公式**：
```
怒气增加 = 伤害 / 最大生命值 / 4
最大怒气 = 1 + 0.1667 × 「无尽之怒」天赋点数
```

**天赋效果**：

| 天赋等级 | 最大怒气 |
|----------|----------|
| 0 | 100% |
| 1 | 116.67% |
| 2 | 133.33% |
| 3 | 150% |

**调用位置**：`Hero.defenseProc()` 第1541-1542行

---

### recover(float percent)

```java
public void recover(float percent){
    if (state == State.RECOVERING && levelRecovery > 0){
        levelRecovery -= percent;
        if (levelRecovery <= 0) {
            levelRecovery = 0;
            if (turnRecovery == 0){
                state = State.NORMAL;
            }
        }
    }
}
```

**功能**：通过获得经验值恢复狂暴状态（用于死后狂暴）

**参数**：
- `percent`: 获得的经验百分比（相对于升级所需经验）

**调用位置**：`Hero.onGainExp()` 第1987-1988行

---

### ActionIndicator.Action 接口实现

#### actionName()
```java
@Override
public String actionName() {
    return Messages.get(this, "action_name");
}
```
**功能**：返回动作按钮显示的名称

#### actionIcon()
```java
@Override
public int actionIcon() {
    return HeroIcon.BERSERK;
}
```
**功能**：返回动作按钮图标ID

#### secondaryVisual()
```java
@Override
public Visual secondaryVisual() {
    BitmapText txt = new BitmapText(PixelScene.pixelFont);
    txt.text((int) (power * 100) + "%");
    txt.hardlight(CharSprite.POSITIVE);
    txt.measure();
    return txt;
}
```
**功能**：返回动作按钮右下角显示的怒气百分比文本

#### indicatorColor()
```java
@Override
public int indicatorColor() {
    return 0x660000;
}
```
**功能**：返回动作按钮背景色（深红色）

#### doAction()
```java
@Override
public void doAction() {
    WarriorShield shield = target.buff(WarriorShield.class);
    if (shield != null && shield.maxShield() > 0) {
        startBerserking();
        ActionIndicator.clearAction(this);
    } else {
        GLog.w(Messages.get(this, "no_seal"));
    }
}
```
**功能**：玩家点击动作按钮时执行，检查是否有战士印章后激活狂暴

---

### Buff显示相关方法

#### icon()
```java
@Override
public int icon() {
    return BuffIndicator.BERSERK;
}
```
**功能**：返回Buff图标ID

#### tintIcon(Image icon)
```java
@Override
public void tintIcon(Image icon) {
    switch (state){
        case NORMAL: default:
            if (power < 1f) icon.hardlight(1f, 0.5f, 0f);  // 橙色
            else            icon.hardlight(1f, 0f, 0f);    // 红色
            break;
        case BERSERK:
            icon.hardlight(1f, 0f, 0f);                    // 红色
            break;
        case RECOVERING:
            icon.hardlight(0, 0, 1f);                      // 蓝色
            break;
    }
}
```
**功能**：根据状态为图标着色

#### iconFadePercent()
```java
@Override
public float iconFadePercent() {
    switch (state){
        case NORMAL: default:
            float maxPower = 1f + 0.1667f*((Hero)target).pointsInTalent(Talent.ENDLESS_RAGE);
            return (maxPower - power)/maxPower;
        case BERSERK:
            return 1f - shielding() / (float)maxShieldBoost();
        case RECOVERING:
            if (levelRecovery > 0) {
                return levelRecovery/(LEVEL_RECOVER_START-Dungeon.hero.pointsInTalent(Talent.DEATHLESS_FURY));
            } else {
                return turnRecovery/(float)TURN_RECOVERY_START;
            }
    }
}
```
**功能**：返回图标淡出百分比，用于显示进度

#### iconTextDisplay()
```java
public String iconTextDisplay(){
    switch (state){
        case NORMAL: default:
            return (int)(power*100) + "%";           // 显示怒气百分比
        case BERSERK:
            return Integer.toString(shielding());    // 显示护盾值
        case RECOVERING:
            if (levelRecovery > 0) {
                return Messages.decimalFormat("#.##", levelRecovery);  // 显示等级恢复进度
            } else {
                return Integer.toString(turnRecovery);  // 显示回合数
            }
    }
}
```
**功能**：返回图标上显示的文本

---

### name() 和 desc()

```java
@Override
public String name() {
    switch (state){
        case NORMAL: default:
            return Messages.get(this, "angered");      // "愤怒"
        case BERSERK:
            return Messages.get(this, "berserk");      // "狂暴"
        case RECOVERING:
            return Messages.get(this, "recovering");   // "恢复中"
    }
}

@Override
public String desc() {
    float dispDamage = ((int)damageFactor(10000) / 100f) - 100f;
    switch (state){
        case NORMAL: default:
            return Messages.get(this, "angered_desc", Math.floor(power * 100f), dispDamage, currentShieldBoost());
        case BERSERK:
            return Messages.get(this, "berserk_desc", shielding());
        case RECOVERING:
            if (levelRecovery > 0){
                return Messages.get(this, "recovering_desc") + "\n\n" + Messages.get(this, "recovering_desc_levels", levelRecovery);
            } else {
                return Messages.get(this, "recovering_desc") + "\n\n" + Messages.get(this, "recovering_desc_turns", turnRecovery);
            }
    }
}
```
**功能**：返回Buff的显示名称和描述文本

---

## 11. 使用示例

### 1. 获取并使用Berserk实例

```java
// 在Hero.defenseProc()中 - 受伤时积累怒气
if (damage > 0 && subClass == HeroSubClass.BERSERKER){
    Berserk berserk = Buff.affect(this, Berserk.class);
    berserk.damage(damage);  // 传入受到的伤害
}
```

### 2. 计算伤害加成

```java
// 在Char.attackProc()中 - 应用伤害加成
Berserk berserk = buff(Berserk.class);
if (berserk != null) {
    dmg = berserk.damageFactor(dmg);  // 应用怒气伤害加成
}
```

### 3. 检查狂暴状态（用于判断存活）

```java
// 在Hero.isAlive()中 - 允许狂暴中的英雄存活
@Override
public boolean isAlive() {
    if (HP <= 0){
        if (berserk == null) berserk = buff(Berserk.class);
        return berserk != null && berserk.berserking();  // 狂暴状态下视为存活
    } else {
        berserk = null;
        return super.isAlive();
    }
}
```

### 4. 恢复狂暴状态

```java
// 在Hero.onGainExp()中 - 通过经验恢复
Berserk berserk = buff(Berserk.class);
if (berserk != null) {
    berserk.recover(percent);  // 传入获得的经验百分比
}
```

### 5. 应用附魔加成

```java
// 在Weapon.genericProcChanceMultiplier()中
Berserk rage = attacker.buff(Berserk.class);
if (rage != null) {
    multi = rage.enchantFactor(multi);  // 增加附魔触发概率
}
```

---

## 注意事项

### 1. 护盾优先级
Berserk护盾的`shieldUsePriority = -1`，这意味着：
- **所有其他护盾会优先被消耗**
- Berserk护盾是最后被消耗的"保险"护盾

### 2. 状态持久性
- `detachesAtZero = false`：护盾消失不会导致Buff分离
- 即使怒气归零，需要手动detach或通过act()自然分离

### 3. 死亡检测
- 狂暴状态下如果护盾消失且HP≤0，会立即死亡
- `berserking()`返回false后，`isAlive()`将返回false

### 4. 恢复机制差异
| 触发方式 | 恢复方式 | 基础时间 |
|----------|----------|----------|
| 主动激活 | 等待回合 | 100回合 |
| 濒死触发 | 获得经验 | 4级经验 |

### 5. HoldFast天赋交互
- HoldFast天赋可以减缓护盾衰减
- 1级：衰减50%，2级：衰减75%，3级：完全不衰减

### 6. 动作指示器
- 怒气达到100%时自动显示动作按钮
- 怒气低于100%或狂暴激活后清除按钮

---

## 最佳实践

### 1. 怒气管理
```java
// 最佳激活时机：低血量时激活可获得更多护盾
// 公式：护盾倍率 = 1 + 2 × (1 - HP/HT)³
// 在37%血量时激活可获得1.5倍护盾
```

### 2. 天赋协同
- **无尽之怒**：提高怒气上限，增加护盾量，减少恢复时间
- **不死之怒**：保命核心天赋，建议优先点满
- **狂暴催化剂**：与高附魔概率武器配合效果最佳

### 3. 装备搭配
```java
// 护甲强化等级直接影响基础护盾
// 公式：基础护盾 = 8 + 2 × 护甲强化等级
// +10护甲可提供28点基础护盾
```

### 4. 避免误操作
- 不要在满血时激活狂暴（护盾效率最低）
- 狂暴结束后需要等待恢复，不要在Boss战前浪费
- 死后狂暴需要获得经验才能恢复，确保有经验来源

---

## 相关文件

- [`ShieldBuff.java`](./ShieldBuff.md) - 护盾Buff基类
- [`Buff.java`](./Buff.md) - Buff基类
- [`Hero.java`](../hero/Hero.md) - 英雄类（主要调用者）
- [`Talent.java`](../hero/Talent.md) - 天赋系统
- [`ActionIndicator.java`](../../ui/ActionIndicator.md) - 动作指示器UI
- [`HoldFast.java`](./HoldFast.md) - 坚守天赋（护盾衰减减缓）
- [`Regeneration.java`](./Regeneration.md) - 再生系统