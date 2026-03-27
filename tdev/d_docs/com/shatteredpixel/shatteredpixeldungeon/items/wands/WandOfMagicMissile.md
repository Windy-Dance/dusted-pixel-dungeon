# WandOfMagicMissile 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/wands/WandOfMagicMissile.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.wands |
| **文件类型** | class |
| **继承关系** | extends DamageWand |
| **代码行数** | 166 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
魔弹法杖是最基础的伤害型法杖，具有以下特点：
- 对单个目标造成魔法伤害
- 拥有比其他法杖更多的初始充能（3个）
- 升级后可暂时强化其他法杖的伤害
- 作为法师职业的初始法杖

### 系统定位
位于 DamageWand 层次，是最基础的伤害型法杖，作为其他法杖伤害的基准参照。

### 不负责什么
- 不提供特殊状态效果（如燃烧、冻结等）
- 不提供 AOE 伤害能力

## 3. 结构总览

### 主要成员概览
- **伤害方法**：`min(int lvl)`, `max(int lvl)`
- **特殊方法**：`initialCharges()` 返回 3
- **内部类**：`MagicCharge` - 魔力强化Buff

### 主要逻辑块概览
1. **施法流程**：`onZap()` → 伤害目标 → 可能施加 `MagicCharge`
2. **近战效果**：`onHit()` → 为其他法杖充能

## 4. 继承与协作关系

### 父类提供的能力
从 `DamageWand` 继承：
- `min()`, `max()`, `damageRoll()` 等伤害计算方法
- `statsDesc()`, `upgradeStat1()` 等描述方法

### 覆写的方法
| 方法 | 说明 |
|------|------|
| `min(int lvl)` | 返回 2+lvl |
| `max(int lvl)` | 返回 8+2*lvl |
| `onZap(Ballistica)` | 施法效果 |
| `onHit(MagesStaff, Char, Char, int)` | 近战命中效果 |
| `initialCharges()` | 返回 3 |

### 依赖的关键类
| 类 | 用途 |
|----|------|
| `MagicCharge` | 魔力强化内部Buff类 |
| `SpellSprite` | 法术视觉效果 |
| `Wand.Charger` | 法杖充能器 |

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无（继承自 DamageWand）

## 6. 构造与初始化机制

### 构造器
无显式构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.WAND_MAGIC_MISSILE;
}
```

## 7. 方法详解

### min(int lvl)
**可见性**：public

**是否覆写**：是

**方法职责**：返回指定等级的最小伤害

**返回值**：int，`2 + lvl`

---

### max(int lvl)
**可见性**：public

**是否覆写**：是

**方法职责**：返回指定等级的最大伤害

**返回值**：int，`8 + 2 * lvl`

---

### onZap(Ballistica bolt)
**可见性**：public

**是否覆写**：是

**方法职责**：施放法术，对目标造成伤害并可能施加魔力强化

**参数**：
- `bolt` (Ballistica)：投射路径

**核心实现逻辑**：
```java
Char ch = Actor.findChar(bolt.collisionPos);
if (ch != null) {
    wandProc(ch, chargesPerCast());
    ch.damage(damageRoll(), this);
    Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f));
    ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);
    
    // 如果有其他法杖等级较低，或已有魔力强化buff，则施加魔力强化
    for (Wand.Charger wandCharger : curUser.buffs(Wand.Charger.class)) {
        if (wandCharger.wand().buffedLvl() < buffedLvl() || curUser.buff(MagicCharge.class) != null) {
            Buff.prolong(curUser, MagicCharge.class, MagicCharge.DURATION).setup(this);
            break;
        }
    }
}
```

---

### onHit(MagesStaff staff, Char attacker, Char defender, int damage)
**可见性**：public

**是否覆写**：是

**方法职责**：近战命中时为其他法杖充能

**参数**：
- `staff` (MagesStaff)：法师手杖
- `attacker` (Char)：攻击者
- `defender` (Char)：防御者
- `damage` (int)：伤害值

**核心实现逻辑**：
```java
SpellSprite.show(attacker, SpellSprite.CHARGE);
for (Wand.Charger c : attacker.buffs(Wand.Charger.class)) {
    if (c.wand() != this) {
        c.gainCharge(0.5f * procChanceMultiplier(attacker));
    }
}
```

---

### initialCharges()
**可见性**：public

**是否覆写**：是

**方法职责**：返回初始充能数

**返回值**：int，固定返回 3

---

## MagicCharge 内部类

### 基本信息
魔力强化是一个正面Buff，用于提升其他法杖的有效等级。

### 字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| `DURATION` | float | 持续时间常量，4回合 |
| `level` | int | 强化的等级 |
| `wandJustApplied` | Wand | 刚施加此buff的法杖 |

### 方法
| 方法 | 职责 |
|------|------|
| `setup(Wand)` | 设置强化等级 |
| `level()` | 返回强化等级 |
| `wandJustApplied()` | 返回并清除刚施加的法杖引用 |
| `icon()` | 返回图标 |
| `tintIcon(Image)` | 设置图标颜色 |
| `desc()` | 返回描述 |

## 8. 对外暴露能力

### 显式 API
继承自 DamageWand，无新增公开方法。

### 内部辅助方法
`MagicCharge` 内部类提供魔力强化功能。

## 9. 运行机制与调用链

### 创建时机
- 法师职业初始携带
- 地牢随机生成

### 调用者
- `Wand.zapper` - 施法时
- `MagesStaff` - 近战命中时

### 被调用者
- `Wand.Charger` - 充能系统
- `MagicCharge` - 魔力强化

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.wands.wandofmagicmissile.name` | 魔弹法杖 | 法杖名称 |
| `items.wands.wandofmagicmissile.staff_name` | 魔弹魔杖 | 手杖名称 |
| `items.wands.wandofmagicmissile.desc` | 描述文本 | 法杖描述 |
| `items.wands.wandofmagicmissile.stats_desc` | 统计描述 | 伤害范围 |
| `items.wands.wandofmagicmissile.bmage_desc` | 战斗法师描述 | 子职业效果 |
| `items.wands.wandofmagicmissile.discover_hint` | 某位英雄初始携带该物品。 | 发现提示 |
| `items.wands.wandofmagicmissile$magiccharge.name` | 魔力强化 | Buff名称 |
| `items.wands.wandofmagicmissile$magiccharge.desc` | Buff描述 | 效果说明 |

## 11. 使用示例

```java
// 创建魔弹法杖
WandOfMagicMissile wand = new WandOfMagicMissile();
wand.level(2);  // +2级

// 施放法术
Ballistica bolt = new Ballistica(hero.pos, targetPos, Ballistica.MAGIC_BOLT);
wand.onZap(bolt);  // 造成 4-12 伤害

// 魔力强化效果
// 施放后，玩家获得 MagicCharge buff
// 其他法杖的下次施法将使用魔弹法杖的等级
```

## 12. 开发注意事项

### 状态依赖
- `MagicCharge` buff 会影响 `Wand.buffedLvl()` 的计算
- 施法时检查其他法杖等级决定是否施加魔力强化

### 常见陷阱
- `wandJustApplied()` 只能调用一次，之后返回 null
- 魔力强化不会强化自身

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改 `min(int)` 和 `max(int)` 调整伤害曲线
- 修改 `initialCharges()` 调整初始充能

### 不建议修改的位置
- `MagicCharge` 的逻辑会影响其他法杖的等级计算

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点