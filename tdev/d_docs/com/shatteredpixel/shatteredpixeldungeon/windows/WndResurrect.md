# WndResurrect 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndResurrect.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 183 |

## 2. 类职责说明
WndResurrect 是安克复活选择窗口，当玩家使用安克复活道具时显示，允许玩家从背包中选择2个物品在复活后保留。

## 7. 方法详解
- **构造函数 WndResurrect(Ankh ankh)**: 创建复活选择窗口，接收 Ankh 复活道具作为参数。
- **物品选择器**: 集成 WndBag.ItemSelector 实现物品选择功能，限制最多选择2个物品。
- **视觉反馈**: 已选择的物品有特殊的视觉标识，选择数量达到限制时禁用其他物品。
- **确认机制**: 提供确认按钮执行复活操作，取消按钮放弃复活并丢弃安克。
- **数据处理**: 将选择的物品列表传递给安克道具的 resurrect() 方法完成复活流程。
- **布局优化**: 使用紧凑的布局设计，确保在有限空间内清晰显示选择状态和物品信息。