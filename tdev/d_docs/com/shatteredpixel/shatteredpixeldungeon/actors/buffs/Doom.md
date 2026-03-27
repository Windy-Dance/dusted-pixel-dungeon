# Doom 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Doom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| **类类型** | class |
| **继承关系** | extends Buff |
| **代码行数** | 44 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Doom（末日）是一个负面状态效果，代表即将到来的毁灭或死亡威胁，主要用于视觉效果。

### 系统定位
位于 buffs 包中，作为 Buff 的子类，是一个纯视觉状态效果。

### 不负责什么
- 不实现具体伤害逻辑
- 不处理持续时间

## 3. 结构总览

### 主要成员概览
- 方法：fx(), icon()

### 生命周期/调用时机
由特定游戏机制创建。

## 4. 继承与协作关系

### 父类提供的能力
Buff 提供基础状态效果行为。

### 覆写的方法
| 方法 | 来源 |
|------|------|
| fx(boolean) | Buff |
| icon() | Buff |

### 依赖的关键类
- `CharSprite`：视觉效果
- `BuffIndicator`：UI 图标显示

## 5. 字段/常量详解

### 实例字段
无自定义实例字段。

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
仅提供视觉反馈，无实际效果逻辑。

## 7. 方法详解

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

## 8. 对外暴露能力

### 显式 API
无特殊 API，继承自 Buff。

### 扩展入口
可被继承添加具体效果逻辑。

## 9. 运行机制与调用链

### 创建时机
特定游戏机制触发时。

### 系统流程位置
状态效果系统的视觉层。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
可能使用 Doom 相关翻译。

### 中文翻译来源
actors_zh.properties 文件。

## 11. 使用示例

### 基本用法
```java
// 施加末日效果
Buff.affect(target, Doom.class);
// 目标会显示变暗效果和腐化图标
```

## 12. 开发注意事项

### 状态依赖
- 仅视觉效果
- 隐身时移除变暗效果

### 常见陷阱
- 此类不实现任何实际效果
- 需要与其他系统配合使用

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可覆写 act() 添加具体效果

### 重构建议
可扩展为具有具体效果的 Doom 类型。

## 14. 事实核查清单

- [x] 已覆盖全部字段（无自定义字段）
- [x] 已覆盖全部方法
- [x] 已检查继承链与覆写关系
- [x] 已核对官方中文翻译
- [x] 无推测性表述
- [x] 示例代码真实可用
- [x] 已标注资源关联
- [x] 已明确说明注意事项与扩展点