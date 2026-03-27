# CloakOfShadows 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/CloakOfShadows.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.artifacts |
| 类类型 | public class |
| 继承关系 | extends Artifact |
| 代码行数 | 401行 |

## 2. 类职责说明
暗影斗篷是盗贼职业专属的神器物品，允许玩家进入隐身状态。使用时消耗充能进入隐身，每回合消耗1点充能。隐身期间可以准备伏击攻击（刺客子职业），也可以获得保护性阴影效果（相应天赋）。斗篷可以通过天赋在不装备时使用（轻灵斗篷天赋）。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Artifact {
        <<abstract>>
        +int charge
        +int chargeCap
        +int levelCap
        +ArtifactBuff passiveBuff()
        +ArtifactBuff activeBuff()
    }
    
    class CloakOfShadows {
        -int turnsToCost
        +String AC_STEALTH
        +ArrayList~String~ actions(Hero)
        +void execute(Hero, String)
        +void activate(Char)
        +boolean doUnequip(Hero, boolean, boolean)
        +boolean collect(Bag)
        +void charge(Hero, float)
        +void directCharge(int)
        +Item upgrade()
        +int value()
    }
    
    class cloakRecharge {
        +boolean act()
    }
    
    class cloakStealth {
        -int turnsToCost
        +int icon()
        +void tintIcon(Image)
        +float iconFadePercent()
        +String iconTextDisplay()
        +String desc()
        +boolean attachTo(Char)
        +boolean act()
        +void dispel()
        +void fx(boolean)
        +void detach()
    }
    
    class ArtifactBuff {
        <<abstract>>
    }
    
    Artifact <|-- CloakOfShadows
    ArtifactBuff <|-- cloakRecharge
    ArtifactBuff <|-- cloakStealth
    CloakOfShadows +-- cloakRecharge
    CloakOfShadows +-- cloakStealth
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_STEALTH | String | "STEALTH" | 隐身动作标识，用于进入/退出隐身状态 |
| STEALTHED | String | "stealthed" | Bundle存储键（已弃用） |
| BUFF | String | "buff" | Bundle存储键，用于序列化活跃Buff |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标，使用ItemSpriteSheet.ARTIFACT_CLOAK |
| exp | int | - | 经验值，初始为0 |
| levelCap | int | - | 等级上限为10级 |
| charge | int | - | 当前充能，初始为等级+3（最大10） |
| partialCharge | float | - | 部分充能值 |
| chargeCap | int | - | 充能上限，为等级+3（最大10） |
| defaultAction | String | - | 默认动作为AC_STEALTH |
| unique | boolean | - | 是否为唯一物品（true） |
| bones | boolean | - | 是否可出现在遗骨中（false） |

## 7. 方法详解

### actions(Hero hero)
**签名**: `ArrayList<String> actions(Hero hero)`
**功能**: 获取该物品可用的动作列表
**参数**:
- hero: Hero - 英雄对象
**返回值**: ArrayList<String> - 动作名称列表
**实现逻辑**:
1. 调用父类actions方法（第72行）
2. 检查条件（第73-76行）：
   - 已装备或有轻灵斗篷天赋
   - 未诅咒
   - 无魔法免疫
   - 有充能或正在隐身
3. 满足条件时添加AC_STEALTH动作（第77行）
4. 返回动作列表（第79行）

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
3. 如果动作是AC_STEALTH（第89-112行）：
   - 如果未隐身（activeBuff==null）（第91-103行）：
     - 检查装备、诅咒、充能状态
     - 花费1回合，播放音效
     - 激活隐身Buff
   - 如果正在隐身（第104-111行）：
     - 取消隐身Buff
     - 如果完全显形，移除准备状态
     - 播放动画

### activate(Char ch)
**签名**: `void activate(Char ch)`
**功能**: 激活斗篷的被动效果
**参数**:
- ch: Char - 目标角色
**返回值**: void
**实现逻辑**:
1. 调用父类activate方法（第118行）
2. 如果activeBuff存在但目标为null，重新附加到角色（第119-121行）

### doUnequip(Hero hero, boolean collect, boolean single)
**签名**: `boolean doUnequip(Hero hero, boolean collect, boolean single)`
**功能**: 卸下斗篷
**参数**:
- hero: Hero - 卸下装备的英雄
- collect: boolean - 是否放入背包
- single: boolean - 是否为单独操作
**返回值**: boolean - 是否卸下成功
**实现逻辑**:
1. 调用父类doUnequip方法（第126行）
2. 如果成功（第127-136行）：
   - 如果不收集或无轻灵斗篷天赋，移除隐身Buff
   - 否则重新激活斗篷

### collect(Bag container)
**签名**: `boolean collect(Bag container)`
**功能**: 将斗篷放入背包
**参数**:
- container: Bag - 容器
**返回值**: boolean - 是否成功
**实现逻辑**:
1. 调用父类collect方法（第143行）
2. 如果拥有轻灵斗篷天赋，激活被动效果（第144-148行）

### onDetach()
**签名**: `protected void onDetach()`
**功能**: 从背包移除时的处理
**参数**: 无
**返回值**: void
**实现逻辑**:
1. 移除被动Buff（第157-160行）
2. 如果未装备，移除主动Buff（第161-164行）

### passiveBuff()
**签名**: `protected ArtifactBuff passiveBuff()`
**功能**: 获取被动Buff实例
**参数**: 无
**返回值**: ArtifactBuff - cloakRecharge实例
**实现逻辑**:
- 创建并返回新的cloakRecharge内部类实例（第169行）

### activeBuff()
**签名**: `protected ArtifactBuff activeBuff()`
**功能**: 获取主动Buff实例
**参数**: 无
**返回值**: ArtifactBuff - cloakStealth实例
**实现逻辑**:
- 创建并返回新的cloakStealth内部类实例（第174行）

### charge(Hero target, float amount)
**签名**: `void charge(Hero target, float amount)`
**功能**: 为斗篷充能
**参数**:
- target: Hero - 目标英雄
- amount: float - 充能数量
**返回值**: void
**实现逻辑**:
1. 检查诅咒和魔法免疫状态（第179行）
2. 如果充能未满（第181行）：
   - 如果未装备，充能效率降低（第182行）
   - 增加部分充能（第183行）
   - 转换为整数充能（第184-187行）
   - 限制不超过上限（第188-191行）
3. 更新快捷栏（第192行）

### directCharge(int amount)
**签名**: `void directCharge(int amount)`
**功能**: 直接增加充能
**参数**:
- amount: int - 充能数量
**返回值**: void
**实现逻辑**:
- 直接增加充能，不超过上限（第197-198行）

### upgrade()
**签名**: `Item upgrade()`
**功能**: 升级斗篷，增加充能上限
**参数**: 无
**返回值**: Item - 升级后的物品
**实现逻辑**:
1. 增加充能上限，最大为10（第203行）
2. 调用父类upgrade方法（第204行）

### storeInBundle(Bundle bundle)
**签名**: `void storeInBundle(Bundle bundle)`
**功能**: 将物品状态保存到Bundle
**参数**:
- bundle: Bundle - 存储容器
**返回值**: void
**实现逻辑**:
1. 调用父类storeInBundle方法（第212行）
2. 如果activeBuff存在，保存它（第213行）

### restoreFromBundle(Bundle bundle)
**签名**: `void restoreFromBundle(Bundle bundle)`
**功能**: 从Bundle恢复物品状态
**参数**:
- bundle: Bundle - 存储容器
**返回值**: void
**实现逻辑**:
1. 调用父类restoreFromBundle方法（第218行）
2. 如果存在保存的Buff，恢复它（第219-222行）

### value()
**签名**: `int value()`
**功能**: 获取物品价值
**参数**: 无
**返回值**: int - 价值（0，因为是独特物品）
**实现逻辑**:
- 返回0（第227行）

## 内部类 cloakRecharge

### act()
**签名**: `boolean act()`
**功能**: 每帧执行的Buff行为（充能逻辑）
**参数**: 无
**返回值**: boolean - 是否继续执行
**实现逻辑**:
1. 检查充能未满且无诅咒和魔法免疫（第233行）
2. 如果未隐身且可以恢复（第234行）：
   - 计算充能速度（第235-242行）
   - 应用能量戒指加成
   - 未装备时充能效率降低
3. 转换部分充能为整数充能（第246-253行）
4. 减少冷却（第258-259行）
5. 更新快捷栏（第261行）
6. 花费1tick时间（第263行）

## 内部类 cloakStealth

### attachTo(Char target)
**签名**: `boolean attachTo(Char target)`
**功能**: 附加隐身效果到目标
**参数**:
- target: Char - 目标角色
**返回值**: boolean - 是否附加成功
**实现逻辑**:
1. 调用父类attachTo方法（第305行）
2. 增加目标隐身层数（第306行）
3. 如果是刺客子职业，添加准备Buff（第307-309行）
4. 如果有保护性阴影天赋，添加追踪器（第310-312行）

### act()
**签名**: `boolean act()`
**功能**: 每帧执行的Buff行为（消耗充能）
**参数**: 无
**返回值**: boolean - 是否继续执行
**实现逻辑**:
1. 减少回合计时器（第321行）
2. 如果计时器<=0（第323-353行）：
   - 消耗1点充能（第324行）
   - 充能为负时取消隐身（第325-329行）
   - 否则计算经验并升级（第332-350行）
   - 重置计时器为4回合（第350行）
3. 更新快捷栏（第352行）
4. 花费1tick时间（第355行）

### dispel()
**签名**: `void dispel()`
**功能**: 强制驱散隐身效果
**参数**: 无
**返回值**: void
**实现逻辑**:
1. 如果计时器<=0且有充能，消耗1点充能（第361-363行）
2. 更新快捷栏（第364行）
3. 移除Buff（第365行）

### fx(boolean on)
**签名**: `void fx(boolean on)`
**功能**: 设置视觉效果
**参数**:
- on: boolean - 是否开启效果
**返回值**: void
**实现逻辑**:
- 开启时添加隐身状态（第370行）
- 关闭时如果完全显形，移除隐身状态（第371行）

### detach()
**签名**: `void detach()`
**功能**: 移除Buff
**参数**: 无
**返回值**: void
**实现逻辑**:
1. 清除activeBuff引用（第376行）
2. 减少隐身层数（第378行）
3. 更新快捷栏（第380行）
4. 调用父类detach方法（第381行）

## 11. 使用示例
```java
// 创建暗影斗篷（盗贼专属）
CloakOfShadows cloak = new CloakOfShadows();

// 装备斗篷
cloak.doEquip(hero);

// 进入隐身
cloak.execute(hero, CloakOfShadows.AC_STEALTH);

// 隐身期间每4回合消耗1点充能
// 可以进行伏击攻击

// 退出隐身
cloak.execute(hero, CloakOfShadows.AC_STEALTH);

// 升级增加充能上限
cloak.upgrade(); // +1充能上限
```

## 注意事项
1. 盗贼专属物品，其他职业无法有效使用
2. 隐身每4回合消耗1点充能
3. 轻灵斗篷天赋允许未装备时使用（效率降低）
4. 刺客子职业隐身时会获得准备Buff
5. 斗篷不能出售（价值为0）

## 最佳实践
1. 战斗前确保充能满
2. 利用隐身进行伏击攻击
3. 升级斗篷增加充能上限
4. 配合能量戒指提高充能效率
5. 使用保护性阴影天赋获得额外护盾