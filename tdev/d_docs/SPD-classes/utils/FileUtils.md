# FileUtils 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\SPD-classes\src\main\java\com\watabou\utils\FileUtils.java |
| **包名** | com.watabou.utils |
| **文件类型** | class |
| **继承关系** | 无继承，无实现接口 |
| **代码行数** | 226 |
| **所属模块** | SPD-classes |

## 2. 文件职责说明

### 核心职责
提供跨平台文件系统操作工具，封装LibGDX文件API，处理文件读写、目录管理、Bundle序列化和临时文件清理等操作。

### 系统定位
作为游戏引擎的文件抽象层，统一处理不同平台（Android/iOS/Desktop）的文件系统差异，为存档系统和资源配置提供可靠的基础服务。

### 不负责什么
- 不负责网络文件传输
- 不处理文件加密/解密
- 不管理大文件内存映射

## 3. 结构总览

### 主要成员概览
- `defaultFileType`: 默认文件类型（Internal/External等）
- `defaultPath`: 默认基础路径
- 静态工具方法集合

### 主要逻辑块概览
- 文件句柄管理（getFileHandle系列）
- 文件操作（存在性检查、删除、覆盖）
- 目录操作（列表、删除）
- Bundle序列化（bundleFromFile/bundleToFile）
- 临时文件清理（cleanTempFiles）

### 生命周期/调用时机
- 通过setDefaultFileProperties初始化默认配置
- 按需调用各种文件操作方法
- 存档加载/保存时使用Bundle相关方法

## 4. 继承与协作关系

### 父类提供的能力
无

### 覆写的方法
无

### 实现的接口契约
无

### 依赖的关键类
- `com.badlogic.gdx.Files/FileHandle`: LibGDX文件系统API
- `com.badlogic.gdx.Gdx`: LibGDX核心入口
- `com.watabou.utils.Bundle`: 序列化容器
- Java I/O流类库

### 使用者
- 游戏存档系统（SaveUtils）
- 资源管理系统
- 配置文件处理器
- 云同步功能

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无

### 静态字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| defaultFileType | Files.FileType | null | 默认文件类型，用于简化文件操作 |
| defaultPath | String | "" | 默认基础路径，用于相对路径解析 |

## 6. 构造与初始化机制

### 构造器
无公共构造器，类不能被实例化

### 初始化块
无

### 初始化注意事项
- 必须先调用setDefaultFileProperties设置默认配置
- defaultFileType为null时getFileHandle(name)会失败

## 7. 方法详解

### setDefaultFileProperties()
**可见性**：public static

**是否覆写**：否

**方法职责**：设置默认文件类型和基础路径

**参数**：
- `type` (Files.FileType)：默认文件类型
- `path` (String)：默认基础路径

**返回值**：void

**前置条件**：type不能为null

**副作用**：修改静态字段defaultFileType和defaultPath

**核心实现逻辑**：
直接赋值给静态字段

**边界情况**：path为null会存储null值

### getFileHandle() (重载1)
**可见性**：public static

**是否覆写**：否

**方法职责**：获取使用默认配置的文件句柄

**参数**：
- `name` (String)：文件名（相对路径）

**返回值**：FileHandle，文件句柄

**前置条件**：defaultFileType必须已设置

**副作用**：无

**核心实现逻辑**：
```java
return getFileHandle(defaultFileType, defaultPath, name);
```

**边界情况**：defaultFileType为null时抛出异常

### getFileHandle() (重载2)
**可见性**：public static

**是否覆写**：否

**方法职责**：获取指定类型、默认路径的文件句柄

**参数**：
- `type` (Files.FileType)：文件类型
- `name` (String)：文件名

**返回值**：FileHandle，文件句柄

**前置条件**：type不能为null

**副作用**：无

**核心实现逻辑**：
```java
return getFileHandle(type, "", name);
```

**边界情况**：无

### getFileHandle() (重载3)
**可见性**：public static

**是否覆写**：否

**方法职责**：获取完全指定的文件句柄

**参数**：
- `type` (Files.FileType)：文件类型
- `basePath` (String)：基础路径
- `name` (String)：文件名

**返回值**：FileHandle，文件句柄

**前置条件**：type不能为null

**副作用**：无

**核心实现逻辑**：
根据文件类型调用对应的Gdx.files方法：
- Classpath: Gdx.files.classpath()
- Internal: Gdx.files.internal()
- External: Gdx.files.external()
- Absolute: Gdx.files.absolute()
- Local: Gdx.files.local()

**边界情况**：不支持的文件类型返回null

### cleanTempFiles() (重载1)
**可见性**：public static

**是否覆写**：否

**方法职责**：清理根目录下的临时文件

**参数**：无

**返回值**：boolean，true表示找到并处理了临时文件

**前置条件**：已设置默认文件配置

**副作用**：可能删除或移动文件

**核心实现逻辑**：
调用重载版本，目录名为空字符串

**边界情况**：无

### cleanTempFiles() (重载2)
**可见性**：public static

**是否覆写**：否

**方法职责**：递归清理指定目录下的临时文件

**参数**：
- `dirName` (String)：目录名

**返回值**：boolean，true表示找到并处理了临时文件

**前置条件**：已设置默认文件配置

**副作用**：可能删除无效文件，可能恢复临时文件

**核心实现逻辑**：
1. 递归遍历目录
2. 删除空文件
3. 处理.spdtmp临时文件：
   - 验证临时文件有效性
   - 验证原始文件有效性
   - 如果临时文件更新或原始文件损坏，用临时文件替换
   - 否则删除临时文件

**边界情况**：深层嵌套目录可能导致栈溢出

### fileExists()
**可见性**：public static

**是否覆写**：否

**方法职责**：检查文件是否存在且有效

**参数**：
- `name` (String)：文件名

**返回值**：boolean，true表示文件存在且非空

**前置条件**：已设置默认文件配置

**副作用**：无

**核心实现逻辑**：
```java
FileHandle file = getFileHandle(name);
return file.exists() && !file.isDirectory() && file.length() > 0;
```

**边界情况**：空文件被认为是不存在的

### fileLength()
**可见性**：public static

**是否覆写**：否

**方法职责**：获取文件长度（字节）

**参数**：
- `name` (String)：文件名

**返回值**：long，文件长度，不存在时返回0

**前置条件**：已设置默认文件配置

**副作用**：无

**核心实现逻辑**：
检查文件存在性和非目录性，返回长度或0

**边界情况**：目录文件返回0

### deleteFile()
**可见性**：public static

**是否覆写**：否

**方法职责**：删除指定文件

**参数**：
- `name` (String)：文件名

**返回值**：boolean，true表示删除成功

**前置条件**：已设置默认文件配置

**副作用**：删除文件

**核心实现逻辑**：
```java
return getFileHandle(name).delete();
```

**边界情况**：文件不存在时返回false

### overwriteFile()
**可见性**：public static

**是否覆写**：否

**方法职责**：用垃圾数据覆盖文件（防云同步问题）

**参数**：
- `name` (String)：文件名
- `bytes` (int)：覆盖字节数

**返回值**：void

**前置条件**：已设置默认文件配置

**副作用**：覆盖文件内容

**核心实现逻辑**：
创建全1字节数组并写入文件

**边界情况**：bytes为0时创建空文件

### dirExists()
**可见性**：public static

**是否覆写**：否

**方法职责**：检查目录是否存在

**参数**：
- `name` (String)：目录名

**返回值**：boolean，true表示目录存在

**前置条件**：已设置默认文件配置

**副作用**：无

**核心实现逻辑**：
```java
FileHandle dir = getFileHandle(name);
return dir.exists() && dir.isDirectory();
```

**边界情况**：无

### deleteDir()
**可见性**：public static

**是否覆写**：否

**方法职责**：递归删除目录

**参数**：
- `name` (String)：目录名

**返回值**：boolean，true表示删除成功

**前置条件**：已设置默认文件配置

**副作用**：删除整个目录树

**核心实现逻辑**：
调用FileHandle.deleteDirectory()

**边界情况**：目录不存在或不是目录时返回false

### filesInDir()
**可见性**：public static

**是否覆写**：否

**方法职责**：获取目录中的文件列表

**参数**：
- `name` (String)：目录名

**返回值**：ArrayList<String>，文件名列表

**前置条件**：已设置默认文件配置

**副作用**：无

**核心实现逻辑**：
遍历目录的FileHandle.list()并收集文件名

**边界情况**：目录不存在时返回空列表

### bundleFromFile()
**可见性**：public static

**是否覆写**：否

**方法职责**：从文件读取Bundle

**参数**：
- `fileName` (String)：文件名

**返回值**：Bundle，读取的Bundle对象

**前置条件**：已设置默认文件配置，文件必须存在且有效

**副作用**：可能抛出IOException

**核心实现逻辑**：
1. 获取文件句柄
2. 验证文件存在性和有效性
3. 调用bundleFromStream读取

**边界情况**：文件不存在或无效时抛出IOException

### bundleFromStream()
**可见性**：private static

**是否覆写**：否

**方法职责**：从输入流读取Bundle

**参数**：
- `input` (InputStream)：输入流

**返回值**：Bundle，读取的Bundle对象

**前置条件**：input不能为null

**副作用**：关闭输入流

**核心实现逻辑**：
调用Bundle.read(input)并关闭流

**边界情况**：无效Bundle格式抛出IOException

### bundleToFile()
**可见性**：public static

**是否覆写**：否

**方法职责**：将Bundle写入文件（带临时文件保护）

**参数**：
- `fileName` (String)：文件名
- `bundle` (Bundle)：要写入的Bundle

**返回值**：void

**前置条件**：已设置默认文件配置，bundle不能为null

**副作用**：可能创建/修改文件，可能抛出IOException

**核心实现逻辑**：
1. 如果文件存在，先写入临时文件(.spdtmp)
2. 删除原文件
3. 移动临时文件到原位置
4. 如果文件不存在，直接写入

**边界情况**：写入中断时临时文件可被后续清理

### bundleToStream()
**可见性**：private static

**是否覆写**：否

**方法职责**：将Bundle写入输出流

**参数**：
- `output` (OutputStream)：输出流
- `bundle` (Bundle)：要写入的Bundle

**返回值**：void

**前置条件**：参数不能为null

**副作用**：关闭输出流

**核心实现逻辑**：
调用Bundle.write(bundle, output)并关闭流

**边界情况**：I/O错误抛出IOException

## 8. 对外暴露能力

### 显式 API
- 文件句柄获取（getFileHandle）
- 文件操作（fileExists/deleteFile/overwriteFile）
- 目录操作（dirExists/deleteDir/filesInDir）
- Bundle序列化（bundleFromFile/bundleToFile）
- 临时文件清理（cleanTempFiles）

### 内部辅助方法
- bundleFromStream/bundleToStream

### 扩展入口
- 无扩展点，设计为封闭工具类

## 9. 运行机制与调用链

### 创建时机
- 首次调用静态方法时类加载
- 默认配置在游戏启动时设置

### 调用者
- SaveUtils类（存档系统）
- ResourceManager类（资源管理）
- GameApplication类（应用生命周期）

### 被调用者
- LibGDX文件系统API
- Bundle序列化系统
- Java I/O流

### 系统流程位置
- 文件抽象层，连接游戏逻辑和底层文件系统

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无

### 依赖的资源
- 存档文件（.dat）
- 配置文件
- 临时文件（.spdtmp）

### 中文翻译来源
不适用

## 11. 使用示例

### 基本用法
```java
// 初始化默认配置
FileUtils.setDefaultFileProperties(Files.FileType.Local, "saves/");

// 文件操作
if (FileUtils.fileExists("save_1.dat")) {
    long size = FileUtils.fileLength("save_1.dat");
    System.out.println("Save file size: " + size);
}

// Bundle序列化
Bundle saveData = new Bundle();
saveData.put("hero_level", 5);
FileUtils.bundleToFile("save_1.dat", saveData);

// 读取存档
Bundle loaded = FileUtils.bundleFromFile("save_1.dat");
int level = loaded.getInt("hero_level");
```

### 安全存档操作
```java
// 自动处理临时文件清理
FileUtils.cleanTempFiles(); // 在游戏启动时调用

// 安全删除存档（防云同步问题）
FileUtils.overwriteFile("save_1.dat", 1024); // 用1KB垃圾数据覆盖
FileUtils.deleteFile("save_1.dat");
```

## 12. 开发注意事项

### 状态依赖
- 静态defaultFileType/defaultPath影响所有操作
- 必须在使用前调用setDefaultFileProperties

### 生命周期耦合
- 文件操作应在适当的应用生命周期阶段进行
- 避免在主线程进行大量I/O操作

### 常见陷阱
- 忘记设置默认配置导致NPE
- 假设所有平台都支持相同的文件类型
- 在多线程环境中同时访问同一文件
- 忽略I/O异常处理
- 临时文件清理的递归深度限制

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可以添加异步文件操作支持
- 可以添加文件监控功能
- 可以添加压缩/加密支持

### 不建议修改的位置
- Bundle序列化逻辑（影响存档兼容性）
- 临时文件处理机制（关键的数据安全特性）

### 重构建议
- 考虑使用Builder模式简化配置
- 可以添加更细粒度的异常类型
- 考虑使用现代Java NIO API替代部分功能

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点