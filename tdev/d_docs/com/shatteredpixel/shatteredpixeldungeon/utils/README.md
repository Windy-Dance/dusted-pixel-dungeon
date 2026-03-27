# 工具包 (Utils Package)

## 概述

`utils` 包包含了 Shattered Pixel Dungeon 游戏中三个独立的实用工具类，分别处理节日事件、游戏日志和地牢种子管理。这些工具类提供了游戏核心功能的支持。

## 文件结构

- `Holiday.java` - 节日事件管理
- `GLog.java` - 游戏日志系统  
- `DungeonSeed.java` - 地牢种子管理

---

## Holiday.java

### 类描述
`Holiday` 枚举类管理基于时间的节日事件，为游戏提供季节性内容。它包含日期检测算法和缓存机制，避免在游戏中重复计算节日。

### 枚举值

支持 10 种节日状态，覆盖每年约 61-62 天的节日时间：

| 节日 | 描述 |
|------|------|
| `NONE` | 无特殊节日 |
| `LUNAR_NEW_YEAR` | 农历新年 |
| `APRIL_FOOLS` | 愚人节 |
| `EASTER` | 复活节 |
| `PRIDE` | 骄傲月 |
| `SHATTEREDPD_BIRTHDAY` | Shattered Pixel Dungeon 生日 |
| `HALLOWEEN` | 万圣节 |
| `PD_BIRTHDAY` | Pixel Dungeon 生日 |
| `WINTER_HOLIDAYS` | 冬季节日（圣诞节/新年） |
| `NEW_YEARS` | 元旦 |

### 主要方法

| 方法 | 描述 |
|------|------|
| `getCurrentHoliday()` | 返回当前节日（使用缓存） |
| `getHolidayForDate(GregorianCalendar)` | 核心逻辑：根据日期确定节日 |
| `isLunarNewYear(int year, int dayOfYear)` | 硬编码查找表（2020-2100年） |
| `isEaster(int year, int dayOfYear, boolean isLeapYear)` | 使用匿名格里高利算法计算复活节 |
| `clearCachedHoliday()` | 清除节日缓存（在游戏启动/场景切换时调用） |

### 节日计算算法

- **农历新年**: 使用预计算的硬编码表（2020-2100年）
- **复活节**: 实现匿名格里高利算法（Anonymous Gregorian Algorithm）
- **其他节日**: 基于固定日期或日期范围

### 缓存机制

为了避免在游戏中频繁重新计算节日，使用静态缓存：
- 缓存键：`(year, dayOfYear)` 组合
- 缓存清理：在游戏启动或主场景切换时自动清理

---

## GLog.java

### 类描述
`GLog` 是游戏事件日志系统，通过信号机制分发格式化消息，并支持不同严重性/优先级前缀。

### 主要组件

| 组件 | 描述 |
|------|------|
| `TAG = "GAME"` | DeviceCompat 日志的标签 |
| `POSITIVE = "++ "` | 正面消息前缀 |
| `NEGATIVE = "-- "` | 负面消息前缀 |
| `WARNING = "** "` | 警告消息前缀 |
| `HIGHLIGHT = "@@ "` | 高亮消息前缀 |
| `update` | `Signal<String>` 供订阅者接收日志消息 |

### 主要方法

| 方法 | 描述 |
|------|------|
| `i(String text, Object... args)` | 信息日志（支持格式化字符串） |
| `p(String text, Object... args)` | 正面消息（前缀 `++ `） |
| `n(String text, Object... args)` | 负面消息（前缀 `-- `） |
| `w(String text, Object... args)` | 警告消息（前缀 `** `） |
| `h(String text, Object... args)` | 高亮消息（前缀 `@@ `） |
| `newLine()` | 分发换行事件 |

### 信号订阅模式

`GLog` 使用观察者模式：
```java
// 订阅日志消息
GLog.update.add(new Callback<String>() {
    @Override
    public void call(String message) {
        // 处理日志消息
        gameUI.showMessage(message);
    }
});

// 发送日志
GLog.p("获得了一个新的物品！");
GLog.w("生命值很低！");
```

### 消息类型用途

- **正面消息 (`p`)**: 获得物品、升级、完成目标等积极事件
- **负面消息 (`n`)**: 受到伤害、失败、损失物品等消极事件  
- **警告消息 (`w`)**: 危险警告、重要提醒等
- **高亮消息 (`h`)**: 特别重要的信息，需要用户注意
- **信息消息 (`i`)**: 一般信息，无特殊前缀

---

## DungeonSeed.java

### 类描述
`DungeonSeed` 提供地牢种子生成、验证和代码转换工具。种子用于确定性地生成地牢布局。

### 主要常量

| 常量 | 值 | 描述 |
|------|-----|------|
| `TOTAL_SEEDS` | 5,429,503,678,976L | 总可能种子数（26^9） |

### 种子代码格式

种子代码格式：`AAA-BBB-CCC`
- 每个字符为 A-Z（大写）
- 表示 base-26 数字系统
- 易于分享和输入
- 排除元音字母（A, E, I, O, U）以避免意外单词

### 主要方法

| 方法 | 描述 |
|------|------|
| `randomSeed()` | 生成随机种子（排除元音字母） |
| `convertFromCode(String code)` | 解析种子代码 `@@@-@@@-@@@` 为 long 值（base-26） |
| `convertToCode(long seed)` | 将 long 种子转换为格式化代码 |
| `convertFromText(String inputText)` | 灵活解析器：尝试代码 → 数字 → 文本哈希 |
| `formatText(String inputText)` | 标准化有效代码，否则返回原文本 |

### 种子解析流程

`convertFromText` 方法的解析顺序：
1. 首先尝试解析为种子代码格式（`@@@-@@@-@@@`）
2. 如果失败，尝试解析为纯数字（十进制或十六进制）
3. 如果仍然失败，对输入文本进行哈希生成种子

### 使用示例

```java
// 生成随机种子
long seed = DungeonSeed.randomSeed();

// 转换为可分享的代码
String code = DungeonSeed.convertToCode(seed); // "BCD-EFG-HIJ"

// 从代码恢复种子
long restoredSeed = DungeonSeed.convertFromCode(code);

// 从任意文本生成种子
long textSeed = DungeonSeed.convertFromText("my_custom_seed");

// 验证并格式化输入
String formatted = DungeonSeed.formatText("abc-def-ghi"); // 标准化为大写
```

### 安全考虑

- **元音排除**: 随机种子生成时排除元音字母，避免生成不当单词
- **输入验证**: 严格验证种子代码格式
- **回退机制**: 提供灵活的文本到种子转换，确保任何输入都能生成有效种子

---

## 工具包架构总结

这三个工具类各自独立，但都遵循以下设计原则：

1. **静态工具类**: 所有方法都是静态的，无需实例化
2. **单一职责**: 每个类专注于一个特定功能领域
3. **无状态操作**: 大部分方法是无状态的纯函数
4. **错误容忍**: 提供合理的默认值和回退机制
5. **性能优化**: 使用缓存和高效的算法实现

这些工具类被游戏的各个部分广泛使用，为游戏的核心体验提供了基础支持。