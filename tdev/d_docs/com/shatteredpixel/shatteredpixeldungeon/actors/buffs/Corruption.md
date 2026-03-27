# Corruption 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Corruption.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| **类类型** | class |
| **继承关系** | extends AllyBuff |
| **代码行数** | 76 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Corruption（腐化）是一个负面状态效果，主要用于将敌人转变为玩家的盟友，同时对其造成持续伤害。

### 系统定位
位于 buffs 包中，作为 AllyBuff 的子类，专门处理腐化效果的盟友转换和持续伤害逻辑。

### 不负责什么
- 不负责腐化效果的具体触发条件
- 不负责腐化来源的伤害计算

## 3. 结构总览

### 主要成员概览
- 字段：buildToDamage
- 方法：act(), fx(), icon(), corruptionHeal()

### 主要逻辑块概览
1. 盟友转换：将敌人转为盟友
2. 持续伤害：每回合造成最大生命值1%
3. 视觉效果：添加变暗状态

### 生命周期/调用时机
由腐化类技能创建并附加到目标。

## 4. 继承与协作关系

### 父类提供的能力
AllyBuff 提供：
- 盟友状态效果基础
- 阵营转换逻辑

### 覆写的方法
| 方法 | 来源 |
|------|------|
| act() | Buff |
| fx(boolean) | Buff |
| icon() | Buff |

### 实现的接口契约
无直接实现的接口。

### 依赖的关键类
- `Char`：目标角色
- `CharSprite`：视觉效果
- `BuffIndicator`：UI 图标显示
- `SoulMark`：灵魂标记

### 使用者
- 腐化类武器附魔
- 特殊技能

## 5. 字段/常量详解

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| buildToDamage | float | 0f | 累积伤害值 |

### 初始化块
```java
{
    type = buffType.NEGATIVE;
    announced = true;
}
```

## 6. 构造与初始化机制

### 构造器
使用默认构造器。

### 初始化注意事项
附加时会调用 corruptionHeal() 进行转换处理。

## 7. 方法详解

### act()

**可见性**：public

**是否覆写**：是

**方法职责**：腐化效果的主逻辑，每回合执行一次。

**核心实现逻辑**：
```java
buildToDamage += target.HT/100f;
int damage = (int)buildToDamage;
buildToDamage -= damage;
if (damage > 0)
    target.damage(damage, this);
spend(TICK);
```
每回合累积目标最大生命值1%的伤害。

### fx(boolean on)

**可见性**：public

**是否覆写**：是

**方法职责**：添加/移除视觉效果。

**核心实现逻辑**：
```java
if (on) target.sprite.add(CharSprite.State.DARKENED);
else if (target.invisible == 0) target.sprite.remove(CharSprite.State.DARKENED);
```

### icon()

**可见性**：public

**是否覆写**：是

**方法职责**：返回 UI 图标标识符。

**返回值**：BuffIndicator.CORRUPT

### corruptionHeal(Char target)

**可见性**：public static

**方法职责**：腐化转换时完全治疗目标。

**核心实现逻辑**：
```java
target.HP = target.HT;
// 显示治疗数字
// 清除所有负面效果（除灵魂标记）
```

## 8. 对外暴露能力

### 显式 API
- `corruptionHeal(Char)`：转换治疗

### 内部辅助方法
无。

### 扩展入口
可覆写 act() 实现不同的伤害逻辑。

## 9. 运行机制与调用链

### 创建时机
- 腐化附魔触发时
- 特定技能使用时

### 调用者
- `Buff.affect(Char, Corruption.class)`
- 腐化附魔

### 被调用者
- `Char.damage()`：造成伤害
- `corruptionHeal()`：转换治疗

### 系统流程位置
战斗系统中状态效果处理环节。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| actors.buffs.corruption.name | 腐化 | 状态名称 |
| actors.buffs.corruption.desc | 腐化魔法会侵入生物体的生命本质... | 状态描述 |

### 中文翻译来源
actors_zh.properties 文件。

## 11. 使用示例

### 基本用法
```java
// 施加腐化效果
Buff.affect(enemy, Corruption.class);
// 敌人自动转换为盟友并完全治疗

// 腐化后目标每回合受最大生命值1%伤害
```

## 12. 开发注意事项

### 状态依赖
- 继承自 AllyBuff 会处理阵营转换
- 伤害累积使用浮点数确保精度

### 生命周期耦合
与目标角色生命周期绑定。

### 常见陷阱
- 腐化会清除大部分负面效果
- 隐身时不会显示变暗效果

## 13. 修改建议与扩展点

### 适合扩展的位置
- act()：可修改伤害公式

### 不建议修改的位置
- corruptionHeal() 的清除逻辑

### 重构建议
无当前重构需求。

## 14. 事实核查清单

- [x] 已覆盖全部字段
- [x] 已覆盖全部方法
- [x] 已检查继承链与覆写关系
- [x] 已核对官方中文翻译（腐化）
- [x] 无推测性表述
- [x] 示例代码真实可用
- [x] 已标注资源关联
- [x] 已明确说明注意事项与扩展点