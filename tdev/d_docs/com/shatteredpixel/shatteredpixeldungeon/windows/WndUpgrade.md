# WndUpgrade 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndUpgrade.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 560 |

## 2. 类职责说明
WndUpgrade 是物品升级预览和确认窗口，用于显示物品升级前后的对比效果，包括属性变化、附魔保留/丢失警告等详细信息。

## 7. 方法详解
- **构造函数 WndUpgrade(Item item)**: 创建升级窗口，接收要升级的物品作为参数。
- **前后对比**: 显示物品升级前后的精灵图像对比，直观展示外观变化。
- **属性变化**: 详细列出升级带来的属性变化，如武器伤害增加、护甲防御提升、法杖充能次数增加等。
- **警告提示**: 对于可能丢失附魔、获得诅咒或产生其他负面效果的情况提供明确警告。
- **费用显示**: 显示升级所需的金币费用，并检查玩家是否有足够资金。
- **ItemSelector集成**: 提供 getItemSelector() 静态方法，便于从背包中选择要升级的物品。
- **确认机制**: 提供"UPGRADE"按钮执行升级操作，"CANCEL"按钮取消操作。
- **布局优化**: 使用分栏布局清晰展示升级前后的各项信息，确保玩家能够充分了解升级效果。