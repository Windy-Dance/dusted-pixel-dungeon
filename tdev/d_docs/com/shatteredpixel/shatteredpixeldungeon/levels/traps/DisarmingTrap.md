# DisarmingTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/DisarmingTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 117 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`DisarmingTrap`（缴械陷阱）负责触发后将受害者手中的武器传送走，使其失去武器。

### 系统定位
陷阱系统中的功能性陷阱。通过移除武器对玩家造成战术上的不利影响。

### 不负责什么
- 不直接造成伤害
- 不负责物品的具体传送逻辑（由 Level.drop 处理）

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = RED`，`shape = LARGE_DOT`
- **activate() 方法**：执行缴械效果

### 主要逻辑块概览
1. **物品堆处理**：传送物品堆中的物品到随机位置
2. **雕像处理**：击杀石像守卫
3. **英雄处理**：传送英雄手中的武器到远处

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
| `activate()` | 实现缴械效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Heap` | 地面物品堆 |
| `Hero` | 英雄类 |
| `KindOfWeapon` | 武器基类 |
| `Statue` | 石像守卫 |
| `PathFinder` | 路径计算 |
| `GLog` | 日志输出 |
| `CellEmitter` | 粒子效果 |
| `Speck` | 光效粒子 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | RED (0) | 红色陷阱 |
| `shape` | LARGE_DOT (6) | 大点图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = RED;
    shape = LARGE_DOT;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：执行缴械效果，传送物品堆、击杀雕像、传送英雄武器。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 传送物品堆中的物品
- 击杀石像守卫
- 传送英雄武器到远处
- 播放视觉效果和音效

**核心实现逻辑**：
```java
@Override
public void activate() {
    // 处理物品堆
    Heap heap = Dungeon.level.heaps.get(pos);
    if (heap != null && heap.type == Heap.Type.HEAP) {
        int cell;
        do {
            cell = Dungeon.level.randomRespawnCell(null);
        } while (cell != -1 && Dungeon.level.heaps.get(pos) != null
                && Dungeon.level.heaps.get(pos).type != Heap.Type.HEAP);

        if (cell != -1) {
            Item item = heap.pickUp();
            Heap dropped = Dungeon.level.drop(item, cell);
            dropped.seen = true;
            // 特殊处理破碎蜜罐
            if (item instanceof Honeypot.ShatteredPot) {
                ((Honeypot.ShatteredPot)item).movePot(pos, cell);
            }
            // 标记周围格子为已访问
            for (int i : PathFinder.NEIGHBOURS9) 
                Dungeon.level.visited[cell+i] = true;
            GameScene.updateFog();
            Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
            CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 4);
        }
    }

    // 处理石像
    if (Actor.findChar(pos) instanceof Statue) {
        Actor.findChar(pos).die(this);
        Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
        CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 4);
    }

    // 处理英雄
    if (Dungeon.hero.pos == pos && !Dungeon.hero.flying) {
        Hero hero = Dungeon.hero;
        KindOfWeapon weapon = hero.belongings.weapon;

        if (weapon != null && !weapon.cursed) {
            // 寻找距离 10-20 格的传送目标
            int cell;
            int tries = 50;
            do {
                cell = Dungeon.level.randomRespawnCell(null);
                if (tries-- < 0 && cell != -1) break;
                PathFinder.buildDistanceMap(pos, Dungeon.level.passable);
            } while (cell == -1 || PathFinder.distance[cell] < 10 || PathFinder.distance[cell] > 20);

            if (tries < 0) return;

            // 移除武器并传送
            hero.belongings.weapon = null;
            Dungeon.quickslot.clearItem(weapon);
            weapon.updateQuickslot();

            Dungeon.level.drop(weapon, cell).seen = true;
            // 标记周围格子为已映射
            for (int i : PathFinder.NEIGHBOURS9) {
                Dungeon.level.mapped[cell + i] = true;
            }
            GameScene.updateFog(cell, 1);

            GLog.w(Messages.get(this, "disarm"));
            Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
            CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 4);
        }
    }
}
```

**边界情况**：
- 被诅咒的武器不会被传送
- 飞行状态的英雄不受影响
- 物品堆类型必须为 HEAP 才会被传送

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发缴械效果（覆写自 Trap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `Dungeon.level.randomRespawnCell()`：获取随机重生点
- `Dungeon.level.drop()`：放置物品
- `PathFinder.buildDistanceMap()`：计算距离
- `CellEmitter.get().burst()`：播放粒子效果

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
检查陷阱位置的对象类型
    ↓
物品堆：传送到随机位置
雕像：直接击杀
英雄：传送武器到 10-20 格外
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.disarmingtrap.name` | 缴械陷阱 | 陷阱名称 |
| `levels.traps.disarmingtrap.disarm` | 你手中的武器被传送走了！ | 缴械日志 |
| `levels.traps.disarmingtrap.desc` | 这个陷阱包含着非常有针对性的传送魔法，它会将触发陷阱的受害者的武器传送到其他位置。 | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.TELEPORT` | 传送音效 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置缴械陷阱
DisarmingTrap trap = new DisarmingTrap();
trap.set(position);
trap.hide();

// 英雄踩中后，手中的非诅咒武器会被传送到 10-20 格外的位置
```

## 12. 开发注意事项

### 状态依赖
- 飞行状态的英雄不受影响
- 被诅咒的武器不会被传送
- 物品堆类型必须为 HEAP

### 武器传送距离
- 最小距离：10 格
- 最大距离：20 格
- 最大尝试次数：50 次

### 常见陷阱
- 英雄没有武器或武器被诅咒时，陷阱对英雄无效果
- 石像会被直接击杀而非缴械

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整武器传送距离范围
- 可修改雕像和物品堆的处理逻辑

### 不建议修改的位置
- 距离计算使用 PathFinder，修改可能影响游戏平衡

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已详细说明