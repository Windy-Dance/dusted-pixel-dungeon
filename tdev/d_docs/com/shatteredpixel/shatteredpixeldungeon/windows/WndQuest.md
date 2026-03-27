# WndQuest 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndQuest.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends WndTitledMessage |
| 代码行数 | 32 |

## 2. 类职责说明
WndQuest 是NPC任务对话窗口，继承自 WndTitledMessage，用于显示非玩家角色（NPC）的任务相关对话内容。

## 7. 方法详解
- **构造函数 WndQuest(Char npc, String text)**: 创建任务对话窗口，接收NPC角色和对话文本作为参数。
- **标题栏设置**: 使用NPC的精灵图像作为图标，NPC名称作为标题文本。
- **消息显示**: 显示完整的任务对话内容，支持多行文本自动换行。
- **简洁实现**: 由于继承了 WndTitledMessage 的完整功能，实现非常简洁，专注于任务对话展示。
- **一致性**: 遵循游戏NPC对话的标准模式，确保用户界面的一致性。