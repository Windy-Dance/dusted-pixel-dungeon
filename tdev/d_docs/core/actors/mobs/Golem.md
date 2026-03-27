# Golem（魔像）类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **类名** | `Golem` |
| **包路径** | `com.shatteredpixel.shatteredpixeldungeon.actors.mobs` |
| **继承关系** | `extends Mob` |
| **文件位置** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/Golem.java` |
| **代码行数** | 257 行 |
| **出现区域** | 恶魔大厅（Demon Halls，第21-25层） |

---

## 类职责

`Golem` 是游戏中一种高等级敌对怪物，具有以下核心职责：

1. **战斗实体** - 作为恶魔大厅中的强力敌人，拥有高生命值（120 HP）和高伤害输出（25-30）
2. **传送能力** - 具备两种独特的传送机制：
   - **自身传送**：在游荡状态下可传送至目标位置
   - **敌人传送**：在追击状态下可将敌人传送至自己附近
3. **战利品掉落** - 死亡时有概率掉落武器或护甲装备
4. **小鬼任务关联** - 与 Imp.Quest 任务系统交互

---

## 4. 继承与协作关系

```
                    ┌─────────────────┐
                    │     Char        │
                    │   (抽象基类)    │
                    └────────┬────────┘
                             │ extends
                    ┌────────▼────────┐
                    │      Mob        │
                    │   (怪物基类)    │
                    └────────┬────────┘
                             │ extends
                    ┌────────▼────────┐
                    │     Golem       │
                    │   (魔像实现)    │
                    └─────────────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
     ┌────────▼────┐  ┌──────▼──────┐  ┌────▼────┐
     │  Wandering  │  │   Hunting   │  │GolemSprite│
     │ (内部类)    │  │  (内部类)   │  │(精灵类)  │
     └─────────────┘  └─────────────┘  └──────────┘
```

**依赖关系**：
- `GolemSprite` - 负责魔像的视觉表现和传送特效
- `ScrollOfTeleportation` - 提供传送功能实现
- `Imp.Quest` - 小鬼任务处理
- `Generator` - 装备生成器
- `MagicImmune` - 魔法免疫状态检测

---

## 静态常量表

| 常量名 | 类型 | 值 | 用途 |
|--------|------|-----|------|
| `TELEPORTING` | `String` | `"teleporting"` | Bundle 存储键：传送状态标志 |
| `SELF_COOLDOWN` | `String` | `"self_cooldown"` | Bundle 存储键：自身传送冷却 |
| `ENEMY_COOLDOWN` | `String` | `"enemy_cooldown"` | Bundle 存储键：敌人传送冷却 |

---

## 实例字段表

| 字段名 | 类型 | 初始值 | 用途 |
|--------|------|--------|------|
| `spriteClass` | `Class<? extends CharSprite>` | `GolemSprite.class` | 精灵类引用 |
| `HP` | `int` | `120` | 当前生命值 |
| `HT` | `int` | `120` | 最大生命值 |
| `defenseSkill` | `int` | `15` | 防御技能值 |
| `EXP` | `int` | `12` | 击杀经验值 |
| `maxLvl` | `int` | `22` | 提供经验的最大玩家等级 |
| `loot` | `Object` | 随机武器/护甲类别 | 战利品类型 |
| `lootChance` | `float` | `0.2f` | 基础掉落概率 |
| `teleporting` | `boolean` | `false` | 是否正在传送中 |
| `selfTeleCooldown` | `int` | `0` | 自身传送冷却回合数 |
| `enemyTeleCooldown` | `int` | `0` | 敌人传送冷却回合数 |

---

## 7. 方法详解

### 初始化块（第42-59行）

```java
{
    spriteClass = GolemSprite.class;
    
    HP = HT = 120;
    defenseSkill = 15;
    
    EXP = 12;
    maxLvl = 22;

    loot = Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR);
    lootChance = 0.2f;

    properties.add(Property.INORGANIC);
    properties.add(Property.LARGE);

    WANDERING = new Wandering();
    HUNTING = new Hunting();
}
```

**逐行解释**：
- **第43行**：设置精灵类为 `GolemSprite`，负责渲染和动画
- **第45行**：设置生命值为120，属于高血量怪物
- **第46行**：防御技能15，影响被命中的概率
- **第48行**：经验值12，击杀后玩家获得的经验
- **第49行**：最大有效等级22，超过此等级击杀不获得经验
- **第51行**：随机选择武器或护甲作为战利品类别
- **第52行**：初始掉落概率20%
- **第54-55行**：添加 `INORGANIC`（无机物）和 `LARGE`（大型）属性
  - `INORGANIC`：不受某些影响有机物的效果影响
  - `LARGE`：无法进入狭窄空间
- **第57-58行**：用自定义的 AI 状态替换默认的游荡和追击行为

---

### damageRoll()（第62-64行）

```java
@Override
public int damageRoll() {
    return Random.NormalIntRange( 25, 30 );
}
```

**逐行解释**：
- 返回25-30之间的随机伤害值
- 使用正态分布随机数，确保伤害稳定且具有威胁性
- 此伤害值在恶魔大厅属于较高水平

---

### attackSkill()（第66-69行）

```java
@Override
public int attackSkill( Char target ) {
    return 28;
}
```

**逐行解释**：
- 返回固定攻击技能值28
- 此值用于计算命中率，公式涉及攻击方技能与防御方技能的对比
- 28属于较高的攻击技能值，使命中率较高

---

### drRoll()（第71-74行）

```java
@Override
public int drRoll() {
    return super.drRoll() + Random.NormalIntRange(0, 12);
}
```

**逐行解释**：
- 调用父类方法获取基础伤害减免
- 额外添加0-12的随机伤害减免
- 体现魔像的高护甲特性，平均减免约6点伤害

---

### lootChance()（第76-81行）

```java
@Override
public float lootChance() {
    //each drop makes future drops 1/3 as likely
    // so loot chance looks like: 1/5, 1/15, 1/45, 1/135, etc.
    return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.GOLEM_EQUIP.count);
}
```

**逐行解释**：
- 重写掉落概率计算方法
- 每次掉落后，后续掉落概率变为原来的1/3
- 掉落概率序列：20% → 6.67% → 2.22% → 0.74%...
- 使用 `Dungeon.LimitedDrops.GOLEM_EQUIP.count` 追踪已掉落数量
- 防止玩家通过刷魔像获取过多装备

---

### rollToDropLoot()（第83-87行）

```java
@Override
public void rollToDropLoot() {
    Imp.Quest.process( this );
    super.rollToDropLoot();
}
```

**逐行解释**：
- 在掉落判定前调用 `Imp.Quest.process()`
- 处理小鬼任务相关逻辑（可能涉及任务进度更新）
- 然后调用父类的标准掉落判定方法

---

### createLoot()（第89-97行）

```java
public Item createLoot() {
    Dungeon.LimitedDrops.GOLEM_EQUIP.count++;
    //uses probability tables for demon halls
    if (loot == Generator.Category.WEAPON){
        return Generator.randomWeapon(5, true);
    } else {
        return Generator.randomArmor(5);
    }
}
```

**逐行解释**：
- **第90行**：增加装备掉落计数器，影响后续掉落概率
- **第92-93行**：如果是武器类别，生成T5（恶魔大厅等级）随机武器
  - `true` 参数表示使用恶魔大厅的概率表
- **第94-96行**：否则生成T5随机护甲
- 返回的装备适合恶魔大厅难度

---

### storeInBundle()（第107-113行）

```java
@Override
public void storeInBundle(Bundle bundle) {
    super.storeInBundle(bundle);
    bundle.put(TELEPORTING, teleporting);
    bundle.put(SELF_COOLDOWN, selfTeleCooldown);
    bundle.put(ENEMY_COOLDOWN, enemyTeleCooldown);
}
```

**逐行解释**：
- 保存魔像状态到 Bundle（用于游戏存档）
- 保存三个传送相关字段：
  - `teleporting`：是否正在传送
  - `selfTeleCooldown`：自身传送冷却
  - `enemyTeleCooldown`：敌人传送冷却

---

### restoreFromBundle()（第115-121行）

```java
@Override
public void restoreFromBundle(Bundle bundle) {
    super.restoreFromBundle(bundle);
    teleporting = bundle.getBoolean( TELEPORTING );
    selfTeleCooldown = bundle.getInt( SELF_COOLDOWN );
    enemyTeleCooldown = bundle.getInt( ENEMY_COOLDOWN );
}
```

**逐行解释**：
- 从 Bundle 恢复魔像状态（用于读取存档）
- 恢复三个传送相关字段
- 确保传送状态在存档/读档后保持一致

---

### act()（第123-140行）

```java
@Override
protected boolean act() {
    selfTeleCooldown--;
    enemyTeleCooldown--;
    if (teleporting){
        ((GolemSprite)sprite).teleParticles(false);
        if (Actor.findChar(target) == null && Dungeon.level.openSpace[target]) {
            ScrollOfTeleportation.appear(this, target);
            selfTeleCooldown = 30;
        } else {
            target = Dungeon.level.randomDestination(this);
        }
        teleporting = false;
        spend(TICK);
        return true;
    }
    return super.act();
}
```

**逐行解释**：
- **第125-126行**：每回合减少两个冷却计时器
- **第127行**：检查是否处于传送状态
- **第128行**：关闭传送粒子特效
- **第129行**：检查目标位置是否可用（无角色且有开放空间）
- **第130行**：执行传送，魔像出现在目标位置
- **第131行**：设置自身传送冷却为30回合
- **第133行**：如果目标位置不可用，重新选择随机目的地
- **第135行**：重置传送状态
- **第136行**：消耗一个回合
- **第139行**：如果不在传送状态，执行父类行为逻辑

---

### onZapComplete()（第142-145行）

```java
public void onZapComplete(){
    teleportEnemy();
    next();
}
```

**逐行解释**：
- 由 `GolemSprite.zap()` 的回调触发
- 在传送特效播放完成后执行
- 调用 `teleportEnemy()` 执行敌人传送
- 调用 `next()` 继续行动队列

---

### teleportEnemy()（第147-173行）

```java
public void teleportEnemy(){
    spend(TICK);

    int bestPos = enemy.pos;
    for (int i : PathFinder.NEIGHBOURS8){
        if (Dungeon.level.passable[pos + i]
            && Actor.findChar(pos+i) == null
            && Dungeon.level.trueDistance(pos+i, enemy.pos) > Dungeon.level.trueDistance(bestPos, enemy.pos)){
            bestPos = pos+i;
        }
    }

    if (enemy.buff(MagicImmune.class) != null){
        bestPos = enemy.pos;
    }

    if (bestPos != enemy.pos){
        ScrollOfTeleportation.appear(enemy, bestPos);
        if (enemy instanceof Hero){
            ((Hero) enemy).interrupt();
            Dungeon.observe();
            GameScene.updateFog();
        }
    }

    enemyTeleCooldown = 20;
}
```

**逐行解释**：
- **第148行**：消耗一个回合
- **第150行**：初始化最佳位置为敌人当前位置
- **第151-157行**：遍历魔像周围的8个相邻格子
  - 检查是否可通行且无角色
  - 选择距离敌人当前位置最远的格子
  - 目的是将敌人传送到魔像附近但远离原位置
- **第159-161行**：如果敌人有魔法免疫状态，取消传送（位置不变）
- **第163-169行**：如果找到了合适的位置
  - 执行传送，敌人出现在新位置
  - 如果敌人是英雄，打断当前动作并更新视野
- **第172行**：设置敌人传送冷却为20回合

---

### canTele()（第175-183行）

```java
private boolean canTele(int target){
    if (enemyTeleCooldown > 0) return false;
    PathFinder.buildDistanceMap(target, BArray.not(Dungeon.level.solid, null), Dungeon.level.distance(pos, target)+1);
    //zaps can go around blocking terrain, but not through it
    if (PathFinder.distance[pos] == Integer.MAX_VALUE){
        return false;
    }
    return true;
}
```

**逐行解释**：
- **第176行**：如果敌人传送冷却未结束，返回 false
- **第177行**：构建从目标位置的距离地图
  - 使用非实心格子作为可通行区域
  - 检查魔像是否能到达目标位置
- **第179-181行**：如果魔像位置距离为无限大（不可达），返回 false
- **第182行**：否则返回 true，可以传送敌人

---

## 内部类详解

### Wandering 内部类（第185-206行）

```java
private class Wandering extends Mob.Wandering{

    @Override
    protected boolean continueWandering() {
        enemySeen = false;

        int oldPos = pos;
        if (target != -1 && getCloser( target )) {
            spend( 1 / speed() );
            return moveSprite( oldPos, pos );
        } else if (!Dungeon.bossLevel() && target != -1 && target != pos && selfTeleCooldown <= 0) {
            ((GolemSprite)sprite).teleParticles(true);
            teleporting = true;
            spend( 2*TICK );
        } else {
            target = randomDestination();
            spend( TICK );
        }

        return true;
    }
}
```

**逐行解释**：
- 继承自 `Mob.Wandering`，重写游荡行为
- **第189行**：重置敌人可见状态
- **第192-195行**：如果有目标且能靠近，正常移动
- **第196-200行**：特殊传送逻辑
  - 非Boss层且目标有效且不在当前位置且冷却结束
  - 启动传送粒子特效
  - 设置传送状态为 true
  - 消耗2个回合（传送需要准备时间）
- **第201-203行**：否则选择新的随机目的地

---

### Hunting 内部类（第208-255行）

```java
private class Hunting extends Mob.Hunting{

    @Override
    public boolean act(boolean enemyInFOV, boolean justAlerted) {
        if (!enemyInFOV || canAttack(enemy)) {
            return super.act(enemyInFOV, justAlerted);
        } else {

            if (handleRecentAttackers()){
                return act( true, justAlerted );
            }

            enemySeen = true;
            target = enemy.pos;

            int oldPos = pos;

            if (distance(enemy) >= 1 && Random.Int(100/distance(enemy)) == 0
                    && !Char.hasProp(enemy, Property.IMMOVABLE) && canTele(target)){
                if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                    sprite.zap( enemy.pos );
                    return false;
                } else {
                    teleportEnemy();
                    return true;
                }

            } else if (getCloser( target )) {
                spend( 1 / speed() );
                return moveSprite( oldPos,  pos );

            } else if (!Char.hasProp(enemy, Property.IMMOVABLE) && canTele(target)) {
                if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                    sprite.zap( enemy.pos );
                    return false;
                } else {
                    teleportEnemy();
                    return true;
                }

            } else {
                //attempt to swap targets if the current one can't be reached or teleported
                return handleUnreachableTarget(enemyInFOV, justAlerted);
            }

        }
    }
}
```

**逐行解释**：
- 继承自 `Mob.Hunting`，重写追击行为
- **第212-213行**：如果敌人在视野外或可以攻击，使用父类行为
- **第216-218行**：处理最近的攻击者（目标切换逻辑）
- **第220-221行**：设置敌人可见并更新目标位置
- **第225-234行**：敌人传送判定
  - 距离至少1格
  - 概率为 `100/distance` 分之一
  - 敌人不是不可移动的
  - 可以传送
  - 播放zap动画或直接传送
- **第235-238行**：尝试靠近敌人
- **第239-246行**：如果无法靠近但可以传送，执行传送
- **第248-250行**：如果所有方法都失败，尝试切换目标

---

## 11. 使用示例

### 在关卡中生成魔像

```java
// 在恶魔大厅层级生成魔像
Golem golem = new Golem();
golem.pos = someValidPosition;
Dungeon.level.mobs.add(golem);
GameScene.add(golem);
```

### 检测魔像是否可以传送敌人

```java
// 在自定义AI或技能中检测
if (golem.canTele(hero.pos)) {
    // 魔像可以传送英雄
    // 考虑使用魔法免疫来抵抗
}
```

### 给玩家添加魔法免疫以抵抗传送

```java
// 魔法免疫可以阻止魔像传送玩家
Buff.affect(hero, MagicImmune.class);
```

---

## 注意事项

### 1. 传送机制

- **自身传送冷却**：30回合
- **敌人传送冷却**：20回合
- **传送准备时间**：2回合（游荡传送）
- 魔法免疫（`MagicImmune`）可以完全抵抗敌人传送

### 2. 掉落机制

- 每次掉落装备后，下次掉落概率降低为1/3
- 掉落序列：20% → 6.67% → 2.22% → ...
- 装备等级为T5（恶魔大厅等级）

### 3. 属性特性

- `INORGANIC`：不受某些影响有机物的效果（如毒素、流血）
- `LARGE`：无法进入单格通道，需要开阔空间

### 4. 视觉效果

- 传送时会显示 Elmo 粒子特效
- `GolemSprite.teleParticles(true/false)` 控制特效开关

---

## 最佳实践

### 1. 作为敌人设计参考

```java
// 创建类似功能的敌人时：
// 1. 继承 Mob 类
// 2. 重写 Wandering 和 Hunting 内部类
// 3. 实现自定义的 act() 方法处理特殊状态
// 4. 使用 Bundle 存储恢复状态
```

### 2. 平衡性考虑

- 魔像的生命值（120）和伤害（25-30）与其传送能力相匹配
- 冷却机制防止传送过于频繁
- 概率性传送增加战斗不确定性

### 3. 存档兼容性

```java
// 添加新的状态字段时：
// 1. 添加静态常量作为存储键
// 2. 在 storeInBundle() 中保存
// 3. 在 restoreFromBundle() 中恢复
// 4. 设置合理的默认值
```

### 4. 与其他系统集成

```java
// 与任务系统集成
@Override
public void rollToDropLoot() {
    Imp.Quest.process(this);  // 处理任务进度
    super.rollToDropLoot();
}

// 与LimitedDrops系统集成
Dungeon.LimitedDrops.GOLEM_EQUIP.count++;  // 追踪掉落数量
```

---

## 相关文件

| 文件 | 说明 |
|------|------|
| `Mob.java` | 父类，提供基础怪物行为 |
| `GolemSprite.java` | 精灵类，处理视觉和动画 |
| `ScrollOfTeleportation.java` | 传送卷轴，提供传送实现 |
| `Imp.java` | 小鬼NPC，处理任务逻辑 |
| `MagicImmune.java` | 魔法免疫状态 |

---

*文档生成时间：2026-03-26*
*游戏版本：Shattered Pixel Dungeon*