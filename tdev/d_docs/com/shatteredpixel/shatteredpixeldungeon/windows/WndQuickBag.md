# WndQuickBag 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndQuickBag.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 184 |

## 2. 类职责说明
WndQuickBag 是快速物品访问窗口，以紧凑的弹出式界面显示玩家背包中有默认操作的物品，便于快速使用常用物品。

## 7. 方法详解
- **构造函数 WndQuickBag()**: 创建快速物品窗口，自动筛选背包中具有默认操作的物品。
- **排序逻辑**: 使用 quickBagComparator 对物品进行排序，优先显示已装备物品，然后按物品类别分组。
- **网格布局**: 在紧凑的网格中显示物品图标，默认每行5个物品，支持滚动查看更多。
- **快速操作**: 点击物品立即执行其默认操作（如使用药水、投掷武器等），无需打开完整背包。
- **Toast样式**: 采用类似Toast通知的简洁设计，窗口较小且可快速关闭。
- **上下文敏感**: 根据当前游戏状态动态调整显示的物品列表，确保只显示相关和可用的物品。