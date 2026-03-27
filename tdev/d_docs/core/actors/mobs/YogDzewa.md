# YogDzewa - 古神 Yog-Dzewa (最终Boss)

> **文件路径**: `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/YogDzewa.java`  
> **BOSS等级**: 第五关Boss (恶魔深渊 - 矮人都城底层)  
> **继承关系**: `Mob` → `YogDzewa`

---

## 一、基本信息

| 属性 | 数值 | 说明 |
|------|------|------|
| **HP/HT** | 1000 | 生命值上限 (固定) |
| **EXP** | 50 | 击杀经验值 |
| **防御技能** | N/A | 使用无限命中率 |
| **视野距离** | 12 | 高于普通怪物 |
| **命中率** | `INFINITE_ACCURACY` | 必中攻击 |
| **属性标签** | `Property.BOSS`, `Property.IMMOVABLE`, `Property.DEMONIC`, `Property.STATIC` | Boss、不可移动、恶魔、静态 |

### 战斗阶段

| 阶段 | 触发条件 | 召唤的拳头 | 视野距离 |
|------|----------|------------|----------|
| **Phase 0** | 初始状态 | 无 | 正常 |
| **Phase 1** | HP ≤ 700 | 第1个拳头 | 4格 |
| **Phase 2** | HP ≤ 400 | 第2个拳头 | 3格 |
| **Phase 3** | HP ≤ 100 | 第3个拳头 | 2格 |
| **Phase 4** | HP = 100 | 等待拳头全灭 | 1格 |
| **Phase 5** | 所有拳头死亡 | 最终阶段 | 1格 (狂暴) |

---

## 二、类职责

`YogDzewa` 类实现了游戏中最终Boss战逻辑，负责：

1. **多阶段战斗系统** - 5个阶段，血量阈值触发阶段转换
2. **拳头召唤机制** - 每阶段召唤1个YogFist（强敌挑战模式召唤2个）
3. **魔能光束攻击** - 发射死亡射线，伤害高且可追踪
4. **随从召唤系统** - 召唤Larva、YogRipper、YogEye、YogScorpio
5. **视野限制机制** - 随阶段推进逐渐降低玩家视野
6. **无敌状态管理** - 存在拳头时免疫伤害
7. **Boss分数统计** - 死亡时计算挑战得分

---

## 三、类关系图

```
┌─────────────────────────────────────────────────────────────────┐
│                         YogDzewa                                 │
│  (最终Boss - 古神 Yog-Dzewa)                                     │
├─────────────────────────────────────────────────────────────────┤
│ 继承:                                                            │
│   Mob → YogDzewa                                                 │
├─────────────────────────────────────────────────────────────────┤
│ 关联类:                                                          │
│   YogFist             - 拳头（6种类型）                          │
│   BossHealthBar       - Boss血条UI                               │
│   LockedFloor         - 锁层机制                                 │
│   HallsBossLevel      - 关卡控制                                 │
│   Music               - 背景音乐控制                              │
├─────────────────────────────────────────────────────────────────┤
│ 内部类 (随从):                                                   │
│   Larva               - 古神幼虫 (弱小但数量多)                   │
│   YogRipper           - 撕裂魔 (继承RipperDemon)                 │
│   YogEye              - 魔眼 (继承Eye)                           │
│   YogScorpio          - 蝎魔 (继承Scorpio)                       │
├─────────────────────────────────────────────────────────────────┤
│ 拳头类型 (YogFist子类):                                          │
│   BurningFist         - 燃烧之拳 (火属性)                        │
│   SoiledFist          - 泥土之拳 (草属性)                        │
│   RottingFist         - 腐烂之拳 (毒素属性)                      │
│   RustedFist          - 锈蚀之拳 (金属属性)                      │
│   BrightFist          - 光明之拳 (致盲属性)                      │
│   DarkFist            - 黑暗之拳 (消光属性)                      │
└─────────────────────────────────────────────────────────────────┘
```

---

## 四、静态常量表

### 技能冷却常量

| 常量名 | 值 | 说明 |
|--------|-----|------|
| `MIN_ABILITY_CD` | 10 | 光束技能最小冷却时间 |
| `MAX_ABILITY_CD` | 15 | 光束技能最大冷却时间 |
| `MIN_SUMMON_CD` | 10 | 召唤技能最小冷却时间 |
| `MAX_SUMMON_CD` | 15 | 召唤技能最大冷却时间 |

### Bundle存储键

| 常量名 | 值 | 说明 |
|--------|-----|------|
| `PHASE` | "phase" | 当前阶段 |
| `ABILITY_CD` | "ability_cd" | 光束技能冷却 |
| `SUMMON_CD` | "summon_cd" | 召唤技能冷却 |
| `FIST_SUMMONS` | "fist_summons" | 拳头召唤队列 |
| `REGULAR_SUMMONS` | "regular_summons" | 普通随从召唤队列 |
| `CHALLENGE_SUMMONS` | "challenges_summons" | 强敌挑战拳头队列 |
| `TARGETED_CELLS` | "targeted_cells" | 光束目标格子 |

---

## 五、实例字段表

| 字段名 | 类型 | 初始值 | 说明 |
|--------|------|--------|------|
| `phase` | `int` | `0` | 当前战斗阶段 (0-5) |
| `abilityCooldown` | `float` | 随机 | 光束技能冷却计时器 |
| `summonCooldown` | `float` | 随机 | 召唤技能冷却计时器 |
| `fistSummons` | `ArrayList<Class>` | 随机3个 | 拳头召唤队列 |
| `challengeSummons` | `ArrayList<Class>` | 随机3个 | 强敌模式拳头队列 |
| `regularSummons` | `ArrayList<Class>` | 4-6个 | 普通随从队列 |
| `targetedCells` | `ArrayList<Integer>` | 空 | 光束目标格子列表 |

### 拳头配对规则

```java
// 拳头按配对出现，同一配对不会同时出现在同一轮
BurningFist ←→ SoiledFist    // 火-土配对
RottingFist ←→ RustedFist    // 腐-金配对  
BrightFist  ←→ DarkFist      // 光-暗配对
```

### 随从生成规则

**普通模式**:
```java
// 4个随从
if (spawnersAlive > 0) → Larva    // 有恶魔雕像存活时召唤幼虫
else → YogRipper                  // 否则召唤撕裂魔
```

**强敌挑战模式** (`Challenges.STRONGER_BOSSES`):
```java
// 6个随从
if (i >= 4) → YogRipper           // 后2个是撕裂魔
else if (spawnersAlive > 0) → Larva // 有雕像时召唤幼虫
else → YogEye/YogScorpio (交替)   // 否则召唤魔眼/蝎魔
```

---

## 六、方法详解

### 6.1 核心属性方法

#### `attackSkill(Char target)`
```java
@Override
public int attackSkill(Char target) {
    return INFINITE_ACCURACY;  // 无限命中率，光束必中
}
```
**职责**: 返回无限命中率，确保光束攻击无法闪避。

---

### 6.2 主行为逻辑 (`act()`)

#### Phase 0 - 待机状态

```java
if (phase == 0){
    // 等待玩家发现
    if (Dungeon.level.heroFOV[pos]) {
        notice();  // 触发Boss战开始
    }
    spend(TICK);
    return true;
}
```

#### Phase 1-5 - 战斗状态

**光束攻击流程**:

```java
// 1. 等待玩家解除定身后发射光束
if (!targetedCells.isEmpty() && !Dungeon.hero.rooted) {
    for (int i : targetedCells) {
        Ballistica b = new Ballistica(pos, i, Ballistica.WONT_STOP);
        // 创建死亡射线视觉效果
        sprite.parent.add(new Beam.DeathRay(...));
        
        // 对路径上的角色造成伤害
        for (int p : b.path) {
            Char ch = Actor.findChar(p);
            if (ch != null && ch.alignment != alignment) {
                if (hit(this, ch, true)) {
                    // 强敌模式: 30-50伤害, 普通模式: 20-30伤害
                    ch.damage(Random.NormalIntRange(20, 50), new Eye.DeathGaze());
                }
            }
        }
    }
    targetedCells.clear();
}
```

**光束目标选择**:

```java
// 光束数量随血量降低而增加
int beams = 1 + (HT - HP) / 400;  // HP=1000→1束, HP=600→2束, HP=200→3束

// 主目标始终是英雄位置
int targetPos = Dungeon.hero.pos;

// 额外光束选择英雄周围的随机位置
if (i != 0) {
    targetPos = Dungeon.hero.pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
}
```

**召唤随从逻辑**:

```java
while (summonCooldown <= 0) {
    // 从队列中取出随从类型
    Class<?extends Mob> cls = regularSummons.remove(0);
    Mob summon = Reflection.newInstance(cls);
    regularSummons.add(cls);  // 循环使用
    
    // 在相邻空位生成
    int spawnPos = -1;
    for (int i : PathFinder.NEIGHBOURS8) {
        if (Actor.findChar(pos+i) == null) {
            spawnPos = pos + i;
            break;
        }
    }
    
    // 如果没有空位，尝试杀死绵羊来腾出位置
    if (spawnPos == -1) {
        for (int i : PathFinder.NEIGHBOURS8) {
            if (Actor.findChar(pos+i) instanceof Sheep) {
                Actor.findChar(pos+i).die(null);
                spawnPos = pos + i;
                break;
            }
        }
    }
}
```

**最终阶段加速**:

```java
// Phase 5 时技能冷却大幅缩短
if (phase == 5 && abilityCooldown > 2) {
    abilityCooldown = 2;
}
if (phase == 5 && summonCooldown > 3) {
    summonCooldown = 3;
}
```

---

### 6.3 伤害处理与阶段转换 (`damage()`)

```java
@Override
public void damage(int dmg, Object src) {
    int preHP = HP;
    super.damage(dmg, src);
    
    // 存在拳头时免疫伤害
    if (phase == 0 || findFist() != null) return;
    
    // 血量分段保护（每阶段300血）
    if (phase < 4) {
        HP = Math.max(HP, HT - 300 * phase);
    } else if (phase == 4) {
        HP = Math.max(HP, 100);  // 保持最低100血
    }
    
    // 受伤时缩短技能冷却
    int dmgTaken = preHP - HP;
    if (dmgTaken > 0) {
        abilityCooldown -= dmgTaken / 10f;
        summonCooldown -= dmgTaken / 10f;
    }
    
    // 阶段转换
    if (phase < 4 && HP <= HT - 300*phase) {
        phase++;
        updateVisibility(Dungeon.level);
        GLog.n(Messages.get(this, "darkness"));  // "黑暗正在逼近..."
        
        // 召唤拳头
        addFist(Reflection.newInstance(fistSummons.remove(0)));
        
        // 强敌挑战模式额外召唤一个拳头
        if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
            addFist(Reflection.newInstance(challengeSummons.remove(0)));
        }
    }
}
```

---

### 6.4 拳头死亡处理 (`processFistDeath()`)

```java
public void processFistDeath(){
    // 最后一个拳头死亡时触发最终阶段
    if (phase == 4 && findFist() == null) {
        yell(Messages.get(this, "hope"));  // "汝 之 希 望 皆 为 虚 妄"
        summonCooldown = -15;  // 立即召唤大量随从
        phase = 5;
        BossHealthBar.bleed(true);  // 血条显示出血效果
        
        // 切换到最终战音乐
        Music.INSTANCE.fadeOut(0.5f, new Callback() {
            public void call() {
                Music.INSTANCE.play(Assets.Music.HALLS_BOSS_FINALE, true);
            }
        });
    }
}
```

---

### 6.5 拳头召唤 (`addFist()`)

```java
public void addFist(YogFist fist) {
    fist.pos = Dungeon.level.exit();  // 初始位置在出口
    
    // 特效：暗影粒子爆发
    CellEmitter.get(Dungeon.level.exit()).burst(ShadowParticle.UP, 100);
    
    // 确定最终生成位置
    int targetPos = Dungeon.level.exit() + Dungeon.level.width();
    
    // 优先选择出口下方的位置
    // 强敌模式下可以覆盖绵羊位置
    if (!Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
        if (Actor.findChar(targetPos) == null || Actor.findChar(targetPos) instanceof Sheep) {
            fist.pos = targetPos;
        }
    }
    
    // 如果位置被绵羊占据，杀死绵羊
    if (Actor.findChar(fist.pos) instanceof Sheep) {
        Actor.findChar(fist.pos).die(null);
    }
    
    GameScene.add(fist, 4);
    Actor.add(new Pushing(fist, Dungeon.level.exit(), fist.pos));
}
```

---

### 6.6 视野控制 (`updateVisibility()`)

```java
public void updateVisibility(Level level) {
    int viewDistance = 4;
    
    // 随阶段推进降低视野
    if (phase > 1 && isAlive()) {
        viewDistance = Math.max(4 - (phase-1), 1);
    }
    
    // 黑暗挑战模式下视野更低
    if (Dungeon.isChallenged(Challenges.DARKNESS)) {
        viewDistance = Math.min(viewDistance, 2);
    }
    
    level.viewDistance = viewDistance;
    
    // 如果英雄没有光源buff，更新英雄视野
    if (Dungeon.hero != null && Dungeon.hero.buff(Light.class) == null) {
        Dungeon.hero.viewDistance = level.viewDistance;
        Dungeon.observe();
    }
}
```

**视野距离对照表**:

| 阶段 | 基础视野 | 黑暗挑战视野 |
|------|----------|--------------|
| Phase 0-1 | 4 | 2 |
| Phase 2 | 3 | 2 |
| Phase 3 | 2 | 2 |
| Phase 4-5 | 1 | 1 |

---

### 6.7 无敌判定 (`isInvulnerable()`)

```java
@Override
public boolean isInvulnerable(Class effect) {
    // 以下情况无敌:
    // 1. Phase 0 (战斗未开始)
    // 2. 存在存活的拳头
    return phase == 0 || findFist() != null || super.isInvulnerable(effect);
}
```

---

### 6.8 仇恨管理 (`aggro()`)

```java
@Override
public void aggro(Char ch) {
    // 古神本体不会移动，但会指挥周围4格内的随从攻击目标
    if (ch != null && ch.alignment != alignment) {
        for (Mob mob : Dungeon.level.mobs.clone()) {
            if (mob.alignment == alignment &&
                Dungeon.level.distance(pos, mob.pos) <= 4 &&
                (mob instanceof Larva || mob instanceof YogRipper || 
                 mob instanceof YogEye || mob instanceof YogScorpio)) {
                mob.aggro(ch);
            }
        }
    }
}
```

---

### 6.9 死亡处理 (`die()`)

```java
@Override
public void die(Object cause) {
    // 跳过随从击杀计数
    Bestiary.skipCountingEncounters = true;
    
    // 清除所有随从
    for (Mob mob : Dungeon.level.mobs.clone()) {
        if (mob instanceof Larva || mob instanceof YogRipper || 
            mob instanceof YogEye || mob instanceof YogScorpio) {
            mob.die(cause);
        }
    }
    Bestiary.skipCountingEncounters = false;
    
    // 恢复视野
    updateVisibility(Dungeon.level);
    
    // Boss击杀处理
    GameScene.bossSlain();
    
    // 徽章验证
    if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES) && Statistics.spawnersAlive == 4) {
        Badges.validateBossChallengeCompleted();
    }
    
    // Boss分数计算
    // 基础5000分 + 每个存活的恶魔雕像1250分
    Statistics.bossScores[4] += 5000 + 1250 * Statistics.spawnersAlive;
    
    // 解锁关卡
    Dungeon.level.unseal();
    super.die(cause);
    
    yell(Messages.get(this, "defeated"));  // "..."
}
```

---

### 6.10 发现玩家 (`notice()`)

```java
@Override
public void notice() {
    if (!BossHealthBar.isAssigned()) {
        BossHealthBar.assignBoss(this);
        yell(Messages.get(this, "notice"));  // "我 看 见 你 了"
        
        // 幽灵英雄对话
        for (Char ch : Actor.chars()) {
            if (ch instanceof DriedRose.GhostHero) {
                ((DriedRose.GhostHero) ch).sayBoss();
            }
        }
        
        // 播放Boss音乐
        Music.INSTANCE.play(Assets.Music.HALLS_BOSS, true);
        
        // 进入Phase 1
        if (phase == 0) {
            phase = 1;
            summonCooldown = Random.NormalFloat(MIN_SUMMON_CD, MAX_SUMMON_CD);
            abilityCooldown = Random.NormalFloat(MIN_ABILITY_CD, MAX_ABILITY_CD);
        }
    }
}
```

---

## 七、内部类详解

### 7.1 Larva - 古神幼虫

```java
public static class Larva extends Mob {
    HP = HT = 20;
    defenseSkill = 12;
    EXP = 5;
    maxLvl = -2;  // 不提供经验成长
    
    attackSkill() = 30;
    damageRoll() = Random.NormalIntRange(15, 25);
    drRoll() = Random.NormalIntRange(0, 4);
    
    properties: DEMONIC, BOSS_MINION
}
```

**特点**:
- 血量低（20点）
- 攻击力中等（15-25）
- 生成速度快
- 数量多时难以应对

### 7.2 YogRipper - 撕裂魔

```java
public static class YogRipper extends RipperDemon {
    maxLvl = -2;
    properties: BOSS_MINION
}
```

**特点**: 继承自RipperDemon，拥有撕裂攻击能力

### 7.3 YogEye - 魔眼

```java
public static class YogEye extends Eye {
    maxLvl = -2;
    properties: BOSS_MINION
}
```

**特点**: 继承自Eye，能发射死亡凝视

### 7.4 YogScorpio - 蝎魔

```java
public static class YogScorpio extends Scorpio {
    maxLvl = -2;
    properties: BOSS_MINION
}
```

**特点**: 继承自Scorpio，远程毒刺攻击

---

## 八、使用示例

### 8.1 检查Yog-Dzewa状态

```java
// 查找Yog-Dzewa实例
YogDzewa yog = null;
for (Char c : Actor.chars()) {
    if (c instanceof YogDzewa) {
        yog = (YogDzewa) c;
        break;
    }
}

if (yog != null) {
    int currentPhase = yog.phase;
    YogFist activeFist = yog.findFist();
    boolean isInvuln = yog.isInvulnerable(null);
}
```

### 8.2 手动触发阶段转换

```java
// 强制造成伤害触发阶段转换
yog.damage(300, new Object());  // 触发Phase 1→2
```

### 8.3 检查恶魔雕像状态

```java
// Statistics.spawnersAlive 表示存活的恶魔雕像数量
// 存活越多，Yog召唤的随从越强
int spawners = Statistics.spawnersAlive;

if (spawners > 0) {
    GLog.w("古神会使用上层能量召唤更强大的手下！");
}
```

### 8.4 自定义拳头召唤

```java
// 手动添加特定类型的拳头
YogFist fist = new YogFist.BurningFist();
yog.addFist(fist);
```

---

## 九、注意事项

### 9.1 无敌机制

Yog-Dzewa在以下情况下完全无敌：
- **Phase 0**: 战斗尚未开始
- **存在拳头**: 任何拳头存活时本体无敌

```java
// 必须先消灭拳头才能伤害本体
if (yog.findFist() != null) {
    // 伤害无效
}
```

### 9.2 血量保护机制

每阶段有血量下限保护：
- Phase 1: HP最低700
- Phase 2: HP最低400
- Phase 3: HP最低100
- Phase 4: HP最低100（等待拳头死亡）

```java
// 单次伤害无法跨阶段
if (phase < 4) {
    HP = Math.max(HP, HT - 300 * phase);
}
```

### 9.3 恶魔雕像影响

上层关卡存活的恶魔雕像会影响随从类型：
- `spawnersAlive > 0`: 召唤较弱的Larva
- `spawnersAlive == 0`: 召唤更强的YogRipper/YogEye/YogScorpio

**建议**: 在挑战Yog-Dzewa前先清除所有恶魔雕像以获得更高分数。

### 9.4 强敌挑战模式差异

开启 `Challenges.STRONGER_BOSSES` 时：
- 每阶段召唤**2个拳头**（普通模式1个）
- 召唤**6个随从**（普通模式4个）
- 光束伤害更高（30-50 vs 20-30）
- 锁层时间延长更多

### 9.5 光束攻击

- 英雄被定身时光束不会发射
- 光束可摧毁可燃地形
- 被光束击中会扣除Boss挑战分数（-500分）

### 9.6 绵羊处理

Yog-Dzewa会主动杀死绵羊来腾出召唤位置：
```java
if (Actor.findChar(spawnPos) instanceof Sheep) {
    Actor.findChar(spawnPos).die(null);
}
```

---

## 十、最佳实践

### 10.1 修改战斗难度

```java
// 增加血量
{
    HP = HT = 1500;
}

// 修改阶段阈值
// 原始: 每300血一个阶段
// 自定义: 每250血一个阶段
if (phase < 4 && HP <= HT - 250*phase) {
    phase++;
    // ...
}
```

### 10.2 自定义拳头顺序

```java
// 固定拳头出场顺序
private ArrayList<Class> fistSummons = new ArrayList<>();
{
    fistSummons.add(YogFist.BurningFist.class);   // 第1个
    fistSummons.add(YogFist.RottingFist.class);   // 第2个
    fistSummons.add(YogFist.BrightFist.class);    // 第3个
}
```

### 10.3 修改视野范围

```java
@Override
public void updateVisibility(Level level) {
    // 完全黑暗模式
    level.viewDistance = 0;  // 玩家完全无法看见
    
    // 或者更宽容的模式
    level.viewDistance = Math.max(6 - phase, 3);
}
```

### 10.4 添加自定义随从

```java
// 创建新的随从类型
public static class CustomMinion extends Mob {
    // ...
}

// 在regularSummons中添加
regularSummons.add(CustomMinion.class);
```

### 10.5 序列化处理

```java
@Override
public void storeInBundle(Bundle bundle) {
    super.storeInBundle(bundle);
    bundle.put(PHASE, phase);
    bundle.put(ABILITY_CD, abilityCooldown);
    bundle.put(SUMMON_CD, summonCooldown);
    // 确保所有自定义字段都被保存
}

@Override
public void restoreFromBundle(Bundle bundle) {
    super.restoreFromBundle(bundle);
    phase = bundle.getInt(PHASE);
    abilityCooldown = bundle.getFloat(ABILITY_CD);
    summonCooldown = bundle.getFloat(SUMMON_CD);
    // 恢复Boss血条状态
    if (phase != 0) {
        BossHealthBar.assignBoss(this);
        if (phase == 5) BossHealthBar.bleed(true);
    }
}
```

---

## 十一、调试信息

### 战斗流程图

```
┌─────────────────────────────────────────────────────────────────┐
│                    Yog-Dzewa 战斗流程                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  [Phase 0] ──发现英雄──> [Phase 1]                              │
│    待机                    HP≤700, 召唤拳头1                    │
│                            视野→4格                             │
│                               │                                 │
│                               ▼                                 │
│                          [Phase 2]                              │
│                            HP≤400, 召唤拳头2                    │
│                            视野→3格                             │
│                               │                                 │
│                               ▼                                 │
│                          [Phase 3]                              │
│                            HP≤100, 召唤拳头3                    │
│                            视野→2格                             │
│                               │                                 │
│                               ▼                                 │
│                          [Phase 4]                              │
│                            HP=100 (锁定)                        │
│                            等待所有拳头死亡                     │
│                            视野→1格                             │
│                               │                                 │
│                          拳头全灭                               │
│                               │                                 │
│                               ▼                                 │
│                          [Phase 5] ──HP=0──> [死亡]             │
│                            最终狂暴阶段                         │
│                            技能冷却极短                         │
│                            大量召唤随从                         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 技能冷却公式

```
基础冷却: Random(10, 15)

实际冷却 = 基础冷却 - (phase - 1)

受伤加速: cooldown -= damageTaken / 10

Phase 5加速: cooldown = min(cooldown, 2或3)
```

### Boss分数计算

```
击杀基础分: 5000
每个存活的恶魔雕像: +1250
被光束击中: -500

最高可能分数: 5000 + 4*1250 = 10000
最低分数(无雕像): 5000
```

---

## 十二、拳头详细机制

### 拳头共同特性

| 属性 | 数值 |
|------|------|
| HP/HT | 300 |
| 防御技能 | 20 |
| 攻击技能 | 36 |
| 近战伤害 | 18-36 |
| 伤害减免 | 0-15 |
| 远程冷却 | 8-12回合 |

### 拳头在古神附近无敌

```java
// 拳头在古神周围4格内时免疫伤害
protected boolean isNearYog() {
    int yogPos = Dungeon.level.exit() + 3 * Dungeon.level.width();
    return Dungeon.level.distance(pos, yogPos) <= 4;
}

@Override
public boolean isInvulnerable(Class effect) {
    return isNearYog() || super.isInvulnerable(effect);
}
```

### 六种拳头特性

| 拳头类型 | 特殊能力 | 免疫 |
|----------|----------|------|
| **BurningFist** | 蒸发水格、点燃周围 | Frost |
| **SoiledFist** | 生长草丛、根须束缚 | Burning伤害 |
| **RottingFist** | 毒气攻击、水中回血、伤害转流血 | ToxicGas |
| **RustedFist** | 高伤害(22-44)、残废攻击 | - |
| **BrightFist** | 致盲攻击、半血瞬移、无远程冷却 | - |
| **DarkFist** | 消光攻击、半血瞬移、无远程冷却 | - |

---

*文档版本: 1.0*  
*最后更新: 2026-03-26*  
*基于 Shattered Pixel Dungeon 源码分析*