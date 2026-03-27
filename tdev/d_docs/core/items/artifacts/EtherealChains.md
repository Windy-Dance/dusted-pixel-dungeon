# EtherealChains 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/EtherealChains.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.artifacts |
| 类类型 | public class |
| 继承关系 | extends Artifact |
| 代码行数 | 364行 |

## 2. 类职责说明
虚灵锁链是一个功能型神器，可以通过投射魔法锁链来拉扯敌人或移动自己。对敌人使用时，将敌人拉向自己；对空地使用时，将自己拉向目标位置。锁链消耗充能，距离越远消耗越多。使用锁链可以获得经验并升级。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Artifact {
        <<abstract>>
        +int charge
        +int levelCap
        +int exp
        +ArtifactBuff passiveBuff()
    }
    
    class EtherealChains {
        +String AC_CAST
        +CellSelector.Listener caster
        +ArrayList~String~ actions(Hero)
        +int targetingPos(Hero, int)
        +void execute(Hero, String)
        +void resetForTrinity(int)
        +void charge(Hero, float)
        +String desc()
        -void chainEnemy(Ballistica, Hero, Char)
        -void chainLocation(Ballistica, Hero)
    }
    
    class chainsRecharge {
        +boolean act()
        +void gainExp(float)
    }
    
    class ArtifactBuff {
        <<abstract>>
    }
    
    Artifact <|-- EtherealChains
    ArtifactBuff <|-- chainsRecharge
    EtherealChains +-- chainsRecharge
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_CAST | String | "CAST" | 投射动作标识 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标，使用ItemSpriteSheet.ARTIFACT_CHAINS |
| levelCap | int | - | 等级上限为5级 |
| exp | int | - | 经验值，初始为0 |
| charge | int | - | 当前充能，初始为5 |
| defaultAction | String | - | 默认动作为AC_CAST |
| usesTargeting | boolean | - | 是否使用目标选择，初始为true |
| caster | CellSelector.Listener | public | 单元格选择监听器 |

## 7. 方法详解

### actions(Hero hero)
**签名**: `ArrayList<String> actions(Hero hero)`
**功能**: 获取该物品可用的动作列表
**参数**:
- hero: Hero - 英雄对象
**返回值**: ArrayList<String> - 动作名称列表
**实现逻辑**:
1. 调用父类actions方法（第75行）
2. 检查条件（第76行）：已装备、有充能、未诅咒、无魔法免疫
3. 满足条件时添加AC_CAST动作（第77行）

### targetingPos(Hero user, int dst)
**签名**: `int targetingPos(Hero user, int dst)`
**功能**: 获取目标位置
**参数**:
- user: Hero - 使用者
- dst: int - 目标坐标
**返回值**: int - 目标位置
**实现逻辑**:
- 直接返回目标坐标（第83行）

### execute(Hero hero, String action)
**签名**: `void execute(Hero hero, String action)`
**功能**: 执行指定的物品动作
**参数**:
- hero: Hero - 执行动作的英雄
- action: String - 要执行的动作名称
**返回值**: void
**实现逻辑**:
1. 调用父类execute方法（第89行）
2. 检查魔法免疫状态（第91行）
3. 如果动作是AC_CAST（第93-112行）：
   - 设置当前用户（第95行）
   - 检查装备、充能、诅咒状态（第97-107行）
   - 打开单元格选择器（第111行）

### resetForTrinity(int visibleLevel)
**签名**: `void resetForTrinity(int visibleLevel)`
**功能**: 为三位一体模式重置物品状态
**参数**:
- visibleLevel: int - 可见等级
**返回值**: void
**实现逻辑**:
1. 调用父类方法（第119行）
2. 设置充能为软上限：5 + 等级*2（第120行）

### chainEnemy(Ballistica chain, final Hero hero, final Char enemy)
**签名**: `private void chainEnemy(Ballistica chain, final Hero hero, final Char enemy)`
**功能**: 将敌人拉向英雄
**参数**:
- chain: Ballistica - 弹道计算结果
- hero: Hero - 英雄
- enemy: Char - 敌人
**返回值**: void
**实现逻辑**:
1. 检查敌人是否不可移动（第157-160行）
2. 寻找最佳拉扯位置（第162-171行）
3. 检查充能是否足够（第180-184行）
4. 播放锁链动画和音效（第187-213行）
5. 执行推拉动画，移动敌人（第194-210行）
6. 消耗充能，触发神器使用天赋（第198-201行）

### chainLocation(Ballistica chain, final Hero hero)
**签名**: `private void chainLocation(Ballistica chain, final Hero hero)`
**功能**: 将英雄拉向目标位置
**参数**:
- chain: Ballistica - 弹道计算结果
- hero: Hero - 英雄
**返回值**: void
**实现逻辑**:
1. 检查英雄是否被定身（第220-224行）
2. 检查目标位置是否有效（第227-244行）
3. 检查充能是否足够（第248-252行）
4. 播放锁链动画和音效（第257-279行）
5. 执行推拉动画，移动英雄（第262-276行）

### passiveBuff()
**签名**: `protected ArtifactBuff passiveBuff()`
**功能**: 获取被动Buff实例
**参数**: 无
**返回值**: ArtifactBuff - chainsRecharge实例

### charge(Hero target, float amount)
**签名**: `void charge(Hero target, float amount)`
**功能**: 为锁链充能
**参数**:
- target: Hero - 目标英雄
- amount: float - 充能数量
**返回值**: void
**实现逻辑**:
1. 检查诅咒和魔法免疫（第289行）
2. 计算充能上限：5 + 等级*2（第290行）
3. 如果充能未达上限的2倍，增加充能（第291-298行）

### desc()
**签名**: `String desc()`
**功能**: 获取物品描述文本
**参数**: 无
**返回值**: String - 描述文本
**实现逻辑**:
1. 获取基础描述（第303行）
2. 如果已装备，添加装备状态描述（第305-311行）

## 内部类 chainsRecharge

### act()
**签名**: `boolean act()`
**功能**: 每帧执行的Buff行为
**参数**: 无
**返回值**: boolean - 是否继续执行
**实现逻辑**:
1. 计算充能目标（第319行）
2. 如果充能未满且无诅咒和魔法免疫（第320-327行）：
   - 计算充能速度：(1 / (40 - (目标-当前)*2))
   - 应用能量戒指加成
3. 如果诅咒且随机触发，施加残废效果（第328-330行）
4. 转换部分充能为整数充能（第332-335行）

### gainExp(float levelPortion)
**签名**: `void gainExp(float levelPortion)`
**功能**: 通过经验升级锁链
**参数**:
- levelPortion: float - 经验比例
**返回值**: void
**实现逻辑**:
1. 检查诅咒和魔法免疫（第345行）
2. 增加经验值（第347行）
3. 超过软上限时，充能效率降低（第350-352行）
4. 经验足够时升级（第355-360行）

## 11. 使用示例
```java
// 创建虚灵锁链
EtherealChains chains = new EtherealChains();

// 装备锁链
chains.doEquip(hero);

// 投射锁链（选择目标）
chains.execute(hero, EtherealChains.AC_CAST);
// 对敌人：将敌人拉向自己
// 对空地：将自己拉向目标位置

// 充能随时间恢复
// 使用时消耗充能，距离越远消耗越多
```

## 注意事项
1. 锁链对不可移动的敌人无效
2. 拉扯英雄需要目标位置附近有可抓取的固体
3. 英雄被定身时无法自我拉扯
4. 诅咒状态下会随机施加残废效果
5. 等级上限为5级，充能上限随等级增长

## 最佳实践
1. 用于控制敌人位置或快速移动
2. 配合远程攻击可以在安全距离战斗
3. 升级锁链增加充能上限
4. 注意充能消耗与距离的关系
5. 在采矿层中可以无视可到达性检查