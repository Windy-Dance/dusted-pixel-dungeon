# WndClericSpells 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndClericSpells.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.windows |
| 类类型 | class |
| 继承关系 | extends Window |
| 代码行数 | 265 |

## 2. 类职责说明
WndClericSpells 是牧师法术施放界面，用于显示和选择牧师英雄可用的各种法术，支持信息查看模式和实际施放模式。

## 7. 方法详解
- **构造函数 WndClericSpells()**: 创建法术界面，根据牧师当前的法术等级显示可用的法术选项。
- **法术分层**: 按法术等级（Tier）分组显示法术，每个等级包含不同的法术选项。
- **双模式切换**: 支持信息模式（查看法术详情）和施放模式（选择目标施放法术）的切换。
- **SpellButton内部类**: 每个法术按钮包含图标、名称、描述和能量消耗信息。
- **目标选择**: 在施放模式下，点击法术后进入目标选择状态，允许玩家选择法术目标。
- **能量管理**: 显示当前能量值和每个法术的能量消耗，禁用能量不足的法术选项。
- **信息查看**: 点击法术按钮在信息模式下显示详细描述（通过 Tooltips 接口）。