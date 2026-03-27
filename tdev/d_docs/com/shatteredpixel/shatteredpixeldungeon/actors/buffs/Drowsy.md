# Drowsy 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Drowsy.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| **类类型** | class |
| **继承关系** | extends FlavourBuff |
| **代码行数** | 60 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Drowsy（困倦）是一个中性状态效果，持续5回合后会使目标进入魔法睡眠状态。

### 系统定位
位于 buffs 包中，作为 FlavourBuff 的子类，是睡眠效果的过渡状态。

### 不负责什么
- 不负责睡眠后的伤害/恢复逻辑
- 不负责视觉效果渲染

## 3. 结构总览

### 主要成员概览
- 常量：DURATION = 5f
- 方法：attachTo(), act(), icon(), iconFadePercent()

### 主要逻辑块概览
1. 免疫检查：睡眠免疫者无法附加
2. 过渡机制：持续时间后施加魔法睡眠

### 生命周期/调用时机
由特定技能或道具创建。

## 4. 继承与协作关系

### 父类提供的能力
FlavourBuff 提供持续时间管理和视觉冷却计算。

### 覆写的方法
| 方法 | 来源 |
|------|------|
| attachTo(Char) | Buff |
| act() | Buff |
| icon() | Buff |
| iconFadePercent() | FlavourBuff |

### 依赖的关键类
- `MagicalSleep`：魔法睡眠状态
- `Sleep`：睡眠状态（免疫检查）
- `BuffIndicator`：UI 图标显示

### 使用者
- 特定技能
- 道具效果

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| DURATION | float | 5f | 基础持续时间（5回合） |

### 初始化块
```java
{
    type = buffType.NEUTRAL;
    announced = true;
}
```

## 6. 构造与初始化机制

### 构造器
使用默认构造器。

### 初始化注意事项
附加时检查目标是否对睡眠免疫。

## 7. 方法详解

### attachTo(Char target)

**可见性**：public

**是否覆写**：是

**方法职责**：附加效果，检查睡眠免疫。

**核心实现逻辑**：
```java
if (!target.isImmune(Sleep.class) && super.attachTo(target)) {
    return true;
}
return false;
```

### act()

**可见性**：public

**是否覆写**：是

**方法职责**：持续时间结束后施加魔法睡眠。

**核心实现逻辑**：
```java
Buff.affect(target, MagicalSleep.class);
return super.act();
```

### icon()

**可见性**：public

**是否覆写**：是

**方法职责**：返回 UI 图标标识符。

**返回值**：BuffIndicator.DROWSY

### iconFadePercent()

**可见性**：public

**是否覆写**：是

**方法职责**：计算图标淡化百分比。

## 8. 对外暴露能力

### 显式 API
- `DURATION`：基础持续时间常量

### 扩展入口
无特殊扩展点。

## 9. 运行机制与调用链

### 创建时机
特定技能或道具使用时。

### 调用者
- `Buff.affect(Char, Drowsy.class)`

### 被调用者
- `MagicalSleep`：施加睡眠

### 系统流程位置
状态效果系统的过渡层。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| actors.buffs.drowsy.name | 困倦 | 状态名称 |
| actors.buffs.drowsy.desc | 魔法睡眠正在慢慢靠近... | 状态描述 |

### 中文翻译来源
actors_zh.properties 文件。

## 11. 使用示例

### 基本用法
```java
// 施加困倦效果
if (Buff.affect(target, Drowsy.class) != null) {
    // 5回合后目标会进入魔法睡眠
}
```

## 12. 开发注意事项

### 状态依赖
- 睡眠免疫者无法获得此状态
- 持续时间结束后自动施加魔法睡眠

### 生命周期耦合
与目标角色生命周期绑定。

### 常见陷阱
- 检查免疫时使用 Sleep.class 而非 MagicalSleep.class

## 13. 修改建议与扩展点

### 不建议修改的位置
- DURATION 常量

### 重构建议
无当前重构需求。

## 14. 事实核查清单

- [x] 已覆盖全部字段
- [x] 已覆盖全部方法
- [x] 已检查继承链与覆写关系
- [x] 已核对官方中文翻译（困倦）
- [x] 无推测性表述
- [x] 示例代码真实可用
- [x] 已标注资源关联
- [x] 已明确说明注意事项与扩展点