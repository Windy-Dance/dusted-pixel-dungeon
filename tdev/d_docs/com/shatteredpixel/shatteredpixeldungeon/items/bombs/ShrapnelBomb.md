# ShrapnelBomb 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/bombs/ShrapnelBomb.java |
 | **包名** | com.shatteredpixel.shatteredpixeldungeon.items.bombs |
 |
| **文件类型** | class |
 | **继承关系** | extends Bomb |
 | **代码行数** | 90 行 |
 | **所属模块** | core |

## 2. 文件职责说明

### 核心职责
破片炸弹，爆炸时释放金属破片，在极大范围内造成伤害，伤害受视野限制，需要躲在掩体后避免伤害。需要 MetalShard（金属碎片，来自DM-300）作为炼金材料,### 系统定位,作为 `Bomb` 的子类，实现大范围伤害型炸弹。爆炸范围8格，但伤害受视野限制（需要掩体保护自己）。### 不负责什么, - 不负责地形破坏（`explodesDestructively` 返回 false）, 父类 `explode()` 不执行破坏逻辑, 因为此炸弹不破坏地形)

## 3. 结构总览,### 主要成员概览,无新增实例字段，继承 `Bomb` 所有字段。### 主要逻辑块概览, - **视野检测**：使用 ShadowCaster 检测视野范围, - **破片伤害**：对视野范围内所有角色造成伤害,### 生命周期/调用时机,与父类 `Bomb` 相同，爆炸时释放破片造成大范围伤害。## 4. 继承与协作关系,### 父类提供的能力,继承 `Bomb` 所有能力。### 覆写的方法,| 方法 | 职责变更 |
|------|---------|
| `explodesDestructively()` | 返回 false，不破坏地形 |
 | `explosionRange()` | 返回 8，视野范围 |
 | `explode(int)` | 实现破片伤害效果 |
 | `value()` | 返回 70（材料价格之和） |

### 依赖的关键类
| 类 | 用途 |
|-----|------|
| `ShadowCaster` | 视野检测算法, | `BlastParticle` | 爆炸粒子效果  | `MetalShard` | 炼金材料，来自DM-300 |

### 使用者
- `EnhanceBomb` 炼金配方系统, - 玩家使用

## 5. 字段/常量详解,### 实例字段,| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.SHRAPNEL_BOMB | 狼片炸弹图标 |

## 6. 构造与初始化机制,### 构造器
无显式构造器，使用默认构造器。### 初始化块,```java
{
    image = ItemSpriteSheet.SHRAPNEL_BOMB;
}```## 7. 方法详解,### explodesDestructively()

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：不破坏地形。**返回值**：boolean，返回 false。### explosionRange()

**可见性**：protected

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：返回视野范围。**返回值**：int，返回 8。### explode(int)

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：执行爆炸，释放破片造成大范围伤害。**参数**：
- `cell` (int)：爆炸中心格子**副作用**：
- 调用 `super.explode()` 执行标准逻辑（因 `explodesDestructively` 为 false，不造成伤害和破坏, - 对视野范围内所有角色造成伤害, - 显示爆炸粒子效果**核心实现逻辑**：
```java
@Override
public void explode(int cell) {
    super.explode(cell);
    
    // 计算视野范围    boolean[] FOV = new boolean[Dungeon.level.length()];
    Point c = Dungeon.level.cellToPoint(cell);    ShadowCaster.castShadow(c.x, c.y, Dungeon.level.width(), FOV, Dungeon.level.losBlocking, explosionRange());
    
    ArrayList<Char> affected = new ArrayList<>();
    
    for (int i = 0; i < FOV.length; i++) {        if (FOV[i]) {            // 显示爆炸粒子
 if (Dungeon.level.heroFOV[i] && !Dungeon.level.solid[i]) {                CellEmitter.center(i).burst(BlastParticle.FACTORY, 5);            }
            Char ch = Actor.findChar(i);            if (ch != null){                affected.add(ch);            }
        }    }
    
    for (Char ch : affected){        // 标准炸弹伤害，受护甲减免        int damage = Random.NormalIntRange(4 + Dungeon.scalingDepth(), 12 + 3*Dungeon.scalingDepth());        damage -= ch.drRoll();        ch.damage(damage, this);        if (ch == Dungeon.hero && !ch.isAlive()) {            Dungeon.fail(this);        }    }}```**边界情况**：
- 伤害受护甲减免, - 需要掩体保护自己（视野外的角色才会受到伤害）,### value()

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：返回破片炸弹出售价格。**返回值**：int，返回 `quantity * 70`（炸弹 20 + MetalShard 50）。## 8. 对外暴露能力,### 显式 API,继承 `Bomb` 所有公开API。### 内部辅助方法,无。### 扩展入口, `explosionRange()` 可覆写修改视野范围, - `explode(int)` 可覆写修改伤害计算,## 9. 运行机制与调用链,### 创建时机,通过炼金合成：`Bomb` + `MetalShard` → `ShrapnelBomb`（炼金费用 6）。### 调用者, - 炼金系统, - 玩家使用,### 被调用者, - `ShadowCaster` 视野检测, - `BlastParticle` 粒子效果,### 系统流程位置,```
炼金合成 → ShrapnelBomb
    ↓
点燃投掷 → Bomb 倒计时,    ↓
爆炸 → 视野检测 + 破片伤害  ```## 10. 资源、配置与国际化关联,### 引用的 messages 文案,| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.bombs.shrapnelbomb.name` | 破片炸弹 | 物品名称 |
| `items.bombs.shrapnelbomb.desc` | 这枚改造过的炸弹的外壳由DM-300的金属残骸制成... | 物品描述 |
| `items.bombs.shrapnelbomb.discover_hint` | 你可通过炼金合成该物品。 | 发现提示 |

### 依赖的资源
- **图标**：`ItemSpriteSheet.SHRAPNEL_BOMB`

### 中文翻译来源
`core/src/main/assets/messages/items_zh.properties`

## 11. 使用示例

### 基本用法

```java
// 磨金合成
Bomb bomb = new Bomb();
MetalShard metalShard = new MetalShard();
// 合成 ShrapnelBomb

ShrapnelBomb shrapnelBomb = new ShrapnelBomb();
hero.handle(shrapnelBomb);

// 使用时需要躲在掩体后面！
```

## 12. 开发注意事项

### 状态依赖
无特殊状态依赖。### 生命周期耦合
无特殊耦合.### 常见陷阱, - 伤害受视野限制，需要掩体保护自己, - 伤害受护甲减免, - 纯粹的伤害型炸弹，不造成地形破坏或## 13. 修改建议与扩展点,### 适合扩展的位置, `explosionRange()` 可覆写修改视野范围, - `explode(int)` 可覆写修改伤害计算,### 不建议修改的位置, `explodesDestructively()` 返回 false 的设定,### 重构建议,无.## 14. 事实核查清单, - [x] 是否已覆盖全部字段, - [x] 是否已覆盖全部方法, - [x] 是否已检查继承链与覆写关系, - [x] 是否已核对官方中文翻译, - [x] 是否存在任何推测性表述, - [x] 示例代码是否真实可用, - [x] 是否遗漏资源/配置/本地化关联, - [x] 是否明确说明了注意事项与扩展点