# ScrollOfRage 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/ScrollOfRage.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.scrolls |
| 类类型 | class |
| 继承关系 | extends Scroll |
| 代码行数 | 66 |

## 2. 类职责说明
ScrollOfRage 是狂怒卷轴类，使用后召唤所有敌人靠近英雄，并使视野内的非友军敌人陷入狂暴状态（Amok）。狂暴的敌人会攻击最近的生物，包括其他敌人。这是一个强大的战术工具，可以让敌人互相攻击。

## 7. 方法详解

### doRead()
**签名**: `@Override public void doRead()`
**功能**: 召唤敌人并使其狂暴
**实现逻辑**:
```java
// 第43-60行
detach(curUser.belongings.backpack);

// 召唤所有敌人靠近
for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
    mob.beckon(curUser.pos);
    
    // 视野内的非友军敌人陷入狂暴
    if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
        Buff.prolong(mob, Amok.class, 5f);
    }
}

GLog.w(Messages.get(this, "roar"));
identify();

// 显示尖叫特效
curUser.sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3);
Sample.INSTANCE.play(Assets.Sounds.CHALLENGE);

readAnimation();
```

## 11. 使用示例
```java
ScrollOfRage scroll = new ScrollOfRage();
scroll.execute(hero, Scroll.AC_READ);
// 效果：所有敌人靠近并狂暴
```

## 注意事项
1. 召唤所有敌人（包括视野外的）
2. 只有视野内敌人陷入狂暴
3. 狂暴持续5回合
4. 已鉴定价值40金币

## 最佳实践
1. 在有多个敌人时使用
2. 配合强力武器击杀被分散的敌人
3. 让敌人互相攻击