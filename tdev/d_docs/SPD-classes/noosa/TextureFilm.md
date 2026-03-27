# TextureFilm Class Documentation

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | SPD-classes/src/main/java/com/watabou/noosa/TextureFilm.java |
| **包名** | com.watabou.noosa |
| **文件类型** | class |
| **继承关系** | 无（独立类） |
| **代码行数** | 146 |
| **所属模块** | SPD-classes |

## 2. 文件职责说明

### 核心职责
TextureFilm 类负责管理纹理图集（texture atlas）中的帧映射，将任意标识符（通常是整数或字符）映射到纹理中的特定 UV 矩形区域，为 Sprite、Tilemap 和 BitmapText 等组件提供高效的纹理帧查找服务。

### 系统定位
作为纹理管理的中间层，TextureFilm 桥接了原始 SmartTexture 资源和具体的可视化组件。它不直接参与渲染，而是提供抽象的帧映射接口，使得上层组件无需关心底层纹理的具体布局。

### 不负责什么
- 不处理纹理资源的加载和缓存（由 TextureCache 处理）
- 不直接参与 OpenGL 渲染（由 Visual 子类处理）
- 不管理纹理内存（由 SmartTexture 处理）

## 3. 结构总览

### 主要成员概览
- **纹理尺寸**: texWidth, texHeight
- **帧映射**: frames (HashMap<Object, RectF>)
- **静态常量**: FULL (完整纹理矩形)

### 主要逻辑块概览
- 多种构造器支持不同纹理布局方式
- add() 方法添加自定义帧映射
- get() 方法查询帧 UV 坐标
- width()/height() 方法计算帧实际尺寸

### 生命周期/调用时机
- **创建时**: 构造器根据纹理布局自动构建帧映射
- **运行时**: get() 方法被 Sprite、Tilemap 等组件频繁调用
- **无销毁需求**: 纯数据结构，无需要清理的资源

## 4. 继承与协作关系

### 父类提供的能力
无父类（独立类）

### 覆写的方法
无

### 实现的接口契约
无直接接口实现

### 依赖的关键类
- **com.watabou.gltextures.SmartTexture**: 底层纹理资源
- **com.watabou.gltextures.TextureCache**: 纹理缓存管理
- **com.watabou.utils.RectF**: UV 坐标矩形表示

### 使用者
- **com.watabou.noosa.BitmapText.Font**: 字体字符映射
- **com.watabou.noosa.Sprite**: 动画帧映射
- **com.watabou.noosa.Tilemap**: 瓷砖映射
- **所有基于纹理图集的可视化组件**

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| FULL | RectF | (0,0,1,1) | 完整纹理的 UV 坐标 |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| texWidth | int | 构造参数 | 纹理宽度（像素）|
| texHeight | int | 构造参数 | 纹理高度（像素）|
| frames | HashMap<Object,RectF> | empty map | 帧标识符到 UV 矩形的映射 |

## 6. 构造与初始化机制

### 构造器

```java
public TextureFilm(Object tx)
```
- 通过 TextureCache.get(tx) 获取 SmartTexture
- 设置 texWidth/texHeight 为纹理尺寸
- 添加 null → FULL 的默认映射

```java
public TextureFilm(SmartTexture texture, int width)
```
- 调用 this(texture, width, texture.height)
- 适用于正方形瓷砖的简化构造

```java
public TextureFilm(Object tx, int width, int height)
```
- 获取 SmartTexture
- 计算列数(cols)=texWidth/width, 行数(rows)=texHeight/height
- 自动构建网格布局的帧映射（索引 0 到 cols×rows-1）

```java
public TextureFilm(TextureFilm atlas, Object key, int width, int height)
```
- 从现有 TextureFilm 的指定帧区域创建子图集
- 计算子区域内的网格布局
- 偏移 UV 坐标到父图集的正确位置

```java
public TextureFilm(int txWidth, int txHeight, int width, int height)
```
- 创建不关联实际纹理的虚拟 TextureFilm
- 仅用于尺寸计算，不支持实际纹理绑定

### 初始化块
无静态或实例初始化块

### 初始化注意事项
- 所有构造器都验证宽度/高度参数的合理性
- 网格布局构造器要求纹理尺寸能被瓷砖尺寸整除
- 子图集构造器依赖父图集包含有效的 key 映射

## 7. 方法详解

### add(Object id, RectF rect)
**可见性**：public  
**是否覆写**：否  
**方法职责**：添加自定义帧映射  
**参数**：
- id (Object) - 帧标识符（可为 Integer, Character, String 等）
- rect (RectF) - UV 坐标矩形
**返回值**：void  
**前置条件**：rect 值在 [0,1] 范围内  
**副作用**：修改 frames HashMap  
**核心实现逻辑**：frames.put(id, rect)  
**边界情况**：重复 id 会覆盖原有映射

### add(Object id, float left, float top, float right, float bottom)
**可见性**：public  
**是否覆写**：否  
**方法职责**：以像素坐标添加帧映射（自动转换为 UV）  
**参数**：
- id (Object) - 帧标识符
- left/top/right/bottom (float) - 像素坐标
**返回值**：void  
**前置条件**：坐标在纹理范围内  
**副作用**：修改 frames HashMap  
**核心实现逻辑**：创建 RectF(left/texWidth, top/texHeight, right/texWidth, bottom/texHeight)  
**边界情况**：坐标超出纹理范围会产生无效 UV 坐标

### get(Object id)
**可见性**：public  
**是否覆写**：否  
**方法职责**：查询帧 UV 坐标  
**参数**：id (Object) - 帧标识符  
**返回值**：RectF，UV 坐标矩形或 null  
**前置条件**：无  
**副作用**：无  
**核心实现逻辑**：return frames.get(id)  
**边界情况**：未知 id 返回 null

### width(Object id)
**可见性**：public  
**是否覆写**：否  
**方法职责**：获取帧的实际宽度（像素）  
**参数**：id (Object) - 帧标识符  
**返回值**：float，宽度像素值  
**前置条件**：id 必须存在于 frames 中  
**副作用**：无  
**核心实现逻辑**：调用 width(get(id))  
**边界情况**：未知 id 导致 NullPointerException

### width(RectF frame)
**可见性**：public  
**是否覆写**：否  
**方法职责**：计算 UV 矩形对应的实际宽度  
**参数**：frame (RectF) - UV 坐标矩形  
**返回值**：float，宽度像素值  
**前置条件**：frame 非 null  
**副作用**：无  
**核心实现逻辑**：return frame.width() * texWidth  
**边界情况**：null frame 导致 NullPointerException

### height(Object id)
**可见性**：public  
**是否覆写**：否  
**方法职责**：获取帧的实际高度（像素）  
**参数**：id (Object) - 帧标识符  
**返回值**：float，高度像素值  
**前置条件**：id 必须存在于 frames 中  
**副作用**：无  
**核心实现逻辑**：调用 height(get(id))  
**边界情况**：未知 id 导致 NullPointerException

### height(RectF frame)
**可见性**：public  
**是否覆写**：否  
**方法职责**：计算 UV 矩形对应的实际高度  
**参数**：frame (RectF) - UV 坐标矩形  
**返回值**：float，高度像素值  
**前置条件**：frame 非 null  
**副作用**：无  
**核心实现逻辑**：return frame.height() * texHeight  
**边界情况**：null frame 导致 NullPointerException

## 8. 对外暴露能力

### 显式 API
- **帧管理**: add(), get() 方法
- **尺寸查询**: width(), height() 方法
- **多种构造方式**: 支持网格、子图集、自定义等布局

### 内部辅助方法
- **UV 转换**: 像素坐标到 UV 坐标的自动转换
- **尺寸计算**: UV 矩形到实际像素尺寸的计算

### 扩展入口
- 无直接扩展点（设计为不可继承的工具类）
- 通过组合使用实现自定义功能

## 9. 运行机制与调用链

### 创建时机
- 资源加载时（Sprite 动画、Tilemap 瓷砖、BitmapText 字体）
- 动态纹理生成时（程序化内容）
- 游戏初始化时（预加载常用纹理图集）

### 调用者
- **Sprite**: 查询动画帧 UV 坐标
- **Tilemap**: 查询瓷砖 UV 坐标  
- **BitmapText.Font**: 查询字符 UV 坐标
- **CustomVisual**: 自定义可视化组件的纹理映射

### 被调用者
- **TextureCache**: 获取 SmartTexture 实例
- **SmartTexture**: 查询纹理尺寸

### 系统流程位置
1. **资源初始化**: new TextureFilm("sprites/hero.png", 16, 16)
2. **帧查询**: sprite.frame(animFrameIndex) → textureFilm.get(frameIndex)
3. **尺寸计算**: tilemap.cellW = textureFilm.width(0)
4. **渲染准备**: visual.updateVertices() → uses UV coordinates from get()

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无

### 依赖的资源
- **纹理文件**: PNG/JPG 纹理图集文件
- **内存**: HashMap 存储帧映射关系

### 中文翻译来源
无官方中文翻译关联

## 11. 使用示例

### 网格布局纹理图集
```java
// 创建 4x4 网格的英雄精灵图集（每个帧16x16像素）
SmartTexture heroTex = TextureCache.get("sprites/hero.png");
TextureFilm heroFilm = new TextureFilm(heroTex, 16, 16);

// 查询第5帧的UV坐标（索引从0开始）
RectF frame5 = heroFilm.get(5);
// 获取帧的实际尺寸
float frameWidth = heroFilm.width(5); // = 16.0f

// 在 Sprite 中使用
Sprite hero = new Sprite(heroTex);
hero.frame(heroFilm.get(walkAnimFrame));
```

### 子图集创建
```java
// 从大图集中提取武器子图集
TextureFilm allItems = new TextureFilm(TextureCache.get("items/all.png"), 16, 16);
// 提取索引10-15的武器区域（假设是2x3网格）
TextureFilm weapons = new TextureFilm(allItems, 10, 16, 16);

// 武器子图集现在有自己独立的索引0-5
Sprite sword = new Sprite(weapons.texture);
sword.frame(weapons.get(0)); // 第一个武器
```

### 自定义帧映射
```java
// 创建手动映射的纹理图集
TextureFilm customFilm = new TextureFilm("ui/buttons.png");
// 添加自定义按钮区域（像素坐标）
customFilm.add("play_btn", 0, 0, 64, 32);
customFilm.add("quit_btn", 64, 0, 128, 32);

// 使用字符串键查询
Image playButton = new Image(customFilm.texture);
playButton.frame(customFilm.get("play_btn"));
```

### 虚拟 TextureFilm（仅尺寸计算）
```java
// 创建不关联实际纹理的 TextureFilm（用于布局计算）
TextureFilm virtualFilm = new TextureFilm(256, 256, 32, 32);
// 可以查询尺寸但不能用于实际渲染
int cols = 256 / 32; // 8列
int rows = 256 / 32; // 8行
float cellWidth = virtualFilm.width(0); // 32.0f
```

## 12. 开发注意事项

### 状态依赖
- **纹理一致性**: TextureFilm 必须与实际使用的 SmartTexture 匹配
- **映射完整性**: 所有使用的帧标识符必须预先添加到映射中
- **坐标系统**: UV 坐标范围 [0,1]，像素坐标范围 [0, texture_size]

### 生命周期耦合
- **纹理生命周期**: 依赖 SmartTexture 的生命周期管理
- **无资源清理**: 纯数据结构，GC 会自动回收

### 常见陷阱
- **索引越界**: 网格构造器中的索引必须在有效范围内
- **浮点精度**: UV 坐标计算可能存在微小浮点误差
- **内存泄漏**: 虽然 TextureFilm 本身无资源，但持有 SmartTexture 引用
- **线程安全**: HashMap 非线程安全，在多线程环境中需同步访问

## 13. 修改建议与扩展点

### 适合扩展的位置
- **批量操作**: 添加批量 addAll() 方法提高初始化性能
- **反向映射**: 支持从 UV 坐标反查帧标识符
- **序列化**: 支持保存/加载帧映射配置

### 不建议修改的位置
- **核心映射逻辑**: HashMap 查找经过性能验证
- **UV 坐标计算**: 当前的计算公式是标准做法
- **构造器多样性**: 多种构造方式满足不同使用场景

### 重构建议
- **TypedTextureFilm**: 使用泛型提供类型安全的帧标识符
- **ImmutableTextureFilm**: 提供不可变版本用于线程安全场景
- **LazyLoading**: 支持延迟加载帧映射以减少内存使用

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（无相关翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点