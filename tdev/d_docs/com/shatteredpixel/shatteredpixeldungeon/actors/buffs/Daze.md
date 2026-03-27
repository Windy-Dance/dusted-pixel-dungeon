# Daze 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Daze.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| **类类型** | class |
| **继承关系** | extends FlavourBuff |
| **代码行数** | 45 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Daze（恍惚）是一个负面状态效果（Debuff），降低目标的精准和闪避。

### 系统定位
位于 buffs 包中，作为 FlavourBuff 的子类，是一个简单的持续型状态效果。

### 不负责什么
- 不直接实现精准/闪避降低逻辑（由 Char 类处理）

## 3. 结构总览

### 主要成员概览
- 常量：DURATION = 5f
- 方法：icon(), iconFadePercent()

### 生命周期/调用时机
由特定攻击或技能创建。

## 4. 继承与协作关系

### 父类提供的能力
FlavourBuff 提供持续时间管理和视觉冷却计算。

### 覆写的方法
| 方法 | 来源 |
|------|------|
| icon() | Buff |
| iconFadePercent() | FlavourBuff |

### 依赖的关键类
- `BuffIndicator`：UI 图标显示
- `Char`：角色属性系统

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| DURATION | float | 5f | 基础持续时间（5回合） |

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
精准/闪避降低效果由 Char 类实现。

## 7. 方法详解

### icon()

**可见性**：public

**是否覆写**：是

**方法职责**：返回 UI 图标标识符。

**返回值**：BuffIndicator.DAZE

### iconFadePercent()

**可见性**：public

**是否覆写**：是

**方法职责**：计算图标淡化百分比。

**核心实现逻辑**：
```java
return Math.max(0, (DURATION - visualcooldown()) / DURATION);
```

## 8. 对外暴露能力

### 显式 API
- `DURATION`：基础持续时间常量

## 9. 运行机制与调用链

### 创建时机
- 特定武器暴击时
- 技能效果

### 系统流程位置
战斗系统中属性计算环节。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| actors.buffs.daze.name | 恍惚 | 状态名称 |
| actors.buffs.daze.desc | 耳畔嗡鸣，视野模糊... | 状态描述 |

### 中文翻译来源
actors_zh.properties 文件。

## 11. 使用示例

### 基本用法
```java
// 施加恍惚效果
Buff.affect(target, Daze.class, Daze.DURATION);
```

## 12. 开发注意事项

### 状态依赖
- 精准/闪避降低由 Char 类实现
- 效果：-50% 精准和闪避

## 13. 修改建议与扩展点

### 不建议修改的位置
- DURATION 常量

## 14. 事实核查清单

- [x] 已覆盖全部字段
- [x] 已覆盖全部方法
- [x] 已检查继承链与覆写关系
- [x] 已核对官方中文翻译（恍惚）
- [x] 无推测性表述
- [x] 示例代码真实可用
- [x] 已标注资源关联
- [x] 已明确说明注意事项与扩展点