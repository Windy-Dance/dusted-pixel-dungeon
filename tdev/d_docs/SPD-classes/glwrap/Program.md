# Program 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | SPD-classes/src/main/java/com/watabou/glwrap/Program.java |
| **包名** | com.watabou.glwrap |
| **文件类型** | class |
| **继承关系** | 无继承，无接口实现 |
| **代码行数** | 80 |
| **所属模块** | SPD-classes |

## 2. 文件职责说明

### 核心职责
Program类封装了OpenGL着色器程序（Shader Program）的操作，提供程序的创建、着色器附加、链接、属性/统一变量获取和使用功能。

### 系统定位
作为OpenGL着色器程序的包装器，为上层渲染系统提供简化的着色器管理接口，属于图形渲染核心基础设施层。

### 不负责什么
- 不负责着色器源代码的编译（由Shader类处理）
- 不负责着色器源代码的加载和管理
- 不负责渲染管线的具体执行

## 3. 结构总览

### 主要成员概览
- `handle` (int): OpenGL着色器程序对象的ID

### 主要逻辑块概览
- 构造与销毁：创建和删除着色器程序
- 着色器管理：附加着色器和链接程序
- 接口获取：获取顶点属性和统一变量的位置
- 程序激活：设置当前使用的着色器程序

### 生命周期/调用时机
在需要使用自定义着色器进行渲染时创建，通常在游戏初始化或资源加载阶段。

## 4. 继承与协作关系

### 父类提供的能力
无（直接继承自Object）

### 覆写的方法
无

### 实现的接口契约
无

### 依赖的关键类
- `com.badlogic.gdx.Gdx`: 提供OpenGL上下文访问
- `com.badlogic.gdx.utils.BufferUtils`: 提供IntBuffer创建
- `com.watabou.noosa.Game`: 用于错误报告
- `com.watabou.glwrap.Shader`: 着色器对象
- `com.watabou.glwrap.Attribute`: 顶点属性包装器
- `com.watabou.glwrap.Uniform`: 统一变量包装器

### 使用者
- 渲染系统：创建和使用各种着色器程序
- 特效系统：使用自定义着色器实现视觉效果
- UI系统：使用专用着色器渲染界面元素

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| handle | int | glCreateProgram()结果 | OpenGL着色器程序对象ID |

## 6. 构造与初始化机制

### 构造器
```java
public Program()
```
调用Gdx.gl.glCreateProgram()创建新的着色器程序对象，并存储其ID到handle字段。

### 初始化块
无

### 初始化注意事项
- 构造后程序对象为空，需要附加着色器并链接才能使用
- 程序对象创建失败会返回0，但GDX通常会抛出异常

## 7. 方法详解

### handle()

**可见性**：public

**是否覆写**：否

**方法职责**：获取底层OpenGL着色器程序对象的ID

**参数**：
无

**返回值**：int，OpenGL程序对象ID

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return handle;
```

**边界情况**：无

### attach(Shader shader)

**可见性**：public

**是否覆写**：否

**方法职责**：将着色器对象附加到程序对象

**参数**：
- `shader` (Shader)：要附加的着色器对象

**返回值**：void

**前置条件**：程序和着色器都必须有效

**副作用**：调用Gdx.gl.glAttachShader，修改程序对象状态

**核心实现逻辑**：
调用OpenGL的glAttachShader函数，将指定着色器附加到程序

**边界情况**：
- 同一个着色器不能重复附加到同一个程序
- 无效的着色器或程序会导致OpenGL错误

### link()

**可见性**：public

**是否覆写**：否

**方法职责**：链接程序对象中的所有着色器

**参数**：
无

**返回值**：void

**前置条件**：程序必须包含至少一个顶点着色器和一个片段着色器

**副作用**：调用Gdx.gl.glLinkProgram和相关查询函数，可能抛出运行时异常

**核心实现逻辑**：
1. 调用glLinkProgram链接程序
2. 查询链接状态（GL_LINK_STATUS）
3. 如果链接失败，通过Game.reportException报告错误并包含错误日志

**边界情况**：
- 链接失败会通过Game.reportException报告异常
- 成功链接后的程序可以被使用

### attribute(String name)

**可见性**：public

**是否覆写**：否

**方法职责**：获取指定名称的顶点属性位置，并包装为Attribute对象

**参数**：
- `name` (String)：顶点属性的名称（与着色器中声明的名称匹配）

**返回值**：Attribute，包装了属性位置的Attribute对象

**前置条件**：程序必须已成功链接

**副作用**：调用Gdx.gl.glGetAttribLocation，查询属性位置

**核心实现逻辑**：
调用OpenGL的glGetAttribLocation函数获取属性位置，然后创建Attribute对象

**边界情况**：
- 如果属性名不存在，返回-1（Attribute.location()将返回-1）
- 属性名区分大小写

### uniform(String name)

**可见性**：public

**是否覆写**：否

**方法职责**：获取指定名称的统一变量位置，并包装为Uniform对象

**参数**：
- `name` (String)：统一变量的名称（与着色器中声明的名称匹配）

**返回值**：Uniform，包装了统一变量位置的Uniform对象

**前置条件**：程序必须已成功链接

**副作用**：调用Gdx.gl.glGetUniformLocation，查询统一变量位置

**核心实现逻辑**：
调用OpenGL的glGetUniformLocation函数获取统一变量位置，然后创建Uniform对象

**边界情况**：
- 如果统一变量名不存在或被编译器优化掉，返回-1（Uniform.location()将返回-1）
- 统一变量名区分大小写

### use()

**可见性**：public

**是否覆写**：否

**方法职责**：激活当前程序对象为OpenGL的当前着色器程序

**参数**：
无

**返回值**：void

**前置条件**：程序必须已成功链接

**副作用**：调用Gdx.gl.glUseProgram，改变OpenGL状态机

**核心实现逻辑**：
调用OpenGL的glUseProgram函数激活指定程序

**边界情况**：
- 如果程序无效，OpenGL会产生错误
- 激活程序后，后续的绘制调用将使用此程序

### delete()

**可见性**：public

**是否覆写**：否

**方法职责**：删除着色器程序对象

**参数**：
无

**返回值**：void

**前置条件**：程序对象必须存在且未被其他对象引用

**副作用**：调用Gdx.gl.glDeleteProgram，释放OpenGL资源

**核心实现逻辑**：
调用OpenGL的glDeleteProgram函数删除程序对象

**边界情况**：
- 删除后不应再使用该程序对象
- 如果程序当前正在使用，行为未定义（通常会延迟删除）

### create(Shader ...shaders)

**可见性**：public static

**是否覆写**：否

**方法职责**：创建、配置并链接完整的着色器程序

**参数**：
- `shaders` (Shader...)：可变参数，包含要附加的所有着色器对象

**返回值**：Program，已链接完成的程序对象

**前置条件**：必须至少包含一个顶点着色器和一个片段着色器

**副作用**：创建Program对象，附加着色器，链接程序，可能抛出异常

**核心实现逻辑**：
1. 创建新的Program对象
2. 遍历所有传入的着色器并附加到程序
3. 调用link()方法链接程序
4. 返回配置完成的Program对象

**边界情况**：
- 如果着色器组合不完整（缺少顶点或片段着色器），链接会失败并抛出异常
- 空的着色器数组会导致链接失败

## 8. 对外暴露能力

### 显式 API
- `handle()`: 获取原生程序ID
- `attach()`, `link()`: 程序配置
- `attribute()`, `uniform()`: 接口获取
- `use()`, `delete()`: 程序控制
- `create()`: 工厂方法

### 内部辅助方法
无（所有方法都是公共API的一部分）

### 扩展入口
无（可通过继承扩展，但设计上不鼓励）

## 9. 运行机制与调用链

### 创建时机
在需要使用自定义着色器时创建，通常在资源加载阶段

### 调用者
- ShaderProgramFactory：着色器程序工厂类
- 渲染管理器：管理不同类型的渲染程序
- 特效系统：创建特效专用着色器

### 被调用者
- Gdx.gl.glCreateProgram()
- Gdx.gl.glAttachShader()
- Gdx.gl.glLinkProgram()
- Gdx.gl.glGetAttribLocation()
- Gdx.gl.glGetUniformLocation()
- Gdx.gl.glUseProgram()
- Gdx.gl.glDeleteProgram()
- Game.reportException()

### 系统流程位置
位于渲染管线的着色器准备阶段，在实际绘制调用之前

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无

### 依赖的资源
- 着色器源代码文件（.vert, .frag）
- 编译后的着色器对象

### 中文翻译来源
不适用（纯技术类，无用户可见文本）

## 11. 使用示例

### 基本用法
```java
// 创建顶点和片段着色器
Shader vertexShader = Shader.createCompiled(Shader.VERTEX, vertexSource);
Shader fragmentShader = Shader.createCompiled(Shader.FRAGMENT, fragmentSource);

// 创建并链接程序
Program program = Program.create(vertexShader, fragmentShader);

// 获取属性和统一变量
Attribute positionAttr = program.attribute("a_position");
Uniform modelViewMatrix = program.uniform("u_modelViewMatrix");

// 使用程序进行渲染
program.use();
positionAttr.enable();
// ... 设置统一变量和绑定数据 ...
// ... 调用绘制命令 ...

// 清理资源
vertexShader.delete();
fragmentShader.delete();
program.delete();
```

### 扩展示例
不适用（基础功能已足够）

## 12. 开发注意事项

### 状态依赖
- 依赖OpenGL上下文的有效性
- 程序链接后才能获取有效的属性和统一变量位置
- 程序必须被use()激活后才能生效

### 生命周期耦合
- 程序对象持有对附加着色器的引用
- 删除程序不会自动删除附加的着色器
- 着色器可以在多个程序间共享

### 常见陷阱
- **链接失败处理**：链接失败会通过Game.reportException报告，但程序仍会被创建
- **无效位置**：不存在的属性/统一变量名会返回-1，使用时会导致OpenGL错误
- **资源泄漏**：忘记delete()会导致GPU内存泄漏
- **状态管理**：频繁切换程序会影响性能，应尽量减少切换次数

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可添加统一变量批量设置方法
- 可添加程序信息查询方法（如活动统一变量数量）
- 可添加二进制程序加载/保存支持

### 不建议修改的位置
- 现有的核心方法签名：已被广泛使用
- 错误处理机制：与Game.reportException集成

### 重构建议
当前实现简洁高效。可考虑添加更详细的错误信息和调试支持，以及统一变量缓存机制以提高性能。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点