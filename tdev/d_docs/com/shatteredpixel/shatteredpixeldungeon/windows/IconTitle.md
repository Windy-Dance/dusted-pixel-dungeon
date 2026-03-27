# IconTitle 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/IconTitle.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Component |
| 代码行数 | 149 |

## 2. 类职责说明
IconTitle 是一个可重用的标题栏组件，用于在各种窗口顶部显示图标、文本标签和可选的生命值条。它被广泛用于信息显示窗口中作为标题部分。

## 7. 方法详解
- **构造函数 IconTitle()**: 创建基本的标题组件实例。
- **icon(Image image)**: 设置标题栏左侧显示的图标图像。
- **label(String text) / label(String text, int color)**: 设置标题文本内容和颜色（默认为 TITLE_COLOR）。
- **health(float value)**: 显示生命值条，通常用于显示怪物或角色的生命状态。
- **reqWidth()**: 计算标题组件所需的最小宽度，用于布局调整。
- **layout()**: 负责内部组件（图标、文本、生命条）的布局计算和位置调整。