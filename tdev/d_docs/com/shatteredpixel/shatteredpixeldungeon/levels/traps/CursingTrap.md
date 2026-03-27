# CursingTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/CursingTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 122 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`CursingTrap`（诅咒陷阱）负责触发后诅咒触发者身上的装备物品，使其带有诅咒效果。

### 系统定位
陷阱系统中的负面效果型陷阱。通过诅咒装备对玩家造成长期的负面影响。

### 不负责什么
- 不直接造成伤害
- 不负责诅咒效果的具体实现（由物品的 curse 属性处理）

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = VIOLET`，`shape = WAVES`
- **activate() 方法**：触发诅咒效果
- **curse(Hero) 方法**：对英雄装备实施诅咒
- **curse(Item) 方法**：诅咒单个物品

### 主要逻辑块概览
1. **位置检测**：检查陷阱位置是否有物品堆或英雄
2. **物品诅咒**：诅咒物品堆中的可升级物品
3. **装备诅咒**：诅咒英雄的武器或护甲
4. **视觉效果**：播放暗影粒子效果

### 生命周期/调用时机
```
创建 → set(pos) → trigger() → activate() → disarm()
```

## 4. 继承与协作关系

### 父类提供的能力
继承自 `Trap`：
- 所有实例字段
- `trigger()`, `disarm()` 等方法

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `activate()` | 实现诅咒效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Hero` | 英雄类 |
| `Heap` | 地面物品堆 |
| `Item` | 物品基类 |
| `Weapon` / `Armor` | 武器和护甲类 |
| `GLog` | 日志输出 |
| `CellEmitter` | 粒子效果 |
| `ShadowParticle` | 暗影粒子 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | VIOLET (5) | 紫色陷阱 |
| `shape` | WAVES (1) | 波浪图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = VIOLET;
    shape = WAVES;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：触发诅咒效果，诅咒陷阱位置的物品和英雄装备。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 诅咒物品堆中的物品
- 诅咒英雄的装备
- 播放视觉效果和音效

**核心实现逻辑**：
```java
@Override
public void activate() {
    if (Dungeon.level.heroFOV[pos]) {
        CellEmitter.get(pos).burst(ShadowParticle.UP, 5);
        Sample.INSTANCE.play(Assets.Sounds.CURSED);
    }

    // 诅咒地面物品堆
    Heap heap = Dungeon.level.heaps.get(pos);
    if (heap != null) {
        for (Item item : heap.items) {
            if (item.isUpgradable() && !(item instanceof MissileWeapon))
                curse(item);
        }
    }

    // 诅咒英雄装备
    if (Dungeon.hero.pos == pos && !Dungeon.hero.flying) {
        curse(Dungeon.hero);
    }
}
```

---

### curse(Hero hero)

**可见性**：public static

**是否覆写**：否

**方法职责**：对英雄身上的装备实施诅咒，优先诅咒无附魔的装备。

**参数**：
- `hero` (Hero)：目标英雄

**返回值**：void

**前置条件**：英雄位于陷阱位置且不在飞行状态

**副作用**：
- 诅咒一件装备
- 显示诅咒日志

**核心实现逻辑**：
```java
public static void curse(Hero hero) {
    // 优先诅咒列表（无附魔的装备）
    ArrayList<Item> priorityCurse = new ArrayList<>();
    // 可诅咒列表（有附魔的装备）
    ArrayList<Item> canCurse = new ArrayList<>();

    // 检查武器
    KindOfWeapon weapon = hero.belongings.weapon();
    if (weapon instanceof Weapon && !(weapon instanceof MagesStaff)) {
        if (((Weapon) weapon).enchantment == null)
            priorityCurse.add(weapon);
        else
            canCurse.add(weapon);
    }

    // 检查护甲
    Armor armor = hero.belongings.armor();
    if (armor != null) {
        if (armor.glyph == null)
            priorityCurse.add(armor);
        else
            canCurse.add(armor);
    }

    // 随机选择诅咒
    Collections.shuffle(priorityCurse);
    Collections.shuffle(canCurse);

    if (!priorityCurse.isEmpty()) {
        curse(priorityCurse.remove(0));
    } else if (!canCurse.isEmpty()) {
        curse(canCurse.remove(0));
    }

    EquipableItem.equipCursed(hero);
    GLog.n(Messages.get(CursingTrap.class, "curse"));
}
```

---

### curse(Item item)

**可见性**：private static

**是否覆写**：否

**方法职责**：诅咒单个物品，设置诅咒状态并添加诅咒附魔/铭文。

**参数**：
- `item` (Item)：目标物品

**返回值**：void

**前置条件**：物品可被诅咒

**副作用**：
- 设置 `cursed` 和 `cursedKnown` 为 true
- 为武器添加诅咒附魔
- 为护甲添加诅咒铭文

**核心实现逻辑**：
```java
private static void curse(Item item) {
    item.cursed = item.cursedKnown = true;

    if (item instanceof Weapon) {
        Weapon w = (Weapon) item;
        if (w.enchantment == null) {
            w.enchant(Weapon.Enchantment.randomCurse());
        }
    }
    if (item instanceof Armor) {
        Armor a = (Armor) item;
        if (a.glyph == null) {
            a.inscribe(Armor.Glyph.randomCurse());
        }
    }
}
```

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发诅咒效果（覆写自 Trap） |
| `curse(Hero)` | 对英雄装备实施诅咒（静态方法） |

### 内部辅助方法
| 方法 | 用途 |
|------|------|
| `curse(Item)` | 诅咒单个物品 |

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `CellEmitter.get().burst()`：播放粒子效果
- `curse(Hero)`：诅咒英雄装备
- `curse(Item)`：诅咒单个物品

### 系统流程位置
```
英雄踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
检查位置是否有物品堆/英雄
    ↓
诅咒符合条件的物品/装备
    ↓
物品变为诅咒状态
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.cursingtrap.name` | 诅咒陷阱 | 陷阱名称 |
| `levels.traps.cursingtrap.curse` | 你身上的装备被诅咒了！ | 诅咒日志 |
| `levels.traps.cursingtrap.desc` | 这个陷阱充满了诅咒的力量。触发它会诅咒你身上的部分装备。 | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.CURSED` | 诅咒音效 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置诅咒陷阱
CursingTrap trap = new CursingTrap();
trap.set(position);
trap.hide();

// 英雄踩中后，身上的武器或护甲会被诅咒
// 优先诅咒无附魔的装备
```

### 静态方法调用
```java
// 可以直接调用静态方法诅咒英雄
CursingTrap.curse(Dungeon.hero);
```

## 12. 开发注意事项

### 状态依赖
- 只影响地面的物品堆和站在陷阱位置的英雄
- 飞行状态的英雄不受影响

### 诅咒优先级
1. 优先诅咒无附魔/铭文的装备
2. 其次诅咒有附魔/铭文的装备
3. 只诅咒一件装备

### 常见陷阱
- 投射武器（MissileWeapon）不会被诅咒
- 法师法杖（MagesStaff）不会被诅咒

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可修改诅咒优先级逻辑
- 可调整诅咒的装备数量

### 不建议修改的位置
- 静态方法 `curse(Hero)` 被其他系统调用，签名变更需谨慎

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`, `curse(Hero)`, `curse(Item)`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已详细说明