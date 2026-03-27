# WndOptionsCondensed 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndOptionsCondensed.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends WndOptions |
| 代码行数 | 163 |

## 2. 类职责说明
WndOptionsCondensed 是紧凑型选项对话框，继承自 WndOptions，通过智能按钮布局算法在单行内显示更多按钮，适用于选项较多的场景。

## 7. 方法详解
- **构造函数 WndOptionsCondensed(Image icon, String title, String message, String... options)**: 创建紧凑型选项对话框。
- **智能布局算法**: 重写 layoutBody() 方法，使用更复杂的算法将按钮尽可能紧凑地排列在单行内。
- **宽度优化**: 根据按钮文本长度动态调整按钮宽度，确保在有限空间内容纳更多选项。
- **最小宽度保证**: 确保每个按钮至少有20像素的最小宽度，保证基本的可点击性。
- **继承功能**: 保留 WndOptions 的所有功能特性，包括回调机制、图标支持、状态控制等。
- **适用场景**: 主要用于需要显示4个以上选项的对话框，如物品选择、技能选择等场景。