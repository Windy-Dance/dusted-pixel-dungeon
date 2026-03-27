# WandOfLightning 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/wands/WandOfLightning.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.wands |
| **文件类型** | class |
| **继承关系** | extends DamageWand |
| **代码行数** | 215 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
雷霆法杖是一种连锁伤害法杖，具有以下特点：
- 对目标发射闪电
- 闪电在附近敌人间跳跃
- 在水中效果更强（伤害不衰减）
- 可能伤害施法者（自身伤害减半）
- 战斗法师子职业可获得起电效果

### 系统定位
位于 DamageWand 层次，是主要的连锁伤害型法杖，适合对付密集敌群。

## 3. 结构总览

### 主要成员概览
- **伤害方法**：`min(int lvl)`, `max(int lvl)`
- **连锁机制**：`arc(Char)` - 闪电跳跃逻辑
- **内部字段**：`affected` - 受影响的角色列表，`arcs` - 闪电弧线列表
- **内部类**：`LightningCharge` - 起电Buff

## 4. 继承与协作关系

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `min(int lvl)` | 返回 5 + lvl |
| `max(int lvl)` | 返回 10 + 5 * lvl |
| `onZap(Ballistica)` | 施放闪电连锁 |
| `onHit(MagesStaff, Char, Char, int)` | 近战命中施加起电 |
| `fx(Ballistica, Callback)` | 闪电视觉效果 |
| `staffFx(MagesStaff.StaffParticle)` | 手杖粒子效果 |

## 5. 字段/常量详解

### 实例字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| `affected` | ArrayList&lt;Char&gt; | 受影响的角色列表 |
| `arcs` | ArrayList&lt;Lightning.Arc&gt; | 闪电弧线列表 |

## 6. 构造与初始化机制

### 初始化块
```java
{
    image = ItemSpriteSheet.WAND_LIGHTNING;
}
```

## 7. 方法详解

### min(int lvl)
**可见性**：public

**是否覆写**：是

**方法职责**：返回最小伤害

**返回值**：int，`5 + lvl`

---

### max(int lvl)
**可见性**：public

**是否覆写**：是

**方法职责**：返回最大伤害

**返回值**：int，`10 + 5 * lvl`

---

### onZap(Ballistica bolt)
**可见性**：public

**是否覆写**：是

**方法职责**：施放闪电连锁伤害

**核心实现逻辑**：
1. 从受影响列表移除盟友（除非是主要目标）和有起电效果的目标
2. 计算伤害乘数：`0.4 + (0.6 / affected.size())`
3. 如果主要目标在水中，所有目标受全额伤害
4. 对所有受影响目标造成伤害（自身伤害减半）

**伤害分配公式**：
```java
float multiplier = 0.4f + (0.6f / affected.size());
if (Dungeon.level.water[bolt.collisionPos]) multiplier = 1f;
```

---

### arc(Char ch)
**可见性**：private

**方法职责**：处理闪电跳跃

**核心实现逻辑**：
1. 计算跳跃距离：水中2格，陆地1格（有起电效果+1）
2. 查找附近可跳跃的目标
3. 添加到受影响列表
4. 递归处理连锁跳跃

---

### onHit(MagesStaff staff, Char attacker, Char defender, int damage)
**可见性**：public

**是否覆写**：是

**方法职责**：近战命中时可能施加起电效果

**触发概率**：
- 0级：25%
- 1级：40%
- 2级：50%

**起电效果**：
- 持续10回合
- 免疫雷霆法杖伤害
- 增加闪电连锁范围

---

## LightningCharge 内部类

### 基本信息
起电是一个正面Buff，提供雷霆法杖伤害免疫和增强闪电连锁范围。

### 字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| `DURATION` | float | 持续时间常量，10回合 |

## 8. 对外暴露能力

继承自 DamageWand，提供连锁闪电伤害能力。

## 9. 运行机制与调用链

### 闪电连锁流程
1. `fx()` 收集所有受影响目标
2. `arc()` 递归处理跳跃
3. `onZap()` 分配伤害

### 水中加成
- 主目标在水中：所有目标全额伤害
- 目标在水中：跳跃距离+1

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.wands.wandoflightning.name` | 雷霆法杖 | 法杖名称 |
| `items.wands.wandoflightning.staff_name` | 雷霆魔杖 | 手杖名称 |
| `items.wands.wandoflightning.ondeath` | 你用雷霆法杖害死了自己... | 死亡消息 |
| `items.wands.wandoflightning.desc` | 描述文本 | 法杖描述 |
| `items.wands.wandoflightning.stats_desc` | 统计描述 | 伤害和连锁 |
| `items.wands.wandoflightning.bmage_desc` | 战斗法师描述 | 子职业效果 |
| `items.wands.wandoflightning$lightningcharge.name` | 起电 | Buff名称 |
| `items.wands.wandoflightning$lightningcharge.desc` | Buff描述 | 效果说明 |

## 11. 使用示例

```java
// 创建雷霆法杖
WandOfLightning wand = new WandOfLightning();
wand.level(2);  // +2级

// 施放法术（闪电会在敌人间跳跃）
Ballistica bolt = new Ballistica(hero.pos, targetPos, Ballistica.MAGIC_BOLT);
wand.fx(bolt, callback);  // 收集受影响目标
wand.onZap(bolt);  // 造成 7-20 伤害（分配给所有目标）

// 如果主目标在水中，所有目标受全额伤害
```

## 12. 开发注意事项

### 状态依赖
- 自身可能受到伤害（使用时需小心）
- 起电效果提供免疫
- 水中增强效果

### 常见陷阱
- 对密集敌群使用时自身受伤风险高
- 盟友不会被闪电影响（除非是主要目标）

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改 `arc()` 中的跳跃距离计算
- 修改伤害分配公式

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点