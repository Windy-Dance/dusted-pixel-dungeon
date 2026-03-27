# TenguDartTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/TenguDartTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends PoisonDartTrap |
| **代码行数** | 49 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`TenguDartTrap`（天狗飞镖陷阱）是 `PoisonDartTrap` 的特殊变种，用于天狗 Boss 战，不会攻击天狗本人。

### 系统定位
陷阱系统中的特殊毒镖陷阱。可隐藏且无法通过搜索发现。

### 不负责什么
- 不攻击天狗（`canTarget` 返回 false）
- 不使用深度的中毒量计算

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `canBeHidden = true`，`canBeSearched = false`
- **poisonAmount() 方法**：返回固定中毒量
- **canTarget(Char) 方法**：排除天狗

### 主要逻辑块概览
继承自 `PoisonDartTrap`，主要覆写目标选择和中毒量计算。

### 生命周期/调用时机
```
创建 → set(pos) → trigger() → activate() → disarm()
```

## 4. 继承与协作关系

### 父类提供的能力
继承自 `PoisonDartTrap`：
- 所有实例字段和方法
- 目标搜索逻辑
- 飞镖发射动画

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `poisonAmount()` | 返回固定中毒量 |
| `canTarget(Char)` | 排除天狗 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Tengu` | 天狗 Boss |
| `Challenges.STRONGER_BOSSES` | Boss 增强挑战 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `PoisonDartTrap`。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `canBeHidden` | true | 可以被隐藏 |
| `canBeSearched` | false | 无法通过搜索发现 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    canBeHidden = true;
    canBeSearched = false;
}
```

## 7. 方法详解

### poisonAmount()

**可见性**：protected

**是否覆写**：是，覆写自 `PoisonDartTrap`

**方法职责**：返回固定中毒量，根据挑战模式调整。

**参数**：无

**返回值**：int，中毒量

**核心实现逻辑**：
```java
@Override
protected int poisonAmount() {
    if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
        return 15; // 50 damage total，等于深度 10 的毒镖陷阱
    } else {
        return 8; // 17 damage total
    }
}
```

---

### canTarget(Char ch)

**可见性**：protected

**是否覆写**：是，覆写自 `PoisonDartTrap`

**方法职责**：判断是否可以攻击指定目标，排除天狗。

**参数**：
- `ch` (Char)：目标角色

**返回值**：boolean，是否可以攻击

**核心实现逻辑**：
```java
@Override
protected boolean canTarget(Char ch) {
    return !(ch instanceof Tengu);
}
```

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `poisonAmount()` | 计算中毒量（覆写） |
| `canTarget(Char)` | 判断是否可攻击（覆写） |

### 继承的 API
- `activate()`：触发毒镖发射

## 9. 运行机制与调用链

### 创建时机
天狗 Boss 战中生成。

### 调用者
- Boss 战逻辑
- `Trap.trigger()`

### 被调用者
继承自 `PoisonDartTrap`

### 系统流程位置
```
陷阱被触发
    ↓
trigger() 调用 activate()（继承自 PoisonDartTrap）
    ↓
搜索目标时排除天狗
    ↓
发射毒镖
    ↓
造成伤害和中毒
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.tengudarttrap.desc` | 显然，天狗做了充分的战斗准备。这个陷阱会激活一个隐藏的飞镖发射器，向距离最近且不是天狗的单位发射一枚毒镖。\n\n陷阱的制造技巧极其高深，不使用魔法手段的话，触发装置完全无法找到。不过陷阱在刚被布置的短时间内是可以用肉眼观察到的。 | 陷阱描述 |

注：此陷阱没有独立的 `name` 键。

### 依赖的资源
继承自 `PoisonDartTrap`。

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 天狗飞镖陷阱通常在 Boss 战中生成
TenguDartTrap trap = new TenguDartTrap();
trap.set(position);
trap.hide();

// 特性：
// - 可以隐藏（canBeHidden = true）
// - 无法搜索发现（canBeSearched = false）
// - 不攻击天狗
// - 中毒量：普通 8，强化 Boss 挑战 15
```

## 12. 开发注意事项

### 与 PoisonDartTrap 的区别
| 特性 | TenguDartTrap | PoisonDartTrap |
|------|---------------|----------------|
| 可隐藏 | 是 | 否 |
| 可搜索 | 否 | 是 |
| 中毒量 | 固定 8 或 15 | 基于深度计算 |
| 目标筛选 | 排除天狗 | 所有角色 |

### 中毒伤害
- 普通模式：8（总伤害约 17）
- 强化 Boss 挑战：15（总伤害约 50）

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整中毒量
- 可修改目标排除规则

### 设计意图
此陷阱专为天狗 Boss 战设计，确保陷阱只攻击玩家，不会误伤天狗。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `poisonAmount()`, `canTarget()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 PoisonDartTrap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关信息
- [x] 是否明确说明了注意事项与扩展点：已详细说明