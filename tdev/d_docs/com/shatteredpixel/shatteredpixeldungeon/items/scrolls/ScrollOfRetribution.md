# ScrollOfRetribution 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/ScrollOfRetribution.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.scrolls |
| 类类型 | class |
| 继承关系 | extends Scroll |
| 代码行数 | 88 |

## 2. 类职责说明
ScrollOfRetribution 是惩罚卷轴类，使用后对视野内所有敌人造成伤害并使其失明。伤害量取决于英雄当前生命值与最大生命值的差距——生命值越低，伤害越高。作为代价，英雄也会受到虚弱和失明状态。这是一个高风险高回报的绝境反击工具。

## 7. 方法详解

### doRead()
**签名**: `@Override public void doRead()`
**功能**: 对敌人造成伤害并付出代价
**实现逻辑**:
```java
// 第45-82行
detach(curUser.belongings.backpack);
GameScene.flash(0x80FFFFFF);

// 计算伤害强度（基于生命值差距）
float hpPercent = (curUser.HT - curUser.HP) / (float)(curUser.HT);
float power = Math.min(4f, 4.45f * hpPercent);

Sample.INSTANCE.play(Assets.Sounds.BLAST);
GLog.i(Messages.get(this, "blast"));

// 收集目标
ArrayList<Mob> targets = new ArrayList<>();
for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
    if (Dungeon.level.heroFOV[mob.pos]) {
        targets.add(mob);
    }
}

// 对每个目标造成伤害
for (Mob mob : targets) {
    // 伤害：10%最大生命值 + 基于当前生命值的额外伤害
    mob.damage(Math.round(mob.HT/10f + (mob.HP * power * 0.225f)), this);
    if (mob.isAlive()) {
        Buff.prolong(mob, Blindness.class, Blindness.DURATION);
    }
}

// 英雄代价：虚弱和失明
Buff.prolong(curUser, Weakness.class, Weakness.DURATION);
Buff.prolong(curUser, Blindness.class, Blindness.DURATION);
Dungeon.observe();

identify();
readAnimation();
```

## 11. 使用示例
```java
ScrollOfRetribution scroll = new ScrollOfRetribution();
scroll.execute(hero, Scroll.AC_READ);
// 效果：对视野内敌人造成伤害（生命值越低伤害越高）
// 代价：英雄虚弱和失明
```

## 注意事项
1. 伤害取决于生命值差距
2. 英雄也会受负面影响
3. 只影响视野内敌人
4. 已鉴定价值40金币

## 最佳实践
1. 在生命值低时使用效果最好
2. 绝境反击的最后手段
3. 配合治疗物品恢复