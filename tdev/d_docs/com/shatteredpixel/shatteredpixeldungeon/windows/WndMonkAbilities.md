# WndMonkAbilities 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndMonkAbilities.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 98 |

## 2. 类职责说明
WndMonkAbilities 是武僧能量能力选择界面，用于显示武僧英雄可用的各种能量消耗能力，并允许玩家选择和使用这些能力。

## 7. 方法详解
- **构造函数 WndMonkAbilities()**: 创建能力选择界面，显示所有可用的武僧能力。
- **能力展示**: 每个能力按钮显示图标、名称、描述和能量消耗值。
- **状态控制**: 根据当前能量值动态启用或禁用能力按钮，确保只有能量足够的能力可被选择。
- **目标选择**: 对于需要选择目标的能力（如治疗、攻击等），点击后进入目标选择模式。
- **即时使用**: 对于不需要目标的能力（如自我增益），点击后立即执行。
- **能量反馈**: 实时显示当前能量值，帮助玩家做出明智的能力选择决策。
- **简洁布局**: 使用紧凑的网格布局，在有限空间内清晰展示所有能力选项。