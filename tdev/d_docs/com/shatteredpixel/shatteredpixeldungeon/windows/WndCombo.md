# WndCombo 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndCombo.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 89 |

## 2. 类职责说明
WndCombo 是决斗者连击技能选择窗口，用于在玩家达到足够的连击次数后选择要执行的特殊连击动作。

## 7. 方法详解
- **构造函数 WndCombo()**: 创建连击技能窗口，根据当前的连击计数（comboCount）确定可用的技能选项。
- **技能展示**: 显示所有满足连击次数要求的可用连击技能，每个技能包含图标、名称和简短描述。
- **状态控制**: 根据连击次数动态启用或禁用技能按钮，确保只有符合条件的技能可被选择。
- **技能执行**: 点击技能按钮立即执行对应的连击动作，并消耗相应的连击次数。
- **简洁设计**: 界面简洁明了，专注于技能选择功能，避免不必要的复杂元素。
- **实时更新**: 每次打开窗口时都会重新计算可用技能，确保显示最新的技能状态。