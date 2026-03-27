# WarpingTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/WarpingTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends TeleportationTrap |
| **代码行数** | 48 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`WarpingTrap`（扭曲陷阱）负责触发后传送周围的角色和物品，并使英雄遗忘本层的地图和物品位置。

### 系统定位
陷阱系统中的增强传送型陷阱。继承自 `TeleportationTrap`，额外添加遗忘地图效果。

### 不负责什么
- 不直接造成伤害
- 不负责传送的具体实现（由父类处理）

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = TEAL`，`shape = STARS`
- **activate() 方法**：传送并遗忘地图

### 主要逻辑块概览
1. **地图遗忘**：清除英雄的地图记忆
2. **传送效果**：调用父类的传送逻辑
3. **视野更新**：更新游戏视野

### 生命周期/调用时机
```
创建 → set(pos) → trigger() → activate() → disarm()
```

## 4. 继承与协作关系

### 父类提供的能力
继承自 `TeleportationTrap`：
- 所有实例字段和方法
- 传送角色和物品的逻辑

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `activate()` | 添加遗忘地图效果后调用父类方法 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `BArray.setFalse()` | 清除数组 |
| `Dungeon.level.visited` | 已访问格子标记 |
| `Dungeon.level.mapped` | 已映射格子标记 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `TeleportationTrap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | TEAL (4) | 青色陷阱 |
| `shape` | STARS (3) | 星形图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = TEAL;
    shape = STARS;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `TeleportationTrap`

**方法职责**：清除地图记忆后执行传送效果。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 清除英雄的地图记忆
- 清除物品位置记忆
- 传送范围内的角色和物品（继承自父类）

**核心实现逻辑**：
```java
@Override
public void activate() {
    // 如果英雄在陷阱附近，清除地图记忆
    if (Dungeon.level.distance(Dungeon.hero.pos, pos) <= 1) {
        BArray.setFalse(Dungeon.level.visited);
        BArray.setFalse(Dungeon.level.mapped);
    }

    // 调用父类的传送逻辑
    super.activate();

    // 更新视野
    GameScene.updateFog();
    Dungeon.observe();
}
```

**边界情况**：
- 只有英雄在陷阱 1 格范围内才清除地图
- 地图遗忘影响整个楼层

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发扭曲传送效果（覆写自 TeleportationTrap） |

### 继承的 API
- `TeleportationTrap.activate()`：传送功能

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `BArray.setFalse()`：清除地图记忆
- `super.activate()`：调用父类传送逻辑
- `GameScene.updateFog()`：更新视野
- `Dungeon.observe()`：重新观察

### 系统流程位置
```
英雄踩中陷阱（距离 <= 1）
    ↓
trigger() 调用 activate()
    ↓
清除地图记忆（visited 和 mapped）
    ↓
调用父类传送逻辑
    ↓
传送范围内的角色和物品
    ↓
更新视野
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.warpingtrap.name` | 扭曲陷阱 | 陷阱名称 |
| `levels.traps.warpingtrap.desc` | 这种陷阱和传送陷阱颇为相似，不过它还会导致英雄忘却本层的地图与各种东西的方位！ | 陷阱描述 |

### 依赖的资源
继承自 `TeleportationTrap`。

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置扭曲陷阱
WarpingTrap trap = new WarpingTrap();
trap.set(position);
trap.hide();

// 触发后：
// 1. 如果英雄在 1 格范围内，清除地图记忆
// 2. 传送 3x3 范围内的角色和物品
// 3. 更新视野
```

## 12. 开发注意事项

### 与 TeleportationTrap 的区别
| 特性 | WarpingTrap | TeleportationTrap |
|------|-------------|-------------------|
| 地图遗忘 | 是（英雄距离 <= 1） | 否 |
| 形状 | STARS | DOTS |

### 遗忘效果
- 清除 `Dungeon.level.visited`：已访问的格子
- 清除 `Dungeon.level.mapped`：已映射的格子
- 效果范围：整个楼层

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整遗忘效果的触发距离
- 可添加额外的遗忘效果

### 设计意图
增加传送陷阱的危险性，使玩家在探索完成后再次面临未知。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 TeleportationTrap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关信息
- [x] 是否明确说明了注意事项与扩展点：已详细说明