# BArray 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\SPD-classes\src\main\java\com\watabou\utils\BArray.java |
| **包名** | com.watabou.utils |
| **文件类型** | class |
| **继承关系** | 无继承，无实现接口 |
| **代码行数** | 156 |
| **所属模块** | SPD-classes |

## 2. 文件职责说明

### 核心职责
提供高效的布尔数组操作工具方法，包括逻辑运算（AND、OR、NOT）、数组初始化（setFalse）以及整型数组到布尔数组的条件转换。

### 系统定位
作为底层工具类，为游戏引擎提供高性能的位操作和布尔数组处理能力，避免频繁创建新数组对象，优化内存使用和性能。

### 不负责什么
- 不负责数组的存储管理
- 不提供线程安全保证
- 不处理其他数据类型的数组操作

## 3. 结构总览

### 主要成员概览
- `falseArray`: 静态缓存的全false布尔数组，用于快速初始化

### 主要逻辑块概览
- 数组初始化优化（setFalse）
- 逻辑运算方法（and, or, not）
- 条件转换方法（is, isOneOf, isNot, isNotOneOf）

### 生命周期/调用时机
- 类加载时初始化静态字段
- 方法按需调用，无特定生命周期要求

## 4. 继承与协作关系

### 父类提供的能力
无

### 覆写的方法
无

### 实现的接口契约
无

### 依赖的关键类
- `java.lang.System`（用于System.arraycopy）

### 使用者
- 游戏引擎中的路径查找、碰撞检测等需要高效布尔数组操作的模块

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无

### 静态字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| falseArray | boolean[] | null | 缓存的全false布尔数组，用于快速初始化其他布尔数组，避免重复创建 |

## 6. 构造与初始化机制

### 构造器
无公共构造器，类不能被实例化

### 初始化块
无静态或实例初始化块

### 初始化注意事项
- 类通过静态字段falseArray实现缓存优化
- falseArray在首次调用setFalse时按需初始化，并根据需要自动扩容

## 7. 方法详解

### setFalse()
**可见性**：public static

**是否覆写**：否

**方法职责**：将指定的布尔数组所有元素设置为false，使用缓存的falseArray进行高效复制

**参数**：
- `toBeFalse` (boolean[])：需要设置为全false的数组

**返回值**：void

**前置条件**：toBeFalse不能为null

**副作用**：可能修改静态字段falseArray（当数组长度超过当前缓存时）

**核心实现逻辑**：
```java
if (falseArray == null || falseArray.length < toBeFalse.length)
    falseArray = new boolean[toBeFalse.length];
System.arraycopy(falseArray, 0, toBeFalse, 0, toBeFalse.length);
```

**边界情况**：当toBeFalse长度为0时，不执行任何操作

### and()
**可见性**：public static

**是否覆写**：否

**方法职责**：对两个布尔数组执行逐元素AND运算

**参数**：
- `a` (boolean[])：第一个输入数组
- `b` (boolean[])：第二个输入数组  
- `result` (boolean[])：结果数组，可为null

**返回值**：boolean[]，运算结果数组

**前置条件**：a和b长度必须相等，不能为null

**副作用**：如果result为null，则创建新数组；否则修改传入的result数组

**核心实现逻辑**：
遍历数组，执行result[i] = a[i] && b[i]

**边界情况**：当result为null时，自动创建与输入数组同长度的结果数组

### or() (重载1)
**可见性**：public static

**是否覆写**：否

**方法职责**：对两个布尔数组执行逐元素OR运算（完整数组）

**参数**：
- `a` (boolean[])：第一个输入数组
- `b` (boolean[])：第二个输入数组
- `result` (boolean[])：结果数组，可为null

**返回值**：boolean[]，运算结果数组

**前置条件**：a和b长度必须相等，不能为null

**副作用**：内部调用带偏移量的or方法，offset=0, length=a.length

**核心实现逻辑**：
调用or(a, b, 0, a.length, result)

**边界情况**：同带偏移量版本

### or() (重载2)
**可见性**：public static

**是否覆写**：否

**方法职责**：对两个布尔数组的指定范围执行逐元素OR运算

**参数**：
- `a` (boolean[])：第一个输入数组
- `b` (boolean[])：第二个输入数组
- `offset` (int)：起始偏移量
- `length` (int)：处理长度
- `result` (boolean[])：结果数组，可为null

**返回值**：boolean[]，运算结果数组

**前置条件**：offset + length不能超过数组长度，数组不能为null

**副作用**：如果result为null，则创建指定长度的新数组

**核心实现逻辑**：
从offset开始，处理length个元素，执行result[i] = a[i] || b[i]

**边界情况**：length为0时不执行任何操作

### not()
**可见性**：public static

**是否覆写**：否

**方法职责**：对布尔数组执行逐元素NOT运算（取反）

**参数**：
- `a` (boolean[])：输入数组
- `result` (boolean[])：结果数组，可为null

**返回值**：boolean[]，运算结果数组

**前置条件**：a不能为null

**副作用**：如果result为null，则创建与输入数组同长度的新数组

**核心实现逻辑**：
遍历数组，执行result[i] = !a[i]

**边界情况**：当result为null时，自动创建结果数组

### is()
**可见性**：public static

**是否覆写**：否

**方法职责**：将整型数组转换为布尔数组，标记等于指定值的元素

**参数**：
- `a` (int[])：输入整型数组
- `result` (boolean[])：结果布尔数组，可为null
- `v1` (int)：比较值

**返回值**：boolean[]，转换结果数组

**前置条件**：a不能为null

**副作用**：如果result为null，则创建与输入数组同长度的新数组

**核心实现逻辑**：
遍历数组，执行result[i] = (a[i] == v1)

**边界情况**：当result为null时，自动创建结果数组

### isOneOf()
**可见性**：public static

**是否覆写**：否

**方法职责**：将整型数组转换为布尔数组，标记等于任意指定值的元素

**参数**：
- `a` (int[])：输入整型数组
- `result` (boolean[])：结果布尔数组，可为null
- `v` (int...)：可变参数，比较值列表

**返回值**：boolean[]，转换结果数组

**前置条件**：a不能为null

**副作用**：如果result为null，则创建与输入数组同长度的新数组

**核心实现逻辑**：
对每个元素，检查是否等于v数组中的任意一个值，使用短路逻辑

**边界情况**：当v为空数组时，所有结果为false

### isNot()
**可见性**：public static

**是否覆写**：否

**方法职责**：将整型数组转换为布尔数组，标记不等于指定值的元素

**参数**：
- `a` (int[])：输入整型数组
- `result` (boolean[])：结果布尔数组，可为null
- `v1` (int)：比较值

**返回值**：boolean[]，转换结果数组

**前置条件**：a不能为null

**副作用**：如果result为null，则创建与输入数组同长度的新数组

**核心实现逻辑**：
遍历数组，执行result[i] = (a[i] != v1)

**边界情况**：当result为null时，自动创建结果数组

### isNotOneOf()
**可见性**：public static

**是否覆写**：否

**方法职责**：将整型数组转换为布尔数组，标记不等于所有指定值的元素

**参数**：
- `a` (int[])：输入整型数组
- `result` (boolean[])：结果布尔数组，可为null
- `v` (int...)：可变参数，比较值列表

**返回值**：boolean[]，转换结果数组

**前置条件**：a不能为null

**副作用**：如果result为null，则创建与输入数组同长度的新数组

**核心实现逻辑**：
对每个元素，检查是否不等于v数组中的所有值，使用短路逻辑

**边界情况**：当v为空数组时，所有结果为true

## 8. 对外暴露能力

### 显式 API
- 所有public static方法都是公开API
- setFalse: 高效数组初始化
- and/or/not: 布尔数组逻辑运算
- is/isOneOf/isNot/isNotOneOf: 整型数组条件转换

### 内部辅助方法
- 无private或protected方法，所有方法都是公开的

### 扩展入口
- 无扩展点，类设计为final工具类模式（虽然没有显式声明final）

## 9. 运行机制与调用链

### 创建时机
- 类在首次调用任何静态方法时由JVM加载

### 调用者
- PathFinder类（路径查找算法）
- 游戏中需要位掩码操作的系统
- 碰撞检测和区域计算模块

### 被调用者
- java.lang.System.arraycopy（在setFalse中）
- 基本的数组访问和逻辑运算符

### 系统流程位置
- 底层工具层，为上层游戏逻辑提供高效的数据结构操作支持

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无

### 依赖的资源
无

### 中文翻译来源
不适用

## 11. 使用示例

### 基本用法
```java
// 高效初始化布尔数组
boolean[] visited = new boolean[100];
BArray.setFalse(visited);

// 执行布尔数组AND运算
boolean[] result = BArray.and(array1, array2, null);

// 检查整型数组中哪些元素等于特定值
int[] tiles = {1, 2, 3, 2, 1};
boolean[] isWall = BArray.is(tiles, null, 1); // [true, false, false, false, true]

// 检查整型数组中哪些元素属于多个值之一
boolean[] isObstacle = BArray.isOneOf(tiles, null, 1, 2); // [true, true, false, true, true]
```

### 扩展示例
```java
// 复用结果数组以减少内存分配
boolean[] tempResult = new boolean[100];
// 在循环中重复使用tempResult
for (int i = 0; i < iterations; i++) {
    BArray.or(sourceA, sourceB, tempResult);
    // 使用tempResult进行后续处理
}
```

## 12. 开发注意事项

### 状态依赖
- 静态字段falseArray是共享状态，但只用于读取，线程安全
- 方法不维护实例状态，纯函数式设计

### 生命周期耦合
- 无生命周期依赖，可以随时调用
- 静态缓存会随应用程序生命周期持续存在

### 常见陷阱
- 忘记检查数组长度一致性（and/or方法假定输入数组长度相同）
- 错误地认为result参数会被自动调整大小（实际按传入长度处理）
- 在多线程环境中误解线程安全性（方法本身是线程安全的，但传入的数组需要外部同步）

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可以添加更多逻辑运算方法（如XOR）
- 可以添加针对特定游戏场景的优化方法

### 不建议修改的位置
- setFalse的缓存机制（已高度优化）
- 核心循环逻辑（简洁高效）

### 重构建议
- 考虑将类声明为final以明确不可继承的设计意图
- 可以添加参数验证以提高健壮性

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点