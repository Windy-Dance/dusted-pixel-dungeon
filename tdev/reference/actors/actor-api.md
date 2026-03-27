# Actor API 参考

## 类声明
```java
public abstract class Actor implements Bundlable
```

Actor类是Shattered Pixel Dungeon回合制系统的核心抽象基类，所有需要参与时间调度的游戏实体（如角色、怪物、状态效果和地形效果）都继承自此类。它实现了`Bundlable`接口，支持游戏存档功能。

## 类职责
Actor类是回合制系统的核心，负责管理游戏中的时间调度。它通过维护每个Actor的时间值（time）和优先级（actPriority）来决定执行顺序。当多个Actor的时间值相同时，优先级高的会先执行。所有游戏逻辑（移动、攻击、特效等）都通过实现`act()`方法并在适当的时间点被调用。

## 关键字段
| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|-------|------|---------|-------|------|
| time | float | private | 0 | 当前Actor的内部时间戳，相对于全局now时间 |
| id | int | private | 0 | Actor的唯一标识符，用于查找和引用 |
| actPriority | int | protected | DEFAULT (-100) | 执行优先级，值越大优先级越高 |
| TICK | float | public static final | 1f | 标准回合时间单位 |

## 优先级常量
| 常量名       | 值    | 说明                      |
| --------- | ---- | ----------------------- |
| VFX_PRIO  | 100  | 视觉特效优先级，最高优先级           |
| HERO_PRIO | 0    | 英雄角色优先级，作为基准点           |
| BLOB_PRIO | -10  | 地形效果（Blob）优先级，英雄之后、怪物之前 |
| MOB_PRIO  | -20  | 怪物（Mob）优先级，状态效果和地形效果之间  |
| BUFF_PRIO | -30  | 状态效果（Buff）优先级，最低优先级     |
| DEFAULT   | -100 | 默认优先级，如果没有指定则使用此值       |

## 抽象方法
| 方法签名 | 返回值 | 说明 |
|---------|-------|------|
| `protected abstract boolean act()` | boolean | 核心抽象方法，必须由子类实现。返回true表示继续处理下一个Actor，false表示暂停处理 |

## 时间管理方法
| 方法签名 | 返回值 | 说明 |
|---------|-------|------|
| `protected void spendConstant(float time)` | void | 花费指定时间，不受时间影响因素干扰 |
| `protected void spend(float time)` | void | 花费时间（默认调用spendConstant） |
| `public void spendToWhole()` | void | 将时间调整到下一个整数点 |
| `protected void postpone(float time)` | void | 推迟执行，确保至少等待指定时间 |
| `public float cooldown()` | float | 返回距离下次执行的剩余时间（time - now） |
| `public void clearTime()` | void | 清除时间偏移，将时间重置为相对现在 |
| `public void timeToNow()` | void | 将Actor的时间设置为当前全局时间 |
| `protected void diactivate()` | void | 停用Actor，将其时间设为最大值（永不执行） |

## 静态管理方法
| 方法签名 | 返回值 | 说明 |
|---------|-------|------|
| `public static synchronized void clear()` | void | 清空所有Actor，重置游戏时间 |
| `public static synchronized void fixTime()` | void | 修复时间漂移，将所有Actor时间对齐到整数 |
| `public static void init()` | void | 初始化Actor系统，添加英雄、怪物和地形效果 |
| `public static void process()` | void | 主处理循环，执行所有Actor的act()方法 |
| `public static void add(Actor actor)` | void | 添加Actor到调度系统 |
| `public static void addDelayed(Actor actor, float delay)` | void | 延迟添加Actor到调度系统 |
| `public static synchronized void remove(Actor actor)` | void | 从调度系统中移除Actor |
| `public static void delayChar(Char ch, float time)` | void | 延迟指定角色及其所有状态效果 |
| `public static synchronized Char findChar(int pos)` | Char | 在指定位置查找角色 |
| `public static synchronized Actor findById(int id)` | Actor | 通过ID查找Actor |
| `public static synchronized HashSet<Actor> all()` | HashSet<Actor> | 获取所有Actor的副本 |
| `public static synchronized HashSet<Char> chars()` | HashSet<Char> | 获取所有角色（Char）的副本 |
| `public static float now()` | float | 获取当前全局时间 |
| `public static boolean processing()` | boolean | 检查是否正在处理Actor |
| `public static int curActorPriority()` | int | 获取当前正在处理的Actor的优先级 |

## 生命周期
Actor的完整生命周期如下：

1. **创建**：实例化Actor子类对象
2. **添加**：通过`Actor.add()`或`Actor.addDelayed()`添加到调度系统
3. **初始化回调**：系统调用`onAdd()`方法（可重写）
4. **执行**：当Actor的时间到达时，系统调用`act()`方法
5. **时间管理**：在`act()`中通过`spend()`、`postpone()`等方法管理下一次执行时间
6. **移除**：通过`Actor.remove()`从调度系统中移除
7. **清理回调**：系统调用`onRemove()`方法（可重写）
8. **销毁**：对象被垃圾回收

## 回合制系统原理
Shattered Pixel Dungeon使用基于时间和优先级的回合制系统：

- **时间机制**：每个Actor维护一个`time`字段，表示它下次执行的时间点。全局`now`变量表示当前游戏时间。
- **执行顺序**：系统总是选择`time`值最小的Actor执行。如果多个Actor的`time`相同，则比较`actPriority`，优先级高的先执行。
- **时间推进**：当Actor执行完`act()`后，通过`spend()`方法增加其`time`值，决定下次执行的时间。
- **时间修正**：`fixTime()`方法定期调用，将所有Actor的时间对齐到整数，防止浮点数精度问题导致的时间漂移。
- **暂停机制**：当没有Actor需要立即执行时，处理线程会等待，直到被唤醒继续处理。

这种设计允许不同类型的实体以不同的速度行动（例如，快速角色可以比慢速角色行动更频繁），同时保证了确定性和可预测性。

## 使用示例

### 示例1: 创建自定义Actor
```java
public class CustomEffect extends Actor {
    private int duration;
    private int targetPos;
    
    public CustomEffect(int duration, int targetPos) {
        this.duration = duration;
        this.targetPos = targetPos;
        // 设置高优先级，确保在视觉效果阶段执行
        this.actPriority = VFX_PRIO;
    }
    
    @Override
    protected boolean act() {
        // 执行自定义逻辑
        if (duration > 0) {
            // 显示视觉效果
            CellEmitter.get(targetPos).burst(SparkParticle.FACTORY, 5);
            duration--;
            // 花费1个标准回合时间
            spend(TICK);
            return true; // 继续执行
        } else {
            // 效果结束，自动移除
            Actor.remove(this);
            return false; // 停止执行
        }
    }
}

// 使用方式
CustomEffect effect = new CustomEffect(3, hero.pos);
Actor.add(effect); // 立即添加
```

### 示例2: 理解回合执行顺序
```java
// 创建不同优先级的测试Actor
public class TestActor extends Actor {
    private String name;
    private int priority;
    
    public TestActor(String name, int priority) {
        this.name = name;
        this.actPriority = priority;
    }
    
    @Override
    protected boolean act() {
        System.out.println(name + " executed at time " + now());
        // 每个Actor每2个回合执行一次
        spend(TICK * 2);
        return true;
    }
}

// 添加测试Actor
Actor.add(new TestActor("Hero", HERO_PRIO));      // 优先级 0
Actor.add(new TestActor("Blob", BLOB_PRIO));      // 优先级 -10  
Actor.add(new TestActor("Mob", MOB_PRIO));        // 优先级 -20
Actor.add(new TestActor("Buff", BUFF_PRIO));      // 优先级 -30

// 执行顺序输出：
// 时间0: Hero执行（优先级最高）
// 时间0: Blob执行
// 时间0: Mob执行  
// 时间0: Buff执行
// 时间2: Hero执行
// 时间2: Blob执行
// ...
```

## 相关子类
Actor的主要子类包括：

- **Char**: 角色基类，包括Hero（玩家角色）和Mob（怪物）
- **Buff**: 状态效果基类，附加到角色上提供各种增益/减益效果
- **Blob**: 地形效果基类，如火焰、毒气、水等区域效果
- **Visual**: 视觉特效，通常使用VFX_PRIO高优先级

这些子类都继承了Actor的核心时间调度功能，并根据各自的特点实现了具体的`act()`方法。

## 常见错误
开发者在使用Actor系统时容易犯的错误：

1. **忘记调用spend()**: 在`act()`方法中忘记调用`spend()`会导致无限循环，因为Actor的时间永远不会推进。

2. **错误的时间计算**: 直接修改`time`字段而不是使用提供的方法，可能导致时间同步问题。

3. **不当的优先级设置**: 设置过高的优先级可能破坏游戏平衡，设置过低可能导致关键逻辑延迟执行。

4. **内存泄漏**: 忘记调用`Actor.remove()`移除不再需要的Actor，导致内存泄漏和性能问题。

5. **线程安全问题**: 在非主线程中直接操作Actor集合，应该使用提供的同步方法。

6. **忽略diactivate()**: 对于临时停用的Actor，应该使用`diactivate()`而不是设置很大的时间值。

7. **重复添加**: 同一个Actor被多次添加到调度系统，应该先检查是否已存在。

8. **错误的TICK使用**: 在某些情况下，可能需要花费小于TICK的时间（如反应速度），但要注意时间漂移问题。