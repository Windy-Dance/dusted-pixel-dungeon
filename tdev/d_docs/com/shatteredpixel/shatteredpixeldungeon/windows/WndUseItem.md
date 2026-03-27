# WndUseItem 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndUseItem.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends WndInfoItem |
| 代码行数 | 178 |

## 2. 类职责说明
WndUseItem 是物品使用窗口，继承自 WndInfoItem，在显示物品详细信息的基础上添加了操作按钮，允许玩家执行物品的各种可用动作。

## 7. 方法详解
- **构造函数 WndUseItem(Window parent, Item item)**: 创建物品使用窗口，接收父窗口和物品对象作为参数。
- **按钮布局管理**: 根据可用动作数量智能布局按钮，支持单行或多行排列，每行最多3个按钮。
- **动作执行**: 点击按钮时执行对应的物品动作（通过 item.execute() 方法），并处理各种结果状态。
- **特殊按钮**: 包含"INFO"按钮用于显示详细物品信息，某些情况下还包含"JOURNAL"按钮。
- **错误处理**: 处理物品使用过程中可能出现的各种错误情况，并显示相应的提示消息。
- **窗口协调**: 与父窗口（通常是背包窗口）协调，在物品使用完成后正确关闭窗口。