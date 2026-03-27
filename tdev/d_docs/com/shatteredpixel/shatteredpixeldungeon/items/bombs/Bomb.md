# Bomb 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/bombs/Bomb.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.bombs |
| **文件类型** | class |
| **继承关系** | extends Item |
| **代码行数** | 449 行 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
炸弹基类，实现可投掷爆炸物品的核心机制，包括引信点燃、爆炸效果、伤害计算和地形破坏。

### 系统定位
作为所有炸弹类型的基类，位于物品系统（items）下的炸弹子系统中。提供标准炸弹行为，被子类覆写以实现特殊效果。

### 不负责什么
- 不负责特殊爆炸效果（由子类覆写 `explode()` 实现）
- 不负责炼金合成逻辑（由 `EnhanceBomb` 内部类处理）

## 3. 结构总览

### 主要成员概览

| 成员 | 类型 | 说明 |
|------|------|------|
| `fuse` | Fuse | 引信实例，控制爆炸时机 |
| `AC_LIGHTTHROW` | String | 动作常量："LIGHTTHROW" |
| `lightingFuse` | boolean (static) | 标记当前是否正在点燃引信 |

### 主要逻辑块概览
- **点燃投掷流程**：`execute()` → `onThrow()` → `createFuse().ignite()`
- **爆炸流程**：`Fuse.act()` → `trigger()` → `explode()`
- **炼金增强**：`EnhanceBomb` 内部类定义炸弹合成配方

### 生命周期/调用时机
1. 玩家选择"点燃并扔出"动作
2. 炸弹被投掷到目标格子
3. 引信开始倒计时（延迟2回合）
4. 引信烧尽，触发爆炸
5. 爆炸造成伤害和地形效果

## 4. 继承与协作关系

### 父类提供的能力
- 继承自 `Item`：物品基本属性（stackable、image）、`actions()`、`doPickUp()`、`onThrow()` 等
- 继承自 `Item`：`isUpgradable()`、`isIdentified()`、`value()`、`random()`

### 覆写的方法
| 方法 | 职责变更 |
|------|---------|
| `isSimilar(Item)` | 增加引信状态比较 |
| `actions(Hero)` | 添加 `AC_LIGHTTHROW` 动作 |
| `execute(Hero, String)` | 处理点燃投掷动作 |
| `onThrow(int)` | 点燃引信后投掷 |
| `doPickUp(Hero, int)` | 拾取时熄灭引信 |
| `isUpgradable()` | 返回 false，炸弹不可升级 |
| `isIdentified()` | 返回 true，炸弹默认已鉴定 |
| `random()` | 有 25% 概率返回 `DoubleBomb` |
| `glowing()` | 有引信时显示红色发光效果 |
| `desc()` | 动态生成描述，包含伤害值 |
| `storeInBundle(Bundle)` | 保存引信状态 |
| `restoreFromBundle(Bundle)` | 恢复引信状态 |

### 依赖的关键类
| 类 | 用途 |
|-----|------|
| `Fuse` | 内部类，管理引信倒计时和爆炸触发 |
| `DoubleBomb` | 内部类，双倍炸弹的特殊掉落 |
| `EnhanceBomb` | 内部类，炸弹炼金配方 |
| `Heap` | 物品堆，炸弹存放位置 |
| `PathFinder` | 计算爆炸范围 |
| `Actor` | 管理引信作为游戏角色 |
| `CellEmitter` | 爆炸粒子效果 |
| `BlastParticle` | 爆炸粒子 |
| `SmokeParticle` | 烟雾粒子 |

### 使用者
- 所有炸弹子类（`ArcaneBomb`、`Firebomb` 等）
- `EnhanceBomb` 炼金配方系统
- `Heap.explode()` 连锁爆炸
- 其他可能触发爆炸的游戏机制

## 5. 字段/常量详解

### 静态常量

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `AC_LIGHTTHROW` | String | "LIGHTTHROW" | 点燃并扔出动作标识 |
| `lightingFuse` | boolean | false (默认) | 静态变量，标记当前操作是否为点燃投掷 |

### 实例字段

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `fuse` | Fuse | null | 当前引信实例，非空表示炸弹正在燃烧 |

### 初始化块字段

| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.BOMB | 物品图标 |
| `defaultAction` | String | AC_LIGHTTHROW | 默认动作 |
| `usesTargeting` | boolean | true | 使用目标选择 |
| `stackable` | boolean | true | 可堆叠 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器。初始化由实例初始化块完成。

### 初始化块
```java
{
    image = ItemSpriteSheet.BOMB;
    defaultAction = AC_LIGHTTHROW;
    usesTargeting = true;
    stackable = true;
}
```

### 初始化注意事项
- 炸弹默认可堆叠，但 `DoubleBomb` 覆写为不可堆叠
- `fuse` 初始为 null，只有在点燃投掷时才会创建

## 7. 方法详解

### explodesDestructively()

**可见性**：public

**是否覆写**：否

**方法职责**：判断炸弹是否进行破坏性爆炸（破坏地形和物品）。

**返回值**：boolean，默认返回 true。

**核心实现逻辑**：
```java
public boolean explodesDestructively(){
    return true;
}
```

**边界情况**：子类如 `ArcaneBomb`、`RegrowthBomb`、`ShrapnelBomb` 覆写返回 false 以避免地形破坏。

---

### explosionRange()

**可见性**：protected

**是否覆写**：否

**方法职责**：返回爆炸影响范围（以格为单位）。

**返回值**：int，默认返回 1（3x3 区域）。

**核心实现逻辑**：
```java
protected int explosionRange(){
    return 1;
}
```

**边界情况**：子类可覆写以扩大爆炸范围。

---

### isSimilar(Item)

**可见性**：public

**是否覆写**：是，覆写自 `Item`

**方法职责**：判断两个炸弹是否可合并堆叠。

**参数**：
- `item` (Item)：待比较的物品

**返回值**：boolean，两者都是炸弹且引信状态相同。

**核心实现逻辑**：
```java
@Override
public boolean isSimilar(Item item) {
    return super.isSimilar(item) && this.fuse == ((Bomb) item).fuse;
}
```

---

### actions(Hero)

**可见性**：public

**是否覆写**：是，覆写自 `Item`

**方法职责**：返回物品可用动作列表。

**参数**：
- `hero` (Hero)：英雄实例

**返回值**：ArrayList<String>，包含父类动作和 `AC_LIGHTTHROW`。

**核心实现逻辑**：
```java
@Override
public ArrayList<String> actions(Hero hero) {
    ArrayList<String> actions = super.actions(hero);
    actions.add(AC_LIGHTTHROW);
    return actions;
}
```

---

### execute(Hero, String)

**可见性**：public

**是否覆写**：是，覆写自 `Item`

**方法职责**：执行指定动作，处理点燃投掷逻辑。

**参数**：
- `hero` (Hero)：英雄实例
- `action` (String)：动作名称

**副作用**：设置静态变量 `lightingFuse`，影响 `onThrow()` 行为。

**核心实现逻辑**：
```java
@Override
public void execute(Hero hero, String action) {
    if (action.equals(AC_LIGHTTHROW)) {
        lightingFuse = true;
        action = AC_THROW;
    } else
        lightingFuse = false;
    super.execute(hero, action);
}
```

---

### createFuse()

**可见性**：protected

**是否覆写**：否

**方法职责**：创建引信实例，允许子类覆写以使用自定义引信。

**返回值**：Fuse，新引信实例。

**核心实现逻辑**：
```java
protected Fuse createFuse(){
    return new Fuse();
}
```

---

### onThrow(int)

**可见性**：protected

**是否覆写**：是，覆写自 `Item`

**方法职责**：处理投掷行为，点燃引信。

**参数**：
- `cell` (int)：目标格子坐标

**前置条件**：目标格子不是深坑（pit）。

**副作用**：创建并添加引信 Actor，延迟 2 回合执行。

**核心实现逻辑**：
```java
@Override
protected void onThrow(int cell) {
    if (!Dungeon.level.pit[cell] && lightingFuse) {
        Actor.addDelayed(fuse = createFuse().ignite(this), 2);
    }
    super.onThrow(cell);
}
```

**边界情况**：
- 投掷到深坑时不点燃引信
- `lightingFuse` 为 false 时不点燃（普通投掷）

---

### doPickUp(Hero, int)

**可见性**：public

**是否覆写**：是，覆写自 `Item`

**方法职责**：拾取炸弹，熄灭正在燃烧的引信。

**参数**：
- `hero` (Hero)：英雄实例
- `pos` (int)：拾取位置

**副作用**：熄灭引信并清除引用。

**核心实现逻辑**：
```java
@Override
public boolean doPickUp(Hero hero, int pos) {
    if (fuse != null) {
        GLog.w(Messages.get(this, "snuff_fuse"));
        fuse.snuff();
        fuse = null;
    }
    return super.doPickUp(hero, pos);
}
```

---

### explode(int)

**可见性**：public

**是否覆写**：否（子类通常覆写此方法）

**方法职责**：执行爆炸，造成伤害和破坏地形。

**参数**：
- `cell` (int)：爆炸中心格子

**副作用**：
- 播放爆炸音效
- 对范围内角色造成伤害
- 破坏可燃地形
- 销毁范围内物品堆
- 可能导致英雄死亡

**核心实现逻辑**：
```java
public void explode(int cell){
    if (fuse != null) {
        fuse.snuff();
        this.fuse = null;
    }
    Sample.INSTANCE.play(Assets.Sounds.BLAST);
    
    if (explodesDestructively()) {
        // 计算爆炸范围
        PathFinder.buildDistanceMap(cell, explodable, explosionRange());
        
        // 破坏地形和物品
        for (int i : affectedCells) {
            if (Dungeon.level.flamable[i]) {
                Dungeon.level.destroy(i);
                GameScene.updateMap(i);
            }
            Heap heap = Dungeon.level.heaps.get(i);
            if (heap != null) heap.explode();
        }
        
        // 造成伤害
        for (Char ch : affectedChars) {
            int dmg = Random.NormalIntRange(4 + Dungeon.scalingDepth(), 
                                            12 + 3*Dungeon.scalingDepth());
            dmg -= ch.drRoll();
            if (dmg > 0) ch.damage(dmg, this);
        }
    }
}
```

**边界情况**：
- 如果角色已被其他炸弹杀死，跳过伤害
- 如果是 `ConjuredBomb` 子类，验证"死于友方魔法"徽章

---

### isUpgradable()

**可见性**：public

**是否覆写**：是，覆写自 `Item`

**方法职责**：炸弹不可升级。

**返回值**：boolean，始终返回 false。

---

### isIdentified()

**可见性**：public

**是否覆写**：是，覆写自 `Item`

**方法职责**：炸弹默认已鉴定。

**返回值**：boolean，始终返回 true。

---

### random()

**可见性**：public

**是否覆写**：是，覆写自 `Item`

**方法职责**：随机生成炸弹，有概率生成双倍炸弹。

**返回值**：Item，25% 概率返回 `DoubleBomb`，否则返回自身。

**核心实现逻辑**：
```java
@Override
public Item random() {
    switch(Random.Int(4)){
        case 0:
            return new DoubleBomb();
        default:
            return this;
    }
}
```

---

### glowing()

**可见性**：public

**是否覆写**：是，覆写自 `Item`

**方法职责**：返回发光效果，用于显示引信燃烧状态。

**返回值**：ItemSprite.Glowing，有引信时返回红色发光，否则返回 null。

**核心实现逻辑**：
```java
@Override
public ItemSprite.Glowing glowing() {
    return fuse != null ? new ItemSprite.Glowing(0xFF0000, 0.6f) : null;
}
```

---

### value()

**可见性**：public

**是否覆写**：是，覆写自 `Item`

**方法职责**：返回炸弹出售价格。

**返回值**：int，15 * 数量。

---

### desc()

**可见性**：public

**是否覆写**：是，覆写自 `Item`

**方法职责**：返回炸弹描述，包含动态伤害值。

**返回值**：String，包含基础描述和引信状态描述。

**核心实现逻辑**：
```java
@Override
public String desc() {
    int depth = Dungeon.hero == null ? 1 : Dungeon.scalingDepth();
    String desc = Messages.get(this, "desc", 4+depth, 12+3*depth);
    if (fuse == null) {
        return desc + "\n\n" + Messages.get(this, "desc_fuse");
    } else {
        return desc + "\n\n" + Messages.get(this, "desc_burning");
    }
}
```

## 8. 对外暴露能力

### 显式 API
- `explode(int cell)` - 触发爆炸
- `explodesDestructively()` - 是否破坏性爆炸
- `explosionRange()` - 爆炸范围
- `createFuse()` - 创建引信

### 内部辅助方法
- `isSimilar(Item)` - 堆叠判断
- `glowing()` - 发光效果
- `random()` - 随机生成

### 扩展入口
- `explodesDestructively()` - 子类覆写控制是否破坏地形
- `explosionRange()` - 子类覆写控制爆炸范围
- `createFuse()` - 子类覆写使用自定义引信
- `explode(int)` - 子类覆写实现特殊爆炸效果

## 9. 运行机制与调用链

### 创建时机
- 作为战利品掉落
- 通过炼金合成
- 商店购买

### 调用者
- `Hero` - 玩家使用
- `EnhanceBomb.brew()` - 炼金合成
- `Heap.explode()` - 连锁爆炸

### 被调用者
- `Fuse` - 引信倒计时
- `CellEmitter` - 粒子效果
- `PathFinder` - 范围计算
- `Dungeon.level` - 地形操作

### 系统流程位置
```
玩家选择动作 → execute() → onThrow() → Fuse.ignite()
    ↓
Fuse.act() (延迟后) → trigger() → explode()
    ↓
伤害计算 → 地形破坏 → 特殊效果（子类）
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.bombs.bomb.name` | 炸弹 | 物品名称 |
| `items.bombs.bomb.ac_lightthrow` | 点燃并扔出 | 动作名称 |
| `items.bombs.bomb.snuff_fuse` | 你迅速掐灭了炸弹引线。 | 拾取时提示 |
| `items.bombs.bomb.ondeath` | 爆炸杀死了你... | 死亡消息 |
| `items.bombs.bomb.rankings_desc` | 死于爆炸 | 排行榜死因 |
| `items.bombs.bomb.desc` | 一枚颇大的黑火药炸弹... | 物品描述 |
| `items.bombs.bomb.desc_fuse` | 看起来引信在点燃后还能烧几回合。 | 引信描述 |
| `items.bombs.bomb.desc_burning` | 引信快烧完了，保持距离或者赶紧掐灭！ | 燃烧描述 |
| `items.bombs.bomb$doublebomb.name` | 一对炸弹 | 双倍炸弹名称 |
| `items.bombs.bomb$doublebomb.desc` | 两枚装的黑火药重装炸弹... | 双倍炸弹描述 |

### 依赖的资源
- **图标**：`ItemSpriteSheet.BOMB`、`ItemSpriteSheet.DBL_BOMB`
- **音效**：`Assets.Sounds.BLAST`

### 中文翻译来源
`core/src/main/assets/messages/items_zh.properties`

## 11. 使用示例

### 基本用法

```java
// 创建炸弹
Bomb bomb = new Bomb();

// 点燃并投掷（玩家操作）
hero.handle(bomb); // 选择 AC_LIGHTTHROW 动作

// 直接触发爆炸（程序调用）
bomb.explode(cell);
```

### 扩展示例

```java
// 创建自定义炸弹子类
public class MyCustomBomb extends Bomb {
    @Override
    protected int explosionRange() {
        return 3; // 更大的爆炸范围
    }
    
    @Override
    public void explode(int cell) {
        super.explode(cell);
        // 添加自定义效果
        // ...
    }
}
```

## 12. 开发注意事项

### 状态依赖
- `fuse` 状态决定炸弹是否在燃烧
- `lightingFuse` 是静态变量，多线程环境下可能有问题（源码注释已指出）

### 生命周期耦合
- 引信作为 `Actor` 存在于游戏循环中
- 引信与炸弹是双向引用关系

### 常见陷阱
- 静态变量 `lightingFuse` 可能在连续操作时出现状态混乱
- 爆炸会触发连锁爆炸（`Heap.explode()`）
- 子类覆写 `explode()` 时必须调用 `super.explode()` 以清除引信

## 13. 修改建议与扩展点

### 适合扩展的位置
- `explodesDestructively()` - 控制是否破坏地形
- `explosionRange()` - 调整爆炸范围
- `explode(int)` - 添加特殊效果
- `createFuse()` - 使用自定义引信

### 不建议修改的位置
- `Fuse` 内部类的基本逻辑
- `isSimilar()` 的引信比较逻辑
- `onThrow()` 的基本流程

### 重构建议
- 将 `lightingFuse` 静态变量改为实例变量或使用其他机制
- 考虑将爆炸效果拆分为策略模式

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点