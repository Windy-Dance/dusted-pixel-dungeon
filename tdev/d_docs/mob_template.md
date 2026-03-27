# [MobClassName] 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/[MobClassName].java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.mobs |
| **类类型** | class（非抽象） |
| **继承关系** | extends Mob |
| **代码行数** | [line_count] |
| **中文名称** | [chinese_name] |

---

## 类职责

[MobClassName]（[chinese_name]）是游戏中的敌对生物之一。它负责：

1. **战斗行为**：定义攻击模式和战斗策略
2. **AI状态机**：管理不同的行为状态（休眠、游荡、追击等）
3. **属性配置**：设置生命值、防御、攻击力等基础属性
4. **特殊能力**：实现独特的技能或效果机制

**设计模式**：
- **模板方法模式**：重写父类方法定义特定行为
- **状态模式**：通过内部状态类管理不同行为模式

---

## 4. 继承与协作关系

```mermaid
classDiagram
    class Char {
        +int pos
        +int HP, HT
        +int attackSkill(Char)
        +int damageRoll()
        +int drRoll()
    }
    
    class Mob {
        +State state
        +int EXP
        +float spawningWeight()
        +boolean reset()
        +void die(Object)
    }
    
    class [MobClassName] {
        +spriteClass: Class
        +State SLEEPING
        +State WANDERING
        +[specific_fields]
        +spawnningWeight() float
        +damageRoll() int
        +attackSkill(Char) int
        +drRoll() int
        +act() boolean
        +die(Object) void
    }
    
    Char <|-- Mob
    Mob <|-- [MobClassName]
```

---

## 静态常量表

[If applicable]

| 常量 | 值 | 说明 |
|------|-----|------|
| [CONSTANT_NAME] | [value] | [description] |

---

## 实例字段表

| 字段名 | 类型 | 设置值 | 说明 |
|--------|------|--------|------|
| `spriteClass` | Class | [SpriteClass].class | 角色精灵类 |
| `HP` / `HT` | int | [value] | 当前/最大生命值 |
| `defenseSkill` | int | [value] | 防御技能等级 |
| `EXP` | int | [value] | 击败后获得的经验值 |
| `loot` | Class | [ItemClass].class | 掉落物品类型 |
| `lootChance` | float | [value] | 掉落概率 |
| `maxLvl` | int | [value] | 最大出现等级 |

### 特殊属性

| 属性 | 说明 |
|------|------|
| `Property.IMMOVABLE` | 不可移动 |
| `Property.BOSS` | BOSS单位 |
| `Property.UNDEAD` | 亡灵单位 |
| `Property.LARGE` | 大型单位 |

---

## 7. 方法详解

### 构造块（Instance Initializer）

```java
{
    spriteClass = [SpriteClass].class;
    HP = HT = [health_value];
    defenseSkill = [defense_value];
    
    EXP = [exp_value];
    maxLvl = [max_level];
    
    loot = [LootClass].class;
    lootChance = [loot_chance];
    
    properties.add(Property.[PROPERTY]);
}
```

**作用**：初始化mob的基础属性。

---

### spawningWeight()

```java
@Override
public float spawningWeight() {
    return [weight_value];
}
```

**方法作用**：返回在生成池中的权重，影响出现频率。

**返回值**：
- `[weight_value]`：生成权重（0表示不会自然生成）

---

### damageRoll()

```java
@Override
public int damageRoll() {
    return Random.NormalIntRange([min_damage], [max_damage]);
}
```

**方法作用**：计算攻击造成的伤害范围。

**伤害计算**：
- 最小伤害：`[min_damage]`
- 最大伤害：`[max_damage]`
- 平均伤害：`([min_damage] + [max_damage]) / 2`

---

### attackSkill(Char target)

```java
@Override
public int attackSkill(Char target) {
    return [attack_value];
}
```

**方法作用**：返回攻击技能等级，影响命中率。

**参数**：
- `target` (Char)：攻击目标

**返回值**：
- `[attack_value]`：攻击技能等级

---

### drRoll()

```java
@Override
public int drRoll() {
    return super.drRoll() + Random.NormalIntRange([min_dr], [max_dr]);
}
```

**方法作用**：计算伤害减免范围。

**伤害减免**：
- 额外减免：`[min_dr]` 到 `[max_dr]` 点

---

### act()

```java
@Override
protected boolean act() {
    // [behavior description]
    return super.act();
}
```

**方法作用**：定义每回合的行为逻辑。

**行为描述**：[详细描述AI行为]

---

### die(Object cause)

```java
@Override
public void die(Object cause) {
    // [death behavior]
    super.die(cause);
}
```

**方法作用**：定义死亡时的特殊行为。

**参数**：
- `cause` (Object)：死亡原因

**特殊行为**：[描述死亡时的特殊效果]

---

## AI状态机

### SLEEPING 状态

**触发条件**：初始状态或特定条件下

**行为**：[描述休眠状态的行为]

### WANDERING 状态

**触发条件**：被惊醒后或失去目标

**行为**：[描述游荡状态的行为]

### HUNTING 状态

**触发条件**：发现敌人

**行为**：[描述追击状态的行为]

---

## 11. 使用示例

### 基本实例化

```java
// 创建 mob 实例
[MobClassName] mob = new [MobClassName]();
mob.pos = targetPos;  // 设置位置

// 添加到游戏场景
GameScene.add(mob);
Dungeon.level.mobs.add(mob);
```

### 在关卡生成中使用

```java
// 在房间生成中添加 mob
[MobClassName] mob = new [MobClassName]();
mob.pos = room.random();  // 随机位置
Room.spawnMob(mob, room);  // 标准生成方法
```

---

## 注意事项

### 平衡性考虑

1. **难度曲线**：[MobClassName] 通常在 [floor_range] 层出现
2. **威胁等级**：[threat_description]
3. **克制关系**：[counter_relationships]

### 特殊机制

1. **[mechanism_name]**：[mechanism_description]
2. **[another_mechanism]**：[description]

### 技术限制

1. **性能影响**：[performance_notes]
2. **保存/加载**：[serialization_notes]

---

## 最佳实践

### AI 行为优化

```java
// 示例：优化的 AI 决策
@Override
protected boolean act() {
    if (enemyInFOV) {
        target = enemy.pos;
        return attack(enemy);
    } else {
        return super.act();
    }
}
```

### 自定义变体

```java
// 创建自定义变体
public class Enhanced[MobClassName] extends [MobClassName] {
    @Override
    public int damageRoll() {
        return super.damageRoll() + 5;  // 增加伤害
    }
}
```

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `Mob` | 父类 | 所有怪物的基类 |
| `[SpriteClass]` | 精灵类 | 对应的视觉表现 |
| `[LootClass]` | 掉落物品 | 可能掉落的物品 |
| `DungeonLevel` | 使用者 | 关卡生成系统 |
| `GameScene` | 管理者 | 游戏场景管理 |

---

## 消息键

| 键名 | 值 | 用途 |
|------|-----|------|
| `npcs.[mob_key].name` | [english_name] | NPC名称（如果适用） |
| `monsters.[mob_key].name` | [english_name] | 怪物名称 |
| `monsters.[mob_key].desc` | [description] | 怪物描述 |