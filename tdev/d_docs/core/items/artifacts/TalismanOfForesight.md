# TalismanOfForesight 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/TalismanOfForesight.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.artifacts |
| 类类型 | public class |
| 继承关系 | extends Artifact |
| 代码行数 | 432行 |

## 2. 类职责说明
预见护符是一个探测型神器，装备后可以感知附近的隐藏陷阱，并在发现时警告玩家。使用护符可以扫描锥形区域，揭示隐藏的陷阱和门，发现敌人并标记它们的位置。扫描消耗充能，距离越远消耗越大。

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
    
    class TalismanOfForesight {
        -boolean warn
        +String AC_SCRY
        +CellSelector.Listener scry
        +ArrayList~String~ actions(Hero)
        +void execute(Hero, String)
        +String desc()
        -float maxDist()
    }
    
    class Foresight {
        +boolean act()
        +void checkAwareness()
        +void charge(int)
        +int icon()
    }
    
    class CharAwareness {
        +int charID
    }
    
    class HeapAwareness {
        +int pos
        +int depth
        +int branch
    }
    
    class ArtifactBuff {
        <<abstract>>
    }
    
    class FlavourBuff {
        <<abstract>>
    }
    
    Artifact <|-- TalismanOfForesight
    ArtifactBuff <|-- Foresight
    FlavourBuff <|-- CharAwareness
    FlavourBuff <|-- HeapAwareness
    TalismanOfForesight +-- Foresight
    TalismanOfForesight +-- CharAwareness
    TalismanOfForesight +-- HeapAwareness
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_SCRY | String | "SCRY" | 扫描动作标识 |
| WARN | String | "warn" | Bundle存储键 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标，使用ItemSpriteSheet.ARTIFACT_TALISMAN |
| exp | int | - | 经验值，初始为0 |
| levelCap | int | - | 等级上限为10级 |
| charge | int | - | 当前充能，初始为0 |
| partialCharge | float | - | 部分充能值 |
| chargeCap | int | - | 充能上限为100 |
| defaultAction | String | - | 默认动作为AC_SCRY |
| warn | boolean | private | 是否已警告 |
| scry | CellSelector.Listener | public | 扫描监听器 |

## 7. 方法详解

### actions(Hero hero)
**签名**: `ArrayList<String> actions(Hero hero)`
**功能**: 获取该物品可用的动作列表
**参数**:
- hero: Hero - 英雄对象
**返回值**: ArrayList<String> - 动作名称列表
**实现逻辑**:
1. 调用父类actions方法（第74行）
2. 检查条件（第75-77行）：已装备、未诅咒、无魔法免疫
3. 满足条件时添加AC_SCRY动作（第78行）

### execute(Hero hero, String action)
**签名**: `void execute(Hero hero, String action)`
**功能**: 执行指定的物品动作
**参数**:
- hero: Hero - 执行动作的英雄
- action: String - 要执行的动作名称
**返回值**: void
**实现逻辑**:
1. 调用父类execute方法（第85行）
2. 检查魔法免疫（第87行）
3. 如果动作是AC_SCRY（第89-93行）：
   - 检查装备、充能状态
   - 打开目标选择器

### passiveBuff()
**签名**: `protected ArtifactBuff passiveBuff()`
**功能**: 获取被动Buff实例
**参数**: 无
**返回值**: ArtifactBuff - Foresight实例

### charge(Hero target, float amount)
**签名**: `void charge(Hero target, float amount)`
**功能**: 为护符充能
**参数**:
- target: Hero - 目标英雄
- amount: float - 充能数量
**返回值**: void
**实现逻辑**:
- 增加充能，不超过上限（第104-116行）

### desc()
**签名**: `String desc()`
**功能**: 获取物品描述文本
**参数**: 无
**返回值**: String - 描述文本
**实现逻辑**:
1. 获取基础描述（第121行）
2. 添加装备状态描述（第123-130行）

### maxDist()
**签名**: `private float maxDist()`
**功能**: 计算最大扫描距离
**参数**: 无
**返回值**: float - 最大距离
**实现逻辑**:
- 计算公式：min(5 + 2*等级, (充能-3)/1.08)（第136行）

## 内部类 Foresight

### act()
**签名**: `boolean act()`
**功能**: 每帧执行的Buff行为
**参数**: 无
**返回值**: boolean - 是否继续执行
**实现逻辑**:
1. 花费1tick时间（第276行）
2. 检查感知（第278行）
3. 如果充能未满且可恢复（第280-298行）：
   - 计算充能速度：0.05 + 等级*0.005（第285行）
   - 应用能量戒指加成
   - 增加充能

### checkAwareness()
**签名**: `void checkAwareness()`
**功能**: 检查附近是否有隐藏陷阱
**参数**: 无
**返回值**: void
**实现逻辑**:
1. 定义检测范围为3格（第306行）
2. 遍历范围内的所有单元格（第327-338行）
3. 检查是否在视野内、是否是隐藏陷阱、是否可搜索（第330-335行）
4. 如果发现隐藏陷阱，显示警告（第340-352行）

### charge(int boost)
**签名**: `void charge(int boost)`
**功能**: 直接增加充能
**参数**:
- boost: int - 充能数量
**返回值**: void
**实现逻辑**:
- 增加充能，不超过上限（第357行）

### icon()
**签名**: `int icon()`
**功能**: 获取Buff图标
**参数**: 无
**返回值**: int - 图标ID
**实现逻辑**:
- 如果有警告，返回FORESIGHT图标（第365行）
- 否则返回NONE图标（第367行）

## 内部类 CharAwareness

继承自FlavourBuff，标记一个角色的位置。

### 字段
- charID: int - 角色的Actor ID

### detach()
**功能**: 移除时更新视野
**实现逻辑**:
- 更新视野和迷雾（第380-381行）

## 内部类 HeapAwareness

继承自FlavourBuff，标记一个物品堆的位置。

### 字段
- pos: int - 位置
- depth: int - 深度
- branch: int - 分支

## 扫描监听器 scry

### onSelect(Integer target)
**功能**: 选择目标后的处理
**参数**:
- target: Integer - 目标位置
**实现逻辑**:
1. 检查目标有效性（第143行）
2. 确保至少2格距离（第146-148行）
3. 计算实际距离，限制在最大范围内（第150-161行）
4. 计算扫描角度（第164行）
5. 创建锥形区域（第165行）
6. 遍历区域内所有单元格（第169-211行）：
   - 显示检查效果（第170行）
   - 揭示未探索区域（第171-174行）
   - 发现隐藏陷阱和门（第176-188行）
   - 标记敌人（第190-201行）
   - 标记物品堆（第203-210行）
7. 增加经验，可能升级（第214-220行）
8. 消耗充能（第224-233行）
9. 更新视野（第237-239行）
10. 播放音效（第243-244行）

## 11. 使用示例
```java
// 创建预见护符
TalismanOfForesight talisman = new TalismanOfForesight();

// 装备护符
talisman.doEquip(hero);

// 护符会自动检测附近的隐藏陷阱
// 发现时会显示警告

// 扫描区域
talisman.execute(hero, TalismanOfForesight.AC_SCRY);
// 选择目标方向进行扫描
// 揭示隐藏陷阱、门，发现敌人

// 消耗充能
// 距离越远，消耗越大
// 最少5点充能（2格距离）
```

## 注意事项
1. 被动效果只能检测隐藏陷阱，不能检测隐藏门
2. 扫描消耗充能，距离越远消耗越大
3. 发现隐藏门获得大量经验（100）
4. 发现隐藏陷阱获得中等经验（10）
5. 发现未见过的敌人或物品获得经验（10）

## 最佳实践
1. 装备后注意警告，说明附近有隐藏陷阱
2. 在未知区域使用扫描探索
3. 升级护符增加扫描范围和持续时间
4. 扫描可以发现隐藏的敌人和物品
5. 配合能量戒指提高充能效率