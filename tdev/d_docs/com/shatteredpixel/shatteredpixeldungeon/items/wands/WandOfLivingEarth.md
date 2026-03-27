# WandOfLivingEarth 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/wands/WandOfLivingEarth.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.wands |
| **文件类型** | class |
| **继承关系** | extends DamageWand |
| **代码行数** | 484 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
灵壤法杖是一种召唤型伤害法杖，具有以下特点：
- 对敌人发射魔法泥石造成伤害
- 伤害转化为灵壤护甲积累
- 护甲积累到一定程度可召唤灵壤守卫
- 灵壤守卫会攻击敌人并保护施法者

### 系统定位
位于 DamageWand 层次，是主要的召唤型伤害法杖，适合需要额外输出的情况。

## 3. 结构总览

### 主要成员概览
- **伤害方法**：`min(int lvl)`, `max(int lvl)`
- **内部类**：`RockArmor` - 灵壤护甲Buff，`EarthGuardian` - 灵壤守卫NPC

## 4. 继承与协作关系

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `min(int lvl)` | 返回 4（固定） |
| `max(int lvl)` | 返回 6 + 2 * lvl |
| `onZap(Ballistica)` | 施放泥石并积累护甲/治疗守卫 |
| `onHit(MagesStaff, Char, Char, int)` | 近战命中积累护甲 |
| `fx(Ballistica, Callback)` | 土石飞弹视觉 |
| `upgradeStat2(int)` | 守卫最大生命值 |
| `upgradeStat3(int)` | 守卫防御 |
| `staffFx(MagesStaff.StaffParticle)` | 手杖粒子效果 |

## 5. 字段/常量详解

无实例字段（所有状态由内部类管理）。

## 6. 构造与初始化机制

### 初始化块
```java
{
    image = ItemSpriteSheet.WAND_LIVING_EARTH;
}
```

## 7. 方法详解

### min(int lvl)
**可见性**：public

**是否覆写**：是

**方法职责**：返回最小伤害

**返回值**：int，固定返回 4

---

### max(int lvl)
**可见性**：public

**是否覆写**：是

**方法职责**：返回最大伤害

**返回值**：int，`6 + 2 * lvl`

---

### onZap(Ballistica bolt)
**可见性**：public

**是否覆写**：是

**方法职责**：施放泥石法术

**核心实现逻辑**：
1. 计算伤害值
2. 查找现有的灵壤守卫
3. 根据目标类型执行不同逻辑：
   - 射向守卫：治疗守卫
   - 射向敌人：造成伤害，积累护甲，可能召唤守卫
   - 射向空地：造成伤害或召唤守卫

**护甲积累规则**：
- 只有攻击敌人才积累护甲
- 护甲上限为 `2 * armorToGuardian()`
- 护甲达到 `armorToGuardian()` 时可召唤守卫

**守卫召唤条件**：
```java
if (guardian == null && buff != null && buff.armor >= buff.armorToGuardian()) {
    // 召唤新守卫
}
```

---

### onHit(MagesStaff staff, Char attacker, Char defender, int damage)
**可见性**：public

**是否覆写**：是

**方法职责**：近战命中时积累护甲或治疗守卫

**核心实现逻辑**：
```java
int armor = Math.round(damage * 0.33f * procChanceMultiplier(attacker));
if (guardian != null) {
    guardian.setInfo(Dungeon.hero, buffedLvl(), armor);
} else {
    Buff.affect(attacker, RockArmor.class).addArmor(buffedLvl(), armor);
}
```

---

## RockArmor 内部类

### 基本信息
灵壤护甲是一个正面Buff，提供伤害减免并作为守卫召唤的条件。

### 字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| `wandLevel` | int | 法杖等级 |
| `armor` | int | 当前护甲量 |
| `powerOfManyTurns` | float | 万物一心强化回合数 |

### 方法
| 方法 | 职责 |
|------|------|
| `addArmor(int, int)` | 增加护甲 |
| `armorToGuardian()` | 返回召唤守卫所需护甲（8 + level*4） |
| `absorb(int)` | 吸收50%伤害 |
| `isEmpowered()` | 是否被万物一心强化 |

---

## EarthGuardian 内部类

### 基本信息
灵壤守卫是一个友方NPC，会自动攻击敌人。

### 字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| `wandLevel` | int | 法杖等级 |

### 方法
| 方法 | 职责 |
|------|------|
| `setInfo(Hero, int, int)` | 设置属性并治疗 |
| `attackSkill(Char)` | 返回攻击技能 |
| `damageRoll()` | 返回伤害值 |
| `drRoll()` | 返回防御值 |

### 特殊行为
- 无敌人时会重新化为护甲
- 攻击时会吸引敌人仇恨

## 8. 对外暴露能力

继承自 DamageWand，提供伤害和召唤守卫能力。

## 9. 运行机制与调用链

### 护甲机制
- 伤害的约33%转化为护甲
- 护甲减免50%伤害
- 护甲耗尽后消失

### 守卫机制
- 生命值：16 + 8 * level
- 防御：level ~ 3 + 3 * level
- 攻击：2 ~ 4 + depth/2
- 视野内无敌人时返回护甲形态

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.wands.wandoflivingearth.name` | 灵壤法杖 | 法杖名称 |
| `items.wands.wandoflivingearth.staff_name` | 灵壤魔杖 | 手杖名称 |
| `items.wands.wandoflivingearth.desc` | 描述文本 | 法杖描述 |
| `items.wands.wandoflivingearth.stats_desc` | 统计描述 | 伤害和守卫 |
| `items.wands.wandoflivingearth.upgrade_stat_name_2` | 守卫最大生命值 | 属性名称 |
| `items.wands.wandoflivingearth.upgrade_stat_name_3` | 守卫防御 | 属性名称 |
| `items.wands.wandoflivingearth$rockarmor.name` | 灵壤护甲 | Buff名称 |
| `items.wands.wandoflivingearth$rockarmor.desc` | Buff描述 | 效果说明 |
| `items.wands.wandoflivingearth$earthguardian.name` | 灵壤守卫 | NPC名称 |
| `items.wands.wandoflivingearth$earthguardian.desc` | NPC描述 | 效果说明 |

## 11. 使用示例

```java
// 创建灵壤法杖
WandOfLivingEarth wand = new WandOfLivingEarth();
wand.level(2);  // +2级

// 施放法术
Ballistica bolt = new Ballistica(hero.pos, targetPos, Ballistica.MAGIC_BOLT);
wand.onZap(bolt);  // 造成 4-10 伤害，积累护甲

// 护甲达到阈值（16）时召唤守卫
// 守卫生命值：32，防御：2-9
```

## 12. 开发注意事项

### 状态依赖
- 需要先积累护甲才能召唤守卫
- 守卫离开视野会返回护甲形态

### 常见陷阱
- 射向盟友不会积累护甲
- 守卫可能被敌人集火

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改 `RockArmor.armorToGuardian()` 调整召唤阈值
- 修改 `EarthGuardian` 的属性计算

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点