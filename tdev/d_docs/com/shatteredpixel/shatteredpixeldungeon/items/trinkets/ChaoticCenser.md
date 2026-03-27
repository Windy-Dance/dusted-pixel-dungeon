# ChaoticCenser 饰物文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/trinkets/ChaoticCenser.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.trinkets |
| **文件类型** | class |
| **继承关系** | extends Trinket |
| **代码行数** | 343 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
混沌香炉是一种会自动生成并喷出各种气体的饰物。在炼金釜内炼制一段时间后，香炉会自行生烟，并间歇性随机向敌人喷去。气体只会在敌人存在时生成，且会在释放前给予预警。

### 系统定位
作为主动战斗辅助型饰物，混沌香炉为玩家提供额外的气体攻击手段，但不受玩家直接控制。气体种类和效果随饰物等级提升而增强。

### 不负责什么
- 不处理气体的具体伤害计算（由各气体类实现）
- 不控制气体喷出的时机（自动判定）

## 3. 结构总览

### 主要成员概览
- **图像索引**：ItemSpriteSheet.CHAOTIC_CENSER
- **静态常量**：GAS_CAT_CHANCES、COMMON_GASSES、UNCOMMON_GASSES、RARE_GASSES、MISSILE_VFX
- **内部类**：CenserGasTracker、GasSpewer

### 主要逻辑块概览
- 气体生成计时机制（CenserGasTracker）
- 气体喷发机制（GasSpewer）
- 气体种类选择与权重分配
- 目标选择与位置计算

### 生命周期/调用时机
- 装备时：附加CenserGasTracker buff
- 战斗中：定期检查并生成气体
- 生成气体后：延迟一回合喷发

## 4. 继承与协作关系

### 父类提供的能力
从Trinket继承：
- upgradeEnergyCost()框架
- statsDesc()框架
- trinketLevel()静态方法
- 所有Item基类功能

### 覆写的方法
| 方法 | 说明 |
|------|------|
| upgradeEnergyCost() | 返回6+2*level() |
| statsDesc() | 返回气体生成频率描述 |

### 依赖的关键类
- `Buff`、`FlavourBuff`：Buff系统
- `Blob`及其子类：各种气体效果
- `Char`、`Mob`：角色系统
- `TargetHealthIndicator`：目标指示器
- `GameScene`：游戏场景管理
- `MagicMissile`、`Speck`：视觉效果
- `Messages`：本地化文本
- `GLog`：日志输出
- `PathFinder`、`BArray`：路径查找
- `Random`：随机数生成
- `Bundle`：序列化支持

### 使用者
- CenserGasTracker（作为Buff附加到英雄）
- 各游戏系统查询averageTurnsUntilGas()

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| GAS_CAT_CHANCES | float[4][3] | 见下表 | 各等级气体类别概率 |
| COMMON_GASSES | HashMap<Class, Float> | 见下表 | 普通气体列表及数量 |
| UNCOMMON_GASSES | HashMap<Class, Float> | 见下表 | 稀有气体列表及数量 |
| RARE_GASSES | HashMap<Class, Float> | 见下表 | 罕见气体列表及数量 |
| MISSILE_VFX | HashMap<Class, Integer> | 见下表 | 气体对应的视觉效果 |

### GAS_CAT_CHANCES 各等级概率
| 等级 | 普通 | 稀有 | 罕见 |
|------|------|------|------|
| 0 | 70% | 25% | 5% |
| 1 | 60% | 30% | 10% |
| 2 | 50% | 35% | 15% |
| 3 | 40% | 40% | 20% |

### COMMON_GASSES 普通气体
| 气体类型 | 数量 |
|----------|------|
| ToxicGas（毒气） | 300 |
| ConfusionGas（混乱气体） | 300 |
| Regrowth（再生气体） | 200 |

### UNCOMMON_GASSES 稀有气体
| 气体类型 | 数量 |
|----------|------|
| StormCloud（风暴云） | 300 |
| SmokeScreen（烟雾屏障） | 300 |
| StenchGas（恶臭气体） | 200 |

### RARE_GASSES 罕见气体
| 气体类型 | 数量 |
|----------|------|
| Inferno（炼狱火） | 300 |
| Blizzard（暴风雪） | 300 |
| CorrosiveGas（腐蚀气体） | 200 |

### 实例字段
无显式实例字段，使用继承字段。

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.CHAOTIC_CENSER;
}
```

### 静态初始化块
在类加载时初始化所有静态HashMap常量，定义气体种类、权重和视觉效果。

## 7. 方法详解

### upgradeEnergyCost()

**可见性**：protected

**是否覆写**：是，覆写自Trinket

**方法职责**：返回升级所需炼金能量

**参数**：无

**返回值**：int，公式为`6 + 2 * level()`

**核心实现逻辑**：
```java
@Override
protected int upgradeEnergyCost() {
    //6 -> 8(14) -> 10(24) -> 12(36)
    return 6+2*level();
}
```

**升级能量消耗**：
| 升级目标等级 | 所需能量 | 累计能量 |
|--------------|----------|----------|
| 0→1 | 6 | 6 |
| 1→2 | 8 | 14 |
| 2→3 | 10 | 24 |

---

### statsDesc()

**可见性**：public

**是否覆写**：是，覆写自Trinket

**方法职责**：返回属性描述文本

**参数**：无

**返回值**：String，包含气体生成频率的描述

**核心实现逻辑**：
```java
@Override
public String statsDesc() {
    if (isIdentified()){
        return Messages.get(this, "stats_desc", averageTurnsUntilGas(buffedLvl()));
    } else {
        return Messages.get(this, "stats_desc", averageTurnsUntilGas(0));
    }
}
```

---

### averageTurnsUntilGas()

**可见性**：public static

**是否覆写**：否

**方法职责**：获取平均气体生成间隔（无参数版本）

**参数**：无

**返回值**：int，调用带参数版本

**核心实现逻辑**：
```java
public static int averageTurnsUntilGas(){
    return averageTurnsUntilGas(trinketLevel(ChaoticCenser.class));
}
```

---

### averageTurnsUntilGas(int level)

**可见性**：public static

**是否覆写**：否

**方法职责**：计算指定等级的平均气体生成回合数

**参数**：
- `level` (int)：饰物等级

**返回值**：int，平均回合数；等级为-1时返回-1

**核心实现逻辑**：
```java
public static int averageTurnsUntilGas(int level){
    if (level <= -1){
        return -1;
    } else {
        return 300 / (level + 1);
    }
}
```

**各级别平均回合数**：
| 等级 | 平均回合数 |
|------|-----------|
| 0 | 300 |
| 1 | 150 |
| 2 | 100 |
| 3 | 75 |

---

### produceGas(Char target)

**可见性**：private static

**是否覆写**：否

**方法职责**：在目标附近生成气体

**参数**：
- `target` (Char)：目标敌人

**返回值**：boolean，成功生成返回true

**前置条件**：
- 目标必须是敌对单位
- 目标必须处于活跃状态
- 目标不是被动状态的Mob

**核心实现逻辑**：
1. 根据等级选择气体类别（普通/稀有/罕见）
2. 从对应类别中随机选择具体气体类型
3. 计算候选位置（英雄视野内2-6格）
4. 优先选择靠近目标的位置
5. 创建GasSpewer buff，延迟一回合喷发
6. 显示预警（红色目标格子和日志消息）

**边界情况**：
- 没有合适的位置时返回false
- 等级超出范围(0-3)时返回false

---

## 8. 内部类详解

### CenserGasTracker

**类型**：extends Buff

**职责**：追踪气体生成计时，定期触发气体喷发

**实例字段**：
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| left | int | Integer.MAX_VALUE | 剩余回合数 |
| safeAreaDelay | int | 100 | 安全区域延迟 |

**主要方法**：

#### act()
**核心逻辑**：
1. 计算平均气体生成回合数
2. 初始化或重置剩余回合数
3. 检查是否有有效目标（通过TargetHealthIndicator）
4. 当剩余回合数<=0时，尝试生成气体
5. 每次tick随机延迟1-3回合
6. 支持Bundle序列化

---

### GasSpewer

**类型**：extends FlavourBuff

**职责**：延迟一回合后实际喷发气体

**实例字段**：
| 字段名 | 类型 | 说明 |
|--------|------|------|
| targetCell | int | 目标格子位置 |
| depth | int | 地牢深度 |
| branch | int | 分支编号 |
| gasType | Class<? extends Blob> | 气体类型 |
| gasQuantity | int | 气体数量 |

**主要方法**：

#### set(int targetCell, Class<? extends Blob> gasType, int gasQuantity)
设置气体喷发参数。

#### act()
**核心逻辑**：
1. 检查是否仍在同一深度和分支
2. 在目标格子生成气体Blob
3. 对于CorrosiveGas，设置强度
4. 播放视觉效果和声音
5. 分离Buff

---

## 9. 对外暴露能力

### 显式 API
| 方法 | 说明 |
|------|------|
| averageTurnsUntilGas() | 获取平均气体生成间隔 |

### 内部辅助方法
| 方法 | 说明 |
|------|------|
| produceGas(Char) | 生成气体（私有） |

## 10. 运行机制与调用链

### 创建时机
通过TrinketCatalyst在炼金釜中制作。

### 调用者
- CenserGasTracker在每次act()时检查并触发
- GameScene或其他系统查询averageTurnsUntilGas()

### 系统流程位置
```
装备ChaoticCenser
    ↓
CenserGasTracker附加到英雄
    ↓
定期检查TargetHealthIndicator中的目标
    ↓
满足条件时调用produceGas()
    ↓
创建GasSpewer（延迟1回合）
    ↓
GasSpewer.act()生成实际气体
```

## 11. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.trinkets.chaoticcenser.name | 混沌香炉 | 名称 |
| items.trinkets.chaoticcenser.spew | 你的香炉将要喷出：%s。 | 预警消息 |
| items.trinkets.chaoticcenser.desc | 在炼金釜内炼制一段时间后... | 描述 |
| items.trinkets.chaoticcenser.typical_stats_desc | 这件饰物通常会每经_%d_±回合... | 典型属性描述 |
| items.trinkets.chaoticcenser.stats_desc | 在当前等级下，这件饰物会每经_%d_±回合... | 属性描述 |

### 依赖的资源
- ItemSpriteSheet.CHAOTIC_CENSER：图标
- Assets.Sounds.GAS：音效
- MagicMissile视觉效果
- Speck粒子效果

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 12. 使用示例

### 基本用法

```java
// 检查混沌香炉是否装备
int level = Trinket.trinketLevel(ChaoticCenser.class);
if (level >= 0) {
    // 获取平均气体生成间隔
    int avgTurns = ChaoticCenser.averageTurnsUntilGas(level);
    // 等级0: 300回合, 等级3: 75回合
}
```

### 游戏内行为

```java
// 在战斗中，香炉会自动：
// 1. 检测玩家选中的敌人（通过TargetHealthIndicator）
// 2. 当计时器到达0时，在敌人附近生成气体
// 3. 显示预警（红色目标格子）让玩家有反应时间
// 4. 一回合后实际喷发气体
```

## 13. 开发注意事项

### 状态依赖
- 气体生成依赖于TargetHealthIndicator显示目标
- 只有在敌人存在时才会生成气体
- 气体不会在安全区域立即生成

### 生命周期耦合
- CenserGasTracker随饰物装备而附加
- GasSpewer是临时Buff，执行后自动分离

### 常见陷阱
1. **误解触发条件**：气体生成需要玩家选中目标，不是完全随机
2. **忽视预警**：玩家应利用预警时间调整位置
3. **友军伤害**：某些气体可能伤害召唤物或盟友

## 14. 修改建议与扩展点

### 适合扩展的位置
- 添加新的气体类型到各个分类
- 调整气体类别权重
- 修改目标选择算法

### 不建议修改的位置
- produceGas()的核心逻辑结构
- GasSpewer的延迟机制

### 重构建议
无当前重构需求。

## 15. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是，extends Trinket
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：否
- [x] 是否明确说明了注意事项与扩展点：是