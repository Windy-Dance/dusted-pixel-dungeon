# WndScoreBreakdown 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndScoreBreakdown.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 121 |

## 2. 类职责说明
WndScoreBreakdown 是分数计算明细窗口，用于详细展示游戏得分的各个组成部分和计算方式，帮助玩家理解最终分数的构成。

## 7. 方法详解
- **构造函数 WndScoreBreakdown()**: 创建分数明细窗口，从 Results 枚举中获取所有相关的分数数据。
- **分类展示**: 将分数分解为多个类别（如击败敌人、收集物品、探索深度、完成挑战等），每个类别单独显示。
- **数值计算**: 显示每个类别的基础分数、乘数因子和最终贡献值。
- **总额计算**: 在底部显示所有类别分数的总和，即最终游戏得分。
- **布局管理**: 使用固定宽度（120像素）设计，通过 statSlot() 和 addInfo() 方法添加行项目。
- **信息层级**: 主要数值使用大字体突出显示，辅助说明使用小字体提供详细解释。