# InventoryClericSpell 类详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/InventoryClericSpell.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | abstract class |
| **继承关系** | extends ClericSpell |
| **代码行数** | 74 |
| **中文名称** | 物品选择牧师法术基类 |

---

## 类概述

`InventoryClericSpell` 是需要从背包中选择物品的牧师法术的抽象基类。该类扩展了 `ClericSpell`，提供了统一的物品选择界面和物品验证机制。所有需要玩家从背包中选择特定物品进行操作的法术都应该继承此类。

**核心功能**：
1. **物品选择界面**：自动打开背包选择器（WndBag）
2. **物品过滤**：允许子类定义可选择的物品条件
3. **背包偏好**：支持指定优先显示的背包类型
4. **抽象回调**：定义 `onItemSelected` 抽象方法供子类处理选择结果

**适用场景**：
- 需要对背包物品施放效果的法术
- 需要消耗或转换特定物品的法术
- 需要与装备物品交互的法术

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
    
    class InventoryClericSpell {
        +void onCast(HolyTome, Hero)
        #String inventoryPrompt()
        #Class~? extends Bag~ preferredBag()
        #boolean usableOnItem(Item)
        #abstract void onItemSelected(HolyTome, Hero, Item)
    }
    
    ClericSpell <|-- InventoryClericSpell
```

---

## 核心方法

### onCast(HolyTome tome, Hero hero)

**签名**: `@Override public void onCast(HolyTome tome, Hero hero)`

**功能**: 触发物品选择界面

**实现逻辑**:
1. 调用 `GameScene.selectItem()` 打开背包选择器
2. 设置选择监听器，当玩家选择物品时调用 `onItemSelected`
3. 使用 `inventoryPrompt()` 获取选择提示文本
4. 应用 `preferredBag()` 和 `usableOnItem()` 进行过滤

### onItemSelected(HolyTome tome, Hero hero, Item item)

**签名**: `protected abstract void onItemSelected(HolyTome tome, Hero hero, Item item)`

**功能**: 处理物品选择结果（由子类实现）

**参数**:
- `tome`: HolyTome - 神圣典籍实例
- `hero`: Hero - 施法英雄
- `item`: Item - 选择的物品（可能为 null）

**注意事项**:
- `item` 可能为 null（玩家取消选择）
- 子类必须处理无效物品的情况
- 必须在方法末尾调用 `onSpellCast(tome, hero)`

### inventoryPrompt()

**签名**: `protected String inventoryPrompt()`

**功能**: 返回物品选择界面的提示文本

**默认实现**: `Messages.get(this, "prompt")`

**自定义**: 子类可以重写此方法提供自定义提示

### preferredBag()

**签名**: `protected Class<? extends Bag> preferredBag()`

**功能**: 返回优先显示的背包类型

**默认实现**: 返回 null（不指定偏好）

**常见返回值**:
- `null`: 显示所有背包
- `ScrollHolder.class`: 优先显示卷轴背包
- `PotionBandolier.class`: 优先显示药水背包
- `WandHolster.class`: 优先显示法杖背包

### usableOnItem(Item item)

**签名**: `protected boolean usableOnItem(Item item)`

**功能**: 检查物品是否可用于此法术

**默认实现**: 返回 true（所有物品都可用）

**自定义**: 子类应该重写此方法进行物品类型检查

---

## 使用示例

### 基本物品法术实现

```java
public class ExampleInventorySpell extends InventoryClericSpell {
    public static ExampleInventorySpell INSTANCE = new ExampleInventorySpell();
    
    @Override
    protected boolean usableOnItem(Item item) {
        return item instanceof Scroll; // 只能选择卷轴
    }
    
    @Override
    protected Class<? extends Bag> preferredBag() {
        return ScrollHolder.class; // 优先显示卷轴背包
    }
    
    @Override
    protected void onItemSelected(HolyTome tome, Hero hero, Item item) {
        if (item == null) {
            return; // 玩家取消选择
        }
        
        // 执行法术逻辑
        Scroll scroll = (Scroll) item;
        // ...
        
        onSpellCast(tome, hero); // 必须调用
    }
}
```

### 自定义提示和过滤

```java
@Override
protected String inventoryPrompt() {
    return Messages.get(this, "select_weapon");
}

@Override
protected boolean usableOnItem(Item item) {
    return item instanceof Weapon && 
           ((Weapon) item).level() >= 0; // 只能选择未诅咒的武器
}

@Override
protected Class<? extends Bag> preferredBag() {
    return null; // 显示所有背包，因为武器不在特殊背包中
}
```

### 多种物品类型支持

```java
@Override
protected boolean usableOnItem(Item item) {
    return (item instanceof Potion) || 
           (item instanceof Scroll) ||
           (item instanceof Stylus); // 支持多种物品类型
}
```

---

## 注意事项

### 物品验证

子类必须验证物品的有效性：
- **类型检查**: 确保物品是预期类型
- **状态检查**: 检查物品是否未被诅咒、已识别等
- **等级检查**: 验证物品等级是否满足要求
- **唯一性检查**: 防止对同一物品重复使用

### 错误处理

- **空物品**: 处理 `item == null` 的情况（玩家取消）
- **无效物品**: 给出适当的错误提示（使用 `GLog.w()`）
- **消耗确认**: 如果物品会被消耗，考虑添加确认步骤

### 性能考虑

- **高效过滤**: `usableOnItem` 方法应该快速执行
- **避免修改**: 不要在过滤方法中修改物品状态
- **内存管理**: 及时释放对物品的引用

---

## 最佳实践

### 用户体验

1. **明确提示**: 提示文本应该清楚说明需要选择什么类型的物品
2. **智能过滤**: 只显示符合条件的物品，减少用户困惑
3. **合理排序**: 优先显示最常用的背包类型
4. **取消支持**: 允许玩家轻松取消选择

### 代码结构

```java
@Override
protected void onItemSelected(HolyTome tome, Hero hero, Item item) {
    // 1. 处理取消情况
    if (item == null) return;
    
    // 2. 验证物品（虽然已经过滤，但仍需二次验证）
    if (!isValidForSpell(item)) {
        GLog.w(Messages.get(this, "invalid_item"));
        return;
    }
    
    // 3. 执行主要逻辑
    processItem(tome, hero, item);
    
    // 4. 调用父类完成施法
    onSpellCast(tome, hero);
}
```

### 物品类型指南

| 物品类型 | 适用法术 | 注意事项 |
|----------|----------|----------|
| Scrolls | 回忆铭文、记忆祈祷 | 需要已识别 |
| Potions | 净化、祝福 | 需要未诅咒 |
| Weapons | 圣光武器 | 需要可装备 |
| Armor | 神圣护甲 | 需要可装备 |
| Wands | 各种转换法术 | 需要充能 |

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `ClericSpell` | 父类 | 牧师法术基类 |
| `WndBag` | 依赖 | 背包选择窗口 |
| `GameScene` | 使用者 | 游戏场景，提供选择器 |
| `Item` | 参数 | 被选择的物品基类 |
| `Bag` | 过滤 | 背包类型，用于偏好设置 |
| `Messages` | 依赖 | 本地化消息系统 |
| `GLog` | 依赖 | 游戏日志系统（用于错误提示） |

---

## 典型子类

在当前代码库中，以下法术可能继承自 `InventoryClericSpell`：

- **RecallInscription**: 回忆铭文，选择卷轴进行回忆
- **MnemonicPrayer**: 记忆祈祷，选择物品进行记忆增强

注意：根据现有代码分析，大部分牧师法术实际上是无目标或目标选择类型，真正需要物品选择的法术相对较少。但 `InventoryClericSpell` 为未来可能的物品交互法术提供了完整的框架。

### 实现要点

如果要实现物品选择法术，需要特别注意：

1. **物品所有权**: 确保物品属于当前英雄
2. **背包同步**: 处理物品从一个背包移动到另一个背包的情况
3. **堆叠处理**: 正确处理可堆叠物品（如药水、卷轴）的数量
4. **视觉反馈**: 在物品选择和使用时提供清晰的视觉效果