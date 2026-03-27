# WndTabbed 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndTabbed.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 291 |

## 2. 类职责说明
WndTabbed 是带标签页导航的窗口基类，为需要多标签界面的窗口提供通用功能。它管理标签页的创建、选择、布局和键盘导航，并处理窗口大小调整时的标签重新布局。

## 7. 方法详解
- **构造函数 WndTabbed()**: 初始化标签页窗口，设置键盘监听器以支持标签页切换快捷键（CYCLE 动作）。
- **add(Tab tab)**: 添加新的标签页到窗口底部，自动处理位置计算。
- **select(int index) / select(Tab tab)**: 选择指定索引或标签页作为活动标签，更新视觉状态。
- **layoutTabs()**: 重新计算所有标签页的位置和大小，确保它们在窗口底部正确排列。
- **tabHeight()**: 返回标签页的高度（默认25像素），子类可重写以自定义高度。
- **onClick(Tab tab)**: 处理标签页点击事件，子类可重写以执行特定逻辑。
- **内部类 Tab**: 标签页基类，处理选中状态的视觉反馈和点击事件。
- **内部类 LabeledTab**: 带文本标签的标签页，显示指定的文字内容。
- **内部类 IconTab**: 带图标的标签页，显示指定的图像图标。