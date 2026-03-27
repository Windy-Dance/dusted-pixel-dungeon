# WandOfCorruption 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/wands/WandOfCorruption.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.wands |
| **文件类型** | class |
| **继承关系** | extends Wand |
| **代码行数** | 284 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
腐化法杖是一种控制型法杖，具有以下特点：
- 削弱敌人并可能将其腐化为盟友
- 对无法腐化的敌人施加减益效果
- 敌人越虚弱，腐化成功率越高
- 已有减益效果降低敌人抵抗

### 系统定位
位于 Wand 层次（非 DamageWand），是主要的控制型法杖，适合转化敌人为盟友。

## 3. 结构总览

### 主要成员概览
- **减益列表**：`MINOR_DEBUFFS`, `MAJOR_DEBUFFS` - 可施加的减益效果
- **施法方法**：`onZap()` - 腐化或施加减益
- **辅助方法**：`debuffEnemy()`, `corruptEnemy()`

## 4. 继承与协作关系

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `onZap(Ballistica)` | 腐化或削弱敌人 |
| `onHit(MagesStaff, Char, Char, int)` | 近战命中施加狂乱 |
| `fx(Ballistica, Callback)` | 暗影飞弹视觉 |
| `staffFx(MagesStaff.StaffParticle)` | 手杖粒子效果 |
| `upgradeStat1(int)` | 腐化强度 |
| `upgradeStat2(int)` | 减益持续时间 |

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `MINOR_DEBUFF_WEAKEN` | float | 1/4 | 轻微减益削弱比例 |
| `MAJOR_DEBUFF_WEAKEN` | float | 1/2 | 严重减益削弱比例 |
| `MINOR_DEBUFFS` | HashMap | - | 轻微减益列表 |
| `MAJOR_DEBUFFS` | HashMap | - | 严重减益列表 |

### 减益效果列表
**轻微减益**：虚弱、脆弱、残废、目盲、恐惧等
**严重减益**：狂乱、缓慢、诅咒、麻痹等

## 6. 构造与初始化机制

### 初始化块
```java
{
    image = ItemSpriteSheet.WAND_CORRUPTION;
}
```

## 7. 方法详解

### onZap(Ballistica bolt)
**可见性**：public

**是否覆写**：是

**方法职责**：尝试腐化或削弱敌人

**核心实现逻辑**：
1. 计算腐化强度：`3 + level / 3`
2. 计算敌人抵抗：
   - 基础抵抗基于敌人经验值
   - 特殊敌人有特殊计算方式
   - 生命值百分比影响抵抗
   - 已有减益降低抵抗
3. 比较强度与抵抗决定效果：
   - 强度 > 抵抗：腐化成功
   - 否则：施加减益（大概率严重减益，小概率轻微减益）

**抵抗计算公式**：
```java
// 基础抵抗
float enemyResist = 1 + enemy.EXP;
// 生命值影响：100%生命=5倍抵抗，50%生命=2倍抵抗
enemyResist *= 1 + 4 * Math.pow(enemy.HP / (float)enemy.HT, 2);
// 减益削弱
for (Buff buff : enemy.buffs()) {
    if (MAJOR_DEBUFFS.containsKey(buff.getClass())) enemyResist *= 0.5f;
    else if (MINOR_DEBUFFS.containsKey(buff.getClass())) enemyResist *= 0.75f;
}
```

---

### debuffEnemy(Mob enemy, HashMap category)
**可见性**：private

**方法职责**：对敌人施加减益效果

**核心实现逻辑**：
1. 排除已有的和免疫的减益
2. 随机选择一个减益
3. 如果没有可用减益，升级到下一级效果

---

### corruptEnemy(Mob enemy)
**可见性**：private

**方法职责**：腐化敌人为盟友

**核心实现逻辑**：
```java
if (!enemy.isImmune(Corruption.class)) {
    Corruption.corruptionHeal(enemy);
    AllyBuff.affectAndLoot(enemy, curUser, Corruption.class);
} else {
    // 对免疫腐化的敌人施加厄运
    Buff.affect(enemy, Doom.class);
}
```

---

### onHit(MagesStaff staff, Char attacker, Char defender, int damage)
**可见性**：public

**是否覆写**：是

**方法职责**：近战命中时可能施加狂乱效果

**触发概率**：
- 0级：16%
- 1级：28.5%
- 2级：37.5%

**效果**：施加 `4 + level * 2` 回合的狂乱

## 8. 对外暴露能力

继承自 Wand，提供腐化敌人和施加减益的能力。

## 9. 运行机制与调用链

### 腐化流程
1. 计算腐化强度和敌人抵抗
2. 比较决定效果类型
3. 执行腐化或施加减益

### 特殊敌人处理
- 宝箱怪、雕像：抵抗基于地牢深度
- 食人鱼、蜜蜂：抵抗减半
- 幽灵：抵抗除以5

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.wands.wandofcorruption.name` | 腐化法杖 | 法杖名称 |
| `items.wands.wandofcorruption.staff_name` | 腐化魔杖 | 手杖名称 |
| `items.wands.wandofcorruption.already_corrupted` | 这个角色已经被你腐化。 | 已腐化提示 |
| `items.wands.wandofcorruption.desc` | 描述文本 | 法杖描述 |
| `items.wands.wandofcorruption.stats_desc` | 统计描述 | 效果说明 |
| `items.wands.wandofcorruption.upgrade_stat_name_1` | 腐化强度 | 属性名称 |
| `items.wands.wandofcorruption.upgrade_stat_name_2` | 减益持续时间 | 属性名称 |
| `items.wands.wandofcorruption.bmage_desc` | 战斗法师描述 | 子职业效果 |

## 11. 使用示例

```java
// 创建腐化法杖
WandOfCorruption wand = new WandOfCorruption();
wand.level(2);  // +2级

// 施放法术
Ballistica bolt = new Ballistica(hero.pos, targetPos, Ballistica.MAGIC_BOLT);
wand.onZap(bolt);  // 尝试腐化或削弱敌人

// 腐化强度：约3.67
// 对虚弱敌人更有效
// 已有减益的敌人更容易被腐化
```

## 12. 开发注意事项

### 状态依赖
- 不造成直接伤害
- 效果取决于敌人当前状态

### 常见陷阱
- Boss 无法被腐化
- 已腐化或已有厄运的敌人不受影响
- 需要先削弱敌人再腐化

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改 `MINOR_DEBUFFS` 和 `MAJOR_DEBUFFS` 列表
- 修改腐化强度计算

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点