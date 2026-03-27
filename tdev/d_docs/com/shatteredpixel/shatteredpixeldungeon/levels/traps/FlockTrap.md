# FlockTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/FlockTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 85 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`FlockTrap`（羊群陷阱）负责触发后在周围生成一群魔法绵羊，属于趣味性陷阱。

### 系统定位
陷阱系统中的趣味型陷阱。生成的绵羊是无害的 NPC，主要用于阻挡移动。

### 不负责什么
- 不造成任何伤害
- 不生成敌对怪物

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = WHITE`，`shape = WAVES`
- **activate() 方法**：生成绵羊

### 主要逻辑块概览
1. **范围计算**：计算 2 格范围内的可生成位置
2. **绵羊生成**：在空位置生成魔法绵羊
3. **陷阱处理**：触发绵羊位置的陷阱

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
| `activate()` | 实现绵羊生成效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Sheep` | 魔法绵羊 NPC |
| `PathFinder.buildDistanceMap()` | 距离计算 |
| `BArray.not()` | 数组取反 |
| `CellEmitter` | 粒子效果 |
| `Speck.WOOL` | 羊毛粒子 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | WHITE (6) | 白色陷阱 |
| `shape` | WAVES (1) | 波浪图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = WHITE;
    shape = WAVES;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：在陷阱周围 2 格范围内生成一群魔法绵羊。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 生成多只绵羊
- 触发绵羊位置的陷阱
- 播放音效

**核心实现逻辑**：
```java
@Override
public void activate() {
    // 计算范围内可生成位置
    PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), 2);
    ArrayList<Integer> spawnPoints = new ArrayList<>();
    for (int i = 0; i < PathFinder.distance.length; i++) {
        if (PathFinder.distance[i] < Integer.MAX_VALUE) {
            spawnPoints.add(i);
        }
    }

    for (int i : spawnPoints) {
        Trap t;
        if (Dungeon.level.insideMap(i)
                && Actor.findChar(i) == null
                && !(Dungeon.level.pit[i])) {
            // 生成绵羊
            Sheep sheep = new Sheep();
            sheep.initialize(6);
            sheep.pos = i;
            GameScene.add(sheep);
            CellEmitter.get(i).burst(Speck.factory(Speck.WOOL), 4);
            
            // 触发该位置的陷阱
            if ((t = Dungeon.level.traps.get(i)) != null && t.active) {
                if (t.disarmedByActivation) t.disarm();
                t.reveal();
                Bestiary.setSeen(t.getClass());
                Bestiary.countEncounter(t.getClass());
                t.activate();
            }
            Dungeon.level.occupyCell(sheep);
        } else if (Actor.findChar(i) instanceof Mob) {
            Buff.prolong(Actor.findChar(i), Trap.HazardAssistTracker.class, 
                         HazardAssistTracker.DURATION);
        }
    }
    
    Sample.INSTANCE.play(Assets.Sounds.PUFF);
    Sample.INSTANCE.play(Assets.Sounds.SHEEP);
}
```

**边界情况**：
- 深渊位置不会生成绵羊
- 地图边界外不会生成绵羊
- 已有角色的位置不会生成绵羊

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发绵羊生成（覆写自 Trap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `PathFinder.buildDistanceMap()`：计算范围
- `Sheep.initialize()`：初始化绵羊
- `GameScene.add()`：添加绵羊
- `CellEmitter.get().burst()`：播放粒子

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
计算 2 格范围内的位置
    ↓
在空位置生成绵羊
    ↓
触发绵羊位置的陷阱
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.flocktrap.name` | 羊群陷阱 | 陷阱名称 |
| `levels.traps.flocktrap.desc` | 也许是个来自一些业余法师的玩笑，触发这个陷阱就会创造一群魔法绵羊。 | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.PUFF` | 烟雾音效 |
| 音效 | `Assets.Sounds.SHEEP` | 羊叫声 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置羊群陷阱
FlockTrap trap = new FlockTrap();
trap.set(position);
trap.hide();

// 触发后在 2 格范围内生成一群绵羊
// 绵羊寿命 = 6 turn
```

## 12. 开发注意事项

### 状态依赖
- 绵羊不会在深渊位置生成
- 绵羊不会在地图边界外生成

### 绵羊特性
- 类型：NPC（中立）
- 寿命：6 回合
- 功能：阻挡移动

### 常见陷阱
- 绵羊会触发其位置的陷阱
- 绵羊可能阻挡玩家移动

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整绵羊寿命
- 可修改生成范围

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已详细说明