# 服务包 (Services Package)

## 概述

`services` 包提供了 Shattered Pixel Dungeon 游戏的平台特定服务层，主要包括游戏更新检查和新闻文章获取功能。该包采用了 **服务定位器模式 (Service Locator Pattern)**，通过静态工具类封装平台特定的实现细节，使核心游戏代码保持平台无关性。

## 文件结构

- `updates/Updates.java` - 游戏更新和评价服务
- `news/News.java` - 新闻文章服务

---

## Updates.java

### 类描述
`Updates` 是一个静态工具类，提供游戏更新检查和应用内评价功能。它作为 facade（外观模式）将调用委托给平台特定的 `UpdateService` 实现。

### 主要字段

| 字段 | 类型 | 描述 |
|------|------|------|
| `service` | `UpdateService` | 平台特定的实现（外部注入） |
| `updateData` | `AvailableUpdateData` | 缓存的更新信息（当可用时） |
| `lastCheck` | `Date` | 上次更新检查的时间戳 |
| `CHECK_DELAY` | `long` | 检查之间的最小时间间隔（1小时） |

### 主要方法

| 方法 | 用途 |
|------|------|
| `supportsUpdates()` | 返回是否支持更新服务 |
| `supportsUpdatePrompts()` | 检查平台是否支持更新通知 |
| `supportsBetaChannel()` | 检查是否支持 Beta 更新频道 |
| `checkForUpdate()` | 启动更新检查（带有速率限制，1小时冷却） |
| `launchUpdate(data)` | 启动平台的更新机制 |
| `updateAvailable()` / `updateData()` | 查询缓存的更新可用性 |
| `clearUpdate()` | 重置缓存的更新状态 |
| `supportsReviews()` | 检查平台是否支持评价提示 |
| `launchReview(callback)` / `openReviewURI()` | 处理应用内评价流程 |

### 使用示例
```java
// 检查是否有更新
if (Updates.supportsUpdates()) {
    Updates.checkForUpdate();
    if (Updates.updateAvailable()) {
        // 显示更新提示
    }
}

// 启动应用内评价
if (Updates.supportsReviews()) {
    Updates.launchReview(reviewCallback);
}
```

---

## News.java

### 类描述
`News` 是一个静态工具 class，提供新闻文章获取和显示功能。处理文章缓存、图标解析和连接回退逻辑。

### 主要字段

| 字段 | 类型 | 描述 |
|------|------|------|
| `service` | `NewsService` | 平台特定的实现（外部注入） |
| `articles` | `ArrayList<NewsArticle>` | 缓存的文章列表 |
| `lastCheck` | `Date` | 上次新闻检查的时间戳 |
| `CHECK_DELAY` | `long` | 检查之间的最小时间间隔（1小时） |

### 主要方法

| 方法 | 用途 |
|------|------|
| `supportsNews()` | 返回是否支持新闻服务 |
| `checkForNews()` | 获取文章（为旧版 Android 提供 HTTPS 回退） |
| `articlesAvailable()` | 检查文章是否已缓存且非空 |
| `articles()` | 返回缓存文章的副本 |
| `unreadArticles(lastRead)` | 计算比给定日期更新的文章数量 |
| `clearArticles()` | 重置缓存的文章状态 |
| `parseArticleIcon(article)` | 解析图标字符串为 Image 对象（支持 ICON:, ITEM:, 或资源格式） |
| `parseArticleDate(article)` | 将文章日期格式化为 YYYY-MM-DD 字符串 |

### 文章图标格式
- `ICON:icon_name` - 使用预定义图标
- `ITEM:item_class` - 使用物品图标  
- `asset_path` - 直接使用资源路径

### 架构模式

两个文件都遵循 **服务定位器模式**：
- 静态 `service` 字段持有平台特定的实现
- 静态工具方法委托给注入的服务
- 这允许核心游戏代码保持平台无关，而 Android/iOS/Desktop 实现处理平台特定逻辑

### 平台集成
在不同平台上，需要实现相应的服务接口：
- **Android**: 实现 `UpdateService` 和 `NewsService` 接口
- **iOS**: 实现相应的 Swift/Objective-C 服务
- **Desktop**: 实现桌面版本的服务逻辑

这些服务通常在应用程序启动时通过依赖注入进行初始化。