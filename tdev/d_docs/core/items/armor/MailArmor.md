# MailArmor 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/MailArmor.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.armor |
| 类类型 | public class |
| 继承关系 | extends Armor |
| 代码行数 | 36 行 |

## 2. 类职责说明
MailArmor（锁甲）是层级3的护甲类型。提供良好的伤害减免，是中期游戏的主要护甲选择。需要较高的力量才能装备。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Armor {
        +tier int
        +image int
        +DRMin() int
        +DRMax() int
    }
    
    class MailArmor {
        +image ARMOR_MAIL
        +tier 3
    }
    
    Armor <|-- MailArmor
```

## 静态常量表
无静态常量。

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | 初始化块 | 精灵图为 ARMOR_MAIL |

## 7. 方法详解

### 构造函数
**签名**: `public MailArmor()`
**功能**: 创建层级3的锁甲
**实现逻辑**:
```java
super(3);  // 调用父类构造函数，设置tier=3
```

## 护甲属性

| 属性 | 值 |
|------|-----|
| 层级 (tier) | 3 |
| 最小伤害减免 | 0 |
| 最大伤害减免 | 6 |
| 力量需求 | 14 |

## 11. 使用示例
```java
// 创建锁甲
MailArmor mail = new MailArmor();

// 层级3护甲，提供良好保护
// 适合中期游戏使用
```

## 注意事项
1. 层级3护甲
2. 力量需求14
3. 伤害减免0-6
4. 中期游戏的主要护甲

## 最佳实践
1. 中期获取后尽快装备
2. 需要足够的力量
3. 符文增强效果明显
4. 可升级增加保护