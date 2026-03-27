# StormTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/StormTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 61 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`StormTrap`（雷暴陷阱）负责触发后在较大范围内释放雷电风暴，对角色造成电击效果。

### 系统定位
陷阱系统中的大范围电击型陷阱。是 `ShockingTrap` 的增强版本。

### 不负责什么
- 不直接造成伤害（由 Electricity Blob 处理）
- 不负责电击的具体效果

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = YELLOW`，`shape = STARS`
- **activate() 方法**：释放雷电风暴效果

### 主要逻辑块概览
1. **范围计算**：计算 2 格范围内的地格
2. **电能生成**：在非实体格子生成电能 Blob
3. **音效播放**：播放闪电音效

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
| `activate()` | 实现雷电风暴效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Electricity` | 电能 Blob 类 |
| `PathFinder.buildDistanceMap()` | 距离计算 |
| `BArray.not()` | 数组取反 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | YELLOW (2) | 黄色陷阱 |
| `shape` | STARS (3) | 星形图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = YELLOW;
    shape = STARS;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：在陷阱周围 2 格范围内释放雷电风暴。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 生成电能 Blob
- 为怪物添加标记
- 播放音效

**核心实现逻辑**：
```java
@Override
public void activate() {
    if (Dungeon.level.heroFOV[pos]) {
        Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
    }

    PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), 2);
    for (int i = 0; i < PathFinder.distance.length; i++) {
        if (PathFinder.distance[i] < Integer.MAX_VALUE) {
            GameScene.add(Blob.seed(i, 20, Electricity.class));
            if (Actor.findChar(i) instanceof Mob) {
                Buff.prolong(Actor.findChar(i), Trap.HazardAssistTracker.class, 
                             HazardAssistTracker.DURATION);
            }
        }
    }
}
```

**边界情况**：
- 只在玩家视野内播放音效
- 电能强度固定为 20

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发电击效果（覆写自 Trap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `Sample.INSTANCE.play()`：播放音效
- `PathFinder.buildDistanceMap()`：计算范围
- `GameScene.add()`：添加电能 Blob

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
播放闪电音效
    ↓
计算 2 格范围内位置
    ↓
每个非实体地格生成电能 Blob
    ↓
电能对角色造成影响
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.stormtrap.name` | 雷暴陷阱 | 陷阱名称 |
| `levels.traps.stormtrap.desc` | 一种储存着庞大电能的机关。触发这个陷阱会让它把能量释放出来形成大范围的雷电风暴。 | 陷阱描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.LIGHTNING` | 闪电音效 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置雷暴陷阱
StormTrap trap = new StormTrap();
trap.set(position);
trap.hide();

// 触发后在 2 格范围内生成电能（强度 20）
```

## 12. 开发注意事项

### 与 ShockingTrap 的区别
| 特性 | StormTrap | ShockingTrap |
|------|-----------|--------------|
| 范围 | 2 格距离 | 3x3 邻域 |
| 电能强度 | 20 | 10 |
| 形状 | STARS | DOTS |

### 状态依赖
- 只在玩家视野内播放音效

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整电能强度参数
- 可修改范围参数

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已详细说明