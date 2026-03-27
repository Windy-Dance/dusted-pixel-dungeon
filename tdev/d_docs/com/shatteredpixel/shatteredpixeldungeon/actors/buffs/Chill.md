# Chill 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Chill.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| **类类型** | class |
| **继承关系** | extends FlavourBuff |
| **代码行数** | 70 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Chill（冻伤）是一个负面状态效果（Debuff），降低目标的移动速度，并与燃烧效果互斥。

### 系统定位
位于 buffs 包中，作为 FlavourBuff 的子类，专门处理冻伤效果的速度减益逻辑。

### 不负责什么
- 不负责直接造成伤害
- 不负责冻结效果（由 Frost 类处理）

## 3. 结构总览

### 主要成员概览
- 常量：DURATION = 10f
- 方法：attachTo(), speedFactor(), icon(), iconFadePercent(), fx(), desc()

### 主要逻辑块概览
1. 互斥处理：附加时移除燃烧效果
2. 速度计算：根据剩余回合数计算速度因子
3. 视觉效果：添加/移除冻伤特效

### 生命周期/调用时机
由游戏逻辑在需要施加冻伤效果时创建并附加到角色。

## 4. 继承与协作关系

### 父类提供的能力
FlavourBuff 提供：
- 基础 Buff 行为
- 持续时间管理
- 视觉冷却时间计算

### 覆写的方法
| 方法 | 来源 |
|------|------|
| attachTo(Char) | Buff |
| icon() | Buff |
| iconFadePercent() | FlavourBuff |
| fx(boolean) | Buff |
| desc() | Buff |

### 实现的接口契约
无直接实现的接口。

### 依赖的关键类
- `Char`：目标角色
- `BuffIndicator`：UI 图标显示
- `CharSprite`：视觉效果
- `Messages`：本地化消息
- `Burning`：互斥的 Buff

### 使用者
- 冰霜类攻击效果
- 冰霜药剂
- 冰霜陷阱

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| DURATION | float | 10f | 基础持续时间（10回合） |

### 实例字段
无自定义实例字段。继承自 FlavourBuff。

### 初始化块
```java
{
    type = buffType.NEGATIVE;
    announced = true;
}
```
设置 Buff 类型为负面效果，并在获得时向玩家显示提示。

## 6. 构造与初始化机制

### 构造器
使用默认构造器。

### 初始化块
设置 type = NEGATIVE（负面效果）和 announced = true（获得时提示）。

### 初始化注意事项
附加时会自动移除目标身上的燃烧效果。

## 7. 方法详解

### attachTo(Char target)

**可见性**：public

**是否覆写**：是，覆写自 Buff

**方法职责**：将冻伤效果附加到目标，同时移除燃烧效果。

**参数**：
- `target` (Char)：目标角色

**返回值**：boolean，是否成功附加

**核心实现逻辑**：
```java
Buff.detach( target, Burning.class );
return super.attachTo(target);
```

**副作用**：移除目标身上的 Burning 效果。

### speedFactor()

**可见性**：public

**是否覆写**：否

**方法职责**：计算当前的速度因子。

**参数**：无

**返回值**：float，速度因子（0.5 ~ 1.0）

**核心实现逻辑**：
```java
return Math.max(0.5f, 1 - cooldown()*0.1f);
```
每剩余1回合，速度降低10%，最低为50%。

### icon()

**可见性**：public

**是否覆写**：是，覆写自 Buff

**方法职责**：返回 UI 图标标识符。

**参数**：无

**返回值**：int，BuffIndicator.FROST 常量

### iconFadePercent()

**可见性**：public

**是否覆写**：是，覆写自 FlavourBuff

**方法职责**：计算图标淡化百分比。

**参数**：无

**返回值**：float，淡化比例（0~1）

**核心实现逻辑**：
```java
return Math.max(0, (DURATION - visualcooldown()) / DURATION);
```

### fx(boolean on)

**可见性**：public

**是否覆写**：是，覆写自 Buff

**方法职责**：添加或移除视觉效果。

**参数**：
- `on` (boolean)：是否开启效果

**返回值**：void

**核心实现逻辑**：
```java
if (on) target.sprite.add(CharSprite.State.CHILLED);
else target.sprite.remove(CharSprite.State.CHILLED);
```

### desc()

**可见性**：public

**是否覆写**：是，覆写自 Buff

**方法职责**：返回状态的描述文本。

**参数**：无

**返回值**：String，本地化描述

**核心实现逻辑**：
```java
return Messages.get(this, "desc", dispTurns(), 
    Messages.decimalFormat("#.##", (1f-speedFactor())*100f));
```

## 8. 对外暴露能力

### 显式 API
- `speedFactor()`：获取当前速度因子
- `DURATION`：基础持续时间常量

### 内部辅助方法
无。

### 扩展入口
可覆写 speedFactor() 实现不同的速度减益公式。

## 9. 运行机制与调用链

### 创建时机
- 冰霜类攻击命中时
- 冰霜药剂使用时
- 冰霜陷阱触发时

### 调用者
- `Buff.affect(Char, Chill.class)`
- 冰霜相关技能和物品

### 被调用者
- `Char.speed()`：使用 speedFactor() 计算速度
- `BuffIndicator`：显示状态图标

### 系统流程位置
战斗系统中速度计算环节。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| actors.buffs.chill.name | 冻伤 | 状态名称 |
| actors.buffs.chill.desc | 还没有完全冻住... | 状态描述 |
| actors.buffs.chill.freezes | %s冻住了！ | 冻结提示 |

### 依赖的资源
- 图标：BuffIndicator.FROST

### 中文翻译来源
actors_zh.properties 文件。

## 11. 使用示例

### 基本用法
```java
// 施加冻伤效果
Buff.affect(target, Chill.class, Chill.DURATION);

// 获取速度因子
Chill chill = target.buff(Chill.class);
if (chill != null) {
    float speed = chill.speedFactor(); // 0.5 ~ 1.0
}
```

### 互斥处理
```java
// 冻伤会自动移除燃烧
// 无需手动处理互斥
Buff.affect(target, Chill.class);
// 此时如果目标有燃烧效果，会被移除
```

## 12. 开发注意事项

### 状态依赖
- 依赖 FlavourBuff 的持续时间管理
- 依赖 CharSprite.State.CHILLED 视觉效果

### 生命周期耦合
与目标角色生命周期绑定。

### 常见陷阱
- 修改速度公式时注意下限 0.5
- 不要与 Frost（冰冻）混淆

## 13. 修改建议与扩展点

### 适合扩展的位置
- speedFactor()：可覆写实现不同速度减益

### 不建议修改的位置
- DURATION 常量：影响游戏平衡

### 重构建议
无当前重构需求。

## 14. 事实核查清单

- [x] 已覆盖全部字段
- [x] 已覆盖全部方法
- [x] 已检查继承链与覆写关系
- [x] 已核对官方中文翻译（冻伤）
- [x] 无推测性表述
- [x] 示例代码真实可用
- [x] 已标注资源关联
- [x] 已明确说明注意事项与扩展点