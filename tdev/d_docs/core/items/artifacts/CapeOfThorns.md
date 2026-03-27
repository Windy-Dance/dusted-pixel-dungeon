# CapeOfThorns 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/CapeOfThorns.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.artifacts |
| 类类型 | public class |
| 继承关系 | extends Artifact |
| 代码行数 | 148行 |

## 2. 类职责说明
荆棘披风是一个防御型神器，通过吸收受到的伤害来积累充能，充能满后激活反弹状态，在激活期间会将受到的伤害反弹给攻击者。披风的等级影响充能积累效率和反弹持续时间。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Artifact {
        <<abstract>>
        +int charge
        +int chargeCap
        +int cooldown
        +int levelCap
        +ArtifactBuff passiveBuff()
    }
    
    class CapeOfThorns {
        +ArtifactBuff passiveBuff()
        +void charge(Hero, float)
        +String desc()
    }
    
    class Thorns {
        +boolean act()
        +int proc(int, Char, Char)
        +String desc()
        +int icon()
        +void detach()
    }
    
    class ArtifactBuff {
        <<abstract>>
    }
    
    Artifact <|-- CapeOfThorns
    ArtifactBuff <|-- Thorns
    CapeOfThorns +-- Thorns
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无 | - | - | 此类没有定义静态常量 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标，使用ItemSpriteSheet.ARTIFACT_CAPE |
| levelCap | int | - | 等级上限为10级 |
| charge | int | - | 当前充能值，初始为0 |
| chargeCap | int | - | 充能上限为100 |
| cooldown | int | - | 冷却时间，初始为0 |
| defaultAction | String | - | 默认动作为"NONE"，支持快捷栏 |

## 7. 方法详解

### passiveBuff()
**签名**: `protected ArtifactBuff passiveBuff()`
**功能**: 获取被动Buff实例
**参数**: 无
**返回值**: ArtifactBuff - Thorns实例
**实现逻辑**:
- 创建并返回新的Thorns内部类实例（第50行）

### charge(Hero target, float amount)
**签名**: `void charge(Hero target, float amount)`
**功能**: 为披风充能
**参数**:
- target: Hero - 接受充能的目标英雄
- amount: float - 充能数量
**返回值**: void
**实现逻辑**:
1. 检查冷却是否为0（第55行）
2. 增加充能值，充能量为4*amount（第56行）
3. 更新快捷栏（第57行）
4. 如果充能达到上限，触发激活效果（第59-61行）

### desc()
**签名**: `String desc()`
**功能**: 获取物品描述文本
**参数**: 无
**返回值**: String - 描述文本
**实现逻辑**:
1. 获取基础描述文本（第66行）
2. 如果已装备（第67-73行）：
   - 冷却为0时显示未激活描述
   - 冷却>0时显示激活中描述

## 内部类 Thorns

### act()
**签名**: `boolean act()`
**功能**: 每帧执行的Buff行为
**参数**: 无
**返回值**: boolean - 是否继续执行
**实现逻辑**:
1. 如果冷却>0（第82行）：
   - 减少冷却值（第83行）
   - 冷却归零时提示披风已失效（第84-86行）
   - 更新快捷栏（第87行）
2. 花费1tick时间（第89行）
3. 返回true继续执行（第90行）

### proc(int damage, Char attacker, Char defender)
**签名**: `int proc(int damage, Char attacker, Char defender)`
**功能**: 处理伤害反弹逻辑
**参数**:
- damage: int - 原始伤害值
- attacker: Char - 攻击者
- defender: Char - 防御者
**返回值**: int - 处理后的伤害值
**实现逻辑**:
1. 如果冷却为0（未激活状态）（第94-100行）：
   - 积累充能：damage * (0.5 + 等级*0.05)（第95行）
   - 充能达到100时激活（第96行）：
     - 重置充能为0（第97行）
     - 设置冷却时间：10 + 等级（第98行）
     - 显示激活提示（第99行）
2. 如果冷却>0（激活状态）（第103-119行）：
   - 随机计算反弹伤害（第104行）
   - 减少受到的伤害（第105行）
   - 如果攻击者在相邻位置，反弹伤害给攻击者（第107-109行）
   - 增加经验值（第111行）
   - 经验足够时升级（第113-118行）
3. 更新快捷栏（第121行）
4. 返回处理后的伤害（第122行）

### desc()
**签名**: `String desc()`
**功能**: 获取Buff描述
**参数**: 无
**返回值**: String - 描述文本
**实现逻辑**:
- 返回带冷却时间显示的描述文本（第127行）

### icon()
**签名**: `int icon()`
**功能**: 获取Buff图标
**参数**: 无
**返回值**: int - 图标ID
**实现逻辑**:
- 冷却为0时返回NONE图标（第133行）
- 冷却>0时返回THORNS图标（第135行）

### detach()
**签名**: `void detach()`
**功能**: 移除Buff时的清理
**参数**: 无
**返回值**: void
**实现逻辑**:
1. 重置冷却为0（第140行）
2. 重置充能为0（第141行）
3. 调用父类detach方法（第142行）

## 11. 使用示例
```java
// 创建荆棘披风
CapeOfThorns cape = new CapeOfThorns();

// 装备披风
cape.doEquip(hero);

// 披风会自动积累充能
// 当受到伤害时：
// 1. 未激活状态：积累充能
// 2. 激活状态：反弹伤害给攻击者

// 通过充能方法加速充能
cape.charge(hero, 5.0f);
```

## 注意事项
1. 披风需要受到伤害才能积累充能
2. 充能满后自动激活，激活期间会反弹伤害
3. 反弹只对相邻的攻击者有效
4. 升级需要通过反弹伤害积累经验
5. 冷却结束后需要重新积累充能

## 最佳实践
1. 在面对大量近战敌人时使用效果最佳
2. 升级披风可以提高充能效率和反弹持续时间
3. 配合高防御装备可以在激活期间承受更多伤害
4. 注意反弹不能对远程攻击者生效