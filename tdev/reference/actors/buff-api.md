# Buff API 参考

## 类声明
public class Buff extends Actor

## 类职责
Buff是所有状态效果（增益/减益）的基类，可以附加到Char上，修改角色的属性和行为。

## 关键字段
| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|-------|------|---------|-------|------|
| target | Char | public | null | 状态效果所附加的角色对象 |
| type | buffType | public | NEUTRAL | 状态类型，影响UI显示颜色 |
| announced | boolean | public | false | 是否在状态栏中显示名称 |
| revivePersists | boolean | public | false | 状态是否在玩家复活后仍然存在 |
| resistances | HashSet<Class> | protected | new HashSet<>() | 此状态对哪些其他状态有抗性 |
| immunities | HashSet<Class> | protected | new HashSet<>() | 此状态使角色免疫哪些其他状态 |

## buffType枚举
[POSITIVE, NEGATIVE, NEUTRAL - 说明UI颜色显示]

buffType枚举定义了三种状态类型，直接影响UI的颜色显示：
- **POSITIVE**（正面）：显示为绿色，表示有益状态效果
- **NEGATIVE**（负面）：显示为红色，表示有害状态效果  
- **NEUTRAL**（中性）：显示为灰色，表示既不有益也不有害的状态

这些类型主要用于BuffIndicator图标的颜色显示，帮助玩家快速识别状态性质。

## 核心方法 - 附加/分离
| 方法签名 | 返回值 | 说明 |
|----------|--------|------|
| boolean attachTo(Char target) | boolean | 将状态附加到目标角色，成功返回true，失败返回false（如目标对该状态免疫） |
| void detach() | void | 从目标角色分离并移除此状态 |

## 可重写方法
| 方法签名 | 返回值 | 默认行为 | 说明 |
|----------|--------|----------|------|
| boolean act() | boolean | 调用diactivate()并返回true | 状态的行动逻辑，每回合执行一次 |
| int icon() | int | BuffIndicator.NONE | 返回状态图标的ID，用于UI显示 |
| void tintIcon(Image icon) | void | 不执行任何操作 | 对状态图标进行着色，通常根据状态类型调整颜色 |
| float iconFadePercent() | float | 0 | 返回图标淡出百分比（0-1），通常用于即将过期的状态 |
| String iconTextDisplay() | String | "" | 返回在桌面UI大图标上显示的文本，通常显示剩余回合数 |
| void fx(boolean on) | void | 不执行任何操作 | 角色精灵上的视觉效果，on为true时添加效果，false时移除 |
| String heroMessage() | String | 从消息系统获取"heromsg" | 返回英雄获得此状态时显示的消息 |
| String name() | String | 从消息系统获取"name" | 返回状态的本地化名称 |
| String desc() | String | 从消息系统获取"desc" | 返回状态的详细描述 |
| float visualcooldown() | float | cooldown()+1f | 返回用于视觉显示的冷却时间，通常比实际剩余时间多1回合 |

## 静态工厂方法
| 方法签名 | 返回值 | 说明 |
|----------|--------|------|
| <T extends Buff> T append(Char target, Class<T> buffClass) | T | 创建新状态实例并附加到目标，允许同一类型状态重复存在 |
| <T extends FlavourBuff> T append(Char target, Class<T> buffClass, float duration) | T | 创建持续时间状态并设置持续时间，考虑目标的抗性 |
| <T extends Buff> T affect(Char target, Class<T> buffClass) | T | 获取现有状态或创建新状态，防止同一类型状态重复 |
| <T extends FlavourBuff> T affect(Char target, Class<T> buffClass, float duration) | T | 获取或创建持续时间状态并设置持续时间 |
| <T extends FlavourBuff> T prolong(Char target, Class<T> buffClass, float duration) | T | 延长已有状态的持续时间，或创建新状态并设置时间 |
| <T extends CounterBuff> T count(Char target, Class<T> buffclass, float count) | T | 获取或创建计数型状态，并增加计数值 |
| void detach(Char target, Class<? extends Buff> cl) | void | 移除目标身上所有指定类型的状态 |

## 生命周期
[Buff从创建到移除的完整流程]

Buff的完整生命周期如下：

1. **创建**：通过静态工厂方法（如`append()`、`affect()`）创建Buff实例
2. **附加**：调用`attachTo()`方法将状态附加到目标角色
   - 检查目标是否免疫该状态类型
   - 设置target引用
   - 调用`target.add(this)`将状态添加到角色的状态列表
   - 如果附加成功且角色有精灵，则调用`fx(true)`显示视觉效果
3. **激活**：状态被添加到游戏的Actor系统中，每回合按优先级执行`act()`方法
4. **运行**：在`act()`方法中执行状态逻辑
   - 持续时间状态（FlavourBuff）：倒计时并在时间结束时自动分离
   - 计数状态（CounterBuff）：维护计数值
   - 护盾状态（ShieldBuff）：处理伤害吸收
   - 自定义状态：实现特定逻辑（如每回合造成伤害）
5. **分离**：通过以下方式之一移除状态
   - 时间到期自动分离（FlavourBuff）
   - 手动调用`detach()`方法
   - 被其他状态替换或移除
   - 目标死亡（某些状态可能在死亡时触发特殊逻辑）
6. **清理**：状态从Actor系统和角色的状态列表中移除，调用`fx(false)`移除视觉效果

## 常用子类类型
- FlavourBuff - 有持续时间的状态
- CounterBuff - 计数型状态
- ShieldBuff - 护盾型状态

## 使用示例
### 示例1: 创建简单的持续伤害状态
```java
// 创建一个自定义的持续伤害状态
public class Bleeding extends FlavourBuff {
    {
        type = buffType.NEGATIVE;
        announced = true;
    }
    
    @Override
    public boolean act() {
        if (target.isAlive()) {
            target.damage(2, this); // 每回合造成2点伤害
            spend(TICK);
            left -= TICK;
            if (left <= 0) {
                detach();
            }
        } else {
            detach();
        }
        return true;
    }
    
    @Override
    public int icon() {
        return BuffIndicator.BLEEDING;
    }
}

// 使用静态方法添加状态
Bleeding bleeding = Buff.append(hero, Bleeding.class, 5f); // 持续5回合
```

### 示例2: 创建带图标的状态
```java
public class Bless extends FlavourBuff {
    {
        type = buffType.POSITIVE; // 正面状态，显示绿色
    }
    
    @Override
    public int icon() {
        return BuffIndicator.BLESS; // 返回图标ID
    }
    
    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.5f, 1f, 0.5f); // 自定义图标颜色
    }
    
    @Override
    public String heroMessage() {
        return Messages.get(this, "heromsg"); // 英雄获得祝福时的消息
    }
}
```

### 示例3: 使用静态方法添加状态
```java
// 添加状态，不允许重复
Haste haste = Buff.affect(hero, Haste.class, Haste.DURATION);

// 延长已有状态的时间
Buff.prolong(hero, Haste.class, 10f);

// 添加计数型状态
Combo combo = Buff.count(hero, Combo.class, 1); // 增加1点连击

// 批量移除状态
Buff.detach(enemy, Poison.class); // 移除敌人身上所有中毒状态
```

## 相关子类
[列出常见Buff子类: Poison, Burning, Paralysis, Haste, Bless等]

**负面状态（NEGATIVE）**：
- `Poison` - 中毒：每回合造成伤害
- `Burning` - 燃烧：每回合造成伤害并可能烧毁物品
- `Paralysis` - 麻痹：无法移动和攻击
- `Blindness` - 失明：视野受限

**正面状态（POSITIVE）**：
- `Haste` - 加速：移动和攻击速度提升
- `Bless` - 祝福：命中率和伤害提升
- `Regeneration` - 再生：每回合恢复生命值
- `Recharging` - 充能：加快魔力恢复

**中性/特殊状态（NEUTRAL）**：
- `Invisibility` - 隐身：对敌人不可见
- `Levitation` - 漂浮：无视地形效果

## 常见错误
1. **忘记设置状态类型**：未设置`type`字段会导致UI显示为灰色，用户无法直观判断状态好坏
2. **错误的持续时间管理**：在自定义`act()`方法中忘记调用`spend(TICK)`和更新剩余时间
3. **内存泄漏**：在`detach()`方法中忘记清理引用或停止相关效果
4. **忽略抗性机制**：创建状态时不考虑目标的抗性，应该使用提供的工厂方法自动处理抗性计算
5. **重复状态问题**：使用`append()`而非`affect()`导致同一类型状态重复叠加，可能导致意外行为
6. **保存/加载问题**：添加新字段时忘记在`storeInBundle()`和`restoreFromBundle()`中处理序列化
7. **视觉效果不一致**：在`attachTo()`中添加视觉效果但忘记在`detach()`中移除
8. **生命周期错误**：在状态分离后仍然尝试访问`target`字段，应该始终检查`target != null`