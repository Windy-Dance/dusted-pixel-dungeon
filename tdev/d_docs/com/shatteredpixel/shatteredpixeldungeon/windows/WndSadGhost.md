# WndSadGhost 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndSadGhost.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 159 |

## 2. 类职责说明
WndSadGhost 是悲伤幽灵NPC任务奖励选择窗口，用于在完成幽灵任务后让玩家从两个奖励选项中选择一个。

## 7. 方法详解
- **构造函数 WndSadGhost(Quest quest)**: 创建幽灵奖励窗口，接收任务对象作为参数。
- **Boss视觉**: 根据任务类型（老鼠、狼人、螃蟹）显示对应的Boss精灵图像。
- **奖励选择**: 提供两个奖励物品选项，点击任一选项完成任务并获得对应奖励。
- **RewardWindow内部类**: 处理具体的奖励选择逻辑和物品创建。
- **任务完成**: 调用 selectReward() 方法完成任务，将选择的物品添加到玩家背包。
- **布局设计**: 使用固定的布局结构，确保Boss图像、描述文本和奖励选项的清晰展示。