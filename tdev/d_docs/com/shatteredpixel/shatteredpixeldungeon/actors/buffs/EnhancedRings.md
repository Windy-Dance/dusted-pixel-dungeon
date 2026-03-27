# EnhancedRings 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/EnhancedRings.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| **类类型** | class |
| **继承关系** | extends FlavourBuff |
| **代码行数** | 68 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
EnhancedRings（强化戒指）是一个正面状态效果，用于增强角色装备的戒指效果。

### 系统定位
位于 buffs 包中，作为 FlavourBuff 的子类，与天赋系统关联。

### 不负责什么
- 不负责戒指装备逻辑
- 不负责天赋点数管理

## 3. 结构总览

### 主要成员概览
- 方法：attachTo(), detach(), icon(), tintIcon(), iconFadePercent()

### 主要逻辑块概览
1. 属性更新：附加/移除时更新最大生命值
2. 天赋关联：持续时间由天赋决定

### 生命周期/调用时机
由天赋触发创建。

## 4. 继承与协作关系

### 父类提供的能力
FlavourBuff 提供持续时间管理。

### 覆写的方法
| 方法 | 来源 |
|------|------|
| attachTo(Char) | Buff |
| detach() | Buff |
| icon() | Buff |
| tintIcon(Image) | Buff |
| iconFadePercent() | FlavourBuff |

### 依赖的关键类
- `Hero`：英雄类
- `Talent`：天赋系统
- `BuffIndicator`：UI 图标显示

### 使用者
- 强化戒指天赋

## 5. 字段/常量详解

### 初始化块
```java
{
    type = Buff.buffType.POSITIVE;
}
```

## 6. 构造与初始化机制

### 构造器
使用默认构造器。

### 初始化注意事项
持续时间由天赋点数决定（3 × 天赋点数）。

## 7. 方法详解

### attachTo(Char target)

**可见性**：public

**是否覆写**：是

**方法职责**：附加效果并更新属性。

**核心实现逻辑**：
```java
if (super.attachTo(target)){
    if (target == Dungeon.hero) ((Hero) target).updateHT(false);
    return true;
}
```

### detach()

**可见性**：public

**是否覆写**：是

**方法职责**：移除效果并恢复属性。

**核心实现逻辑**：
```java
super.detach();
if (target == Dungeon.hero) ((Hero) target).updateHT(false);
```

### icon()

**可见性**：public

**是否覆写**：是

**方法职责**：返回 UI 图标标识符。

**返回值**：BuffIndicator.UPGRADE

### tintIcon(Image icon)

**可见性**：public

**是否覆写**：是

**方法职责**：为图标着色为绿色。

**核心实现逻辑**：
```java
icon.hardlight(0, 1, 0);
```

### iconFadePercent()

**可见性**：public

**是否覆写**：是

**方法职责**：计算图标淡化百分比。

**核心实现逻辑**：
```java
float max = 3 * Dungeon.hero.pointsInTalent(Talent.ENHANCED_RINGS);
return Math.max(0, (max - visualcooldown()) / max);
```

## 8. 对外暴露能力

### 显式 API
无特殊 API，继承自 FlavourBuff。

## 9. 运行机制与调用链

### 创建时机
强化戒指天赋触发时。

### 调用者
- 天赋系统

### 被调用者
- `Hero.updateHT()`：更新属性

### 系统流程位置
天赋效果系统。

## 10. 资源、配置与国际化关联

### 中文翻译来源
actors_zh.properties 文件。

## 11. 使用示例

### 基本用法
```java
// 由天赋系统自动触发
// 持续时间 = 3 × 天赋点数
Buff.affect(hero, EnhancedRings.class, 3 * points);
```

## 12. 开发注意事项

### 状态依赖
- 依赖天赋点数
- 仅影响英雄

### 常见陷阱
- 移除时必须更新属性

## 13. 修改建议与扩展点

### 不建议修改的位置
- 属性更新逻辑

### 重构建议
无当前重构需求。

## 14. 事实核查清单

- [x] 已覆盖全部字段（无自定义字段）
- [x] 已覆盖全部方法
- [x] 已检查继承链与覆写关系
- [x] 已核对官方中文翻译
- [x] 无推测性表述
- [x] 示例代码真实可用
- [x] 已标注资源关联
- [x] 已明确说明注意事项与扩展点