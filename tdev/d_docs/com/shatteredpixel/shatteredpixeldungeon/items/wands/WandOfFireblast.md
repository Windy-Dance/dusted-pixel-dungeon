# WandOfFireblast 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/wands/WandOfFireblast.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.wands |
| **文件类型** | class |
| **继承关系** | extends DamageWand |
| **代码行数** | 293 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
焰浪法杖是一种锥形范围伤害法杖，具有以下特点：
- 释放锥形火焰区域，造成范围伤害
- 充能消耗越多，范围和伤害越大（1/2/3充能）
- 点燃范围内的敌人和地形
- 高充能时造成残废或麻痹效果

### 系统定位
位于 DamageWand 层次，是主要的范围伤害型法杖，适合对付密集敌群。

## 3. 结构总览

### 主要成员概览
- **伤害方法**：`min(int lvl)`, `max(int lvl)` - 伤害随充能消耗变化
- **充能方法**：`chargesPerCast()` - 动态计算消耗
- **视觉效果**：`fx()` - 锥形火焰效果
- **内部字段**：`cone` - ConeAOE 锥形区域

## 4. 继承与协作关系

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `min(int lvl)` | 最小伤害：`(1+lvl) * chargesPerCast()` |
| `max(int lvl)` | 最大伤害随充能消耗变化 |
| `onZap(Ballistica)` | 施放锥形火焰 |
| `onHit(MagesStaff, Char, Char, int)` | 近战命中爆炸效果 |
| `fx(Ballistica, Callback)` | 锥形火焰视觉 |
| `chargesPerCast()` | 动态充能消耗 |
| `statsDesc()` | 格式化统计描述 |
| `upgradeStat1/2/3(int)` | 三个充能档位的伤害 |
| `staffFx(MagesStaff.StaffParticle)` | 手杖粒子效果 |

## 5. 字段/常量详解

### 实例字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| `cone` | ConeAOE | 锥形区域对象 |

## 6. 构造与初始化机制

### 初始化块
```java
{
    image = ItemSpriteSheet.WAND_FIREBOLT;
    collisionProperties = Ballistica.WONT_STOP;
}
```

## 7. 方法详解

### min(int lvl)
**可见性**：public

**是否覆写**：是

**方法职责**：返回最小伤害（与充能消耗成正比）

**返回值**：int，`(1 + lvl) * chargesPerCast()`

---

### max(int lvl)
**可见性**：public

**是否覆写**：是

**方法职责**：返回最大伤害

**返回值**：int，根据充能消耗返回不同值
- 1充能：`2 + 2 * lvl`
- 2充能：`2 * (4 + 2 * lvl)`
- 3充能：`3 * (6 + 2 * lvl)`

---

### chargesPerCast()
**可见性**：protected

**是否覆写**：是

**方法职责**：计算每次施法消耗的充能

**返回值**：int，消耗当前充能的30%，向上取整，范围1-3

**核心实现逻辑**：
```java
if (cursed || charger.target.buff(WildMagic.WildMagicTracker.class) != null) {
    return 1;
}
return (int) GameMath.gate(1, (int) Math.ceil(curCharges * 0.3f), 3);
```

---

### onZap(Ballistica bolt)
**可见性**：public

**是否覆写**：是

**方法职责**：施放锥形火焰

**核心实现逻辑**：
1. 遍历锥形区域内的所有格子
2. 打开门，点燃可燃物
3. 对敌人造成伤害并施加燃烧
4. 根据充能消耗施加残废或麻痹效果

---

### onHit(MagesStaff staff, Char attacker, Char defender, int damage)
**可见性**：public

**是否覆写**：是

**方法职责**：近战命中时根据周围火焰概率触发爆炸

**核心实现逻辑**：
- 每个燃烧的敌人增加25%触发概率
- 每个燃烧的地形增加5%触发概率
- 触发后清除周围火焰并造成范围伤害

---

## 8. 对外暴露能力

继承自 DamageWand，提供锥形范围火焰伤害能力。

## 9. 运行机制与调用链

### 施法流程
1. `chargesPerCast()` 计算充能消耗
2. `fx()` 创建锥形视觉
3. `onZap()` 执行伤害和效果

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.wands.wandoffireblast.name` | 焰浪法杖 | 法杖名称 |
| `items.wands.wandoffireblast.staff_name` | 焰浪魔杖 | 手杖名称 |
| `items.wands.wandoffireblast.desc` | 描述文本 | 法杖描述 |
| `items.wands.wandoffireblast.stats_desc` | 统计描述 | 伤害和范围 |
| `items.wands.wandoffireblast.upgrade_stat_name_1/2/3` | 1/2/3点充能伤害 | 属性名称 |
| `items.wands.wandoffireblast.bmage_desc` | 战斗法师描述 | 子职业效果 |

## 11. 使用示例

```java
// 创建焰浪法杖
WandOfFireblast wand = new WandOfFireblast();
wand.curCharges = 3;  // 满充能

// 施放法术（消耗1充能时范围小，3充能时范围大）
Ballistica bolt = new Ballistica(hero.pos, targetPos, Ballistica.WONT_STOP);
wand.fx(bolt, callback);
wand.onZap(bolt);  // 锥形火焰伤害
```

## 12. 开发注意事项

### 状态依赖
- 充能消耗是动态计算的，每次施法可能不同
- 火焰效果会交互地形（点燃可燃物）

### 常见陷阱
- 诅咒状态固定消耗1充能
- `collisionProperties` 设置为 `WONT_STOP` 用于瞄准

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改 `chargesPerCast()` 的计算逻辑调整充能消耗
- 修改锥形范围参数（距离和角度）

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点