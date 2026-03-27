# OozeTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/OozeTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 56 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`OozeTrap`（淤泥陷阱）负责触发后在周围释放腐蚀性淤泥，使范围内的角色受到淤泥腐蚀效果。

### 系统定位
陷阱系统中的 Debuff 型陷阱。通过淤泥效果造成持续腐蚀伤害。

### 不负责什么
- 不直接造成伤害（由 Ooze Buff 处理）
- 不对飞行状态的角色生效

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = GREEN`，`shape = DOTS`
- **activate() 方法**：释放淤泥效果

### 主要逻辑块概览
1. **邻域遍历**：遍历 3x3 区域
2. **效果施加**：对非飞行角色施加淤泥效果
3. **视觉效果**：播放黑色溅射效果

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
| `activate()` | 实现淤泥效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Ooze` | 淤泥腐蚀 Buff |
| `Splash` | 溅射视觉效果 |
| `Char.flying` | 飞行状态判断 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | GREEN (3) | 绿色陷阱 |
| `shape` | DOTS (0) | 点状图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = GREEN;
    shape = DOTS;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：在周围 3x3 区域释放淤泥，使范围内的非飞行角色受到腐蚀效果。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 施加淤泥腐蚀效果
- 播放黑色溅射效果

**核心实现逻辑**：
```java
@Override
public void activate() {
    for (int i : PathFinder.NEIGHBOURS9) {
        if (!Dungeon.level.solid[pos + i]) {
            Splash.at(pos + i, 0x000000, 5);
            Char ch = Actor.findChar(pos + i);
            if (ch != null && !ch.flying) {
                Buff.affect(ch, Ooze.class).set(Ooze.DURATION);
                if (ch instanceof Mob) {
                    Buff.prolong(ch, Trap.HazardAssistTracker.class, HazardAssistTracker.DURATION);
                }
            }
        }
    }
}
```

**边界情况**：
- 飞行状态的角色不受影响
- 实心墙壁位置不会有效果

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发淤泥效果（覆写自 Trap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `Splash.at()`：溅射效果
- `Buff.affect()`：施加淤泥

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
遍历 3x3 邻域
    ↓
对非飞行角色施加淤泥效果
    ↓
淤泥造成持续腐蚀伤害
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.oozetrap.name` | 淤泥陷阱 | 陷阱名称 |
| `levels.traps.oozetrap.desc` | 这个陷阱将会洒出腐蚀性的淤泥，它将烧灼你的皮肤直到被洗掉。 | 陷阱描述 |

### 依赖的资源
无特殊音效资源。

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置淤泥陷阱
OozeTrap trap = new OozeTrap();
trap.set(position);
trap.hide();

// 触发后在 3x3 范围内释放淤泥
// 非飞行角色受到 Ooze.DURATION 回合的腐蚀效果
```

## 12. 开发注意事项

### 状态依赖
- 飞行状态的角色不受影响
- 淤泥效果可被水洗掉

### 淤泥效果
- 持续时间：`Ooze.DURATION`
- 造成持续腐蚀伤害
- 可通过站在水中消除

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整淤泥持续时间
- 可修改范围

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关信息
- [x] 是否明确说明了注意事项与扩展点：已详细说明