# WndBadge 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndBadge.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 94 |

## 2. 类职责说明
WndBadge 是徽章/成就解锁通知窗口，用于显示新获得的徽章信息，包含徽章图标、名称、描述和特殊的高亮动画效果。

## 7. 方法详解
- **构造函数 WndBadge(Badge badge)**: 创建徽章通知窗口，接收 Badge 对象作为参数。
- **视觉效果**: 使用 BadgeBanner.highlight() 方法为新解锁的徽章添加闪烁高亮效果。
- **内容展示**: 显示徽章图标、本地化名称、详细描述以及获取条件（如果适用）。
- **布局管理**: 固定宽度设计，自动计算高度以适应所有内容。
- **特殊处理**: 对于某些特殊徽章（如排行榜相关），显示额外的信息或替代文本。
- **用户交互**: 提供简单的确认关闭机制，专注于信息展示而非复杂交互。