# WndSupportPrompt 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndSupportPrompt.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 96 |

## 2. 类职责说明
WndSupportPrompt 是支持提示窗口，用于鼓励玩家通过Patreon支持游戏开发者，提供直接链接到支持页面的按钮。

## 7. 方法详解
- **构造函数 WndSupportPrompt()**: 创建支持提示窗口，显示友好的支持请求消息和Patreon相关信息。
- **链接功能**: 提供"GO TO PATREON"按钮，点击后在默认浏览器中打开Patreon支持页面。
- **关闭选项**: 提供"CLOSE"按钮关闭窗口，并可选择不再显示此提示（设置 nag suppression）。
- **视觉设计**: 使用友好的语言和积极的视觉元素，避免给玩家带来压力或负罪感。
- **频率控制**: 集成nag系统，允许玩家选择不再显示此提示，尊重玩家的选择。
- **平台适配**: 在不同平台上正确处理外部链接的打开方式（桌面浏览器、移动应用等）。