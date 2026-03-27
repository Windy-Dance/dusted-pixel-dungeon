# WndDailies 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndDailies.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 147 |

## 2. 类职责说明
WndDailies 是每日挑战分数历史窗口，用于展示玩家在不同日期完成的每日挑战及其获得的分数，支持点击查看详细回放信息。

## 7. 方法详解
- **构造函数 WndDailies()**: 创建每日挑战历史窗口，从 DailyDungeon 模块读取所有历史记录。
- **数据展示**: 使用滚动列表显示日期、分数和完成状态，按时间倒序排列（最新在前）。
- **交互功能**: 点击历史记录条目可以查看该次挑战的详细回放信息（通过 WndRanking 窗口）。
- **视觉反馈**: 根据完成状态使用不同颜色标识（成功/失败），高分记录可能有特殊标记。
- **布局管理**: 支持长列表的滚动显示，确保所有历史记录都可访问。
- **数据刷新**: 每次打开窗口时都会刷新数据，确保显示最新的挑战历史。