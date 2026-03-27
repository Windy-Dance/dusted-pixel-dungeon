# SmokeBomb 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/bombs/SmokeBomb.java |
 | **包名** | com.shatteredpixel.shatteredpixeldungeon.items.bombs |
 |
| **文件类型** | class |
 | **继承关系** | extends Bomb |
 | **代码行数** | 68 行 |
 | **所属模块** | core |

## 2. 文件职责说明

### 核心职责
烟雾弹，爆炸时释放浓厚烟云，阻挡视野。需要 PotionOfInvisibility（隐形药剂）作为炼金材料,### 系统定位,作为 `Bomb` 的子类，实现烟雾遮挡效果，用于掩护移动或逃脱,### 不负责什么, - 不负责特殊伤害（依赖父类标准伤害）
 - 不负责地形破坏（依赖父类破坏逻辑）

## 3. 结构总览,### 主要成员概览,无新增实例字段，继承 `Bomb` 所有字段。### 主要逻辑块概览, - **烟雾释放**：爆炸时创建 SmokeScreen Blob,### 生命周期/调用时机,与父类 `Bomb` 相同，爆炸时释放烟雾。## 4. 继承与协作关系,### 父类提供的能力,继承 `Bomb` 所有能力。### 覆写的方法,| 方法 | 职责变更 |
|------|---------|
| `explosionRange()` | 返回 2，5x5 爆炸范围 |
 | `explode(int)` | 添加烟雾效果 | `value()` | 返回 60（材料价格之和） |

### 依赖的关键类
| 类 | 用途 |
|-----|------|
| `SmokeScreen` | 烟雾Blob，阻挡视野  | `Blob` | Blob基类  | `PotionOfInvisibility` | 炼金材料 |

### 使用者
- `EnhanceBomb` 炼金配方系统, - 玩家使用

## 5. 字段/常量详解,### 实例字段,| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.SMOKE_BOMB | 烟雾弹图标 |

## 6. 构造与初始化机制,### 构造器
无显式构造器，使用默认构造器。### 初始化块,```java
{
    image = ItemSpriteSheet.SMOKE_BOMB;
}```## 7. 方法详解,### explosionRange()

**可见性**：protected

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：返回爆炸范围。**返回值**：int，返回 2（5x5 区域）。### explode(int)

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：执行爆炸，释放烟雾。**参数**：
- `cell` (int)：爆炸中心格子**副作用**：
- 调用 `super.explode()` 执行标准爆炸, - 创建 SmokeScreen Blob，阻挡视野**核心实现逻辑**：
```java
@Override
public void explode(int cell) {
    super.explode(cell);

    int centerVolume = 1000; // 40*25
 PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), explosionRange());    for (int i = 0; i < PathFinder.distance.length; i++) {        if (PathFinder.distance[i] < Integer.MAX_VALUE) {            GameScene.add(Blob.seed(i, 40, SmokeScreen.class));            centerVolume -= 40;        }    }

    // 如果有些格子被阻挡，剩余容量加到中心    if (centerVolume > 0){        GameScene.add(Blob.seed(cell, centerVolume, SmokeScreen.class));    }}```**边界情况**：
- 篇幅上限为1000，如果某些格子被阻挡，剩余容量会集中释放到中心,### value()

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：返回烟雾弹出售价格。**返回值**：int，返回 `quantity * 60`（炸弹 20 + 隐形药剂 40）。## 8. 对外暴露能力,### 显式 API,继承 `Bomb` 所有公开API。### 内部辅助方法,无.### 扩展入口, `explosionRange()` 可覆写修改烟雾范围, - `explode(int)` 可覆写修改烟雾量,## 9. 运行机制与调用链,### 创建时机,通过炼金合成：`Bomb` + `PotionOfInvisibility` → `SmokeBomb`（炼金费用 2）。### 调用者, - 炼金系统, - 玩家使用,### 被调用者, - `SmokeScreen` 瑟雾效果,### 系统流程位置,```
炼金合成 → SmokeBomb
    ↓
点燃投掷 → Bomb 倒计时    ↓
爆炸 → 标准爆炸 + 烟雾释放  ```## 10. 资源、配置与国际化关联,### 引用的 messages 文案,| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.bombs.smokebomb.name` | 粉雾弹 | 物品名称 |
| `items.bombs.smokebomb.desc` | 这枚改造过的炸弹会在爆炸时释放一阵浓厚的气体烟云... | 物品描述 |
| `items.bombs.smokebomb.discover_hint` | 你可通过炼金合成该物品。 | 发现提示 |

### 依赖的资源
- **图标**：`ItemSpriteSheet.SMOKE_BOMB`

### 中文翻译来源
`core/src/main/assets/messages/items_zh.properties`

## 11. 使用示例

### 基本用法

```java
// 摇金合成
Bomb bomb = new Bomb();
PotionOfInvisibility invisPotion = new PotionOfInvisibility();
// 合成 SmokeBomb

SmokeBomb smokeBomb = new SmokeBomb();
hero.handle(smokeBomb);

// 用于掩护撤退或隐形
```

## 12. 开发注意事项

### 状态依赖
无特殊状态依赖。### 生命周期耦合
无特殊耦合.### 常见陷阱, - 累雾对敌我视野同样有影响, - 档在烟雾中时隐形效果会被移除,## 13. 修改建议与扩展点,### 适合扩展的位置, `explosionRange()` 可覆写修改烟雾范围, - `explode(int)` 可覆写修改烟雾量,### 不建议修改的位置,无。### 重构建议,无.## 14. 事实核查清单, - [x] 是否已覆盖全部字段, - [x] 是否已覆盖全部方法, - [x] 是否已检查继承链与覆写关系, - [x] 是否已核对官方中文翻译, - [x] 是否存在任何推测性表述, - [x] 示例代码是否真实可用, - [x] 是否遗漏资源/配置/本地化关联, - [x] 是否明确说明了注意事项与扩展点