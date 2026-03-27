# Pasty 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/food/Pasty.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.food |
| 类类型 | public class |
| 继承关系 | extends Food |
| 代码行数 | 243行 |

## 2. 类职责说明
馅饼是一种特殊的食物，根据当前节日显示不同的外观和效果。游戏中有多个节日，每个节日的馅饼都有独特的额外效果。馅饼的能量值很高（STARVING），是游戏中最好的常规食物之一。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Food {
        <<abstract>>
        +float energy
        +void satisfy(Hero)
        +void eatSFX()
    }
    
    class Pasty {
        +void reset()
        +void satisfy(Hero)
        +void eatSFX()
        +String name()
        +String desc()
        +int value()
    }
    
    class FishLeftover {
        +int value()
    }
    
    Food <|-- Pasty
    Food <|-- FishLeftover
    Pasty +-- FishLeftover
```

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标（根据节日变化） |
| energy | float | - | 能量值（STARVING，农历新年为HUNGRY） |
| bones | boolean | - | 是否可出现在遗骨中（true） |

## 7. 方法详解

### reset()
**签名**: `void reset()`
**功能**: 重置物品状态（更新节日图标）
**参数**: 无
**返回值**: void
**实现逻辑**:
- 根据当前节日设置对应的图标（第58-89行）

### eatSFX()
**签名**: `void eatSFX()`
**功能**: 播放进食音效
**参数**: 无
**返回值**: void
**实现逻辑**:
1. 如果是骄傲节或新年，播放饮水音效（第95-98行）
2. 否则播放普通进食音效（第100行）

### satisfy(Hero hero)
**签名**: `void satisfy(Hero hero)`
**功能**: 满足饥饿需求并触发节日效果
**参数**:
- hero: Hero - 英雄
**返回值**: void
**实现逻辑**:
1. 如果是农历新年，能量值设为HUNGRY（第105-108行）
2. 调用父类satisfy方法（第110行）
3. 根据节日触发额外效果（第112-171行）

### name() / desc()
**功能**: 根据节日返回不同的名称和描述

### value()
**签名**: `int value()`
**功能**: 获取物品价值
**参数**: 无
**返回值**: int - 价值（20 * 数量）

## 节日效果表

| 节日 | 图标 | 名称 | 效果 |
|------|------|------|------|
| 无节日 | PASTY | 馅饼 | 无额外效果 |
| 农历新年 | STEAMED_FISH | 蒸鱼 | 额外获得鱼残渣（75能量） |
| 愚人节 | CHOC_AMULET | 巧克力护符 | 播放宝箱怪音效+充能神器 |
| 复活节 | EASTER_EGG | 复活节彩蛋 | 充能神器+充能物品 |
| 骄傲节 | RAINBOW_POTION | 彩虹药水 | 魅惑相邻敌人 |
| SPD生日 | SHATTERED_CAKE | 破碎蛋糕 | 获得经验值（最大经验的10%） |
| 万圣节 | PUMPKIN_PIE | 南瓜派 | 治疗（最大生命的5%） |
| PD生日 | VANILLA_CAKE | 香草蛋糕 | 获得经验值（最大经验的10%） |
| 冬季节日 | CANDY_CANE | 糖果手杖 | 充能装备（0.5f） |
| 新年 | SPARKLING_POTION | 气泡药水 | 获得护盾（最大生命的10%） |

## 内部类 FishLeftover

鱼残渣，农历新年时额外获得的食物。

### 实例字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| image | int | FISH_LEFTOVER |
| energy | float | HUNGRY/2（约75点） |

## 11. 使用示例
```java
// 创建馅饼
Pasty pasty = new Pasty();

// 图标和效果根据当前节日自动设置
pasty.reset();

// 食用馅饼
pasty.execute(hero, Food.AC_EAT);
// 恢复大量饥饿值（STARVING）
// 根据节日获得额外效果

// 检查当前节日
Holiday holiday = Holiday.getCurrentHoliday();
switch (holiday) {
    case LUNAR_NEW_YEAR:
        // 农历新年效果
        break;
    case HALLOWEEN:
        // 万圣节效果
        break;
    // ...
}
```

## 注意事项
1. 节日效果会自动激活
2. 农历新年的能量值较低但额外获得鱼残渣
3. 某些节日效果很有价值（如经验值、护盾）
4. 节日期间出现的馅饼有特殊外观
5. 节日效果每局游戏只生效一次

## 最佳实践
1. 在节日时食用可获得额外效果
2. 农历新年可获得两份食物
3. 生日蛋糕可快速获得经验
4. 新年气泡药水可提供护盾
5. 骄傲节可魅惑敌人获得优势