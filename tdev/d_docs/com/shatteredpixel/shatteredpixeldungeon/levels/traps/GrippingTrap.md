# GrippingTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/GrippingTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 60 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`GrippingTrap`（捕猎陷阱）负责触发后对踩中的角色造成出血伤害并使其残废，固定在原地。

### 系统定位
陷阱系统中的控制型陷阱。可多次触发，对地面角色造成持续影响。

### 不负责什么
- 不对飞行状态的角色生效
- 不负责出血和残废的具体效果（由 Buff 类处理）

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = GREY`，`shape = DOTS`，`disarmedByActivation = false`，`avoidsHallways = true`
- **activate() 方法**：施加出血和残废效果

### 主要逻辑块概览
1. **目标检测**：检测陷阱位置的角色
2. **效果施加**：施加出血和残废效果
3. **视觉效果**：播放伤口效果

### 生命周期/调用时机
```
创建 → set(pos) → trigger() → activate()（可多次触发）
```

## 4. 继承与协作关系

### 父类提供的能力
继承自 `Trap`：
- 所有实例字段
- `trigger()`, `disarm()` 等方法
- `scalingDepth()` 方法

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `activate()` | 实现捕猎效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Bleeding` | 出血 Buff |
| `Cripple` | 残废 Buff |
| `Wound` | 伤口视觉效果 |
| `Char.flying` | 飞行状态判断 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | GREY (7) | 灰色陷阱 |
| `shape` | DOTS (0) | 点状图案 |
| `disarmedByActivation` | false | 触发后不解除 |
| `avoidsHallways` | true | 避免放置在走廊 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = GREY;
    shape = DOTS;
    disarmedByActivation = false;
    avoidsHallways = true;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：对陷阱位置的非飞行角色施加出血和残废效果。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 施加出血效果
- 施加残废效果
- 播放伤口视觉效果

**核心实现逻辑**：
```java
@Override
public void activate() {
    Char c = Actor.findChar(pos);

    if (c != null && !c.flying) {
        if (c instanceof Mob) {
            Buff.prolong(c, Trap.HazardAssistTracker.class, HazardAssistTracker.DURATION);
        }
        // 出血伤害：2 + 深度/2 - 护甲减免的一半
        int damage = Math.max(0, (2 + scalingDepth() / 2) - c.drRoll() / 2);
        Buff.affect(c, Bleeding.class).set(damage);
        // 残废效果
        Buff.prolong(c, Cripple.class, Cripple.DURATION);
        Wound.hit(c);
    } else {
        Wound.hit(pos);
    }
}
```

**边界情况**：
- 飞行状态的角色不受影响
- 空位置时播放地面伤口效果
- 伤害会被护甲减免

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发捕猎效果（覆写自 Trap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `Buff.affect()`：施加出血
- `Buff.prolong()`：延长残废
- `Wound.hit()`：播放伤口效果

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
检查角色是否存在且非飞行
    ↓
施加出血和残废效果
    ↓
播放伤口视觉效果
    ↓
陷阱保持激活（可再次触发）
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.grippingtrap.name` | 捕猎陷阱 | 陷阱名称 |
| `levels.traps.grippingtrap.desc` | 触发这个陷阱将使一对钳子合上，伤害受害者并将他们固定在这里。\n\n由于其简单的构造，这种陷阱可被多次激活而不损毁。 | 陷阱描述 |

### 依赖的资源
视觉效果由 `Wound` 类处理。

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置捕猎陷阱
GrippingTrap trap = new GrippingTrap();
trap.set(position);
trap.hide();

// 触发后：
// - 出血伤害 = 2 + 深度/2 - 护甲减免/2
// - 残废效果
// 注意：可多次触发，飞行状态不受影响
```

## 12. 开发注意事项

### 状态依赖
- `disarmedByActivation = false`：陷阱不自动解除
- `avoidsHallways = true`：不生成在走廊
- 飞行状态的角色不受影响

### 伤害计算
- 基础伤害：2
- 深度加成：depth / 2
- 护甲减免：减免值 / 2

### 效果持续时间
| Buff | 持续时间 |
|------|----------|
| 出血 | 基于 depth 计算 |
| 残废 | Cripple.DURATION |

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整出血伤害计算
- 可添加额外效果

### 不建议修改的位置
- `disarmedByActivation = false` 是核心设计特性

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关信息
- [x] 是否明确说明了注意事项与扩展点：已详细说明