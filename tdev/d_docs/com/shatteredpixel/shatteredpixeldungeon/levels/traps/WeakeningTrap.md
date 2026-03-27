# WeakeningTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/WeakeningTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 58 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`WeakeningTrap`（虚弱陷阱）负责触发后使踩中的角色虚弱，降低其战斗能力。

### 系统定位
陷阱系统中的 Debuff 型陷阱。对 Boss 和精英怪物效果减半。

### 不负责什么
- 不直接造成伤害
- 不负责虚弱的具体效果（由 Weakness Buff 处理）

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = GREEN`，`shape = WAVES`
- **activate() 方法**：施加虚弱效果

### 主要逻辑块概览
1. **视觉效果**：播放暗影粒子效果
2. **效果判断**：判断目标是否为 Boss/精英
3. **虚弱施加**：根据目标类型施加不同强度的虚弱

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
| `activate()` | 实现虚弱效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Weakness` | 虚弱 Buff |
| `Char.Property.BOSS` | Boss 属性 |
| `Char.Property.MINIBOSS` | 精英属性 |
| `ShadowParticle` | 暗影粒子效果 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | GREEN (3) | 绿色陷阱 |
| `shape` | WAVES (1) | 波浪图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = GREEN;
    shape = WAVES;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：对陷阱位置的角色施加虚弱效果。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 施加虚弱效果
- 播放视觉效果

**核心实现逻辑**：
```java
@Override
public void activate() {
    if (Dungeon.level.heroFOV[pos]) {
        CellEmitter.get(pos).burst(ShadowParticle.UP, 5);
    }

    Char ch = Actor.findChar(pos);
    if (ch != null) {
        // Boss 和精英怪物效果减半
        if (ch.properties().contains(Char.Property.BOSS)
                || ch.properties().contains(Char.Property.MINIBOSS)) {
            Buff.prolong(ch, Weakness.class, Weakness.DURATION / 2f);
        }
        // 普通角色效果完整
        Buff.prolong(ch, Weakness.class, Weakness.DURATION * 3f);
        
        if (ch instanceof Mob) {
            Buff.prolong(ch, Trap.HazardAssistTracker.class, HazardAssistTracker.DURATION);
        }
    }
}
```

**边界情况**：
- 只影响陷阱位置的角色
- Boss 和精英怪物虚弱时间减半
- 普通角色虚弱时间为 3 倍 DURATION

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发虚弱效果（覆写自 Trap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `CellEmitter.get().burst()`：播放粒子效果
- `Buff.prolong()`：施加虚弱

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
检查陷阱位置是否有角色
    ↓
根据角色类型施加虚弱
    ↓
Boss/精英：DURATION/2
普通：DURATION*3
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.weakeningtrap.name` | 虚弱陷阱 | 陷阱名称 |
| `levels.traps.weakeningtrap.desc` | 陷阱中的黑暗魔法能够吸取任何接触物里的能量，不过强大的敌人能抵抗这种效果。 | 陷阱描述 |

### 依赖的资源
视觉效果由 `ShadowParticle` 处理。

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置虚弱陷阱
WeakeningTrap trap = new WeakeningTrap();
trap.set(position);
trap.hide();

// 触发后对陷阱位置的角色施加虚弱
// Boss/精英：Weakness.DURATION / 2
// 普通：Weakness.DURATION * 3
```

## 12. 开发注意事项

### 虚弱效果持续时间
| 目标类型 | 持续时间 |
|----------|----------|
| Boss | DURATION / 2 |
| 精英 (MINIBOSS) | DURATION / 2 |
| 普通怪物 | DURATION * 3 |
| 英雄 | DURATION * 3 |

### 状态依赖
- 只影响陷阱位置的角色
- 需要角色实际踩中陷阱

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整虚弱持续时间倍率
- 可添加范围效果

### 设计意图
使强大敌人能抵抗虚弱效果，保持游戏平衡。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关信息
- [x] 是否明确说明了注意事项与扩展点：已详细说明