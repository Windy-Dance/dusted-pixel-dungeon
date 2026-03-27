# Vertigo 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Vertigo.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| **类类型** | public class |
| **继承关系** | extends FlavourBuff |
| **代码行数** | 45 行 |
| **官方中文名** | 眩晕 |

## 2. 文件职责说明

Vertigo 类表示“眩晕”Buff。它是一个极简的负面 FlavourBuff，只定义持续时间、公告、图标和淡出显示。

**核心职责**：
- 定义眩晕持续时间 `10f`
- 标记为负面且可公告的 Buff
- 提供 `VERTIGO` 图标与淡出显示

## 3. 结构总览

```
Vertigo (extends FlavourBuff)
├── 常量
│   └── DURATION: float = 10f
├── 初始化块
│   ├── type = NEGATIVE
│   └── announced = true
└── 方法
    ├── icon(): int
    └── iconFadePercent(): float
```

## 4. 继承与协作关系

### 继承关系图

```mermaid
classDiagram
    Buff <|-- FlavourBuff
    FlavourBuff <|-- Vertigo
```

### 协作关系

| 协作类 | 协作方式 |
|--------|----------|
| **FlavourBuff** | 父类，提供时限型 Buff 行为 |
| **BuffIndicator** | 使用 `VERTIGO` 图标 |

## 5. 字段与常量详解

### 常量

| 常量 | 类型 | 值 | 说明 |
|------|------|----|------|
| `DURATION` | float | `10f` | 默认持续时间 |

### 初始化块

```java
{
    type = buffType.NEGATIVE;
    announced = true;
}
```

## 6. 构造与初始化机制

Vertigo 没有显式构造函数。通常通过：

```java
Buff.affect(target, Vertigo.class, Vertigo.DURATION);
```

施加。

## 7. 方法详解

### icon()

返回 `BuffIndicator.VERTIGO`。

### iconFadePercent()

公式：

```java
Math.max(0, (DURATION - visualcooldown()) / DURATION)
```

## 8. 对外暴露能力

| 方法/成员 | 用途 |
|-----------|------|
| `DURATION` | 标准持续时间 |
| `icon()` | UI 图标显示 |

## 9. 运行机制与调用链

```
Buff.affect(target, Vertigo.class, DURATION)
└── FlavourBuff 生命周期运行
```

## 10. 资源、配置与国际化关联

文件：`core/src/main/assets/messages/actors/actors_zh.properties`

```properties
actors.buffs.vertigo.name=眩晕
actors.buffs.vertigo.desc=如果整个世界都在旋转的话，想走直线会变得十分困难。
```

## 11. 使用示例

```java
Buff.affect(enemy, Vertigo.class, Vertigo.DURATION);
```

## 12. 开发注意事项

- 本类不直接改动移动逻辑，眩晕导致的“随机移动”由外部系统根据 Buff 是否存在来决定。
- 作为极简 FlavourBuff，它没有自定义视觉状态、描述参数或额外副作用。

## 13. 修改建议与扩展点

- 若未来希望眩晕强度可变，可加入字段控制扰动程度。
- 若要提供更明显的视觉反馈，可增加自定义 `fx(boolean)`。

## 14. 事实核查清单

- [x] 已覆盖全部自有方法与常量
- [x] 已验证继承关系 `extends FlavourBuff`
- [x] 已验证 `NEGATIVE` 与 `announced = true`
- [x] 已验证图标与淡出公式
- [x] 已核对官方中文名来自翻译文件
- [x] 无臆测性机制说明
