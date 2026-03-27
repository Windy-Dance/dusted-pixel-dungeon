# WandOfRegrowth 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/wands/WandOfRegrowth.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.wands |
| **文件类型** | class |
| **继承关系** | extends Wand |
| **代码行数** | 512 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
再生法杖是一种植物型法杖，具有以下特点：
- 在锥形区域内生成草地和植物
- 充能消耗越多，范围越大
- 可能生成特殊植物（结露草、种子荚、黄金莲）
- 有充能使用上限，超过后效果衰减
- 对范围内敌人施加缠绕

### 系统定位
位于 Wand 层次（非 DamageWand），是主要的资源生成型法杖，适合获取种子和草药。

## 3. 结构总览

### 主要成员概览
- **状态字段**：`totChrgUsed` - 总充能使用，`chargesOverLimit` - 超限充能
- **充能方法**：`chargesPerCast()` - 动态计算消耗
- **内部类**：`Dewcatcher` - 结露草，`Seedpod` - 种子荚，`Lotus` - 黄金莲

## 4. 继承与协作关系

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `tryToZap(Hero, int)` | 保存目标位置 |
| `onZap(Ballistica)` | 生成植物和草地 |
| `onHit(MagesStaff, Char, Char, int)` | 近战命中回复生命 |
| `fx(Ballistica, Callback)` | 植物锥形视觉 |
| `chargesPerCast()` | 动态充能消耗 |
| `statsDesc()` | 统计描述 |
| `upgradeStat1(int)` | 每充能再生量 |
| `upgradeStat2(int)` | 再生充能限制 |
| `staffFx(MagesStaff.StaffParticle)` | 手杖粒子效果 |

## 5. 字段/常量详解

### 实例字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| `totChrgUsed` | int | 总充能使用量 |
| `chargesOverLimit` | int | 超过限制的充能数 |
| `cone` | ConeAOE | 锥形区域 |
| `target` | int | 目标位置 |

## 6. 构造与初始化机制

### 初始化块
```java
{
    image = ItemSpriteSheet.WAND_REGROWTH;
    collisionProperties = Ballistica.WONT_STOP;
}
```

## 7. 方法详解

### tryToZap(Hero owner, int target)
**可见性**：public

**是否覆写**：是

**方法职责**：检查并保存目标位置

**核心实现逻辑**：
```java
if (super.tryToZap(owner, target)) {
    this.target = target;
    return true;
}
return false;
```

---

### onZap(Ballistica bolt)
**可见性**：public

**是否覆写**：是

**方法职责**：在锥形区域生成植物和草地

**核心实现逻辑**：
1. 计算枯萎概率（超限时增加）
2. 过滤不可生成的格子
3. 生成草地
4. 施加缠绕效果
5. 3充能时生成黄金莲
6. 随机生成特殊植物
7. 更新充能使用统计

**生成概率**：
- 高草：`3.67 + level/3` 格每充能
- 种子荚/结露草：`16%/33%/50%`（1/2/3充能）
- 随机种子：`33%/66%/100%`（1/2/3充能）

---

### chargeLimit(int heroLvl, int wndLvl)
**可见性**：private

**方法职责**：计算充能使用上限

**返回值**：int
- 10级以上：无限
- 否则：`20 + heroLvl * (2 + wndLvl) * (1 + wndLvl / (50 - 5 * wndLvl))`

---

### chargesPerCast()
**可见性**：protected

**是否覆写**：是

**方法职责**：计算每次施法消耗的充能

**返回值**：int，消耗当前充能的30%，范围1-3

---

### onHit(MagesStaff staff, Char attacker, Char defender, int damage)
**可见性**：public

**是否覆写**：是

**方法职责**：近战命中时在草地上回复生命

**触发条件**：攻击者或防御者站在草地上

**回复量**：`damage * (level + 2) / (level + 6) / 2`

---

## Dewcatcher 内部类

### 基本信息
结露草是一种特殊植物，激活时生成3-6滴露水。

### 方法
| 方法 | 职责 |
|------|------|
| `activate(Char)` | 生成露水 |

---

## Seedpod 内部类

### 基本信息
种子荚是一种特殊植物，激活时生成2-4粒随机种子。

### 方法
| 方法 | 职责 |
|------|------|
| `activate(Char)` | 生成种子 |

---

## Lotus 内部类

### 基本信息
黄金莲是一种特殊NPC，增强植物效果。

### 属性
- 视野范围：法杖等级
- 生命值：25 + 3 * level
- 免疫所有伤害和效果

### 效果
- 种子保存概率：40% + 4% * level
- 植物立即触发效果
- 增强涂药飞镖耐久

## 8. 对外暴露能力

继承自 Wand，提供生成植物和资源的能力。

## 9. 运行机制与调用链

### 充能限制机制
- 使用上限随英雄和法杖等级增长
- 超限后草会变成枯萎草
- 10级以上法杖无限制

### 生成流程
1. `chargesPerCast()` 计算消耗
2. `fx()` 创建锥形视觉
3. `onZap()` 生成植物

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.wands.wandofregrowth.name` | 再生法杖 | 法杖名称 |
| `items.wands.wandofregrowth.staff_name` | 再生魔杖 | 手杖名称 |
| `items.wands.wandofregrowth.desc` | 描述文本 | 法杖描述 |
| `items.wands.wandofregrowth.stats_desc` | 统计描述 | 范围和效果 |
| `items.wands.wandofregrowth.degradation` | 充能限制提示 | 效果衰减警告 |
| `items.wands.wandofregrowth.upgrade_stat_name_1` | 每充能再生量 | 属性名称 |
| `items.wands.wandofregrowth.upgrade_stat_name_2` | 再生充能限制 | 属性名称 |
| `items.wands.wandofregrowth.bmage_desc` | 战斗法师描述 | 子职业效果 |
| `items.wands.wandofregrowth$dewcatcher.name` | 结露草 | 植物名称 |
| `items.wands.wandofregrowth$seedpod.name` | 种子荚 | 植物名称 |
| `items.wands.wandofregrowth$lotus.name` | 黄金莲 | NPC名称 |

## 11. 使用示例

```java
// 创建再生法杖
WandOfRegrowth wand = new WandOfRegrowth();
wand.level(2);  // +2级

// 施放法术
Ballistica bolt = new Ballistica(hero.pos, targetPos, Ballistica.WONT_STOP);
wand.fx(bolt, callback);
wand.onZap(bolt);  // 在锥形区域生成草地和植物

// 充能限制：约34次（10级前）
// 超限后效果衰减
```

## 12. 开发注意事项

### 状态依赖
- 效果受充能使用历史影响
- 草地生成依赖地形类型

### 常见陷阱
- 超限使用效果衰减
- 植物可能被敌人踩踏

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改充能限制计算
- 修改生成概率

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点