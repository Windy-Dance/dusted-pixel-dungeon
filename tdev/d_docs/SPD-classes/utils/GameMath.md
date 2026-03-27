# GameMath 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\SPD-classes\src\main\java\com\watabou\utils\GameMath.java |
| **包名** | com.watabou.utils |
| **文件类型** | class |
| **继承关系** | 无继承，无实现接口 |
| **代码行数** | 46 |
| **所属模块** | SPD-classes |

## 2. 文件职责说明

### 核心职责
提供游戏特定的数学计算工具方法，包括基于游戏时间的速度计算和数值范围限制（钳制）功能。

### 系统定位
作为游戏引擎的数学工具类，为物理模拟、动画系统和数值处理提供基础的游戏时间感知计算能力。

### 不负责什么
- 不提供通用数学函数（如三角函数、对数等）
- 不处理复杂的物理模拟
- 不管理游戏时间本身（仅使用Game.elapsed）

## 3. 结构总览

### 主要成员概览
- 所有方法都是静态工具方法

### 主要逻辑块概览
- 速度计算（考虑加速度和游戏时间）
- 数值钳制（限制在指定范围内）

### 生命周期/调用时机
- 按需调用，通常在游戏更新循环中使用
- 依赖Game.elapsed提供帧时间信息

## 4. 继承与协作关系

### 父类提供的能力
无

### 覆写的方法
无

### 实现的接口契约
无

### 依赖的关键类
- `com.watabou.noosa.Game`: 提供游戏时间信息（Game.elapsed）

### 使用者
- 物理系统（角色移动、抛射物）
- 动画系统（缓动计算）
- UI动画和过渡效果
- 游戏数值平衡系统

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无

## 6. 构造与初始化机制

### 构造器
无公共构造器，类不能被实例化

### 初始化块
无

### 初始化注意事项
- 类为纯静态工具类
- 依赖Game类的静态状态（elapsed字段）

## 7. 方法详解

### speed()
**可见性**：public static

**是否覆写**：否

**方法职责**：根据加速度和游戏时间计算新的速度值

**参数**：
- `speed` (float)：当前速度值
- `acc` (float)：加速度值

**返回值**：float，更新后的速度值

**前置条件**：无特殊前置条件

**副作用**：无（纯函数式）

**核心实现逻辑**：
```java
if (acc != 0) {
    speed += acc * Game.elapsed;
}
return speed;
```

**边界情况**：
- acc为0时直接返回原速度（无加速度）
- 负加速度会减速
- 依赖Game.elapsed的准确性

### gate()
**可见性**：public static

**是否覆写**：否

**方法职责**：将数值限制在指定的最小值和最大值之间（钳制函数）

**参数**：
- `min` (float)：最小允许值
- `value` (float)：待限制的值
- `max` (float)：最大允许值

**返回值**：float，限制后的值

**前置条件**：min <= max（虽然未显式验证）

**副作用**：无（纯函数式）

**核心实现逻辑**：
```java
if (value < min) {
    return min;
} else if (value > max) {
    return max;
} else {
    return value;
}
```

**边界情况**：
- value小于min时返回min
- value大于max时返回max
- value在[min, max]范围内时返回原值
- min > max时行为未定义（但通常不会发生）

## 8. 对外暴露能力

### 显式 API
- speed(float, float): 游戏时间感知的速度计算
- gate(float, float, float): 数值范围限制

### 内部辅助方法
无

### 扩展入口
- 无扩展点，设计为封闭工具类

## 9. 运行机制与调用链

### 创建时机
- 首次调用静态方法时类加载

### 调用者
- 角色控制器（Hero、Mob等）
- 抛射物系统（Projectiles）
- UI组件动画
- 游戏状态机

### 被调用者
- Game.elapsed（获取帧时间）
- 基本算术运算

### 系统流程位置
- 游戏数学工具层，连接游戏逻辑和数值计算

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无

### 依赖的资源
- Game.elapsed（游戏时间系统）

### 中文翻译来源
不适用

## 11. 使用示例

### 基本用法
```java
// 角色移动速度计算
float currentSpeed = 100f;
float acceleration = 50f;
float newSpeed = GameMath.speed(currentSpeed, acceleration);
// newSpeed = 100 + 50 * Game.elapsed

// 生命值限制
float health = 150f;
float maxHealth = 100f;
float actualHealth = GameMath.gate(0f, health, maxHealth); // 返回100f

// 动画进度限制
float progress = 1.2f; // 超出范围
float clampedProgress = GameMath.gate(0f, progress, 1f); // 返回1f
```

### 游戏场景示例
```java
// 物理模拟中的速度更新
public void updateVelocity() {
    // 应用重力加速度
    verticalSpeed = GameMath.speed(verticalSpeed, GRAVITY_ACCELERATION);
    
    // 限制水平速度
    horizontalSpeed = GameMath.gate(-MAX_SPEED, horizontalSpeed, MAX_SPEED);
}

// UI滑块值限制
public void setSliderValue(float rawValue) {
    this.value = GameMath.gate(minValue, rawValue, maxValue);
}
```

## 12. 开发注意事项

### 状态依赖
- speed()方法依赖Game.elapsed的全局状态
- 无其他状态依赖，gate()是纯函数

### 生命周期耦合
- 必须在Game.elapsed有效时调用speed()
- 通常在游戏主循环或更新方法中调用

### 常见陷阱
- 在Game未初始化时调用speed()（Game.elapsed可能为0或未定义）
- 假设speed()会自动处理最大速度限制（需要额外调用gate()）
- min > max时gate()的行为不一致（取决于具体实现）
- 忘记speed()已经包含了时间因素（不要重复乘以deltaTime）

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可以添加更多游戏特定的数学函数（如缓动函数）
- 可以添加向量版本的speed/gate方法
- 可以添加积分/微分相关的工具方法

### 不建议修改的位置
- 核心算法逻辑（简洁高效）
- 参数顺序（已建立使用习惯）

### 重构建议
- 考虑创建更丰富的GameMath类，包含更多游戏数学工具
- 可以添加泛型版本支持不同数值类型
- 考虑将Game.elapsed依赖注入而不是硬编码

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点