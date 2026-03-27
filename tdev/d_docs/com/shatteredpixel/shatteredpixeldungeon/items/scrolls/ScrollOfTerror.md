# ScrollOfTerror 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/ScrollOfTerror.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.scrolls |
| 类类型 | class |
| 继承关系 | extends Scroll |
| 代码行数 | 81 |

## 2. 类职责说明
ScrollOfTerror 是恐惧卷轴类，使用后使视野内所有敌人陷入恐惧状态。恐惧的敌人会逃离英雄，无法攻击。这是一个强大的群体控制工具，特别适合在被包围时使用。

## 7. 方法详解

### doRead()
**签名**: `@Override public void doRead()`
**功能**: 使视野内敌人恐惧
**实现逻辑**:
```java
// 第43-75行
detach(curUser.belongings.backpack);

// 显示红色光芒
new Flare(5, 32).color(0xFF0000, true).show(curUser.sprite, 2f);
Sample.INSTANCE.play(Assets.Sounds.READ);

int count = 0;
Mob affected = null;

// 遍历所有敌人
for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
    if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
        // 施加恐惧状态
        Buff.affect(mob, Terror.class, Terror.DURATION).object = curUser.id();

        if (mob.buff(Terror.class) != null) {
            count++;
            affected = mob;
        }
    }
}

// 显示结果消息
switch (count) {
    case 0:
        GLog.i(Messages.get(this, "none"));
        break;
    case 1:
        GLog.i(Messages.get(this, "one", affected.name()));
        break;
    default:
        GLog.i(Messages.get(this, "many"));
}

identify();
readAnimation();
```

## 11. 使用示例
```java
// 使用恐惧卷轴
ScrollOfTerror scroll = new ScrollOfTerror();
scroll.execute(hero, Scroll.AC_READ);
// 效果：视野内敌人逃离
```

## 注意事项
1. 只影响视野内的敌人
2. 友军不受影响
3. 某些敌人免疫恐惧
4. 已鉴定价值40金币

## 最佳实践
1. 被包围时使用逃脱
2. 分散敌群逐个击破
3. 逃跑时阻止追击