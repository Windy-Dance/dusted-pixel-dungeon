# TargetedClericSpell 类详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/TargetedClericSpell.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | abstract class |
| **继承关系** | extends ClericSpell |
| **代码行数** | 59 |
| **中文名称** | 目标选择牧师法术基类 |

---

## 类概述

`TargetedClericSpell` 是需要目标选择的牧师法术的抽象基类。该类扩展了 `ClericSpell`，提供了统一的目标选择界面和弹道处理机制。所有需要玩家点选目标位置或敌人的法术都应该继承此类。

**核心功能**：
1. **目标选择界面**：自动打开单元格选择器（CellSelector）
2. **弹道集成**：与游戏的弹道系统（Ballistica）集成
3. **抽象回调**：定义 `onTargetSelected` 抽象方法供子类处理目标结果

**适用场景**：
- 需要指向特定位置的法术（如圣光长矛）
- 需要选择敌人目标的法术（如审判）
- 需要区域效果但需要指定中心点的法术

---

## 类关系图

```mermaid
classDiagram
    class ClericSpell {
        <<abstract>>
        +abstract void onCast(HolyTome, Hero)
        +boolean usesTargeting()
        +int targetingFlags()
    }
    
    class TargetedClericSpell {
        +void onCast(HolyTome, Hero)
        +int targetingFlags()
        #String targetingPrompt()
        #abstract void onTargetSelected(HolyTome, Hero, Integer)
    }
    
    ClericSpell <|-- TargetedClericSpell
```

---

## 核心方法

### onCast(HolyTome tome, Hero hero)

**签名**: `@Override public void onCast(HolyTome tome, Hero hero)`

**功能**: 触发目标选择界面

**实现逻辑**:
1. 调用 `GameScene.selectCell()` 打开单元格选择器
2. 设置选择监听器，当玩家选择目标时调用 `onTargetSelected`
3. 使用 `targetingPrompt()` 获取选择提示文本

### onTargetSelected(HolyTome tome, Hero hero, Integer target)

**签名**: `protected abstract void onTargetSelected(HolyTome tome, Hero hero, Integer target)`

**功能**: 处理目标选择结果（由子类实现）

**参数**:
- `tome`: HolyTome - 神圣典籍实例
- `hero`: Hero - 施法英雄
- `target`: Integer - 选择的目标单元格坐标（可能为 null）

**注意事项**:
- `target` 可能为 null（玩家取消选择）
- 子类必须处理无效目标的情况
- 必须在方法末尾调用 `onSpellCast(tome, hero)`

### targetingPrompt()

**签名**: `protected String targetingPrompt()`

**功能**: 返回目标选择界面的提示文本

**默认实现**: `Messages.get(this, "prompt")`

**自定义**: 子类可以重写此方法提供自定义提示

### targetingFlags()

**签名**: `@Override public int targetingFlags()`

**功能**: 返回弹道标志

**默认实现**: `Ballistica.MAGIC_BOLT`

**可选标志**:
- `Ballistica.MAGIC_BOLT`: 魔法弹道（穿过敌人停止）
- `Ballistica.STOP_TARGET`: 在目标处停止
- `Ballistica.WONT_STOP`: 不会停止（直线穿透）

### usesTargeting()

**继承自父类**: 返回 true（表示需要目标选择）

---

## 使用示例

### 基本目标法术实现

```java
public class ExampleTargetedSpell extends TargetedClericSpell {
    public static ExampleTargetedSpell INSTANCE = new ExampleTargetedSpell();
    
    @Override
    protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
        if (target == null) {
            return; // 玩家取消选择
        }
        
        // 验证目标有效性
        if (!Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "out_of_range"));
            return;
        }
        
        // 执行法术逻辑
        // ...
        
        onSpellCast(tome, hero); // 必须调用
    }
    
    @Override
    public int targetingFlags() {
        return Ballistica.MAGIC_BOLT;
    }
}
```

### 自定义提示文本

```java
@Override
protected String targetingPrompt() {
    return Messages.get(this, "custom_prompt");
}

// 或者直接返回字符串
@Override
protected String targetingPrompt() {
    return "选择要攻击的敌人";
}
```

### 不同的弹道类型

```java
// 停止在目标处（适合单体攻击）
@Override
public int targetingFlags() {
    return Ballistica.STOP_TARGET;
}

// 穿透所有目标（适合范围效果）
@Override
public int targetingFlags() {
    return Ballistica.WONT_STOP;
}
```

---

## 注意事项

### 目标验证

子类必须验证目标的有效性：
- **视野检查**: `Dungeon.level.heroFOV[target]`
- **地图边界**: `Dungeon.level.insideMap(target)`
- **障碍物**: `!Dungeon.level.solid[target]`
- **敌人存在**: `Actor.findChar(target) != null`

### 错误处理

- **空目标**: 处理 `target == null` 的情况（玩家取消）
- **无效目标**: 给出适当的错误提示（使用 `GLog.w()`）
- **距离限制**: 检查施法距离是否在范围内

### 性能考虑

- **避免复杂计算**: 目标选择应该快速响应
- **缓存结果**: 如果需要复杂的路径查找，考虑缓存结果
- **异步处理**: 长时间运行的操作应该分解到多帧

---

## 最佳实践

### 用户体验

1. **清晰提示**: 提示文本应该明确说明需要选择什么
2. **即时反馈**: 目标选择后立即给出视觉反馈
3. **合理默认**: 如果可能，提供智能默认目标
4. **取消支持**: 允许玩家轻松取消选择

### 代码结构

```java
@Override
protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
    // 1. 处理取消情况
    if (target == null) return;
    
    // 2. 验证目标
    if (!isValidTarget(target)) {
        GLog.w(Messages.get(this, "invalid_target"));
        return;
    }
    
    // 3. 执行主要逻辑
    executeSpellLogic(tome, hero, target);
    
    // 4. 调用父类完成施法
    onSpellCast(tome, hero);
}
```

### 弹道选择指南

| 弹道类型 | 适用场景 | 示例法术 |
|----------|----------|----------|
| MAGIC_BOLT | 标准魔法弹道，穿过敌人停止 | 圣光长矛、太阳射线 |
| STOP_TARGET | 在目标处精确停止 | 审判、神圣干预 |
| WONT_STOP | 直线穿透所有目标 | 墙壁之光、辐射 |

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `ClericSpell` | 父类 | 牧师法术基类 |
| `CellSelector` | 依赖 | 单元格选择界面 |
| `GameScene` | 使用者 | 游戏场景，提供选择器 |
| `Ballistica` | 依赖 | 弹道计算系统 |
| `Messages` | 依赖 | 本地化消息系统 |
| `GLog` | 依赖 | 游戏日志系统（用于错误提示） |

---

## 典型子类

以下法术继承自 `TargetedClericSpell`：

- **BeamingRay**: 传送盟友到目标位置
- **DivineIntervention**: 神圣干预，治疗区域内的友方
- **Flash**: 闪光，致盲目标区域的敌人  
- **HolyLance**: 圣光长矛，直线攻击
- **Judgement**: 审判，对目标造成伤害
- **LifeLinkSpell**: 生命链接，连接两个目标
- **MnemonicPrayer**: 记忆祈祷，回忆法术
- **Smite**: 惩击，对目标造成神圣伤害
- **Stasis**: 静滞，暂停目标时间
- **Sunray**: 太阳射线，持续伤害光束
- **WallOfLight**: 光之墙，创建光墙屏障