# PlatformSupport 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\SPD-classes\src\main\java\com\watabou\utils\PlatformSupport.java |
| **包名** | com.watabou.utils |
| **文件类型** | abstract class |
| **继承关系** | 无父类，无实现接口 |
| **代码行数** | 187 |
| **所属模块** | SPD-classes |

## 2. 文件职责说明

### 核心职责
提供跨平台功能抽象层，封装不同操作系统（Android/iOS/Desktop）的特定功能，包括显示管理、输入处理、振动反馈、字体生成和系统UI集成。

### 系统定位
作为游戏引擎的平台适配层，为上层游戏逻辑提供统一的平台功能接口，屏蔽底层操作系统和硬件差异。

### 不负责什么
- 不负责具体平台的实现细节（由子类实现）
- 不处理业务逻辑（仅提供平台能力抽象）
- 不管理游戏状态或数据

## 3. 结构总览

### 主要成员概览
- 抽象方法：updateDisplaySize, updateSystemUI, connectedToUnmeteredNetwork, supportsVibration, setupFontGenerators, getGeneratorForString, splitforTextBlock
- 实现方法：vibrate, openURI, setOnscreenKeyboardVisible, resetGenerators, reloadGenerators, getFont
- 静态常量：INSET_ALL/INSET_LRG/INSET_BLK
- 静态字段：fonts（字体缓存）
- 实例字段：pageSize, packer, systemfont

### 主要逻辑块概览
- 显示和UI管理（safe insets, display cutout）
- 输入和振动处理
- 字体生成和缓存管理
- 系统集成功能（URI打开、网络检测）

### 生命周期/调用时机
- 游戏启动时初始化平台支持
- 显示尺寸变化时调用updateDisplaySize
- 需要字体时调用getFont
- 应用生命周期事件时调用相应方法

## 4. 继承与协作关系

### 父类提供的能力
无

### 覆写的方法
无

### 实现的接口契约
子类必须实现所有抽象方法

### 依赖的关键类
- `com.badlogic.gdx.*`: LibGDX框架核心API
- `com.watabou.input.ControllerHandler`: 控制器输入处理
- `com.watabou.noosa.Game`: 异常报告
- Java集合框架

### 使用者
- GameApplication类（应用主类）
- UI系统（字体渲染、键盘控制）
- 输入系统（振动、控制器）
- 网络功能（云存档、更新检查）

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| INSET_ALL | int | 3 | 所有安全区域插槽级别（孔洞到导航栏） |
| INSET_LRG | int | 2 | 仅大插槽级别（完整尺寸缺口和导航栏） |
| INSET_BLK | int | 1 | 仅完全阻塞资产（如导航栏） |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pageSize | int | 0 | 字体图集页面大小 |
| packer | PixmapPacker | null | 字体纹理打包器 |
| systemfont | boolean | false | 是否使用系统字体 |

### 静态字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| fonts | HashMap<FreeTypeFontGenerator, HashMap<Integer, BitmapFont>> | null | 字体缓存，按生成器和大小索引 |

## 6. 构造与初始化机制

### 构造器
抽象类，不能直接实例化

### 初始化块
无

### 初始化注意事项
- 子类负责具体的平台初始化
- 字体系统通过setupFontGenerators初始化
- 显示尺寸通过updateDisplaySize更新

## 7. 方法详解

### updateDisplaySize()
**可见性**：public abstract

**是否覆写**：否

**方法职责**：更新显示尺寸信息（平台特定）

**参数**：无

**返回值**：void

**前置条件**：无

**副作用**：可能更新内部显示状态

**核心实现逻辑**：
由子类实现，通常处理屏幕旋转、窗口大小变化等

**边界情况**：无

### supportsFullScreen()
**可见性**：public

**是否覆写**：否

**方法职责**：检查平台是否支持全屏模式

**参数**：无

**返回值**：boolean，true表示支持全屏

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return true; // 默认支持
```

**边界情况**：子类可以覆写以禁用全屏

### getSafeInsets()
**可见性**：public

**是否覆写**：否

**方法职责**：获取安全区域插槽（处理刘海屏、导航栏等）

**参数**：
- `level` (int)：插槽级别（INSET_ALL/INSET_LRG/INSET_BLK）

**返回值**：RectF，安全区域的四个边距

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return new RectF(
    Gdx.graphics.getSafeInsetLeft(),
    Gdx.graphics.getSafeInsetTop(), 
    Gdx.graphics.getSafeInsetRight(),
    Gdx.graphics.getSafeInsetBottom()
);
```

**边界情况**：不支持安全区域的平台返回零值

### getDisplayCutout()
**可见性**：public

**是否覆写**：否

**方法职责**：获取显示屏缺口信息（刘海屏等）

**参数**：无

**返回值**：RectF，缺口区域（设备像素），无缺口时返回空RectF

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return new RectF(); // 默认无缺口
```

**边界情况**：子类可以覆写以提供平台特定实现

### updateSystemUI()
**可见性**：public abstract

**是否覆写**：否

**方法职责**：更新系统UI状态（沉浸式模式、状态栏等）

**参数**：无

**返回值**：void

**前置条件**：无

**副作用**：可能改变系统UI外观

**核心实现逻辑**：
由子类实现，处理平台特定的UI集成

**边界情况**：无

### connectedToUnmeteredNetwork()
**可见性**：public abstract

**是否覆写**：否

**方法职责**：检查是否连接到不限流量网络

**参数**：无

**返回值**：boolean，true表示连接到WiFi等不限流量网络

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
由子类实现，用于决定是否下载大文件或同步数据

**边界情况**：桌面平台通常返回true

### supportsVibration()
**可见性**：public abstract

**是否覆写**：否

**方法职责**：检查设备是否支持振动

**参数**：无

**返回值**：boolean，true表示支持振动

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
由子类实现，根据硬件能力返回结果

**边界情况**：桌面平台通常返回false

### vibrate()
**可见性**：public

**是否覆写**：否

**方法职责**：触发设备振动

**参数**：
- `millis` (int)：振动持续时间（毫秒）

**返回值**：void

**前置条件**：supportsVibration()应返回true

**副作用**：触发设备振动

**核心实现逻辑**：
```java
if (ControllerHandler.isControllerConnected()) {
    ControllerHandler.vibrate(millis);
} else {
    Gdx.input.vibrate(millis);
}
```

**边界情况**：控制器优先于设备振动

### setHonorSilentSwitch()
**可见性**：public

**是否覆写**：否

**方法职责**：设置是否遵循静音开关（iOS特定）

**参数**：
- `value` (boolean)：true表示遵循静音开关

**返回值**：void

**前置条件**：无

**副作用**：无（默认实现）

**核心实现逻辑**：
```java
// does nothing by default
```

**边界情况**：iOS子类会覆写此方法

### openURI()
**可见性**：public

**是否覆写**：否

**方法职责**：在外部浏览器中打开URI

**参数**：
- `uri` (String)：要打开的URI

**返回值**：boolean，true表示成功打开

**前置条件**：uri格式正确

**副作用**：启动外部应用程序

**核心实现逻辑**：
```java
return Gdx.net.openURI(uri);
```

**边界情况**：无效URI或无浏览器时返回false

### setOnscreenKeyboardVisible()
**可见性**：public

**是否覆写**：否

**方法职责**：控制屏幕键盘可见性

**参数**：
- `value` (boolean)：true显示键盘
- `multiline` (boolean)：是否多行输入（默认忽略）

**返回值**：void

**前置条件**：无

**副作用**：显示或隐藏虚拟键盘

**核心实现逻辑**：
```java
Gdx.input.setOnscreenKeyboardVisible(value, Input.OnscreenKeyboardType.Default);
```

**边界情况**：桌面平台通常无效果

### setupFontGenerators()
**可见性**：public abstract

**是否覆写**：否

**方法职责**：初始化字体生成器

**参数**：
- `pageSize` (int)：字体图集页面大小
- `systemFont` (boolean)：是否使用系统字体

**返回值**：void

**前置条件**：无

**副作用**：初始化字体系统

**核心实现逻辑**：
由子类实现，设置平台特定的字体生成器

**边界情况**：无

### getGeneratorForString()
**可见性**：protected abstract

**是否覆写**：否

**方法职责**：根据输入文本获取适当的字体生成器

**参数**：
- `input` (String)：输入文本

**返回值**：FreeTypeFontGenerator，字体生成器

**前置条件**：已调用setupFontGenerators

**副作用**：无

**核心实现逻辑**：
由子类实现，处理多语言字体选择

**边界情况**：无合适字体时返回null

### splitforTextBlock()
**可见性**：public abstract

**是否覆写**：否

**方法职责**：将文本分割为适合显示的块

**参数**：
- `text` (String)：要分割的文本
- `multiline` (boolean)：是否允许多行

**返回值**：String[]，分割后的文本块数组

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
由子类实现，处理平台特定的文本布局

**边界情况**：无

### resetGenerators()
**可见性**：public

**是否覆写**：否

**方法职责**：重置并可选重新初始化字体生成器

**参数**：
- `setupAfter` (boolean)：是否在重置后重新设置（默认true）

**返回值**：void

**前置条件**：已初始化字体系统

**副作用**：释放所有字体资源并重新初始化

**核心实现逻辑**：
1. 遍历所有生成器，释放字体和纹理资源
2. 清空缓存
3. 如果setupAfter为true，调用setupFontGenerators重新初始化

**边界情况**：未初始化时安全执行

### reloadGenerators()
**可见性**：public

**是否覆写**：否

**方法职责**：重新加载字体生成器（保留生成器但重置字体）

**参数**：无

**返回值**：void

**前置条件**：已初始化字体系统

**副作用**：释放字体资源但保留生成器

**核心实现逻辑**：
1. 释放所有字体资源
2. 重新创建PixmapPacker
3. 保留生成器实例

**边界 cases**：适用于配置更改但不需要完全重置的场景

### getFont()
**可见性**：public

**是否覆写**：否

**方法职责**：获取指定大小和样式的字体

**参数**：
- `size` (int)：字体大小
- `text` (String)：示例文本（用于字体选择）
- `flipped` (boolean)：是否垂直翻转（Shattered使用Y-down坐标系）
- `border` (boolean)：是否添加边框

**返回值**：BitmapFont，请求的字体

**前置条件**：已调用setupFontGenerators

**副作用**：可能创建新字体并缓存

**核心实现逻辑**：
1. 获取适当的生成器
2. 计算缓存键（考虑size, flipped, border）
3. 如果缓存中不存在，生成新字体：
   - 设置字体参数（大小、翻转、边框、渲染次数等）
   - 生成字体并设置缺失字符
   - 缓存字体
4. 返回缓存的字体

**边界情况**：
- 生成器为null时返回null
- 字体生成失败时捕获异常并返回null
- 缓存键使用负值表示翻转，大值偏移表示边框

## 8. 对外暴露能力

### 显式 API
- 平台检测（supportsFullScreen, supportsVibration, connectedToUnmeteredNetwork）
- 显示管理（updateDisplaySize, getSafeInsets, getDisplayCutout）
- 输入控制（vibrate, setOnscreenKeyboardVisible）
- 系统集成（openURI, setHonorSilentSwitch）
- 字体系统（getFont, resetGenerators, reloadGenerators）

### 内部辅助方法
- getGeneratorForString（受保护）
- 字体缓存管理

### 扩展入口
- 必须通过继承实现抽象方法
- 子类可以覆写具体方法提供平台特定行为

## 9. 运行机制与调用链

### 创建时机
- 游戏启动时创建具体平台子类实例
- 字体系统在需要时延迟初始化

### 调用者
- GameApplication（主应用类）
- UI组件（文本渲染、输入处理）
- 网络管理器（流量检测）
- 音频系统（静音开关集成）

### 被调用者
- LibGDX平台API
- FreeType字体库
- 系统原生功能

### 系统流程位置
- 平台抽象层，连接游戏逻辑和操作系统

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无

### 依赖的资源
- 系统字体文件
- 图形纹理内存
- 网络连接状态

### 中文翻译来源
不适用

## 11. 使用示例

### 基本用法
```java
// 获取平台支持实例（通常由GameApplication提供）
PlatformSupport platform = Game.platform;

// 检查网络连接
if (platform.connectedToUnmeteredNetwork()) {
    // 下载高质量资源
    downloadHDAssets();
}

// 触发振动反馈
if (platform.supportsVibration()) {
    platform.vibrate(100); // 振动100毫秒
}

// 打开外部链接
platform.openURI("https://shatteredpixel.com");

// 获取字体用于文本渲染
BitmapFont font = platform.getFont(16, "Sample text", true, true);
```

### 安全区域处理
```java
// 处理刘海屏和导航栏
RectF safeInsets = platform.getSafeInsets(PlatformSupport.INSET_ALL);
float leftMargin = safeInsets.left;
float topMargin = safeInsets.top;
// 调整UI元素位置以避开安全区域
uiPanel.setPosition(leftMargin, topMargin);
```

### 字体管理
```java
// 重置字体系统（例如语言切换后）
platform.resetGenerators();

// 获取不同样式的字体
BitmapFont regularFont = platform.getFont(14, "Hello", true, false);
BitmapFont borderedFont = platform.getFont(14, "World", true, true);
```

## 12. 开发注意事项

### 状态依赖
- 字体缓存是共享状态，影响内存使用
- 显示尺寸状态影响所有UI布局
- 网络状态可能动态变化

### 生命周期耦合
- 字体资源需要在应用退出时正确释放
- 显示尺寸变化需要及时通知UI系统
- 平台支持实例通常贯穿整个应用生命周期

### 常见陷阱
- 忘记检查supportsVibration()直接调用vibrate()
- 在未初始化字体系统时调用getFont()
- 假设所有平台都支持相同的功能集
- 字体缓存导致内存泄漏（未调用resetGenerators）
- 坐标系混淆（LibGDX是Y-up，Shattered是Y-down）

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可以添加更多平台特定功能（如推送通知、分享）
- 可以改进字体缓存策略（LRU、内存限制）
- 可以添加更细粒度的网络状态检测

### 不建议修改的位置
- 核心抽象方法（影响所有平台实现）
- 字体生成参数（影响视觉一致性）
- 坐标系处理逻辑（关键的游戏渲染基础）

### 重构建议
- 按TODO注释考虑将字体功能分离到独立类
- 可以添加异步字体加载支持
- 考虑使用现代字体渲染技术（如SDF字体）

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点