# MnemonicPrayer 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/MnemonicPrayer.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 197 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
MnemonicPrayer（祈愿诗篇）延长目标的增益/减益效果持续时间。

### 系统定位
作为第3层级的辅助法术：
- 需要天赋 MNEMONIC_PRAYER 解锁
- 祭司专属强力法术
- 施法不消耗回合

## 3. 方法详解

### icon()
**返回值**：HeroIcon.MNEMONIC_PRAYER

### targetingFlags()
**返回值**：Ballistica.STOP_TARGET

### canCast()
**前置条件**：需要 MNEMONIC_PRAYER 天赋

### onTargetSelected()
**核心逻辑**：
1. 检查目标在视野内
2. 调用 affectChar() 延长效果
3. 生命联结下复制效果

### affectChar()
**效果**：
- 盟友：延长所有正面效果（排除护甲能力、位格效果等）
- 敌人：延长所有负面效果并施加光耀

**延长时间**：2 + 天赋等级 回合

**限制**：每个效果只能延长一次（mnemonicExtended 标记）

## 4. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.mnemonicprayer.name | 祈愿诗篇 |
| actors.hero.spells.mnemonicprayer.short_desc | 延长盟友/敌人的增益/减益效果，并重新施加光耀。 |
| actors.hero.spells.mnemonicprayer.desc | 祭司念诵出延长特定目标所有增益或减益效果的经文... |

### 中文翻译来源
actors_zh.properties 文件

## 5. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译