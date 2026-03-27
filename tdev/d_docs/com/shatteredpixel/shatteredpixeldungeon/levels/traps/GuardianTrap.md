# GuardianTrap 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/GuardianTrap.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.traps |
| **文件类型** | class |
| **继承关系** | extends Trap |
| **代码行数** | 117 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
`GuardianTrap`（守卫陷阱）负责触发后召唤守卫并向本层所有生物发出警报。

### 系统定位
陷阱系统中的召唤+警报型陷阱。结合了 `AlarmTrap` 的警报功能和召唤守卫的能力。

### 不负责什么
- 不直接造成伤害（由召唤的守卫造成）
- 不负责守卫的具体行为

## 3. 结构总览

### 主要成员概览
- **初始化块**：设置 `color = RED`，`shape = STARS`
- **activate() 方法**：触发警报并召唤守卫
- **Guardian 内部类**：召唤守卫的定义
- **GuardianSprite 内部类**：守卫的精灵

### 主要逻辑块概览
1. **警报广播**：使所有怪物向陷阱位置聚集
2. **守卫召唤**：根据深度召唤多个守卫
3. **视觉效果**：播放尖叫声粒子效果

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
| `activate()` | 实现警报和召唤效果 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Statue` | 石像基类 |
| `Generator` | 武器生成器 |
| `MeleeWeapon` | 近战武器 |
| `Mob` | 怪物基类 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
继承自 `Trap`，无新增字段。

初始化块设置：
| 字段 | 值 | 说明 |
|------|-----|------|
| `color` | RED (0) | 红色陷阱 |
| `shape` | STARS (3) | 星形图案 |

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器。

### 初始化块
```java
{
    color = RED;
    shape = STARS;
}
```

## 7. 方法详解

### activate()

**可见性**：public

**是否覆写**：是，覆写自 `Trap`

**方法职责**：触发警报并召唤守卫。

**参数**：无

**返回值**：void

**前置条件**：由 `trigger()` 调用

**副作用**：
- 使所有怪物向陷阱位置聚集
- 召唤多个守卫
- 播放警报效果

**核心实现逻辑**：
```java
@Override
public void activate() {
    // 警报效果
    for (Mob mob : Dungeon.level.mobs) {
        mob.beckon(pos);
    }

    if (Dungeon.level.heroFOV[pos]) {
        GLog.w(Messages.get(this, "alarm"));
        CellEmitter.center(pos).start(Speck.factory(Speck.SCREAM), 0.3f, 3);
    }

    Sample.INSTANCE.play(Assets.Sounds.ALERT);

    // 召唤守卫：数量 = (深度 - 5) / 5
    for (int i = 0; i < (scalingDepth() - 5) / 5; i++) {
        Guardian guardian = new Guardian();
        guardian.createWeapon(false);
        guardian.state = guardian.WANDERING;
        guardian.pos = Dungeon.level.randomRespawnCell(guardian);
        if (guardian.pos != -1) {
            GameScene.add(guardian);
            guardian.beckon(Dungeon.hero.pos);
        }
    }
}
```

**边界情况**：
- 深度 5 及以下不召唤守卫
- 无法找到重生位置时不召唤

---

### Guardian 内部类

**类型**：public static class extends Statue

**职责**：定义守卫的行为和属性。

**主要特点**：
- 蓝色精灵
- 无经验值
- 可被召唤
- 持有随机武器

**关键方法**：

#### createWeapon(boolean useDecks)

```java
@Override
public void createWeapon(boolean useDecks) {
    weapon = (MeleeWeapon) Generator.randomUsingDefaults(Generator.Category.WEAPON);
    weapon.cursed = false;
    weapon.enchant(null);
    weapon.level(0);
}
```

#### beckon(int cell)

```java
@Override
public void beckon(int cell) {
    notice();
    if (state != HUNTING) {
        state = WANDERING;
    }
    target = cell;
}
```

---

### GuardianSprite 内部类

**类型**：public static class extends StatueSprite

**职责**：定义守卫的视觉效果。

**特点**：
- 继承石像精灵
- 添加蓝色色调

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate()` | 触发警报和召唤（覆写自 Trap） |
| `Guardian` | 守卫怪物类 |
| `GuardianSprite` | 守卫精灵类 |

## 9. 运行机制与调用链

### 创建时机
关卡生成时随机放置。

### 调用者
- `Level` 移动检测
- `Trap.trigger()`

### 被调用者
- `Mob.beckon()`：使怪物聚集
- `GameScene.add()`：添加守卫
- `CellEmitter.center()`：播放粒子效果

### 系统流程位置
```
角色踩中陷阱
    ↓
trigger() 调用 activate()
    ↓
所有怪物向陷阱位置聚集
    ↓
根据深度召唤守卫
    ↓
守卫追击英雄
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `levels.traps.guardiantrap.name` | 守卫陷阱 | 陷阱名称 |
| `levels.traps.guardiantrap.alarm` | 陷阱产生的尖锐的警报声在地牢里回荡！ | 警报日志 |
| `levels.traps.guardiantrap.desc` | 这个陷阱有着奇怪的魔法机制，它将召唤守卫并向将使本层所有生物对这里产生警觉。 | 陷阱描述 |
| `levels.traps.guardiantrap$guardian.name` | 召唤守卫 | 守卫名称 |
| `levels.traps.guardiantrap$guardian.desc` | 这个蓝色的幻影似乎是地牢中石像守卫的一个召唤映像。 | 守卫描述 |

### 依赖的资源
| 资源类型 | 资源路径 | 用途 |
|----------|----------|------|
| 音效 | `Assets.Sounds.ALERT` | 警报音效 |

### 中文翻译来源
文件：`core/src/main/assets/messages/levels/levels_zh.properties`

## 11. 使用示例

### 基本用法
```java
// 创建并放置守卫陷阱
GuardianTrap trap = new GuardianTrap();
trap.set(position);
trap.hide();

// 触发后：
// - 所有怪物向陷阱位置聚集
// - 召唤 (深度-5)/5 个守卫
// 例如深度 15 召唤 2 个守卫
```

## 12. 开发注意事项

### 状态依赖
- 守卫数量 = (depth - 5) / 5
- 深度 5 及以下不召唤守卫

### 守卫特性
- 持有随机近战武器
- 武器无附魔、无诅咒、等级 0
- 初始状态为游荡
- 会响应召唤向英雄位置移动

### 守卫数量表
| 深度范围 | 守卫数量 |
|----------|----------|
| 1-9 | 0 |
| 10-14 | 1 |
| 15-19 | 2 |
| 20-24 | 3 |
| 25-26 | 4 |

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可调整守卫数量计算公式
- 可修改守卫的武器生成逻辑

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：继承字段已在父类文档中覆盖
- [x] 是否已覆盖全部方法：已覆盖 `activate()` 及内部类方法
- [x] 是否已检查继承链与覆写关系：已说明继承自 Trap
- [x] 是否已核对官方中文翻译：已使用官方翻译
- [x] 是否存在任何推测性表述：无
- [x] 示例代码是否真实可用：示例代码基于实际 API 编写
- [x] 是否遗漏资源/配置/本地化关联：已列出所有相关资源
- [x] 是否明确说明了注意事项与扩展点：已详细说明