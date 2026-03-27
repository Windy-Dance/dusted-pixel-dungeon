# WndInfoSubclass 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndInfoSubclass.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends WndTitledMessage |
| 代码行数 | 57 |

## 2. 类职责说明
WndInfoSubclass 是显示英雄子职业（Subclass）详细信息的窗口类，继承自 WndTitledMessage，专门用于展示子职业的描述信息和相关的第三层天赋。

## 7. 方法详解
- **构造函数 WndInfoSubclass(HeroClass heroClass, SubClass subClass)**: 创建子职业信息窗口，接收英雄职业和子职业作为参数。
- **标题栏设置**: 使用子职业的图标和名称作为标题，颜色根据子职业类型进行区分。
- **天赋展示**: 列出该子职业解锁的所有第三层天赋（Tier 3 talents），每个天赋都带有详细描述。
- **描述整合**: 将子职业基础描述与天赋信息整合显示，提供完整的子职业能力概览。
- **视觉设计**: 使用统一的布局风格，确保与游戏其他信息窗口保持一致的用户体验。