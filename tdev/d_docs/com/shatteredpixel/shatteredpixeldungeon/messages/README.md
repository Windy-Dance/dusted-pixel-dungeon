# 消息包 (Messages Package)

## 概述

`messages` 包是 Shattered Pixel Dungeon 游戏的 **国际化 (i18n) 核心**，为游戏提供多语言支持。该包封装了 libGDX 的 `I18NBundle` 系统，提供了简单易用的 API 来获取本地化文本字符串，并支持 20 多种语言。

## 文件结构

- `Messages.java` - 国际化包装类
- `Languages.java` - 支持的语言枚举定义

---

## Messages.java

### 类描述
`Messages` 是核心国际化包装类，封装了 libGDX 的 `I18NBundle` 系统。它提供了一个简单的 API，用于在整个游戏中检索本地化文本字符串。

### 主要方法

| 方法/字段 | 类型 | 描述 |
|-----------|------|------|
| `setup(Languages lang)` | static | 为指定语言初始化 I18N 资源包 |
| `lang()` | static | 返回当前的 `Languages` 枚举值 |
| `locale()` | static | 返回当前的 `Locale` 对象 |
| `get(String key, Object...args)` | static | 通过键获取本地化字符串（支持可选格式化参数） |
| `get(Class c, String k, Object...args)` | static | 使用基于类的键解析获取本地化字符串 |
| `getFromBundle(String key)` | static | 从资源包链中获取字符串的内部方法 |
| `format(String format, Object...args)` | static | 使用当前区域设置进行字符串格式化 |
| `decimalFormat(String format, double number)` | static | 区域设置感知的十进制数字格式化 |
| `capitalize(String str)` | static | 使用当前区域设置大写首字符 |
| `titleCase(String str)` | static | 转换为标题大小写（英文）或句子大小写（其他语言） |
| `upperCase(String str)` / `lowerCase(String str)` | static | 区域设置感知的大小写转换 |
| `NO_TEXT_FOUND` | 常量 | 当翻译键缺失时的回退字符串 |

### 基于类的键解析模式

`Messages` 类使用基于类名的键解析机制，例如：
```java
// Messages.get(MyClass.class, "name") 会解析为 "myclass.name"
Messages.get(Warrior.class, "desc"); // 解析为 "warrior.desc"

// 这允许对象请求简单的键如 "name"，而无需指定完整路径
```

### 资源文件结构
- 资源文件通常命名为 `strings.properties`
- 支持语言特定的变体，如 `strings_zh.properties`（中文）
- 键值对格式：`key=value`

### 使用示例
```java
// 基本用法
String playerName = Messages.get("player_name");

// 带参数的格式化
String damageMsg = Messages.get("damage_dealt", hero.damage(), enemy.name());

// 基于类的键
String warriorDesc = Messages.get(Warrior.class, "desc");

// 字符串操作
String title = Messages.titleCase("hello world"); // "Hello World"
String upper = Messages.upperCase("text"); // "TEXT"（根据区域设置）
```

---

## Languages.java

### 类描述
`Languages` 枚举定义了游戏中所有支持的语言及其元数据，包括翻译状态、翻译者署名和审校者署名。

### 主要组件

#### Languages 枚举值
支持 20 多种语言，包括：
- `ENGLISH` - 英语
- `CHINESE` - 中文（简体）
- `KOREAN` - 韩语  
- `RUSSIAN` - 俄语
- `SPANISH` - 西班牙语
- `PORTUGUESE` - 葡萄牙语
- `FRENCH` - 法语
- `GERMAN` - 德语
- `JAPANESE` - 日语
- 以及其他多种语言

#### Status 枚举（嵌套）
翻译状态定义：
- `X_UNFINISH` - 翻译完成度 < 100%
- `__UNREVIEW` - 翻译完成 100% 但未经过审校
- `O_COMPLETE` - 翻译完成 100% 且已审校

### 主要方法

| 方法/字段 | 类型 | 描述 |
|-----------|------|------|
| `nativeName()` | instance | 返回语言在其原生脚本中的名称（例如："简体中文", "日本語"） |
| `code()` | instance | 返回 ISO 语言代码（例如："en", "zh", "ja"） |
| `status()` | instance | 返回翻译 `Status` |
| `reviewers()` / `translators()` | instance | 返回贡献者姓名数组 |
| `matchLocale(Locale locale)` | static | 将 Java `Locale` 匹配到 `Languages` 枚举值 |
| `matchCode(String code)` | static | 将语言代码字符串匹配到 `Languages` 枚举值 |

### 贡献者署名系统

每个语言条目都包含：
- **翻译者 (Translators)**: 负责翻译的贡献者列表
- **审校者 (Reviewers)**: 负责审校的贡献者列表
- **状态跟踪**: 翻译完成度和质量状态

### 自动区域检测和回退

系统支持：
- 自动检测用户设备的区域设置
- 智能回退机制（如果首选语言不可用，则回退到英语）
- 手动语言选择覆盖

### 架构优势

1. **集中化配置**: 所有语言信息在一个地方管理
2. **贡献者归属**: 自动显示翻译和审校贡献者
3. **状态跟踪**: 清晰的翻译完成度指示
4. **扩展性**: 易于添加新语言支持
5. **本地化感知**: 所有字符串操作都考虑当前区域设置

### 实际应用场景

- 游戏启动时自动检测并设置语言
- 设置菜单中显示可用语言列表
- 显示翻译贡献者信息在关于页面
- 根据翻译状态决定是否显示某些功能
- 动态切换语言而无需重启游戏