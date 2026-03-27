# Messages.java - 本地化消息系统

## 概述
`Messages` 类是《破碎像素地牢》的本地化消息包装器，基于 libGDX 的 I18NBundle 系统实现。它提供简化的 API 来获取本地化字符串，并支持类基础的键名结构，使代码更加简洁和可维护。

## 核心设计思想

### 类基础键名系统
每个字符串资源的键名由类定义和局部值组合而成：
- 对象可以直接请求 `"name"` 而不是完整的路径如 `"items.weapon.enchantments.death.name"`
- 键名自动构建为 `ClassName.localKey` 格式
- 支持静态方法调用时传入 Class 对象

### 多 Bundle 管理
系统维护9个独立的属性文件 Bundle：
- `ACTORS` - 角色相关消息
- `ITEMS` - 物品相关消息  
- `JOURNAL` - 日记/图鉴相关消息
- `LEVELS` - 关卡相关消息
- `MISC` - 杂项消息
- `PLANTS` - 植物相关消息
- `SCENES` - 场景相关消息
- `UI` - 用户界面消息
- `WINDOWS` - 窗口相关消息

## 主要API

### 基础消息获取
```java
// 获取简单消息
String message = Messages.get("key");

// 获取带参数的消息（格式化）
String formatted = Messages.get("key", arg1, arg2);

// 使用类上下文获取消息
String name = Messages.get(MyClass.class, "name");
```

### 工具方法
```java
// 字符串格式化
public static String format(String key, Object... args)

// 数字格式化
public static String decimalFormat(String pattern, double value)

// 字符串大小写转换
public static String capitalize(String str)
public static String titleCase(String str) 
public static String upperCase(String str)
public static String lowerCase(String str)
```

### 语言管理
```java
// 设置当前语言
public static void setup(Languages lang)

// 获取当前语言
public static Languages lang()
```

## 技术实现细节

### Bundle 初始化
- 在 `setup()` 方法中按特定顺序加载所有 Bundle
- 使用 `FileUtils.withExtension()` 查找正确的属性文件
- 处理印尼语的特殊 locale (`id` vs `in`) 兼容性问题

### 消息查找机制
1. 首先在 ACTORS Bundle 中查找（历史原因）
2. 如果未找到，在所有其他 Bundle 中顺序查找
3. 返回第一个匹配的结果或原键名（作为后备）

### 参数格式化
- 使用 `String.format()` 进行参数替换
- 包装 `IllegalFormatException` 为运行时异常
- 支持多语言的数字和日期格式化

## 性能优化

### 缓存机制
- Bundle 对象在初始化后缓存重用
- 避免重复的文件I/O操作
- 使用高效的 HashMap 查找

### 内存管理  
- 按需加载 Bundle（首次使用时）
- 避免不必要的字符串创建
- 使用原始类型参数减少装箱开销

### 查找优化
- 早期返回优化（ACTORS Bundle 优先）
- 减少不必要的空值检查
- 批量操作的支持（虽然当前未使用）

## 错误处理和健壮性

### 容错设计
- Bundle 加载失败时使用空 Bundle（避免崩溃）
- 消息键不存在时返回原键名（便于调试）
- 格式化错误被安全捕获并记录

### 调试支持
- 开发模式下更容易识别缺失的翻译
- 错误日志包含完整的上下文信息
- 支持快速切换语言进行测试

## 与其他系统的集成

### 游戏对象集成
- 所有游戏对象（物品、敌人、技能等）都使用此系统
- 通过继承或组合获得本地化支持
- 支持动态消息生成（如伤害数字、状态效果）

### UI 系统集成
- 窗口标题、按钮文本、提示信息
- 动态内容更新（如背包物品描述）
- 多语言界面适配

### 保存系统
- 消息系统本身不处理保存
- 但保存的游戏数据可能包含需要本地化的文本
- 支持跨语言会话的兼容性

## 扩展性和维护

### 添加新 Bundle
1. 在枚举中添加新的 Bundle 类型
2. 创建对应的属性文件（如 `mybundle.properties`）
3. 在 `setup()` 方法中添加加载逻辑
4. 更新查找顺序（如果需要）

### 添加新语言
1. 创建对应语言的属性文件目录
2. 在 `Languages` 枚举中注册新语言
3. 确保所有 Bundle 都有对应的翻译文件
4. 测试特殊字符和格式化规则

### 维护最佳实践
- 保持键名的一致性和可读性
- 避免硬编码字符串（始终使用 Messages.get()）
- 定期清理未使用的键名
- 确保参数占位符与实际参数匹配

## 使用示例

### 基本用法
```java
// 简单消息
String welcome = Messages.get("welcome_message");

// 带参数的消息  
String damage = Messages.get("attack_damage", hero.damage(), enemy.name());

// 类上下文消息
public class Sword {
    public String name() {
        return Messages.get(Sword.class, "name");
    }
    
    public String desc() {
        return Messages.get(Sword.class, "desc");
    }
}
```

### 静态方法用法
```java
public class GameActions {
    public static String usePotion() {
        return Messages.get(GameActions.class, "use_potion");
    }
    
    public static String pickupItem(String itemName) {
        return Messages.format("pickup_item", itemName);
    }
}
```

### UI 集成
```java
// 窗口标题
window.title(Messages.get(MyWindow.class, "title"));

// 按钮文本  
button.text(Messages.get("confirm"));

// 动态提示
tooltip.text(Messages.get("damage_info", minDmg, maxDmg));
```

## 设计模式

### 单例模式
- 整个应用共享一个 Messages 实例
- 通过静态方法提供全局访问
- 状态通过静态字段维护

### 门面模式  
- 简化复杂的 I18NBundle API
- 提供统一的访问接口
- 隐藏底层实现细节

### 策略模式
- 不同的 Bundle 作为不同的策略
- 查找算法可配置（当前是固定顺序）
- 格式化策略支持不同语言规则

## 局限性和未来改进

### 当前局限
- 查找顺序固定，无法优化特定场景
- 不支持复数形式的复杂规则
- 缺少上下文相关的翻译（如同一词在不同场景的不同翻译）

### 可能的改进
- 添加缓存层进一步提升性能
- 支持更复杂的格式化规则
- 添加翻译缺失的自动检测工具
- 支持运行时语言切换的完整刷新