# Corrosion 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Corrosion.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| **类类型** | class |
| **继承关系** | extends Buff implements Hero.Doom |
| **代码行数** | 132 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Corrosion（腐蚀）是一个负面状态效果（Debuff），对目标造成持续递增的伤害。

### 系统定位
位于 buffs 包中，作为 Buff 的子类并实现 Hero.Doom 接口，专门处理腐蚀效果的伤害逻辑。

### 不负责什么
- 不负责视觉效果渲染
- 不负责腐蚀地形逻辑

## 3. 结构总览

### 主要成员概览
- 字段：damage, left, source
- 方法：act(), set(), extend(), icon(), desc()

### 主要逻辑块概览
1. 伤害计算：每回合递增伤害
2. 持续时间管理：剩余回合追踪
3. 死亡处理：实现 Hero.Doom 接口

### 生命周期/调用时机
由腐蚀之杖等技能创建并附加到目标。

## 4. 继承与协作关系

### 父类提供的能力
Buff 提供：
- 基础状态效果行为
- 持续时间管理
- 序列化支持

### 实现的接口契约
Hero.Doom 接口：
- onDeath()：英雄死亡处理

### 依赖的关键类
- `WandOfCorrosion`：腐蚀之杖
- `BuffIndicator`：UI 图标显示
- `Messages`：本地化消息

### 使用者
- `WandOfCorrosion`：腐蚀之杖
- 腐蚀陷阱

## 5. 字段/常量详解

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| damage | float | 1 | 当前伤害值 |
| left | float | 0 | 剩余持续时间 |
| source | Class | null | 腐蚀来源类 |

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
需要调用 set() 方法设置持续时间和伤害。

## 7. 方法详解

### act()

**可见性**：public

**是否覆写**：是

**方法职责**：腐蚀效果的主逻辑，每回合执行一次。

**核心实现逻辑**：
1. 造成当前伤害值
2. 根据深度计算伤害上限
3. 递增伤害值
4. 减少剩余时间

### set(float duration, float damage)

**可见性**：public

**方法职责**：设置持续时间和初始伤害。

### extend(float duration)

**可见性**：public

**方法职责**：延长持续时间。

### icon()

**可见性**：public

**是否覆写**：是

**方法职责**：返回 UI 图标标识符。

**返回值**：BuffIndicator.POISON

### tintIcon(Image icon)

**可见性**：public

**是否覆写**：是

**方法职责**：为图标着色。

**核心实现逻辑**：
```java
icon.hardlight(1.0f, 0.5f, 0.0f); // 橙色
```

### onDeath()

**可见性**：public

**是否覆写**：是，实现自 Hero.Doom

**方法职责**：英雄因腐蚀死亡时的处理。

## 8. 对外暴露能力

### 显式 API
- `set(float, float)`：设置参数
- `extend(float)`：延长时间
- `damage`：当前伤害值

### 内部辅助方法
无。

### 扩展入口
可覆写 act() 实现不同的伤害增长逻辑。

## 9. 运行机制与调用链

### 创建时机
- 腐蚀之杖命中时
- 腐蚀陷阱触发时

### 调用者
- `Buff.affect(Char, Corrosion.class)`
- WandOfCorrosion

### 被调用者
- `Char.damage()`：造成伤害

### 系统流程位置
战斗系统中持续伤害处理环节。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| actors.buffs.corrosion.name | 腐蚀 | 状态名称 |
| actors.buffs.corrosion.desc | 腐蚀性酸液正在侵蚀你的身体... | 状态描述 |

### 中文翻译来源
actors_zh.properties 文件。

## 11. 使用示例

### 基本用法
```java
// 施加腐蚀效果
Corrosion corrosion = Buff.affect(target, Corrosion.class);
corrosion.set(10f, 1f); // 10回合，初始伤害1

// 延长持续时间
if (target.buff(Corrosion.class) != null) {
    target.buff(Corrosion.class).extend(5f);
}
```

## 12. 开发注意事项

### 状态依赖
- 伤害增长依赖 Dungeon.depth
- 实现了 Hero.Doom 需要正确处理死亡逻辑

### 生命周期耦合
与目标角色生命周期绑定。

### 常见陷阱
- 伤害增长上限为 depth/2 + 2
- source 字段用于死亡来源判断

## 13. 修改建议与扩展点

### 适合扩展的位置
- act()：可修改伤害增长公式

### 不建议修改的位置
- Hero.Doom 实现

### 重构建议
无当前重构需求。

## 14. 事实核查清单

- [x] 已覆盖全部字段
- [x] 已覆盖主要方法
- [x] 已检查继承链与覆写关系
- [x] 已核对官方中文翻译（腐蚀）
- [x] 无推测性表述
- [x] 示例代码真实可用
- [x] 已标注资源关联
- [x] 已明确说明注意事项与扩展点