# WndGameInProgress 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndGameInProgress.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 190 |

## 2. 类职责说明
WndGameInProgress 是已保存游戏信息窗口，用于显示当前加载的游戏进度摘要，并提供继续游戏或删除存档的选项。

## 7. 方法详解
- **构造函数 WndGameInProgress()**: 创建游戏进度窗口，从 SavedGameData 中读取当前游戏的摘要信息。
- **信息展示**: 显示英雄的基本属性（力量、生命值、经验值）、收集的金币、探索的最深深度以及地牢种子信息。
- **操作选项**: 提供 CONTINUE（继续）和 ERASE（删除）两个主要按钮，分别用于继续游戏或删除存档。
- **数据整合**: 从多个数据源（Statistics、Dungeon 等）整合游戏进度信息，确保显示内容的准确性。
- **特殊模式处理**: 正确处理每日挑战、自定义种子等特殊游戏模式的信息显示。
- **布局管理**: 使用固定宽度设计，通过 statSlot() 方法添加统计信息行，确保信息清晰易读。