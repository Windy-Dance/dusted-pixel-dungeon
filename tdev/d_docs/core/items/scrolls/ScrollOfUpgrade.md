# ScrollOfUpgrade.java (升级卷轴) 详细文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/ScrollOfUpgrade.java` |
| **类名** | `ScrollOfUpgrade` |
| **修饰符** | `public` |
| **继承关系** | `extends InventoryScroll` |
| **实现接口** | 无 |
| **代码行数** | 173 行 |
| **许可证** | GNU General Public License v3.0 |

---

## 类职责

`ScrollOfUpgrade`（升级卷轴）是游戏中**最重要且独特的卷轴**，负责：

1. **物品升级**：将目标物品的等级提升+1，增强其属性
2. **诅咒处理**：升级时可能削弱或移除物品上的诅咒
3. **降级清除**：使用时清除英雄身上的降级状态（Degrade）
4. **统计追踪**：记录升级使用次数，关联成就/天赋解锁
5. **确认界面**：提供 `WndUpgrade` 窗口显示升级预览并要求玩家确认

**独特性**：
- `unique = true`：升级卷轴在复活后保留（不像普通物品会丢失）
- 不自动消耗：需要玩家在确认窗口中点击"升级"按钮才消耗

---

## 4. 继承与协作关系

```
┌─────────────────────────────────────────────────────────────────┐
│                           Item                                   │
│                     (物品基类 - 可堆叠/可识别)                    │
└─────────────────────────────────────────────────────────────────┘
                                ▲
                                │ extends
┌─────────────────────────────────────────────────────────────────┐
│                      Scroll (抽象类)                             │
│           卷轴基类 - 定义阅读动作和识别系统                        │
└─────────────────────────────────────────────────────────────────┘
                                ▲
                                │ extends
┌─────────────────────────────────────────────────────────────────┐
│                  InventoryScroll (抽象类)                        │
│       库存卷轴 - 提供物品选择器框架                               │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ 字段: identifiedByUse, preferredBag, itemSelector        │   │
│  │ 方法: doRead(), usableOnItem(), onItemSelected()         │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                                ▲
                                │ extends
┌─────────────────────────────────────────────────────────────────┐
│                    ScrollOfUpgrade                               │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ 字段: icon, unique, talentFactor                         │   │
│  │ 方法: usableOnItem(), onItemSelected(), upgradeItem()    │   │
│  │       reShowSelector(), getSelector()                    │   │
│  │ 静态方法: upgrade(), weakenCurse(), removeCurse()        │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
         │                           │
         │ uses                      │ uses
         ▼                           ▼
┌─────────────────┐         ┌─────────────────┐
│   WndUpgrade    │         │    Degrade      │
│   (升级窗口)    │         │   (降级状态)    │
└─────────────────┘         └─────────────────┘
```

### 关联类

| 类名 | 关系 | 说明 |
|------|------|------|
| `InventoryScroll` | 父类 | 提供物品选择器框架 |
| `WndUpgrade` | 使用 | 升级确认窗口UI |
| `Degrade` | 移除 | 升级时清除降级debuff |
| `Weapon` | 处理 | 武器升级逻辑，处理附魔诅咒 |
| `Armor` | 处理 | 护甲升级逻辑，处理铭文诅咒 |
| `Wand` | 处理 | 法杖升级逻辑 |
| `Ring` | 处理 | 戒指升级逻辑 |
| `Badges` | 通知 | 成就验证 |
| `Statistics` | 更新 | 升级使用统计 |

---

## 静态常量表

本类没有定义静态常量，继承自父类的常量：

| 常量名 | 来源 | 值 | 说明 |
|--------|------|-----|------|
| `AC_READ` | `Scroll` | `"READ"` | 阅读动作标识符 |
| `TIME_TO_READ` | `Scroll` | `1f` | 阅读卷轴所需时间（1回合） |

---

## 实例字段表

| 字段名 | 类型 | 值 | 访问级别 | 说明 |
|--------|------|-----|---------|------|
| `icon` | `int` | `ItemSpriteSheet.Icons.SCROLL_UPGRADE` | 继承自Item | 卷轴图标标识 |
| `preferredBag` | `Class<? extends Bag>` | `Belongings.Backpack.class` | 继承自InventoryScroll | 首选背包类型（主背包） |
| `unique` | `boolean` | `true` | 继承自Item | **唯一物品**：死亡复活后保留 |
| `talentFactor` | `float` | `2f` | 继承自Scroll | 天赋触发强度系数（普通卷轴为1） |

### 继承自 InventoryScroll 的字段

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `identifiedByUse` | `boolean` | 是否通过使用来识别（未鉴定时使用自动识别） |
| `itemSelector` | `WndBag.ItemSelector` | 物品选择器实例 |

---

## 7. 方法详解

### 1. 实例初始化块（第47-54行）

```java
{
    icon = ItemSpriteSheet.Icons.SCROLL_UPGRADE;
    preferredBag = Belongings.Backpack.class;

    unique = true;

    talentFactor = 2f;
}
```

**逐行解释**：

| 行号 | 代码 | 说明 |
|------|------|------|
| 48 | `icon = ItemSpriteSheet.Icons.SCROLL_UPGRADE;` | 设置卷轴图标为升级卷轴专用图标 |
| 49 | `preferredBag = Belongings.Backpack.class;` | 设置首选背包为主背包（而非卷轴袋），因为升级物品可能在任何位置 |
| 51 | `unique = true;` | **关键设置**：标记为唯一物品，死亡后不会丢失 |
| 53 | `talentFactor = 2f;` | 天赋触发强度翻倍，表示这是重要的卷轴 |

---

### 2. usableOnItem() 方法（第56-59行）

```java
@Override
protected boolean usableOnItem(Item item) {
    return item.isUpgradable();
}
```

**逐行解释**：

| 行号 | 代码 | 说明 |
|------|------|------|
| 57-58 | `return item.isUpgradable();` | 返回物品是否可升级。只有可升级的物品才能被选中 |

**设计说明**：
- 重写父类 `InventoryScroll.usableOnItem()`（默认返回true）
- 使用物品自身的 `isUpgradable()` 方法判断
- 大多数装备（武器、护甲、法杖、戒指）可升级
- 消耗品、钥匙等不可升级

---

### 3. onItemSelected() 方法（第61-66行）

```java
@Override
protected void onItemSelected( Item item ) {

    GameScene.show(new WndUpgrade(this, item, identifiedByUse));

}
```

**逐行解释**：

| 行号 | 代码 | 说明 |
|------|------|------|
| 64 | `GameScene.show(new WndUpgrade(this, item, identifiedByUse));` | 显示升级确认窗口。传入：当前卷轴实例、目标物品、是否通过使用识别 |

**流程说明**：
1. 玩家使用卷轴 → 选择物品
2. 选择物品后调用此方法
3. 显示 `WndUpgrade` 窗口让玩家确认升级
4. 玩家可以选择"升级"或"返回"

**与其他库存卷轴的区别**：
- 其他库存卷轴在此方法中直接执行效果
- 升级卷轴打开一个**可取消**的确认窗口
- 这就是为什么升级卷轴不在此处自动消耗

---

### 4. reShowSelector() 方法（第68-72行）

```java
public void reShowSelector(boolean force){
    identifiedByUse = force;
    curItem = this;
    GameScene.selectItem(itemSelector);
}
```

**逐行解释**：

| 行号 | 代码 | 说明 |
|------|------|------|
| 69 | `identifiedByUse = force;` | 设置识别状态标识 |
| 70 | `curItem = this;` | 设置当前物品引用（用于静态回调） |
| 71 | `GameScene.selectItem(itemSelector);` | 重新显示物品选择界面 |

**使用场景**：
- 玩家在 `WndUpgrade` 窗口点击"返回"按钮时调用
- 允许玩家取消升级并选择其他物品
- `force` 参数保持识别状态的一致性

---

### 5. getSelector() 方法（第74-78行）

```java
public WndBag.ItemSelector getSelector(boolean force){
    identifiedByUse = force;
    curItem = this;
    return itemSelector;
}
```

**逐行解释**：

| 行号 | 代码 | 说明 |
|------|------|------|
| 75 | `identifiedByUse = force;` | 设置识别状态标识 |
| 76 | `curItem = this;` | 设置当前物品引用 |
| 77 | `return itemSelector;` | 返回物品选择器实例 |

**使用场景**：
- 被 `WndUpgrade.getItemSelector()` 调用
- 用于在窗口关闭后恢复物品选择器状态

---

### 6. upgradeItem() 方法（第80-147行）—— 核心升级逻辑

```java
public Item upgradeItem( Item item ){
    upgrade( curUser );

    Degrade.detach( curUser, Degrade.class );

    //logic for telling the user when item properties change from upgrades
    //...yes this is rather messy
    if (item instanceof Weapon){
        Weapon w = (Weapon) item;
        boolean wasCursed = w.cursed;
        boolean wasHardened = w.enchantHardened;
        boolean hadCursedEnchant = w.hasCurseEnchant();
        boolean hadGoodEnchant = w.hasGoodEnchant();

        item = w.upgrade();

        if (w.cursedKnown && hadCursedEnchant && !w.hasCurseEnchant()){
            removeCurse( Dungeon.hero );
        } else if (w.cursedKnown && wasCursed && !w.cursed){
            weakenCurse( Dungeon.hero );
        }
        if (wasHardened && !w.enchantHardened){
            GLog.w( Messages.get(Weapon.class, "hardening_gone") );
        } else if (hadGoodEnchant && !w.hasGoodEnchant()){
            GLog.w( Messages.get(Weapon.class, "incompatible") );
        }

    } else if (item instanceof Armor){
        Armor a = (Armor) item;
        boolean wasCursed = a.cursed;
        boolean wasHardened = a.glyphHardened;
        boolean hadCursedGlyph = a.hasCurseGlyph();
        boolean hadGoodGlyph = a.hasGoodGlyph();

        item = a.upgrade();

        if (a.cursedKnown && hadCursedGlyph && !a.hasCurseGlyph()){
            removeCurse( Dungeon.hero );
        } else if (a.cursedKnown && wasCursed && !a.cursed){
            weakenCurse( Dungeon.hero );
        }
        if (wasHardened && !a.glyphHardened){
            GLog.w( Messages.get(Armor.class, "hardening_gone") );
        } else if (hadGoodGlyph && !a.hasGoodGlyph()){
            GLog.w( Messages.get(Armor.class, "incompatible") );
        }

    } else if (item instanceof Wand || item instanceof Ring) {
        boolean wasCursed = item.cursed;

        item = item.upgrade();

        if (item.cursedKnown && wasCursed && !item.cursed){
            removeCurse( Dungeon.hero );
        }

    } else {
        item = item.upgrade();
    }

    Badges.validateItemLevelAquired( item );
    Statistics.upgradesUsed++;
    Badges.validateMageUnlock();

    Catalog.countUse(item.getClass());

    return item;
}
```

**逐段解释**：

#### 6.1 升级动画与降级清除（第81-83行）

| 行号 | 代码 | 说明 |
|------|------|------|
| 81 | `upgrade( curUser );` | 播放升级特效动画 |
| 83 | `Degrade.detach( curUser, Degrade.class );` | **清除英雄身上的降级状态** |

**降级状态**：
- 由某些敌人攻击或陷阱触发
- 降低装备等级
- 升级卷轴使用时会完全清除

#### 6.2 武器升级处理（第87-105行）

```java
if (item instanceof Weapon){
    Weapon w = (Weapon) item;
    boolean wasCursed = w.cursed;                    // 记录升级前是否被诅咒
    boolean wasHardened = w.enchantHardened;         // 记录附魔是否被硬化
    boolean hadCursedEnchant = w.hasCurseEnchant();  // 记录是否有诅咒附魔
    boolean hadGoodEnchant = w.hasGoodEnchant();     // 记录是否有良性附魔

    item = w.upgrade();                              // 执行升级

    // 诅咒附魔被移除 → 显示完全解咒
    if (w.cursedKnown && hadCursedEnchant && !w.hasCurseEnchant()){
        removeCurse( Dungeon.hero );
    } 
    // 诅咒被削弱但未完全移除 → 显示削弱诅咒
    else if (w.cursedKnown && wasCursed && !w.cursed){
        weakenCurse( Dungeon.hero );
    }
    // 硬化附魔丢失 → 警告
    if (wasHardened && !w.enchantHardened){
        GLog.w( Messages.get(Weapon.class, "hardening_gone") );
    } 
    // 良性附魔丢失 → 警告
    else if (hadGoodEnchant && !w.hasGoodEnchant()){
        GLog.w( Messages.get(Weapon.class, "incompatible") );
    }
}
```

**武器升级的特殊逻辑**：
1. **诅咒处理**：升级可能削弱或移除诅咒
2. **附魔风险**：高等级升级可能丢失良性附魔
3. **硬化丢失**：硬化附魔（保护附魔不丢失）在升级后可能失效

#### 6.3 护甲升级处理（第107-125行）

与武器逻辑类似，处理：
- 诅咒状态变化
- 铭文（Glyph）丢失风险
- 硬化铭文丢失警告

#### 6.4 法杖/戒指升级处理（第127-135行）

```java
} else if (item instanceof Wand || item instanceof Ring) {
    boolean wasCursed = item.cursed;
    item = item.upgrade();
    if (item.cursedKnown && wasCursed && !item.cursed){
        removeCurse( Dungeon.hero );
    }
}
```

**简化处理**：
- 法杖和戒指只处理诅咒状态变化
- 没有附魔/铭文系统的复杂性

#### 6.5 普通物品升级（第136-138行）

```java
} else {
    item = item.upgrade();
}
```

**其他物品**：
- 直接调用 `upgrade()` 方法
- 无特殊逻辑

#### 6.6 后处理（第140-144行）

| 行号 | 代码 | 说明 |
|------|------|------|
| 140 | `Badges.validateItemLevelAquired( item );` | 验证物品等级成就 |
| 141 | `Statistics.upgradesUsed++;` | **增加升级使用统计** |
| 142 | `Badges.validateMageUnlock();` | 验证法师职业解锁条件 |
| 144 | `Catalog.countUse(item.getClass());` | 记录物品使用到图鉴 |

---

### 7. upgrade() 静态方法（第149-151行）

```java
public static void upgrade( Hero hero ) {
    hero.sprite.emitter().start( Speck.factory( Speck.UP ), 0.2f, 3 );
}
```

**逐行解释**：

| 行号 | 代码 | 说明 |
|------|------|------|
| 150 | `hero.sprite.emitter().start( Speck.factory( Speck.UP ), 0.2f, 3 );` | 在英雄精灵上播放升级粒子特效 |

**特效参数**：
- `Speck.UP`：升级类型的粒子
- `0.2f`：粒子生成间隔（秒）
- `3`：粒子总数

---

### 8. weakenCurse() 静态方法（第153-156行）

```java
public static void weakenCurse( Hero hero ){
    GLog.p( Messages.get(ScrollOfUpgrade.class, "weaken_curse") );
    hero.sprite.emitter().start( ShadowParticle.UP, 0.05f, 5 );
}
```

**逐行解释**：

| 行号 | 代码 | 说明 |
|------|------|------|
| 154 | `GLog.p( ... );` | 显示正面消息"诅咒被削弱" |
| 155 | `hero.sprite.emitter().start( ShadowParticle.UP, 0.05f, 5 );` | 播放阴影粒子上升特效 |

**触发条件**：
- 升级后物品仍被诅咒，但诅咒强度降低

---

### 9. removeCurse() 静态方法（第158-162行）

```java
public static void removeCurse( Hero hero ){
    GLog.p( Messages.get(ScrollOfUpgrade.class, "remove_curse") );
    hero.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10 );
    Badges.validateClericUnlock();
}
```

**逐行解释**：

| 行号 | 代码 | 说明 |
|------|------|------|
| 159 | `GLog.p( ... );` | 显示正面消息"诅咒被移除" |
| 160 | `hero.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10 );` | 播放更多阴影粒子特效（10个 vs 5个） |
| 161 | `Badges.validateClericUnlock();` | 验证牧师职业解锁条件 |

**触发条件**：
- 诅咒附魔被完全移除
- 物品的诅咒状态完全清除

---

### 10. value() 方法（第164-167行）

```java
@Override
public int value() {
    return isKnown() ? 50 * quantity : super.value();
}
```

**逐行解释**：

| 行号 | 代码 | 说明 |
|------|------|------|
| 166 | `return isKnown() ? 50 * quantity : super.value();` | 已识别时价值50金币×数量，否则使用父类价值 |

**价值对比**：
- 普通卷轴：30金币
- 升级卷轴：50金币（已识别）
- 升级卷轴是最有价值的卷轴之一

---

### 11. energyVal() 方法（第169-172行）

```java
@Override
public int energyVal() {
    return isKnown() ? 10 * quantity : super.energyVal();
}
```

**逐行解释**：

| 行号 | 代码 | 说明 |
|------|------|------|
| 171 | `return isKnown() ? 10 * quantity : super.energyVal();` | 已识别时炼金能量值10×数量 |

**能量值对比**：
- 普通卷轴：6能量
- 升级卷轴：10能量（已识别）
- 高能量值反映其珍贵程度

---

## 11. 使用示例

### 示例1：玩家使用升级卷轴的基本流程

```java
// 1. 玩家点击升级卷轴
ScrollOfUpgrade scroll = hero.belongings.getItem(ScrollOfUpgrade.class);
scroll.execute(hero, Scroll.AC_READ);

// 2. doRead() 被调用（继承自 InventoryScroll）
//    - 如果未识别，自动识别
//    - 显示物品选择界面

// 3. 玩家选择一把武器
//    - onItemSelected(weapon) 被调用
//    - 显示 WndUpgrade 确认窗口

// 4. 玩家点击"升级"按钮
//    - WndUpgrade 调用 scroll.upgradeItem(weapon)
//    - 武器等级+1
//    - 播放特效，更新统计
```

### 示例2：程序化使用升级卷轴

```java
// 直接对物品执行升级（跳过UI）
ScrollOfUpgrade scroll = new ScrollOfUpgrade();
scroll.curUser = hero;
Item upgraded = scroll.upgradeItem(weapon);
// 注意：这种方式不会消耗卷轴，需要手动处理
```

### 示例3：检查物品是否可升级

```java
// 通过 usableOnItem 判断
ScrollOfUpgrade scroll = new ScrollOfUpgrade();
if (scroll.usableOnItem(item)) {
    // 物品可以被升级
}
```

---

## 注意事项

### 1. 唯一物品特性

```java
unique = true;  // 升级卷轴是唯一物品
```

- **死亡后保留**：使用未祝福的安赫复活时，升级卷轴不会丢失
- **不会出现在遗骸中**：无法通过玩家遗骸传递给其他玩家
- **始终可堆叠**：多张升级卷轴会自动合并

### 2. 确认窗口机制

与其他库存卷轴不同，升级卷轴：

| 特性 | 普通库存卷轴 | 升级卷轴 |
|------|-------------|---------|
| 选择物品后 | 直接执行效果 | 显示确认窗口 |
| 可否取消 | 否（已消耗） | 是（可返回重选） |
| 音效播放时机 | 选择物品后 | 确认升级后 |
| 卷轴消耗时机 | 选择物品时 | 确认升级时 |

### 3. 诅咒处理规则

升级卷轴对诅咒的影响：

| 物品类型 | 诅咒状态 | 升级效果 |
|---------|---------|---------|
| 武器 | 有诅咒附魔 | 可能完全移除诅咒附魔 |
| 武器 | 仅被诅咒 | 可能削弱诅咒 |
| 护甲 | 有诅咒铭文 | 可能完全移除诅咒铭文 |
| 护甲 | 仅被诅咒 | 可能削弱诅咒 |
| 法杖/戒指 | 被诅咒 | 可能削弱或移除诅咒 |
| 其他物品 | 被诅咒 | 可能削弱或移除诅咒 |

### 4. 附魔/铭文丢失风险

高等级升级时可能丢失良性附魔/铭文：

```
附魔丢失概率 = min(100, 10 × 2^(等级-4))
```

**示例**：
- 等级+4：10%
- 等级+5：20%
- 等级+6：40%
- 等级+7：80%
- 等级+8+：100%

**硬化附魔**（降低风险）：
```
硬化附魔丢失概率 = min(100, 10 × 2^(等级-6))
```

### 5. 与 MagicalInfusion 的区别

| 特性 | ScrollOfUpgrade | MagicalInfusion |
|------|-----------------|-----------------|
| 获取方式 | 地牢掉落 | 炼金制作 |
| 附魔风险 | 有 | 无（100%保留） |
| 诅咒处理 | 同样处理 | 同样处理 |
| 可堆叠 | 是 | 是 |

---

## 最佳实践

### 1. 升级策略建议

```java
// 推荐升级优先级（高到低）：
// 1. 主要武器（输出核心）
// 2. 主要护甲（生存保障）
// 3. 法杖（法师职业）
// 4. 戒指（辅助增益）
```

### 2. 附魔保护策略

```java
// 在高等级升级前考虑：
// - 使用硬化附魔降低风险
// - 使用 MagicalInfusion 替代
// - 准备重新附魔的手段
```

### 3. 诅咒清除利用

```java
// 升级卷轴可以作为解咒手段：
// - 对被诅咒的装备使用可能解除诅咒
// - 比专门的解咒卷轴更可控（有确认窗口）
// - 同时提升装备等级
```

### 4. 扩展开发建议

如果需要创建类似的升级物品：

```java
public class CustomUpgrader extends InventoryScroll {
    {
        icon = ItemSpriteSheet.Icons.CUSTOM;
        preferredBag = Belongings.Backpack.class;
        unique = false;  // 根据需求设置
        talentFactor = 1.5f;
    }
    
    @Override
    protected boolean usableOnItem(Item item) {
        // 自定义可用条件
        return item.isUpgradable() && item.level() < 10;
    }
    
    @Override
    protected void onItemSelected(Item item) {
        // 自定义升级逻辑
    }
}
```

---

## 相关文档

- [Scroll.md](./Scroll.md) - 卷轴基类文档
- [Item.md](../Item.md) - 物品基类文档
- [InventoryScroll.java](../../../../../../../core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/InventoryScroll.java) - 库存卷轴抽象类
- [WndUpgrade.java](../../../../../../../core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndUpgrade.java) - 升级确认窗口

---

## 更新历史

| 日期 | 版本 | 变更说明 |
|------|------|---------|
| 2026-03-26 | 1.0 | 初始文档创建 |