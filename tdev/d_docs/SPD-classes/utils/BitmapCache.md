# BitmapCache 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\SPD-classes\src\main\java\com\watabou\utils\BitmapCache.java |
| **包名** | com.watabou.utils |
| **文件类型** | class |
| **继承关系** | 无继承，无实现接口 |
| **代码行数** | 118 |
| **所属模块** | SPD-classes |

## 2. 文件职责说明

### 核心职责
提供位图（Pixmap）资源的缓存管理功能，避免重复加载相同资源，优化内存使用和加载性能。

### 系统定位
作为游戏引擎的资源管理层，为图形渲染系统提供高效的位图资源缓存服务，支持分层缓存机制。

### 不负责什么
- 不负责位图的实际渲染
- 不处理位图的修改或编辑
- 不管理其他类型资源（如音频、文本等）

## 3. 结构总览

### 主要成员概览
- `DEFAULT`: 默认图层名称常量
- `layers`: 存储各图层缓存的HashMap
- `Layer`: 内部私有类，扩展HashMap以支持自动资源释放

### 主要逻辑块概览
- 资源获取（get方法）
- 缓存清理（clear方法）
- 资源释放（Layer.clear重写）

### 生命周期/调用时机
- 资源按需加载并缓存
- 清理方法在场景切换或内存压力时调用
- 自动释放底层图形资源

## 4. 继承与协作关系

### 父类提供的能力
无

### 覆写的方法
- Layer.clear(): 重写HashMap.clear()以确保Pixmap资源正确释放

### 实现的接口契约
无

### 依赖的关键类
- `com.badlogic.gdx.Gdx`: LibGDX框架入口点
- `com.badlogic.gdx.graphics.Pixmap`: LibGDX位图类
- `com.watabou.noosa.Game`: 游戏主类，用于异常报告
- `java.util.HashMap`: 基础数据结构

### 使用者
- 游戏中的UI系统
- 精灵和纹理管理系统
- 需要加载位图资源的任何模块

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| DEFAULT | String | "__default" | 默认缓存图层的名称 |

### 实例字段
无

### 静态字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| layers | HashMap<String,Layer> | new HashMap<>() | 存储所有缓存图层的映射，键为图层名称，值为Layer实例 |

## 6. 构造与初始化机制

### 构造器
无公共构造器，类不能被实例化

### 初始化块
无静态或实例初始化块

### 初始化注意事项
- layers字段在类加载时初始化为空HashMap
- 各Layer实例在首次访问对应图层时按需创建

## 7. 方法详解

### get() (重载1)
**可见性**：public static

**是否覆写**：否

**方法职责**：从默认图层获取指定资产名称的位图资源

**参数**：
- `assetName` (String)：资产文件路径

**返回值**：Pixmap，加载的位图资源，失败时返回null

**前置条件**：assetName不能为null

**副作用**：可能创建新的Layer实例，可能从文件系统加载新资源

**核心实现逻辑**：
```java
return get(DEFAULT, assetName);
```

**边界情况**：资源文件不存在时捕获异常并返回null

### get() (重载2)
**可见性**：public static

**是否覆写**：否

**方法职责**：从指定图层获取位图资源，支持缓存和按需加载

**参数**：
- `layerName` (String)：缓存图层名称
- `assetName` (String)：资产文件路径

**返回值**：Pixmap，加载的位图资源，失败时返回null

**前置条件**：参数不能为null

**副作用**：可能创建新图层，可能从文件加载资源，可能报告异常

**核心实现逻辑**：
1. 检查图层是否存在，不存在则创建
2. 检查资源是否已缓存，已缓存则直接返回
3. 未缓存则通过Gdx.files.internal()加载资源
4. 加载失败时报告异常并返回null

**边界情况**：文件不存在或格式错误时捕获异常

### clear() (重载1)
**可见性**：public static

**是否覆写**：否

**方法职责**：清理指定图层的所有缓存资源

**参数**：
- `layerName` (String)：要清理的图层名称

**返回值**：void

**前置条件**：layerName不能为null

**副作用**：释放该图层所有Pixmap资源，从layers中移除图层

**核心实现逻辑**：
调用Layer.clear()释放资源，然后从layers映射中移除

**边界情况**：图层不存在时不执行任何操作

### clear() (重载2)
**可见性**：public static

**是否覆写**：否

**方法职责**：清理所有图层的缓存资源

**参数**：无

**返回值**：void

**前置条件**：无

**副作用**：释放所有缓存的Pixmap资源，清空layers映射

**核心实现逻辑**：
遍历所有Layer实例，调用其clear()方法，然后清空layers

**边界情况**：无缓存时安全执行

### Layer.clear()
**可见性**：private (内部类)

**是否覆写**：是，覆写自HashMap

**方法职责**：释放所有Pixmap资源并清空映射

**参数**：无

**返回值**：void

**前置条件**：无

**副作用**：调用每个Pixmap的dispose()方法释放底层资源

**核心实现逻辑**：
```java
for (Pixmap bmp:values()) {
    bmp.dispose();
}
super.clear();
```

**边界情况**：空映射时安全执行

## 8. 对外暴露能力

### 显式 API
- get(String): 从默认图层获取资源
- get(String, String): 从指定图层获取资源  
- clear(): 清理所有缓存
- clear(String): 清理指定图层缓存

### 内部辅助方法
- Layer内部类及其clear方法

### 扩展入口
- 无扩展点，设计为封闭的工具类

## 9. 运行机制与调用链

### 创建时机
- 首次调用get方法时按需创建Layer实例
- Pixmap资源在首次请求时加载

### 调用者
- 游戏启动时的资源预加载
- 场景切换时的资源管理
- UI组件的动态资源加载

### 被调用者
- Gdx.files.internal(): LibGDX文件系统API
- Pixmap.dispose(): 资源释放
- Game.reportException(): 异常报告

### 系统流程位置
- 资源管理层，位于游戏引擎和图形API之间

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无

### 依赖的资源
- 图像文件（PNG、JPEG等LibGDX支持的格式）
- 通过assetName参数指定的文件路径

### 中文翻译来源
不适用

## 11. 使用示例

### 基本用法
```java
// 从默认图层加载位图
Pixmap heroSprite = BitmapCache.get("sprites/hero.png");

// 从指定图层加载位图（用于组织不同类型的资源）
Pixmap uiElement = BitmapCache.get("ui", "ui/button.png");

// 清理特定图层（如切换关卡时清理旧关卡资源）
BitmapCache.clear("level_1");

// 清理所有缓存（如游戏重启时）
BitmapCache.clear();
```

### 扩展示例
```java
// 安全使用模式，检查返回值
Pixmap texture = BitmapCache.get("textures/wall.png");
if (texture != null) {
    // 使用texture进行渲染
} else {
    // 处理资源加载失败的情况
}
```

## 12. 开发注意事项

### 状态依赖
- layers静态字段维护全局缓存状态
- 各Layer实例持有Pixmap引用，影响内存使用

### 生命周期耦合
- 必须在适当时候调用clear()释放资源，避免内存泄漏
- Pixmap资源与LibGDX的图形上下文生命周期相关

### 常见陷阱
- 忘记调用clear()导致内存泄漏
- 在错误的线程访问（LibGDX通常要求在渲染线程）
- 假设资源一定存在而忽略null检查
- 多次加载相同资源（缓存机制会避免重复加载，但仍有查找开销）

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可以添加LRU缓存策略以限制内存使用
- 可以添加异步加载支持
- 可以添加资源预热功能

### 不建议修改的位置
- Layer.clear()的dispose()调用（关键的资源释放）
- 异常处理逻辑（确保稳定性）

### 重构建议
- 考虑使用WeakReference防止内存泄漏
- 可以添加缓存大小监控和统计

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点