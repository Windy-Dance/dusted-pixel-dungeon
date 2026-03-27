# Trap.java 中文文档

> 陷阱基类 - 定义游戏中所有陷阱的抽象基类

---

## 📋 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/Trap.java` |
| **包名** | `com.shatteredpixel.shatteredpixeldungeon.levels.traps` |
| **修饰符** | `public abstract` |
| **父类/接口** | `implements Bundlable` |
| **代码行数** | 151 行 |
| **版权** | Shattered Pixel Dungeon (C) 2014-2026 Evan Debenham |

---

## 🎯 类职责

`Trap` 是游戏中所有陷阱的**抽象基类**，负责：

1. **定义陷阱的基本属性** - 位置、可见性、激活状态、颜色、形状
2. **提供陷阱生命周期管理** - 触发、激活、解除
3. **实现序列化机制** - 通过 `Bundlable` 接口支持存档/读档
4. **定义视觉效果规范** - 通过颜色和形状常量统一陷阱外观
5. **追踪遭遇记录** - 与图鉴(Bestiary)集成记录玩家遭遇

---

## 📊 类关系图

```
┌─────────────────────────────────────────────────────────────┐
│                        <<interface>>                         │
│                         Bundlable                            │
│                    (com.watabou.utils)                       │
└─────────────────────────────────────────────────────────────┘
                              ▲
                              │ implements
                              │
┌─────────────────────────────────────────────────────────────┐
│                      <<abstract>>                            │
│                          Trap                                │
│─────────────────────────────────────────────────────────────│
│ + color: int              // 陷阱颜色                        │
│ + shape: int              // 陷阱形状                        │
│ + pos: int                // 地图位置                        │
│ + visible: boolean        // 是否可见                        │
│ + active: boolean         // 是否激活                        │
│ + reclaimed: boolean      // 是否由回收陷阱生成              │
│ + disarmedByActivation: boolean  // 激活后是否解除           │
│ + canBeHidden: boolean    // 是否可隐藏                      │
│ + canBeSearched: boolean  // 是否可被搜索发现                │
│ + avoidsHallways: boolean // 是否避开走廊放置                │
│─────────────────────────────────────────────────────────────│
│ + set(pos): Trap           // 设置位置                       │
│ + reveal(): Trap           // 显示陷阱                       │
│ + hide(): Trap             // 隐藏陷阱                       │
│ + trigger(): void          // 触发陷阱                       │
│ + activate(): void         // 激活效果 (抽象)                │
│ + disarm(): void           // 解除陷阱                       │
│ + name(): String           // 获取名称                       │
│ + desc(): String           // 获取描述                       │
│ # scalingDepth(): int      // 计算缩放深度                   │
└─────────────────────────────────────────────────────────────┘
                              ▲
                              │ extends
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
┌───────┴───────┐   ┌────────┴────────┐   ┌───────┴───────┐
│  WornDartTrap │   │   ExplosiveTrap │   │   GrimTrap    │
│  (磨损飞镖陷阱) │   │   (爆炸陷阱)     │   │  (死亡陷阱)   │
└───────────────┘   └─────────────────┘   └───────────────┘
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              │
                    (共 30+ 个具体陷阱子类)
```

**主要子类列表：**

| 子类名 | 中文名 | 颜色 | 形状 |
|--------|--------|------|------|
| `WornDartTrap` | 磨损飞镖陷阱 | GREY | DOTS |
| `PoisonDartTrap` | 毒镖陷阱 | GREEN | DOTS |
| `GrippingTrap` | 夹击陷阱 | GREY | CROSSHAIR |
| `BurningTrap` | 燃烧陷阱 | ORANGE | GRILL |
| `BlazingTrap` | 烈焰陷阱 | RED | GRILL |
| `FrostTrap` | 霜冻陷阱 | TEAL | WAVES |
| `ChillingTrap` | 冰寒陷阱 | TEAL | WAVES |
| `ExplosiveTrap` | 爆炸陷阱 | ORANGE | DIAMOND |
| `GrimTrap` | 死亡陷阱 | BLACK | LARGE_DOT |
| `ToxicTrap` | 剧毒陷阱 | GREEN | WAVES |
| `CorrosionTrap` | 腐蚀陷阱 | GREEN | STARS |
| `AlarmTrap` | 警报陷阱 | RED | DIAMOND |
| `SummoningTrap` | 召唤陷阱 | VIOLET | STARS |
| `TeleportationTrap` | 传送陷阱 | VIOLET | WAVES |
| `WarpingTrap` | 扭曲陷阱 | VIOLET | DIAMOND |

---

## 📦 静态常量表

### 颜色常量 (Color Constants)

用于定义陷阱在地图上的视觉颜色：

| 常量名 | 值 | 含义 | 常见用途 |
|--------|-----|------|----------|
| `RED` | 0 | 红色 | 危险陷阱 (AlarmTrap, GrimTrap等) |
| `ORANGE` | 1 | 橙色 | 火焰相关 (BurningTrap, ExplosiveTrap等) |
| `YELLOW` | 2 | 黄色 | 特殊效果 |
| `GREEN` | 3 | 绿色 | 毒素相关 (ToxicTrap, PoisonDartTrap等) |
| `TEAL` | 4 | 青色 | 冰冻相关 (FrostTrap, ChillingTrap等) |
| `VIOLET` | 5 | 紫色 | 魔法相关 (SummoningTrap, TeleportationTrap等) |
| `WHITE` | 6 | 白色 | 中性陷阱 |
| `GREY` | 7 | 灰色 | 普通陷阱 (WornDartTrap, GrippingTrap等) |
| `BLACK` | 8 | 黑色 | 致命陷阱 (GrimTrap) |

### 形状常量 (Shape Constants)

用于定义陷阱在地图上的视觉图案：

| 常量名 | 值 | 含义 | 常见用途 |
|--------|-----|------|----------|
| `DOTS` | 0 | 点状 | 飞镖类陷阱 |
| `WAVES` | 1 | 波浪状 | 冰冻、传送、毒素类 |
| `GRILL` | 2 | 格栅状 | 火焰类陷阱 |
| `STARS` | 3 | 星形 | 腐蚀、召唤类 |
| `DIAMOND` | 4 | 菱形 | 爆炸、警报类 |
| `CROSSHAIR` | 5 | 十字准星 | 夹击类陷阱 |
| `LARGE_DOT` | 6 | 大圆点 | 死亡陷阱 |

---

## 📝 实例字段表

| 字段名 | 类型 | 默认值 | 访问级别 | 说明 |
|--------|------|--------|----------|------|
| `color` | `int` | - | `public` | 陷阱颜色，取值于颜色常量 |
| `shape` | `int` | - | `public` | 陷阱形状，取值于形状常量 |
| `pos` | `int` | - | `public` | 陷阱在地图上的位置索引 |
| `reclaimed` | `boolean` | `false` | `public` | 是否由 Reclaim Trap（回收陷阱）生成 |
| `visible` | `boolean` | - | `public` | 陷阱是否对玩家可见 |
| `active` | `boolean` | `true` | `public` | 陷阱是否处于激活状态 |
| `disarmedByActivation` | `boolean` | `true` | `public` | 激活后是否自动解除 |
| `canBeHidden` | `boolean` | `true` | `public` | 是否可以被隐藏 |
| `canBeSearched` | `boolean` | `true` | `public` | 是否可以通过搜索发现 |
| `avoidsHallways` | `boolean` | `false` | `public` | 生成时是否避开走廊 |

---

## 🔧 方法详解

### `set(int pos)` - 设置陷阱位置

```java
public Trap set(int pos){
    this.pos = pos;
    return this;
}
```

**逐行解释：**
- 第 71 行：方法签名，接收位置参数，返回 Trap 自身以支持链式调用
- 第 72 行：将参数 `pos` 赋值给实例字段 `this.pos`
- 第 73 行：返回 `this`，允许 `trap.set(pos).reveal()` 这样的链式调用

**设计模式：** 流式接口（Fluent Interface）模式，便于链式调用

**使用场景：** 陷阱创建时初始化位置，通常在关卡生成器中使用

---

### `reveal()` - 显示陷阱

```java
public Trap reveal() {
    visible = true;
    GameScene.updateMap(pos);
    return this;
}
```

**逐行解释：**
- 第 76 行：方法签名，无参数，返回 Trap 自身
- 第 77 行：将 `visible` 设为 `true`，使陷阱可见
- 第 78 行：调用 `GameScene.updateMap(pos)` 刷新地图上该位置的显示
- 第 79 行：返回 `this` 支持链式调用

**副作用：** 触发地图视觉更新

**使用场景：** 玩家搜索发现陷阱、触发陷阱后使其可见

---

### `hide()` - 隐藏陷阱

```java
public Trap hide() {
    if (canBeHidden) {
        visible = false;
        GameScene.updateMap(pos);
        return this;
    } else {
        return reveal();
    }
}
```

**逐行解释：**
- 第 82 行：方法签名
- 第 83 行：检查陷阱是否允许被隐藏
- 第 84 行：如果可以隐藏，将 `visible` 设为 `false`
- 第 85 行：刷新地图显示
- 第 86 行：返回 `this`
- 第 88 行：如果不允许隐藏，调用 `reveal()` 确保陷阱可见并返回

**设计决策：** 某些陷阱（如特殊机关）不能被隐藏，调用 `hide()` 会强制显示

---

### `trigger()` - 触发陷阱

```java
public void trigger() {
    if (active) {
        if (Dungeon.level.heroFOV[pos]) {
            Sample.INSTANCE.play(Assets.Sounds.TRAP);
        }
        if (disarmedByActivation) disarm();
        Dungeon.level.discover(pos);
        Bestiary.setSeen(getClass());
        Bestiary.countEncounter(getClass());
        activate();
    }
}
```

**逐行解释：**
- 第 92 行：方法签名，无返回值
- 第 93 行：检查陷阱是否处于激活状态
- 第 94-96 行：如果英雄视野包含陷阱位置，播放陷阱触发音效
- 第 97 行：如果该陷阱类型"激活后解除"，调用 `disarm()`
- 第 98 行：调用 `Dungeon.level.discover(pos)` 发现该位置
- 第 99 行：在图鉴中标记已见过此类陷阱
- 第 100 行：增加此类陷阱的遭遇计数
- 第 101 行：调用抽象方法 `activate()` 执行具体陷阱效果

**核心流程：** 检查激活 → 播放音效 → 解除(可选) → 发现位置 → 记录图鉴 → 执行效果

**设计模式：** 模板方法模式（Template Method），`trigger()` 定义流程骨架，`activate()` 由子类实现具体效果

---

### `activate()` - 激活陷阱效果（抽象方法）

```java
public abstract void activate();
```

**说明：**
- 抽象方法，必须由子类实现
- 定义陷阱触发时的具体效果
- 在 `trigger()` 中被调用

**实现示例：**
```java
// WornDartTrap.java 中的实现
@Override
public void activate() {
    Char ch = Actor.findChar(pos);
    if (ch != null && !ch.flying) {
        int damage = Math.max(0, scalingDepth() - ch.drRoll());
        ch.damage(damage, this);
        if (ch == Dungeon.hero) {
            // 对英雄的特殊处理
        }
    }
}
```

---

### `disarm()` - 解除陷阱

```java
public void disarm(){
    active = false;
    Dungeon.level.disarmTrap(pos);
}
```

**逐行解释：**
- 第 107 行：方法签名
- 第 108 行：将 `active` 设为 `false`，陷阱不再可触发
- 第 109 行：通知关卡该位置的陷阱已解除

**使用场景：**
- 陷阱触发后自动解除（如果 `disarmedByActivation` 为 true）
- 玩家通过技能/道具主动解除陷阱
- 特殊陷阱效果需要移除陷阱

---

### `scalingDepth()` - 计算缩放深度

```java
protected int scalingDepth(){
    return (reclaimed || Dungeon.level.traps.get(pos) != this) ? Dungeon.scalingDepth() : Dungeon.depth;
}
```

**逐行解释：**
- 第 115 行：`protected` 方法，供子类使用
- 第 116 行：条件判断 - 如果陷阱是回收生成的(`reclaimed`)，或不在关卡陷阱表中，返回 `Dungeon.scalingDepth()`；否则返回 `Dungeon.depth`

**设计意图：**
- 关卡中的陷阱使用真实深度（`Dungeon.depth`）
- 由 Reclaim Trap 动态生成的陷阱使用缩放深度，确保难度平衡

**使用场景：** 子类在 `activate()` 中计算伤害或效果强度时调用

---

### `name()` - 获取陷阱名称

```java
public String name(){
    return Messages.get(this, "name");
}
```

**逐行解释：**
- 第 119 行：方法签名，返回陷阱的显示名称
- 第 120 行：通过 `Messages.get()` 从本地化资源文件获取名称

**本地化支持：** 名称定义在 `messages/properties/traps.properties` 等文件中

---

### `desc()` - 获取陷阱描述

```java
public String desc() {
    return Messages.get(this, "desc");
}
```

**逐行解释：**
- 第 123 行：方法签名，返回陷阱的详细描述文本
- 第 124 行：通过 `Messages.get()` 从本地化资源文件获取描述

**用途：** 玩家检查陷阱时显示的说明文字

---

### `restoreFromBundle(Bundle)` - 从存档恢复

```java
private static final String POS = "pos";
private static final String VISIBLE = "visible";
private static final String ACTIVE = "active";

@Override
public void restoreFromBundle( Bundle bundle ) {
    pos = bundle.getInt( POS );
    visible = bundle.getBoolean( VISIBLE );
    if (bundle.contains(ACTIVE)){
        active = bundle.getBoolean(ACTIVE);
    }
}
```

**逐行解释：**
- 第 127-129 行：定义存储键名常量
- 第 132 行：实现 `Bundlable` 接口的方法
- 第 133 行：从 Bundle 中读取位置信息
- 第 134 行：从 Bundle 中读取可见性状态
- 第 135-137 行：兼容性处理 - 如果存档包含 `ACTIVE` 字段才读取，保证旧存档兼容

**存档系统：** Shattered Pixel Dungeon 使用 Bundle 系统进行序列化

---

### `storeInBundle(Bundle)` - 保存到存档

```java
@Override
public void storeInBundle( Bundle bundle ) {
    bundle.put( POS, pos );
    bundle.put( VISIBLE, visible );
    bundle.put( ACTIVE, active );
}
```

**逐行解释：**
- 第 141 行：实现 `Bundlable` 接口的方法
- 第 142 行：存储位置
- 第 143 行：存储可见性状态
- 第 144 行：存储激活状态

---

### `HazardAssistTracker` 内部类

```java
public static class HazardAssistTracker extends FlavourBuff{
    public static final float DURATION = 50f;
}
```

**说明：**
- 静态内部类，继承 `FlavourBuff`
- 用于追踪角色最近受到的陷阱影响
- 持续时间 50 回合

**用途：** 游戏系统内部追踪，用于成就、统计或其他机制

---

## 💡 使用示例

### 示例 1：创建并放置陷阱

```java
// 创建一个磨损飞镖陷阱
WornDartTrap trap = new WornDartTrap();
trap.color = Trap.GREY;
trap.shape = Trap.DOTS;

// 设置位置并添加到关卡
int trapPos = 42; // 地图位置索引
Dungeon.level.traps.put(trapPos, trap.set(trapPos));

// 陷阱初始隐藏
trap.hide();
```

### 示例 2：触发陷阱

```java
// 当角色踩到陷阱位置
Trap trap = Dungeon.level.traps.get(charPos);
if (trap != null && trap.active) {
    trap.trigger();
}
```

### 示例 3：搜索发现陷阱

```java
// 玩家搜索时
if (trap.canBeSearched && !trap.visible) {
    if (Random.Float() < searchChance) {
        trap.reveal();
        GLog.i(Messages.get(Hero.class, "discovered_trap", trap.name()));
    }
}
```

### 示例 4：自定义陷阱实现

```java
public class MyCustomTrap extends Trap {
    
    {
        color = RED;
        shape = DIAMOND;
        disarmedByActivation = false; // 可重复触发
        canBeHidden = false;          // 始终可见
    }
    
    @Override
    public void activate() {
        // 自定义效果逻辑
        Char ch = Actor.findChar(pos);
        if (ch != null) {
            // 对角色造成效果
            Buff.affect(ch, Poison.class).set(5f);
        }
        
        // 使用缩放深度计算效果强度
        int effectPower = scalingDepth() * 2;
        // ... 应用效果
    }
}
```

---

## ⚠️ 注意事项

### 1. 线程安全
- 陷阱操作主要在游戏主线程进行
- 避免在 `activate()` 中执行异步操作

### 2. 存档兼容性
- 添加新字段时，确保在 `restoreFromBundle()` 中处理缺失字段
- 使用 `bundle.contains()` 检查字段存在性

### 3. 位置管理
- `pos` 必须是有效的地图索引
- 陷阱移动时需更新 `Dungeon.level.traps` 映射

### 4. 视觉更新
- 修改 `visible` 或 `active` 后必须调用 `GameScene.updateMap(pos)`
- 避免频繁调用导致性能问题

### 5. 陷阱解除
- `disarmByActivation` 为 true 的陷阱触发后自动解除
- 部分陷阱（如某些机关）可能设置为 false 以重复触发

### 6. 深度计算
- 使用 `scalingDepth()` 而非直接访问 `Dungeon.depth`
- 确保动态生成的陷阱难度平衡

---

## 🏆 最佳实践

### 1. 陷阱创建模式

```java
// 推荐：使用实例初始化块设置属性
public class MyTrap extends Trap {
    {
        color = RED;
        shape = DIAMOND;
        disarmedByActivation = true;
    }
}
```

### 2. 链式调用

```java
// 推荐：利用链式调用
new MyTrap().set(pos).reveal();

// 不推荐：分开调用
Trap t = new MyTrap();
t.pos = pos;
t.visible = true;
```

### 3. 效果实现

```java
@Override
public void activate() {
    // 1. 查找目标
    Char ch = Actor.findChar(pos);
    
    // 2. 验证目标有效性
    if (ch == null || ch.flying) return;
    
    // 3. 使用 scalingDepth 计算效果
    int damage = scalingDepth() * 2;
    
    // 4. 应用效果
    ch.damage(damage, this);
    
    // 5. 记录到图鉴
    Bestiary.setSeen(getClass());
}
```

### 4. 资源管理

```java
// 陷阱名称和描述定义在资源文件中
// messages/properties/traps.properties
// traps.mytrap.name=我的陷阱
// traps.mytrap.desc=这是一个自定义陷阱...
```

### 5. 测试要点

- 测试陷阱在英雄视野内外的触发表现
- 验证存档/读档后陷阱状态正确
- 检查 `reclaimed` 陷阱的深度计算
- 确保解除后无法再次触发

---

## 📚 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `Bundlable` | 接口 | 序列化接口 |
| `Level` | 关联 | 关卡类管理陷阱集合 |
| `GameScene` | 关联 | 场景类处理视觉更新 |
| `Bestiary` | 关联 | 图鉴类记录遭遇 |
| `FlavourBuff` | 关联 | HazardAssistTracker 父类 |
| `Messages` | 依赖 | 本地化消息系统 |

---

## 📝 更新历史

| 版本 | 变更 |
|------|------|
| 初始版本 | 基础陷阱框架 |
| 后续更新 | 添加 `reclaimed`、`avoidsHallways` 等字段 |
| 当前版本 | 完整的陷阱生命周期管理 |

---

*文档生成日期：2026-03-26*