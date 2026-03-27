# WandOfFrost 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/wands/WandOfFrost.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.wands |
| **文件类型** | class |
| **继承关系** | extends DamageWand |
| **代码行数** | 168 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
冰霜法杖是一种控制型伤害法杖，具有以下特点：
- 对目标造成冰霜伤害
- 施加冻伤（Chill）效果
- 在水中效果更强
- 对已冻结目标不造成伤害
- 冻伤目标受到的伤害减少

### 系统定位
位于 DamageWand 层次，是主要的控制型伤害法杖，适合减速和控制敌人。

## 3. 结构总览

### 主要成员概览
- **伤害方法**：`min(int lvl)`, `max(int lvl)`
- **特殊逻辑**：`onZap()` 中的冻伤伤害减免计算
- **近战效果**：`onHit()` 可能直接冻结目标

## 4. 继承与协作关系

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `min(int lvl)` | 返回 2 + lvl |
| `max(int lvl)` | 返回 8 + 5 * lvl |
| `onZap(Ballistica)` | 施放冰霜效果 |
| `onHit(MagesStaff, Char, Char, int)` | 近战命中冻结效果 |
| `fx(Ballistica, Callback)` | 冰霜飞弹视觉 |
| `upgradeStat2(int)` | 冻伤持续时间 |
| `staffFx(MagesStaff.StaffParticle)` | 手杖粒子效果 |

## 5. 字段/常量详解

无实例字段。

## 6. 构造与初始化机制

### 初始化块
```java
{
    image = ItemSpriteSheet.WAND_FROST;
}
```

## 7. 方法详解

### min(int lvl)
**可见性**：public

**是否覆写**：是

**方法职责**：返回最小伤害

**返回值**：int，`2 + lvl`

---

### max(int lvl)
**可见性**：public

**是否覆写**：是

**方法职责**：返回最大伤害

**返回值**：int，`8 + 5 * lvl`

---

### onZap(Ballistica bolt)
**可见性**：public

**是否覆写**：是

**方法职责**：施放冰霜法术

**核心实现逻辑**：
1. 冻结物品堆和熄灭火焰
2. 查找目标角色
3. 如果目标已冻结，不造成伤害
4. 如果目标有冻伤，伤害随冻伤回合数减少（每回合减少6.67%）
5. 施加冻伤效果（水中4+lvl回合，陆地2+lvl回合）

**伤害计算公式**：
```java
// 冻伤目标伤害减免
float chillturns = Math.min(10, ch.buff(Chill.class).cooldown());
damage = (int) Math.round(damage * Math.pow(0.9333f, chillturns));
```

---

### onHit(MagesStaff staff, Char attacker, Char defender, int damage)
**可见性**：public

**是否覆写**：是

**方法职责**：近战命中时可能冻结目标

**触发概率**：
- 冻伤2回合时：1/9 概率
- 冻伤10回合时：9/9 概率

**核心实现逻辑**：
```java
Chill chill = defender.buff(Chill.class);
if (chill != null) {
    float procChance = ((int) Math.floor(chill.cooldown()) - 1) / 9f;
    procChance *= procChanceMultiplier(attacker);
    if (Random.Float() < procChance) {
        // 延迟冻结（避免被伤害打断）
        Buff.affect(defender, Frost.class, Math.round(Frost.DURATION * powerMulti));
    }
}
```

---

## 8. 对外暴露能力

继承自 DamageWand，提供冰霜伤害和控制能力。

## 9. 运行机制与调用链

### 伤害机制
- 基础伤害：2+lvl ~ 8+5*lvl
- 冻伤目标伤害减少
- 水中延长冻伤持续时间

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.wands.wandoffrost.name` | 冰霜法杖 | 法杖名称 |
| `items.wands.wandoffrost.staff_name` | 冰霜魔杖 | 手杖名称 |
| `items.wands.wandoffrost.desc` | 描述文本 | 法杖描述 |
| `items.wands.wandoffrost.stats_desc` | 统计描述 | 伤害和冻伤 |
| `items.wands.wandoffrost.upgrade_stat_name_2` | 冻伤持续时间 | 属性名称 |
| `items.wands.wandoffrost.bmage_desc` | 战斗法师描述 | 子职业效果 |

## 11. 使用示例

```java
// 创建冰霜法杖
WandOfFrost wand = new WandOfFrost();
wand.level(2);  // +2级

// 施放法术
Ballistica bolt = new Ballistica(hero.pos, targetPos, Ballistica.MAGIC_BOLT);
wand.onZap(bolt);  // 造成 4-18 伤害，施加 4 回合冻伤

// 如果目标在水中
// 冻伤持续时间变为 6 回合
```

## 12. 开发注意事项

### 状态依赖
- 已冻结目标不受伤害（免疫）
- 冻伤会减少后续冰霜伤害
- 水中增强效果

### 常见陷阱
- 连续使用效果递减（因为目标已有冻伤）
- 需要配合其他伤害来源击杀

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改伤害公式调整伤害曲线
- 修改冻伤持续时间计算

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点