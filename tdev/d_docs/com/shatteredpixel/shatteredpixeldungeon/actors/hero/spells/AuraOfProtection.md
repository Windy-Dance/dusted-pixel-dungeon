# AuraOfProtection 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/AuraOfProtection.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends ClericSpell |
| **代码行数** | 113 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
AuraOfProtection（守御灵光）为圣骑士及其附近盟友提供伤害减免和刻印强化效果。

### 系统定位
作为第3层级的圣骑士专属增益法术：
- 需要天赋 AURA_OF_PROTECTION 解锁
- 创建持续20回合的防护光环
- 范围内盟友获得伤害减免和刻印效果

## 3. 方法详解

### icon()
**返回值**：HeroIcon.AURA_OF_PROTECTION

### chargeUse()
**返回值**：固定返回 2

### canCast()
**前置条件**：需要 AURA_OF_PROTECTION 天赋

### onCast()
**核心逻辑**：
```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    Buff.affect(hero, AuraBuff.class, AuraBuff.DURATION);
    Sample.INSTANCE.play(Assets.Sounds.READ);
    hero.spend(1f);
    hero.busy();
    hero.sprite.operate(hero.pos);
    onSpellCast(tome, hero);
}
```

### desc()
**动态计算**：根据天赋等级计算伤害减免和刻印强化百分比

## 4. 内部类详解

### AuraBuff

**类型**：public static class extends FlavourBuff

**职责**：守御灵光效果Buff，显示粒子效果。

**常量**：
- `DURATION = 20f`：持续时间

**主要方法**：
- `icon()`：返回 PROT_AURA 图标
- `fx(boolean)`：管理光粒子效果

**字段**：
- `particles`：Emitter，粒子发射器

## 5. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.auraofprotection.name | 守御灵光 |
| actors.hero.spells.auraofprotection.short_desc | 强化圣骑士与附近盟友的防御。 |
| actors.hero.spells.auraofprotection.desc | 圣骑士开始辐射出保护性能量... |

### 中文翻译来源
actors_zh.properties 文件

## 6. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译