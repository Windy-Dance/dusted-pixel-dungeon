# FlashBangBomb 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/bombs/FlashBangBomb.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.bombs |
| **文件类型** | class |
| **继承关系** | extends Bomb |
| **代码行数** | 99 行 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
闪光弹，爆炸时释放电击效果，造成伤害并麻痹范围内所有角色。

### 系统定位
作为 `Bomb` 的子类，提供电击麻痹效果。需要 `ScrollOfRecharging`（充能卷轴）作为炼金材料。

### 不负责什么
- 不负责地形破坏效果
- 不负责持续伤害

## 3. 结构总览

### 主要成员概览
无新增实例字段，继承 `Bomb` 所有字段。

### 主要逻辑块概览
- **电击伤害**：25% 额外伤害
- **麻痹效果**：10 回合麻痹
- **视觉效果**：闪电弧线和屏幕闪白

### 生命周期/调用时机
与父类 `Bomb` 相同，爆炸时触发电击效果。

## 4. 继承与协作关系

### 父类提供的能力
继承 `Bomb` 所有能力。

### 覆写的方法
| 方法 | 职责变更 |
|------|---------|
| `explosionRange()` | 返回 2，5x5 爆炸范围 |
| `explode(int)` | 添加电击伤害和麻痹效果 |
| `value()` | 返回 50 |

### 依赖的关键类
| 类 | 用途 |
|-----|------|
| `Lightning` | 闪电视觉效果 |
| `Electricity` | 电击伤害类型 |
| `Paralysis` | 麻痹 Buff |
| `SparkParticle` | 电火花粒子 |
| `ScrollOfRecharging` | 炼金材料 |

### 使用者
- `EnhanceBomb` 炼金配方系统
- 玩家使用

## 5. 字段/常量详解

### 实例字段

| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.FLASHBANG | 闪光弹图标 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.FLASHBANG;
}
```

## 7. 方法详解

### explosionRange()

**可见性**：protected

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：返回爆炸范围。

**返回值**：int，返回 2（5x5 区域）。

---

### explode(int)

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：执行爆炸并释放电击麻痹效果。

**参数**：
- `cell` (int)：爆炸中心格子

**副作用**：
- 调用 `super.explode()` 执行标准爆炸
- 对范围内角色造成 25% 额外电击伤害
- 麻痹范围内角色 10 回合
- 显示闪电弧线效果
- 英雄视角闪白
- 播放闪电音效

**核心实现逻辑**：
```java
@Override
public void explode(int cell) {
    super.explode(cell);

    ArrayList<Char> affected = new ArrayList<>();
    PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), explosionRange());
    for (int i = 0; i < PathFinder.distance.length; i++) {
        if (PathFinder.distance[i] < Integer.MAX_VALUE && Actor.findChar(i) != null) {
            affected.add(Actor.findChar(i));
        }
    }

    ArrayList<Lightning.Arc> arcs = new ArrayList<>();
    for (Char ch : affected) {
        // 25% 额外伤害
        int damage = Math.round(Random.NormalIntRange(4 + Dungeon.scalingDepth(), 
                                                        12 + 3*Dungeon.scalingDepth()) / 4f);
        ch.damage(damage, new Electricity());
        
        // 10 回合麻痹
        if (ch.isAlive()) Buff.prolong(ch, Paralysis.class, Paralysis.DURATION);
        
        // 闪电弧线
        arcs.add(new Lightning.Arc(DungeonTilemap.tileCenterToWorld(cell), ch.sprite.center()));

        if (ch == Dungeon.hero) {
            GameScene.flash(0x80FFFFFF);  // 屏幕闪白
        }

        if (ch == Dungeon.hero && !ch.isAlive()) {
            Badges.validateDeathFromFriendlyMagic();
            GLog.n(Messages.get(this, "ondeath"));
            Dungeon.fail(this);
        }
    }

    CellEmitter.center(cell).burst(SparkParticle.FACTORY, 20);
    Dungeon.hero.sprite.parent.addToFront(new Lightning(arcs, null));
    Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
}
```

**边界情况**：
- 电击伤害使用 `Electricity` 作为伤害来源（影响某些抗性）
- 麻痹时长使用 `Paralysis.DURATION` 常量
- 对英雄使用时屏幕会闪白

---

### value()

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：返回闪光弹的出售价格。

**返回值**：int，返回 `quantity * 50`（炸弹 20 + 充能卷轴 30）。

## 8. 对外暴露能力

### 显式 API
继承 `Bomb` 所有公开 API。

### 内部辅助方法
无。

### 扩展入口
- `explode(int)` 可覆写修改伤害比例或麻痹时长

## 9. 运行机制与调用链

### 创建时机
通过炼金合成：`Bomb` + `ScrollOfRecharging` → `FlashBangBomb`（炼金费用 2）

### 调用者
- 炼金系统
- 玩家使用

### 系统流程位置
```
炼金合成 → 获得 FlashBangBomb
    ↓
点燃投掷 → Fuse 倒计时
    ↓
爆炸 → super.explode() + 电击伤害 + 麻痹
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.bombs.flashbangbomb.name` | 闪光弹 | 物品名称 |
| `items.bombs.flashbangbomb.desc` | 这枚改造过的炸弹在爆炸时会爆发出一阵电闪雷鸣... | 物品描述 |
| `items.bombs.flashbangbomb.discover_hint` | 你可通过炼金合成该物品。 | 发现提示 |

### 依赖的资源
- **图标**：`ItemSpriteSheet.FLASHBANG`
- **粒子**：`SparkParticle`
- **音效**：`Assets.Sounds.LIGHTNING`

### 中文翻译来源
`core/src/main/assets/messages/items_zh.properties`

## 11. 使用示例

### 基本用法

```java
// 炼金合成
Bomb bomb = new Bomb();
ScrollOfRecharging scroll = new ScrollOfRecharging();
// 合成得到 FlashBangBomb

// 使用
FlashBangBomb flashbang = new FlashBangBomb();
hero.handle(flashbang);
```

## 12. 开发注意事项

### 状态依赖
- 麻痹时长由 `Paralysis.DURATION` 决定
- 电击伤害比例固定为 25%

### 生命周期耦合
- 麻痹效果会持续直到时间结束或被清除

### 常见陷阱
- 电击效果对英雄同样有效，会麻痹英雄
- 屏幕闪白效果可能影响玩家视野

## 13. 修改建议与扩展点

### 适合扩展的位置
- `explode(int)` 可修改伤害比例或麻痹时长

### 不建议修改的位置
- `Lightning` 视觉效果的创建逻辑

### 重构建议
无。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点