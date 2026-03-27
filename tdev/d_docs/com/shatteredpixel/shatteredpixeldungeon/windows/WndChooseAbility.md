# WndChooseAbility 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndChooseAbility.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 167 |

## 2. 类职责说明
WndChooseAbility 是护甲能力选择窗口，用于在玩家使用国王王冠后从可用的护甲能力选项中进行选择，支持随机选择和详细信息查看。

## 7. 方法详解
- **构造函数 WndChooseAbility(HeroClass heroClass)**: 创建护甲能力选择窗口，接收英雄职业作为参数以确定可用选项。
- **选项展示**: 显示所有可用的护甲能力选项，每个选项包含图标、名称和简短描述。
- **随机选择**: 提供"RANDOMIZE"按钮，允许玩家随机选择一个护甲能力。
- **信息查看**: 每个选项都有信息按钮，点击可以查看该护甲能力的详细信息（通过 WndInfoArmorAbility 窗口）。
- **选择确认**: 点击护甲能力选项完成选择，应用选定的能力到当前英雄。
- **布局管理**: 使用与子职业选择窗口相同的网格布局，确保界面一致性。