# WndImp 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndImp.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 87 |

## 2. 类职责说明
WndImp 是小恶魔NPC任务完成窗口，用于在收集到足够多的恶魔代币后与小恶魔交互，兑换奖励物品。

## 7. 方法详解
- **构造函数 WndImp()**: 创建小恶魔交互窗口，检查玩家拥有的恶魔代币数量。
- **奖励兑换**: 提供"TAKE REWARD"按钮，点击后消耗所有恶魔代币获得随机奖励物品。
- **代币显示**: 显示当前拥有的恶魔代币数量，以及达到下一个奖励等级所需的代币数。
- **任务完成**: 调用 takeReward() 方法处理奖励兑换逻辑，包括移除代币和添加奖励物品。
- **简洁设计**: 界面简洁明了，专注于代币数量显示和奖励兑换功能。
- **状态反馈**: 根据代币数量提供不同的提示信息，引导玩家继续收集代币。