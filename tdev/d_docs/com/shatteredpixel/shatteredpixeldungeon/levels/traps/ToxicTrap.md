# ToxicTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/ToxicTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 54 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`ToxicTrap`（毒气陷阱）负责触发后释放有毒气体，对范围内角色造成中毒伤害。

### 系统定位
陷阱系统中的气体型陷阱。释放有毒气体造成持续性伤害。

### 不负责什么
- 不直接造成伤害（由 ToxicGas Blob 处理）
- 不负责中毒的具体效果

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = GREEN`，`shape = GRILL`
- **activate() 方法**：释放有毒气体

### 主要逻辑块概览
1. **气体生成**：生成有毒气体 Blob，强度随深度增加
2. **邻域遍历**：为怪物添加 HazardAssistTracker 标记
3. **音效播放**：播放气体释放音效

### 生命周期/调用时机
```
创建 → set(pos) → trigger() → activate() → disarm()
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
| `activate()` | 实现毒气释放效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `ToxicGas` | 有毒气体 Blob 类 |
| `PathFinder.NEIGHBOURS9` | 9 格邻域 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | GREEN (3) | 绿色陷阱 |
| `shape` | GRILL (2) | 格栅图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = GREEN;
    shape = GRILL;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：释放有毒气体，气体持续时间随深度增加。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 生成有毒气体 Blob
- 为怪物添加标记
- 播放音效

**核心实现逻辑**：
```java
@Override
public void activate() {
    // 生成有毒气体，持续时间 = 300 + 20 * 深度
    GameScene.add(Blob.seed(pos, 300 + 20 * scalingDepth(), ToxicGas.class));
    Sample.INSTANCE.play(Assets.Sounds.GAS);

    for (int i : PathFinder.NEIGHBOURS9) {
        if (Actor.findChar(pos + i) instanceof Mob) {
            Buff.prolong(Actor.findChar(pos + i), Trap.HazardAssistTracker.class, 
                         HazardAssistTracker.DURATION);
        }
    }
}
```

**边界情况**：
- 气体持续时间随深度线性增长
- 深度 1 时持续 320 tick，深度 26 时持续 820 tick

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发毒气释放（覆写自 Trap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `GameScene.add()`：添加气体 Blob
- `Sample.INSTANCE.play()`：播放音效
- `Buff.prolong()`：添加标记

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
生成有毒气体 Blob
    ↓
气体扩散并影响角色
    ↓
角色受到中毒伤害
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.toxictrap.name` | 毒气陷阱 | 陷阱名称 |
| `levels.traps.toxictrap.desc` | 触发这个陷阱将放出一片有毒气体到这附近。 | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.GAS` | 气体释放音效 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置毒气陷阱
ToxicTrap trap = new ToxicTrap();
trap.set(position);
trap.hide();

// 触发后释放有毒气体
// 气体持续时间 = 300 + 20 * 当前深度
```

## 12. 开发注意事项

### 状态依赖
- 使用 `scalingDepth()` 计算气体强度
- 深度影响气体持续时间

### 气体持续时间
| 深度 | 持续时间 (tick) |
|------|-----------------|
| 1 | 320 |
| 10 | 500 |
| 20 | 700 |
| 26 | 820 |

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整气体持续时间公式
- 可覆写 `activate()` 自定义气体类型

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已详细说明