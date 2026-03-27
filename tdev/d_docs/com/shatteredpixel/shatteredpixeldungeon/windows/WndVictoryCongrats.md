# WndVictoryCongrats 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndVictoryCongrats.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 143 |

## 2. 类职责说明
WndVictoryCongrats 是胜利祝贺窗口，在玩家成功完成游戏后显示，包含祝贺消息和当前游戏模式的相关信息。

## 7. 方法详解
- **构造函数 WndVictoryCongrats()**: 创建胜利祝贺窗口，自动检测并显示当前游戏的相关信息。
- **游戏模式信息**: 显示启用的挑战模式、自定义种子、每日挑战等特殊游戏模式的信息。
- **链接功能**: 提供链接到支持者场景（SupporterScene）的按钮，鼓励玩家支持开发者。
- **视觉设计**: 使用庆祝性的视觉元素和积极的语言，营造胜利的喜悦氛围。
- **简洁布局**: 界面简洁明了，专注于祝贺信息和相关链接，避免过多复杂元素。
- **动态内容**: 根据实际的游戏完成情况动态调整显示的内容，确保信息的准确性。