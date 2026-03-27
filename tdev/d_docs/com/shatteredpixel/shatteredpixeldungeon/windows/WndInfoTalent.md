# WndInfoTalent 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndInfoTalent.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 102 |

## 2. 类职责说明
WndInfoTalent 是显示天赋（Talent）详细信息的窗口类，展示天赋的图标、名称、描述、所需点数，并提供可选的操作按钮用于天赋升级或变形。

## 7. 方法详解
- **构造函数 WndInfoTalent(HeroClass heroClass, int talent)**: 创建天赋信息窗口，接收英雄职业和天赋ID作为参数。
- **布局设计**: 固定宽度为140像素，包含天赋图标、名称标签、描述文本、点数信息和可选操作按钮。
- **按钮回调机制**: 通过 TalentButtonCallback 抽象内部类定义按钮点击行为，支持不同的操作模式（如升级、变形）。
- **视觉反馈**: 根据天赋状态使用不同颜色高亮，已解锁天赋使用特殊颜色标识。
- **响应式调整**: 在横向屏幕模式下可适当增加宽度以优化长文本显示效果。