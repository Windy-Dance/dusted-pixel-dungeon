# WndTradeItem 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndTradeItem.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends WndInfoItem |
| 代码行数 | 305 |

## 2. 类职责说明
WndTradeItem 是商店交易窗口，继承自 WndInfoItem，用于处理玩家与商人之间的物品买卖操作，支持批量销售和特殊购买选项。

## 7. 方法详解
- **构造函数 WndTradeItem(Item item, boolean selling)**: 创建交易窗口，根据销售或购买模式显示不同的界面和按钮。
- **销售功能**: 提供"SELL"、"SELL ONE"、"SELL ALL"三个销售选项，支持批量处理堆叠物品。
- **购买功能**: 显示物品价格和购买按钮，持有盗贼臂章时提供"STEAL"选项。
- **价格计算**: 根据物品类型、等级和游戏规则计算准确的买卖价格。
- **库存管理**: 处理物品从背包移除或添加到背包的逻辑，更新金币数量。
- **静态辅助方法**: 提供 sell()、sellOne() 等静态方法供其他类调用执行销售操作。