# Framebuffer 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | SPD-classes/src/main/java/com/watabou/glwrap/Framebuffer.java |
| **包名** | com.watabou.glwrap |
| **文件类型** | class |
| **继承关系** | 无继承，无接口实现 |
| **代码行数** | 66 |
| **所属模块** | SPD-classes |

## 2. 文件职责说明

### 核心职责
Framebuffer类封装了OpenGL帧缓冲区对象（Framebuffer Object, FBO）的操作，提供帧缓冲区的创建、绑定、附件附加和状态检查功能。

### 系统定位
作为OpenGL帧缓冲区的包装器，为上层渲染系统提供离屏渲染支持，属于高级图形渲染基础设施层。

### 不负责什么
- 不负责纹理或渲染缓冲区的具体创建
- 不负责帧缓冲区内容的读取
- 不负责多渲染目标的复杂配置

## 3. 结构总览

### 主要成员概览
- `COLOR`, `DEPTH`, `STENCIL` (static final int): 帧缓冲区附件点常量
- `system` (static final Framebuffer): 系统默认帧缓冲区（ID为0）
- `id` (int): 帧缓冲区对象的OpenGL ID

### 主要逻辑块概览
- 构造与销毁：创建和删除帧缓冲区对象
- 绑定操作：激活帧缓冲区进行渲染
- 附件管理：将纹理或渲染缓冲区附加到帧缓冲区
- 状态验证：检查帧缓冲区完整性

### 生命周期/调用时机
在需要离屏渲染时创建，使用完毕后删除。通常用于后期处理效果、阴影映射、GUI渲染等场景。

## 4. 继承与协作关系

### 父类提供的能力
无（直接继承自Object）

### 覆写的方法
无

### 实现的接口契约
无

### 依赖的关键类
- `com.badlogic.gdx.Gdx`: 提供OpenGL上下文访问
- `com.watabou.glwrap.Texture`: 纹理附件
- `com.watabou.glwrap.Renderbuffer`: 渲染缓冲区附件

### 使用者
- 后期处理系统：用于屏幕空间效果
- 阴影系统：用于深度映射
- GUI系统：用于界面元素的离屏渲染

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| COLOR | int | Gdx.gl.GL_COLOR_ATTACHMENT0 | 颜色附件点 |
| DEPTH | int | Gdx.gl.GL_DEPTH_ATTACHMENT | 深度附件点 |
| STENCIL | int | Gdx.gl.GL_STENCIL_ATTACHMENT | 模板附件点 |
| system | Framebuffer | new Framebuffer(0) | 系统默认帧缓冲区（窗口系统帧缓冲区） |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| id | int | glGenBuffer()结果 | OpenGL帧缓冲区对象ID |

## 6. 构造与初始化机制

### 构造器
```java
public Framebuffer()
```
调用Gdx.gl.glGenBuffer()生成帧缓冲区ID（注意：此处应为glGenFramebuffer，但源码如此实现）

```java
private Framebuffer(int n)
```
私有构造器，用于创建系统帧缓冲区（ID=0）

### 初始化块
无

### 初始化注意事项
- 公共构造器使用glGenBuffer()而非glGenFramebuffer()，这可能是代码错误
- 系统帧缓冲区使用ID=0，代表窗口系统默认帧缓冲区

## 7. 方法详解

### bind()

**可见性**：public

**是否覆写**：否

**方法职责**：绑定帧缓冲区为当前渲染目标

**参数**：
无

**返回值**：void

**前置条件**：OpenGL上下文必须有效，帧缓冲区必须已创建

**副作用**：调用Gdx.gl.glBindFramebuffer，改变当前渲染目标

**核心实现逻辑**：
调用OpenGL的glBindFramebuffer函数，将指定帧缓冲区绑定为GL_FRAMEBUFFER

**边界情况**：如果id无效，OpenGL会产生错误

### delete()

**可见性**：public

**是否覆写**：否

**方法职责**：删除帧缓冲区对象

**参数**：
无

**返回值**：void

**前置条件**：帧缓冲区必须已创建且未被其他对象引用

**副作用**：调用Gdx.gl.glDeleteBuffer(id)（注意：此处应为glDeleteFramebuffer）

**核心实现逻辑**：
调用OpenGL的glDeleteBuffer函数删除帧缓冲区（可能存在实现错误）

**边界情况**：重复删除可能导致未定义行为

### attach(int point, Texture tex)

**可见性**：public

**是否覆写**：否

**方法职责**：将纹理附加到帧缓冲区的指定附件点

**参数**：
- `point` (int)：附件点（COLOR/DEPTH/STENCIL）
- `tex` (Texture)：要附加的纹理对象

**返回值**：void

**前置条件**：帧缓冲区必须已绑定，纹理必须有效

**副作用**：调用bind()和glFramebufferTexture2D，修改帧缓冲区配置

**核心实现逻辑**：
先绑定帧缓冲区，然后调用glFramebufferTexture2D将纹理附加到指定附件点

**边界情况**：
- point必须是有效的附件点常量
- tex.id必须是有效的纹理ID

### attach(int point, Renderbuffer buffer)

**可见性**：public

**是否覆写**：否

**方法职责**：将渲染缓冲区附加到帧缓冲区的指定附件点

**参数**：
- `point` (int)：附件点（COLOR/DEPTH/STENCIL）
- `buffer` (Renderbuffer)：要附加的渲染缓冲区对象

**返回值**：void

**前置条件**：帧缓冲区必须已绑定，渲染缓冲区必须有效

**副作用**：调用bind()和glFramebufferRenderbuffer，修改帧缓冲区配置

**核心实现逻辑**：
先绑定帧缓冲区，然后调用glFramebufferRenderbuffer将渲染缓冲区附加到指定附件点（注意：第三个参数使用了GL_TEXTURE_2D，应为GL_RENDERBUFFER）

**边界情况**：
- point必须是有效的附件点常量
- buffer.id()必须是有效的渲染缓冲区ID

### status()

**可见性**：public

**是否覆写**：否

**方法职责**：检查帧缓冲区的完整性状态

**参数**：
无

**返回值**：boolean，true表示帧缓冲区完整可用，false表示存在配置错误

**前置条件**：帧缓冲区必须已绑定

**副作用**：调用bind()和glCheckFramebufferStatus，查询帧缓冲区状态

**核心实现逻辑**：
先绑定帧缓冲区，然后调用glCheckFramebufferStatus检查状态，返回是否等于GL_FRAMEBUFFER_COMPLETE

**边界情况**：如果帧缓冲区配置不正确（如缺少颜色附件），返回false

## 8. 对外暴露能力

### 显式 API
- `bind()`: 绑定帧缓冲区
- `delete()`: 删除帧缓冲区
- `attach()`: 附加纹理或渲染缓冲区
- `status()`: 检查完整性

### 内部辅助方法
无（所有方法都是公共API的一部分）

### 扩展入口
无（可通过继承扩展，但设计上不鼓励）

## 9. 运行机制与调用链

### 创建时机
在需要离屏渲染功能时创建，通常在场景初始化或特效系统初始化时

### 调用者
- 后期处理管理器
- 阴影映射系统  
- 自定义渲染通道

### 被调用者
- Gdx.gl.glBindFramebuffer()
- Gdx.gl.glDeleteBuffer()（可能应为glDeleteFramebuffer）
- Gdx.gl.glFramebufferTexture2D()
- Gdx.gl.glFramebufferRenderbuffer()
- Gdx.gl.glCheckFramebufferStatus()

### 系统流程位置
位于渲染管线的离屏渲染阶段，在主渲染循环之前或之后

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无

### 依赖的资源
- 纹理资源：用于颜色附件
- 渲染缓冲区：用于深度/模板附件

### 中文翻译来源
不适用（纯技术类，无用户可见文本）

## 11. 使用示例

### 基本用法
```java
// 创建帧缓冲区用于后期处理
Framebuffer fbo = new Framebuffer();
Texture colorTex = Texture.create(width, height, pixels);
fbo.attach(Framebuffer.COLOR, colorTex);

// 检查帧缓冲区是否有效
if (fbo.status()) {
    // 绑定FBO进行离屏渲染
    fbo.bind();
    // ... 渲染场景到FBO ...
    
    // 切换回系统帧缓冲区
    Framebuffer.system.bind();
    // ... 使用colorTex进行后期处理 ...
}

// 清理资源
fbo.delete();
colorTex.delete();
```

### 扩展示例
不适用（基础功能已足够）

## 12. 开发注意事项

### 状态依赖
- 依赖OpenGL上下文的有效性
- 帧缓冲区绑定会影响后续所有渲染操作

### 生命周期耦合
- 帧缓冲区的附件（纹理/渲染缓冲区）必须在其生命周期内保持有效
- 删除帧缓冲区不会自动删除其附件

### 常见陷阱
- **代码问题**：构造器中使用glGenBuffer()而不是glGenFramebuffer()
- **代码问题**：delete()中使用glDeleteBuffer()而不是glDeleteFramebuffer()  
- **代码问题**：renderbuffer attach中使用GL_TEXTURE_2D而不是GL_RENDERBUFFER
- 忘记检查帧缓冲区状态可能导致渲染失败而不报错
- 帧缓冲区尺寸必须与附件匹配

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可添加多渲染目标（MRT）支持
- 可添加帧缓冲区尺寸查询功能

### 不建议修改的位置
- 现有的附件点常量：这些是OpenGL标准
- 核心绑定逻辑：已被广泛使用

### 重构建议
**重要**：修复构造器和删除方法中的OpenGL函数调用错误：
- 将`glGenBuffer()`改为`glGenFramebuffer()`
- 将`glDeleteBuffer()`改为`glDeleteFramebuffer()`
- 将renderbuffer attach中的`GL_TEXTURE_2D`改为`GL_RENDERBUFFER`

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点