# Cleanse 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/Cleanse.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells |
| **文件类型** | class |
| **继承关系** | extends ClericSpell |
| **代码行数** | 115 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Cleanse（神圣净化）清除自身及视野内盟友的所有负面效果，并给予护盾。

### 系统定位
作为第3层级的辅助法术：
- 需要天赋 CLEANSE 解锁
- 范围效果：影响视野内所有盟友
- 天赋等级提升后额外提供免疫效果

## 3. 方法详解

### icon()
**返回值**：HeroIcon.CLEANSE

### chargeUse()
**返回值**：固定返回 2

### canCast()
**前置条件**：需要 CLEANSE 天赋

### onCast()
**核心逻辑**：
```java
@Override
public void onCast(HolyTome tome, Hero hero) {
    ArrayList<Char> affected = new ArrayList<>();
    affected.add(hero);
    
    // 收集视野内的盟友
    for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
        if (Dungeon.level.heroFOV[mob.pos] && mob.alignment == Char.Alignment.ALLY) {
            affected.add(mob);
        }
    }
    
    // 生命联结下的额外盟友
    Char ally = PowerOfMany.getPoweredAlly();
    if (ally != null && ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null
            && !affected.contains(ally)) {
        affected.add(ally);
    }
    
    for (Char ch : affected) {
        // 移除负面Buff（排除盟友Buff和遗落行囊）
        for (Buff b : ch.buffs()) {
            if (b.type == Buff.buffType.NEGATIVE
                    && !(b instanceof AllyBuff)
                    && !(b instanceof LostInventory)) {
                b.detach();
            }
        }
        
        // 天赋>1时给予免疫效果
        if (hero.pointsInTalent(Talent.CLEANSE) > 1) {
            Buff.prolong(ch, PotionOfCleansing.Cleanse.class, 2 * (hero.pointsInTalent(Talent.CLEANSE)-1));
        }
        
        // 给予护盾
        Buff.affect(ch, Barrier.class).setShield(10 * hero.pointsInTalent(Talent.CLEANSE));
        new Flare(6, 32).color(0xFF4CD2, true).show(ch.sprite, 2f);
    }
    
    hero.spend(1f);
    hero.busy();
    hero.sprite.operate(hero.pos);
    Sample.INSTANCE.play(Assets.Sounds.READ);
    
    onSpellCast(tome, hero);
}
```

### desc()
**动态计算**：
- 免疫回合数：0/2/4（天赋1/2/3时）
- 护盾量：10/20/30（天赋1/2/3时）

## 4. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 |
|------|---------|
| actors.hero.spells.cleanse.name | 神圣净化 |
| actors.hero.spells.cleanse.short_desc | 清除减益并获得护盾。 |
| actors.hero.spells.cleanse.desc | 牧师清除自身和视野内盟友的所有负面状态效果... |

### 中文翻译来源
actors_zh.properties 文件

## 5. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已核对官方中文翻译