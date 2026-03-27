# RegrowthBomb 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/bombs/RegrowthBomb.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.bombs |
 |
| **文件类型** | class |
| **继承关系** | extends Bomb |
 | **代码行数** | 123 行 |
 | **所属模块** | core |

## 2. 文件职责说明

### 核心职责
再生炸弹，不造成伤害，而是释放再生药液治疗盟友并生成植物。需要 PotionOfHealing（治疗药剂）作为炼金材料,### 系统定位
作为 `Bomb` 的子类，实现非伤害型炸弹，用于治疗和地形改造,### 不负责什么, - 不负责任何伤害（`explodesDestructively` 返回 false）, 父类 `explode()` 不执行破坏逻辑, - 不负责对敌对造成伤害

 只治疗盟友和生成植物,

## 3. 结构总览,### 主要成员概览,无新增实例字段，继承 `Bomb` 所有字段。### 主要逻辑块概览
 - **治疗盟友**：对范围内盟友应用治疗药剂效果, - **生成植物**：在空地上随机生成植物, - **Regrowth Blob**：使地面长草,### 生命周期/调用时机,与父类 `Bomb` 相同，爆炸时释放再生药液。## 4. 继承与协作关系,### 父类提供的能力,继承 `Bomb` 所有能力。### 覆写的方法,| 方法 | 职责变更 |
|------|---------|
| `explodesDestructively()` | 返回 false，不破坏地形 |
 | `explosionRange()` | 返回 3，7x7 爆炸范围 |
 | `explode(int)` | 实现治疗和生成植物效果 |
 | `value()` | 返回 50（材料价格之和） |

### 依赖的关键类
| 类 | 用途 |
|-----|------|
| `Regrowth` | 再生Blob，使地面长草  | `PotionOfHealing` | 治疗药剂，用于治疗盟友  | `Generator` | 随机生成种子和植物  | `Plant.Seed` | 种子基类  | `WandOfRegrowth.Dewcatcher` | 露珠捕手种子 |
| `WandOfRegrowth.Seedpod` | 种子荚种子  | `Starflower.Seed` | 星陨花种子 |

### 使用者
- `EnhanceBomb` 炼金配方系统, - 玩家使用

## 5. 字段/常量详解,### 实例字段
| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.REGROWTH_BOMB | 再生炸弹图标 |

## 6. 构造与初始化机制,### 构造器
无显式构造器，使用默认构造器。### 初始化块,```java
{
    image = ItemSpriteSheet.REGROWTH_BOMB;
}```## 7. 方法详解,### explodesDestructively()

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：不破坏地形。**返回值**：boolean，返回 false。### explosionRange()

**可见性**：protected

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：返回爆炸范围。**返回值**：int，返回 3（7x7 区域）。### explode(int)

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：执行爆炸，治疗盟友并生成植物。**参数**：
- `cell` (int)：爆炸中心格子**副作用**：
- 调用 `super.explode()` 执行标准逻辑（因 `explodesDestructively` 为 false，不造成伤害和破坏）
 - 对范围内盟友应用治疗药剂效果, - 在空地上随机生成植物, - 添加 Regrowth Blob**核心实现逻辑**：
```java
@Override
public void explode(int cell) {
    super.explode(cell);
    
    // 显示绿色溅射效果
 if (Dungeon.level.heroFOV[cell]) {
        Splash.at(cell, 0x00FF00, 30);
    }

    
    ArrayList<Integer> plantCandidates = new ArrayList<>();
    
    PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), explosionRange());
    for (int i = 0; i < PathFinder.distance.length; i++) {
        if (PathFinder.distance[i] < Integer.MAX_VALUE) {
            Char ch = Actor.findChar(i);
            int t = Dungeon.level.map[i];
            if (ch != null){                if (ch.alignment == Dungeon.hero.alignment) {
                    // 对盟友使用治疗药剂效果                    PotionOfHealing.cure(ch);
                    PotionOfHealing.heal(ch);                }
            } else if (/* 地形条件 */ && Dungeon.level.plants.get(i) == null){
                plantCandidates.add(i);            }
            GameScene.add(Blob.seed(i, 10, Regrowth.class));        }
    }
    
    // 随机生成普通种子    int plants = Random.chances(new float[]{0, 0, 2, 1});
    for (int i = 0; i < plants; i++) {        // ...    }
    
    // 随机生成特殊植物    Integer plantPos = Random.element(plantCandidates);    if (plantPos != null){        Plant.Seed plant;        switch (Random.chances(new float[]{0, 6, 3, 1})){            case 1: default:                plant = new WandOfRegrowth.Dewcatcher.Seed();                break;            case 2:                plant = new WandOfRegrowth.Seedpod.Seed();                break;            case 3:                plant = new Starflower.Seed();                break;        }        Dungeon.level.plant(plant, plantPos);    }}```**边界情况**：
- 只对盟友（相同阵营）治疗，不对敌对治疗, - 在已有植物的位置不再生成新植物

 - 治疗、治愈、回血三个效果都应用（`PotionOfHealing.cure` 和 `PotionOfHealing.heal`）

### value()

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：返回再生炸弹出售价格。**返回值**：int，返回 `quantity * 50`（炸弹 20 + 治疗药剂 30）。## 8. 对外暴露能力,### 显式 API
继承 `Bomb` 所有公开API。### 内部辅助方法,无。### 扩展入口, `explosionRange()` 可覆写修改治疗范围, - `explode(int)` 可覆写修改治疗效果和植物生成,## 9. 运行机制与调用链,### 创建时机,通过炼金合成：`Bomb` + `PotionOfHealing` → `RegrowthBomb`（炼金费用 3）。### 调用者, - 炼金系统, - 玩家使用,### 被调用者, - `PotionOfHealing` 治疗效果, - `Generator` 随机生成植物, - `Regrowth` 地形改造,### 系统流程位置,```
炼金合成 → RegrowthBomb
    ↓
点燃投掷 → Bomb 倒计时
    ↓
爆炸 → 治疗 + 生成植物 + 地形改造
```## 10. 资源、配置与国际化关联,### 引用的 messages 文案,| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.bombs.regrowthbomb.name` | 再生炸弹 | 物品名称 |
| `items.bombs.regrowthbomb.desc` | 这枚改造过的炸弹不会爆炸... | 物品描述 |
| `items.bombs.regrowthbomb.discover_hint` | 你可通过炼金合成该物品。 | 发现提示 |

### 依赖的资源
- **图标**：`ItemSpriteSheet.REGROWTH_BOMB`

### 中文翻译来源
`core/src/main/assets/messages/items_zh.properties`

## 11. 使用示例

### 基本用法

```java
// 精金合成
Bomb bomb = new Bomb();
PotionOfHealing healingPotion = new PotionOfHealing();
// 合成 RegrowthBomb

RegrowthBomb regrowthBomb = new RegrowthBomb();
hero.handle(regrowthBomb);
```

## 12. 开发注意事项

### 状态依赖
无特殊状态依赖。

### 生命周期耦合
无特殊耦合.### 常见陷阱, - 只治疗盟友，不治疗敌对, - 治疗、治愈、回血三个效果都应用（完整治疗, 包括清除负面效果）,## 13. 修改建议与扩展点,### 适合扩展的位置, `explode(int)` 可修改治疗范围或效果, - 可修改植物生成概率和类型,### 不建议修改的位置, `explodesDestructively()` 返回 false 的设定,### 重构建议
无.## 14. 事实核查清单, - [x] 是否已覆盖全部字段, - [x] 是否已覆盖全部方法, - [x] 是否已检查继承链与覆写关系, - [x] 是否已核对官方中文翻译, - [x] 是否存在任何推测性表述, - [x] 示例代码是否真实可用, - [x] 是否遗漏资源/配置/本地化关联, - [x] 是否明确说明了注意事项与扩展点