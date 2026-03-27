# Shader 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | SPD-classes/src/main/java/com/watabou/glwrap/Shader.java |
| **包名** | com.watabou.glwrap |
| **文件类型** | class |
| **继承关系** | 无继承，无接口实现 |
| **代码行数** | 68 |
| **所属模块** | SPD-classes |

## 2. 文件职责说明

### 核心职责
Shader类封装了OpenGL着色器对象（Shader Object）的操作，提供着色器的创建、源代码设置、编译和删除功能。

### 系统定位
作为OpenGL着色器的包装器，为Program类提供着色器管理支持，属于图形渲染核心基础设施层。

### 不负责什么
- 不负责着色器程序的链接（由Program类处理）
- 不负责着色器源代码的加载和解析
- 不负责渲染管线的具体执行

## 3. 结构总览

### 主要成员概览
- `VERTEX`, `FRAGMENT` (static final int): 着色器类型常量
- `handle` (int): OpenGL着色器对象的ID

### 主要逻辑块概览
- 构造与销毁：创建和删除着色器对象
- 源代码管理：设置着色器源代码
- 编译操作：编译着色器源代码

### 生命周期/调用时机
在需要使用自定义着色器时创建，通常在资源加载阶段，配合Program类使用。

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

### 使用者
- Program类：attach()方法中使用
- 渲染系统：创建各种类型的着色器
- 特效系统：使用自定义着色器实现视觉效果

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| VERTEX | int | Gdx.gl.GL_VERTEX_SHADER | 顶点着色器类型 |
| FRAGMENT | int | Gdx.gl.GL_FRAGMENT_SHADER | 片段着色器类型 |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| handle | int | glCreateShader()结果 | OpenGL着色器对象ID |

## 6. 构造与初始化机制

### 构造器
```java
public Shader(int type)
```
接受着色器类型参数（VERTEX或FRAGMENT），调用Gdx.gl.glCreateShader()创建着色器对象并存储ID到handle字段。

### 初始化块
无

### 初始化注意事项
- 构造后着色器对象为空，需要设置源代码并编译才能使用
- 无效的type参数会导致glCreateShader返回0
- 着色器对象创建失败会返回0，但GDX通常会抛出异常

## 7. 方法详解

### handle()

**可见性**：public

**是否覆写**：否

**方法职责**：获取底层OpenGL着色器对象的ID

**参数**：
无

**返回值**：int，OpenGL着色器对象ID

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return handle;
```

**边界情况**：无

### source(String src)

**可见性**：public

**是否覆写**：否

**方法职责**：设置着色器的源代码

**参数**：
- `src` (String)：着色器源代码字符串

**返回值**：void

**前置条件**：着色器对象必须有效

**副作用**：调用Gdx.gl.glShaderSource，将源代码关联到着色器对象

**核心实现逻辑**：
调用OpenGL的glShaderSource函数将源代码字符串设置到着色器对象

**边界情况**：
- src为null会导致OpenGL错误
- 空字符串是合法的（但编译会失败）

### compile()

**可见性**：public

**是否覆写**：否

**方法职责**：编译着色器源代码

**参数**：
无

**返回值**：void

**前置条件**：着色器必须已设置源代码

**副作用**：调用Gdx.gl.glCompileShader和相关查询函数，可能抛出Error异常

**核心实现逻辑**：
1. 调用glCompileShader编译着色器
2. 查询编译状态（GL_COMPILE_STATUS）
3. 如果编译失败，抛出Error异常并包含错误日志（通过glGetShaderInfoLog）

**边界情况**：
- 编译失败会抛出Error异常（不是RuntimeException）
- 成功编译后的着色器可以附加到程序对象

### delete()

**可见性**：public

**是否覆写**：否

**方法职责**：删除着色器对象

**参数**：
无

**返回值**：void

**前置条件**：着色器对象必须存在且未被其他对象引用

**副作用**：调用Gdx.gl.glDeleteShader，释放OpenGL资源

**核心实现逻辑**：
调用OpenGL的glDeleteShader函数删除着色器对象

**边界情况**：
- 删除后不应再使用该着色器对象
- 如果着色器附加到程序对象，删除会被延迟直到程序也被删除

### createCompiled(int type, String src)

**可见性**：public static

**是否覆写**：否

**方法职责**：创建、设置源代码并编译完整的着色器对象

**参数**：
- `type` (int)：着色器类型（VERTEX或FRAGMENT）
- `src` (String)：着色器源代码字符串

**返回值**：Shader，已编译完成的着色器对象

**前置条件**：type必须是有效的着色器类型，src必须是有效的GLSL代码

**副作用**：创建Shader对象，设置源代码，编译着色器，可能抛出异常

**核心实现逻辑**：
1. 创建新的Shader对象（指定类型）
2. 调用source()设置源代码
3. 调用compile()编译着色器
4. 返回编译完成的Shader对象

**边界情况**：
- 无效的GLSL代码会导致compile()抛出Error异常
- 无效的type参数会导致构造器创建失败

## 8. 对外暴露能力

### 显式 API
- `handle()`: 获取原生着色器ID
- `source()`, `compile()`: 着色器编译流程
- `delete()`: 着色器删除
- `createCompiled()`: 工厂方法

### 内部辅助方法
无（所有方法都是公共API的一部分）

### 扩展入口
无（可通过继承扩展，但设计上不鼓励）

## 9. 运行机制与调用链

### 创建时机
在需要使用自定义着色器时创建，通常在资源加载阶段

### 调用者
- ShaderLoader：着色器加载器
- Program.create()：程序工厂方法
- 特效系统：动态创建着色器

### 被调用者
- Gdx.gl.glCreateShader()
- Gdx.gl.glShaderSource()
- Gdx.gl.glCompileShader()
- Gdx.gl.glGetShaderiv()
- Gdx.gl.glGetShaderInfoLog()
- Gdx.gl.glDeleteShader()

### 系统流程位置
位于渲染管线的着色器准备阶段，在程序链接之前

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无

### 依赖的资源
- GLSL着色器源代码文件（.vert, .frag）
- OpenGL上下文

### 中文翻译来源
不适用（纯技术类，无用户可见文本）

## 11. 使用示例

### 基本用法
```java
// 加载着色器源代码
String vertexSource = loadShaderSource("basic.vert");
String fragmentSource = loadShaderSource("basic.frag");

// 创建并编译着色器
Shader vertexShader = Shader.createCompiled(Shader.VERTEX, vertexSource);
Shader fragmentShader = Shader.createCompiled(Shader.FRAGMENT, fragmentSource);

// 创建程序并附加着色器
Program program = new Program();
program.attach(vertexShader);
program.attach(fragmentShader);
program.link();

// 使用程序进行渲染
program.use();
// ... 设置属性和统一变量 ...
// ... 调用绘制命令 ...

// 清理资源
vertexShader.delete();
fragmentShader.delete();
program.delete();
```

### 错误处理示例
```java
try {
    Shader shader = Shader.createCompiled(Shader.FRAGMENT, invalidSource);
} catch (Error e) {
    // 编译失败，错误信息包含在异常消息中
    System.err.println("Shader compilation failed: " + e.getMessage());
}
```

## 12. 开发注意事项

### 状态依赖
- 依赖OpenGL上下文的有效性
- 源代码必须在编译前设置
- 编译后的着色器可以附加到多个程序对象

### 生命周期耦合
- 着色器对象可以在多个程序间共享
- 删除着色器不会影响已链接的程序
- 程序对象持有对着色器的引用

### 常见陷阱
- **异常类型**：编译失败抛出Error而不是Exception，需要特别注意捕获
- **资源泄漏**：忘记delete()会导致GPU内存泄漏
- **源代码格式**：GLSL源代码必须符合OpenGL版本要求
- **编译优化**：编译器可能优化掉未使用的统一变量或属性，导致 glGetUniformLocation/AttribLocation 返回-1

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可添加着色器信息查询方法（如活动统一变量列表）
- 可添加二进制着色器支持
- 可添加更详细的错误信息解析

### 不建议修改的位置
- 现有的核心方法签名：已被广泛使用
- 异常处理机制：抛出Error是标准做法

### 重构建议
当前实现简洁高效。可考虑：
- 添加参数验证以提高调试友好性
- 支持从文件直接加载的便捷方法
- 添加着色器缓存机制以避免重复编译

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点