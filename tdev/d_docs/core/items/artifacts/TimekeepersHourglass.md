# TimekeepersHourglass 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/TimekeepersHourglass.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.artifacts |
| 类类型 | public class |
| 继承关系 | extends Artifact |
| 代码行数 | 546行 |

## 2. 类职责说明
守时者的沙漏是一个时间控制型神器，提供两种时间操控能力：时间停滞（进入隐身和麻痹状态，时间快速流逝）和时间冻结（敌人停止行动，玩家可以自由行动）。通过收集沙袋来升级沙漏，增加充能上限。

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
    
    class TimekeepersHourglass {
        +int sandBags
        +String AC_ACTIVATE
        +ArrayList~String~ actions(Hero)
        +void execute(Hero, String)
        +Item upgrade()
        +String desc()
        +void activate(Char)
        +boolean doUnequip(Hero, boolean, boolean)
    }
    
    class hourglassRecharge {
        +boolean act()
    }
    
    class timeStasis {
        +boolean attachTo(Char)
        +boolean act()
        +void detach()
        +void fx(boolean)
    }
    
    class timeFreeze {
        -float turnsToCost
        -ArrayList~Integer~ presses
        +void processTime(float)
        +void setDelayedPress(int)
        +void triggerPresses()
        +void disarmPresses()
        +void detach()
        +void fx(boolean)
        +int icon()
    }
    
    class sandBag {
        +boolean doPickUp(Hero, int)
        +int value()
    }
    
    class ArtifactBuff {
        <<abstract>>
    }
    
    class Item {
        <<abstract>>
    }
    
    Artifact <|-- TimekeepersHourglass
    ArtifactBuff <|-- hourglassRecharge
    ArtifactBuff <|-- timeStasis
    ArtifactBuff <|-- timeFreeze
    Item <|-- sandBag
    TimekeepersHourglass +-- hourglassRecharge
    TimekeepersHourglass +-- timeStasis
    TimekeepersHourglass +-- timeFreeze
    TimekeepersHourglass +-- sandBag
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_ACTIVATE | String | "ACTIVATE" | 激活动作标识 |
| SANDBAGS | String | "sandbags" | Bundle存储键 |
| BUFF | String | "buff" | Bundle存储键 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标，使用ItemSpriteSheet.ARTIFACT_HOURGLASS |
| levelCap | int | - | 等级上限为5级 |
| charge | int | - | 当前充能，初始为5+等级 |
| chargeCap | int | - | 充能上限，5+等级 |
| defaultAction | String | - | 默认动作为AC_ACTIVATE |
| sandBags | int | public | 已收集的沙袋数量 |

## 7. 方法详解

### resetForTrinity(int visibleLevel)
**签名**: `void resetForTrinity(int visibleLevel)`
**功能**: 为三位一体模式重置物品状态
**参数**:
- visibleLevel: int - 可见等级
**返回值**: void
**实现逻辑**:
1. 调用父类方法（第75行）
2. 设置充能为等级/2-1（第76行）

### actions(Hero hero)
**签名**: `ArrayList<String> actions(Hero hero)`
**功能**: 获取该物品可用的动作列表
**参数**:
- hero: Hero - 英雄对象
**返回值**: ArrayList<String> - 动作名称列表
**实现逻辑**:
1. 调用父类actions方法（第86行）
2. 检查条件（第87-90行）：已装备、未诅咒、无魔法免疫、有充能或激活中
3. 满足条件时添加AC_ACTIVATE动作（第91行）

### execute(Hero hero, String action)
**签名**: `void execute(Hero hero, String action)`
**功能**: 执行指定的物品动作
**参数**:
- hero: Hero - 执行动作的英雄
- action: String - 要执行的动作名称
**返回值**: void
**实现逻辑**:
1. 调用父类execute方法（第99行）
2. 检查魔法免疫（第101行）
3. 如果动作是AC_ACTIVATE（第103-153行）：
   - 检查装备、充能、诅咒状态（第105-113行）
   - 显示选项窗口（第114-152行）：
     - 时间停滞：进入隐身麻痹状态
     - 时间冻结：敌人停止，玩家自由行动

### activate(Char ch)
**签名**: `void activate(Char ch)`
**功能**: 激活沙漏（恢复存档时）
**参数**:
- ch: Char - 目标角色
**返回值**: void
**实现逻辑**:
1. 调用父类activate方法（第158行）
2. 如果有活跃Buff，重新附加（第159-160行）

### doUnequip(Hero hero, boolean collect, boolean single)
**签名**: `boolean doUnequip(Hero hero, boolean collect, boolean single)`
**功能**: 卸下沙漏
**参数**:
- hero: Hero - 卸下装备的英雄
- collect: boolean - 是否放入背包
- single: boolean - 是否为单独操作
**返回值**: boolean - 是否卸下成功
**实现逻辑**:
1. 调用父类doUnequip方法（第165行）
2. 如果成功，移除活跃Buff（第166-169行）

### passiveBuff()
**签名**: `protected ArtifactBuff passiveBuff()`
**功能**: 获取被动Buff实例
**参数**: 无
**返回值**: ArtifactBuff - hourglassRecharge实例

### charge(Hero target, float amount)
**签名**: `void charge(Hero target, float amount)`
**功能**: 为沙漏充能
**参数**:
- target: Hero - 目标英雄
- amount: float - 充能数量
**返回值**: void
**实现逻辑**:
- 增加充能，不超过上限（第182-192行）

### upgrade()
**签名**: `Item upgrade()`
**功能**: 升级沙漏，增加充能上限和沙袋计数
**参数**: 无
**返回值**: Item - 升级后的物品
**实现逻辑**:
1. 增加充能上限（第197行）
2. 更新沙袋计数（第200-201行）
3. 调用父类upgrade方法（第203行）

### desc()
**签名**: `String desc()`
**功能**: 获取物品描述文本
**参数**: 无
**返回值**: String - 描述文本
**实现逻辑**:
1. 获取基础描述（第208行）
2. 添加装备状态描述（第210-217行）

## 内部类 hourglassRecharge

### act()
**签名**: `boolean act()`
**功能**: 每帧执行的充能逻辑
**参数**: 无
**返回值**: boolean - 是否继续执行
**实现逻辑**:
1. 检查充能未满、未诅咒、无魔法免疫、可恢复（第256-259行）
2. 计算充能速度：1 / (90 - (上限-当前)*3)（第261行）
3. 应用能量戒指加成（第262行）
4. 转换部分充能为整数充能（第265-272行）
5. 如果诅咒且随机触发，花费时间（第273-274行）

## 内部类 timeStasis

时间停滞Buff，进入隐身麻痹状态，时间快速流逝。

### attachTo(Char target)
**签名**: `boolean attachTo(Char target)`
**功能**: 附加停滞效果
**参数**:
- target: Char - 目标角色
**返回值**: boolean - 是否附加成功
**实现逻辑**:
1. 调用父类attachTo方法（第294行）
2. 取消隐身（第296行）
3. 计算使用的充能数（第298行）
4. 花费时间：5*充能数回合（第300行）
5. 满足饥饿需求（第303-306行）
6. 消耗充能（第308行）
7. 增加隐身和麻痹层数（第310-311行）

### act()
**签名**: `boolean act()`
**功能**: 每帧执行，结束时移除Buff
**参数**: 无
**返回值**: boolean - 是否继续执行
**实现逻辑**:
- 移除Buff（第328行）

### detach()
**签名**: `void detach()`
**功能**: 移除Buff时的清理
**参数**: 无
**返回值**: void
**实现逻辑**:
1. 减少隐身和麻痹层数（第334-335行）
2. 调用父类detach方法（第336行）
3. 清除活跃Buff引用（第337行）
4. 更新视野（第338行）

### fx(boolean on)
**功能**: 设置视觉效果
**参数**:
- on: boolean - 是否开启
**实现逻辑**:
- 开启时添加麻痹状态效果（第343行）
- 关闭时如果不再麻痹/隐身，移除效果（第345-346行）

## 内部类 timeFreeze

时间冻结Buff，敌人停止行动，玩家可以自由行动。

### processTime(float time)
**签名**: `void processTime(float time)`
**功能**: 处理时间流逝
**参数**:
- time: float - 时间量
**返回值**: void
**实现逻辑**:
1. 减少回合计时器（第362行）
2. 每2回合消耗1点充能（第365-368行）
3. 更新快捷栏（第370行）
4. 如果充能不足，移除Buff（第372-375行）

### setDelayedPress(int cell)
**签名**: `void setDelayedPress(int cell)`
**功能**: 设置延迟触发的陷阱
**参数**:
- cell: int - 单元格位置
**返回值**: void
**实现逻辑**:
- 如果不在列表中，添加到延迟触发列表（第380-381行）

### triggerPresses()
**签名**: `void triggerPresses()`
**功能**: 触发所有延迟的陷阱
**参数**: 无
**返回值**: void
**实现逻辑**:
1. 创建临时Actor处理陷阱触发（第387-407行）
2. 触发植物和陷阱（第395-402行）

### disarmPresses()
**签名**: `void disarmPresses()`
**功能**: 解除所有延迟的陷阱
**参数**: 无
**返回值**: void
**实现逻辑**:
1. 移除植物（腐莓除外）（第413-414行）
2. 解除陷阱（第416-419行）

### detach()
**签名**: `void detach()`
**功能**: 移除Buff时的处理
**参数**: 无
**返回值**: void
**实现逻辑**:
1. 更新快捷栏（第427行）
2. 调用父类detach方法（第428行）
3. 清除活跃Buff引用（第429行）
4. 触发延迟陷阱（第430行）
5. 角色继续行动（第431行）

### fx(boolean on)
**功能**: 设置视觉效果
**参数**:
- on: boolean - 是否开启
**实现逻辑**:
1. 冻结所有发射器（第437行）
2. 遍历所有敌人添加/移除麻痹效果（第439-446行）

### icon() / tintIcon() / iconFadePercent() / iconTextDisplay() / desc()
**功能**: 返回Buff指示器的显示信息

## 内部类 sandBag

沙袋物品，用于升级沙漏。

### doPickUp(Hero hero, int pos)
**签名**: `boolean doPickUp(Hero hero, int pos)`
**功能**: 拾取沙袋
**参数**:
- hero: Hero - 拾取的英雄
- pos: int - 拾取位置
**返回值**: boolean - 是否成功
**实现逻辑**:
1. 查找沙漏物品（第511行）
2. 如果有沙漏且未诅咒，升级沙漏（第512-519行）
3. 否则提示没有沙漏（第524行）

## 11. 使用示例
```java
// 创建守时者的沙漏
TimekeepersHourglass hourglass = new TimekeepersHourglass();

// 装备沙漏
hourglass.doEquip(hero);

// 激活沙漏
hourglass.execute(hero, TimekeepersHourglass.AC_ACTIVATE);
// 选择：
// 1. 时间停滞：进入隐身麻痹，时间快速流逝
// 2. 时间冻结：敌人停止，玩家自由行动

// 收集沙袋升级
sandBag sand = new sandBag();
sand.doPickUp(hero, pos); // 自动升级沙漏
```

## 注意事项
1. 时间停滞会进入隐身和麻痹状态，无法行动
2. 时间冻结每2回合消耗1点充能
3. 时间冻结期间踩到的陷阱会延迟触发
4. 诅咒状态下会随机花费时间
5. 沙袋可以升级沙漏，满级为5级

## 最佳实践
1. 时间停滞用于安全等待技能冷却或状态恢复
2. 时间冻结用于紧急情况逃脱或攻击
3. 升级沙漏增加充能上限
4. 配合能量戒指提高充能效率
5. 注意时间冻结结束后会触发踩过的陷阱