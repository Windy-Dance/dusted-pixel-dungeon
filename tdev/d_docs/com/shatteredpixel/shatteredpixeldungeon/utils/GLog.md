# GLog.java - 游戏日志系统

## 概述
`GLog` 类是《破碎像素地牢》的游戏日志工具，提供带样式的日志消息功能和信号分发系统。它不仅记录游戏事件，还通过信号系统将消息传递给UI组件进行显示。

## 核心功能

### 样式化日志类型
系统定义四种标准日志样式：

- **POSITIVE (`++ `)** - 积极/成功消息（如获得物品、升级）
- **NEGATIVE (`-- `)** - 消极/失败消息（如受到伤害、物品损坏）  
- **WARNING (`** `)** - 警告消息（如低生命值、危险状态）
- **HIGHLIGHT (`@@ `)** - 高亮重要消息（如Boss出现、关键事件）

### 信号分发系统
- 使用 `Signal<String>` 实现观察者模式
- UI组件可以注册监听器接收日志消息
- 支持多订阅者同时接收相同消息

### 设备日志集成
- 同时输出到设备日志（Android logcat等）
- 使用统一的 `GAME` 标签便于过滤
- 保持开发调试和玩家体验的一致性

## 主要API

### 基础日志方法
```java
// 信息日志（无样式前缀）
public static void i(String text, Object... args)

// 积极日志（++ 前缀）
public static void p(String text, Object... args)

// 消极日志（-- 前缀）  
public static void n(String text, Object... args)

// 警告日志（** 前缀）
public static void w(String text, Object... args)

// 高亮日志（@@ 前缀）
public static void h(String text, Object... args)
```

### 特殊功能
```java
// 发送换行符
public static void newLine()

// 直接分发信号（高级用法）
public static Signal<String> update = new Signal<>();
```

### 参数格式化
所有日志方法都支持可变参数格式化：
```java
GLog.p("获得了 %s 个金币", goldAmount);
GLog.w("生命值低于 %d%%", healthPercentage);
```

格式化使用 `Messages.format()` 方法，支持本地化。

## 技术实现

### 本地化集成
- 自动调用 `Messages.format()` 进行字符串格式化
- 支持多语言的消息模板
- 保持与游戏其他文本的一致性

### 信号系统
- 基于 `com.watabou.utils.Signal` 实现
- 线程安全的消息分发
- 高效的订阅者管理

### 内存管理
- 静态方法设计，无实例开销
- 字符串常量池优化
- 避免不必要的对象创建

## 游戏集成

### UI消息显示
- 主游戏界面的消息栏接收日志信号
- 消息按时间顺序显示
- 不同样式对应不同颜色和图标

### 游戏事件记录
- 战斗结果（命中、闪避、暴击）
- 物品交互（拾取、使用、装备）
- 状态变化（中毒、燃烧、祝福等）
- 关卡事件（发现秘密、触发陷阱等）

### 成就和统计
- 某些成就条件基于日志事件
- 游戏统计信息通过日志收集
- 调试信息用于问题诊断

## 使用模式

### 基本使用
```java
// 简单消息
GLog.i("进入新的地牢层");

// 带参数的消息
GLog.p("击败了 %s！", enemy.name());

// 不同类型的反馈
if (damage > 0) {
    GLog.n("受到了 %d 点伤害", damage);
} else {
    GLog.p("成功闪避攻击！");
}
```

### UI集成
```java
// 在游戏场景中注册监听器
GLog.update.listen(new Signal.Listener<String>() {
    @Override
    public void onSignal(String message) {
        if (message.equals(GLog.NEW_LINE)) {
            messageBar.newLine();
        } else {
            messageBar.addMessage(message);
        }
    }
});
```

### 调试用途
```java
// 开发时的详细日志
GLog.i("DEBUG: 当前状态 = %s", gameState.toString());
```

## 性能考虑

### 高效实现
- 静态方法调用，无对象分配
- 字符串格式化延迟执行（仅在需要时）
- 信号分发使用高效的回调机制

### 内存友好
- 避免字符串拼接创建临时对象
- 重用格式化参数数组
- 最小化的内存占用

### 线程安全
- 信号分发在主线程执行
- 避免跨线程同步问题
- 安全的并发访问

## 设计模式

### 单例模式
- 整个应用共享一个日志系统
- 通过静态方法提供全局访问
- 统一的日志格式和行为

### 观察者模式
- 日志生产者与消费者解耦
- 支持多个UI组件同时监听
- 灵活的消息路由

### 门面模式
- 简化复杂的日志和信号API
- 提供一致的使用接口
- 隐藏底层实现细节

## 扩展性

### 自定义日志类型
虽然当前只有4种样式，但可以轻松扩展：
```java
public static final String CUSTOM_STYLE = "!! ";
public static void custom(String text, Object... args) {
    i(CUSTOM_STYLE + text, args);
}
```

### 高级信号处理
- 可以添加消息过滤逻辑
- 支持优先级队列
- 实现消息批处理

### 外部集成
- 可以将日志发送到远程分析服务
- 支持文件日志记录（调试版本）
- 集成崩溃报告系统

## 最佳实践

### 消息设计原则
- **简洁明了**：消息应该简短且信息丰富
- **一致性**：相同类型的事件使用相同的格式
- **用户友好**：避免技术术语，使用玩家易懂的语言

### 性能意识
- 避免在性能关键路径上频繁调用日志
- 使用合适的日志级别（不要滥用高亮消息）
- 考虑消息的显示频率和重要性

### 本地化考虑
- 所有消息都应该支持本地化
- 避免硬编码数字和单位
- 考虑不同语言的语法结构差异

## 调试和维护

### 开发者工具
- 可以禁用特定类型的消息（调试时）
- 支持详细的调试日志模式
- 提供日志级别控制

### 错误处理
- 格式化错误被安全捕获
- 消息分发失败不会影响游戏流程
- 提供详细的错误上下文信息

### 维护指南
- 保持消息键的一致性
- 定期清理未使用的日志调用
- 确保所有新功能都有适当的日志反馈