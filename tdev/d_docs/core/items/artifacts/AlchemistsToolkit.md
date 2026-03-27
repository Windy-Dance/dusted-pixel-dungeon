# AlchemistsToolkit 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/AlchemistsToolkit.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.artifacts |
| 类类型 | public class |
| 继承关系 | extends Artifact |
| 代码行数 | 262行 |

## 2. 类职责说明
炼金工具箱是一个神器类物品，为玩家提供便携式炼金功能。玩家装备后可以使用炼金锅进行物品合成，同时可以通过消耗能量来升级工具箱本身。工具箱具有预热机制，装备后需要一定时间才能使用，等级越高预热时间越短。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Artifact {
        <<abstract>>
        +int charge
        +float partialCharge
        +int levelCap
        +ArtifactBuff passiveBuff()
        +void charge(Hero, float)
    }
    
    class AlchemistsToolkit {
        -float warmUpDelay
        +String AC_BREW
        +String AC_ENERGIZE
        +ArrayList~String~ actions(Hero)
        +void execute(Hero, String)
        +String status()
        +int availableEnergy()
        +int consumeEnergy(int)
        +String desc()
        +boolean doEquip(Hero)
    }
    
    class kitEnergy {
        +boolean act()
        +void gainCharge(float)
    }
    
    class ArtifactBuff {
        <<abstract>>
    }
    
    Artifact <|-- AlchemistsToolkit
    ArtifactBuff <|-- kitEnergy
    AlchemistsToolkit +-- kitEnergy
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_BREW | String | "BREW" | 炼金动作标识，用于打开炼金界面 |
| AC_ENERGIZE | String | "ENERGIZE" | 充能动作标识，用于消耗能量升级工具箱 |
| WARM_UP | String | "warm_up" | Bundle存储键，用于序列化预热延迟 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标，使用ItemSpriteSheet.ARTIFACT_TOOLKIT |
| defaultAction | String | - | 默认动作为AC_BREW |
| levelCap | int | - | 等级上限为10级 |
| charge | int | - | 当前充能值，初始为0 |
| partialCharge | float | - | 部分充能值，用于平滑充能计算 |
| warmUpDelay | float | private | 预热延迟时间，装备后需要预热才能使用 |

## 7. 方法详解

### actions(Hero hero)
**签名**: `ArrayList<String> actions(Hero hero)`
**功能**: 获取该物品可用的动作列表
**参数**:
- hero: Hero - 英雄对象
**返回值**: ArrayList<String> - 动作名称列表
**实现逻辑**:
1. 调用父类actions方法获取基础动作列表（第64行）
2. 检查是否已装备且未诅咒且英雄无魔法免疫状态（第65行）
3. 如果满足条件，添加AC_BREW动作（炼金）（第66行）
4. 如果等级未达上限，添加AC_ENERGIZE动作（升级）（第67-69行）
5. 返回动作列表（第71行）

### execute(Hero hero, String action)
**签名**: `void execute(Hero hero, String action)`
**功能**: 执行指定的物品动作
**参数**:
- hero: Hero - 执行动作的英雄
- action: String - 要执行的动作名称
**返回值**: void
**实现逻辑**:
1. 调用父类execute方法（第77行）
2. 检查英雄是否有魔法免疫，有则直接返回（第79行）
3. 如果动作是AC_BREW（第81-88行）：
   - 未装备则提示需要装备（第82行）
   - 诅咒状态则提示诅咒信息（第83行）
   - 预热未完成则提示未就绪（第84行）
   - 否则切换到炼金场景（第86-87行）
4. 如果动作是AC_ENERGIZE（第90-141行）：
   - 检查装备、诅咒状态、能量是否足够（第91-93行）
   - 计算可升级的最大等级数（第96行）
   - 显示选项窗口让玩家选择升级等级（第105-140行）
   - 选择后扣除能量并播放音效（第114-127行）

### status()
**签名**: `String status()`
**功能**: 获取物品状态显示字符串
**参数**: 无
**返回值**: String - 状态字符串
**实现逻辑**:
1. 检查是否已装备、预热中且未诅咒（第149行）
2. 如果满足条件，返回预热进度百分比（第150行）
3. 否则调用父类status方法（第152行）

### passiveBuff()
**签名**: `protected ArtifactBuff passiveBuff()`
**功能**: 获取被动Buff实例
**参数**: 无
**返回值**: ArtifactBuff - kitEnergy实例
**实现逻辑**:
- 创建并返回新的kitEnergy内部类实例（第158行）

### charge(Hero target, float amount)
**签名**: `void charge(Hero target, float amount)`
**功能**: 为工具箱充能
**参数**:
- target: Hero - 接受充能的目标英雄
- amount: float - 充能数量
**返回值**: void
**实现逻辑**:
1. 检查目标是否有魔法免疫（第163行）
2. 增加部分充能值（第164行）
3. 当部分充能值>=1时，转换为整数充能（第165-168行）
4. 更新快捷栏显示（第169行）

### availableEnergy()
**签名**: `int availableEnergy()`
**功能**: 获取可用的炼金能量数量
**参数**: 无
**返回值**: int - 当前充能值
**实现逻辑**:
- 直接返回charge字段值（第173行）

### consumeEnergy(int amount)
**签名**: `int consumeEnergy(int amount)`
**功能**: 消耗指定数量的炼金能量
**参数**:
- amount: int - 要消耗的能量数量
**返回值**: int - 剩余需要消耗的能量（如果不足）
**实现逻辑**:
1. 计算消耗后的剩余需求（第177行）
2. 扣除充能值，最小为0（第178行）
3. 触发神器使用天赋（第179行）
4. 返回剩余需求（第180行）

### desc()
**签名**: `String desc()`
**功能**: 获取物品描述文本
**参数**: 无
**返回值**: String - 描述文本
**实现逻辑**:
1. 获取基础描述文本（第185行）
2. 如果已装备（第187-191行）：
   - 诅咒状态添加诅咒描述
   - 预热中添加预热描述
   - 否则添加提示信息

### doEquip(Hero hero)
**签名**: `boolean doEquip(Hero hero)`
**功能**: 装备该神器
**参数**:
- hero: Hero - 装备英雄
**返回值**: boolean - 是否装备成功
**实现逻辑**:
1. 调用父类doEquip方法（第198行）
2. 如果成功，设置预热延迟为101（第199行）
3. 返回装备结果（第200-203行）

### storeInBundle(Bundle bundle)
**签名**: `void storeInBundle(Bundle bundle)`
**功能**: 将物品状态保存到Bundle
**参数**:
- bundle: Bundle - 存储容器
**返回值**: void
**实现逻辑**:
1. 调用父类storeInBundle方法（第210行）
2. 保存预热延迟值（第211行）

### restoreFromBundle(Bundle bundle)
**签名**: `void restoreFromBundle(Bundle bundle)`
**功能**: 从Bundle恢复物品状态
**参数**:
- bundle: Bundle - 存储容器
**返回值**: void
**实现逻辑**:
1. 调用父类restoreFromBundle方法（第216行）
2. 读取预热延迟值（第217行）

## 内部类 kitEnergy

### act()
**签名**: `boolean act()`
**功能**: 每帧执行的Buff行为
**参数**: 无
**返回值**: boolean - 是否继续执行
**实现逻辑**:
1. 如果预热延迟>0（第225行）：
   - 等级为10时立即完成预热（第226-227行）
   - 初始状态(101)设为100开始倒计时（第228-229行）
   - 未诅咒且无魔法免疫时，按公式减少预热时间（第230-232行）
   - 预热时间公式：100 / (10-level)²
2. 更新快捷栏（第234行）
3. 花费1tick时间（第237行）
4. 返回true继续执行（第238行）

### gainCharge(float levelPortion)
**签名**: `void gainCharge(float levelPortion)`
**功能**: 通过经验获取充能
**参数**:
- levelPortion: float - 经验比例
**返回值**: void
**实现逻辑**:
1. 检查诅咒和魔法免疫状态（第242行）
2. 计算充能增益：(2 + 等级) * 经验比例（第247行）
3. 应用能量戒指的充能倍率（第248行）
4. 累加部分充能（第249行）
5. 当部分充能>=1时转换为整数充能（第252-256行）

## 11. 使用示例
```java
// 创建炼金工具箱
AlchemistsToolkit toolkit = new AlchemistsToolkit();

// 装备工具箱
toolkit.doEquip(hero);

// 检查可用能量
int energy = toolkit.availableEnergy();

// 消耗能量
int remaining = toolkit.consumeEnergy(5);

// 升级工具箱（通过UI操作）
// 需要消耗6点炼金能量升级1级
```

## 注意事项
1. 工具箱装备后需要预热才能使用，等级越高预热越快
2. 升级工具箱需要消耗炼金能量（每级6点）
3. 魔法免疫状态下无法使用工具箱功能
4. 工具箱可以为炼金锅提供额外能量储备

## 最佳实践
1. 尽早装备工具箱以完成预热
2. 优先升级工具箱以提高能量上限和减少预热时间
3. 配合能量戒指可以提高充能效率
4. 在炼金场景中充分利用工具箱的额外能量