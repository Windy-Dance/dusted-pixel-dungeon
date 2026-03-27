# CharSprite 源码详解

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/sprites/CharSprite.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.sprites |
| **类类型** | class（非抽象） |
| **继承关系** | extends MovieClip implements Tweener.Listener, MovieClip.Listener |
| **代码行数** | 890 |

---

## 类职责

CharSprite 是游戏中所有角色精灵（包括英雄、怪物、NPC等）的基类。它是游戏视觉表现系统的核心，处理：

1. **基础动画**：空闲、奔跑、攻击、操作、施法、死亡等状态动画
2. **位置移动**：网格间移动、跳跃、转向等位置变化处理
3. **状态效果**：燃烧、冰冻、隐形、盾牌等多种视觉状态效果
4. **粒子系统**：与各种粒子效果（火焰、雪花、爱心等）的集成
5. **UI元素**：血量指示器、表情图标、浮动文字等UI组件管理
6. **阴影渲染**：可配置的阴影效果，支持不同高度和偏移
7. **同步控制**：与游戏逻辑的同步，确保动画完成后执行相应操作

**设计模式**：
- **观察者模式**：通过Callback接口通知动画完成事件
- **状态模式**：通过State枚举管理不同的视觉状态
- **组合模式**：聚合多种视觉效果组件（Emitter、Block、Halo等）

---

## 4. 继承与协作关系

```mermaid
classDiagram
    class MovieClip {
        <<abstract>>
        +Animation curAnim
        +boolean finished
        +play(Animation)
        +onComplete(Animation)
    }
    
    class Tweener~Listener~ {
        <<interface>>
        +onComplete(Tweener)
    }
    
    class MovieClip~Listener~ {
        <<interface>>
        +onComplete(Animation)
    }
    
    class CharSprite {
        +Char ch
        +volatile boolean isMoving
        +static final int DEFAULT
        +static final int POSITIVE  
        +static final int NEGATIVE
        +static final int WARNING
        +static final int NEUTRAL
        +enum State {BURNING, LEVITATING, INVISIBLE...}
        +link(Char)
        +move(int, int)
        +attack(int)
        +die()
        +add(State)
        +remove(State)
        +showStatus(int, String, Object...)
        +place(int)
        +turnTo(int, int)
        +jump(int, int, Callback)
        +showSleep()
        +hideSleep()
        +showAlert()
        +hideAlert()
        +burst(int, int)
        +bloodBurstA(PointF, int)
        +flash()
        +update()
        +kill()
    }
    
    class MobSprite {
        +fall()
        +update()
        +onComplete(Animation)
    }
    
    class HeroSprite {
        +updateArmor()
        +disguise(HeroClass)
        +sprint(float)
        +read()
        +avatar(Hero)
    }
    
    MovieClip <|-- CharSprite
    Tweener~Listener~ <|.. CharSprite
    MovieClip~Listener~ <|.. CharSprite
    CharSprite <|-- MobSprite
    CharSprite <|-- HeroSprite
```

---

## 静态常量

### 颜色常量

| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `DEFAULT` | int | 0xFFFFFF | 默认白色 |
| `POSITIVE` | int | 0x00FF00 | 正面效果绿色 |
| `NEGATIVE` | int | 0xFF0000 | 负面效果红色 |
| `WARNING` | int | 0xFF8800 | 警告效果橙色 |
| `NEUTRAL` | int | 0xFFFF00 | 中性效果黄色 |

### 时间常量

| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `DEFAULT_MOVE_INTERVAL` | float | 0.1f | 默认移动间隔时间 |
| `FLASH_INTERVAL` | float | 0.05f | 闪光持续时间 |

---

## 实例字段

### 视觉属性

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `perspectiveRaise` | float | 6/16f | 角色在透视视角下的抬升高度（像素） |
| `renderShadow` | boolean | false | 是否渲染阴影 |
| `shadowWidth` | float | 1.2f | 阴影宽度比例 |
| `shadowHeight` | float | 0.25f | 阴影高度比例 |
| `shadowOffset` | float | 0.25f | 阴影垂直偏移 |

### 动画字段

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `idle` | Animation | 空闲动画 |
| `run` | Animation | 奔跑动画 |
| `attack` | Animation | 攻击动画 |
| `operate` | Animation | 操作动画 |
| `zap` | Animation | 施法动画 |
| `die` | Animation | 死亡动画 |

### 状态效果字段

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `burning` | Emitter | 燃烧粒子效果 |
| `chilled` | Emitter | 冰冷粒子效果 |
| `marked` | Emitter | 标记粒子效果 |
| `levitation` | Emitter | 悬浮粒子效果 |
| `healing` | Emitter | 治疗粒子效果 |
| `hearts` | Emitter | 爱心粒子效果 |
| `iceBlock` | IceBlock | 冰冻方块效果 |
| `darkBlock` | DarkBlock | 黑暗方块效果 |
| `glowBlock` | GlowBlock | 发光方块效果 |
| `light` | TorchHalo | 火把光环效果 |
| `shield` | ShieldHalo | 盾牌光环效果 |
| `invisible` | AlphaTweener | 隐形透明度效果 |
| `aura` | Flare | 光环特效 |
| `emo` | EmoIcon | 表情图标 |
| `health` | CharHealthIndicator | 血量指示器 |

### 运动控制字段

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `motion` | PosTweener | 位置移动补间器 |
| `jumpTweener` | JumpTweener | 跳跃补间器 |
| `animCallback` | Callback | 动画完成回调 |
| `jumpCallback` | Callback | 跳跃完成回调 |

### 状态字段

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `flashTime` | float | 0 | 闪光剩余时间 |
| `sleeping` | boolean | false | 是否处于睡眠状态 |
| `ch` | Char | null | 关联的角色对象 |
| `isMoving` | volatile boolean | false | 是否正在移动（用于同步） |

### 状态管理集合

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `stateAdditions` | HashSet<State> | 待添加的状态集合 |
| `stateRemovals` | HashSet<State> | 待移除的状态集合 |

---

## 静态字段

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `moveInterval` | float | 全局移动间隔时间 |

---

## 枚举类型

### State

```java
public enum State {
    BURNING,        // 燃烧
    LEVITATING,     // 悬浮  
    INVISIBLE,      // 隐形
    PARALYSED,      // 瘫痪
    FROZEN,         // 冰冻
    ILLUMINATED,    // 照亮
    CHILLED,        // 冰冷
    DARKENED,       // 黑暗
    MARKED,         // 标记
    HEALING,        // 治疗
    SHIELDED,       // 盾牌
    HEARTS,         // 爱心
    GLOWING,        // 发光
    AURA            // 光环
}
```

---

## 7. 方法详解

### 构造方法

```java
public CharSprite() {
    super();
    listener = this;
}
```

**方法作用**：初始化字符精灵，设置动画监听器为自身。

---

### link(Char ch)

```java
public void link(Char ch) {
    linkVisuals(ch);
    this.ch = ch;
    ch.sprite = this;
    place(ch.pos);
    turnTo(ch.pos, Random.Int(Dungeon.level.length()));
    renderShadow = true;
    
    if (ch != Dungeon.hero) {
        if (health == null) {
            health = new CharHealthIndicator(ch);
        } else {
            health.target(ch);
        }
    }
    
    ch.updateSpriteState();
}
```

**方法作用**：将精灵与角色关联，并在游戏世界中放置。

**参数**：
- `ch` (Char)：要关联的角色

**关键步骤**：
1. 调用 `linkVisuals()` 更新视觉外观
2. 建立双向引用关系
3. 将精灵放置在角色位置
4. 随机转向
5. 为非英雄角色创建血量指示器
6. 更新精灵状态

---

### linkVisuals(Char ch)

```java
public void linkVisuals(Char ch) {
    // 默认不执行任何操作
}
```

**方法作用**：基于给定角色更新精灵视觉外观（子类重写）。

**参数**：
- `ch` (Char)：角色对象

---

### destroy()

```java
@Override
public void destroy() {
    super.destroy();
    if (ch != null && ch.sprite == this) {
        ch.sprite = null;
    }
}
```

**方法作用**：销毁精灵并清理关联关系。

---

### worldToCamera(int cell)

```java
public PointF worldToCamera(int cell) {
    final int csize = DungeonTilemap.SIZE;
    return new PointF(
        PixelScene.align(Camera.main, ((cell % Dungeon.level.width()) + 0.5f) * csize - width() * 0.5f),
        PixelScene.align(Camera.main, ((cell / Dungeon.level.width()) + 1.0f) * csize - height() - csize * perspectiveRaise)
    );
}
```

**方法作用**：将游戏世界坐标转换为相机坐标。

**参数**：
- `cell` (int)：游戏世界格子坐标

**返回值**：屏幕坐标点

**计算逻辑**：
- X坐标：居中对齐到格子中心
- Y坐标：底部对齐，并考虑透视抬升

---

### place(int cell)

```java
public void place(int cell) {
    point(worldToCamera(cell));
}
```

**方法作用**：将精灵放置在指定格子位置。

**参数**：
- `cell` (int)：目标格子

---

### showStatus(int color, String text, Object... args)

```java
public void showStatus(int color, String text, Object... args) {
    showStatusWithIcon(color, text, FloatingText.NO_ICON, args);
}
```

**方法作用**：显示浮动状态文字。

**参数**：
- `color` (int)：文字颜色
- `text` (String)：文字内容（支持格式化）
- `args` (Object...)：格式化参数

---

### showStatusWithIcon(int color, String text, int icon, Object... args)

```java
public void showStatusWithIcon(int color, String text, int icon, Object... args) {
    if (visible) {
        if (args.length > 0) {
            text = Messages.format(text, args);
        }
        float x = destinationCenter().x;
        float y = destinationCenter().y - height()/2f;
        int pos = DungeonTilemap.worldToTile(x, y + height(), Dungeon.level.width());
        if (ch != null) {
            FloatingText.show(x, y, pos, text, color, icon, true);
        } else {
            FloatingText.show(x, y, -1, text, color, icon, true);
        }
    }
}
```

**方法作用**：显示带图标的浮动状态文字。

**参数**：
- `icon` (int)：图标索引

---

### idle()

```java
public void idle() {
    play(idle);
}
```

**方法作用**：播放空闲动画。

---

### move(int from, int to)

```java
public void move(int from, int to) {
    turnTo(from, to);
    play(run);
    motion = new PosTweener(this, worldToCamera(to), moveInterval);
    motion.listener = this;
    parent.add(motion);
    isMoving = true;
    
    if (visible && Dungeon.level.water[from] && !ch.flying) {
        GameScene.ripple(from);
    }
}
```

**方法作用**：执行从一个位置到另一个位置的移动。

**参数**：
- `from` (int)：起始位置
- `to` (int)：目标位置

**关键特性**：
1. 自动转向
2. 播放奔跑动画
3. 创建位置补间动画
4. 水面涟漪效果
5. 设置 `isMoving` 标志用于同步

---

### attack(int cell)

```java
public void attack(int cell) {
    attack(cell, null);
}

public synchronized void attack(int cell, Callback callback) {
    animCallback = callback;
    turnTo(ch.pos, cell);
    play(attack);
}
```

**方法作用**：执行攻击动画。

**参数**：
- `callback` (Callback)：动画完成回调

**同步说明**：使用 `synchronized` 确保线程安全。

---

### operate(int cell) 和 zap(int cell)

类似 attack 方法，分别用于操作和施法动作。

---

### turnTo(int from, int to)

```java
public void turnTo(int from, int to) {
    int fx = from % Dungeon.level.width();
    int tx = to % Dungeon.level.width();
    if (tx > fx) {
        flipHorizontal = false;
    } else if (tx < fx) {
        flipHorizontal = true;
    }
}
```

**方法作用**：根据目标位置调整精灵朝向（水平翻转）。

**朝向规则**：
- 目标在右侧：不翻转
- 目标在左侧：水平翻转

---

### jump(int from, int to, Callback callback)

```java
public void jump(int from, int to, Callback callback) {
    float distance = Math.max(1f, Dungeon.level.trueDistance(from, to));
    jump(from, to, distance * 2, distance * 0.1f, callback);
}

public void jump(int from, int to, float height, float duration, Callback callback) {
    jumpCallback = callback;
    jumpTweener = new JumpTweener(this, worldToCamera(to), height, duration);
    jumpTweener.listener = this;
    parent.add(jumpTweener);
    turnTo(from, to);
}
```

**方法作用**：执行跳跃动画。

**参数**：
- `height` (float)：跳跃高度
- `duration` (float)：跳跃持续时间

**内部类 JumpTweener**：
- 实现抛物线运动轨迹
- 动态调整阴影偏移模拟跳跃效果

---

### die()

```java
public void die() {
    sleeping = false;
    processStateRemoval(State.PARALYSED);
    play(die);
    hideEmo();
    if (health != null) {
        health.killAndErase();
    }
}
```

**方法作用**：执行死亡动画。

**清理工作**：
1. 取消睡眠状态
2. 移除瘫痪状态
3. 隐藏表情图标
4. 销毁血量指示器

---

### emitter() / centerEmitter() / bottomEmitter()

```java
public Emitter emitter() {
    Emitter emitter = GameScene.emitter();
    if (emitter != null) emitter.pos(this);
    return emitter;
}
```

**方法作用**：创建位于精灵不同位置的粒子发射器。

---

### burst(int color, int n)

```java
public void burst(final int color, int n) {
    if (visible) {
        Splash.at(center(), color, n);
    }
}
```

**方法作用**：在精灵中心位置爆发粒子效果。

---

### bloodBurstA(PointF from, int damage)

```java
public void bloodBurstA(PointF from, int damage) {
    if (visible) {
        PointF c = center();
        int n = (int)Math.min(9 * Math.sqrt((double)damage / ch.HT), 9);
        Splash.at(c, PointF.angle(from, c), 3.1415926f / 2, blood(), n);
    }
}

public int blood() {
    return 0xFFBB0000;
}
```

**方法作用**：从指定方向喷溅血液效果（英雄重写为空方法以减少暴力内容）。

**参数计算**：
- 粒子数量基于伤害比例和最大生命值计算
- 最大9个粒子

---

### flash()

```java
public void flash() {
    ra = ba = ga = 1f;
    flashTime = FLASH_INTERVAL;
}
```

**方法作用**：使精灵短暂闪光（白色高亮）。

---

### add(State state) 和 remove(State state)

```java
public void add(State state) {
    if (state == State.PARALYSED) {
        paused = true;
    } else {
        synchronized (State.class) {
            stateRemovals.remove(state);
            stateAdditions.add(state);
        }
    }
}

public void remove(State state) {
    if (state == State.PARALYSED) {
        paused = false;
    } else {
        synchronized (State.class) {
            stateAdditions.remove(state);
            stateRemovals.add(state);
        }
    }
}
```

**方法作用**：添加或移除视觉状态效果。

**同步机制**：使用 `State.class` 作为锁对象确保线程安全。

**特殊处理**：PARALYSED 状态直接控制 `paused` 属性。

---

### aura(int color, int nRays)

```java
public void aura(int color, int nRays) {
    add(State.AURA);
    auraColor = color;
    auraRays = nRays;
}
```

**方法作用**：添加带颜色和射线数量的光环效果。

---

### processStateAddition(State state) 和 processStateRemoval(State state)

这两个方法分别处理状态的添加和移除，包含完整的 switch-case 语句处理所有 State 枚举值。

**状态效果对应表**：

| 状态 | 添加效果 | 移除效果 |
|------|----------|----------|
| BURNING | FlameParticle 火焰粒子 | 停止火焰粒子 |
| LEVITATING | Speck.JET 喷射粒子 | 停止喷射粒子 |
| INVISIBLE | AlphaTweener 透明度 | 恢复完全不透明 |
| PARALYSED | paused = true | paused = false |
| FROZEN | IceBlock 冰块效果 | 冰块融化 |
| ILLUMINATED | TorchHalo 火把光环 | 熄灭光环 |
| CHILLED | SnowParticle 雪花粒子 | 停止雪花粒子 |
| DARKENED | DarkBlock 黑暗方块 | 光明恢复 |
| MARKED | ShadowParticle 阴影粒子 | 停止阴影粒子 |
| HEALING | Speck.HEALING 治疗粒子 | 停止治疗粒子 |
| SHIELDED | ShieldHalo 盾牌光环 | 熄灭盾牌 |
| HEARTS | Speck.HEART 爱心粒子 | 停止爱心粒子 |
| GLOWING | GlowBlock 发光方块 | 停止发光 |
| AURA | Flare 光环特效 | 销毁光环 |

---

### update()

```java
@Override
public void update() {
    if (paused && ch != null && curAnim != null && !curAnim.looped && !finished) {
        listener.onComplete(curAnim);
        finished = true;
    }
    
    super.update();
    
    if (flashTime > 0 && (flashTime -= Game.elapsed) <= 0) {
        resetColor();
    }

    synchronized (State.class) {
        for (State s : stateAdditions) {
            processStateAddition(s);
        }
        stateAdditions.clear();
        for (State s : stateRemovals) {
            processStateRemoval(s);
        }
        stateRemovals.clear();
    }

    // 更新各种效果的可见性
    if (burning != null) burning.visible = visible;
    if (levitation != null) levitation.visible = visible;
    // ... 其他效果的可见性更新
    
    // 睡眠状态处理
    if (sleeping) {
        showSleep();
    } else {
        hideSleep();
    }
    
    // 表情图标可见性
    synchronized (EmoIcon.class) {
        if (emo != null && emo.alive) {
            emo.visible = visible;
        }
    }
}
```

**方法作用**：每帧更新精灵状态。

**更新顺序**：
1. 处理暂停动画完成
2. 更新基础动画
3. 处理闪光效果
4. 批量处理状态变更
5. 同步更新所有效果的可见性
6. 处理睡眠和表情状态

---

### draw()

```java
@Override
public void draw() {
    if (texture == null || (!dirty && buffer == null))
        return;

    if (renderShadow) {
        // 绘制阴影（先绘制）
        // ... 阴影矩阵计算和绘制
    }

    super.draw(); // 绘制精灵本身（后绘制）
}
```

**方法作用**：自定义绘制逻辑，支持阴影渲染。

**绘制顺序**：阴影 → 精灵主体（确保阴影在下方）

---

### onComplete(Tweener tweener) 和 onComplete(Animation anim)

这两个方法分别处理补间动画和普通动画的完成事件。

**Tweener 完成处理**：
- 跳跃完成：播放水波纹、调用回调、排序精灵
- 移动完成：设置 isMoving=false、通知角色、排序精灵

**Animation 完成处理**：
- 攻击完成：进入空闲状态、通知角色攻击完成
- 操作完成：进入空闲状态、通知角色操作完成
- 调用 animCallback 回调

---

## 静态方法详解

### setMoveInterval(float interval)

```java
public static void setMoveInterval(float interval) {
    moveInterval = interval;
}
```

**方法作用**：设置全局移动间隔时间（用于调整游戏速度）。

---

## 内部类详解

### JumpTweener

```java
private static class JumpTweener extends Tweener {
    public CharSprite visual;
    public PointF start;
    public PointF end;
    public float height;

    public JumpTweener(CharSprite visual, PointF pos, float height, float time) {
        super(visual, time);
        this.visual = visual;
        start = visual.point();
        end = pos;
        this.height = height;
    }

    @Override
    protected void updateValues(float progress) {
        float hVal = -height * 4 * progress * (1 - progress);
        visual.point(PointF.inter(start, end, progress).offset(0, hVal));
        visual.shadowOffset = 0.25f - hVal * 0.8f;
    }
}
```

**类作用**：实现跳跃的抛物线运动补间器。

**运动公式**：`hVal = -height * 4 * progress * (1 - progress)`
- 这是一个二次函数，在 progress=0.5 时达到最高点
- 阴影偏移动态调整，模拟跳跃时阴影的变化

---

## 与其他类的交互

### 被哪些类继承

| 类名 | 说明 |
|------|------|
| `MobSprite` | 怪物精灵基类 |
| `HeroSprite` | 英雄精灵 |
| `MissileSprite` | 投射物精灵 |
| `ItemSprite` | 物品精灵 |

### 使用了哪些类

| 类名 | 用于什么目的 |
|------|-------------|
| `Char` | 关联的游戏角色 |
| `Dungeon` | 游戏世界访问 |
| `GameScene` | 场景管理和特效 |
| `DungeonTilemap` | 坐标转换 |
| `PixelScene` | 像素对齐 |
| `Camera` | 相机操作 |
| `MovieClip` | 基础动画功能 |
| `Tweener` | 补间动画 |
| `Emitter` | 粒子系统 |
| `IceBlock/DarkBlock/GlowBlock` | 特殊视觉效果 |
| `TorchHalo/ShieldHalo` | 光环效果 |
| `Flare` | 光晕特效 |
| `EmoIcon` | 表情图标 |
| `CharHealthIndicator` | 血量指示器 |
| `FloatingText` | 浮动文字 |
| `Splash` | 粒子爆发效果 |
| `Messages` | 国际化文本 |
| `Assets` | 资源路径 |

---

## 11. 使用示例

### 基本使用流程

```java
// 1. 创建精灵实例
CharSprite sprite = new CharSprite();

// 2. 关联角色
sprite.link(character);

// 3. 执行各种动作
sprite.idle();                    // 空闲
sprite.move(fromPos, toPos);      // 移动
sprite.attack(targetPos);         // 攻击
sprite.showStatus(CharSprite.POSITIVE, "造成%d点伤害", damage); // 显示状态

// 4. 添加状态效果
sprite.add(CharSprite.State.BURNING);    // 燃烧
sprite.add(CharSprite.State.FROZEN);     // 冰冻

// 5. 移除状态效果
sprite.remove(CharSprite.State.BURNING);

// 6. 死亡处理
sprite.die();
```

### 自定义状态效果

```java
// 添加自定义状态（需要扩展State枚举）
public void addCustomEffect() {
    // 创建自定义粒子效果
    Emitter customEffect = emitter();
    customEffect.pour(CustomParticle.FACTORY, 0.1f);
    // 存储引用以便后续移除
    this.customEffect = customEffect;
}

public void removeCustomEffect() {
    if (customEffect != null) {
        customEffect.on = false;
        customEffect = null;
    }
}
```

### 动画回调使用

```java
// 带回调的攻击
sprite.attack(targetPos, new Callback() {
    @Override
    public void call() {
        // 攻击动画完成后执行的逻辑
        character.onAttackComplete();
    }
});
```

---

## 注意事项

### 线程安全

1. **状态变更同步**：`add()` 和 `remove()` 方法使用 `State.class` 同步
2. **表情图标同步**：`showSleep()` 等方法使用 `EmoIcon.class` 同步
3. **移动完成通知**：`onComplete()` 方法使用 `synchronized` 关键字

### 性能优化

1. **批量状态处理**：在 `update()` 中批量处理状态变更，避免每帧多次处理
2. **可见性检查**：只在 `visible=true` 时创建和更新特效
3. **资源复用**：使用对象池模式复用粒子发射器和特效对象

### 常见的坑

1. **忘记调用 link()**：精灵不会正确关联到角色
2. **直接修改 paused**：应该通过 `add/remove(State.PARALYSED)` 控制
3. **动画中断问题**：死亡动画不应该被其他动画中断（有特殊保护逻辑）
4. **同步问题**：移动完成时的 `notifyAll()` 用于同步等待线程

### 最佳实践

1. **使用状态管理**：优先使用 `add/remove` 而不是直接操作效果对象
2. **合理使用回调**：在需要精确时机控制时使用动画完成回调
3. **注意可见性**：在不可见状态下避免创建不必要的特效
4. **继承时重写 linkVisuals()**：用于处理角色外观变化