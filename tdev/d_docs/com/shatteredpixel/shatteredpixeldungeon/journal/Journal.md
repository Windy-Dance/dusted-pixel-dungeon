# Journal.java - 日记主入口

## 概述
`Journal` 类是日记系统的中央枢纽，负责全局日记数据的保存和加载。它协调 Catalog（物品目录）、Bestiary（敌人图鉴）和 Document（文档系统）的持久化操作。

## 核心功能

### 全局数据管理
- **文件存储**：使用 `journal.dat` 作为持久化文件
- **懒加载**：仅在首次需要时加载全局数据（`loadGlobal()`）
- **条件保存**：仅在数据有变化时才执行保存操作（`saveNeeded` 标志）

### 数据协调
- 统一管理三个子系统的序列化：
  - `Catalog.store()/restore()` - 物品目录数据
  - `Bestiary.store()/restore()` - 敌人图鉴数据  
  - `Document.store()/restore()` - 文档页面数据

## 主要方法

### loadGlobal()
```java
public static void loadGlobal()
```
- 检查是否已加载（避免重复加载）
- 从 `journal.dat` 文件读取 Bundle 数据
- 调用各子系统的 `restore()` 方法恢复数据
- 设置 `loaded = true` 标志

### saveGlobal()
```java
// 基础版本
public static void saveGlobal()

// 强制保存版本  
public static void saveGlobal(boolean force)
```
- 检查是否需要保存（`saveNeeded` 或 `force` 参数）
- 创建新的 Bundle 对象
- 调用各子系统的 `store()` 方法保存数据
- 将 Bundle 写入 `journal.dat` 文件
- 重置 `saveNeeded = false`

## 使用模式

### 自动保存触发
各子系统在数据变更时自动设置 `Journal.saveNeeded = true`：
```java
// Catalog.java 示例
public static void setSeen(Class<?> cls){
    // ... 更新逻辑
    Journal.saveNeeded = true;  // 触发自动保存
}
```

### 手动保存调用
通常在游戏会话结束或重要里程碑时调用：
```java
// 游戏退出时
Journal.saveGlobal();

// 完成重要操作后
Journal.saveGlobal(true); // 强制保存
```

## 错误处理
- 文件读取失败时创建空的 Bundle（确保游戏正常启动）
- 文件写入失败时捕获异常并报告（不影响游戏流程）
- 使用 `ShatteredPixelDungeon.reportException()` 记录I/O错误

## 设计原则

### 单一职责
- 专注数据持久化，不处理业务逻辑
- 各子系统负责自己的数据结构和验证

### 性能优化
- 避免不必要的磁盘I/O操作
- 使用增量保存策略
- 异步保存考虑（虽然当前实现是同步的）

### 可靠性
- 容错设计（文件不存在时正常工作）
- 异常安全（不会因保存失败导致游戏崩溃）
- 数据一致性（原子性保存操作）

## 依赖关系
- **核心依赖**：`Bundle`、`FileUtils`（文件操作）
- **子系统依赖**：`Catalog`、`Bestiary`、`Document`
- **错误报告**：`ShatteredPixelDungeon.reportException()`