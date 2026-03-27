# WndHardNotification 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndHardNotification.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends WndTitledMessage |
| 代码行数 | 84 |

## 2. 类职责说明
WndHardNotification 是强制通知窗口，用于显示重要的系统消息或警告，具有倒计时机制防止用户过快关闭。

## 7. 方法详解
- **构造函数 WndHardNotification(Image icon, String title, String message, int countdownSeconds)**: 创建强制通知窗口，接收图标、标题、消息和倒计时秒数参数。
- **倒计时机制**: OK按钮在指定的倒计时结束前保持禁用状态，防止用户匆忙确认重要消息。
- **返回键阻止**: 在倒计时期间禁用返回键（BACK/ESCAPE），强制用户等待并阅读完整消息。
- **视觉反馈**: 倒计时结束后按钮变为可用状态，并可能有视觉提示（如颜色变化）。
- **继承功能**: 继承 WndTitledMessage 的所有布局和显示功能，专注于强制通知的特殊行为。
- **适用场景**: 用于显示重要的更新通知、法律声明、使用条款等需要用户仔细阅读的内容。