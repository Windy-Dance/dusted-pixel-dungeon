# ScrollOfPsionicBlast 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/exotic/ScrollOfPsionicBlast.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic |
| **文件类型** | class |
| **继承关系** | extends ExoticScroll |
| **代码行数** | 89 行 |
| **所属模块** | core |
| **官方中文名** | 灵爆秘卷 |

## 2. 文件职责说明

### 核心职责
灵爆秘卷是一种攻击型秘卷，阅读后对视野内所有敌人造成大量伤害，同时使用者也会受到反噬伤害、失明和虚弱效果。

### 系统定位
作为复仇卷轴的升级版本，对应普通卷轴为复仇卷轴（ScrollOfRetribution）。

### 不负责什么
- 不对视野外的敌人造成伤害
- 不造成物理伤害（纯粹的心灵伤害）

## 3. 结构总览

### 主要成员概览
- `icon`: 图标标识（ItemSpriteSheet.Icons.SCROLL_PSIBLAST）

### 主要逻辑块概览
- `doRead()`: 阅读逻辑，对视野内敌人造成伤害并反噬使用者

### 生命周期/调用时机
阅读后立即生效

## 4. 继承与协作关系

### 父类提供的能力
从 ExoticScroll 继承：
- 鉴定状态共享机制
- 价值计算
- 符文和图像设置

### 覆写的方法
| 方法 | 覆写目的 |
|------|----------|
| `doRead()` | 实现阅读逻辑：造成范围伤害并反噬 |

### 依赖的关键类
- `Blindness`: 失明Buff
- `Weakness`: 虚弱Buff
- `Mob`: 怪物类
- `Dungeon.level.heroFOV`: 英雄视野
- `ScrollOfRetribution`: 共享消息键
- `Badges`: 成就验证
- `GameScene`: 画面闪烁效果

## 5. 字段/常量详解

### 实例字段

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `icon` | int | ItemSpriteSheet.Icons.SCROLL_PSIBLAST | 物品图标标识 |

### 静态常量
无

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过初始化块设置图标。

### 初始化块
```java
{
    icon = ItemSpriteSheet.Icons.SCROLL_PSIBLAST;
}
```

## 7. 方法详解

### doRead()

**可见性**：public

**是否覆写**：是，覆写自 ExoticScroll

**方法职责**：实现阅读逻辑，对视野内所有敌人造成伤害并反噬使用者。

**前置条件**：
- 英雄必须可以阅读

**副作用**：
- 从背包移除物品
- 鉴定物品
- 播放音效和画面效果
- 对视野内所有敌人造成伤害
- 使用者受到伤害、失明和虚弱
- 可能导致英雄死亡

**核心实现逻辑**：
```java
@Override
public void doRead() {
    detach(curUser.belongings.backpack);
    GameScene.flash( 0x80FFFFFF ); // 画面闪烁
    
    Sample.INSTANCE.play( Assets.Sounds.BLAST );
    GLog.i(Messages.get(ScrollOfRetribution.class, "blast"));

    ArrayList<Mob> targets = new ArrayList<>();

    // 1. 计算视野内的所有敌人
    for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
        if (Dungeon.level.heroFOV[mob.pos]) {
            targets.add(mob);
        }
    }

    // 2. 对每个敌人造成伤害
    for (Mob mob : targets){
        // 伤害 = (最大生命值 + 当前生命值) / 2
        mob.damage(Math.round(mob.HT/2f + mob.HP/2f), this);
        if (mob.isAlive()) {
            Buff.prolong(mob, Blindness.class, Blindness.DURATION);
        }
    }
    
    // 3. 使用者受到反噬
    // 反噬伤害 = 英雄最大生命值 * 0.5 * 0.9^目标数量
    curUser.damage(Math.max(0, Math.round(curUser.HT*(0.5f * (float)Math.pow(0.9, targets.size())))), this);
    
    if (curUser.isAlive()) {
        Buff.prolong(curUser, Blindness.class, Blindness.DURATION);
        Buff.prolong(curUser, Weakness.class, Weakness.DURATION*5f);
        Dungeon.observe();
        readAnimation();
    } else {
        // 死亡处理
        Badges.validateDeathFromFriendlyMagic();
        Dungeon.fail( this );
        GLog.n( Messages.get(this, "ondeath") );
    }

    identify();
}
```

**边界情况**：
- 如果英雄死亡，记录死亡原因为"友方魔法"
- 目标越多，反噬伤害越低

## 8. 对外暴露能力

### 显式 API
- `doRead()`: 阅读逻辑

### 内部辅助方法
无

### 扩展入口
- 可覆写 `doRead()` 自定义伤害或反噬逻辑

## 9. 运行机制与调用链

### 创建时机
- 通过炼金转换（复仇卷轴 + 6能量）
- 通过 Generator 随机生成

### 调用者
- 英雄使用物品时调用

### 被调用者
- `GameScene.flash()`: 画面闪烁效果
- `Dungeon.level.heroFOV`: 获取视野数组
- `mob.damage()`: 对敌人造成伤害
- `Buff.prolong()`: 添加/延长Buff
- `curUser.damage()`: 对使用者造成伤害

### 系统流程位置
```
阅读 → doRead() → 收集视野内敌人 → 对每个敌人造成伤害 → 
使用者受到反噬 → 添加失明和虚弱 → 鉴定物品
```

### 伤害计算公式

**对敌人伤害**：
```java
damage = Math.round(mob.HT/2f + mob.HP/2f);
// 对于满血敌人：(HT/2 + HT/2) = HT，即100%最大生命值
// 对于半血敌人：(HT/2 + HT/4) = 3*HT/4，即75%最大生命值
// 对于1/3血敌人：(HT/2 + HT/6) = 2*HT/3，约67%最大生命值
```

**反噬伤害**：
```java
damage = Math.max(0, Math.round(curUser.HT * 0.5f * Math.pow(0.9, targets.size())));
// 0个目标：50% 最大生命值
// 1个目标：45% 最大生命值
// 5个目标：约 30% 最大生命值
// 10个目标：约 19% 最大生命值
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.scrolls.exotic.scrollofpsionicblast.name | 灵爆秘卷 | 物品名称 |
| items.scrolls.exotic.scrollofpsionicblast.ondeath | 灵能震爆撕碎了你的意识... | 死亡消息 |
| items.scrolls.exotic.scrollofpsionicblast.desc | 这张秘卷封存着惊人的毁灭性能量，一旦被释放出来可摧毁视野内所有生物的心智。\n\n然而，使用者也会遭受灵爆的严重反噬，使其身受重创，双目失明，力量虚弱。灵爆秘卷击中的目标越多，其对使用者造成的伤害越低。 | 物品描述 |
| items.scrolls.scrollofretribution.blast | 强大的魔力从卷轴中爆发！ | 效果消息 |

### 依赖的资源
- ItemSpriteSheet.Icons.SCROLL_PSIBLAST: 物品图标
- Assets.Sounds.BLAST: 爆炸音效

### 中文翻译来源
- core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法

```java
// 阅读灵爆秘卷
ScrollOfPsionicBlast scroll = new ScrollOfPsionicBlast();
scroll.doRead();

// 效果：
// 1. 画面闪烁
// 2. 播放爆炸音效
// 3. 对视野内所有敌人造成 (HT/2 + HP/2) 伤害
// 4. 使用者受到反噬伤害
// 5. 使用者获得失明和虚弱效果
```

### 伤害计算示例

```java
// 假设英雄最大生命值为 100，视野内有 3 个敌人：
// 反噬伤害 = 100 * 0.5 * 0.9^3 ≈ 36

// 对一个最大生命值 50、当前生命值 30 的敌人：
// 伤害 = (50/2 + 30/2) = 40
```

## 12. 开发注意事项

### 状态依赖
- 依赖 `Dungeon.level.heroFOV` 判断敌人在视野内
- 依赖 `Blindness.DURATION` 和 `Weakness.DURATION` 设置Buff持续时间

### 生命周期耦合
- 可能导致英雄死亡，需要正确处理死亡流程

### 常见陷阱
1. **反噬致死**：使用前必须确保有足够生命值
2. **目标数量**：目标越少反噬越重，目标越多反噬越轻
3. **死亡原因**：死亡时记录为"友方魔法"（validateDeathFromFriendlyMagic）

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可修改反噬伤害公式
- 可添加额外效果（如眩晕）

### 不建议修改的位置
- 死亡处理逻辑

### 重构建议
无重大重构建议

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是（仅 icon）
- [x] 是否已覆盖全部方法：是（仅 doRead）
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：否
- [x] 是否明确说明了注意事项与扩展点：是