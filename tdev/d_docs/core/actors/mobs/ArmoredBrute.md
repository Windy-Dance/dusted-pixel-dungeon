# ArmoredBrute 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/ArmoredBrute.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.actors.mobs |
| 类类型 | public class |
| 继承关系 | extends Brute |
| 代码行数 | 94 行 |

## 2. 类职责说明
ArmoredBrute（装甲暴徒）是 Brute（暴徒）的精英变种，拥有额外的护甲和特殊的护盾狂暴机制。死亡时会获得护盾并在 60 回合后逐渐消亡。掉落高级护甲作为战利品。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Mob {
        +spriteClass Class
        +HP int
        +HT int
        +drRoll() int
        +createLoot() Item
    }
    
    class Brute {
        +hasRaged boolean
        +triggerEnrage() void
        +BruteRage inner class
    }
    
    class ArmoredBrute {
        +spriteClass ShieldedSprite
        +loot Generator.Category.ARMOR
        +lootChance 1f
        +drRoll() int
        +triggerEnrage() void
        +createLoot() Item
        +ArmoredRage inner class
    }
    
    Mob <|-- Brute
    Brute <|-- ArmoredBrute
    Brute +-- BruteRage
    ArmoredBrute +-- ArmoredRage
    BruteRage <|-- ArmoredRage
```

## 静态常量表
无静态常量。

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| spriteClass | Class | 初始化块 | 精灵类为 ShieldedSprite |
| loot | Generator.Category | 初始化块 | 掉落物类别为护甲 |
| lootChance | float | 初始化块 | 100% 掉落概率 |

## 7. 方法详解

### drRoll
**签名**: `public int drRoll()`
**功能**: 计算额外的伤害减免值
**返回值**: int - 额外的伤害减免值（4点）
**实现逻辑**:
```java
// 第48-50行：额外提供4点伤害减免
return super.drRoll() + 4;  // 父类伤害减免 + 额外4点，总计4-12点DR
```

### triggerEnrage
**签名**: `protected void triggerEnrage()`
**功能**: 触发狂暴状态，获得护盾
**实现逻辑**:
```java
// 第53-61行：死亡时获得护盾狂暴
Buff.affect(this, ArmoredRage.class).setShield(HT/2 + 1);  // 施加护盾，值为最大生命值的一半+1
sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(HT/2), FloatingText.SHIELDING); // 显示护盾图标
if (Dungeon.level.heroFOV[pos]) {                          // 如果玩家可见
    sprite.showStatus(CharSprite.WARNING, Messages.get(this, "enraged")); // 显示狂暴消息
}
spend(TICK);                                                // 消耗1回合
hasRaged = true;                                            // 标记已狂暴
```

### createLoot
**签名**: `public Item createLoot()`
**功能**: 创建掉落物（护甲）
**返回值**: Item - 随机护甲（板甲或鳞甲）
**实现逻辑**:
```java
// 第64-69行：随机生成护甲
if (Random.Int(4) == 0) {              // 25%概率掉落板甲
    return new PlateArmor().random();  // 返回随机板甲
}
return new ScaleArmor().random();      // 75%概率掉落鳞甲
```

## 内部类详解

### ArmoredRage
**类型**: public static class extends Brute.BruteRage
**功能**: 装甲狂暴状态，护盾会缓慢衰减
**实现逻辑**:
```java
// 第72-93行：护盾狂暴行为
@Override
public boolean act() {
    if (target.HP > 0) {                // 如果目标生命值恢复
        detach();                        // 移除狂暴状态
        return true;
    }
    
    absorbDamage(Math.round(AscensionChallenge.statModifier(target))); // 每回合吸收伤害（受飞升挑战影响）
    
    if (shielding() <= 0) {             // 如果护盾耗尽
        target.die(null);               // 目标死亡
    }
    
    spend(3 * TICK);                    // 每3回合触发一次（比普通暴徒慢3倍）
    return true;
}
```

## 11. 使用示例
```java
// 在关卡生成时创建装甲暴徒
ArmoredBrute armored = new ArmoredBrute();
armored.pos = position;
Dungeon.level.mobs.add(armored);

// 装甲暴徒拥有额外护甲
// 死亡时获得护盾狂暴
// 击杀后掉落高级护甲
```

## 注意事项
1. 继承自 Brute，具有暴徒的基础属性和狂暴机制
2. 额外 4 点伤害减免使其更难被击杀
3. 狂暴后护盾持续 60 回合（比普通暴徒长 3 倍）
4. 掉落板甲（25%）或鳞甲（75%）

## 最佳实践
1. 高伤害武器可以更快击杀
2. 狂暴后护盾会缓慢衰减，可以等待
3. 不要在狂暴期间近战，护盾会吸收伤害
4. 板甲和鳞甲都是高级护甲，值得收集