# 念力结晶 (TelekineticGrab)

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\items\spells\TelekineticGrab.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.spells |
| **文件类型** | class |
| **继承关系** | extends TargetedSpell |
| **代码行数** | 166 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
念力结晶是一个远程拾取法术，允许使用者远程抓取一格内的所有物体，或从敌人身上取回卡住的投掷武器。结晶无法用于抓取其他角色拥有的物品，也无法抓取诸如箱子这般的容器。

### 系统定位
作为TargetedSpell的子类，念力结晶在游戏的物品管理系统中提供远程交互能力。它与物品拾取系统、堆栈系统、敌人状态系统（PinCushion）和视觉效果系统深度集成。

### 不负责什么
- 不处理战斗伤害计算
- 不提供直接的buff或debuff效果
- 不涉及经济系统或商店交互

## 3. 结构总览

### 主要成员概览
- `image`: 物品图标（ItemSpriteSheet.TELE_GRAB）
- `talentChance`: 天赋触发概率（1/8）
- `Recipe`: 内部类，定义合成配方
- 继承自TargetedSpell的targeting和effect机制

### 主要逻辑块概览
- `fx()`: 自定义魔法飞弹效果（BEACON类型）
- `timeToCast()`: 重写施法时间计算
- `affectTarget()`: 处理远程拾取的核心逻辑
- 合成配方：使用液金

### 生命周期/调用时机
- 玩家选择施放 → 显示目标选择界面
- 选择目标位置 → 射击弹道 → 执行远程拾取
- 消耗念力结晶并触发天赋

## 4. 继承与协作关系

### 父类提供的能力
从TargetedSpell继承：
- `onCast()`: 打开目标选择界面
- 弹道系统和碰撞检测
- `onSpellused()`: 处理消耗和天赋触发

从Spell继承：
- `AC_CAST`动作常量
- `talt.