# MasterThievesArmband 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/MasterThievesArmband.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.artifacts |
| 类类型 | public class |
| 继承关系 | extends Artifact |
| 代码行数 | 339行 |

## 2. 类职责说明
大师盗贼臂章是一个盗窃型神器，允许玩家从敌人身上偷窃物品。偷窃成功时获得敌人的战利品，并致盲和残废敌人。偷窃有成功率，受等级、惊喜状态和敌人是否已被偷窃影响。成功偷窃会获得经验并升级臂章。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Artifact {
        <<abstract>>
        +int charge
        +int chargeCap
        +int levelCap
        +int exp
        +ArtifactBuff passiveBuff()
    }
    
    class MasterThievesArmband {
        +String AC_STEAL
        +CellSelector.Listener targeter
        +ArrayList~String~ actions(Hero)
        +void execute(Hero, String)
        +Item upgrade()
        +String desc()
    }
    
    class StolenTracker {
        +void setItemStolen(boolean)
        +boolean itemWasStolen()
    }
    
    class Thievery {
        +boolean act()
        +void gainCharge(float)
        +boolean steal(Item)
        +float stealChance(Item)
        +int chargesToUse(Item)
    }
    
    class ArtifactBuff {
        <<abstract>>
    }
    
    class CounterBuff {
        <<abstract>>
    }
    
    Artifact <|-- MasterThievesArmband
    CounterBuff <|-- StolenTracker
    ArtifactBuff <|-- Thievery
    MasterThievesArmband +-- StolenTracker
    MasterThievesArmband +-- Thievery
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_STEAL | String | "STEAL" | 偷窃动作标识 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标，使用ItemSpriteSheet.ARTIFACT_ARMBAND |
| levelCap | int | - | 等级上限为10级 |
| charge | int | - | 当前充能，初始为0 |
| partialCharge | float | - | 部分充能值 |
| chargeCap | int | - | 充能上限，5 + 等级/2 |
| defaultAction | String | - | 默认动作为AC_STEAL |
| targeter | CellSelector.Listener | public | 目标选择监听器 |

## 7. 方法详解

### actions(Hero hero)
**签名**: `ArrayList<String> actions(Hero hero)`
**功能**: 获取该物品可用的动作列表
**参数**:
- hero: Hero - 英雄对象
**返回值**: ArrayList<String> - 动作名称列表
**实现逻辑**:
1. 调用父类actions方法（第73行）
2. 检查条件（第74-77行）：已装备、有充能、无魔法免疫、未诅咒
3. 满足条件时添加AC_STEAL动作（第78行）

### execute(Hero hero, String action)
**签名**: `void execute(Hero hero, String action)`
**功能**: 执行指定的物品动作
**参数**:
- hero: Hero - 执行动作的英雄
- action: String - 要执行的动作名称
**返回值**: void
**实现逻辑**:
1. 调用父类execute方法（第85行）
2. 检查魔法免疫状态（第87行）
3. 如果动作是AC_STEAL（第89-108行）：
   - 检查装备、充能、诅咒状态
   - 打开目标选择器

### upgrade()
**签名**: `Item upgrade()`
**功能**: 升级臂章，增加充能上限
**参数**: 无
**返回值**: Item - 升级后的物品
**实现逻辑**:
1. 更新充能上限：5 + (等级+1)/2（第242行）
2. 调用父类upgrade方法（第243行）

### desc()
**签名**: `String desc()`
**功能**: 获取物品描述文本
**参数**: 无
**返回值**: String - 描述文本
**实现逻辑**:
1. 获取基础描述（第248行）
2. 如果已装备，添加装备状态描述（第250-256行）

### passiveBuff()
**签名**: `protected ArtifactBuff passiveBuff()`
**功能**: 获取被动Buff实例
**参数**: 无
**返回值**: ArtifactBuff - Thievery实例

### charge(Hero target, float amount)
**签名**: `void charge(Hero target, float amount)`
**功能**: 为臂章充能
**参数**:
- target: Hero - 目标英雄
- amount: float - 充能数量
**返回值**: void
**实现逻辑**:
1. 检查诅咒和魔法免疫（第224行）
2. 增加充能，不超过上限（第225-237行）

## 内部类 StolenTracker

继承自CounterBuff，用于标记敌人是否已被偷窃。

### setItemStolen(boolean stolen)
**功能**: 设置偷窃状态
**参数**:
- stolen: boolean - 是否成功偷窃

### itemWasStolen()
**功能**: 检查是否已被偷窃
**返回值**: boolean - 是否已偷窃

## 内部类 Thievery

### act()
**签名**: `boolean act()`
**功能**: 每帧执行的Buff行为
**参数**: 无
**返回值**: boolean - 是否继续执行
**实现逻辑**:
1. 如果诅咒且随机触发，丢失1金币（第265-268行）
2. 花费1tick时间（第270行）

### gainCharge(float levelPortion)
**签名**: `void gainCharge(float levelPortion)`
**功能**: 通过经验获取充能
**参数**:
- levelPortion: float - 经验比例
**返回值**: void
**实现逻辑**:
1. 检查诅咒和魔法免疫（第275行）
2. 计算充能增益：3 * 比例 * 能量戒指倍率（第278-279行）
3. 增加充能（第281-291行）

### steal(Item item)
**签名**: `boolean steal(Item item)`
**功能**: 从商店偷窃物品
**参数**:
- item: Item - 要偷窃的物品
**返回值**: boolean - 是否偷窃成功
**实现逻辑**:
1. 计算需要的充能和成功率（第299-300行）
2. 随机判断是否成功（第301-303行）
3. 成功时扣除充能，获得经验（第304-316行）

### stealChance(Item item)
**签名**: `float stealChance(Item item)`
**功能**: 计算偷窃物品的成功率
**参数**:
- item: Item - 物品
**返回值**: float - 成功率（0-1）
**实现逻辑**:
- 成功率 = min(1, 充能数 * (10 + 等级/2) / 物品价值)（第322行）

### chargesToUse(Item item)
**签名**: `int chargesToUse(Item item)`
**功能**: 计算偷窃物品需要的充能数
**参数**:
- item: Item - 物品
**返回值**: int - 需要的充能数
**实现逻辑**:
- 计算需要多少充能才能覆盖物品价值（第327-334行）

## 内部字段 targeter

目标选择监听器，用于选择偷窃目标。

### onSelect(Integer target)
**功能**: 选择目标后的处理
**参数**:
- target: Integer - 目标位置
**实现逻辑**:
1. 检查目标有效性（第118-128行）
2. 如果目标是商店老板，提示无法偷窃（第124-125行）
3. 如果目标是敌对生物，执行偷窃（第129-198行）：
   - 计算惊喜状态（第136行）
   - 计算战利品倍率和减益持续时间（第137-138行）
   - 如果惊喜状态，增加倍率和经验（第142-148行）
   - 判断是否可以偷窃（第152-156行）
   - 尝试偷窃并生成战利品（第160-178行）
   - 施加致盲和残废效果（第180-181行）
   - 消耗充能，获得经验（第185-194行）

## 11. 使用示例
```java
// 创建大师盗贼臂章
MasterThievesArmband armband = new MasterThievesArmband();

// 装备臂章
armband.doEquip(hero);

// 偷窃敌人
armband.execute(hero, MasterThievesArmband.AC_STEAL);
// 选择相邻的敌人进行偷窃

// 偷窃商店物品（通过Thievery Buff）
Thievery buff = hero.buff(Thievery.class);
if (buff != null && buff.steal(item)) {
    // 偷窃成功
}
```

## 注意事项
1. 只能偷窃相邻的敌人
2. 商店老板无法被偷窃
3. 同一敌人只能被偷窃一次
4. 英雄等级超过敌人最大等级+2时无法偷窃
5. 诅咒状态下会随机丢失金币

## 最佳实践
1. 在敌人未发现时偷窃（惊喜状态增加成功率）
2. 升级臂章提高成功率和充能上限
3. 优先偷窃高价值战利品的敌人
4. 配合隐身效果更容易进入惊喜状态
5. 注意偷窃会消耗充能并致盲敌人