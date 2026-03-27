# ChampionEnemy 精英敌人强化Buff

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **类名** | `ChampionEnemy` |
| **包路径** | `com.shatteredpixel.shatteredpixeldungeon.actors.buffs` |
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/ChampionEnemy.java` |
| **继承关系** | `extends Buff` |
| **类类型** | `abstract class` (抽象类) |
| **修饰符** | `public abstract` |
| **代码行数** | 306行 |

---

## 类职责

`ChampionEnemy` 是精英敌人系统的核心抽象类，用于为普通敌人添加特殊的强化效果。

### 核心职责：

1. **精英敌人生成机制** - 通过 `rollForChampion()` 方法控制精英敌人的随机生成
2. **Buff效果抽象定义** - 定义了精英敌人共有的属性和方法接口
3. **视觉表现管理** - 统一管理精英敌人的颜色标识和光环特效
4. **战斗属性增强** - 提供伤害、防御、闪避等属性的增强接口

### 六种精英类型：

| 类型 | 类名 | 颜色 | 核心特性 |
|------|------|------|----------|
| 烈焰 | `Blazing` | 橙色 `0xFF8800` | 点燃敌人、死亡产生火焰 |
| 投射 | `Projecting` | 紫色 `0x8800FF` | 4格远程攻击 |
| 抗魔 | `AntiMagic` | 绿色 `0x00FF00` | 50%魔法减伤 |
| 巨型 | `Giant` | 蓝色 `0x0088FF` | 2格近战范围、80%减伤 |
| 祝福 | `Blessed` | 黄色 `0xFFFF00` | 4倍闪避和精准 |
| 成长 | `Growing` | 红色 `0xFF2222` | 属性随时间增长 |

---

## 4. 继承与协作关系

```
┌─────────────────────────────────────────────────────────────┐
│                         Buff (父类)                          │
│  - type, revivePersists                                      │
│  + icon(), tintIcon(), fx(), detach()                        │
└─────────────────────────┬───────────────────────────────────┘
                          │ extends
                          ▼
┌─────────────────────────────────────────────────────────────┐
│               ChampionEnemy (抽象类)                         │
│  # color: int              // 光环颜色                       │
│  # rays: int               // 光环射线数                      │
│  + icon(): int                                              │
│  + tintIcon(Image): void                                    │
│  + fx(boolean): void                                        │
│  + onAttackProc(Char): void                                 │
│  + canAttackWithExtraReach(Char): boolean                   │
│  + meleeDamageFactor(): float                               │
│  + damageTakenFactor(): float                               │
│  + evasionAndAccuracyFactor(): float                        │
│  + rollForChampion(Mob): void  [静态方法]                    │
└─────────────────────────┬───────────────────────────────────┘
                          │ extends
          ┌───────────────┼───────────────┬───────────────┬───────┬──────────┐
          ▼               ▼               ▼               ▼       ▼          ▼
    ┌──────────┐   ┌───────────┐   ┌──────────┐   ┌────────┐ ┌────────┐ ┌────────┐
    │ Blazing  │   │Projecting │   │AntiMagic │   │ Giant  │ │Blessed │ │Growing │
    │ 烈焰精英  │   │ 投射精英   │   │ 抗魔精英  │   │巨型精英│ │祝福精英│ │成长精英│
    └──────────┘   └───────────┘   └──────────┘   └────────┘ └────────┘ └────────┘
```

---

## 静态常量表

本类无显式定义的静态常量。使用到的常量值如下：

| 常量位置 | 值 | 用途 |
|----------|-----|------|
| `BuffIndicator.CORRUPT` | icon返回值 | Buff图标索引 |
| 精英类型数量 | `6` | `Random.Int(6)` 的范围 |
| 初始计数器 | `8` | `mobsToChampion` 基础值 |

---

## 实例字段表

### 父类初始化块字段

```java
{
    type = buffType.POSITIVE;    // Buff类型：正面效果
    revivePersists = true;        // 复活后保留Buff
}
```

### 实例字段

| 字段名 | 类型 | 修饰符 | 默认值 | 说明 |
|--------|------|--------|--------|------|
| `color` | `int` | `protected` | 子类定义 | 光环颜色 (ARGB格式) |
| `rays` | `int` | `protected` | 子类定义 | 光环射线数量 |

### 各子类字段定义

| 子类 | color | rays | 额外字段 |
|------|-------|------|----------|
| `Blazing` | `0xFF8800` | 4 | - |
| `Projecting` | `0x8800FF` | 4 | - |
| `AntiMagic` | `0x00FF00` | 5 | - |
| `Giant` | `0x0088FF` | 5 | - |
| `Blessed` | `0xFFFF00` | 6 | - |
| `Growing` | `0xFF2222` | 6 | `multiplier: float = 1.19f` |

---

## 7. 方法详解

### 1. 父类初始化块

```java
{
    type = buffType.POSITIVE;
    revivePersists = true;
}
```
- **行46-49**: 实例初始化块
- `type = buffType.POSITIVE`: 将此Buff标记为正面效果，影响UI显示和某些机制
- `revivePersists = true`: 当敌人复活时（如通过腐化杖），此Buff会保留

---

### 2. icon() 方法

```java
@Override
public int icon() {
    return BuffIndicator.CORRUPT;
}
```
- **行54-57**: 返回Buff图标索引
- 使用 `CORRUPT` 图标（腐化图标）作为精英敌人的统一标识
- 所有子类共享同一图标，通过颜色区分类型

---

### 3. tintIcon() 方法

```java
@Override
public void tintIcon(Image icon) {
    icon.hardlight(color);
}
```
- **行59-62**: 为Buff图标着色
- 使用 `hardlight()` 方法应用颜色滤镜
- `color` 由各子类定义，实现不同精英类型的视觉区分

---

### 4. fx() 方法

```java
@Override
public void fx(boolean on) {
    if (on) target.sprite.aura(color, rays);
    else target.sprite.clearAura();
}
```
- **行64-68**: 控制精英敌人的视觉特效
- **参数 `on`**:
  - `true`: Buff生效时，为目标精灵添加光环效果
  - `false`: Buff移除时，清除光环效果
- `aura(color, rays)`: 创建指定颜色和射线数的光环动画

---

### 5. onAttackProc() 方法

```java
public void onAttackProc(Char enemy) {
    // 空实现，子类可覆盖
}
```
- **行70-72**: 攻击触发回调
- **参数 `enemy`**: 被攻击的目标
- 默认空实现，由子类覆盖以添加攻击时的特殊效果
- 示例：`Blazing` 覆盖此方法点燃敌人

---

### 6. canAttackWithExtraReach() 方法

```java
public boolean canAttackWithExtraReach(Char enemy) {
    return false;
}
```
- **行74-76**: 判断是否可以远程攻击
- **参数 `enemy`**: 潜在攻击目标
- **返回**: 默认 `false`，表示无额外攻击范围
- 由 `Projecting` 和 `Giant` 子类覆盖实现远程攻击

---

### 7. meleeDamageFactor() 方法

```java
public float meleeDamageFactor() {
    return 1f;
}
```
- **行78-80**: 近战伤害倍率
- **返回**: 伤害乘数，默认 `1.0` (100%)
- 子类覆盖以修改近战伤害：
  - `Blazing`: `1.25f` (+25%伤害)
  - `Projecting`: `1.25f` (+25%伤害)
  - `Growing`: 随时间增长的 `multiplier`

---

### 8. damageTakenFactor() 方法

```java
public float damageTakenFactor() {
    return 1f;
}
```
- **行82-84**: 受伤倍率
- **返回**: 受伤乘数，默认 `1.0` (100%)
- 子类覆盖以提供减伤：
  - `AntiMagic`: `0.5f` (减伤50%)
  - `Giant`: `0.2f` (减伤80%)
  - `Growing`: `1f/multiplier` (随时间增强减伤)

---

### 9. evasionAndAccuracyFactor() 方法

```java
public float evasionAndAccuracyFactor() {
    return 1f;
}
```
- **行86-88**: 闪避和精准倍率
- **返回**: 倍数，默认 `1.0` (100%)
- 同时影响闪避率和命中率
- `Blessed` 覆盖为 `4f` (400%)
- `Growing` 覆盖为动态 `multiplier`

---

### 10. 免疫初始化块

```java
{
    immunities.add(AllyBuff.class);
}
```
- **行90-92**: 添加免疫类型
- 所有精英敌人免疫 `AllyBuff`（盟友Buff）
- 防止精英敌人被转化为盟友

---

### 11. rollForChampion() 静态方法 (核心机制)

```java
public static void rollForChampion(Mob m) {
    Dungeon.mobsToChampion--;

    // 随机选择精英类型
    Class<?extends ChampionEnemy> buffCls;
    switch (Random.Int(6)) {
        case 0: default:    buffCls = Blazing.class;      break;
        case 1:             buffCls = Projecting.class;   break;
        case 2:             buffCls = AntiMagic.class;    break;
        case 3:             buffCls = Giant.class;        break;
        case 4:             buffCls = Blessed.class;      break;
        case 5:             buffCls = Growing.class;      break;
    }

    if (Dungeon.mobsToChampion <= 0 && Dungeon.isChallenged(Challenges.CHAMPION_ENEMIES)) {
        // 低层限制：阻止特定敌人早期成为精英
        if (m instanceof Crab  && Dungeon.scalingDepth() <= 3) return;
        if (m instanceof Thief && Dungeon.scalingDepth() <= 4) return;
        if (m instanceof Guard && Dungeon.scalingDepth() <= 7) return;
        if (m instanceof Bat   && Dungeon.scalingDepth() <= 9) return;

        Buff.affect(m, buffCls);
        // 精英出现间隔随深度增加而缩短
        Dungeon.mobsToChampion += 8 - Math.min(20, Dungeon.scalingDepth()-1)/10f;
        if (m.state != m.PASSIVE) {
            m.state = m.WANDERING;
        }
    }
}
```

**详细行解析：**

| 行号 | 代码 | 说明 |
|------|------|------|
| 94 | `public static void rollForChampion(Mob m)` | 静态方法，每次敌人生成时调用 |
| 95 | `Dungeon.mobsToChampion--` | 递减精英计数器 |
| 97-98 | 注释 | 即使不生成精英也要保持RNG调用一致性 |
| 100-107 | `switch (Random.Int(6))` | 随机选择6种精英类型之一 |
| 109 | `if (Dungeon.mobsToChampion <= 0 && ...)` | 检查是否应该生成精英 |
| 112-115 | `if (m instanceof ...)` | 低层精英限制检查 |
| 117 | `Buff.affect(m, buffCls)` | 为敌人添加精英Buff |
| 119 | `Dungeon.mobsToChampion += ...` | 重置计数器，深度越高间隔越短 |
| 120-122 | `m.state = m.WANDERING` | 激活非被动状态的精英 |

**精英生成概率机制：**
- 基础间隔：每8个敌人中出现1个精英
- 深度影响：`8 - min(20, depth-1)/10`
  - 深度1: `8 - 0 = 8` (每8敌1精英)
  - 深度11: `8 - 1 = 7` (每7敌1精英)
  - 深度21: `8 - 2 = 6` (每6敌1精英)

---

## 子类详细解析

### Blazing (烈焰精英)

```java
public static class Blazing extends ChampionEnemy {
    {
        color = 0xFF8800;  // 橙色
        rays = 4;
    }

    @Override
    public void onAttackProc(Char enemy) {
        // 水中不会被点燃
        if (!Dungeon.level.water[enemy.pos]) {
            Buff.affect(enemy, Burning.class).reignite(enemy);
        }
    }

    @Override
    public void detach() {
        // 死亡时产生火焰（除非掉入深渊）
        if (target.flying || !Dungeon.level.pit[target.pos]) {
            for (int i : PathFinder.NEIGHBOURS9) {
                if (!Dungeon.level.solid[target.pos + i] && 
                    !Dungeon.level.water[target.pos + i]) {
                    GameScene.add(Blob.seed(target.pos + i, 2, Fire.class));
                }
            }
        }
        super.detach();
    }

    @Override
    public float meleeDamageFactor() {
        return 1.25f;  // +25% 近战伤害
    }

    {
        immunities.add(Burning.class);  // 免疫燃烧
    }
}
```

**特性总结：**
- ✅ 近战伤害 +25%
- ✅ 攻击时点燃敌人（水中除外）
- ✅ 死亡时在周围3x3区域产生火焰
- ✅ 免疫燃烧状态
- ✅ 掉入深渊时不产生火焰

---

### Projecting (投射精英)

```java
public static class Projecting extends ChampionEnemy {
    {
        color = 0x8800FF;  // 紫色
        rays = 4;
    }

    @Override
    public float meleeDamageFactor() {
        return 1.25f;  // +25% 近战伤害
    }

    @Override
    public boolean canAttackWithExtraReach(Char enemy) {
        if (Dungeon.level.distance(target.pos, enemy.pos) > 4) {
            return false;
        }
        // 寻路检查：确保4格内有可行路径
        boolean[] passable = BArray.not(Dungeon.level.solid, null);
        for (Char ch : Actor.chars()) {
            passable[ch.pos] = ch == target;  // 自身位置可通行
        }
        PathFinder.buildDistanceMap(enemy.pos, passable, 4);
        return PathFinder.distance[target.pos] <= 4;
    }
}
```

**特性总结：**
- ✅ 近战伤害 +25%
- ✅ 4格远程攻击能力
- ✅ 使用寻路算法验证可达性
- ⚠️ 仍然属于近战攻击（触发近战相关效果）

---

### AntiMagic (抗魔精英)

```java
public static class AntiMagic extends ChampionEnemy {
    {
        color = 0x00FF00;  // 绿色
        rays = 5;
    }

    @Override
    public float damageTakenFactor() {
        return 0.5f;  // 减伤50%
    }

    {
        // 继承抗魔符文的魔法抗性列表
        immunities.addAll(com.dustedpixel.dustedpixeldungeon.items.armor.glyphs.AntiMagic.RESISTS);
    }
}
```

**特性总结：**
- ✅ 魔法伤害减半（50%减伤）
- ✅ 免疫所有魔法效果（来自AntiMagic.RESISTS列表）
- ❌ 无近战伤害加成

---

### Giant (巨型精英)

```java
// Also makes target large, see Char.properties()
public static class Giant extends ChampionEnemy {
    {
        color = 0x0088FF;  // 蓝色
        rays = 5;
    }

    @Override
    public float damageTakenFactor() {
        return 0.2f;  // 减伤80%
    }

    @Override
    public boolean canAttackWithExtraReach(Char enemy) {
        if (Dungeon.level.distance(target.pos, enemy.pos) > 2) {
            return false;
        }
        // 2格寻路检查
        boolean[] passable = BArray.not(Dungeon.level.solid, null);
        for (Char ch : Actor.chars()) {
            passable[ch.pos] = ch == target;
        }
        PathFinder.buildDistanceMap(enemy.pos, passable, 2);
        return PathFinder.distance[target.pos] <= 2;
    }
}
```

**特性总结：**
- ✅ 减伤80%（仅受20%伤害）
- ✅ 2格近战攻击范围
- ✅ 使目标变为"大型"（见 `Char.properties()`）
- ❌ 无近战伤害加成

---

### Blessed (祝福精英)

```java
public static class Blessed extends ChampionEnemy {
    {
        color = 0xFFFF00;  // 黄色
        rays = 6;
    }

    @Override
    public float evasionAndAccuracyFactor() {
        return 4f;  // 4倍闪避和精准
    }
}
```

**特性总结：**
- ✅ 闪避率 ×4
- ✅ 命中率 ×4
- ❌ 无伤害加成
- ❌ 无减伤效果
- ⚠️ 光环射线数最多（6条）

---

### Growing (成长精英)

```java
public static class Growing extends ChampionEnemy {
    {
        color = 0xFF2222;  // 红色
        rays = 6;
    }

    private float multiplier = 1.19f;  // 初始倍率

    @Override
    public boolean act() {
        multiplier += 0.01f;  // 每次行动增加1%
        spend(4 * TICK);       // 4游戏刻度执行一次
        return true;
    }

    @Override
    public float meleeDamageFactor() {
        return multiplier;
    }

    @Override
    public float damageTakenFactor() {
        return 1f / multiplier;  // 受伤率随成长降低
    }

    @Override
    public float evasionAndAccuracyFactor() {
        return multiplier;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", 
            (int)(100 * (multiplier - 1)),           // 伤害加成百分比
            (int)(100 * (1 - 1f / multiplier)));     // 减伤百分比
    }

    private static final String MULTIPLIER = "multiplier";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(MULTIPLIER, multiplier);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        multiplier = bundle.getFloat(MULTIPLIER);
    }
}
```

**特性总结：**
- ✅ 每4游戏刻度，所有属性增长1%
- ✅ 初始属性：+19%伤害、-16%受伤、+19%闪避/命中
- ✅ 属性无限增长，后期极其危险
- ✅ 支持序列化（保存/加载游戏时保留成长进度）

**成长计算示例：**
| 游戏刻度 | multiplier | 伤害加成 | 减伤率 | 闪避/命中加成 |
|----------|------------|----------|--------|--------------|
| 0 (初始) | 1.19 | +19% | 16% | +19% |
| 4 | 1.20 | +20% | 17% | +20% |
| 40 | 1.29 | +29% | 22% | +29% |
| 100 | 1.44 | +44% | 31% | +44% |

---

## 11. 使用示例

### 1. 在敌人生成时调用

```java
// 在 Mob.spawn() 或类似方法中
public static Mob spawn(Class<? extends Mob> mobClass, int pos) {
    Mob m = (Mob) Reflection.newInstance(mobClass);
    // ... 其他生成逻辑 ...
    
    // 尝试生成精英
    ChampionEnemy.rollForChampion(m);
    
    return m;
}
```

### 2. 手动为敌人添加精英Buff

```java
// 直接为敌人添加特定精英类型
Mob enemy = new Crab();
Buff.affect(enemy, ChampionEnemy.Blazing.class);  // 烈焰精英

// 或添加成长精英
Buff.affect(enemy, ChampionEnemy.Growing.class);
```

### 3. 检查敌人是否为精英

```java
public boolean isChampionEnemy(Char ch) {
    // 检查是否有任意精英Buff
    for (ChampionEnemy buff : ch.buffs(ChampionEnemy.class)) {
        if (buff != null) return true;
    }
    return false;
}

// 检查特定精英类型
public boolean isBlazing(Char ch) {
    return ch.buff(ChampionEnemy.Blazing.class) != null;
}
```

### 4. 获取精英属性加成

```java
// 计算最终伤害
public float calculateDamage(Mob attacker, Char defender) {
    float baseDamage = attacker.damageRoll();
    
    // 应用精英近战伤害加成
    for (ChampionEnemy buff : attacker.buffs(ChampionEnemy.class)) {
        baseDamage *= buff.meleeDamageFactor();
    }
    
    // 应用精英减伤
    for (ChampionEnemy buff : defender.buffs(ChampionEnemy.class)) {
        baseDamage *= buff.damageTakenFactor();
    }
    
    return baseDamage;
}
```

### 5. 处理精英攻击范围

```java
public boolean canAttack(Mob attacker, Char target) {
    int distance = Dungeon.level.distance(attacker.pos, target.pos);
    
    // 标准近战范围
    if (distance <= 1) return true;
    
    // 检查精英额外攻击范围
    for (ChampionEnemy buff : attacker.buffs(ChampionEnemy.class)) {
        if (buff.canAttackWithExtraReach(target)) {
            return true;
        }
    }
    
    return false;
}
```

---

## 注意事项

### 1. 精英生成条件

- **挑战开关必须启用**: `Challenges.CHAMPION_ENEMIES`
- **计数器机制**: `Dungeon.mobsToChampion` 控制生成频率
- **低层限制**: 某些敌人类型在低层不会成为精英

### 2. 性能考虑

```java
// rollForChampion 每次敌人生成都调用
// 即使不生成精英，也要保持RNG一致性
switch (Random.Int(6)) { ... }  // 始终消耗一个随机数
```

### 3. 免疫链

所有精英敌人具有以下免疫：
- `AllyBuff.class` - 不能被转化为盟友

特定精英额外免疫：
- `Blazing`: `Burning.class`
- `AntiMagic`: 所有魔法效果（来自AntiMagic.RESISTS）

### 4. Giant 特殊标记

```java
// Giant 还会修改目标的大小属性
// 参见 Char.properties() 中的处理
```

### 5. Growing 序列化

成长精英的 `multiplier` 会保存/加载：
```java
// 确保游戏存档时正确保存成长进度
@Override
public void storeInBundle(Bundle bundle) { ... }
@Override
public void restoreFromBundle(Bundle bundle) { ... }
```

### 6. 视觉效果

- 精英敌人使用 `CORRUPT` 图标 + 颜色区分
- 光环效果通过 `target.sprite.aura()` 实现
- `rays` 参数影响光环的射线数量（4-6）

---

## 最佳实践

### 1. 添加新的精英类型

```java
public static class MyCustomChampion extends ChampionEnemy {
    {
        color = 0xABCDEF;  // 自定义颜色
        rays = 5;
    }
    
    @Override
    public float meleeDamageFactor() {
        return 1.5f;  // 自定义伤害加成
    }
    
    // 添加自定义免疫
    {
        immunities.add(Poison.class);
    }
}
```

然后在 `rollForChampion` 中添加：
```java
switch (Random.Int(7)) {  // 更新数量
    case 6: buffCls = MyCustomChampion.class; break;
    // ... 其他case
}
```

### 2. 自定义攻击触发

```java
@Override
public void onAttackProc(Char enemy) {
    // 添加自定义攻击效果
    if (Random.Float() < 0.25f) {
        Buff.affect(enemy, Paralysis.class, 2f);
    }
}
```

### 3. 动态属性计算

```java
@Override
public float meleeDamageFactor() {
    // 基于条件的动态加成
    float base = 1f;
    if (target.HP < target.HT * 0.5f) {
        base += 0.5f;  // 低血量时额外加成
    }
    return base;
}
```

### 4. 调整精英出现频率

```java
// 在 Dungeon 或自定义位置修改
Dungeon.mobsToChampion = 5;  // 更频繁出现精英

// 或修改 rollForChampion 中的公式
Dungeon.mobsToChampion += 6 - Math.min(20, Dungeon.scalingDepth()-1)/10f;
```

### 5. 防止特定敌人成为精英

```java
// 在 rollForChampion 中添加
if (m instanceof Boss) return;  // Boss不能成为精英
if (m.properties().contains(Char.Property.MINIBOSS)) return;
```

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `Buff` | 父类 | 所有Buff的基类 |
| `Mob` | 目标 | 精英Buff的应用对象 |
| `Char` | 关联 | 角色基类，提供属性接口 |
| `Burning` | 效果 | Blazing点燃效果 |
| `Fire` | 效果 | Blazing死亡产生的火焰 |
| `Challenges` | 配置 | 精英敌人生成的挑战开关 |
| `BuffIndicator` | UI | Buff图标显示 |
| `PathFinder` | 工具 | 投射/巨型精英的寻路计算 |
| `BArray` | 工具 | 布尔数组操作工具 |

---

## 版本历史

| 版本 | 变更 |
|------|------|
| 原版 | 基础精英敌人系统实现 |
| 当前 | 6种精英类型完整实现 |

---

*文档生成时间: 2026-03-26*  
*源码版本: Shattered Pixel Dungeon*