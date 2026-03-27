# Weakness 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Weakness.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| 类类型 | class |
| 继承关系 | extends FlavourBuff |
| 代码行数 | 44 |

## 2. 类职责说明
Weakness（虚弱）是一个负面Buff，使受影响的角色攻击伤害降低。虚弱状态下角色的战斗能力下降，更难击败敌人。主要用于诅咒武器、特定敌人攻击等场景。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Buff {
        <<abstract>>
        +type: buffType
        +announced: boolean
    }
    class FlavourBuff {
        +DURATION: float
    }
    class Weakness {
        +DURATION: float = 20f
        +icon(): int
        +iconFadePercent(): float
    }
    Buff <|-- FlavourBuff
    FlavourBuff <|-- Weakness
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| DURATION | float | 20f | 默认持续时间（回合数） |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| type | buffType | - | NEGATIVE（负面Buff） |
| announced | boolean | - | true（会公告） |

## 7. 方法详解

### icon()
**签名**: `public int icon()`
**功能**: 返回Buff图标的索引标识符。
**返回值**: int - 返回BuffIndicator.WEAKNESS（虚弱图标）。

### iconFadePercent()
**签名**: `public float iconFadePercent()`
**功能**: 计算Buff图标的淡出百分比。
**返回值**: float - 图标完整度比例。

## 11. 使用示例
```java
// 对敌人施加虚弱效果，持续20回合
Buff.affect(enemy, Weakness.class, Weakness.DURATION);

// 检查是否有虚弱
if (hero.buff(Weakness.class) != null) {
    // 英雄攻击伤害降低
}

// 延长虚弱时间
Buff.prolong(hero, Weakness.class, 10f);
```

## 注意事项
1. 虚弱效果降低攻击伤害
2. 实际的伤害计算在Char类中检查此Buff实现
3. 是负面Buff，会被净化效果移除
4. 持续时间较长（20回合）
5. 会显示公告消息

## 最佳实践
1. 对强敌使用可以降低其威胁
2. 在危险时避免被施加虚弱
3. 使用净化道具尽快移除
4. 配合其他减益效果叠加使用