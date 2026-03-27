# LayOnHands 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/LayOnHands.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 139 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
LayOnHands（圣疗之触）治疗附近目标或为自己提供护盾。

### 系统定位
作为第3层级的治疗法术：
- 需要天赋 LAY_ON_HANDS 解锁
- 圣骑士专属强力法术
- 可对自己或邻近目标施放

## 3. 方法详解

### icon()
**返回值**：HeroIcon.LAY_ON_HANDS

### targetingFlags()
**返回值**：-1（禁用自动目标选择）

### canCast()
**前置条件**：需要 LAY_ON_HANDS 天赋

### onTargetSelected()
**核心逻辑**：
1. 验证目标在1格范围内
2. 调用 affectChar() 施加效果
3. 生命联结下复制效果

### affectChar()
**效果**：
- 对自己：给予护盾（上限为3倍治疗量）
- 对他人：治疗（溢出转护盾，上限同上）

**治疗/护盾量**：10 + 5 × 天赋等级

## 4. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.layonhands.name | 圣疗之触 |
| actors.hero.spells.layonhands.short_desc | 立即治疗附近一个单位或使圣骑士获得护盾。 |
| actors.hero.spells.layonhands.desc | 圣骑士以其双手引导出神圣能量，治疗或保护其所触的任何单位... |

### 中文翻译来源
actors_zh.properties 文件

## 5. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译