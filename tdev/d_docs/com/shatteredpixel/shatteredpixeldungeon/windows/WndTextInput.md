# WndTextInput 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndTextInput.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 236 |

## 2. 类职责说明
WndTextInput 是文本输入对话框，提供用户输入文本的功能，支持单行和多行输入模式，并集成复制/粘贴功能。

## 7. 方法详解
- **构造函数 WndTextInput(String title, String initialValue, int maxLength, boolean multiLine)**: 创建文本输入窗口，支持自定义标题、初始值、最大长度和输入模式。
- **输入控件**: 使用 TextField 组件处理用户输入，支持键盘和虚拟键盘输入。
- **复制/粘贴**: 集成系统剪贴板功能，提供复制和粘贴按钮。
- **回调机制**: 通过重写 onSelect(boolean pressedOK, String text) 方法处理输入完成事件。
- **输入验证**: 支持最大长度限制和特殊字符过滤。
- **布局管理**: 根据输入模式（单行/多行）自动调整窗口尺寸和控件布局。
- **平台适配**: 针对不同平台（桌面/移动）优化输入体验。