# Cripple 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Cripple.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| **类类型** | class |
| **继承关系** | extends FlavourBuff |
| **代码行数** | 44 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Cripple（残废）是一个负面状态效果（Debuff），降低目标的移动能力。

### 系统定位
位于 buffs 包中，作为 FlavourBuff 的子类，是一个简单的持续型状态效果。

### 不负责什么
- 不直接实现移动速度逻辑（由 Char 类处理）
- 不负责视觉效果渲染

## 3. 结构总览

### 主要成员概览
- 常量：DURATION = 10f
- 方法：icon(), iconFadePercent()

### 主要逻辑块概览
仅定义基础属性，具体效果由其他系统实现。

### 生命周期/调用时机
由特定攻击、陷阱或道具创建。

## 4. 继承与协作关系

### 父类提供的能力
FlavourBuff 提供：
- 基础 Buff 行为
- 持续时间管理
- 视觉冷却时间计算

### 覆写的方法
| 方法 | 来源 |
|------|------|
| icon() | Buff |
| iconFadePercent() | FlavourBuff |

### 实现的接口契约
无直接实现的接口。

### 依赖的关键类
- `BuffIndicator`：UI 图标显示
- `Char`：角色移动系统

### 使用者
- 陷阱
- 特定攻击
- 道具

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

## 6. 构造与初始化机制

### 构造器
使用默认构造器。

### 初始化注意事项
移动减益效果由 Char.crippleFactor() 方法实现。

## 7. 方法详解

### icon()

**可见性**：public

**是否覆写**：是

**方法职责**：返回 UI 图标标识符。

**返回值**：BuffIndicator.CRIPPLE

### iconFadePercent()

**可见性**：public

**是否覆写**：是

**方法职责**：计算图标淡化百分比。

**返回值**：float，淡化比例

**核心实现逻辑**：
```java
return Math.max(0, (DURATION - visualcooldown()) / DURATION);
```

## 8. 对外暴露能力

### 显式 API
- `DURATION`：基础持续时间常量

### 内部辅助方法
无。

### 扩展入口
无特殊扩展点。

## 9. 运行机制与调用链

### 创建时机
- 陷阱触发时
- 特定攻击命中时

### 调用者
- `Buff.affect(Char, Cripple.class)`
- Char 类的移动计算

### 被调用者
- `Char.crippleFactor()`：检查残废状态

### 系统流程位置
战斗系统中移动计算环节。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| actors.buffs.cripple.name | 残废 | 状态名称 |
| actors.buffs.cripple.desc | 你很确定自己的腿不该折成那样... | 状态描述 |

### 中文翻译来源
actors_zh.properties 文件。

## 11. 使用示例

### 基本用法
```java
// 施加残废效果
Buff.affect(target, Cripple.class, Cripple.DURATION);

// 检查是否有残废
if (target.buff(Cripple.class) != null) {
    // 目标处于残废状态
}
```

## 12. 开发注意事项

### 状态依赖
- 移动减益由 Char 类实现
- 持续时间由 FlavourBuff 管理

### 生命周期耦合
与目标角色生命周期绑定。

### 常见陷阱
- 此类仅标记状态，实际效果在其他地方实现

## 13. 修改建议与扩展点

### 适合扩展的位置
无特殊扩展需求。

### 不建议修改的位置
- DURATION 常量

### 重构建议
无当前重构需求。

## 14. 事实核查清单

- [x] 已覆盖全部字段
- [x] 已覆盖全部方法
- [x] 已检查继承链与覆写关系
- [x] 已核对官方中文翻译（残废）
- [x] 无推测性表述
- [x] 示例代码真实可用
- [x] 已标注资源关联
- [x] 已明确说明注意事项与扩展点