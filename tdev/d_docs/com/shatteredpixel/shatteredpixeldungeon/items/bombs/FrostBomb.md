# FrostBomb 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/bombs/FrostBomb.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.bombs |
| **文件类型** | class |
| **继承关系** | extends Bomb |
| **代码行数** | 67 行 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
冰霜炸弹，爆炸后释放持续冻结的冰霜，造成范围伤害和冰冻效果。

### 系统定位
作为 `Bomb` 的子类，提供冰霜爆炸效果。需要 `PotionOfFrost`（冰霜药剂）作为炼金材料。

### 不负责什么
- 不负责特殊的伤害计算（使用父类默认伤害）
- 不负责其他元素效果

## 3. 结构总览

### 主要成员概览
无新增实例字段，继承 `Bomb` 所有字段。

### 主要逻辑块概览
- **爆炸效果**：5x5 范围伤害 + 持续冰霜
- **冰冻效果**：对范围内角色施加 2 回合 Frost Buff

### 生命周期/调用时机
与父类 `Bomb` 相同，爆炸时释放冰霜。

## 4. 继承与协作关系

### 父类提供的能力
继承 `Bomb` 所有能力。

### 覆写的方法
| 方法 | 职责变更 |
|------|---------|
| `explosionRange()` | 返回 2，5x5 爆炸范围 |
| `explode(int)` | 添加冰霜地形和冰冻效果 |
| `value()` | 返回 50 |

### 依赖的关键类
| 类 | 用途 |
|-----|------|
| `Freezing` | 冰霜 Blob，持续冻结 |
| `Frost` | 冰冻 Buff，阻止行动 |
| `PotionOfFrost` | 炼金材料 |

### 使用者
- `EnhanceBomb` 炼金配方系统
- 玩家使用

## 5. 字段/常量详解

### 实例字段

| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.FROST_BOMB | 冰霜炸弹图标 |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.FROST_BOMB;
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

**方法职责**：执行爆炸并释放持续冰霜。

**参数**：
- `cell` (int)：爆炸中心格子

**副作用**：
- 调用 `super.explode()` 执行标准爆炸
- 在 5x5 范围内创建冰霜 Blob（强度 10）
- 对范围内角色施加 2 回合 Frost Buff

**核心实现逻辑**：
```java
@Override
public void explode(int cell) {
    super.explode(cell);
    
    PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), explosionRange());
    for (int i = 0; i < PathFinder.distance.length; i++) {
        if (PathFinder.distance[i] < Integer.MAX_VALUE) {
            GameScene.add(Blob.seed(i, 10, Freezing.class));
            Char ch = Actor.findChar(i);
            if (ch != null) {
                Buff.affect(ch, Frost.class, 2f);
            }
        }
    }
}
```

**边界情况**：
- 冰霜强度固定为 10
- Frost Buff 持续时间固定为 2 回合

---

### value()

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：返回冰霜炸弹的出售价格。

**返回值**：int，返回 `quantity * 50`（炸弹 20 + 冰霜药剂 30）。

## 8. 对外暴露能力

### 显式 API
继承 `Bomb` 所有公开 API。

### 内部辅助方法
无。

### 扩展入口
- `explode(int)` 可覆写修改冰霜强度或 Frost 持续时间

## 9. 运行机制与调用链

### 创建时机
通过炼金合成：`Bomb` + `PotionOfFrost` → `FrostBomb`（炼金费用 0）

### 调用者
- 炼金系统
- 玩家使用

### 系统流程位置
```
炼金合成 → 获得 FrostBomb
    ↓
点燃投掷 → Fuse 倒计时
    ↓
爆炸 → super.explode() + Freezing Blob + Frost Buff
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.bombs.frostbomb.name` | 冰霜炸弹 | 物品名称 |
| `items.bombs.frostbomb.desc` | 这枚改造过的炸弹的爆炸范围更大... | 物品描述 |
| `items.bombs.frostbomb.discover_hint` | 你可通过炼金合成该物品。 | 发现提示 |

### 依赖的资源
- **图标**：`ItemSpriteSheet.FROST_BOMB`

### 中文翻译来源
`core/src/main/assets/messages/items_zh.properties`

## 11. 使用示例

### 基本用法

```java
// 炼金合成（炼金费用为 0，最便宜的增强炸弹）
Bomb bomb = new Bomb();
PotionOfFrost potion = new PotionOfFrost();
// 合成得到 FrostBomb

// 使用
FrostBomb frostBomb = new FrostBomb();
hero.handle(frostBomb);
```

## 12. 开发注意事项

### 状态依赖
- 冰霜 Blob 强度固定为 10
- Frost Buff 持续时间固定为 2 回合

### 生命周期耦合
- 冰霜 Blob 会持续存在直到自然消退
- Frost Buff 会阻止角色行动

### 常见陷阱
- 冰冻效果对英雄同样有效
- 冰霜可以冻结水面等特殊地形

## 13. 修改建议与扩展点

### 适合扩展的位置
- `explode(int)` 可修改冰霜强度或 Frost 持续时间

### 不建议修改的位置
- 冰霜 Blob 的基本创建逻辑

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