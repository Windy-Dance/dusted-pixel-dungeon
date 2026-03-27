# WndHeroInfo 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndHeroInfo.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends WndTabbed |
| 代码行数 | 398 |

## 2. 类职责说明
WndHeroInfo 是英雄详细信息窗口，继承自 WndTabbed，提供多个标签页展示英雄职业的详细信息，包括基础描述、天赋、子职业和护甲能力。

## 7. 方法详解
- **构造函数 WndHeroInfo(HeroClass heroClass)**: 创建英雄信息窗口，接收英雄职业作为参数。
- **HeroInfoTab（英雄信息）**: 显示英雄职业的基础描述、特点和游戏风格说明。
- **TalentInfoTab（天赋信息）**: 列出该职业所有可用的天赋，按层级分组显示，并提供详细描述。
- **SubclassInfoTab（子职业信息）**: 展示该职业所有可用的子职业选项，每个子职业都有详细的描述和特性说明。
- **ArmorAbilityInfoTab（护甲能力信息）**: 显示通过国王王冠获得的护甲能力选项和相关信息。
- **交互功能**: 支持点击天赋、子职业等条目查看更详细的信息（通过 WndInfoTalent、WndInfoSubclass 等窗口）。
- **数据整合**: 从 HeroClass 模块获取所有相关的英雄职业数据，确保信息的完整性和准确性。
- **布局管理**: 使用标准的标签页布局，每个标签页都实现 createChildren() 和 layout() 方法进行内容管理。