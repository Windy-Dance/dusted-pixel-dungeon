# 调试工作流程指南

## 概述
本指南介绍 Shattered Pixel Dungeon 开发中常见问题的诊断方法和调试技巧。

---

## 常见问题诊断

### 1. 自定义物品不出现

**症状**：编译成功，游戏中找不到物品

**诊断步骤**：
1. 检查 `Generator.java` 中是否正确注册
2. 确认 `defaultProbs` 数组是否添加了对应概率
3. 验证物品是否有正确的构造函数

**调试代码**：
```java
// 在游戏启动后检查（可在 TitleScene 或 GameScene 中临时添加）
for (Class<?> cls : Generator.Category.ARTIFACT.classes) {
    GLog.i("Registered artifact: " + cls.getSimpleName());
}

// 检查物品实例化
try {
    Item testItem = new MyCustomItem();
    GLog.i("Item created: " + testItem.name());
} catch (Exception e) {
    GLog.n("Failed to create item: " + e.getMessage());
}
```

**常见原因**：
- 忘记在 `Generator.Category.XXX.classes` 数组中添加类
- 概率数组长度与类数组长度不匹配
- 类没有无参构造函数

---

### 2. 自定义效果不触发

**症状**：物品存在但效果不生效

**诊断步骤**：
1. 在相关方法开头添加日志
2. 检查方法是否被正确重写
3. 确认父类方法是否被调用

**调试代码**：
```java
@Override
public void execute(Hero hero, String action) {
    GLog.i("execute called with action: " + action);  // 验证方法被调用
    super.execute(hero, action);
    
    if (action.equals(AC_SPECIAL)) {
        GLog.i("Special action triggered!");  // 验证条件分支
        // ... 效果代码
    }
}

@Override
public ArrayList<String> actions(Hero hero) {
    ArrayList<String> actions = super.actions(hero);
    GLog.i("actions() called, returning " + actions.size() + " actions");
    actions.add(AC_SPECIAL);
    return actions;
}
```

**常见原因**：
- 方法签名与父类不匹配（参数类型、返回值）
- 忘记调用 `super.method()` 导致父类逻辑丢失
- 条件判断逻辑错误

---

### 3. Buff 不附加

**症状**：被动效果不生效

**诊断步骤**：
1. 检查 `passiveBuff()` 返回值
2. 在 `attachTo()` 中添加日志
3. 确认目标有效

**调试代码**：
```java
@Override
protected ArtifactBuff passiveBuff() {
    GLog.i("passiveBuff() called, creating new buff");
    return new MyArtifactBuff();
}

public class MyArtifactBuff extends ArtifactBuff {
    
    @Override
    public boolean attachTo(Char target) {
        boolean result = super.attachTo(target);
        GLog.i("Buff attach result: " + result + " to " + target.getClass().getSimpleName());
        
        if (result) {
            GLog.i("Buff successfully attached!");
        } else {
            GLog.w("Buff attachment failed!");
        }
        
        return result;
    }
    
    @Override
    public boolean act() {
        GLog.i("Buff act() called");  // 验证每回合执行
        spend(TICK);
        return true;
    }
}
```

**常见原因**：
- `passiveBuff()` 返回 null
- `attachTo()` 返回 false
- 目标已有相同类型的 Buff

---

### 4. 充能不增长

**症状**：神器充能条不变化

**诊断步骤**：
1. 检查 `charge()` 方法签名
2. 验证 `partialCharge` 和 `charge` 的处理逻辑
3. 确认 `updateQuickslot()` 被调用

**调试代码**：
```java
@Override
public void charge(Hero target, float amount) {
    GLog.i("charge() called with amount: " + amount);
    GLog.i("Before: charge=" + charge + ", partialCharge=" + partialCharge);
    
    if (cursed) {
        GLog.w("Item is cursed, not charging");
        return;
    }
    
    if (target.buff(MagicImmune.class) != null) {
        GLog.w("MagicImmune active, not charging");
        return;
    }
    
    if (charge >= chargeCap) {
        GLog.i("Charge at cap: " + charge + "/" + chargeCap);
        return;
    }
    
    partialCharge += amount;
    GLog.i("After partialCharge: " + partialCharge);
    
    while (partialCharge >= 1 && charge < chargeCap) {
        partialCharge--;
        charge++;
        GLog.p("Charge increased! Now: " + charge + "/" + chargeCap);
    }
    
    updateQuickslot();
}

// 验证 charge() 是否被调用
public class MyArtifactBuff extends ArtifactBuff {
    @Override
    public void charge(Hero target, float amount) {
        GLog.i("ArtifactBuff.charge() called with: " + amount);
        MyArtifact.this.charge(target, amount);
    }
}
```

**常见原因**：
- 神器被诅咒
- `MagicImmune` Buff 激活
- 充能已达上限
- `charge()` 方法没有被任何地方调用

---

### 5. 存档数据丢失

**症状**：读档后数据不正确

**诊断步骤**：
1. 检查 `storeInBundle()` 是否包含所有字段
2. 验证 `restoreFromBundle()` 是否正确恢复
3. 确认字段名常量唯一

**调试代码**：
```java
private static final String MY_FIELD = "my_field";
private static final String MY_CHARGE = "my_charge";

@Override
public void storeInBundle(Bundle bundle) {
    super.storeInBundle(bundle);
    bundle.put(MY_FIELD, myField);
    bundle.put(MY_CHARGE, charge);
    GLog.i("Saved: myField=" + myField + ", charge=" + charge);
}

@Override
public void restoreFromBundle(Bundle bundle) {
    super.restoreFromBundle(bundle);
    myField = bundle.getInt(MY_FIELD);
    charge = bundle.getInt(MY_CHARGE);
    GLog.i("Restored: myField=" + myField + ", charge=" + charge);
}
```

**常见原因**：
- 字段名常量与其他类冲突
- 忘记调用 `super.storeInBundle()` 或 `super.restoreFromBundle()`
- 类型不匹配（存的是 int，读的是 float）

---

### 6. 编译错误

**常见编译错误**：

| 错误类型 | 原因 | 解决方案 |
|---------|------|---------|
| `cannot find symbol` | 导入缺失或类名错误 | 添加正确的 import 语句 |
| `method does not override` | 方法签名不匹配 | 检查参数类型和返回值 |
| `incompatible types` | 类型转换错误 | 使用正确的类型或添加强制转换 |
| `cannot access private member` | 访问权限问题 | 修改访问修饰符或使用 getter |

---

## 使用 GLog 调试

GLog 是游戏内置的日志系统，消息会显示在游戏界面左下角。

### 日志级别

```java
GLog.i("Info message");      // 白色 - 普通信息
GLog.p("Positive message");  // 绿色 - 成功/正面信息
GLog.n("Negative message");  // 红色 - 错误/负面信息
GLog.w("Warning message");   // 橙色 - 警告
GLog.h("Highlight message"); // 黄色 - 高亮
GLog.newLine();              // 空行
```

### 日志最佳实践

```java
// 好的做法：包含上下文信息
GLog.i("MyArtifact.charge() called, current charge: " + charge);

// 不好的做法：信息不足
GLog.i("called");

// 调试完成后记得移除日志
// 或使用条件编译
private static final boolean DEBUG = true;

if (DEBUG) {
    GLog.i("Debug: " + value);
}
```

---

## 控制台命令

开发版本支持控制台命令（需要启用调试模式）。

### 启用调试模式

在代码中设置：

```java
import com.dustedpixel.dustedpixeldungeon.SPDSettings;

// 启用调试
SPDSettings.debug(true);
```

### 常用命令

| 命令 | 说明 | 示例 |
|------|------|------|
| `give <ClassName>` | 给予物品 | `give TimeAmulet` |
| `spawn <ClassName>` | 生成怪物 | `spawn Rat` |
| `level <N>` | 设置等级 | `level 10` |
| `depth <N>` | 传送到指定层 | `depth 5` |
| `gold <N>` | 设置金币 | `gold 1000` |
| `hp <N>` | 设置生命值 | `hp 100` |
| `str <N>` | 设置力量值 | `str 15` |
| `identify` | 鉴定所有物品 | `identify` |
| `upgrade` | 升级装备 | `upgrade` |
| `god` | 无敌模式 | `god` |
| `die` | 测试死亡 | `die` |

---

## 断点调试

### 在 IDE 中设置断点

1. 在代码行号处点击设置断点
2. 以调试模式运行游戏
3. 当执行到断点时暂停
4. 检查变量值和调用栈

### 关键断点位置

| 断点位置 | 用途 |
|---------|------|
| `Hero.earnExp()` | 验证经验获取和充能触发 |
| `Mob.die()` | 验证敌人死亡处理 |
| `Buff.attachTo()` | 验证 Buff 附加 |
| `Item.execute()` | 验证物品使用 |
| `Artifact.charge()` | 验证充能逻辑 |

---

## 常见错误信息

### NullPointerException

**原因**：尝试访问 null 对象的成员

**调试**：
```java
// 检查可能为 null 的对象
if (target == null) {
    GLog.n("Error: target is null!");
    return;
}

// 使用安全访问
Char ch = Actor.findChar(pos);
if (ch != null) {
    ch.damage(damage, this);
}
```

### ClassCastException

**原因**：类型转换错误

**调试**：
```java
// 使用 instanceof 检查
if (item instanceof Weapon) {
    Weapon weapon = (Weapon) item;
    // ...
} else {
    GLog.w("Item is not a Weapon: " + item.getClass().getSimpleName());
}
```

### ConcurrentModificationException

**原因**：在遍历集合时修改它

**调试**：
```java
// 错误：在遍历时修改
for (Buff b : target.buffs()) {
    if (shouldRemove(b)) {
        b.detach();  // 抛出异常！
    }
}

// 正确：使用副本或迭代器
for (Buff b : new ArrayList<>(target.buffs())) {
    if (shouldRemove(b)) {
        b.detach();
    }
}
```

### IllegalArgumentException

**原因**：方法参数无效

**调试**：
```java
// 添加参数验证
public void setCharge(int value) {
    if (value < 0) {
        GLog.w("Invalid charge value: " + value);
        return;
    }
    if (value > chargeCap) {
        value = chargeCap;
    }
    charge = value;
}
```

---

## 性能调试

### 检查帧率

```java
// 在 GameScene 或相关场景中
@Override
public void update() {
    long start = System.currentTimeMillis();
    super.update();
    long elapsed = System.currentTimeMillis() - start;
    
    if (elapsed > 16) {  // 超过16ms表示掉帧
        GLog.w("Frame took " + elapsed + "ms");
    }
}
```

### 内存使用

```java
Runtime runtime = Runtime.getRuntime();
long usedMemory = runtime.totalMemory() - runtime.freeMemory();
GLog.i("Memory: " + (usedMemory / 1024 / 1024) + "MB");
```

---

## 验证清单

### 新物品验证

- [ ] 物品能在游戏中获得（give 命令或掉落）
- [ ] 物品名称正确显示
- [ ] 物品描述正确显示
- [ ] 物品图标正确
- [ ] 使用功能正常（如适用）
- [ ] 装备功能正常（如适用）
- [ ] 存档/读档数据正确

### 新 Buff 验证

- [ ] Buff 正确附加到目标
- [ ] Buff 图标正确显示
- [ ] 效果按预期触发
- [ ] Buff 正确移除
- [ ] 存档/读档数据正确

### 充能系统验证

- [ ] 充能条正确显示
- [ ] 击杀敌人时充能增长
- [ ] 充能达到上限时停止
- [ ] 使用能力时正确消耗充能
- [ ] 诅咒状态下充能不能使用

---

## 相关资源

- [游戏生命周期与集成指南](game-lifecycle-guide.md)
- [测试指南](testing-guide.md)
- [调试工具指南](debug-tools.md)