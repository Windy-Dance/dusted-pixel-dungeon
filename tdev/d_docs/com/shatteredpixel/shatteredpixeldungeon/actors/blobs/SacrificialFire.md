# SacrificialFire 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/blobs/SacrificialFire.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.actors.blobs |
| **类类型** | public class |
| **继承关系** | extends Blob |
| **代码行数** | 232 行 |
| **直接子类** | 无 |
| **内部类** | Marked |

## 2. 文件职责说明

SacrificialFire 类代表游戏中的"献祭之火"区域效果。它出现在献祭房间中，玩家可以通过在火中击杀被标记的敌人来获得奖励。

**核心职责**：
- 标记献祭之火周围的敌人
- 处理献祭逻辑（击杀被标记敌人）
- 管理奖励物品
- 生成额外怪物（减少等待时间）

**设计意图**：献祭之火是一种风险与回报机制。玩家需要在火中击杀敌人来"献祭"，累积足够的献祭值后获得奖励。

## 3. 结构总览

```
SacrificialFire (extends Blob)
├── 实例初始化块
│   └── actPriority = MOB_PRIO - 1
│
├── 字段
│   ├── curEmitter: BlobEmitter     // 粒子发射器引用
│   ├── bonusSpawns: int            // 额外怪物生成次数
│   └── prize: Item                 // 奖励物品
│
├── 常量（Bundle键名）
│   ├── BONUS_SPAWNS = "bonus_spawns"
│   └── PRIZE = "prize"
│
├── 方法
│   ├── landmark(): Landmark                 // 返回地图标记
│   ├── evolve(): void                       // 扩散并标记敌人
│   ├── use(BlobEmitter): void               // 设置视觉效果
│   ├── tileDesc(): String                   // 返回描述文本
│   ├── setPrize(Item): void                 // 设置奖励物品
│   ├── sacrifice(Char): void                // 处理献祭逻辑
│   ├── storeInBundle(Bundle): void          // 保存状态
│   └── restoreFromBundle(Bundle): void      // 恢复状态
│
└── 内部类
    └── Marked extends FlavourBuff           // 被标记状态
```

## 4. 继承与协作关系

### 继承关系图

```mermaid
classDiagram
    class Blob {
        +volume: int
        +cur: int[]
        +evolve(): void
    }
    
    class SacrificialFire {
        -curEmitter: BlobEmitter
        -bonusSpawns: int
        -prize: Item
        +evolve(): void
        +sacrifice(Char): void
    }
    
    class Marked {
        +DURATION: float
        +detach(): void
    }
    
    Blob <|-- SacrificialFire
    SacrificialFire +-- Marked
```

### 协作关系

| 协作类 | 协作方式 |
|--------|----------|
| **Blob** | 父类，提供基础框架 |
| **Marked** | 内部类，标记敌人状态 |
| **Char** | 被标记和献祭的角色 |
| **Mob** | 被献祭的怪物类型 |
| **Hero** | 玩家角色（可被献祭） |
| **SacrificeRoom** | 提供奖励生成逻辑 |
| **Notes.Landmark** | 地图标记系统 |
| **SacrificialParticle** | 献祭粒子效果 |
| **Badges** | 成就系统 |
| **Messages** | 国际化消息获取 |
| **GLog** | 日志系统 |

## 5. 字段与常量详解

### 实例字段

| 字段名 | 类型 | 访问级别 | 说明 |
|--------|------|----------|------|
| `curEmitter` | BlobEmitter | 包级私有 | 粒子发射器引用，用于动态调整粒子密度 |
| `bonusSpawns` | int | private | 额外怪物生成次数，初始值 3 |
| `prize` | Item | private | 奖励物品，可为 null |

### 行动优先级设置

```java
{
    //acts after mobs, so they can get marked as they move
    actPriority = MOB_PRIO - 1;
}
```

献祭之火在怪物之后行动，确保怪物移动后能被标记。

### 献祭值计算

| 怪物类型 | 经验值 |
|----------|--------|
| Statue, Mimic | 1 + Dungeon.depth |
| Piranha, Bee | 1 + Dungeon.depth/2 |
| Wraith | 1 + Dungeon.depth/3 |
| Swarm（子体） | 1 |
| 其他（EXP > 0） | 1 + EXP |
| Hero | 1,000,000 |

### 奖励触发条件

```java
int max = 6 + Dungeon.depth * 4;
```

献祭值需要达到 `6 + 楼层 * 4` 才能触发奖励。

## 6. 构造与初始化机制

SacrificialFire 类没有显式构造函数，使用默认构造函数和实例初始化块。

### 典型初始化方式

```java
// 通过静态 seed 方法创建
Blob.seed(firePos, initialVolume, SacrificialFire.class);
```

### 奖励设置

```java
SacrificialFire fire = Dungeon.level.blobs.get(SacrificialFire.class);
fire.setPrize(customPrize);
```

## 7. 方法详解

### evolve() - 扩散与标记

```java
@Override
protected void evolve()
```

**职责**：实现献祭之火的扩散、标记敌人和生成额外怪物。

**执行逻辑**：

1. **扩散（不衰减）**：
   ```java
   off[cell] = cur[cell];
   volume += off[cell];
   ```

2. **标记周围敌人**：
   ```java
   for (int k : PathFinder.NEIGHBOURS9) {
       Char ch = Actor.findChar(cell + k);
       if (ch != null) {
           if (Dungeon.level.heroFOV[cell+k] && ch.buff(Marked.class) == null) {
               CellEmitter.get(cell+k).burst(SacrificialParticle.FACTORY, 5);
           }
           Buff.prolong(ch, Marked.class, Marked.DURATION);
       }
   }
   ```

3. **生成额外怪物**：
   ```java
   if (Dungeon.level.mobCount() == 0 && bonusSpawns > 0) {
       if (Dungeon.level.spawnMob(4)) {
           bonusSpawns--;
       }
   }
   ```

4. **更新粒子密度**：
   ```java
   int max = 6 + Dungeon.depth * 4;
   curEmitter.pour(SacrificialParticle.FACTORY, 0.01f + ((volume / (float)max) * 0.09f));
   ```

### sacrifice() - 献祭处理

```java
public void sacrifice(Char ch)
```

**职责**：处理被标记角色死亡时的献祭逻辑。

**参数**：
- `ch`: 死亡的角色

**执行逻辑**：

1. **查找献祭之火位置**：
   ```java
   int firePos = -1;
   for (int i : PathFinder.NEIGHBOURS9) {
       if (volume > 0 && cur[ch.pos + i] > 0) {
           firePos = ch.pos + i;
           break;
       }
   }
   ```

2. **计算经验值**：
   ```java
   int exp = 0;
   if (ch instanceof Mob) {
       // 根据怪物类型计算
       exp *= Random.IntRange(2, 3);
   } else if (ch instanceof Hero) {
       exp = 1_000_000;
       Badges.validateDeathFromSacrifice();
   }
   ```

3. **处理献祭结果**：
   - 若献祭值未满：
     ```java
     cur[firePos] -= exp;
     volume -= exp;
     bonusSpawns++;
     GLog.w(Messages.get(SacrificialFire.class, "worthy"));
     ```
   - 若献祭值已满：
     ```java
     clear(firePos);
     // 掉落奖励
     Dungeon.level.drop(prize, firePos).sprite.drop();
     GLog.w(Messages.get(SacrificialFire.class, "reward"));
     ```

### Marked 内部类

```java
public static class Marked extends FlavourBuff
```

**职责**：标记被献祭之火影响的角色。

**常量**：
- `DURATION = 2f`：标记持续时间

**关键方法**：
```java
@Override
public void detach() {
    if (!target.isAlive()) {
        SacrificialFire fire = (SacrificialFire) Dungeon.level.blobs.get(SacrificialFire.class);
        if (fire != null) {
            fire.sacrifice(target);
        }
    }
    super.detach();
}
```

当被标记角色死亡时，触发献祭。

## 8. 对外暴露能力

### 公共 API

| 方法 | 用途 | 调用者 |
|------|------|--------|
| `setPrize(Item)` | 设置奖励物品 | 房间生成 |
| `sacrifice(Char)` | 处理献祭 | Marked.detach() |
| `landmark()` | 返回地图标记 | 地图标记系统 |

## 9. 运行机制与调用链

### 标记流程

```
怪物移动到献祭之火附近
    └── SacrificialFire.evolve()
        └── 遍历 NEIGHBOURS9
            └── Buff.prolong(ch, Marked.class, DURATION)
                └── 怪物被标记
```

### 献祭流程

```
被标记怪物死亡
    └── Marked.detach()
        └── SacrificialFire.sacrifice(target)
            ├── 计算经验值
            ├── 更新献祭值
            └── [献祭值满] 掉落奖励
```

### 奖励触发

```
献祭值 >= (6 + depth * 4)
    └── 清除献祭之火
    └── 掉落奖励物品
    └── 移除地图标记
```

## 10. 资源、配置与国际化关联

### 国际化资源

**资源文件位置**：
- `core/src/main/assets/messages/actors/actors_zh.properties`

**相关翻译键**：
```properties
actors.blobs.sacrificialfire.name=献祭之火
actors.blobs.sacrificialfire.desc=这是一个承载着献祭之火的祭坛。在此殒命的生物都将成为献给地牢幽魂的祭品。\n\n或许献祭够多，就能得到回报？
actors.blobs.sacrificialfire.worthy=火焰吞噬了你的祭品，燃烧得愈加旺盛。
actors.blobs.sacrificialfire.unworthy=火焰吞噬了你的祭品，然而没有任何变化。
actors.blobs.sacrificialfire.reward=火焰骤然升腾，继而消散，并留下一份奖励！
```

### 视觉资源

| 资源 | 说明 |
|------|------|
| **SacrificialParticle** | 献祭粒子效果 |
| **BlobEmitter** | 粒子发射器 |

### 音效资源

| 资源 | 说明 |
|------|------|
| **Assets.Sounds.BURNING** | 燃烧音效 |

## 11. 使用示例

### 创建献祭之火

```java
// 在献祭房间创建献祭之火
int initialVolume = 6 + Dungeon.depth * 4;
Blob.seed(firePos, initialVolume, SacrificialFire.class);
```

### 设置自定义奖励

```java
SacrificialFire fire = Dungeon.level.blobs.get(SacrificialFire.class);
fire.setPrize(new CustomItem());
```

### 检查献祭之火

```java
SacrificialFire fire = Dungeon.level.blobs.get(SacrificialFire.class);
if (fire != null && fire.volume > 0) {
    // 存在献祭之火
}
```

## 12. 开发注意事项

### 额外怪物生成

- 最多生成 3 只额外怪物
- 仅在楼层没有怪物时生成
- 这减少了玩家等待怪物的时间

### 英雄献祭

- 英雄被献祭时经验值为 1,000,000
- 这确保英雄献祭总是能触发奖励
- 同时触发成就验证

### 粒子密度动态调整

- 粒子密度随献祭值增加
- 这提供了视觉反馈
- 公式：`0.01f + (volume / max) * 0.09f`

### Marked Buff 的作用

- 标记持续 2 回合
- 死亡时触发献祭
- 这确保只有被标记的敌人才算献祭

## 13. 修改建议与扩展点

### 扩展点

1. **自定义献祭值计算**：修改 sacrifice() 中的经验值公式
2. **添加新的怪物类型**：在经验值计算中添加新类型

### 修改建议

1. **奖励配置化**：将奖励生成逻辑提取为可配置方法
2. **粒子效果优化**：考虑使用更高效的粒子系统

## 14. 事实核查清单

- [x] 是否已覆盖全部 public/protected 方法
- [x] 是否已覆盖全部字段（curEmitter, bonusSpawns, prize）
- [x] 是否已验证继承关系（extends Blob）
- [x] 是否已验证内部类 Marked
- [x] 是否已验证行动优先级设置（MOB_PRIO - 1）
- [x] 是否已验证献祭值计算逻辑
- [x] 是否已验证奖励触发条件
- [x] 是否已验证额外怪物生成逻辑
- [x] 是否已验证视觉效果设置
- [x] 所有中文术语是否来自官方翻译文件
- [x] 是否存在臆测性内容（无）
