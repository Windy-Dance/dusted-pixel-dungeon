# HornOfPlenty 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/HornOfPlenty.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.artifacts |
| 类类型 | public class |
| 继承关系 | extends Artifact |
| 代码行数 | 348行 |

## 2. 类职责说明
丰饶之角是一个食物型神器，可以通过存储食物来充能，然后释放能量来充饥。玩家可以将食物存入号角中升级它，之后可以使用号角中的能量来满足饥饿需求。每点充能可以提供相当于1/5最大饥饿值的饱食度。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Artifact {
        <<abstract>>
        +int charge
        +int chargeCap
        +int levelCap
        +ArtifactBuff passiveBuff()
    }
    
    class HornOfPlenty {
        -int storedFoodEnergy
        +String AC_SNACK
        +String AC_EAT
        +String AC_STORE
        +ArrayList~String~ actions(Hero)
        +void execute(Hero, String)
        +void doEatEffect(Hero, int)
        +void charge(Hero, float)
        +void level(int)
        +Item upgrade()
        +void gainFoodValue(Food)
    }
    
    class hornRecharge {
        +void gainCharge(float)
    }
    
    class ArtifactBuff {
        <<abstract>>
    }
    
    Artifact <|-- HornOfPlenty
    ArtifactBuff <|-- hornRecharge
    HornOfPlenty +-- hornRecharge
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_SNACK | String | "SNACK" | 小吃动作标识，使用1点充能 |
| AC_EAT | String | "EAT" | 进食动作标识，使用足够充能填饱肚子 |
| AC_STORE | String | "STORE" | 存储动作标识，将食物存入号角 |
| STORED | String | "stored" | Bundle存储键 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标，根据充能变化（HORN1-4） |
| levelCap | int | - | 等级上限为10级 |
| charge | int | - | 当前充能，初始为0 |
| partialCharge | float | - | 部分充能值 |
| chargeCap | int | - | 充能上限，5 + 等级/2 |
| defaultAction | String | - | 默认动作为AC_SNACK |
| storedFoodEnergy | int | private | 存储的食物能量值 |

## 7. 方法详解

### actions(Hero hero)
**签名**: `ArrayList<String> actions(Hero hero)`
**功能**: 获取该物品可用的动作列表
**参数**:
- hero: Hero - 英雄对象
**返回值**: ArrayList<String> - 动作名称列表
**实现逻辑**:
1. 调用父类actions方法（第78行）
2. 检查魔法免疫（第79行）
3. 如果已装备且有充能，添加进食动作（第80-83行）
4. 如果已装备且未满级且未诅咒，添加存储动作（第84-86行）

### execute(Hero hero, String action)
**签名**: `void execute(Hero hero, String action)`
**功能**: 执行指定的物品动作
**参数**:
- hero: Hero - 执行动作的英雄
- action: String - 要执行的动作名称
**返回值**: void
**实现逻辑**:
1. 调用父类execute方法（第93行）
2. 检查魔法免疫（第95行）
3. 如果动作是AC_EAT或AC_SNACK（第97-118行）：
   - 计算每点充能的饱食度（第103-106行）
   - 计算需要使用的充能数（第109-115行）
   - 执行进食效果（第117行）
4. 如果动作是AC_STORE（第120-124行）：
   - 打开物品选择器（第122行）

### doEatEffect(Hero hero, int chargesToUse)
**签名**: `void doEatEffect(Hero hero, int chargesToUse)`
**功能**: 执行进食效果
**参数**:
- hero: Hero - 英雄
- chargesToUse: int - 使用的充能数
**返回值**: void
**实现逻辑**:
1. 计算饱食度（第128-131行）
2. 满足饥饿需求（第133行）
3. 更新统计（第135行）
4. 消耗充能，触发神器使用天赋（第137-138行）
5. 播放动画和音效（第140-144行）
6. 处理进食天赋（第146-157行）
7. 更新图标（第161-165行）

### passiveBuff()
**签名**: `protected ArtifactBuff passiveBuff()`
**功能**: 获取被动Buff实例
**参数**: 无
**返回值**: ArtifactBuff - hornRecharge实例

### charge(Hero target, float amount)
**签名**: `void charge(Hero target, float amount)`
**功能**: 为号角充能
**参数**:
- target: Hero - 目标英雄
- amount: float - 充能数量
**返回值**: void
**实现逻辑**:
1. 检查充能未满、未诅咒、无魔法免疫（第176行）
2. 增加部分充能（第177行）
3. 转换为整数充能（第178-193行）
4. 充能满时显示提示（第182-184行）
5. 更新图标（第187-191行）

### level(int value)
**签名**: `void level(int value)`
**功能**: 设置物品等级
**参数**:
- value: int - 等级值
**返回值**: void
**实现逻辑**:
1. 调用父类level方法（第215行）
2. 更新充能上限（第216行）

### upgrade()
**签名**: `Item upgrade()`
**功能**: 升级号角
**参数**: 无
**返回值**: Item - 升级后的物品
**实现逻辑**:
1. 调用父类upgrade方法（第221行）
2. 更新充能上限（第222行）

### gainFoodValue(Food food)
**签名**: `void gainFoodValue(Food food)`
**功能**: 从食物获取升级价值
**参数**:
- food: Food - 食物物品
**返回值**: void
**实现逻辑**:
1. 检查是否已满级（第227行）
2. 累加食物能量（第229行）
3. 特殊食物额外加成（第231-235行）
4. 能量足够时升级（第236-250行）

### storeInBundle(Bundle bundle)
**签名**: `void storeInBundle(Bundle bundle)`
**功能**: 将物品状态保存到Bundle
**参数**:
- bundle: Bundle - 存储容器
**返回值**: void

### restoreFromBundle(Bundle bundle)
**签名**: `void restoreFromBundle(Bundle bundle)`
**功能**: 从Bundle恢复物品状态
**参数**:
- bundle: Bundle - 存储容器
**返回值**: void
**实现逻辑**:
1. 调用父类方法（第263行）
2. 恢复存储的食物能量（第265行）
3. 更新图标（第267-270行）

## 内部类 hornRecharge

### gainCharge(float levelPortion)
**签名**: `void gainCharge(float levelPortion)`
**功能**: 通过经验获取充能
**参数**:
- levelPortion: float - 经验比例
**返回值**: void
**实现逻辑**:
1. 检查诅咒和魔法免疫（第275行）
2. 如果充能未满（第277-308行）：
   - 计算充能增益：饥饿值 * 比例 * (0.25 + 0.125*等级)
   - 应用能量戒指加成
   - 转换为充能单位
   - 充能满时显示提示

## 静态字段 itemSelector

物品选择器，用于选择要存入号角的食物。

### onSelect(Item item)
**功能**: 选择食物后的处理
**参数**:
- item: Item - 选中的物品
**实现逻辑**:
1. 检查是否是有效食物（第332-334行）
2. 播放动画，花费时间（第337-339行）
3. 获取食物价值并存入号角（第341-342行）

## 11. 使用示例
```java
// 创建丰饶之角
HornOfPlenty horn = new HornOfPlenty();

// 装备号角
horn.doEquip(hero);

// 存储食物升级
horn.execute(hero, HornOfPlenty.AC_STORE);
// 选择食物存入号角

// 使用号角进食
horn.execute(hero, HornOfPlenty.AC_SNACK); // 使用1点充能
horn.execute(hero, HornOfPlenty.AC_EAT);   // 使用足够充能填饱肚子

// 每点充能 = 1/5最大饥饿值
```

## 注意事项
1. 无味水果不能存入号角
2. 馅饼和幻影肉有额外的升级价值
3. 肉饼的升级价值是普通食物的4倍
4. "禁食"挑战下每点充能的饱食度降低为1/3
5. 进食会触发相关天赋效果

## 最佳实践
1. 优先存储高价值食物（肉饼、馅饼等）
2. 保持充能满以备不时之需
3. 升级号角增加充能上限
4. 使用小吃模式节省充能
5. 配合能量戒指提高充能效率