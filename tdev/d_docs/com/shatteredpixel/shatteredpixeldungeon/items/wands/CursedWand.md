# CursedWand 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/wands/CursedWand.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.wands |
| **文件类型** | class |
| **继承关系** | 无继承（工具类） |
| **代码行数** | 1288 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
CursedWand 是诅咒法杖效果的辅助工具类，负责：
- 管理诅咒法杖施法时的随机效果
- 提供多种诅咒效果类型（普通/稀有/极稀有）
- 处理奇妙树脂（WondrousResin）带来的正面效果转化
- 实现各种诅咒效果的视觉和逻辑

### 系统定位
作为法杖系统的诅咒效果处理器，被 Wand 类在处理诅咒法杖施法时调用。不继承任何类，纯工具类设计。

### 不负责什么
- 不处理法杖的诅咒状态判定（由 Wand 处理）
- 不处理法杖的充能消耗（由 Wand.wandUsed() 处理）

## 3. 结构总览

### 主要成员概览
- **核心方法**：`cursedZap()`, `tryForWandProc()`, `randomEffect()`, `randomValidEffect()`
- **效果分类**：`COMMON_EFFECTS`, `UNCOMMON_EFFECTS`, `RARE_EFFECTS`, `VERY_RARE_EFFECTS`
- **效果基类**：`CursedEffect` 抽象类
- **具体效果类**：约 32 个具体效果实现类

### 主要逻辑块概览
1. **效果选择**：根据概率分布选择效果类别
2. **效果执行**：`FX()` → `effect()` 流程
3. **正面转化**：检查 `WondrousResin` 并调整效果

### 生命周期/调用时机
- 当诅咒法杖施法时由 `Wand.zapper.onSelect()` 调用
- 效果执行完成后触发回调继续法杖使用流程

## 4. 继承与协作关系

### 父类提供的能力
无继承。

### 覆写的方法
不适用。

### 实现的接口契约
无显式接口实现。

### 依赖的关键类
| 类 | 用途 |
|----|------|
| `Wand` | 法杖基类，调用诅咒效果 |
| `Ballistica` | 投射路径 |
| `WondrousResin` | 奇妙树脂饰品，影响效果类型 |
| `MagicMissile` | 视觉效果 |
| `Buff` 系列 | 各种状态效果 |
| `Blob` 系列 | 各种区域效果 |
| `Generator` | 随机物品生成 |

### 使用者
- `Wand.zapper` - 诅咒法杖施法时调用

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `EFFECT_CAT_CHANCES` | float[] | {60, 30, 9, 1} | 效果类别概率分布（普通60%/罕见30%/稀有9%/极稀有1%） |
| `COMMON_EFFECTS` | ArrayList&lt;CursedEffect&gt; | 8种效果 | 普通效果列表 |
| `UNCOMMON_EFFECTS` | ArrayList&lt;CursedEffect&gt; | 8种效果 | 获得效果列表 |
| `RARE_EFFECTS` | ArrayList&lt;CursedEffect&gt; | 8种效果 | 稀有效果列表 |
| `VERY_RARE_EFFECTS` | ArrayList&lt;CursedEffect&gt; | 8种效果 | 极稀有效果列表 |

### 实例字段
无（所有方法均为静态）

## 6. 构造与初始化机制

### 构造器
无显式构造器（工具类不需要实例化）。

### 初始化块
静态初始化块填充效果列表：
```java
static {
    COMMON_EFFECTS.add(new BurnAndFreeze());
    COMMON_EFFECTS.add(new SpawnRegrowth());
    // ... 更多效果
}
```

### 初始化注意事项
- 所有效果类在类加载时实例化并加入对应列表
- 效果分类基于稀有度和影响程度

## 7. 方法详解

### cursedZap(Item origin, Char user, Ballistica bolt, Callback afterZap)
**可见性**：public static

**是否覆写**：否

**方法职责**：执行诅咒法杖施法效果

**参数**：
- `origin` (Item)：法杖来源物品
- `user` (Char)：施法者
- `bolt` (Ballistica)：投射路径
- `afterZap` (Callback)：效果完成后的回调

**核心实现逻辑**：
```java
public static void cursedZap(final Item origin, final Char user, final Ballistica bolt, final Callback afterZap) {
    // 检查奇妙树脂是否触发正面效果
    boolean positiveOnly = user == Dungeon.hero && Random.Float() < WondrousResin.positiveCurseEffectChance();
    
    // 选择有效的效果
    CursedEffect effect = randomValidEffect(origin, user, bolt, positiveOnly);

    // 执行视觉效果后执行效果
    effect.FX(origin, user, bolt, new Callback() {
        @Override
        public void call() {
            effect.effect(origin, user, bolt, positiveOnly);
            afterZap.call();
        }
    });
}
```

---

### tryForWandProc(Char target, Item origin)
**可见性**：public static

**是否覆写**：否

**方法职责**：尝试触发法杖命中效果

**参数**：
- `target` (Char)：目标角色
- `origin` (Item)：来源物品

**核心实现逻辑**：
```java
public static void tryForWandProc(Char target, Item origin) {
    if (target != null && target != Dungeon.hero && origin instanceof Wand) {
        Wand.wandProc(target, origin.buffedLvl(), 1);
    }
}
```

---

### randomEffect()
**可见性**：public static

**是否覆写**：否

**方法职责**：随机选择一个诅咒效果（不考虑有效性）

**返回值**：CursedEffect，随机效果

**核心实现逻辑**：
```java
public static CursedEffect randomEffect() {
    switch (Random.chances(EFFECT_CAT_CHANCES)) {
        case 0: default: return randomCommonEffect();
        case 1: return randomUncommonEffect();
        case 2: return randomRareEffect();
        case 3: return randomVeryRareEffect();
    }
}
```

---

### randomValidEffect(Item origin, Char user, Ballistica bolt, boolean positiveOnly)
**可见性**：public static

**是否覆写**：否

**方法职责**：随机选择一个有效的诅咒效果

**参数**：
- `origin` (Item)：来源物品
- `user` (Char)：施法者
- `bolt` (Ballistica)：投射路径
- `positiveOnly` (boolean)：是否仅正面效果

**返回值**：CursedEffect，有效的随机效果

**核心实现逻辑**：
```java
public static CursedEffect randomValidEffect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
    switch (Random.chances(EFFECT_CAT_CHANCES)) {
        case 0: default: return randomValidCommonEffect(origin, user, bolt, positiveOnly);
        case 1: return randomValidUncommonEffect(origin, user, bolt, positiveOnly);
        case 2: return randomValidRareEffect(origin, user, bolt, positiveOnly);
        case 3: return randomValidVeryRareEffect(origin, user, bolt, positiveOnly);
    }
}
```

---

## CursedEffect 抽象类

### 类定义
```java
public static abstract class CursedEffect {
    public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
        return true;
    }
    
    public void FX(final Item origin, final Char user, final Ballistica bolt, final Callback callback) {
        MagicMissile.boltFromChar(user.sprite.parent, MagicMissile.RAINBOW, user.sprite, bolt.collisionPos, callback);
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
    }
    
    public abstract boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly);
}
```

### 方法说明
| 方法 | 职责 |
|------|------|
| `valid()` | 检查效果是否在当前条件下有效 |
| `FX()` | 播放视觉效果 |
| `effect()` | 执行实际效果逻辑 |

---

## 具体效果类概览

### 普通效果 (COMMON_EFFECTS) - 60%概率
| 效果类 | 中文描述 | 效果说明 |
|--------|---------|---------|
| `BurnAndFreeze` | 燃烧与冻结 | 随机燃烧或冻结目标和施法者 |
| `SpawnRegrowth` | 再生生长 | 在目标位置生成再生植物 |
| `RandomTeleport` | 随机传送 | 传送目标或施法者 |
| `RandomGas` | 随机气体 | 释放混乱/毒素/麻痹气体 |
| `RandomAreaEffect` | 随机区域效果 | 触发燃烧/冰霜/电击陷阱 |
| `Bubbles` | 气泡 | 播放气泡特效 |
| `RandomWand` | 随机法杖 | 随机使用另一根法杖的效果 |
| `SelfOoze` | 自身淤泥 | 对周围施加淤泥效果 |

### 获得效果 (UNCOMMON_EFFECTS) - 30%概率
| 效果类 | 中文描述 | 效果说明 |
|--------|---------|---------|
| `RandomPlant` | 随机植物 | 在目标位置生成随机植物 |
| `HealthTransfer` | 生命转移 | 转移生命值（可能正向或反向） |
| `Explosion` | 爆炸 | 在目标位置引发爆炸 |
| `LightningBolt` | 闪电链 | 释放闪电链效果 |
| `Geyser` | 间歇泉 | 触发间歇泉陷阱 |
| `SummonSheep` | 召唤羊群 | 召唤羊群（使用群聚陷阱） |
| `Levitate` | 漂浮 | 施加漂浮效果 |
| `Alarm` | 警报 | 惊动所有怪物 |

### 稀有效果 (RARE_EFFECTS) - 9%概率
| 效果类 | 中文描述 | 效果说明 |
|--------|---------|---------|
| `SheepPolymorph` | 羊群变形 | 将目标变形为羊 |
| `CurseEquipment` | 诅咒装备 | 诅咒目标装备或施加减益 |
| `InterFloorTeleport` | 跨层传送 | 传送到其他楼层 |
| `SummonMonsters` | 召唤怪物 | 召唤敌人或镜像 |
| `FireBall` | 火球 | 范围火焰伤害 |
| `ConeOfColors` | 彩色锥形 | 随机元素锥形伤害 |
| `MassInvuln` | 群体无敌 | 所有角色获得无敌和祝福 |
| `Petrify` | 石化 | 施法者进入时间静止状态 |

### 极稀有效果 (VERY_RARE_EFFECTS) - 1%概率
| 效果类 | 中文描述 | 效果说明 |
|--------|---------|---------|
| `ForestFire` | 森林大火 | 全图再生后引发火灾 |
| `SpawnGoldenMimic` | 生成黄金宝箱怪 | 召唤黄金宝箱怪 |
| `AbortRetryFail` | 异常终止 | 模拟游戏崩溃（彩蛋） |
| `RandomTransmogrify` | 随机变形 | 将法杖变形为其他物品 |
| `HeroShapeShift` | 英雄变形 | 改变英雄外观 |
| `SuperNova` | 超新星 | 延迟的巨大爆炸 |
| `SinkHole` | 地陷 | 制造地陷陷阱 |
| `GravityChaos` | 重力混乱 | 随机方向重力效果 |

---

## 典型效果类详解

### BurnAndFreeze（燃烧与冻结）
```java
public static class BurnAndFreeze extends CursedEffect {
    @Override
    public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
        Char target = Actor.findChar(bolt.collisionPos);
        if (Random.Int(2) == 0) {
            if (target != null) Buff.affect(target, Burning.class).reignite(target);
            if (!positiveOnly) Buff.affect(user, Frost.class, Frost.DURATION);
        } else {
            if (!positiveOnly) Buff.affect(user, Burning.class).reignite(user);
            if (target != null) Buff.affect(target, Frost.class, Frost.DURATION);
        }
        tryForWandProc(target, origin);
        return true;
    }
}
```

### HealthTransfer（生命转移）
```java
public static class HealthTransfer extends CursedEffect {
    @Override
    public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
        final Char target = Actor.findChar(bolt.collisionPos);
        if (target != null) {
            int damage = Dungeon.scalingDepth() * 2;
            Char toHeal, toDamage;
            
            // 正面效果时只伤害目标
            if (positiveOnly || Random.Int(2) == 0) {
                toHeal = user;
                toDamage = target;
            } else {
                toHeal = target;
                toDamage = user;
            }
            
            toHeal.HP = Math.min(toHeal.HT, toHeal.HP + damage/2);
            toDamage.damage(damage, new CursedWand());
            // ... 处理死亡等情况
        }
        return true;
    }
}
```

### RandomTransmogrify（随机变形）
```java
public static class RandomTransmogrify extends CursedEffect {
    @Override
    public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
        // 正面效果时触发变形选择窗口
        if (positiveOnly) {
            GameScene.show(new ScrollOfMetamorphosis.WndMetamorphChoose());
            return true;
        }
        
        // 否则将法杖变形为随机物品
        origin.detach(Dungeon.hero.belongings.backpack);
        Item result;
        do {
            result = Generator.randomUsingDefaults(Random.oneOf(
                Generator.Category.WEAPON, 
                Generator.Category.ARMOR,
                Generator.Category.RING, 
                Generator.Category.ARTIFACT));
        } while (result.cursed);
        
        if (result.isUpgradable()) result.upgrade();
        result.cursed = result.cursedKnown = true;
        Dungeon.level.drop(result, user.pos).sprite.drop();
        return true;
    }
}
```

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `cursedZap(Item, Char, Ballistica, Callback)` | 执行诅咒法杖施法 |
| `tryForWandProc(Char, Item)` | 尝试触发法杖命中效果 |
| `randomEffect()` | 随机选择效果 |
| `randomValidEffect(Item, Char, Ballistica, boolean)` | 随机选择有效效果 |

### 内部辅助方法
| 方法 | 用途 |
|------|------|
| `randomCommonEffect()` | 随机普通效果 |
| `randomUncommonEffect()` | 随机获得效果 |
| `randomRareEffect()` | 随机稀有效果 |
| `randomVeryRareEffect()` | 随机极稀有效果 |
| `randomValidCommonEffect()` 等 | 随机有效效果（带验证） |

### 扩展入口
- 继承 `CursedEffect` 并实现 `effect()` 方法可添加新的诅咒效果
- 将新效果添加到对应的效果列表中

## 9. 运行机制与调用链

### 创建时机
类加载时静态初始化，不需要实例化。

### 调用者
- `Wand.zapper.onSelect()` - 诅咒法杖施法时

### 被调用者
- `Wand.wandProc()` - 法杖命中效果
- `WondrousResin` - 正面效果概率检查
- `MagicMissile` - 视觉效果
- 各种 `Buff` 和 `Blob` 类

### 系统流程位置
```
Wand 施法流程
├── tryToZap() - 检查可施法
├── fx() - 视觉效果
│   └── 如果诅咒: CursedWand.cursedZap()
│       ├── randomValidEffect() - 选择效果
│       ├── effect.FX() - 播放视觉
│       └── effect.effect() - 执行效果
└── wandUsed() - 完成处理
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.wands.cursedwand.ondeath` | 你死于自己的%s。 | 死亡消息 |
| `items.wands.cursedwand.nothing` | 什么事都没发生。 | 无效果提示 |
| `items.wands.cursedwand.mass_invuln` | 明耀的光芒从你的法杖迸射而出！ | 群体无敌效果 |
| `items.wands.cursedwand.petrify` | 你突然被定在了原地！ | 石化效果 |
| `items.wands.cursedwand.grass` | 草木在你周围疯长而出！ | 再生效果 |
| `items.wands.cursedwand.fire` | 你闻到了烧烤的味道... | 火灾警告 |
| `items.wands.cursedwand.transmogrify_wand` | 你的法杖变化成了另一样东西！ | 法杖变形 |
| `items.wands.cursedwand.transmogrify_other` | 你的道具变化成了另一样东西！ | 道具变形 |
| `items.wands.cursedwand.disguise` | 你的外貌在你眼前发生了变化！ | 外观变化 |
| `items.wands.cursedwand.supernova` | 一颗灼热的能量球开始膨胀。情况不妙，走为上计！ | 超新星警告 |
| `items.wands.cursedwand.supernova_positive` | 一颗灼热的能量球开始膨胀，但不知何故它似乎很安全。 | 安全超新星 |
| `items.wands.cursedwand.sinkhole` | 你周围的地板在迅速崩塌！ | 地陷警告 |
| `items.wands.cursedwand.sinkhole_positive` | 地板在迅速崩塌，但你感觉脚下足够结实。 | 安全地陷 |
| `items.wands.cursedwand.gravity` | 重力突然开始向随机方向牵引！ | 重力混乱 |
| `items.wands.cursedwand.gravity_positive` | 重力突然开始向随机方向牵引，但这对你没有影响。 | 安全重力混乱 |
| `items.wands.cursedwand.gravity_end` | 重力混乱结束了。 | 重力恢复 |

### 依赖的资源
- `Assets.Sounds.ZAP` - 施法音效
- `Assets.Sounds.LIGHTNING` - 闪电音效
- `Assets.Sounds.GAS` - 气体音效
- `Assets.Sounds.BLAST` - 爆炸音效
- `MagicMissile.RAINBOW` - 彩虹飞弹视觉

### 中文翻译来源
所有中文翻译来自 `core/src/main/assets/messages/items/items_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 在诅咒法杖施法时调用
CursedWand.cursedZap(wand, hero, bolt, new Callback() {
    @Override
    public void call() {
        wand.wandUsed();  // 完成法杖使用流程
    }
});
```

### 添加新的诅咒效果
```java
// 1. 创建新的效果类
public static class MyCustomEffect extends CursedEffect {
    @Override
    public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
        // 实现自定义效果
        Char target = Actor.findChar(bolt.collisionPos);
        if (target != null) {
            Buff.affect(target, MyCustomBuff.class);
        }
        return true;
    }
}

// 2. 在静态初始化块中添加
static {
    COMMON_EFFECTS.add(new MyCustomEffect());
}
```

## 12. 开发注意事项

### 状态依赖
- 效果执行依赖 `Dungeon.hero` 和 `Dungeon.level` 存在
- `positiveOnly` 参数由 `WondrousResin` 饰品决定

### 生命周期耦合
- 效果必须通过回调机制正确完成
- 回调未执行会导致法杖使用流程中断

### 常见陷阱
1. **忘记调用回调**：导致法杖使用流程卡住
2. **忽略 positiveOnly 参数**：在正面效果模式下伤害队友
3. **效果无效时返回 false**：应确保至少有一个备选效果
4. **在 effect() 中执行耗时操作**：应确保效果立即完成

## 13. 修改建议与扩展点

### 适合扩展的位置
- 新增 `CursedEffect` 子类添加自定义效果
- 修改 `EFFECT_CAT_CHANCES` 调整效果概率分布
- 在 `valid()` 方法中添加新的有效性检查

### 不建议修改的位置
- `cursedZap()` 的核心流程
- 效果分类的概率数组

### 重构建议
- 可考虑将效果类拆分到独立文件
- 可使用注解标记效果类型和概率

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点