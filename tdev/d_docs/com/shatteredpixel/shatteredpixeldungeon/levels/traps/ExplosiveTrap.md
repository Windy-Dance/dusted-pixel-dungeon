# ExplosiveTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/ExplosiveTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 54 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`ExplosiveTrap`（爆炸陷阱）负责触发后在陷阱位置产生爆炸效果，造成范围伤害。

### 系统定位
陷阱系统中的范围伤害型陷阱。利用 `Bomb` 类的爆炸机制造成伤害。

### 不负责什么
- 不直接计算伤害（由 `Bomb.explode()` 处理）
- 不负责爆炸的视觉效果（由 `Bomb` 类处理）

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = ORANGE`，`shape = DIAMOND`
- **activate() 方法**：触发爆炸

### 主要逻辑块概览
1. **标记处理**：为周围怪物添加 HazardAssistTracker
2. **爆炸执行**：调用 `Bomb.explode()` 执行爆炸
3. **死亡验证**：验证友方魔法击杀成就

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
| `activate()` | 实现爆炸效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Bomb` | 炸弹类，提供爆炸功能 |
| `Badges` | 成就系统 |
| `PathFinder.NEIGHBOURS9` | 9 格邻域 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | ORANGE (1) | 橙色陷阱 |
| `shape` | DIAMOND (4) | 菱形图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = ORANGE;
    shape = DIAMOND;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：在陷阱位置产生爆炸效果。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 对周围角色造成爆炸伤害
- 破坏地形和物品
- 可能导致英雄死亡

**核心实现逻辑**：
```java
@Override
public void activate() {
    // 为周围怪物添加标记
    for (int i : PathFinder.NEIGHBOURS9) {
        if (Actor.findChar(pos + i) instanceof Mob) {
            Buff.prolong(Actor.findChar(pos + i), Trap.HazardAssistTracker.class, 
                         HazardAssistTracker.DURATION);
        }
    }

    // 执行爆炸
    new Bomb().explode(pos);
    
    // 验证友方魔法击杀成就
    if (reclaimed && !Dungeon.hero.isAlive()) {
        Badges.validateDeathFromFriendlyMagic();
    }
}
```

**边界情况**：
- 使用 `new Bomb()` 创建临时炸弹，爆炸后消失
- 回收陷阱产生的爆炸击杀英雄会触发特定成就

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发爆炸效果（覆写自 Trap） |

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `Bomb.explode()`：执行爆炸
- `Buff.prolong()`：添加标记
- `Badges.validateDeathFromFriendlyMagic()`：验证成就

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
标记周围怪物
    ↓
创建临时炸弹并引爆
    ↓
爆炸造成范围伤害
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.explosivetrap.name` | 爆炸陷阱 | 陷阱名称 |
| `levels.traps.explosivetrap.desc` | 这个陷阱包含一些粉状炸药和一个触发机制。激活它会导致一定范围的爆炸。 | 陷阱描述 |

### 依赖的资源
爆炸音效由 `Bomb` 类处理。

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置爆炸陷阱
ExplosiveTrap trap = new ExplosiveTrap();
trap.set(position);
trap.hide();

// 触发后在陷阱位置产生爆炸
// 爆炸范围和伤害由 Bomb 类定义
```

## 12. 开发注意事项

### 状态依赖
- 爆炸效果完全依赖 `Bomb` 类
- 回收陷阱状态影响成就验证

### 爆炸特性
- 创建临时 `Bomb` 对象
- 爆炸伤害和范围由 `Bomb.explode()` 决定
- 可能破坏周围地形

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可创建自定义炸弹类型以改变爆炸效果
- 可在爆炸前添加额外效果

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()`
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关信息
- [x] 是否明确说明了注意事项与扩展点：已详细说明