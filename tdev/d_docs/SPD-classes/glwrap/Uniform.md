# Uniform 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | SPD-classes/src/main/java/com/watabou/glwrap/Uniform.java |
| **包名** | com.watabou.glwrap |
| **文件类型** | class |
| **继承关系** | 无继承，无接口实现 |
| **代码行数** | 65 |
| **所属模块** | SPD-classes |

## 2. 文件职责说明

### 核心职责
Uniform类封装了OpenGL统一变量（Uniform）的操作，提供统一变量位置的管理和各种数据类型的值设置功能。

### 系统定位
作为OpenGL统一变量的包装器，为上层渲染系统提供简化的统一变量设置接口，属于图形渲染核心基础设施层。

### 不负责什么
- 不负责统一变量位置的查询（由Program类处理）
- 不负责着色器程序的管理
- 不负责统一变量的类型验证

## 3. 结构总览

### 主要成员概览
- `location` (int): 统一变量的位置索引

### 主要逻辑块概览
- 位置管理：构造函数和location()方法
- 值设置：value1f()、value2f()、value4f()、valueM3()、valueM4()方法
- 属性兼容方法：enable()、disable()方法（注释：这些方法可能不适用于统一变量）

### 生命周期/调用时机
在着色器程序链接后，通过Program.uniform()方法创建，用于在渲染时设置统一变量的值。

## 4. 继承与协作关系

### 父类提供的能力
无（直接继承自Object）

### 覆写的方法
无

### 实现的接口契约
无

### 依赖的关键类
- `com.badlogic.gdx.Gdx`: 提供OpenGL上下文访问

### 使用者
- Program类：通过uniform()方法创建Uniform实例
- 渲染系统：在绘制几何体时设置统一变量值

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| location | int | 构造函数参数 | OpenGL统一变量的位置索引，由着色器中的uniform声明和glGetUniformLocation获取 |

## 6. 构造与初始化机制

### 构造器
```java
public Uniform(int location)
```
接受一个整数参数作为统一变量的位置索引，直接赋值给location字段。

### 初始化块
无

### 初始化注意事项
- location参数必须是有效的OpenGL统一变量位置（通常由glGetUniformLocation返回）
- 无效的位置可能导致后续OpenGL调用失败或静默忽略

## 7. 方法详解

### location()

**可见性**：public

**是否覆写**：否

**方法职责**：获取当前统一变量的位置索引

**参数**：
无

**返回值**：int，统一变量的位置索引

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return location;
```

**边界情况**：无

### enable()

**可见性**：public

**是否覆写**：否

**方法职责**：启用顶点属性数组（注释：此方法对统一变量无意义）

**参数**：
无

**返回值**：void

**前置条件**：OpenGL上下文必须有效

**副作用**：调用Gdx.gl.glEnableVertexAttribArray(location)，影响OpenGL状态机

**核心实现逻辑**：
调用OpenGL的glEnableVertexAttribArray函数启用指定位置的顶点属性数组

**边界情况**：
- 如果location无效，OpenGL会产生错误
- **注意**：此方法对统一变量没有实际作用，可能是复制Attribute类代码时的错误

### disable()

**可见性**：public

**是否覆写**：否

**方法职责**：禁用顶点属性数组（注释：此方法对统一变量无意义）

**参数**：
无

**返回值**：void

**前置条件**：OpenGL上下文必须有效

**副作用**：调用Gdx.gl.glDisableVertexAttribArray(location)，影响OpenGL状态机

**核心实现逻辑**：
调用OpenGL的glDisableVertexAttribArray函数禁用指定位置的顶点属性数组

**边界情况**：
- 如果location无效，OpenGL会产生错误
- **注意**：此方法对统一变量没有实际作用，可能是复制Attribute类代码时的错误

### value1f(float value)

**可见性**：public

**是否覆写**：否

**方法职责**：设置float类型的统一变量值

**参数**：
- `value` (float)：统一变量的值

**返回值**：void

**前置条件**：OpenGL上下文必须有效，着色器程序必须已激活

**副作用**：调用Gdx.gl.glUniform1f(location, value)，设置统一变量值

**核心实现逻辑**：
调用OpenGL的glUniform1f函数设置指定位置的float统一变量值

**边界情况**：
- 如果location无效（-1），OpenGL会静默忽略
- 如果统一变量类型不匹配，OpenGL会产生错误

### value2f(float v1, float v2)

**可见性**：public

**是否覆写**：否

**方法职责**：设置vec2类型的统一变量值

**参数**：
- `v1` (float)：第一个分量值
- `v2` (float)：第二个分量值

**返回值**：void

**前置条件**：OpenGL上下文必须有效，着色器程序必须已激活

**副作用**：调用Gdx.gl.glUniform2f(location, v1, v2)，设置统一变量值

**核心实现逻辑**：
调用OpenGL的glUniform2f函数设置指定位置的vec2统一变量值

**边界情况**：
- 如果location无效（-1），OpenGL会静默忽略
- 如果统一变量类型不匹配，OpenGL会产生错误

### value4f(float v1, float v2, float v3, float v4)

**可见性**：public

**是否覆写**：否

**方法职责**：设置vec4类型的统一变量值

**参数**：
- `v1` (float)：第一个分量值
- `v2` (float)：第二个分量值
- `v3` (float)：第三个分量值
- `v4` (float)：第四个分量值

**返回值**：void

**前置条件**：OpenGL上下文必须有效，着色器程序必须已激活

**副作用**：调用Gdx.gl.glUniform4f(location, v1, v2, v3, v4)，设置统一变量值

**核心实现逻辑**：
调用OpenGL的glUniform4f函数设置指定位置的vec4统一变量值

**边界情况**：
- 如果location无效（-1），OpenGL会静默忽略
- 如果统一变量类型不匹配，OpenGL会产生错误

### valueM3(float[] value)

**可见性**：public

**是否覆写**：否

**方法职责**：设置mat3类型的统一变量值

**参数**：
- `value` (float[])：包含9个元素的矩阵数组（列主序）

**返回值**：void

**前置条件**：OpenGL上下文必须有效，着色器程序必须已激活，value.length >= 9

**副作用**：调用Gdx.gl.glUniformMatrix3fv(location, 1, false, value, 0)，设置统一变量值

**核心实现逻辑**：
调用OpenGL的glUniformMatrix3fv函数设置指定位置的3x3矩阵统一变量值，使用列主序且不转置

**边界情况**：
- 如果location无效（-1），OpenGL会静默忽略
- 如果value数组长度不足9，会导致ArrayIndexOutOfBoundsException
- 如果统一变量类型不匹配，OpenGL会产生错误

### valueM4(float[] value)

**可见性**：public

**是否覆写**：否

**方法职责**：设置mat4类型的统一变量值

**参数**：
- `value` (float[])：包含16个元素的矩阵数组（列主序）

**返回值**：void

**前置条件**：OpenGL上下文必须有效，着色器程序必须已激活，value.length >= 16

**副作用**：调用Gdx.gl.glUniformMatrix4fv(location, 1, false, value, 0)，设置统一变量值

**核心实现逻辑**：
调用OpenGL的glUniformMatrix4fv函数设置指定位置的4x4矩阵统一 variable值，使用列主序且不转置

**边界情况**：
- 如果location无效（-1），OpenGL会静默忽略
- 如果value数组长度不足16，会导致ArrayIndexOutOfBoundsException
- 如果统一变量类型不匹配，OpenGL会产生错误

## 8. 对外暴露能力

### 显式 API
- `location()`: 获取统一变量位置
- `value1f()`, `value2f()`, `value4f()`: 标量和向量值设置
- `valueM3()`, `valueM4()`: 矩阵值设置

### 内部辅助方法
- `enable()`, `disable()`: 顶点属性相关方法（对统一变量无实际作用）

### 扩展入口
无（final类设计，不可扩展）

## 9. 运行机制与调用链

### 创建时机
在着色器程序链接完成后，通过Program.uniform()方法创建

### 调用者
- Program类的uniform()方法
- 渲染系统的统一变量设置逻辑

### 被调用者
- Gdx.gl.glEnableVertexAttribArray()（无实际作用）
- Gdx.gl.glDisableVertexAttribArray()（无实际作用）
- Gdx.gl.glUniform1f()
- Gdx.gl.glUniform2f()
- Gdx.gl.glUniform4f()
- Gdx.gl.glUniformMatrix3fv()
- Gdx.gl.glUniformMatrix4fv()

### 系统流程位置
位于渲染管线的数据设置阶段，在实际绘制调用之前

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无

### 依赖的资源
无纹理、图标、音效等资源依赖

### 中文翻译来源
不适用（纯技术类，无用户可见文本）

## 11. 使用示例

### 基本用法
```java
// 创建着色器程序后获取统一变量
Program program = Program.create(vertexShader, fragmentShader);
Uniform modelViewMatrix = program.uniform("u_modelViewMatrix");
Uniform color = program.uniform("u_color");
Uniform time = program.uniform("u_time");

// 激活着色器程序
program.use();

// 设置统一变量值
float[] matrixData = new float[16];
// ... 填充matrixData ...
modelViewMatrix.valueM4(matrixData);

color.value4f(1.0f, 0.5f, 0.2f, 1.0f); // RGBA颜色

time.value1f(System.currentTimeMillis() / 1000.0f); // 时间值

// 调用绘制命令
// ... glDrawArrays/glDrawElements ...
```

### 注意事项示例
```java
// 注意：enable()/disable()方法对统一变量无实际作用
// 不应该调用这些方法
Uniform uniform = program.uniform("some_uniform");
// uniform.enable();  // 错误：这会影响顶点属性，不是统一变量
// uniform.disable(); // 错误：同上

// 正确的做法是直接设置值
uniform.value1f(1.0f);
```

## 12. 开发注意事项

### 状态依赖
- 依赖着色器程序已激活（program.use()）
- 依赖OpenGL上下文的有效性
- 统一变量位置必须有效（非-1）

### 生命周期耦合
- 必须在着色器程序链接后创建
- 应在程序删除前停止使用
- 同一Uniform对象可以在多次渲染调用中重复使用

### 常见陷阱
- **无用方法**：enable()和disable()方法对统一变量没有作用，可能是代码复制错误
- **位置验证**：location为-1时所有value*()方法都会被OpenGL静默忽略
- **类型匹配**：统一变量的实际类型必须与调用的方法匹配
- **矩阵格式**：矩阵数组必须是列主序，这与Java的行主序习惯相反
- **程序激活**：必须在program.use()之后设置统一变量

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可添加更多数据类型支持（int、bool、数组等）
- 可添加统一变量存在性检查
- 可移除enable()/disable()方法以避免混淆

### 不建议修改的位置
- 现有的value*()方法签名：已被广泛使用
- 矩阵存储格式：必须保持与OpenGL兼容的列主序

### 重构建议
**重要**：移除enable()和disable()方法，因为它们对统一变量没有实际作用，容易造成混淆。当前实现可能是从Attribute类复制代码时的错误。

可考虑添加：
- 参数验证（检查location != -1）
- 更完整的数据类型支持
- 统一变量缓存机制以提高性能

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点