# HallowedGround 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/HallowedGround.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 270 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
HallowedGround（神圣领域）创建一片治疗盟友、减速敌人的神圣区域。

### 系统定位
作为第3层级的区域效果法术：
- 需要天赋 HALLOWED_GROUND 解锁
- 祭司专属强力法术
- 持续20回合的区域效果

## 3. 方法详解

### icon()
**返回值**：HeroIcon.HALLOWED_GROUND

### chargeUse()
**返回值**：固定返回 2

### targetingFlags()
**返回值**：Ballistica.STOP_TARGET

### canCast()
**前置条件**：需要 HALLOWED_GROUND 天赋

### onTargetSelected()
**核心逻辑**：
1. 验证目标位置（非实心、在视野内）
2. 根据天赋计算影响范围
3. 将空地转为草地
4. 创建神圣领域地形Blob
5. 立即影响范围内的角色

### affectChar(Char)
**效果**：
- 盟友：治疗15点生命或给予护盾
- 敌人（非飞行）：施加光耀+缠绕2回合

## 4. 内部类详解

### HallowedTerrain

**类型**：public static class extends Blob

**职责**：神圣领域的地形效果，每回合触发效果。

**效果**：
- 每回合治疗盟友1点或给予护盾
- 对敌人施加残疾效果
- 随机催生高草
- 被火焰摧毁

### HallowedFurrowTracker

**类型**：public static class extends CounterBuff

**职责**：追踪神圣领域产生的草量，用于控制枯草生成。

## 5. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.hallowedground.name | 神圣领域 |
| actors.hero.spells.hallowedground.short_desc | 治疗盟友，减速敌人并在范围内扩散植被。 |
| actors.hero.spells.hallowedground.desc | 祭司将其神圣魔法聚集在附近的地面上... |

### 中文翻译来源
actors_zh.properties 文件

## 6. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译