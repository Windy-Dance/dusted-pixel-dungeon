# AlarmTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/AlarmTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 54 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`AlarmTrap`（警报陷阱）负责触发后向本层所有怪物发送警报信号，使它们向陷阱位置聚集。

### 系统定位
陷阱系统中的群体控制型陷阱。属于非伤害性但战术意义重大的陷阱类型，能够改变关卡的战斗态势。

### 不负责什么
- 不直接对触发者造成伤害
- 不生成新怪物，仅改变现有怪物的行为

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = RED`，`shape = DOTS`
- **activate() 方法**：触发警报效果

### 主要逻辑块概览
1. **警报广播**：遍历所有怪物并调用 `beckon()`
2. **视觉效果**：播放尖叫声粒子效果（如果在视野内）
3. **音效播放**：播放警报音效

### 生命周期/调用时机
```
创建 → set(pos) → trigger() → activate() → disarm()
```

## 4. 继承与协作关系

### 父类提供的能力
继承自 `Trap`：
- 所有实例字段（`pos`, `visible`, `active` 等）
- `trigger()`, `disarm()`, `reveal()`, `hide()` 方法
- 序列化支持

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `activate()` | 实现警报效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Dungeon.level.mobs` | 获取本层所有怪物 |
| `Mob.beckon(int)` | 使怪物向指定位置移动 |
| `CellEmitter` | 播放粒子效果 |
| `Speck.SCREAM` | 尖叫粒子类型 |
| `GLog` | 显示日志消息 |
| `Sample` | 播放音效 |
| `Messages` | 获取本地化文本 |

### 使用者
- 关卡生成器
- `Level` 类

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | RED (0) | 红色陷阱 |
| `shape` | DOTS (0) | 点状图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = RED;
    shape = DOTS;
}
```

### 初始化注意事项
1. 创建后需调用 `set(int pos)` 设置位置
2. 颜色和形状在初始化块中固定设置

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：触发警报效果，使本层所有怪物向陷阱位置聚集。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 修改所有怪物的目标位置
- 播放音效和粒子效果
- 显示日志消息

**核心实现逻辑**：
```java
@Override
public void activate() {
    // 使所有怪物向陷阱位置聚集
    for (Mob mob : Dungeon.level.mobs) {
        mob.beckon(pos);
    }

    // 视觉效果（仅在玩家视野内）
    if (Dungeon.level.heroFOV[pos]) {
        GLog.w(Messages.get(this, "alarm"));
        CellEmitter.center(pos).start(Speck.factory(Speck.SCREAM), 0.3f, 3);
    }

    // 播放警报音效
    Sample.INSTANCE.play(Assets.Sounds.ALERT);
}
```

**边界情况**：
- 即使没有怪物也会播放音效
- 怪物列表为空时仍正常执行

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发警报效果（覆写自 Trap） |

### 内部辅助方法
无

### 扩展入口
可通过覆写 `activate()` 方法自定义警报行为。

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `Mob.beckon()`：改变怪物目标
- `GLog.w()`：显示警告日志
- `CellEmitter.center()`：播放粒子效果
- `Sample.INSTANCE.play()`：播放音效

### 系统流程位置
```
角色踩中陷阱位置
    ↓
Level 检测并调用 trigger()
    ↓
trigger() 调用 activate()
    ↓
遍历所有怪物，调用 beckon()
    ↓
怪物开始向陷阱位置移动
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.alarmtrap.name` | 警报陷阱 | 陷阱名称 |
| `levels.traps.alarmtrap.alarm` | 陷阱产生的尖锐的警报声在地牢里回荡！ | 警报日志 |
| `levels.traps.alarmtrap.desc` | 这个陷阱看起来有着能造成很大响动的机制。触发它将使本层所有生物对这里产生警觉。 | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.ALERT` | 警报音效 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置警报陷阱
AlarmTrap trap = new AlarmTrap();
trap.set(position);
trap.hide();  // 隐藏陷阱

// 角色踩中后自动触发
// trigger() -> activate() -> 所有怪物向该位置聚集
```

### 检查陷阱状态
```java
Trap trap = Dungeon.level.traps.get(pos);
if (trap instanceof AlarmTrap) {
    // 这是一个警报陷阱
    GLog.i("前方有警报陷阱！");
}
```

## 12. 开发注意事项

### 状态依赖
- 依赖 `Dungeon.level.mobs` 获取怪物列表
- 依赖 `Dungeon.level.heroFOV` 判断是否显示效果

### 生命周期耦合
- 触发后陷阱自动解除（`disarmedByActivation = true`）

### 常见陷阱
- 在空楼层触发时仅播放音效，无实际效果
- 怪物可能已经在陷阱位置附近，效果不明显

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可覆写 `activate()` 实现不同的警报范围
- 可添加怪物筛选逻辑

### 不建议修改的位置
- `beckon()` 调用逻辑是核心行为

### 重构建议
可考虑添加配置选项控制警报范围（如仅限一定距离内的怪物）。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用 levels_zh.properties 中的翻译
- [x] 是否存在任何推测性表述：无，所有信息均来自源码
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已在第 12、13 章详细说明