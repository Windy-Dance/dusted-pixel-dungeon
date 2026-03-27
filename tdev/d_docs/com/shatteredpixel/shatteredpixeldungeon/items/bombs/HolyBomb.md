# HolyBomb 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/bombs/HolyBomb.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.bombs |
| **文件类型** | class |
| **继承关系** | extends Bomb |
| **代码行数** | 91 行 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
神圣炸弹，爆炸时释放圣光，对亡灵和恶魔敌人造成 50% 额外伤害。

### 系统定位
作为 `Bomb` 的子类，提供对抗不死/恶魔敌人的特殊效果。需要 `ScrollOfRemoveCurse`（解咒卷轴）作为炼金材料。

### 不负责什么
- 不负责对普通敌人的额外伤害
- 不负责治疗或增益效果

## 3. 结构总览

### 主要成员概览
无新增实例字段，继承 `Bomb` 所有字段。

### 主要逻辑块概览
- **爆炸效果**：5x5 范围伤害 + 圣光视觉效果
- **额外伤害**：对 UNDEAD/DEMONIC 属性敌人造成 50% 额外伤害

### 生命周期/调用时机
与父类 `Bomb` 相同，爆炸时触发圣光效果。

## 4. 继承与协作关系

### 父类提供的能力
继承 `Bomb` 所有能力。

### 覆写的方法
| 方法 | 职责变更 |
|------|---------|
| `explosionRange()` | 返回 2，5x5 爆炸范围 |
| `explode(int)` | 添加圣光视觉效果和对亡灵/恶魔的额外伤害 |
| `value()` | 返回 50 |

### 依赖的关键类
| 类 | 用途 |
|-----|------|
| `Flare` | 圣光视觉效果 |
| `ShadowParticle` | 对亡灵/恶魔伤害时的暗影粒子 |
| `HolyDamage` | 内部类，标记圣光伤害来源 |
| `Char.Property` | 角色属性（UNDEAD、DEMONIC） |
| `ScrollOfRemoveCurse` | 炼金材料 |

### 使用者
- `EnhanceBomb` 炼金配方系统
- 玩家使用

## 5. 字段/常量详解

### 实例字段

| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.HOLY_BOMB | 神圣炸弹图标 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.HOLY_BOMB;
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

**方法职责**：执行爆炸并对亡灵/恶魔敌人造成额外圣光伤害。

**参数**：
- `cell` (int)：爆炸中心格子

**副作用**：
- 调用 `super.explode()` 执行标准爆炸
- 显示圣光 Flare 视觉效果
- 对 UNDEAD 或 DEMONIC 属性敌人造成 50% 额外伤害
- 显示暗影粒子效果
- 播放阅读音效

**核心实现逻辑**：
```java
@Override
public void explode(int cell) {
    super.explode(cell);
    
    if (Dungeon.level.heroFOV[cell]) {
        new Flare(10, 64).show(Dungeon.hero.sprite.parent, 
                               DungeonTilemap.tileCenterToWorld(cell), 2f);
    }
    
    ArrayList<Char> affected = new ArrayList<>();
    
    PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), explosionRange());
    for (int i = 0; i < PathFinder.distance.length; i++) {
        if (PathFinder.distance[i] < Integer.MAX_VALUE) {
            Char ch = Actor.findChar(i);
            if (ch != null) {
                affected.add(ch);
            }
        }
    }
    
    for (Char ch : affected) {
        if (ch.properties().contains(Char.Property.UNDEAD) || 
            ch.properties().contains(Char.Property.DEMONIC)) {
            ch.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
            
            // 50% 额外伤害
            int damage = Math.round(Random.NormalIntRange(Dungeon.scalingDepth()+4, 
                                                          12 + 3*Dungeon.scalingDepth()) * 0.5f);
            ch.damage(damage, new HolyDamage());
        }
    }
    
    Sample.INSTANCE.play(Assets.Sounds.READ);
}
```

**边界情况**：
- 只有具有 UNDEAD 或 DEMONIC 属性的敌人才受额外伤害
- 额外伤害使用 `HolyDamage` 作为伤害来源

---

### value()

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：返回神圣炸弹的出售价格。

**返回值**：int，返回 `quantity * 50`（炸弹 20 + 解咒卷轴 30）。

## 8. 对外暴露能力

### 显式 API
继承 `Bomb` 所有公开 API。

### 内部辅助方法
无。

### 扩展入口
- `explode(int)` 可覆写修改额外伤害比例或目标属性判断

## 9. 运行机制与调用链

### 创建时机
通过炼金合成：`Bomb` + `ScrollOfRemoveCurse` → `HolyBomb`（炼金费用 3）

### 调用者
- 炼金系统
- 玩家使用

### 系统流程位置
```
炼金合成 → 获得 HolyBomb
    ↓
点燃投掷 → Fuse 倒计时
    ↓
爆炸 → super.explode() + 圣光效果 + 额外伤害
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.bombs.holybomb.name` | 神圣炸弹 | 物品名称 |
| `items.bombs.holybomb.desc` | 这枚改造过的炸弹会在爆炸时在大范围内闪耀出圣光... | 物品描述 |
| `items.bombs.holybomb.discover_hint` | 你可通过炼金合成该物品。 | 发现提示 |

### 依赖的资源
- **图标**：`ItemSpriteSheet.HOLY_BOMB`
- **音效**：`Assets.Sounds.READ`

### 中文翻译来源
`core/src/main/assets/messages/items_zh.properties`

## 11. 使用示例

### 基本用法

```java
// 炼金合成
Bomb bomb = new Bomb();
ScrollOfRemoveCurse scroll = new ScrollOfRemoveCurse();
// 合成得到 HolyBomb

// 使用
HolyBomb holyBomb = new HolyBomb();
hero.handle(holyBomb);

// 特别适合对付亡灵和恶魔敌人
// 如：Skeleton、Wraith、Demon 类敌人
```

## 12. 开发注意事项

### 状态依赖
- 额外伤害只对 UNDEAD 或 DEMONIC 属性生效
- 圣光视觉效果只在玩家视野内显示

### 生命周期耦合
- `HolyDamage` 内部类用于标记伤害来源

### 常见陷阱
- 普通敌人不会受到额外伤害
- 圣光效果对英雄无效

## 13. 修改建议与扩展点

### 适合扩展的位置
- `explode(int)` 可修改额外伤害比例
- 可添加对其他敌人属性的支持

### 不建议修改的位置
- `HolyDamage` 内部类定义

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