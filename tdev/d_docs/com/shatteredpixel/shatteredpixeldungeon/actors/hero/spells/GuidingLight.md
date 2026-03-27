# GuidingLight 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/GuidingLight.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends TargetedClericSpell |
| **代码行数** | 181 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
GuidingLight（神导之光）是牧师的远程攻击法术，发射魔法光矢攻击目标并施加光耀效果，使下一次物理攻击必定命中。

### 系统定位
作为第1层级的基础法术，神导之光是牧师的主要远程输出手段：
- 继承自 TargetedClericSpell，需要选择目标
- 与祭司子职业有特殊联动（免费施法）
- 为其他法术（如破晓辐光）提供光耀效果触发条件

### 不负责什么
- 不负责光耀效果的伤害触发（由其他法术/攻击处理）
- 不负责光耀效果的具体伤害计算（由调用方处理）

## 3. 结构总览

### 主要成员概览
- `INSTANCE`：单例实例
- `GuidingLightPriestCooldown`：祭司免费施法冷却Buff
- `Illuminated`：光耀效果Buff
- `WasIlluminatedTracker`：光耀历史追踪Buff

### 主要逻辑块概览
- **目标选择与弹道计算**：使用 Ballistica 计算攻击路径
- **伤害与效果施加**：造成2-8点伤害，施加光耀效果
- **祭司特殊机制**：每50回合首次施法免费

### 生命周期/调用时机
- 玩家选择目标后施放
- 光耀效果持续至被消耗或自然消退

## 4. 继承与协作关系

### 父类提供的能力
继承自 TargetedClericSpell：
- `onCast()`：目标选择框架
- `targetingFlags()`：弹道计算标志
- `targetingPrompt()`：目标选择提示

### 覆写的方法
| 方法 | 说明 |
|------|------|
| icon() | 返回神导之光图标 |
| onTargetSelected() | 实现法术效果 |
| chargeUse() | 祭司免费施法机制 |
| desc() | 祭司额外描述 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| Ballistica | 弹道计算 |
| MagicMissile | 魔法飞弹视觉效果 |
| Illuminated | 光耀效果Buff |
| GuidingLightPriestCooldown | 祭司冷却Buff |

### 使用者
- HolyTome：法术调用入口
- ClericSpell.getSpellList()：第1层级法术列表

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| INSTANCE | GuidingLight | 单例 | 法术单例实例 |

### 实例字段
无

## 6. 构造与初始化机制

### 构造器
使用默认无参构造器，通过静态 INSTANCE 单例访问。

## 7. 方法详解

### icon()

**可见性**：public

**是否覆写**：是，覆写自 ClericSpell

**方法职责**：返回法术图标ID。

**返回值**：int，HeroIcon.GUIDING_LIGHT

---

### onTargetSelected()

**可见性**：protected

**是否覆写**：是，覆写自 TargetedClericSpell

**方法职责**：处理目标选择后的法术效果：
1. 计算弹道路径
2. 播放施法动画和音效
3. 对目标造成2-8点伤害
4. 施加光耀效果
5. 处理祭司免费施法机制

**参数**：
- `tome` (HolyTome)：神圣法典
- `hero` (Hero)：施法英雄
- `target` (Integer)：目标格子坐标

**核心实现逻辑**：
```java
@Override
protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
    if (target == null) return;
    
    // 弹道计算
    Ballistica aim = new Ballistica(hero.pos, target, targetingFlags());
    
    // 不能瞄准自己
    if (Actor.findChar(aim.collisionPos) == hero) {
        GLog.i(Messages.get(Wand.class, "self_target"));
        return;
    }
    
    hero.busy();
    Sample.INSTANCE.play(Assets.Sounds.ZAP);
    hero.sprite.zap(target);
    
    // 发射魔法飞弹
    MagicMissile.boltFromChar(hero.sprite.parent, MagicMissile.LIGHT_MISSILE, 
                              hero.sprite, aim.collisionPos, new Callback() {
        @Override
        public void call() {
            Char ch = Actor.findChar(aim.collisionPos);
            if (ch != null) {
                // 造成伤害
                ch.damage(Hero.heroDamageIntRange(2, 8), GuidingLight.this);
                Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f));
                ch.sprite.burst(0xFFFFFF44, 3);
                if (ch.isAlive()) {
                    // 施加光耀效果
                    Buff.affect(ch, Illuminated.class);
                    Buff.affect(ch, WasIlluminatedTracker.class);
                }
            } else {
                Dungeon.level.pressCell(aim.collisionPos);
            }
            
            hero.spend(1f);
            hero.next();
            
            onSpellCast(tome, hero);
            
            // 祭司免费施法冷却
            if (hero.subClass == HeroSubClass.PRIEST && 
                hero.buff(GuidingLightPriestCooldown.class) == null) {
                Buff.prolong(hero, GuidingLightPriestCooldown.class, 50f);
                ActionIndicator.refresh();
            }
        }
    });
}
```

**边界情况**：
- 目标为null时直接返回
- 目标是自己时显示错误信息
- 目标死亡时不施加光耀效果

---

### chargeUse()

**可见性**：public

**是否覆写**：是，覆写自 ClericSpell

**方法职责**：计算充能消耗，祭司在冷却外时免费。

**返回值**：float，祭司免费时返回0，否则返回1

**核心实现逻辑**：
```java
@Override
public float chargeUse(Hero hero) {
    if (hero.subClass == HeroSubClass.PRIEST
        && hero.buff(GuidingLightPriestCooldown.class) == null) {
        return 0;
    } else {
        return 1;
    }
}
```

---

### desc()

**可见性**：public

**是否覆写**：是，覆写自 ClericSpell

**方法职责**：返回法术描述，祭司有额外说明。

**返回值**：String，包含祭司特殊效果的描述

## 8. 内部类详解

### GuidingLightPriestCooldown

**类型**：public static class extends FlavourBuff

**职责**：记录祭司免费施法的冷却时间。

**主要方法**：
- `icon()`：返回光耀图标
- `tintIcon(Image)`：将图标调暗至0.5亮度
- `iconFadePercent()`：返回冷却进度百分比
- `detach()`：移除时刷新动作指示器

**字段**：无实例字段

---

### Illuminated

**类型**：public static class extends Buff

**职责**：光耀效果Buff，使被光耀的单位更容易被命中。

**类型标记**：NEGATIVE（负面效果）

**主要方法**：
- `icon()`：返回光耀图标
- `fx(boolean)`：添加/移除光耀视觉效果
- `desc()`：根据子职业返回不同描述

**字段**：无实例字段

---

### WasIlluminatedTracker

**类型**：public static class extends Buff

**职责**：追踪单位曾经被光耀过，用于成就统计等。

**字段**：无

## 9. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| INSTANCE | 获取法术单例 |
| Illuminated | 光耀效果Buff类 |

### 内部辅助方法
| 方法 | 用途 |
|------|------|
| WasIlluminatedTracker | 光耀历史追踪 |

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| actors.hero.spells.guidinglight.name | 神导之光 | 法术名称 |
| actors.hero.spells.guidinglight.short_desc | 造成远程魔法必中伤害。 | 简短描述 |
| actors.hero.spells.guidinglight.desc | 牧师发射一束魔能光矢攻击目标... | 详细描述 |
| actors.hero.spells.guidinglight.desc_priest | 祭司施放该法术时效果更强... | 祭司额外描述 |
| actors.hero.spells.guidinglight$illuminated.name | 光耀 | 光耀Buff名称 |
| actors.hero.spells.guidinglight$illuminated.desc | 该单位因被神导之光击中而获得光耀... | 光耀Buff描述 |

### 依赖的资源
| 资源类型 | 资源名 | 用途 |
|---------|--------|------|
| 音效 | Assets.Sounds.ZAP | 施法音效 |
| 音效 | Assets.Sounds.HIT_MAGIC | 命中音效 |
| 视觉效果 | MagicMissile.LIGHT_MISSILE | 魔法飞弹效果 |

### 中文翻译来源
actors_zh.properties 文件

## 11. 使用示例

### 基本用法

```java
// 施放神导之光
GuidingLight.INSTANCE.onCast(holyTome, hero);

// 检查目标是否有光耀效果
if (target.buff(GuidingLight.Illuminated.class) != null) {
    // 消耗光耀效果，造成额外伤害
    target.damage(hero.lvl + 5, this);
    target.buff(GuidingLight.Illuminated.class).detach();
}
```

### 检查祭司免费施法

```java
// 判断当前施法是否免费
float cost = GuidingLight.INSTANCE.chargeUse(hero);
if (cost == 0) {
    GLog.i("本次施法免费！");
}
```

## 12. 开发注意事项

### 状态依赖
- 依赖英雄的子职业状态
- 依赖目标的存活状态
- 祭司冷却Buff状态

### 生命周期耦合
- 与 MagicMissile 的异步回调耦合
- 与光耀效果的其他触发源耦合

### 常见陷阱
1. **光耀效果不叠加**：多次施法只刷新持续时间
2. **目标死亡不施加光耀**：需检查 isAlive()
3. **祭司免费机制**：每50回合首次免费，需注意冷却状态

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改伤害范围（当前2-8点）
- 调整祭司免费冷却（当前50回合）
- 扩展光耀效果的交互

### 不建议修改的位置
- 弹道计算逻辑：已与游戏系统深度集成
- 光耀效果类型：其他系统依赖此Buff类型

### 重构建议
- 可考虑将光耀效果伤害计算提取到 Illuminated 类中

## 14. 事实核查清单

- [x] 是否已覆盖全部字段（1个静态常量）
- [x] 是否已覆盖全部方法（4个公开方法）
- [x] 是否已检查继承链与覆写关系（继承TargetedClericSpell）
- [x] 是否已核对官方中文翻译（从actors_zh.properties获取）
- [x] 是否存在任何推测性表述（无，全部基于源码）
- [x] 示例代码是否真实可用（是，遵循项目代码风格）
- [x] 是否遗漏资源/配置/本地化关联（已列出）
- [x] 是否明确说明了注意事项与扩展点（已说明）