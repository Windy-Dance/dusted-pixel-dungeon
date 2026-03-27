# BeamingRay 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/BeamingRay.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 223 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
BeamingRay（光灵召唤）传送盟友至目标位置并使其获得对最近敌人的伤害加成。

### 系统定位
作为第4层级的盟友操作法术：
- 需要天赋 BEAMING_RAY 解锁
- 需要有强化盟友（万物一心或星界投射）
- 可传送通常无法移动的盟友（射程减半）

## 3. 方法详解

### icon()
**返回值**：HeroIcon.BEAMING_RAY

### targetingFlags()
**返回值**：Ballistica.STOP_TARGET（在目标处停止）

### canCast()
**前置条件**：
- 需要 BEAMING_RAY 天赋
- 需要有强化盟友（PowerOfMany.getPoweredAlly() 或 Stasis.getStasisAlly()）

### onTargetSelected()
**核心逻辑**：
1. 获取强化盟友
2. 计算传送位置（目标格或相邻格）
3. 检查射程限制
4. 播放光束效果并传送盟友
5. 为盟友施加伤害加成Buff

### desc()
**动态计算**：根据天赋等级计算射程和伤害加成

## 4. 内部类详解

### BeamingRayBoost

**类型**：public static class extends FlavourBuff

**职责**：光灵召唤增益Buff，记录伤害加成目标。

**常量**：
- `DURATION = 10f`：持续时间

**字段**：
- `object`：int，伤害加成目标的角色ID

## 5. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.beamingray.name | 光灵召唤 |
| actors.hero.spells.beamingray.short_desc | 传送你的盟友并使其获得伤害加成。 |
| actors.hero.spells.beamingray.no_space | 那里没有空间让你的盟友出现。 |
| actors.hero.spells.beamingray.out_of_range | 那个位置超出了范围。 |

### 中文翻译来源
actors_zh.properties 文件

## 6. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译