# CorrosionTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/CorrosionTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 58 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`CorrosionTrap`（腐蚀酸雾陷阱）负责触发后释放强酸性腐蚀气体，对范围内角色造成持续腐蚀伤害。

### 系统定位
陷阱系统中的持续性伤害型陷阱。释放的腐蚀气体具有深度相关的伤害强度。

### 不负责什么
- 不直接造成伤害（由 CorrosiveGas 处理）
- 不负责腐蚀效果的具体计算

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = GREY`，`shape = GRILL`
- **activate() 方法**：释放腐蚀酸雾

### 主要逻辑块概览
1. **气体创建**：创建腐蚀气体 Blob，强度随深度增加
2. **强度设置**：调用 `setStrength()` 设置腐蚀强度
3. **邻域遍历**：为怪物添加 HazardAssistTracker 标记
4. **音效播放**：播放气体释放音效

### 生命周期/调用时机
```
创建 → set(pos) → trigger() → activate() → disarm()
```

## 4. 继承与协作关系

### 父类提供的能力
继承自 `Trap`：
- 所有实例字段
- `trigger()`, `disarm()` 等方法
- `scalingDepth()` 方法用于深度计算

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `activate()` | 实现腐蚀酸雾释放效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Blob.seed()` | 生成气体 Blob |
| `CorrosiveGas` | 腐蚀气体效果类 |
| `PathFinder.NEIGHBOURS9` | 9 格邻域偏移量 |
| `HazardAssistTracker` | 标记怪物受到陷阱伤害 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | GREY (7) | 灰色陷阱 |
| `shape` | GRILL (2) | 格栅图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = GREY;
    shape = GRILL;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：释放腐蚀酸雾，气体持续时间和伤害强度随深度增加。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 生成腐蚀气体 Blob
- 设置气体伤害强度
- 为怪物添加 `HazardAssistTracker` 标记
- 播放音效

**核心实现逻辑**：
```java
@Override
public void activate() {
    // 创建腐蚀气体，持续时间 = 80 + 5 * 深度
    CorrosiveGas corrosiveGas = Blob.seed(pos, 80 + 5 * scalingDepth(), CorrosiveGas.class);
    Sample.INSTANCE.play(Assets.Sounds.GAS);

    // 设置腐蚀强度 = 1 + 深度/4
    corrosiveGas.setStrength(1 + scalingDepth() / 4);

    for (int i : PathFinder.NEIGHBOURS9) {
        if (Actor.findChar(pos + i) instanceof Mob) {
            Buff.prolong(Actor.findChar(pos + i), Trap.HazardAssistTracker.class, 
                         HazardAssistTracker.DURATION);
        }
    }

    GameScene.add(corrosiveGas);
}
```

**边界情况**：
- 持续时间：深度 1 时 85 tick，深度 26 时 210 tick
- 腐蚀强度：深度 1-3 时为 1，深度 4-7 时为 2，依此类推

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发腐蚀酸雾释放（覆写自 Trap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `Blob.seed()`：创建气体
- `corrosiveGas.setStrength()`：设置强度
- `GameScene.add()`：添加气体
- `Buff.prolong()`：添加 Buff

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
创建腐蚀气体并设置强度
    ↓
气体扩散并影响角色
    ↓
角色受到持续腐蚀伤害
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.corrosiontrap.name` | 腐蚀酸雾陷阱 | 陷阱名称 |
| `levels.traps.corrosiontrap.desc` | 触发这个陷阱将在附近释放出一片致命的强酸性雾气。 | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.GAS` | 气体释放音效 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置腐蚀酸雾陷阱
CorrosionTrap trap = new CorrosionTrap();
trap.set(position);
trap.hide();

// 触发后释放腐蚀酸雾
// 持续时间 = 80 + 5 * 深度
// 腐蚀强度 = 1 + 深度/4
```

## 12. 开发注意事项

### 状态依赖
- 使用 `scalingDepth()` 计算气体强度和持续时间
- 深度同时影响持续时间和伤害强度

### 强度计算公式
| 深度范围 | 腐蚀强度 |
|----------|----------|
| 1-3 | 1 |
| 4-7 | 2 |
| 8-11 | 3 |
| 12-15 | 4 |
| 16-19 | 5 |
| 20-23 | 6 |
| 24-26 | 7 |

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整强度计算公式
- 可覆写 `activate()` 自定义气体属性

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已详细说明