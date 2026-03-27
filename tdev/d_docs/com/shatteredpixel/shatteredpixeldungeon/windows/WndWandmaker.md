# WndWandmaker 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndWandmaker.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 162 |

## 2. 类职责说明
WndWandmaker 是魔杖匠NPC任务奖励选择窗口，用于在完成魔杖匠任务后让玩家从两个魔杖奖励选项中选择一个。

## 7. 方法详解
- **构造函数 WndWandmaker(Quest quest)**: 创建魔杖匠奖励窗口，接收任务对象作为参数。
- **任务物品视觉**: 根据任务类型（尘埃、余烬、浆果）显示对应的任务物品精灵图像。
- **魔杖奖励**: 提供两个随机生成的魔杖作为奖励选项，每个魔杖都有完整的属性和随机附魔。
- **RewardWindow内部类**: 处理具体的魔杖生成和选择逻辑。
- **任务完成**: 完成任务后将选择的魔杖添加到玩家背包，并移除任务物品。
- **布局设计**: 类似于其他NPC奖励窗口，使用固定的布局结构确保清晰的信息展示。