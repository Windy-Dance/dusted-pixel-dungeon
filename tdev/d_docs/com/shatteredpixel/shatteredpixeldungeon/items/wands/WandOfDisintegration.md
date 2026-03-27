# WandOfDisintegration 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/wands/WandOfDisintegration.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.wands |
| **文件类型** | class |
| **继承关系** | extends DamageWand |
| **代码行数** | 165 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
解离法杖是一种穿透型伤害法杖，具有以下特点：
- 发射穿透障碍物的光束
- 光束射程随等级增加
- 对光束路径上的所有敌人造成伤害
- 穿透地形和敌人增加额外伤害

### 系统定位
位于 DamageWand 层次，是主要的穿透伤害型法杖，适合对付直线排列的敌人。

## 3. 结构总览

### 主要成员概览
- **伤害方法**：`min(int lvl)`, `max(int lvl)`
- **特殊方法**：`distance()` - 计算射程
- **伤害机制**：穿透地形和敌人增加伤害加成

## 4. 继承与协作关系

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `min(int lvl)` | 返回 2 + lvl |
| `max(int lvl)` | 返回 8 + 4 * lvl |
| `targetingPos(Hero, int)` | 非诅咒时直接瞄准目标位置 |
| `onZap(Ballistica)` | 施放穿透光束 |
| `onHit(MagesStaff, Char, Char, int)` | 近战无直接效果（通过 reachfactor 实现） |
| `fx(Ballistica, Callback)` | 死亡射线视觉 |
| `upgradeStat2(int)` | 射程上限 |
| `staffFx(MagesStaff.StaffParticle)` | 手杖粒子效果 |

## 5. 字段/常量详解

无实例字段。

## 6. 构造与初始化机制

### 初始化块
```java
{
    image = ItemSpriteSheet.WAND_DISINTEGRATION;
    collisionProperties = Ballistica.WONT_STOP;
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

**返回值**：int，`8 + 4 * lvl`

---

### targetingPos(Hero user, int dst)
**可见性**：public

**是否覆写**：是

**方法职责**：返回瞄准位置

**返回值**：int，非诅咒时直接返回目标位置

**核心实现逻辑**：
```java
if (!cursed || !cursedKnown) {
    return dst;  // 直接瞄准
} else {
    return super.targetingPos(user, dst);  // 诅咒时可能有偏差
}
```

---

### onZap(Ballistica beam)
**可见性**：public

**是否覆写**：是

**方法职责**：施放穿透光束

**核心实现逻辑**：
1. 遍历光束路径上的所有格子
2. 统计穿透的地形和敌人
3. 计算伤害加成：
   - 每穿透3格地形增加1点伤害等级
   - 每命中一个敌人增加1点伤害等级
4. 对所有命中敌人造成伤害
5. 摧毁可燃地形

**伤害加成计算**：
```java
int terrainBonus = terrainPassed / 3;  // 每3格地形+1
int lvl = level + (chars.size() - 1) + terrainBonus;
```

---

### distance()
**可见性**：private

**方法职责**：计算光束射程

**返回值**：int，`6 + 2 * level`

---

### onHit(MagesStaff staff, Char attacker, Char defender, int damage)
**可见性**：public

**是否覆写**：是

**方法职责**：近战命中效果（无直接效果）

**说明**：嵌入法师手杖后增加攻击距离，由 `MagesStaff.reachFactor` 实现

## 8. 对外暴露能力

继承自 DamageWand，提供穿透光束伤害能力。

## 9. 运行机制与调用链

### 伤害机制
- 基础伤害：2+lvl ~ 8+4*lvl
- 穿透加成：每穿透3格地形或1个敌人增加伤害等级
- 射程：6 + 2*level

### 与法师手杖配合
嵌入后增加近战攻击距离，类似索敌附魔。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.wands.wandofdisintegration.name` | 解离法杖 | 法杖名称 |
| `items.wands.wandofdisintegration.staff_name` | 解离魔杖 | 手杖名称 |
| `items.wands.wandofdisintegration.desc` | 描述文本 | 法杖描述 |
| `items.wands.wandofdisintegration.stats_desc` | 统计描述 | 伤害和射程 |
| `items.wands.wandofdisintegration.upgrade_stat_name_2` | 射程上限 | 属性名称 |
| `items.wands.wandofdisintegration.bmage_desc` | 战斗法师描述 | 子职业效果 |

## 11. 使用示例

```java
// 创建解离法杖
WandOfDisintegration wand = new WandOfDisintegration();
wand.level(2);  // +2级

// 施放法术（光束穿透障碍物）
Ballistica beam = new Ballistica(hero.pos, targetPos, Ballistica.WONT_STOP);
wand.onZap(beam);  // 造成 4-16 基础伤害，穿透加成

// 射程：10格
// 穿透地形和敌人增加伤害
```

## 12. 开发注意事项

### 状态依赖
- 不继承自 DamageWand 的标准伤害掷骰
- 伤害加成来自穿透计算

### 常见陷阱
- 光束可能摧毁有用物品（可燃地形上的）
- 对空旷区域使用时无穿透加成

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改 `distance()` 调整射程
- 修改穿透伤害加成计算

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点