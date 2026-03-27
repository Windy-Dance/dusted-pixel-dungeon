# WndInfoArmorAbility 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndInfoArmorAbility.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends WndTitledMessage |
| 代码行数 | 57 |

## 2. 类职责说明
WndInfoArmorAbility 是显示护甲能力（Armor Ability）详细信息的窗口类，继承自 WndTitledMessage，专门用于展示通过国王王冠获得的护甲能力描述和相关的第四层天赋。

## 7. 方法详解
- **构造函数 WndInfoArmorAbility(HeroClass heroClass, ArmorAbility ability)**: 创建护甲能力信息窗口，接收英雄职业和护甲能力作为参数。
- **标题栏设置**: 使用护甲能力的图标和名称作为标题，使用特殊的绿色调色板突出显示。
- **天赋展示**: 列出该护甲能力解锁的所有第四层天赋（Tier 4 talents），每个天赋都带有详细描述。
- **描述整合**: 将护甲能力基础描述与天赋信息整合显示，提供完整的护甲能力效果概览。
- **视觉一致性**: 遵循与其他信息窗口相同的设计模式，确保用户界面的一致性。