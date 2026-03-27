# 创建新怪物教程

## 目标
完成这个教程后，你将能够创建一个自定义怪物，包含AI行为、属性设置、战利品掉落等。

## 前置知识
- 阅读 [Mob API 参考](../../reference/actors/mob-api.md)
- 阅读 [Char API 参考](../../reference/actors/char-api.md)
- 了解 Java 基础

## 最终成果
创建一个"暗影刺客"怪物：
- 中等生命值和攻击力
- 可以隐身接近玩家
- 掉落特殊物品

## 步骤

### 步骤1：创建怪物类
**目标**：创建怪物的基本框架
**文件**：`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/ShadowAssassin.java`
**代码**：

```java
/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.dustedpixel.dustedpixeldungeon.actors.mobs;

import com.dustedpixel.dustedpixeldungeon.sprites.ShadowAssassinSprite;
import com.watabou.utils.Random;

public class ShadowAssassin extends Mob {

    {
        spriteClass = ShadowAssassinSprite.class;

        // 基础属性将在步骤2中设置
        HP = HT = 0;
        defenseSkill = 0;

        EXP = 6;
        maxLvl = 12;

        // 战利品将在步骤5中设置
        loot = null;
        lootChance = 0f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(6, 12);
    }

    @Override
    public int attackSkill(Char target) {
        return 15;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 3);
    }
}
```

### 步骤2：设置属性
**目标**：定义生命值、攻击力、防御力
**代码**：

```java
{
	spriteClass = ShadowAssassinSprite.class;
	
	// 暗影刺客拥有中等生命值
	HP = HT = 45;
	// 较高的防御技能，使其更难被命中
	defenseSkill = 18;
	
	EXP = 6;
	maxLvl = 12;
	
	// 战利品将在步骤5中设置
	loot = null;
	lootChance = 0f;
}
```

### 步骤3：自定义AI行为
**目标**：重写选择目标和移动逻辑
**代码**：

```java
@Override
protected boolean act() {
	// 调用父类的act方法来处理基本行为
	super.act();
	
	// 如果没有敌人，尝试寻找目标
	if (enemy == null || !enemy.isAlive()) {
		enemy = chooseEnemy();
	}
	
	// 如果有可见的敌人，进入狩猎状态
	if (enemy != null && fieldOfView[enemy.pos] && enemy.invisible <= 0) {
		state = HUNTING;
		target = enemy.pos;
	} else if (state == HUNTING) {
		// 如果失去目标视野，进入徘徊状态
		state = WANDERING;
		target = randomDestination();
	}
	
	return true;
}

@Override
protected Char chooseEnemy() {
	// 优先选择英雄作为目标
	if (fieldOfView[Dungeon.hero.pos] && Dungeon.hero.invisible <= 0) {
		return Dungeon.hero;
	}
	
	// 如果看不到英雄，返回null
	return null;
}
```

### 步骤4：添加特殊能力
**目标**：实现隐身能力
**代码**：

```java
import com.dustedpixel.dustedpixeldungeon.actors.buffs.Buff;
import com.dustedpixel.dustedpixeldungeon.actors.buffs.Invisibility;
import com.dustedpixel.dustedpixeldungeon.Dungeon;
import com.watabou.utils.Random;

// 在类的开头添加字段
private boolean hasUsedInvisibility = false;
        private static final float INVISIBILITY_COOLDOWN = 30f;
        private float invisibilityCooldown = 0f;

        @Override
        protected boolean act() {
            super.act();

            // 更新隐身冷却时间
            if (invisibilityCooldown > 0) {
                invisibilityCooldown -= 1f;
            }

            // 如果还没有使用过隐身，并且冷却时间结束
            if (!hasUsedInvisibility && invisibilityCooldown <= 0) {
                // 当距离英雄较远时（大于4格），使用隐身
                if (Dungeon.level.distance(pos, Dungeon.hero.pos) > 4) {
                    Buff.affect(this, Invisibility.class, Invisibility.DURATION);
                    hasUsedInvisibility = true;
                    invisibilityCooldown = INVISIBILITY_COOLDOWN;

                    // 切换到徘徊状态，开始悄悄接近
                    state = WANDERING;
                    target = Dungeon.hero.pos;
                }
            }

            // 如果正在隐身且靠近英雄（3格以内），取消隐身并攻击
            if (invisible > 0 && Dungeon.level.distance(pos, Dungeon.hero.pos) <= 3) {
                Invisibility.dispel(this);
                state = HUNTING;
                target = Dungeon.hero.pos;
            }

            // 敌人选择逻辑
            if (enemy == null || !enemy.isAlive()) {
                enemy = chooseEnemy();
            }

            if (enemy != null && fieldOfView[enemy.pos] && enemy.invisible <= 0) {
                state = HUNTING;
                target = enemy.pos;
            } else if (state == HUNTING) {
                state = WANDERING;
                target = randomDestination();
            }

            return true;
        }
```

### 步骤5：设置战利品
**目标**：定义掉落物品
**代码**：

```java
import com.dustedpixel.dustedpixeldungeon.items.Gold;
import com.dustedpixel.dustedpixeldungeon.items.weapon.melee.Dagger;

{
spriteClass =ShadowAssassinSprite .class;

HP =HT =45;
defenseSkill =18;

EXP =6;
maxLvl =12;

// 50% 几率掉落金币，30% 几率掉落匕首
loot =new Object[]{Gold .class,Dagger .class};
lootChance =0.5f;
        }

@Override
public Item createLoot() {
    // 根据随机数决定掉落什么
    if (Random.Float() < 0.6f) {
        // 60% 几率掉落金币
        return new Gold(Random.NormalIntRange(20, 40));
    } else {
        // 40% 几率掉落匕首
        return new Dagger();
    }
}
```

### 步骤6：创建精灵图
**目标**：创建视觉表现
**文件**：`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/sprites/ShadowAssassinSprite.java`

**精灵图文件**：你需要在 `assets/sprites/` 目录下创建 `shadowassassin.png` 文件。这个PNG文件应该包含多个帧用于不同动画（空闲、奔跑、攻击、死亡）。

**Sprite类代码**：

```java
/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.dustedpixel.dustedpixeldungeon.sprites;

import com.dustedpixel.dustedpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class ShadowAssassinSprite extends MobSprite {

    public ShadowAssassinSprite() {
        super();

        // 使用我们在Assets.java中定义的纹理
        texture(Assets.Sprites.SHADOW_ASSASSIN);

        // 假设精灵图每个帧是12x16像素
        TextureFilm frames = new TextureFilm(texture, 12, 16);

        // 空闲动画：帧0, 1
        idle = new Animation(2, true);
        idle.frames(frames, 0, 0, 0, 1, 0, 0, 1, 1);

        // 奔跑动画：帧2, 3, 4, 5
        run = new Animation(12, true);
        run.frames(frames, 2, 3, 4, 5);

        // 攻击动画：帧6, 7, 0
        attack = new Animation(12, false);
        attack.frames(frames, 6, 7, 0);

        // 死亡动画：帧8, 9, 10
        die = new Animation(12, false);
        die.frames(frames, 8, 9, 10);

        // 播放空闲动画
        play(idle);
    }
}
```

**Assets.java修改**：
你需要在 `Assets.java` 文件中添加对新精灵图的引用：

```java
// 在Assets.java的SPRITES部分添加
public static final String SHADOW_ASSASSIN    = "sprites/shadowassassin.png";
```

### 步骤7：注册怪物
**目标**：将怪物添加到生成池
**代码**：

要将你的新怪物添加到游戏中，你需要修改 `MobSpawner.java` 文件中的 `standardMobRotation` 方法。

找到对应深度的case（例如深度3），然后添加你的怪物：

```java
case 3:
	//1x rat, 1x snake, 3x gnoll, 1x swarm, 1x crab, 1x shadow assassin
	return new ArrayList<>(Arrays.asList(Rat.class,
			Snake.class,
			Gnoll.class, Gnoll.class, Gnoll.class,
			Swarm.class,
			Crab.class,
			ShadowAssassin.class)); // 添加这行
```

如果你想要你的怪物在特定条件下生成（比如稀有怪物），你也可以在 `addRareMobs` 方法中添加：

```java
case 3:
	if (Random.Float() < 0.05f) rotation.add(ShadowAssassin.class); // 5% 几率
	return;
```

## 完整代码

```java
/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.dustedpixel.dustedpixeldungeon.actors.mobs;

import com.dustedpixel.dustedpixeldungeon.Dungeon;
import com.dustedpixel.dustedpixeldungeon.actors.Char;
import com.dustedpixel.dustedpixeldungeon.actors.buffs.Buff;
import com.dustedpixel.dustedpixeldungeon.actors.buffs.Invisibility;
import com.dustedpixel.dustedpixeldungeon.items.Gold;
import com.dustedpixel.dustedpixeldungeon.items.Item;
import com.dustedpixel.dustedpixeldungeon.items.weapon.melee.Dagger;
import com.dustedpixel.dustedpixeldungeon.sprites.ShadowAssassinSprite;
import com.watabou.utils.Random;

public class ShadowAssassin extends Mob {

    private boolean hasUsedInvisibility = false;
    private static final float INVISIBILITY_COOLDOWN = 30f;
    private float invisibilityCooldown = 0f;

    {
        spriteClass = ShadowAssassinSprite.class;

        HP = HT = 45;
        defenseSkill = 18;

        EXP = 6;
        maxLvl = 12;

        loot = new Object[]{Gold.class, Dagger.class};
        lootChance = 0.5f;
    }

    @Override
    protected boolean act() {
        super.act();

        // 更新隐身冷却时间
        if (invisibilityCooldown > 0) {
            invisibilityCooldown -= 1f;
        }

        // 如果还没有使用过隐身，并且冷却时间结束
        if (!hasUsedInvisibility && invisibilityCooldown <= 0) {
            // 当距离英雄较远时（大于4格），使用隐身
            if (Dungeon.level.distance(pos, Dungeon.hero.pos) > 4) {
                Buff.affect(this, Invisibility.class, Invisibility.DURATION);
                hasUsedInvisibility = true;
                invisibilityCooldown = INVISIBILITY_COOLDOWN;

                // 切换到徘徊状态，开始悄悄接近
                state = WANDERING;
                target = Dungeon.hero.pos;
            }
        }

        // 如果正在隐身且靠近英雄（3格以内），取消隐身并攻击
        if (invisible > 0 && Dungeon.level.distance(pos, Dungeon.hero.pos) <= 3) {
            Invisibility.dispel(this);
            state = HUNTING;
            target = Dungeon.hero.pos;
        }

        // 敌人选择逻辑
        if (enemy == null || !enemy.isAlive()) {
            enemy = chooseEnemy();
        }

        if (enemy != null && fieldOfView[enemy.pos] && enemy.invisible <= 0) {
            state = HUNTING;
            target = enemy.pos;
        } else if (state == HUNTING) {
            state = WANDERING;
            target = randomDestination();
        }

        return true;
    }

    @Override
    protected Char chooseEnemy() {
        // 优先选择英雄作为目标
        if (fieldOfView[Dungeon.hero.pos] && Dungeon.hero.invisible <= 0) {
            return Dungeon.hero;
        }

        // 如果看不到英雄，返回null
        return null;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(6, 12);
    }

    @Override
    public int attackSkill(Char target) {
        return 15;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 3);
    }

    @Override
    public Item createLoot() {
        // 根据随机数决定掉落什么
        if (Random.Float() < 0.6f) {
            // 60% 几率掉落金币
            return new Gold(Random.NormalIntRange(20, 40));
        } else {
            // 40% 几率掉落匕首
            return new Dagger();
        }
    }
}
```

## 测试验证

1. **编译游戏**：确保所有代码都能正确编译，没有语法错误。
2. **运行游戏**：启动游戏并进入相应的深度（你注册怪物的深度）。
3. **观察行为**：
   - 暗影刺客应该在距离较远时隐身
   - 当靠近玩家时应该取消隐身并攻击
   - 检查掉落物品是否符合预期
4. **调试技巧**：
   - 如果怪物没有出现，检查 `MobSpawner.java` 中的注册是否正确
   - 如果隐身不工作，检查 `invisible` 计数器是否正确增加和减少
   - 使用调试日志来跟踪AI状态变化

## 进阶修改

### 1. 添加更多能力
你可以为暗影刺客添加更多特殊能力：

```java
// 添加暴击能力
@Override
public int damageRoll() {
	int damage = Random.NormalIntRange(6, 12);
	// 20% 几率造成双倍伤害
	if (Random.Float() < 0.2f) {
		damage *= 2;
	}
	return damage;
}
```

### 2. 自定义AI状态
创建完全自定义的AI状态：

```java
public class StealthyHunting implements AiState {
	public static final String TAG = "STEALTHY_HUNTING";
	
	@Override
	public boolean act(boolean enemyInFOV, boolean justAlerted) {
		// 自定义隐身狩猎逻辑
		return true;
	}
}

// 在怪物类中使用
public AiState STEALTHY_HUNTING = new StealthyHunting();
```

### 3. 特殊音效和视觉效果
添加特殊的攻击效果：

```java
@Override
public void onAttackComplete() {
	super.onAttackComplete();
	
	// 添加特殊攻击效果
	if (Random.Float() < 0.3f) {
		// 30% 几率使目标眩晕
		Buff.affect(enemy, Vertigo.class, 2f);
	}
}
```

## 常见问题

**Q: 我的怪物不生成怎么办？**
A: 检查以下几点：
1. 确保在 `MobSpawner.java` 中正确注册了怪物
2. 确保你进入了正确的深度
3. 检查是否有编译错误

**Q: 隐身效果不显示怎么办？**
A: 
1. 确保精灵图正确加载
2. 检查 `invisible` 计数器是否正确管理
3. 确保调用了 `Buff.affect(this, Invisibility.class, duration)`

**Q: 如何调整怪物的难度？**
A: 修改以下属性：
- `HP` 和 `HT`：增加生命值
- `defenseSkill`：增加防御技能值
- `damageRoll()`：增加伤害范围
- `attackSkill()`：增加攻击技能值

**Q: 怪物掉落物品不正确怎么办？**
A: 检查 `createLoot()` 方法的实现，确保返回正确的Item实例。

**Q: 如何让怪物只在特定条件下生成？**
A: 你可以在 `MobSpawner.java` 的 `addRareMobs` 方法中添加条件逻辑，或者创建自定义的生成逻辑。

通过这个教程，你应该能够创建一个功能完整的自定义怪物。记住，最重要的是测试和迭代——创建一个基础版本，然后逐步添加和调整功能直到达到你想要的效果。