# 本地化指南

## 概述
Shattered Pixel Dungeon 支持多语言本地化。本指南介绍如何添加新的翻译文本和创建新的语言支持。

## 本地化系统架构

### 核心类
- **Messages**: 本地化消息管理器
- **MessagesBundle**: 资源包加载器
- **properties 文件**: 存储翻译文本

### 文件位置
```
core/assets/messages/
├── messages.properties        # 英文（默认）
├── messages_zh.properties     # 简体中文
├── messages_zh_TW.properties  # 繁体中文
├── messages_ja.properties     # 日语
├── messages_ko.properties     # 韩语
└── ...
```

---

## 添加新翻译

### 步骤 1：添加文本键

在代码中使用 `Messages.get()` 方法：

```java
String text = Messages.get(ClassName.class, "key_name");
```

### 步骤 2：添加翻译文本

在 `messages.properties` 中添加英文文本：

```properties
items.weapon.melee.long_sword.name=longsword
items.weapon.melee.long_sword.desc=A fairly reliable weapon, capable of solid damage.
```

在 `messages_zh.properties` 中添加中文翻译：

```properties
items.weapon.melee.long_sword.name=长剑
items.weapon.melee.long_sword.desc=一把可靠的武器，能造成稳定的伤害。
```

---

## 文本键命名规范

### 格式
```
<包路径>.<类名>.<属性>
```

### 示例

| 键 | 说明 |
|----|------|
| `actors.mobs.rat.name` | 怪物名称 |
| `actors.mobs.rat.description` | 怪物描述 |
| `items.potion.potionofhealing.name` | 物品名称 |
| `items.potion.potionofhealing.desc` | 物品描述 |
| `ui.challenges.title` | UI 标题 |
| `windows.wndsettings.title` | 窗口标题 |

### 常用属性后缀

| 后缀 | 用途 |
|------|------|
| `.name` | 名称 |
| `.desc` | 描述 |
| `.info` | 详细信息 |
| `.title` | 标题 |
| `.message` | 消息文本 |
| `.action` | 动作名称 |

---

## 格式化字符串

### 参数替换

```java
// 代码
String text = Messages.get(Hero.class, "you_now_have", itemName);

// properties 文件
actors.hero.hero.you_now_have=You now have %s.
```

### 多参数

```java
// 代码
String text = Messages.get(Weapon.class, "stats", damage, delay);

// properties 文件
items.weapon.weapon.stats=damage: %d, delay: %.1f
```

### 复数处理

```properties
# 使用复数形式
items.item.count_one=1 item
items.item.count_many=%d items
```

---

## 添加新语言

### 步骤 1：创建属性文件

在 `core/assets/messages/` 目录创建新文件：
```
messages_<语言代码>.properties
```

### 步骤 2：注册语言

在 `Languages.java` 中添加语言枚举：

```java
public enum Language {
    ENGLISH("english",      "en", Status.RELEASED),
    CHINESE("chinese",      "zh", Status.RELEASED),
    CHINESE_TW("chinese_tw","zh_TW", Status.RELEASED),
    NEW_LANG("new_lang",    "xx", Status.UNRELEASED);  // 新语言
    
    // ...
}
```

### 步骤 3：复制并翻译

1. 复制 `messages.properties` 内容
2. 翻译所有文本条目
3. 保存为 UTF-8 编码

---

## 在代码中使用本地化

### 基本用法

```java
// 获取当前语言的文本
String name = Messages.get(getClass(), "name");

// 格式化文本
String desc = Messages.get(getClass(), "desc", value1, value2);

// 获取带类名的文本
String text = Messages.get(SomeClass.class, "message");
```

### 条件文本

```java
// 根据条件选择不同的文本
String key = condition ? "special_desc" : "normal_desc";
String desc = Messages.get(getClass(), key);
```

### 大写处理

```java
// 首字母大写
String capitalizedName = Messages.capitalize(Messages.get(getClass(), "name"));
```

---

## 调试本地化

### 检查缺失翻译

```java
// 启用调试模式查看缺失的键
// 在开发环境中，缺失的键会显示为 "???key_name???"
```

### 测试语言切换

```java
// 临时切换语言测试
Messages.setup(Languages.Language.CHINESE);
```

---

## 最佳实践

1. **使用完整键名**: 始终使用完整的包路径作为键前缀
2. **避免硬编码文本**: 所有显示文本都应通过 Messages 获取
3. **保持键名一致**: 同类内容使用相同的属性后缀
4. **测试所有语言**: 确保格式化字符串在各语言中都正确
5. **UTF-8 编码**: 确保属性文件使用 UTF-8 编码保存

---

## 特殊情况

### 动态生成的键

```java
// 使用 format 生成动态键
String key = String.format("items.rings.ringof%s.name", gemType);
String name = Messages.get(Ring.class, key);
```

### HTML 格式文本

```properties
# 使用 HTML 标签
items.weapon.enchantment.desc=This weapon is <font color="cyan">enchanted</font>.
```

### 换行处理

```properties
# 使用 \n 换行
items.scroll.desc=Line 1\nLine 2\nLine 3
```

---

## 相关资源

- [注册指南](registration-guide.md) - 内容注册流程
- [项目翻译页面](https://explore.transifex.com/shattered-pixel/shattered-pixel-dungeon/)