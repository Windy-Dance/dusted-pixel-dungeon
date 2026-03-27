# Document.java - 文档系统

## 概述
`Document` 枚举管理游戏中的所有可读文档，包括冒险指南、炼金术指南和各区域的背景故事文档。每个文档包含多个页面，跟踪每个页面的发现和阅读状态。

## 文档类型

### 核心指南
- **ADVENTURERS_GUIDE** (`ItemSpriteSheet.GUIDE_PAGE`) - 冒险者指南
  - 提供游戏机制指导（检查、突袭、识别、食物、炼金等）
  - 在游戏中逐步解锁新页面
  
- **ALCHEMY_GUIDE** (`ItemSpriteSheet.ALCH_PAGE`) - 炼金术指南  
  - 介绍炼金系统和配方
  - 包含药水、符石、炸弹、法术等内容

### 区域文档
- **INTROS** (`Icons.STAIRS`) - 各层介绍文档
  - 地牢、下水道、监狱、洞穴、城市、大厅的背景介绍
  
- **区域特定文档**：
  - `SEWERS_GUARD` - 下水道守卫笔记
  - `PRISON_WARDEN` - 监狱看守日志  
  - `CAVES_EXPLORER` - 洞穴探险家报告
  - `CITY_WARLOCK` - 城市术士记录
  - `HALLS_KING` - 大厅之王信件

## 页面状态管理

### 状态定义
- `NOT_FOUND` (0) - 未发现
- `FOUND` (1) - 已发现但未阅读  
- `READ` (2) - 已阅读

### 状态操作方法
```java
// 发现页面（NOT_FOUND → FOUND）
boolean findPage(String page)

// 阅读页面（FOUND/NOT_FOUND → READ）  
boolean readPage(String page)

// 删除页面（FOUND/READ → NOT_FOUND）
boolean deletePage(String page)

// 重置为未读（READ → FOUND）
boolean unreadPage(String page)
```

### 状态查询方法
```java
// 检查页面状态
boolean isPageFound(String page)
boolean isPageRead(String page)

// 批量查询
boolean anyPagesFound()  // 是否有任何页面被发现
boolean allPagesFound()  // 是否所有页面都被发现
```

## 内容本地化

### 标题和内容获取
- 使用 `Messages.get()` 进行本地化
- 动态构造键名：`documentName.pageName.title/body`

```java
// 获取文档标题
String title = document.title(); // Messages.get(document, "name.title")

// 获取页面标题和内容  
String pageTitle = document.pageTitle("Intro");
String pageBody = document.pageBody("Intro");
```

### 图标支持
- 为不同页面提供专用图标
- 冒险指南页面有特殊视觉效果
- 支持按页面动态切换图标

## 初始化配置

### 默认页面设置
- 调试模式下所有页面默认已阅读
- 正常模式下按游戏进度逐步解锁
- 每个文档预定义其包含的页面列表

### 示例配置
```java
static {
    // 冒险指南初始页面（获得指南书时解锁）
    ADVENTURERS_GUIDE.pagesStates.put(GUIDE_INTRO, debug ? READ : NOT_FOUND);
    ADVENTURERS_GUIDE.pagesStates.put(GUIDE_EXAMINING, debug ? READ : NOT_FOUND);
    // ...
    
    // 下水道守卫笔记（在下水道获得）
    SEWERS_GUARD.pagesStates.put("new_position", debug ? READ : NOT_FOUND);
    SEWERS_GUARD.pagesStates.put("dangerous", debug ? READ : NOT_FOUND);
    // ...
}
```

## 持久化

### 序列化格式
- 只保存非 `NOT_FOUND` 状态的页面
- 使用嵌套 Bundle 结构：`documents → documentName → pageName → state`

### 序列化方法
```java
public static void store(Bundle bundle)
public static void restore(Bundle bundle)
```

## 游戏集成

### 成就验证
- 页面状态变更时触发成就检查：
  ```java
  Badges.validateCatalogBadges();
  ```

### 自动保存
- 状态变更时设置保存标志：
  ```java
  Journal.saveNeeded = true;
  ```

## 使用示例

### 游戏内文档发现
```java
// 获得冒险指南书时
Document.ADVENTURERS_GUIDE.findPage(Document.GUIDE_INTRO);
Document.ADVENTURERS_GUIDE.findPage(Document.GUIDE_EXAMINING);

// 在下水道遇到守卫后
Document.SEWERS_GUARD.findPage("new_position");
Document.SEWERS_GUARD.findPage("dangerous");
```

### UI显示逻辑
```java
// 显示文档列表
for (Document doc : Document.values()) {
    if (doc.anyPagesFound()) {
        // 显示文档条目
    }
}

// 显示页面列表
for (String pageName : document.pageNames()) {
    int state = document.pagesStates.get(pageName);
    if (state > Document.NOT_FOUND) {
        // 显示页面条目，根据状态着色
    }
}
```

## 设计特点

### 枚举单例模式
- 利用Java枚举的天然单例特性
- 静态初始化块配置默认数据
- 类型安全的文档引用

### 灵活的页面系统
- 支持动态添加新页面
- 独立的状态跟踪机制
- 与本地化系统无缝集成

### 性能优化
- 按需加载（调试模式特殊处理）
- 高效的状态存储（LinkedHashMap保持顺序）
- 最小化持久化数据量