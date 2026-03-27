# ArcaneBomb 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/bombs/ArcaneBomb.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.bombs |
| **文件类型** | class |
| **继承关系** | extends Bomb |
| **代码行数** | 148 行 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
奥术炸弹，释放类似粘咕（Goo）蓄力攻击的魔法冲击，造成无视护甲的魔法伤害，并显示预警粒子效果。

### 系统定位
作为 `Bomb` 的子类，提供特殊爆炸效果——5x5 范围魔法伤害，无地形破坏。需要 GooBlob（粘咕团）作为炼金材料。

### 不负责什么
- 不负责破坏地形
- 不负责火焰/冰冻等元素效果

## 3. 结构总览

### 主要成员概览
无新增实例字段，继承 `Bomb` 所有字段。

### 主要逻辑块概览
- **爆炸效果**：5x5 范围魔法伤害，无视护甲
- **预警粒子**：使用 `GooSprite.GooParticle` 显示即将爆炸区域

### 生命周期/调用时机
与父类 `Bomb` 相同，爆炸时触发魔法冲击效果。

## 4. 继承与协作关系

### 父类提供的能力
继承 `Bomb` 所有能力：点燃投掷、爆炸触发、伤害计算框架等。

### 覆写的方法
| 方法 | 职责变更 |
|------|---------|
| `explodesDestructively()` | 返回 false，不破坏地形 |
| `explosionRange()` | 返回 2，5x5 爆炸范围 |
| `createFuse()` | 返回 `ArcaneBombFuse`，带粒子预警 |
| `explode(int)` | 实现魔法冲击伤害，无视护甲 |
| `value()` | 返回 50（材料价格之和） |

### 依赖的关键类
| 类 | 用途 |
|-----|------|
| `ArcaneBombFuse` | 内部类，带粒子预警的引信 |
| `GooSprite.GooParticle` | 粘咕粒子效果，用于预警 |
| `ElmoParticle` | 爆炸时的魔法粒子 |
| `GooBlob` | 炼金材料（粘咕团） |

### 使用者
- `EnhanceBomb` 炼金配方系统
- 玩家背包和使用

## 5. 字段/常量详解

### 实例字段

| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.ARCANE_BOMB | 奥术炸弹图标 |

### 内部类 ArcaneBombFuse 字段

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `gooWarnEmitters` | ArrayList<Emitter> | 粒子发射器列表，用于预警效果 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.ARCANE_BOMB;
}
```

## 7. 方法详解

### explodesDestructively()

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：奥术炸弹不破坏地形。

**返回值**：boolean，始终返回 false。

**核心实现逻辑**：
```java
@Override
public boolean explodesDestructively() {
    return false;
}
```

---

### explosionRange()

**可见性**：protected

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：返回爆炸范围。

**返回值**：int，返回 2（5x5 区域）。

---

### createFuse()

**可见性**：protected

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：创建带粒子预警效果的引信。

**返回值**：Fuse，返回 `ArcaneBombFuse` 实例。

---

### explode(int)

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：执行魔法冲击爆炸，造成无视护甲的伤害。

**参数**：
- `cell` (int)：爆炸中心格子

**副作用**：
- 调用 `super.explode()` 清除引信和播放音效
- 对 5x5 范围内所有角色造成魔法伤害
- 显示 `ElmoParticle` 粒子效果
- 可能导致英雄死亡

**核心实现逻辑**：
```java
@Override
public void explode(int cell) {
    super.explode(cell);
    
    ArrayList<Char> affected = new ArrayList<>();
    
    PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), explosionRange());
    for (int i = 0; i < PathFinder.distance.length; i++) {
        if (PathFinder.distance[i] < Integer.MAX_VALUE) {
            CellEmitter.get(i).burst(ElmoParticle.FACTORY, 10);
            Char ch = Actor.findChar(i);
            if (ch != null) affected.add(ch);
        }
    }
    
    for (Char ch : affected) {
        // 无视护甲的伤害
        int damage = Math.round(Random.NormalIntRange(4 + Dungeon.scalingDepth(), 
                                                        12 + 3*Dungeon.scalingDepth()));
        ch.damage(damage, this);
        if (ch == Dungeon.hero && !ch.isAlive()) {
            Badges.validateDeathFromFriendlyMagic();
            Dungeon.fail(this);
        }
    }
}
```

**边界情况**：伤害直接应用，不减去护甲减免（`drRoll()`）。

---

### value()

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：返回奥术炸弹的出售价格。

**返回值**：int，返回 `quantity * 50`（炸弹 20 + GooBlob 30）。

## 8. 对外暴露能力

### 显式 API
继承 `Bomb` 所有公开 API。

### 内部辅助方法
无。

### 扩展入口
- `explode(int)` 可进一步覆写修改伤害计算

## 9. 运行机制与调用链

### 创建时机
通过炼金合成：`Bomb` + `GooBlob` → `ArcaneBomb`（炼金费用 6）

### 调用者
- 炼金系统
- 玩家使用

### 系统流程位置
```
炼金合成 → 获得 ArcaneBomb
    ↓
点燃投掷 → ArcaneBombFuse.ignite() → 显示预警粒子
    ↓
引信烧尽 → explode() → 魔法冲击伤害
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.bombs.arcanebomb.name` | 奥术炸弹 | 物品名称 |
| `items.bombs.arcanebomb.desc` | 这枚炸弹里灌注了奥术能量... | 物品描述 |
| `items.bombs.arcanebomb.discover_hint` | 你可通过炼金合成该物品。 | 发现提示 |

### 依赖的资源
- **图标**：`ItemSpriteSheet.ARCANE_BOMB`
- **粒子**：`ElmoParticle`、`GooSprite.GooParticle`

### 中文翻译来源
`core/src/main/assets/messages/items_zh.properties`

## 11. 使用示例

### 基本用法

```java
// 炼金合成
Bomb bomb = new Bomb();
GooBlob blob = new GooBlob();
// 使用 EnhanceBomb 配方合成
ArcaneBomb arcaneBomb = new ArcaneBomb();

// 使用方式与普通炸弹相同
hero.handle(arcaneBomb);
```

## 12. 开发注意事项

### 状态依赖
- 预警粒子效果由 `ArcaneBombFuse` 管理
- 粒子发射器需要在引信熄灭时清理

### 生命周期耦合
- `gooWarnEmitters` 需要在 `snuff()` 时正确清理

### 常见陷阱
- 爆炸伤害无视护甲，对英雄同样有效
- 预警粒子在游戏场景加载完成后才添加

## 13. 修改建议与扩展点

### 适合扩展的位置
- `explode(int)` 可覆写修改伤害公式或添加额外效果

### 不建议修改的位置
- `ArcaneBombFuse` 的粒子管理逻辑

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