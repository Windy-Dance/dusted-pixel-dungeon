# ScrollOfLullaby 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/ScrollOfLullaby.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.scrolls |
| 类类型 | class |
| 继承关系 | extends Scroll |
| 代码行数 | 67 |

## 2. 类职责说明
ScrollOfLullaby 是摇篮曲卷轴类，使用后使视野内所有角色（包括英雄和敌人）陷入昏睡状态。昏睡的角色会在几回合后真正入睡。这是一个双刃剑般的工具，需要谨慎使用时机。

## 7. 方法详解

### doRead()
**签名**: `@Override public void doRead()`
**功能**: 使视野内所有角色昏睡
**实现逻辑**:
```java
// 第42-61行
detach(curUser.belongings.backpack);

// 显示音符特效
curUser.sprite.centerEmitter().start(Speck.factory(Speck.NOTE), 0.3f, 5);
Sample.INSTANCE.play(Assets.Sounds.LULLABY);

// 使视野内敌人昏睡
for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
    if (Dungeon.level.heroFOV[mob.pos]) {
        Buff.affect(mob, Drowsy.class, Drowsy.DURATION);
        mob.sprite.centerEmitter().start(Speck.factory(Speck.NOTE), 0.3f, 5);
    }
}

// 英雄也会昏睡
Buff.affect(curUser, Drowsy.class, Drowsy.DURATION);

GLog.i(Messages.get(this, "sooth"));

identify();
readAnimation();
```

## 11. 使用示例
```java
// 使用摇篮曲卷轴
ScrollOfLullaby scroll = new ScrollOfLullaby();
scroll.execute(hero, Scroll.AC_READ);
// 效果：视野内所有角色昏睡（包括英雄）
```

## 注意事项
1. 影响视野内所有角色
2. 英雄也会昏睡
3. 昏睡后几回合入睡
4. 已鉴定价值40金币

## 最佳实践
1. 在安全位置使用
2. 睡前准备逃跑
3. 与队友配合使用