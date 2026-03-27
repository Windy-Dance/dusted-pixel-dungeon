# WndList 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndList.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 62 |

## 2. 类职责说明
WndList 是简单的项目符号列表显示窗口，用于展示有序或无序的文本列表内容，每个项目前带有圆点标记。

## 7. 方法详解
- **构造函数 WndList(String[] items)**: 创建列表窗口，接收字符串数组作为列表项目。
- **布局管理**: 固定窗口宽度为120像素，根据项目数量自动计算窗口高度。
- **项目格式化**: 每个项目文本前自动添加圆点符号（•），使用8号字体渲染。
- **多行支持**: 支持长文本项目的自动换行，确保所有内容完整显示。
- **响应式设计**: 根据屏幕方向优化布局，在横向模式下可能增加宽度以减少高度。
- **简洁实现**: 专注于列表内容展示，不包含交互功能或复杂布局。