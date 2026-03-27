# WndJournalItem 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndJournalItem.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends WndTitledMessage |
| 代码行数 | 45 |

## 2. 类职责说明
WndJournalItem 是日志条目详情窗口，继承自 WndTitledMessage，用于显示日志系统中单个条目的详细信息，包括图标、标题和完整描述。

## 7. 方法详解
- **构造函数 WndJournalItem(Image icon, String title, String message)**: 创建日志条目窗口，接收图标、标题和详细描述作为参数。
- **标题栏设置**: 使用指定的图标和标题文本创建标题栏，颜色使用标准的 TITLE_COLOR（黄色）。
- **描述显示**: 显示完整的条目描述内容，支持长文本自动换行。
- **交互限制**: 添加 PointerArea 覆盖整个窗口，在点击非窗口区域时关闭窗口，但禁用返回键以防止意外关闭。
- **简洁设计**: 专注于信息展示，不包含额外的操作按钮或复杂功能。