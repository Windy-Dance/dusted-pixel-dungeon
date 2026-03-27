# ClericSpell 类详解

## 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/ClericSpell.java |
| **包名** | com.dustedpixel.dustedpixeldungeon.actors.hero.spells |
| **类类型** | abstract class |
| **继承关系** | extends Object |
| **代码行数** | 240 |
| **中文名称** | 牧师法术基类 |

---

## 类概述

`ClericSpell` 是所有牧师法术的抽象基类。该类定义了牧师法术的核心接口和通用行为，包括充能消耗、施法逻辑、天赋检查等。所有具体的牧师法术都必须继承此类并实现其抽象方法。

**核心职责**：
1. **法术框架**：提供统一的法术执行框架
2. **充能管理**：处理法术充能消耗和恢复
3. **天赋集成**：与牧师天赋系统集成
4. **效果触发**：在施法后触发相关效果（如屏障、圣光武器延长等）

**设计模式**：
- **模板方法模式**：定义了 `onCast` 抽象方法供子类实现
- **单例模式**：所有具体法术都使用 INSTANCE 单例实例

---

## 类关系图

```mermaid
classDiagram
    class ClericSpell {
        <<abstract>>
        +abstract void onCast(HolyTome, Hero)
        +float chargeUse(Hero)
        +boolean canCast(Hero)
        +String name()
        +String shortDesc()
        +String desc()
        +boolean usesTargeting()
        +int targetingFlags()
        +int icon()
        +void onSpellCast(HolyTome, Hero)
        +static ArrayList~ClericSpell~ getSpellList(Hero, int)
        +static ArrayList~ClericSpell~ getAllSpells()
    }
    
    class TargetedClericSpell {
        +void onCast(HolyTome, Hero)
        +int targetingFlags()
        #abstract void onTargetSelected(HolyTome, Hero, Integer)
    }
    
    class InventoryClericSpell {
        +void onCast(HolyTome, Hero)
        #abstract void onItemSelected(HolyTome, Hero, Item)
    }
    
    ClericSpell <|-- TargetedClericSpell
    ClericSpell <|-- InventoryClericSpell
```

---

## 静态方法

### getSpellList(Hero cleric, int tier)

**签名**: `public static ArrayList<ClericSpell> getSpellList(Hero cleric, int tier)`

**功能**: 根据英雄的天赋和职业获取指定等级的可用法术列表

**参数**:
- `cleric`: Hero - 牧师英雄实例
- `tier`: int - 法术等级（1-4）

**返回值**: ArrayList<ClericSpell> - 可用法术列表

**实现逻辑**:
- **1级法术**：基础法术（引导之光、圣光武器、神圣护甲）
- **2级法术**：需要对应天赋解锁
- **3级法术**：根据职业分支（祭司/圣骑士）和天赋解锁
- **4级法术**：高级法术，全部需要对应天赋解锁

### getAllSpells()

**签名**: `public static ArrayList<ClericSpell> getAllSpells()`

**功能**: 返回所有牧师法术的完整列表

**返回值**: ArrayList<ClericSpell> - 包含所有27个法术的列表

---

## 抽象方法

### onCast(HolyTome tome, Hero hero)

**签名**: `public abstract void onCast(HolyTome tome, Hero hero)`

**功能**: 执行法术的具体逻辑（由子类实现）

**参数**:
- `tome`: HolyTome - 神圣典籍实例
- `hero`: Hero - 施法的英雄

---

## 核心方法

### chargeUse(Hero hero)

**签名**: `public float chargeUse(Hero hero)`

**功能**: 返回施放此法术所需的充能数量

**默认实现**: 返回 1f（子类可重写修改）

### canCast(Hero hero)

**签名**: `public boolean canCast(Hero hero)`

**功能**: 检查英雄是否可以施放此法术

**默认实现**: 返回 true（子类通常重写以检查天赋）

### name(), shortDesc(), desc()

**功能**: 返回法术的名称和描述文本

**实现**: 通过 Messages.get() 获取本地化文本

### usesTargeting() 和 targetingFlags()

**功能**: 定义法术的目标选择行为

**默认实现**: 
- `usesTargeting()` 返回 false
- `targetingFlags()` 返回 -1（无目标选择）

### onSpellCast(HolyTome tome, Hero hero)

**签名**: `public void onSpellCast(HolyTome tome, Hero hero)`

**功能**: 处理施法后的通用逻辑

**关键逻辑**:
1. **解除隐身**: `Invisibility.dispel()`
2. **饱食法术**: 如果有饱食法术天赋，为目标和盟友添加屏障
3. **消耗充能**: `tome.spendCharge(chargeUse(hero))`
4. **神器使用**: `Talent.onArtifactUsed(hero)`
5. **圣骑士增强**: 延长圣光武器和神圣护甲的持续时间
6. **升天形态**: 增加升天形态的护盾值

---

## 内部结构

### 法术等级系统

牧师法术分为4个等级：
- **1级**: 基础法术，无需天赋
- **2级**: 需要对应天赋解锁  
- **3级**: 职业分支相关，需要天赋解锁
- **4级**: 高级法术，全部需要天赋解锁

### 目标选择类型

1. **无目标**: 直接施放（如祝福术）
2. **目标选择**: 需要点选目标（继承 TargetedClericSpell）
3. **物品选择**: 需要选择背包物品（继承 InventoryClericSpell）

### 充能消耗模式

- **标准消耗**: 1点充能
- **高消耗**: 2点或更多充能（如保护光环）
- **天赋影响**: 某些天赋可能影响充能消耗

---

## 使用示例

### 创建自定义法术

```java
public class CustomSpell extends ClericSpell {
    public static CustomSpell INSTANCE = new CustomSpell();
    
    @Override
    public void onCast(HolyTome tome, Hero hero) {
        // 自定义施法逻辑
        // ...
        onSpellCast(tome, hero); // 必须调用父类方法
    }
    
    @Override
    public float chargeUse(Hero hero) {
        return 1.5f; // 自定义充能消耗
    }
    
    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.CUSTOM_TALENT);
    }
}
```

### 目标选择法术

```java
public class TargetedSpell extends TargetedClericSpell {
    public static TargetedSpell INSTANCE = new TargetedSpell();
    
    @Override
    protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
        // 处理目标选择结果
        // ...
        onSpellCast(tome, hero);
    }
    
    @Override
    public int targetingFlags() {
        return Ballistica.MAGIC_BOLT; // 设置弹道类型
    }
}
```

---

## 注意事项

### 继承规范

1. **必须实现 onCast**: 所有子类必须实现抽象方法
2. **调用父类方法**: 在 onCast 或 onTargetSelected 中必须调用 onSpellCast
3. **单例模式**: 使用 INSTANCE 静态字段提供唯一实例
4. **天赋检查**: 通过重写 canCast 方法检查必要天赋

### 性能考虑

1. **轻量级**: 法术对象应该是轻量级的，避免存储大量状态
2. **无状态**: 法术本身不保存游戏状态，状态通过 Buff 系统管理
3. **高效检查**: canCast 方法应该快速执行，避免复杂计算

### 本地化支持

所有文本都应该通过 Messages.get() 获取，确保多语言支持：
- `Messages.get(this, "name")` - 法术名称
- `Messages.get(this, "desc")` - 法术描述  
- `Messages.get(this, "prompt")` - 目标选择提示（如适用）

---

## 相关类

| 类名 | 关系 | 说明 |
|------|------|------|
| `HolyTome` | 使用者 | 神圣典籍，法术的载体和充能管理器 |
| `Hero` | 施法者 | 英雄角色，拥有天赋和状态 |
| `TargetedClericSpell` | 子类 | 需要目标选择的法术基类 |
| `InventoryClericSpell` | 子类 | 需要物品选择的法术基类 |
| `Talent` | 依赖 | 天赋系统，控制法术解锁 |
| `Buff` | 效果 | 法术产生的持续效果 |

---

## 最佳实践

### 法术设计原则

1. **单一职责**: 每个法术应该有明确的单一功能
2. **平衡性**: 充能消耗应该与法术强度匹配
3. **清晰反馈**: 施法时应该有明确的视觉和音频反馈
4. **错误处理**: 目标无效时应该给出适当提示

### 代码组织

```java
// 推荐的法术类结构
public class ExampleSpell extends ClericSpell {
    public static ExampleSpell INSTANCE = new ExampleSpell();
    
    // 图标
    @Override public int icon() { return HeroIcon.EXAMPLE; }
    
    // 描述（包含动态参数）
    @Override public String desc() { 
        return Messages.get(this, "desc", parameter1, parameter2) + 
               "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }
    
    // 充能消耗
    @Override public float chargeUse(Hero hero) { return 1f; }
    
    // 天赋检查
    @Override public boolean canCast(Hero hero) { 
        return super.canCast(hero) && hero.hasTalent(Talent.EXAMPLE_TALENT); 
    }
    
    // 主要逻辑
    @Override public void onCast(HolyTome tome, Hero hero) {
        // 实现逻辑...
        onSpellCast(tome, hero);
    }
}