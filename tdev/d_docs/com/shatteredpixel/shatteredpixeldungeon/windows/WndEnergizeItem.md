# WndEnergizeItem 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndEnergizeItem.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends WndInfoItem |
| 代码行数 | 208 |

## 2. 类职责说明
WndEnergizeItem 是物品能量转换窗口，继承自 WndInfoItem，允许玩家将不需要的物品转换为能量货币，用于天赋升级和其他游戏机制。

## 7. 方法详解
- **构造函数 WndEnergizeItem(Item item)**: 创建能量转换窗口，显示物品信息和转换选项。
- **转换选项**: 提供"ENERGIZE"、"ENERGIZE ONE"、"ENERGIZE ALL"三个转换选项，支持批量处理堆叠物品。
- **能量计算**: 根据物品类型、稀有度和等级计算转换获得的能量值。
- **确认机制**: 在执行转换前显示确认对话框，防止误操作。
- **静态辅助方法**: 提供 energizeAll()、energizeOne() 等静态方法供其他类调用执行转换操作。
- **物品选择器**: 集成 WndBag.ItemSelector 用于从背包中选择要转换的物品。