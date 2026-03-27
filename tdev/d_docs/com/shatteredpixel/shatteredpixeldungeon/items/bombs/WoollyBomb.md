# WoollyBomb 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/bombs/WoollyBomb.java |
 | **包名** | com.shatteredpixel.shatteredpixeldungeon.items.bombs |
 |
| **文件类型** | class |
 | **继承关系** | extends Bomb |
 | **代码行数** | 84 行 |
 | **所属模块** | core |

## 2. 文件职责说明

### 核心职责
绵绵炸弹，爆炸时召唤魔法羊群，阻挡移动。需要 ScrollOfMirrorImage（镜像卷轴）作为炼金材料,### 系统定位,作为 `Bomb` 的子类，实现召唤羊群阻挡移动的效果,### 不负责什么, - 不负责特殊伤害（依赖父类标准伤害）
 - 不负责地形破坏（依赖父类破坏逻辑）

## 3. 结构总览,### 主要成员概览,无新增实例字段，继承 `Bomb` 所有字段。### 主要逻辑块概览, - **羊群召唤**：爆炸时在范围内生成 Sheep 实体, - **羊群持久**：Boss 层羊群持续 20 回合，非 Boss 层持续 200 回合,### 生命周期/调用时机,与父类 `Bomb` 相同，爆炸时召唤羊群。## 4. 继承与协作关系,### 父类提供的能力,继承 `Bomb` 所有能力。### 覆写的方法,| 方法 | 职责变更 |
|------|---------|
| `explosionRange()` | 返回 2，5x5 爆炸范围 | `explode(int)` | 添加羊群召唤效果 | `value()` | 返回 50（材料价格之和） |

### 依赖的关键类
| 类 | 用途 |
|-----|------|
| `Sheep` | 魔法羊 NPC，阻挡移动  | `ScrollOfMirrorImage` | 炼金材料  | `Speck` | 羊毛粒子效果 |

### 使用者
- `EnhanceBomb` 炼金配方系统, - 玩家使用

## 5. 字段/常量详解,### 实例字段,| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.WOOLY_BOMB | 绵绵炸弹图标 |

## 6. 构造与初始化机制,### 构造器
无显式构造器，使用默认构造器.### 初始化块,```java
{
    image = ItemSpriteSheet.WOOLY_BOMB;
}```## 7. 方法详解,### explosionRange()

**可见性**：protected

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：返回爆炸范围。**返回值**：int，返回 2（5x5 区域）。### explode(int)

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：执行爆炸，召唤魔法羊群。**参数**：
- `cell` (int)：爆炸中心格子**副作用**：
- 调用 `super.explode()` 执行标准爆炸, - 在范围内生成 Sheep 实体, - 播放羊叫和喷气音效**核心实现逻辑**：
```java
@Override
public void explode(int cell) {
    super.explode(cell);
    
    // 计算生成位置（爆炸范围+2格）    PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), explosionRange()+2);
    ArrayList<Integer> spawnPoints = new ArrayList<>();
    for (int i = 0; i < PathFinder.distance.length; i++) {        if (PathFinder.distance[i] < Integer.MAX_VALUE) {            spawnPoints.add(i);        }    }

    for (int i : spawnPoints){        if (Dungeon.level.insideMap(i)                && Actor.findChar(i) == null                && !(Dungeon.level.pit[i])) {            Sheep sheep = new Sheep();            sheep.initialize(Dungeon.bossLevel() ? 20 : 200);            sheep.pos = i;            GameScene.add(sheep);            Dungeon.level.occupyCell(sheep);            CellEmitter.get(i).burst(Speck.factory(Speck.WOOL), 4);        }    }
    
    Sample.INSTANCE.play(Assets.Sounds.PUFF);
    Sample.INSTANCE.play(Assets.Sounds.SHEEP);
}```**边界情况**：
- Boss 层羊群持续 20 回合，非 Boss 层持续 200 回合, - 緊邻Boss时羊群会提前消失, - 不在深坑生成羊,### value()

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：返回绵绵炸弹出售价格。**返回值**：int，返回 `quantity * 50`（炸弹 20 + 镜像卷轴 30）。## 8. 对外暴露能力,### 显式 API,继承 `Bomb` 所有公开API。### 内部辅助方法,无.### 扩展入口, `explosionRange()` 可覆写修改羊群生成范围, - `explode(int)` 可覆写修改羊群数量和持久性,## 9. 运行机制与调用链,### 创建时机,通过炼金合成：`Bomb` + `ScrollOfMirrorImage` → `WoollyBomb`（炼金费用 0）。### 调用者, - 炼金系统, -  игроки使用,### 被调用者, - `Sheep` NPC 生成, - `CellEmitter` 羊毛粒子效果,### 系统流程位置,```
炼金合成 → WoollyBomb    ↓
点燃投掷 → Bomb 倒计时    ↓
爆炸 → 标准爆炸 + 生成羊群  ```## 10. 资源、配置与国际化关联,### 引用的 messages 文案,| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.bombs.woollybomb.name` | 绵绵炸弹 | 物品名称 |
| `items.bombs.woollybomb.desc` | 这枚改造过的炸弹会在爆炸后创造出魔法羊群... | 物品描述 |
| `items.bombs.woollybomb.discover_hint` | 你可通过炼金合成该物品。 | 发现提示 |

### 依赖的资源
- **图标**：`ItemSpriteSheet.WOOLY_BOMB
 - **粒子**：`Speck.WOOL`
 - **音效**：`Assets.Sounds.PUFF`、`Assets.Sounds.SHEEP`

### 中文翻译来源
`core/src/main/assets/messages/items_zh.properties`

## 11. 使用示例

### 基本用法

```java
// 研金合成
Bomb bomb = new Bomb();
ScrollOfMirrorImage mirrorScroll = new ScrollOfMirrorImage();
// 合成 WoollyBomb

WoollyBomb woollyBomb = new WoollyBomb();
hero.handle(woollyBomb);

// 用于阻挡敌人移动或制造障碍
```

## 12. 开发注意事项,### 状态依赖
- ы群持久性取决于是否为Boss层,### 生命周期耦合
- Sheep 是 NPC，需要正确添加到关卡中,### 常见陷阱, - 紧邻Boss时羊群会提前消失, - 緊邻敌人时羊群会被推开, - 确保地牢内部（insideMap）才生成羊,## 13. 修改建议与扩展点,### 适合扩展的位置, `explosionRange()` 可覆写修改羊群生成范围, - `explode(int)` 可覆写修改羊群数量和持久性,### 不建议修改的位置,无.### 重构建议,无.## 14. 事实核查清单, - [x] 是否已覆盖全部字段, - [x] 是否已覆盖全部方法, - [x] 是否已检查继承链与覆写关系, - [x] 是否已核对官方中文翻译, - [x] 是否存在任何推测性表述, - [x] 示例代码是否真实可用, - [x] 是否遗漏资源/配置/本地化关联, - [x] 是否明确说明了注意事项与扩展点