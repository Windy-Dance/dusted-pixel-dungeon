# LloydsBeacon 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/LloydsBeacon.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.artifacts |
| 类类型 | public class |
| 继承关系 | extends Artifact |
| 代码行数 | 337行 |

## 2. 类职责说明
劳埃德信标是一个传送型神器，允许玩家设置传送点并在之后返回该位置。此外还可以使用信标将敌人传送走。信标的充能用于传送敌人，自我传送不消耗充能。升级信标可以增加充能上限。

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
    
    class LloydsBeacon {
        +int returnDepth
        +int returnPos
        +float TIME_TO_USE
        +String AC_ZAP
        +String AC_SET
        +String AC_RETURN
        +CellSelector.Listener zapper
        +ArrayList~String~ actions(Hero)
        +void execute(Hero, String)
        +Item upgrade()
        +String desc()
        +Glowing glowing()
    }
    
    class beaconRecharge {
        +boolean act()
    }
    
    class ArtifactBuff {
        <<abstract>>
    }
    
    Artifact <|-- LloydsBeacon
    ArtifactBuff <|-- beaconRecharge
    LloydsBeacon +-- beaconRecharge
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| TIME_TO_USE | float | 1.0 | 使用信标需要的时间 |
| AC_ZAP | String | "ZAP" | 传送敌人动作标识 |
| AC_SET | String | "SET" | 设置传送点动作标识 |
| AC_RETURN | String | "RETURN" | 返回传送点动作标识 |
| DEPTH | String | "depth" | Bundle存储键 |
| POS | String | "pos" | Bundle存储键 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标，使用ItemSpriteSheet.ARTIFACT_BEACON |
| levelCap | int | - | 等级上限为3级 |
| charge | int | - | 当前充能，初始为0 |
| chargeCap | int | - | 充能上限，3 + 等级 |
| defaultAction | String | - | 默认动作为AC_ZAP |
| usesTargeting | boolean | - | 是否使用目标选择，初始为true |
| returnDepth | int | public | 返回深度，-1表示未设置 |
| returnPos | int | public | 返回位置坐标 |

## 7. 方法详解

### storeInBundle(Bundle bundle)
**签名**: `void storeInBundle(Bundle bundle)`
**功能**: 将物品状态保存到Bundle
**参数**:
- bundle: Bundle - 存储容器
**返回值**: void
**实现逻辑**:
1. 调用父类方法（第81行）
2. 保存返回深度和位置（第82-85行）

### restoreFromBundle(Bundle bundle)
**签名**: `void restoreFromBundle(Bundle bundle)`
**功能**: 从Bundle恢复物品状态
**参数**:
- bundle: Bundle - 存储容器
**返回值**: void
**实现逻辑**:
1. 调用父类方法（第90行）
2. 恢复返回深度和位置（第91-92行）

### actions(Hero hero)
**签名**: `ArrayList<String> actions(Hero hero)`
**功能**: 获取该物品可用的动作列表
**参数**:
- hero: Hero - 英雄对象
**返回值**: ArrayList<String> - 动作名称列表
**实现逻辑**:
1. 调用父类actions方法（第97行）
2. 添加传送敌人动作（第98行）
3. 添加设置传送点动作（第99行）
4. 如果已设置传送点，添加返回动作（第100-102行）

### execute(Hero hero, String action)
**签名**: `void execute(Hero hero, String action)`
**功能**: 执行指定的物品动作
**参数**:
- hero: Hero - 执行动作的英雄
- action: String - 要执行的动作名称
**返回值**: void
**实现逻辑**:
1. 调用父类execute方法（第109行）
2. 如果动作是AC_SET或AC_RETURN（第111-126行）：
   - 检查是否在Boss层或不允许层间传送
   - 检查周围是否有敌人
3. 如果动作是AC_ZAP（第128-143行）：
   - 检查装备状态和充能
   - 打开目标选择器
4. 如果动作是AC_SET（第145-157行）：
   - 设置返回深度和位置
   - 播放音效和动画
5. 如果动作是AC_RETURN（第158-187行）：
   - 如果在同一层，直接传送
   - 否则切换到返回层

### passiveBuff()
**签名**: `protected ArtifactBuff passiveBuff()`
**功能**: 获取被动Buff实例
**参数**: 无
**返回值**: ArtifactBuff - beaconRecharge实例

### charge(Hero target, float amount)
**签名**: `void charge(Hero target, float amount)`
**功能**: 为信标充能
**参数**:
- target: Hero - 目标英雄
- amount: float - 充能数量
**返回值**: void
**实现逻辑**:
- 增加充能，不超过上限（第276-289行）

### upgrade()
**签名**: `Item upgrade()`
**功能**: 升级信标，增加充能上限
**参数**: 无
**返回值**: Item - 升级后的物品
**实现逻辑**:
1. 如果已满级则不升级（第294行）
2. 增加充能上限（第295行）
3. 显示升级提示（第296行）
4. 调用父类upgrade方法（第297行）

### desc()
**签名**: `String desc()`
**功能**: 获取物品描述文本
**参数**: 无
**返回值**: String - 描述文本
**实现逻辑**:
1. 获取基础描述（第302行）
2. 如果已设置传送点，显示返回深度（第303-305行）

### glowing()
**签名**: `Glowing glowing()`
**功能**: 获取物品发光效果
**参数**: 无
**返回值**: Glowing - 发光效果（已设置传送点时为白色）
**实现逻辑**:
- 如果已设置传送点，返回白色发光效果（第313行）
- 否则返回null（第313行）

## 内部类 beaconRecharge

### act()
**签名**: `boolean act()`
**功能**: 每帧执行的Buff行为
**参数**: 无
**返回值**: boolean - 是否继续执行
**实现逻辑**:
1. 如果充能未满且未诅咒且可以恢复（第319行）
2. 计算充能速度：1 / (100 - (上限-当前)*10)（第320行）
3. 转换部分充能为整数充能（第322-329行）

## 内部字段 zapper

目标选择器监听器，用于处理传送敌人的目标选择。

### onSelect(Integer target)
**功能**: 选择目标后的处理
**参数**:
- target: Integer - 目标位置
**实现逻辑**:
1. 如果目标是英雄自己，执行自我传送（第201-203行）
2. 否则发射魔法弹，传送目标敌人（第212-255行）
3. 敌人被传送到随机重生点（第225-251行）

## 11. 使用示例
```java
// 创建劳埃德信标
LloydsBeacon beacon = new LloydsBeacon();

// 装备信标
beacon.doEquip(hero);

// 设置传送点
beacon.execute(hero, LloydsBeacon.AC_SET);
// 当前位置被保存

// 传送敌人
beacon.execute(hero, LloydsBeacon.AC_ZAP);
// 选择敌人将其传送走

// 返回传送点
beacon.execute(hero, LloydsBeacon.AC_RETURN);
// 传送到之前设置的位置
```

## 注意事项
1. 在Boss层或不允许层间传送的区域无法设置/返回
2. 周围有敌人时无法设置或返回
3. 传送敌人消耗充能，深度>20时消耗2点充能
4. 自我传送不消耗充能
5. 不可移动的敌人无法被传送

## 最佳实践
1. 在安全区域设置传送点
2. 用于快速返回商店或重要地点
3. 紧急情况下传送危险敌人
4. 升级信标增加充能上限
5. 注意Boss战前无法使用返回功能