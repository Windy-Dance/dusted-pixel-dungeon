# DamageWand 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/wands/DamageWand.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.wands |
| **文件类型** | abstract class |
| **继承关系** | extends Wand |
| **代码行数** | 77 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
DamageWand 是所有直接伤害型法杖的抽象基类，提供：
- 统一的最小/最大伤害计算框架
- 伤害掷骰机制
- 与 `WandEmpower` Buff 的集成（额外伤害加成）
- 统计描述格式化

### 系统定位
位于 Wand 类层次中作为中间抽象层，为直接造成伤害的法杖提供通用伤害计算能力。适用于直接对目标造成伤害的法杖（如魔弹法杖、火焰法杖等），不适用于间接伤害型法杖（如酸蚀法杖、腐化法杖）。

### 不负责什么
- 不实现具体的 `onZap()` 效果（由子类实现）
- 不处理 AOE 伤害分布逻辑
- 不处理条件性伤害（如目标状态影响伤害）

## 3. 结构总览

### 主要成员概览
- **伤害方法**：`min()`, `max()`, `min(int)`, `max(int)`, `damageRoll()`, `damageRoll(int)`
- **覆写方法**：`statsDesc()`, `upgradeStat1(int)`

### 主要逻辑块概览
1. **伤害计算**：`min(lvl)` / `max(lvl)` 抽象方法定义基础伤害范围
2. **伤害掷骰**：`damageRoll(lvl)` 实现随机伤害并应用强化效果

### 生命周期/调用时机
- 由子类在 `onZap()` 中调用 `damageRoll()` 获取伤害值
- 由 `statsDesc()` 调用以显示伤害范围

## 4. 继承与协作关系

### 父类提供的能力
从 `Wand` 继承所有能力，包括：
- 充能系统
- 施法机制
- 鉴定机制
- 诅咒处理

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `statsDesc()` | 格式化显示伤害范围 |
| `upgradeStat1(int level)` | 返回指定等级的伤害范围字符串 |

### 实现的接口契约
定义了以下抽象方法供子类实现：
- `min(int lvl)` - 返回指定等级的最小伤害
- `max(int lvl)` - 返回指定等级的最大伤害

### 依赖的关键类
| 类 | 用途 |
|----|------|
| `WandEmpower` | 法杖强化Buff，提供额外伤害 |
| `Hero` | 伤害掷骰的随机数生成 |
| `Messages` | 国际化文案 |
| `Assets.Sounds` | 音效常量 |

### 使用者
- `WandOfMagicMissile` - 魔弹法杖
- `WandOfFireblast` - 焰浪法杖
- `WandOfFrost` - 冰霜法杖
- `WandOfLightning` - 雷霆法杖
- `WandOfDisintegration` - 解离法杖
- `WandOfLivingEarth` - 灵壤法杖
- `WandOfPrismaticLight` - 棱光法杖
- `WandOfBlastWave` - 冲击波法杖

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无（继承自 Wand）

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器。

### 初始化块
无（继承自 Wand）

### 初始化注意事项
- 子类必须实现 `min(int lvl)` 和 `max(int lvl)` 抽象方法
- 子类通常需要设置 `image` 字段以指定图标

## 7. 方法详解

### min()
**可见性**：public

**是否覆写**：否

**方法职责**：返回当前等级的最小伤害

**返回值**：int，最小伤害值

**核心实现逻辑**：
```java
public int min() {
    return min(buffedLvl());
}
```

---

### min(int lvl)
**可见性**：public abstract

**是否覆写**：否，抽象方法

**方法职责**：返回指定等级的最小伤害

**参数**：
- `lvl` (int)：法杖等级

**返回值**：int，最小伤害值

**说明**：子类必须实现此方法定义伤害公式

---

### max()
**可见性**：public

**是否覆写**：否

**方法职责**：返回当前等级的最大伤害

**返回值**：int，最大伤害值

**核心实现逻辑**：
```java
public int max() {
    return max(buffedLvl());
}
```

---

### max(int lvl)
**可见性**：public abstract

**是否覆写**：否，抽象方法

**方法职责**：返回指定等级的最大伤害

**参数**：
- `lvl` (int)：法杖等级

**返回值**：int，最大伤害值

**说明**：子类必须实现此方法定义伤害公式

---

### damageRoll()
**可见性**：public

**是否覆写**：否

**方法职责**：返回当前等级的随机伤害值

**返回值**：int，随机伤害值

**核心实现逻辑**：
```java
public int damageRoll() {
    return damageRoll(buffedLvl());
}
```

---

### damageRoll(int lvl)
**可见性**：public

**是否覆写**：否

**方法职责**：计算并返回指定等级的随机伤害值，应用 `WandEmpower` 强化效果

**参数**：
- `lvl` (int)：法杖等级

**返回值**：int，随机伤害值（含强化加成）

**前置条件**：`Dungeon.hero` 存在

**副作用**：
- 可能消耗 `WandEmpower` buff 次数
- 播放强击音效

**核心实现逻辑**：
```java
public int damageRoll(int lvl) {
    // 基础伤害掷骰
    int dmg = Hero.heroDamageIntRange(min(lvl), max(lvl));
    
    // 检查并应用法杖强化效果
    WandEmpower emp = Dungeon.hero.buff(WandEmpower.class);
    if (emp != null) {
        dmg += emp.dmgBoost;  // 增加额外伤害
        emp.left--;           // 减少剩余次数
        if (emp.left <= 0) {
            emp.detach();     // 移除已耗尽的buff
        }
        Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG, 0.75f, 1.2f);  // 播放强击音效
    }
    return dmg;
}
```

**边界情况**：
- `WandEmpower` buff 可能不存在（无强化时）
- `emp.left` 减到 0 时自动移除 buff

---

### statsDesc()
**可见性**：public

**是否覆写**：是，覆写自 Wand

**方法职责**：返回格式化的伤害统计描述

**返回值**：String，伤害范围描述

**核心实现逻辑**：
```java
@Override
public String statsDesc() {
    if (levelKnown) {
        return Messages.get(this, "stats_desc", min(), max());
    } else {
        return Messages.get(this, "stats_desc", min(0), max(0));
    }
}
```

**边界情况**：
- 已鉴定时显示实际伤害范围
- 未鉴定时显示 0 级伤害范围

---

### upgradeStat1(int level)
**可见性**：public

**是否覆写**：是，覆写自 Wand

**方法职责**：返回指定等级的伤害范围字符串，用于UI显示

**参数**：
- `level` (int)：等级

**返回值**：String，格式如 "2-8"

**核心实现逻辑**：
```java
@Override
public String upgradeStat1(int level) {
    return min(level) + "-" + max(level);
}
```

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `min()` | 当前等级最小伤害 |
| `max()` | 当前等级最大伤害 |
| `min(int lvl)` | 指定等级最小伤害（抽象） |
| `max(int lvl)` | 指定等级最大伤害（抽象） |
| `damageRoll()` | 当前等级随机伤害 |
| `damageRoll(int lvl)` | 指定等级随机伤害 |

### 内部辅助方法
无

### 扩展入口
- `min(int lvl)` - 必须实现，定义最小伤害公式
- `max(int lvl)` - 必须实现，定义最大伤害公式

## 9. 运行机制与调用链

### 创建时机
由子类实例化，不直接实例化。

### 调用者
- 子类的 `onZap()` 方法调用 `damageRoll()`
- `Wand.statsDesc()` 间接调用 `statsDesc()`

### 被调用者
- `Hero.heroDamageIntRange()` - 伤害随机数生成
- `WandEmpower` - 强化效果处理

### 系统流程位置
```
Wand (法杖基类)
├── DamageWand (伤害法杖基类)
│   ├── WandOfMagicMissile - 魔弹法杖
│   ├── WandOfFireblast - 焰浪法杖
│   ├── WandOfFrost - 冰霜法杖
│   ├── WandOfLightning - 雷霆法杖
│   ├── WandOfDisintegration - 解离法杖
│   ├── WandOfLivingEarth - 灵壤法杖
│   ├── WandOfPrismaticLight - 棱光法杖
│   └── WandOfBlastWave - 冲击波法杖
├── WandOfCorrosion - 酸蚀法杖 (间接伤害)
├── WandOfCorruption - 腐化法杖 (控制型)
├── WandOfRegrowth - 再生法杖 (植物型)
├── WandOfTransfusion - 注魂法杖 (治疗/伤害型)
└── WandOfWarding - 哨卫法杖 (召唤型)
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.wands.damagewand.upgrade_stat_name_1` | 魔法伤害 | 升级属性名称 |

### 依赖的资源
- `Assets.Sounds.HIT_STRONG` - 强化伤害音效

### 中文翻译来源
所有中文翻译来自 `core/src/main/assets/messages/items/items_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 定义一个简单的伤害法杖
public class MyDamageWand extends DamageWand {
    
    @Override
    public int min(int lvl) {
        return 2 + lvl;  // 最小伤害：2 + 等级
    }
    
    @Override
    public int max(int lvl) {
        return 8 + 2 * lvl;  // 最大伤害：8 + 2*等级
    }
    
    @Override
    public void onZap(Ballistica bolt) {
        Char ch = Actor.findChar(bolt.collisionPos);
        if (ch != null) {
            int damage = damageRoll();  // 获取随机伤害
            ch.damage(damage, this);
        }
    }
    
    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        // 近战命中效果
    }
}
```

### 子类实现示例（魔弹法杖）
```java
// WandOfMagicMissile 的伤害公式
@Override
public int min(int lvl) {
    return 2 + lvl;
}

@Override
public int max(int lvl) {
    return 8 + 2 * lvl;
}

// +0 级: 2-8 伤害
// +1 级: 3-10 伤害
// +5 级: 7-18 伤害
```

## 12. 开发注意事项

### 状态依赖
- `damageRoll()` 依赖 `Dungeon.hero` 存在
- 伤害计算依赖 `buffedLvl()` 的正确实现

### 生命周期耦合
- 子类应在 `onZap()` 中调用 `damageRoll()` 获取伤害
- 不应在构造器中调用伤害计算方法（等级未初始化）

### 常见陷阱
1. **忘记实现 min/max(int)**：编译错误，必须实现抽象方法
2. **在非伤害场景调用 damageRoll()**：可能导致意外的 `WandEmpower` 消耗
3. **伤害公式不平衡**：应参考现有法杖的伤害曲线设计

## 13. 修改建议与扩展点

### 适合扩展的位置
- `min(int lvl)` - 必须实现，定义最小伤害公式
- `max(int lvl)` - 必须实现，定义最大伤害公式

### 不建议修改的位置
- `damageRoll(int lvl)` - 核心伤害计算逻辑，修改可能破坏 `WandEmpower` 集成

### 重构建议
- 可考虑将 `WandEmpower` 处理逻辑提取为独立的静态方法，便于测试

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点