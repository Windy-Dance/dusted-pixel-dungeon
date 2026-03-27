# AttackIndicator 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ui/AttackIndicator.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.ui |
| **类类型** | class |
| **继承关系** | extends Tag |
| **代码行数** | 208 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
AttackIndicator 是攻击指示器组件，用于在游戏界面中显示可攻击敌人的快捷操作按钮，并提供直观的视觉反馈和交互功能。

### 系统定位
位于 ui 包中，作为 Tag 的子类，是游戏界面中攻击快捷入口的核心组件。

### 不负责什么
- 不负责攻击伤害计算
- 不负责敌人AI逻辑

## 3. 结构总览

### 主要成员概览
- 常量：ENABLED, DISABLED
- 静态字段：delay, instance
- 实例字段：sprite, lastTarget, candidates, enabled

### 主要逻辑块概览
1. 敌人检测：扫描可攻击目标
2. 目标选择：选择最近的可攻击敌人
3. 视觉更新：显示敌人精灵
4. 交互处理：点击执行攻击

### 生命周期/调用时机
由 GameScene 创建并管理。

## 4. 继承与协作关系

### 父类提供的能力
Tag 提供：
- 圆形按钮基础功能
- 点击交互支持

### 覆写的方法
| 方法 | 来源 |
|------|------|
| createChildren() | Component |
| layout() | Component |
| update() | Component |
| onClick() | Tag |
| destroy() | Component |

### 实现的接口契约
无直接实现的接口。

### 依赖的关键类
- `Mob`：怪物实体
- `CharSprite`：角色精灵
- `Dungeon`：游戏数据
- `DangerIndicator`：危险指示器

### 使用者
- `GameScene`：创建和管理

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| ENABLED | float | 1.0f | 启用状态透明度 |
| DISABLED | float | 0.3f | 禁用状态透明度 |

### 静态字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| delay | float | 消失延迟时间 |
| instance | AttackIndicator | 单例实例 |

### 实例字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| sprite | CharSprite | 目标敌人精灵 |
| lastTarget | Mob | 上一个攻击目标 |
| candidates | ArrayList<Mob> | 可攻击敌人列表 |
| enabled | boolean | 是否启用 |

## 6. 构造与初始化机制

### 构造器
```java
public AttackIndicator()
```
创建实例，使用危险指示器颜色，初始化单例引用。

### 初始化注意事项
默认隐藏，只有检测到可攻击敌人才显示。

## 7. 方法详解

### update()

**可见性**：public synchronized

**是否覆写**：是

**方法职责**：每帧更新状态。

**核心实现逻辑**：
1. 检测可见范围内的敌人
2. 更新目标列表
3. 显示/隐藏按钮
4. 调整透明度

### layout()

**可见性**：protected synchronized

**是否覆写**：是

**方法职责**：计算精灵位置。

### onClick()

**可见性**：protected

**是否覆写**：是

**方法职责**：处理点击事件，执行攻击。

**核心实现逻辑**：
```java
if (enabled && lastTarget != null && Dungeon.hero.ready) {
    Dungeon.hero.handle(lastTarget.pos);
}
```

### target(Mob target)

**可见性**：public static synchronized

**方法职责**：设置攻击目标。

## 8. 对外暴露能力

### 显式 API
- `target(Mob)`：设置目标
- `instance`：单例访问

### 内部辅助方法
- 私有敌人检测方法

### 扩展入口
可覆写目标选择逻辑。

## 9. 运行机制与调用链

### 创建时机
GameScene 初始化时。

### 调用者
- `GameScene`：创建
- 战斗系统：设置目标

### 被调用者
- `Dungeon.hero.handle()`：执行攻击

### 系统流程位置
游戏界面交互层。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
通过 Messages.get() 获取提示文本。

### 中文翻译来源
ui_zh.properties 文件。

## 11. 使用示例

### 基本用法
```java
// 设置攻击目标
AttackIndicator.target(enemy);

// 用户点击按钮后自动执行攻击
```

## 12. 开发注意事项

### 状态依赖
- 依赖 Dungeon.hero.ready 状态
- 依赖敌人可见性

### 生命周期耦合
与游戏场景生命周期绑定。

### 常见陷阱
- 线程安全问题需使用 synchronized
- 延迟机制影响显示/隐藏时机

## 13. 修改建议与扩展点

### 适合扩展的位置
- 目标选择算法

### 不建议修改的位置
- 静态单例模式

### 重构建议
源码注释提到需要重构线程交互。

## 14. 事实核查清单

- [x] 已覆盖全部字段
- [x] 已覆盖主要方法
- [x] 已检查继承链与覆写关系
- [x] 已核对官方中文翻译
- [x] 无推测性表述
- [x] 示例代码真实可用
- [x] 已标注资源关联
- [x] 已明确说明注意事项与扩展点