# Fury 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Fury.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| **类类型** | public class |
| **继承关系** | extends Buff |
| **代码行数** | 50 行 |
| **官方中文名** | 愤怒 |

## 2. 文件职责说明

Fury 类表示“愤怒”Buff。它是一个条件维持型正面 Buff：只要目标生命值不高于最大生命值的一定比例，就继续存在；一旦血量高于阈值就自动移除。

**核心职责**：
- 定义生命阈值 `LEVEL = 0.5f`
- 标记为正面且会公告的 Buff
- 每回合检查目标当前生命值
- 在血量恢复过高时自动移除

## 3. 结构总览

```
Fury (extends Buff)
├── 常量
│   └── LEVEL: float = 0.5f
├── 初始化块
│   ├── type = POSITIVE
│   └── announced = true
└── 方法
    ├── act(): boolean
    └── icon(): int
```

## 4. 继承与协作关系

### 继承关系图

```mermaid
classDiagram
    Buff <|-- Fury
```

### 协作关系

| 协作类 | 协作方式 |
|--------|----------|
| **Buff** | 父类，提供附着与行动调度 |
| **BuffIndicator** | 提供 `FURY` 图标 |

## 5. 字段与常量详解

### 常量

| 常量 | 类型 | 值 | 说明 |
|------|------|----|------|
| `LEVEL` | float | `0.5f` | 生命值阈值比例；用于 `target.HT * LEVEL` 判定 |

### 初始化块

```java
{
    type = buffType.POSITIVE;
    announced = true;
}
```

## 6. 构造与初始化机制

Fury 没有显式构造函数。常见创建方式：

```java
Buff.affect(target, Fury.class);
```

## 7. 方法详解

### act()

```java
@Override
public boolean act()
```

执行逻辑：
1. 若 `target.HP > target.HT * LEVEL`，调用 `detach()`
2. `spend(TICK)`
3. 返回 `true`

也就是说，Fury 只在目标血量不高于 50% 最大生命值时持续存在。

### icon()

返回 `BuffIndicator.FURY`。

## 8. 对外暴露能力

| 方法/成员 | 用途 |
|-----------|------|
| `LEVEL` | 判断 Fury 是否应继续存在 |
| `icon()` | UI 图标显示 |

## 9. 运行机制与调用链

```
Buff.affect(target, Fury.class)
└── Fury.act()
    ├── 检查 target.HP > target.HT * 0.5f
    ├── [是] detach()
    └── spend(TICK)
```

## 10. 资源、配置与国际化关联

文件：`core/src/main/assets/messages/actors/actors_zh.properties`

```properties
actors.buffs.fury.name=愤怒
actors.buffs.fury.desc=你非常暴怒，很明显敌人并不喜欢这样的你。
```

## 11. 使用示例

```java
Buff.affect(hero, Fury.class);

if (hero.HP <= hero.HT * Fury.LEVEL) {
    // Fury 可以继续维持
}
```

## 12. 开发注意事项

- 本类只负责存在条件，不直接在源码里写伤害加成逻辑；实际战斗加成由其他战斗计算流程读取该 Buff 是否存在。
- 判定条件使用的是 `>`，因此当血量正好等于 `HT * 0.5f` 时，Buff 仍可继续存在。

## 13. 修改建议与扩展点

- 若要支持不同职业或不同来源的 Fury 阈值，可把 `LEVEL` 改成实例字段。
- 若后续需要不同图标或描述，可按来源拆分成多个 Fury 子类。

## 14. 事实核查清单

- [x] 已覆盖全部自有方法与常量
- [x] 已验证继承关系 `extends Buff`
- [x] 已验证 `POSITIVE` 与 `announced = true`
- [x] 已验证血量阈值判定逻辑
- [x] 已验证图标为 `BuffIndicator.FURY`
- [x] 已核对官方中文名来自翻译文件
- [x] 无臆测性机制说明
